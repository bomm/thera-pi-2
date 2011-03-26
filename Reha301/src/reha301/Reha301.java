package reha301;

import java.awt.Cursor;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jdesktop.swingworker.SwingWorker;

import reha301Panels.RehaIOMessages;

import Tools.INIFile;
import Tools.Verschluesseln;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.application.OfficeApplicationRuntime;
import ag.ion.bion.officelayer.document.DocumentException;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.event.ITerminateEvent;
import ag.ion.bion.officelayer.event.VetoTerminateListener;

public class Reha301 implements WindowListener  {
	public static boolean DbOk;
	JFrame jFrame;
	public static JFrame thisFrame = null;
	public Connection conn;
	public static Reha301 thisClass;
	
	public static IOfficeApplication officeapplication;
	
	public String dieseMaschine = null;
	/*
	public static String dbIpAndName = null;
	public static String dbUser = null;
	public static String dbPassword = null;
	
	
*/
	public final Cursor wartenCursor = new Cursor(Cursor.WAIT_CURSOR);
	public final Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
	public final Cursor kreuzCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
	public final Cursor cmove = new Cursor(Cursor.MOVE_CURSOR);  //  @jve:decl-index=0:
	public final Cursor cnsize = new Cursor(Cursor.N_RESIZE_CURSOR);  //  @jve:decl-index=0:
	public final Cursor cnwsize = new Cursor(Cursor.NW_RESIZE_CURSOR);  //  @jve:decl-index=0:
	public final Cursor cnesize = new Cursor(Cursor.NE_RESIZE_CURSOR);  //  @jve:decl-index=0:
	public final Cursor cswsize = new Cursor(Cursor.SW_RESIZE_CURSOR);  //  @jve:decl-index=0:
	public final Cursor cwsize = new Cursor(Cursor.W_RESIZE_CURSOR);  //  @jve:decl-index=0:
	public final Cursor csesize = new Cursor(Cursor.SE_RESIZE_CURSOR);  //  @jve:decl-index=0:
	public final Cursor cssize = new Cursor(Cursor.S_RESIZE_CURSOR);  //  @jve:decl-index=0:
	public final Cursor cesize = new Cursor(Cursor.E_RESIZE_CURSOR);  //  @jve:decl-index=0:	
	public final Cursor cdefault = new Cursor(Cursor.DEFAULT_CURSOR);  //  @jve:decl-index=0:	


	public static String dbIpAndName = "jdbc:mysql://192.168.2.2:3306/rtadaten";
	public static String dbUser = "rtauser";
	public static String dbPassword = "rtacurie";
	public static String officeProgrammPfad = "C:/Program Files (x86)/LibreOffice 3";
	//public static String officeProgrammPfad = "C:/Programme/OpenOffice.org 3";
	public static String officeNativePfad = "C:/RehaVerwaltung/Libraries/lib/openofficeorg/";
	public static String progHome = "C:/RehaVerwaltung/";
	public static String aktIK = "510841109";
	public static String hmRechnungPrivat = "C:/RehaVerwaltung/vorlagen/HMRechnungPrivatKopie.ott";
	public static String hmRechnungKasse = "C:/RehaVerwaltung/vorlagen/HMRechnungPrivatKopie.ott";
	public static String rhRechnungPrivat = "C:/RehaVerwaltung/vorlagen/HMRechnungPrivatKopie.ott";
	public static String rhRechnungKasse = "C:/RehaVerwaltung/vorlagen/HMRechnungPrivatKopie.ott";
	
	public static boolean nachrichtfuerRezept = false;
	public static String argsRezeptnummer = "";

	public static String inbox = "//192.168.2.3/programme/data301/540840108/inbox/"; //C:/OODokumente/RehaVerwaltung/Dokumentation/301-er/";
	public static String outbox ="//192.168.2.3/programme/data301/540840108/outbox/"; 
	
	public static int rehaPort = -1;
	
	public static int xport = 7000;
	
	//public static String encodepfad = "C:/OODokumente/RehaVerwaltung/Dokumentation/301-er/";
	/*
	public static String dbIpAndName = "jdbc:mysql://192.168.2.2:3306/rtadaten";
	public static String dbUser = "rtauser";
	public static String dbPassword = "rtacurie";
	public static String officeProgrammPfad = "C:/Programme/OpenOffice.org 3";
	public static String officeNativePfad = "C:/RehaVerwaltung/Libraries/lib/openofficeorg/";
	public static String progHome = "C:/RehaVerwaltung/";
	public static String aktIK = "510841109";
	public static String hmRechnungPrivat = "C:/RehaVerwaltung/vorlagen/HMRechnungPrivatKopie.ott";
	public static String hmRechnungKasse = "C:/RehaVerwaltung/vorlagen/HMRechnungPrivatKopie.ott";
	public static String rhRechnungPrivat = "C:/RehaVerwaltung/vorlagen/HMRechnungPrivatKopie.ott";
	public static String rhRechnungKasse = "C:/RehaVerwaltung/vorlagen/HMRechnungPrivatKopie.ott";
	*/
	public static boolean testcase = false;
	
	public static void main(String[] args) {
		Reha301 application = new Reha301();
		application.getInstance();
		
		if(args.length > 0 || testcase){
			if(!testcase){
				System.out.println("hole daten aus INI-Datei "+args[0]);
				INIFile inif = new INIFile(args[0]+"ini/"+args[1]+"/rehajava.ini");
				dbIpAndName = inif.getStringProperty("DatenBank","DBKontakt1");
				dbUser = inif.getStringProperty("DatenBank","DBBenutzer1");
				String pw = inif.getStringProperty("DatenBank","DBPasswort1");
				String decrypted = null;
				if(pw != null){
					Verschluesseln man = Verschluesseln.getInstance();
					man.init(Verschluesseln.getPassword().toCharArray(), man.getSalt(), man.getIterations());
					decrypted = man.decrypt (pw);
				}else{
					decrypted = new String("");
				}
				dbPassword = decrypted.toString();
				inif = new INIFile(args[0]+"ini/"+args[1]+"/rehajava.ini");
				officeProgrammPfad = inif.getStringProperty("OpenOffice.org","OfficePfad");
				officeNativePfad = inif.getStringProperty("OpenOffice.org","OfficeNativePfad");
				progHome = args[0];
				aktIK = args[1];
				INIFile ini301 = new INIFile(args[0]+"ini/"+args[1]+"/dta301.ini");
				inbox = ini301.getStringProperty("DatenPfade301", "inbox");
				outbox = ini301.getStringProperty("DatenPfade301", "outbox");
				if(args.length == 3){
					try{
						rehaPort = Integer.parseInt(args[2]);
						//new SocketClient().setzeRehaNachricht(rehaPort, "Reha301#IrgendEineNachricht");
						//JOptionPane.showMessageDialog(null, "Modul Reha301 registriert Port "+Integer.toString(rehaPort));
					}catch(Exception ex){
						JOptionPane.showMessageDialog(null, "Fehler im Modul Reha301, kann den IO-Port nicht ermitteln");
					}
				}
			}
			final Reha301 xapplication = application;
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws java.lang.Exception {
					xapplication.starteDB();
					long zeit = System.currentTimeMillis();
					while(! DbOk){
						try {
							Thread.sleep(20);
							if(System.currentTimeMillis()-zeit > 5000){
								System.exit(0);
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					if(!DbOk){
						JOptionPane.showMessageDialog(null, "Datenbank konnte nicht geöffnet werden!\nReha-301 kann nicht gestartet werden");				
					}
					Reha301.starteOfficeApplication();
					return null;
				}
				
			}.execute();
			application.getJFrame();
			new SocketClient().setzeRehaNachricht(rehaPort, "Reha301#"+RehaIOMessages.IS_STARTET);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//new SocketClient().setzeRehaNachricht(rehaPort, "Reha301#"+RehaIOMessages.MUST_PATANDREZFIND+"#28222#RH7194");
		}else{
			/*
			final Reha301 xapplication = application;
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws java.lang.Exception {
					xapplication.starteDB();
					long zeit = System.currentTimeMillis();
					while(! DbOk){
						try {
							Thread.sleep(20);
							if(System.currentTimeMillis()-zeit > 5000){
								System.exit(0);
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					if(!DbOk){
						JOptionPane.showMessageDialog(null, "Datenbank konnte nicht ge�ffnet werden!\nReha-Sql kann nicht gestartet werden");				
					}
					Reha301.starteOfficeApplication();
					return null;
				}
				
			}.execute();
			application.getJFrame();
			*/
			
			JOptionPane.showMessageDialog(null, "Keine Datenbankparameter übergeben!\nReha-Sql kann nicht gestartet werden");
			System.exit(0);
			
		}
		
	}
	/********************/
	
	public JFrame getJFrame(){
		try {
			UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		thisClass = this;
		jFrame = new JFrame();
		jFrame.addWindowListener(this);
		jFrame.setSize(1000,550);
		jFrame.setTitle("Thera-Pi  §301-er  [IK: "+aktIK+"] "+"[Server-IP: "+dbIpAndName+"]");
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setLocationRelativeTo(null);
		jFrame.getContentPane().add (new Reha301Tab());
		jFrame.setVisible(true);
		thisFrame = jFrame;
		return jFrame;
	}
	
	
	/********************/
	
	public Reha301 getInstance(){
		thisClass = this;
		return this;
	}
	
	/*******************/
	
	public void starteDB(){
		
		//piHelpDatenbankStarten dbstart = new piHelpDatenbankStarten();
		//dbstart.run(); 			
		
		DatenbankStarten dbstart = new DatenbankStarten();
		dbstart.run();
		 			
	}
	
	/*******************/
	
	public static void stoppeDB(){
		try {
			Reha301.thisClass.conn.close();
			Reha301.thisClass.conn = null;
		} catch (SQLException e) {
			e.printStackTrace();
		} 			
	}
	
	/**********************************************************
	 * 
	 */
	final class DatenbankStarten implements Runnable{
		private void StarteDB(){
			final Reha301 obj = Reha301.thisClass;

			final String sDB = "SQL";
			if (obj.conn != null){
				try{
				obj.conn.close();}
				catch(final SQLException e){}
			}
			try{
				Class.forName("com.mysql.jdbc.Driver").newInstance();
	        } catch (InstantiationException e) {
				e.printStackTrace();
        		System.out.println(sDB+"Treiberfehler: " + e.getMessage());
        		Reha301.DbOk = false;
	    		return ;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
        		System.out.println(sDB+"Treiberfehler: " + e.getMessage());
        		Reha301.DbOk = false;
	    		return ;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
        		System.out.println(sDB+"Treiberfehler: " + e.getMessage());
        		Reha301.DbOk = false;
	    		return ;
			}	
        	try {
        		
   				obj.conn = (Connection) DriverManager.getConnection(dbIpAndName+"?jdbcCompliantTruncation=false",dbUser,dbPassword);
				Reha301.DbOk = true;
    			System.out.println("Datenbankkontakt hergestellt");
        	} 
        	catch (final SQLException ex) {
        		System.out.println("SQLException: " + ex.getMessage());
        		System.out.println("SQLState: " + ex.getSQLState());
        		System.out.println("VendorError: " + ex.getErrorCode());
        		Reha301.DbOk = false;
        
        	}
	        return;
		}
		public void run() {
			StarteDB();
		}
	
	
	}
	/*****************************************************************
	 * 
	 */
	/**********************************************************
	 * 
	 */
	final class piHelpDatenbankStarten implements Runnable{
		private void StarteDB(){
			final Reha301 obj = Reha301.thisClass;

			final String sDB = "SQL";
			if (obj.conn != null){
				try{
				obj.conn.close();}
				catch(final SQLException e){}
			}
			try{
				Class.forName("de.root1.jpmdbc.Driver");
				//Class.forName("com.mysql.jdbc.Driver").newInstance();
	         
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Kein Kontakt zu MySql bei 1 & 1");
        		System.out.println(sDB+"Treiberfehler: " + e.getMessage());
        		Reha301.DbOk = false;
	    		return ;
			}	
        	try {
    			System.out.println("Starte de.root1.jpmdbc.Drive");
    			//System.out.println("Starte Serveradresse:192.168.2.2");
    			//obj.connMySql = (Connection) DriverManager.getConnection("jdbc:mysql://192.168.2.2:3306/dbf","entwickler","entwickler");
    			Properties connProperties = new Properties();
    			connProperties.setProperty("user", "dbo336243054");
    			connProperties.setProperty("password", "allepreise");
    			//connProperties.setProperty("host", "localhost");
    			connProperties.setProperty("host", "db2614.1und1.de");	        			
    			//connProperties.setProperty("host", "db2614.1und1.de");
    			connProperties.setProperty("port", "3306");
    			connProperties.setProperty("compression","false");
    			connProperties.setProperty("NO_DRIVER_INFO", "1");

    			obj.conn = (Connection) DriverManager.getConnection("jdbc:jpmdbc:http://www.thera-pi.org/jpmdbc.php?db336243054",connProperties);
        		
   				//obj.conn = (Connection) DriverManager.getConnection(dbIpAndName,dbUser,dbPassword);
				Reha301.DbOk = true;
    			System.out.println("Datenbankkontakt hergestellt");
        	} 
        	catch (final SQLException ex) {
				JOptionPane.showMessageDialog(null, "Kein Kontakt zu MySql bei 1 & 1");
        		System.out.println("SQLException: " + ex.getMessage());
        		System.out.println("SQLState: " + ex.getSQLState());
        		System.out.println("VendorError: " + ex.getErrorCode());
        		Reha301.DbOk = false;
        
        	}
	        return;
		}
		public void run() {
			StarteDB();
		}
	
	
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
	}
	@Override
	public void windowClosed(WindowEvent arg0) {
		if(Reha301.thisClass.conn != null){
			try {
				Reha301.thisClass.conn.close();
				System.out.println("Datenbankverbindung wurde geschlossen");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.exit(0);
	}
	@Override
	public void windowClosing(WindowEvent arg0) {
		if(Reha301.thisClass.conn != null){
			try {
				Reha301.thisClass.conn.close();
				System.out.println("Datenbankverbindung wurde geschlossen");
				new SocketClient().setzeRehaNachricht(rehaPort, "Reha301#"+RehaIOMessages.IS_FINISHED);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.exit(0);
	}
	@Override
	public void windowDeactivated(WindowEvent arg0) {
	}
	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}
	@Override
	public void windowIconified(WindowEvent arg0) {
	}
	@Override
	public void windowOpened(WindowEvent arg0) {
	}
	
	/***************************/
	
    public static void starteOfficeApplication(){ 

    	final String OPEN_OFFICE_ORG_PATH = Reha301.officeProgrammPfad;

        try
        {
        	//System.out.println("**********Open-Office wird gestartet***************");
            String path = OPEN_OFFICE_ORG_PATH;
            Map <String, String>config = new HashMap<String, String>();
            config.put(IOfficeApplication.APPLICATION_HOME_KEY, path);
            config.put(IOfficeApplication.APPLICATION_TYPE_KEY, IOfficeApplication.LOCAL_APPLICATION);
            System.setProperty(IOfficeApplication.NOA_NATIVE_LIB_PATH,Reha301.officeNativePfad);
            officeapplication = OfficeApplicationRuntime.getApplication(config);
            officeapplication.activate();
            officeapplication.getDesktopService().addTerminateListener(new VetoTerminateListener() {
            	  public void queryTermination(ITerminateEvent terminateEvent) {
            	    super.queryTermination(terminateEvent);
            	    try {
            	      IDocument[] docs = officeapplication.getDocumentService().getCurrentDocuments();
            	      if (docs.length == 1) { 
            	        docs[0].close();
            	        //System.out.println("Letztes Dokument wurde geschlossen");
            	      }
            	    }
            	    catch (DocumentException e) {
            	    	e.printStackTrace();
            	    } catch (OfficeApplicationException e) {
						e.printStackTrace();
					}
            	  }
            	});
        }
        catch (OfficeApplicationException e) {
            e.printStackTrace();
        }
    }
	

}
