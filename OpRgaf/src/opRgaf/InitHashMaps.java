package opRgaf;



import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

import Tools.DatFunk;
import Tools.StringTools;


public class InitHashMaps {
	public static HashMap<String,String> hmAdrRDaten = null;
	public static HashMap<String,String> hmAdrPDaten = null;
	public static HashMap<String,String> hmAdrAFRDaten = null;

	
	/********************/
	public InitHashMaps(){
		hmAdrAFRDaten = new HashMap<String,String>();
		List<String> lAdrAFRDaten = Arrays.asList(new String[]{"<AFRposition1>","<AFRposition2>","<AFRposition3>"
				,"<AFRposition4>","<AFRpreis1>","<AFRpreis2>","<AFRpreis3>","<AFRpreis4>","<AFRgesamt>","<AFRnummer>",
				"<AFRkurz1>","<AFRkurz2>","<AFRkurz3>","<AFRkurz4>","<AFRlang1>","<AFRlang2>","<AFRlang3>","<AFRlang4>","<AFRdatum>"});
		for(int i = 0; i < lAdrAFRDaten.size(); i++){
			hmAdrAFRDaten.put(lAdrAFRDaten.get(i),"");
		}
		/********************/
		
		/********************/		
		hmAdrRDaten = new HashMap<String,String>();
		List<String> lAdrRDaten = Arrays.asList(new String[]{"<Rpatid>","<Rnummer>","<Rdatum>","<Rposition1>","<Rposition2>","<Rposition3>"
				,"<Rposition4>","<Rpreis1>","<Rpreis2>","<Rpreis3>","<Rpreis4>","<Rproz1>","<Rproz2>","<Rproz3>"
				,"<Rproz4>","<Rgesamt1>","<Rgesamt2>","<Rgesamt3>","<Rgesamt4>","<Rpauschale>","<Rendbetrag>","<Ranzahl1>"
				,"<Ranzahl2>","<Ranzahl3>","<Ranzahl4>","<Rerstdat>","<Rletztdat>","<Rid>","<Rtage>","<Rkurz1>","<Rkurz2"
				,"<Rkurz3>","<Rkurz4>","<Rlang1>","<Rlang2>","<Rlang3>","<Rlang4>","<Rbarcode>","<Systemik>","<Rwert>"
				,"<Rhbpos>","<Rwegpos>","<Rhbpreis>","<Rwegpreis>","<Rhbproz>","<Rwegproz>","<Rhbanzahl>","<Rweganzahl>"
				,"<Rhbgesamt>","<Rweggesamt>","<Rwegkm>","<Rtage>"});
		for(int i = 0; i < lAdrRDaten.size(); i++){
			hmAdrRDaten.put(lAdrRDaten.get(i),"");
		}
		/********************/
		
		/********************/		
		hmAdrPDaten = new HashMap<String,String>();
		List<String> lAdrPDaten = Arrays.asList(new String[]{"<Padr1>","<Padr2>","<Padr3>","<Padr4>","<Padr5>",
											"<Pgeboren>","<Panrede>","<Pnname>","<Pvname>","<Pbanrede>",
											"<Ptelp>","<Ptelg>","<Ptelmob>","<Pfax>","<Pemail>","<Ptitel>","<Pihrem>","<Pihnen>","<Pid>","<Palter>","<Pzigsten>"});
		for(int i = 0; i < lAdrPDaten.size(); i++){
			hmAdrPDaten.put(lAdrPDaten.get(i),"");
		}


	}
	
	public static void constructPatHMap(Vector<String> patDaten){
		boolean isherr = false;
		boolean iskind = false;
		try{
			//int lang = hmAdrPDaten.hashCode();
			////System.out.println(lang);
			//hmAdrPDaten.put("<Padr1>", patDaten.get(0));
			String anrede = StringTools.EGross(patDaten.get(0));
			if(anrede.toUpperCase().equals("HERR")){
				isherr = true;
			}
			String titel =  StringTools.EGross(patDaten.get(1));
			String vorname =  StringTools.EGross(patDaten.get(3));
			String nachname = StringTools.EGross(StringTools.EscapedDouble(patDaten.get(2)));
			
			if(nachname.trim().equals("") && vorname.trim().equals("")){
				JOptionPane.showMessageDialog(null, "Ausgewählter Patient hat weder Vor- noch Nachname!!!\n+Zifix 'luja");
				return;
			}
			
			//String nachname =  StringTools.EGross(patDaten.get(2));
			String strasse = StringTools.EGross(patDaten.get(21));
			String plzort = patDaten.get(23)+" "+StringTools.EGross(patDaten.get(24));
			String geboren = DatFunk.sDatInDeutsch(patDaten.get(4));
			String zeile1 = "";
			String zeile2 = "";
			String zeile3 = "";
			String branrede = "";
			int jahrheute = Integer.valueOf(DatFunk.sHeute().substring(6));
			int jahrgeboren = 0;
			try{
				jahrgeboren = Integer.valueOf(geboren.substring(6));
			}catch(Exception ex){
				JOptionPane.showMessageDialog(null, "Irgend eine Arschgeige hat einmal mehr kein Geburtstag eingegeben");
			}
			int ialter = jahrheute - jahrgeboren;
			
			
			if( ialter <= 13){
				iskind = true;
			}
			hmAdrPDaten.put("<Palter>", Integer.toString(ialter));
			if(ialter >= 20){
				hmAdrPDaten.put("<Pzigsten>", Integer.toString(ialter)+"-sten");	
			}else{
				hmAdrPDaten.put("<Pzigsten>", Integer.toString(ialter)+"-ten");
			}

			zeile1 = (titel.length() > 0 ? titel+" " : "")+vorname+" "+nachname;
			zeile2 = strasse;
			zeile3 = plzort;
			if(titel.indexOf("med.") > 0){
					titel = titel.replace("med.", "");				
			}
			if(isherr){
				if(!iskind){
					branrede = "Sehr geehrter Herr"+(titel.length() > 0 ? " "+titel : "")+" "+nachname;
					hmAdrPDaten.put("<Panrede>", anrede);
					hmAdrPDaten.put("<Pihnen>", "Ihnen");
					hmAdrPDaten.put("<Pihrem>", "Ihrem");
				}else{
					branrede = "Lieber "+vorname;
					hmAdrPDaten.put("<Panrede>", "");				
					hmAdrPDaten.put("<Pihnen>", "Dir");
					hmAdrPDaten.put("<Pihrem>", "Deinem");
				}

			}else{
				if(!iskind){
					branrede = "Sehr geehrte Frau"+(titel.length() > 0 ? " "+titel : "")+" "+nachname;
					hmAdrPDaten.put("<Panrede>", anrede);
					hmAdrPDaten.put("<Pihnen>", "Ihnen");
					hmAdrPDaten.put("<Pihrem>", "Ihrem");
				}else{
					branrede = "Liebe "+vorname;
					hmAdrPDaten.put("<Panrede>", "");
					hmAdrPDaten.put("<Pihnen>", "Dir");
					hmAdrPDaten.put("<Pihrem>", "Deinem");
				}
			}
					
			hmAdrPDaten.put("<Padr1>", zeile1);
			hmAdrPDaten.put("<Padr2>", zeile2);
			hmAdrPDaten.put("<Padr3>", zeile3);
			hmAdrPDaten.put("<Pbanrede>", branrede);
			hmAdrPDaten.put("<Pgeboren>", geboren);
			hmAdrPDaten.put("<Pnname>", nachname);
			hmAdrPDaten.put("<Pvname>", vorname);		
					
			hmAdrPDaten.put("<Ptelp>", patDaten.get(18));
			hmAdrPDaten.put("<Ptelg>", patDaten.get(19));
			hmAdrPDaten.put("<Ptelmob>", patDaten.get(20));
			//hmAdrPDaten.put("<Pfax>", patDaten.get(21));
			hmAdrPDaten.put("<Pemail>", patDaten.get(50));
			hmAdrPDaten.put("<Ptitel>", titel);
			hmAdrPDaten.put("<Pid>", patDaten.get(66));
		}catch(Exception ex){
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null,"Fehler beim zusammenstellen der Patienten HashMap");
		}
	}
	

}
