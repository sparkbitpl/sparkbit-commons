package pl.sparkbit.commons.util;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static pl.sparkbit.commons.CommonsProperties.ID_GENERATOR_ENABLED;

@Configuration
@ConditionalOnProperty(value = ID_GENERATOR_ENABLED, havingValue = "true", matchIfMissing = true)
public class IdGeneratorAutoConfiguration {

    @Bean
    public IdGenerator sparkbitIdGenerator() {
        return new IdGeneratorImpl();
    }
}
