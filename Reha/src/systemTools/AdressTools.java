package systemTools;

public class AdressTools {
	public static String[] machePrivatAdresse(Object[] oin){
		//"anrede,titel,nachname,vorname,strasse,plz,ort"
		String[] str = {null,null,null,null,null,null,null};
		String anrede =  ( ((String)oin[0]) == null ? "" : ((String)oin[0]) );
		String titel = ( ((String)oin[1]) == null ? "" : ((String)oin[1]) );
		String nname = ( ((String)oin[2]) == null ? "" : ((String)oin[2]) );
		String vname = ( ((String)oin[3]) == null ? "" : ((String)oin[3]) );
		String strasse = ( ((String)oin[4]) == null ? "" : ((String)oin[4]) );
		String plz = ( ((String)oin[5]) == null ? "" : ((String)oin[5]) );
		String ort = ( ((String)oin[6]) == null ? "" : ((String)oin[6]) );
		
		String banrede = ""; 
		boolean isherr = false;
		boolean isnosex = false;
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
		str[1] = new String((titel.length() > 0 ? " "+StringTools.EGross(titel) : "")+" "+
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
			str[4] = "Sehr geehrter Herr"+(anredetitel.length() > 0 ? " "+anredetitel : "")+" "+nname;
		}else if((!isnosex) && (!isherr)){
			str[4] = "Sehr geehrte Frau"+(anredetitel.length() > 0 ? " "+anredetitel : "")+" "+nname;
		}
		return str;
	}
	
	public static String[] macheFirmenAdresse(){
		String[] str = null;
		return str;
	}

}
