package lvaEntlassmitteilung;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

public class PDFDummy {
	
	
	
	Vector<Vector<String>> vec = new Vector<Vector<String>>();
	Vector<String> dummyvec = new Vector<String>();	
	//
	float abstandoben;
	float abstandlinks;
	float abstandYzwischen;
	float laengedatum;
	float abstandXzwischen;
	Vector<String> vec2 = new Vector<String>();
	
	public PDFDummy(Vector<String> vec,String[] werte){
	
		abstandoben = Float.parseFloat(werte[0]);
		abstandlinks = Float.parseFloat(werte[1]);
		abstandYzwischen = Float.parseFloat(werte[2]);
		laengedatum = Float.parseFloat(werte[3]);
		abstandXzwischen = Float.parseFloat(werte[4]);
		System.out.println(abstandoben);
		System.out.println(abstandlinks);
		System.out.println(abstandYzwischen);
		System.out.println(laengedatum);
		System.out.println(abstandXzwischen);
		//Methodenaufruf der die PDF Produziert
		this.vec2 = vec;
		doPdfGenerate();
	}

	
	private void doPdfGenerate(){
		int zeilen = 0;
		if(this.vec2.size() <= 5 ){
			zeilen = 1;
		}else{
			zeilen = (int) this.vec2.size() / 5;
			if( (this.vec2.size() % 5) > 0){
				zeilen++;
			}
		}
		System.out.println("Benötigte Zeilen = "+zeilen);
		
 
		int pos = 0;
		Float xpos;
		Float ypos;
		
		Document entlassmitteilung = new Document(PageSize.A4);
		PdfWriter writer;
		PdfContentByte cb = null;
		Font font = new Font(Font.HELVETICA,12);
		BaseFont bf = font.getCalculatedBaseFont(false);
		try {
			writer = PdfWriter.getInstance(entlassmitteilung,
					new FileOutputStream("C:/Entlassunsmitteilung.pdf"));
		    writer.setPdfVersion(PdfWriter.VERSION_1_6);	
			entlassmitteilung.open();
		    cb = writer.getDirectContent();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		for(int i1 = 0; i1 < zeilen; i1++){
			for(int i2 = 0; i2 < 5; i2++){
				ypos = abstandoben +(Float.parseFloat(Integer.toString(i1))*abstandYzwischen);
				xpos = abstandlinks + (( Float.parseFloat(Integer.toString(i2))* (abstandXzwischen+laengedatum) ));
				System.out.println("Wert "+(pos)+" - Pdf-Position X = "+PDFTools.getPdfPositionX(xpos));
				System.out.println("Wert "+(pos)+" - Pdf-Position Y = "+PDFTools.getPdfPositionY(ypos));
				if(pos < this.vec2.size()){
					//in pdf schreiben this.vec2.get(pos);
					PDFTools.setzeText(cb,
							PDFTools.getPdfPositionX(xpos),
							842.0f-PDFTools.getPdfPositionY(ypos),
							1.f,
							bf,
							12,
							this.vec2.get(pos));
				}
				pos++;
			}
		}
		entlassmitteilung.close();
		Process process;
		try {
			
			process = new ProcessBuilder("C:/Programme/Adobe/Reader 9.0/Reader/AcroRd32.exe","","C:/Entlassunsmitteilung.pdf").start();
	       InputStream is = process.getInputStream();
	       InputStreamReader isr = new InputStreamReader(is);
	       BufferedReader br = new BufferedReader(isr);
	       String line;
	       while ((line = br.readLine()) != null) {
	         //System.out.println(line);
	       }
	       is.close();
	       isr.close();
	       br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	


}
