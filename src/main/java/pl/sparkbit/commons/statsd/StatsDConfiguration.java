package pl.sparkbit.commons.statsd;

import com.timgroup.statsd.NoOpStatsDClient;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.sparkbit.commons.CommonsProperties;

import static pl.sparkbit.commons.CommonsProperties.*;

@ConditionalOnProperty(value = STATSD_ENABLED, havingValue = "true")
@Configuration
@Slf4j
@SuppressWarnings("SpringFacetCodeInspection")
public class StatsDConfiguration {

    @Bean
    public StatsDClient statsDClient(CommonsProperties configuration) {

        String host = configuration.getStatsd().getHost();

        if (host.isEmpty()) {
            log.warn("NoOpStatsDClient will be used as statsd is disabled by configuration");
            return new NoOpStatsDClient();
        } else {
            return new NonBlockingStatsDClient(configuration.getStatsd().getPrefix(), host,
                    configuration.getStatsd().getPort());
        }
    }
}
