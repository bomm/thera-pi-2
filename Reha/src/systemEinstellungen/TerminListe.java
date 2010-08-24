package systemEinstellungen;

import hauptFenster.Reha;

import java.util.Vector;

public class TerminListe{
	public int AnzahlTerminTabellen;
	public int AnzahlSpaltenProTabellen;
	public Vector<String> NamenSpalten = new Vector<String>();
	
	public String  PatNamenPlatzhalter; 
	//public String[] NameTabelle = {null,null,null,null,null,null};
	public int AnzahlTermineProTabelle;
	public String NameTemplate;
	public String NameTerminDrucker;
	public String iniName = Reha.proghome+"ini/"+Reha.aktIK+"/terminliste.ini";
	public int PatNameDrucken;
	public int MitUeberschrift;
	public boolean DirektDruck;
	public TerminListe init(){
		AnzahlTerminTabellen = Integer.valueOf(RWJedeIni.leseIniDatei(iniName, "TerminListe1", "AnzahlTabellen"));
		AnzahlSpaltenProTabellen = Integer.valueOf(RWJedeIni.leseIniDatei(iniName, "TerminListe1", "AnzahlSpaltenProTabellen"));
		for(int i = 0;i<AnzahlSpaltenProTabellen;i++){
			NamenSpalten.add(RWJedeIni.leseIniDatei(iniName, "TerminListe1", "InhaltSpalte"+(i+1)) );
		}
		AnzahlTermineProTabelle = Integer.valueOf(RWJedeIni.leseIniDatei(iniName, "TerminListe1", "AnzahlTermineProTabelle"));
		NameTemplate = RWJedeIni.leseIniDatei(iniName, "TerminListe1", "NameTemplate");
		NameTerminDrucker = RWJedeIni.leseIniDatei(iniName, "TerminListe1", "NameTerminDrucker");
		PatNameDrucken = Integer.valueOf(RWJedeIni.leseIniDatei(iniName, "TerminListe1", "PatNameDrucken"));
		PatNamenPlatzhalter = RWJedeIni.leseIniDatei(iniName, "TerminListe1", "PatNamePlatzhalter");
		MitUeberschrift = Integer.valueOf(RWJedeIni.leseIniDatei(iniName, "TerminListe1", "MitSpaltenUeberschrift"));
		DirektDruck = (RWJedeIni.leseIniDatei(iniName, "TerminListe1", "DirektDruck").trim().equals("0") ? false : true);
		//System.out.println(AnzahlTerminTabellen);
		////System.out.println(NameTabelle);
		//System.out.println(AnzahlTermineProTabelle);		
		//System.out.println(NameTemplate);
		//System.out.println(NameTerminDrucker);		
	return this;
	}
	
}