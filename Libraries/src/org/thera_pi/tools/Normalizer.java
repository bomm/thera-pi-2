package org.thera_pi.tools;


public class Normalizer {

	/* copied from NebraskaUtil */
	
	/**
	 * Remove leading IK from input.
	 * 
	 * @param input an institution ID string, optionally with leading "IK"
	 * 
	 * @return normalized institution ID
	 */
	public static String normalizeIK(String input) {
		return input.trim().replaceFirst("^[iI][kK]", "").trim();
	}
	
	

}
