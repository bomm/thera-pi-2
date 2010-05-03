package urlaubBeteiligung;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import oOorgTools.OOTools;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingworker.SwingWorker;

import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.spreadsheet.ISpreadsheetDocument;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.noa.NOAException;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.container.NoSuchElementException;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.IndexOutOfBoundsException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.sheet.XSheetCellCursor;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.sheet.XSpreadsheets;
import com.sun.star.uno.UnoRuntime;

import rehaInternalFrame.JBeteiligungInternal;
import sqlTools.SqlInfo;
import stammDatenTools.RezTools;
import systemTools.ButtonTools;
import systemTools.JRtaComboBox;
import systemTools.JRtaTextField;
import systemTools.JRtaCheckBox;
import terminKalender.DatFunk;
import terminKalender.ParameterLaden;

public class Beteiligung  extends JXPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5302438379722827660L;
	
	JBeteiligungInternal internal=null;
	JRtaComboBox jcmb = null;
	JLabel lab = null;
	JRtaTextField[] tfs = {null,null};
	JRtaTextField[] tfbeteil = {null,null,null};
	JRtaCheckBox chbbeteil = null;
	JXPanel content = null;
	JButton[] buts = {null};
	ActionListener al = null;
	KeyListener kl = null;
	boolean initok = false;
	String aktBehandlerNummer = "";
	JLabel progresstext1 = null;
	JProgressBar progressbar1 = null;
	final String copyright = "\u00AE"  ;

	int prozBehandl=0;
	int prozHb=0;
	int prozWeg=0;
	
	Vector<Vector<String>> veckolls = new Vector<Vector<String>>();  
	
	Vector<Vector<Vector<String>>> tagesvec = new Vector<Vector<Vector<String>>>(); 
	
	Vector<Integer[]> summenPos = new Vector<Integer[]>();
	DecimalFormat dcf = new DecimalFormat("##########0.00");
	
	int anzahlTagesBehandlungen = 0;
	int calcrow = 0;	
	ISpreadsheetDocument spreadsheetDocument = null;;
	IDocument document  = null;
	XSheetCellCursor cellCursor = null;
	XSpreadsheet spreadsheet = null;
	String sheetName;
	
	String[] kopfzeile = {"Patient","PG","Pos1","Pos2","Pos3","Pos4","HB","WG",
			"Preis1","Preis2","Preis3","Preis4","HB-Pr.","WG-Pr.","Summe","A"};
	String[] cols = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P"};

	
	
	public Beteiligung(JBeteiligungInternal bti){
		super();
		this.internal = bti;
		makeListeners();
		add(getContent(),BorderLayout.CENTER);
		initok = true;
	}

	private JXPanel getContent(){       //   1            2     3     4    5     6     7     8   9    
		FormLayout lay = new FormLayout("fill:0:grow(0.5),5dlu,70dlu,3dlu,60dlu,3dlu,70dlu,3dlu,60dlu,"+
		//10   11    12     13
		"3dlu,70dlu,3dlu, 60dlu,25dlu,fill:0:grow(0.5)",
		// 1   2  3   4  5   6  7    8  9  10 11
		"10dlu,p,5dlu,p,5dlu,p,5dlu,p,2dlu,p,10dlu");
		CellConstraints cc = new CellConstraints();
		content = new JXPanel();
		content.setLayout(lay);
		lab = new JLabel("Mitarbeiter");
		content.add(lab,cc.xy(3,2,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		jcmb = new JRtaComboBox();
		jcmb.setDataVectorVector(doKollegen(), 0, 1);
		jcmb.setActionCommand("kollegen");
		jcmb.addActionListener(al);
		content.add(jcmb,cc.xy(5,2));
		lab = new JLabel("Bet.an Behandl.(%)");
		content.add(lab,cc.xy(7,2,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		tfbeteil[0]= new JRtaTextField("ZAHLEN",true);
		tfbeteil[0].setText("0");
		content.add(tfbeteil[0],cc.xy(9,2));
		lab = new JLabel("Bet.an HB(%)");
		content.add(lab,cc.xy(11,2,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		tfbeteil[1]= new JRtaTextField("ZAHLEN",true);
		tfbeteil[1].setText("0");
		content.add(tfbeteil[1],cc.xy(13,2));
		
		lab = new JLabel("Bet.an Weggeb(%)");
		content.add(lab,cc.xy(11,4,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		tfbeteil[2]= new JRtaTextField("ZAHLEN",true);
		tfbeteil[2].setText("0");
		content.add(tfbeteil[2],cc.xy(13,4));
		
		
		lab = new JLabel("von...");
		content.add(lab,cc.xy(3,6,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		tfs[0]= new JRtaTextField("DATUM",true);
		tfs[0].setText(DatFunk.sHeute());
		content.add(tfs[0],cc.xy(5,6));

		lab = new JLabel("bis...");
		content.add(lab,cc.xy(7,6,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		tfs[1]= new JRtaTextField("DATUM",true);
		tfs[1].setText(DatFunk.sHeute());
		content.add(tfs[1],cc.xy(9,6));
		
		content.add((buts[0]=ButtonTools.macheButton("ermitteln", "ermitteln", al)),cc.xy(13,6));
		buts[0].addKeyListener(kl);

		progresstext1 = new JLabel(" ");
		content.add(progresstext1,cc.xyw(5,8,9,CellConstraints.CENTER,CellConstraints.DEFAULT));
		
		progressbar1 = new JProgressBar();
		progressbar1.setStringPainted(true);
		content.add(progressbar1,cc.xyw(5,10,9,CellConstraints.FILL,CellConstraints.DEFAULT));
		
		content.validate();
		return content;
	}
	private Vector<Vector<String>> doKollegen(){
		int lang = ParameterLaden.vKollegen.size();
		veckolls.clear();
		Vector<String> vecdummy = new Vector<String>(); 
		for(int i = 0;i<lang;i++){
			vecdummy.clear();
			//System.outprintln(ParameterLaden.vKollegen.get(i));
			vecdummy.add((String)ParameterLaden.vKollegen.get(i).get(0));
			vecdummy.add((String)ParameterLaden.vKollegen.get(i).get(3));
			veckolls.add((Vector<String>)vecdummy.clone());
		}
		Comparator<Vector<String>> comparator = new Comparator<Vector<String>>() {
			@Override
			public int compare(Vector<String> o1, Vector<String> o2) {
				String s1 = (String)o1.get(0);
				String s2 = (String)o2.get(0);
				return s1.compareTo(s2);
			}
		};
		Collections.sort(veckolls,comparator);
		return veckolls;
	}
	
	private void doTagAnalysieren(Vector<String> tag,int anzahltermine){
		String reznum,patname;
		Vector<Vector<String>> vec;
		boolean inhistory = false;
		boolean ohnezuordnung = false;
		int pos;
		progressbar1.setMinimum(0);
		progressbar1.setMaximum(anzahltermine);
		Vector<String> rezvec = new Vector<String>();
		for(int i = 0;i < anzahltermine;i++){
			progressbar1.setValue(i);
			progresstext1.setText("Auswerten Tag: "+DatFunk.sDatInDeutsch(tag.get(304))+
					" Termin "+Integer.toString(i+1) +" von "+tag.get(300));
			inhistory = false;
			ohnezuordnung = false;
			reznum = tag.get((i*5)+1).trim();
			if( (reznum.indexOf("\\")) >= 0){
				pos = reznum.indexOf("\\");
				reznum = reznum.substring(0,pos);
			}
			if( (!reznum.contains("@FREI")) && (!reznum.contains("@INTERN")) ){
				if( (reznum.trim().equals("")) && (!tag.get((i*5)).trim().equals("")) ){
					doKeineZuordnung("ohne Zuordnung: "+tag.get((i*5)),false);
					anzahlTagesBehandlungen++;
				}else{
					if((reznum.length()>2)){
						if( ("KGMAERLORH".contains(reznum.substring(0,2))) ){
							vec = SqlInfo.holeFelder("select * from verordn where rez_nr ='"+reznum+"' LIMIT 1");
							if(vec.size()<=0){
								vec = SqlInfo.holeFelder("select * from lza where rez_nr ='"+reznum+"' LIMIT 1");
								if(vec.size()<=0){
									ohnezuordnung = true;
								}else{
									inhistory = true;
								}
							}
							//In beiden Tabellen Rezept nicht gefunden gefunden
							if(ohnezuordnung){
								doKeineZuordnung("ohne Zuordnung: "+tag.get((i*5)),false);
								anzahlTagesBehandlungen++;
							}else{
								if(rezvec.contains(vec.get(0).get(1))){
									doKeineZuordnung("an diesem Tag bereits erfaßt: "+tag.get((i*5))+"-"+vec.get(0).get(1).toString(),false);
									anzahlTagesBehandlungen++;
								}else{
									doAnteilBerechnen(vec,inhistory,DatFunk.sDatInDeutsch(tag.get(304)),tag.get((i*5)));
									rezvec.add(vec.get(0).get(1).toString());
									anzahlTagesBehandlungen++;
								}
								//System.out.println("Tag "+DatFunk.sDatInDeutsch(tag.get(304))+" TerminNr "+(i+1)+" = Rezeptnummer "+reznum);
							}
						}else{
							doKeineZuordnung("ohne Zuordnung: "+tag.get((i*5)),false);
							anzahlTagesBehandlungen++;
						}
					}else{
						if(!tag.get((i*5)).trim().equals("")){
							doKeineZuordnung("ohne Zuordnung: "+tag.get((i*5)),false);
							anzahlTagesBehandlungen++;
						}
					}
				}
			}
		}
	}
	private void doAuswerten(int tage){
		progressbar1.setForeground(Color.RED);
		progressbar1.setMinimum(0);
		progressbar1.setMaximum(tage+1);
		int lang = tagesvec.size();
		//Durch die einzelnen Tage
		for(int i = 0; i < lang;i++){
			//progressbar1.setValue(i);
			//progresstext1.setText("Auswerten Tag: "+DatFunk.sDatInDeutsch(tagesvec.get(i).get(0).get(304))+
					//" Termin "+Integer.toString(i+1) +" von "+tagesvec.get(i).get(0).get(300));
			try {
				anzahlTagesBehandlungen = 0;
				doTagesKopf(DatFunk.sDatInDeutsch(tagesvec.get(i).get(0).get(304)));
				Integer[] summenStart = {calcrow+1,0};
				summenPos.add(summenStart);
				doTagAnalysieren(tagesvec.get(i).get(0),Integer.parseInt(tagesvec.get(i).get(0).get(300)));
				if(anzahlTagesBehandlungen==0){
					doKeineZuordnung("Keine Behandlungen an diesem Tag (Urlaub/Krankheit)",false);
				}
				summenPos.get(summenPos.size()-1)[1] = calcrow;
				doTagesSumme(i);
				
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
			} catch (UnknownPropertyException e) {
				e.printStackTrace();
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (WrappedTargetException e) {
				e.printStackTrace();
			}

		}
		doEndsumme();
		progresstext1.setText("feddisch....");
		progressbar1.setForeground(Color.GREEN);
		progressbar1.setValue(progressbar1.getMaximum());

		
	}
	/*******************************/
	private void doEinlesen(String start,String end,int differenz){
		String akttag;
		String cmd;
		tagesvec.clear();
		tagesvec.trimToSize();
		progressbar1.setForeground(Color.RED);
		progressbar1.setMinimum(0);
		progressbar1.setMaximum(differenz+1);
		for(int i = 0; i <= differenz;i++){
			progressbar1.setValue(i);
			akttag = DatFunk.sDatInSQL( DatFunk.sDatPlusTage(DatFunk.sDatInDeutsch(start), i) );
			progresstext1.setText("Termindaten abholen: "+DatFunk.sDatInDeutsch(akttag));
			
			cmd = "select * from flexkc where datum ='"+akttag+"'"+ 
			" AND behandler ='"+aktBehandlerNummer+"BEHANDLER'";
			tagesvec.add( SqlInfo.holeFelder(cmd) );
		}
		progressbar1.setForeground(Color.GREEN);
		progressbar1.setValue(differenz+1);
		doAuswerten(differenz);
	}
	/**
	 * @throws NOAException 
	 * @throws OfficeApplicationException 
	 * @throws IllegalArgumentException 
	 * @throws PropertyVetoException 
	 * @throws UnknownPropertyException 
	 * @throws IndexOutOfBoundsException 
	 * @throws WrappedTargetException 
	 * @throws NoSuchElementException *****************************/
	private boolean doErmitteln() throws NoSuchElementException, WrappedTargetException, IndexOutOfBoundsException, UnknownPropertyException, PropertyVetoException, IllegalArgumentException, OfficeApplicationException, NOAException{
		if(jcmb.getValue().toString().trim().equals("00")){
			JOptionPane.showMessageDialog(null, "Bitte wählen Sie einen Behandler aus");
			jcmb.requestFocus();
			return false;
		}
		if(tfbeteil[0].getText().trim().equals("0") && tfbeteil[1].getText().trim().equals("0")){
			JOptionPane.showMessageDialog(null, "Bei jeweils 0% Beteiligung gibt's eigentlich nichts was zu ermitteln wäre\nDeppen gibt's.....");
			tfbeteil[0].requestFocus();
			return false;
		}
		try{
			prozBehandl = Integer.parseInt(tfbeteil[0].getText().trim());
			prozHb = Integer.parseInt(tfbeteil[1].getText().trim());
			prozWeg = Integer.parseInt(tfbeteil[2].getText().trim());
		}catch(Exception ex){
			JOptionPane.showMessageDialog(null, "Ein oder mehrere Prozent-Werte sind unzulässig");
			tfbeteil[0].requestFocus();
			return false;
		}
		String dat1=null,dat2=null;
		try{
			dat1 = DatFunk.sDatInSQL(tfs[0].getText().trim());
			dat2 = DatFunk.sDatInSQL(tfs[1].getText().trim());
		}catch(Exception ex){
			JOptionPane.showMessageDialog(null, "Die eingegebenen Datumswerte sind unzulässig");
			tfs[0].requestFocus();
			return false;
		}
		long differenz = DatFunk.TageDifferenz(tfs[0].getText().trim(), tfs[1].getText().trim());
		if(differenz < 0){
			JOptionPane.showMessageDialog(null, "Das eingegebene Enddatum liegt vor dem Startdatum");
			tfbeteil[0].requestFocus();
			return false;
		}
		starteCalc();
		doEinlesen(dat1,dat2,Integer.parseInt(Long.toString(differenz)));
		zeigeCalc();
		return true;
	}
	/*******************************/	
	private void makeListeners(){
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd = arg0.getActionCommand();
				if(cmd.equals("ermitteln")){
					new SwingWorker<Void,Void>(){
						@Override
						protected Void doInBackground() throws Exception {
							try{
								tagesvec.clear(); 
								tagesvec.trimToSize();
								summenPos.clear();
								summenPos.trimToSize();								
								anzahlTagesBehandlungen = 0;
								calcrow = 0;
								if(!doErmitteln()){
									return null;
								}
							}catch(Exception ex){
								ex.printStackTrace();
							}
							return null;
						}
					}.execute();
				}
				if(cmd.equals("kollegen") && initok){
					aktBehandlerNummer = jcmb.getValue().toString();
					//System.outprintln("Aktueller Behandler = "+aktBehandlerNummer);
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
	/**
	 * @throws WrappedTargetException 
	 * @throws NoSuchElementException 
	 * @throws IllegalArgumentException 
	 * @throws PropertyVetoException 
	 * @throws UnknownPropertyException 
	 * @throws IndexOutOfBoundsException *****************************/
	private void starteCalc() throws OfficeApplicationException, NOAException, NoSuchElementException, WrappedTargetException, IndexOutOfBoundsException, UnknownPropertyException, PropertyVetoException, IllegalArgumentException{
		if(!Reha.officeapplication.isActive()){
			Reha.starteOfficeApplication();
		}
		IDocumentService documentService = Reha.officeapplication.getDocumentService();
        IDocumentDescriptor docdescript = new DocumentDescriptor();
       	docdescript.setHidden(true);
        docdescript.setAsTemplate(true);
		document = documentService.constructNewDocument(IDocument.CALC, docdescript);
		spreadsheetDocument = (ISpreadsheetDocument) document;
		OOTools.setzePapierFormatCalc((ISpreadsheetDocument) spreadsheetDocument, 21000, 29700);
		OOTools.setzeRaenderCalc((ISpreadsheetDocument) spreadsheetDocument, 1000,1000, 1000, 1000);
		XSpreadsheets spreadsheets = spreadsheetDocument.getSpreadsheetDocument().getSheets();
		
		sheetName= "Tabelle1";
		spreadsheet = (XSpreadsheet)UnoRuntime.queryInterface(XSpreadsheet.class,spreadsheets.getByName(sheetName));
		cellCursor = spreadsheet.createCursor();
		doTabellenKopf();
		doSpaltenJustieren();
	}
	/**
	 * @throws IndexOutOfBoundsException 
	 * @throws WrappedTargetException 
	 * @throws IllegalArgumentException 
	 * @throws PropertyVetoException 
	 * @throws UnknownPropertyException *****************************/
	private void doTabellenKopf() throws IndexOutOfBoundsException, UnknownPropertyException, PropertyVetoException, IllegalArgumentException, WrappedTargetException{
		OOTools.doCellFontBold(cellCursor, 0, 0);
		OOTools.doCellValue(cellCursor, 0, 0, "Gesamtumsätze vom "+tfs[0].getText()+" bis einschließlich "+tfs[1].getText());
		OOTools.doCellColor(cellCursor, 11, 0, 0xffffff);
		OOTools.doCellColor(cellCursor, 12, 0, 0xffffff);
		OOTools.doCellColor(cellCursor, 13, 0, 0xffffff);
		OOTools.doCellValue(cellCursor, 11, 0, Double.parseDouble(tfbeteil[0].getText()));
		OOTools.doCellValue(cellCursor, 12, 0, Double.parseDouble(tfbeteil[1].getText()));
		OOTools.doCellValue(cellCursor, 13, 0, Double.parseDouble(tfbeteil[2].getText()));
		calcrow++;
		OOTools.doCellFontBold(cellCursor, 0, calcrow);
		OOTools.doCellValue(cellCursor, 0, calcrow, jcmb.getSelectedItem().toString());
		calcrow+=2;
	}
	private void doSpaltenJustieren() throws NoSuchElementException, WrappedTargetException, IndexOutOfBoundsException, UnknownPropertyException, PropertyVetoException, IllegalArgumentException{
		OOTools.doColWidth(spreadsheetDocument,sheetName,0,0,4000);
		OOTools.doColTextAlign(spreadsheetDocument,sheetName,0,0,0);

		OOTools.doColWidth(spreadsheetDocument,sheetName,1,1,1200);
		OOTools.doColTextAlign(spreadsheetDocument,sheetName,1,1,2);
		
		OOTools.doColWidth(spreadsheetDocument,sheetName,2,7,1500);
		OOTools.doColTextAlign(spreadsheetDocument,sheetName,2,7,0);
		
		OOTools.doColWidth(spreadsheetDocument,sheetName,8,14,1500);
		OOTools.doColTextAlign(spreadsheetDocument,sheetName,8,14,3);

		OOTools.doColWidth(spreadsheetDocument,sheetName,15,15,1200);
		OOTools.doColTextAlign(spreadsheetDocument,sheetName,15,15,2);
		
		OOTools.doColNumberFormat(spreadsheetDocument,sheetName,8,14,2);

	}
	private void doTagesKopf(String tag) throws IndexOutOfBoundsException, UnknownPropertyException, PropertyVetoException, IllegalArgumentException, WrappedTargetException{
		OOTools.doCellFontBold(cellCursor, 0, calcrow);
		OOTools.doCellValue(cellCursor, 0, calcrow,tag);
		calcrow++;
		for(int i = 0; i < kopfzeile.length;i++){
			OOTools.doCellFontItalic(cellCursor, i, calcrow);
			OOTools.doCellValue(cellCursor, i, calcrow,kopfzeile[i]);
		}
		calcrow++;
		
	}
	private void doKeineZuordnung(String name,boolean history){
		try {
			OOTools.doCellColor(cellCursor, 0, calcrow, 0xff0000);
			OOTools.doCellValue(cellCursor, 0, calcrow,name.replace(copyright, ""));
			if(name.contains("erfaßt")){
				OOTools.doCellFontBold(cellCursor, 0, calcrow);
			}
			for(int i = 8;i < 13; i++){
				OOTools.doCellValue(cellCursor, i, calcrow,Double.parseDouble("0.00"));
			}
			OOTools.doCellFormula(cellCursor, 14,calcrow, "=sum(I"+Integer.toString(calcrow+1)+":N"+Integer.toString(calcrow+1)+")");
			calcrow++;
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		} catch (UnknownPropertyException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (WrappedTargetException e) {
			e.printStackTrace();
		}
	}
	private void doAnteilBerechnen(Vector<Vector<String>> rezvec,boolean history,String datum,String name){
		Vector<String> calczeile = RezTools.macheUmsatzZeile(rezvec,datum);
		//System.outprintln(calczeile);
		try {
			OOTools.doCellValue(cellCursor, 0, calcrow,name.replace(copyright, ""));
			OOTools.doCellValue(cellCursor, 1,calcrow, rezvec.get(0).get(41) );
			String formula;
			for(int i = 0 ; i < 6;i++){
				
				OOTools.doCellValue(cellCursor, i+2,calcrow, calczeile.get(i) );
				if(i==4){
					formula = "="+calczeile.get(i+6)+"/100*$M$1";
				}else if(i==5){
					formula = "="+calczeile.get(i+6)+"/100*$N$1";
				}else{
					formula = "="+calczeile.get(i+6)+"/100*$L$1";
				
				}
				OOTools.doCellFormula(cellCursor, i+8,calcrow, formula );
			}
			OOTools.doCellFormula(cellCursor, 14,calcrow, "=sum(I"+Integer.toString(calcrow+1)+":N"+Integer.toString(calcrow+1)+")");
			if(history){
				OOTools.doCellValue(cellCursor, 15,calcrow, "X");
			}
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		calcrow++;
	}
	private void doTagesSumme(int tag){
		try {
			OOTools.doCellColor(cellCursor, 0, calcrow, 0xff0000);
			OOTools.doCellValue(cellCursor, 0, calcrow, "Tagessumme");
			String formel = "=sum(O"+Integer.toString(summenPos.get(tag)[0])+":O"+Integer.toString(summenPos.get(tag)[1])+")";
			OOTools.doCellColor(cellCursor, 14, calcrow, 0xff0000);
			OOTools.doCellFormula(cellCursor, 14, calcrow, formel);
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		} catch (UnknownPropertyException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (WrappedTargetException e) {
			e.printStackTrace();
		}
		calcrow+= 2;
	}
	private void doEndsumme(){
		try {
			OOTools.doCellColor(cellCursor, 0, calcrow, 0xff0000);
			OOTools.doCellFontBold(cellCursor, 0, calcrow);
			OOTools.doCellValue(cellCursor, 0, calcrow, "Gesamtsumme");
			OOTools.doCellColor(cellCursor, 14, calcrow, 0xff0000);
			OOTools.doCellFontBold(cellCursor, 14, calcrow);
			String formula = "=";
			
			for(int i = 0;i < summenPos.size();i++){
				formula = formula+"O"+Integer.toString(summenPos.get(i)[1]+1)+(i < (summenPos.size()-1) ? "+" : "");
			}
			OOTools.doCellFormula(cellCursor, 14, calcrow, formula);
				
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		} catch (UnknownPropertyException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (WrappedTargetException e) {
			e.printStackTrace();
		}
	}
	private void zeigeCalc(){
		final ISpreadsheetDocument xspredsheetDocument = spreadsheetDocument;
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				xspredsheetDocument.getFrame().getXFrame().getContainerWindow().setVisible(true);
				xspredsheetDocument.getFrame().setFocus();
			}
		});
		
	}
	private void doBehandlung(){
		
	}

	public void doAufraeumen(){
		
	}

}
