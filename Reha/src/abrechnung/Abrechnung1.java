package abrechnung;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import hauptFenster.Reha;
import hauptFenster.UIFSplitPane;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;
import org.jdesktop.swingx.treetable.TreeTableNode;







import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import sqlTools.SqlInfo;
import systemEinstellungen.SystemConfig;
import systemTools.JCompTools;
import systemTools.JRtaComboBox;
import systemTools.JRtaRadioButton;
import systemTools.StringTools;
import terminKalender.DatFunk;

import RehaInternalFrame.JAbrechnungInternal;

import events.PatStammEvent;
import events.PatStammEventListener;

public class Abrechnung1 extends JXPanel implements PatStammEventListener,ActionListener,TreeSelectionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3580427603080353812L;
	private JAbrechnungInternal jry;
	private UIFSplitPane jSplitLR = null;
	
	final String plus = "+";
	final String EOL = "'\n";
	final String SOZ = "?";
	public String abzurechnendeKassenID = "";
	String ik_kasse,ik_kostent,ik_nutzer,ik_physika,ik_papier,ik_email;
	String aktEsol;
	String aktDfue;
	String aktRechnung;
	String[] diszis = {"KG","MA","ER","LO"};

	/*******Controls für die linke Seite*********/
	ButtonGroup bg = new ButtonGroup();
	JRtaRadioButton[] rbLinks = {null,null,null,null};
	JButton[] butLinks = {null,null,null,null};
	JRtaComboBox cmbDiszi = null;
	JXTree treeKasse = null;
	
//	public DefaultMutableTreeNode rootKasse;
//	public DefaultTreeModel treeModelKasse;

	public JXTTreeNode rootKasse;
	public KassenTreeModel treeModelKasse;
	public JXTTreeNode aktuellerKnoten;
	public JXTTreeNode aktuellerKassenKnoten;
	public int kontrollierteRezepte;
	
	public StringBuffer positionenBuf = new StringBuffer();
	public StringBuffer unbBuf = new StringBuffer();
	public StringBuffer unzBuf = new StringBuffer();
	public StringBuffer gesamtBuf = new StringBuffer();
	public int positionenAnzahl = 0;
	
	Double[] preis00 = {0.00,0.00,0.00};
	Double[] preis11 = {0.00,0.00,0.00};
	Double[] preis31 = {0.00,0.00,0.00};
	Double[] preis51 = {0.00,0.00,0.00};
	DecimalFormat dfx = new DecimalFormat( "0.00" );	
	
	Vector<String> existiertschon = new Vector<String>();	
	/*******Controls für die rechte Seite*********/
	AbrechnungRezept abrRez = null;
	
	public String aktuellerPat = "";
	public Abrechnung1(JAbrechnungInternal xjry){
		super();
		this.setJry(xjry);
		setLayout(new BorderLayout());
		jSplitLR =  UIFSplitPane.createStrippedSplitPane(JSplitPane.HORIZONTAL_SPLIT,
        		getLeft(),
        		getRight()); 
		jSplitLR.setDividerSize(7);
		jSplitLR.setDividerBorderVisible(true);
		jSplitLR.setName("BrowserSplitLinksRechts");
		jSplitLR.setOneTouchExpandable(true);
		jSplitLR.setDividerLocation(230);
		add(jSplitLR,BorderLayout.CENTER);
		
	}
	/**********
	 * 
	 * 
	 * 
	 * Linke Seite
	 */
	private JScrollPane getLeft(){
		FormLayout lay = new FormLayout("5dlu,fill:0:grow(1.0),5dlu",
				//1   2  3   4  5    6  7    8  9      10             11
				"5dlu,p,5dlu,p,15dlu,p,20dlu,p,15dlu,fill:0:grow(1.0),5dlu");
		PanelBuilder pb = new PanelBuilder(lay);
		CellConstraints cc = new CellConstraints();
		pb.getPanel().setBackground(Color.WHITE);
		
		pb.addLabel("Heilmittel auswählen",cc.xy(2,2));
		cmbDiszi = new JRtaComboBox(new String[] {"Physio-Rezept","Massage/Lymphdrainage-Rezept","Ergotherapie-Rezept","Logopädie-Rezept"});
		cmbDiszi.setSelectedItem(SystemConfig.initRezeptKlasse);
		cmbDiszi.setActionCommand("einlesen");
		
		pb.add(cmbDiszi,cc.xy(2,4));
		/*
		butLinks[0] = new JButton("Abrechnungsdaten einlesen");
		butLinks[0].setActionCommand("einlesen");
		butLinks[0].addActionListener(this);
		pb.addLabel("",cc.xy(2,6));
		pb.add(butLinks[0],cc.xy(2,8));
		*/
		//rootKasse = new DefaultMutableTreeNode( "Abrechnung für Kasse..." );
		//rootKasse = new DefaultMutableTreeNode( "Abrechnung für Kasse..." );
		rootKasse = new JXTTreeNode(new KnotenObjekt("Abrechnung für Kasse...","",false),true);
		treeModelKasse = new KassenTreeModel((JXTTreeNode) rootKasse);

		treeKasse = new JXTree(treeModelKasse);
		treeKasse.setModel(treeModelKasse);
		treeKasse.setName("kassentree");
		treeKasse.getSelectionModel().addTreeSelectionListener(this);
		treeKasse.setCellRenderer(new MyRenderer(new ImageIcon(Reha.proghome+"icons/Haken_klein.gif")));
		JScrollPane jscrk = JCompTools.getTransparentScrollPane(treeKasse);
		pb.add(jscrk,cc.xy(2, 6));
		
		doEinlesen();
		
		pb.getPanel().validate();
		JScrollPane jscr = JCompTools.getTransparentScrollPane(pb.getPanel());
		jscr.validate();
		cmbDiszi.addActionListener(this);
		return jscr;
	}
	private JXPanel getRight(){
		this.abrRez = new AbrechnungRezept(this);
		return abrRez;
	}

	@Override
	public void patStammEventOccurred(PatStammEvent evt) {
		// TODO Auto-generated method stub
		
	}

	public void setJry(JAbrechnungInternal jry) {
		this.jry = jry;
	}

	public JAbrechnungInternal getJry() {
		return jry;
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		String cmd = arg0.getActionCommand();
		if(cmd.equals("einlesen")){
			//rootKasse.removeAllChildren();
			String[] reznr = {"KG","MA","ER","LO"};
			abrRez.setKuerzelVec(reznr[cmbDiszi.getSelectedIndex()]);
			if(abrRez.rezeptSichtbar){
				abrRez.setRechtsAufNull();
	    		aktuellerPat = "";
			}
			doEinlesen();
			//setPreisVec(cmbDiszi.getSelectedIndex());
		}
		
	}
	private void setPreisVec(int pos){
		String[] reznr = {"KG","MA","ER","LO"};
		abrRez.setPreisVec(reznr[pos]);
	}
	
	/*********
	 * Einlesen der abrechnungsdaten
	 */
	private void doEinlesen(){
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				try{
					existiertschon.clear();
					String dsz = diszis[cmbDiszi.getSelectedIndex()];
					
					String cmd = "select name1,ikktraeger from fertige where rezklasse='"+dsz+"' ORDER BY ikktraeger";

					Vector <Vector<String>> vecKassen = SqlInfo.holeFelder(cmd);
					System.out.println("Insgesamt fertige Rezepte der Disziplin "+dsz+" = "+vecKassen.size());
					if(vecKassen.size() <= 0){
						kassenBaumLoeschen();
						return null;
					}
					kassenBaumLoeschen();
					treeKasse.setEnabled(true);
					String kas = vecKassen.get(0).get(0).trim().toUpperCase();
					String ktraeger = vecKassen.get(0).get(1).trim();
					
					existiertschon.add(ktraeger);

					int aeste = 0;					
					astAnhaengen(kas,ktraeger);
					rezepteAnhaengen(0);
					aeste++;
					
					



					for(int i = 0; i < vecKassen.size();i++){
						//System.out.println(ktraeger);
						//System.out.println(vecKassen.get(i).get(1));
						if(! existiertschon.contains(vecKassen.get(i).get(1).trim().toUpperCase())){
							kas = vecKassen.get(i).get(0).trim().toUpperCase();
							ktraeger = vecKassen.get(i).get(1);
							existiertschon.add(ktraeger);
							astAnhaengen(kas,ktraeger);
							rezepteAnhaengen(aeste);
							aeste++;

							treeKasse.repaint();
						}
					}
					treeKasse.validate();
					treeKasse.setRootVisible(true);
					treeKasse.expandRow(0);
					treeKasse.repaint();
				}catch(Exception ex){
					ex.printStackTrace();
				}
				return null;
			}
		}.execute();
	}
	private void rezepteAnhaengen(int knoten){
		String ktraeger = ((JXTTreeNode)rootKasse.getChildAt(knoten)).knotenObjekt.ktraeger;
		String dsz = diszis[cmbDiszi.getSelectedIndex()];
		String cmd = "select rez_nr,pat_intern,ediok from fertige where rezklasse='"+dsz+"' AND ikktraeger='"+
		ktraeger+"' ORDER BY pat_intern";

		Vector <Vector<String>> vecKassen = SqlInfo.holeFelder(cmd);

		JXTTreeNode node = (JXTTreeNode) rootKasse.getChildAt(knoten);
		JXTTreeNode treeitem = null;
		
		JXTTreeNode meinitem = null;
		for(int i = 0;i<vecKassen.size();i++){
			System.out.println("In Rezeptanhängen "+i);
			cmd = "select n_name from pat5 where pat_intern='"+
				vecKassen.get(i).get(1)+"' LIMIT 1";

			String name = SqlInfo.holeFelder(cmd).get(0).get(0);

			KnotenObjekt rezeptknoten = new KnotenObjekt(vecKassen.get(i).get(0)+"-"+name,
					vecKassen.get(i).get(0),
					(vecKassen.get(i).get(2).equals("T")? true : false));
			rezeptknoten.ktraeger = ktraeger;
			rezeptknoten.pat_intern = vecKassen.get(i).get(1);
			meinitem = new JXTTreeNode(rezeptknoten,true);

			treeModelKasse.insertNodeInto(meinitem,node,node.getChildCount());
			treeKasse.validate();
		}

	}
	private void astAnhaengen(String ast,String ktraeger){
		KnotenObjekt knoten = new KnotenObjekt(ast,"",false);
		knoten.ktraeger = ktraeger;
		JXTTreeNode node = new JXTTreeNode(knoten,true);
		treeModelKasse.insertNodeInto(node, rootKasse, rootKasse.getChildCount());
		treeKasse.validate();
	}
	private void kassenBaumLoeschen(){
		try{
		int childs;	
		while( (childs=rootKasse.getChildCount()) > 0){
			System.out.println("HauptKnoten überig = "+childs);
			treeModelKasse.removeNodeFromParent((JXTTreeNode) ((JXTTreeNode) treeModelKasse.getRoot()).getChildAt(0));
		}
		treeKasse.validate();
		treeKasse.repaint();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	/*******************************************/
	private void doKassenTreeAuswerten(KnotenObjekt node){
			//Rezept ausgewählt
			setCursor(new Cursor(Cursor.WAIT_CURSOR));
			if(! this.abrRez.setNewRez(node.rez_num,node.fertig) ){
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				return;
			}
				

			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			return;

	}
	/*******************************************/	
	private void doAufraeumen(){
		butLinks[0].removeActionListener(this);
		cmbDiszi.removeActionListener(this);
		treeKasse.getSelectionModel().removeTreeSelectionListener(this);
	}
	@Override
	public void valueChanged(TreeSelectionEvent arg0) {
		//TreePath path = arg0.getNewLeadSelectionPath();
    	TreePath tp =  treeKasse.getSelectionPath();
		kontrollierteRezepte = 0;
    	if(tp==null){

    		return;
    	}
    	JXTTreeNode node = (JXTTreeNode) tp.getLastPathComponent();
    	String rez_nr = ((JXTTreeNode)node).knotenObjekt.rez_num;
    	if(!rez_nr.trim().equals("")){
    		aktuellerKnoten = node;
    		doKassenTreeAuswerten(node.knotenObjekt);
    		aktuellerPat = node.knotenObjekt.pat_intern;
    		aktuellerKassenKnoten =(JXTTreeNode) ((JXTTreeNode)aktuellerKnoten).getParent();
    	}else{
    		abrRez.setRechtsAufNull();
    		aktuellerKnoten = (JXTTreeNode)node;
    		System.out.println("AktuellerKnoten = "+aktuellerKnoten);
    		if(aktuellerKnoten.getParent() != null){
    			if(((JXTTreeNode)aktuellerKnoten.getParent()).isRoot()){
    	    		aktuellerKassenKnoten =(JXTTreeNode) ((JXTTreeNode)aktuellerKnoten);
    			}
        		System.out.println("Pfad zu Parent = "+new TreePath(aktuellerKnoten.getParent()).toString());    			
    		}else{
    			aktuellerKassenKnoten = null;
    		}
    		aktuellerPat = "";
    		
    	}
    	if(aktuellerKassenKnoten != null){
        	new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					int lang = aktuellerKassenKnoten.getChildCount();
					for(int i = 0; i < lang;i++){
						if( ((JXTTreeNode)aktuellerKassenKnoten.getChildAt(i)).knotenObjekt.fertig ){
							kontrollierteRezepte++;
						}
					}
					System.out.println("Es gibt insgesamt "+kontrollierteRezepte+" Rezepte die für diese Kasse abgerechnet werden können");
					return null;
				}
        		
        	}.execute();
    	}


	}
	/**************************************************/
	public void starteAbrechnung(){
		if(aktuellerKassenKnoten==null){
			JOptionPane.showMessageDialog(null, "Keine Kasse für die Abrechnung ausgewählt!");
			return;
		}
		if(kontrollierteRezepte <=0){
			JOptionPane.showMessageDialog(null, "Keine Kasse für die Abrechnung ausgewählt!");
			return;
			
		}
		preis00 = setzePreiseAufNull(preis00);
		preis11 = setzePreiseAufNull(preis11);
		preis31 = setzePreiseAufNull(preis31);
		preis51 = setzePreiseAufNull(preis51);
		positionenBuf.setLength(0);positionenBuf.trimToSize();
		unbBuf.setLength(0);unbBuf.trimToSize();
		unzBuf.setLength(0);unzBuf.trimToSize();
		gesamtBuf.setLength(0);gesamtBuf.trimToSize();
		positionenAnzahl = 0;
		holeEdifact();
		macheKopfDaten();
		macheEndeDaten();
		gesamtBuf.append(unbBuf.toString());
		gesamtBuf.append(positionenBuf.toString());
		gesamtBuf.append(unzBuf.toString());
		System.out.println(gesamtBuf.toString());
		System.out.println("Anzahl Positonen (reine Abrechnugsdaten) = "+positionenAnzahl);
		
	}
	/*************************************************/
	private void macheEndeDaten(){
		String zeilenzahl = StringTools.fuelleMitZeichen(Integer.toString(positionenAnzahl+5), "0", true, 6);  
		unzBuf.append("UNT"+plus+zeilenzahl+plus+"00002"+EOL);
		unzBuf.append("UNZ"+plus+"000002"+plus+aktDfue+EOL);
	}
	private void macheKopfDaten(){
		String cmd = "select ik_kasse,ik_kostent,ik_nutzer,ik_physika,ik_papier,email1 from kass_adr where id='"+abzurechnendeKassenID+"'";
		Vector<Vector<String>> vec = SqlInfo.holeFelder(cmd);
		ik_kasse = vec.get(0).get(0);
		ik_kostent = vec.get(0).get(1);
		ik_nutzer = vec.get(0).get(2);
		ik_physika = vec.get(0).get(3);
		ik_papier = vec.get(0).get(4);
		ik_email = vec.get(0).get(5);
		aktEsol = StringTools.fuelleMitZeichen(Integer.toString(SqlInfo.erzeugeNummerMitMax("esol", 999)), "0", true, 3);
		aktDfue = StringTools.fuelleMitZeichen(Integer.toString(SqlInfo.erzeugeNummerMitMax("dfue", 99999)), "0", true, 5);
		aktRechnung = Integer.toString(SqlInfo.erzeugeNummer("rnr"));
		System.out.println(aktEsol + "  - "+aktDfue);
		unbBuf.append("UNB+UNOC:3+"+Reha.aktIK+plus+ik_nutzer+plus);
		unbBuf.append(getEdiDatumFromDeutsch(DatFunk.sHeute())+":"+getEdiTimeString()+plus);
		unbBuf.append(aktDfue+plus+"B"+plus+"SL"+Reha.aktIK.substring(2,8)+"S01"+plus+"2"+EOL);
		unbBuf.append("UNH+00001+SLGA:06:0:0"+EOL);
		unbBuf.append("FKT+01"+plus+plus+Reha.aktIK+plus+ik_kostent+plus+ik_kasse+plus+Reha.aktIK+EOL);
		unbBuf.append("REC"+plus+aktRechnung+":0"+plus+getEdiDatumFromDeutsch(DatFunk.sHeute())+plus+"1"+EOL);
		unbBuf.append("UST"+plus+SystemConfig.hmFirmenDaten.get("Steuernummer")+plus+"J"+EOL);
		unbBuf.append("GES"+plus+"00"+plus+dfx.format(preis00[0])+plus+dfx.format(preis00[1])+plus+dfx.format(preis00[2])+EOL);
		unbBuf.append("GES"+plus+"11"+plus+dfx.format(preis11[0])+plus+dfx.format(preis11[1])+plus+dfx.format(preis11[2])+EOL);
		unbBuf.append("GES"+plus+"31"+plus+dfx.format(preis31[0])+plus+dfx.format(preis31[1])+plus+dfx.format(preis31[2])+EOL);
		unbBuf.append("GES"+plus+"51"+plus+dfx.format(preis51[0])+plus+dfx.format(preis51[1])+plus+dfx.format(preis51[2])+EOL);
		unbBuf.append("NAM"+plus+SystemConfig.hmFirmenDaten.get("Ikbezeichnung")+plus+
				SystemConfig.hmFirmenDaten.get("Anrede").trim()+" "+
				SystemConfig.hmFirmenDaten.get("Nachname").trim()+plus+SystemConfig.hmFirmenDaten.get("Telefon")+EOL);
		unbBuf.append("UNT+000010+00001"+EOL);
		unbBuf.append("UNH+00002+SLGA:06:0:0"+EOL);
		unbBuf.append("FKT+01"+plus+plus+Reha.aktIK+plus+ik_kostent+plus+ik_kasse+EOL);
		unbBuf.append("REC"+plus+aktRechnung+":0"+plus+getEdiDatumFromDeutsch(DatFunk.sHeute())+plus+"1"+EOL);
		getEdiTimeString();
	}
	/*************************************************/	
	private Double[] setzePreiseAufNull(Double[] preis){
		preis[0] = 0.00;
		preis[1] = 0.00;
		preis[2] = 0.00;
		return preis;
	}
	private String getEdiDatumFromDeutsch(String deutschDat){
		if(deutschDat.trim().length()<10){
			return "";
		}
		return deutschDat.substring(6)+deutschDat.substring(3,5)+deutschDat.substring(0,2);
	}
	private String getEdiTimeString(){
		Date date = new Date();
		String[] datesplit = date.toString().split(" ");
		return datesplit[3].substring(0,2)+datesplit[3].substring(3,5);
	}
	/*************************************************/
	private void holeEdifact(){
		int lang = aktuellerKassenKnoten.getChildCount();
		JXTTreeNode node;
		Vector<Vector<String>> vec;
		for(int i = 0; i < lang;i++){
			node = (JXTTreeNode) aktuellerKassenKnoten.getChildAt(i);
			if(node.knotenObjekt.fertig){
				vec = SqlInfo.holeFelder("select edifact from fertige where rez_nr='"+(String) node.knotenObjekt.rez_num+"'");
				try{
					if(i==0){
						abzurechnendeKassenID = holeAbrechnungsKasse(vec.get(0).get(0));
					}
					anhaengenEdifact(vec.get(0).get(0));
				}catch(Exception ex){}
			}
		}
		/*
		for(int i = 0; i < 3;i++){
			System.out.println("Gesamt   = "+preis00[i]);
			System.out.println("Status 1 = "+preis11[i]);
			System.out.println("Status 3 = "+preis51[i]);
			System.out.println("Status 5 = "+preis51[i]);
			System.out.println("**********************");
		}
		*/

	}
	/*************************************************/
	private String holeAbrechnungsKasse(String edifact){
		String[] komplett = edifact.split("\n");
		String[] zeile1 = komplett[0].split(":");
		return zeile1[zeile1.length-1].split("=")[1];
	}
	/*************************************************/
	private void anhaengenEdifact(String string){
		String[] edi = string.split("\n");
		String[] preise = edi[0].split(":");
		String status = "";
		try{
			status = edi[4].split("\\+")[2];
		}catch(Exception ex){
			status = "10001";
		}
		for(int i = 4; i < edi.length;i++){
			positionenBuf.append(edi[i]+"\n");
			positionenAnzahl++;
		}
		if(status.startsWith("1")){
			preis11 = addierePreise(preis11,edi[edi.length-1]);
		}else if(status.startsWith("3")){
			preis31 = addierePreise(preis31,edi[edi.length-1]);
		}else if(status.startsWith("5")){
			preis51 = addierePreise(preis51,edi[edi.length-1]);
		}
	}
	/*************************************************/
	private Double[] addierePreise(Double[] preis,String zeile){
		String[] zahlen = zeile.split("\\+");
		Double brutto = Double.parseDouble(zahlen[1].replace(",", "."));
		Double zuzahl  = Double.parseDouble(zahlen[2].replace(",", "."));
		preis[1] = preis[1]+ brutto;
		preis[2] = preis[2]+ zuzahl;
		preis[0] = preis[0] + (brutto-zuzahl);
		preis00[0] = preis00[0] + (brutto-zuzahl);
		preis00[1] = preis00[1] + (brutto);
		preis00[2] = preis00[2] + (zuzahl);
		return preis;
	}
	/*************************************************/	
	public void setRezeptOk(boolean ok){
    	treeKasse.getSelectionCount();
    	TreePath path = treeKasse.getSelectionPath();
    	JXTTreeNode node = (JXTTreeNode) path.getLastPathComponent();
    	((KnotenObjekt)node.getUserObject()).fertig = ok;
    	if(ok){
    		kontrollierteRezepte++;
    	}else{
    		kontrollierteRezepte--;
    	}
    	treeKasse.repaint();
	}
	/***************************************/
	private static class JXTTreeNode extends DefaultMutableTreeNode {
    	private boolean enabled = false;
    	private KnotenObjekt knotenObjekt = null;
    	public JXTTreeNode(KnotenObjekt obj,boolean enabled){
    		super();
    		this.enabled = enabled;
   			this.knotenObjekt = obj;
   			if(obj != null){
   				this.setUserObject(obj);
   			}
    	}
 
		public boolean isEnabled() {
			return enabled;
		}
		
		public KnotenObjekt getObject(){
			return knotenObjekt;
		}
    }
	/***************************************/	
	class KnotenObjekt{
		public String titel;
		public boolean fertig;
		public String rez_num;
		public String ktraeger;
		public String pat_intern;
		public String entschluessel;

		public KnotenObjekt(String titel,String rez_num,boolean fertig){
			this.titel = titel;
			this.fertig = fertig;
			this.rez_num = rez_num;
		}
	}
	/*************************************/
	private class KassenTreeModel extends DefaultTreeModel {
		 public KassenTreeModel(JXTTreeNode node) {
	            super((TreeNode) node);
	        }
		 public Object getValueAt(Object node, int column) {
			 JXTTreeNode jXnode = (JXTTreeNode) node;

	        	KnotenObjekt  o = null;
	        	o =  (KnotenObjekt) jXnode.getUserObject();
	        	switch (column) {
            	case 0:
            		return o.titel;
            	case 1:
            		return o.fertig;
            		
	        	}
	        	return jXnode.getObject().titel;
		 } 
		 public int getColumnCount() {
	            return 3;
	        }
	      public void setValueAt(Object value, Object node, int column){
	    	  JXTTreeNode jXnode = (JXTTreeNode) node;
	    	  KnotenObjekt  o;
	    	  o = jXnode.getObject();
	    	  switch (column) {
	            case 0:
					o.titel =((String) value) ;
					break;
	            case 1:
					o.fertig =((Boolean) value) ;
	            	break;
	    	  }
	      } 
	      public Class<?> getColumnClass(int column) {
	            switch (column) {
	            case 0:
	                return String.class;
	            case 1:
	                return Boolean.class;
	            }
	            return Object.class;
	      }      
	}
	/*****************************************/
	private class MyRenderer extends DefaultTreeCellRenderer {
		Icon fertigIcon;

		public MyRenderer(Icon icon) {
		fertigIcon = icon;
		}

		public Component getTreeCellRendererComponent(
		JTree tree,
		Object value,
		boolean sel,
		boolean expanded,
		boolean leaf,
		int row,
		boolean hasFocus) {

		super.getTreeCellRendererComponent(
		tree, value, sel,
		expanded, leaf, row,
		hasFocus);
		KnotenObjekt o = ((JXTTreeNode)value).knotenObjekt;
		if (leaf && istFertig(value)) {
			setIcon(fertigIcon);
			this.setText(o.titel);
			setToolTipText("Verordnung "+o.rez_num+" kann dirket abgerechnet werden.");
		} else {
			setToolTipText(null);
			this.setText(o.titel);
		}
		return this;
		}	

	}
	protected boolean istFertig(Object value) {
		DefaultMutableTreeNode node =
		(DefaultMutableTreeNode)value;
		KnotenObjekt fertig =
		(KnotenObjekt)(node.getUserObject());
		boolean istfertig = fertig.fertig;
		if(istfertig){
			return true;
		}
		return false;
	}
}
