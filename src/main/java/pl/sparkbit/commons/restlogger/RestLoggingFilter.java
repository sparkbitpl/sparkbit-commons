package pl.sparkbit.commons.restlogger;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicLong;

import static javax.servlet.DispatcherType.ERROR;
import static javax.servlet.DispatcherType.REQUEST;
import static org.springframework.web.servlet.DispatcherServlet.EXCEPTION_ATTRIBUTE;

@Slf4j(topic = "restlogger")
public class RestLoggingFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID_ATTRIBUTE = "logging.requestId";

    private final AtomicLong id = new AtomicLong(0);

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        long requestId = getRequestId(request);

        if (log.isTraceEnabled()) {
            if (shouldLogRequest(request)) {
                request = logRequest(requestId, request);
            }

            response = new ResponseWrapper(response);
            chain.doFilter(request, response);

            if (shouldLogResponse(request)) {
                logResponse(requestId, request, response);
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        //we need this filter to be run also for ERROR dispatch - to log error responses
        return false;
    }

    private long getRequestId(HttpServletRequest request) {
        if (request.getAttribute(REQUEST_ID_ATTRIBUTE) != null) {
            //requestId found in attribute - this must be ERROR dispatch
            return (long) request.getAttribute(REQUEST_ID_ATTRIBUTE);
        } else {
            // new requestId - this must be REQUEST dispatch
            long requestId = id.incrementAndGet();
            request.setAttribute(REQUEST_ID_ATTRIBUTE, requestId);
            return requestId;
        }
    }

    private boolean shouldLogRequest(HttpServletRequest request) {
        //only log requests for REQUEST dispatch - request for ERROR dispatch should not be logged, because they
        //have already been logged by REQUEST dispatch
        return request.getDispatcherType() == REQUEST;
    }

    private HttpServletRequest logRequest(long requestId, HttpServletRequest request) throws IOException {
        log.trace("{} Incoming request headers \n{}", inPrompt(requestId), getHeaders(requestId, request));
        try {
            if (request.getContentType() != null &&
                    MediaType.valueOf(request.getContentType()).isCompatibleWith(MediaType.APPLICATION_JSON)) {
                request = new RequestWrapper(request, inPrompt(requestId));
                log.trace(((RequestWrapper) request).asString());
            }
        } catch (InvalidMediaTypeException e) {
            log.trace("Request has invalid Content-Type!");
        }
        return request;
    }

    private boolean shouldLogResponse(HttpServletRequest request) {
        //log responses for ERROR dispatch and when there was no exception and thus there will be no ERROR dispatch
        //don't log response if this is REQUEST dispatch, but there was an exception and ERROR dispatch will follow
        return request.getDispatcherType() == ERROR || request.getAttribute(EXCEPTION_ATTRIBUTE) == null;
    }

    private void logResponse(long requestId, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        log.trace("{} Outgoing response headers (possibly not all included): \n{}", outPrompt(requestId),
                getHeaders(requestId, response, request.getProtocol()));
        if (response.getContentType() != null &&
                MediaType.valueOf(response.getContentType()).isCompatibleWith(MediaType.APPLICATION_JSON)) {
            log.trace(((ResponseWrapper) response).asString(outPrompt(requestId)));
        }
    }

    private String getHeaders(long requestId, HttpServletRequest request) {
        String prompt = inPrompt(requestId);
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append(prompt).append(request.getMethod()).append(' ').append(request.getRequestURI());
        if (request.getQueryString() != null) {
            logBuilder.append('?').append(request.getQueryString());
        }
        logBuilder.append(' ').append(request.getProtocol()).append('\n');
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            logBuilder.append(prompt).append(headerName).append(" : ").
                    append(request.getHeader(headerName)).append('\n');
        }
        return logBuilder.deleteCharAt(logBuilder.length() - 1).toString();
    }

    private String getHeaders(long requestId, HttpServletResponse response, String protocol) {
        String prompt = outPrompt(requestId);
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append(prompt).append(protocol).append(' ').append(response.getStatus()).append(' ').
                append(HttpStatus.valueOf(response.getStatus()).name()).append('\n');
        Collection<String> headerNames = response.getHeaderNames();
        for (String headerName : headerNames) {
            logBuilder.append(prompt).append(headerName).append(" : ").append(response.getHeader(headerName)).
                    append('\n');
        }
        return logBuilder.deleteCharAt(logBuilder.length() - 1).toString();
    }

    private String outPrompt(long requestId) {
        return requestId + " <== ";
    }

    private String inPrompt(long requestId) {
        return requestId + " ==> ";
    }
}
