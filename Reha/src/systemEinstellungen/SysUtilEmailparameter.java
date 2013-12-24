package systemEinstellungen;

import hauptFenster.Reha;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXPanel;
import org.thera_pi.nebraska.gui.utils.JCompTools;

import CommonTools.JRtaTextField;
import systemTools.Verschluesseln;
import CommonTools.INIFile;
import CommonTools.INITool;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import emailHandling.EmailSendenExtern;



public class SysUtilEmailparameter extends JXPanel implements KeyListener, ActionListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
	JComboBox Secure = null;
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
		Authent.setSelected((SystemConfig.hmEmailExtern.get("SmtpAuth").equals("0") ? false : true));
		
		Secure = new JComboBox(new String[] {"keine","TLS/STARTTLS","SSL"});
		if(SystemConfig.hmEmailExtern.get("SmtpSecure")==null){
			SystemConfig.hmEmailExtern.put("SmtpSecure","keine");
		}
		Secure.setSelectedItem(SystemConfig.hmEmailExtern.get("SmtpSecure"));
/*
			hmEmailExtern = new HashMap<String,String>();
			hmEmailExtern.put("SmtpHost",String.valueOf(ini.getStringProperty("EmailExtern","SmtpHost")));
			hmEmailExtern.put("SmtpAuth",String.valueOf(ini.getStringProperty("EmailExtern","SmtpAuth")));			
			hmEmailExtern.put("Pop3Host",String.valueOf(ini.getStringProperty("EmailExtern","Pop3Host")));
			hmEmailExtern.put("Username",String.valueOf(ini.getStringProperty("EmailExtern","Username")));
			String pw = String.valueOf(ini.getStringProperty("EmailExtern","Password"));
			Verschluesseln man = Verschluesseln.getInstance();
		    man.init(Verschluesseln.getPassword().toCharArray(), man.getSalt(), man.getIterations());
			String decrypted = man.decrypt (pw);
			hmEmailExtern.put("Password",String.valueOf(decrypted));
			hmEmailExtern.put("SenderAdresse",String.valueOf(ini.getStringProperty("EmailExtern","SenderAdresse")));			
			hmEmailExtern.put("Bestaetigen",String.valueOf(ini.getStringProperty("EmailExtern","EmpfangBestaetigen")));			
********/
 		

		
		
        //                                      1.            2.    3.         4.     5.     6.    7.      8.     9.
		FormLayout lay = new FormLayout("right:max(60dlu;p), 10dlu, 140dlu, right:p",
				    //, 4dlu, 40dlu, 4dlu, 40dlu, 4dlu, 40dlu",
	   //1.   2.  3.   4.   5.   6.  7.  8.    9.  10.  11.  12.  13. 14.  15. 16.  17. 18. 19. 20.  21.  22.  23.  24.  25   26   27   28   29  30   31   32  33    34  35  36     37
		"p, 5dlu, p, 10dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 5dlu");
	//	"p, 5dlu, p, 10dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 10dlu, p, 10dlu, p");
		
		
		PanelBuilder builder = new PanelBuilder(lay);
		//builder.setDefaultDialogBorder();
		builder.getPanel().setOpaque(false);
		CellConstraints cc = new CellConstraints();
		
		//f�r das Panel oben im Fenster:
		//Hier k�nnen Sie bis zu zwei E-Mail-Postf�cher f�r programminterne Funktionen einrichten (z.B. Terminlisten mailen).
		//Die Zugangsdaten f�r Ihre E-Mailpostf�cher erhalten Sie von Ihrem Internetprovider. 
		//N�heres dazu auch im FAQ-Bereich auf thera-pi.org
		
		//builder.addLabel("Postfach w�hlen", cc.xy(3, 2));
		builder.add(Postfach, cc.xy(4, 1));
		
		builder.addSeparator("Emailadresse / Empfangsquittung", cc.xyw(1,3,4));
		
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
		builder.addLabel("SMTP-Host (Mailausgang)", cc.xy(1,17));
		builder.add(SMTPhost, cc.xyw(3, 17, 2));
		builder.addLabel("Authentifizierung erforderlich", cc.xy(1, 19));
		builder.add(Authent, cc.xy(4, 19));
		builder.addLabel("POP3-Host (Maileingang)", cc.xy(1,21));
		builder.add(POPhost, cc.xyw(3, 21, 2));
		

		/*******Hier die Verschlüsselung rein*******/
		builder.addLabel("Sicherheitsstufe", cc.xy(1,23));
		builder.add(Secure, cc.xyw(3, 23, 2));
		
		/*******************************************/
		//builder.addSeparator("", cc.xyw(1, 23, 4));
		
		/***********nachfolgendes in eigenes Panel*****************/
		FormLayout lay1 = new FormLayout("right:max(60dlu;p), 10dlu, 140dlu,70px, right:p","5dlu,p,5dlu,p,5dlu,p,2dlu,p,5dlu");
		PanelBuilder builder1 = new PanelBuilder(lay1);
		CellConstraints cc1 = new CellConstraints(); 
		builder1.getPanel().setOpaque(false);
		
		//builder.add(new JLabel(""), cc.xyw(3, 1, 3,CellConstraints.FILL,CellConstraints.DEFAULT));
		
		builder1.addSeparator("Konfiguration testen / speichern / abbrechen", cc1.xyw(1,2,5));

		builder1.add(knopf1, cc1.xy(1, 4));
		builder1.add(knopf2, cc1.xy(5, 4));
		
		builder1.addLabel("Achtung: nur(!) wenn Sie nach dem Test eine Re-Email erhalten war die Einrichtung erfolgreich.", cc1.xyw(1, 6 , 5));
		builder1.add(knopf3, cc1.xy(5, 8));
		

		/*************Ende builder1******************************/
		
		builder.getPanel().validate();
		builder1.getPanel().validate();

		JScrollPane scr = JCompTools.getTransparentScrollPane(builder.getPanel());
		scr.validate();
		//JScrollPane scr0 = JCompTools.getTransparent2ScrollPane(scr);
		//scr0.validate();

		FormLayout lay0 = new FormLayout("fill:0:grow(1.0)","fill:0:grow(1.0),p");
		PanelBuilder builder0 = new PanelBuilder(lay0);
		builder0.getPanel().setOpaque(false);
		CellConstraints cc0 = new CellConstraints();
		
		builder0.add(scr,cc0.xy(1,1,CellConstraints.FILL,CellConstraints.FILL));
		builder0.add(builder1.getPanel(),cc0.xy(1,2,CellConstraints.FILL,CellConstraints.FILL));
		builder0.getPanel().validate();

		return builder0.getPanel();
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
			SystemInit.abbrechen();
			//SystemUtil.thisClass.parameterScroll.requestFocus();
		}

		
	}
	private void datenSpeichern(HashMap<String,String> mailmap,String postfach ){
		try{
			String sender = Mailadresse.getText().trim();
			String bestaetigung  = (EmpfBest.isSelected() ? "1" : "0");
			String benutzer = Benutzer.getText().trim();
			String pass1 = String.valueOf(Pass1.getPassword()).trim();
			String pass2 = String.valueOf(Pass2.getPassword()).trim();
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
			mailmap.put("SmtpSecure", Secure.getSelectedItem().toString());
			INIFile ini = INITool.openIni(Reha.proghome+"ini/"+Reha.aktIK+"/", "email.ini");
			ini.setStringProperty(postfach, "SenderAdresse", sender, null);
			ini.setStringProperty(postfach, "EmpfangBestaetigen", bestaetigung, null);	
			ini.setStringProperty(postfach, "Username",benutzer , null);		
			ini.setStringProperty(postfach, "Password",encrypted , null);		
			ini.setStringProperty(postfach, "SmtpHost",smtphost , null);		
			ini.setStringProperty(postfach, "Pop3Host",pophost , null);		
			ini.setStringProperty(postfach, "SmtpAuth",authent , null);	
			ini.setStringProperty(postfach, "SmtpSecure",Secure.getSelectedItem().toString(),null);
			INITool.saveIni(ini);
			JOptionPane.showMessageDialog(null, "Emailparameter für --> "+Postfach.getSelectedItem().toString()+" <-- wurden erfolgreich gespeichert");
		}catch(Exception ex){
			JOptionPane.showMessageDialog(null, "Fehler beim speichern der Emailparameter für --> "+Postfach.getSelectedItem().toString()+" <-- !!!!!");			
		}

	
	}
	@SuppressWarnings("unchecked")
	private void wechsleEmail(){
		if(Postfach.getSelectedIndex()==0){
			if(SystemConfig.hmEmailExtern.get("SmtpSecure")==null){
				SystemConfig.hmEmailExtern.put("SmtpSecure","keine");
			}
			hmEmail = (HashMap<String,String>)SystemConfig.hmEmailExtern.clone();
		}else{
			if(SystemConfig.hmEmailIntern.get("SmtpSecure")==null){
				SystemConfig.hmEmailIntern.put("SmtpSecure","keine");
			}
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
		Secure.setSelectedItem(hmEmail.get("SmtpSecure"));
	}

	private void testeEmail(){
		String sender = Mailadresse.getText().trim();
		String bestaetigung  = (EmpfBest.isSelected() ? "1" : "0");
		String benutzer = Benutzer.getText().trim();
		String pass1 = String.valueOf(Pass1.getPassword()).trim();
		String pass2 = String.valueOf(Pass2.getPassword()).trim();
		if(!pass1.equals(pass2)){
			JOptionPane.showMessageDialog(null,"Die Passwort-Wiederholung stimmt nicht überein");
			return;
		}
		String smtphost = SMTPhost.getText().trim();
		//String pophost = POPhost.getText().trim();
		String authent = ( Authent.isSelected() ? "1" : "0");
		String text = "Herzlichen Glückwunsch Ihr Postfach (Emailausgang) ist perfekt konfiguriert\n\n"+
				"Sie können diese Konfiguration nun abspeichern!\n(getestet wurde lediglich der Mailausgang)\n\n";
		boolean authx = (authent.equals("0") ? false : true);
		boolean bestaetigen = (bestaetigung.equals("0") ? false : true);
		String smtpport=null,popport=null;
		if(Secure.getSelectedItem().toString().equals("keine")){
			smtpport = "25";
			popport = "110";
		}else if(Secure.getSelectedItem().toString().equals("TLS/STARTTLS")){
			smtpport = "587";
			popport = "995";
		}else if(Secure.getSelectedItem().toString().equals("SSL")){
			smtpport = "465";
			popport = "995";
		}else{
			smtpport = "keine oder falsche Werte";
			popport = "keine oder falsche Werte";
		}
		ArrayList<String[]> attachments = new ArrayList<String[]>();
		String meldung = "Hostname (Mailausgang) = "+smtphost+"\n"+
		"SMTP-Port = "+smtpport+"\n"+
		"Hostname (Maileingang) = "+POPhost.getText()+"\n"+
		"Pop(3)-Port = "+popport+"\n"+
		"Benutzername = "+benutzer+"\n"+
		"Emailadresse = "+sender+"\n"+
		"PasswortAuthent. = "+(authx ? "JA" : "NEIN")+"\n"+
		"Sicherheitsstufe = "+Secure.getSelectedItem().toString();
		JOptionPane.showMessageDialog(null,"Gestestet wird mit folgenden Einstellungen:\n\n"+meldung+"\n");
		EmailSendenExtern oMail = new EmailSendenExtern();
		try{
			boolean success = oMail.sendMail(smtphost, benutzer, pass1, sender, sender, "Test der Emailkonfiguration", text+"\n\n"+meldung,attachments,authx,bestaetigen,Secure.getSelectedItem().toString());
			
	        if(success){
	        	JOptionPane.showMessageDialog(null,"Der Email-Account wurde korrekt konfiguriert!\n\n"+
	        			"Sie erhalten in Kürze eine Erfolgsmeldung per Email");
	        }else{
				JOptionPane.showMessageDialog(null, "Emailversand fehlgeschlagen\n\n"+
	        			"Mögliche Ursachen:\n"+
	        			"- falsche Angaben zu Ihrem Emailpostfach und/oder dem Provider\n"+
	        			"- Sie haben kein Kontakt zum Internet");
	        }
	        oMail = null;
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, "Emailversand fehlgeschlagen\n\n"+
        			"Mögliche Ursachen:\n"+
        			"- falsche Angaben zu Ihrem Emailpostfach und/oder dem Provider\n"+
        			"- Sie haben kein Kontakt zum Internet\n\nException="+(e.getMessage()==null ? "keine Angaben" : e.getMessage().toString()));
			e.printStackTrace( );
		}
		/*message.addHeader("Return-Receipt-To", "toMe@home.com");*/
		
	}


}
