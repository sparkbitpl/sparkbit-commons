package pl.sparkbit.commons.restlogger;

import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@ConditionalOnProperty(value = RestLoggerProperties.REST_LOGGER_ENABLED, havingValue = "true", matchIfMissing = true)
@Configuration
@EnableConfigurationProperties(RestLoggerProperties.class)
@RequiredArgsConstructor
public class RestLoggingFilterAutoConfiguration {

    private static final int RIGHT_AFTER_HIGHEST_PRECEDENCE = HIGHEST_PRECEDENCE + 1;

    @Bean
    public FilterRegistrationBean<RestLoggingFilter> requestLoggingFilterRegistration(RestLoggingFilter filter) {

        FilterRegistrationBean<RestLoggingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        //this filter should run very early in the chain - possibly only after compression filter
        registration.setOrder(RIGHT_AFTER_HIGHEST_PRECEDENCE);
        registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ERROR);
        return registration;
    }

    @Bean
    public RestLoggingFilter requestLoggingFilter(RestLoggerProperties properties) {
        return new RestLoggingFilter(properties.getExcludeUrlPatterns(), properties.getHttpHeadersToMask(),
                properties.getCookiesToMask());
    }
}
