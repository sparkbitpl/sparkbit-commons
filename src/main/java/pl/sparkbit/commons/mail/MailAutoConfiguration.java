package pl.sparkbit.commons.mail;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "sparkbit.commons.mail.sendgrid-enabled", havingValue = "true")
@EnableConfigurationProperties(MailProperties.class)
public class MailAutoConfiguration {

    @Bean
    public MailService mailService(MailProperties mailProperties) {
        return new SendGridMailServiceImpl(mailProperties);
    }
}
