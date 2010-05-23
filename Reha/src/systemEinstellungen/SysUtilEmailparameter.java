package systemEinstellungen;

import hauptFenster.Reha;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LinearGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import systemTools.JRtaTextField;
import systemTools.Verschluesseln;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import emailHandling.EmailSendenExtern;



public class SysUtilEmailparameter extends JXPanel implements KeyListener, ActionListener {
	
	JButton knopf1 = null;
	JButton knopf2 = null;
	JButton knopf3 = null;

	//JButton knopf3 = null;
	
	JComboBox Postfach = null;
	String PFset = null;
	JRtaTextField Mailadresse = null;	
	JCheckBox EmpfBest = null;
	JRtaTextField Benutzer = null;
	JPasswordField Pass1 = null;
	JPasswordField Pass2 = null;
	JRtaTextField SMTPhost = null;
	JRtaTextField POPhost = null;
	JCheckBox Authent = null;
	HashMap<String,String> hmEmail = new HashMap<String,String>();
	public SysUtilEmailparameter(){
		super(new GridLayout(1,1));
		//System.out.println("Aufruf SysUtilEmailparameter");
		this.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 0));
		/****/
		setBackgroundPainter(Reha.thisClass.compoundPainter.get("SystemInit"));
		/****/
	     JScrollPane jscr = new JScrollPane();
	     jscr.setBorder(null);
	     jscr.setOpaque(false);
	     jscr.getViewport().setOpaque(false);
	     jscr.getVerticalScrollBar().setUnitIncrement(15);
	     jscr.setViewportView(getVorlagenSeite());
	     jscr.validate();
	     add(jscr);
		return;
	}
	/************** Beginn der Methode f�r die Objekterstellung und -platzierung *********/
	private JPanel getVorlagenSeite(){
		
		knopf1 = new JButton("abbrechen");
		knopf1.setPreferredSize(new Dimension(70, 20));
		knopf1.addActionListener(this);
		knopf1.setActionCommand("abbruch");
		knopf1.addKeyListener(this);
		knopf2 = new JButton("speichern");
		knopf2.setPreferredSize(new Dimension(70, 20));
		knopf2.addActionListener(this);
		knopf2.setActionCommand("speichern");
		knopf2.addKeyListener(this);
		knopf3 = new JButton("testen");
		knopf3.setPreferredSize(new Dimension(70, 20));
		knopf3.addActionListener(this);
		knopf3.setActionCommand("testen");
		knopf3.addKeyListener(this);
		String[] PFset = {"Postfach A", "Postfach B"};
		Postfach = new JComboBox(PFset);
		Postfach.setSelectedIndex(0);
		Postfach.setActionCommand("FachWahl");
		Postfach.addActionListener(this);
		Mailadresse = new JRtaTextField("", true);
		Mailadresse.setText(SystemConfig.hmEmailExtern.get("SenderAdresse"));
		EmpfBest = new JCheckBox();
		EmpfBest.setSelected((SystemConfig.hmEmailExtern.get("Bestaetigen")=="0" ? false : true));
		Benutzer = new JRtaTextField("", true);
		Benutzer.setText(SystemConfig.hmEmailExtern.get("Username"));
		Pass1 = new JPasswordField("");
		Pass1.setText(SystemConfig.hmEmailExtern.get("Password"));
		Pass2 = new JPasswordField("");
		Pass2.setText(SystemConfig.hmEmailExtern.get("Password"));		
		SMTPhost = new JRtaTextField("", true);
		SMTPhost.setText(SystemConfig.hmEmailExtern.get("SmtpHost"));		
		POPhost = new JRtaTextField("", true);
		POPhost.setText(SystemConfig.hmEmailExtern.get("Pop3Host"));
		Authent = new JCheckBox();
		Authent.setSelected((SystemConfig.hmEmailExtern.get("SmtpAuth")=="0" ? false : true));
/*
			hmEmailExtern = new HashMap<String,String>();
			hmEmailExtern.put("SmtpHost",new String(ini.getStringProperty("EmailExtern","SmtpHost")));
			hmEmailExtern.put("SmtpAuth",new String(ini.getStringProperty("EmailExtern","SmtpAuth")));			
			hmEmailExtern.put("Pop3Host",new String(ini.getStringProperty("EmailExtern","Pop3Host")));
			hmEmailExtern.put("Username",new String(ini.getStringProperty("EmailExtern","Username")));
			String pw = new String(ini.getStringProperty("EmailExtern","Password"));
			Verschluesseln man = Verschluesseln.getInstance();
		    man.init(Verschluesseln.getPassword().toCharArray(), man.getSalt(), man.getIterations());
			String decrypted = man.decrypt (pw);
			hmEmailExtern.put("Password",new String(decrypted));
			hmEmailExtern.put("SenderAdresse",new String(ini.getStringProperty("EmailExtern","SenderAdresse")));			
			hmEmailExtern.put("Bestaetigen",new String(ini.getStringProperty("EmailExtern","EmpfangBestaetigen")));			
********/
 		

		
		
        //                                      1.            2.    3.         4.     5.     6.    7.      8.     9.
		FormLayout lay = new FormLayout("right:max(60dlu;p), 10dlu, 90dlu, right:p",
				    //, 4dlu, 40dlu, 4dlu, 40dlu, 4dlu, 40dlu",
	   //1.    2.   3.   4.  5.    6.   7.  8.  9.   10.  11.  12.  13. 14.  15. 16.  17. 18.  19. 20. 21.  22.  23.  24.  25   26  27  28   29  30   31   32  33    34  35  36     37
		"p, 5dlu, p, 10dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 10dlu, p, 10dlu, p");
					
		PanelBuilder builder = new PanelBuilder(lay);
		builder.setDefaultDialogBorder();
		builder.getPanel().setOpaque(false);
		CellConstraints cc = new CellConstraints();
		
		//f�r das Panel oben im Fenster:
		//Hier k�nnen Sie bis zu zwei E-Mail-Postf�cher f�r programminterne Funktionen einrichten (z.B. Terminlisten mailen).
		//Die Zugangsdaten f�r Ihre E-Mailpostf�cher erhalten Sie von Ihrem Internetprovider. 
		//N�heres dazu auch im FAQ-Bereich auf thera-pi.org
		
		//builder.addLabel("Postfach w�hlen", cc.xy(3, 2));
		builder.add(Postfach, cc.xy(4, 1));
		
		builder.addSeparator("optionale Angaben", cc.xyw(1,3,4));
		
		builder.addLabel("Absender-Mailadresse", cc.xy(1, 5));
		builder.add(Mailadresse, cc.xyw(3,5,2));
		builder.addLabel("Empfangsbestätigung anfordern", cc.xy(1,7));
		builder.add(EmpfBest, cc.xy(4, 7));
		
		builder.addSeparator("Zugangsdaten", cc.xyw(1, 9, 4));
		
		builder.addLabel("Benutzername", cc.xy(1, 11));
		builder.add(Benutzer,cc.xyw(3, 11, 2));
		builder.addLabel("Passwort", cc.xy(1,13));
		builder.add(Pass1, cc.xyw(3, 13, 2));
		builder.addLabel("Passwort wiederholen", cc.xy(1,15));
		builder.add(Pass2, cc.xyw(3, 15, 2));
		builder.addLabel("SMTP-Host", cc.xy(1,17));
		builder.add(SMTPhost, cc.xyw(3, 17, 2));
		builder.addLabel("Authentifizierung erforderlich", cc.xy(1, 19));
		builder.add(Authent, cc.xy(4, 19));
		builder.addLabel("POP3-Host", cc.xy(1,21));
		builder.add(POPhost, cc.xyw(3, 21, 2));
		
		builder.addSeparator("", cc.xyw(1, 23, 4));
		
		
		builder.add(knopf1, cc.xy(1, 25));
		
		builder.add(knopf2, cc.xy(4, 25));
		
		builder.addSeparator("Testmail", cc.xyw(1,27,4));
		builder.addLabel("Wenn Sie eine Testmail erhalten, war die Einrichtung erfolgreich.", cc.xyw(1, 29, 3));
		builder.add(knopf3, cc.xy(4, 29));
		
		
		return builder.getPanel();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getActionCommand().equals("speichern")){
			if(Postfach.getSelectedIndex()==0){
				datenSpeichern(SystemConfig.hmEmailExtern,"EmailExtern");
			}else{
				datenSpeichern(SystemConfig.hmEmailIntern,"EmailIntern");				
			}
		}
		if(e.getActionCommand().equals("FachWahl")){
			wechsleEmail();
		}
		if(e.getActionCommand().equals("testen")){
			testeEmail();
		}
		if(e.getActionCommand().equals("abbruch")){
			SystemUtil.abbrechen();
			SystemUtil.thisClass.parameterScroll.requestFocus();
		}

		
	}
	private void datenSpeichern(HashMap<String,String> mailmap,String postfach ){
		String sender = Mailadresse.getText().trim();
		String bestaetigung  = (EmpfBest.isSelected() ? "1" : "0");
		String benutzer = Benutzer.getText().trim();
		String pass1 = Pass1.getText().trim();
		String pass2 = Pass2.getText().trim();
		if(!pass1.equals(pass2)){
			JOptionPane.showMessageDialog(null,"Die Passwort-Wiederholung stimmt nicht überein");
			return;
		}
		Verschluesseln man = Verschluesseln.getInstance();
		man.init(Verschluesseln.getPassword().toCharArray(), man.getSalt(), man.getIterations());
		String encrypted = man.encrypt(pass1);
		String smtphost = SMTPhost.getText().trim();
		String pophost = POPhost.getText().trim();
		String authent = ( Authent.isSelected() ? "1" : "0");
		mailmap.put("SenderAdresse", sender);
		mailmap.put("Bestaetigen", bestaetigung); 
		mailmap.put("Username", benutzer);
		mailmap.put("Password", pass1);		
		mailmap.put("SmtpHost", smtphost);
		mailmap.put("Pop3Host", pophost);
		mailmap.put("SmtpAuth", authent);		
		INIFile ini = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/rehajava.ini");
		ini.setStringProperty(postfach, "SenderAdresse", sender, null);
		ini.setStringProperty(postfach, "EmpfangBestaetigen", bestaetigung, null);	
		ini.setStringProperty(postfach, "Username",benutzer , null);		
		ini.setStringProperty(postfach, "Password",encrypted , null);		
		ini.setStringProperty(postfach, "SmtpHost",smtphost , null);		
		ini.setStringProperty(postfach, "Pop3Host",pophost , null);		
		ini.setStringProperty(postfach, "SmtpAuth",authent , null);		
		ini.save();
	
	}
	private void wechsleEmail(){
		if(Postfach.getSelectedIndex()==0){
			hmEmail = (HashMap<String,String>)SystemConfig.hmEmailExtern.clone();
		}else{
			hmEmail = (HashMap<String,String>)SystemConfig.hmEmailIntern.clone();
		}
		Mailadresse.setText(hmEmail.get("SenderAdresse"));
		EmpfBest.setSelected((hmEmail.get("Bestaetigen").equals("0") ? false : true));
		Benutzer.setText(hmEmail.get("Username"));
		Pass1.setText(hmEmail.get("Password"));
		Pass2.setText(hmEmail.get("Password"));		
		SMTPhost.setText(hmEmail.get("SmtpHost"));		
		POPhost.setText(hmEmail.get("Pop3Host"));
		Authent.setSelected((hmEmail.get("SmtpAuth").equals("0") ? false : true));
	
	}

	private void testeEmail(){
		String sender = Mailadresse.getText().trim();
		String bestaetigung  = (EmpfBest.isSelected() ? "1" : "0");
		String benutzer = Benutzer.getText().trim();
		String pass1 = Pass1.getText().trim();
		String pass2 = Pass2.getText().trim();
		if(!pass1.equals(pass2)){
			JOptionPane.showMessageDialog(null,"Die Passwort-Wiederholung stimmt nicht überein");
			return;
		}
		String smtphost = SMTPhost.getText().trim();
		String pophost = POPhost.getText().trim();
		String authent = ( Authent.isSelected() ? "1" : "0");
		String text = "Herzlichen Glückwunsch Ihr Postfach ist perferkt konfiguriert\n\n"+
				"Sie können diese Konfiguration nun abspeichern";
		boolean authx = (authent.equals("0") ? false : true);
		boolean bestaetigen = (bestaetigung.equals("0") ? false : true);

		ArrayList<String[]> attachments = new ArrayList<String[]>();		
		EmailSendenExtern oMail = new EmailSendenExtern();
		try{
		oMail.sendMail(smtphost, benutzer, pass1, sender, sender, "Test der Emailkonfiguration", text,attachments,authx,bestaetigen);
		oMail = null;
        JOptionPane.showMessageDialog(null,"Der Email-Account wurde korrekt konfiguriert!\n\n"+
		"Sie erhalten in Kürze eine Erfolgsmeldung per Email");
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, "Emailversand fehlgeschlagen\n\n"+
        			"Mögliche Ursachen:\n"+
        			"- falsche Angaben zu Ihrem Emailpostfach und/oder dem Provider\n"+
        			"- Sie haben kein Kontakt zum Internet");
			e.printStackTrace( );
		}
		/*message.addHeader("Return-Receipt-To", "toMe@home.com");*/
		
	}


}
