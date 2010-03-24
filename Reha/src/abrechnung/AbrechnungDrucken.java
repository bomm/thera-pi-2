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
	DecimalFormat dfx = new DecimalFormat( "0.00" );
	public AbrechnungDrucken(String url) throws Exception{
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
		textTable.addRow(positionen);
		ITextTableCell[] tcells = null;
		ITextTableCellProperties props = null;
		for(int i = 0; i < positionen;i++){
			textTable.getCell(1,aktuellePosition+(i+1)).getTextService().getText().setText(posvec.get(i));
			anz = Double.valueOf(anzahlvec.get(i).doubleValue()).intValue();
			textTable.getCell(2,aktuellePosition+(i+1)).getTextService().getText().setText(Integer.toString(anz));
			textTable.getCell(3,aktuellePosition+(i+1)).getTextService().getText().setText(dfx.format(einzelpreis.get(i).doubleValue()));
			textTable.getCell(4,aktuellePosition+(i+1)).getTextService().getText().setText(dfx.format(gesamtpreis.get(i).doubleValue()));
			textTable.getCell(5,aktuellePosition+(i+1)).getTextService().getText().setText(dfx.format(zuzahlung.get(i).doubleValue()));
			netto = gesamtpreis.get(i).subtract(zuzahlung.get(i));
			textTable.getCell(6,aktuellePosition+(i+1)).getTextService().getText().setText(dfx.format(netto.doubleValue()));
			tcells = textTable.getRow(aktuellePosition+(i+1)).getCells();
			for(int i2 = 0;i2<tcells.length;i2++){
				props = tcells[i2].getProperties();
				XPropertySet xprops = props.getXPropertySet();
				xprops.setPropertyValue("TopBorderDistance", 0);
				xprops.setPropertyValue("BottomBorderDistance", 0);
				xprops.setPropertyValue("LeftBorderDistance", 0);
				xprops.setPropertyValue("RightBorderDistance", 0);

				tcells[i2].getCharacterProperties().setFontSize(8.f);
				tcells[i2].getCharacterProperties().setFontBold(false);
				tcells[i2].getCharacterProperties().setFontUnderline(false);
			}
		}
		aktuellePosition += positionen;
		
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
