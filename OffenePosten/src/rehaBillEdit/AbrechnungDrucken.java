package rehaBillEdit;



import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JOptionPane;




import offenePosten.OffenePosten;

import org.jdesktop.swingworker.SwingWorker;






import CommonTools.SqlInfo;
import CommonTools.OOTools;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.DocumentException;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextField;
import ag.ion.bion.officelayer.text.ITextFieldService;
import ag.ion.bion.officelayer.text.ITextTable;
import ag.ion.bion.officelayer.text.ITextTableCell;
import ag.ion.bion.officelayer.text.ITextTableCellProperties;
import ag.ion.bion.officelayer.text.TextException;
import ag.ion.noa.NOAException;
import ag.ion.noa.internal.printing.PrintProperties;

import com.sun.star.beans.XPropertySet;
import com.sun.star.text.XTextTableCursor;

@SuppressWarnings("unused")
public class AbrechnungDrucken {
	int aktuellePosition = 0;
	ITextTable textTable = null;
	ITextTable textEndbetrag = null;
	ITextDocument textDocument = null;
	int positionen;
	String rechnungNummer;
	String papierIK;
	DecimalFormat dfx = new DecimalFormat( "0.00" );
	int zugabe = 0;
	BigDecimal rechnungsBetrag = new BigDecimal(Double.valueOf("0.00"));
	BigDecimal rechnungsGesamt = new BigDecimal(Double.valueOf("0.00"));
	BigDecimal rechnungsRezgeb = new BigDecimal(Double.valueOf("0.00"));
	HashMap<String,String> hmAdresse = new HashMap<String,String>();
	boolean mitTage = false;
	//AbrechnungGKV eltern = null;
	int anzahlRezepte = 0;
	public AbrechnungDrucken(/*AbrechnungGKV eltern,*/String url,boolean mittage) throws Exception{
		//this.eltern = eltern;
		
		starteDokument(url);
		this.mitTage = mittage;
	}
	public void setIKundRnr(String papierIk,String rnr,HashMap<String,String> hmap){
		this.papierIK = papierIk;
		this.rechnungNummer = rnr;
		this.hmAdresse = hmap;
		
		try {
			setRechnungsBetrag();
			ersetzePlatzhalter();
			textDocument.getFrame().getXFrame().getContainerWindow().setVisible(true);
		} catch (TextException e) {
			JOptionPane.showMessageDialog(null,"Fehler im Rechnungsdruck, Fehler = TextException" );
			e.printStackTrace();
		}

	}
	/********************/

	public void setDaten(String nameVorname,
			String status,
			String rezNr, 
			Vector<String> posvec,
			Vector<BigDecimal> anzahlvec,
			Vector<BigDecimal> anzahltagevec,
			Vector<BigDecimal> einzelpreis,
			Vector<BigDecimal> gesamtpreis,
			Vector<BigDecimal> zuzahlung,
			boolean mitPauschale) throws Exception{
			BigDecimal netto;
		positionen = posvec.size();
		int anz;
		anzahlRezepte++;
		String dummy;
		BigDecimal gesamtPreise = new BigDecimal(Double.valueOf("0.00"));
		BigDecimal gesamtZuzahlung =new BigDecimal(Double.valueOf("0.00"));
		BigDecimal gesamtNetto = new BigDecimal(Double.valueOf("0.00"));
		
		textTable.addRow(positionen+2 +(mitTage ? 1 : 0));
		ITextTableCell[] tcells = null;

		tcells = textTable.getRow(aktuellePosition+1 ).getCells();
		setPositionenCells(false,tcells);
		textTable.getCell(0,aktuellePosition+1).getTextService().getText().setText(nameVorname);
		textTable.getCell(0,aktuellePosition+2).getTextService().getText().setText(status+" - "+rezNr);
		int i;
		for(i = 0; i < positionen;i++){
			textTable.getCell(1,aktuellePosition+(i+2)).getTextService().getText().setText(posvec.get(i));
			anz = Double.valueOf(anzahlvec.get(i).doubleValue()).intValue();
			textTable.getCell(2,aktuellePosition+(i+2)).getTextService().getText().setText(Integer.toString(anz));
			textTable.getCell(3,aktuellePosition+(i+2)).getTextService().getText().setText(dfx.format(einzelpreis.get(i).doubleValue()));
			textTable.getCell(4,aktuellePosition+(i+2)).getTextService().getText().setText(dfx.format(gesamtpreis.get(i).doubleValue()));
			textTable.getCell(5,aktuellePosition+(i+2)).getTextService().getText().setText(dfx.format(zuzahlung.get(i).doubleValue()));
			netto = gesamtpreis.get(i).subtract(zuzahlung.get(i));
			textTable.getCell(6,aktuellePosition+(i+2)).getTextService().getText().setText(dfx.format(netto.doubleValue()));
			tcells = textTable.getRow(aktuellePosition+(i+2)).getCells();
			setPositionenCells(false,tcells);
			gesamtPreise = gesamtPreise.add(gesamtpreis.get(i));
			gesamtZuzahlung = gesamtZuzahlung.add(zuzahlung.get(i));
		}
		if(mitPauschale){
			gesamtZuzahlung = gesamtZuzahlung.add(BigDecimal.valueOf(Double.valueOf("10.00")));
		}
		
		gesamtNetto = gesamtNetto.add(gesamtPreise.subtract(gesamtZuzahlung));
		rechnungsRezgeb = rechnungsRezgeb.add(gesamtZuzahlung);
		rechnungsGesamt = rechnungsGesamt.add(gesamtPreise);
		rechnungsBetrag = rechnungsBetrag.add(gesamtNetto);
		/****************/
		tcells = textTable.getRow(aktuellePosition+(i+2)).getCells();
		setPositionenCells(true,tcells);
		dummy = dfx.format(gesamtPreise.doubleValue());
		textTable.getCell(4,aktuellePosition+(i+2)).getTextService().getText().setText(dummy);
		dummy = dfx.format(gesamtZuzahlung.doubleValue());
		textTable.getCell(5,aktuellePosition+(i+2)).getTextService().getText().setText(dummy);
		dummy = dfx.format(gesamtNetto.doubleValue());
		textTable.getCell(6,aktuellePosition+(i+2)).getTextService().getText().setText(dummy);
		/****************/
		//Hier muß die Einzeltage-Geschichte abgehandelt werden.
		//textTable.getCell(0,aktuellePosition+(i+2)).getName();
		System.out.println("Mittage = "+mitTage);
		if(mitTage){
			String terms = SqlInfo.holeEinzelFeld("select termine from lza where rez_nr='"+rezNr+"' LIMIT 1");
			Vector<String> tagevec = Tools.RezTools.holeEinzelTermineAusRezept(null, terms);
			dummy = "Behandlungstage: ";
			for(int ii = 0; ii < tagevec.size();ii++){
				if(ii == 0){
					dummy = dummy + tagevec.get(ii);
				}else{
					dummy = dummy + ", " + tagevec.get(ii);
				}
			}
			
			OOTools.setOneCellProperty(textTable.getCell(0,aktuellePosition+(i+2)),false,false,false,0x00,7.f);
			OOTools.setOneCellProperty(textTable.getCell(1,aktuellePosition+(i+2)),false,false,false,0x00,7.f);
			//setOneCellProperty(textTable.getCell(1,aktuellePosition+(i+2)));
			
			XTextTableCursor cursor = OOTools.doMergeCellsInTextTabel(textTable.getXTextTable(), 
					textTable.getCell(0,aktuellePosition+(i+2)).getName().getName().intern(), 
					textTable.getCell(1,aktuellePosition+(i+2)).getName().getName().intern() );
					
					
			textTable.getCell(0,aktuellePosition+(i+2)).getTextService().getText().setText(dummy);

			tcells = textTable.getRow(aktuellePosition+(i+2)+1).getCells();
			setPositionenCells(false,tcells);
			
			
		}
		/****************/

		/****************/		
		aktuellePosition += (positionen+2 +  (mitTage ? 1 : 0)  );
	}
	public void setRechnungsBetrag() throws TextException{
		textEndbetrag.getCell(2,0).getTextService().getText().setText(dfx.format(rechnungsGesamt.doubleValue())+" EUR");
		textEndbetrag.getCell(3,1).getTextService().getText().setText(dfx.format(rechnungsRezgeb.doubleValue())+" EUR");
		textEndbetrag.getCell(4,2).getTextService().getText().setText(dfx.format(rechnungsBetrag.doubleValue())+" EUR");
	}

	private void setPositionenCells(boolean italicAndBold,ITextTableCell[] tcells) throws Exception{
		ITextTableCellProperties props = null;
		for(int i2 = 0;i2<tcells.length;i2++){
			props = tcells[i2].getProperties();
			XPropertySet xprops = props.getXPropertySet();
			xprops.setPropertyValue("TopBorderDistance", 0);
			xprops.setPropertyValue("BottomBorderDistance", 0);
			//xprops.setPropertyValue("LeftBorderDistance", 0);
			//xprops.setPropertyValue("RightBorderDistance", 0);
			tcells[i2].getCharacterProperties().setFontSize(8.f);
			tcells[i2].getCharacterProperties().setFontUnderline(false);
			if(italicAndBold){
				tcells[i2].getCharacterProperties().setFontItalic(true);
				tcells[i2].getCharacterProperties().setFontBold(true);
			}else{
				tcells[i2].getCharacterProperties().setFontItalic(false);
				tcells[i2].getCharacterProperties().setFontBold(false);
			}
		}

	}
	public void druckeRechnung(int anzahl){
		
	}
	public void starteDokument(String url) throws Exception{
		IDocumentService documentService = null;;
		documentService = OffenePosten.officeapplication.getDocumentService();
		IDocumentDescriptor docdescript = new DocumentDescriptor();
        docdescript.setHidden(true);
        docdescript.setAsTemplate(true);
		IDocument document = null;
		document = documentService.loadDocument(url,docdescript);
		/**********************/
		textDocument = (ITextDocument)document;
		OOTools.druckerSetzen(textDocument, OffenePosten.hmAbrechnung.get("hmgkvrechnungdrucker"));
		textTable = textDocument.getTextTableService().getTextTable("Tabelle1");
		textEndbetrag = textDocument.getTextTableService().getTextTable("Tabelle2");
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
				placeholders[i].getTextRange().setText(hmAdresse.get("<gkv1>"));
			}else if(placeholders[i].getDisplayText().toLowerCase().equals("<gkv2>")){
				placeholders[i].getTextRange().setText(hmAdresse.get("<gkv2>"));				
			}else if(placeholders[i].getDisplayText().toLowerCase().equals("<gkv3>")){
				placeholders[i].getTextRange().setText(hmAdresse.get("<gkv3>"));				
			}else if(placeholders[i].getDisplayText().toLowerCase().equals("<gkv4>")){
				placeholders[i].getTextRange().setText(hmAdresse.get("<gkv4>"));				
			}else if(placeholders[i].getDisplayText().toLowerCase().equals("<gkv5>")){
				placeholders[i].getTextRange().setText(hmAdresse.get("<gkv5>"));				
			}else if(placeholders[i].getDisplayText().toLowerCase().equals("<gkv6>")){
				placeholders[i].getTextRange().setText(hmAdresse.get("<gkv6>"));
			}
		}
	}
}
