package Tools;

import java.util.Vector;



public class LeistungTools {
	public static String[] getLeistung(String hmart,String hmposition,int preisgruppe){
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
