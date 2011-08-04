package arztBausteine;



import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXFrame;

import com.sun.star.uno.Exception;

import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.application.OfficeApplicationRuntime;
import ag.ion.bion.officelayer.document.DocumentException;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.event.ITerminateEvent;
import ag.ion.bion.officelayer.event.VetoTerminateListener;

public class ArztBausteine implements WindowListener, WindowStateListener {

	/**
	 * @param args
	 */
	public static String OpenOfficePfad = "C:/Programme/OpenOffice.org 3";
	public static IOfficeApplication officeapplication = null;
	public static String OpenOfficeNativePfad = "C:/RehaVerwaltung/Libraries/lib/openofficeorg";
	
	public Connection conn = null;
	public static boolean DbOk = false;
	public static ArztBausteine thisClass = null;
	public static String osVersion = null;
	public static String proghome = null;

	public JXFrame jFrame = null;

	public String dieseMaschine = null;	
	public static String dbIpAndName = "jdbc:mysql://192.168.2.2:3306/dbf";
	public static String dbUser = "entwickler";
	public static String dbPassword = "entwickler";
	
	ArztBausteinPanel arztbausteinpanel = null;
	
	public static void main(String[] args) throws OfficeApplicationException {
	
		String prog = java.lang.System.getProperty("user.dir");
		osVersion = System.getProperty("os.name");
		if(osVersion.contains("Linux")){
			proghome = "/opt/RehaVerwaltung/";
		}else if(osVersion.contains("Windows")){
			proghome = prog.substring(0, 2)+"/RehaVerwaltung/";
		}else if(osVersion.contains("OSX")){
			
		}
		if(args.length > 0){
			System.out.println("hole daten aus INI-Datei "+args[0]);
			INIFile ini = new INIFile(args[0]);
			dbIpAndName = ini.getStringProperty("DatenBank","DBKontakt1");
			dbUser = ini.getStringProperty("DatenBank","DBBenutzer1");
			String pw = ini.getStringProperty("DatenBank","DBPasswort1");
			String decrypted = null;
			if(pw != null){
				Verschluesseln man = Verschluesseln.getInstance();
				man.init(Verschluesseln.getPassword().toCharArray(), man.getSalt(), man.getIterations());
				decrypted = man.decrypt (pw);
			}else{
				decrypted = new String("");
			}
			dbPassword = decrypted.toString();
			OpenOfficePfad = ini.getStringProperty("OpenOffice.org","OfficePfad");
			OpenOfficeNativePfad = ini.getStringProperty("OpenOffice.org","OfficeNativePfad");
		}
		
		starteOfficeApplication();
		ArztBausteine arztbaustein = new ArztBausteine();
		arztbaustein.getJFrame(args);

	}
	
	public JXFrame getJFrame(String[] args){
		if (jFrame == null) {
			jFrame = new JXFrame();
			thisClass = this;
			try {
				dieseMaschine = java.net.InetAddress.getLocalHost().toString();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					try {
						starteDB();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					return null;
				}
				
			}.execute();

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
			

			jFrame.addWindowListener(this);
			jFrame.addWindowStateListener(this);
			jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jFrame.setPreferredSize(new Dimension(1024,800));

			jFrame.setTitle("Bausteine für ärztlichen Entlassbericht anlegen / ändern");
			

			jFrame.getContentPane().setPreferredSize(new Dimension(1024,800));
			jFrame.getContentPane().setLayout(new GridLayout());
			jFrame.getContentPane().add ( (arztbausteinpanel=new ArztBausteinPanel()));
//			jFrame.validate();
//			

			jFrame.setVisible(true);

			jFrame.pack();
						
		}	
		return jFrame;
	}

	
	
	@Override
	public void windowActivated(WindowEvent arg0) {
	}
	@Override
	public void windowClosed(WindowEvent arg0) {
		if(arztbausteinpanel.document != null){
			arztbausteinpanel.document.close();
			arztbausteinpanel.document = null;
		}
		if(conn != null){
			try {
				conn.close();
				System.out.println("Datenbankverbindung wurde geschlossen-2");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.exit(0);
		
	}
	@Override
	public void windowClosing(WindowEvent arg0) {
		if(arztbausteinpanel.document != null){
			arztbausteinpanel.document.close();
			arztbausteinpanel.document = null;
			System.out.println("Dokument wurde geschlossen");
			/*
			try {
				IDocument[] docs = officeapplication.getDocumentService().getCurrentDocuments();
				if(docs.length > 0){
					for(int i = docs.length-1; i >= 0;i--){
						docs[i].close();
						System.out.println("Schliesse Dokument "+i);
					}
				}
			} catch (DocumentException e) {
				e.printStackTrace();
			} catch (OfficeApplicationException e) {
				e.printStackTrace();
			}
			*/
		}
		if(conn != null){
			try {
				conn.close();
				System.out.println("Datenbankverbindung wurde geschlossen");
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
	@Override
	public void windowStateChanged(WindowEvent arg0) {
	}
	
    public static void starteOfficeApplication()
    { 

    	final String OPEN_OFFICE_ORG_PATH = ArztBausteine.OpenOfficePfad;
    	//final String OPEN_OFFICE_ORG_PATH = "C:\\Programme\\OpenOffice.org 2.3";
        try
        {
        	//System.out.println(piHelp.OpenOfficePfad);
        	//System.out.println(piHelp.OfficeNativePfad);
            String path = OPEN_OFFICE_ORG_PATH;
            Map <String, String>config = new HashMap<String, String>();
            config.put(IOfficeApplication.APPLICATION_HOME_KEY, path);
            config.put(IOfficeApplication.APPLICATION_TYPE_KEY, IOfficeApplication.LOCAL_APPLICATION);
            config.put(IOfficeApplication.APPLICATION_HOST_KEY, "localhost");
            //config.put(IOfficeApplication.APPLICATION_TYPE_KEY, IOfficeApplication.LOCAL_APPLICATION);
            System.setProperty(IOfficeApplication.NOA_NATIVE_LIB_PATH,ArztBausteine.OpenOfficeNativePfad);
            officeapplication = OfficeApplicationRuntime.getApplication(config);
            officeapplication.activate();
            officeapplication.getDesktopService().addTerminateListener(new VetoTerminateListener() {
          	  public void queryTermination(ITerminateEvent terminateEvent) {
          	    super.queryTermination(terminateEvent);
          	    try {
          	      IDocument[] docs = officeapplication.getDocumentService().getCurrentDocuments();
          	      if (docs.length == 1) { 
          	        docs[0].close();
          	        ////System.out.println("Letztes Dokument wurde geschlossen");
          	      }else{
          	    	System.out.println("Es sind noch "+docs.length+" Dokumente offen");
          	      }
          	    }
          	    catch (DocumentException e) {
          	    	e.printStackTrace();
          	    } catch (OfficeApplicationException e) {
						e.printStackTrace();
				}
          	  }
          	});

            
            //IFrame frame = Reha.officeapplication.getDesktopService().constructNewOfficeFrame();
            //System.out.println("Open-Office wurde gestartet");
            //System.out.println("Open-Office-Typ: "+officeapplication.getApplicationType());
        }
        catch (OfficeApplicationException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
	
	private void starteDB() throws InstantiationException, IllegalAccessException, ClassNotFoundException{

			final String sDB = "SQL";
			if (conn != null){
				try{
				conn.close();}
				catch(final SQLException e){}
			}
			
			Class.forName("com.mysql.jdbc.Driver").newInstance();


        	try {
   				conn = (Connection) DriverManager.getConnection(dbIpAndName,dbUser,dbPassword);
    			DbOk = true;
    			System.out.println("Datenbankkontakt hergestellt");
        	} 
        	catch (final SQLException ex) {
        		System.out.println("SQLException: " + ex.getMessage());
        		System.out.println("SQLState: " + ex.getSQLState());
        		System.out.println("VendorError: " + ex.getErrorCode());
        		DbOk = false;
		        
        	}
        	return;
	}		


}
