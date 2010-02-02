package abrechnung;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
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
		TreePath path = arg0.getNewLeadSelectionPath();
		/*
		String[] split = path.toString().split(",");
	    System.out.println("Pfad als ganzes = "+path.toString());
	    System.out.println("Pfad-Count = "+path.getPathCount());
	    for(int i = 0;i<split.length;i++){
	    	System.out.println("****"+split[i]);
	    }
	    */
		System.out.println("arg0 = "+arg0.getPath().getPathCount());
		//doKassenTreeAuswerten(arg0.getPath().getPathCount(),arg0.getPath().toString());
    	TreePath tp =  treeKasse.getSelectionPath();
    	if(tp==null){
    		return;
    	}
    	JXTTreeNode node = (JXTTreeNode) tp.getLastPathComponent();
    	String rez_nr = ((JXTTreeNode)node).knotenObjekt.rez_num;
    	if(!rez_nr.trim().equals("")){
    		doKassenTreeAuswerten(node.knotenObjekt);
    		aktuellerPat = node.knotenObjekt.pat_intern;
    	}else{
    		abrRez.setRechtsAufNull();
    		aktuellerPat = "";
    	}
    	System.out.println("RezeptNummer von Knoten = " +((JXTTreeNode)node).knotenObjekt.rez_num);

	}
	public void setRezeptOk(boolean ok){
    	treeKasse.getSelectionCount();
    	TreePath path = treeKasse.getSelectionPath();
    	JXTTreeNode node = (JXTTreeNode) path.getLastPathComponent();
    	((KnotenObjekt)node.getUserObject()).fertig = ok;
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
