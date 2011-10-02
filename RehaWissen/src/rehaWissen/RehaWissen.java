package rehaWissen;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;

import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;

import rtaWissen.BrowserFenster;







public class RehaWissen implements WindowListener, KeyListener {
	static JFrame jDiag = null;
	JXPanel contentPanel = null;
	static JLabel standDerDingelbl = null;
	public static RehaWissen thisClass = null;
	static boolean socketoffen = false;
	public static String proghome;
	public boolean HilfeDbOk;
	public Connection hilfeConn = null;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RehaWissen application = new RehaWissen();
		String prog = java.lang.System.getProperty("user.dir");
		if(System.getProperty("os.name").contains("Linux")){
			proghome = "/opt/RehaVerwaltung/";
		}else if(System.getProperty("os.name").contains("Windows")){
			proghome = prog.substring(0, 2)+"/RehaVerwaltung/";
		}else if(System.getProperty("os.name").contains("Mac")){
			proghome = "/opt/RehaVerwaltung/";			
		}
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
		SystemConfig.InetSeitenEinlesen();
		//System.setProperty("nativeswing.webbrowser.runtime","C:/Programme/Mozilla Firefox/");
		//System.setProperty("nativeswing.webbrowser.runtime","C:/RehaVerwaltung/xulrunner.xulrunner.exe");
		//System.setProperty("nativeswing.webbrowser.xulrunner.home",RehaWissen.proghome+"xulrunner/xulrunner.exe");
		//System.setProperty("nativeswing.webbrowser.xulrunner.home",RehaWissen.proghome+"xulrunner/xulrunner.exe");
		//System.setProperty("nativeswing.webbrowser.xulrunner.home",RehaWissen.proghome+"xulrunner/");
		//System.out.println("DBTreiber = " +SystemConfig.hmHilfeServer.get("HilfeDBTreiber"));
		//JWebBrowser.useXULRunnerRuntime();
		NativeInterface.initialize();
		JWebBrowser.useXULRunnerRuntime();
		NativeInterface.open();
		NativeInterface.runEventPump();
		final RehaWissen xapplication = application;
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				System.out.println("ProgHome = "+proghome);
				jDiag = xapplication.getDialog();

				jDiag.validate();
				jDiag.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				jDiag.pack();
				//jDiag.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				jDiag.setExtendedState(JXFrame.MAXIMIZED_BOTH);
				jDiag.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				jDiag.setVisible(true);				
			}
		});
		


		
		
	}
	private JFrame getDialog() {
		thisClass = this;
		new HilfeDatenbankStarten().StarteDB();
		long zeit = System.currentTimeMillis();
		while(!HilfeDbOk){
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if((System.currentTimeMillis()-zeit) > 2000){
				break;
			}
		}
		if(!HilfeDbOk){
			//System.exit(0);
		}
		return new BrowserFenster(null,"RehaWissen");
		/*
		contentPanel = new JXPanel(new BorderLayout());
		contentPanel.setPreferredSize(new Dimension(400,300));
		contentPanel.add(new JXPanel(),BorderLayout.CENTER);
		JXPanel textPanel = new JXPanel(new BorderLayout());
		textPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		textPanel.setBackground(Color.WHITE);
		textPanel.setPreferredSize(new Dimension(0,15));
		standDerDingelbl = new JLabel("OpenSource-Projekt Reha-xSwing");
		standDerDingelbl.setFont(new Font("Arial", 8, 10));
		textPanel.add(standDerDingelbl,BorderLayout.WEST);
		contentPanel.add(textPanel,BorderLayout.SOUTH);
		contentPanel.validate();

		JDialog xDiag = new JDialog();
		xDiag.setCursor(new Cursor(Cursor.WAIT_CURSOR));		
		xDiag.setUndecorated(false);
		xDiag.setContentPane(contentPanel);
		xDiag.setSize(450, 200);
		xDiag.setLocationRelativeTo(null);
		xDiag.addWindowListener(this);
		xDiag.addKeyListener(this);
		xDiag.pack();
		return xDiag;
			*/
	}
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void windowClosing(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	

		
	
	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	

}
final class HilfeDatenbankStarten implements Runnable{

	public void run(){
		final RehaWissen obj = RehaWissen.thisClass;

		final String sDB = "SQL";
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
			RehaWissen.thisClass.hilfeConn =  DriverManager.getConnection("jdbc:jpmdbc:http://www.thera-pi.org/jpmdbc.php?db336243054",connProperties);
			
			//Date zeit = new Date();
			//String stx = "Insert into eingeloggt set comp='"+"Preis-Listen "+java.net.InetAddress.getLocalHost()+": import"+"', zeit='"+zeit.toString()+"', einaus='ein';";
			//sqlAusfuehren(stx);
			
    	} 
    	catch (final SQLException ex) {
    		System.out.println("SQLException-1: " + ex.getMessage());
    		System.out.println("SQLState-1: " + ex.getSQLState());
    		System.out.println("VendorError-1: " + ex.getErrorCode());
    		JOptionPane.showMessageDialog(null,"Fehler: Datenbankkontakt zum Preislisten-Server konnte nicht hergestellt werden.");
    		return;
    	}
    	return;
    	/*
		if (obj.hilfeConn != null){
			try{
			obj.hilfeConn.close();}
			catch(final SQLException e){}
		}
		try{
				
				Class.forName(SystemConfig.hmHilfeServer.get("HilfeDBTreiber")).newInstance();
				RehaWissen.thisClass.HilfeDbOk = true; 
    	}
	    	catch ( final Exception e ){
        		System.out.println(sDB+"-Treiberfehler: " + e.getMessage());
        		RehaWissen.thisClass.HilfeDbOk = false;
	    		return ;
	        }	
	        	try {
	        		RehaWissen.thisClass.hilfeConn = 
	        			(Connection) DriverManager.getConnection(SystemConfig.hmHilfeServer.get("HilfeDBLogin"),
	        					SystemConfig.hmHilfeServer.get("HilfeDBUser"),SystemConfig.hmHilfeServer.get("HilfeDBPassword"));
	        	} 
	        	catch (final SQLException ex) {
	        		System.out.println("SQLException: " + ex.getMessage());
	        		System.out.println("SQLState: " + ex.getSQLState());
	        		System.out.println("VendorError: " + ex.getErrorCode());
	        		RehaWissen.thisClass.HilfeDbOk = false;
	        		return;
	        	}
	        System.out.println("HilfeServer wurde - gestartet");	
	        return;
	       */ 
	}
	public void StarteDB() {
		run();
	}
}
