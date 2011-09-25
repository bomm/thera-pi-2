package einzigesPaket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;



public class EmailSendenExtern {
    public void sendMail(String smtpHost,String username,String password,String senderAddress,
    		String recipientsAddress,String subject,String text,ArrayList<String[]>attachments,boolean authx){

        MailAuthenticator auth = new MailAuthenticator(username, password);
        Properties properties = new Properties();
        // Den Properties wird die ServerAdresse hinzugefügt
        properties.put("mail.smtp.host", smtpHost);
        // !!Wichtig!! Falls der SMTP-Server eine Authentifizierung
        // verlangt
        // muss an dieser Stelle die Property auf "true" gesetzt
        // werden
        if(authx){
        	properties.put("mail.smtp.auth", "true");
        }else{
        	properties.put("mail.smtp.auth", "false");        	
        }
        
        /*
        if(SystemConfig.hmEmailExtern.get("SmtpAuth").equals("1")){
        	properties.put("mail.smtp.auth", "true");
        } else {
        	properties.put("mail.smtp.auth", "false");
        }
        */
        // Hier wird mit den Properties und dem implements Contructor
        // erzeugten
        // MailAuthenticator eine Session erzeugt
        Session session = Session.getDefaultInstance(properties, auth);
        try {
            // Eine neue Message erzeugen
            Message msg = new MimeMessage(session);
            // Hier werden die Absender- und Empfängeradressen gesetzt
            msg.setFrom(new InternetAddress(senderAddress));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(
                    recipientsAddress, false));
            // Der Betreff und Body der Message werden gesetzt
            msg.setSubject(subject);
            //msg.setText(text);
/*********************/
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(text);
            
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            
            if(attachments.size()>0){
            	DataSource source = null;
            	for(int i = 0;i<attachments.size();i++){
            		messageBodyPart = new MimeBodyPart();
    	           	source = new FileDataSource(attachments.get(i)[0]);
    	           	messageBodyPart.setDataHandler(new DataHandler(source));
    	           	messageBodyPart.setFileName(attachments.get(i)[1]);
    	           	multipart.addBodyPart(messageBodyPart);
    	           	System.out.println("In Email Files = "+attachments.get(i)[1]);
            	}
            }
            msg.setContent(multipart);
/*********************/            
            // Hier lassen sich HEADER-Informationen hinzufügen
            msg.setHeader("Test", "Test");
            msg.setSentDate(new Date( ));
            // Zum Schluss wird die Mail natürlich noch verschickt
            Transport.send(msg);
        }
        catch (Exception e) {
            e.printStackTrace( );
        }
    }

    class MailAuthenticator extends Authenticator {
        /**
         * Ein String, der den Usernamen nach der Erzeugung eines
         * Objektes<br>
         * dieser Klasse enthalten wird.
         */
        private final String user;
 
        /**
         * Ein String, der das Passwort nach der Erzeugung eines
         * Objektes<br>
         * dieser Klasse enthalten wird.
         */
        private final String password;
 
        /**
         * Der Konstruktor erzeugt ein MailAuthenticator Objekt<br>
         * aus den beiden Parametern user und passwort.
         *
         * @param user
         *            String, der Username fuer den Mailaccount.
         * @param password
         *            String, das Passwort fuer den Mailaccount.
         */
        public MailAuthenticator(String user, String password) {
            this.user = user;
            this.password = password;
        }
 
        /**
         * Diese Methode gibt ein neues PasswortAuthentication
         * Objekt zurueck.
         *
         * @see javax.mail.Authenticator#getPasswordAuthentication()
         */
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(this.user, this.password);
        }
    }
   
/*
    public static void main(String[] args) {
        String username = "";
        String password = "";
        String senderAddress ="";//someone@web.de
        String recipientsAddress = ""; //somereceiver@web.de
        String subject = "Test";
        String text = "text";
        String smtpHost = "smtp.web.de";
        new SendMailExample().sendMail(smtpHost, username, password, senderAddress, recipientsAddress, subject, text);

    }
*/    
}