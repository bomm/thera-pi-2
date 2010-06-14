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



}
