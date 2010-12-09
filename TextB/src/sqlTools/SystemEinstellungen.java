package sqlTools;

import java.util.HashMap;
import java.util.Vector;

public class SystemEinstellungen {
	static String[] disziplinen = {"Physio","Massage","Ergo","Logo","Reha","Podo"};
	public static HashMap<String,Vector<String>> hmThema = new HashMap<String,Vector<String>>();
	public static HashMap<String,Vector<String>> hmOberbegriff = new HashMap<String,Vector<String>>();

	public static void ladeGelenke(String[] args){
	INIFile inif = null;
	if(args.length <= 0){
		inif = new INIFile("C:/RehaVerwaltung/ini/510841109/thbericht.ini");	
	}else{
		inif = new INIFile(args[1]);
	}
	 
	 if(inif == null){
		 System.out.println("inif = null");
	 }
	 Vector<String> vec = new Vector<String>();
	 try{
	 int anzahlthemen = -1;
	 for(int i = 0 ; i < disziplinen.length; i++){
		 vec.clear();
		 System.out.println(disziplinen[i]);
		 anzahlthemen = inif.getIntegerProperty("Textbausteine", "AnzahlThemen_"+disziplinen[i]);
		 System.out.println(anzahlthemen);
		 int anzahl = anzahlthemen; 
		 for(int y = 0; y < anzahl; y++){
			vec.add( inif.getStringProperty(disziplinen[i], "Thema"+Integer.toString(y+1))  ); 
		 }
		 System.out.println(vec);
		 hmThema.put(disziplinen[i],(Vector)vec.clone());
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
	 System.out.println(hmThema);
	 System.out.println(hmOberbegriff);
	 
	 }catch(Exception ex){
		 
	 }
	 
	}
}
