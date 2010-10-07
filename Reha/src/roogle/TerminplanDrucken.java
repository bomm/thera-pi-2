package roogle;

import hauptFenster.Reha;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JOptionPane;

import systemEinstellungen.SystemConfig;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.DocumentException;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.filter.PDFFilter;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextField;
import ag.ion.bion.officelayer.text.ITextFieldService;
import ag.ion.bion.officelayer.text.ITextTable;
import ag.ion.bion.officelayer.text.TextException;
import ag.ion.noa.NOAException;
import ag.ion.noa.printing.IPrinter;
import emailHandling.EmailSendenExtern;





public class TerminplanDrucken extends Thread implements Runnable {
private Vector<TermObjekt> termindat = null;
private boolean ldrucken; 
private String patient;
private String rezept;
public int  seiten = 1;
TerminplanDrucken thisDruck = null;
String[] tabName; 
static String exporturl = "";
SuchenSeite eltern;
	public void init(Vector<TermObjekt> termdat,boolean drucken,String patName,String rezNr,SuchenSeite xeltern){
		this.termindat = termdat;
		this.ldrucken = drucken;
		this.patient = patName;
		this.rezept = rezNr;
		eltern = xeltern;
		thisDruck = this;
		start();
	}
	
	public void run() {
			String url = Reha.proghome+"vorlagen/"+Reha.aktIK+"/"+SystemConfig.oTerminListe.NameTemplate;
			//String url = Reha.proghome+"vorlagen/"+SystemConfig.oTerminListe.NameTemplate; 
			////System.out.println("***************URL = "+url+"****************");
			String terminDrucker = SystemConfig.oTerminListe.NameTerminDrucker;
			int anzahl = termindat.size();
			int AnzahlTabellen = SystemConfig.oTerminListe.AnzahlTerminTabellen;
			int maxTermineProTabelle = SystemConfig.oTerminListe.AnzahlTermineProTabelle;
			int maxTermineProSeite = AnzahlTabellen * maxTermineProTabelle;
			//int spaltenProtabelle = SystemConfig.oTerminListe.AnzahlSpaltenProTabellen;
			Vector<String> spaltenNamen = SystemConfig.oTerminListe.NamenSpalten;
			int ipatdrucken = SystemConfig.oTerminListe.PatNameDrucken;
			int iheader = SystemConfig.oTerminListe.MitUeberschrift;
			//String patplatzhalter = SystemConfig.oTerminListe.PatNamenPlatzhalter;

			//int anzahl = oOTermine.size();
			String patname = (patient.indexOf("?")>=0 ? patient.substring(1).trim() : patient.trim());
			String rez = (rezept.trim().equals("") ? "" : " - "+rezept.trim());
	        patname = patname+rez;
			IDocumentService documentService = null;;
			if(!Reha.officeapplication.isActive()){
				Reha.starteOfficeApplication();
			}
			try{
				documentService = Reha.officeapplication.getDocumentService();

			} catch (OfficeApplicationException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,"Fehler im OpenOffice-System, Terminplan kann nicht gedruckt werden");
			}
			
	        IDocumentDescriptor docdescript = new DocumentDescriptor();
	        docdescript.setHidden(true);
	        docdescript.setAsTemplate(true);
			IDocument document = null;
			ITextTable[] tbl = null;

			try {
				document = documentService.loadDocument(url,docdescript);

			} catch (NOAException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/**********************/
			ITextDocument textDocument = (ITextDocument)document;
			tbl = textDocument.getTextTableService().getTextTables();

			if(tbl.length != AnzahlTabellen){
				JOptionPane.showMessageDialog (null, "Anzahl Tabellen stimmt nicht mit der Vorlagen.ini �berein.\nDruck nicht m�glich");
				textDocument.close();
				eltern.cursorWait(false);
				return;
			}
			tabName = new String[AnzahlTabellen];
			int x = 0;
			for(int i=AnzahlTabellen;i>0;i--){
				tabName[x] = tbl[(tbl.length-1)-x].getName(); 
				////System.out.println(tabName[x]);
				x++;
			}
			/*********************/

			//Aktuellen Drucker ermitteln
			String druckerName = null;
			try {
				druckerName = textDocument.getPrintService().getActivePrinter().getName();
			} catch (NOAException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//Wenn nicht gleich wie in der INI angegeben -> Drucker wechseln
			IPrinter iprint = null;
			if(! druckerName.equals(terminDrucker)){
				try {
					iprint = (IPrinter) textDocument.getPrintService().createPrinter(terminDrucker);
				} catch (NOAException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					textDocument.getPrintService().setActivePrinter(iprint);
				} catch (NOAException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//Jetzt den Platzhalter ^Name^ suchen
			//SearchDescriptor searchDescriptor = null;
			//ISearchResult searchResult = null;
			if(ipatdrucken  > 0){
		      ITextFieldService textFieldService = textDocument.getTextFieldService();
		      ITextField[] placeholders = null;
				try {
					placeholders = textFieldService.getPlaceholderFields();
				} catch (TextException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				for (int i = 0; i < placeholders.length; i++) {
					String placeholderDisplayText = placeholders[i].getDisplayText();
					////System.out.println("Platzhalter-Name = "+placeholderDisplayText);
					if(placeholderDisplayText.equals("<^Name^>")){
						placeholders[i].getTextRange().setText(patname);
					}	
				}
			      
			}
			/********************************************/
			//int zeile = 0;
			//int startTabelle = 0;
			int aktTabelle = 0;
			int aktTermin = -1;
			int aktTerminInTabelle = -1;
			String druckDatum = "";
			ITextTable textTable = null;
			try {
				textTable = textDocument.getTextTableService().getTextTable(tabName[aktTabelle]);
			} catch (TextException e) {
				e.printStackTrace();
			}
			while(true){
				aktTerminInTabelle = aktTerminInTabelle+1;
				aktTermin = aktTermin+1;
				
				if(aktTermin >= anzahl){
					break;
				}
				/***********Wenn die Spalte voll ist und die aktuelle Tabelle nicht die letzte ist*/
				if(aktTerminInTabelle >= maxTermineProTabelle && aktTabelle < AnzahlTabellen-1  ){
					aktTabelle = aktTabelle+1;
					try {
						textTable = textDocument.getTextTableService().getTextTable(tabName[aktTabelle]);
					} catch (TextException e) {
						e.printStackTrace();
					}
					aktTerminInTabelle = 0;
					////System.out.println("Spaltenwechsel nach Spalte"+aktTabelle);
				}

				/************Wenn die aktuelle Seite voll ist******************/
				if(aktTermin >= maxTermineProSeite && aktTerminInTabelle==maxTermineProTabelle){

					textDocument.getViewCursorService().getViewCursor().getPageCursor().jumpToEndOfPage();
					try {
						textDocument.getViewCursorService().getViewCursor().getTextCursorFromEnd().insertPageBreak();
						textDocument.getViewCursorService().getViewCursor().getTextCursorFromEnd().insertDocument(url) ;
					} catch (NOAException e) {
						e.printStackTrace();
					}
					tbl = textDocument.getTextTableService().getTextTables();
					x = 0;
					for(int i=AnzahlTabellen;i>0;i--){
						tabName[x] = tbl[(tbl.length-1)-x].getName(); 
						x++;
					}
					
					if(ipatdrucken  > 0){
					      ITextFieldService textFieldService = textDocument.getTextFieldService();
					      ITextField[] placeholders = null;
							try {
								placeholders = textFieldService.getPlaceholderFields();
							} catch (TextException e) {
								e.printStackTrace();
							}
							for (int i = 0; i < placeholders.length; i++) {
								String placeholderDisplayText = placeholders[i].getDisplayText();
								if(placeholderDisplayText.equals("<^Name^>")){
									placeholders[i].getTextRange().setText(patname);
								}	
							}
					      
					}

					aktTabelle = 0;
					aktTerminInTabelle = 0;

					try {
						textTable = textDocument.getTextTableService().getTextTable(tabName[aktTabelle]);
					} catch (TextException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				/********************/
				//**************/Hier die Zellen*************//
				if(spaltenNamen.contains("Wochentag")){
					int zelle = spaltenNamen.indexOf("Wochentag");
					
					druckDatum = termindat.get(aktTermin).tag;
					if(aktTerminInTabelle > 0){
						if(! druckDatum.equals(termindat.get(aktTermin-1).tag)){
							try {
								textTable.getCell(zelle,aktTerminInTabelle+iheader).getTextService().getText().setText(druckDatum.substring(0,2) );
							} catch (TextException e) {
								e.printStackTrace();
							}					
						}
					}else{
						try {
							textTable.getCell(zelle,aktTerminInTabelle+iheader).getTextService().getText().setText(druckDatum.substring(0,2) );
						} catch (TextException e) {
							e.printStackTrace();
						}
					}
				}
				try {
					if(spaltenNamen.indexOf("Datum") > 0){
						int zelle = spaltenNamen.indexOf("Datum");
						textTable.getCell(zelle,aktTerminInTabelle+iheader).getTextService().getText().setText(druckDatum.substring(3) );
					}
					if(spaltenNamen.indexOf("Uhrzeit") > 0){
						int zelle = spaltenNamen.indexOf("Uhrzeit");
						textTable.getCell(zelle,aktTerminInTabelle+iheader).getTextService().getText().setText(termindat.get(aktTermin).beginn);
					}
					if(spaltenNamen.indexOf("Behandler") > 0){
						int zelle = spaltenNamen.indexOf("Behandler");						
						textTable.getCell(zelle,aktTerminInTabelle+iheader).getTextService().getText().setText(termindat.get(aktTermin).termtext);
					}

				} catch (TextException e) {
					e.printStackTrace();
				}
				
				/********************/
			}
			// Jetzt das fertige Dokument drucken, bzw. als PDF aufbereiten;
			
			/********************************************/			
			if (ldrucken){
				
				try {
						if(SystemConfig.oTerminListe.DirektDruck){
							textDocument.print();
							textDocument.close();
							this.termindat = null;
							
							eltern.cursorWait(false);
						}else{
							eltern.cursorWait(false);
							document.getFrame().getXFrame().getContainerWindow().setVisible(true);
							this.termindat = null;
						}
						
				} catch (DocumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				exporturl = Reha.proghome+"temp/"+Reha.aktIK+"/Terminplan.pdf";
				try {
					textDocument.getPersistenceService().export(exporturl, new PDFFilter());
				} catch (DocumentException e) {
					e.printStackTrace();
				}			
			}
			// Anschließend die Vorlagendatei schließen
			//textDocument.close();
			if(!ldrucken){
				textDocument.close();
				sendeEmail();
				this.termindat = null;
			}
	
			eltern.cursorWait(false);
		}
	
	private void sendeEmail(){
		String emailaddy=null,pat_intern=null;
		if(this.rezept.trim().equals("")){
			emailaddy = JOptionPane.showInputDialog (null, "Bitte geben Sie eine g�ltige Email-Adresse ein");
			try{
				if(emailaddy.equals("")){
					return;
				}
			}catch(java.lang.NullPointerException ex){
				return;
			}
		}else{
			String trailing = null;
			if(this.rezept.trim().contains("\\")){
				trailing = this.rezept.substring(0,this.rezept.indexOf("\\"));
			}else{
				trailing = this.rezept.trim();
			}
			pat_intern = holeAusDB("select PAT_INTERN from verordn where REZ_NR ='"+trailing+"'");
			if(pat_intern.equals("")){
				emailaddy = JOptionPane.showInputDialog (null, "Bitte geben Sie eine g�ltige Email-Adresse ein");
				try{
					if(emailaddy.equals("")){
						return;
					}
				}catch(java.lang.NullPointerException ex){
					return;
				}
			}else{
				emailaddy = holeAusDB("select EMAILA from pat5 where PAT_INTERN ='"+pat_intern+"'");
				if(emailaddy.equals("")){
					emailaddy = JOptionPane.showInputDialog(null,"Bitte geben Sie eine g�ltige Email-Adresse ein" , emailaddy);
					try{
						if(emailaddy.equals("")){
						return;
						}
					}catch(java.lang.NullPointerException ex){
						return;
					}
				}else{
					emailaddy = JOptionPane.showInputDialog(null,"Soll diese Emailadresse verwendet werden?" , emailaddy); 
				}
			}
		}
		/*****************bis hierher lediglich Emailadressen gewurschtel**************************/
		ArrayList<String[]> attachments = new ArrayList<String[]>();		
		String[] anhang = {null,null};
		anhang[0] = Reha.proghome+"temp/"+Reha.aktIK+"/Terminplan.pdf";
		anhang[1] = "Terminplan.pdf";
		attachments.add(anhang.clone());
		
		String username = SystemConfig.hmEmailExtern.get("Username");
		String password = SystemConfig.hmEmailExtern.get("Password");
		String senderAddress =SystemConfig.hmEmailExtern.get("SenderAdresse");
		String recipientsAddress = emailaddy;
		String subject = "Ihre Behandlungstermine";
		boolean authx = (SystemConfig.hmEmailExtern.get("SmtpAuth").equals("0") ? false : true);
		boolean bestaetigen = (SystemConfig.hmEmailExtern.get("Bestaetigen").equals("0") ? false : true);
		String text = "";
		/*********/
		 File file = new File(Reha.proghome+"vorlagen/"+Reha.aktIK+"/EmailTerminliste.txt");
	      try {
	         // FileReader zum Lesen aus Datei
	         FileReader fr = new FileReader(file);
	         // Der String, der am Ende ausgegeben wird
	         String gelesen;
	         // char-Array als Puffer fuer das Lesen. Die
	         // Laenge ergibt sich aus der Groesse der Datei
	         char[] temp = new char[(int) file.length()];
	         // Lesevorgang
	         fr.read(temp);
	         // Umwandlung des char-Arrays in einen String
	         gelesen = String.valueOf(temp);
	         text = gelesen;
	         //Ausgabe des Strings
	         ////System.out.println(gelesen);
	         // Ressourcen freigeben
	         fr.close();
	      } catch (FileNotFoundException e1) {
	         // die Datei existiert nicht
	         System.err.println("Datei nicht gefunden: ");
	      } catch (IOException e2) {
	         // andere IOExceptions abfangen.
	         e2.printStackTrace();
	      }
		/*********/
	      if (text.equals("")){
	    	  text = "Sehr geehrte Damen und Herren,\n"+
					"im Dateianhang finden Sie die von Ihnen gew�nschten Behandlungstermine.\n\n"+
					"Termine die Sie nicht einhalten bzw. wahrnehmen k�nnen, m��en 24 Stunden vorher\n"+
					"abgesagt werden.\n\nIhr Planungs-Team vom RTA";
	      }
		String smtpHost = SystemConfig.hmEmailExtern.get("SmtpHost");
		
		EmailSendenExtern oMail = new EmailSendenExtern();
		try{
		oMail.sendMail(smtpHost, username, password, senderAddress, recipientsAddress, subject, text,attachments,authx,bestaetigen);
		oMail = null;
		eltern.cursorWait(false);
		JOptionPane.showMessageDialog (null, "Die Terminliste wurde aufbereitet und per Email versandt\n");
		}catch(Exception e){
			eltern.cursorWait(false);
			JOptionPane.showMessageDialog (null, "Der Emailversand der Terminliste ist fehlgeschlagen!!!!!\n");
			e.printStackTrace( );
		}
		
		
			
		
	}
	private String holeAusDB(String exStatement){
		Statement stmt = null;
		ResultSet rs = null;
		String sergebnis = "";
		try {
			stmt = (Statement) Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE );
			try{
				rs = (ResultSet) stmt.executeQuery(exStatement);		
				while(rs.next()){
					sergebnis = (rs.getString(1) == null ? "" : rs.getString(1));
				}
			}catch(SQLException ev){
        		//System.out.println("SQLException: " + ev.getMessage());
        		//System.out.println("SQLState: " + ev.getSQLState());
        		//System.out.println("VendorError: " + ev.getErrorCode());
			}	

		}catch(SQLException ex) {
			//System.out.println("von stmt -SQLState: " + ex.getSQLState());
		}

		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) { // ignore }
					rs = null;
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) { // ignore }
					stmt = null;
				}
			}

		}
		return sergebnis;
	}
	        
	}


