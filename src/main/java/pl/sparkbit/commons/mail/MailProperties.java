package pl.sparkbit.commons.mail;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("sparkbit.commons.mail")
@Validated
@Data
public class MailProperties {
    private String defaultSenderAddress;
    private String defaultSenderName;
    private String sendgridApiKey;
}
