package org.thera_pi.nebraska;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Iterator;

import org.bouncycastle.cms.CMSEnvelopedDataParser;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedDataParser;
import org.bouncycastle.cms.CMSTypedStream;
import org.bouncycastle.cms.RecipientId;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;

public class NebraskaDecryptor {

	private Nebraska nebraska;
	private X509Certificate certificate;
	private PrivateKey privateKey;
	private String issuer;
	private BigInteger serial;

	/**
	 * Create a Nebraska decryptor for self
	 * 
	 * @param nebraska reference to Nebraska object that contains the key store
	 * @throws NebraskaCryptoException 
	 */
	NebraskaDecryptor(Nebraska nebraska) throws NebraskaCryptoException {
		this.nebraska = nebraska;
		certificate = nebraska.getSenderCertificate();
		privateKey = nebraska.getSenderKey();
		issuer = certificate.getIssuerDN().getName();
		serial = certificate.getSerialNumber();
	}
	
	/**
	 * Decrypt data and check signature.
	 * 
	 * @param inStream encrypted data stream
	 * @param outStream plain text data stream
	 * @throws NebraskaFileException 
	 * @throws NebraskaCryptoException 
	 */
	public void decrypt(InputStream inStream, OutputStream outStream) throws NebraskaCryptoException, NebraskaFileException {
		CMSEnvelopedDataParser parser;
		try {
			parser = new CMSEnvelopedDataParser(inStream);
		} catch (CMSException e) {
			throw new NebraskaCryptoException(e);
		} catch (IOException e) {
			throw new NebraskaFileException(e);
		}
		RecipientInformationStore  recipients = parser.getRecipientInfos();
	      Collection<?>  c = recipients.getRecipients();
	      Iterator<?> it = c.iterator();

	      NebraskaPrincipal myPrincipal = new NebraskaPrincipal(this.issuer);
	      while (it.hasNext())
	      {
	          RecipientInformation   recipient = (RecipientInformation)it.next();
	          RecipientId rid = recipient.getRID();
	          String issuer = rid.getIssuer().getName();
	          BigInteger serial = rid.getSerialNumber();

	          NebraskaPrincipal receiverPrincipal = new NebraskaPrincipal(issuer);
	          if(myPrincipal.equals(receiverPrincipal) && this.serial.equals(serial))
	          {
		          CMSTypedStream recData;
		          try {
					recData = recipient.getContentStream(privateKey, 
							  NebraskaConstants.SECURITY_PROVIDER);
		          } catch (NoSuchProviderException e) {
		        	  throw new NebraskaCryptoException(e);
		          } catch (CMSException e) {
		        	  throw new NebraskaCryptoException(e);
		          }
		          processSignedData(recData.getContentStream(), outStream);
		          break;
	          }
	      }
	}

	public void processSignedData(InputStream signedContentStream, OutputStream outStream) throws NebraskaCryptoException, NebraskaFileException {
		// TODO Auto-generated method stub
		CMSSignedDataParser parser;
		try {
			parser = new CMSSignedDataParser(signedContentStream);
		} catch (CMSException e) {
			throw new NebraskaCryptoException(e);
		}

		CMSTypedStream signedContent = parser.getSignedContent();
		System.out.println(signedContent.getContentType());
		
		InputStream contentStream = signedContent.getContentStream();
		
		try {
			byte[] buffer = new byte[1024];
			int len;
			while((len = contentStream.read(buffer)) > 0)
			{
				outStream.write(buffer, 0, len);
			}
			outStream.flush();
		
		} catch (IOException e) {
			throw new NebraskaFileException(e);
		}

//		try {
//			signedContent.drain();
//		} catch (IOException e) {
//			throw new NebraskaFileException(e);
//		}

		CertStore certs = null;
		SignerInformationStore signerInformation = null;
		try {
			certs = parser.getCertificatesAndCRLs(NebraskaConstants.CERTSTORE_TYPE, NebraskaConstants.SECURITY_PROVIDER);
			signerInformation = parser.getSignerInfos();
		} catch (NoSuchAlgorithmException e) {
      	  throw new NebraskaCryptoException(e);
		} catch (NoSuchProviderException e) {
      	  throw new NebraskaCryptoException(e);
		} catch (CMSException e) {
      	  throw new NebraskaCryptoException(e);
		}

		Collection<?> signerColl = signerInformation.getSigners();
		Iterator<?> signerIterator = signerColl.iterator();

		while (signerIterator.hasNext())
		{
			SignerInformation signer = (SignerInformation)signerIterator.next();
			Collection<?> certCollection;
			try {
				certCollection = certs.getCertificates(signer.getSID());
			} catch (CertStoreException e) {
	      	  throw new NebraskaCryptoException(e);
			}

			Iterator<?> certIterator = certCollection.iterator();
			X509Certificate cert = (X509Certificate)certIterator.next();

			String subject = cert.getSubjectX500Principal().getName();
			String issuer = cert.getIssuerX500Principal().getName();

			boolean verified;
			try {
				verified = signer.verify(cert, NebraskaConstants.SECURITY_PROVIDER);
			} catch (CertificateExpiredException e) {
		      	  throw new NebraskaCryptoException(e);
			} catch (CertificateNotYetValidException e) {
		      	  throw new NebraskaCryptoException(e);
			} catch (NoSuchAlgorithmException e) {
		      	  throw new NebraskaCryptoException(e);
			} catch (NoSuchProviderException e) {
		      	  throw new NebraskaCryptoException(e);
			} catch (CMSException e) {
		      	  throw new NebraskaCryptoException(e);
			}
			System.out.println("verify returns: " + verified);
		}

	}

}
