package pl.sparkbit.commons.exception;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.sparkbit.commons.i18n.Messages;

import static pl.sparkbit.commons.CommonsProperties.REST_ERROR_ATTRIBUTES_ENABLED;

@Configuration
@ConditionalOnProperty(value = REST_ERROR_ATTRIBUTES_ENABLED, havingValue = "true", matchIfMissing = true)
public class RestErrorAutoConfiguration {

    @Bean
    public RestErrorAttributes restErrorAttributes(ObjectProvider<Messages> messages) {
        return new RestErrorAttributes(messages);
    }
}
