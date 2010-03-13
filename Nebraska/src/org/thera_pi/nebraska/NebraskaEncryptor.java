package org.thera_pi.nebraska;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.X509Certificate;

import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSEnvelopedDataGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;

/**
 * This object can be used to encrypt data with the certificate of a specified receiver.
 * Optionally it can add an encryption using the sender's certificate to allow decryption by the sender.
 * 
 * @author bodo
 *
 */
public class NebraskaEncryptor {
	private String receiverIK;
	private Nebraska nebraska;
	private X509Certificate receiverCert;
	private X509Certificate senderCert;
	private PrivateKey senderKey;
	private CertStore certificateChain;
	/**
	 * Create a Nebraska encryptor for specified receiver.
	 * 
	 * @param IK receiver ID (IK)
	 * @param nebraska reference to Nebraska object that contains the key store
	 * @throws NebraskaCryptoException 
	 */
	NebraskaEncryptor(String IK, Nebraska nebraska) throws NebraskaCryptoException {
		this.receiverIK = IK;
		this.nebraska = nebraska;
		
		readReceiverCert();
		readSenderCert();
		readSenderKey();
		readCertificateChain();
	}
	
	private void readSenderKey() throws NebraskaCryptoException {
		this.senderKey = nebraska.getSenderKey();
	}

	/**
	 * Read the receiver's certificate from the keystore.
	 * @throws NebraskaCryptoException 
	 */
	private void readReceiverCert() throws NebraskaCryptoException {
		receiverCert = nebraska.getCertificate(receiverIK);
	}

	/**
	 * Read the sender's certificate from the keystore.
	 * @throws NebraskaCryptoException 
	 */
	private void readSenderCert() throws NebraskaCryptoException {
		senderCert = nebraska.getSenderCertificate();
	}

	/**
	 * Read the the certificate chain (CA certificates) from the key store.
	 * @throws NebraskaCryptoException 
	 */
	private void readCertificateChain() throws NebraskaCryptoException {
		certificateChain = nebraska.getSenderCertChain();
	}

	/**
	 * Encrypt data for the receiver specified in constructor.
	 * 
	 * @param inStream plain text data stream
	 * @param outStream encrypted data stream
	 * @throws NebraskaFileException 
	 * @throws NebraskaCryptoException 
	 */
	public void encrypt(InputStream inStream, OutputStream outStream) throws NebraskaCryptoException, NebraskaFileException {
		encryptToReceiverOrSelf(inStream, outStream, false);
	}

	/**
	 * Encrypt data for the receiver specified in constructor and for sender.
	 * 
	 * @param inStream plain text data stream
	 * @param outStream encrypted data stream
	 * @throws NebraskaFileException 
	 * @throws NebraskaCryptoException 
	 */
	public void encryptToSelf(InputStream inStream, OutputStream outStream) throws NebraskaCryptoException, NebraskaFileException {
		encryptToReceiverOrSelf(inStream, outStream, true);
	}
	
	/**
	 * Encrypt data for the receiver specified in constructor and for sender.
	 * 
	 * @param inStream plain text data stream
	 * @param outStream encrypted data stream
	 * @throws NebraskaCryptoException 
	 * @throws NebraskaFileException 
	 */
	private void encryptToReceiverOrSelf(InputStream inStream, OutputStream outStream,
			boolean toSelf) throws NebraskaCryptoException, NebraskaFileException {
		/*
		 * To get the input as byte array we copy all data to a 
		 * ByteArrayOutputStream and retrieve the byte array from it.
		 */
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		CMSProcessable plainContent;
		try {
			byte[] buffer = new byte[1024];
			int len;
			while((len = inStream.read(buffer)) > 0)
			{
				byteStream.write(buffer, 0, len);
			}
			byteStream.flush();
		
			// generate needs a CMSProcessable
			plainContent = new CMSProcessableByteArray(byteStream.toByteArray());
			byteStream.close();
		} catch (IOException e) {
			throw new NebraskaFileException(e);
		}

		// first processing step: sign data
		
		CMSSignedDataGenerator generator = new CMSSignedDataGenerator();
		generator.addSigner(senderKey, (X509Certificate)senderCert,
			      CMSSignedDataGenerator.DIGEST_SHA1);
		try {
			generator.addCertificatesAndCRLs(certificateChain);
		} catch (CertStoreException e) {
			throw new NebraskaCryptoException(e);
		} catch (CMSException e) {
			throw new NebraskaCryptoException(e);
		}

		CMSSignedData signedData;
		try {
			signedData = generator.generate(plainContent, true, 
					NebraskaConstants.SECURITY_PROVIDER);
		} catch (NoSuchAlgorithmException e) {
			throw new NebraskaCryptoException(e);
		} catch (NoSuchProviderException e) {
			throw new NebraskaCryptoException(e);
		} catch (CMSException e) {
			throw new NebraskaCryptoException(e);
		}

		// DER encoded output
		byte[] encodedSignedData = null;
		try {
			encodedSignedData = signedData.getEncoded();
		} catch (IOException e) {
			throw new NebraskaFileException(e);
		}

		// FIXME remove debug output
		FileOutputStream debugOutput;
		try {
			debugOutput = new FileOutputStream("/home/bodo/thera-pi/test/signed.dat");
			debugOutput.write(encodedSignedData);
			debugOutput.close();
		} catch (FileNotFoundException e) {
			throw new NebraskaFileException(e);
		}
		catch (IOException e) {
			throw new NebraskaFileException(e);
		}
		
		
		// second processing step: encrypt data
		
		CMSEnvelopedDataGenerator envelopedGenerator = new CMSEnvelopedDataGenerator();
		
		// the receiver must be able to decrypt the data
		envelopedGenerator.addKeyTransRecipient((X509Certificate) receiverCert);
		
		// optionally the sender may also decrypt it
		if(toSelf) {
			envelopedGenerator.addKeyTransRecipient((X509Certificate) senderCert);
		}
		
		CMSProcessable signedContent;
		signedContent = new CMSProcessableByteArray(encodedSignedData);
		CMSEnvelopedData envelopedData;
		try {
			envelopedData = envelopedGenerator.generate(signedContent, 
					CMSEnvelopedDataGenerator.DES_EDE3_CBC, 
					NebraskaConstants.SECURITY_PROVIDER);
		} catch (NoSuchAlgorithmException e) {
			throw new NebraskaCryptoException(e);
		} catch (NoSuchProviderException e) {
			throw new NebraskaCryptoException(e);
		} catch (CMSException e) {
			throw new NebraskaCryptoException(e);
		}
		byte[] encodedEnvelopedData;
		try {
			encodedEnvelopedData = envelopedData.getEncoded();
		} catch (IOException e) {
			throw new NebraskaFileException(e);
		}
		
		// write result to output
		try {
			outStream.write(encodedEnvelopedData);
		} catch (IOException e) {
			throw new NebraskaFileException(e);
		}
	}
	
}
