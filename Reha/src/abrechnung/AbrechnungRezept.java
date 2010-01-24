package abrechnung;

import hauptFenster.AktiveFenster;
import hauptFenster.Reha;
import hauptFenster.UIFSplitPane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.util.EventObject;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.CellEditorListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.NumberFormatter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import jxTableTools.DblCellEditor;
import jxTableTools.DoubleTableCellRenderer;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.JXTable.NumberEditor;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableModel;

import patientenFenster.AktuelleRezepte;


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
import systemTools.JRtaComboBox;
import systemTools.MyTableStringDatePicker;
import systemTools.StringTools;
import terminKalender.DatFunk;
import terminKalender.ParameterLaden;



public class AbrechnungRezept extends JXPanel implements HyperlinkListener,ActionListener{
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
	
	public JXTable tageTbl = null;
	public MyTageTableModel tageMod = new MyTageTableModel();
	
	private UIFSplitPane jSplitOU = null;
	private String[] voArt = {"Erstverordnung","Folgeverordnung","Folgeverordn. außerhalb d. Regelf."};
	private String[] voBreak = {"","K","U","T","A"};
	private String[] voPreis = {"akt. Tarif","alter Tarif"};
	JEditorPane htmlPane = null;
	
	private static JXTTreeTableNode root = null;
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
				jSplitOU.setDividerLocation(getHeight());
				System.out.println("Höhe ="+getHeight());
			}
		});
		
	}
	private JXPanel getSplitPane(){
		JXPanel jpan = new JXPanel();
		jpan.setLayout(new BorderLayout());
		jSplitOU =  UIFSplitPane.createStrippedSplitPane(JSplitPane.VERTICAL_SPLIT,
				getHTMLPanel(),
				/*getTageTree()*/ getEinzelRezPanel()); 
		jSplitOU.setDividerSize(7);
		jSplitOU.setDividerBorderVisible(true);
		jSplitOU.setName("BrowserSplitObenUnten");
		jSplitOU.setOneTouchExpandable(true);
		//jSplitOU.setDividerLocation(jpan.getHeight());
		
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
		//cmbkuerzel.setDataVectorVector((Vector<Vector<String>>)vec_kuerzel.clone(), 0, 1);
		System.out.println("Einstellen des KürzelVectors ---> "+vec_kuerzel);
		
	}
	public boolean setNewRez(String rez){
		try{
		String dummy1 = rez.split(",")[2];
		String dummy2 = dummy1.split("-")[0];
		System.out.println("Neues Rezept = "+dummy2);
		final String xpreis = dummy2;
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				
				return null;
			}
		}.execute();
		setPreisVec(xpreis);
		setWerte(xpreis);		
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

        jXTreeTable = new JXTreeTable(demoTreeTableModel);
        jXTreeTable.setOpaque(true);
        jXTreeTable.setRootVisible(false);
        jXTreeTable.getColumnModel().getColumn(0).setCellEditor(new MyDateCellEditor());

        
        jXTreeTable.getColumn(1).setCellEditor(new DefaultCellEditor(cmbkuerzel));

        //jXTreeTable.getColumn(0).setCellEditor(new MyDateCellEditor());
        //jXTreeTable.getColumn(0).setCellEditor(new MyTableStringDatePicker());
        jXTreeTable.getColumn(7).setMaxWidth(0);
        jXTreeTable.getColumn(7).setMinWidth(0);
        jXTreeTable.getColumn(0).setMinWidth(150);
        jXTreeTable.validate();
        //jXTreeTable.getColumn(1).setCellEditor(new MyTableStringDatePicker());
        //jXTreeTable.getColumn(0).setCellEditor(new MyDateCellEditor());
        
        JScrollPane jscr = JCompTools.getTransparentScrollPane(jXTreeTable);
        jscr.validate();
        jpan.add(jscr,cc.xywh(3,4,1,3));
		return jpan;
		
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
		
		
		rootRezept = new DefaultMutableTreeNode( "Rezept???" );
		treeModelRezept = new DefaultTreeModel(rootRezept);
		treeRezept = new JXTree( rootRezept );
		treeRezept.setEditable(true);
		treeRezept.setName("rezeptetree");
		/*
		public DefaultMutableTreeNode rootRdaten;
		public DefaultMutableTreeNode rootPdaten;
		public DefaultMutableTreeNode rootAdaten;
		public DefaultMutableTreeNode rootTdaten;
		public DefaultMutableTreeNode rootGdaten;
		*/
		rootRdaten = new DefaultMutableTreeNode( "<html>"+"<img src='file:///"+Reha.proghome+"icons/refresh.gif'>"+"&nbsp;<b>Rezeptdaten</b></html>" );
		DefaultMutableTreeNode item = new DefaultMutableTreeNode( "Ausstellungsdatum:" );
		DefaultMutableTreeNode item2 = new DefaultMutableTreeNode("");
		item.add(item2);
		rootRdaten.add(item);
		item = new DefaultMutableTreeNode( "Verordnungsart := " );
		item2 = new DefaultMutableTreeNode("");
		item.add(item2);
		rootRdaten.add(item);
		item = new DefaultMutableTreeNode( "Indikationsschlüssel := " );
		item2 = new DefaultMutableTreeNode("");
		item.add(item2);

		rootRdaten.add(item);
		
		((DefaultMutableTreeNode)rootRezept).add(rootRdaten);

		String pat = "<html>"+"<img src='file:///"+Reha.proghome+"icons/personen16.gif'>"+"&nbsp;<b>Patient</b></html>";

		rootPdaten = new DefaultMutableTreeNode( pat );
		item = new DefaultMutableTreeNode( "Name := " );
		item2 = new DefaultMutableTreeNode("");
		item.add(item2);
		rootPdaten.add(item);
		item = new DefaultMutableTreeNode( "Status := " );
		item2 = new DefaultMutableTreeNode("");
		item.add(item2);
		rootPdaten.add(item);
		item = new DefaultMutableTreeNode( "IK der. Vers.Karte := " );
		item2 = new DefaultMutableTreeNode("");
		item.add(item2);
		rootPdaten.add(item);
		item = new DefaultMutableTreeNode( "geboren := " );
		item2 = new DefaultMutableTreeNode("");
		item.add(item2);
		rootPdaten.add(item);
		item = new DefaultMutableTreeNode( "Strasse := " );
		item2 = new DefaultMutableTreeNode("");
		item.add(item2);
		rootPdaten.add(item);
		item = new DefaultMutableTreeNode( "Plz/Ort := " );
		item2 = new DefaultMutableTreeNode("");
		item.add(item2);
		rootPdaten.add(item);
		item = new DefaultMutableTreeNode( "Zuzahlungsstatus" );
		item2 = new DefaultMutableTreeNode("");
		item.add(item2);
		rootPdaten.add(item);
		
		
		((DefaultMutableTreeNode)rootRezept).add(rootPdaten);

		rootAdaten = new DefaultMutableTreeNode( "<html><b>Arzt</b></html>" );
		//rootAdaten = new DefaultMutableTreeNode( "Arzt" );
		item = new DefaultMutableTreeNode( "Arztname := " );
		item2 = new DefaultMutableTreeNode("");
		item.add(item2);
		rootAdaten.add(item);
		item = new DefaultMutableTreeNode( "Betriebsstätte := " );
		item2 = new DefaultMutableTreeNode("");
		item.add(item2);
		rootAdaten.add(item);
		item = new DefaultMutableTreeNode( "LANR := " );
		item2 = new DefaultMutableTreeNode("");
		item.add(item2);
		rootAdaten.add(item);
		
		((DefaultMutableTreeNode)rootRezept).add(rootAdaten);
		
		rootTdaten = new DefaultMutableTreeNode( "<html><b>Abrechnungsfälle</b></html>" );
		((DefaultMutableTreeNode)rootRezept).add(rootTdaten);

		rootGdaten = new DefaultMutableTreeNode( "<html><b>Brutto/Netto</b></html>" );
		((DefaultMutableTreeNode)rootRezept).add(rootGdaten);
		
		
		//treeKasse.getSelectionModel().addTreeSelectionListener(this);
		JScrollPane jscrr = JCompTools.getTransparentScrollPane(treeRezept);
		jscrr.validate();
		/*
		jpan.add(jscrr,cc.xy(3,4));
		treeRezept.expandRow(0);
		treeRezept.expandRow(1);
		treeRezept.repaint();
		*/
		/****/
		tageMod.setColumnIdentifiers(new String[] {"Behandlungstag","Heilmittel","Anzahl","Tarif","Zuzahlung","Rez.Geb.","Unterbrechung","Alt",""});
		tageTbl = new JXTable(tageMod);
		tageTbl.setSortable(false);
		tageTbl.getColumn(8).setMinWidth(0);
		tageTbl.getColumn(8).setMaxWidth(0);
		//TableCellEditor datEdit = new TableCellEditor(new JXDatePicker());
		MyTableStringDatePicker pic = new MyTableStringDatePicker();
		tageTbl.getColumn(0).setCellEditor(pic);
		cmbpreis = new JRtaComboBox(vec_kuerzel,0,1);
		cmbpreis.setActionCommand("preis");
		cmbpreis.addActionListener(this);
		cmbpreis.setVisible(true);

		tageTbl.getColumn(1).setCellEditor(new DefaultCellEditor(cmbkuerzel));
		tageTbl.getColumn(3).setCellEditor(new JXTable.NumberEditor());
		//tageTbl.getColumn(3).setCellEditor(new DblCellEditor());
		tageTbl.getColumn(3).setCellRenderer(new DoubleTableCellRenderer() );

		chkzuzahl = new JCheckBox();
		chkzuzahl.setVerticalAlignment(SwingConstants.CENTER);
		chkzuzahl.setHorizontalAlignment(SwingConstants.CENTER);
		chkzuzahl.setActionCommand("zuzahlung");
		chkzuzahl.setVisible(true);
		chkzuzahl.addActionListener(this);
		tageTbl.getColumn(4).setCellEditor(new DefaultCellEditor(chkzuzahl));

		tageTbl.setEditable(true);
		JScrollPane jscrt = JCompTools.getTransparentScrollPane(tageTbl);
		jscrt.validate();
		/***************************
		Vector xvec =new Vector();
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		try {
			xvec.add(sdf.parseObject((String)"31.12.2009"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		xvec.add("Behandlung");
		xvec.add(Boolean.TRUE);
		xvec.add("3");
		xvec.add("4");
		xvec.add("5");
		xvec.add("6");
		tageMod.addRow((Vector) xvec.clone());
		tageMod.addRow((Vector)xvec.clone());
		tageMod.addRow((Vector)xvec.clone());
		*****************/
		//ermittleAbrechnungsfall();
		tageTbl.validate();
		/****/
		//jpan.add(jscrt,cc.xy(3,6));
		jpan.add(jscrt,cc.xywh(3,4,1,3));
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

		/*
		labs[0].setText(rez_nr.trim()); //Rezeptnummer		
		labs[1].setText(DatFunk.sDatInDeutsch(vec_rez.get(0).get(2))); //Rezeptdatum
		labs[2].setText(vec_pat.get(0).get(7)); //Status
		labs[3].setText(vec_pat.get(0).get(6)); //Versichertennummer
		labs[4].setText(vec_pat.get(0).get(0)); //Nachname
		labs[5].setText(vec_pat.get(0).get(1)); //Vorname
		labs[6].setText(DatFunk.sDatInDeutsch(vec_pat.get(0).get(2))); //Geboren
		*/
	}
	/*****************************************************************************************/
	private void ermittleAbrechnungsfall(){
		vec_tabelle.clear();
		
		Vector<Vector<String>> vectage = RezTools.macheTerminVector( vec_rez.get(0).get(34));
		Vector<Vector<String>> vecabrfaelle = new Vector<Vector<String>>();
		Vector<String> vecabrfall = new Vector<String>();
		vec_tabelle.clear();
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

		for(int i = 0; i < vectage.size();i++){
			splitvec = vectage.get(i).get(3);
			behandlungen = splitvec.split(",");
			if(behandlungen.length > 0 && (!splitvec.trim().equals(""))){
				//Es stehen Behandlungsdaten im Terminblatt;
				//Positionen = length+hbposanzahl;
				//System.out.println("Länge des Feldes = "+behandlungen.length);
				//anzahlbehandlungen = behandlungen.length;
				if(anzahlbehandlungen == 1){
					//System.out.println("Inhalt von Feld[0] = "+behandlungen[0]);
					//System.out.println("Behandlungsanzahl (==1)= "+anzahlbehandlungen);
					//System.out.println("Anzahl-Hausbesuch  (==1)= "+anzahlhb);
				}else if(anzahlbehandlungen > 1){
					//System.out.println("Behandlungsanzahl (!=1)= "+anzahlbehandlungen);
					//System.out.println("Anzahl-Hausbesuch  (!=1)= "+anzahlhb);
				}
				constructTagVector(vectage.get(i).get(0),behandlungen,behandlungen.length,anzahlhb);
				this.tageMod.setRowCount(0);

				if(vec_tabelle.size()>0){
					for(int i2 = 0;i2<vec_tabelle.size();i2++){
						tageMod.addRow((Vector<Object>)vec_tabelle.get(i2).clone());
					}
					tageTbl.setRowSelectionInterval(0,0);
					tageTbl.scrollRowToVisible(0);
				}
				this.tageTbl.validate();

			}else{
				//Es sind keine  Behandlungsformen im Terminblatt verzeichnet;
				//in anzahlbehandlungen steht die tatsächliche Anzahl
				//System.out.println("Keine Behandlungen im Terminblatt = "+anzahlbehandlungen);
				//System.out.println("Anzahl-Hausbesuch  keine Beh. im Terminblatt= "+anzahlhb);
				constructTagVector(vectage.get(i).get(0),null,anzahlbehandlungen,anzahlhb);
				this.tageMod.setRowCount(0);

				if(vec_tabelle.size()>0){
					for(int i2 = 0;i2<vec_tabelle.size();i2++){
						tageMod.addRow((Vector<Object>)vec_tabelle.get(i2).clone());
					}
					tageTbl.setRowSelectionInterval(0,0);
					tageTbl.scrollRowToVisible(0);
				}
				this.tageTbl.validate();
			}
		}
		doGebuehren();
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
		
		boolean jahresWechsel = false;
		if(patU18){
			unter18 = true;
			vollFrei = true;
			amBeginnFrei = true;
			volleZuzahlung = false;
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
				doFreiAb(0,vec_tabelle.size(),false);
			}else if(( (Boolean)u18[0]) && ( (Boolean)u18[2])) {
				//Während der Behandlung 18 geworden, Rezept muß gesplittet werden;
				unter18 = true;
				teilFrei = true;
				amBeginnFrei = true;
				volleZuzahlung = false;
				mitPauschale = false;
				doFreiAb(0,(Integer)u18[1],false);
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
				doFreiAb(0,vec_tabelle.size(),false);
				mitPauschale = false;
				wechselfall = 2;
			}else if( (patVorjahrFrei) && (!patAktuellFrei) ){ //3.Fall
				// im Vorjahr befreit und jetzt zuzahlungspflichtig
				doFreiAb(0,(Integer)newYear[1],false);
				mitPauschale = false;
				wechselfall = 3;
			}else if((!patVorjahrFrei) && (!patAktuellFrei) && (!unter18) ){ //4.Fall
				//weder im Vorjahr noch im aktuellen Jahr befreit
				doFreiAb(0,vec_tabelle.size(),true);
				mitPauschale = true;
				wechselfall = 4;
			}else if((!patVorjahrFrei) && (patAktuellFrei) && (!unter18)){ //5.Fall
				// war nicht im Vorjahr befreit ist aber jetzt befreit
				doFreiAb((Integer) newYear[1],vec_tabelle.size(),true);
				mitPauschale = true;
				wechselfall = 5;
			}
		}else if(!(Boolean)newYear[0]){
			//Das Rezept wurde vollständig im aktuelle Jahr abgearbeitet
			if(patAktuellFrei && (!gebuehrBezahlt)){
				doFreiAb(0,vec_tabelle.size(),false);
				mitPauschale = false;
				neufall = 1;
			}else if(patAktuellFrei && gebuehrBezahlt ){
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
	private void doFreiAb(int von, int bis,boolean pflichtig){
		for(int i = von; i < bis; i++){
			tageMod.setValueAt( pflichtig, i, 4);	
			if(!pflichtig){
				tageMod.setValueAt(0.00, i, 5);	
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
				vecdummy.add("1,00");
				//Preisuntersuchen ob alt oder neu....
				
				//.....
				vecdummy.add(RezTools.getPreisAktFromID(id[i], preisgruppe, preisvec));
				//untersuchen ob befreit
				//.....
				vecdummy.add(Boolean.valueOf(true));
				//untersuchen ob unter 18
				//.....
				
				//untersuchen ob teilweise frei
				//.....
				vecdummy.add(rechneRezGeb(vecdummy.get(3).toString()));
				//System.out.println(vecdummy.clone());
				vec_tabelle.add((Vector<Object>)vecdummy.clone());
			}
		}
	}
	/**************************/
	private String rechneRezGeb(String preis){
		BigDecimal bi_rezgeb = BigDecimal.valueOf(Double.parseDouble(preis.replace(",", ".")));
		bi_rezgeb = bi_rezgeb.divide(BigDecimal.valueOf(Double.parseDouble("10.000")));
		bi_rezgeb  = bi_rezgeb.setScale(2, BigDecimal.ROUND_HALF_UP);
		return	dfx.format(bi_rezgeb).replace(".", ",");
	}
	
	/*****************************************************************************************/
	private void testeTage(){
		Vector<Vector<String>> vectage = RezTools.macheTerminVector( vec_rez.get(0).get(34));
		System.out.println(vectage);
		String[] behandlungen;
		String preisgruppe = vec_rez.get(0).get(41);
		int anzahlbehandlungen = 0;
		boolean hb = false;
		int hbposanzahl = 0;
		int[] ids = {Integer.parseInt(vec_rez.get(0).get(8)),
				Integer.parseInt(vec_rez.get(0).get(9)),
				Integer.parseInt(vec_rez.get(0).get(10)),
				Integer.parseInt(vec_rez.get(0).get(11)) };
		for(int i = 0; i < 4; i++){
			if(ids[0] > 0 ){
				anzahlbehandlungen++;
			}else{
				break;
			}
		}
		if(vec_rez.get(0).get(43).equals("T")){
			hb= true;
			if(RezTools.zweiPositionenBeiHB(preisgruppe)){
				hbposanzahl = 2;
				anzahlbehandlungen+=2;
			}else{
				hbposanzahl = 1;
				anzahlbehandlungen++;
			}
		}

		for(int i = 0; i < vectage.size();i++){
			behandlungen = vectage.get(i).get(3).split(",");
			if(behandlungen.length > 0){
				//Es stehen Behandlungsdaten im Terminblatt;
				//Positionen = length+hbposanzahl;
				anzahlbehandlungen = behandlungen.length+hbposanzahl;
				if(anzahlbehandlungen == 1){
					//foo = new JXTTreeTableNode(vectage.get(i).get(0),true);
					foo = new JXTTreeTableNode("",new AbrFall(vectage.get(i).get(0),"Keine...",1),true);
					//foo.setUserObject(new AbrFall(vectage.get(i).get(0),"Keine..."));
					demoTreeTableModel.insertNodeInto(foo, root, root.getChildCount());
					//xx;
				}else{
					
				}
			}else{
				//Es sind keine  Behandlungsformen im Terminblatt verzeichnet;
				//in anzahlbehandlungen steht die tatsächliche Anzahl
			}
		}
	}
	class AbrFall{
		public Date datum;
		public String bezeichnung;
		public int anzahl = 0;
		public double preis = 0.00;
		public boolean zuzahlung = true;
		public double rezgeb = 0.00;
		public String unterbrechung ="";
		public String sqldatum = "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		public AbrFall(String datum,String bezeichnung,int anzahl){
			try {
				this.datum = sdf.parse(datum);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.bezeichnung = bezeichnung; 
			this.anzahl = anzahl;
		}
	}
	private JScrollPane getDatenBereich(){
		//                                1    2  3   4  5   6  7   8
		FormLayout lay = new FormLayout("10dlu,p,15dlu,p,15dlu,p,15dlu,p",
		//		
				"30dlu,p,5dlu,p,20dlu,p");
		PanelBuilder pb = new PanelBuilder(lay);
		CellConstraints cc = new CellConstraints();
		labs[0] = new JLabel(); //Rezeptnummer
		labs[0].setFont(new Font("Tahoma",Font.PLAIN,16));
		labs[0].setForeground(Color.BLUE);
		pb.add(labs[0],cc.xy(2,2));
		
		labs[1] = new JLabel(); //Rezeptdatum
		labs[1].setFont(new Font("Tahoma",Font.PLAIN,12));
		labs[1].setForeground(Color.BLUE);
		pb.add(labs[1],cc.xy(4,2));
		
		labs[2] = new JLabel(); //Status
		labs[2].setFont(new Font("Tahoma",Font.PLAIN,12));
		labs[2].setForeground(Color.BLUE);
		pb.add(labs[2],cc.xy(6,2));

		labs[3] = new JLabel(); //Versichertennummer
		labs[3].setFont(new Font("Tahoma",Font.PLAIN,12));
		labs[3].setForeground(Color.BLUE);
		pb.add(labs[3],cc.xy(8,2));

		labs[4] = new JLabel(); //Nachname
		labs[4].setFont(new Font("Tahoma",Font.PLAIN,12));
		labs[4].setForeground(Color.BLUE);
		pb.add(labs[4],cc.xy(2,4));

		labs[5] = new JLabel(); //Vorname
		labs[5].setFont(new Font("Tahoma",Font.PLAIN,12));
		labs[5].setForeground(Color.BLUE);
		pb.add(labs[5],cc.xy(4,4));

		labs[6] = new JLabel(); //Geboren
		labs[6].setFont(new Font("Tahoma",Font.PLAIN,12));
		labs[6].setForeground(Color.BLUE);
		pb.add(labs[6],cc.xy(6,4));

		JScrollPane jscr = JCompTools.getTransparentScrollPane(pb.getPanel());
		jscr.validate();
		return jscr;
		
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
		".spalte2{color:#FF0000;}"+
		".spalte2{color:#FF0000;}"+
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
		vec_rez.get(0).get(44)+
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
	                return sdf.format(o.datum);
	            case 1:
	                return o.bezeichnung;
	            case 2:
	                return o.anzahl;
	            case 3:
	                return o.preis;
	            case 4:
	            	return o.zuzahlung;
	            case 5:
	                return dfx.format(o.rezgeb);
	            case 6:
	            	return o.unterbrechung;
	            	
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
            	try {
					o.datum = sdf.parse( ((String) value) );
				} catch (ParseException e) {
					e.printStackTrace();
				}
            	o.sqldatum = DatFunk.sDatInSQL(((String) value));
            	break;
            case 1:
            	o.bezeichnung = ((String) value);
            	break;
            case 2:
            	o.anzahl = ((Integer)value);
            	break;
            case 3:
            	o.preis =  Double.parseDouble(dfx.format( ((Double)value) ).replaceAll(",", ".") ) ;
            	o.rezgeb = (Double) ((Double)value)/100.00*10.00;
            	jXTreeTable.repaint();
            	break;
            case 4:
            	o.zuzahlung = ((Boolean)value);
            	break;
            case 5:
            	o.rezgeb = ((Double)value);
            	break;
            case 6:
            	o.unterbrechung = ((String)value);
            	break;
	
            }
        }
 
        public boolean isCellEditable(java.lang.Object node,int column){
            switch (column) {
            case 0:
                return true;
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
                return Date.class;
            case 1:
                return String.class;
            case 2:
                return Integer.class;
            case 3:
                return Double.class;
            case 4:
                return Boolean.class;
            case 5:
                return Double.class;
            case 6:
                return String.class;
            case 7:
                return String.class;
            default:
                return Object.class;
            }
        }
 
        public int getColumnCount() {
            return 8;
        }
 
 
        public String getColumnName(int column) {
            switch (column) {
            case 0:
                return "Behandlungstag";
            case 1:
                return "Heilmittel";
            case 2:
                return "Anzahl";
            case 3:
                return "Preis";
            case 4:
                return "Zuzahlung";
            case 5:
                return "Rez.Gebühr";
            case 6:
                return "Unterbrech.";
            case 7:
                return "";
                
            default:
                return "Column " + (column + 1);
            }
        }
    }
 
    private static class JXTTreeTableNode extends DefaultMutableTreeTableNode {
    	private boolean enabled = false;
    	private AbrFall abr = null;
    	public JXTTreeTableNode(String name, AbrFall abr ,boolean enabled){
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
		if(cmd.equals("cmbkuerzel")){
			System.out.println(arg0);
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					System.out.println("Wert von cmbKuerzel = "+cmbkuerzel.getValueAt(1)+
							" / DisplayWert = "+cmbkuerzel.getValueAt(0)+ " Selektierte Tabellenzeile = "+
							tageTbl.getSelectedRow()	);
				}
			});
			return;
		}
		if(cmd.equals("zuzahlung")){
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					System.out.println("Wert der CheckBox = "+chkzuzahl.isSelected()+ " Selektierte Tabellenzeile = "+
							tageTbl.getSelectedRow()	);
				}
			});

		}
		
		
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

	public Class getColumnClass(int columnIndex) {
		   if(columnIndex==0)
			   return Date.class;
		   if(columnIndex==1)
			   return String.class;
		   if(columnIndex==2){
			   return String.class;
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