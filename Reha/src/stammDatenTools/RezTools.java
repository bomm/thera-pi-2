package stammDatenTools;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.JOptionPane;

import patientenFenster.AktuelleRezepte;
import patientenFenster.PatGrundPanel;

import sqlTools.SqlInfo;
import systemEinstellungen.SystemConfig;
import systemTools.StringTools;
import terminKalender.ParameterLaden;
import terminKalender.datFunk;

public class RezTools {
	
	public static Vector<String> holeEinzelTermineAusRezept(String xreznr,String termine){
		Vector<String> xvec = null;
		Vector retvec = new Vector();
		String terms = null;
		if(termine.equals("")){
			xvec = SqlInfo.holeSatz("verordn", "termine,pat_intern", "rez_nr='"+xreznr+"'", Arrays.asList(new String[] {}));			
			if(xvec.size()==0){
				return (Vector)retvec.clone();
			}else{
				terms = (String) xvec.get(0);	
			}
		}else{
			terms = termine;
		}
		if(terms==null){
			return (Vector)retvec.clone();
		}
		if(terms.equals("")){
			return (Vector)retvec.clone();
		}
		String[] tlines = terms.split("\n");
		int lines = tlines.length;

		for(int i = 0;i<lines;i++){
			String[] terdat = tlines[i].split("@");
			int ieinzel = terdat.length;
			retvec.add(new String((terdat[0].trim().equals("") ? "  .  .    " : terdat[0])));
		}
		Comparator comparator = new Comparator<String>() {
			public int compare(String s1, String s2) {
		        String strings1 = datFunk.sDatInSQL(s1);
		        String strings2 = datFunk.sDatInSQL(s2);
		        return strings1.compareTo(strings2);
		    }
		};	
		Collections.sort(retvec,comparator);
		return (Vector)retvec.clone();
	}
	/************************************************************/
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
	public static int testeRezGebArt(boolean hintergrund,String srez,String termine){
		int iret = 0;
		Vector<String> vAktTermine = null;
		boolean bTermine = false;
		int iTermine = -1;
		boolean bMitJahresWechsel = false;
		ZuzahlModell zm = new ZuzahlModell();
		//Vector<String> patvec = SqlInfo.holeSatz("pat5", "geboren,jahrfrei", "pat_intern='"+xvec.get(1)+"'", Arrays.asList(new String[] {}));
		//String patGeboren = datFunk.sDatInDeutsch(patvec.get(0));
		//String patJahrfrei = datFunk.sDatInDeutsch(patvec.get(0));
		//
		
		
		//1. Schritt haben wir bereits Termineinträge die man auswerten kann
		if( (vAktTermine = holeEinzelTermineAusRezept("",termine)).size() > 0 ){
			// Es gibt Termine in der Tabelle
			bTermine = true;
			iTermine = vAktTermine.size();
			if( ((String)vAktTermine.get(0)).substring(6).equals(SystemConfig.vorJahr)){
				bMitJahresWechsel = true;
			}
		}

		System.out.println(vAktTermine);
		for(int i = 0;i < 1;i++){

			if(new Integer(((String)PatGrundPanel.thisClass.vecaktrez.get(63))) <= 0){
				// Kasse erfordert keine Zuzahlung
				zm.allefrei = true;
				iret = 0;
				break;
			}
			if(new Integer(((String)PatGrundPanel.thisClass.vecaktrez.get(39))) == 1){
				// Hat bereits bezahlt normal behandeln (zzstatus == 1)
				zm.allezuzahl = true;
				iret = 2;
				break;
			}

			/************************ Jetzt der Ober-Scheißdreck für den Achtzehner-Test***********************/
			if((boolean) ((String)PatGrundPanel.thisClass.vecaktrez.get(60)).equals("T")){
				// Es ist ein unter 18 Jahre Test notwendig
				if(bTermine){
					
					int [] test = ZuzahlTools.terminNachAchtzehn(vAktTermine,datFunk.sDatInDeutsch((String)PatGrundPanel.thisClass.patDaten.get(4))); 
					if( test[0] > 0 ){
						//muß zuzahlen
						zm.allefrei = false;
						if(test[1] > 0){
							zm.allefrei = false;
							zm.allezuzahl = false;
							zm.anfangfrei = true;
							zm.teil1 = test[1];
							System.out.println("Splitten frei für "+test[1]+" Tage, bezahlen für "+(maxAnzahl()-test[1]));
							iret = 1;
						}else{
							zm.allezuzahl = true;
							zm.teil1 = test[1];
							System.out.println("Jeden Termin bezahlen insgesamt bezahlen für "+(maxAnzahl()-test[1]));
							iret = 2;
						}
					}else{
						//Voll befreit
						System.out.println("Frei für "+test[1]+" Tage - also alle");
						zm.allefrei = true;
						iret = 0;
					}
				}else{
					//Es stehen keine Termine für Analyse zur Verfügung also muß das Fenster für manuelle Eingabe geöffnet werden!!
					String geburtstag = datFunk.sDatInDeutsch(PatGrundPanel.thisClass.patDaten.get(4));
					String stichtag = datFunk.sHeute().substring(0,6)+new Integer(new Integer(SystemConfig.aktJahr)-18).toString();
					if(datFunk.TageDifferenz(geburtstag ,stichtag) >= 0 ){
						System.out.println("Normale Zuzahlung....");
						zm.allefrei = false;
						zm.allezuzahl = true;
						iret = 2;
					}else{
						System.out.println("Alle Frei....");						
						zm.allefrei = true;
						zm.allezuzahl = false;
						iret = 0;
					}
				}
				//iret = 1;
				break;
			}

			/************************ Keine Befreiung Aktuell und keine Vorjahr (Normalfall************************/
			if((boolean) ((String)PatGrundPanel.thisClass.vecaktrez.get(12)).equals("F") && 
					(((String)PatGrundPanel.thisClass.vecaktrez.get(59)).trim().equals("")) ){
				// Es liegt weder eine Befreiung für dieses noch für letztes Jahr vor.
				// Standard
				iret = 2;
				break;
			}
			/************************ Aktuell Befreit und im Vorjahr auch befreit************************/			
			if((boolean) ((String)PatGrundPanel.thisClass.vecaktrez.get(12)).equals("T") && 
					(((String)PatGrundPanel.thisClass.vecaktrez.get(59)).equals(SystemConfig.vorJahr)) ){
				// Es liegt eine Befreiung vor und im Vorjahr ebenfenfalls befreit
				iret = 0;
				break;
			}
			/************************ aktuell Nicht frei, Vorjahr frei************************/
			if((boolean) ((String)PatGrundPanel.thisClass.vecaktrez.get(12)).equals("F") && 
					(((String)PatGrundPanel.thisClass.vecaktrez.get(59)).trim().equals(SystemConfig.vorJahr)) ){
				if(!bMitJahresWechsel){//Alle Termine aktuell
					iret = 2;
				}else{// es gibt Termine im Vorjahr
					Object[] obj = JahresWechsel(vAktTermine,SystemConfig.vorJahr);
					if(!(Boolean)obj[0]){// alle Termine waren im Vorjahr 
						iret = 0;
					}else{// gemischte Termine
						System.out.println("Termine aus dem Vorjahr(frei) = "+obj[1]+" Termine aus diesem Jahr(Zuzahlung) = "+obj[2]);
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
			if((boolean) ((String)PatGrundPanel.thisClass.vecaktrez.get(12)).equals("T") && 
					(((String)PatGrundPanel.thisClass.vecaktrez.get(59)).trim().equals("")) ){
				if(!bMitJahresWechsel){//Alle Termine aktuell
					iret = 0;
				}else{// es gibt Termine im Vorjahr
					Object[] obj = JahresWechsel(vAktTermine,SystemConfig.vorJahr);
					if(!(Boolean)obj[0]){// alle Termine waren im Vorjahr 
						iret = 2;
					}else{// gemischte Termine
						System.out.println("Termine aus dem Vorjahr(Zuzahlung) = "+obj[1]+" Termine aus diesem Jahr(frei) = "+obj[2]);
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
		

		zm.hausbesuch = ((String)PatGrundPanel.thisClass.vecaktrez.get(43)).equals("T");
		zm.hbvoll = ((String)PatGrundPanel.thisClass.vecaktrez.get(61)).equals("T");
		zm.hbheim = ((String)PatGrundPanel.thisClass.patDaten.get(44)).equals("T");
		zm.km = new Integer(StringTools.ZahlTest(((String)PatGrundPanel.thisClass.patDaten.get(48))));
		zm.preisgruppe = new Integer(((String)PatGrundPanel.thisClass.vecaktrez.get(41)));
		//Hausbesuch als logischen wert

		if(iret==0){
			constructGanzFreiRezHMap(zm);
		}
		if(iret==1){
			constructAnfangFreiRezHMap(zm);
		}
		if(iret==2){
			constructNormalRezHMap(zm);
		}
		if(iret==3){
			constructEndeFreiRezHMap(zm);
		}

		System.out.println("ZZ-Variante = "+iret);
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
	public static void constructNormalRezHMap(ZuzahlModell zm){
		/************************************/
		Double rezgeb = new Double(0.000);
		BigDecimal[] preise = {null,null,null,null};
		BigDecimal xrezgeb = BigDecimal.valueOf(new Double(0.000));
		
		//System.out.println("nach nullzuweisung " +xrezgeb.toString());
		int[] anzahl = {0,0,0,0};
		int[] artdbeh = {0,0,0,0};
		int i;
		BigDecimal einzelpreis = null;
		BigDecimal poswert = null;
		BigDecimal rezwert = BigDecimal.valueOf(new Double(0.000));
		SystemConfig.hmAdrRDaten.put("<Rid>",(String)PatGrundPanel.thisClass.vecaktrez.get(35) );
		SystemConfig.hmAdrRDaten.put("<Rnummer>",(String)PatGrundPanel.thisClass.vecaktrez.get(1) );
		SystemConfig.hmAdrRDaten.put("<Rdatum>",(String)PatGrundPanel.thisClass.vecaktrez.get(2) );		
		for(i = 0;i < 4;i++){
			anzahl[i] = new Integer((String)PatGrundPanel.thisClass.vecaktrez.get(i+3));
			artdbeh[i] = new Integer((String)PatGrundPanel.thisClass.vecaktrez.get(i+8));
			preise[i] = BigDecimal.valueOf(new Double((String)PatGrundPanel.thisClass.vecaktrez.get(i+18)));
		}
		xrezgeb.add(BigDecimal.valueOf(new Double(10.00)));
		rezgeb = 10.00;
		//System.out.println("nach 10.00 zuweisung " +rezgeb.toString());		
		String runden;
		DecimalFormat dfx = new DecimalFormat( "0.00" );
		BigDecimal endpos;
		SystemConfig.hmAdrRDaten.put("<Rnummer>",(String)PatGrundPanel.thisClass.vecaktrez.get(1) );
		SystemConfig.hmAdrRDaten.put("<Rpatid>",(String)PatGrundPanel.thisClass.vecaktrez.get(0) );
		SystemConfig.hmAdrRDaten.put("<Rdatum>",datFunk.sDatInDeutsch( (String)PatGrundPanel.thisClass.vecaktrez.get(2) )  );		
		SystemConfig.hmAdrRDaten.put("<Rpauschale>",dfx.format(rezgeb) );
		
		for(i = 0; i < 4; i++){
			/*
			System.out.println(new Integer(anzahl[i]).toString()+" / "+ 
					new Integer(artdbeh[i]).toString()+" / "+
					preise[i].toString() );
			*/		
			if(artdbeh[i] > 0){
				SystemConfig.hmAdrRDaten.put("<Rposition"+(i+1)+">",(String)PatGrundPanel.thisClass.vecaktrez.get(48+i) );
				SystemConfig.hmAdrRDaten.put("<Rpreis"+(i+1)+">", dfx.format(preise[i]) );
				
				einzelpreis = preise[i].divide(BigDecimal.valueOf(new Double(10.000)));

				poswert = preise[i].multiply(BigDecimal.valueOf(new Double(anzahl[i]))); 
				rezwert = rezwert.add(poswert);
				//System.out.println("Einzelpreis "+i+" = "+einzelpreis);
				BigDecimal testpr = einzelpreis.setScale(2, BigDecimal.ROUND_HALF_UP);
				//System.out.println("test->Einzelpreis "+i+" = "+testpr);

				SystemConfig.hmAdrRDaten.put("<Rproz"+(i+1)+">", dfx.format(testpr) );
				SystemConfig.hmAdrRDaten.put("<Ranzahl"+(i+1)+">", new Integer(anzahl[i]).toString() );
				
				endpos = testpr.multiply(BigDecimal.valueOf(new Double(anzahl[i]))); 
				SystemConfig.hmAdrRDaten.put("<Rgesamt"+(i+1)+">", dfx.format(endpos) );
				rezgeb = rezgeb + endpos.doubleValue();
				//System.out.println(rezgeb.toString());

			}else{
				SystemConfig.hmAdrRDaten.put("<Rposition"+(i+1)+">","----");
				SystemConfig.hmAdrRDaten.put("<Rpreis"+(i+1)+">", "0,00" );
				SystemConfig.hmAdrRDaten.put("<Rproz"+(i+1)+">", "0,00");				
				SystemConfig.hmAdrRDaten.put("<Rgesamt"+(i+1)+">", "0,00" );
				SystemConfig.hmAdrRDaten.put("<Ranzahl"+(i+1)+">", "----" );
						
			}
		}
		/*****************************************************/
		//"<Rhbpos>","<Rwegegeld>"
		/*
		zm.hausbesuch = ((String)PatGrundPanel.thisClass.vecaktrez.get(43)).equals("T");
		zm.hbvoll = ((String)PatGrundPanel.thisClass.vecaktrez.get(61)).equals("T");
		zm.hbheim = ((String)PatGrundPanel.thisClass.patDaten.get(44)).equals("T");
		zm.km = new Integer(StringTools.ZahlTest(((String)PatGrundPanel.thisClass.patDaten.get(48))));
		zm.preisgruppe = new Integer(((String)PatGrundPanel.thisClass.patDaten.get(41)));
		*/
		//"<Rhbpos>","<Rwegpos>","<Rhbpreis>","<Rwegpreis>","<Rhbproz>","<Rwegproz>","<Rhbanzahl>"});
		
		//PreisUeberPosition("29933",zm.preisgruppe,SystemConfig.hmAdrRDaten.get("<Rnummer>").substring(0,2) );
		PreisUeberPosition("29933",2,SystemConfig.hmAdrRDaten.get("<Rnummer>").substring(0,2) );
		if(zm.hausbesuch){ //Hausbesuch
			String zz =  SystemConfig.vHBRegeln.get(zm.preisgruppe-1).get(4);
			String kmgeld = SystemConfig.vHBRegeln.get(zm.preisgruppe-1).get(2);
			String kmpausch = SystemConfig.vHBRegeln.get(zm.preisgruppe-1).get(3);
			if(zm.hbheim){ // und zwar im Heim
				if(zm.hbvoll){// Volle Ziffer abrechnen?
					SystemConfig.hmAdrRDaten.put("<Rhbpos>",SystemConfig.vHBRegeln.get(zm.preisgruppe-1).get(0));
					if(!kmgeld.equals("")){// Wenn Kilometer abgerechnet werden können
						if(zm.km > 0 ){
							SystemConfig.hmAdrRDaten.put("<Rwegpos>",SystemConfig.vHBRegeln.get(zm.preisgruppe-1).get(2));
						}else{// Keine Kilometer angegeben also pauschale verwenden
							if(!kmpausch.equals("")){//Wenn die Kasse keine Pauschale zur Verfügung stellt
								SystemConfig.hmAdrRDaten.put("<Rwegpos>",SystemConfig.vHBRegeln.get(zm.preisgruppe-1).get(3));								
							}else{
								JOptionPane.showMessageDialog(null, "Dieser Kostenträger kennt keine Weg-Pauschale, geben Sie im Patientenstamm die Anzahl Kilometer an" );
								SystemConfig.hmAdrRDaten.put("<Rwegpos>","----");
							}
							
						}
					}else{// es können keine Kilometer abgerechnet werden
						SystemConfig.hmAdrRDaten.put("<Rwegpos>","----");						
					}
				}else{//nur Mit-Hausbesuch
					SystemConfig.hmAdrRDaten.put("<Rhbpos>",SystemConfig.vHBRegeln.get(zm.preisgruppe-1).get(1));					
				}
				if(zz.equals("1")){// Zuzahlungspflichtig
					
				}
			}else{//nicht im Heim
				
				SystemConfig.hmAdrRDaten.put("<Rhbpos>",SystemConfig.vHBRegeln.get(zm.preisgruppe-1).get(0));
				if(!kmgeld.equals("")){// Wenn Kilometer abgerechnet werden können
					if(zm.km > 0 ){
						SystemConfig.hmAdrRDaten.put("<Rwegpos>",SystemConfig.vHBRegeln.get(zm.preisgruppe-1).get(2));
					}else{
						if(!kmpausch.equals("")){//Wenn die Kasse keine Pauschale zur Verfügung stellt
							SystemConfig.hmAdrRDaten.put("<Rwegpos>",SystemConfig.vHBRegeln.get(zm.preisgruppe-1).get(3));								
						}else{
							JOptionPane.showMessageDialog(null, "Dieser Kostenträger kennt keine Weg-Pauschale, geben Sie im Patientenstamm die Anzahl Kilometer an" );
							SystemConfig.hmAdrRDaten.put("<Rwegpos>","----");
						}
					}
				}else{// es können keine Kilometer abgerechnet werden
					SystemConfig.hmAdrRDaten.put("<Rwegpos>","----");						
				}
			}
		}
		/*****************************************************/		
		Double drezwert = rezwert.doubleValue();
		SystemConfig.hmAdrRDaten.put("<Rendbetrag>", dfx.format(rezgeb) );
		SystemConfig.hmAdrRDaten.put("<Rwert>", dfx.format(drezwert) );
		DecimalFormat df = new DecimalFormat( "0.00" );
		String s = df.format( rezgeb);
		//System.out.println("----------------------------------------------------");
		//System.out.println("Endgültige und geparste Rezeptgebühr = "+s+" EUR");
		//System.out.println(SystemConfig.hmAdrRDaten);
		/***********************/
		
		// Hier muß noch Hausbesuchshandling eingebaut werden
		// Ebenso das Wegegeldhandling
	}
	
	public static void constructGanzFreiRezHMap(ZuzahlModell zm){
		SystemConfig.hmAdrRDaten.put("<Rid>",(String)PatGrundPanel.thisClass.vecaktrez.get(35) );
		SystemConfig.hmAdrRDaten.put("<Rnummer>",(String)PatGrundPanel.thisClass.vecaktrez.get(1) );
		SystemConfig.hmAdrRDaten.put("<Rdatum>",(String)PatGrundPanel.thisClass.vecaktrez.get(2) );		
		SystemConfig.hmAdrRDaten.put("<Rpatid>",(String)PatGrundPanel.thisClass.vecaktrez.get(0) );
		SystemConfig.hmAdrRDaten.put("<Rpauschale>","0,00");
		for(int i = 0;i<5;i++){
			SystemConfig.hmAdrRDaten.put("<Rposition"+(i+1)+">","----");
			SystemConfig.hmAdrRDaten.put("<Rpreis"+(i+1)+">", "0,00" );
			SystemConfig.hmAdrRDaten.put("<Rproz"+(i+1)+">", "0,00");				
			SystemConfig.hmAdrRDaten.put("<Rgesamt"+(i+1)+">", "0,00" );
			SystemConfig.hmAdrRDaten.put("<Ranzahl"+(i+1)+">", "----" );
		}
		SystemConfig.hmAdrRDaten.put("<Rendbetrag>", "0,00" );
		SystemConfig.hmAdrRDaten.put("<Rwert>", "0,00" );
	}
	public static void constructAnfangFreiRezHMap(ZuzahlModell zm){
		
	}
	public static void constructEndeFreiRezHMap(ZuzahlModell zm){
		
	}	
	public static Vector<Vector<String>>splitteTermine(String terms){
		Vector<Vector<String>> termine = new Vector<Vector<String>>();
		String[] tlines = terms.split("\n");
		int lines = tlines.length;
		//System.out.println("Anzahl Termine = "+lines);
		Vector<String> tvec = new Vector<String>();
		String[] terdat = null;
		for(int i = 0;i<lines;i++){
			terdat = tlines[i].split("@");
			int ieinzel = terdat.length;
			//System.out.println("Anzahl Splits = "+ieinzel);
			tvec.clear();
			for(int y = 0; y < ieinzel;y++){
					tvec.add(new String((terdat[y].trim().equals("") ? "  .  .    " : terdat[y])));
			}
			//System.out.println("Termivector = "+tvec);
			termine.add((Vector<String>)tvec.clone());
		}
		return (Vector<Vector<String>>) termine.clone();
	}
	
	public static Object[] JahrEnthalten(Vector<String>vtage,String jahr){
		Object[] ret = {new Boolean(false),new Integer(-1)};
		for(int i = 0; i < vtage.size();i++){
			if( ((String)vtage.get(i)).equals(jahr) ){
				ret[0] = true;
				ret[1] = new Integer(i);
				break;
			}
		}
		return ret;
	}
	public static Object[] JahresWechsel(Vector<String>vtage,String jahr){
		Object[] ret = {new Boolean(false),new Integer(-1),new Integer(-1)};
		for(int i = 0; i < vtage.size();i++){
			if(!((String)vtage.get(i)).substring(6).equals(jahr) ){
				ret[0] = true;
				ret[1] = new Integer(i);
				ret[2] = maxAnzahl()-(Integer)ret[1];
				break;
			}
		}
		return ret;
	}
	public static int maxAnzahl(){
		int ret = -1;
		int test;
		for(int i = 3; i < 7;i++){
			test = new Integer(((String)PatGrundPanel.thisClass.vecaktrez.get(i)));
			if(test > ret){
				ret = new Integer(test);
			}
		}
		return ret;
	}
	public static String PreisUeberPosition(String position,int preisgruppe,String disziplin ){
		String ret = "";
		Vector preisvec = null;
		if(disziplin.equals("KG")){
			preisvec = ParameterLaden.vKGPreise;			
		}
		if(disziplin.equals("MA")){
			preisvec = ParameterLaden.vMAPreise;
		}
		if(disziplin.equals("ER")){
			preisvec = ParameterLaden.vERPreise;			
		}
		if(disziplin.equals("LO")){
			preisvec = ParameterLaden.vLOPreise;			
		}
		System.out.println(preisvec);
		return ret;
	}
	

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
