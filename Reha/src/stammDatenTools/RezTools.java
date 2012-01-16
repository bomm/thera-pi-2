package stammDatenTools;

import hauptFenster.Reha;

import java.awt.Point;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.jdesktop.swingworker.SwingWorker;

import sqlTools.SqlInfo;
import systemEinstellungen.SystemConfig;
import systemEinstellungen.SystemPreislisten;
import systemTools.StringTools;
import terminKalender.BestaetigungsDaten;
import terminKalender.DatFunk;
import terminKalender.TerminBestaetigenAuswahlFenster;
import terminKalender.TermineErfassen;

public class RezTools {
	public static final int REZEPT_IST_JETZ_VOLL = 0;
	public static final int REZEPT_IST_BEREITS_VOLL = 1;
	public static final int REZEPT_HAT_LUFT = 2;
	public static final int REZEPT_FEHLER = 3;
	public static final int REZEPT_ABBRUCH = 4;
	public static final int DIALOG_ABBRUCH = -1;
	public static final int DIALOG_OK = 0;
	public static int DIALOG_WERT = 0;
	
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
	
	public static Vector<ArrayList<?>> Y_holePosUndAnzahlAusRezept(String xreznr){
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
	private static Object[] sucheDoppel(int pos,List<String> list,String comperator){
		//System.out.println("Position="+pos+" fistIndex="+list.indexOf(comperator)+" lastIndex="+list.lastIndexOf(comperator));
		if(pos == list.indexOf(comperator)){
			Object[] obj = {true,list.indexOf(comperator),list.lastIndexOf(comperator)};
			return obj.clone();
		}else{
			Object[] obj = {true,list.lastIndexOf(comperator),list.indexOf(comperator)};
			return obj.clone();
		}
	}
	/**************
	 * 
	 * 
	 * Mistding - elendes aber jetzt haben wir dich!
	 * 
	 */
	public static Vector<ArrayList<?>> holePosUndAnzahlAusTerminen(String xreznr){
		Vector<ArrayList<?>> xvec = new Vector<ArrayList<?>>();

		Vector<String> rezvec = SqlInfo.holeSatz("verordn", "termine,pos1,pos2,pos3,"+
				"pos4,kuerzel1,kuerzel2,kuerzel3,kuerzel4,preisgruppe", "rez_nr='"+xreznr+"'", Arrays.asList(new String[] {}));
		Vector<String> termvec = holeEinzelZiffernAusRezept(null,rezvec.get(0));
		
		List<String> list = Arrays.asList(rezvec.get(1),rezvec.get(2),rezvec.get(3),rezvec.get(4));


		
		ArrayList<String> positionen = new ArrayList<String>();
		String behandlungen = null;
		String[] einzelbehandlung = null;
		ArrayList<Integer>anzahl = new ArrayList<Integer>();
		ArrayList<Boolean>vorrangig = new ArrayList<Boolean>();
		ArrayList<Boolean>einzelerlaubt = new ArrayList<Boolean>();
		ArrayList<Object[]>doppelpos = new ArrayList<Object[]>();
		boolean[] bvorrangig = null;
		//String aktpos = "";
		for(int i = 1; i < 5; i++ ){

			if(rezvec.get(i).trim().equals("")){
				break;
				
			}
			positionen.add(String.valueOf(rezvec.get(i)));
			bvorrangig = isVorrangigAndExtra(rezvec.get(i+4),xreznr);
			vorrangig.add(Boolean.valueOf(bvorrangig[0]));
			einzelerlaubt.add(Boolean.valueOf(bvorrangig[1]));
			anzahl.add(0);
			
			if(countOccurence(list,rezvec.get(i)) > 1){
				doppelpos.add(sucheDoppel(i-1,list,rezvec.get(i)));
			}else{
				Object[] obj = {false,i-1,i-1};
				doppelpos.add(obj.clone());	
			}
			
			
		}

		Vector<String> imtag = new Vector<String>();
		Object[] tests = null;
		for(int i = 0; i < termvec.size();i++){
			//Über alle Tage hinweg
			try{
				behandlungen = termvec.get(i);
				if(! behandlungen.equals("")){
					einzelbehandlung = behandlungen.split(",");
					imtag.clear();
					int i2;
					for(i2=0;i2<einzelbehandlung.length;i2++){
						try{
							//Jetzt testen ob Doppelbehandlung
							tests = doppelpos.get(list.indexOf(einzelbehandlung[i2]));
							if((Boolean) tests[0]){
							//Ja Doppelbehandlung
								imtag.add(String.valueOf(einzelbehandlung[i2]));
								//Jetzt testen ob erste oder Zweite
								if(imtag.indexOf(einzelbehandlung[i2]) == imtag.lastIndexOf(einzelbehandlung[i2]) ){
									//Erstes mal
									anzahl.set((Integer)tests[1], anzahl.get((Integer)tests[1])+1);
								}else{
									//Zweites mal
									anzahl.set((Integer)tests[2], anzahl.get((Integer)tests[2])+1);
								}
							}else{
							//Nein keine Doppelbehandlung
								anzahl.set((Integer)tests[1], anzahl.get((Integer)tests[1])+1);
							}
						}catch(Exception ex){
							try{
								String disziplin = RezTools.putRezNrGetDisziplin(xreznr);
								String kuerzel = RezTools.getKurzformFromPos(einzelbehandlung[i2], rezvec.get(9), SystemPreislisten.hmPreise.get(disziplin).get(Integer.parseInt(rezvec.get(9))-1));
								JOptionPane.showMessageDialog(null,"<html><font color='#ff0000' size=+2>Fehler in der Ermittlung der Behandlungspositionen!</font><br><br>"+
										"<b>Bitte kontrollieren sie die bereits gespeicherten Behandlungspositionen!!<br><br>"+
										"Der problematische Termin ist der <font color='#ff0000'>"+(i+1)+".Termin</font>,<br>bestätigte Behandlungsart ist <font color='#ff0000'>"+kuerzel+" ("+einzelbehandlung[i2]+")<br>"+
										"<br>Diese Behandlungsart ist im Rezeptblatt nicht, oder nicht mehr verzeichnet</font><br><br>"+
										"<br>"+
										"<b><font color='#ff0000'>Lösung:</font> Klicken Sie die Termintabelle an, drücken Sie dann die rechte Maustaste und wählen Sie dann die Option<br><br>"+
										"<b><u>\"alle Behandlungsarten den Rezeptdaten angleichen\"</u></b><br>"+
										"</b>oder<br><b><u>\"alle im Rezept gespeicherten Behandlungsarten löschen\"</u></b><br></html>");
									return xvec;
							}catch(Exception ex2){
								JOptionPane.showMessageDialog(null,"<html><font color='#ff0000' size=+2>Fehler in der Ermittlung der Behandlungspositionen!</font><br><br>"+
										"<b>Bitte kontrollieren sie die bereits gespeicherten Behandlungspositionen!!<br><br>"+
										"Der Fehler kann nicht genau lokalisiert werden!<br><br>"+
										"Vermutlich wurden in den bisherigen Terminen Positionen bestätigt, die im Rezeptblatt<br>"+
										"<u>nicht oder nicht mehr aufgeführt sind.</u><br><br>"+
										"<b>Klicken Sie die Termintabelle an, drücken Sie dann die rechte Maustaste und wählen Sie eine Option aus.<b><br></html>");
									return xvec;
							}				
						}
						
					}
				}else{
					for(int i3=0;i3<positionen.size();i3++)
						anzahl.set(i3, anzahl.get(i3)+1);
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		/*
		System.out.println("*************************************************");
		System.out.println(positionen);
		System.out.println(anzahl);
		System.out.println(vorrangig);
		System.out.println(einzelerlaubt);
		System.out.println("*************************************************");
		*/
		xvec.add((ArrayList<?>)positionen.clone());
		xvec.add((ArrayList<?>)anzahl.clone());
		xvec.add((ArrayList<?>)vorrangig.clone());
		xvec.add((ArrayList<?>)einzelerlaubt.clone());
		xvec.add((ArrayList<?>)doppelpos.clone());
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
	public static int countOccurence(List<String> list, String comperator){
		int ret = 0;
		for(int i = 0; i < list.size();i++){
			if(list.get(i).trim().equals(comperator.trim())){
				ret++;
			}
		}
		return ret;
	}
	
	public static boolean[] isVorrangigAndExtra(String kuerzel,String xreznr){
		boolean[] bret = {false,false};
		Vector<Vector<String>> vec = SqlInfo.holeFelder("select vorrangig,extraok from kuerzel where kuerzel='"+kuerzel+
				"' and disziplin ='"+xreznr.substring(0,2)+"' LIMIT 1");
		if(vec.size() <= 0){
			String msg = "Achtung!\n\n"+
			"Ihre Kürzelzuordnung in den Preislisten ist nicht korrekt!!!!!\n"+
			"Kürzel: "+kuerzel+"\n"+
			"Disziplin: "+xreznr.substring(0,2)+"\n\n"+
			"Für die ausgewählte Diziplin ist das angegebene Kürzel nicht in der Kürzeltabelle vermerkt!";
			JOptionPane.showMessageDialog(null, msg);
			return null;
		}
		bret[0] = vec.get(0).get(0).equals("T");
		bret[1] = vec.get(0).get(1).equals("T");
		return bret;
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
		//Parameter preisgruppe wird nicht ausgewertet
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
		boolean u18Test = false;
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
			if(DatFunk.Unter18(vAktTermine.get(0), 
					DatFunk.sDatInDeutsch(Reha.thisClass.patpanel.patDaten.get(4)))){
				//System.out.println(vAktTermine);
				u18Test = true;
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
			if( ((Boolean) ((String)Reha.thisClass.patpanel.vecaktrez.get(60)).equals("T")) || (u18Test)){
				// Es ist ein unter 18 Jahre Test notwendig
				//System.out.println("Es ist ein unter 18 Jahre Test notwendig");
				if(bTermine){

					int [] test = ZuzahlTools.terminNachAchtzehn(vAktTermine,DatFunk.sDatInDeutsch((String)Reha.thisClass.patpanel.patDaten.get(4))); 
					if( test[0] > 0 ){
						//muß zuzahlen
						//System.out.println("Parameter 1 = "+test[0]);
						//System.out.println("Parameter 2 = "+test[1]);

						zm.allefrei = false;
						if(test[1] > 0){
							zm.allefrei = false;
							zm.allezuzahl = false;
							zm.anfangfrei = true;
							zm.teil1 = test[1];
							zm.teil2 = maxAnzahl()-test[1];
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
		//System.out.println("Rückgabewert iret = "+iret);
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
	

	public static void constructRawHMap(){
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				try{
					DecimalFormat df = new DecimalFormat( "0.00" );
					String diszi = RezTools.putRezNrGetDisziplin((String)Reha.thisClass.patpanel.vecaktrez.get(1));

					int pg = Integer.parseInt((String)Reha.thisClass.patpanel.vecaktrez.get(41))-1;
					String id = "";
					SystemConfig.hmAdrRDaten.put("<Rid>",(String)Reha.thisClass.patpanel.vecaktrez.get(35) );
					SystemConfig.hmAdrRDaten.put("<Rnummer>",(String)Reha.thisClass.patpanel.vecaktrez.get(1) );
					SystemConfig.hmAdrRDaten.put("<Rdatum>",DatFunk.sDatInDeutsch((String)Reha.thisClass.patpanel.vecaktrez.get(2)) );

					for(int i = 0;i<4;i++){
						id = (String)Reha.thisClass.patpanel.vecaktrez.get(8+i);
						SystemConfig.hmAdrRDaten.put("<Rposition"+(i+1)+">",(String)Reha.thisClass.patpanel.vecaktrez.get(48+i));
						SystemConfig.hmAdrRDaten.put("<Rpreis"+(i+1)+">", (String)Reha.thisClass.patpanel.vecaktrez.get(18+i).replace(".",",") );
						SystemConfig.hmAdrRDaten.put("<Ranzahl"+(i+1)+">", (String)Reha.thisClass.patpanel.vecaktrez.get(3+i) );
						SystemConfig.hmAdrRDaten.put("<Rgesamt"+(i+1)+">", df.format( ((BigDecimal)BigDecimal.valueOf(Double.valueOf(SystemConfig.hmAdrRDaten.get("<Ranzahl"+(i+1)+">"))).multiply(BigDecimal.valueOf(Double.valueOf(SystemConfig.hmAdrRDaten.get("<Rpreis"+(i+1)+">").replace(",","."))))).doubleValue() ));
						if(!id.equals("0")){
							SystemConfig.hmAdrRDaten.put("<Rkuerzel"+(i+1)+">", RezTools.getKurzformFromID(id, SystemPreislisten.hmPreise.get(diszi).get(pg) ) );
							SystemConfig.hmAdrRDaten.put("<Rlangtext"+(i+1)+">", RezTools.getLangtextFromID(id, "", SystemPreislisten.hmPreise.get(diszi).get(pg) ) );
						}else{
							SystemConfig.hmAdrRDaten.put("<Rkuerzel"+(i+1)+">", "");
							SystemConfig.hmAdrRDaten.put("<Rlangtext"+(i+1)+">", "");
						}
					}
					//Hausbesuche
					if( ((String)Reha.thisClass.patpanel.vecaktrez.get(43)).equals("T") ){
						SystemConfig.hmAdrRDaten.put("<Rhbpos>", SystemPreislisten.hmHBRegeln.get(diszi).get(pg).get(0));
						SystemConfig.hmAdrRDaten.put("<Rhbanzahl>",(String)Reha.thisClass.patpanel.vecaktrez.get(64) );
						SystemConfig.hmAdrRDaten.put("<Rhbpreis>",RezTools.getPreisAktFromPos(SystemConfig.hmAdrRDaten.get("<Rhbpos>"), "", SystemPreislisten.hmPreise.get(diszi).get(pg)).replace(".",",") );
						SystemConfig.hmAdrRDaten.put("<Rwegpos>", SystemPreislisten.hmHBRegeln.get(diszi).get(pg).get(2));
						SystemConfig.hmAdrRDaten.put("<Rweganzahl>",(String)Reha.thisClass.patpanel.vecaktrez.get(7) );
						SystemConfig.hmAdrRDaten.put("<Rwegpreis>",RezTools.getPreisAktFromPos(SystemConfig.hmAdrRDaten.get("<Rwegpos>"), "", SystemPreislisten.hmPreise.get(diszi).get(pg)).replace(".",",") );						
					}else{
						SystemConfig.hmAdrRDaten.put("<Rhbpos>", "");
						SystemConfig.hmAdrRDaten.put("<Rhbanzahl>","");
						SystemConfig.hmAdrRDaten.put("<Rhbpreis>","");
						SystemConfig.hmAdrRDaten.put("<Rwegpos>", "");
						SystemConfig.hmAdrRDaten.put("<Rweganzahl>","");
						SystemConfig.hmAdrRDaten.put("<Rwegpreis>","");						
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
				return null;
			}
		}.execute();
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
		try{
			//System.out.println("*****In Anfang-frei*********");
			if(anfang){
				zm.gesamtZahl = Integer.valueOf(zm.teil2);
				//System.out.println("Restliche Behandlungen berechnen = "+zm.gesamtZahl);
			}else{
				zm.gesamtZahl = Integer.valueOf(zm.teil1);
				//System.out.println("Beginn der Behandlung berechnen = "+zm.gesamtZahl);
			}
		}catch(Exception ex){
			ex.printStackTrace();
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
	public static Vector<String> macheUmsatzZeile(Vector<Vector<String>> vec,String tag,String kaluser){
		Vector<String> retvec = new Vector<String>();
		for(int i = 0; i < 13;i++){
			retvec.add("");
		}
			
		String disziplin = putRezNrGetDisziplin(vec.get(0).get(1));
		String pos = "";
		String preis = "";
		String pospauschale = "";
		@SuppressWarnings("unused")
		String preispauschale = "";
		int preisgruppe = Integer.parseInt(vec.get(0).get(41));
		String termine = vec.get(0).get(34);
		boolean rezept = false;
		Double wgkm;
		int fehlerstufe = 0;
		int ipos = 0;
		String kform = "";
		String[] posbestaetigt = null;;
		Object[][] preisobj = {{null,null,null,null},{null,null,null,null}};
		/************************/
		//1. Termine aus Rezept holen
		String bestaetigte = vec.get(0).get(34);
		//2. Testen ob der Tag erfaßt wenn nicht weiter mit der vollen Packung + Fehlerstufe 1
		if(!termine.contains(tag)){
			fehlerstufe = 1;
		}else{
			//3. Sofern der Tagin der Termintabelle vorhanden ist,
			//   die Positionen ermitteln
			//    wenn keine Positionen vorhanden, weiter mit voller Packung + Fehlerstufe 2
			posbestaetigt = bestaetigte.substring(bestaetigte.indexOf(tag)).split("@")[3].split(",");
			if(posbestaetigt[0].trim().equals("")){
				fehlerstufe = 2;
			}

		}
		//4. Überprüfen ob die Positionen in der Tarifgruppe existieren,
		//   sofern nicht, Preise und Positionen aus Rezept entnehmen, also volle Packung + Fehlerstufe 3 
		if(fehlerstufe==0){
			for(int j = 0; j < posbestaetigt.length;j++){
				if(! posbestaetigt[j].trim().equals("")){
					 if((kform=RezTools.getKurzformFromPos(posbestaetigt[j].trim(), Integer.toString(preisgruppe), SystemPreislisten.hmPreise.get(disziplin).get( (preisgruppe==0 ? 0 : preisgruppe-1) ))).equals("")){
						fehlerstufe = 3;
						break;
					 }
					 preisobj[0][j] = kform;
					 preisobj[1][j] = RezTools.getPreisAktFromPos(posbestaetigt[j], Integer.toString(preisgruppe), SystemPreislisten.hmPreise.get(disziplin).get( (preisgruppe==0 ? 0 : preisgruppe-1) ));
				}
			}
		}
 
		if(fehlerstufe==0){
			//5. Wenn hier angekommen die Preise und Positionen aus der Preisliste entnehmen
			for(int j = 0; j < 4;j++){
				retvec.set(j,String.valueOf((String) (preisobj[0][j] != null ? preisobj[0][j] : "-----")));
				retvec.set(j+6,String.valueOf((String) (preisobj[1][j] != null ? preisobj[1][j] : "0.00")));
			}
			retvec.set(12,"0");
		}else{
			for(int i = 0;i<4;i++){
				if(!vec.get(0).get(i+8).trim().equals("0")){
					pos = RezTools.getKurzformFromID(vec.get(0).get(i+8).trim(),SystemPreislisten.hmPreise.get(disziplin).get( (preisgruppe==0 ? 0 : preisgruppe-1) ));
					if(pos.trim().equals("")){
						pos = RezTools.getKurzformFromPos(vec.get(0).get(i+48).trim(), Integer.toString(preisgruppe), SystemPreislisten.hmPreise.get(disziplin).get( (preisgruppe==0 ? 0 : preisgruppe-1) ));					
					}
					retvec.set(i, pos);
					retvec.set(i+6,vec.get(0).get(i+18).trim());
				}else{
					retvec.set(i, "-----");
					retvec.set(i+6,"0.00");
				}
			}
			retvec.set(12,Integer.toString(fehlerstufe));
		}
		/************************/
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
	public static void loescheRezAusVolleTabelle(final String reznr){
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				SqlInfo.sqlAusfuehren("delete from volle where rez_nr='"+reznr+"'");
				return null;
			}
			
		}.execute();
		
	}
	/***************************************************/
	
	public static void fuelleVolleTabelle(final String reznr,final String rezbehandler){
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				try{
					if(SqlInfo.gibtsSchon("select rez_nr from volle where rez_nr ='"+reznr+"' LIMIT 1")){
						return null;
					}
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

	/***********************************************************************************/

	public static Object[] BehandlungenAnalysieren(String swreznum,
			boolean doppeltOk,boolean xforceDlg,boolean alletermine,
			Vector<String> vecx,Point pt,String xkollege,String datum){
		int i,j,count =0;
		boolean doppelBeh = false;
		int doppelBehA = 0, doppelBehB = 0;
		boolean dlgZeigen = false; // unterdrückt die Anzeige des TeminBestätigenAuswahlFensters
		boolean jetztVoll = false;
		boolean anzahlRezeptGleich = true;
		boolean nochOffenGleich = true;
		Vector<BestaetigungsDaten> hMPos= new Vector<BestaetigungsDaten>();
		hMPos.add(new BestaetigungsDaten(false, "./.", 0, 0,false,false));
		hMPos.add(new BestaetigungsDaten(false, "./.", 0, 0,false,false));
		hMPos.add(new BestaetigungsDaten(false, "./.", 0, 0,false,false));
		hMPos.add(new BestaetigungsDaten(false, "./.", 0, 0,false,false));
		Vector<String> vec = null;
		String copyright = "\u00AE"  ;
		StringBuffer termbuf = new StringBuffer();
		int preisgruppe = -1;
		int iposindex = -1;
		boolean erstedoppel = true;
		boolean debug = false;
		String disziplin = "";
		boolean hmrtest = false;
		
		Object[] retObj = {null,null,null};

		try{
			// die anzahlen 1-4 werden jetzt zusammenhängend ab index 11 abgerufen
			if(vecx==null){
				vec = SqlInfo.holeSatz("verordn", "termine,pos1,pos2,pos3,pos4,hausbes,unter18,jahrfrei,pat_intern,preisgruppe,zzregel,anzahl1,anzahl2,anzahl3,anzahl4,preisgruppe", "rez_nr='"+swreznum+"'", Arrays.asList(new String[] {}));	
			}else{
				vec = vecx;
			}
			preisgruppe = Integer.parseInt(vec.get(9));
			disziplin = RezTools.putRezNrGetDisziplin(swreznum);
			hmrtest = (SystemPreislisten.hmHMRAbrechnung.get(disziplin).get(preisgruppe-1) == 1);
			if (vec.size() > 0){
				termbuf = new StringBuffer();
				if(alletermine){
					termbuf.append((String) vec.get(0));	
				}
				
				Vector<ArrayList<?>> termine = RezTools.holePosUndAnzahlAusTerminen(swreznum);
				//System.out.println(termine);
				if(termine.size()==0){return null;}
				//Arrays innerhalb dem Vector termine
				//termine.get(0) = Positionen (String)
				//termine.get(1) = Anzahlen   (Integer)
				//termine.get(2) = Vorrangiges Heilmittel (Boolean)
				//termine.get(3) = als Standalone ergänzendes Heilmittel erlaubt (Boolean)	
				//termine.get(4) = Object[] {doppelbehandlung(Boolean),erstepos(Integer),letztepos(Integer)}	

				for (i=0;i<=3;i++){
					if(vec.get(1+i).toString().trim().equals("") ){
						hMPos.get(i).hMPosNr = "./.";
						hMPos.get(i).vOMenge = 0;
						hMPos.get(i).anzBBT = 0;
					}else{
						hMPos.get(i).hMPosNr = String.valueOf(vec.get(1+i));
						hMPos.get(i).vOMenge = Integer.parseInt( (String) vec.get(i+11) );
						hMPos.get(i).vorrangig = (Boolean)((ArrayList<?>)((Vector<?>)termine).get(2)).get(i);
						hMPos.get(i).invOBelegt = true;
						hMPos.get(i).anzBBT = Integer.valueOf( (Integer)((ArrayList<?>)((Vector<?>)termine).get(1)).get(i));
					}
					hMPos.get(i).gehtNochEiner();
				}
				//Jetzt alle Objekte die unbelegt sind löschen
				for(i=3;i>=0;i--){
					if(!hMPos.get(i).invOBelegt){hMPos.remove(i);}
				}
				hMPos.trimToSize();
				//Die Variable j erhält jetzt den Wert der Anzahl der verbliebenen Objekte
				j= hMPos.size();

				//Nur wenn nach HMR-geprüft werden muß
				if(hmrtest){
				//1. erst Prüfen ob das Rezept bereits voll ist
					for(i=0;i<j;i++){
						if(!hMPos.get(i).einerOk && hMPos.get(i).vorrangig){
							//ein vorrangiges Heilmittel ist voll
							//testen ob es sich um eine Doppelposition dreht
							if(debug){
								Object[] testobj = (Object[])((ArrayList<?>)((Vector<?>)termine).get(4)).get(i);
								//System.out.println(testobj[0]+"-"+testobj[1]+"-"+testobj[2]);
							}
							if(((Object[])((ArrayList<?>)((Vector<?>)termine).get(4)).get(i))[0]==Boolean.valueOf(true)){
								//testen ob es die 1-te Pos der Doppelbehandlung ist
								if(((Integer)((Object[])((ArrayList<?>)((Vector<?>)termine).get(4)).get(i))[1]) <
										((Integer)((Object[])((ArrayList<?>)((Vector<?>)termine).get(4)).get(i))[2]) ){
									//Es ist die 1-te Position die voll ist also Ende-Gelände
									retObj[0] = String.valueOf(termbuf.toString());
									retObj[1] = Integer.valueOf(RezTools.REZEPT_IST_BEREITS_VOLL);
									//if(debug){System.out.println("erste Position = voll + Doppelbehandlung");}
									//if(debug){System.out.println(hMPos.get(i).hMPosNr+"-"+hMPos.get(i).vOMenge+"-"+hMPos.get(i).anzBBT);}
									return retObj;
								}
							}else{
								//nein keine Doppelposition also Ende-Gelände
								retObj[0] = String.valueOf(termbuf.toString());
								retObj[1] = Integer.valueOf(RezTools.REZEPT_IST_BEREITS_VOLL);
								//if(debug){System.out.println("erste Position = voll und keine Doppelbehandlung");}
								//if(debug){System.out.println(hMPos.get(i).hMPosNr+"-"+hMPos.get(i).vOMenge+"-"+hMPos.get(i).anzBBT);}
								return retObj;
							}
						}else if(!hMPos.get(i).einerOk && (!hMPos.get(i).vorrangig) && j==1){
							//Falls eines der wenigen ergänzenden Heilmittel solo verordnet wurde
							//z.B. Ultraschall oder Elektrotherapie
							retObj[0] = String.valueOf(termbuf.toString());
							retObj[1] = Integer.valueOf(RezTools.REZEPT_IST_BEREITS_VOLL);
							//if(debug){System.out.println("es geht kein zusätzlicher");}
							//if(debug){System.out.println(hMPos.get(i).hMPosNr+"-"+hMPos.get(i).vOMenge+"-"+hMPos.get(i).anzBBT);}
							return retObj;
						}else if( (!hMPos.get(i).vorrangig) && (j==1) && 
								(Boolean)((ArrayList<?>)((Vector<?>)termine).get(2)).get(i)){
							//Ein ergänzendes Heilmittel wurde separat verordent das nicht zulässig ist
							//könnte man auswerten, dann verbaut man sich aber die Möglichkeit
							//bei PrivatPat. abzurechnen was geht....
							//if(debug){System.out.println("unerlaubtes Ergänzendes Heilmittel solo verordnet");}
							//if(debug){System.out.println(hMPos.get(i).hMPosNr+"-"+hMPos.get(i).vOMenge+"-"+hMPos.get(i).anzBBT);}
						}
						//if(debug){System.out.println("Position kann bestätigt werden");}
						//if(debug){System.out.println(hMPos.get(i).hMPosNr+"-"+hMPos.get(i).vOMenge+"-"+hMPos.get(i).anzBBT);}
					}
					//Ende nur wenn Tarifgruppe HMR-Gruppe ist
				}
				
				//2. dann prüfen welche Behandlungsformen noch einen vertragen können
				count = 0;
				int ianzahl = hMPos.get(0).vOMenge;
				int ioffen = hMPos.get(0).vORestMenge;
				for(i=0;i<j;i++){
					hMPos.get(i).danachVoll();
					//wenn eine Behandlung noch frei ist //(ausgeschaltet)und die Position keine Doppelposition ist
					if(hMPos.get(i).einerOk /*&& ((Object[])((ArrayList<?>)((Vector<?>)termine).get(4)).get(i))[0]==Boolean.valueOf(false)*/){
						count++;
					}
					if(ianzahl != hMPos.get(i).vOMenge){anzahlRezeptGleich=false;}
					if(ioffen != hMPos.get(i).vORestMenge){nochOffenGleich=false;}
				}
				//Keine Postition mehr frei
				if(count==0){
					retObj[0] = String.valueOf(termbuf.toString());
					retObj[1] = Integer.valueOf(RezTools.REZEPT_IST_BEREITS_VOLL);
					//if(debug){System.out.println("Rezept war bereits voll");}
					return retObj;
				}
				//Nur Wenn mehrere Behandlungen im Rezept vermerkt
				if(j > 1){
					//Wenn mehrere noch offen sind aber ungleiche noch Offen
					if( (count > 1) && (!(/*anzahlRezeptGleich &&*/ nochOffenGleich)) ){
						dlgZeigen = true;						
					}
				}
				//3. Dann Dialog zeigen
				// TerminBestätigenAuswahlFenster anzeigen oder überspringen
				// Evtl. noch Einbauen ob bei unterschiedlichen Anzahlen (System-Initialisierung) immer geöffnet wird.
				if (xforceDlg || (dlgZeigen && (Boolean)SystemConfig.hmTerminBestaetigen.get("dlgzeigen") ) ){
							
							TerminBestaetigenAuswahlFenster termBestAusw = new TerminBestaetigenAuswahlFenster(Reha.thisFrame,null,(Vector<BestaetigungsDaten>)hMPos,swreznum,Integer.parseInt((String)vec.get(15)));
							termBestAusw.pack();
							if(pt==null){
								termBestAusw.setLocationRelativeTo(null);
							}else{
								termBestAusw.setLocation(pt);
							}
							//
							termBestAusw.setzeFocus();
							termBestAusw.setModal(true);
							termBestAusw.setVisible(true);
							if(RezTools.DIALOG_WERT==RezTools.DIALOG_ABBRUCH){
								retObj[0] = String.valueOf(termbuf.toString());
								retObj[1] = Integer.valueOf(RezTools.REZEPT_ABBRUCH);
								return retObj;
							}
							for (i=0; i<j; i++){
								if(hMPos.get(i).best){
									hMPos.get(i).anzBBT += 1;
									//gleichzeitig prüfen ob voll
									if(hMPos.get(i).jetztVoll() && hMPos.get(i).vorrangig){
										jetztVoll = true;
									}else if(hMPos.get(i).jetztVoll() && (!hMPos.get(i).vorrangig) && j==1){
										jetztVoll = true;
									}
								}
							}
							
				}else{
					/*
					 * Der Nutzer wünscht kein Auswahlfenster:
					 * bestätige alle noch offenen Heilmittel
					 *   
					 */		
					for (i=0; i<j; i++){
						hMPos.get(i).best = Boolean.valueOf(hMPos.get(i).einerOk);
						if(hMPos.get(i).einerOk){
							hMPos.get(i).anzBBT += 1;
							hMPos.get(i).best = true;
							if(hMPos.get(i).jetztVoll() && hMPos.get(i).vorrangig){
								jetztVoll = true;	
							}else if(hMPos.get(i).jetztVoll() && (!hMPos.get(i).vorrangig) && j==1){
								jetztVoll = true;
							}
						}
					}
				}
				String[] params = {null,null,null,null};
				count = 0;
				for(i=0;i<j;i++){
					if(hMPos.get(i).best){params[i]=vec.get(i+1);count++;}
				}
				if(count==0){jetztVoll=true;}
				termbuf.append(TermineErfassen.macheNeuTermin2(
						(params[0] != null ? params[0] : ""),
						(params[1] != null ? params[1] : ""),
						(params[2] != null ? params[2] : ""),
						(params[3] != null ? params[3] : ""),
						xkollege,datum));

				retObj[0] = String.valueOf(termbuf.toString());
				retObj[1] = (jetztVoll ? Integer.valueOf(RezTools.REZEPT_IST_JETZ_VOLL) : Integer.valueOf(RezTools.REZEPT_HAT_LUFT ));
									
				//if(debug){System.out.println("am ende angekommen");}
				return retObj;
			}else{
				//System.out.println("*****************IN ELSE***************************");
			}
			return retObj;
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			//vec = null;
			hMPos = null;
		}
		return retObj;
	}
	private static int welcheIstMaxInt(int i1,int i2){
		if(i1 > i2){return 1;}
		if(i1==i2){return 0;}
		return 2;
	}

/************************************/

	
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
