package oOorgTools;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import hauptFenster.Reha;
import systemEinstellungen.SystemConfig;

import com.sun.star.frame.XController;
import com.sun.star.text.XTextViewCursorSupplier;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.view.XLineCursor;

import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.presentation.IPresentationDocument;
import ag.ion.bion.officelayer.spreadsheet.ISpreadsheetDocument;
import ag.ion.bion.officelayer.text.ITextCursor;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextField;
import ag.ion.bion.officelayer.text.ITextFieldService;
import ag.ion.bion.officelayer.text.IViewCursor;
import ag.ion.bion.officelayer.text.TextException;
import ag.ion.noa.NOAException;

public class OOTools {

	public static void loescheLeerenPlatzhalter(ITextDocument textDocument, ITextField placeholders){
		IViewCursor viewCursor = textDocument.getViewCursorService().getViewCursor();
		viewCursor.goToRange(placeholders.getTextRange(), false);
		/*
		ILineCursor lineCursor = viewCursor.getLineCursor();
		lineCursor.gotoStartOfLine(false);
		lineCursor.gotoEndOfLine(true);
		*/
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
	
	public static void starteStandardFormular(String url){
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
		    /*****************/			
			Set entries = SystemConfig.hmAdrPDaten.entrySet();
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
		    entries = SystemConfig.hmAdrKDaten.entrySet();
		    it = entries.iterator();
		    while (it.hasNext() && (!schonersetzt)) {
		      Map.Entry entry = (Map.Entry) it.next();
		      if(((String)entry.getKey()).toLowerCase().equals(placeholderDisplayText)){
		    	  placeholders[i].getTextRange().setText(((String)entry.getValue()));
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
		    	  placeholders[i].getTextRange().setText(((String)entry.getValue()));
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
		    	  placeholders[i].getTextRange().setText(((String)entry.getValue()));
		    	  schonersetzt = true;
		    	  break;
		      }
		    }
		    if(!schonersetzt){
		    	OOTools.loescheLeerenPlatzhalter(textDocument, placeholders[i]);
		    }
		    /*****************/
		}
		
		
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
	}

	public static void starteLeerenWriter(){
		try {
			IDocumentService documentService = Reha.officeapplication.getDocumentService();
			IDocument document = documentService.constructNewDocument(IDocument.WRITER, DocumentDescriptor.DEFAULT);
			ITextDocument textDocument = (ITextDocument)document;
		} 
		catch (OfficeApplicationException exception) {
			exception.printStackTrace();
		} 
		catch (NOAException exception) {
			exception.printStackTrace();
		}
	}
		

	public static void starteLeerenCalc(){
		try {
			IDocumentService documentService = Reha.officeapplication.getDocumentService();
			IDocument document = documentService.constructNewDocument(IDocument.CALC, DocumentDescriptor.DEFAULT);
			ISpreadsheetDocument spreadsheetDocument = (ISpreadsheetDocument) document;
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
		}
		catch(Throwable throwable) {
			throwable.printStackTrace();
		}
		
		
	}
	
	
}
