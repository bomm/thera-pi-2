package org.thera_pi.nebraska;

import java.text.Normalizer;
import java.util.Calendar;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

public class NebraskaUtil {
	
	/**
	 * This is the set of charactes allowes by ITSG Trust Center.
	 * It is a subset of PrintableString.
	 */
	private static final String allowedCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 /.()-";
	
	/**
	 * Create Subject DN string from variable parts and constant part.
	 * X500Principal is used to normalize the string.
	 * 
	 * @param IK the institutions ID number (IK)
	 * @param institutionName name of the institution
	 * @param personName name of the responsible person
	 * @return Subject DN string
	 */
	public static String getSubjectDN(String IK, String institutionName, String personName) {
		X500Principal principal = new X500Principal(
			NebraskaConstants.X500_PRINCIPAL_COMMON + "," +
				" OU=" + normalizeDnField(institutionName) + 
				", OU=IK" + normalizeDnField(normalizeIK(IK)) +
				", CN=" + normalizeDnField(personName));
		return principal.getName();
	}

	/**
	 * Create a String that contains only characters of the PrintableString 
	 * subset allowed by ITSG Trust Center.
	 * @param input string to normalize
	 * @return normalized string
	 */
	public static String normalizeDnField(String input) {
		String result;
		// change unicode characters to composite form if possible
		result = Normalizer.normalize(input, Normalizer.Form.NFKC);
		// special replacement sequences, mainly for German
		result = result.replaceAll("ä", "ae").replaceAll("ö", "oe").replaceAll("ü", "ue");
		result = result.replaceAll("Ä",	"AE").replaceAll("Ö", "OE").replaceAll("Ü", "UE");
		result = result.replaceAll("ß",	"ss").replaceAll("æ", "ae").replaceAll("ø", "o");
		result = result.replaceAll("Ø","O").replaceAll("ł", "l").replaceAll("Ł", "L");
		// change unicode characters to combining sequence, that means
		// accents are separated from base characters
		result = Normalizer.normalize(result, Normalizer.Form.NFKD);
		// remove all accents
		result = result.replaceAll("[\\p{M}]", "");
		// remove all illegal characters following or preceding a space
		result = result.replaceAll(" [^" + allowedCharacters + "]+"," ");
		result = result.replaceAll("[^" + allowedCharacters + "]+ "," ");
		// replace all illegal characters with space
		result = result.replaceAll("[^" + allowedCharacters + "]+"," ");
		
		return result;
	}
	
	/**
	 * remove leading IK from input
	 * @param input
	 * @return
	 */
	public static String normalizeIK(String input) {
		return input.trim().replaceFirst("^[iI][kK]", "").trim();
	}
	
	public static String getCertAlias(String input) {
		return input.trim();
	}
	
	public static String getKeyAlias(String input) {
		return input.trim();
	}
	
	static Date certificateStart(Date date) {
		return certificateStartOrEnd(date, false);
	}

	static private Date certificateStartOrEnd(Date date, boolean end) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		if(end) {
			calendar.setLenient(true);
			calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + NebraskaConstants.CERTIFICATE_YEARS);
			calendar.set(Calendar.MILLISECOND, -1);
		}
		return calendar.getTime();
	}

	static Date certificateEnd(Date date) {
		return certificateStartOrEnd(date, true);
	}

	public static String toHexString(byte[] bytes) {
		StringBuffer result = new StringBuffer();
		for(int i = 0; i < bytes.length; i++) {
			if(i>0) {
				result.append(':');
			}
			String hex = Integer.toHexString(0xFF & (int)(bytes[i]));
			if(hex.length() < 2){
				result.append('0');
			}
			result.append(hex);
		}
		return result.toString();
	}

	
//	public static void main(String args[]) {
//		for(int i = 0; i < args.length; i++) {
//			System.out.println("" + args[i] + " -> " + normalizeDnField(args[i]));
//		}
//	}
}
