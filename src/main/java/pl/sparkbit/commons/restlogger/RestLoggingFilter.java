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

@Slf4j
public class RestLoggingFilter extends OncePerRequestFilter {

    private final AtomicLong id = new AtomicLong(0);

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        long requestId = id.incrementAndGet();
        if (log.isTraceEnabled()) {
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

            response = new ResponseWrapper(response);
            chain.doFilter(request, response);

            log.trace("{} Outgoing response headers (possibly not all included): \n{}", outPrompt(requestId),
                    getHeaders(requestId, response, request.getProtocol()));
            if (response.getContentType() != null &&
                    MediaType.valueOf(response.getContentType()).isCompatibleWith(MediaType.APPLICATION_JSON)) {
                log.trace(((ResponseWrapper) response).asString(outPrompt(requestId)));
            }
        } else {
            chain.doFilter(request, response);
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
