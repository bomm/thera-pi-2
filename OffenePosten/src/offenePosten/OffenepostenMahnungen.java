package offenePosten;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import offenePosten.OffenepostenPanel.eSpalte;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import Tools.ButtonTools;
import Tools.DatFunk;
import Tools.JCompTools;
import Tools.JRtaCheckBox;
import Tools.JRtaRadioButton;
import Tools.JRtaTextField;
import Tools.OOTools;
import Tools.SqlInfo;
import Tools.StringTools;
import Tools.TableTool;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextField;
import ag.ion.bion.officelayer.text.ITextFieldService;
import ag.ion.bion.officelayer.text.TextException;
import ag.ion.noa.NOAException;
import ag.ion.noa.internal.printing.PrintProperties;

import com.hexiong.jdbf.DBFReader;
import com.hexiong.jdbf.JDBFException;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class OffenepostenMahnungen extends JXPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7011413450109922373L;
	ActionListener al = null;
	OffenepostenTab eltern = null;
	JXPanel content = null;

	ButtonGroup bgroup = new ButtonGroup();
	JRtaRadioButton[] rbMahnart = {null,null,null,null};
	JButton suchen = null;

	// Lemmi-Doku: Das sind die Textfelder auf der linken Seite
	JRtaTextField[] rtfs = {null,null,null,null,null,
							null,null,null,null,null,
							null,null}; 
	// Lemmi 20110102: Harte Indices ersetzt durch sprechende Namen
	final int cMNAME1 = 0;
	final int cMNAME2 = 1;
	final int cMSTRASSE = 2;
	final int cMPLZORT = 3;
	final int cMRNUMMER = 4;
	final int cMRDATUM = 5;
	final int cMRBETRAG = 6;
	final int cMROFFEN = 7;
	final int cMMAHNDAT1 = 8;
	final int cMMAHNDAT2 = 9;
	final int cMMAHNDAT3 = 10;
	
	
	JRtaCheckBox cbMahnsperre = null;
	int aktuelleMahnstufe = 1;  // initialisierung
	
	MyMahnungenTableModel tabmod = null;
	JXTable tab = null;
	
	JButton[] mahnbuts = {null,null,null};

	DBFReader dbfreader = null;
	File f = null;
	Font fontfett = new Font("Tahoma",Font.BOLD,10);
	
	DecimalFormat dcf = new DecimalFormat("###0.00");
	
	HashMap<String,String> mahnParameter = new HashMap<String,String>(); 
	ITextDocument textDocument;
	
	public OffenepostenMahnungen(OffenepostenTab xeltern){
		super();
		this.eltern = xeltern;
		this.setLayout(new BorderLayout());
		activateActionListener();
		add(getContent(),BorderLayout.CENTER);
		
		
	}
	
	// Lemmi Doku: Gesamt-Layout des Dialogs
	private JXPanel getContent(){
		//String xwerte = "fill:0:grow(0.5),fill:0:grow(0.5),2dlu";
		String xwerte = "220dlu,fill:0:grow(1.0),5dlu";
		//                 1  2  3   4  5   6      7
//		String ywerte = "0dlu,p,0dlu,p,0dlu,p,fill:0:grow(1.0)";
		// Lemmi 20101225: y-Einteilung geändert 
		//                 1  2  3   4                 5  
		String ywerte = "0dlu,p,0dlu,fill:p:grow(1.0),5dlu";
		
		content = new JXPanel();
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		content.setLayout(lay);
		
		content.add(getRadioPanel(),cc.xyw(1,2,2,CellConstraints.FILL,CellConstraints.TOP));
		content.add(getRechnungDatenPanel(),cc.xy(1,4));
		content.add(getTablePanel(),cc.xy(2,4,CellConstraints.FILL,CellConstraints.FILL));
		
		// Lemmi 20101215: "ButtonPanel" rausgenommen und die Knöpfe direkt im "RechnungDatenPanel" mit eingebaut, damit die Tabelle wachsen kann
//		content.add(getButtonPanel(),cc.xy(1,6,CellConstraints.FILL,CellConstraints.TOP));

		content.validate();
		return content;
	}
	
/* Lemmi 20101215: "ButtonPanel" rausgenommen und die Knöpfe im "getRechnungDatenPanel" mit eingebaut
	private JXPanel getButtonPanel(){
		JXPanel buttonpan = new JXPanel();
		//                     1            2          3            4           5           6          7
//		String xwerte = "fill:0:grow(0.25),80dlu,fill:0:grow(0.25),80dlu,fill:0:grow(0.25),80dlu,fill:0:grow(0.25)";
		String xwerte = "fill:0:grow(0.25),80dlu,fill:0:grow(0.25),30dlu,fill:0:grow(0.25),30dlu,fill:0:grow(0.25)";
		String ywerte = "15dlu,p,15dlu";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		buttonpan.setLayout(lay);
		buttonpan.add((mahnbuts[0] = ButtonTools.macheButton("Mahnung drucken", "mahnungstarten", al)),cc.xy(2,2));
		buttonpan.add((mahnbuts[1] = ButtonTools.macheButton(" << ", "vorheriger", al)),cc.xy(4,2));
		buttonpan.add((mahnbuts[2] = ButtonTools.macheButton(" >> ", "naechster", al)),cc.xy(6,2));
		buttonpan.validate();
		return buttonpan;
	}
*/

	// Lemmi Doku: Tabelle mit den Rechnungsdaten
	private JXPanel getTablePanel(){
		JXPanel tablepan = new JXPanel();
		String xwerte = "fill:0:grow(1.0)";
		String ywerte = "fill:0:grow(1.0)";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		tablepan.setLayout(lay);
		
		// Kopf der Liste erstellen
		tabmod = new MyMahnungenTableModel();
		Vector<Vector<String>> felder = SqlInfo.holeFelder("describe rliste");
		String[] spalten = new String[felder.size()];
		for(int i= 0; i < felder.size();i++){
			spalten[i] = felder.get(i).get(0);
		}
		tabmod.setColumnIdentifiers(spalten);
		tab = new JXTable(tabmod);
		tab.setHorizontalScrollEnabled(true);
		tab.getColumn(eSpalte.cX_NUMMER.iValue).setCellRenderer(new Tools.MitteRenderer());
		tab.getColumn(eSpalte.cR_KLASSE.iValue).setCellRenderer(new Tools.MitteRenderer());
		tab.getColumn(eSpalte.cR_BETRAG.iValue).setCellRenderer(new Tools.DoubleTableCellRenderer());
		tab.getColumn(eSpalte.cR_OFFEN.iValue).setCellRenderer(new Tools.DoubleTableCellRenderer());
		tab.getColumn(eSpalte.cR_ZUZAHL.iValue).setCellRenderer(new Tools.DoubleTableCellRenderer());
		tab.getColumn(eSpalte.cR_BETRAG.iValue).setCellEditor(new Tools.DblCellEditor());
		tab.getColumn(eSpalte.cR_OFFEN.iValue).setCellEditor(new Tools.DblCellEditor());
		tab.getColumn(eSpalte.cR_ZUZAHL.iValue).setCellEditor(new Tools.DblCellEditor());
		
		// Lemmi 20101220: Die Spalte R_NUMMER unsichtbar machen
		tab.getColumn(eSpalte.cR_NUMMER.iValue).setMinWidth(0);
		tab.getColumn(eSpalte.cR_NUMMER.iValue).setMaxWidth(0);

		tab.getSelectionModel().addListSelectionListener( new MahnungListSelectionHandler());
		tab.setHighlighters(HighlighterFactory.createSimpleStriping(HighlighterFactory.CLASSIC_LINE_PRINTER));
		
		JScrollPane jscr = JCompTools.getTransparentScrollPane(tab);
		jscr.validate();
		
		tablepan.add(jscr,cc.xy(1,1));
		
		tablepan.validate();
		return tablepan;
	}
	
	// Lemmi Doku: Die Textfelder auf der linken Dialog-Seite
	private JXPanel getRechnungDatenPanel(){
		JXPanel rechnungpan = new JXPanel();
		
		//                1     2     3    4         5   6     7     8     9    10    11   12     13
		String xwerte = "15dlu,60dlu,5dlu,100dlu:g,3dlu";
		//                1    2  3   4  5   6  7   8  9  10 11  12 13  14 15  16 17  18  19
		String ywerte = "25dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,"+
		//20 21 22 23  24 25  26  27 28  29 30 31
		"p,2dlu,p,2dlu,p,4dlu,p,2dlu,p,2dlu,p,2dlu";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		rechnungpan.setLayout(lay);

		JLabel lab = new JLabel("Einzelheiten der ausgewählten Rechnung");
		lab.setForeground(Color.BLUE);
		rechnungpan.add(lab,cc.xyw(2,1,3,CellConstraints.DEFAULT,CellConstraints.CENTER));
		
		lab = new JLabel("Name1");
		rechnungpan.add(lab,cc.xy(2,2));
		rtfs[cMNAME1] = new JRtaTextField("nix",true);
		rechnungpan.add(rtfs[cMNAME1],cc.xy(4,2));
		
		lab = new JLabel("Name2");
		rechnungpan.add(lab,cc.xy(2,4));
		rtfs[cMNAME2] = new JRtaTextField("nix",true);
		rechnungpan.add(rtfs[cMNAME2],cc.xy(4,4));

		lab = new JLabel("Strasse");
		rechnungpan.add(lab,cc.xy(2,6));
		rtfs[cMSTRASSE] = new JRtaTextField("nix",true);
		rechnungpan.add(rtfs[cMSTRASSE],cc.xy(4,6));

		lab = new JLabel("PLZ/Ort");
		rechnungpan.add(lab,cc.xy(2,8));
		rtfs[cMPLZORT] = new JRtaTextField("nix",true);
		rechnungpan.add(rtfs[cMPLZORT],cc.xy(4,8));

		lab = new JLabel("R-Nr.");
		rechnungpan.add(lab,cc.xy(2,10));
		// Lemmi 20101220: Die Rechnungs-Nummer kann auch alphanumerisch sein !
//		rtfs[cMRNUMMER] = new JRtaTextField("ZAHLEN",true);
		rtfs[cMRNUMMER] = new JRtaTextField("nix",true);
		rtfs[cMRNUMMER].setFont(fontfett);
		rechnungpan.add(rtfs[cMRNUMMER],cc.xy(4,10));

		lab = new JLabel("R-Datum");
		rechnungpan.add(lab,cc.xy(2,12));
		rtfs[cMRDATUM] = new JRtaTextField("DATUM",true);
		rtfs[cMRDATUM].setFont(fontfett);
		rechnungpan.add(rtfs[cMRDATUM],cc.xy(4,12));

		lab = new JLabel("R-Betrag");
		rechnungpan.add(lab,cc.xy(2,14));
		rtfs[cMRBETRAG] = new JRtaTextField("F",true,"6.2","LINKS");
		rtfs[cMRBETRAG].setFont(fontfett);
		rtfs[cMRBETRAG].setForeground(Color.BLUE);
		rechnungpan.add(rtfs[cMRBETRAG],cc.xy(4,14));
		
		lab = new JLabel("R-Offen");
		rechnungpan.add(lab,cc.xy(2,16));
		rtfs[cMROFFEN] = new JRtaTextField("F",true,"6.2","LINKS");
		rtfs[cMROFFEN].setFont(fontfett);
		rtfs[cMROFFEN].setForeground(Color.RED);
		rechnungpan.add(rtfs[cMROFFEN],cc.xy(4,16));
		
		lab = new JLabel("1. Mahnung");
		rechnungpan.add(lab,cc.xy(2,18));
		rtfs[cMMAHNDAT1] = new JRtaTextField("DATUM",true);
		rtfs[cMMAHNDAT1].setFont(fontfett);
		rechnungpan.add(rtfs[cMMAHNDAT1],cc.xy(4,18));

		lab = new JLabel("2. Mahnung");
		rechnungpan.add(lab,cc.xy(2,20));
		rtfs[cMMAHNDAT2] = new JRtaTextField("DATUM",true);
		rtfs[cMMAHNDAT2].setFont(fontfett);
		rechnungpan.add(rtfs[cMMAHNDAT2],cc.xy(4,20));
		
		lab = new JLabel("3. Mahnung");
		rechnungpan.add(lab,cc.xy(2,22));
		rtfs[cMMAHNDAT3] = new JRtaTextField("DATUM",true);
		rtfs[cMMAHNDAT3].setFont(fontfett);
		rechnungpan.add(rtfs[cMMAHNDAT3],cc.xy(4,22));
		
		lab = new JLabel("Mahnsperre?");
		rechnungpan.add(lab,cc.xy(2,24));
		cbMahnsperre = new JRtaCheckBox("Mahnsperre verhängt");
		rechnungpan.add(cbMahnsperre,cc.xy(4,24));

		// Lemmi 20101225: die Knöpfe aus der früheren "ButtonPanel" hier eingebaut, damit die Daten-Tabelle möglichst groß werden kann
		rechnungpan.add((mahnbuts[0] = ButtonTools.macheButton("Mahnung drucken", "mahnungstarten", al)),cc.xy(4,26));
		rechnungpan.add((mahnbuts[1] = ButtonTools.macheButton("^ ^ ^", "vorheriger", al)),cc.xy(4,28));
		rechnungpan.add((mahnbuts[2] = ButtonTools.macheButton("v v v", "naechster", al)),cc.xy(4,30));
		
		rechnungpan.validate();
		return rechnungpan;

	}
	private JXPanel getRadioPanel(){
		JXPanel radiopan = new JXPanel();
		radiopan.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		//                1     2     3    4    5     6    7     8     9    10    11   12     13
		String xwerte = "15dlu,150dlu,2dlu,60dlu,2dlu,60dlu,2dlu,60dlu,2dlu,60dlu,2dlu,30dlu,0dlu:g,5dlu";
		//                1    2  3
		String ywerte = "10dlu,p,10dlu";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		radiopan.setLayout(lay);
		
		JLabel lab = new JLabel("Bitte die gewünschte Mahnstufe einstellen");
		radiopan.add(lab,cc.xy(2,2));
		
		rbMahnart[0] = new JRtaRadioButton("1. Mahnung");
		rbMahnart[0].setSelected(true);
		rbMahnart[0].setName("mahnung1");
		rbMahnart[0].addActionListener(al);
		bgroup.add(rbMahnart[0]);
		radiopan.add(rbMahnart[0],cc.xy(4,2));
		
		rbMahnart[1] = new JRtaRadioButton("2. Mahnung");
		rbMahnart[1].setName("mahnung2");
		rbMahnart[1].addActionListener(al);
		bgroup.add(rbMahnart[1]);
		radiopan.add(rbMahnart[1],cc.xy(6,2));

		rbMahnart[2] = new JRtaRadioButton("3. Mahnung");
		rbMahnart[2].setName("mahnung3");
		rbMahnart[2].addActionListener(al);
		bgroup.add(rbMahnart[2]);
		radiopan.add(rbMahnart[2],cc.xy(8,2));
		
		rbMahnart[3] = new JRtaRadioButton("Anwaltsliste");
		rbMahnart[3].setName("mahnung4");
		rbMahnart[3].addActionListener(al);
		bgroup.add(rbMahnart[3]);
		radiopan.add(rbMahnart[3],cc.xy(10,2));
		
		suchen = new JButton("los..");
		suchen.setActionCommand("suchen");
		suchen.addActionListener(al);
		radiopan.add(suchen,cc.xy(12,2));

		radiopan.validate();
		return radiopan;
	}
	private void activateActionListener(){
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd = arg0.getActionCommand();
				if(arg0.getSource() instanceof JRtaRadioButton){
					String componente = ((JComponent)arg0.getSource()).getName();
					aktuelleMahnstufe = Integer.parseInt( componente.substring(componente.length()-1)  );
					doAllesAufNull();
					return;
				}
				if(cmd.equals("suchen")){
					new SwingWorker<Void,Void>(){
						@Override
						protected Void doInBackground() throws Exception {
							try{
								OffenePosten.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
								doSuchen();	
							}catch(Exception ex){
								ex.printStackTrace();
								OffenePosten.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
								JOptionPane.showMessageDialog(null,"Fehler beim Bezug der Mahndaten in Mahnstufe "+Integer.toString(aktuelleMahnstufe));
							}
							OffenePosten.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
							return null;
						}
						
					}.execute();
					
				}
				if(cmd.equals("mahnungstarten")){
					doMahnungStarten();
				}
				if(cmd.equals("vorheriger")){
					doVorheriger();
				}
				if(cmd.equals("naechster")){
					doNaechster();					
				}
			}
			
		};
	}
	private void doNaechster(){
		int row = tab.getSelectedRow();
		if(row < 0){
			JOptionPane.showMessageDialog(null, "Keine Rechnung in der Tabelle ausgewählt");
			return;
		}
		if(row < tab.getRowCount() - 1){  // Lemmi 20101225: "- 1" ergänzt
			tab.setRowSelectionInterval(row+1, row+1);
		}else{
			JOptionPane.showMessageDialog(null, "Sie sind bereits am Tabellenende angelangt");
			return;
		}
	}
	private void doVorheriger(){
		int row = tab.getSelectedRow();
		if(row < 0){
			JOptionPane.showMessageDialog(null, "Keine Rechnung in der Tabelle ausgewählt");
			return;
		}
		if(row > 0){
			tab.setRowSelectionInterval(row-1, row-1);
		}else{
			JOptionPane.showMessageDialog(null, "Sie sind bereits am Teballenanfang angelangt");
			return;
		}
	}

	
	// Lemmi 20101220: In der ganzen Routine harte Spalten-Zahlen ersetzt durch "eSpalte.c??????.iValue"
	private void doMahnungStarten(){
		if(cbMahnsperre.isSelected()){
			JOptionPane.showMessageDialog(null,"Diese Rechnung ist mit einer Mahnsperre belegt und kann deshalb nicht angemahnt werden!");
			return;
		}
		initHashMap();
		mahnParameter.put("<Mname1>", rtfs[cMNAME1].getText().trim());		
		mahnParameter.put("<Mname2>", rtfs[cMNAME2].getText().trim());
		mahnParameter.put("<Mstrasse>", rtfs[cMSTRASSE].getText().trim());
		mahnParameter.put("<Mplzort>", rtfs[cMPLZORT].getText().trim());
		mahnParameter.put("<Mrnummer>", rtfs[cMRNUMMER].getText().trim());
		mahnParameter.put("<Mrdatum>", rtfs[cMRDATUM].getText().trim());
		mahnParameter.put("<Mrbetrag>", rtfs[cMRBETRAG].getText().trim());
		mahnParameter.put("<Mroffen>", rtfs[cMROFFEN].getText().trim());
		mahnParameter.put("<Mheute>", DatFunk.sHeute());
		mahnParameter.put("<Mmahndat1>", (rtfs[cMMAHNDAT1].getText().trim().length()==10 ? rtfs[cMMAHNDAT1].getText().trim() : ""));
		mahnParameter.put("<Mmahndat2>", (rtfs[cMMAHNDAT2].getText().trim().length()==10 ? rtfs[cMMAHNDAT2].getText().trim() : ""));
//		mahnParameter.put("<Mmahndat3>", (rtfs[10].getText().trim().length()==10 ? rtfs[8].getText().trim() : ""));      Lemmi Fehler: die letzte 8 ist falsch
		mahnParameter.put("<Mmahndat3>", (rtfs[cMMAHNDAT3].getText().trim().length()==10 ? rtfs[cMMAHNDAT3].getText().trim() : ""));  // Lemmi 20110102: obigen Fehler korrigiert
		String datei = OffenePosten.progHome+"vorlagen/"+OffenePosten.aktIK+"/Mahnung"+Integer.toString(aktuelleMahnstufe)+".ott";
		try{
			starteMahnDruck(datei);
			if((Boolean) OffenePosten.mahnParameter.get("inofficestarten")){
				if(textDocument != null){
					textDocument.getFrame().getXFrame().getContainerWindow().setVisible(true);	
				}else{
					JOptionPane.showMessageDialog(null,"Kann den OpenOffice-Writer nicht starten 'textDocument' ist bereits null" );
				}
			}else{
				int exemplare = (Integer) OffenePosten.mahnParameter.get("exemplare");
				Thread.sleep(100);
				PrintProperties printprop = new PrintProperties ((short)exemplare,null);
				textDocument.getPrintService().print(printprop);
				Thread.sleep(200);
				textDocument.close();
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null,"Fehler im OpenOffice-System, Mahnung kann nicht gedruckt werden");
		}
		int row = tab.getSelectedRow();
		if(row < 0){
			JOptionPane.showMessageDialog(null, "Mahndaten können nicht in die Tabelle geschrieben werden bitte manuell eintragen!");
			return;
		}
		
		//int id = (Integer)tabmod.getValueAt(tab.convertRowIndexToModel(row), 15);
		int id = (Integer)tabmod.getValueAt(tab.convertRowIndexToModel(row), eSpalte.cID.iValue);
		
		String cmd = "";
		switch(aktuelleMahnstufe){
			case 1: 
				cmd = "update rliste set mahndat1='"+DatFunk.sDatInSQL(DatFunk.sHeute())+"' where id='"+Integer.toString(id)+"' LIMIT 1";
				break;
			case 2:
				cmd = "update rliste set mahndat2='"+DatFunk.sDatInSQL(DatFunk.sHeute())+"' where id='"+Integer.toString(id)+"' LIMIT 1";
				break;
			case 3:
				cmd = "update rliste set mahndat3='"+DatFunk.sDatInSQL(DatFunk.sHeute())+"' where id='"+Integer.toString(id)+"' LIMIT 1";
				break;
		}  // end switch
		if(!OffenePosten.testcase){
			SqlInfo.sqlAusfuehren(cmd);			
		}
		TableTool.loescheRowAusModel(tab, row);
	}
	
	private void initHashMap(){
		mahnParameter.put("<Mname1>", "");		
		mahnParameter.put("<Mname2>", "");
		mahnParameter.put("<Mstrasse>", "");
		mahnParameter.put("<Mplzort>", "");
		mahnParameter.put("<Mrnummer>", "");
		mahnParameter.put("<Mrdatum>", "");
		mahnParameter.put("<Mrbetrag>", "");
		mahnParameter.put("<Mroffen>", "");
		mahnParameter.put("<Mheute>", "");
		mahnParameter.put("<Mmahndat1>", "");
		mahnParameter.put("<Mmahndat2>", "");
		mahnParameter.put("<Mmahndat3>", "");
	}
	
	// Lemmi-Doku: lösche alle Eingebfelder auf der linken Seite
	private void doAllesAufNull(){
		for(int i = 0;i < rtfs.length; i++){
			if(i==cMRDATUM || i==cMMAHNDAT1 || i==cMMAHNDAT2 || i==cMMAHNDAT3 ){  // Lemmi-Doku: Das sind Datums-Felder
				rtfs[i].setText("  .  .    ");
			}else{								// Lemmi Doku: Das sind die Text-Eingabefelder
				rtfs[i].setText("");				
			}
		}
		cbMahnsperre.setSelected(false);
		tabmod.setRowCount(0);
		tab.validate();
		tab.repaint();
	}
	
	
	// Lemmi Doku: die hinterlegten Suchkriterien für die einzelnen Mahnstufen
	private void doSuchen(){
		String nichtvorDatum = eltern.getNotBefore();
		int frist1 = eltern.getFrist(1);
		int frist2 = eltern.getFrist(2);
		int frist3 = eltern.getFrist(3);
		if(frist1 < 0 || frist2 < 0 || frist3 < 0){
			return;
		}

		String cmd = "";
		String vergleichsdatum = "";
		switch(aktuelleMahnstufe){
		case 1:
			vergleichsdatum = DatFunk.sDatInSQL( DatFunk.sDatPlusTage(DatFunk.sHeute(), (frist1*-1)) );
			//cmd = "select * from rliste where (r_offen > '0.00' AND r_datum <='"+vergleichsdatum+"' AND r_datum >='"+nichtvorDatum+"')";
			cmd = "select * from rliste where (r_offen > '0.00' AND r_datum <='"+vergleichsdatum+"' AND r_datum >='"+nichtvorDatum+"' AND mahndat1 IS NULL)";
			starteSuche(cmd);
			break;
		case 2:
			vergleichsdatum = DatFunk.sDatInSQL( DatFunk.sDatPlusTage(DatFunk.sHeute(), (frist2*-1)) );
			//cmd = "select * from rliste where (r_offen > '0.00' AND r_datum <='"+vergleichsdatum+"' AND r_datum >='"+nichtvorDatum+"')";
			cmd = "select * from rliste where (r_offen > '0.00' AND mahndat1 <='"+vergleichsdatum+"' AND r_datum >='"+nichtvorDatum+"' AND mahndat2 IS NULL)";
			starteSuche(cmd);
			break;
		case 3:
			vergleichsdatum = DatFunk.sDatInSQL( DatFunk.sDatPlusTage(DatFunk.sHeute(), (frist3*-1)) );
			//cmd = "select * from rliste where (r_offen > '0.00' AND r_datum <='"+vergleichsdatum+"' AND r_datum >='"+nichtvorDatum+"')";
			cmd = "select * from rliste where (r_offen > '0.00' AND mahndat2 <='"+vergleichsdatum+"' AND r_datum >='"+nichtvorDatum+"' AND mahndat3 IS NULL)";
			starteSuche(cmd);
			break;
		case 4:
			vergleichsdatum = DatFunk.sDatInSQL( DatFunk.sDatPlusTage(DatFunk.sHeute(), (frist3*-1)) );
			//cmd = "select * from rliste where (r_offen > '0.00' AND r_datum <='"+vergleichsdatum+"' AND r_datum >='"+nichtvorDatum+"')";
			cmd = "select * from rliste where (r_offen > '0.00' AND mahndat3 <='"+vergleichsdatum+"' AND r_datum >='"+nichtvorDatum+"')";
			starteSuche(cmd);
			break;

		}
	}
	
	private void starteSuche(String sstmt){
		tabmod.setRowCount(0);
		tab.validate();
		tab.repaint();
		Statement stmt = null;
		ResultSet rs = null;

			
		try {
			stmt =  OffenePosten.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			
			
			rs = stmt.executeQuery(sstmt);
			Vector<Object> vec = new Vector<Object>();
			int durchlauf = 0;
			while(rs.next()){
				vec.clear();
				// Lemmi 20101202: Fixe Spalten-Nummern durch lesbare Parameter ersetzt
				vec.add( (rs.getString("x_nummer")==null ? rs.getString("r_nummer") : rs.getString("x_nummer"))); //"x_nummer"
				vec.add(rs.getInt("r_nummer")); //r_nummer
				vec.add(rs.getDate("r_datum")); // r_datum
				vec.add( (rs.getString("r_kasse")==null ? "" : rs.getString("r_kasse")) );// r_kasse
				vec.add( (rs.getString("r_name")==null ? "" : rs.getString("r_name")) );//r_name
				vec.add( (rs.getString("r_klasse")==null ? "" : rs.getString("r_klasse")) );//r_klasse
				vec.add(rs.getBigDecimal("r_betrag").doubleValue());//r_betrag
				vec.add(rs.getBigDecimal("r_offen").doubleValue());//r_offen
				vec.add(rs.getDate("r_bezdatum"));//r_bezdatum
				vec.add(rs.getDate("r_stornodat"));//r_stornodat
				vec.add(rs.getBigDecimal("r_zuzahl").doubleValue());//r_zuzahl
				vec.add(rs.getDate("mahndat1"));//mahndat1
				vec.add(rs.getDate("mahndat2"));//mahndat2
				vec.add(rs.getDate("mahndat3"));//mahndat3
				vec.add( (rs.getString("mahnsperr")==null ?  Boolean.FALSE : (rs.getString("mahnsperr").equals("T") ? Boolean.TRUE : Boolean.FALSE)) );//mahnsperr
				vec.add( (rs.getString("pat_intern")==null ? "" : rs.getString("pat_intern")) );//pat_intern
				vec.add( (rs.getString("ikktraeger")==null ? "" : rs.getString("ikktraeger")));//ikktraeger
				vec.add(rs.getInt("id"));//id
				
				tabmod.addRow( (Vector<?>) vec.clone());
				if(durchlauf>200){
					try {
						tab.validate();
						tab.repaint();
						Thread.sleep(100);
						durchlauf = 0;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
			tab.validate();
			tab.repaint();
			if(tab.getRowCount() > 0){
				tab.setRowSelectionInterval(0, 0);
			}
			
		}catch(SQLException ev){
			System.out.println("SQLException: " + ev.getMessage());
			System.out.println("SQLState: " + ev.getSQLState());
			System.out.println("VendorError: " + ev.getErrorCode());
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
	

	class MyMahnungenTableModel extends DefaultTableModel{
	
		private static final long serialVersionUID = 1L;

/* Lemmi 20101220: ausgeklammert, weil hier nicht verwendet		
		public Class<?> getColumnClass(int columnIndex) {
			switch(columnIndex){
			case 0:
				return Integer.class;
			case 1:
				return Date.class;				
			case 2:
				return String.class;
			case 3:
				return String.class;				
			case 4:
				return String.class;
			case 5:
				return Double.class;				
			case 6:
				return Double.class;
			case 7:
				return Date.class;				
			case 8:
				return Double.class;				
			case 9:
				return Date.class;
			case 10:
				return Date.class;				
			case 11:
				return Date.class;				
			case 12:
				return Boolean.class;				
			case 13:
				return String.class;				
			case 14:
				return String.class;				
			case 15:
				return Integer.class;				

			}
		   return String.class;
	    }
*/
		public boolean isCellEditable(int row, int col) {
//			if(col==12){  // Lemmi Frage: was immer hier mal mit der Spalte 12 gemeint war ???? Nach alter rListe ist es die Mahnsperre !
			if ( col == eSpalte.cMAHNSPERR.iValue ) {
				return true;				
			}
			return false;
		}
		   
	}
	
	/*******************************/

	class MahnungListSelectionHandler implements ListSelectionListener {
		
	    public void valueChanged(ListSelectionEvent e) {
	        ListSelectionModel lsm = (ListSelectionModel)e.getSource();
	        boolean isAdjusting = e.getValueIsAdjusting();
	        if(isAdjusting){
	        	return;
	        }
	        if (lsm.isSelectionEmpty()) {

	        } else {
	            int minIndex = lsm.getMinSelectionIndex();
	            int maxIndex = lsm.getMaxSelectionIndex();
	            for (int i = minIndex; i <= maxIndex; i++) {
	                if (lsm.isSelectedIndex(i)) {
	                	untersucheSatz();
	                    break;
	                }
	            }
	        }

	    }
	}
	private void untersucheSatz(){
		int row = tab.getSelectedRow();
		if(row < 0){
			JOptionPane.showMessageDialog(null,"Keine offene Rechnung ausgewählt");
			return;
		}
		// Lemmi 20101220: Ersetzte harten Spaltenwert gegen sprechenden Parameter
//		String rnr = tabmod.getValueAt( tab.convertRowIndexToModel(row), 0).toString();
		String rnr = tabmod.getValueAt( tab.convertRowIndexToModel(row), eSpalte.cR_NUMMER.iValue).toString();
		 
		if ( rnr.equals("0") ){ 		// Lemmi 20101220: neu: Abwicklung der RGR und AFR -Rechnungen
			String strPatIntern = tabmod.getValueAt( tab.convertRowIndexToModel(row), eSpalte.cPATINTERN.iValue).toString();
			String cmd = "select pat_intern from pat5 where pat_intern = '" + strPatIntern + "' LIMIT 1";
			//System.out.println(cmd);
			if(SqlInfo.gibtsSchon(cmd)){
				OffenePosten.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
				try{
					doAdressePat( strPatIntern, row );
				}catch(Exception ex){
					ex.printStackTrace();
					JOptionPane.showMessageDialog(null,"Fehler beim Bezug der Rechnungsdaten (RGR & AFR)");
				}
				OffenePosten.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}	
		} else {  // Abwicklung der KK-Rechnungen
		
			String cmd = "select rnummer from faktura where rnummer = '"+rnr+"' LIMIT 1";
			//System.out.println(cmd);
			if(SqlInfo.gibtsSchon(cmd)){
				OffenePosten.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
				try{
					doFakturaGedoense(rnr,row);
				}catch(Exception ex){
					ex.printStackTrace();
					JOptionPane.showMessageDialog(null,"Fehler beim Bezug der Rechnungsdaten (neu)");
				}
				OffenePosten.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				// bereits in der neuen faktura Datenbank enthalten also kann man sich den mit mit dbf&co sparen
			}else{
				OffenePosten.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
				try{
					doDbfGedoense(rnr,row);
				}catch(Exception ex){
					ex.printStackTrace();
					JOptionPane.showMessageDialog(null,"Fehler beim Bezug der Rechnungsdaten (alt)");
				}
				OffenePosten.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		}
	}
	
	// Lemmi Doku: Füllt die Textfelder links mit Adressdaten aus der Tabelle "faktura" ?
	// Lemmi 20101220: In der ganzen Routine harte Spalten-Zahlen ersetzt durch "eSpalte.c??????.iValue"
	private void doFakturaGedoense(String rnr,int row){
		Vector<Vector<String>> vecx =
			SqlInfo.holeFelder("select kassen_nam,kassen_na2,strasse,plz,ort from faktura where rnummer='"+
					rnr+"' and lfnr='0' LIMIT 1");
		if(vecx.size() <= 0){
			JOptionPane.showMessageDialog(null, "Rechnungsdaten können nicht ermittelt werden");
			return;
		}
		rtfs[cMNAME1].setText( vecx.get(0).get(0));
		rtfs[cMNAME2].setText( vecx.get(0).get(1));
		rtfs[cMSTRASSE].setText( vecx.get(0).get(2));
		rtfs[cMPLZORT].setText( vecx.get(0).get(3)+" "+vecx.get(0).get(4));
		
		rtfs[cMRNUMMER].setText( tabmod.getValueAt(tab.convertRowIndexToModel(row), eSpalte.cR_NUMMER.iValue).toString() );
		rtfs[cMRDATUM].setText( DatFunk.sDatInDeutsch(tabmod.getValueAt(tab.convertRowIndexToModel(row), eSpalte.cR_DATUM.iValue).toString()) );
		rtfs[cMRBETRAG].setText( dcf.format((Double)tabmod.getValueAt(tab.convertRowIndexToModel(row), eSpalte.cR_BETRAG.iValue) ) );
		rtfs[cMROFFEN].setText( dcf.format((Double)tabmod.getValueAt(tab.convertRowIndexToModel(row), eSpalte.cR_OFFEN.iValue) ) );
		
		Date test = (Date)tabmod.getValueAt(tab.convertRowIndexToModel(row), eSpalte.cMAHNDAT1.iValue);
		if(test==null){
			rtfs[cMMAHNDAT1].setText("  .  .    ");
		}else if(test.toString().trim().length() != 10){
			rtfs[cMMAHNDAT1].setText("  .  .    ");	
		}else{
			rtfs[cMMAHNDAT1].setText(DatFunk.sDatInDeutsch(test.toString()));
		}
		
		test = (Date)tabmod.getValueAt(tab.convertRowIndexToModel(row), eSpalte.cMAHNDAT2.iValue);
		if(test==null){
			rtfs[cMMAHNDAT2].setText("  .  .    ");
		}else if(test.toString().trim().length() != 10){
			rtfs[cMMAHNDAT2].setText("  .  .    ");	
		}else{
			rtfs[cMMAHNDAT2].setText(DatFunk.sDatInDeutsch(test.toString()));
		}
		test = (Date)tabmod.getValueAt(tab.convertRowIndexToModel(row), eSpalte.cMAHNDAT3.iValue);
		if(test==null){
			rtfs[cMMAHNDAT3].setText("  .  .    ");
		}else if(test.toString().trim().length() != 10){
			rtfs[cMMAHNDAT3].setText("  .  .    ");
		}else{
			rtfs[cMMAHNDAT3].setText(DatFunk.sDatInDeutsch(test.toString()));
		}
		cbMahnsperre.setSelected( (Boolean)tabmod.getValueAt(tab.convertRowIndexToModel(row), eSpalte.cMAHNSPERR.iValue) );
		
	}

	// Lemmi 20101220: Neue Routine, analog zu "doFakturaGedoense"
	// Lemmi Doku: Für RGR und AFR: Füllt die Textfelder links mit Adressdaten aus der Tabelle "pat5" ?
	private void doAdressePat( String strPatIntern, int row ){
		Vector<Vector<String>> vecx =
//			SqlInfo.holeFelder("select kassen_nam,kassen_na2,strasse,plz,ort from faktura where rnummer='"+
//					rnr+"' and lfnr='0' LIMIT 1");
			SqlInfo.holeFelder("select anrede, v_name, n_name, strasse,plz,ort from pat5 where pat_intern='" +
					strPatIntern + "' LIMIT 1");
		if(vecx.size() <= 0){
			JOptionPane.showMessageDialog(null, "Rechnungsdaten können nicht ermittelt werden");
			return;
		}
		rtfs[cMNAME1].setText( vecx.get(0).get(0));								// anrede
		rtfs[cMNAME2].setText( vecx.get(0).get(1) + " " + vecx.get(0).get(2));  	// v_name n_name
		rtfs[cMSTRASSE].setText( vecx.get(0).get(3));								// strasse
		rtfs[cMPLZORT].setText( vecx.get(0).get(4) +" " + vecx.get(0).get(5)); 	// plz ort
		
		rtfs[cMRNUMMER].setText( (String)tabmod.getValueAt(tab.convertRowIndexToModel(row), eSpalte.cX_NUMMER.iValue) );
		rtfs[cMRDATUM].setText( DatFunk.sDatInDeutsch(tabmod.getValueAt(tab.convertRowIndexToModel(row), eSpalte.cR_DATUM.iValue).toString()) );
		rtfs[cMRBETRAG].setText( dcf.format((Double)tabmod.getValueAt(tab.convertRowIndexToModel(row), eSpalte.cR_BETRAG.iValue) ) );
		rtfs[cMROFFEN].setText( dcf.format((Double)tabmod.getValueAt(tab.convertRowIndexToModel(row), eSpalte.cR_OFFEN.iValue) ) );
		
		Date test = (Date)tabmod.getValueAt(tab.convertRowIndexToModel(row), eSpalte.cMAHNDAT1.iValue);
		if(test==null){
			rtfs[cMMAHNDAT1].setText("  .  .    ");
		}else if(test.toString().trim().length() != 10){
			rtfs[cMMAHNDAT1].setText("  .  .    ");	
		}else{
			rtfs[cMMAHNDAT1].setText(DatFunk.sDatInDeutsch(test.toString()));
		}
		
		test = (Date)tabmod.getValueAt(tab.convertRowIndexToModel(row), eSpalte.cMAHNDAT2.iValue);
		if(test==null){
			rtfs[cMMAHNDAT2].setText("  .  .    ");
		}else if(test.toString().trim().length() != 10){
			rtfs[cMMAHNDAT2].setText("  .  .    ");	
		}else{
			rtfs[cMMAHNDAT2].setText(DatFunk.sDatInDeutsch(test.toString()));
		}
		test = (Date)tabmod.getValueAt(tab.convertRowIndexToModel(row), eSpalte.cMAHNDAT3.iValue);
		if(test==null){
			rtfs[cMMAHNDAT3].setText("  .  .    ");
		}else if(test.toString().trim().length() != 10){
			rtfs[cMMAHNDAT3].setText("  .  .    ");
		}else{
			rtfs[cMMAHNDAT3].setText(DatFunk.sDatInDeutsch(test.toString()));
		}
		cbMahnsperre.setSelected( (Boolean)tabmod.getValueAt(tab.convertRowIndexToModel(row), eSpalte.cMAHNSPERR.iValue) );
		
	}

	
	// Lemmi 20101220: In der ganzen Routine harte Spalten-Zahlen ersetzt durch "eSpalte.c??????.iValue"
	private void doDbfGedoense(String rnr, int row){
		String rdatei = ((String)OffenePosten.mahnParameter.get("diralterechnungen"))+rnr+".dbf";
		f = new File(rdatei);
		if(! f.exists()){
			JOptionPane.showMessageDialog(null,"Die 'ehemalige' Rechnungsdatei -> "+rdatei+" <- existiert nicht!\n"+
					"Maschinelle Mahnung ist deshalb nicht möglich");
			return;
		}
		try {
			dbfreader = new DBFReader(rdatei);
			if(dbfreader.hasNextRecord()){
				Object aobj[] = dbfreader.nextRecord();
				try{
					rtfs[cMNAME1].setText( StringTools.EGross((String) aobj[0]) );
					rtfs[cMNAME2].setText( StringTools.EGross((String) aobj[1]) );
					rtfs[cMSTRASSE].setText( StringTools.EGross((String) aobj[2]) );
					rtfs[cMPLZORT].setText( ((String) aobj[3])+" "+StringTools.EGross((String) aobj[4]) );
					dbfreader.close();
					dbfreader = null;
				}catch(Exception ex){
					ex.printStackTrace();
					JOptionPane.showConfirmDialog(null,"Keine verwertbaren Adesssdaten vorhanden in 'ehemaliger' Rechnungsdatei "+rdatei);					
				}
				rtfs[cMRNUMMER].setText( tabmod.getValueAt(tab.convertRowIndexToModel(row), eSpalte.cR_NUMMER.iValue).toString() );
				rtfs[cMRDATUM].setText( DatFunk.sDatInDeutsch(tabmod.getValueAt(tab.convertRowIndexToModel(row), eSpalte.cR_DATUM.iValue).toString()) );
				rtfs[cMRBETRAG].setText( dcf.format((Double)tabmod.getValueAt(tab.convertRowIndexToModel(row), eSpalte.cR_BETRAG.iValue) ) );
				rtfs[cMROFFEN].setText( dcf.format((Double)tabmod.getValueAt(tab.convertRowIndexToModel(row), eSpalte.cR_OFFEN.iValue) ) );
				
				Date test = (Date)tabmod.getValueAt(tab.convertRowIndexToModel(row), eSpalte.cMAHNDAT1.iValue);
				if(test==null){
					rtfs[cMMAHNDAT1].setText("  .  .    ");
				}else if(test.toString().trim().length() != 10){
					rtfs[cMMAHNDAT1].setText("  .  .    ");	
				}else{
					rtfs[cMMAHNDAT1].setText(DatFunk.sDatInDeutsch(test.toString()));
				}
				
				test = (Date)tabmod.getValueAt(tab.convertRowIndexToModel(row), eSpalte.cMAHNDAT2.iValue);
				if(test==null){
					rtfs[cMMAHNDAT2].setText("  .  .    ");
				}else if(test.toString().trim().length() != 10){
					rtfs[cMMAHNDAT2].setText("  .  .    ");	
				}else{
					rtfs[cMMAHNDAT2].setText(DatFunk.sDatInDeutsch(test.toString()));
				}
				test = (Date)tabmod.getValueAt(tab.convertRowIndexToModel(row), eSpalte.cMAHNDAT3.iValue);
				if(test==null){
					rtfs[cMMAHNDAT3].setText("  .  .    ");
				}else if(test.toString().trim().length() != 10){
					rtfs[cMMAHNDAT3].setText("  .  .    ");
				}else{
					rtfs[cMMAHNDAT3].setText(DatFunk.sDatInDeutsch(test.toString()));
				}
				cbMahnsperre.setSelected( (Boolean)tabmod.getValueAt(tab.convertRowIndexToModel(row), eSpalte.cMAHNSPERR.iValue) );
				
			}else{
				JOptionPane.showConfirmDialog(null,"Keine verwertbaren Adesssdaten vorhanden in 'ehemaliger' Rechnungsdatei "+rdatei);
			}
			
			
		} catch (JDBFException e) {
			e.printStackTrace();
		}
		
	}
	/*******************************/
	private void starteMahnDruck(String url){
		IDocumentService documentService = null;;
		//System.out.println("Starte Datei -> "+url);
		if(!OffenePosten.officeapplication.isActive()){
			OffenePosten.starteOfficeApplication();
		}
		try {
			documentService = OffenePosten.officeapplication.getDocumentService();
		} catch (OfficeApplicationException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Fehler im OpenOffice-System - Ausfallrechnung kann nicht erstellt werden");
			return;
		}
		
        IDocumentDescriptor docdescript = new DocumentDescriptor();
       	docdescript.setHidden(false);
        docdescript.setAsTemplate(true);
		IDocument document = null;
		//ITextTable[] tbl = null;
		try {
			document = documentService.loadDocument(url,docdescript);
		} catch (NOAException e) {

			e.printStackTrace();
		}
		textDocument = (ITextDocument)document;
		Tools.OOTools.druckerSetzen(textDocument, (String)OffenePosten.mahnParameter.get("drucker"));
		ITextFieldService textFieldService = textDocument.getTextFieldService();
		ITextField[] placeholders = null;
		try {
			placeholders = textFieldService.getPlaceholderFields();
		} catch (TextException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < placeholders.length; i++) {
			//boolean loeschen = false;
			boolean schonersetzt = false;
			String placeholderDisplayText = placeholders[i].getDisplayText().toLowerCase();
			////System.out.println(placeholderDisplayText);	
		    /*****************/			
			Set entries = mahnParameter.entrySet();
		    Iterator it = entries.iterator();
		    while (it.hasNext()) {
		      Map.Entry entry = (Map.Entry) it.next();
		      if(((String)entry.getKey()).toLowerCase().equals(placeholderDisplayText)){
		    	  placeholders[i].getTextRange().setText(((String)entry.getValue()));
		    	  schonersetzt = true;
		    	  break;
		      }
		    }
		    /*****************/
		    
		    if(!schonersetzt){
		    	OOTools.loescheLeerenPlatzhalter(textDocument, placeholders[i]);
		    }
		    
		    /*****************/
		}
		

	}
}
