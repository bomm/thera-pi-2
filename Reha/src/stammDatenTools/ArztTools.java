package stammDatenTools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import patientenFenster.PatGrundPanel;
import sqlTools.SqlInfo;
import systemEinstellungen.SystemConfig;
import systemTools.StringTools;
import terminKalender.datFunk;

public class ArztTools {
	public static void constructArztHMap(){
		boolean isherr = false;
		int xid = StringTools.ZahlTest(PatGrundPanel.thisClass.patDaten.get(67));
		if(xid <= 0){
			return;
		}
		List<String> nichtlesen = Arrays.asList(new String[] {""});
		Vector vec = SqlInfo.holeSatz("arzt", " * ", "id='"+xid+"'", new ArrayList());

		/*
		 * 	List<String> lAdrADaten = Arrays.asList(new String[]{"<Aadr1>","<Aadr2>","<Aadr3>","<Aadr4>","<Aadr5>",
		 *														"<Atel>","<Afax>","<Aemail>","<Aid>"});
		 */
		String anrede = StringTools.EGross((String)vec.get(0));
		if(anrede.toUpperCase().equals("HERR")){
			isherr = true;
		}
		String titel =  StringTools.EGross((String) vec.get(1));
		String vorname =  StringTools.EGross((String) vec.get(3));
		String nachname =  StringTools.EGross((String) vec.get(2));
		String strasse = StringTools.EGross((String) vec.get(4));
		String plzort = (String) vec.get(5)+" "+StringTools.EGross((String) vec.get(6));
		String zeile1 = "";
		String zeile2 = "";
		String zeile3 = "";
		String branrede = "";
		
		
		SystemConfig.hmAdrADaten.put("<Aadr1>", anrede);

		zeile1 = new String(vorname+(titel.length() > 0 ? " "+titel : "")+" "+nachname);
		SystemConfig.hmAdrADaten.put("<Aadr2>", zeile1);
		
		SystemConfig.hmAdrADaten.put("<Aadr3>", strasse);
		SystemConfig.hmAdrADaten.put("<Aadr4>", plzort);

		if(titel.indexOf("med.") > 0){
			titel = titel.replace("med.", "");				
		}
		if(isherr){
			branrede = "Sehr geehrter Herr"+(titel.length() > 0 ? " "+titel : "")+" "+nachname;
		}else{
			branrede = "Sehr geehrte "+anrede+(titel.length() > 0 ? " "+titel : "")+" "+nachname;
		}
		SystemConfig.hmAdrADaten.put("<Aadr5>", branrede);
		
		SystemConfig.hmAdrADaten.put("<Atel>", (String) vec.get(8));
		SystemConfig.hmAdrADaten.put("<Afax>", (String) vec.get(9));		
		SystemConfig.hmAdrADaten.put("<Aemail>", (String) vec.get(14));		
		SystemConfig.hmAdrADaten.put("<Aid>", (String) vec.get(16));
	}
}
