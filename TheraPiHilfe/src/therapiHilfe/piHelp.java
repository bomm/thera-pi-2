package therapiHilfe;









import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXFrame;

import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.application.OfficeApplicationRuntime;
import ag.ion.bion.officelayer.document.DocumentException;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.event.ITerminateEvent;
import ag.ion.bion.officelayer.event.VetoTerminateListener;







public class piHelp implements WindowListener, WindowStateListener, ComponentListener, ContainerListener {
public static String proghome;
public static String dbtreiber;
public static String dblogin;
public static String dbuser;
public static String dbpassword;
public static String tempvz;
public static String hilfeserver;
public static String hilfeftp;
public static String hilfeuser;
public static String hilfepasswd;

public static Connection conn = null;
public static piHelp thisClass = null;
public static JXFrame thisFrame = null;
public JXFrame jFrame = null;

public static boolean DbOk;
public helpFenster hf = null; 
public static String OpenOfficePfad;
public static String OfficeNativePfad;
public static IOfficeApplication officeapplication;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String prog = java.lang.System.getProperty("user.dir");
		if(System.getProperty("os.name").contains("Linux")){
			proghome = "/opt/RehaVerwaltung/";
		}else if(System.getProperty("os.name").contains("Windows")){
			proghome = prog.substring(0, 2)+"/RehaVerwaltung/";
		}else if(System.getProperty("os.name").contains("Mac")){
			proghome = "/opt/RehaVerwaltung/";			
		}

		//System.out.println("ProgHome = "+proghome);
		/************Für die Entwicklung dieses Teil benutzen********/
		//INIFile inif = new INIFile(piHelp.proghome+"pihelp.ini");
		

		INIFile inif = new INIFile(piHelp.proghome+"ini/pihelp.ini");
		// Wird nicht mehr gebraucht, da MySql-Datenbank von 1&1 fest verdrahtet ist
		/*
			dbtreiber = new String(inif.getStringProperty("piHelp", "DBTreiber"));
			dblogin = new String(inif.getStringProperty("piHelp", "DBLogin"));			
			dbuser = new String(inif.getStringProperty("piHelp", "DBUser"));
			 
			String pw = new String(inif.getStringProperty("piHelp", "DBPassword"));
			Verschluesseln man = Verschluesseln.getInstance();
			man = Verschluesseln.getInstance();
		    man.init(Verschluesseln.getPassword().toCharArray(), man.getSalt(), man.getIterations());
		    String decrypted = man.decrypt (pw);
		    dbpassword = new String(decrypted);
		 */   
		    tempvz = new String(inif.getStringProperty("piHelp", "TempVZ"));
		    
		    OpenOfficePfad = new String(inif.getStringProperty("piHelp", "OOPfad"));
		    OfficeNativePfad = new String(inif.getStringProperty("piHelp", "OONative"));
		    starteOfficeApplication();

		    hilfeserver = new String(inif.getStringProperty("piHelp", "HilfeServer"));
		    hilfeftp = new String(inif.getStringProperty("piHelp", "HilfeFTP"));
		    hilfeuser = new String(inif.getStringProperty("piHelp", "HilfeUser"));
		    hilfepasswd = new String(inif.getStringProperty("piHelp", "HilfePasswd")); 

		    
		    try {
				UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InstantiationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (UnsupportedLookAndFeelException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					piHelp application = new piHelp();
					application.getJFrame();
					application.thisFrame.setIconImage( Toolkit.getDefaultToolkit().getImage( proghome+"icons/fragezeichen.png" ) );
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							new WorkerGruppen().execute();
						}
					});
					 
				}
			});

	}
	private JXFrame getJFrame() {
		if (jFrame == null) {
			jFrame = new JXFrame();
			jFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			thisClass = this;
			thisFrame = jFrame;
			jFrame.setTitle("pi-Hilfe - Generator");
			jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jFrame.addWindowListener(this);
			jFrame.addWindowStateListener(this);			
			jFrame.addComponentListener(this);
			jFrame.addContainerListener(this);
			jFrame.setLayout(new BorderLayout());
			hf = new helpFenster();
			jFrame.setContentPane(hf);
			jFrame.pack();
			DatenbankStarten db = new DatenbankStarten();
			db.StarteDB();

			jFrame.setSize(800,600);
			//jFrame.setExtendedState(JXFrame.MAXIMIZED_BOTH);
			
            SwingUtilities.invokeLater(new Runnable(){
            	public  void run(){
            		jFrame.setVisible(true);
            		hf.starteOOO();
            	}
         	});
           
			
		}
		return jFrame;
	}
	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void windowClosed(WindowEvent arg0) {
	}
	@Override
	public void windowClosing(WindowEvent arg0) {
		// TODO Auto-generated method stub
		try {
			piHelp.conn.close();
			//System.out.println("MySQL - geschlossen");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(ooPanel.webdocument != null){
			try{
				if(ooPanel.webdocument.isOpen()){
					ooPanel.webdocument.close();	
				}
			}catch(Exception ex){
				ex.printStackTrace();
				System.exit(1);
			}
			
			//System.out.println("OOWeb Dokument - geschlossen");
		}
		if(ooPanel.document != null){
			try{	
				ooPanel.document.close();
			}catch(com.sun.star.lang.DisposedException dex){
				System.exit(1);
			}
			//System.out.println("OOWriter Dokument - geschlossen");
		}

		
		/*
		try {
			piHelp.officeapplication.deactivate();
			//System.out.println("OpenOffice deaktiviert");			
		} catch (OfficeApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		//System.out.println("Programm Exit(0)");
		
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
	@Override
	public void windowStateChanged(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void componentResized(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void componentShown(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void componentAdded(ContainerEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void componentRemoved(ContainerEvent arg0) {
		// TODO Auto-generated method stub
		
	}	
    public static void starteOfficeApplication()
    { 

    	final String OPEN_OFFICE_ORG_PATH = piHelp.OpenOfficePfad;
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
            System.setProperty(IOfficeApplication.NOA_NATIVE_LIB_PATH,piHelp.OfficeNativePfad);
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


}

final class DatenbankStarten implements Runnable{

	void StarteDB(){
		final piHelp obj = piHelp.thisClass;

		final String sDB = "SQL";
/********************/
		try{
			Class.forName("de.root1.jpmdbc.Driver");
    	}
    	catch ( final Exception e ){
    		JOptionPane.showMessageDialog(null,"Fehler beim Laden des Datenbanktreibers für Preislisten-Server");
    		return;
        }
    	try {
			Properties connProperties = new Properties();
			connProperties.setProperty("user", "dbo336243054");
			connProperties.setProperty("password", "allepreise");
			connProperties.setProperty("host", "db2614.1und1.de");	        			
			connProperties.setProperty("port", "3306");
			connProperties.setProperty("compression","false");
			connProperties.setProperty("NO_DRIVER_INFO", "1");
			piHelp.conn =  DriverManager.getConnection("jdbc:jpmdbc:http://www.thera-pi.org/jpmdbc.php?db336243054",connProperties);
			
			//Date zeit = new Date();
			//String stx = "Insert into eingeloggt set comp='"+"Preis-Listen "+java.net.InetAddress.getLocalHost()+": import"+"', zeit='"+zeit.toString()+"', einaus='ein';";
			//sqlAusfuehren(stx);
			
    	} 
    	catch (final SQLException ex) {
    		//System.out.println("SQLException-1: " + ex.getMessage());
    		//System.out.println("SQLState-1: " + ex.getSQLState());
    		//System.out.println("VendorError-1: " + ex.getErrorCode());
    		JOptionPane.showMessageDialog(null,"Fehler: Datenbankkontakt zum Preislisten-Server konnte nicht hergestellt werden.");
    		return;
    	}
    	return;
		
		
/********************/	
    	/*
		if (obj.conn != null){
			try{
			obj.conn.close();}
			catch(final SQLException e){}
		}
		try{
				Class.forName(piHelp.dbtreiber).newInstance();
				piHelp.DbOk = true;
    	}
	    	catch ( final Exception e ){
        		//System.out.println(sDB+"Treiberfehler: " + e.getMessage());
        		piHelp.DbOk = false;
	    		return ;
	        }	
	        	try {
	    				piHelp.conn = (Connection) DriverManager.getConnection(piHelp.dblogin,
	    						piHelp.dbuser,piHelp.dbpassword);
	        	} 
	        	catch (final SQLException ex) {
	        		//System.out.println("SQLException: " + ex.getMessage());
	        		//System.out.println("SQLState: " + ex.getSQLState());
	        		//System.out.println("VendorError: " + ex.getErrorCode());
	        		piHelp.DbOk = false;
	        		return;
	        	}
	        //System.out.println("MySql - gestartet");	
	        return;
	    */    
	}
	public void run() {
		int i=0;
		StarteDB();
	}
}

final class WorkerGruppen extends SwingWorker<Void,Void>{
	JComboBox jcom;
	public void init(JComboBox jcom){
		this.jcom = jcom;
		//execute();
	}
	public void setEnde(){

	}
	
	protected Void doInBackground() throws Exception {
		String[] combInhalt = holeGruppen();
		//ActionListener[] al = helpFenster.gruppenbox.getActionListeners();
		if(helpFenster.gruppenbox.getItemCount() > 0){
			//helpFenster.gruppenbox.removeActionListener(al[0]);
			helpFenster.gruppenbox.removeAllItems();
		}
		//System.out.println("elemente = "+combInhalt.length);
		for(int i = 0;i < combInhalt.length;i++){
			helpFenster.gruppenbox.addItem(new String(combInhalt[i]));
		}
		//helpFenster.gruppenbox.addActionListener(al[0]);
		//System.out.println("WorkerThread beendet");
		return null;
			
	}
	/************************/
	private String[] holeGruppen(){
		Statement stmtx = null;
		ResultSet rsx = null;
		String[] comboInhalt = null;
				stmtx = null;
				rsx = null;
				//System.out.println("In holeGruppen");
				try {
					stmtx = (Statement) piHelp.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					        ResultSet.CONCUR_UPDATABLE );
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					rsx = stmtx.executeQuery("select count(*) from hgroup");
					rsx.next();
					//System.out.println("Insgesamt Gruppen = "+rsx.getInt(1));
					comboInhalt = new String[rsx.getInt(1)];
					rsx = stmtx.executeQuery("select gruppe from hgroup order by reihenfolge");
					int i = 0;
					while(rsx.next()){
						comboInhalt[i] = rsx.getString(1);
						//System.out.println(rsx.getString(1));
						i++;
					}
				}catch(SQLException e){
					e.printStackTrace();
				}
					if (rsx != null) {
						try {
							rsx.close();
						} catch (SQLException sqlEx) { // ignore }
							rsx = null;
						}
					}	
					if (stmtx != null) {
						try {
							stmtx.close();
						} catch (SQLException sqlEx) { // ignore }
							stmtx = null;
						}
					}
			//System.out.println("Insgesamt Elemente = "+comboInhalt.length);		
			return comboInhalt.clone();
			
			}
}
