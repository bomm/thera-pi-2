package systemEinstellungen;

import hauptFenster.Reha;

import java.util.Vector;

import terminKalender.DatFunk;
import terminKalender.ZeitFunk;

public class GruppenEinlesen{
	public Vector gruppenParam = null;
	public Vector<String> gruppenNamen;
	public Vector<Integer> gruppenDauer;
	public Vector<Long[]> gruppenGueltig;
	public Vector gruppeAlle = new Vector();
	public int anzahl;
	private INIFile ini;
	public GruppenEinlesen init(){
		ini = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/gruppen.ini");
		//ini = new INIFile("/RehaVerwaltung/ini/gruppen.ini");

		anzahl = new Integer(ini.getStringProperty("Gruppen", "GruppenAnzahl")); 
		gruppenNamen = new Vector<String>();
		gruppenNamen = new Vector<String>();
		gruppenGueltig = new Vector<Long[]>();
		for(int i = 1; i<= anzahl;i++){
			gruppenNamen.add(ini.getStringProperty("Gruppen", "GruppenName"+i));
			Long[] datwert = new Long[3];
			datwert[0] = DatFunk.DatumsWert(ini.getStringProperty("Gruppen", "Gruppe"+i+"NeuAb"));
			datwert[1] = DatFunk.DatumsWert(ini.getStringProperty("Gruppen", "Gruppe"+i+"AltBis"));
			datwert[2] = new Long(ini.getStringProperty("Gruppen", "Gruppe"+i+"Dauer"));
			gruppenGueltig.add(datwert.clone());
		}
		gruppeAlle = new Vector();
		for(int i = 1; i<= anzahl;i++){ // Alle Gruppen
			String rubrikName = gruppenNamen.get(i-1);
			Vector gruppeWoche = new Vector();
			for(int j = 1; j<=2;j++){//Alte Definition und neue Definition
				Vector gruppeTag = new Vector();
				String rubrik = rubrikName+"_"+j;
				System.out.println(rubrik);
				for(int k = 1;k<=7;k++){//Alle Wochentage
					int gruppenAmTag = new Integer(ini.getStringProperty(rubrik, "WOTA"+k));
					Vector gruppeAmTag = new Vector();
					for(int l = 1;l<= gruppenAmTag;l++){//einzelne Tage einlesen
						Vector gruppenParam = new Vector();
						String vonbis = ini.getStringProperty(rubrik,"TA"+k+"GR"+l);
						gruppenParam.add(ZeitFunk.MinutenSeitMitternacht(vonbis.split("-")[0]));
						gruppenParam.add(ZeitFunk.MinutenSeitMitternacht(vonbis.split("-")[1]));
						gruppenParam.add(ini.getStringProperty(rubrik,"TA"+k+"ZE"+l));
						gruppenParam.add(ini.getStringProperty(rubrik,"TA"+k+"TX"+l));						
						gruppenParam.add(ini.getStringProperty(rubrik,"TA"+k+"DA"+l));
						gruppeAmTag.add((Vector)gruppenParam.clone());
					}//Ende einzelne Tage einlesen
					gruppeTag.add((Vector)gruppeAmTag.clone());	
				}//Ende alle Wochentage
				gruppeWoche.add((Vector)gruppeTag.clone());
			}//Ende alte / neue Definition
			gruppeAlle.add((Vector)gruppeWoche.clone());
		}//Ende alle Gruppen
		return this;
	}
	
}