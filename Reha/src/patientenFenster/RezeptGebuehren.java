package patientenFenster;

import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextField;
import ag.ion.bion.officelayer.text.ITextFieldService;
import ag.ion.bion.officelayer.text.TextException;
import ag.ion.noa.NOAException;
import hauptFenster.Reha;
import systemEinstellungen.SystemConfig;

public class RezeptGebuehren {
	boolean nurkopie;
	boolean aushistorie;
	public RezeptGebuehren(boolean kopie,boolean historie){
		this.nurkopie = kopie;
		this.aushistorie = historie;
		new Thread(){
			public void run(){
				rezGebDrucken();
			}
		}.start();
	}
	
	
	public void rezGebDrucken(){
		String url = SystemConfig.rezGebVorlageNeu;	
		IDocumentService documentService = null;;
		
		try {
			documentService = Reha.officeapplication.getDocumentService();
		} catch (OfficeApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        IDocumentDescriptor docdescript = new DocumentDescriptor();
        //docdescript.setHidden(true);
        docdescript.setHidden(false);
        docdescript.setAsTemplate(true);
		IDocument document = null;
		try {
			document = documentService.loadDocument(url,docdescript);

		} catch (NOAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/**********************/
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
			String placeholderDisplayText = placeholders[i].getDisplayText();
			System.out.println("Platzhalter-Name = "+placeholderDisplayText);
			placeholders[i].getTextRange().setText(SystemConfig.hmAdrRDaten.get(placeholderDisplayText));
		}
		//document.getFrame().getXFrame().getContainerWindow().setVisible(true);
	}
}
