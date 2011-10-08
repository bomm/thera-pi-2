package org.thera_pi.tools;

import java.io.File;

public class TheraPiLocations {

	/* NOTE: never change these values to public, use the getters only! */
	/* try to get proghome directory from environment */
	private static final String envHome = System.getenv("THERA_PI_HOME");
	/* if environment variable is specified use this directory */ 
	private static final String theraPiHome = (envHome != null) ? envHome
			 /* else try to find directory containing JAR files */
			: ClassLoader.getSystemClassLoader().getResource(".").getPath();
	
	/* without trailing separator because we return this directly */ 
	private static final String iconsDir = theraPiHome + File.separator + "icons";
	/* with trailing separator because we will append the institution ID (IK) */
	private static final String edifactBase = theraPiHome + File.separator + "edifact" + File.separator;
	private static final String templatesBase = theraPiHome + File.separator + "vorlagen" + File.separator;
	private static final String keystoreBase = theraPiHome + File.separator + "keystore" + File.separator;
	
	/**
	 * Get the base directory of Thera-Pi program files.
	 * 
	 * @return program home directory name
	 */
	public static final String getTheraPiHome()
	{
		return theraPiHome;
	}
	
	/**
	 * Get the directory for icons.
	 * 
	 * @return icons directory name
	 */
	public static final String getIconsDir()
	{
		return iconsDir;
	}
	
	/**
	 * Get the institution specific directory for EDIFACT files.
	 * 
	 * @return EDIFACT directory name
	 */
	public static String getEdifactDir(String IK)
	{
		return edifactBase + Normalizer.normalizeIK(IK);
	}
	
	/**
	 * Get the institution specific directory for document template files.
	 * 
	 * @return templates directory name
	 */
	public static String getTemplatesDir(String IK)
	{
		return templatesBase + Normalizer.normalizeIK(IK);
	}
	
	/**
	 * Get the institution specific keystore file name.
	 * 
	 * @return keystore file name
	 */
	public static String getKeystoreFile(String IK)
	{
		String normalizedIK = Normalizer.normalizeIK(IK);
		return keystoreBase + normalizedIK + File.separator + normalizedIK + ".p12";
	}
}
