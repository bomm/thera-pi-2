package systemEinstellungen;

import sqlTools.SqlInfo;

public class inidb {

/**
 * Lemmi 20101225: neue Klasse
 * Klasse iniDB:
 * Lesen und Schreiben von INI-Properties aus und in die Datenbank anstelle von INI-Dateien 
 * Insgesamt identische Handhabung wie INI-Dateien inkl. Kommentar zu jeder Property
 * 
 */

	// Schreibt einen INTEGER Wert in die DB
	public static void WritePropInteger(String strRubrik, String strSection, String strProp, Integer iValue, String strComment ) {
		doWriteProperty(strRubrik, strSection, strProp, iValue.toString(), "INTEGER", strComment ); 
	}

	// Schreibt einen STRING Wert in die DB
	public static void WritePropString(String strRubrik, String strSection, String strProp, String strValue, String strComment ) {
		doWriteProperty(strRubrik, strSection, strProp, strValue, "STRING", strComment ); 
	}

	private static void doWriteProperty(String strRubrik, String strSection, String strProp, String strValue, String strType, String strComment ) {
		// Prüfen, ob Eintrag vorhanden?
		String strId = SqlInfo.holeEinzelFeld("select id from ini where " +
				"rubrik = '" + strRubrik + "' AND SECTION = '"  + strSection + "' AND PROPERTY = '" + strProp + "' " +
				" LIMIT 1" );
		
		if ( strId.isEmpty() ){  // neuen Eintrag wegschreiben
			SqlInfo.sqlAusfuehren("insert into ini set rubrik='" + strRubrik + "', SECTION='" + strSection + "', PROPERTY='" + strProp 
								  + "', VALUE='" + strValue + "', TYPE='" + strType + "', COMMENT='" + strComment + "'" );   
		} else {  // vorhandenen Eintrag updaten
			SqlInfo.sqlAusfuehren("update ini set rubrik='" + strRubrik + "', SECTION='" + strSection + "', PROPERTY='" + strProp 
								  + "', VALUE='" + strValue + "', TYPE='" + strType + "', COMMENT='" + strComment 
								  + "' where id='" + strId + "'" );   
		}
	}

	// Liest einen String-Wert aus der DB
	public static String ReadPropString(String strRubrik, String strSection, String strProp ) {
		String strValue = doReadProperty(strRubrik, strSection, strProp );
		return strValue;
	}
	public static Integer ReadPropInteger(String strRubrik, String strSection, String strProp ) {
		Integer iValue = Integer.parseInt(doReadProperty(strRubrik, strSection, strProp ));
		return iValue;
	}
	private static String doReadProperty(String strRubrik, String strSection, String strProp ) {
		String strValue = SqlInfo.holeEinzelFeld("select PROPERTY from ini where " +
				"rubrik = '" + strRubrik + "' AND SECTION = '"  + strSection + "' AND PROPERTY = '" + strProp + "' " +
				" LIMIT 1" );
		return strValue;
	}
	
}
