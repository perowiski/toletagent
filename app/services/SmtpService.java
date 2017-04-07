package services;

import controllers.Utility;
import pojos.Smtp;

import javax.mail.Address;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class SmtpService {
	public static String sendSmtp(String email, String subject, String body, String sender, String sendName, String replyTo, Smtp smtp) {
		Properties props = System.getProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.port", smtp.port);

		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.starttls.required", "true");

		try {
			Session session = Session.getDefaultInstance(props);

			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(sender.trim().toLowerCase(), sendName.trim()));
			msg.setRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(email.trim()));
			msg.setSubject(subject);
			msg.setContent(body,"text/html");

			if(Utility.isNotEmpty(replyTo)) {
				msg.setReplyTo(new Address[]{new InternetAddress(replyTo.trim())});
			}

			Transport transport = session.getTransport();
			transport.connect(smtp.host, smtp.username, smtp.password);

			transport.sendMessage(msg, msg.getAllRecipients());
			transport.close();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return ex.getMessage();
		}
		return "success";
	}
}