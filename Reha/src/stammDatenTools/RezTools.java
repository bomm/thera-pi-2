package stammDatenTools;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Vector;

import patientenFenster.AktuelleRezepte;
import patientenFenster.PatGrundPanel;

import sqlTools.SqlInfo;
import systemEinstellungen.SystemConfig;
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
		return (Vector)retvec.clone();
	}
	/************************************************************/
	public static int testeRezGebArt(String srez,String termine){
		int iret = 0;
		//Vector<String> patvec = SqlInfo.holeSatz("pat5", "geboren,jahrfrei", "pat_intern='"+xvec.get(1)+"'", Arrays.asList(new String[] {}));
		//String patGeboren = datFunk.sDatInDeutsch(patvec.get(0));
		//String patJahrfrei = datFunk.sDatInDeutsch(patvec.get(0));
		//
		String patGeboren = "";
		String patJahrfrei = "";
		String neuePreiseab = "";
		String befreitVorjahr = "";
		String kilometer = "";
		String pauschale = "";
		String nachherfrei = "";
		String vorherfrei = "";
		Vector vAktTermine = null;
		boolean bNeuePreise = false;
		long lNeuePreise = 0;
		boolean bTermine = false;
		boolean bUnter18 = false;
		boolean bVorjahrFrei = false;
		String sVorjahrDatum = "";
		boolean bHausbesuch = false;
		boolean bHeimbewohner = false;
		boolean bMitJahresWechsel = false;
		boolean bHbVoll = false;
		boolean bPauschal = false;
		boolean bDiesesRezFrei = false;
		int iKilometer = 0;
		int iTermine = -1;
		int iVorjahr = -1;
		int iFreie = 0;
		
		//1. Schritt haben wir bereits Termineinträge die man auswerten kann
		if( (vAktTermine = holeEinzelTermineAusRezept("",termine)).size() > 0 ){
			// Es gibt Termine in der Tabelle
			bTermine = true;
			iTermine = vAktTermine.size();
			if( ! ((String)vAktTermine.get(0)).substring(6).equals(SystemConfig.aktJahr)){
				bMitJahresWechsel = true;
			}
		}
		System.out.println(vAktTermine);

		//2. Schritt war der Pat zum Zeitpunkt der Rezeptanlage unter 18, wenn nein umso besser;
		if( (boolean) ((String)PatGrundPanel.thisClass.vecaktrez.get(60)).equals("T") ){
			// Zum Zeitpunkt der Rezeptanlage unter 18, Prüfen ob während Behandlung
			bUnter18 = true;
		}
		//3. Schritt war der Patient im Vorjahr befreit??? Wenn nein umso besser.
		String vorjahr;
		if( (! (vorjahr = ((String)PatGrundPanel.thisClass.patDaten.get(69)).trim()).equals("") ) && bMitJahresWechsel){
			// War im Vorjahr Zuzahlungsbefreit
			bVorjahrFrei = true;
			iVorjahr = new Integer(vorjahr);
			//iVorjahr = new Integer(((String)PatGrundPanel.thisClass.patDaten.get(69)));
			sVorjahrDatum = "31.12."+vorjahr;
		}
		//4. Schritt ist der Patient aktuell befreit?? 
		if((boolean) ((String)PatGrundPanel.thisClass.vecaktrez.get(43)).equals("T")){
			// Rezept ist Hausbesuchsrezept
			bHausbesuch = true;
			if((boolean) ((String)PatGrundPanel.thisClass.patDaten.get(44)).equals("T") ){
				//Hausbesuch bei Heimbewohner
				bHeimbewohner = true;
			}
			if((boolean) ((String)PatGrundPanel.thisClass.vecaktrez.get(61)).equals("T")){
				//Hausbesuch voll abrechnen
				bHbVoll = true;
			}
			if(! ((String)PatGrundPanel.thisClass.patDaten.get(48)).trim().equals("") ){
				// Pauschale für Weggebühr
				
				iKilometer = new Integer(((String)PatGrundPanel.thisClass.patDaten.get(48)));
				if(iKilometer <= 0){
					bPauschal = true;
				}
			}else{
				bPauschal = true;
				iKilometer = 0;
			}

		}
		//5. Schritt ist der Patient zwar befreit aber noch nicht für dieses Rezept
		if((!bUnter18) && (!bVorjahrFrei) &&  (!bHausbesuch)){
			// ganz normale Rezeptgebühren
			iret = 0;
		}
		
		/*
		System.out.println("Rezept angelegt bei unter 18: "+bUnter18);
		System.out.println("Patient war im Vorjahr befreit: "+bVorjahrFrei);
		System.out.println("Rezept ist Hausbesuchsrezept: "+bHausbesuch);
		System.out.println("Im Fall von Hausbesuch - voll abrechnen: "+bHbVoll);
		System.out.println("Weggebührpauschale verwenden: "+bPauschal);
		System.out.println("Im Fall von km-Abrechnung km-Anzahl: "+iKilometer);
		System.out.println("Patient ist Heimbewohner: "+bHeimbewohner);
		*/
		if(bTermine){
			for(int i = 0;i < iTermine;i++){
				if(bVorjahrFrei){
					try{
						if(datFunk.DatumsWert((String)vAktTermine.get(i)) <= datFunk.DatumsWert((String)"01.01."+SystemConfig.aktJahr) ){
							iFreie++;
						}
					}catch(Exception ex){
						
					}
				}
			}
		}
		//System.out.println("Termine ohne Zuzahlung: "+iFreie);
		//Zunächst testen ob sich das Rezept über den Jahreswechsel zieht.
		//Prüfen ob Terminanzahl vollständig
		// 0 = ganz normale Rezeptgebührenberechnung ohne HB
		// 1 = normale Rezeptgebühren mit HB normal
		// 2 = normale Rezeptgebühren mit HB aber in soz. Einrichtung
		// 3 = zu Beginn befreit und jetzt ZuZahl-pflichtig
		// 4 = zu Beginn befreit und jetzt ZuZahl-pflichtig mitHB normal
		// 5 = zu Beginn befreit und jetzt ZuZahl-pflichtig mitHB in soz. Einrichtung		
		// 6 = Preisumstellung der Krankenkasse		
		if(iret==0){
			constructNormalRezHMap();
		}
		return iret;
	}
	public static void constructNormalRezHMap(){
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

}
