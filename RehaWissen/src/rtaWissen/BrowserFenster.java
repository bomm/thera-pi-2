package rtaWissen;




import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import rehaWissen.JRtaTextField;
import rehaWissen.RehaWissen;
import rehaWissen.SystemConfig;
import rehaWissen.UIFSplitPane;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;






public class BrowserFenster extends JFrame implements MouseListener,MouseMotionListener,ListSelectionListener,ActionListener, WindowListener, KeyListener, TreeSelectionListener{
	/**
	 * 
	 */
	public static BrowserFenster thisClass = null;
	private static final long serialVersionUID = -3482074172384055074L;
	private int setOben;


	private JXPanel jp1 = null;
	private ArrayList<String[]> termine = new ArrayList<String[]>();
	private JXPanel jtp = null;
	private String dieserName = "";
	private JXTable pliste = null;

	public JRtaTextField tfSuche = null;
	private JXButton heute = null;
	private JXButton heute4 = null;
	private JTextArea tae = null;
	private JXLabel lblsuche = null;
	private JXLabel lbldatum = null;
	private JXTable ttbl = null;
	private String aktDatum = "";
	private Vector vTdata = new Vector();

	private JList listSeiten = null;
	private JXHeader header = null;  
	private JXPanel jxLinks = null;
	private JXPanel jxRechts = null;
	private JXPanel jxInhaltRechts = null;
	
	public JScrollPane parameterScroll = null;
	private JScrollPane panelScroll = null;
	private HashMap<String,String> htitel = new HashMap<String,String>();
	private HashMap<String,String> hdescription = new HashMap<String,String>();
	private HashMap<String,ImageIcon> hicon = new HashMap<String,ImageIcon>();

	private JXPanel leftOPanel = null;
	private JXPanel leftUPanel = null;	
	private JXPanel rightPanel = null;
	private UIFSplitPane jSplitLR = null;
	private UIFSplitPane jSplitLinksOU = null;
	RtaWissen rtaWissen = null;
	private int linkalt=0,linkneu=0;
	private JXTitledPanel jtpx = null;
	public JXTable tblgefunden = null;
	public DefaultTableModel themenDtblm = null;
	

	
	
	JFormattedTextField jtf = null;
	public static DefaultMutableTreeNode root;
	public DefaultTreeModel treeModel;
	public JXTree tree;
	public Vector dateien = new Vector();
	
	public Connection conn = null;
	private BrowserSockServer bws = null;
	
	public BrowserFenster(JXFrame owner,String stitel){
		
		//super(frame, titlePanel());
		super();
		//super(owner,stitel);
		this.dieserName = stitel;
		//this.dieserName = "BrowserFenster"+WinNum.NeueNummer();
		thisClass = this;
		this.setName(this.dieserName);
		//getSmartTitledPanel().setName(this.dieserName);

		setTitle("Thera-PI - Browser");

		this.addWindowListener(this);
		this.addKeyListener(this);
		setPreferredSize(new Dimension(1024,750));
		this.setUndecorated(false);
		this.setBackground(Color.WHITE);
		//this.setContentPanel(new RtaWissen(1,"http://www.rta.de"));
		
		JXPanel jpan = new JXPanel(new BorderLayout());
		jpan.setPreferredSize(new Dimension(1024,750));
		jpan.setBackground(Color.WHITE);
		jpan.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		//this.setContentPanel(jpan);

		this.setContentPane(new JXPanel(new BorderLayout()));
		
		//getSmartTitledPanel().setTitle("Thera-PI - Browser");
		
		jSplitLR =  UIFSplitPane.createStrippedSplitPane(JSplitPane.HORIZONTAL_SPLIT,
        		getLeft(),
        		getRight()); 
		jSplitLR.setDividerSize(7);
		jSplitLR.setDividerBorderVisible(true);
		jSplitLR.setName("BrowserSplitLinksRechts");
		jSplitLR.setOneTouchExpandable(true);
		jSplitLR.setDividerLocation(230);
		//this.getContentPanel().add(jSplitLR,BorderLayout.CENTER);
		this.getContentPane().add(jSplitLR,BorderLayout.CENTER);
		jSplitLR.validate();
		listSeiten.requestFocus();
  
		
			
	}
	
	private UIFSplitPane getLeft(){
		leftOPanel = new JXPanel(new BorderLayout());
		leftOPanel.add(getSeiten(),BorderLayout.CENTER);
		leftOPanel.setBackground(new Color(255,229,191));
		leftUPanel = new JXPanel(new BorderLayout());
		leftUPanel.setBackground(Color.WHITE);
		leftUPanel.add(getLinksUnten(),BorderLayout.CENTER);

		jSplitLinksOU  =  UIFSplitPane.createStrippedSplitPane(JSplitPane.VERTICAL_SPLIT,
        		leftOPanel,
        		leftUPanel); 
		jSplitLinksOU.setDividerSize(7);
		jSplitLinksOU.setDividerBorderVisible(true);
		jSplitLinksOU.setName("BrowserSplitObenUnten");
		jSplitLinksOU.setOneTouchExpandable(true);
		jSplitLinksOU.setDividerLocation(150);
		jSplitLinksOU.validate();
		return jSplitLinksOU;
	}
	private JXPanel getRight(){
		rightPanel = new JXPanel(new BorderLayout());
		rightPanel.setBackground(Color.WHITE);
		rightPanel.addKeyListener(this);
		rightPanel.setBackground(Color.WHITE);
					// TODO Auto-generated method stub
					/*
					JTabbedPane tpane = new JTabbedPane();
					tpane.addTab("Hilfe", new RtaWissen(1,"http://www.thera-pi.org") );
					tpane.addTab("Browser", new RtaWissen(1,"http://www.thera-pi.org") );
					rightPanel.add(tpane);
					*/
					BrowserFenster.thisClass.setCursor(new Cursor(Cursor.WAIT_CURSOR));
					
					rtaWissen = new RtaWissen(1,SystemConfig.InetSeiten.get(0).get(2));
					//rtaWissen = new RtaWissen(1,"http://www.thera-pi.org"); 
					rightPanel.add(rtaWissen);
					rightPanel.validate();
					BrowserFenster.thisClass.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));				

		return rightPanel;
	}
	private JScrollPane getSeiten(){
		final DefaultListModel model = new DefaultListModel();
		listSeiten = new JList(model);
		listSeiten.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		listSeiten.setCellRenderer(new MyCellRenderer());
		listSeiten.setFixedCellHeight(20);
		listSeiten.addKeyListener(this);
		listSeiten.setName("liste");
		listSeiten.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
        		int ind = listSeiten.getSelectedIndex();
        		linkneu = ind;
            	rtaWissen.highlight = false;
       			rtaWissen.Navigiere(SystemConfig.InetSeiten.get(ind).get(2));
            }
        });




		ImageIcon icon = null;
		JXLabel lab = null;
		//System.out.println("InetSeiten-Anzahl = "+SystemConfig.InetSeiten.size());
		for(int i=0;i < SystemConfig.InetSeiten.size();i++){
			if(SystemConfig.InetSeiten.get(i).get(1).equals("")){
				lab = new JXLabel(SystemConfig.InetSeiten.get(i).get(0));
				lab.setIcon(new ImageIcon(RehaWissen.proghome+"icons/eye_16x16.gif"));
				lab.setIconTextGap(10);
			}else{
				lab = new JXLabel(SystemConfig.InetSeiten.get(i).get(0));
				lab.setIcon(new ImageIcon( RehaWissen.proghome + "icons/" + SystemConfig.InetSeiten.get(i).get(1)));
				lab.setIconTextGap(10);
			}
			model.addElement((JXLabel)lab);
			//listSeiten.add(lab);
		}
		listSeiten.setSelectedIndex(0);
		listSeiten.addListSelectionListener(this);
		JScrollPane jscr = new JScrollPane(listSeiten);
		jscr.setBorder(null);
		jscr.setOpaque(false);
		jscr.getViewport().setOpaque(false);
		jscr.setViewportBorder(BorderFactory.createEmptyBorder(10,15,10,15));
		jscr.setVisible(true);
		jscr.validate();
		return jscr;
	}
		
	class MyCellRenderer extends JXLabel implements ListCellRenderer {
		Color background;
        Color foreground;
        Font selectedfont = null;
        Font unselectedfont = null;
        public MyCellRenderer() {
        	super();
            setOpaque(true);
            selectedfont = new Font("Tahoma",Font.BOLD,11);
            unselectedfont = new Font("Tahoma",Font.PLAIN,11);            
        }

		public Component getListCellRendererComponent(
		JList list, Object value, int index, boolean selected,boolean focus){
			setText(((JXLabel)value).getText());
			setIcon(((JXLabel)value).getIcon());
			((JXLabel)value).setIconTextGap(15);
			
			if(selected){
				//Paint newPaint = new GradientPaint(0,0,new Color(112,141,223),0,getHeight(),Color.WHITE,true);
				//background = new Color(112,141,223);
				//System.out.println("Selected = "+((JXLabel)value).getText());
				
				this.background = Color.BLUE;
	            this.foreground = Color.RED;
	            ((JXLabel)value).setFont(this.selectedfont);
	            ((JXLabel)value).repaint();
				
			}else{
				//System.out.println("nicht Selected = "+((JXLabel)value).getText());
				//Paint newPaint = new GradientPaint(0,0,Color.WHITE,0,getHeight(),Color.WHITE,true);
				this.background = Color.WHITE;
	            this.foreground = Color.BLACK;
	            ((JXLabel)value).setFont(this.unselectedfont);
	            ((JXLabel)value).repaint();
	            
			}
			setBackground(this.background);
			setForeground(this.foreground);
			
		return this;
		}
	}
	private JXPanel getLinksUnten(){
		JXPanel jlupan = new JXPanel();
		jlupan.setBackground(new Color(255,204,137));
		FormLayout lay = new FormLayout("2dlu,100dlu:g,2dlu","15dlu,p,3dlu,p,3dlu,100dlu,25dlu,p,3dlu,50dlu:g,0dlu");
		jlupan.setLayout(lay);
		CellConstraints cc = new CellConstraints();
		jlupan.add(new JXLabel("Suche nach"),cc.xy(2,2));
		jtf = new JFormattedTextField();
		jtf.addKeyListener(this);
		jlupan.add(jtf,cc.xy(2,4));
		/*****************/
		themenDtblm = new MyDefaultTableModel();
		String[] column = 	{"gefunden in...","ID","Datei"};
		themenDtblm.setColumnIdentifiers(column);
		//themenDtblm.addTableModelListener(this);
		
		tblgefunden = new JXTable(themenDtblm);
		tblgefunden.setDoubleBuffered(true);
		tblgefunden.setHighlighters(HighlighterFactory.createSimpleStriping(new Color(204,255,255)));
		//tblgefunden.getColumn(0).setMinWidth(100);
		tblgefunden.getColumn(1).setMinWidth(0);
		tblgefunden.getColumn(1).setMaxWidth(0);
		tblgefunden.getColumn(2).setMinWidth(0);
		tblgefunden.getColumn(2).setMaxWidth(0);
		
		ListSelectionModel listSelectionModel = tblgefunden.getSelectionModel();
		listSelectionModel.addListSelectionListener(new SharedListSelectionHandler());
		tblgefunden.setSelectionModel(listSelectionModel);
		//tblgefunden.setPreferredSize(new Dimension(0,150));
		tblgefunden.setName("gefunden");
		tblgefunden.setSelectionMode(0);
		tblgefunden.addMouseListener(this);
		tblgefunden.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
            	if(tblgefunden.getSelectedRow()>=0){
                	String url = (String)tblgefunden.getValueAt(tblgefunden.getSelectedRow(),2);
                	rtaWissen.highlight = true;
                	rtaWissen.Navigiere(SystemConfig.HilfeServer+url); 
            	}
            }
        });
		
		JScrollPane gefunden = new JScrollPane(tblgefunden);
		gefunden.validate();
		jlupan.add(gefunden,cc.xy(2,6));
		/*****************/
		JButton aktbutton = new JButton("Hilfetexte aktualisieren");
		aktbutton.setActionCommand("aktualisieren");
		aktbutton.addActionListener(this);
		jlupan.add(aktbutton,cc.xy(2,8));
		
		DefaultMutableTreeNode treeitem = null;
		parameterScroll = new JScrollPane();
		parameterScroll.setOpaque(true);
		parameterScroll.setBorder(null);
		parameterScroll.setBackground(new Color(255,153,0));
		parameterScroll.setViewportBorder(BorderFactory.createEmptyBorder(10,5,10,5));


		root = new DefaultMutableTreeNode( "Thera-PI HowTo" );
		treeModel = new DefaultTreeModel(root);
		tree = new JXTree( root );
		
		tree.getSelectionModel().addTreeSelectionListener(this); 
		FuelleTree ft = new FuelleTree();
		ft.execute();
		
		
		
		parameterScroll.setViewportView(tree);
		parameterScroll.validate();
		jlupan.add(parameterScroll,cc.xy(2, 10));
		
		
		//jlupan.add(dummy2,cc.xy(2, 10));
		jlupan.validate();
		
		return jlupan;
	}
	public static void hilfsDateien(Vector vec){
		thisClass.dateien.add(vec);
	}
	
	public static BrowserFenster getInstance(){
		return thisClass;
	}
	public static String getSuchkriterium(){
		return thisClass.jtf.getText().trim();
	}
	public void closeBrowserSockServer(){
		BrowserFenster.thisClass.bws.setListen(false);
	}
	public static void browserMeldung(String meldung){
		//System.out.println(meldung);
	}
	
	private boolean WertZwischen(int punkt,int kleinerWert,int grosserWert){
		if (punkt < kleinerWert){
			return false;
		}
		if (punkt > grosserWert){
			return false;
		}
		return true;
	}


	public void FensterSchliessen(String welches){
		
		this.dispose();
		//NativeInterface.close();
	}
	

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.getActionCommand().equals("aktualisieren")){
			DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
			int childs = model.getChildCount(root);
			for(int i = childs-1; i > 0;i--){
				try{
					root.removeAllChildren();
				}catch(java.lang.ArrayIndexOutOfBoundsException obex){
					
				}
			}
			
			//model.removeNodeFromParent((DefaultMutableTreeNode)tree.getLastSelectedPathComponent());
			
			//tree.setModel(new DefaultTreeModel(root));
			
			//tree.remove(0);
			model.reload();
			tree.validate();
			tree.repaint();
			FuelleTree ft = new FuelleTree();
			ft.execute();
			model.reload();
			tree.validate();
			tree.repaint();
			
			/*
			tree.setModel(new DefaultTreeModel(root));
			
			
			*/
			/*
			*/
			/*
			DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
			tree.validate();
			tree.repaint();
			*/
		}
		//System.out.println(arg0);
	}

	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		Runtime r = Runtime.getRuntime();
	    r.gc();
	    long freeMem = r.freeMemory();
	    //System.out.println("Freier Speicher nach  gc():    " + freeMem); 
	}



	public void keyPressed(KeyEvent arg0) {
//		System.out.println("KeyPressed "+arg0.getKeyCode()+" - "+arg0.getSource());
		if(arg0.getSource() instanceof JFormattedTextField ){
			if(arg0.getKeyCode() == 10){
				arg0.consume();
				//System.out.println("In KeyPressed");
				try{
					MachSuche ms = new MachSuche();
					ms.execute();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
			if(arg0.getKeyCode() == 40){
				tblgefunden.setRowSelectionInterval(0, 0);
				tblgefunden.requestFocus();
			}
			
			if(arg0.getKeyCode() == 27){
				arg0.consume();
			}
		}

		if(arg0.getKeyCode() == 27){
			arg0.consume();
			FensterSchliessen(null);
		}
		if(arg0.getKeyCode() == 10){
					arg0.consume();
		}
	}


	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		//System.out.println("KeyReleased "+arg0);
		if(arg0.getKeyCode() == 10){
			arg0.consume();
		}
	}


	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		//System.out.println("KeyTyped "+arg0);
		if(arg0.getKeyCode() == 10){
			arg0.consume();
		}
	}
	
	@Override
	public void windowActivated(WindowEvent e) {
		//System.out.println("Fenster "+this.dieserName+" wurde aktiviert");
		//pinPanel.SetzeAktivButton(true);
		this.rtaWissen.requestFocus();
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowClosing(WindowEvent e) {
		//System.out.println("Fenster in Schliessen "+e);
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowDeactivated(WindowEvent e) {
		//pinPanel.SetzeAktivButton(false);
		// TODO Auto-generated method stub
	}
	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		// TODO Auto-generated method stub
		int ind = listSeiten.getSelectedIndex();
		linkneu = ind;
		if(linkneu != linkalt){
			//System.out.println("Indes des gewï¿½hlten Items = "+ind);
			//System.out.println(SystemConfig.InetSeiten.get(ind).get(2));
			rtaWissen.Navigiere(SystemConfig.InetSeiten.get(ind).get(2));
			linkalt = linkneu;
		}
		
	}
/***********************************/

	
	
	
	

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void valueChanged(TreeSelectionEvent arg0) {
		// TODO Auto-generated method stub
		//System.out.println(arg0);
		TreePath path = arg0.getNewLeadSelectionPath();
		try{
		int element = path.getPathCount();
	    if(element >= 3){
	    	String sitem = (String) path.getPathComponent(element-1).toString();
	    	String sgruppe = (String) path.getPathComponent(element-2).toString();
	    	int i;
	    	for(i = 0;i < dateien.size();i++){
	    		if( ((String)((Vector)dateien.get(i)).get(0)).equals(sitem)  && 
	    			((String)((Vector)dateien.get(i)).get(1)).equals(sgruppe) ) {
	    			String pfad = RehaWissen.proghome+"howto";
	    			File file = new File(pfad,sitem+".html");
	    			//File file = new File(pfad,sitem+".html");
	    			if (file.exists()) {
	    				rtaWissen.highlight = true;
	    				rtaWissen.Navigiere(SystemConfig.HilfeServer+((String)((Vector)dateien.get(i)).get(2)));
		    			//rtaWissen.Navigiere("file:///"+Reha.proghome+"howto/"+sitem+".html");
	    			}else{
	    				rtaWissen.highlight = true;	    				
	    				rtaWissen.Navigiere(SystemConfig.HilfeServer+((String)((Vector)dateien.get(i)).get(2)));
	    				//rtaWissen.Navigiere(SystemConfig.HilfeServer+sitem+".html");
	    			}
	    			break;
	    		}
	    	}
	    }
	    /*
	    if(thema.equals("Grundlagen der Bedienung")){
	    	rtaWissen.Navigiere("file:///"+Reha.proghome+"howto/tk-bedienung.html");	    	
	    }
	*/
		}catch(java.lang.NullPointerException npex){
			
		}
	    

		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	
}
final class FuelleTree extends SwingWorker<Void,Void>{
	Vector<String> gruppen = new Vector<String>();
	@Override
	protected Void doInBackground() throws Exception {
		// TODO Auto-generated method stub
		gruppen = holeGruppen();
		holeTitel();
		return null;
	}
/*****************************************************************/	
	private Vector holeGruppen(){
		Vector<String> vec = new Vector<String>();
		Statement stmtx = null;
		ResultSet rsx = null;
		
		stmtx = null;
		rsx = null;
		try {
			stmtx = (Statement) RehaWissen.thisClass.hilfeConn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			        ResultSet.CONCUR_UPDATABLE );
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			rsx = stmtx.executeQuery("select gruppe from hgroup order by reihenfolge");
			
			while(rsx.next()){
				vec.add(rsx.getString(1));
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
			if (rsx != null) {
				try {
					rsx.close();
				} catch (SQLException sqlEx) { // ignore }
					rsx = null;
				}
			}	
			if (stmtx != null) {
				try {
					stmtx.close();
				} catch (SQLException sqlEx) { // ignore }
					stmtx = null;
				}
			}
	
			return (Vector)vec.clone();
	}
/**********************************************************************/
	private void holeTitel(){
		int i;
		Statement stmtx = null;
		ResultSet rsx = null;
		
		stmtx = null;
		rsx = null;
			
		try {
			stmtx = (Statement) RehaWissen.thisClass.hilfeConn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			        ResultSet.CONCUR_UPDATABLE );
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for(i = 0; i < gruppen.size();i++){
			/*******/				
			try {
				rsx = stmtx.executeQuery("select titel,gruppe,datei from htitel where gruppe='"+gruppen.get(i).trim()+"'");		
				try {
					stmtx = (Statement) RehaWissen.thisClass.hilfeConn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					        ResultSet.CONCUR_UPDATABLE );
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/*
				DefaultMutableTreeNode node = new DefaultMutableTreeNode( "Terminkalender");
				treeitem = new DefaultMutableTreeNode("Grundlagen der Bedienung");
				node.add(treeitem ); 
				treeitem = new DefaultMutableTreeNode("Kalenderbenutzer anlegen");
				node.add(treeitem );
				treeitem = new DefaultMutableTreeNode("Was sind Behandlersets");
				node.add(treeitem );
				treeitem = new DefaultMutableTreeNode("Behandlersets anlegen");
				node.add(treeitem );
				root.add(node);

				node = new DefaultMutableTreeNode( "Ru:gl"); 
				treeitem = new DefaultMutableTreeNode("Grundlagen der Bedienung");
				node.add(treeitem );
				root.add(node);
				*/
				DefaultMutableTreeNode treeitem = null;
				DefaultMutableTreeNode node = new DefaultMutableTreeNode( gruppen.get(i));
				boolean hatitems = false;
				Vector<String> items = new Vector<String>();
				//System.out.println("Gruppe: " + gruppen.get(i).trim() );
				while(rsx.next()){
					hatitems = true;
					items.clear();
					treeitem = new DefaultMutableTreeNode(rsx.getString(1));
					node.add(treeitem);
					
					/*************/

					items.add(rsx.getString(1));
					items.add(rsx.getString(2));
					items.add(rsx.getString(3));
					BrowserFenster.hilfsDateien((Vector) items.clone());
					//System.out.println(items);
				}	
				BrowserFenster.thisClass.root.add(node);
				BrowserFenster.thisClass.tree.validate();
			}catch(SQLException e){
				e.printStackTrace();
			}
			
			/*******/			
		}
		
		if (rsx != null) {
			try {
				rsx.close();
			} catch (SQLException sqlEx) { // ignore }
				rsx = null;
			}
		}	
		if (stmtx != null) {
			try {
				stmtx.close();
			} catch (SQLException sqlEx) { // ignore }
				stmtx = null;
			}
		}
		BrowserFenster.thisClass.tree.expandRow(0);
		BrowserFenster.thisClass.tree.validate();
		//BrowserFenster.thisClass.tree.setExpandsSelectedPaths(true);

	}
	
	
}
/**************************************/
@SuppressWarnings("unchecked")	
class MyDefaultTableModel extends DefaultTableModel{
	   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Class getColumnClass(int columnIndex) {
		   if(columnIndex==0){return String.class;}
		  /* if(columnIndex==1){return JLabel.class;}*/
		   else{return String.class;}
           //return (columnIndex == 0) ? Boolean.class : String.class;
       }

	    public boolean isCellEditable(int row, int col) {
	        //Note that the data/cell address is constant,
	        //no matter where the cell appears onscreen.
	    	/*
	    	if (col == 0){
	        	return true;
	        }else if(col == 6){
	        	return true;
	        }else if(col == 7){
	        	return true;
	        }else if(col == 11){
	        	return true;
	        } else{
	          return false;
	        }
	        */
	    	return false;
	      }
	   
}
class SharedListSelectionHandler implements ListSelectionListener {
    public void valueChanged(ListSelectionEvent e) { 
        ListSelectionModel lsm = (ListSelectionModel)e.getSource();

        int firstIndex = e.getFirstIndex();
        int lastIndex = e.getLastIndex();
        boolean isAdjusting = e.getValueIsAdjusting(); 
        /*
        output.append("Event for indexes "
                      + firstIndex + " - " + lastIndex
                      + "; isAdjusting is " + isAdjusting
                      + "; selected indexes:");
        */              
        
        if (lsm.isSelectionEmpty()) {
           // output.append(" <none>");
        } else {
        	
            // Find out which indexes are selected.
            int minIndex = lsm.getMinSelectionIndex();
            
            int maxIndex = lsm.getMaxSelectionIndex();
            for (int i = minIndex; i <= maxIndex; i++) {
                if (lsm.isSelectedIndex(i)) {
                	String url = (String)BrowserFenster.thisClass.themenDtblm.getValueAt(i,2);
                	BrowserFenster.thisClass.rtaWissen.highlight = false;
                	BrowserFenster.thisClass.rtaWissen.Navigiere(SystemConfig.HilfeServer+url);
                	//helpFenster.thisClass.stitel.setText(
                			//(String)helpFenster.thisClass.tblThemen.getValueAt(i,0)) ;
                    //output.append(" " + i);
                }
            }
        }
        //output.append(newline);
        //output.setCaretPosition(output.getDocument().getLength());
    }

}

/**************************************/
final class MachSuche extends SwingWorker<Void,Void>{
		private String xstmt1 = "";
		private String xstmt2 = "";
		String[] fundstelle;
		@Override
		protected Void doInBackground(){
			// TODO Auto-generated method stub
			//System.out.println("Statement = "+xstmt1);
			
			if(BrowserFenster.thisClass.tblgefunden.getRowCount() > 0){
				BrowserFenster.thisClass.themenDtblm.setRowCount(0);
				BrowserFenster.thisClass.tblgefunden.validate();
			}
			
			String test = new String(BrowserFenster.thisClass.jtf.getText().trim());
			//String test = "";
			//System.out.println("ï¿½bergebener String = "+test);
			//System.out.println("Statement = "+xstmt1);
	 
			for(int i = 0; i < 1; i++){
				if(test.equals("")){
				 xstmt1 = "select titel,id,datei from htitel";
				 xstmt2 = "select titel,id,datei from htitel";
				 break;
				}
				fundstelle = test.split(" ");			
				if(fundstelle.length == 0){
					xstmt1 = "select titel,id,datei from htitel where inhalt LIKE '%"+ersetzeUmlaute2(test)+"%'";
					xstmt2 = "select titel,id,datei from htitel where inhalt LIKE '%"+ersetzeUmlaute2(test)+"%'";
					//System.out.println("Statement = "+xstmt1);
					break;
				}
				if(fundstelle.length > 0){
					xstmt1= "select titel,id,datei from htitel where inhalt LIKE '%"+ersetzeUmlaute2(fundstelle[0])+"%'";
					xstmt2= "select titel,id,datei from htitel where inhalt LIKE '%"+ersetzeUmlaute2(fundstelle[0])+"%'";					
					for(i = 1;i<fundstelle.length;i++){
						String zusatz1 = " AND inhalt LIKE '%"+ersetzeUmlaute2(fundstelle[i])+"%'";
						xstmt1 = xstmt1 + new String(zusatz1);
						String zusatz2 = " AND inhalt LIKE '%"+ersetzeUmlaute2(fundstelle[i])+"%'";
						xstmt2 = xstmt2 + new String(zusatz2);

					}
					//System.out.println("Statement = "+xstmt1);
					//System.out.println("Statement = "+xstmt2);					
					break;
				}

			}

			tabelleFuellen();
			return null;
		}
		private void tabelleFuellen(){
			if(xstmt1.equals("")){
				return;
			}
			//System.out.println(xstmt1);
			Statement stmtx = null;
			ResultSet rsx = null;
			Vector comboInhalt = null;
			Vector gesamtVec = new Vector();
					stmtx = null;
					rsx = null;
					//System.out.println("In holeTitel");
					try {
						stmtx = (Statement) RehaWissen.thisClass.hilfeConn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
						        ResultSet.CONCUR_UPDATABLE );
						
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						/*
						rsx = stmtx.executeQuery("select inhalt from htitel where id='37'");
						while(rsx.next()){
							System.out.println(rsx.getString("inhalt"));
						}
						*/
						//System.out.println("Statement = "+stmtx);
						rsx = stmtx.executeQuery(xstmt1);

						while(rsx.next()){
							gesamtVec.clear();
							gesamtVec.add(rsx.getString("titel"));
							gesamtVec.add(rsx.getInt("id"));
							gesamtVec.add(rsx.getString("datei"));
							//System.out.println(rsx.getString("inhalt"));
							BrowserFenster.thisClass.themenDtblm.addRow((Vector) gesamtVec.clone());
						}
						if(BrowserFenster.thisClass.tblgefunden.getRowCount() > 0){
							BrowserFenster.thisClass.tblgefunden.setRowSelectionInterval(0, 0);
		                	String url = (String)BrowserFenster.thisClass.tblgefunden.getValueAt(0,2);
		                	BrowserFenster.thisClass.rtaWissen.Navigiere(SystemConfig.HilfeServer+url);
		                	BrowserFenster.thisClass.rtaWissen.Markiere(fundstelle);

						}/*else{
							rsx = stmtx.executeQuery(xstmt2);

							while(rsx.next()){
								gesamtVec.clear();
								gesamtVec.add(rsx.getString("titel"));
								gesamtVec.add(rsx.getInt("id"));
								gesamtVec.add(rsx.getString("datei"));
								//System.out.println(rsx.getString("inhalt"));
								BrowserFenster.thisClass.themenDtblm.addRow((Vector) gesamtVec.clone());
							}
							if(BrowserFenster.thisClass.tblgefunden.getRowCount() > 0){
								BrowserFenster.thisClass.tblgefunden.setRowSelectionInterval(0, 0);
			                	String url = (String)BrowserFenster.thisClass.tblgefunden.getValueAt(0,2);
			                	BrowserFenster.thisClass.rtaWissen.Navigiere(SystemConfig.HilfeServer+url);
			                	//BrowserFenster.thisClass.rtaWissen.Markiere(fundstelle);

							}
						}*/
						
					}catch(SQLException e){
						e.printStackTrace();
					}
					if (rsx != null) {
						try {
							rsx.close();
						} catch (SQLException sqlEx) { // ignore }
							rsx = null;
						}
					}	
					if (stmtx != null) {
						try {
							stmtx.close();
						} catch (SQLException sqlEx) { // ignore }
							stmtx = null;
						}
					}
				return;
				}
		private String ersetzeUmlaute1(String str){
			String ersetzen = new String(str);
			String neu = "", alt = neu = "";
			neu = new String(ersetzen.replaceAll("ï¿½","Ã¤")); 
			alt = new String(neu);
			neu = new String(alt.replaceAll("ï¿½","Ã¶" ));
			alt = new String(neu);
			neu = new String(alt.replaceAll("ï¿½","Ã¼" ));
			alt = new String(neu);
			neu = new String(alt.replaceAll("ï¿½","Ã" ));
			alt = new String(neu);
			neu = new String(alt.replaceAll("ï¿½","Ã" ));
			alt = new String(neu);
			neu = new String(alt.replaceAll("ï¿½","Ã" ));
			alt = new String(neu);
			neu = new String(alt.replaceAll("ï¿½","Ã" ));

			/*
			Ã¤ statt ï¿½
			Ã¶ statt ï¿½
			Ã¼ statt ï¿½
			Ã statt ï¿½
			Ã statt ï¿½
			Ã statt ï¿½
			Ã statt ï¿½
			*/
			//ersetzen
			return new String(neu);
			
		}
		private String ersetzeUmlaute2(String str){
			String ersetzen = new String(str);
			String neu = "", alt = neu = "";
			neu = new String(ersetzen.replaceAll("ï¿½","&auml;")); 
			alt = new String(neu);
			neu = new String(alt.replaceAll("ï¿½","&ouml;" ));
			alt = new String(neu);
			neu = new String(alt.replaceAll("ï¿½","&uuml;" ));
			alt = new String(neu);
			neu = new String(alt.replaceAll("ï¿½","&Auml;" ));
			alt = new String(neu);
			neu = new String(alt.replaceAll("ï¿½","&Ouml;" ));
			alt = new String(neu);
			neu = new String(alt.replaceAll("ï¿½","&Uuml;" ));
			alt = new String(neu);
			neu = new String(alt.replaceAll("ï¿½","&szlig;" ));

			/*
			Ã¤ statt ï¿½
			Ã¶ statt ï¿½
			Ã¼ statt ï¿½
			Ã statt ï¿½
			Ã statt ï¿½
			Ã statt ï¿½
			Ã statt ï¿½
			*/
			//ersetzen
			return new String(neu);
			
		}

}
	/****************************************/	

class BrowserSockServer{
	static ServerSocket serv = null;
	boolean xlisten = true;
	BrowserSockServer(boolean lis) throws IOException{
		xlisten = lis;
		try {
			serv = new ServerSocket(9999);
			//System.out.println("BrowserSockeServer gestartet auf Port 9999");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//RehaxSwing.jDiag.dispose();
			return;
		}
		
		Socket client = null;
		//RehaxSwing.standDerDingelbl.setText("ï¿½ffne Socket");
		while(xlisten){
			try {
				//RehaxSwing.socketoffen = true;				
				client = serv.accept();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
			StringBuffer sb = new StringBuffer();
			InputStream input = client.getInputStream();
			OutputStream output = client.getOutputStream();
			int byteStream;
			String test = "";
			while( (byteStream =  input.read()) > -1){
				//System.out.println("******byteStream Erhalten******  "+byteStream );
				char b = (char)byteStream;
				
				sb.append(b);
			}

			test = new String(sb);
			//System.out.println("Socket= "+test);			
			final String xtest = new String(test);
			BrowserFenster.browserMeldung(new String(xtest));
			byte[] schreib = "ok".getBytes();
			output.write(schreib);
			output.flush();
			output.close();
			input.close();

			//JOptionPane.showMessageDialog(null, test);
		}
		if(serv != null){
			serv.close();
			serv = null;
			//System.out.println("Socket wurde geschlossen");
			//RehaxSwing.socketoffen = false;
			//RehaxSwing.jDiag.dispose();
		}else{
			//System.out.println("Socket wurde geschlossen");
			//RehaxSwing.socketoffen = false;
			//RehaxSwing.jDiag.dispose();
		}
		/*
		SetzeLabel slb = new SetzeLabel();
		slb.init(new String(new String("Abbruch der Socketfunktion")));
		slb.execute();
		*/
		//System.out.println("BrowserSockeServer beendet auf Port 9999");
		return;
	}
	public void setListen(boolean lis){
		xlisten = lis;
	}

}
