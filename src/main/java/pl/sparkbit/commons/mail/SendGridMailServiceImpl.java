package pl.sparkbit.commons.mail;

import com.sendgrid.*;
import com.sendgrid.Mail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import pl.sparkbit.commons.CommonsProperties;
import pl.sparkbit.commons.exception.InternalException;

import java.io.IOException;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static pl.sparkbit.commons.CommonsProperties.*;

@ConditionalOnProperty(value = MAIL_SENDGRID_ENABLED, havingValue = "true")
@SuppressWarnings("unused")
@Service
@Slf4j
public class SendGridMailServiceImpl implements MailService {

    private static final String MAIL_SEND_ENDPOINT = "mail/send";
    private static final String TEMPLATE_VARIABLE_MARKER = "%";

    private final SendGrid sendGrid;

    private CommonsProperties configuration;

    public SendGridMailServiceImpl(@Value("#{commonsProperties.getMail().sendgridApiKey()}") String sendGridApiKey) {
        sendGrid = new SendGrid(sendGridApiKey);
    }

    @Override
    public void sendMail(String templateId, String to) {
        sendMail(templateId, to, configuration.getMail().getDefaultSenderAddress(),
                configuration.getMail().getDefaultSenderName(), emptyMap());
    }

    @Override
    public void sendMail(String templateId, String to, Map<String, String> params) {
        sendMail(templateId, to, configuration.getMail().getDefaultSenderAddress(),
                configuration.getMail().getDefaultSenderName(), params);
    }

    @Override
    public void sendMail(String templateId, String to, String senderAddress, String senderName) {
        sendMail(templateId, to, senderAddress, senderName, emptyMap());
    }

    @Override
    public void sendMail(String templateId, String to, String senderAddress, String senderName,
                         Map<String, String> params) {
        Mail mail = new Mail();
        mail.setFrom(new Email(senderAddress, senderName));
        mail.setTemplateId(templateId);

        Personalization personalization = new Personalization();
        personalization.addTo(new Email(to));
        params.forEach((key, value) -> personalization.addSubstitution(formatTemplateVariableKey(key), value));

        mail.addPersonalization(personalization);

        Request request = new Request();
        request.method = Method.POST;
        request.endpoint = MAIL_SEND_ENDPOINT;

        try {
            request.body = mail.build();
            sendGrid.api(request);
        } catch (IOException e) {
            log.error("Error while sending mail to %s", to);
            throw new InternalException("Sending email failed");
        }
    }

    private static String formatTemplateVariableKey(String key) {
        return TEMPLATE_VARIABLE_MARKER + key + TEMPLATE_VARIABLE_MARKER;
    }
}
