package anmeldungUmsatz;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;

import rehaInternalFrame.JUmsaetzeInternal;
import sqlTools.SqlInfo;
import systemTools.ButtonTools;
import systemTools.JRtaTextField;
import terminKalender.DatFunk;
import terminKalender.ParameterLaden;

import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocument;
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
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.sheet.XSpreadsheets;
import com.sun.star.table.XCellRange;
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
 
	BigDecimal gesamtUmsatz = BigDecimal.valueOf(Double.parseDouble("0.00"));
	Vector<Integer> summenPos = new Vector<Integer>();
	DecimalFormat dcf = new DecimalFormat("##########0.00");
	
	int calcrow = 0;	
	ISpreadsheetDocument spreadsheetDocument = null;;
	IDocument document  = null;
	
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
		
		aktion1 = new JLabel("  ");
		aktion1.setForeground(Color.BLUE);
		this.content.add(aktion1,cc.xyw(1,4,9,CellConstraints.LEFT,CellConstraints.DEFAULT));
		
		progress1 = new JProgressBar();
		progress1.setStringPainted(true);
		this.content.add(progress1,cc.xyw(1,6,10,CellConstraints.FILL,CellConstraints.DEFAULT));
		
		aktion2 = new JLabel("  ");
		aktion2.setForeground(Color.BLUE);
		this.content.add(aktion2,cc.xyw(1,8,9,CellConstraints.LEFT,CellConstraints.DEFAULT));

		progress2 = new JProgressBar();
		progress2.setStringPainted(true);
		this.content.add(progress2,cc.xyw(1,10,10,CellConstraints.FILL,CellConstraints.DEFAULT));

		return content;
	}
	/**********************/
	private void doManageRezept(String reznum){
		Vector<Vector<String>> vec = SqlInfo.holeFelder("select * from verordn where rez_nr='"+reznum+"' LIMIT 1");
		if(vec.size()>0){
			
		}else{
			
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
		for(int i1 = 0; i1 < anzahlbehandler;i1++ ){
			//progressbar einstellen
			progress2.setValue(i1);
			aktion2.setText("ermittle Umsatz von:"+kalUsers.get(i1).get(0));

			progress1.setMinimum(0);
			progress1.setMaximum(anzahltage);
			for(int i2 = 0; i2 < anzahltage;i2++){
				if(vecBehandlungen.size() < anzahltage){
					vecBehandlungen.add(new Vector<String>());
				}
				progress1.setValue(i2);
				aktion1.setText("ermittle Umsätze vom: "+allDates.get(i1).get(i2).get(304));
				//text einstellen
				int anzahltermine = Integer.parseInt(allDates.get(i1).get(i2).get(300));
				System.out.println(kalUsers.get(i1).get(0)+" hat am "+DatFunk.sDatInDeutsch(allDates.get(i1).get(i2).get(304))+" Anzahl Termine = "+allDates.get(i1).get(i2).get(300));
				for(int i3 = 0; i3 < anzahltermine; i3++){
					if(! (reznum = allDates.get(i1).get(i2).get((i3*5)+1)).contains("@FREI")){
						// Wenn der Termin nich leer ist
						if((!reznum.trim().equals("")) && (reznum.trim().length()>2)){
							
							if("KGMAERLORH".contains(reznum.substring(0,2))){
								rawreznum = reznum; 
								if( (pos=rawreznum.indexOf("\\")) >= 0){
									pos = reznum.indexOf("\\");
									endreznum = allDates.get(i1).get(i2).get((i3*5)+1).substring(0,pos).trim(); 
									//System.out.println("Behandler="+kalUsers.get(i1).get(0)+" Tag = "+allDates.get(i1).get(i2).get(304)+"-Rezeptnummer = "+allDates.get(i1).get(i2).get((i3*5)+1).substring(0,pos).trim());
								}else{
									//System.out.println("Behandler="+kalUsers.get(i1).get(0)+" Tag = "+allDates.get(i1).get(i2).get(304)+"-Rezeptnummer = "+allDates.get(i1).get(i2).get((i3*5)+1).trim());
									endreznum = allDates.get(i1).get(i2).get((i3*5)+1).trim();
								}
								//testen ob diese Rezeptnummer an selben Tag bereits einbezogen wurde!
								if(vecBehandlungen.get(i2).indexOf(endreznum)>=0){
									//bereits erfaßt
									System.out.println("Alarm alarm  - Rezeptnummer "+endreznum+" an diesem Tag bereits erfaßt");
								}else{
									vecBehandlungen.get(i2).add(String.valueOf(endreznum));
									// an dieser Stelle nach dem Rezeptsuchen
									// den Rezeptwert analysieren udn
									// anschließend die Zeile in die Umsatzdatei (oo-calc) Schreiben
									//
									doManageRezept(endreznum);
								}
							}
						}
					}
					
				}
			}
		}
		progress1.setValue(anzahltage);
		aktion1.setText("geschafft....");

		progress2.setValue(anzahlbehandler);
		aktion2.setText("Umsatzermittlung abgeschlossen");
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	/**********************/	
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
							new SwingWorker<Void,Void>(){
								@Override
								protected Void doInBackground() throws Exception {
									try{
										setCursor(Reha.thisClass.wartenCursor);
										new SwingWorker<Void,Void>(){
											@Override
											protected Void doInBackground() throws Exception {
												if( testeKalenderUser() > 0){
													String dat1,dat2;
													dat1 = DatFunk.sDatInSQL(tfs[0].getText());
													dat2 = DatFunk.sDatInSQL(tfs[1].getText());
													try{
														if(ermittleFaelle(dat1,dat2) > 0){
															starteCalc();
															doColWith(spreadsheetDocument,0,1,2500);
															doColWith(spreadsheetDocument,2,13,2000);
															doColWith(spreadsheetDocument,14,15,2500);
															ermittleDates();															
														}
													}catch(Exception ex){
														ex.printStackTrace();
														JOptionPane.showMessageDialog(null,"Die von Ihnen eingegebenen Datumswerte sind nicht korrekt");
													}
					
												}
												setCursor(Reha.thisClass.cdefault);
												//aktion1.setText("  ");
												//progress1.setValue(0);
												System.out.println("Daten wurden gesammelt von "+allDates.size()+" Behandlern");
												System.out.println("Anzahl Tage analysiert "+allDates.get(0).size());
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
	}
	/**
	 * @throws OfficeApplicationException 
	 * @throws NOAException *****************************/
	private void starteCalc() throws OfficeApplicationException, NOAException{
		IDocumentService documentService = Reha.officeapplication.getDocumentService();
		IDocument document = documentService.constructNewDocument(IDocument.CALC, DocumentDescriptor.DEFAULT);
		spreadsheetDocument = (ISpreadsheetDocument) document;
		spreadsheetDocument.getFrame().setFocus();		
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
			try{
				Object aColumnObj = xColumns.getByIndex(i);
				xPropSet = (com.sun.star.beans.XPropertySet)
				UnoRuntime.queryInterface(com.sun.star.beans.XPropertySet.class, aColumnObj);
				xPropSet.setPropertyValue("Width", new Integer(width));
			}catch(Exception ex){
				System.out.println("Fehler bei ColumnIndex="+i);
				ex.printStackTrace();
			}
		}
	}
}
