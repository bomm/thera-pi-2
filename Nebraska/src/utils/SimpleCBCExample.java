package utils;

import java.io.File;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import nebraska.BCStatics;
import nebraska.Constants;
import nebraska.Nebraska;
/**
* Basic symmetric encryption example with padding and CBC using DES
*/
public class SimpleCBCExample
{
public static void main(String[] args) throws Exception
{
	String pfad = Nebraska.keystoredir;
	String datei = BCStatics.chooser(pfad);
	if(datei.equals("")){System.exit(0);}
	BCStatics.providerTest();
	byte[] input = new byte[] {
			0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
			0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f,
			0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07 };

	byte[] keyBytes = new byte[] {
			0x01, 0x23, 0x45, 0x67,
			(byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef };

	byte[] ivBytes = new byte[] {
			0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01, 0x00 };
	
			SecretKeySpec key = new SecretKeySpec(keyBytes, Constants.SECRET_KEY_DES_DER3_CBC);
			IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
			Cipher cipher = Cipher.getInstance(Constants.CIPHER_AND_PADDING_OLD, Constants.SECURITY_PROVIDER);
			
			input = BCStatics.BytesFromFile(new File(pfad+datei));
			System.out.println("L�nge des OriginalFiles = "+input.length);
			BCStatics.BytesToFile(input,new File(pfad+datei+".orig2"));
			System.out.println("input : \n" + NUtils.toHex(input));
			System.out.println(new String(input));

			// encryption pass
			cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
			byte[] cipherText = new byte[
			cipher.getOutputSize(ivBytes.length + input.length)];
			int ctLength = cipher.update(ivBytes, 0, ivBytes.length, cipherText, 0);
			ctLength += cipher.update(input, 0, input.length, cipherText, ctLength);
			ctLength += cipher.doFinal(cipherText, ctLength);
			System.out.println("cipher: " +NUtils.toHex(cipherText, ctLength)
			+ " bytes: " + ctLength);
			BCStatics.BytesToFile(cipherText, new File(pfad+datei+".cipher"));

			
			// decryption pass
			cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
			byte[] buf = BCStatics.BytesFromFile(new File(pfad+datei+".cipher"));
			// original = byte[] buf = new byte[cipher.getOutputSize(ctLength)];
			int bufLength = cipher.update(cipherText, 0, ctLength, buf, 0);
			bufLength += cipher.doFinal(buf, bufLength);
			// remove the iv from the start of the message
			byte[] plainText = new byte[bufLength - ivBytes.length];
			
			System.arraycopy(buf, ivBytes.length, plainText, 0, plainText.length);
			
			BCStatics.BytesToFile(plainText, new File(pfad+datei+".plain"));
			
		}
}