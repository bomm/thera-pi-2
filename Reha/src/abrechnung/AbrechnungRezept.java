package abrechnung;

import hauptFenster.AktiveFenster;
import hauptFenster.Reha;
import hauptFenster.UIFSplitPane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
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

import jxTableTools.DblCellEditor;
import jxTableTools.DoubleTableCellRenderer;
import jxTableTools.MyTableCheckBox;
import jxTableTools.MyTableComboBox;
import jxTableTools.MyTableStringDatePicker;

import org.jdesktop.swingx.JXDatePicker;
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
import systemTools.StringTools;
import terminKalender.DatFunk;
import terminKalender.ParameterLaden;



public class AbrechnungRezept extends JXPanel implements HyperlinkListener,ActionListener, MouseListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8387184772704779192L;
	private Abrechnung1 eltern;
	JToolBar tb = null;
	
	DecimalFormat dfx = new DecimalFormat( "0.00" );
	
	String preisgruppe = "-1";
	
	JButton[] tbbuts = {null,null,null,null};
	JLabel[] labs = {null,null,null,null,null,null,null,null,null,null,
					null,null,null,null,null,null,null,null,null,null,
					null,null,null,null,null,null,null,null,null,null};
	
	Vector<Vector<Object>> vec_tabelle = new Vector<Vector<Object>>();
	Vector<Object> vecdummy = new Vector<Object>();
	
	Vector<Vector<String>>vec_rez = null;
	Vector<Vector<String>>vec_pat = null;
	Vector<Vector<String>>vec_term = null;
	
	Vector<Vector<String>>vec_kuerzel = new Vector<Vector<String>>();
	Vector<String> kundid = new Vector<String>();

	Vector<String>vec_poskuerzel = new Vector<String>();
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
	
	Double rezeptWert;
	Double zuzahlungWert;
	Double kmWert;
	
	public JXTable tageTbl = null;
	public MyTageTableModel tageMod = new MyTageTableModel();
	MyTableComboBox mycomb;
	MyTableComboBox mycomb2 = new MyTableComboBox();
	MyTableCheckBox mycheck;
	
	private UIFSplitPane jSplitOU = null;
	private String[] voArt = {"Erstverordnung","Folgeverordnung","Folgeverordn. außerhalb d. Regelf."};
	private String[] voBreak = {"","K","U","T","A"};
	private String[] voPreis = {"akt. Tarif","alter Tarif"};
	JEditorPane htmlPane = null;
	
	private JXTTreeTableNode aktnode;
	private int aktrow;
	private JXTTreeTableNode root = null;
	private TageTreeTableModel demoTreeTableModel = null;
	private JXTreeTable jXTreeTable = null;
	private JXTTreeTableNode foo = null;
	
	public AbrechnungRezept(Abrechnung1 xeltern){
		eltern = xeltern;
		setLayout(new BorderLayout());
		//add(getEinzelRezPanel(),BorderLayout.CENTER);
		cmbkuerzel = new JRtaComboBox( (Vector<Vector<String>>) vec_kuerzel,0,1);
		cmbkuerzel.setActionCommand("cmbkuerzel");
		cmbkuerzel.addActionListener(this);
		add(getSplitPane(),BorderLayout.CENTER);
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				jSplitOU.setDividerLocation(100);
				//jSplitOU.setDividerLocation(getHeight());
				System.out.println("Höhe ="+getHeight());
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
		getEinzelRezPanel();
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
			preisvec = ParameterLaden.vKGPreise;
		}else if(xreznummer.contains("MA")){
			preisvec = ParameterLaden.vMAPreise;
		}else if(xreznummer.contains("ER")){
			preisvec = ParameterLaden.vERPreise;
		}else if(xreznummer.contains("LO")){
			preisvec = ParameterLaden.vLOPreise;
		}else if(xreznummer.contains("RH")){
			preisvec = ParameterLaden.vRHPreise;
		}
		//vec_kuerzel.clear();
		int idpos = preisvec.get(0).size()-1;
		for(int i = 0;i< preisvec.size();i++){
			kundid.clear();
			kundid.add(preisvec.get(i).get(1));
			kundid.add(preisvec.get(i).get(idpos));
			//vec_kuerzel.add( (Vector<String>)kundid.clone() );
		}
		//cmbkuerzel.setDataVectorVector((Vector<Vector<String>>)vec_kuerzel.clone(), 0, 1);
		System.out.println("Einstellen des KürzelVectors ---> "+vec_kuerzel);
		
	}
	public void setKuerzelVec(String xreznummer){
		if(xreznummer.contains("KG")){
			preisvec = ParameterLaden.vKGPreise;
		}else if(xreznummer.contains("MA")){
			preisvec = ParameterLaden.vMAPreise;
		}else if(xreznummer.contains("ER")){
			preisvec = ParameterLaden.vERPreise;
		}else if(xreznummer.contains("LO")){
			preisvec = ParameterLaden.vLOPreise;
		}else if(xreznummer.contains("RH")){
			preisvec = ParameterLaden.vRHPreise;
		}
		vec_kuerzel.clear();
		int idpos = preisvec.get(0).size()-1;
		for(int i = 0;i< preisvec.size();i++){
			kundid.clear();
			kundid.add(preisvec.get(i).get(1));
			kundid.add(preisvec.get(i).get(idpos));
			vec_kuerzel.add( (Vector<String>)kundid.clone() );
		}
		try{
			mycomb2.setVector(vec_kuerzel,0,1);
			mycomb.setVector(vec_kuerzel,0,1);			
		}catch(Exception ex){
			ex.printStackTrace();
			cmbkuerzel.setDataVectorVector(vec_kuerzel,0,1);
		}

		//cmbkuerzel.setDataVectorVector((Vector<Vector<String>>)vec_kuerzel.clone(), 0, 1);
		System.out.println("Einstellen des KürzelVectors ---> "+vec_kuerzel);
		
	}
	public boolean setNewRez(String rez){
		try{
			String dummy1 = rez.split(",")[2];
			String dummy2 = dummy1.split("-")[0];
			System.out.println("Neues Rezept = "+dummy2);

			setPreisVec(dummy2);
			setWerte(dummy2);		
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
		FormLayout lay = new FormLayout("0dlu,20dlu,fill:0:grow(1.0),20dlu,0dlu",
				"0dlu,p,15dlu,fill:0:grow(0.5),2dlu,fill:0:grow(0.5),60dlu");
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
        			ZeigePopupMenu(evt.getX(),evt.getY());
        		}else{
        			jXTreeTable.setShowGrid(false);
        		}
        	}
        	
        });
        
        jXTreeTable.setOpaque(true);
        jXTreeTable.setRootVisible(false);
        jXTreeTable.getColumnModel().getColumn(1).setCellEditor(new MyTableStringDatePicker());

    	//MyTableComboBox mycomb = new MyTableComboBox();
        jXTreeTable.getColumn(2).setCellEditor(mycomb2);

        //jXTreeTable.getColumn(0).setCellEditor(new MyDateCellEditor());
        //jXTreeTable.getColumn(0).setCellEditor(new MyTableStringDatePicker());
        jXTreeTable.getColumnModel().getColumn(9).setMinWidth(0);
        jXTreeTable.getColumnModel().getColumn(9).setMaxWidth(0);
        
        jXTreeTable.getColumn(0).setMinWidth(50);
        jXTreeTable.validate();
        //jXTreeTable.getColumn(1).setCellEditor(new MyTableStringDatePicker());
        //jXTreeTable.getColumn(0).setCellEditor(new MyDateCellEditor());
        jXTreeTable.setSortOrder(7, SortOrder.ASCENDING);
        //jXTreeTable.getSelectionModel().addListSelectionListener(new AbrechnungListSelectionHandler());
        
        jXTreeTable.addTreeSelectionListener(new AbrechnungTreeSelectionListener());
        jXTreeTable.setSelectionMode(0);
        
        JScrollPane jscr = JCompTools.getTransparentScrollPane(jXTreeTable);
        jscr.validate();
        //jpan.add(jscr,cc.xywh(3,4,1,3));
        jpan.add(jscr,cc.xywh(1,4,5,4));
		return jpan;
		
	}

	public void ZeigePopupMenu(int x, int y){
		JPopupMenu jPop = getTerminPopupMenu();
		
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
		item = new JMenuItem("neu Behandlung einfügen");
		item.setActionCommand("behandlungneu");
		item.addActionListener(this);
		jPopupMenu.add(item);
		jPopupMenu.addSeparator();
		item = new JMenuItem("Behandlung löschen");
		item.setActionCommand("behandlungloeschen");
		item.addActionListener(this);
		jPopupMenu.add(item);
		
		jPopupMenu.addSeparator();
		return jPopupMenu;
	}
	
	
	/*******
	 * 
	 * 
	 * @return
	 */
	private JXPanel getEinzelRezPanel(){
		JXPanel jpan = new JXPanel(new BorderLayout());
		FormLayout lay = new FormLayout("0dlu,20dlu,fill:0:grow(1.0),20dlu,0dlu",
				"0dlu,p,15dlu,fill:0:grow(0.5),2dlu,fill:0:grow(0.5),60dlu");
		jpan.setLayout(lay);
		CellConstraints cc = new CellConstraints();
		//jpan.add(getToolbar(),BorderLayout.NORTH);
		jpan.add(getToolbar(),cc.xyw(1,2,5));
		/****/
		tageMod.setColumnIdentifiers(new String[] {"Behandlungstag","Heilmittel","Anzahl","Tarif","Zuzahlung","Rez.Geb.","Unterbrechung","Alt","sqldat"});
		tageTbl = new JXTable(tageMod);
		tageTbl.addMouseListener(this);
		tageTbl.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION );
		tageTbl.getSelectionModel().addListSelectionListener(new AbrechnungListSelectionHandler());
		tageTbl.setSortable(false);
		tageTbl.getColumn(8).setMinWidth(0);
		tageTbl.getColumn(8).setMaxWidth(0);
		
		MyTableStringDatePicker pic = new MyTableStringDatePicker();
		tageTbl.getColumn(0).setCellEditor(pic);
		/*
		cmbpreis = new JRtaComboBox(vec_kuerzel,0,1);
		cmbpreis.setActionCommand("preis");
		cmbpreis.addActionListener(this);
		cmbpreis.setVisible(true);
		*/
		//tageTbl.getColumn(1).setCellEditor(new DefaultCellEditor(cmbkuerzel));
		mycomb = new MyTableComboBox();
		((JRtaComboBox)mycomb.getComponent()).setActionCommand("kuerzel");
		((JRtaComboBox)mycomb.getComponent()).addActionListener(this);
		
		tageTbl.getColumn(1).setCellEditor(mycomb);
		
		tageTbl.getColumn(3).setCellEditor(new JXTable.NumberEditor());
		tageTbl.getColumn(3).setCellRenderer(new DoubleTableCellRenderer() );

		chkzuzahl = new JCheckBox();
		chkzuzahl.setVerticalAlignment(SwingConstants.CENTER);
		chkzuzahl.setHorizontalAlignment(SwingConstants.CENTER);
		chkzuzahl.setActionCommand("zuzahlung");
		chkzuzahl.setVisible(true);
		chkzuzahl.addActionListener(this);
		
		mycheck = new MyTableCheckBox();
		((JRtaCheckBox)mycheck.getComponent()).setActionCommand("zuzahlung");
		((JRtaCheckBox)mycheck.getComponent()).addActionListener(this);
		tageTbl.getColumn(4).setCellEditor(mycheck);
		//tageTbl.getColumn(4).setCellEditor(new DefaultCellEditor(chkzuzahl));

		tageTbl.setEditable(true);
		tageTbl.setVisible(true);
		tageTbl.validate();
		
		JScrollPane jscrt = JCompTools.getTransparentScrollPane(tageTbl);
		jscrt.validate();
		jscrt.setVisible(true);
		jpan.add(jscrt,cc.xywh(3,4,1,3));
		jpan.validate();
		jpan.setVisible(true);
		return jpan;
	}
	private JToolBar getToolbar(){
		JToolBar jtb = new JToolBar();
		jtb.setOpaque(false);
		jtb.setRollover(true);
		jtb.setBorder(null);
		jtb.setOpaque(false);
		tbbuts[0] = new JButton();
		tbbuts[0].setIcon(SystemConfig.hmSysIcons.get("print"));
		jtb.add(tbbuts[0]);
		tbbuts[1] = new JButton();
		tbbuts[1].setIcon(SystemConfig.hmSysIcons.get("scanner"));
		jtb.add(tbbuts[1]);
		return jtb;
	}
	private void setWerte(String rez_nr){

		String cmd = "select * from verordn where rez_nr='"+rez_nr.trim()+"' LIMIT 1";
		System.out.println("Kommando = "+cmd);
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
		/*
		boolean patAktuellFrei = false;
		boolean patVorjahrFrei = false;
		boolean patU18 = false;
		String	patFreiAb;
		String	patFreiBis;
		*/
		//System.out.println("PatArztVektor = "+vec_pat);
		//System.out.println(rootRezept.getRoot().toString());
		//Object rootNode = treeRezept.getModel().getRoot();
		//((DefaultMutableTreeNode)rootNode).setUserObject(rez_nr.trim());
		//treeRezept.repaint();
		//testeTage();
		ermittleAbrechnungsfall();
		parseHTML(rez_nr.trim());
		if(tageTbl.getRowCount()>0){
			tageTbl.validate();
			tageTbl.repaint();					
		}
		
	}
	/*****************************************************************************************/
	private void ermittleAbrechnungsfall(){
		//vec_tabelle.clear();
		
		Vector<Vector<String>> vectage = RezTools.macheTerminVector( vec_rez.get(0).get(34));
		Vector<Vector<String>> vecabrfaelle = new Vector<Vector<String>>();
		Vector<String> vecabrfall = new Vector<String>();
		vec_tabelle.clear();
		vec_poskuerzel.clear();
		vec_posanzahl.clear();
		String[] behandlungen = null;
		
		preisgruppe = vec_rez.get(0).get(41);
		int anzahlbehandlungen = 0;
		int anzahlhb = 0;
		boolean hb = false;
		int hbposanzahl = 0;
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
		if(vec_rez.get(0).get(43).equals("T")){
			hb= true;
			if(RezTools.zweiPositionenBeiHB(preisgruppe)){
				hbposanzahl = 2;
				anzahlhb=2;
			}else{
				hbposanzahl = 1;
				anzahlhb=1;
			}
		}
		String splitvec = null;
		try{
			System.out.println("Vector ========= "+(Vector<Vector<String>>) vec_kuerzel.clone());
			System.out.println("Combobox = "+cmbkuerzel);
			cmbkuerzel.setDataVectorVector( (Vector<Vector<String>>) vec_kuerzel.clone(), 0, 1);
		}catch(Exception ex){
			ex.printStackTrace();
			System.out.println("VecKuerzel = "+vec_kuerzel);
		}
		
		this.tageMod.setRowCount(0);

		
		int childs;
		while( (childs=root.getChildCount()) > 0){
			demoTreeTableModel.removeNodeFromParent((MutableTreeTableNode) root.getChildAt(0));
		}
		
		for(int i = 0; i < vectage.size();i++){
			splitvec = vectage.get(i).get(3);
			behandlungen = splitvec.split(",");
			if(behandlungen.length > 0 && (!splitvec.trim().equals(""))){
				//Es stehen Behandlungsdaten im Terminblatt;
				//Positionen = length+hbposanzahl;
				//System.out.println("Länge des Feldes = "+behandlungen.length);
				//anzahlbehandlungen = behandlungen.length;
				constructTagVector(vectage.get(i).get(0),behandlungen,behandlungen.length,anzahlhb);
			}else{
				//Es sind keine  Behandlungsformen im Terminblatt verzeichnet;
				//in anzahlbehandlungen steht die tatsächliche Anzahl
				//System.out.println("Keine Behandlungen im Terminblatt = "+anzahlbehandlungen);
				//System.out.println("Anzahl-Hausbesuch  keine Beh. im Terminblatt= "+anzahlhb);
				constructTagVector(vectage.get(i).get(0),null,anzahlbehandlungen,anzahlhb);
			}
		}
		if(vec_tabelle.size()>0){
			for(int i2 = 0;i2<vec_tabelle.size();i2++){
				tageMod.addRow((Vector<Object>)vec_tabelle.get(i2).clone());
			}
		}
		
		this.tageTbl.validate();
		
		doFuelleTreeTable();
		doGebuehren();
		doPositionenErmitteln();
		doRezeptWertermitteln();
		
		//System.out.println(vec_tabelle);
		
	}
	/******************************
	 * 
	 * 
	 * 
	 * 
	 */
	private void doGebuehren(){
		boolean amBeginnFrei = false;
		boolean amEndeFrei = false;

		boolean volleZuzahlung = true;

		boolean unter18 = false;
		
		boolean vollFrei = false;
		boolean teilFrei = false;
		mitPauschale = true;
		boolean jahresWechsel = false;
		if(patU18){
			unter18 = true;
			vollFrei = true;
			amBeginnFrei = true;
			volleZuzahlung = false;
			mitPauschale = false;
			doTreeFreiAb(0,vec_tabelle.size(),false);
			doFreiAb(0,vec_tabelle.size(),false);
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
				doFreiAb(0,vec_tabelle.size(),false);
				doTreeFreiAb(0,vec_tabelle.size(),false);
			}else if(( (Boolean)u18[0]) && ( (Boolean)u18[2])) {
				//Während der Behandlung 18 geworden, Rezept muß gesplittet werden;
				unter18 = true;
				teilFrei = true;
				amBeginnFrei = true;
				volleZuzahlung = false;
				mitPauschale = false;
				doFreiAb(0,(Integer)u18[1],false);
				doTreeFreiAb(0,(Integer)u18[1],false);
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
				doTreeFreiAb(0,vec_tabelle.size(),false);
				doFreiAb(0,vec_tabelle.size(),false);
				mitPauschale = false;
			}
			
		}else if((Boolean)newYear[0] && (!(Boolean)newYear[2])){
			//Das Rezept ist erstreckt sich über den Jahreswechsel
			if(gebuehrBezahlt){  //1.Fall
				// die Gebühr wurde bereits bezahlt was soviel heißt wie es muß verrechnet werden
				// doFreiAb(0,vec_tabelle.size(),true);
				mitPauschale = true;
				wechselfall = 1;
			}else if(patVorjahrFrei && patAktuellFrei && (!gebuehrBezahlt)){ //2.Fall
				// im Vorjahr befreit und jetzt schon wieder befreit und kein Vermerk bezahlt
				doTreeFreiAb(0,vec_tabelle.size(),false);
				doFreiAb(0,vec_tabelle.size(),false);
				mitPauschale = false;
				wechselfall = 2;
			}else if( (patVorjahrFrei) && (!patAktuellFrei) ){ //3.Fall
				// im Vorjahr befreit und jetzt zuzahlungspflichtig
				doTreeFreiAb(0,(Integer)newYear[1],false);
				doFreiAb(0,(Integer)newYear[1],false);
				mitPauschale = false;
				wechselfall = 3;
			}else if((!patVorjahrFrei) && (!patAktuellFrei) && (!unter18) ){ //4.Fall
				//weder im Vorjahr noch im aktuellen Jahr befreit
				doTreeFreiAb(0,vec_tabelle.size(),true);
				doFreiAb(0,vec_tabelle.size(),true);
				mitPauschale = true;
				wechselfall = 4;
			}else if((!patVorjahrFrei) && (patAktuellFrei) && (!unter18)){ //5.Fall
				// war nicht im Vorjahr befreit ist aber jetzt befreit
				doTreeFreiAb((Integer) newYear[1],vec_tabelle.size(),true);
				doFreiAb((Integer) newYear[1],vec_tabelle.size(),true);
				mitPauschale = true;
				wechselfall = 5;
			}
		}else if(!(Boolean)newYear[0]){
			//Das Rezept wurde vollständig im aktuelle Jahr abgearbeitet
			if(patAktuellFrei && (!gebuehrBezahlt)){
				doTreeFreiAb(0,vec_tabelle.size(),false);
				doFreiAb(0,vec_tabelle.size(),false);
				mitPauschale = false;
				neufall = 1;
			}else if(patAktuellFrei && gebuehrBezahlt ){
				doTreeFreiAb(0,vec_tabelle.size(),true);
				doFreiAb(0,vec_tabelle.size(),true);
				mitPauschale = true;
				neufall = 2;
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
		/*
		boolean patAktuellFrei = false;
		boolean patVorjahrFrei = false;
		boolean patU18 = false;
		String	patFreiAb;
		String	patFreiBis;
		gebuehrBezahlt = vec_rez.get(0).get(14).trim().equals("T");
		gebuehrBetrag = Double.parseDouble(vec_rez.get(0).get(13));

		*/
		
		
		///// Jetzt der Tarifwchsel-Check
		// erst einlesen ab wann der Tarif gültig ist
		// dann testen ob Rzeptdatum nach diesem Datum liegt wenn ja sind Preise o.k.
		// wenn nein -> testen welche Anwendungsregel gilt und entsprchend in einer
		// for next Schleife die Preise anpassen!
		// bevor jetzt weitergemacht werden kann muß der Vector für die Behandlungen erstellt werden!!!!!

	}
	private void doPositionenErmitteln(){
		int lang = 0;
		if( (lang=vec_tabelle.size())<=0){return;}
		for(int i = 0; i < lang;i++){
			if(! vec_poskuerzel.contains((vec_tabelle.get(i).get(1) )) ){
				vec_poskuerzel.add(vec_tabelle.get(i).get(1).toString());
				vec_posanzahl.add(1);
			}else{
				int pos = vec_poskuerzel.indexOf(vec_tabelle.get(i).get(1) );
				int anzahl = vec_posanzahl.get(pos);
				vec_posanzahl.set(pos, anzahl+1);
			}
		}
		for(int i = 0; i < vec_poskuerzel.size();i++){
			System.out.println("Behandlungsart: "+vec_poskuerzel.get(i)+" - Anzahl verabreicht: "+vec_posanzahl.get(i) );
		}
	}
	private void doZeileWertermitteln(){
		
	}
	private void doRezeptWertermitteln(){
		int lang = 0;
		rezeptWert = 0.00;
		zuzahlungWert = 0.00;

		BigDecimal dummy1 = BigDecimal.valueOf(Double.parseDouble("0.00"));
		BigDecimal dummy2 = BigDecimal.valueOf(Double.parseDouble("0.00"));

		BigDecimal ddummy1 = BigDecimal.valueOf(Double.parseDouble("0.00"));
		BigDecimal ddummy2 = BigDecimal.valueOf(Double.parseDouble("0.00"));

		if( (lang=tageMod.getRowCount())<=0){return;}
		System.out.println("Vectorlänge von vec_tabelle = "+lang);
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
		for(int i = 0; i < lang;i++){
			AbrFall abr = this.holeAbrFall(i);
			ob1 = abr.anzahl;
			ob2 = abr.preis;
			ob3 = abr.zuzahlung;
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

	private void doFreiAb(int von, int bis,boolean pflichtig){
		for(int i = von; i < bis; i++){
			tageMod.setValueAt( pflichtig, i, 4);	
			if(!pflichtig){
				tageMod.setValueAt(0.00, i, 5);	
			}
		}
		
	}
	private void doTreeFreiAb(int von, int bis,boolean pflichtig){
		for(int i = von; i < bis; i++){
			AbrFall abr = holeAbrFall(i);
			System.out.println("Satz "+i+" ist Zuzahlungspflichti = "+abr);
			//if(abr != null){
				abr.zuzahlung = pflichtig;
				if(!pflichtig){
					abr.rezgeb = Double.parseDouble("0.00");
				}
			//}else{
				System.out.println("Fehler in Zeile "+i);
			//}
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
				abrObject = RezTools.getKurzformUndIDFromPos(behandlungen[i].trim(), preisgruppe, preisvec);
				abrfall[i] = abrObject[0].toString();
				id[i] = abrObject[1].toString();
				System.out.println(""+i+". Behandlung aus Terminblatt = "+abrfall[i]);
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
				//untersuchen ob unter 18
				//.....
				
				//untersuchen ob teilweise frei
				//.....
				vecdummy.add(Double.valueOf(rechneRezGeb(vecdummy.get(3).toString()).replace(",", ".")) );
				
				vecdummy.add((String) "K" );
				vecdummy.add((String) "Alt" );
				vecdummy.add((String) DatFunk.sDatInSQL(datum) );
				//System.out.println(vecdummy.clone());
				vec_tabelle.add((Vector<Object>)vecdummy.clone());
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
			System.out.println("Anzahl Root-Knoten = "+rootAnzahl);
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
				System.out.println("Anzahl Kind-Knoten = "+kindAnzahl);
				geprueft++;
				for(int i2 = 0; i2 < kindAnzahl;i2++){
					
					if(geprueft==zeile){
						childNode = (JXTTreeTableNode) rootNode.getChildAt(i2);
						System.out.println("Zeile gefunden geprueft wurden "+geprueft);
						return childNode.abr;
					}else{
						childNode = (JXTTreeTableNode) rootNode.getChildAt(i2);
						System.out.println("Zeile überpüft="+geprueft+" - "+childNode.abr.datum+" - "+childNode.abr.bezeichnung);
						geprueft ++;						
					}

				}
			}else{
				System.out.println("Keine Bedingung trifft zu="+geprueft);
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
			System.out.println("Anzahl Root-Knoten = "+rootAnzahl);
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
				System.out.println("Anzahl Kind-Knoten = "+kindAnzahl);
				geprueft++;
				for(int i2 = 0; i2 < kindAnzahl;i2++){
					
					if(geprueft==zeile){
						childNode = (JXTTreeTableNode) rootNode.getChildAt(i2);
						System.out.println("Zeile gefunden geprueft wurden "+geprueft);
						return childNode;
					}else{
						childNode = (JXTTreeTableNode) rootNode.getChildAt(i2);
						System.out.println("Zeile überpüft="+geprueft+" - "+childNode.abr.datum+" - "+childNode.abr.bezeichnung);
						geprueft ++;						
					}

				}
			}else{
				System.out.println("Keine Bedingung trifft zu="+geprueft);
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
					(String)vec_tabelle.get(i).get(8));
			if(!testdatum.trim().equals(abr.datum.trim())){
				tag++;
				abr.titel= Integer.toString(tag)+".Tag";
				knoten = new JXTTreeTableNode(abr.datum,abr,true);
				demoTreeTableModel.insertNodeInto(knoten, root, root.getChildCount());
				testdatum = new String(abr.datum);
				System.out.println(testdatum);
				continue;
				
			}else{
				foo = new JXTTreeTableNode("",abr,true);
				demoTreeTableModel.insertNodeInto(foo, knoten, knoten.getChildCount());
				testdatum = new String(abr.datum);
				System.out.println("In einzelner Funktion "+testdatum);
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
		//SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		public AbrFall(String titel,String datum,String bezeichnung,Double anzahl,Double preis,
				boolean zuzahlung,double rezgeb,String unterbrechung,String alterpreis,String sqldatum){
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
		}
	}
	
	private void parseHTML(String rez_nr){
		if(rez_nr==null){
			return;
		}
		/*
		labs[0].setText(rez_nr.trim()); //Rezeptnummer		
		labs[1].setText(DatFunk.sDatInDeutsch(vec_rez.get(0).get(2))); //Rezeptdatum
		labs[2].setText(vec_pat.get(0).get(7)); //Status
		labs[3].setText(vec_pat.get(0).get(6)); //Versichertennummer
		labs[4].setText(vec_pat.get(0).get(0)); //Nachname
		labs[5].setText(vec_pat.get(0).get(1)); //Vorname
		labs[6].setText(DatFunk.sDatInDeutsch(vec_pat.get(0).get(2))); //Geboren
		*/

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
		"<b>"+rez_nr+"<b><br><br>"+
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
		(vec_rez.get(0).get(44).startsWith("kein Indi") ? "<b>"+vec_rez.get(0).get(44)+"</b>" : vec_rez.get(0).get(44))+
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
		(vec_rez.get(0).get(27).equals("T") ? "befreit" : "zuzahlungspflichtig")+
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



		"</table>"+
		"</font>"+
		"</div>"+
		"</html>";
		this.htmlPane.setText(text);
	}
	@Override
	public void hyperlinkUpdate(HyperlinkEvent event) {
	    if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
        	System.out.println(event.getURL());
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
	        	System.out.println(lsm);
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
								System.out.println(jXTreeTable.getValueAt(row, col));
								//demoTreeTableModel
								//JXTTreeTableNode
								//System.out.println("ChildCount = "+demoTreeTableModel.getRoot().getChildCount());
								//JXTTreeTableNode node = jXTreeTable.i
								//System.out.println("Gewählt wurde Reihe="+jXTreeTable.getSelectedRow());
								//System.out.println("Gewählt wurde Spalte="+ jXTreeTable.getSelectedColumn());
								for(int i = 0; i < root.getChildCount();i++ ){
									JXTTreeTableNode node = (JXTTreeTableNode) root.getChildAt(i);
									System.out.println("Bezeichnung = "+i+".0 "+node.abr.bezeichnung);
									int childs = node.getChildCount();
									//System.out.println("Kinder von root="+childs);
									//System.out.println("Hauptknoten="+root.getIndex(node));
									for(int i2 = 0; i2 < childs; i2++){
										JXTTreeTableNode node2 = (JXTTreeTableNode) node.getChildAt(i2);
										System.out.println("Bezeichnung = "+i+"."+(i2+1)+node2.abr.bezeichnung);
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
	        //System.out.println(output.toString());
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
					
					/*
					for(int i = 0; i < selpaths.length;i++){
						System.out.println(selpaths[i]);
					}
					*/
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
					//							selpaths = new TreePath[selPathList.size()];
					selpaths = selPathList.toArray(new TreePath[0]);

					tt.getTreeSelectionModel().setSelectionPaths(selpaths);
					
					TreePath tp = tt.getTreeSelectionModel().getSelectionPath();
					aktnode =  (JXTTreeTableNode) tp.getLastPathComponent();//selpaths[selpaths.length-1].getLastPathComponent();
					new SwingWorker<Void,Void>(){
						protected Void doInBackground() throws Exception {
							int lang = getNodeCount();
							for(int i = 0; i < lang; i++){
								if(aktnode == holeNode(i)){
									aktrow = i;
									System.out.println("Zeilennummer =  = "+i);
									System.out.println("Node selektiert = "+aktnode.abr.bezeichnung);
									System.out.println("Behandlungsdatum selektiert = "+aktnode.abr.datum+" / "+aktnode.abr.bezeichnung);
									break;
								}
							}
							return null;
						}
						
					}.execute();
					
					//System.out.println(selPathList);
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
        	//System.out.println(jXTreeTableNode.getUserObject());
        	//JXTTreeTableNode
        	
        	AbrFall o = null;
        	
            try{
            	o =  (AbrFall) jXTreeTableNode.getUserObject();
            }catch(Exception ex){
            	//ex.printStackTrace();
            	//System.out.println("SuperValue....."+super.getValueAt(node, column));
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
            System.out.println(value+"  "+column);
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
            	o.rezgeb = (Double) ((Double)value)/100.00*10.00;
            	jXTreeTable.repaint();
            	break;
            case 5:
            	o.zuzahlung = ((Boolean)value);
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
	
            }
        }
 
        public boolean isCellEditable(java.lang.Object node,int column){
            switch (column) {
            case 0:
                return false;
            case 1:
                return true;
            case 2:
                return true;
            case 3:
                return true;
            case 4:
                return true;   
            case 5:
                return true;
            case 6:
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
            return 10;
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
                return null;                
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

		if(cmd.equals("kuerzel")){
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					if(tageTbl.getSelectedRow() < 1){return;}
					System.out.println("Wert von cmbKuerzel = "+cmbkuerzel.getValueAt(1)+
							" / DisplayWert = "+((JRtaComboBox)mycomb.getComponent()).getValueAt(0)+ " Selektierte Tabellenzeile = "+
							tageTbl.getSelectedRow()	);
				}
			});
			return;
		}
		if(cmd.equals("zuzahlung")){
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					if(tageTbl.getSelectedRow() < 1){return;}
					System.out.println("Wert der CheckBox = "+((JRtaCheckBox)mycheck.getComponent()).isSelected()+ " Selektierte Tabellenzeile = "+
							tageTbl.getSelectedRow()	);
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

		}
		if(cmd.equals("behandlungneu")){
			//jXTreeTable.collapseAll();
		}
		if(cmd.equals("behandlungloeschen")){
			//jXTreeTable.collapseAll();
		}
	

			

		
		
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

	
/************************
 * 
 * 
 * 	
 */
}


/***********************************/
class MyTageTableModel extends DefaultTableModel{
	   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Class<?> getColumnClass(int columnIndex) {
		   if(columnIndex==0)
			   return String.class;
		   if(columnIndex==1)
			   return String.class;
		   if(columnIndex==2){
			   return Double.class;
		   }if(columnIndex==3){
			   return Double.class;
		   }if(columnIndex==4){
			   return Boolean.class;
		   }if(columnIndex==5){
			   return Double.class;
		   }else{
			   return String.class;
		   }
        //return (columnIndex == 0) ? Boolean.class : String.class;
    }

	    public boolean isCellEditable(int row, int col) {
	        //Note that the data/cell address is constant,
	        //no matter where the cell appears onscreen.

	        if (col == 0){
	        	return true;
	        }else if(col == 1){
	        	return true;
	        }else if(col == 2){
	        	return true;
	        }else if(col == 3){
	        	return true;
	        }else if(col == 4){
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
					/*
					s1 = "#PATEDIT";
					s2 = (String) xpat_int;
					pEvt.setPatStammEvent("PatEdit");
					pEvt.setDetails(s1,s2,"#PATEDIT-"+xreznr) ;
					PatStammEventClass.firePatStammEvent(pEvt);
					*/

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
			/*
			s1 = "#PATEDIT";
			s2 = (String) pat_int;
			pEvt.setPatStammEvent("PatEdit");
			pEvt.setDetails(s1,s2,"#PATEDIT-"+xreznr) ;
			PatStammEventClass.firePatStammEvent(pEvt);
			*/

		}
	}
	
}