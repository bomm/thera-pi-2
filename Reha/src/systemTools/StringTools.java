package systemTools;

public class StringTools {
	
	public static String EGross(String string){
		if(string == null){
			return "";
		}
		if(string.trim().equals("")){
			return "";
		}

		String test = String.valueOf(string.trim());
		String neuString = "";
		try{
		boolean zerhackt = false;	
		for(int y = 0; y < 1; y++){

			if(  (test.indexOf(" ") < 0)  && (test.indexOf("-") < 0) && (test.indexOf("/") < 0) ){
				neuString = test.substring(0,1).toUpperCase()+
				 (test.length() > 1 ? test.substring(1).toLowerCase() : "");
				test = String.valueOf(neuString.trim());
				/***********/
				if(test.indexOf("Mc") > -1){
					try{
						if(test.length() >=3){
							if(!test.substring(2,3).equals(" ")){
								test = "Mc"+test.substring(2,3).toUpperCase()+test.substring(3).toLowerCase();
							}
						}
					}catch(Exception ex){
						
					}
				}
				/*
				if(test.indexOf("Mac") > -1){
					try{
						if(test.length() >=4){
							if(!test.substring(3,4).equals(" ")){
								test = "Mac"+test.substring(3,4).toUpperCase()+test.substring(4).toLowerCase(); 
							}
						}
					}catch(Exception ex){
						
					}
				}
				*/
				/***********/
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
				test = String.valueOf(neuString.trim());
				zerhackt = true;
			}

			if(test.indexOf(" - ") > -1){
				neuString = "";
				String[] splitString = test.split(" - ");
				for(int i = 0;i < splitString.length;i++){
					neuString = neuString + 
								(splitString[i].substring(0,1).toUpperCase())+
							 (splitString[i].length() > 1 ? splitString[i].substring(1).toLowerCase() : "");
					neuString = neuString + (i < (splitString.length-1) ? " - " : "");
				}
				test = String.valueOf(neuString.trim());
				zerhackt = true;
				break;
			}
			
			if(test.indexOf("-") > -1){
				neuString = "";
				String praefix = "";
				String[] splitString = test.split("-");
				if(splitString[0].indexOf(" ") >= 0){
					praefix = splitString[0].substring(0,splitString[0].lastIndexOf(" ")+1);
					splitString[0] = String.valueOf(splitString[0].substring(splitString[0].lastIndexOf(" ")+1)).trim();
				}
				for(int i = 0;i < splitString.length;i++){
					if(i==0){
						neuString = neuString+praefix;
					}
					neuString = neuString + 
								(splitString[i].substring(0,1).toUpperCase())+
							 (splitString[i].length() > 1 ? splitString[i].substring(1).toLowerCase() : "");
					neuString = neuString + (i < (splitString.length-1) ? "-" : "");
				}
				test = String.valueOf(neuString.trim());
				zerhackt = true;
				//System.out.println("in - Ergebnis = "+test);
			}
			
			
			if(test.indexOf("/") > -1 && !zerhackt){
				neuString = "";
				String[] splitString = test.split("/");
				for(int i = 0;i < splitString.length;i++){
					splitString[i] = splitString[i].trim();

					neuString = neuString +
								(splitString[i].substring(0,1).toUpperCase())+
							 (splitString[i].length() > 1 ? splitString[i].substring(1).toLowerCase() : "");
					neuString = neuString + (i < (splitString.length-1) ? " / " : "");
				}
				test = String.valueOf(neuString.trim());
				//System.out.println(test);
			}
		}
		if(test.indexOf("prof.") > -1){
			neuString = test.replaceAll("prof.", "Prof.");
			test = String.valueOf(neuString.trim());
		}
		if(test.indexOf("dr.") > -1){
			neuString = test.replaceAll("dr.", "Dr.");
			test = String.valueOf(neuString.trim());
		}
		if(test.indexOf("dres.") > -1){
			neuString = test.replaceAll("dres.", "Dres.");
			test = String.valueOf(neuString.trim());
		}
		if(test.indexOf(" Med. ") > -1){
			neuString = test.replaceAll(" Med. ", " med. ");
			test = String.valueOf(neuString.trim());
		}
		if(test.indexOf("Med.") > -1){
			neuString = test.replaceAll("Med.", "med.");
			test = String.valueOf(neuString.trim());
		}
		if(test.indexOf(" Von ") > -1){
			neuString = test.replaceAll(" Von ", " von ");
			test = String.valueOf(neuString.trim());
		}
		if(test.indexOf("Von ") > -1){
			neuString = test.replaceAll("Von ", " von ");
			test = String.valueOf(neuString.trim());
		}
		if(test.indexOf(" Und ") > -1){
			neuString = test.replaceAll(" Und ", " und ");
			test = String.valueOf(neuString.trim());
		}
		if(test.indexOf(" Zu ") > -1){
			neuString = test.replaceAll(" Zu ", " zu ");
			test = String.valueOf(neuString.trim());
		}
		if(test.indexOf(" VON ") > -1){
			neuString = test.replaceAll(" VON ", " von ");
			test = String.valueOf(neuString.trim());
		}
		
		if(test.indexOf(" UND ") > -1){
			neuString = test.replaceAll(" UND ", " und ");
			test = String.valueOf(neuString.trim());
		}
		if(test.indexOf(" ZU ") > -1){
			neuString = test.replaceAll(" ZU ", " zu ");
			test = String.valueOf(neuString.trim());
		}

		if(test.indexOf(" An ") > -1){
			neuString = test.replaceAll(" An ", " an ");
			test = String.valueOf(neuString.trim());
		}
		if(test.indexOf(" Am ") > -1){
			neuString = test.replaceAll(" Am ", " am ");
			test = String.valueOf(neuString.trim());
		}
		if(test.indexOf(" Auf ") > -1){
			neuString = test.replaceAll(" Auf ", " auf ");
			test = String.valueOf(neuString.trim());
		}
		if(test.indexOf(" Der ") > -1){
			neuString = test.replaceAll(" Der ", " der ");
			test = String.valueOf(neuString.trim());
		}
		if(test.indexOf(" Bei ") > -1){
			neuString = test.replaceAll(" Bei ", " bei ");
			test = String.valueOf(neuString.trim());
		}
		if(test.indexOf(" Beim ") > -1){
			neuString = test.replaceAll(" Beim ", " bei ");
			test = String.valueOf(neuString.trim());
		}
		if(test.indexOf(" Den ") > -1){
			neuString = test.replaceAll(" Den ", " den ");
			test = String.valueOf(neuString.trim());
		}
		if(test.indexOf(" Dem ") > -1){
			neuString = test.replaceAll(" Dem ", " dem ");
			test = String.valueOf(neuString.trim());
		}
		if(test.indexOf(" Die ") > -1){
			neuString = test.replaceAll(" Die ", " die ");
			test = neuString;
		}
		if(test.indexOf("-Die ") > -1){
			neuString = test.replaceAll("-Die ", "-die ");
			test = neuString;
		}
		if(test.indexOf(" Ob ") > -1){
			neuString = test.replaceAll(" Ob ", " ob ");
			test = neuString;
		}
		if(test.indexOf(" Über ") > -1){
			neuString = test.replaceAll(" Über ", " über ");
			test = neuString;
		}
		if(test.indexOf(" Überm ") > -1){
			neuString = test.replaceAll(" Überm ", " überm ");
			test = neuString;
		}
		if(test.indexOf(" Unter ") > -1){
			neuString = test.replaceAll(" Unter ", " unter ");
			test = neuString;
		}
		if(test.indexOf(" A.d. ") > -1){
			neuString = test.replaceAll(" A.d. ", " a.d. ");
			test = neuString;
		}
		if(test.indexOf(" U.d. ") > -1){
			neuString = test.replaceAll(" U.d. ", " u.d. ");
			test = neuString;
		}
		if(test.indexOf("Aok") > -1){
			neuString = test.replaceAll("Aok", "AOK");
			test = neuString;
		}
		if(test.indexOf("Gek") > -1){
			neuString = test.replaceAll("Gek", "GEK");
			test = neuString;
		}
		if(test.indexOf("Bkk") > -1){
			neuString = test.replaceAll("Bkk", "BKK");
			test = neuString;
		}
		if(test.indexOf("Bek ") > -1){
			neuString = test.replaceAll("Bek ", "BEK ");
			test = neuString;
		}
		if(test.indexOf("Ikk") > -1){
			neuString = test.replaceAll("Ikk", "IKK");
			test = neuString;
		}
		if(test.indexOf("Lkk") > -1){
			neuString = test.replaceAll("Lkk", "LKK");
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
		if(test.indexOf("Ddg") > -1){
			neuString = test.replaceAll("Ddg", "DDG");
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
			////System.out.println(ex);
			return ""+test;
		}
		
		return test;
		//return neuString.trim();
	}
	
	public static String EGross2(String test){
		String retString = "";
		return retString;
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
				zahl = Integer.valueOf(string.trim());
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
	public static String getDisziplin(String reznr){
		if(reznr.startsWith("KG")){
			return "Physio";
		}else if(reznr.startsWith("MA")){
			return "Massage";
		}else if(reznr.startsWith("ER")){
			return "Ergo";
		}else if(reznr.startsWith("LO")){
			return "Logo";
		}else if(reznr.startsWith("RH")){
			return "Reha";
		}
		return "Physio";
	}
}
