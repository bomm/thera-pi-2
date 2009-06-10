package systemEinstellungen;



import hauptFenster.Reha;
import hauptFenster.UIFSplitPane;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.decorator.SortOrder;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import rehaContainer.RehaTP;
import systemTools.JRtaTextField;
import systemTools.Verschluesseln;
import systemTools.WinNum;
import dialoge.PinPanel;
import dialoge.RehaSmartDialog;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;




public class SystemUtil extends RehaSmartDialog implements TreeSelectionListener, ActionListener, WindowListener, KeyListener, RehaTPEventListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3482074172384055074L;
	private int setOben;

	private RehaTPEventClass rtp = null;
	private JXPanel jp1 = null;
	private ArrayList<String[]> termine = new ArrayList<String[]>();
	private JXPanel jtp = null;
	private String dieserName = "";
	private JXTable pliste = null;
	private RehaTP  jp;
	private JRtaTextField tfSuche = null;
	private JXButton heute = null;
	private JXButton heute4 = null;
	private JTextArea tae = null;
	private JXLabel lblsuche = null;
	private JXLabel lbldatum = null;
	private JXTable ttbl = null;
	private String aktDatum = "";
	private Vector vTdata = new Vector();
	public static SystemUtil thisClass = null;

	private JXHeader header = null;  
	private UIFSplitPane jSplitLR = null;
	private JXPanel jxLinks = null;
	private JXPanel jxRechts = null;
	private JXPanel jxInhaltRechts = null;
	
	public JScrollPane parameterScroll = null;
	private JScrollPane panelScroll = null;
	private HashMap<String,String> htitel = new HashMap<String,String>();
	private HashMap<String,String> hdescription = new HashMap<String,String>();
	private HashMap<String,ImageIcon> hicon = new HashMap<String,ImageIcon>();
	public DefaultMutableTreeNode root;
	public DefaultTreeModel treeModel;
	public JTree tree;
	public SystemUtil(JXFrame owner){
		
		//super(frame, titlePanel());
		super(owner,"SystemUtil");
		this.dieserName = "SystemUtil"+WinNum.NeueNummer();
		thisClass = this;
		setName(this.dieserName);
		getSmartTitledPanel().setName(this.dieserName);

		this.setModal(true);
		this.addWindowListener(this);
		this.addKeyListener(this);
		this.setUndecorated(true);
		this.setContentPanel(titlePanel() );
		getSmartTitledPanel().setTitle("Systeminitialisierung");
		PinPanel pinPanel = new PinPanel();
		pinPanel.getGruen().setVisible(false);
		pinPanel.setName(dieserName);
		pinPanel.setzeName(dieserName);
		this.jp.setzeName(dieserName);
		setPinPanel(pinPanel);
		rtp = new RehaTPEventClass();
		rtp.addRehaTPEventListener((RehaTPEventListener) this);
		
		
		
		JXPanel jp1 = new JXPanel();
		jp1.setBorder(null);
		jp1.setBackground(Color.WHITE);
        jp1.setLayout(new BorderLayout());
        //jp1.setLayout(new VerticalLayout(1));
        String ss = Reha.proghome+"icons/header-image.png"; //"icons/header-image.png";
        header = new JXHeader("Mit der Systeminitialisierung....",
                "....erstellen bzw. ändern Sie die Systemeinstellungen auf Ihre inividuelle Bedürfnisse hin.\n" +
                "Von den Behandlersets des Terminkalender, Aktualisierungszyklen bis hin zu Pfadangaben und Emailparameter.\n" +
                "Systemparameter die ein gewisses Maß an Computer-Kenntnisse voraussetzen haben wir mit einem roten Ausrufezeichen versehen.\n\n"+
                "Sie schließen dieses Fenster über den roten Punkt rechts oben, oder mit der Taste >>ESC<<.",
                new ImageIcon(ss));
        header.setPreferredSize(new Dimension(0,150));
        	jp1.add(header,BorderLayout.NORTH);
        	jxLinks = new JXPanel(new BorderLayout());
        	jxLinks.setBackground(Color.WHITE);
        	JXPanel dummy = new JXPanel();
        	dummy.setBorder(null);
        	dummy.setBackground(Color.WHITE);
        	dummy.setPreferredSize(new Dimension(10,20));
        	jxLinks.add(dummy,BorderLayout.NORTH);
        	dummy = new JXPanel();
        	dummy.setBorder(null);
        	dummy.setBackground(Color.WHITE);
        	dummy.setPreferredSize(new Dimension(10,20));
        	jxLinks.add(dummy,BorderLayout.SOUTH);
        	dummy = new JXPanel();
        	dummy.setBorder(null);
        	dummy.setBackground(Color.WHITE);
        	dummy.setPreferredSize(new Dimension(10,20));
        	jxLinks.add(dummy,BorderLayout.WEST);
        	dummy = new JXPanel();
        	dummy.setBorder(null);
        	dummy.setBackground(Color.WHITE);
        	dummy.setPreferredSize(new Dimension(10,20));
        	jxLinks.add(dummy,BorderLayout.EAST);

        	jxLinks.add(getParameterListe(),BorderLayout.CENTER);
        	// hier muß das add für die weitern Panels rein
        	jxRechts = new JXPanel(new BorderLayout());
    		/****/
   	     	Point2D start = new Point2D.Float(0, 0);
   	     	Point2D end = new Point2D.Float(400,500);
   	     	float[] dist = {0.0f, 0.5f};
   	     	Color[] colors = {Color.WHITE,getBackground()};
   	     	LinearGradientPaint p =
   	     		new LinearGradientPaint(start, end, dist, colors);
   	     	MattePainter mp = new MattePainter(p);
   	     	jxRechts.setBackgroundPainter(new CompoundPainter(mp));
   	     	/****/
        	// hier muß das add für die weitern Panels rein
        	jSplitLR =  UIFSplitPane.createStrippedSplitPane(JSplitPane.HORIZONTAL_SPLIT,
        		jxLinks,
        		jxRechts);        
			jSplitLR.setBackground(Color.WHITE);
			jSplitLR.setDividerSize(7);
			//jSplitLR.setDividerBorderVisible(false);
			jSplitLR.setDividerBorderVisible(true);
			jSplitLR.setName("GrundSplitLinksRechts");
			jSplitLR.setOneTouchExpandable(true);
			jSplitLR.setDividerLocation(260);

			((BasicSplitPaneUI) jSplitLR.getUI()).getDivider().setBackground(Color.WHITE);
			jp1.add(jSplitLR,BorderLayout.CENTER);
			jp1.revalidate();
			this.jtp.add(jp1);
			this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			auswertenSysUtil("nix");
			SwingUtilities.invokeLater(new Runnable(){
				public  void run(){
					new Thread(){
						public void run(){
							headerInfos();							
						}
					}.start();

		 	  	}
			});			

			
	}		    
/*******************************************************/			
/*********************************************************/
	
public void FensterSchliessen(String welches){
	//System.out.println("Eltern-->"+this.getParent().getParent().getParent().getParent().getParent());
	//webBrowser.dispose();
	super.dispose();
	this.dispose();
}

private static JXPanel titlePanel(){
	SystemUtil.thisClass.jp = new RehaTP(0);
	SystemUtil.thisClass.jp.getContentContainer().setLayout(new BorderLayout());
	SystemUtil.thisClass.jtp = (JXPanel) SystemUtil.thisClass.jp.getContentContainer();
	SystemUtil.thisClass.jtp.setSize(new Dimension(200,200));
	SystemUtil.thisClass.jtp.setName(SystemUtil.thisClass.dieserName);
	SystemUtil.thisClass.jtp.setVisible(true);
	return SystemUtil.thisClass.jtp;
}


public String dieserName(){
	return this.getName();
}

public void RehaTPEventOccurred(RehaTPEvent evt) {
	// TODO Auto-generated method stub
	System.out.println("****************Schließen des SystemUtil-Fensters**************");
	String ss =  this.getName();
	System.out.println("SystemUtil "+this.getName()+" Eltern "+ss);
	try{
		//if (evt.getDetails()[0].equals(ss) && evt.getDetails()[1]=="ROT"){
			FensterSchliessen(evt.getDetails()[0]);
			rtp.removeRehaTPEventListener((RehaTPEventListener) this);
		//}	
	}catch(NullPointerException ne){
		System.out.println("In DruckFenster" +evt);
	}
}

public void actionPerformed(ActionEvent arg0) {
	String cmd = arg0.getActionCommand();
	for(int i = 0; i< 1;i++){
		if(cmd.equals("Heute")){
			break;
		}
	}	
}

@Override
public void keyPressed(KeyEvent arg0) {
	System.out.println(arg0.getKeyCode()+" - "+arg0.getSource());
	if(arg0.getKeyCode() == 27){
		arg0.consume();
		rtp.removeRehaTPEventListener((RehaTPEventListener) this);
		FensterSchliessen(null);
	}
	if(arg0.getKeyCode() == 10){
				arg0.consume();
	}
}
@Override
public void keyReleased(KeyEvent arg0) {
	// TODO Auto-generated method stub
	if(arg0.getKeyCode() == 10){
		arg0.consume();
	}
}
@Override
public void keyTyped(KeyEvent arg0) {
	// TODO Auto-generated method stub
	if(arg0.getKeyCode() == 27){
		rtp.removeRehaTPEventListener((RehaTPEventListener) this);
		FensterSchliessen(null);
	}	
	if(arg0.getKeyCode() == 10){
		arg0.consume();
	}
}

public void windowClosed(WindowEvent arg0) {
	// TODO Auto-generated method stub
	rtp.removeRehaTPEventListener((RehaTPEventListener) this);	
	Runtime r = Runtime.getRuntime();
    r.gc();
    long freeMem = r.freeMemory();
    System.out.println("Freier Speicher nach  gc():    " + freeMem); 
}


public void windowClosing(WindowEvent arg0) {
	// TODO Auto-generated method stub
	rtp.removeRehaTPEventListener((RehaTPEventListener) this);
}



private JScrollPane getParameterListe(){
	DefaultMutableTreeNode treeitem = null;
	parameterScroll = new JScrollPane();
	parameterScroll.setBorder(null);
	parameterScroll.setBackground(Color.WHITE);
	
	root = new DefaultMutableTreeNode( "System Reha-Verwaltung" );
	treeModel = new DefaultTreeModel(root);


	DefaultMutableTreeNode node = new DefaultMutableTreeNode( "Mandant(en) / Datenbank");
	treeitem = new DefaultMutableTreeNode("Firmenangaben Mandaten");
	node.add(treeitem ); 
	treeitem = new DefaultMutableTreeNode("Datenbankparameter");
	node.add(treeitem );
	root.add(node);
	/***/
	node = new DefaultMutableTreeNode( "Terminkalender"); 
	treeitem = new DefaultMutableTreeNode("Terminkal. Grundeinstellungen");
	node.add(treeitem ); 
	treeitem = new DefaultMutableTreeNode("Kalenderbenutzer verwalten");
	node.add(treeitem ); 
	treeitem = new DefaultMutableTreeNode("Behandlersets definieren");
	node.add(treeitem );
	treeitem = new DefaultMutableTreeNode("Druckvorlage Terminliste");
	node.add(treeitem );
	//treeitem = new DefaultMutableTreeNode("Termin-Drucker verwalten");
	//node.add(treeitem );	
	treeitem = new DefaultMutableTreeNode("Kalenderfarben definieren");
	node.add(treeitem ); 
	treeitem = new DefaultMutableTreeNode("Gruppentermine definieren");
	node.add(treeitem );	
	treeitem = new DefaultMutableTreeNode("Neues Kalenderjahr anlegen");
	node.add(treeitem );	
	//treeitem = new DefaultMutableTreeNode("Feiertage eintragen");
	//node.add(treeitem );	
	root.add(node);
	/***/	
	node = new DefaultMutableTreeNode( "[Ru:gl]");
	treeitem = new DefaultMutableTreeNode("Ru:gl-Grundeinstellungen");
	node.add(treeitem);
	treeitem = new DefaultMutableTreeNode("Ru:gl-Gruppen definieren");
	node.add(treeitem);
	root.add(node);
	/***/
	/***/	
	node = new DefaultMutableTreeNode( "Stammdaten Optionen");
	treeitem = new DefaultMutableTreeNode("Patient");
	node.add(treeitem);
	treeitem = new DefaultMutableTreeNode("Rezepte");
	node.add(treeitem);
	treeitem = new DefaultMutableTreeNode("Krankenkasse");
	node.add(treeitem);
	treeitem = new DefaultMutableTreeNode("Arzt");
	node.add(treeitem);
	root.add(node);
	/***/
	node = new DefaultMutableTreeNode( "Emailparameter");
	root.add(node);
	/***/
	node = new DefaultMutableTreeNode( "Geräteverwaltung"); 
	treeitem = new DefaultMutableTreeNode("KV-Kartenlesegerät");
	node.add(treeitem ); 
	treeitem = new DefaultMutableTreeNode("Dokumentenscanner");
	node.add(treeitem ); 
	treeitem = new DefaultMutableTreeNode("Barcode-Scanner/Drucker");
	node.add(treeitem ); 
	treeitem = new DefaultMutableTreeNode("EC-Karten Lesegerät");
	node.add(treeitem );
	root.add(node);
	/***/
	node = new DefaultMutableTreeNode( "Preislisten");
	treeitem = new DefaultMutableTreeNode("Heilmittelkatalog einlesen");
	node.add(treeitem ); 
	treeitem = new DefaultMutableTreeNode("Tarifzuordnungen");
	node.add(treeitem ); 
	treeitem = new DefaultMutableTreeNode("Preise-Physiopraxis");
	node.add(treeitem ); 
	treeitem = new DefaultMutableTreeNode("Preise-Massagepraxis");
	node.add(treeitem ); 
	treeitem = new DefaultMutableTreeNode("Preise-Ergopraxis");
	node.add(treeitem ); 
	treeitem = new DefaultMutableTreeNode("Preise-Logopraxis");
	node.add(treeitem ); 
	treeitem = new DefaultMutableTreeNode("Reha-Tagessätze");
	node.add(treeitem ); 
	root.add(node);
	/***/
	node = new DefaultMutableTreeNode( "Fortlaufender Nummernkreis");
	treeitem = new DefaultMutableTreeNode("Patientennummer");
	node.add(treeitem ); 
	treeitem = new DefaultMutableTreeNode("Rezeptnummer");
	node.add(treeitem ); 
	treeitem = new DefaultMutableTreeNode("Rechnungsnummer");
	node.add(treeitem ); 
	root.add(node);
	/***/
	node = new DefaultMutableTreeNode( "Abrechnung §302");
	treeitem = new DefaultMutableTreeNode("ITSG Zertifikat anfordern/einlesen");
	node.add(treeitem ); 
	treeitem = new DefaultMutableTreeNode("Annahmekey abholen/einlesen");
	node.add(treeitem ); 
	root.add(node);

	tree = new JTree( root );
	tree.getSelectionModel().addTreeSelectionListener(this); 


	parameterScroll.setViewportView(tree);
	return parameterScroll; 
}
/*******************************************************************/
private void auswertenSysUtil(String util){
	System.out.println("SysUtil = "+util);
	for(int i = 0; i<1; i++){
		if(jxInhaltRechts!=null){
			cursorWait(true);
			//jxInhaltRechts.setVisible(false);
			jxRechts.remove(jxInhaltRechts);
			jxInhaltRechts.setVisible(false);
			jxInhaltRechts = null;
		}else{
			cursorWait(true);
		}
		if(util.equals("Behandlersets definieren")){

			jxInhaltRechts = new SysUtilBehandlerset();
			jxInhaltRechts.setVisible(true);
			jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
			jxRechts.revalidate();
			cursorWait(false);			
			setHeader("Behandlersets");
			break;
		}
		if(util.equals("Kalenderbenutzer verwalten")){
			//headerInfos(0);
			//SwingUtilities.invokeLater(new Runnable(){
				//public  void run(){
			jxInhaltRechts = new SysUtilKalenderBenutzer();
			jxInhaltRechts.setVisible(true);
			jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
			jxRechts.revalidate();
			cursorWait(false);
			setHeader("Kalenderbenutzer");
			//}
			//});			
			break;
		}
		if(util.equals("Ru:gl-Gruppen definieren")){
			//headerInfos(0);
			//SwingUtilities.invokeLater(new Runnable(){
				//public  void run(){


					jxInhaltRechts = new SysUtilRoogleGruppen();
					jxInhaltRechts.setVisible(true);
					jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
					jxRechts.revalidate();
					cursorWait(false);
					setHeader("RoogleGrundeinstellungen");
			 	  	//}
			//});			
			break;
		}
		if(util.equals("Neues Kalenderjahr anlegen")){
			//headerInfos(0);
			//SwingUtilities.invokeLater(new Runnable(){
				//public  void run(){
					jxInhaltRechts = new SysUtilKalenderanlegen();
					jxInhaltRechts.setVisible(true);
					jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
					jxRechts.revalidate();
					cursorWait(false);
					setHeader("Kalenderjahranlegen");
					//}
			//});			
			break;
		}
		if(util.equals("Kalenderfarben definieren")){
			//headerInfos(0);
			//SwingUtilities.invokeLater(new Runnable(){
				//public  void run(){
			/*
					new Thread(){
						public void run(){*/
							jxInhaltRechts = new SysUtilKalenderfarben();
							jxInhaltRechts.setVisible(true);
							jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
							jxRechts.revalidate();
							cursorWait(false);
						/*}
					}.start();*/
		 	  	//}
			//});			
			setHeader("Kalenderfarben");
			break;
		}
		if(util.equals("Terminkal. Grundeinstellungen")){
			//headerInfos(0);
			/*
			SwingUtilities.invokeLater(new Runnable(){
				public  void run(){
					new Thread(){
						public void run(){
						*/
							jxInhaltRechts = new SysUtilKalendereinstell();
							jxInhaltRechts.setVisible(true);
							jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
							jxRechts.revalidate();
							cursorWait(false);
						/*}
					}.start();
		 	  	}
			});*/
			setHeader("TKGrundeinstellungen");			
			break;
		}
		if(util.equals("Gruppentermine definieren")){
			//headerInfos(0);
			/*
			SwingUtilities.invokeLater(new Runnable(){
				public  void run(){
					new Thread(){
						public void run(){*/
							jxInhaltRechts = new SysUtilGruppenDef();
							jxInhaltRechts.setVisible(true);
							jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
							jxRechts.revalidate();
							cursorWait(false);
						/*}
					}.start();
		 	  	}
			});*/			
			setHeader("Gruppentermine");
			break;
		}
		if(util.equals("Emailparameter")){
			/*
			SwingUtilities.invokeLater(new Runnable(){
				public  void run(){
					new Thread(){
						public void run(){*/
							jxInhaltRechts = new SysUtilEmailparameter();
							jxInhaltRechts.setVisible(true);
							jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
							jxRechts.revalidate();
							cursorWait(false);
						/*}
					}.start();
		 	  	}
			});*/			
			break;
		}
		if(util.equals("Druckvorlage Terminliste")){
			/*
			SwingUtilities.invokeLater(new Runnable(){
				public  void run(){
					new Thread(){
						public void run(){
						*/
							jxInhaltRechts = new SysUtilDruckvorlage();
							jxInhaltRechts.setVisible(true);
							jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
							jxRechts.revalidate();
							cursorWait(false);
						/*}
					}.start();
		 	  	}
			});*/			
			setHeader("DruckvorlageTerminliste");
			break;
		}
		if(util.equals("Ru:gl-Grundeinstellungen")){
			/*
			SwingUtilities.invokeLater(new Runnable(){
				public  void run(){
					new Thread(){
						public void run(){
						*/
							jxInhaltRechts = new SysUtilRoogleEinstellungen();
							jxInhaltRechts.setVisible(true);
							jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
							jxRechts.revalidate();
							cursorWait(false);
						/*}
					}.start();
		 	  	}
			});*/			
			setHeader("RoogleGrundeinstellungen");
			break;
		}
		if(util.equals("Datenbankparameter")){
			/*
			SwingUtilities.invokeLater(new Runnable(){
				public  void run(){
					new Thread(){
						public void run(){
						*/
							jxInhaltRechts = new SysUtilDBdaten();
							jxInhaltRechts.setVisible(true);
							jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
							jxRechts.revalidate();
							cursorWait(false);
						/*}
					}.start();
		 	  	}
			});*/			
			setHeader("RoogleGrundeinstellungen");
			break;
		}
		if(util.equals("Firmenangaben Mandaten")){
			/*
			SwingUtilities.invokeLater(new Runnable(){
				public  void run(){
					new Thread(){
						public void run(){
						*/
							jxInhaltRechts = new SysUtilMandanten();
							jxInhaltRechts.setVisible(true);
							jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
							jxRechts.revalidate();
							cursorWait(false);
						/*}
					}.start();
		 	  	}
			});*/			
			setHeader("RoogleGrundeinstellungen");
			break;
		}
		if(util.equals("Patient")){
			/*
			SwingUtilities.invokeLater(new Runnable(){
				public  void run(){
					new Thread(){
						public void run(){
						*/
							jxInhaltRechts = new SysUtilPatient();
							jxInhaltRechts.setVisible(true);
							jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
							jxRechts.revalidate();
							cursorWait(false);
						/*}
					}.start();
		 	  	}
			});*/			
			setHeader("RoogleGrundeinstellungen");
			break;
		}
		if(util.equals("Arzt")){
			/*
			SwingUtilities.invokeLater(new Runnable(){
				public  void run(){
					new Thread(){
						public void run(){
						*/
							jxInhaltRechts = new SysUtilArzt();
							jxInhaltRechts.setVisible(true);
							jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
							jxRechts.revalidate();
							cursorWait(false);
						/*}
					}.start();
		 	  	}
			});*/			
			setHeader("RoogleGrundeinstellungen");
			break;
		}
		if(util.equals("Krankenkasse")){
			/*
			SwingUtilities.invokeLater(new Runnable(){
				public  void run(){
					new Thread(){
						public void run(){
						*/
							jxInhaltRechts = new SysUtilKrankenkasse();
							jxInhaltRechts.setVisible(true);
							jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
							jxRechts.revalidate();
							cursorWait(false);
						/*}
					}.start();
		 	  	}
			});*/			
			setHeader("RoogleGrundeinstellungen");
			break;
		}
		
		
		

		jxInhaltRechts = new SysUtilVorlage();
		jxInhaltRechts.setVisible(true);
		jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
		cursorWait(false);
		jxRechts.revalidate();

	}
	
}
/*******************************************/
private void cursorWait(boolean ein){
	if(!ein){
		this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}else{
		this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
	}
}
@Override
public void valueChanged(TreeSelectionEvent e) {
	// TODO Auto-generated method stub
	System.out.println(e);
	TreePath path = e.getNewLeadSelectionPath(); 
    String[] split = path.toString().split(",");
    auswertenSysUtil(split[split.length-1].replaceAll("\\]","").trim());
}

public static void abbrechen(){
	
	SystemUtil.thisClass.tree.setSelectionInterval(0,0);
	SystemUtil.thisClass.auswertenSysUtil("nix");
}
//private HashMap<String,String> htitel = new HashMap();
//private HashMap<String,String> hdescription = new HashMap();
//private HashMap<String,ImageIcon> hicon = new HashMap();

private void setHeader(String shm){
	header.setTitle(htitel.get(shm));
	header.setDescription(hdescription.get(shm));
	header.setIcon(hicon.get(shm));
}

private void headerInfos(){
	String sdummy = null;
	ImageIcon icodummy = null;
	/***********Numero uno***********/
	sdummy = "Terminkalender-Grundeinstellungen";
	htitel.put("TKGrundeinstellungen", sdummy);
	sdummy = "Die Grundeinstellungen beeinflussen die Struktur und die Funktionen der Kalenderdatenbank. Änderungen von Tagesbeginn \n"+
	"oder -ende führen zur Neudefinition ALLER bestehenden Datenbanken. Dieser Vorgang benötigt viel Rechenkapazität und dauert daher\n"+
	"entsprechend lange.\n"+
	"Der Refreshtakt definiert, in welchem Zyklus die Bildschirmanzeige mit der Datenbank abgeglichen wird. Einzelplatzinstallationen\n "+
	"arbeiten dabei ohne zeitliche Verzögerung.";
	hdescription.put("TKGrundeinstellungen", sdummy);
	icodummy = new ImageIcon("icons/header-image.png");
	hicon.put("TKGrundeinstellungen", icodummy);

	/***********Numero due***********/
	sdummy = "Kalenderbenutzer verwalten";
	htitel.put("Kalenderbenutzer", sdummy);
	sdummy = "´Kalenderbenutzer´ kann ein Behandler, sonstige Mitarbeiter oder auch eine besondere Ressource sein.\n\n"+
	"Der Matchcode ist die dargestellte Überschrift bzw. der in der Terminsuche verwendete Begriff. Er kann frei vergeben werden\n"+
	"und ist der Form nicht festgelegt. ´Peter´, ´Müller Martina´ oder ´Gewürzgurke007´ - Alles erlaubt!";
	hdescription.put("Kalenderbenutzer", sdummy);
	icodummy = new ImageIcon("icons/header-image.png");
	hicon.put("Kalenderbenutzer", icodummy);

	/***********Numero tre***********/
	sdummy = "Behandlersets definieren";
	htitel.put("Behandlersets", sdummy);
	sdummy = "In Behandlersets werden bis zu 7 Kalenderbenutzer gruppiert, die im Terminkalender gleichzeitig\n"+
	"nebeneinander dargestellt werden sollen. Im Terminkalender kann über F12 schnell zwischen den verschiedenen Sets\n"+
	"umgeschaltet werden.";
	hdescription.put("Behandlersets", sdummy);
	icodummy = new ImageIcon("icons/header-image.png");
	hicon.put("Behandlersets", icodummy);

	/***********Numero vier***********/
	sdummy = "Druckvorlage";
	htitel.put("DruckvorlageTerminliste", sdummy);
	sdummy = "In diesem Fenster können für jeden angeschlossenen Rechner Vorgaben zum Druck der Terminliste\n"+
	"gemacht werden. \n"+
	"Wenn Sie die Druckvorlagendatei hinsichtlich der Tabellen-, Zeilen- oder Spaltenzahl verändern, müssen diese\n"+
	"Strukturparameter unbedingt hier ebenfalls angepasst werden, da die Daten sonst nicht korrekt übergeben werden können."+
	"Angaben zu Papierformat und verwendetem Druckereinzug werden in der Vorlagendatei mit OpenOffice eingestellt.";
	hdescription.put("DruckvorlageTerminliste", sdummy);
	icodummy = new ImageIcon("icons/header-image.png");
	hicon.put("DruckvorlageTerminliste", icodummy);

	/***********Numero fünef***********/
	sdummy = "Kalenderfarben definieren";
	htitel.put("Kalenderfarben", sdummy);
	sdummy = "Die farbliche Darstellung der Termine im Kalender ist eine sehr effiziente Signalsprache. \n"+
	"Sie können zwischen 4 vordefinierten Farbkombinationen wählen oder für die jeweilige Bedeutung eine eigene\n"+
	"Kombination vergeben.\n"+
	"Besondere Bedeutung haben die Codes ´A bis ´H´, die mit einer Bedeutung frei verknüpft werden können.\n"+
	"Diese Codes können dann wie in der Tabelle angegeben in den Terminen hinter der Rezeptnummer eingetragen werden\n"+
	"und bestimmen die Farbgebung (der Code für die Behandlungszeit wird dann nachrangig).";
	hdescription.put("Kalenderfarben", sdummy);
	icodummy = new ImageIcon("icons/header-image.png");
	hicon.put("Kalenderfarben", icodummy);

	/***********Numero sex***********/
	sdummy = "Gruppentermine definieren";
	htitel.put("Gruppentermine", sdummy);
	sdummy = "Gruppentermine ist was für den Spezialisten\n"+
	"Der wird dann auch den Text schreiben....\n"+
	"...und beim Hilfetext richtig ins Zeugs legen!";
	hdescription.put("Gruppentermine", sdummy);
	icodummy = new ImageIcon("icons/header-image.png");
	hicon.put("Gruppentermine", icodummy);

	/***********Numero siebene***********/
	sdummy = "Kalenderjahr anlegen";
	htitel.put("Kalenderjahranlegen", sdummy);
	sdummy = "Hier wird die Kalenderdatenbank um ein weiteres Jahr verlängert. Sinnvoll ist dies, wenn zuvor\n"+
	"die Arbeitszeitpläne inklusive der Dauertermine auf den aktuellen Stand gebracht wurden (sofern gewünscht). \n"+
	"Die Feiertagsliste kann importiert werden und durch eigene Daten (z.B. Betriebsausflug, -ferien) ergänzt werden.";
	hdescription.put("Kalenderjahranlegen", sdummy);
	icodummy = new ImageIcon("icons/header-image.png");
	hicon.put("Kalenderjahranlegen", icodummy);

	/***********Numero achte***********/
	sdummy = "Ru:gl - Grundeinstellungen";
	htitel.put("RoogleGrundeinstellungen", sdummy);
	sdummy = "Die Grundeinstellungen für die Terminsuchmaschine [Ru:gl] legen die Vorgaben fest, mit denen\n"+
	"Ru:gl startet. Sie können natürlich für jeden Suchvorgang im Ru:gl-Fenster ´individueller Zeitraum´ angepasst werden.\n";
	hdescription.put("RoogleGrundeinstellungen", sdummy);
	icodummy = new ImageIcon("icons/header-image.png");
	hicon.put("RoogleGrundeinstellungen", icodummy);

	}

/******************************************/
}
