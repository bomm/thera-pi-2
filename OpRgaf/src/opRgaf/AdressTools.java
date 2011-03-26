package opRgaf;

import Tools.StringTools;

public class AdressTools {
	public static String[] machePrivatAdresse(Object[] oin,boolean egross){
		//"anrede,titel,nachname,vorname,strasse,plz,ort"
		String[] str = {null,null,null,null,null,null,null};
		String anrede =  ( ((String)oin[0]) == null ? "" : ((String)oin[0]) ).trim();
		String titel = ( ((String)oin[1]) == null ? "" : ((String)oin[1]) ).trim();
		String nname = ( ((String)oin[2]) == null ? "" : ((String)oin[2]) ).trim();
		String vname = ( ((String)oin[3]) == null ? "" : ((String)oin[3]) ).trim();
		String strasse = ( ((String)oin[4]) == null ? "" : ((String)oin[4]) ).trim();
		String plz = ( ((String)oin[5]) == null ? "" : ((String)oin[5]) ).trim();
		String ort = ( ((String)oin[6]) == null ? "" : ((String)oin[6]) ).trim();
		
		//String banrede = ""; 
		boolean isherr = false;
		boolean isnosex = false;
		if(egross){
			if(((String)oin[0]).toUpperCase().equals("HERR")){
				anrede = StringTools.EGross(anrede);
				isherr = true;
			}else if(((String)oin[0]).toUpperCase().equals("FRAU")){
				anrede = StringTools.EGross(anrede);
				isherr = false;
			}else{
				isnosex = true; 
			}
			str[0] = anrede;
			str[1] = String.valueOf((titel.length() > 0 ? " "+StringTools.EGross(titel) : "")+" "+
					StringTools.EGross(vname)+" "+
					StringTools.EGross(nname) ).trim();
			str[2] = StringTools.EGross(strasse);
			str[3] = (plz+" "+StringTools.EGross(ort)).trim();
			
			String anredetitel = "";
			if(titel.indexOf("med.") > 0){
				anredetitel = titel.replace("med.", "");				
			}else{
				anredetitel = titel;
			}
			
			if(isnosex){
				str[4] = "Sehr geehrte Damen und Herren";
			}else if((!isnosex) && (isherr)){
				str[4] = "Sehr geehrter Herr"+(anredetitel.trim().length() > 0 ? " "+anredetitel.trim() : "")+" "+StringTools.EGross(nname).trim();
			}else if((!isnosex) && (!isherr)){
				str[4] = "Sehr geehrte Frau"+(anredetitel.trim().length() > 0 ? " "+anredetitel.trim() : "")+" "+StringTools.EGross(nname).trim();
			}
			return str;
		}else{
			if(((String)oin[0]).toUpperCase().equals("HERR")){
				anrede = "Herr";
				isherr = true;
			}else if(((String)oin[0]).toUpperCase().equals("FRAU")){
				anrede = "Frau";
				isherr = false;
			}else{
				isnosex = true; 
			}
			str[0] = anrede;
			str[1] =String.valueOf((titel.length() > 0 ? " "+titel : "")+" "+
					vname+" "+
					nname ).trim();
			str[2] = strasse;
			str[3] = (plz+" "+ort).trim();
			
			String anredetitel = "";
			if(titel.indexOf("med.") > 0){
				anredetitel = titel.replace("med.", "");				
			}else{
				anredetitel = titel;
			}
			
			if(isnosex){
				str[4] = "Sehr geehrte Damen und Herren";
			}else if((!isnosex) && (isherr)){
				str[4] = "Sehr geehrter Herr"+(anredetitel.trim().length() > 0 ? " "+anredetitel.trim() : "")+" "+nname.trim();
			}else if((!isnosex) && (!isherr)){
				str[4] = "Sehr geehrte Frau"+(anredetitel.trim().length() > 0 ? " "+anredetitel.trim() : "")+" "+nname.trim();
			}
			return str;
		}
	}
	

	public static String[] macheFirmenAdresse(){
		String[] str = null;
		return str;
	}

}
