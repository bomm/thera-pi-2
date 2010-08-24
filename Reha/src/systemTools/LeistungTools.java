package systemTools;

import java.util.Vector;

import systemEinstellungen.SystemPreislisten;
import terminKalender.ParameterLaden;

public class LeistungTools {
	
	public static String[] getLeistung(String hmart,String hmposition,int preisgruppe){
		String ret[] = {"",""};
		Vector vec = null;
		int ipos = Integer.parseInt(hmposition);
		for(int i = 0;i <1;i++){
			if(hmart.equals("KG")){
				//vec = ParameterLaden.vKGPreise;
				vec = SystemPreislisten.hmPreise.get("Physio").get(preisgruppe);
				break;
			}
			if(hmart.equals("MA")){
				//vec = ParameterLaden.vMAPreise;
				vec = SystemPreislisten.hmPreise.get("Massage").get(preisgruppe);
				break;
			}
			if(hmart.equals("ER")){
				//vec = ParameterLaden.vERPreise;
				vec = SystemPreislisten.hmPreise.get("Ergo").get(preisgruppe);				
				break;
			}
			if(hmart.equals("LO")){
				//vec = ParameterLaden.vLOPreise;
				vec = SystemPreislisten.hmPreise.get("Logo").get(preisgruppe);
				break;
			}
			if(hmart.equals("RH")){
				//vec = ParameterLaden.vRHPreise;
				vec = SystemPreislisten.hmPreise.get("Reha").get(preisgruppe);				
				break;
			}
			// wenn jetzt noch nicht abgebrochen wurde gib null,null zur�ck
			return ret;
			
		}
		int lang = vec.size();
		for(int i = 0; i <lang;i++){
			if( Integer.parseInt( (String) ((Vector)vec.get(i)).get(9)) == ipos ){
				return new String[] {String.valueOf( (String) ((Vector)vec.get(i)).get(0)),
						String.valueOf( (String) ((Vector)vec.get(i)).get(1))};
				
			}
			
		}
		return ret;
	}

}
