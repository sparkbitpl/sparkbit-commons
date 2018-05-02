package pl.sparkbit.commons.util;

import com.github.ziplet.filter.compression.CompressingFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.sparkbit.commons.CommonsProperties;

import javax.servlet.Filter;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;
import static pl.sparkbit.commons.CommonsProperties.CONTENT_COMPRESSION_ENABLED;

@ConditionalOnProperty(value = CONTENT_COMPRESSION_ENABLED, havingValue = "true")
@Configuration
@RequiredArgsConstructor
@SuppressWarnings("SpringFacetCodeInspection")
public class ContentCompressionConfiguration {

    private final CommonsProperties configuration;

    @Bean
    public FilterRegistrationBean compressingFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(compressingFilter());
        String threshold = String.valueOf(configuration.getContentCompression().getThreshold());
        registration.addInitParameter("compressionThreshold", threshold);
        registration.setOrder(HIGHEST_PRECEDENCE);
        return registration;
    }

    @Bean
    public Filter compressingFilter() {
        return new CompressingFilter();
    }
}
