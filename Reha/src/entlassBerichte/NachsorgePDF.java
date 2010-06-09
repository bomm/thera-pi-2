package entlassBerichte;

import hauptFenster.Reha;

import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jdesktop.swingworker.SwingWorker;



import systemEinstellungen.SystemConfig;
import systemTools.ReaderStart;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

public class NachsorgePDF {
	public EBerichtPanel eltern = null;
	public String tempPfad = Reha.proghome+"temp/"+Reha.aktIK+"/";
	public String vorlagenPfad = Reha.proghome+"vorlagen/"+Reha.aktIK+"/";
	public String[] rvVorlagen = {null,null,null,null};
	String[][] tempDateien = {null,null,null,null,null};

	
	
	public NachsorgePDF(EBerichtPanel xeltern,boolean nurVorschau,int[] version){
		eltern = xeltern;
		rvVorlagen[0]  = vorlagenPfad+"Nachsorge1-Variante2.pdf";
		rvVorlagen[1]  = vorlagenPfad+"Nachsorge2-Variante2.pdf";
		rvVorlagen[2]  = vorlagenPfad+"";
		rvVorlagen[3]  = vorlagenPfad+"";
		@SuppressWarnings("unused")
		boolean geklappt = doSeite1(true,"","");
		geklappt = doSeite2(true,"","");
		try {
			geklappt = doSeitenZusammenstellen();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final String xdatei =  tempDateien[2][0];
		if(nurVorschau){
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					new ReaderStart(xdatei);
					return null;
				}	
			}.execute();
		}else{
			doNachsorgeDrucken(version);
		}
		
	}
	private boolean doSeite2(boolean vorschau,String ausfertigung,String bereich){
		@SuppressWarnings("unused")
		String pdfPfad = rvVorlagen[1];
		//PdfStamper stamper = null;
		tempDateien[1] = new String[]{tempPfad+"NS2"+System.currentTimeMillis()+".pdf"};
		BaseFont bf = null;
		//PdfReader reader = null;
		try {
			bf = BaseFont.createFont(BaseFont.COURIER_BOLD,BaseFont.CP1250,BaseFont.EMBEDDED/*NOT_EMBEDDED*/);
			//String text = "";
			Float [] pos = {null,null,null};
			float fy0 =  0.25f;
			float fy1 =  6.2f;
			//Float[] xseite = getFloats(188.25f,278.5f,fy0);
			
			Document docktl = new Document(PageSize.A4);
			PdfCopy cop = new PdfCopy(docktl,new FileOutputStream(tempDateien[1][0]));
			docktl.open();
			int zugabe = 0;
			for(int i = 0;i < 1;i++){
				ByteArrayOutputStream baos  = new ByteArrayOutputStream();
				PdfReader readeroriginal = new PdfReader(rvVorlagen[1]);
				PdfStamper stamper2 = new PdfStamper(readeroriginal,baos);
				PdfContentByte cb = stamper2.getOverContent(1);


				
				
				pos = getFloats(28.50f,265.75f,fy0);
				setzeText(cb,pos[0], pos[1],pos[2],bf,12,eltern.btf[2].getText());
				pos = getFloats(166.75f,265.75f,fy1);
				setzeText(cb,pos[0], pos[1],pos[2],bf,12,macheDatum2Sechs(eltern.btf[3].getText()));
				
				//                    0x-start,  1y-start,  2h�he  3y-ende  4xCode   5xDauer   6xAnzahl
				Float[] startwerte = {33.0f ,    249.5f     ,8.00f, 149.f,  153.25f,  177.00f  ,186.75f};
				for(int y=0;y < 10;y++){
					if(eltern.ktlcmb[y+zugabe].getSelectedIndex() > 0){
						cb.setCharacterSpacing(0.25f);
						schreibeKTLText(y,y+zugabe,startwerte,cb);
						schreibeKTLCode(y,y+zugabe,startwerte,cb);
					}else{
						break;
					}
				}
				cb.setCharacterSpacing(0.25f);	
				
				ColumnText ct = new ColumnText(cb);
				ct.setSimpleColumn(rechneX(28.5f), rechneY(112.f),rechneX(190.0f),rechneY(162.f),8.25f,Element.ALIGN_BOTTOM);
				Phrase ph = new Phrase();
				ph.setFont(FontFactory.getFont("Courier",9,Font.PLAIN));

				ph.add(eltern.bta[7].getText().trim());
				ct.addText(ph);
				ct.go();
				//Die CheckBoxen
				/*
				Float[][] poswert1 = {getFloats(28.45f,90.35f,fy1),getFloats(69.25f,90.35f,fy1),getFloats(124.05f,90.35f,fy1),getFloats(159.8f,90.35f,fy1),
									getFloats(28.45f,82.25f,fy1),getFloats(28.45f,74.25f,fy1)
				};
				*/
				Float[][] poswert1 = {getFloats(28.45f,90.35f,fy1),getFloats(28.45f,82.25f,fy1),getFloats(28.45f,74.25f,fy1),
									getFloats(69.25f,90.35f,fy1),getFloats(69.25f,82.25f,fy1),getFloats(69.25f,74.25f,fy1),
									getFloats(124.05f,90.35f,fy1),getFloats(124.05f,82.25f,fy1),getFloats(124.05f,74.25f,fy1),
									getFloats(159.8f,90.35f,fy1),getFloats(159.8f,82.25f,fy1),getFloats(159.8f,74.25f,fy1)
				};
				bf = BaseFont.createFont(BaseFont.HELVETICA,BaseFont.CP1250,BaseFont.EMBEDDED/*NOT_EMBEDDED*/);
				for(i = 7; i < 19; i++){
					setzeText(cb,poswert1[i-7][0], poswert1[i-7][1],poswert1[i-7][2],bf,12,( eltern.bchb[i].isSelected() ? "X" : "") );
				}
				//Erl�uterungen der CheckBox-Auswahl
				cb.setCharacterSpacing(0.25f);				
				ct = new ColumnText(cb);
				ct.setSimpleColumn(rechneX(28.5f), rechneY(60.f),rechneX(190.0f),rechneY(69.f),8.25f,Element.ALIGN_BOTTOM);
				ph = new Phrase();
				ph.setFont(FontFactory.getFont("Courier",9,Font.PLAIN));
				ph.add(eltern.bta[8].getText().trim());
				ct.addText(ph);
				ct.go();
				//Letzte Medikation
				cb.setCharacterSpacing(0.25f);				
				ct = new ColumnText(cb);
				ct.setSimpleColumn(rechneX(28.5f), rechneY(44.f),rechneX(190.0f),rechneY(54.0f),8.25f,Element.ALIGN_BOTTOM);
				ph = new Phrase();
				ph.setFont(FontFactory.getFont("Courier",9,Font.PLAIN));
				ph.add(eltern.bta[9].getText().trim());
				ct.addText(ph);
				ct.go();
				//Ort und Datum
				bf = BaseFont.createFont(BaseFont.COURIER,BaseFont.CP1250,BaseFont.EMBEDDED/*NOT_EMBEDDED*/);
				String text = eltern.btf[23].getText().trim();
				//Float [] fsign = getFloats(24.25f,27.60f,fy1);
				Float [] fort = getFloats(28.f,28.0f,fy0);
				if(! text.equals(".  .")){
					setzeText(cb,fort[0], fort[1],fort[2],bf,12,SystemConfig.sGutachtenOrt+", "+text);
				}else{
					setzeText(cb,fort[0], fort[1],fort[2],bf,12,SystemConfig.sGutachtenOrt+", ");
				}
				


				// ab hier das Stamper und Copy Ged�nse....				
				stamper2.close();
				cop.addPage(cop.getImportedPage(new PdfReader(baos.toByteArray()),1));
				baos.close();
				readeroriginal.close();
				
				
			}
			docktl.close();	
			cop.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (DocumentException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	private boolean doSeite1(boolean vorschau,String ausfertigung,String bereich){

		String pdfPfad = rvVorlagen[0];
		PdfStamper stamper = null;
		tempDateien[0] = new String[]{tempPfad+"NS1"+System.currentTimeMillis()+".pdf"};
		BaseFont bf = null;
		PdfReader reader = null;
		try {
			bf = BaseFont.createFont(BaseFont.COURIER_BOLD,BaseFont.CP1250,BaseFont.EMBEDDED /*.NOT_EMBEDDED*/);
			reader = new PdfReader (pdfPfad);
			stamper = new PdfStamper(reader,new  FileOutputStream(tempDateien[0][0]));
			PdfContentByte cb = stamper.getOverContent(1);
			Float [] pos = {null,null,null};
			float fy0 =  0.25f;
			float fy1 =  6.20f;
			// Hier die Positionierung f�r das obere Ged�nse
									//        0RV-Nr.
			Float[][] poswert1 = {getFloats(29.f,268.25f-15.0f,fy1),
							//      1Kennzeich                              2Name                           3Geburtst
								getFloats(103.30f,268.25f-15.0f,fy1),getFloats(29.f,260.0f-14.75f,fy0),getFloats(29.0f,251.5f-14.15f,fy1),
								//   4Strasse                             5PLZ                               6Ort
								getFloats(29.f,243.0f-13.65f,fy0),getFloats(29.f,234.5f-13.25f,fy1), getFloats(52.5f,234.5f-13.25f,fy0),
								//  7VersichertenName              8MSNR                                                          9BNR
								getFloats(29.f,226.0f-12.95f,fy0),getFloats(131.25f-7.f,268.25f-15.0f,fy1),getFloats(156.25f-3.25f,268.25f-15.0f,fy1)
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
			pos = getFloats(131.25f-7.f,226.0f-12.95f,fy1);
			setzeText(cb,pos[0], pos[1],pos[2],bf,12,eltern.btf[28].getText());
			//Abteilung-Nr.
			pos = getFloats(181.50f-4.75f,226.0f-12.95f,fy1);
			setzeText(cb,pos[0], pos[1],pos[2],bf,12,(eltern.btf[10].getText().trim().equals("") ? "2300" : eltern.btf[10].getText().trim() ) );
			//                                    Beginn                        Abschluss
			poswert1 = new Float [][]{getFloats(28.75f,197.25f,fy1),getFloats(93.25f,197.25f,fy1),
			//						       ICD-10 1                                  ICD-10 2
									getFloats(112.25f,178.5f-0.75f,fy1),getFloats(112.25f,166.65f-0.75f,fy1),
			//							   ICD-10 3                                  ICD-10 4
									getFloats(112.25f,154.80f-0.75f,fy1),getFloats(112.25f,142.75f-0.75f,fy1),
			//							   ICD-10 5                             Gewicht Beginn
									getFloats(112.25f,131.0f-0.75f,fy1),getFloats(162.25f,82.75f,fy1),
			//                             Gewicht Abschlu�               Untersuchungsdatum
									getFloats(181.70f,82.75f,fy1),getFloats(168.25f,46.0f,fy1),
			//						       arbeitsunf�hig seit			arbeitsunf�hig bis
									getFloats(85.f,23.0f,fy0),getFloats(139.5f,23.0f,fy0)									
			};
			for(int i = 11; i < 23; i++){
				if(i == 11 || i == 12  || i == 20){
					setzeText(cb,poswert1[i-11][0], poswert1[i-11][1],poswert1[i-11][2],bf,12,this.macheDatum2Sechs(eltern.btf[i].getText()));
					//setzeText(cb,poswert1[i-11][0], poswert1[i-11][1],poswert1[i-11][2],bf,12,this.macheDatum2Sechs(eltern.btf[i].getText()));
				}else if(i == 21 || i == 22){
					if(! eltern.btf[i].getText().trim().equals(".  .")){
						setzeText(cb,poswert1[i-11][0], poswert1[i-11][1],poswert1[i-11][2],bf,12,eltern.btf[i].getText());						
					}
				}else{
					setzeText(cb,poswert1[i-11][0], poswert1[i-11][1],poswert1[i-11][2],bf,12,eltern.btf[i].getText());
				}
			}
			// Jetzt kommen die Combo-Boxen der Diagnosen 1 - 5
			//                                       Seite 1                 Sicherheit 1                          Beh.Ergebnis 1
			poswert1 = new Float [][]{getFloats(141.05f,178.5f-0.75f,fy1),getFloats(152.80f,178.5f-0.75f,fy1),getFloats(164.65f,178.5f-0.75f,fy1),
			//                                       Seite 2                 Sicherheit 2                          Beh.Ergebnis 2		
									getFloats(141.05f,166.65f-0.75f,fy1),getFloats(152.80f,166.65f-0.75f,fy1),getFloats(164.65f,166.65f-0.75f,fy1),
			//                                       Seite 3                 Sicherheit 3                          Beh.Ergebnis 3									
									getFloats(141.05f,154.80f-0.75f,fy1),getFloats(152.80f,154.80f-0.75f,fy1),getFloats(164.65f,154.80f-0.75f,fy1),									
			//                                       Seite 4                 Sicherheit 4                          Beh.Ergebnis 4
									getFloats(141.05f,142.75f-0.75f,fy1),getFloats(152.80f,142.75f-0.75f,fy1),getFloats(164.65f,142.75f-0.75f,fy1),
			//                                       Seite 5                 Sicherheit 5                          Beh.Ergebnis 5
									getFloats(141.05f,131.0f-0.75f,fy1),getFloats(152.80f,131.0f-0.75f,fy1),getFloats(164.65f,131.0f-0.75f,fy1),									
					};
			for(int i = 0; i < 15; i++){
				setzeText(cb,poswert1[i][0], poswert1[i][1],poswert1[i][2],bf,12,(String)eltern.bcmb[i].getSelectedItem());
			}
			// Die Check-Boxen
			//                                 Herz/Kreislauf            Bewegungsapparat
			poswert1 = new Float [][]{getFloats(28.6f,109.30f,fy0),getFloats(64.35f,109.30f,fy0),
			//                                 Nevensysten            Psyche										
								getFloats(105.1f,109.30f,fy0),getFloats(138.50f,109.30f,fy0),
			//                                 sonstige				  abreitsf�hig ja!				
								getFloats(162.35f,109.30f,fy0),getFloats(28.6f,22.5f,fy0),
			//                         abreitsf�hig nein!				  								
								getFloats(48.25f,22.5f,fy0)
			};
			bf = BaseFont.createFont(BaseFont.HELVETICA,BaseFont.CP1250,BaseFont.EMBEDDED/*.NOT_EMBEDDED*/);
			for(int i = 0; i < 7; i++){
				setzeText(cb,poswert1[i][0], poswert1[i][1],poswert1[i][2],bf,12,( eltern.bchb[i].isSelected() ? "X" : "") );
			}
			/***********Jetzt der mehrzeilige Text der Diagnosen 1-5******************/
			cb.setCharacterSpacing(0.5f);
			/*
			float xstart = 82.f;
			float xend = 282.f;
			float ystartunten = 495.f;
			float ystartoben = 530.f;
			float yschritt = 35.f;
			*/
			float xstart = rechneX(33.f);
			float xend = rechneX(105.f);
			float ystartunten = rechneY(177.f);
			float ystartoben = rechneY(189.f);
			float yschritt = rechneY(12.0f);

			ColumnText ct = null;
			//Chunk chunk;
			Phrase ph = null;
			//float zaehler = 1.f;
			for(int i = 0;i < 5; i++){
				/*
		        chunk = new Chunk(eltern.bta[i].getText().trim());
		         chunk.setFont(FontFactory.getFont("Courier",9,Font.PLAIN));
		         ct = new ColumnText(cb);
		         ct.addText(chunk);
		         ct.setSimpleColumn(xstart, ystartunten,xend,ystartoben,8,Element.ALIGN_BOTTOM);
		         ct.go();
				//float yAbsolutePosition = iTextDoc.bottomMargin() + iTextPdfPTable.getTotalHeight(); 
				//iTextPdfPTable.writeSelectedRows(0, -1, iTextDoc.leftMargin(), yAbsolutePosition, pdfWriter.getDirectContent());
		         */
		        

				ct = new ColumnText(cb);
				ct.setSimpleColumn(xstart, ystartunten,xend,ystartoben,8,Element.ALIGN_BOTTOM);
				ph = new Phrase();
				ph.setFont(FontFactory.getFont("Courier",8,Font.PLAIN));
				ph.add(eltern.bta[i].getText().trim());
				ct.addText(ph);
				ct.go();

				ystartunten -= (yschritt);
				//ystartunten -= (yschritt+zaehler);
				ystartoben = (ystartunten+yschritt);
			}
			//Erl�uterungen
			ct = new ColumnText(cb);
			ct.setSimpleColumn(rechneX(29.f), rechneY(81.f),rechneX(139.0f),rechneY(101.f),8,Element.ALIGN_BOTTOM);
			ph = new Phrase();
			ph.setFont(FontFactory.getFont("Courier",9,Font.PLAIN));
			ph.add(eltern.bta[5].getText().trim());
			ct.addText(ph);
			ct.go();
			//Beschreibung pr�/post
			
			ct = new ColumnText(cb);
			ct.setSimpleColumn(rechneX(29.f), rechneY(48.f),rechneX(182.0f),rechneY(76.f),8,Element.ALIGN_BOTTOM);
			ph = new Phrase();
			ph.setFont(FontFactory.getFont("Courier",9,Font.PLAIN));
			ph.add(eltern.bta[6].getText().trim());
			ct.addText(ph);
			ct.go();
			 
			//PDFTools.FliessText(rechneX(29.f), rechneY(48.f), rechneX(160.0f),rechneY(76.f), cb, eltern.bta[6].getText().trim(),stamper);
			
			/*****************************************************************/
			// Der Block rechts oben mit der Einrichtungsadresse
			StringBuffer reha = new StringBuffer();
			int lang = SystemConfig.vGutachtenAbsAdresse.size();
			for(int i = 0; i < lang;i++){
				reha.append(SystemConfig.vGutachtenAbsAdresse.get(i)+(i < (lang-1) ? "\n" : ""));
			}
			// Reha-Einrichtung
			Float[] rehaunten =  getFloats(125.0f,220.0f,0.5f);
			Float[] rehaoben =  getFloats(195.0f,249.0f,0.5f);
			ct = new ColumnText(cb);
			ct.setSimpleColumn(rehaunten[0], rehaunten[1],rehaoben[0],rehaoben[1],11,Element.ALIGN_BASELINE);
			ph = new Phrase();
			ph.setFont(FontFactory.getFont("Helvectica",10,Font.PLAIN));
			ph.add(reha.toString());
			ct.addText(ph);
			ct.go();
			
			/*****************************************************************/

			
			/*********************************/
			stamper.setFormFlattening(true);
			stamper.close();
			reader.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}

		return true;
	}	
	public boolean doSeitenZusammenstellen() throws IOException, DocumentException{
		Document docgesamt = new Document(PageSize.A4);
		//InputStream isb = null;
		//PdfImportedPage page2;		
		
		tempDateien[2] = new String[]{tempPfad+"NSGesamt"+System.currentTimeMillis()+".pdf"};
		
		PdfCopy cop = new PdfCopy(docgesamt,new FileOutputStream(tempDateien[2][0]));
		docgesamt.open();

		cop.addPage(cop.getImportedPage(new PdfReader(tempDateien[0][0]),1));
		cop.addPage(cop.getImportedPage(new PdfReader(tempDateien[1][0]),1));

		docgesamt.close();
		cop.close();

		

		return false;
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

	@SuppressWarnings("unused")
	private void schreibeKTLErlaeut(int ierlaeut,PdfContentByte cb){
		ColumnText ct = null;
		Phrase ph = null;
		float xstart = rechneX(25.f);
		float ystartunten = rechneY(27.0f);
		float xende = rechneX(195.0f);
		float ystartoben = rechneY(42.f);
		ct = new ColumnText(cb);
		ct.setSimpleColumn(xstart, ystartunten,xende,ystartoben,8,Element.ANCHOR);
		ph = new Phrase();
		ph.setFont(FontFactory.getFont("Courier",9,Font.PLAIN));
		if(ierlaeut > 0){
			ph.add((String)this.eltern.bta[9].getText());			
		}else{
			ph.add((String)this.eltern.bta[8].getText());
		}
		ct.addText(ph);
		try {
			ct.go();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	private void schreibeKTLCode(int position,int ktlpos,Float[] startwerte,PdfContentByte cb){
		//                    0x-start,  1y-start,  2h�he  3y-ende  4xCode   5xDauer  6xAnzahl
		//Float[] startwerte = {30.0f ,    251.f      ,8.5f, 149.f,  154.75f,   181.f  ,191.f};
		float stretch = 6.2f;
		float ystartunten = rechneY(startwerte[1]-(new Float(position) * startwerte[2])+0.5f);
		float xcode = rechneX(startwerte[4]);
		float xdauer = rechneX(startwerte[5]);
		float xanzahl = rechneX(startwerte[6]);	
		BaseFont bf;
		try {
			bf = BaseFont.createFont(BaseFont.COURIER,BaseFont.CP1250,BaseFont.EMBEDDED/*.NOT_EMBEDDED*/);
			setzeText(cb,xcode, ystartunten,stretch,bf,12,eltern.ktltfc[ktlpos].getText());
			setzeText(cb,xdauer, ystartunten,stretch,bf,12,eltern.ktltfd[ktlpos].getText());
			if(eltern.ktltfa[ktlpos].getText().trim().length()==1 ){
				setzeText(cb,xanzahl, ystartunten,stretch,bf,12,"0"+eltern.ktltfa[ktlpos].getText());
			}else{
				setzeText(cb,xanzahl, ystartunten,stretch,bf,12,eltern.ktltfa[ktlpos].getText());				
			}

		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private void schreibeKTLText(int position,int ktlpos,Float[] startwerte,PdfContentByte cb){
		ColumnText ct = null;
		Phrase ph = null;
		//float zaehler = 1.f;
 
		//                     x-start,  y-start,  h�he   x-ende
		//Float[] startwerte = {28.0f ,  251.f      ,8.5f, 152.f};

		float xstart = rechneX(startwerte[0]+1.5f);
		float ystartunten = startwerte[1]-(new Float(position) * startwerte[2])-2.f;
		float xende = rechneX(startwerte[3]);
		float ystartoben = rechneY(ystartunten+ startwerte[2]+(0.1f*(position-1)) );
		ystartunten = rechneY(ystartunten);
		ct = new ColumnText(cb);
		ct.setSimpleColumn(xstart, ystartunten,xende,ystartoben,8,Element.ALIGN_TOP);
		
		ph = new Phrase();
		ph.setFont(FontFactory.getFont("Courier",9,Font.PLAIN));
		ph.add((String)eltern.ktlcmb[ktlpos].getSelectedItem());
		ct.addText(ph);
		try {
			ct.go();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void schreibeEmpfaenger(PdfContentByte cb,String empfaenger) throws DocumentException, IOException{
		float fy0 =  0.25f;
		//float fy1 =  7.1f;
		//xx
		Float[] xempfaenger = getFloats(27.5f,281.60f,fy0);
		//Float[] xbereich = getFloats(155.5f,277.60f,fy0);

		BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA_BOLD,BaseFont.CP1250,BaseFont.EMBEDDED/*.NOT_EMBEDDED*/); 
		cb.beginText();
		cb.moveText(xempfaenger[0], xempfaenger[1]);
		cb.setFontAndSize(bf,10.5f);
		//cb.setCharacterSpacing(xempfaenger[2]);
		cb.showText("Ausfertigung für "+empfaenger);
		cb.endText();
	}
	
	private void doNachsorgeDrucken(int[] exemplare){
		String[] empfs = {"die Deutsche Rentenversicherung - "+(String) eltern.cbktraeger.getSelectedItem(),
				"die Deutsche Rentenversicherung - "+(String) eltern.cbktraeger.getSelectedItem(),
				"den behandelnden Arzt","die Nachsorgeeinrichtung"};

		try {

			for(int i = 0; i < 4; i++){
				String tempversion = tempPfad+"Print"+System.currentTimeMillis()+".pdf";
				Document docversion = new Document(PageSize.A4);
				ByteArrayOutputStream baout = null;
				//PdfImportedPage pageImport;
				PdfCopy cop;

				cop = new PdfCopy(docversion,new FileOutputStream(tempversion));
				docversion.open();

				PdfReader rvorlage = new PdfReader(tempDateien[2][0]);
				baout = new ByteArrayOutputStream();
				PdfStamper stamper = new PdfStamper(rvorlage,baout);
				PdfContentByte cb1 = stamper.getOverContent(1);
				schreibeEmpfaenger(cb1,empfs[i]);
				stamper.setFormFlattening(true);
				stamper.close();
				PdfReader seitefertig = new PdfReader(baout.toByteArray());
				cop.addPage(cop.getImportedPage(seitefertig,1));
				seitefertig.close();
				rvorlage.close();
				baout.close();
				PdfReader readseite2 = new PdfReader(tempDateien[2][0]);
				cop.addPage(cop.getImportedPage(readseite2,2));
				readseite2.close();
				cop.close();
				docversion.close();
				if(exemplare[5]> 0){
					new ReaderStart(tempversion);
				}else{
					druckeVersion(tempversion);
				}
				if(exemplare[4]<= 0){
					return;
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void druckeVersion(String datei){
		final String xcmd = "java -jar "+Reha.proghome+"PDFDrucker.jar "+datei;
		
		try {
			Runtime.getRuntime().exec(xcmd);
			//System.out.println(xcmd);
			Reha.thisClass.progressStarten(false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}	