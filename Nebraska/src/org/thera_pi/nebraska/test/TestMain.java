/**
 * 
 */
package org.thera_pi.nebraska.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.thera_pi.nebraska.Nebraska;
import org.thera_pi.nebraska.NebraskaCryptoException;
import org.thera_pi.nebraska.NebraskaDecryptor;
import org.thera_pi.nebraska.NebraskaEncryptor;
import org.thera_pi.nebraska.NebraskaFileException;

/**
 * @author bodo
 *
 */
public class TestMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			String basedir = "/home/bodo/thera-pi/test/";
			Nebraska nebraska = new Nebraska(basedir + "keystore.p12", "123456", "abcdef", "540840108", "Reutlinger Therapie- und Analysezentrum GmbH", "Juergen Steinhilber");
			nebraska.importKeyPair(basedir + "540840108.prv");
			nebraska.importCertificateReply(basedir + "540840108.p7c");
			nebraska.importReceiverCertificates("annahme-pkcs.key");
			FileInputStream inStream;
			FileOutputStream outStream;
//			inStream = new FileInputStream(basedir + "plain.txt");
//			inStream = new FileInputStream(basedir + "plain.txt");
//			outStream = new FileOutputStream(basedir + "encrypted.dat");
//			NebraskaEncryptor encryptor = nebraska.getEncryptor("109900019");
//			encryptor.encryptToSelf(inStream, outStream);
//			inStream.close();
//			outStream.close();
			
//			inStream = new FileInputStream(basedir + "encrypted.dat");
			inStream = new FileInputStream(basedir + "TSOL0021.org.encoded");
			outStream = new FileOutputStream(basedir + "decrypted.dat");
			NebraskaDecryptor decryptor = nebraska.getDecryptor();
//			decryptor.processSignedData(new FileInputStream(basedir + "signed.dat"), outStream);
			decryptor.decrypt(inStream, outStream);
			inStream.close();
			outStream.close();
			
		} catch (NebraskaCryptoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NebraskaFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
