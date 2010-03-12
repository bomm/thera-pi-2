package encode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import nebraska.BCStatics;
import nebraska.BCStatics2;
import nebraska.BCStatics3;
import nebraska.Constants;
import nebraska.FileStatics;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.DEREncodable;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSEnvelopedDataGenerator;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSProcessableFile;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.jce.PKCS7SignedData;

import sun.security.pkcs.ContentInfo;
import utils.NUtils;

public class EncUtils {
	
	 public static byte[] doNextEncTest4(String inFile,String kkAlias) throws Exception{
		 
		 X509Certificate praxiscert = null; //eigenes Zertifikat
		 X509Certificate certRecipient = null; // Zertifikat des Empfängers
		 X509Certificate certRoot = null; 
		 BCStatics.providerTest();
		  
		 // den PrivKey und das Zertifikat des IKK-Bundesverbandes aus dem Keystore holen
		 KeyStore store = BCStatics2.loadStore(Constants.KEYSTORE_DIR+File.separator+Constants.PRAXIS_OU_ALIAS.replace("IK", ""), Constants.PRAXIS_KS_PW);
		 praxiscert = (X509Certificate) store.getCertificate(Constants.PRAXIS_OU_ALIAS);
		 PrivateKey privkey = (PrivateKey) store.getKey(Constants.PRAXIS_OU_ALIAS, Constants.PRAXIS_KS_PW.toCharArray());
		 //certRecipient = (X509Certificate) store.getCertificate(kkAlias); IKK-Bundesverband 
		 certRecipient = (X509Certificate) praxiscert; // in der Hoffnung ich könnte es wieder entschlüsseln
		 certRoot = BCStatics3.getRootCACert(store);
		 /******************Signed Data*************************/
		 CMSSignedDataGenerator sgen = new CMSSignedDataGenerator();
		 Certificate[] chain = new Certificate[]{praxiscert,certRoot};
		 CertStore certsAndCRLs = CertStore.getInstance("Collection", new CollectionCertStoreParameters(Arrays.asList(chain)),"BC");
		 sgen.addSigner(privkey,praxiscert,CMSSignedDataGenerator.DIGEST_SHA1);
		 sgen.addCertificatesAndCRLs(certsAndCRLs);
		 CMSProcessable dataToSign = new CMSProcessableFile(new File(inFile));
		 CMSSignedData signed = sgen.generate(dataToSign,true,"BC");
		 FileStatics.BytesToFile(signed.getEncoded(),new File(inFile+".sign") );
		 CMSProcessable process = signed.getSignedContent();
		 ByteArrayOutputStream out = new ByteArrayOutputStream();
		 process.write(out);
		 out.flush();
		 out.close();
		 FileStatics.BytesToFile(out.toByteArray(),new File(inFile+".signonly") );
		 FileStatics.BytesToFile(signed.getEncoded(),new File(inFile+".signall") );
		 /******************Enveloped Data*************************/
		 CMSEnvelopedDataGenerator gen = new CMSEnvelopedDataGenerator();
		 gen.addKeyTransRecipient(certRecipient); //IKK
		 //CMSProcessable process = new CMSProcessableByteArray(signed.getSignedContent());
		 CMSEnvelopedData envelopedData = gen.generate(signed.getSignedContent(),CMSEnvelopedDataGenerator.DES_EDE3_CBC ,"BC");

		 return envelopedData.getEncoded();
		 }
/******************************/
	public static void dec1(String inFile) throws Exception{
		BCStatics.providerTest();
		byte[] inBytes = FileStatics.BytesFromFile(new File(inFile));
		 
		CMSSignedDataGenerator cms = new CMSSignedDataGenerator();
		CMSSignedData signedData = cms.generate(new CMSProcessableByteArray(inBytes), "BC");
		
		
		CMSProcessableByteArray cpb = (CMSProcessableByteArray) signedData.getSignedContent();
		byte[] signedContent = (byte[]) cpb.getContent();
		//System.out.println(new String(signedContent));

		CMSSignedData cms7 = new CMSSignedData(signedData.getEncoded());
        CertStore certs = cms7.getCertificatesAndCRLs("Collection", "BC"); 
        SignerInformationStore signers = cms7.getSignerInfos();
        System.out.println("Anzahl Signer = "+signers.size());
        //In that line its creating the nullpointerException*

        Collection c = signers.getSigners();
        Iterator it = c.iterator();
        int verified = 0;
        X509Certificate cert = null;
        //String signerInfoOID = null;
        while (it.hasNext()) {
        	System.out.println("in Signer Verifizierung");
            SignerInformation signer = (SignerInformation) it.next();
            //signerInfoOID = signer.getEncryptionAlgOID();
            Collection certCollection = certs.getCertificates(signer.getSID());
            Iterator certIt = certCollection.iterator();
            cert = (X509Certificate) certIt.next();
            if (signer.verify(cert.getPublicKey(), "BC")) {
            	System.out.println( "publicKey Verifizierung erfolgreich");
            }else {
            	System.out.println( "publicKey Verifizierung fehlgeschlagen");
            }
        }

	}         
	
	private static byte[] encodeObj(DEREncodable obj) throws IOException {
		            if (obj != null) {
		                    ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		                    ASN1OutputStream aOut = new ASN1OutputStream(bOut);
		
		                    aOut.writeObject(obj);
		
		                    return bOut.toByteArray();
		                }
		
		                return null;
		            }
   
	 private static DERObject makeObj(byte[]  encoding) throws IOException{
	            if (encoding == null)
	            {
	                return null;
	            }

	            ASN1InputStream         aIn = new ASN1InputStream(encoding);

	            return aIn.readObject();
	        }



}
