package CommonTools;



import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.jdesktop.swingworker.SwingWorker;


import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.desktop.IFrame;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.DocumentException;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.event.IDocumentEvent;
import ag.ion.bion.officelayer.event.IDocumentListener;
import ag.ion.bion.officelayer.event.IEvent;
import ag.ion.bion.officelayer.filter.RTFFilter;
import ag.ion.bion.officelayer.presentation.IPresentationDocument;
import ag.ion.bion.officelayer.spreadsheet.ISpreadsheetDocument;
import ag.ion.bion.officelayer.text.IText;
import ag.ion.bion.officelayer.text.ITextCursor;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextField;
import ag.ion.bion.officelayer.text.ITextFieldService;
import ag.ion.bion.officelayer.text.ITextRange;
import ag.ion.bion.officelayer.text.ITextTableCell;
import ag.ion.bion.officelayer.text.ITextTableCellProperties;
import ag.ion.bion.officelayer.text.IViewCursor;
import ag.ion.bion.officelayer.text.TextException;
import ag.ion.noa.NOAException;
import ag.ion.noa.internal.printing.PrintProperties;
import ag.ion.noa.printing.IPrinter;
import ag.ion.noa.search.ISearchResult;
import ag.ion.noa.search.SearchDescriptor;

import com.sun.star.awt.XTopWindow;
import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.comp.helper.BootstrapException;
import com.sun.star.container.NoSuchElementException;
import com.sun.star.container.XEnumeration;
import com.sun.star.container.XEnumerationAccess;
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
import com.sun.star.lang.XServiceInfo;
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
import com.sun.star.text.XTextField;
import com.sun.star.text.XTextFieldsSupplier;
import com.sun.star.text.XTextRange;
import com.sun.star.text.XTextTable;
import com.sun.star.text.XTextTableCursor;
import com.sun.star.text.XTextViewCursor;
import com.sun.star.text.XTextViewCursorSupplier;
import com.sun.star.uno.Exception;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.view.XLineCursor;
import com.sun.star.util.XNumberFormats;

public class OOTools{
	public OOTools(){
		
	}
	public static synchronized void sucheLeerenPlatzhalter(ITextDocument textDocument, ITextField placeholders){
		
	}
	public static synchronized void loescheLeerenPlatzhalter(ITextDocument textDocument, ITextField placeholders){
		try{
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
		}catch(java.lang.IllegalArgumentException ex){
			ex.printStackTrace();
		}
	}
	public static XTextTableCursor doMergeCellsInTextTabel(XTextTable table,String startCell,String endCell){
		//System.out.println(startCell+" / "+endCell);
		XTextTableCursor cursor = table.createCursorByCellName(startCell);
		cursor.gotoCellByName(endCell, true);
		cursor.mergeRange(); 
		return cursor;
	}
	public static void setOneCellProperty(ITextTableCell cell,boolean italic,boolean bold,boolean underline,int color,float size) throws Exception{
		//ITextTableCellProperties props = cell.getProperties();
		//XPropertySet xprops = props.getXPropertySet();
		try {
			cell.getCharacterProperties().setFontItalic(italic);
			cell.getCharacterProperties().setFontBold(bold);
			cell.getCharacterProperties().setFontUnderline(underline);
			cell.getCharacterProperties().setFontColor(color);
			cell.getCharacterProperties().setFontSize(size);
		} catch (TextException e) {
			e.printStackTrace();
		}
		
	}
	public static void setOneCellWidth(ITextTableCell cell,short width) throws Exception{
		ITextTableCellProperties props = cell.getProperties();
		XPropertySet xpropset = props.getXPropertySet();
		xpropset.setPropertyValue("Width", width);		
	}	
	public static synchronized void printAndClose(final ITextDocument textDocument, final int exemplare){
		
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws java.lang.Exception {
					try {
						Thread.sleep(100);
						PrintProperties printprop = new PrintProperties ((short)exemplare,null);
						textDocument.getPrintService().print(printprop);
						while(textDocument.getPrintService().isActivePrinterBusy()){
							Thread.sleep(50);
						}
						Thread.sleep(150);
						textDocument.close();
						Thread.sleep(150);
					} catch (InterruptedException e) {
						JOptionPane.showMessageDialog(null,"Fehler im Rechnungsdruck, Fehler = InterruptedException" );
						e.printStackTrace();
					} catch (DocumentException e) {
						JOptionPane.showMessageDialog(null,"Fehler im Rechnungsdruck, Fehler = DocumentException" );
						e.printStackTrace();
					} catch (NOAException e) {
						JOptionPane.showMessageDialog(null,"Fehler in der Abfrage isActivePrinterBusy()" );
						e.printStackTrace();
					}
					return null;
				}
				
			}.execute();
	}
	public static synchronized ITextField[] holePlatzhalter(ITextDocument textDocument){
		ITextField[] placeholders = null;
		ITextFieldService textFieldService = null;
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		try {
			textFieldService = textDocument.getTextFieldService();

			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			Thread.sleep(50);
			placeholders = textFieldService.getPlaceholderFields();
			Thread.sleep(75);
		} catch (TextException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return placeholders.clone();
	}
	/**************************************************************************************/
    private static ArrayList<XTextRange> testePlatzhalter(XTextDocument xTextDocument){
    	if(xTextDocument==null){return null;}
    	long millis = 50;
    	ArrayList<XTextRange> arrayList = new ArrayList<XTextRange>();
		    try {
	 		      XTextFieldsSupplier xTextFieldsSupplier = (XTextFieldsSupplier)UnoRuntime.queryInterface(XTextFieldsSupplier.class, xTextDocument);
	 		      Thread.sleep(millis);
	 		      XEnumerationAccess xEnumerationAccess = xTextFieldsSupplier.getTextFields();
	 		      Thread.sleep(millis);
	 		      XEnumeration xEnumeration = xEnumerationAccess.createEnumeration();
	 		      while(xEnumeration.hasMoreElements()) {
	 		        Object object = xEnumeration.nextElement();
	 		        Thread.sleep(millis);
	 		        XTextField xTextField = (XTextField)UnoRuntime.queryInterface(XTextField.class, object);
	 		        XServiceInfo xInfo = (XServiceInfo)UnoRuntime.queryInterface(XServiceInfo.class, xTextField);
	 		        XTextRange range = xTextField.getAnchor();
	 		        // nur die Platzhalter
	 		        if(xInfo.supportsService("com.sun.star.text.TextField.JumpEdit") /* || 
	 		        		xInfo.supportsService("com.sun.star.text.TextField.User")*/){
	 		          arrayList.add(range);
	 		        }
	 		      }
		    }catch(Exception exception) {
		    	exception.printStackTrace();
		    } catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    return arrayList;
  }	
	/**************************************************************************************/
	/**************************************************************************************/
	/**************************************************************************************/
	/*******************************************************************************************/
	private static boolean sucheNachPlatzhalter(ITextDocument document){
		IText text = document.getTextService().getText();
		String stext = text.getText();
		int start = 0;
		//int end = 0;
		String dummy;
		int vars = 0;
		//int sysvar = -1;
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
	/*******************************************************************************************/
	/*******************************************************************************************/
	public static synchronized void setzePapierFormat(ITextDocument textDocument,int hoch,int breit) throws NoSuchElementException, WrappedTargetException, UnknownPropertyException, PropertyVetoException, IllegalArgumentException{
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
		//z.B. für A5
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
	/*************************************************************************/
		
		
	
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
	
	public static void druckerSetzen(ITextDocument textDocument,String drucker){

		/**********************/
		if(drucker != null){
			String druckerName = null;
			try {
				druckerName = textDocument.getPrintService().getActivePrinter().getName();
			} catch (NOAException e) {
				e.printStackTrace();
			} catch (NullPointerException ex){
				ex.printStackTrace();
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
        }else if(value instanceof Integer){
        	cell.setValue((Integer)value);
        }else if(value instanceof Date){
        	//System.out.println("date");
        	//cell.setValue( ((Date)value).getTime());
        }else if(value instanceof Long){
        	cell.setValue((Long)value);
        }
	}
	/*
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
	*/
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
		/* Beispiel für Auflistung der Property-Namen
		//System.out.println("Start-CellPropertie*********************************");
	      Property[] prop = xPropSet.getPropertySetInfo().getProperties();
	      for(int i = 0; i < prop.length;i++){
	    	  //System.out.println(prop[i].Name);
	    	  //System.out.println(prop[i].Attributes);
	      }
		//System.out.println("End-CellPropertie*********************************");
		*/
	    
	}
	public static void doCellFontBold(XSheetCellCursor cellCursor,int col,int row) throws IndexOutOfBoundsException, UnknownPropertyException, PropertyVetoException, IllegalArgumentException, WrappedTargetException{
		XCell cell= cellCursor.getCellByPosition(col,row);
        UnoRuntime.queryInterface(com.sun.star.beans.XPropertySet.class, cell);
        com.sun.star.beans.XPropertySet xPropSet = null;
		xPropSet = (com.sun.star.beans.XPropertySet)
		UnoRuntime.queryInterface(com.sun.star.beans.XPropertySet.class, cell);        
		xPropSet.setPropertyValue( "CharWeight",com.sun.star.awt.FontWeight.BOLD );
		/* Beispiele für Fonthandling
		xPropSet.setPropertyValue("CharFontStyleName", String.valueOf("Times New Roman"));
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
	public static String doOODate(String datum){
		String aDateStr = null;
		try{
			aDateStr = datum.substring(3,5) + "/" + 
			datum.substring(0,2) + "/" + 
			datum.substring(6);
		}catch(NullPointerException ex){
			aDateStr = "";
		}
		return String.valueOf(aDateStr);
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
	public static void doCellDateFormatGerman(ISpreadsheetDocument spreadsheetDocument,XSheetCellCursor cellCursor,int col,int row,boolean jahrvierstellig) throws IndexOutOfBoundsException, UnknownPropertyException, PropertyVetoException, IllegalArgumentException, WrappedTargetException{
		XCell cell= cellCursor.getCellByPosition(col,row);

	    com.sun.star.util.XNumberFormatsSupplier xNumberFormatsSupplier =
	          (com.sun.star.util.XNumberFormatsSupplier)
	          UnoRuntime.queryInterface(
	          com.sun.star.util.XNumberFormatsSupplier.class, spreadsheetDocument.getSpreadsheetDocument() );
	    /******************/
		if(xNumberFormatsSupplier == null){
			System.out.println("XNumberFormatsSupplier = null");
			return;
		}
	    /******************/
		com.sun.star.util.XNumberFormats xNumberFormats = (XNumberFormats)
	          xNumberFormatsSupplier.getNumberFormats();
		/******************/
		com.sun.star.util.XNumberFormatTypes xNumberFormatTypes =
			          (com.sun.star.util.XNumberFormatTypes)
			          UnoRuntime.queryInterface(
			          com.sun.star.util.XNumberFormatTypes.class, xNumberFormats );
		/******************/
		com.sun.star.lang.Locale aLocale = new com.sun.star.lang.Locale();
	    int nFormat = xNumberFormatTypes.getStandardFormat(
	          com.sun.star.util.NumberFormat.DATE, aLocale );
	    /******************/
        com.sun.star.beans.XPropertySet xPropSet = (XPropertySet) UnoRuntime.queryInterface(com.sun.star.beans.XPropertySet.class, cell);
        xPropSet.setPropertyValue("NumberFormat", nFormat- (jahrvierstellig ? 1 : 0));
	}
	
	
	
	
	/*******************************************************/
}
class TheraPiDocListener implements IDocumentListener{
	Object document = null;
	public TheraPiDocListener(IDocument document){
		this.document = document;
	}
	@Override
	public void disposing(IEvent arg0) {
		System.out.println("disposing");
		
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
		System.out.println("onInsertDone");
		
	}

	@Override
	public void onInsertStart(IDocumentEvent arg0) {
		System.out.println("onInsertStart");
		
	}

	@Override
	public void onLoad(IDocumentEvent arg0) {
		System.out.println("onLoad");
		
	}

	@Override
	public void onLoadDone(IDocumentEvent arg0) {
		System.out.println("onLoadDone");
		
	}

	@Override
	public void onLoadFinished(IDocumentEvent arg0) {
		System.out.println("onLoadFinished");
		
	}

	@Override
	public void onModifyChanged(IDocumentEvent arg0) {
		System.out.println("onModifyChanged");
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
		// TODO Auto-generated method stub
		
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
		
	}

	@Override
	public void onSaveFinished(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUnload(IDocumentEvent arg0) {
		System.out.println("onUnload");
		
	}
	
}
/**************************************/
