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
	public static boolean testeRecht(String rechte,int recht){
		if(rechte.substring(recht,recht+1).equals("1")){
			return true;
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
	public static int Kalender_unbelegt1			= 38;
	public static int Kalender_unbelegt2			= 39;
	
	public static int Masken_erstellen				= 40;
	public static int Masken_uebertragen			= 41;
	
	public static int Rugl_open						= 42;
	public static int Rugl_write					= 43;
	public static int Rugl_unbelegt1				= 44;
	public static int Rugl_unbelegt2				= 45;
	
	public static int Funktion_kassenabrechnung		= 46;
	public static int Funktion_rehaabrechnung		= 47;
	public static int Funktion_barkasse				= 48;
	public static int Funktion_neuanmeldungen		= 49;
	public static int Funktion_umsatzvonbis			= 50;
	public static int Funktion_mitarbeiterbeteiligung	= 51;
	public static int Funktion_urlaubueberstunden	= 52;
	public static int Funktion_offeneposten				= 53;
	public static int Funktion_kassenbuch				= 54;
	public static int Funktion_unbelegt3				= 55;
	public static int Funktion_unbelegt4				= 56;
	public static int Funktion_unbelegt5				= 57;
	public static int Funktion_unbelegt6				= 58;
	public static int Funktion_unbelegt7				= 59;
	public static int Funktion_unbelegt8				= 60;
	public static int Funktion_unbelegt9				= 61;
	public static int Funktion_unbelegt10				= 62;
	
	public static int Systeminit_mandanten				= 63;
	public static int Systeminit_datenbank				= 64;
	public static int Systeminit_kalendergrundeinstellung	= 65;
	public static int Systeminit_kalenderbenutzer		= 66;
	public static int Systeminit_kalenderbenutzersets	= 67;
	public static int Systeminit_terminlistedruck		= 68;
	public static int Systeminit_kalenderfarbsets		= 69;
	public static int Systeminit_kalendergruppentermine	= 70;
	public static int Systeminit_kalenderjahranlegen	= 71;
	public static int Systeminit_ruglgrundeinstellung	= 72;
	public static int Systeminit_ruglgruppen			= 73;
	public static int Systeminit_fensterpatient			= 74;
	public static int Systeminit_fensterrezept			= 75;
	public static int Systeminit_fensterkasse			= 76;
	public static int Systeminit_fensterarzt			= 77;
	public static int Systeminit_eimailadressen			= 78;
	public static int Systeminit_schnittstellen			= 79;
	public static int Systeminit_geraete				= 80;
	public static int Systeminit_behandlungskuerzel		= 81;
	public static int Systeminit_tarifgruppen			= 82;
	public static int Systeminit_preiseimportieren		= 83;
	public static int Systeminit_nummernkreis			= 84;
	public static int Systeminit_nebraska				= 85;
	public static int Systeminit_abrechnungformulare	= 86;
	public static int Systeminit_kostentraegerdatei		= 87;
	public static int Systeminit_fremdprogramme			= 88;
	public static int Systeminit_unbelegt1				= 89;
	public static int Systeminit_unbelegt2				= 90;
	public static int Systeminit_unbelegt3				= 91;
	public static int Systeminit_unbelegt4				= 92;
	public static int Systeminit_unbelegt5				= 93;
	
	public static int Sonstiges_verkaufsmodul			= 94;
	public static int Sonstiges_rehaformulare			= 95;
	public static int Sonstiges_textbausteinegutachten	= 96;
	public static int Sonstiges_rezepttransfer			= 97;
	public static int Sonstiges_rezeptbehandlungsartloeschen = 98;
	public static int Sonstiges_geburtstagsbriefe		= 99;
	public static int Sonstiges_sqlmodul				= 100;
	public static int Sonstiges_unbelegt5				= 101;
	public static int Sonstiges_unbelegt6				= 102;
	public static int Sonstiges_unbelegt7				= 103;
	public static int Sonstiges_unbelegt8				= 104;
	public static int Sonstiges_unbelegt9				= 105;
	public static int Sonstiges_unbelegt10				= 106;
	
	
	
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
		"unbelegt-1","unbelegt-2",
		
		"Wochenarbeitszeit erstellen","Wochenarbeitszeit in Terminkalender übertragen",
		
		"[Ru:gl] öffnen","Termine mit [Ru:gl] überschreiben","unbelegt-1","unbelegt-2",
		
		"Kassenabrechnung starten","Rehaabrechnung starten","Barkassen Abrechnung starten",
		"Neuanmeldungen ermitteln","Umsätze von bis ermitteln","Mitarbeiterbeteiligung ermitteln",
		"Urlaub-/Überstunden ermitteln","Offene-Posten starten","Kassenbuch anlegen/ändern","unbelegt-3","unbelegt-4","unbelegt-5",
		"unbelegt-6","unbelegt-7","unbelegt-8","unbelegt-9","unbelegt-10",
		
		"Firmenangaben Mandanten","Datenbankparameter","Kalender Grundeinstellungen","Kalender-Benuter verwalten",
		"Behandlersets einstellen","Druckvorlage für Terminliste","Kalender-Farbdefinition","Gruppentermine verwalten",
		"Neues Kalenderjahr anlegen","[Ru:gl]-Grundeinstellungen","[Ru:gl]-Gruppen definieren","Einstellungen Patientenfenster",
		"Einstellungen Rezepte","Einstellungen Krankenfenster","Einstellungen Arztfenster","Emailparameter ändern","Geräteanschlüsse (Schnittstellen)",
		"angeschlossene Geräte","Behandlunskürzel verwalten","Tarifgruppen bearbeiten","Preise bearbeiten/importieren",
		"Nummernkreise verwalten","Nebraska benützen","Abrechnungsformulare und Druckparameter","Kostenträgerdatei einlesen","Fremdprogramme verwalten",
		"unbelegt 1 für zukünftige Erweiterungen","unbelegt 2 für zukünftige Erweiterungen",
		"unbelegt 3 für zukünftige Erweiterungen","unbelegt 4 für zukünftige Erweiterungen","unbelegt 5 für zukünftige Erweiterungen",
		
		"Verkaufsmodul benutzen","Rehaformulare verwenden","Textbausteine für Gutachten anlegen/ändern","Rezepte transferieren","im Rezept gespeicherte Behandlungsarten löschen",
		"Geburtstagsbriefe erstellen","Sql-Modul verwenden","unbelegt-5",
		"unbelegt-6","unbelegt-7","unbelegt-8","unbelegt-9","unbelegt-10"
		
	};
}
