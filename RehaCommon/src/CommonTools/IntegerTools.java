package CommonTools;

public class IntegerTools {
	
	public static int trailNullAndRetInt(String zahl){
		int ret = 0;
		if(zahl==null){
			return ret;
		}
		int lang = zahl.length(); 
		if(lang == 0){
			return ret;
		}
		int i  = 0;
		for(i = 0; i < lang;i++){
			if(! zahl.substring(i,i+1).equals("0")){
				break;
			}
		}
		if(i==(lang-1)){
			return ret;
		}
		ret = Integer.parseInt(zahl.substring(i));
		return ret;
	}

}
