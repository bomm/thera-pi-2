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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
import org.therapi.reha.patient.LadeProg;


import rechteTools.Rechte;
import systemEinstellungen.SystemConfig;
import systemTools.Colors;
import systemTools.IntegerTools;
import systemTools.JRtaTextField;
import systemTools.StringTools;
import terminKalender.ParameterLaden;
import terminKalender.DatFunk;
import terminKalender.ZeitFunk;

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
	public SuchenSeite thisClass = null; 
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
	public Vector sucheDaten = new Vector();
	//public static Rdaten rooDaten = new Rdaten();
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
	public String[] selectUhr = {null,null,null,null}; 
	public boolean[] selectWal = {false,false,false,false};
	public String[] schichtUhr = {null,null}; 
	public boolean[] schichtWal = {false,false};	
	public boolean[] schichtVor = {false,false};
	public String[] kollegenAbteilung = null;
	public boolean[] kollegenSuchen = null;
	public JScrollPane ptc = null;
	public boolean workerfertig = true;
	public String selektbeginn;
	public String schichtbeginn;
	public RoogleFenster eltern;
	SuchenSeite(RoogleFenster xeltern){
		super();
		setBorder(null);
		setLayout(new GridBagLayout());
		erstelleGridBag();
		validate();
		addFocusListener(this);
		addPropertyChangeListener(this);
		addKeyListener(this);
		this.eltern = xeltern;
	}

	public void setWorkerFertig(boolean wert){
		workerfertig = wert;
	}
	public SuchenSeite getInstance(){
		return this;
	}
	public void setzeDatum(String sdat){
		aktLbl.setText(sdat);
	}
	public void setKollegenSuchen(boolean[] kollSuchen){
		kollegenSuchen = kollSuchen; 
	}
	public boolean getKollegenSuchen(int koll){
		return this.kollegenSuchen[koll]; 
	}
	public void setKollegenAbteilung(String[] kolls){
		kollegenAbteilung = kolls;
	}
	public String getKollegenAbteilung(int koll){
		return this.kollegenAbteilung[koll];
	}
	public void setzeTreffer(int treffer){
		trefferLbl.setText(Integer.toString(treffer));
	}
	public void setzeZeilenAusgewaehlt(int xgewaehlt){
		ausgewaehltLbl.setText(Integer.toString(xgewaehlt));
	}
	public String getStartDatum(){
		return startLbl.getText();
	}
	public String getStopDatum(){
		return stopLbl.getText();
	}
	public String getAktDatum(){
		return aktLbl.getText();
	}
	public String getSuchName(){
		return sucheName.getText();
	}
	public String getSuchNummer(){
		return sucheNummer.getText();
	}
	public void setSucheBeendet(){
		mussUnterbrechen = true;
		sucheStoppen.setEnabled(false);
		sucheStarten.setEnabled(true);				
	}
	public boolean getSucheBeendet(){
		return mussUnterbrechen;
	}
	public void setFortschrittRang(int von, long bis){
		fortschritt.setForeground(Color.RED);
		fortschritt.setMinimum(von);
		fortschritt.setMaximum((int) bis);
	}
	public void setFortschrittSetzen(int wert){
		fortschritt.setValue(wert);
	}
	public void setFortschrittZeigen(boolean zeigen){
		fortschritt.setVisible(zeigen);		
	}


	synchronized public void setDatenVector(Vector vec){
		sucheDaten.add(vec);
	}
	synchronized public Vector getDatenVector(){
		return (Vector)sucheDaten;
	}
	public static void verarbeitungEinschalten(){
	}
	
	public void setKollegenEinstellen(Object[][] obj){
		sucheKollegen = obj; //obj.clone();
	}
	public Object[][] getKollegenEinstellen(){
		return sucheKollegen;
	}
	public void setKollegenZeiten(HashMap<String,Integer> xhZeiten){
		hZeiten =  ((HashMap<String,Integer>)xhZeiten);//((HashMap<String,Integer>)hZeiten.clone());
	}
	public HashMap<String,Integer> getKollegenZeiten(){
		return this.hZeiten; 
		
	}
	public void setGewaehlt(int gewaehlt){
		this.gewaehlt = gewaehlt;
	}
	public int getGewaehlt(){
		return this.gewaehlt;
	}
	public void setZeit(){
		this.zeit = Long.valueOf(System.currentTimeMillis()).toString();	
	}
	public String getZeit(){
		return this.zeit;	
	}
	
	public int getTreffer(){
		return Integer.parseInt(trefferLbl.getText());
	
	}
	public boolean tagDurchsuchen(String sdatum){
		boolean ret = false;
		if(suchenTage[DatFunk.TagDerWoche(sdatum)-1]==0){
			ret = false;
		}else{
			ret = true;
		}
		return ret;
	}
	public void datumEinstellen(){
		startLbl.setText(eltern.zeitraumEdit[0].getText());
		stopLbl.setText(eltern.zeitraumEdit[1].getText());		
	}
	public void tageEinstellen(){
		for(int i = 0;i<7;i++){
			suchenTage[i] = (eltern.tageCheck[i].isSelected() ? 1 : 0);
		}
		sucheName.requestFocus();
	}
	public void behandlerEinstellen(){
	}

	public void zeileTesten(Vector vtest){
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
	     panLinks.setBackgroundPainter(Reha.thisClass.compoundPainter.get("SuchenSeite"));
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
		schreibeName.setToolTipText("Mit diesem Feld können Sie bestimmen mit was oder wem (evtl.) später Termineinträge überschrieben werden");
		builder.add(schreibeName,cc.xy(2,11));

		schreibeNummer = new JRtaTextField("GROSS",true);
		schreibeNummer.setToolTipText("Mit diesem Feld geben Sie an welche Rezeptnummer (evtl.) später eingetragen werden soll");
		builder.add(schreibeNummer,cc.xyw(4,11,3));

		if(RoogleFenster.gedropt){
			if(RoogleFenster.sldrops[0].length() > 10){
				sucheName.setText(RoogleFenster.sldrops[0].substring(0,10));			
			}else{
				sucheName.setText(RoogleFenster.sldrops[0]);
			}
			schreibeName.setText(RoogleFenster.sldrops[0]);
			schreibeNummer.setText(RoogleFenster.sldrops[1]);
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
		sucheStarten.setIcon(SystemConfig.hmSysIcons.get("buttongruen") );
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
		sucheStoppen.setIcon(SystemConfig.hmSysIcons.get("buttonrot"));
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
		
		auswahlInDatei = new  JButton("Auswahl exportieren");
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
		
		
		setKnopfGedoense(new int[]  {1,0,0,0,0,0,0,0,0,0});
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

		((TableColumnExt)jxSucheTable.getColumn(0)).setCellEditor(new JXTable.BooleanEditor());

		jxSucheTable.getColumn(0).setMinWidth(25);	
		jxSucheTable.getColumn(0).setMaxWidth(25);

		TableCellRenderer renderer = new DefaultTableRenderer(new MappedValue(StringValues.EMPTY, IconValues.ICON), JLabel.CENTER);
		// original   TableCellRenderer renderer = new DefaultTableRenderer(new MappedValue(StringValue.EMPTY, IconValue.ICON), JLabel.CENTER);

		// ImageIcon f�r gesperrt oder nicht
		jxSucheTable.getColumn(1).setCellRenderer(renderer);		
		jxSucheTable.getColumn(1).setMinWidth(20);	
		jxSucheTable.getColumn(1).setMaxWidth(20);
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
		// �brige daten sind versteckt
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
		startLbl.setText(eltern.zeitraumEdit[0].getText());
		stopLbl.setText(eltern.zeitraumEdit[1].getText());		
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
				new SwingWorker<Void,Void>(){

					@Override
					protected Void doInBackground() throws Exception {
						knopfGedoense(new int[]  {1,0,0,0,0,0,0,0,0,0});
						roogleZuruecksetzen();
						return null;
					}
					
				}.execute();
				//Runtime r = Runtime.getRuntime();
			    //r.gc();
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
					JOptionPane.showMessageDialog(null,"So so...,Sie suchen 'freie Termine' und wollen diese dann mit 'freien Terminen' überschreiben...\nOh Herr schmeiß Hirn ra!");
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
			if(name.equals("export")){
				if(gewaehlt==0){
					JOptionPane.showMessageDialog(null,"So so...,Sie haben zwar nix gewählt wollen es aber schon mal exportieren - das NIX....\nOh Herr schmeiß Hirn ra!");
					return;
				}
				cursorWait(true);
				SwingUtilities.invokeLater(new Runnable(){
					public  void run(){
						new Thread(){
							public void run(){
								auswahlExportieren();								
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
		if(!Rechte.hatRecht(Rechte.Rugl_write, false)){
			auswahlUebernahme.setEnabled(false);
		}else{
			auswahlUebernahme.setEnabled((knopf[3]==1 ? true : false));			
		}
		auswahlDrucken.setEnabled((knopf[4]==1 ? true : false));
		auswahlPerEmail.setEnabled((knopf[5]==1 ? true : false));
		auswahlInDatei.setEnabled((knopf[6]==1 ? true : false));		
		allesMarkieren.setEnabled((knopf[7]==1 ? true : false));		
		allesEntmarkieren.setEnabled((knopf[8]==1 ? true : false));		
		allesZuruecksetzen.setEnabled((knopf[9]==1 ? true : false));		
	}
	public void setKnopfGedoense(int[] knopf){
		knopfGedoense(knopf);
	}
	private void roogleStarten(){
		listenerAusschalten();
		this.zeilengewaehlt = 0;
		setzeZeilenAusgewaehlt(this.zeilengewaehlt);
		EntsperreSatz es = new EntsperreSatz();
		es.setzeEltern(getInstance());
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
				WsIT.setzeStatement(getInstance());
				WsIT.execute();
				
				/*
				SwingUtilities.invokeLater(new Runnable(){
					public  void run(){
						getInstance().workerfertig = false;
						
						WorkerTabelle2 wt = new WorkerTabelle2();
						wt.init();
						wt.execute();
					}
				});
				*/
					
				
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
		dtblm.getDataVector().trimToSize();
		dtblm.setRowCount(0);
		jxSucheTable.removeAll();
		sucheDaten.clear();
		sucheDaten.trimToSize();
		jxSucheTable.clearSelection();
		jxSucheTable.validate();
		jxSucheTable.repaint();
		EntsperreSatz es = new EntsperreSatz();
		es.setzeEltern(getInstance());
		es.start();
		trefferLbl.setText("0");
		ausgewaehltLbl.setText("0");
		verarbeitetLbl.setText("0");
		vecWahl.clear();
		vecWahl.trimToSize();
		this.zeilengewaehlt = 0;
		sucheName.requestFocus();
	}
	private void auswahlExportieren(){
		JRtaTextField exportart = new JRtaTextField("nix",false);
		ExportWahl exwahl = new ExportWahl(auswahlInDatei.getLocationOnScreen(),exportart,"Was soll exportiert werden?");
		exwahl.setVisible(true);
		if(exportart.getText().equals("rehaplan")){
			if(rehaplanExport());
			JOptionPane.showMessageDialog(null, "Plandateien wurden erfolgreich exportiert");
		}else if(exportart.getText().equals("fahrdienstliste")){
			if(fahrdienstExport());
			JOptionPane.showMessageDialog(null, "Daten für Fahrdienstliste wurden erfolgreich exportiert");
		}
		cursorWait(false);
	}	
	private boolean fahrdienstExport(){
		try{
			exportVectorInit();
			int lang = vecWahl.size();
			if(lang <=0){
				JOptionPane.showMessageDialog(null, "Keine Termine zum Exportieren in die Fahrdienstliste ausgewählt");
				return false;
			}
			//Druckzeit sofern vorhanden in offizielle Druckzeit übertragen
			for(int i = 0; i < lang;i++){
				if(! ((Vector<String>)vecWahl.get(i)).get(11).trim().equals("")){
					((Vector<String>)vecWahl.get(i)).set(6, ((Vector<String>)vecWahl.get(i)).get(11));
				}
			}
			Comparator<Vector> comparator = new Comparator<Vector>() {
				@Override
				public int compare(Vector o1, Vector o2) {
					String s1 = ((String)o1.get(15))+"/"+((String)o1.get(8)).replace("KGG-", "")+"/"+((String)o1.get(6));
					String s2 = ((String)o2.get(15))+"/"+((String)o2.get(8)).replace("KGG-", "")+"/"+((String)o2.get(6));
					////System.out.println("Ergebnis der Sortierung = "+s1.compareTo(s2)+" S1="+s1+" / S2="+s2);
					return s1.compareTo(s2);
				}
			};
			Collections.sort(vecWahl,comparator);
			FileOutputStream outputFile = new  FileOutputStream(SystemConfig.hmVerzeichnisse.get("Fahrdienstrohdatei")+"FPSort.txt");
            OutputStreamWriter out = new OutputStreamWriter(outputFile, "ISO-8859-1"); 
			BufferedWriter bw = null;
			String drzeit = "";
			bw = new BufferedWriter(out);
			//bw = new BufferedWriter(new FileWriter(SystemConfig.hmVerzeichnisse.get("Fahrdienstrohdatei")+"FPSort.txt"));
			for(int i = 0; i < lang;i++){
				if( ((Vector<Boolean>)vecWahl.get(i)).get(19) ){
					//dauer holen
					drzeit = getGruppendauer(((Vector<String>)vecWahl.get(i)).get(8));
				}else{
					drzeit = getGruppendauer(((Vector<String>)vecWahl.get(i)).get(5));
				}
				bw.write(((Vector<String>)vecWahl.get(i)).get(15)+"°"+((Vector<String>)vecWahl.get(i)).get(6)+
						"°"+((Vector<String>)vecWahl.get(i)).get(10)+"°"+((Vector<String>)vecWahl.get(i)).get(8).replace("KGG-", "")+
						"°"+((Vector<String>)vecWahl.get(i)).get(9)+"°"+drzeit
						);
				bw.newLine();
			}
			bw.flush();
			bw.close();
			out.close();
			outputFile.close();
			new LadeProg(Reha.proghome+"FahrdienstExporter.jar " +
					Reha.proghome+"ini/"+Reha.aktIK+"/rehajava.ini");
		}catch(Exception ex){
			ex.printStackTrace();
			cursorWait(false);
			return false;
			
		}
		cursorWait(false);
		return true;
	}
	private boolean rehaplanExport(){	
		exportVectorInit();
		int lang = vecWahl.size();
		if(lang <=0){
			JOptionPane.showMessageDialog(null, "Keine Termine zum Exportieren nach RehaPlaner ausgewählt");
			return false;
		}
		if(sucheName.getText().trim().equals("")){
			JOptionPane.showMessageDialog(null, "Kein Name für die Exportdatei vorhanden (Suchefeld=leer)"+
					"\n\nExport in Plandatei nicht möglich\n\n");
			return false;
		}

		//Druckzeit sofern vorhanden in offizielle Druckzeit übertragen
		for(int i = 0; i < lang;i++){
			if(! ((Vector<String>)vecWahl.get(i)).get(11).trim().equals("")){
				((Vector<String>)vecWahl.get(i)).set(6, ((Vector<String>)vecWahl.get(i)).get(11));
			}
		}
		Comparator<Vector> comparator = new Comparator<Vector>() {
			@Override
			public int compare(Vector o1, Vector o2) {
				String s1 = ((String)o1.get(15))+((String)o1.get(6));
				String s2 = ((String)o2.get(15))+((String)o2.get(6));
				return s1.compareTo(s2);
			}
		};
		Collections.sort(vecWahl,comparator);
		////System.out.println(vecWahl);
		String drzeit = "";
		String gruppe = "";
		int kalzeile = -1;
		int kollege = -1;
		String zeile = "";

		try {
			FileOutputStream outputFile = new  FileOutputStream(SystemConfig.hmVerzeichnisse.get("Rehaplaner")+sucheName.getText().trim()+".txt");
	        OutputStreamWriter out = new OutputStreamWriter(outputFile, "ISO-8859-1"); 
			BufferedWriter bw = null;
			bw = new BufferedWriter(out);

			for(int i = 0; i < lang;i++){
				//Wenn Gruppensignal gesetzt
				if( ((Vector<Boolean>)vecWahl.get(i)).get(19) ){
					//dauer holen
					drzeit = getGruppendauer(((Vector<String>)vecWahl.get(i)).get(8));
				}else{
					drzeit = getGruppendauer(((Vector<String>)vecWahl.get(i)).get(5));
				}
				//Jetzt die gruppenzugehörigkeit holen
				try{
					kalzeile = Integer.parseInt(((Vector<String>)vecWahl.get(i)).get(13).substring(0,2));
					gruppe = ParameterLaden.searchAbteilung(kalzeile);
					////System.out.println(((Vector<String>)vecWahl.get(i)).get(6)+"/"+((Vector<String>)vecWahl.get(i)).get(10));
				}catch(Exception ex){
					ex.printStackTrace();
				}
				//                        Datum auf deutsch                     Anfangszeit(=Druckzeit)
				bw.write(((Vector<String>)vecWahl.get(i)).get(14)+"°"+((Vector<String>)vecWahl.get(i)).get(6)+
				//   ermittelte Dauer                     Gruppenkürzel
						"°"+drzeit+"°"+(gruppe.trim().equals("") ? "--" : gruppe)+"°"+
				//   Matchcode Kalenderbenutzer
						((Vector<String>)vecWahl.get(i)).get(10)+"°"+
				//		
						((Vector<String>)vecWahl.get(i)).get(8).replaceFirst("\\\\", "").replace("\\", "/Gruppe:_")
						
				);
				bw.newLine();
			}
			bw.flush();
			bw.close();
			out.close();
			outputFile.close();
		} catch (IOException e) {
			e.printStackTrace();
			cursorWait(false);
			return false;
		}
		cursorWait(false);
		return true;
	}
	private String getGruppendauer(String zeit){
		String sret = "30";
		int lastline = zeit.lastIndexOf("-");
		String nurmin = zeit.substring(lastline+1);
		sret = nurmin.split("Min.")[0];
		return sret;
	}

	private void auswahlDrucken(boolean drucken){
		int lang,i;
		TermObjekt termin = null;
		Vector<TermObjekt> vec = new Vector<TermObjekt>();
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
							termtext = ((String)((Vector)vecWahl.get(i)).get(10));  // neu eingef�gt am 06.04.2009
						}
					}else{
						termtext = ((String)((Vector)vecWahl.get(i)).get(8));
						if(termtext.contains("Gruppe:_")){
							termtext = termtext.substring(8);
						}else{
							termtext = ((String)((Vector)vecWahl.get(i)).get(10));  // neu eingef�gt am 06.04.2009
						}
					}

				}else{
					termtext = ((String)((Vector)vecWahl.get(i)).get(10));
				}
				vec.add(new TermObjekt(tag,druckzeit,termtext,sorter));
				

				
			}
			Collections.sort(vec);
			new TerminplanDrucken().init((Vector<TermObjekt>)vec, drucken,schreibeName.getText().trim(),schreibeNummer.getText().trim(),getInstance());
		}
		
	}
	@Override
	public void tableChanged(TableModelEvent arg0) {
		if(arg0.getType() == TableModelEvent.INSERT){
			////System.out.println("Tabellen-Zeile eingef�gt");
		}
		if(arg0.getType() == TableModelEvent.UPDATE){

			if(jxSucheTable.isEditable()){
				if(arg0.getColumn() == 0){
					String sym = ((ImageIcon)jxSucheTable.getValueAt(arg0.getFirstRow(),1)).getDescription();
					if(sym.equals("gesperrt") && ((Boolean)jxSucheTable.getValueAt(arg0.getFirstRow(),0)) ){
						jxSucheTable.setValueAt(Boolean.valueOf(false),arg0.getFirstRow(),0);
					}else if(!sym.equals("gesperrt")){
						if( ((Boolean)jxSucheTable.getValueAt(arg0.getFirstRow(),0)) ){
							this.zeilengewaehlt++;
							setzeZeilenAusgewaehlt(this.zeilengewaehlt);
							if(this.zeilengewaehlt==1){
								if(!Rechte.hatRecht(Rechte.Rugl_write, false)){
									auswahlUebernahme.setEnabled(false);									
								}else{
									auswahlUebernahme.setEnabled(true);									
								}
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
					if(((String)jxSucheTable.getValueAt(arg0.getFirstRow(), 11)).trim().equals(":")){
						jxSucheTable.setValueAt("", arg0.getFirstRow(), 11);
					}
				}
				if(arg0.getColumn() == 6){
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
						// Pr�fung einbauen ob Beginnzeit + Dauer nicht Endzeit �bersteigt bzw. vor Planbeginnzeit liegt.
						//jxSucheTable.setValueAt("", arg0.getFirstRow(), 6);
				}
				if(arg0.getColumn() == 7){
						if(testeZeiten(arg0.getFirstRow(),arg0.getColumn()) > 0){
							jxSucheTable.setValueAt(jxSucheTable.getValueAt(arg0.getFirstRow(),5), arg0.getFirstRow(), 7);	
						}
						//jxSucheTable.setValueAt("", arg0.getFirstRow(), 7);
						// Pr�fung einbauen ob Beginnzeit + Dauer nicht Endzeit �bersteigt bzw. vor Planbeginnzeit liegt.
				}

			}

		}
	}
	private int testeZeiten(int reihe, int zeile){
		int ret = 0;
		int tkstart,tkende,plstart,pldauer;
		  tkstart = (int) ZeitFunk.MinutenSeitMitternacht(((String)jxSucheTable.getValueAt(reihe, 3)).trim()+":00");
		  tkende = (int) ZeitFunk.MinutenSeitMitternacht(((String)jxSucheTable.getValueAt(reihe, 4)).trim()+":00");
		  plstart = (int) ZeitFunk.MinutenSeitMitternacht(((String)jxSucheTable.getValueAt(reihe, 6)).trim()+":00");
		  pldauer = Integer.parseInt((String)jxSucheTable.getValueAt(reihe, 7));
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
						dtblm.setValueAt(Boolean.valueOf(true),i, 0);
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
			if(!Rechte.hatRecht(Rechte.Rugl_write, false)){
				auswahlUebernahme.setEnabled(false);									
			}else{
				auswahlUebernahme.setEnabled(true);									
			}			
			auswahlDrucken.setEnabled(true);
			auswahlPerEmail.setEnabled(true);
			auswahlInDatei.setEnabled(true);
			sucheWeiter.setEnabled(true);
		}else{
			listenerAusschalten();			
			for(i=0;i<bis;i++){
				dtblm.setValueAt(Boolean.valueOf(false),i, 0);
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

		//name = new String(schreibeName.getText().trim());
		//nummer = new String(schreibeNummer.getText().trim().replace("\\", "\\\\") );
		////System.out.println("Rezeptnummer = "+nummer);

		for(i=0;i<lang;i++){
			
			if((Boolean)dtblm.getValueAt(i,0)){

				setFortschrittSetzen(durchlauf++);
				vecWahl.add(dtblm.getDataVector().get(i));
				((Vector)vecWahl.get(vecWahl.size()-1)).set(11, (String) dtblm.getValueAt(i,11));
				((Vector)vecWahl.get(vecWahl.size()-1)).set(6, (String) dtblm.getValueAt(i,6));		
			}
		}	
	}
	private void exportVectorInit(){
		int lang = dtblm.getRowCount(),i;
		int durchlauf = 0;
		Vector vec = null;
		String name,nummer;
		vecWahl.clear();
		for(i=0;i<lang;i++){
			if((Boolean)dtblm.getValueAt(i,0)){
				setFortschrittSetzen(durchlauf++);
				vecWahl.add( ((Vector<String>)dtblm.getDataVector().get(i)).clone());
				((Vector)vecWahl.get(vecWahl.size()-1)).set(11, (String) dtblm.getValueAt(i,11));
				((Vector)vecWahl.get(vecWahl.size()-1)).set(6, (String) dtblm.getValueAt(i,6));		
			}
		}	
	}
	
	/********************************************************/
	class SchreibeAuswahl extends SwingWorker<Void,Void>{

		@Override
		protected Void doInBackground() throws Exception {
			//Hier wird gel�scht unbedingt vorher fragen bevor
			if(  (schreibeName.getText().trim().equals("")) && (schreibeNummer.getText().trim().equals(""))){
				int anfrage = JOptionPane.showConfirmDialog(null, "Wollen Sie wirklich die ausgewählten Termine löschen (=freigeben) ?", "Achtung wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
				if(anfrage == JOptionPane.NO_OPTION){
					setKnopfGedoense(new int[]  {0,0,0,1,1,1,1,1,1,1});
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
		setFortschrittRang(0,this.zeilengewaehlt );
		setFortschrittSetzen(0);
		setFortschrittZeigen(true);

		vecWahl.clear();
		
		name = schreibeName.getText().trim();
		nummer =schreibeNummer.getText().trim().replace("\\", "\\\\") ;
		for(i=0;i<lang;i++){
			
			if((Boolean)dtblm.getValueAt(i,0)){

				setFortschrittSetzen(durchlauf++);
				vecWahl.add(dtblm.getDataVector().get(i));
				((Vector)vecWahl.get(vecWahl.size()-1)).set(11, (String) dtblm.getValueAt(i,11));
				((Vector)vecWahl.get(vecWahl.size()-1)).set(6, (String) dtblm.getValueAt(i,6));
				tkstart = (int) ZeitFunk.MinutenSeitMitternacht(((String)jxSucheTable.getValueAt(i, 3)).trim()+":00");
				tkdauer = Integer.parseInt((String)jxSucheTable.getValueAt(i, 5));
				plstart = (int) ZeitFunk.MinutenSeitMitternacht(((String)jxSucheTable.getValueAt(i, 6)).trim()+":00");
				pldauer = Integer.parseInt((String)jxSucheTable.getValueAt(i, 7));
					for(int j = 0;j<1;j++){
						if((tkstart==plstart) && (tkdauer==pldauer)){
							// Beginn und Dauer sind gleich geblieben
							if((name.equals("")) &&	(nummer.equals(""))){
								// Termin wird gel�scht es m�ssen die Bl�cke vorher und nachher getestet werden;
								// deshalb zuerst Vector mit dem Tag holen
								try {
									vec = sucheZeile(i);
									schreibeLoeschen(vec,i,name,nummer);
								} catch (SQLException e) {
									e.printStackTrace();
								}
								break;
							}else{
								// nur Statement bilden und weg damit... kein Vector ged�nse
								String snum = Integer.toString(((Integer) jxSucheTable.getValueAt(i, 16)));
								String stmt = "Update flexkc set T"+snum+"='"+StringTools.EscapedDouble(name)+"', N"+snum+"='"+nummer+"' where id='"+
								((String)jxSucheTable.getValueAt(i, 17)).trim()+"'";
								try {
									schreibeZeile(stmt);
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								break;
							}
						}else{
							// Zun�chst den Vector mit dem Tag holen der immer �berpr�ft werden mu�				
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
								int tkende = (int) ZeitFunk.MinutenSeitMitternacht( ZeitFunk.MinutenZuZeit(tkstart+tkdauer) );
								int plende = (int) ZeitFunk.MinutenSeitMitternacht( ZeitFunk.MinutenZuZeit(plstart+pldauer) );

								//2. Abfrage ob Beginn sp�ter und Ende fr�her - Mittelblock erforderlich - 2 neue Bl�cke!
								if((tkstart != plstart) && (tkdauer != pldauer) && (tkende != plende)){
									schreibeTermin(vec,i,1,tkstart,tkdauer,tkende,plstart,pldauer,plende,name,nummer);
									break;
								}
								//3. Abfrage ob Beginn gleich und Ende fr�her - Nachblock erforderlich - 1 neuer Block!
								if((tkstart == plstart) && (tkdauer != pldauer)){
									schreibeTermin(vec,i,2,tkstart,tkdauer,tkende,plstart,pldauer,plende,name,nummer);
									break;
								}
								//4. Abfrage ob Beginn sp�ter und Ende gleich - Vorblock erforderlich - 1 neuer Block!
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
		setFortschrittZeigen(false);
		
	}
	/*****************************************/
	private void schreibeTermin(Vector vec,int zeile,int art,int tkstart,int tkdauer,int tkende,int plstart,int pldauer,int plende,String name,String nummer){
		int vecgross = vec.size();
		Vector newvec = new Vector();
		int block = (Integer) jxSucheTable.getValueAt(zeile,16);
		int geaendert = Integer.parseInt( (String) jxSucheTable.getValueAt(zeile,18) );
		if(vecgross != geaendert ){
			////System.out.println("vermerkt sind "+vecgross+" Bl�cke / tats�chlich in der Datenbank sind "+geaendert+" Bl�cke");
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
						((Vector)newvec.get(i)).set(3,Integer.toString(plstart-tkstart));
						((Vector)newvec.get(i)).set(4,ZeitFunk.MinutenZuZeit(tkstart+(plstart-tkstart)));
						Vector aktvec = new Vector();
						aktvec.add(name);
						aktvec.add(nummer);
						aktvec.add(ZeitFunk.MinutenZuZeit(plstart));
						aktvec.add(Integer.toString(pldauer));
						aktvec.add(ZeitFunk.MinutenZuZeit(plstart+pldauer));
						newvec.add(aktvec);
						Vector nachvec = new Vector();
						nachvec.add( ((Vector)vec.get(i)).get(0));
						nachvec.add( ((Vector)vec.get(i)).get(1));
						nachvec.add( aktvec.get(4));
						String planende = (String)jxSucheTable.getValueAt(zeile,4)+":00";
						
						nachvec.add( Integer.toString((int) ZeitFunk.MinutenSeitMitternacht(planende) - 
								(int)ZeitFunk.MinutenSeitMitternacht((String)nachvec.get(2)) ) ) ;					
						nachvec.add(planende );
						
						newvec.add(nachvec);
					}else if (i>(block-1)){
						newvec.add(vec.get(i));
					}
				}
				String stmt = macheStat(newvec,Integer.parseInt((String)jxSucheTable.getValueAt(zeile,17)),name,nummer);
				////System.out.println("Mit vor und nachblock "+stmt);
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
						aktvec.add(ZeitFunk.MinutenZuZeit(plstart));
						aktvec.add(Integer.toString(pldauer));
						aktvec.add(ZeitFunk.MinutenZuZeit(plstart+pldauer));
						newvec.add(aktvec);
						
						Vector nachvec = new Vector();
						nachvec.add( ((String)((Vector)vec.get(i)).get(0)) ) ;
						nachvec.add( ((String)((Vector)vec.get(i)).get(1)) ) ;
						nachvec.add( (String)aktvec.get(4) ) ;						
						nachvec.add( Integer.toString(tkdauer-pldauer)) ;
						nachvec.add( ZeitFunk.MinutenZuZeit(tkende) ) ;
						newvec.add(nachvec);

					}else{
						newvec.add(vec.get(i));
					}
					
				}
				String stmt = macheStat(newvec,Integer.parseInt((String)jxSucheTable.getValueAt(zeile,17)),name,nummer);
				////System.out.println("Nur mit Nachblock "+stmt);
				try {
					////System.out.println("EscapedDouble String = "+stmt);
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
						vorvec.add( ZeitFunk.MinutenZuZeit(tkstart)) ;
						vorvec.add( Integer.toString(tkdauer-pldauer) ) ;
						vorvec.add( ZeitFunk.MinutenZuZeit(plstart)) ;
						newvec.add(vorvec);
						
						Vector aktvec = new Vector();
						aktvec.add(name);
						aktvec.add(nummer);
						aktvec.add(ZeitFunk.MinutenZuZeit(plstart));
						aktvec.add(Integer.toString(pldauer));
						aktvec.add(ZeitFunk.MinutenZuZeit(plstart+pldauer));
						newvec.add(aktvec);

					}else{
						newvec.add(vec.get(i));
					}
					
				}
				String stmt = macheStat(newvec,Integer.parseInt((String)jxSucheTable.getValueAt(zeile,17)),name,nummer);
				try {
					schreibeZeile(stmt);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				break;
			}

		}
		
	}
	/*****************************************/	
	private void schreibeLoeschen(Vector vec,int zeile,String name,String nummer){
		int block = (Integer) jxSucheTable.getValueAt(zeile,16);
		int bloecke = vec.size(); 
		int geaendert = Integer.parseInt( (String) jxSucheTable.getValueAt(zeile,18) );
		if(bloecke != geaendert ){
			////System.out.println("vermerkt sind "+bloecke+" Bl�cke / tats�chlich in der Datenbank sind "+geaendert+" Bl�cke");
			block += (bloecke-geaendert);
		}
		String stmt = null;
		// nur den nachfolgenden Block pr�fen!!
		for(int i=0;i<1;i++){
			if(block == 1){
				

				// es gibt nur einen Block 
				if(bloecke==1){
					////System.out.println(jxSucheTable.getValueAt(zeile,8)+" ist im ersten Block / insgesamt existieren "+vec.size()+" Bl�cke");
					// es gibt mehrere bl�cke
					((Vector)vec.get(block-1)).set(0, "");
					((Vector)vec.get(block-1)).set(1, "");					
					stmt = macheStat(vec,Integer.parseInt((String)jxSucheTable.getValueAt(zeile,17)),name,nummer);
					////System.out.println("Es gibt nur einen Block, deshalb kann man dirket das statement basteln");
					////System.out.println(stmt);
					break;
				}else{
				//es gibt mehrere bloecke und der betroffene block = der erste, also mu� der nachfolgende untersucht werden.
					////System.out.println(jxSucheTable.getValueAt(zeile,8)+" ist im ersten Block / insgesamt existieren "+vec.size()+" Bl�cke");
					String xname,xnummer;
					xname = (String) ((Vector)vec.get(1)).get(0);
					xnummer = (String) ((Vector)vec.get(1)).get(1);
					if(xname.trim().equals("") && xnummer.trim().equals("")){
						String startzeit = (String) ((Vector)vec.get(0)).get(2);
						String endzeit = (String) ((Vector)vec.get(1)).get(4);
						int dauer1 = Integer.parseInt((String) ((Vector)vec.get(1)).get(3));
						int dauer2 = Integer.parseInt((String) ((Vector)vec.get(0)).get(3));
						int dauerNeu = dauer1+dauer2;
						((Vector)vec.get(1)).set(0, "");
						((Vector)vec.get(1)).set(1, "");					
						((Vector)vec.get(1)).set(2, startzeit);
						((Vector)vec.get(1)).set(3, Integer.toString(dauerNeu));
						((Vector)vec.get(1)).set(4,endzeit);
						vec.remove(0);
						stmt = macheStat(vec,Integer.parseInt((String)jxSucheTable.getValueAt(zeile,17)),name,nummer);
						////System.out.println("Der betroffene Block ist der erste und der nachfolgende Block = ebenfalls leer");
						////System.out.println(stmt);						
						break;
						
					}else{
						((Vector)vec.get(block-1)).set(0, "");
						((Vector)vec.get(block-1)).set(1, "");					
						stmt = macheStat(vec,Integer.parseInt((String)jxSucheTable.getValueAt(zeile,17)),name,nummer);
						////System.out.println("Der betroffene Block ist der erste und der nachfolgende Block = nicht(!!) leer");
						////System.out.println(stmt);
						break;
					}
				}
			}	
			// es gibt mehrere Bl�cke aber der betroffene ist der letzte nur den vorherigen Block pr�fen
			if(block == bloecke){
				////System.out.println(jxSucheTable.getValueAt(zeile,8)+" ist im ersten Block / insgesamt existieren "+vec.size()+" Bl�cke");
				String xname,xnummer;
				xname = (String) ((Vector)vec.get(block-2)).get(0);
				xnummer = (String) ((Vector)vec.get(block-2)).get(1);
				if(xname.trim().equals("") && xnummer.trim().equals("")){
					String startzeit = (String) ((Vector)vec.get(block-2)).get(2);
					String endzeit = (String) ((Vector)vec.get(block-1)).get(4);
					int dauer1 = Integer.parseInt((String) ((Vector)vec.get(block-2)).get(3));
					int dauer2 = Integer.parseInt((String) ((Vector)vec.get(block-1)).get(3));
					int dauerNeu = dauer1+dauer2;
					((Vector)vec.get(block-2)).set(0, "");
					((Vector)vec.get(block-2)).set(1, "");					
					((Vector)vec.get(block-2)).set(2, startzeit);
					((Vector)vec.get(block-2)).set(3, Integer.toString(dauerNeu));
					((Vector)vec.get(block-2)).set(4,endzeit);
					vec.remove(block-1);
					stmt = macheStat(vec,Integer.parseInt((String)jxSucheTable.getValueAt(zeile,17)),name,nummer);
					////System.out.println("Der betroffene Block ist der letzte und der vorhergehende ist ebenfalls leer");
					////System.out.println(stmt);
					break;				
				}else{
					((Vector)vec.get(block-1)).set(0, "");
					((Vector)vec.get(block-1)).set(1, "");					
					stmt = macheStat(vec,Integer.parseInt((String)jxSucheTable.getValueAt(zeile,17)),name,nummer);
					////System.out.println("Der betroffene Block ist der letzte und der nachfolgende Block = nicht(!!) leer");
					////System.out.println(stmt);
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
					String startzeitvb = (String) ((Vector)vec.get(block-2)).get(2);
					String endzeitnb = (String) ((Vector)vec.get(block)).get(4);
					int dauervb = Integer.parseInt((String) ((Vector)vec.get(block-2)).get(3));
					int dauerakt = Integer.parseInt((String) ((Vector)vec.get(block-1)).get(3));
					int dauernb = Integer.parseInt((String) ((Vector)vec.get(block)).get(3));
					int dauerNeu = dauervb+dauerakt+dauernb;
					((Vector)vec.get(block-2)).set(0, "");
					((Vector)vec.get(block-2)).set(1, "");					
					((Vector)vec.get(block-2)).set(2, startzeitvb);
					((Vector)vec.get(block-2)).set(3, Integer.toString(dauerNeu));
					((Vector)vec.get(block-2)).set(4,endzeitnb);
					vec.remove(block);
					vec.remove(block-1);					
					stmt = macheStat(vec,Integer.parseInt((String)jxSucheTable.getValueAt(zeile,17)),name,nummer);
					////System.out.println("Der betroffene Block ist zwischendrinn der vorhergehende und der nachfolgende sind ebenfalls leer");
					////System.out.println(stmt);
					break;
				}
				if(( xname.trim().equals("")) && (xnummer.trim().equals("")) ){
					//der Vorblock ist leer der Nachblock nicht !!
					String startzeit = (String) ((Vector)vec.get(block-2)).get(2);
					String endzeit = (String) ((Vector)vec.get(block-1)).get(4);
					int dauer1 = Integer.parseInt((String) ((Vector)vec.get(block-2)).get(3));
					int dauer2 = Integer.parseInt((String) ((Vector)vec.get(block-1)).get(3));
					int dauerNeu = dauer1+dauer2;
					((Vector)vec.get(block-2)).set(0, "");
					((Vector)vec.get(block-2)).set(1, "");					
					((Vector)vec.get(block-2)).set(2, startzeit);
					((Vector)vec.get(block-2)).set(3, Integer.toString(dauerNeu));
					((Vector)vec.get(block-2)).set(4,endzeit);
					vec.remove(block-1);
					stmt = macheStat(vec,Integer.parseInt((String)jxSucheTable.getValueAt(zeile,17)),name,nummer);
					////System.out.println("Der betroffene Block zwischendrinn und der vorhergende ist ebenfalls leer");
					////System.out.println(stmt);
					break;				
				}
				if(( yname.trim().equals("")) && (ynummer.trim().equals("")) ){
					//der Vorblock ist nicht leer aber der Nachblock!!
					String startzeit = (String) ((Vector)vec.get(block-1)).get(2);
					String endzeit = (String) ((Vector)vec.get(block)).get(4);
					int dauer1 = Integer.parseInt((String) ((Vector)vec.get(block-1)).get(3));
					int dauer2 = Integer.parseInt((String) ((Vector)vec.get(block)).get(3));
					int dauerNeu = dauer1+dauer2;
					((Vector)vec.get(block-1)).set(0, "");
					((Vector)vec.get(block-1)).set(1, "");					
					((Vector)vec.get(block-1)).set(2, startzeit);
					((Vector)vec.get(block-1)).set(3, Integer.toString(dauerNeu));
					((Vector)vec.get(block-1)).set(4,endzeit);
					vec.remove(block);
					stmt = macheStat(vec,Integer.parseInt((String)jxSucheTable.getValueAt(zeile,17)),name,nummer);
					////System.out.println("Der betroffene Block zwischendrinn und der nachfolgende ist ebenfalls leer");
					////System.out.println(stmt);
					break;				
				}else{
					((Vector)vec.get(block-1)).set(0, "");
					((Vector)vec.get(block-1)).set(1, "");					
					stmt = macheStat(vec,Integer.parseInt((String)jxSucheTable.getValueAt(zeile,17)),name,nummer);
					////System.out.println("Der betroffene Block zwischendrinn und weder der vorherige noch der nachfolgende sind leer");
					////System.out.println(stmt);
					break;
				}

				
			}
			////System.out.println(jxSucheTable.getValueAt(zeile,8)+" ist im Block Nr. "+block+" / insgesamt existieren "+vec.size()+" Bl�cke");
			
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
		String stmt = "";//new String();
		int gross = vec.size(),i;
		String rnummer = null;
		String snummer = null;
		stmt = "Update flexkc set ";
		////System.out.println(name+"  -  "+nummer);
		for(i=0;i<gross;i++){
			////System.out.println(vec.get(i));
			stmt = stmt +"T"+(i+1)+" = '"+StringTools.Escaped(((String)((Vector)vec.get(i)).get(0)))+"'"+", ";
			rnummer = ((String)((Vector)vec.get(i)).get(1));
			snummer = rnummer.trim().replace("\\", "\\\\") ;
			rnummer = snummer.replace("\\\\\\\\", "\\\\");
			stmt = stmt +"N"+(i+1)+" = '"+rnummer+"'"+", ";
			//stmt = stmt +"N"+(i+1)+" = '"+((String)((Vector)vec.get(i)).get(1))+"'"+", ";
			stmt = stmt +"TS"+(i+1)+" = '"+((String)((Vector)vec.get(i)).get(2))+"'"+", ";
			stmt = stmt +"TD"+(i+1)+" = '"+((String)((Vector)vec.get(i)).get(3))+"'"+", ";
			stmt = stmt +"TE"+(i+1)+" = '"+((String)((Vector)vec.get(i)).get(4))+"'"+", ";			
		}
		stmt = stmt+ "BELEGT ='"+Integer.toString(vec.size())+"' where id='"+Integer.toString(id)+"'";
		////System.out.println(stmt);
		
		return stmt;
	}
	/*****************************************/
	private void schreibeZeile(String sstmt) throws SQLException{
		Statement stmt = null;
		ResultSet rs = null;
		boolean res;
		//Wenn die Zeilen tats�chlich geschrieben werden sollen ab hier entfernen
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
			Vector vx = new Vector();
			while(rs.next()){
				int bloecke = Integer.parseInt(rs.getString(301));
				////System.out.println("Bl�cke = "+bloecke);
				int ii;
				
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
			vx = null;
		}catch(Exception ex){
			
		}finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) { // ignore }
					rs = null;
				}
			}	
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) { // ignore }
					stmt = null;
				}
			}

		}
		
		return (Vector)vec;//.clone();
	}
	/********Ende SwingWorker*/
	

/**************Ende der Hauptklasse*******************************/

	
	public void propertyChange(PropertyChangeEvent arg0) {
	}

@Override
public void keyPressed(KeyEvent arg0) {
	////System.out.println("********Button in KeyPressed*********");	
	////System.out.println(((JComponent)arg0.getSource()).getName());	
	if(arg0.getKeyCode()== 10 || arg0.getKeyCode()==0){
		arg0.consume();
	}
}

@Override
public void keyReleased(KeyEvent arg0) {
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
	if(arg0.getKeyCode() == 10 || arg0.getKeyCode() == 0){
		arg0.consume();
	}else{
		return;
	}	
}	

/***********************Bereits jetzt f�r*************************/
/***********************ein Update vorgesehen*********************/
/*****************************************************************/
@SuppressWarnings("unchecked")
class WorkerSuchenInKalenderTagen extends SwingWorker<Void,Void>{
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
	public ArrayList<String> sperrDatum = new ArrayList<String>();
	//private ArrayList<String> sperrDatum = new ArrayList<String>();
	ImageIcon img,img2;
	int schichtArt = -1;
	int selektivArt = -1;
	SuchenSeite eltern;
	Vector machevec = new Vector();
	

	public void setzeStatement(SuchenSeite xeltern){
		eltern = xeltern;
		
		img = SystemConfig.hmSysIcons.get("zuzahlnichtok");
		img2 = SystemConfig.hmSysIcons.get("zuzahlfrei");		
		//img = new ImageIcon(Reha.proghome+"icons/Kreuz_klein.gif");
		//img2 = new ImageIcon(Reha.proghome+"icons/frei.png");		
		img.setDescription("gesperrt");
		img2.setDescription("offen");
		setZeit();
		zeit = getZeit();
		this.sperrDatum.clear();
		if(SuchenSeite.selektiv){
			selektivArt = macheSelektiv();
		}
		
	}


	@Override
	protected Void doInBackground() throws Exception {
		// TODO Auto-generated method stub

	
		//Vector treadVect = new Vector();
		aktDatum = getAktDatum();
		
		try {
			stmt = (Statement) Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE );
			sqlEnde = DatFunk.sDatInSQL(getStopDatum());

			sqlAkt = DatFunk.sDatInSQL(aktDatum);
			sqlAlt = sqlAkt;

			suchkrit1 = getSuchName().trim();
			suchkrit2 = getSuchNummer().trim();
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
			tabelleAusschalten();
			setFortschrittRang(0,DatFunk.TageDifferenz( eltern.getStartDatum(), eltern.getStopDatum() ) );
			setFortschrittSetzen(0);
			setFortschrittZeigen(true);
			Vector abtlg = new Vector(Arrays.asList(abtei));
			Vector yvec = null;
			sperrDatum.clear();
			int aktuell;
			String sperre;
			String cmd;
			int ret;
			while( (DatFunk.DatumsWert(sqlAkt) <= DatFunk.DatumsWert(sqlEnde)) && (!eltern.mussUnterbrechen)){
				setzeDatum(aktDatum );
				if( tagDurchsuchen(aktDatum) ){

					
					String test = "";
					if(getGewaehlt() <= 20){
						test = macheStatement(sqlAkt);	
						////System.out.println(test);
					}else{
						test = "select * from flexkc where datum = '"+sqlAkt+"' LIMIT "+ParameterLaden.maxKalZeile;						
					}
					rs = (ResultSet) stmt.executeQuery(test);
					

					while(rs.next()){
						
						
						/*in Spalte 301 steht die Anzahl der belegten Bl�cke*/ 
						int belegt = rs.getInt(301);

						String name = "";
						String nummer = "";
						String skollege = "";
						//int	dauer = 0;
						int ikollege = 0;
						int defdauer = 0;


						skollege = rs.getString(303).substring(0,2);
						if( skollege.substring(0,1) == "0" ){
							ikollege = Integer.parseInt(skollege.substring(1,2));
						}else{
							ikollege = Integer.parseInt(skollege);								
						}

						/***************Hier wird getestet ob die Kalender zeile belegt ist*****/
						/********wenn ein Benutzer gel�scht wurde kommt leer zur�ck*******/
						szeil = ParameterLaden.getKollegenUeberDBZeile(ikollege);
						////System.out.println("Kollege �ber DBZeile = "+szeil);
						if(!szeil.equals("")){
						
						String sabteilung = eltern.kollegenAbteilung[ikollege].trim();//getKollegenAbteilung(ikollege).trim();
						if(! sabteilung.equals("") ){
							if( (abtlg.contains(sabteilung)) ){
								defdauer = (int) eltern.hZeiten.get(eltern.kollegenAbteilung[ikollege]);
										//getKollegenAbteilung(ikollege));
								gruppe = false;
							}else{
								////System.out.println("Kollege "+szeil+" hat keine Zeitzuordnung, nehme Gruppendefiniton");
								
								
								if( (abteilnr = SystemConfig.oGruppen.gruppenNamen.indexOf(sabteilung)) >= 0){
									defdauer = Long.valueOf(SystemConfig.oGruppen.gruppenGueltig.get(abteilnr)[2]).intValue();
									//defdauer = (int) new Long(SystemConfig.oGruppen.gruppenGueltig.get(abteilnr)[2]).intValue();
									////System.out.println("G�ltig ab:"+ datFunk.WertInDatum(SystemConfig.oGruppen.gruppenGueltig.get(abteilnr)[0]));
									//ermitteln ob alte oder neue Definition verwendet werden soll // in Variable ablegen
									//dann Sprung in die Tests
								}else{
									abteilnr = -1;
									defdauer = 15;
								}
								gruppe = true;
							
							}					
						}else{
							defdauer = 15;
							gruppe = false;
						}
							
						int ii;
						for(ii = 1;ii <= belegt;ii++){
							
							name =  ( rs.getString("T"+(ii))== null ? "" : rs.getString("T"+(ii)).trim() ); 
							nummer =  ( rs.getString("N"+(ii))== null ? "" : rs.getString("N"+(ii)).trim() );							

							
							if(! (eltern.getKollegenSuchen(ikollege)) ){
								////System.out.println("Keine Suche erforderlich f�r Kollege "+ikollege);
								// falls hier nicht gesucht werden soll = anzahl > 20
								break;
							}
							
							
							int j;
							yvec = null;
							int kaldauer = Integer.parseInt(rs.getString("TD"+(ii)));

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
										////System.out.println("Es wurde ein vecor erstllt");
										break;
									}
									if( (keintest)  && (gruppe)){
										if(name.equals("") && nummer.equals("")){
											Vector vecgruppe;
											if(abteilnr < 0){
												break;
											}
											if( (vecgruppe = gruppenTest(rs,abteilnr,ii,defdauer,true)) != null){
												////System.out.println("In Gruppensuchen nach Freien");
													yvec = sucheNachGruppenFreien(rs,name,nummer,skollege,ikollege,ii,defdauer,abteilnr,vecgruppe,true);
											}
										}
										break;
									}									
									if(schicht){
										if(freiTermin(name,nummer)){
											if(schichtTest(rs,ii,defdauer)){
												yvec = sucheNachSelect(rs,name,nummer,skollege,ikollege,ii,defdauer);
												break;
											}
										}
										break;
									}
									if(selektiv){
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
											////System.out.println("In Gruppensuchen nach Freien");
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
											////System.out.println("In Gruppensuchen nach Freien");
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
										if(abteilnr < 0){
											break;
										}
										if( (vecgruppe = gruppenTest(rs,abteilnr,ii,defdauer,false)) != null){
											////System.out.println("In Gruppensuchen nach Freien");
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
								yvec.set(1,img2);
								getInstance().dtblm.addRow(yvec);
								/**************************/
								aktuell = dtblm.getRowCount();
								SuchenSeite.verarbeitetLbl.setText(Integer.toString(aktuell));
								sperre = (String)dtblm.getValueAt(aktuell-1,13)+
								dtblm.getValueAt(aktuell-1,14) ;  
								if(!sperrDatum.contains(sperre+SystemConfig.dieseMaschine+zeit)){
									cmd = "sperre='"+sperre+"'";
									ret = SqlInfo.zaehleSaetze("flexlock", cmd);
									if(ret==0){
										cmd = "insert into flexlock set sperre='"+sperre+"', maschine='"+SystemConfig.dieseMaschine+"', "+
										"zeit='"+zeit+"'";
										SqlInfo.sqlAusfuehren(cmd);
										sperrDatum.add(sperre+SystemConfig.dieseMaschine+zeit);
									}else{
										dtblm.setValueAt(img,aktuell-1,1);
									}
								}
								//sucheDaten.add(yvec);
								trefferSetzen();
							}
							
						} // ende der for f�r die einzelnen Felder
						/*****************/
						} //neu endif von: keine Zeile im Kalender
					} // Klammer der While 
					
				aktDatum = DatFunk.sDatPlusTage(aktDatum, 1);
				setFortschrittSetzen(++ftage);
				sqlAlt = new String(sqlAkt);
				sqlAkt = DatFunk.sDatInSQL(aktDatum );
				}else{
					aktDatum = DatFunk.sDatPlusTage(aktDatum, 1);
					sqlAlt = sqlAkt;					
					sqlAkt = DatFunk.sDatInSQL(aktDatum );					////System.out.println(SuchenSeite.getAktDatum()+"-"+SuchenSeite.tagDurchsuchen(SuchenSeite.getAktDatum()) );
					setFortschrittSetzen(++ftage);
				}
				/*********Ende der oberen While*********/
			}		
		}catch(SQLException ex) {
			//System.out.println("von stmt -SQLState: " + ex.getSQLState());
		}catch(Exception ex2){
			ex2.printStackTrace();
		}




		finally {
			if (rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (SQLException sqlEx) { // ignore }
					rs = null;
				}
			}	
			if (stmt != null) {
				try {
					stmt.close();
					stmt = null;
				} catch (SQLException sqlEx) { // ignore }
					stmt = null;
				}
			}

		}
		if (DatFunk.DatumsWert(sqlAkt) > DatFunk.DatumsWert(sqlEnde)){
			setSucheBeendet();
			setFortschrittZeigen(false);
		}
		long dauer = (System.currentTimeMillis()-zeit1);
		SwingUtilities.invokeLater(new Runnable(){
			public  void run(){
				setKnopfGedoense(new int[]  {0,0,0,0,0,0,0,1,1,1});
				tabelleEinschalten();
				listenerEinschalten();
				////System.out.println("Vectorgröße = "+getInstance().dtblm.getRowCount());
				mussUnterbrechen = true;
				jxSucheTable.revalidate();
			}
		});
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
		return (Vector)cols;//(Vector)cols.clone();
	}
	
/********************************************/
	private Vector gruppenTest(ResultSet rs, int gruppe,int feld,int defdauer,boolean suchleer) throws SQLException{
		Vector vecret = null;
		String sDatum = DatFunk.sDatInDeutsch(rs.getString("DATUM"));

		int taginwoche = DatFunk.TagDerWoche(sDatum);
		int altneu = 1;
		
		if( DatFunk.DatumsWert(sDatum) >=
			SystemConfig.oGruppen.gruppenGueltig.get(gruppe)[0] ){
			altneu = 0;
		}
		int xdauer = Integer.parseInt(rs.getString("TD"+(feld)).trim());		
		String xzeit = rs.getString("TS"+(feld)).trim().substring(0,5);
		
		int gross = (int)((Vector)((Vector)((Vector)SystemConfig.oGruppen.gruppeAlle.get(gruppe)).get(altneu)).get(taginwoche-1)).size();
		////System.out.println("Termine = "+gross);
		for(int i = 0;i<gross;i++){
			long lgrenzeklein =(Long) ((Vector)((Vector)((Vector)((Vector)SystemConfig.oGruppen.gruppeAlle.get(gruppe)).get(altneu)).get(taginwoche-1)).get(i)).get(0);
			long lgrenzegross =(Long) ((Vector)((Vector)((Vector)((Vector)SystemConfig.oGruppen.gruppeAlle.get(gruppe)).get(altneu)).get(taginwoche-1)).get(i)).get(1); 	

			if(longPasstZwischen(lgrenzeklein,lgrenzegross,xzeit,(suchleer ? defdauer : xdauer))){
				//vecret = (Vector) ((Vector)((Vector)((Vector)((Vector)SystemConfig.oGruppen.gruppeAlle.get(gruppe)).get(altneu)).get(taginwoche-1)).get(i)).clone();
				vecret = (Vector) ((Vector)((Vector)((Vector)((Vector)SystemConfig.oGruppen.gruppeAlle.get(gruppe)).get(altneu)).get(taginwoche-1)).get(i));
				return (Vector)vecret;//(Vector)vecret.clone();
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
		wogerade = DatFunk.GeradeWoche(DatFunk.sDatInDeutsch(rs.getString("DATUM")));
		kalanfang = SystemConfig.KalenderUmfang[0];
		kalende = SystemConfig.KalenderUmfang[1];
		
		// Bearbeitet nur gerade Kalenderwochen
		uhr1 = schichtUhr[0];
		uhr2 = schichtUhr[1];
		if((wogerade) && (schichtWal[0])){
			if(schichtVor[0]){
				selektparm1 = (Schnittmenge(kalanfang,uhr1,pbeginn,pende));
				if(selektparm1[0] >=defdauer){
					String drzeit = (selektparm1[3] > 0 ? 
					 ZeitFunk.MinutenZuZeit(selektparm1[1]).substring(0,5) :
					 ZeitFunk.MinutenZuZeit(selektparm1[2]-defdauer).substring(0,5)); 
					selektbeginn = drzeit;
					return true;
				}else{
					return false;
				}
			}else{
				selektparm1 = (Schnittmenge(uhr1,kalende,pbeginn,pende));
				if(selektparm1[0] >=defdauer){
					String drzeit = (selektparm1[3] > 0 ? 
					 ZeitFunk.MinutenZuZeit(selektparm1[1]).substring(0,5) :
					 ZeitFunk.MinutenZuZeit(selektparm1[2]-defdauer).substring(0,5)); 
					selektbeginn = drzeit;
					return true;
				}else{
					return false;
				}
			}		
		}
		if((!wogerade) && (schichtWal[1])){
			if(schichtVor[1]){
				selektparm1 = (Schnittmenge(kalanfang,uhr2,pbeginn,pende));
				if(selektparm1[0] >=defdauer){
					String drzeit = (selektparm1[3] > 0 ? 
					 ZeitFunk.MinutenZuZeit(selektparm1[1]).substring(0,5) :
					 ZeitFunk.MinutenZuZeit(selektparm1[2]-defdauer).substring(0,5)); 
					selektbeginn = drzeit;
					return true;
				}else{
					return false;
				}
			}else{
				selektparm1 = (Schnittmenge(uhr2,kalende,pbeginn,pende));
				if(selektparm1[0] >=defdauer){
					String drzeit = (selektparm1[3] > 0 ? 
					 ZeitFunk.MinutenZuZeit(selektparm1[1]).substring(0,5) :
					 ZeitFunk.MinutenZuZeit(selektparm1[2]-defdauer).substring(0,5)); 
					selektbeginn = drzeit;
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
		wbeginn1 = selectUhr[0];
		wende1 = selectUhr[1];
		wbeginn2 = selectUhr[2];
		wende2 = selectUhr[3];
 		
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
					 ZeitFunk.MinutenZuZeit(selektparm1[1]).substring(0,5) :
					 ZeitFunk.MinutenZuZeit(selektparm1[2]-defdauer).substring(0,5)); 
					selektbeginn = drzeit;
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
					 ZeitFunk.MinutenZuZeit(selektparm1[1]).substring(0,5) :
					 ZeitFunk.MinutenZuZeit(selektparm1[2]-defdauer).substring(0,5));
					selektbeginn = drzeit;
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
					 ZeitFunk.MinutenZuZeit(selektparm1[1]).substring(0,5) :
					 ZeitFunk.MinutenZuZeit(selektparm1[2]-defdauer).substring(0,5));
					selektbeginn = drzeit;
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
					//if(selektparm1[3] == -1){//System.out.println("Anfang = -1 - "+pbeginn);}
					String drzeit = (selektparm1[3] > 0 ? 
					 ZeitFunk.MinutenZuZeit(selektparm1[1]).substring(0,5) :
					 ZeitFunk.MinutenZuZeit(selektparm1[2]-defdauer).substring(0,5));
					selektbeginn = drzeit;
					}else{
					String drzeit = (selektparm2[3] > 0 ? 
					 ZeitFunk.MinutenZuZeit(selektparm2[1]).substring(0,5) :
					 ZeitFunk.MinutenZuZeit(selektparm2[2]-defdauer).substring(0,5));
					selektbeginn = drzeit;
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
					 ZeitFunk.MinutenZuZeit(selektparm1[1]).substring(0,5) :
					 ZeitFunk.MinutenZuZeit(selektparm1[2]-defdauer).substring(0,5));
					selektbeginn = drzeit;
					}else{
					String drzeit = (selektparm2[3] > 0 ? 
					 ZeitFunk.MinutenZuZeit(selektparm2[1]).substring(0,5) :
					 ZeitFunk.MinutenZuZeit(selektparm2[2]-defdauer).substring(0,5));
					selektbeginn = drzeit;
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
					 ZeitFunk.MinutenZuZeit(selektparm1[1]).substring(0,5) :
					 ZeitFunk.MinutenZuZeit(selektparm1[2]-defdauer).substring(0,5));
					selektbeginn = drzeit;
					}else{
					String drzeit = (selektparm2[3] > 0 ? 
					 ZeitFunk.MinutenZuZeit(selektparm2[1]).substring(0,5) :
					 ZeitFunk.MinutenZuZeit(selektparm2[2]-defdauer).substring(0,5));
					selektbeginn = drzeit;
					}	
					return true;
				}else{
					return false;
				}
			}
		}

		return false;
	}
	boolean passtZwischen(String sgrenzeklein,String sgrenzegross,String szeit,int dauer){
		long z1 = ZeitFunk.MinutenSeitMitternacht(sgrenzeklein);
		long z2 = ZeitFunk.MinutenSeitMitternacht(sgrenzegross);
		long z3 = ZeitFunk.MinutenSeitMitternacht(szeit)+Long.parseLong(Integer.toString(dauer));
		return( ((z3 >= z1) &&  (z3<=z2)) ? true : false);
	}
	boolean longPasstZwischen(long lgrenzeklein,long lgrenzegross,String szeit,int dauer){
		long z1 = lgrenzeklein;
		long z2 = lgrenzegross;
		
		long z3 = ZeitFunk.MinutenSeitMitternacht(szeit)+Long.parseLong(Integer.toString(dauer));
		return( ((z3 >= z1) &&  (z3<=z2)) ? true : false);
	}
	boolean ZeitGroesserGleich(String szeit1,String szeit2){
		long z1 = ZeitFunk.MinutenSeitMitternacht(szeit1);
		long z2 = ZeitFunk.MinutenSeitMitternacht(szeit2);
		return( z2 >= z1 ? true : false);
	}
	boolean ZeitGroesser(String szeit1,String szeit2){
		long z1 = ZeitFunk.MinutenSeitMitternacht(szeit1);
		long z2 = ZeitFunk.MinutenSeitMitternacht(szeit2);
		return( z2 > z1 ? true : false);
	}
	boolean ZeitKleinerGleich(String szeit1,String szeit2){
		long z1 = ZeitFunk.MinutenSeitMitternacht(szeit1);
		long z2 = ZeitFunk.MinutenSeitMitternacht(szeit2);
		return( z2 <= z1 ? true : false);
	}
	boolean ZeitKleiner(String szeit1,String szeit2){
		long z1 = ZeitFunk.MinutenSeitMitternacht(szeit1);
		long z2 = ZeitFunk.MinutenSeitMitternacht(szeit2);
		return( z2 < z1 ? true : false);
	}
	int[] Schnittmenge(String sklein1,String sgross1,String sklein2,String sgross2 ){
		long z1 = ZeitFunk.MinutenSeitMitternacht(sklein1);
		long z2 = ZeitFunk.MinutenSeitMitternacht(sgross1);
		long z3 = ZeitFunk.MinutenSeitMitternacht(sklein2);
		long z4 = ZeitFunk.MinutenSeitMitternacht(sgross2);
		long schnittbeginn, schnittende;
		int ananfang = 1;
		// Wenn Wunschbeginn fr�her oder gleich als gefundener Termin-Beginn  
		if ( (z1 <= z3) && (z2 >= z4) ){
			schnittbeginn = z3;
			schnittende = z4;
			ananfang = 1;
			return new int[] {Long.valueOf(schnittende-schnittbeginn).intValue(),Long.valueOf(schnittbeginn).intValue(),Long.valueOf(schnittende).intValue(),ananfang};
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
		
		return new int[] {Long.valueOf(schnittende-schnittbeginn).intValue(),Long.valueOf(schnittbeginn).intValue(),Long.valueOf(schnittende).intValue(),ananfang};
	}
	
	
	private int macheSelektiv(){
		boolean c0 = selectWal[0];
		boolean c1 = selectWal[1];
		boolean c2 = selectWal[2];
		boolean c3 = selectWal[3];
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
		return ((!schicht) &&(!selektiv) ? true : false);
	}	
	public void trefferSetzen(){
		SwingUtilities.invokeLater(new Runnable(){
			public  void run(){
				setzeTreffer(treffer);						       	  	
			}
		});
	}
	private String macheStatement(String sqldatum){
		String ret = "";
		int i,lang;
		int isuchen = 0;
		String ssuchen = "";
		String stmt = "";
		lang = getKollegenEinstellen().length;
	
			for (i=0;i<lang;i++){
				
				if(((Boolean)getKollegenEinstellen()[i][1]) ){
					////System.out.println("Inhalt von [i]="+i+" / Inhalt von [5=]"+SuchenSeite.getKollegenEinstellen()[i][5] );
					int dbZeile = (Integer) getKollegenEinstellen()[i][0];
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
//		//System.out.println("Gültig ab:"+ datFunk.WertInDatum(SystemConfig.oGruppen.gruppenGueltig.get(gruppennr)[0]));
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
		vec.set(6, selektbeginn);
		return vec;
	}
	private Vector sucheNachKGG(ResultSet rs,String name,String nummer,String skollege,int ikollege,int ii,int defdauer) throws SQLException{
		Vector vec = null;
		vec = macheKGGVector(rs,name,nummer,skollege,ikollege,ii,defdauer);
		return vec;
	}
	
	private Vector macheVector(ResultSet rs,String name,String nummer,String skollege,int ikollege,int ii,int defdauer) throws SQLException{
		//Vector vec = new Vector();
		machevec.clear();
		machevec.trimToSize();
		String uhrzeit;
		String sorigdatum;
		String sdatum;

		uhrzeit = rs.getString("TS"+(ii));
		
		sorigdatum = rs.getString(305); 
		sdatum = DatFunk.sDatInDeutsch(sorigdatum);
		skollege = (String) ParameterLaden.getKollegenUeberReihe(ikollege);
		//{"x?","G!","Datum","Beginn","Ende","Min.","Namen","Rez.Nr.","Behandler","Druckzeit","Sort","Spalte","richtigesDatum","block","id-db"};
		machevec.add(Boolean.valueOf(false));
		machevec.add(null);
		machevec.add(DatFunk.WochenTag(sdatum).substring(0,2)+"-"+sdatum);
		machevec.add(uhrzeit.substring(0,5));
		machevec.add(rs.getString("TE"+(ii)).trim().substring(0,5));
		machevec.add(rs.getString("TD"+(ii)).trim());		
		machevec.add(rs.getString("TS"+(ii)).trim().substring(0,5));
		machevec.add((defdauer == -1 ? rs.getString("TD"+(ii)).trim() : Integer.toString(defdauer) ) );
		machevec.add(name);
		machevec.add(nummer);								
		machevec.add(skollege);								
		machevec.add("");								
		machevec.add(""); //fr�her sorter
		machevec.add(rs.getString("BEHANDLER"));		
		machevec.add(sdatum);
		machevec.add(sorigdatum);
		machevec.add(ii);
		machevec.add(rs.getString("id"));
		machevec.add(rs.getString("BELEGT"));
		machevec.add(Boolean.valueOf(false));

		return (Vector)machevec.clone();
	}

	private Vector macheKGGVector(ResultSet rs,String name,String nummer,String skollege,int ikollege,int ii,int defdauer) throws SQLException{
		machevec.clear();
		machevec.trimToSize();
		//Vector vec = new Vector();
		String uhrzeit;
		String sorigdatum;
		String sdatum;

		uhrzeit = rs.getString("TS"+(ii));
		
		sorigdatum = rs.getString(305); 
		sdatum = DatFunk.sDatInDeutsch(sorigdatum);
		skollege = (String) ParameterLaden.getKollegenUeberReihe(ikollege);
		//{"x?","G!","Datum","Beginn","Ende","Min.","Namen","Rez.Nr.","Behandler","Druckzeit","Sort","Spalte","richtigesDatum","block","id-db"};
		machevec.add(Boolean.valueOf(false));
		machevec.add(null);
		machevec.add(DatFunk.WochenTag(sdatum).substring(0,2)+"-"+sdatum);
		machevec.add(uhrzeit.substring(0,5));
		machevec.add(rs.getString("TE"+(ii)).trim().substring(0,5));
		machevec.add(rs.getString("TD"+(ii)).trim());		
		machevec.add(rs.getString("TS"+(ii)).trim().substring(0,5));
		machevec.add((defdauer == -1 ? rs.getString("TD"+(ii)).trim() : Integer.toString(defdauer) ) );
		machevec.add(name);
		machevec.add(nummer);								
		machevec.add(skollege);								
		machevec.add(rs.getString("TS"+(ii)).trim().substring(0,2)+":00");								
		machevec.add(skollege); //früher sorter
		machevec.add(rs.getString("BEHANDLER"));		
		machevec.add(sdatum);
		machevec.add(sorigdatum);
		machevec.add(ii);
		machevec.add(rs.getString("id"));
		machevec.add(rs.getString("BELEGT"));
		machevec.add(Boolean.valueOf(false));

		return (Vector)machevec.clone();
	}

	private Vector macheGruppenVector(ResultSet rs,String name,String nummer,String skollege,int ikollege,int ii,int defdauer,Vector vecgruppe,boolean suchleer) throws SQLException{
		
		machevec.clear();
		machevec.trimToSize();
		//Vector vec = new Vector();
		String uhrzeit;
		String sorigdatum;
		String sdatum;

		uhrzeit = rs.getString("TS"+(ii));
		
		sorigdatum = rs.getString(305); 
		sdatum = DatFunk.sDatInDeutsch(sorigdatum);
		skollege = (String) ParameterLaden.getKollegenUeberReihe(ikollege);
		//{"x?","G!","Datum","Beginn","Ende","Min.","Namen","Rez.Nr.","Behandler","Druckzeit","Sort","Spalte","richtigesDatum","block","id-db"};
		machevec.add(Boolean.valueOf(false));
		machevec.add(null);
		machevec.add(DatFunk.WochenTag(sdatum).substring(0,2)+"-"+sdatum);
		machevec.add(uhrzeit.substring(0,5));
		machevec.add(rs.getString("TE"+(ii)).trim().substring(0,5));
		machevec.add(rs.getString("TD"+(ii)).trim());		
		machevec.add(rs.getString("TS"+(ii)).trim().substring(0,5));
		machevec.add(Integer.toString(defdauer));
		boolean xgruppe = false;
		//if(name.trim().equals("")){
			String snam = null;
			if(((String)vecgruppe.get(3)).toUpperCase().contains("@BEHANDLER")){
				/******** hier noch das handling f�r Teile des Namens anzeigen *******/
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
			machevec.add(snam);
		//}else{
			//vec.add(name);			
		//}
		machevec.add(nummer);								
		machevec.add(skollege);
		String drzeit = (String)vecgruppe.get(2);
		machevec.add((drzeit.trim().equals("00:00") ? "--:--" : vecgruppe.get(2)));								
		machevec.add((xgruppe ? skollege : ""));  //fr�her sorter
		machevec.add(rs.getString("BEHANDLER"));		
		machevec.add(sdatum);
		machevec.add(sorigdatum);
		machevec.add(ii);
		machevec.add(rs.getString("id"));
		machevec.add(rs.getString("BELEGT"));
		machevec.add(Boolean.valueOf(true));

		return (Vector)machevec.clone();
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
	sperre = (String)((Vector)vecx).get(13)+
						(String)((Vector)vecx).get(14) ; 

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
					/*
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					*/
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
		dtblm.setValueAt(bild, wo, 1);
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

/**************************************************/
/**************************************************/
class WorkerTabelle extends SwingWorker<Void,Void>{
	boolean fertig = false;
	int aktuell = -1;
	public ArrayList<String> sperrDatum = new ArrayList<String>();
	private ImageIcon img,img2;
	private String zeit;
	private int merken = -1;

	public void init(){
		img = SystemConfig.hmSysIcons.get("zuzahlnichtok");
		img2 = SystemConfig.hmSysIcons.get("zuzahlfrei");		
		img.setDescription("gesperrt");
		img2.setDescription("offen");
		setZeit();
		zeit = getZeit();
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
			anzahl = sucheDaten.size();
			if( (SuchenSeite.mussUnterbrechen) && (anzahl==0) && (getTreffer()==anzahl) ){
				break;
			}
			if( (SuchenSeite.mussUnterbrechen) && (anzahl==(aktuell+1)) && (aktuell==(getTreffer()-1)) ){
				break;

			}

			if(anzahl > 0){
				if(aktuell != (anzahl-1)){
					 	
						aktuell++;
						SuchenSeite.verarbeitetLbl.setText(Integer.toString(aktuell+1));
						nvec = (Vector) ((Vector)sucheDaten.get(aktuell));//.clone();
						sperre = (String)((Vector)nvec).get(13)+
											(String)((Vector)nvec).get(14) ; 

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
						dtblm.addRow(nvec);
						new Thread(){
							public void run(){
								jxSucheTable.validate();
							}
						}.start();
						if( (SuchenSeite.mussUnterbrechen) && (anzahl==(aktuell+1)) && (aktuell==(getTreffer()-1)) ){
							break;

						}
				}
			}
			if( (SuchenSeite.mussUnterbrechen) 
					&& (anzahl==(aktuell+1)) 
					&& (aktuell==(getTreffer()-1)) 
					&& (getTreffer()==anzahl) ){
				break;

			}
			
		}
		setWorkerFertig(true);
		nvec = null;
		sperre = null;
		return null;
	}
	/************************/
	private int XSperrenVerarbeiten(int akt,Vector vecx,String zeit){
		Statement stmtx = null;
		ResultSet rsx = null;
		boolean neu = true;
		
		String sperre;
		sperre = (String)((Vector)vecx).get(13)+
							(String)((Vector)vecx).get(14) ;
		
		if(neu){
			String cmd = "sperre='"+sperre+"'";
			if(SqlInfo.zaehleSaetze("flexlock", cmd)==0){
				cmd = "insert into flexlock set sperre='"+sperre+"', maschine='"+SystemConfig.dieseMaschine+"', "+
				"zeit='"+zeit+"'";
				//SqlInfo.sqlAusfuehren(cmd);
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
					rsx = stmtx.executeQuery("select sperre,maschine from flexlock where sperre='"+sperre+"' LIMIT 1");
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
					rsx = null;
				} catch (SQLException sqlEx) { // ignore }
					rsx = null;
				}
			}	
			if (stmtx != null) {
				try {
					stmtx.close();
					stmtx = null;
				} catch (SQLException sqlEx) { // ignore }
					stmtx = null;
				}
			}
					
			return 0;
		}
	
	
}
/**************************************************/
class WorkerTabelle2 extends SwingWorker<Void,Void>{
	boolean fertig = false;
	int aktuell = -1;
	public ArrayList<String> sperrDatum = new ArrayList<String>();
	private ImageIcon img,img2;
	private String zeit;
	private int merken = -1;

	public void init(){
		img = SystemConfig.hmSysIcons.get("zuzahlnichtok");
		img2 = SystemConfig.hmSysIcons.get("zuzahlfrei");		
		img.setDescription("gesperrt");
		img2.setDescription("offen");
		setZeit();
		zeit = getZeit();
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
			anzahl = dtblm.getRowCount();
			if( (SuchenSeite.mussUnterbrechen) && (anzahl==0) && (getTreffer()==anzahl) ){
				break;
			}
			if( (SuchenSeite.mussUnterbrechen) && (anzahl==(aktuell+1)) && (aktuell==(getTreffer()-1)) ){
				break;

			}

			if(anzahl > 0){
				if(aktuell != (anzahl-1)){
					 	
						aktuell++;
						SuchenSeite.verarbeitetLbl.setText(Integer.toString(aktuell+1));
						sperre = (String)dtblm.getValueAt(aktuell,13)+
						dtblm.getValueAt(aktuell,14) ; 
						if(sperrDatum.contains(sperre+SystemConfig.dieseMaschine+zeit)){
							//dtblm.setValueAt(img2,aktuell,1) ;
						}else{
							//int ret = 0;
							int ret = XSperrenVerarbeiten(aktuell,sperre,zeit);
							if(ret==0){
								sperrDatum.add(sperre+SystemConfig.dieseMaschine+zeit);
							}else{
								dtblm.setValueAt(img,aktuell,1);	
							}
							

						}
						if( (SuchenSeite.mussUnterbrechen) && (anzahl==(aktuell+1)) && (aktuell==(getTreffer()-1)) ){
							break;

						}
				}
			}
			if( (SuchenSeite.mussUnterbrechen) 
					&& (anzahl==(aktuell+1)) 
					&& (aktuell==(getTreffer()-1)) 
					&& (getTreffer()==anzahl) ){
				break;

			}
			
		}
		setWorkerFertig(true);
		nvec = null;
		sperre = null;
		img = null;
		img2 = null;
		return null;
	}
	/************************/
	private int XSperrenVerarbeiten(int akt,String xsperre,String zeit){
		Statement stmtx = null;
		ResultSet rsx = null;
		boolean neu = true;
		
		String sperre = xsperre;
		if(neu){
			String cmd = "sperre='"+sperre+"'";
			if(SqlInfo.zaehleSaetze("flexlock", cmd)==0){
				cmd = "insert into flexlock set sperre='"+sperre+"', maschine='"+SystemConfig.dieseMaschine+"', "+
				"zeit='"+zeit+"'";
				SqlInfo.sqlAusfuehren(cmd);
				//new ExUndHop().setzeStatement(cmd);
				return(0); 
			}else{
				return(1);				
			}
		}
					
		return 0;
	}

	
	
}
/**************************************************/


	

}
class TermObjekt implements Comparable<TermObjekt>{
	public String tag;
	public String beginn;
	public String termtext;
	public String sorter;
	
	public TermObjekt(String xtag,String xbeginn,String xtermtext,String xsorter){
		this.tag = xtag;
		this.beginn = xbeginn;
		this.termtext =xtermtext;
		this.sorter =  xsorter;
		
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
class EntsperreSatz extends Thread implements Runnable{
	private SuchenSeite eltern;
	public void setzeEltern(SuchenSeite xeltern){
		this.eltern = xeltern;
	}
	public void run(){
		Statement stmt = null;

		try {
			stmt = (Statement) Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			        ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			if(this.eltern == null){
				//System.out.println("this.eltern.getZeit() == NULL");
			}
			boolean rs = stmt.execute("delete from flexlock where maschine='"+SystemConfig.dieseMaschine+"' AND zeit='"+this.eltern.getZeit()+"'");
		} catch (SQLException e) {
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
