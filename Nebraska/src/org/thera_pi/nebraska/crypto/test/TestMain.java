/**
 * 
 */
package org.thera_pi.nebraska.crypto.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.thera_pi.nebraska.crypto.NebraskaKeystore;
import org.thera_pi.nebraska.crypto.NebraskaCryptoException;
import org.thera_pi.nebraska.crypto.NebraskaDecryptor;
import org.thera_pi.nebraska.crypto.NebraskaEncryptor;
import org.thera_pi.nebraska.crypto.NebraskaFileException;
import org.thera_pi.nebraska.crypto.NebraskaNotInitializedException;

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
			NebraskaKeystore nebraskaKeystore = new NebraskaKeystore(basedir + "keystore.p12", "123456", "abcdef", "540840108", "Reutlinger Therapie- und Analysezentrum GmbH", "Juergen Steinhilber");
			nebraskaKeystore.importKeyPair(basedir + "540840108.prv");
			nebraskaKeystore.importCertificateReply(basedir + "540840108.p7c");
			nebraskaKeystore.importReceiverCertificates("annahme-pkcs.key");
			FileInputStream inStream;
			FileOutputStream outStream;
			String inFileName = basedir + "plain.txt";
			String outFileName = basedir + "encrypted.dat";
			inStream = new FileInputStream(inFileName);
			outStream = new FileOutputStream(outFileName);
			NebraskaEncryptor encryptor = nebraskaKeystore.getEncryptor("109900019");
			encryptor.setEncryptToSelf(true);
			encryptor.encrypt(inStream, outStream);
			inStream.close();
			outStream.close();
			
			long size = encryptor.encrypt(inFileName, outFileName);
			System.out.println("oputput file " + outFileName + " size " + size);
			inStream = new FileInputStream(outFileName);
//			inStream = new FileInputStream(basedir + "TSOL0021.org.encoded");
			outStream = new FileOutputStream(basedir + "decrypted.dat");
			NebraskaDecryptor decryptor = nebraskaKeystore.getDecryptor();
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
