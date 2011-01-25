package dta301;

import java.util.Arrays;
import java.util.List;

import systemTools.StringTools;

public class Dta301CodeListen {
	
	//Arbeitsfähigkeit (nur RV)
	private static String[][] codeB01 = {
		{"0","Maßnahme nicht ordnungsgemäß abgeschlossen gestorben"},
		{"1","Arbeitsfähig"},
		{"3","Arbeitsunfähig"},
		{"4","Kinderreha"},
		{"5","Hausfrau/Hausmann"},
		{"9","Beurteilung nicht erforderlich (Altersrentner,Angehöriger)"}
	};
	//Arbeitsfähigkeit bei Entlassung
	private static String[][] codeB02 = {
		{"1","Arbeitsfähig entlassen"},
		{"2","Arbeitsunfähig entlassen"},
		{"3","Arbeitsfähig mit Schungszeit entlassen (BGE)"},
		{"9","Meldung nicht erforderlich (Altersrentner,Angehöriger)"}
	};
	//AU-Zeiten der letzten 12 Monate (nur RV)
	private static String[][] codeB03 = {
		{"0","Keine Arbeitsunfähigkeitszeiten währen der letzten 12 Monate"},		
		{"1","Bis unter 3 Monate arbeitsunfähig"},
		{"2","3 bis unter 6 Monate arbeitsunfähig"},
		{"3","6 und mehr Monate arbeitsunfähig"},
		{"9","Nicht erwererbstätig (Altersrentner,Vorruhestantsgeldempfänger)"}
	};
	//Art der Fahrtkostenerstattung
	private static String[][] codeB04 = {
		{"01","Keine Angabe"},
		{"02","Keine Auszahlung an Patienten durch Klinik"},
		{"03","Klinik zahlt ausgew. Betrag zzgl. genehm. Nebenkosten aus"},
		{"04","Klinik zahlt DB 1.Klasse zzgl. genehm. Nebenkosten aus"},
		{"05","Klinik zahlt DB 2.Klasse zzgl. genehm. Nebenkosten aus"},
		{"06","Klinik zahlt DB 1.Klasse Gruppentarif/Mitfahrerersp. zzgl. genehm. Nebenkosten aus"},
		{"07","Klinik zahlt DB 2.Klasse Gruppentarif/Mitfahrerersp. zzgl. genehm. Nebenkosten aus"},
		{"08","Klinik zahlt Taxikosten zzgl. genehm. Nebenkosten aus"},
		{"09","Klinik zahlt Kosten für Krankentransport aus"},
		{"10","Klinik zahlt Kosten für Flugticket aus"},
		{"11","Klinik zahlt Pkw-Kosten nach km aus"},
		{"12","Klinik zahlt Pkw-Kosten in Höhe des Bahntarifs aus"},
		{"13","Klinik zahlt Pkw-Kosten in Höhe des Großkunden-Bahntarifs aus"},
		{"98","Klinik zahlt nur genehm. Nebenkosten, da Fahrausweis vom Träger beschafft"},
		{"99","Klinik zahlt Fahrkosten gemäß besonderer Vereinbarung aus"}
	};
	//Funktion des Erläuterungstextes (nur RV)
	private static String[][] codeB05 = {
		{"1","Erläuterung für nachfolgende Maßnahmen (Blatt 1)"},
		{"2","Letzte Medikation (Blatt 1)"},
		{"3","Beschreibung des Leistungsbildes"},
		{"4","Erläuterung der Leistungsdaten"},
		{"5","Freier medizinischer Entlassungsbericht"}
	};
	//Entlassform (nur RV)
	private static String[][] codeB06 = {
		{"1","Regulär"},
		{"2","Vorzeitig auf ärztliche Veranlassung"},
		{"3","Vorzeitig mit ärztlichem Einverständnis"},
		{"4","Vorzeitig ohne ärztliches Einverständnis"},
		{"5","Disziplinarisch"},
		{"6","Verlegt"},
		{"7","Wechsel zu ambulanter,teilstationärer Reha"},		
		{"9","Gestorben"}
	};
	//Erläuterung zur Entlassung
	private static String[][] codeB07 = {
		{"01","Behandlung regulär beendet"},
		{"03","Behandlung aus sonstigen Gründen beendet"},
		{"04","Behandlung gegen ärztlichen Rat beendet"},
		{"05","Zuständigkeitswechsel des Leistungsträgers"},
		{"06","Verlegung"},
		{"07","Tod"},
		{"08","Behandlung vorzeitig auf ärztliche Veranlassung beendet"},		
		{"09","Behandlung vorzeitig mit ärztlichem Einverständnis beendet"},
		{"10","Behandlung aus disziplinarischen Gründen beendet"}
	};
	//Erläuterung zur Unterbrechung
	private static String[][] codeB08 = {
		{"01","Familienheimfahrt"},
		{"02","Todesfall in der Familie"},
		{"03","Interkurrente Erkrankung"},
		{"04","Stationäre Krankenhausbehandlung (nicht interkurrente Erkr."},
		{"09","Sonstiger Grund"}
	};
	//Indikationsgruppenzuordnung
	private static String[][] codeB09 = {
		{"01","Krankheiten des Herzens und des Kreislaufs"},
		{"02","Krankheiten der Gefäße"},
		{"03","Entzündliche rheumatische Erkrankungen"},
		{"04","Muskoloskeletale Erkrankungen"},
		{"05","Gastroenterologische Erkrankungen"},
		{"06","Stoffwechselerkrankungen"},
		{"07","Krankheiten der Atmungsorgane"},
		{"08","Krankheiten der Niere, Harnwege und Prostata"},
		{"09","Neurologische Erkrankungen"},
		{"10","Bösartige Geschwulsterkrankungen und maligne Systemerkrank."},
		{"11","Gynäkologische Erkrankungen"},
		{"12","Hauterkrankungen"},
		{"13","Psychosomatische psychovegetativeErkrankungen"},
		{"14","Psychische Erkrankungen"},
		{"15","Suchterkrankungen"},
		{"16","Krankheiten des Blutes und der Blutbildungsorgane"},
		{"17","Venenerkrankungen"},
		{"18","Unfall- und Verletzungsfolgen"},
		{"19","Geriatrie"},
		{"20","Störungen der Sinnesorgane"},
		{"21","Sostige"}
	};
	//Ursache der Erkrankung
	private static String[][] codeB10 = {
		{"0","1-5 triff nicht zu"},
		{"1","Arbeitsunfall einschließlich Wegeunfall"},
		{"2","Berufskrankheit"},
		{"3","Schädigungsfolge durch Einwirkung Dritter (Unfallfolge)"},
		{"4","Folge von Kriegs-,Zivil- oder Wehrdienst"},
		{"5","Meldepflichtige Erkrankung"}
	};
	//Art der Versorgung
	private static String[][] codeB11 = {
		{"1","stationär"},
		{"2","ganztägig ambulant"},
		{"3","ambulant (nur RV)"}
	};	
	//Erläuterung zur ambulanten Leistung (nur RV)
	private static String[][] codeB12 = {
		{"1","Meldung des 1.Behandlungstages bzw. 1.Abschnitt"},
		{"2","Meldung des 2 bis n-ten Beh.Tages bzw. Abschnitts"},
		{"3","Meldung des letzten Behandl.tages bzw. letzter Abschnitt"},
		{"4","Meldung des gesamten Behandlung"}
	};	
	//DMP-Patient (nur RV)
	private static String[][] codeB13 = {
		{"0","kein DMP-Patient"},
		{"1","Diabetes mellitus Typ 1"},
		{"2","Diabetes mellitus Typ 2"},
		{"3","Brustkrebs"},
		{"4","KHK"},
		{"5","Asthma bronchiale / COPD"},
		{"6","mehrere DMP's"},
		{"7","andere DMP's"},
	};	
	/***********
	 * 
	 * Es folgen die A-CodeListen
	 * 
	 */
	//Anreise
	private static String[][] codeA01 = {
		{"1","Anreise mit öffentliche Verkehrsmittel, Patient löst Fahrkarte selbst"},
		{"2","Anreise mit öffentliche Verkehrsmittel, Fahrkarte wird gestellt"},
		{"3","Anreise mit PKW"},
		{"4","Anreise mit Krankentrasport / Taxi"},
		{"5","Anreise mit Flugzeug"},
		{"6","Anreise mit Sammelanreise (Kinder)"}
	};	
	
	//Antragsart (nur RV)
	private static String[][] codeA02 = {
		{"11","Normale medizinische Reha einschl. RPK"},
		{"12","Entwöhnungsbehandlung (stationäre Suchtleistung)"},
		{"13","CA-Leistung für den Versicherten"},
		{"21","Medizinische Leistung für nicht versicherte Erwachsene"},
		{"22","Kinderheilbehandlung"},
		{"31","ambulante und/oder ganztägig ambulante medizinische Reha gem. § 15 SGB VI"},
		{"32","Sonstige Leistungen gemäß § 31 Abs. 1 Nr. 1 SGB VI"},
		{"33","Sonstige Leistungen gemäß § 31 Abs. 1 Nr. 2 SGB VI"},
		{"99","Auftragsheilbehandlung"},
	};	
	//Art der Suchterkrankung
	private static String[][] codeA03 = {
		{"1","Keine Suchterkrankung bekannt"},
		{"2","Alkoholabhängigkeit"},
		{"3","Medikamentenabhängigkeit"},
		{"4","Drogenabhängigkeit"},
		{"5","Mehrfachabhängigkeit"},
		{"6","Spielsucht"},		
		{"7","Eßstörungen"}
	};
	//Auftragsleistung
	private static String[][] codeA04 = {
		{"1","Keine Auftragsleistung"},
		{"2","Vollständige Fallabwicklung durch beauftragte Stelle"},
		{"3","Bewilligung durch beauftragte Stelle, weitere Abwicklung durch Klinik"}
	};
	//Begleitperson Hinreise
	private static String[][] codeA05 = {
		{"1","Anreise keine Begleitperson"},
		{"2","Anreise Begleitperson (Erwachsene)"},
		{"3","Anreise Begleitperson (Kind)"},
		{"4","Anreise Begleitperson Ehepartner/Lebenspartner"},
		{"9","Anreise mehr als eine Begleitperson (Familienkur)"}
	};
	//Begleitperson Rückreise
	private static String[][] codeA06 = {
		{"1","Rückreise keine Begleitperson"},
		{"2","Rückreise Begleitperson (Erwachsene)"},
		{"3","Rückreise Begleitperson (Kind)"},
		{"4","Rückreise Begleitperson Ehepartner/Lebenspartner"},
		{"9","Rückreise mehr als eine Begleitperson (Familienkur)"}
	};
	//Begleitperson Aufenthalt
	private static String[][] codeA07 = {
		{"1","Aufenthalt keine Begleitperson"},
		{"2","Aufenthalt Begleitperson (Erwachsene)"},
		{"3","Aufenthalt Begleitperson (Kind)"},
		{"4","Aufenthalt Begleitperson Ehepartner/Lebenspartner"},
		{"9","Aufenthalt mehr als eine Begleitperson (Familienkur)"}
	};
	//Behinderung
	private static String[][] codeA08 = {
		{"01","Keine Schwerbehinderung bekannt"},
		{"02","Geistige Behinderung"},
		{"03","Anfallskrankheit"},
		{"04","Blindheit"},
		{"05","Gehbehinderung"},
		{"06","Querschnittslähmung"},
		{"07","Rollstuhlfahrer/in"},
		{"08","Taubstummheit"},
		{"09","Mehrfachbehinderung"},
		{"10","Nicht näher sepzifiz. Schwerbehinderung"}
	};
	//Eilfall
	private static String[][] codeA09 = {
		{"J","JA"},
		{"N","NEIN"}
	};
	//Entgeldkategorie
	private static String[][] codeA10 = {
		{"1","Vergütung"},
		{"2","Zuschuß"},
		{"3","Fallpauschale"}
	};
	//Maßnahmeart
	private static String[][] codeA11 = {
		{"01","Noch keine Angaben möglich"},
		{"10","Normale medizinische Heilbehandlung"},
		{"21","Normale Leistung wegen psychischer Erkrankung"},
		{"22","Rehabilitation psychisch Kranker"},
		{"23","Entwöhnung wg. Alkoholabhängigkeit"},
		{"24","Entwöhnung wg. Medikamentenabhängigkeit"},
		{"25","Entwöhnung wg. Drogenabhängigkeit"},
		{"26","Entwöhnung wg. Mehrfachabhängigkeit"},
		{"31","CA-Rehaleistung nach § 15 SGB VI"},
		{"32","CA-Rehaleistung nach § 31 Abs.1 Nr.3 SGB VI"},
		{"42","Sonstige Leistung nach § 31 Abs.1 Nr.1 SGB VI"},
		{"43","Sonstige Leistung nach § 31 Abs.1 Nr.2 SGB VI"},
		{"65","Stufenweise Wiedereingliederung nach §28 SGB IX"},
		{"99","Auftragsheilbehandlung"}
	};
	//Verfahrensart Anspruchsgrundlage
	private static String[][] codeA12 = {
		{"71","Anschlußrehabilitation"},
		{"72","Wiederholungsheilbehandlung"},
		{"73","Vorleistung nach §6 Abs.2 RehaAnglG"},
		{"74","Rehabilitation in mehreren Behandlungsabschnitten"},
		{"75","Erstattungsfall"},
		{"76","Verfahren nach §51 SGB V, §105a AFG"},
		{"77","Reha-Leistung aus dem Rentenverfahren"},
		{"78","Reha-Leistung aus dem Rechtsbehelf"},
		{"79","Normales Rehaverfahren"},
		{"80","Rehabilitationssport nach §44 Abs.1 Nr.3 SGB IX"},
		{"81","Funktionstraining nach §44 Abs.1 Nr.4 SGB IX"},
		{"82","Nachsorgeleistung nach §15 SGB IV"},
		{"90","Geriatrische Reha-Maßnahme nach §40 SGB V"},
		{"91","Station. Vorsorgemaßnahme nach §23 Abs.4 SGB V"},
		{"92","Station. Rehamaßnahme nach §40 Abs.2 SGB V"},
		{"93","Anschlußheilbehandlung nach §40 Abs.2 SGB V"},
		{"94","Vorsorgekur für Mütter nach §24 SGB V"},
		{"95","Müttergenesungskur nach §41 SGB V"},
		{"96","Rehamaßnahme nach §40 Abs.2 SGB V"},
		{"97","Mutter/Kind Rehamaßnahme nach §41 SGB V"},
		{"98","Mutter/Kind Vorsorgekur nach §24 SGB V"}
	};
	//Zuzahlungseinzug
	private static String[][] codeA13 = {
		{"1","Keine Zuzahlung"},
		{"2","Zuzahlungsbetrag soll von der Klinik eingezogen werden"},
		{"3","Zuzahlungsbetrag soll von der Klinik nicht(!!) eingezogen werden"}
	};
	//Zwischenrechnung erlaubt
	private static String[][] codeA14 = {
		{"J","JA"},
		{"N","NEIN"}
	};
	/***********
	 * 
	 * Es folgen die C-CodeListen
	 * 
	 */	
	//Arbeitshaltung im Stehen (nur RV)
	private static String[][] codeC01 = {
		{"1","Keine Angaben"},
		{"2","Ständig"},
		{"3","Überwiegend"},
		{"4","Zeitweise"}
	};
	//Arbeitshaltung im Sitzen (nur RV)
	private static String[][] codeC02 = {
		{"1","Keine Angaben"},
		{"2","Ständig"},
		{"3","Überwiegend"},
		{"4","Zeitweise"}
	};
	//Arbeitshaltung im Gehen (nur RV)
	private static String[][] codeC03 = {
		{"1","Keine Angaben"},
		{"2","Ständig"},
		{"3","Überwiegend"},
		{"4","Zeitweise"}
	};
	//Arbeitsschwere (nur RV)
	private static String[][] codeC04 = {
		{"1","Keine Angaben"},
		{"2","Schwere Arbeiten"},
		{"3","Mittelschwere Arbeiten"},
		{"4","Leichte bis mittelschwere Arbeiten"},
		{"5","Leichte Arbeiten"}
	};
	//Leistungsfähigkeit im bisherigen Beruf (nur RV)
	private static String[][] codeC05 = {
		{"5","Mindestens 6 Stunden"},
		{"6","Mindestens 3 und unter 6 Stunden"},
		{"7","Weniger als 3 Stunden"},
		{"9","Keine Angaben erforderlich"}
	};
	//Leistungsfähigkeit auf dem allgemeinen Arbeitsmarkt (nur RV)
	private static String[][] codeC06 = {
		{"5","Mindestens 6 Stunden"},
		{"6","Mindestens 3 und unter 6 Stunden"},
		{"7","Weniger als 3 Stunden"},
		{"9","Keine Angaben erforderlich"}
	};
	//Arbeitsorganisation Tagesschicht (nur RV)
	private static String[][] codeC07 = {
		{"J","JA"},
		{"N","NEIN"}
	};
	//Arbeitsorganisation Früh-/Spätschicht (nur RV)
	private static String[][] codeC08 = {
		{"J","JA"},
		{"N","NEIN"}
	};
	//Arbeitsorganisation Nachtschicht (nur RV)
	private static String[][] codeC09 = {
		{"J","JA"},
		{"N","NEIN"}
	};
	//Keine wesentlichen Einschränkungen (nur RV)
	private static String[][] codeC10 = {
		{"J","JA"},
		{"N","NEIN"}
	};
	//Belastbarkeit (geistig/psychisch) (nur RV)
	private static String[][] codeC11 = {
		{"J","JA"},
		{"N","NEIN"}
	};
	//Sinnesorgane (nur RV)
	private static String[][] codeC12 = {
		{"J","JA"},
		{"N","NEIN"}
	};
	//Bewegungs-/Haltungsapparat (nur RV)
	private static String[][] codeC13 = {
		{"J","JA"},
		{"N","NEIN"}
	};
	//Gefährdungs-/Belastungsfaktoren (nur RV)
	private static String[][] codeC14 = {
		{"J","JA"},
		{"N","NEIN"}
	};
	private static List<String> fettDruck(){
		List<String> liste = Arrays.asList(new String[] {"Allgemeine und klinische",
				"Familienanamnese:","Eigenanamnese:",
				"Bisherige Reha-Aufenthalte",
				"Vegetative Anamnese",
				"Risikofaktoren",
				"Jetzige Beschwerden",
				StringTools.do301String("Spezielle männliche Anamnese"),
				"Spezielle weibliche",
				StringTools.do301String("Funktionelle Einschränkungen der Aktivitäten/Teilhabe"),
				StringTools.do301String("Krankheitsverständnis des"),
				StringTools.do301String("Krankheitsverständnis der"),
				StringTools.do301String("Gegenwärtige Therapie"),
				StringTools.do301String("Medikation:"),
				StringTools.do301String("Sonstige Maßnahmen:"),
				StringTools.do301String("Behandelnde Ärzte:"),
				StringTools.do301String("Allgemeine Sozialanamnese:"),
				StringTools.do301String("Arbeits- und Berufsanamnese:"),
				StringTools.do301String("Aufnahmebefund, Vorbefunde,"),
				StringTools.do301String("Vor-Befunde"),
				StringTools.do301String("Internistisch-körperlicher"),
				StringTools.do301String("Orthopädischer Befund:"),
				StringTools.do301String("Neurologischer Befund:"),
				StringTools.do301String("Psychischer Befund:"),
				StringTools.do301String("Vor-Befunde:"),
				StringTools.do301String("Labor bei der Aufnahme und EKG:"),
				StringTools.do301String("Rehabilitationsziele:"),
				StringTools.do301String("Rehabilitationsverlauf:"),
				StringTools.do301String("Rehabilitationsergebnis:"),
				StringTools.do301String("Sozialmedizinische Epikrise:"),
				StringTools.do301String("Therapieempfehlungen:"),
		});
		return liste;                                           
	}

	public static String[][] getCodeListe(String codeListe){
		String[][] ret = {{"unbekannt","unbekannt"}};
		if(codeListe.equals("B01")){return codeB01;}
		if(codeListe.equals("B02")){return codeB02;}
		if(codeListe.equals("B03")){return codeB03;}
		if(codeListe.equals("B04")){return codeB04;}
		if(codeListe.equals("B05")){return codeB05;}
		if(codeListe.equals("B06")){return codeB06;}
		if(codeListe.equals("B07")){return codeB07;}
		if(codeListe.equals("B08")){return codeB08;}
		if(codeListe.equals("B09")){return codeB09;}
		if(codeListe.equals("B10")){return codeB10;}
		if(codeListe.equals("B11")){return codeB11;}
		if(codeListe.equals("B12")){return codeB12;}
		if(codeListe.equals("B13")){return codeB13;}
		/****************/
		if(codeListe.equals("A01")){return codeA01;}
		if(codeListe.equals("A02")){return codeA02;}
		if(codeListe.equals("A03")){return codeA03;}
		if(codeListe.equals("A04")){return codeA04;}
		if(codeListe.equals("A05")){return codeA05;}
		if(codeListe.equals("A06")){return codeA06;}
		if(codeListe.equals("A07")){return codeA07;}
		if(codeListe.equals("A08")){return codeA08;}
		if(codeListe.equals("A09")){return codeA09;}
		if(codeListe.equals("A10")){return codeA10;}
		if(codeListe.equals("A11")){return codeA11;}
		if(codeListe.equals("A12")){return codeA12;}
		if(codeListe.equals("A13")){return codeA13;}
		if(codeListe.equals("A14")){return codeA14;}
		/****************/
		if(codeListe.equals("C01")){return codeC01;}
		if(codeListe.equals("C02")){return codeC02;}
		if(codeListe.equals("C03")){return codeC03;}
		if(codeListe.equals("C04")){return codeC04;}
		if(codeListe.equals("C05")){return codeC05;}
		if(codeListe.equals("C06")){return codeC06;}
		if(codeListe.equals("C07")){return codeC07;}
		if(codeListe.equals("C08")){return codeC08;}
		if(codeListe.equals("C09")){return codeC09;}
		if(codeListe.equals("C10")){return codeC10;}
		if(codeListe.equals("C11")){return codeC11;}
		if(codeListe.equals("C12")){return codeC12;}
		if(codeListe.equals("C13")){return codeC13;}
		if(codeListe.equals("C14")){return codeC14;}

		return ret;
	}
	
	public static String getCodeText(String codeListe,String codeNumber){
		String sret = "";
		String[][] codeliste = getCodeListe(codeListe);
		for(int i = 0; i < codeliste.length;i++){
			if(codeliste[0].equals(codeNumber)){
				return String.valueOf(codeliste[1]);
			}
		}
		return sret;
	}
	public static boolean mussFettDruck(String string){
		for(int i = 0 ; i < fettDruck().size() ; i++){
			if( string.contains(fettDruck().get(i))){
				return true;
			}
		}
		return false;
		
	}

}
