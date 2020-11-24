package pl.sparkbit.commons.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.IgnoredPropertyException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.assertj.core.data.Offset;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.testcontainers.shaded.com.google.common.collect.ImmutableList;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;
import pl.sparkbit.commons.i18n.Messages;

import java.util.Arrays;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class RestErrorAttributesTest {

    private Messages messages = Mockito.mock(Messages.class);
    private ObjectProvider<Messages> messagesObjectProvider = Mockito.mock(ObjectProvider.class);
    private RestErrorAttributes errorAttributes = new RestErrorAttributes(messagesObjectProvider);

    private final MockHttpServletRequest request = new MockHttpServletRequest();
    private final WebRequest webRequest = new ServletWebRequest(this.request);

    @Before
    public void init() {
        Mockito.when(messagesObjectProvider.getIfAvailable()).thenReturn(messages);
        Mockito.doCallRealMethod().when(messagesObjectProvider).ifAvailable(Mockito.any());
    }

    @Test
    public void expectedFields() {
        Map<String, Object> attributes = this.errorAttributes.getErrorAttributes(this.webRequest,
            ErrorAttributeOptions.defaults());
        assertThat(attributes).containsOnlyKeys("timestamp", "status", "message");
    }

    @Test
    public void includeTimeStampInMilliseconds() {
        Map<String, Object> attributes = this.errorAttributes.getErrorAttributes(this.webRequest,
            ErrorAttributeOptions.defaults());
        assertThat(attributes.get("timestamp")).isInstanceOf(Long.class);
        assertThat((Long) attributes.get("timestamp")).isCloseTo(System.currentTimeMillis(), Offset.offset(10_000L));
    }

    @Test
    public void specificStatusCode() {
        this.request.setAttribute("javax.servlet.error.status_code", 404);
        Map<String, Object> attributes = this.errorAttributes.getErrorAttributes(this.webRequest,
            ErrorAttributeOptions.defaults());
        assertThat(attributes.get("status")).isEqualTo(404);
    }

    @Test
    public void genericMessage() {
        RuntimeException ex = new RuntimeException("Test exception");
        ModelAndView modelAndView = this.errorAttributes.resolveException(this.request, null, null, ex);
        this.request.setAttribute("javax.servlet.error.exception", new RuntimeException("Ignored"));

        Map<String, Object> attributes = this.errorAttributes.getErrorAttributes(this.webRequest,
            ErrorAttributeOptions.defaults());

        assertThat(this.errorAttributes.getError(this.webRequest)).isSameAs(ex);
        assertThat(modelAndView).isNull();
        assertThat(attributes.get("message")).isEqualTo("Unknown error");
        assertThat(attributes).containsOnlyKeys("timestamp", "status", "message");
    }

    @Test
    public void translatedMessageForBusinessException() {
        BusinessException ex = new NotFoundException("Default message");
        ModelAndView modelAndView = this.errorAttributes.resolveException(this.request, null, null, ex);
        this.request.setAttribute("javax.servlet.error.exception", new RuntimeException("Ignored"));
        Mockito.when(messages.error("NOT_FOUND", new String[]{"Default message"}))
            .thenReturn("Translated message");

        Map<String, Object> attributes = this.errorAttributes.getErrorAttributes(this.webRequest,
            ErrorAttributeOptions.defaults());

        assertThat(this.errorAttributes.getError(this.webRequest)).isSameAs(ex);
        assertThat(modelAndView).isNull();
        assertThat(attributes.get("message")).isEqualTo("Default message");
        assertThat(attributes.get("translatedMessage")).isEqualTo("Translated message");
        assertThat(attributes.get("errorCode")).isEqualTo("NOT_FOUND");
        assertThat(attributes).containsOnlyKeys("timestamp", "status", "message", "errorCode", "translatedMessage");
    }

    @Test
    public void additionalDetailsForBusinessException() {
        Map<String, Object> details = ImmutableMap.of("f1", 1, "f2", "2");
        BusinessException ex = new CustomBusinessException("Custom exception message", details);
        ModelAndView modelAndView = this.errorAttributes.resolveException(this.request, null, null, ex);
        this.request.setAttribute("javax.servlet.error.exception", new RuntimeException("Ignored"));
        Mockito.when(messages.error(
            "CUSTOM_BUSINESS_RULES_VIOLATED",
            new String[]{"i18nParam1"}
        )).thenReturn("Translated message");

        Map<String, Object> attributes = this.errorAttributes.getErrorAttributes(this.webRequest,
            ErrorAttributeOptions.defaults());

        assertThat(this.errorAttributes.getError(this.webRequest)).isSameAs(ex);
        assertThat(modelAndView).isNull();
        assertThat(attributes.get("message")).isEqualTo("Custom exception message");
        assertThat(attributes.get("translatedMessage")).isEqualTo("Translated message");
        assertThat(attributes.get("errorCode")).isEqualTo("CUSTOM_BUSINESS_RULES_VIOLATED");
        assertThat(attributes.get("errorDetails")).isEqualTo(details);
        assertThat(attributes).containsOnlyKeys(
            "timestamp", "status", "message", "errorCode", "translatedMessage", "errorDetails");
    }

    @Test
    public void handlingValidationErrors() {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(this, "test");
        errors.addError(new FieldError("object1", "field1", "size should be equal 2"));
        errors.addError(new FieldError("object1", "field2", "other field error"));
        errors.addError(new ObjectError("object1", "other field error"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(
            new MethodParameter(this.getClass().getDeclaredMethods()[0], -1),
            errors
        );
        ModelAndView modelAndView = this.errorAttributes.resolveException(this.request, null, null, ex);

        Map<String, Object> attributes = this.errorAttributes.getErrorAttributes(this.webRequest,
            ErrorAttributeOptions.defaults());

        assertThat(this.errorAttributes.getError(this.webRequest)).isSameAs(ex);
        assertThat(modelAndView).isNull();
        assertThat(attributes.get("message")).isEqualTo("Validation errors:\n" +
            "- size should be equal 2\n" +
            "- other field error");
        assertThat(attributes.get("fieldErrors")).isEqualTo(ImmutableList.of(ImmutableMap.of(
            "msg", "size should be equal 2",
            "path", "field1"
        ), ImmutableMap.of(
            "msg", "other field error",
            "path", "field2"
        )));
        assertThat(attributes).containsOnlyKeys("timestamp", "status", "message", "fieldErrors");
    }

    @Test
    public void handlingNonReadableJsonMessageCausedByInvalidFormatWithPath() {
        InvalidFormatException cause = new InvalidFormatException(
            null,
            "JSON is invalid",
            "someValue",
            Map.class
        );
        cause.prependPath("object1", "field1");

        Map<String, Object> attributes = runMessageNotReadable(cause);

        assertThat(attributes.get("message")).isEqualTo("Invalid value \"someValue\"");
        assertThat(attributes.get("fieldErrors")).isEqualTo(ImmutableList.of(ImmutableMap.of(
            "msg", "invalid value \"someValue\"",
            "path", "field1"
        )));
        assertThat(attributes).containsOnlyKeys("timestamp", "status", "message", "fieldErrors");
    }

    @Test
    public void handlingNonReadableJsonMessageCausedByInvalidEnumConstant() {
        InvalidFormatException cause = new InvalidFormatException(
            null,
            "JSON is invalid",
            "CONST4",
            SomeEnum.class
        );
        cause.prependPath("object1", "field1");

        Map<String, Object> attributes = runMessageNotReadable(cause);

        assertThat(attributes.get("message")).isEqualTo("Invalid value \"CONST4\". Correct values are: CONST1, " +
            "CONST2, CONST3");
        assertThat(attributes.get("fieldErrors")).isEqualTo(ImmutableList.of(ImmutableMap.of(
            "msg", "invalid value \"CONST4\", valid are: CONST1, CONST2, CONST3",
            "path", "field1"
        )));
        assertThat(attributes).containsOnlyKeys("timestamp", "status", "message", "fieldErrors");
    }

    @Test
    public void handlingNonReadableJsonMessageCausedByInvalidFormatWithoutPath() {
        InvalidFormatException cause = new InvalidFormatException(
            null,
            "JSON is invalid",
            "someValue",
            Map.class
        );

        Map<String, Object> attributes = runMessageNotReadable(cause);

        assertThat(attributes.get("message")).isEqualTo("Invalid value \"someValue\"");
        assertThat(attributes).containsOnlyKeys("timestamp", "status", "message");
    }

    @Test
    public void handlingNonReadableJsonMessageCausedByInputMismatchWithoutPath() {
        MismatchedInputException cause = new IgnoredPropertyException(
            null,
            "field2 is missing",
            null,
            Object.class,
            "field2",
            Arrays.asList("field1", "field2")
        );

        Map<String, Object> attributes = runMessageNotReadable(cause);

        assertThat(attributes.get("message")).isEqualTo("Invalid property \"field2\"");
        assertThat(attributes).containsOnlyKeys("timestamp", "status", "message");
    }

    @Test
    public void handlingNonReadableJsonMessageCausedByInputMismatchWithPath() {
        MismatchedInputException cause = new IgnoredPropertyException(
            null,
            "field2 is missing",
            null,
            Object.class,
            "field2",
            Arrays.asList("field1", "field2")
        );
        cause.prependPath("object1", "field2");

        Map<String, Object> attributes = runMessageNotReadable(cause);

        assertThat(attributes.get("message"))
            .isEqualTo("Invalid property \"field2\"");
        assertThat(attributes.get("fieldErrors")).isEqualTo(ImmutableList.of(ImmutableMap.of(
            "msg", "invalid",
            "path", "field2"
        )));
        assertThat(attributes).containsOnlyKeys("timestamp", "status", "message", "fieldErrors");
    }

    @Test
    public void handlingNonReadableJsonMessageCausedByUnrecognizedProperty() {
        UnrecognizedPropertyException cause = new UnrecognizedPropertyException(
            null,
            "field2 is missing",
            null,
            Object.class,
            "field2",
            Arrays.asList("field1", "field2")
        );
        cause.prependPath("object1", "field2");

        Map<String, Object> attributes = runMessageNotReadable(cause);

        assertThat(attributes.get("message")).isEqualTo("Unrecognized property \"field2\"");
        assertThat(attributes.get("fieldErrors")).isEqualTo(ImmutableList.of(ImmutableMap.of(
            "msg", "unrecognized property",
            "path", "field2"
        )));
        assertThat(attributes).containsOnlyKeys("timestamp", "status", "message", "fieldErrors");
    }

    @Test
    public void handlingNonReadableJsonMessageCausedByJsonParseException() {
        JsonParseException cause = new JsonParseException(null, "some details");

        Map<String, Object> attributes = runMessageNotReadable(cause);

        assertThat(attributes.get("message")).isEqualTo("JSON payload is not well formatted");
        assertThat(attributes).containsOnlyKeys("timestamp", "status", "message");
    }

    @Test
    public void handlingNonReadableJsonMessageCausedByInvalidTypeId() {
        InvalidTypeIdException cause = new InvalidTypeIdException(
            null,
            "invalid type",
            null,
            "invalidObjectTypeId"
        );
        cause.prependPath("object1", "field2");

        Map<String, Object> attributes = runMessageNotReadable(cause);

        assertThat(attributes.get("message")).isEqualTo("Invalid object type \"invalidObjectTypeId\"");
        assertThat(attributes.get("fieldErrors")).isEqualTo(ImmutableList.of(ImmutableMap.of(
            "msg", "invalid",
            "path", "field2"
        )));
        assertThat(attributes).containsOnlyKeys("timestamp", "status", "message", "fieldErrors");
    }

    private Map<String, Object> runMessageNotReadable(Exception cause) {
        HttpInputMessage body = new MockHttpInputMessage("{\"field1\": \"val1\"}".getBytes());
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Invalid Json", cause, body);
        ModelAndView modelAndView = this.errorAttributes.resolveException(this.request, null, null, ex);

        Map<String, Object> attributes = this.errorAttributes.getErrorAttributes(this.webRequest,
            ErrorAttributeOptions.defaults());

        assertThat(this.errorAttributes.getError(this.webRequest)).isSameAs(ex);
        assertThat(modelAndView).isNull();
        return attributes;
    }

    private enum SomeEnum {
        CONST1,
        CONST2,
        CONST3
    }

    private static class CustomBusinessException extends BusinessException {

        private static final long serialVersionUID = 8680913028295574246L;
        private final Map<String, Object> details;

        public CustomBusinessException(String message, Map<String, Object> details) {
            super(message, "CUSTOM_BUSINESS_RULES_VIOLATED", new String[]{"i18nParam1"});
            this.details = details;
        }

        @Override
        protected Map<String, Object> getAdditionalErrorDetails() {
            return details;
        }
    }
}
