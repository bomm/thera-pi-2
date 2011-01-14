package reha301Panels;





import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import reha301.Dta301Model;
import reha301.Reha301;
import reha301.Reha301Tab;

import Tools.ButtonTools;
import Tools.Colors;
import Tools.DatFunk;
import Tools.INIFile;
import Tools.JCompTools;
import Tools.JRtaTextField;
import Tools.OOTools;
import Tools.RezTools;
import Tools.SqlInfo;
import Tools.StringTools;
import Tools.SystemPreislisten;
import Tools.WartenAufDB;


import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.TextException;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import dialoge.ArztAuswahl;
import dialoge.KassenAuswahl;

public class Reha301Auswerten extends JXPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Reha301Tab eltern = null;
	public JXTable tab = null;
	public MyTableModel tabmod;
	public JEditorPane[] editpan = {null,null,null};
	public JComboBox patcombo = null;
	private Vector<Vector<String>> vec_patstamm = null;
	private JButton[] buts = {null,null,null,null};
	
	ActionListener al = null;
	int anzeigeart = -1;
	public String[] artderNachricht = {"Nachr.Typ unbekannt","Bewilligung","Ablehnung","Verl. Zustimmung","Verl. Ablehnung","sonstige Nachricht"};
	public String patBetroffen = null;
	public boolean patneuangelegt = false;
	public String patneuepatnr = "";
	public boolean rezneuangelegt = false;
	public String rezneuereznr = "";
	Object[] rVTraeger;
	Dta301Model dta301mod = null;
	
	public JXTreeNode root;
	public DefaultTreeModel treeModel;
	public JXTree tree;

	Vector<String> vecgelesen = new Vector<String>();
	
	public Reha301Auswerten(Reha301Tab xeltern){
		super(new BorderLayout());
		eltern = xeltern;
		setOpaque(false);
		/**************************/
		CompoundPainter<Object> cp = null;
		MattePainter mp = null;
		LinearGradientPaint p = null;
		/*****************/
		Point2D start = new Point2D.Float(0, 0);
		Point2D end = new Point2D.Float(960,100);
	    float[] dist = {0.0f, 0.75f};
	    Color[] colors = {Color.WHITE,Colors.PiOrange.alpha(0.25f)};
	    p =  new LinearGradientPaint(start, end, dist, colors);
	    mp = new MattePainter(p);
	    cp = new CompoundPainter<Object>(mp);
	    this.setBackgroundPainter(cp);
		/**************************/
		this.ActivateListener();
		add(getContent(),BorderLayout.CENTER);
		initReha301Auswerten();
		ladPreislisten();
		validate();
	}
	public void ladPreislisten(){
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				try{
					while(!Reha301.DbOk){
						try {
							Thread.sleep(25);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					System.out.println("Reha301-Datenbank = o.k.");
					SystemPreislisten.ladePreise("Reha");
					SystemPreislisten.ladePreise("Physio");
				}catch(Exception ex){
					ex.printStackTrace();
				}
				return null;
			}
			
		}.execute();
		
	}
	public void initReha301Auswerten(){
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				try{
					//                      0        1          2            3          4                5       6  7    8        9      10        
					String cmd = "select eingelesen,sender,nachrichtentyp,patangaben,versicherungsnr,ktraeger,datum,id,leistung,eilfall,eingelesenam from dta301 where eingelesen='F' order by datum";
					dta301mod = new Dta301Model(tab,-1);
					regleTabelle(cmd,1);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				return null;
			}
			
		}.execute();
	}

	public JXPanel getContent(){
		JXPanel pan = new JXPanel();
		String xwerte = "10dlu,fill:0:grow(1.0),10dlu";
		String ywerte = "10dlu,fill:0:grow(0.25),fill:0:grow(0.75)";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		pan.setLayout(lay);
		pan.setOpaque(false);
		tabmod = new MyTableModel();
		//                                          0       1        2          3                4              5        6      7               8            9    10  11 12   13                                          
		tabmod.setColumnIdentifiers(new String[] {"lfnr","import","Sender","Nachrichtentyp","Name, Vorname","Adresse","VSNR","Kostenträger","Krankenkasse","Datum","","","","verarbeitet"});
		tab = new JXTable(tabmod);
		tab.getColumn(0).setMaxWidth(25);
		tab.getColumn(1).setMaxWidth(45);
		tab.getColumn(2).setMaxWidth(65);
		tab.getColumn(3).setMinWidth(150);
		tab.getColumn(4).setMinWidth(150);
		tab.getColumn(5).setMinWidth(150);
		tab.getColumn(6).setMinWidth(85);
		tab.getColumn(6).setMaxWidth(85);
		tab.getColumn(7).setMaxWidth(65);
		tab.getColumn(9).setMaxWidth(75);
		tab.getColumn(10).setMinWidth(0);
		tab.getColumn(10).setMaxWidth(0);
		tab.getColumn(11).setMinWidth(0);
		tab.getColumn(11).setMaxWidth(0);
		tab.getColumn(12).setMinWidth(0);
		tab.getColumn(12).setMaxWidth(0);
		tab.setEditable(false);
		tab.setSortable(false);
		tab.setHighlighters(HighlighterFactory.createSimpleStriping(HighlighterFactory.CLASSIC_LINE_PRINTER));
		tab.validate();
		tab.getSelectionModel().addListSelectionListener( new MyAuswertungListSelectionHandler());		
		tab.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(arg0.getButton()==3){
					ZeigePopupMenu(arg0);
					return;
				}
				if(arg0.getClickCount()==2){
					if(anzeigeart==1){
						if(tab.getSelectedRow()>=0){
							//doPatUntersuchen(tab.getSelectedRow(),arg0.getLocationOnScreen());							
						}
					}
					
				}
				
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
			
		});
		JScrollPane jscr = JCompTools.getTransparentScrollPane(tab);
		jscr.validate();
		pan.add(jscr,cc.xy(2,2,CellConstraints.FILL,CellConstraints.FILL));

		pan.add(getActionPanel(),cc.xy(2,3,CellConstraints.FILL,CellConstraints.FILL));
		
		pan.validate();
		return pan;
	}
	private void ZeigePopupMenu(java.awt.event.MouseEvent me){
		JPopupMenu jPop = doPopUpMenue();
		jPop.show( me.getComponent(), me.getX(), me.getY() ); 
	}
	private JPopupMenu doPopUpMenue(){
		JPopupMenu jPopupMenu = new JPopupMenu();
		// Lemmi 20101231: Icon zugefügt
		JMenuItem item = new JMenuItem("Nachricht im OO-Writer öffnen");
		item.setActionCommand("nachrichtinwriter");
		item.addActionListener(al);
		jPopupMenu.add(item);

		return jPopupMenu;
		
	}
	private JXPanel getActionPanel(){
		JXPanel pan = new JXPanel();
		//                1         2            3            4         5        6             7
		String xwerte = "0dlu,fill:0:grow(0.33),5dlu,fill:0:grow(0.33),5dlu,fill:0:grow(0.33),0dlu";
		//                1    2   3    4              5    6  7
		String ywerte = "10dlu,p,2dlu,fill:0:grow(1.0),2dlu,p,10dlu";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		pan.setLayout(lay);
		pan.setOpaque(false);
		pan.add(getButtonPanel(),cc.xy(2,4,CellConstraints.FILL,CellConstraints.FILL));
		
		JLabel lab = new JLabel("<html><b><font color='#0000ff'>301-er Daten</font></b></html>");
		pan.add(lab,cc.xy(4,2,CellConstraints.FILL,CellConstraints.FILL));
		editpan[0] = new JEditorPane();
		editpan[0].setContentType("text/html");
		editpan[0].setEditable(false);
		editpan[0].setOpaque(true);
		JScrollPane jscr = JCompTools.getTransparentScrollPane(editpan[0]);
		jscr.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		jscr.validate();
        pan.add(jscr,cc.xy(4,4,CellConstraints.FILL,CellConstraints.FILL));
        
        JXPanel pan2 = new JXPanel();
        pan2.setOpaque(false);
        FormLayout lay2 = new FormLayout("fill:0:grow(0.33),2dlu,fill:0:grow(0.66)","p");
        CellConstraints cc2 = new CellConstraints();
        pan2.setLayout(lay2);
		lab = new JLabel("<html><b>Thera-Pi</b> Stammdaten</html>");
		pan2.add(lab,cc2.xy(1,1,CellConstraints.FILL,CellConstraints.FILL));
		patcombo = new JComboBox();
		patcombo.setActionCommand("auswerten");
		//patcombo.addActionListener(al);
		pan2.add(patcombo,cc2.xy(3,1,CellConstraints.FILL,CellConstraints.FILL));
		pan2.validate();
		
		pan.add(pan2,cc.xy(6,2,CellConstraints.FILL,CellConstraints.FILL));
		
        editpan[1] = new JEditorPane();
		editpan[1].setContentType("text/html");
		editpan[1].setEditable(false);
		editpan[1].setOpaque(true);
		JXPanel panneu = new JXPanel(new BorderLayout());
		panneu.setOpaque(false);
		panneu.setAlpha(.5f);
		panneu.add(editpan[1],BorderLayout.CENTER);
		jscr = JCompTools.getTransparentScrollPane(panneu);
		//jscr = JCompTools.getTransparentScrollPane(editpan[1]);
		jscr.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		jscr.validate();

        pan.add(jscr,cc.xy(6,4,CellConstraints.FILL,CellConstraints.FILL));
        
		pan.validate();
		return pan;
	}
	private JXPanel getButtonPanel(){
		JXPanel pan = new JXPanel();
		//                1         2            3  4   5       6             7
		String xwerte = "0dlu,fill:0:grow(0.5),2dlu,fill:0:grow(0.5),0dlu";
		//                1   2  3   4  5     6                7
		String ywerte = "0dlu,p,15dlu,p,2dlu,fill:0:grow(1.0),0dlu";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		pan.setLayout(lay);		
		pan.setOpaque(false);
		buts[1] = ButtonTools.macheButton("Verordnung anlegen", "rezneuanlage", al);
		pan.add(buts[1],cc.xy(2,2));
		buts[0] = ButtonTools.macheButton("Patient neu anlegen >>", "patneuanlage", al);
		pan.add(buts[0],cc.xy(4,2));
		JLabel lab = new JLabel("<html><b><font color='#ff0000'>eingetroffene Nachrichten</font></b></html>");
		pan.add(lab,cc.xy(2,4));
		pan.add(tree301(),cc.xyw(2,6,3,CellConstraints.FILL,CellConstraints.FILL));
		pan.validate();
		return pan;
	}
	private JScrollPane tree301(){
		KnotenObjekt kobjekt = null;
		kobjekt = new KnotenObjekt("301-Nachrichten","",false,"","");
		root = new JXTreeNode( kobjekt,true );
		treeModel = new DefaultTreeModel(root);
		File dir = new File(Reha301.inbox);
		File[] files = dir.listFiles();
		DefaultMutableTreeNode node = null;
		for(int i = 0; i < files.length;i++){
			if(files[i].getName().toUpperCase().endsWith(".AUF")){
				System.out.println( files[i].getName()+"-"+files[i].lastModified() );
				kobjekt = new KnotenObjekt(files[i].getName(),"",false,"","");
				root.add(new JXTreeNode(kobjekt,true));
				//node = new DefaultMutableTreeNode( files[i].getName());
				//root.add(node);
			}
		}
		tree = new JXTree( root );
		tree.setCellRenderer(new MyRenderer(new ImageIcon(Reha301.progHome+"icons/Haken_klein.gif")));
		tree.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(arg0.getClickCount()==2 && arg0.getButton()==1){
					String treelabel = ((JXTreeNode)tree.getSelectionPath().getLastPathComponent()).knotenObjekt.titel;
					if(treelabel.toUpperCase().endsWith(".AUF")){
						if(!vecgelesen.contains(treelabel)){
							int anfrage = JOptionPane.showConfirmDialog(null, "Wollen Sie die Nachricht "+treelabel+" einlesen und verarbeiten?", "Achtung wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
							if(anfrage == JOptionPane.YES_OPTION){
								final String xtreelabel = treelabel;
								new SwingWorker<Void,Void>(){
									@Override
									protected Void doInBackground()
											throws Exception {
										Reha301.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
										Reha301Einlesen einlesen = new Reha301Einlesen(eltern);
										einlesen.decodeAndRead(Reha301.inbox+xtreelabel);
										vecgelesen.add(String.valueOf(xtreelabel));
										Reha301.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
										((JXTreeNode)tree.getSelectionPath().getLastPathComponent()).knotenObjekt.fertig = true;
										return null;
									}
									
								}.execute();
							}
						}else{
							JOptionPane.showMessageDialog(null, "Diese Nachricht wurde bereits verarbeitet");
						}
					}
				}
				if(arg0.getClickCount()==1 && arg0.getButton()==3){
					try{
						String treelabel = ((JXTreeNode)tree.getSelectionPath().getLastPathComponent()).knotenObjekt.titel;
						if(treelabel.toUpperCase().endsWith(".AUF")){
							doNachrichtenPopUp(arg0);
						}
					}catch(Exception ex){}
				}
				
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
			
		});
		tree.setOpaque(false);
		tree.setVisible(true);
		tree.validate();
		//pan.add(tree,cc.xy(2,2,CellConstraints.FILL,CellConstraints.FILL));
		//JScrollPane jscr = JCompTools.getTransparentScrollPane(pan);
		JScrollPane jscr = JCompTools.getTransparentScrollPane(tree);
		jscr.validate();
		jscr.setVisible(true);
		jscr.setBackground(Color.RED);
		System.out.println(tree);
		return jscr;
	}
	public void ActivateListener(){
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd = arg0.getActionCommand();
				if(cmd.equals("einlesen")){

				}
				if(cmd.equals("auswerten")){
					//System.out.println(combo.getValue());
					//System.out.println(patcombo.getSelectedIndex());
					show301PatExist(patcombo.getSelectedIndex());
				}
				if(cmd.equals("patneuanlage")){
					if(tab.getRowCount()<=0){return;}
					doPatNeuanlage();
				}
				if(cmd.equals("rezneuanlage")){
					if(tab.getRowCount()<=0){return;}
					doRezNeuanlage();
				}
				if(cmd.equals("nachrichtinwriter")){
					doNachrichtInWriter();
				}

				
			}
		};
	}
	private void doNachrichtenPopUp(java.awt.event.MouseEvent me){
		JPopupMenu jPop = doNachrichtenMenue();
		jPop.show( me.getComponent(), me.getX(), me.getY() ); 
	}
	private JPopupMenu doNachrichtenMenue(){
		JPopupMenu jPopupMenu = new JPopupMenu();
		// Lemmi 20101231: Icon zugefügt
		JMenuItem item = new JMenuItem("untersuche Nachricht");
		item.setActionCommand("nachrichtuntersuchen");
		item.addActionListener(al);
		jPopupMenu.add(item);
		return jPopupMenu;
		
	}
	private void doNachrichtInWriter(){
		ITextDocument document = OOTools.starteLeerenWriter(true);
		try {
			document.getTextService().getCursorService().getTextCursor().getCharacterProperties().setFontName("Courier New");
			document.getTextService().getCursorService().getTextCursor().getCharacterProperties().setFontSize(9.f);
		} catch (TextException e) {
			e.printStackTrace();
		}
		document.getTextService().getText().setText(
				SqlInfo.holeEinzelFeld("select edifact from dta301 where id='"+dta301mod.getDtaID()+"' LIMIT 1"));
	}
	private void doNachrichtDirektDrucken(){
		
	}
	private void doNachrichtEmail(){
		
	}
	
	@SuppressWarnings("unused")
	private void doPatUntersuchen(int row,Point pos){
		String[] teilen = tab.getValueAt(row,4).toString().split("#");
		String cmd = "select * from pat5 where n_name='"+teilen[1]+"' and v_name='"+teilen[2]+"' and "+
		"geboren='"+tab.getValueAt(row,10).toString()+"'";
		//System.out.println(tab.getValueAt(row,10));
		Vector<Vector<String>> patvec = SqlInfo.holeFelder(cmd);

		if(patvec.size()<=0){
			JOptionPane.showMessageDialog(null, "Patient nicht in Datenbank vorhanden");
		}else{
			doPatientenWahl(tab.getValueAt(row,11).toString(),pos);
		}

	}
	private void doPatientenWahl(String id,Point pos){
		Reha301PatAuswahl patwahl = new Reha301PatAuswahl(this,id);
		patwahl.setModal(true);
		patwahl.setLocation(pos);
		patwahl.pack();
		patwahl.setVisible(true);
		patwahl = null;
		
	}
	/********************************************/
	private Vector<Object> doSetPatientFuerNachricht(String patid){
		//dta301mod.show_X_PatData();
		Vector<Object> vecobj = new Vector<Object>();
		patBetroffen = String.valueOf(patid);
		String ktraeger = tab.getValueAt(tab.getSelectedRow(), 7).toString();
		//String kkasse = tab.getValueAt(tab.getSelectedRow(), 8).toString();
		String id = tab.getValueAt(tab.getSelectedRow(), 11).toString();
		String diag1 = SqlInfo.holeEinzelFeld("select diagschluessel from dta301 where id ='"+id+"' LIMIT 1");
		String[] diag2 = diag1.split("\\+");
		String diaggruppe = null;
		if(diag2[2].split(":")[0].startsWith("M") || diag2[2].split(":")[0].startsWith("S") ){
			diaggruppe = "04";
		}
		rVTraeger = testeDTAIni(ktraeger,diaggruppe);
		String diagnose = diag2[2].split(":")[0];
		String fallart = SqlInfo.holeEinzelFeld("select leistung from dta301 where id ='"+id+"' LIMIT 1");
		String eilfall = SqlInfo.holeEinzelFeld("select eilfall from dta301 where id ='"+id+"' LIMIT 1");
		String preisgruppe = SqlInfo.holeEinzelFeld("select preisgruppe from kass_adr where ik_kostent ='"+ktraeger+"' LIMIT 1");
		System.out.println();
		if( ((Integer)rVTraeger[0]) >= 0){
			//RV-Träger
			/*
			System.out.println("Patient="+patBetroffen);
			System.out.println("Kostenträger="+ktraeger);
			System.out.println("Diagnose="+diagnose);
			System.out.println("Fallart="+fallart);
			System.out.println("Eilfall="+eilfall);
			System.out.println("Preisgruppe="+preisgruppe);
			System.out.println("Diagnosegruppe="+diaggruppe);
			for(int i = 0; i < rVTraeger.length;i++){
				System.out.println(rVTraeger[i]);
			}
			*/
			vecobj.add(rVTraeger);
			vecobj.add(patBetroffen);
			vecobj.add(ktraeger);
			vecobj.add(diagnose);
			vecobj.add(fallart);
			vecobj.add(eilfall);
			vecobj.add(preisgruppe);
			vecobj.add(diaggruppe);
			
		}else{
			//KrankenKasse
		}
		
		//Verordnung überpüfen
		//Verordnung anlegen
		//
		return vecobj;
	}
	/********************************************/	
	private Object[] testeDTAIni(String ktraeger,String diaggruppe){
		Object[] retobject = {-1,null,null,null,null,null};
		System.out.println(ktraeger+" - "+diaggruppe);
		INIFile ini = new INIFile(Reha301.progHome+"ini/"+Reha301.aktIK+"/dta301.ini");

		String gruppe = ini.getStringProperty("RehaGruppen", diaggruppe);
		if(gruppe == null){
			return retobject.clone();
		}
		int anzahl = ini.getIntegerProperty("RVTraeger_"+gruppe, "RVTraegerAnzahl");
		for(int i = 1 ; i <= anzahl;i++){
			if(ini.getStringProperty("RVTraeger_"+gruppe, "RVTraegerIK"+Integer.toString(i)).equals(ktraeger)){
				retobject[0] = i;
				retobject[1] = ini.getStringProperty("RVTraeger_"+gruppe, "RVTraegerIK"+Integer.toString(i)); 
				retobject[2] = ini.getStringProperty("RVTraeger_"+gruppe, "RVTraegerMed"+Integer.toString(i));
				retobject[3] = ini.getStringProperty("RVTraeger_"+gruppe, "RVTraegerAHB"+Integer.toString(i));
				retobject[4] = ini.getStringProperty("RVTraeger_"+gruppe, "RVTraegerNS"+Integer.toString(i));
				retobject[5] = String.valueOf(gruppe);
				return retobject.clone();
			}
		}
		return retobject.clone();
	}
	private void regleTabelle(String statement,int tabart){
//		"select eingelesen,sender,nachrichtentyp,patangaben,versicherungsnr,ktraeger,datum from dta301 where eingelesen='F' order by datum";

		System.out.println(statement);
		try{
			if(! WartenAufDB.IsDbOk()){
				JOptionPane.showMessageDialog(null, "Datenbank konnte nicht gestartet werden");
				return;
			}
			Vector<Vector<String>> vec = SqlInfo.holeFelder(statement);
			tabmod.setRowCount(0);
			anzeigeart = tabart;
			Vector<Object> vecobj = new Vector<Object>();
			String[] pat = null;
			String patangaben = null;
			String ortsangaben = null;
			String kassenangaben = null;
			String patgeboren = null;
			if(vec.size()> 0){
				for(int i = 0; i < vec.size();i++){
					vecobj.clear();
					
					vecobj.add((String) Integer.toString(i+1));
					vecobj.add((Boolean) (vec.get(i).get(0).equals("T")? true : false) );
					vecobj.add((String)vec.get(i).get(1));
					vecobj.add((String) artderNachricht[Integer.parseInt(vec.get(i).get(2))]+"-"+vec.get(i).get(8)+"-"+vec.get(i).get(9));
					pat = vec.get(i).get(3).split("#");
					patangaben = "";
					ortsangaben = "";
					kassenangaben = "";
					patgeboren = null;
					if(pat.length >=7){
						patangaben =pat[0]+"#"+pat[1]+"#"+pat[2];
						ortsangaben = pat[4]+"#"+pat[5]+"#"+pat[6];
						try{
							patgeboren = pat[3];
						}catch(Exception ex){
							
						}
					}
					vecobj.add((String) patangaben);
					vecobj.add((String) ortsangaben);
					vecobj.add((String) vec.get(i).get(4));
					vecobj.add((String) vec.get(i).get(5));
					if(pat.length >=8){
						kassenangaben = SqlInfo.holeEinzelFeld("select name1 from ktraeger where ikkasse='"+pat[7]+"' LIMIT 1");
						kassenangaben = kassenangaben+String.valueOf("#"+pat[7]);
					}
					vecobj.add((String) kassenangaben);
					try{
						vecobj.add((String) DatFunk.sDatInDeutsch(vec.get(i).get(6)));	
					}catch(Exception ex){
						vecobj.add("01.01.0000");
					}
					vecobj.add((String) patgeboren);
					vecobj.add((String) vec.get(i).get(7));
					vecobj.add((String) pat[3]);
					if( ((String) vec.get(i).get(10)).trim().length() < 10){
						vecobj.add("");
					}else{
						vecobj.add(DatFunk.sDatInDeutsch((String) vec.get(i).get(10)));	
					}
					
					tabmod.addRow( (Vector<?>)vecobj.clone());
					
					
				}
				tab.setRowSelectionInterval(0, 0);
				//show301PatData(0);
				//doSucheNachPat();

			}
			tab.validate();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	private void show301PatData(int row){
		StringBuffer buf = new StringBuffer();
		buf.append("<html><head>");
		buf.append(
		 "<style type='text/css'>.linksbuendig {text-align: left;}"+
         ".rechtsbuendig {text-align: right;}"+
         ".rbrot {text-align: right;color:#ff0000;}"+
         ".rbblau {text-align: right;color:#0000ff;}"+
         ".zentriert {text-align: center;}"+
         ".blocksatz {text-align: justify;}"+
         "</style>" ); 
		buf.append("</head>");
		buf.append("<table style='font-family:Arial'>");
		/******************/
		buf.append("<tr><td colspan='2';class='rbrot'>"+dta301mod.getDtaNachrichtAnlass()+"</td></tr>");
		buf.append("<tr><td class='rbblau'>Anrede</td><td>"+dta301mod.getPatAnrede()+"</td></tr>");
		buf.append("<tr><td class='rbblau'>Name</td><td>"+dta301mod.getPatNachname()+", "+dta301mod.getPatVorname()+"</td></tr>");
		buf.append("<tr><td class='rbblau'>geboren</td><td>"+dta301mod.getPatGeboren()+"</td></tr>");
		buf.append("<tr><td class='rbblau'>Strasse</td><td>"+dta301mod.getPatStrasse()+"</td></tr>");
		buf.append("<tr><td class='rbblau'>Ort</td><td>"+dta301mod.getPatPlz()+" "+dta301mod.getPatOrt()+"</td></tr>");
		buf.append("<tr><td class='rbblau'>VSNR</td><td>"+dta301mod.getPatVsnr()+"</td></tr>");
		buf.append("<tr><td class='rbblau'>Kostenträger</td><td>"+dta301mod.getDtaKtraegerIK()+"</td></tr>");
		buf.append("<tr><td class='rbblau'>Krankenkasse</td><td>"+dta301mod.getPatKassenName()+"</td></tr>");
		buf.append("<tr><td class='rbblau'>Krankenk.-IK</td><td>"+dta301mod.getPatKassenIk()+"</td></tr>");

		/******************/
		buf.append("</table>");
		buf.append("</html>");
		editpan[0].setText(buf.toString());
		
	}
	private void show301PatExist(int combovalue){
		StringBuffer buf = new StringBuffer();

		dta301mod.setXPatData(vec_patstamm.get(combovalue));
		
		buf.append("<html><head>");
		buf.append(
		 "<style type='text/css'>.linksbuendig {text-align: left;}"+
         ".rechtsbuendig {text-align: right;}"+
         ".rbrot {text-align: right;color:#ff0000;}"+
         ".rbblau {text-align: right;color:#0000ff;}"+
         ".rbtherapie {text-align: right;color:#e77817;}"+
         ".zentriert {text-align: center;}"+
         ".blocksatz {text-align: justify;}"+
         "</style>" ); 
		buf.append("</head>");
		buf.append("<table style='font-family:Arial'>");
		buf.append("<tr><td colspan='2';class='rbrot'>Patient im Thera-Pi Pat-Stamm</td></tr>");
		/****************************/
		buf.append("<tr><td class='rbtherapie'>Anrede</td><td>"+dta301mod.getX_patAnrede()+"</td></tr>");
		buf.append("<tr><td class='rbtherapie'>Name</td><td>"+dta301mod.getX_patNachname()+", "+dta301mod.getX_patVorname()+"</td></tr>");
		buf.append("<tr><td class='rbtherapie'>geboren</td><td>"+dta301mod.getX_patGeboren()+"</td></tr>");
		buf.append("<tr><td class='rbtherapie'>Strasse</td><td>"+dta301mod.getX_patStrasse()+"</td></tr>");
		buf.append("<tr><td class='rbtherapie'>Ort</td><td>"+dta301mod.getX_patPlz()+" "+dta301mod.getX_patOrt()+"</td></tr>");
		buf.append("<tr><td class='rbtherapie'>Krankenkasse</td><td>"+dta301mod.getX_patKassenName()+"</td></tr>");
		buf.append("<tr><td class='rbtherapie'>Patienten-ID</td><td>"+dta301mod.getX_patIntern()+"</td></tr>");
		/****************************/
		buf.append("</table>");
		buf.append("</html>");
		editpan[1].setText(buf.toString());
		
	}	
	private void show301NoPatExist(){
		StringBuffer buf = new StringBuffer();
		buf.append("<html><head>");
		buf.append(
		 "<style type='text/css'>.linksbuendig {text-align: left;}"+
         ".rechtsbuendig {text-align: right;}"+
         ".rbrot {text-align: right;color:#ff0000;}"+
         ".rbblau {text-align: right;color:#0000ff;}"+
         ".rbtherapie {text-align: right;color:#e77817;}"+
         ".zentriert {text-align: center;}"+
         ".blocksatz {text-align: justify;}"+
         "</style>" ); 
		buf.append("</head>");
		buf.append("<table style='font-family:Arial'>");
		buf.append("<tr><td colspan='2';class='rbrot'>Patient existiert nicht</td></tr>");
		buf.append("</table>");
		buf.append("</html>");
		editpan[1].setText(buf.toString());		
	}
	private void show301WasCreated(String patcreate){
		StringBuffer buf = new StringBuffer();
		buf.append("<html><head>");
		buf.append(
		 "<style type='text/css'>.linksbuendig {text-align: left;}"+
         ".rechtsbuendig {text-align: right;}"+
         ".rbrot {text-align: right;color:#ff0000;}"+
         ".rbblau {text-align: right;color:#0000ff;}"+
         ".rbtherapie {text-align: right;color:#e77817;}"+
         ".zentriert {text-align: center;}"+
         ".blocksatz {text-align: justify;}"+
         "</style>" ); 
		buf.append("</head>");
		buf.append("<table style='font-family:Arial'>");
		buf.append("<tr><td colspan='2';class='rbrot'>Patient wurde angelegt mit ID = "+patcreate+"</td></tr>");
		buf.append("</table>");
		buf.append("</html>");
		editpan[1].setText(buf.toString());		
	}
	
	private void doSucheNachPat(){
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				try{
					patcombo.removeActionListener(al);
					patcombo.removeAllItems();
					String pat = tab.getValueAt(tab.getSelectedRow(), 4).toString();
					String geboren = tab.getValueAt(tab.getSelectedRow(), 12).toString();
					String cmd = "select concat(n_name,', ',v_name,', ',DATE_FORMAT(geboren,'%d.%m.%Y')) as name,anrede,n_name,v_name,geboren,strasse,plz,ort,pat_intern,kasse,id,kassenid,arzt,arztid,kv_nummer,arzt_num from pat5 where n_name='"+pat.split("#")[1]+"' and "+
					//String cmd = "select n_name,geboren,strasse,plz,ort,pat_intern,id from pat5 where n_name='"+pat.split("#")[1]+"' and "+
					"v_name='"+pat.split("#")[2]+"' and geboren = '"+geboren+"'";
					vec_patstamm = SqlInfo.holeFelder(cmd);
					
					if(vec_patstamm.size() > 0){
						for(int i = 0; i < vec_patstamm.size();i++){
							patcombo.addItem(Integer.toString(i+1)+" von "+Integer.toString(vec_patstamm.size())+" - "+StringTools.EGross(String.valueOf(vec_patstamm.get(i).get(0)).toString()));
						}
						patcombo.addActionListener(al);
						patcombo.setSelectedIndex(0);
						return null;
					}else{
						show301NoPatExist();
					}
					patcombo.addActionListener(al);
				}catch(Exception ex){
					ex.printStackTrace();
				}

				return null;
			}
			
		}.execute();

	}
	
/********************************************************/	
	class MyTableModel extends DefaultTableModel{
		   /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Class<?> getColumnClass(int columnIndex) {
			   if(columnIndex==1 ){
				   return Boolean.class;}
			   else{
				   return String.class;
			   }
	       }

	    public boolean isCellEditable(int row, int col) {

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
	class MyAuswertungListSelectionHandler implements ListSelectionListener {
		
	    public void valueChanged(ListSelectionEvent e) {
	        ListSelectionModel lsm = (ListSelectionModel)e.getSource();
	        
	        //int firstIndex = e.getFirstIndex();
	        //int lastIndex = e.getLastIndex();
	        boolean isAdjusting = e.getValueIsAdjusting();
	        if(isAdjusting){
	        	return;
	        }
			//StringBuffer output = new StringBuffer();
	        if (lsm.isSelectionEmpty()) {

	        } else {
	            int minIndex = lsm.getMinSelectionIndex();
	            int maxIndex = lsm.getMaxSelectionIndex();
	            for (int i = minIndex; i <= maxIndex; i++) {
	                if (lsm.isSelectedIndex(i)) {
	                	dta301mod.setDtaID();
	                	dta301mod.set301Data();
	    				show301PatData(i);
	    				doSucheNachPat();
	    				if(! (Boolean) tab.getValueAt(i, 1)){
	    					do301KommData();
		    				rezneuangelegt = false;
		    				patneuangelegt = false;
		    				buts[0].setEnabled(true);
		    				buts[1].setEnabled(true);
	    				}else{
		    				rezneuangelegt = true;
		    				patneuangelegt = true;
		    				buts[0].setEnabled(false);
		    				buts[1].setEnabled(false);
	    				}
	                    break;
	                }
	            }
	        }

	    }
	}
/**************************************************************/
	private void do301KommData(){
		
	}
/**************************************************************/
	private void doPatNeuanlage(){
//Sind sind sich sicher daß keiner der rechts aufgeführten Patienten der richtige ist???
		if(patcombo.getItemCount() > 0){
			int anfrage = JOptionPane.showConfirmDialog(null, "Sind Sie sicher daß kein passender Patient in Ihrem Pat-Stamm vorhanden ist?", "Achtung wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
			if(anfrage != JOptionPane.YES_OPTION){
				return;	
			}
		}

		try{
			//Zuerst das Kassengedönse
			if(dta301mod.getPatKassenIk()==null || dta301mod.getPatKassenIk().equals("")){JOptionPane.showMessageDialog(null,"IK der Krankenkasse ist nicht verfügbar, Patientenanlage wird abgebrochen");}
			String cmdkassex = "select id from kass_adr where ik_kasse='"+dta301mod.getPatKassenIk()+"' LIMIT 1";
			String idkassex = SqlInfo.holeEinzelFeld(cmdkassex);
			if(idkassex.trim().equals("")){
				String[] retwerte = kassenAuswahl(new String[] {dta301mod.getPatKassenIk(),""});
				if(retwerte[2].equals("-1") || retwerte[2].equals("")){
					JOptionPane.showMessageDialog(null, "<html><b>Achtung!!!<br><br><b>Kasse</b> konnte nicht zugeordnet werden<br><br>Patientenanlage wird abgebrochen!</html>");
					return;
				}
				dta301mod.setPatKassenId(String.valueOf(retwerte[2]));
				dta301mod.setPatKassenName(String.valueOf(retwerte[0]));
			}else{
				dta301mod.setPatKassenId(String.valueOf(idkassex));
				dta301mod.setPatKassenName(SqlInfo.holeEinzelFeld("select kassen_nam1 from kass_adr where id='"+idkassex+"' LIMIT 1"));
			}
			//Jetzt den Arzt
			String[] retwerte = arztAuswahl(new String[] {"",""});
			if(retwerte[2].equals("-1") || retwerte[2].equals("")){
				JOptionPane.showMessageDialog(null, "<html><b>Achtung!!!<br><br><b>Arzt</b> konnte nicht zugeordnet werden<br><br>Patientenanlage wird abgebrochen!</html>");
				return;
			}
			dta301mod.setPatArztName(String.valueOf(retwerte[0]));
			dta301mod.setPatArztNummer(String.valueOf(retwerte[1]));
			dta301mod.setPatArztId(String.valueOf(retwerte[2]));
			
			int patnummer = SqlInfo.erzeugeNummer("pat");
			patneuepatnr = Integer.toString(patnummer);
			patneuangelegt = true;

			dta301mod.setPatIntern(String.valueOf(patneuepatnr));
			//so jetzt haben wir alles was wir brauchen das wollen wir jetzt sehen//
			//dta301mod.show301Data();
			//dta301mod.show_X_PatData();
			
			StringBuffer buf = new StringBuffer();
			buf.append("insert into pat5 set ");
			buf.append("pat_intern='"+patneuepatnr+"', ");
			buf.append("anrede='"+dta301mod.getPatAnrede()+"', ");
			buf.append("n_name='"+dta301mod.getPatNachname()+"', ");
			buf.append("v_name='"+dta301mod.getPatVorname()+"', ");
			buf.append("strasse='"+dta301mod.getPatStrasse()+"', ");
			buf.append("plz='"+dta301mod.getPatPlz()+"', ");
			buf.append("ort='"+dta301mod.getPatOrt()+"', ");
			buf.append("geboren='"+DatFunk.sDatInSQL(dta301mod.getPatGeboren())+"', ");
			buf.append("anamnese='"+"Pat wurde vom 301-er automatisch angelegt\nVSNR-"+dta301mod.getPatVsnr()+"-', ");
			buf.append("kassenid='"+dta301mod.getPatKassenId()+"', ");
			buf.append("kasse='"+dta301mod.getPatKassenName()+"', ");
			buf.append("kv_nummer='"+dta301mod.getPatKassenIk()+"', ");
			buf.append("arzt='"+dta301mod.getPatArztName()+"', ");
			buf.append("arzt_num='"+dta301mod.getPatArztNummer()+"', ");
			buf.append("arztid='"+dta301mod.getPatArztId()+"'");
			SqlInfo.sqlAusfuehren(buf.toString());
			show301WasCreated(patneuepatnr);
			System.out.println(buf.toString());
			JOptionPane.showMessageDialog(null, "<html>Der Patient wurde <b>erfolgreich</b> angelegt.</html>");			
		}catch(Exception ex){
			JOptionPane.showMessageDialog(null, "<html><b>Fehler bei der Anlage des Patienten !!!</b></html>");
		}
		buts[0].setEnabled(false);
	}
	/*******************************************************************************/
	/*******************************************************************************/
	private void doRezNeuanlage(){
		String id = dta301mod.getDtaId();//tab.getValueAt(tab.getSelectedRow(), 11).toString();
		String ktraeger = dta301mod.getDtaKtraegerIK();//tab.getValueAt(tab.getSelectedRow(), 7).toString();
		String welcherpat = "";
		if(patcombo.getItemCount() > 0 && (!patneuangelegt)){
			int anfrage = JOptionPane.showConfirmDialog(null, "Wollen Sie die Verordnung auf den (rechts) ausgewählten Patienten übertragen?\nGewähle Patienten-ID = "+vec_patstamm.get(patcombo.getSelectedIndex()).get(8), "Achtung wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
			if(anfrage != JOptionPane.YES_OPTION){
				return;	
			}
			welcherpat = dta301mod.getX_patIntern();//String.valueOf(vec_patstamm.get(patcombo.getSelectedIndex()).get(8));
			dta301mod.transferXPatDataTo301();
		}else if(patcombo.getItemCount() <= 0 && (patneuangelegt) ){
			welcherpat = dta301mod.getPatIntern();//patneuepatnr;
		}else{
			JOptionPane.showMessageDialog(null, "Kein bestehender Patient ausgewählt und kein neuer Patient angelegt - das geht einfach nicht!!!!");
			return;
		}
		Vector<Object> vecobj = doSetPatientFuerNachricht(welcherpat);
		
		doNurZumTesten(vecobj);
		
		JOptionPane.showMessageDialog(null, "<html>Die Verordnung wurde <b>erfolgreich</b> angelegt.</html>");
		int preisgruppe = Integer.parseInt((String)vecobj.get(6));
		int posgruppe = 0;
		String disziplin = null;
		if( ((String)vecobj.get(4)).equals("MED") ){posgruppe=2; disziplin="Reha";	}
		if( ((String)vecobj.get(4)).equals("AR") ){posgruppe=3;	disziplin="Reha";}
		if( ((String)vecobj.get(4)).equals("NACHSORGE") ){posgruppe=4;	disziplin="Physio";}
		
		
		String kuerzel = (String) ((Object[])vecobj.get(0))[posgruppe]; // (String)vecobj.get(posgruppe); 

		String preisid = RezTools.getIDFromKurzform(kuerzel, SystemPreislisten.hmPreise.get(disziplin).get(preisgruppe-1));	

		String preis = RezTools.getPreisAktFromID(preisid, SystemPreislisten.hmPreise.get(disziplin).get(preisgruppe-1));
		
		String preispos = RezTools.getPosFromID(preisid, null, SystemPreislisten.hmPreise.get(disziplin).get(preisgruppe-1));

		System.out.println("Der Preis beträgt "+preis);
		System.out.println("Preis-ID = "+preisid);
		System.out.println("Kürzel = "+kuerzel);
		System.out.println("Die Positionsnummer = "+preispos);
		
		String kid = SqlInfo.holeEinzelFeld("select id from kass_adr where ik_kasse ='"+ktraeger+"' LIMIT 1");
 
		if(kid.trim().equals("")){
			
			String meldung = "Ein Kostenträger mit der IK "+ktraeger+" ist im aktuellen Kassen-Stamm\n"+
			"nicht enthalten!!!!\n\n"+
			"Bitte legen Sie zuerst den Kostenträger in Thera-Pi an.";
			
			JOptionPane.showMessageDialog(null,meldung);
			return;
		}
		
		String ktraegername = SqlInfo.holeEinzelFeld("select kassen_nam1 from kass_adr where id ='"+kid+"' LIMIT 1");
		
		int reznummer = SqlInfo.erzeugeNummer((disziplin.equals("Reha") ? "rh" : "kg"));

		rezneuereznr = (disziplin.equals("Reha") ? "RH" : "KG")+Integer.toString(reznummer);
		rezneuangelegt = true;
		StringBuffer buf = new StringBuffer();
		buf.append("insert into verordn set ");
		buf.append("pat_intern='"+dta301mod.getPatIntern()+"', ");
		buf.append("rez_nr='"+rezneuereznr+"', ");
		buf.append("rez_datum='"+SqlInfo.holeEinzelFeld("select datum from dta301 where id='"+dta301mod.getDtaId()+"' LIMIT 1")+"', ");
		buf.append("datum='"+DatFunk.sDatInSQL(DatFunk.sHeute())+"', ");
		String anzahlen = SqlInfo.holeEinzelFeld("select tage from dta301 where id='"+dta301mod.getDtaId()+"' LIMIT 1");
		buf.append("anzahl1='"+anzahlen+"', ");
		buf.append("anzahl2='"+anzahlen+"', ");
		buf.append("anzahl3='"+anzahlen+"', ");
		buf.append("anzahl4='"+anzahlen+"', ");
		buf.append("art_dbeh1='"+preisid+"', ");
		buf.append("preise1='"+preis+"', ");
		buf.append("pos1='"+preispos+"', ");
		buf.append("kuerzel1='"+kuerzel+"', ");
		buf.append("dauer='"+(disziplin.equals("Reha") ? "30" : "15")+"', ");
		buf.append("zzregel='"+"0"+"', ");
		buf.append("zzstatus='"+"0"+"', ");
		buf.append("kid='"+kid+"', ");
		buf.append("ktraeger='"+ktraegername+"', ");
		buf.append("arzt='"+dta301mod.getPatArztName()+"', ");
		buf.append("arztid='"+dta301mod.getPatArztId()+"', ");
		buf.append("farbcode='"+""+"', ");
		buf.append("jahrfrei='"+""+"', ");
		buf.append("frequenz='"+(disziplin.equals("Reha") ? "5" : "")+"', ");
		buf.append("barcodeform='"+(disziplin.equals("Reha") ? "4" : "0")+"', ");
		buf.append("preisgruppe='"+Integer.toString(preisgruppe)+"', ");
		buf.append("angelegtvon='"+"dta301"+"', ");
		buf.append("diagnose='"+(String)vecobj.get(3)+"\n"+(String)vecobj.get(5)+"'");
		System.out.println("\n"+buf.toString());
		SqlInfo.sqlAusfuehren(buf.toString());
		/************Jetzt die Dta301 beschreiben********/
		buf.setLength(0);
		buf.trimToSize();
		buf.append("update dta301 set rez_nr='"+rezneuereznr+"', ");
		buf.append("pat_intern='"+dta301mod.getPatIntern()+"', ");
		buf.append("eingelesen='"+"T"+"', ");
		buf.append("eingelesenam='"+DatFunk.sDatInSQL(DatFunk.sHeute())+"' ");
		buf.append("where id='"+id+"' LIMIT 1");
		SqlInfo.sqlAusfuehren(buf.toString());
		/************Dann die Tabelle regeln********/
		int xrow = tab.convertRowIndexToModel(tab.getSelectedRow());
		tabmod.setValueAt(Boolean.valueOf(true),xrow, 1);
		tabmod.setValueAt((String)DatFunk.sHeute(),xrow, 13);
		buts[1].setEnabled(false);
		buts[0].setEnabled(false);
	}
	
	
	public void doNurZumTesten(Vector<Object> vecobj){
		for(int i = 0; i < vecobj.size(); i++){
			if(vecobj.get(i) instanceof Object[]){
				for(int o = 0; o < ((Object[])vecobj.get(i)).length;o++){
					System.out.println("Object["+Integer.toString(o)+"] = "+((Object[])vecobj.get(i))[o] );
				}
			}else{
				String[] titel = {"","Patient=","Kostenträger=","Diagnose=","Fallart=",
						"Eilfall=","Preisgruppe=","Diagnosegruppe="
				};
				/*
				System.out.println("Patient="+patBetroffen);
				System.out.println("Kostenträger="+ktraeger);
				System.out.println("Diagnose="+diagnose);
				System.out.println("Fallart="+fallart);
				System.out.println("Eilfall="+eilfall);
				System.out.println("Preisgruppe="+preisgruppe);
				System.out.println("Diagnosegruppe="+diaggruppe);
				*/
				System.out.println(Integer.toString(i)+" - "+titel[i]+vecobj.get(i));
			}
			
		}
		/*
		for(int i = 0; i < SystemPreislisten.hmPreise.get("Reha").size();i++){
				
		}
		*/
		
	}


	private String[] kassenAuswahl(String[] suchenach){
		String[] ret = {"","",""};
		JRtaTextField rettf1 = new JRtaTextField("nix",false);
		JRtaTextField rettf2 = new JRtaTextField("nix",false);
		JRtaTextField rettf3 = new JRtaTextField("nix",false);
		//String[] suchenachx = new String[] {"",""};
		KassenAuswahl kwahl = new KassenAuswahl(null,"KassenAuswahl",suchenach,new JRtaTextField[] {rettf1,rettf2,rettf3},"");
		kwahl.setModal(true);
		kwahl.setLocationRelativeTo(this);
		kwahl.setVisible(true);
		kwahl.dispose();
		kwahl = null;
		ret[0] = rettf1.getText().trim();
		ret[1] = rettf2.getText().trim();
		ret[2] = rettf3.getText().trim();
		return(ret);
	}
	
	private String[] arztAuswahl(String[] suchenach){
		String[] ret = {"","",""};
		JRtaTextField rettf1 = new JRtaTextField("nix",false);
		JRtaTextField rettf2 = new JRtaTextField("nix",false);
		JRtaTextField rettf3 = new JRtaTextField("nix",false);
		ArztAuswahl awahl = new ArztAuswahl(null,"ArztAuswahl",suchenach,new JRtaTextField[] {rettf1,rettf2,rettf3},"");
		awahl.setModal(true);
		awahl.setLocationRelativeTo(this);
		awahl.setVisible(true);
		awahl.dispose();
		awahl = null;
		ret[0] = rettf1.getText().trim();
		ret[1] = rettf2.getText().trim();
		ret[2] = rettf3.getText().trim();
		System.out.println(ret[0]);
		System.out.println(ret[1]);
		System.out.println(ret[2]);
		return(ret);

	}
	/************************************************************/
	private static class JXTreeNode extends DefaultMutableTreeNode {
    	/**
		 * 
		 */
		private static final long serialVersionUID = 2195590211796817012L;
		@SuppressWarnings("unused")
		public boolean enabled = false;
    	private KnotenObjekt knotenObjekt = null;
    	public JXTreeNode(KnotenObjekt obj,boolean enabled){
    		super();
    		this.enabled = enabled;
   			this.knotenObjekt = obj;
   			if(obj != null){
   				this.setUserObject(obj);
   			}
    	}
    	/*
		public boolean isEnabled() {
			return enabled;
		}
		*/
		
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
		public String ikkasse;
		public String preisgruppe;
		
		public KnotenObjekt(String titel,String rez_num,boolean fertig,String ikkasse,String preisgruppe){
			this.titel = titel;
			this.fertig = fertig;
			this.rez_num = rez_num;
			this.ikkasse = ikkasse;
			this.preisgruppe = preisgruppe;
		}
	}
	/*************************************/	
	private class MyRenderer extends DefaultTreeCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2333990367290526356L;
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
		KnotenObjekt o = ((JXTreeNode)value).knotenObjekt;
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
	
	

