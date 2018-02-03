package pl.sparkbit.commons.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
@SuppressWarnings({"unused", "WeakerAccess"})
public class NotFoundException extends BusinessException {

    private static final String DEFAULT_ERROR_CODE = "NOT_FOUND";

    public NotFoundException(String message) {
        this(message, DEFAULT_ERROR_CODE);
    }

    public NotFoundException(String message, String errorCode) {
        super(message, errorCode, new String[]{message});
    }
}
