package errorMail;

import java.util.ArrayList;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import CommonTools.INIFile;
import CommonTools.INITool;

import emailHandling.EmailSendenExtern;

import systemEinstellungen.SystemConfig;
import hauptFenster.Reha;

public class ErrorMail extends Thread{
	String fehlertext = null;
	String maschine = null;
	String benutzer = null;
	String sender = null;
	String titel = null;
	
	public ErrorMail(String text,String comp,String user,String senderadress,String xtitel){
		super();
		this.fehlertext = String.valueOf(text);
		this.maschine = String.valueOf(comp);
		this.benutzer = String.valueOf(user);
		this.sender = String.valueOf(senderadress);
		this.titel = String.valueOf(xtitel);
		start();
	}
	public void run(){
		INIFile ini = INITool.openIni(Reha.proghome+"ini/"+Reha.aktIK+"/", "error.ini"); 
		String empfaenger = ini.getStringProperty("Email", "RecipientAdress");
		   EmailSendenExtern oMail = new EmailSendenExtern();
			String smtphost = SystemConfig.hmEmailIntern.get("SmtpHost");
			String authent = SystemConfig.hmEmailIntern.get("SmtpAuth");
			String benutzer = SystemConfig.hmEmailIntern.get("Username") ;				
			String pass1 = SystemConfig.hmEmailIntern.get("Password");
			String sender = SystemConfig.hmEmailIntern.get("SenderAdresse"); 
			String recipient = empfaenger;
			ArrayList<String[]> attachments = new ArrayList<String[]>();
			boolean authx = (authent.equals("0") ? false : true);
			boolean bestaetigen = false;
			String emailtext = this.fehlertext+"\n"+
			"ausgelöst am Computer: "+this.maschine+"\n"+
			"eingeloggter Benutzer: "+this.benutzer+"\n"+
			"Absenderadresse: "+this.sender;
			try {
				oMail.sendMail(smtphost, benutzer, pass1, sender, recipient, titel, emailtext,attachments,authx,bestaetigen);
			} catch (AddressException e) {
				e.printStackTrace();
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		
		
	}

}
