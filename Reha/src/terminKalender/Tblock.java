package terminKalender;

import hauptFenster.Reha;

import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JOptionPane;

import systemEinstellungen.SystemConfig;
import systemTools.StringTools;



class Tblock {

private String[] terminDat;	
private int Spalte;
private int Block;
private int Kollege;
private boolean fehler;
private int result;
private String Name;
private String Nummer;
private String Beginn;
private int Dauer;
private String Ende;
private Vector vect = new Vector();
private Felder feld;
private int iName,iNummer,iBeginn,iDauer,iEnde,iGesamt;
private int AnzahlOrigBloecke;
private int aktBlockNeu;	
private TerminFenster Eltern;
private String[] aktDatum;
private int dbBehandler;
	public int TblockInit(TerminFenster parent,String[] tDaten,int iSpalte, int iBlock, int iKollege,Vector vterm,String[] datum,int iKoll){

		int i;
		this.iName=0;
		this.iNummer=1;
		this.iBeginn=2;
		this.iDauer=3;
		this.iEnde=4;
		this.iGesamt=5;
		
		this.aktDatum = datum;
		
		this.terminDat = tDaten;
		this.Spalte = iSpalte;
		this.Block = iBlock;
		
		this.Kollege = iKollege;
		
		this.dbBehandler = iKoll;
		
		this.Name = tDaten[0];
		this.Nummer = tDaten[1];
		this.Beginn = tDaten[2];
		this.Dauer = Integer.parseInt(tDaten[3]);
		this.Ende = tDaten[4];
		this.aktBlockNeu = this.Block;
		
		this.Eltern = parent;
		this.vect = vterm;
		this.feld = new Felder();
		this.feld.Init(this.vect);

		AnzahlOrigBloecke = this.feld.getAnzahlBloecke(this.Kollege);
		for(i=0;i<1;i++){

			
			if((result = BlockPasstGenau()) == 0){
				setzeBlockPasstGenau();
				this.Eltern.neuerBlockAktiv(this.aktBlockNeu);
				break;
			}
			/************************************/
			if((result = BlockPasstObenUntenNicht())>= 0){
				int state;
				switch(result){
				case (1):
					//System.out.println("***nicht erster und Ende = kleiner oder gleich Ende Folgeblock");
					//beginn gleich aber ende nach hinten = folgeblock kürzen
					state = JOptionPane.showConfirmDialog(null,
							"Die Endzeit Ihres Terminwunsches ragt in den nachfolgenden Termin\n"+
							"Soll die Startzeit und Dauer des nachfolgenden Termines\n"+"" +
							"entsprechend angepaßt (vekürzt) werden??",
							"Benutzeranweisung erforderlich",JOptionPane.YES_NO_OPTION);
					if (state == JOptionPane.YES_OPTION) {
								KuerzeNachBlock();
					}else{
						TerminFenster.getThisClass().setUpdateVerbot(false);
						TerminFenster.starteUnlock();	
					}
					break;
				case (2):
					TerminFenster.getThisClass().setUpdateVerbot(false);
					TerminFenster.starteUnlock();	
					//System.out.println("***nicht letzter Block  und Ende = größer als Ende Folgeblock");
					break;
				case (3):
					TerminFenster.getThisClass().setUpdateVerbot(false);
					TerminFenster.starteUnlock();	
					//System.out.println("***es ist der letzte Block  und Ende = kleiner oder gleich Ende Folgeblock");
					break;
				case (4):
					TerminFenster.getThisClass().setUpdateVerbot(false);
					TerminFenster.starteUnlock();	
					//System.out.println("***es ist der letzte Block  und Ende = größer als Kalenderende");					
					break;				
				case (5):
					ObenAndockenUntenNeuBlock();
					this.Eltern.neuerBlockAktiv(this.aktBlockNeu);
					break;
				}
				//System.out.println("Rückgabewert: "+result);
				//setzeBlockPasstObenUntenNicht();
				break;
			}
			/************************************/
			if((result = BlockPasstUntenObenNicht())>= 0){
				switch(result){
				case (1):
					UntenAndockenObenNeuBlock();
					this.Eltern.neuerBlockAktiv(this.aktBlockNeu+1);					 
					break;
				case (2):
					this.Eltern.neuerBlockAktiv(this.aktBlockNeu+1);					
					ObenNeuBlockUntenNeuBlock();
					break;
				case (3):
					TerminFenster.getThisClass().setUpdateVerbot(false);
					TerminFenster.starteUnlock();	
					break;
				case (4):
					TerminFenster.getThisClass().setUpdateVerbot(false);
					TerminFenster.starteUnlock();	
					break;				
				case (5):
					TerminFenster.getThisClass().setUpdateVerbot(false);
					TerminFenster.starteUnlock();	
					break;
				}
				//System.out.println("Rückgabewert: "+result);
				//setzeBlockPasstUntenObenNicht();
				break;
			}			
			/************************************/
			if( ((result = BeginnRagtInVorBlock())>= 0) ){
				int state;
				switch(result){
					case (1):
						// ragt in Vorblock ende ist gelich wie der start des Folgeblock
						state = JOptionPane.showConfirmDialog(null,
						"Die Startzeit Ihres Terminwunsches ragt in den vorherigen Termin!!!\n\n"+
						"Soll der vorherige Termin entsprechend gekürzt werden?",
						"Benutzeranweisung erforderlich",JOptionPane.YES_NO_OPTION);
						if (state == JOptionPane.YES_OPTION) {
							//System.out.println("Fehler bei --->KuerzeVorBlock()");
							KuerzeVorBlock();
						}else{
							TerminFenster.getThisClass().setUpdateVerbot(false);
							TerminFenster.starteUnlock();	
						}
						break;
					case (2):
						// ragt in Vorblock ende ist früher als start Folgeblock // (vorblock kürzen und nachblock erforderlich)
						state = JOptionPane.showConfirmDialog(null,
								"Die Startzeit Ihres Terminwunsches ragt in den vorherigen Termin!!!\n\n"+
								"Soll der vorherige Termin entsprechend gekürzt werden?",
								"Benutzeranweisung erforderlich",JOptionPane.YES_NO_OPTION);
								if (state == JOptionPane.YES_OPTION) {
									//System.out.println("Fehler bei --->KuerzeVorBlockUndNeuBlock()");
									KuerzeVorBlockUndNeuBlock();
								}else{
									TerminFenster.getThisClass().setUpdateVerbot(false);
									TerminFenster.starteUnlock();	
								}
						break;
					case (3):
						// ragt in Vorblock ende ist später als start Folgeblock und kürzer als ende Folgeblock// (Kürzung Vor- und Nachblock erforderlich)
						state = JOptionPane.showConfirmDialog(null,
								"Die Startzeit Ihres Terminwunsches ragt sowohl in den vorherigen Termin\n"+
								"als auch in den nachfolgenden Termin!!!\n\n"+
								"Sollen sowohl der vorherige, als auch der nachfolgende\n"+"" +
								"Termin entsprechend gekürzt werden??",
								"Benutzeranweisung erforderlich",JOptionPane.YES_NO_OPTION);
								if (state == JOptionPane.YES_OPTION) {
									//System.out.println("Fehler bei --->KuerzeVorBlockUndNachBlock()");
									KuerzeVorUndNachBlock();
								}else{
									TerminFenster.getThisClass().setUpdateVerbot(false);
									TerminFenster.starteUnlock();	
								}
						break;

				}
				break;
			}	
			/************************************/			
			if( ((result = EndeRagtInNachBlock())>= 0) ){
				int state;
				switch(result){
					case (1):
						//beginn gleich aber ende nach hinten = folgeblock kürzen
						state = JOptionPane.showConfirmDialog(null,
								"Die Endzeit Ihres Terminwunsches ragt in den nachfolgenden Termin\n"+
								"Soll die Startzeit und Dauer des nachfolgenden Termines\n"+"" +
								"entsprechend angepaßt (verkürzt) werden??",
								"Benutzeranweisung erforderlich",JOptionPane.YES_NO_OPTION);
								if (state == JOptionPane.YES_OPTION) {
									KuerzeNachBlock();
								}else{
									TerminFenster.getThisClass().setUpdateVerbot(false);
									TerminFenster.starteUnlock();	
								}
						break;
					case (2):
						state = JOptionPane.showConfirmDialog(null,
								"Die Endzeit Ihres Terminwunsches ragt in den nachfolgenden Termin\n"+
								"Soll die Startzeit und Dauer des nachfolgenden Termines\n"+"" +
								"entsprechend angepaßt (verkürzt) werden??",
								"Benutzeranweisung erforderlich",JOptionPane.YES_NO_OPTION);
								if (state == JOptionPane.YES_OPTION) {
									NeuVorBlockUndKuerzeNachBlock();
								}else{
									TerminFenster.getThisClass().setUpdateVerbot(false);
									TerminFenster.starteUnlock();	
								}
						break;
				}
				break;
			}	
			/************************************/			
			result = -1;
		}
		if(result == -1){
			TerminFenster.getThisClass().setUpdateVerbot(false);
			TerminFenster.starteUnlock();	
			JOptionPane.showMessageDialog (null, "Die von Ihnen eingegebenen Terminangaben kollidieren\n"+
						"mit dem vorherigen oder nachfolgenden Termin\n\n"+
						"Ihr Terminwunsch kann daher nicht eingetragen werden!");
		}
		return result;
	}
/******************************/	
	private int BlockPasstGenau(){
		String sBeginn = this.feld.getFeld(this.Kollege,iBeginn,this.Block);
		String sEnde = this.feld.getFeld(this.Kollege,iEnde,this.Block);		
		String sDauer = this.feld.getFeld(this.Kollege,iDauer,this.Block);
		if( this.Beginn.equals(sBeginn) && this.Ende.equals(sEnde) && this.Dauer==Integer.parseInt(sDauer) ){
			return 0;
		}
		return -1;
	}
/******************************/
	private void setzeBlockPasstGenau(){
		this.feld.setFeld(this.Kollege,iName,this.Block,this.Name);
		this.feld.setFeld(this.Kollege,iNummer,this.Block,this.Nummer);
		KalenderBeschreiben th = new KalenderBeschreiben();
		th.KalenderDaten(this.feld,this.Kollege,aktDatum[this.Spalte],this.dbBehandler);
		return;
	}
/******************************/	
	private int BlockPasstObenUntenNicht(){
		String sBeginn = this.feld.getFeld(this.Kollege,iBeginn,this.Block);
		String sEnde = this.feld.getFeld(this.Kollege,iEnde,this.Block);		
		String sDauer = this.feld.getFeld(this.Kollege,iDauer,this.Block);
		//System.out.println("++++Beginn PasstObenUntenNicht++++++");
		if( this.Beginn.equals(sBeginn) && (!this.Ende.equals(sEnde))){
			//System.out.println("Ende Ungleich");
			/**Wenn das angegebene Ende größer ist als das ende des Datenbankblocks**/
			if(ZeitFunk.MinutenSeitMitternacht(this.Ende) > ZeitFunk.MinutenSeitMitternacht(sEnde)){
				//System.out.println("Ende größer als Block");
				//****ist der - aktuelle Block - kleiner als die Gesamtzahl**//
				if((this.Block+1) < AnzahlOrigBloecke){
					/***Wenn ja prüfen ob wenigstens das Ende des Folgeblockes kleiner ist**/
					//System.out.println("Dies ist nicht der letzte Block");
					if(ZeitFunk.MinutenSeitMitternacht(this.Ende) < ZeitFunk.MinutenSeitMitternacht(this.feld.getFeld(this.Kollege,iEnde,this.Block+1))){		
						System.out.println("Dies ist nicht der letzte Block und das Ende ist kleiner oder gleich dem Ende des Folgeblockes - Rückgabewert 1");						
						return 1;
					}else if(ZeitFunk.MinutenSeitMitternacht(this.Ende) == ZeitFunk.MinutenSeitMitternacht(this.feld.getFeld(this.Kollege,iEnde,this.Block+1))){
						return -1;
					}else{
						//System.out.println("Dies ist nicht der letzte Block und das Ende ist größer als das Ende des Folgeblockes - Rückgabewert 2");
						//**nicht der - letzte Block - und der nachfolgende kann gekürzt werden**//
						return 2;
					}
				//***der aktuelle Block - ist der letzte Block****//
				}else{
					//System.out.println("Es handelt sich um den letzten Block");
					//**das angegebene Ende ist vor dem Ende des letzten Blockes
					//System.out.println("Zeit1:"+zeitFunk.MinutenSeitMitternacht(this.Ende)+
					//		" Zeit2:"+zeitFunk.MinutenSeitMitternacht(this.feld.getFeld(this.Kollege,iEnde,this.Block)));
					if(ZeitFunk.MinutenSeitMitternacht(this.Ende) <= ZeitFunk.MinutenSeitMitternacht(this.feld.getFeld(this.Kollege,iEnde,this.Block))){		
						//System.out.println("Das Ende ist kleiner oder gleich des aktuellen (letzten) Blockes - Rückgabewert 3");
						return 3;
					//**das angegebene Ende würden das Ende des letzten Blockes übersteigen**/
					}else{
						//System.out.println("Das Ende ist größer als das des aktuellen (letzten) Blockes - Rückgabewert 4");
						return 4;
					}
				}
			}else{
				//System.out.println("Das Ende ist kleiner als das bisherige Ende Rückgabewert 5");
				return 5;
			}
		}
		//System.out.println("Beginn gleich und Ende ungleich trifft nicht zu (dann muß der Anfang unterschiedlich sein)");
		return -1;
	}
/******************************/
	private void setzeBlockPasstObenUntenNicht(){
		this.feld.setFeld(this.Kollege,iName,this.Block,this.Name);
		this.feld.setFeld(this.Kollege,iNummer,this.Block,this.Nummer);
		return;
	}
/******************************/	
	private int BlockPasstUntenObenNicht(){
		String sBeginn = this.feld.getFeld(this.Kollege,iBeginn,this.Block);
		String sEnde = this.feld.getFeld(this.Kollege,iEnde,this.Block);		
		String sDauer = this.feld.getFeld(this.Kollege,iDauer,this.Block);
		//System.out.println("++++Beginn PasstUntenObenNicht++++++");
		if( this.Ende.equals(sEnde) && (!this.Beginn.equals(sBeginn))){ /****Das Ende gleich aber der Anfang nicht)***/
			if (ZeitFunk.MinutenSeitMitternacht(sBeginn) < ZeitFunk.MinutenSeitMitternacht(this.Beginn) ){ /*** Die neue Beginnzeit später ist**/
				//System.out.println("Endzeit gleich aber Beginnzeit später als bislang");
				return 1;
			}
			
		}else{
			if( (ZeitFunk.MinutenSeitMitternacht(sBeginn) < ZeitFunk.MinutenSeitMitternacht(this.Beginn)) &&
					(ZeitFunk.MinutenSeitMitternacht(sEnde) > ZeitFunk.MinutenSeitMitternacht(this.Ende)) ){
				//System.out.println("Der Block liegt zwischen dem bisherigen Block");
				return 2;
			}
		}
		//System.out.println("Der Beginn wurde vor den Ursprungsbeginn gestellt");
		return -1;
	}
/******************************/
	private void setzeBlockPasstUntenObenNicht(){
		this.feld.setFeld(this.Kollege,iName,this.Block,this.Name);
		this.feld.setFeld(this.Kollege,iNummer,this.Block,this.Nummer);
		KalenderBeschreiben th = new KalenderBeschreiben();
		th.KalenderDaten(this.feld,this.Kollege,aktDatum[this.Spalte],this.dbBehandler);

		return;
	}
/******************************/
	private void ObenAndockenUntenNeuBlock(){
		String endeBisher,endeNeu,beginnBisher,beginnNeu,beginnZweitBlock, aktName,aktNummer;
		int dauerBisher, dauerNeu;
		int differenz,minuten;
		//String anfangNeu;
		//int dauerNeu, dauerAlt;
		//int neueDauer,alteDauer,minuten1,minuten2,xdauer; 
		//String alteZeit,dummyZeit;
		String altesEnde;

		endeBisher = this.feld.getFeld(this.Kollege,iEnde,this.Block);
		endeNeu = this.Ende;
		beginnBisher = this.feld.getFeld(this.Kollege,iBeginn,this.Block);
		beginnNeu = this.Beginn;
		dauerBisher = new Integer(this.feld.getFeld(this.Kollege,iDauer,this.Block));
		dauerNeu = this.Dauer;
		differenz = dauerBisher-dauerNeu;
		aktName = this.feld.getFeld(this.Kollege,iName,this.Block);
		aktNummer = this.feld.getFeld(this.Kollege,iNummer,this.Block);	
		
		/**** Hier noch Test einbauen ob nachfolgender Block leer und ob zusammengefaßt werden soll */

		/****Als erste den neuen Block einfügen und beschreiben*****/
		int neublocknum = this.Block+1;
		this.feld.einfuegenBlock(this.Kollege,neublocknum);
		if (! aktName.trim().isEmpty()){
			this.feld.setFeld(this.Kollege,iName,neublocknum,aktName);			
		}
		//if (! aktNummer.trim().equals("@FREI")){
			this.feld.setFeld(this.Kollege,iNummer,neublocknum,aktNummer);			
		//}
		/**** neue Endezeit ist bisherige Endezeit **/
		this.feld.setFeld(this.Kollege,iEnde,neublocknum,endeBisher);
		this.feld.setFeld(this.Kollege,iDauer,neublocknum,(String) Integer.toString(differenz));
		minuten = (int) ZeitFunk.MinutenSeitMitternacht( endeBisher)-differenz;
		beginnZweitBlock = ZeitFunk.MinutenZuZeit(minuten);
		this.feld.setFeld(this.Kollege,iBeginn,neublocknum,beginnZweitBlock);		

		/****Dann den bisherigen block mit den neu eingegebenen Daten beschreiben*****/
		this.feld.setFeld(this.Kollege,iName,this.Block,this.Name);
		//Eingabe Nummer eintragen
		this.feld.setFeld(this.Kollege,iNummer,this.Block,this.Nummer);
		//Beginn eintragen 
		this.feld.setFeld(this.Kollege,iBeginn, this.Block,this.Beginn);
		//Eingabe Dauer eintragen
		this.feld.setFeld(this.Kollege,iDauer, this.Block, Integer.toString(this.Dauer));
		//Endzeit eintragen		
		this.feld.setFeld(this.Kollege,iEnde, this.Block, this.Ende);

		//Blockzahl erhöhen und eintragen
		AnzahlOrigBloecke++;
		this.feld.setAnzahlBloecke(this.Kollege,AnzahlOrigBloecke);	
		KalenderBeschreiben th = new KalenderBeschreiben();
		th.KalenderDaten(this.feld,this.Kollege,aktDatum[this.Spalte],this.dbBehandler);



		return;
	}
/******************************/
	private void UntenAndockenObenNeuBlock(){
		String endeBisher,endeNeu,beginnBisher,beginnNeu,beginnZweitBlock,aktName,aktNummer;
		int dauerBisher, dauerNeu;
		int differenz,minuten;
		//String anfangNeu;
		//int dauerNeu, dauerAlt;
		//int neueDauer,alteDauer,minuten1,minuten2,xdauer; 
		//String alteZeit,dummyZeit;
		String altesEnde;

		beginnBisher = this.feld.getFeld(this.Kollege,iBeginn,this.Block);
		endeBisher = this.feld.getFeld(this.Kollege,iEnde,this.Block);
		dauerBisher = Integer.parseInt(this.feld.getFeld(this.Kollege,iDauer,this.Block));
		
		aktName = this.feld.getFeld(this.Kollege,iName,this.Block);
		aktNummer = this.feld.getFeld(this.Kollege,iNummer,this.Block);	
		
		
		beginnNeu = this.Beginn;
		endeNeu = this.Ende;
		dauerNeu = this.Dauer;
		differenz = dauerBisher - dauerNeu;
		
		/****Als erstes den bisherigen block mit den neu eingegebenen Daten beschreiben*****/
		this.feld.setFeld(this.Kollege,iName,this.Block,this.Name);
		//Eingabe Nummer eintragen
		this.feld.setFeld(this.Kollege,iNummer,this.Block,this.Nummer);
		//Beginn eintragen 
		this.feld.setFeld(this.Kollege,iBeginn, this.Block,this.Beginn);
		//Eingabe Dauer eintragen
		this.feld.setFeld(this.Kollege,iDauer, this.Block, Integer.toString(this.Dauer));
		//Endzeit eintragen		
		this.feld.setFeld(this.Kollege,iEnde, this.Block, this.Ende);

		/****Dann den neuen Block vor den bisherigen Block einfügen und beschreiben*****/		
		int neublocknum = this.Block;
		this.feld.einfuegenBlock(this.Kollege,neublocknum);
		if (! aktName.trim().isEmpty()){
			this.feld.setFeld(this.Kollege,iName,neublocknum,aktName);			
		}
		if (aktNummer.trim().equals("@FREI")){
			this.feld.setFeld(this.Kollege,iNummer,neublocknum,this.Nummer);			
		}
		/**** neue Beginnzeit ist bisherige Beginnzeit**/
		this.feld.setFeld(this.Kollege,iBeginn,neublocknum,beginnBisher);
		this.feld.setFeld(this.Kollege,iDauer,neublocknum,(String) Integer.toString(differenz));
		this.feld.setFeld(this.Kollege,iEnde,neublocknum,this.Beginn);		

		//Blockzahl erhöhen und eintragen
		AnzahlOrigBloecke++;
		this.feld.setAnzahlBloecke(this.Kollege,AnzahlOrigBloecke);	
		KalenderBeschreiben th = new KalenderBeschreiben();
		th.KalenderDaten(this.feld,this.Kollege,aktDatum[this.Spalte],this.dbBehandler);


	}
/******************************/	
	private void ObenNeuBlockUntenNeuBlock(){
		String endeBisher,endeNeu,beginnBisher,beginnNeu,beginnZweitBlock,aktName,aktNummer;
		int dauerBisher, dauerNeu;
		int dauer_vorBlock,dauer_nachBlock,dauer_aktBlock;
		//String anfangNeu;
		//int dauerNeu, dauerAlt;
		//int neueDauer,alteDauer,minuten1,minuten2,xdauer; 
		//String alteZeit,dummyZeit;
		String altesEnde;

		aktName = this.feld.getFeld(this.Kollege,iName,this.Block);
		aktNummer = this.feld.getFeld(this.Kollege,iNummer,this.Block);		
		beginnBisher = this.feld.getFeld(this.Kollege,iBeginn,this.Block);
		endeBisher = this.feld.getFeld(this.Kollege,iEnde,this.Block);
		dauerBisher = Integer.parseInt(this.feld.getFeld(this.Kollege,iDauer,this.Block));
		
		beginnNeu = this.Beginn;
		endeNeu = this.Ende;
		dauerNeu = this.Dauer;
		dauer_vorBlock = (int) (ZeitFunk.MinutenSeitMitternacht(beginnNeu) -
								ZeitFunk.MinutenSeitMitternacht( beginnBisher));
		dauer_nachBlock = (int) (ZeitFunk.MinutenSeitMitternacht(endeBisher) -
				ZeitFunk.MinutenSeitMitternacht( endeNeu));
		dauer_aktBlock = dauerNeu;
		/****im bisherigen Block lediglich die Zeitdaten verändern***/
		this.feld.setFeld(this.Kollege,iBeginn, this.Block,beginnBisher);
		//Eingabe Dauer eintragen
		this.feld.setFeld(this.Kollege,iDauer, this.Block, Integer.toString(dauer_vorBlock));
		//Endzeit eintragen	diese entspricht der Beginnzeit des neuen Blocks
		this.feld.setFeld(this.Kollege,iEnde, this.Block, this.Beginn);
		
		/****jetzt einen neuen Block setzen das ist jetzt unser aktiver Block***/
		int neublocknum = this.Block+1;
		this.feld.einfuegenBlock(this.Kollege,neublocknum);

		/****diesen aktuellen Block mit den eingegebenen Datenbeschreiben**/
		this.feld.setFeld(this.Kollege,iName,neublocknum,this.Name);
		//Eingabe Nummer eintragen
		this.feld.setFeld(this.Kollege,iNummer,neublocknum,this.Nummer);

		this.feld.setFeld(this.Kollege,iBeginn, neublocknum,this.Beginn);
		//Eingabe Dauer eintragen
		this.feld.setFeld(this.Kollege,iDauer, neublocknum, Integer.toString(dauer_aktBlock));
		//Endzeit eintragen	diese entspricht der Beginnzeit des neuen Blocks
		this.feld.setFeld(this.Kollege,iEnde, neublocknum, this.Ende);
		
		/****jetzt erneut einen neuen Block setzen das ist jetzt der Nachblock***/
		neublocknum = neublocknum +1;
		this.feld.einfuegenBlock(this.Kollege,neublocknum);

		
		/****diesen Nachblock Block mit alten Datenbeschreiben**/
		//this.feld.setFeld(this.Kollege,iName,neublocknum,this.Name);
		//Eingabe Nummer eintragen
		if (! aktName.trim().isEmpty()){
			this.feld.setFeld(this.Kollege,iName,neublocknum,aktName);			
		}
		this.feld.setFeld(this.Kollege,iNummer,neublocknum,aktNummer);			
		
		this.feld.setFeld(this.Kollege,iBeginn, neublocknum,this.Ende);
		//Eingabe Dauer eintragen
		this.feld.setFeld(this.Kollege,iDauer, neublocknum, Integer.toString(dauer_nachBlock));
		//Endzeit eintragen	diese entspricht der Beginnzeit des neuen Blocks
		String sEndeNeu = ZeitFunk.MinutenZuZeit( (int)
							ZeitFunk.MinutenSeitMitternacht(this.Ende)+
							dauer_nachBlock	);
		this.feld.setFeld(this.Kollege,iEnde, neublocknum, sEndeNeu);
	
		//Blockzahl erhöhen und eintragen
		AnzahlOrigBloecke = AnzahlOrigBloecke + 2;
		this.feld.setAnzahlBloecke(this.Kollege,AnzahlOrigBloecke);	
		KalenderBeschreiben th = new KalenderBeschreiben();
		th.KalenderDaten(this.feld,this.Kollege,aktDatum[this.Spalte],this.dbBehandler);
		
		
	}
/******************************/	
	private int BeginnRagtInVorBlock(){
		System.out.println("Die eingestellte Startzeit kollidiert mit den VorBlock");
		// Testen ob versucht wurde vor dem Kalender-Nullpunkt zu starten = 07:00:00;
		// Testen ob es sich um den ersten Block handelt;
		// Testen ob Start und Dauer des Vorblocks eine Reduzierung zuläßt; 
		String sBeginn = this.feld.getFeld(this.Kollege,iBeginn,this.Block);
		String sEnde = this.feld.getFeld(this.Kollege,iEnde,this.Block);		
		String sDauer = this.feld.getFeld(this.Kollege,iDauer,this.Block);
		int endeVorBlock;
		int startAktuell;
		if(this.Block == 0){
			//System.out.println("Terminblock = block 0");
			JOptionPane.showMessageDialog(null,"Sie versuchen vor den Kalenderstart zu springen\n"+
											"Termin kann nicht geschrieben werden", "Wichtige Mitteilung", JOptionPane.WARNING_MESSAGE);
			return -1;
		}
		endeVorBlock = (int) ZeitFunk.MinutenSeitMitternacht(this.feld.getFeld(this.Kollege,iEnde,this.Block-1));
		startAktuell =  (int) ZeitFunk.MinutenSeitMitternacht(this.Beginn);

		if((int) ZeitFunk.MinutenSeitMitternacht(this.feld.getFeld(this.Kollege,iBeginn,this.Block-1)) >
				startAktuell){
			JOptionPane.showMessageDialog(null,"Der gewünschte Starttermin würde sich mit dem voangegangenen\n"+
					"Termin überschneiden und kann deshalb nicht geschrieben werden", "Wichtige Mitteilung", JOptionPane.WARNING_MESSAGE);
			//System.out.println("Beginn Vorblock = nach dem Beginn des aktuellen Blocks");
			return -1;
		}
		if( (this.Block < (AnzahlOrigBloecke-1)) && (startAktuell < endeVorBlock) ){
			if(SystemConfig.TerminUeberlappung == 0){
				JOptionPane.showMessageDialog(null,"Der gewünschte Starttermin würde sich mit dem voangegangenen\n"+
						"Termin überschneiden und kann deshalb nicht geschrieben werden", "Wichtige Mitteilung", JOptionPane.WARNING_MESSAGE);
				//System.out.println("Terminüberlappung ist nicht erlaubt");
				return -1;
			}
			int endeAktuell,endeFolgeBlock,startFolgeBlock;
			endeAktuell = (int) ZeitFunk.MinutenSeitMitternacht(sEnde);
			endeFolgeBlock = (int)ZeitFunk.MinutenSeitMitternacht(this.feld.getFeld(this.Kollege,iEnde,this.Block+1));
			startFolgeBlock = (int)ZeitFunk.MinutenSeitMitternacht(this.feld.getFeld(this.Kollege,iBeginn,this.Block+1));
			if((int) ZeitFunk.MinutenSeitMitternacht(this.Ende) == startFolgeBlock){
				//System.out.println("Ende aktuell == Start Folgeblock");				
				return 1;
			}
			if((int) ZeitFunk.MinutenSeitMitternacht(this.Ende) < startFolgeBlock){
				//System.out.println("Ende aktuell < Start Folgeblock");				
				return 2;
			}
			if( ((int) ZeitFunk.MinutenSeitMitternacht(this.Ende) > startFolgeBlock) && 
					((int) ZeitFunk.MinutenSeitMitternacht(this.Ende) < endeFolgeBlock) ){
				//System.out.println("Ende aktuell > startFolgeblock und < ende Folgeblock");
				return 3;
			}
		}
		return -1;
	}
/******************************/
	private void KuerzeVorBlock(){
		
		String startVorblock = this.feld.getFeld(this.Kollege,iBeginn,this.Block-1);
		int dauerVorBlock = (int)ZeitFunk.ZeitDifferenzInMinuten(startVorblock,this.Beginn);
		if(dauerVorBlock==0){
			//System.out.println("KuerzeVorBlock: Die Dauer wäre 0 Minuten****************->");
			TerminFenster.getThisClass().setUpdateVerbot(false);
			TerminFenster.starteUnlock();
			JOptionPane.showMessageDialog (null, "Die von Ihnen eingegebenen Terminangaben kollidieren\n"+
					"mit dem vorherigen oder nachfolgenden Termin\n\n"+
					"Ihr Terminwunsch kann daher nicht eingetragen werden!");
			return;
		}
		this.feld.setFeld(this.Kollege,iEnde,this.Block-1,this.Beginn);

		this.feld.setFeld(this.Kollege,iDauer,this.Block-1,Integer.toString(dauerVorBlock));
		//int dauerVorblock = new Integer(this.feld.getFeld(this.Kollege,iDauer,this.Block-1));
		this.feld.setFeld(this.Kollege,iName,this.Block,this.Name);
		this.feld.setFeld(this.Kollege,iNummer,this.Block,this.Nummer);		
		this.feld.setFeld(this.Kollege,iBeginn,this.Block,this.Beginn);		
		this.feld.setFeld(this.Kollege,iDauer,this.Block,Integer.toString(this.Dauer));
		KalenderBeschreiben th = new KalenderBeschreiben();
		th.KalenderDaten(this.feld,this.Kollege,aktDatum[this.Spalte],this.dbBehandler);
	}	
/******************************/
	private void KuerzeVorBlockUndNeuBlock(){
		String name = this.feld.getFeld(this.Kollege,iName,this.Block);
		String nummer = this.feld.getFeld(this.Kollege,iNummer,this.Block);
		String ende = this.feld.getFeld(this.Kollege,iEnde,this.Block);
		
		String startVorblock = this.feld.getFeld(this.Kollege,iBeginn,this.Block-1);
		int dauerVorBlock = (int)ZeitFunk.ZeitDifferenzInMinuten(startVorblock,this.Beginn);
		if(dauerVorBlock==0){
			//System.out.println("KuerzeVorBlockUndNeuBlock: Die Dauer wäre 0 Minuten****************->");
			TerminFenster.getThisClass().setUpdateVerbot(false);
			TerminFenster.starteUnlock();	
			JOptionPane.showMessageDialog (null, "Die von Ihnen eingegebenen Terminangaben kollidieren\n"+
					"mit dem vorherigen oder nachfolgenden Termin\n\n"+
					"Ihr Terminwunsch kann daher nicht eingetragen werden!");
			return;
		}
		this.feld.setFeld(this.Kollege,iEnde,this.Block-1,this.Beginn);
		//int dauerVorBlock = (int)zeitFunk.ZeitDifferenzInMinuten(startVorblock,this.Beginn);
		this.feld.setFeld(this.Kollege,iDauer,this.Block-1,Integer.toString(dauerVorBlock));

		this.feld.setFeld(this.Kollege,iName,this.Block,this.Name);
		this.feld.setFeld(this.Kollege,iNummer,this.Block,this.Nummer);		
		this.feld.setFeld(this.Kollege,iBeginn,this.Block,this.Beginn);		
		this.feld.setFeld(this.Kollege,iDauer,this.Block,Integer.toString(this.Dauer));
		this.feld.setFeld(this.Kollege,iEnde,this.Block,this.Ende);

		int neublocknum = this.Block+1;
		this.feld.einfuegenBlock(this.Kollege,neublocknum);

		this.feld.setFeld(this.Kollege,iName,neublocknum,name);
		this.feld.setFeld(this.Kollege,iNummer,neublocknum,nummer);		
		this.feld.setFeld(this.Kollege,iBeginn,neublocknum,this.Ende);
		int neudauer = (int)ZeitFunk.ZeitDifferenzInMinuten(this.Ende,ende);
		this.feld.setFeld(this.Kollege,iDauer,neublocknum,Integer.toString(neudauer));
		this.feld.setFeld(this.Kollege,iEnde,neublocknum,ende);
		this.feld.setAnzahlBloecke(this.Kollege,AnzahlOrigBloecke+1);
		//System.out.println("Zeitdifferenz "+neudauer);
		KalenderBeschreiben th = new KalenderBeschreiben();
		th.KalenderDaten(this.feld,this.Kollege,aktDatum[this.Spalte],this.dbBehandler);
	}	

/******************************/
	private void KuerzeVorUndNachBlock(){
		String startVorblock = this.feld.getFeld(this.Kollege,iBeginn,this.Block-1);
		int dauerVorBlock = (int)ZeitFunk.ZeitDifferenzInMinuten(startVorblock,this.Beginn);
		if(dauerVorBlock==0){
			//System.out.println("KuerzeVorUndNachBlock: Die Dauer wäre 0 Minuten****************->");
			TerminFenster.getThisClass().setUpdateVerbot(false);
			TerminFenster.starteUnlock();
			JOptionPane.showMessageDialog (null, "Die von Ihnen eingegebenen Terminangaben kollidieren\n"+
					"mit dem vorherigen oder nachfolgenden Termin\n\n"+
					"Ihr Terminwunsch kann daher nicht eingetragen werden!");
			return;
		}
		this.feld.setFeld(this.Kollege,iEnde,this.Block-1,this.Beginn);
		//int dauerVorBlock = (int)zeitFunk.ZeitDifferenzInMinuten(startVorblock,this.Beginn);
		this.feld.setFeld(this.Kollege,iDauer,this.Block-1,Integer.toString(dauerVorBlock));
		//int dauerVorblock = new Integer(this.feld.getFeld(this.Kollege,iDauer,this.Block-1));

		String endeNachBlock = this.feld.getFeld(this.Kollege,iEnde,this.Block+1);

		this.feld.setFeld(this.Kollege,iName,this.Block,this.Name);
		this.feld.setFeld(this.Kollege,iNummer,this.Block,this.Nummer);		
		this.feld.setFeld(this.Kollege,iBeginn,this.Block,this.Beginn);		
		this.feld.setFeld(this.Kollege,iDauer,this.Block,Integer.toString(this.Dauer));
		this.feld.setFeld(this.Kollege,iEnde,this.Block,this.Ende);
		
		this.feld.setFeld(this.Kollege,iBeginn,this.Block+1,this.Ende);		
		int dauerNachBlock = (int)ZeitFunk.ZeitDifferenzInMinuten(this.Ende,endeNachBlock);
		this.feld.setFeld(this.Kollege,iDauer,this.Block+1,Integer.toString(dauerNachBlock));		
		
		KalenderBeschreiben th = new KalenderBeschreiben();
		th.KalenderDaten(this.feld,this.Kollege,aktDatum[this.Spalte],this.dbBehandler);
	}
/******************************/

	private int EndeRagtInNachBlock(){
		// Testen ob versucht wurde nach dem Kalender-Maximum zu enden = 22:00:00;
		// Testen ob es sich um den letzten Block handelt;
		// Testen ob Ende und Dauer des Nachblocks eine Reduzierung zuläßt;
		int ende = (int) ZeitFunk.MinutenSeitMitternacht(this.Ende);
		String sende = this.feld.getFeld(this.Kollege,iEnde,this.Block);
		int aktende = (int) ZeitFunk.MinutenSeitMitternacht(sende);
		String sbeginn = this.feld.getFeld(this.Kollege,iBeginn,this.Block);
		if(ende >= SystemConfig.KalenderMilli[1]){
			JOptionPane.showMessageDialog(null,"Die gewünschte Endzeit des Termines liegt nach der absoluten Endzeit\n"+
					"des Terminkalenders und kann deshalb nicht geschrieben werden", "Wichtige Mitteilung", JOptionPane.WARNING_MESSAGE);
			return -1;
		}
		// verpacken in gültige if-abfrage
		if( (ende > aktende) && (this.Block < AnzahlOrigBloecke-1) ){
			if(SystemConfig.TerminUeberlappung == 0){
				JOptionPane.showMessageDialog(null,"Der gewünschte Endzeitpunkt würde sich mit anderen \n"+
					"Terminen überschneiden und kann deshalb nicht geschrieben werden", "Wichtige Mitteilung", JOptionPane.WARNING_MESSAGE);
				//System.out.println("Terminüberlappung ist nicht erlaubt");
				return -1;
			}
			String sendefolge = this.feld.getFeld(this.Kollege,iEnde,this.Block+1);
			int endefolge = (int) ZeitFunk.MinutenSeitMitternacht(sendefolge);
			//anfang gleich aber ende nach hinten verschoben = folgeblock kürzen
			if( (this.Beginn.equals(sbeginn)) && (ende < endefolge)){
				return 1;
			}
			int zbeginn = (int) ZeitFunk.MinutenSeitMitternacht(this.Beginn);
			int aktbeginn = (int) ZeitFunk.MinutenSeitMitternacht(sbeginn);
			//anfang nach hinten verschoben und ende nach hinten verschoben = neuen vorblock setzen und folgeblock kürzen
			if( (zbeginn > aktbeginn) && (ende < endefolge)){
				//System.out.println("In NachBlock-Prüfung mit Vorblock");
				return 2;
			}
		}
		return -1;
	}
/******************************/
	private void KuerzeNachBlock(){
		String endeNachBlock = this.feld.getFeld(this.Kollege,iEnde,this.Block+1);

		this.feld.setFeld(this.Kollege,iName,this.Block,this.Name);
		this.feld.setFeld(this.Kollege,iNummer,this.Block,this.Nummer);		
		this.feld.setFeld(this.Kollege,iDauer,this.Block,Integer.toString(this.Dauer));
		this.feld.setFeld(this.Kollege,iEnde,this.Block,this.Ende);
		

		this.feld.setFeld(this.Kollege,iBeginn,this.Block+1,this.Ende);
		
		int iDauerNachBlock = (int)ZeitFunk.ZeitDifferenzInMinuten(this.Ende,endeNachBlock);
		this.feld.setFeld(this.Kollege,iDauer,this.Block+1,Integer.toString(iDauerNachBlock));

		KalenderBeschreiben th = new KalenderBeschreiben();
		th.KalenderDaten(this.feld,this.Kollege,aktDatum[this.Spalte],this.dbBehandler);
	}	
	
/******************************/
	private void NeuVorBlockUndKuerzeNachBlock(){
		//xxxxx
		String aktName = this.feld.getFeld(this.Kollege,iName,this.Block);
		String aktNummer = this.feld.getFeld(this.Kollege,iNummer,this.Block);		
		String beginnBisher = this.feld.getFeld(this.Kollege,iBeginn,this.Block);
		String endeBisher = this.feld.getFeld(this.Kollege,iEnde,this.Block);
		int dauerBisher = Integer.parseInt(this.feld.getFeld(this.Kollege,iDauer,this.Block));
		int iDauerVorBlock = (int)ZeitFunk.ZeitDifferenzInMinuten(beginnBisher,this.Beginn);		

		this.feld.setFeld(this.Kollege,iEnde,this.Block,this.Beginn);
		this.feld.setFeld(this.Kollege,iDauer,this.Block,Integer.toString(iDauerVorBlock));
		
		
		int neublocknum = this.Block+1;
		this.feld.einfuegenBlock(this.Kollege,neublocknum);
		
		this.feld.setFeld(this.Kollege,iName,neublocknum,this.Name);
		this.feld.setFeld(this.Kollege,iNummer,neublocknum,this.Nummer);		
		this.feld.setFeld(this.Kollege,iBeginn,neublocknum,this.Beginn);
		this.feld.setFeld(this.Kollege,iDauer,neublocknum,Integer.toString(this.Dauer));		
		this.feld.setFeld(this.Kollege,iEnde,neublocknum,this.Ende);

		//System.out.println(this.Name+"/"+this.Nummer+"/"+this.Beginn+"/"+new Integer(this.Dauer).toString()+"/"+this.Ende);
		
		this.feld.setFeld(this.Kollege,iBeginn,neublocknum+1,this.Ende);
		String endeNachBlock = this.feld.getFeld(this.Kollege,iEnde,neublocknum+1);
		int iDauerNachBlock = (int)ZeitFunk.ZeitDifferenzInMinuten(this.Ende,endeNachBlock);
		this.feld.setFeld(this.Kollege,iDauer,neublocknum+1,Integer.toString(iDauerNachBlock));
		
		this.feld.setAnzahlBloecke(this.Kollege,AnzahlOrigBloecke+1);

		KalenderBeschreiben th = new KalenderBeschreiben();
		th.KalenderDaten(this.feld,this.Kollege,aktDatum[this.Spalte],this.dbBehandler);
	}	

/**********************Ende Klasse**********************/
}


/************
 * 
 * @author admin
 * Hier wird der Vector gelesen und beschrieben
 */




class Felder{
	String srueck;
	int dbKollege;
	Vector tvect = new Vector();
	
	public Felder Init(Vector termv){
		tvect = termv;
		return this;
	}
	public String getFeld(int iKoll,int iFeld,int iBlock){
		String sRet;
		sRet = ((String) ((ArrayList<Vector<String>>) tvect.get(iKoll)).get(iFeld).get(iBlock));
		return sRet;
	}
	public void setFeld(int iKoll,int iFeld,int iBlock,String text){
		String sRet;
		sRet = ((String) ((ArrayList<Vector<String>>) tvect.get(iKoll)).get(iFeld).set(iBlock,text));
		return;
	}
	public int getAnzahlBloecke(int iKoll){
		return new Integer( ((String) ((ArrayList<Vector<String>>) tvect.get(iKoll)).get(5).get(0)));
	}
	public int setAnzahlBloecke(int iKoll,int bloecke){
		((ArrayList<Vector<String>>) tvect.get(iKoll)).get(5).set(0,Integer.toString(bloecke));
		return bloecke;
	}
	public int getSize(int iKoll){
		return ((int) ((ArrayList<Vector<String>>) tvect.get(iKoll)).get(0).size());
	}
	public boolean einfuegenBlock(int iKoll,int iFeld){
		((ArrayList<Vector<String>>) tvect.get(iKoll)).get(0).insertElementAt("", iFeld);
		((ArrayList<Vector<String>>) tvect.get(iKoll)).get(1).insertElementAt("", iFeld);
		((ArrayList<Vector<String>>) tvect.get(iKoll)).get(2).insertElementAt("", iFeld);
		((ArrayList<Vector<String>>) tvect.get(iKoll)).get(3).insertElementAt("", iFeld);
		((ArrayList<Vector<String>>) tvect.get(iKoll)).get(4).insertElementAt("", iFeld);		
		//System.out.println("Vor der Erhöhung"+getAnzahlBloecke(iKoll));
		setAnzahlBloecke(iKoll,getSize(iKoll));
		//if (setAnzahlBloecke(iKoll,getAnzahlBloecke(iKoll)+1) >= 0){
			//System.out.println("Nach der Erhöhung:"+getAnzahlBloecke(iKoll));
		//}
		return true;
	}
	public boolean loeschenBlock(int iKoll,int iFeld){
		((ArrayList<Vector<String>>) tvect.get(iKoll)).get(0).removeElementAt(iFeld);
		((ArrayList<Vector<String>>) tvect.get(iKoll)).get(1).removeElementAt(iFeld);
		((ArrayList<Vector<String>>) tvect.get(iKoll)).get(2).removeElementAt(iFeld);
		((ArrayList<Vector<String>>) tvect.get(iKoll)).get(3).removeElementAt(iFeld);
		((ArrayList<Vector<String>>) tvect.get(iKoll)).get(4).removeElementAt(iFeld);		
		//System.out.println("Vor der Erhöhung"+getAnzahlBloecke(iKoll));
		setAnzahlBloecke(iKoll,getSize(iKoll));
		//System.out.println("Nach der Löschung:"+getAnzahlBloecke(iKoll));
		
		return true;
	}

}

class KalenderBeschreiben extends Thread implements Runnable{


	  private int iKoll;
	  private Felder vKalDaten = null;
	  private String datum = null;
	  private String threadStmt = "";
	  private Statement sState = null;
	  private boolean klappt;
	  private int dbKollege;

	  
	  public void KalenderDaten(Felder vKalDaten, int iKoll,String datum,int dbKollege){
		  this.vKalDaten = vKalDaten;
		  this.iKoll = iKoll;
		  this.datum = datum;
		  this.dbKollege = dbKollege;
		  //System.out.println("iKoll = "+iKoll);
		  //System.out.println("datum = "+datum);
		  //System.out.println("dbKollege = "+dbKollege);
		  start();
	  }
	  public void run(){
		  if(Reha.thisClass.terminpanel.getAnsicht()==2){
			  //System.out.println("vKalDaten="+this.vKalDaten);
			  //System.out.println("iKoll = "+this.iKoll);
			  //System.out.println("datum = "+this.datum);
			  //System.out.println("dbKollege = "+this.dbKollege);
			  maskenrun();
			  return;
		  }
		  StringBuffer buff = new StringBuffer();
		  int i = 0, anzahl = vKalDaten.getAnzahlBloecke(iKoll);
		  String sKoll ="";
		  buff.append("update flexkc set ");
		  String backtest = "";
		  for(i=0;i<anzahl;i++){
			  backtest = vKalDaten.getFeld(iKoll,1,i).trim();
			  String [] split = {null,null};
			  if(backtest.contains("\\")){
				split = backtest.split("\\\\");
				backtest = split[0]+"\\\\"+split[1];
			  }
			  buff.append("T" + Integer.toString(i+1) + "='" +StringTools.Escaped(vKalDaten.getFeld(iKoll,0,i).trim())+"', " );
			  buff.append("N" + Integer.toString(i+1) + "='" +backtest+"', "  );
			  buff.append("TS" + Integer.toString(i+1) + "='" +vKalDaten.getFeld(iKoll,2,i).trim()+"', "  );
			  buff.append("TD" + Integer.toString(i+1) + "='" +vKalDaten.getFeld(iKoll,3,i).trim()+"', "  );
			  buff.append("TE" + Integer.toString(i+1) + "='" +vKalDaten.getFeld(iKoll,4,i).trim()+"', "  );
		  }
		  buff.append("BELEGT" + "='"+Integer.toString(anzahl).trim()+"', "  );
		  //System.out.println("Kollege = "+iKoll);
		  if(dbKollege == 0){
			  	sKoll = ((iKoll)+1 >=10 ? Integer.toString(iKoll+1)+"BEHANDLER" : "0"+Integer.toString(iKoll+1)+"BEHANDLER");			  
		  }else{
			  	sKoll = ((dbKollege) >=10 ? Integer.toString(dbKollege)+"BEHANDLER" : "0"+Integer.toString(dbKollege)+"BEHANDLER");			  
		  }
			  

		  buff.append("BEHANDLER" + "='" +sKoll.trim()+"', "  );
		  buff.append("DATUM" + "='" +DatFunk.sDatInSQL(this.datum)+"' "  );			  

		  buff.append("where datum = '"+DatFunk.sDatInSQL(this.datum)+"' ");
		  buff.append("and behandler = '"+sKoll+"'");
		  //System.out.println(buff.toString());
		  threadStmt = buff.toString();
			try {
				this.sState = TerminFenster.getThisClass().privstmt;
				klappt = this.sState.execute(threadStmt);
				//System.out.println("Echtdaten geschrieben Erfolg: "+klappt);
				klappt = this.sState.execute("COMMIT");

				//System.out.println("Echtdaten Commit Erfolg: "+klappt);				
			}catch(SQLException ex) {
				System.out.println("von ResultSet SQLState: " + ex.getSQLState());
				System.out.println("von ResultSet ErrorCode: " + ex.getErrorCode ());System.out.println("ErrorCode: " + ex.getErrorCode ());
				System.out.println("von ResultSet ErrorMessage: " + ex.getMessage ());
				Reha.thisClass.messageLabel.setText("Entsperren misslungen");			
				TerminFenster.setLockOk(-1," Durch Fehler in SQL-Statement:" +ex.getMessage());
			}
			TerminFenster.starteUnlock();
			TerminFenster.getThisClass().setUpdateVerbot(false);
	  }
	  private void maskenrun(){
		  StringBuffer buff = new StringBuffer();
		  int i = 0, anzahl = vKalDaten.getAnzahlBloecke(iKoll);
		  String sKoll ="";
		  buff.append("update masken set ");
		  String backtest = "";
		  for(i=0;i<anzahl;i++){
			  backtest = vKalDaten.getFeld(iKoll,1,i).trim();
			  String [] split = {null,null};
			  if(backtest.contains("\\")){
				split = backtest.split("\\\\");
				backtest = split[0]+"\\\\"+split[1];
			  }
			  buff.append("T" + Integer.toString(i+1) + "='" +StringTools.Escaped(vKalDaten.getFeld(iKoll,0,i).trim())+"', " );
			  buff.append("N" + Integer.toString(i+1) + "='" +backtest+"', "  );
			  buff.append("TS" + Integer.toString(i+1) + "='" +vKalDaten.getFeld(iKoll,2,i).trim()+"', "  );
			  buff.append("TD" + Integer.toString(i+1) + "='" +vKalDaten.getFeld(iKoll,3,i).trim()+"', "  );
			  buff.append("TE" + Integer.toString(i+1) + "='" +vKalDaten.getFeld(iKoll,4,i).trim()+"', "  );
		  }
		  buff.append("BELEGT" + "='"+Integer.toString(anzahl).trim()+"', "  );
	  		sKoll = ((dbKollege) >=10 ? Integer.toString(dbKollege)+"BEHANDLER" : "0"+Integer.toString(dbKollege)+"BEHANDLER");			  
		  
			  

		  buff.append("BEHANDLER" + "='" +sKoll.trim()+"' "  );
  

		  buff.append("where art = '"+(iKoll+1)+"' ");
		  buff.append("and behandler = '"+sKoll+"'");
		  //System.out.println(buff.toString());
		  threadStmt = buff.toString();
		  TerminFenster.rechneMaske();
		  try {
			  this.sState = TerminFenster.getThisClass().privstmt;
			  klappt = this.sState.execute(threadStmt);
			  klappt = this.sState.execute("COMMIT");
		  }catch(SQLException ex) {
			  System.out.println("von ResultSet SQLState: " + ex.getSQLState());
			  System.out.println("von ResultSet ErrorCode: " + ex.getErrorCode ());System.out.println("ErrorCode: " + ex.getErrorCode ());
			  System.out.println("von ResultSet ErrorMessage: " + ex.getMessage ());
			  Reha.thisClass.messageLabel.setText("Entsperren misslungen");			
			  TerminFenster.setLockOk(-1," Durch Fehler in SQL-Statement:" +ex.getMessage());
		  }
		  TerminFenster.starteUnlock();
		  TerminFenster.getThisClass().setUpdateVerbot(false);
	  }
}
