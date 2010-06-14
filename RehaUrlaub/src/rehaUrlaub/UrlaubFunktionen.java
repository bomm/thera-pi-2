package rehaUrlaub;

public class UrlaubFunktionen {
	
	public static String getUrlaubTableDef(String jahr){
		
		StringBuffer buf = new StringBuffer();
		
		buf.append("CREATE TABLE IF NOT EXISTS urlaub"+jahr+" (");
		buf.append("KW int(11) NOT NULL,");
		buf.append("VON_BIS varchar(21) DEFAULT NULL,");
		buf.append("MO_STUNDEN decimal(5,2) DEFAULT NULL,");
		buf.append("MO_ART varchar(4) DEFAULT NULL,");
		buf.append("DI_STUNDEN decimal(5,2) DEFAULT NULL,");
		buf.append("DI_ART varchar(4) DEFAULT NULL,");
		buf.append("MI_STUNDEN decimal(5,2) DEFAULT NULL,");
		buf.append("MI_ART varchar(4) DEFAULT NULL,");
		buf.append("DO_STUNDEN decimal(5,2) DEFAULT NULL,");
		buf.append("DO_ART varchar(4) DEFAULT NULL,");
		buf.append("FR_STUNDEN decimal(5,2) DEFAULT NULL,");
		buf.append("FR_ART varchar(4) DEFAULT NULL,");
		buf.append("SA_STUNDEN decimal(5,2) DEFAULT NULL,");
		buf.append("SA_ART varchar(4) DEFAULT NULL,");
		buf.append("SO_STUNDEN decimal(5,2) DEFAULT NULL,");
		buf.append("SO_ART varchar(4) DEFAULT NULL,");
		buf.append("IST_ARBEIT decimal(8,2) DEFAULT NULL,");
		buf.append("SOLL_ARBEI decimal(8,2) DEFAULT NULL,");
		buf.append("UEBERSTUND decimal(8,2) DEFAULT NULL,");
		buf.append("URLAUB decimal(8,2) DEFAULT NULL,");
		buf.append("KRANKHEIT decimal(8,2) DEFAULT NULL,");
		buf.append("AUSFALL decimal(8,2) DEFAULT NULL,");
		buf.append("KAL_ZEILE int(11) NOT NULL,");
		buf.append("KAL_BENUTZER varchar(40) NOT NULL,");
		buf.append("JAHR int(11) NOT NULL,");
		buf.append("BERECHNET enum(\"T\",\"F\") DEFAULT \"F\",");
		buf.append("ID int(11) NOT NULL AUTO_INCREMENT,");
		buf.append("PRIMARY KEY (ID),");
		buf.append("KEY urlaub (JAHR,KW,KAL_ZEILE)");
		buf.append(") ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1");
		
		return buf.toString();
		
	}

}
