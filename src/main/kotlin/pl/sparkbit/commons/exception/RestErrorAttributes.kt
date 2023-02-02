package pl.sparkbit.commons.exception

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.exc.IgnoredPropertyException
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import mu.KotlinLogging
import org.springframework.beans.TypeMismatchException
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.firewall.RequestRejectedException
import org.springframework.validation.BindException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.WebRequest
import org.springframework.web.multipart.MaxUploadSizeExceededException
import org.springframework.web.multipart.MultipartException
import org.springframework.web.multipart.support.MissingServletRequestPartException
import pl.sparkbit.commons.i18n.Messages
import java.time.Instant
import javax.servlet.RequestDispatcher

@Order(Ordered.HIGHEST_PRECEDENCE)
class RestErrorAttributes(
    private val messagesOpt: ObjectProvider<Messages>,
    private val restErrorProperties: RestErrorProperties
) : DefaultErrorAttributes() {

    private val log = KotlinLogging.logger {}

    override fun getErrorAttributes(webRequest: WebRequest, opts: ErrorAttributeOptions): Map<String, Any?> {
        return if (isRequestAcceptJsonOrAll(webRequest)) {
            val errorAttributes = mutableMapOf<String, Any?>()
            val status = getAttribute<Int>(webRequest, RequestDispatcher.ERROR_STATUS_CODE) ?: 999
            val throwable: Throwable? = getError(webRequest)
            addTimestamp(errorAttributes)
            addStatus(errorAttributes, status)
            addMessagesWithDetails(errorAttributes, throwable)
            addFieldErrors(errorAttributes, throwable)
            when {
                throwable is RequestRejectedException -> {
                    // Ignore RequestRejectedException. It's workaround for Spring Security issue
                    // RequestRejectedException should be recognized as 4xx client error
                    // but currently the status is unspecified
                    // https://github.com/spring-projects/spring-security/issues/7568
                }
                status in 500..599 -> {
                    val message = getAttribute<Any>(webRequest, RequestDispatcher.ERROR_MESSAGE)
                    log.error { "Runtime exception: $message" }
                }
                status !in 400..499 && isLoggableException(throwable) -> {
                    log.error("Runtime exception", throwable)
                }
            }
            errorAttributes
        } else {
            super.getErrorAttributes(webRequest, opts)
        }
    }

    private fun isRequestAcceptJsonOrAll(webRequest: WebRequest): Boolean {
        val accept = webRequest.getHeader("accept")
        if (accept == null || accept.isBlank()) {
            return true
        }
        return accept.split(",")
            .map { MediaType.valueOf(it.trim()) }
            .any { it.isCompatibleWith(MediaType.APPLICATION_JSON) }
    }

    private fun isLoggableException(throwable: Throwable?) =
        throwable?.let { instance ->
            NOT_LOGGABLE_EXCEPTIONS.none { it.isInstance(instance) }
        } ?: true

    private fun addMessagesWithDetails(errorAttributes: MutableMap<String, Any?>, throwable: Throwable?) {
        val message: String? = when (throwable) {
            is BusinessException -> {
                errorAttributes["errorCode"] = throwable.errorCode
                messagesOpt.ifAvailable { messages: Messages ->
                    errorAttributes["translatedMessage"] = messages.error(throwable.errorCode, throwable.messageDetails)
                }
                val additionalErrorDetails = throwable.additionalErrorDetails
                if (additionalErrorDetails != null) {
                    errorAttributes["errorDetails"] = additionalErrorDetails
                }
                throwable.message
            }
            is BindException -> {
                throwable.bindingResult.allErrors.mapNotNull { it.defaultMessage }.let {
                    if (it.isNotEmpty()) {
                        it.joinToString(separator = "\n- ", prefix = "Validation errors:\n- ")
                    } else {
                        "Validation failed"
                    }
                }
            }
            is HttpMessageNotReadableException -> {
                when (val cause = throwable.cause) {
                    is InvalidFormatException -> {
                        if (cause.targetType.isEnum) {
                            "Invalid value \"${cause.value}\". Correct values are: ${cause.targetType.enumConstants.joinToString { it.toString() }}"
                        } else {
                            "Invalid value \"${cause.value}\""
                        }
                    }
                    is UnrecognizedPropertyException -> {
                        "Unrecognized property \"${cause.propertyName}\""
                    }
                    is IgnoredPropertyException -> {
                        "Invalid property \"${cause.propertyName}\""
                    }
                    is InvalidTypeIdException -> {
                        "Invalid object type \"${cause.typeId}\""
                    }
                    is MismatchedInputException -> {
                        "Invalid JSON payload"
                    }
                    is JsonParseException -> {
                        "JSON payload is not well formatted"
                    }
                    is JsonMappingException -> {
                        if (cause.path.isEmpty()) {
                            null
                        } else {
                            "Invalid value in field \"${cause.path.asStr()}\""
                        }
                    }
                    else -> null
                }
            }
            is MissingServletRequestParameterException -> {
                "Missing request parameter: ${throwable.parameterName}"
            }
            is MissingServletRequestPartException -> {
                "Missing request part: ${throwable.requestPartName}"
            }
            is RequestRejectedException -> {
                "The request was rejected because requests contains malicious URL, parameters or payload"
            }
            is MaxUploadSizeExceededException -> {
                "Upload exceeds the maximum upload size allowed"
            }
            is MultipartException -> {
                "Invalid multipart request"
            }
            else -> null
        }
        errorAttributes["message"] = message ?: "Unknown error"
        if (restErrorProperties.includeStacktraceForErrors) {
            errorAttributes["stacktrace"] = throwable?.stackTraceToString()
        }
    }

    private fun addFieldErrors(attrs: MutableMap<String, Any?>, exc: Throwable?) {
        if (exc == null) return
        when (exc) {
            is BindException -> {
                attrs[FIELD_ERRORS] = exc.bindingResult.fieldErrors.map {
                    mapOf(FIELD_PATH to it.field, FIELD_ERROR_MSG to it.defaultMessage)
                }
            }
            is HttpMessageNotReadableException -> {
                when (val cause = exc.cause) {
                    is InvalidFormatException -> {
                        val msg = if (cause.targetType.isEnum) {
                            "invalid value \"${cause.value}\", valid are: ${cause.targetType.enumConstants.joinToString { it.toString() }}"
                        } else {
                            "invalid value \"${cause.value}\""
                        }
                        if (cause.path.isNotEmpty()) {
                            attrs[FIELD_ERRORS] = listOf(mapOf(FIELD_PATH to cause.path.asStr(), FIELD_ERROR_MSG to msg))
                        }
                    }
                    is UnrecognizedPropertyException -> {
                        if (cause.path.isNotEmpty()) {
                            attrs[FIELD_ERRORS] = listOf(mapOf(FIELD_PATH to cause.path.asStr(), FIELD_ERROR_MSG to "unrecognized property"))
                        }
                    }
                    is JsonMappingException -> {
                        if (cause.path.isNotEmpty()) {
                            attrs[FIELD_ERRORS] = listOf(mapOf(FIELD_PATH to cause.path.asStr(), FIELD_ERROR_MSG to "invalid"))
                        }
                    }
                }
            }
        }
    }

    private fun addTimestamp(result: MutableMap<String, Any?>) {
        result["timestamp"] = Instant.now().toEpochMilli()
    }

    private fun addStatus(result: MutableMap<String, Any?>, status: Int) {
        result["status"] = status
    }

    private inline fun <reified T> getAttribute(requestAttributes: RequestAttributes, name: String): T? {
        val attr = requestAttributes.getAttribute(name, RequestAttributes.SCOPE_REQUEST) ?: return null
        if (attr !is T) {
            throw IllegalStateException("Attribute \"$name\" is $attr (${attr.javaClass}). Expected ${T::class.java}")
        }
        return attr
    }

    companion object {
        private val NOT_LOGGABLE_EXCEPTIONS: Set<Class<*>> = setOf(
            TypeMismatchException::class.java,
            MethodArgumentNotValidException::class.java,
            AccessDeniedException::class.java
        )
        private const val FIELD_ERRORS = "fieldErrors"
        private const val FIELD_PATH = "path"
        private const val FIELD_ERROR_MSG = "msg"
    }
}

private fun List<JsonMappingException.Reference>.asStr(): String {
    return this.joinToString(separator = ".") { it.fieldName ?: "[${it.index}]" }
}
