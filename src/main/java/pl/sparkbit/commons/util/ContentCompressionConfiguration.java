package pl.sparkbit.commons.util;

import com.github.ziplet.filter.compression.CompressingFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;
import static pl.sparkbit.commons.Properties.CONTENT_COMPRESSION_ENABLED;
import static pl.sparkbit.commons.Properties.CONTENT_COMPRESSION_THRESHOLD;

@ConditionalOnProperty(value = CONTENT_COMPRESSION_ENABLED, havingValue = "true", matchIfMissing = true)
@Configuration
@SuppressWarnings("SpringFacetCodeInspection")
public class ContentCompressionConfiguration {

    private static final int DEFAULT_COMPRESSION_THRESHOLD = 1024;

    @Value("${" + CONTENT_COMPRESSION_THRESHOLD + ":" + DEFAULT_COMPRESSION_THRESHOLD + "}")
    private int compressionThreshold;

    @Bean
    public FilterRegistrationBean compressingFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(compressingFilter());
        registration.addInitParameter("compressionThreshold", String.valueOf(compressionThreshold));
        registration.setOrder(HIGHEST_PRECEDENCE);
        return registration;
    }

    @Bean
    public Filter compressingFilter() {
        return new CompressingFilter();
    }
}
