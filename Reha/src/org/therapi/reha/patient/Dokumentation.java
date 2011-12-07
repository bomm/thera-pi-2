package org.therapi.reha.patient;




import generalSplash.RehaSplash;
import geraeteInit.ScannerUtil;
import hauptFenster.Reha;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import jxTableTools.TableTool;
import oOorgTools.OOTools;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.IconValues;
import org.jdesktop.swingx.renderer.MappedValue;
import org.jdesktop.swingx.renderer.StringValues;

import patientenFenster.KeinRezept;
import patientenFenster.MyAccessory;
import rechteTools.Rechte;
import sqlTools.ExUndHop;
import sqlTools.SqlInfo;
import systemEinstellungen.SystemConfig;
import systemTools.Colors;
import systemTools.FileTools;
import systemTools.GrafikTools;
import systemTools.IconListRenderer;
import systemTools.JCompTools;
import terminKalender.DatFunk;
import uk.co.mmscomputing.device.scanner.Scanner;
import uk.co.mmscomputing.device.scanner.ScannerDevice;
import uk.co.mmscomputing.device.scanner.ScannerIOException;
import uk.co.mmscomputing.device.scanner.ScannerIOMetadata;
import uk.co.mmscomputing.device.scanner.ScannerListener;
import uk.co.mmscomputing.device.twain.TwainConstants;
import uk.co.mmscomputing.device.twain.TwainIOMetadata;
import uk.co.mmscomputing.device.twain.TwainImageLayout;
import uk.co.mmscomputing.device.twain.TwainSource;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.event.IDocumentEvent;
import ag.ion.bion.officelayer.event.IDocumentListener;
import ag.ion.bion.officelayer.event.IEvent;
import ag.ion.bion.officelayer.spreadsheet.ISpreadsheetDocument;
import ag.ion.bion.officelayer.text.ITextDocument;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import com.mysql.jdbc.PreparedStatement;

import dialoge.ToolsDialog;

public class Dokumentation extends JXPanel implements ActionListener, TableModelListener, PropertyChangeListener, ScannerListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//public static Dokumentation doku = null;
	JXPanel leerPanel = null;
	//JXPanel vollPanel = null;
	JXPanel vollPanel = null;
	JXPanel wechselPanel = null;
	public JLabel anzahlDokus= null;
	//public JLabel anzahlRezepte= null;
	public String aktPanel = "";
	public JXTable tabdokus= null;
	public MyDoku2TableModel dtblm;
	public TableCellEditor tbl = null;
	//public boolean rezneugefunden = false;
	//public boolean neuDlgOffen = false;
	//public String[] indphysio = null;
	//public String[] indergo = null;
	//public String[] indlogo = null;
	public JXPanel jpan1 = null;
	public JButton[] dokubut = {null,null,null,null,null,null};
	public JButton[] pmbut = {null,null,null,null,null};
	public static boolean inDokuDaten = false;
	public JComboBox seitengroesse = null;
	public JComboBox aufloesung = null;
	public JComboBox farbe = null;
	public Vector<String> LabName = new Vector<String>();
	int bildnummer=0;
	public Vector<String>vecBilderPfad = new Vector<String>();
	public Vector<String>vecBilderFormat = new Vector<String>();
	public Vector<String>vecPdfPfad = new Vector<String>();
	public Vector<String>vecBilderAktion = new Vector<String>();
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
	public String lastPath = null;
	public RehaSplash rehaSplash =  null;
	public ImageIcon[] tabIcons = {null,null,null,null};
	public String aktion  = "";
	public String quelle = "";
	public String nameOOorgDokuNeu;
	public ImageIcon pdfplus;
	public ImageIcon oowriterplus;
	public ImageIcon oocalcplus;

	//public JRtaTextField annika = null;
	Scanner scanner;
	public Dokumentation(){
		super();
		//doku = this;
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
		"0dlu,p,2dlu,p,2dlu,fill:0:grow(1.00),2dlu");
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
						"p,2dlu,100dlu,0dlu,fill:0:grow(1.00),0dlu");
				CellConstraints vpcc = new CellConstraints();
				vollPanel.setLayout(vplay);
				vollPanel.setOpaque(false);
				vollPanel.setBorder(null);
				
				Font font = new Font("Tahoma",Font.PLAIN,11);
				anzahlDokus = new JLabel("Anzahl gespeicherter Dokumentationen: 0");
				anzahlDokus.setFont(font);
				vollPanel.add(anzahlDokus,vpcc.xy(1,1));
				
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
						Image ico = SystemConfig.hmSysIcons.get("pdf").getImage().getScaledInstance(26,26, Image.SCALE_SMOOTH);
						pdfplus = new ImageIcon(ico);
						ico = SystemConfig.hmSysIcons.get("ooowriter").getImage().getScaledInstance(26,26, Image.SCALE_SMOOTH);
						oowriterplus = new ImageIcon(ico);
						ico = SystemConfig.hmSysIcons.get("ooocalc").getImage().getScaledInstance(26,26, Image.SCALE_SMOOTH);
						oocalcplus = new ImageIcon(ico);
						
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
		FormLayout lay = new FormLayout("0dlu,fill:0:grow(1.00),0dlu","0dlu,fill:60:grow(1.00),1dlu,42dlu");
		CellConstraints cc = new CellConstraints();
		tbereich.setLayout(lay);

		tbereich.add(getBildPanel(),cc.xy(2,2));
		
		tbereich.add(getInfoPanel(),cc.xy(2,4,CellConstraints.DEFAULT,CellConstraints.BOTTOM));
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
		FormLayout lay = new FormLayout("2dlu,p,25dlu,right:max(50dlu;p),2dlu,p,20dlu,right:max(50dlu;p),2dlu,p",
       // 1    2  3    4   5  6   7  8   9
		" 0dlu,p,0dlu, p,0dlu,p,0dlu,p, 0dlu");
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
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		ImageIcon hgicon;
		int icx,icy;
		AlphaComposite xac1 = null;
		AlphaComposite xac2 = null;		
		DokuPanel(){
			super();
			setOpaque(false);
			hgicon = SystemConfig.hmSysIcons.get("scannergross"); 
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
		//System.out.println("Image icon bei Tabellenerstellung = "+(SystemConfig.hmSysIcons.get("pdf")==null));
		tabIcons[0]= SystemConfig.hmSysIcons.get("pdf");
		tabIcons[1]= new ImageIcon(SystemConfig.hmSysIcons.get("ooowriter").getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH));
		tabIcons[2]= new ImageIcon(SystemConfig.hmSysIcons.get("ooocalc").getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH));

		dtblm = new MyDoku2TableModel();
		String[] column = 	{"Doku-Id","Doku-Art","Titel","erfaßt am","von","","",""};
		dtblm.setColumnIdentifiers(column);
		tabdokus = new JXTable(dtblm);
		tabdokus.setRowHeight(tabdokus.getRowHeight()+10);
		tabdokus.setHighlighters(HighlighterFactory.createSimpleStriping(Colors.PiOrange.alpha(0.25f)));
		tabdokus.setDoubleBuffered(true);
		tabdokus.setEditable(false);
		tabdokus.setSortable(false);
		tabdokus.getColumn(0).setMaxWidth(50);
		TableCellRenderer renderer = new DefaultTableRenderer(new MappedValue(StringValues.EMPTY, IconValues.ICON), JLabel.CENTER);
		tabdokus.getColumn(1).setCellRenderer(renderer);
		tabdokus.getColumn(1).setMaxWidth(50);
		tabdokus.getColumn(2).setMinWidth(275);
		tabdokus.getColumn(3).setMaxWidth(80);
		//tabhistorie.getColumn(4).setMinWidth(0);
		//tabhistorie.getColumn(4).setMaxWidth(0);		
		tabdokus.getColumn(5).setMinWidth(0);
		tabdokus.getColumn(5).setMaxWidth(0);		
		tabdokus.getColumn(6).setMinWidth(0);
		tabdokus.getColumn(6).setMaxWidth(0);		
		tabdokus.getColumn(7).setMinWidth(0);
		tabdokus.getColumn(7).setMaxWidth(0);		

		tabdokus.validate();
		tabdokus.setName("AktDoku");
		tabdokus.setSelectionMode(0);
		//tabaktrez.addPropertyChangeListener(this);
		tabdokus.getSelectionModel().addListSelectionListener( new DokuListSelectionHandler());
		tabdokus.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent arg0) {
				if(!Rechte.hatRecht(Rechte.Doku_open, true)){
					return;
				}
				if(arg0.getClickCount()==2){
					while(inDokuDaten){
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					int row = tabdokus.getSelectedRow();
					/***********************/
					if(! ((String)tabdokus.getValueAt(row, 7)).trim().equals("")){
						setCursor(Reha.thisClass.wartenCursor);
						String sid = (String)tabdokus.getValueAt(row, 6);
						String sdatei = SystemConfig.hmVerzeichnisse.get("Temp")+"/"+(String)tabdokus.getValueAt(row, 7);
						holeOorg(sdatei,sid);
						return;
					}
					
					/***********************/
					String sdatei = SystemConfig.hmVerzeichnisse.get("Temp")+"/pdf"+tabdokus.getValueAt(row, 0)+".pdf";
					//System.out.println("Starte doku holen");
					String sid = (String)tabdokus.getValueAt(row, 6);
					File file = new File(sdatei);
					if(file.exists()){
						final String xdatei = sdatei;
						new SwingWorker<Void,Void>(){
							@Override
							protected Void doInBackground() throws Exception {
								Process process = new ProcessBuilder(SystemConfig.hmFremdProgs.get("AcrobatReader"),"",xdatei).start();
							       InputStream is = process.getInputStream();
							       InputStreamReader isr = new InputStreamReader(is);
							       BufferedReader br = new BufferedReader(isr);
							       //String line;
							       while ((br.readLine()) != null) {
							         //System.out.println(line);
							       }
							       is.close();
							       isr.close();
							       br.close();

								return null;
							}
							
						}.execute();
					}else{
						setCursor(Reha.thisClass.wartenCursor);
						/*
						final String xid = sid;
						new SwingWorker<Void,Void>(){
							@Override
							protected Void doInBackground() throws Exception {
								long zeit = System.currentTimeMillis();
								return null;
							}
							
						}.execute();
						*/
						holeDoku(sdatei,sid);
						setCursor(Reha.thisClass.normalCursor);
					}
				}
			}
		});
		tabdokus.addKeyListener(new KeyAdapter(){
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
		JScrollPane aktrezscr = JCompTools.getTransparentScrollPane((Component)tabdokus);
		aktrezscr.validate(); 
		aktrezscr.getVerticalScrollBar().setUnitIncrement(15);
		dummypan.add(aktrezscr,BorderLayout.CENTER);
		dummypan.validate();
		return dummypan;
	}
	
	public void setzeDokuPanelAufNull(boolean aufnull){
		if(aufnull){
			if(aktPanel.equals("vollPanel")){
				wechselPanel.remove(vollPanel);
				wechselPanel.add(leerPanel);
				aktPanel = "leerPanel";
				try{
					dokubut[2].setEnabled(false);
				}catch(Exception ex ){}	
				wechselPanel.validate();
				wechselPanel.repaint();
				for(int i = 0; i < 4;i++){
					//dokubut[i].setEnabled(false);
				}
			}
		}else{
			if(aktPanel.equals("leerPanel")){
				wechselPanel.remove(leerPanel);
				wechselPanel.add(vollPanel);
				aktPanel = "vollPanel";
				dokubut[1].setEnabled(true);
				/*
				for(int i = 0; i < 4;i++){
					//dokubut[i].setEnabled(true);
				}
				*/
				wechselPanel.validate();
				wechselPanel.repaint();
			}
		}
	}
////////////////////////////
////////////////////////////
//////////////////////////// Holt Doku aus der Doku-Datenbank
////////////////////////////
	private Dokumentation getInstance(){
		return this;
	}
	public void holeOorg(String sdatei,String sid){
		Statement stmt = null;;
		ResultSet rs = null;
		//int bilder = 0;
		//System.out.println(sdatei);

		try {
			stmt = (Statement) Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE );
				String test = "select dokublob from doku1 where id='"+sid+"'";
				rs = (ResultSet) stmt.executeQuery(test);
				if(rs.next()){
					FileTools.ByteArray2File(rs.getBytes(1), sdatei);
				}
				final String xdatei = sdatei;
				final String xid = sid;
				new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() throws Exception {
						if(xdatei.toLowerCase().endsWith("odt")){
							ITextDocument itext = new OOTools().starteWriterMitDatei(xdatei);
							itext.addDocumentListener(new OoListener(Reha.officeapplication,xdatei,xid,getInstance()));
						}else if(xdatei.toLowerCase().endsWith("ods")){
							ISpreadsheetDocument ispread = new OOTools().starteCalcMitDatei(xdatei);
							ispread.addDocumentListener(new OoListener(Reha.officeapplication,xdatei,xid,getInstance()));
						}
						Reha.thisClass.patpanel.dokumentation.setCursor(Reha.thisClass.normalCursor);
						return null;
					}
				}.execute();
				
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) { // ignore }
					rs = null;
				}
			}	
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) { // ignore }
					stmt = null;
				}
			}
			
		}
				
		
		
	}
	public void holeDoku(String datei,String id){
		Statement stmt = null;;
		ResultSet rs = null;
		//int bilder = 0;
		
		try {
			stmt = (Statement) Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE );
					String test = "select dokublob from doku1 where id='"+id+"'";
				rs = (ResultSet) stmt.executeQuery(test);
				if(rs.next()){
					FileTools.ByteArray2File(rs.getBytes(1), datei);
				}
				
				final String xdatei = datei;
				new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() throws Exception {
						Process process = new ProcessBuilder(SystemConfig.hmFremdProgs.get("AcrobatReader"),"",xdatei).start();
					       InputStream is = process.getInputStream();
					       InputStreamReader isr = new InputStreamReader(is);
					       BufferedReader br = new BufferedReader(isr);
					       //String line;
					       while ((br.readLine()) != null) {
					         //System.out.println(line);
					       }
					       is.close();
					       isr.close();
					       br.close();

						return null;
					}
					
				}.execute();


		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) { // ignore }
					rs = null;
				}
			}	
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) { // ignore }
					stmt = null;
				}
			}
			
		}
	}
	/*****************************
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * */
	public JToolBar getToolbar(){
		JToolBar jtb = new JToolBar();
		jtb.setRollover(true);
		jtb.setBorder(null);
		jtb.setOpaque(false);

		dokubut[5] = new JButton();
		dokubut[5].setIcon(SystemConfig.hmSysIcons.get("delete"));
		dokubut[5].setToolTipText("Dokumentation löschen");
		dokubut[5].setActionCommand("delete");
		dokubut[5].setEnabled(false);
		dokubut[5].addActionListener(this);
		jtb.add(dokubut[5]);

		jtb.addSeparator(new Dimension(10,0));
		dokubut[2] = new JButton();
		dokubut[2].setIcon(SystemConfig.hmSysIcons.get("abbruch"));
		dokubut[2].setToolTipText("Kompletten Vorgang abbrechen");
		dokubut[2].setActionCommand("Dokuabbruch");
		dokubut[2].addActionListener(this);
		dokubut[2].setEnabled(false);
		jtb.add(dokubut[2]);

		jtb.addSeparator(new Dimension(40,0));
		
		dokubut[0] = new JButton();
		dokubut[0].setIcon(SystemConfig.hmSysIcons.get("scanner"));
		dokubut[0].setToolTipText("Papierbericht einscannen");
		dokubut[0].setActionCommand("scannen");
		dokubut[0].setEnabled(false);
		dokubut[0].addActionListener(this);
		jtb.add(dokubut[0]);
	
		jtb.addSeparator(new Dimension(40,0));
		dokubut[1] = new JButton();
		dokubut[1].setIcon(SystemConfig.hmSysIcons.get("tools"));
		dokubut[1].setToolTipText("Scannereinstellungen ändern");
		dokubut[1].setActionCommand("werkzeuge");
		dokubut[1].addActionListener(this);		
		jtb.add(dokubut[1]);

		/*
		dokubut[1] = new JButton();
		dokubut[1].setIcon(SystemConfig.hmSysIcons.get("tools"));
		dokubut[1].setToolTipText("Scannereinstellungen ändern");
		dokubut[1].setActionCommand("scanedit");
		dokubut[1].addActionListener(this);		
		jtb.add(dokubut[1]);

		jtb.addSeparator(new Dimension(40,0));
		dokubut[3] = new JButton();
		dokubut[3].setIcon(SystemConfig.hmSysIcons.get("camera"));
		dokubut[3].setToolTipText("Photo von DigiCam in Doku aufnehmen");
		dokubut[3].setActionCommand("Digicam");
		dokubut[3].addActionListener(this);
		dokubut[3].setEnabled(false);
		jtb.add(dokubut[3]);
		

		dokubut[4] = new JButton();
		dokubut[4].setIcon(SystemConfig.hmSysIcons.get("openoffice26"));
		dokubut[4].setToolTipText("OpenOffice-Dokument (Writer oder Calc) in Doku aufnehmen");
		dokubut[4].setActionCommand("Oofiles");
		dokubut[4].addActionListener(this);
		dokubut[4].setEnabled(false);
		jtb.add(dokubut[4]);
		*/
		
		

		
		
		
		/*
		
		JLabel jlab = new JLabel("Patienten-Nummer eingeben ");
		jlab.setOpaque(false);
		jtb.add(jlab);
		JXPanel tfp = new JXPanel(new FlowLayout(FlowLayout.LEFT));
		tfp.setOpaque(false);
		annika = new JRtaTextField("ZAHLEN",true);
		annika.setPreferredSize(new Dimension(100,25));
		tfp.add(annika);
		jtb.add(tfp);
		*/
		
		if(!scanaktiv){
			dokubut[0].setEnabled(false);
			dokubut[1].setEnabled(false);
			dokubut[2].setEnabled(false);
		}
		

		for(int i = 0; i < 4;i++){
			//dokubut[i].setEnabled(false);
		}
		
		
		return jtb;
	}
	
	
	

	public void macheTabelle(Vector<?> vec){
		if(vec.size()> 0){
			dtblm.addRow(vec);	
		}else{
			dtblm.setRowCount(0);
			tabdokus.validate();
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
			if(!Rechte.hatRecht(Rechte.Doku_scannen, true)){
				return;
			}
			if(SystemConfig.sDokuScanner.equals("Scanner nicht aktiviert!")){
				return;
			}
			/*
			if(annika.getText().trim().equals("")){
				JOptionPane.showMessageDialog(null, "Bitte vor dem Scannen die Patientennummer eingeben");
				return;
			}
			*/
			if(aktPanel.equals("leerPanel")){
				this.setzeDokuPanelAufNull(false);
			}
			try {
				setzeDokuPanelAufNull(false);
//				vollpanel.validate();
				if(scanner==null){
					//System.out.println("Neustart des Scannersystems erforderlich");
					scanStarten();					
				}
				setCursor(Reha.thisClass.wartenCursor);
				scanner.acquire();
        		aktion = "bildgescannt";
			} catch (ScannerIOException e) {
				// TODO Auto-generated catch block
				//System.out.println("***************Fehler beim scannen*******************");
				setCursor(Reha.thisClass.normalCursor);
				e.printStackTrace();
			}
				
			return;			
		}else if(cmd.equals("scanedit")){
			if(!Rechte.hatRecht(Rechte.Doku_scannen, true)){
				return;
			}
			Point pt = ((JComponent)arg0.getSource()).getLocationOnScreen();
			final Point ptx = pt;
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					ScannerUtil su = new ScannerUtil(new Point(ptx.x,ptx.y+32));
					su.setModal(true);
					su.setVisible(true);
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					updateInfoLab();

					return null;
				}
			}.execute();

			
			return;			
		}else if(cmd.equals("Dokusave")){
			//rehaSplash = new RehaSplash(null,"Erstelle Dokumentation");
			//rehaSplash.setVisible(true);
			String value = (String)JOptionPane.showInputDialog(null,
					"Bitte einen Titel für die Dokumentation eingeben\n\n", "Benutzereingabe erforderlich....", JOptionPane.PLAIN_MESSAGE, null,
					null, "Eingescannte Therapeuten-Doku");
			if( (value == null) || (value.length()==0)){
				JOptionPane.showMessageDialog(null, "Kein Titel - kein speichern. Ganz einfach!!");
				return;
			}
			/*
			if(annika.getText().trim().equals("")){
				JOptionPane.showMessageDialog(null, "Ohne Patientennummer kann nicht gespeichert werden");
				return;
			}
			*/
			doDokusave(value);
			
			//rehaSplash.dispose();
			//rehaSplash = null;
			return;
		}else if(cmd.equals("Dokudelete")){
			if(!Rechte.hatRecht(Rechte.Doku_scannen, true)){
				return;
			}
			doDokudelete();
			return;
		}else if(cmd.equals("Dokuabbruch")){
			loescheBilderPan();
			return;
		}else if(cmd.equals("Digicam")){
			ladeJpeg();
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {

					return null;
				}
			}.execute();
			return;
		}else if(cmd.equals("delete")){
			if(!Rechte.hatRecht(Rechte.Doku_delete, true)){
				return;
			}
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					int row = tabdokus.getSelectedRow();			
					if(row >= 0){
						
						String sdokuid = (String)tabdokus.getValueAt(row, 0);
						int frage = JOptionPane.showConfirmDialog(null, "Soll die Dokumentation mit der ID-"+sdokuid+" wirklich gelöscht werden?","Achtung wichtige Benutzeranfrage",JOptionPane.YES_NO_OPTION);
						if(frage == JOptionPane.NO_OPTION){
							return null;
						}
						String scmd = "delete from doku1 where dokuid='"+sdokuid+"'";
						new ExUndHop().setzeStatement(scmd);
						TableTool.loescheRow(tabdokus, row);
						if(tabdokus.getRowCount()==0){
							if(plusminus != null){
								loescheBilderPan();						
							}
							setzeDokuPanelAufNull(true);
							dokubut[5].setEnabled(false);
						}
						Reha.thisClass.patpanel.getTab().setTitleAt(3,macheHtmlTitel(tabdokus.getRowCount(),"Dokumentation"));
					}
					return null;
				}
			}.execute();
		}else if(cmd.equals("Oofiles")){
			setCursor(Reha.thisClass.wartenCursor);
			ladeOoDocs();
			setCursor(Reha.thisClass.normalCursor);
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					

					return null;
				}
			}.execute();	
		}else if(cmd.equals("werkzeuge")){
			new ToolsDlgDokumentation("",dokubut[1].getLocationOnScreen());
		}
	}
	private void doScanEdit(Point pt){
		final Point ptx = pt;
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				ScannerUtil su = new ScannerUtil(new Point(ptx.x,ptx.y+32));
				su.setModal(true);
				su.setVisible(true);
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				updateInfoLab();

				return null;
			}
		}.execute();

	}
	private void doHolePhoto(){
		ladeJpeg();
	}
	private void doHoleOO(){
		setCursor(Reha.thisClass.wartenCursor);
		ladeOoDocs();
		setCursor(Reha.thisClass.normalCursor);
	}
	private void ladeOoDocs(){
		String[] bild = oeffneBild(new String[] {"odt","ods","???"},false);
		if(bild.length > 0){
			String bildpfad = bild[1].replaceAll("\\\\", "/");
			if( (bildpfad.toLowerCase().endsWith(".odt")) || 
					(bildpfad.toLowerCase().endsWith(".ods")) ){
				try {
					Reha.thisClass.patpanel.dokumentation.setCursor(Reha.thisClass.wartenCursor);
					FileTools.copyFile(new File(bildpfad), new File(SystemConfig.hmVerzeichnisse.get("Temp")+"/"+bild[0]), 4096*4, true);
					File f = new File(SystemConfig.hmVerzeichnisse.get("Temp")+"/"+bild[0]);
					if(f.exists()){
						int dokuid = SqlInfo.erzeugeNummer("doku");
						int pat_int = Integer.valueOf(Reha.thisClass.patpanel.aktPatID); //Integer.valueOf(annika.getText().trim());

							
							speichernOoDocs(
									dokuid,
									pat_int,
									SystemConfig.hmVerzeichnisse.get("Temp")+"/"+bild[0],
									(bildpfad.toLowerCase().endsWith(".odt") ? 1 : 2),
									new String[] {DatFunk.sDatInSQL(DatFunk.sHeute()),bild[0],Reha.aktUser,""},
									true);

							this.holeDokus(Reha.thisClass.patpanel.aktPatID,Integer.toString(dokuid));
							setCursor(Reha.thisClass.normalCursor);
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}else{
				JOptionPane.showMessageDialog(null,"Es werden ausschliesslich OpenOffice Writer und Calc Dateien unterstützt");
				return;
			}
		}
	}

	private void ladeJpeg(){
		String[] bild = oeffneBild(new String[] {"jpg","xxx","xxx"},true);
		if(bild.length > 0){
			//System.out.println(bild[0]);
			//System.out.println(bild[1].replaceAll("\\\\", "/"));
			String bildpfad = bild[1].replaceAll("\\\\", "/");
			if(! bildpfad.toLowerCase().endsWith(".jpg")){
				JOptionPane.showMessageDialog(null,"Es werden ausschliesslich JPEG Bilder unterstützt");
				return;
			}
			if(aktPanel.equals("leerPanel")){
				this.setzeDokuPanelAufNull(false);
			}

			//BufferedImage img = null;
			try {
				setCursor(Reha.thisClass.wartenCursor);
				commonName = Long.toString(System.currentTimeMillis()); 
				String fname = "scan"+commonName+".jpg";
				Image img2 = null;
				/*
				img = ImageIO.read(new File( bildpfad));
				img.flush();
				Image img2 = null;
				img2 = img.getScaledInstance(50, 65, Image.SCALE_FAST);
				img.flush();
				img = null;
				*/
				try {
					img2 = Toolkit.getDefaultToolkit()
							.getImage(bildpfad).getScaledInstance(50, 65, Image.SCALE_FAST);
					MediaTracker mediaTracker = new MediaTracker(this);
					mediaTracker.addImage(img2, 0);
					mediaTracker.waitForID(0);
				} catch (Exception ie) {
				}
				

				FileTools.copyFile(new File(bildpfad), new File(SystemConfig.hmVerzeichnisse.get("Temp")+"/"+fname), 4096*5, false);

		        //final String pfad = bild[1];
		        /*new Thread(){
		        	public void run(){
		        	*/
						quelle = "bildgeladen";
		        		//vecBilderAktion.add("jpggeladen");
		        		aktion = "bildgeladen";
		        		zeigeBilder(img2,SystemConfig.hmVerzeichnisse.get("Temp")+"/"+fname,commonName);
		        	/*	
			}
		        }.start();
		        */
		       img2 = null; 		
		       Runtime r = Runtime.getRuntime();
        	    r.gc();
        	    r.freeMemory();
        	    //System.out.println("Freier Speicher "+freeMem);
        	    setCursor(Reha.thisClass.normalCursor);
		        
			} catch (IOException e) {
				// TODO Auto-generated catch block
				setCursor(Reha.thisClass.normalCursor);
				e.printStackTrace();
			} catch (OutOfMemoryError ome) {
				setCursor(Reha.thisClass.normalCursor);
				String  cmd = "Die Bilder sind (zusammen) zu groß für Arbeitsspeicher.\nSpeichern Sie jedes Bild einzeln als Dokumentation";
				JOptionPane.showMessageDialog(null, cmd);
				//System.err.println("Bild zu gro� f�r Arbeitsspeicher.\nSpeichern Sie nur dieses Bild in einer eigenen Dokumentation");
			}
		}	
	}
	private String[] oeffneBild(String[] pattern,boolean mitVorschau){
		String[] sret = {};
		JFileChooser chooser = new JFileChooser("Verzeichnis wählen");
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        if(mitVorschau){
        	chooser.setAccessory(new MyAccessory(chooser));
        }
        //
        final String[] xpattern = pattern;
        chooser.addChoosableFileFilter(new FileFilter() {
            public boolean accept(File f) {
              if (f.isDirectory()) return true;
              if(f.getName().toLowerCase().endsWith(xpattern[0]) || f.getName().toLowerCase().endsWith(xpattern[1])||
            		  f.getName().toLowerCase().endsWith(xpattern[2]) ){
            	  return true;
              }else{
            	  return false;
              }
              //return f.getName().toLowerCase().endsWith(xpattern);
            }
            public String getDescription () { return ""; }  
          });


        File file = null;
        if(lastPath==null){
        	file = new File(Reha.proghome);	
        }else{
        	file = new File(lastPath);
        }

        chooser.setCurrentDirectory(file);
        PropertyChangeListener pl = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)
                        || e.getPropertyName().equals(JFileChooser.DIRECTORY_CHANGED_PROPERTY)) {
                    @SuppressWarnings("unused")
					final File f = (File) e.getNewValue();
                }
            }
        }; 
        chooser.addPropertyChangeListener(pl);
        chooser.setVisible(true);
        setCursor(Reha.thisClass.normalCursor);
        final int result = chooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File inputVerzFile = chooser.getSelectedFile();
            String inputVerzStr = inputVerzFile.getPath();
            

            if(inputVerzFile.getName().trim().equals("")){
            	sret = new String[] {};
            }else{
            	Reha.thisClass.patpanel.dokumentation.setCursor(Reha.thisClass.wartenCursor);
            	sret = new String[] {inputVerzFile.getName().trim(),inputVerzStr};	
            	lastPath = inputVerzFile.getAbsolutePath();
            }
        }else{
        	sret = new String[] {}; //vorlagenname.setText(SystemConfig.oTerminListe.NameTemplate);
        }
        chooser.setVisible(false); 
        chooser.removePropertyChangeListener(pl);
        chooser.removeAll();
        chooser = null;
        pl = null;
        return sret;
		
	}
	private void doDokudelete(){
		if(this.aktivesBild <= 0){
			JOptionPane.showMessageDialog(null, "Es wurde keine Seite zum Löschen ausgewählt");	
			return;
		}
		if(vecBilderPfad.size()==1){
			loescheBilderPan();
			return;
		}
		bilderPan.remove(Labels.get(aktivesBild-1));
		vecBilderPfad.removeElementAt(aktivesBild-1);
		vecBilderPfad.trimToSize();
		vecPdfPfad.removeElementAt(aktivesBild-1);
		vecPdfPfad.trimToSize();

		vecBilderFormat.removeElementAt(aktivesBild-1);
		vecBilderFormat.trimToSize();
		vecBilderAktion.removeElementAt(aktivesBild-1);
		vecBilderAktion.trimToSize();
		Labels.removeElementAt(aktivesBild-1);
		Labels.trimToSize();
		aktivesBild = 0;
		for(int i = 0;i < Labels.size();i++){
			Labels.get(i).setText("Seite-"+(i+1));
			Labels.get(i).setName("Label-"+(i+1));
		}
		bilderPan.validate();
		bilderPan.repaint();
		
	}
	private Rectangle getLowagieForm(String format){
		Rectangle[] rec = {PageSize.A6,PageSize.A6.rotate(),
				PageSize.A5,PageSize.A5.rotate(),PageSize.A4,PageSize.A4.rotate(),null};
		String[] forms ={"Din A6","Din A6-quer","Din A5","Din A5-quer","Din A4","Din A4-quer","angepasst"};
		for(int i = 0;i< forms.length;i++){
			if(forms[i].equals(format)){
				return rec[i];
			}
		}
		return rec[0];
	}
	
	/******************************************/
	private void doDokusave(String dokuTitel){
		dokubut[0].setEnabled(false);
		dokubut[1].setEnabled(false);
		setCursor(Reha.thisClass.wartenCursor);
		Document document = null;
		FileOutputStream fout = null;
		for(int i = 0;i<vecBilderPfad.size();i++){
			//rehaSplash.setNewText("Erstelle Dokuseite "+(i+1));
			//System.out.println("Sende Seitengröße an Funktion "+vecBilderFormat.get(i));
			Rectangle format = null;
			try {
			com.lowagie.text.Image jpg2 = com.lowagie.text.Image.getInstance(vecBilderPfad.get(i));
			if(((String)vecBilderAktion.get(i)).equals("scanner")){
				format = getLowagieForm(vecBilderFormat.get(i));				
			}else if(((String)vecBilderAktion.get(i)).equals("bildgeladen")){
				if(jpg2.getPlainWidth() > jpg2.getPlainHeight()){
					format = new Rectangle(PageSize.A4.rotate());
				}else{
					format = new Rectangle(PageSize.A4);
				}
			}

			if(format==null){
				format = new Rectangle(jpg2.getPlainWidth(),jpg2.getPlainHeight()); 
			}
			//System.out.println("Das Format = "+format);
			document = new Document();
			document.setPageSize(format);	
			document.setMargins(0.0f, 0.0f, 0.0f, 0.0f);
				fout = new  FileOutputStream(SystemConfig.hmVerzeichnisse.get("Temp")+"/pdfDokuSeite"+(i+1)+".pdf");
				PdfWriter writer = PdfWriter.getInstance(document, fout);
			document.open(); 

			if(((String)vecBilderAktion.get(i)).equals("scanner")){
				jpg2.scaleAbsoluteHeight(document.getPageSize().getHeight());
				jpg2.scaleAbsoluteWidth(document.getPageSize().getWidth());
				document.add(jpg2);
			}else if(((String)vecBilderAktion.get(i)).equals("bildgeladen")){
				jpg2.scaleAbsoluteHeight(document.getPageSize().getHeight());
				jpg2.scaleAbsoluteWidth(document.getPageSize().getWidth());						
				document.add(jpg2);
			}
			

			document.close();
			writer.close();
			fout.flush();
			fout.close();
			Thread.sleep(100);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		
		PdfReader reader = null;
		PdfCopy copy = null;
		for(int i = 0;i<vecBilderPfad.size();i++){
			//rehaSplash.setNewText("Seiten zusammenf�hren - Seite"+(i+1)+" von "+vecBilderPfad.size());
			//Rectangle format = getLowagieForm(vecBilderFormat.get(i)); // vorher eingeschaltet....
			//System.out.println("Das Format = "+format);
			
			try {
				reader = new PdfReader(SystemConfig.hmVerzeichnisse.get("Temp")+"/pdfDokuSeite"+(i+1)+".pdf");
				if(i==0){
					document = new Document(reader.getPageSizeWithRotation(1));
					copy = new PdfCopy(document,
							new FileOutputStream(SystemConfig.hmVerzeichnisse.get("Temp")+"/FertigeDoku.pdf"));
					document.open();
				}
				copy.addPage(copy.getImportedPage(reader,1));
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		document.close();
		copy.close();
		document = null;

		
		
		  /*
		   * 
		    ps.setBytes(1,   //dokuid - integer
		    ps.setBytes(2,   //datum - date
		    ps.setBytes(3,   //dokutitel - longtext			    
		    ps.setBytes(4,   //benutzer - zeichen
			ps.setBytes(5,   //pat_intern - integer			    			    
			ps.setBytes(6,   //format - integer
			ps.setBytes(7,   //dokutext - longtext								
			ps.setBytes(8,   //dokublob - longblog /bin�r	
			public static void doSpeichernDoku(
			int dokuid,
			int pat_intern, 
			String dateiname,
			int format,
			Vector<String> vec,
			boolean neu){			
		   */
		//System.out.println("Beginne speichern");
		//rehaSplash.setNewText("Dokumentation auf Server transferieren");
		int dokuid = SqlInfo.erzeugeNummer("doku");
		int pat_int = Integer.valueOf(Reha.thisClass.patpanel.aktPatID); //Integer.valueOf(annika.getText().trim());
		try {
			
			doSpeichernDoku(
					dokuid,
					pat_int,
					SystemConfig.hmVerzeichnisse.get("Temp")+"/FertigeDoku.pdf",
					0,
					new String[] {DatFunk.sDatInSQL(DatFunk.sHeute()),dokuTitel,Reha.aktUser,""},
					true);

			loescheBilderPan();
			dokubut[0].setEnabled(true);
			dokubut[1].setEnabled(true);
			this.holeDokus(Reha.thisClass.patpanel.aktPatID,Integer.toString(dokuid));
			setCursor(Reha.thisClass.normalCursor);
			JOptionPane.showMessageDialog(null,"Dokumentation wurde gespeichert für Patient-Nr.: "+pat_int);
			//annika.setText("");
			//annika.requestFocus();

		} catch (Exception e) {
			loescheBilderPan();
			dokubut[0].setEnabled(true);
			dokubut[1].setEnabled(true);
			//this.holeDokus(Reha.thisClass.patpanel.aktPatID,Integer.valueOf(dokuid).toString());
			setCursor(Reha.thisClass.normalCursor);
			JOptionPane.showMessageDialog(null,"Fehler beim Speichern der Dokumentation \nDoku wurde nicht gespeichert");
			//annika.setText("");
			//annika.requestFocus();
			e.printStackTrace();
		}
		////System.out.println("Fertig mit speichern");
	}
	private void loescheBilderPan(){
		//System.out.println(FileTools.delFileWithSuffixAndPraefix(new File(SystemConfig.hmVerzeichnisse.get("Temp")), "scan",".jpg"));
		//System.out.println(FileTools.delFileWithSuffixAndPraefix(new File(SystemConfig.hmVerzeichnisse.get("Temp")), "pdf",".pdf"));
		for(int i = 0 ; i < Labels.size(); i++){
			bilderPan.remove(Labels.get(i));
			Labels.get(i).setVisible(false);
		}
		bilderPan.remove(plusminus);
		plusminus.setVisible(false);
		plusminus = null;
		Labels.clear();
		vecBilderPfad.clear();
		vecPdfPfad.clear();
		vecBilderFormat.clear();
		vecBilderAktion.clear();
		bildnummer = 0;
		if(scanner != null){
			scanner.removeListener(this);
			scanner = null;

		}
		for(int i = 0 ; i < Labels.size(); i++){
			Labels.set(i,null);
		}
		dokubut[2].setEnabled(false);
		bilderPan.validate();
		bilderPan.repaint();
		aktivesBild=0;
		
		if(this.dtblm.getRowCount()==0){
			this.setzeDokuPanelAufNull(true);
		}
		
	}
	private void updateInfoLab(){
		try {
			Thread.sleep(50);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(!infolab[0].getText().equals(SystemConfig.sDokuScanner)){
			//System.out.println("Dokuscanner = "+SystemConfig.sDokuScanner);
			try {
				if(scanner==null){
					scanStarten();
				}else{
					scanner.select(SystemConfig.sDokuScanner);					
				}

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
	/*
	void setRezeptDaten(){
		int row = this.tabdokus.getSelectedRow();
		if(row >= 0){
			final int xrow = row;
			SwingUtilities.invokeLater(new Runnable(){
				public  void run(){
					String reznr = (String)tabdokus.getValueAt(xrow,0);
					String id = (String)tabdokus.getValueAt(xrow,6);
					//jpan1.setRezeptDaten(reznr,id);
					//System.out.println("Aus Bericht....."+reznr+"....."+id);
				}
			});	

		}
	}
	*/	
	/**************************************************
	 * 
	 * 
	 */
	public void scanStarten(){
		if((!scanaktiv) || 
				SystemConfig.sDokuScanner.equals("") ){
			//System.out.println("Scanner = null");
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
		List<String> list = Arrays.asList( new String[] {"Din A6","Din A6-quer","Din A5","Din A5-quer","Din A4","Din A4-quer","angepasst"});
		//String[] dims = new String[] {"Din A6","Din A6-quer","Din A5","Din A5-quer","Din A4","Din A4-quer"};
		Double[][] d =  new Double[][]  {{4.23,5.82},{4.23,5.82},{5.82,8.26},{5.82,8.26},{8.26,11.69},{8.26,11.69},{8.26,11.69}};
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
    				int dpi= Integer.valueOf(SystemConfig.hmDokuScanner.get("aufloesung"));
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
    				setCursor(Reha.thisClass.normalCursor);
    			}catch(Exception e){
    	            //System.out.println("3\b"+getClass().getName()+".update:\n\tCannot retrieve image information.\n\t"+e);
    			}
    		}
    		/*****************************************************/      		
		}else if ( ScannerIOMetadata.STATECHANGE.equals(type)){
    		if(metadata.isFinished()){
    			if(metadata.getImage() != null){
    				metadata.setImage(null);
	                Runtime r = Runtime.getRuntime();
	        	    r.gc();
	        	    r.freeMemory();
	        	    //System.out.println("Freier Speicher "+freeMem);
      	    	  	//System.out.println("finished und nicht null = "+metadata.getStateStr());
    			}else{
      	    	  	//System.out.println("finished aber null = "+metadata.getStateStr());
	                Runtime r = Runtime.getRuntime();
	        	    r.gc();
	        	    r.freeMemory();
    			}
    	        //System.out.println("Scanvorgang wurde beendet");
				setCursor(Reha.thisClass.normalCursor);
    	      }else{
    	    	  //System.out.println("nicht finished = "+metadata.getStateStr());	  
    	      }
    		
    		/*****************************************************/    		
		}else if ( type.equals(ScannerIOMetadata.EXCEPTION)){
			//System.out.println("Exception in EXEPTION");
    	    if(!metadata.getException().getMessage().contains("Failed during call to twain source")){
        	    JOptionPane.showMessageDialog(null,"Bezug des Scans fehlgeschlagen.\nVersuchen Sie es mit einer niedrigeren Auflösung\n\n"+
	    		"Ideale Auflösung für Dokumentation: 150dpi");
    	    }
			
    	    scanner.removeListener(this);
    	    scanner = null;
            Runtime r = Runtime.getRuntime();
    	    r.gc();
    	    setCursor(Reha.thisClass.normalCursor);
    		/*****************************************************/    	    
		}else if ( ScannerIOMetadata.ACQUIRED.equals( type )){
			//System.out.println("ACUIRED");
			//System.out.println(metadata.getStateStr());
			if(metadata.getStateStr().contains("Transferring Data")){
				setCursor(Reha.thisClass.wartenCursor);
				if(metadata.getImage() != null){
					commonName = Long.toString(System.currentTimeMillis()); 
					String fname = "scan"+commonName+".jpg";
					quelle = "scanner";
			        File file = new File(SystemConfig.hmVerzeichnisse.get("Temp"),fname);
			        try {
						ImageIO.write( metadata.getImage(),
								"jpg", 
								file ) ;
						
						//System.out.println("Fertig mit Image schreiben");
				        final Image img = metadata.getImage().getScaledInstance(50, 65,Image.SCALE_SMOOTH);
				        final String pfad = file.getAbsolutePath();
				        new Thread(){
				        	public void run(){
				        		zeigeBilder(img,pfad,commonName);		
				        	}
				        }.start();
					} catch (IOException e) {
						//System.out.println("Exception in Statechange - ACOUIRED");
						e.printStackTrace();
					}
				}else{
					//System.out.println("ImageDate = null");
				}
				setCursor(Reha.thisClass.normalCursor);
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
		img = null;

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
		vecBilderFormat.add(infolabLeer[3].getText());
		vecBilderAktion.add(quelle);
		String pdfPfad = "pdf"+commonname+".pdf";
		vecPdfPfad.add(pdfPfad);
		if(vecBilderPfad.size()==1){
			bilderPan.add(setzePlusMinus());
			dokubut[2].setEnabled(true);
			pmbut[0].setEnabled(true);
			pmbut[2].setEnabled(false);
			pmbut[3].setEnabled(false);
			bilderPan.validate();
		}else if(vecBilderPfad.size() >1 ){
			pmbut[0].setEnabled(true);
			pmbut[2].setEnabled(false);
			pmbut[3].setEnabled(false);
		}
		
		bilderPan.add(lab);
		bilderPan.validate();
	}
	/*********************
	 * 
	 * 
	 * 
	 */
	public void pdfZeigen(int seite){

			  setCursor(Reha.thisClass.wartenCursor);
/*****************/
				Document document = null;
					String datname = "";
					//System.out.println("Sende Seitengröße an Funktion "+vecBilderFormat.get(seite));
					//Rectangle format = PageSize.A4.rotate();
					Rectangle format = null;
					if(((String)vecBilderAktion.get(seite)).equals("scanner")){
						format = getLowagieForm(vecBilderFormat.get(seite));
					}else if(((String)vecBilderAktion.get(seite)).equals("bildgeladen")){
						
					}
					
					try {
					com.lowagie.text.Image jpg2 = com.lowagie.text.Image.getInstance(vecBilderPfad.get(seite));
					if(((String)vecBilderAktion.get(seite)).equals("scanner")){
						format = getLowagieForm(vecBilderFormat.get(seite));
					}else if(((String)vecBilderAktion.get(seite)).equals("bildgeladen")){
						if(jpg2.getPlainWidth() > jpg2.getPlainHeight()){
							format = new Rectangle(PageSize.A4.rotate());
						}else{
							format = new Rectangle(PageSize.A4);
						}
						//format = new Rectangle(jpg2.getPlainWidth(),jpg2.getPlainHeight());						
					}
					
					
					
					
					//System.out.println("Das Format = "+format);
					document = new Document();
					document.setPageSize(format);	
					document.setMargins(0.0f, 0.0f, 0.0f, 0.0f);
					datname = SystemConfig.hmVerzeichnisse.get("Temp")+"/"+vecPdfPfad.get(seite);
					FileOutputStream fout = new FileOutputStream(datname);
					PdfWriter writer = PdfWriter.getInstance(document, fout);  
					document.open(); 
					//System.out.println("Die aktion = "+((String)vecBilderAktion.get(seite)) );
					if(((String)vecBilderAktion.get(seite)).equals("scanner")){
						jpg2.scaleAbsoluteHeight(document.getPageSize().getHeight());
						jpg2.scaleAbsoluteWidth(document.getPageSize().getWidth());
						document.add(jpg2);
					}else if(((String)vecBilderAktion.get(seite)).equals("bildgeladen")){

						//document.setPageSize(new Rectangle(jpg2.getScaledWidth(),jpg2.getScaledHeight()));
						Thread.sleep(20);
						//writer.setPageSize(document.getPageSize());

						//jpg2.scalePercent(0.25f);
						jpg2.scaleAbsoluteHeight(document.getPageSize().getHeight());
						jpg2.scaleAbsoluteWidth(document.getPageSize().getWidth());						
						document.add(jpg2);
					}
					

					document.close();
					writer.close();
					fout.flush();
					fout.close();
					
					Thread.sleep(100);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (DocumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
			  
				/***************************/				
				
				File file = new File(SystemConfig.hmFremdProgs.get("AcrobatReader"));
				if(!file.exists()){
					JOptionPane.showMessageDialog(null, "Der Pfad zu Ihrem Adobe-Acrobatreader ist nicht korrekt konfiguriert");
					return;
				}
				//final String xdatname = datname;
				/*
				new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() throws Exception {
						Process process = new ProcessBuilder(SystemConfig.hmFremdProgs.get("AcrobatReader"),"",xdatname).start();
					       InputStream is = process.getInputStream();
					       InputStreamReader isr = new InputStreamReader(is);
					       BufferedReader br = new BufferedReader(isr);
					       String line;
					       while ((line = br.readLine()) != null) {
					         //System.out.println(line);
					       }
					       is.close();
					       isr.close();
					       br.close();

						return null;
					}
					
				}.execute();
				*/
		        
				setCursor(Reha.thisClass.normalCursor);
			
			
		
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
		pmbut[2].setEnabled(false);
		jtbpm.add(pmbut[2]);

		pmbut[3] = new JButton();
		pmbut[3].setIcon(SystemConfig.hmSysIcons.get("rechts"));
		pmbut[3].setToolTipText("Seiten nach rechts verschieben");
		pmbut[3].setActionCommand("Dokurechts");
		pmbut[3].addActionListener(this);
		pmbut[3].setEnabled(false);		
		jtbpm.add(pmbut[3]);
		plusminus.add(jtbpm,cc.xy(1,2));
		
		return plusminus;
	}
	
	public void setzeListener(){
		mlist = new MouseListener(){

			@Override
			public void mousePressed(MouseEvent arg0) {
				if(arg0.getClickCount()==2){
					//System.out.println("LabeName = "+((JComponent)arg0.getSource()).getName());
					int seite = Integer.valueOf( ((JComponent)arg0.getSource()).getName().split("-")[1] );
					File file = null;
					String datei = "";
					String grafik = SystemConfig.hmFremdProgs.get("GrafikProg").trim();
					if(((String)vecBilderAktion.get(seite-1)).equals("scanner") || grafik.equals("")){
						pdfZeigen(seite-1);
						file = new File(SystemConfig.hmFremdProgs.get("AcrobatReader"));
						if(!file.exists()){
							JOptionPane.showMessageDialog(null, "Der Pfad zu Ihrem Adobe-Acrobatreader ist nicht korrekt konfiguriert");
							return;
						}
						datei = SystemConfig.hmVerzeichnisse.get("Temp")+"/"+vecPdfPfad.get(aktivesBild-1);
						
					}else if(((String)vecBilderAktion.get(seite-1)).equals("bildgeladen")){
						file = new File(SystemConfig.hmFremdProgs.get("GrafikProg"));
						if(!file.exists()){
							JOptionPane.showMessageDialog(null, "Der Pfad zu Ihrem Bildbearbeitungsprogramm ist nicht korrekt konfiguriert");
							return;
						}
						datei = ((String)vecBilderPfad.get(aktivesBild-1));
					}

					try {
						
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
						
						//System.out.println("Starte :"+file.getAbsolutePath()+" "+datei);
						//Runtime.getRuntime().exec(datei.trim());
						Runtime.getRuntime().exec(file.getAbsolutePath().toString()+" "+datei.trim());
			        } catch(IOException e) {
			            e.printStackTrace();
			        }
					
				}else if(arg0.getClickCount()==1){
					if(aktivesBild > 0){
						int bild = Integer.valueOf( Labels.get(aktivesBild-1).getText().split("-")[1]); 
						Labels.get(bild-1).setBorder(BorderFactory.createLineBorder(Color.BLACK));
					}
					aktivesBild = Integer.valueOf( ((JLabel)((JComponent)arg0.getSource())).getText().split("-")[1] );
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
	private String macheHtmlTitel(int anz,String titel){
		
		String ret = titel+" - "+Integer.toString(anz);
		
		/*
		String ret = "<html>"+titel+
		(anz > 0 ? " - <font color='#ff0000'>"+Integer.valueOf(anz).toString()+"<font></html>" : " - <font color='#000000'>"+Integer.valueOf(anz).toString()+"</font>");
		*/
		return ret;
	}
	public void holeDokus(String patint,String doku){
		final String xpatint = patint;


		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				try{
		//{"Doku-Id","Doku-Art","Titel","erfa�t am","von","",""};
				//String sstmt = "select * from verordn where PAT_INTERN ='"+xpatint+"' ORDER BY REZ_DATUM";
				Vector<Vector<String>> vec = SqlInfo.holeSaetze("doku1", 
						"dokuid,format,dokutitel,DATE_FORMAT(datum,'%d.%m.%Y') AS dokudatum," +
						"benutzer,pat_intern,id,datei", 
						"pat_intern='"+xpatint+"' ORDER BY dokuid DESC", Arrays.asList(new String[]{}));

				int anz = vec.size();

				for(int i = 0; i < anz;i++){
					if(i==0){
						dtblm.setRowCount(0);						
					}

					int zzbild = 0;
					if( ((Vector<?>)vec.get(i)).get(1) == null){
						zzbild = 0;
					}else if(!((Vector<?>)vec.get(i)).get(1).equals("")){
						zzbild = Integer.valueOf((String) ((Vector<?>)vec.get(i)).get(1) );
					}
					dtblm.addRow((Vector<?>)vec.get(i));
					dtblm.setValueAt(tabIcons[zzbild], i, 1);
					//dtblm.setValueAt(Reha.thisClass.patpanel.imgzuzahl[zzbild], i, 1);
				}
				Reha.thisClass.patpanel.getTab().setTitleAt(3,macheHtmlTitel(anz,"Dokumentation"));
				if(anz > 0){
					setzeDokuPanelAufNull(false);
					//int anzeigen = -1;
					anzahlDokus.setText("Anzahl gespeicherter Dokumentationen: "+anz);	
					tabdokus.setRowSelectionInterval(0, 0);
					wechselPanel.revalidate();
					wechselPanel.repaint();	
					if((!dokubut[0].isEnabled()) && SystemConfig.hmDokuScanner.get("aktivieren").trim().equals("1")){
						dokubut[0].setEnabled(true);						
					}
					dokubut[5].setEnabled(true);
					dokubut[1].setEnabled(true);
					/*
					dokubut[3].setEnabled(true);	
					dokubut[4].setEnabled(true);
					dokubut[5].setEnabled(true);
					*/

				}else{
					setzeDokuPanelAufNull(true);
					anzahlDokus.setText("Anzahl gespeicherter Dokumentationen: 0");
					wechselPanel.revalidate();
					wechselPanel.repaint();
					dtblm.setRowCount(0);
					if((!dokubut[0].isEnabled()) && SystemConfig.hmDokuScanner.get("aktivieren").trim().equals("1")){
						dokubut[0].setEnabled(true);						
					}
					dokubut[1].setEnabled(true);
					dokubut[5].setEnabled(false);
					/*
					dokubut[3].setEnabled(true);	
					dokubut[4].setEnabled(true);
					dokubut[5].setEnabled(false);
					*/

				}
				
				}catch(Exception ex){
					ex.printStackTrace();
				}
				return null;
			}
		
		}.execute();
		
	}
	
	
	/*************************************************/
	class DokuListSelectionHandler implements ListSelectionListener {

	    public void valueChanged(ListSelectionEvent e) {
	    	/*
			if(rezneugefunden){
				rezneugefunden = false;
				return;
			}
			*/
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
	                	//final int ix = i;
	                	
	                	new SwingWorker<Void,Void>(){

							@Override
							protected Void doInBackground() throws Exception {
						
								// TODO Auto-generated method stub
								inDokuDaten = true;
	                			setCursor(Reha.thisClass.wartenCursor);
	                    		//holeEinzelTermine(ix,null);
	    						//jpan1.setRezeptDaten((String)tabhistorie.getValueAt(ix, 0),(String)tabhistorie.getValueAt(ix, 6));
	    						////System.out.println("rezeptdaten akutalisieren in ListSelectionHandler");
	    						setCursor(Reha.thisClass.normalCursor);
	    						inDokuDaten = false;

	    						return null;
							}
	                		
	                	}.execute();

	                    break;
	                }
	            }
	        }
	        ////System.out.println(output.toString());
	    }
	}


	public static void doSpeichernDoku(int dokuid,int pat_intern, String dateiname,int format,String[] str,boolean neu) throws Exception{
		Statement stmt = null;;
		ResultSet rs = null;
		PreparedStatement ps = null;
		//boolean ret = false;
		//int bilder = 0;
		//FileInputStream fis = null;
		//piTool.app.conn.setAutoCommit(true);
		try {
			stmt = (Statement) Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE );
			
			String select = "Insert into doku1 set dokuid = ? , datum = ?, dokutitel = ?,"+
			"benutzer = ?, pat_intern = ?, format = ?,"+
			"dokutext = ?, dokublob = ? , groesse = ? ";
			  ps = (PreparedStatement) Reha.thisClass.conn.prepareStatement(select);
			  /*
			   * 
			    ps.setBytes(1,   //dokuid - integer
			    ps.setBytes(2,   //datum - date
			    ps.setBytes(3,   //dokutitel - longtext			    
			    ps.setBytes(4,   //benutzer - zeichen
				ps.setBytes(5,   //pat_intern - integer			    			    
				ps.setBytes(6,   //format - integer
				ps.setBytes(7,   //dokutext - longtext								
				ps.setBytes(8,   //dokublob - longblog /bin�r
				new String[] {datFunk.sDatInSQL(datFunk.sHeute()),"Eingescannte Papierdokumentation",Reha.aktUser,""},				
			   */
			  //System.out.println("Setze InputStream "+dateiname);
			  ps.setInt(1, dokuid);
			  ps.setString(2, str[0]);			  
			  ps.setString(3, str[1]);
			  ps.setString(4, str[2]);			  
			  ps.setInt(5, pat_intern);
			  ps.setInt(6, format);			  
			  ps.setString(7, str[3]);
			  File f = new File(dateiname);
			  byte[] b = FileTools.File2ByteArray(f);
			  ps.setBytes(8,b);
			  ps.setInt(9, (int)b.length);
			  ps.execute();

			} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			  }	

			   


			  
	  
							  
		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) { // ignore }
					rs = null;
				}
			}	
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) { // ignore }
					stmt = null;
				}
			}
			if(ps != null){
				ps.close();
			}
		}
		
	}
	public void	oooDokuNeu(int art){
		String[] welcheArt = {"OpenOffice-Writer","OpenOffice-Calc"};
		String value = (String)JOptionPane.showInputDialog(null,
				"Bitte einen Titel für die Dokumentation eingeben\n\n", "Benutzereingabe erforderlich....", JOptionPane.PLAIN_MESSAGE, null,
				null, "Neue "+welcheArt[art]+" Dokumentation");
		if( (value == null) || (value.length()==0)){
			JOptionPane.showMessageDialog(null, "Kein Titel - kein speichern. Ganz einfach!!");
			return;
		}
		String dest = null;
		if(art==0){
			String src = Reha.proghome+"vorlagen/"+Reha.aktIK+"/EmptyWriterDoku.ott";
			dest = Reha.proghome+"temp/"+Reha.aktIK+"/"+value+".odt";

			try {
				FileTools.copyFile(new File(src), new File(dest), 8192,true);
				ITextDocument itext = new OOTools().starteWriterMitDatei(dest);
				itext.addDocumentListener(new OoListener(Reha.officeapplication,dest,"",this));
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Fehler kann neues WriterDokument nicht erzeugen");
			}
		}else{
			String src = Reha.proghome+"vorlagen/"+Reha.aktIK+"/EmptyCalcDoku.ots";
			dest = Reha.proghome+"temp/"+Reha.aktIK+"/"+value+".ods";
			
			try {
				FileTools.copyFile(new File(src), new File(dest), 8192,true);
				ISpreadsheetDocument ispread = new OOTools().starteCalcMitDatei(dest);
				ispread.addDocumentListener(new OoListener(Reha.officeapplication,dest,"",this));

			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Fehler kann neues CalcDokument nicht erzeugen");
			}
		}
		
	}
/**************************************************/
	private void pdfSpeichernDoku(){
		int dokuid = -1;
		String[] doku = oeffneBild(new String[] {"pdf","????","???"},false);
		if(doku.length==0){setCursor(Reha.thisClass.normalCursor);return;}
		if(! doku[0].toLowerCase().endsWith(".pdf")){
			setCursor(Reha.thisClass.normalCursor);
			JOptionPane.showMessageDialog(null,"Diese Funktion ist nur für PDF-Dateien geeignet");
			return;
		}
		doku[1] = doku[1].replaceAll("\\\\", "/");
		try {
			doSpeichernDoku( (dokuid=SqlInfo.erzeugeNummer("doku")),
					Integer.valueOf(Reha.thisClass.patpanel.aktPatID), 
					doku[1],
					0,
					new String[] {DatFunk.sDatInSQL(DatFunk.sHeute()),doku[0],Reha.aktUser,""},
					true);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.holeDokus(Reha.thisClass.patpanel.aktPatID,Integer.toString(dokuid));
		setCursor(Reha.thisClass.normalCursor);
	}
/**************************************************/
	public void speichernOoDocs(int dokuid,int pat_intern, String dateiname,int format,String[] str,boolean neu) throws Exception{
		Statement stmt = null;;
		ResultSet rs = null;
		PreparedStatement ps = null;
		//boolean ret = false;
		//int bilder = 0;
		//FileInputStream fis = null;
		//piTool.app.conn.setAutoCommit(true);
		try {
			stmt = (Statement) Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE );
			//Dokumentation.speichernOoDocs(Integer.valueOf(id), -1, file, -1, null, false);
			if(neu){
			String select = "Insert into doku1 set dokuid = ? , datum = ?, dokutitel = ?,"+
			"benutzer = ?, pat_intern = ?, format = ?,"+
			"dokutext = ?, dokublob = ? , groesse = ? , datei = ?";
			  ps = (PreparedStatement) Reha.thisClass.conn.prepareStatement(select);
			  /*
			   * 
			    ps.setBytes(1,   //dokuid - integer
			    ps.setBytes(2,   //datum - date
			    ps.setBytes(3,   //dokutitel - longtext			    
			    ps.setBytes(4,   //benutzer - zeichen
				ps.setBytes(5,   //pat_intern - integer			    			    
				ps.setBytes(6,   //format - integer
				ps.setBytes(7,   //dokutext - longtext								
				ps.setBytes(8,   //dokublob - longblog /bin�r
				ps.setBytes(9,   //groesse - longtext								
				ps.setBytes(10,   //datei - longblog /bin�r
				
				new String[] {datFunk.sDatInSQL(datFunk.sHeute()),"Eingescannte Papierdokumentation",Reha.aktUser,""},				
			   */
			  //System.out.println("Setze InputStream "+dateiname);
			  ps.setInt(1, dokuid);
			  ps.setString(2, DatFunk.sDatInSQL(DatFunk.sHeute()));			  
			  ps.setString(3, dateiname.substring(dateiname.replace("\\", "/").lastIndexOf("/")+1));
			  ps.setString(4, Reha.aktUser);			  
			  ps.setInt(5, Integer.parseInt(Reha.thisClass.patpanel.patDaten.get(29).trim()));
			  ps.setInt(6,  (dateiname.endsWith(".odt") ? 1 : 2));			  
			  ps.setString(7, dateiname.substring(dateiname.replace("\\", "/").lastIndexOf("/")+1));
			  File f = new File(dateiname);
			  byte[] b = FileTools.File2ByteArray(f);
			  ps.setBytes(8,b);
			  ps.setInt(9, (int)b.length);
			  ps.setString(10, dateiname.substring(dateiname.replace("\\", "/").lastIndexOf("/")+1));
			  ps.execute();
			  //System.out.println("OOOrg-Doku wurde gespeichert");
			  //String id = SqlInfo.holeEinzelFeld("select id from doku1 where dokuid='"+dokuid+"' LIMIT 1");
			  ////System.out.println("ID der neuen Doku ist "+id);
			  holeDokus(Reha.thisClass.patpanel.patDaten.get(29).trim(),"");
			  
			}else{
				//int dokuid,int pat_intern, String dateiname,int format,String[] str,boolean neu
				//Dokumentation.speichernOoDocs(Integer.valueOf(id), -1, file, -1, null, false);	
				String select = "update doku1 set dokublob = ?, groesse = ?, datum = ? "+
				" where id = ?";
				//System.out.println("Prepared statement: "+select);
				  ps = (PreparedStatement) Reha.thisClass.conn.prepareStatement(select);
				  File f = new File(dateiname);
				  byte[] b = FileTools.File2ByteArray(f);
				  ps.setBytes(1,b);
				  ps.setInt(2, (int)b.length);
				  ps.setString(3, DatFunk.sDatInSQL(DatFunk.sHeute()));
				  ps.setString(4,Integer.valueOf(dokuid).toString());
				  ps.execute();
				  //System.out.println("Fertig mit execute");
				  //System.out.println("Größe des Datenstroms="+b.length+" Bytes");
				  //System.out.println("Datei = "+f.getAbsolutePath());
				  //System.out.println("dokuid = "+Integer.toString(dokuid));
				  //System.out.println("Dateigröße = "+b.length+" Bytes");
				  //System.out.println("Datum = "+DatFunk.sDatInSQL(DatFunk.sHeute()));
				  f.delete();
				  Reha.thisClass.patpanel.dokumentation.setCursor(Reha.thisClass.normalCursor);
			}

			} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			  }	

			   


			  
	  
							  
		finally {
			if (rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (SQLException sqlEx) { // ignore }
					rs = null;
				}
			}	
			if (stmt != null) {
				try {
					stmt.close();
					stmt = null;
				} catch (SQLException sqlEx) { // ignore }
					stmt = null;
				}
			}
			if(ps != null){
				ps.close();
				ps = null;
			}
		}
		
	}
/****************************/

	class ToolsDlgDokumentation{
		public ToolsDlgDokumentation(String command,Point pt){

			Map<Object, ImageIcon> icons = new HashMap<Object, ImageIcon>();
			icons.put("Scanner einstellungen",SystemConfig.hmSysIcons.get("scanner"));
			icons.put("Photo von DigiCam holen",SystemConfig.hmSysIcons.get("camera"));
			icons.put("Office-Dokument aufnehmen", SystemConfig.hmSysIcons.get("openoffice26"));
			icons.put("PDF-Dokument aufnehmen", pdfplus /*SystemConfig.hmSysIcons.get("pdf")*/);
			icons.put("Neue OO-Writer-Doku erstellen",oowriterplus/*SystemConfig.hmSysIcons.get("ooowriter")*/);
			icons.put("Neue OO-Calc-Doku erstellen",oocalcplus/*SystemConfig.hmSysIcons.get("ooocalc")*/);

			// create a list with some test data
			JList list = new JList(	new Object[] {"Scanner einstellungen",
					"Photo von DigiCam holen", 
					"Office-Dokument aufnehmen",
					"PDF-Dokument aufnehmen",
					"Neue OO-Writer-Doku erstellen",
					"Neue OO-Calc-Doku erstellen"});
			list.setCellRenderer(new IconListRenderer(icons));	
			Reha.toolsDlgRueckgabe = -1;
			ToolsDialog tDlg = new ToolsDialog(Reha.thisFrame,"Werkzeuge: Dokumentation",list);
			tDlg.setPreferredSize(new Dimension(220,220+
					((Boolean)SystemConfig.hmPatientenWerkzeugDlgIni.get("ToolsDlgShowButton")? 25 : 0) ));
			tDlg.setLocation(pt.x-70,pt.y+30);
			tDlg.pack();
			tDlg.setModal(true);
			tDlg.activateListener();
			tDlg.setVisible(true);
			switch(Reha.toolsDlgRueckgabe){
			case 0:
				if(!Rechte.hatRecht(Rechte.Doku_scannen, true)){
					return;
				}
				doScanEdit(pt);
				break;
			case 1:
				if(Reha.thisClass.patpanel.aktPatID.equals("")){
					keinAtiverPatient();
					tDlg = null;
					return;
				}
				if(!Rechte.hatRecht(Rechte.Doku_ooorg, true)){
					return;
				}
				doHolePhoto();
				break;
			case 2:
				if(Reha.thisClass.patpanel.aktPatID.equals("")){
					keinAtiverPatient();
					tDlg = null;
					return;
				}
				if(!Rechte.hatRecht(Rechte.Doku_ooorg, true)){
					return;
				}
				doHoleOO();
				break;
			case 4:
				if(Reha.thisClass.patpanel.aktPatID.equals("")){
					keinAtiverPatient();
					tDlg = null;
					return;
				}  // 0 = Writer
				if(!Rechte.hatRecht(Rechte.Doku_ooorg, true)){
					return;
				}
				oooDokuNeu(0);
				break;
			case 5:
				if(Reha.thisClass.patpanel.aktPatID.equals("")){
					keinAtiverPatient();
					tDlg = null;
					return;
				}// 0 = Calc
				if(!Rechte.hatRecht(Rechte.Doku_ooorg, true)){
					return;
				}
				oooDokuNeu(1);
				break;
			case 3:
				if(Reha.thisClass.patpanel.aktPatID.equals("")){
					keinAtiverPatient();
					tDlg = null;
					return;
				}// 0 = Calc
				if(!Rechte.hatRecht(Rechte.Doku_ooorg, true)){
					return;
				}
				pdfSpeichernDoku();
				break;
			}
			tDlg = null;
			////System.out.println("Rückgabewert = "+tDlg.rueckgabe);
		}
		private void keinAtiverPatient(){
			JOptionPane.showMessageDialog(null,"Kein Patient für Dokumentation ausgewählt");
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

	public Class<?> getColumnClass(int columnIndex) {
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

	public Class<?> getColumnClass(int columnIndex) {
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

class OoListener implements IDocumentListener {

	public IOfficeApplication officeAplication = null;
	private String datei;
	private String id;
	private boolean geaendert = false;
	private boolean neu = false;
	public boolean warschoninsave = false;
	private Dokumentation eltern;
	IDocument document;
	public OoListener(IOfficeApplication officeAplication,String xdatei,String xid,Dokumentation xeltern) {
		this.officeAplication = officeAplication;
		datei = xdatei;
		geaendert = false;
		id = xid;
		eltern = xeltern;
		if(xid.equals("")){
			neu = true;
		}else{
			neu = false;
		}
	}
	@Override
	public void onAlphaCharInput(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onFocus(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onInsertDone(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onInsertStart(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onLoad(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onLoadDone(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
	}
	@Override
	public void onLoadFinished(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
			
	}
	@Override
	public void onModifyChanged(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onMouseOut(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onMouseOver(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onNew(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onNonAlphaCharInput(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onSave(IDocumentEvent arg0) {
		////System.out.println("onSave");
		
	}
	@Override
	public void onSaveAs(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onSaveAsDone(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onSaveDone(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		////System.out.println("Savedone");
		IDocument doc = arg0.getDocument();
		if(doc == null){
			return;
		}
		String file = arg0.getDocument().getPersistenceService().getLocation().getPath();
		file = file.substring(1).replace("%20", " ");
		if(datei.equals(file)){
			geaendert = true;
		}
	}
	
	@SuppressWarnings("unused")
	private void doUebertragen(String file){
		
	}
	@Override
	public void onSaveFinished(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		////System.out.println("SaveFinisched");
		
	}
	@Override
	public void onUnload(IDocumentEvent arg0) {
		try {
			
			IDocument doc = arg0.getDocument();
			if(doc == null){
				System.out.println(geaendert+" - "+datei+" - "+neu+" doc = null ");
				return;
			}
			
			String file = arg0.getDocument().getPersistenceService().getLocation().getPath();
			file = file.substring(1).replace("%20", " ");
			System.out.println(geaendert+" - "+datei+" - "+file+" - "+neu);
			if(geaendert && datei.equals(file) && (!neu)){
				final String xfile = file;
				final int xid = Integer.parseInt(id);
				//final IDocumentEvent xarg0 = arg0;
				Thread.sleep(50);
				new Thread(){
					public void run(){
						String nurDatei = datei.substring(datei.replace("\\", "/").lastIndexOf("/")+1);
						int frage = JOptionPane.showConfirmDialog(null, "Die Dokumentationsdatei --> "+nurDatei+" <-- wurde geändert\n\nWollen Sie die geänderte Fassung in die Patienten-Dokumentation übernehmen?", "Wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
						if(frage == JOptionPane.YES_OPTION){
							geaendert = false;
									try {
										Reha.thisClass.patpanel.dokumentation.setCursor(Reha.thisClass.wartenCursor);
										eltern.speichernOoDocs(xid, -1, xfile, -1, null, neu);
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}							

						}
					}
				}.start();
				arg0.getDocument().removeDocumentListener(this);
				warschoninsave = true;
			}else if(datei.equals(file) && !geaendert){
				System.out.println(geaendert+" - "+datei+" - "+file+" - "+neu);
				arg0.getDocument().removeDocumentListener(this);
				//System.out.println("Listener entfernt - Datei nicht geändert"+file);
				warschoninsave = true;
			}else if(neu){
				System.out.println(geaendert+" - "+datei+" - "+file+" - "+neu);
				new Thread(){
					public void run(){	
						String nurDatei = datei.substring(datei.replace("\\", "/").lastIndexOf("/")+1);
						int frage = JOptionPane.showConfirmDialog(null, "Die Dokumentationsdatei --> "+nurDatei+" <-- wurde geändert\n\nWollen Sie die geänderte Fassung in die Patienten-Dokumentation übernehmen?", "Wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
						if(frage == JOptionPane.YES_OPTION){
							geaendert = false;
									try {
										Reha.thisClass.patpanel.dokumentation.setCursor(Reha.thisClass.wartenCursor);
										int nummer = SqlInfo.erzeugeNummer("doku");
										eltern.speichernOoDocs(nummer, -1, datei, -1, null, neu);
									} catch (Exception e) {
										e.printStackTrace();
									}							

						}
					}
				}.start();	
				arg0.getDocument().removeDocumentListener(this);
			}else{
				//System.out.println("else");
				//System.out.println("Datei equals(file) = "+datei.equals(file));
				//System.out.println("Datei = "+datei);
				//System.out.println("File = "+file);
				//System.out.println("geändert = "+geaendert);
				arg0.getDocument().removeDocumentListener(this);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	@Override
	public void disposing(IEvent arg0) {
		/*
		if(!warschoninsave){
			try {
				IDocument doc = this.document;
				if(doc == null){
					//System.out.println("doc=null");
					return;
				}
				String file = doc.getLocationURL().toString().replaceAll("file:/", "");
				if(geaendert && datei.equals(file)){
					final String xfile = file;
					final int xid = Integer.parseInt(id);

					Thread.sleep(50);
					new Thread(){
						public void run(){				
					int frage = JOptionPane.showConfirmDialog(null, "Die Dokumentationsdatei "+xfile+" wurde geändert\n\nWollen Sie die geänderte Fassung in die Patienten-Dokumentation übernehmen?", "Wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
					if(frage == JOptionPane.YES_OPTION){
						geaendert = false;
								try {
									Reha.thisClass.patpanel.dokumentation.setCursor(Reha.thisClass.wartenCursor);
									Dokumentation.speichernOoDocs(xid, -1, xfile, -1, null, neu);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}							

					}

					//Reha.officeapplication.getDesktopService().removeDocumentListener(this);
					//System.out.println("Listener entfernt - Datei geändert "+xfile);
						}
					}.start();
					doc.removeDocumentListener(this);				
				}else if(datei.equals(file) && !geaendert){
					doc.removeDocumentListener(this);
					//System.out.println("Listener entfernt - Datei nicht geändert"+file);
				}
				warschoninsave = true;

			} catch (ag.ion.bion.officelayer.document.DocumentException e) {
				e.printStackTrace();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}else{
			//System.out.println("warschoninsave = "+warschoninsave);
		}
		*/
		
	}

	
}	

