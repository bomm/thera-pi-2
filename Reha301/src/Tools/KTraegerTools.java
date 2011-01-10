package Tools;

import java.util.Vector;



public class KTraegerTools {

	public static String getEmailAdresse(String ik){
		return SqlInfo.holeEinzelFeld("select email from ktraeger where ikkasse='"+ik+"' LIMIT 1");
	}
	public static String getKtraegerIK(String ik){
		return SqlInfo.holeEinzelFeld("select ikkostentraeger  from ktraeger where ikkasse='"+ik+"' LIMIT 1");
	}
	public static String getDatenIK(String ik){
		return SqlInfo.holeEinzelFeld("select ikdaten  from ktraeger where ikkasse='"+ik+"' LIMIT 1");
	}
	public static String getNutzerIK(String ik){
		return SqlInfo.holeEinzelFeld("select ikentschluesselung from ktraeger where ikkasse='"+ik+"' LIMIT 1");
	}
	public static String getPapierIK(String ik){
		return SqlInfo.holeEinzelFeld("select ikpapier from ktraeger where ikkasse='"+ik+"' LIMIT 1");
	}
	public static Vector<Vector<String>> getPapierAdresse(String ik){
		return SqlInfo.holeFelder("select name1,name2,name3,adresse1,adresse2,adresse3 from ktraeger where ikkasse='"+ik+"' LIMIT 1");
	}
}
