package oOorgTools;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import hauptFenster.Reha;
import systemEinstellungen.SystemConfig;

import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.NoSuchElementException;
import com.sun.star.container.XNameContainer;
import com.sun.star.frame.XController;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.sheet.XSpreadsheetDocument;
import com.sun.star.style.XStyle;
import com.sun.star.style.XStyleFamiliesSupplier;
import com.sun.star.text.XTextDocument;
import com.sun.star.text.XTextViewCursor;
import com.sun.star.text.XTextViewCursorSupplier;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.view.XLineCursor;
import com.sun.xml.internal.bind.v2.runtime.property.Property;


import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.presentation.IPresentationDocument;
import ag.ion.bion.officelayer.spreadsheet.ISpreadsheetDocument;
import ag.ion.bion.officelayer.text.IText;
import ag.ion.bion.officelayer.text.ITextCursor;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextField;
import ag.ion.bion.officelayer.text.ITextFieldService;
import ag.ion.bion.officelayer.text.ITextService;
import ag.ion.bion.officelayer.text.IViewCursor;
import ag.ion.bion.officelayer.text.IViewCursorService;
import ag.ion.bion.officelayer.text.TextException;
import ag.ion.noa.NOAException;
import ag.ion.noa.printing.IPrinter;

public class OOTools {
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
	
	public static void starteStandardFormular(String url,String drucker){
		IDocumentService documentService = null;;
		System.out.println("Starte Datei -> "+url);
		try {
			documentService = Reha.officeapplication.getDocumentService();
		} catch (OfficeApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		ITextDocument textDocument = (ITextDocument)document;
		/**********************/
		if(drucker != null){
			String druckerName = null;
			try {
				druckerName = textDocument.getPrintService().getActivePrinter().getName();
			} catch (NOAException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//Wenn nicht gleich wie in der INI angegeben -> Drucker wechseln
			IPrinter iprint = null;
			if(! druckerName.equals(drucker)){
				try {
					iprint = (IPrinter) textDocument.getPrintService().createPrinter(drucker);
				} catch (NOAException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					textDocument.getPrintService().setActivePrinter(iprint);
				} catch (NOAException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		/**********************/
		ITextFieldService textFieldService = textDocument.getTextFieldService();
		ITextField[] placeholders = null;
		try {
			placeholders = textFieldService.getPlaceholderFields();
			/*
			for(int y = 0 ; y < placeholders.length;y++){
				System.out.println(placeholders[y].getDisplayText());
			}
			System.out.println("************feddisch mit den Placeholders********************");
			*/
		} catch (TextException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String placeholderDisplayText = "";
		for (int i = 0; i < placeholders.length; i++) {
			boolean loeschen = false;
			boolean schonersetzt = false;
			try{
				placeholderDisplayText = placeholders[i].getDisplayText().toLowerCase();
				System.out.println(placeholderDisplayText);
			}catch(com.sun.star.uno.RuntimeException ex){
				System.out.println("************catch()*******************");
				ex.printStackTrace();
			}
	
		    /*****************/			
			Set entries = SystemConfig.hmAdrPDaten.entrySet();
		    Iterator it = entries.iterator();
		    while (it.hasNext()) {
		      Map.Entry entry = (Map.Entry) it.next();
		      if(((String)entry.getKey()).toLowerCase().equals(placeholderDisplayText)){
		    	  if(((String)entry.getValue()).trim().equals("")){
		    		  placeholders[i].getTextRange().setText("\b");
		    		  //OOTools.loescheLeerenPlatzhalter(textDocument, placeholders[i]);
		    	  }else{
			    	  placeholders[i].getTextRange().setText(((String)entry.getValue()));		    		  
		    	  }
		    	  //placeholders[i].getTextRange().setText(((String)entry.getValue()));
		    	  schonersetzt = true;
		    	  break;
		      }
		    }
		    /*****************/
		    entries = SystemConfig.hmAdrKDaten.entrySet();
		    it = entries.iterator();
		    while (it.hasNext() && (!schonersetzt)) {
		      Map.Entry entry = (Map.Entry) it.next();
		      if(((String)entry.getKey()).toLowerCase().equals(placeholderDisplayText)){
		    	  if(((String)entry.getValue()).trim().equals("")){
		    		  OOTools.loescheLeerenPlatzhalter(textDocument, placeholders[i]);
		    	  }else{
			    	  placeholders[i].getTextRange().setText(((String)entry.getValue()));		    		  
		    	  }
		    	  //placeholders[i].getTextRange().setText(((String)entry.getValue()));
		    	  schonersetzt = true;
		    	  break;
		      }
		    }
		    /*****************/
		    entries = SystemConfig.hmAdrADaten.entrySet();
		    it = entries.iterator();
		    while (it.hasNext() && (!schonersetzt)) {
		      Map.Entry entry = (Map.Entry) it.next();
		      if(((String)entry.getKey()).toLowerCase().equals(placeholderDisplayText)){
		    	  if(((String)entry.getValue()).trim().equals("")){
		    		  //placeholders[i].getTextRange().setText("\b");
		    		  OOTools.loescheLeerenPlatzhalter(textDocument, placeholders[i]);
		    	  }else{
			    	  placeholders[i].getTextRange().setText(((String)entry.getValue()));		    		  
		    	  }
		    	  //placeholders[i].getTextRange().setText(((String)entry.getValue()));
		    	  schonersetzt = true;
		    	  break;
		      }
		    }
		    /*****************/
		    entries = SystemConfig.hmAdrRDaten.entrySet();
		    it = entries.iterator();
		    while (it.hasNext() && (!schonersetzt)) {
		      Map.Entry entry = (Map.Entry) it.next();
		      if(((String)entry.getKey()).toLowerCase().equals(placeholderDisplayText)){
		    	  //System.out.println("Gefunden ->"+((String)entry.getValue()));
		    	  try{
		    		  
		    	  }catch(com.sun.star.uno.RuntimeException ex){
		    		  System.out.println("Fehler bei AdrRDaten");
		    	  }
		    	  if(((String)entry.getValue()).trim().equals("")){
		    		  placeholders[i].getTextRange().setText("");
		    		  //OOTools.loescheLeerenPlatzhalter(textDocument, placeholders[i]);
		    	  }else{
			    	  placeholders[i].getTextRange().setText(((String)entry.getValue()));		    		  
		    	  }
		    	  schonersetzt = true;
		    	  break;
		      }
		    }
		    if(!schonersetzt){
		    	OOTools.loescheLeerenPlatzhalter(textDocument, placeholders[i]);
		    }
		    /*****************/
		}
		textDocument.getFrame().setFocus();
		//textDocument.getFrame().getXFrame().getContainerWindow().
		//textDocument.getFrame().getXFrame().getContainerWindow().setVisible(true);
	}
	public static void starteTherapieBericht(String url){
		IDocumentService documentService = null;;
		//System.out.println("Starte Datei -> "+url);
		try {
			documentService = Reha.officeapplication.getDocumentService();
		} catch (OfficeApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		ITextDocument textDocument = (ITextDocument)document;
		ITextFieldService textFieldService = textDocument.getTextFieldService();
		ITextField[] placeholders = null;
		try {
			placeholders = textFieldService.getPlaceholderFields();
		} catch (TextException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < placeholders.length; i++) {
			boolean loeschen = false;
			boolean schonersetzt = false;
			String placeholderDisplayText = placeholders[i].getDisplayText().toLowerCase();

			//System.out.println(placeholderDisplayText);	
			//System.out.println("Oiginal-Placeholder-Text = "+placeholders[i].getDisplayText());
		    /*****************/
			
			Set entries = SystemConfig.hmAdrBDaten.entrySet();
		    Iterator it = entries.iterator();
		    while (it.hasNext()) {
		      Map.Entry entry = (Map.Entry) it.next();
		      String key = entry.getKey().toString().toLowerCase();
		      if( (key.contains("<bblock") || key.contains("<btitel")) && key.equals(placeholderDisplayText) ){
		    	  //System.out.println("enthält block oder titel");
		    	  int bblock;
		    	  if(key.contains("<bblock")){
			    	  //System.out.println("enthält block");
		    		  bblock = new Integer(key.substring((key.length()-2),(key.length()-1)) );
		    		  if(("<bblock"+bblock+">").equals(placeholderDisplayText)){
		    			  if(((String)entry.getValue()).trim().equals("")){
		    				  OOTools.loescheLeerenPlatzhalter(textDocument, placeholders[i]);
		    				  break;
		    			  }else{
		    				  placeholders[i].getTextRange().setText(((String)entry.getValue()));
		    				  break;
		    			  }
		    		  }
		    	  }
		    	  if(key.contains("<btitel")){
			    	  //System.out.println("enthält titel");
		    		  bblock = new Integer(key.substring((key.length()-2),(key.length()-1)) );
		    		  if(("<btitel"+bblock+">").equals(placeholderDisplayText)){
		    			  if(((String)entry.getValue()).trim().equals("")){
		    				  OOTools.loescheLeerenPlatzhalter(textDocument, placeholders[i]);
		    				  break;
		    			  }else{
		    				  placeholders[i].getTextRange().setText(((String)entry.getValue()));
		    				  break;
		    			  }
		    		  }
		    	  }
		    	  
		      }else if( (!(key.contains("<bblock") || key.contains("<btitel"))) && key.equals(placeholderDisplayText)  ){
			      if(((String)entry.getKey()).toLowerCase().equals(placeholderDisplayText)){
			    	  placeholders[i].getTextRange().setText(((String)entry.getValue()));
			    	  break;
			      }
		      }else{
		    	  //System.out.println("in keinem von Beidem "+entry.getKey()+" - "+key+" - "+placeholderDisplayText);
		      }
		    }
		}
		textDocument.getFrame().setFocus();
	}

	public static void starteLeerenWriter(){
		try {
			IDocumentService documentService = Reha.officeapplication.getDocumentService();
			IDocument document = documentService.constructNewDocument(IDocument.WRITER, DocumentDescriptor.DEFAULT);
			ITextDocument textDocument = (ITextDocument)document;
			textDocument.getFrame().setFocus();
		} 
		catch (OfficeApplicationException exception) {
			exception.printStackTrace();
		} 
		catch (NOAException exception) {
			exception.printStackTrace();
		}
	}
		
	public ITextDocument starteWriterMitDatei(String url){
		try {
			IDocumentService documentService = Reha.officeapplication.getDocumentService();
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
	public ISpreadsheetDocument starteCalcMitDatei(String url){
		try {
			IDocumentService documentService = Reha.officeapplication.getDocumentService();
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
			IDocumentService documentService = Reha.officeapplication.getDocumentService();
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
			IDocumentService documentService = Reha.officeapplication.getDocumentService();
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
		System.out.println(props[i] .Name + " = "
		+ xStyleProps.getPropertyValue(props[i].Name));
		}
		//z.B. für A5
		 * 
		 */
		xStyleProps.setPropertyValue("Height", new Integer(hoch));
		xStyleProps.setPropertyValue("Width", new Integer(breit));
	}
	
	
}
