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
	public static int Rezept_editteil		= 11; //Reserve
	public static int Rezept_delete			= 12;
	public static int Rezept_gebuehren		= 13;
	public static int Rezept_ausfallrechnung= 14;
	public static int Rezept_lock			= 15;
	public static int Rezept_unlock			= 16;
	public static int Rezept_privatrechnung	= 17;
	public static int Rezept_thbericht		= 18;
	
	public static int Historie_gesamtumsatz	= 19;
	public static int Historie_tagedrucken	= 20;
	public static int Historie_thbericht	= 21;
	
	public static int Berichte_editvoll		= 22;
	public static int Berichte_delete		= 23;

	public static int Doku_open				= 24;
	public static int Doku_scannen			= 25;
	public static int Doku_delete			= 26;
	public static int Doku_ooorg			= 27;
	
	public static int Gutachten_anlegen		= 28;
	public static int Gutachten_editvoll	= 29;
	public static int Gutachten_delete		= 30;
	public static int Gutachten_copy		= 31;
	
	public static int Kalender_terminanlegenteil	= 32;
	public static int Kalender_terminanlegenvoll	= 33;
	public static int Kalender_termindelete			= 34;
	public static int Kalender_terminconfirm		= 35;
	public static int Kalender_termingroup			= 36;
	public static int Kalender_termindragdrop		= 37;
	
	public static int Masken_erstellen				= 38;
	public static int Masken_uebertragen			= 39;
	
	public static int Rugl_open						= 40;
	public static int Rugl_write					= 41;
	

	private static String[] rechteExt = {"Benutzerverwaltung öffnen","Benutzerrechte ändern","Super-User",
		
		"Neuen Patient anlegen","Patientendaten ändern","Patientendaten ändern","Patient löschen","Email an Patient versenden",
		"SMS an Patient versenden","Zusatzinformationen einsehen",
		
		"Rezepte neu anlegen","Rezepte ändern","Rezepte löschen","Rezeptgebühren kassieren","Ausfallrechnungen erstellen",
		"Rezepte abschließen","Rezepte aufschließen","Privat- und BGE-Rechnungen erstellen","Therapieberichte erstellen",
		
		"Gesamtumsatz des Patienten ermitteln","Tage eines Historienrezeptes drucken","Nachträglich Therapieberichte erstellen",
		
		"Therapieberichte ändern","Therapieberichte löschen",
		
		"Dokumentation öffnen","Dokumentation scannen","Dokumentation löschen","Grafik- und OOorg-Dokumente erstellen",
		
		"Gutachten anlegen","Gutachten ändern","Gutachten löschen","Stammdaten auf neues Gutachten übertragen",
		
		"Eintrag nur in freie Termine erlauben","Termine vollständig eintragen / ändern","Termine löschen","Behandlungen bestätigen","Termine gruppieren","Termin Drag & Drop",
		
		"Wochenarbeitszeit erstellen","Wochenarbeitszeit in Terminkalender übertragen",
		
		"[Ru:gl] öffnen","Termine mit [Ru:gl] überschreiben"
		
	};
}
