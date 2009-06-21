package kurzAufrufe;

import hauptFenster.Reha;

import java.awt.Color;
import java.util.Arrays;
import java.util.Vector;

import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.container.NoSuchElementException;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.WrappedTargetException;

import oOorgTools.OOTools;

import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.internal.text.TextTableColumn;
import ag.ion.bion.officelayer.text.IParagraph;
import ag.ion.bion.officelayer.text.IParagraphProperties;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextTable;
import ag.ion.bion.officelayer.text.TextException;
import ag.ion.noa.NOAException;

import sqlTools.SqlInfo;
import terminKalender.ParameterLaden;
import terminKalender.datFunk;

public class KurzAufrufe {
	public static void starteFunktion(String funktion){
		if(funktion.equals("Akutliste")){
			try {
				new AkutListe();
			} catch (TextException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
/*********************************************/
class AkutListe{
	Vector vec = null;
	String felder = "akutbeh,n_name,v_name,telefonp,telefong,telefonm,emaila,termine1,termine2,akutdat,akutbis";
	public AkutListe() throws TextException{
		vec = SqlInfo.holeSaetze("pat5", felder, "akutpat='T' order by akutbeh", Arrays.asList(new String[] {}));
		int lang;
		if( (lang = vec.size()) > 0){
			IDocumentService documentService = null;
			try {
				documentService = Reha.officeapplication.getDocumentService();
			} catch (OfficeApplicationException e) {
				e.printStackTrace();
			}
			IDocument document = null;
			try {
				DocumentDescriptor docdecript = new DocumentDescriptor();
				docdecript.setHidden(true);
				document = documentService.constructNewDocument(IDocument.WRITER, docdecript);

				//document = documentService.constructNewDocument(IDocument.WRITER, DocumentDescriptor.DEFAULT);
			} catch (NOAException e) {
				e.printStackTrace();
			}
			
			ITextDocument textDocument = (ITextDocument)document;
			/*
			IParagraph paragraph = 
		  		textDocument.getTextService().getTextContentService().constructNewParagraph();	  	
			*/
			try {
				OOTools.setzePapierFormat(textDocument, 21000, 29700);
			} catch (NoSuchElementException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (WrappedTargetException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (UnknownPropertyException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (PropertyVetoException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalArgumentException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			/*
			 * Saichtext basteln und einsetzen
			 */
			ITextTable textTable = null;
			try {
				textTable = textDocument.getTextTableService().constructTextTable(lang+1, 5);

			} catch (TextException e) {
				e.printStackTrace();
			}				
			try {
				
				textDocument.getTextService().getTextContentService().insertTextContent(textTable);
			} catch (TextException e) {
				e.printStackTrace();
			}
			try {
				textTable.getCell(0,0).getTextService().getText().setText("Behandler");
				textTable.getCell(1,0).getTextService().getText().setText("Patient");
				textTable.getCell(2,0).getTextService().getText().setText("Kontakt");			
				textTable.getCell(3,0).getTextService().getText().setText("von / bis");
				textTable.getCell(4,0).getTextService().getText().setText("mögliche Termine");

			} 
			  catch (TextException exception) {
			  	exception.printStackTrace();
			}
			  String text = "";
			  String test = "";
			for(int i = 0; i < lang;i++){
				  try {
					  /*
					  String felder = "akutbeh," +
					  		"n_name,v_name," +
					  		"telefonp,telefong,telefonm,emaila," +
					  		"termine1,"termine2";
					  		,akutdat,akutbis
					  */		
					  text = (String) ((Vector)vec.get(i)).get(0);
					  textTable.getCell(0,i+1).getTextService().getText().setText(text);

					  text = (String) ((Vector)vec.get(i)).get(1)+",\r"+ (String)((Vector)vec.get(i)).get(2);
					  textTable.getCell(1,i+1).getTextService().getText().setText(text);
					  
					  text = "";
					  test = (String) ((Vector)vec.get(i)).get(3);
					  text = text + (test.trim().equals("") ? "" : "p:"+test);
					  test = (String) ((Vector)vec.get(i)).get(4);
					  text = text + (test.trim().equals("") ? "" : "\r"+"g:"+test);
					  test = (String) ((Vector)vec.get(i)).get(5);
					  text = text + (test.trim().equals("") ? "" : "\r"+"m:"+test);
					  test = (String) ((Vector)vec.get(i)).get(6);
					  text = text + (test.trim().equals("") ? "" : "\r"+"e:"+test);
					  textTable.getCell(2,i+1).getTextService().getText().setText(text);
					  
					  text = "";
					  test = (String) ((Vector)vec.get(i)).get(9);
					  test = (test.trim().equals("") ? "ab: " : "ab:  "+datFunk.sDatInDeutsch(test));
					  text = text + test;
					  test = (String) ((Vector)vec.get(i)).get(10);
					  test = (test.trim().equals("") ? "\rbis: " : "\rbis: "+datFunk.sDatInDeutsch(test));
					  text = text + test;
					  textTable.getCell(3,i+1).getTextService().getText().setText(text);
					  
					  text = "";
					  test = (String) ((Vector)vec.get(i)).get(7);
					  text = text + (test.trim().equals("") ? "" : test);
					  test = (String) ((Vector)vec.get(i)).get(8);
					  text = text + (test.trim().equals("") ? "" : "\r"+test);
					  textTable.getCell(4,i+1).getTextService().getText().setText(text);

					} 
				  catch (TextException exception) {
				  	exception.printStackTrace();
					}
			}
			TextTableColumn[] tbc = (TextTableColumn[]) textTable.getColumns();
			tbc[0].setWidth((short) 1500);
			tbc[1].setWidth((short) 1500);
			tbc[2].setWidth((short) 1700);
			tbc[3].setWidth((short) 1500);			
			//tbc[4].setWidth((short) 7920);
			System.out.println("Es gibt insgesamt "+tbc.length+" Column");

			int cols = textTable.getColumnCount();
			int rows = textTable.getRowCount();
			int rot = Color.RED.getRGB();
			int blau = Color.BLUE.getRGB();
			int magenta = Color.MAGENTA.getRGB();
			for (int i = 0; i < rows; i++) {
				textTable.getCell(0, i).getCharacterProperties().setFontName("Courier New");
				textTable.getCell(0, i).getCharacterProperties().setFontSize(10.f);
				textTable.getCell(0, i).getCharacterProperties().setFontBold((i==0 ? true : false));
				textTable.getCell(0, i).getCharacterProperties().setFontColor(magenta);				
				textTable.getCell(1, i).getCharacterProperties().setFontName("Courier New");
				textTable.getCell(1, i).getCharacterProperties().setFontSize(10.f);
				textTable.getCell(1, i).getCharacterProperties().setFontColor(blau);
				textTable.getCell(1, i).getCharacterProperties().setFontBold((i==0 ? true : false));
				textTable.getCell(2, i).getCharacterProperties().setFontName("Courier New");
				textTable.getCell(2, i).getCharacterProperties().setFontSize(10.f);
				textTable.getCell(2, i).getCharacterProperties().setFontBold((i==0 ? true : false));
				textTable.getCell(3, i).getCharacterProperties().setFontName("Courier New");
				textTable.getCell(3, i).getCharacterProperties().setFontSize(10.f);
				textTable.getCell(3, i).getCharacterProperties().setFontBold((i==0 ? true : false));
				textTable.getCell(4, i).getCharacterProperties().setFontName("Courier New");
				textTable.getCell(4, i).getCharacterProperties().setFontSize(10.f);
				textTable.getCell(4, i).getCharacterProperties().setFontBold((i==0 ? true : false));
				textTable.getCell(4, i).getCharacterProperties().setFontColor(rot);

			}

			document.getFrame().getXFrame().getContainerWindow().setVisible(true);

		}
	}
}

