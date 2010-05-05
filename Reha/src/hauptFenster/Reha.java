package hauptFenster;

import geraeteInit.BarCodeScanner;

import java.awt.AWTEvent;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
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
import java.beans.PropertyVetoException;
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
import java.util.TooManyListenersException;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import javax.swing.TransferHandler;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import krankenKasse.KassenPanel;
import kvKarte.KVKWrapper;
import menus.TerminMenu;
import oOorgTools.OOTools;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXRootPane;
import org.jdesktop.swingx.JXStatusBar;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.border.DropShadowBorder;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;
import org.therapi.reha.patient.PatientHauptPanel;

import preisListenHandling.MachePreisListe;



//import patientenFenster.PatGrundPanel;
import rechteTools.Rechte;
import rehaInternalFrame.JRehaInternal;
import roogle.RoogleFenster;
import sqlTools.ExUndHop;
import sqlTools.SqlInfo;
import systemEinstellungen.INIFile;
import systemEinstellungen.SystemConfig;
import systemEinstellungen.SystemPreislisten;
import systemTools.Colors;
import systemTools.FileTools;
import systemTools.Meldungen;
import systemTools.RehaPainters;
import systemTools.TestePatStamm;
import systemTools.WinNum;
import terminKalender.DatFunk;
import terminKalender.ParameterLaden;
import terminKalender.TerminFenster;
import urlaubBeteiligung.Beteiligung;
import urlaubBeteiligung.Urlaub;
import verkauf.Verkauf;
import abrechnung.AbrechnungGKV;
import abrechnung.AbrechnungReha;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.application.OfficeApplicationRuntime;
import ag.ion.bion.officelayer.document.DocumentException;
import ag.ion.bion.officelayer.document.IDocument;
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
import entlassBerichte.EBerichtPanel;
import events.RehaEvent;
import events.RehaEventClass;
import events.RehaEventListener;



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
	public Verkauf verkaufpanel = null;
	public Barkasse barkassenpanel = null;
	public AbrechnungReha rehaabrechnungpanel = null;
	public BenutzerRechte benutzerrechtepanel = null;
	
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
	private JMenu editMenu = null;
	private JMenu urlaubMenu = null;
	private JMenu helpMenu = null;
	private JMenuItem exitMenuItem = null;
	private JMenuItem aboutMenuItem = null;
	private JMenuItem cutMenuItem = null;
	private JMenuItem copyMenuItem = null;
	private JMenuItem pasteMenuItem = null;
	private JMenuItem saveMenuItem = null;
	private JDialog aboutDialog = null;
	private JPanel aboutContentPane = null;
	private JLabel aboutVersionLabel = null;
	private JXRootPane JXRootPane = null;
	public JXStatusBar jXStatusBar = null;

	private int dividerLocLR = 0; 
	private int dividerLocOU = 0;
	
	public JLabel shiftLabel = null;
	public JLabel messageLabel = null;
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
	//public final static String Titel = "Thera-3.141592654";
	public final static String Titel = "Thera-\u03C0";
	public boolean KollegenOk = false;
	public static String aktLookAndFeel = "";
	public static SystemConfig sysConf = null;
	public static IOfficeApplication officeapplication;
	//public TerminFenster TerminFenster[]={null,null,null,null,null}; 
		
	public static RehaSockServer RehaSock = null;
	@SuppressWarnings("unchecked")
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
	public static boolean demoversion = true;
	public static boolean vollbetrieb = false;

	//  
	//@jve:decl-index=0:
	/**
	 * @param args
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		//Process pro = null;
		String prog = java.lang.System.getProperty("user.dir");
		osVersion = System.getProperty("os.name");
		if(osVersion.contains("Linux")){
			proghome = "/opt/RehaVerwaltung/";
		}else if(osVersion.contains("Windows")){
			proghome = prog.substring(0, 2)+"/RehaVerwaltung/";
		}else if(osVersion.contains("OSX")){
			
		}
		String javaPfad = java.lang.System.getProperty("java.home").replaceAll("\\\\","/");
		System.out.println("Die JavaVersion = "+java.lang.System.getProperty("java.version"));
		System.out.println("Der Pfad zu Java = "+javaPfad);
		if(args.length > 0){
			String[] split = args[0].split("@");
			aktIK = split[0];
			aktMandant = split[1];

		}else{
			INIFile inif = new INIFile(Reha.proghome+"ini/mandanten.ini");
			int DefaultMandant = inif.getIntegerProperty("TheraPiMandanten", "DefaultMandant");
			aktIK = inif.getStringProperty("TheraPiMandanten", "MAND-IK"+DefaultMandant);
			aktMandant = inif.getStringProperty("TheraPiMandanten", "MAND-NAME"+DefaultMandant);			
		}
		Titel2 = "  -->  [Mandant: "+aktMandant+"]";
		System.out.println(Titel2);
		/**************************/
		new Thread(){
			public  void run(){
				try {
					System.out.println("Starte SocketServer");
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
					process = new ProcessBuilder("java", "-jar",proghome+"RehaxSwing.jar").start();
					InputStream is = process.getInputStream();
					
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader br = new BufferedReader(isr);
					String line;
						       
			       while ((line = br.readLine()) != null) {
			         //System.out.println("Lese Socket "+line);
			       }
			       is.close();
			       isr.close();
			       br.close();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}		
		}.start();
		
		System.out.println("starte RehaxSwing");
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
		File f = new File(javaPfad+"/bin/win32com.dll");
		if(! f.exists()){
			new SocketClient().setzeInitStand("Kopiere win32com.dll");
			try {
				FileTools.copyFile(new File(proghome+"Libraries/lib/serial/win32com.dll"),f, 4096, false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			System.out.println("Systemdateien win32com.dll existiert bereits, kopieren nicht erforderlich");
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
			System.out.println("Systemdateien comm.jar existiert bereits, kopieren nicht erforderlich");
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
			System.out.println("Systemdateien javax.comm.properties existiert bereits, kopieren nicht erforderlich");
		}
		new Thread(){
			public void run(){
				new SocketClient().setzeInitStand("System-Icons laden");
				SystemConfig.SystemIconsInit();
				iconsOk = true;
				new SocketClient().setzeInitStand("System-Config initialisieren");
			}
		}.start();
		/*
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws java.lang.Exception {
				new SocketClient().setzeInitStand("System-Parameter laden");
				SystemConfig.SystemIconsInit();
				iconsOk = true;
				return null;
			}
		}.execute();
		*/
		/*
		new Thread(){
			public void run(){
				new SocketClient().setzeInitStand("System-Parameter laden");
				SystemConfig.SystemIconsInit();
				iconsOk = true;
			}
		}.start();
		*/

		System.out.println("RehaxSwing wurde gestartet");
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
			String name = "Tahoma";
			int size = 10;
			//PLAIN=1, BOLD=1, ITALIC=2
			Font[] fonts = {new Font(name, 0, size), new Font(name, 1, size),
			new Font(name, 2, size), new Font(name, 3, size)}; 
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
				System.out.println("ProgHome = "+Reha.proghome);
				Reha.thisClass.setDivider(5);
				Reha.thisClass.doCompoundPainter();
		   		//Reha.starteOfficeApplication();
			}
		});
		
	}
	public void setzeInitEnde(){
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws java.lang.Exception {
				System.out.println("poste INITENDE über Socket");
				new SocketClient().setzeInitStand("INITENDE");
				return null;
			}
			
		}.execute();
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
			    progLoader = new ProgLoader();
				return null;
			}
			
		}.execute();

		
	}
	public void aktiviereNaechsten(int welchen){
		JInternalFrame[] frame = desktops[welchen].getAllFrames();
		System.out.println("Es gibt noch insgesamt "+frame.length+" in diesem Desktop");
		if(frame.length > 0){
			for(int i = 0; i < frame.length;i++){
				System.out.println("InternalFrames übrig = "+frame[i].getTitle());
				((JRehaInternal)frame[0]).toFront();
				((JRehaInternal)frame[0]).setActive(true);
				((JRehaInternal)frame[0]).getContent().requestFocus();
				break;
			}
		}else{
			if(welchen==0){
				frame = desktops[1].getAllFrames();
				for(int i = 0; i < frame.length;i++){
					System.out.println("InternalFrames übrig = "+frame[i].getTitle());
					((JRehaInternal)frame[0]).toFront();
					((JRehaInternal)frame[0]).setActive(true);
					((JRehaInternal)frame[0]).getContent().requestFocus();
					ProgLoader.containerHandling(1);
					break;
				}
			}else{
				frame = desktops[0].getAllFrames();
				for(int i = 0; i < frame.length;i++){
					System.out.println("InternalFrames übrig = "+frame[i].getTitle());
					((JRehaInternal)frame[0]).toFront();
					((JRehaInternal)frame[0]).setActive(true);
					((JRehaInternal)frame[0]).getContent().requestFocus();
					ProgLoader.containerHandling(0);
					break;
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

	@SuppressWarnings("unchecked")
	private JXFrame getJFrame() {
		if (jFrame == null) {
			jFrame = new JXFrame();
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
			desktops[1].addFocusListener(this);
			desktops[1].addMouseListener(this);
			desktops[1].addMouseMotionListener(this);
			desktops[1].addComponentListener(this);
			desktops[1].addContainerListener(this);
		    jpUnten.add(desktops[1]);
		    jp2.add(jpUnten,BorderLayout.CENTER);
		    jxRechtsUnten.add(jp2,BorderLayout.CENTER);
			jxRechtsUnten.validate();
			jxRechtsUnten.updateUI();
			/********************************/
			

			jSplitRechtsOU = UIFSplitPane.createStrippedSplitPane(JSplitPane.VERTICAL_SPLIT,
		             jxRechtsOben,
		             jxRechtsUnten);
			jSplitRechtsOU.addPropertyChangeListener(new PropertyChangeListener(){
				@Override
				public void propertyChange(PropertyChangeEvent arg0) {
					dividerLocOU = jSplitRechtsOU.getDividerLocation();
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
				System.out.println("Event getSource: = "+evt.getSource());
				System.out.println("Event Nachricht: = "+ evt.getRehaEvent());				
			}
	    });
		*/
	    return jFrame;
	}
	public static void setSystemConfig(SystemConfig sysConf){
		Reha.sysConf = sysConf;
	}

	/*
	private JXRootPane getJXRootPane() {
		if (JXRootPane == null) {
			JXRootPane = new JXRootPane();
			JXRootPane.setDoubleBuffered(true);
			JXRootPane.setBackground(Color.WHITE);
		}
		return JXRootPane;
	}
	*/
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
			JXPanel bar2 = new JXPanel(new BorderLayout());
			bar2.setOpaque(false);
			bar2.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
			JLabel lab = new JLabel("Benutzer: Admin");
			lab.setVerticalAlignment(JLabel.CENTER);
			lab.setHorizontalAlignment(JLabel.LEFT);
			bar2.add(lab);
			bar.add(bar2);
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
			bar2 = new JXPanel(new BorderLayout());
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
				        	mitgebracht  = new String((String) tr.getTransferData(flavors[i]).toString());
				      }
				      System.out.println("Es wurde mitgebracht -> "+mitgebracht);
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
	/**
	 * This method initializes jJMenuBar	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */
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

	/**
	 * This method initializes jMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu();
			fileMenu.setFont(new Font("Dialog", Font.PLAIN, 12));			
			fileMenu.setText("Datei");
			//fileMenu.add(getSaveMenuItem());
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
			men = new JMenuItem("Aerzte");
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
			/*
			men = new JMenuItem("Heilmittel-Privatliquidation");
			men.setActionCommand("hmprivatabrechnung");
			men.addActionListener(this);
			abrechnungMenu.add(men);
			*/
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
		}
		return abrechnungMenu;
	}
	private JMenu getstatistikMenu() {
		if (statistikMenu == null) {
			statistikMenu = new JMenu();
			statistikMenu.setFont(new Font("Dialog", Font.PLAIN, 12));			
			statistikMenu.setText("Statistiken");
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
		}
		return toolsMenu;
	}
	private JMenu getverkaufMenu() {
		if (verkaufMenu == null) {
			verkaufMenu = new JMenu();
			verkaufMenu.setFont(new Font("Dialog", Font.PLAIN, 12));			
			verkaufMenu.setText("Verkauf");
			JMenuItem men = new JMenuItem("Verkäufe tätigen");
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


	/**
	 * This method initializes jMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getEditMenu() {
		if (editMenu == null) {
			editMenu = new JMenu();
			editMenu.setFont(new Font("Dialog", Font.PLAIN, 12));
			editMenu.setText("Edit");
			editMenu.add(getCutMenuItem());
			editMenu.add(getCopyMenuItem());
			editMenu.add(getPasteMenuItem());
 
		}
		return editMenu;
	}



    /**
	 * This method initializes jMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getHelpMenu() {
		if (helpMenu == null) {
			helpMenu = new JMenu();
			helpMenu.setFont(new Font("Dialog", Font.PLAIN, 12));
			helpMenu.setText("Hilfe");
			helpMenu.add(getAboutMenuItem());
		}
		return helpMenu;
	}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getExitMenuItem() {
		if (exitMenuItem == null) {
			exitMenuItem = new JMenuItem();
			exitMenuItem.setText("Thera-Pi beenden");
			exitMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Runtime r = Runtime.getRuntime();
				    r.gc();
				    long freeMem = r.freeMemory();
				    System.out.println("Freier Speicher nach  gc():    " + freeMem);
					if(JOptionPane.showConfirmDialog(null, "thera-\u03C0 wirklich schließen?", "Bitte bestätigen", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION ) {
						if(Reha.DbOk){
							if( (SystemConfig.dieseMaschine.toString().indexOf("10.8.0.6") > 0) ||
									(SystemConfig.dieseMaschine.toString().indexOf("192.168.2.55") > 0)	){
								//fare niente
							}else{
								Date zeit = new Date();
								String stx = "Insert into eingeloggt set comp='"+SystemConfig.dieseMaschine+"', zeit='"+zeit.toString()+"', einaus='aus'";
								new ExUndHop().setzeStatement(stx);
							}
						}

						System.exit(0);
					}else{
						return;
					}
				    
				}
			});
		}
		return exitMenuItem;
	}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getAboutMenuItem() {
		if (aboutMenuItem == null) {
			aboutMenuItem = new JMenuItem();
			aboutMenuItem.setText("About");
		}
		return aboutMenuItem;
	}

	/**
	 * This method initializes aboutDialog	
	 * 	
	 * @return javax.swing.JDialog
	 */
	private JDialog getAboutDialog() {
		if (aboutDialog == null) {
			aboutDialog = new JDialog(getJFrame(), true);
			aboutDialog.setTitle("About");
			aboutDialog.setContentPane(getAboutContentPane());
		}
		return aboutDialog;
	}

	/**
	 * This method initializes aboutContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getAboutContentPane() {
		if (aboutContentPane == null) {
			aboutContentPane = new JPanel();
			aboutContentPane.setLayout(new BorderLayout());
			aboutContentPane.add(getAboutVersionLabel(), BorderLayout.CENTER);
		}
		return aboutContentPane;
	}

	/**
	 * This method initializes aboutVersionLabel	
	 * 	
	 * @return javax.swing.JLabel	
	 */
	private JLabel getAboutVersionLabel() {
		if (aboutVersionLabel == null) {
			aboutVersionLabel = new JLabel();
			aboutVersionLabel.setText("Version 1.0");
			aboutVersionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return aboutVersionLabel;
	}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getCutMenuItem() {
		if (cutMenuItem == null) {
			cutMenuItem = new JMenuItem();
			cutMenuItem.setText("Cut");
			cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
					Event.CTRL_MASK, true));
		}
		return cutMenuItem;
	}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getCopyMenuItem() {
		if (copyMenuItem == null) {
			copyMenuItem = new JMenuItem();
			copyMenuItem.setText("Copy");
			copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
					Event.CTRL_MASK, true));
		}
		return copyMenuItem;
	}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getPasteMenuItem() {
		if (pasteMenuItem == null) {
			pasteMenuItem = new JMenuItem();
			pasteMenuItem.setText("Paste");
			pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
					Event.CTRL_MASK, true));
		}
		return pasteMenuItem;
	}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getSaveMenuItem() {
		if (saveMenuItem == null) {
			saveMenuItem = new JMenuItem();
			saveMenuItem.setText("Save");
//			saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
//					Event.CTRL_MASK, true));
			saveMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					//thisFrame.getContentPane().add(jri);
					JInternalFrame[] frm = Reha.thisClass.desktops[1].getAllFrames(); 
					if(frm.length > 3){
						Component comp = frm[0];
						if(!frm[0].isIcon()){
							((JRehaInternal)comp).setDesktop(0);
							Reha.thisClass.desktops[1].remove(comp);
							Reha.thisClass.desktops[1].revalidate();
							Reha.thisClass.desktops[1].repaint();
							Reha.thisClass.desktops[0].add(comp);
							((JRehaInternal)comp).toFront();
							Reha.thisClass.desktops[0].revalidate();
							Reha.thisClass.desktops[0].repaint();							
							try {
								((JInternalFrame)comp).setSelected(true);
							} catch (PropertyVetoException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}else{
						String name = "ToolFenster"+WinNum.NeueNummer();
						JRehaInternal jry = new JRehaInternal("Reha-Interner Frame Nummer:"+
								Reha.thisClass.desktops[1].getComponentCount()+1 ,new ImageIcon(Reha.proghome+"icons/eye_16x16.gif"),1) ;
						AktiveFenster.setNeuesFenster(name,(JComponent)jry,0,(Container)jry.getContentPane());
						jry.setName(name);
						jry.setSize(new Dimension(350,300));
						JXPanel newcont = new JXPanel();
						newcont.setBackgroundPainter(Reha.RehaPainter[2]);
						jry.setContent(newcont);
						//jri.setzeTitel("Doofi");
						jry.addComponentListener(Reha.thisClass);
						jry.setVisible(true);
						Reha.thisClass.desktops[1].add(jry);
						System.out.println("Anzahl Fenster = "+Reha.thisClass.desktops[1].getComponentCount());
						try {
							jry.setSelected(true);
						} catch (PropertyVetoException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

				}
			});

		}
		return saveMenuItem;
	}

	public void setzeUi(String sUI,JScrollPane panel){
	      try {
	    	  SystemConfig.UpdateIni("HauptFenster","LookAndFeel",sUI);
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
	

	
    public static void starteOfficeApplication()
    { 

    	final String OPEN_OFFICE_ORG_PATH = SystemConfig.OpenOfficePfad;

        try
        {
        	System.out.println("**********Open-Office wird gestartet***************");
            String path = OPEN_OFFICE_ORG_PATH;
            Map <String, String>config = new HashMap<String, String>();
            config.put(IOfficeApplication.APPLICATION_HOME_KEY, path);
            config.put(IOfficeApplication.APPLICATION_TYPE_KEY, IOfficeApplication.LOCAL_APPLICATION);
            System.setProperty(IOfficeApplication.NOA_NATIVE_LIB_PATH,SystemConfig.OpenOfficeNativePfad);
            officeapplication = OfficeApplicationRuntime.getApplication(config);
            officeapplication.activate();
            officeapplication.getDesktopService().addTerminateListener(new VetoTerminateListener() {
            	  public void queryTermination(ITerminateEvent terminateEvent) {
            	    super.queryTermination(terminateEvent);
            	    try {
            	      IDocument[] docs = officeapplication.getDocumentService().getCurrentDocuments();
            	      if (docs.length == 1) { 
            	        docs[0].close();
            	        System.out.println("Letztes Dokument wurde geschlossen");
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
            System.out.println("Open-Office wurde gestartet");
            System.out.println("Open-Office-Typ: "+officeapplication.getApplicationType());
            
            new SwingWorker<Void,Void>(){

				@Override
				protected Void doInBackground() throws Exception {
            		System.out.println("OpenOffice -> Aufruf");
          
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
        						Reha.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        						Reha.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        						return null;
        					}
        				}.execute();
                    }
                    if(keyEvent.isAltDown() &&
                            keyEvent.getID() == KeyEvent.KEY_PRESSED && keyEvent.getKeyCode()==88) {  // Ctrl-P
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
    					JComponent kasse = AktiveFenster.getFensterAlle("KrankenKasse");
    					System.out.println("Krankenkassen einlesen");
                    }
                    if(keyEvent.isControlDown() &&
                            keyEvent.getID() == KeyEvent.KEY_PRESSED && keyEvent.getKeyCode()==65) {  // Ctrl-K
    					JComponent arzt = AktiveFenster.getFensterAlle("ArztVerwaltung");
    					
						Reha.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
						Reha.thisClass.progLoader.ArztFenster(0,TestePatStamm.PatStammArztID());
						Reha.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
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
            		System.out.println("MausEvent = "+event);
            	}
			}
        	
        }, AWTEvent.MOUSE_EVENT_MASK);
        */
    }
    public void setDivider(int variante){
    	final int xvariante = variante;
    	SwingUtilities.invokeLater(new Runnable(){
      	   public  void run()
      	   {	
      		   int i;
      		   for(i=0;i<1;i++){
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
			//Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener(){
            public void eventDispatched(AWTEvent event)  {
                if(event instanceof FocusEvent) {
                    FocusEvent focusEvent = (FocusEvent) event;
                }
            }
        }, mask);
	}    

	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	public void componentResized(ComponentEvent arg0) {
		//Gr��e einstellen
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
		// TODO Auto-generated method stub
		//System.out.println("MausClick"+arg0);
		
	}


	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		//System.out.println("Reha->Maus eingetreten->"+arg0.getSource());
	}


	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		//System.out.println("Reha->Maus verlassen->"+arg0.getSource());		
	}


	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		//System.out.println("Reha->Maus Taste gedr�ckt->"+arg0.getSource());
	}


	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		//System.out.println("Reha->Maus Taste losgelassen->"+arg0.getSource());		
	}
/************Motion f�r DragEvent******************/

	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		//System.out.println("MausDragged"+arg0);
		//System.out.println("Reha->Maus Taste drag->"+arg0.getSource());		
	}


	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
/************KeyListener*************************/

	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	


	public void rehaEventOccurred(RehaEvent evt) {
		//System.out.println("Reha thisClass Event getSource: = "+evt.getSource());
		//System.out.println("Reha thisClass Event Nachricht: = "+ evt.getRehaEvent());	
		if(evt.getRehaEvent().equals("PatSuchen")){
//			System.out.println("Event getSource.getDetails: = "+((RehaEvent) evt.getSource()).getDetails());
			//System.out.println("Reha thisClass Event getDetails[0]: = "+evt.getDetails()[0]);
			//System.out.println("Reha thisClass Event getDetails[1]: = "+evt.getDetails()[1]);			
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

	}
	@Override
	public void windowClosing(WindowEvent arg0) {
		if(JOptionPane.showConfirmDialog(null, "thera-\u03C0 wirklich schließen?", "Bitte bestätigen", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION ) {
			if(Reha.DbOk){
				if( (SystemConfig.dieseMaschine.toString().indexOf("10.8.0.6") > 0) ||
						(SystemConfig.dieseMaschine.toString().indexOf("192.168.2.55") > 0)	){
					//fare niente
				}else{
					Date zeit = new Date();
					String stx = "Insert into eingeloggt set comp='"+SystemConfig.dieseMaschine+"', zeit='"+zeit.toString()+"', einaus='aus'";
					SqlInfo.sqlAusfuehren(stx);
					//new ExUndHop().setzeStatement(stx);
				}
			}

			JInternalFrame[] frame = desktops[0].getAllFrames();
			System.out.println("Es gibt noch insgesamt "+frame.length+" in diesem Desktop");
			for(int i = 0; i < frame.length;i++){
				System.out.println("InternalFrames wird geschlossen = "+frame[i].getTitle());
				frame[i].dispose();
				frame[i] = null;
			}
			frame = desktops[1].getAllFrames();
			System.out.println("Es gibt noch insgesamt "+frame.length+" in diesem Desktop");
			for(int i = 0; i < frame.length;i++){
				System.out.println("InternalFrames wird geschlossen = "+frame[i].getTitle());
				frame[i].dispose();
				frame[i] = null;
			}
			if(Reha.thisClass.conn != null){
				try {
					Reha.thisClass.conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(Reha.officeapplication != null){
				//Reha.officeapplication.dispose();
			}
			System.exit(0);
		}else{
			return;
		}
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
		System.out.println("WindowStateEvent: "+arg0);
	}
	@Override
	public void focusGained(FocusEvent e) {
		System.out.println("fokus erhalten "+e);
	}
	@Override
	public void focusLost(FocusEvent e) {
		System.out.println("fokus verloren "+e);
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
		// TODO Auto-generated method stub
		
	}
	@Override
	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void componentResized(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		repaint();
	}
	@Override
	public void componentShown(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
}
/*******************/
@Override
public void actionPerformed(ActionEvent arg0) {
	String cmd = arg0.getActionCommand();
	if(cmd.equals("patient")){
		System.out.println("ActionListener patient");
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				//Reha.thisClass.progressStarten(true);
				progLoader.ProgPatientenVerwaltung(1);
				Reha.thisClass.progressStarten(false);
				return null;
			}
		}.execute();
		return;
	}
	if(cmd.equals("kasse")){
		System.out.println("ActionListener kasse");		
		Reha.thisClass.progLoader.KassenFenster(0,TestePatStamm.PatStammKasseID());
		return;
	}
	if(cmd.equals("arzt")){
		System.out.println("ActionListener arzt");	
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
		Reha.thisClass.progLoader.UrlaubFenster(1,"");
		return;
	}
	if(cmd.equals("umsatzbeteiligung")){
		Reha.thisClass.progLoader.BeteiligungFenster(1,"");
		return;
	}
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
        		System.out.println(sDB+" Treiber gestartet");
				Class.forName(SystemConfig.vDatenBank.get(0).get(0)).newInstance();
			}/*else{
				Class.forName(SystemConfig.vDatenBank.get(1).get(0)).newInstance();
			}*/	

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
	    			}/*else{	
	    				obj.conn = (Connection) DriverManager.getConnection(SystemConfig.vDatenBank.get(1).get(1),"","");
	    			}*/	
	        		Reha.DbOk = true;

	        	} 
	        	catch (final SQLException ex) {
	        		System.out.println("SQLException: " + ex.getMessage());
	        		System.out.println("SQLState: " + ex.getSQLState());
	        		System.out.println("VendorError: " + ex.getErrorCode());
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
				if( (SystemConfig.dieseMaschine.toString().indexOf("10.8.0.6") > 0) ||
						(SystemConfig.dieseMaschine.toString().indexOf("192.168.2.55") > 0)	){

				}else{
					Date zeit = new Date();
					String stx = "Insert into eingeloggt set comp='"+SystemConfig.dieseMaschine+"', zeit='"+zeit.toString()+"', einaus='ein'";
					new ExUndHop().setzeStatement(stx);
				}
				try {
				
				Thread.sleep(50);
				 
				new SocketClient().setzeInitStand("Datenbank starten");

				System.out.println("Connection ok");
				new SocketClient().setzeInitStand("Datenbank ok");

				ParameterLaden.Init();
				new SocketClient().setzeInitStand("Systemparameter laden");
				
				Reha.thisClass.shiftLabel.setText("Term-User ok!");
				Reha.sysConf.SystemInit(3);
				ParameterLaden.Passwort();
				new SocketClient().setzeInitStand("Systemparameter ok");
				
				Reha.thisClass.shiftLabel.setText("Prog-User ok!");

				/************************/
				//System.setProperty("nativeswing.webbrowser.runtime","xulrunner.exe");
				//NativeInterface.initialize();
				//NativeInterface.open();
				/************************/
				new SocketClient().setzeInitStand("Native Interface ok");
				
				Reha.thisClass.shiftLabel.setText("Native Interface ok!");
				Reha.sysConf.SystemInit(4);
				new SocketClient().setzeInitStand("Emailparameter");

				Reha.sysConf.SystemInit(6);
				new SocketClient().setzeInitStand("Roogle-Gruppen ok!");

				Reha.sysConf.SystemInit(7);
				new SocketClient().setzeInitStand("Verzeichnisse");

				new SocketClient().setzeInitStand("Mandanten-Daten einlesen");
				Reha.sysConf.SystemInit(11);
				//new SocketClient().setzeInitStand("TK-Farben einlesen");			
				Reha.sysConf.SystemInit(9);

				//SystemConfig.InetSeitenEinlesen();

				//new SocketClient().setzeInitStand("Tarifgruppen einlesen");
				//SystemConfig.TarifeLesen();

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
				SystemConfig.compTest();
				new SocketClient().setzeInitStand("Fremdprogramme überprüfen");
				SystemConfig.FremdProgs();
				new SocketClient().setzeInitStand("Geräteliste erstellen");
				SystemConfig.GeraeteListe();
				SystemConfig.CompanyInit();
				FileTools.deleteAllFiles(new File(SystemConfig.hmVerzeichnisse.get("Temp")));
				if(SystemConfig.sBarcodeAktiv.equals("1")){
					try {
						new BarCodeScanner(SystemConfig.sBarcodeCom);
					} catch (Exception e) {
						System.out.println("Barcode-Scanner konnte nicht installiert werden");
					} catch (java.lang.Exception e) {
						e.printStackTrace();
					}
				}
				new SocketClient().setzeInitStand("Firmendaten einlesen");
				Vector<Vector<String>> vec = SqlInfo.holeFelder("select min(datum),max(datum) from flexkc");
				Reha.kalMin = DatFunk.sDatInDeutsch( ((String)((Vector)vec.get(0)).get(0)) );
				Reha.kalMax = DatFunk.sDatInDeutsch( ((String)((Vector)vec.get(0)).get(1)) );
				System.out.println("Kalenderspanne = von "+Reha.kalMin+" bis "+Reha.kalMax);
				SystemConfig.FirmenDaten();			
				new SocketClient().setzeInitStand("Gutachten Parameter einlesen");
				SystemConfig.GutachtenInit();
				SystemConfig.AbrechnungParameter();
				new Thread(new PreisListenLaden()).start();
				}catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}catch (NullPointerException e2) {
					// TODO Auto-generated catch block
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
				//obj.conn = (Connection) DriverManager.getConnection("jdbc:mysql://194.168.1.8:3306/dbf","entwickler","entwickler");
				Reha.thisClass.conn = (Connection) DriverManager.getConnection(SystemConfig.vDatenBank.get(0).get(1)+"?jdbcCompliantTruncation=false","entwickler","entwickler");
				JOptionPane.showMessageDialog(null,"Die Wiederherstellung der Datenbankverbindung war - erfolgreich!");
			}else{	
				Reha.thisClass.conn = (Connection) DriverManager.getConnection(SystemConfig.vDatenBank.get(1).get(1),"","");
			}	
    		Reha.DbOk = true;
    	} 
    	catch (final SQLException ex) {
    		System.out.println("SQLException: " + ex.getMessage());
    		System.out.println("SQLState: " + ex.getSQLState());
    		System.out.println("VendorError: " + ex.getErrorCode());
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
				Dimension res = Toolkit.getDefaultToolkit().getScreenSize();
				int breit= 800,hoch=600; 
				int x = res.width / 2 - (breit/2);
				int y = res.height /2 - (hoch/2);
				Reha.thisFrame.setMinimumSize(new Dimension(800,600));
				Reha.thisFrame.setExtendedState(JXFrame.MAXIMIZED_BOTH);
				Reha.thisClass.setDivider(5);
				Reha.thisFrame.getRootPane().validate();
				Reha.thisFrame.setVisible(true);
				new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() throws Exception {
						if(SystemConfig.sReaderAktiv.equals("1")){
							KVKWrapper kvw = new KVKWrapper(SystemConfig.sReaderName);
							kvw.KVK_Einlesen();
						}
						return null;
					}
				}.execute();
			}
		});
	}
}

final class PreisListenLaden implements Runnable{
	private void Einlesen(){
		
		//ParameterLaden.PreiseEinlesen("KG");

		//ParameterLaden.PreiseEinlesen("MA");

		//ParameterLaden.PreiseEinlesen("ER");
		
		//ParameterLaden.PreiseEinlesen("LO");

		//ParameterLaden.PreiseEinlesen("RH");

		Reha.thisClass.jxLinks.setAlpha(1.0f);
		Reha.thisClass.jxRechts.setAlpha(1.0f);
		
		//new SocketClient().setzeInitStand("INITENDE");
		
		//MachePreisListe.preiseFuellenNeu();

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
		SystemPreislisten.ladePreise("Common");
		new SocketClient().setzeInitStand("System-Init abgeschlossen!");
		/*
		System.out.println(SystemPreislisten.vKGPreise);
		System.out.println(SystemPreislisten.vKGPreise.get(0));
		System.out.println(SystemPreislisten.vKGPreise.get(0).get(0));
		System.out.println(SystemPreislisten.vKGPreise.get(0).get(0).get(0));
		System.out.println("*************************************************************");
		System.out.println(SystemPreislisten.hmPreise.get("Physio"));
		System.out.println(SystemPreislisten.hmPreise.get("Physio").get(0));
		System.out.println(SystemPreislisten.hmPreise.get("Physio").get(0).get(0));
		System.out.println(SystemPreislisten.hmPreise.get("Physio").get(0).get(0).get(0));
		System.out.println("******Benötigte Zeit zum Einlesen aller Preislisten = "+(System.currentTimeMillis()-zeit)+" Millisekunden");
		*/
		/*
		System.out.println(SystemPreislisten.vKGPreise);
		System.out.println(SystemPreislisten.vKGPreise.get(0));
		System.out.println(SystemPreislisten.vKGPreise.get(1));
		System.out.println(SystemPreislisten.vKGPreise.get(2));
		System.out.println(SystemPreislisten.vKGPreise.get(3));
		System.out.println(SystemPreislisten.vKGPreise.get(4));

		System.out.println(SystemPreislisten.vKGPreise);
		System.out.println(SystemPreislisten.vKGPreise.get(0));
		System.out.println(SystemPreislisten.vKGPreise.get(0).get(0));
		System.out.println(SystemPreislisten.vKGPreise.get(0).get(0).get(0));
		System.out.println("*************************************************************");
		System.out.println(SystemPreislisten.hmPreise.get("Physio"));
		System.out.println(SystemPreislisten.hmPreise.get("Physio").get(0));
		System.out.println(SystemPreislisten.hmPreise.get("Physio").get(0).get(0));
		System.out.println(SystemPreislisten.hmPreise.get("Physio").get(0).get(0).get(0));

		System.out.println(SystemPreislisten.hmPreisGruppen.get("Physio"));
		System.out.println(SystemPreislisten.hmPreisBereich.get("Physio"));
		System.out.println(SystemPreislisten.hmZuzahlRegeln.get("Physio"));
		System.out.println(SystemPreislisten.hmHMRAbrechnung.get("Physio"));
		System.out.println(SystemPreislisten.hmNeuePreiseAb.get("Physio"));
		System.out.println(SystemPreislisten.hmNeuePreiseRegel.get("Physio"));
		System.out.println(SystemPreislisten.hmHBRegeln.get("Physio"));
		System.out.println(SystemPreislisten.hmBerichtRegeln.get("Physio"));
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
				// TODO Auto-generated catch block
				
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
		this.server = new Socket("localhost",1234);
		OutputStream output = (OutputStream) server.getOutputStream();
		InputStream input = server.getInputStream();

		byte[] bytes = this.stand.getBytes();

		output.write(bytes);
		output.flush();
		//System.out.println("Schreibe auf socket -> "+this.stand);
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
			System.out.println("Reha SocketServer gestartet auf Port 1235");
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
			test = new String(sb);
			//System.out.println("Socket= "+test);			
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
						System.out.println("INITENDE-angekommen");
						Reha.warten = false;
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
			System.out.println("Socket war bereits geschlossen");
		}
		return;
	}
}
/*******************************************/
final class HilfeDatenbankStarten implements Runnable{

	void StarteDB(){
		final Reha obj = Reha.thisClass;

		final String sDB = "SQL";
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
	        		System.out.println("SQLException: " + ex.getMessage());
	        		System.out.println("SQLState: " + ex.getSQLState());
	        		System.out.println("VendorError: " + ex.getErrorCode());
	        		Reha.HilfeDbOk = false;
	        		return;
	    }
        return;
	}
	public void run() {
		StarteDB();
	}
}
