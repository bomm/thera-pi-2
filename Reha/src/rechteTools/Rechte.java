package rechteTools;

import javax.swing.JOptionPane;

import hauptFenster.Reha;

public class Rechte {
	public static boolean hatRecht(int recht,boolean dialogzeigen){
		if(Reha.progRechte.substring(recht,recht+1).equals("1") ||
				Reha.progRechte.substring(BenutzerSuper_user,BenutzerSuper_user+1).equals("1")){
			return true;
		}
		if(dialogzeigen){
			try{
				JOptionPane.showMessageDialog(null,"Keine Berechtigung zum Aufruf dieser Funktion\n\n"+
						"Funktion --> "+rechteExt[recht]+"\n");
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return false;
	}
	
	public static int BenutzerDialog_open 	= 0;
	public static int BenutzerRechte_set 	= 1;
	public static int BenutzerSuper_user 	= 2;

	public static int Patient_anlegen 		= 3;
	public static int Patient_editvoll 		= 4;
	public static int Patient_editteil 		= 5;
	public static int Patient_delete 		= 6;
	public static int Patient_email 		= 7;
	public static int Patient_sms 			= 8;
	public static int Patient_zusatzinfo	= 9;
	
	public static int Rezept_anlegen		= 10;
	public static int Rezept_editvoll		= 11;
	public static int Rezept_editteil		= 12; //Reserve
	public static int Rezept_delete			= 13;
	public static int Rezept_gebuehren		= 14;
	public static int Rezept_ausfallrechnung= 15;
	public static int Rezept_lock			= 16;
	public static int Rezept_unlock			= 17;
	public static int Rezept_privatrechnung	= 18;
	public static int Rezept_thbericht		= 19;
	
	private static String[] rechteExt = {"Benutzerverwaltung öffnen","Benutzerrechte ändern","Super-User",
		"Neuen Patient anlegen","Patientendaten ändern","Patientendaten ändern","Patient löschen","Email an Patient versenden",
		"SMS an Patient versenden","Zusatzinformationen einsehen"
		
	};
}
