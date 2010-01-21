package abrechnung;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import hauptFenster.UIFSplitPane;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTree;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import sqlTools.SqlInfo;
import systemEinstellungen.SystemConfig;
import systemTools.JCompTools;
import systemTools.JRtaComboBox;
import systemTools.JRtaRadioButton;

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
	public DefaultMutableTreeNode rootKasse;
	public DefaultTreeModel treeModelKasse;

	
	/*******Controls für die rechte Seite*********/
	AbrechnungRezept abrRez = null;
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
		cmbDiszi.addActionListener(this);
		pb.add(cmbDiszi,cc.xy(2,4));
		/*
		butLinks[0] = new JButton("Abrechnungsdaten einlesen");
		butLinks[0].setActionCommand("einlesen");
		butLinks[0].addActionListener(this);
		pb.addLabel("",cc.xy(2,6));
		pb.add(butLinks[0],cc.xy(2,8));
		*/
		rootKasse = new DefaultMutableTreeNode( "Abrechnung für Kasse..." );
		treeModelKasse = new DefaultTreeModel(rootKasse);
		treeKasse = new JXTree( rootKasse );
		treeKasse.setName("kassentree");
		treeKasse.getSelectionModel().addTreeSelectionListener(this);
		JScrollPane jscrk = JCompTools.getTransparentScrollPane(treeKasse);
		pb.add(jscrk,cc.xy(2, 10));
		
		doEinlesen();
		pb.getPanel().validate();
		JScrollPane jscr = JCompTools.getTransparentScrollPane(pb.getPanel());
		jscr.validate();
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
			String[] reznr = {"KG","MA","ER","LO"};
			abrRez.setKuerzelVec(reznr[cmbDiszi.getSelectedIndex()]);
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
					String dsz = diszis[cmbDiszi.getSelectedIndex()];
					Vector <Vector<String>> vecKassen = SqlInfo.holeFelder("select name1,ikktraeger from fertige where rezklasse='"+dsz+"' ORDER BY ikktraeger");
					if(vecKassen.size() <= 0){
						kassenBaumLoeschen();
						treeKasse.setEnabled(false);
						return null;
					}
					int aeste = 0;
					kassenBaumLoeschen();
					treeKasse.setEnabled(true);
					String kas = vecKassen.get(0).get(0).trim();
					String ktraeger = vecKassen.get(0).get(1).trim();
					astAnhaengen(kas);
					rezepteAnhaengen(aeste);
					aeste++;
					for(int i = 0; i < vecKassen.size();i++){
						System.out.println(ktraeger);
						System.out.println(vecKassen.get(i).get(1));
						if(! vecKassen.get(i).get(1).equals(ktraeger)){
							kas = vecKassen.get(i).get(0);
							ktraeger = vecKassen.get(i).get(1);
							astAnhaengen(kas);
							rezepteAnhaengen(aeste);
							aeste++;
							treeKasse.repaint();
						}
					}
					
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
		System.out.println("Knoten von root "+rootKasse.getChildCount());
		//System.out.println(rootKasse.getChildAt(knoten));
		String kasse = rootKasse.getChildAt(knoten).toString();
		String dsz = diszis[cmbDiszi.getSelectedIndex()];
		Vector <Vector<String>> vecKassen = SqlInfo.holeFelder("select rez_nr,pat_intern from fertige where rezklasse='"+dsz+"' AND name1='"+
				kasse+"' ORDER BY pat_intern");
		
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) rootKasse.getChildAt(knoten);
		DefaultMutableTreeNode treeitem = null;
		for(int i = 0;i<vecKassen.size();i++){
			String name = SqlInfo.holeFelder("select n_name from pat5 where pat_intern='"+
					vecKassen.get(i).get(1)+"' LIMIT 1").get(0).get(0);
			treeitem = new DefaultMutableTreeNode(vecKassen.get(i).get(0)+"-"+name);
			node.add(treeitem);
		}

	}
	private void astAnhaengen(String ast){
		System.out.println("HauptAst wird angehängt");
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(ast);
		rootKasse.add(node);
		treeKasse.validate();
		treeModelKasse.reload();
	}
	private void kassenBaumLoeschen(){
		System.out.println("Kassenbaum wird gelöscht");
		treeKasse.collapseAll();
		rootKasse.removeAllChildren();
		treeKasse.validate();
	}
	/*******************************************/
	private void doKassenTreeAuswerten(int pathCount,String path){
		//System.out.println("PathCount = "+arg0.getPath().getPathCount());
		if(pathCount==2){
			//Kasse ausgewählt
			return;
		}
		if(pathCount==3){
			//Rezept ausgewählt
			setCursor(new Cursor(Cursor.WAIT_CURSOR));
			if(! this.abrRez.setNewRez(path)){
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				return;
			}
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			return;
		}
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
		doKassenTreeAuswerten(arg0.getPath().getPathCount(),arg0.getPath().toString());
	    if(arg0.getSource().equals(treeModelKasse)){
	    	doKassenTreeAuswerten(arg0.getPath().getPathCount(),arg0.getPath().toString());
	    }
	}
	public void analysiereRezept(String rez,String pat){
		//rezeptdaten holen
		//testen ob 18 oder im Behandlungsverlauf 18 geworden
		//testen ob befreit
		//testen ob wechsel zwischen befreit und nicht befreit
		//testen ob Tarifwechsel während der Behandlung
		/**
		 * 
		 */
		// erforderliche Variablen
		//boolean vollfrei
		//boolean teilfrei
		//boolean frei anfang
		//boolean tarifwechsel
		//String stichtag_jahreswechsel = 31.12.Vorjahr
		//String stichtag_tarifwechsel = xx.xx.xxxx
	}

}
