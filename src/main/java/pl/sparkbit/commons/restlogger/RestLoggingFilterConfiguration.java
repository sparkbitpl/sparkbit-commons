package pl.sparkbit.commons.restlogger;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.sparkbit.commons.CommonsProperties;

import javax.servlet.DispatcherType;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;
import static pl.sparkbit.commons.CommonsProperties.REQUEST_LOGGING_ENABLED;

@ConditionalOnProperty(value = REQUEST_LOGGING_ENABLED, havingValue = "true")
@Configuration
@RequiredArgsConstructor
@SuppressWarnings("SpringFacetCodeInspection")
public class RestLoggingFilterConfiguration {

    private static final Integer RIGHT_AFTER_HIGHEST_PRECEDENCE = HIGHEST_PRECEDENCE + 1;

    private final CommonsProperties config;

    @Bean
    public FilterRegistrationBean requestLoggingFilterRegistration() {
        FilterRegistrationBean<RestLoggingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(requestLoggingFilter());
        //this filter should run very early in the chain - possibly only after compression filter
        registration.setOrder(RIGHT_AFTER_HIGHEST_PRECEDENCE);
        registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ERROR);
        return registration;
    }

    @Bean
    public RestLoggingFilter requestLoggingFilter() {
        return new RestLoggingFilter(config.getRestLogger().getExcludeUrlPatterns());
    }
}
