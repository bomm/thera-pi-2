package rehaSql;

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





import CommonTools.INIFile;
import CommonTools.INITool;
import CommonTools.SqlInfo;
import CommonTools.StartOOApplication;
import CommonTools.Verschluesseln;
import RehaIO.RehaIOMessages;
import RehaIO.RehaReverseServer;
import RehaIO.SocketClient;
import ag.ion.bion.officelayer.application.ILazyApplicationInfo;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.application.OfficeApplicationRuntime;
import ag.ion.bion.officelayer.document.DocumentException;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.event.ITerminateEvent;
import ag.ion.bion.officelayer.event.VetoTerminateListener;

public class RehaSql implements WindowListener {

	/**
	 * @param args
	 */
	/**
	 * @param args
	 */
	public static boolean DbOk;
	JFrame jFrame;
	public static JFrame thisFrame = null;
	public Connection conn;
	public static RehaSql thisClass;
	
	public static IOfficeApplication officeapplication;
	
	public String dieseMaschine = null;
	/*
	public static String dbIpAndName = null;
	public static String dbUser = null;
	public static String dbPassword = null;
	
	
*/
	public final Cursor wartenCursor = new Cursor(Cursor.WAIT_CURSOR);
	public final Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);

	public static String dbIpAndName = "jdbc:mysql://192.168.2.3:3306/rtadaten";
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
	
	public static int xport = -1;
	public static boolean xportOk = false;
	public RehaReverseServer rehaReverseServer = null;
	public static int rehaReversePort = -1;
	public boolean isLibreOffice;
	SqlInfo sqlInfo = null;
	
	public static void main(String[] args) {
		RehaSql application = new RehaSql();
		application.getInstance();
		application.getInstance().sqlInfo = new SqlInfo();
		if(args.length > 0 || testcase){
			if(!testcase){
				System.out.println("hole daten aus INI-Datei "+args[0]);
				INIFile inif = new INIFile(args[0]+"ini/"+args[1]+"/rehajava.ini");
				dbIpAndName = inif.getStringProperty("DatenBank","DBKontakt1")+"?jdbcCompliantTruncation=false&zeroDateTimeBehavior=convertToNull";
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
				INITool.init(progHome+"ini/"+aktIK+"/");
				if(args.length >= 3){
					rehaReversePort = Integer.parseInt(args[2]);
				}
			}

			final RehaSql xapplication = application;
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws java.lang.Exception {
					xapplication.starteDB();
					long zeit = System.currentTimeMillis();
					while(! DbOk){
						try {
							Thread.sleep(20);
							if(System.currentTimeMillis()-zeit > 10000){
								System.exit(0);
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					if(!DbOk){
						JOptionPane.showMessageDialog(null, "Datenbank konnte nicht geöffnet werden!\nReha-Sql kann nicht gestartet werden");				
					}
					RehaSql.starteOfficeApplication();
					return null;
				}
				
			}.execute();
			application.getJFrame();
		}else{
			/*
			final RehaSql xapplication = application;
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
						JOptionPane.showMessageDialog(null, "Datenbank konnte nicht geöffnet werden!\nReha-Sql kann nicht gestartet werden");				
					}
					RehaSql.starteOfficeApplication();
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
		jFrame = new JFrame(){
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void setVisible(final boolean visible) {
				
				if(getState()!=JFrame.NORMAL) { setState(JFrame.NORMAL); }

				  if (visible) {
				      //setDisposed(false);
				  }
				  if (!visible || !isVisible()) { 
				      super.setVisible(visible);
				  }

				  if (visible) {
				      int state = super.getExtendedState();
				      state &= ~JFrame.ICONIFIED;
				      super.setExtendedState(state);
				      super.setAlwaysOnTop(true);
				      super.toFront();
				      super.requestFocus();
				      super.setAlwaysOnTop(false);
				  }
			}

			@Override
			public void toFront() {
				  super.setVisible(true);
				  int state = super.getExtendedState();
				  state &= ~JFrame.ICONIFIED;
				  super.setExtendedState(state);
				  super.setAlwaysOnTop(true);
				  super.toFront();
				  super.requestFocus();
				  super.setAlwaysOnTop(false);
			}	
		};	
		try{
			rehaReverseServer = new RehaReverseServer(7000);
		}catch(Exception ex){
			rehaReverseServer = null;
		}
		sqlInfo.setFrame(jFrame);
		jFrame.addWindowListener(this);
		jFrame.setSize(1000,700);
		jFrame.setTitle("Thera-Pi  Sql-Modul  [IK: "+aktIK+"] "+"[Server-IP: "+dbIpAndName+"] - Äußerste Vorsicht ist geboten!!!");
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setLocationRelativeTo(null);
		jFrame.getContentPane().add (new RehaSqlTab());
		jFrame.setVisible(true);
		thisFrame = jFrame;
		try{
			new SocketClient().setzeRehaNachricht(RehaSql.rehaReversePort,"AppName#RehaSql#"+Integer.toString(RehaSql.xport));
			new SocketClient().setzeRehaNachricht(RehaSql.rehaReversePort,"RehaSql#"+RehaIOMessages.IS_STARTET);
		}catch(Exception ex){
			JOptionPane.showMessageDialog(null, "Fehler in der Socketkommunikation");
		}

		return jFrame;
	}
	
	
	/********************/
	
	public RehaSql getInstance(){
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
			RehaSql.thisClass.conn.close();
			RehaSql.thisClass.conn = null;
		} catch (SQLException e) {
			e.printStackTrace();
		} 			
	}
	
	/**********************************************************
	 * 
	 */
	final class DatenbankStarten implements Runnable{
		private void StarteDB(){
			final RehaSql obj = RehaSql.thisClass;

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
        		RehaSql.DbOk = false;
	    		return ;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
        		System.out.println(sDB+"Treiberfehler: " + e.getMessage());
        		RehaSql.DbOk = false;
	    		return ;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
        		System.out.println(sDB+"Treiberfehler: " + e.getMessage());
        		RehaSql.DbOk = false;
	    		return ;
			}	
        	try {
   				obj.conn = (Connection) DriverManager.getConnection(dbIpAndName,dbUser,dbPassword);
    			RehaSql.thisClass.sqlInfo.setConnection(obj.conn);
				RehaSql.DbOk = true;
    			System.out.println("Datenbankkontakt hergestellt");
        	} 
        	catch (final SQLException ex) {
        		System.out.println("SQLException: " + ex.getMessage());
        		System.out.println("SQLState: " + ex.getSQLState());
        		System.out.println("VendorError: " + ex.getErrorCode());
        		RehaSql.DbOk = false;
        
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
			final RehaSql obj = RehaSql.thisClass;

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
        		RehaSql.DbOk = false;
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
 
				RehaSql.DbOk = true;
    			System.out.println("Datenbankkontakt hergestellt");
        	} 
        	catch (final SQLException ex) {
				JOptionPane.showMessageDialog(null, "Kein Kontakt zu MySql bei 1 & 1");
        		System.out.println("SQLException: " + ex.getMessage());
        		System.out.println("SQLState: " + ex.getSQLState());
        		System.out.println("VendorError: " + ex.getErrorCode());
        		RehaSql.DbOk = false;
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
		if(RehaSql.thisClass.conn != null){
			try {
				RehaSql.thisClass.conn.close();
				System.out.println("Datenbankverbindung wurde geschlossen");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.exit(0);
	}
	@Override
	public void windowClosing(WindowEvent arg0) {
		if(RehaSql.thisClass.conn != null){
			try {
				RehaSql.thisClass.conn.close();
				System.out.println("Datenbankverbindung wurde geschlossen");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(RehaSql.thisClass.rehaReverseServer != null){
			try{
				new SocketClient().setzeRehaNachricht(RehaSql.rehaReversePort,"RehaSql#"+RehaIOMessages.IS_FINISHED);
				rehaReverseServer.serv.close();
			}catch(Exception ex){
				ex.printStackTrace();
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
    	try {
			officeapplication = (IOfficeApplication)new StartOOApplication(RehaSql.officeProgrammPfad,RehaSql.officeNativePfad).start(false);
			 System.out.println("OpenOffice ist gestartet und Active ="+officeapplication.isActive());
		} catch (OfficeApplicationException e1) {
			e1.printStackTrace();
		}
    }
	

}
