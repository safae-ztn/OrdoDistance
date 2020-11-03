package pfa.ordodistance.services;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pfa.ordodistance.models.MailProperties;

@Service
public class SendingMailService {
	
    private final MailProperties mailProperties;

    @Autowired
    SendingMailService(MailProperties mailProperties){
        this.mailProperties = mailProperties;
    }
    
    public boolean sendMail(String toEmail, String ordoPath) {
        try {
            Properties props = System.getProperties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.port", mailProperties.getSmtp().getPort());
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.ssl.trust", "*");
            Session session = Session.getDefaultInstance(props);
            session.setDebug(true);
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(mailProperties.getFrom(), mailProperties.getFromName()));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            msg.setSubject("Votre ordonnance electronique");
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent("Bon retablissement -  Powered By : OrdoDistance,plateforme de generation des ordonnances electroniques.", "text/html");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            MimeBodyPart attachPart = new MimeBodyPart();

            attachPart.attachFile(ordoPath);
            multipart.addBodyPart(attachPart);
            msg.setContent(multipart);

            Transport transport = session.getTransport();
            transport.connect(mailProperties.getSmtp().getHost(), mailProperties.getSmtp().getUsername(), mailProperties.getSmtp().getPassword());
            transport.sendMessage(msg, msg.getAllRecipients());
            return true;
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return false;
    }
}
