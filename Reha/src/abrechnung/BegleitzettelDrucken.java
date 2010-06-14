package abrechnung;

import java.util.HashMap;

import javax.swing.JOptionPane;

import oOorgTools.OOTools;

import systemEinstellungen.SystemConfig;

import hauptFenster.Reha;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextField;
import ag.ion.bion.officelayer.text.ITextFieldService;
import ag.ion.bion.officelayer.text.TextException;

public class BegleitzettelDrucken {
	ITextDocument textDocument = null;
	int anzahlRezepte = 0;
	String kostentrIK;
	String kostentrName;
	String rechnungNummer;
	HashMap<String,String> annahmeStelle = null;
	AbrechnungGKV eltern;
	public BegleitzettelDrucken(AbrechnungGKV eltern,int anzahlRezepte,String kostentrIK,String kostentrName, HashMap<String,String> annahme,String rnr,String url) throws Exception{
		this.anzahlRezepte = anzahlRezepte;
		this.annahmeStelle = annahme;
		this.kostentrIK = kostentrIK;
		this.kostentrName = kostentrName;
		this.rechnungNummer = rnr; 
		this.eltern = eltern;

		try{
			starteDokument(url);
		}catch(Exception ex){
			JOptionPane.showMessageDialog(null, "Fehler im Modul starteDokument() Begleitzettl\n"+ex.getMessage());	
		}
		Thread.sleep(100);

		try{
			ersetzePlatzhalter();
		}catch(Exception ex){
			JOptionPane.showMessageDialog(null, "Fehler im Modul ersetzePlatzhalter() Begleitzettl\n"+ex.getMessage());
		}
		
		if(SystemConfig.hmAbrechnung.get("hmallinoffice").equals("1")){
			textDocument.getFrame().getXFrame().getContainerWindow().setVisible(true);
		}else{
			try{
				Thread.sleep(100);
				textDocument.print();
				try{
					while(textDocument.getPrintService().isActivePrinterBusy()){
						Thread.sleep(50);
					}
				}catch(Exception ex){
					JOptionPane.showMessageDialog(null, "Fehler im Modul isActivePrinterBusy() Begleitzettl\n"+ex.getMessage());
				}
			}catch(Exception ex){
				JOptionPane.showMessageDialog(null, "Fehler im Modul print() Begleitzettl\n"+ex.getMessage());					
			}
			Thread.sleep(150);
			try{
				textDocument.close();
			}catch(Exception ex){
				JOptionPane.showMessageDialog(null, "Fehler im Modul close() Begleitzettl\n"+ex.getMessage());
			}
			Thread.sleep(100);
		}
	}
	
	private void starteDokument(String url) throws Exception{
		IDocumentService documentService = null;;

		documentService = Reha.officeapplication.getDocumentService();

		IDocumentDescriptor docdescript = new DocumentDescriptor();
        docdescript.setHidden(true);
        docdescript.setAsTemplate(true);
		IDocument document = null;

		document = documentService.loadDocument(url,docdescript);

		/**********************/
		textDocument = (ITextDocument)document;
		OOTools.druckerSetzen(textDocument, SystemConfig.hmAbrechnung.get("hmgkvrechnungdrucker"));
	}
	
	private void ersetzePlatzhalter(){
		ITextFieldService textFieldService = textDocument.getTextFieldService();
		ITextField[] placeholders = null;
		try {
			placeholders = textFieldService.getPlaceholderFields();
		} catch (TextException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < placeholders.length; i++) {
			if(placeholders[i].getDisplayText().toLowerCase().equals("<gkv1>")){
				placeholders[i].getTextRange().setText(annahmeStelle.get("<gkv1>"));
			}else if(placeholders[i].getDisplayText().toLowerCase().equals("<gkv2>")){
				placeholders[i].getTextRange().setText(annahmeStelle.get("<gkv2>"));				
			}else if(placeholders[i].getDisplayText().toLowerCase().equals("<gkv3>")){
				placeholders[i].getTextRange().setText(annahmeStelle.get("<gkv3>"));				
			}else if(placeholders[i].getDisplayText().toLowerCase().equals("<gkv4>")){
				placeholders[i].getTextRange().setText(annahmeStelle.get("<gkv4>"));				
			}else if(placeholders[i].getDisplayText().toLowerCase().equals("<gkv5>")){
				placeholders[i].getTextRange().setText(this.kostentrIK);				
			}else if(placeholders[i].getDisplayText().toLowerCase().equals("<gkv6>")){
				placeholders[i].getTextRange().setText(this.kostentrName);
			}else if(placeholders[i].getDisplayText().toLowerCase().equals("<gkv7>")){
				placeholders[i].getTextRange().setText(SystemConfig.hmFirmenDaten.get("Firma1")+" "+
						SystemConfig.hmFirmenDaten.get("Firma2")+"\n"+
						SystemConfig.hmFirmenDaten.get("Strasse")+"\n"+
						SystemConfig.hmFirmenDaten.get("Plz")+" "+
						SystemConfig.hmFirmenDaten.get("Ort"));
			}else if(placeholders[i].getDisplayText().toLowerCase().equals("<gkv8>")){
				placeholders[i].getTextRange().setText(Reha.aktIK);
			}else if(placeholders[i].getDisplayText().toLowerCase().equals("<gkv9>")){
				placeholders[i].getTextRange().setText(this.rechnungNummer);
			}else if(placeholders[i].getDisplayText().toLowerCase().equals("<gkv10>")){
				placeholders[i].getTextRange().setText(Integer.toString(this.anzahlRezepte));				
			}else if(placeholders[i].getDisplayText().toLowerCase().equals("<gkv11>")){
				if(this.anzahlRezepte==1){
					placeholders[i].getTextRange().setText(Integer.toString(this.anzahlRezepte)+ " Originalrezept");					
				}else{
					placeholders[i].getTextRange().setText(Integer.toString(this.anzahlRezepte)+ " Originalrezepte");					
				}
			}
		}
	}

}
