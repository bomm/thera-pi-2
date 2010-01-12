package abrechnung;

import hauptFenster.Reha;
import hauptFenster.UIFSplitPane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.SwingWorker;
import javax.swing.event.CellEditorListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableModel;


import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import sqlTools.SqlInfo;
import stammDatenTools.RezTools;
import systemEinstellungen.SystemConfig;
import systemTools.JCompTools;
import systemTools.StringTools;
import terminKalender.DatFunk;
import terminKalender.ParameterLaden;


public class AbrechnungRezept extends JXPanel implements HyperlinkListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8387184772704779192L;
	private Abrechnung1 eltern;
	JToolBar tb = null;
	JButton[] tbbuts = {null,null,null,null};
	JLabel[] labs = {null,null,null,null,null,null,null,null,null,null,
					null,null,null,null,null,null,null,null,null,null,
					null,null,null,null,null,null,null,null,null,null};
	
	Vector<Vector<String>>vec_rez = null;
	Vector<Vector<String>>vec_pat = null;
	Vector<Vector<String>>vec_term = null;
	
	JXTree treeRezept = null;
	
	public DefaultMutableTreeNode rootRezept;
	public DefaultMutableTreeNode rootRdaten;
	public DefaultMutableTreeNode rootPdaten;
	public DefaultMutableTreeNode rootAdaten;
	public DefaultMutableTreeNode rootTdaten;
	public DefaultMutableTreeNode rootGdaten;
	public DefaultMutableTreeNode rootStammdaten;
	public DefaultTreeModel treeModelRezept;
	
	Vector preisvec = null;
	
	public JXTable tageTbl = null;
	public MyTageTableModel tageMod = new MyTageTableModel();
	
	private UIFSplitPane jSplitOU = null;
	private String[] voArt = {"Erstverordnung","Folgeverordnung","Folgeverordn. außerhalb d. Regelf."};
	
	JEditorPane htmlPane = null;
	
	private static JXTTreeTableNode root = null;
	private TageTreeTableModel demoTreeTableModel = null;
	private JXTreeTable jXTreeTable = null;

	
	public AbrechnungRezept(Abrechnung1 xeltern){
		eltern = xeltern;
		setLayout(new BorderLayout());
		//add(getEinzelRezPanel(),BorderLayout.CENTER);
		add(getSplitPane(),BorderLayout.CENTER);
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
		jSplitOU.setDividerLocation(350);
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
				setPreisVec(xpreis);
				return null;
			}
		}.execute();
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
		
		root = new JXTTreeTableNode("root", true);
        demoTreeTableModel = new TageTreeTableModel(root);

        jXTreeTable = new JXTreeTable(demoTreeTableModel);
        jXTreeTable.setOpaque(false);
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
		tageMod.setColumnIdentifiers(new String[] {"Beh.Tag","Behandlung","Anzahl","Einzelpreis","Zuzahlung","Zuz.Betrag",""});
		tageTbl = new JXTable(tageMod);
		tageTbl.setSortable(false);
		tageTbl.getColumn(6).setMinWidth(0);
		tageTbl.getColumn(6).setMaxWidth(0);
		//TableCellEditor datEdit = new TableCellEditor(new JXDatePicker());
		tageTbl.getColumn(0).setCellEditor(new MyDateCellEditor());
		
		JScrollPane jscrt = JCompTools.getTransparentScrollPane(tageTbl);
		jscrt.validate();
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

		vec_pat = SqlInfo.holeFelder("select  t1.n_name,t1.v_name,t1.geboren,t1.strasse,t1.plz,t1.ort,"+
				"t1.v_nummer,t1.kv_status,t1.kv_nummer,t2.nachname,t2.bsnr,t2.arztnum,t3.kassen_nam1 from pat5 t1,arzt t2,kass_adr t3 where t1.pat_intern='"+
				vec_rez.get(0).get(0)+"' AND t2.id ='"+vec_rez.get(0).get(16)+"' AND t3.id='"+vec_rez.get(0).get(37)+"' LIMIT 1");
		//System.out.println("PatArztVektor = "+vec_pat);
		//System.out.println(rootRezept.getRoot().toString());
		//Object rootNode = treeRezept.getModel().getRoot();
		//((DefaultMutableTreeNode)rootNode).setUserObject(rez_nr.trim());
		//treeRezept.repaint();
		testeTage();
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
	private void testeTage(){
		Vector<Vector<String>> vectage = RezTools.macheTerminVector( vec_rez.get(0).get(34));
		System.out.println(vectage);
		String[] behandlungen;
		for(int i = 0; i < vectage.size();i++){
			behandlungen = vectage.get(i).get(3).split(",");
			if(behandlungen.length > 0){
				//Es stehen Behandlungsdaten im Terminblatt;
			}else{
				//Es sind keine  Behandlungsformen im Terminblatt verzeichnet;
			}
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
		"<th rowspan=\"5\" valign=\"top\"><img src='file:///"+Reha.proghome+"icons/kontact_contacts.png' width=52 height=52></th>" +
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
		"</td><td class=\"spalte2\" align=\"left\">"+
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
		"<th rowspan=\"3\" valign=\"top\"><img src='file:///"+Reha.proghome+"icons/system-users.png' width=52 height=52></th>" +
		"<td class=\"spalte1\" align=\"right\">"+
		"verordnender Arzt"+
		"</td><td class=\"spalte2\" align=\"left\">"+
		StringTools.EGross(vec_pat.get(0).get(9))+
		"</td>"+
		"</tr>"+
		/*******/
		"<tr>"+
		"<td class=\"spalte1\" align=\"right\">"+
		"Betriebsstätte"+
		"</td><td class=\"spalte2\" align=\"left\">"+
		(vec_pat.get(0).get(10).trim().equals("") ? "999999999" : vec_pat.get(0).get(10).trim())+
		"</td>"+
		"</tr>"+
		/*******/
		"<tr>"+
		"<td class=\"spalte1\" align=\"right\">"+
		"LANR"+
		"</td><td class=\"spalte2\" align=\"left\">"+
		(vec_pat.get(0).get(11).trim().equals("") ? "999999999" : vec_pat.get(0).get(11).trim())+
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
	      }
	}
	
/*************************
 * 
 * 	
 */
	private static class TageTreeTableModel extends DefaultTreeTableModel {
        public TageTreeTableModel(JXTTreeTableNode jXTTreeTableNode) {
            super(jXTTreeTableNode);
        }
        
     
        public Object getValueAt(Object node, int column) {
        	JXTTreeTableNode jXTreeTableNode = (JXTTreeTableNode) node;
            
            Object o = jXTreeTableNode.getUserObject();
 
            switch (column) {
	            case 0:
	                return o.toString();
	            case 1:
	                return o.toString();
	            case 2:
	                return o.hashCode() % 2 == 0;
	            case 3:
	                return new Date(o.hashCode());
	            case 4:
	            	return jXTreeTableNode.isEnabled();
            }
            return super.getValueAt(node, column);
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
            default:
                return false;
            }
        	
        }
        
        public Class<?> getColumnClass(int column) {
            switch (column) {
            case 0:
                return String.class;
            case 1:
                return Integer.class;
            case 2:
                return Boolean.class;
            case 3:
                return Date.class;
            case 4:
                return Boolean.class;    
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
    	
    	public JXTTreeTableNode(String name, boolean enabled){
    		super(name);
    		this.enabled = enabled;
    	}
 
		public boolean isEnabled() {
			return enabled;
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
			   return Boolean.class;
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
	        }else if(col == 3){
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

