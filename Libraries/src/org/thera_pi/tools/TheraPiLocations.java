package org.thera_pi.tools;

import java.io.File;

public class TheraPiLocations {

	private static final String initialIK = "510841109";
	
	/* for easy switch between OS specific separator and "/" */
	private static final String separator = File.separator;
	
	/* NOTE: never change these values to public, use the getters only! */
	/* try to get proghome directory from environment */
	private static final String envHome = System.getenv("THERA_PI_HOME");
	/* if environment variable is specified use this directory */ 
	private static final String theraPiHome = (envHome != null) ? envHome
			 /* else try to find directory containing JAR files */
			: ClassLoader.getSystemClassLoader().getResource(".").getPath();
	
	/* without trailing separator because we return this directly */ 
	private static final String iconsDir = theraPiHome + separator + "icons";
	private static final String commonIniDir = theraPiHome + separator + "ini";
	
	/* with trailing separator because we will append the institution ID (IK) */
	private static final String edifactBase = theraPiHome + separator + "edifact" + separator;
	private static final String templatesBase = theraPiHome + separator + "vorlagen" + separator;
	private static final String keystoreBase = theraPiHome + separator + "keystore" + separator;
	private static final String iniBase = commonIniDir + separator;
	private static final String defaultsBase = theraPiHome + separator + "defaults" + separator;
	
	/* without trailing separator because we return this directly */ 
	private static final String defaultsIniDir =  defaultsBase + "ini";
	private static final String defaultsTemplatesDir =  defaultsBase + "vorlagen";
	private static final String defaultsTempDir =  defaultsBase + "temp";
	private static final String defaultsEdifactDir =  defaultsBase + "edifact";
	private static final String defaultsKeystoreDir =  defaultsBase + "keystore";
	private static final String defaultsVacationDir =  defaultsBase + "urlaub";

	/**
	 * Get the base directory of Thera-Pi program files.
	 * Replaces proghome, but does not include trailingg separator
	 * 
	 * @return program home directory name
	 */
	public static String getTheraPiHome()
	{
		return theraPiHome;
	}
	
	/**
	 * Get the directory for icons.
	 * Replaces proghome + "icons".
	 * 
	 * @return icons directory name
	 */
	public static String getIconsDir()
	{
		return iconsDir;
	}
	
	/**
	 * Get the common directory for INI files.
	 * Replaces proghome + "ini" (without IK.)
	 *   
	 * @return common INI files directory name
	 */
	public static String getCommonIniDir()
	{
		return commonIniDir;
	}
	
	/**
	 * Get the directory with default INI files.
	 * To be used in SysUtilMandanten.neuSpeichern().
	 *   
	 * @return default INI files directory name
	 */
	public static String getDefaultsIniDir()
	{
		return defaultsIniDir;
	}
	
	/**
	 * Get the directory with default document templates.
	 * To be used in SysUtilMandanten.neuSpeichern().
	 *   
	 * @return default document templates directory name
	 */
	public static String getDefaultsTemplatesDir()
	{
		return defaultsTemplatesDir;
	}
	
	/**
	 * Get the directory with default temp files.
	 * To be used in SysUtilMandanten.neuSpeichern().
	 *   
	 * @return default temp files directory name
	 */
	public static String getDefaultsTempDir()
	{
		return defaultsTempDir;
	}
	
	/**
	 * Get the directory with default EDIFACT files.
	 * To be used in SysUtilMandanten.neuSpeichern().
	 *   
	 * @return default EDIFACT files directory name
	 */
	public static String getDefaultsEdifactDir()
	{
		return defaultsEdifactDir;
	}
	
	/**
	 * Get the directory with default keystore files.
	 * To be used in SysUtilMandanten.neuSpeichern().
	 *   
	 * @return default keystore files directory name
	 */
	public static String getDefaultsKeystoreDir()
	{
		return defaultsKeystoreDir;
	}
	
	/**
	 * Get the directory with default vacation files.
	 * To be used in SysUtilMandanten.neuSpeichern().
	 *   
	 * @return default vacation files directory name
	 */
	public static String getDefaultsVacationDir()
	{
		return defaultsVacationDir;
	}
	
	/**
	 * Get the institution specific directory for INI files.
	 * Replaces proghome + "ini/" + Reha.aktIK or proghome + "ini/" + "510841109".
	 * 
	 * @param IK identification of current institution or NULL for initial IK
	 * 
	 * @return INI files directory name
	 */
	public static String getIniDir(String IK)
	{
		return iniBase + normalizeIkOrInitial(IK);
	}
	
	/**
	 * Get the institution specific directory for EDIFACT files.
	 * Replaces proghome + "edifact/" + Reha.aktIK or proghome + "edifact/" + "510841109".
	 * 
	 * @param IK identification of current institution or NULL for initial IK
	 * 
	 * @return EDIFACT directory name
	 */
	public static String getEdifactDir(String IK)
	{
		return edifactBase + normalizeIkOrInitial(IK);
	}
	
	/**
	 * Get the institution specific directory for document template files.
	 * Replaces proghome + "vorlagen/" + Reha.aktIK or proghome + "vorlagen/" + "510841109".
	 * 
	 * @param IK identification of current institution or NULL for initial IK
	 * 
	 * @return templates directory name
	 */
	public static String getTemplatesDir(String IK)
	{
		return templatesBase + normalizeIkOrInitial(IK);
	}
	
	/**
	 * Get the institution specific keystore file name.
	 * Replaces proghome + "keystore/" + Reha.aktIK + "/" + Reha.aktIK + ".p12" or similar.
	 * 
	 * @param IK identification of current institution or NULL for initial IK
	 * 
	 * @return keystore file name
	 */
	public static String getKeystoreFile(String IK)
	{
		String normalizedIK = normalizeIkOrInitial(IK);
		return keystoreBase + normalizedIK + separator + normalizedIK + ".p12";
	}

	/**
	 * Get the institution specific keystore directory name.
	 * Replaces proghome + "keystore/" + Reha.aktIK or proghome + "keystore/" + "510841109".
	 * 
	 * @param IK identification of current institution or NULL for initial IK
	 * 
	 * @return keystore directory name
	 */
	public static String getKeystoreDir(String IK)
	{
		String normalizedIK = normalizeIkOrInitial(IK);
		return keystoreBase + normalizedIK + separator + normalizedIK + ".p12";
	}

	/**
	 * Normalize specified institution ID (IK) or return initial IK.
	 * 
	 * @param IK identification of current institution or NULL for initial IK
	 * 
	 * @return normalized IK
	 */
	private static String normalizeIkOrInitial(String IK)
	{
		if(IK != null)
		{
			return Normalizer.normalizeIK(IK);
		}
		else
		{
			return initialIK;
		}
	}
}
