package CommonTools;

import java.util.HashMap;
import java.util.Vector;





public class LeistungTools {
	static HashMap<String,Vector<Vector<Vector<String>>>> hmPreise;
	public LeistungTools(HashMap<String,Vector<Vector<Vector<String>>>> hmpreise ){
		hmPreise = hmpreise;
	}
	public static void setPreise(HashMap<String,Vector<Vector<Vector<String>>>> hmpreise){
		hmPreise = hmpreise;
	}
	public static String[] getLeistung(String hmart,String hmposition,int preisgruppe){
		String ret[] = {"",""};
		Vector<Vector<String>> vec = null;
		int ipos = Integer.parseInt(hmposition);
		for(int i = 0;i <1;){
			if(hmart.equals("KG")){
				//vec = ParameterLaden.vKGPreise;
				vec = hmPreise.get("Physio").get(preisgruppe);
				break;
			}
			if(hmart.equals("MA")){
				//vec = ParameterLaden.vMAPreise;
				vec = hmPreise.get("Massage").get(preisgruppe);
				break;
			}
			if(hmart.equals("ER")){
				//vec = ParameterLaden.vERPreise;
				vec = hmPreise.get("Ergo").get(preisgruppe);				
				break;
			}
			if(hmart.equals("LO")){
				//vec = ParameterLaden.vLOPreise;
				vec = hmPreise.get("Logo").get(preisgruppe);
				break;
			}
			if(hmart.equals("RH")){
				//vec = ParameterLaden.vRHPreise;
				vec = hmPreise.get("Reha").get(preisgruppe);				
				break;
			}
			if(hmart.equals("PO")){
				//vec = ParameterLaden.vRHPreise;
				vec = hmPreise.get("Podo").get(preisgruppe);				
				break;
			}

			// wenn jetzt noch nicht abgebrochen wurde gib null,null zurück
			return ret;
			
		}
		int lang = vec.size();
		for(int i = 0; i <lang;i++){
			if( Integer.parseInt( (String) ((Vector<String>)vec.get(i)).get(9)) == ipos ){
				return new String[] {String.valueOf( (String) ((Vector<String>)vec.get(i)).get(0)),
						String.valueOf( (String) ((Vector<String>)vec.get(i)).get(1))};
				
			}
			
		}
		return ret;
	}
	
	/*********************************/
	public static String[] getLeistungRaw(String hmart,String hmposition,int preisgruppe){
		String ret[] = {"",""};
		Vector<Vector<String>> vec = null;
		String preisliste = null; 
		int ipos = Integer.parseInt(hmposition);
		for(int i = 0;i <1;){
			if(hmart.equals("KG")){
				//vec = ParameterLaden.vKGPreise;
				preisliste = "kgtarif"+Integer.toString(preisgruppe);
				break;
			}
			if(hmart.equals("MA")){
				//vec = ParameterLaden.vMAPreise;
				preisliste = "matarif"+Integer.toString(preisgruppe);
				break;
			}
			if(hmart.equals("ER")){
				//vec = ParameterLaden.vERPreise;
				preisliste = "ertarif"+Integer.toString(preisgruppe);				
				break;
			}
			if(hmart.equals("LO")){
				//vec = ParameterLaden.vLOPreise;
				preisliste = "lotarif"+Integer.toString(preisgruppe);
				break;
			}
			if(hmart.equals("RH")){
				//vec = ParameterLaden.vRHPreise;
				preisliste = "rhtarif"+Integer.toString(preisgruppe);				
				break;
			}
			if(hmart.equals("PO")){
				//vec = ParameterLaden.vRHPreise;
				preisliste = "potarif"+Integer.toString(preisgruppe);				
				break;
			}

			// wenn jetzt noch nicht abgebrochen wurde gib null,null zur�ck
			return ret;
			
		}
		String cmd = "select leistung,kuerzel from "+preisliste+" where id = '"+hmposition+"' LIMIT 1";
		vec = SqlInfo.holeFelder(cmd);
		if(vec.size() == 1){
			return new String[] {String.valueOf(vec.get(0).get(0)),String.valueOf(vec.get(0).get(1))};
		}
		return ret;
	}
	

}
	


