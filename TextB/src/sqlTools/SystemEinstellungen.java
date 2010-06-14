package sqlTools;

import java.util.HashMap;
import java.util.Vector;

public class SystemEinstellungen {
	static String[] disziplinen = {"Physio","Massage","Ergotherapie","Logopädie","Reha"};
	public static HashMap<String,Vector<String>> hmGelenke = new HashMap<String,Vector<String>>();
	public static HashMap<String,Vector<String>> hmOberbegriff = new HashMap<String,Vector<String>>();

	public static void ladeGelenke(String[] args){
	INIFile inif = null;
	if(args.length <= 0){
		inif = new INIFile("C:/RehaVerwaltung/ini/textbaustein.ini");	
	}else{
		inif = new INIFile(args[1]);
	}
	 
	 if(inif == null){
		 System.out.println("inif = null");
	 }
	 Vector<String> vec = new Vector<String>();
	 try{
	 
	 for(int i = 0 ; i < disziplinen.length; i++){
		 vec.clear();
		 System.out.println(disziplinen[i]);
		 System.out.println(inif.getIntegerProperty(disziplinen[i], "GelenkAnzahl"));
		 int anzahl = inif.getIntegerProperty(disziplinen[i], "GelenkAnzahl"); 
		 for(int y = 0; y < anzahl; y++){
			vec.add( inif.getStringProperty(disziplinen[i], "Gelenk"+Integer.toString(y+1))  ); 
		 }
		 System.out.println(vec);
		 hmGelenke.put(disziplinen[i],(Vector)vec.clone());
	 }
	 for(int i = 0 ; i < disziplinen.length; i++){
		 vec.clear();
		 int anzahl = inif.getIntegerProperty(disziplinen[i], "OberbegriffAnzahl"); 
		 for(int y = 0; y < anzahl; y++){
			vec.add( inif.getStringProperty(disziplinen[i], "Oberbegriff"+Integer.toString(y+1))  ); 
		 }
		 System.out.println(vec);
		 hmOberbegriff.put(disziplinen[i],(Vector)vec.clone());
	 }
	 
	 }catch(Exception ex){
		 
	 }
	 
	}
}
