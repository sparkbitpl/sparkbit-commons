package pl.sparkbit.commons.util;

import com.github.ziplet.filter.compression.CompressingFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;
import static pl.sparkbit.commons.CommonsProperties.CONTENT_COMPRESSION_ENABLED;

@Configuration
@EnableConfigurationProperties(ContentCompressionProperties.class)
@ConditionalOnProperty(value = CONTENT_COMPRESSION_ENABLED, havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@SuppressWarnings("SpringFacetCodeInspection")
@ConditionalOnClass(CompressingFilter.class)
public class ContentCompressionAutoConfiguration {

    private final ContentCompressionProperties configuration;

    @Bean
    public FilterRegistrationBean<CompressingFilter> compressingFilterRegistration() {
        FilterRegistrationBean<CompressingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(compressingFilter());
        String threshold = String.valueOf(configuration.getThreshold());
        registration.addInitParameter("compressionThreshold", threshold);
        registration.setOrder(HIGHEST_PRECEDENCE);
        return registration;
    }

    @Bean
    public CompressingFilter compressingFilter() {
        return new CompressingFilter();
    }
}

