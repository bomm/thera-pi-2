package gBriefe;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;


import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.application.OfficeApplicationRuntime;
import ag.ion.bion.officelayer.document.DocumentException;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.event.ITerminateEvent;
import ag.ion.bion.officelayer.event.VetoTerminateListener;



public class GBriefe implements WindowStateListener, WindowListener, ComponentListener, ContainerListener {
	public static String proghome;
	public static String dbtreiber;
	public static String dblogin;
	public static String dbuser;
	public static String dbpassword;
	public static String aktIK;
	public static String tempvz;
	public static String vorlagenvz;
	public static String hilfeserver;
	public static String hilfeftp;
	public static String hilfeuser;
	public static String hilfepasswd;
	public static String adsconnection;

	public static Connection conn = null;
	public static GBriefe thisClass = null;
	public static JFrame thisFrame = null;
	public JXFrame jFrame = null;

	public static boolean DbOk;
	public static String OpenOfficePfad;
	public static String OfficeNativePfad;
	public static IOfficeApplication officeapplication;
	public static boolean warten = true;
	public JXPanel contpan = null;
	public static RehaSockServer RehaSock = null;

	public static boolean testcase = false;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GBriefe application = new GBriefe();
		String prog = java.lang.System.getProperty("user.dir");
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
		INIFile inif = null;
		if(args.length <= 0 || testcase){
			int ind = prog.indexOf("GeburtstagsBriefe");
			if(ind >= 0){
				prog = prog.substring(0,ind);
			}
			proghome = prog.replace('\\', '/');
			if(!proghome.substring(proghome.length()-1).equals("/")){
				proghome = proghome+'/';
			}
		}else{
			proghome = args[0]; 
		}
		
		/**************************/
		new Thread(){
			public  void run(){
				try {
					//System.out.println("Starte SocketServer");
					RehaSock = new RehaSockServer();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
		/**************************/
		
		
		ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar",proghome+"RehaxSwing.jar");
		try {
			processBuilder.start();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		//System.out.println("starte RehaxSwing");
		int i=0;
		while(warten && i < 50){
		try {
			Thread.sleep(100);
			// System.out.println("In warten");
			i++;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if(args.length > 0 || testcase){
			System.out.println("Programmverzeichnis = "+proghome);
			inif = new INIFile(args[0]+"ini/"+args[1]+"/rehajava.ini");
			dbtreiber = new String(inif.getStringProperty("DatenBank", "DBTreiber1"));
			dblogin = new String(inif.getStringProperty("DatenBank","DBKontakt1"));			
			dbuser = new String(inif.getStringProperty("DatenBank","DBBenutzer1"));
			 
			String pw = new String(inif.getStringProperty("DatenBank","DBPasswort1"));
			Verschluesseln man = Verschluesseln.getInstance();
			man = Verschluesseln.getInstance();
		    man.init(Verschluesseln.getPassword().toCharArray(), man.getSalt(), man.getIterations());
		    String decrypted = man.decrypt (pw);
		    dbpassword = new String(decrypted);
		    tempvz = args[0]+"temp/"+args[1]+"/"; //new String(inif.getStringProperty("GBriefe", "TempVZ"));
		    vorlagenvz = args[0]+"vorlagen/"+args[1]+"/"; //new String(inif.getStringProperty("GBriefe", "VorlagenVZ"));
			inif = new INIFile(args[0]+"ini/"+args[1]+"/rehajava.ini");
			OpenOfficePfad = inif.getStringProperty("OpenOffice.org","OfficePfad");
			OfficeNativePfad = inif.getStringProperty("OpenOffice.org","OfficeNativePfad");
			aktIK = args[1];
		}else{
			System.out.println("Programmverzeichnis = "+proghome);
			inif = new INIFile(proghome+"ini/gbriefe.ini");
			dbtreiber = new String(inif.getStringProperty("GBriefe", "DBTreiber"));
			dblogin = new String(inif.getStringProperty("GBriefe", "DBLogin"));			
			dbuser = new String(inif.getStringProperty("GBriefe", "DBUser"));
			 
			String pw = new String(inif.getStringProperty("GBriefe", "DBPassword"));
			Verschluesseln man = Verschluesseln.getInstance();
			man = Verschluesseln.getInstance();
		    man.init(Verschluesseln.getPassword().toCharArray(), man.getSalt(), man.getIterations());
		    String decrypted = man.decrypt (pw);
		    dbpassword = new String(decrypted);
		    tempvz = new String(inif.getStringProperty("GBriefe", "TempVZ"));
		    vorlagenvz = new String(inif.getStringProperty("GBriefe", "VorlagenVZ"));
		    OpenOfficePfad = new String(inif.getStringProperty("GBriefe", "OOPfad"));
		    OfficeNativePfad = new String(inif.getStringProperty("GBriefe", "OONative"));
		    aktIK = "unbekannt";
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				GBriefe application = new GBriefe();
				application.getJFrame();
				application.thisFrame.setIconImage( Toolkit.getDefaultToolkit().getImage( proghome+"icons/fragezeichen.png" ) );
				 
			}
		});
		new SocketClient().setzeInitStand("Initialisiere Geburtstagsbriefe-Generator");
		
	
	}
	private JXFrame getJFrame() {
		if (jFrame == null) {
			jFrame = new JXFrame();
			
			jFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			thisClass = this;
			thisFrame = jFrame;
			jFrame.setTitle("Geburtstagsbriefe - Generator  [IK: "+aktIK+"] "+"[Server-IP: "+dblogin+"]");
			//jFrame.setTitle("Geburtstagsbriefe - Generator");
			jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jFrame.addWindowListener(this);
			jFrame.addWindowStateListener(this);			
			jFrame.addComponentListener(this);
			jFrame.addContainerListener(this);
			jFrame.setLayout(new BorderLayout());
			contpan = new JXPanel(new BorderLayout());
			contpan.add(new SteuerPanel(),BorderLayout.NORTH);
			starteOfficeApplication();
			JPanel jpan = new JPanel(new GridLayout());
			//jpan.validate();
    		//jpan.setVisible(true);
    		
			contpan.add(jpan,BorderLayout.CENTER);
			jFrame.setContentPane(contpan);
			jFrame.setExtendedState(JXFrame.MAXIMIZED_BOTH);
			jFrame.validate();
			jFrame.pack();
			new OOoPanel(jpan);

			DatenbankStarten db = new DatenbankStarten();
			db.StarteDB();


			jFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			//jFrame.setExtendedState(JXFrame.MAXIMIZED_BOTH);
			
            SwingUtilities.invokeLater(new Runnable(){
            	public  void run(){
            		jFrame.setSize(800,600);
            		jFrame.setExtendedState(JXFrame.MAXIMIZED_BOTH);
            		jFrame.setVisible(true);
            		new SocketClient().setzeInitStand("INITENDE");
       			
            	}
         	});
           
			
		}
		return jFrame;
	}
	@Override
	public void windowStateChanged(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowClosed(WindowEvent arg0) {

			// TODO Auto-generated method stub
			try {
				GBriefe.conn.close();
				System.out.println("Datenbankkontakt - geschlossen");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Programm Exit(0)");
			System.exit(0);
	}

	@Override
	public void windowClosing(WindowEvent arg0) {

		// TODO Auto-generated method stub
		try {
			GBriefe.conn.close();
			System.out.println("Datenbankkontakt - geschlossen");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(OOoPanel.document != null){
			try{	
				OOoPanel.document.close();
			}catch(com.sun.star.lang.DisposedException dex){
				System.exit(1);
			}
			System.out.println("OOWriter Dokument - geschlossen");
		}		
		System.out.println("Programm Exit(0)");
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

    	final String OPEN_OFFICE_ORG_PATH = GBriefe.OpenOfficePfad;
    	//final String OPEN_OFFICE_ORG_PATH = "C:\\Programme\\OpenOffice.org 2.3";
        try
        {
        	System.out.println(GBriefe.OpenOfficePfad);
        	System.out.println(GBriefe.OfficeNativePfad);
            String path = OPEN_OFFICE_ORG_PATH;
            Map <String, String>config = new HashMap<String, String>();
            config.put(IOfficeApplication.APPLICATION_HOME_KEY, path);
            config.put(IOfficeApplication.APPLICATION_TYPE_KEY, IOfficeApplication.LOCAL_APPLICATION);
            config.put(IOfficeApplication.APPLICATION_HOST_KEY, "localhost");
            //config.put(IOfficeApplication.APPLICATION_TYPE_KEY, IOfficeApplication.LOCAL_APPLICATION);
            System.setProperty(IOfficeApplication.NOA_NATIVE_LIB_PATH,GBriefe.OfficeNativePfad);
            officeapplication = OfficeApplicationRuntime.getApplication(config);
            officeapplication.activate();
            //IFrame frame = Reha.officeapplication.getDesktopService().constructNewOfficeFrame();
            System.out.println("Open-Office wurde gestartet");
            System.out.println("Open-Office-Typ: "+officeapplication.getApplicationType());
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
          	    	//Reha.thisClass.messageLabel = new JLabel("OO.org nicht Verf�gbar!!!");
          	    } catch (OfficeApplicationException e) {
						e.printStackTrace();
						//Reha.thisClass.messageLabel = new JLabel("OO.org nicht Verf�gbar!!!");
					}
          	  }
          	});

        }
        catch (OfficeApplicationException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
	

}
/*************************************/
final class DatenbankStarten implements Runnable{

	void StarteDB(){
		final GBriefe obj = GBriefe.thisClass;

		final String sDB = "SQL";
		if (obj.conn != null){
			try{
			obj.conn.close();}
			catch(final SQLException e){}
		}
		try{
			
				//Class.forName("com.extendedsystems.jdbc.advantage.ADSDriver").newInstance();
				System.out.println("new Instace() "+GBriefe.dbtreiber);
				Class.forName(GBriefe.dbtreiber).newInstance();
				GBriefe.DbOk = true;
    	}
	    	catch ( final Exception e ){
        		System.out.println(sDB+"Treiberfehler: " + e.getMessage());
        		GBriefe.DbOk = false;
	    		return ;
	        }	
	        	try {
	        		//GBriefe.conn = (Connection) DriverManager.getConnection("G:\\rta\\dbf"+";TableType=cdx","","");
	        		//GBriefe.conn = (Connection) DriverManager.getConnection(GBriefe.adsconnection+";TableType=cdx","","");
	        		//GBriefe.conn = (Connection) DriverManager.getConnection("jdbc:extendedsystems:advantage://192.168.1.101:2000/programme;TableType=cdx","","");
	        		//GBriefe.conn = (Connection) DriverManager.getConnection("jdbc:extendedsystems:advantage://192.168.2.3:2000/programme;TableType=cdx","","");
	        		//GBriefe.conn = (Connection) DriverManager.getConnection("jdbc:extendedsystems:advantage://192.168.2.3:2000/programme/projekte/rta/dbf;TableType=cdx","","");
	        		
	        		GBriefe.conn = (Connection) DriverManager.getConnection(GBriefe.dblogin,
	        				GBriefe.dbuser,GBriefe.dbpassword);
	        				
	        				
	        	} 
	        	catch (final SQLException ex) {
	        		System.out.println("SQLException: " + ex.getMessage());
	        		System.out.println("SQLState: " + ex.getSQLState());
	        		System.out.println("VendorError: " + ex.getErrorCode());
	        		GBriefe.DbOk = false;
	        		return;
	        	}
	        System.out.println("Datenbank - gestartet = "+GBriefe.dblogin);	
	        return;
	}
	public void run() {
		int i=0;
		StarteDB();
	}
}

class SocketClient {
	String stand = "";
	Socket server = null;
	public void setzeInitStand(String stand){
		this.stand = new String(stand);
		run();
	}
	public void run() {
		try {
			serverStarten();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			String mes = new String(  e.toString());
			//JOptionPane.showMessageDialog(null,  mes);
		}
	}
	private void serverStarten() throws IOException{
		this.server = new Socket("localhost",1234);
		OutputStream output = (OutputStream) server.getOutputStream();
		InputStream input = server.getInputStream();

		byte[] bytes = this.stand.getBytes();

		output.write(bytes);
		output.flush();
		int zahl = input.available();
		if (zahl > 0){
			byte[] lesen = new byte[zahl];
			input.read(lesen);
		}
	
		server.close();
		input.close();
		output.close();
	}
}
/**************************/
class RehaSockServer{
	static ServerSocket serv = null;
	RehaSockServer() throws IOException{
		try {
			serv = new ServerSocket(1235);
			System.out.println("Reha SocketServer gestartet auf Port 1235");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//RehaxSwing.jDiag.dispose();
			return;
		}
		
		Socket client = null;

		while(true){
			try {
				
				client = serv.accept();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
			StringBuffer sb = new StringBuffer();
			InputStream input = client.getInputStream();
			OutputStream output = client.getOutputStream();
			int byteStream;
			String test = "";
			while( (byteStream =  input.read()) > -1){
				//System.out.println("******byteStream Erhalten******  "+byteStream );
				char b = (char)byteStream;
				
				sb.append(b);
			}

			test = new String(sb);
			System.out.println("Socket= "+test);			
			final String xtest = new String(test);

			if(xtest.equals("INITENDE")){
						byte[] schreib = "ok".getBytes();
						output.write(schreib);
						output.flush();
						output.close();
						input.close();
						serv.close();
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}								
						System.out.println("INITENDE-angekommen");
						GBriefe.warten = false;
						break;
			}else{
				/*
				SetzeLabel slb = new SetzeLabel();
				slb.init(new String(new String(xtest)));
				slb.execute();
				*/
			}
			byte[] schreib = "ok".getBytes();
			output.write(schreib);
			output.flush();
			output.close();
			input.close();


		}
		if(serv != null){
			serv.close();
			serv = null;
			System.out.println("Socket wurde geschlossen");
		}else{
			System.out.println("Socket wurde geschlossen");
		}

		return;
	}
}
/*******************************************/