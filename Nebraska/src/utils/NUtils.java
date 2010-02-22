package utils;

import java.security.MessageDigest;
import java.security.SecureRandom;

import nebraska.Constants;


public class NUtils {
	private static String digits = "0123456789abcdef";


	public static String toHex(byte[] data,int length ){
		StringBuffer buf = new StringBuffer();
		for(int i =0; i!= length; i++){
			int v = data[i] & 0xff;
			buf.append(digits.charAt(v >>4));
			buf.append(digits.charAt(v & 0xf));
		}
		return buf.toString();
	}
	public static String toHex(byte[] data){
		return toHex(data,data.length);
	}
	/*********/
	//Byte array zu String
	public static String toString(byte[] bytes,int length){
		char[] chars = new char[length];
		for(int i = 0; i != length;i++){
			chars[i] = (char) (bytes[i] & 0xff);
		}
		return new String(chars);
	}
	public static String toString(byte[] bytes){
		return toString(bytes,bytes.length);
	}
	/*********/
	//String in byteArray
	public static byte[] toByteArray(String string){
		byte[] bytes = new byte[string.length()];
		char[] chars = string.toCharArray();
		for(int i = 0; i != chars.length;i++){
			bytes[i] = (byte) chars[i];
		}
		return bytes;
	}
	/*********/
	//Liefert ein SecureRandom zurück
	private static class FixedRand extends SecureRandom{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1903627596069377055L;
		MessageDigest sha;
		byte[] state;
		FixedRand(){
			try{
				this.sha = MessageDigest.getInstance(Constants.HASH_ALGORITHM_SHA_1,Constants.SECURITY_PROVIDER);
				this.state = sha.digest();
				
			}catch(Exception e){
				throw new RuntimeException("kann SHA-1 nicht finden");
			}
		}

		public void nextBytes(byte[] bytes){
			int off = 0;
			sha.update(state);
			while(off < bytes.length){
				state = sha.digest();
				if(bytes.length -off > state.length){
					System.arraycopy(state,0,bytes,off,state.length);
				}else{
					System.arraycopy(state,0,bytes,off,bytes.length-off);
				}
				off += state.length;
				sha.update(state);
			}
		}
	}
	public static SecureRandom createFixedRandom(){
		return new FixedRand();
	}
}
