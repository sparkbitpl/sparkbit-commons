package pl.sparkbit.commons.mail;

import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Personalization;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import lombok.extern.slf4j.Slf4j;
import pl.sparkbit.commons.exception.InternalException;

import java.io.IOException;
import java.util.Map;

import static java.util.Collections.emptyMap;

@Slf4j
public class SendGridMailServiceImpl implements MailService {

    private static final String MAIL_SEND_ENDPOINT = "mail/send";
    private static final String TEMPLATE_VARIABLE_MARKER = "%";

    private final SendGrid sendGrid;
    private final MailProperties mailProperties;

    public SendGridMailServiceImpl(MailProperties mailProperties) {
        this.sendGrid = new SendGrid(mailProperties.getSendgridApiKey());
        this.mailProperties = mailProperties;
    }

    @Override
    public void sendMail(String templateId, String to) {
        sendMail(templateId, to, mailProperties.getDefaultSenderAddress(),
            mailProperties.getDefaultSenderName(), emptyMap());
    }

    @Override
    public void sendMail(String templateId, String to, Map<String, String> params) {
        sendMail(templateId, to, mailProperties.getDefaultSenderAddress(),
            mailProperties.getDefaultSenderName(), params);
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
            throw new InternalException("Sending email failed", e);
        }
    }

    private static String formatTemplateVariableKey(String key) {
        return TEMPLATE_VARIABLE_MARKER + key + TEMPLATE_VARIABLE_MARKER;
    }
}
