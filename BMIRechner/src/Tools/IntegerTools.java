package Tools;

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
			if(! zahl.substring(i,i+1).equals(0)){
				break;
			}
		}
		if(i==(lang-1)){
			return ret;
		}
		ret = Integer.parseInt(zahl.substring(i));
		return ret;
	}
	
	public static int killeNullen(String zahlen){
		String test = zahlen;
		int retwert = -1;
		int position = -1;
		for(int i = 0; i < test.length() ; i++){
			if(!test.substring(i,i+1).equals("0")){
				position = i;
				break;
			}
		}
		if(position < 0){
			return 0;
		}
		/***********/
		try{
			retwert = new Integer(test.substring(position)); 	
		}catch(Exception ex){
			
		}
		return retwert;
	}


}
