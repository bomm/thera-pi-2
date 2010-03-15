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
import org.thera_pi.nebraska.NebraskaNotInitializedException;

/**
 * @author bodo
 *
 */
public class TestMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			String basedir = "/home/bodo/thera-pi/test/";
			Nebraska nebraska = new Nebraska(basedir + "keystore.p12", "123456", "abcdef", "540840108", "Reutlinger Therapie- und Analysezentrum GmbH", "Juergen Steinhilber");
			nebraska.importKeyPair(basedir + "540840108.prv");
			nebraska.importCertificateReply(basedir + "540840108.p7c");
			nebraska.importReceiverCertificates("annahme-pkcs.key");
			FileInputStream inStream;
			FileOutputStream outStream;
			String inFileName = basedir + "plain.txt";
			String outFileName = basedir + "encrypted.dat";
			inStream = new FileInputStream(inFileName);
			outStream = new FileOutputStream(outFileName);
			NebraskaEncryptor encryptor = nebraska.getEncryptor("109900019");
			encryptor.setEncryptToSelf(true);
			encryptor.encrypt(inStream, outStream);
			inStream.close();
			outStream.close();
			
			long size = encryptor.encrypt(inFileName, outFileName);
			System.out.println("oputput file " + outFileName + " size " + size);
			inStream = new FileInputStream(outFileName);
//			inStream = new FileInputStream(basedir + "TSOL0021.org.encoded");
			outStream = new FileOutputStream(basedir + "decrypted.dat");
			NebraskaDecryptor decryptor = nebraska.getDecryptor();
//			decryptor.processSignedData(new FileInputStream(basedir + "signed.dat"), outStream);
			decryptor.decrypt(inStream, outStream);
			inStream.close();
			outStream.close();
			
		} catch (NebraskaCryptoException e) {
			e.printStackTrace();
		} catch (NebraskaFileException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NebraskaNotInitializedException e) {
			e.printStackTrace();
		}
		
	}

}
