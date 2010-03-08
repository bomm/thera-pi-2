package org.thera_pi.nebraska;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * This object can be used to encrypt data with the certificate of a specified receiver.
 * Optionally it can add an encryption using the sender's certificate to allow decryption by the sender.
 * 
 * @author bodo
 *
 */
public class NebraskaEncryptor {
	private String receiverIK;
	
	/**
	 * Create a Nebraska encryptor for specified receiver.
	 * 
	 * @param IK receiver ID (IK)
	 * @param nebraska reference to Nebraska object that contains the keystore
	 */
	protected NebraskaEncryptor(String IK, Nebraska nebraska) {
		this.receiverIK = IK;
		
		readReceiverCert();
		readCertificateChain();
		readSenderCert();
	}
	
	/**
	 * Read the receiver's certificate from the keystore.
	 */
	private void readReceiverCert() {
		// FIXME Auto-generated method stub
	}

	/**
	 * Read the sender's certificate from the keystore.
	 */
	private void readSenderCert() {
		// FIXME Auto-generated method stub
	}

	/**
	 * Read the the certificate chain (CA certs) from the keystore.
	 */
	private void readCertificateChain() {
		// FIXME Auto-generated method stub
	}

	/**
	 * Encrypt data for the receiver specified in constructor.
	 * 
	 * @param inStream plain text data stream
	 * @param outStream encrypted data stream
	 */
	public void encrypt(InputStream inStream, OutputStream outStream) {
		// FIXME encrypt
	}
	
	/**
	 * Encrypt data for the receiver specified in constructor and for sender.
	 * 
	 * @param inStream plain text data stream
	 * @param outStream encrypted data stream
	 */
	public void encryptToSelf(InputStream inStream, OutputStream outStream) {
		// FIXME encrypt
	}
	
	/**
	 * Encrypt data for the receiver specified in constructor and for sender.
	 * 
	 * @param inStream plain text data stream
	 * @param outStream encrypted data stream
	 */
	private void encryptToReceiverOrSelf(InputStream inStream, OutputStream outStream,
			boolean toSelf) {
		// FIXME encrypt
	}
	
}
