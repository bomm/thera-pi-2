package Tools;

import java.util.HashMap;
import java.util.Vector;

public class SystemEinstellungen {
	static String[] disziplinen = {"Physio","Massage","Ergotherapie","Logopädie","Reha"};
	public static HashMap<String,Vector<String>> hmGelenke = new HashMap<String,Vector<String>>();


	public static void ladeGelenke(){
	 INIFile inif = new INIFile("C:/RehaVerwaltung/ini/textbaustein.ini");
	 Vector<String> vec = new Vector<String>();
	 for(int i = 0 ; i < disziplinen.length; i++){
		 vec.clear();
		 int anzahl = inif.getIntegerProperty(disziplinen[i], "GelenkAnzahl"); 
		 for(int y = 0; y < anzahl; y++){
			vec.add( inif.getStringProperty(disziplinen[i], "Gelenk"+Integer.toString(y+1))  ); 
		 }
		 System.out.println(vec);
		 hmGelenke.put(disziplinen[i],(Vector)vec.clone());
	 }
	 
	}
}
