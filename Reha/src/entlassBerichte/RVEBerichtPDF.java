package entlassBerichte;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import systemEinstellungen.SystemConfig;

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
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

import hauptFenster.Reha;

public class RVEBerichtPDF {
	public EBerichtPanel eltern = null;

	public String vorlagenPfad = Reha.proghome+"vorlagen/"+Reha.aktIK+"/";
	public String[] rvVorlagen = {null,null,null,null};
	String[][] tempDateien = {null,null,null,null,null};
	public String tempPfad = Reha.proghome+"temp/"+Reha.aktIK+"/";

	
	public RVEBerichtPDF(EBerichtPanel xeltern, boolean nurVorschau,int[] versionen){
		eltern = xeltern;
		rvVorlagen[0]  = vorlagenPfad+"EBericht-Seite1-Variante2.pdf";
		rvVorlagen[1]  = vorlagenPfad+"EBericht-Seite2-Variante2.pdf";
		rvVorlagen[2]  = vorlagenPfad+"EBericht-Seite3-Variante2.pdf";
		rvVorlagen[3]  = vorlagenPfad+"EBericht-Seite4-Variante2.pdf";
		doRVVorschau(nurVorschau,"","");
	}
	private void doRVVorschau(boolean vorschau,String ausfertigung,String bereich){
		boolean geklappt;
		
		// Zu Beginn sicherstellen daß die OO.org PDF produziert wird.
		
			try {
				
				File ft = new File(tempPfad+"EBfliesstext.pdf");
				if(! ft.exists()){
					JOptionPane.showMessageDialog(null, "Fließtext noch nicht aufbereitet!\n"+
							"Wechseln sie auf den Karteireiter 'Fliesstext' und starten Sie\n"+
							"die Druckvorschau erneut.");
					return;
				}
				System.out.println("Konfiguriere Seite 1");
				geklappt = doSeite1(vorschau,ausfertigung,bereich);
				if(!geklappt){
					JOptionPane.showMessageDialog(null,"Fehler beim Aufbau der Seite - 1 ");
					return;
				}
				System.out.println("Konfiguriere Seite 2");				
				geklappt = doSeite2();
				if(!geklappt){
					JOptionPane.showMessageDialog(null,"Fehler beim Aufbau der Seite - 2 ");
					return;
				}
				System.out.println("Konfiguriere Seite 3");				
				geklappt = doSeite3();
				if(!geklappt){
					JOptionPane.showMessageDialog(null,"Fehler beim Aufbau der Seite - KTL 1-2 ");
					return;
				}
				System.out.println("Stelle Kapitel zusammen");
				geklappt = doSeitenZusammenstellen();

				// AdobeReader starten
				//final String xdatei =  "C:/RehaVerwaltung/temp/510841109/freitext.pdf";
				//final String xdatei =  tempDateien[3][0];//sdatei;
				Reha.thisClass.progressStarten(true);
				//System.out.println("Es wird die PDF-Datei "+tempDateien[4][0]+" gedruckt");
				//PdfDrucker.setup(tempDateien[4][0]);



				
				final String xdatei =  tempDateien[4][0];
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
						
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	}
	/***********
	 * 
	 * E-Bericht Seite 1 
	 * 
	 * 
	 * 
	 * 
	 **********/
	private boolean doSeite1(boolean vorschau,String ausfertigung,String bereich){
		String pdfPfad = rvVorlagen[0];
		PdfStamper stamper = null;
			
		// Geschiss bis die bestehende PDF eingelesen und gestampt ist
		tempDateien[0] = new String[]{Reha.proghome+"temp/"+Reha.aktIK+"/EB1"+System.currentTimeMillis()+".pdf"};
		 
		BaseFont bf;
		try {
			bf = BaseFont.createFont(BaseFont.COURIER,BaseFont.CP1252,BaseFont.NOT_EMBEDDED);
		PdfReader reader = new PdfReader (pdfPfad);
		//ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		stamper = new PdfStamper(reader,new  FileOutputStream(tempDateien[0][0]));
		// Die Ausfertigung händeln...		
		/*
		AcroFields form = stamper.getAcroFields();
		form.setField("Ausfertigung", ausfertigung);
		form.setField("Bereich", bereich);
		*/
		// Die Ausfertigung händeln...---				
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
		/* 
		 * 
		 */
		// Jetzt kommen die Felder ab stationär, ganztägig ambulant etc. bis letztes Feld = Körpergröße
		Float[][] poswert2 = {	getFloats(29.25f,218.0f,fy1),getFloats(67.10f,218.0f,fy1),
								getFloats(29.25f,196.5f,fy1),getFloats(67.10f,196.5f,fy1),
								getFloats(29.25f,205.30f,fy1),getFloats(67.10f,205.30f,fy1)	};
		for(int i = 11; i < 17;i++){
			text = eltern.btf[i].getText().trim();
			if(! text.equals(".  .")){
				text = macheDatum2Sechs(text);
				setzeText(cb,poswert2[i-11][0], poswert2[i-11][1],poswert2[i-11][2],bf,12,text);						
			}
		}	
		/*******************************************************************/
		// Jetzt die Diagnoseschlüssel
		//                             Diag1                            Diag2
		Float[][] poswert3 = {	getFloats(113.10f,175.70f,fy1),getFloats(113.10f,162.95f,fy1),
		//                             Diag3                            Diag4
								getFloats(113.10f,150.00f,fy1),getFloats(113.10f,137.50f,fy1),
		//                             Diag5						
								getFloats(113.10f,124.40f,fy1)};
		for(int i = 17; i < 22;i++){
			text = eltern.btf[i].getText().trim();
			setzeText(cb,poswert3[i-17][0], poswert3[i-17][1],poswert3[i-17][2],bf,12,text);						

		}	
		/*******************************************************************/
		// Dann die CheckBoxen auswerten
		float xfs = 0.0f;
		float yfs = 0.0f;
		bf = BaseFont.createFont(BaseFont.HELVETICA,BaseFont.CP1252,BaseFont.NOT_EMBEDDED);
		Float[][] poswert4 =
			//	 		  0                          1                                             					
				{getFloats(26.60f+xfs,77.75f+yfs,fy0),getFloats(54.60f+xfs,77.75f+yfs,fy0),
			//	          2                          3
				 getFloats(26.60f+xfs,69.25f+yfs,fy0),getFloats(54.60f+xfs,69.25f+yfs,fy0),
			//            4                          5                       
				 getFloats(26.60f+xfs,60.85f+yfs,fy0),getFloats(54.60f+xfs,60.85f+yfs,fy0),
			//			  6                          7                                         
				 getFloats(90.15f+xfs,77.75f+yfs,fy0),getFloats(120.50f+xfs,77.75f+yfs,fy0),
			//	          8							 9  
				 getFloats(90.15f+xfs,69.25f+yfs,fy0),getFloats(120.50f+xfs,69.25f+yfs,fy0),
			//	         10                         11               
				 getFloats(90.15f+xfs,60.85f+yfs,fy0),getFloats(120.50f+xfs,60.85f+yfs,fy0),
			//	         12                         13                                                
				getFloats(145.75f+xfs,77.75f+yfs,fy0),getFloats(176.20f+xfs,77.75f+yfs,fy0),
			//	         14                         15
				getFloats(145.75f+xfs,69.25f+yfs,fy0),getFloats(176.20f+xfs,69.25f+yfs,fy0),
			//           16
				getFloats(145.75f+xfs,60.85f+yfs,fy0)};
		for(int i = 0; i <17; i++){
			if(eltern.bchb[i].isSelected()){
				text = "X";
				setzeText(cb,poswert4[i][0], poswert4[i][1],poswert4[i][2],bf,14,text);
			}
		}
		bf = BaseFont.createFont(BaseFont.COURIER,BaseFont.CP1252,BaseFont.NOT_EMBEDDED);
		/*************************************************************************/
		// Gewichte und Körpergröße             1                              2                            3
		Float[][] poswert5 = {getFloats(29.25f,116.50f,fy1),getFloats(29.25f,108.25f,fy1),getFloats(29.25f,99.80f,fy1) }; 
		for(int i = 22; i < 25;i++){
			text = eltern.btf[i].getText().trim();
			setzeText(cb,poswert5[i-22][0], poswert5[i-22][1],poswert5[i-22][2],bf,12,text);						
		}
		/*************************************************************************/
		// Das Unterschriftsdatum = eltern.btf[27].getText() + den Ort
		text = eltern.btf[27].getText().trim();
		Float [] fsign = getFloats(24.25f,27.60f,fy1);
		if(! text.equals(".  .")){
			text = macheDatum2Sechs(text);
			setzeText(cb,fsign[0], fsign[1],fsign[2],bf,12,text);						
		}
		Float [] fort = getFloats(61.25f,27.60f,fy0);
		setzeText(cb,fort[0], fort[1],fort[2],bf,12,SystemConfig.sGutachtenOrt);
		/*************************************************************************/
		// Jetzt die ComboBoxen abarbeiten
		Float[][] poswert6 = {
			//   0=Enlassform                      1	
			getFloats(115.65f,213.65f,fy1),getFloats(163.45f,213.65f,fy1),
			//   2=Diag1Teil                       3                                  4
			getFloats(143.35f,175.70f,fy1),getFloats(155.75f,175.70f,fy1),getFloats(168.25f,175.70f,fy1),
			//   5=Diag2Teil				       6                                  7
			getFloats(143.35f,162.95f,fy1),getFloats(155.75f,162.95f,fy1),getFloats(168.25f,162.95f,fy1),
			//   8=Diag3Teil 					   9								 10
			getFloats(143.35f,150.00f,fy1),getFloats(155.75f,150.00f,fy1),getFloats(168.25f,150.00f,fy1),
			//  11=Diag4Teil					  12								 13 
			getFloats(143.35f,137.50f,fy1),getFloats(155.75f,137.50f,fy1),getFloats(168.25f,137.50f,fy1),
			//  14=Diag5Teil					  15								 16
			getFloats(143.35f,124.40f,fy1),getFloats(155.75f,124.40f,fy1),getFloats(168.25f,124.40f,fy1),
			//  17=Ursache der..				  18								 19
			getFloats(77.15f,116.50f,fy1),getFloats(128.35f,116.50f,fy1),getFloats(166.35f,116.50f,fy1)
			
		};
		for(int i = 0; i < poswert6.length;i++){
			text = ((String)eltern.bcmb[i].getSelectedItem()).trim();
			setzeText(cb,poswert6[i][0], poswert6[i][1],poswert6[i][2],bf,12,text);						
		}
		/***********Jetzt der mehrzeilige Text der Diagnosen 1-5******************/
		cb.setCharacterSpacing(0.5f);
		float xstart = 82.f;
		float xend = 282.f;
		float ystartunten = 495.f;
		float ystartoben = 530.f;
		float yschritt = 35.f;
		ColumnText ct = null;
		Phrase ph = null;
		float zaehler = 1.f;
		for(int i = 0;i < 5; i++){
			ct = new ColumnText(cb);
			ct.setSimpleColumn(xstart, ystartunten,xend,ystartoben,8,Element.ANCHOR);
			ph = new Phrase();
			ph.setFont(FontFactory.getFont("Courier",9,Font.PLAIN));
			ph.add(eltern.bta[i].getText().trim());
			ct.addText(ph);
			ct.go();
			
			ystartunten -= (yschritt+zaehler);
			ystartoben = (ystartunten+yschritt);
		}
		// Erläuternungen Box
		Float[] empfunten =  getFloats(24.50f,40.0f,0.5f);
		Float[] empfoben =  getFloats(201.00f,51.25f,0.5f);
		ct = new ColumnText(cb);
		ct.setSimpleColumn(empfunten[0], empfunten[1],empfoben[0],empfoben[1],9,Element.ALIGN_BASELINE);
		ph = new Phrase();
		ph.setFont(FontFactory.getFont("Courier",10,Font.PLAIN));
		ph.add(eltern.bta[5].getText().trim());
		ct.addText(ph);
		ct.go();
		/*****************************************************************/
		// Der Block rechts oben mit der Einrichtungsadresse
		StringBuffer reha = new StringBuffer();
		int lang = SystemConfig.vGutachtenAbsAdresse.size();
		for(int i = 0; i < lang;i++){
			reha.append(SystemConfig.vGutachtenAbsAdresse.get(i)+(i < (lang-1) ? "\n" : ""));
		}
		// Reha-Einrichtung
		Float[] rehaunten =  getFloats(131.25f,242.0f,0.5f);
		Float[] rehaoben =  getFloats(199.00f,264.0f,0.5f);
		ct = new ColumnText(cb);
		ct.setSimpleColumn(rehaunten[0], rehaunten[1],rehaoben[0],rehaoben[1],11,Element.ALIGN_BASELINE);
		ph = new Phrase();
		ph.setFont(FontFactory.getFont("Helvectica",10,Font.PLAIN));
		ph.add(reha.toString());
		ct.addText(ph);
		ct.go();
		
		/*****************************************************************/
		stamper.setFormFlattening(true);
		stamper.close();
		reader.close();
		/*****************************************************************/		
		} catch (DocumentException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;			
		}
		return true;
	}
	/***********
	 * 
	 * E-Bericht Seite 2 
	 * 
	 * 
	 * 
	 * 
	 **********/
	private boolean doSeite2(){
		
		try {
			String pdfPfad = rvVorlagen[1];
			tempDateien[1] = new String[]{Reha.proghome+"temp/"+Reha.aktIK+"/EB2"+System.currentTimeMillis()+".pdf"};
			PdfReader reader = new PdfReader (pdfPfad);
			PdfStamper stamper2 = new PdfStamper(reader,new  FileOutputStream(tempDateien[1][0]));

			PdfContentByte cb = stamper2.getOverContent(1);
			BaseFont bf = BaseFont.createFont(BaseFont.COURIER,BaseFont.CP1252,BaseFont.NOT_EMBEDDED);
			String text = "";
			Float [] pos = {null,null,null};
			float fy0 =  0.25f;
			float fy1 =  6.9f;
			pos = getFloats(24.50f,268.00f,fy0);
			setzeText(cb,pos[0], pos[1],pos[2],bf,12,eltern.btf[2].getText());
			pos = getFloats(171.50f,268.00f,fy1);
			setzeText(cb,pos[0], pos[1],pos[2],bf,12,macheDatum2Sechs(eltern.btf[3].getText()));
			pos = getFloats(78.00f,248.00f,fy0);
			setzeText(cb,pos[0], pos[1],pos[2],bf,9,eltern.btf[25].getText());
			Float[] xseite = getFloats(188.25f,278.5f,fy0);
			int seite = 1;
			cb.beginText();
			cb.moveText(xseite[0], xseite[1]);
			cb.setFontAndSize(bf,11);
			cb.setCharacterSpacing(xseite[2]);
			cb.showText(new Integer(seite).toString());
			cb.endText();


			/* Für ältere Berichte < 01.01.2008 noch den Berufsklassenschlüssel einbauen
			pos = getFloats(78.00f,247.00f,fy0);
			setzeText(cb,pos[0], pos[1],pos[2],bf,12,macheDatum2Sechs(eltern.btf[26].getText()));
			*/
			
			/************
			 * FloatWerte von Christian
			 * 
			 */


			Float[][] poswert1 = 
			//	  17                                  18                          19
			{getFloats(90.1f,238.0f,fy0),getFloats(128.25f,238.0f,fy0),getFloats(171.1f,238.0f,fy0),
					//   20                                 21                               22                           23
					getFloats(77.4f,204.75f,fy0),getFloats(112.8f,204.75f,fy0),getFloats(156.0f,204.75f,fy0),getFloats(186.4f,204.75f,fy0),	
					//       24                            25                          26                       27        
					getFloats(28.75f,192.0f,fy0),getFloats(46.7f,192.0f,fy0),getFloats(64.7f,192.0f,fy0),getFloats(87.7f,192.0f,fy0),
					//         28                          29                              30                       31         
					getFloats(105.3f,192.0f,fy0),getFloats(123.2f,192.0f,fy0),getFloats(146.0f,192.0f,fy0),getFloats(163.6f,192.0f,fy0),
					//         32
					getFloats(181.4f,192.0f,fy0),
					//          33                      34                               35
					getFloats(77.3f,183.55f,fy0),getFloats(112.8f,183.55f,fy0),getFloats(155.9f,183.55f,fy0),
					//          36                        37                         38
					getFloats(36.4f,175.0f,fy0),getFloats(36.4f,153.9f,fy0),getFloats(36.4f,141.3f,fy0),
					//          39                        40
					getFloats(36.4f,128.85f,fy0),getFloats(36.40f,116.15f,fy0),
					//           41                        42                       43
					getFloats(90.23f,22.8f,fy0),getFloats(128.23f,22.8f,fy0),getFloats(171.13f,22.8f,fy0),

			};
			bf = BaseFont.createFont(BaseFont.HELVETICA,BaseFont.CP1252,BaseFont.NOT_EMBEDDED);
			text = "X";    // < 44
			for(int i = 17; i < 44;i++){
				if(eltern.bchb[i].isSelected()){
					setzeText(cb,poswert1[i-17][0], poswert1[i-17][1],poswert1[i-17][2],bf,14,text);
				}
			}
			ColumnText ct = null;
			Phrase ph = null;
			//bf = BaseFont.createFont(BaseFont.COURIER,BaseFont.CP1252,BaseFont.NOT_EMBEDDED);
			Float[] rehaunten =  getFloats(31.00f,34.0f,0.5f);
			Float[] rehaoben =  getFloats(195.00f,104.0f,0.5f);
			ct = new ColumnText(cb);
			ct.setSimpleColumn(rehaunten[0], rehaunten[1],rehaoben[0],rehaoben[1],9,Element.ANCHOR);
			ph = new Phrase();
			ph.setFont(FontFactory.getFont("Courier",9,Font.PLAIN));
			ph.add(eltern.bta[7].getText());
			ct.addText(ph);
			ct.go();

			stamper2.close();
			
		} catch (DocumentException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
		
	}

	/***********
	 * 
	 * E-Bericht Seite 3 = KTL 1 - 2 
	 * 
	 * 
	 * 
	 * 
	 **********/

	private boolean doSeite3(){
		tempDateien[2] = new String[]{Reha.proghome+"temp/"+Reha.aktIK+"/EB3"+System.currentTimeMillis()+".pdf"};
		//String pdfPfad = rvVorlagen[2];
		//PdfReader reader;
		try {
			BaseFont bf = BaseFont.createFont(BaseFont.COURIER,BaseFont.CP1252,BaseFont.NOT_EMBEDDED);
			//String text = "";
			Float [] pos = {null,null,null};
			float fy0 =  0.25f;
			float fy1 =  6.9f;
			Float[] xseite = getFloats(188.25f,278.5f,fy0);
			
			Document docktl = new Document(PageSize.A4);
			PdfCopy cop = new PdfCopy(docktl,new FileOutputStream(tempDateien[2][0]));
			docktl.open();
			int zugabe = 0;
			for(int i = 0;i < 2;i++){
				ByteArrayOutputStream baos  = new ByteArrayOutputStream();
				PdfReader readeroriginal = new PdfReader(rvVorlagen[2]);
				PdfStamper stamper2 = new PdfStamper(readeroriginal,baos);
				PdfContentByte cb = stamper2.getOverContent(1);


				
				
				pos = getFloats(24.50f,268.00f,fy0);
				setzeText(cb,pos[0], pos[1],pos[2],bf,12,eltern.btf[2].getText());
				pos = getFloats(171.50f,268.00f,fy1);
				setzeText(cb,pos[0], pos[1],pos[2],bf,12,macheDatum2Sechs(eltern.btf[3].getText()));
				int seite = i+1;

				cb.beginText();
				cb.moveText(xseite[0], xseite[1]);
				cb.setFontAndSize(bf,11);
				cb.setCharacterSpacing(xseite[2]);
				cb.showText(new Integer(seite).toString());
				cb.endText();
				
				//                    0x-start,  1y-start,  2höhe  3y-ende  4xCode   5xDauer  6xAnzahl
				Float[] startwerte = {30.0f ,    251.f      ,8.5f, 149.f,  153.75f,   178.5f  ,191.75f};
				for(int y=0;y < 25;y++){
					if(eltern.ktlcmb[y+zugabe].getSelectedIndex() > 0){
						cb.setCharacterSpacing(0.25f);
						schreibeKTLText(y,y+zugabe,startwerte,cb);
						schreibeKTLCode(y,y+zugabe,startwerte,cb);
					}else{
						break;
					}
				}

				cb.setCharacterSpacing(0.25f);
				schreibeKTLErlaeut(zugabe,cb);
				// ab hier das Stamper und Copy Gedönse....				
				stamper2.close();
				cop.addPage(cop.getImportedPage(new PdfReader(baos.toByteArray()),1));
				baos.close();
				readeroriginal.close();
				zugabe = 25;
				if(eltern.ktlcmb[25].getSelectedIndex()<=0){
					break;
				}
				

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
		//                    0x-start,  1y-start,  2höhe  3y-ende  4xCode   5xDauer  6xAnzahl
		//Float[] startwerte = {30.0f ,    251.f      ,8.5f, 149.f,  154.75f,   181.f  ,191.f};
		float stretch = 6.9f;
		float ystartunten = rechneY(startwerte[1]-(new Float(position) * startwerte[2])+0.5f);
		float xcode = rechneX(startwerte[4]);
		float xdauer = rechneX(startwerte[5]);
		float xanzahl = rechneX(startwerte[6]);	
		BaseFont bf;
		try {
			bf = BaseFont.createFont(BaseFont.COURIER,BaseFont.CP1252,BaseFont.NOT_EMBEDDED);
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
		float zaehler = 1.f;
 
		//                     x-start,  y-start,  höhe   x-ende
		//Float[] startwerte = {28.0f ,  251.f      ,8.5f, 152.f};

		float xstart = rechneX(startwerte[0]+1.5f);
		float ystartunten = startwerte[1]-(new Float(position) * startwerte[2])-2.f;
		float xende = rechneX(startwerte[3]);
		float ystartoben = rechneY(ystartunten+ startwerte[2]);
		ystartunten = rechneY(ystartunten);
		ct = new ColumnText(cb);
		ct.setSimpleColumn(xstart, ystartunten,xende,ystartoben,8,Element.ALIGN_BOTTOM);
		
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
	/**************
	 * 
	 * 
	 * 
	 * @return
	 * @throws IOException
	 * @throws DocumentException
	 */
	public boolean doSeitenZusammenstellen() throws IOException, DocumentException{
		InputStream isb = null;
		tempDateien[3] = new String[]{tempPfad+"EB4"+System.currentTimeMillis()+".pdf"};
		String pdfPfad = rvVorlagen[3];
		Document docgesamt = new Document(PageSize.A4);
		File ft = new File(tempPfad+"EBfliesstext.pdf");
		PdfReader reader = new PdfReader(tempPfad+"EBfliesstext.pdf");

		int seiten = reader.getNumberOfPages();
		System.out.println("Insgesamt Seiten Fließtext = "+seiten);
	
		PdfImportedPage page2;		
		
		tempDateien[4] = new String[]{tempPfad+"EBGesamt"+System.currentTimeMillis()+".pdf"};
		PdfCopy cop = new PdfCopy(docgesamt,new FileOutputStream(tempDateien[4][0]));
		docgesamt.open();
		
		ByteArrayOutputStream bpage1 = new ByteArrayOutputStream();
		PdfReader readerPage1 = new PdfReader (tempDateien[0][0]);
		PdfStamper stampPage1 = new PdfStamper(readerPage1,bpage1);
		PdfContentByte cbPage1 = stampPage1.getOverContent(1);
		schreibeVonBis(cbPage1,seiten);
		stampPage1.close();
		
		cop.addPage(cop.getImportedPage(new PdfReader(bpage1.toByteArray()),1));
		cop.addPage(cop.getImportedPage(new PdfReader(tempDateien[1][0]),1));
		
		PdfReader ktlreader = new PdfReader(tempDateien[2][0]);
		int ktlseiten = ktlreader.getNumberOfPages();
		for(int i = 1; i <= ktlseiten;i++){
			cop.addPage(cop.getImportedPage(ktlreader,i));
		}
		
		ByteArrayOutputStream bvorlage;
		Float[] xy = getFloats(17.f,13.f,0.f);
		for(int i = 1; i <= seiten; i++){
			//PdfReader rvorlage = new PdfReader(vorlage);
			PdfReader rvorlage = new PdfReader(pdfPfad);
			bvorlage = new ByteArrayOutputStream();
			PdfStamper stamper = new PdfStamper(rvorlage,bvorlage);
			PdfContentByte cb2 = stamper.getOverContent(1);
			page2 = cb2.getPdfWriter().getImportedPage(reader, i);
			cb2.addTemplate(page2,xy[0],xy[1]);
			try{
				schreibeKopf(cb2,i);				
			}catch(Exception ex){
				ex.printStackTrace();
			}
			stamper.setFormFlattening(true);
			stamper.close();
			PdfReader komplett = new PdfReader(bvorlage.toByteArray());
			cop.addPage(cop.getImportedPage(komplett,1));
			bvorlage.close();
		}
		

		docgesamt.close();
		reader.close();
		ktlreader.close();
		readerPage1.close();
		cop.close();
		
		return false;
	}
	
	
	private void schreibeKopf(PdfContentByte cb,int seite) throws DocumentException, IOException{
		float fy0 =  0.25f;
		float fy1 =  7.1f;

		Float[] xname = getFloats(24.f,268.f,fy0);
		Float[] xgeboren = getFloats(171.5f,268.f,fy1);
		Float[] xseite = getFloats(188.25f,278.5f,fy0);
		BaseFont bf = BaseFont.createFont(BaseFont.COURIER,BaseFont.CP1252,BaseFont.NOT_EMBEDDED); 
		cb.beginText();
		cb.moveText(xname[0], xname[1]);
		cb.setFontAndSize(bf,12);
		cb.setCharacterSpacing(xname[2]);
		cb.showText(eltern.btf[2].getText());
		cb.endText();
		cb.beginText();
		cb.moveText(xgeboren[0], xgeboren[1]);
		cb.setFontAndSize(bf,12);
		cb.setCharacterSpacing(xgeboren[2]);
		cb.showText(this.macheDatum2Sechs(eltern.btf[3].getText()));
		cb.endText();
		cb.beginText();
		cb.moveText(xseite[0], xseite[1]);
		cb.setFontAndSize(bf,11);
		cb.setCharacterSpacing(xseite[2]);
		cb.showText(new Integer(seite).toString());
		cb.endText();

	}
	private void schreibeVonBis(PdfContentByte cb,int seiten) throws DocumentException, IOException{
		Float[] xseite = getFloats(174.0f,27.0f,0.f);
		BaseFont bf = BaseFont.createFont(BaseFont.COURIER,BaseFont.CP1252,BaseFont.NOT_EMBEDDED); 
		cb.beginText();
		cb.moveText(xseite[0], xseite[1]);
		cb.setFontAndSize(bf,11);
		cb.setCharacterSpacing(xseite[2]);
		cb.showText(new Integer(seiten).toString());
		cb.endText();
	}
		

	
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
