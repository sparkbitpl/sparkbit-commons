package pl.sparkbit.commons.exception;

import com.google.common.collect.ImmutableSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import pl.sparkbit.commons.i18n.Messages;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static pl.sparkbit.commons.CommonsProperties.REST_ERROR_ATTRIBUTES_ENABLED;

@Component
@ConditionalOnProperty(value = REST_ERROR_ATTRIBUTES_ENABLED, havingValue = "true")
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("unused")
public class RestErrorAttributes extends DefaultErrorAttributes {

    private static final Set<Class> NOT_LOGGABLE_EXCEPTIONS = ImmutableSet.of(
            TypeMismatchException.class,
            MethodArgumentNotValidException.class,
            AccessDeniedException.class);

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private final Optional<Messages> messagesOpt;

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
        String contentType = webRequest.getHeader("content-type");
        if (contentType != null && MediaType.valueOf(contentType).isCompatibleWith(MediaType.APPLICATION_JSON)) {
            Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, false);
            removeUnwantedAttributes(errorAttributes);
            changeTimestampToMillis(errorAttributes);
            Throwable throwable = getError(webRequest);

            if (throwable == null) {
                if (HttpStatus.valueOf((Integer) errorAttributes.get("status")).is5xxServerError()) {
                    log.error("Runtime exception: {}", errorAttributes.get("message"));
                }
            } else if (throwable instanceof BusinessException) {
                BusinessException businessException = (BusinessException) throwable;
                errorAttributes.put("errorCode", businessException.getErrorCode());
                messagesOpt.ifPresent(messages -> errorAttributes.put("translatedMessage",
                        messages.error(businessException.getErrorCode(), businessException.getMessageDetails())));
                Map<String, Object> additionalErrorDetails = businessException.getAdditionalErrorDetails();
                if (additionalErrorDetails != null) {
                    errorAttributes.put("errorDetails", additionalErrorDetails);
                }
            } else if (NOT_LOGGABLE_EXCEPTIONS.stream().noneMatch(nlec -> nlec.isInstance(throwable))) {
                log.error("Runtime exception", throwable);
            }
            return errorAttributes;
        } else {
            return super.getErrorAttributes(webRequest, includeStackTrace);
        }
    }

    private void removeUnwantedAttributes(Map<String, Object> result) {
        result.remove("exception");
        result.remove("error");
    }

    private void changeTimestampToMillis(Map<String, Object> result) {
        result.put("timestamp", Instant.now().toEpochMilli());
    }
}
