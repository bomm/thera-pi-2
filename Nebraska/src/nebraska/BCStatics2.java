package nebraska;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.CertStore;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DEREncodable;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.pkcs.CertificationRequestInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.Attribute;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.x509.X509V3CertificateGenerator;

import utils.DatFunk;
import utils.NUtils;

public class BCStatics2 {
	
	public static void createKeyStore(String name,String passw,boolean isroot,Vector<String> praxvec,Vector<String>cavec,KeyPair kp) throws Exception{
		providerTest();
		KeyStore store;
		X509Certificate cert = null;
		KeyPair keypair = kp;//BCStatics.generateRSAKeyPair();
		if(isroot){
			cert = generateRootCert(keypair,cavec);
		}else{
			cert = generateSelfSignedV3Certificate(keypair,praxvec,cavec);
			//cert = generateV3Certificate(keypair,praxvec,cavec);
		}
		storePrivateToPem(keypair,name);
		storePublicToPem(keypair,name);
		FileStatics.BytesToFile(cert.getEncoded(),new File(NebraskaTestPanel.keystoreDir+ File.separator +name));
		certToFile(cert,cert.getEncoded(),NebraskaTestPanel.keystoreDir+ File.separator +name);
		store = KeyStore.getInstance("BCPKCS12","BC");
		store.load(null,null);
		String alias = (isroot ? cavec.get(0) : praxvec.get(0));
		if(isroot){
			store.setCertificateEntry(alias, cert);
			X509Certificate[] chain = new X509Certificate[1];
			chain[0] = (X509Certificate) cert;
			//store.setKeyEntry(alias,keypair.getPrivate(),passw.toCharArray(),(java.security.cert.Certificate[]) chain);
		}
		//String alias = (isroot ? cavec.get(0) : "keys");

		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		store.store(bOut,passw.toCharArray());
		FileStatics.BytesToFile(bOut.toByteArray(),new File(NebraskaTestPanel.keystoreDir + File.separator +name+".p12"));
		bOut.close();
		System.out.println("\n*********************Zertifikat Beginn************************");
		System.out.println(cert);
		System.out.println("*********************Zertifikat Ende***************************\n");
	}
	
	public static X509Certificate generateRootCert(KeyPair pair,Vector<String>vecca) throws InvalidKeyException, NoSuchProviderException, SecurityException, SignatureException{
		providerTest();
		X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
		certGen.setSerialNumber(BigInteger.valueOf(1));
		certGen.setIssuerDN(new X500Principal("O="+vecca.get(3)+",C=DE"));
		certGen.setNotBefore(new Date(BCStatics2.certifikatsDatum(DatFunk.sHeute(), 0)));
		certGen.setNotAfter(new Date(BCStatics2.certifikatsDatum(DatFunk.sHeute(), 3)));
		certGen.setSubjectDN(new X500Principal("CN="+vecca.get(2)+",OU="+vecca.get(0)+"," +
		"OU="+vecca.get(1)+", O="+vecca.get(3)+",C=DE"));
		certGen.setPublicKey(pair.getPublic());
		certGen.setSignatureAlgorithm("SHA1WithRSAEncryption");

		certGen.addExtension(X509Extensions.BasicConstraints,false,
				new BasicConstraints(true));
		certGen.addExtension(X509Extensions.KeyUsage,true,
				new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));
		certGen.addExtension(X509Extensions.ExtendedKeyUsage,true,
				new ExtendedKeyUsage(KeyPurposeId.id_kp_serverAuth));
		X509Certificate cert = certGen.generateX509Certificate(pair.getPrivate(),"BC"); 
		return cert;
	}
	public static X509Certificate generateV3Certificate(KeyPair pair,Vector<String>praxvec,Vector<String>cavec) 
	throws Exception{
		providerTest();
		X509V3CertificateGenerator certGen = new X509V3CertificateGenerator(); 
		certGen.setSerialNumber(BigInteger.valueOf(1));
		certGen.setIssuerDN(new X500Principal("O="+cavec.get(3)+",C=DE"));
		certGen.setNotBefore(new Date(BCStatics2.certifikatsDatum(DatFunk.sHeute(), 0)));
		certGen.setNotAfter(new Date(BCStatics2.certifikatsDatum(DatFunk.sHeute(), 3)));
		certGen.setSubjectDN(new X500Principal("CN="+praxvec.get(2)+",OU="+praxvec.get(0)+"," +
		"OU="+praxvec.get(1)+", O="+cavec.get(3)+",C=DE"));
		certGen.setPublicKey(pair.getPublic());
		certGen.setSignatureAlgorithm("SHA1WithRSAEncryption");
		/*
		certGen.addExtension(X509Extensions.BasicConstraints,true,
				new BasicConstraints(true));
		certGen.addExtension(X509Extensions.KeyUsage,true,
				new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));
		certGen.addExtension(X509Extensions.ExtendedKeyUsage,true,
				new ExtendedKeyUsage(KeyPurposeId.id_kp_serverAuth));
			
		Date d = new Date(BCStatics2.certifikatsDatum(DatFunk.sHeute(), 0));
		Time time = new Time(d.getTime());
		 */
		X509Certificate cert = certGen.generateX509Certificate(pair.getPrivate(),"BC");
		cert.checkValidity();
		cert.verify(pair.getPublic(), "BC");
		return cert; 
	}
	/*********************************/
	public static PKCS10CertificationRequest generateRequest(KeyPair pair,Vector<String>praxvec,Vector<String>cavec) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException{
		providerTest();
		System.out.println("Pbulic Key SHA-1 Hash = "+getSHA1fromByte(pair.getPublic().getEncoded()));
		System.out.println("  Public Key MD5 Hash = "+getMD5fromByte(pair.getPublic().getEncoded()));
 
		return new PKCS10CertificationRequest("SHA1withRSA",
				new X500Principal("CN="+praxvec.get(2)+"," +
				"OU="+praxvec.get(0)+",OU="+praxvec.get(1)+","+
				"O="+cavec.get(3)+",C=DE"),
				pair.getPublic(),
				null,
				pair.getPrivate() );
	}
	/*********************************/	
	public static X509Certificate[] makeReply(String csrPfad,String keystorefile,String alias,String passw) throws Exception{
		providerTest();
		/******Zuerst den Zertifikatsrequest �ffnen******/
		ASN1InputStream ain = new ASN1InputStream(new FileInputStream(csrPfad));
		DERObject derob = ain.readObject();
		PKCS10CertificationRequest csr = new PKCS10CertificationRequest(
            	  derob.getDEREncoded() );
		CertificationRequestInfo csrInfo = csr.getCertificationRequestInfo();
		/*
		System.out.println(csr.getSignature());
		System.out.println(csr.getPublicKey("BC"));
		System.out.println(csr.getSignatureAlgorithm().getParameters());
		System.out.println(csrInfo.getSubject());
		System.out.println(csrInfo.getSubjectPublicKeyInfo());
		*/
		/****Unterschrift****/
		
		String pfad = NebraskaTestPanel.keystoreDir + File.separator  + keystorefile + ".p12" ;
		KeyStore store = KeyStore.getInstance("BCPKCS12","BC");
		FileInputStream fin = new FileInputStream(new File(pfad));
		store.load(fin, passw.toCharArray());
		fin.close();
		store = loadStore(NebraskaTestPanel.keystoreDir + File.separator + keystorefile,NebraskaTestPanel.caPassw);
		X509Certificate rootCert = (X509Certificate) store.getCertificate(alias);
		//X509Certificate rootCert = readSingleCert(NebraskaTestPanel.keystoreDir);
		PrivateKey rootPkey = getPrivateFromPem(NebraskaTestPanel.keystoreDir + File.separator + keystorefile);

		X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
		certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
		X500Principal xprinc1 = rootCert.getSubjectX500Principal();
		System.out.println(xprinc1);
		String[] princ = xprinc1.toString().split(",");
		X500Principal xprinc2 = new X500Principal(princ[3]+","+princ[4]); 
		certGen.setIssuerDN(xprinc2);
		certGen.setNotBefore(new Date(BCStatics2.certifikatsDatum(DatFunk.sHeute(), 0)));
		certGen.setNotAfter(new Date(BCStatics2.certifikatsDatum(DatFunk.sHeute(), 3)));
		certGen.setSubjectDN(csrInfo.getSubject());
		certGen.setPublicKey(csr.getPublicKey("BC"));
		certGen.setSignatureAlgorithm("SHA1WithRSAEncryption");
		/*
		certGen.addExtension(X509Extensions.AuthorityKeyIdentifier,false,
				new AuthorityKeyIdentifierStructure(rootCert));
		certGen.addExtension(X509Extensions.SubjectKeyIdentifier,false,
				new SubjectKeyIdentifierStructure(csr.getPublicKey("BC")));
		certGen.addExtension(X509Extensions.BasicConstraints,false,
				new BasicConstraints(true));
		certGen.addExtension(X509Extensions.KeyUsage,true,
				new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));
		certGen.addExtension(X509Extensions.ExtendedKeyUsage,true,
				new ExtendedKeyUsage(KeyPurposeId.id_kp_serverAuth));
		ASN1Set attributes = csr.getCertificationRequestInfo().getAttributes();
		System.out.println("Attribute ist "+attributes);
		if(attributes != null){
			for(int i = 0; i != attributes.size();i++){
				Attribute attr = Attribute.getInstance(attributes.getObjectAt(i));
				if(attr.getAttrType().equals(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest)){
					X509Extensions extensions = X509Extensions.getInstance(attr.getAttrValues().getObjectAt(0));
					Enumeration e = extensions.oids();
					while(e.hasMoreElements()){
						DERObjectIdentifier oid = (DERObjectIdentifier)e.nextElement();
						X509Extension ext = extensions.getExtension(oid);
						certGen.addExtension(oid, ext.isCritical() ,ext.getValue().getOctets());
					}
				}
			}
		}
		*/
		X509Certificate issuedCert = certGen.generateX509Certificate(rootPkey);
		System.out.println("IsscuedCert = "+issuedCert);
		System.out.println("CSR-Pfad = "+csrPfad);
		FileStatics.BytesToFile(issuedCert.getEncoded(), new File(csrPfad.replace("p10", "p7c")));
		return  new X509Certificate[] {issuedCert,rootCert};
	}
	/***************************************************************************/
	public static int readMultipleAnnahme(String keystoreDir,String pw,String xalias,boolean checkonly,X509Certificate[] certs) throws Exception{
		final String zertanfang = "-----BEGIN CERTIFICATE-----";
		final String zertende = "-----END CERTIFICATE-----";
		String pfad = keystoreDir;
		String sret = "";
		int certcount = 0;
		if(NebraskaTestPanel.annahmeKeyFile.equals("")){
			final JFileChooser chooser = new JFileChooser("Verzeichnis w�hlen");
	        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
	        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	        final File file = new File(pfad);
	        chooser.setCurrentDirectory(file);
	        chooser.addPropertyChangeListener(new PropertyChangeListener() {
	            public void propertyChange(PropertyChangeEvent e) {
	                if (e.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)
	                        || e.getPropertyName().equals(JFileChooser.DIRECTORY_CHANGED_PROPERTY)) {
	                    final File f = (File) e.getNewValue();
	                }
	            }

	        });
	        chooser.setVisible(true);
	        final int result = chooser.showOpenDialog(null);
	        if (result == JFileChooser.APPROVE_OPTION) {
	            File inputVerzFile = chooser.getSelectedFile();
	            String inputVerzStr = inputVerzFile.getPath();
	            if(inputVerzFile.getName().trim().equals("")){
	            	sret = "";
	            }else{
	            	sret = inputVerzFile.getName().trim();	
	            }
	        }else{
	        	sret = ""; 
	        }
	        if(sret.equals("")){
	        	return certcount;
	        }
	        NebraskaTestPanel.annahmeKeyFile = sret;
		}else{
			sret = NebraskaTestPanel.annahmeKeyFile;
		}
		String serial;
		providerTest();
		FileReader reader;
		int zertifikate = 0;
		int eingelesen = 0;
		reader = new FileReader(pfad+File.separator+sret);
		BufferedReader in
		   = new BufferedReader(reader);//new BufferedReader(new FileReader(pfad+sret));

		StringBuffer buf = new StringBuffer();
		String zeile = "";
		String separator = System.getProperty("line.separator");
		buf.append(zertanfang+separator);
		while(true){
			if( (zeile = in.readLine()) != null){
				if(! zeile.equals("")){
					buf.append(zeile+separator);					
				}else{
					buf.append(zertende+separator);
					if(!checkonly){
						certs[certcount] = makeCertFromPem(keystoreDir,buf);
						//importCertsPEM(keystoreDir,buf,pw,xalias);
					}
					certcount++;
					Thread.sleep(20);
					eingelesen++;
					buf.setLength(0);
					buf.trimToSize();
					buf.append(zertanfang+separator);
				}
			}else{
				break;
			}
		}
		return certcount;
	}
	public static X509Certificate makeCertFromPem(String keystoreDir,StringBuffer buf) throws Exception{
		X509Certificate cert = null;
		File f = new File(keystoreDir+ File.separator +"temp.pem");
		FileWriter writer;
		writer = new FileWriter(f);
		writer.write(buf.toString());
		writer.close();
		FileReader reader = new FileReader(f); 
		PEMReader pemReader = new PEMReader(reader);
		Object o;
		o = pemReader.readObject();
		if (o instanceof X509Certificate){
				cert = (X509Certificate) o;
		}	
		pemReader.close();
		return cert;
	}
	public static void importCertsPEM(String keystoreDir, StringBuffer buf,String pw,String xalias) throws Exception{
		File f = new File(keystoreDir+ File.separator +"temp.pem");
		FileWriter writer;
		writer = new FileWriter(f);
		writer.write(buf.toString());
		writer.close();
		FileReader reader = new FileReader(f); 
		PEMReader pemReader = new PEMReader(reader);
		Object o;
		o = pemReader.readObject();
		if (o instanceof X509Certificate){
				X509Certificate cert = (X509Certificate) o;
				importCertIntoStore(cert,keystoreDir,pw,xalias);
		}	
		pemReader.close();
	}

	public static void importCertIntoStore(X509Certificate cert,String keystore,String pw,String xalias) throws Exception{
		KeyStore store;
		String alias;
		store = KeyStore.getInstance("BCPKCS12","BC");
		InputStream in = new FileInputStream(new File(keystore+ xalias+".p12"));
		store.load(in,pw.toCharArray());
		in.close();
		String subjectDN = cert.getSubjectDN().toString();
		String[] splits = subjectDN.split(",");
		if(splits.length==5){
			alias = splits[3].split("=")[1];
			System.out.println("xalias = "+xalias+" - alias = "+alias);
		} else {
			alias = subjectDN;
		}
			if(!store.containsAlias(alias.trim())){	
				store.setCertificateEntry(alias, cert);
				System.out.println("Zertifikat von "+alias+" der Datenbank "+keystore + File.separator +xalias+".p12"+" hinzugefügt");
				ByteArrayOutputStream bOut = new ByteArrayOutputStream();
				store.store(bOut,pw.toCharArray());
				bOut.close();
				FileStatics.BytesToFile(bOut.toByteArray(), new File(keystore +xalias+".p12"));
			}else{
				System.out.println("Zertifikat von "+alias+" bereits enthalten");
				int frage = JOptionPane.showConfirmDialog(null,"Zertifikat ist bereits in der Datenbank enthalten.\nTrotzdem importieren?","Achtung!",JOptionPane.YES_NO_OPTION);
				if(frage==JOptionPane.YES_OPTION){
					store.setCertificateEntry(alias, cert);
					System.out.println("Zertifikat von "+alias+" der Datenbank "+keystore + File.separator +xalias+".p12"+" hinzugefügt");
					ByteArrayOutputStream bOut = new ByteArrayOutputStream();
					store.store(bOut,pw.toCharArray());
					bOut.close();
					FileStatics.BytesToFile(bOut.toByteArray(), new File(keystore +xalias+".p12"));
				}
				cert.getBasicConstraints();
			
			}	
	}
	
	public static void deleteCertFromStore(String alias,String keystoreFile,String pw) throws Exception{
		KeyStore store;
		providerTest();
		store = KeyStore.getInstance("BCPKCS12","BC");
		InputStream in = new FileInputStream(new File(keystoreFile +".p12"));
		store.load(in,pw.toCharArray());
		in.close();
		Enumeration en = store.aliases();
		if(alias == null){
			while (en.hasMoreElements()){
				String aliases = (String)en.nextElement();
				if(aliases != null){
					if(store.isCertificateEntry(aliases)){
						store.deleteEntry(aliases);
						System.out.println("Store Entry mit alias "+aliases+" wurde gel�scht!");
					}else{
						
					}
				}
			}	
		}else{
			if(store.containsAlias(alias)){
				store.deleteEntry(alias);
			}
		}
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		store.store(bOut,"196205".toCharArray());
		System.out.println("fertig gestored....");
		FileStatics.BytesToFile(bOut.toByteArray(), new File(keystoreFile+".p12"));
		//BCStatics2.storeToFile(keystoreFile+".p12",bOut.toByteArray());
		bOut.close();
	}

	 public static X509Certificate readSingleCert(String xpfad) throws Exception{
		 String pfad = xpfad;
		 String datei = BCStatics2.chooser(pfad);
		 providerTest();
		 if(datei.equals("")){return null;}

			File f = new File(datei);

			FileInputStream in = new FileInputStream(f);
			CertificateFactory fact = CertificateFactory.getInstance("X.509","BC");
			X509Certificate cert = (X509Certificate) fact.generateCertificate(in);
			byte[] encryptCert = cert.getEncoded();
			in.close();
			return cert;
	 }

		public static void readCertReply(String keystoreDir, String keystoreFile, String keystorePassword)
		throws Exception {
		 String pfad = keystoreDir;
		 String datei = chooser(pfad);
		 providerTest();
		 if(datei.equals("")){return;}

		File f = new File(datei);

    	CMSSignedData sd = new CMSSignedData(new FileInputStream(f));
    	CertStore certs = sd.getCertificatesAndCRLs ("Collection", "BC");

    	Collection<?> certColl = certs.getCertificates(null);
   	    for (Iterator<?> certIt = certColl.iterator(); certIt.hasNext(); ) {
   	    	X509Certificate cert = (X509Certificate) certIt.next();
   	    	X500Principal subject = cert.getSubjectX500Principal();
   	    	System.out.println ("Subject: " + subject);
   	    	X500Principal issuer = cert.getIssuerX500Principal();
   	    	System.out.println ("Issuer: " + issuer);
   			BCStatics2.importCertIntoStore(cert,keystoreDir + File.separator +keystoreFile,keystorePassword,"");
    	}
	}

	/*********************************************/
	public static KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException{
		providerTest();
		KeyPairGenerator kpGen= KeyPairGenerator.getInstance("RSA","BC");
		kpGen.initialize(2048, new SecureRandom());
		return kpGen.generateKeyPair();
	}
	public static void providerTest(){
		Provider provBC = Security.getProvider("BC");
		if(provBC==null){
			Security.addProvider(new BouncyCastleProvider());			 
		}
	}
	/*********************************************/
	public static void certToFile(X509Certificate x509Cert,byte[] b,String name) throws IOException{
			File f = new File(name+".p7b");
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(b);
			fos.flush();
			fos.close();
			/*
			f =  new File(name+".pem");
			fos = new FileOutputStream(f);
			fos.write(x509Cert.toString().getBytes());
			fos.flush();
			fos.close();
			*/
			FileWriter file = new FileWriter(new File(name+".pem.pem"));
	        PEMWriter pemWriter = new PEMWriter(file);
	        pemWriter.writeObject(x509Cert);
	        pemWriter.close();
	        file.close();
	 }
	 public static String getSHA1(X509Certificate cert){
		 	byte[] encryptCert;
		 	byte[] dig = null;;
			try {
				encryptCert = cert.getEncoded();
				MessageDigest messageDigest = MessageDigest.getInstance("SHA-1","BC");
				messageDigest.update(encryptCert);
				dig = messageDigest.digest();
			} catch (CertificateEncodingException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (NoSuchProviderException e) {
				e.printStackTrace();
			}
			return NUtils.toHex(dig);
	 }
	 public static String getSHA1fromByte(byte[] b){
		 	byte[] dig = null;;
			try {
				MessageDigest messageDigest = MessageDigest.getInstance("SHA-1","BC");
				messageDigest.update(b);
				dig = messageDigest.digest();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (NoSuchProviderException e) {
				e.printStackTrace();
			}
			return NUtils.toHex(dig);
	 }

	 public static String getMD5(X509Certificate cert){
		 	byte[] encryptCert;
		 	byte[] dig = null;;
			try {
				encryptCert = cert.getEncoded();
				MessageDigest messageDigest = MessageDigest.getInstance("MD5","BC");
				messageDigest.update(encryptCert);
				dig = messageDigest.digest();
			} catch (CertificateEncodingException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (NoSuchProviderException e) {
				e.printStackTrace();
			}
			return NUtils.toHex(dig);
	 }

	 public static String getMD5fromByte(byte[] b){
		 	byte[] dig = null;;
			try {
				MessageDigest messageDigest = MessageDigest.getInstance("MD5","BC");
				messageDigest.update(b);
				dig = messageDigest.digest();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (NoSuchProviderException e) {
				e.printStackTrace();
			}
			return NUtils.toHex(dig);
	 }
		public static X509Certificate generateSelfSignedV3Certificate(KeyPair pair,Vector<String>praxvec,Vector<String>cavec) 
		throws Exception{
			providerTest();
			X509V3CertificateGenerator certGen = new X509V3CertificateGenerator(); 
			certGen.setSerialNumber(BigInteger.valueOf(1));
			certGen.setIssuerDN(new X500Principal("O="+cavec.get(3)+",C=DE"));
			certGen.setNotBefore(new Date(BCStatics2.certifikatsDatum(DatFunk.sHeute(), 0)));
			certGen.setNotAfter(new Date(BCStatics2.certifikatsDatum(DatFunk.sHeute(), 3)));
			certGen.setSubjectDN(new X500Principal("CN="+praxvec.get(2)+",OU="+praxvec.get(0)+"," +
			"OU="+praxvec.get(1)+", O="+cavec.get(3)+",C=DE"));
			certGen.setPublicKey(pair.getPublic());
			certGen.setSignatureAlgorithm("SHA1WithRSAEncryption");
			/*
			certGen.addExtension(X509Extensions.BasicConstraints,true,
					new BasicConstraints(true));
			certGen.addExtension(X509Extensions.KeyUsage,true,
					new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));
			certGen.addExtension(X509Extensions.ExtendedKeyUsage,true,
					new ExtendedKeyUsage(KeyPurposeId.id_kp_serverAuth));
			*/	
			/*
			Date d = new Date(BCStatics2.certifikatsDatum(DatFunk.sHeute(), 0));
			Time time = new Time(d.getTime());
			certGen.addExtension(X509Extensions.BasicConstraints, true, new BasicConstraints(false));
		    certGen.addExtension(X509Extensions.KeyUsage, true, new KeyUsage(KeyUsage.digitalSignature
		        | KeyUsage.keyEncipherment));
		    certGen.addExtension(X509Extensions.ExtendedKeyUsage, true, new ExtendedKeyUsage(
		        KeyPurposeId.id_kp_serverAuth));	
		    */    
		    X509Certificate cert = certGen.generateX509Certificate(pair.getPrivate(),"BC");
			cert.checkValidity(new Date());
			//cert.verify(pair.getPublic(), "BC");
			cert.verify(pair.getPublic(),"BC");
		    return cert; 
		}


	 static X509Extensions getX509ExtensionsFromCsr(
		      PKCS10CertificationRequest certificateSigningRequest ) throws Exception{
		   CertificationRequestInfo certificationRequestInfo = certificateSigningRequest
		         .getCertificationRequestInfo();
		   ASN1Set attributesAsn1Set = certificationRequestInfo.getAttributes();
		   //final ASN1Set attributesAsn1Set = certificationRequestInfo.getAttributes()
		   System.out.println(certificateSigningRequest.verify());
		   System.out.println(certificateSigningRequest.getPublicKey());
		   //System.out.println("1"+attributesAsn1Set);
		   //System.out.println("2"+certificateSigningRequest);
		   //System.out.println("3"+certificationRequestInfo);
		   System.out.println("4"+certificationRequestInfo.getVersion());
		   System.out.println("5"+certificationRequestInfo.getDERObject());
		   System.out.println("6"+certificationRequestInfo.getSubject());

		   if(attributesAsn1Set==null){
			   return null;
		   }
		   X509Extensions certificateRequestExtensions = null;
		   for (int i = 0; i < attributesAsn1Set.size(); ++i){
		      DEREncodable derEncodable = attributesAsn1Set.getObjectAt( i );
		      if (derEncodable instanceof DERSequence){
		         final Attribute attribute = new Attribute( (DERSequence) attributesAsn1Set
		               .getObjectAt( i ) );

		         if (attribute.getAttrType().equals( PKCSObjectIdentifiers.pkcs_9_at_extensionRequest )){
		            ASN1Set attributeValues = attribute.getAttrValues();
		            if (attributeValues.size() >= 1){
		               certificateRequestExtensions = new X509Extensions( (ASN1Sequence) attributeValues
		                     .getObjectAt( 0 ) );
		               break;
		            }
		         }
		      }
		   }
		   if (null == certificateRequestExtensions){
		      throw new CertificateException( "Keine X509Extension im CSR" );
		   }
		   return certificateRequestExtensions;
	 }
	 /***************
	  * 
	  * @param alias
	  * @throws Exception
	  */
	 public static void verschluesseln(String alias,String keystoreFile,String pw) throws Exception{
		 String pfad = keystoreFile;
		 String datei = BCStatics2.chooser(pfad);
		 if(datei.equals("")){
			 return;
		 }
		 
		 providerTest();
		 KeyStore store;
		 store = KeyStore.getInstance("BCPKCS12","BC");
		 InputStream in;
		 in = new FileInputStream(new File(keystoreFile+".p12"));
		 store.load(in,pw.toCharArray());
		 if(store.containsAlias(alias)){
			 X509Certificate cert = (X509Certificate)store.getCertificate(alias);
			 cert.getPublicKey();

			 /*java.security.interfaces.RSAPublicKey pub =
				(java.security.interfaces.RSAPublicKey)cert.getPublicKey();
				BigInteger pue_bi = pub.getPublicExponent();
*/
			 	String sha = getSHA1fromByte(cert.getPublicKey().getEncoded());
			 	System.out.println(sha);
			 	byte[] newbyte = new byte[20];
			 	for(int i = 0; i < 40; i+=2){
			 		newbyte[i/2] = sha.substring(i,i+2).getBytes()[0];
			 	}
			 	SecretKeySpec key = new SecretKeySpec(newbyte,"DES-EDE3-CBC");
			 	IvParameterSpec ivSpec = new IvParameterSpec(new byte[8]);
			 	
			 	
			 	//Cipher	cipher = Cipher.getInstance("DESEDE/CBC/NoPadding", "BC");
			 	Cipher	cipher = Cipher.getInstance("DESEDE/CBC/TBCPadding", "BC");
				System.out.println(datei);
				byte[] sigBytes = FileStatics.BytesFromFile(new File(datei));
				byte[] out = new byte[sigBytes.length];
				System.out.println("L�nge des Bytestreams="+sigBytes.length);
 
				cipher.init(Cipher.ENCRYPT_MODE, key,ivSpec); 
				//cipher.init(Cipher.ENCRYPT_MODE,  cert.getPublicKey());
				 int offset = 0;
				 byte[] result = cipher.update(sigBytes);
				
				 
				 cipher.doFinal();
				 FileStatics.BytesToFile(out,new File(datei+".cipher"));
				 System.out.println( new String(result));				 
				 //cipher = Cipher.getInstance("DESEDE/CBC/TBCPadding", "BC");
				 cipher.init(Cipher.DECRYPT_MODE, key,ivSpec);
				 byte[] decode = cipher.update(result);
				 cipher.doFinal();
				 System.out.println( new String(decode));
				 
		 }
	 }
	/**************
	 * 
	 * 
	 * 
	 * 	 
	 */

	 public static KeyStore loadStore(String keystoreFile,String pw) throws Exception{
		 	providerTest();
		 KeyStore store;

			String alias;
			store = KeyStore.getInstance("BCPKCS12","BC");
			InputStream in = new FileInputStream(new File(keystoreFile+".p12"));
			store.load(in,pw.toCharArray());
			in.close();	
			return store;
	 }
	 public static void saveStore(KeyStore keystore,String pw,String name) throws Exception{
		 	providerTest();
		 	ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			keystore.store(bOut,pw.toCharArray());
			FileStatics.BytesToFile(bOut.toByteArray(),new File(NebraskaTestPanel.keystoreDir+ File.separator +name+".p12"));
			bOut.close();
	 }

	 public static void storePrivateToPem(KeyPair keypair,String name) throws Exception{
			FileWriter fWriter = new FileWriter(new File(NebraskaTestPanel.keystoreDir+ File.separator +name+".prv"));
	        PEMWriter pemWriter = new PEMWriter(fWriter, "BC");
	        pemWriter.writeObject(keypair.getPrivate());
	        pemWriter.close();
	        fWriter.close();
	        /*
			fWriter = new FileWriter(new File(NebraskaTestPanel.keystoreDir+ File.separator +name+".pub"));
	        pemWriter = new PEMWriter(fWriter, "BC");
	        pemWriter.writeObject(keypair.getPublic());
	        pemWriter.close();
	        fWriter.close();
			fWriter = new FileWriter(new File(NebraskaTestPanel.keystoreDir+ File.separator +name+".sls"));
	        pemWriter = new PEMWriter(fWriter, "BC");
	        pemWriter.writeObject(keypair);
	        .close();
	        fWriter.close();
	        FileReader fReader = new FileReader(new File(NebraskaTestPanel.keystoreDir+ File.separator +name+".prv"));
	        PEMReader pReader = new PEMReader(fReader);
	        KeyPair pKey = (KeyPair)pReader.readObject();
	        if(pKey.getPrivate().equals(keypair.getPrivate())){
	        	System.out.println("Private Schl�ssel sind identisch");
	        }else{
	        	System.out.println("Private Schl�ssel sind nicht(!)identisch");
	        }
			*/
	 }
	 public static void storePublicToPem(KeyPair keypair,String name) throws Exception{
			FileWriter fWriter = new FileWriter(new File(NebraskaTestPanel.keystoreDir+ File.separator +name+".pub"));
	        PEMWriter pemWriter = new PEMWriter(fWriter, "BC");
	        pemWriter.writeObject(keypair.getPublic());
	        pemWriter.close();
	        fWriter.close();

	 }
	 
	 public static PrivateKey getPrivateFromPem(String name) throws Exception{
	   		 FileReader fReader = new FileReader(new File(name+".prv"));
		        PEMReader pReader = new PEMReader(fReader);
		        KeyPair pKey = (KeyPair)pReader.readObject();
		        pReader.close();
		        fReader.close();
		        return pKey.getPrivate();
	 }
	 public static PublicKey getPublicFromPem(String name) throws Exception{
   		 FileReader fReader = new FileReader(new File(name+".pub"));
	        PEMReader pReader = new PEMReader(fReader);
	        KeyPair pKey = (KeyPair)pReader.readObject();
	        pReader.close();
	        fReader.close();
	        return pKey.getPublic();
	 }
	 public static KeyPair getBothFromPem(String name) throws Exception{
		 providerTest();
   		 FileReader fReader = new FileReader(new File(name+".prv"));
	        PEMReader pReader = new PEMReader(fReader);
	        KeyPair pKey = (KeyPair)pReader.readObject();
	        pReader.close();
	        fReader.close();
	        return pKey;
	 }
	 public static long  certifikatsDatum(String datum, int jahre){
		 int aktjahr = Integer.parseInt(datum.substring(6));
		 aktjahr = aktjahr+jahre;
		 return DatFunk.DatumsWert(datum.substring(0,6)+Integer.toString(aktjahr));
		 
	 }
	 public static String chooser(String pfad){
			//String pfad = "C:/Lost+Found/verschluesselung/";
			String sret = "";
			final JFileChooser chooser = new JFileChooser("Verzeichnis w�hlen");
	        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
	        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	        final File file = new File(pfad);

	        chooser.setCurrentDirectory(file);

	        chooser.addPropertyChangeListener(new PropertyChangeListener() {
	            public void propertyChange(PropertyChangeEvent e) {
	                if (e.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)
	                        || e.getPropertyName().equals(JFileChooser.DIRECTORY_CHANGED_PROPERTY)) {
	                    final File f = (File) e.getNewValue();
	                }
	            }

	        });
	        chooser.setVisible(true);
	        final int result = chooser.showOpenDialog(null);

	        if (result == JFileChooser.APPROVE_OPTION) {
	            File inputVerzFile = chooser.getSelectedFile();
	            String inputVerzStr = inputVerzFile.getPath();
	            System.out.println("Im Chooser inputVerzStr = "+inputVerzStr);

	            if(inputVerzFile.getName().trim().equals("")){
	            	sret = "";
	            }else{
	            	//sret = inputVerzFile.getName().trim();	
	            	sret = inputVerzStr;
	            }
	        }else{
	        	sret = ""; //vorlagenname.setText(SystemConfig.oTerminListe.NameTemplate);
	        }
	        return sret;
	 }
	 
	 public static String macheHexDump(String hexstring,int zeilenlaenge,String trenner){
		 String zeile = "";
		 String ganzerString = "";
		 int bytes = hexstring.length()/2;
		 int zeilen = bytes/zeilenlaenge;
		 if(bytes <= zeilenlaenge){
			 zeilen = 1;
		 }else if( (((bytes / zeilen) % 2) != 0)  && (bytes>zeilenlaenge) ){
			 zeilen = zeilen+1;
		 }
		 int stelle;
		 int i2;
		 for(int i = 0;i < zeilen ;i++){
			 zeile = "";
			 stelle = i*(zeilenlaenge*2);
			 for(i2 = 0; i2 < (zeilenlaenge*2);i2+=2){
				 try{
					 zeile = zeile+ hexstring.substring(stelle+i2,stelle+i2+2)+trenner;
				 }catch(Exception ex){

				 }
			 }
			 if( (((i*zeilenlaenge)+i2)-zeilenlaenge) < bytes ){
				 ganzerString = ganzerString + zeile.trim() + "\n";//System.getProperty("line.separator");	 
			 }else{
				 ganzerString = ganzerString + zeile.trim();
			 }
		 }
		 return ganzerString;
	 }


}
