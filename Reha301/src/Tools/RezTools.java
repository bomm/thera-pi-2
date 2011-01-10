package Tools;

import java.util.Vector;

public class RezTools {
	
	public static String getLangtextFromID(String id,Vector<Vector<String>> vec){
		try{
		int lang = vec.size(),i;
		int idpos = vec.get(0).size()-1;
		String ret = "kein Lantext vorhanden";
		for(i = 0; i < lang;i++){
			if( vec.get(i).get(idpos).equals(id)){
				ret = vec.get(i).get(0).toString();
				break;
			}
		}
		return ret;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
	
	public static String getPreisAktFromID(String id,Vector<Vector<String>> vec){
		int lang = vec.size(),i;
		int idpos = vec.get(0).size()-1;
		String ret = "0.00";
		for(i = 0; i < lang;i++){
			if( vec.get(i).get(idpos).equals(id)){
				ret = vec.get(i).get(3).toString();
				break;
			}
		}
		return ret;
	}
	public static String getPreisAktFromID(String id,String preisgruppe,Vector<Vector<String>> vec){
		int lang = vec.size(),i;
		int idpos = vec.get(0).size()-1;
		String ret = "0.00";
		for(i = 0; i < lang;i++){
			if( vec.get(i).get(idpos).equals(id)){
				ret = vec.get(i).get(3).toString();
				break;
			}
		}
		return ret;
	}
	public static String getPreisAltFromID(String id,String preisgruppe,Vector<Vector<String>> vec){
		int lang = vec.size(),i;
		int idpos = vec.get(0).size()-1;
		String ret = "0.00";
		for(i = 0; i < lang;i++){
			if( vec.get(i).get(idpos).equals(id)){
				ret = vec.get(i).get(4).toString();
				break;
			}
		}
		return ret;
	}
/***************************************/
	public static String getPreisAktFromPos(String pos,String preisgruppe,Vector<Vector<String>> vec){
		try{
		int lang = vec.size(),i;
		String ret = "0.00";
		for(i = 0; i < lang;i++){
			if( vec.get(i).get(2).equals(pos)){
				ret = vec.get(i).get(3).toString();
				break;
			}
		}
		return ret;
		}catch(Exception ex){
			ex.printStackTrace();
			return "0.00";
		}
	}
	public static String getPreisAltFromPos(String pos,String preisgruppe,Vector<Vector<String>> vec){
		int lang = vec.size(),i;
		String ret = "0.00";
		for(i = 0; i < lang;i++){
			if( vec.get(i).get(1).equals(pos)){
				ret = vec.get(i).get(4).toString();
				break;
			}
		}
		return ret;
	}
	
/***************************************/	
	public static String getIDFromPos(String pos,String preisgruppe,Vector<Vector<String>> vec){
		int lang = vec.size(),i;
		int idpos = vec.get(0).size()-1;
		String ret = "-1";
		for(i = 0; i < lang;i++){
			if( vec.get(i).get(2).equals(pos)){
				ret = vec.get(i).get(idpos).toString();
				break;
			}
		}
		return ret;
	}	
	
	public static String getIDFromKurzform(String kurzform,Vector<Vector<String>> vec){
		int lang = vec.size(),i;
		int idpos = vec.get(0).size()-1;
		String ret = "";
		for(i = 0; i < lang;i++){
			if(vec.get(i).get(1).equals(kurzform)){
				ret = vec.get(i).get(idpos).toString(); 
				break;
			}
		}
		return ret;
	}

	public static String getPosFromID(String id,String preisgruppe,Vector<Vector<String>> vec){
		int lang = vec.size(),i;
		int idpos = vec.get(0).size()-1;
		String ret = "";
		for(i = 0; i < lang;i++){
			if( vec.get(i).get(idpos).equals(id)){
				ret = vec.get(i).get(2).toString();
				break;
			}
		}
		return ret;
	}



}
