package entlassBerichte;
import hauptFenster.Reha;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JOptionPane;

import oOorgTools.OOTools;

import org.jdesktop.swingworker.SwingWorker;

import stammDatenTools.ArztTools;
import systemEinstellungen.SystemConfig;
import systemTools.ReaderStart;
import ag.ion.bion.officelayer.filter.PDFFilter;
import ag.ion.bion.officelayer.text.ITextCursor;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextField;
import ag.ion.bion.officelayer.text.ITextFieldService;
import ag.ion.bion.officelayer.text.ITextRange;
import ag.ion.bion.officelayer.text.ITextTable;
import ag.ion.bion.officelayer.text.IViewCursor;
import ag.ion.bion.officelayer.text.TextException;
import ag.ion.noa.NOAException;
import ag.ion.noa.filter.OpenOfficeFilter;

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

import dialoge.PinPanel;
import dialoge.RehaSmartDialog;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;

public class RVEBerichtPDF {
	public EBerichtPanel eltern = null;

	public String vorlagenPfad = Reha.proghome+"vorlagen/"+Reha.aktIK+"/";
	public String[] rvVorlagen = {null,null,null,null};
	String[][] tempDateien = {null,null,null,null,null,null};
	public String tempPfad = Reha.proghome+"temp/"+Reha.aktIK+"/";
	public boolean RV;
	public boolean altesFormular;
	
	
	public RVEBerichtPDF(EBerichtPanel xeltern, boolean nurVorschau,int[] versionen,boolean xaltesFormular,boolean xRV){
		eltern = xeltern;
		RV = xRV;
		altesFormular = xaltesFormular;
		if(RV && altesFormular){
			// die alten Formulare m�ssen noch aufbereitet werden....
			rvVorlagen[0]  = vorlagenPfad+"EBericht-Seite1-Variante2.pdf";
			rvVorlagen[1]  = vorlagenPfad+"EBericht-Seite2-Variante2.pdf";
			rvVorlagen[2]  = vorlagenPfad+"EBericht-Seite3-Variante2.pdf";
			rvVorlagen[3]  = vorlagenPfad+"EBericht-Seite4-Variante2.pdf";
		}else if(RV && !altesFormular){
			rvVorlagen[0]  = vorlagenPfad+"EBericht-Seite1-Variante2.pdf";
			rvVorlagen[1]  = vorlagenPfad+"EBericht-Seite2-Variante2.pdf";
			rvVorlagen[2]  = vorlagenPfad+"EBericht-Seite3-Variante2.pdf";
			rvVorlagen[3]  = vorlagenPfad+"EBericht-Seite4-Variante2.pdf";
		}else{ // GKV
			rvVorlagen[0]  = vorlagenPfad+"GKVBericht-Seite1-Variante2.pdf";
			rvVorlagen[1]  = vorlagenPfad+"EBericht-Seite2-Variante2.pdf";
			rvVorlagen[2]  = vorlagenPfad+"GKVBericht-Seite2-Variante2.pdf";
			rvVorlagen[3]  = vorlagenPfad+"EBericht-Seite4-Variante2.pdf";
		}
		//System.out.println("Nur Vorschau = "+nurVorschau);
		if(!nurVorschau){
			//System.out.println("Drucken Kapitel 1 = "+versionen[0]);
			//System.out.println("Drucken Kapitel 2 = "+versionen[1]);
			//System.out.println("Drucken Kapitel 3 = "+versionen[2]);
			//System.out.println("Drucken Kapitel 4 = "+versionen[3]);
		}
		doRVVorschau(nurVorschau,versionen);
	}
	
	/************************
	 * 
	 * @param vorschau
	 * @param versionen
	 * @param RV
	 * 
	 */
	private void doRVVorschau(boolean vorschau,int[] versionen){
		boolean geklappt = false;
		Reha.thisClass.progressStarten(true);
		File ft = new File(tempPfad+"EBfliesstext.pdf");
		if(! ft.exists()){
			JOptionPane.showMessageDialog(null, "<html>Fließtext noch nicht aufbereitet!<br>"+
					"Wechseln sie auf den Karteireiter <b>'Fliesstext'</b> und starten Sie<br>"+
					"die Druckvorschau erneut.");
			return;
		}
		//System.out.println("Konfiguriere Seite 1");
		if(RV && !altesFormular ){
			geklappt = doSeite1Neu();			
		}else{
			geklappt = doSeite1Alt();
		}
		if(!geklappt){
			JOptionPane.showMessageDialog(null,"Fehler beim Aufbau der Seite - 1 ");
			return;
		}
		//System.out.println("Konfiguriere Seite 2"); // Nur wenn RV-Tr�ger	
		if(RV){
			geklappt = doSeite2();
			if(!geklappt){
				JOptionPane.showMessageDialog(null,"Fehler beim Aufbau der Seite - 2 ");
				return;
			}
		}
		//System.out.println("Konfiguriere Seite 3");				
		geklappt = doSeite3();
		if(!geklappt){
			JOptionPane.showMessageDialog(null,"Fehler beim Aufbau der Seite - KTL 1-2 ");
			return;
		}
		//System.out.println("Stelle Kapitel zusammen");
		//final boolean xvorschau = vorschau;
		final int[] exemplare = versionen;
		//Falls nur Vorschau**************************************/
		if(vorschau){
			//System.out.println("InDo Seitenzusammenstellen");
			geklappt = doSeitenZusammenstellen();	
			if(!geklappt){
				JOptionPane.showMessageDialog(null,"Fehler beim Zusammenstellen der Berichtseiten");
				return;
			}else{
				final String xdatei =  tempDateien[4][0];
				new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() throws Exception {
						try{
							if(!RV){
								starteGKVDruck(new int[] {1,1,1,1,1,1},true);
							}
							new ReaderStart(xdatei);
							//starteReader(xdatei);
						}catch(Exception ex){
							ex.printStackTrace();
						}
						return null;
					}
				}.execute();
			}
			Reha.thisClass.progressStarten(false);
			
			return;
		//Keine Vorschau direkt drucken***************************/	
		}else{
			if(RV){
				//System.out.println("Starte die Funktion starteRVDruck()");
				new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() throws Exception {
						try{
							starteRVDruck(exemplare);
							Reha.thisClass.progressStarten(false);
						}catch(Exception ex){
							ex.printStackTrace();
							Reha.thisClass.progressStarten(false);
						}
						return null;
					}
				}.execute();
			}else{
				new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() throws Exception {
						try{
							starteGKVDruck(exemplare,false);
							Reha.thisClass.progressStarten(false);
						}catch(Exception ex){
							ex.printStackTrace();
							Reha.thisClass.progressStarten(false);
						}
						return null;
					}
				}.execute();				
			}
		}
	}
	/****************************************************************/
	private boolean starteGKVDruck(int[] exemplare,boolean vorschau){
		try {

			doSeitenZusammenstellen();
			
			if(exemplare[3] > 0){
				doArztAuswaehlen(eltern);
				if(eltern.aerzte.length > 0){
					Reha.thisClass.progressStarten(true);
					String id = SystemConfig.hmAdrADaten.get("<Aid>");
					for(int i = 0; i < eltern.aerzte.length;i++){
						////System.out.println("***********Mach HashMap für Arzt "+eltern.aerzte[i]);
						ArztTools.constructArztHMap(eltern.aerzte[i]);
						ByteArrayOutputStream baout = new ByteArrayOutputStream();
						SystemConfig.hmEBerichtDaten.put("<Ebbeginn>",eltern.btf[15].getText() );
						SystemConfig.hmEBerichtDaten.put("<Ebende>",eltern.btf[16].getText() );
						eltern.document.getPersistenceService().store(baout);
						//eltern.document.getPersistenceService().export(baout, new RTFFilter());
						InputStream is = new ByteArrayInputStream(baout.toByteArray());
						baout.flush();
						baout.close();
						////System.out.println("Bytes available = "+is.available());
 						ITextDocument doc = OOTools.starteGKVBericht(Reha.proghome+"vorlagen/"+Reha.aktIK+"/GKVArztbericht2.ott", "");
 						Thread.sleep(100);
 						//doc.getViewCursorService().getViewCursor().getTextCursorFromStart().insertDocument( is, RTFFilter.FILTER );
 						ITextFieldService textFieldService = doc.getTextFieldService();
 						ITextField[] placeholders = null;
 						placeholders = textFieldService.getPlaceholderFields();
 						////System.out.println("Anzahl PlaceHolders = "+placeholders.length);
 						for(int  p2 = 0 ; p2 < placeholders.length ; p2++ ){
 							if(placeholders[p2].getDisplayText().toLowerCase().equals("<bblock1>")){
 	 							ITextRange range = placeholders[p2].getTextRange();
 	 							ITextCursor textCursor;
 	 							textCursor = doc.getTextService().getText().getTextCursorService().getTextCursor();
 	 							textCursor.gotoRange(range, false);
 	 							textCursor.insertDocument(is, OpenOfficeFilter.FILTER);
 	 	 						break;
 							}
 						}
 						is.close();
 						ITextTable[] tbl = doc.getTextTableService().getTextTables();
 						int itbl = -1;
 						boolean btblok = false;
 						for(int y = 0; y < tbl.length;y++){
 							//System.out.println("Tabellenname = "+tbl[y].getName());
 							if(tbl[y].getName().equals("Diagnosen")){
 								itbl = y;
 								btblok = true;
 								break;
 							}
 						}
 						if(!btblok){
 							JOptionPane.showMessageDialog(null,"Keine Tabelle mit dem Namen 'Diagnosen' gefunden.\n"+
 									"Setzen Sie die Diagnosen deshalb per copy&paste ein.");
 						}
 						for(int d = 0; d < 5; d++){
 							if(! eltern.bta[d].getText().trim().equals("")){
 	 							if(d > 0){
 	 								//System.out.println("Hänge neue Zeilen an Tabelle: "+tbl[itbl].getName());
 	 								tbl[itbl].addRow(d);
 	 							}
 								tbl[itbl].getCell(0,d).getTextService().getText().setText(Integer.toString(d+1)+".");
 								tbl[itbl].getCell(1,d).getTextService().getText().setText(eltern.bta[d].getText().trim());
 							}
 						}
 						if(exemplare[5] > 0 || vorschau){
 							String tp = tempPfad+"PrintArzt"+Integer.toString(i)+System.currentTimeMillis()+".pdf";
 							doc.getPersistenceService().export(tp, PDFFilter.FILTER);
 							String tp2 = tempPfad+"PrintArzt"+Integer.toString(i)+System.currentTimeMillis()+".odt";
 							doc.getPersistenceService().export(tp2, OpenOfficeFilter.FILTER);
 							doc.close();
 							new ReaderStart(String.valueOf(tp));
 						}else{
 	 						IViewCursor viewCursor = doc.getViewCursorService().getViewCursor();
 	 						viewCursor.getPageCursor().jumpToFirstPage();
 	 						viewCursor.getPageCursor().jumpToStartOfPage();
 	 						doc.getFrame().getXFrame().getContainerWindow().setVisible(true);
 	 						doc.getFrame().setFocus();
 						}

					}
					Reha.thisFrame.setCursor(Reha.thisClass.normalCursor);
					Reha.thisClass.progressStarten(false);
					ArztTools.constructArztHMap(id);
				}
			}
			if(vorschau){
				return true;
			}
			String tempversion = tempPfad+"Print"+System.currentTimeMillis()+".pdf";

			Document docversion = new Document(PageSize.A4);
			PdfCopy cop = new PdfCopy(docversion,new FileOutputStream(tempversion));
			
			docversion.open();
			//System.out.println(tempDateien[0][0] );
			@SuppressWarnings("unused")
			File f = new File(tempDateien[0][0]);
			//System.out.println("Die Datei = "+tempDateien[0][0]+" existiert = "+f.exists() );
			PdfReader reader = new PdfReader(tempDateien[0][0]);
			cop.addPage(cop.getImportedPage(reader,1 ));
			reader.close();
			f = new File(tempDateien[2][0]);
			//System.out.println("Die Datei = "+tempDateien[2][0]+" existiert = "+f.exists() );
			reader = new PdfReader(tempDateien[2][0]);
			int seiten = reader.getNumberOfPages();
			for(int i = 1 ; i <= seiten;i++){
				cop.addPage(cop.getImportedPage(reader,i ));
			}
			docversion.close();
			cop.close();
			reader.close();
		
			if(exemplare[5] > 0){
				new ReaderStart(tempversion );
			}else{
				druckeVersion(tempversion );							
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NOAException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ag.ion.bion.officelayer.document.DocumentException e) {
			e.printStackTrace();
		} catch (TextException e) {
			e.printStackTrace();
		}
		

		return true;
	}
	/******************************************/
	@SuppressWarnings("unused")
	private void starteReader(String datei) throws IOException{
		Process process = new ProcessBuilder(SystemConfig.hmFremdProgs.get("AcrobatReader"),"",datei).start();
		InputStream is = process.getInputStream();
		
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line;
		 Reha.thisClass.progressStarten(false);							       
       while ((line = br.readLine()) != null) {
         //System.out.println("Lade Adobe "+line);
       }
       is.close();
       isr.close();
       br.close();
	}
	/***********
	 * 
	 * E-Bericht Seite 1 - Neues Formular
	 * 
	 * 
	 * 
	 * 
	 **********/
	private boolean doSeite1Neu(){
		String pdfPfad = rvVorlagen[0];
		PdfStamper stamper = null;
			
		// Geschiss bis die bestehende PDF eingelesen und gestampt ist
		tempDateien[0] = new String[]{Reha.proghome+"temp/"+Reha.aktIK+"/EB1"+System.currentTimeMillis()+".pdf"};
		 
		BaseFont bf;
		try {
			bf = BaseFont.createFont(BaseFont.COURIER,BaseFont.CP1252,BaseFont.NOT_EMBEDDED);
		PdfReader reader = new PdfReader (pdfPfad);
		
		stamper = new PdfStamper(reader,new  FileOutputStream(tempDateien[0][0]));

		// Die Ausfertigung handeln...---				
		PdfContentByte cb = stamper.getOverContent(1);
		Float [] pos = {null,null,null};
		float fy0 =  0.25f;
		float fy1 =  6.9f;
		// Hier die Positionierung f�r das obere Ged�nse
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
		// Jetzt kommen die Felder ab station�r, ganzt�gig ambulant etc. bis letztes Feld = K�rpergr��e
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
		// Jetzt die Diagnoseschl�ssel
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
		//Hier die Ärzte
		Float[][] faerzte = {getFloats(23.25f,(23.50f-5.90f),fy0),
				getFloats(84.50f,(23.50f-5.90f),fy0),
				getFloats(145.00f,(23.50f-5.90f),fy0)};
		setzeText(cb,faerzte[0][0], faerzte[0][1],faerzte[0][2],bf,9,eltern.barzttf[0].getText());
		setzeText(cb,faerzte[1][0], faerzte[1][1],faerzte[1][2],bf,9,eltern.barzttf[1].getText());
		setzeText(cb,faerzte[2][0], faerzte[2][1],faerzte[2][2],bf,9,eltern.barzttf[2].getText());
		
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
	 * E-Bericht Seite 1 - Neues Formular
	 * 
	 * 
	 * 
	 * 
	 **********/
	private boolean doSeite1Alt(){
		String pdfPfad = rvVorlagen[0];
		PdfStamper stamper = null;
			
		// Geschiss bis die bestehende PDF eingelesen und gestampt ist
		tempDateien[0] = new String[]{Reha.proghome+"temp/"+Reha.aktIK+"/EB1"+System.currentTimeMillis()+".pdf"};
		 
		BaseFont bf;
		try {
			bf = BaseFont.createFont(BaseFont.COURIER,BaseFont.CP1252,BaseFont.NOT_EMBEDDED);
		PdfReader reader = new PdfReader (pdfPfad);
		
		stamper = new PdfStamper(reader,new  FileOutputStream(tempDateien[0][0]));

		// Die Ausfertigung handeln...---				
		PdfContentByte cb = stamper.getOverContent(1);
		Float [] pos = {null,null,null};
		float fy0 =  0.25f;
		float fy1 =  6.9f;
		// Hier die Positionierung f�r das obere Ged�nse
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
		// Jetzt kommen die Felder ab station�r, ganzt�gig ambulant etc. bis letztes Feld = K�rpergr��e
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
		// Jetzt die Diagnoseschl�ssel
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
				{getFloats(33.9f+xfs,81.75f+yfs,fy0),getFloats(61.8f+xfs,81.75f+yfs,fy0),
			//	          2                          3
				 getFloats(33.9f+xfs,73.7f+yfs,fy0),getFloats(61.8f+xfs,73.7f+yfs,fy0),
			//            4                          5                       
				 getFloats(33.9f+xfs,65.4f+yfs,fy0),getFloats(61.8f+xfs,65.4f+yfs,fy0),
			//			  6                          7                                         
				 getFloats(89.85f+xfs,81.75f+yfs,fy0),getFloats(120.35f+xfs,81.75f+yfs,fy0),
			//	          8							 9  
				 getFloats(89.85f+xfs,73.7f+yfs,fy0),getFloats(120.35f+xfs,73.7f+yfs,fy0),
			//	         10                         11               
				 getFloats(89.85f+xfs,65.4f+yfs,fy0),getFloats(120.35f+xfs,65.4f+yfs,fy0),
			//	         12                         13                                                
				getFloats(148.25f+xfs,81.75f+yfs,fy0),getFloats(178.45f+xfs,81.75f+yfs,fy0),
			//	         14                         15
				getFloats(148.25f+xfs,73.7f+yfs,fy0),getFloats(178.45f+xfs,73.7f+yfs,fy0),
			//           16
				getFloats(148.25f+xfs,65.4f+yfs,fy0)};
		for(int i = 0; i <17; i++){
			if(eltern.bchb[i].isSelected()){
				text = "X";
				setzeText(cb,poswert4[i][0], poswert4[i][1],poswert4[i][2],bf,14,text);
			}
		}
		bf = BaseFont.createFont(BaseFont.COURIER,BaseFont.CP1252,BaseFont.NOT_EMBEDDED);
		/*************************************************************************/
		// Gewichte und K�rpergr��e             1                              2                            3
		Float[][] poswert5 = {getFloats(29.25f,116.50f,fy1),getFloats(29.25f,108.25f,fy1),getFloats(29.25f,99.80f,fy1) }; 
		for(int i = 22; i < 25;i++){
			text = eltern.btf[i].getText().trim();
			setzeText(cb,poswert5[i-22][0], poswert5[i-22][1],poswert5[i-22][2],bf,12,text);						
		}
		/*************************************************************************/
		// Das Unterschriftsdatum = eltern.btf[27].getText() + den Ort
		text = eltern.btf[27].getText().trim();
		Float [] fsign = getFloats(24.25f,16.25f,fy0);
		if(! text.equals(".  .")){
			setzeText(cb,fsign[0], fsign[1],fsign[2],bf,10,SystemConfig.sGutachtenOrt+", "+text);						
		}
		//Float [] fort = getFloats(61.25f,27.60f,fy0);
		//setzeText(cb,fort[0], fort[1],fort[2],bf,12,SystemConfig.sGutachtenOrt);
		/*************************************************************************/
		// Jetzt die ComboBoxen abarbeiten
		Float[][] poswert6 = {
			//   0=Enlassform                      1	
			getFloats(118.15f,213.65f,fy1),getFloats(166.40f,213.65f,fy1),
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
			getFloats(77.65f,116.50f,fy1),getFloats(120.70f,116.50f,fy1)
			
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
		// Erl�uternungen Box
		Float[] empfunten =  getFloats(24.50f,51.25f,0.5f);
		Float[] empfoben =  getFloats(201.00f,58.f,0.5f);
		ct = new ColumnText(cb);
		ct.setSimpleColumn(empfunten[0], empfunten[1],empfoben[0],empfoben[1],9,Element.ALIGN_BASELINE);
		ph = new Phrase();
		ph.setFont(FontFactory.getFont("Courier",10,Font.PLAIN));
		ph.add(eltern.bta[5].getText().trim());
		ct.addText(ph);
		ct.go();
		// Letzte Medikation
		empfunten =  getFloats(24.50f,26.5f,0.5f);
		empfoben =  getFloats(201.00f,39.f,0.5f);
		ct = new ColumnText(cb);
		ct.setSimpleColumn(empfunten[0], empfunten[1],empfoben[0],empfoben[1],9,Element.ALIGN_BASELINE);
		ph = new Phrase();
		ph.setFont(FontFactory.getFont("Courier",10,Font.PLAIN));
		ph.add(eltern.bta[6].getText().trim());
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
			cb.showText(Integer.toString(seite));
			cb.endText();


			/* F�r �ltere Berichte < 01.01.2008 noch den Berufsklassenschl�ssel einbauen
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
				if(RV){
					cb.beginText();
					cb.moveText(xseite[0], xseite[1]);
					cb.setFontAndSize(bf,11);
					cb.setCharacterSpacing(xseite[2]);
					cb.showText(Integer.toString(seite));
					cb.endText();
				}
				
				//                    0x-start,  1y-start,  2h�he  3y-ende  4xCode   5xDauer  6xAnzahl
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
				// ab hier das Stamper und Copy Ged�nse....				
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
		//                    0x-start,  1y-start,  2h�he  3y-ende  4xCode   5xDauer  6xAnzahl
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
		//float zaehler = 1.f;
 
		//                     x-start,  y-start,  h�he   x-ende
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
	public boolean	starteRVDruck(int[] exemplare){
		try{
			
			int seiten = 0;
			
			String[] empfs = {"den RV-Träger - "+(String) eltern.cbktraeger.getSelectedItem(),
					"den RV-Träger - "+(String) eltern.cbktraeger.getSelectedItem(),
					"den behandelnden Arzt","die Rehabilitationseinrichtung","die Krankenkasse"};
			String[] bereich = {"Bereich Reha","Bereich EDV","","",""};
			// ZU diesem Zeitpunkt sind Bereits alle Seiten aufbereitet.
			//Falls der Fliestext ebenfalls gedruck werden soll;
			//System.out.println("Kapitel Fliesstext erforderlich");
			if(exemplare[3] >= 0){
				//System.out.println("starte Kapitel Fliesstext");
				seiten = doKapitelFiesstext();
				//System.out.println("Es wurden "+seiten+" Fliesstext zusammengestellt");
			}
			for(int empfaenger = 0; empfaenger < 5; empfaenger++){
				//System.out.println("in der Empf�ngerroutine");
				druckeEmpfaengerVersion(empfaenger,empfs[empfaenger],bereich[empfaenger],(seiten > 0 ? true : false),seiten,exemplare);
				if(exemplare[4] <= 0){
					return true;
				}
			}
		}catch(IOException ex1){
			ex1.printStackTrace();
			return false;
		}catch(DocumentException ex2){
			ex2.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**************************************
	 * 
	 * 
	 * 
	 * @param empfaenger
	 * @param empftext
	 * @param empfbereich
	 * @param mitfliesstext
	 * @param seitenfliesstext
	 * @param blatt
	 */
	private void druckeEmpfaengerVersion(int empfaenger,String empftext,String empfbereich,boolean mitfliesstext,int seitenfliesstext,int[] blatt){
		String tempversion = "";
		try {
			tempversion = tempPfad+"Print"+System.currentTimeMillis()+".pdf";
			Document docversion = new Document(PageSize.A4);
			ByteArrayOutputStream baout = null;
			//PdfImportedPage pageImport;
			PdfCopy cop = new PdfCopy(docversion,new FileOutputStream(tempversion));
			docversion.open();
			
				if(blatt[0]> 0 ){
					PdfReader rvorlage = new PdfReader(tempDateien[0][0]);
					baout = new ByteArrayOutputStream();
					PdfStamper stamper = new PdfStamper(rvorlage,baout);
					PdfContentByte cb1 = stamper.getOverContent(1);
					if(mitfliesstext){
						schreibeVonBis(cb1,Integer.toString(seitenfliesstext));
					}else{
						schreibeVonBis(cb1,"-");						
					}
					schreibeEmpfaenger(cb1,empftext,empfbereich);
					stamper.setFormFlattening(true);
					stamper.close();
					PdfReader seitefertig = new PdfReader(baout.toByteArray());
					cop.addPage(cop.getImportedPage(seitefertig,1));
					seitefertig.close();
					rvorlage.close();
					baout.close();
					if(empfaenger==4){
						docversion.close();
						cop.close();
						if(blatt[5] > 0){
							new ReaderStart(String.valueOf(tempversion));
							//starteReader(String.valueOf(tempversion));
						}else{
							druckeVersion(String.valueOf(tempversion));							
						}
						return;
					}
				}
				if(blatt[1]> 0 ){
					macheGedoense(cop,1,empftext,empfbereich);
				}
				if(blatt[2]> 0){
					PdfReader rvorlage = new PdfReader(tempDateien[2][0]);
					int ktls = rvorlage.getNumberOfPages();
					for(int k = 1; k <= ktls;k++){
						cop.addPage(cop.getImportedPage(rvorlage, k));
					}
					rvorlage.close();
				}
				if(blatt[3]> 0 && empfaenger != 1){
					PdfReader rvorlage = new PdfReader(tempDateien[5][0]);
					int ktls = rvorlage.getNumberOfPages();
					for(int k = 1; k <= ktls;k++){
						cop.addPage(cop.getImportedPage(rvorlage, k));
					}
					rvorlage.close();
				}
				docversion.close();
				cop.close();
				Thread.sleep(50);
				if(blatt[5] > 0){
					new ReaderStart(tempversion);
					//starteReader(String.valueOf(tempversion));
				}else{
					druckeVersion(tempversion);							
				}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
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
		
		
		/*
		new Thread(){
			public void run(){
				try {
					Runtime.getRuntime().exec(xcmd);
					//System.out.println(xcmd);
					Reha.thisClass.progressStarten(false);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
		*/
		

	}
	private void macheGedoense(PdfCopy cop,int kapitel,String empftext,String empfbereich){
		ByteArrayOutputStream baout = null;
		//PdfImportedPage pageImport;
		PdfReader rvorlage;
		try {
			rvorlage = new PdfReader(tempDateien[kapitel][0]);
			baout = new ByteArrayOutputStream();
			PdfStamper stamper = new PdfStamper(rvorlage,baout);
			PdfContentByte cb1 = stamper.getOverContent(1);
			schreibeEmpfaenger(cb1,empftext,empfbereich);
			stamper.setFormFlattening(true);
			stamper.close();
			PdfReader seitefertig = new PdfReader(baout.toByteArray());
			cop.addPage(cop.getImportedPage(seitefertig,1));
			seitefertig.close();
			rvorlage.close();
			baout.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	private int doKapitelFiesstext() throws IOException, DocumentException{
		int seiten;
		//Dokument f�r nur Fliestext erstellen
		Document docnurfliesstext = new Document(PageSize.A4);
		//Reader f�r Fliesstext erstellen
		PdfReader readerflt = new PdfReader(tempPfad+"EBfliesstext.pdf");
		seiten = readerflt.getNumberOfPages();
		PdfImportedPage pageImport;
		//Reader f�r Zusammengef�hrten Fliesstext erstellen
		tempDateien[5] = new String[]{tempPfad+"EBGesFlies"+System.currentTimeMillis()+".pdf"};
		PdfCopy cop = new PdfCopy(docnurfliesstext,new FileOutputStream(tempDateien[5][0]));
		docnurfliesstext.open();

		ByteArrayOutputStream bvorlage;
		Float[] xy = getFloats(17.f,13.f,0.f);
		for(int i = 1; i <= seiten; i++){
			//PdfReader rvorlage = new PdfReader(vorlage);
			PdfReader rvorlage = new PdfReader(rvVorlagen[3]);
			bvorlage = new ByteArrayOutputStream();
			PdfStamper stamper = new PdfStamper(rvorlage,bvorlage);
			PdfContentByte cb2 = stamper.getOverContent(1);
			pageImport = cb2.getPdfWriter().getImportedPage(readerflt, i);
			cb2.addTemplate(pageImport,xy[0],xy[1]);
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
			rvorlage.close();
			komplett.close();
			bvorlage.close();
		}
		docnurfliesstext.close();
		readerflt.close();
		cop.close();
		
		//System.out.println(Integer.toString(seiten)+" Seiten Flie�text wurden zusammengestellt");	
		return seiten;
	}
	public boolean doSeitenZusammenstellen(){
		try{
		//InputStream isb = null;
		tempDateien[3] = new String[]{tempPfad+"EB4"+System.currentTimeMillis()+".pdf"};
		String pdfPfad = rvVorlagen[3];
		Document docgesamt = new Document(PageSize.A4);
		//File ft = new File(tempPfad+"EBfliesstext.pdf");
		PdfReader reader = new PdfReader(tempPfad+"EBfliesstext.pdf");

		int seiten = reader.getNumberOfPages();
		//System.out.println("Insgesamt Seiten Flie�text = "+seiten);
	
		PdfImportedPage page2;		
		
		tempDateien[4] = new String[]{tempPfad+"EBGesamt"+System.currentTimeMillis()+".pdf"};
		PdfCopy cop = new PdfCopy(docgesamt,new FileOutputStream(tempDateien[4][0]));
		docgesamt.open();
		
		ByteArrayOutputStream bpage1 = new ByteArrayOutputStream();
		if(RV){ // Seitenzahl recht unten nur im Falle der RV
			PdfReader readerPage1 = new PdfReader (tempDateien[0][0]);
			PdfStamper stampPage1 = new PdfStamper(readerPage1,bpage1);
			PdfContentByte cbPage1 = stampPage1.getOverContent(1);
			schreibeVonBis(cbPage1,Integer.toString(seiten));
			stampPage1.close();
			cop.addPage(cop.getImportedPage(new PdfReader(bpage1.toByteArray()),1));
			readerPage1.close();
			//Nur Bei RV die Seite Sozialmedizin
			cop.addPage(cop.getImportedPage(new PdfReader(tempDateien[1][0]),1));
		}else{
			PdfReader readerPage1 = new PdfReader (tempDateien[0][0]);
			cop.addPage(cop.getImportedPage(readerPage1,1));
			readerPage1.close();
		}
		PdfReader ktlreader = new PdfReader(tempDateien[2][0]);
		int ktlseiten = ktlreader.getNumberOfPages();
		for(int i = 1; i <= ktlseiten;i++){
			cop.addPage(cop.getImportedPage(ktlreader,i));
		}
		if(!RV){
			docgesamt.close();
			reader.close();
			ktlreader.close();
			cop.close();
			return true;
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
		cop.close();
		}catch(Exception ex){
			ex.printStackTrace();
			return false;
		}
		
		return true;
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
		cb.showText(Integer.toString(seite));
		cb.endText();

	}
	private void schreibeEmpfaenger(PdfContentByte cb,String empfaenger,String bereich) throws DocumentException, IOException{
		float fy0 =  0.25f;
		//float fy1 =  7.1f;
		//xx
		Float[] xempfaenger = getFloats(56.5f,277.60f,fy0);
		Float[] xbereich = getFloats(155.5f,277.60f,fy0);

		BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA_BOLD,BaseFont.CP1252,BaseFont.NOT_EMBEDDED); 
		cb.beginText();
		cb.moveText(xempfaenger[0], xempfaenger[1]);
		cb.setFontAndSize(bf,9.5f);
		//cb.setCharacterSpacing(xempfaenger[2]);
		cb.showText("Ausfertigung für "+empfaenger);
		cb.endText();
		cb.beginText();
		cb.moveText(xbereich[0], xbereich[1]);
		cb.setFontAndSize(bf,9.5f);
		//cb.setCharacterSpacing(xbereich[2]);
		cb.showText(bereich);
		cb.endText();
	}
	private void schreibeVonBis(PdfContentByte cb,String seiten) throws DocumentException, IOException{
		Float[] xseite;
		if(!altesFormular){
			xseite = getFloats(174.0f,27.0f,0.f);			
		}else{
			xseite = getFloats(196.0f,22.0f,0.f);
		}
		BaseFont bf = BaseFont.createFont(BaseFont.COURIER,BaseFont.CP1252,BaseFont.NOT_EMBEDDED); 
		cb.beginText();
		cb.moveText(xseite[0], xseite[1]);
		cb.setFontAndSize(bf,11);
		cb.setCharacterSpacing(xseite[2]);
		cb.showText(seiten);
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
	private void doArztAuswaehlen(EBerichtPanel eltern){
		try{
			//System.out.println("Starte Arztauswahl");
			String titel = "Aerzte für GKV-Bericht auswählen";
			EBPrintDlg printDlg = new EBPrintDlg();
			//JDialog neuPat = new JDialog();
			PinPanel pinPanel = new PinPanel();
			pinPanel.setName("EBPrint");
			pinPanel.getGruen().setVisible(false);
			printDlg.setPinPanel(pinPanel);
			printDlg.getSmartTitledPanel().setTitle(titel);	
			printDlg.setSize(650,250);
			printDlg.setPreferredSize(new Dimension(650,250));
			printDlg.getSmartTitledPanel().setPreferredSize(new Dimension (650,250));
			printDlg.setPinPanel(pinPanel);
			//Hier das Versionsged�nse
			printDlg.getSmartTitledPanel().setContentContainer(new BerichtArztAuswahl(eltern));
			printDlg.getSmartTitledPanel().getContentContainer().setName("EBPrint");
			printDlg.setName("EBPrint");
			
			//printDlg.setLocation(p.x-100,p.y+35);
			printDlg.setLocationRelativeTo(null);
			printDlg.setTitle(titel);

			printDlg.setModal(true);
			printDlg.pack();	
			printDlg.setVisible(true);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		
	}
	
}

class EBAerzteDlg extends RehaSmartDialog implements RehaTPEventListener,WindowListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2637367692780850096L;
	private RehaTPEventClass rtp = null;
	public EBAerzteDlg(){
		super(null,"EBPrint");
		this.setName("EBPrint");
		rtp = new RehaTPEventClass();
		rtp.addRehaTPEventListener((RehaTPEventListener) this);

	}
	public void rehaTPEventOccurred(RehaTPEvent evt) {
		// TODO Auto-generated method stub
		try{
			if(evt.getDetails()[0] != null){
				if(evt.getDetails()[0].equals(this.getName())){
					this.setVisible(false);
					rtp.removeRehaTPEventListener((RehaTPEventListener) this);
					rtp = null;
					this.dispose();
					//System.out.println("****************EGPrint -> Listener entfernt**************");				
				}
			}
		}catch(NullPointerException ne){
			//System.out.println("In PatNeuanlage" +evt);
		}
	}
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		if(rtp != null){
			this.setVisible(false);			
			rtp.removeRehaTPEventListener((RehaTPEventListener) this);		
			rtp = null;
			dispose();
			//System.out.println("****************EGPrint -> Listener entfernt (Closed)**********");
		}
		
		
	}
}	
