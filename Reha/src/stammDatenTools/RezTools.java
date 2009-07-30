package stammDatenTools;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Vector;

import patientenFenster.PatGrundPanel;

import sqlTools.SqlInfo;
import systemEinstellungen.SystemConfig;
import terminKalender.datFunk;

public class RezTools {
	
	public static Vector<String> holeEinzelTermineAusRezept(String xreznr){
		Vector<String> xvec = null;
		Vector retvec = new Vector();
		xvec = SqlInfo.holeSatz("verordn", "termine", "rez_nr='"+xreznr+"'", Arrays.asList(new String[] {}));			

		String terms = (String) xvec.get(0);
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
	public static int testeRezGebArt(String srez){
		int iret = 0;
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
