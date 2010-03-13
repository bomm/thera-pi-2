package encode;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

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
import org.bouncycastle.cms.RecipientId;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;
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
		 PrivateKey privKey = null;
		 BCStatics.providerTest();
		 byte[] encData = null;
		 
		 // den PrivKey und das Zertifikat des IKK-Bundesverbandes aus dem Keystore holen
		 KeyStore store = BCStatics2.loadStore(Constants.KEYSTORE_DIR+File.separator+Constants.PRAXIS_OU_ALIAS.replace("IK", ""), Constants.PRAXIS_KS_PW);
		 praxiscert = (X509Certificate) store.getCertificate(Constants.PRAXIS_OU_ALIAS);
		 PrivateKey privkey = (PrivateKey) store.getKey(Constants.PRAXIS_OU_ALIAS, Constants.PRAXIS_KS_PW.toCharArray());
		 //certRecipient = (X509Certificate) store.getCertificate(kkAlias); IKK-Bundesverband 
		 certRecipient = (X509Certificate) praxiscert; // in der Hoffnung ich könnte es wieder entschlüsseln
		 certRoot = BCStatics3.getRootCACert(store);
		 encData = FileStatics.BytesFromFile(new File(inFile));
		 /******************Signed Data*************************/
		 CreatePKCS7 pkcs = new CreatePKCS7(kkAlias,
					Constants.PRAXIS_OU_ALIAS,
					Constants.PRAXIS_KS_PW,
					store);
		 byte[] signedBytes = pkcs.signAndGetBytes(encData);

		 /******************Enveloped Data*************************/
		 CMSProcessable bout = new CMSProcessableByteArray(signedBytes);
		 CMSEnvelopedDataGenerator gen = new CMSEnvelopedDataGenerator();
		 gen.addKeyTransRecipient(praxiscert); //540840108
		 //gen.addKeyTransRecipient(certRecipient); //IKK-BV

		 CMSEnvelopedData envelopedData = gen.generate(bout/*signed.getSignedContent()*/,CMSEnvelopedDataGenerator.DES_EDE3_CBC ,"BC");

		 byte[] keyId = recipientKeyCheck(envelopedData);
		 System.out.println("KeyBytes = "+keyId);
		 BigInteger bSerial = recipientSerialCheck(envelopedData);
		 System.out.println("Recipient-Cert SerialNumber = "+bSerial.toString());
		 return envelopedData.getEncoded();
		 }
	private static byte[] recipientKeyCheck(CMSEnvelopedData envelopedData) throws Exception{
		 RecipientInformationStore infos = envelopedData.getRecipientInfos();
		 Collection<?> colinfos = infos.getRecipients();
		 System.out.println("Anzahl Nachrichtenempfänger = "+colinfos.size());
		 Iterator<?> itinfos = colinfos.iterator();
		 while(itinfos.hasNext()){
			 RecipientInformation inform = ((RecipientInformation)itinfos.next());
			 RecipientId iRID = inform.getRID();
			 return  iRID.getKeyIdentifier();
		 }
		 return null;
	}
	private static BigInteger recipientSerialCheck(CMSEnvelopedData envelopedData) throws Exception{
		 RecipientInformationStore infos = envelopedData.getRecipientInfos();
		 Collection<?> colinfos = infos.getRecipients();
		 System.out.println("Anzahl Nachrichtenempfänger = "+colinfos.size());
		 Iterator<?> itinfos = colinfos.iterator();
		 while(itinfos.hasNext()){
			 RecipientInformation inform = ((RecipientInformation)itinfos.next());
			 RecipientId iRID = inform.getRID();
			 return  iRID.getSerialNumber();
		 }
		 return BigInteger.valueOf(Long.valueOf("-1"));
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
	 

	 public static void encrypt2(PublicKey publicKey/*String publicKeyFile*/, String inputFile, String outputFile)
	 {
	     try
	     {
	         KeyGenerator keygen = KeyGenerator.getInstance("AES");
	         SecureRandom random = new SecureRandom();
	         keygen.init(random);
	         SecretKey key = keygen.generateKey();
	  
	         // Wrapp with public key
	         /*
	         ObjectInputStream keyIn = new ObjectInputStream(new FileInputStream(publicKeyFile));
	         Key publicKey = (Key) keyIn.readObject();
	         keyIn.close();
	  		*/	
	         Cipher cipher = Cipher.getInstance("RSA");
	         cipher.init(Cipher.WRAP_MODE, publicKey);
	         byte[] wrappedKey = cipher.wrap(key);
	         DataOutputStream out = new DataOutputStream(new FileOutputStream(outputFile));
	         out.writeInt(wrappedKey.length);
	         out.write(wrappedKey);
	  
	         InputStream in = new FileInputStream(inputFile);
	         cipher = Cipher.getInstance("AES");
	         cipher.init(Cipher.ENCRYPT_MODE, key);
	         // seperate method in Crypt.java
	         crypt(in, out, cipher);
	         in.close();
	         out.close();
	     }
	     catch (Exception e)
	     {
	    	 e.printStackTrace();
	     }
	 }	 

	 public static byte[] encrypt1(PublicKey publicKey, String inputFile, String outputFile)
	 {
		 DataOutputStream out = null;
		 ByteArrayOutputStream bout = null;
		 try
	     {
			 KeyGenerator keygen = KeyGenerator.getInstance("AES");
			
	         SecureRandom random = new SecureRandom();
	         
	         keygen.init(random);
	         
	         SecretKey key = keygen.generateKey();

	         bout = new ByteArrayOutputStream();
	         out = new DataOutputStream(bout);

	         Cipher cipher = Cipher.getInstance("RSA");
	         cipher.init(Cipher.WRAP_MODE, publicKey);
	         byte[] wrappedKey = cipher.wrap(key);


	         bout = new ByteArrayOutputStream(); 

	         out = new DataOutputStream(bout);
	         out.writeInt(wrappedKey.length);
	         out.write(wrappedKey);
	  		
	         
	         byte[] inBytes1 = FileStatics.BytesFromFile(new File(inputFile));
	         byte[] inBytes = new byte[inBytes1.length+(inBytes1.length%16)];
	         for(int i = 0;i<inBytes1.length;i++){
	        	 inBytes[i] = inBytes1[i];
	         }
	         for(int i = inBytes1.length;i<inBytes.length;i++){
	        	 inBytes[i] = 0x00;
	         }
	         
	         int inputSize = inBytes.length;

	         cipher = Cipher.getInstance("AES");

	         cipher.init(Cipher.ENCRYPT_MODE, key,random);
	         
	         int blockSize = cipher.getBlockSize();
	         System.out.println("Encoding-Blockgröße = "+blockSize);
	         
	         int outputSize = cipher.getOutputSize(blockSize);

	         byte[] outBytes = cipher.doFinal(inBytes);
	         System.out.println(new String(outBytes));
	         out.write(outBytes);
	         out.flush();
	         System.out.println("Größe der Encrypted-Datei = "+out.size()+" Bytes");

	         out.close();
		     bout.flush();
		     bout.close();

	     }catch (Exception e){
	    	 e.printStackTrace();
	     }
	     return bout.toByteArray();

	 }


	 public static void decrypt1(PrivateKey privateKey, String inputFile, String outputFile)
	 {
	     try
	     {
	         DataInputStream in = new DataInputStream(new FileInputStream(inputFile));
	         

	         int length = in.readInt();
	         byte[] wrappedKey = new byte[length];
	         int keySize = in.read(wrappedKey, 0, length);
	         System.out.println("Gelesen 1 ="+length+" Gelesen 2 = "+keySize);
	         
	         Cipher cipher = Cipher.getInstance("RSA");
	         cipher.init(Cipher.UNWRAP_MODE, privateKey);
	         
	         Key key = cipher.unwrap(wrappedKey, "AES", Cipher.SECRET_KEY);
	         
	         /**********************************/
	         byte[] byteFully = FileStatics.BytesFromFile(new File(inputFile));
	         int offset = length;
	         byte[] inByte = new byte[(byteFully.length-offset)+((byteFully.length-offset)%16)];	                                
	       
	         int i = 0; 
	         for(i = length;i<byteFully.length;i++){
	        	 inByte[i-offset] = byteFully[i];
	         }
	         for(int i2 = i;i2 < inByte.length;i2++){
	        	 inByte[i2] = 0x00;
	         }

	         int inputSize = inByte.length;
	         System.out.println("Länge des inputStreams 1 = "+inputSize);

	         cipher = Cipher.getInstance("AES");
	        
	         cipher.init(Cipher.DECRYPT_MODE, key);
	         int blockSize = cipher.getBlockSize();
	         System.out.println("Decoding-Blockgröße = "+blockSize);
	         
	         int outputSize = cipher.getOutputSize(inputSize);
	         System.out.println("Auffüllen muit "+(inputSize % 16)+" Bytes");
	         byte[] outByte = new byte[blockSize];
	         try{
	        	 outByte = cipher.doFinal(inByte);
	         }catch(Exception ex){
	        	 ex.printStackTrace();
	         }
	         System.out.println(new String(outByte));
	         FileStatics.BytesToFile(outByte, new File(outputFile));


	         in.close();

	     }
	     catch (Exception e){
	    	 e.printStackTrace();
	     }
	 }
	 
	 public static void decrypt2(PrivateKey privateKey, String inputFile, String outputFile)
	 {
	     try
	     {
	         DataInputStream in = new DataInputStream(new FileInputStream(inputFile));
	         int length = in.readInt();
	         byte[] wrappedKey = new byte[length];
	         in.read(wrappedKey, 0, length);

	         Cipher cipher = Cipher.getInstance("RSA");
	         cipher.init(Cipher.UNWRAP_MODE, privateKey);
	         Key key = cipher.unwrap(wrappedKey, "AES", Cipher.SECRET_KEY);

	         OutputStream out = new FileOutputStream(outputFile);
	         cipher = Cipher.getInstance("AES");
	         cipher.init(Cipher.DECRYPT_MODE, key);
	  
	         crypt(in, out, cipher);
	         in.close();
	         out.close();
	     }
	     catch (Exception e)
	     {
	       e.printStackTrace();
	     }
	 }	 
	 
	 public static void crypt(InputStream in, OutputStream out,Cipher cipher) throws Exception{
		      int blockSize = cipher.getBlockSize();
		      int outputSize = cipher.getOutputSize(blockSize);
		      byte[] inBytes = new byte[blockSize];
		      byte[] outBytes = new byte[outputSize];       
		      int inLength = 0;;
		      boolean more = true;
		      while (more){
		         inLength = in.read(inBytes);
		         if (inLength == blockSize)
		         {
		            int outLength
		               = cipher.update(inBytes, 0, blockSize, outBytes);
		            out.write(outBytes, 0, outLength);
		         }
		         else more = false;
		      }
		      if (inLength > 0)
		         outBytes = cipher.doFinal(inBytes, 0, inLength);
		      else
		         outBytes = cipher.doFinal();
		      out.write(outBytes);
		   }    public static final int KEYSIZE = 128;
	 


}
