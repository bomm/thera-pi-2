package anmeldungUmsatz;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DecimalFormat;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import oOorgTools.OOTools;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;

import rehaInternalFrame.JUmsaetzeInternal;
import sqlTools.SqlInfo;
import stammDatenTools.RezTools;
import systemTools.ButtonTools;
import systemTools.JRtaTextField;
import terminKalender.DatFunk;
import terminKalender.ParameterLaden;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.spreadsheet.ISpreadsheetDocument;
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
import com.sun.star.table.XCell;
import com.sun.star.table.XCellRange;
import com.sun.star.text.XText;
import com.sun.star.uno.UnoRuntime;

public class Umsaetze extends JXPanel{

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -906652242216759628L;
	JUmsaetzeInternal internal = null;
	JXPanel content = null;
	JLabel lab;
	JRtaTextField[] tfs = {null,null};
	JButton[] buts = {null};
	JLabel aktion1 = null;
	JLabel aktion2 = null;
	JProgressBar progress1 = null;
	JProgressBar progress2 = null;
	Vector<Vector<Object>> kalUsers = new Vector<Vector<Object>>();
	Vector<Vector<Vector<String>>> allDates = new Vector<Vector<Vector<String>>>(); 
	ActionListener al = null;
	KeyListener kl = null;

	int behandlungenProTag = 0;
	
	Vector<Integer[]> summenPos = new Vector<Integer[]>();
	DecimalFormat dcf = new DecimalFormat("##########0.00");
	
	int calcrow = 0;	
	ISpreadsheetDocument spreadsheetDocument = null;;
	IDocument document  = null;
	XSheetCellCursor cellCursor = null;
	String[] kopfzeile = {"Datum","Uhrzeit","Pos1","Pos2","Pos3","Pos4","HB","WG",
			"Preis1","Preis2","Preis3","Preis4","HB-Preis","WG-Pr.","RezNum","Historie"};
	String[] cols = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N"};
	
	long timeused = 0;
	public Umsaetze(JUmsaetzeInternal uint){
		super();
		this.internal = uint;
		this.makeListeners();
		this.add(getContent(),BorderLayout.CENTER);
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				setzeFocus();
			}
		});		
	}
	private void setzeFocus(){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				tfs[0].requestFocus();
			}
		});
	}
	private JXPanel getContent(){//             1          2   3      4    5     6      7    8     9     10         11    
		FormLayout lay = new FormLayout("fill:0:grow(0.5),5dlu,20dlu,60dlu,20dlu,20dlu,60dlu,25dlu,60dlu,5dlu,fill:0:grow(0.5),",
		//    1           2  3   4  5   6   7  8   9  10 
		"fill:0:grow(0.5),p,5dlu,p,2dlu,p,2dlu,p,2dlu,p,fill:0:grow(0.5)");
		CellConstraints cc = new CellConstraints();
		content = new JXPanel();
		content.setLayout(lay);

		lab = new JLabel("von..");
		this.content.add(lab,cc.xy(3,2));
		this.tfs[0] = new JRtaTextField("DATUM",false);
		this.tfs[0].setText(DatFunk.sHeute());
		this.content.add(this.tfs[0],cc.xy(4,2));
		
		lab = new JLabel("bis..");
		this.content.add(lab,cc.xy(6,2));
		this.tfs[1] = new JRtaTextField("DATUM",false);
		this.tfs[1].setText(DatFunk.sHeute());
		this.content.add(this.tfs[1],cc.xy(7,2));
		
		this.content.add((this.buts[0] = ButtonTools.macheButton("ermitteln", "ermitteln", al)),cc.xyw(9,2,2));
		this.buts[0].addKeyListener(kl);
		aktion1 = new JLabel("  ");
		aktion1.setForeground(Color.BLUE);
		this.content.add(aktion1,cc.xyw(1,4,9,CellConstraints.LEFT,CellConstraints.DEFAULT));
		
		progress1 = new JProgressBar();
		progress1.setForeground(Color.RED);
		progress1.setStringPainted(true);
		this.content.add(progress1,cc.xyw(1,6,10,CellConstraints.FILL,CellConstraints.DEFAULT));
		
		aktion2 = new JLabel("  ");
		aktion2.setForeground(Color.BLUE);
		this.content.add(aktion2,cc.xyw(1,8,9,CellConstraints.LEFT,CellConstraints.DEFAULT));

		progress2 = new JProgressBar();
		progress2.setForeground(Color.RED);
		progress2.setStringPainted(true);
		this.content.add(progress2,cc.xyw(1,10,10,CellConstraints.FILL,CellConstraints.DEFAULT));

		return content;
	}
	private void doKeineBehandlung(String datum) throws NumberFormatException, IndexOutOfBoundsException, UnknownPropertyException, PropertyVetoException, IllegalArgumentException, WrappedTargetException{
		doCellColor(0,calcrow,0xff0000);
		doCellValue(0,calcrow,datum);
		doCellColor(2,calcrow,0xff0000);
		doCellValue(2,calcrow,"Keine Behandlungen an diesem Tag");
		for(int i = 8; i < 14;i++){
			doCellValue(i,calcrow,Double.parseDouble("0.00"));
		}
		calcrow++;
	}

	private void doLeerzeile(String datum,String uhrzeit,String text) throws NumberFormatException, IndexOutOfBoundsException, UnknownPropertyException, PropertyVetoException, IllegalArgumentException, WrappedTargetException{
		doCellValue(0,calcrow,datum);
		doCellValue(1,calcrow,uhrzeit);
		doCellColor(2,calcrow,0xff0000);
		doCellValue(2,calcrow,text);
		for(int i = 8; i < 14;i++){
			doCellValue(i,calcrow,Double.parseDouble("0.00"));
		}
		calcrow++;
	}
	/**
	 * @throws IndexOutOfBoundsException 
	 * @throws WrappedTargetException 
	 * @throws IllegalArgumentException 
	 * @throws PropertyVetoException 
	 * @throws UnknownPropertyException 
	 * @throws NumberFormatException ********************/
	private void doManageRezept(String reznum,String datum,String uhrzeit) throws IndexOutOfBoundsException, NumberFormatException, UnknownPropertyException, PropertyVetoException, IllegalArgumentException, WrappedTargetException{
		Vector<Vector<String>> vec = SqlInfo.holeFelder("select * from verordn where rez_nr='"+reznum+"' LIMIT 1");
		boolean ishistory = false;
		boolean emptyrow = false;
		if(vec.size()<=0){
			// in Hisorie suchen
			vec = SqlInfo.holeFelder("select * from lza where rez_nr='"+reznum+"' LIMIT 1");
			if(vec.size()<=0){
				emptyrow = true;
			}else{
				ishistory = true;
			}
		}
		if(emptyrow){
			doLeerzeile(datum,uhrzeit,"Rezept "+reznum+" weder in aktuelle Rezept noch in Historie gefunden");
			//Leerzeile produzieren mit Rez nicht gefunden
		}else{
			// Zeile managen +
			//if(ishistory) einen Marker setzen
			Vector<String> calczeile = RezTools.macheUmsatzZeile(vec,datum);
			doCellValue(0,calcrow,datum);
			doCellValue(1,calcrow,uhrzeit);
			for(int i = 0; i < calczeile.size();i++){
				try{
					if(i >=6 && i <=12){
						doCellValue(i+2,calcrow,Double.parseDouble(calczeile.get(i)));
					}else{
						doCellValue(i+2,calcrow,calczeile.get(i));
					}
				}catch(Exception ex){
					JOptionPane.showMessageDialog(null,"Bei der Positionsberechnung ist ein Fehler aufgetregen");
					//ex.printStackTrace();
				}
			}
			doCellValue(14,calcrow,reznum);
			if(ishistory){
				doCellValue(15,calcrow,"X");
			}
			calcrow++;
		}
	}
	/**********************/
	private void ermittleDates(){
		try{
			int anzahlbehandler = allDates.size();
			int anzahltage = allDates.get(0).size();
			Vector<Vector<String>> vecBehandlungen = new Vector<Vector<String>>();
			
			String reznum = "";
			String rawreznum = "";
			String endreznum =  "";
			int pos;
			progress2.setMinimum(0);
			progress2.setMaximum(anzahlbehandler);

			//Oberste Schleife für die Kalenderbenutzer
			for(int i1 = 0; i1 < anzahlbehandler;i1++ ){
				//progressbar 1+2 einstellen
				progress1.setMinimum(0);
				progress1.setMaximum(anzahltage);
				
				progress2.setValue(i1);
				aktion2.setText("ermittle Umsatz von:"+kalUsers.get(i1).get(0));

				//calcValue
				doCellCharWeight(0,calcrow,com.sun.star.awt.FontWeight.BOLD);
				doCellValue(0,calcrow,"Umsatz von: "+kalUsers.get(i1).get(0));
				calcrow++;
				for(int kopf = 0; kopf < kopfzeile.length;kopf++){
					doCellValue(kopf,calcrow,kopfzeile[kopf]);
				}
				calcrow++;
				//Hier die int-Werte der Reihen für die summenbildung Speichern zunächst nur Beginn!
				//Endwert hat an dieser Stelle noch 0
				Integer[] rows = {calcrow+1,0};
				summenPos.add(rows);

				//1. Unterschleife für die einzelnen Behandlungstage (von - bis Angaben)
				for(int i2 = 0; i2 < anzahltage;i2++){
					if(vecBehandlungen.size() < anzahltage){
						vecBehandlungen.add(new Vector<String>());
					}
					//text einstellen
					int anzahltermine = Integer.parseInt(allDates.get(i1).get(i2).get(300));
					progress1.setMinimum(0);
					progress1.setMaximum(anzahltermine);

					////System.out.println(kalUsers.get(i1).get(0)+" hat am "+DatFunk.sDatInDeutsch(allDates.get(i1).get(i2).get(304))+" Anzahl Termine = "+allDates.get(i1).get(i2).get(300));

					//2. und letzte Unterschleife für die einzelnen Termine pro Behandlunstag
					behandlungenProTag = 0;
					//
					String akttag = DatFunk.sDatInDeutsch(allDates.get(i1).get(i2).get(304));
					for(int i3 = 0; i3 < anzahltermine; i3++){
						progress1.setValue(i3);
						akttag = DatFunk.sDatInDeutsch(allDates.get(i1).get(i2).get(304));
						aktion1.setText(akttag+": ermittle Termin "+Integer.toString(i3+2)+" von "+Integer.toString(anzahltermine));

						if( (!(reznum = allDates.get(i1).get(i2).get((i3*5)+1)).contains("@FREI")) &&
								(!(reznum = allDates.get(i1).get(i2).get((i3*5)+1)).contains("@INTERN"))	){
							// Wenn der Termin nich leer ist
							if((!reznum.trim().equals("")) && (reznum.trim().length()>2)){
								
								if("KGMAERLORH".contains(reznum.substring(0,2))){
									rawreznum = reznum; 
									if( (pos=rawreznum.indexOf("\\")) >= 0){
										pos = reznum.indexOf("\\");
										endreznum = allDates.get(i1).get(i2).get((i3*5)+1).substring(0,pos).trim(); 
										////System.out.println("Behandler="+kalUsers.get(i1).get(0)+" Tag = "+allDates.get(i1).get(i2).get(304)+"-Rezeptnummer = "+allDates.get(i1).get(i2).get((i3*5)+1).substring(0,pos).trim());
									}else{
										////System.out.println("Behandler="+kalUsers.get(i1).get(0)+" Tag = "+allDates.get(i1).get(i2).get(304)+"-Rezeptnummer = "+allDates.get(i1).get(i2).get((i3*5)+1).trim());
										endreznum = allDates.get(i1).get(i2).get((i3*5)+1).trim();
									}
									//testen ob diese Rezeptnummer an selben Tag bereits einbezogen wurde!
									if(vecBehandlungen.get(i2).indexOf(endreznum)>=0){
										//bereits erfaßt
										doLeerzeile(DatFunk.sDatInDeutsch(allDates.get(i1).get(i2).get(304)),
												allDates.get(i1).get(i2).get((i3*5)+2),"Rezept "+endreznum+" an diesem Tag bereits erfaßt!!");
										behandlungenProTag++;
									}else{
										vecBehandlungen.get(i2).add(String.valueOf(endreznum));
										doManageRezept(endreznum,DatFunk.sDatInDeutsch(allDates.get(i1).get(i2).get(304)),allDates.get(i1).get(i2).get((i3*5)+2));
										behandlungenProTag++;
									}
								}
							}
						}
					}
					if(behandlungenProTag==0){
						doKeineBehandlung(akttag);
					}
					progress1.setValue(progress1.getMaximum());
				}
				doCellColor(0,calcrow,0xff0000);
				doCellValue(0,calcrow,"Summen");
				summenPos.get(summenPos.size()-1)[1] = calcrow+1;
				if(summenPos.get(summenPos.size()-1)[1]==summenPos.get(summenPos.size()-1)[0]){
					//Keine Positionen gefunden = Urlaub, Krankheit etc.
					for(int i = 8; i < 14;i++){
						doCellColor(i,summenPos.get(summenPos.size()-1)[1]-1,0xff0000);
						doCellValue(i,summenPos.get(summenPos.size()-1)[1]-1,Double.parseDouble("0.00"));
						//Hier korrigieren. Leerzeile einfügen und danach die Summenformel
						//damit kann man z.B. Beträge für Krankheits- oder Urlaubstage einfügen
					}
				}else{
					for(int i = 8; i < 14;i++){
						String formula ="=sum("+
						cols[i]+
						Integer.toString(summenPos.get(summenPos.size()-1)[0])+
						":"+
						cols[i]+
						Integer.toString(summenPos.get(summenPos.size()-1)[1]-1)+
						")"; 
						//System.out.println(formula);
						doCellColor(i,summenPos.get(summenPos.size()-1)[1]-1,0xff0000);
						doCellFormula(i,summenPos.get(summenPos.size()-1)[1]-1,formula);
					}
				}

				//System.out.println("StartRow für SummenFormel = "+summenPos.get(summenPos.size()-1)[0]+
						//" EndRow für SummenFormel = "+summenPos.get(summenPos.size()-1)[1]);
				
				calcrow += 2;
			}
			progress1.setForeground(Color.GREEN);
			progress1.setValue(progress1.getMaximum());
			aktion1.setText("geschafft....");
			progress2.setForeground(Color.GREEN);
			progress2.setValue(anzahlbehandler);
			aktion2.setText("Umsatzermittlung abgeschlossen");
			

			calcrow += 2;
			int iusers = kalUsers.size();
			doCellCharWeight(0,calcrow,com.sun.star.awt.FontWeight.BOLD);
			doCellValue(0,calcrow,"Gesamtbeträge");
			calcrow++;
			int rowstart,rowend;
			rowstart = calcrow;
			String formula = "";
			for(int i = 0; i < iusers;i++){
				doCellValue(0,calcrow,kalUsers.get(i).get(0));
				formula = "=sum(I"+
				Integer.toString(summenPos.get(i)[1])+
				":"+
				"N"+
				Integer.toString(summenPos.get(i)[1])+
				")";
				//System.out.println("\nSummenFormula = "+formula+"\n");
				doCellNumberFormat(4,calcrow,2);
				//doCellColor(4, calcrow, 0xff0000);
				doCellFormula(4,calcrow,formula.toString());
				calcrow++;
			}
			rowend = calcrow;
			doCellColor(0,calcrow,0xff0000);
			doCellCharWeight(0,calcrow,com.sun.star.awt.FontWeight.BOLD);
			doCellValue(0,calcrow,"Umsatz im Gesamtbetrieb");
			formula = "=sum("+
			cols[4]+Integer.toString(rowstart)+
			":"+
			cols[4]+Integer.toString(rowend)+
			")";
			//System.out.println("Summen Formel = "+formula);
			doCellNumberFormat(4,calcrow,2);
			doCellColor(4,calcrow,0xff0000);
			doCellCharWeight(4,calcrow,com.sun.star.awt.FontWeight.BOLD);
			doCellFormula(4,calcrow,formula);
			if(!Reha.vollbetrieb){
				JOptionPane.showMessageDialog(null,"Benötigte Zeit für diesen Vorgang insgesamt "+(System.currentTimeMillis()-timeused)+" Millisekunden");
			}
			final ISpreadsheetDocument xspredsheetDocument = spreadsheetDocument;
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					xspredsheetDocument.getFrame().getXFrame().getContainerWindow().setVisible(true);
					xspredsheetDocument.getFrame().setFocus();
				}
			});

			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	/**********************/	
	@SuppressWarnings("unchecked")
	private int testeKalenderUser(){
		int lang = ParameterLaden.vKKollegen.size();
		Vector<Object> vec = new Vector<Object>();
		kalUsers.clear();
		kalUsers.trimToSize();
		for(int i = 0; i < lang; i++){
			if(! ParameterLaden.getMatchcode(i).trim().equals("./.")){
				vec.clear();
				vec.add((String)ParameterLaden.getMatchcode(i));
				vec.add((Integer)ParameterLaden.getDBZeile(i));
				
				kalUsers.add((Vector<Object>)vec.clone());
			}	
		}
		return kalUsers.size();
	}
	/**********************/
	@SuppressWarnings("unchecked")
	private int ermittleFaelle(String datum_von,String datum_bis){
		int lang = kalUsers.size();
		int behandler = 0;
		progress1.setMinimum(0);
		progress1.setMaximum(lang-1);
		progress1.setValue(0);
		Vector<Vector<String>> vec = new Vector<Vector<String>>();
		allDates.clear();
		allDates.trimToSize();
		for(int i = 0; i < lang; i++){
			vec.clear();
			aktion1.setText("Hole Daten von Kalenderbenutzer: "+kalUsers.get(i).get(0));
			progress1.setValue(i);
			behandler = (Integer) kalUsers.get(i).get(1);
			vec = 
				sqlTools.SqlInfo.holeFelder("select * from flexkc where datum >='"+
					datum_von+
					"' AND datum <='"+
					datum_bis+
					"' AND "+
					"behandler ='"+
					(behandler < 10 ? "0"+Integer.toString(behandler)+"BEHANDLER'" : Integer.toString(behandler)+"BEHANDLER'")
					);
			allDates.add((Vector)vec.clone());
		}
		progress1.setValue(lang);
		aktion1.setText("einlesen der Datenbank abgeschlossen");
		return allDates.size();
	}
	/**********************/	
	private void makeListeners(){
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd = arg0.getActionCommand();
				if(cmd.equals("ermitteln")){
						calcrow = 0;
						kalUsers.clear();
						kalUsers.trimToSize();
						allDates.clear(); 
						allDates.trimToSize();
						summenPos.clear();
						progress1.setForeground(Color.RED);
						progress2.setForeground(Color.RED);
						new SwingWorker<Void,Void>(){
							@Override
							protected Void doInBackground() throws Exception {
								try{
									setCursor(Reha.thisClass.wartenCursor);
									new SwingWorker<Void,Void>(){
										@Override
										protected Void doInBackground() throws Exception {
											timeused = System.currentTimeMillis();
											if( testeKalenderUser() > 0){
												String dat1,dat2;
												dat1 = DatFunk.sDatInSQL(tfs[0].getText());
												dat2 = DatFunk.sDatInSQL(tfs[1].getText());
												try{
													if(ermittleFaelle(dat1,dat2) > 0){
														starteCalc();
														doColWith(spreadsheetDocument,0,0,2050);
														doColWith(spreadsheetDocument,1,1,1800);
														doColTextAlign(spreadsheetDocument,0,7,0);
														
														doColWith(spreadsheetDocument,2,13,1500);
														doColTextAlign(spreadsheetDocument,8,14,3);
														
														doColWith(spreadsheetDocument,14,14,1900);
														doColWith(spreadsheetDocument,15,15,1900);
														doColTextAlign(spreadsheetDocument,15,153,2);
														
														doColNumberFormat(spreadsheetDocument,8,13,2);

														XSpreadsheets spreadsheets = spreadsheetDocument.getSpreadsheetDocument().getSheets();
														String sheetName= "Tabelle1";
														XSpreadsheet spreadsheet1 = (XSpreadsheet)UnoRuntime.queryInterface(XSpreadsheet.class,spreadsheets.getByName(sheetName));
														cellCursor = spreadsheet1.createCursor();
														doCellCharWeight(0,calcrow,com.sun.star.awt.FontWeight.BOLD);
														doCellValue(0,calcrow,"Gesamtumsätze vom "+tfs[0].getText()+" bis einschließlich "+tfs[1].getText());
														calcrow += 2;
														ermittleDates();															
													}
												}catch(Exception ex){
													ex.printStackTrace();
													JOptionPane.showMessageDialog(null,"Es ist ein Fehler in der Bearbeitung aufgetreten\nSind die eingegebenen Datumswerte korrekt?");
												}
											}
											setCursor(Reha.thisClass.cdefault);
											//aktion1.setText("  ");
											//progress1.setValue(0);
											////System.out.println("Daten wurden gesammelt von "+allDates.size()+" Behandlern");
											////System.out.println("Anzahl Tage analysiert "+allDates.get(0).size());
											return null;
										}
										
									}.execute();
								}catch(Exception ex){
									ex.printStackTrace();
								}
								return null;
							}
						}.execute();
							
					return;
				}
				if(cmd.equals("calc")){
					JOptionPane.showMessageDialog(null, "Diese Funktion ist noch nicht implementiert");
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
	public void doAufraeumen(){
		
	}
	/**
	 * @throws OfficeApplicationException 
	 * @throws NOAException 
	 * @throws IllegalArgumentException 
	 * @throws PropertyVetoException 
	 * @throws UnknownPropertyException 
	 * @throws WrappedTargetException 
	 * @throws NoSuchElementException *****************************/
	private void starteCalc() throws OfficeApplicationException, NOAException, NoSuchElementException, WrappedTargetException, UnknownPropertyException, PropertyVetoException, IllegalArgumentException{
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
	}
	private void doColWith(ISpreadsheetDocument spreadsheetDocument, int col_first,int col_last,int width) throws NoSuchElementException, WrappedTargetException, IndexOutOfBoundsException, UnknownPropertyException, PropertyVetoException, IllegalArgumentException{
		XSpreadsheets spreadsheets = spreadsheetDocument.getSpreadsheetDocument().getSheets();
		String sheetName= "Tabelle1";
		XSpreadsheet spreadsheet1 = (XSpreadsheet)UnoRuntime.queryInterface(XSpreadsheet.class,spreadsheets.getByName(sheetName));
		XCellRange xCellRange = spreadsheet1.getCellRangeByPosition( 0, 0, col_last, 0 );
		com.sun.star.table.XColumnRowRange xColRowRange = ( com.sun.star.table.XColumnRowRange )
		UnoRuntime.queryInterface( com.sun.star.table.XColumnRowRange.class, xCellRange );

		com.sun.star.beans.XPropertySet xPropSet = null;
		com.sun.star.table.XTableColumns xColumns = xColRowRange.getColumns();
		for(int i = col_first; i <= col_last;i++){
			Object aColumnObj = xColumns.getByIndex(i);
			xPropSet = (com.sun.star.beans.XPropertySet)
			UnoRuntime.queryInterface(com.sun.star.beans.XPropertySet.class, aColumnObj);
			xPropSet.setPropertyValue("Width", new Integer(width));
		}
	}
	private void doColTextAlign(ISpreadsheetDocument spreadsheetDocument, int col_first,int col_last,int col_textalign) throws NoSuchElementException, WrappedTargetException, IndexOutOfBoundsException, UnknownPropertyException, PropertyVetoException, IllegalArgumentException{
		XSpreadsheets spreadsheets = spreadsheetDocument.getSpreadsheetDocument().getSheets();
		String sheetName= "Tabelle1";
		XSpreadsheet spreadsheet1 = (XSpreadsheet)UnoRuntime.queryInterface(XSpreadsheet.class,spreadsheets.getByName(sheetName));
		XCellRange xCellRange = spreadsheet1.getCellRangeByPosition( 0, 0, col_last, 0 );
		com.sun.star.table.XColumnRowRange xColRowRange = ( com.sun.star.table.XColumnRowRange )
		UnoRuntime.queryInterface( com.sun.star.table.XColumnRowRange.class, xCellRange );

		com.sun.star.beans.XPropertySet xPropSet = null;
		com.sun.star.table.XTableColumns xColumns = xColRowRange.getColumns();
		for(int i = col_first; i <= col_last;i++){
			Object aColumnObj = xColumns.getByIndex(i);
			xPropSet = (com.sun.star.beans.XPropertySet)
			UnoRuntime.queryInterface(com.sun.star.beans.XPropertySet.class, aColumnObj);
			xPropSet.setPropertyValue("HoriJustify", col_textalign);
		}
	}
	private void doColNumberFormat(ISpreadsheetDocument spreadsheetDocument, int col_first,int col_last,int col_numberformat) throws NoSuchElementException, WrappedTargetException, IndexOutOfBoundsException, UnknownPropertyException, PropertyVetoException, IllegalArgumentException{
		XSpreadsheets spreadsheets = spreadsheetDocument.getSpreadsheetDocument().getSheets();
		String sheetName= "Tabelle1";
		XSpreadsheet spreadsheet1 = (XSpreadsheet)UnoRuntime.queryInterface(XSpreadsheet.class,spreadsheets.getByName(sheetName));
		XCellRange xCellRange = spreadsheet1.getCellRangeByPosition( 0, 0, col_last, 0 );
		com.sun.star.table.XColumnRowRange xColRowRange = ( com.sun.star.table.XColumnRowRange )
		UnoRuntime.queryInterface( com.sun.star.table.XColumnRowRange.class, xCellRange );

		com.sun.star.beans.XPropertySet xPropSet = null;
		com.sun.star.table.XTableColumns xColumns = xColRowRange.getColumns();
		for(int i = col_first; i <= col_last;i++){
			Object aColumnObj = xColumns.getByIndex(i);
			xPropSet = (com.sun.star.beans.XPropertySet)
			UnoRuntime.queryInterface(com.sun.star.beans.XPropertySet.class, aColumnObj);
			xPropSet.setPropertyValue("NumberFormat", col_numberformat);
		}
	}
	private void doCellNumberFormat(int col,int row,int cell_numberformat) throws NoSuchElementException, WrappedTargetException, IndexOutOfBoundsException, UnknownPropertyException, PropertyVetoException, IllegalArgumentException{
		XCell cell= cellCursor.getCellByPosition(col,row);
        UnoRuntime.queryInterface(com.sun.star.beans.XPropertySet.class, cell);
        com.sun.star.beans.XPropertySet xPropSet = null;
		xPropSet = (com.sun.star.beans.XPropertySet)
		UnoRuntime.queryInterface(com.sun.star.beans.XPropertySet.class, cell);        
		xPropSet.setPropertyValue( "NumberFormat", cell_numberformat );
	}
	
	private void doCellValue(int col,int row,Object value) throws IndexOutOfBoundsException{
		XCell cell= cellCursor.getCellByPosition(col,row);
        XText cellText;
        if(value instanceof Double){
        	cell.setValue((Double)value);
        }else if(value instanceof String){
        	cellText = (XText)UnoRuntime.queryInterface(XText.class, cell);
        	cellText.setString((String)value);
        }else{
        	
        }
	}
	private void doCellFormula(int col,int row,String formula) throws IndexOutOfBoundsException{
		XCell cell= cellCursor.getCellByPosition(col,row);
        cell.setFormula(formula);
	}
	private void doCellColor(int col,int row,int color) throws IndexOutOfBoundsException, UnknownPropertyException, PropertyVetoException, IllegalArgumentException, WrappedTargetException{
		XCell cell= cellCursor.getCellByPosition(col,row);
        UnoRuntime.queryInterface(com.sun.star.beans.XPropertySet.class, cell);
        com.sun.star.beans.XPropertySet xPropSet = null;
		xPropSet = (com.sun.star.beans.XPropertySet)
		UnoRuntime.queryInterface(com.sun.star.beans.XPropertySet.class, cell);        
		xPropSet.setPropertyValue( "CharColor", color );
		/*
		//System.out.println("Start-CellPropertie*********************************");
	      Property[] prop = xPropSet.getPropertySetInfo().getProperties();
	      for(int i = 0; i < prop.length;i++){
	    	  //System.out.println(prop[i].Name);
	    	  //System.out.println(prop[i].Attributes);
	      }
		//System.out.println("End-CellPropertie*********************************");
		*/
	    
	}
	private void doCellCharWeight(int col,int row,float charweight) throws IndexOutOfBoundsException, UnknownPropertyException, PropertyVetoException, IllegalArgumentException, WrappedTargetException{
		XCell cell= cellCursor.getCellByPosition(col,row);
        UnoRuntime.queryInterface(com.sun.star.beans.XPropertySet.class, cell);
        com.sun.star.beans.XPropertySet xPropSet = null;
		xPropSet = (com.sun.star.beans.XPropertySet)
		UnoRuntime.queryInterface(com.sun.star.beans.XPropertySet.class, cell);        
		xPropSet.setPropertyValue( "CharWeight", charweight );
		/*
		xProps.setPropertyValue("CharFontStyleName", new String("Times New Roman"));
		xProps.setPropertyValue("CharWeight", new Float(com.sun.star.awt.FontWeight.NORMAL));
		xProps.setPropertyValue("CharHeight", new Float(12));
		xText.insertString(xSentenceCursor, str_text, false);
		*/ 

	}
	
	
}
