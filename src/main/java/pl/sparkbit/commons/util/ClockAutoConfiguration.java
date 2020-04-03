package pl.sparkbit.commons.util;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

import static pl.sparkbit.commons.CommonsProperties.CLOCK_ENABLED;

@ConditionalOnProperty(value = CLOCK_ENABLED, havingValue = "true", matchIfMissing = true)
@Configuration
public class ClockAutoConfiguration {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
