package abrechnung;

import hauptFenster.Reha;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import oOorgTools.OOTools;

import org.jdesktop.swingworker.SwingWorker;

import sqlTools.SqlInfo;
import systemEinstellungen.SystemConfig;

import ag.ion.bion.officelayer.document.DocumentDescriptor;
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
import ag.ion.noa.internal.printing.PrintProperties;

import com.sun.star.beans.XPropertySet;

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
	AbrechnungGKV eltern = null;
	int anzahlRezepte = 0;
	public AbrechnungDrucken(AbrechnungGKV eltern,String url) throws Exception{
		this.eltern = eltern;
		starteDokument(url);
	}
	public void setIKundRnr(String papierIk,String rnr,HashMap<String,String> hmap){
		this.papierIK = papierIk;
		this.rechnungNummer = rnr;
		this.hmAdresse = hmap;
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				try{
					setRechnungsBetrag();
					ersetzePlatzhalter();
					
					if(SystemConfig.hmAbrechnung.get("hmallinoffice").equals("1")){
						textDocument.getFrame().getXFrame().getContainerWindow().setVisible(true);
					}else{
						int exemplare = Integer.parseInt(SystemConfig.hmAbrechnung.get("hmgkvrexemplare"));
						Thread.sleep(100);
						PrintProperties printprop = new PrintProperties ((short)exemplare,null);
						textDocument.getPrintService().print(printprop);
						Thread.sleep(200);
						textDocument.close();
						

					}
					eltern.abrDruck = null;
					//textDocument.getFrame().getXFrame().getContainerWindow().setVisible(true);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				return null;
			}
		}.execute();
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
		
		textTable.addRow(positionen+2);
		ITextTableCell[] tcells = null;

		tcells = textTable.getRow(aktuellePosition+1).getCells();
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
		aktuellePosition += (positionen+2);
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
		documentService = Reha.officeapplication.getDocumentService();
		IDocumentDescriptor docdescript = new DocumentDescriptor();
        docdescript.setHidden(true);
        docdescript.setAsTemplate(true);
		IDocument document = null;
		document = documentService.loadDocument(url,docdescript);
		/**********************/
		textDocument = (ITextDocument)document;
		OOTools.druckerSetzen(textDocument, SystemConfig.hmAbrechnung.get("hmgkvrechnungdrucker"));
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
				placeholders[i].getTextRange().setText(hmAdresse.get("<gkv6>"));				
			}else if(placeholders[i].getDisplayText().toLowerCase().equals("<gkv6>")){
				placeholders[i].getTextRange().setText(hmAdresse.get("<gkv6>"));
			}
		}
	}
}
