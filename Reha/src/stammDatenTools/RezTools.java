package stammDatenTools;

import hauptFenster.Reha;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.jdesktop.swingworker.SwingWorker;

import sqlTools.SqlInfo;
import systemEinstellungen.SystemConfig;
import systemEinstellungen.SystemPreislisten;
import systemTools.StringTools;
import terminKalender.DatFunk;

public class RezTools {
	public static boolean mitJahresWechsel(String datum){
		boolean ret = false;
		try{
			if( (Integer.parseInt(datum.substring(6))-Integer.parseInt(SystemConfig.aktJahr)) < 0){
				ret = true;
			}
		}catch(Exception ex){
			
		}
		return ret;
	}
	
	public static Vector<ArrayList<?>> holePosUndAnzahlAusRezept(String xreznr){
		Vector<ArrayList<?>> xvec = new Vector<ArrayList<?>>();
		ArrayList<String> positionen = new ArrayList<String>();
		ArrayList<String> doppeltest = new ArrayList<String>();
		ArrayList<Integer>anzahl = new ArrayList<Integer>();
		
		Vector<String> rezvec = null;
		
		rezvec = SqlInfo.holeSatz("verordn", "art_dbeh1,art_dbeh2,art_dbeh3,art_dbeh4,"+
				"anzahl1,anzahl2,anzahl3,anzahl4", "rez_nr='"+xreznr+"'", Arrays.asList(new String[] {}));
		for(int i = 0; i < 4; i++){
			if(! rezvec.get(i).equals("0")){
				if(i==0){
					positionen.add( holePosAusIdUndRezNr( rezvec.get(i), xreznr) );
					anzahl.add(Integer.parseInt(rezvec.get(i+4)));
					doppeltest.add(rezvec.get(i));
				}else if(i>=1){
					doppeltest.add(rezvec.get(i));					

					if(rezvec.indexOf(rezvec.get(i))!=i){ //Doppelbehandlung, wenn die HMPos vor i schon mal aufgeführt ist. Hintergrund: Doppelbehandlungen müssen nicht auf i=0 und i=1 sein
						anzahl.set(0, Integer.parseInt(rezvec.get(i+4))+Integer.parseInt(rezvec.get(rezvec.indexOf(rezvec.get(i))+4)));	
					}else{
						positionen.add( holePosAusIdUndRezNr( rezvec.get(i), xreznr) );
						anzahl.add(Integer.parseInt(rezvec.get(i+4)));
					}
		
					//positionen.add( holePosAusIdUndRezNr( rezvec.get(i), xreznr) );
					//anzahl.add(Integer.parseInt(rezvec.get(i+4)));
				}else{
					doppeltest.add(rezvec.get(i));
					positionen.add( holePosAusIdUndRezNr( rezvec.get(i),xreznr) );
					anzahl.add(Integer.parseInt(rezvec.get(i+4)));
				}			
			}
		}
		xvec.add((ArrayList<?>)positionen.clone());
		xvec.add((ArrayList<?>)anzahl.clone());
		xvec.add((ArrayList<?>)doppeltest.clone()); //zum Test ausgeschaltet
		return xvec;
		
	}
	public static Vector<ArrayList<?>> holePosUndAnzahlAusTerminen(String xreznr){
		Vector<ArrayList<?>> xvec = new Vector<ArrayList<?>>();

		Vector<String> rezvec = SqlInfo.holeSatz("verordn", "termine,pos1,pos2,pos3,"+
				"pos4", "rez_nr='"+xreznr+"'", Arrays.asList(new String[] {}));
		Vector<String> termvec = holeEinzelZiffernAusRezept(null,rezvec.get(0));

		boolean doppelbeh = false;
		boolean durchlaufen = false;
		boolean trigger = false;
		boolean testen = true;
		int doppelbehindex = -1;
		String doppelbehpos = "";
		
		ArrayList<String> positionen = new ArrayList<String>();
		String behandlungen = null;
		String[] einzelbehandlung = null;
		ArrayList<Integer>anzahl = new ArrayList<Integer>();
		
		String aktpos = "";
		for(int i = 1; i < 5; i++ ){

			if(rezvec.get(i).trim().equals("")){
				break;
			}else if((i == 1)){
				positionen.add(String.valueOf(rezvec.get(i)));
				anzahl.add(0);
				aktpos = String.valueOf(rezvec.get(i));
				
			}else{
				if(rezvec.get(i).equals(aktpos)){
					doppelbeh = true;
					if(testen){
						doppelbehindex = Integer.valueOf(i)-1;
						doppelbehpos = String.valueOf(rezvec.get(i));
						testen = false;
					}
				}
				positionen.add(String.valueOf(rezvec.get(i)));
				anzahl.add(0);
				aktpos = String.valueOf(rezvec.get(i));
			}
		}
		int index = -1;
		
		for(int i = 0; i < termvec.size();i++){
			behandlungen = termvec.get(i);
			if(! behandlungen.equals("")){
				einzelbehandlung = behandlungen.split(",");
				durchlaufen = false;
				trigger = false;
				for(int i2 = 0; i2 < einzelbehandlung.length;i2++){
					if(doppelbeh){
						index = positionen.indexOf(einzelbehandlung[i2]);
						if( (einzelbehandlung[i2].equals(doppelbehpos)) && (!durchlaufen)){
							trigger = true;
							anzahl.set(index, anzahl.get(index)+1);
						}else if((einzelbehandlung[i2].equals(doppelbehpos)) && (durchlaufen)){
							anzahl.set(positionen.lastIndexOf(einzelbehandlung[i2]), anzahl.get(positionen.lastIndexOf(einzelbehandlung[i2]))+1);
						}else{
							anzahl.set(index, anzahl.get(index)+1);
						}
						if(trigger){
							durchlaufen = true;
							trigger = false;
						}
					}else{
						index = positionen.indexOf(einzelbehandlung[i2]); 
						anzahl.set(index, anzahl.get(index)+1);
					}
				}
			}
		}	
		for(int i = (anzahl.size()-1); i >= 0; i--){
			if(anzahl.get(i)==0){
				anzahl.remove(i);
				positionen.remove(i);
			}	
		}	
		System.out.println(positionen);
		System.out.println(anzahl);
		xvec.add((ArrayList<?>)positionen.clone());
		xvec.add((ArrayList<?>)anzahl.clone());
		return xvec;
	}

	public static Vector<ArrayList<?>> X_holePosUndAnzahlAusTerminen(String xreznr){
		Vector<ArrayList<?>> xvec = new Vector<ArrayList<?>>();
		Vector<String> termvec = holeEinzelZiffernAusRezept(xreznr,"");
		String behandlungen = null;
		String[] einzelbehandlung = null;
		ArrayList<String> positionen = new ArrayList<String>();
		ArrayList<Integer>anzahl = new ArrayList<Integer>();
		int trefferbei = -1;
		for(int i = 0; i < termvec.size();i++){
			behandlungen = termvec.get(i);
			if(! behandlungen.equals("")){
				einzelbehandlung = behandlungen.split(",");
				//Scheiße weil Doppelbehandlungen zusammengefaßt werden
				//Wird verwendet von: TerminFenster.terminBestaetigen()
				//dort werden Doppelbehandlungen nachträglich erkannt und korrigiert
				for(int i2 = 0; i2 < einzelbehandlung.length;i2++){
					trefferbei = positionen.indexOf(einzelbehandlung[i2]);
					if(trefferbei >= 0){
						anzahl.set(trefferbei,anzahl.get(trefferbei)+1 );
					}else{
						positionen.add(einzelbehandlung[i2]);
						anzahl.add(1);
					}
				}
			}
		}
		xvec.add((ArrayList<?>)positionen.clone());
		xvec.add((ArrayList<?>)anzahl.clone());
		return xvec;
	}
	@SuppressWarnings("unchecked")
	public static Vector<String> holeEinzelZiffernAusRezept(String xreznr,String termine){
		Vector<String> xvec = null;
		Vector<String> retvec = new Vector<String>();
		String terms = null;
		if(termine.equals("")){
			xvec = SqlInfo.holeSatz("verordn", "termine,pat_intern", "rez_nr='"+xreznr+"'", Arrays.asList(new String[] {}));			
			if(xvec.size()==0){
				return (Vector<String>)retvec.clone();
			}else{
				terms = (String) xvec.get(0);	
			}
		}else{
			terms = termine;
		}
		if(terms==null){
			return (Vector<String>)retvec.clone();
		}
		if(terms.equals("")){
			return (Vector<String>)retvec.clone();
		}
		String[] tlines = terms.split("\n");
		int lines = tlines.length;

		for(int i = 0;i<lines;i++){
			String[] terdat = tlines[i].split("@");
			//int ieinzel = terdat.length;
			retvec.add(terdat[3].trim());
		}
		return (Vector<String>)retvec.clone();
	}
	
	public static Object[] holeTermineAnzahlUndLetzter(String termine){
		Object[] retobj = {null,null};
		try{
			String[] tlines = termine.split("\n");
			int lines = tlines.length;
			if(lines <= 0){
				retobj[0] = 0;
				retobj[1] = null;
				return retobj;
			}
			String[] terdat = null;
			terdat = tlines[lines-1].split("@");
			retobj[0] = Integer.valueOf(lines);
			retobj[1] = String.valueOf(terdat[0]);
		}catch(Exception ex){
			
		}
		return retobj;
	}
	
	@SuppressWarnings("unchecked")
	public static Vector<String> holeEinzelTermineAusRezept(String xreznr,String termine){
		Vector<String> xvec = null;
		Vector<String> retvec = new Vector<String>();
		String terms = null;
		if(termine.equals("")){
			xvec = SqlInfo.holeSatz("verordn", "termine,pat_intern", "rez_nr='"+xreznr+"'", Arrays.asList(new String[] {}));			
			if(xvec.size()==0){
				return (Vector<String>)retvec.clone();
			}else{
				terms = (String) xvec.get(0);	
			}
		}else{
			terms = termine;
		}
		if(terms==null){
			return (Vector<String>)retvec.clone();
		}
		if(terms.equals("")){
			return (Vector<String>)retvec.clone();
		}
		String[] tlines = terms.split("\n");
		int lines = tlines.length;
		String[] terdat = null;
		for(int i = 0;i<lines;i++){
			terdat = tlines[i].split("@");
			//int ieinzel = terdat.length;
			retvec.add((terdat[0].trim().equals("") ? "  .  .    " : String.valueOf(terdat[0])));
		}
		Comparator<String> comparator = new Comparator<String>() {
			public int compare(String s1, String s2) {
		        String strings1 = DatFunk.sDatInSQL(s1);
		        String strings2 = DatFunk.sDatInSQL(s2);
		        return strings1.compareTo(strings2);
		    }
		};	
		Collections.sort(retvec,comparator);
		return (Vector<String>)retvec.clone();
	}
	public static String holePosAusIdUndRezNr(String id,String reznr){
		String diszi = RezTools.putRezNrGetDisziplin(reznr);
		String preisgruppe = SqlInfo.holeEinzelFeld("select preisgruppe from verordn where rez_nr='"+reznr+"' LIMIT 1");
		Vector<Vector<String>> preisvec = (Vector<Vector<String>>) SystemPreislisten.hmPreise.get(diszi).get(Integer.parseInt(preisgruppe)-1);
		String pos = RezTools.getPosFromID(id, preisgruppe, preisvec) ;
		return (pos==null ? "" : pos);
	}
/********************************************************************************/
	@SuppressWarnings("unchecked")
	public static Vector<Vector<String>> macheTerminVector(String termine){
		String[] tlines = termine.split("\n");
		int lines = tlines.length;
		////System.out.println("Anzahl Termine = "+lines);
		Vector<Vector<String>> tagevec = new Vector<Vector<String>>();
		Vector<String> tvec = new Vector<String>();
		String[] terdat = null;
		for(int i = 0;i<lines;i++){
			terdat = tlines[i].split("@");
			int ieinzel = terdat.length;
			////System.out.println("Anzahl Splits = "+ieinzel);
			tvec.clear();
			for(int y = 0; y < ieinzel;y++){
				if(y==0){
					tvec.add(String.valueOf((terdat[y].trim().equals("") ? "  .  .    " : terdat[y])));
					if(i==0){
						SystemConfig.hmAdrRDaten.put("<Rerstdat>",String.valueOf((terdat[y].trim().equals("") ? "  .  .    " : terdat[y])));						
					}
				}else{
					tvec.add(String.valueOf(terdat[y]));					
				}
				////System.out.println("Feld "+y+" = "+terdat[y]);	
			}
			////System.out.println("Termivector = "+tvec);
			tagevec.add((Vector<String>)tvec.clone());
		}
		if(tagevec.size() > 0 ){
			Comparator<Vector<String>> comparator = new Comparator<Vector<String>>() {
				@Override
				public int compare(Vector<String> o1, Vector<String> o2) {
					String s1 = (String)o1.get(4);
					String s2 = (String)o2.get(4);
					return s1.compareTo(s2);
				}
			};
			Collections.sort(tagevec,comparator);

		}
		return (Vector<Vector<String>>)tagevec.clone();
		
	}
/********************************************************************************/
	public static boolean zweiPositionenBeiHB(String disziplin,String preisgruppe){
		int pg = Integer.parseInt(preisgruppe)-1;
		if(SystemPreislisten.hmHBRegeln.get(disziplin).get(pg).get(2).trim().equals("") &&
				SystemPreislisten.hmHBRegeln.get(disziplin).get(pg).get(3).trim().equals("")	){
			return false;
		}
		return true;
	}
	public static boolean keineWeggebuehrBeiHB(String disziplin,String preisgruppe){
		int pg = Integer.parseInt(preisgruppe)-1;
		if(SystemPreislisten.hmHBRegeln.get(disziplin).get(pg).get(2).trim().equals("") &&
				SystemPreislisten.hmHBRegeln.get(disziplin).get(pg).get(3).trim().equals("")	){
			return true;
		}
		return false;
	}
	
/********************************************************************************/

	public static String getLangtextFromID(String id,String preisgruppe,Vector<Vector<String>> vec){
		int lang = vec.size(),i;
		int idpos = vec.get(0).size()-1;
		String ret = "kein Lantext vorhanden";
		for(i = 0; i < lang;i++){
			if( vec.get(i).get(idpos).equals(id)){
				ret = vec.get(i).get(0).toString();
				break;
			}
		}
		return ret;
	}

	public static String getPreisAktFromID(String id,String preisgruppe,Vector<Vector<String>> vec){
		int lang = vec.size(),i;
		int idpos = vec.get(0).size()-1;
		String ret = "0.00";
		for(i = 0; i < lang;i++){
			if( vec.get(i).get(idpos).equals(id)){
				ret = vec.get(i).get(3).toString();
				break;
			}
		}
		return ret;
	}
	public static String getPreisAltFromID(String id,String preisgruppe,Vector<Vector<String>> vec){
		int lang = vec.size(),i;
		int idpos = vec.get(0).size()-1;
		String ret = "0.00";
		for(i = 0; i < lang;i++){
			if( vec.get(i).get(idpos).equals(id)){
				ret = vec.get(i).get(4).toString();
				break;
			}
		}
		return ret;
	}
/***************************************/
	public static String getPreisAktFromPos(String pos,String preisgruppe,Vector<Vector<String>> vec){
		try{
		int lang = vec.size(),i;
		String ret = "0.00";
		for(i = 0; i < lang;i++){
			if( vec.get(i).get(2).equals(pos)){
				ret = vec.get(i).get(3).toString();
				break;
			}
		}
		return ret;
		}catch(Exception ex){
			ex.printStackTrace();
			return "0.00";
		}
	}
	public static String getPreisAltFromPos(String pos,String preisgruppe,Vector<Vector<String>> vec){
		int lang = vec.size(),i;
		String ret = "0.00";
		for(i = 0; i < lang;i++){
			if( vec.get(i).get(1).equals(pos)){
				ret = vec.get(i).get(4).toString();
				break;
			}
		}
		return ret;
	}
	
/***************************************/	
	public static String getIDFromPos(String pos,String preisgruppe,Vector<Vector<String>> vec){
		int lang = vec.size(),i;
		int idpos = vec.get(0).size()-1;
		String ret = "-1";
		for(i = 0; i < lang;i++){
			if( vec.get(i).get(2).equals(pos)){
				ret = vec.get(i).get(idpos).toString();
				break;
			}
		}
		return ret;
	}
	public static String getIDFromPosX(String pos,String preisgruppe,String disziplin){
		Vector<Vector<String>> vec = holePreisVector(disziplin,Integer.parseInt(preisgruppe)-1);
		int lang = vec.size(),i;
		int idpos = vec.get(0).size()-1;
		String ret = "-1";
		for(i = 0; i < lang;i++){
			if( vec.get(i).get(2).equals(pos)){
				ret = vec.get(i).get(idpos).toString();
				break;
			}
		}
		return ret;
	}
	
	public static String getPosFromID(String id,String preisgruppe,Vector<Vector<String>> vec){
		int lang = vec.size(),i;
		int idpos = vec.get(0).size()-1;
		String ret = "";
		for(i = 0; i < lang;i++){
			if( vec.get(i).get(idpos).equals(id)){
				ret = vec.get(i).get(2).toString();
				break;
			}
		}
		return ret;
	}

	public static String getKurzformFromID(String id,Vector<Vector<String>> vec){
		int lang = vec.size(),i;
		int idpos = vec.get(0).size()-1;
		String ret = "";
		for(i = 0; i < lang;i++){
			if(vec.get(i).get(idpos).equals(id)){
				ret = vec.get(i).get(1).toString();
				break;
			}
		}
		return ret;
	}
	public static String getKurzformFromPos(String pos,String preisgruppe,Vector<Vector<String>> vec){
		int lang = vec.size(),i;
		//int suchenin = (Integer.parseInt(preisgruppe)*4)-2;
		String ret = "";
		for(i = 0; i < lang;i++){
			if(vec.get(i).get(2).trim().equals(pos.trim()) && (!vec.get(i).get(1).equals("Isokin")) ){
				ret = vec.get(i).get(1).toString();
				break;
			}
		}
		return ret;
	}
	public static Object[] getKurzformUndIDFromPos(String pos,String preisgruppe,Vector<Vector<String>> vec){
		int lang = vec.size(),i;
		//int suchenin = (Integer.parseInt(preisgruppe)*4)-2;
		int idpos = vec.get(0).size()-1;
		Object[] retobj = {"",""};
		for(i = 0; i < lang;i++){
			if(vec.get(i).get(2).equals(pos) && (!vec.get(i).get(1).equals("Isokin"))){
				retobj[0] = vec.get(i).get(1).toString();
				retobj[1] = vec.get(i).get(idpos).toString();
				break;
			}
		}
		return retobj.clone();
	}

	public static String getIDFromKurzform(String kurzform,Vector<Vector<String>> vec){
		int lang = vec.size(),i;
		int idpos = vec.get(0).size()-1;
		String ret = "";
		for(i = 0; i < lang;i++){
			if(vec.get(i).get(1).equals(kurzform)){
				ret = vec.get(i).get(idpos).toString(); 
				break;
			}
		}
		return ret;
	}

	public static Vector<Vector<String>> holePreisVector(String disziplin,int preisgruppe){
		if(disziplin.startsWith("KG")){
			//return  (Vector<Vector<String>>)ParameterLaden.vKGPreise;
			return SystemPreislisten.hmPreise.get("Physio").get(preisgruppe);			
		}else if(disziplin.startsWith("MA")){
			//return  (Vector<Vector<String>>)ParameterLaden.vMAPreise;
			return SystemPreislisten.hmPreise.get("Massage").get(preisgruppe);			
		}else if(disziplin.startsWith("ER")){
			//return  (Vector<Vector<String>>)ParameterLaden.vERPreise;
			return SystemPreislisten.hmPreise.get("Ergo").get(preisgruppe);
		}else if(disziplin.startsWith("LO")){
			//return  (Vector<Vector<String>>)ParameterLaden.vLOPreise;
			return SystemPreislisten.hmPreise.get("Logo").get(preisgruppe);
		}else if(disziplin.startsWith("RH")){
			//return  (Vector<Vector<String>>)ParameterLaden.vRHPreise;
			return SystemPreislisten.hmPreise.get("Reha").get(preisgruppe);
		}else if(disziplin.startsWith("PO")){
			//return  (Vector<Vector<String>>)ParameterLaden.vRHPreise;
			return SystemPreislisten.hmPreise.get("Podo").get(preisgruppe);
		}
		return null;
	}
	
	/*	
	class ZuzahlModell{
		public int gesamtZahl;
		public boolean allefrei;
		public boolean allezuzahl;
		public boolean anfangfrei;
		public int teil1;
		public int teil2;
		public boolean hausbes;
		public boolean mithausbes;
		public ZuzahlModell(int gesamt,boolean allefrei){
			
		}
	}
*/
	public static int testeRezGebArt(boolean testefuerbarcode,boolean hintergrund,String srez,String termine){
		int iret = 0;
		Vector<String> vAktTermine = null;
		boolean bTermine = false;
		//int iTermine = -1;
		boolean bMitJahresWechsel = false;
		ZuzahlModell zm = new ZuzahlModell();
		//Vector<String> patvec = SqlInfo.holeSatz("pat5", "geboren,jahrfrei", "pat_intern='"+xvec.get(1)+"'", Arrays.asList(new String[] {}));
		//String patGeboren = datFunk.sDatInDeutsch(patvec.get(0));
		//String patJahrfrei = datFunk.sDatInDeutsch(patvec.get(0));
		//
		
		
		//1. Schritt haben wir bereits Termineintr�ge die man auswerten kann
		if( (vAktTermine = holeEinzelTermineAusRezept("",termine)).size() > 0 ){
			// Es gibt Termine in der Tabelle
			bTermine = true;
			//iTermine = vAktTermine.size();
			if( ((String)vAktTermine.get(0)).substring(6).equals(SystemConfig.vorJahr)){
				bMitJahresWechsel = true;
			}
		}

		//System.out.println(vAktTermine);
		for(int i = 0;i < 1;i++){

			if(Integer.parseInt(((String)Reha.thisClass.patpanel.vecaktrez.get(63))) <= 0){
				// Kasse erfordert keine Zuzahlung
				//System.out.println("Kasse erfordert keine Zuzahlung");
				zm.allefrei = true;
				iret = 0;
				break;
			}
			if(Integer.parseInt(((String)Reha.thisClass.patpanel.vecaktrez.get(39))) == 1){
				// Hat bereits bezahlt normal behandeln (zzstatus == 1)
				//System.out.println("Hat bereits bezahlt normal behandeln (zzstatus == 1)");
				zm.allezuzahl = true;
				iret = 2;
				//break;
			}

			/************************ Jetzt der Ober-Scheißdreck für den Achtzehner-Test***********************/
			if((boolean) ((String)Reha.thisClass.patpanel.vecaktrez.get(60)).equals("T")){
				// Es ist ein unter 18 Jahre Test notwendig
				//System.out.println("Es ist ein unter 18 Jahre Test notwendig");
				if(bTermine){
					
					int [] test = ZuzahlTools.terminNachAchtzehn(vAktTermine,DatFunk.sDatInDeutsch((String)Reha.thisClass.patpanel.patDaten.get(4))); 
					if( test[0] > 0 ){
						//mu� zuzahlen
						zm.allefrei = false;
						if(test[1] > 0){
							zm.allefrei = false;
							zm.allezuzahl = false;
							zm.anfangfrei = true;
							zm.teil1 = test[1];
							//System.out.println("Splitten frei für "+test[1]+" Tage, bezahlen für "+(maxAnzahl()-test[1]));
							iret = 1;
						}else{
							zm.allezuzahl = true;
							zm.teil1 = test[1];
							//System.out.println("Jeden Termin bezahlen insgesamt bezahlen für "+(maxAnzahl()-test[1]));
							iret = 2;
						}
					}else{
						//Voll befreit
						//System.out.println("Frei für "+test[1]+" Tage - also alle");
						zm.allefrei = true;
						iret = 0;
					}
				}else{
					//Es stehen keine Termine für Analyse zur Verfügung also muß das Fenster für manuelle Eingabe geöffnet werden!!
					String geburtstag = DatFunk.sDatInDeutsch(Reha.thisClass.patpanel.patDaten.get(4));
					String stichtag = DatFunk.sHeute().substring(0,6)+Integer.valueOf(Integer.valueOf(SystemConfig.aktJahr)-18).toString();
					if(DatFunk.TageDifferenz(geburtstag ,stichtag) >= 0 ){
						//System.out.println("Normale Zuzahlung....");
						zm.allefrei = false;
						zm.allezuzahl = true;
						iret = 2;
					}else{
						//System.out.println("Alle Frei....");						
						zm.allefrei = true;
						zm.allezuzahl = false;
						iret = 0;
					}
				}
				//iret = 1;
				break;
			}

			/************************ Keine Befreiung Aktuell und keine Vorjahr (Normalfall************************/
			if((boolean) ((String)Reha.thisClass.patpanel.patDaten.get(30)).equals("F") && 
					(((String)Reha.thisClass.patpanel.patDaten.get(69)).trim().equals("")) ){
				// Es liegt weder eine Befreiung für dieses noch für letztes Jahr vor.
				// Standard
				//System.out.println("Es liegt weder eine Befreiung für dieses noch für letztes Jahr vor.");
				iret = 2;
				break;
			}
			/************************ Aktuell Befreit und im Vorjahr auch befreit************************/			
			if((boolean) ((String)Reha.thisClass.patpanel.patDaten.get(30)).equals("T") && 
					(!((String)Reha.thisClass.patpanel.patDaten.get(69)).equals("")) ){
				// Es liegt eine Befreiung vor und im Vorjahr ebenfenfalls befreit
				//System.out.println("Es liegt eine Befreiung vor und im Vorjahr ebenfenfalls befreit");
				iret = 0;
				break;
			}
			/************************ aktuell Nicht frei, Vorjahr frei************************/
			if((boolean) (((String)Reha.thisClass.patpanel.patDaten.get(30)).equals("F")) && 
					(!((String)Reha.thisClass.patpanel.patDaten.get(69)).equals("")) ){
				//System.out.println("aktuell Nicht frei, Vorjahr frei");
				if(!bMitJahresWechsel){//Alle Termine aktuell
					iret = 2;
				}else{// es gibt Termine im Vorjahr
					Object[] obj = JahresWechsel(vAktTermine,SystemConfig.vorJahr);
					if(!(Boolean)obj[0]){// alle Termine waren im Vorjahr 
						//System.out.println("1 - Termine aus dem Vorjahr(frei) = "+obj[1]+" Termine aus diesem Jahr(Zuzahlung) = "+obj[2]);
						if(vAktTermine.size() < maxAnzahl()){
							String meldung = "<html>Während der Befreiung wurden <b>"+Integer.toString(vAktTermine.size())+"  von "+
							Integer.toString(maxAnzahl())+" Behandlungen</b> durchgeführt!<br>"+
									"Rezeptgebühren müssen also noch für <b>"+Integer.toString(maxAnzahl()-vAktTermine.size())+" Behandlungen</b> entrichtet werden.<br>"+
									"<br><br>Ist das korrekt?<br><br></html>";
							int anfrage = JOptionPane.showConfirmDialog(null, meldung, "Achtung wichtige Benutzeranfrage", JOptionPane.YES_NO_CANCEL_OPTION);
							if(anfrage == JOptionPane.YES_OPTION){
								zm.allefrei = false;
								zm.allezuzahl = false;
								zm.anfangfrei = true;
								zm.teil1 = vAktTermine.size();
								zm.teil2 = maxAnzahl()-vAktTermine.size();
								iret = 1;
							}else if(anfrage == JOptionPane.CANCEL_OPTION){
								return -1;
								
							}else{
								Object ret = JOptionPane.showInputDialog(null, "Geben Sie bitte die Anzahl Behandlungen ein für die\nRezeptgebühren berechnet werden sollen:", Integer.toString(maxAnzahl()-vAktTermine.size()));
								if(ret == null){
									//iret = 0;
									return -1;
								}else{
									zm.allefrei = false;
									zm.allezuzahl = false;
									zm.anfangfrei = true;
									zm.teil1 = maxAnzahl()-Integer.parseInt((String)ret);
									zm.teil2 = Integer.parseInt((String)ret);
									iret = 1;
								}
							}
						}else{
							iret = 0;	
						}
						
					}else{// gemischte Termine
						//System.out.println("2 -Termine aus dem Vorjahr(frei) = "+obj[1]+" Termine aus diesem Jahr(Zuzahlung) = "+obj[2]);
						zm.allefrei = false;
						zm.allezuzahl = false;
						zm.anfangfrei = true;
						zm.teil1 = (Integer)obj[1];
						zm.teil2 = (Integer)obj[2];
						iret = 1;
					}
				}
				break;
			}
			/************************Aktuelle Befreiung aber nicht im Vorjahr************************/		
			// Fehler !!!!!!!!!!!!!!!!!!!!!!! muß korrigiert werden!!!!!!!!!!!!!!!!!!!!!!!
			if((boolean) ((String)Reha.thisClass.patpanel.patDaten.get(30)).equals("T") && 
					(((String)Reha.thisClass.patpanel.vecaktrez.get(59)).trim().equals("")) ){
				//System.out.println("Aktuelle Befreiung aber nicht im Vorjahr");
				if(!bMitJahresWechsel){//Alle Termine aktuell
					iret = 0;
				}else{// es gibt Termine im Vorjahr
					Object[] obj = JahresWechsel(vAktTermine,SystemConfig.vorJahr);
					if(!(Boolean)obj[0]){// alle Termine waren im Vorjahr 
						iret = 2;
					}else{// gemischte Termine
						//System.out.println("Termine aus dem Vorjahr(Zuzahlung) = "+obj[1]+" Termine aus diesem Jahr(frei) = "+obj[2]);
						zm.allefrei = false;
						zm.allezuzahl = false;
						zm.anfangfrei = false;
						zm.teil1 = (Integer)obj[1];
						zm.teil2 = (Integer)obj[2];
						iret = 3;
					}
				}
				break;
			}
		}
		

		zm.hausbesuch = ((String)Reha.thisClass.patpanel.vecaktrez.get(43)).equals("T");
		zm.hbvoll = ((String)Reha.thisClass.patpanel.vecaktrez.get(61)).equals("T");
		zm.hbheim = ((String)Reha.thisClass.patpanel.patDaten.get(44)).equals("T");
		zm.km = StringTools.ZahlTest(((String)Reha.thisClass.patpanel.patDaten.get(48)));
		zm.preisgruppe = Integer.parseInt(((String)Reha.thisClass.patpanel.vecaktrez.get(41)));
		zm.gesamtZahl = Integer.parseInt(((String)Reha.thisClass.patpanel.vecaktrez.get(64)));
		//Hausbesuch als logischen wert
		
		if(iret==0){
			if(testefuerbarcode){
				constructGanzFreiRezHMap(zm);
				constructNormalRezHMap(zm,false);
				SystemConfig.hmAdrRDaten.put("<Rendbetrag>", "0,00" );
			}else{
				constructGanzFreiRezHMap(zm);				
			}
		}
		if(iret==1){
			constructAnfangFreiRezHMap(zm,true);
		}
		if(iret==2){
			constructNormalRezHMap(zm,false);
		}
		if(iret==3){
			constructEndeFreiRezHMap(zm,false);
		}
		/*
		System.out.println(macheUmsatzZeile(
				SqlInfo.holeFelder("select * from verordn where rez_nr='"+
						Reha.thisClass.patpanel.vecaktrez.get(1)+"' LIMIT 1"),"")
				);
		*/		
		//System.out.println("ZZ-Variante = "+iret);
		return iret;
	}
	/************
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	public static void constructNormalRezHMap(ZuzahlModell zm,boolean unregelmaessig){
		/************************************/
		//System.out.println("*****In Normal HMap*********");
		Double rezgeb = new Double(0.000);
		BigDecimal[] preise = {null,null,null,null};
		BigDecimal xrezgeb = BigDecimal.valueOf(new Double(0.000));
		
		////System.out.println("nach nullzuweisung " +xrezgeb.toString());
		int[] anzahl = {0,0,0,0};
		int[] artdbeh = {0,0,0,0};
		int i;
		BigDecimal einzelpreis = null;
		BigDecimal poswert = null;
		BigDecimal rezwert = BigDecimal.valueOf(new Double(0.000));
		SystemConfig.hmAdrRDaten.put("<Rid>",(String)Reha.thisClass.patpanel.vecaktrez.get(35) );
		SystemConfig.hmAdrRDaten.put("<Rnummer>",(String)Reha.thisClass.patpanel.vecaktrez.get(1) );
		SystemConfig.hmAdrRDaten.put("<Rdatum>",DatFunk.sDatInDeutsch((String)Reha.thisClass.patpanel.vecaktrez.get(2)) );		
		for(i = 0;i < 4;i++){
			anzahl[i] = Integer.valueOf((String)Reha.thisClass.patpanel.vecaktrez.get(i+3));
			artdbeh[i] = Integer.valueOf((String)Reha.thisClass.patpanel.vecaktrez.get(i+8));
			preise[i] = BigDecimal.valueOf(new Double((String)Reha.thisClass.patpanel.vecaktrez.get(i+18)));
		}
		xrezgeb = xrezgeb.add(BigDecimal.valueOf(new Double(10.00)));
		rezgeb = 10.00;
		////System.out.println("nach 10.00 zuweisung " +rezgeb.toString());		
//		String runden;
		DecimalFormat dfx = new DecimalFormat( "0.00" );
		BigDecimal endpos;
		SystemConfig.hmAdrRDaten.put("<Rnummer>",(String)Reha.thisClass.patpanel.vecaktrez.get(1) );
		SystemConfig.hmAdrRDaten.put("<Rpatid>",(String)Reha.thisClass.patpanel.vecaktrez.get(0) );
		SystemConfig.hmAdrRDaten.put("<Rdatum>",DatFunk.sDatInDeutsch( (String)Reha.thisClass.patpanel.vecaktrez.get(2))  );		
		SystemConfig.hmAdrRDaten.put("<Rpauschale>",dfx.format(rezgeb) );
		
		for(i = 0; i < 4; i++){
			/*
			//System.out.println(Integer.valueOf(anzahl[i]).toString()+" / "+ 
					Integer.valueOf(artdbeh[i]).toString()+" / "+
					preise[i].toString() );
			*/		
			if(artdbeh[i] > 0){
				SystemConfig.hmAdrRDaten.put("<Rposition"+(i+1)+">",(String)Reha.thisClass.patpanel.vecaktrez.get(48+i) );
				SystemConfig.hmAdrRDaten.put("<Rpreis"+(i+1)+">", dfx.format(preise[i]) );
				
				einzelpreis = preise[i].divide(BigDecimal.valueOf(new Double(10.000)));

				poswert = preise[i].multiply(BigDecimal.valueOf(new Double(anzahl[i]))); 
				rezwert = rezwert.add(poswert);
				////System.out.println("Einzelpreis "+i+" = "+einzelpreis);
				BigDecimal testpr = einzelpreis.setScale(2, BigDecimal.ROUND_HALF_UP);
				////System.out.println("test->Einzelpreis "+i+" = "+testpr);

				SystemConfig.hmAdrRDaten.put("<Rproz"+(i+1)+">", dfx.format(testpr) );
				SystemConfig.hmAdrRDaten.put("<Ranzahl"+(i+1)+">", Integer.valueOf(anzahl[i]).toString() );
				
				endpos = testpr.multiply(BigDecimal.valueOf(new Double(anzahl[i]))); 
				SystemConfig.hmAdrRDaten.put("<Rgesamt"+(i+1)+">", dfx.format(endpos) );
				rezgeb = rezgeb + endpos.doubleValue();
				////System.out.println(rezgeb.toString());

			}else{
				SystemConfig.hmAdrRDaten.put("<Rposition"+(i+1)+">","----");
				SystemConfig.hmAdrRDaten.put("<Rpreis"+(i+1)+">", "0,00" );
				SystemConfig.hmAdrRDaten.put("<Rproz"+(i+1)+">", "0,00");				
				SystemConfig.hmAdrRDaten.put("<Rgesamt"+(i+1)+">", "0,00" );
				SystemConfig.hmAdrRDaten.put("<Ranzahl"+(i+1)+">", "----" );
						
			}
		}
		//System.out.println(SystemConfig.hmAdrRDaten);
		/*****************************************************/
		if(zm.hausbesuch){ //Hausbesuch
			Object[] obi = hbNormal(zm,rezwert,rezgeb,Integer.valueOf(((String)Reha.thisClass.patpanel.vecaktrez.get(64))));
			rezwert = ((BigDecimal)obi[0]);
			rezgeb = (Double)obi[1];
		}
		
		
		/*****************************************************/		
		Double drezwert = rezwert.doubleValue();
		SystemConfig.hmAdrRDaten.put("<Rendbetrag>", dfx.format(rezgeb) );
		SystemConfig.hmAdrRDaten.put("<Rwert>", dfx.format(drezwert) );
		DecimalFormat df = new DecimalFormat( "0.00" );
		df.format( rezgeb);
		////System.out.println("----------------------------------------------------");
		////System.out.println("Endgültige und geparste Rezeptgebühr = "+s+" EUR");
		////System.out.println(SystemConfig.hmAdrRDaten);
		/***********************/
		
		// Hier muß noch Hausbesuchshandling eingebaut werden
		// Ebenso das Wegegeldhandling
	}
	
	public static void constructGanzFreiRezHMap(ZuzahlModell zm){
		SystemConfig.hmAdrRDaten.put("<Rid>",(String)Reha.thisClass.patpanel.vecaktrez.get(35) );
		SystemConfig.hmAdrRDaten.put("<Rnummer>",(String)Reha.thisClass.patpanel.vecaktrez.get(1) );
		SystemConfig.hmAdrRDaten.put("<Rdatum>",DatFunk.sDatInDeutsch((String)Reha.thisClass.patpanel.vecaktrez.get(2)) );		
		SystemConfig.hmAdrRDaten.put("<Rpatid>",(String)Reha.thisClass.patpanel.vecaktrez.get(0) );
		SystemConfig.hmAdrRDaten.put("<Rpauschale>","0,00");
		for(int i = 0;i<5;i++){
			SystemConfig.hmAdrRDaten.put("<Rposition"+(i+1)+">","----");
			SystemConfig.hmAdrRDaten.put("<Rpreis"+(i+1)+">", "0,00" );
			SystemConfig.hmAdrRDaten.put("<Rproz"+(i+1)+">", "0,00");				
			SystemConfig.hmAdrRDaten.put("<Rgesamt"+(i+1)+">", "0,00" );
			SystemConfig.hmAdrRDaten.put("<Ranzahl"+(i+1)+">", "----" );
		}
		SystemConfig.hmAdrRDaten.put("<Rhbpos>","----");
		SystemConfig.hmAdrRDaten.put("<Rhbpreis>", "0,00");
		SystemConfig.hmAdrRDaten.put("<Rhbproz>", "0,00");
		SystemConfig.hmAdrRDaten.put("<Rhbgesamt>", "0,00");
		SystemConfig.hmAdrRDaten.put("<Rwegpos>","----");
		SystemConfig.hmAdrRDaten.put("<Rwegpreis>", "0,00");
		SystemConfig.hmAdrRDaten.put("<Rwegproz>", "0,00");
		SystemConfig.hmAdrRDaten.put("<Rweggesamt>", "0,00");
		SystemConfig.hmAdrRDaten.put("<Rendbetrag>", "0,00" );
		SystemConfig.hmAdrRDaten.put("<Rwert>", "0,00" );
	}
	public static void constructAnfangFreiRezHMap(ZuzahlModell zm,boolean anfang){
		//System.out.println("*****In Anfang-frei*********");
		if(anfang){
			zm.gesamtZahl = Integer.valueOf(zm.teil2);
			//System.out.println("Restliche Behandlungen berechnen = "+zm.gesamtZahl);
		}else{
			zm.gesamtZahl = Integer.valueOf(zm.teil1);
			//System.out.println("Beginn der Behandlung berechnen = "+zm.gesamtZahl);
		}

		Double rezgeb = new Double(0.000);
		BigDecimal[] preise = {null,null,null,null};
		BigDecimal xrezgeb = BigDecimal.valueOf(new Double(0.000));
		
		////System.out.println("nach nullzuweisung " +xrezgeb.toString());
		int[] anzahl = {0,0,0,0};
		int[] artdbeh = {0,0,0,0};
		/***************/ //Einbauen für Barcode
		int[] gesanzahl = {0,0,0,0};
		int i;
		BigDecimal einzelpreis = null;
		BigDecimal poswert = null;
		BigDecimal rezwert = BigDecimal.valueOf(new Double(0.000));
		SystemConfig.hmAdrRDaten.put("<Rid>",(String)Reha.thisClass.patpanel.vecaktrez.get(35) );
		SystemConfig.hmAdrRDaten.put("<Rnummer>",(String)Reha.thisClass.patpanel.vecaktrez.get(1) );
		SystemConfig.hmAdrRDaten.put("<Rdatum>",(String)Reha.thisClass.patpanel.vecaktrez.get(2) );		
		for(i = 0;i < 4;i++){
			gesanzahl[i] = Integer.valueOf((String)Reha.thisClass.patpanel.vecaktrez.get(i+3));
			anzahl[i] = Integer.valueOf((String)Reha.thisClass.patpanel.vecaktrez.get(i+3));
			if(! (anzahl[i] < zm.gesamtZahl)){
				anzahl[i] = Integer.valueOf(zm.gesamtZahl);
			}
			artdbeh[i] = Integer.valueOf((String)Reha.thisClass.patpanel.vecaktrez.get(i+8));
			preise[i] = BigDecimal.valueOf(new Double((String)Reha.thisClass.patpanel.vecaktrez.get(i+18)));
		}
		xrezgeb = xrezgeb.add(BigDecimal.valueOf(new Double(10.00)));
		if(anfang){
			rezgeb = 00.00;			
		}else{
			rezgeb = 10.00;
		}
		

		////System.out.println("nach 10.00 zuweisung " +rezgeb.toString());		
		//String runden;
		DecimalFormat dfx = new DecimalFormat( "0.00" );
		BigDecimal endpos;
		SystemConfig.hmAdrRDaten.put("<Rnummer>",(String)Reha.thisClass.patpanel.vecaktrez.get(1) );
		SystemConfig.hmAdrRDaten.put("<Rpatid>",(String)Reha.thisClass.patpanel.vecaktrez.get(0) );
		SystemConfig.hmAdrRDaten.put("<Rdatum>",DatFunk.sDatInDeutsch( (String)Reha.thisClass.patpanel.vecaktrez.get(2) )  );		
		SystemConfig.hmAdrRDaten.put("<Rpauschale>",dfx.format(rezgeb) );
		
		for(i = 0; i < 4; i++){
			/*
			//System.out.println(Integer.valueOf(anzahl[i]).toString()+" / "+ 
					Integer.valueOf(artdbeh[i]).toString()+" / "+
					preise[i].toString() );
			*/		
			if(artdbeh[i] > 0){
				SystemConfig.hmAdrRDaten.put("<Rposition"+(i+1)+">",(String)Reha.thisClass.patpanel.vecaktrez.get(48+i) );
				SystemConfig.hmAdrRDaten.put("<Rpreis"+(i+1)+">", dfx.format(preise[i]) );
				
				einzelpreis = preise[i].divide(BigDecimal.valueOf(new Double(10.000)));
				//***********vorher nur anzahl[]*****************/
				poswert = preise[i].multiply(BigDecimal.valueOf(new Double(gesanzahl[i]))); 
				rezwert = rezwert.add(poswert);
				////System.out.println("Einzelpreis "+i+" = "+einzelpreis);
				BigDecimal testpr = einzelpreis.setScale(2, BigDecimal.ROUND_HALF_UP);
				////System.out.println("test->Einzelpreis "+i+" = "+testpr);

				SystemConfig.hmAdrRDaten.put("<Rproz"+(i+1)+">", dfx.format(testpr) );
				SystemConfig.hmAdrRDaten.put("<Ranzahl"+(i+1)+">", Integer.toString(anzahl[i]) );
				
				endpos = testpr.multiply(BigDecimal.valueOf(new Double(anzahl[i]))); 
				SystemConfig.hmAdrRDaten.put("<Rgesamt"+(i+1)+">", dfx.format(endpos) );
				rezgeb = rezgeb + endpos.doubleValue();
				////System.out.println(rezgeb.toString());

			}else{
				SystemConfig.hmAdrRDaten.put("<Rposition"+(i+1)+">","----");
				SystemConfig.hmAdrRDaten.put("<Rpreis"+(i+1)+">", "0,00" );
				SystemConfig.hmAdrRDaten.put("<Rproz"+(i+1)+">", "0,00");				
				SystemConfig.hmAdrRDaten.put("<Rgesamt"+(i+1)+">", "0,00" );
				SystemConfig.hmAdrRDaten.put("<Ranzahl"+(i+1)+">", "----" );
						
			}
		}
		/*****************************************************/
		if(zm.hausbesuch){ //Hausbesuch
			if(zm.gesamtZahl > Integer.valueOf(((String)Reha.thisClass.patpanel.vecaktrez.get(64)))){
				zm.gesamtZahl = Integer.valueOf(((String)Reha.thisClass.patpanel.vecaktrez.get(64))); 
			}
			Object[] obi = hbNormal(zm,rezwert,rezgeb,Integer.valueOf(((String)Reha.thisClass.patpanel.vecaktrez.get(64))));
			rezwert = ((BigDecimal)obi[0]);
			rezgeb = (Double)obi[1];
		}
		/*****************************************************/		
		Double drezwert = rezwert.doubleValue();
		SystemConfig.hmAdrRDaten.put("<Rendbetrag>", dfx.format(rezgeb) );
		SystemConfig.hmAdrRDaten.put("<Rwert>", dfx.format(drezwert) );
		DecimalFormat df = new DecimalFormat( "0.00" );
		df.format( rezgeb);
		//System.out.println("----------------------------------------------------");
		//System.out.println("Endgültige und geparste Rezeptgebühr = "+s+" EUR");
		////System.out.println(SystemConfig.hmAdrRDaten);
		/***********************/
	}
		

	public static void constructEndeFreiRezHMap(ZuzahlModell zm,boolean anfang){
		//System.out.println("*****Über Ende Frei*********");
		constructAnfangFreiRezHMap(zm,anfang);
	}	
	@SuppressWarnings("unchecked")
	public static Vector<Vector<String>>splitteTermine(String terms){
		Vector<Vector<String>> termine = new Vector<Vector<String>>();
		String[] tlines = terms.split("\n");
		int lines = tlines.length;
		////System.out.println("Anzahl Termine = "+lines);
		Vector<String> tvec = new Vector<String>();
		String[] terdat = null;
		for(int i = 0;i<lines;i++){
			terdat = tlines[i].split("@");
			int ieinzel = terdat.length;
			if(ieinzel <= 1){
				return (Vector<Vector<String>>) termine.clone();
			}
			////System.out.println("Anzahl Splits = "+ieinzel);
			tvec.clear();
			for(int y = 0; y < ieinzel;y++){
					tvec.add((terdat[y].trim().equals("") ? "  .  .    " : terdat[y]));
			}
			termine.add((Vector<String>)tvec.clone());
		}
		return (Vector<Vector<String>>) termine.clone();
	}
	
	public static Object[] JahrEnthalten(Vector<String>vtage,String jahr){
		Object[] ret = {Boolean.valueOf(false),-1};
		for(int i = 0; i < vtage.size();i++){
			if( ((String)vtage.get(i)).equals(jahr) ){
				ret[0] = true;
				ret[1] = Integer.valueOf(i);
				break;
			}
		}
		return ret;
	}
	public static Object[] JahresWechsel(Vector<String>vtage,String jahr){
		Object[] ret = {Boolean.valueOf(false),-1,-1};
		for(int i = 0; i < vtage.size();i++){
			if(!((String)vtage.get(i)).substring(6).equals(jahr) ){
				ret[0] = true;
				ret[1] = Integer.valueOf(i);
				ret[2] = maxAnzahl()-(Integer)ret[1];
				return ret;
			}
		}
		/*
		if(maxAnzahl() > vtage.size()){
			ret[0] = true;
			ret[1] = Integer.valueOf(vtage.size());
			ret[2] = maxAnzahl()-(Integer)ret[1];
		}
		System.out.println("maximale Anzahl "+maxAnzahl());
		*/
		return ret;
	}
	public static int maxAnzahl(){
		int ret = -1;
		int test;
		for(int i = 3; i < 7;i++){
			test = Integer.valueOf(((String)Reha.thisClass.patpanel.vecaktrez.get(i)));
			if(test > ret){
				ret = Integer.valueOf(test);
			}
		}
		return ret;
	}
	public static String PreisUeberPosition(String position,int preisgruppe,String disziplin,boolean neu ){
		//JOptionPane.showMessageDialog(null, "Aufruf der Funktion PreisUeberPosition");
		String ret = null;
		Vector<?> preisvec = null;
		preisvec = SystemPreislisten.hmPreise.get(putRezNrGetDisziplin(disziplin)).get(preisgruppe-1);
		for(int i = 0; i < preisvec.size();i++){
			if(  ((String)((Vector<?>)preisvec.get(i)).get(2)).equals(position) ){
				ret =  ((String)((Vector<?>)preisvec.get(i)).get(3+(neu ? 0 : 1)));
//				//System.out.println("Der Preis von "+position+" = "+ret);
				return ret;
			}
		}
		////System.out.println("Der Preis von "+position+" wurde nicht gefunden!!");
		return ret;
	}
	
	public static String putRezNrGetDisziplin(String reznr){
		if(reznr.startsWith("KG")){
			return "Physio";
		}else if(reznr.startsWith("MA")){
			return "Massage";
		}else if(reznr.startsWith("ER")){
			return "Ergo";
		}else if(reznr.startsWith("LO")){
			return "Logo";
		}else if(reznr.startsWith("RH")){
			return "Reha";
		}else if(reznr.startsWith("PO")){
			return "Podo";
		}
		return "Physio";
	}
	public static Object[] ermittleRezeptwert(Vector<String> vec){
		Object[] retobj = {null,null,null};
		return retobj;
	}
	public static Object[] ermittleHBwert(Vector<String> vec){
		Object[] retobj = {null,null,null};
		String disziplin = putRezNrGetDisziplin(vec.get(1));
		String pos = "";
		Double preis =0.00;
		Double wgkm = 0.00;
		
		@SuppressWarnings("unused")
		String pospauschale = "";
		Double preispauschale =0.00;
		//Double wgpauschal = 0.00;
		// erst testen ob HB-Einzeln oder HB-Mehrere
		int anzahl = Integer.parseInt(vec.get(64));
		int preisgruppe = Integer.parseInt(vec.get(41));
		if(vec.get(61).equals("T")){
			//Einzelhausbesuch
			 pos = SystemPreislisten.hmHBRegeln.get(disziplin).get(preisgruppe-1).get(0);
			 preis = Double.parseDouble(RezTools.getPreisAktFromPos(pos, Integer.toString(preisgruppe), SystemPreislisten.hmPreise.get(disziplin).get(preisgruppe-1)));
			 retobj[0] = BigDecimal.valueOf(preis).multiply(BigDecimal.valueOf(Double.parseDouble(Integer.toString(anzahl)))).doubleValue();
			 //testen ob Fahrtgeldüberhaupt gezahlt wird;
			 if(keineWeggebuehrBeiHB(disziplin,Integer.toString(preisgruppe))){
				 return retobj;
			 }
			 if(zweiPositionenBeiHB(disziplin,Integer.toString(preisgruppe))){
				 //Weggebühr und pauschale
				 /* In Betrieb bis 26.11.2010 *****************
				 if( (wgkm=Double.parseDouble(vec.get(7))) > 7 ){
					 //Kilometer verwenden
					 pos = SystemPreislisten.hmHBRegeln.get(disziplin).get(preisgruppe-1).get(2);
					 preis = Double.parseDouble(RezTools.getPreisAktFromPos(pos, Integer.toString(preisgruppe), SystemPreislisten.hmPreise.get(disziplin).get(preisgruppe-1)));
					 BigDecimal kms = BigDecimal.valueOf(preis).multiply(BigDecimal.valueOf(Double.parseDouble(Integer.toString(anzahl))));
					 kms = kms.multiply(BigDecimal.valueOf(wgkm));
					 retobj[1] = kms.doubleValue();
					 return retobj;
				 }else{
					 //Pauschale verwenden
					 pos = SystemPreislisten.hmHBRegeln.get(disziplin).get(preisgruppe-1).get(3);
					 preis = Double.parseDouble(RezTools.getPreisAktFromPos(pos, Integer.toString(preisgruppe), SystemPreislisten.hmPreise.get(disziplin).get(preisgruppe-1)));
					 retobj[1] = BigDecimal.valueOf(preis).multiply(BigDecimal.valueOf(Double.parseDouble(Integer.toString(anzahl)))).doubleValue();
					 return retobj;
				 }
				 */
				 pos = SystemPreislisten.hmHBRegeln.get(disziplin).get(preisgruppe-1).get(2);
				 preis = Double.parseDouble(RezTools.getPreisAktFromPos(pos, Integer.toString(preisgruppe), SystemPreislisten.hmPreise.get(disziplin).get(preisgruppe-1)));
				 BigDecimal kms = BigDecimal.valueOf(preis).multiply(BigDecimal.valueOf(Double.parseDouble(Integer.toString(anzahl))));
				 kms = kms.multiply(BigDecimal.valueOf(wgkm));

				 pospauschale = SystemPreislisten.hmHBRegeln.get(disziplin).get(preisgruppe-1).get(3);
				 preispauschale = Double.parseDouble(RezTools.getPreisAktFromPos(pos, Integer.toString(preisgruppe), SystemPreislisten.hmPreise.get(disziplin).get(preisgruppe-1)));
				 if(kms.doubleValue() > BigDecimal.valueOf(preispauschale).multiply(BigDecimal.valueOf(Double.parseDouble(Integer.toString(anzahl)))).doubleValue()){
					 retobj[1] = kms.doubleValue();
				 }else{
					 retobj[1] = BigDecimal.valueOf(preispauschale).multiply(BigDecimal.valueOf(Double.parseDouble(Integer.toString(anzahl)))).doubleValue();
				 }
				 return retobj;
			 }
		}else{
			//Mehrere Hausbesuch
			 pos = SystemPreislisten.hmHBRegeln.get(disziplin).get(preisgruppe-1).get(1);
			 preis = Double.parseDouble(RezTools.getPreisAktFromPos(pos, Integer.toString(preisgruppe), SystemPreislisten.hmPreise.get(disziplin).get(preisgruppe-1)));
			 retobj[0] = BigDecimal.valueOf(preis).multiply(BigDecimal.valueOf(Double.parseDouble(Integer.toString(anzahl)))).doubleValue();
	}

		return retobj;
	}
	public static Vector<String> macheUmsatzZeile(Vector<Vector<String>> vec,String tag){
		Vector<String> retvec = new Vector<String>();
		for(int i = 0; i < 12;i++){
			retvec.add("");
		}
			
		String disziplin = putRezNrGetDisziplin(vec.get(0).get(1));
		String pos = "";
		String preis = "";
		String pospauschale = "";
		@SuppressWarnings("unused")
		String preispauschale = "";
		int preisgruppe = Integer.parseInt(vec.get(0).get(41));
		
		Double wgkm;
		for(int i = 0;i<4;i++){
			if(!vec.get(0).get(i+8).trim().equals("0")){
				// hier kann man später noch untersuchen ob Positionen die mit Anzahl=1 aufgenommen wurden
				// aufgeführt werden sollen (wg. evtl. Umsatzverfälschung)
				// dafür kann der Parameter tag und das dbFeld termine verwendet werden
				pos = RezTools.getKurzformFromID(vec.get(0).get(i+8).trim(),SystemPreislisten.hmPreise.get(disziplin).get( (preisgruppe==0 ? 0 : preisgruppe-1) ));
				if(pos.trim().equals("")){
					pos = RezTools.getKurzformFromPos(vec.get(0).get(i+48).trim(), Integer.toString(preisgruppe), SystemPreislisten.hmPreise.get(disziplin).get( (preisgruppe==0 ? 0 : preisgruppe-1) ));					
				}
				////System.out.println("Haupt-Position = "+pos);
				retvec.set(i, pos);
				retvec.set(i+6,vec.get(0).get(i+18).trim());
			}else{
				retvec.set(i, "-----");
				retvec.set(i+6,"0.00");
			}
		}
		//mit Hausbesuch?
		if(vec.get(0).get(43).equals("T")){
			//Hausbesuch einzeln?
			if(vec.get(0).get(61).equals("T")){
				pos = SystemPreislisten.hmHBRegeln.get(disziplin).get( (preisgruppe==0 ? 0 : preisgruppe-1) ).get(0);
				preis = RezTools.getPreisAktFromPos(pos, Integer.toString(preisgruppe), SystemPreislisten.hmPreise.get(disziplin).get( (preisgruppe==0 ? 0 : preisgruppe-1) ));
				 retvec.set(4, pos);
				 retvec.set(10,preis);

				 if(! keineWeggebuehrBeiHB(disziplin,Integer.toString((preisgruppe==0 ? 1 : preisgruppe)))){
					 ////System.out.println("Kasse kennt Weggebühr...");
					 if(zweiPositionenBeiHB(disziplin,Integer.toString((preisgruppe==0 ? 1 : preisgruppe)))){
						 //Weggebühr und pauschale
						 ////System.out.println("Kasse kennt km und Pauschale...");
						 pos = SystemPreislisten.hmHBRegeln.get(disziplin).get((preisgruppe==0 ? 0 : preisgruppe-1)).get(2);
						 pospauschale = SystemPreislisten.hmHBRegeln.get(disziplin).get((preisgruppe==0 ? 0 : preisgruppe-1)).get(3);
						 wgkm = Double.parseDouble(vec.get(0).get(7));
						 
						 if( kmBesserAlsPauschale(pospauschale,pos,wgkm,preisgruppe,disziplin) ){
						 //if( (wgkm=Double.parseDouble(vec.get(0).get(7))) > 7 ){
							 //Kilometer verwenden
							 ////System.out.println("Kilometer verwenden...");
							 pos = SystemPreislisten.hmHBRegeln.get(disziplin).get((preisgruppe==0 ? 0 : preisgruppe-1)).get(2);
							 preis = RezTools.getPreisAktFromPos(pos, Integer.toString(preisgruppe), SystemPreislisten.hmPreise.get(disziplin).get((preisgruppe==0 ? 0 : preisgruppe-1)));
							 BigDecimal kms = BigDecimal.valueOf(Double.parseDouble(preis)).multiply(BigDecimal.valueOf(wgkm));
							 retvec.set(5, pos);
							 retvec.set(11,Double.toString(kms.doubleValue()));
							 ////System.out.println("Pos = "+pos);
							 ////System.out.println("Preis = "+preis);
						 }else{
							 //Pauschale verwenden
							 ////System.out.println("Pauschale verwenden....");
							 pospauschale = SystemPreislisten.hmHBRegeln.get(disziplin).get((preisgruppe==0 ? 0 : preisgruppe-1)).get(3);
							 preis = RezTools.getPreisAktFromPos(pospauschale, Integer.toString(preisgruppe), SystemPreislisten.hmPreise.get(disziplin).get((preisgruppe==0 ? 0 : preisgruppe-1)));
							 //System.out.println("Pos = "+pos);
							 //System.out.println("Preis = "+preis);
							 retvec.set(5, pospauschale);
							 retvec.set(11,preis);
						 }
					 }else{
						 //System.out.println("Kann Weggebührmodalität nicht ermitteln....");
					 }
					 
				 }else{
					 //System.out.println("Kasse kennt keine Weggebühr....");
					 retvec.set(5, "-----");
					 retvec.set(11,"0.00");
				 }
			}else{
				//Hausbesuch mit
				 pos = SystemPreislisten.hmHBRegeln.get(disziplin).get((preisgruppe==0 ? 0 : preisgruppe-1)).get(1);
				 preis =RezTools.getPreisAktFromPos(pos, Integer.toString(preisgruppe), SystemPreislisten.hmPreise.get(disziplin).get((preisgruppe==0 ? 0 : preisgruppe-1)));
				 retvec.set(4, pos);
				 retvec.set(10,preis);
				 retvec.set(5, "-----");
				 retvec.set(11,"0.00");
			}
		}else{
			retvec.set(4, "-----");
			retvec.set(10,"0.00");
			retvec.set(5, "-----");
			retvec.set(11,"0.00");
		}
		return retvec;
	}
	public static boolean kmBesserAlsPauschale(String pospauschal,String poskm,Double anzahlkm,int preisgruppe,String disziplin){
		String meldung = ""; 
		try{
			String preiskm;
			String preispauschal;
			meldung = " Pospauschal = "+pospauschal+"\n"+
			          "PosKilometer = "+poskm+"\n"+
			          "   Anzahl km = "+anzahlkm+"\n"+
			          " Preisgruppe = "+preisgruppe+"\n"+
			          "   Disziplin = "+disziplin;
			preiskm = RezTools.getPreisAktFromPos(poskm, Integer.toString(preisgruppe), SystemPreislisten.hmPreise.get(disziplin).get((preisgruppe==0 ? 0 : preisgruppe-1)));
			BigDecimal kms = BigDecimal.valueOf(Double.parseDouble(preiskm)).multiply(BigDecimal.valueOf(anzahlkm));
			preispauschal = RezTools.getPreisAktFromPos(pospauschal, Integer.toString(preisgruppe), SystemPreislisten.hmPreise.get(disziplin).get((preisgruppe==0 ? 0 : preisgruppe-1)));
			BigDecimal pauschal = BigDecimal.valueOf(Double.parseDouble(preispauschal));
			return (kms.doubleValue() > pauschal.doubleValue());
		}catch(Exception ex){
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Fehler in der Ermittlung km-Abrechnung besser als Pauschale\n"+meldung);
		}
		return false;
	}
	public static Object[] hbNormal(ZuzahlModell zm, BigDecimal rezwert,Double rezgeb,int realhbAnz){
		
		//Object[] retobj = {new BigDecimal(new Double(0.00)),(Double)rezgeb};
		//((BigDecimal)retobj[0]).add(BigDecimal.valueOf(new Double(1.00)));
		//((BigDecimal) retobj[0]).add(new BigDecimal(rezwert));
		Object[] retobj = {(BigDecimal) rezwert,(Double)rezgeb};
		//System.out.println("Die tatsächlich HB-Anzahl = "+realhbAnz);
		//System.out.println("Der Rezeptwert zu Beginn = "+retobj[0]);
		if(zm.hausbesuch){ //Hausbesuch
			//System.out.println("Hausbesuch ist angesagt");
			//String[] praefix = {"1","2","5","3","MA","KG","ER","LO"};
			String rezid = SystemConfig.hmAdrRDaten.get("<Rnummer>").substring(0,2);
			/*
			String zz =  SystemConfig.vHBRegeln.get(zm.preisgruppe-1).get(4);
			String kmgeld = SystemConfig.vHBRegeln.get(zm.preisgruppe-1).get(2);
			String kmpausch = SystemConfig.vHBRegeln.get(zm.preisgruppe-1).get(3);			
			String hbpos = SystemConfig.vHBRegeln.get(zm.preisgruppe-1).get(0);
			String hbmit = SystemConfig.vHBRegeln.get(zm.preisgruppe-1).get(1);
			*/
			String zz =  SystemPreislisten.hmHBRegeln.get(putRezNrGetDisziplin(rezid)).get(zm.preisgruppe-1).get(4);
			String kmgeld = SystemPreislisten.hmHBRegeln.get(putRezNrGetDisziplin(rezid)).get(zm.preisgruppe-1).get(2);
			String kmpausch = SystemPreislisten.hmHBRegeln.get(putRezNrGetDisziplin(rezid)).get(zm.preisgruppe-1).get(3);			
			String hbpos = SystemPreislisten.hmHBRegeln.get(putRezNrGetDisziplin(rezid)).get(zm.preisgruppe-1).get(0);
			String hbmit = SystemPreislisten.hmHBRegeln.get(putRezNrGetDisziplin(rezid)).get(zm.preisgruppe-1).get(1);

			//für jede Disziplin eine anderes praefix
			//String ersatz = praefix[Arrays.asList(praefix).indexOf(rezid)-4];
			/*
			kmgeld = kmgeld.replaceAll("x",ersatz); 
			kmpausch = kmpausch.replaceAll("x",ersatz); 
			hbpos = hbpos.replaceAll("x",ersatz); 
			hbmit = hbmit.replaceAll("x",ersatz);
			*/
			String preis = "";
			BigDecimal bdrezgeb;
			BigDecimal bdposwert;
			BigDecimal bdpreis;
			BigDecimal bdendrezgeb;
			BigDecimal testpr;
			SystemConfig.hmAdrRDaten.put("<Rwegkm>",Integer.toString(zm.km));
			SystemConfig.hmAdrRDaten.put("<Rhbanzahl>",Integer.toString(zm.gesamtZahl) );
			DecimalFormat dfx = new DecimalFormat( "0.00" );

			if(zm.hbheim){ // und zwar im Heim
				//System.out.println("Der HB ist im Heim");
				if(zm.hbvoll){// Volle Ziffer abrechnen?
					//System.out.println("Es kann der volle Hausbesuch abgerechnet werden");
					SystemConfig.hmAdrRDaten.put("<Rhbpos>",hbpos);
					preis = PreisUeberPosition(SystemConfig.hmAdrRDaten.get("<Rhbpos>"),
							zm.preisgruppe,SystemConfig.hmAdrRDaten.get("<Rnummer>").substring(0,2),true);
					//,"<Rhbpos>","<Rwegpos>","<Rhbpreis>","<Rwegpreis>","<Rhbproz>","<Rwegproz>","<Rhbanzahl>"
					//,"<Rhbgesamt>","<Rweggesamt>","<Rwegkm>"});
					bdpreis = new BigDecimal(new Double(preis));
					//bdposwert = bdpreis.multiply(BigDecimal.valueOf(new Double(zm.gesamtZahl)));
					bdposwert = bdpreis.multiply(BigDecimal.valueOf(new Double(realhbAnz)));
					retobj[0] = ((BigDecimal)retobj[0]).add(BigDecimal.valueOf(bdposwert.doubleValue()));
			
					
					/*******************************/
					if(zz.equals("1")){// Zuzahlungspflichtig
						SystemConfig.hmAdrRDaten.put("<Rhbpreis>", preis);			
						bdrezgeb = bdpreis.divide(BigDecimal.valueOf(new Double(10.000)));
						testpr = bdrezgeb.setScale(2, BigDecimal.ROUND_HALF_UP);
						bdendrezgeb = testpr.multiply(BigDecimal.valueOf(new Double(zm.gesamtZahl)));
						SystemConfig.hmAdrRDaten.put("<Rhbproz>", dfx.format(testpr.doubleValue()));
						SystemConfig.hmAdrRDaten.put("<Rhbgesamt>", dfx.format(bdendrezgeb.doubleValue()));
						retobj[1] = ((Double)retobj[1]) +bdendrezgeb.doubleValue();
					}else{
						SystemConfig.hmAdrRDaten.put("<Rhbanzahl>","----");
						SystemConfig.hmAdrRDaten.put("<Rhbpos>","----");
						SystemConfig.hmAdrRDaten.put("<Rhbpreis>", "0,00");
						SystemConfig.hmAdrRDaten.put("<Rhbproz>", "0,00");
						SystemConfig.hmAdrRDaten.put("<Rhbgesamt>", "0,00");
						SystemConfig.hmAdrRDaten.put("<Rweganzahl>","----");
						SystemConfig.hmAdrRDaten.put("<Rwegpos>","----");
						SystemConfig.hmAdrRDaten.put("<Rwegpreis>", "0,00");
						SystemConfig.hmAdrRDaten.put("<Rwegproz>", "0,00");
						SystemConfig.hmAdrRDaten.put("<Rweggesamt>", "0,00");
					}
					/*******************************/
					if(!kmgeld.equals("")){// Wenn Kilometer abgerechnet werden können
						//System.out.println("Es könnten Kilometer abgerechnet werden");
						if( kmBesserAlsPauschale(kmpausch,
													kmgeld,
													Double.parseDouble(Integer.toString(zm.km)),
													zm.preisgruppe,
													RezTools.putRezNrGetDisziplin(SystemConfig.hmAdrRDaten.get("<Rnummer>"))
												) 	){
							//Mit Kilometerabrechnung verdient man mehr
							preis = PreisUeberPosition(kmgeld,
									zm.preisgruppe,SystemConfig.hmAdrRDaten.get("<Rnummer>").substring(0,2),true);
							SystemConfig.hmAdrRDaten.put("<Rwegpos>",""+zm.km+"km*"+preis);
							/*******************************/
							bdpreis = new BigDecimal(new Double(preis)).multiply(new BigDecimal(new Double(zm.km)));
							bdposwert = bdpreis.multiply(BigDecimal.valueOf(new Double(realhbAnz)));
							retobj[0] = ((BigDecimal)retobj[0]).add(BigDecimal.valueOf(bdposwert.doubleValue()));
							SystemConfig.hmAdrRDaten.put("<Rwegpreis>", dfx.format(bdpreis.doubleValue()));
							if(zz.equals("1")){// Zuzahlungspflichtig
								bdrezgeb = bdpreis.divide(BigDecimal.valueOf(new Double(10.000)));
								testpr = bdrezgeb.setScale(2, BigDecimal.ROUND_HALF_UP);
								bdendrezgeb = testpr.multiply(BigDecimal.valueOf(new Double(zm.gesamtZahl)));
								SystemConfig.hmAdrRDaten.put("<Rwegproz>", dfx.format(testpr.doubleValue()));
								SystemConfig.hmAdrRDaten.put("<Rweggesamt>", dfx.format(bdendrezgeb.doubleValue()));
								SystemConfig.hmAdrRDaten.put("<Rweganzahl>",Integer.valueOf(zm.gesamtZahl).toString() );
								retobj[1] = ((Double)retobj[1]) +bdendrezgeb.doubleValue();
							}else{
								SystemConfig.hmAdrRDaten.put("<Rhbanzahl>","----");
								SystemConfig.hmAdrRDaten.put("<Rhbpos>","----");
								SystemConfig.hmAdrRDaten.put("<Rhbpreis>", "0,00");
								SystemConfig.hmAdrRDaten.put("<Rhbproz>", "0,00");
								SystemConfig.hmAdrRDaten.put("<Rhbgesamt>", "0,00");
								SystemConfig.hmAdrRDaten.put("<Rweganzahl>","----");
								SystemConfig.hmAdrRDaten.put("<Rwegpos>","----");
								SystemConfig.hmAdrRDaten.put("<Rwegpreis>", "0,00");
								SystemConfig.hmAdrRDaten.put("<Rwegproz>", "0,00");
								SystemConfig.hmAdrRDaten.put("<Rweggesamt>", "0,00");
							}
							/*******************************/

							//hier zuerst die kilometer ermitteln mal Kilometerpreis = der Endpreis
						}else{// Keine Kilometer angegeben also pauschale verwenden
							//System.out.println("Es wurden keine Kilometer angegeben also wird nach Ortspauschale abgerechnet");
							if(!kmpausch.equals("")){//Wenn die Kasse keine Pauschale zur Verfügung stellt
								SystemConfig.hmAdrRDaten.put("<Rwegpos>",kmpausch);
								preis = PreisUeberPosition(SystemConfig.hmAdrRDaten.get("<Rwegpos>"),
										zm.preisgruppe,SystemConfig.hmAdrRDaten.get("<Rnummer>").substring(0,2),true);
								/*******************************/
								bdpreis = new BigDecimal(new Double(preis));
								bdposwert = bdpreis.multiply(BigDecimal.valueOf(new Double(realhbAnz)));
								retobj[0] = ((BigDecimal)retobj[0]).add(BigDecimal.valueOf(bdposwert.doubleValue()));
								SystemConfig.hmAdrRDaten.put("<Rwegpreis>", dfx.format(bdpreis.doubleValue()));
								if(zz.equals("1")){// Zuzahlungspflichtig
									bdrezgeb = bdpreis.divide(BigDecimal.valueOf(new Double(10.000)));
									testpr = bdrezgeb.setScale(2, BigDecimal.ROUND_HALF_UP);
									bdendrezgeb = testpr.multiply(BigDecimal.valueOf(new Double(zm.gesamtZahl)));
									SystemConfig.hmAdrRDaten.put("<Rwegproz>", dfx.format(testpr.doubleValue()));
									SystemConfig.hmAdrRDaten.put("<Rweggesamt>", dfx.format(bdendrezgeb.doubleValue()));
									SystemConfig.hmAdrRDaten.put("<Rweganzahl>",Integer.valueOf(zm.gesamtZahl).toString() );								
									retobj[1] = ((Double)retobj[1]) +bdendrezgeb.doubleValue();
								}else{
									SystemConfig.hmAdrRDaten.put("<Rhbanzahl>","----");
									SystemConfig.hmAdrRDaten.put("<Rhbpos>","----");
									SystemConfig.hmAdrRDaten.put("<Rhbpreis>", "0,00");
									SystemConfig.hmAdrRDaten.put("<Rhbproz>", "0,00");
									SystemConfig.hmAdrRDaten.put("<Rhbgesamt>", "0,00");
									SystemConfig.hmAdrRDaten.put("<Rweganzahl>","----");
									SystemConfig.hmAdrRDaten.put("<Rwegpos>","----");
									SystemConfig.hmAdrRDaten.put("<Rwegpreis>", "0,00");
									SystemConfig.hmAdrRDaten.put("<Rwegproz>", "0,00");
									SystemConfig.hmAdrRDaten.put("<Rweggesamt>", "0,00");
								}
								/*******************************/
								

							}else{
								JOptionPane.showMessageDialog(null, "Dieser Kostenträger kennt keine Weg-Pauschale, geben Sie im Patientenstamm die Anzahl Kilometer an" );
								SystemConfig.hmAdrRDaten.put("<Rweganzahl>","----");
								SystemConfig.hmAdrRDaten.put("<Rwegpos>","----");
								SystemConfig.hmAdrRDaten.put("<Rwegpreis>", "0,00");
								SystemConfig.hmAdrRDaten.put("<Rwegproz>", "0,00");
								SystemConfig.hmAdrRDaten.put("<Rweggesamt>", "0,00");
							}
							
						}
					}else{// es können keine Kilometer abgerechnet werden
						SystemConfig.hmAdrRDaten.put("<Rwegpos>","----");	
						preis = PreisUeberPosition(SystemConfig.hmAdrRDaten.get("<Rwegpos>"),
								zm.preisgruppe,SystemConfig.hmAdrRDaten.get("<Rnummer>").substring(0,2),true);
						if(preis != null){
							/*******************************/
							bdpreis = new BigDecimal(new Double(preis));
							bdposwert = bdpreis.multiply(BigDecimal.valueOf(new Double(realhbAnz)));
							retobj[0] = ((BigDecimal)retobj[0]).add(BigDecimal.valueOf(bdposwert.doubleValue()));
							SystemConfig.hmAdrRDaten.put("<Rwegpreis>", dfx.format(bdpreis.doubleValue()));
							if(zz.equals("1")){// Zuzahlungspflichtig
								bdrezgeb = bdpreis.divide(BigDecimal.valueOf(new Double(10.000)));
								testpr = bdrezgeb.setScale(2, BigDecimal.ROUND_HALF_UP);
								bdendrezgeb = testpr.multiply(BigDecimal.valueOf(new Double(zm.gesamtZahl)));
								SystemConfig.hmAdrRDaten.put("<Rwegproz>", dfx.format(testpr.doubleValue()));
								SystemConfig.hmAdrRDaten.put("<Rweganzahl>",Integer.valueOf(zm.gesamtZahl).toString() );							
								SystemConfig.hmAdrRDaten.put("<Rweggesamt>", dfx.format(bdendrezgeb.doubleValue()));
								retobj[1] = ((Double)retobj[1]) +bdendrezgeb.doubleValue();
							}else{
								SystemConfig.hmAdrRDaten.put("<Rhbanzahl>","----");
								SystemConfig.hmAdrRDaten.put("<Rhbpos>","----");
								SystemConfig.hmAdrRDaten.put("<Rhbpreis>", "0,00");
								SystemConfig.hmAdrRDaten.put("<Rhbproz>", "0,00");
								SystemConfig.hmAdrRDaten.put("<Rhbgesamt>", "0,00");
								SystemConfig.hmAdrRDaten.put("<Rweganzahl>","----");
								SystemConfig.hmAdrRDaten.put("<Rwegpos>","----");
								SystemConfig.hmAdrRDaten.put("<Rwegpreis>", "0,00");
								SystemConfig.hmAdrRDaten.put("<Rwegproz>", "0,00");
								SystemConfig.hmAdrRDaten.put("<Rweggesamt>", "0,00");
							}
							
						}else{
							SystemConfig.hmAdrRDaten.put("<Rweganzahl>","----");
							SystemConfig.hmAdrRDaten.put("<Rwegpos>","----");
							SystemConfig.hmAdrRDaten.put("<Rwegpreis>", "0,00");
							SystemConfig.hmAdrRDaten.put("<Rwegproz>", "0,00");
							SystemConfig.hmAdrRDaten.put("<Rweggesamt>", "0,00");
						}
						

					}
				}else{//nur Mit-Hausbesuch
					SystemConfig.hmAdrRDaten.put("<Rhbpos>",hbmit); 
					preis = PreisUeberPosition(SystemConfig.hmAdrRDaten.get("<Rhbpos>"),
							zm.preisgruppe,SystemConfig.hmAdrRDaten.get("<Rnummer>").substring(0,2),true);
					/*******************************/
					if(preis != null){
						bdpreis = new BigDecimal(new Double(preis));
						bdposwert = bdpreis.multiply(BigDecimal.valueOf(new Double(realhbAnz)));
						retobj[0] = ((BigDecimal)retobj[0]).add(BigDecimal.valueOf(bdposwert.doubleValue()));
						if(zz.equals("1")){// Zuzahlungspflichtig
							SystemConfig.hmAdrRDaten.put("<Rhbpreis>", preis);
							bdrezgeb = bdpreis.divide(BigDecimal.valueOf(new Double(10.000)));
							testpr = bdrezgeb.setScale(2, BigDecimal.ROUND_HALF_UP);
							bdendrezgeb = testpr.multiply(BigDecimal.valueOf(new Double(zm.gesamtZahl)));
							SystemConfig.hmAdrRDaten.put("<Rhbproz>", dfx.format(testpr.doubleValue()));
							SystemConfig.hmAdrRDaten.put("<Rhbgesamt>", dfx.format(bdendrezgeb.doubleValue()));
							//SystemConfig.hmAdrRDaten.put("<Rweganzahl>",Integer.valueOf(zm.gesamtZahl).toString() );					
							SystemConfig.hmAdrRDaten.put("<Rwegpos>","----" );
							SystemConfig.hmAdrRDaten.put("<Rwpreis>","0,00" );						
							SystemConfig.hmAdrRDaten.put("<Rweganzahl>","----" );
							retobj[1] = ((Double)retobj[1]) +bdendrezgeb.doubleValue();
						}else{
							SystemConfig.hmAdrRDaten.put("<Rhbanzahl>","----");
							SystemConfig.hmAdrRDaten.put("<Rhbpos>","----");
							SystemConfig.hmAdrRDaten.put("<Rhbpreis>", "0,00");
							SystemConfig.hmAdrRDaten.put("<Rhbproz>", "0,00");
							SystemConfig.hmAdrRDaten.put("<Rhbgesamt>", "0,00");
							SystemConfig.hmAdrRDaten.put("<Rweganzahl>","----");
							SystemConfig.hmAdrRDaten.put("<Rwegpos>","----");
							SystemConfig.hmAdrRDaten.put("<Rwegpreis>", "0,00");
							SystemConfig.hmAdrRDaten.put("<Rwegproz>", "0,00");
							SystemConfig.hmAdrRDaten.put("<Rweggesamt>", "0,00");
						}
					}else{
						SystemConfig.hmAdrRDaten.put("<Rhbanzahl>","----");
						SystemConfig.hmAdrRDaten.put("<Rhbpos>","----");
						SystemConfig.hmAdrRDaten.put("<Rhbpreis>", "0,00");
						SystemConfig.hmAdrRDaten.put("<Rhbproz>", "0,00");
						SystemConfig.hmAdrRDaten.put("<Rhbgesamt>", "0,00");
						SystemConfig.hmAdrRDaten.put("<Rweganzahl>","----");
						SystemConfig.hmAdrRDaten.put("<Rwegpos>","----");
						SystemConfig.hmAdrRDaten.put("<Rwegpreis>", "0,00");
						SystemConfig.hmAdrRDaten.put("<Rwegproz>", "0,00");
						SystemConfig.hmAdrRDaten.put("<Rweggesamt>", "0,00");
					}
					/*******************************/
				}
			}else{//nicht im Heim
				//System.out.println("Der Hausbesuch ist nicht in einem Heim");
				SystemConfig.hmAdrRDaten.put("<Rhbpos>",hbpos);
				preis = PreisUeberPosition(SystemConfig.hmAdrRDaten.get("<Rhbpos>"),
						zm.preisgruppe,SystemConfig.hmAdrRDaten.get("<Rnummer>").substring(0,2),true);
				/*******************************/
				bdpreis = new BigDecimal(new Double(preis));
				bdposwert = bdpreis.multiply(BigDecimal.valueOf(new Double(realhbAnz)));
				retobj[0] = ((BigDecimal)retobj[0]).add(BigDecimal.valueOf(bdposwert.doubleValue()));
				SystemConfig.hmAdrRDaten.put("<Rhbpreis>", preis);

				bdrezgeb = bdpreis.divide(BigDecimal.valueOf(new Double(10.000)));
				testpr = bdrezgeb.setScale(2, BigDecimal.ROUND_HALF_UP);
				bdendrezgeb = testpr.multiply(BigDecimal.valueOf(new Double(zm.gesamtZahl)));
				SystemConfig.hmAdrRDaten.put("<Rhbproz>", dfx.format(testpr.doubleValue()));
				SystemConfig.hmAdrRDaten.put("<Rhbgesamt>", dfx.format(bdendrezgeb.doubleValue()));
				retobj[1] = ((Double)retobj[1]) +bdendrezgeb.doubleValue();
				/*******************************/
				

				if(!kmgeld.equals("")){// Wenn Kilometer abgerechnet werden k�nnen
					//System.out.println("Es könnten Kilometer abgerechnet werden");
					if( kmBesserAlsPauschale(kmpausch,kmgeld,Double.parseDouble(Integer.toString(zm.km)),zm.preisgruppe,RezTools.putRezNrGetDisziplin(SystemConfig.hmAdrRDaten.get("<Rnummer>"))) ){
						//Kilometerabrechnung besser als Pauschale
						preis = PreisUeberPosition(kmgeld,
								zm.preisgruppe,SystemConfig.hmAdrRDaten.get("<Rnummer>").substring(0,2),true);
						SystemConfig.hmAdrRDaten.put("<Rwegpos>",""+zm.km+"km*"+preis );
						/*******************************/
						
						bdpreis = new BigDecimal(new Double(preis)).multiply(new BigDecimal(new Double(zm.km)));
						bdposwert = bdpreis.multiply(BigDecimal.valueOf(new Double(realhbAnz)));
						retobj[0] = ((BigDecimal)retobj[0]).add(BigDecimal.valueOf(bdposwert.doubleValue()));
						SystemConfig.hmAdrRDaten.put("<Rwegpreis>", dfx.format(bdpreis.doubleValue()));

						bdrezgeb = bdpreis.divide(BigDecimal.valueOf(new Double(10.000)));
						testpr = bdrezgeb.setScale(2, BigDecimal.ROUND_HALF_UP);
						bdendrezgeb = testpr.multiply(BigDecimal.valueOf(new Double(zm.gesamtZahl)));
						SystemConfig.hmAdrRDaten.put("<Rwegproz>", dfx.format(testpr.doubleValue()));
						SystemConfig.hmAdrRDaten.put("<Rweggesamt>", dfx.format(bdendrezgeb.doubleValue()));
						SystemConfig.hmAdrRDaten.put("<Rweganzahl>",Integer.valueOf(zm.gesamtZahl).toString() );
						retobj[1] = ((Double)retobj[1]) +bdendrezgeb.doubleValue();
						/*******************************/



					}else{
						//System.out.println("Es wurden keine Kilometer angegeben also wird nach Ortspauschale abgerechnet");
						if(!kmpausch.equals("")){//Wenn die Kasse keine Pauschale zur Verfügung stellt
							SystemConfig.hmAdrRDaten.put("<Rwegpos>",kmpausch);	
							preis = PreisUeberPosition(SystemConfig.hmAdrRDaten.get("<Rwegpos>"),
									zm.preisgruppe,SystemConfig.hmAdrRDaten.get("<Rnummer>").substring(0,2),true);
							/*******************************/
							bdpreis = new BigDecimal(new Double(preis));
							bdposwert = bdpreis.multiply(BigDecimal.valueOf(new Double(realhbAnz)));
							retobj[0] = ((BigDecimal)retobj[0]).add(BigDecimal.valueOf(bdposwert.doubleValue()));
							SystemConfig.hmAdrRDaten.put("<Rwegpreis>", dfx.format(bdpreis.doubleValue()));

							bdrezgeb = bdpreis.divide(BigDecimal.valueOf(new Double(10.000)));
							testpr = bdrezgeb.setScale(2, BigDecimal.ROUND_HALF_UP);
							bdendrezgeb = testpr.multiply(BigDecimal.valueOf(new Double(zm.gesamtZahl)));
							SystemConfig.hmAdrRDaten.put("<Rwegproz>", dfx.format(testpr.doubleValue()));
							SystemConfig.hmAdrRDaten.put("<Rweggesamt>", dfx.format(bdendrezgeb.doubleValue()));
							SystemConfig.hmAdrRDaten.put("<Rweganzahl>",Integer.valueOf(zm.gesamtZahl).toString() );
							retobj[1] = ((Double)retobj[1]) +bdendrezgeb.doubleValue();
							/*******************************/
						}else{
							JOptionPane.showMessageDialog(null, "Dieser Kostenträger kennt keine Weg-Pauschale, geben Sie im Patientenstamm die Anzahl Kilometer an" );
							SystemConfig.hmAdrRDaten.put("<Rwegpos>","----");
							SystemConfig.hmAdrRDaten.put("<Rweganzahl>","----");						
							SystemConfig.hmAdrRDaten.put("<Rwegpreis>", "0,00");
							SystemConfig.hmAdrRDaten.put("<Rwegproz>", "0,00");
							SystemConfig.hmAdrRDaten.put("<Rweggesamt>", "0,00");

						}
					}
				}else{// es können keine Kilometer abgerechnet werden
					SystemConfig.hmAdrRDaten.put("<Rwegpos>","----");		
					SystemConfig.hmAdrRDaten.put("<Rweganzahl>","----");				
					SystemConfig.hmAdrRDaten.put("<Rwegpreis>", "0,00");
					SystemConfig.hmAdrRDaten.put("<Rwegproz>", "0,00");
					SystemConfig.hmAdrRDaten.put("<Rweggesamt>", "0,00");
					

				}
			}
		}else{
			SystemConfig.hmAdrRDaten.put("<Rhbanzahl>","----");
			SystemConfig.hmAdrRDaten.put("<Rhbpos>","----");
			SystemConfig.hmAdrRDaten.put("<Rhbpreis>", "0,00");
			SystemConfig.hmAdrRDaten.put("<Rhbproz>", "0,00");
			SystemConfig.hmAdrRDaten.put("<Rhbgesamt>", "0,00");
			SystemConfig.hmAdrRDaten.put("<Rweganzahl>","----");
			SystemConfig.hmAdrRDaten.put("<Rwegpos>","----");
			SystemConfig.hmAdrRDaten.put("<Rwegpreis>", "0,00");
			SystemConfig.hmAdrRDaten.put("<Rwegproz>", "0,00");
			SystemConfig.hmAdrRDaten.put("<Rweggesamt>", "0,00");
		}
		//System.out.println("Der Rezeptwert = "+retobj[0]);
		return retobj;
		/*****************************************************/		
		
	}
	public static void constructVirginHMap(){
		try{
		SystemConfig.hmAdrRDaten.put("<Rid>",(String)Reha.thisClass.patpanel.vecaktrez.get(35) );
		SystemConfig.hmAdrRDaten.put("<Rnummer>",(String)Reha.thisClass.patpanel.vecaktrez.get(1) );
		SystemConfig.hmAdrRDaten.put("<Rdatum>",DatFunk.sDatInDeutsch((String)Reha.thisClass.patpanel.vecaktrez.get(2)) );
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public static void constructFormularHMap(){
		try{
			DecimalFormat dfx = new DecimalFormat( "0.00" );	
		SystemConfig.hmAdrRDaten.put("<Rid>",(String)Reha.thisClass.patpanel.vecaktrez.get(35) );
		SystemConfig.hmAdrRDaten.put("<Rnummer>",(String)Reha.thisClass.patpanel.vecaktrez.get(1) );
		SystemConfig.hmAdrRDaten.put("<Rdatum>",DatFunk.sDatInDeutsch((String)Reha.thisClass.patpanel.vecaktrez.get(2)) );
		for(int i = 3; i < 7;i++){
			if( ! Reha.thisClass.patpanel.vecaktrez.get(i).equals("0") ){
				SystemConfig.hmAdrRDaten.put("<Rposition"+(i-2)+">",(String)Reha.thisClass.patpanel.vecaktrez.get(45+i) );
				Double preis = Double.parseDouble((String)Reha.thisClass.patpanel.vecaktrez.get(15+i));

				SystemConfig.hmAdrRDaten.put("<Rpreis"+(i-2)+">", dfx.format(preis).replace(".",",") );
				SystemConfig.hmAdrRDaten.put("<Ranzahl"+(i-2)+">", (String)Reha.thisClass.patpanel.vecaktrez.get(i) );
				BigDecimal gesamt = BigDecimal.valueOf(preis).multiply(BigDecimal.valueOf(Double.parseDouble((String)Reha.thisClass.patpanel.vecaktrez.get(i) ))) ;
				SystemConfig.hmAdrRDaten.put("<Rgesamt"+(i-2)+">", dfx.format(gesamt).replace(".",",") );
			}
		}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	/***************************************************/
	/***************************************************/
	/******Funktionen für Abrechnung nach §302**********/
	/***************************************************/
	public static Object[] unter18Check(Vector<Vector<Object>> behandlungsfall,String geburtstag){
						// unter 18               ab Vector x über 18      gesplittet
		Object[] ret = {Boolean.valueOf(true),behandlungsfall.size(),Boolean.valueOf(false)};
		String tag1 = (String)behandlungsfall.get(behandlungsfall.size()-1).get(0);
		String tag2 = (String)behandlungsfall.get(0).get(0);
		if(DatFunk.Unter18(tag1, geburtstag)){
			return ret;
		}
		if( (!DatFunk.Unter18(tag1, geburtstag)) && (!DatFunk.Unter18(tag2, geburtstag))){
			ret[0] = false;
			ret[1] = -1;
			ret[2] = false;
			return ret;
		}

		int i;
		for(i = 0; i < behandlungsfall.size();i++){
			tag1 = (String)behandlungsfall.get(i).get(0);
			if(! DatFunk.Unter18(tag1, geburtstag)){
				break;
			}
		}
		ret[0] = true;
		ret[1] = i;
		ret[2] = true;
		return ret;
	}
	/***************************************************/
	/***************************************************/
	public static Object[] jahresWechselCheck(Vector<Vector<Object>> behandlungsfall,boolean unter18){
		//                Jahreswechsel       ab Position  vollständig im alten Jahr
		//unter18 wird hier nicht mehr ausgewertet, als Parameter aber noch belassen
		Object[]  ret = {Boolean.valueOf(false), -1       ,Boolean.valueOf(false)};
		if( ((String)behandlungsfall.get(0).get(0)).endsWith(SystemConfig.aktJahr)){
			return ret;
		}
		for(int i = 0;i < behandlungsfall.size();i++){
			if(! ((String)behandlungsfall.get(i).get(0)).endsWith(SystemConfig.aktJahr)){
					ret[0] = true;
					ret[2] = true;
			}else{
				ret[0] = true;
				ret[1] = i;
				ret[2] = false;
				break;				
			}
		}
		return ret;
	}
	/***************************************************/
	
	public static void fuelleVolleTabelle(final String reznr,final String rezbehandler){
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				try{
					Vector<Vector<String>> vec = SqlInfo.holeFelder("select pat_intern,rez_datum,id from verordn where rez_nr = '"+reznr+"' LIMIT 1" );
					String cmd = "insert into volle set rez_nr='"+reznr+"', "+
					"pat_intern='"+vec.get(0).get(0)+"', behandler='"+rezbehandler+"', "+
					"fertigam='"+DatFunk.sDatInSQL(DatFunk.sHeute())+"', "+
					"rez_datum='"+vec.get(0).get(1)+"', rezid='"+vec.get(0).get(2)+"'";
					SqlInfo.sqlAusfuehren(cmd);
				}catch(Exception ex){
					JOptionPane.showMessageDialog(null,"Fehler bei der Ausführung 'fuelleVolleTabelle'");
				}
				return null;
			}
		}.execute();
	}
	
	/***************************************************/
	/***************************************************/

}
class ZuzahlModell{
	public int gesamtZahl;
	public boolean allefrei;
	public boolean allezuzahl;
	public boolean anfangfrei;
	public int teil1;
	public int teil2;
	public int preisgruppe;
	public boolean hausbesuch;
	boolean hbvoll;
	boolean hbheim;
	int km;

	public ZuzahlModell(){
		
	}
}
