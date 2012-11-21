package systemEinstellungen;

import hauptFenster.Reha;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import CommonTools.INIFile;
import CommonTools.INITool;

import CommonTools.SqlInfo;

public class SystemPreislisten {
	public static Vector<Vector<Vector<String>>> vKGPreise = new Vector<Vector<Vector<String>>>();
	public static Vector<Vector<Vector<String>>> vMAPreise = new Vector<Vector<Vector<String>>>();
	public static Vector<Vector<Vector<String>>> vERPreise = new Vector<Vector<Vector<String>>>();
	public static Vector<Vector<Vector<String>>> vLOPreise = new Vector<Vector<Vector<String>>>();
	public static Vector<Vector<Vector<String>>> vRHPreise = new Vector<Vector<Vector<String>>>();
	public static Vector<Vector<Vector<String>>> vPOPreise = new Vector<Vector<Vector<String>>>();
	
	public static HashMap<String,Vector<Vector<Vector<String>>>> hmPreise = new HashMap<String,Vector<Vector<Vector<String>>>>();
	
	public static HashMap<String,Vector<String>> hmPreisGruppen = new HashMap<String,Vector<String>>();

	public static HashMap<String,Vector<String>> hmPreisBereich = new HashMap<String,Vector<String>>();

	public static HashMap<String,Vector<Integer>> hmZuzahlRegeln = new HashMap<String,Vector<Integer>>();	
	public static HashMap<String,Vector<Integer>> hmZuzahlModus = new HashMap<String,Vector<Integer>>();

	public static HashMap<String,Vector<Integer>> hmHMRAbrechnung = new HashMap<String,Vector<Integer>>();

	public static HashMap<String,Vector<String>> hmNeuePreiseAb = new HashMap<String,Vector<String>>();

	public static HashMap<String,Vector<Integer>> hmNeuePreiseRegel = new HashMap<String,Vector<Integer>>();
	

	public static HashMap<String,Vector<Vector<String>>> hmHBRegeln = new HashMap<String,Vector<Vector<String>>>();
	
	public static HashMap<String,Vector<String>> hmBerichtRegeln = new HashMap<String,Vector<String>>();
	
	private static Vector<String> dummy = new Vector<String>();
	private static Vector<Integer> intdummy = new Vector<Integer>();
	private static Vector<Vector<String>> hbdummy = new Vector<Vector<String>>();
	private static Vector<String> hbdummy_1 = new Vector<String>();
	private static Vector<Vector<String>> preisliste = new Vector<Vector<String>>();
	private static Vector<Integer> modusdummy = new Vector<Integer>();
	public static HashMap<String,Vector<Object>> hmFristen = new HashMap<String,Vector<Object>>();
	private static Vector<Object> odummy = new Vector<Object>(); 

	static INIFile fristenini = null;
	static INIFile inif = null;
	@SuppressWarnings("unchecked")
	public static void ladePreise(String disziplin){
		String[] diszis = {"Physio","Massage","Ergo","Logo","Reha","Common","Podo"};
		
		List<String> list = Arrays.asList(diszis);
		int treffer = list.indexOf(disziplin);
		
		
		if(treffer < 0){
			return;
		}

		inif = INITool.openIni(Reha.proghome+"ini/"+Reha.aktIK+"/", "preisgruppen.ini");
		int tarife = inif.getIntegerProperty("PreisGruppen_"+diszis[treffer], "AnzahlPreisGruppen");
		fristenini = INITool.openIni(Reha.proghome+"ini/"+Reha.aktIK+"/", "fristen.ini");
		
		

	 	Comparator<Vector> comparator = new Comparator<Vector>() {
			@Override
			public int compare(Vector o1, Vector o2) {
				
				String s1 = (String)((Vector)o1).get(0).toString();
				String s2 = (String)((Vector)o2).get(0).toString();
				return s1.compareTo(s2);
			}
		};

		if(treffer==0){
			vKGPreise.clear();
			for(int i = 0; i < tarife;i++){
				preisliste.clear();
				preisliste = (Vector<Vector<String>>)SqlInfo.holeFelder("select * from kgtarif"+Integer.toString(i+1));
				Collections.sort((Vector)preisliste,comparator);
				vKGPreise.add((Vector<Vector<String>> )preisliste.clone());
			}
			preisliste.clear();
			hmPreise.put("Physio", vKGPreise);
			dummy.clear();
			getPreisGruppen(inif,"Physio",tarife);
			hmPreisGruppen.put("Physio", (Vector<String>)dummy.clone() );
			dummy.clear();
			getPreisBereich(inif,"Physio",tarife);
			hmPreisBereich.put("Physio", (Vector<String>)dummy.clone() );
			dummy.clear();
			getNeuePreiseAb(inif,"Physio",tarife);
			hmNeuePreiseAb.put("Physio", (Vector<String>)dummy.clone() );
			dummy.clear();				
			intdummy.clear();
			getNeuePreiseRegeln(inif,"Physio",tarife);
			hmNeuePreiseRegel.put("Physio", (Vector<Integer>)intdummy.clone());
			intdummy.clear();
			getZuzahlRegeln(inif,"Physio",tarife);
			hmZuzahlRegeln.put("Physio", (Vector<Integer>)intdummy.clone());
			hmZuzahlModus.put("Physio", (Vector<Integer>)modusdummy.clone());
			intdummy.clear();
			modusdummy.clear();
			getHMRAbrechnung(inif,"Physio",tarife);
			hmHMRAbrechnung.put("Physio", (Vector<Integer>)intdummy.clone());
			intdummy.clear();
			hbdummy.clear();
			getHBRegeln(inif,"Physio",tarife);
			hmHBRegeln.put("Physio", (Vector<Vector<String>>)hbdummy.clone());
			hbdummy.clear();
			dummy.clear();
			getBerichtRegeln(inif,"Physio",tarife);
			hmBerichtRegeln.put("Physio", (Vector<String>)dummy.clone() );
			dummy.clear();
			doFristen("Physio",tarife);
			return;
		}
		
		/***************************************************/
		if(treffer==1){
			vMAPreise.clear();
			for(int i = 0; i < tarife;i++){
				preisliste.clear();
				preisliste = (Vector<Vector<String>>)SqlInfo.holeFelder("select * from matarif"+Integer.toString(i+1));
				Collections.sort((Vector)preisliste,comparator);
				vMAPreise.add((Vector<Vector<String>> )preisliste.clone());
			}
			preisliste.clear();
			hmPreise.put("Massage", vMAPreise);
			dummy.clear();
			getPreisGruppen(inif,"Massage",tarife);
			hmPreisGruppen.put("Massage", (Vector<String>)dummy.clone() );
			dummy.clear();
			getPreisBereich(inif,"Massage",tarife);
			hmPreisBereich.put("Massage", (Vector<String>)dummy.clone() );
			dummy.clear();
			intdummy.clear();
			getNeuePreiseRegeln(inif,"Massage",tarife);
			hmNeuePreiseRegel.put("Massage", (Vector<Integer>)intdummy.clone());
			intdummy.clear();
			getNeuePreiseAb(inif,"Massage",tarife);
			hmNeuePreiseAb.put("Massage", (Vector<String>)dummy.clone() );
			dummy.clear();				
			intdummy.clear();
			getZuzahlRegeln(inif,"Massage",tarife);
			hmZuzahlRegeln.put("Massage", (Vector<Integer>)intdummy.clone());
			hmZuzahlModus.put("Massage", (Vector<Integer>)modusdummy.clone());
			intdummy.clear();
			modusdummy.clear();
			getHMRAbrechnung(inif,"Massage",tarife);
			hmHMRAbrechnung.put("Massage", (Vector<Integer>)intdummy.clone());
			intdummy.clear();
			hbdummy.clear();
			getHBRegeln(inif,"Massage",tarife);
			hmHBRegeln.put("Massage", (Vector<Vector<String>>)hbdummy.clone());
			hbdummy.clear();
			dummy.clear();
			getBerichtRegeln(inif,"Massage",tarife);
			hmBerichtRegeln.put("Massage", (Vector<String>)dummy.clone() );
			dummy.clear();
			doFristen("Massage",tarife);
			return;
		}
		/***************************************************/		
		if(treffer==2){
			vERPreise.clear();
			for(int i = 0; i < tarife;i++){
				preisliste.clear();
				preisliste = (Vector<Vector<String>>)SqlInfo.holeFelder("select * from ertarif"+Integer.toString(i+1));
				Collections.sort((Vector)preisliste,comparator);
				vERPreise.add((Vector<Vector<String>> )preisliste.clone());
			}
			preisliste.clear();
			hmPreise.put("Ergo", vERPreise);
			dummy.clear();
			getPreisGruppen(inif,"Ergo",tarife);
			hmPreisGruppen.put("Ergo", (Vector<String>)dummy.clone() );
			dummy.clear();
			getPreisBereich(inif,"Ergo",tarife);
			hmPreisBereich.put("Ergo", (Vector<String>)dummy.clone() );
			dummy.clear();
			intdummy.clear();
			getNeuePreiseRegeln(inif,"Ergo",tarife);
			hmNeuePreiseRegel.put("Ergo", (Vector<Integer>)intdummy.clone());
			intdummy.clear();
			getNeuePreiseAb(inif,"Ergo",tarife);
			hmNeuePreiseAb.put("Ergo", (Vector<String>)dummy.clone() );
			dummy.clear();				
			intdummy.clear();
			getZuzahlRegeln(inif,"Ergo",tarife);
			hmZuzahlRegeln.put("Ergo", (Vector<Integer>)intdummy.clone());
			hmZuzahlModus.put("Ergo", (Vector<Integer>)modusdummy.clone());
			intdummy.clear();
			modusdummy.clear();
			getHMRAbrechnung(inif,"Ergo",tarife);
			hmHMRAbrechnung.put("Ergo", (Vector<Integer>)intdummy.clone());
			intdummy.clear();
			hbdummy.clear();
			getHBRegeln(inif,"Ergo",tarife);
			hmHBRegeln.put("Ergo", (Vector<Vector<String>>)hbdummy.clone());
			hbdummy.clear();
			dummy.clear();
			getBerichtRegeln(inif,"Ergo",tarife);
			hmBerichtRegeln.put("Ergo", (Vector<String>)dummy.clone() );
			dummy.clear();
			doFristen("Ergo",tarife);
			return;
		}
		/***************************************************/		
		if(treffer==3){
			vLOPreise.clear();
			for(int i = 0; i < tarife;i++){
				preisliste.clear();
				preisliste = (Vector<Vector<String>>)SqlInfo.holeFelder("select * from lotarif"+Integer.toString(i+1));
				Collections.sort((Vector)preisliste,comparator);
				vLOPreise.add((Vector<Vector<String>> )preisliste.clone());
			}
			preisliste.clear();
			hmPreise.put("Logo", vLOPreise);
			dummy.clear();
			getPreisGruppen(inif,"Logo",tarife);
			hmPreisGruppen.put("Logo", (Vector<String>)dummy.clone() );
			dummy.clear();
			getPreisBereich(inif,"Logo",tarife);
			hmPreisBereich.put("Logo", (Vector<String>)dummy.clone() );
			dummy.clear();
			intdummy.clear();
			getNeuePreiseRegeln(inif,"Logo",tarife);
			hmNeuePreiseRegel.put("Logo", (Vector<Integer>)intdummy.clone());
			intdummy.clear();
			getNeuePreiseAb(inif,"Logo",tarife);
			hmNeuePreiseAb.put("Logo", (Vector<String>)dummy.clone() );
			dummy.clear();				
			intdummy.clear();
			getZuzahlRegeln(inif,"Logo",tarife);
			hmZuzahlRegeln.put("Logo", (Vector<Integer>)intdummy.clone());
			hmZuzahlModus.put("Logo", (Vector<Integer>)modusdummy.clone());
			intdummy.clear();
			modusdummy.clear();
			getHMRAbrechnung(inif,"Logo",tarife);
			hmHMRAbrechnung.put("Logo", (Vector<Integer>)intdummy.clone());
			intdummy.clear();
			hbdummy.clear();
			getHBRegeln(inif,"Logo",tarife);
			hmHBRegeln.put("Logo", (Vector<Vector<String>>)hbdummy.clone());
			hbdummy.clear();
			dummy.clear();
			getBerichtRegeln(inif,"Logo",tarife);
			hmBerichtRegeln.put("Logo", (Vector<String>)dummy.clone() );
			dummy.clear();
			doFristen("Logo",tarife);
			return;
		}
		/***************************************************/		
		if(treffer==4){
			vRHPreise.clear();
			for(int i = 0; i < tarife;i++){
				preisliste.clear();
				preisliste = (Vector<Vector<String>>)SqlInfo.holeFelder("select * from rhtarif"+Integer.toString(i+1));
				Collections.sort((Vector)preisliste,comparator);
				vRHPreise.add((Vector<Vector<String>> )preisliste.clone());
			}
			preisliste.clear();
			hmPreise.put("Reha", vRHPreise);
			dummy.clear();
			getPreisGruppen(inif,"Reha",tarife);
			hmPreisGruppen.put("Reha", (Vector<String>)dummy.clone() );
			dummy.clear();
			getPreisBereich(inif,"Reha",tarife);
			hmPreisBereich.put("Reha", (Vector<String>)dummy.clone() );
			dummy.clear();
			intdummy.clear();
			getNeuePreiseRegeln(inif,"Reha",tarife);
			hmNeuePreiseRegel.put("Reha", (Vector<Integer>)intdummy.clone());
			intdummy.clear();
			getNeuePreiseAb(inif,"Reha",tarife);
			hmNeuePreiseAb.put("Reha", (Vector<String>)dummy.clone() );
			dummy.clear();				
			intdummy.clear();
			getZuzahlRegeln(inif,"Reha",tarife);
			hmZuzahlRegeln.put("Reha", (Vector<Integer>)intdummy.clone());
			hmZuzahlModus.put("Reha", (Vector<Integer>)modusdummy.clone());
			intdummy.clear();
			modusdummy.clear();
			getHMRAbrechnung(inif,"Reha",tarife);
			hmHMRAbrechnung.put("Reha", (Vector<Integer>)intdummy.clone());
			intdummy.clear();
			hbdummy.clear();
			getHBRegeln(inif,"Reha",tarife);
			hmHBRegeln.put("Reha", (Vector<Vector<String>>)hbdummy.clone());
			hbdummy.clear();
			dummy.clear();
			getBerichtRegeln(inif,"Reha",tarife);
			hmBerichtRegeln.put("Reha", (Vector<String>)dummy.clone() );
			dummy.clear();
			doFristen("Reha",tarife);
			return;
		}
		
		if(treffer==6){
			vPOPreise.clear();
			for(int i = 0; i < tarife;i++){
				preisliste.clear();
				preisliste = (Vector<Vector<String>>)SqlInfo.holeFelder("select * from potarif"+Integer.toString(i+1));
				Collections.sort((Vector)preisliste,comparator);
				vPOPreise.add((Vector<Vector<String>> )preisliste.clone());
			}
			preisliste.clear();
			hmPreise.put("Podo", vPOPreise);
			dummy.clear();
			getPreisGruppen(inif,"Podo",tarife);
			hmPreisGruppen.put("Podo", (Vector<String>)dummy.clone() );
			dummy.clear();
			getPreisBereich(inif,"Podo",tarife);
			hmPreisBereich.put("Podo", (Vector<String>)dummy.clone() );
			dummy.clear();
			intdummy.clear();
			getNeuePreiseRegeln(inif,"Podo",tarife);
			hmNeuePreiseRegel.put("Podo", (Vector<Integer>)intdummy.clone());
			intdummy.clear();
			getNeuePreiseAb(inif,"Podo",tarife);
			hmNeuePreiseAb.put("Podo", (Vector<String>)dummy.clone() );
			dummy.clear();				
			intdummy.clear();
			getZuzahlRegeln(inif,"Podo",tarife);
			hmZuzahlRegeln.put("Podo", (Vector<Integer>)intdummy.clone());
			hmZuzahlModus.put("Podo", (Vector<Integer>)modusdummy.clone());
			intdummy.clear();
			modusdummy.clear();
			getHMRAbrechnung(inif,"Podo",tarife);
			hmHMRAbrechnung.put("Podo", (Vector<Integer>)intdummy.clone());
			intdummy.clear();
			hbdummy.clear();
			getHBRegeln(inif,"Podo",tarife);
			hmHBRegeln.put("Podo", (Vector<Vector<String>>)hbdummy.clone());
			hbdummy.clear();
			dummy.clear();
			getBerichtRegeln(inif,"Podo",tarife);
			hmBerichtRegeln.put("Podo", (Vector<String>)dummy.clone() );
			dummy.clear();
			doFristen("Podo",tarife);
			return;
		}

		if(treffer==5){
			dummy.clear();
			getPreisGruppen(inif,"Common",tarife);
			hmPreisGruppen.put("Common", (Vector<String>)dummy.clone() );
			dummy.clear();	
		}
		inif = null;
		fristenini = null;

	}

	
	/**********
	 * 
	 * 
	 * @param f
	 * @param disziplin
	 * @param tarife
	 * @param dummy
	 * @return
	 */
	public static void getPreisGruppen(INIFile f,String disziplin,int tarife){
		for(int i = 0; i < tarife;i++){
			dummy.add(f.getStringProperty("PreisGruppen_"+disziplin, "PGName"+Integer.toString(i+1)));
		}
	}
	public static void getPreisBereich(INIFile f,String disziplin,int tarife){
		for(int i = 0; i < tarife;i++){
			dummy.add(f.getStringProperty("PreisGruppen_"+disziplin, "PGBereich"+Integer.toString(i+1)));
		}
	}
	public static void getZuzahlRegeln(INIFile f,String disziplin,int tarife){
		boolean mustsave = false;
		String sdummy;
		for(int i = 0; i < tarife;i++){
			intdummy.add(f.getIntegerProperty("ZuzahlRegeln_"+disziplin, "ZuzahlRegel"+Integer.toString(i+1)));
			if( (sdummy=f.getStringProperty("ZuzahlRegeln_"+disziplin, "ZuzahlModus"+Integer.toString(i+1)))==null){
				f.setStringProperty("ZuzahlRegeln_"+disziplin, "ZuzahlModus"+Integer.toString(i+1), "1", null);
				mustsave = true;
				sdummy = "1";
			}
			modusdummy.add(Integer.parseInt(sdummy));
		}
		if(mustsave){
			INITool.saveIni(f);
		}
	}
	public static void getHMRAbrechnung(INIFile f,String disziplin,int tarife){
		for(int i = 0; i < tarife;i++){
			intdummy.add(f.getIntegerProperty("HMRAbrechnung_"+disziplin, "HMRAbrechnung"+Integer.toString(i+1)));
		}
	}
	@SuppressWarnings("unchecked")
	public static void getHBRegeln(INIFile f,String disziplin,int tarife){
		for(int i = 0; i < tarife;i++){
			hbdummy_1.clear();
			hbdummy_1.add(f.getStringProperty("HBRegeln_"+disziplin,"HBPosVoll"+(i+1) ));
			hbdummy_1.add(f.getStringProperty("HBRegeln_"+disziplin,"HBPosMit"+(i+1) ));			
			hbdummy_1.add(f.getStringProperty("HBRegeln_"+disziplin,"HBKilometer"+(i+1) ));			
			hbdummy_1.add(f.getStringProperty("HBRegeln_"+disziplin,"HBPauschal"+(i+1) ));			
			hbdummy_1.add(f.getStringProperty("HBRegeln_"+disziplin,"HBHeimMitZuZahl"+(i+1) ));
			hbdummy.add((Vector<String>)hbdummy_1.clone());
		}
		hbdummy_1.clear();
	}
	public static void getNeuePreiseRegeln(INIFile f,String disziplin,int tarife){
		for(int i = 0; i < tarife;i++){
			intdummy.add(f.getIntegerProperty("PreisRegeln_"+disziplin, "PreisRegel"+Integer.toString(i+1)));
		}
	}
	public static void getNeuePreiseAb(INIFile f,String disziplin,int tarife){
		for(int i = 0; i < tarife;i++){
			dummy.add(f.getStringProperty("PreisRegeln_"+disziplin, "PreisAb"+Integer.toString(i+1)));
		}
	}
	public static void getBerichtRegeln(INIFile f,String disziplin,int tarife){
		for(int i = 0; i < tarife;i++){
			dummy.add(f.getStringProperty("BerichtRegeln_"+disziplin, "Bericht"+Integer.toString(i+1)));
		}
	}
	public static void loescheHashMaps(){
		hmPreise.clear();
		hmPreisGruppen.clear();
		hmPreisBereich.clear();
		hmZuzahlRegeln.clear();	
		hmHMRAbrechnung.clear();
		hmNeuePreiseAb.clear();
		hmNeuePreiseRegel.clear();
		hmHBRegeln.clear();
		hmBerichtRegeln.clear();
		hmFristen.clear();
		
		dummy.clear();
		intdummy.clear();
		hbdummy.clear();
		hbdummy_1.clear();
		preisliste.clear();
		hmFristen.clear();
		odummy.clear(); 
		
		hmPreisGruppen.clear();
		hmPreisBereich.clear();
		hmZuzahlRegeln.clear();	
		hmHMRAbrechnung.clear();
		hmNeuePreiseAb.clear();
		hmNeuePreiseRegel.clear();
		hmHBRegeln.clear();
		hmBerichtRegeln.clear();
		
		
		dummy.trimToSize();
		intdummy.trimToSize();
		hbdummy.trimToSize();
		hbdummy_1.trimToSize();
		preisliste.trimToSize();

	}

	class Sortiere{
		Vector<Vector<String>> vector = null;
		public Sortiere (Vector<Vector<String>> vec){
			this.vector = vec;
			}
		@SuppressWarnings("unchecked")
		public Vector<Vector<String>> sortieren(){
			Comparator<Vector> comparator = new Comparator<Vector>() {

				@Override
				public int compare(Vector o1, Vector o2) {
					String s1 = (String)((Vector)o1).get(0).toString();
					String s2 = (String)((Vector)o2).get(0).toString();
					return s1.compareTo(s2);
				}
			};

			Collections.sort((Vector)this.vector,comparator);
			return this.vector;
		}
	}
/*********************************/
	private static void doFristen(String disziplin,int anzahl){
		odummy.clear();
		Vector<Object> xdummy = new Vector<Object>(); 
		for(int i = 0; i < anzahl;i++){
			xdummy.add(fristenTesten("Fristen_"+disziplin, "FristBeginn"+Integer.toString(i+1),true));
			//xdummy.add(fristenini.getIntegerProperty("Fristen_"+disziplin, "FristBeginn"+Integer.toString(i+1)));
		}
		odummy.add(xdummy.clone());
		xdummy.clear();
		for(int i = 0; i < anzahl;i++){
			//xdummy.add( (fristenini.getIntegerProperty("Fristen_"+disziplin, "BeginnKalendertage"+Integer.toString(i+1))==1 ? true : false));
			xdummy.add(fristenTesten("Fristen_"+disziplin, "BeginnKalendertage"+Integer.toString(i+1),false));
		}
		odummy.add(xdummy.clone());
		xdummy.clear();
		for(int i = 0; i < anzahl;i++){
			xdummy.add(fristenTesten("Fristen_"+disziplin, "FristUnterbrechung"+Integer.toString(i+1),true));
			//xdummy.add(fristenini.getIntegerProperty("Fristen_"+disziplin, "FristUnterbrechung"+Integer.toString(i+1)));
		}
		odummy.add(xdummy.clone());
		xdummy.clear();
		for(int i = 0; i < anzahl;i++){
			xdummy.add(fristenTesten("Fristen_"+disziplin, "UnterbrechungKalendertage"+Integer.toString(i+1),false));
			//xdummy.add( (fristenini.getIntegerProperty("Fristen_"+disziplin, "UnterbrechungKalendertage"+Integer.toString(i+1))==1 ? true : false));
		}
		odummy.add(xdummy.clone());
		xdummy.clear();
		for(int i = 0; i < anzahl;i++){
			xdummy.add(fristenTesten("Fristen_"+disziplin, "BeginnMitSamstag"+Integer.toString(i+1),false));
			//xdummy.add( (fristenini.getIntegerProperty("Fristen_"+disziplin, "UnterbrechungKalendertage"+Integer.toString(i+1))==1 ? true : false));
		}
		odummy.add(xdummy.clone());
		xdummy.clear();
		for(int i = 0; i < anzahl;i++){
			xdummy.add(fristenTesten("Fristen_"+disziplin, "UnterbrechungMitSamstag"+Integer.toString(i+1),false));
			//xdummy.add( (fristenini.getIntegerProperty("Fristen_"+disziplin, "UnterbrechungKalendertage"+Integer.toString(i+1))==1 ? true : false));
		}
		odummy.add(xdummy.clone());
		xdummy.clear();
		
		hmFristen.put(disziplin,(Vector<Object>)odummy.clone());
		odummy.clear();
	}
	
	/***
	 * 
	 * 
	 * @param kategorie
	 * @param item
	 * @param retint
	 * @return
	 */
	private static Object fristenTesten(String kategorie,String item, boolean retint){
		if(fristenini.getIntegerProperty(kategorie,item)==null){
			System.out.println("SytemPreislisten: erstelle Parameter für Kategorie="+kategorie+" Preisgruppe="+item);
			if(retint){
				fristenini.setIntegerProperty(kategorie,item,14,null);
			}else{
				fristenini.setIntegerProperty(kategorie,item,1,null);
			}
			INITool.saveIni(fristenini);
		}
		if(retint){
			return fristenini.getIntegerProperty(kategorie,item);
		}else{
			return (fristenini.getIntegerProperty(kategorie,item)==1 ? true : false );
		}
	}
	
/*********************************/	
}
