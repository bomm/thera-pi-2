package systemTools;

import java.util.Vector;

import terminKalender.ParameterLaden;

public class LeistungTools {
	
	public static String[] getLeistung(String hmart,String hmposition){
		String ret[] = {"",""};
		Vector vec = null;
		int ipos = new Integer(hmposition);
		for(int i = 0;i <1;i++){
			if(hmart.equals("KG")){
				vec = ParameterLaden.vKGPreise;
				break;
			}
			if(hmart.equals("MA")){
				vec = ParameterLaden.vMAPreise;
				break;
			}
			if(hmart.equals("ER")){
				vec = ParameterLaden.vERPreise;
				break;
			}
			if(hmart.equals("LO")){
				vec = ParameterLaden.vLOPreise;
				break;
			}
			if(hmart.equals("RH")){
				vec = ParameterLaden.vRHPreise;
				break;
			}
			// wenn jetzt noch nicht abgebrochen wurde gib null,null zurück
			return ret;
			
		}
		int lang = vec.size();
		for(int i = 0; i <lang;i++){
			if( new Integer( (String) ((Vector)vec.get(i)).get(35)) == ipos ){
				return new String[] {new String( (String) ((Vector)vec.get(i)).get(0)),
						new String( (String) ((Vector)vec.get(i)).get(1))};
				
			}
			
		}
		return ret;
	}

}
