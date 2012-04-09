package opRgaf;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
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


import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class OpRgafMahnungen extends JXPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7011413450109922373L;
	ActionListener al = null;
	OpRgafTab eltern = null;
	JXPanel content = null;

	ButtonGroup bgroup = new ButtonGroup();
	ButtonGroup artgroup = new ButtonGroup();
	JRtaRadioButton[] rbMahnart = {null,null,null,null,null,null,null};
	JButton suchen = null;
	JButton kopie = null;
	
	JRtaTextField[] rtfs = {null,null,null,null,null,null,null,null,null,null,null,null};
	JRtaCheckBox cbMahnsperre = null;
	int aktuelleMahnstufe = 1;
	
	MyMahnungenTableModel tabmod = null;
	JXTable tab = null;
	
	JButton[] mahnbuts = {null,null,null};


	File f = null;
	Font fontfett = new Font("Tahoma",Font.BOLD,10);
	
	DecimalFormat dcf = new DecimalFormat("###0.00");
	
	HashMap<String,String> mahnParameter = new HashMap<String,String>(); 
	ITextDocument textDocument;
	
	final String stmtString = 
		"select concat(t2.n_name, ', ',t2.v_name,', ',DATE_FORMAT(geboren,'%d.%m.%Y'))," +
		"t1.rnr,t1.rdatum,t1.rgesamt,t1.roffen,t1.rpbetrag,t1.rbezdatum,t1.rmahndat1,t1.rmahndat2,t3.kassen_nam1,t1.reznr,t1.id "+
		"from rgaffaktura as t1 inner join pat5 as t2 on (t1.pat_intern = t2.pat_intern) "+
		"left join kass_adr as t3 ON ( t2.kassenid = t3.id )";
	int gefunden;
	String[] spalten = {"Name,Vorname,Geburtstag","Rechn.Nr.","Rechn.Datum","Gesamtbetrag","Offen","Bearb.Gebühr","Bezahldatum","Mahndatum1","Mahndatum2","Krankenkasse","RezeptNr.","id"};
	String[] colnamen ={"nix","rnr","rdatum","rgesamt","roffen","rpbetrag","rbezdatum","rmahndat1","rmahndat2","nix","RezeptNr.","id"};
	
	
	public OpRgafMahnungen(OpRgafTab xeltern){
		super();
		this.eltern = xeltern;
		this.setLayout(new BorderLayout());
		activateActionListener();
		add(getContent(),BorderLayout.CENTER);
		
		
	}
	
	private JXPanel getContent(){
		String xwerte = "fill:0:grow(0.5),fill:0:grow(0.5),2dlu";
		//                 1  2  3   4  5   6      7
		String ywerte = "0dlu,p,0dlu,p,0dlu,p,fill:0:grow(1.0)";
		content = new JXPanel();
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		content.setLayout(lay);
		
		content.add(getRadioPanel(),cc.xyw(1,2,2,CellConstraints.FILL,CellConstraints.TOP));
		content.add(getRechnungDatenPanel(),cc.xy(1,4));
		content.add(getTablePanel(),cc.xy(2,4,CellConstraints.FILL,CellConstraints.FILL));
		content.add(getButtonPanel(),cc.xy(1,6,CellConstraints.FILL,CellConstraints.TOP));
		content.validate();
		return content;
	}
	private JXPanel getButtonPanel(){
		JXPanel buttonpan = new JXPanel();
		//                     1            2          3            4           5           6          7
		String xwerte = "fill:0:grow(0.25),80dlu,fill:0:grow(0.25),80dlu,fill:0:grow(0.25),80dlu,fill:0:grow(0.25)";
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
	private JXPanel getTablePanel(){
		JXPanel tablepan = new JXPanel();
		String xwerte = "fill:0:grow(1.0)";
		String ywerte = "fill:0:grow(1.0)";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		tablepan.setLayout(lay);
		
		tabmod = new MyMahnungenTableModel();
		/*
		Vector<Vector<String>> felder = SqlInfo.holeFelder("describe rliste");
		String[] spalten = new String[felder.size()];
		for(int i= 0; i < felder.size();i++){
			spalten[i] = felder.get(i).get(0);
		}
		*/
		tabmod.setColumnIdentifiers(spalten);
		tab = new JXTable(tabmod);
		tab.setHorizontalScrollEnabled(true);
		DateTableCellEditor tble = new DateTableCellEditor();
		tab.getColumn(1).setCellRenderer(new Tools.MitteRenderer());
		
		tab.getColumn(2).setCellEditor(tble);
		
		tab.getColumn(3).setCellRenderer(new Tools.DoubleTableCellRenderer());
		tab.getColumn(3).setCellEditor(new Tools.DblCellEditor());
		
		tab.getColumn(4).setCellRenderer(new Tools.DoubleTableCellRenderer());
		tab.getColumn(4).setCellEditor(new Tools.DblCellEditor());

		tab.getColumn(5).setCellRenderer(new Tools.DoubleTableCellRenderer());
		tab.getColumn(5).setCellEditor(new Tools.DblCellEditor());

		tab.getColumn(6).setCellEditor(tble);
		tab.getColumn(7).setCellEditor(tble);
		tab.getColumn(8).setCellEditor(tble);
		tab.getColumn(10).setMaxWidth(50);
		tab.getColumn(11).setMaxWidth(50);
		tab.getSelectionModel().addListSelectionListener( new MahnungListSelectionHandler());
		tab.setHighlighters(HighlighterFactory.createSimpleStriping(HighlighterFactory.CLASSIC_LINE_PRINTER));
		
		JScrollPane jscr = JCompTools.getTransparentScrollPane(tab);
		jscr.validate();
		
		tablepan.add(jscr,cc.xy(1,1));
		
		tablepan.validate();
		return tablepan;
	}
	
	private JXPanel getRechnungDatenPanel(){
		JXPanel rechnungpan = new JXPanel();
		
		//                1     2     3    4         5     6    7     8     9    10    11   12     13
		String xwerte = "15dlu,60dlu,5dlu,100dlu:g,15dlu";
		//                1    2  3   4  5   6  7   8  9  10 11  12 13  14 15  16 17  18  19
		String ywerte = "25dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,"+
		//20 21 22 23  24 25
		"p,2dlu,p,2dlu,p,2dlu";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		rechnungpan.setLayout(lay);

		JLabel lab = new JLabel("Einzelheiten der ausgewählten Rechnung");
		lab.setForeground(Color.BLUE);
		rechnungpan.add(lab,cc.xyw(2,1,3,CellConstraints.DEFAULT,CellConstraints.CENTER));
		
		lab = new JLabel("Name1");
		rechnungpan.add(lab,cc.xy(2,2));
		rtfs[0] = new JRtaTextField("nix",true);
		rechnungpan.add(rtfs[0],cc.xy(4,2));
		
		lab = new JLabel("Name2");
		rechnungpan.add(lab,cc.xy(2,4));
		rtfs[1] = new JRtaTextField("nix",true);
		rechnungpan.add(rtfs[1],cc.xy(4,4));

		lab = new JLabel("Strasse");
		rechnungpan.add(lab,cc.xy(2,6));
		rtfs[2] = new JRtaTextField("nix",true);
		rechnungpan.add(rtfs[2],cc.xy(4,6));

		lab = new JLabel("PLZ/Ort");
		rechnungpan.add(lab,cc.xy(2,8));
		rtfs[3] = new JRtaTextField("nix",true);
		rechnungpan.add(rtfs[3],cc.xy(4,8));

		lab = new JLabel("R-Nr.");
		rechnungpan.add(lab,cc.xy(2,10));
		rtfs[4] = new JRtaTextField("nix",true);
		rtfs[4].setFont(fontfett);
		rechnungpan.add(rtfs[4],cc.xy(4,10));

		lab = new JLabel("R-Datum");
		rechnungpan.add(lab,cc.xy(2,12));
		rtfs[5] = new JRtaTextField("DATUM",true);
		rtfs[5].setFont(fontfett);
		rechnungpan.add(rtfs[5],cc.xy(4,12));

		lab = new JLabel("R-Betrag");
		rechnungpan.add(lab,cc.xy(2,14));
		rtfs[6] = new JRtaTextField("F",true,"6.2","LINKS");
		rtfs[6].setFont(fontfett);
		rtfs[6].setForeground(Color.BLUE);
		rechnungpan.add(rtfs[6],cc.xy(4,14));
		
		lab = new JLabel("R-Offen");
		rechnungpan.add(lab,cc.xy(2,16));
		rtfs[7] = new JRtaTextField("F",true,"6.2","LINKS");
		rtfs[7].setFont(fontfett);
		rtfs[7].setForeground(Color.RED);
		rechnungpan.add(rtfs[7],cc.xy(4,16));
		
		lab = new JLabel("1. Mahnung");
		rechnungpan.add(lab,cc.xy(2,18));
		rtfs[8] = new JRtaTextField("DATUM",true);
		rtfs[8].setFont(fontfett);
		rechnungpan.add(rtfs[8],cc.xy(4,18));

		lab = new JLabel("2. Mahnung");
		rechnungpan.add(lab,cc.xy(2,20));
		rtfs[9] = new JRtaTextField("DATUM",true);
		rtfs[9].setFont(fontfett);
		rechnungpan.add(rtfs[9],cc.xy(4,20));
		/*
		lab = new JLabel("3. Mahnung");
		rechnungpan.add(lab,cc.xy(2,22));
		rtfs[10] = new JRtaTextField("DATUM",true);
		rtfs[10].setFont(fontfett);
		rechnungpan.add(rtfs[10],cc.xy(4,22));
		
		lab = new JLabel("Mahnsperre?");
		rechnungpan.add(lab,cc.xy(2,24));
		cbMahnsperre = new JRtaCheckBox("Mahnsperre verhängt");
		rechnungpan.add(cbMahnsperre,cc.xy(4,24));
		*/

		rechnungpan.validate();
		return rechnungpan;

	}
	private JXPanel getRadioPanel(){
		JXPanel radiopan = new JXPanel();
		radiopan.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		//                1     2     3    4    5     6    7     8     9    10       11   12     13
		String xwerte = "15dlu,150dlu,2dlu,60dlu,2dlu,60dlu,2dlu,60dlu,2dlu,60dlu:g,10dlu,30dlu,0dlu:g,5dlu";
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

		
		rbMahnart[3] = new JRtaRadioButton("Anwaltsliste");
		rbMahnart[3].setName("mahnung4");
		rbMahnart[3].addActionListener(al);
		bgroup.add(rbMahnart[3]);
		radiopan.add(rbMahnart[3],cc.xy(8,2));
		/********/
		JXPanel pan = new JXPanel();
		pan.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		//                1     2     3    4    5     6    7     8     9    10    11   12     13
		String xwerte2 = "2dlu,p,2dlu";
		//                1    2  3
		String ywerte2 = "p,p,p";
		FormLayout lay2 = new FormLayout(xwerte2,ywerte2);
		CellConstraints cc2 = new CellConstraints();
		pan.setLayout(lay2);

		rbMahnart[4] = new JRtaRadioButton("nur Ausfallrechnungen");
		rbMahnart[4].setName("nuraf5");
		//rbMahnart[4].addActionListener(al);
		artgroup.add(rbMahnart[4]);
		rbMahnart[4].setSelected(true);
		pan.add(rbMahnart[4],cc2.xy(2,1));
		rbMahnart[5] = new JRtaRadioButton("nur Rezeptgebührrech.");
		rbMahnart[5].setName("nurrg6");
		//rbMahnart[5].addActionListener(al);
		artgroup.add(rbMahnart[5]);
		pan.add(rbMahnart[5],cc2.xy(2,2));
		rbMahnart[6] = new JRtaRadioButton("beide Rechn.Arten");
		rbMahnart[6].setName("beides7");
		//rbMahnart[6].addActionListener(al);
		artgroup.add(rbMahnart[6]);
		pan.add(rbMahnart[6],cc2.xy(2,3));
		pan.validate();
		radiopan.add(pan,cc.xy(10,2,CellConstraints.FILL,CellConstraints.FILL));
		/********/
		
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
								OpRgaf.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
								doSuchen();	
							}catch(Exception ex){
								ex.printStackTrace();
								OpRgaf.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
								JOptionPane.showMessageDialog(null,"Fehler beim Bezug der Mahndaten in Mahnstufe "+Integer.toString(aktuelleMahnstufe));
							}
							OpRgaf.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
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
		if(row < tab.getRowCount()){
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
	private void doMahnungStarten(){
		/*
		if(cbMahnsperre.isSelected()){
			JOptionPane.showMessageDialog(null,"Diese Rechnung ist mit einer Mahnsperre belegt und kann deshalb nicht angemaht werden!");
			return;
		}
		*/
		initHashMap();
		mahnParameter.put("<Mname1>", rtfs[0].getText().trim());		
		mahnParameter.put("<Mname2>", rtfs[1].getText().trim());
		mahnParameter.put("<Mstrasse>", rtfs[2].getText().trim());
		mahnParameter.put("<Mplzort>", rtfs[3].getText().trim());
		mahnParameter.put("<Mrnummer>", rtfs[4].getText().trim());
		mahnParameter.put("<Mrdatum>", rtfs[5].getText().trim());
		mahnParameter.put("<Mrbetrag>", rtfs[6].getText().trim());
		mahnParameter.put("<Mroffen>", rtfs[7].getText().trim());
		mahnParameter.put("<Mheute>", DatFunk.sHeute());
		mahnParameter.put("<Mmahndat1>", (rtfs[8].getText().trim().length()==10 ? rtfs[8].getText().trim() : ""));
		mahnParameter.put("<Mmahndat2>", (rtfs[9].getText().trim().length()==10 ? rtfs[9].getText().trim() : ""));
		//mahnParameter.put("<Mmahndat3>", (rtfs[10].getText().trim().length()==10 ? rtfs[8].getText().trim() : ""));
		String datei = (String)OpRgaf.mahnParameter.get("formular"+Integer.toString(aktuelleMahnstufe));
		//String datei = OpRgaf.progHome+"vorlagen/"+OpRgaf.aktIK+"/RGAFMahnung"+Integer.toString(aktuelleMahnstufe)+".ott";
		try{
			starteMahnDruck(datei);
			if(textDocument != null){
				textDocument.getFrame().getXFrame().getContainerWindow().setVisible(true);	
			}else{
				JOptionPane.showMessageDialog(null,"Kann den OpenOffice-Writer nicht starten 'textDocument' ist bereits null" );
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
		int id = (Integer) tabmod.getValueAt(tab.convertRowIndexToModel(row), 11);
		String cmd = "";
		switch(aktuelleMahnstufe){
		case 1: 
			cmd = "update rgaffaktura set rmahndat1='"+DatFunk.sDatInSQL(DatFunk.sHeute())+"' where id='"+Integer.toString(id)+"' LIMIT 1";
			break;
		case 2:
			cmd = "update rgaffaktura set rmahndat2='"+DatFunk.sDatInSQL(DatFunk.sHeute())+"' where id='"+Integer.toString(id)+"' LIMIT 1";
			break;
		case 3:
			cmd = "update rgaffaktura set rahndat3='"+DatFunk.sDatInSQL(DatFunk.sHeute())+"' where id='"+Integer.toString(id)+"' LIMIT 1";
			break;
			
		}
		if(!OpRgaf.testcase){
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
	
	private void doAllesAufNull(){
		for(int i = 0;i < 11; i++){
			if(rtfs[i] != null){
				if(rtfs[i].getRtaType().equals("DATUM")){
					rtfs[i].setText("  .  .    ");
				}else{
					rtfs[i].setText("");				
				}
			}
		}
		//cbMahnsperre.setSelected(false);
		tabmod.setRowCount(0);
		tab.validate();
		tab.repaint();
	}
	private void doSuchen(){
		String nichtvorDatum = eltern.getNotBefore();
		int frist1 = (Integer) OpRgaf.mahnParameter.get("frist1");
		int frist2 = (Integer) OpRgaf.mahnParameter.get("frist2");
		int frist3 = (Integer) OpRgaf.mahnParameter.get("frist3");
		//int frist1 = eltern.getFrist(1);
		//int frist2 = eltern.getFrist(2);
		//int frist3 = eltern.getFrist(3);
		if(frist1 < 0 || frist2 < 0 || frist3 < 0){
			return;
		}

		String cmd = "";
		String vergleichsdatum = "";
		switch(aktuelleMahnstufe){
		case 1:
			vergleichsdatum = DatFunk.sDatInSQL( DatFunk.sDatPlusTage(DatFunk.sHeute(), (frist1*-1)) );
			//cmd = "select * from rliste where (r_offen > '0.00' AND r_datum <='"+vergleichsdatum+"' AND r_datum >='"+nichtvorDatum+"')";
			cmd = stmtString+" where (roffen > '0.00' AND rdatum <='"+vergleichsdatum+"' AND rdatum >='"+nichtvorDatum+"' AND rmahndat1 IS NULL "+testArt()+")";
			starteSuche(cmd);
			break;
		case 2:
			vergleichsdatum = DatFunk.sDatInSQL( DatFunk.sDatPlusTage(DatFunk.sHeute(), (frist2*-1)) );
			//cmd = "select * from rliste where (r_offen > '0.00' AND r_datum <='"+vergleichsdatum+"' AND r_datum >='"+nichtvorDatum+"')";
			cmd = stmtString+" where (roffen > '0.00' AND rmahndat1 <='"+vergleichsdatum+"' AND rdatum >='"+nichtvorDatum+"' AND rmahndat2 IS NULL "+testArt()+")";
			starteSuche(cmd);
			break;
		case 3:
			vergleichsdatum = DatFunk.sDatInSQL( DatFunk.sDatPlusTage(DatFunk.sHeute(), (frist3*-1)) );
			//cmd = "select * from rliste where (r_offen > '0.00' AND r_datum <='"+vergleichsdatum+"' AND r_datum >='"+nichtvorDatum+"')";
			cmd = "select * from rliste where (roffen > '0.00' AND rmahndat2 <='"+vergleichsdatum+"' AND rdatum >='"+nichtvorDatum+"' AND mahndat3 IS NULL "+testArt()+")";
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
	private String testArt(){
		if(this.rbMahnart[4].isSelected()){
			return " and rnr like 'AFR-%'";
		}else if(this.rbMahnart[5].isSelected()){
			return " and rnr like 'RGR-%'";
		}
		return "";
	}
	private void starteSuche(String sstmt){
		tabmod.setRowCount(0);
		tab.validate();
		//tab.repaint();
		Statement stmt = null;
		ResultSet rs = null;

			
		try {
			stmt =  OpRgaf.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			
			
			rs = stmt.executeQuery(sstmt);
			Vector<Object> vec = new Vector<Object>();
			int durchlauf = 0;
			ResultSetMetaData rsMetaData = null;
			while(rs.next()){
				vec.clear();
				rsMetaData = rs.getMetaData() ;
				int numberOfColumns = rsMetaData.getColumnCount()+1;
				 for(int i = 1 ; i < numberOfColumns;i++){
					 if(rsMetaData.getColumnClassName(i).toString().equals("java.lang.String")){
						 vec.add( (rs.getString(i)==null ? "" : rs.getString(i)) ); 
					 }else if(rsMetaData.getColumnClassName(i).toString().equals("java.math.BigDecimal")){
						 vec.add( rs.getBigDecimal(i).doubleValue() ); 
					 }else if(rsMetaData.getColumnClassName(i).toString().equals("java.sql.Date")){
						 vec.add( rs.getDate(i) ); 
					 }else if(rsMetaData.getColumnClassName(i).toString().equals("java.lang.Integer")){
						 vec.add( rs.getInt(i) );
					 }
					 //vec.add( (rs.getString(i)==null ? "" : rs.getString(i)) );//r_klasse
					 //System.out.println(rsMetaData.getColumnClassName(i));
				 }				
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
		   /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Class<?> getColumnClass(int columnIndex) {
			switch(columnIndex){
			case 0:
				return String.class;
			case 1:
				return String.class;				
			case 2:
				return Date.class;
			case 3:
				return Double.class;				
			case 4:
				return Double.class;
			case 5:
				return Double.class;
			case 6:
				return Date.class;
			case 7:
				return Date.class;
			case 8:
				return Date.class;
			case 9:
				return String.class;				
			case 10:
				return String.class;				
			case 11:
				return Integer.class;				
			}
		   return String.class;
	    }

		public boolean isCellEditable(int row, int col) {
			if(col > 1 && col < 9){
				return false;				
			}
			return false;		}
		   
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
		String id = tabmod.getValueAt( tab.convertRowIndexToModel(row)  ,11).toString();
		String cmd = //tabmod.getValueAt( tab.convertRowIndexToModel(row)  ,0).toString();
		"select t2.anrede,t2.n_name,t2.v_name,t2.strasse,t2.plz,t2.ort," +
		"t1.rnr,t1.rdatum,t1.rgesamt,t1.roffen,t1.rmahndat1,t1.rmahndat2 "+
		"from rgaffaktura as t1 inner join pat5 as t2 on (t1.pat_intern = t2.pat_intern) where t1.id='"+id+"' LIMIT 1";
		
		//String cmd = "select rnummer from faktura where rnummer = '"+rnr+"' LIMIT 1";
		//System.out.println(cmd);
		if(SqlInfo.gibtsSchon(cmd)){
			OpRgaf.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			try{
				doFakturaGedoense(id,SqlInfo.holeFelder(cmd));
			}catch(Exception ex){
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null,"Fehler beim Bezug der Rechnungsdaten (neu)");
			}
			OpRgaf.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			// bereits in der neuen faktura Datenbank enthalten also kann man sich den mit mit dbf&co sparen
		}else{
			OpRgaf.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			try{
				//doDbfGedoense(rnr,row);
			}catch(Exception ex){
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null,"Fehler beim Bezug der Rechnungsdaten (alt)");
			}
			OpRgaf.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}
	private void doFakturaGedoense(String rnr,Vector<Vector<String>> vecx){
		/*
		Vector<Vector<String>> vecx =
			SqlInfo.holeFelder("select kassen_nam,kassen_na2,strasse,plz,ort from faktura where rnummer='"+
					rnr+"' and lfnr='0' LIMIT 1");
		*/			
		if(vecx.size() <= 0){
			JOptionPane.showMessageDialog(null, "Rechnungsdaten können nicht ermittelt werden");
			return;
		}
		rtfs[0].setText( StringTools.EGross(vecx.get(0).get(0)) );
		rtfs[1].setText( StringTools.EGross(vecx.get(0).get(2))+" "+StringTools.EGross(vecx.get(0).get(1)) );
		rtfs[2].setText( StringTools.EGross(vecx.get(0).get(3)) );
		rtfs[3].setText( vecx.get(0).get(4)+" "+StringTools.EGross(vecx.get(0).get(5)) );
		rtfs[4].setText( vecx.get(0).get(6) );
		rtfs[5].setText( DatFunk.sDatInDeutsch(vecx.get(0).get(7)) );

		rtfs[6].setText( dcf.format(Double.parseDouble(vecx.get(0).get(8))));
		rtfs[7].setText( dcf.format(Double.parseDouble(vecx.get(0).get(9))));

		/*
		rtfs[4].setText( tabmod.getValueAt(tab.convertRowIndexToModel(row), 0).toString() );
		rtfs[5].setText( DatFunk.sDatInDeutsch(tabmod.getValueAt(tab.convertRowIndexToModel(row), 1).toString()) );
		rtfs[6].setText( dcf.format((Double)tabmod.getValueAt(tab.convertRowIndexToModel(row), 5) ) );
		rtfs[7].setText( dcf.format((Double)tabmod.getValueAt(tab.convertRowIndexToModel(row), 6) ) );
		*/
		
		String test = vecx.get(0).get(10);
		if(test==null){
			rtfs[8].setText("  .  .    ");
		}else if(test.toString().trim().length() != 10){
			rtfs[8].setText("  .  .    ");	
		}else{
			rtfs[8].setText(DatFunk.sDatInDeutsch(test.toString()));
		}
		
		test = vecx.get(0).get(11);
		if(test==null){
			rtfs[9].setText("  .  .    ");
		}else if(test.toString().trim().length() != 10){
			rtfs[9].setText("  .  .    ");	
		}else{
			rtfs[9].setText(DatFunk.sDatInDeutsch(test.toString()));
		}
		/*
		test = (Date)tabmod.getValueAt(tab.convertRowIndexToModel(row), 11);
		if(test==null){
			rtfs[10].setText("  .  .    ");
		}else if(test.toString().trim().length() != 10){
			rtfs[10].setText("  .  .    ");
		}else{
			rtfs[10].setText(DatFunk.sDatInDeutsch(test.toString()));
		}
		*/
		/*
		cbMahnsperre.setSelected( (Boolean)tabmod.getValueAt(tab.convertRowIndexToModel(row), 12) );
		*/


		
	}
	/*******************************/
	@SuppressWarnings("rawtypes")
	private void starteMahnDruck(String url){
		IDocumentService documentService = null;;
		//System.out.println("Starte Datei -> "+url);
		if(!OpRgaf.officeapplication.isActive()){
			OpRgaf.starteOfficeApplication();
		}
		try {
			documentService = OpRgaf.officeapplication.getDocumentService();
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
		Tools.OOTools.druckerSetzen(textDocument, (String)OpRgaf.mahnParameter.get("drucker"));
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
