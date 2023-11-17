package pl.sparkbit.commons.mail;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import static pl.sparkbit.commons.CommonsProperties.MAIL;

@ConfigurationProperties(MAIL)
@Validated
@Data
public class MailProperties {
    @NotBlank
    private String defaultSenderAddress;
    @NotBlank
    private String defaultSenderName;
    @NotBlank
    private String sendgridApiKey;
    private boolean sendgridEnabled = false;
}
