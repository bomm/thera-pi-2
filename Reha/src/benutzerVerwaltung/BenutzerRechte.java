package benutzerVerwaltung;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.AbstractCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreePath;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.IconValues;
import org.jdesktop.swingx.renderer.MappedValue;
import org.jdesktop.swingx.renderer.StringValues;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableModel;

import rehaInternalFrame.JBenutzerInternal;
import systemEinstellungen.SystemConfig;
import systemTools.ButtonTools;
import systemTools.JCompTools;
import systemTools.JRtaCheckBox;
import systemTools.JRtaComboBox;
import systemTools.JRtaTextField;
import systemTools.Verschluesseln;
import terminKalender.ParameterLaden;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.sun.star.uno.Exception;

public class BenutzerRechte extends JXPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6231533201163780445L;
	HashMap<String,String[]> rechteMap = new HashMap<String,String[]>();
	JBenutzerInternal internal = null;
	String[] hauptGruppen = {"Benutzer-Verwaltung","Patiente","Rezepte","Historie","Therapieberichte",
			"Dokumentation","Gutachten","Terminkalender","[Ru:gl]"};

	String[] gruppe0 = {"Benutzer-Verwaltung öffnen","Benutzer anlegen/ändern/löschen","Benutzer hat alle Rechte = SuperUser"};

	String[] gruppe1 = {"anlegen","komplett ändern","nur Teile ändern:Tel,Fax,Akut,mögl. Termine","löschen"};

	String[] gruppe2 = {"anlegen","ändern","löschen","Gebühren kassieren","Ausfallrechnung erstellen","Rezept abschließen",
			"Rezept aufschließen","Privat-/BG-Rechnung erstellen","Therapieberichte erstellen"};

	String[] gruppe3 = {"Gesamtumsatz einsehen","einzelne Behandlungstage drucken","nachträglich Therapie-Berichte erstellen/ändern"};
	
	String[] gruppe4 = {"bestehende Berichte ändern","löschen"};
	
	String[] gruppe5 = {"löschen","Scannen","OOorg Doku erstellen"};
	
	String[] gruppe6 = {"anlegen","ändern","löschen","Stammdaten auf neues Gutachten übertragen"};
	
	String[] gruppe7 = {"löschen","Scannen","OOorg Doku erstellen"};
	
	String[] gruppe8 = {"anlegen","ändern","löschen","Stammdaten auf neues Gut. übertragen"};
	
	/*************************************/
	
	JXPanel content = null;
	JButton[] buts = {null,null,null,null};
	JRtaTextField[] tfs = {null};
	JPasswordField[] pws = {null,null};
	JRtaComboBox jcmb = null;
	JRtaCheckBox jchb = null;
	String aktuelleRechte;
	
	private JXRechteTreeTableNode aktNode;
	private int aktRow;
	private JXRechteTreeTableNode root = null;
	private RechteTreeTableModel rechteTreeTableModel = null;
	private JXTreeTable jXTreeTable = null;
	private JXRechteTreeTableNode foo = null;
	private MyRechteComboBox comborechte = null;

	
	ActionListener al;
	KeyListener kl;
	public BenutzerRechte(JBenutzerInternal bint){
		super();
		this.internal = bint;
		putRechte();
		makeListeners();
		this.setLayout(new BorderLayout());
		add(getHeader(),BorderLayout.NORTH);
		add(getContent(),BorderLayout.CENTER);

		validate();
	}
	private JXHeader getHeader(){
		String ss = Reha.proghome+"icons/header-image.png";
	    JXHeader header = new JXHeader("Benutzerverwaltung",
	            "Hier legen Sie die Namen der Programmbenutzer fest. Sie können Passwörter erstellen oder ändern.\n" +
	            "Der Benutzername des eingeloggten Users erscheint später im Fenster-Titel. \n" +
	            "Darüberhinaus können Sie hier jedem Benutzer individuelle Berechtigungen für einzelne Programmteile zuweisen.",
	            new ImageIcon(ss));
	    return header;
	}
    
	private JXPanel getContent(){
		FormLayout lay = new FormLayout("fill:0:grow(0.5),fill:0:grow(0.5)","fill:0:grow(1.0)");
		CellConstraints cc = new CellConstraints();
		content = new JXPanel();
		content.setLayout(lay);
		content.add(getButtonTeil(),cc.xy(1,1));
		content.add(getTreeTableTeil(),cc.xy(2,1));
		content.validate();
		return content;
	}
	private JScrollPane getTreeTableTeil(){

			comborechte = new MyRechteComboBox();
			/*
			JLabel lab = new JLabel();
    		lab.setIcon(SystemConfig.hmSysIcons.get("zuzahlnichtok"));
			comborechte.component.addItem(lab);
    		lab = new JLabel();
    		lab.setIcon(SystemConfig.hmSysIcons.get("zuzahlok"));
			comborechte.component.addItem(lab);
			*/

			root = new JXRechteTreeTableNode("root",null, true);
	        rechteTreeTableModel = new RechteTreeTableModel(root);
	        String[] colidentify = {"Programmfunktion","berechtigt"};
	        rechteTreeTableModel.setColumnIdentifiers(Arrays.asList(colidentify));
	        
	        //Highlighter hl = HighlighterFactory.createAlternateStriping();

	        
	        jXTreeTable = new JXTreeTable(rechteTreeTableModel);
	        //jXTreeTable.addHighlighter(hl);
	        TableCellRenderer renderer = new DefaultTableRenderer(new MappedValue(StringValues.EMPTY, IconValues.ICON), JLabel.CENTER);
	        jXTreeTable.getColumn(1).setCellRenderer(renderer);
	        jXTreeTable.getColumn(1).setMaxWidth(100);
	        jXTreeTable.getColumn(1).setCellEditor(comborechte);
	        jXTreeTable.setSelectionMode(0);
	        jXTreeTable.setShowGrid(true, false);
	        for(int i1 = 0; i1 < hauptGruppen.length;i1++){
	        	JXRechteTreeTableNode node = new JXRechteTreeTableNode(hauptGruppen[i1].toString(),
	        			new Rechte(hauptGruppen[i1],-1,null), true);
	        	String[] programmteile = rechteMap.get("gruppe"+Integer.toString(i1));
	        	if(programmteile != null){
	            	for(int i2 = 0; i2 < programmteile.length;i2++){
	                	JXRechteTreeTableNode node2 = new JXRechteTreeTableNode(programmteile[i2].toString(),
	                			new Rechte(programmteile[i2].toString(),0,""), true);
	            		node.insert(node2,node.getChildCount());
	            	}
	        		
	        	}
	        	rechteTreeTableModel.insertNodeInto(node, root, root.getChildCount());
	        }
	        jXTreeTable.addTreeSelectionListener(new RechteTreeSelectionListener() );
	        jXTreeTable.setCellSelectionEnabled(true);
	        jXTreeTable.setEnabled(false);
	        jXTreeTable.validate();

	        jXTreeTable.repaint();
		
        JScrollPane jscr = JCompTools.getTransparentScrollPane(jXTreeTable);
        jscr.validate();

		return jscr;
	}
	
	private JXPanel getButtonTeil(){
		//                                   1            2  3   4        5 
		FormLayout lay = new FormLayout("fill:0:grow(0.5),80dlu,3dlu,80dlu,fill:0:grow(0.5)",
		//       1             2   3   4  5   6  7   8  9  10  11  12  13 14   15 
			"fill:0:grow(0.33),p,20dlu,p,1dlu,p,1dlu,p,1dlu,p,25dlu,p,5dlu,p,fill:0:grow(0.66)");
		CellConstraints cc = new CellConstraints();
		JXPanel jpan = new JXPanel();
		jpan.setLayout(lay);

		JLabel lab = new JLabel("Benutzer auswählen");
		jpan.add(lab,cc.xy(2,2,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		jcmb = new JRtaComboBox();
		jcmb.setDataVectorWithStartElement(ParameterLaden.pKollegen, 0, 1, "./.");
		jcmb.setActionCommand("benutzerwahl");
		jcmb.addActionListener(al);
		jpan.add(jcmb,cc.xy(4,2));
		
		lab = new JLabel("Benutzername");
		jpan.add(lab,cc.xy(2,4,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		tfs[0] = new JRtaTextField("GROSS",false);
		tfs[0].setEnabled(false);
		jpan.add(tfs[0],cc.xy(4,4));
		
		lab = new JLabel("Passwortanzeige");
		jpan.add(lab,cc.xy(2,6,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		jchb = new JRtaCheckBox("im Klartext anzeigen");
		jpan.add(jchb,cc.xy(4,6));
		
		lab = new JLabel("Passwort");
		jpan.add(lab,cc.xy(2,8,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		pws[0] = new JPasswordField();
		pws[0].setEnabled(false);
		jpan.add(pws[0],cc.xy(4,8));
		
		lab = new JLabel("Passwort wiederholen");
		jpan.add(lab,cc.xy(2,10,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		pws[1] = new JPasswordField();
		pws[1].setEnabled(false);
		jpan.add(pws[1],cc.xy(4,10));

		jpan.add((buts[0] = ButtonTools.macheButton("neuer Benutzer", "neu", al)),cc.xy(2,12));
		jpan.add((buts[1] = ButtonTools.macheButton("Benutzer ändern", "edit", al)),cc.xy(4,12));
		jpan.add((buts[2] = ButtonTools.macheButton("Benutzer speichern", "save", al)),cc.xy(2,14));
		jpan.add((buts[3] = ButtonTools.macheButton("Benutzer löschen", "delete", al)),cc.xy(4,14));
		//buts[2].setEnabled(false);
		
		
		return jpan;
	}
	
	private void putRechte(){
		rechteMap.put("gruppe0",gruppe0);
		rechteMap.put("gruppe1",gruppe1);
		rechteMap.put("gruppe2",gruppe2);
		rechteMap.put("gruppe3",gruppe3);
		rechteMap.put("gruppe4",gruppe4);
		rechteMap.put("gruppe5",gruppe5);
		rechteMap.put("gruppe6",gruppe6);
		rechteMap.put("gruppe7",gruppe7);
		rechteMap.put("gruppe8",gruppe8);		
	}
	/*******************************/
	private void doBenutzerWahl(){
		if(jcmb.getSelectedIndex()==0){
			tfs[0].setText("");
			pws[0].setText("");
			pws[1].setText("");
			aktuelleRechte = "";
		}else{
			tfs[0].setText(jcmb.getSelectedItem().toString());
			pws[0].setText(jcmb.getValue().toString());
			pws[1].setText(jcmb.getValue().toString());
			aktuelleRechte = ParameterLaden.pKollegen.get(jcmb.getSelectedIndex()-1).get(2);
			System.out.println("Aktuelle Rechte sind "+aktuelleRechte);
			aktualisiereTree();
		}
	}
	/*******************************/
	private void aktualisiereTree(){
        

		
	}
	/*******************************/	
	private void makeListeners(){
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd = arg0.getActionCommand();
				if(cmd.equals("benutzerwahl")){
					doBenutzerWahl();
					return;
				}
				if(cmd.equals("neu")){
					jXTreeTable.clearSelection();
					jXTreeTable.setEnabled(true);
					return;
				}
				if(cmd.equals("edit")){
					jXTreeTable.clearSelection();
					jXTreeTable.setEnabled(true);
					return;
				}
				if(cmd.equals("save")){
					doSave();
					jXTreeTable.clearSelection();
					jXTreeTable.setEnabled(false);
					return;
				}
				if(cmd.equals("delete")){
					jXTreeTable.clearSelection();
					return;
					
				}
				
			}
		};
		kl = new KeyListener(){
			@Override
			public void keyPressed(KeyEvent arg0) {
			}
			@Override
			public void keyReleased(KeyEvent arg0) {
			}
			@Override
			public void keyTyped(KeyEvent arg0) {
			}
		};
	}
	
	private void doSave(){
		int lang = getNodeCount();
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < lang;i++){
			JXRechteTreeTableNode node = holeNode(i);
			System.out.println(node.rechte.bildnummer);
			buf.append(Integer.toString(node.rechte.bildnummer));
		}
		String pw = buf.toString();
		Verschluesseln man = Verschluesseln.getInstance();
	    man.init(Verschluesseln.getPassword().toCharArray(), man.getSalt(), man.getIterations());
		String encrypted = man.encrypt(pw);
		System.out.println("Encrypted Name = "+man.encrypt(tfs[0].getText()));
		System.out.println("Encrypted Rechte = "+encrypted);
		System.out.println("Encrypted Pasword = "+man.encrypt(new String(pws[0].getPassword())));
		
	}
/******************************************************************/	
	private int getNodeCount(){
		int ret = 0; 
		int  rootAnzahl;
		int  kindAnzahl;
		JXRechteTreeTableNode rootNode;
		JXRechteTreeTableNode childNode;
		rootAnzahl =  root.getChildCount();
		if(rootAnzahl<=0){return 0;}
		int geprueft = 0;
		for(int i = 0; i < rootAnzahl;i++){
			rootNode = (JXRechteTreeTableNode) root.getChildAt(i);
			ret += 1;
			if( (kindAnzahl = rootNode.getChildCount())>0){
				ret+=kindAnzahl;
			}
		}
		return ret;
	}
	
	private JXRechteTreeTableNode holeNode(int zeile){
		
		JXRechteTreeTableNode node = null;
		int  rootAnzahl;
		int  kindAnzahl;
		JXRechteTreeTableNode rootNode;
		JXRechteTreeTableNode childNode;
		rootAnzahl =  root.getChildCount();
		if(rootAnzahl<=0){return node;}
		int geprueft = 0;
		for(int i = 0; i < rootAnzahl;i++){
			
			rootNode = (JXRechteTreeTableNode) root.getChildAt(i);

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
				geprueft++;
				for(int i2 = 0; i2 < kindAnzahl;i2++){
					
					if(geprueft==zeile){
						childNode = (JXRechteTreeTableNode) rootNode.getChildAt(i2);
						return childNode;
					}else{
						childNode = (JXRechteTreeTableNode) rootNode.getChildAt(i2);
						geprueft ++;						
					}

				}
			}else{
				geprueft++;	
			}
		}
		return node;
	}
	
/****************************************************************************************/
    private static class JXRechteTreeTableNode extends DefaultMutableTreeTableNode {
    	private boolean enabled = false;
    	private Rechte rechte = null;
    	public JXRechteTreeTableNode(String name,Rechte rechte ,boolean enabled){
    		super(name);
    		this.enabled = enabled;
   			this.rechte = rechte;
   			if(rechte != null){
   				this.setUserObject(rechte);
   			}
    	}
 
		public boolean isEnabled() {
			return enabled;
		}
		
		public Rechte getObject(){
			return rechte;
		}
    }
	private class RechteTreeTableModel extends DefaultTreeTableModel {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		DecimalFormat dfx = new DecimalFormat( "0.00" );
		
        public RechteTreeTableModel(JXRechteTreeTableNode jXrechteTreeTableNode) {
            super(jXrechteTreeTableNode);
        }
        /******************/
        public Object getValueAt(Object node, int column) {
        	JXRechteTreeTableNode jXTreeTableNode = (JXRechteTreeTableNode) node;

        	Rechte o = null;
        	try{
        		o =  (Rechte) jXTreeTableNode.getUserObject();
        	}catch(ClassCastException cex){
        		return super.getValueAt(node, column);
        	}
        	 switch (column) {
        	 	case 0:
        	 		return o.hauptgruppe;
        	 	case 1:
        	 		return o.rechteicon.getIcon();
	            case 2:
	                //return o.programmteil;	               
        	 }        
        	return super.getValueAt(node, column);
        }
        /******************/
        public void setValueAt(Object value, Object node, int column){
        	JXRechteTreeTableNode jXTreeTableNode = (JXRechteTreeTableNode) node;
        	Rechte o;
        	
           	try{
            	o =  (Rechte) jXTreeTableNode.getUserObject();
            }catch(ClassCastException cex){
            	cex.printStackTrace();
            	return;
            }
            switch (column) {
            	case 0:
            		o.hauptgruppe =((String) value) ;
            		break;
            	case 1:
            		o.rechteicon.setIcon(o.img[(Integer) value]) ;   
            		o.bildnummer = (Integer) value;
            		break;
            	case 2:
            		o.programmteil =((String) value) ;
            		break;
            }	
        }  
        /******************/
        public int getColumnCount() {
            return 2;
        }

        /******************/
        public boolean isCellEditable(java.lang.Object node,int column){
        	Rechte o = null;
        	try{
        		o =  (Rechte) ((JXRechteTreeTableNode)node).getUserObject();
        	}catch(ClassCastException cex){
        		cex.printStackTrace();
        	}        	
            switch (column) {
            case 0:
                return false;
            case 1:
            	if(o.bildnummer >= 0){
            		return true;
            	}else{
                    return false;            		
            	}
            case 2:
                return true;
            default:
                return false;
            }
        }
        /******************/
        public Class<?> getColumnClass(int column) {
            switch (column) {
            case 0:
                return String.class;
            case 1:
                return JLabel.class;
            case 2:
                return String.class;
            default:
                return Object.class;
            }
        }
        /******************/        
        
	}    
    
    class Rechte{
    	String hauptgruppe="";
    	String programmteil="";
    	JLabel rechteicon = null;
    	int bildnummer = -1;
    	ImageIcon[] img = {SystemConfig.hmSysIcons.get("zuzahlnichtok"),SystemConfig.hmSysIcons.get("zuzahlok")};
    	
    	public Rechte(String hauptgruppe,int rechteicon,String xrechte){
    		this.hauptgruppe = hauptgruppe;
    		this.programmteil = xrechte;
			this.rechteicon = new JLabel("");
    		if(rechteicon>=0){
    			this.rechteicon.setHorizontalAlignment(JLabel.CENTER);
        		this.rechteicon.setIcon(img[rechteicon]);
        		this.bildnummer = rechteicon;
    		}
    		
    	}
    }
    class MyRechteComboBox extends AbstractCellEditor implements TableCellEditor{ 
    	/**
		 * 
		 */
		private static final long serialVersionUID = -1394804970777323591L;
		// This is the component that will handle the editing of the cell value 
    	/**
    	 * 
    	 */
    	public JComboBox component =null;
    	
    	ImageIcon[] img = {SystemConfig.hmSysIcons.get("zuzahlnichtok"),SystemConfig.hmSysIcons.get("zuzahlok")};
    	public MyRechteComboBox(){
    		component = new JComboBox(new Object[] { SystemConfig.hmSysIcons.get("zuzahlnichtok"), 
    				SystemConfig.hmSysIcons.get("zuzahlok")});

    		((JComboBox)component).setRenderer(new RechteComboBoxRenderer() );
    		
    	}
		@Override
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			if(isSelected){
				((JComboBox)component).requestFocus();
				if(value instanceof ImageIcon){
					if( ((ImageIcon)value).equals(img[0])){
						((JComboBox)component).setSelectedIndex(0);					
					}else if( ((ImageIcon)value).equals(img[1])){
						((JComboBox)component).setSelectedIndex(1);
					}else{
						((JComboBox)component).setSelectedIndex(0);
					}
				}
			}else{
				return null;
			}
			return component;			
		}

		@Override
		public Object getCellEditorValue() {
			return ((JComboBox)component).getSelectedIndex();
		}
		
    }
    class RechteComboBoxRenderer extends JLabel  implements ListCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1773401769072883430L;

		public RechteComboBoxRenderer() {
				super();
				setOpaque(true);
		        setHorizontalAlignment(CENTER);
		        setVerticalAlignment(CENTER);

		}

		@Override
		public Component getListCellRendererComponent(JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {

			//int selectedIndex = ((Integer)value).intValue();

	        if (isSelected) {
	            setBackground(list.getSelectionBackground());
	            setForeground(list.getSelectionForeground());
	        } else {
	            setBackground(list.getBackground());
	            setForeground(list.getForeground());
	        }
	        if(value != null){
	        	setIcon((ImageIcon)value);
	        }

			return this;
		}
    	
    }
	class RechteTreeSelectionListener implements TreeSelectionListener {
		boolean isUpdating = false;
		
		public void valueChanged(TreeSelectionEvent e) {
			if (!isUpdating) {
				isUpdating = true;
				JXTreeTable tt = jXTreeTable;
				TreeTableModel ttmodel = tt.getTreeTableModel();
				TreePath[] selpaths = tt.getTreeSelectionModel().getSelectionPaths();
				
				if (selpaths !=null) {
					ArrayList<TreePath> selPathList = new ArrayList<TreePath>(Arrays.asList(selpaths));
					int i=1;
					while(i<=selPathList.size()) {
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
					aktNode =  (JXRechteTreeTableNode) tp.getLastPathComponent();//selpaths[selpaths.length-1].getLastPathComponent();
					new SwingWorker<Void,Void>(){
						protected Void doInBackground() throws Exception {
							int lang = getNodeCount();
							aktRow = -1;
							
							for(int i = 0; i < lang; i++){
								if(aktNode == holeNode(i)){
									aktRow = i;
									//System.out.println("Zeilennummer =  = "+i);
									//System.out.println("Node selektiert = "+aktNode.abr.bezeichnung);
									//System.out.println("Behandlungsdatum selektiert = "+aktNode.abr.datum+" / "+aktNode.abr.bezeichnung);
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
    

}
