package stammDatenTools;

import hauptFenster.Reha;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import sqlTools.SqlInfo;
import systemEinstellungen.SystemConfig;
import systemTools.StringTools;

public class KasseTools {
	public static void constructKasseHMap(String id){
		int xid;
		if(id.equals("")){
			xid = StringTools.ZahlTest(Reha.thisClass.patpanel.patDaten.get(68));
		}else{
			xid = Integer.valueOf(id);
		}
		if(xid <= 0){
			return;
		}
		try{
			List<String> nichtlesen = Arrays.asList(new String[] {""});
			Vector<String> vec = SqlInfo.holeSatz("kass_adr", "kassen_nam1,kassen_nam2,strasse,plz,ort,telefon,fax,email1", "id='"+xid+"'", nichtlesen);
			SystemConfig.hmAdrKDaten.put("<Kadr1>", ((String) vec.get(0)).trim());
			SystemConfig.hmAdrKDaten.put("<Kadr2>", ((String)vec.get(1)).trim());
			SystemConfig.hmAdrKDaten.put("<Kadr3>", ((String)vec.get(2)).trim());
			SystemConfig.hmAdrKDaten.put("<Kadr4>", ((String)vec.get(3)).trim()+" "+((String)vec.get(4)).trim()  );
			SystemConfig.hmAdrKDaten.put("<Ktel>", ((String)vec.get(5)).trim());
			SystemConfig.hmAdrKDaten.put("<Kfax>", ((String)vec.get(6)).trim());
			SystemConfig.hmAdrKDaten.put("<Kemail>", ((String)vec.get(7)).trim());
			
		}catch(Exception ex){
			
		}
	}

}
