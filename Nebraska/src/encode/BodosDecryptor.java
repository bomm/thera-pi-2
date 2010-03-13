package encode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Iterator;

import javax.security.auth.x500.X500Principal;

import nebraska.NebraskaTestPanel;

import org.bouncycastle.cms.CMSEnvelopedDataParser;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedDataParser;
import org.bouncycastle.cms.CMSTypedStream;
import org.bouncycastle.cms.RecipientId;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.jce.X509Principal;

public class BodosDecryptor {

	private NebraskaTestPanel nebraska;
	private X509Certificate certificate;
	private PrivateKey privateKey;
	private String issuer;
	private BigInteger serial;

	/**
	 * Create a Nebraska decryptor for self
	 * 
	 * @param nebraskaTestPanel reference to Nebraska object that contains the key store
	 * @throws NebraskaCryptoException 
	 */
	public BodosDecryptor(NebraskaTestPanel nebraskaTestPanel, KeyStore store,PrivateKey key) throws Exception {
		this.nebraska = nebraskaTestPanel;
		try {
			certificate = (X509Certificate) store.getCertificate("IK540840108");
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
		privateKey = key;
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
	public void decrypt(InputStream inStream, OutputStream outStream) throws Exception {
		CMSEnvelopedDataParser parser;
	
			parser = new CMSEnvelopedDataParser(inStream);
	
		RecipientInformationStore  recipients = parser.getRecipientInfos();
	      Collection<?>  c = recipients.getRecipients();
	      Iterator<?> it = c.iterator();
	      X500Principal myPrincipal = certificate.getSubjectX500Principal(); 
	      //Principal myPrincipal = new Principal("DE");
	      while (it.hasNext())
	      {
	          RecipientInformation   recipient = (RecipientInformation)it.next();
	          RecipientId rid = recipient.getRID();
	          String issuer = rid.getIssuer().getName();
	          System.out.println("Issuer = "+issuer);
	          BigInteger serial = rid.getSerialNumber();
	          X500Principal receiverPrincipal = rid.getIssuer();
	          //NebraskaPrincipal receiverPrincipal = new NebraskaPrincipal(issuer);
	          System.out.println("myPrincipal = "+myPrincipal+" / receiverPrincipa = "+receiverPrincipal);
	          if(this.serial.equals(serial))
	        	  System.out.println("Durchlauf der Schleife");
	          //if(myPrincipal.equals(receiverPrincipal) && this.serial.equals(serial))
	          {
		          CMSTypedStream recData=null;
		          try {
					recData = recipient.getContentStream(privateKey, 
							  "BC");
		          } catch (NoSuchProviderException e) {
		        	  
		          } catch (CMSException e) {
		        	  
		          }
		          processSignedData(recData.getContentStream(), outStream);
		          break;
	          }
	      }
	}

	private void processSignedData(InputStream signedContentStream, OutputStream outStream) throws Exception {
		// TODO Auto-generated method stub
		CMSSignedDataParser parser;
	
		parser = new CMSSignedDataParser(signedContentStream);
	
		CMSTypedStream signedContent = parser.getSignedContent();
		System.out.println(signedContent.getContentType());
		
		InputStream contentStream = signedContent.getContentStream();
		System.out.println("Bytes im ContentStream = "+signedContent.getContentStream().available());
		
		try {
			byte[] buffer = new byte[1024];
			int len;
			while((len = contentStream.read(buffer)) > 0)
			{
				outStream.write(buffer, 0, len);
			}
			outStream.flush();
		
		} catch (IOException e) {
			
		}

		try {
			signedContent.drain();
		} catch (IOException e) {
			
		}

		CertStore certs = null;
		SignerInformationStore signers = null;
		try {
			certs = parser.getCertificatesAndCRLs("Collection", "BC");
			signers = parser.getSignerInfos();
		} catch (NoSuchAlgorithmException e) {
      	 
		} catch (NoSuchProviderException e) {
      	 
		} catch (CMSException e) {
      	 
		}

		Collection<?> c = signers.getSigners();
		Iterator<?> it = c.iterator();

		while (it.hasNext())
		{
			SignerInformation signer = (SignerInformation)it.next();
			Collection<?> certCollection = null;
			try {
				certCollection = certs.getCertificates(signer.getSID());
			} catch (CertStoreException e) {
	     
			}

			Iterator<?> certIt = certCollection.iterator();
			X509Certificate cert = (X509Certificate)certIt.next();

			try {
				System.out.println("verify returns: " + signer.verify(cert, "BC"));
			} catch (CertificateExpiredException e) {

			} catch (CertificateNotYetValidException e) {

			} catch (NoSuchAlgorithmException e) {

			} catch (NoSuchProviderException e) {

			} catch (CMSException e) {

			}
		}

	}

}
