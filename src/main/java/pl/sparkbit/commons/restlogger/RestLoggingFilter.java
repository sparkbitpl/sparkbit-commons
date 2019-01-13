package pl.sparkbit.commons.restlogger;

import lombok.RequiredArgsConstructor;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

@RequiredArgsConstructor
public class RestLoggingFilter extends CommonsRequestLoggingFilter {

    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final List<String> excludeUrlPatterns;

    @Override
    protected boolean shouldLog(HttpServletRequest request) {
        if (excludeUrlPatterns.stream().anyMatch(p -> pathMatcher.match(p, request.getServletPath()))) {
            return false;
        }
        return super.shouldLog(request);
    }
}
