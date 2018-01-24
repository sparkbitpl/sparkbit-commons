package pl.sparkbit.commons.buildinfo;

import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.sparkbit.commons.buildinfo.domain.BuildInfo;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class BuildInfoFilter extends OncePerRequestFilter {

    private static final String APPLICATION_VERSION_HEADER = "X-App-Version";
    private static final String BUILD_NUMBER_HEADER = "X-Build-Number";
    private static final String BUILD_TIMESTAMP_HEADER = "X-Build-Timestamp";

    private final BuildInfoFactory buildInfoFactory;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final BuildInfo buildInfo = buildInfoFactory.getBuildInfo();

        response.addHeader(APPLICATION_VERSION_HEADER, buildInfo.getApplicationVersion());
        response.addHeader(BUILD_NUMBER_HEADER, buildInfo.getBuildNumber());
        response.addHeader(BUILD_TIMESTAMP_HEADER, buildInfo.getBuildTimestamp());
        filterChain.doFilter(request, response);
    }
}
