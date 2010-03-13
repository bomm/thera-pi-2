package encode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import nebraska.BCStatics;
import nebraska.BCStatics3;
import nebraska.Constants;

import org.bouncycastle.cms.CMSEnvelopedDataGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import sun.misc.IOUtils;

public class CreatePKCS7 {
	PrivateKey privateKey;
	String alias;
	String aliasRecipient;
	String password;
	File filein;
	File fileout;
	byte[] origBytes;
	byte[] encodedBytes;
	KeyStore store;
	public CreatePKCS7(String aliasRecipient,String aliasEncoder,String password,KeyStore store){
		this.password = password;
		this.alias = aliasEncoder;
		this.aliasRecipient = aliasRecipient;
		this.store = store;
	}

	public byte[] signAndGetBytes(byte[] data) throws GeneralSecurityException, CMSException, IOException {
			BCStatics.providerTest();
		  //Security.addProvider(new BouncyCastleProvider());
		  CMSSignedDataGenerator generator = new CMSSignedDataGenerator();
		  generator.addSigner(getPrivateKey(), (X509Certificate) getCertificate(),
		      CMSSignedDataGenerator.DIGEST_SHA1);

		  generator.addCertificatesAndCRLs(getCertStore());
		  CMSProcessable content = new CMSProcessableByteArray(data);

		  CMSSignedData signedData = generator.generate(content, true, "BC");
		  return signedData.getEncoded().clone();
		  
		}
	public CMSProcessable signAndGetContent(byte[] data) throws GeneralSecurityException, CMSException, IOException {
		BCStatics.providerTest();
	  //Security.addProvider(new BouncyCastleProvider());
	  CMSSignedDataGenerator generator = new CMSSignedDataGenerator();
	  generator.addSigner(getPrivateKey(), (X509Certificate) getCertificate(),
	      CMSSignedDataGenerator.DIGEST_SHA1);

	  generator.addCertificatesAndCRLs(getCertStore());
	  CMSProcessable content = new CMSProcessableByteArray(data);

	  CMSSignedData signedData = generator.generate(content, true, "BC");
	  return signedData.getSignedContent();
	  
	}

	private X509Certificate getCertificate() throws KeyStoreException, GeneralSecurityException, IOException{
		return (X509Certificate) store.getCertificate(this.alias);
	}
/**
 * @throws IOException *************************************************/
	private CertStore getCertStore() throws GeneralSecurityException, IOException {
		  ArrayList<Certificate> list = new ArrayList<Certificate>();
		  X509Certificate ownCert = (X509Certificate) store.getCertificate(this.alias);
		  X509Certificate rootCACert = BCStatics3.getRootCACert(store);
		  X509Certificate intermediateCert = BCStatics3.getIntermediateCert(store);
		  list.add(ownCert);
		  list.add(intermediateCert);
		  list.add(rootCACert);
		  System.out.println("Die Kette beinhaltet "+list.size()+" Zertifikate");
		  return CertStore.getInstance("Collection", new CollectionCertStoreParameters(list), "BC");
		}
		    
		private PrivateKey getPrivateKey() throws GeneralSecurityException, IOException {
		  if (this.privateKey == null) {
		     this.privateKey = initalizePrivateKey();
		  }
		  return this.privateKey;
		}

		private PrivateKey initalizePrivateKey() throws GeneralSecurityException, IOException {
		   return (PrivateKey) store.getKey(this.alias, Constants.PRAXIS_KS_PW.toCharArray());
		}
/*******************************************/

/*******************************************/		

}
