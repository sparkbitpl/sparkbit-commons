package pl.sparkbit.commons.i18n;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

@SuppressWarnings({"unused", "WeakerAccess"})
@RequiredArgsConstructor
public class Messages {

    private static final String ERROR_MESSAGE_PREFIX = "error.";

    private final MessageSource messageSource;

    public String get(String key) {
        return get(key, null);
    }

    public String get(String key, Object[] args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, args, locale);
    }

    @Nullable
    public String error(String errorCode, String[] details) {
        String key = ERROR_MESSAGE_PREFIX + errorCode;
        try {
            return get(key, details);
        } catch (NoSuchMessageException e) {
            // message not found
            return null;
        }
    }
}
