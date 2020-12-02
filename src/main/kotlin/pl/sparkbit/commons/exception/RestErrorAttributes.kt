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
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.WebRequest
import pl.sparkbit.commons.i18n.Messages
import java.time.Instant
import javax.servlet.RequestDispatcher

@Order(Ordered.HIGHEST_PRECEDENCE)
class RestErrorAttributes(
    private val messagesOpt: ObjectProvider<Messages>
) : DefaultErrorAttributes() {

    private val log = KotlinLogging.logger {}

    override fun getErrorAttributes(webRequest: WebRequest, opts: ErrorAttributeOptions): Map<String, Any?> {
        val accept = webRequest.getHeader("accept")
        return if (accept == null || MediaType.valueOf(accept).isCompatibleWith(MediaType.APPLICATION_JSON)) {
            val errorAttributes = mutableMapOf<String, Any?>()
            val status = getAttribute<Int>(webRequest, RequestDispatcher.ERROR_STATUS_CODE) ?: 999
            val throwable: Throwable? = getError(webRequest)
            addTimestamp(errorAttributes)
            addStatus(errorAttributes, status)
            addMessagesWithDetails(errorAttributes, throwable)
            addFieldErrors(errorAttributes, throwable)
            if (throwable == null && status in 500..599) {
                val message = getAttribute<Any>(webRequest, RequestDispatcher.ERROR_MESSAGE)
                log.error { "Runtime exception: $message" }
            } else if (NOT_LOGGABLE_EXCEPTIONS.stream().noneMatch { it.isInstance(throwable) }) {
                log.error("Runtime exception", throwable)
            }
            errorAttributes
        } else {
            super.getErrorAttributes(webRequest, opts)
        }
    }

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
            is MethodArgumentNotValidException -> {
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
            else -> null
        }
        errorAttributes["message"] = message ?: "Unknown error"
    }

    private fun addFieldErrors(attrs: MutableMap<String, Any?>, exc: Throwable?) {
        if (exc == null) return
        when (exc) {
            is MethodArgumentNotValidException -> {
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
