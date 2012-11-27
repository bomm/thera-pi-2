package rehaUrlaub;



import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;



import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.spreadsheet.ISpreadsheetDocument;
import ag.ion.noa.NOAException;

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

import CommonTools.FileTools;
import CommonTools.OOTools;

public class RehaUrlaubCalc {

	RehaUrlaubPanel eltern = null;
	String urlaubsdatei = "";
	
	DecimalFormat dcf = new DecimalFormat("##########0.00");
	
	int calcrow = 0;	
	ISpreadsheetDocument spreadsheetDocument = null;;
	IDocument document  = null;
	XSheetCellCursor cellCursor = null;

	
	public RehaUrlaubCalc(RehaUrlaubPanel xeltern,String jahr,String user){
		this.eltern = xeltern;
		testeObExistiert(jahr,user);
		try {
			starteCalc();
			werteSchreiben();
		} catch (NoSuchElementException e) {
			e.printStackTrace();
			spreadSheetSchliessen();
		} catch (WrappedTargetException e) {
			e.printStackTrace();
			spreadSheetSchliessen();
		} catch (UnknownPropertyException e) {
			e.printStackTrace();
			spreadSheetSchliessen();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
			spreadSheetSchliessen();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			spreadSheetSchliessen();
		} catch (OfficeApplicationException e) {
			e.printStackTrace();
			spreadSheetSchliessen();
		} catch (NOAException e) {
			e.printStackTrace();
			spreadSheetSchliessen();
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
			spreadSheetSchliessen();
		}
		
	}
	private void spreadSheetSchliessen(){
		if(spreadsheetDocument != null){
			spreadsheetDocument.close();
		}
	}
	private boolean testeObExistiert(String jahr,String user){
		boolean ret = false;
		String vorlage = RehaUrlaub.urlaubsDateiVorlage.replace(".ods", "");
		String pfadvorlage = RehaUrlaub.progHome+"vorlagen/"+RehaUrlaub.aktIK+"/"+RehaUrlaub.urlaubsDateiVorlage;
		this.urlaubsdatei = RehaUrlaub.progHome+"urlaub/"+RehaUrlaub.aktIK+"/"+vorlage+"_"+jahr+"_"+user+".ods";
		File f = new File(urlaubsdatei);
		if(! f.exists()){
			JOptionPane.showMessageDialog(null, "Urlaubstabelle für das Jahr -> "+jahr+" <- und User -> "+user+" <- wird angelegt");
			try {
				FileTools.copyFile(new File(pfadvorlage), f, 8192, true);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Fehler beim anlegen der Urlaubstabelle");
				e.printStackTrace();
			}
		}else{
			//System.out.println(pfadvorlage);
			//System.out.println(urlaubsdatei);
		}
		return ret;
	}
	
	
	
	private void starteCalc() throws OfficeApplicationException, NOAException, NoSuchElementException, WrappedTargetException, UnknownPropertyException, PropertyVetoException, IllegalArgumentException{
		if(!RehaUrlaub.officeapplication.isActive()){
			RehaUrlaub.starteOfficeApplication();
		}
		IDocumentService documentService = RehaUrlaub.officeapplication.getDocumentService();
        IDocumentDescriptor docdescript = new DocumentDescriptor();
       	docdescript.setHidden(true);
        docdescript.setAsTemplate(false);
		document = documentService.loadDocument(this.urlaubsdatei, docdescript);
		spreadsheetDocument = (ISpreadsheetDocument) document;
		//Tools.OOTools.setzePapierFormatCalc((ISpreadsheetDocument) spreadsheetDocument, 21000, 29700);
		//Tools.OOTools.setzeRaenderCalc((ISpreadsheetDocument) spreadsheetDocument, 1000,1000, 1000, 1000);
		
		XSpreadsheets spreadsheets = spreadsheetDocument.getSpreadsheetDocument().getSheets();
		String sheetName= "Tabelle1";
		XSpreadsheet spreadsheet1 = (XSpreadsheet)UnoRuntime.queryInterface(XSpreadsheet.class,spreadsheets.getByName(sheetName));
		cellCursor = spreadsheet1.createCursor();

	}
	private void werteSchreiben() throws IndexOutOfBoundsException{
		int rows = eltern.tabmod.getRowCount();
		int row = 0;
		for(int i = 0; i < rows; i++){
			//System.out.println((Boolean) eltern.tabmod.getValueAt(i, 25));
			if( ((Boolean) eltern.tabmod.getValueAt(i, 25)) ){
				row = setRow(i);
				OOTools.doCellValue(cellCursor, 0, row,Double.parseDouble(Integer.toString((Integer)eltern.tabmod.getValueAt(i, 0))));
				OOTools.doCellValue(cellCursor, 1, row, eltern.tabmod.getValueAt(i, 1).toString() );
				
				OOTools.doCellValue(cellCursor, 2, row, (Double)eltern.tabmod.getValueAt(i, 2) );
				farbTest(3,row,i,3);
				OOTools.doCellValue(cellCursor, 3, row, eltern.tabmod.getValueAt(i, 3).toString() );
				
				OOTools.doCellValue(cellCursor, 4, row, (Double)eltern.tabmod.getValueAt(i, 4) );
				farbTest(5,row,i,5);
				OOTools.doCellValue(cellCursor, 5, row, eltern.tabmod.getValueAt(i, 5).toString() );

				OOTools.doCellValue(cellCursor, 6, row, (Double)eltern.tabmod.getValueAt(i, 6) );
				farbTest(7,row,i,7);
				OOTools.doCellValue(cellCursor, 7, row, eltern.tabmod.getValueAt(i, 7).toString() );

				OOTools.doCellValue(cellCursor, 8, row, (Double)eltern.tabmod.getValueAt(i, 8) );
				farbTest(9,row,i,9);
				OOTools.doCellValue(cellCursor, 9, row, eltern.tabmod.getValueAt(i, 9).toString() );

				OOTools.doCellValue(cellCursor, 10, row, (Double)eltern.tabmod.getValueAt(i, 10) );
				farbTest(11,row,i,11);
				OOTools.doCellValue(cellCursor, 11, row, eltern.tabmod.getValueAt(i, 11).toString() );

				OOTools.doCellValue(cellCursor, 12, row, (Double)eltern.tabmod.getValueAt(i, 12) );
				farbTest(13,row,i,13);
				OOTools.doCellValue(cellCursor, 13, row, eltern.tabmod.getValueAt(i, 13).toString() );

				OOTools.doCellValue(cellCursor, 14, row, (Double)eltern.tabmod.getValueAt(i, 14) );
				farbTest(15,row,i,15);
				OOTools.doCellValue(cellCursor, 15, row, eltern.tabmod.getValueAt(i, 15).toString() );
				
				OOTools.doCellValue(cellCursor, 17, row, (Double)eltern.tabmod.getValueAt(i, 17) );
				
				OOTools.doCellValue(cellCursor, 19, row, (Double)eltern.tabmod.getValueAt(i, 19) );
				OOTools.doCellValue(cellCursor, 20, row, (Double)eltern.tabmod.getValueAt(i, 20) );
				OOTools.doCellValue(cellCursor, 21, row, (Double)eltern.tabmod.getValueAt(i, 21) );
			}
		}
		final ISpreadsheetDocument xspredsheetDocument = spreadsheetDocument;
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				xspredsheetDocument.getFrame().getXFrame().getContainerWindow().setVisible(true);
				xspredsheetDocument.getFrame().setFocus();
			}
		});

		
	}
	private void farbTest(int calccol,int calcrow,int tablerow,int tablecol){
		try {
			if(eltern.tabmod.getValueAt(tablerow, tablecol).toString().startsWith("Ff")){
				OOTools.doCellColor(cellCursor, calccol, calcrow, 0x529e06);				
			}else if(eltern.tabmod.getValueAt(tablerow, tablecol).toString().startsWith("Uu")){
				OOTools.doCellColor(cellCursor, calccol, calcrow, 0x0000ff);
			}else if(eltern.tabmod.getValueAt(tablerow, tablecol).toString().startsWith("Kk")){
				OOTools.doCellColor(cellCursor, calccol, calcrow, 0xff0000);
			}
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
	private int setRow(int row){
		return row+11;
	}

	
	

}
