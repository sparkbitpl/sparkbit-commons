package pl.sparkbit.commons.exception

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.multipart.MaxUploadSizeExceededException
import org.springframework.web.multipart.MultipartException
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver

class RestErrorStatusCodeResolver : AbstractHandlerExceptionResolver() {

    init {
        order = LOWEST_PRECEDENCE
    }

    private fun badRequest(response: HttpServletResponse): ModelAndView {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST)
        return ModelAndView()
    }

    override fun doResolveException(request: HttpServletRequest, response: HttpServletResponse, handler: Any?, ex: Exception): ModelAndView? {
        return when (ex) {
            is MaxUploadSizeExceededException -> badRequest(response)
            is MultipartException -> badRequest(response)
            else -> null
        }
    }
}
