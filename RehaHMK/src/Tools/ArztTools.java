package Tools;


import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.JComponent;

import CommonTools.SqlInfo;

import rehaHMK.RehaHMK;


public class ArztTools {
	public static void constructArztHMap(String id){
		boolean isherr = false;
		int xid;
		xid = Integer.valueOf(id);

		if(xid <= 0){
			return;
		}
		List<String> nichtlesen = Arrays.asList(new String[] {""});
		Vector<String> vec = SqlInfo.holeSatz("arzt", " * ", "id='"+xid+"'", nichtlesen);

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
		//String zeile2 = "";
		//String zeile3 = "";
		String branrede = "";
		
		RehaHMK.hmAdrADaten.put("<Aklinik>", ((String) vec.get(12)).trim() );
		
		RehaHMK.hmAdrADaten.put("<Aadr1>", anrede);

		zeile1 = (titel.trim().length() > 0 ? titel+" " : "")+vorname+" "+nachname;
		RehaHMK.hmAdrADaten.put("<Aadr2>", zeile1);
		
		RehaHMK.hmAdrADaten.put("<Aadr3>", strasse);
		RehaHMK.hmAdrADaten.put("<Aadr4>", plzort);


		if(titel.indexOf("med.") > 0){
			titel = titel.replace("med.", "");				
		}
		if(isherr){
			branrede = "Sehr geehrter Herr"+(titel.length() > 0 ? " "+titel : "")+" "+nachname;
		}else{
			branrede = "Sehr geehrte "+anrede+(titel.length() > 0 ? " "+titel : "")+" "+nachname;
		}
		RehaHMK.hmAdrADaten.put("<Aadr5>", branrede);
		
		RehaHMK.hmAdrADaten.put("<Atel>", (String) vec.get(8));
		RehaHMK.hmAdrADaten.put("<Afax>", (String) vec.get(9));		
		RehaHMK.hmAdrADaten.put("<Aemail>", (String) vec.get(14));		
		RehaHMK.hmAdrADaten.put("<Aid>", (String) vec.get(16));
		

	}
}
