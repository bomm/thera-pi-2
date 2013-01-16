package rehaHMKPanels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import javax.swing.JPanel;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;


import CommonTools.ButtonTools;
import CommonTools.INIFile;
import CommonTools.JCompTools;
import CommonTools.JRtaCheckBox;
import CommonTools.JRtaComboBox;
import CommonTools.JRtaRadioButton;
import CommonTools.JRtaTextField;
import CommonTools.SqlInfo;
import Tools.ArztTools;



import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.DocumentException;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.text.IText;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextDocumentTextShape;
import ag.ion.bion.officelayer.text.ITextField;
import ag.ion.bion.officelayer.text.ITextFieldService;
import ag.ion.bion.officelayer.text.ITextRange;
import ag.ion.bion.officelayer.text.ITextTable;
import ag.ion.bion.officelayer.text.IViewCursor;
import ag.ion.bion.officelayer.text.TextException;
import ag.ion.noa.NOAException;
import ag.ion.noa.graphic.GraphicInfo;
import ag.ion.noa.search.ISearchResult;
import ag.ion.noa.search.SearchDescriptor;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.sun.star.beans.Property;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.XNameContainer;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.text.HoriOrientation;
import com.sun.star.text.TextContentAnchorType;
import com.sun.star.text.VertOrientation;
import com.sun.star.text.XText;
import com.sun.star.text.XTextContent;
import com.sun.star.text.XTextCursor;
import com.sun.star.uno.AnyConverter;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.util.XRefreshable;

import dialoge.ArztAuswahl;

import rehaHMK.RehaHMK;
import rehaHMK.RehaHMKTab;


import uk.co.mmscomputing.device.scanner.Scanner;
import uk.co.mmscomputing.device.scanner.ScannerIOException;
import uk.co.mmscomputing.device.scanner.ScannerIOMetadata;
import uk.co.mmscomputing.device.scanner.ScannerIOMetadata.Type;
import uk.co.mmscomputing.device.scanner.ScannerListener;

public class RehaHMKPanel2 extends JXPanel implements ScannerListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	RehaHMKTab eltern; 
	
	Scanner scanner;
	JPanel content = null;
	int ckERST_VO = 0; int ckFOLGE_VO=1; int ckADR_VO=2; int ckGRUPPEN_TH=3;int ckREZEPT_DAT=4;
	int ckBEHANDL_BEGIN=5;int ckHB_JANEIN=6;int ckTB_JANEIN=7;
	int ckINDI_SCHL=8;int ckMAX_ANZAHL=9;int ckFREQU_WO=10;
	int ckBEH_DAUER=11;
	int ckHEIL_MITTEL=12;int ckLEIT_SYMPTOMATIK=13;int ckDIAG_NOSE=14;int ckBEGRUEND_ADR=15;
	int ckSONSTIGER_GRUND=16;
	
	int tfsREZEPT_DAT=0;int tfsBEHANDL_BEGIN=1;
	int tfsINDI_SCHL=2;int tfsMAX_ANZAHL=3;
	int tfsFREQU_WO=4;int tfsBEH_DAUER=5;
	int tfsHEIL_MITTEL=6;int tfsLEIT_SYMPTOMATIK=7;
	int tfsDIAG_NOSE=8;int tfsBEGRUEND_ADR=9;
	int tfsSONSTIGER_GRUND=10;
	int rbHB_JA=0;int rbHB_NEIN=1;int rbTB_JA=2;int rbTB_NEIN=3;

	JRtaCheckBox[] chbox = {null,null,null,null,null,null,null,null,null,null,
			null,null,null,null,null,null,null,null,null,null};
	
	JRtaTextField[] tfs = {null,null,null,null,null,null,null,null,null,null,null};
	JRtaRadioButton[] rbuts = {null,null,null,null};
	ButtonGroup hbgroup = new ButtonGroup();
	ButtonGroup tbgroup = new ButtonGroup();
	JTextArea[] tas = {null,null,null,null};
	
	JButton[] buts = {null,null,null,null};
	JRtaComboBox scanners = null;
	JRtaComboBox scanformat = null;
	JRtaCheckBox scandialog = null;
	boolean scannerok = false;
	
	INIFile inifile = null;
	
	ActionListener al = null;
	
	IDocument document = null;
	ITextDocument textDocument = null;
	boolean sourceenabled = false;
	JEditorPane htmlpane = null;
	
	StringBuffer arztbuf = new StringBuffer();
	int xversatz = 0;
	
	int[][] kreuzpos = 
	{ {0,0},{3,0}, {0,2},{3,2},
	  {0,5},{5,5},{0,8},{8,8},
	  {0,11},{4,11},
	  {0,14},{4,14},
	  {0,17},{0,21},
	  {0,25},{0,29},{0,33}
	};
	int[][] textpos =
	{
			{3,11},{7,11},
			{3,14},{7,14},
			{2,18},{2,22},{2,26},{2,30},{2,34}
	};
	public RehaHMKPanel2(RehaHMKTab xeltern){
		eltern = xeltern;
		setOpaque(false);
	    setBackgroundPainter(RehaHMK.cp);
		setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		setLayout(new BorderLayout());

		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				try{
					UIManager.put("Separator.foreground", new Color(231,120,23) );
					try{
						scanStarten();
						scannerok = true;
					}catch(Exception ex2){
						scannerok = false;
					}
					activateListener();
					/*
					File f = null;
					if(! (f = new File(RehaHMK.progHome+"ini/"+RehaHMK.aktIK+"/hmrmodul.ini")).exists()){
						f.createNewFile();
					}
					*/
					inifile = new INIFile(RehaHMK.progHome+"ini/"+RehaHMK.aktIK+"/hmrmodul.ini");
					add(getContent(),BorderLayout.CENTER);
					add(getScannerSaich(),BorderLayout.SOUTH);
					validate();
				}catch(Exception ex){
					ex.printStackTrace();
				}
				return null;
			}
		}.execute();
	}
	/******************************/
	private JXPanel getScannerSaich(){
		//            1     2     3    4      5     6    7    8                9    10     11    12    13    14    15  
		String x = "15dlu,95dlu,15dlu,95dlu,15dlu,0dlu,15dlu,fill:0:grow(1.0),15dlu,95dlu,15dlu,95dlu,15dlu";
		//             1  2   3   4  5   6  7
		String y = "15dlu,p,15dlu,p,5dlu,p,15dlu";
		FormLayout lay = new FormLayout(x,y);
		CellConstraints cc = new CellConstraints();
		PanelBuilder pb = new PanelBuilder(lay);
		pb.getPanel().setOpaque(false);
		pb.getPanel().validate();
		pb.addSeparator("",cc.xyw(1, 2,11));
		boolean mustsave = false;
		pb.add(buts[0]= ButtonTools.macheButton("Formular erzeugen","ooformular",al),cc.xy(2,4));

		String sdummy = null;
		if(scannerok){
			try {
				pb.addLabel("Scanner auswählen",cc.xy(12,4)); 
				pb.add(scanners = new JRtaComboBox(scanner.getDeviceNames()),cc.xy(12,6));
				if( (sdummy = inifile.getStringProperty("HMRModul", "Scanner")) == null){
					inifile.setStringProperty("HMRModul", "Scanner", scanners.getSelectedItem().toString(), null);
					inifile.setIntegerProperty("HMRModul", "XVersatz", 0, null);
					mustsave = true;
				}else{
					scanners.setSelectedItem(sdummy);
					if( (sdummy = inifile.getStringProperty("HMRModul", "XVersatz")) == null){
						inifile.setStringProperty("HMRModul", "XVersatz", "0", null);
						mustsave = true;
					}else{
						xversatz = inifile.getIntegerProperty("HMRModul", "XVersatz");	
					}
				}
				scanners.setActionCommand("scannerwahl");
				scanners.addActionListener(al);

				/***************/
				/*
				pb.add(scandialog = new JRtaCheckBox("Scannerdialog verwenden"),cc.xy(6,6));
				if( (sdummy = inifile.getStringProperty("HMRModul", "Scandialog")) == null){
					inifile.setStringProperty("HMRModul", "Scandialog", "0", null);
					mustsave = true;
				}else{
					scandialog.setSelected(sdummy.equals("1"));
				}
				scandialog.setActionCommand("scandialog");
				scandialog.addActionListener(al);
				*/
				
			} catch (ScannerIOException e) {
				System.out.println("Kein Scanner installiert");
			} catch (NullPointerException np){
				System.out.println("Kein Scanner installiert");
			}
		}

		/***************/
		scanformat = new JRtaComboBox(new String[] {"DIN A5","DIN A4"});
		//pb.add( scanformat ,cc.xy(8,6));
		if( (sdummy = inifile.getStringProperty("HMRModul", "Scanformat")) == null){
			inifile.setStringProperty("HMRModul", "Scanformat", scanformat.getSelectedItem().toString(), null);
			mustsave = true;
		}else{
			scanformat.setSelectedItem(sdummy);
		}
		scanformat.setActionCommand("scanformat");scanformat.addActionListener(al);
		scanformat.addActionListener(al);
		/***************/
		//pb.add(buts[1]= ButtonTools.macheButton("Rezept scannen","scannen",al),cc.xy(10,6));
		pb.add(buts[2]= ButtonTools.macheButton("Arzt suchen","arztadresse",al),cc.xy(10,4));
		pb.add(buts[3]= ButtonTools.macheButton("Arzt über RezNr.","arztreznr",al),cc.xy(10,6));
		if(mustsave){inifile.save();}
		
		htmlpane = new JEditorPane();
		htmlpane.setContentType("text/html");
        htmlpane.setEditable(false);
        htmlpane.setOpaque(false);
        JScrollPane scr = JCompTools.getTransparentScrollPane(htmlpane);
        scr.validate();
        pb.add(scr,cc.xywh(4,2,5,6));
        JXPanel pane = new JXPanel(new BorderLayout());
        pane.setOpaque(false);
        //pane.setBackgroundPainter(RehaHMK.cpscanner);
        pane.add(pb.getPanel(),BorderLayout.CENTER);
		return pane;
	}
	/******************************/
	private JScrollPane getContent(){
		//           1     2          3          4          5          6
		String x = "5dlu,25dlu,fill:0:grow(0.5),5dlu,fill:0:grow(0.5),5dlu";
		//           1   2  3   4  5   6   7   8   9   10  11  12  13 14
		String y = "5dlu,p,5dlu,p,2dlu,p,10dlu,p, 5dlu,p, 10dlu,p,5dlu,p,"+
		//			15   16 17  18  19  20 21  22  23  24 23 24 25  26  27  28 29 30 31 
				   "10dlu,p,5dlu,p,10dlu,p,5dlu,p,2dlu,p,5dlu,p,2dlu,p,5dlu,p,2dlu,p,5dlu";
		FormLayout lay = new FormLayout(x,y);
		CellConstraints cc = new CellConstraints();
		PanelBuilder pb = new PanelBuilder(lay);
		pb.getPanel().setOpaque(false);
		/*******************/
		pb.addSeparator("Änderung der Rezeptart",cc.xyw(2,2,4));
		
		pb.add(chbox[this.ckERST_VO]= new JRtaCheckBox("Erstverordnung statt Folgeverordnung"),cc.xy(3,4));
		pb.add(chbox[this.ckFOLGE_VO]= new JRtaCheckBox("Folgeverordnung statt Erstverordnung"),cc.xy(5,4));
		pb.add(chbox[this.ckADR_VO]= new JRtaCheckBox("Verordnung außerhalb des Regelfalles"),cc.xy(3,6));
		pb.add(chbox[this.ckGRUPPEN_TH]= new JRtaCheckBox("Gruppentherapie"),cc.xy(5,6));
		/*******************/
		pb.addSeparator("Änderung Rezeptdatum / spätester Behandlungsbeginn",cc.xyw(2,8,4));
		//                        1  2     3        4           5  6    7     8
		JXPanel pan = getNewForm("p,10dlu,40dlu,fill:0:grow(0.5)","p");
		CellConstraints cc2 = new CellConstraints();
		pan.add(chbox[this.ckREZEPT_DAT]= new JRtaCheckBox("Rezeptdatum ändern in                  "),cc2.xy(1,1));
		pan.add(tfs[this.tfsREZEPT_DAT] = new JRtaTextField("DATUM",true),cc2.xy(3,1));
		pb.add(pan,cc.xy(3,10));

		pan = getNewForm("p,10dlu,40dlu,fill:0:grow(0.5)","p");
		pan.add(chbox[this.ckBEHANDL_BEGIN]= new JRtaCheckBox("Spätester Beh.Beginn ändern in      "),cc2.xy(1,1));
		pan.add(tfs[this.tfsBEHANDL_BEGIN] = new JRtaTextField("DATUM",true),cc2.xy(3,1));
		pb.add(pan,cc.xy(5,10));
		/*******************/
		pb.addSeparator("Hausbesuch / Therapiebericht",cc.xyw(2,12,4));		
		
		pan = getNewForm("p,10dlu,40dlu,fill:0:grow(0.5)","p,2dlu,p");
		cc2 = new CellConstraints();
		pan.add(chbox[this.ckHB_JANEIN]= new JRtaCheckBox("Hausbesuch JA/NEIN                      "),cc2.xywh(1,1,1,3,CellConstraints.DEFAULT,CellConstraints.CENTER));
		pan.add(rbuts[this.rbHB_JA] = new JRtaRadioButton("Ja"),cc2.xy(3,1));
		pan.add(rbuts[this.rbHB_NEIN] = new JRtaRadioButton("Nein"),cc2.xy(3,3));
		pb.add(pan,cc.xy(3,14));

		pan = getNewForm("p,10dlu,40dlu,fill:0:grow(0.5)","p,2dlu,p");
		pan.add(chbox[this.ckTB_JANEIN]= new JRtaCheckBox("Therapiebericht JA/NEIN                 "),cc2.xywh(1,1,1,3,CellConstraints.DEFAULT,CellConstraints.CENTER));
		pan.add(rbuts[this.rbTB_JA] = new JRtaRadioButton("Ja"),cc2.xy(3,1));
		pan.add(rbuts[this.rbTB_NEIN] = new JRtaRadioButton("Nein"),cc2.xy(3,3));
		pb.add(pan,cc.xy(5,14));
		for(int i = 0; i < 2;i++){
			if(i==1){rbuts[i].setSelected(true);rbuts[i+2].setSelected(true);}
			rbuts[i].setOpaque(false);rbuts[i+2].setOpaque(false);
			rbuts[i].setEnabled(false);rbuts[i+2].setEnabled(false);
			hbgroup.add(rbuts[i]);tbgroup.add(rbuts[i+2]);
		}
		/*******************/
		pb.addSeparator("Indikationsschlüssel / Verordnungsmenge / Behandlungsfrequenz / Behandlungsdauer",cc.xyw(2,16,4));		

		pan = getNewForm("p,10dlu,40dlu,fill:0:grow(0.5)","p,5dlu,p");
		cc2 = new CellConstraints();
		pan.add(chbox[this.ckINDI_SCHL]= new JRtaCheckBox("Indikationsschlüssel ändern in"),cc2.xy(1,1));
		pan.add(tfs[this.tfsINDI_SCHL] = new JRtaTextField("nix",true),cc2.xy(3,1));
		pan.add(chbox[this.ckFREQU_WO]= new JRtaCheckBox("Anzahl Behandlungen pro Woche"),cc2.xy(1,3));
		pan.add(tfs[this.tfsFREQU_WO] = new JRtaTextField("nix",true),cc2.xy(3,3));
		pb.add(pan,cc.xy(3,18));

		pan = getNewForm("p,10dlu,40dlu,fill:0:grow(0.5)","p,5dlu,p");
		pan.add(chbox[this.ckMAX_ANZAHL]= new JRtaCheckBox("Anzahl der Behandlungen lt. HMR"),cc2.xy(1,1));
		pan.add(tfs[this.tfsMAX_ANZAHL] = new JRtaTextField("ZAHLEN",true),cc2.xy(3,1));
		pan.add(chbox[this.ckBEH_DAUER]= new JRtaCheckBox("Dauer der Behandlugen in Minuten"),cc2.xy(1,3));
		pan.add(tfs[this.tfsBEH_DAUER] = new JRtaTextField("nix",true),cc2.xy(3,3));
		pb.add(pan,cc.xy(5,18));
		/*******************/
		pb.addSeparator("Heilmittel, Leitsymptomatik und mehr....",cc.xyw(2,20,4));	
		
		pb.add(chbox[this.ckHEIL_MITTEL]= new JRtaCheckBox("Heilmittel / HM-Kombination ändern in"),cc.xy(3,22));
		pb.add(tfs[this.tfsHEIL_MITTEL]= new JRtaTextField("nix",true),cc.xy(3,24));

		pb.add(chbox[this.ckLEIT_SYMPTOMATIK]= new JRtaCheckBox("Leitsymptomatik gemäß HMR"),cc.xy(5,22));
		pb.add(tfs[this.tfsLEIT_SYMPTOMATIK]= new JRtaTextField("nix",true),cc.xy(5,24));
		
		pb.add(chbox[this.ckDIAG_NOSE]= new JRtaCheckBox("Diagnose gemäß HMR"),cc.xy(3,26));
		pb.add(tfs[this.tfsDIAG_NOSE]= new JRtaTextField("nix",true),cc.xy(3,28));

		pb.add(chbox[this.ckBEGRUEND_ADR]= new JRtaCheckBox("medizinische Begründung für außerhalb d.Regelfalles"),cc.xy(5,26));
		pb.add(tfs[this.tfsBEGRUEND_ADR]= new JRtaTextField("nix",true),cc.xy(5,28));

		pb.add(chbox[this.ckSONSTIGER_GRUND]= new JRtaCheckBox("Sonstige Änderungen"),cc.xy(3,30));
		pb.add(tfs[this.tfsSONSTIGER_GRUND]= new JRtaTextField("nix",true),cc.xyw(3,32,3));

		
		for(int i = 0; i < 11;i++){
			tfs[i].setEnabled(false);
		}
		for(int i = 0; i < 17;i++){
			chbox[i].setActionCommand("cbox-"+Integer.toString(i));
			chbox[i].addActionListener(al);
		}
		pb.getPanel().validate();
		content = pb.getPanel();
		JScrollPane scrpane = JCompTools.getTransparentScrollPane(content);
		scrpane.getVerticalScrollBar().setUnitIncrement(15);
		scrpane.validate();
		
		return scrpane;
	}
	/******************************/
	private JXPanel getNewForm(String x,String y){
		FormLayout lay = new FormLayout(x,y);
		JXPanel pan = new JXPanel();
		pan.setOpaque(false);
		pan.setLayout(lay);
		return pan;
	}
	/******************************/
	private JRtaCheckBox createBox(String text,Font font,Color fcolor,String command,ActionListener al ){
		JRtaCheckBox box = new JRtaCheckBox(text);
		box.setFont(font);
		box.setForeground(fcolor);
		if(al != null){
			box.addActionListener(al);
		}
		return box;
	}
	/******************************/
	private void scanStarten(){
	    
	    try {
			if(scanner == null){
				scanner = Scanner.getDevice();
			}
			String[] names = scanner.getDeviceNames();
		} catch (ScannerIOException e2) {
			e2.printStackTrace();
		}

		//scanner.addListener( this);    
		
	}
	private void activateListener(){
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				String cmd = e.getActionCommand();
				if(cmd.contains("-")){
					int zahl = Integer.parseInt(cmd.split("-")[1]);
					//zuerst die Verordnungsart
					if(zahl < 4){
						if(chbox[zahl].isSelected()){
							for(int i = 0; i < 4;i++){
								if(i != zahl){
									chbox[i].setSelected(false);		
								}
							}
						}
					}else if(zahl == 4){
						if(chbox[4].isSelected()){tfs[0].setEnabled(true);tfs[0].requestFocus();}
						else{tfs[0].setText("  .  .    ");tfs[0].setEnabled(false);}
					}else if(zahl == 5){
						if(chbox[5].isSelected()){tfs[1].setEnabled(true);tfs[1].requestFocus();}
						else{tfs[1].setText("  .  .    ");tfs[1].setEnabled(false);}
					}else if(zahl == 6){
						if(chbox[6].isSelected()){rbuts[0].setEnabled(true);rbuts[1].setEnabled(true);rbuts[0].requestFocus();}
						else{rbuts[1].setSelected(true);rbuts[0].setEnabled(false);rbuts[1].setEnabled(false);}
					}else if(zahl == 7){
						if(chbox[7].isSelected()){rbuts[2].setEnabled(true);rbuts[3].setEnabled(true);rbuts[2].requestFocus();}
						else{rbuts[3].setSelected(true);rbuts[2].setEnabled(false);rbuts[3].setEnabled(false);}
					}else if(zahl == 8){
						if(chbox[8].isSelected()){tfs[2].setEnabled(true);tfs[2].requestFocus();}
						else{tfs[2].setText("");tfs[2].setEnabled(false);}
					}else if(zahl == 9){
						if(chbox[9].isSelected()){tfs[3].setEnabled(true);tfs[3].requestFocus();}
						else{tfs[3].setText("");tfs[3].setEnabled(false);}
					}else if(zahl == 10){
						if(chbox[10].isSelected()){tfs[4].setEnabled(true);tfs[4].requestFocus();}
						else{tfs[4].setText("");tfs[4].setEnabled(false);}
					}else if(zahl == 11){
						if(chbox[11].isSelected()){tfs[5].setEnabled(true);tfs[5].requestFocus();}
						else{tfs[5].setText("");tfs[5].setEnabled(false);}
					}else if(zahl == 12){
						if(chbox[12].isSelected()){tfs[6].setEnabled(true);tfs[6].requestFocus();}
						else{tfs[6].setText("");tfs[6].setEnabled(false);}
					}else if(zahl == 13){
						if(chbox[13].isSelected()){tfs[7].setEnabled(true);tfs[7].requestFocus();}
						else{tfs[7].setText("");tfs[7].setEnabled(false);}
					}else if(zahl == 14){
						if(chbox[14].isSelected()){tfs[8].setEnabled(true);tfs[8].requestFocus();}
						else{tfs[8].setText("");tfs[8].setEnabled(false);}
					}else if(zahl == 15){
						if(chbox[15].isSelected()){tfs[9].setEnabled(true);tfs[9].requestFocus();}
						else{tfs[9].setText("");tfs[9].setEnabled(false);}
					}else if(zahl == 16){
						if(chbox[16].isSelected()){tfs[10].setEnabled(true);tfs[10].requestFocus();}
						else{tfs[10].setText("");tfs[10].setEnabled(false);}
					}
				}else if(cmd.equals("scannerwahl")){
					doIniFile("HMRModul", "Scanner",scanners.getSelectedItem().toString());
				}else if(cmd.equals("scandialog")){
					doIniFile("HMRModul", "Scandialog",(scandialog.isSelected() ? "1" : "0"));
				}else if(cmd.equals("scanformat")){
					doIniFile("HMRModul", "Scanformat",scanformat.getSelectedItem().toString());
				}else if(cmd.equals("ooformular")){
					doOOFormular();
				}else if(cmd.equals("scannen")){
					if(scannerok){doScannen();}					
				}else if(cmd.equals("arztadresse")){
					doArztAdresse();					
				}else if(cmd.equals("arztreznr")){
					doArztReznum();					
				}
			}
			
		};
	}
	private void doArztAdresse(){
		JRtaTextField tf1 = new JRtaTextField("nix",false);
		JRtaTextField tf2 = new JRtaTextField("nix",false);
		JRtaTextField tf3 = new JRtaTextField("nix",false);
		ArztAuswahl awahl = new ArztAuswahl(null,"ArztAuswahl",new String[] {"",""},new JRtaTextField[] {tf1,tf2,tf3},"");
		awahl.setModal(true);
		awahl.setLocationRelativeTo(this);
		awahl.setVisible(true);
		awahl.dispose();
		awahl = null;
		System.out.println("1-"+tf1.getText());
		System.out.println("2-"+tf2.getText());
		System.out.println("3-"+tf3.getText());
		if(!tf3.getText().equals("")){
			regleHTML(tf3.getText());
		}else{
			htmlpane.setText("");
			RehaHMK.hmAdrADaten = new HashMap<String,String>();
		}
		
	}
	private void doArztReznum(){
		Object reznum = JOptionPane.showInputDialog(null, "Bitte geben Sie die Rezeptnummer ein");
		if(reznum==null || reznum.toString().equals("")){
			htmlpane.setText("");
			RehaHMK.hmAdrADaten = new HashMap<String,String>();
			return;
		}else{
			String test = SqlInfo.holeEinzelFeld("select arztid from verordn where rez_nr = '"+reznum.toString()+"' LIMIT 1");
			if(test.equals("")){
				test = SqlInfo.holeEinzelFeld("select arztid from lza where rez_nr = '"+reznum.toString()+"' LIMIT 1");
				if(test.equals("")){
					JOptionPane.showMessageDialog(null,"Die Rezeptnummer ist weder im aktuellen Rezeptstamm noch in der Historie vorhanden!");
					htmlpane.setText("");
					RehaHMK.hmAdrADaten = new HashMap<String,String>();
				}else{
					JOptionPane.showMessageDialog(null,"Zur Info: Das angegebene Rezept ist bereits in der Historie!");
					regleHTML(test);
				}
			}else{
				regleHTML(test);
			}
		}
	}
	private void regleHTML(String arztid){
		ArztTools.constructArztHMap(arztid);
		arztbuf.setLength(0);
		arztbuf.trimToSize();
		arztbuf.append("<html><body><br><font face='Arial, Helvetica'>");
		arztbuf.append("Klinik: "+(RehaHMK.hmAdrADaten.get("<Aklinik>").equals("") ? "keine Klinik!!!" : RehaHMK.hmAdrADaten.get("<Aklinik>"))+"<br>");
		arztbuf.append("Anrede: "+RehaHMK.hmAdrADaten.get("<Aadr1>")+" "+RehaHMK.hmAdrADaten.get("<Aadr2>")+"<br>");
		arztbuf.append("Fax: "+(RehaHMK.hmAdrADaten.get("<Afax>").equals("") ? "keine Faxnummer vorhanden!!!": RehaHMK.hmAdrADaten.get("<Afax>")));
		arztbuf.append("<br>Briefanrede: "+RehaHMK.hmAdrADaten.get("<Aadr5>"));
		arztbuf.append("</font></body></html>");
		htmlpane.setText(arztbuf.toString());
	}
	private void doIniFile(String sektion,String property,String value){
		inifile.setStringProperty(sektion, property, value, null);
		inifile.save();
	}
	private void doOOFormular(){
		//Rezeptkorrektur_A5-Rezepte.ott
		IDocumentService documentService = null;;
		RehaHMK.thisFrame.setCursor(RehaHMK.thisClass.wartenCursor);
		if(!RehaHMK.officeapplication.isActive()){
			RehaHMK.starteOfficeApplication();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			documentService = RehaHMK.officeapplication.getDocumentService();

		} catch (OfficeApplicationException e) {
			e.printStackTrace();
		}
        IDocumentDescriptor docdescript = new DocumentDescriptor();
        docdescript.setHidden(true);
    	//docdescript.setHidden(false);
        docdescript.setAsTemplate(true);
		IDocument document = null;
		
		String url = (scanformat.getSelectedIndex()==0 ? 
				RehaHMK.progHome+"vorlagen/"+RehaHMK.aktIK+"/Rezeptkorrektur_A5-Rezepte.ott" :
				RehaHMK.progHome+"vorlagen/"+RehaHMK.aktIK+"/Rezeptkorrektur_A4-Rezepte.ott");	
		try {
			document = documentService.loadDocument(url,docdescript);
		} catch (NOAException e) {
			e.printStackTrace();
		}
		textDocument = (ITextDocument)document;
		//doRefresh(textDocument);
		/**********************/
		ITextFieldService textFieldService = textDocument.getTextFieldService();

		ITextField[] placeholders = null;
		try {
			placeholders = textFieldService.getPlaceholderFields();

		} catch (TextException e) {
			e.printStackTrace();
		}
		try{
			String placeholderDisplayText = null;
			for (int i = 0; i < placeholders.length; i++) {
				boolean schonersetzt = false;
				try{
					placeholderDisplayText = placeholders[i].getDisplayText().toLowerCase();
				}catch(com.sun.star.uno.RuntimeException ex){
					ex.printStackTrace();
				}
				Set<?> entries = RehaHMK.hmAdrADaten.entrySet();
			    Iterator<?> it = entries.iterator();
			    while (it.hasNext()) {
			      Map.Entry<?,?> entry = (Map.Entry<?, ?>) it.next();
			      //System.out.println(placeholderDisplayText);
			      if(entry.getKey().toString().toLowerCase().equals(placeholderDisplayText)){
			    	  if(entry.getValue().toString().trim().equals("")){
			    		  placeholders[i].getTextRange().setText("");
			    	  }else{
				    	  placeholders[i].getTextRange().setText(entry.getValue().toString());		    		  
			    	  }
			    	  schonersetzt = true;
			    	  break;
			      }
			    }			
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		ITextTable textTable = null;
		try {
			textTable = textDocument.getTextTableService().getTextTable("Tabelle1");
		} catch (TextException e) {
			e.printStackTrace();
		}
		if(textTable==null){
			JOptionPane.showMessageDialog(null,"Kan Tabelle nicht finden");
		}else{
			try {
				for(int i2 = 0; i2 < kreuzpos.length;i2++){
					textTable.getCell(kreuzpos[i2][0],kreuzpos[i2][1]).getTextService().getText().setText((chbox[i2].isSelected() ? "X" : ""));
					if(i2 >=8){
						textTable.getCell(textpos[i2-8][0],textpos[i2-8][1]).getTextService().getText().setText(tfs[(i2-8)+2].getText());
					}
				}
				//Datumsfelder
				if( chbox[4].isSelected() && (!tfs[0].getText().trim().equals(".  .")) ){
						textTable.getCell(4,5).getTextService().getText().setText(tfs[0].getText());
				}
				if( chbox[5].isSelected() && (!tfs[1].getText().trim().equals(".  .")) ){
					textTable.getCell(9,5).getTextService().getText().setText(tfs[1].getText());
				}
				//RadioButtons
				if( chbox[6].isSelected() ){
					if(rbuts[0].isSelected()){
						textTable.getCell(3,8).getTextService().getText().setText("X");
					}else{
						textTable.getCell(5,8).getTextService().getText().setText("X");
					}
				}
				if(chbox[7].isSelected() ){
					if(rbuts[2].isSelected()){
						textTable.getCell(11,8).getTextService().getText().setText("X");
					}else{
						textTable.getCell(13,8).getTextService().getText().setText("X");						
					}
				}
			
			} catch (TextException e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(50);
				textDocument.getFrame().getXFrame().getContainerWindow().setFocus();
				doRefresh(textDocument);
				textDocument.getFrame().getXFrame().getContainerWindow().setFocus();
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//ITextDocumentTextShape shape = null;
			System.out.println(textDocument.getXTextDocument().getText().getText().getText().getText());
			try {
				textTable = textDocument.getTextTableService().getTextTable("Tabelle1");
			} catch (TextException e) {
				e.printStackTrace();
			}			
			
			System.out.println("Textinhalt = "+textDocument.getTextService().getText().getText());
			sucheNachPlatzhalter( (ITextDocument) document);
			

		}
		/**********************/
		if(scannerok){
			int frage = JOptionPane.showConfirmDialog(null, "Wollen Sie im Anschluß ein Rezept einscannen", "Benutzeranfrage", JOptionPane.YES_NO_OPTION);
			if(frage == JOptionPane.YES_OPTION){
				try{
					doScannen();	
				}catch(Exception ex){
					JOptionPane.showMessageDialog(null, "Scanner nicht angeschlossen oder nicht eingeschaltet");
				}
			}else{
		        new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() throws Exception {
						textDocument.getFrame().getXFrame().getContainerWindow().setVisible(true);
						return null;
					}
		        	
		        }.execute();

			}
		}else{
	        new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					textDocument.getFrame().getXFrame().getContainerWindow().setVisible(true);
					return null;
				}
	        	
	        }.execute();


		}
		
		
		
		RehaHMK.thisFrame.setCursor(RehaHMK.thisClass.normalCursor);
	}
	private static void doRefresh(ITextDocument document){
		XRefreshable refresh = null;
        refresh = (XRefreshable)UnoRuntime.queryInterface(XRefreshable.class, document.getXTextDocument());
        refresh.refresh();
        try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private static boolean sucheNachPlatzhalter(ITextDocument document){
		OutputStream out = null;
		try {
			document.getPersistenceService().store(out);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		doRefresh(document);
		
		IViewCursor viewCursor = document.getViewCursorService().getViewCursor();
		viewCursor.getPageCursor().jumpToStartOfPage();
		document.getTextService().getCursorService().getTextCursor().gotoStart(true);

		IText text = document.getTextService().getText();
		System.out.println(text.getText());
		String stext = text.getText();
		int start = 0;
		//int end = 0;
		String dummy;
		int vars = 0;
		//int sysvar = -1;
		//document.getTextService().getCursorService().getTextCursor().gotoStart(false);
		boolean noendfound = false;
		//System.out.println(stext);
		System.out.println("Index von ^ = "+stext.indexOf("^"));
		while ((start = stext.indexOf("^")) >= 0){
			noendfound = true;
			for(int i = 1;i < 150;i++){
				if(stext.substring(start+i,start+(i+1)).equals("^")){
					dummy = stext.substring(start,start+(i+1));
					String sanweisung = dummy.toString().replace("^", "");
					Object ret = JOptionPane.showInputDialog(null,"<html>Bitte Wert eingeben für: --\u003E<b> "+sanweisung+" </b> &nbsp; </html>","Platzhalter gefunden", 1);
					if(ret==null){
						return true;
							//sucheErsetze(dummy,"");
					}else{
						sucheErsetze(document,dummy,((String)ret).trim(),false);
						stext = text.getText();
					}
					noendfound = false;
					vars++;
					break;
				}
			}
			if(noendfound){
				JOptionPane.showMessageDialog(null,"Der Baustein ist fehlerhaft, eine Übernahme deshalb nicht möglich"+
						"\n\nVermutete Ursache des Fehlers: es wurde ein Start-/Endezeichen '^' für Variable vergessen\n");
				return false;
			}
		}
		return true;
	}
	
	private static void sucheErsetze(ITextDocument document,String suchenach,String ersetzemit,boolean alle){
		SearchDescriptor searchDescriptor = new SearchDescriptor(suchenach);
		searchDescriptor.setIsCaseSensitive(true);
		ISearchResult searchResult = null;
		if(alle){
			searchResult = document.getSearchService().findAll(searchDescriptor);
		}else{
			searchResult = document.getSearchService().findFirst(searchDescriptor);			
		}

		if(!searchResult.isEmpty()) {
			ITextRange[] textRanges = searchResult.getTextRanges();
			for (int resultIndex=0; resultIndex<textRanges.length; resultIndex++) {
				textRanges[resultIndex].setText(ersetzemit);
				
			}
		}
	}
	
	/*************
	 * 
	 * 
	 * Scanner-routine
	 * 
	 * 
	 * 
	 */
	
	private void doScannen(){
		if(!scannerok){return;}
		try {
			if(textDocument == null){
				JOptionPane.showMessageDialog(null, "Erzeugen Sie zuerst das Formular");
				return;
			}

			scanner.addListener(this);
			scanner.select(scanners.getSelectedItem().toString());
			sourceenabled = false;
			scanner.acquire();
		} catch (ScannerIOException e1) {
			e1.printStackTrace();
		}
		
	}
	/******************************************************************/
	@Override
	public void update(ScannerIOMetadata.Type type, ScannerIOMetadata metadata) {
		if ( ScannerIOMetadata.NEGOTIATE.equals(type)){
			//System.out.println("Status 1");
			//System.out.println(metadata.getStateStr()+"\n");
		}else if( ScannerIOMetadata.STATECHANGE.equals(type)){
			//System.out.println("Status 2");
			if(metadata.getStateStr().equals("Source Manager Open") && sourceenabled ){
				//Abbruch durch den Benutzer
		        new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() throws Exception {
						textDocument.getFrame().getXFrame().getContainerWindow().setVisible(true);
						return null;
					}
		        	
		        }.execute();

			}else if(metadata.getStateStr().equals("Source Enabled")){
				sourceenabled = true;
			}
			//System.out.println(metadata.getStateStr()+"\n");
		}else if(type.equals(ScannerIOMetadata.EXCEPTION)){
			//System.out.println("Status 3");
	        new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					textDocument.getFrame().getXFrame().getContainerWindow().setVisible(true);
					return null;
				}
	        	
	        }.execute();

		}else if(ScannerIOMetadata.ACQUIRED.equals( type )){
			//System.out.println("Status 4"+"\n");
			//System.out.println(metadata.getStateStr()+"\n");
			scanner.removeListener(this);
			File file = new File(RehaHMK.progHome+"temp/"+RehaHMK.aktIK+"/rezkorrekt.jpg");
			try {
				FileOutputStream fout = new FileOutputStream(file);
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);
				JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(((BufferedImage)metadata.getImage()));  
				param.setQuality((float) 1.0f, false);  
				encoder.setJPEGEncodeParam(param);  
				encoder.encode(((BufferedImage)metadata.getImage()));
				os.close();
				fout.write( os.toByteArray());
				fout.flush();
				fout.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (ImageFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			GraphicInfo graphicInfo = null;
			String imagePath = new File(RehaHMK.progHome+"temp/"+RehaHMK.aktIK+"/rezkorrekt.jpg").getAbsolutePath(); 
			graphicInfo = new GraphicInfo(imagePath, 200, true, 200, true,
                    VertOrientation.TOP, HoriOrientation.RIGHT,
                    TextContentAnchorType.AT_PAGE); 
			
			XMultiServiceFactory multiServiceFactory = (XMultiServiceFactory) UnoRuntime.queryInterface(XMultiServiceFactory.class,
	                  textDocument.getXTextDocument());
	          XText xText = textDocument.getXTextDocument().getText();
	          
	        XTextCursor  xTextCursor = xText.createTextCursor();
	        embedGraphic(graphicInfo,multiServiceFactory,xTextCursor);
	        new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					textDocument.getFrame().getXFrame().getContainerWindow().setVisible(true);
					return null;
				}
	        	
	        }.execute();
			sourceenabled = false;
		}
		
	}
	
	private void embedGraphic(GraphicInfo grProps,
            XMultiServiceFactory xMSF, XTextCursor xCursor) {

    XNameContainer xBitmapContainer = null;
    XText xText = xCursor.getText();
    XTextContent xImage = null;
    String internalURL = null;
    String url = null;

    try {
            xBitmapContainer = (XNameContainer) UnoRuntime.queryInterface(
                            XNameContainer.class, xMSF.createInstance(
                                            "com.sun.star.drawing.BitmapTable"));
            xImage = (XTextContent) UnoRuntime.queryInterface(
                            XTextContent.class,     xMSF.createInstance(
                                            "com.sun.star.text.TextGraphicObject"));
            XPropertySet xProps = (XPropertySet) UnoRuntime.queryInterface(
                            XPropertySet.class, xImage);
            
  	      
            /*
  	      	Property[] prop = xProps.getPropertySetInfo().getProperties(); 
	      	for(int i = 0; i < prop.length;i++){
	    	  	System.out.println(prop[i].Name);
	    	  	System.out.println(prop[i].Attributes);
	      	}
			*/
            //url = "file:///"+RehaHMK.progHome+"ScreenShots/termin__temp.jpg";
            url = "file:///"+RehaHMK.progHome+"temp/"+RehaHMK.aktIK+"/rezkorrekt.jpg";

            xBitmapContainer.insertByName("someID",(Object) url);
            //xBitmapContainer.insertByName("someID", grProps.getUrl());
            internalURL = AnyConverter.toString(xBitmapContainer
                            .getByName("someID"));

            xProps.setPropertyValue("AnchorType",
                            com.sun.star.text.TextContentAnchorType.AT_PAGE);
            xProps.setPropertyValue("GraphicURL", internalURL);

            xProps.setPropertyValue("Width", (int) 14850);
            xProps.setPropertyValue("Height", (int) 21000);

            /*
            xProps.setPropertyValue("ContourOutside", Boolean.valueOf(true));

            xProps.setPropertyValue("TextWrap", 1);
            xProps.setPropertyValue( "HoriOrientPosition",
            		HoriOrientation.RIGHT );
            xProps.setPropertyValue( "HoriOrient",
            		HoriOrientation.RIGHT );
            xProps.setPropertyValue( "HoriOrientRelation",
            		7 );
            xProps.setPropertyValue( "VertOrientPosition",
                    VertOrientation.TOP );     
            */
            xProps.setPropertyValue("TextWrap", 1);
            xProps.setPropertyValue( "HoriOrientRelation",7);
            xProps.setPropertyValue( "HoriOrient",0);
            xProps.setPropertyValue( "HoriOrientPosition",15210+xversatz);
            
            xProps.setPropertyValue( "VertOrientRelation",7);
            xProps.setPropertyValue( "VertOrient",0);
            xProps.setPropertyValue( "VertOrientPosition",609);
            
            		
            /*
            xProps.setPropertyValue( "HoriOrientPosition",
            		15014 );
            xProps.setPropertyValue( "LeftMargin",
            		15014 );
            */		

            

            
            xProps.setPropertyValue("Width", (int) 14000);
            xProps.setPropertyValue("Height", (int) 19798);


            xText.insertTextContent(xCursor, xImage, false);
            xBitmapContainer.removeByName("someID");
    } catch (Exception e) {
    	e.printStackTrace();
            System.out.println("Fehler beim einfügen der Grafik");
            System.out.println(url);
    }
}
	


}
