package reha301;

public class Dta301CodeListen {
	
	//Arbeitsfähigkeit (nur RV)
	public static String[][] codeB01 = {
		{"0","Maßnahme nicht ordnungsgemäß abgeschlossen gestorben"},
		{"1","Arbeitsfähig"},
		{"3","Arbeitsunfähig"},
		{"4","Kinderreha"},
		{"5","Hausfrau/Hausmann"},
		{"9","Beurteilung nicht erforderlich (Altersrentner,Angehöriger)"}
	};
	//Arbeitsfähigkeit bei Entlassung
	public static String[][] codeB02 = {
		{"1","Arbeitsfähig entlassen"},
		{"2","Arbeitsunfähig entlassen"},
		{"3","Arbeitsfähig mit Schungszeit entlassen (BGE)"},
		{"9","Meldung nicht erforderlich (Altersrentner,Angehöriger)"}
	};
	//AU-Zeiten der letzten 12 Monate (nur RV)
	public static String[][] codeB03 = {
		{"0","Keine Arbeitsunfähigkeitszeiten währen der letzten 12 Monate"},		
		{"1","Bis unter 3 Monate arbeitsunfähig"},
		{"2","3 bis unter 6 Monate arbeitsunfähig"},
		{"3","6 und mehr Monate arbeitsunfähig"},
		{"9","Nicht erwererbstätig (Altersrentner,Vorruhestantsgeldempfänger)"}
	};
	//Art der Fahrtkostenerstattung
	public static String[][] codeB04 = {
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
	public static String[][] codeB05 = {
		{"1","Erläuterung für nachfolgende Maßnahmen (Blatt 1)"},
		{"2","Letzte Medikation (Blatt 1)"},
		{"3","Beschreibung des Leistungsbildes"},
		{"4","Erläuterung der Leistungsdaten"},
		{"5","Freier medizinischer Entlassungsbericht"}
	};
	//Entlassform (nur RV)
	public static String[][] codeB06 = {
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
	public static String[][] codeB07 = {
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
	public static String[][] codeB08 = {
		{"01","Familienheimfahrt"},
		{"02","Todesfall in der Familie"},
		{"03","Interkurrente Erkrankung"},
		{"04","Stationäre Krankenhausbehandlung (nicht interkurrente Erkr."},
		{"09","Sonstiger Grund"}
	};
	//Indikationsgruppenzuordnung
	public static String[][] codeB09 = {
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
	public static String[][] codeB10 = {
		{"0","1-5 triff nicht zu"},
		{"1","Arbeitsunfall einschließlich Wegeunfall"},
		{"2","Berufskrankheit"},
		{"3","Schädigungsfolge durch Einwirkung Dritter (Unfallfolge)"},
		{"4","Folge von Kriegs-,Zivil- oder Wehrdienst"},
		{"5","Meldepflichtige Erkrankung"}
	};
	//Art der Versorgung
	public static String[][] codeB11 = {
		{"1","stationär"},
		{"2","ganztägig ambulant"},
		{"3","ambulant (nur RV)"}
	};	
	//Erläuterung zur ambulanten Leistung (nur RV)
	public static String[][] codeB12 = {
		{"1","Meldung des 1.Behandlungstages bzw. 1.Abschnitt"},
		{"2","Meldung des 2 bis n-ten Beh.Tages bzw. Abschnitts"},
		{"3","Meldung des letzten Behandl.tages bzw. letzter Abschnitt"},
		{"4","Meldung des gesamten Behandlung"}
	};	
	//DMP-Patient (nur RV)
	public static String[][] codeB13 = {
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
	public static String[][] codeA01 = {
		{"1","Anreise mit öffentliche Verkehrsmittel, Patient löst Fahrkarte selbst"},
		{"2","Anreise mit öffentliche Verkehrsmittel, Fahrkarte wird gestellt"},
		{"3","Anreise mit PKW"},
		{"4","Anreise mit Krankentrasport / Taxi"},
		{"5","Anreise mit Flugzeug"},
		{"6","Anreise mit Sammelanreise (Kinder)"}
	};	
	
	//Antragsart (nur RV)
	public static String[][] codeA02 = {
		{"11","Normale medizinische Reha einschl. RPK"},
		{"12","Entwöhnungsbehandlung (stationäre Suchtleistung)"},
		{"13","CA-Leistung für den Versicherten"},
		{"21","Medizinische Leistung für nicht versicherte Erwachsene"},
		{"22","Kinderheilbehandlung"},
		{"31","ambulante und/oder ganztägig ambulante medizinische Reha gem. § 15 SGB VI"},
		{"32","Sonstige Leistungen gemäß § 31 Abs. 1 Nr. 1 SGB VI"},
		{"33","Sonstige Leistungen gemäß § 31 Abs. 1 Nr. 2 SGB VI"},
		{"99","Auftagsheilbehandlung"},
	};	
	//Art der Suchterkrankung
	public static String[][] codeA03 = {
		{"1","Keine Suchterkrankung bekannt"},
		{"2","Alkoholabhängigkeit"},
		{"3","Medikamentenabhängigkeit"},
		{"4","Drogenabhängigkeit"},
		{"5","Mehrfachabhängigkeit"},
		{"6","Spielsucht"},		
		{"7","Eßstörungen"}
	};
	//Auftragsleistung
	public static String[][] codeA04 = {
		{"1","Keine Auftragsleistung"},
		{"2","Vollständige Fallabwicklung durch beauftragte Stelle"},
		{"3","Bewilligung durch beauftragte Stelle, weitere Abwicklung durch Klinik"}
	};
	//Begleitperson Hinreise
	public static String[][] codeA05 = {
		{"1","Anreise keine Begleitperson"},
		{"2","Anreise Begleitperson (Erwachsene)"},
		{"3","Anreise Begleitperson (Kind)"},
		{"4","Anreise Begleitperson Ehepartner/Lebenspartner"},
		{"9","Anreise mehr als eine Begleitperson (Familienkur)"}
	};
	//Begleitperson Rückreise
	public static String[][] codeA06 = {
		{"1","Rückreise keine Begleitperson"},
		{"2","Rückreise Begleitperson (Erwachsene)"},
		{"3","Rückreise Begleitperson (Kind)"},
		{"4","Rückreise Begleitperson Ehepartner/Lebenspartner"},
		{"9","Rückreise mehr als eine Begleitperson (Familienkur)"}
	};
	//Begleitperson Aufenthalt
	public static String[][] codeA07 = {
		{"1","Aufenthalt keine Begleitperson"},
		{"2","Aufenthalt Begleitperson (Erwachsene)"},
		{"3","Aufenthalt Begleitperson (Kind)"},
		{"4","Aufenthalt Begleitperson Ehepartner/Lebenspartner"},
		{"9","Aufenthalt mehr als eine Begleitperson (Familienkur)"}
	};
	//Behinderung
	public static String[][] codeA08 = {
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
	public static String[][] codeA09 = {
		{"J","JA"},
		{"N","NEIN"}
	};
	//Entgeldkategorie
	public static String[][] codeA10 = {
		{"1","Vergütung"},
		{"2","Zuschuß"},
		{"3","Fallpauschale"}
	};
	//Maßnahmeart
	public static String[][] codeA11 = {
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
	public static String[][] codeA12 = {
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
	public static String[][] codeA13 = {
		{"1","Keine Zuzahlung"},
		{"2","Zuzahlungsbetrag soll von der Klinik eingezogen werden"},
		{"3","Zuzahlungsbetrag soll von der Klinik nicht(!!) eingezogen werden"}
	};
	//Zwischenrechnung erlaubt
	public static String[][] codeA14 = {
		{"J","JA"},
		{"N","NEIN"}
	};	

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

}
