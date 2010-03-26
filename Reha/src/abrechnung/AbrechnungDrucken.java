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

import com.sun.star.beans.XPropertySet;

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
	public AbrechnungDrucken(String url) throws Exception{
		starteDokument(url);
	}
	public void setIKundRnr(String papierIk,String rnr){
		this.papierIK = papierIk;
		this.rechnungNummer = rnr;
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				try{
					holeAdresse(papierIK);
					setRechnungsBetrag();
					ersetzePlatzhaler();
				}catch(Exception ex){
					ex.printStackTrace();
				}
				return null;
			}
		}.execute();
	}
	/********************/
	private void holeAdresse(String papIK){
		String[] hmKeys = {"<gkv1>","<gkv2>","<gkv3>","<gkv4>","<gkv5>","<gkv6>"};
		Vector<Vector<String>> vec = SqlInfo.holeFelder("select * from kass_adr where ik_kasse ='"+papIK+"'");
		if(vec.size()==0){
			for(int i = 0; i < hmKeys.length-1;i++){
				hmAdresse.put(hmKeys[i], "");
			}
			hmAdresse.put(hmKeys[5],rechnungNummer);
			return;
		}
		hmAdresse.put(hmKeys[0],vec.get(0).get(2) );
		hmAdresse.put(hmKeys[1],vec.get(0).get(3) );
		hmAdresse.put(hmKeys[2],vec.get(0).get(4) );
		hmAdresse.put(hmKeys[3],vec.get(0).get(5)+" "+vec.get(0).get(6));
		hmAdresse.put(hmKeys[4],"");
		hmAdresse.put(hmKeys[5],rechnungNummer);
	}
	public void setDaten(String nameVorname,
			String status,
			String rezNr, 
			Vector<String> posvec,
			Vector<BigDecimal> anzahlvec,
			Vector<BigDecimal> einzelpreis,
			Vector<BigDecimal> gesamtpreis,
			Vector<BigDecimal> zuzahlung,
			boolean mitPauschale) throws Exception{
			BigDecimal netto;
		positionen = posvec.size();
		int anz;
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
        //docdescript.setHidden(true);
        docdescript.setAsTemplate(true);
		IDocument document = null;

		document = documentService.loadDocument(url,docdescript);

		/**********************/
		textDocument = (ITextDocument)document;
		textTable = textDocument.getTextTableService().getTextTable("Tabelle1");
		textEndbetrag = textDocument.getTextTableService().getTextTable("Tabelle2");
	}
	@SuppressWarnings("unchecked")
	private void ersetzePlatzhaler(){
		ITextFieldService textFieldService = textDocument.getTextFieldService();
		ITextField[] placeholders = null;
		try {
			placeholders = textFieldService.getPlaceholderFields();
		} catch (TextException e) {
			e.printStackTrace();
		}
		String placeholderDisplayText = "";
		for (int i = 0; i < placeholders.length; i++) {
			boolean schonersetzt = false;
			try{
				placeholderDisplayText = placeholders[i].getDisplayText().toLowerCase();
			}catch(com.sun.star.uno.RuntimeException ex){
				ex.printStackTrace();
			}
		    Set<?> entries = hmAdresse.entrySet();
		    Iterator<?> it = entries.iterator();
		    while (it.hasNext() && (!schonersetzt)) {
		      Map.Entry entry = (Map.Entry) it.next();
		      if(((String)entry.getKey()).toLowerCase().equals(placeholderDisplayText.toLowerCase())){
		    	  if(((String)entry.getValue()).trim().equals("")){
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
		}	
	}

}
