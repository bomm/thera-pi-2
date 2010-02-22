package utils;



import java.awt.Cursor;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import nebraska.Nebraska;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.text.ITextCursor;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextField;
import ag.ion.bion.officelayer.text.ITextFieldService;
import ag.ion.bion.officelayer.text.IViewCursor;
import ag.ion.noa.printing.IPrinter;

import com.sun.star.frame.XController;
import com.sun.star.text.XTextViewCursorSupplier;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.view.XLineCursor;

public class OOorgTools {
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
	public static void starteStandardFormular(String url,String drucker) throws Exception{
		IDocumentService documentService = null;;
		Nebraska.jf.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		System.out.println("Starte Datei -> "+url);

		documentService = Nebraska.officeapplication.getDocumentService();
        IDocumentDescriptor docdescript = new DocumentDescriptor();
       	docdescript.setHidden(true);
        docdescript.setAsTemplate(true);
		IDocument document = null;
		document = documentService.loadDocument(url,docdescript);
		ITextDocument textDocument = (ITextDocument)document;
		/**********************/
		if(drucker != null){
			String druckerName = null;
			druckerName = textDocument.getPrintService().getActivePrinter().getName();
			IPrinter iprint = null;
			if(! druckerName.equals(drucker)){
				iprint = (IPrinter) textDocument.getPrintService().createPrinter(drucker);
				textDocument.getPrintService().setActivePrinter(iprint);
			}
		}
		/**********************/
		ITextFieldService textFieldService = textDocument.getTextFieldService();
		ITextField[] placeholders = null;

		placeholders = textFieldService.getPlaceholderFields();
		String placeholderDisplayText = "";
		for (int i = 0; i < placeholders.length; i++) {
			boolean schonersetzt = false;
			placeholderDisplayText = placeholders[i].getDisplayText().toLowerCase();
			System.out.println(placeholderDisplayText);
	
		    /*****************/			
			Set<Map.Entry<String,String>> entries = Nebraska.hmZertifikat.entrySet();
		    Iterator<Entry<String, String>> it = entries.iterator();
		    while (it.hasNext()) {
		      Map.Entry<String,String> entry = it.next();
		      if(entry.getKey().toLowerCase().equals(placeholderDisplayText)){
		    	  if(entry.getValue().trim().equals("")){
		    		  placeholders[i].getTextRange().setText("\b");
		    	  }else{
			    	  placeholders[i].getTextRange().setText(((String)entry.getValue()));		    		  
		    	  }
		    	  schonersetzt = true;
		    	  break;
		      }
		    }
		    /*****************/
		    if(!schonersetzt){
		    	loescheLeerenPlatzhalter(textDocument, placeholders[i]);
		    }
		    /*****************/
		}
		Nebraska.jf.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		IViewCursor viewCursor = textDocument.getViewCursorService().getViewCursor();
		viewCursor.getPageCursor().jumpToFirstPage();
		textDocument.getFrame().getXFrame().getContainerWindow().setVisible(true);
		textDocument.getFrame().setFocus();
	}

}
