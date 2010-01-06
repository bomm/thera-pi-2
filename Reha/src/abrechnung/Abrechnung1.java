package abrechnung;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

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

public class Abrechnung1 extends JXPanel implements PatStammEventListener,ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3580427603080353812L;
	private JAbrechnungInternal jry;
	private UIFSplitPane jSplitLR = null;
	private UIFSplitPane jSplitLinksOU = null;
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
		JScrollPane jscrk = JCompTools.getTransparentScrollPane(treeKasse);
		pb.add(jscrk,cc.xy(2, 10));
		
		doEinlesen();
		pb.getPanel().validate();
		JScrollPane jscr = JCompTools.getTransparentScrollPane(pb.getPanel());
		jscr.validate();
		return jscr;
	}
	private JXPanel getRight(){
		JXPanel pan = new JXPanel();
		//pan.setBackground(Color.GRAY);
		return pan;
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
		if(cmd.equals("einlesen")){doEinlesen();}
		
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
					Vector <Vector<String>> vecKassen = SqlInfo.holeFelder("select name1 from fertige where rezklasse='"+dsz+"' ORDER BY name1");
					if(vecKassen.size() <= 0){
						kassenBaumLoeschen();
						treeKasse.setEnabled(false);
						return null;
					}
					kassenBaumLoeschen();
					treeKasse.setEnabled(true);
					String kas = vecKassen.get(0).get(0);
					astAnhaengen(kas);
					for(int i = 0; i < vecKassen.size();i++){
						if(! vecKassen.get(i).get(0).equals(kas)){
							kas = vecKassen.get(i).get(0);
							astAnhaengen(kas);
						}
					}
					treeKasse.expandRow(0);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				return null;
			}
		}.execute();
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
		rootKasse.removeAllChildren();
		treeKasse.collapseAll();
		treeKasse.validate();
		/*
		int anzahl = treeModelKasse.getChildCount(rootKasse);
		for(int i = 0; i < anzahl; i++){
			treeModelKasse.removeNodeFromParent(rootKasse);	
		}
		*/
				
	}
	private void doAufraeumen(){
		butLinks[0].removeActionListener(this);
		cmbDiszi.removeActionListener(this);
	}

}
