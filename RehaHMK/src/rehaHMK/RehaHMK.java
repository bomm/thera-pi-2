package rehaHMK;





import java.awt.Color;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.LinearGradientPaint;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Point2D;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;



import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.application.OfficeApplicationRuntime;
import ag.ion.bion.officelayer.document.DocumentException;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.event.ITerminateEvent;
import ag.ion.bion.officelayer.event.VetoTerminateListener;

import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;







import CommonTools.Colors;
import CommonTools.INIFile;
import CommonTools.SqlInfo;
import CommonTools.StartOOApplication;
import CommonTools.Verschluesseln;
import RehaIO.RehaIOMessages;
import RehaIO.RehaReverseServer;
import RehaIO.SocketClient;

public class RehaHMK implements WindowListener {
	public static boolean DbOk;
	JFrame jFrame;
	public static JFrame thisFrame = null;
	public Connection conn;
	public static RehaHMK thisClass;
	public SqlInfo sqlInfo = null;
	
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

	public static int xport = -1;
	public static boolean xportOk = false;
	public RehaReverseServer rehaReverseServer = null;
	public static int rehaReversePort = 6000;

	public static String dbIpAndName = "jdbc:mysql://192.168.2.3:3306/rtadaten";
	public static String dbUser = "rtauser";
	public static String dbPassword = "rtacurie";
	public static String officeProgrammPfad = "C:/Program Files (x86)/OpenOffice.org 3";
	public static String officeNativePfad = "C:/RehaVerwaltung/Libraries/lib/openofficeorg/";
	public static String progHome = "C:/RehaVerwaltung/";
	public static String aktIK = "510841109";
	public static CompoundPainter<Object> cp = null;
	public static CompoundPainter<Object> cparzt = null;
	public static CompoundPainter<Object> cpscanner = null;
	public static boolean testcase = false;
	
	public static HashMap<String,Integer> pgReferenz = new HashMap<String,Integer>(); 
	public static HashMap<String,ImageIcon> icons = new HashMap<String,ImageIcon>();
	public static HashMap<String,String> hmAdrADaten = new HashMap<String,String>();
	public static IOfficeApplication officeapplication;
	
	public static String[] arztGruppen=null;
	public static String hmkURL = null;
	//public static StartOOApplication ooStart = null;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RehaHMK application = new RehaHMK();
		
		application.getInstance();
		application.getInstance().sqlInfo = new SqlInfo();
		
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
				
				INIFile hmrinif = new INIFile(RehaHMK.progHome+"ini/"+RehaHMK.aktIK+"/hmrmodul.ini");
				hmkURL = hmrinif.getStringProperty("HMRModul","HMKUrl");
				
				if(args.length >= 3){
					rehaReversePort = Integer.parseInt(args[2]);
				}
				
				if(args.length >= 4){
					if(args[3].equals("info")){
						String meldung = "Pfad zur rehajava.ini = "+args[0]+"ini/"+args[1]+"/rehajava.ini\n"+
						"Aktuell verwendetes IK = "+aktIK+"\n"+
						"Connection-String = "+dbIpAndName+"\n"+
						"DB-Benutzername = "+dbUser+"\n"+
						"DB-Passwort = "+dbPassword+"\n"+
						"Pfad zu OpenOffice = "+officeProgrammPfad+"\n"+
						"Pfad zur NativeView.dll = "+officeNativePfad+"\n";
						JOptionPane.showMessageDialog(null, meldung);
					}
				}

			}

			cp = null;
			MattePainter mp = null;
			LinearGradientPaint p = null;
			/*****************/
			Point2D start = new Point2D.Float(0, 0);
			Point2D end = new Point2D.Float(150,800);
		    float[] dist = {0.0f, 0.75f};
		    Color[] colors = {Color.WHITE,Color.LIGHT_GRAY};
		    p =  new LinearGradientPaint(start, end, dist, colors);
		    mp = new MattePainter(p);
		    cp = new CompoundPainter<Object>(mp);
		    start = new Point2D.Float(0, 0);
		    end = new Point2D.Float(0,400);
		    dist = new  float[] {0.0f, 0.75f};
		    colors = new Color[] {Color.WHITE,Colors.TaskPaneBlau.alpha(0.45f)};
		    p =  new LinearGradientPaint(start, end, dist, colors);
		    mp = new MattePainter(p);
		    cparzt = new CompoundPainter<Object>(mp);
		    start = new Point2D.Float(0, 0);
		    end = new Point2D.Float(0,40);
		    dist = new float[] {0.0f, 1.00f};
		    colors = new Color[] {Colors.PiOrange.alpha(0.5f),Color.WHITE};	     
		    p = new LinearGradientPaint(start, end, dist, colors);
		    mp = new MattePainter(p);
		    cpscanner = new CompoundPainter<Object>(mp);
			
			NativeInterface.initialize();
			JWebBrowser.useXULRunnerRuntime();
			NativeInterface.open();
			NativeInterface.runEventPump();

			final RehaHMK xapplication = application;
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws java.lang.Exception {
					xapplication.starteDB();
					long zeit = System.currentTimeMillis();
					while(! DbOk){
						try {
							Thread.sleep(20);
							if(System.currentTimeMillis()-zeit > 10000){
								break;
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					if(!DbOk){
						JOptionPane.showMessageDialog(null, "Datenbank konnte nicht geöffnet werden!\nReha-Sql kann nicht gestartet werden");	
						System.exit(0);
					}
					try{
						RehaHMK.starteOfficeApplication();
						RehaHMK.ArztGruppenInit();
					}catch(Exception ex){
						ex.printStackTrace();
					}
					return null;
				}
				
			}.execute();
			fuelleReferenz();
			application.getJFrame();
		}else{
			JOptionPane.showMessageDialog(null, "Keine Datenbankparameter übergeben!\nReha-Sql kann nicht gestartet werden");
			System.exit(0);
			
		}
		
	}
	public static void ArztGruppenInit(){
		INIFile inif = new INIFile(RehaHMK.progHome+"ini/"+RehaHMK.aktIK+"/arzt.ini");
		int ags;
		if( (ags = inif.getIntegerProperty("ArztGruppen", "AnzahlGruppen")) > 0){
			arztGruppen = new String[ags];
			for(int i = 0; i < ags; i++){
				arztGruppen[i] =  inif.getStringProperty("ArztGruppen", "Gruppe"+Integer.valueOf(i+1).toString());
			}
		}
	}
	public static void fuelleReferenz(){
		String[] diszis = {"Physio","Massage","Ergo","Logo","Podo"};
		INIFile inif = new INIFile(progHome+"ini/"+aktIK+"/pgreferenz.ini");
		for(int i = 0; i < diszis.length;i++){
			pgReferenz.put(diszis[i], inif.getIntegerProperty("HMR_ReferenzPreisGruppe", diszis[i]));
		}
		icons.put("browser",new ImageIcon(RehaHMK.progHome+"icons/internet-web-browser.png"));
		icons.put("key",new ImageIcon(RehaHMK.progHome+"icons/entry_pk.gif"));
		icons.put("lupe",new ImageIcon(RehaHMK.progHome+"icons/mag.png"));
		icons.put("erde",new ImageIcon(RehaHMK.progHome+"icons/earth.gif"));
		icons.put("inaktiv",new ImageIcon(RehaHMK.progHome+"icons/inaktiv.png"));
		icons.put("green",new ImageIcon(RehaHMK.progHome+"icons/green.png"));
		icons.put("rot",new ImageIcon(RehaHMK.progHome+"icons/red.png"));
		Image ico = new ImageIcon(RehaHMK.progHome+"icons/blitz.png").getImage().getScaledInstance(26, 26,Image.SCALE_SMOOTH );
		icons.put("blitz",new ImageIcon(ico));
		icons.put("strauss",new ImageIcon(RehaHMK.progHome+"icons/strauss_150.png"));
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
		jFrame.setSize(1020,675);
		jFrame.setTitle("Thera-Pi  Heilmittelkatalog  [IK: "+aktIK+"] "+"[Server-IP: "+dbIpAndName+"]");
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setLocationRelativeTo(null);
		jFrame.getContentPane().add (new RehaHMKTab());
		jFrame.setVisible(true);
		thisFrame = jFrame;
		try{
			new SocketClient().setzeRehaNachricht(RehaHMK.rehaReversePort,"AppName#RehaHMK#"+Integer.toString(RehaHMK.xport));
			new SocketClient().setzeRehaNachricht(RehaHMK.rehaReversePort,"RehaHMK#"+RehaIOMessages.IS_STARTET);
		}catch(Exception ex){
			JOptionPane.showMessageDialog(null, "Fehler in der Socketkommunikation");
		}
		return jFrame;
	}
		
	/********************/
	
	public RehaHMK getInstance(){
		thisClass = this;
		return this;
	}
	@Override
	public void windowOpened(WindowEvent e) {
	}
	@Override
	public void windowClosing(WindowEvent e) {
		if(RehaHMK.thisClass.conn != null){
			try {
				RehaHMK.thisClass.conn.close();
				System.out.println("Datenbankverbindung wurde geschlossen");
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		if( (RehaHMK.thisClass.rehaReverseServer != null) ){
			try{
				new SocketClient().setzeRehaNachricht(RehaHMK.rehaReversePort,"RehaHMK#"+RehaIOMessages.IS_FINISHED);
				rehaReverseServer.serv.close();
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
		}
		/*
		if( (RehaHMK.officeapplication != null) ){
			try{
				new SocketClient().setzeRehaNachricht(RehaHMK.rehaReversePort,"RehaHMK#"+RehaIOMessages.IS_FINISHED);
				rehaReverseServer.serv.close();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		*/
		
		System.exit(0);
	}
	@Override
	public void windowClosed(WindowEvent e) {
	}
	@Override
	public void windowIconified(WindowEvent e) {
	}
	@Override
	public void windowDeiconified(WindowEvent e) {
	}
	@Override
	public void windowActivated(WindowEvent e) {
	}
	@Override
	public void windowDeactivated(WindowEvent e) {
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
			RehaHMK.thisClass.conn.close();
			RehaHMK.thisClass.conn = null;
		} catch (SQLException e) {
			e.printStackTrace();
		} 			
	}
	
	/**********************************************************
	 * 
	 */
	final class DatenbankStarten implements Runnable{
		private void StarteDB(){
			final RehaHMK obj = RehaHMK.thisClass;

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
        		RehaHMK.DbOk = false;
	    		return ;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
        		System.out.println(sDB+"Treiberfehler: " + e.getMessage());
        		RehaHMK.DbOk = false;
	    		return ;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
        		System.out.println(sDB+"Treiberfehler: " + e.getMessage());
        		RehaHMK.DbOk = false;
	    		return ;
			}	
        	try {
   				obj.conn = (Connection) DriverManager.getConnection(dbIpAndName,dbUser,dbPassword);
				RehaHMK.DbOk = true;
				RehaHMK.thisClass.sqlInfo.setConnection(RehaHMK.thisClass.conn); 
    			System.out.println("Datenbankkontakt hergestellt");
        	} 
        	catch (final SQLException ex) {
        		JOptionPane.showMessageDialog(null,"Fehler im Datenbankkontakt\n"+
        				"Ihr verwendeter Connection-String: "+dbIpAndName+"\n"+
        				"Ihr verwendeter Benutzer-Name: "+dbUser);
        		System.out.println("SQLException: " + ex.getMessage());
        		System.out.println("SQLState: " + ex.getSQLState());
        		System.out.println("VendorError: " + ex.getErrorCode());
        		RehaHMK.DbOk = false;
        
        	}
	        return;
		}
		public void run() {
			StarteDB();
		}
	
	
	}
	
	public static void starteOfficeApplication(){ 
    	File f = new File(RehaHMK.officeProgrammPfad);
    	boolean exists = f.isDirectory();
    	if(!exists){
        	JOptionPane.showMessageDialog(null,"Fehler!!!!!\n\nDer von Ihnen verwendete OpenOffice-Pfad lautet:\n"+
        			RehaHMK.officeProgrammPfad+"\n\n"+
        			"Dieser Pfad existiert: NEIN");
    	}
    	try {
			officeapplication = (IOfficeApplication)new StartOOApplication(RehaHMK.officeProgrammPfad,RehaHMK.officeNativePfad).start(false);
		} catch (OfficeApplicationException e1) {
			e1.printStackTrace();
		}
    }

	
	
	
	
}


