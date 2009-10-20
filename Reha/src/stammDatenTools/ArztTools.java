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
	public static void constructArztHMap(String id){
		boolean isherr = false;
		int xid;
		if(id.equals("")){
			xid = StringTools.ZahlTest(PatGrundPanel.thisClass.patDaten.get(67));			
		}else{
			xid = new Integer(id);
		}

		if(xid <= 0){
			return;
		}
		List<String> nichtlesen = Arrays.asList(new String[] {""});
		Vector vec = SqlInfo.holeSatz("arzt", " * ", "id='"+xid+"'", new ArrayList());

		/*
		 * 	List<String> lAdrADaten = Arrays.asList(new String[]{"<Aadr1>","<Aadr2>","<Aadr3>","<Aadr4>","<Aadr5>",
		 *														"<Atel>","<Afax>","<Aemail>","<Aid>"});
		 */
		if(vec.size()==0){return;}
		String anrede = (String)vec.get(0);
		if(anrede.toUpperCase().equals("HERR")){
			isherr = true;
		}
		String titel =  ((String) vec.get(1)).trim();
		String vorname =  (String) vec.get(3);
		String nachname =  (String) vec.get(2);
		String strasse = (String) vec.get(4);
		String plzort = (String) vec.get(5)+" "+(String) vec.get(6);
		String zeile1 = "";
		String zeile2 = "";
		String zeile3 = "";
		String branrede = "";
		
		SystemConfig.hmAdrADaten.put("<Aklinik>", ((String) vec.get(12)).trim() );
		
		SystemConfig.hmAdrADaten.put("<Aadr1>", anrede);

		zeile1 = new String((titel.trim().length() > 0 ? titel+" " : "")+vorname+" "+nachname);
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
		
		//"<Aihrer>","<Apatientin>","<Adie>"
		if(! PatGrundPanel.thisClass.aktPatID.equals("")){
			boolean bfrau = ( ((String)vec.get(0)).equalsIgnoreCase("FRAU") ? true : false );
			SystemConfig.hmAdrADaten.put("<Aihrer>", (bfrau ? "Ihrer" : "Ihres"));
			SystemConfig.hmAdrADaten.put("<Apatientin>", (bfrau ? "Patientin" : "Patienten"));
			SystemConfig.hmAdrADaten.put("<Adie>", (bfrau ? "die" : "den"));
			
		}
	}
}
