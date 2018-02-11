package pl.sparkbit.commons.exception;

import com.google.common.collect.ImmutableSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.RequestAttributes;
import pl.sparkbit.commons.i18n.Messages;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("unused")
public class RestErrorAttributes extends DefaultErrorAttributes {

    private static final Set<Class> NOT_LOGGABLE_EXCEPTIONS = ImmutableSet.of(
            MethodArgumentNotValidException.class,
            AccessDeniedException.class);

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private final Optional<Messages> messagesOpt;

    @Override
    public Map<String, Object> getErrorAttributes(RequestAttributes requestAttributes, boolean includeStackTrace) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(requestAttributes, false);
        Map<String, Object> result = new HashMap<>(errorAttributes);
        removeUnwantedAttributes(result);
        changeTimestampToMillis(result);
        Throwable throwable = getError(requestAttributes);

        if (throwable == null) {
            log.error("Runtime exception: {}", result.get("message"));
        } else if (throwable instanceof BusinessException) {
            BusinessException businessException = (BusinessException) throwable;
            result.put("errorCode", businessException.getErrorCode());
            messagesOpt.ifPresent(messages -> result.put("translatedMessage",
                    messages.error(businessException.getErrorCode(), businessException.getMessageDetails())));
            Map<String, Object> additionalErrorDetails = businessException.getAdditionalErrorDetails();
            if (additionalErrorDetails != null) {
                result.put("errorDetails", additionalErrorDetails);
            }
        } else if (!NOT_LOGGABLE_EXCEPTIONS.contains(throwable.getClass())) {
            log.error("Runtime exception", throwable);
        }
        return result;
    }

    private void removeUnwantedAttributes(Map<String, Object> result) {
        result.remove("exception");
        result.remove("error");
    }

    private void changeTimestampToMillis(Map<String, Object> result) {
        result.put("timestamp", Instant.now().toEpochMilli());
    }
}