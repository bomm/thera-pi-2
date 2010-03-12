package org.thera_pi.nebraska;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Iterator;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.cms.CMSEnvelopedDataParser;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSTypedStream;
import org.bouncycastle.cms.RecipientId;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;

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
	protected NebraskaDecryptor(Nebraska nebraska) throws NebraskaCryptoException {
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
	      
	      while (it.hasNext())
	      {
	          RecipientInformation   recipient = (RecipientInformation)it.next();
	          RecipientId rid = recipient.getRID();
	          String issuer = rid.getIssuer().getName();
	          BigInteger serial = rid.getSerialNumber();
	          
	          if(issuer.equals(this.issuer) && serial.equals(this.serial))
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
		          processDataStream(recData.getContentStream());
		          break;
	          }
	      }
	}

	private void processDataStream(InputStream contentStream) {
		// TODO Auto-generated method stub
		
	}

}
