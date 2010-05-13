package org.thera_pi.nebraska.gui.utils;

public class IntegerTools {
	
	public static int trailNullAndRetInt(String zahl){
		int ret = 0;
		
		// null pointer is 0
		if(zahl==null){
			return ret;
		}
		
		int lang = zahl.length();
		// empty string is 0
		if(lang == 0){
			return ret;
		}
		int i  = 0;
		// ???
		for(i = 0; i < lang;i++){
			// FIXME I think a String (substring) is never equal to number 0 
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

}
