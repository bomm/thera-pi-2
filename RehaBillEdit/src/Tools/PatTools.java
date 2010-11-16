package Tools;

import java.util.Vector;

import javax.swing.JOptionPane;

import rehaBillEdit.RehaBillEdit;


public class PatTools {
	
	/*
		lAdrPDaten = Arrays.asList(new String[]{
		"<Padr1>",
		"<Padr2>",
		"<Padr3>",
		"<Padr4>",
		"<Padr5>",
		"<Pgeboren>",
		"<Panrede>",
		"<Pnname>",
		"<Pvname>",
		"<Pbanrede>",
		"<Ptelp>",
		"<Ptelg>",
		"<Ptelmob>",
		"<Pfax>",
		"<Pemail>",
		"<Pid>"});
		"<Palter>",
		"<Pzigsten>"}
		hmAdrPDaten.put(lAdrPDaten.get(i),"");
 
	 * 
	 */
	public static void constructPatHMap(Vector<String> patDaten){
		boolean isherr = false;
		boolean iskind = false;
		try{
			RehaBillEdit.hmAdrPDaten.clear();
			//int lang = SystemConfig.hmAdrPDaten.hashCode();
			////System.out.println(lang);
			//SystemConfig.hmAdrPDaten.put("<Padr1>", patDaten.get(0));
			String anrede = Tools.StringTools.EGross(patDaten.get(0));
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
			int jahrgeboren = Integer.valueOf(geboren.substring(6));
			int ialter = jahrheute - jahrgeboren;
			
			
			if( ialter <= 13){
				iskind = true;
			}
			RehaBillEdit.hmAdrPDaten.put("<Palter>", Integer.toString(ialter));
			if(ialter >= 20){
				RehaBillEdit.hmAdrPDaten.put("<Pzigsten>", Integer.toString(ialter)+"-sten");	
			}else{
				RehaBillEdit.hmAdrPDaten.put("<Pzigsten>", Integer.toString(ialter)+"-ten");
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
					RehaBillEdit.hmAdrPDaten.put("<Panrede>", anrede);
					RehaBillEdit.hmAdrPDaten.put("<Pihnen>", "Ihnen");
					RehaBillEdit.hmAdrPDaten.put("<Pihrem>", "Ihrem");
				}else{
					branrede = "Lieber "+vorname;
					RehaBillEdit.hmAdrPDaten.put("<Panrede>", "");				
					RehaBillEdit.hmAdrPDaten.put("<Pihnen>", "Dir");
					RehaBillEdit.hmAdrPDaten.put("<Pihrem>", "Deinem");
				}

			}else{
				if(!iskind){
					branrede = "Sehr geehrte Frau"+(titel.length() > 0 ? " "+titel : "")+" "+nachname;
					RehaBillEdit.hmAdrPDaten.put("<Panrede>", anrede);
					RehaBillEdit.hmAdrPDaten.put("<Pihnen>", "Ihnen");
					RehaBillEdit.hmAdrPDaten.put("<Pihrem>", "Ihrem");
				}else{
					branrede = "Liebe "+vorname;
					RehaBillEdit.hmAdrPDaten.put("<Panrede>", "");
					RehaBillEdit.hmAdrPDaten.put("<Pihnen>", "Dir");
					RehaBillEdit.hmAdrPDaten.put("<Pihrem>", "Deinem");
				}
			}
					
			RehaBillEdit.hmAdrPDaten.put("<Padr1>", zeile1);
			RehaBillEdit.hmAdrPDaten.put("<Padr2>", zeile2);
			RehaBillEdit.hmAdrPDaten.put("<Padr3>", zeile3);
			RehaBillEdit.hmAdrPDaten.put("<Pbanrede>", branrede);
			RehaBillEdit.hmAdrPDaten.put("<Pgeboren>", geboren);
			RehaBillEdit.hmAdrPDaten.put("<Pnname>", nachname);
			RehaBillEdit.hmAdrPDaten.put("<Pvname>", vorname);		
					
			RehaBillEdit.hmAdrPDaten.put("<Ptelp>", patDaten.get(18));
			RehaBillEdit.hmAdrPDaten.put("<Ptelg>", patDaten.get(19));
			RehaBillEdit.hmAdrPDaten.put("<Ptelmob>", patDaten.get(20));
			//RehaBillEdit.hmAdrPDaten.put("<Pfax>", patDaten.get(21));
			RehaBillEdit.hmAdrPDaten.put("<Pemail>", patDaten.get(50));
			RehaBillEdit.hmAdrPDaten.put("<Ptitel>", titel);
			RehaBillEdit.hmAdrPDaten.put("<Pid>", patDaten.get(66));
		}catch(Exception ex){
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null,"Fehler beim zusammenstellen der Patienten HashMap");
		}
	}
	public static String[] constructPatHMapFromStrings(String panrede,
												String ptitel,
												String pvorname,
												String pnachname,
												String pstrasse,
												String pplz,
												String port){
		
		String[] adresse = {null,null,null,null,null};	
		try{
			boolean isherr = false;
			String anrede = StringTools.EGross(panrede);
			if(anrede.toUpperCase().equals("HERR")){
				isherr = true;
			}
			String titel =  StringTools.EGross(ptitel);
			String vorname =  StringTools.EGross(pvorname);
			String nachname =  StringTools.EGross(pnachname);
			String strasse = StringTools.EGross(pstrasse);
			String plzort = pplz+" "+StringTools.EGross(port);
			String branrede = "";
			if(isherr){
					branrede = "Sehr geehrter Herr"+(titel.length() > 0 ? " "+titel : "")+" "+nachname;
					adresse[0] = anrede;
			}else{
					branrede = "Sehr geehrte Frau"+(titel.length() > 0 ? " "+titel : "")+" "+nachname;
					adresse[0] = anrede;
			}
			adresse[1]=vorname+(titel.length() > 0 ? " "+titel : "")+" "+nachname;
			adresse[2]=strasse;
			adresse[3]=plzort;
			adresse[4]=branrede;
		}catch(Exception ex){
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null,"Fehler beim zusammenstellen der Patienten HashMap (String[])");

		}


		return adresse;
	}
}
