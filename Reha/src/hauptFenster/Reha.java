package hauptFenster;

import java.awt.AWTEvent;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LinearGradientPaint;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;
import java.util.TooManyListenersException;
import java.util.Vector;

import javax.media.CannotRealizeException;
import javax.media.CaptureDeviceInfo;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.media.format.YUVFormat;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.TransferHandler;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import krankenKasse.KassenPanel;
import menus.TerminMenu;
import oOorgTools.OOTools;
import ocf.OcKVK;
import opencard.core.service.CardServiceException;
import opencard.core.terminal.CardTerminalException;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXStatusBar;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.border.DropShadowBorder;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;
import org.thera_pi.updates.TestForUpdates;
import org.therapi.reha.patient.LadeProg;
import org.therapi.reha.patient.PatientHauptPanel;

import rechteTools.Rechte;
import rehaInternalFrame.JRehaInternal;
import rehaInternalFrame.OOODesktopManager;
import roogle.RoogleFenster;
import sqlTools.ExUndHop;
import sqlTools.SqlInfo;
import systemEinstellungen.INIFile;
import systemEinstellungen.SystemConfig;
import systemEinstellungen.SystemInit;
import systemEinstellungen.SystemPreislisten;
import systemTools.Colors;
import systemTools.FileTools;
import systemTools.RehaPainters;
import systemTools.RezeptFahnder;
import systemTools.TestePatStamm;
import terminKalender.DatFunk;
import terminKalender.ParameterLaden;
import terminKalender.TerminFenster;
import urlaubBeteiligung.Beteiligung;
import urlaubBeteiligung.Urlaub;
import verkauf.VerkaufTab;
import wecker.Wecker;
import abrechnung.AbrechnungGKV;
import abrechnung.AbrechnungReha;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.application.OfficeApplicationRuntime;
import ag.ion.bion.officelayer.document.DocumentException;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.event.IEvent;
import ag.ion.bion.officelayer.event.IEventListener;
import ag.ion.bion.officelayer.event.ITerminateEvent;
import ag.ion.bion.officelayer.event.VetoTerminateListener;
import anmeldungUmsatz.Anmeldungen;
import anmeldungUmsatz.Umsaetze;
import arztFenster.ArztPanel;
import barKasse.Barkasse;
import benutzerVerwaltung.BenutzerRechte;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.looks.HeaderStyle;
import com.jgoodies.looks.Options;
import com.sun.star.uno.Exception;

import dialoge.RehaSmartDialog;
import dta301.Dta301;
import entlassBerichte.EBerichtPanel;
import events.RehaEvent;
import events.RehaEventClass;
import events.RehaEventListener;
import geraeteInit.BarCodeScanner;

//@SuppressWarnings("unused")

public class Reha implements FocusListener,ComponentListener,ContainerListener,MouseListener,MouseMotionListener,KeyListener,RehaEventListener, WindowListener, WindowStateListener, ActionListener  {

	public PatientHauptPanel patpanel = null;
	public EBerichtPanel eberichtpanel = null;
	public KassenPanel kassenpanel = null;
	public ArztPanel arztpanel = null;
	public TerminFenster terminpanel = null;
	public RoogleFenster rooglepanel = null;
	public AbrechnungGKV abrechnungpanel = null;
	public Anmeldungen anmeldungenpanel = null;
	public Umsaetze umsaetzepanel = null;
	public Beteiligung beteiligungpanel = null;
	public Urlaub urlaubpanel = null;
	public VerkaufTab verkaufpanel = null;
	public Barkasse barkassenpanel = null;
	public AbrechnungReha rehaabrechnungpanel = null;
	public BenutzerRechte benutzerrechtepanel = null;
	public SystemInit systeminitpanel = null;
	public Dta301 dta301panel = null;
	
	public final int patiddiff = 5746;
	private JXFrame jFrame = null;

	private JMenuBar jJMenuBar = null;
	private JMenu fileMenu = null;
	private JMenu stammMenu = null;
	private JMenu abrechnungMenu = null;
	private JMenu statistikMenu = null;
	private JMenu toolsMenu = null;	
	private JMenu bueroMenu = null;	
	private JMenu verkaufMenu = null;	
	private JMenu urlaubMenu = null;
	private JMenu helpMenu = null;
	private JMenuItem exitMenuItem = null;
	private JMenuItem aboutMenuItem = null;
	public JXStatusBar jXStatusBar = null;
	private int dividerLocLR = 0; 
	public JLabel shiftLabel = null;
	public static boolean dividerOk = false;
	public JLabel messageLabel = null;
	public JLabel dbLabel = null;
	public JXPanel versionbar = null;
	public JLabel mousePositionLabel = null;
	public JXPanel jxPinContainer = null;
	public JXPanel jxCopyContainer = null;
	public JLabel copyLabel = null;
	
	public JXPanel jxLinks = null;
	public JXPanel jxRechts = null;
	public JXPanel jxRechtsOben = null;
	public JXPanel jxRechtsUnten = null;	
	public UIFSplitPane jSplitLR = null;
	public UIFSplitPane jSplitRechtsOU = null;
	public JXTitledPanel jxTitelOben = null;
	public JXTitledPanel jxTitelUnten = null;	
	public static Reha thisClass;  //  @jve:decl-index=0:
	public static JXFrame thisFrame;
	
	public JXPanel jInhaltOben = null;
	public JXPanel jInhaltUnten = null;	
	public JXPanel jEventTargetOben = null;
	public JXPanel jEventTargetUnten = null;	
	public JXPanel jContainerOben = null;
	public JXPanel jContainerUnten = null;	
	public JXPanel jLeerOben = null;
	public JXPanel jLeerUnten = null;	

	public boolean initok = false;
	public boolean splashok = false;

	public RehaSmartDialog splash = null;
	
	public Connection conn = null;
	public Connection hilfeConn = null;
	
	public static boolean DbOk = false;
	public static boolean HilfeDbOk = false;
	
	public static String progRechte = "0123";

	public final static String Titel = "Thera-\u03C0";

	public boolean KollegenOk = false;
	public static String aktLookAndFeel = "";
	public static SystemConfig sysConf = null;
	public static IOfficeApplication officeapplication;
	
	public static BarCodeScanner barcodeScanner = null;
		
	public static RehaSockServer RehaSock = null;
	@SuppressWarnings("rawtypes")
	public static CompoundPainter[] RehaPainter = {null,null,null,null,null};
	public Vector<Object> aktiveFenster = new Vector<Object>();
	public static String proghome = "";
	public static String userHome = "";
	public final String NULL_DATE = "  .  .    ";
	public static boolean warten=true;
	public static String aktIK = "000000000";
	public static String aktMandant = "Übungs-Mandant";
	public static String aktUser = "";
	public static String kalMin = "";
	public static String kalMax = "";
	public static String Titel2;
	public static String osVersion = ""; 
	public int vollsichtbar = 0; 
	public JDesktopPane deskrechts = new JDesktopPane();
	public JDesktopPane[] desktops = {null,null,null,null};
	public JDesktopPane desktopUnten = new JDesktopPane();
	public JXPanel jpOben = null;
	public JXPanel jpUnten = null;
	
	public static boolean patientFirstStart = true;
	public static boolean terminFirstStart = true;
	public static boolean kassenFirstStart = true;	
	public static boolean arztFirstStart = true;
	public static boolean iconsOk = false;
	public static ImageIcon rehaBackImg = null;
	public JLabel bunker = null;
	public JProgressBar Rehaprogress = null;
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
	
	
	public GradientPaint gp1 = new GradientPaint(0,0,new Color(112,141,255),0,25,Color.WHITE,true);	
	public GradientPaint gp2 = new GradientPaint(0,0,new Color(112,141,120),0,25,Color.WHITE,true);
	public HashMap<String,CompoundPainter<Object>> compoundPainter = new HashMap<String,CompoundPainter<Object>>();
	/**************************/
	public JXPanel desktop = null;
	public ProgLoader progLoader =null;
	public static boolean demoversion = false;
	public static boolean vollbetrieb = true;

	public static String aktuelleVersion = "V=2012-04-03-DB=";
	
	public static Vector<Vector<Object>> timerVec = new Vector<Vector<Object>>();
	public static Timer fangoTimer = null;
	public static boolean timerLaeuft = false;
	public static boolean timerInBearbeitung = false;
	
	public static java.util.Timer nachrichtenTimer = null;
	public static boolean nachrichtenLaeuft = false;
	public static boolean nachrichtenInBearbeitung = false;
	//final public JProgressBar rehaNachrichtenprogress = new JProgressBar();

	public static boolean updatesBereit = false;
	public static boolean updatesChecken = true;
	public static int toolsDlgRueckgabe = -1;
	
	public RehaIOServer rehaIOServer = null;
	public static int xport = 6000;
	public static boolean isStarted = false;
	public static int divider1 = -1;
	public static int divider2 = -1;
	
	public static int zugabex = 20;
	public static int zugabey = 20;
	
	public OcKVK ocKVK = null;
	
	public CaptureDeviceInfo device = null;
	public MediaLocator ml = null;
	public Player player = null;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) {
		String prog = java.lang.System.getProperty("user.dir");
		String homedir = java.lang.System.getProperty("user.home");
		osVersion = System.getProperty("os.name");
		if(osVersion.contains("Linux")){
			proghome = "/opt/RehaVerwaltung/";
		}else if(osVersion.contains("Windows")){
			proghome = prog.substring(0, 2)+"/RehaVerwaltung/";
		}else if(osVersion.contains("Mac OS X")){
			proghome = homedir+"/RehaVerwaltung/";
		}
		
		//Reha.proghome = "C:/RehaVerwaltung/";
		System.out.println("Programmverzeichnis = "+Reha.proghome);
		
		String javaPfad = java.lang.System.getProperty("java.home").replaceAll("\\\\","/");
		if(args.length > 0){
			String[] split = args[0].split("@");
			aktIK = split[0];
			aktMandant = split[1];
			if(args.length > 1){
				for(int i = 1; i < args.length;i++){
					try{
						aktMandant += " "+args[i];
					}catch(NullPointerException ex){
						aktMandant = split[1];
					}
				}
			}
		}else{
			INIFile inif = new INIFile(Reha.proghome+"ini/mandanten.ini");
			int DefaultMandant = inif.getIntegerProperty("TheraPiMandanten", "DefaultMandant");
			aktIK = inif.getStringProperty("TheraPiMandanten", "MAND-IK"+DefaultMandant);
			aktMandant = inif.getStringProperty("TheraPiMandanten", "MAND-NAME"+DefaultMandant);			
		}
		Titel2 = "  -->  [Mandant: "+aktMandant+"]";
		//System.out.println(Titel2);
		/**************************/
		new Thread(){
			public  void run(){
				try {
					RehaSock = new RehaSockServer();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
		/**************************/
		new Thread(){
			public  void run(){
				Process process;
				try {
					System.out.println("Starte RehaxSwing.jar");
					process = new ProcessBuilder("java", "-jar",proghome+"RehaxSwing.jar").start();
					InputStream is = process.getInputStream();
					
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader br = new BufferedReader(isr);
				       
					while ((br.readLine()) != null) {
						//System.out.println(br.readLine());
					}
					is.close();
					isr.close();
					br.close();
					System.out.println("RehaxSwing beendet");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}		
		}.start();
		int i=0;
		while(warten && i < 50){
		try {
			Thread.sleep(100);
			i++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		new SocketClient().setzeInitStand("Überprüfe Dateisystem");
		File f = null;
		if(osVersion.contains("Windows")){
			f = new File(javaPfad+"/bin/win32com.dll");
			if(! f.exists()){
				new SocketClient().setzeInitStand("Kopiere win32com.dll");
				try {
					FileTools.copyFile(new File(proghome+"Libraries/lib/serial/win32com.dll"),f, 4096, false);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{
				////System.out.println("Systemdateien win32com.dll existiert bereits, kopieren nicht erforderlich");
			}	
		}	
		f = new File(javaPfad+"/lib/ext/comm.jar");
		if(! f.exists()){
			try {
				new SocketClient().setzeInitStand("Kopiere comm.jar");
				FileTools.copyFile(new File(proghome+"Libraries/lib/serial/comm.jar"),f, 4096, false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			////System.out.println("Systemdateien comm.jar existiert bereits, kopieren nicht erforderlich");
		}
		f = new File(javaPfad+"/lib/javax.comm.properties");
		if(! f.exists()){
			try {
				new SocketClient().setzeInitStand("Kopiere javax.comm.properties");
				FileTools.copyFile(new File(proghome+"Libraries/lib/serial/javax.comm.properties"),f, 4096, false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			////System.out.println("Systemdateien javax.comm.properties existiert bereits, kopieren nicht erforderlich");
		}

		new Thread(){
			public void run(){
				new SocketClient().setzeInitStand("System-Icons laden");
				SystemConfig.SystemIconsInit();
				iconsOk = true;
				new SocketClient().setzeInitStand("System-Config initialisieren");
			}
		}.start();

		/*********/
		
		SystemConfig sysConf = new SystemConfig();
		
		setSystemConfig(sysConf);
			
		sysConf.SystemStart(Reha.proghome);

		sysConf.SystemInit(1);

		sysConf.SystemInit(2);

		try {
			UIManager.setLookAndFeel((aktLookAndFeel = SystemConfig.aHauptFenster.get(4)));
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		/***********************/
		Color c = UIManager.getColor("Button.disabledForeground");
		if (c != null) {
			UIManager.put("Button.disabledText", new Color(112,126,106)/*original = Color.BLACK*/);
		}else{
			UIManager.put("Button.disabledText", new Color(112,126,106)/*original = Color.BLACK*/);
			UIManager.put("Button.disabledForeground",new Color(112,126,106)/*original = Color.BLACK*/);
		}
		UIManager.put("ComboBox.disabledForeground", Color.RED);
		
		/***********************/		

		javax.swing.plaf.FontUIResource fontUIDresource = new FontUIResource("Tahoma", Font.PLAIN, 11);
		/*
		Font fon= new Font("Tahoma", Font.PLAIN, 11);
		Attribute[] attr = (Attribute[]) fontUIDresource.getAvailableAttributes();
		Map attrMap = fontUIDresource.getAttributes();
		for(int i2 = 0; i2 < attr.length;i2++){
			System.out.println("Key  = "+attr[i2]);	
			System.out.println("Wert = "+attrMap.get(attr[i2]));
		}
		*/

		//String name = "Tahoma";
		//int size = 10;
		//PLAIN=0, BOLD=1, ITALIC=2
		//Font[] fonts = {new Font(name, 0, size), new Font(name, 1, size),
		//new Font(name, 2, size), new Font(name, 3, size)}; 
		UIDefaults defs = (UIDefaults) UIManager.getLookAndFeelDefaults().clone();

		for(Iterator ii = new HashSet(defs.keySet()).iterator(); ii.hasNext(); ) {   
			Object key = ii.next();
			if(key.equals("FormattedTextField.font")){
				UIManager.put(key, fontUIDresource);
			}
			if(key.equals("TextField.font")){
				UIManager.put(key, fontUIDresource);
			}
			if(key.equals("Label.font")){
				UIManager.put(key, fontUIDresource);
			}
			if(key.equals("Button.font")){
				UIManager.put(key, fontUIDresource);
			}
			if(key.equals("Table.font")){
				UIManager.put(key, fontUIDresource);
			}
			if(key.equals("ComboBox.font")){
				UIManager.put(key, fontUIDresource);
			}
		}
		//new ListUIManagerValues();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Reha application = new Reha();
				rehaBackImg = new ImageIcon(Reha.proghome+"icons/therapieMT1.gif");
				application.getJFrame();
				Reha.thisFrame.setIconImage( Toolkit.getDefaultToolkit().getImage( Reha.proghome+"icons/pi.png" ) );
				
				if(!dividerOk){
					//Reha.thisClass.setDivider(5);
				}
				
				//
				Reha.thisClass.doCompoundPainter();
				Reha.thisClass.starteTimer();
				if(SystemConfig.timerdelay > 0){
					Reha.thisClass.starteNachrichtenTimer();
				}
				
			    SwingUtilities.invokeLater(new Runnable(){
			    	public void run(){
			    		try{
			    			Reha.thisClass.rehaIOServer = new RehaIOServer(6000);
			    			System.out.println("RehaIOServer wurde initialisiert");
							SystemConfig.AktiviereLog();
			    		}catch(NullPointerException ex){
			    			System.out.println("RehaIOServer = null");
			    		}
			    	}
			    });
				
			}
		});

		
	}
	public void setzeInitEnde(){
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws java.lang.Exception {
				new SocketClient().setzeInitStand("INITENDE");
				return null;
			}
		}.execute();
	}
	public void ende()	{
		try {
			Runtime.getRuntime().exec("cmd /c start.bat");
		} catch (IOException e) {
			e.printStackTrace();
		}
		INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/rehajava.ini");
		SystemConfig.UpdateIni(inif, "HauptFenster", "Divider1",(Object)jSplitLR.getDividerLocation(),null );
		SystemConfig.UpdateIni(inif, "HauptFenster", "Divider2",(Object)jSplitRechtsOU.getDividerLocation(),null );
		SystemConfig.UpdateIni(inif, "HauptFenster", "TP1Offen",(Object)(LinkeTaskPane.tp1.isCollapsed() ? "1" : "0"),null );
		SystemConfig.UpdateIni(inif, "HauptFenster", "TP2Offen",(Object)(LinkeTaskPane.tp4.isCollapsed() ? "1" : "0"),null );
		SystemConfig.UpdateIni(inif, "HauptFenster", "TP3Offen",(Object)(LinkeTaskPane.tp3.isCollapsed() ? "1" : "0"),null );
		SystemConfig.UpdateIni(inif, "HauptFenster", "TP4Offen",(Object)(LinkeTaskPane.tp5.isCollapsed() ? "1" : "0"),null );
		SystemConfig.UpdateIni(inif, "HauptFenster", "TP5Offen",(Object)(LinkeTaskPane.tp2.isCollapsed() ? "1" : "0"),null );
		SystemConfig.UpdateIni(inif, "HauptFenster", "TP6Offen",(Object)(LinkeTaskPane.tp6.isCollapsed() ? "1" : "0"),null );
		System.exit(0);
	}
	public void beendeSofort(){
		this.jFrame.removeWindowListener(this);
		if(Reha.thisClass.conn != null){
			try {
				Reha.thisClass.conn.close();
				System.out.println("Datenbankverbindung geschlossen");
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		if(Reha.barcodeScanner != null){
			try{
				BarCodeScanner.serialPort.close();
				Reha.barcodeScanner = null;
				System.out.println("Serielle-Schnittstelle geschlossen");
			}catch(NullPointerException ex){
				
			}
		}
		if(Reha.timerLaeuft){
			Reha.fangoTimer.stop();
			Reha.timerLaeuft = false;
		}
		if(Reha.nachrichtenTimer != null){
			Reha.nachrichtenTimer.cancel();
			Reha.nachrichtenLaeuft = false;
			Reha.nachrichtenTimer = null;
		}
		if(rehaIOServer != null){
			try {
				rehaIOServer.serv.close();
				System.out.println("RehaIO-SocketServer geschlossen");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(SystemConfig.sReaderAktiv.equals("1") && Reha.thisClass.ocKVK != null){
			try{
			Reha.thisClass.ocKVK.TerminalDeaktivieren();
			System.out.println("Card-Terminal deaktiviert");
			}catch(NullPointerException ex){
				
			}
		}
		INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/rehajava.ini");
		SystemConfig.UpdateIni(inif, "HauptFenster", "Divider1",(Object)jSplitLR.getDividerLocation(),null );
		SystemConfig.UpdateIni(inif, "HauptFenster", "Divider2",(Object)jSplitRechtsOU.getDividerLocation(),null );
		SystemConfig.UpdateIni(inif, "HauptFenster", "TP1Offen",(Object)(LinkeTaskPane.tp1.isCollapsed() ? "1" : "0"),null );
		SystemConfig.UpdateIni(inif, "HauptFenster", "TP2Offen",(Object)(LinkeTaskPane.tp4.isCollapsed() ? "1" : "0"),null );
		SystemConfig.UpdateIni(inif, "HauptFenster", "TP3Offen",(Object)(LinkeTaskPane.tp3.isCollapsed() ? "1" : "0"),null );
		SystemConfig.UpdateIni(inif, "HauptFenster", "TP4Offen",(Object)(LinkeTaskPane.tp5.isCollapsed() ? "1" : "0"),null );
		SystemConfig.UpdateIni(inif, "HauptFenster", "TP5Offen",(Object)(LinkeTaskPane.tp2.isCollapsed() ? "1" : "0"),null );
		SystemConfig.UpdateIni(inif, "HauptFenster", "TP6Offen",(Object)(LinkeTaskPane.tp6.isCollapsed() ? "1" : "0"),null );

		System.exit(0);
	}
	
	private void doCompoundPainter(){
		new SwingWorker<Void,Void>(){

			@Override
			protected Void doInBackground() throws Exception {
				CompoundPainter<Object> cp = null;
				MattePainter mp = null;
				LinearGradientPaint p = null;
				/*****************/
				Point2D start = new Point2D.Float(0, 0);
				Point2D end = new Point2D.Float(960,100);
			    float[] dist = {0.0f, 0.75f};
			    Color[] colors = {Color.WHITE,Colors.PiOrange.alpha(0.25f)};
			    p =  new LinearGradientPaint(start, end, dist, colors);
			    mp = new MattePainter(p);
			    cp = new CompoundPainter<Object>(mp);
			    Reha.thisClass.compoundPainter.put("PatNeuanlage",cp);
				/*****************/
				start = new Point2D.Float(0, 0);
			    end = new Point2D.Float(0,100);
			    dist = new float[] {0.0f, 0.75f};
			    colors = new Color[] {Color.WHITE,new Color(231,120,23)};
			    p =       new LinearGradientPaint(start, end, dist, colors);
			    mp = new MattePainter(p);
			    cp = new CompoundPainter<Object>(mp);
			    Reha.thisClass.compoundPainter.put("SuchePanel",cp);
			    /*****************/
				start = new Point2D.Float(0, 0);
			    end = new Point2D.Float(0,15);//vorher 45
			    dist = new float[] {0.0f, 0.75f};
			    colors = new Color[] {Colors.PiOrange.alpha(0.5f),Color.WHITE};
			    p = new LinearGradientPaint(start, end, dist, colors);
			    mp = new MattePainter(p);
			    cp = new CompoundPainter<Object>(mp);
			    Reha.thisClass.compoundPainter.put("ButtonPanel",cp);
			    /*****************/
				start = new Point2D.Float(0, 0);
			    end = new Point2D.Float(0,40);
			    dist = new float[] {0.0f, 1.00f};
			    colors = new Color[] {Colors.PiOrange.alpha(0.5f),Color.WHITE};	     
			    p = new LinearGradientPaint(start, end, dist, colors);
			    mp = new MattePainter(p);
			    cp = new CompoundPainter<Object>(mp);
			    Reha.thisClass.compoundPainter.put("StammDatenPanel",cp);
			    /*****************/
				start = new Point2D.Float(0, 0);
			    end = new Point2D.Float(0,100);
			    dist = new  float[] {0.0f, 0.75f};
			    colors = new  Color[] {Colors.PiOrange.alpha(0.70f),Color.WHITE};
			    p =  new LinearGradientPaint(start, end, dist, colors);
			    mp = new MattePainter(p);
			    cp = new CompoundPainter<Object>(mp);
			    Reha.thisClass.compoundPainter.put("AnredePanel",cp);
			    /*****************/
				start = new Point2D.Float(0, 0);
			    end = new Point2D.Float(0,150);
			    dist = new  float[] {0.0f, 0.75f};
			    colors = new  Color[] {Color.WHITE,Colors.PiOrange.alpha(0.5f)};
			    p = new LinearGradientPaint(start, end, dist, colors);
			    mp = new MattePainter(p);
			    cp = new CompoundPainter<Object>(mp);
			    Reha.thisClass.compoundPainter.put("HauptPanel",cp);
			    /*****************/
				start = new Point2D.Float(0, 0);
			    end = new Point2D.Float(0,150);
			    dist = new float[] {0.0f, 0.75f};
			    colors = new Color[] {Color.WHITE,Colors.PiOrange.alpha(0.5f)};
			    p = new LinearGradientPaint(start, end, dist, colors);
			    mp = new MattePainter(p);
			    cp = new CompoundPainter<Object>(mp);
			    Reha.thisClass.compoundPainter.put("FliessText",cp);
			    /*****************/
			    start = new Point2D.Float(0, 0);
			    end = new Point2D.Float(0,150);
			    dist = new  float[] {0.0f, 0.75f};
			    colors = new Color[] {Color.WHITE,Colors.PiOrange.alpha(0.5f)};
			    p = new LinearGradientPaint(start, end, dist, colors);
			    mp = new MattePainter(p);
			    cp = new CompoundPainter<Object>(mp);
			    Reha.thisClass.compoundPainter.put("getTabs",cp);
			    /*****************/
				start = new Point2D.Float(0, 0);
			    end = new Point2D.Float(0,450);
			    dist = new  float[] {0.0f, 0.75f};
			    colors = new  Color[] {Colors.PiOrange.alpha(0.25f),Color.WHITE};
			    p = new LinearGradientPaint(start, end, dist, colors);
			    mp = new MattePainter(p);
			    cp = new CompoundPainter<Object>(mp);
			    Reha.thisClass.compoundPainter.put("getTabs2",cp);
				/*****************/
			    start = new Point2D.Float(0, 0);
			    end = new Point2D.Float(350,290);
			    dist = new  float[] {0.0f, 0.75f};
			    colors = new Color[] {Color.WHITE,Colors.Yellow.alpha(0.05f)};
			    p = new LinearGradientPaint(start, end, dist, colors);
			    mp = new MattePainter(p);
			    cp = new CompoundPainter<Object>(mp);
			    Reha.thisClass.compoundPainter.put("RezeptGebuehren",cp);
			    /*****************/
			    start = new Point2D.Float(0, 0);
			    end = new Point2D.Float(400,550);
			    dist = new float[] {0.0f, 0.75f};
			    colors = new Color[] {Color.WHITE,Colors.Gray.alpha(0.15f)};
			    p =  new LinearGradientPaint(start, end, dist, colors);
			    mp = new MattePainter(p);
			    cp = new CompoundPainter<Object>(mp);
			    Reha.thisClass.compoundPainter.put("EBerichtPanel",cp);
			    /*****************/
				start = new Point2D.Float(0, 0);
				end = new Point2D.Float(600,350);
			    dist = new float[] {0.0f, 0.75f};
			    colors = new Color[] {Color.WHITE,Colors.Yellow.alpha(0.25f)};
			    p =  new LinearGradientPaint(start, end, dist, colors);
			    mp = new MattePainter(p);
			    cp = new CompoundPainter<Object>(mp);
			    Reha.thisClass.compoundPainter.put("ArztBericht",cp);
			    /*****************/
				start = new Point2D.Float(0, 0);
			    end = new Point2D.Float(600,750);
			    dist = new  float[] {0.0f, 0.75f};
			    colors = new  Color[] {Color.WHITE,Colors.Yellow.alpha(0.05f)};
			    p =  new LinearGradientPaint(start, end, dist, colors);
			    mp = new MattePainter(p);
			    cp = new CompoundPainter<Object>(mp);
			    Reha.thisClass.compoundPainter.put("RezNeuanlage",cp);
			    /*****************/
				start = new Point2D.Float(0, 0);
			    end = new Point2D.Float(300,100);
			    dist = new  float[] {0.0f, 0.75f};
			    colors = new  Color[] {Color.WHITE,Colors.Gray.alpha(0.05f)};
			    p = new LinearGradientPaint(start, end, dist, colors);
			    mp = new MattePainter(p);
			    cp = new CompoundPainter<Object>(mp);
			    Reha.thisClass.compoundPainter.put("ScannerUtil",cp);
			    /*****************/
				start = new Point2D.Float(0, 0);
			    end = new Point2D.Float(0,400);
			    dist = new  float[] {0.0f, 0.75f};
			    colors = new Color[] {Color.WHITE,Colors.TaskPaneBlau.alpha(0.45f)};
			    p =  new LinearGradientPaint(start, end, dist, colors);
			    mp = new MattePainter(p);
			    cp = new CompoundPainter<Object>(mp);
			    Reha.thisClass.compoundPainter.put("ArztAuswahl",cp);
			    /*****************/
				start = new Point2D.Float(0, 0);
				end = new Point2D.Float(0,400);
			    dist = new float[] {0.0f, 0.75f};
			    colors = new Color[] {Color.WHITE,Colors.Green.alpha(0.45f)};
			    p = new LinearGradientPaint(start, end, dist, colors);
			    mp = new MattePainter(p);
			    cp = new CompoundPainter<Object>(mp);
			    Reha.thisClass.compoundPainter.put("KassenAuswahl",cp);
			    /*****************/
			    start = new Point2D.Float(0, 0);
			    end = new Point2D.Float(900,100);
			    dist = new float[] {0.0f, 0.75f};
			    colors = new Color[] {Color.WHITE,Colors.PiOrange.alpha(0.25f)};
			    p = new LinearGradientPaint(start, end, dist, colors);
			    mp = new MattePainter(p);
			    cp = new CompoundPainter<Object>(mp);
			    Reha.thisClass.compoundPainter.put("KVKRohDaten",cp);
			    /*****************/
				start = new Point2D.Float(0, 0);
			    end = new Point2D.Float(600,550);
			    dist = new  float[] {0.0f, 0.75f};
			    colors = new Color[] {Color.WHITE,Colors.TaskPaneBlau.alpha(0.45f)};
			    p = new LinearGradientPaint(start, end, dist, colors);
			    mp = new MattePainter(p);
			    cp = new CompoundPainter<Object>(mp);
			    Reha.thisClass.compoundPainter.put("ArztPanel",cp);
			    /*****************/
				start = new Point2D.Float(0, 0);
			    end = new Point2D.Float(400,100);
			    dist = new  float[]{0.0f, 0.75f};
			    colors = new Color[] {Color.WHITE,Colors.Blue.alpha(0.15f)};
			    p = new LinearGradientPaint(start, end, dist, colors);
			    mp = new MattePainter(p);
			    cp = new CompoundPainter<Object>(mp);
			    Reha.thisClass.compoundPainter.put("ArztNeuanlage",cp);
			    /*****************/
				start = new Point2D.Float(0, 0);
			    end = new Point2D.Float(400,100);
			    dist = new float[] {0.0f, 0.75f};
			    colors = new Color[] {Color.WHITE,Colors.Green.alpha(0.25f)};
			    p = new LinearGradientPaint(start, end, dist, colors);
			    mp = new MattePainter(p);
			    cp = new CompoundPainter<Object>(mp);
			    Reha.thisClass.compoundPainter.put("KasseNeuanlage",cp);
			    /*****************/
				start = new Point2D.Float(0, 0);
			    end = new Point2D.Float(600,550);
			    dist = new  float[] {0.0f, 0.75f};
			    colors = new Color[] {Color.WHITE,Colors.Green.alpha(0.5f)};
			    p = new LinearGradientPaint(start, end, dist, colors);
			    mp = new MattePainter(p);
			    cp = new CompoundPainter<Object>(mp);
			    Reha.thisClass.compoundPainter.put("KassenPanel",cp);
			    /*****************/			    
				start = new Point2D.Float(0, 0);
			    end = new Point2D.Float(200,120);
			    dist = new  float[] {0.0f, 0.5f};
			    colors = new  Color[] {Colors.TaskPaneBlau.alpha(1.0f), Color.WHITE};
			    p = new LinearGradientPaint(start, end, dist, colors);
			    mp = new MattePainter(p);
			    cp = new CompoundPainter<Object>(mp);
			    Reha.thisClass.compoundPainter.put("SuchenSeite",cp);
			    /*****************/
				start = new Point2D.Float(0, 0);
			    end = new Point2D.Float(300,270);
			    dist = new float[] {0.0f, 0.75f};
			    colors = new Color[] {Color.WHITE,Colors.Gray.alpha(0.15f)};
			    p =  new LinearGradientPaint(start, end, dist, colors);
			    mp = new MattePainter(p);
			    cp = new CompoundPainter<Object>(mp);
			    Reha.thisClass.compoundPainter.put("GutachtenWahl",cp);
			    /*****************/
				start = new Point2D.Float(0, 0);
			    end = new Point2D.Float(900,100);
			    dist = new  float[] {0.0f, 0.75f};
			    colors = new  Color[] {Color.WHITE,Colors.Yellow.alpha(0.05f)};
			    p = new LinearGradientPaint(start, end, dist, colors);
			    mp = new MattePainter(p);
			    cp = new CompoundPainter<Object>(mp);
			    Reha.thisClass.compoundPainter.put("VorBerichte",cp);			    		
			    /*****************/
				start = new Point2D.Float(0, 0);
			    end = new Point2D.Float(0,600);
			    dist = new float[] {0.0f, 0.75f};
			    colors = new Color[] {Colors.Yellow.alpha(0.15f),Color.WHITE};
			    p = new LinearGradientPaint(start, end, dist, colors);
			    mp = new MattePainter(p);
			    cp = new CompoundPainter<Object>(mp);
			    Reha.thisClass.compoundPainter.put("TextBlock",cp);			    		
			    /*****************/			    
				start = new Point2D.Float(0, 0);
			    end = new Point2D.Float(200,120);
			    dist = new  float[] {0.0f, 0.5f};
			    colors = new  Color[] {Colors.TaskPaneBlau.alpha(1.0f), Color.WHITE};
			    p = new LinearGradientPaint(start, end, dist, colors);
			    mp = new MattePainter(p);
			    cp = new CompoundPainter<Object>(mp);
			    Reha.thisClass.compoundPainter.put("TagWahlNeu",cp);			    		
			    /*****************/			    
				start = new Point2D.Float(0, 0);
			    end = new Point2D.Float(390,180);
			    dist = new  float[] {0.0f, 0.5f};
			    colors = new  Color[] {Colors.TaskPaneBlau.alpha(1.0f), Color.WHITE};
			    p = new LinearGradientPaint(start, end, dist, colors);
			    mp = new MattePainter(p);
			    cp = new CompoundPainter<Object>(mp);
			    Reha.thisClass.compoundPainter.put("Zeitfenster",cp);			    		
			    /*****************/
				start = new Point2D.Float(0, 0);
			    end = new Point2D.Float(400,500);
			    dist = new float[] {0.0f, 0.5f};
			    colors = new Color[] {Color.WHITE,Colors.Gray.alpha(0.15f)};
			    p =  new LinearGradientPaint(start, end, dist, colors);
			    mp = new MattePainter(p);
			    cp = new CompoundPainter<Object>(mp);
			    Reha.thisClass.compoundPainter.put("SystemInit",cp);

			    /*****************/			    
			    progLoader = new ProgLoader();
				return null;
			}
			
		}.execute();

		
	}
	/***************************************/	
	private void starteNachrichtenTimer(){
			Reha.nachrichtenTimer = new java.util.Timer();
			TimerTask task = new TimerTask() {
				public void run() {
					if(!nachrichtenInBearbeitung){
						//nur wenn das Nachrichtentool nich läuft
						if(!RehaIOServer.rehaMailIsActive){
							nachrichtenInBearbeitung = true;
							/**************/
								if( (!Reha.aktUser.equals("")) && (checkForMails()) ){
									nachrichtenRegeln();
								}
							/*************/	
						}	
						nachrichtenInBearbeitung = false;
					}
				}
			};
			//start des Timers:
			Reha.nachrichtenTimer.scheduleAtFixedRate(task, SystemConfig.timerdelay, SystemConfig.timerdelay);
	}
	public static void nachrichtenRegeln(){
		//System.out.println(Reha.aktUser);
		boolean newmail = checkForMails();
		if((!Reha.aktUser.trim().startsWith("Therapeut")) && RehaIOServer.rehaMailIsActive && newmail){
			new ReverseSocket().setzeRehaNachricht(RehaIOServer.rehaMailreversePort, "Reha#"+RehaIOMessages.MUST_CHANGEUSER+"#"+Reha.aktUser);
			new ReverseSocket().setzeRehaNachricht(RehaIOServer.rehaMailreversePort,"Reha#"+RehaIOMessages.MUST_GOTOFRONT);
		}else if((!Reha.aktUser.trim().startsWith("Therapeut")) && RehaIOServer.rehaMailIsActive && (!newmail)){
			new ReverseSocket().setzeRehaNachricht(RehaIOServer.rehaMailreversePort, "Reha#"+RehaIOMessages.MUST_CHANGEUSER+"#"+Reha.aktUser);			
		}else{
			if((!Reha.aktUser.trim().startsWith("Therapeut")) && Reha.checkForMails()){
				new LadeProg(Reha.proghome+"RehaMail.jar"+" "+Reha.proghome+" "+Reha.aktIK+" "+Reha.xport+" "+Reha.aktUser.replace(" ", "#"));
			}
		}
	}
	public static boolean checkForMails(){
		if(!SqlInfo.holeEinzelFeld("select gelesen from pimail where empfaenger_person ='"+
				Reha.aktUser+"' and gelesen='F' LIMIT 1").trim().equals("") ) {
			return true;
		}
		return false;
	}
	/***************************************/
	public void aktiviereNaechsten(int welchen){
		JInternalFrame[] frame = desktops[welchen].getAllFrames();
		if(frame.length > 0){
			for(int i = 0; i < frame.length ;i++){
				////System.out.println("InternalFrames übrig = "+frame[i].getTitle());
				((JRehaInternal)frame[i]).toFront();
				((JRehaInternal)frame[i]).setActive(true);
				((JRehaInternal)frame[i]).getContent().requestFocus();
				if(i==0){
					break;					
				}
			}
		}else{
			if(welchen==0){
				frame = desktops[1].getAllFrames();
				for(int i = 0; i < frame.length;i++){
					((JRehaInternal)frame[i]).toFront();
					((JRehaInternal)frame[i]).setActive(true);
					((JRehaInternal)frame[i]).getContent().requestFocus();
					ProgLoader.containerHandling(1);
					if(i==0){
						break;					
					}
				}
			}else{
				frame = desktops[0].getAllFrames();
				for(int i = 0; i < frame.length;i++){
					((JRehaInternal)frame[i]).toFront();
					((JRehaInternal)frame[i]).setActive(true);
					((JRehaInternal)frame[i]).getContent().requestFocus();
					ProgLoader.containerHandling(0);
					if(i==0){
						break;					
					}
				}
			}
		}
		
	}
	
	public void aktiviereNachNamen(String winname){
		
	}
	public void aktiviereNachWinnum(int winnum){
		
	}	
	public void setzeDivider(){
		
	}

	@SuppressWarnings("rawtypes")
	private JXFrame getJFrame() {
		if (jFrame == null) {
			jFrame = new JXFrame();/*{
				
				private static final long serialVersionUID = 1L;

				//@Override
				public void setVisible(final boolean visible) {
					if(!isStarted){return;}
					if(getState()!=JFrame.NORMAL) { setState(JFrame.NORMAL); }
					
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

				//@Override
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
			};*/	
			thisClass = this;
			jFrame.addWindowListener(this);
			jFrame.addWindowStateListener(this);			
			jFrame.addComponentListener(this);
			jFrame.addContainerListener(this);

			new Thread(new SplashStarten()).start();

			jFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			
			
			JDesktopPane desk = new JDesktopPane();
			desk.setName("desk");
			desk.setOpaque(false);
			jFrame.setContentPane(desk);
			
			/*******/
			jFrame.setTitle(Titel+Titel2);
			jFrame.setJMenuBar(getJJMenuBar());
			jFrame.setStatusBar(getJXStatusBar());

			Reha.RehaPainter[0] = RehaPainters.getBlauPainter();
			Reha.RehaPainter[1] = RehaPainters.getSchwarzGradientPainter();
			Reha.RehaPainter[2] = RehaPainters.getBlauGradientPainter() ;

			/**
			 * Zuerste die Panels für die linke und rechte Seite erstellen,
			 * dann die Splitpane generieren und die Panels L+R übergeben
			 * 
			 */
			jxLinks = new JXPanel(new BorderLayout());
			jxLinks.setDoubleBuffered(true);
			jxLinks.setName("LinkesGrundpanel");
			jxLinks.setBorder(null);
			jxLinks.setBackground(Color.WHITE);

			jxRechts = new JXPanel(new BorderLayout());
			jxRechts.setDoubleBuffered(true);
			jxRechts.setName("RechtesGrundpanel");
			jxRechts.setBackground(Color.WHITE);
			jxRechts.setBorder(BorderFactory.createEmptyBorder(5,0,5,5));

			jSplitLR =  UIFSplitPane.createStrippedSplitPane(JSplitPane.HORIZONTAL_SPLIT,
	        		jxRechts,
	        		jxLinks);
			jSplitLR.setBackground(Color.WHITE);
			jSplitLR.setDividerSize(7);
			jSplitLR.addPropertyChangeListener(new PropertyChangeListener(){
				@Override
				public void propertyChange(PropertyChangeEvent arg0) {
					dividerLocLR = jSplitLR.getDividerLocation();
				}
			});
			
			jSplitLR.setDividerBorderVisible(false);
			jSplitLR.setName("GrundSplitLinksRechts");
			jSplitLR.setOneTouchExpandable(true);
			jSplitLR.setDividerLocation(Toolkit.getDefaultToolkit().getScreenSize().width-250);
			((BasicSplitPaneUI) jSplitLR.getUI()).getDivider().setBackground(Color.WHITE);

			desktop = new JXPanel(new BorderLayout());
			desktop.add(jSplitLR,BorderLayout.CENTER);
			desktop.setSize(2500,2500);

			jFrame.getContentPane().add(desktop);
			jFrame.getContentPane().addComponentListener(this);
			
			/********* den BackgroundPainter basteln *********/
			Point2D start = new Point2D.Float(0, 0);
			Point2D end = new Point2D.Float(800,500);
			float[] dist = {0.2f, 0.7f, 1.0f};
			Color[] colors = {Colors.TaskPaneBlau.alpha(1.0f), Color.WHITE, Colors.TaskPaneBlau.alpha(1.0f)};
			LinearGradientPaint p =
		         new LinearGradientPaint(start, end, dist, colors);
		     MattePainter mp = new MattePainter(p);

		     DropShadowBorder dropShadow = new DropShadowBorder(Color.BLACK, 10, 1, 5, false, true, true, true);

			/**
			 * Jetzt die Panels für die rechte Seite oben und unten erstellen,
			 * dann die Splitpane generieren und die Panels O+U  übergeben.
			 */
			jxRechtsOben = new JXPanel(new BorderLayout());
			jxRechtsOben.setDoubleBuffered(true);
			jxRechtsOben.setPreferredSize(new Dimension(0,250));
			jxRechtsOben.setName("RechtsOben");
			jxRechtsOben.setBorder(null);
			jxRechtsOben.setBackground(Color.WHITE);
			JXPanel jp2 = new JXPanel(new BorderLayout());
			jp2.setBackground(Color.WHITE);
			jp2.setBorder(dropShadow);
			//***

			jpOben = new JXPanel(new BorderLayout());
			jpOben.setBorder(null);
			jpOben.setBackgroundPainter(new CompoundPainter(mp));
			jpOben.setName("PanelOben");
			jpOben.addComponentListener(this);
		    
			desktops[0] = new Hintergrund(Reha.rehaBackImg);
			desktops[0].setName("DesktopOben");
			desktops[0].setOpaque(false);
			desktops[0].setSize(2000,2000);
			desktops[0].setDesktopManager(new OOODesktopManager(0));
			desktops[0].addFocusListener(this);
			desktops[0].addMouseListener(this);
			desktops[0].addMouseMotionListener(this);
			desktops[0].addComponentListener(this);
			desktops[0].addContainerListener(this);	
			
			jpOben.add(desktops[0]);		

		    jp2.add(jpOben,BorderLayout.CENTER);
		    jxRechtsOben.add(jp2,BorderLayout.CENTER);
			jxRechtsOben.validate();
			jxRechtsOben.updateUI();

			
			/*********************/
			jxRechtsUnten = new JXPanel(new BorderLayout());
			jxRechtsUnten.setDoubleBuffered(true);
			jxRechtsUnten.setPreferredSize(new Dimension(0,250));
			jxRechtsUnten.setName("RechtsUnten");
			jxRechtsUnten.setBorder(null);
			jxRechtsUnten.setBackground(Color.WHITE);

			jp2 = new JXPanel(new BorderLayout());
			jp2.setBackground(Color.WHITE);
			jp2.setBorder(dropShadow);
			jp2.addComponentListener(this);

		    jpUnten = new JXPanel(new BorderLayout());
			jpUnten.setBorder(null);
			jpUnten.setBackgroundPainter(new CompoundPainter(mp));
			jpUnten.setName("PanelUnten");
			jpUnten.addComponentListener(this);
			
			desktops[1] = new Hintergrund(Reha.rehaBackImg);
			desktops[1].setName("DesktopUnten");
			desktops[1].setOpaque(false);
			desktops[1].setSize(2000,2000);
			desktops[1].setDesktopManager(new OOODesktopManager(1));
			desktops[1].addFocusListener(this);
			desktops[1].addMouseListener(this);
			desktops[1].addMouseMotionListener(this);
			desktops[1].addComponentListener(this);
			desktops[1].addContainerListener(this);
			
			//desktops[1].add(new WorkFlow("WorkFlow",null,1));
			
		    jpUnten.add(desktops[1]);
		    jp2.add(jpUnten,BorderLayout.CENTER);
		    jxRechtsUnten.add(jp2,BorderLayout.CENTER);
			jxRechtsUnten.validate();
			jxRechtsUnten.updateUI();
			/********************************/
			
			if(SystemConfig.desktopHorizontal){
				jSplitRechtsOU = UIFSplitPane.createStrippedSplitPane(JSplitPane.VERTICAL_SPLIT,
			             jxRechtsOben,
			             jxRechtsUnten);
			}else{
				jSplitRechtsOU = UIFSplitPane.createStrippedSplitPane(JSplitPane.HORIZONTAL_SPLIT,
			             jxRechtsOben,
			             jxRechtsUnten);
				
			}
			jSplitRechtsOU.addPropertyChangeListener(new PropertyChangeListener(){
				@Override
				public void propertyChange(PropertyChangeEvent arg0) {
					//dividerLocOU = jSplitRechtsOU.getDividerLocation();
				}
			});

			jSplitRechtsOU.setDividerBorderVisible(false);
			jSplitRechtsOU.setDividerSize(7);
			((BasicSplitPaneUI) jSplitRechtsOU.getUI()).getDivider().setBackground(Color.WHITE);
			
			jSplitRechtsOU.setBackground(Color.WHITE);
			jSplitRechtsOU.setName("RechtsSplitObenUnten");
			jSplitRechtsOU.setOneTouchExpandable(true);
			jxRechts.add(jSplitRechtsOU,BorderLayout.CENTER); //bislang o.k.

			
			jxRechts.addComponentListener(this);
			jxRechts.validate();
			
			/**
			 * Jetzt erstellen wir die TaskPanes der linken Seite
			 */
			new  Thread(){
				public void run(){
					int i=0;
					while((!iconsOk) && (i < 50)){
					try {
						Thread.sleep(100);
						i++;
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					jxLinks.add(new LinkeTaskPane(),BorderLayout.CENTER);
					jxLinks.validate();
					jFrame.getContentPane().validate();
				}
			}.start();
			
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws java.lang.Exception {
					try{
						INIFile updateini = new INIFile(Reha.proghome+"ini/tpupdate.ini");
						try{
							Reha.updatesChecken = (updateini.getIntegerProperty("TheraPiUpdates", "UpdateChecken") > 0 ? true : false);
						}catch(NullPointerException ex){
							Reha.updatesChecken = true;
						}
						if(!Reha.updatesChecken){
							return null;
						}
						TestForUpdates tfupd = null;
						tfupd = new TestForUpdates();
						Reha.updatesBereit = tfupd.doFtpTest();
						if(Reha.updatesBereit){
							JOptionPane.showMessageDialog(null, "<html><b><font color='aa0000'>Es existieren Updates für Thera-Pi 1.0.</font></b><br><br>Bitte gehen Sie auf die Seite<br><br><b>System-Initialisierung -> 'Software-Updateservice'</b></html>");	
						}
					}catch(NullPointerException ex){
						StackTraceElement[] element = ex.getStackTrace();
						String cmd = "";
						for(int i = 0; i < element.length;i++){
							cmd = cmd+element[i]+"\n";
						}
						JOptionPane.showMessageDialog(null, "Suche nach Updates fehlgeschlagen!\nIst die Internetverbindung o.k.");
					}
					return null;
				}
				
			}.execute();
		}
		
		
		thisFrame = jFrame;
		
		jxLinks.setAlpha(0.3f);
		jxRechts.setAlpha(0.3f);
		
		new Thread(new DatenbankStarten()).start();

		jFrame.setExtendedState(JXFrame.MAXIMIZED_BOTH);

		setKeyboardActions();
		setFocusWatcher();
 
	    RehaEventClass rehaEvent = new RehaEventClass();
	    rehaEvent.addRehaEventListener(this);
	    AktiveFenster.Init();
	    
	    
		
	    /*
	    rehaEvent.addRehaEventListener(new RehaEventListener() {
			@Override
			public void RehaEventOccurred(RehaEvent evt) {
				//System.out.println("Event getSource: = "+evt.getSource());
				//System.out.println("Event Nachricht: = "+ evt.getRehaEvent());				
			}
	    });
		*/

	    return jFrame;
	}
	public static void setSystemConfig(SystemConfig sysConf){
		Reha.sysConf = sysConf;
	}

	private JXStatusBar getJXStatusBar() {
		if (jXStatusBar == null) {
			UIManager.put("Separator.foreground", new Color(231,120,23) );

			jXStatusBar = new JXStatusBar();
			
			jXStatusBar.putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.BOTH);
			jXStatusBar.putClientProperty(Options.NO_CONTENT_BORDER_KEY,Boolean.TRUE );			
			jXStatusBar.putClientProperty(Options.HI_RES_GRAY_FILTER_ENABLED_KEY,Boolean.FALSE );			
			
			jXStatusBar.setPreferredSize(new Dimension(1280, 30));
			jXStatusBar.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
			jXStatusBar.setLayout(new BorderLayout());
			
			FormLayout sblay = new FormLayout("10dlu,fill:0:grow(0.16),4dlu,fill:0:grow(0.16),4dlu,fill:0:grow(0.16),4dlu,fill:0:grow(0.16),4dlu,fill:0:grow(0.16),4dlu,fill:0:grow(0.16),10dlu",
												"fill:0:grow(0.5),18px,fill:0:grow(0.5)");
			CellConstraints sbcc = new CellConstraints();
			JXPanel sbkomplett = new JXPanel();
			sbkomplett.setBorder(BorderFactory.createEmptyBorder(1,0,1,0));
			sbkomplett.setOpaque(false);
			sbkomplett.setLayout(sblay);
			
			/*************1 Container*****************************/
			JXPanel bar = new JXPanel(new BorderLayout());
			bar.setOpaque(false);
			bar.setBorder(BorderFactory.createLoweredBevelBorder());
			JXPanel versionbar = new JXPanel(new BorderLayout());
			versionbar.setOpaque(false);
			versionbar.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
			dbLabel = new JLabel(" ");
			//JLabel lab = new JLabel("Benutzer: Admin");
			dbLabel.setVerticalAlignment(JLabel.CENTER);
			dbLabel.setHorizontalAlignment(JLabel.LEFT);
			versionbar.add(dbLabel);
			bar.add(versionbar);
			sbkomplett.add(bar,sbcc.xy(2, 2));

			/*************2 Container*****************************/

			FlowLayout flay = new FlowLayout(FlowLayout.LEFT);
			flay.setVgap(1);
			jxPinContainer = new JXPanel(flay);
			jxPinContainer.setBorder(null);
			jxPinContainer.setOpaque(false);
			bar = new JXPanel(new BorderLayout());
			bar.setOpaque(false);
			bar.setBorder(BorderFactory.createLoweredBevelBorder());
			JXPanel bar2 = new JXPanel(new BorderLayout());
			bar2.setOpaque(false);
			bar2.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
			messageLabel = new JLabel("starte OpenOffice.org");
			messageLabel.setForeground(Color.RED);
			messageLabel.setVerticalAlignment(SwingConstants.CENTER);
			messageLabel.setHorizontalAlignment(SwingConstants.LEFT);
			jxPinContainer.add(messageLabel);
			bar2.add(jxPinContainer);
			bar.add(bar2);
			sbkomplett.add(bar,sbcc.xy(4, 2));
			
			/**************3 Container****************************/
			
			bar = new JXPanel(new BorderLayout());
			bar.setOpaque(false);
			bar.setBorder(BorderFactory.createLoweredBevelBorder());
			bar2 = new JXPanel(new BorderLayout());
			bar2.setOpaque(false);
			bar2.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
			mousePositionLabel = new javax.swing.JLabel("Druckliste = leer");
	        mousePositionLabel.setHorizontalAlignment(SwingConstants.LEFT);
	        mousePositionLabel.setVerticalAlignment(SwingConstants.CENTER);
	        bar2.add(mousePositionLabel);
	        bar.add(bar2);
	        sbkomplett.add(bar,sbcc.xy(6, 2));

			/**************4 Container****************************/
	        
			bar = new JXPanel(new BorderLayout());
			bar.setOpaque(false);
			bar.setBorder(BorderFactory.createLoweredBevelBorder());
			bar2 = new JXPanel(new BorderLayout());
			bar2.setOpaque(false);
			bar2.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
	        Rehaprogress = new JProgressBar();
	        Rehaprogress.setOpaque(false);
	        Rehaprogress.setIndeterminate(true);
	        Rehaprogress.setForeground(Color.RED);
	        Rehaprogress.setBorder(null);
	        Rehaprogress.setBorderPainted(false);

	        bar2.add(Rehaprogress);
	        bar.add(bar2);
	        sbkomplett.add(bar,sbcc.xy(8, 2));
	        
			/***************5 Container***************************/
	        
			bar = new JXPanel(new BorderLayout());
			bar.setOpaque(false);
			bar.setBorder(BorderFactory.createLoweredBevelBorder());
			bar2 = new JXPanel(new BorderLayout());
			bar2.setOpaque(false);
			bar2.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));

	        shiftLabel = new JLabel("Standard User");
	        shiftLabel.setForeground(Color.RED);
	        shiftLabel.setVerticalAlignment(SwingConstants.CENTER);
	        shiftLabel.setHorizontalAlignment(SwingConstants.LEFT);
	        shiftLabel.setForeground(Color.RED);
	        bar2.add(shiftLabel,BorderLayout.WEST);
	        bar.add(bar2);
	        sbkomplett.add(bar,sbcc.xy(10,2));

			/******************************************/

			bar = new JXPanel(new BorderLayout());
			bar.setOpaque(false);
			bar.setBorder(BorderFactory.createLoweredBevelBorder());
			bar2 = new JXPanel(new BorderLayout());
			bar2.setOpaque(false);
			bar2.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
	        copyLabel = new JLabel("");
	        copyLabel.setHorizontalAlignment(SwingConstants.LEFT);
	        copyLabel.setVerticalAlignment(SwingConstants.CENTER);
	        new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					while(! iconsOk){
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					copyLabel.setIcon(SystemConfig.hmSysIcons.get("bunker"));
					return null;
				}
	        }.execute();
			DropTarget dndt = new DropTarget();
			DropTargetListener dropTargetListener =
				 new DropTargetListener() {
				  public void dragEnter(DropTargetDragEvent e) {}
				  public void dragExit(DropTargetEvent e) {}
				  public void dragOver(DropTargetDragEvent e) {}
				  public void drop(DropTargetDropEvent e) {
					  String mitgebracht = "";
				    try {
				      Transferable tr = e.getTransferable();
				      DataFlavor[] flavors = tr.getTransferDataFlavors();
				      for (int i = 0; i < flavors.length; i++){
				        	mitgebracht  = String.valueOf((String) tr.getTransferData(flavors[i]).toString());
				      }
				      if(mitgebracht.indexOf("°") >= 0){
			    		  String[] labs = mitgebracht.split("°");
				    	  if(labs[0].contains("TERMDAT")){
				    		  copyLabel.setText(labs[1]+"°"+labs[2]+"°"+labs[3]);
				    		  bunker.setText("TERMDATEXT°"+copyLabel.getText());
				    		  e.dropComplete(true);
				    		  return;
				    	  }else if(labs[0].contains("PATDAT")){
				    		  copyLabel.setText("");
				    		  bunker.setText("");
				    		  e.dropComplete(true);
				    	  }else{
				    		  copyLabel.setText("");
				    		  bunker.setText("");
				    		  e.dropComplete(true);
				    		  return;
				    	  }
				      }
				    } catch (Throwable t) { t.printStackTrace(); }
				    e.dropComplete(true);
				  }
				  public void dropActionChanged(
				         DropTargetDragEvent e) {}
			};
			try {
				dndt.addDropTargetListener(dropTargetListener);
			} catch (TooManyListenersException e1) {
				e1.printStackTrace();
			}
			copyLabel.setDropTarget(dndt);
			
		    final String propertyName = "text";
		    bunker = new JLabel();
		    bunker.setName("bunker");
		    bunker.setTransferHandler(new TransferHandler(propertyName));
		    copyLabel.setTransferHandler(new TransferHandler(propertyName));
		    copyLabel.setName("copyLabel");
		    copyLabel.addMouseListener(new MouseAdapter() {
		        public void mousePressed(MouseEvent evt) {
		        	if(!Rechte.hatRecht(Rechte.Kalender_termindragdrop, false)){
		        		return;
		        	}
		            JComponent comp = (JComponent)evt.getSource();
		            if( ((JLabel)comp).getText().equals("") ){
		            	return;
		            }
		            if(bunker.getText().startsWith("TERMDAT")){
		            	TerminFenster.setDragMode(0);
		            }
		            TransferHandler th = bunker.getTransferHandler();
		            th.exportAsDrag((JComponent) bunker, evt, TransferHandler.COPY);
		        }
		    });
		    bar2.add(copyLabel);
		    bar.add(bar2);
		    sbkomplett.add(bar,sbcc.xy(12,2));
		    sbkomplett.validate();
		    jXStatusBar.add(sbkomplett,BorderLayout.CENTER);
	        jXStatusBar.validate();
	        jXStatusBar.setVisible(true);
	        
		}
		return jXStatusBar;
	}
	public void progressStarten(boolean starten){
		final boolean xstarten = starten;
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				new Thread(){
					public void run(){
						Rehaprogress.setIndeterminate(xstarten);						
					}
				}.start();
				return null;
			}
		}.execute();
	}

	private JMenuBar getJJMenuBar() {

		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
			jJMenuBar.setFont(new Font("Dialog", Font.PLAIN, 12));
			jJMenuBar.putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.BOTH);			
			jJMenuBar.add(getFileMenu());
			jJMenuBar.add(getstammMenu());
			jJMenuBar.add(new TerminMenu().getJMenu());
			jJMenuBar.add(getabrechnungMenu());
			jJMenuBar.add(geturlaubMenu());			
			jJMenuBar.add(getverkaufMenu());
			jJMenuBar.add(getstatistikMenu());			
			jJMenuBar.add(getbueroMenu());			
			jJMenuBar.add(gettoolsMenu());			
			jJMenuBar.add(getHelpMenu());
		}
		return jJMenuBar;
	}

	private JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu();
			fileMenu.setFont(new Font("Dialog", Font.PLAIN, 12));			
			fileMenu.setText("Datei");
			fileMenu.add(getExitMenuItem());
		}
		return fileMenu;
	}

	private JMenu getstammMenu() {
		if (stammMenu == null) {
			stammMenu = new JMenu();
			stammMenu.setFont(new Font("Dialog", Font.PLAIN, 12));			
			stammMenu.setText("Stammdaten");
			JMenuItem men = new JMenuItem("Patienten Rezepte etc.");
			men.setActionCommand("patient");
			men.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, Event.CTRL_MASK, false));
			men.setMnemonic(KeyEvent.VK_P);
			men.addActionListener(this);
			stammMenu.add(men);
			stammMenu.addSeparator();
			men = new JMenuItem("Krankenkassen");
			men.setActionCommand("kasse");
			men.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, Event.CTRL_MASK, false));
			men.setMnemonic(KeyEvent.VK_K);
			men.addActionListener(this);
			stammMenu.add(men);
			stammMenu.addSeparator();
			men = new JMenuItem("Ärzte");
			men.setActionCommand("arzt");
			men.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, Event.CTRL_MASK, false));
			men.setMnemonic(KeyEvent.VK_A);
			men.addActionListener(this);
			stammMenu.add(men);
			
		}
		return stammMenu;
	}
	private JMenu getabrechnungMenu() {
		if (abrechnungMenu == null) {
			abrechnungMenu = new JMenu();
			abrechnungMenu.setFont(new Font("Dialog", Font.PLAIN, 12));			
			abrechnungMenu.setText("Abrechnung");
			JMenuItem men = new JMenuItem("Heilmittel-Abrechnung nach §302 SGB V");
			men.setActionCommand("hmabrechnung");
			men.addActionListener(this);
			abrechnungMenu.add(men);
			abrechnungMenu.addSeparator();
			men = new JMenuItem("Reha-Abrechnung");
			men.setActionCommand("rehaabrechnung");
			men.addActionListener(this);
			abrechnungMenu.add(men);
			abrechnungMenu.addSeparator();
			men = new JMenuItem("Barkasse abrechnen");
			men.setActionCommand("barkasse");
			men.addActionListener(this);
			abrechnungMenu.add(men);
			abrechnungMenu.addSeparator();
			men = new JMenuItem("Anmeldezahlen ermitteln");
			men.setActionCommand("anmeldezahlen");
			men.addActionListener(this);
			abrechnungMenu.add(men);
			men = new JMenuItem("Tagesumsätze ermitteln");
			men.setActionCommand("tagesumsatz");
			men.addActionListener(this);
			abrechnungMenu.add(men);
			abrechnungMenu.addSeparator();
			men = new JMenuItem("Offene Posten / Mahnwesen");
			men.setActionCommand("offeneposten");
			men.addActionListener(this);
			abrechnungMenu.add(men);
			abrechnungMenu.addSeparator();
			men = new JMenuItem("Rezeptgebührrechnung/Ausfallrechnung");
			men.setActionCommand("rgaffaktura");
			men.addActionListener(this);
			abrechnungMenu.add(men);
		}
		return abrechnungMenu;
	}

	private JMenu getstatistikMenu() {
		if (statistikMenu == null) {
			statistikMenu = new JMenu();
			statistikMenu.setFont(new Font("Dialog", Font.PLAIN, 12));			
			statistikMenu.setText("Statistiken");
			JMenuItem men = new JMenuItem("LVA/BfA Statistik");
			men.setActionCommand("lvastatistik");
			men.addActionListener(this);
			statistikMenu.add(men);			
		}
		return statistikMenu;
	}

	private JMenu getbueroMenu() {
		if (bueroMenu == null) {
			bueroMenu = new JMenu();
			bueroMenu.setFont(new Font("Dialog", Font.PLAIN, 12));			
			bueroMenu.setText("Büroprogramme");
		}
		return bueroMenu;
	}

	private JMenu gettoolsMenu() {
		if (toolsMenu == null) {
			toolsMenu = new JMenu();
			toolsMenu.setFont(new Font("Dialog", Font.PLAIN, 12));			
			toolsMenu.setText("Tools");
			JMenuItem men = new JMenuItem("Rezeptnummer suchen");
			men.setActionCommand("rezeptfahnder");
			men.addActionListener(this);
			toolsMenu.add(men);
			toolsMenu.addSeparator();
			men = new JMenuItem("Kassenbuch erstellen");
			men.setActionCommand("kassenbuch");
			men.addActionListener(this);
			toolsMenu.add(men);		
			toolsMenu.addSeparator();
			men = new JMenuItem("Geburtstagsbriefe erstellen");
			men.setActionCommand("geburtstagsbriefe");
			men.addActionListener(this);
			toolsMenu.add(men);		
			toolsMenu.addSeparator();
			men = new JMenuItem("Sql-Modul");
			men.setActionCommand("sqlmodul");
			men.addActionListener(this);
			toolsMenu.add(men);
			toolsMenu.addSeparator();
			men = new JMenuItem("§301 Reha Fall-Steuerung");
			men.setActionCommand("fallsteuerung");
			men.addActionListener(this);
			toolsMenu.add(men);		
			toolsMenu.addSeparator();
			men = new JMenuItem("Work-Flow Manager");
			men.setActionCommand("workflow");
			men.addActionListener(this);
			toolsMenu.add(men);		
			toolsMenu.addSeparator();
			men = new JMenuItem("Heilmittelrichtlinien-Tool");
			men.setActionCommand("hmrsearch");
			men.addActionListener(this);
			toolsMenu.add(men);		


		}
		return toolsMenu;
	}

	private JMenu getverkaufMenu() {
		if (verkaufMenu == null) {
			verkaufMenu = new JMenu();
			verkaufMenu.setFont(new Font("Dialog", Font.PLAIN, 12));			
			verkaufMenu.setText("Verkauf");
			JMenuItem men = new JMenuItem("Verkaufsmodul starten");
			men.setActionCommand("verkauf");
			men.addActionListener(this);
			verkaufMenu.add(men);
		}
		return verkaufMenu;
	}

	private JMenu geturlaubMenu() {
		if (urlaubMenu == null) {
			urlaubMenu = new JMenu();
			urlaubMenu.setFont(new Font("Dialog", Font.PLAIN, 12));			
			urlaubMenu.setText("Urlaub/Überstunden");
			JMenuItem men = new JMenuItem("Urlaub-/Überstunden verwalten");
			men.setActionCommand("urlaub");
			men.addActionListener(this);
			urlaubMenu.add(men);
			urlaubMenu.addSeparator();
			men = new JMenuItem("Umsatzbeteiligung ermitteln");
			men.setActionCommand("umsatzbeteiligung");
			men.addActionListener(this);
			urlaubMenu.add(men);
		}
		return urlaubMenu;
	}

	private JMenu getHelpMenu() {
		if (helpMenu == null) {
			helpMenu = new JMenu();
			helpMenu.setFont(new Font("Dialog", Font.PLAIN, 12));
			helpMenu.setText("Hilfe");
			helpMenu.add(getAboutMenuItem());
		}
		return helpMenu;
	}

	private JMenuItem getExitMenuItem() {
		if (exitMenuItem == null) {
			exitMenuItem = new JMenuItem();
			exitMenuItem.setText("Thera-Pi beenden");
			exitMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Runtime r = Runtime.getRuntime();
				    r.gc();
					if(JOptionPane.showConfirmDialog(null, "thera-\u03C0 wirklich schließen?", "Bitte bestätigen", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION ) {
						if(Reha.DbOk &&  (Reha.thisClass.conn != null) ){
							Date zeit = new Date();
							String stx = "Insert into eingeloggt set comp='"+SystemConfig.dieseMaschine+"', zeit='"+zeit.toString()+"', einaus='aus'";
							SqlInfo.sqlAusfuehren(stx);	
						}
						if(Reha.thisClass.conn != null){
							try {
								Reha.thisClass.conn.close();
								System.out.println("Datenbankverbindung geschlossen");
							} catch (SQLException e1) {
								e1.printStackTrace();
							}
						}
						if(Reha.barcodeScanner != null){
							try{
								BarCodeScanner.serialPort.close();
								Reha.barcodeScanner = null;
								System.out.println("Serielle-Schnittstelle geschlossen");
							}catch(NullPointerException ex){
								
							}
						}
						if(Reha.timerLaeuft){
							Reha.fangoTimer.stop();
							Reha.timerLaeuft = false;
						}
						if(Reha.nachrichtenTimer != null){
							Reha.nachrichtenTimer.cancel();
							Reha.nachrichtenLaeuft = false;
							Reha.nachrichtenTimer = null;
							
						}						
						if(rehaIOServer != null){
							try {
								rehaIOServer.serv.close();
								System.out.println("RehaIO-SocketServer geschlossen");
							} catch (IOException e2) {
								e2.printStackTrace();
							}
						}
						if(SystemConfig.sReaderAktiv.equals("1") && Reha.thisClass.ocKVK != null){
							try{
							Reha.thisClass.ocKVK.TerminalDeaktivieren();
							System.out.println("Card-Terminal deaktiviert");
							}catch(NullPointerException ex){
								
							}
						}

						INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/rehajava.ini");
						SystemConfig.UpdateIni(inif, "HauptFenster", "Divider1",(Object)jSplitLR.getDividerLocation(),null );
						SystemConfig.UpdateIni(inif, "HauptFenster", "Divider2",(Object)jSplitRechtsOU.getDividerLocation(),null );
						SystemConfig.UpdateIni(inif, "HauptFenster", "TP1Offen",(Object)(LinkeTaskPane.tp1.isCollapsed() ? "1" : "0"),null );
						SystemConfig.UpdateIni(inif, "HauptFenster", "TP2Offen",(Object)(LinkeTaskPane.tp4.isCollapsed() ? "1" : "0"),null );
						SystemConfig.UpdateIni(inif, "HauptFenster", "TP3Offen",(Object)(LinkeTaskPane.tp3.isCollapsed() ? "1" : "0"),null );
						SystemConfig.UpdateIni(inif, "HauptFenster", "TP4Offen",(Object)(LinkeTaskPane.tp5.isCollapsed() ? "1" : "0"),null );
						SystemConfig.UpdateIni(inif, "HauptFenster", "TP5Offen",(Object)(LinkeTaskPane.tp2.isCollapsed() ? "1" : "0"),null );
						SystemConfig.UpdateIni(inif, "HauptFenster", "TP6Offen",(Object)(LinkeTaskPane.tp6.isCollapsed() ? "1" : "0"),null );

						System.exit(0);
					}else{
						return;
					}
				    
				}
			});
		}
		return exitMenuItem;
	}

	private JMenuItem getAboutMenuItem() {
		if (aboutMenuItem == null) {
			aboutMenuItem = new JMenuItem();
			aboutMenuItem.setText("About");
		}
		return aboutMenuItem;
	}

	public void setzeUi(String sUI,JScrollPane panel){
	      try {
	    	  SystemConfig.UpdateIni("rehajava.ini","HauptFenster","LookAndFeel",sUI);
	    	  UIManager.setLookAndFeel((aktLookAndFeel = sUI));
	    	  SwingUtilities.updateComponentTreeUI(thisFrame);
	    	  SwingUtilities.updateComponentTreeUI(this.jxRechtsOben);	    	  
	    	  SwingUtilities.updateComponentTreeUI(this.jxRechtsUnten);
	    	  SwingUtilities.updateComponentTreeUI(this.jSplitLR);
	    	  SwingUtilities.updateComponentTreeUI(this.jxLinks);	    	  
	    	  SwingUtilities.updateComponentTreeUI(this.jxRechts);
	    	  LinkeTaskPane.UpdateUI();
			}catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			} catch (InstantiationException e1) {
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			} catch (UnsupportedLookAndFeelException e1) {
				e1.printStackTrace();
			}
	}
	

	
    public static void starteOfficeApplication(){ 

    	final String OPEN_OFFICE_ORG_PATH = SystemConfig.OpenOfficePfad;

        try
        {
        	if(Reha.isStarted){
        		JOptionPane.showMessageDialog(null,"Zur Info die UNO-Runtime wird neu gestartet!");
        	}
        	//System.out.println("**********Open-Office wird gestartet***************");
            String path = OPEN_OFFICE_ORG_PATH;
            Map <String, Object>config = new HashMap<String, Object>();
            config.put(IOfficeApplication.APPLICATION_HOME_KEY, path);
            config.put(IOfficeApplication.APPLICATION_TYPE_KEY, IOfficeApplication.LOCAL_APPLICATION);

            if(path.indexOf("LibreOffice") >= 0){
            	
            	System.out.println("Nehme die neue Variante");
                config.put(IOfficeApplication.APPLICATION_ARGUMENTS_KEY, 
                		new String[] {"--nodefault",
                		"--nofirststartwizard",
                		"--nologo",
                		"--norestore",
                		"--headless"
                		});
                System.out.println("Nehme LibreOffice Configuration ");
                
            }

            
            System.setProperty(IOfficeApplication.NOA_NATIVE_LIB_PATH,SystemConfig.OpenOfficeNativePfad);
            officeapplication = OfficeApplicationRuntime.getApplication(config);
            officeapplication.activate();
            //Nachfolgende try/catch nur dann einschalten
            //wenn Sie über die neueste ag.ion.noa_2.2.3.jar vom 20.06.2011
            //verfügen!!!!! sonst läuft nix mehr /st.

            
            try {
				if(officeapplication.getOfficeConnection() != null){
					System.out.println("OfficeConnection o.k.");
					officeapplication.getOfficeConnection().addBridgeEventListener(new IEventListener(){
						@Override
						public void disposing(IEvent arg0) {
							JOptionPane.showMessageDialog(null,"Bridge zu OpenOffice unterbrochen");
						}
						
					});
				}
			} catch (java.lang.Exception e1) {
				e1.printStackTrace();
			}
			
			
            
            officeapplication.getDesktopService().addTerminateListener(new VetoTerminateListener() {
            	  public void queryTermination(ITerminateEvent terminateEvent) {
            	    super.queryTermination(terminateEvent);
            	    try {
            	      IDocument[] docs = officeapplication.getDocumentService().getCurrentDocuments();
            	      if (docs.length == 1) { 
            	        docs[0].close();
            	      }
            	    }
            	    catch (DocumentException e) {
            	    	e.printStackTrace();
            	    	Reha.thisClass.messageLabel = new JLabel("OO.org nicht Verfügbar!!!");
            	    } catch (OfficeApplicationException e) {
						e.printStackTrace();
						Reha.thisClass.messageLabel = new JLabel("OO.org nicht Verfügbar!!!");
					}
            	  }
            	});
            Reha.thisClass.Rehaprogress.setIndeterminate(false);
            /*
            new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
            		Reha.thisClass.Rehaprogress.setIndeterminate(false);
            	    if(Reha.DbOk){
            	    	for(int i = 1;i<6;i++){
            	    		new MachePreisListe("kgtarif"+i);
            	    		new MachePreisListe("matarif"+i);
            	    		new MachePreisListe("ertarif"+i);
            	    		new MachePreisListe("lotarif"+i);
            	    		new MachePreisListe("rhtarif"+i);
            	    	}
            	    }
            		
            		try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			
					return null;
				}
            }.execute();
            */
        }
        catch (OfficeApplicationException e) {
			Reha.thisClass.messageLabel = new JLabel("OO.org nicht Verfügbar!!!");
            e.printStackTrace();
        }
    }

    private void setKeyboardActions() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        toolkit.addAWTEventListener(new AWTEventListener() {
            public void eventDispatched(AWTEvent event)  {
                if(event instanceof KeyEvent) {
                    KeyEvent keyEvent = (KeyEvent) event;
                    if(progRechte.equals("")){
                    	return;
                    }
                    if(keyEvent.isAltDown() &&
                            keyEvent.getID() == KeyEvent.KEY_PRESSED && keyEvent.getKeyCode()==88) {  // Ctrl-P
                        }
                    if(keyEvent.isControlDown() &&
                       keyEvent.getID() == KeyEvent.KEY_PRESSED && keyEvent.getKeyCode()==80) {  // Ctrl-P
        				new SwingWorker<Void,Void>(){
        					@Override
        					protected Void doInBackground() throws Exception {
        						Reha.thisFrame.setCursor(Reha.thisClass.wartenCursor);
        						Reha.thisFrame.setCursor(Reha.thisClass.normalCursor);
        						return null;
        					}
        				}.execute();
                    }
                    if(keyEvent.isAltDown() && keyEvent.getID()== KeyEvent.KEY_PRESSED 
                    		&& keyEvent.getKeyCode() ==	KeyEvent.VK_R){
                    	new RezeptFahnder(true);
                    	return;
                    	
                    }
                    if(keyEvent.isAltDown() &&
                            keyEvent.getID() == KeyEvent.KEY_PRESSED && keyEvent.getKeyCode()==88) {  // Ctrl-P
                    		Reha.aktUser = "";
                    		SwingUtilities.invokeLater(new Runnable(){
                    			public void run(){
                    				if(RehaIOServer.rehaMailIsActive){
                    					new ReverseSocket().setzeRehaNachricht(RehaIOServer.rehaMailreversePort, "Reha#"+RehaIOMessages.MUST_RESET);
                    				}
                    			}
                    		});
                            ProgLoader.PasswortDialog(0);
                    }

                    if(keyEvent.isControlDown() &&
                            keyEvent.getID() == KeyEvent.KEY_PRESSED && keyEvent.getKeyCode()==84) {  // Ctrl-P
        					JComponent termin = AktiveFenster.getFensterAlle("TerminFenster");
        					if(termin == null){
        						//
        					}else{
        						//ProgLoader.ProgTerminFenster(0,0);//
        					}
                    }
                    if(keyEvent.isControlDown() &&
                            keyEvent.getID() == KeyEvent.KEY_PRESSED && keyEvent.getKeyCode()==79) {  // Ctrl-P
 
                    }
                    if(keyEvent.isControlDown() &&
                    		keyEvent.getID() == KeyEvent.KEY_PRESSED && keyEvent.getKeyCode()==75) {  // Ctrl-K
    					//JComponent kasse = AktiveFenster.getFensterAlle("KrankenKasse");
    					
                    }
                    if(keyEvent.isControlDown() &&
                            keyEvent.getID() == KeyEvent.KEY_PRESSED && keyEvent.getKeyCode()==65) {  // Ctrl-K
    					//JComponent arzt = AktiveFenster.getFensterAlle("ArztVerwaltung");
    					
						Reha.thisFrame.setCursor(Reha.thisClass.wartenCursor);
						Reha.thisClass.progLoader.ArztFenster(0,TestePatStamm.PatStammArztID());
						Reha.thisFrame.setCursor(Reha.thisClass.normalCursor);
                    }
                    
                    if(keyEvent.isControlDown() &&
                            keyEvent.getID() == KeyEvent.KEY_PRESSED && keyEvent.getKeyCode()==37) {  // Ctrl-Pfeil nach links
                    		setDivider(1);
                    		keyEvent.consume();
                    }
                    if(keyEvent.isControlDown() &&
                            keyEvent.getID() == KeyEvent.KEY_PRESSED && keyEvent.getKeyCode()==39) {  // Ctrl-Pfeil nach rechts
                    		setDivider(2);
                    		keyEvent.consume();
                    }
                    if(keyEvent.isControlDown() &&
                            keyEvent.getID() == KeyEvent.KEY_PRESSED && keyEvent.getKeyCode()==38) {  // Ctrl-Pfeil nach oben
                    		setDivider(3);
                    		keyEvent.consume();
                    }
                    if(keyEvent.isControlDown() &&
                            keyEvent.getID() == KeyEvent.KEY_PRESSED && keyEvent.getKeyCode()==40) {  // Ctrl-Pfeil nach unten
                			setDivider(4);
                    		keyEvent.consume();
                    }
                }
            }
        }, AWTEvent.KEY_EVENT_MASK);
        /*
        toolkit.addAWTEventListener(new AWTEventListener() {

			@Override
			public void eventDispatched(AWTEvent event) {
				// TODO Auto-generated method stub
				MouseEvent mouseEvent = (MouseEvent) event;
				if(event instanceof MouseEvent) {
            		//System.out.println("MausEvent = "+event);
            	}
			}
        	
        }, AWTEvent.MOUSE_EVENT_MASK);
        */
    }
    public void setVertDivider(final int variante){
    	//System.out.println("Variante = "+variante);
    	//Diese Funktion wäre etwas für Michael Schütt
    	/*
    	 * Im Grunde würde es genügen wenn Strg+Pfeil-Links/oder Rechts gedrückt wird,
    	 * die Arbeitsflächen entweder hälftig oder voll sichtbar darzustellen
    	 * den Rest müßte man dann einfacht mit der Maus herstellen.
    	 * 
    	 */
    	SwingUtilities.invokeLater(new Runnable(){
    		public void run(){
    			//links
    	    	if(variante==1){
    	    		
    	    	
    	        	if(desktops[0].getWidth() <= 25){
    	        		jSplitRechtsOU.setDividerLocation(0);
    	        		vollsichtbar = 1;
    	        		return;
    	        	}else if(desktops[0].getWidth() > 25){
    	    			jSplitRechtsOU.setDividerLocation((jSplitRechtsOU.getDividerLocation()-25));
    	    			vollsichtbar = -1;
    	    			return;
    	    		}
    	    		
    	        //rechts	
    	    	}else if(variante==2){
    	    		if(desktops[1].getWidth() <= 25){
    	        		jSplitRechtsOU.setDividerLocation(jSplitRechtsOU.getWidth()-7);
    	        		vollsichtbar = 0;
    	        		return;
    	    		}else{
    	    			jSplitRechtsOU.setDividerLocation((jSplitRechtsOU.getDividerLocation()+25));
    	    			vollsichtbar = -1;
    	    			return;
    	    		}
    	    	}
    	    	vollsichtbar = -1;
    			
    		}
    	});
    }
    public void setDivider(int variante){
    	final int xvariante = variante;
    	
    	SwingUtilities.invokeLater(new Runnable(){
      	   public  void run()
      	   {
      		   if(!SystemConfig.desktopHorizontal){
      			   setVertDivider(xvariante);
      			   return;
      		   }
      		   int i;
      		   for(i=0;i<1;i++){
      			   //links
      			   if(xvariante==1){
      				   if(jSplitLR.getDividerLocation()>250){
      					   jSplitLR.setDividerLocation(dividerLocLR-10);
      				   }else{
      					   if(dividerLocLR-10 < 0){
      						   jSplitLR.setDividerLocation(0);        				
      					   }else{
      						   jSplitLR.setDividerLocation(dividerLocLR-10);        				
      					   }
      				   }
      				   break;
      			   }
      			   //rechts
      			   if(xvariante==2){
      				   if(jSplitLR.getDividerLocation()<250){
      					   jSplitLR.setDividerLocation(dividerLocLR+10);
      				   }else{
      					   if(dividerLocLR+10 > thisFrame.getRootPane().getWidth()-7){
      						   jSplitLR.setDividerLocation(thisFrame.getRootPane().getWidth()-7);        				
      					   }else{
      						   jSplitLR.setDividerLocation(dividerLocLR+10);        				
      					   }
      				   }
      				   break;
      			   }
      			   if(xvariante==3){
      				   // nach oben
      				   if(jSplitRechtsOU.getDividerLocation() > (thisFrame.getRootPane().getHeight()/2)-3){
      					   jSplitRechtsOU.setDividerLocation((jxLinks.getHeight()/2)-3);
          				   vollsichtbar = -1;
      				   }else{
      					   jSplitRechtsOU.setDividerLocation(0);
          				   vollsichtbar = 1;
      				   }
      			   }
      			   if(xvariante==4){
      				   // nach unten
      				   if(jSplitRechtsOU.getDividerLocation() < (jxLinks.getHeight()/2)-3 ){
      					   jSplitRechtsOU.setDividerLocation((jxLinks.getHeight()/2)-3);
          				   vollsichtbar = -1;
      				   }else{
      					   jSplitRechtsOU.setDividerLocation(thisFrame.getRootPane().getHeight()-7);
          				   vollsichtbar = 0;
      				   }
      				   break;
      			   }
      			   if(xvariante==5){
      				   // oben Vollbild
      				   vollsichtbar = 0;
      				   jSplitRechtsOU.setDividerLocation(thisFrame.getRootPane().getHeight()-7);                			
      				   break;
      			   }
      			   if(xvariante==6){
      				   // unten Vollbild
      				   vollsichtbar = 1;
  					   jSplitRechtsOU.setDividerLocation(0);                			
      				   break;
      			   }
      			   if(xvariante==7){
      				   vollsichtbar = 1;
  					   jSplitRechtsOU.setDividerLocation(0);
      				   break;
      			   }
      		   }
      	   }
     	});

    }
    public int getSichtbar(){
    	/*
		System.out.println("\n\nDivider-Location = "+jSplitRechtsOU.getDividerLocation());
		System.out.println("Divider-Ok 		 = "+Reha.dividerOk);
		System.out.println("Höhe der RootPane = "+thisFrame.getRootPane().getHeight()+"\n");
		System.out.println("Höhe von Desktop[0] = "+desktops[0].getHeight());
		System.out.println("Höhe von Desktop[1] = "+desktops[1].getHeight());
		System.out.println("Breite von Desktop[0] = "+desktops[0].getWidth());
		System.out.println("Breite von Desktop[1] = "+desktops[1].getWidth());
		*/
		if(SystemConfig.desktopHorizontal){
			if(desktops[0].getHeight() <= 10){
				return 1;
			}else if(desktops[1].getHeight() <= 10){
				return 0;
			}
		}else{
			/*
			if(desktops[0].getWidth() <= 10){
				return 1;
			}else if(desktops[1].getWidth() <= 10){
				return 0;
			}
			*/
		}
		return -1;
    }
    public void setFocusWatcher() {
		long mask = AWTEvent.FOCUS_EVENT_MASK;
		/*
			AWTEvent.ACTION_EVENT_MASK
			| AWTEvent.MOUSE_EVENT_MASK
			| AWTEvent.FOCUS_EVENT_MASK
			| AWTEvent.MOUSE_MOTION_EVENT_MASK
			| AWTEvent.MOUSE_WHEEL_EVENT_MASK
			| AWTEvent.TEXT_EVENT_MASK
			| AWTEvent.WINDOW_EVENT_MASK
			| AWTEvent.WINDOW_FOCUS_EVENT_MASK
			| AWTEvent.WINDOW_STATE_EVENT_MASK
			| AWTEvent.COMPONENT_EVENT_MASK;
		*/
        	Toolkit toolkit = Toolkit.getDefaultToolkit();
        	toolkit.addAWTEventListener(new AWTEventListener(){		

            public void eventDispatched(AWTEvent event)  {
                if(event instanceof FocusEvent) {
                    //FocusEvent focusEvent = (FocusEvent) event;
                }
            }
        }, mask);
	}    

	public void componentHidden(ComponentEvent arg0) {
	}


	public void componentMoved(ComponentEvent arg0) {
	}


	public void componentResized(ComponentEvent arg0) {
		//Größe einstellen
		try{
			if(((JComponent)arg0.getSource()).getName() != null){
				if( ((String)((JComponent)arg0.getSource()).getName()).equals("PanelOben")){
					desktops[0].setBounds(0,0,Reha.thisClass.jpOben.getWidth(),
							Reha.thisClass.jpOben.getHeight());
				}
				if( ((String)((JComponent)arg0.getSource()).getName()).equals("PanelUnten")){
					desktops[1].setBounds(0,0,Reha.thisClass.jpUnten.getWidth(),
							Reha.thisClass.jpUnten.getHeight() );
				}
				JInternalFrame[] frm = Reha.thisClass.desktops[0].getAllFrames();
				for(int i = 0;i< frm.length;i++){
					if(((JRehaInternal)frm[i]).getImmerGross()){
						frm[i].setBounds(2,2,Reha.thisClass.jpOben.getWidth()-2,
									Reha.thisClass.jpOben.getHeight()-2);
					}
					((JRehaInternal)frm[i]).setCompOrder(i);
					((JRehaInternal)frm[i]).setzeIcon();
				}
				frm = Reha.thisClass.desktops[1].getAllFrames();
				for(int i = 0;i< frm.length;i++){
					if(((JRehaInternal)frm[i]).getImmerGross()){
						frm[i].setBounds(2,2,Reha.thisClass.jpUnten.getWidth()-2,
									Reha.thisClass.jpUnten.getHeight()-2);
					}
					((JRehaInternal)frm[i]).setCompOrder(i);					
					((JRehaInternal)frm[i]).setzeIcon();
				}

			}
		}catch(java.lang.ClassCastException cex){
			
		}
		jSplitLR.validate();
		desktop.setBounds(0,0,thisFrame.getContentPane().getWidth(),thisFrame.getContentPane().getHeight());
		desktop.validate();
		jFrame.getContentPane().validate();
		
	}


	/************Motion Event******************/

	public void mouseClicked(MouseEvent arg0) {
	}
	public void mouseEntered(MouseEvent arg0) {
	}
	public void mouseExited(MouseEvent arg0) {
	}
	public void mousePressed(MouseEvent arg0) {
	}
	public void mouseReleased(MouseEvent arg0) {
	}
/************Motion für DragEvent******************/
	public void mouseDragged(MouseEvent arg0) {
	}
	public void mouseMoved(MouseEvent arg0) {
	}
/************KeyListener*************************/
	public void keyPressed(KeyEvent arg0) {
	}
	public void keyReleased(KeyEvent arg0) {
	}
	public void keyTyped(KeyEvent arg0) {
	}

	public void rehaEventOccurred(RehaEvent evt) {
		if(evt.getRehaEvent().equals("PatSuchen")){
		}
	}
	static Component WerHatFocus(){
		final Component focusOwner = null;
		//focusOwner = FocusManager.getCurrentManager.getFocusedWindow();
		return focusOwner;
	}
	public void ladenach(){
		int nochmals = JOptionPane.showConfirmDialog(null,"Die Datenbank konnte nicht gestartet werden, erneuter Versuch?","Wichtige Benuterzinfo",JOptionPane.YES_NO_OPTION);
		if(nochmals == JOptionPane.YES_OPTION){
			new Thread(new DbNachladen()).start();
		}
	}
	public void addSbContainer(String simage,String sname,JComponent jcomponent){
	}
	public void setzeInitStand(String stand){
		new SocketClient().setzeInitStand(stand);
	}
	@Override
	public void windowActivated(WindowEvent arg0) {
		desktop.setBounds(0,0,thisFrame.getContentPane().getWidth(),thisFrame.getContentPane().getHeight());
	}
	@Override
	public void windowClosed(WindowEvent arg0) {
		if(Reha.barcodeScanner != null){
			BarCodeScanner.serialPort.close();
			System.out.println("Serielle Schnittstelle wurde geschlossen");	
		}
	}
	@Override
	public void windowClosing(WindowEvent arg0) {
		if(JOptionPane.showConfirmDialog(null, "thera-\u03C0 wirklich schließen?", "Bitte bestätigen", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION ) {
			if(Reha.DbOk &&  (Reha.thisClass.conn != null) ){
				Date zeit = new Date();
				String stx = "Insert into eingeloggt set comp='"+SystemConfig.dieseMaschine+"', zeit='"+zeit.toString()+"', einaus='aus'";
				SqlInfo.sqlAusfuehren(stx);
			}

			JInternalFrame[] frame = desktops[0].getAllFrames();
			for(int i = 0; i < frame.length;i++){
				frame[i].dispose();
				frame[i] = null;
			}
			frame = desktops[1].getAllFrames();
			for(int i = 0; i < frame.length;i++){
				frame[i].dispose();
				frame[i] = null;
			}
			if(Reha.thisClass.conn != null){
				try {
					Reha.thisClass.conn.close();
					Reha.thisClass.conn = null;
					System.out.println("Datenbankverbindung wurde geschlossen");
				} catch (SQLException e) {
					e.printStackTrace();
					System.exit(0);
				}
			}
			if(Reha.barcodeScanner != null){
				try{
				BarCodeScanner.serialPort.close();
				Reha.barcodeScanner = null;
				System.out.println("Serielle Schnittstelle wurde geschlossen");
				}catch(NullPointerException ex){
					System.exit(0);
				}
			}
			if(Reha.officeapplication != null){
			}
			if(Reha.timerLaeuft){
				Reha.fangoTimer.stop();
				Reha.timerLaeuft = false;
			}
			if(rehaIOServer != null){
				try {
					rehaIOServer.serv.close();
					System.out.println("RehaIO-SocketServer geschlossen");					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(Reha.nachrichtenTimer != null){
				Reha.nachrichtenTimer.cancel();
				Reha.nachrichtenLaeuft = false;
				Reha.nachrichtenTimer = null;
			}
			if(SystemConfig.sReaderAktiv.equals("1") && Reha.thisClass.ocKVK != null){
				try{
				Reha.thisClass.ocKVK.TerminalDeaktivieren();
				System.out.println("Card-Terminal deaktiviert");
				}catch(NullPointerException ex){
					
				}
			}

			
			INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/rehajava.ini");
			SystemConfig.UpdateIni(inif, "HauptFenster", "Divider1",(Object)jSplitLR.getDividerLocation(),null );
			SystemConfig.UpdateIni(inif, "HauptFenster", "Divider2",(Object)jSplitRechtsOU.getDividerLocation(),null );
			SystemConfig.UpdateIni(inif, "HauptFenster", "TP1Offen",(Object)(LinkeTaskPane.tp1.isCollapsed() ? "1" : "0"),null );
			SystemConfig.UpdateIni(inif, "HauptFenster", "TP2Offen",(Object)(LinkeTaskPane.tp4.isCollapsed() ? "1" : "0"),null );
			SystemConfig.UpdateIni(inif, "HauptFenster", "TP3Offen",(Object)(LinkeTaskPane.tp3.isCollapsed() ? "1" : "0"),null );
			SystemConfig.UpdateIni(inif, "HauptFenster", "TP4Offen",(Object)(LinkeTaskPane.tp5.isCollapsed() ? "1" : "0"),null );
			SystemConfig.UpdateIni(inif, "HauptFenster", "TP5Offen",(Object)(LinkeTaskPane.tp2.isCollapsed() ? "1" : "0"),null );
			SystemConfig.UpdateIni(inif, "HauptFenster", "TP6Offen",(Object)(LinkeTaskPane.tp6.isCollapsed() ? "1" : "0"),null );

			System.exit(0);
		}else{
			return;
		}
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
	@Override
	public void focusGained(FocusEvent e) {
	}
	@Override
	public void focusLost(FocusEvent e) {
	}
	@Override
	public void componentShown(ComponentEvent arg0) {
	}
	@Override
	public void componentAdded(ContainerEvent arg0) {
	}
	@Override
	public void componentRemoved(ContainerEvent arg0) {
	}	
/*******************/
class Hintergrund extends JDesktopPane implements ComponentListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ImageIcon hgicon;
	int icx,icy;
	AlphaComposite xac1 = null;
	AlphaComposite xac2 = null;	
	public Hintergrund(ImageIcon icon){
		super();
		
		if(icon != null){
			hgicon = icon;
			icx = hgicon.getIconWidth()/2;
			icy = hgicon.getIconHeight()/2;
			xac1 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.15f); 
			xac2 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f);			
		}else{
			hgicon = null;
		}
		this.setDoubleBuffered(true);
	}
	
	@Override
	public void paintComponent( Graphics g ) { 
		super.paintComponent( g );
		Graphics2D g2d = (Graphics2D)g;
		
		if(hgicon != null){
			g2d.setComposite(this.xac1);
			g2d.drawImage(hgicon.getImage(), (getWidth()/2)-icx , (getHeight()/2)-icy,null);
			g2d.setComposite(this.xac2);
		}
	}
	@Override
	public void componentHidden(ComponentEvent arg0) {
	}
	@Override
	public void componentMoved(ComponentEvent arg0) {
	}
	@Override
	public void componentResized(ComponentEvent arg0) {
		repaint();
	}
	@Override
	public void componentShown(ComponentEvent arg0) {
	}
}
/***************************/
public void starteTimer(){
	Reha.fangoTimer = new Timer(60000,this);
	Reha.fangoTimer.setActionCommand("testeFango");
	Reha.fangoTimer.start();
	Reha.timerLaeuft = true;
}

public static void testeNummernKreis(){
	String cmd = "select mandant from nummern LIMIT 1";
	Vector<Vector<String>> vecnummern = SqlInfo.holeFelder(cmd);
	if(vecnummern.size() <= 0){
		cmd = "insert into nummern set pat='1',kg='1',ma='1',er='1',"+
		"lo='1',rh='1',rnr='1',esol='1',bericht='1',afrnr='1',rgrnr='1',doku='1',"+
		"dfue='1',mandant='"+Reha.aktIK+"'";
		//System.out.println(cmd);
		SqlInfo.sqlAusfuehren(cmd);
	}
}
/**********Actions**********/
@Override
public void actionPerformed(ActionEvent arg0) {
	String cmd = arg0.getActionCommand();
	if(cmd.equals("testeFango")){
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws java.lang.Exception {
				Wecker.testeWecker();
				return null;
			}
		}.execute();
	}
	if(cmd.equals("patient")){
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				progLoader.ProgPatientenVerwaltung(1);
				Reha.thisClass.progressStarten(false);
				return null;
			}
		}.execute();
		return;
	}
	if(cmd.equals("kasse")){
		Reha.thisClass.progLoader.KassenFenster(0,TestePatStamm.PatStammKasseID());
		return;
	}
	if(cmd.equals("arzt")){
		Reha.thisClass.progLoader.ArztFenster(0,TestePatStamm.PatStammArztID());
		return;
	}
	if(cmd.equals("hmabrechnung")){
		Reha.thisClass.progLoader.AbrechnungFenster(1);
		return;
	}
	if(cmd.equals("rehaabrechnung")){
		Reha.thisClass.progLoader.RehaabrechnungFenster(1,"");
		return;
	}
	if(cmd.equals("barkasse")){
		Reha.thisClass.progLoader.BarkassenFenster(1,"");
		return;
	}
	if(cmd.equals("anmeldezahlen")){
		Reha.thisClass.progLoader.AnmeldungenFenster(1,"");
		return;
	}
	if(cmd.equals("tagesumsatz")){
		Reha.thisClass.progLoader.UmsatzFenster(1,"");
		return;
	}
	if(cmd.equals("verkauf")){
		Reha.thisClass.progLoader.VerkaufFenster(1,"");
		return;
	}
	if(cmd.equals("urlaub")){
		if(! Rechte.hatRecht(Rechte.Funktion_urlaubueberstunden, true)){
			return;
		}
		new LadeProg(Reha.proghome+"RehaUrlaub.jar"+" "+Reha.proghome+" "+Reha.aktIK);
		return;
	}
	if(cmd.equals("umsatzbeteiligung")){
		Reha.thisClass.progLoader.BeteiligungFenster(1,"");
		return;
	}
	if(cmd.equals("lvastatistik")){
		new LadeProg(Reha.proghome+"RehaStatistik.jar"+" "+Reha.proghome+" "+Reha.aktIK);
		return;
	}
	/*****************************/
	if(cmd.equals("offeneposten")){
		if(!Rechte.hatRecht(Rechte.Funktion_offeneposten, true)){
			return;
		}
		if(! RehaIOServer.offenePostenIsActive){
			new LadeProg(Reha.proghome+"OffenePosten.jar"+" "+Reha.proghome+" "+Reha.aktIK+" "+Reha.xport);
		}else{
			new ReverseSocket().setzeRehaNachricht(RehaIOServer.offenePostenreversePort,"Reha#"+RehaIOMessages.MUST_GOTOFRONT);
		}
		return;
	}
	/*****************************/
	if(cmd.equals("rezeptfahnder")){
		new RezeptFahnder(true);
		return;
	}
	/*****************************/
	if(cmd.equals("rgaffaktura")){
		if(! Rechte.hatRecht(Rechte.Funktion_barkasse, false)){
			JOptionPane.showMessageDialog(null, "Keine Berechtigung -> Funktion Ausbuchen RGAF-Faktura");
			return;
		}
		if(! RehaIOServer.rgAfIsActive){
			new LadeProg(Reha.proghome+"OpRgaf.jar"+" "+Reha.proghome+" "+Reha.aktIK+" "+Reha.xport);
		}else{
			new ReverseSocket().setzeRehaNachricht(RehaIOServer.rgAfreversePort,"Reha#"+RehaIOMessages.MUST_GOTOFRONT);
		}
		return;
	}
	/*****************************/
	if(cmd.equals("kassenbuch")){
		if(!Rechte.hatRecht(Rechte.Funktion_kassenbuch, true)){
			return;
		}
		new LadeProg(Reha.proghome+"RehaKassenbuch.jar"+" "+Reha.proghome+" "+Reha.aktIK);
		return;
	}
	if(cmd.equals("geburtstagsbriefe")){
		if(!Rechte.hatRecht(Rechte.Sonstiges_geburtstagsbriefe, true)){
			return;
		}
		new LadeProg(Reha.proghome+"GBriefe.jar"+" "+Reha.proghome+" "+Reha.aktIK);
		return;
	}
	/*****************************/
	if(cmd.equals("sqlmodul")){
		if(!Rechte.hatRecht(Rechte.Sonstiges_sqlmodul, true)){
			return;
		}
		if(!RehaIOServer.rehaSqlIsActive){
			new LadeProg(Reha.proghome+"RehaSql.jar"+" "+Reha.proghome+" "+Reha.aktIK+" "+String.valueOf(Integer.toString(Reha.xport)));	
		}else{
			new ReverseSocket().setzeRehaNachricht(RehaIOServer.rehaSqlreversePort,"Reha#"+RehaIOMessages.MUST_GOTOFRONT );			
		}
		
		return;
	}
	/*****************************/
	if(cmd.equals("fallsteuerung")){
		if(!Rechte.hatRecht(Rechte.Sonstiges_Reha301, true)){
			return;
		}
		if(RehaIOServer.reha301IsActive){
			JOptionPane.showMessageDialog(null,"Das 301-er Modul läuft bereits");
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					new ReverseSocket().setzeRehaNachricht(RehaIOServer.reha301reversePort,"Reha301#"+RehaIOMessages.MUST_GOTOFRONT );		
				}
			});
			return;
		}
		new LadeProg(Reha.proghome+"Reha301.jar "+
				" "+Reha.proghome+" "+Reha.aktIK+" "+String.valueOf(Integer.toString(Reha.xport)) );
		//Reha.thisFrame.setCursor(Reha.thisClass.wartenCursor);
		return;
	}
	/*****************************/
	if(cmd.equals("workflow")){
		if(!Rechte.hatRecht(Rechte.Sonstiges_Reha301, true)){
			return;
		}
		if(RehaIOServer.rehaWorkFlowIsActive){
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					new ReverseSocket().setzeRehaNachricht(RehaIOServer.rehaWorkFlowreversePort,"ZeigeFrame#"+RehaIOMessages.MUST_GOTOFRONT );		
				}
			});
			return;
		}
		new LadeProg(Reha.proghome+"WorkFlow.jar "+
				" "+Reha.proghome+" "+Reha.aktIK+" "+String.valueOf(Integer.toString(Reha.xport)) );
		//Reha.thisFrame.setCursor(Reha.thisClass.wartenCursor);
		return;
	}
	if(cmd.equals("hmrsearch")){
		System.out.println("isActive = "+RehaIOServer.rehaHMKIsActive);
		if(RehaIOServer.rehaHMKIsActive){
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					new ReverseSocket().setzeRehaNachricht(RehaIOServer.rehaHMKreversePort,"Reha#"+RehaIOMessages.MUST_GOTOFRONT );		
				}
			});
			return;
		}
		new LadeProg(Reha.proghome+"RehaHMK.jar "+
				" "+Reha.proghome+" "+Reha.aktIK+" "+String.valueOf(Integer.toString(Reha.xport)) );
		//Reha.thisFrame.setCursor(Reha.thisClass.wartenCursor);
		return;
	}
	
	
}
/***************/
public void activateWebCam(){
	
	new SwingWorker<Void,Void>(){
		@SuppressWarnings("rawtypes")
		@Override
		protected Void doInBackground() throws java.lang.Exception {
	
			try{
				try{
		            @SuppressWarnings("unused")
					Class c = Class.forName("javax.media.Manager");
		        }catch (ClassNotFoundException e){
		        	SystemConfig.sWebCamActive = "0";
		        	JOptionPane.showMessageDialog(null, "Java Media Framework (JMF) ist nicht installiert"+
		        			"\nWebCam kann nicht gestartet werden");
		           
		        }
				@SuppressWarnings("unchecked")
				Vector<CaptureDeviceInfo> deviceList = (Vector<CaptureDeviceInfo>)javax.media.cdm.CaptureDeviceManager.getDeviceList(new YUVFormat());
				if(deviceList == null){
					JOptionPane.showMessageDialog(null,"Keine WebCam verfügbar!!");
					SystemConfig.sWebCamActive = "0";
					return null;
				}
				device = (CaptureDeviceInfo) deviceList.firstElement();
				ml = device.getLocator();
				Manager.setHint(Manager.LIGHTWEIGHT_RENDERER, new Boolean(true));
				player = Manager.createRealizedPlayer(ml);
			}catch(NullPointerException ex){
				ex.printStackTrace();
				SystemConfig.sWebCamActive = "0";
			}catch (NoPlayerException e) {
				e.printStackTrace();
				SystemConfig.sWebCamActive = "0";
			} catch (CannotRealizeException e) {
				e.printStackTrace();
				SystemConfig.sWebCamActive = "0";
			} catch (IOException e) {
				e.printStackTrace();
				SystemConfig.sWebCamActive = "0";
			}
			System.out.println("Web-Cam erfolgreich gestartet");
			return null;
			
		}
	}.execute();
}

/*********************************************/
}


/**************
 * 
 * Thread zum Start der Datenbank
 * @author admin
 *
 */
final class DatenbankStarten implements Runnable{
	private void StarteDB(){
		final Reha obj = Reha.thisClass;

		final String sDB = "SQL";
		if (obj.conn != null){
			try{
			obj.conn.close();}
			catch(final SQLException e){}
		}
		try{
			if (sDB=="SQL"){
				new SocketClient().setzeInitStand("Datenbanktreiber installieren");
				Class.forName(SystemConfig.vDatenBank.get(0).get(0)).newInstance();
			}	
    	}
    	catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}	
		try {
			if (sDB=="SQL"){
				//obj.conn = (Connection) DriverManager.getConnection("jdbc:mysql://194.168.1.8:3306/dbf","entwickler","entwickler");
				new SocketClient().setzeInitStand("Datenbank initialisieren und öffnen");
				obj.conn = (Connection) DriverManager.getConnection(SystemConfig.vDatenBank.get(0).get(1)+"?jdbcCompliantTruncation=false",
						SystemConfig.vDatenBank.get(0).get(3),SystemConfig.vDatenBank.get(0).get(4));
				}	
				int nurmaschine = SystemConfig.dieseMaschine.toString().lastIndexOf("/");
				new ExUndHop().setzeStatement("delete from flexlock where maschine like '%"+SystemConfig.dieseMaschine.toString().substring(0, nurmaschine)+"%'");
				if(obj.dbLabel != null){
					String db = SystemConfig.vDatenBank.get(0).get(1).replace("jdbc:mysql://", "");
					db = db.substring(0,db.indexOf("/"));
					obj.dbLabel.setText(Reha.aktuelleVersion+db);
				}
				Reha.DbOk = true;
				Reha.testeNummernKreis();
		}catch (final SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			Reha.DbOk = false;
			int nochmals = JOptionPane.showConfirmDialog(null,"Die Datenbank konnte nicht gestartet werden, erneuter Versuch?","Wichtige Benuterzinfo",JOptionPane.YES_NO_OPTION);
			if(nochmals == JOptionPane.YES_OPTION){
				new Thread(new DbNachladen()).start();
			}else{
				new SocketClient().setzeInitStand("INITENDE");
			}
			return;
		}
		return;
	}
	public void run() {
		int i=0;
		while (!Reha.thisClass.splashok){
			i = i+1;
			if(i>10){
				break;
			}
			try {
				Thread.sleep(300);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		StarteDB();
		if (Reha.DbOk){
			Date zeit = new Date();
			String stx = "Insert into eingeloggt set comp='"+SystemConfig.dieseMaschine+"', zeit='"+zeit.toString()+"', einaus='ein'";
			new ExUndHop().setzeStatement(stx);
			try {
				Thread.sleep(50);
				
				new SocketClient().setzeInitStand("Datenbank starten");

				new SocketClient().setzeInitStand("Datenbank ok");

				ParameterLaden.Init();
				new SocketClient().setzeInitStand("Systemparameter laden");
				
				Reha.thisClass.shiftLabel.setText("Term-User ok!");
				Reha.sysConf.SystemInit(3);
				ParameterLaden.Passwort();
				new SocketClient().setzeInitStand("Systemparameter ok");
				
				Reha.thisClass.shiftLabel.setText("Prog-User ok!");

				new SocketClient().setzeInitStand("Native Interface ok");
				
				Reha.sysConf.SystemInit(4);
				
				new SocketClient().setzeInitStand("Emailparameter");

				Reha.sysConf.SystemInit(6);
				
				new SocketClient().setzeInitStand("Roogle-Gruppen ok!");

				Reha.sysConf.SystemInit(7);
				
				new SocketClient().setzeInitStand("Verzeichnisse");

				new SocketClient().setzeInitStand("Mandanten-Daten einlesen");
				
				Reha.sysConf.SystemInit(11);
			
				Reha.sysConf.SystemInit(9);

				Thread.sleep(50);
				
				new SocketClient().setzeInitStand("HashMaps initialisieren");
				
				SystemConfig.HashMapsVorbereiten();
				
				Thread.sleep(50);

				new SocketClient().setzeInitStand("Desktop konfigurieren");

				SystemConfig.DesktopLesen();

				Thread.sleep(50);

				new SocketClient().setzeInitStand("Patientenstamm init");

				SystemConfig.PatientLesen();

				Thread.sleep(50);

				new SocketClient().setzeInitStand("Gerätetreiber initialiseieren");

				SystemConfig.GeraeteInit();

				Thread.sleep(50);

				new SocketClient().setzeInitStand("Arztgruppen einlesen");

				SystemConfig.ArztGruppenInit();

				Thread.sleep(50);

				new SocketClient().setzeInitStand("Rezeptparameter einlesen");

				SystemConfig.RezeptInit();

				new SocketClient().setzeInitStand("Bausteine für Therapie-Berichte laden");

				SystemConfig.TherapBausteinInit();

				//SystemConfig.compTest();

				new SocketClient().setzeInitStand("Fremdprogramme überprüfen");

				SystemConfig.FremdProgs();

				new SocketClient().setzeInitStand("Geräteliste erstellen");

				SystemConfig.GeraeteListe();

				SystemConfig.CompanyInit();

				FileTools.deleteAllFiles(new File(SystemConfig.hmVerzeichnisse.get("Temp")));
				if(SystemConfig.sBarcodeAktiv.equals("1")){
					try {
						Reha.barcodeScanner = new BarCodeScanner(SystemConfig.sBarcodeCom);
					} catch (Exception e) {
						////System.out.println("Barcode-Scanner konnte nicht installiert werden");
					} catch (java.lang.Exception e) {
						e.printStackTrace();
					}
				}
				new SocketClient().setzeInitStand("Firmendaten einlesen");

				Vector<Vector<String>> vec = SqlInfo.holeFelder("select min(datum),max(datum) from flexkc");

				Reha.kalMin = DatFunk.sDatInDeutsch( ((String)((Vector<String>)vec.get(0)).get(0)) );

				Reha.kalMax = DatFunk.sDatInDeutsch( ((String)((Vector<String>)vec.get(0)).get(1)) );
				
				SystemConfig.FirmenDaten();			

				new SocketClient().setzeInitStand("Gutachten Parameter einlesen");

				SystemConfig.GutachtenInit();

				SystemConfig.AbrechnungParameter();
				
				SystemConfig.BedienungIni_ReadFromIni();
				
				SystemConfig.OffenePostenIni_ReadFromIni();
				
				SystemConfig.JahresUmstellung();
				
				SystemConfig.Feiertage();
				
				//notwendig bis alle Überhangsrezepte der BKK-Gesundheit abgearbeitet sind.
				SystemConfig.ArschGeigenTest();

				
				new Thread(new PreisListenLaden()).start();
				
				if(SystemConfig.sWebCamActive.equals("1")){
					Reha.thisClass.activateWebCam();
				}

				
			}catch (InterruptedException e1) {
					e1.printStackTrace();
			}catch (NullPointerException e2) {
					e2.printStackTrace();
			}	
		}else{
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws java.lang.Exception {
					new SocketClient().setzeInitStand("INITENDE");
					return null;
				}
			}.execute();

		}
	}
}

final class DbNachladen implements Runnable{
	public void run(){
		final String sDB = "SQL";
		final Reha obj = Reha.thisClass;
		if(Reha.thisClass.conn != null){
			try {
				Reha.thisClass.conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
    	try {
			if (sDB=="SQL"){
				new SocketClient().setzeInitStand("Datenbank initialisieren und öffnen");
				obj.conn = (Connection) DriverManager.getConnection(SystemConfig.vDatenBank.get(0).get(1)+"?jdbcCompliantTruncation=false",
						SystemConfig.vDatenBank.get(0).get(3),SystemConfig.vDatenBank.get(0).get(4));
			}	
			int nurmaschine = SystemConfig.dieseMaschine.toString().lastIndexOf("/");
			new ExUndHop().setzeStatement("delete from flexlock where maschine like '%"+SystemConfig.dieseMaschine.toString().substring(0, nurmaschine)+"%'");
			if(obj.dbLabel != null){
				String db = SystemConfig.vDatenBank.get(0).get(1).replace("jdbc:mysql://", "");
				db = db.substring(0,db.indexOf("/"));
				obj.dbLabel.setText(Reha.aktuelleVersion+db);
			}
    		Reha.DbOk = true;

    	} 
    	catch (final SQLException ex) {
    		Reha.DbOk = false;
    		int nochmals = JOptionPane.showConfirmDialog(null,"Wiederherstellung der Datenbankverbindung - erfolglos!\nErneut versuchen?","Wichtige Benuterzinfo",JOptionPane.YES_NO_OPTION);
    		if(nochmals == JOptionPane.YES_OPTION){
    			new Thread(new DbNachladen()).start();
    		}
    		return;
    	}
    return;
	}
}

final class ErsterLogin implements Runnable{
	private void Login(){
		new Thread(){
			public void run(){
			Reha.starteOfficeApplication();
			OOTools.ooOrgAnmelden();
			}
		}.start();
		ProgLoader.PasswortDialog(0);
	}
	public void run() {
		Login();
		SwingUtilities.invokeLater(new Runnable(){
			public  void run(){
				Reha.thisFrame.setMinimumSize(new Dimension(800,600));
				Reha.thisFrame.setPreferredSize(new Dimension(800,600));
				Reha.thisFrame.setExtendedState(JXFrame.MAXIMIZED_BOTH);
				Reha.thisFrame.setVisible(true);
				INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/rehajava.ini");
				if(inif.getIntegerProperty("HauptFenster", "Divider1") != null){
					Reha.thisClass.jSplitLR.setDividerLocation((Reha.divider1=inif.getIntegerProperty("HauptFenster", "Divider1")));
					Reha.thisClass.jSplitRechtsOU.setDividerLocation((Reha.divider2=inif.getIntegerProperty("HauptFenster", "Divider2")));
					//System.out.println("Divider gesetzt");
					//System.out.println("Divider 1 = "+inif.getIntegerProperty("HauptFenster", "Divider1"));
					//System.out.println("Divider 2 = "+inif.getIntegerProperty("HauptFenster", "Divider2")+"\n\n");
					Reha.dividerOk= true;
					//Hier mußt noch eine funktion getSichtbar() entwickelt werden
					//diese ersetzt die nächste Zeile 
					//System.out.println("Sichtbar Variante = "+Reha.thisClass.getSichtbar());
				}else{
					//System.out.println("Divider-Angaben sind noch null");
					Reha.thisClass.setDivider(5);	
				}

				Reha.thisFrame.getRootPane().validate();
				Reha.isStarted = true;
				
				Reha.thisFrame.setVisible(true);
				

				if(Reha.dividerOk){
					Reha.thisClass.vollsichtbar = Reha.thisClass.getSichtbar();
					if(!SystemConfig.desktopHorizontal){
						Reha.thisClass.jSplitRechtsOU.setDividerLocation((Reha.divider2));	
					}
					//System.out.println("Wert für Vollsichtbar = "+Reha.thisClass.vollsichtbar);
				}

				//Reha.thisFrame.pack();
				new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() throws Exception {
						try{
							//JOptionPane.showMessageDialog(null,System.getProperty("java.home"));
							//JOptionPane.showMessageDialog(null,System.getProperty("java.version"));
							
							
						//SCR335
						//ctpcsc31kv

						if(SystemConfig.sReaderAktiv.equals("1")){
							try{

								System.out.println("Aktiviere Reader: "+SystemConfig.sReaderName+"\n"+
										"CT-API Bibliothek: "+SystemConfig.sReaderCtApiLib);
								Reha.thisClass.ocKVK = new OcKVK(SystemConfig.sReaderName.trim().replace(" ", "_"),
									SystemConfig.sReaderCtApiLib,SystemConfig.sReaderDeviceID,false);
							}catch(CardTerminalException ex){
								disableReader("Fehlerstufe rc = -8 = CardTerminal reagiert nicht\n"+ex.getMessage());
							} catch (CardServiceException e) {
								disableReader("Fehlerstufe rc = -2 oder -4  = Karte wird nicht unterstützt\n"+e.getMessage());
							} catch (ClassNotFoundException e) {
								disableReader("Fehlerstufe rc = -1 = CT-API läßt sich nicht initialisieren\n"+e.getMessage());
							} catch (java.lang.Exception e) {
								disableReader("Anderweitiger Fehler\n"+e.getMessage());
							}
							if(Reha.thisClass.ocKVK != null){
								Vector<Vector<String>> vec = Reha.thisClass.ocKVK.getReaderList();
								for(int i = 0; i < vec.get(0).size();i++){
									System.out.println("*******************");
									System.out.println(vec.get(0).get(i)+" - "+
											vec.get(1).get(i)+" - "+
											vec.get(2).get(i)+" - "+
											vec.get(3).get(i));
								}
								
							}
							//KVKWrapper kvw = new KVKWrapper(SystemConfig.sReaderName);
							//kvw.KVK_Einlesen();
						}
						}catch(NullPointerException ex){
							ex.printStackTrace();
						}
						return null;
					}
				}.execute();
			}
		});
	}
	private void disableReader(String error){
		SystemConfig.sReaderAktiv = "0";
		Reha.thisClass.ocKVK = null;
		JOptionPane.showMessageDialog(null, error);
	}
}

final class PreisListenLaden implements Runnable{
	private void Einlesen(){
		/*
		ParameterLaden.PreiseEinlesen("KG");

		ParameterLaden.PreiseEinlesen("MA");

		ParameterLaden.PreiseEinlesen("ER");
		
		ParameterLaden.PreiseEinlesen("LO");

		ParameterLaden.PreiseEinlesen("RH");

		MachePreisListe.preiseFuellenNeu();	
		
		*/

		Reha.thisClass.jxLinks.setAlpha(1.0f);
		Reha.thisClass.jxRechts.setAlpha(1.0f);
		/*
		for(int i = 1; i < 7;i++){
			new MachePreisListe("potarif"+Integer.toString(i));
		}
		*/
		//long zeit = System.currentTimeMillis();
		new SocketClient().setzeInitStand("Preisliste Physio einlesen");
		SystemPreislisten.ladePreise("Physio");
		new SocketClient().setzeInitStand("Preisliste Massage einlesen");
		SystemPreislisten.ladePreise("Massage");
		new SocketClient().setzeInitStand("Preisliste Ergo einlesen");
		SystemPreislisten.ladePreise("Ergo");
		new SocketClient().setzeInitStand("Preisliste Logo einlesen");		
		SystemPreislisten.ladePreise("Logo");
		new SocketClient().setzeInitStand("Preisliste Reha einlesen");		
		SystemPreislisten.ladePreise("Reha");
		new SocketClient().setzeInitStand("Preisliste Podologie einlesen");
		SystemPreislisten.ladePreise("Podo");
		SystemPreislisten.ladePreise("Common");
		new SocketClient().setzeInitStand("System-Init abgeschlossen!");
		/*
		System.out.println("******************");
		System.out.println(SystemPreislisten.hmFristen.get("Physio"));
		System.out.println("******************");
		System.out.println(SystemPreislisten.hmFristen.get("Massage"));
		System.out.println("******************");
		System.out.println(SystemPreislisten.hmFristen.get("Ergo"));
		System.out.println("******************");
		System.out.println(SystemPreislisten.hmFristen.get("Logo"));
		System.out.println("******************");
		System.out.println(SystemPreislisten.hmFristen.get("Reha"));
		System.out.println("******************");
		System.out.println(SystemPreislisten.hmFristen.get("Podo"));
		System.out.println("******************");
		*/
		/*
		//System.out.println(SystemPreislisten.vKGPreise);
		//System.out.println(SystemPreislisten.vKGPreise.get(0));
		//System.out.println(SystemPreislisten.vKGPreise.get(0).get(0));
		//System.out.println(SystemPreislisten.vKGPreise.get(0).get(0).get(0));
		//System.out.println("*************************************************************");
		//System.out.println(SystemPreislisten.hmPreise.get("Physio"));
		//System.out.println(SystemPreislisten.hmPreise.get("Physio").get(0));
		//System.out.println(SystemPreislisten.hmPreise.get("Physio").get(0).get(0));
		//System.out.println(SystemPreislisten.hmPreise.get("Physio").get(0).get(0).get(0));
		//System.out.println("******Benötigte Zeit zum Einlesen aller Preislisten = "+(System.currentTimeMillis()-zeit)+" Millisekunden");
		*/
		/*
		//System.out.println(SystemPreislisten.vKGPreise);
		//System.out.println(SystemPreislisten.vKGPreise.get(0));
		//System.out.println(SystemPreislisten.vKGPreise.get(1));
		//System.out.println(SystemPreislisten.vKGPreise.get(2));
		//System.out.println(SystemPreislisten.vKGPreise.get(3));
		//System.out.println(SystemPreislisten.vKGPreise.get(4));

		//System.out.println(SystemPreislisten.vKGPreise);
		//System.out.println(SystemPreislisten.vKGPreise.get(0));
		//System.out.println(SystemPreislisten.vKGPreise.get(0).get(0));
		//System.out.println(SystemPreislisten.vKGPreise.get(0).get(0).get(0));
		//System.out.println("*************************************************************");
		//System.out.println(SystemPreislisten.hmPreise.get("Physio"));
		//System.out.println(SystemPreislisten.hmPreise.get("Physio").get(0));
		//System.out.println(SystemPreislisten.hmPreise.get("Physio").get(0).get(0));
		//System.out.println(SystemPreislisten.hmPreise.get("Physio").get(0).get(0).get(0));

		//System.out.println(SystemPreislisten.hmPreisGruppen.get("Physio"));
		//System.out.println(SystemPreislisten.hmPreisBereich.get("Physio"));
		//System.out.println(SystemPreislisten.hmZuzahlRegeln.get("Physio"));
		//System.out.println(SystemPreislisten.hmHMRAbrechnung.get("Physio"));
		//System.out.println(SystemPreislisten.hmNeuePreiseAb.get("Physio"));
		//System.out.println(SystemPreislisten.hmNeuePreiseRegel.get("Physio"));
		//System.out.println(SystemPreislisten.hmHBRegeln.get("Physio"));
		//System.out.println(SystemPreislisten.hmBerichtRegeln.get("Physio"));
		*/

		Reha.thisClass.setzeInitEnde();
		Reha.thisClass.initok = true;
	}
	public void run() {
		Einlesen();
		int i=0;
		while (!Reha.thisClass.initok){
			i = i+1;
			if(i>10){
				break;
			}
			try {
				Thread.sleep(100);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}	
		new Thread(new ErsterLogin()).start();
	}
	
}
final class SplashStarten extends Thread implements Runnable{
	private void StarteSplash(){
	}	
	public void run() {
		StarteSplash();
	}
}

class SocketClient {
	String stand = "";
	Socket server = null;
	public void setzeInitStand(String stand){
		this.stand = stand;
		run();
	}
	public void run() {
		try {
			serverStarten();
		} catch (IOException e) {
		}
	}
	private void serverStarten() throws IOException{
		try{
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
		}catch(NullPointerException ex){
			ex.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
}
class MyGradPanel extends JXPanel  {
    /**
	 * 
	 */
	private static final long serialVersionUID = 2882847000287058980L;

	public void paint(Graphics g){
      Graphics2D g2d = (Graphics2D)g;
      Color s1 = Colors.TaskPaneBlau.alpha(1.0f);
      Color e = Color.WHITE;
      GradientPaint gradient = new GradientPaint(0,0,s1,getWidth(),getHeight(),e);

      
      g2d.setPaint(gradient);
      g2d.fillRect(0,0,getWidth(),getHeight());
      Image jImage = Reha.rehaBackImg.getImage();
      g.drawImage(jImage,((getWidth()/2)-(jImage.getWidth(this)/2)),((getHeight()/2)-(jImage.getHeight(this)/2)), this);
    }
  }
/**************************/
class RehaSockServer{
	static ServerSocket serv = null;
	RehaSockServer() throws IOException{
		try {
			serv = new ServerSocket(1235);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		Socket client = null;
		while(true){
			try {
				client = serv.accept();
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
			StringBuffer sb = new StringBuffer();
			InputStream input = client.getInputStream();
			OutputStream output = client.getOutputStream();
			int byteStream;
			String test = "";
			while( (byteStream =  input.read()) > -1){
				char b = (char)byteStream;
				sb.append(b);
			}
			test = String.valueOf(sb);
			final String xtest = test;
			if(xtest.equals("INITENDE")){
				byte[] schreib = "ok".getBytes();
				output.write(schreib);
				output.flush();
				output.close();
				input.close();
				serv.close();
				serv = null;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}								
				Reha.warten = false;
				break;
			}else{
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
			////System.out.println("Socket wurde geschlossen");
		}else{
			////System.out.println("Socket war bereits geschlossen");
		}
		return;
	}
}
/*******************************************/
final class HilfeDatenbankStarten implements Runnable{

	void StarteDB(){
		final Reha obj = Reha.thisClass;

//		final String sDB = "SQL";
		if (obj.hilfeConn != null){
			try{
			obj.hilfeConn.close();}
			catch(final SQLException e){}
		}
		try{
				Class.forName(SystemConfig.hmHilfeServer.get("HilfeDBTreiber")).newInstance();
				Reha.HilfeDbOk = true; 
    	}catch (InstantiationException e) {
				e.printStackTrace();
		} catch (IllegalAccessException e) {
				e.printStackTrace();
		} catch (ClassNotFoundException e) {
				e.printStackTrace();
		}	
	    try {
	        		Reha.thisClass.hilfeConn = 
	        			(Connection) DriverManager.getConnection(SystemConfig.hmHilfeServer.get("HilfeDBLogin"),
	        					SystemConfig.hmHilfeServer.get("HilfeDBUser"),SystemConfig.hmHilfeServer.get("HilfeDBPassword"));
	    }catch (final SQLException ex) {
	    	Reha.HilfeDbOk = false;
	    	return;
	    }
        return;
	}
	public void run() {
		StarteDB();
	}
}
