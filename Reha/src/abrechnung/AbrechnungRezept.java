package abrechnung;

import hauptFenster.AktiveFenster;
import hauptFenster.Reha;
import hauptFenster.UIFSplitPane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.CellEditorListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.NumberFormatter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import jxTableTools.CheckRenderer;
import jxTableTools.DblCellEditor;
import jxTableTools.DoubleTableCellRenderer;
import jxTableTools.MitteRenderer;
import jxTableTools.MyTableCheckBox;
import jxTableTools.MyTableComboBox;
import jxTableTools.MyTableStringDatePicker;

import org.jdesktop.swinghelper.layer.JXGlassPane;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXMonthView;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.JXTable.NumberEditor;
import org.jdesktop.swingx.calendar.DateSelectionModel.SelectionMode;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableNode;

import patientenFenster.AktuelleRezepte;
import patientenFenster.RezeptDaten;


import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.sun.star.drawing.Alignment;

import events.PatStammEvent;
import events.PatStammEventClass;

import sqlTools.SqlInfo;
import stammDatenTools.RezTools;
import systemEinstellungen.SystemConfig;
import systemTools.JCompTools;
import systemTools.JRtaCheckBox;
import systemTools.JRtaComboBox;
import systemTools.ListenerTools;
import systemTools.StringTools;
import terminKalender.DatFunk;
import terminKalender.ParameterLaden;



public class AbrechnungRezept extends JXPanel implements HyperlinkListener,ActionListener, MouseListener, PopupMenuListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8387184772704779192L;
	private Abrechnung1 eltern;
	JToolBar tb = null;
	
	DecimalFormat dfx = new DecimalFormat( "0.00" );
	SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
	
	String preisgruppe = "-1";
	
	JButton[] tbbuts = {null,null,null,null};
	JLabel[] labs = {null,null,null,null,null,null,null,null,null,null,
					null,null,null,null,null,null,null,null,null,null,
					null,null,null,null,null,null,null,null,null,null};

	JLabel aktRezNum = null;
	Vector<Vector<Object>> vec_tabelle = new Vector<Vector<Object>>();
	Vector<Object> vecdummy = new Vector<Object>();
	
	Vector<Vector<String>> vectage = null;
	Vector<Vector<String>>vec_rez = null;
	Vector<Vector<String>>vec_pat = null;
	Vector<Vector<String>>vec_term = null;
	Vector<Vector<String>>vec_hb = null;
	
	Vector<Vector<String>>vec_kuerzel = new Vector<Vector<String>>();
	Vector<String> kundid = new Vector<String>();

	Vector<String>vec_poskuerzel = new Vector<String>();
	Vector<String>vec_pospos = new Vector<String>();
	Vector<Integer>vec_posanzahl = new Vector<Integer>();
	
	
	JXTree treeRezept = null;
	
	public DefaultMutableTreeNode rootRezept;
	public DefaultMutableTreeNode rootRdaten;
	public DefaultMutableTreeNode rootPdaten;
	public DefaultMutableTreeNode rootAdaten;
	public DefaultMutableTreeNode rootTdaten;
	public DefaultMutableTreeNode rootGdaten;
	public DefaultMutableTreeNode rootStammdaten;
	public DefaultTreeModel treeModelRezept;
	final String plus = "+";
	final String EOL = "'\n";
	final String SOZ = "?";
	Vector<Vector<String>> preisvec = null;
	
	JRtaComboBox cmbkuerzel = null;
	JRtaComboBox cmbbreak = null;
	JComboBox cmbpreis = null;
	JCheckBox chkzuzahl = null;
	
	boolean patAktuellFrei = false;
	boolean patVorjahrFrei = false;
	boolean patU18 = false;
	String	patFreiAb;
	String	patFreiBis;
	boolean  gebuehrBezahlt;
	Double  gebuehrBetrag;
	boolean mitPauschale;
	
	private String zuZahlungsIndex = "";
	private String zuZahlungsPos = "";
	
	String disziplinIndex = "";
	String disziplinGruppe = "";
	int preisregelIndex = -1;
	
	int anzahlhb = 0;
	int anzahlposhb = 0;
	boolean hausbesuch = false;

	Double rezeptWert;
	Double zuzahlungWert;
	Double kmWert;
	JXDatePicker datePick = new JXDatePicker();
	JXMonthView sv;

	public boolean	rezeptSichtbar = false;
	
	public JXTable tageTbl = null;
	//public MyTageTableModel tageMod = new MyTageTableModel();
	MyTableComboBox mycomb;
	MyTableComboBox mycomb2;
	MyTableComboBox mycomb3;
	MyTableComboBox mycomb4;
	MyTableCheckBox mycheck;
	JRtaCheckBox check;
	
	private UIFSplitPane jSplitOU = null;
	private String[] voArt = {"Erstverordnung","Folgeverordnung","Folgeverordn. außerhalb d. Regelf."};
	private String[] voIndex = {"01","02","10"};  

	private String[] voBreak = {"","K","U","T","A"};
	private String[] voPreis = {"akt. Tarif","alter Tarif"};
	JEditorPane htmlPane = null;
	
	private JXTTreeTableNode aktNode;
	private int aktRow;
	private JXTTreeTableNode root = null;
	private TageTreeTableModel demoTreeTableModel = null;
	private JXTreeTable jXTreeTable = null;
	private JXTTreeTableNode foo = null;
	private JXMonthView mv;
	JDialog dlg;
	
	private int popUpX;
	private int popUpY;
	
	ActionListener tbaction = null;
	Rectangle rec = new Rectangle(0,0,0,0);
	
	boolean rezeptFertig = false;
	
	StringBuffer edibuf = new StringBuffer();
	StringBuffer htmlpos = new StringBuffer();
	StringBuffer htmlposbuf = new StringBuffer();
	String[] zzpflicht = {"keine gesetzliche Zuzahlung",
			"Zuzahlungsbefreit",
			"keine Zuzahlung trotz schriftlicher Zahlungsaufforderung",
			"Zuzahlungspflichtig",
			"Übergang zuzahlungspflichtig zu zuzahlungsfrei",
			"Übergang zuzahlungsfrei zu zuzahlungspflichtig"
	};
	
	boolean inworker = false;
	
	
	public AbrechnungRezept(Abrechnung1 xeltern){
		eltern = xeltern;
		setLayout(new BorderLayout());
		cmbkuerzel = new JRtaComboBox( (Vector<Vector<String>>) vec_kuerzel,0,1);
		cmbkuerzel.setActionCommand("cmbkuerzel");
		cmbkuerzel.addActionListener(this);
		add(getSplitPane(),BorderLayout.CENTER);

		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				jSplitOU.setDividerLocation(100);
				//jSplitOU.setDividerLocation(getHeight());
				//System.out.println("Höhe ="+getHeight());
			}
		});
		
	}
	private JXPanel getSplitPane(){
		JXPanel jpan = new JXPanel();
		jpan.setLayout(new BorderLayout());
		jSplitOU =  UIFSplitPane.createStrippedSplitPane(JSplitPane.VERTICAL_SPLIT,
				getHTMLPanel(),
				getTageTree() /*getEinzelRezPanel()*/); 
		jSplitOU.setDividerSize(7);
		jSplitOU.setDividerBorderVisible(true);
		jSplitOU.setName("BrowserSplitObenUnten");
		jSplitOU.setOneTouchExpandable(true);
		//jSplitOU.setDividerLocation(jpan.getHeight());
		jpan.add(getToolbar(),BorderLayout.NORTH);
		jpan.add(jSplitOU,BorderLayout.CENTER);

		return jpan;
	}
	private JScrollPane getHTMLPanel(){
		htmlPane = new JEditorPane(/*initialURL*/);
        htmlPane.setContentType("text/html");
        htmlPane.setEditable(false);
        htmlPane.setOpaque(false);
        htmlPane.addHyperlinkListener(this);
        parseHTML(null);
        JScrollPane jscr = JCompTools.getTransparentScrollPane(htmlPane);
        jscr.validate();
		return jscr;
	}
	public void setPreisVec(String xreznummer){
		if(xreznummer.contains("KG")){
			preisvec = (Vector<Vector<String>>)ParameterLaden.vKGPreise.clone();
			disziplinIndex = "2";
			disziplinGruppe = "22";
			preisregelIndex = 0;
		}else if(xreznummer.contains("MA")){
			preisvec = (Vector<Vector<String>>)ParameterLaden.vMAPreise.clone();
			disziplinIndex = "1";
			disziplinGruppe = "21";			
			preisregelIndex = 1;
		}else if(xreznummer.contains("ER")){
			preisvec = (Vector<Vector<String>>)ParameterLaden.vERPreise;
			disziplinIndex = "5";
			disziplinGruppe = "26";			
			preisregelIndex = 2;
		}else if(xreznummer.contains("LO")){
			preisvec = (Vector<Vector<String>>)ParameterLaden.vLOPreise;
			disziplinIndex = "3";
			disziplinGruppe = "23";			
			preisregelIndex = 3;
		}else if(xreznummer.contains("RH")){
			preisvec = (Vector<Vector<String>>)ParameterLaden.vRHPreise;
			disziplinIndex = "8";
			disziplinGruppe = "29";			
			preisregelIndex = 4;
		}
		int idpos = preisvec.get(0).size()-1;
		for(int i = 0;i< preisvec.size();i++){
			kundid.clear();
			kundid.add(preisvec.get(i).get(1));
			kundid.add(preisvec.get(i).get(idpos));
		}
	}
	public void setKuerzelVec(String xreznummer){
		if(xreznummer.contains("KG")){
			preisvec = (Vector<Vector<String>>)ParameterLaden.vKGPreise;
			disziplinIndex = "2";
			disziplinGruppe = "22";
			preisregelIndex = 0;
		}else if(xreznummer.contains("MA")){
			preisvec = (Vector<Vector<String>>)ParameterLaden.vMAPreise;
			disziplinIndex = "1";
			disziplinGruppe = "21";
			preisregelIndex = 1;
		}else if(xreznummer.contains("ER")){
			preisvec = (Vector<Vector<String>>)ParameterLaden.vERPreise;
			disziplinIndex = "5";
			disziplinGruppe = "26";
			preisregelIndex = 2;
		}else if(xreznummer.contains("LO")){
			preisvec = (Vector<Vector<String>>)ParameterLaden.vLOPreise;
			disziplinIndex = "3";
			disziplinGruppe = "23";
			preisregelIndex = 3;
		}else if(xreznummer.contains("RH")){
			preisvec = (Vector<Vector<String>>)ParameterLaden.vRHPreise;
			disziplinIndex = "8";
			disziplinGruppe = "29";
			preisregelIndex = 4;

		}
		vec_kuerzel.clear();
		int idpos = preisvec.get(0).size()-1;
		for(int i = 0;i< preisvec.size();i++){
			kundid.clear();
			kundid.add(preisvec.get(i).get(1));
			kundid.add(preisvec.get(i).get(idpos));
			vec_kuerzel.add( (Vector<String>)kundid.clone() );
		}
		Comparator<Vector> comparator = new Comparator<Vector>() {
		    public int compare(String s1, String s2) {
		        String[] strings1 = s1.split("\\s");
		        String[] strings2 = s2.split("\\s");
		        return strings1[strings1.length - 1]
		            .compareTo(strings2[strings2.length - 1]);
		    }

			@Override
			public int compare(Vector o1, Vector o2) {
				String s1 = (String)o1.get(0);
				String s2 = (String)o2.get(0);
				return s1.compareTo(s2);
			}
		};
		Collections.sort(vec_kuerzel,comparator);
		//System.out.println("Aus Funktion setKuerzelVec="+vec_kuerzel);
		try{
			mycomb2.setVector(vec_kuerzel,0,1);
			//mycomb.setVector(vec_kuerzel,0,1);			
		}catch(Exception ex){
			ex.printStackTrace();
			cmbkuerzel.setDataVectorVector(vec_kuerzel,0,1);
		}
		
	}
	public boolean setNewRez(String rez,boolean schonfertig){
		try{
//			String dummy1 = rez.split(",")[2];
//			String dummy2 = dummy1.split("-")[0];
			//System.out.println("Neues Rezept = "+dummy2);
			rezeptFertig = schonfertig;
			if(!rezeptFertig){
				aktRezNum.setText(rez);
				setKuerzelVec(rez);
				setWerte(rez);
			}else{
				System.out.println("Einlesen aus Edifact-Daten");
				aktRezNum.setText(rez);
				setKuerzelVec(rez);
				if(holeEDIFACT(rez)){
					while(inworker){
						Thread.sleep(20);
					}
					prepareTreeFromVector(true);
					doTreeRezeptWertermitteln();
					parseHTML(rez.trim());
					doPositionenErmitteln();
				}
				
			}
			rezeptSichtbar = true;;
			
		}catch(Exception ex){
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null,"Fehler - Rezept kann nicht abgerechner werden");
			return false;
		}
		return true;
	}
	/******
	 * 
	 * 
	 * @return
	 */
	
	private JXPanel getTageTree(){
		JXPanel jpan = new JXPanel(new BorderLayout());
		FormLayout lay = new FormLayout("0dlu,0dlu,fill:0:grow(1.0),20dlu,0dlu",
				"0dlu,p,0dlu,fill:0:grow(0.5),2dlu,fill:0:grow(0.5),60dlu");
		jpan.setLayout(lay);
		CellConstraints cc = new CellConstraints();
		
		root = new JXTTreeTableNode("root",null, true);
        demoTreeTableModel = new TageTreeTableModel(root);
        Highlighter hl = HighlighterFactory.createAlternateStriping();

        
        jXTreeTable = new JXTreeTable(demoTreeTableModel);
        jXTreeTable.addHighlighter(hl);
        jXTreeTable.addMouseListener(new MouseAdapter(){
        	public void mousePressed(MouseEvent evt){
        		if(evt.getButton()==3){
        			TreePath selpathss = jXTreeTable.getPathForLocation(evt.getX(), evt.getY());
        			//jXTreeTable.addHighlighter(HighlighterFactory.createAlternateStriping());
        			jXTreeTable.getTreeSelectionModel().setSelectionPath(selpathss);
        			ZeigePopupMenu(evt.getX(),evt.getY(),evt.getXOnScreen(),evt.getYOnScreen());
        		}else{
        			jXTreeTable.setShowGrid(false);
        		}
        	}
        	
        });
        
        jXTreeTable.setOpaque(true);
        jXTreeTable.setRootVisible(false);
        /*
        datePick.addPopupMenuListener(this);
        datePick.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		System.out.println("DatePicker Event = "+datePick.getDate().toString());
        	}
        });
        //DatePicker von Datum
        
        MyTableStringDatePicker myDate = new MyTableStringDatePicker(datePick);
        jXTreeTable.getColumnModel().getColumn(1).setCellEditor(myDate);
        */
        
        
      //ComboBox von Behandlungsart
        mycomb2 = new MyTableComboBox();
        ((JRtaComboBox)mycomb2.getComponent()).setActionCommand("kuerzel");
		((JRtaComboBox)mycomb2.getComponent()).addActionListener(this);
        jXTreeTable.getColumnModel().getColumn(2).setCellEditor(mycomb2);
        // Anzahlspalte
        jXTreeTable.getColumnModel().getColumn(3).setCellEditor(new DblCellEditor());
        //Preisspalte
        //jXTreeTable.getColumnModel().getColumn(4).setCellEditor(new DblCellEditor());
        jXTreeTable.getColumnModel().getColumn(4).setCellRenderer(new DoubleTableCellRenderer() );

        //Checkbox von Zuzahlung   
        check = new JRtaCheckBox();
        check.setActionCommand("zuzahlung");
        //check.addActionListener(this);
        check.setOpaque(true);
        ActionListener alcheck = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				zuzahlCheck(check.isSelected()); //System.out.println("Wert von Check = "+check.isSelected());
			}
        };
        
		mycheck = new MyTableCheckBox(check,alcheck);
		//((JRtaCheckBox)mycheck.getComponent()).setActionCommand("zuzahlung");
		//((JRtaCheckBox)mycheck.getComponent()).addActionListener(this);
		jXTreeTable.getColumnModel().getColumn(5).setCellEditor(mycheck);
		//jXTreeTable.getColumnModel().getColumn(5).setCellRenderer(new CheckRenderer(false));
		
        //Zuzahlungsbetrag
        jXTreeTable.getColumnModel().getColumn(6).setCellEditor(new DblCellEditor());
        jXTreeTable.getColumnModel().getColumn(6).setCellRenderer(new DoubleTableCellRenderer() );
        
        //Unterbrechungskennzeichen
        JRtaComboBox unterbrechung = new JRtaComboBox(voBreak);
        unterbrechung.setActionCommand("break");
        unterbrechung.addActionListener(this);
        MyTableComboBox combbreak = new MyTableComboBox(unterbrechung);
		jXTreeTable.getColumnModel().getColumn(7).setCellEditor(combbreak);
		jXTreeTable.getColumnModel().getColumn(7).setCellRenderer(new MitteRenderer());

        //Unterbrechungskennzeichen
        JRtaComboBox tarifart = new JRtaComboBox(new String[] {"aktuell","alt"});
        tarifart.setActionCommand("akttarif");
        tarifart.addActionListener(this);
        MyTableComboBox combtarifakt = new MyTableComboBox(tarifart);
		jXTreeTable.getColumnModel().getColumn(8).setCellEditor(combtarifakt);
		jXTreeTable.getColumnModel().getColumn(8).setCellRenderer(new MitteRenderer());

		
		//sqldatum
        jXTreeTable.getColumnModel().getColumn(9).setMinWidth(0);
        jXTreeTable.getColumnModel().getColumn(9).setMaxWidth(0);
        jXTreeTable.getColumnModel().getColumn(10).setMinWidth(0);
        jXTreeTable.getColumnModel().getColumn(10).setMaxWidth(0);
        
        jXTreeTable.getColumn(0).setMinWidth(55);
        jXTreeTable.validate();
        //jXTreeTable.getColumn(1).setCellEditor(new MyTableStringDatePicker());
        //jXTreeTable.getColumn(0).setCellEditor(new MyDateCellEditor());
        jXTreeTable.setSortOrder(9, SortOrder.ASCENDING);
        //jXTreeTable.getSelectionModel().addListSelectionListener(new AbrechnungListSelectionHandler());
        
        jXTreeTable.addTreeSelectionListener(new AbrechnungTreeSelectionListener());
        jXTreeTable.setSelectionMode(0);
        
        JScrollPane jscr = JCompTools.getTransparentScrollPane(jXTreeTable);
        jscr.validate();
        //jpan.add(jscr,cc.xywh(3,4,1,3));
        jpan.add(jscr,cc.xywh(1,4,5,4));
		return jpan;
		
	}
	private void zuzahlCheck(boolean zuzahl){
		int nodes;
		if(demoTreeTableModel.getPathToRoot(aktNode).length==2 && ((nodes = aktNode.getChildCount()) > 0)){
			String text = "Soll die Einstellung '"+(zuzahl ? "Zuzahlungspflichtig" : "keine Zuzahlung")+"' auf den gesamten Knoten angewendet werden";
			int anfrage = JOptionPane.showConfirmDialog(null,text ,"Achtung wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
			if(anfrage==JOptionPane.YES_OPTION  ){
				for(int i = 0; i< nodes;i++){
					((JXTTreeTableNode)aktNode.getChildAt(i)).abr.zuzahlung = Boolean.valueOf(zuzahl);
				}
			}
			if(this.aktRow==0){
				this.mitPauschale = zuzahl;
			}
		}else{
			((JXTTreeTableNode)aktNode).abr.zuzahlung = Boolean.valueOf(zuzahl);
			System.out.println(((JXTTreeTableNode)aktNode).abr.bezeichnung);
			System.out.println( "Zuzahlung ="+((JXTTreeTableNode)aktNode).abr.zuzahlung);
			if(this.aktRow==0){
				this.mitPauschale = zuzahl;
			}
		}
		jXTreeTable.repaint();
		//aktualisiereTree();
		doTreeRezeptWertermitteln();
		parseHTML(vec_rez.get(0).get(1).trim());
	}

	public void ZeigePopupMenu(int x, int y, int x2,int y2){
		JPopupMenu jPop = getTerminPopupMenu();
		popUpX = x2;
		popUpY = y2;
		jPop.show( (Component)jXTreeTable, (int)x, (int)y ); 
	}
	private JPopupMenu getTerminPopupMenu(){
		JPopupMenu jPopupMenu = new JPopupMenu();
		JMenuItem item = new JMenuItem("Alle Knoten expandieren");
		item.setActionCommand("expandall");
		item.addActionListener(this);
		jPopupMenu.add(item);
		item = new JMenuItem("Alle Knoten schließen");
		item.setActionCommand("collapsall");
		item.addActionListener(this);
		jPopupMenu.add(item);
		jPopupMenu.addSeparator();
		item = new JMenuItem("neuen Tag einfügen");
		item.setActionCommand("tagneu");
		item.addActionListener(this);
		jPopupMenu.add(item);
		jPopupMenu.addSeparator();
		item = new JMenuItem("neue Behandlung einfügen");
		item.setActionCommand("behandlungneu");
		item.addActionListener(this);
		jPopupMenu.add(item);
		jPopupMenu.addSeparator();
		item = new JMenuItem("Behandlung löschen");
		item.setActionCommand("behandlungloeschen");
		item.addActionListener(this);
		jPopupMenu.add(item);
		

		return jPopupMenu;
	}
	private JXMonthView showView(){
		final JXMonthView mv = new JXMonthView();
		ActionListener al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(mv.getSelectionDate());
			}
		};
		mv.addActionListener(this);
		mv.setName("picker2");
		mv.setTraversable(true);
		mv.setPreferredColumnCount(1);
		mv.setPreferredRowCount(1);
		mv.setShowingWeekNumber(true);
		return mv;
	}
	private JToolBar getToolbar(){
		JToolBar jtb = new JToolBar();
		jtb.setOpaque(false);
		jtb.setRollover(true);
		jtb.setBorder(null);
		jtb.setOpaque(false);
		tbaction = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd = arg0.getActionCommand();
				if(cmd.equals("abschliessen")){
					if(rezeptFertig){
						rezeptFertig = false;
						new SwingWorker<Void,Void>(){
							@Override
							protected Void doInBackground()
									throws Exception {
								SqlInfo.sqlAusfuehren("update fertige set ediok='F',edifact='' where rez_nr='"+vec_rez.get(0).get(1)+"' LIMIT 1");
								return null;
							}
							
						}.execute();
					}else{
						if(macheEDIFACT()){
							rezeptFertig = true;
							new SwingWorker<Void,Void>(){
								@Override
								protected Void doInBackground()
										throws Exception {
									SqlInfo.sqlAusfuehren("update fertige set ediok='T',edifact='"+StringTools.Escaped(edibuf.toString())+"' where rez_nr='"+vec_rez.get(0).get(1)+"' LIMIT 1");
									return null;
								}
								
							}.execute();
						}else{
							rezeptFertig = false;
							new SwingWorker<Void,Void>(){
								@Override
								protected Void doInBackground()
										throws Exception {
									SqlInfo.sqlAusfuehren("update fertige set ediok='F',edifact='' where rez_nr='"+vec_rez.get(0).get(1)+"' LIMIT 1");
									return null;
								}
								
							}.execute();
						}
						
						//hier den Edifact einbauen
					}
					eltern.setRezeptOk(rezeptFertig);

				}
				if(cmd.equals("scannen")){
					eltern.starteAbrechnung();
				}
			}
		};
		JXPanel rezpan = new JXPanel(new BorderLayout());
		rezpan.setBorder(BorderFactory.createEtchedBorder(1));
		//rezpan.setPreferredSize(new Dimension(150,0));
		rezpan.setSize(100,30);
		rezpan.setMaximumSize(new Dimension(150,30));
		rezpan.setOpaque(false);
		aktRezNum = new JLabel();
		aktRezNum.setHorizontalAlignment(JLabel.CENTER);
		aktRezNum.setFont(new Font("Tahoma",Font.PLAIN,15));
		aktRezNum.setForeground(Color.BLUE);
		rezpan.add(aktRezNum,BorderLayout.CENTER);
		jtb.add(rezpan);
		
		tbbuts[2] = new JButton();
		tbbuts[2].setIcon(SystemConfig.hmSysIcons.get("abschliessen"));
		tbbuts[2].setToolTipText("Rezept abschließen");
		tbbuts[2].setActionCommand("abschliessen");
		tbbuts[2].addActionListener(tbaction);
		jtb.add(tbbuts[2]);

		jtb.addSeparator(new Dimension(30,0));
		
		tbbuts[0] = new JButton();
		tbbuts[0].setIcon(SystemConfig.hmSysIcons.get("print"));
		tbbuts[0].setToolTipText("Rezept taxieren");
		tbbuts[0].setActionCommand("taxieren");
		tbbuts[0].addActionListener(tbaction);
		jtb.add(tbbuts[0]);

		tbbuts[1] = new JButton();
		tbbuts[1].setIcon(SystemConfig.hmSysIcons.get("scanner"));
		tbbuts[1].setToolTipText("Rezept scannen");
		tbbuts[1].setActionCommand("scannen");
		tbbuts[1].addActionListener(tbaction);
		jtb.add(tbbuts[1]);


		return jtb;
	}
	public void setRechtsAufNull(){
		while( root.getChildCount() > 0){
			demoTreeTableModel.removeNodeFromParent((MutableTreeTableNode) root.getChildAt(0));
		}
		vec_tabelle.clear();
		htmlPane.setText("<html></html>");
		aktRezNum.setText("");
		rezeptSichtbar = false;
		return;
		
	}
	private void setWerte(String rez_nr){

		String cmd = "select * from verordn where rez_nr='"+rez_nr.trim()+"' LIMIT 1";
		//System.out.println("Kommando = "+cmd);
		vec_rez = SqlInfo.holeFelder(cmd);
		//System.out.println("RezeptVektor = "+vec_rez);
		if(vec_rez.size()<=0){
			return;
		}
		gebuehrBezahlt = vec_rez.get(0).get(14).trim().equals("T");
		gebuehrBetrag = Double.parseDouble(vec_rez.get(0).get(13));
		//                                       0         1         2             3     4       5 
		vec_pat = SqlInfo.holeFelder("select  t1.n_name,t1.v_name,t1.geboren,t1.strasse,t1.plz,t1.ort,"+
		//            6             7            8              9     10         11        12
				"t1.v_nummer,t1.kv_status,t1.kv_nummer,"+"t1.befreit,t1.bef_ab,bef_dat,t1.jahrfrei,"+
		//           13         14         15       16      		
				"t2.nachname,t2.bsnr,t2.arztnum,t3.kassen_nam1 from pat5 t1,arzt t2,kass_adr t3 where t1.pat_intern='"+
				vec_rez.get(0).get(0)+"' AND t2.id ='"+vec_rez.get(0).get(16)+"' AND t3.id='"+vec_rez.get(0).get(37)+"' LIMIT 1");
		if(vec_pat.get(0).get(9).equals("T")){
			patAktuellFrei = true;
		}else{
			patAktuellFrei  = false;
		}
		if(! vec_pat.get(0).get(12).trim().equals("")){
			patVorjahrFrei = true;
		}else{
			patVorjahrFrei = false;
		}
		patFreiAb = vec_pat.get(0).get(10);
		patFreiBis = vec_pat.get(0).get(11);
		patU18 = DatFunk.Unter18(DatFunk.sHeute(), DatFunk.sDatInDeutsch(vec_pat.get(0).get(2)));
		System.out.println("Vec_Rez = "+vec_rez);
		System.out.println("Vec_Pat = "+vec_pat);
		ermittleAbrechnungsfall(true);
		if(hausbesuch){
			doHausbesuchKomplett();
			getVectorFromNodes();
		}
		System.out.println("Nodes insgesamt ="+getNodeCount()+" VectorLänge = "+vec_tabelle.size());
		doGebuehren();
		doPositionenErmitteln();
		doTreeRezeptWertermitteln();
		parseHTML(rez_nr.trim());
		
	}
	/*****************************************************************************************/
	private void ermittleAbrechnungsfall(boolean construct){
		//vec_tabelle.clear();
		
		vectage = RezTools.macheTerminVector( vec_rez.get(0).get(34));
		Vector<Vector<String>> vecabrfaelle = new Vector<Vector<String>>();
		Vector<String> vecabrfall = new Vector<String>();
		vec_tabelle.clear();
		vec_poskuerzel.clear();
		vec_posanzahl.clear();
		vec_pospos.clear();
		String[] behandlungen = null;
		
		preisgruppe = vec_rez.get(0).get(41);
		int anzahlbehandlungen = 0;

		int[] ids = {Integer.parseInt(vec_rez.get(0).get(8)),
				Integer.parseInt(vec_rez.get(0).get(9)),
				Integer.parseInt(vec_rez.get(0).get(10)),
				Integer.parseInt(vec_rez.get(0).get(11)) };
		for(int i = 0; i < 4; i++){
			if(ids[i] > 0 ){
				anzahlbehandlungen++;
			}else{
				break;
			}
		}
		//int anzahlhb = 0;
		//int anzahlposhb = 0;
		//boolean hausbesuch = false;

		if(vec_rez.get(0).get(43).equals("T")){
			hausbesuch= true;
			if(RezTools.zweiPositionenBeiHB(preisgruppe)){
				anzahlposhb = 2;
				anzahlhb=Integer.valueOf(vec_rez.get(0).get(64));
			}else{
				anzahlposhb = 1;
				anzahlhb=Integer.valueOf(vec_rez.get(0).get(64));
			}
		}else{
			hausbesuch = false;
		}
		String splitvec = null;
		try{
			System.out.println("Vector ========= "+(Vector<Vector<String>>) vec_kuerzel.clone());
			//ystem.out.println("Combobox = "+cmbkuerzel);
			//cmbkuerzel.setDataVectorVector( (Vector<Vector<String>>) vec_kuerzel.clone(), 0, 1);
		}catch(Exception ex){
			ex.printStackTrace();
			System.out.println("Fehler in VecKuerzel = "+vec_kuerzel);
		}

		if(construct){
			int childs;
			while( (childs=root.getChildCount()) > 0){
				demoTreeTableModel.removeNodeFromParent((MutableTreeTableNode) root.getChildAt(0));
			}
		}
		
		for(int i = 0; i < vectage.size();i++){
			splitvec = vectage.get(i).get(3);
			behandlungen = splitvec.split(",");
			if(behandlungen.length > 0 && (!splitvec.trim().equals(""))){
				//Es stehen Behandlungsdaten im Terminblatt;
				//Positionen = length+hbposanzahl;
				//System.out.println("Länge des Feldes = "+behandlungen.length);
				//anzahlbehandlungen = behandlungen.length;
				System.out.println("Konstruiere Abrechnungsfall aus behandlungen.length");
				constructTagVector(vectage.get(i).get(0),behandlungen,behandlungen.length,anzahlhb);
			}else{
				//Es sind keine  Behandlungsformen im Terminblatt verzeichnet;
				//in anzahlbehandlungen steht die tatsächliche Anzahl
				//System.out.println("Keine Behandlungen im Terminblatt = "+anzahlbehandlungen);
				//System.out.println("Anzahl-Hausbesuch  keine Beh. im Terminblatt= "+anzahlhb);
				System.out.println("Konstruiere Abrechnungsfall aus Rezeptangaben");
				constructTagVector(vectage.get(i).get(0),null,anzahlbehandlungen,anzahlhb);
			}
		}
		

		if(construct){
			doFuelleTreeTable();
			aktNode = null;
			aktRow = -1;
		}
	}
	/******************************
	 * 
	 * 
	 * 
	 * 
	 */
	private void doGebuehren(){
		zuZahlungsIndex = 	zzpflicht[3];
		zuZahlungsPos = "3";
		int nodes;
		if((nodes =getNodeCount())<= 0){return;}
		
		boolean amBeginnFrei = false;
		boolean amEndeFrei = false;

		boolean volleZuzahlung = true;

		boolean unter18 = false;
		
		boolean vollFrei = false;
		boolean teilFrei = false;
		mitPauschale = true;
		boolean jahresWechsel = false;
		zuZahlungsPos = "3";
		if(SystemConfig.vZuzahlRegeln.get(Integer.valueOf(preisgruppe)-1).equals("0")){
			zuZahlungsIndex = zzpflicht[0];
			zuZahlungsPos = "0";
			doTreeFreiAb(0,nodes,false);
			return;
		}
		if(patU18){
			unter18 = true;
			vollFrei = true;
			amBeginnFrei = true;
			volleZuzahlung = false;
			mitPauschale = false;
			doTreeFreiAb(0,nodes,false);
			zuZahlungsIndex = zzpflicht[0];
			zuZahlungsPos = "0";
			//doTreeFreiAb(0,vec_tabelle.size(),false);

		}else{
			Object[] u18 = RezTools.unter18Check(vec_tabelle, DatFunk.sDatInDeutsch(vec_pat.get(0).get(2)) );
			System.out.println("Unter 18 = "+u18[0]+" / ab Position = "+u18[1]+" Splitting erforderlich = "+u18[2]);
			if( ( (Boolean)u18[0]) && (! (Boolean)u18[2]) ){
				//Während der Kompletten Behandlung unter 18;
				unter18 = true;
				vollFrei = true;
				amBeginnFrei = true;
				volleZuzahlung = false;
				mitPauschale = false;
				doTreeFreiAb(0,nodes,false);
				zuZahlungsIndex = zzpflicht[0];
				zuZahlungsPos = "0";
				//doTreeFreiAb(0,vec_tabelle.size(),false);
			}else if(( (Boolean)u18[0]) && ( (Boolean)u18[2])) {
				//Während der Behandlung 18 geworden, Rezept muß gesplittet werden;
				unter18 = true;
				teilFrei = true;
				amBeginnFrei = true;
				volleZuzahlung = false;
				mitPauschale = false;
				doTreeFreiAb(0,(Integer)u18[1],false);
				doTreeFreiAb((Integer)u18[1],nodes,true);
				zuZahlungsIndex = zzpflicht[5];
				zuZahlungsPos = "5";
			}
		}

		Object[] newYear = RezTools.jahresWechselCheck(vec_tabelle);
		System.out.println("Jahreswechsel = "+newYear[0]+" / JahresWechsel ab Position = "+newYear[1]+" / Rezept vollständig im Vorjahr  = "+newYear[2]);
		int wechselfall = 0;
		int altfall = 0;
		int neufall = 0;
		
		if((Boolean)newYear[0] && (Boolean)newYear[2]){
			//Das Rezept ist komplett im Vorjahr abgearbeitet worden
			if(patVorjahrFrei && (!unter18)){
				altfall = 1;
				doTreeFreiAb(0,nodes,false);
				mitPauschale = false;
				zuZahlungsIndex = zzpflicht[1];
				zuZahlungsPos = "1";
			}else{
				altfall = 2;
				doTreeFreiAb(0,nodes,true);
				mitPauschale = true;
				zuZahlungsIndex = zzpflicht[3];
				zuZahlungsPos = "3";
			}
			
		}else if((Boolean)newYear[0] && (!(Boolean)newYear[2])){
			//Das Rezept ist erstreckt sich über den Jahreswechsel
			if(gebuehrBezahlt){  //1.Fall
				// die Gebühr wurde bereits bezahlt was soviel heißt wie es muß verrechnet werden
				// doFreiAb(0,vec_tabelle.size(),true);
				doTreeFreiAb(0,nodes,true);
				mitPauschale = true;
				wechselfall = 1;
				zuZahlungsIndex = zzpflicht[3];
				zuZahlungsPos = "3";
			}else if(patVorjahrFrei && patAktuellFrei && (!gebuehrBezahlt)){ //2.Fall
				// im Vorjahr befreit und jetzt schon wieder befreit und kein Vermerk bezahlt
				doTreeFreiAb(0,nodes,false);
				//doTreeFreiAb(0,vec_tabelle.size(),false);
				mitPauschale = false;
				wechselfall = 2;
				zuZahlungsIndex = zzpflicht[1];
				zuZahlungsPos = "1";
			}else if( (patVorjahrFrei) && (!patAktuellFrei) ){ //3.Fall
				// im Vorjahr befreit und jetzt zuzahlungspflichtig
				doTreeFreiAb(0,(Integer)newYear[1],false);
				doTreeFreiAb((Integer)newYear[1],nodes,true);
				//doFreiAb(0,(Integer)newYear[1],false);
				mitPauschale = false;
				wechselfall = 3;
				zuZahlungsIndex = zzpflicht[5];
				zuZahlungsPos = "5";
			}else if((!patVorjahrFrei) && (!patAktuellFrei) && (!unter18) ){ //4.Fall
				//weder im Vorjahr noch im aktuellen Jahr befreit und auch nicht unter 18
				doTreeFreiAb(0,nodes,true);
				mitPauschale = true;
				wechselfall = 4;
				zuZahlungsIndex = zzpflicht[3];
				zuZahlungsPos = "3";
			}else if((!patVorjahrFrei) && (patAktuellFrei) && (!unter18)){ //5.Fall
				// war nicht im Vorjahr nicht befreit ist aber jetzt befreit
				doTreeFreiAb(0,(Integer) newYear[1],true);
				doTreeFreiAb((Integer) newYear[1],nodes,false);
				//doFreiAb((Integer) newYear[1],vec_tabelle.size(),true);
				mitPauschale = true;
				wechselfall = 5;
				zuZahlungsIndex = zzpflicht[4];
				zuZahlungsPos = "4";
			}
		}else if(!(Boolean)newYear[0]){
			//Das Rezept wurde vollständig im aktuelle Jahr abgearbeitet
			if(patAktuellFrei && (!gebuehrBezahlt)){
				doTreeFreiAb(0,nodes,false);
				//doFreiAb(0,vec_tabelle.size(),false);
				mitPauschale = false;
				neufall = 1;
				zuZahlungsIndex = zzpflicht[1];
				zuZahlungsPos = "1";
			}else if(patAktuellFrei && gebuehrBezahlt ){
				doTreeFreiAb(0,nodes,true);
				//doFreiAb(0,vec_tabelle.size(),true);
				mitPauschale = true;
				neufall = 2;
				zuZahlungsIndex = zzpflicht[3];
				zuZahlungsPos = "3";
			}
		}
		System.out.println("/*****************************************/");
		System.out.println("Im aktuellen Jahr befreit? = "+patAktuellFrei);
		System.out.println("               Befreit ab? = "+(DatFunk.datumOk(patFreiAb)==null ? "keine Angaben" : DatFunk.datumOk(patFreiAb)) );
		System.out.println("              Befreit bis? = "+(DatFunk.datumOk(patFreiBis)==null ? "keine Angaben" : DatFunk.datumOk(patFreiBis)) );
		System.out.println("   Im VorjahrJahr befreit? = "+patVorjahrFrei);
		System.out.println("     Patient ist unter 18? = "+patU18);
		System.out.println("   Rezeptgebühren bezahlt? = "+gebuehrBezahlt);
		System.out.println("    Rezeptgebühren Betrag? = "+gebuehrBetrag);
		System.out.println("     Mit 10 EUR Pauschale? = "+mitPauschale);
		System.out.println("    Konstellation nur alt? = "+altfall);
		System.out.println("    Konstellation wechsel? = "+wechselfall);
		System.out.println("    Konstellation nur neu? = "+neufall);
		System.out.println("/*****************************************/");		
		/*****HausbesuchsCheck*****/

		
		
		///// Jetzt der Tarifwchsel-Check
		// erst einlesen ab wann der Tarif gültig ist
		// dann testen ob Rzeptdatum nach diesem Datum liegt wenn ja sind Preise o.k.
		// wenn nein -> testen welche Anwendungsregel gilt und entsprchend in einer
		// for next Schleife die Preise anpassen!
		// bevor jetzt weitergemacht werden kann muß der Vector für die Behandlungen erstellt werden!!!!!
		doTarifWechselCheck();

	}
	/***************************TarifWechselCheck********************************/
	private void doTarifWechselCheck(){
		int tarifgruppe = Integer.valueOf(preisgruppe)-1;
		String datum = SystemConfig.vNeuePreiseAb.get(this.preisregelIndex).get(tarifgruppe);
		if(datum.trim().equals("")){
			return;
		}
		int regel = SystemConfig.vNeuePreiseRegel.get(this.preisregelIndex).get(tarifgruppe);
		String erster = getDatumErsterTag();
		String letzter = getDatumLetzterTag();
		
		System.out.println(DatFunk.TageDifferenz(datum, getDatumErsterTag()));
		System.out.println(DatFunk.TageDifferenz("31.12.2009", "31.12.2009"));

		if(regel==1){
			//erste Behandlung >= Stichtag alle zu neuem Tarif
			if(DatFunk.TageDifferenz(datum, erster) < 0){
				//setze alle auf alten Tarif
				setTarif(true,false,"");
				return;
			}
		}
		if(regel==2){
			//Rezeptdatum >= Stichtag
			if(DatFunk.TageDifferenz(datum, DatFunk.sDatInDeutsch(vec_rez.get(0).get(2))) < 0){
				//setze alle auf alten Tarif
				setTarif(true,false,"");
				return;
			}
		}
		if(regel==3){
			//Beliebiger Tag innerhalb er Spanne
			if(  (DatFunk.TageDifferenz(datum,erster )< 0)  ||  (DatFunk.TageDifferenz(datum,letzter ) < 0) ){
				//setze alle auf alten Tarif
				setTarif(true,false,"");
				return;
			}
		}
		if(regel==4){
			//es muß gesplittet werden
			setTarif(false,true,datum);
			return;
		}
		
	}
	private void setTarif(boolean allealt,boolean split,String splitdatum){
		int count = getNodeCount();
		AbrFall abr;
		if(allealt){
			for(int i = 0; i< count;i++){
				abr = this.holeAbrFall(i);
				abr.alterpreis = "alt";
				abr.preis = Double.valueOf(RezTools.getPreisAltFromID(abr.preisid, preisgruppe, preisvec).replace(",", "."));
			}
			return;
		}else if(split){
			for(int i = 0; i< count;i++){
				abr = this.holeAbrFall(i);
				if( DatFunk.TageDifferenz(splitdatum,abr.datum ) < 0){
					abr.alterpreis = "alt";
					String preis = RezTools.getPreisAltFromID(abr.preisid, preisgruppe, preisvec).replace(",", ".");
					System.out.println("Alter Preis = "+preis);
					System.out.println("PreisID = "+abr.preisid);
					try{
						abr.preis = Double.valueOf(preis);
					}catch(Exception ex){}
				}
			}
			return;
		}
	}
	private String getDatumErsterTag(){
		return this.holeAbrFall(0).datum;
	}
	private String getDatumLetzterTag(){
		return this.holeAbrFall(getNodeCount()-1).datum;
	}
	/***************************Hausbesuchsgedönse********************************/
	private void doHausbesuchKomplett(){
		
		int insgesamthb = anzahlhb;
		vec_hb = SqlInfo.holeFelder("select heimbewohn,kilometer from pat5 where pat_intern='"+
				vec_rez.get(0).get(0)+"' LIMIT 1");
		boolean vollepackung = vec_rez.get(0).get(61).equals("T");
		boolean heimbewohner = vec_hb.get(0).get(0).equals("T");
		double anzahlkm = 0.00;
		
		int maxanzahl = root.getChildCount();
		try{
			anzahlkm = Double.parseDouble(vec_hb.get(0).get(1).replace(",", "."));
		}catch(Exception ex){}

		/*********Jetzt geht's los ***********/
		//System.out.println("HB-Regeln = "+SystemConfig.vHBRegeln);
		if(maxanzahl < insgesamthb){
			JOptionPane.showMessageDialog(null,"Achtung die Anzahl der Behandlungstage stimmt nicht mit der\n"+
					"Angabe Anzahl Hausbesuche im Rezeptstamm überein");
		}
		System.out.println("zugrundeLiegende Preisgruppe = "+preisgruppe);
		String tag = ""; 
		int pos = 0;
		String position;
		boolean immerfrei = false;
		String kilometerpos = SystemConfig.vHBRegeln.get(Integer.parseInt(preisgruppe)-1).get(2).replace("x",disziplinIndex);
		String pauschalepos = SystemConfig.vHBRegeln.get(Integer.parseInt(preisgruppe)-1).get(3).replace("x",disziplinIndex);
		String hauptziffer = SystemConfig.vHBRegeln.get(Integer.parseInt(preisgruppe)-1).get(0).replace("x",disziplinIndex);
		String mehrereziffer = SystemConfig.vHBRegeln.get(Integer.parseInt(preisgruppe)-1).get(1).replace("x",disziplinIndex);
		System.out.println("Anzahl Kilometer = "+anzahlkm);
		for(int i = 0; i < maxanzahl;i++){
			
			JXTTreeTableNode node = (JXTTreeTableNode)root.getChildAt(i);
			tag = node.abr.datum;
			System.out.println("Behandlungstag "+tag+" HB hinzufügen");
			//Zunächst die Position HB-Einzeln oder HB-Mit hinzufügen
			if(vollepackung){
				position = hauptziffer; 
			}else{
				position = mehrereziffer;
			}
			if(heimbewohner){
				immerfrei = SystemConfig.vHBRegeln.get(Integer.parseInt(preisgruppe)-1).get(4).equals("0");
			}
			System.out.println("Es ist ein Heimbewohner ="+heimbewohner+" HB-Ziffern sind immer frei="+immerfrei);
			// Die Hauptziffer anhängen
			// Parameter sind    1.Tag  Basis  Datum  HM-Postion       Anzahl                 immerfrei
			abrfallAnhaengen(    i+1,   node,  tag,   position,    Double.parseDouble("1.00"),immerfrei);

			//Jetzt untersuchen ob Wegegeld angehängt werden kann! (nur möglich wenn hauptziffer abgerechnet wird.
			if( (vollepackung) && (this.anzahlposhb > 1)){
				//Kilometer im Patientenstamm angegeben und es existiert eine Kilometerziffer 
				if(anzahlkm > 0 && (!kilometerpos.trim().equals(""))){
					abrfallAnhaengen(    i+1,   node,  tag,   kilometerpos,    anzahlkm ,immerfrei);
					//Kilometer im Patientenstamm auf 0 gesetzt und es existiert eine Pauschalenziffer
				}else if(anzahlkm == 0 && (!pauschalepos.trim().equals(""))){
					abrfallAnhaengen(    i+1,   node,  tag,   pauschalepos,    Double.parseDouble("1.00"),immerfrei);
				}else if(anzahlkm == 0 && (!kilometerpos.trim().equals("")) &&
						(!pauschalepos.trim().equals(""))){
					JOptionPane.showMessageDialog(null,"Diese Kasse kann nur mit Kilometer abgerechnet werden.\n"+
							"Die Angaben im Pateientenstamm lauten auf 0-Kilometer, bitte korrigieren");
					abrfallAnhaengen(    i+1,   node,  tag,   kilometerpos,    Double.parseDouble("1.00"),immerfrei);
				}
					
			}
		}
		

	}
	private void doHausbesuchEinzeln(JXTTreeTableNode node,int basisindex){
		vec_hb = SqlInfo.holeFelder("select heimbewohn,kilometer from pat5 where pat_intern='"+
				vec_rez.get(0).get(0)+"' LIMIT 1");
		int insgesamthb = anzahlhb;
		boolean vollepackung = vec_rez.get(0).get(61).equals("T");
		boolean heimbewohner = vec_hb.get(0).get(0).equals("T");
		double anzahlkm = 0.00;
		int maxanzahl = root.getChildCount();
		try{
			anzahlkm = Double.parseDouble(vec_hb.get(0).get(1).replace(",", "."));
		}catch(Exception ex){}

		/*********Jetzt geht's los ***********/
		//System.out.println("HB-Regeln = "+SystemConfig.vHBRegeln);
		if(maxanzahl < insgesamthb){
			JOptionPane.showMessageDialog(null,"Achtung die Anzahl der Behandlungstage stimmt nicht mit der\n"+
					"Angabe Anzahl Hausbesuche im Rezeptstamm überein");
		}
		System.out.println("zugrundeLiegende Preisgruppe = "+preisgruppe);
		String tag = ""; 
		int pos = 0;
		String position;
		boolean immerfrei = false;
		String kilometerpos = SystemConfig.vHBRegeln.get(Integer.parseInt(preisgruppe)-1).get(2).replace("x",disziplinIndex);
		String pauschalepos = SystemConfig.vHBRegeln.get(Integer.parseInt(preisgruppe)-1).get(3).replace("x",disziplinIndex);
		String hauptziffer = SystemConfig.vHBRegeln.get(Integer.parseInt(preisgruppe)-1).get(0).replace("x",disziplinIndex);
		String mehrereziffer = SystemConfig.vHBRegeln.get(Integer.parseInt(preisgruppe)-1).get(1).replace("x",disziplinIndex);
		System.out.println("Anzahl Kilometer = "+anzahlkm);

			
			//JXTTreeTableNode node = (JXTTreeTableNode)root.getChildAt(i);
		tag = node.abr.datum;
		System.out.println("Behandlungstag "+tag+" HB hinzufügen");
		//Zunächst die Position HB-Einzeln oder HB-Mit hinzufügen
		if(vollepackung){
			position = hauptziffer; 
		}else{
			position = mehrereziffer;
		}
		if(heimbewohner){
			immerfrei = SystemConfig.vHBRegeln.get(Integer.parseInt(preisgruppe)-1).get(4).equals("0");
		}
		System.out.println("Es ist ein Heimbewohner ="+heimbewohner+" HB-Ziffern sind immer frei="+immerfrei);
		// Die Hauptziffer anhängen
		// Parameter sind    1.Tag  Basis  Datum  HM-Postion       Anzahl                 immerfrei
		abrfallAnhaengen(    basisindex+1,   node,  tag,   position,    Double.parseDouble("1.00"),immerfrei);

		//Jetzt untersuchen ob Wegegeld angehängt werden kann! (nur möglich wenn hauptziffer abgerechnet wird.
		if( (vollepackung) && (this.anzahlposhb > 1)){
			//Kilometer im Patientenstamm angegeben und es existiert eine Kilometerziffer 
			if(anzahlkm > 0 && (!kilometerpos.trim().equals(""))){
				abrfallAnhaengen(    basisindex+1,   node,  tag,   kilometerpos,    anzahlkm ,immerfrei);
				//Kilometer im Patientenstamm auf 0 gesetzt und es existiert eine Pauschalenziffer
			}else if(anzahlkm == 0 && (!pauschalepos.trim().equals(""))){
				abrfallAnhaengen(    basisindex+1,   node,  tag,   pauschalepos,    Double.parseDouble("1.00"),immerfrei);
			}else if(anzahlkm == 0 && (!kilometerpos.trim().equals("")) &&
					(!pauschalepos.trim().equals(""))){
					JOptionPane.showMessageDialog(null,"Diese Kasse kann nur mit Kilometer abgerechnet werden.\n"+
							"Die Angaben im Pateientenstamm lauten auf 0-Kilometer, bitte korrigieren");
					abrfallAnhaengen(    basisindex+1,   node,  tag,   kilometerpos,    Double.parseDouble("1.00"),immerfrei);
			}
		}
	}
	
	/***************************Hausbesuchsgedönse********************************/	
	private void doPositionenErmitteln(){
		vec_poskuerzel.clear();
		vec_posanzahl.clear();
		vec_pospos.clear();
		int lang = 0;
		if( (lang=vec_tabelle.size())<=0){return;}
		for(int i = 0; i < lang;i++){
			if(! vec_poskuerzel.contains((vec_tabelle.get(i).get(1) )) ){
				vec_poskuerzel.add(vec_tabelle.get(i).get(1).toString());
				vec_pospos.add(RezTools.getPosFromID(vec_tabelle.get(i).get(9).toString(), preisgruppe, preisvec));
				vec_posanzahl.add(1);
			}else{
				int pos = vec_poskuerzel.indexOf(vec_tabelle.get(i).get(1) );
				int anzahl = vec_posanzahl.get(pos);
				vec_posanzahl.set(pos, anzahl+1);
			}
		}
		System.out.println("****************nachfolgende Positionen wurden ermittelt****************");
		System.out.println(vec_poskuerzel);
		System.out.println(vec_posanzahl);
		System.out.println(vec_pospos);
	}
	private void doZeileWertermitteln(){
		
	}
	/*
	private void doRezeptWertermitteln(){
		int lang = 0;
		rezeptWert = 0.00;
		zuzahlungWert = 0.00;

		BigDecimal dummy1 = BigDecimal.valueOf(Double.parseDouble("0.00"));
		BigDecimal dummy2 = BigDecimal.valueOf(Double.parseDouble("0.00"));

		BigDecimal ddummy1 = BigDecimal.valueOf(Double.parseDouble("0.00"));
		BigDecimal ddummy2 = BigDecimal.valueOf(Double.parseDouble("0.00"));

		if( (lang=tageMod.getRowCount())<=0){return;}
		//System.out.println("Vectorlänge von vec_tabelle = "+lang);
		Object ob1 = null;
		Object ob2 = null;
		Object ob3 = null;
		for(int i = 0; i < lang;i++){
			ob1 = tageMod.getValueAt(i, 2);
			ob2 = tageMod.getValueAt(i, 3);
			ob3 = tageMod.getValueAt(i, 5);
			ddummy1 = dummy1.add(  (BigDecimal.valueOf((Double)ob1).multiply(BigDecimal.valueOf((Double)ob2)))   );
			ddummy2 = dummy2.add(  (BigDecimal.valueOf((Double)ob1).multiply(BigDecimal.valueOf((Double)ob3)))   );
			dummy1 = ddummy1;
			dummy2 = ddummy2;
		}
		if(mitPauschale){
			ddummy2 = dummy2.add(BigDecimal.valueOf(Double.parseDouble("10.00")));
		}
		rezeptWert = ddummy1.doubleValue();
		zuzahlungWert = ddummy2.doubleValue();
		System.out.println("Rezeptwert Gesamt = "+dfx.format(rezeptWert));
		System.out.println("Zuzahlung Gesamt = "+dfx.format(zuzahlungWert));

	}
	*/
	private void doTreeRezeptWertermitteln(){
		int lang = 0;
		rezeptWert = 0.00;
		zuzahlungWert = 0.00;

		BigDecimal dummy1 = BigDecimal.valueOf(Double.parseDouble("0.00"));
		BigDecimal dummy2 = BigDecimal.valueOf(Double.parseDouble("0.00"));

		BigDecimal ddummy1 = BigDecimal.valueOf(Double.parseDouble("0.00"));
		BigDecimal ddummy2 = BigDecimal.valueOf(Double.parseDouble("0.00"));

		if( (lang=this.getNodeCount())<=0){return;}
		Object ob1 = null;
		Object ob2 = null;
		Object ob3 = null;
		boolean zuzahl = false;
		for(int i = 0; i < lang;i++){
			AbrFall abr = this.holeAbrFall(i);
			ob1 = abr.anzahl;
			ob2 = abr.preis;
			
			zuzahl = abr.zuzahlung;
			ddummy1 = dummy1.add(  (BigDecimal.valueOf((Double)ob1).multiply(BigDecimal.valueOf((Double)ob2)))   );
			if(zuzahl && (!abr.niezuzahl)){
				//ob3 = this.rechneRezGebFromDouble(abr.preis);
				 ob3 = (BigDecimal.valueOf((Double)ob1).multiply(BigDecimal.valueOf((Double)this.rechneRezGebFromDouble(abr.preis))));
				 abr.rezgeb = ((BigDecimal)ob3).doubleValue();
				 ddummy2 = dummy2.add(  ((BigDecimal)ob3));
			}else{

				abr.zuzahlung = false;
				abr.rezgeb = 0.00;
			}
			dummy1 = ddummy1;
			dummy2 = ddummy2;
		}
		if(mitPauschale){
			ddummy2 = dummy2.add(BigDecimal.valueOf(Double.parseDouble("10.00")));
		}
		rezeptWert = ddummy1.doubleValue();
		zuzahlungWert = ddummy2.doubleValue();
		System.out.println("Rezeptwert Gesamt = "+dfx.format(rezeptWert));
		System.out.println("Zuzahlung Gesamt = "+dfx.format(zuzahlungWert));

	}

	private void doTreeFreiAb(int von, int bis,boolean pflichtig){
		System.out.println("Starte bei "+von);
		System.out.println("Ende bei "+bis);
		System.out.println("Setze Zuzahlungs-Flag="+pflichtig);
		for(int i = von; i < bis; i++){
			AbrFall abr = holeAbrFall(i);
				if((!pflichtig) || (abr.niezuzahl)){
					abr.zuzahlung = pflichtig;
					abr.rezgeb = Double.parseDouble("0.00");
				}else{
					abr.zuzahlung = pflichtig;
					abr.rezgeb = rechneRezGebFromDouble(abr.preis);
				}
		}
	}
	
	/*******************************
	 * 
	 * 
	 * 
	 * 
	 */
	/*******************************/
	private void constructTagVector(String datum,String[] behandlungen,int anzahlbehandlungen,int anzahlhb){
		String[] abrfall = new String[anzahlbehandlungen];
		String[] id = new String[anzahlbehandlungen];
		Object[] abrObject = {"",""};
		if(behandlungen!=null){
			for(int i = 0; i < anzahlbehandlungen;i++){
				//abrfall[i] = RezTools.getKurzformFromPos(behandlungen[i].trim(), preisgruppe, preisvec);
				System.out.println("Behandlung = "+behandlungen[i]);
				abrObject = RezTools.getKurzformUndIDFromPos(behandlungen[i].trim(), preisgruppe, preisvec);
				abrfall[i] = abrObject[0].toString();
				id[i] = abrObject[1].toString();
				//System.out.println(""+i+". Behandlung aus Terminblatt = "+abrfall[i]);
				System.out.println(""+i+". id = "+id[i]);
			}
		}else{
			for(int i = 0; i < anzahlbehandlungen;i++){
				abrfall[i] = RezTools.getKurzformFromID(
						vec_rez.get(0).get(i+8),preisvec).toString();
				id[i]= vec_rez.get(0).get(i+8).toString();
				
				//System.out.println(""+i+". Behandlung aus RezeptTabelle = "+abrfall[i]);
			}
		}
		System.out.println("anzahl Behandlungen =*************************"+anzahlbehandlungen);
		for(int i = 0; i < anzahlbehandlungen;i++){
			if(! abrfall[i].trim().equals("")){
				vecdummy.clear();
				vecdummy.add(datum);
				vecdummy.add(abrfall[i]);
				vecdummy.add(Double.valueOf("1.00"));
				//Preisuntersuchen ob alt oder neu....
				
				//.....
				vecdummy.add(Double.valueOf(RezTools.getPreisAktFromID(id[i], preisgruppe, preisvec).replace(",", ".")));
				//untersuchen ob befreit
				//.....
				vecdummy.add(Boolean.valueOf(true));

				vecdummy.add(Double.valueOf(rechneRezGeb(vecdummy.get(3).toString()).replace(",", ".")) );
				
				vecdummy.add((String) "-" );
				vecdummy.add((String) "aktuell" );
				vecdummy.add((String) DatFunk.sDatInSQL(datum) );
				vecdummy.add((String) id[i]);
				vecdummy.add(Boolean.valueOf(false));

				vec_tabelle.add((Vector<Object>)vecdummy.clone());
				System.out.println(vecdummy);
			}
		}
	}
	private AbrFall holeAbrFall(int zeile){
		AbrFall abrFall = null;
		int  rootAnzahl;
		int  kindAnzahl;
		JXTTreeTableNode rootNode;
		JXTTreeTableNode childNode;
		rootAnzahl =  root.getChildCount();
		if(rootAnzahl<=0){return abrFall;}
		int geprueft = 0;
		for(int i = 0; i < rootAnzahl;i++){
			
			rootNode = (JXTTreeTableNode) root.getChildAt(i);
			//System.out.println("Anzahl Root-Knoten = "+rootAnzahl);
			if(rootNode.isLeaf() ){
				if(geprueft == zeile){
					return rootNode.abr;	
				}else{
					geprueft++;
					continue;
				}
				
			}else if((!rootNode.isLeaf()) && ((geprueft==zeile))){
				return rootNode.abr;
			}else if(!rootNode.isLeaf()){
				kindAnzahl = rootNode.getChildCount();
				//System.out.println("Anzahl Kind-Knoten = "+kindAnzahl);
				geprueft++;
				for(int i2 = 0; i2 < kindAnzahl;i2++){
					
					if(geprueft==zeile){
						childNode = (JXTTreeTableNode) rootNode.getChildAt(i2);
						//System.out.println("Zeile gefunden geprueft wurden "+geprueft);
						return childNode.abr;
					}else{
						childNode = (JXTTreeTableNode) rootNode.getChildAt(i2);
						//System.out.println("Zeile überpüft="+geprueft+" - "+childNode.abr.datum+" - "+childNode.abr.bezeichnung);
						geprueft ++;						
					}

				}
			}else{
				//System.out.println("Keine Bedingung trifft zu="+geprueft);
				geprueft++;	
			}
		}
		
		return abrFall;
		
	}
	private JXTTreeTableNode holeNode(int zeile){
		
		JXTTreeTableNode node = null;
		int  rootAnzahl;
		int  kindAnzahl;
		JXTTreeTableNode rootNode;
		JXTTreeTableNode childNode;
		rootAnzahl =  root.getChildCount();
		if(rootAnzahl<=0){return node;}
		int geprueft = 0;
		for(int i = 0; i < rootAnzahl;i++){
			
			rootNode = (JXTTreeTableNode) root.getChildAt(i);
			//System.out.println("Anzahl Root-Knoten = "+rootAnzahl);
			if(rootNode.isLeaf() ){
				if(geprueft == zeile){
					return rootNode;	
				}else{
					geprueft++;
					continue;
				}
				
			}else if((!rootNode.isLeaf()) && ((geprueft==zeile))){
				return rootNode;
			}else if(!rootNode.isLeaf()){
				kindAnzahl = rootNode.getChildCount();
				//System.out.println("Anzahl Kind-Knoten = "+kindAnzahl);
				geprueft++;
				for(int i2 = 0; i2 < kindAnzahl;i2++){
					
					if(geprueft==zeile){
						childNode = (JXTTreeTableNode) rootNode.getChildAt(i2);
						//System.out.println("Zeile gefunden geprueft wurden "+geprueft);
						return childNode;
					}else{
						childNode = (JXTTreeTableNode) rootNode.getChildAt(i2);
						//System.out.println("Zeile überpüft="+geprueft+" - "+childNode.abr.datum+" - "+childNode.abr.bezeichnung);
						geprueft ++;						
					}

				}
			}else{
				//System.out.println("Keine Bedingung trifft zu="+geprueft);
				geprueft++;	
			}
		}
		
		return node;
		
	}
	private int getNodeCount(){
		int ret = 0; 

		int  rootAnzahl;
		int  kindAnzahl;
		JXTTreeTableNode rootNode;
		JXTTreeTableNode childNode;
		rootAnzahl =  root.getChildCount();
		if(rootAnzahl<=0){return 0;}
		int geprueft = 0;
		for(int i = 0; i < rootAnzahl;i++){
			rootNode = (JXTTreeTableNode) root.getChildAt(i);
			ret += 1;
			if( (kindAnzahl = rootNode.getChildCount())>0){
				ret+=kindAnzahl;
			}
		}
		return ret;
	}
	
	/**************************/
	private String rechneRezGeb(String preis){
		BigDecimal bi_rezgeb = BigDecimal.valueOf(Double.parseDouble(preis.replace(",", ".")));
		bi_rezgeb = bi_rezgeb.divide(BigDecimal.valueOf(Double.parseDouble("10.000")));
		bi_rezgeb  = bi_rezgeb.setScale(2, BigDecimal.ROUND_HALF_UP);
		return	dfx.format(bi_rezgeb).replace(".", ",");
	}
	private Double rechneRezGebFromDouble(Double preis){
		BigDecimal bi_rezgeb = BigDecimal.valueOf(preis);
		bi_rezgeb = bi_rezgeb.divide(BigDecimal.valueOf(Double.parseDouble("10.000")));
		bi_rezgeb  = bi_rezgeb.setScale(2, BigDecimal.ROUND_HALF_UP);
		return	bi_rezgeb.doubleValue();
	}
	
	/*****************************************************************************************/

	private void doFuelleTreeTable(){
		AbrFall abr;
		if(vec_tabelle.size()<=0){return;}
		String testdatum = "";
		JXTTreeTableNode knoten = null;
		int tag = 0;
		for(int i = 0; i < vec_tabelle.size();i++){
			abr = new AbrFall(Integer.toString(tag)+".Tag",
					(String)vec_tabelle.get(i).get(0),
					(String)vec_tabelle.get(i).get(1),
					(Double)vec_tabelle.get(i).get(2),
					(Double)vec_tabelle.get(i).get(3),
					(Boolean)vec_tabelle.get(i).get(4),
					(Double)vec_tabelle.get(i).get(5),
					(String)vec_tabelle.get(i).get(6),
					(String)vec_tabelle.get(i).get(7),
					(String)vec_tabelle.get(i).get(8),
					(String)vec_tabelle.get(i).get(9),
					(Boolean)vec_tabelle.get(i).get(10));
			if(!testdatum.trim().equals(abr.datum.trim())){
				tag++;
				abr.unterbrechung = vectage.get(tag-1).get(2);
				abr.titel= Integer.toString(tag)+".Tag";
				knoten = new JXTTreeTableNode(abr.datum,abr,true);
				demoTreeTableModel.insertNodeInto(knoten, root, root.getChildCount());
				testdatum = new String(abr.datum);
				//System.out.println(testdatum);
				continue;
				
			}else{
				abr.unterbrechung = vectage.get(tag-1).get(2);
				foo = new JXTTreeTableNode("",abr,true);
				demoTreeTableModel.insertNodeInto(foo, knoten, knoten.getChildCount());
				testdatum = new String(abr.datum);
				//System.out.println("In einzelner Funktion "+testdatum);
				continue;
			}
		}
	}
	class AbrFall{
		public String titel;
		public String datum;
		public String bezeichnung;
		public double anzahl = 0.0;
		public double preis = 0.00;
		public boolean zuzahlung = true;
		public double rezgeb = 0.00;
		public String unterbrechung ="";
		public String alterpreis ="";
		public String sqldatum = "";
		public String preisid = "";
		public boolean niezuzahl = false;
		//SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		public AbrFall(String titel,String datum,String bezeichnung,Double anzahl,Double preis,
				boolean zuzahlung,double rezgeb,String unterbrechung,String alterpreis,String sqldatum,String preisid,boolean niezuzahl){
			this.titel = titel;
			this.datum = datum;
			this.bezeichnung = bezeichnung; 
			this.anzahl = anzahl;
			this.preis = preis;
			this.zuzahlung = zuzahlung;
			this.rezgeb = rezgeb;
			this.unterbrechung = unterbrechung;
			this.sqldatum = sqldatum;
			this.alterpreis = alterpreis;
			this.preisid = preisid;
			this.niezuzahl = niezuzahl;
		}
	}
	
	private void parseHTML(String rez_nr){
		if(rez_nr==null){
			return;
		}

		String dummy="";
		String text = 
		"<html><head>"+
		"<STYLE TYPE=\"text/css\">"+
		"<!--"+
		"A{text-decoration:none;background-color:transparent;border:none}"+
		"TD{font-family: Arial; font-size: 12pt; padding-left:5px;padding-right:30px}"+
		".spalte1{color:#0000FF;}"+
		".spalte2{color:#333333;}"+
		".spalte2{color:#333333;}"+
		"--->"+
		"</STYLE>"+
		"</head>"+
		"<div style=margin-left:30px;>"+
		"<font face=\"Tahoma\"><style=margin-left=30px;>"+
		"<br>"+
		"<table>"+
		/*****Rezept****/
		/*******/
		"<tr>"+
		"<th rowspan=\"4\"><a href=\"http://rezedit.de\"><img src='file:///"+Reha.proghome+"icons/Rezept.png' border=0></a></th>" +
		"<td class=\"spalte1\" align=\"right\">"+
		"Ausstellungsdatum"+
		"</td><td class=\"spalte2\" align=\"left\">"+
		DatFunk.sDatInDeutsch(vec_rez.get(0).get(2))+
		"</td>"+
		"</tr>"+
		/*******/
		"<tr>"+
		"<td class=\"spalte1\" align=\"right\">"+
		"Verordnungsart"+
		"</td><td class=\"spalte2\" align=\"left\">"+
		voArt[Integer.parseInt(vec_rez.get(0).get(27))]+
		"</td>"+
		"</tr>"+
		/*******/
		"<tr>"+
		"<td class=\"spalte1\" align=\"right\">"+
		"Indikationsschlüssel"+
		"</td><td class=\"spalte2\" align=\"left\">"+
		(vec_rez.get(0).get(44).startsWith("kein Indi") ? "<b><font color=#FF0000>"+vec_rez.get(0).get(44)+"</font></b>" : vec_rez.get(0).get(44))+
		"</td>"+
		"</tr>"+
		/*******/
		"<tr>"+
		"<td class=\"spalte1\" align=\"right\">"+
		"Hausbesuch"+
		"</td><td class=\"spalte2\" align=\"left\">"+
		(vec_rez.get(0).get(43).equals("T") ? "JA" : "NEIN")+
		"</td>"+
		"</tr>"+
		/*******/
		"<tr>"+
		"<td>&nbsp;"+
		"</td>"+
		"</tr>"+
		/********Patient********/
		"<tr>"+
		"<th rowspan=\"5\" valign=\"top\"><a href=\"http://patedit.de\"><img src='file:///"+Reha.proghome+"icons/kontact_contacts.png' width=52 height=52 border=0></a></th>" +
		"<td class=\"spalte1\" align=\"right\">"+
		"Patient"+
		"</td><td class=\"spalte2\" align=\"left\">"+
		StringTools.EGross(vec_pat.get(0).get(0))+", "+
		StringTools.EGross(vec_pat.get(0).get(1))+", geb.am "+DatFunk.sDatInDeutsch(vec_pat.get(0).get(2))+
		"</td>"+
		"</tr>"+
		/*******/
		"<tr>"+
		"<td class=\"spalte1\" align=\"right\">"+
		"Adresse"+
		"</td><td class=\"spalte2\" align=\"left\">"+
		StringTools.EGross(vec_pat.get(0).get(3))+", "+
		vec_pat.get(0).get(4)+" "+
		StringTools.EGross(vec_pat.get(0).get(5))+
		"</td>"+
		"</tr>"+
		/*******/
		"<tr>"+
		"<td class=\"spalte1\" align=\"right\">"+
		"Versicherten-Status"+
		"</td><td class=\"spalte2\" align=\"left\">"+
		vec_pat.get(0).get(7)+
		"</td>"+
		"</tr>"+
		/*******/
		"<tr>"+
		"<td class=\"spalte1\" align=\"right\">"+
		"Mitgliedsnummer"+
		"</td><td class=\"spalte2\" align=\"left\">"+
		vec_pat.get(0).get(6)+
		"</td>"+
		"</tr>"+
		/*******/
		"<tr>"+
		"<td class=\"spalte1\" align=\"right\">"+
		"Zuzahlungs-Status"+
		"</td><td class=\"spalte2\" id=\"zzpflicht\" align=\"left\">"+
		(zuZahlungsIndex.equals("Zuzahlungspflichtig") ? "" : "<b><font color=#FF0000>")+
		zuZahlungsIndex+
		(zuZahlungsIndex.equals("Zuzahlungspflichtig") ? "" : "</font></b>")+
		"</td>"+
		"</tr>"+
		/*******/
		"<tr>"+
		"<td>&nbsp;"+
		"</td>"+
		"</tr>"+
		/********Arzt********/
		"<tr>"+
		"<th rowspan=\"3\" valign=\"top\"><a href=\"http://arztedit.de\"><img src='file:///"+Reha.proghome+"icons/system-users.png' width=52 height=52 border=0></a></th>" +
		"<td class=\"spalte1\" align=\"right\">"+
		"verordnender Arzt"+
		"</td><td class=\"spalte2\" align=\"left\">"+
		StringTools.EGross(vec_pat.get(0).get(13))+
		"</td>"+
		"</tr>"+
		/*******/
		"<tr>"+
		"<td class=\"spalte1\" align=\"right\">"+
		"Betriebsstätte"+
		"</td><td class=\"spalte2\" align=\"left\">"+
		(vec_pat.get(0).get(14).trim().equals("") ? "999999999" : vec_pat.get(0).get(14).trim())+
		"</td>"+
		"</tr>"+
		/*******/
		"<tr>"+
		"<td class=\"spalte1\" align=\"right\">"+
		"LANR"+
		"</td><td class=\"spalte2\" align=\"left\">"+
		(vec_pat.get(0).get(15).trim().equals("") ? "999999999" : vec_pat.get(0).get(15).trim())+
		"</td>"+
		"</tr>"+
		/*******/
		"<tr>"+
		"<td>&nbsp;</td>"+
		"<td class=\"spalte1\" align=\"right\">"+
		"Rezeptwert"+
		"</td><td class=\"spalte2\" align=\"left\">"+
		"<b>"+dfx.format(rezeptWert)+"</b>"+
		"</td>"+
		"</tr>"+
		"<tr>"+
		"<td>&nbsp;</td>"+
		"<td class=\"spalte1\" align=\"right\">"+
		"Zuzahlung"+
		"</td><td class=\"spalte2\" align=\"left\">"+
		(zuzahlungWert > 0 && vec_rez.get(0).get(14).equals("F") ? getNoZuZahl() : "<b>"+dfx.format(zuzahlungWert)+"</b>")+
		"</td>"+
		"</tr>"+
		/*******/
		getHTMLPositionen()+

//Double rezeptWert;
//Double zuzahlungWert;
		"</table>"+
		"</font>"+
		"</div>"+
		"</html>";
		this.htmlPane.setText(text);
		((JScrollPane)this.htmlPane.getParent().getParent()).validate();
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				JViewport vp = ((JScrollPane)htmlPane.getParent().getParent()).getViewport();
				vp.setViewPosition(new Point(0,0));
				((JScrollPane)htmlPane.getParent().getParent()).validate();
			}
		});
	}
	private String getNoZuZahl(){
		return "<b><font color=#FF0000><a href=\"http://nozz.de\">"+dfx.format(zuzahlungWert)+"</a></font></b>";
	}
	private String getHTMLPositionen(){
		
		htmlposbuf.setLength(0);
		//vec_poskuerzel.clear();
		//vec_posanzahl.clear();
		for(int i = 0; i < vec_poskuerzel.size();i++){
			htmlposbuf.append("<tr>"+
		"<td>&nbsp;</td>"+
		"<td class=\"spalte1\" align=\"right\">");
			htmlposbuf.append(vec_poskuerzel.get(i));
			htmlposbuf.append("</td><td class=\"spalte2\" align=\"left\">");
			htmlposbuf.append(Integer.toString(vec_posanzahl.get(i))+" x");
			htmlposbuf.append("</td>"+
					"</tr>");
		}
		return htmlposbuf.toString();
	}
	@Override
	public void hyperlinkUpdate(HyperlinkEvent event) {
	    if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
	    	System.out.println(event.getURL().toString());
	    	if(event.getURL().toString().contains("rezedit")){
	    		
	    	}
	    	if(event.getURL().toString().contains("patedit")){
	    		SucheNachAllem.doPatSuchen(vec_rez.get(0).get(0).trim(), vec_rez.get(0).get(1),this);
	    	}

	      }
	}
	
/*************************
 * 
 * 
 * 
 * 
 * 	
 */
	class AbrechnungListSelectionHandler implements ListSelectionListener {

	    public void valueChanged(ListSelectionEvent e) {
	        ListSelectionModel lsm = (ListSelectionModel)e.getSource();
	        
	        int firstIndex = e.getFirstIndex();
	        int lastIndex = e.getLastIndex();
	        boolean isAdjusting = e.getValueIsAdjusting();
	        if(isAdjusting){
	        	//System.out.println("isAdjusting-Return bei RowIndex="+firstIndex);
	        	//System.out.println("Reihe="+tageTbl.getSelectedRow());
	        	return;
	        }
			//StringBuffer output = new StringBuffer();
	        if (lsm.isSelectionEmpty()) {

	        } else {
	        	//System.out.println(lsm);
	            int minIndex = lsm.getMinSelectionIndex();
	            int maxIndex = lsm.getMaxSelectionIndex();
	            for (int i = minIndex; i <= maxIndex; i++) {
	                if (lsm.isSelectedIndex(i)) {
	                	final int ix = i;
	                	new SwingWorker<Void,Void>(){
							@Override
							protected Void doInBackground() throws Exception {
								int row = jXTreeTable.getSelectedRow();
								int col = jXTreeTable.getSelectedColumn();
								//System.out.println(jXTreeTable.getValueAt(row, col));
								//demoTreeTableModel
								//JXTTreeTableNode
								//System.out.println("ChildCount = "+demoTreeTableModel.getRoot().getChildCount());
								//JXTTreeTableNode node = jXTreeTable.i
								//System.out.println("Gewählt wurde Reihe="+jXTreeTable.getSelectedRow());
								//System.out.println("Gewählt wurde Spalte="+ jXTreeTable.getSelectedColumn());
								for(int i = 0; i < root.getChildCount();i++ ){
									JXTTreeTableNode node = (JXTTreeTableNode) root.getChildAt(i);
									//System.out.println("Bezeichnung = "+i+".0 "+node.abr.bezeichnung);
									int childs = node.getChildCount();
									//System.out.println("Kinder von root="+childs);
									//System.out.println("Hauptknoten="+root.getIndex(node));
									for(int i2 = 0; i2 < childs; i2++){
										JXTTreeTableNode node2 = (JXTTreeTableNode) node.getChildAt(i2);
										//System.out.println("Bezeichnung = "+i+"."+(i2+1)+node2.abr.bezeichnung);
										//System.out.println("Kindknoten="+node.getIndex(node2));
									}
								}
								
								return null;
							}
	                		
	                	}.execute();
	                    break;
	                }
	            }
	        }
	    }
	}
	
	class AbrechnungTreeSelectionListener implements TreeSelectionListener {
		boolean isUpdating = false;
		
		public void valueChanged(TreeSelectionEvent e) {
			if (!isUpdating) {
				isUpdating = true;
				JXTreeTable tt = jXTreeTable;//(JXTreeTable) e.getSource();
				TreeTableModel ttmodel = tt.getTreeTableModel();
				TreePath[] selpaths = tt.getTreeSelectionModel().getSelectionPaths();
				
				if (selpaths !=null) {
					ArrayList<TreePath> selPathList = new ArrayList<TreePath>(Arrays.asList(selpaths));
					int i=1;
					while(i<=selPathList.size()) {
						//add all kiddies.
						TreePath currPath = selPathList.get(i-1);
						Object currentObj = currPath.getLastPathComponent();
						int childCnt = ttmodel.getChildCount(currentObj);
						for(int j=0;j<childCnt; j++) {
							Object child = ttmodel.getChild(currentObj, j);
							TreePath nuPath = currPath.pathByAddingChild(child);
							if(!selPathList.contains(nuPath)) {
								selPathList.add(nuPath);
							}
						}
						i++;
					}
					selpaths = selPathList.toArray(new TreePath[0]);

					tt.getTreeSelectionModel().setSelectionPaths(selpaths);
					
					TreePath tp = tt.getTreeSelectionModel().getSelectionPath();
					aktNode =  (JXTTreeTableNode) tp.getLastPathComponent();//selpaths[selpaths.length-1].getLastPathComponent();
					new SwingWorker<Void,Void>(){
						protected Void doInBackground() throws Exception {
							int lang = getNodeCount();
							aktRow = -1;
							for(int i = 0; i < lang; i++){
								if(aktNode == holeNode(i)){
									aktRow = i;
									System.out.println("Zeilennummer =  = "+i);
									System.out.println("Node selektiert = "+aktNode.abr.bezeichnung);
									System.out.println("Behandlungsdatum selektiert = "+aktNode.abr.datum+" / "+aktNode.abr.bezeichnung);
									break;
								}
							}
							return null;
						}
						
					}.execute();
				}
			}
			isUpdating = false;
		}
		
		
	 
	}
	
/*************************
 * 
 * 	
 */
	private class TageTreeTableModel extends DefaultTreeTableModel {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		DecimalFormat dfx = new DecimalFormat( "0.00" );
		
        public TageTreeTableModel(JXTTreeTableNode jXTTreeTableNode) {
            super(jXTTreeTableNode);
        }
        
     
        public Object getValueAt(Object node, int column) {
        	JXTTreeTableNode jXTreeTableNode = (JXTTreeTableNode) node;

        	AbrFall o = null;
        	
            try{
            	o =  (AbrFall) jXTreeTableNode.getUserObject();
            }catch(Exception ex){
            	return super.getValueAt(node, column);
            }

 
            switch (column) {
            	case 0:
            		return o.titel;
            	case 1:
	                return o.datum;
	            case 2:
	                return o.bezeichnung;
	            case 3:
	                return o.anzahl;
	            case 4:
	                return o.preis;
	            case 5:
	            	return o.zuzahlung;
	            case 6:
	                return dfx.format(o.rezgeb);
	            case 7:
	            	return o.unterbrechung;
	            case 8:
	            	return o.alterpreis;
	            case 9:
	            	return o.sqldatum;

	            	
            }
            return super.getValueAt(node, column);
        }
        
        public void setValueAt(Object value, Object node, int column){
        	JXTTreeTableNode jXTreeTableNode = (JXTTreeTableNode) node;
        	AbrFall o;
        	try{
            	o =  (AbrFall) jXTreeTableNode.getUserObject();
            }catch(Exception ex){
            	return;
            } 
            switch (column) {
            case 0:
				o.titel =((String) value) ;
				break;
            case 1:
					o.datum =((String) value) ;
					o.sqldatum = DatFunk.sDatInSQL(((String) value));
            	break;
            case 2:
            	o.bezeichnung = ((String) value);
            	break;
            case 3:
            	o.anzahl = ((Double)value);
            	break;
            case 4:
            	o.preis =  Double.parseDouble(dfx.format( ((Double)value) ).replaceAll(",", ".") ) ;
            	o.rezgeb = (((Boolean)o.zuzahlung) ? rechneRezGebFromDouble(o.preis) : (Double) 0.00);
            	break;
            case 5:
            	System.out.println("in SetValue Zuzahlung="+value);
            	o.zuzahlung = ((Boolean)value);
            	o.rezgeb = (((Boolean)value) ? rechneRezGebFromDouble(o.preis) : (Double) 0.00);
            	break;
            case 6:
            	o.rezgeb = ((Double)value);
            	break;
            case 7:
            	o.unterbrechung = ((String)value);
            	break;
            case 8:
            	o.alterpreis = ((String)value);
            	break;
            case 9:
            	o.sqldatum = ((String)value);
            	break;
            case 10:
            	o.niezuzahl = ((Boolean)value);
            	break;
	
            }
        }
 
        public boolean isCellEditable(java.lang.Object node,int column){
            switch (column) {
            case 0:
                return false;
            case 1:
                return false;
            case 2:
                return true;
            case 3:
                return true;
            case 4:
                return false;   
            case 5:
                return true;
            case 6:
                return false;
            case 7:
                return true;
            case 8:
                return true;                
                
            default:
                return false;
            }
        }
        
        public Class<?> getColumnClass(int column) {
            switch (column) {
            case 0:
                return String.class;
            case 1:
                return String.class;
            case 2:
                return String.class;
            case 3:
                return Double.class;
            case 4:
                return Double.class;
            case 5:
                return Boolean.class;
            case 6:
                return Double.class;
            case 7:
                return String.class;
            case 8:
                return String.class;
            case 9:
                return String.class;
            default:
                return Object.class;
            }
        }
 
        public int getColumnCount() {
            return 11;
        }
 
 
        public String getColumnName(int column) {
            switch (column) {
            case 0:
                return "Abr.Fall";
            case 1:
                return "Behandlungstag";
            case 2:
                return "Heilmittel";
            case 3:
                return "Anzahl";
            case 4:
                return "Preis";
            case 5:
                return "Zuzahlung";
            case 6:
                return "Rez.Gebühr";
            case 7:
                return "Unterbrech.";
            case 8:
                return "Akt.Tarif";
            case 9:
                return "sqldatum";                
            default:
                return "Column " + (column + 1);
            }
        }
    }
 
    private static class JXTTreeTableNode extends DefaultMutableTreeTableNode {
    	private boolean enabled = false;
    	private AbrFall abr = null;
    	public JXTTreeTableNode(String name,AbrFall abr ,boolean enabled){
    		super(name);
    		this.enabled = enabled;
   			this.abr = abr;
   			if(abr != null){
   				this.setUserObject(abr);
   			}

    	}
 
		public boolean isEnabled() {
			return enabled;
		}
		
		public AbrFall getObject(){
			return abr;
		}
    }
	@Override
	public void actionPerformed(ActionEvent arg0) {

		String cmd = arg0.getActionCommand();
		if(cmd.equals("monthViewCommit")){
			String tagNeu = sdf.format(  ((JXMonthView)arg0.getSource()).getSelectionDate() );
			ListenerTools.removeListeners(dlg.getContentPane());
			dlg.setVisible(false);
			dlg.dispose();
			dlg = null;
			if(tagNeu!=null){
				doTagNeu2(tagNeu);
				aktualisiereTree();
			}

		}
		if(cmd.equals("kuerzel")){
			if(vec_rez==null){return;}
			final ActionEvent arg0X = arg0;
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					if(aktRow < 0 || vec_rez.size()<=0){return;}
					setEinzelPreis((String)((JRtaComboBox)arg0X.getSource()).getValueAt(0),(String)((JRtaComboBox)arg0X.getSource()).getValueAt(1));
					getVectorFromNodes();					
					doTreeRezeptWertermitteln();
					doPositionenErmitteln();
					parseHTML(vec_rez.get(0).get(1).trim());
				}
			});
			return;
		}
		if(cmd.equals("break")){
			final ActionEvent arg0X = arg0;
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					if(aktRow < 0){return;}
					doBreak((String) ((JRtaComboBox)arg0X.getSource()).getSelectedItem());
				}
			});
			return;
		}
		if(cmd.equals("akttarif")){
			final ActionEvent arg0X = arg0;
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					if(aktRow < 0){return;}
					doAkttarif((String) ((JRtaComboBox)arg0X.getSource()).getSelectedItem());
					doTreeRezeptWertermitteln();
					parseHTML(vec_rez.get(0).get(1).trim());

				}
			});
			return;
		}

		if(cmd.equals("zuzahlung")){
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					System.out.println("Wert der CheckBox = "+((JRtaCheckBox)mycheck.getComponent()).isSelected()+ " Selektierte Tabellenzeile = ");
				}
			});

		}
		if(cmd.equals("expandall")){
			jXTreeTable.expandAll();
		}
		if(cmd.equals("collapsall")){
			jXTreeTable.collapseAll();
		}
		if(cmd.equals("tagneu")){
			doShowView();
			
		}
		if(cmd.equals("behandlungneu")){
			doBehandlungNeu();
			aktualisiereTree();
		}
		if(cmd.equals("behandlungloeschen")){
			doBehandlungLoeschen();
			aktualisiereTree();
		}
	}
	private void doBreak(String unterbrechung){
		aktNode.abr.unterbrechung = unterbrechung;
	}
	private void doAkttarif(String tarif){
		try{
			if(tarif.equals("alt")){
				aktNode.abr.preis = Double.valueOf(RezTools.getPreisAltFromID(aktNode.abr.preisid, preisgruppe, preisvec).replace(",", "."));
			}else{
				aktNode.abr.preis =  Double.valueOf(RezTools.getPreisAktFromID(aktNode.abr.preisid, preisgruppe, preisvec).replace(",", "."));
			}
			if(aktNode.abr.zuzahlung){
				aktNode.abr.rezgeb = rechneRezGebFromDouble(aktNode.abr.preis);
			}
		}catch(Exception ex){
			if(aktNode != null){
				demoTreeTableModel.setValueAt(Double.valueOf("0.00"), aktNode, 4);
			}
		}
		jXTreeTable.repaint();
	}
	private void setEinzelPreis(String bez,String id){
		try{
			if(aktNode.abr.alterpreis.equals("alt")){
				aktNode.abr.preis = Double.valueOf(RezTools.getPreisAltFromID(id, preisgruppe, preisvec).replace(",", "."));
				aktNode.abr.preisid = id;
				aktNode.abr.bezeichnung = bez;
			}else{
				aktNode.abr.preis = Double.valueOf(RezTools.getPreisAktFromID(id, preisgruppe, preisvec).replace(",", "."));
				aktNode.abr.preisid = id;
				aktNode.abr.bezeichnung = bez;
			}
			if(aktNode.abr.zuzahlung){
				aktNode.abr.rezgeb = rechneRezGebFromDouble(aktNode.abr.preis);
			}
			//Double preis =  Double.valueOf(RezTools.getPreisAktFromID(id, preisgruppe, preisvec).replace(",", "."));
			//demoTreeTableModel.setValueAt(preis, aktNode, 4);
		}catch(Exception ex){
			if(aktNode != null){
				demoTreeTableModel.setValueAt(Double.valueOf("0.00"), aktNode, 4);
			}
		}
		jXTreeTable.repaint();
	}
	
	
	private void doShowView(){
		dlg = new JDialog();
		dlg.setModal(true);
		dlg.setPreferredSize(new Dimension(200,200));
		dlg.setUndecorated(true);
		dlg.setLocation(popUpX, popUpY);
		final JDialog fdiag = dlg;
		dlg.setContentPane(showView());
		dlg.getContentPane().addMouseMotionListener(new MouseMotionListener(){
			@Override
			public void mouseDragged(MouseEvent arg0) {
				int x = arg0.getX();
				int y = arg0.getY();
				if( x<=2 || y<=2 || x>= (dlg.getWidth()-2) || y>=(dlg.getHeight()-2)){
					ListenerTools.removeListeners(fdiag.getContentPane());
					fdiag.setVisible(false);
					fdiag.dispose();
				}
			}
			@Override
			public void mouseMoved(MouseEvent arg0) {
				int x = arg0.getX();
				int y = arg0.getY();
				if( x<=2 || y<=2 || x>= (dlg.getWidth()-2) || y>=(dlg.getHeight()-2)){
					ListenerTools.removeListeners(fdiag.getContentPane());
					fdiag.setVisible(false);
					fdiag.dispose();
				}
			}
			
		});
		dlg.pack();
		dlg.setVisible(true);
	}
	private void doTagNeu2(String tag){
		// erst testen ob es dieses Datum schon gibt
		String neudatum = DatFunk.sDatInSQL(tag);
		JXTTreeTableNode node;
		int count = root.getChildCount();
		int einfuegenbei = count;
		if(count==0){
			demoTreeTableModel.insertNodeInto(macheTag(tag,einfuegenbei), root, 0);
			if(hausbesuch){
				doHausbesuchEinzeln((JXTTreeTableNode) root.getChildAt(einfuegenbei), 0);
			}
			return;
		}
		for(int i = 0; i<count;i++){
			node = (JXTTreeTableNode) root.getChildAt(i);
			
			if( ((JXTTreeTableNode)root.getChildAt(i)).abr.datum.equals(tag) ){
				JOptionPane.showMessageDialog(null, "Dieser Behandlungstag existiert bereits");
				return;
				
			}else if( DatFunk.TageDifferenz(((JXTTreeTableNode)root.getChildAt(i)).abr.datum, tag) < 0 ){
				einfuegenbei = i;
				break;
				
			}
			System.out.println( DatFunk.TageDifferenz(((JXTTreeTableNode)root.getChildAt(i)).abr.datum, tag) );
			
		}
		System.out.println( "******>Neuer Tag wird eingefügt bei Index = "+einfuegenbei);
		
		if(einfuegenbei == count){
			demoTreeTableModel.insertNodeInto(macheTag(tag,einfuegenbei), root, count);
			if(hausbesuch){
				doHausbesuchEinzeln((JXTTreeTableNode) root.getChildAt(einfuegenbei), count);
			}
		}else{
			demoTreeTableModel.insertNodeInto(macheTag(tag,einfuegenbei), root, einfuegenbei);
			if(hausbesuch){
				doHausbesuchEinzeln((JXTTreeTableNode) root.getChildAt(einfuegenbei), count);
			}
		}
	}
/*************************************************************************************************************/
	
	private void aktualisiereTree(){
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				getVectorFromNodes();
				doGebuehren();
				doTreeRezeptWertermitteln();
				doTitelRepair();
				jXTreeTable.repaint();
				doPositionenErmitteln();
				parseHTML(vec_rez.get(0).get(1).trim());
				
				return null;
			}
		}.execute();
	}
	private JXTTreeTableNode macheTag(String tag,int einfuegen){
		JXTTreeTableNode node = null;
		//AbrFall fall= null;
		System.out.println(vec_rez.get(0).get(34));
		String[] tage = vec_rez.get(0).get(34).split("\n");
		int zaehler = 0;
		String alletage = ""; 
		for(int i = 0; i < root.getChildCount()+1;i++){
			if(i==einfuegen){
				alletage = alletage+tag+"@@@@"+DatFunk.sDatInSQL(tag)+"\n";
			}else{
				alletage = alletage+tage[zaehler]+"\n";
				zaehler++;
			}
		}
		System.out.println(alletage);
		vec_rez.get(0).set(34,alletage);
		/******Entscheidender Funktionsaufruf****************************/
		ermittleAbrechnungsfall(false);
		
		AbrFall abr = null;
		JXTTreeTableNode childnode = null;
		int neu = 0;
		
		for(int i = 0; i < vec_tabelle.size();i++){
			if(vec_tabelle.get(i).get(0).equals(tag)){
				if(neu==0){
					abr =constuctAbrFall(i,einfuegen+1);
					node = new JXTTreeTableNode(abr.datum,abr,true);
					neu++;
				}else{
					abr = constuctAbrFall(i,einfuegen+1);
					childnode = new JXTTreeTableNode(abr.datum,abr,true);
					node.add(childnode);
				}
			}
		}
		
		System.out.println(node);
		
		return node;
	}
	private AbrFall constuctAbrFall(int vecindex, int tag){
		AbrFall abr = new AbrFall(Integer.toString(tag)+".Tag",
				(String)vec_tabelle.get(vecindex).get(0),
				(String)vec_tabelle.get(vecindex).get(1),
				(Double)vec_tabelle.get(vecindex).get(2),
				(Double)vec_tabelle.get(vecindex).get(3),
				(Boolean)vec_tabelle.get(vecindex).get(4),
				(Double)vec_tabelle.get(vecindex).get(5),
				(String)vec_tabelle.get(vecindex).get(6),
				(String)vec_tabelle.get(vecindex).get(7),
				(String)vec_tabelle.get(vecindex).get(8),
				(String)vec_tabelle.get(vecindex).get(9),
				(Boolean)vec_tabelle.get(vecindex).get(10));
		return abr;
	}
	private JXTTreeTableNode constructNewBehandlung(JXTTreeTableNode node){
		JXTTreeTableNode xnode = null;
		AbrFall abr = null;
		Vector<Object> tagdummy = new Vector<Object>();
		for(int i = 0; i < vec_tabelle.get(0).size();i++){
			tagdummy.add(vec_tabelle.get(aktRow).get(i));
		}
		vec_tabelle.insertElementAt((Vector<Object>)tagdummy.clone(),aktRow);
		abr = constuctAbrFall(aktRow,-1);
		xnode = new JXTTreeTableNode(abr.datum,abr,true);
		JXTTreeTableNode ynode = (JXTTreeTableNode) getBasicNodeFromChild(node);
		demoTreeTableModel.insertNodeInto(xnode,ynode, ynode.getChildCount());
		return xnode;
	}
	private void doTitelRepair(){
		int count = root.getChildCount();
		int nodes ;
		int xtag = 1;
		String datum=null,dummydat=null;
		for(int i = 0; i < count;i++){
			if(i==0){
				datum = ((JXTTreeTableNode)root.getChildAt(i)).abr.datum;
				dummydat = datum;
			}
			if(! datum.equals(dummydat)){
				datum = ((JXTTreeTableNode)root.getChildAt(i)).abr.datum;
				dummydat = datum;
			}
			((JXTTreeTableNode)root.getChildAt(i)).abr.titel = Integer.toString(xtag)+".Tag";
			nodes = ((JXTTreeTableNode)root.getChildAt(i)).getChildCount();
			for(int i2 = 0; i2 < nodes;i2++){
				((JXTTreeTableNode)((JXTTreeTableNode)root.getChildAt(i)).getChildAt(i2)).abr.titel = Integer.toString(xtag)+".Tag"; 
			}
			xtag++;

		}
		jXTreeTable.repaint();		
	}
	private void doBehandlungNeu(){
		if(aktRow < 0){
			JOptionPane.showMessageDialog(null, "Kein Behandlungstag ausgewählt für zusätzliches Heilmittel");
			return;
		}
		if(root.getChildCount()==0){
			JOptionPane.showMessageDialog(null, "Es existiert kein Behandlungstag!\nWie bitteschön wollen Sie einem  nicht existierenden Behandlungstag\nein ergänzendes Heilmittel hinzufügen??");
			return;
		}
		//TreeTableNode[] nodes = demoTreeTableModel.getPathToRoot(aktNode);
		constructNewBehandlung(aktNode);
		doGebuehren();
		doTreeRezeptWertermitteln();
	}
	
	/**************************************************************/
	
	private void doBehandlungLoeschen(){
		if(getNodeCount() == 0 || holeAbrFall(aktRow)==null){return;}
		try{
			TreeTableNode[] nodes = demoTreeTableModel.getPathToRoot(aktNode);
			String behandlung = aktNode.abr.bezeichnung;
			String datum = aktNode.abr.datum;
			String text = "";
			if(nodes.length==3){
				text = "Diese Behandlung wirklich löschen???\n\nBehandlung = "+behandlung+"\nDatum = "+datum+"\n"; 
			}else{
				text = "Sie löschen einen kompletten Behandlungstag(!!)\n\nBehandlungstag = "+datum+"\n";
			}

			int anfrage = JOptionPane.showConfirmDialog(null,text ,"Achtung wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
			if(anfrage == JOptionPane.YES_OPTION){
				demoTreeTableModel.removeNodeFromParent(aktNode);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	private void loescheTree(){
		int childs;
		while( (childs=root.getChildCount()) > 0){
			demoTreeTableModel.removeNodeFromParent((MutableTreeTableNode) root.getChildAt(0));
		}
	}
	private void getVectorFromNodes(){
		int lang = this.getNodeCount();
		
		//System.out.println("NodeCount nach löschen == "+this.getNodeCount());
		vec_tabelle.clear();
		AbrFall abr = null;
		
		for(int i = 0;i<lang;i++){
			vecdummy.clear();
			abr = this.holeAbrFall(i); 
			
			vecdummy.add(abr.datum);
			vecdummy.add(abr.bezeichnung);
			vecdummy.add(abr.anzahl);
			vecdummy.add(abr.preis);
			vecdummy.add((abr.niezuzahl ? false : abr.zuzahlung));
			vecdummy.add(abr.rezgeb);
			vecdummy.add(abr.unterbrechung);
			vecdummy.add(abr.alterpreis);
			vecdummy.add(abr.sqldatum);
			vecdummy.add(abr.preisid);
			vecdummy.add(abr.niezuzahl);
			vec_tabelle.add((Vector<Object>)vecdummy.clone());
		}
		sortiereVector(vec_tabelle,0);
		
	}
	private void sortiereVector(Vector vec,int dimension){
		final int xdimension = dimension;
		Comparator<Vector> comparator = new Comparator<Vector>() {
			@Override
			public int compare(Vector o1, Vector o2) {
				// TODO Auto-generated method stub
				String s1 = DatFunk.sDatInSQL( (String)o1.get(xdimension) );
				String s2 = DatFunk.sDatInSQL( (String)o2.get(xdimension) );
				return s1.compareTo(s2);
			}
		};
		Collections.sort(vec,comparator);

	}
	private TreePath getPathFromNode(TreeTableNode node){
		TreePath path = new TreePath(demoTreeTableModel.getPathToRoot(node));
		return path;
	}
	
	private TreeTableNode getNodeFromPath(TreePath path){
		TreeTableNode node = null;
		return null;
	}
	private TreeTableNode getBasicNodeFromChild(TreeTableNode node){
		TreeTableNode xnode;
		TreeTableNode ynode;
		TreeTableNode retnode = null;
		int anzahlBasics = root.getChildCount();
		int anzahlKinder = 0;
		for(int i = 0; i < anzahlBasics;i++){
			xnode = root.getChildAt(i);
			if(xnode==node){
				return root.getChildAt(i);
			}else{
				anzahlKinder = xnode.getChildCount();
				for(int i2 = 0;i2 < anzahlKinder;i2++){
					ynode = xnode.getChildAt(i2);{
						if(ynode == node){
							return xnode;
						}
					}
				}
			
				
			}
		}
		return retnode;
	}
	private void loescheNodeKomplett(TreeTableNode node){
		
	}
	private void loescheNode(TreeTableNode node){
		
	}
	private void aktualisiereTage(){
		
	}
	private void abrfallAnhaengen(int tagindex, JXTTreeTableNode node,String tag,String position,
			double anzahl,boolean immerfrei){
		AbrFall abr;
//		public AbrFall(String titel,String datum,String bezeichnung,Double anzahl,Double preis,
//				boolean zuzahlung,double rezgeb,String unterbrechung,String alterpreis,String sqldatum,String preisid){

		String id = RezTools.getIDFromPos(position, preisgruppe, this.preisvec);
		Double preis = Double.parseDouble(RezTools.getPreisAktFromID(id, preisgruppe, this.preisvec).replace(",","."));
		abr = new AbrFall(Integer.toString(tagindex)+".Tag",
				(String)tag,
				(String)RezTools.getKurzformFromID(id,  this.preisvec),
				(Double)anzahl,
				(Double)preis,
				(Boolean)(immerfrei ? false : true),
				(Double)this.rechneRezGebFromDouble(preis),
				(String)node.abr.unterbrechung,
				(String)node.abr.alterpreis,
				(String)node.abr.sqldatum,
				(String)id,
				(Boolean)immerfrei);
		//System.out.println("niezuzahl ="+abr.niezuzahl+" Zuzahlungspflicht = "+abr.zuzahlung);
		JXTTreeTableNode xnode = new JXTTreeTableNode("",abr,true);
		demoTreeTableModel.insertNodeInto(xnode, node, node.getChildCount());

	}
	private void prepareTreeFromVector(boolean zeigefertige ){
		AbrFall abr;
		if(vec_tabelle.size()<=0){return;}
		String testdatum = "";
		JXTTreeTableNode knoten = null;
		int tag = 0;
		for(int i = 0; i < vec_tabelle.size();i++){
			abr = new AbrFall(Integer.toString(tag)+".Tag",
					(String)vec_tabelle.get(i).get(0),
					(String)vec_tabelle.get(i).get(1),
					(Double)vec_tabelle.get(i).get(2),
					(Double)vec_tabelle.get(i).get(3),
					(Boolean)vec_tabelle.get(i).get(4),
					(Double)vec_tabelle.get(i).get(5),
					(String)vec_tabelle.get(i).get(6),
					(String)vec_tabelle.get(i).get(7),
					(String)vec_tabelle.get(i).get(8),
					(String)vec_tabelle.get(i).get(9),
					(Boolean)vec_tabelle.get(i).get(10));
			if(!testdatum.trim().equals(abr.datum.trim())){
				tag++;
				if(!zeigefertige){
					abr.unterbrechung = vectage.get(tag-1).get(2);					
				}
				abr.titel= Integer.toString(tag)+".Tag";
				knoten = new JXTTreeTableNode(abr.datum,abr,true);
				demoTreeTableModel.insertNodeInto(knoten, root, root.getChildCount());
				testdatum = new String(abr.datum);
				//System.out.println(testdatum);
				continue;
				
			}else{
				if(!zeigefertige){
					abr.unterbrechung = vectage.get(tag-1).get(2);
				}	
				foo = new JXTTreeTableNode("",abr,true);
				demoTreeTableModel.insertNodeInto(foo, knoten, knoten.getChildCount());
				testdatum = new String(abr.datum);
				//System.out.println("In einzelner Funktion "+testdatum);
				continue;
			}
		}
		//jXTreeTable.expandAll();

	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void popupMenuCanceled(PopupMenuEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
		System.out.println("Will become Invisible - "+arg0);		// TODO Auto-generated method stub
		
	}
	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
		System.out.println("Will become Visible - "+arg0);
		
	}
	/************************************************************************/

	private boolean macheEDIFACT(){
		boolean ret = true;
		double gesamt = 0.00;
		double rez = 0.00;
		double pauschal = (mitPauschale ? 10.00 : 0.00);
		edibuf.setLength(0);
		edibuf.trimToSize();
		
		String test = vec_pat.get(0).get(6);
		if( (test.trim().length()> 12) || (test.trim().length()==0) ){
			//Versichertennummer falsch oder nicht angegeben
			JOptionPane.showMessageDialog(null,"Versichertennummer nicht angegeben oder falsch");
			return false;
		}
		edibuf.append("INV+"+test.trim()+plus);
		test = vec_pat.get(0).get(7);
		if(test.trim().equals("")){
			//Status nicht angegeben
			JOptionPane.showMessageDialog(null,"Status nicht angegeben oder falsch");
			return false;			
		}else if(test.trim().length() > 5){
			test = test.substring(0,5);
		}else{
			test = test.substring(0,1)+"0001";
		}
		edibuf.append(test+plus+plus);
		edibuf.append(vec_rez.get(0).get(1).trim()+EOL);
		edibuf.append("NAD+"+hochKomma(vec_pat.get(0).get(0).trim())+plus);
		edibuf.append(hochKomma(vec_pat.get(0).get(1).trim())+plus);
		test = ediDatumFromSql(vec_pat.get(0).get(2));
		if(test.length()==0){
			JOptionPane.showMessageDialog(null,"Geburtsdatum nicht angegeben");
			return false;			
		}
		edibuf.append(test+plus);
		edibuf.append(hochKomma(vec_pat.get(0).get(3).trim())+plus);
		edibuf.append(hochKomma(vec_pat.get(0).get(4).trim())+plus);
		edibuf.append(hochKomma(vec_pat.get(0).get(5).trim())+EOL);
		JXTTreeTableNode node;
		for(int i = 0; i < getNodeCount();i++){
			node = holeNode(i);
			edibuf.append("EHE+"+disziplinGruppe+plus+SystemConfig.vPreisGueltig.get(Integer.parseInt(preisgruppe)-1)+"000"+plus);
			edibuf.append(RezTools.getPosFromID(node.abr.preisid, preisgruppe, preisvec)+plus);
			edibuf.append(dfx.format(node.abr.anzahl)+plus);
			gesamt += node.abr.preis;
			edibuf.append(dfx.format(node.abr.preis)+plus);
			edibuf.append(ediDatumFromDeutsch(node.abr.datum));
			if(node.abr.rezgeb > 0){
				rez += node.abr.rezgeb;
				edibuf.append(plus+dfx.format(node.abr.rezgeb)+EOL);
			}else{
				edibuf.append(EOL);
			}
			if(!node.abr.unterbrechung.trim().equals("")){
				edibuf.append("TXT+"+node.abr.unterbrechung.trim()+EOL);
			}
			
		}
		edibuf.append("ZHE+");
		test = vec_pat.get(0).get(14).trim();
		if( test.length() != 9 ){
			//Betriebsstätte
			test = "999999999";
		}
		try{
			int i = Integer.parseInt(test);
		}catch(Exception ex){
			test = "999999999";
		}
		edibuf.append(test+plus);
		test = vec_pat.get(0).get(15).trim();
		if( test.length() != 9 ){
			//LANR
			test = "999999999";
		}
		try{
			int i = Integer.parseInt(test);
		}catch(Exception ex){
			test = "999999999";
		}
		edibuf.append(test+plus);
		edibuf.append(ediDatumFromSql(vec_rez.get(0).get(2))+plus); 
		edibuf.append(zuZahlungsPos+plus);
		test = vec_rez.get(0).get(44).trim();
		if(test.startsWith("kein Indi")){
			JOptionPane.showMessageDialog(null,"Kein Indikationsschlüssel angegeben");
			return false;			
		}
		edibuf.append(test.replace(" ", "")+plus);
		edibuf.append(voIndex[Integer.parseInt(vec_rez.get(0).get(27))]+EOL);
		edibuf.append("BES+");
		edibuf.append(dfx.format(gesamt)+plus);
		edibuf.append(dfx.format(rez+pauschal)+plus);
		edibuf.append(dfx.format(rez)+plus);
		edibuf.append(dfx.format(pauschal)+EOL);
		

		String kopfzeile = "PG="+preisgruppe+":PATINTERN="+vec_rez.get(0).get(0).trim()+":REZNUM="+vec_rez.get(0).get(1)+
			":GESAMT="+dfx.format(gesamt)+":REZGEB="+dfx.format(rez+pauschal)+
			":REZANTEIL="+dfx.format(rez)+":REZPAUSCHL="+dfx.format(pauschal)+":KASSENID="+vec_rez.get(0).get(37)+"\n";
		edibuf.insert(0,vec_poskuerzel.toString()+"\n");
		edibuf.insert(0,vec_posanzahl.toString()+"\n");
		edibuf.insert(0,vec_pospos.toString()+"\n");
		edibuf.insert(0, kopfzeile);
		System.out.println(edibuf.toString());
		return ret;
	}	

	private	String ediDatumFromSql(String deutschDat){
		if(deutschDat.trim().length()<10){
			return "";
		}
		return deutschDat.replace("-","");
	}
	private String ediDatumFromDeutsch(String deutschDat){
		if(deutschDat.trim().length()<10){
			return "";
		}
		return deutschDat.substring(6)+deutschDat.substring(3,5)+deutschDat.substring(0,2);
	}
	private String datumFromEdiDeutsch(String deutschDat){
		return deutschDat.substring(6)+"."+deutschDat.substring(4,6)+"."+deutschDat.substring(0,4);
	}

	private String hochKomma(String string){
		String str = string.replace("?", "??");
		str = string.replace("'", "?'");
		str = str.replace(":", "?:");
		str = str.replace("+", "?+");
		str = str.replace(",", "?,");
		return str;
	}
	/************************************************************************/
	private boolean holeEDIFACT(String rez_nr){

		boolean ret = true;
		edibuf.setLength(0);
		edibuf.trimToSize();
		try{
			edibuf.append(SqlInfo.holeFelder("select edifact from fertige where rez_nr='"+rez_nr+"'").get(0).get(0));
		}catch(Exception ex){}
		if(edibuf.length()<=0){
			JOptionPane.showMessageDialog(null,"EDIFACT-Code kann nicht abgeholt werden");
		}
		String[] zeilen = edibuf.toString().split("\n");
		String[] positionen = zeilen[0].split(":");
 
		zuZahlungsPos = zeilen[zeilen.length-2].split("\\+")[4];
		zuZahlungsIndex = zzpflicht[Integer.parseInt(zuZahlungsPos)];
		this.preisgruppe = positionen[0].split("=")[1];
		this.mitPauschale = (Double.parseDouble(zeilen[zeilen.length-1].split("\\+")[4].replace(",", ".").replace("'", "")) > Double.parseDouble("0.00") ? true : false);
		int lang = zeilen.length;
		final String xrez_nr = rez_nr;
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				try{
					inworker = true;
					sucheRezept(xrez_nr);
					inworker = false;
				}catch(Exception ex){
					inworker = false;
				}
				return null;
			}
		}.execute();


		baumLoeschen();
		vecdummy.clear();
		vec_tabelle.clear();
		String [] pos;
		String id;
		String datum;
		String aktuell;
		for(int i = 4; i < lang;i++){
			pos = zeilen[i].split("\\+");
			if(pos[0].equals("EHE")){
				datum = datumFromEdiDeutsch(pos[6]);
				vecdummy.add( (String) datum );
				id = RezTools.getIDFromPos(pos[3], preisgruppe, preisvec);
				vecdummy.add((String) RezTools.getKurzformFromID(id, preisvec) );
				vecdummy.add((Double) Double.valueOf(pos[4].replace(",", ".")));
				vecdummy.add((Double) Double.valueOf(pos[5].replace(",", ".")));
				if(pos.length==8){
					vecdummy.add((boolean) Boolean.valueOf(true));
					vecdummy.add((Double) Double.valueOf(pos[7].replace(",", ".").replace("'", "")));
				}else{
					vecdummy.add((boolean) Boolean.valueOf(false));
					vecdummy.add((Double) Double.valueOf("0.00"));
				}
				if(i < (lang-1)){
					if(zeilen[i+1].split("\\+")[0].equals("TXT")){
						vecdummy.add( (String) zeilen[i+1].split("\\+")[1].replace("'",""));
					}else{
						vecdummy.add( (String) "");
					}
				}else{
					vecdummy.add( (String) "");
				}
				if( (aktuell=RezTools.getPreisAktFromID(id, preisgruppe, preisvec).trim().replace(".", ",")).equals(pos[5].trim()) ){
					vecdummy.add( (String) "aktuell");
				}else{
					vecdummy.add( (String) "alt");
				}
				vecdummy.add( (String) DatFunk.sDatInSQL(datum));
				vecdummy.add( (String) id);
				vecdummy.add((boolean) Boolean.valueOf(false));
				vec_tabelle.add((Vector<Object>)vecdummy.clone());
				vecdummy.clear();
				//public AbrFall(String titel,String datum,String bezeichnung,Double anzahl,Double preis,
				//	boolean zuzahlung,double rezgeb,String unterbrechung,String alterpreis,String sqldatum,String preisid,boolean niezuzahl){
			}

		}
		return ret;
	}
	/************************/
	private void baumLoeschen(){
		while( (root.getChildCount()) > 0){
			demoTreeTableModel.removeNodeFromParent((MutableTreeTableNode) root.getChildAt(0));
		}
	}

	 
	private void sucheRezept(String rez_nr){
		String cmd = "select * from verordn where rez_nr='"+rez_nr.trim()+"' LIMIT 1";
		//System.out.println("Kommando = "+cmd);
		vec_rez = SqlInfo.holeFelder(cmd);
		//System.out.println("RezeptVektor = "+vec_rez);
		if(vec_rez.size()<=0){
			return;
		}
		gebuehrBezahlt = vec_rez.get(0).get(14).trim().equals("T");
		gebuehrBetrag = Double.parseDouble(vec_rez.get(0).get(13));
		//                                       0         1         2             3     4       5 
		vec_pat = SqlInfo.holeFelder("select  t1.n_name,t1.v_name,t1.geboren,t1.strasse,t1.plz,t1.ort,"+
		//            6             7            8              9     10         11        12
				"t1.v_nummer,t1.kv_status,t1.kv_nummer,"+"t1.befreit,t1.bef_ab,bef_dat,t1.jahrfrei,"+
		//           13         14         15       16      		
				"t2.nachname,t2.bsnr,t2.arztnum,t3.kassen_nam1 from pat5 t1,arzt t2,kass_adr t3 where t1.pat_intern='"+
				vec_rez.get(0).get(0)+"' AND t2.id ='"+vec_rez.get(0).get(16)+"' AND t3.id='"+vec_rez.get(0).get(37)+"' LIMIT 1");
		if(vec_pat.get(0).get(9).equals("T")){
			patAktuellFrei = true;
		}else{
			patAktuellFrei  = false;
		}
		if(! vec_pat.get(0).get(12).trim().equals("")){
			patVorjahrFrei = true;
		}else{
			patVorjahrFrei = false;
		}
		patFreiAb = vec_pat.get(0).get(10);
		patFreiBis = vec_pat.get(0).get(11);
		patU18 = DatFunk.Unter18(DatFunk.sHeute(), DatFunk.sDatInDeutsch(vec_pat.get(0).get(2)));
	}
 /* 
 * 
 * 	
 */
}

class MyDateCellEditor extends AbstractCellEditor implements TableCellEditor { 
	// This is the component that will handle the editing of the cell value 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JComponent component = new JXDatePicker(); 
	SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy"); 
	// This method is called when a cell value is edited by the user. 
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int rowIndex, int vColIndex) {
		if (isSelected)	{ 
			((JXDatePicker)component).getEditor().setEditable(false);
			// cell (and perhaps other cells) are selected } 
			// Configure the component with the specified value 
			//((JXDatePicker)component).setDate((Date) table.getValueAt(rowIndex,vColIndex) );
			((JXDatePicker)component).setDate((Date) value );
			((JXDatePicker)component).setVisible(true);
			// Return the configured component 
			return component;
		} // This method is called when editing is completed. 
		return null;
		
	}
	// 'value' is value contained in the cell located at (rowIndex, vColIndex) 
	// It must return the new value to be stored in the cell. 
	public Object getCellEditorValue() { 
		return ((JXDatePicker)component).getDate(); 
	} 
}

class MyDate2CellEditor extends AbstractCellEditor implements TableCellEditor {

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getCellEditorValue() {
		// TODO Auto-generated method stub
		return null;
	}
	
}

class SucheNachAllem{
	public static void doPatSuchen(String patint,String reznr,Object source){
		String pat_int;
		pat_int = patint;
		JComponent patient = AktiveFenster.getFensterAlle("PatientenVerwaltung");
		final String xreznr = reznr;
		if(patient == null){
			final String xpat_int = pat_int;
			final Object xsource = source;
			new SwingWorker<Void,Void>(){
				protected Void doInBackground() throws Exception {
					JComponent xpatient = AktiveFenster.getFensterAlle("PatientenVerwaltung");
					Reha.thisClass.progLoader.ProgPatientenVerwaltung(1);
					while( (xpatient == null) ){
						Thread.sleep(20);
						xpatient = AktiveFenster.getFensterAlle("PatientenVerwaltung");
					}
					while(  (!AktuelleRezepte.initOk) ){
						Thread.sleep(20);
					}
					
					String s1 = "#PATSUCHEN";
					String s2 = (String) xpat_int;
					PatStammEvent pEvt = new PatStammEvent(xsource);
					pEvt.setPatStammEvent("PatSuchen");
					pEvt.setDetails(s1,s2,"#REZHOLEN-"+xreznr) ;
					PatStammEventClass.firePatStammEvent(pEvt);
					return null;
				}
				
			}.execute();
		}else{
			Reha.thisClass.progLoader.ProgPatientenVerwaltung(1);
			String s1 = "#PATSUCHEN";
			String s2 = (String) pat_int;
			PatStammEvent pEvt = new PatStammEvent(source);
			pEvt.setPatStammEvent("PatSuchen");
			pEvt.setDetails(s1,s2,"#REZHOLEN-"+xreznr) ;
			PatStammEventClass.firePatStammEvent(pEvt);

		}
	}
	
}