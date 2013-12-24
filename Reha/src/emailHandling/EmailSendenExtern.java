package emailHandling;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.Security;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.JOptionPane;

import com.sun.mail.util.MailSSLSocketFactory;


@SuppressWarnings("unused")
public class EmailSendenExtern {
    public boolean sendMail(String smtpHost,String username,String password,String senderAddress,String recipientsAddress,String subject,String text,ArrayList<String[]>attachments,boolean authx,boolean bestaetigen,String secure ) throws AddressException, MessagingException, Exception{
    	
    	Session session = null;
    	Properties properties = null;
        Transport tran = null;
        Message msg = null;
        MailAuthenticator auth = null;
    	if(secure.equals("keine")){
            auth = new MailAuthenticator(username, password);
            
            properties = new Properties();
            if(properties.get("mail.smtp.host") != null){
              //System.out.println("Bereits belegt mit "+properties.get("mail.smtp.host"));	
            }
            properties.clear();
            
            
            properties.put("mail.smtp.host", smtpHost);
            properties.put("mail.smtp.socketFactory.fallback", "false");
            if(authx){
            	properties.put("mail.smtp.auth", "true");
            } else {
            	properties.put("mail.smtp.auth", "false");
            }
            session = Session.getInstance(properties, auth);	
            
        }else if(secure.equals("TLS/STARTTLS")){
        	properties = new Properties();
            properties.put("mail.smtp.host", smtpHost);
        	properties.put("mail.smtp.ssl.trust", smtpHost);            
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.port", "587");

            if(authx){
            	properties.put("mail.smtp.auth", "true");
            } else {
            	properties.put("mail.smtp.auth", "false");
            }
	
    		final String xusername = username;
    		final String xpassword = password;
    		session = Session.getInstance(properties,
    				new javax.mail.Authenticator() {
    			protected PasswordAuthentication getPasswordAuthentication() {
    				return new PasswordAuthentication(xusername, xpassword);
    			}
    		  });  
    		
        }else if(secure.equals("SSL")){
        	//Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        	properties = new Properties();
        	properties.put("mail.smtp.host", smtpHost);
        	
        	MailSSLSocketFactory sf = new MailSSLSocketFactory();
        	sf.setTrustAllHosts(true);
        	properties.put("mail.smtp.ssl.enable", "true");
        	properties.put("mail.smtp.ssl.socketFactory", sf);
 

        	properties.put("mail.smtp.socketFactory.port", "465");
        	properties.put("mail.smtp.socketFactory.class",
    				"javax.net.ssl.SSLSocketFactory");
           	properties.put("mail.smtp.ssl.trust", smtpHost);
            if(authx){
            	properties.put("mail.smtp.auth", "true");
            } else {
            	properties.put("mail.smtp.auth", "false");
            }
            properties.put("mail.smtp.port", "465");
    		final String xusername = username;
    		final String xpassword = password;
    		session = Session.getDefaultInstance(properties,
    			new javax.mail.Authenticator() {
    				protected PasswordAuthentication getPasswordAuthentication() {
    					return new PasswordAuthentication(xusername,xpassword);
    				}
    			});   
    		
        }else{
        	JOptionPane.showMessageDialog(null,"Fehler in der Emailkonfiguration, Item Sicherheitsstufe!");
        	return false;
        }
        
        
        //try{
        

         // Eine neue Message erzeugen
        msg = new MimeMessage(session);
        // Hier werden die Absender- und Empfängeradressen gesetzt
        msg.setFrom(new InternetAddress(senderAddress));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientsAddress, false));            	

            
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
            		/*
            		FileInputStream file;
					try {
						file = new FileInputStream(attachments.get(i)[0]);
	            		messageBodyPart.setDataHandler(new DataHandler(new Mail3Attachment(file, "application/octet-stream")));
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					*/
            	      
    	           	source = new FileDataSource(attachments.get(i)[0]);
    	           	messageBodyPart.setDataHandler(new DataHandler(source));
    	           	messageBodyPart.setFileName(attachments.get(i)[1]);
    	           	multipart.addBodyPart(messageBodyPart);
    	           	////System.out.println("In Email Files = "+attachments.get(i)[1]);
            	}
            }
            msg.setContent(multipart);
/*********************/            
            // Hier lassen sich HEADER-Informationen hinzuf�gen
            //msg.setHeader("Test", "Test");
            if(bestaetigen){
            	msg.addHeader("Return-Receipt-To", senderAddress);	
            }
            
            msg.setSentDate(new Date( ));
            // Zum Schluss wird die Mail natürlich noch verschickt
            
   			tran = session.getTransport("smtp");
   			/*
   			//System.out.println("Sender Domain ="+smtpHost);
   			//System.out.println("Sender Adress ="+senderAddress);
   			//System.out.println("Sender Password ="+password);
   			//System.out.println("Benutzername  ="+username);
   			tran.connect(smtpHost, 25, senderAddress, password);
   			*/
   			
            

            Transport.send(msg);
        /*    
            
        }catch(Exception ex){
        	JOptionPane.showMessageDialog(null,"Fehler beim Versand der Email, evtl kein Kontakt zum Internet");
        }
        */
           

        	if(session != null){
        		session = null;
        	}
        	if(msg != null){
                msg = null;        		
        	}
        	if(auth != null){
                auth = null;        		
        	}
        	if(tran != null){
        		tran = null;
        	}

        	return true;


    }

    class MailAuthenticator extends Authenticator {
        /**
         * Ein String, der den Usernamen nach der Erzeugung eines
         * Objektes<br>
         * dieser Klasse enthalten wird.
         */
        private String user;
 
        /**
         * Ein String, der das Passwort nach der Erzeugung eines
         * Objektes<br>
         * dieser Klasse enthalten wird.
         */
        private String password;
 
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
            ////System.out.println("In Authenticator user ="+this.user+"  Passwort = "+this.password);
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
    
/******************************************************/
   class Mail3Attachment implements DataSource
    {
      /** Datei */
      private byte[] m_File;
      /** MimeType */
      private String m_MimeType;

      /**
       * Erzeugt ein DataSource-Objekt über einen String
       * @param p_File Datei als String
       * @param p_MimeType MimeType
       */
      public Mail3Attachment(String p_File, String p_MimeType)
      {
        try
        {
          m_File = p_File.getBytes("iso-8859-1");
          m_MimeType = p_MimeType;
        }
        catch (UnsupportedEncodingException ueException)
        {
          ueException.printStackTrace();
        }
      }  

      /**
       * Erzeugt ein DataSource-Objekt über einen InputStream
       * @param p_File Datei als InputStream
       * @param p_MimeType MimeType der Datei
       */
      public Mail3Attachment(InputStream p_File, String p_MimeType) throws IOException
      {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        int ch;

        // Gepuffertes lesen
        BufferedInputStream bufferedInputStream = new BufferedInputStream(p_File);

        while((ch = bufferedInputStream.read()) != -1)
        {
          os.write(ch);
        }
        m_File = os.toByteArray();
        m_MimeType = p_MimeType;
      }

      /**
       * Erzeugt ein DataSource-Objekt über ein byte-Array
       * @param p_File Datei als byte-Array
       * @param p_MimeType MimeType der Datei
       */
      public Mail3Attachment(byte[] p_File, String p_MimeType)
      {
        m_File = p_File;
        m_MimeType = p_MimeType;
      }

      /*
       * (non-Javadoc)
       * @see javax.activation.DataSource#getInputStream()
       */
      public InputStream getInputStream() throws IOException
      {
        if (m_File == null)
        {
          throw new IOException("Keine Daten vorhanden");
        }
        return new ByteArrayInputStream(m_File);
      }

      /*
       * (non-Javadoc)
       * @see javax.activation.DataSource#getOutputStream()
       */
      public OutputStream getOutputStream() throws IOException
      {
        throw new IOException("Nicht implementiert");
      }

      /*
       * (non-Javadoc)
       * @see javax.activation.DataSource#getContentType()
       */
      public String getContentType()
      {
        return m_MimeType;
      }

      /*
       * (non-Javadoc)
       * @see javax.activation.DataSource#getName()
       */
      public String getName()
      {
        return "dummy";
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