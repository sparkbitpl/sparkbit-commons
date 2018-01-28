package pl.sparkbit.commons.mail;

import java.util.Map;

@SuppressWarnings("unused")
public interface MailService {

    void sendMail(String templateId, String to);

    void sendMail(String templateId, String to, Map<String, String> params);

    void sendMail(String templateId, String to, String senderAddress, String senderName);

    void sendMail(String templateId, String to, String senderAddress, String senderName, Map<String, String> params);
}
