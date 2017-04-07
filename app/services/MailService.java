package services;

import pojos.Smtp;

/**
 * Created by seyi on 6/23/16.
 */
public class MailService {

    public static Smtp smtp() {
        Smtp smtp = new Smtp();
        smtp.username = SMTP_USERNAME;
        smtp.password = SMTP_PASSWORD;
        smtp.host = HOST;
        smtp.port = PORT;

        return smtp;
    }

    public static void sendAsync(String email, String subject, String body) {
        sendAsync(email, subject, body, SENDER, sendName, replyTo);
    }

    public static void sendAsync(String email, String subject, String body, String sender, String sendName, String replyTo) {
        new Thread(() -> {
            SmtpService.sendSmtp(email, subject, body, sender, sendName, replyTo, smtp());
        }).start();
    }

    public static final String SENDER = "tolet@tolet.com.ng";
    public static final String sendName = "ToLet.com.ng";
    public static final String replyTo = "info@tolet.com.ng";

    private static final String SMTP_USERNAME = "toletng";
    private static final String SMTP_PASSWORD = "uptPuE2TZARJfPwSYZQ2-A";

    private static final String HOST = "smtp.mandrillapp.com";
    private static final int PORT = 587;
}
