package utils;

public class StringTools {
	
	public static String EGross(String string){
		if(string == null){
			return "";
		}
		if(string.trim().equals("")){
			return "";
		}

		String test = new String(string.trim());
		String neuString = "";
		try{
		for(int y = 0; y < 1; y++){
			

		if(  (test.indexOf(" ") < 0)  && (test.indexOf("-") < 0) ){
			neuString = test.substring(0,1).toUpperCase()+
			 (test.length() > 1 ? test.substring(1).toLowerCase() : "");
			test = new String(neuString.trim());
			return test;
		}
		
		if(test.indexOf(" ") > -1){
			String[] splitString = test.split(" ");
			for(int i = 0;i < splitString.length;i++){
				neuString = neuString + 
							(splitString[i].substring(0,1).toUpperCase())+
							 (splitString[i].length() > 1 ? splitString[i].substring(1).toLowerCase() : "");
				neuString = neuString + " ";
			}
			test = new String(neuString.trim());
		}
		if(test.indexOf(" - ") > -1){
			neuString = "";
			String[] splitString = test.split("-");
			for(int i = 0;i < splitString.length;i++){
				neuString = neuString + 
							(splitString[i].substring(0,1).toUpperCase())+
						 (splitString[i].length() > 1 ? splitString[i].substring(1).toLowerCase() : "");
				neuString = neuString + (i < (splitString.length-1) ? " - " : "");
			}
			test = new String(neuString.trim());
			break;
		}
		if(test.indexOf("-") > -1){
			neuString = "";
			String[] splitString = test.split("-");
			for(int i = 0;i < splitString.length;i++){
				neuString = neuString + 
							(splitString[i].substring(0,1).toUpperCase())+
						 (splitString[i].length() > 1 ? splitString[i].substring(1).toLowerCase() : "");
				neuString = neuString + (i < (splitString.length-1) ? "-" : "");
			}
			test = new String(neuString.trim());
		}
		
		}
		if(test.indexOf("prof.") > -1){
			neuString = test.replaceAll("prof.", "Prof.");
			test = new String(neuString);
		}
		if(test.indexOf("dr.") > -1){
			neuString = test.replaceAll("dr.", "Dr.");
			test = new String(neuString);
		}
		if(test.indexOf("dres.") > -1){
			neuString = test.replaceAll("dres.", "Dres.");
			test = new String(neuString);
		}
		if(test.indexOf(" Med. ") > -1){
			neuString = test.replaceAll(" Med. ", " med. ");
			test = new String(neuString);
		}
		if(test.indexOf("Med.") > -1){
			neuString = test.replaceAll("Med.", "med.");
			test = new String(neuString);
		}

		if(test.indexOf(" Von ") > -1){
			neuString = test.replaceAll(" Von ", " von ");
			test = new String(neuString);
		}
		
		if(test.indexOf(" Und ") > -1){
			neuString = test.replaceAll(" Und ", " und ");
			test = new String(neuString);
		}
		if(test.indexOf(" Zu ") > -1){
			neuString = test.replaceAll(" Zu ", " zu ");
			test = new String(neuString);
		}
		if(test.indexOf(" An ") > -1){
			neuString = test.replaceAll(" An ", " an ");
			test = new String(neuString);
		}

		if(test.indexOf(" Auf ") > -1){
			neuString = test.replaceAll(" Auf ", " auf ");
			test = new String(neuString);
		}

		if(test.indexOf(" Der ") > -1){
			neuString = test.replaceAll(" Der ", " der ");
			test = new String(neuString);
		}
		if(test.indexOf(" Bei ") > -1){
			neuString = test.replaceAll(" Bei ", " bei ");
			test = new String(neuString);
		}
		if(test.indexOf(" Den ") > -1){
			neuString = test.replaceAll(" Den ", " den ");
			test = new String(neuString);
		}
		if(test.indexOf(" Die ") > -1){
			neuString = test.replaceAll(" Die ", " die ");
			test = neuString;
		}
		if(test.indexOf("-Die") > -1){
			neuString = test.replaceAll("-Die", "-die");
			test = neuString;
		}
		if(test.indexOf("Aok") > -1){
			neuString = test.replaceAll("Aok", "AOK");
			test = neuString;
		}
		if(test.indexOf("Bkk") > -1){
			neuString = test.replaceAll("Bkk", "BKK");
			test = neuString;
		}
		if(test.indexOf("Ikk") > -1){
			neuString = test.replaceAll("ikk", "IKK");
			test = neuString;
		}
		if(test.indexOf("Tkk") > -1){
			neuString = test.replaceAll("Tkk", "TKK");
			test = neuString;
		}
		if(test.indexOf("Dak") > -1){
			neuString = test.replaceAll("Dak", "DAK");
			test = neuString;
		}
		if(test.indexOf("str.") > -1){
			neuString = test.replaceAll(" str.", " Str.");
			test = neuString;
		}

		if(test.indexOf(" U. ") > -1){
			neuString = test.replaceAll(" U. ", " u. ");
			test = neuString;
		}

		if(test.indexOf("gesundheitskasse") > -1){
			neuString = test.replaceAll("gesundheitskasse", "Gesundheitskasse");
			test = neuString;
		}

		}catch(java.lang.StringIndexOutOfBoundsException ex){
			//System.out.println(ex);
			return ""+test;
		}

		return neuString.trim();
	}
	
	public static String NullTest(String string){
		if(string==null){
			return "";
		}else{
			return string;
		}
	}
	public static int ZahlTest(String string){
		if(string==null){
			return -1;
		}else{
			int zahl;
			try {
				zahl = new Integer(string.trim());
			}catch(NumberFormatException ex){
				zahl = -1;
			}
			return zahl;
		}
	}
	public static String Escaped(String string){
		String escaped = string.replaceAll("\'", "\\\\'");
		escaped = escaped.replaceAll("\"", "\\\\\"");
		return escaped;
	}
	public static String EscapedDouble(String string){
		String escaped = string.replaceAll("\'", "\\\\\\'");
		return escaped;
	}
	public static String fuelleMitZeichen(String string,String zeichen,boolean vorne,int endlang){
		String orig = string;
		String praefi = zeichen;
		String dummy = "";
		String sret = ""; 
		int solllang = endlang;
		int istlang = orig.length();
		int differenz = solllang - istlang;
		if(differenz > 0){
			for(int i = 0; i < differenz;i++){
				dummy = dummy+praefi;
			}
			if(vorne){
				sret = dummy+orig;
			}else{
				sret = orig+dummy;
			}
		}else{
			sret = orig;
		}
		return sret;
	}

}
