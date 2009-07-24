package patientenFenster;


import geraeteInit.ScannerUtil;
import hauptFenster.Reha;


import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import jxTableTools.DateTableCellEditor;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.decorator.SortOrder;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.IconValues;
import org.jdesktop.swingx.renderer.MappedValue;
import org.jdesktop.swingx.renderer.StringValues;

import patientenFenster.Historie.HistorPanel;
import patientenFenster.Historie.HistorRezepteListSelectionHandler;
import sqlTools.SqlInfo;
import sun.awt.image.ImageFormatException;
import systemEinstellungen.SystemConfig;
import systemTools.Colors;
import systemTools.GrafikTools;
import systemTools.JCompTools;
import uk.co.mmscomputing.device.scanner.Scanner;
import uk.co.mmscomputing.device.scanner.ScannerDevice;
import uk.co.mmscomputing.device.scanner.ScannerIOException;
import uk.co.mmscomputing.device.scanner.ScannerIOMetadata;
import uk.co.mmscomputing.device.scanner.ScannerListener;
import uk.co.mmscomputing.device.scanner.ScannerIOMetadata.Type;
import uk.co.mmscomputing.device.twain.TwainConstants;
import uk.co.mmscomputing.device.twain.TwainIOMetadata;
import uk.co.mmscomputing.device.twain.TwainImageInfo;
import uk.co.mmscomputing.device.twain.TwainImageLayout;
import uk.co.mmscomputing.device.twain.TwainSource;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;
//import com.lowagie.text.Image;

public class Dokumentation extends JXPanel implements ActionListener, TableModelListener, PropertyChangeListener, ScannerListener{
	public static Dokumentation dokumentation = null;
	JXPanel leerPanel = null;
	//JXPanel vollPanel = null;
	JXPanel vollPanel = null;
	JXPanel wechselPanel = null;
	public JLabel anzahlTermine= null;
	public JLabel anzahlRezepte= null;
	public String aktPanel = "";
	public JXTable tabhistorie = null;
	public JXTable tabaktterm = null;
	public MyDoku2TableModel dtblm;
	public MyDokuTermTableModel dtermm;
	public TableCellEditor tbl = null;
	public boolean rezneugefunden = false;
	public boolean neuDlgOffen = false;
	public String[] indphysio = null;
	public String[] indergo = null;
	public String[] indlogo = null;
	public JXPanel jpan1 = null;
	public JButton[] dokubut = {null,null,null,null,null};
	public JButton[] pmbut = {null,null,null,null,null};
	public static boolean inDokuDaten = false;
	public JComboBox seitengroesse = null;
	public JComboBox aufloesung = null;
	public JComboBox farbe = null;
	public Vector<String> LabName = new Vector<String>();
	int bildnummer=0;
	public Vector<String>vecBilderPfad = new Vector<String>();
	public Vector<String>vecPdfPfad = new Vector<String>();
	public Vector<JLabel>Labels = new Vector<JLabel>();
	public JXPanel bilderPan = null;
	public JScrollPane bildscroll = null;
	public JLabel[] infolab = {null,null,null,null,null};
	public JLabel[] infolabLeer = {null,null,null,null,null};
	public MouseListener mlist = null;
	public boolean deviceinstalled = false;
	public boolean scanaktiv = false;
	public int aktivesBild = 0;
	public JXPanel plusminus;
	public JPanel leerInfo = null;
	public String commonName = "";
	Scanner scanner;
	public Dokumentation(){
		super();
		dokumentation = this;
		scanaktiv = (SystemConfig.hmDokuScanner.get("aktivieren").trim().equals("1") ? true : false );
		setOpaque(false);
		setLayout(new BorderLayout());
		/********zuerst das Leere Panel basteln**************/
		leerPanel = new KeinRezept("noch keine Dokumentation angelegt für diesen Patient");
		leerPanel.setName("leerpanel");
		leerPanel.setOpaque(false);
		leerInfo = getInfoPanelLeer();
		leerPanel.add(leerInfo,BorderLayout.SOUTH);
		
		/********dann das volle**************/		
		JXPanel allesrein = new JXPanel(new BorderLayout());
		allesrein.setOpaque(false);
		allesrein.setBorder(null);
		
		FormLayout lay = new FormLayout("5dlu,fill:0:grow(1.00),0dlu",
		"0dlu,p,2dlu,p,2dlu,fill:0:grow(1.00),5dlu");
		CellConstraints cc = new CellConstraints();
		allesrein.setLayout(lay);
		
		wechselPanel = new JXPanel(new BorderLayout());
		wechselPanel.setOpaque(false);
		wechselPanel.setBorder(null);
		wechselPanel.add(leerPanel,BorderLayout.CENTER);
		aktPanel = "leerPanel";
		
		allesrein.add(getToolbar(),cc.xy(2, 2));

		allesrein.add(wechselPanel,cc.xy(2, 6));

		add(JCompTools.getTransparentScrollPane(allesrein),BorderLayout.CENTER);
		validate();
		
		new SwingWorker<Void,Void>(){

			@Override
			protected Void doInBackground() throws Exception {
				vollPanel = new JXPanel();
				
				FormLayout vplay = new FormLayout("fill:0:grow(0.60),5dlu,fill:0:grow(0.40),5dlu",
						"p,2dlu,125dlu,5dlu,fill:0:grow(1.00),0dlu");
				CellConstraints vpcc = new CellConstraints();
				vollPanel.setLayout(vplay);
				vollPanel.setOpaque(false);
				vollPanel.setBorder(null);
				
				Font font = new Font("Tahome",Font.PLAIN,11);
				anzahlRezepte = new JLabel("Anzahl gespeicherter Dokumentationen: 0");
				anzahlRezepte.setFont(font);
				vollPanel.add(anzahlRezepte,vpcc.xy(1,1));
				
				vollPanel.add(getTabelle(),vpcc.xywh(1,3,3,1));

				
				jpan1 = new DokuPanel();
				jpan1.setLayout(new BorderLayout());
				jpan1.setOpaque(false);
				jpan1.add(getToolBereich(),BorderLayout.CENTER);
				vollPanel.add(jpan1,vpcc.xyw(1,5,3));
				jpan1.validate();
				
				new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() throws Exception {
						if(scanaktiv){
							scanStarten();
						}
						setzeListener();
						return null;
					}
				}.execute();
				
				
				return null;
			}
		}.execute();



		
		


	}
	public JXPanel getToolBereich(){
		JXPanel tbereich = new JXPanel();
		tbereich.setOpaque(false);
		FormLayout lay = new FormLayout("0dlu,fill:0:grow(1.00),0dlu","0dlu,fill:40:grow(1.00),59dlu");
		CellConstraints cc = new CellConstraints();
		tbereich.setLayout(lay);

		tbereich.add(getBildPanel(),cc.xy(2,2));
		
		tbereich.add(getInfoPanel(),cc.xy(2,3));
		tbereich.validate();
		return tbereich;
	}
	public JScrollPane getBildPanel(){
		bilderPan = new JXPanel(new FlowLayout(FlowLayout.LEFT));
		//dummy1.setBackground(Color.RED);
		bilderPan.setOpaque(false);
		JScrollPane bildscroll = JCompTools.getTransparentScrollPane(bilderPan);
		bildscroll.validate();
		return bildscroll;
	}
	public JPanel getInfoPanel(){      // 1   2  3        4                5  6   7         8            9    10
		FormLayout lay = new FormLayout("2dlu,p,20dlu,right:max(50dlu;p),2dlu,p,20dlu,right:max(50dlu;p),2dlu,p",
       // 1    2  3    4   5  6   7  8   9
		"10dlu,p,5dlu,p,1dlu,p,1dlu,p,10dlu");
		CellConstraints cc = new CellConstraints();
		PanelBuilder pb = new PanelBuilder(lay);
		//JPanel dummy2 = new JXPanel();
		//dummy2.setBackground(Color.RED);
		Font fon = new Font("Tahoma",Font.BOLD,10);
		JLabel jlab = new JLabel("Geräte-Info");
		jlab.setFont(new Font("Tahoma",Font.BOLD,14));
		jlab.setForeground(Color.BLUE);
		pb.add(jlab,cc.xy(2,2));
		pb.addLabel("aktives Gerät:",cc.xy(4, 4));
		if(! scanaktiv){
			infolab[0] = new JLabel("Scanner nicht aktiviert");
			infolab[0].setFont(fon);
			pb.add(infolab[0],cc.xy(6,4));
			return pb.getPanel();
		}
		infolab[0] = new JLabel(SystemConfig.sDokuScanner);
		infolab[0].setFont(fon);
		pb.add(infolab[0],cc.xy(6,4));
		pb.addLabel("Scanmodus:",cc.xy(4, 6));
		infolab[1] = new JLabel(SystemConfig.hmDokuScanner.get("farben"));
		infolab[1].setFont(fon);		
		pb.add(infolab[1],cc.xy(6,6));
		pb.addLabel("Auflösung:",cc.xy(8, 4));
		infolab[2] = new JLabel(SystemConfig.hmDokuScanner.get("aufloesung")+"dpi");
		infolab[2].setFont(fon);		
		pb.add(infolab[2],cc.xy(10,4));
		pb.addLabel("Seitenformat:",cc.xy(8, 6));
		infolab[3] = new JLabel(SystemConfig.hmDokuScanner.get("seiten"));
		infolab[3].setFont(fon);		
		pb.add(infolab[3],cc.xy(10,6));
		pb.addLabel("Scannerdialog verwenden:",cc.xy(8, 8));
		infolab[4] = new JLabel( (SystemConfig.hmDokuScanner.get("dialog").equals("1") ? "ja" : "nein"));
		infolab[4].setFont(fon);		
		pb.add(infolab[4],cc.xy(10,8));
		pb.getPanel().setOpaque(false);
		pb.getPanel().setPreferredSize(new Dimension(500,100));
		return pb.getPanel();
	}
	public JPanel getInfoPanelLeer(){      // 1   2  3        4                5  6   7         8            9    10
		FormLayout lay = new FormLayout("2dlu,p,20dlu,right:max(50dlu;p),2dlu,p,20dlu,right:max(50dlu;p),2dlu,p",
       // 1    2  3    4   5  6   7  8   9
		"10dlu,p,5dlu,p,1dlu,p,1dlu,p,10dlu");
		CellConstraints cc = new CellConstraints();
		PanelBuilder pb = new PanelBuilder(lay);
		Font fon = new Font("Tahoma",Font.BOLD,10);
		JLabel jlab = new JLabel("Geräte-Info");
		jlab.setFont(new Font("Tahoma",Font.BOLD,14));
		jlab.setForeground(Color.BLUE);
		pb.add(jlab,cc.xy(2,2));
		pb.addLabel("aktives Gerät:",cc.xy(4, 4));
		if(! scanaktiv){
			infolabLeer[0] = new JLabel("Scanner nicht aktiviert");
			infolabLeer[0].setFont(fon);
			pb.add(infolabLeer[0],cc.xy(6,4));
			return pb.getPanel();
		}
		infolabLeer[0] = new JLabel(SystemConfig.sDokuScanner);
		infolabLeer[0].setFont(fon);
		pb.add(infolabLeer[0],cc.xy(6,4));
		pb.addLabel("Scanmodus:",cc.xy(4, 6));
		infolabLeer[1] = new JLabel(SystemConfig.hmDokuScanner.get("farben"));
		infolabLeer[1].setFont(fon);		
		pb.add(infolabLeer[1],cc.xy(6,6));
		pb.addLabel("Auflösung:",cc.xy(8, 4));
		infolabLeer[2] = new JLabel(SystemConfig.hmDokuScanner.get("aufloesung")+"dpi");
		infolabLeer[2].setFont(fon);		
		pb.add(infolabLeer[2],cc.xy(10,4));
		pb.addLabel("Seitenformat:",cc.xy(8, 6));
		infolabLeer[3] = new JLabel(SystemConfig.hmDokuScanner.get("seiten"));
		infolabLeer[3].setFont(fon);		
		pb.add(infolabLeer[3],cc.xy(10,6));
		pb.addLabel("Scannerdialog verwenden:",cc.xy(8, 8));
		infolabLeer[4] = new JLabel( (SystemConfig.hmDokuScanner.get("dialog").equals("1") ? "ja" : "nein"));
		infolabLeer[4].setFont(fon);		
		pb.add(infolabLeer[4],cc.xy(10,8));
		pb.getPanel().setOpaque(false);
		pb.getPanel().setPreferredSize(new Dimension(500,100));
		return pb.getPanel();
	}

	class DokuPanel extends JXPanel{
		ImageIcon hgicon;
		int icx,icy;
		AlphaComposite xac1 = null;
		AlphaComposite xac2 = null;		
		DokuPanel(){
			super();
			setOpaque(false);
			hgicon = new ImageIcon(Reha.proghome+"icons/xsane.png"); 
			icx = hgicon.getIconWidth()/2;
			icy = hgicon.getIconHeight()/2;
			xac1 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.25f); 
			xac2 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f);			
			
		}
		public void paintComponent( Graphics g ) { 
			super.paintComponent( g );
			Graphics2D g2d = (Graphics2D)g;
			
			if(hgicon != null){
				g2d.setComposite(this.xac1);
				g2d.drawImage(hgicon.getImage(), (getWidth()/3)-(icx+20) , (getHeight()/2)-(icy-40),null);
				g2d.setComposite(this.xac2);
			}
		}
	}	
	

	public JXPanel getTabelle(){
		JXPanel dummypan = new JXPanel(new BorderLayout());
		dummypan.setOpaque(false);
		dummypan.setBorder(null);
		dtblm = new MyDoku2TableModel();
		String[] column = 	{"Doku-Id","Doku-Art","Titel","erfaßt am","von","",""};
		dtblm.setColumnIdentifiers(column);
		tabhistorie = new JXTable(dtblm);
		tabhistorie.setHighlighters(HighlighterFactory.createSimpleStriping(Colors.PiOrange.alpha(0.25f)));
		tabhistorie.setDoubleBuffered(true);
		tabhistorie.setEditable(false);
		tabhistorie.setSortable(false);
		tabhistorie.getColumn(0).setMaxWidth(50);
		TableCellRenderer renderer = new DefaultTableRenderer(new MappedValue(StringValues.EMPTY, IconValues.ICON), JLabel.CENTER);
		tabhistorie.getColumn(1).setCellRenderer(renderer);
		tabhistorie.getColumn(1).setMaxWidth(50);
		tabhistorie.getColumn(2).setMinWidth(275);
		tabhistorie.getColumn(3).setMaxWidth(50);
		//tabhistorie.getColumn(4).setMinWidth(0);
		//tabhistorie.getColumn(4).setMaxWidth(0);		
		tabhistorie.getColumn(5).setMinWidth(0);
		tabhistorie.getColumn(5).setMaxWidth(0);		
		tabhistorie.getColumn(6).setMinWidth(0);
		tabhistorie.getColumn(6).setMaxWidth(0);		
		tabhistorie.validate();
		tabhistorie.setName("AktRez");
		tabhistorie.setSelectionMode(0);
		//tabaktrez.addPropertyChangeListener(this);
		tabhistorie.getSelectionModel().addListSelectionListener( new DokuListSelectionHandler());
		tabhistorie.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				if(arg0.getClickCount()==2){
					while(inDokuDaten){
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					//neuanlageRezept(false,"");
				}
			}
		});
		tabhistorie.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyCode()==10){
					arg0.consume();
					//neuanlageRezept(false,"");
				}
				if(arg0.getKeyCode()==27){
					arg0.consume();
				}
			}

		});
		//tabaktrez.getSelectionModel().addListSelectionListener(this);
		//dtblm.addTableModelListener(this);
		//dummypan.setPreferredSize(new Dimension(0,100));
		JScrollPane aktrezscr = JCompTools.getTransparentScrollPane((Component)tabhistorie);
		aktrezscr.validate(); 
		aktrezscr.getVerticalScrollBar().setUnitIncrement(15);
		dummypan.add(aktrezscr,BorderLayout.CENTER);
		dummypan.validate();
		return dummypan;
	}
	
	public void setzeRezeptPanelAufNull(boolean aufnull){
		if(aufnull){
			if(aktPanel.equals("vollPanel")){
				wechselPanel.remove(vollPanel);
				wechselPanel.add(leerPanel);
				aktPanel = "leerPanel";
				for(int i = 0; i < 4;i++){
					dokubut[i].setEnabled(false);
				}
			}
		}else{
			if(aktPanel.equals("leerPanel")){
				wechselPanel.remove(leerPanel);
				wechselPanel.add(vollPanel);
				aktPanel = "vollPanel";
				for(int i = 0; i < 4;i++){
					//dokubut[i].setEnabled(true);
				}
			}
		}
	}
	
	
	public JToolBar getToolbar(){
		JToolBar jtb = new JToolBar();
		jtb.setRollover(true);
		jtb.setBorder(null);
		jtb.setOpaque(false);

		dokubut[0] = new JButton();
		dokubut[0].setIcon(SystemConfig.hmSysIcons.get("scanner"));
		dokubut[0].setToolTipText("Papierbericht einscannen");
		dokubut[0].setActionCommand("scannen");
		dokubut[0].addActionListener(this);
		jtb.add(dokubut[0]);
		/*
		dokubut[1] = new JButton();
		dokubut[1].setIcon(SystemConfig.hmSysIcons.get("save"));
		dokubut[1].setToolTipText("Speichern der eingescannten Dokumentation");
		dokubut[1].setActionCommand("dokusave");
		dokubut[1].addActionListener(this);
		jtb.add(dokubut[1]);
		*/
		

		//jtb.addSeparator(new Dimension(20,0));
		/*
		jtb.add(dokubut[0]);
		farbe = new JComboBox(new String[]{"Schwarz/Weiß","Graustufen","Farbe"});
		farbe.setSelectedIndex(1);
		jtb.add(farbe);
		aufloesung = new JComboBox(new String[]{"75dpi","100dpi","150dpi","200dpi","400dpi"});
		aufloesung.setSelectedIndex(2);
		jtb.add(aufloesung);
		seitengroesse = new JComboBox(new String[]{"Din A6","Din A5","Din A4"});
		seitengroesse.setSelectedIndex(1);
		jtb.add(seitengroesse);
		jtb.add(aufloesung);
		*/
		
		
		dokubut[1] = new JButton();
		dokubut[1].setIcon(SystemConfig.hmSysIcons.get("tools"));
		dokubut[1].setToolTipText("Scannereinstellungen ändern");
		dokubut[1].setActionCommand("scanedit");
		dokubut[1].addActionListener(this);		
		jtb.add(dokubut[1]);

		dokubut[2] = new JButton();
		dokubut[2].setIcon(SystemConfig.hmSysIcons.get("abbruch"));
		dokubut[2].setToolTipText("Kompletten Vorgang abbrechen");
		dokubut[2].setActionCommand("Dokuabbruch");
		dokubut[2].addActionListener(this);
		jtb.add(dokubut[2]);
		
		
		if(!scanaktiv){
			dokubut[0].setEnabled(false);
			dokubut[1].setEnabled(false);
			dokubut[2].setEnabled(false);
		}
		/*
		dokubut[2] = new JButton();
		dokubut[2].setIcon(SystemConfig.hmSysIcons.get("historieumsatz"));
		dokubut[2].setToolTipText("Gesamtumsatz des Patienten (aller in der Historie befindlichen Rezepte)");
		dokubut[2].setActionCommand("historumsatz");
		dokubut[2].addActionListener(this);		
		jtb.add(dokubut[2]);
		
		dokubut[3] = new JButton();
		dokubut[3].setIcon(SystemConfig.hmSysIcons.get("historietage"));
		dokubut[3].setToolTipText("Behandlungstage des Historien-Rezeptes drucken");
		dokubut[3].setActionCommand("historprinttage");
		dokubut[3].addActionListener(this);		
		jtb.add(dokubut[3]);
		*/

		for(int i = 0; i < 4;i++){
			//dokubut[i].setEnabled(false);
		}
		
		
		return jtb;
	}
	
	
	

	public void macheTabelle(Vector vec){
		if(vec.size()> 0){
			dtblm.addRow(vec);	
		}else{
			dtblm.setRowCount(0);
			tabhistorie.validate();
		}
		
	}
	/******************
	 * 
	 * 
	 */
	/******************
	 * 
	 * 
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		String cmd = arg0.getActionCommand();
		if(cmd.equals("arztbericht")){
			
		}else if(cmd.equals("scannen")){
			if(SystemConfig.sDokuScanner.equals("Scanner nicht aktiviert!")){
				return;
			}
			if(aktPanel.equals("leerPanel")){
				this.setzeRezeptPanelAufNull(false);
			}
			try {
				if(scanner==null){
					System.out.println("Neustart des Scannersystems erforderlich");
					scanStarten();					
				}
				setCursor(new Cursor(Cursor.WAIT_CURSOR));
				scanner.acquire();
			} catch (ScannerIOException e) {
				// TODO Auto-generated catch block
				System.out.println("***************Fehler beim scannen*******************");
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				e.printStackTrace();
			}
				
			return;			
		}else if(cmd.equals("scanedit")){
			Point pt = ((JComponent)arg0.getSource()).getLocationOnScreen();
			ScannerUtil su = new ScannerUtil(new Point(pt.x,pt.y+32));
			su.setModal(true);
			su.setVisible(true);
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			updateInfoLab();
			
			return;			
		}
		
	}
	private void updateInfoLab(){
		if(!infolab[0].getText().equals(SystemConfig.sDokuScanner)){
			try {
				scanner.select(SystemConfig.sDokuScanner);
			} catch (ScannerIOException e) {
				e.printStackTrace();
			}
			infolab[0].setText(SystemConfig.sDokuScanner);
		}
		infolab[1].setText(SystemConfig.hmDokuScanner.get("farben"));
		infolab[2].setText(SystemConfig.hmDokuScanner.get("aufloesung")+"dpi");
		infolab[3].setText(SystemConfig.hmDokuScanner.get("seiten"));
		infolab[4].setText( (SystemConfig.hmDokuScanner.get("dialog").equals("1") ? "ja" : "nein"));
		bilderPan.validate();

		if(!infolabLeer[0].getText().equals(SystemConfig.sDokuScanner)){
			try {
				scanner.select(SystemConfig.sDokuScanner);
			} catch (ScannerIOException e) {
				e.printStackTrace();
			}
			infolabLeer[0].setText(SystemConfig.sDokuScanner);
		}
		infolabLeer[1].setText(SystemConfig.hmDokuScanner.get("farben"));
		infolabLeer[2].setText(SystemConfig.hmDokuScanner.get("aufloesung")+"dpi");
		infolabLeer[3].setText(SystemConfig.hmDokuScanner.get("seiten"));
		infolabLeer[4].setText( (SystemConfig.hmDokuScanner.get("dialog").equals("1") ? "ja" : "nein"));
		leerInfo.validate();
		
	}
	
	@Override
	public void tableChanged(TableModelEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	void setRezeptDaten(){
		int row = this.tabhistorie.getSelectedRow();
		if(row >= 0){
			final int xrow = row;
			SwingUtilities.invokeLater(new Runnable(){
				public  void run(){
					String reznr = (String)tabhistorie.getValueAt(xrow,0);
					String id = (String)tabhistorie.getValueAt(xrow,6);
					//jpan1.setRezeptDaten(reznr,id);
					System.out.println("Aus Bericht....."+reznr+"....."+id);
				}
			});	

		}
	}	
	/**************************************************
	 * 
	 * 
	 */
	public void scanStarten(){
		if((!scanaktiv) || 
				SystemConfig.sDokuScanner.equals("") ){
			System.out.println("Scanner = null");
			return;
		}
		if(scanner == null){
			scanner = Scanner.getDevice();	
		}
	    
	    try {
			String[] names = scanner.getDeviceNames();
			for(int i = 0; i < names.length;i++){
				if(names[i].equals(SystemConfig.sDokuScanner)){
					deviceinstalled = true;
				}
			}
	    	if(deviceinstalled){
	    		scanner.select(SystemConfig.sDokuScanner);	
	    	}else{
	    		if(infolab[0] != null){
	    			infolab[0].setText(scanner.getSelectedDeviceName());
	    		}
	    	}
		} catch (ScannerIOException e2) {
			e2.printStackTrace();
		}

		scanner.addListener( this);    
		
	}
	private Double[] getDims(String seite){
		List list = Arrays.asList( new String[] {"Din A6","Din A6-quer","Din A5","Din A5-quer","Din A4","Din A4-quer"});
		String[] dims = new String[] {"Din A6","Din A6-quer","Din A5","Din A5-quer","Din A4","Din A4-quer"};
		Double[][] d =  new Double[][]  {{4.23,5.82},{4.23,5.82},{5.82,8.26},{5.82,8.26},{8.26,11.69},{8.26,11.69}};
		Double[] ret = {8.26,11.69};		
		ret = d[list.indexOf(seite)].clone();
		return ret;
	}
	@Override
	public void update( ScannerIOMetadata.Type type, ScannerIOMetadata metadata ) {
		
		/*****************************************************/
		if ( ScannerIOMetadata.NEGOTIATE.equals(type)){
			ScannerDevice device=metadata.getDevice();
      		if(metadata instanceof TwainIOMetadata){
      			Double[] setDim = getDims(SystemConfig.hmDokuScanner.get("seiten"));
    			TwainSource source = ((TwainIOMetadata)metadata).getSource();
    			try{
    				int dpi= new Integer(SystemConfig.hmDokuScanner.get("aufloesung"));
    				source.getCapability(TwainConstants.ICAP_UNITS,TwainConstants.MSG_GETCURRENT).setCurrentValue(TwainConstants.TWUN_INCHES);
    				source.getCapability(TwainConstants.ICAP_XRESOLUTION,TwainConstants.MSG_GETCURRENT).setCurrentValue(dpi);
    				source.getCapability(TwainConstants.ICAP_YRESOLUTION,TwainConstants.MSG_GETCURRENT).setCurrentValue(dpi); 

    				if(SystemConfig.hmDokuScanner.get("farben").equals("Schwarz/Weiß")){
    					source.getCapability(TwainConstants.ICAP_PIXELTYPE).setCurrentValue(TwainConstants.TWPT_BW); 
    				}else if(SystemConfig.hmDokuScanner.get("farben").equals("Graustufen")){
    					source.getCapability(TwainConstants.ICAP_PIXELTYPE).setCurrentValue(TwainConstants.TWPT_GRAY); 
    				}else{
    					SystemConfig.hmDokuScanner.get("farben").equals("Farbe");
    					source.getCapability(TwainConstants.ICAP_PIXELTYPE).setCurrentValue(TwainConstants.TWPT_RGB); 
    				}
    				 
    				TwainImageLayout imageLayout=new TwainImageLayout(source);
    				imageLayout.get();
    				imageLayout.setLeft(0.0);
    				imageLayout.setTop(0.0);
    				imageLayout.setRight(setDim[0]);
    				imageLayout.setBottom(setDim[1]);
    				imageLayout.set();
					
    				device.setShowUserInterface((SystemConfig.hmDokuScanner.get("dialog").equals("1") ? true :false) );
    				device.setShowProgressBar(true);
    				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    			}catch(Exception e){
    	            System.out.println("3\b"+getClass().getName()+".update:\n\tCannot retrieve image information.\n\t"+e);
    			}
    		}
    		/*****************************************************/      		
		}else if ( ScannerIOMetadata.STATECHANGE.equals(type)){
    		if(metadata.isFinished()){
    			if(metadata.getImage() != null){
    				metadata.setImage(null);
	                Runtime r = Runtime.getRuntime();
	        	    r.gc();
	        	    long freeMem = r.freeMemory();
	        	    System.out.println("Freier Speicher "+freeMem);
    			}else{
	                Runtime r = Runtime.getRuntime();
	        	    r.gc();
	        	    long freeMem = r.freeMemory();
    			}
    	        System.out.println("Scanvorgang wurde beendet");
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    	      }
    		/*****************************************************/    		
		}else if ( type.equals(ScannerIOMetadata.EXCEPTION)){
			System.out.println("Exception in EXEPTION");
    	    if(!metadata.getException().getMessage().contains("Failed during call to twain source")){
        	    JOptionPane.showMessageDialog(null,"Bezug des Scans fehlgeschlagen.\nVersuchen Sie es mit einer niedrigeren Auflösung\n\n"+
	    		"Ideale Auflösung für Dokumentation: 150dpi");
    	    }
			
    	    scanner.removeListener(this);
    	    scanner = null;
            Runtime r = Runtime.getRuntime();
    	    r.gc();
    	    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    		/*****************************************************/    	    
		}else if ( ScannerIOMetadata.ACQUIRED.equals( type )){
			System.out.println("ACUIRED");
			System.out.println(metadata.getStateStr());
			if(metadata.getStateStr().contains("Transferring Data")){
				setCursor(new Cursor(Cursor.WAIT_CURSOR));
				if(metadata.getImage() != null){
					commonName = new Long(System.currentTimeMillis()).toString(); 
					String fname = "scan"+commonName+".jpg";
			        File file = new File(SystemConfig.hmVerzeichnisse.get("Temp"),fname);
			        try {
						ImageIO.write( metadata.getImage(),
								"jpg", 
								file ) ;
						
						System.out.println("Fertig mit Image schreiben");
				        final Image img = metadata.getImage().getScaledInstance(50, 65,Image.SCALE_SMOOTH);
				        final String pfad = file.getAbsolutePath();
				        new Thread(){
				        	public void run(){
				        		zeigeBilder(img,pfad,commonName);		
				        	}
				        }.start();
					} catch (IOException e) {
						System.out.println("Exception in Statechange - ACOUIRED");
						e.printStackTrace();
					}
				}else{
					System.out.println("ImageDate = null");
				}
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		}
	}   

	/**************************************************
	 * 
	 * 
	 */
	public void zeigeBilder(Image imgx,String datei,String commonname){
		Image img = imgx;

		bildnummer++;
		String name = "Bildnummer-"+bildnummer; 
		LabName.add(name);
		if(SystemConfig.hmDokuScanner.get("seiten").contains("quer")){
			BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
			Graphics g = bimage.createGraphics();
	        g.drawImage(img, 0, 0, null);
	        g.dispose();
	        img = GrafikTools.rotate90DX(bimage).getScaledInstance(img.getWidth(null), img.getHeight(null), Image.SCALE_SMOOTH);
		}
		ImageIcon icon = new ImageIcon(img);

		JLabel lab = new JLabel("Seite-"+(vecBilderPfad.size()+1));
		lab.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		lab.addMouseListener(mlist);
		lab.setName(name);
		lab.setToolTipText("Doppelklick um "+lab.getText()+" zu öffnen");
		lab.setHorizontalTextPosition(JLabel.CENTER);
		lab.setVerticalTextPosition(JLabel.BOTTOM);
		lab.setIcon(icon);
		Labels.add(lab);
		vecBilderPfad.add(datei);
		String pdfPfad = "pdf"+commonname+".pdf";
		vecPdfPfad.add(pdfPfad);
		if(vecBilderPfad.size()==1){
			bilderPan.add(setzePlusMinus());
			pmbut[0].setEnabled(false);
			pmbut[2].setEnabled(false);
			pmbut[3].setEnabled(false);
			bilderPan.validate();
		}else if(vecBilderPfad.size() >1 ){
			pmbut[0].setEnabled(true);
			pmbut[2].setEnabled(true);
			pmbut[3].setEnabled(true);
		}
		
		bilderPan.add(lab);
		bilderPan.validate();
	}
	public void pdfZeigen(int seite){
		  try {
			  setCursor(new Cursor(Cursor.WAIT_CURSOR));

				Document document = new Document();
				if(SystemConfig.hmDokuScanner.get("seiten").contains("Din A4")){
					if( SystemConfig.hmDokuScanner.get("seiten").contains("quer") ){
						document.setPageSize(PageSize.A4.rotate());
					}else{
						document.setPageSize(PageSize.A4);	
					}
				}else if(SystemConfig.hmDokuScanner.get("seiten").contains("Din A5")){
					if( SystemConfig.hmDokuScanner.get("seiten").contains("quer") ){
						document.setPageSize(PageSize.A5.rotate());
					}else{
						document.setPageSize(PageSize.A5);	
					}
				}else if(SystemConfig.hmDokuScanner.get("seiten").contains("Din A6")){
					if( SystemConfig.hmDokuScanner.get("seiten").contains("quer") ){
						document.setPageSize(PageSize.A6.rotate());
					}else{
						document.setPageSize(PageSize.A6);	
					}
				}
				document.setMargins(0.0f, 0.0f, 0.0f, 0.0f);
				String datname = SystemConfig.hmVerzeichnisse.get("Temp")+"/"+vecPdfPfad.get(seite);
				
				PdfWriter.getInstance(document, new FileOutputStream(datname));  
	      
				/***************************/
				document.open(); 
				if( SystemConfig.hmDokuScanner.get("seiten").contains("quer") ){
					Image jpg1 = ImageIO.read(new File(vecBilderPfad.get(seite)));
					if( SystemConfig.hmDokuScanner.get("seiten").contains("quer") ){
						jpg1 = GrafikTools.rotateImage90SX(jpg1);				}
					
					com.lowagie.text.Image jpg2 = com.lowagie.text.Image.getInstance(jpg1,null);
					jpg2.scaleAbsoluteHeight(document.getPageSize().getWidth());
					jpg2.scaleAbsoluteWidth(document.getPageSize().getHeight());
					document.add(jpg2);
				}else{
					com.lowagie.text.Image jpg2 = com.lowagie.text.Image.getInstance(vecBilderPfad.get(seite));
					jpg2.scaleAbsoluteHeight(document.getPageSize().getHeight());
					jpg2.scaleAbsoluteWidth(document.getPageSize().getWidth());
					document.add(jpg2);
				}
				document.close();
				Thread.sleep(100);
				/***************************/				
				
				File file = new File(SystemConfig.hmFremdProgs.get("AcrobatReader"));
				if(!file.exists()){
					JOptionPane.showMessageDialog(null, "Der Pfad zu Ihrem Adobe-Acrobatreader ist nicht korrekt konfiguriert");
					return;
				}
				/*
				try {
		            Runtime.getRuntime().exec(file.getAbsolutePath()+" "+datname);
		        } catch(IOException e) {
		            e.printStackTrace();
		        }
		        */
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				  setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			} catch (BadElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				  setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				  setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	  
		
	}
	public JXPanel setzePlusMinus(){
		plusminus = new JXPanel();
		FormLayout lay = new FormLayout("p","p,p,p");
		CellConstraints cc = new CellConstraints();
		plusminus.setLayout(lay);
		plusminus.setOpaque(false);
		
		JToolBar jtbpm = new JToolBar();
		jtbpm.setRollover(true);
		jtbpm.setBorder(null);
		jtbpm.setOpaque(false);
		
		pmbut[0] = new JButton();
		pmbut[0].setIcon(SystemConfig.hmSysIcons.get("delete"));
		pmbut[0].setToolTipText("Aktive Dokuseite löschen");
		pmbut[0].setActionCommand("Dokudelete");
		pmbut[0].addActionListener(this);
		jtbpm.add(pmbut[0]);

		pmbut[1] = new JButton();
		pmbut[1].setIcon(SystemConfig.hmSysIcons.get("save"));
		pmbut[1].setToolTipText("Seiten zusammenführen und Dokumentation erstellen");
		pmbut[1].setActionCommand("Dokusave");
		pmbut[1].addActionListener(this);
		jtbpm.add(pmbut[1]);
		
		jtbpm.addSeparator(new Dimension(15,0));
		plusminus.add(jtbpm,cc.xy(1,1));

		jtbpm = new JToolBar();
		jtbpm.setRollover(true);
		jtbpm.setBorder(null);
		jtbpm.setOpaque(false);
		
		pmbut[2] = new JButton();
		pmbut[2].setIcon(SystemConfig.hmSysIcons.get("links"));
		pmbut[2].setToolTipText("Seiten nach links verschieben");
		pmbut[2].setActionCommand("Dokulinks");
		pmbut[2].addActionListener(this);
		jtbpm.add(pmbut[2]);

		pmbut[3] = new JButton();
		pmbut[3].setIcon(SystemConfig.hmSysIcons.get("rechts"));
		pmbut[3].setToolTipText("Seiten nach rechts verschieben");
		pmbut[3].setActionCommand("Dokurechts");
		pmbut[3].addActionListener(this);
		jtbpm.add(pmbut[3]);
		plusminus.add(jtbpm,cc.xy(1,2));
		
		return plusminus;
	}
	
	public void setzeListener(){
		mlist = new MouseListener(){

			@Override
			public void mousePressed(MouseEvent arg0) {
				if(arg0.getClickCount()==2){
					System.out.println("LabeName = "+((JComponent)arg0.getSource()).getName());
					int seite = new Integer( ((JComponent)arg0.getSource()).getName().split("-")[1] );
					pdfZeigen(seite-1);
					File file = new File(SystemConfig.hmFremdProgs.get("AcrobatReader"));
					if(!file.exists()){
						JOptionPane.showMessageDialog(null, "Der Pfad zu Ihrem Adobe-Acrobatreader ist nicht korrekt konfiguriert");
						return;
					}
					try {
						String datei = SystemConfig.hmVerzeichnisse.get("Temp")+"/"+vecPdfPfad.get(aktivesBild-1);
						if(Reha.osVersion.contains("Windows")){
							datei = datei.replaceAll("/", "\\\\");
						}
						long zeit1 = System.currentTimeMillis();
						File f = new File(datei);
						while(!f.canRead()){
							try {
								Thread.sleep(50);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							if((System.currentTimeMillis()-zeit1) > 1000){
								break;
							}
						}
						
						System.out.println("Starte :"+file.getAbsolutePath()+" "+datei);
						//Runtime.getRuntime().exec(datei.trim());
						Runtime.getRuntime().exec(file.getAbsolutePath().toString()+" "+datei.trim());
			        } catch(IOException e) {
			            e.printStackTrace();
			        }
					
				}else if(arg0.getClickCount()==1){
					if(aktivesBild > 0){
						int bild = new Integer( Labels.get(aktivesBild-1).getText().split("-")[1]); 
						Labels.get(bild-1).setBorder(BorderFactory.createLineBorder(Color.BLACK));
					}
					aktivesBild = new Integer( ((JLabel)((JComponent)arg0.getSource())).getText().split("-")[1] );
					Labels.get(aktivesBild-1).setBorder(BorderFactory.createLineBorder(Color.RED));
				}
				
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
								
			}


			@Override
			public void mouseReleased(MouseEvent arg0) {
			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			
		};
		
	}
	class DokuListener implements ScannerListener{
		@Override
		public void update(ScannerIOMetadata.Type type, ScannerIOMetadata metadata) {
			if ( ScannerIOMetadata.NEGOTIATE.equals(type)){
				
			}else if ( ScannerIOMetadata.STATECHANGE.equals(type)){
				
			}else if ( type.equals(ScannerIOMetadata.EXCEPTION)){
				
			}else if ( ScannerIOMetadata.ACQUIRED.equals( type )){
				
			}
		}
		
	}
	public void holeRezepte(String patint,String rez_nr){
		final String xpatint = patint;
		final String xrez_nr = rez_nr;
/*
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
		*/
				//String sstmt = "select * from verordn where PAT_INTERN ='"+xpatint+"' ORDER BY REZ_DATUM";
				Vector vec = SqlInfo.holeSaetze("doku", "rez_nr,zzstatus,DATE_FORMAT(rez_datum,'%d.%m.%Y') AS drez_datum,DATE_FORMAT(datum,'%d.%m.%Y') AS datum," +
						"DATE_FORMAT(lastdate,'%d.%m.%Y') AS datum,pat_intern,id", 
						"pat_intern='"+xpatint+"' ORDER BY rez_datum DESC", Arrays.asList(new String[]{}));

				int anz = vec.size();

				for(int i = 0; i < anz;i++){
					if(i==0){
						dtblm.setRowCount(0);						
					}

					int zzbild = 0;
					if( ((Vector)vec.get(i)).get(1) == null){
						zzbild = 0;
					}else if(!((Vector)vec.get(i)).get(1).equals("")){
						zzbild = new Integer((String) ((Vector)vec.get(i)).get(1) );
					}
					//((Vector)vec.get(i)).set(3, PatGrundPanel.thisClass.imgzuzahl[zzbild]);
					
					//System.out.println("Inhalt von zzstatus ="+zzbild);
					dtblm.addRow((Vector)vec.get(i));
					
					dtblm.setValueAt(PatGrundPanel.thisClass.imgzuzahl[zzbild], i, 1);
					if(i==0){
						final int ix = i;
	                    new Thread(){
	                    	public void run(){
	                    		//holeEinzelTermine(ix,null);
	                    	}
	                    }.start();
					}
				}
				if(anz > 0){
					setzeRezeptPanelAufNull(false);
					int anzeigen = -1;
					if(xrez_nr.length() > 0){
						int row = 0;
						rezneugefunden = true;
						for(int ii = 0; ii < anz;ii++){
							if(tabhistorie.getValueAt(ii,0).equals(xrez_nr)){
								row = ii;
								break;
							}
							
						}
						tabhistorie.setRowSelectionInterval(row, row);
						//jpan1.setRezeptDaten((String)tabhistorie.getValueAt(row, 0),(String)tabhistorie.getValueAt(row, 6));
						tabhistorie.scrollRowToVisible(row);
						//holeEinzelTermine(row,null);
						//System.out.println("rezeptdaten akutalisieren in holeRezepte 1");
					}else{
						rezneugefunden = true;
						tabhistorie.setRowSelectionInterval(0, 0);
						//jpan1.setRezeptDaten((String)tabhistorie.getValueAt(0, 0),(String)tabhistorie.getValueAt(0, 6));
						//System.out.println("rezeptdaten akutalisieren in holeRezepte 1");						
					}
					anzahlRezepte.setText("Anzahl Rezepte in Historie: "+anz);
					PatGrundPanel.thisClass.jtab.setTitleAt(1, PatGrundPanel.thisClass.tabTitel[1]+" - <font color='#ff0000'>"+anz+"</font>");
					wechselPanel.revalidate();
					wechselPanel.repaint();					
				}else{
					setzeRezeptPanelAufNull(true);
					anzahlRezepte.setText("Anzahl Rezepte in Historie: "+anz);
					PatGrundPanel.thisClass.jtab.setTitleAt(1, PatGrundPanel.thisClass.tabTitel[1]+" - <font color='#000000'>"+anz+"</font>");
					wechselPanel.revalidate();
					wechselPanel.repaint();
					dtblm.setRowCount(0);
					dtermm.setRowCount(0);
				}
				/*					
				return null;
			}
		
		}.execute();
*/		
	}
	
	
	/*************************************************/
	class DokuListSelectionHandler implements ListSelectionListener {

	    public void valueChanged(ListSelectionEvent e) {
			if(rezneugefunden){
				rezneugefunden = false;
				return;
			}
	        ListSelectionModel lsm = (ListSelectionModel)e.getSource();
	        
	        int firstIndex = e.getFirstIndex();
	        int lastIndex = e.getLastIndex();
	        boolean isAdjusting = e.getValueIsAdjusting();
	        if(isAdjusting){
	        	return;
	        }
			StringBuffer output = new StringBuffer();
	        if (lsm.isSelectionEmpty()) {

	        } else {
	            int minIndex = lsm.getMinSelectionIndex();
	            int maxIndex = lsm.getMaxSelectionIndex();
	            for (int i = minIndex; i <= maxIndex; i++) {
	                if (lsm.isSelectedIndex(i)) {
	                	final int ix = i;
	                	
	                	new SwingWorker<Void,Void>(){

							@Override
							protected Void doInBackground() throws Exception {
						
								// TODO Auto-generated method stub
								inDokuDaten = true;
	                			setCursor(new Cursor(Cursor.WAIT_CURSOR));
	                    		//holeEinzelTermine(ix,null);
	    						//jpan1.setRezeptDaten((String)tabhistorie.getValueAt(ix, 0),(String)tabhistorie.getValueAt(ix, 6));
	    						//System.out.println("rezeptdaten akutalisieren in ListSelectionHandler");
	    						setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	    						inDokuDaten = false;

	    						return null;
							}
	                		
	                	}.execute();

	                    break;
	                }
	            }
	        }
	        //System.out.println(output.toString());
	    }
	}


	

}
/*************************************/
/*************************************/

class MyDoku2TableModel extends DefaultTableModel{
	   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Class getColumnClass(int columnIndex) {
		   if(columnIndex==1){
			   return JLabel.class;}
		   else{
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

class MyDokuTermTableModel extends DefaultTableModel{
	   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Class getColumnClass(int columnIndex) {
		   if(columnIndex==0){return String.class;}
		   /*else if(columnIndex==1){return JLabel.class;}*/
		   else{return String.class;}
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
	        }else if(col == 11){
	        	return true;
	        } else{
	          return false;
	        }
	      }
}
