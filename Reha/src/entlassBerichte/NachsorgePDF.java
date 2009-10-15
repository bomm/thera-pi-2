package entlassBerichte;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.SwingWorker;

import systemEinstellungen.SystemConfig;

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
		final String xdatei =  tempDateien[0][0];
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				try{
					/*
					Reha.thisClass.progressStarten(true);
					String cmd = "java -jar ";
					System.out.println("Starte "+cmd+" "+Reha.proghome+"PDFViewerDrucker.jar"+" "+xdatei);
					final String xcmd = cmd;
					SwingUtilities.invokeLater(new Runnable(){
						public  void run(){
							try {
								Runtime.getRuntime().exec(xcmd+" "+Reha.proghome+"PDFDrucker.jar "+xdatei);
								Reha.thisClass.progressStarten(false);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
					*/
					//Process process = new ProcessBuilder(cmd,"",Reha.proghome+"PDFViewerDrucker.jar"+" "+xdatei).start();
					
					Process process = new ProcessBuilder(SystemConfig.hmFremdProgs.get("AcrobatReader"),"",xdatei).start();
					InputStream is = process.getInputStream();
					
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader br = new BufferedReader(isr);
					String line;
					 Reha.thisClass.progressStarten(false);							       
			       while ((line = br.readLine()) != null) {
			         System.out.println("Lade Adobe "+line);
			       }
			       is.close();
			       isr.close();
			       br.close();
			      
					
				}catch(Exception ex){
					Reha.thisClass.progressStarten(false);
				}
				return null;
			}
		}.execute();
		
		
		
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
			float fy1 =  6.20f;
			// Hier die Positionierung für das obere Gedönse
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
			//                             Gewicht Abschluß               Untersuchungsdatum
									getFloats(181.70f,82.75f,fy1),getFloats(168.25f,46.0f,fy1),
			//						       arbeitsunfähig seit			arbeitsunfähig bis
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
			//                                 sonstige				  abreitsfähig ja!				
								getFloats(162.35f,109.30f,fy0),getFloats(28.6f,22.5f,fy0),
			//                         abreitsfähig nein!				  								
								getFloats(48.25f,22.5f,fy0)
			};
			

			bf = BaseFont.createFont(BaseFont.HELVETICA,BaseFont.CP1252,BaseFont.NOT_EMBEDDED);
			for(int i = 0; i < 7; i++){
				setzeText(cb,poswert1[i][0], poswert1[i][1],poswert1[i][2],bf,12,( eltern.bchb[i].isSelected() ? "X" : "") );
			}
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
