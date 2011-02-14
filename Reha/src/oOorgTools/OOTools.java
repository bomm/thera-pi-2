package oOorgTools;

import hauptFenster.Reha;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.jdesktop.swingworker.SwingWorker;

import systemEinstellungen.SystemConfig;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.desktop.IFrame;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.DocumentException;
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
import ag.ion.bion.officelayer.text.ITextFieldService;
import ag.ion.bion.officelayer.text.ITextRange;
import ag.ion.bion.officelayer.text.IViewCursor;
import ag.ion.bion.officelayer.text.TextException;
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
			
		}
	}
	
	public static void starteStandardFormular(String url,String drucker) {
		IDocumentService documentService = null;
		ITextDocument textDocument = null;
		Reha.thisFrame.setCursor(Reha.thisClass.wartenCursor);
		//System.out.println("Starte Datei -> "+url);
		if(!Reha.officeapplication.isActive()){
			Reha.starteOfficeApplication();
		}
		try{
			documentService = Reha.officeapplication.getDocumentService();

		} catch (OfficeApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IDocumentDescriptor docdescript = new DocumentDescriptor();
		docdescript.setHidden(true);
		docdescript.setAsTemplate(true);
		IDocument document = null;
		//ITextTable[] tbl = null;
		
		try {
			document = documentService.loadDocument(url,docdescript);
		} catch (NOAException e) {

			e.printStackTrace();
		}
		textDocument = (ITextDocument)document;
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
		} catch (TextException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String placeholderDisplayText = "";
		try{
		for (int i = 0; i < placeholders.length; i++) {
			//boolean loeschen = false;
			boolean schonersetzt = false;
			try{
				placeholderDisplayText = placeholders[i].getDisplayText().toLowerCase();
				//System.out.println(placeholderDisplayText);
			}catch(com.sun.star.uno.RuntimeException ex){
				//System.out.println("************catch()*******************");
				ex.printStackTrace();
			}

		    /*****************/			
			Set<?> entries = SystemConfig.hmAdrPDaten.entrySet();
		    Iterator<?> it = entries.iterator();
		    while (it.hasNext()) {
		      Map.Entry<?,?> entry = (Map.Entry<?, ?>) it.next();
		      if(entry.getKey().toString().toLowerCase().equals(placeholderDisplayText)){
		    	  if(entry.getValue().toString().trim().equals("")){
		    		  placeholders[i].getTextRange().setText("\b");
		    		  //OOTools.loescheLeerenPlatzhalter(textDocument, placeholders[i]);
		    	  }else{
			    	  placeholders[i].getTextRange().setText(entry.getValue().toString());		    		  
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
		      Map.Entry<?,?> entry = (Map.Entry<?,?>) it.next();
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
		      Map.Entry<?,?> entry = (Map.Entry<?,?>) it.next();
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
		      Map.Entry<?,?> entry = (Map.Entry<?,?>) it.next();
		      if(((String)entry.getKey()).toLowerCase().equals(placeholderDisplayText)){
		    	  /*
		    	  ////System.out.println("Gefunden ->"+((String)entry.getValue()));
		    	  try{
		    		  
		    	  }catch(com.sun.star.uno.RuntimeException ex){
		    		  //System.out.println("Fehler bei AdrRDaten");
		    	  }
		    	  */
		    	  if(((String)entry.getValue()).trim().equals("")){
		    		  placeholders[i].getTextRange().setText("");
		    		  OOTools.loescheLeerenPlatzhalter(textDocument, placeholders[i]);
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
		}catch(java.lang.IllegalArgumentException ex){
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null,"Fehler in der Dokumentvorlage");
			if(document != null){
				document.close();
				try {
					Reha.officeapplication.deactivate();
				} catch (OfficeApplicationException e) {
					e.printStackTrace();
				}
				Reha.officeapplication.dispose();
				return;
			}
		}
		sucheNachPlatzhalter(textDocument);
		Reha.thisFrame.setCursor(Reha.thisClass.normalCursor);
		IViewCursor viewCursor = textDocument.getViewCursorService().getViewCursor();
		viewCursor.getPageCursor().jumpToFirstPage();


		final ITextDocument xtextDocument = textDocument;
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				xtextDocument.getFrame().getXFrame().getContainerWindow().setVisible(true);
				xtextDocument.getFrame().setFocus();
			}
		});

	}
	/*******************************************************************************************/
	public static void starteBacrodeFormular(String url,String drucker){
		IDocumentService documentService = null;;
		Reha.thisFrame.setCursor(Reha.thisClass.wartenCursor);
		//System.out.println("Starte Datei -> "+url);
		if(!Reha.officeapplication.isActive()){
			Reha.starteOfficeApplication();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			documentService = Reha.officeapplication.getDocumentService();

		} catch (OfficeApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        IDocumentDescriptor docdescript = new DocumentDescriptor();
       	docdescript.setHidden(true);
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
		} catch (TextException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String placeholderDisplayText = "";
		for (int i = 0; i < placeholders.length; i++) {
			//boolean loeschen = false;
			boolean schonersetzt = false;
			try{
				placeholderDisplayText = placeholders[i].getDisplayText().toLowerCase();
				//System.out.println(placeholderDisplayText);
			}catch(com.sun.star.uno.RuntimeException ex){
				//System.out.println("************catch()*******************");
				ex.printStackTrace();
			}
	
		    /*****************/			
			Set<?> entries = SystemConfig.hmAdrPDaten.entrySet();
		    Iterator<?> it = entries.iterator();
		    while (it.hasNext()) {
		      Map.Entry<?,?> entry = (Map.Entry<?,?>) it.next();
		      if(((String)entry.getKey()).toLowerCase().equals(placeholderDisplayText)){
		    	  if(((String)entry.getValue()).trim().equals("")){
		    		  placeholders[i].getTextRange().setText("\b");
		    		  //OOTools.loescheLeerenPlatzhalter(textDocument, placeholders[i]);
		    	  }else{
			    	  placeholders[i].getTextRange().setText(((String)entry.getValue()));		    		  
		    	  }
		    	  schonersetzt = true;
		    	  break;
		      }
		    }
		    /*****************/
		    entries = SystemConfig.hmAdrKDaten.entrySet();
		    it = entries.iterator();
		    while (it.hasNext() && (!schonersetzt)) {
		      Map.Entry<?,?> entry = (Map.Entry<?,?>) it.next();
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
		      Map.Entry<?,?> entry = (Map.Entry<?,?>) it.next();
		      if(((String)entry.getKey()).toLowerCase().equals(placeholderDisplayText)){
		    	  if(((String)entry.getValue()).trim().equals("")){
		    		  //placeholders[i].getTextRange().setText("\b");
		    		  OOTools.loescheLeerenPlatzhalter(textDocument, placeholders[i]);
		    	  }else{
			    	  placeholders[i].getTextRange().setText(((String)entry.getValue()));		    		  
		    	  }
		    	  schonersetzt = true;
		    	  break;
		      }
		    }
		    /*****************/
		    entries = SystemConfig.hmAdrRDaten.entrySet();
		    it = entries.iterator();
		    while (it.hasNext() && (!schonersetzt)) {
		      Map.Entry<?,?> entry = (Map.Entry<?,?>) it.next();
		      if(((String)entry.getKey()).toLowerCase().equals(placeholderDisplayText)){
		    	  /*
		    	  ////System.out.println("Gefunden ->"+((String)entry.getValue()));
		    	  try{
		    		  
		    	  }catch(com.sun.star.uno.RuntimeException ex){
		    		  //System.out.println("Fehler bei AdrRDaten");
		    	  }
		    	  */
		    	  if(((String)entry.getValue()).trim().equals("")){
		    		  placeholders[i].getTextRange().setText("");
		    		  OOTools.loescheLeerenPlatzhalter(textDocument, placeholders[i]);
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
		sucheNachPlatzhalter(textDocument);
		Reha.thisFrame.setCursor(Reha.thisClass.cdefault);
		IViewCursor viewCursor = textDocument.getViewCursorService().getViewCursor();
		viewCursor.getPageCursor().jumpToFirstPage();
		final ITextDocument xtextDocument = textDocument;
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				if(!SystemConfig.oTerminListe.DirektDruck){
					xtextDocument.getFrame().getXFrame().getContainerWindow().setVisible(true);
					xtextDocument.getFrame().setFocus();
				}else{
					try {
						xtextDocument.print();
						xtextDocument.close();
					} catch (DocumentException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
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

	public static void starteTaxierung(String url,HashMap<String,String> taxWerte) throws Exception, OfficeApplicationException, NOAException, TextException, DocumentException{
		//String url = Reha.proghome+"vorlagen/"+Reha.aktIK+"/TaxierungA5.ott";
		//String drucker = "";
		IDocumentService documentService = null;
		Reha.thisFrame.setCursor(Reha.thisClass.wartenCursor);
		////System.out.println("Starte Datei -> "+url);
		if(!Reha.officeapplication.isActive()){
			Reha.starteOfficeApplication();
		}

		documentService = Reha.officeapplication.getDocumentService();

        IDocumentDescriptor docdescript = new DocumentDescriptor();
       	docdescript.setHidden(true);
        docdescript.setAsTemplate(true);
		IDocument document = null;

		document = documentService.loadDocument(url,docdescript);
		ITextDocument textDocument = (ITextDocument)document;
		/**********************/
		OOTools.druckerSetzen(textDocument, SystemConfig.hmAbrechnung.get("hmgkvtaxierdrucker"));
		/**********************/
		ITextFieldService textFieldService = textDocument.getTextFieldService();
		ITextField[] placeholders = null;

		placeholders = textFieldService.getPlaceholderFields();
		String placeholderDisplayText = "";

		for (int i = 0; i < placeholders.length; i++) {
			placeholderDisplayText = placeholders[i].getDisplayText().toLowerCase();
			Set<?> entries = taxWerte.entrySet();
		    Iterator<?> it = entries.iterator();
			    while (it.hasNext()) {
			      Map.Entry<?,?> entry = (Map.Entry<?,?>) it.next();
			      if(((String)entry.getKey()).toLowerCase().equals(placeholderDisplayText)){
			    	  try{
			    		  
			    	  }catch(com.sun.star.uno.RuntimeException ex){
			    		  //System.out.println("Fehler bei "+placeholderDisplayText);
			    	  }
			    	  placeholders[i].getTextRange().setText(((String)entry.getValue()));		    		  

			    	  break;
			      }
			    }
		}
		textDocument.print();
		textDocument.close();
		if(SystemConfig.hmAbrechnung.get("hmallinoffice").equals("1")){
			textDocument.getFrame().getXFrame().getContainerWindow().setVisible(true);
		}else{
			try{
				Thread.sleep(100);
				textDocument.print();
				Thread.sleep(100);
			}catch (InterruptedException e) {
				e.printStackTrace();
			}
			textDocument.close();
		}	
	}
	/*******************************************************************************************/
	/*******************************************************************************************/

	public static void starteTherapieBericht(String url){
		IDocumentService documentService = null;;
		////System.out.println("Starte Datei -> "+url);
		if(!Reha.officeapplication.isActive()){
			Reha.starteOfficeApplication();
		}
		try {
			documentService = Reha.officeapplication.getDocumentService();
		} catch (OfficeApplicationException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Fehler im OpenOffice-System - Therapiebericht kann nicht erstellt werden");
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
			//boolean loeschen = false;
			//boolean schonersetzt = false;
			String placeholderDisplayText = placeholders[i].getDisplayText().toLowerCase();

			////System.out.println(placeholderDisplayText);	
			////System.out.println("Oiginal-Placeholder-Text = "+placeholders[i].getDisplayText());
		    /*****************/
			
			Set<?> entries = SystemConfig.hmAdrBDaten.entrySet();
		    Iterator<?> it = entries.iterator();
		    while (it.hasNext()) {
		      Map.Entry<?,?> entry = (Map.Entry<?,?>) it.next();
		      String key = entry.getKey().toString().toLowerCase();
		      if( (key.contains("<bblock") || key.contains("<btitel")) && key.equals(placeholderDisplayText) ){
		    	  ////System.out.println("enthält block oder titel");
		    	  int bblock;
		    	  if(key.contains("<bblock")){
			    	  ////System.out.println("enthält block");
		    		  bblock = Integer.valueOf(key.substring((key.length()-2),(key.length()-1)) );
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
			    	  ////System.out.println("enthält titel");
		    		  bblock = Integer.valueOf(key.substring((key.length()-2),(key.length()-1)) );
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
			    	  if((key.contains("<blang")) && ((String)entry.getValue()).trim().equals("") ){
			    		  OOTools.loescheLeerenPlatzhalter(textDocument, placeholders[i]);
			    	  }else{
				    	  placeholders[i].getTextRange().setText(((String)entry.getValue()));			    		  
			    	  }
			    	  break;
			      }
		      }else{
		    	  ////System.out.println("in keinem von Beidem "+entry.getKey()+" - "+key+" - "+placeholderDisplayText);
		      }
		    }
		}
		textDocument.getFrame().setFocus();
	}

	public static ITextDocument starteLeerenWriter(){
		ITextDocument textDocument = null;
		try {
			if(!Reha.officeapplication.isActive()){
				Reha.starteOfficeApplication();
			}
			IDocumentService documentService = Reha.officeapplication.getDocumentService();
			IDocument document = documentService.constructNewDocument(IDocument.WRITER,DocumentDescriptor.DEFAULT );
			textDocument = (ITextDocument)document;
			OOTools.inDenVordergrund(textDocument);
			////System.out.println("In den Vordergrund");
				textDocument.getFrame().setFocus();	
			
		} 
		catch (OfficeApplicationException exception) {
			exception.printStackTrace();
		} 
		catch (NOAException exception) {
			exception.printStackTrace();
		}
		return textDocument;
	}
		
	public ITextDocument starteWriterMitDatei(String url){
		try {
			if(!Reha.officeapplication.isActive()){
				Reha.starteOfficeApplication();
			}
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
	public static ITextDocument starteWriterMitStream(InputStream is, String titel){
		try {
			if(!Reha.officeapplication.isActive()){
				Reha.starteOfficeApplication();
			}
			DocumentDescriptor d = new DocumentDescriptor();
        	d.setTitle(titel);
        	d.setFilterDefinition(RTFFilter.FILTER.getFilterDefinition(IDocument.WRITER));
			IDocumentService documentService = Reha.officeapplication.getDocumentService();
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
			if(!Reha.officeapplication.isActive()){
				Reha.starteOfficeApplication();
			}
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
	public static ITextDocument  starteGKVBericht(String url,String drucker){
		Reha.thisFrame.setCursor(Reha.thisClass.wartenCursor);
		IDocumentService documentService = null;;
		//System.out.println("Starte Datei -> "+url);
		try {
			documentService = Reha.officeapplication.getDocumentService();
		} catch (OfficeApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        IDocumentDescriptor docdescript = new DocumentDescriptor();
       	docdescript.setHidden(true);
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
				//System.out.println(placeholders[y].getDisplayText());
			}
			//System.out.println("************feddisch mit den Placeholders********************");
			*/
		} catch (TextException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String placeholderDisplayText = "";
		for (int i = 0; i < placeholders.length; i++) {
			//boolean loeschen = false;
			boolean schonersetzt = false;
			try{
				placeholderDisplayText = placeholders[i].getDisplayText().toLowerCase();
				////System.out.println(placeholderDisplayText);
			}catch(com.sun.star.uno.RuntimeException ex){
				//System.out.println("************catch()*******************");
				ex.printStackTrace();
			}
	
		    /*****************/			
			Set<?> entries = SystemConfig.hmAdrPDaten.entrySet();
		    Iterator<?> it = entries.iterator();
		    while (it.hasNext()) {
		      Map.Entry<?,?> entry = (Map.Entry<?,?>) it.next();
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
		      Map.Entry<?,?> entry = (Map.Entry<?,?>) it.next();
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
		      Map.Entry<?,?> entry = (Map.Entry<?,?>) it.next();
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
		    
		    entries = SystemConfig.hmEBerichtDaten.entrySet();
		    it = entries.iterator();
		    while (it.hasNext() && (!schonersetzt)) {
		      Map.Entry<?,?> entry = (Map.Entry<?,?>) it.next();
		      if(((String)entry.getKey()).toLowerCase().equals(placeholderDisplayText)){
		    	  if(((String)entry.getValue()).trim().equals("")){
		    		  OOTools.loescheLeerenPlatzhalter(textDocument, placeholders[i]);
		    	  }else{
			    	  placeholders[i].getTextRange().setText(((String)entry.getValue()));		    		  
		    	  }
		    	  schonersetzt = true;
		    	  break;
		      }
		    }
		    
		    
		    if( (!schonersetzt) && (!placeholders[i].getDisplayText().equalsIgnoreCase("<bblock1>"))){
		    	OOTools.loescheLeerenPlatzhalter(textDocument, placeholders[i]);
		    }
		    /*****************/
		}
		
		return textDocument;	
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
				Reha.thisFrame.setCursor(Reha.thisClass.wartenCursor);
				try {
					if(!Reha.officeapplication.isActive()){
						Reha.starteOfficeApplication();
					}
					
					IDocumentService documentService = Reha.officeapplication.getDocumentService();
					document = documentService.constructNewDocument(IDocument.WRITER, docdescript);
					textDocument = (ITextDocument)document;
					textDocument.close();
					System.err.println("Initiales Dokument wurde produziert und wieder geschlossen");
					Reha.thisClass.messageLabel.setForeground(Color.BLACK);
					Reha.thisClass.messageLabel.setText("OpenOffice.org: Init o.k.");
				} 
				catch (OfficeApplicationException exception) {
					Reha.thisClass.messageLabel.setText("OO.org: nicht Verfügbar");
					exception.printStackTrace();
				} 
				catch (NOAException exception) {
					Reha.thisClass.messageLabel.setText("OO.org: nicht Verfügbar");
					exception.printStackTrace();
				}
				Reha.thisFrame.setCursor(Reha.thisClass.normalCursor);
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
	
	
	/*******************************************************/
	public static void holeClipBoard() {

		//System.out.println("Connected to a running office ...");
        

		try {
			XComponentContext xComponentContext;

			xComponentContext = (XComponentContext) com.sun.star.comp.helper.Bootstrap.bootstrap();
			XMultiComponentFactory xMultiComponentFactory;
			xMultiComponentFactory = (XMultiComponentFactory) Reha.officeapplication.getDocumentService();
		
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

		//System.out.println("");
		try{
			if (aUniFlv != null){
                //System.out.println("Unicode text on the clipboard...");
                @SuppressWarnings("unused")
				Object aData = xTransferable.getTransferData(aUniFlv);      

                //System.out.println(AnyConverter.toString(aData));
			}
		}catch(UnsupportedFlavorException ex){
			System.err.println( "Requested format is not available" );
		}
		
		} catch (OfficeApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BootstrapException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
}
