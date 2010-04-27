package stammDatenTools;

import hauptFenster.Reha;
import systemEinstellungen.SystemConfig;
import systemTools.StringTools;
import terminKalender.DatFunk;

public class PatTools {
	
	/*
		lAdrPDaten = Arrays.asList(new String[]{
		"<Padr1>",
		"<Padr2>",
		"<Padr3>",
		"<Padr4>",
		"<Padr5>",
		"<Pgeboren>",
		"<Panrede>",
		"<Pnname>",
		"<Pvname>",
		"<Pbanrede>",
		"<Ptelp>",
		"<Ptelg>",
		"<Ptelmob>",
		"<Pfax>",
		"<Pemail>",
		"<Pid>"});
		"<Palter>",
		"<Pzigsten>"}
		hmAdrPDaten.put(lAdrPDaten.get(i),"");
 
	 * 
	 */
	public static void constructPatHMap(){
		boolean isherr = false;
		boolean iskind = false;
		//int lang = SystemConfig.hmAdrPDaten.hashCode();
		//System.out.println(lang);
		//SystemConfig.hmAdrPDaten.put("<Padr1>", Reha.thisClass.patpanel.patDaten.get(0));
		String anrede = StringTools.EGross(Reha.thisClass.patpanel.patDaten.get(0));
		if(anrede.toUpperCase().equals("HERR")){
			isherr = true;
		}
		String titel =  StringTools.EGross(Reha.thisClass.patpanel.patDaten.get(1));
		String vorname =  StringTools.EGross(Reha.thisClass.patpanel.patDaten.get(3));
		String nachname =  StringTools.EGross(Reha.thisClass.patpanel.patDaten.get(2));
		String strasse = StringTools.EGross(Reha.thisClass.patpanel.patDaten.get(21));
		String plzort = Reha.thisClass.patpanel.patDaten.get(23)+" "+StringTools.EGross(Reha.thisClass.patpanel.patDaten.get(24));
		String geboren = DatFunk.sDatInDeutsch(Reha.thisClass.patpanel.patDaten.get(4));
		String zeile1 = "";
		String zeile2 = "";
		String zeile3 = "";
		String branrede = "";
		int jahrheute = new Integer(DatFunk.sHeute().substring(6));
		int jahrgeboren = new Integer(geboren.substring(6));
		int ialter = jahrheute - jahrgeboren;
		
		
		if( ialter <= 13){
			iskind = true;
		}
		SystemConfig.hmAdrPDaten.put("<Palter>", Integer.toString(ialter));
		if(ialter >= 20){
			SystemConfig.hmAdrPDaten.put("<Pzigsten>", Integer.toString(ialter)+"-sten");	
		}else{
			SystemConfig.hmAdrPDaten.put("<Pzigsten>", Integer.toString(ialter)+"-ten");
		}

		zeile1 = (titel.length() > 0 ? titel+" " : "")+vorname+" "+nachname;
		zeile2 = strasse;
		zeile3 = plzort;
		if(titel.indexOf("med.") > 0){
				titel = titel.replace("med.", "");				
		}
		if(isherr){
			if(!iskind){
				branrede = "Sehr geehrter Herr"+(titel.length() > 0 ? " "+titel : "")+" "+nachname;
				SystemConfig.hmAdrPDaten.put("<Panrede>", anrede);
				SystemConfig.hmAdrPDaten.put("<Pihnen>", "Ihnen");
				SystemConfig.hmAdrPDaten.put("<Pihrem>", "Ihrem");
			}else{
				branrede = "Lieber "+vorname;
				SystemConfig.hmAdrPDaten.put("<Panrede>", "");				
				SystemConfig.hmAdrPDaten.put("<Pihnen>", "Dir");
				SystemConfig.hmAdrPDaten.put("<Pihrem>", "Deinem");
			}

		}else{
			if(!iskind){
				branrede = "Sehr geehrte Frau"+(titel.length() > 0 ? " "+titel : "")+" "+nachname;
				SystemConfig.hmAdrPDaten.put("<Panrede>", anrede);
				SystemConfig.hmAdrPDaten.put("<Pihnen>", "Ihnen");
				SystemConfig.hmAdrPDaten.put("<Pihrem>", "Ihrem");
			}else{
				branrede = "Liebe "+vorname;
				SystemConfig.hmAdrPDaten.put("<Panrede>", "");
				SystemConfig.hmAdrPDaten.put("<Pihnen>", "Dir");
				SystemConfig.hmAdrPDaten.put("<Pihrem>", "Deinem");
			}
		}
				
		SystemConfig.hmAdrPDaten.put("<Padr1>", zeile1);
		SystemConfig.hmAdrPDaten.put("<Padr2>", zeile2);
		SystemConfig.hmAdrPDaten.put("<Padr3>", zeile3);
		SystemConfig.hmAdrPDaten.put("<Pbanrede>", branrede);
		SystemConfig.hmAdrPDaten.put("<Pgeboren>", geboren);
		SystemConfig.hmAdrPDaten.put("<Pnname>", nachname);
		SystemConfig.hmAdrPDaten.put("<Pvname>", vorname);		
				
		SystemConfig.hmAdrPDaten.put("<Ptelp>", Reha.thisClass.patpanel.patDaten.get(18));
		SystemConfig.hmAdrPDaten.put("<Ptelg>", Reha.thisClass.patpanel.patDaten.get(19));
		SystemConfig.hmAdrPDaten.put("<Ptelmob>", Reha.thisClass.patpanel.patDaten.get(20));
		//SystemConfig.hmAdrPDaten.put("<Pfax>", Reha.thisClass.patpanel.patDaten.get(21));
		SystemConfig.hmAdrPDaten.put("<Pemail>", Reha.thisClass.patpanel.patDaten.get(50));
		SystemConfig.hmAdrPDaten.put("<Ptitel>", titel);
		SystemConfig.hmAdrPDaten.put("<Pid>", Reha.thisClass.patpanel.patDaten.get(66));
	}
	public static String[] constructPatHMapFromStrings(String panrede,
												String ptitel,
												String pvorname,
												String pnachname,
												String pstrasse,
												String pplz,
												String port){
		String[] adresse = {null,null,null,null,null};		
		boolean isherr = false;
		String anrede = StringTools.EGross(panrede);
		if(anrede.toUpperCase().equals("HERR")){
			isherr = true;
		}
		String titel =  StringTools.EGross(ptitel);
		String vorname =  StringTools.EGross(pvorname);
		String nachname =  StringTools.EGross(pnachname);
		String strasse = StringTools.EGross(pstrasse);
		String plzort = pplz+" "+StringTools.EGross(port);
		String branrede = "";
		if(isherr){
				branrede = "Sehr geehrter Herr"+(titel.length() > 0 ? " "+titel : "")+" "+nachname;
				adresse[0] = anrede;
		}else{
				branrede = "Sehr geehrte Frau"+(titel.length() > 0 ? " "+titel : "")+" "+nachname;
				adresse[0] = anrede;
		}
		adresse[1]=vorname+(titel.length() > 0 ? " "+titel : "")+" "+nachname;
		adresse[2]=strasse;
		adresse[3]=plzort;
		adresse[4]=branrede;


		return adresse;
	}
}
