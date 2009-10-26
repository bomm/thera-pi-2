package roogle;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.LinearGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;


import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;

import jxTableTools.ToolTipRenderer;
import jxTableTools.ZahlTableCellEditor;
import jxTableTools.ZeitCancelCellEditor;
import jxTableTools.ZeitTableCellEditor;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.IconValue;
import org.jdesktop.swingx.renderer.IconValues;
import org.jdesktop.swingx.renderer.JRendererCheckBox;
import org.jdesktop.swingx.renderer.MappedValue;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.renderer.StringValues;
import org.jdesktop.swingx.table.TableColumnExt;


import systemEinstellungen.SystemConfig;
import systemTools.Colors;
import systemTools.JRtaTextField;
import systemTools.StringTools;
import terminKalender.ParameterLaden;
import terminKalender.datFunk;
import terminKalender.zeitFunk;

import sqlTools.ExUndHop;
import sqlTools.SqlInfo;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
@SuppressWarnings({ "unchecked", "unused" })
public class SuchenSeite extends JXPanel implements TableModelListener,FocusListener, ActionListener,PropertyChangeListener, KeyListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static SuchenSeite thisClass = null; 
	private JXPanel panLinks = null;
	private JXPanel panRechts =  null;
	private JRtaTextField sucheName = null;
	private JRtaTextField sucheNummer = null;
	private JRtaTextField schreibeName = null;
	private JRtaTextField schreibeNummer = null;
	
	private JButton sucheStarten = null;
	private JButton sucheWeiter = null;	
	private JButton sucheStoppen = null;	
	private JButton auswahlUebernahme = null;	
	private JButton auswahlDrucken = null;	
	private JButton auswahlInDatei = null;
	private JButton auswahlPerEmail = null;
	private JButton allesMarkieren = null;	
	private JButton allesEntmarkieren = null;	
	private JButton allesZuruecksetzen = null;
	private JProgressBar fortschritt = null;
	
	private JLabel startLbl = null;
	private JLabel stopLbl = null;
	private JLabel aktLbl = null;
	
	private JLabel trefferLbl = null;	
	private JLabel ausgewaehltLbl = null;
	public  static JLabel verarbeitetLbl = null;
	public JXTable jxSucheTable = null;
	
	//private MyRoogleSuche myTable = null;
	public DefaultTableModel dtblm = null;
	/****************************/
	public String aktuellesDatum = null;
	public static boolean mussUnterbrechen = false;
	private int[] suchenTage = {0,0,0,0,0,0,0};
	public static Vector sucheDaten = new Vector();
	public static Rdaten rooDaten = new Rdaten();
	public Vector vecWahl = new Vector();
	private boolean suchelaeuft = false;
	public Object[][] sucheKollegen = null;
	public HashMap<String,Integer> hZeiten = null;
	private int gewaehlt = 0;
	public ArrayList<Boolean> selbstGesperrt;
	public static boolean verarbeitenBeendet = false;
	private String zeit = "";
	private int zeilengewaehlt = 0;
	public static boolean schicht;
	public static boolean selektiv;
	public static String[] selectUhr = {null,null,null,null}; 
	public static boolean[] selectWal = {false,false,false,false};
	public static String[] schichtUhr = {null,null}; 
	public static boolean[] schichtWal = {false,false};	
	public static boolean[] schichtVor = {false,false};
	public String[] kollegenAbteilung = null;
	public boolean[] kollegenSuchen = null;
	public WorkerTabelle wt = null;
	public JScrollPane ptc = null;
	public boolean workerfertig = true;
	public static String selektbeginn;
	public static String schichtbeginn;
	SuchenSeite(){
		super();
		setBorder(null);
		setLayout(new GridBagLayout());
		thisClass = this;
		new Thread(){
			public void run(){
				erstelleGridBag();
				validate();
				addFocusListener(thisClass);
				addPropertyChangeListener(thisClass);
			}
		}.start();
		
		
	}

	public static void setWorkerFertig(boolean wert){
		thisClass.workerfertig = wert;
	}
	public static SuchenSeite getInstance(){
		return thisClass;
	}
	public static void setzeDatum(String sdat){
		thisClass.aktLbl.setText(sdat);
	}
	public static void setKollegenSuchen(boolean[] kollSuchen){
		thisClass.kollegenSuchen = kollSuchen; 
	}
	public static boolean getKollegenSuchen(int koll){
		return thisClass.kollegenSuchen[koll]; 
	}
	public static void setKollegenAbteilung(String[] kolls){
		thisClass.kollegenAbteilung = kolls;
	}
	public static String getKollegenAbteilung(int koll){
		return thisClass.kollegenAbteilung[koll];
	}
	public static void setzeTreffer(int treffer){
		thisClass.trefferLbl.setText(new Integer(treffer).toString());
	}
	public void setzeZeilenAusgewaehlt(int gewaehlt){
		thisClass.ausgewaehltLbl.setText(new Integer(gewaehlt).toString());
	}
	public static String getStartDatum(){
		return thisClass.startLbl.getText();
	}
	public static String getStopDatum(){
		return thisClass.stopLbl.getText();
	}
	public static String getAktDatum(){
		return thisClass.aktLbl.getText();
	}
	public static String getSuchName(){
		return thisClass.sucheName.getText();
	}
	public static String getSuchNummer(){
		return thisClass.sucheNummer.getText();
	}
	public static void setSucheBeendet(){
		mussUnterbrechen = true;
		thisClass.sucheStoppen.setEnabled(false);
		thisClass.sucheStarten.setEnabled(true);				
	}
	public static boolean getSucheBeendet(){
		return mussUnterbrechen;
	}
	public static void setFortschrittRang(int von, long bis){
		thisClass.fortschritt.setForeground(Color.RED);
		thisClass.fortschritt.setMinimum(von);
		thisClass.fortschritt.setMaximum((int) bis);
	}
	public static void setFortschrittSetzen(int wert){
		thisClass.fortschritt.setValue(wert);
	}
	public static void setFortschrittZeigen(boolean zeigen){
		thisClass.fortschritt.setVisible(zeigen);		
	}


	synchronized public static void setDatenVector(Vector vec){
		thisClass.sucheDaten.add(vec);
	}
	synchronized public static Vector getDatenVector(){
		return (Vector)thisClass.sucheDaten;
	}
	public static void verarbeitungEinschalten(){
		/*
			SperrenVerarbeiten svr = new SperrenVerarbeiten();
			svr.initValue(0);
			svr.execute();
		*/	
	}
	
	public static void setKollegenEinstellen(Object[][] obj){
		thisClass.sucheKollegen = obj.clone();
	}
	public static Object[][] getKollegenEinstellen(){
		return thisClass.sucheKollegen;
	}
	public static void setKollegenZeiten(HashMap<String,Integer> hZeiten){
		thisClass.hZeiten = ((HashMap<String,Integer>)hZeiten.clone()); 
		
	}
	public static HashMap<String,Integer> getKollegenZeiten(){
		return thisClass.hZeiten; 
		
	}
	public static void setGewaehlt(int gewaehlt){
		thisClass.gewaehlt = gewaehlt;
		//System.out.println(gewaehlt);
	}
	public static int getGewaehlt(){
		return thisClass.gewaehlt;
	}
	public static void setZeit(){
		thisClass.zeit = new Long(System.currentTimeMillis()).toString();	
	}
	public static String getZeit(){
		return thisClass.zeit;	
	}
	
	public static int getTreffer(){
		return new Integer(thisClass.trefferLbl.getText());
	
	}
	public static boolean tagDurchsuchen(String sdatum){
		boolean ret = false;
		if(thisClass.suchenTage[datFunk.TagDerWoche(sdatum)-1]==0){
			ret = false;
		}else{
			ret = true;
		}
		return ret;
	}
	public static void datumEinstellen(){
		thisClass.startLbl.setText(RoogleFenster.thisClass.zeitraumEdit[0].getText());
		thisClass.stopLbl.setText(RoogleFenster.thisClass.zeitraumEdit[1].getText());		
	}
	public static void tageEinstellen(){
		for(int i = 0;i<7;i++){
			thisClass.suchenTage[i] = (RoogleFenster.thisClass.tageCheck[i].isSelected() ? 1 : 0);
		}
		thisClass.sucheName.requestFocus();
	}
	public static void behandlerEinstellen(){
	}

	public static void zeileTesten(Vector vtest){
		//((MyRoogleSuche) thisClass.jxSucheTable.getModel()).addDaten(vtest);
	}
	public void tabelleAusschalten(){
		jxSucheTable.setEditable(false);
		jxSucheTable.setRowSelectionAllowed(false);
	}
	public void tabelleEinschalten(){
		jxSucheTable.setEditable(true);
		jxSucheTable.setRowSelectionAllowed(true);
	}
	
	void listenerEinschalten(){
		dtblm.addTableModelListener(this);
	}
	void listenerAusschalten(){
		dtblm.removeTableModelListener(this);
	}
	public void cursorWait(boolean ein){
		if(!ein){
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}else{
			this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		}
	}

	
	/******************************************/
	
	private void erstelleGridBag(){

		GridBagConstraints cc = new GridBagConstraints();
		
		cc.anchor = GridBagConstraints.FIRST_LINE_START;
		cc.gridx = 0;
		cc.gridy = 0;
		cc.gridwidth = 1;
		cc.gridheight = 1;
		cc.weightx = 0.00;
		cc.weighty = 1.00;		
		cc.fill = GridBagConstraints.BOTH;
		add(machePaneLinks(),cc);

		cc.anchor = GridBagConstraints.PAGE_START;		
		cc.gridx = 1;
		cc.gridy = 0;
		cc.gridwidth = 3;
		cc.gridheight = 1;		
		cc.weightx = 3.75;
		cc.weighty = 1.00;		
		cc.fill = GridBagConstraints.BOTH;		
		add(machePaneRechts(),cc);
		validate();
	}
	
	private JXPanel machePaneLinks(){
		panLinks = new JXPanel(new BorderLayout());
		panLinks.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
		Point2D start = new Point2D.Float(0, 0);
	     Point2D end = new Point2D.Float(200,120);
	     //Point2D end = new Point2D.Float(getParent().getParent().getWidth(),getParent().getParent().getHeight());
	     float[] dist = {0.0f, 0.5f};
	     Color[] colors = {Colors.TaskPaneBlau.alpha(1.0f), Color.WHITE};
	     //Color[] colors = {Color.WHITE,getBackground()};
	     LinearGradientPaint p =
	         new LinearGradientPaint(start, end, dist, colors);
	     MattePainter mp = new MattePainter(p);
	     panLinks.setBackgroundPainter(new CompoundPainter(mp));

		//panLinks.setBackground(Color.WHITE);
		JScrollPane jscr = new JScrollPane();
		jscr.setOpaque(false);
		jscr.getViewport().setOpaque(false);
		jscr.setBorder(null);
		//jscr.setViewportView(new JXPanel());
		jscr.setViewportView(formLinks());
		panLinks.add(jscr,BorderLayout.CENTER);
		panLinks.validate();
		return panLinks;
	}
	private JXPanel machePaneRechts(){
		panRechts = new JXPanel(new BorderLayout());
		panRechts.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		panRechts.setBackground(Color.WHITE);
		panRechts.validate();
		
		JScrollPane jscr = new JScrollPane();
		jscr.setBorder(null);
		jscr.setBackground(Color.WHITE);
		jscr.setViewportView(formRechts());
		panRechts.add(jscr,BorderLayout.CENTER);
		
		//panRechts.add(formRechts(),BorderLayout.CENTER);
		return panRechts;
	}
	private JPanel formLinks(){
		//                 1    2       3    4      5    6 
		String spalten = "5dlu,70dlu:g,2dlu,30dlu:g,3dlu,p";
		//                1      2  3  4   5  6    7  8   9  10 11  12   13  14  15   16   17    18   19   20   21   22   23   24    25  26   27
		/*
		String reihen = "10dlu,2dlu, p,2dlu,p,13dlu,p,2dlu,p,2dlu,p,10dlu, p, 2dlu,p, 10dlu, p , 2dlu, p , 2dlu, p , 2dlu , p, 8dlu, p, 8dlu, p ,"  +
		//				  28   29  30     31    32   33   34    35   36    37  38  39
						"2dlu , p, 2dlu , p ,  8dlu,  p , 2dlu, p , 8dlu , p, 8dlu,p";
		*/
		//                1      2   3  4   5  6    7  8   9  10 11  12   13  14  15   16   17     f1   f2    18   19   20   21   22   23   24    25  26   27
		String reihen = "10dlu,2dlu, p,2dlu,p,13dlu,p,2dlu,p,2dlu,p,10dlu, p, 2dlu,p, 10dlu, p  ,2px,  8dlu ,2px, p , 2dlu, p , 8dlu , p, 8dlu, p, 8dlu, p ,"  +
		//				  28   29  30     31    32   33   34    35   36    37  38  39
						"2dlu , p, 2dlu , p ,  8dlu,  p , 2dlu, p , 8dlu , p";
		
		FormLayout lay = new FormLayout(spalten,reihen);
		PanelBuilder builder = new PanelBuilder(lay);
		builder.setDefaultDialogBorder();
		builder.getPanel().setOpaque(false);
		//builder.getPanel().setBackground(Color.WHITE);
		CellConstraints cc = new CellConstraints();
		builder.addSeparator("nach wem soll gesucht werden?", cc.xyw(1, 1,5));

		//builder.addLabel("Name suchen (leer = freie Termine)",cc.xy(2, 3));
		//builder.addLabel("Rez.Nr.",cc.xy(4, 3));
		
		sucheName = new JRtaTextField("GROSS",true);
		sucheName.setToolTipText("Geben Sie hier Ihr Suchkriterim ein, leer = freie Termine suchen!");
		//SuchenSeite.thisClass.schreibeName.setText(drops[0]);
		//SuchenSeite.thisClass.schreibeNummer.setText(drops[1]);

		builder.add(sucheName,cc.xy(2,5));

		sucheNummer = new JRtaTextField("GROSS",true);
		sucheNummer.setToolTipText("Hier können Sie nach einer bestimmten Rezeptnummer suchen");
		builder.add(sucheNummer,cc.xyw(4,5,3));

		builder.addSeparator("mit was überschreiben?", cc.xyw(1, 7,5));
		
		schreibeName = new JRtaTextField("GROSS",true);
		schreibeName.setToolTipText("Mit diesem Feld können Sie bestimmen mit was oder wem (evtl.) später Termineinträge überschreiben werden");
		builder.add(schreibeName,cc.xy(2,11));

		schreibeNummer = new JRtaTextField("GROSS",true);
		schreibeNummer.setToolTipText("Mit diesem Feld geben Sie an welche Rezeptnummer (evtl.) später eingetragen werden soll");
		builder.add(schreibeNummer,cc.xyw(4,11,3));

		if(RoogleFenster.gedropt){
			if(RoogleFenster.sldrops[0].length() > 10){
				sucheName.setText(new String(RoogleFenster.sldrops[0].substring(0,10)));			
			}else{
				sucheName.setText(new String(RoogleFenster.sldrops[0]));
			}
			schreibeName.setText(new String(RoogleFenster.sldrops[0]));
			schreibeNummer.setText(new String(RoogleFenster.sldrops[1]));
		}
		builder.addSeparator("suche von / bis...", cc.xyw(1, 13,5));
		
		JXPanel dummy = new JXPanel();
		dummy.setBorder(null);
		dummy.setOpaque(false);
		//dummy.setBackground(Color.WHITE);
		//                   1     2       3          4       5   6     
		String dspalten = "5dlu,70dlu:g,right:5dlu:g,5dlu:g,3dlu,right:p";
		//                1      2   3   4  5  6   7  8   9  10 11  12  13  14  15
		String dreihen = "0dlu,2dlu,p,2dlu,p,2dlu, p"; // ,2dlu,p,2dlu,p,2dlu, p, 2dlu,p";
		FormLayout dlay = new FormLayout(dspalten,dreihen);
		dummy.setLayout(dlay);
		
		CellConstraints dcc = new CellConstraints();
		dummy.add(new JLabel("Start bei Datum:"), dcc.xy(2,3));
		dummy.add(new JLabel("Stop  bei Datum:"), dcc.xy(2,5));
		dummy.add(new JLabel("Aktuelles Datum:"), dcc.xy(2,7));		
		startLbl = new JLabel("01.01.2008");
		startLbl.setForeground(Color.BLUE);
		stopLbl = new JLabel("01.01.2008");		
		stopLbl.setForeground(Color.BLUE);		
		aktLbl = new JLabel("");
		aktLbl.setForeground(Color.RED);
		dummy.add(startLbl, dcc.xyw(6,3,1));
		dummy.add(stopLbl, dcc.xyw(6,5,1));
		dummy.add(aktLbl, dcc.xyw(6,7,1));		
		
		builder.add(dummy,cc.xyw(2,15,4));
		
		builder.addSeparator("Funktionsaufrufe", cc.xyw(1, 17,5));	
		/*********************************************/
		/*********************************************/
		sucheStarten = new JButton("Suchlauf starten");
		sucheStarten.setIcon(new ImageIcon( Reha.proghome+"icons/buttongreen.png") );
		sucheStarten.setMnemonic(KeyEvent.VK_S);
		sucheStarten.setName("start");
		sucheStarten.addKeyListener(this);
		sucheStarten.setActionCommand("sstart");
		sucheStarten.addActionListener(this);
		
		sucheWeiter = new JButton("Auswahl nur(!!) drucken");
		sucheWeiter.setEnabled(false);
		sucheWeiter.setName("sucheWeiter");
		sucheWeiter.addKeyListener(this);		
		sucheWeiter.setActionCommand("nurdrucken");
		sucheWeiter.addActionListener(this);
		
		sucheStoppen = new JButton("Suche unterbrechen");
		sucheStoppen.setIcon(new ImageIcon(Reha.proghome+"icons/buttonred.png") );
		sucheStoppen.setName("stop");
		sucheStoppen.addKeyListener(this);
		sucheStoppen.setEnabled(false);
		sucheStoppen.setActionCommand("sstop");
		sucheStoppen.addActionListener(this);		
	
		auswahlUebernahme = new JButton("Auswahl übernehmen");
		auswahlUebernahme.setActionCommand("uebernahme");
		auswahlUebernahme.addActionListener(this);
		
		auswahlDrucken = new JButton("Term.Liste drucken");
		auswahlDrucken.setActionCommand("drucken");
		auswahlDrucken.addActionListener(this);
		
		auswahlPerEmail = new JButton("per Email senden");
		auswahlPerEmail.setActionCommand("email");
		auswahlPerEmail.addActionListener(this);
		
		auswahlInDatei = new  JButton("Terminliste exportieren");
		auswahlInDatei.setActionCommand("export");
		auswahlInDatei.addActionListener(this);
		
		allesMarkieren = new JButton("alles markieren");
		allesMarkieren.setActionCommand("alles markieren");
		allesMarkieren.addActionListener(this);

		allesEntmarkieren = new JButton("alle entmarkieren");		
		allesEntmarkieren.setActionCommand("alles entmarkieren");
		allesEntmarkieren.addActionListener(this);

		allesZuruecksetzen = new JButton("alles zurücksetzen");
		allesZuruecksetzen.setName("zurueck");
		allesZuruecksetzen.addKeyListener(this);		
		allesZuruecksetzen.setActionCommand("zuruecksetzen");
		allesZuruecksetzen.setMnemonic(KeyEvent.VK_Z);
		allesZuruecksetzen.setForeground(Color.RED);
		allesZuruecksetzen.addActionListener(this);		
		
		fortschritt = new JProgressBar();
		fortschritt.setStringPainted(true);

		fortschritt.setVisible(false);
		builder.add(fortschritt,cc.xyw(2,19,4));

		builder.add(sucheStarten,cc.xyw(2,21, 4));
		builder.add(sucheStoppen,cc.xyw(2,23, 4));		
		builder.add(sucheWeiter,cc.xyw(2,25, 4));		
		builder.add(auswahlUebernahme,cc.xyw(2,27, 4));
		builder.add(auswahlDrucken,cc.xyw(2,29, 4));
		builder.add(auswahlPerEmail,cc.xyw(2,31, 4));	
		builder.add(auswahlInDatei,cc.xyw(2,33, 4));
		
		builder.add(allesMarkieren,cc.xyw(2,35, 4));		
		builder.add(allesEntmarkieren,cc.xyw(2,37, 4));		
		builder.add(allesZuruecksetzen,cc.xyw(2,39, 4));	
		
		
		SuchenSeite.setKnopfGedoense(new int[]  {1,0,0,0,0,0,0,0,0,0});
		//builder.getPanel().addKeyListener(this);
		return builder.getPanel();
	}
	private JPanel formRechts(){
		String spalten = "5dlu,320dlu:g";
		String reihen = "p,1dlu,p:g,2dlu";
		FormLayout lay = new FormLayout(spalten,reihen);
		PanelBuilder builder = new PanelBuilder(lay);
		builder.getPanel().setBackground(Color.WHITE);
		//builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		JXPanel fpan = new JXPanel(new FlowLayout(FlowLayout.LEFT));
		fpan.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		JLabel lab = new JLabel("Treffer: ");
		lab.setForeground(Color.BLUE);
		fpan.add(lab);
		trefferLbl = new JLabel("0");
		trefferLbl.setForeground(Color.BLUE);
		fpan.add(trefferLbl);
		fpan.add(new JLabel("           "));
		fpan.add(new JLabel("Ausgewählt: "));
		ausgewaehltLbl = new JLabel("0");
		fpan.add(ausgewaehltLbl);
		fpan.add(new JLabel("           "));
		JLabel verarb = new JLabel("Verarbeitet: ");
		verarb.setForeground(Color.RED);
		fpan.add(verarb);
		verarbeitetLbl = new JLabel("0");
		verarbeitetLbl.setForeground(Color.RED);
		fpan.add(verarbeitetLbl);
		builder.add(fpan,cc.xy(2,1));
		
/*
		jxSucheTable = new JXTable();
		jxSucheTable.setDoubleBuffered(true);
		new Thread(){
			public void run(){
				TabelleSetzen(0);
				jxSucheTable.validate();
			}	
		}.start();
		
*/        
		dtblm = new MyDefaultTableModel();
		//dtblm.addTableModelListener(this);
		//					 1     2     3       4     5     6       7       8          9	    10        11 
		String[] column = 	{"x?","G!","Datum","von","bis","Min.","Beginn","Dauer.","Namen","Rez.Nr.","Behandler",
		//		  12          13      14      15        16          17       18     19
				"Druckzeit","Sort","Spalte","sDatum","sOrigDatum","iBlock","id","maxblock",""};
		dtblm.setColumnIdentifiers(column);
		jxSucheTable = new JXTable(dtblm);
		jxSucheTable.setDoubleBuffered(true);
		/***************************/		
		//jxSucheTable.setHighlighters(HighlighterFactory.createSimpleStriping());
		jxSucheTable.setHighlighters(HighlighterFactory.createSimpleStriping(new Color(204,255,255)));
		//jxSucheTable.addHighlighter(new ColorHighlighter(Color.LIGHT_GRAY, Color.WHITE,HighlightPredicate.EVEN));
		JCheckBox check = new JCheckBox();
		//DefaultCellEditor edi = new DefaultCellEditor(check);
		//jxSucheTable.getColumnModel().getColumn(0).setCellEditor(edi);
		((TableColumnExt)jxSucheTable.getColumn(0)).setCellEditor(new JXTable.BooleanEditor());
//		((TableColumnExt)jxSucheTable.getColumn(0)).setCellRenderer(new TableCellRenderer()); //.setCellRenderer(new JXTable.BooleanEditor());		
		//jxSucheTable.getColumnModel().getColumn(0).setCellRenderer( (TableCellRenderer) new JXTable.BooleanEditor());		
		//x = Gewählt in ComboBox
		jxSucheTable.getColumn(0).setMinWidth(25);	
		jxSucheTable.getColumn(0).setMaxWidth(25);
		//StringValue.EMPTY, IconValue.ICON
		//TableCellRenderer renderer = new DefaultTableRenderer();
		//TableCellRenderer renderer = new DefaultTableRenderer(new MappedValue(StringValue.EMPTY, IconValue.NULL_ICON), JLabel.CENTER);
		TableCellRenderer renderer = new DefaultTableRenderer(new MappedValue(StringValues.EMPTY, IconValues.ICON), JLabel.CENTER);
		// original   TableCellRenderer renderer = new DefaultTableRenderer(new MappedValue(StringValue.EMPTY, IconValue.ICON), JLabel.CENTER);

		// ImageIcon für gesperrt oder nicht
		jxSucheTable.getColumn(1).setCellRenderer(renderer);		
		jxSucheTable.getColumn(1).setMinWidth(20);	
		jxSucheTable.getColumn(1).setMaxWidth(20);
		//jxSucheTable.getColumn(1).putPorperty(CELL_RENDERER_PROPERTY);
		//Datum
		jxSucheTable.getColumn(2).setMinWidth(80);
		jxSucheTable.getColumn(2).setMaxWidth(80);
		//Beginn
		jxSucheTable.getColumn(3).setMinWidth(40);	
		jxSucheTable.getColumn(3).setMaxWidth(40);
		DefaultTableCellRenderer crenderer = new DefaultTableCellRenderer();
		crenderer.setHorizontalAlignment(JLabel.CENTER);
		jxSucheTable.getColumn(3).setCellRenderer(crenderer);
		//Ende
		jxSucheTable.getColumn(4).setMinWidth(40);	
		jxSucheTable.getColumn(4).setMaxWidth(40);
		jxSucheTable.getColumn(4).setCellRenderer(crenderer);
		//Länge des Termins
		jxSucheTable.getColumn(5).setMinWidth(40);	
		jxSucheTable.getColumn(5).setMaxWidth(40);
		jxSucheTable.getColumn(5).setCellRenderer(crenderer);		
		//Beginn - Termin zum schreiben
		DefaultTableCellRenderer farbrenderer = new DefaultTableCellRenderer();
		farbrenderer.setForeground(Color.BLUE);
		farbrenderer.setHorizontalAlignment(JLabel.CENTER);
		jxSucheTable.getColumn(6).setMinWidth(40);
		jxSucheTable.getColumn(6).setMaxWidth(40);
		jxSucheTable.getColumn(6).setCellRenderer(farbrenderer);
		((TableColumnExt)jxSucheTable.getColumn(6)).setCellEditor((TableCellEditor) new ZeitTableCellEditor());		
		//Dauer - Termin zum schreiben
		jxSucheTable.getColumn(7).setMinWidth(35);
		jxSucheTable.getColumn(7).setMaxWidth(35);
		jxSucheTable.getColumn(7).setCellRenderer(farbrenderer);		
		((TableColumnExt)jxSucheTable.getColumn(7)).setCellEditor((TableCellEditor) new ZahlTableCellEditor());		
		//Name
		jxSucheTable.getColumn(8).setMinWidth(80);	
		//Rezeptnummer
		jxSucheTable.getColumn(9).setMinWidth(55);	
		jxSucheTable.getColumn(9).setMaxWidth(55);
		//Behandler
		jxSucheTable.getColumn(10).setCellRenderer(new ToolTipRenderer(new Color(204,255,255)));
		jxSucheTable.getColumn(10).setMinWidth(65);	
		jxSucheTable.getColumn(10).setMaxWidth(65);
		//jxSucheTable.getColumn(10).setCellRenderer( new ToolTipRenderer());
		//Druckzeit
		jxSucheTable.getColumn(11).setMinWidth(45);	
		jxSucheTable.getColumn(11).setMaxWidth(45);
		jxSucheTable.getColumn(11).setCellRenderer(crenderer);
		((TableColumnExt)jxSucheTable.getColumn(11)).setCellEditor((TableCellEditor) new ZeitCancelCellEditor());
		// übrige daten sind versteckt
		jxSucheTable.getColumn(12).setMinWidth(0);	
		jxSucheTable.getColumn(12).setMaxWidth(0);		
		jxSucheTable.getColumn(13).setMinWidth(0);	
		jxSucheTable.getColumn(13).setMaxWidth(0);
		jxSucheTable.getColumn(14).setMinWidth(0);	
		jxSucheTable.getColumn(14).setMaxWidth(0);		
		jxSucheTable.getColumn(15).setMinWidth(0);	
		jxSucheTable.getColumn(15).setMaxWidth(0);		
		jxSucheTable.getColumn(16).setMinWidth(0);	
		jxSucheTable.getColumn(16).setMaxWidth(0);		
		jxSucheTable.getColumn(17).setMinWidth(0);	
		jxSucheTable.getColumn(17).setMaxWidth(0);
		jxSucheTable.getColumn(18).setMinWidth(0);	
		jxSucheTable.getColumn(18).setMaxWidth(0);		
		jxSucheTable.getColumn(19).setMinWidth(0);	
		jxSucheTable.getColumn(19).setMaxWidth(0);
		jxSucheTable.setColumnControlVisible(false);
		
		jxSucheTable.setEditable(true);
		jxSucheTable.setSortable(false);
		jxSucheTable.validate();
		jxSucheTable.setName("RoogleSuche");
		
		//jxSucheTable.addTableModelListener(this);
		/***************************/
		ptc = new JScrollPane();
        ptc.setBackground(Color.WHITE);
        ptc.setViewportBorder(null);
        ptc.setViewportView(jxSucheTable);
        ptc.revalidate();
		
		
		
		builder.add(ptc,cc.xywh(2, 3, 1, 2));

		return builder.getPanel();
		
	}
	/******************************************/
	
	

	@Override
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		startLbl.setText(RoogleFenster.thisClass.zeitraumEdit[0].getText());
		stopLbl.setText(RoogleFenster.thisClass.zeitraumEdit[1].getText());		
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		String name = arg0.getActionCommand();
		if(name.equals("")){return;}
		for(int i = 0;i<1;i++){
			
			if(name.equals("sstart")){
				knopfGedoense(new int[]  {0,1,0,0,0,0,0,0,0,0});
				roogleStarten();
				sucheStoppen.requestFocus();
				break;
			}
			if(name.equals("sstop")){
				knopfGedoense(new int[]  {0,0,1,0,0,0,0,1,1,1});
				roogleStoppen();
				break;
			}
			if(name.equals("zuruecksetzen")){
				knopfGedoense(new int[]  {1,0,0,0,0,0,0,0,0,0});
				roogleZuruecksetzen();
				Runtime r = Runtime.getRuntime();
			    r.gc();
				break;
			}
			if(name.equals("alles markieren")){
				allesMark(true);
				break;
			}
			if(name.equals("alles entmarkieren")){
				allesMark(false);
				break;
			}
			if(name.equals("uebernahme")){
				if(gewaehlt==0){
					JOptionPane.showMessageDialog(null,"So so...,Sie haben zwar nix gewählt wollen es aber schon mal übernehmen - das NIX....\nOh Herr schmeiß Hirn ra!");
					return;
				}
				if((((String)jxSucheTable.getValueAt(0, 8)).trim().equals("")) && (((String)jxSucheTable.getValueAt(0, 9)).trim().equals("")) && 
						(schreibeName.getText().trim().equals("")) && (schreibeNummer.getText().trim().equals("")) ){
					JOptionPane.showMessageDialog(null,"So so...,Sie suchen 'freie Termine' und wollen die dann mit 'freien Terminen' überschreiben...\nOh Herr schmeiß Hirn ra!");
					return;
					
				}
				SwingUtilities.invokeLater(new Runnable(){
					public  void run(){
						SchreibeAuswahl sa = new SchreibeAuswahl();
						sa.execute();
						knopfGedoense(new int[]  {0,0,0,0,1,1,1,0,0,1});
						//auswahlSchreiben();
					}
				});	
				break;
			}
			if(name.equals("drucken")){
				if(gewaehlt==0){
					JOptionPane.showMessageDialog(null,"So so...,Sie haben zwar nix gewählt wollen es aber schon mal drucken - das NIX....\nOh Herr schmeiß Hirn ra!");
					return;
				}
				cursorWait(true);
				SwingUtilities.invokeLater(new Runnable(){
					public  void run(){
						new Thread(){
							public void run(){
								auswahlDrucken(true);								
							}
						}.start();

						//auswahlSchreiben();
					}
				});	
				break;
			}
			if(name.equals("nurdrucken")){
				if(gewaehlt==0){
					JOptionPane.showMessageDialog(null,"So so...,Sie haben zwar nix gewählt wollen es aber schon mal drucken - das NIX....\nOh Herr schmeiß Hirn ra!");
					return;
				}
				String fragestring = "Achtung Sie schreiben keinerlei Daten in den Kalender!!!!!!\n"+
				"Sie erstellen lediglich einen Ausdruck Ihrer derzeitigen Auswahl\n\n"+
				"Wollen Sie den Vorgang fortsetzen ?";
				int anfrage = JOptionPane.showConfirmDialog(null, fragestring, "Achtung wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION); 
				if(anfrage == JOptionPane.NO_OPTION){
					sucheWeiter.setEnabled(false);
					return;
				}
				cursorWait(true);
				SwingUtilities.invokeLater(new Runnable(){
					public  void run(){
						new Thread(){
							public void run(){
								druckVectorInit();
								auswahlDrucken(true);								
							}
						}.start();
					}
				});	
				break;
				
			}
			if(name.equals("email")){
				if(gewaehlt==0){
					JOptionPane.showMessageDialog(null,"So so...,Sie haben zwar nix gewählt wollen es aber schon mal drucken - das NIX....\nOh Herr schmeiß Hirn ra!");
					return;
				}
				cursorWait(true);
				SwingUtilities.invokeLater(new Runnable(){
					public  void run(){
						new Thread(){
							public void run(){
								auswahlDrucken(false);								
							}
						}.start();

						//auswahlSchreiben();
					}
				});	
				break;
			}

		}
	}
	private void knopfGedoense(int[] knopf){
		while(sucheStarten==null){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		sucheStarten.setEnabled((knopf[0]==1 ? true : false));
		sucheStoppen.setEnabled((knopf[1]==1 ? true : false));
		sucheWeiter.setEnabled((knopf[2]==1 ? true : false));	
		auswahlUebernahme.setEnabled((knopf[3]==1 ? true : false));
		auswahlDrucken.setEnabled((knopf[4]==1 ? true : false));
		auswahlPerEmail.setEnabled((knopf[5]==1 ? true : false));
		auswahlInDatei.setEnabled((knopf[6]==1 ? true : false));		
		allesMarkieren.setEnabled((knopf[7]==1 ? true : false));		
		allesEntmarkieren.setEnabled((knopf[8]==1 ? true : false));		
		allesZuruecksetzen.setEnabled((knopf[9]==1 ? true : false));		
	}
	public static void setKnopfGedoense(int[] knopf){
		thisClass.knopfGedoense(knopf);
	}
	private void roogleStarten(){
		listenerAusschalten();
		this.zeilengewaehlt = 0;
		setzeZeilenAusgewaehlt(this.zeilengewaehlt);
		EntsperreSatz es = new EntsperreSatz();
		es.start();
		SwingUtilities.invokeLater(new Runnable(){
			public  void run(){
				aktLbl.setText(getStartDatum());
				mussUnterbrechen = false;
				
				sucheStoppen.setEnabled(true);
				sucheStarten.setEnabled(false);		
				dtblm.getDataVector().clear();
				sucheDaten.clear();
				sucheDaten.trimToSize();
				
				jxSucheTable.clearSelection();
				jxSucheTable.setSelectionMode(0);
				tabelleAusschalten();
				jxSucheTable.repaint();


				/*
				try {
					Reha.thisClass.conn.setAutoCommit(true);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				*/
				WorkerSuchenInKalenderTagen WsIT = new WorkerSuchenInKalenderTagen();
				WsIT.setzeStatement();
				WsIT.execute();
				
				
				SwingUtilities.invokeLater(new Runnable(){
					public  void run(){
						SuchenSeite.getInstance().workerfertig = false;
						SuchenSeite.getInstance().wt = new WorkerTabelle();
						SuchenSeite.getInstance().wt.init();
						SuchenSeite.getInstance().wt.execute();
					}
				});
					
				
				/* bislang o.k.
				new Thread(){
					public void run(){
						SuchenSeite.getInstance().workerfertig = false;
						SuchenSeite.getInstance().wt = new WorkerTabelle();
						SuchenSeite.getInstance().wt.init();
						SuchenSeite.getInstance().wt.execute();
					}
				}.start();
				*/
				
				
				
				sucheStoppen.requestFocus();
				
			}
		});
	}
	private void roogleStoppen(){
		sucheStoppen.setEnabled(false);
		sucheStarten.setEnabled(true);				
		SwingUtilities.invokeLater(new Runnable(){
			public  void run(){
				new Thread(){
				public void run(){
					mussUnterbrechen = true;
					jxSucheTable.setRowSelectionAllowed(true);
					setFortschrittZeigen(false);
				}
			}.start();
		}});
	}
	private void roogleZuruecksetzen(){
		mussUnterbrechen = true;
		/*
		if(trefferLbl.getText().equals(verarbeitetLbl.getText())){
			verarbeitetLbl.setText("0");
		}else{
			JOptionPane.showMessageDialog(null, "Die Verarbeitung des Suchergebnisses ist noch nicht beendet....");
			return;
		}
		*/
		dtblm.getDataVector().clear();
		dtblm.setRowCount(0);
		jxSucheTable.removeAll();
		sucheDaten.clear();
		jxSucheTable.clearSelection();
		jxSucheTable.validate();
		jxSucheTable.repaint();
		EntsperreSatz es = new EntsperreSatz();
		es.start();
		trefferLbl.setText("0");
		ausgewaehltLbl.setText("0");
		vecWahl.clear();
		this.zeilengewaehlt = 0;
		//wt.setEnde();		
		sucheName.requestFocus();
	}

	private void auswahlDrucken(boolean drucken){
		int lang,i;
		TermObjekt termin = null;
		Vector<TermObjekt> vec = new Vector<TermObjekt>();
		/*public String tag;
		public String beginn;
		public String termtext;
		public String sorter;*/
		if(gewaehlt > 0){
			lang = vecWahl.size();
			if(lang==0){
				JOptionPane.showMessageDialog(null,"Sie haben die Auswahl nicht in den Terminplan übernommen!\n"+
						"Ohne die vorherige Übernahme kann weder ein Ausdruck noch eine Email erstellt werden.");
				cursorWait(false);
				return;
			}

			for(i=0;i<lang;i++){
				boolean isdruckzeit = (  ((String)((Vector)vecWahl.get(i)).get(11)).trim().equals("")
								?  false 
								:  true	);
				String druckzeit = (  isdruckzeit
								?  ((String)((Vector)vecWahl.get(i)).get(11)) 
								: 	 ((String)((Vector)vecWahl.get(i)).get(6))	);
				String sorter = ((String)((Vector)vecWahl.get(i)).get(15))+ druckzeit;
				String tag = ((String)((Vector)vecWahl.get(i)).get(2));
				String termtext = null;
				/*
				String termtext	= (isdruckzeit
								?((String)((Vector)vecWahl.get(i)).get(8))
								:((String)((Vector)vecWahl.get(i)).get(10))  );
				*/				
				
				if(isdruckzeit && ((String)((Vector)vecWahl.get(i)).get(12)).trim().equals("")){
					if(((String)((Vector)vecWahl.get(i)).get(8)).contains("\\\\")){
						termtext = ((String)((Vector)vecWahl.get(i)).get(8)).substring(7);
						if(termtext.contains("Gruppe:_")){
							termtext = termtext.substring(8);
						}else{
							termtext = ((String)((Vector)vecWahl.get(i)).get(10));  // neu eingefügt am 06.04.2009
						}
					}else{
						termtext = ((String)((Vector)vecWahl.get(i)).get(8));
						if(termtext.contains("Gruppe:_")){
							termtext = termtext.substring(8);
						}else{
							termtext = ((String)((Vector)vecWahl.get(i)).get(10));  // neu eingefügt am 06.04.2009
						}
					}

				}else{
					termtext = ((String)((Vector)vecWahl.get(i)).get(10));
				}
				vec.add(new TermObjekt(tag,druckzeit,termtext,sorter));
				

				
			}
			Collections.sort(vec);
			new TerminplanDrucken().init((Vector<TermObjekt>)vec.clone(), drucken,schreibeName.getText().trim(),schreibeNummer.getText().trim());
		}
		
	}
	@Override
	public void tableChanged(TableModelEvent arg0) {
		
		//System.out.println("Art der Änderung : "+arg0.getType());
		//System.out.println("Änderung in Zeile: "+arg0.getFirstRow());
		//System.out.println("Änderung in Spalte: "+arg0.getColumn());
		if(arg0.getType() == TableModelEvent.INSERT){
			//System.out.println("Tabellen-Zeile eingefügt");
		}
		if(arg0.getType() == TableModelEvent.UPDATE){

			if(jxSucheTable.isEditable()){
				//System.out.println("Tabellen-Zeile editiert");
				
				if(arg0.getColumn() == 0){
					String sym = ((ImageIcon)jxSucheTable.getValueAt(arg0.getFirstRow(),1)).getDescription();
					if(sym.equals("gesperrt") && ((Boolean)jxSucheTable.getValueAt(arg0.getFirstRow(),0)) ){
						jxSucheTable.setValueAt(new Boolean(false),arg0.getFirstRow(),0);
					}else if(!sym.equals("gesperrt")){
						if( ((Boolean)jxSucheTable.getValueAt(arg0.getFirstRow(),0)) ){
							this.zeilengewaehlt++;
							setzeZeilenAusgewaehlt(this.zeilengewaehlt);
							if(this.zeilengewaehlt==1){
								auswahlUebernahme.setEnabled(true);
								auswahlDrucken.setEnabled(true);
								auswahlPerEmail.setEnabled(true);
								auswahlInDatei.setEnabled(true);
								sucheWeiter.setEnabled(true);
							}
						}else{
							this.zeilengewaehlt--;
							setzeZeilenAusgewaehlt(this.zeilengewaehlt);
							if(this.zeilengewaehlt==0){
								auswahlUebernahme.setEnabled(false);
								auswahlDrucken.setEnabled(false);
								auswahlPerEmail.setEnabled(false);
								auswahlInDatei.setEnabled(false);
								sucheWeiter.setEnabled(false);
							}
						}
					}	
				}
				if(arg0.getColumn() == 11){
					//System.out.println("In Tabelle = "+jxSucheTable.getValueAt(arg0.getFirstRow(), 11));
					if(((String)jxSucheTable.getValueAt(arg0.getFirstRow(), 11)).trim().equals(":")){
						//System.out.println("In Tabelle = "+jxSucheTable.getValueAt(arg0.getFirstRow(), 11));
						jxSucheTable.setValueAt("", arg0.getFirstRow(), 11);
					}

					//System.out.println("In dtblm = "+dtblm.getValueAt(arg0.getFirstRow(), 11));					
				}
				if(arg0.getColumn() == 6){
						//System.out.println("In Tabelle = "+jxSucheTable.getValueAt(arg0.getFirstRow(), 6));
						int test = testeZeiten(arg0.getFirstRow(),arg0.getColumn()); 
						if( test > 0){
							if(test==1){
								JOptionPane.showMessageDialog(null,"Sie haben versucht den Terminstart von den Beginn des verfügbaren Blockes zu setzen");
								jxSucheTable.setValueAt(jxSucheTable.getValueAt(arg0.getFirstRow(),3), arg0.getFirstRow(), 6);
							}
							if(test==2){
								jxSucheTable.setValueAt(jxSucheTable.getValueAt(arg0.getFirstRow(),3), arg0.getFirstRow(), 6);								
							}
	
						}

						// Prüfung einbauen ob Beginnzeit + Dauer nicht Endzeit übersteigt bzw. vor Planbeginnzeit liegt.
						//jxSucheTable.setValueAt("", arg0.getFirstRow(), 6);
				}
				if(arg0.getColumn() == 7){
						//System.out.println("In Tabelle = "+jxSucheTable.getValueAt(arg0.getFirstRow(), 7));
						if(testeZeiten(arg0.getFirstRow(),arg0.getColumn()) > 0){
							jxSucheTable.setValueAt(jxSucheTable.getValueAt(arg0.getFirstRow(),5), arg0.getFirstRow(), 7);	
						}
						//jxSucheTable.setValueAt("", arg0.getFirstRow(), 7);
						// Prüfung einbauen ob Beginnzeit + Dauer nicht Endzeit übersteigt bzw. vor Planbeginnzeit liegt.
				}

			}

		}

		// TODO Auto-generated method stub
		//System.out.println(arg0);
	}
	private int testeZeiten(int reihe, int zeile){
		int ret = 0;
		int tkstart,tkende,plstart,pldauer;
		  tkstart = (int) zeitFunk.MinutenSeitMitternacht(((String)jxSucheTable.getValueAt(reihe, 3)).trim()+":00");
		  tkende = (int) zeitFunk.MinutenSeitMitternacht(((String)jxSucheTable.getValueAt(reihe, 4)).trim()+":00");
		  plstart = (int) zeitFunk.MinutenSeitMitternacht(((String)jxSucheTable.getValueAt(reihe, 6)).trim()+":00");
		  pldauer = new Integer((String)jxSucheTable.getValueAt(reihe, 7));
		  if(plstart < tkstart){
			  return 1;
		  }
		  if((plstart+pldauer) > tkende){
			  return 2;
		  }
		  if(pldauer==0){
			  return 3;
		  }
		return ret;
	}
	private void allesMark(boolean mark){
		int bis = jxSucheTable.getRowCount(),i;
		boolean wert;
		this.zeilengewaehlt = 0;
		boolean gezeigt = false;
		if(mark){
			listenerAusschalten();
			for(i=0;i<bis;i++){
				if(dtblm.getValueAt(i, 1) != null){
					if(((ImageIcon)dtblm.getValueAt(i, 1)).getDescription().equals("offen")){
						dtblm.setValueAt(new Boolean(true),i, 0);
						this.zeilengewaehlt++;
						
					}
					
				}else{
					if(!gezeigt){
						JOptionPane.showMessageDialog(null,"Ein oder mehrere Termine sind noch nicht verifizier \n und können deshalb nicht gewählt werden");
						gezeigt = true;
					}
				}
					
			}
			setzeZeilenAusgewaehlt(this.zeilengewaehlt);
			listenerEinschalten();
			auswahlUebernahme.setEnabled(true);
			auswahlDrucken.setEnabled(true);
			auswahlPerEmail.setEnabled(true);
			auswahlInDatei.setEnabled(true);
			sucheWeiter.setEnabled(true);
		}else{
			listenerAusschalten();			
			for(i=0;i<bis;i++){
				dtblm.setValueAt(new Boolean(false),i, 0);
			}
			this.zeilengewaehlt = 0;
			setzeZeilenAusgewaehlt(this.zeilengewaehlt);
			listenerEinschalten();
			auswahlUebernahme.setEnabled(false);
			auswahlDrucken.setEnabled(false);
			auswahlPerEmail.setEnabled(false);
			auswahlInDatei.setEnabled(false);
			sucheWeiter.setEnabled(false);
		}
		
	}
	private void druckVectorInit(){
		int lang = dtblm.getRowCount(),i;
		int durchlauf = 0;
		Vector vec = null;
		String name,nummer;
		vecWahl.clear();

		name = new String(schreibeName.getText().trim());
		nummer = new String(schreibeNummer.getText().trim().replace("\\", "\\\\") );
		//System.out.println("Rezeptnummer = "+nummer);

		for(i=0;i<lang;i++){
			
			if((Boolean)dtblm.getValueAt(i,0)){

				SuchenSeite.setFortschrittSetzen(durchlauf++);
				vecWahl.add(sucheDaten.get(i));
				((Vector)vecWahl.get(vecWahl.size()-1)).set(11, (String) dtblm.getValueAt(i,11));
				((Vector)vecWahl.get(vecWahl.size()-1)).set(6, (String) dtblm.getValueAt(i,6));		
			}
		}	
	}
	/********************************************************/
	class SchreibeAuswahl extends SwingWorker<Void,Void>{

		@Override
		protected Void doInBackground() throws Exception {
			//Hier wird gelöscht unbedingt vorher fragen bevor
			if(  (schreibeName.getText().trim().equals("")) && (schreibeNummer.getText().trim().equals(""))){
				int anfrage = JOptionPane.showConfirmDialog(null, "Wollen Sie wirklich die ausgewählten Termine löschen (=freigeben) ?", "Achtung wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
				if(anfrage == JOptionPane.NO_OPTION){
					SuchenSeite.setKnopfGedoense(new int[]  {0,0,0,1,1,1,1,1,1,1});
					return null;
				}
			}
			auswahlSchreiben();
			if(  (schreibeName.getText().trim().equals("")) && (schreibeNummer.getText().trim().equals(""))){
				//roogleZuruecksetzen();
			}
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	private void auswahlSchreiben(){
		int lang = dtblm.getRowCount(),i;
		int durchlauf = 0;
		int tkstart,tkdauer,plstart,pldauer;
		Vector vec = null;
		String name,nummer;
		boolean leer = false;

		// this.zeilengewaehlt;
		SuchenSeite.setFortschrittRang(0,this.zeilengewaehlt );
		SuchenSeite.setFortschrittSetzen(0);
		SuchenSeite.setFortschrittZeigen(true);

		vecWahl.clear();
		
		name = new String(schreibeName.getText().trim());
		nummer = new String(schreibeNummer.getText().trim().replace("\\", "\\\\") );
		//System.out.println("Rezeptnummer = "+nummer);

		for(i=0;i<lang;i++){
			
			if((Boolean)dtblm.getValueAt(i,0)){

				SuchenSeite.setFortschrittSetzen(durchlauf++);
				vecWahl.add(sucheDaten.get(i));
				((Vector)vecWahl.get(vecWahl.size()-1)).set(11, (String) dtblm.getValueAt(i,11));
				((Vector)vecWahl.get(vecWahl.size()-1)).set(6, (String) dtblm.getValueAt(i,6));
				//fortschritt.setValue(durchlauf++);
				//System.out.println(vecWahl);

				tkstart = (int) zeitFunk.MinutenSeitMitternacht(((String)jxSucheTable.getValueAt(i, 3)).trim()+":00");
				tkdauer = new Integer((String)jxSucheTable.getValueAt(i, 5));
				plstart = (int) zeitFunk.MinutenSeitMitternacht(((String)jxSucheTable.getValueAt(i, 6)).trim()+":00");
				pldauer = new Integer((String)jxSucheTable.getValueAt(i, 7));
					for(int j = 0;j<1;j++){
						if((tkstart==plstart) && (tkdauer==pldauer)){
							// Beginn und Dauer sind gleich geblieben
							if((name.equals("")) &&	(nummer.equals(""))){
								// Termin wird gelöscht es müssen die Blöcke vorher und nachher getestet werden;
								// deshalb zuerst Vector mit dem Tag holen
								try {
									vec = sucheZeile(i);
									schreibeLoeschen(vec,i,name,nummer);
								} catch (SQLException e) {
									e.printStackTrace();
								}
								break;
							}else{
								// nur Statement bilden und weg damit... kein Vector gedönse
								String snum = new Integer(((Integer) jxSucheTable.getValueAt(i, 16))).toString();
								String stmt = "Update flexkc set T"+snum+"='"+StringTools.EscapedDouble(name)+"', N"+snum+"='"+nummer+"' where id='"+
								((String)jxSucheTable.getValueAt(i, 17)).trim()+"'";
								//System.out.println(stmt);
								try {
									schreibeZeile(stmt);
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								//System.out.println("Beginn und Ende sind gleich geblieben und Überschreibenfelder sind nicht leer");								
								//System.out.println(stmt);
								break;
							}
						}else{
							// Zunächst den Vector mit dem Tag holen der immer überprüft werden muß				
							try {
								vec = sucheZeile(i);
							} catch (SQLException e) {
								e.printStackTrace();
							}
							if((name.equals("")) &&	(nummer.equals(""))){
								JOptionPane.showMessageDialog(null,"Das löschen eines Termin und gleichzeitiges verändern der Startzeit \n"+
										"bzw. der Termindauer, ist in einem - nicht unerheblich Maß - Schwachsinn!\n"+
										"...und deshalb auch in dieser Software-Version nicht vorgesehen");
								//schreibeLoeschen(vec,i,name,nummer);
								break;
							}else{
								// Vector ebenfalls untersuchen;
								//1. bisheriger Beginn/Dauer/Ende ermitteln
								int tkende = (int) zeitFunk.MinutenSeitMitternacht( zeitFunk.MinutenZuZeit(tkstart+tkdauer) );
								int plende = (int) zeitFunk.MinutenSeitMitternacht( zeitFunk.MinutenZuZeit(plstart+pldauer) );

								//2. Abfrage ob Beginn später und Ende früher - Mittelblock erforderlich - 2 neue Blöcke!
								if((tkstart != plstart) && (tkdauer != pldauer) && (tkende != plende)){
									schreibeTermin(vec,i,1,tkstart,tkdauer,tkende,plstart,pldauer,plende,name,nummer);
									break;
								}
								//3. Abfrage ob Beginn gleich und Ende früher - Nachblock erforderlich - 1 neuer Block!
								if((tkstart == plstart) && (tkdauer != pldauer)){
									schreibeTermin(vec,i,2,tkstart,tkdauer,tkende,plstart,pldauer,plende,name,nummer);
									break;
								}
								//4. Abfrage ob Beginn später und Ende gleich - Vorblock erforderlich - 1 neuer Block!
								if((tkstart != plstart) && (tkdauer != pldauer) && (tkende==plende)){
									schreibeTermin(vec,i,3,tkstart,tkdauer,tkende,plstart,pldauer,plende,name,nummer);
									break;
								}
								break;
							}
						}
					}
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		}
		SuchenSeite.setFortschrittZeigen(false);
		
	}
	/*****************************************/
	private void schreibeTermin(Vector vec,int zeile,int art,int tkstart,int tkdauer,int tkende,int plstart,int pldauer,int plende,String name,String nummer){
		int vecgross = vec.size();
		Vector newvec = new Vector();
		int block = (Integer) jxSucheTable.getValueAt(zeile,16);
		int geaendert = new Integer( (String) jxSucheTable.getValueAt(zeile,18) );
		if(vecgross != geaendert ){
			//System.out.println("vermerkt sind "+vecgross+" Blöcke / tatsächlich in der Datenbank sind "+geaendert+" Blöcke");
			block += (vecgross-geaendert);
		}

		
		int i;
		for(int j = 0; j<1;j++){
			if(art==1){
				for(i=0;i<vecgross;i++){
					if(i<(block-1)){
						newvec.add(vec.get(i));
					}else if(i==(block-1)){
						newvec.add(vec.get(i));
						((Vector)newvec.get(i)).set(3,new Integer(plstart-tkstart).toString());
						((Vector)newvec.get(i)).set(4,zeitFunk.MinutenZuZeit(tkstart+(plstart-tkstart)));
						Vector aktvec = new Vector();
						aktvec.add(name);
						aktvec.add(nummer);
						aktvec.add(zeitFunk.MinutenZuZeit(plstart));
						aktvec.add(new Integer(pldauer).toString());
						aktvec.add(zeitFunk.MinutenZuZeit(plstart+pldauer));
						newvec.add(aktvec);
						Vector nachvec = new Vector();
						nachvec.add( ((Vector)vec.get(i)).get(0));
						nachvec.add( ((Vector)vec.get(i)).get(1));
						nachvec.add( aktvec.get(4));
						String planende = (String)jxSucheTable.getValueAt(zeile,4)+":00";
						
						nachvec.add( new Integer((int) zeitFunk.MinutenSeitMitternacht(planende) - 
								(int)zeitFunk.MinutenSeitMitternacht((String)nachvec.get(2)) ).toString() ) ;					
						nachvec.add(planende );
						
						newvec.add(nachvec);
					}else if (i>(block-1)){
						newvec.add(vec.get(i));
					}
				}
				String stmt = macheStat(newvec,new Integer((String)jxSucheTable.getValueAt(zeile,17)),name,nummer);
				//System.out.println("Mit vor und nachblock "+stmt);
				try {
					schreibeZeile(stmt);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
			if(art==2){
				for(i=0;i<vecgross;i++){
					if(i==(block-1)){
						Vector aktvec = new Vector();
						aktvec.add(name);
						aktvec.add(nummer);
						aktvec.add(zeitFunk.MinutenZuZeit(plstart));
						aktvec.add(new Integer(pldauer).toString());
						aktvec.add(zeitFunk.MinutenZuZeit(plstart+pldauer));
						newvec.add(aktvec);
						
						Vector nachvec = new Vector();
						nachvec.add( ((String)((Vector)vec.get(i)).get(0)) ) ;
						nachvec.add( ((String)((Vector)vec.get(i)).get(1)) ) ;
						nachvec.add( (String)aktvec.get(4) ) ;						
						nachvec.add( new Integer(tkdauer-pldauer).toString() ) ;
						nachvec.add( zeitFunk.MinutenZuZeit(tkende) ) ;
						newvec.add(nachvec);

					}else{
						newvec.add(vec.get(i));
					}
					
				}
				String stmt = macheStat(newvec,new Integer((String)jxSucheTable.getValueAt(zeile,17)),name,nummer);
				//System.out.println("Nur mit Nachblock "+stmt);
				try {
					System.out.println("EscapedDouble String = "+stmt);
					schreibeZeile(stmt);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
			if(art==3){
				for(i=0;i<vecgross;i++){
					if(i==(block-1)){
						Vector vorvec = new Vector();
						vorvec.add( ((String)((Vector)vec.get(i)).get(0)) ) ;
						vorvec.add( ((String)((Vector)vec.get(i)).get(1)) ) ;
						vorvec.add( zeitFunk.MinutenZuZeit(tkstart)) ;
						vorvec.add( new Integer(tkdauer-pldauer).toString() ) ;
						vorvec.add( zeitFunk.MinutenZuZeit(plstart)) ;
						newvec.add(vorvec);
						
						Vector aktvec = new Vector();
						aktvec.add(name);
						aktvec.add(nummer);
						aktvec.add(zeitFunk.MinutenZuZeit(plstart));
						aktvec.add(new Integer(pldauer).toString());
						aktvec.add(zeitFunk.MinutenZuZeit(plstart+pldauer));
						newvec.add(aktvec);

					}else{
						newvec.add(vec.get(i));
					}
					
				}
				String stmt = macheStat(newvec,new Integer((String)jxSucheTable.getValueAt(zeile,17)),name,nummer);
				//System.out.println("Nur mit Vorblock "+stmt);
				try {
					schreibeZeile(stmt);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}

		}
		
	}
	/*****************************************/	
	private void schreibeLoeschen(Vector vec,int zeile,String name,String nummer){
		int block = (Integer) jxSucheTable.getValueAt(zeile,16);
		int bloecke = new Integer(vec.size()); 
		int geaendert = new Integer( (String) jxSucheTable.getValueAt(zeile,18) );
		if(bloecke != geaendert ){
			//System.out.println("vermerkt sind "+bloecke+" Blöcke / tatsächlich in der Datenbank sind "+geaendert+" Blöcke");
			block += (bloecke-geaendert);
		}
		String stmt = null;
		// nur den nachfolgenden Block prüfen!!
		for(int i=0;i<1;i++){
			if(block == 1){
				

				// es gibt nur einen Block 
				if(bloecke==1){
					//System.out.println(jxSucheTable.getValueAt(zeile,8)+" ist im ersten Block / insgesamt existieren "+vec.size()+" Blöcke");
					// es gibt mehrere blöcke
					((Vector)vec.get(block-1)).set(0, "");
					((Vector)vec.get(block-1)).set(1, "");					
					stmt = macheStat(vec,new Integer((String)jxSucheTable.getValueAt(zeile,17)),name,nummer);
					//System.out.println("Es gibt nur einen Block, deshalb kann man dirket das statement basteln");
					//System.out.println(stmt);
					break;
				}else{
				//es gibt mehrere bloecke und der betroffene block = der erste, also muß der nachfolgende untersucht werden.
					//System.out.println(jxSucheTable.getValueAt(zeile,8)+" ist im ersten Block / insgesamt existieren "+vec.size()+" Blöcke");
					String xname,xnummer;
					xname = (String) ((Vector)vec.get(1)).get(0);
					xnummer = (String) ((Vector)vec.get(1)).get(1);
					if(xname.trim().equals("") && xnummer.trim().equals("")){
						String startzeit = new String((String) ((Vector)vec.get(0)).get(2));
						String endzeit = new String((String) ((Vector)vec.get(1)).get(4));
						int dauer1 = new Integer((String) ((Vector)vec.get(1)).get(3));
						int dauer2 = new Integer((String) ((Vector)vec.get(0)).get(3));
						int dauerNeu = dauer1+dauer2;
						((Vector)vec.get(1)).set(0, "");
						((Vector)vec.get(1)).set(1, "");					
						((Vector)vec.get(1)).set(2, startzeit);
						((Vector)vec.get(1)).set(3, new Integer(dauerNeu).toString());
						((Vector)vec.get(1)).set(4,endzeit);
						vec.remove(0);
						stmt = macheStat(vec,new Integer((String)jxSucheTable.getValueAt(zeile,17)),name,nummer);
						//System.out.println("Der betroffene Block ist der erste und der nachfolgende Block = ebenfalls leer");
						//System.out.println(stmt);						
						break;
						
					}else{
						((Vector)vec.get(block-1)).set(0, "");
						((Vector)vec.get(block-1)).set(1, "");					
						stmt = macheStat(vec,new Integer((String)jxSucheTable.getValueAt(zeile,17)),name,nummer);
						//System.out.println("Der betroffene Block ist der erste und der nachfolgende Block = nicht(!!) leer");
						//System.out.println(stmt);
						break;
					}
				}
			}	
			// es gibt mehrere Blöcke aber der betroffene ist der letzte nur den vorherigen Block prüfen
			if(block == bloecke){
				//System.out.println(jxSucheTable.getValueAt(zeile,8)+" ist im ersten Block / insgesamt existieren "+vec.size()+" Blöcke");
				String xname,xnummer;
				xname = (String) ((Vector)vec.get(block-2)).get(0);
				xnummer = (String) ((Vector)vec.get(block-2)).get(1);
				if(xname.trim().equals("") && xnummer.trim().equals("")){
					String startzeit = new String((String) ((Vector)vec.get(block-2)).get(2));
					String endzeit = new String((String) ((Vector)vec.get(block-1)).get(4));
					int dauer1 = new Integer((String) ((Vector)vec.get(block-2)).get(3));
					int dauer2 = new Integer((String) ((Vector)vec.get(block-1)).get(3));
					int dauerNeu = dauer1+dauer2;
					((Vector)vec.get(block-2)).set(0, "");
					((Vector)vec.get(block-2)).set(1, "");					
					((Vector)vec.get(block-2)).set(2, startzeit);
					((Vector)vec.get(block-2)).set(3, new Integer(dauerNeu).toString());
					((Vector)vec.get(block-2)).set(4,endzeit);
					vec.remove(block-1);
					stmt = macheStat(vec,new Integer((String)jxSucheTable.getValueAt(zeile,17)),name,nummer);
					//System.out.println("Der betroffene Block ist der letzte und der vorhergehende ist ebenfalls leer");
					//System.out.println(stmt);
					break;				
				}else{
					((Vector)vec.get(block-1)).set(0, "");
					((Vector)vec.get(block-1)).set(1, "");					
					stmt = macheStat(vec,new Integer((String)jxSucheTable.getValueAt(zeile,17)),name,nummer);
					//System.out.println("Der betroffene Block ist der letzte und der nachfolgende Block = nicht(!!) leer");
					//System.out.println(stmt);
					break;
				}
			}
			if((block > 1) && (block != bloecke)){
				String xname,xnummer,yname,ynummer;
				xname = (String) ((Vector)vec.get(block-2)).get(0);
				xnummer = (String) ((Vector)vec.get(block-2)).get(1);
				yname = (String) ((Vector)vec.get(block)).get(0);
				ynummer = (String) ((Vector)vec.get(block)).get(1);
				if(( xname.trim().equals("")) && (xnummer.trim().equals("")) &&
						( yname.trim().equals("")) && (ynummer.trim().equals("")) ){
					//der Vorbock ist leer und der Nachblock ist auch leef
					String startzeitvb = new String((String) ((Vector)vec.get(block-2)).get(2));
					String endzeitnb = new String((String) ((Vector)vec.get(block)).get(4));
					int dauervb = new Integer((String) ((Vector)vec.get(block-2)).get(3));
					int dauerakt = new Integer((String) ((Vector)vec.get(block-1)).get(3));
					int dauernb = new Integer((String) ((Vector)vec.get(block)).get(3));
					int dauerNeu = dauervb+dauerakt+dauernb;
					((Vector)vec.get(block-2)).set(0, "");
					((Vector)vec.get(block-2)).set(1, "");					
					((Vector)vec.get(block-2)).set(2, startzeitvb);
					((Vector)vec.get(block-2)).set(3, new Integer(dauerNeu).toString());
					((Vector)vec.get(block-2)).set(4,endzeitnb);
					vec.remove(block);
					vec.remove(block-1);					
					stmt = macheStat(vec,new Integer((String)jxSucheTable.getValueAt(zeile,17)),name,nummer);
					//System.out.println("Der betroffene Block ist zwischendrinn der vorhergehende und der nachfolgende sind ebenfalls leer");
					//System.out.println(stmt);
					break;
				}
				if(( xname.trim().equals("")) && (xnummer.trim().equals("")) ){
					//der Vorblock ist leer der Nachblock nicht !!
					String startzeit = new String((String) ((Vector)vec.get(block-2)).get(2));
					String endzeit = new String((String) ((Vector)vec.get(block-1)).get(4));
					int dauer1 = new Integer((String) ((Vector)vec.get(block-2)).get(3));
					int dauer2 = new Integer((String) ((Vector)vec.get(block-1)).get(3));
					int dauerNeu = dauer1+dauer2;
					((Vector)vec.get(block-2)).set(0, "");
					((Vector)vec.get(block-2)).set(1, "");					
					((Vector)vec.get(block-2)).set(2, startzeit);
					((Vector)vec.get(block-2)).set(3, new Integer(dauerNeu).toString());
					((Vector)vec.get(block-2)).set(4,endzeit);
					vec.remove(block-1);
					stmt = macheStat(vec,new Integer((String)jxSucheTable.getValueAt(zeile,17)),name,nummer);
					//System.out.println("Der betroffene Block zwischendrinn und der vorhergende ist ebenfalls leer");
					//System.out.println(stmt);
					break;				
				}
				if(( yname.trim().equals("")) && (ynummer.trim().equals("")) ){
					//der Vorblock ist nicht leer aber der Nachblock!!
					String startzeit = new String((String) ((Vector)vec.get(block-1)).get(2));
					String endzeit = new String((String) ((Vector)vec.get(block)).get(4));
					int dauer1 = new Integer((String) ((Vector)vec.get(block-1)).get(3));
					int dauer2 = new Integer((String) ((Vector)vec.get(block)).get(3));
					int dauerNeu = dauer1+dauer2;
					((Vector)vec.get(block-1)).set(0, "");
					((Vector)vec.get(block-1)).set(1, "");					
					((Vector)vec.get(block-1)).set(2, startzeit);
					((Vector)vec.get(block-1)).set(3, new Integer(dauerNeu).toString());
					((Vector)vec.get(block-1)).set(4,endzeit);
					vec.remove(block);
					stmt = macheStat(vec,new Integer((String)jxSucheTable.getValueAt(zeile,17)),name,nummer);
					//System.out.println("Der betroffene Block zwischendrinn und der nachfolgende ist ebenfalls leer");
					//System.out.println(stmt);
					break;				
				}else{
					((Vector)vec.get(block-1)).set(0, "");
					((Vector)vec.get(block-1)).set(1, "");					
					stmt = macheStat(vec,new Integer((String)jxSucheTable.getValueAt(zeile,17)),name,nummer);
					//System.out.println("Der betroffene Block zwischendrinn und weder der vorherige noch der nachfolgende sind leer");
					//System.out.println(stmt);
					break;
				}

				
			}
			//System.out.println(jxSucheTable.getValueAt(zeile,8)+" ist im Block Nr. "+block+" / insgesamt existieren "+vec.size()+" Blöcke");
			
		}
		if(stmt!=null){
			try {
				schreibeZeile(stmt);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			//
		}
	}
	/*****************************************/	
	private String macheStat(Vector vec,int id,String name,String nummer){
		String stmt = new String();
		int gross = vec.size(),i;
		String rnummer = null;
		String snummer = null;
		stmt = "Update flexkc set ";
		System.out.println(name+"  -  "+nummer);
		for(i=0;i<gross;i++){
			System.out.println(vec.get(i));
			stmt = stmt +"T"+(i+1)+" = '"+StringTools.Escaped(((String)((Vector)vec.get(i)).get(0)))+"'"+", ";
			rnummer = ((String)((Vector)vec.get(i)).get(1));
			snummer = new String(rnummer.trim().replace("\\", "\\\\") );
			rnummer = snummer.replace("\\\\\\\\", "\\\\");
			stmt = stmt +"N"+(i+1)+" = '"+rnummer+"'"+", ";
			//stmt = stmt +"N"+(i+1)+" = '"+((String)((Vector)vec.get(i)).get(1))+"'"+", ";
			stmt = stmt +"TS"+(i+1)+" = '"+((String)((Vector)vec.get(i)).get(2))+"'"+", ";
			stmt = stmt +"TD"+(i+1)+" = '"+((String)((Vector)vec.get(i)).get(3))+"'"+", ";
			stmt = stmt +"TE"+(i+1)+" = '"+((String)((Vector)vec.get(i)).get(4))+"'"+", ";			
		}
		stmt = stmt+ "BELEGT ='"+new Integer(vec.size()).toString()+"' where id='"+new Integer(id).toString()+"'";
		System.out.println(stmt);
		
		return stmt;
	}
	/*****************************************/
	private void schreibeZeile(String sstmt) throws SQLException{
		Statement stmt = null;
		ResultSet rs = null;
		boolean res;
		//Wenn die Zeilen tatsächlich geschrieben werden sollen ab hier entfernen
		/*
		int i = 1;
		if(i == 1){
			return;
		}
		*/
		//bis hierher entfernen
		try {
				stmt = (Statement) Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
				        ResultSet.CONCUR_UPDATABLE );
			
			res = stmt.execute(sstmt);
		}finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) { // ignore }
					rs = null;
				}
				if (stmt != null) {
					try {
						stmt.close();
					} catch (SQLException sqlEx) { // ignore }
						stmt = null;
					}
				}
			}
		}
	}	
	/*****************************************/	
	private Vector sucheZeile(int zeile) throws SQLException{
		Statement stmt = null;
		ResultSet rs = null;
		String suchstmt;
		Vector vec = new Vector();
		try {
				stmt = (Statement) Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
				        ResultSet.CONCUR_UPDATABLE );
			suchstmt = "select * from flexkc where id = '"+jxSucheTable.getValueAt(zeile,17)+"'";						
		

			rs = (ResultSet) stmt.executeQuery(suchstmt);
			while(rs.next()){
				int bloecke = new Integer(rs.getString(301));
				//System.out.println("Blöcke = "+bloecke);
				int ii;
				Vector vx = new Vector();
				for(ii=0;ii<bloecke;ii++){
					vx.add(rs.getString("T"+(ii+1)));
					vx.add(rs.getString("N"+(ii+1)));					
					vx.add(rs.getString("TS"+(ii+1)));					
					vx.add(rs.getString("TD"+(ii+1)));					
					vx.add(rs.getString("TE"+(ii+1)));
					vec.add(vx.clone());
					vx.clear();					
				}
				/*
				vx.clear();
				vx.add(rs.getString("BELEGT"));
				vec.add(vx.clone());
				*/
			}

		}finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) { // ignore }
					rs = null;
				}
				if (stmt != null) {
					try {
						stmt.close();
					} catch (SQLException sqlEx) { // ignore }
						stmt = null;
					}
				}
			}
		}
		return (Vector)vec.clone();
	}
	/********Ende SwingWorker*/
	

/**************Ende der Hauptklasse*******************************/

	
	public void propertyChange(PropertyChangeEvent arg0) {
		// TODO Auto-generated method stub
		//System.out.println("PropertyChange"+arg0);
	}

@Override
public void keyPressed(KeyEvent arg0) {
	System.out.println("********Button in KeyPressed*********");	
	System.out.println(((JComponent)arg0.getSource()).getName());	
	if(arg0.getKeyCode()== 10 || arg0.getKeyCode()==0)
		arg0.consume();

	/*
	// TODO Auto-generated method stub
	System.out.println("********Button in KeyPressed*********");
	if(((JComponent)arg0.getSource()).getName().equals("start")){
		if(arg0.getKeyCode() == 10){
			arg0.consume();
			if(sucheStarten.isEnabled()){
				knopfGedoense(new int[]  {0,1,0,0,0,0,0,0,0,0});
				roogleStarten();
				sucheStoppen.requestFocus();
			}
		}
	}
	if(((JComponent)arg0.getSource()).getName().equals("stop")){
		if(arg0.getKeyCode() == 10){
			arg0.consume();
			if(sucheStoppen.isEnabled()){
				knopfGedoense(new int[]  {0,0,1,0,0,0,0,1,1,1});
				roogleStoppen();
			}
		}
	}
	if(((JComponent)arg0.getSource()).getName().equals("zurueck")){
		if(arg0.getKeyCode() == 10){
			arg0.consume();
			if(allesZuruecksetzen.isEnabled()){
				knopfGedoense(new int[]  {1,0,0,0,0,0,0,0,0,0});
				roogleZuruecksetzen();
				Runtime r = Runtime.getRuntime();
			    r.gc();
			}
		}
	}
	*/
	
}

@Override
public void keyReleased(KeyEvent arg0) {
	
	// TODO Auto-generated method stub
	System.out.println("********Button in KeyReleased*********"+arg0.getKeyCode());
	System.out.println(((JComponent)arg0.getSource()).getName());
	String csource = "";
	if(arg0.getKeyCode() == 10 || arg0.getKeyCode() == 0){
		csource = ((JComponent)arg0.getSource()).getName();
		arg0.consume();
	}else{
		return;
	}
	if(csource.equals("start")){
			if(sucheStarten.isEnabled() ){
				if(! sucheStarten.isSelected() ){
					sucheStarten.setSelected(true);
					return;
				}else{
					knopfGedoense(new int[]  {0,1,0,0,0,0,0,0,0,0});
					roogleStarten();
					sucheStoppen.requestFocus();
					return;
				}
			}
	}
	if(csource.equals("stop")){
			if(sucheStoppen.isEnabled()){
				knopfGedoense(new int[]  {0,0,1,0,0,0,0,1,1,1});
				roogleStoppen();
				return;
			}
	}
	if(csource.equals("zurueck")){
			if(allesZuruecksetzen.isEnabled()){
				knopfGedoense(new int[]  {1,0,0,0,0,0,0,0,0,0});
				roogleZuruecksetzen();
				Runtime r = Runtime.getRuntime();
			    r.gc();
			}
	}

}

@Override
public void keyTyped(KeyEvent arg0) {
	// TODO Auto-generated method stub
	System.out.println("********Button in KeyTyped*********"+arg0.getKeyCode());
	System.out.println(((JComponent)arg0.getSource()).getName());	
	if(arg0.getKeyCode() == 10 || arg0.getKeyCode() == 0){
		arg0.consume();
	}else{
		return;
	}	

	//arg0.consume();
}	

}
/***********************Bereits jetzt für*************************/
/***********************ein Update vorgesehen*********************/
/*****************************************************************/
@SuppressWarnings("unchecked")
final class WorkerSuchenInKalenderTagen extends SwingWorker<Void,Void>{
	Statement stmt = null;
	ResultSet rs = null;
	String sergebnis = "";
	boolean gesperrt = false;
	boolean gruppe = false;
	int abteilnr;
	String[] exStatement = {null};
	String suchkrit1 = "";
	String suchkrit2 = "";
	String sqlEnde = "";
	String sqlAkt = "";
	String sqlAlt = "";
	String zeit = "";
	long zeit1;
	int treffer = 0;
	int ftage = 0;
	String aktDatum = null;
	ArrayList<String> atermine = new ArrayList<String>();
	public static ArrayList<String> sperrDatum = new ArrayList<String>();
	//private ArrayList<String> sperrDatum = new ArrayList<String>();
	ImageIcon img,img2;
	int schichtArt = -1;
	int selektivArt = -1;
	

	public void setzeStatement(){
		img = new ImageIcon(Reha.proghome+"icons/Kreuz_klein.gif");
		img2 = new ImageIcon(Reha.proghome+"icons/frei.png");		
		img.setDescription("gesperrt");
		img2.setDescription("offen");
		SuchenSeite.setZeit();
		zeit = SuchenSeite.getZeit();
		this.sperrDatum.clear();
		if(SuchenSeite.selektiv){
			selektivArt = macheSelektiv();
		}
		
	}


	@Override
	protected Void doInBackground() throws Exception {
		// TODO Auto-generated method stub

	
		//Vector treadVect = new Vector();
		aktDatum = SuchenSeite.getAktDatum();
		
		try {
			stmt = (Statement) Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE );
			sqlEnde = datFunk.sDatInSQL(SuchenSeite.getStopDatum());

			sqlAkt = datFunk.sDatInSQL(aktDatum);
			sqlAlt = sqlAkt;

			suchkrit1 = SuchenSeite.getSuchName().trim();
			suchkrit2 = SuchenSeite.getSuchNummer().trim();
			int suchArt = 0;
			int dbzeil = -1;
			String szeil = "";
			for(int j=0;j<1;j++){
				if(suchkrit1.equals("")&& suchkrit2.equals("")){
					// es wird nach leeren Terminen gesucht
					suchArt = 1;
					break;
				}
				if( (!suchkrit1.equals(""))&& suchkrit2.equals("")){
					// es wird nach einem Namen gesucht
					suchArt = 2;
					break;
				}
				if( (suchkrit1.equals(""))&& (!suchkrit2.equals(""))){
					// es wird nach einer Nummer gesucht					
					suchArt = 3;
					break;
				}
				if( (!suchkrit1.equals(""))&& (!suchkrit2.equals(""))){
					// es wird nach einem Namen und nach einer Nummer gesucht					
					suchArt = 4;
					break;
				}

			}
			String[] abtei = {"KG","MA","ER","LO","SP"};
			
			zeit1 = System.currentTimeMillis();
			SuchenSeite.getInstance().tabelleAusschalten();
			SuchenSeite.setFortschrittRang(0,datFunk.TageDifferenz( SuchenSeite.getStartDatum(), SuchenSeite.getStopDatum() ) );
			SuchenSeite.setFortschrittSetzen(0);
			SuchenSeite.setFortschrittZeigen(true);
			Vector abtlg = new Vector(Arrays.asList(abtei));
			while( (datFunk.DatumsWert(sqlAkt) <= datFunk.DatumsWert(sqlEnde)) && (!SuchenSeite.mussUnterbrechen)){
				SuchenSeite.setzeDatum(aktDatum );
				if( SuchenSeite.tagDurchsuchen(aktDatum) ){

					
					String test = "";
					if(SuchenSeite.getGewaehlt() <= 20){
						test = macheStatement(sqlAkt);	
						//System.out.println(test);
					}else{
						test = "select * from flexkc where datum = '"+sqlAkt+"' LIMIT "+ParameterLaden.maxKalZeile;						
					}
					rs = (ResultSet) stmt.executeQuery(test);
					

					while(rs.next()){
						
						
						/*in Spalte 301 steht die Anzahl der belegten Blöcke*/ 
						int belegt = rs.getInt(301);

						String name = "";
						String nummer = "";
						String skollege = "";
						//int	dauer = 0;
						int ikollege = 0;
						int defdauer = 0;


						skollege = rs.getString(303).substring(0,2);
						if( skollege.substring(0,1) == "0" ){
							ikollege = new Integer(skollege.substring(1,1));
						}else{
							ikollege = new Integer(skollege);								
						}

						/***************Hier wird getestet ob die Kalender zeile belegt ist*****/
						/********wenn ein Benutzer gelöscht wurde kommt leer zurück*******/
						szeil = ParameterLaden.getKollegenUeberDBZeile(ikollege);
						//System.out.println("Kollege über DBZeile = "+szeil);
						if(!szeil.equals("")){
						
						String sabteilung = SuchenSeite.getKollegenAbteilung(ikollege).trim();
						if(! sabteilung.equals("") ){
							if( (abtlg.contains(sabteilung)) ){
								defdauer = (int) SuchenSeite.getKollegenZeiten().get(
										SuchenSeite.getKollegenAbteilung(ikollege));
								gruppe = false;
							}else{
								//System.out.println("Kollege "+szeil+" hat keine Zeitzuordnung, nehme Gruppendefiniton");
								
								
								if( (abteilnr = SystemConfig.oGruppen.gruppenNamen.indexOf(sabteilung)) >= 0){
									defdauer = (int) new Long(SystemConfig.oGruppen.gruppenGueltig.get(abteilnr)[2]).intValue();
									//System.out.println("Gültig ab:"+ datFunk.WertInDatum(SystemConfig.oGruppen.gruppenGueltig.get(abteilnr)[0]));
									//ermitteln ob alte oder neue Definition verwendet werden soll // in Variable ablegen
									//dann Sprung in die Tests
								}else{
									abteilnr = -1;
									defdauer = 15;
								}
								gruppe = true;
								/*
								System.out.println("sabteilung = "+sabteilung);
								System.out.println("abteilnr = "+abteilnr);
								System.out.println("gruppe = "+gruppe);
								*/
							}					
						}else{
							defdauer = 15;
							gruppe = false;
						}
							
						int ii;
						for(ii = 1;ii <= belegt;ii++){
							
							name =  ( rs.getString("T"+(ii))== null ? "" : rs.getString("T"+(ii)).trim() ); 
							nummer =  ( rs.getString("N"+(ii))== null ? "" : rs.getString("N"+(ii)).trim() );							

							
							if(! (SuchenSeite.getKollegenSuchen(ikollege)) ){
								//System.out.println("Keine Suche erforderlich für Kollege "+ikollege);
								// falls hier nicht gesucht werden soll = anzahl > 20
								break;
							}
							
							
							int j;
							Vector yvec = null;
							int kaldauer = new Integer(rs.getString("TD"+(ii)));

							for(j=0;j<1;j++){
								boolean keintest = keinTest();
								if( (suchArt==1) && (kaldauer < defdauer)){
									// wenn die Terminkaldauer kleiner als die eingestellte Dauer
									break;
								}
								if( (suchArt==1)){
									// leerer Termin gesucht
									if((keintest)  && (!gruppe)){
										yvec = sucheNachFreien(rs,name,nummer,skollege,ikollege,ii,defdauer);
										//System.out.println("Es wurde ein vecor erstllt");
										break;
									}
									if( (keintest)  && (gruppe)){
										if(name.equals("") && nummer.equals("")){
											Vector vecgruppe;
											if( (vecgruppe = gruppenTest(rs,abteilnr,ii,defdauer,true)) != null){
												//System.out.println("In Gruppensuchen nach Freien");
													yvec = sucheNachGruppenFreien(rs,name,nummer,skollege,ikollege,ii,defdauer,abteilnr,vecgruppe,true);
											}
										}
										break;
									}									
									if(SuchenSeite.schicht){
										if(freiTermin(name,nummer)){
											if(schichtTest(rs,ii,defdauer)){
												yvec = sucheNachSelect(rs,name,nummer,skollege,ikollege,ii,defdauer);
												break;
											}
										}
										break;
									}
									if(SuchenSeite.selektiv){
										if(freiTermin(name,nummer)){										
											if(selektivTest(rs,ii,defdauer)){
												yvec = sucheNachSelect(rs,name,nummer,skollege,ikollege,ii,defdauer);
												break;
											}
										}
										break;
									}
								}
	
								/***********************************/
								if( (suchArt==2)){
									if(name.contains(suchkrit1) && !nummer.contains("@FREI") && (!gruppe) ){
										if(suchkrit1.equals("#KGG")){
											yvec = sucheNachKGG(rs,name,nummer,skollege,ikollege,ii,defdauer);
											break;
										}
										if(name.contains("KGG")){
											yvec = sucheNachKGG(rs,name,nummer,skollege,ikollege,ii,defdauer);
											break;
										}
										yvec = sucheNachNamen(rs,name,nummer,skollege,ikollege,ii);
										break;
									}
									if(name.contains(suchkrit1) && !nummer.contains("@FREI") && (gruppe) ){
										Vector vecgruppe;
										if( (vecgruppe = gruppenTest(rs,abteilnr,ii,defdauer,false)) != null){
											//System.out.println("In Gruppensuchen nach Freien");
												yvec = sucheNachGruppenFreien(rs,name,nummer,skollege,ikollege,ii,defdauer,abteilnr,vecgruppe,false);
										}
									}
									break;
								}
								/***********************************/
								if( (suchArt==3)){
									if(nummer.contains(suchkrit2) && (!gruppe) ){
										if(name.contains("KGG")){
											yvec = sucheNachKGG(rs,name,nummer,skollege,ikollege,ii,defdauer);
											break;
										}
										yvec = sucheNachNamen(rs,name,nummer,skollege,ikollege,ii);
										break;
									}
									if(nummer.contains(suchkrit2) && (gruppe) ){
										Vector vecgruppe;
										if( (vecgruppe = gruppenTest(rs,abteilnr,ii,defdauer,false)) != null){
											//System.out.println("In Gruppensuchen nach Freien");
												yvec = sucheNachGruppenFreien(rs,name,nummer,skollege,ikollege,ii,defdauer,abteilnr,vecgruppe,false);
										}
									}
									break;
								}
								/***********************************/
								if( (suchArt==4)){
									if(name.contains(suchkrit1) && nummer.contains(suchkrit2) && (!gruppe) ){
										if(suchkrit1.equals("#KGG")){
											yvec = sucheNachKGG(rs,name,nummer,skollege,ikollege,ii,defdauer);
											break;
										}
										if(name.contains("KGG")){
											yvec = sucheNachKGG(rs,name,nummer,skollege,ikollege,ii,defdauer);
											break;
										}
										yvec = sucheNachNamen(rs,name,nummer,skollege,ikollege,ii);
										break;
									}
									if(name.contains(suchkrit1) && nummer.contains(suchkrit2) && (gruppe) ){
										Vector vecgruppe;
										if( (vecgruppe = gruppenTest(rs,abteilnr,ii,defdauer,false)) != null){
											//System.out.println("In Gruppensuchen nach Freien");
												yvec = sucheNachGruppenFreien(rs,name,nummer,skollege,ikollege,ii,defdauer,abteilnr,vecgruppe,false);
										}
									}
									break;
								}
								
								/***********************************/
								break;
							}
						
							if(yvec != null){
								++treffer;
								
								/*********************************************/

								
/********************************************************************************************************/
/*										 
							String sperre;
								
								sperre = new String((String)((Vector)yvec).get(13)+
													(String)((Vector)yvec).get(14) ); 

								if(sperrDatum.contains(sperre+SystemConfig.dieseMaschine+zeit)){
									yvec.set(1, img2);
								}else{
									//int ret = 0;
									int ret = XSperrenVerarbeiten(treffer-1,(Vector)yvec.clone(),zeit);
									if(ret==0){
										sperrDatum.add(sperre+SystemConfig.dieseMaschine+zeit);
									}
									yvec.set(1, (ret==0 ? img2 : img));
								}
								final Vector xvec = new Vector((Vector)yvec.clone()); 
								new Thread(){
									public void run(){
										//xx
										//SuchenSeite.getInstance().dtblm.getDataVector().add((Vector) xvec.clone());
										SuchenSeite.getInstance().dtblm.addRow((Vector) xvec.clone());
										SuchenSeite.setDatenVector((Vector)xvec);
										
									}
								}.start();
								trefferSetzen();
*/								
								
/********************************************************************************************************/								
								/************************/
								/*
								final Vector xvec = new Vector((Vector)yvec.clone()); 
								SwingWorker worker = new SwingWorker<Void, Void>(){
									 protected Void doInBackground(){
										 SuchenSeite.sucheDaten.add((Vector)xvec.clone());
										//SuchenSeite.setDatenVector((Vector)xvec.clone());
										SuchenSeite.setzeDatum(aktDatum );
										trefferSetzen();
										return null;
									 }	
								};
								worker.execute();
								*/
								
								//SuchenSeite.rooDaten.rvec.add((Vector)yvec.clone());
								SuchenSeite.sucheDaten.add((Vector)yvec.clone());
								//SuchenSeite.setDatenVector((Vector)yvec.clone());
								trefferSetzen();
								
//								firePropertyChange("TrefferGesetzt", treffer-1, treffer);
								

								

								/***********************************************/
//								
							}
							
						} // ende der for für die einzelnen Felder
						/*****************/
						} //neu endif von: keine Zeile im Kalender
					} // Klammer der While 
					
				aktDatum = datFunk.sDatPlusTage(aktDatum, 1);
				SuchenSeite.setFortschrittSetzen(++ftage);
				/*
				SwingUtilities.invokeLater(new Runnable(){
					public  void run(){
						//SuchenSeite.setzeDatum(aktDatum );						       	  	
					}
				});
				*/
				//Thread.sleep(30);
				sqlAlt = new String(sqlAkt);
				sqlAkt = datFunk.sDatInSQL(aktDatum );
				//System.out.println(SuchenSeite.getAktDatum()+"-"+SuchenSeite.tagDurchsuchen(SuchenSeite.getAktDatum()) );
				}else{
					aktDatum = datFunk.sDatPlusTage(aktDatum, 1);
					//SuchenSeite.setzeDatum(aktDatum );						       	  	
					sqlAlt = new String(sqlAkt);					
					sqlAkt = datFunk.sDatInSQL(aktDatum );					//System.out.println(SuchenSeite.getAktDatum()+"-"+SuchenSeite.tagDurchsuchen(SuchenSeite.getAktDatum()) );
					SuchenSeite.setFortschrittSetzen(++ftage);
				}
				
								
				//Thread.sleep(5);
				/*********Ende der oberen While*********/
				
			}		
		}catch(SQLException ex) {
			System.out.println("von stmt -SQLState: " + ex.getSQLState());
		}




		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) { // ignore }
					rs = null;
				}
				if (stmt != null) {
					try {
						stmt.close();
					} catch (SQLException sqlEx) { // ignore }
						stmt = null;
					}
				}
			}
		}
		if (datFunk.DatumsWert(sqlAkt) > datFunk.DatumsWert(sqlEnde)){
			SuchenSeite.setSucheBeendet();
			SuchenSeite.setFortschrittZeigen(false);
		}

		//Reha.thisClass.conn.setAutoCommit(false);
		//System.out.println("Insgesamt wurden Treffer gezählt -> "+treffer);
		long dauer = (System.currentTimeMillis()-zeit1);
		//System.out.println("Dauer des Suchlaufes = "+);

		//Reha.thisClass.copyLabel.setText("Suchlauf: "+dauer+"ms");

		//SuchenSeite.mussUnterbrechen = true;
		//####
		SwingUtilities.invokeLater(new Runnable(){
			public  void run(){
				
				//JOptionPane.showMessageDialog(null, "Suchlauf beendet nach "+((System.currentTimeMillis()-zeit1))+" Millisekunden\n\n");//+
				//"Treffer insgesamt: "+SuchenSeite.getInstance().jxSucheTable.getRowCount());
				SuchenSeite.setKnopfGedoense(new int[]  {0,0,0,0,0,0,0,1,1,1});
				SuchenSeite.getInstance().tabelleEinschalten();
				SuchenSeite.getInstance().listenerEinschalten();
				/*
				long zeit = System.currentTimeMillis();
				while(SuchenSeite.getInstance().jxSucheTable.getRowCount() < treffer){
					if( (System.currentTimeMillis() - zeit) > 1500){
						break;
					}
				}
				*/
				//SuchenSeite.getInstance().dtblm.setDataVector(SuchenSeite.sucheDaten,getColVec());
				System.out.println("Vectorgröße = "+SuchenSeite.sucheDaten.size());
				SuchenSeite.mussUnterbrechen = true;
				SuchenSeite.getInstance().jxSucheTable.revalidate();
			}
		});
		

		//schreibeDaten();
	return null;
	}
	private Vector getColVec(){
	Vector cols = new Vector();	
	String[] column = 	{"x?","G!","Datum","von","bis","Min.","Beginn","Dauer.","Namen","Rez.Nr.","Behandler",
			//		  12          13      14      15        16          17       18     19
					"Druckzeit","Sort","Spalte","sDatum","sOrigDatum","iBlock","id","maxblock",""};
	for(int i = 0; i < column.length;i++){
		cols.add(column[i]);
	}
		return (Vector)cols.clone();
	}
	
/********************************************/
	private void schreibeDaten(){
		int anzahl = SuchenSeite.sucheDaten.size();
		int i;
		String datum, beginn,einde,behandler;
		String str;
		Vector vec = null;
		File file = new File("C:\\SucheDaten\\SucheDaten.txt");
 
		 
        try {
			FileWriter fw = new FileWriter(file);
			for(i = 0; i < anzahl; i++){
				vec = (Vector) SuchenSeite.sucheDaten.get(i);
				str = (String)vec.get(2);
				str = str+" - "+(String)vec.get(3);
				str = str+" - "+(String)vec.get(4);
				str = str+" - "+(String)vec.get(10);				
				fw.write(str+"\n");
			}
			str = "Insgesamt gefunden = "+i+" Sätze\n";
			fw.write(str);
			fw.close();

        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private Vector gruppenTest(ResultSet rs, int gruppe,int feld,int defdauer,boolean suchleer) throws SQLException{
		Vector vecret = null;
		String sDatum = datFunk.sDatInDeutsch(rs.getString("DATUM"));

		int taginwoche = datFunk.TagDerWoche(sDatum);
		int altneu = 1;
		
		//System.out.println(((Vector)((Vector)SystemConfig.oGruppen.gruppeAlle.get(gruppe)).get(taginwoche-1)));
		
		if( datFunk.DatumsWert(sDatum) >=
			SystemConfig.oGruppen.gruppenGueltig.get(gruppe)[0] ){
			altneu = 0;
		}
		int xdauer = new Integer(rs.getString("TD"+(feld)).trim());		
		String xzeit = rs.getString("TS"+(feld)).trim().substring(0,5);
		
		int gross = (int)((Vector)((Vector)((Vector)SystemConfig.oGruppen.gruppeAlle.get(gruppe)).get(altneu)).get(taginwoche-1)).size();
		//System.out.println("Termine = "+gross);
		for(int i = 0;i<gross;i++){
			long lgrenzeklein =(Long) ((Vector)((Vector)((Vector)((Vector)SystemConfig.oGruppen.gruppeAlle.get(gruppe)).get(altneu)).get(taginwoche-1)).get(i)).get(0);
			long lgrenzegross =(Long) ((Vector)((Vector)((Vector)((Vector)SystemConfig.oGruppen.gruppeAlle.get(gruppe)).get(altneu)).get(taginwoche-1)).get(i)).get(1); 	

			if(longPasstZwischen(lgrenzeklein,lgrenzegross,xzeit,(suchleer ? defdauer : xdauer))){
				//System.out.println("Zeitcheck = "+xzeit+" und Dauer "+defdauer+" paßt zwischen "+zeitFunk.MinutenZuZeit(new Long(lgrenzeklein).intValue())+
						//" und "+zeitFunk.MinutenZuZeit(new Long(lgrenzegross).intValue()));
				vecret = (Vector) ((Vector)((Vector)((Vector)((Vector)SystemConfig.oGruppen.gruppeAlle.get(gruppe)).get(altneu)).get(taginwoche-1)).get(i)).clone();
				return (Vector)vecret.clone();
			}
			
			
		}


		return vecret;
	}
	private boolean schichtTest(ResultSet rs,int ii,int defdauer) throws SQLException{
		String kalende,kalanfang,datum;
		String pbeginn,pende,uhr1,uhr2;
		boolean wogerade = false;
		int[] selektparm1=null;
		int[] selektparm2=null;
		pbeginn = rs.getString("TS"+ii);
		pende = rs.getString("TE"+ii);
		wogerade = datFunk.GeradeWoche(datFunk.sDatInDeutsch(rs.getString("DATUM")));
		kalanfang = SystemConfig.KalenderUmfang[0];
		kalende = SystemConfig.KalenderUmfang[1];
		
		// Bearbeitet nur gerade Kalenderwochen
		uhr1 = SuchenSeite.schichtUhr[0];
		uhr2 = SuchenSeite.schichtUhr[1];
		if((wogerade) && (SuchenSeite.schichtWal[0])){
			if(SuchenSeite.schichtVor[0]){
				selektparm1 = (Schnittmenge(kalanfang,uhr1,pbeginn,pende));
				if(selektparm1[0] >=defdauer){
					String drzeit = (selektparm1[3] > 0 ? 
					 zeitFunk.MinutenZuZeit(selektparm1[1]).substring(0,5) :
					 zeitFunk.MinutenZuZeit(selektparm1[2]-defdauer).substring(0,5)); 
					SuchenSeite.selektbeginn = drzeit;
					return true;
				}else{
					return false;
				}
			}else{
				selektparm1 = (Schnittmenge(uhr1,kalende,pbeginn,pende));
				if(selektparm1[0] >=defdauer){
					String drzeit = (selektparm1[3] > 0 ? 
					 zeitFunk.MinutenZuZeit(selektparm1[1]).substring(0,5) :
					 zeitFunk.MinutenZuZeit(selektparm1[2]-defdauer).substring(0,5)); 
					SuchenSeite.selektbeginn = drzeit;
					return true;
				}else{
					return false;
				}
			}		
		}
		if((!wogerade) && (SuchenSeite.schichtWal[1])){
			if(SuchenSeite.schichtVor[1]){
				selektparm1 = (Schnittmenge(kalanfang,uhr2,pbeginn,pende));
				if(selektparm1[0] >=defdauer){
					String drzeit = (selektparm1[3] > 0 ? 
					 zeitFunk.MinutenZuZeit(selektparm1[1]).substring(0,5) :
					 zeitFunk.MinutenZuZeit(selektparm1[2]-defdauer).substring(0,5)); 
					SuchenSeite.selektbeginn = drzeit;
					return true;
				}else{
					return false;
				}
			}else{
				selektparm1 = (Schnittmenge(uhr2,kalende,pbeginn,pende));
				if(selektparm1[0] >=defdauer){
					String drzeit = (selektparm1[3] > 0 ? 
					 zeitFunk.MinutenZuZeit(selektparm1[1]).substring(0,5) :
					 zeitFunk.MinutenZuZeit(selektparm1[2]-defdauer).substring(0,5)); 
					SuchenSeite.selektbeginn = drzeit;
					return true;
				}else{
					return false;
				}
			}
		}
		return false;
	}
	private boolean selektivTest(ResultSet rs,int ii,int defdauer) throws SQLException{
		//long planbeginn,planende;
		String kalende,kalanfang;
		String pbeginn,pende,wbeginn1,wende1,wbeginn2,wende2;
		pbeginn = rs.getString("TS"+ii);
		pende = rs.getString("TE"+ii);
		wbeginn1 = SuchenSeite.selectUhr[0];
		wende1 = SuchenSeite.selectUhr[1];
		wbeginn2 = SuchenSeite.selectUhr[2];
		wende2 = SuchenSeite.selectUhr[3];
 		
		kalanfang = SystemConfig.KalenderUmfang[0];
		kalende = SystemConfig.KalenderUmfang[1];
		
		
		int[] selektparm1=null;
		int[] selektparm2=null;
		for(int v = 0;v < 1; v++){
			/*******************************/
			if(macheSelektiv()==0){
				selektparm1 = (Schnittmenge(wbeginn1,kalende,pbeginn,pende));
				if(selektparm1[0] >=defdauer){
					String drzeit = (selektparm1[3] > 0 ? 
					 zeitFunk.MinutenZuZeit(selektparm1[1]).substring(0,5) :
					 zeitFunk.MinutenZuZeit(selektparm1[2]-defdauer).substring(0,5)); 
					SuchenSeite.selektbeginn = drzeit;
					return true;
				}else{
					return false;
				}
			}
			/*******************************/
			if(macheSelektiv()==1){
				selektparm1 = Schnittmenge(kalanfang,wende1,pbeginn,pende); 
				if(selektparm1[0 ]>=defdauer){
					String drzeit = (selektparm1[3] > 0 ? 
					 zeitFunk.MinutenZuZeit(selektparm1[1]).substring(0,5) :
					 zeitFunk.MinutenZuZeit(selektparm1[2]-defdauer).substring(0,5));
					SuchenSeite.selektbeginn = drzeit;
					return true;
				}else{
					return false;
				}
			}
			/*******************************/
			if(macheSelektiv()==2){
				selektparm1 = Schnittmenge(wbeginn1,wende1,pbeginn,pende); 
				if(( selektparm1[0] >=defdauer)){
					String drzeit = (selektparm1[3] > 0 ? 
					 zeitFunk.MinutenZuZeit(selektparm1[1]).substring(0,5) :
					 zeitFunk.MinutenZuZeit(selektparm1[2]-defdauer).substring(0,5));
					SuchenSeite.selektbeginn = drzeit;
					return true;
				}else{
					return false;
				}
			}
			/**************ab hier mit ODER - Bedingung***************/
			if(macheSelektiv()==3){
				selektparm1 = Schnittmenge(wbeginn1,wende1,pbeginn,pende); 
				selektparm2 = Schnittmenge(wbeginn2,kalende,pbeginn,pende);
				if( (selektparm1[0]>=defdauer) || (selektparm2[0]>=defdauer)	){
					if(selektparm1[0] >= defdauer){
					//if(selektparm1[3] == -1){System.out.println("Anfang = -1 - "+pbeginn);}
					String drzeit = (selektparm1[3] > 0 ? 
					 zeitFunk.MinutenZuZeit(selektparm1[1]).substring(0,5) :
					 zeitFunk.MinutenZuZeit(selektparm1[2]-defdauer).substring(0,5));
					SuchenSeite.selektbeginn = drzeit;
					}else{
					String drzeit = (selektparm2[3] > 0 ? 
					 zeitFunk.MinutenZuZeit(selektparm2[1]).substring(0,5) :
					 zeitFunk.MinutenZuZeit(selektparm2[2]-defdauer).substring(0,5));
					SuchenSeite.selektbeginn = drzeit;
					}	
					return true;
				}else{
					return false;
				}
			}
			/*******************************/			
			if(macheSelektiv()==4){
				selektparm1 = Schnittmenge(wbeginn1,wende1,pbeginn,pende);
				selektparm2 = Schnittmenge(kalanfang,wende2,pbeginn,pende);
				if( (selektparm1[0]>=defdauer) || (selektparm2[0]>=defdauer)	){
					if(selektparm1[0] >= defdauer){
					String drzeit = (selektparm1[3] > 0 ? 
					 zeitFunk.MinutenZuZeit(selektparm1[1]).substring(0,5) :
					 zeitFunk.MinutenZuZeit(selektparm1[2]-defdauer).substring(0,5));
					SuchenSeite.selektbeginn = drzeit;
					}else{
					String drzeit = (selektparm2[3] > 0 ? 
					 zeitFunk.MinutenZuZeit(selektparm2[1]).substring(0,5) :
					 zeitFunk.MinutenZuZeit(selektparm2[2]-defdauer).substring(0,5));
					SuchenSeite.selektbeginn = drzeit;
					}	
					return true;
				}else{
					return false;
				}
			}
			/*******************************/			
			if(macheSelektiv()==5){
				selektparm1 = Schnittmenge(wbeginn1,wende1,pbeginn,pende);
				selektparm2 = Schnittmenge(wbeginn2,wende2,pbeginn,pende);
				if( (selektparm1[0]>=defdauer) || (selektparm2[0]>=defdauer)	){
					if(selektparm1[0] >= defdauer){
					String drzeit = (selektparm1[3] > 0 ? 
					 zeitFunk.MinutenZuZeit(selektparm1[1]).substring(0,5) :
					 zeitFunk.MinutenZuZeit(selektparm1[2]-defdauer).substring(0,5));
					SuchenSeite.selektbeginn = drzeit;
					}else{
					String drzeit = (selektparm2[3] > 0 ? 
					 zeitFunk.MinutenZuZeit(selektparm2[1]).substring(0,5) :
					 zeitFunk.MinutenZuZeit(selektparm2[2]-defdauer).substring(0,5));
					SuchenSeite.selektbeginn = drzeit;
					}	
					return true;
				}else{
					return false;
				}
			}
		}

		return false;
	}
	static boolean passtZwischen(String sgrenzeklein,String sgrenzegross,String szeit,int dauer){
		long z1 = zeitFunk.MinutenSeitMitternacht(sgrenzeklein);
		long z2 = zeitFunk.MinutenSeitMitternacht(sgrenzegross);
		long z3 = zeitFunk.MinutenSeitMitternacht(szeit)+new Long(dauer);
		return( ((z3 >= z1) &&  (z3<=z2)) ? true : false);
	}
	static boolean longPasstZwischen(long lgrenzeklein,long lgrenzegross,String szeit,int dauer){
		long z1 = lgrenzeklein;
		long z2 = lgrenzegross;
		long z3 = zeitFunk.MinutenSeitMitternacht(szeit)+new Long(dauer);
		return( ((z3 >= z1) &&  (z3<=z2)) ? true : false);
	}
	static boolean ZeitGroesserGleich(String szeit1,String szeit2){
		long z1 = zeitFunk.MinutenSeitMitternacht(szeit1);
		long z2 = zeitFunk.MinutenSeitMitternacht(szeit2);
		return( z2 >= z1 ? true : false);
	}
	static boolean ZeitGroesser(String szeit1,String szeit2){
		long z1 = zeitFunk.MinutenSeitMitternacht(szeit1);
		long z2 = zeitFunk.MinutenSeitMitternacht(szeit2);
		return( z2 > z1 ? true : false);
	}
	static boolean ZeitKleinerGleich(String szeit1,String szeit2){
		long z1 = zeitFunk.MinutenSeitMitternacht(szeit1);
		long z2 = zeitFunk.MinutenSeitMitternacht(szeit2);
		return( z2 <= z1 ? true : false);
	}
	static boolean ZeitKleiner(String szeit1,String szeit2){
		long z1 = zeitFunk.MinutenSeitMitternacht(szeit1);
		long z2 = zeitFunk.MinutenSeitMitternacht(szeit2);
		return( z2 < z1 ? true : false);
	}
	static int[] Schnittmenge(String sklein1,String sgross1,String sklein2,String sgross2 ){
		long z1 = zeitFunk.MinutenSeitMitternacht(sklein1);
		long z2 = zeitFunk.MinutenSeitMitternacht(sgross1);
		long z3 = zeitFunk.MinutenSeitMitternacht(sklein2);
		long z4 = zeitFunk.MinutenSeitMitternacht(sgross2);
		long schnittbeginn, schnittende;
		int ananfang = 1;
		// Wenn Wunschbeginn früher oder gleich als gefundener Termin-Beginn  
		if ( (z1 <= z3) && (z2 >= z4) ){
			schnittbeginn = z3;
			schnittende = z4;
			ananfang = 1;
			return new int[] {new Long(schnittende-schnittbeginn).intValue(),new Long(schnittbeginn).intValue(),new Long(schnittende).intValue(),new Integer(ananfang)};
		}

		if(z1 <= z3){
			schnittbeginn = z3;
			ananfang = 1; 
			//return new Long(z2-z3).intValue();
		}else if(z1 > z3){
			schnittbeginn = z1;
			ananfang = 0;
		}else {
			schnittbeginn = -1;
			ananfang = -1;
		}
		if(z2 <= z4){
			schnittende = z2;
			ananfang = 1;
		}else if(z2 > z4){
			schnittende = z4;
			ananfang = 0;
		}else{
			ananfang = -1;
			schnittende = -1;
		}
		return new int[] {new Long(schnittende-schnittbeginn).intValue(),new Long(schnittbeginn).intValue(),new Long(schnittende).intValue(),new Integer(ananfang)};
	}
	
	
	private int macheSelektiv(){
		boolean c0 = SuchenSeite.selectWal[0];
		boolean c1 = SuchenSeite.selectWal[1];
		boolean c2 = SuchenSeite.selectWal[2];
		boolean c3 = SuchenSeite.selectWal[3];
		if((c0) && (!c1) && (!c2) && (!c3)){
			return 0;
		}
		if((!c0) && (c1) && (!c2) && (!c3)){
			return 1;
		}
		if((c0) && (c1) && (!c2) && (!c3)){
			return 2;
		}
		if((c0) && (c1) && (c2) && (!c3)){
			return 3;
		}
		if((c0) && (c1) && (!c2) && (c3)){
			return 4;
		}
		if((c0) && (c1) && (c2) && (c3)){
			return 5;
		}

		return -1;
	}
	private boolean keinTest(){
		return ((!SuchenSeite.schicht) &&(!SuchenSeite.selektiv) ? true : false);
	}	
	public void trefferSetzen(){
		SwingUtilities.invokeLater(new Runnable(){
			public  void run(){
				SuchenSeite.setzeTreffer(treffer);						       	  	
			}
		});
	}
	private String macheStatement(String sqldatum){
		String ret = "";
		int i,lang;
		int isuchen = 0;
		String ssuchen = "";
		String stmt = "";
		lang = SuchenSeite.getKollegenEinstellen().length;
	
			for (i=0;i<lang;i++){
				
				if(((Boolean)SuchenSeite.getKollegenEinstellen()[i][1]) ){
					//Verändert*****************
					//System.out.println("Inhalt von [i]="+i+" / Inhalt von [5=]"+SuchenSeite.getKollegenEinstellen()[i][5] );
					int dbZeile = (Integer) SuchenSeite.getKollegenEinstellen()[i][0];
					//int dbZeile = (Integer) SuchenSeite.getKollegenEinstellen()[i][5];
					ssuchen = (dbZeile<=9 ? "0"+(dbZeile)+"BEHANDLER" : (dbZeile)+"BEHANDLER");
					if(isuchen==0){
						stmt = "select * from flexkc where datum = '";
						stmt = stmt+sqldatum+"' AND (BEHANDLER='"+ssuchen+"'";
						++isuchen;
					}else{
						stmt = stmt+" OR BEHANDLER='"+ssuchen+"'";
						++isuchen;
					}
				}
			}
			if(isuchen > 0){
				stmt = stmt+") ORDER BY BEHANDLER";
			}
		return stmt;
	}
			
	
	
/************ alternative ***********/
	private boolean freiTermin(String name,String nummer) throws SQLException{
		if(name.equals("") && nummer.equals("") ){
			return true;
		}	
		return false;
	}
	
	private Vector sucheNachFreien(ResultSet rs,String name,String nummer,String skollege,int ikollege,int ii,int defdauer) throws SQLException{
		Vector vec = null;
		if(name.equals("") && nummer.equals("") ){
			vec = macheVector(rs,name,nummer,skollege,ikollege,ii,defdauer);
		}	
		return vec;
	}
	private Vector sucheNachGruppenFreien(ResultSet rs,String name,String nummer,String skollege,int ikollege,int ii,int defdauer,int gruppennr,Vector grupdat,boolean suchleer) throws SQLException{
//		System.out.println("Gültig ab:"+ datFunk.WertInDatum(SystemConfig.oGruppen.gruppenGueltig.get(gruppennr)[0]));
		/*
		*/
		Vector vec = null;
		vec = macheGruppenVector(rs,name,nummer,skollege,ikollege,ii,defdauer,grupdat,suchleer);
		return vec;
	}

	private Vector sucheNachNamen(ResultSet rs,String name,String nummer,String skollege,int ikollege,int ii) throws SQLException{
		Vector vec = null;
		if(name.contains(suchkrit1)){
			vec = macheVector(rs,name,nummer,skollege,ikollege,ii,-1);
		}	
		return vec;
	}
	private Vector sucheNachSelect(ResultSet rs,String name,String nummer,String skollege,int ikollege,int ii,int defdauer) throws SQLException{
		Vector vec = null;
		vec = macheVector(rs,name,nummer,skollege,ikollege,ii,defdauer);
		vec.set(6, SuchenSeite.selektbeginn);
		return vec;
	}
	private Vector sucheNachKGG(ResultSet rs,String name,String nummer,String skollege,int ikollege,int ii,int defdauer) throws SQLException{
		Vector vec = null;
		vec = macheKGGVector(rs,name,nummer,skollege,ikollege,ii,defdauer);
		return vec;
	}
	
	private Vector macheVector(ResultSet rs,String name,String nummer,String skollege,int ikollege,int ii,int defdauer) throws SQLException{
		Vector vec = new Vector();
		String uhrzeit;
		String sorigdatum;
		String sdatum;

		uhrzeit = rs.getString("TS"+(ii));
		
		sorigdatum = rs.getString(305); 
		sdatum = datFunk.sDatInDeutsch(sorigdatum);
		skollege = (String) ParameterLaden.getKollegenUeberReihe(ikollege);
		//{"x?","G!","Datum","Beginn","Ende","Min.","Namen","Rez.Nr.","Behandler","Druckzeit","Sort","Spalte","richtigesDatum","block","id-db"};
		vec.add(new Boolean(false));
		vec.add(null);
		vec.add(datFunk.WochenTag(sdatum).substring(0,2)+"-"+sdatum);
		vec.add(uhrzeit.substring(0,5));
		vec.add(rs.getString("TE"+(ii)).trim().substring(0,5));
		vec.add(rs.getString("TD"+(ii)).trim());		
		vec.add(rs.getString("TS"+(ii)).trim().substring(0,5));
		vec.add((defdauer == -1 ? rs.getString("TD"+(ii)).trim() : new Integer(defdauer).toString() ) );
		vec.add(name);
		vec.add(nummer);								
		vec.add(skollege);								
		vec.add("");								
		vec.add(""); //früher sorter
		vec.add(rs.getString("BEHANDLER"));		
		vec.add(sdatum);
		vec.add(sorigdatum);
		vec.add(ii);
		vec.add(rs.getString("id"));
		vec.add(rs.getString("BELEGT"));
		vec.add(new Boolean(false));

		return vec;
	}

	private Vector macheKGGVector(ResultSet rs,String name,String nummer,String skollege,int ikollege,int ii,int defdauer) throws SQLException{
		Vector vec = new Vector();
		String uhrzeit;
		String sorigdatum;
		String sdatum;

		uhrzeit = rs.getString("TS"+(ii));
		
		sorigdatum = rs.getString(305); 
		sdatum = datFunk.sDatInDeutsch(sorigdatum);
		skollege = (String) ParameterLaden.getKollegenUeberReihe(ikollege);
		//{"x?","G!","Datum","Beginn","Ende","Min.","Namen","Rez.Nr.","Behandler","Druckzeit","Sort","Spalte","richtigesDatum","block","id-db"};
		vec.add(new Boolean(false));
		vec.add(null);
		vec.add(datFunk.WochenTag(sdatum).substring(0,2)+"-"+sdatum);
		vec.add(uhrzeit.substring(0,5));
		vec.add(rs.getString("TE"+(ii)).trim().substring(0,5));
		vec.add(rs.getString("TD"+(ii)).trim());		
		vec.add(rs.getString("TS"+(ii)).trim().substring(0,5));
		vec.add((defdauer == -1 ? rs.getString("TD"+(ii)).trim() : new Integer(defdauer).toString() ) );
		vec.add(name);
		vec.add(nummer);								
		vec.add(skollege);								
		vec.add(rs.getString("TS"+(ii)).trim().substring(0,2)+":00");								
		vec.add(skollege); //früher sorter
		vec.add(rs.getString("BEHANDLER"));		
		vec.add(sdatum);
		vec.add(sorigdatum);
		vec.add(ii);
		vec.add(rs.getString("id"));
		vec.add(rs.getString("BELEGT"));
		vec.add(new Boolean(false));

		return vec;
	}

	private Vector macheGruppenVector(ResultSet rs,String name,String nummer,String skollege,int ikollege,int ii,int defdauer,Vector vecgruppe,boolean suchleer) throws SQLException{
		Vector vec = new Vector();
		String uhrzeit;
		String sorigdatum;
		String sdatum;

		uhrzeit = rs.getString("TS"+(ii));
		
		sorigdatum = rs.getString(305); 
		sdatum = datFunk.sDatInDeutsch(sorigdatum);
		skollege = (String) ParameterLaden.getKollegenUeberReihe(ikollege);
		//{"x?","G!","Datum","Beginn","Ende","Min.","Namen","Rez.Nr.","Behandler","Druckzeit","Sort","Spalte","richtigesDatum","block","id-db"};
		vec.add(new Boolean(false));
		vec.add(null);
		vec.add(datFunk.WochenTag(sdatum).substring(0,2)+"-"+sdatum);
		vec.add(uhrzeit.substring(0,5));
		vec.add(rs.getString("TE"+(ii)).trim().substring(0,5));
		vec.add(rs.getString("TD"+(ii)).trim());		
		vec.add(rs.getString("TS"+(ii)).trim().substring(0,5));
		vec.add( new Integer(defdauer).toString() );
		boolean xgruppe = false;
		//if(name.trim().equals("")){
			String snam = null;
			if(((String)vecgruppe.get(3)).toUpperCase().contains("@BEHANDLER")){
				/******** hier noch das handling für Teile des Namens anzeigen *******/
				if(name.length() >= 5){
					snam = name.substring(0,5)+"\\\\"+((String)vecgruppe.get(3)).split("@")[0]+"-"+vecgruppe.get(4)+"Min.";
				}else{
					snam = ((String)vecgruppe.get(3)).split("@")[0]+"-"+vecgruppe.get(4)+"Min.";					
				}				
				xgruppe = true;
			}else{
				if(name.length() >= 5){
					snam = name.substring(0,5)+"\\\\"+vecgruppe.get(3)+"-"+vecgruppe.get(4)+"Min.";
				}else{
					snam = vecgruppe.get(3)+"-"+vecgruppe.get(4)+"Min.";					
				}
				
			}
			vec.add(snam);
		//}else{
			//vec.add(name);			
		//}
		vec.add(nummer);								
		vec.add(skollege);
		String drzeit = (String)vecgruppe.get(2);
		vec.add((drzeit.trim().equals("00:00") ? "--:--" : vecgruppe.get(2)));								
		vec.add((xgruppe ? skollege : ""));  //früher sorter
		vec.add(rs.getString("BEHANDLER"));		
		vec.add(sdatum);
		vec.add(sorigdatum);
		vec.add(ii);
		vec.add(rs.getString("id"));
		vec.add(rs.getString("BELEGT"));
		vec.add(new Boolean(true));

		return vec;
	}

	

/**********************************************************************/
/**********************************************************************/
/**********************************************************************/
private synchronized int XSperrenVerarbeiten(int akt,Vector vecx,String zeit){
	Statement stmtx = null;
	ResultSet rsx = null;

	/*
	try {
		Reha.thisClass.conn.setAutoCommit(true);
	} catch (SQLException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	*/
	String sperre;
	sperre = new String((String)((Vector)vecx).get(13)+
						(String)((Vector)vecx).get(14) ); 

		//if(! sperrDatum.contains(sperre+SystemConfig.dieseMaschine)){
			stmtx = null;
			rsx = null;
			try {
				stmtx = (Statement) Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
				        ResultSet.CONCUR_UPDATABLE );
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				rsx = stmtx.executeQuery("select sperre,maschine from flexlock where sperre='"+sperre+"'");
				if(!rsx.next() ){
					//this.sperrDatum.add(sperre+SystemConfig.dieseMaschine+zeit);
					String st = "insert into flexlock set sperre='"+sperre+"', maschine='"+SystemConfig.dieseMaschine+"', "+
					"zeit='"+zeit+"'";
					stmtx.execute(st);
					//new ExUndHop().setzeStatement(new String(st));
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return(0);
				}else{
					return(1);
					/*
					if(! sperrDatum.contains(sperre+SystemConfig.dieseMaschine+SuchenSeite.getZeit())){
						return(1);
					}else{
						return(0);
					}
					*/

				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
					
		//}
		

		
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
				
		return 0;
	}

private synchronized void malen(ImageIcon bild,int wo){
		SuchenSeite.getInstance().dtblm.setValueAt(bild, wo, 1);
	}


}

/************************Ende des WorkerThreads************************/
/**********************************************************************/
/**********************************************************************/
/**********************************************************************/	

/**********************************************************************/

    
@SuppressWarnings("unchecked")	
class MyDefaultTableModel extends DefaultTableModel{
	   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Class getColumnClass(int columnIndex) {
		   if(columnIndex==0){return Boolean.class;}
		  /* if(columnIndex==1){return JLabel.class;}*/
		   else{return String.class;}
           //return (columnIndex == 0) ? Boolean.class : String.class;
       }

	    public boolean isCellEditable(int row, int col) {
	        //Note that the data/cell address is constant,
	        //no matter where the cell appears onscreen.
	        if (col == 0){
	        	return true;
	        }else if(col == 6){
	        	return true;
	        }else if(col == 7){
	        	return true;
	        }else if(col == 11){
	        	return true;
	        } else{
	          return false;
	        }
	      }
	   
}
/*******************************************************************/
/*******************************************************************/
/**************************************************/
class EntsperreSatz extends Thread implements Runnable{
	public void run(){
		Statement stmt = null;

		try {
			stmt = (Statement) Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			        ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			boolean rs = stmt.execute("delete from flexlock where maschine='"+SystemConfig.dieseMaschine+"' AND zeit='"+SuchenSeite.getZeit()+"'");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			if (stmt != null) {
				try {
					stmt.close();
					stmt = null;
				} catch (SQLException sqlEx) { // ignore }
					stmt = null;
				}
			}
		}
	}
}

/**************************************************/
/**************************************************/
final class WorkerTabelle extends SwingWorker<Void,Void>{
	boolean fertig = false;
	int aktuell = -1;
	public static ArrayList<String> sperrDatum = new ArrayList<String>();
	private ImageIcon img,img2;
	private String zeit;
	private int merken = -1;

	public void init(){
		img = SystemConfig.hmSysIcons.get("zuzahlnichtok");
		img2 = SystemConfig.hmSysIcons.get("zuzahlfrei");		
		img.setDescription("gesperrt");
		img2.setDescription("offen");
		SuchenSeite.setZeit();
		zeit = SuchenSeite.getZeit();
		sperrDatum.clear();		
	}
	public void setEnde(){
		fertig = true;
	}
	
	protected Void doInBackground() throws Exception {
		int anzahl;
		int verarbeitet;
		sperrDatum.clear();
		Vector nvec;
		String sperre;
		while(true){
			anzahl = SuchenSeite.sucheDaten.size();
			if( (SuchenSeite.mussUnterbrechen) && (anzahl==0) && (SuchenSeite.getTreffer()==anzahl) ){
				//System.out.println("Ausbruch bei 1");
				break;
			}
			if( (SuchenSeite.mussUnterbrechen) && (anzahl==(aktuell+1)) && (aktuell==(SuchenSeite.getTreffer()-1)) ){
				//System.out.println("Ausbruch bei 2");
				break;

			}

			if(anzahl > 0){
				if(aktuell != (anzahl-1)){
					 	
						aktuell++;
						SuchenSeite.verarbeitetLbl.setText(new Integer(aktuell+1).toString());
						//xxxx
						nvec = (Vector) ((Vector)SuchenSeite.sucheDaten.get(aktuell)).clone();
						
						//String sperre;
						
						sperre = new String((String)((Vector)nvec).get(13)+
											(String)((Vector)nvec).get(14) ); 

						if(sperrDatum.contains(sperre+SystemConfig.dieseMaschine+zeit)){
							nvec.set(1, img2);
						}else{
							//int ret = 0;
							int ret = XSperrenVerarbeiten(aktuell,nvec,zeit);
							if(ret==0){
								sperrDatum.add(sperre+SystemConfig.dieseMaschine+zeit);
							}
							nvec.set(1, (ret==0 ? img2 : img));
						}
						SuchenSeite.getInstance().dtblm.addRow(nvec);
						new Thread(){
							public void run(){
								SuchenSeite.getInstance().jxSucheTable.validate();
							}
						}.start();
						
						//SuchenSeite.getInstance().dtblm.addRow(nvec);
						/*
						SwingUtilities.invokeLater(new Runnable(){
							public void run(){
								SuchenSeite.getInstance().jxSucheTable.validate();																			
								//SuchenSeite.getInstance().jxSucheTable.repaint();									
								new Thread(){
									public void run(){
										
									}
								}.start();
							}
						});	
						*/
						//SuchenSeite.getInstance().jxSucheTable.repaint();


						if( (SuchenSeite.mussUnterbrechen) && (anzahl==(aktuell+1)) && (aktuell==(SuchenSeite.getTreffer()-1)) ){
							//System.out.println("WorkerThread - Unterbrechen bei anzahl = "+anzahl+" und aktuell == "+aktuell);
							//System.out.println("Ausbruch bei 3");
							break;

						}
				}
			}
			if( (SuchenSeite.mussUnterbrechen) 
					&& (anzahl==(aktuell+1)) 
					&& (aktuell==(SuchenSeite.getTreffer()-1)) 
					&& (SuchenSeite.getTreffer()==anzahl) ){
				//System.out.println("WorkerThread - Unterbrechen bei anzahl = "+anzahl+" und aktuell == "+aktuell);
				//System.out.println("Ausbruch bei 4");
				break;

			}
			
		}
		SuchenSeite.setWorkerFertig(true);
		nvec = null;
		sperre = null;
		//SuchenSeite.setKnopfGedoense(new int[]  {0,0,0,0,0,0,0,1,1,1});
		//JOptionPane.showMessageDialog(null,"Anzahl Tabellenzeilen: "+SuchenSeite.getInstance().jxSucheTable.getRowCount());
		//Reha.thisClass.conn.setAutoCommit(false);
		//SuchenSeite.setKnopfGedoense(new int[]  {0,0,0,0,0,0,0,1,1,1});
		//SuchenSeite.getInstance().tabelleEinschalten();
		//SuchenSeite.getInstance().listenerEinschalten();

		//JOptionPane.showMessageDialog(null,"Anzahl Tabellenzeilen: "+SuchenSeite.getInstance().jxSucheTable.getRowCount());
		return null;
	}
	/************************/
	private int XSperrenVerarbeiten(int akt,Vector vecx,String zeit){
		Statement stmtx = null;
		ResultSet rsx = null;
		boolean neu = true;
		
		String sperre;
		sperre = new String((String)((Vector)vecx).get(13)+
							(String)((Vector)vecx).get(14) );
		
		if(neu){
			String cmd = "sperre='"+sperre+"'";
			if(SqlInfo.zaehleSaetze("flexlock", cmd)==0){
				cmd = "insert into flexlock set sperre='"+sperre+"', maschine='"+SystemConfig.dieseMaschine+"', "+
				"zeit='"+zeit+"'";
				new ExUndHop().setzeStatement(cmd);
				return(0); 
			}else{
				return(1);				
			}
		}


			//if(! sperrDatum.contains(sperre+SystemConfig.dieseMaschine)){
				stmtx = null;
				rsx = null;
				try {
					stmtx = (Statement) Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					        ResultSet.CONCUR_UPDATABLE );
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					rsx = stmtx.executeQuery("select sperre,maschine from flexlock where sperre='"+sperre+"'");
					if(!rsx.next() ){
						//this.sperrDatum.add(sperre+SystemConfig.dieseMaschine+zeit);
						String st = "insert into flexlock set sperre='"+sperre+"', maschine='"+SystemConfig.dieseMaschine+"', "+
						"zeit='"+zeit+"'";
						stmtx.execute(st);
						//new ExUndHop().setzeStatement(new String(st));
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return(0);
					}else{
						return(1);
						/*
						if(! sperrDatum.contains(sperre+SystemConfig.dieseMaschine+SuchenSeite.getZeit())){
							return(1);
						}else{
							return(0);
						}
						*/

					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
						
			//}
			

			
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
					
			return 0;
		}
	
	
}
/**************************************************/


	
class TermObjekt implements Comparable<TermObjekt>{
	public String tag;
	public String beginn;
	public String termtext;
	public String sorter;
	
	public TermObjekt(String xtag,String xbeginn,String xtermtext,String xsorter){
		this.tag = new String(xtag);
		this.beginn = new String(xbeginn);
		this.termtext = new String(xtermtext);
		this.sorter =  new String(xsorter);
		
	}

	@Override
	public int compareTo(TermObjekt o) {
	      //First order by name
	      int result = sorter.compareTo(o.sorter);
	      if (0 == result) {
	        //if names are equal order by age, youngest first
	        result = termtext.compareTo(o.termtext);
	      }
	      return result;
	  }

	
}
class Rdaten extends Observable{
	public Vector rvec; 
	public Rdaten(){
		super();
		rvec = new Vector();
	}
	
}