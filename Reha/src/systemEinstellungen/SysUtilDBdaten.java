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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import systemTools.JRtaTextField;
import systemTools.Verschluesseln;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SysUtilDBdaten extends JXPanel implements KeyListener, ActionListener {
	
	JComboBox mandant = null;
	JComboBox dbtyp = null;
	JButton knopf3 = null;
	JRtaTextField treiber = null;
	JRtaTextField server = null;
	JRtaTextField dbname = null;
	JRtaTextField dbuser = null;
	JPasswordField dbpasswort = null;
	JButton knopf4 = null;
	JButton knopf1 = null;
	JButton knopf2 = null;
	JRtaTextField port = null;
	
	
	public SysUtilDBdaten(){
		super(new GridLayout(1,1));
		//System.out.println("Aufruf SysUtilDBdaten");
		this.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 20));
		/****/
		setBackgroundPainter(Reha.thisClass.compoundPainter.get("SystemInit"));
		/****/
	     add(getVorlagenSeite());
		return;
	}
	/************** Beginn der Methode f�r die Objekterstellung und -platzierung *********/
	
	private JPanel getVorlagenSeite(){
		
		knopf2 = new JButton("abbrechen");
		knopf2.setPreferredSize(new Dimension(70, 20));
		knopf2.addActionListener(this);
		knopf2.setActionCommand("abbruch");
		knopf2.addKeyListener(this);
		knopf1 = new JButton("speichern");
		knopf1.setPreferredSize(new Dimension(70, 20));
		knopf1.addActionListener(this);
		knopf1.setActionCommand("speichern");
		knopf1.addKeyListener(this);
		knopf3 = new JButton("anderer Typ");
		knopf3.setPreferredSize(new Dimension(70, 20));
		knopf3.addActionListener(this);
		knopf3.setActionCommand("neutyp");
		knopf3.addKeyListener(this);
		knopf4 = new JButton("testen");
		knopf4.setPreferredSize(new Dimension(70, 20));
		knopf4.addActionListener(this);
		knopf4.setActionCommand("testen");
		knopf4.addKeyListener(this);
		
		mandant = new JComboBox() ;
		int vecindex = 0;
		for(int i = 0; i < SystemConfig.Mandanten.size();i++){
			mandant.addItem(SystemConfig.Mandanten.get(i)[1]);
			if(SystemConfig.Mandanten.get(i)[1].equals(Reha.aktMandant)){
				vecindex = i;
			}
		}
		mandant.setSelectedItem(Reha.aktMandant);
		mandant.setActionCommand("mandantwahl");
		mandant.addActionListener(this);
		dbtyp = new JComboBox ();
		for(int i = 0; i < SystemConfig.DBTypen.size();i++){
			dbtyp.addItem(SystemConfig.DBTypen.get(i)[0]);
		}
		dbtyp.setSelectedItem(SystemConfig.hmDBMandant.get(Reha.aktMandant).get(0));
		dbtyp.setActionCommand("dbtyp");
		dbtyp.addActionListener(this);
		////System.out.println("DBType von "+Reha.aktMandant+"= "+SystemConfig.hmDBMandant.get(Reha.aktMandant).get(0));
		//System.out.println(SystemConfig.hmDBMandant.get(Reha.aktMandant));
		treiber = new JRtaTextField("",true);
		treiber.setText((String)SystemConfig.hmDBMandant.get(Reha.aktMandant).get(1));
		server = new JRtaTextField("",true);
		server.setText((String)SystemConfig.hmDBMandant.get(Reha.aktMandant).get(2));
		dbname = new JRtaTextField("",true);
		dbname.setText((String)SystemConfig.hmDBMandant.get(Reha.aktMandant).get(4));		
		dbuser = new JRtaTextField("",true);
		dbuser.setText((String)SystemConfig.hmDBMandant.get(Reha.aktMandant).get(5));
		dbpasswort = new JPasswordField();
		dbpasswort.setText((String)SystemConfig.hmDBMandant.get(Reha.aktMandant).get(6));		
		port = new JRtaTextField("ZAHLEN", true);
		port.setText((String)SystemConfig.hmDBMandant.get(Reha.aktMandant).get(3));		
		/*
		mandantDB.add(minif.getStringProperty("Application", "DBType1"));
		mandantDB.add(minif.getStringProperty("Application", "DBTreiber1"));
		mandantDB.add(minif.getStringProperty("Application", "DBServer1"));			
		mandantDB.add(minif.getStringProperty("Application", "DBPort1"));			
		mandantDB.add(minif.getStringProperty("Application", "DBName1"));			
		mandantDB.add(minif.getStringProperty("Application", "DBBenutzer1"));	
		*/
		
		
        //                                      1.            2.    3.      4.     5.     6.    7.      8.     9.
		FormLayout lay = new FormLayout("right:max(60dlu;p), 10dlu, 40dlu, 40dlu, 10dlu, 40dlu",
       //1.    2.  3.   4.  5.   6.  7.   8.  9.  10.  11. 12. 13.  14.  15. 16.    17.  18.  19.   20.    21.   22.   23.
		"p, 10dlu, p, 2dlu, p,  2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 10dlu, p");
		
		PanelBuilder builder = new PanelBuilder(lay);
		builder.setDefaultDialogBorder();
		builder.getPanel().setOpaque(false);
		CellConstraints cc = new CellConstraints();
		
		builder.addLabel("Mandant auswählen", cc.xy(1,1));
		builder.add(mandant, cc.xyw(3, 1, 2));
		builder.addLabel("Datenbanktyp", cc.xy(1, 3));
		builder.add(dbtyp, cc.xyw(3,3,2));
		builder.add(knopf3, cc.xy(6,3));
		builder.addLabel("Treiberklasse", cc.xy(1, 5));
		builder.add(treiber, cc.xyw(3, 5, 2));
		builder.addLabel("Port", cc.xy(1, 7));
		builder.add(port, cc.xy(3, 7));
		builder.addLabel("Serveradresse", cc.xy(1,9));
		builder.add(server, cc.xyw(3,9,2));
		builder.addLabel("Datenbankname", cc.xy(1, 11));
		builder.add(dbname, cc.xyw(3,11,2));
		builder.addLabel("DB-Benutzername", cc.xy(1, 13));
		builder.add(dbuser, cc.xyw(3, 13,2));
		builder.addLabel("DB-Passwort", cc.xy(1, 15));
		builder.add(dbpasswort, cc.xyw(3,15,2));
		builder.addLabel("Datenbank testen", cc.xy(1,17));
		builder.add(knopf4, cc.xy(4,17));
		builder.addSeparator("", cc.xyw(1,19,6));
		builder.add(knopf2, cc.xy(4,21));
		builder.add(knopf1, cc.xy(6, 21));
		
		
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
		if(e.getActionCommand().equals("mandantwahl")){
			neuerDBMandant();
		}
		if(e.getActionCommand().equals("speichern")){
			speichernDBMandant();
		}
		if(e.getActionCommand().equals("testen")){
			testenDBMandant();
		}		
		if(e.getActionCommand().equals("neutyp")){
			JOptionPane.showMessageDialog(null,"Die Aufnahme einer 'Nicht-MySQL-Datenbank' wird erst später verfügbar sein");
		}
		if(e.getActionCommand().equals("abbruch")){
			SystemUtil.abbrechen();
			SystemUtil.thisClass.parameterScroll.requestFocus();
		}
		if(e.getActionCommand().equals("dbtyp")){
			dbtypEinstellen();
		}
					
	}
	private void speichernDBMandant(){
		String siniverz = null;
		String smandant = ((String) mandant.getSelectedItem()).trim();
		for(int i = 0; i < SystemConfig.Mandanten.size();i++){
			if(SystemConfig.Mandanten.get(i)[1].equals(smandant)){
				siniverz = SystemConfig.Mandanten.get(i)[0];
			}
		}
		String ss1,ss2,ss3,ss4,ss5,ss6,ss7;
		ss1 = ((String) dbtyp.getSelectedItem()).trim();
		ss2 = (treiber.getText().trim().equals("") ? "keineAngaben" : treiber.getText().trim());
		ss3 = (server.getText().trim().equals("") ? "keineAngaben" : server.getText().trim());		
		ss4 = (port.getText().trim().equals("") ? "" : port.getText().trim());
		ss5= (dbname.getText().trim().equals("") ? "" : dbname.getText().trim());
		ss6 = (dbuser.getText().trim().equals("") ? "" : dbuser.getText().trim());
		ss7 = (dbpasswort.getText().trim().equals("") ? "" : dbpasswort.getText().trim());
		if(ss2.equals("keineAngaben") || ss3.equals("keineAngaben") ||
				ss4.equals("keineAngaben") || ss5.equals("keineAngaben") ){
			JOptionPane.showMessageDialog(null,"Die Datenbank-Parameter sind unvollständig!\nZurück mit o.k.");
				treiber.requestFocus();
				return;
		}
		if(ss7.trim().equals("")){
			String stext = "Sie haben offensichtlich für den Zugang zur Datenbank kein Passwort eingerichtet\n"+
			"Sie müssen sich im klaren darüber sein, daß dies ein -> absolut hohes Sicherheitsrisiko <- bedeutet!";
			JOptionPane.showMessageDialog(null,stext);
		}

		INIFile dbini = new INIFile(Reha.proghome+"ini/"+siniverz+"/rehajava.ini");
		dbini.setStringProperty("DatenBank", "DBType1", ss1, null);
		dbini.setStringProperty("DatenBank", "DBTreiber1", ss2, null);
		dbini.setStringProperty("DatenBank", "DBServer1", ss3, null);
		dbini.setStringProperty("DatenBank", "DBPort1", ss4, null);
		dbini.setStringProperty("DatenBank", "DBName1", ss5, null);
		dbini.setStringProperty("DatenBank", "DBBenutzer1", ss6, null);

		Verschluesseln man = Verschluesseln.getInstance();
		man.init(Verschluesseln.getPassword().toCharArray(), man.getSalt(), man.getIterations());
		String encrypted = man.encrypt(ss7);
		dbini.setStringProperty("DatenBank", "DBPasswort1", encrypted, null);
		String skontakt = "jdbc:"+ss1+"://"+ss3+":"+ss4+"/"+ss5;
		dbini.setStringProperty("DatenBank", "DBKontakt1", skontakt, null);
		//jdbc:mysql://192.168.2.2:3306/dbf
		dbini.save();
		SystemConfig.MandantenEinlesen();
		String saktmandant = ((String)mandant.getSelectedItem()).trim(); 
		if(saktmandant.equals(Reha.aktMandant)){
			JOptionPane.showMessageDialog(null, "Die Datenbankeinstellungen für den aktuellen Mandant,\n"+
					"werden erst nach dem Neustart der ->Software<- wirksam\n\n"+
					"Ein Neustart des Computers ist nicht notwendig.");
		}

		
	}
	private void neuerDBMandant(){
		////System.out.println("In Mandant einstellen="+mandant.getSelectedItem());
		String smandant = ((String) mandant.getSelectedItem()).trim();
		dbtyp.setSelectedItem(SystemConfig.hmDBMandant.get(smandant).get(0));
		treiber.setText((String)SystemConfig.hmDBMandant.get(smandant).get(1));
		server.setText((String)SystemConfig.hmDBMandant.get(smandant).get(2));
		dbname.setText((String)SystemConfig.hmDBMandant.get(smandant).get(4));		
		dbuser.setText((String)SystemConfig.hmDBMandant.get(smandant).get(5));
		dbpasswort.setText((String)SystemConfig.hmDBMandant.get(smandant).get(6));		
		port.setText((String)SystemConfig.hmDBMandant.get(smandant).get(3));
		validate();
	}
	private void dbtypEinstellen(){
		int typ = dbtyp.getSelectedIndex();
		treiber.setText(SystemConfig.DBTypen.get(typ)[1]);
		port.setText(SystemConfig.DBTypen.get(typ)[2]);
	}

	private void testenDBMandant(){
		String ss1,ss2,ss3,ss4,ss5,ss6,ss7;
		ss1 = ((String) dbtyp.getSelectedItem()).trim();
		ss2 = (treiber.getText().trim().equals("") ? "keineAngaben" : treiber.getText().trim());
		ss3 = (server.getText().trim().equals("") ? "keineAngaben" : server.getText().trim());		
		ss4 = (port.getText().trim().equals("") ? "" : ":"+port.getText().trim());
		ss5= (dbname.getText().trim().equals("") ? "keineAngaben" : dbname.getText().trim());
		ss6 = (dbuser.getText().trim().equals("") ? "" : dbuser.getText().trim());
		ss7 = (dbpasswort.getText().trim().equals("") ? "" : dbpasswort.getText().trim());
		String skontakt = "jdbc:"+ss1+"://"+ss3+ss4+"/"+ss5;
		String messtext = "Im Anschluß wird getestet ob Sie mit den angegebenen Parametern auch tatsächlich\n"+
						"einen Aufbau zur Datenbank durchführen können.\n\n"+
						"Der Test kann einige Sekunden in Anspruch nehmen.\n\n"+
						"Bitte brechen Sie den Test keinesfalls ab - solange bis Sie einen\n"+
						"entsprechen Hinweis über Erfolg oder Mißerfolg erhalten";
		//System.out.println(skontakt);
		JOptionPane.showMessageDialog(null,messtext);
		try {
			Class.forName(ss2).newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null,"Die Treiberklasse konnte nicht initialisiert werden");
			return;
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null,"Sie haben keine Berechtigung für diese Treiberklasse");
			return;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null,"Die Treiberklasse der Datenbank wurde nicht gefunden, sind die Angaben korrekt?");
			return;
		}
		try {
			Connection conn = (Connection) DriverManager.getConnection(skontakt,ss6,ss7);
			JOptionPane.showMessageDialog(null,"Glückwunsch - die Datenbankparameter sind in Ordnung.\n"+
					"Kontakt zur Datenbank konnte hergestellt werden!");
			conn.close();
			conn = null;
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null,"Datenbankparameter sind falsch oder unvollständig.\n"+
			"Es konnte kein Kontakt zur Datenbank aufgebaut werden!");
			return;
		}
		
	}
}
