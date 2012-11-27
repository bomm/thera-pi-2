package rehaUrlaub;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jdesktop.swingworker.SwingWorker;

import CommonTools.SqlInfo;
import CommonTools.StartOOApplication;
import CommonTools.INIFile;
import CommonTools.Verschluesseln;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.spreadsheet.ISpreadsheetDocument;

public class RehaUrlaub implements WindowListener {

	/**
	 * @param args
	 */
	/**
	 * @param args
	 */
	public static boolean DbOk = false;
	JFrame jFrame;
	public static JFrame thisFrame = null;
	public Connection conn;
	public static RehaUrlaub thisClass;
	
	public static IOfficeApplication officeapplication;
	public static boolean officeOk = false;
	
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
	public static String urlaubsDateiVorlage = "UrlaubUndStunden.ods";
	public boolean isLibreOffice;
	
	public static ISpreadsheetDocument spreadsheetDocument;
	
	public static boolean testcase = false;
	SqlInfo sqlInfo = null;
	
	public static void main(String[] args) {
		RehaUrlaub application = new RehaUrlaub();
		application.getInstance();
		application.getInstance().sqlInfo = new SqlInfo();
		try{
			

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

				inif = new INIFile(args[0]+"ini/"+args[1]+"/abrechnung.ini");
				String rechnung = inif.getStringProperty("HMPRIRechnung","Pformular");
				rechnung = rechnung.replace(".ott", "");
				rechnung = rechnung+"Kopie.ott";
				//hmRechnungPrivat = rechnung;
				
				
				progHome = args[0];
				aktIK = args[1];
			}

			final RehaUrlaub xapplication = application;
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws java.lang.Exception {
					xapplication.starteDB();
					long zeit = System.currentTimeMillis();
					while(! DbOk){
						try {
							Thread.sleep(20);
							if(System.currentTimeMillis()-zeit > 10000){
								JOptionPane.showMessageDialog(null, "TimeOut-für Datenbank erreicht");
								System.exit(0);
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					if(!DbOk){
						//JOptionPane.showMessageDialog(null, "Datenbank konnte nicht geöffnet werden!\nReha-Statistik kann nicht gestartet werden");
						//System.exit(0);
					}
					RehaUrlaub.starteOfficeApplication();
					return null;
				}
				
			}.execute();
			long zeit = System.currentTimeMillis();
			while(! DbOk){
				try {
					Thread.sleep(20);
					//JOptionPane.showMessageDialog(null, "Datenbank konnte nicht geöffnet werden!\nReha-Statistik kann nicht gestartet werden");
					if(System.currentTimeMillis()-zeit > 7000){
						//System.exit(0);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if(! DbOk){
				//JOptionPane.showMessageDialog(null, "Datenbank konnte nicht geöffnet werden!\nReha-Statistik kann nicht gestartet werden");
				//System.exit(0);
			}
			application.getJFrame();			
		}else{
			JOptionPane.showMessageDialog(null, "Keine Datenbankparameter übergeben!\nReha-Statistik kann nicht gestartet werden");
			System.exit(0);
		}
		}catch(Exception ex){
			ex.printStackTrace();
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
		sqlInfo.setFrame(jFrame);
		jFrame.setCursor(RehaUrlaub.thisClass.wartenCursor);
		jFrame.addWindowListener(this);
		jFrame.setSize(1000,500);
		jFrame.setPreferredSize(new Dimension(1000,500));
		jFrame.setTitle("Thera-Pi  Urlaub- / Überstundenverwaltung  [IK: "+aktIK+"] "+"[Server-IP: "+dbIpAndName+"]");
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setLocationRelativeTo(null);
		RehaUrlaubTab urlaubTab = new RehaUrlaubTab();
		jFrame.getContentPane().add (urlaubTab);
		jFrame.pack();
		//jFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		//urlaubTab.starteOOFrame();
		jFrame.setVisible(true);		

		

		thisFrame = jFrame;
		jFrame.setCursor(RehaUrlaub.thisClass.normalCursor);
		return jFrame;
	}
	
	
	/********************/
	
	public RehaUrlaub getInstance(){
		thisClass = this;
		return this;
	}
	
	/*******************/
	
	public void starteDB(){
		DatenbankStarten dbstart = new DatenbankStarten();
		dbstart.run(); 			
	}
	
	/*******************/
	
	public static void stoppeDB(){
		try {
			RehaUrlaub.thisClass.conn.close();
			RehaUrlaub.thisClass.conn = null;
		} catch (SQLException e) {
			e.printStackTrace();
		} 			
	}
	
	/**********************************************************
	 * 
	 */
	final class DatenbankStarten implements Runnable{
		private void StarteDB(){
			final RehaUrlaub obj = RehaUrlaub.thisClass;

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
        		RehaUrlaub.DbOk = false;
	    		return ;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
        		System.out.println(sDB+"Treiberfehler: " + e.getMessage());
        		RehaUrlaub.DbOk = false;
	    		return ;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
        		System.out.println(sDB+"Treiberfehler: " + e.getMessage());
        		RehaUrlaub.DbOk = false;
	    		return ;
			}	
        	try {
        		
   				obj.conn = (Connection) DriverManager.getConnection(dbIpAndName,dbUser,dbPassword);
   				sqlInfo.setConnection(obj.conn);
				RehaUrlaub.DbOk = true;
    			System.out.println("Datenbankkontakt hergestellt");
        	} 
        	catch (final SQLException ex) {
        		System.out.println("SQLException: " + ex.getMessage());
        		System.out.println("SQLState: " + ex.getSQLState());
        		System.out.println("VendorError: " + ex.getErrorCode());
        		RehaUrlaub.DbOk = false;
        
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

	@Override
	public void windowActivated(WindowEvent arg0) {
	}
	@Override
	public void windowClosed(WindowEvent arg0) {
		if(RehaUrlaub.thisClass.conn != null){
			try {
				RehaUrlaub.thisClass.conn.close();
				System.out.println("Datenbankverbindung wurde geschlossen");
				if(RehaUrlaub.spreadsheetDocument != null){
					RehaUrlaub.spreadsheetDocument.close();
					RehaUrlaub.spreadsheetDocument = null;

				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.exit(0);
	}
	@Override
	public void windowClosing(WindowEvent arg0) {
		if(RehaUrlaub.thisClass.conn != null){
			try {
				RehaUrlaub.thisClass.conn.close();
				System.out.println("Datenbankverbindung wurde geschlossen");
				if(RehaUrlaub.spreadsheetDocument != null){
					RehaUrlaub.spreadsheetDocument.close();
					RehaUrlaub.spreadsheetDocument = null;
				}
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
     	try {
			officeapplication = (IOfficeApplication)new StartOOApplication(RehaUrlaub.officeProgrammPfad,RehaUrlaub.officeNativePfad).start(false);
			RehaUrlaub.officeOk = true;
		} catch (OfficeApplicationException e1) {
			e1.printStackTrace();
		}

    	 
    }
	

}
