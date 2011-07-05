package org.thera_pi.updates;

public class UpdateConfig {
	private String updateDir = "";
	private String updateHost = "";
	private String updateUser = "";
	private String updatePasswd = "";
	private boolean useActiveMode = false;
	private boolean developerMode = false;
	private boolean checkUpdates = false;
	
	private static UpdateConfig instance = null;

	private static String proghome = null;
	private static boolean testphase = false;

	static {
		if(testphase){
			proghome = "C:/RehaVerwaltung/";
		}else{
			proghome = java.lang.System.getProperty("user.dir").replace("\\","/")+"/";	
		}

	}

	
	private UpdateConfig()
	{
		readIniFile();
	}
	
	public static UpdateConfig getInstance()
	{
		if(instance == null)
		{
			instance = new UpdateConfig();
		}
		return instance;
	}
	
	private void readIniFile(){
		INIFile ini = new INIFile(proghome + "/ini/tpupdate.ini");
		
		//System.out.println(proghome+"update.ini");

		updateHost = ini.getStringProperty("TheraPiUpdates", "UpdateFTP");
		updateDir = ini.getStringProperty("TheraPiUpdates", "UpdateVerzeichnis");
		updateUser = ini.getStringProperty("TheraPiUpdates", "UpdateUser");

		String pw = ""; 
		pw = ini.getStringProperty("TheraPiUpdates", "UpdatePasswd");
		Verschluesseln man = Verschluesseln.getInstance();
		man.init(Verschluesseln.getPassword().toCharArray(), man.getSalt(), man.getIterations());
		if(pw.length() <= 20){
			ini.setStringProperty("TheraPiUpdates", "UpdatePasswd", man.encrypt(String.valueOf(pw)),null);
			ini.save();
			updatePasswd = String.valueOf(pw);
		}else{
			updatePasswd = man.decrypt(ini.getStringProperty("TheraPiUpdates", "UpdatePasswd"));
		}
		developerMode = ("1".equals(ini.getStringProperty("TheraPiUpdates", "UpdateEntwickler")) ? true : false);
		useActiveMode = ("1".equals(ini.getStringProperty("TheraPiUpdates", "UseFtpActiveMode")) ? true : false);
		checkUpdates = ("0".equals(ini.getStringProperty("TheraPiUpdates", "UpdateChecken")) ? false : true);
	}


	public String getUpdateDir() {
		return updateDir;
	}

	public String getUpdateHost() {
		return updateHost;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public String getUpdatePasswd() {
		return updatePasswd;
	}

	public boolean isUseActiveMode() {
		return useActiveMode;
	}

	public static String getProghome() {
		return proghome;
	}

	public boolean isDeveloperMode() {
		return developerMode;
	}

	public boolean isCheckUpdates() {
		return checkUpdates;
	}

}
