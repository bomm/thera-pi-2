package lvaEntlassmitteilung;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;






public class LVArahmen implements WindowListener{
	
	JFrame jFrame;
	public static LVArahmen thisClass = null;
	public static boolean DbOk; 
	public Connection conn = null;	
	
	public String dieseMaschine = null;	
	public static String dbIpAndName = "jdbc:mysql://192.168.2.2:3306/dbf";
	public static String dbUser = "entwickler";
	public static String dbPassword = "entwickler";
	public static String reader;
	
	/*
	 * 
	 * Ende Deklaration
	 * 
	 */
	public static void main(String[] args) {
		System.out.println(" Name des Betriebssystems: "+System.getProperty("os.name"));
		System.out.println("      Benutzerverzeichnis: "+java.lang.System.getProperty("user.dir").replaceAll("\\\\","/"));
		System.out.println("Installierte Java-Version: "+java.lang.System.getProperty("java.version"));
		System.out.println("         Java-Verzeichnis: "+java.lang.System.getProperty("java.home").replaceAll("\\\\","/"));
		
		LVArahmen lbaust = new LVArahmen();
		
		lbaust.getJFrame(args);
	}
	
	public JFrame getJFrame(String[] args){
		try {
			UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		thisClass = this;
		if(args.length > 0){
			System.out.println("hole daten aus INI-Datei "+args[0]);
			INIFile inif = new INIFile(args[0]);
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
		}
		
		DatenbankStarten dbstart = new DatenbankStarten();
		dbstart.run();
		//SystemEinstellungen.ladeGelenke();
		jFrame = new JFrame();
		jFrame.setSize(500,500);
		jFrame.setTitle("LVA-Beendigungsmitteilung");
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setLocationRelativeTo(null);

		INIFile file = new INIFile(args[2]);
		reader = file.getStringProperty("FestProg", "FestProgPfad1");

	
		jFrame.getContentPane().add (new LVAoberflaeche(args[1]));
		jFrame.setVisible(true);
		
		return jFrame;
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		if(LVArahmen.thisClass.conn != null){
			try {
				LVArahmen.thisClass.conn.close();
				System.out.println("Datenbankverbindung wurde geschlossen");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.exit(0);
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		if(LVArahmen.thisClass.conn != null){
			try {
				LVArahmen.thisClass.conn.close();
				System.out.println("Datenbankverbindung wurde geschlossen");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.exit(0);
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}

/*************************/
final class DatenbankStarten implements Runnable{
	private void StarteDB(){
		final LVArahmen obj = LVArahmen.thisClass;


		final String sDB = "SQL";
		if (obj.conn != null){
			try{
			obj.conn.close();}
			catch(final SQLException e){}
		}
		try{
				Class.forName("com.mysql.jdbc.Driver").newInstance();

    	}
    	catch ( final Exception e ){
        		System.out.println(sDB+"Treiberfehler: " + e.getMessage());
        		LVArahmen.DbOk = false;
	    		return ;
        }	
	        	try {
	   				obj.conn = (Connection) DriverManager.getConnection(LVArahmen.dbIpAndName,LVArahmen.dbUser,LVArahmen.dbPassword);
    				//obj.conn = (Connection) DriverManager.getConnection("jdbc:mysql://192.168.2.2:3306/dbf","entwickler","entwickler");
	    			LVArahmen.DbOk = true;
	    			System.out.println("Datenbankkontakt hergestellt");
	        	} 
	        	catch (final SQLException ex) {
	        		System.out.println("SQLException: " + ex.getMessage());
	        		System.out.println("SQLState: " + ex.getSQLState());
	        		System.out.println("VendorError: " + ex.getErrorCode());
	        		LVArahmen.DbOk = false;
	        
	        	}
	        return;
	}
	public void run() {
		StarteDB();
	}

}
