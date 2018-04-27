package pl.sparkbit.commons.restlogger;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;
import static pl.sparkbit.commons.Properties.REQUEST_LOGGING_ENABLED;

@ConditionalOnProperty(value = REQUEST_LOGGING_ENABLED, havingValue = "true", matchIfMissing = true)
@Configuration
@SuppressWarnings("SpringFacetCodeInspection")
public class RestLoggingFilterConfiguration {

    private static final Integer RIGHT_AFTER_HIGHEST_PRECEDENCE = HIGHEST_PRECEDENCE + 1;

    @Bean
    public FilterRegistrationBean requestLoggingFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(requestLoggingFilter());
        //this filter should run very early in the chain - possibly only after compression filter
        registration.setOrder(RIGHT_AFTER_HIGHEST_PRECEDENCE);
        registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ERROR);
        return registration;
    }

    @Bean
    public Filter requestLoggingFilter() {
        return new RestLoggingFilter();
    }
}
