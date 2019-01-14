package pl.sparkbit.commons.i18n;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagesAutoConfiguration {

    @Bean
    public Messages sparkbitMessages(MessageSource messageSource) {
        return new Messages(messageSource);
    }
}
