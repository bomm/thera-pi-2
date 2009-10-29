package terminKalender;

import java.util.ArrayList;
import java.util.Vector;

import systemEinstellungen.SystemConfig;

public class BlockHandling {
	int ret = -1;
	int wasTun = -1;
	Vector vterm = null;
	int kollege;
	int spalte;
	int block;
	String[] datum = null;
	int dbBehandler;
	String[] daten = null;
	Felder datenfeld = new Felder();
	BlockHandling(int wasTun ,Vector vterm ,int kollege ,int spalte, int block,
			String[] datum,int dbBehandler,String[] daten){
		this.wasTun = wasTun;
		this.vterm = vterm;
		this.kollege = kollege;
		this.spalte = spalte;
		this.block = block;
		this.datum = datum;
		this.dbBehandler = dbBehandler;
		this.daten = daten;
		//System.out.println("In BlockhHandling Übergabewert = "+wasTun);
		datenfeld.Init(vterm);
		//System.out.println("Nach Init-BlockhHandling Übergabewert = "+wasTun);
	}
	
	public int init(){
		for(int i = 0;i<1;i++){
			if(this.wasTun==1){
				//System.out.println("in Block passt genau");
				this.ret = blockPasstGenau();
				break;
			}
			if(this.wasTun==2){
				//System.out.println("in Block oben aschließen");				
				this.ret = blockObenAnschliessen();
				break;
			}
			if(this.wasTun==3){
				//System.out.println("in Block unten aschließen");
				this.ret = blockUntenAnschliessen();
				break;
			}
			if(this.wasTun==4){
				//System.out.println("in Block ausehnen");
				this.ret = blockAusdehnen();
				break;
			}
			if(this.wasTun==5){
				//System.out.println("in Block manueller Start");
				this.ret = blockManuellStarten();
				break;
			}
			if(this.wasTun==6){
				//System.out.println("in Block Nachfolgeblock kürzen");
				this.ret = blockNachfolgeKuerzen();
				break;
			}
			if(this.wasTun==7){
				//System.out.println("in Block Vorgängerblock kürzen");
				//this.ret = blockVorblockKuerzen();
				break;
			}
			if(this.wasTun==8){
				//System.out.println("in Block Zusammenfassen");
				this.ret = blockZusammenFassen();
				break;
			}
			if(this.wasTun==10){
				//System.out.println("in Freitermin eintragen");
				this.ret = blockFreiTermin();
				break;
			}
			if(this.wasTun==11){
				//System.out.println("in Blocklöschen");
				this.ret = blockLoeschen();
				break;
			}			
			if(this.wasTun==12){
				//System.out.println("in mit Vorgänger tauschen");
				this.ret = blockTauschen(-1);
				break;
			}			
			if(this.wasTun==13){
				//System.out.println("in mit Nachfolger tauschen");
				this.ret = blockTauschen(1);
				break;
			}			
			if(this.wasTun==999){
				//System.out.println("Tag komplett löschen und auf null setzen");
				this.ret = blockAufNull();
				break;
			}			

			

		}
		return this.ret;
	}
/*****************************************/	
	private int blockPasstGenau(){
			//System.out.println("Kollege = "+this.kollege);
			datenfeld.setFeld(kollege,0,block,daten[0]);
			datenfeld.setFeld(kollege,1,block,daten[1]);
			datenfeld.setFeld(kollege,2,block,daten[2]);
			datenfeld.setFeld(kollege,3,block,daten[3]);
			datenfeld.setFeld(kollege,4,block,daten[4]);
			
		KalenderBeschreiben kbs = new KalenderBeschreiben();
		//System.out.println("Kollege="+kollege+" Datum ist gleich="+datum[spalte]+" dbBehandler="+dbBehandler);
		kbs.KalenderDaten(this.datenfeld, kollege,datum[spalte],dbBehandler);
		//TerminFenster.starteUnlock();
		TerminFenster.thisClass.terminAufnehmen(kollege,block);
		return 1;
	}
/*****************************************/	
	private int blockObenAnschliessen(){
		String [] alteDaten = {null,null,null,null,null};
		int aktBlockzahl;
			//Zuerste bisherige Blockdaten sichern
			alteDaten[0] = datenfeld.getFeld(kollege,0,block);
			alteDaten[1] = datenfeld.getFeld(kollege,1,block);
			alteDaten[2] = datenfeld.getFeld(kollege,2,block);
			alteDaten[3] = datenfeld.getFeld(kollege,3,block);
			alteDaten[4] = datenfeld.getFeld(kollege,4,block);
			String neueEndzeit = zeitFunk.MinutenZuZeit((int) (zeitFunk.MinutenSeitMitternacht(alteDaten[2])+Integer.parseInt(daten[3])) );
			int differenz = Integer.parseInt(alteDaten[3])-Integer.parseInt(daten[3]); 
			// jetzt bisherige daten mit neuen Daten überschreiben
			datenfeld.setFeld(kollege,0,block,daten[0]);
			datenfeld.setFeld(kollege,1,block,daten[1]);
			datenfeld.setFeld(kollege,2,block,daten[2]);
			datenfeld.setFeld(kollege,3,block,daten[3]);
			datenfeld.setFeld(kollege,4,block,neueEndzeit);
			//Blockzahl ermitteln und dann neuen Block einfügen
			int bloecke = block+1;
			int maxblock = datenfeld.getAnzahlBloecke(kollege);
			datenfeld.einfuegenBlock(kollege, bloecke);
			datenfeld.setAnzahlBloecke(kollege,maxblock+1);	
			// jetzt den neuen Block mit alten Daten schreiben
			datenfeld.setFeld(kollege,0,bloecke,alteDaten[0]);
			datenfeld.setFeld(kollege,1,bloecke,alteDaten[1]);
			datenfeld.setFeld(kollege,2,bloecke,datenfeld.getFeld(kollege,4,block));
			datenfeld.setFeld(kollege,3,bloecke,Integer.toString(differenz));
			datenfeld.setFeld(kollege,4,bloecke,alteDaten[4]);


		KalenderBeschreiben kbs = new KalenderBeschreiben();
		//System.out.println("Kollege="+kollege+" Datum ist gleich="+datum[spalte]+" dbBehandler="+dbBehandler);
		kbs.KalenderDaten(this.datenfeld, kollege,datum[spalte],dbBehandler);
		TerminFenster.thisClass.terminAufnehmen(kollege,block);
		//System.out.println("0="+alteDaten[0]+"1="+alteDaten[1]+"2="+alteDaten[2]+"3="+alteDaten[3]+"4="+alteDaten[4]);
		//TerminFenster.starteUnlock();
		return 1;
	}

	/*****************************************/	
	private int blockUntenAnschliessen(){
		String [] alteDaten = {null,null,null,null,null};
		int aktBlockzahl;
			//Zuerste bisherige Blockdaten sichern
			alteDaten[0] = datenfeld.getFeld(kollege,0,block);
			alteDaten[1] = datenfeld.getFeld(kollege,1,block);
			alteDaten[2] = datenfeld.getFeld(kollege,2,block);
			alteDaten[3] = datenfeld.getFeld(kollege,3,block);
			alteDaten[4] = datenfeld.getFeld(kollege,4,block);
			
			String neueEndzeit = zeitFunk.MinutenZuZeit((int) (zeitFunk.MinutenSeitMitternacht(alteDaten[4])-Integer.parseInt(daten[3])) );
			//System.out.println("neue Endzeit "+neueEndzeit );
			int differenz = Integer.parseInt(alteDaten[3])-Integer.parseInt(daten[3]); 
			//System.out.println("Zeitdifferenz "+differenz );
			// jetzt bisherige daten mit alten (korrigierten) Daten überschreiben
			datenfeld.setFeld(kollege,0,block,alteDaten[0]);
			datenfeld.setFeld(kollege,1,block,alteDaten[1]);
			datenfeld.setFeld(kollege,2,block,alteDaten[2]);
			datenfeld.setFeld(kollege,3,block,Integer.toString(differenz));
			datenfeld.setFeld(kollege,4,block,neueEndzeit);
			
			//Blockzahl ermitteln und dann neuen Block einfügen
			int bloecke = block+1;
			int maxblock = datenfeld.getAnzahlBloecke(kollege);
			datenfeld.einfuegenBlock(kollege, bloecke);
			datenfeld.setAnzahlBloecke(kollege,maxblock+1);	
			// jetzt den neuen Block mit alten Daten schreiben
			datenfeld.setFeld(kollege,0,bloecke,daten[0]);
			datenfeld.setFeld(kollege,1,bloecke,daten[1]);
			datenfeld.setFeld(kollege,2,bloecke,neueEndzeit);
			datenfeld.setFeld(kollege,3,bloecke,daten[3]);
			datenfeld.setFeld(kollege,4,bloecke,alteDaten[4]);
			


		KalenderBeschreiben kbs = new KalenderBeschreiben();
		//System.out.println("Kollege="+kollege+" Datum ist gleich="+datum[spalte]+" dbBehandler="+dbBehandler);
		kbs.KalenderDaten(this.datenfeld, kollege,datum[spalte],dbBehandler);
		TerminFenster.thisClass.terminAufnehmen(kollege,bloecke);
		//System.out.println("0="+alteDaten[0]+"1="+alteDaten[1]+"2="+alteDaten[2]+"3="+alteDaten[3]+"4="+alteDaten[4]);
		//TerminFenster.starteUnlock();
		return 1;
	}
/*****************************************/	
	private int blockAusdehnen(){
			//System.out.println("Kollege = "+this.kollege);
			datenfeld.setFeld(kollege,0,block,daten[0]);
			datenfeld.setFeld(kollege,1,block,daten[1]);
		KalenderBeschreiben kbs = new KalenderBeschreiben();
		//System.out.println("Kollege="+kollege+" Datum ist gleich="+datum[spalte]+" dbBehandler="+dbBehandler);
		kbs.KalenderDaten(this.datenfeld, kollege,datum[spalte],dbBehandler);
		TerminFenster.thisClass.setAktiverBlock(block);
		TerminFenster.thisClass.terminAufnehmen(kollege,block);
		//TerminFenster.starteUnlock();
		return 1;
	}
/*****************************************/	
	private int blockManuellStarten(){
		String [] alteDaten = {null,null,null,null,null};
		int aktBlockzahl;
			//System.out.println("Blockanzahl zu Beginn: "+datenfeld.getAnzahlBloecke(kollege));
			//Zuerste bisherige Blockdaten sichern
			alteDaten[0] = datenfeld.getFeld(kollege,0,block);//Text
			alteDaten[1] = datenfeld.getFeld(kollege,1,block);//RezNr.
			alteDaten[2] = datenfeld.getFeld(kollege,2,block);//Start
			alteDaten[3] = datenfeld.getFeld(kollege,3,block);//Dauer
			alteDaten[4] = datenfeld.getFeld(kollege,4,block);//Ende
			int dummy = (int) (zeitFunk.MinutenSeitMitternacht(daten[2])-zeitFunk.MinutenSeitMitternacht(alteDaten[2]));
			// jetzt bisherige daten mit alten (korrigierten) Daten überschreiben
			datenfeld.setFeld(kollege,0,block,alteDaten[0]);
			datenfeld.setFeld(kollege,1,block,alteDaten[1]);
			datenfeld.setFeld(kollege,2,block,alteDaten[2]);
			datenfeld.setFeld(kollege,3,block,new Integer(dummy).toString());
			String neueEndzeit = zeitFunk.MinutenZuZeit((int) (zeitFunk.MinutenSeitMitternacht(alteDaten[2])+dummy) );
			datenfeld.setFeld(kollege,4,block,neueEndzeit);
			/*
			for(int i = 0;i<5;i++){
				System.out.println("Datensatz oben = "+datenfeld.getFeld(kollege,i,block));
			}
			*/
			//Blockzahl ermitteln und dann neuen Block einfügen
			int bloecke = block+1;
			int maxblock = datenfeld.getAnzahlBloecke(kollege);
			datenfeld.einfuegenBlock(kollege, bloecke);
			datenfeld.setAnzahlBloecke(kollege,maxblock+1);	
			// jetzt den neuen Block mit alten Daten schreiben
			datenfeld.setFeld(kollege,0,bloecke,daten[0]);
			datenfeld.setFeld(kollege,1,bloecke,daten[1]);
			datenfeld.setFeld(kollege,2,bloecke,neueEndzeit);
			datenfeld.setFeld(kollege,3,bloecke,daten[3]);
			neueEndzeit = zeitFunk.MinutenZuZeit((int) (zeitFunk.MinutenSeitMitternacht(daten[2])+Integer.parseInt(daten[3])) );			
			datenfeld.setFeld(kollege,4,bloecke,neueEndzeit);
			/*
			for(int i = 0;i<5;i++){
				System.out.println("Block mitte = "+datenfeld.getFeld(kollege,i,bloecke));
			}
			System.out.println("Blockanzahl nach erster Einfügung: "+datenfeld.getAnzahlBloecke(kollege));
			*/
			//Blockzahl ermitteln und dann neuen Block einfügen
			bloecke = bloecke+1;
			maxblock++;
			datenfeld.einfuegenBlock(kollege, bloecke);
			datenfeld.setAnzahlBloecke(kollege,maxblock+1);
			
			datenfeld.setFeld(kollege,0,bloecke,alteDaten[0]);
			datenfeld.setFeld(kollege,1,bloecke,alteDaten[1]);
			datenfeld.setFeld(kollege,2,bloecke,neueEndzeit);
			int differenz = (int) (zeitFunk.MinutenSeitMitternacht(alteDaten[4])-zeitFunk.MinutenSeitMitternacht(neueEndzeit));
			datenfeld.setFeld(kollege,3,bloecke,Integer.toString(differenz));
			datenfeld.setFeld(kollege,4,bloecke,alteDaten[4]);
			/*
			for(int i = 0;i<5;i++){
				System.out.println("Block unten = "+datenfeld.getFeld(kollege,i,bloecke));
			}
			*/
			//System.out.println("Blockanzahl nach zweiter Einfügung: "+datenfeld.getAnzahlBloecke(kollege));
			//System.out.println((ArrayList) vterm.get(0)).get(kollege));
			
		
		KalenderBeschreiben kbs = new KalenderBeschreiben();
		//System.out.println("Kollege="+kollege+" Datum ist gleich="+datum[spalte]+" dbBehandler="+dbBehandler);
		kbs.KalenderDaten(this.datenfeld, kollege,datum[spalte],dbBehandler);
		TerminFenster.thisClass.terminAufnehmen(kollege,bloecke-1);
		TerminFenster.thisClass.setAktiverBlock(bloecke-1);
		return 1;
	}
/*****************************************/	
	private int blockNachfolgeKuerzen(){
		String [] alteDaten = {null,null,null,null,null};
		int aktBlockzahl;
			//System.out.println("Blockanzahl zu Beginn: "+datenfeld.getAnzahlBloecke(kollege));
			//Zuerste bisherige Blockdaten sichern
			alteDaten[0] = datenfeld.getFeld(kollege,0,block+1);//Text
			alteDaten[1] = datenfeld.getFeld(kollege,1,block+1);//RezNr.
			alteDaten[2] = datenfeld.getFeld(kollege,2,block+1);//Start
			alteDaten[3] = datenfeld.getFeld(kollege,3,block+1);//Dauer
			alteDaten[4] = datenfeld.getFeld(kollege,4,block+1);//Ende
			int dummy = (int) (zeitFunk.MinutenSeitMitternacht(daten[2])-zeitFunk.MinutenSeitMitternacht(alteDaten[2]));
			// jetzt bisherige daten mit alten (korrigierten) Daten überschreiben
			datenfeld.setFeld(kollege,0,block,daten[0]);
			datenfeld.setFeld(kollege,1,block,daten[1]);
			datenfeld.setFeld(kollege,2,block,daten[2]);
			datenfeld.setFeld(kollege,3,block,daten[3]);
			String neueEndzeit = zeitFunk.MinutenZuZeit((int) (zeitFunk.MinutenSeitMitternacht(daten[2]) +Integer.parseInt(daten[3])) );
			datenfeld.setFeld(kollege,4,block,neueEndzeit);

			datenfeld.setFeld(kollege,0,block+1,alteDaten[0]);
			datenfeld.setFeld(kollege,1,block+1,alteDaten[1]);
			datenfeld.setFeld(kollege,2,block+1,datenfeld.getFeld(kollege,4,block));
			dummy = (int) zeitFunk.MinutenSeitMitternacht(datenfeld.getFeld(kollege,4,block+1)) -
				(int) zeitFunk.MinutenSeitMitternacht(datenfeld.getFeld(kollege,2,block+1));
			datenfeld.setFeld(kollege,3,block+1,Integer.toString(dummy));
						
			
		
		KalenderBeschreiben kbs = new KalenderBeschreiben();
		//System.out.println("Vermutlich falsch------>Kollege="+kollege+" Datum ist gleich="+datum[spalte]+" dbBehandler="+dbBehandler);
		kbs.KalenderDaten(this.datenfeld, kollege,datum[spalte],dbBehandler);
		TerminFenster.thisClass.terminAufnehmen(kollege,block);
		return 1;
	}
	/*****************************************/	
	private int blockZusammenFassen(){
		//String [] alteDaten = {null,null,null,null,null};
		int aktBlockzahl;
		int [] grundDaten = TerminFenster.getThisClass().getGruppierenClipBoard();
		int startBlock,endBlock,anzahlBloecke,anzahlGesamt;
		//System.out.println("in Block-Handling"+grundDaten[0]+"/"+grundDaten[1]+"/"+grundDaten[2]+"/"+grundDaten[3]);

		if(grundDaten[0] > grundDaten[1]){
			startBlock = grundDaten[1];
			endBlock = grundDaten[0];
		}else{
			startBlock = grundDaten[0];
			endBlock = grundDaten[1];
		}

		anzahlGesamt = Integer.parseInt(datenfeld.getFeld(grundDaten[3],5,0));
		String db_datum = DatFunk.sDatInDeutsch(datenfeld.getFeld(grundDaten[3],5,4));
		String db_behandler = datenfeld.getFeld(grundDaten[3],5,2);
		int ibehandler = (db_behandler.substring(0,1).equals("0") ?
						Integer.parseInt(db_behandler.substring(1,1)) :
							Integer.parseInt(db_behandler.substring(0,2))	);
		anzahlBloecke = (endBlock - startBlock) +1;
		String termin = datenfeld.getFeld(grundDaten[3],0, grundDaten[0]);
		String reznummer = datenfeld.getFeld(grundDaten[3],1, grundDaten[0]);
		String startuhr =  datenfeld.getFeld(grundDaten[3],2, startBlock);
		String endeuhr = datenfeld.getFeld(grundDaten[3],4,endBlock);
		int startMinuten = (int) zeitFunk.ZeitDifferenzInMinuten(startuhr,
				endeuhr) ;
		String minuten = Integer.toString(startMinuten);
	
		datenfeld.setFeld(grundDaten[3],0,startBlock,termin);
		datenfeld.setFeld(grundDaten[3],1,startBlock,reznummer);
		datenfeld.setFeld(grundDaten[3],2,startBlock,startuhr );
		datenfeld.setFeld(grundDaten[3],3,startBlock,minuten);
		datenfeld.setFeld(grundDaten[3],4,startBlock,endeuhr);	
		for (int ii = endBlock; ii > startBlock; ii--){
			datenfeld.loeschenBlock(grundDaten[3],ii);
		}

		
		KalenderBeschreiben kbs = new KalenderBeschreiben();
		kbs.KalenderDaten(this.datenfeld,grundDaten[3] ,db_datum,ibehandler);

		return 1;
	}	
	/*****************************************/	
	private int blockFreiTermin(){
		String [] alteDaten = {null,null,null,null,null};
		
		String db_datum = DatFunk.sDatInDeutsch(datenfeld.getFeld(kollege,5,4));
		String db_behandler = datenfeld.getFeld(kollege,5,2);
		int ibehandler = (db_behandler.substring(0,1).equals("0") ?
				Integer.parseInt(db_behandler.substring(1,1)) :
					Integer.parseInt(db_behandler.substring(0,2))	);		
		
		alteDaten[0] = datenfeld.getFeld(kollege,0,block);//Text
		alteDaten[1] = datenfeld.getFeld(kollege,1,block);//RezNr.
		alteDaten[2] = datenfeld.getFeld(kollege,2,block);//Start
		alteDaten[3] = datenfeld.getFeld(kollege,3,block);//Dauer
		alteDaten[4] = datenfeld.getFeld(kollege,4,block);//Ende

		datenfeld.setFeld(kollege,1,block,"@FREI");
		TerminFenster.getThisClass().setDatenSpeicher(alteDaten);
		
		KalenderBeschreiben kbs = new KalenderBeschreiben();
		kbs.KalenderDaten(this.datenfeld,kollege ,db_datum,ibehandler);
		
	return 1;	
	}
	/*****************************************/	
	private int blockLoeschen(){
		String [] alteDaten = {null,null,null,null,null};
		String test1="";
		String test2="";
		String db_datum = DatFunk.sDatInDeutsch(datenfeld.getFeld(kollege,5,4));
		String db_behandler = datenfeld.getFeld(kollege,5,2);
		int ibehandler = (db_behandler.substring(0,1)=="0" ?
				Integer.parseInt(db_behandler.substring(1,1)) :
					Integer.parseInt(db_behandler.substring(0,2))	);		
		
		int maxblock = Integer.parseInt(datenfeld.getFeld(kollege,5,0))-1;
		
		alteDaten[0] = datenfeld.getFeld(kollege,0,block);//Text
		alteDaten[1] = datenfeld.getFeld(kollege,1,block);//RezNr.
		alteDaten[2] = datenfeld.getFeld(kollege,2,block);//Start
		alteDaten[3] = datenfeld.getFeld(kollege,3,block);//Dauer
		alteDaten[4] = datenfeld.getFeld(kollege,4,block);//Ende
		TerminFenster.getThisClass().setDatenSpeicher(alteDaten);
		String termin = "";
		String reznummer = "";
		int startBlock = block;
		int endBlock = block;
		
		if(block > 0){
			test1 = datenfeld.getFeld(kollege,0,block-1);//Text
			test2 = datenfeld.getFeld(kollege,1,block-1);//Text
			if( test1.trim().equals("") && test2.trim().equals("") ){
				startBlock= block-1;
				alteDaten[2] = datenfeld.getFeld(kollege,2,block-1);//Start
			}
		}
		if(block < maxblock){
			test1 = datenfeld.getFeld(kollege,0,block+1);//Text
			test2 = datenfeld.getFeld(kollege,1,block+1);//Text
			if( test1.trim().equals("") && test2.trim().equals("") ){
				endBlock= block+1;
				alteDaten[4] = datenfeld.getFeld(kollege,4,block+1);//Ende
			}
		}
		datenfeld.setFeld(kollege,0,startBlock,"");
		datenfeld.setFeld(kollege,1,startBlock,"");
		datenfeld.setFeld(kollege,2,startBlock,alteDaten[2]);		
		int dauer = (int) zeitFunk.ZeitDifferenzInMinuten(alteDaten[2], alteDaten[4]);
		String sdauer = Integer.toString(dauer);
		datenfeld.setFeld(kollege,3,startBlock,sdauer);		
		datenfeld.setFeld(kollege,4,startBlock,alteDaten[4]);		
		
		for (int ii = endBlock; ii > startBlock; ii--){
			datenfeld.loeschenBlock(kollege,ii);
		}
		//System.out.println("Aktiver Block 0 = "+TerminFenster.thisClass.getAktiverBlock()[0]);
		//System.out.println("Aktiver Block 2 = "+TerminFenster.thisClass.getAktiverBlock()[2]);
		//System.out.println("Block in LöschenBlock = "+block);

		KalenderBeschreiben kbs = new KalenderBeschreiben();
		kbs.KalenderDaten(this.datenfeld,kollege ,db_datum,ibehandler);
		
	return 1;	
	}

	private int blockTauschen(int richtung){
		// -1 = mit Vorgänger
		// +1 = mit Nachfolger
		String[][] tauschTermine = {{null,null,null,null,null},{null,null,null,null,null}};
		int [] bloecke = {block,block+richtung}; 
		String db_datum = DatFunk.sDatInDeutsch(datenfeld.getFeld(kollege,5,4));
		String db_behandler = datenfeld.getFeld(kollege,5,2);
		int ibehandler = (db_behandler.substring(0,1).equals("0") ?
				Integer.parseInt(db_behandler.substring(1,1)) :
					Integer.parseInt(db_behandler.substring(0,2))	);		

		for(int x = 0;x<2;x++){
			for(int y = 0;y<5;y++){
				tauschTermine[x][y] = datenfeld.getFeld(kollege,y,bloecke[x]);//Text
 			}
		}
		if(richtung < 0){
			// mit Vorgängerblock tauschen
			//bisheriger Vorgänger
			datenfeld.setFeld(kollege,0,bloecke[1],tauschTermine[0][0]);
			datenfeld.setFeld(kollege,1,bloecke[1],tauschTermine[0][1]);
			datenfeld.setFeld(kollege,2,bloecke[1],tauschTermine[1][2]);
			datenfeld.setFeld(kollege,3,bloecke[1],tauschTermine[0][3]);
			/// rechnen aus startzeit und dauer 
			int StartAktuell = (int) zeitFunk.MinutenSeitMitternacht(tauschTermine[1][2]);
			StartAktuell = StartAktuell + Integer.parseInt(tauschTermine[0][3]);
			String EndeUhr = zeitFunk.MinutenZuZeit(StartAktuell);		
			datenfeld.setFeld(kollege,4,bloecke[1],EndeUhr);
			/********/
			//derzeit aktueller
			datenfeld.setFeld(kollege,0,bloecke[0],tauschTermine[1][0]);
			datenfeld.setFeld(kollege,1,bloecke[0],tauschTermine[1][1]);
			datenfeld.setFeld(kollege,2,bloecke[0],EndeUhr);
			datenfeld.setFeld(kollege,3,bloecke[0],tauschTermine[1][3]);
			datenfeld.setFeld(kollege,4,bloecke[0],tauschTermine[0][4]);
			KalenderBeschreiben kbs = new KalenderBeschreiben();
			kbs.KalenderDaten(this.datenfeld,kollege ,db_datum,ibehandler);
			//TerminFenster.thisClass.neuerBlockAktiv(bloecke[0]);
			return 1;
		}else{
			datenfeld.setFeld(kollege,0,bloecke[0],tauschTermine[1][0]);
			datenfeld.setFeld(kollege,1,bloecke[0],tauschTermine[1][1]);
			datenfeld.setFeld(kollege,2,bloecke[0],tauschTermine[0][2]);
			datenfeld.setFeld(kollege,3,bloecke[0],tauschTermine[1][3]);
			/// rechnen aus startzeit und dauer 
			int StartAktuell = (int) zeitFunk.MinutenSeitMitternacht(tauschTermine[0][2]);
			StartAktuell = StartAktuell + Integer.parseInt(tauschTermine[1][3]);
			String EndeUhr = zeitFunk.MinutenZuZeit(StartAktuell);		
			datenfeld.setFeld(kollege,4,bloecke[0],EndeUhr);
			/********/
			//derzeit aktueller
			datenfeld.setFeld(kollege,0,bloecke[1],tauschTermine[0][0]);
			datenfeld.setFeld(kollege,1,bloecke[1],tauschTermine[0][1]);
			datenfeld.setFeld(kollege,2,bloecke[1],EndeUhr);
			datenfeld.setFeld(kollege,3,bloecke[1],tauschTermine[0][3]);
			datenfeld.setFeld(kollege,4,bloecke[1],tauschTermine[1][4]);
			KalenderBeschreiben kbs = new KalenderBeschreiben();
			kbs.KalenderDaten(this.datenfeld,kollege ,db_datum,ibehandler);
			//TerminFenster.thisClass.neuerBlockAktiv(bloecke[1]); 
			return 1;
		}
		//return -1;
	}
	private int blockAufNull(){
		//System.out.println("Kollege = "+this.kollege);
		datenfeld.setFeld(kollege,0,0,"");
		datenfeld.setFeld(kollege,1,0,"@FREI");
		datenfeld.setFeld(kollege,2,0,SystemConfig.KalenderUmfang[0]);
		long dauer = SystemConfig.KalenderMilli[1] - SystemConfig.KalenderMilli[0]; 
		datenfeld.setFeld(kollege,3,0,Long.toString(dauer));
		datenfeld.setFeld(kollege,4,0,SystemConfig.KalenderUmfang[1]);
		while(datenfeld.getAnzahlBloecke(kollege)> 1){
			datenfeld.loeschenBlock(kollege, 1);
		}
		datenfeld.setAnzahlBloecke(kollege,1);
	KalenderBeschreiben kbs = new KalenderBeschreiben();
	//System.out.println("Kollege="+kollege+" Datum ist gleich="+datum[spalte]+" dbBehandler="+dbBehandler);
	kbs.KalenderDaten(this.datenfeld, kollege,datum[spalte],dbBehandler);
			return 1;
}

}
