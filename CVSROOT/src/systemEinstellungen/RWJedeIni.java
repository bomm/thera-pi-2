package systemEinstellungen;

public class RWJedeIni {
private static INIFile ini = null; 

	public static void schreibeIniDatei(String iniDatei, String gruppe,String element,String wert){
		ini = new INIFile(iniDatei);
		ini.setStringProperty(gruppe, element, wert, null);
		ini.save();
		ini = null;
	}
	public static String leseIniDatei(String iniDatei, String gruppe,String element){
		ini = new INIFile(iniDatei);
		String sret = new String(ini.getStringProperty(gruppe,element));
		ini = null;
		return sret;
	}

}
