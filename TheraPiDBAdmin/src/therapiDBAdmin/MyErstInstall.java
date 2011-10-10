package therapiDBAdmin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledPanel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;


public class MyErstInstall extends JXPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7924459762623527461L;
	
	public JXTitledPanel jxTitel;
	public JXHeader jxh;
	private Vector<String> vectitel = new Vector<String>();
	private Vector<String> vecdescript = new Vector<String>();
	private Vector<ImageIcon> vecimg = new Vector<ImageIcon>();

	private JButton[] buts = {null,null,null};
	private JFormattedTextField[] tfs = {null,null,null,null,null,null,null};
	JLabel lab1=null,lab2 = null;
	ActionListener al = null;
	MySqlTab eltern = null;

	MyErstInstall(MySqlTab xeltern){
		super();
		eltern = xeltern; 
		setLayout(new BorderLayout());
		doHeader();
		//add(getJXHeader(),BorderLayout.NORTH);
		activateListener();
		add(getJXContent(),BorderLayout.CENTER);
		validate();
		
	}
	private void activateListener(){
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd = arg0.getActionCommand();
				if(cmd.equals("testen")){
					if(doTesten()){
						JOptionPane.showMessageDialog(null,"Parameter sind o.k, Sie können den Mandanten jetzt auf IK-"+tfs[0].getText()+" umsetzen");
						buts[0].setEnabled(false);
						buts[1].setEnabled(true);
					}else{
						JOptionPane.showMessageDialog(null,"Ein oder mehrere Parameter sind fehlerhaft - Umsetzung nicht möglich");
					}
					
					return;
				}
				if(cmd.equals("umsetzen")){
					buts[1].setEnabled(false);
					new SwingWorker<Void,Void>(){
						@Override
						protected Void doInBackground() throws Exception {
							doUmsetzen();
							return null;
						}
					}.execute();
				}
				if(cmd.equals("abbrechen")){
					System.exit(0);
				}

			}
		};
	}
	
	private JXPanel getJXContent(){
		JXPanel pan = new JXPanel();
		//				  1      2     3     4      5
		String xwerte = "0dlu:g,220dlu,4dlu,120dlu,0dlu:g";
		//                1     2  3   4  5   6  7    8  9  10  11  12 13  14 15  16 17  18 19  20  21 
		String ywerte = "0dlu:g,p,3dlu,p,3dlu,p,25dlu,p,3dlu,p,3dlu,p,3dlu,p,10dlu,p,2dlu,p,15dlu,p,0dlu:g";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		pan.setLayout(lay);
		lab1 = new JLabel("IK des Standard-Mandant nach der Installation");
		pan.add(lab1,cc.xy(2,2,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		lab2 = new JLabel();
		lab2.setText("510841109");
		lab2.setForeground(Color.BLUE);
		pan.add(lab2,cc.xy(4,2));
		
		pan.add(new JLabel("IK des Standard-Mandant umsetzen auf Ihr eigenes IK ="),cc.xy(2,4,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		tfs[0] = new JFormattedTextField("123456789");
		tfs[0].setToolTipText("hier bitte Ihr IK eingeben (9-stellig)");
		pan.add(tfs[0],cc.xy(4, 4));
		
		pan.add(new JLabel("Ihr Firmenname (Kurzfassung)"),cc.xy(2,6,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		tfs[1] = new JFormattedTextField("KG-Praxis Häberle");
		tfs[1].setToolTipText("hier bitte Ihren Firmennamen eingeben");
		pan.add(tfs[1],cc.xy(4, 6));
		/*
		pan.add(new JLabel("IP-Adresse des MySql-Servers (oder localhost wenn gleicher Rechner)"),cc.xy(2,8,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		tfs[2] = new JFormattedTextField("192.168.1.100");
		tfs[2].setToolTipText("hier bitte die Adresse des MySql-Servers eingeben");
		pan.add(tfs[2],cc.xy(4, 8));

		pan.add(new JLabel("Name der Datenbank"),cc.xy(2,10,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		tfs[3] = new JFormattedTextField("kgprax");
		tfs[3].setToolTipText("hier bitte den Datenbanknamen eingeben");
		pan.add(tfs[3],cc.xy(4, 10));

		pan.add(new JLabel("Name des Datenbank-Users"),cc.xy(2,12,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		tfs[4] = new JFormattedTextField("kgprax");
		tfs[4].setToolTipText("hier bitte den DB-Usernamens");
		pan.add(tfs[4],cc.xy(4, 12));
		
		pan.add(new JLabel("Passwort des Datenbank-Users"),cc.xy(2,14,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		tfs[5] = new JFormattedTextField("ganzgeheim");
		tfs[5].setToolTipText("Passwort des Datenbank-Users");
		pan.add(tfs[5],cc.xy(4, 14));
		*/
		pan.add(buts[0] = ButtonTools.macheButton("Parameter-Testen", "testen", al),cc.xy(4, 16));
		pan.add(buts[1] = ButtonTools.macheButton("auf eigenes IK umsetzen", "umsetzen", al),cc.xy(4, 18));
		pan.add(buts[2] = ButtonTools.macheButton("abbrechen/Exit", "abbrechen", al),cc.xy(4, 20));
		buts[1].setEnabled(false);
		
		pan.validate();
		return pan;
	}
	private JXHeader getJXHeader(){
		JXHeader head = new JXHeader();
        head.setTitle(vectitel.get(0));
        head.setDescription(vecdescript.get(0));
        head.setIcon(vecimg.get(0));   
		return head;
	}
	private void doHeader(){
		ImageIcon ico;
        //String ss = System.getProperty("user.dir")+File.separator+"icons"+File.separator+"TPorg.png";
        String ss = TheraPiDbAdmin.proghome+"icons"+File.separator+"TPorg.png";
        ico = new ImageIcon(ss);
		vectitel.add("<html><font size='5'><font color='e77817'>Thera-Pi</font> für den erstmaligen Start einrichten</font></html>");
		vecdescript.add("<html>überprüfen Sie bitte ob alle u.g. Voraussetzungen auf Ihr System zutreffen.<br>" +
                "1. <b>MySql</b> muß bereits lokal auf Ihrem Rechner, oder aber auf dem zukünftigen Datenbankserver <b>vollständig und lauffähig</b> installiert sein. <br>" +
                "2. Sie müssen bereits eine <b>neue Datenbank</b> erstellt haben<br>" +
                "3. Für diese Datenbank muß ein <b>Datenbankbenutzer</b> erstellt worden sein der von überall erreichbar sein sollte (Host=<b>'%'</b>)<br>"+
                "4. Für den Datenbankbenutzer muß ein <b>Paßwort</b> erstellt worden sein<br>"+
                "5. Sie haben die <b>Thera-Pi-Tabellen</b> bereits in Ihre neue Datenbank <b>importiert</b>.<br>"+
                "<font color='aa0000'><b>Nur wenn Sie alle oben genannten Voraussetzungen erfüllen sollten Sie mit der Anpassung für den erstmaligen Start von Thera-Pi fortfahren.<br>Andernfalls brechen "+
                "Sie an dieser Stelle ab, holen die versäumten Installationsschritte nach und starten Sie dann dieses Tool erneut.</b></font><br><br>"+"</html>");
		vecimg.add(ico);
	}	
	
	public void setHeader(int header){
        jxh.setTitle(vectitel.get(header));
        jxh.setDescription(vecdescript.get(header));
        jxh.setIcon(vecimg.get(header));
        jxh.validate();
	}
	private boolean doTesten(){
		File f = new File(TheraPiDbAdmin.proghome+"ini/510841109/");
		String meinik = tfs[0].getText().trim();
		if(!f.exists()){
			JOptionPane.showMessageDialog(null, "Mandant 510841109 hat keine Verzeichnisse die umgesetzt werden könnten");
			return false;
		}
		if(meinik.length() != 9){
			JOptionPane.showMessageDialog(null, "IK muß 9-stellig sein");
			return false;
		}
		try{
			int iktest = Integer.parseInt(meinik);
		}catch(Exception ex){
			JOptionPane.showMessageDialog(null, "IK muß 9-stellig sein und darf nur aus Zahlen bestehen");
			return false;
		}
		/*
		boolean contest = doTestConnectivity();
		if(!contest){
			JOptionPane.showMessageDialog(null, "Datenbankverbindung fehlgeschlagen!\nSind die Angaben zur Datenbank wirklich korrekt?");
			return false;
		}
		*/
		for(int i = 0; i < 2; i++){
			tfs[i].setEditable(false);
		}
		return true;
	}
	
	private boolean doTestConnectivity(){
		boolean ret = false;
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (InstantiationException e) {
			e.printStackTrace();
        } catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}	
		try{
			//jdbc:mysql://192.168.2.3:3306/rtadaten
			String connection = "jdbc:mysql://"+
			MySqlTab.iPAdresse+":3306/"+MySqlTab.neuerDBName;
			//tfs[0].getText().trim()+":"+tfs[1].getText().trim()+"/"+tfs[4].getText().trim();
			System.out.println(connection);
			TheraPiDbAdmin.conn_root = (Connection) DriverManager.getConnection(connection,
					MySqlTab.neuerUser,
					MySqlTab.neuesPasswort);

			return true;
		}catch(final SQLException ex){
			JOptionPane.showMessageDialog(null, "Fehler in den Datenbankparametern, Fehlertext:\n"+ex.getMessage());
		}
		
		return ret;
	}
	private void doUmsetzen(){
		try{
			boolean allesok = true;
			String vz = TheraPiDbAdmin.proghome;
			String[] copySource = {vz+"ini/510841109/",vz+"vorlagen/510841109/",vz+"edifact/510841109/",
					vz+"keystore/510841109/",vz+"urlaub/510841109/",vz+"temp/510841109/"};

			String[] copyTarget = {vz+"ini/"+tfs[0].getText().trim()+"/",vz+"vorlagen/"+tfs[0].getText().trim()+"/",vz+"edifact/"+tfs[0].getText().trim()+"/",
					vz+"keystore/"+tfs[0].getText().trim()+"/",vz+"urlaub/"+tfs[0].getText().trim()+"/",vz+"temp/"+tfs[0].getText().trim()+"/"};
			testOderMacheVz(copyTarget);
			// Jetzt die neuen Verzeichnisse beschreiben
			File defvz = null;
			String[] dateien = null;
			for(int j = 0; j < copySource.length;j++){
				defvz = new File(copySource[j]);
				System.out.println("Lese Verzeichnis: "+copySource[j]);
				dateien = defvz.list();
				System.out.println("Anzahl Dateien im Verzeichnis "+copySource[j]+": "+dateien.length+" Dateien");
				for(int i = 0 ;i < dateien.length;i++){
					try {
						System.out.println("Kopiere Dateien Nr. "+i+": "+dateien[i]);
						lab1.setText("kopiere Datei");
						lab2.setText(dateien[i]);
						if(copySource[j].indexOf("keystore")<0){
							copyFile(new File(copySource[j]+dateien[i]),
									new File(copyTarget[j]+dateien[i]),1024,true);
						}
					} catch (IOException e) {
						e.printStackTrace();
						allesok = false;
					}
				}
			}
			if(!allesok){
				JOptionPane.showMessageDialog(null, "Umsetzung auf Mandant "+tfs[0].getText().trim()+" fehlgeschlagen!\n\nFehlertext = Dateien kopieren\n\n+Bitte melden Sie sich im Forum und geben Sie den Fehlertext bekannt.");
			}
			System.out.println("*************************************************************************");
			System.out.println("Verzeichnisse wurden angelegt, alle erforderlichen Dateien wurden kopiert");
			//rehajava.ini beschreiben	
			System.out.println("*************************************************************************");
			System.out.println("Beginne mit der Umsetzung der rehajava.ini");

			String decrypted = MySqlTab.neuesPasswort;
			Verschluesseln man = Verschluesseln.getInstance();
			man.init(Verschluesseln.getPassword().toCharArray(), man.getSalt(), man.getIterations());
			RWJedeIni.schreibeIniDatei(copyTarget[0]+"rehajava.ini", "DatenBank", "DBPasswort1", man.encrypt(decrypted));
			RWJedeIni.schreibeIniDatei(copyTarget[0]+"rehajava.ini", "DatenBank", "AnzahlConnections", "1");
			RWJedeIni.schreibeIniDatei(copyTarget[0]+"rehajava.ini", "DatenBank", "DBKontakt1", 
					"jdbc:mysql://"+MySqlTab.iPAdresse+":3306/"+MySqlTab.neuerDBName);
			RWJedeIni.schreibeIniDatei(copyTarget[0]+"rehajava.ini", "DatenBank", "DBName1",MySqlTab.neuerDBName);		
			RWJedeIni.schreibeIniDatei(copyTarget[0]+"rehajava.ini", "DatenBank", "DBBenutzer1",MySqlTab.neuerUser);
			RWJedeIni.schreibeIniDatei(copyTarget[0]+"rehajava.ini", "DatenBank", "DBServer1",MySqlTab.iPAdresse);
			//firmen.ini beschreiben
			RWJedeIni.schreibeIniDatei(copyTarget[0]+"firmen.ini", "Firma", "Ik",tfs[0].getText().trim() );
			RWJedeIni.schreibeIniDatei(copyTarget[0]+"firmen.ini", "Firma", "Ikbezeichnung",tfs[1].getText().trim() );
			//mandanten.ini beschreiben
			String mandantenini = TheraPiDbAdmin.proghome+"ini/mandanten.ini";
			RWJedeIni.schreibeIniDatei(mandantenini, "TheraPiMandanten", "AnzahlMandanten","1" );
			RWJedeIni.schreibeIniDatei(mandantenini, "TheraPiMandanten", "AuswahlImmerZeigen","1" );
			RWJedeIni.schreibeIniDatei(mandantenini, "TheraPiMandanten", "DefaultMandant","1" );
			RWJedeIni.schreibeIniDatei(mandantenini, "TheraPiMandanten", "LetzterMandant","1" );
			RWJedeIni.schreibeIniDatei(mandantenini, "TheraPiMandanten", "MAND-IK1",tfs[0].getText().trim() );
			RWJedeIni.schreibeIniDatei(mandantenini, "TheraPiMandanten", "MAND-NAME1",tfs[1].getText().trim() );

			System.out.println("*************************************************************************");
			System.out.println("Umsetzung der rehajava.ini beendet");
			System.out.println("*************************************************************************");
			System.out.println("Lösche die Angaben der firmen.ini");
			
			String[] stitel = {"Firma1","Firma2","Anrede","Nachname","Vorname",
					"Strasse","Plz","Ort","Telefon","Telefax","Email","Internet","Bank","Blz","Kto",
					"Steuernummer","Hrb","Logodatei","Zusatz1","Zusatz2","Zusatz3","Zusatz4"};
			for(int i = 0; i < stitel.length;i++){
				RWJedeIni.schreibeIniDatei(copyTarget[0]+"firmen.ini", "Firma",stitel[i], "" );
			}
			System.out.println("*************************************************************************");
			System.out.println("Aufbereitung der firmen.ini beendet");
			try{
				SwingUtilities.invokeLater(new Runnable(){
					public void run(){
						eltern.setSeite4Ok(true);					
					}
				});
			}catch(Exception ex){
				
			}
			JOptionPane.showMessageDialog(null, "IK erfolgreich umgesetzt, Thera-Pi wird nun gestartet....\n\n"+
					"Das Passwort für den Thera-Pi-Zugang lautet: -> superuser <-");
			try {
				System.out.println("*************************************************************************");
				System.out.println("Starte TheraPi");
				
				Runtime.getRuntime().exec("java -jar TheraPi.jar");
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Starten von Thera-Pi fehlgeschlagen!!!\n\n"+
				"Programm wird beendet");

			}
		}catch(Exception ex){
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Umsetzung auf eigenes IK fehlgeschlagen!!!\n\n"+
			"Programm wird beendet");
		}
		System.out.println("*************************************************************************");
		System.out.println("Beende PrepareTheraPi.jar");
		try{
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					System.exit(0);					
				}
			});
		}catch(Exception ex){
			
		}
		
	}
	private void testOderMacheVz(String[] vzs){
		File f;
		for(int i = 0; i < vzs.length;i++){
			f = new File(vzs[i]);
			if(!f.exists()){
				try{
					f.mkdir();
					lab1.setText("Verzeichnis erstellen");
					lab2.setText(vzs[i]);
				}catch(Exception ex){
					lab1.setText("Verzeichnis erstellen fehlgeschlagen");
					lab2.setText(vzs[i]);
				}
			}else{
				lab1.setText("Verzeichnis existiert bereits");
				lab2.setText(vzs[i]);
			}
		}
	}
	
	public static void copyFile(File src, File dest, int bufSize,
	        boolean force) throws IOException {
	    if(dest.exists()) {
	        if(force) {
	            dest.delete();
	        } else {
	            throw new IOException(
	                    "Kann existierende Datei nicht überschreiben: " + dest.getName());
	        }
	    }
	    byte[] buffer = new byte[bufSize];
	    int read = 0;
	    InputStream in = null;
	    OutputStream out = null;
	    try {
	        in = new FileInputStream(src);
	        out = new FileOutputStream(dest);
	        while(true) {
	            read = in.read(buffer);
	            if (read == -1) {
	                //-1 bedeutet EOF
	                break;
	            }
	            out.write(buffer, 0, read);
	        }
	    } finally {
	        // Sicherstellen, dass die Streams auch
	        // bei einem throw geschlossen werden.
	        // Falls in null ist, ist out auch null!
	        if (in != null) {
	            //Falls tats�chlich in.close() und out.close()
	            //Exceptions werfen, die jenige von 'out' geworfen wird.
	            try {
	                in.close();
	            }catch(Exception ex){
	            	
	            }
	            finally {
	                if (out != null) {
	                	out.flush();
	                    out.close();
	                }
	            }
	        }
	    }
	}

	

}
