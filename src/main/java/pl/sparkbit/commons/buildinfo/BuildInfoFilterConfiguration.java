package pl.sparkbit.commons.buildinfo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;
import static pl.sparkbit.commons.Properties.BUILD_INFO_ENABLED;

@ConditionalOnProperty(value = BUILD_INFO_ENABLED, havingValue = "true", matchIfMissing = true)
@Configuration
@SuppressWarnings({"SpringFacetCodeInspection", "SpringJavaInjectionPointsAutowiringInspection"})
public class BuildInfoFilterConfiguration {

    private static final Integer MEDIUM_PRECEDENCE = HIGHEST_PRECEDENCE + 10;

    @Bean
    public FilterRegistrationBean buildInfoFilterRegistration(BuildInfoFactory buildInfoFactory) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(buildInfoFilter(buildInfoFactory));
        registration.setOrder(MEDIUM_PRECEDENCE);
        return registration;
    }

    @Bean
    public Filter buildInfoFilter(BuildInfoFactory buildInfoFactory) {
        return new BuildInfoFilter(buildInfoFactory);
    }
}
