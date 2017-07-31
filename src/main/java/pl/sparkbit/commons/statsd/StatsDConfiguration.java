package pl.sparkbit.commons.statsd;

import com.timgroup.statsd.NoOpStatsDClient;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class StatsDConfiguration {

    @Bean
    public StatsDClient statsDClient(@Value("${metrics.statsd.host:}") String host,
            @Value("${metrics.statsd.port:8125}") int port,
            @Value("${metrics.statsd.prefix:}") String prefix) {
        if (host.isEmpty()) {
            log.warn("NoOpStatsDClient will be used as statsd is disabled by configuration");
            return new NoOpStatsDClient();
        } else {
            return new NonBlockingStatsDClient(prefix, host, port);
        }
    }
}
