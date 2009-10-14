package entlassBerichte;

import java.io.FileOutputStream;

import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

import hauptFenster.Reha;
import ag.ion.bion.officelayer.desktop.IFrame;
import events.RehaEventClass;

public class NachsorgePDF {
	public EBerichtPanel eltern = null;
	public String tempPfad = Reha.proghome+"temp/"+Reha.aktIK+"/";
	public String vorlagenPfad = Reha.proghome+"vorlagen/"+Reha.aktIK+"/";
	public String[] rvVorlagen = {null,null,null,null};
	String[][] tempDateien = {null,null,null,null,null};

	
	
	public NachsorgePDF(EBerichtPanel xeltern,boolean nurVorschau,int version){
		eltern = xeltern;
		rvVorlagen[0]  = vorlagenPfad+"Nachsorge1-Variante2.pdf";
		rvVorlagen[1]  = vorlagenPfad+"Nachsorge1-Variante2.pdf";
		rvVorlagen[2]  = vorlagenPfad+"";
		rvVorlagen[3]  = vorlagenPfad+"";
		boolean geklappt = doSeite1(true,"","");
		
	}
	
	private boolean doSeite1(boolean vorschau,String ausfertigung,String bereich){

		String pdfPfad = rvVorlagen[0];
		PdfStamper stamper = null;
		tempDateien[0] = new String[]{tempPfad+"NS1"+System.currentTimeMillis()+".pdf"};
		BaseFont bf = null;
		PdfReader reader = null;
		try {
			bf = BaseFont.createFont(BaseFont.COURIER,BaseFont.CP1252,BaseFont.NOT_EMBEDDED);
			reader = new PdfReader (pdfPfad);
			stamper = new PdfStamper(reader,new  FileOutputStream(tempDateien[0][0]));
			PdfContentByte cb = stamper.getOverContent(1);
			Float [] pos = {null,null,null};
			float fy0 =  0.25f;
			float fy1 =  6.9f;
			// Hier die Positionierung für das obere Gedönse
									//        0RV-Nr.
			Float[][] poswert1 = {getFloats(29.30f,268.25f,fy1),
							//      1Kennzeich                2Name                           3Geburtst
								getFloats(103.30f,268.25f,fy1),getFloats(24.5f,260.0f,fy0),getFloats(24.5f,251.5f,fy1),
								//   4Strasse                             5PLZ                               6Ort
								getFloats(24.5f,243.0f,fy0),getFloats(24.5f,234.5f,fy1), getFloats(51.5f,234.5f,fy0),
								//  7VersichertenName              8MSNR                     9BNR
								getFloats(24.5f,226.0f,fy0),getFloats(131.25f,268.25f,fy1),getFloats(156.25f,268.25f,fy1)
					};
					// Jetzt die Positionen abarbeiten
			String text = "";
			for(int i = 0; i < 10;i++){
				if(i==3){
					text = macheDatum2Sechs(eltern.btf[i].getText().trim());
				}else{
					text = eltern.btf[i].getText();	
				}
				setzeText(cb,poswert1[i][0], poswert1[i][1],poswert1[i][2],bf,12,text);
			}
			//IK des Berichterstellers
			pos = getFloats(131.25f,225.75f,fy1);
			setzeText(cb,pos[0], pos[1],pos[2],bf,12,eltern.btf[28].getText());
			//Abteilung-Nr.
			pos = getFloats(181.50f,225.75f,fy1);
			setzeText(cb,pos[0], pos[1],pos[2],bf,12,(eltern.btf[10].getText().trim().equals("") ? "2300" : eltern.btf[10].getText().trim() ) );

			/*********************************/
			stamper.setFormFlattening(true);
			stamper.close();
			reader.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}

		return true;
	}	
	
	
	/***********ab hier Christian's Funktionen
	 * 
	 */
	private void setzeText(PdfContentByte cb,float x,float y,float space,BaseFont bf,int fsize,String text){
		cb.beginText();
		cb.moveText(x, y);
		cb.setFontAndSize(bf,fsize);
		cb.setCharacterSpacing(space);
		cb.showText(text);
		cb.endText();
	}
	private float rechneX(float fx){
		return (595.0f/210.0f)*fx;
	}
	private float rechneY(float fy){
		return (842.0f/297.0f)*fy;
	}

	private Float[] getFloats(float fx, float fy, float fcSpace){
		Float [] fret = {0.f,0.f,0.f};
		fret[0] = (595.0f/210.0f)*fx;
		fret[1] = (842.0f/297.0f)*fy;
		fret[2] = fcSpace;
		return fret;
	}
	private String macheDatum2Sechs(String datum){
		String sret = "";
		try{
			String[] split = datum.split("\\.");
			sret = split[0]+split[1]+split[2].substring(2);
		}catch(Exception ex){
			sret = "";
		}
		return sret;
	}
	
}
