package pl.sparkbit.commons.mail;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static pl.sparkbit.commons.CommonsProperties.MAIL_SENDGRID_ENABLED;

@Configuration
@ConditionalOnProperty(value = MAIL_SENDGRID_ENABLED, havingValue = "true")
@EnableConfigurationProperties(MailProperties.class)
public class MailAutoConfiguration {

    @Bean
    public MailService mailService(MailProperties mailProperties) {
        return new SendGridMailServiceImpl(mailProperties);
    }
}
