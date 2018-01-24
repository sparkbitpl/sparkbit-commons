package pl.sparkbit.commons.util;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

import static pl.sparkbit.commons.Properties.CLOCK_ENABLED;

@ConditionalOnProperty(value = CLOCK_ENABLED, havingValue = "true", matchIfMissing = true)
@Configuration
@SuppressWarnings("SpringFacetCodeInspection")
public class ClockConfiguration {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
