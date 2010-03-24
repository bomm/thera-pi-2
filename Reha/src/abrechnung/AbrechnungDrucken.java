package abrechnung;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.Vector;

import com.sun.star.beans.Property;
import com.sun.star.beans.XPropertySet;
import com.sun.star.beans.XPropertySetInfo;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.uno.UnoRuntime;

import hauptFenster.Reha;
import ag.ion.bion.officelayer.beans.IPropertyKey;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.internal.text.TextTableProperties;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextTable;
import ag.ion.bion.officelayer.text.ITextTableCell;
import ag.ion.bion.officelayer.text.ITextTableCellProperties;
import ag.ion.bion.officelayer.text.ITextTableProperties;

public class AbrechnungDrucken {
	int aktuellePosition = 0;
	ITextTable textTable = null;
	ITextDocument textDocument = null;
	int positionen;
	String rechnungNummer;
	String papierIK;
	DecimalFormat dfx = new DecimalFormat( "0.00" );
	int zugabe = 0;
	public AbrechnungDrucken(String url,String papierIk,String rnr) throws Exception{
		this.papierIK = papierIk;
		this.rechnungNummer = rnr;
		starteDokument(url);
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


	}

}
