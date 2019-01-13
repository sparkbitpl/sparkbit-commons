package pl.sparkbit.commons.restlogger;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import javax.servlet.DispatcherType;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@ConditionalOnProperty(value = RestLoggerProperties.REST_LOGGER_ENABLED, havingValue = "true")
@Configuration
@EnableConfigurationProperties(RestLoggerProperties.class)
@RequiredArgsConstructor
@SuppressWarnings("SpringFacetCodeInspection")
public class RestLoggingFilterAutoConfiguration {

    private static final int RIGHT_AFTER_HIGHEST_PRECEDENCE = HIGHEST_PRECEDENCE + 1;

    @Bean
    public FilterRegistrationBean<CommonsRequestLoggingFilter> requestLoggingFilterRegistration(
        CommonsRequestLoggingFilter filter) {
        FilterRegistrationBean<CommonsRequestLoggingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        //this filter should run very early in the chain - possibly only after compression filter
        registration.setOrder(RIGHT_AFTER_HIGHEST_PRECEDENCE);
        registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ERROR);
        return registration;
    }

    @Bean
    public RestLoggingFilter requestLoggingFilter(RestLoggerProperties properties) {
        return new RestLoggingFilter(properties.getExcludeUrlPatterns());
    }
}
