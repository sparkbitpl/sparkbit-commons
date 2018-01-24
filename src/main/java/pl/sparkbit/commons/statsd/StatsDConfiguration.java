package pl.sparkbit.commons.statsd;

import com.timgroup.statsd.NoOpStatsDClient;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static pl.sparkbit.commons.Properties.*;

@ConditionalOnProperty(value = STATSD_ENABLED, havingValue = "true")
@Configuration
@Slf4j
@SuppressWarnings("SpringFacetCodeInspection")
public class StatsDConfiguration {

    @Bean
    public StatsDClient statsDClient(@Value("${" + STATSD_HOST + "}") String host,
                                     @Value("${" + STATSD_PORT + ":8125}") int port,
                                     @Value("${" + STATSD_PREFIX + "}") String prefix) {
        if (host.isEmpty()) {
            log.warn("NoOpStatsDClient will be used as statsd is disabled by configuration");
            return new NoOpStatsDClient();
        } else {
            return new NonBlockingStatsDClient(prefix, host, port);
        }
    }
}
