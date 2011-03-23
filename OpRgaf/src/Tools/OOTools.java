package Tools;

import java.io.IOException;
import java.io.InputStream;

import javax.swing.JOptionPane;


import opRgaf.OpRgaf;

import org.jdesktop.swingworker.SwingWorker;

import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.desktop.IFrame;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.filter.RTFFilter;
import ag.ion.bion.officelayer.presentation.IPresentationDocument;
import ag.ion.bion.officelayer.spreadsheet.ISpreadsheetDocument;
import ag.ion.bion.officelayer.text.IText;
import ag.ion.bion.officelayer.text.ITextCursor;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextField;
import ag.ion.bion.officelayer.text.ITextRange;
import ag.ion.bion.officelayer.text.IViewCursor;
import ag.ion.noa.NOAException;
import ag.ion.noa.printing.IPrinter;
import ag.ion.noa.search.ISearchResult;
import ag.ion.noa.search.SearchDescriptor;

import com.sun.star.awt.XTopWindow;
import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.comp.helper.BootstrapException;
import com.sun.star.container.NoSuchElementException;
import com.sun.star.container.XNameContainer;
import com.sun.star.datatransfer.DataFlavor;
import com.sun.star.datatransfer.UnsupportedFlavorException;
import com.sun.star.datatransfer.XTransferable;
import com.sun.star.datatransfer.clipboard.XClipboard;
import com.sun.star.frame.XController;
import com.sun.star.frame.XFrame;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.IndexOutOfBoundsException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.sheet.XSheetCellCursor;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.sheet.XSpreadsheetDocument;
import com.sun.star.sheet.XSpreadsheets;
import com.sun.star.style.XStyle;
import com.sun.star.style.XStyleFamiliesSupplier;
import com.sun.star.table.XCell;
import com.sun.star.table.XCellRange;
import com.sun.star.text.XText;
import com.sun.star.text.XTextDocument;
import com.sun.star.text.XTextViewCursor;
import com.sun.star.text.XTextViewCursorSupplier;
import com.sun.star.uno.Exception;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.view.XLineCursor;

public class OOTools{
	public OOTools(){
		
	}
	public static void sucheLeerenPlatzhalter(ITextDocument textDocument, ITextField placeholders){
		
	}
	public static void loescheLeerenPlatzhalter(ITextDocument textDocument, ITextField placeholders){
		IViewCursor viewCursor = textDocument.getViewCursorService().getViewCursor();
		viewCursor.goToRange(placeholders.getTextRange(), false);
		XController xController = textDocument.getXTextDocument().getCurrentController();
		XTextViewCursorSupplier xTextViewCursorSupplier = (XTextViewCursorSupplier) UnoRuntime.queryInterface(XTextViewCursorSupplier.class,
		xController);
		XLineCursor xLineCursor = (XLineCursor) UnoRuntime.queryInterface(XLineCursor.class,
		xTextViewCursorSupplier.getViewCursor());
		xLineCursor.gotoStartOfLine(false);
		xLineCursor.gotoEndOfLine(true); 
		ITextCursor textCursor = viewCursor.getTextCursorFromStart();
		textCursor.goLeft((short) 1, false);
		textCursor.gotoRange(viewCursor.getTextCursorFromEnd().getEnd(), true);
		textCursor.setString("");
	}
	

	private static boolean sucheNachPlatzhalter(ITextDocument document){
		IText text = document.getTextService().getText();
		String stext = text.getText();
		int start = 0;
		int end = 0;
		String dummy;
		int vars = 0;
		int sysvar = -1;
		boolean noendfound = false;
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
	
	/*******************************************************************************************/
	@SuppressWarnings("unchecked")
	/*******************************************************************************************/
	/*******************************************************************************************/
		
	public ITextDocument starteWriterMitDatei(String url){
		try {
			if(!OpRgaf.officeapplication.isActive()){
				OpRgaf.starteOfficeApplication();
			}
			IDocumentService documentService = OpRgaf.officeapplication.getDocumentService();
			DocumentDescriptor docdescript = new DocumentDescriptor();
			docdescript.setURL(url);
			docdescript.setHidden(false);
			//IDocument document = documentService.constructNewDocument(IDocument.WRITER,docdescript );
			IDocument document = documentService.loadDocument(url,DocumentDescriptor.DEFAULT);
			ITextDocument textDocument = (ITextDocument) document;
			/*********************/
			XController xController = textDocument.getXTextDocument().getCurrentController();
			XTextViewCursorSupplier xTextViewCursorSupplier = (XTextViewCursorSupplier) UnoRuntime.queryInterface(XTextViewCursorSupplier.class,
			xController);
			XTextViewCursor xtvc = xTextViewCursorSupplier.getViewCursor();
			xtvc.gotoStart(false);
			textDocument.getFrame().setFocus();

			return (ITextDocument) textDocument;	
			
		}catch (OfficeApplicationException exception) {
			exception.printStackTrace();
		}catch (NOAException exception) {
			exception.printStackTrace();
		}
		return null;
		
	}
	public static ITextDocument starteWriterMitStream(InputStream is, String titel){
		try {
			if(!OpRgaf.officeapplication.isActive()){
				OpRgaf.starteOfficeApplication();
			}
			DocumentDescriptor d = new DocumentDescriptor();
        	d.setTitle(titel);
        	d.setFilterDefinition(RTFFilter.FILTER.getFilterDefinition(IDocument.WRITER));
			IDocumentService documentService = OpRgaf.officeapplication.getDocumentService();
			IDocument document = documentService.constructNewDocument(IDocument.WRITER, DocumentDescriptor.DEFAULT);
			ITextDocument textDocument = (ITextDocument)document;
			textDocument.getViewCursorService().getViewCursor().getTextCursorFromStart().insertDocument(is, new RTFFilter());
			XController xController = textDocument.getXTextDocument().getCurrentController();
			XTextViewCursorSupplier xTextViewCursorSupplier = (XTextViewCursorSupplier) UnoRuntime.queryInterface(XTextViewCursorSupplier.class,
			xController);
			XTextViewCursor xtvc = xTextViewCursorSupplier.getViewCursor();
			xtvc.gotoStart(false);
			textDocument.getFrame().setFocus();
			is.close();
			return (ITextDocument) textDocument;	
			
		}catch (OfficeApplicationException exception) {
			exception.printStackTrace();
		}catch (NOAException exception) {
			exception.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}

	public ISpreadsheetDocument starteCalcMitDatei(String url){
		try {
			if(!OpRgaf.officeapplication.isActive()){
				OpRgaf.starteOfficeApplication();
			}
			IDocumentService documentService = OpRgaf.officeapplication.getDocumentService();
			DocumentDescriptor docdescript = new DocumentDescriptor();
			docdescript.setURL(url);
			docdescript.setHidden(false);
			IDocument document = documentService.loadDocument(url,DocumentDescriptor.DEFAULT);
			//IDocument document = documentService.constructNewDocument(IDocument.CALC, DocumentDescriptor.DEFAULT);
			ISpreadsheetDocument spreadsheetDocument = (ISpreadsheetDocument) document;
			/********************/
			spreadsheetDocument.getFrame().setFocus();
			return (ISpreadsheetDocument) spreadsheetDocument;
			
		} 
		catch (Throwable exception) {
			exception.printStackTrace();
		} 
		return null;
	}

	public static void starteLeerenCalc(){
		try {
			IDocumentService documentService = OpRgaf.officeapplication.getDocumentService();
			IDocument document = documentService.constructNewDocument(IDocument.CALC, DocumentDescriptor.DEFAULT);
			ISpreadsheetDocument spreadsheetDocument = (ISpreadsheetDocument) document;
			spreadsheetDocument.getFrame().setFocus();
		} 
		catch (Throwable exception) {
			exception.printStackTrace();
		} 
	}
	
	public static void starteLeerenImpress(){
		try {
			IDocumentService documentService = OpRgaf.officeapplication.getDocumentService();
			IDocument document = documentService.constructNewDocument(IDocument.IMPRESS, DocumentDescriptor.DEFAULT);
			IPresentationDocument presentationDocument = (IPresentationDocument) document;
			presentationDocument.getFrame().setFocus();
		}
		catch(Throwable throwable) {
			throwable.printStackTrace();
		}
		
		
	}
	public static void setzePapierFormat(ITextDocument textDocument,int hoch,int breit) throws NoSuchElementException, WrappedTargetException, UnknownPropertyException, PropertyVetoException, IllegalArgumentException{
		XTextDocument xTextDocument = textDocument.getXTextDocument();
		XStyleFamiliesSupplier xSupplier = (XStyleFamiliesSupplier) UnoRuntime.queryInterface(XStyleFamiliesSupplier.class,
		xTextDocument);
		XNameContainer family = (XNameContainer) UnoRuntime.queryInterface(XNameContainer.class,
		xSupplier.getStyleFamilies().getByName("PageStyles"));
		XStyle xStyle = (XStyle) UnoRuntime.queryInterface(XStyle.class, family.getByName("Standard"));
		XPropertySet xStyleProps = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
		xStyle);
		/*
		com.sun.star.beans.Property[] props = xStyleProps.getPropertySetInfo().getProperties();
		for (int i = 0; i < props.length; i++) {
		//System.out.println(props[i] .Name + " = "
		+ xStyleProps.getPropertyValue(props[i].Name));
		}
		//z.B. f�r A5
		 * 
		 */
		xStyleProps.setPropertyValue("Height", hoch);
		xStyleProps.setPropertyValue("Width", breit);
	}
	public static void setzePapierFormatCalc(ISpreadsheetDocument document,int hoch,int breit) throws NoSuchElementException, WrappedTargetException, UnknownPropertyException, PropertyVetoException, IllegalArgumentException{
		XSpreadsheetDocument xSpreadSheetDocument = document.getSpreadsheetDocument();
		XStyleFamiliesSupplier xSupplier = (XStyleFamiliesSupplier) UnoRuntime.queryInterface(XStyleFamiliesSupplier.class,
		xSpreadSheetDocument);
		XNameContainer family = (XNameContainer) UnoRuntime.queryInterface(XNameContainer.class,
		xSupplier.getStyleFamilies().getByName("PageStyles"));
		XStyle xStyle = (XStyle) UnoRuntime.queryInterface(XStyle.class, family.getByName("Standard"));
		XPropertySet xStyleProps = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
		xStyle);
		xStyleProps.setPropertyValue("Height", hoch);
		xStyleProps.setPropertyValue("Width", breit);
	}
	public static void setzeRaenderCalc(ISpreadsheetDocument document,int oben,int unten,int links,int rechts) throws NoSuchElementException, WrappedTargetException, UnknownPropertyException, PropertyVetoException, IllegalArgumentException{
		XSpreadsheetDocument xSpreadSheetDocument = document.getSpreadsheetDocument();
		XStyleFamiliesSupplier xSupplier = (XStyleFamiliesSupplier) UnoRuntime.queryInterface(XStyleFamiliesSupplier.class,
		xSpreadSheetDocument);
    	XNameContainer family = (XNameContainer) UnoRuntime.queryInterface(XNameContainer.class,
    	xSupplier.getStyleFamilies().getByName("PageStyles"));
    	XStyle xStyle = (XStyle) UnoRuntime.queryInterface(XStyle.class, family.getByName("Standard") );
    	XPropertySet xStyleProps = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
    	xStyle);
    	xStyleProps.setPropertyValue("TopMargin",oben);
    	xStyleProps.setPropertyValue("BottomMargin",unten);
    	xStyleProps.setPropertyValue("LeftMargin",links);
    	xStyleProps.setPropertyValue("RightMargin",rechts);
	}


	public static void setzeRaender(ITextDocument textDocument,int oben,int unten,int links,int rechts) throws NoSuchElementException, WrappedTargetException, UnknownPropertyException, PropertyVetoException, IllegalArgumentException{
    	XTextDocument xTextDocument = textDocument.getXTextDocument();
    	XStyleFamiliesSupplier xSupplier = (XStyleFamiliesSupplier) UnoRuntime.queryInterface(XStyleFamiliesSupplier.class,
    	xTextDocument);
    	XNameContainer family = (XNameContainer) UnoRuntime.queryInterface(XNameContainer.class,
    	xSupplier.getStyleFamilies().getByName("PageStyles"));
    	XStyle xStyle = (XStyle) UnoRuntime.queryInterface(XStyle.class, family.getByName("Standard") );
    	XPropertySet xStyleProps = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
    	xStyle);
    	xStyleProps.setPropertyValue("TopMargin",oben);
    	xStyleProps.setPropertyValue("BottomMargin",unten);
    	xStyleProps.setPropertyValue("LeftMargin",links);
    	xStyleProps.setPropertyValue("RightMargin",rechts);
	}
		
		
	
	public static void inDenVordergrund(ITextDocument textDocumentx){
		ITextDocument textDocument = (ITextDocument) textDocumentx; 
		IFrame officeFrame = textDocument.getFrame();
		XFrame xFrame = officeFrame.getXFrame();
		XTopWindow topWindow = (XTopWindow)
		UnoRuntime.queryInterface(XTopWindow.class,
		xFrame. getContainerWindow());
		//hier beide methoden, beide sind nötig
		xFrame.activate();
		topWindow.toFront();

		
	}
	public static void ooOrgAnmelden(){
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws java.lang.Exception {
		        IDocumentDescriptor docdescript = new DocumentDescriptor();
		       	docdescript.setHidden(true);
				IDocument document = null;
				ITextDocument textDocument = null;
				OpRgaf.thisFrame.setCursor(OpRgaf.thisClass.wartenCursor);
				try {
					if(!OpRgaf.officeapplication.isActive()){
						OpRgaf.starteOfficeApplication();
					}
					
					IDocumentService documentService = OpRgaf.officeapplication.getDocumentService();
					document = documentService.constructNewDocument(IDocument.WRITER, docdescript);
					textDocument = (ITextDocument)document;
					textDocument.close();
				} 
				catch (OfficeApplicationException exception) {
					exception.printStackTrace();
				} 
				catch (NOAException exception) {
					exception.printStackTrace();
				}
				return null;
			}
			
		}.execute();
	}
	
	public static void druckerSetzen(ITextDocument textDocument,String drucker){

		/**********************/
		if(drucker != null){
			String druckerName = null;
			try {
				druckerName = textDocument.getPrintService().getActivePrinter().getName();
			} catch (NOAException e) {
				e.printStackTrace();
			}
			//Wenn nicht gleich wie im Übergebenen Parameter angegeben -> Drucker wechseln
			IPrinter iprint = null;
			if(! druckerName.equals(drucker)){
				try {
					iprint = (IPrinter) textDocument.getPrintService().createPrinter(drucker);
				} catch (NOAException e) {
					e.printStackTrace();
				}
				try {
					textDocument.getPrintService().setActivePrinter(iprint);
				} catch (NOAException e) {
					e.printStackTrace();
				}
			}
		}		
	}
	/***********************OO-Calc Funktionen*******************************/
	public static void doColWidth(ISpreadsheetDocument spreadsheetDocument,String sheetName, int col_first,int col_last,int width) throws NoSuchElementException, WrappedTargetException, IndexOutOfBoundsException, UnknownPropertyException, PropertyVetoException, IllegalArgumentException{
		XSpreadsheets spreadsheets = spreadsheetDocument.getSpreadsheetDocument().getSheets();
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
			xPropSet.setPropertyValue("Width", width);
		}
	}
	public static void doColTextAlign(ISpreadsheetDocument spreadsheetDocument,String sheetName, int col_first,int col_last,int col_textalign) throws NoSuchElementException, WrappedTargetException, IndexOutOfBoundsException, UnknownPropertyException, PropertyVetoException, IllegalArgumentException{
		XSpreadsheets spreadsheets = spreadsheetDocument.getSpreadsheetDocument().getSheets();
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
	public static void doColNumberFormat(ISpreadsheetDocument spreadsheetDocument,String sheetName, int col_first,int col_last,int col_numberformat) throws NoSuchElementException, WrappedTargetException, IndexOutOfBoundsException, UnknownPropertyException, PropertyVetoException, IllegalArgumentException{
		XSpreadsheets spreadsheets = spreadsheetDocument.getSpreadsheetDocument().getSheets();
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
	public static void doCellNumberFormat(XSheetCellCursor cellCursor,int col,int row,int cell_numberformat) throws NoSuchElementException, WrappedTargetException, IndexOutOfBoundsException, UnknownPropertyException, PropertyVetoException, IllegalArgumentException{
		XCell cell= cellCursor.getCellByPosition(col,row);
        UnoRuntime.queryInterface(com.sun.star.beans.XPropertySet.class, cell);
        com.sun.star.beans.XPropertySet xPropSet = null;
		xPropSet = (com.sun.star.beans.XPropertySet)
		UnoRuntime.queryInterface(com.sun.star.beans.XPropertySet.class, cell);        
		xPropSet.setPropertyValue( "NumberFormat", cell_numberformat );
	}
	
	public static void doCellValue(XSheetCellCursor cellCursor,int col,int row,Object value) throws IndexOutOfBoundsException{
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
	public static void doCellFormula(XSheetCellCursor cellCursor,int col,int row,String formula) throws IndexOutOfBoundsException{
		XCell cell= cellCursor.getCellByPosition(col,row);
        cell.setFormula(formula);
	}
	public static void doCellColor(XSheetCellCursor cellCursor,int col,int row,int color) throws IndexOutOfBoundsException, UnknownPropertyException, PropertyVetoException, IllegalArgumentException, WrappedTargetException{
		XCell cell= cellCursor.getCellByPosition(col,row);
        UnoRuntime.queryInterface(com.sun.star.beans.XPropertySet.class, cell);
        com.sun.star.beans.XPropertySet xPropSet = null;
		xPropSet = (com.sun.star.beans.XPropertySet)
		UnoRuntime.queryInterface(com.sun.star.beans.XPropertySet.class, cell);        
		xPropSet.setPropertyValue( "CharColor", color );
	}
	public static void doCellFontBold(XSheetCellCursor cellCursor,int col,int row) throws IndexOutOfBoundsException, UnknownPropertyException, PropertyVetoException, IllegalArgumentException, WrappedTargetException{
		XCell cell= cellCursor.getCellByPosition(col,row);
        UnoRuntime.queryInterface(com.sun.star.beans.XPropertySet.class, cell);
        com.sun.star.beans.XPropertySet xPropSet = null;
		xPropSet = (com.sun.star.beans.XPropertySet)
		UnoRuntime.queryInterface(com.sun.star.beans.XPropertySet.class, cell);        
		xPropSet.setPropertyValue( "CharWeight",com.sun.star.awt.FontWeight.BOLD );
		/* Beispiele für Fonthandling
		xPropSet.setPropertyValue("CharFontStyleName", new String("Times New Roman"));
		xPropSet.setPropertyValue("CharWeight", new Float(com.sun.star.awt.FontWeight.NORMAL));
		xPropSet.setPropertyValue("CharHeight", new Float(12));
		*/ 
	}
	
	public static void doCellFontItalic(XSheetCellCursor cellCursor,int col,int row) throws IndexOutOfBoundsException, UnknownPropertyException, PropertyVetoException, IllegalArgumentException, WrappedTargetException{
		XCell cell= cellCursor.getCellByPosition(col,row);
        UnoRuntime.queryInterface(com.sun.star.beans.XPropertySet.class, cell);
        com.sun.star.beans.XPropertySet xPropSet = null;
		xPropSet = (com.sun.star.beans.XPropertySet)
		UnoRuntime.queryInterface(com.sun.star.beans.XPropertySet.class, cell);        
		xPropSet.setPropertyValue( "CharPosture", com.sun.star.awt.FontSlant.ITALIC );
	}
	
	public static void doCellFontSize(XSheetCellCursor cellCursor,int col,int row,Float size) throws IndexOutOfBoundsException, UnknownPropertyException, PropertyVetoException, IllegalArgumentException, WrappedTargetException{
		XCell cell= cellCursor.getCellByPosition(col,row);
        UnoRuntime.queryInterface(com.sun.star.beans.XPropertySet.class, cell);
        com.sun.star.beans.XPropertySet xPropSet = null;
		xPropSet = (com.sun.star.beans.XPropertySet)
		UnoRuntime.queryInterface(com.sun.star.beans.XPropertySet.class, cell);    
		xPropSet.setPropertyValue("CharHeight", size);
		xPropSet.setPropertyValue( "CharWeight",com.sun.star.awt.FontWeight.NORMAL );
		/* Beispiele für Fonthandling
		xPropSet.setPropertyValue("CharFontStyleName", new String("Times New Roman"));
		xPropSet.setPropertyValue("CharWeight", new Float(com.sun.star.awt.FontWeight.NORMAL));
		xPropSet.setPropertyValue("CharHeight", new Float(12));
		*/ 
	}
	public static void doCellFontName(XSheetCellCursor cellCursor,int col,int row,String fontname) throws IndexOutOfBoundsException, UnknownPropertyException, PropertyVetoException, IllegalArgumentException, WrappedTargetException{
		XCell cell= cellCursor.getCellByPosition(col,row);
        UnoRuntime.queryInterface(com.sun.star.beans.XPropertySet.class, cell);
        com.sun.star.beans.XPropertySet xPropSet = null;
		xPropSet = (com.sun.star.beans.XPropertySet)
		UnoRuntime.queryInterface(com.sun.star.beans.XPropertySet.class, cell);    
		xPropSet.setPropertyValue("CharFontName", fontname);
		//PropSet.setPropertyValue( "CharWeight",com.sun.star.awt.FontWeight.NORMAL );
		/* Beispiele für Fonthandling
		xPropSet.setPropertyValue("CharFontStyleName", new String("Times New Roman"));
		xPropSet.setPropertyValue("CharWeight", new Float(com.sun.star.awt.FontWeight.NORMAL));
		xPropSet.setPropertyValue("CharHeight", new Float(12));
		*/ 
	}
	public static void getCellPropertiesName(XSheetCellCursor cellCursor,int col,int row,String fontname) throws IndexOutOfBoundsException, UnknownPropertyException, PropertyVetoException, IllegalArgumentException, WrappedTargetException{
		XCell cell= cellCursor.getCellByPosition(col,row);
        UnoRuntime.queryInterface(com.sun.star.beans.XPropertySet.class, cell);
        com.sun.star.beans.XPropertySet xPropSet = null;
		xPropSet = (com.sun.star.beans.XPropertySet)
		UnoRuntime.queryInterface(com.sun.star.beans.XPropertySet.class, cell);    
		
	}

	
	
	/*******************************************************/
	public static void holeClipBoard() {
		try {
			XComponentContext xComponentContext;

			xComponentContext = (XComponentContext) com.sun.star.comp.helper.Bootstrap.bootstrap();
			XMultiComponentFactory xMultiComponentFactory;
			xMultiComponentFactory = (XMultiComponentFactory) OpRgaf.officeapplication.getDocumentService();
		
			Object oClipboard =
		          xMultiComponentFactory.createInstanceWithContext(
		          "com.sun.star.datatransfer.clipboard.SystemClipboard", 
		          xComponentContext);
			XClipboard xClipboard = (XClipboard)
	        UnoRuntime.queryInterface(XClipboard.class, oClipboard);

			//---------------------------------------------------
			// 	get a list of formats currently on the clipboard
			//---------------------------------------------------

			XTransferable xTransferable = xClipboard.getContents();

			DataFlavor[] aDflvArr = xTransferable.getTransferDataFlavors();

			// print all available formats

			//System.out.println("Reading the clipboard...");
			//System.out.println("Available clipboard formats:");

			DataFlavor aUniFlv = null;

			for (int i=0;i<aDflvArr.length;i++)	{
				//System.out.println( "MimeType: " + 
	            //    aDflvArr[i].MimeType + 
	              //  " HumanPresentableName: " + 
	            //    aDflvArr[i].HumanPresentableName );    

				// if there is the format unicode text on the clipboard save the
				// corresponding DataFlavor so that we can later output the string

				if (aDflvArr[i].MimeType.equals("text/plain;charset=utf-16"))
				{     
	                aUniFlv = aDflvArr[i];
				}
			}
			try{
				if (aUniFlv != null){
	                //System.out.println("Unicode text on the clipboard...");
	                Object aData = xTransferable.getTransferData(aUniFlv);      
				}
			}catch(UnsupportedFlavorException ex){
				System.err.println( "Requested format is not available" );
			}
		} catch (OfficeApplicationException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} catch (BootstrapException e) {
			e.printStackTrace();
		}

	}
	
}
