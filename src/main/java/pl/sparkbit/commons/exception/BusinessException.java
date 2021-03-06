package pl.sparkbit.commons.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Arrays;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ResponseStatus(BAD_REQUEST)
@SuppressWarnings({"unused", "WeakerAccess"})
public class BusinessException extends RuntimeException {

    private static final String DEFAULT_ERROR_CODE = "BUSINESS_RULES_VIOLATED";

    private final String errorCode;
    private final String[] messageDetails;

    public BusinessException(String message) {
        this(message, DEFAULT_ERROR_CODE);
    }

    public BusinessException(String message, String errorCode) {
        this(message, errorCode, (String[]) null);
    }

    public BusinessException(String message, String... messageDetails) {
        this(message, DEFAULT_ERROR_CODE, messageDetails);
    }

    public BusinessException(String message, String errorCode, String... messageDetails) {
        super(message);
        this.errorCode = errorCode;
        this.messageDetails = messageDetails;
    }

    public String[] getMessageDetails() {
        return messageDetails != null ? Arrays.copyOf(messageDetails, messageDetails.length) : new String[]{};
    }

    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Override this method to add Exception-specific error attributes to the JSON error response.
     * <p>
     * The returned map will be available under "errorDetails" path.
     */
    protected Map<String, Object> getAdditionalErrorDetails() {
        return null;
    }
}
