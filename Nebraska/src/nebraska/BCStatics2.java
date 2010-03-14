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
import java.security.Principal;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

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
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.x509.X509V3CertificateGenerator;

import sun.rmi.runtime.Log;
import utils.DatFunk;
import utils.NUtils;

public class BCStatics2 {
	private static List<CertificateEventListener> listeners = new CopyOnWriteArrayList<CertificateEventListener>();

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
	
	@SuppressWarnings("deprecation")
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
		Enumeration<?> en = store.aliases();
		if(alias == null){
			while (en.hasMoreElements()){
				String aliases = (String)en.nextElement();
				if(aliases != null){
					if(store.isCertificateEntry(aliases)){
						store.deleteEntry(aliases);
						System.out.println("Store Entry mit alias "+aliases+" wurde gel�scht!");
					}else{
						store.deleteEntry(aliases);
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
    	Vector<X509Certificate> certVec = new Vector<X509Certificate>();
   	    for (Iterator<?> certIt = certColl.iterator(); certIt.hasNext(); ) {
   	    	X509Certificate cert = (X509Certificate) certIt.next();
   	    	certVec.add(cert);
   	    	X500Principal subject = cert.getSubjectX500Principal();
   	    	System.out.println ("Subject: " + subject);
   	    	X500Principal issuer = cert.getIssuerX500Principal();
   	    	System.out.println ("Issuer: " + issuer);
   			BCStatics2.importCertIntoStore(cert,keystoreDir + File.separator +keystoreFile,keystorePassword,"");
    	}
			//doCertPath(certVec,keystoreDir,keystoreFile,keystorePassword);
	}
	private static void doCertPath(Vector<X509Certificate> certVec,String keystoreDir, String keystoreFile, String keystorePassword) throws Exception{
		X509Certificate[] chain = new X509Certificate[certVec.size()];
		
			for(int i = 0; i < certVec.size();i++){
				chain[i] = certVec.get(i);
		}
		CertificateFactory fact = CertificateFactory.getInstance("X509","BC");
		CertPath certPath = fact.generateCertPath(Arrays.asList(chain));
		byte[] encoded = certPath.getEncoded();
		//System.out.println(new String(encoded));
		//System.out.println(certPath);
		List<? extends Certificate> chain2 = certPath.getCertificates();
		KeyStore store = KeyStore.getInstance("BCPKCS12","BC");
		store.load(null,null);
		
		for(int i = 0; i < chain2.size();i++){
			String alias = BCStatics3.extrahiereAlias(((X509Certificate)chain2.get(i)).getSubjectDN().toString()).trim();
			System.out.println("Alias = "+alias);
			//BCStatics2.importCertIntoStore((X509Certificate) chain2.get(i),keystoreDir + File.separator +keystoreFile,keystorePassword,"");
			store.setCertificateEntry( alias, (X509Certificate)chain2.get(i));
		}
		X509Certificate[] chain3 = {chain[1],chain[0],chain[2]};
		CollectionCertStoreParameters params = new CollectionCertStoreParameters(Arrays.asList(chain3));
		CertStore cstore = CertStore.getInstance("Collection",params,"BC");
		
		CertPathBuilder builder = CertPathBuilder.getInstance("PKIX","BC");
		X509CertSelector endConstraints = new X509CertSelector();
		endConstraints.setSerialNumber(chain3[2].getSerialNumber());
		endConstraints.setIssuer(chain3[2].getIssuerX500Principal().getEncoded());
		
		PKIXBuilderParameters buildParms = new PKIXBuilderParameters(
				Collections.singleton(new TrustAnchor((X509Certificate) chain3[0],null)),endConstraints);

		buildParms.addCertStore(cstore);
		
		PKIXCertPathBuilderResult result = 
			(PKIXCertPathBuilderResult) builder.build(buildParms);
		
		CertPath path = result.getCertPath();
		Iterator it = path.getCertificates().iterator();
		while(it.hasNext()){
			System.out.println( ((X509Certificate)it.next()).getSubjectX500Principal() );
		}
		System.out.println(result.getTrustAnchor().getTrustedCert().getSubjectX500Principal());
		/*
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		store.store(bOut,"196205".toCharArray());
		FileStatics.BytesToFile(bOut.toByteArray(),new File(Constants.KEYSTORE_DIR + File.separator +"teststore"+".p12"));
		bOut.close();
		*/
		//KeyStore keyStore = BCStatics2.loadStore(Constants.KEYSTORE_DIR+File.separator+keystoreFile, keystorePassword);
		
		//keyStore.
		//keyStore.
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
			 PublicKey pubKey = cert.getPublicKey();
				ASN1InputStream aIn = new ASN1InputStream(cert.getPublicKey().getEncoded());
				SubjectPublicKeyInfo sub = SubjectPublicKeyInfo.getInstance(aIn.readObject());

			 	String sha = getSHA1fromByte(sub.getPublicKeyData().getBytes());
			 	System.out.println(sha);
			 	System.out.println(datei);
				byte[] input = FileStatics.BytesFromFile(new File(datei));
				byte[] out = new byte[input.length];
				System.out.println("\nLänge des Original-File-Bytestreams="+input.length);
				System.out.println("****************Beginn Original-File******************************************");
				System.out.println(new String(input));
				System.out.println("****************Ende Original-File*********************************************");


			 	byte[] keyBytes = new byte[20];
			 	for(int i = 0; i < 40; i+=2){
			 		keyBytes[i/2] = sha.substring(i,i+2).getBytes()[0];
			 	}
			
			 	/**********************************Encrypt***************************************/
			 	byte[] msgNumber = new byte[] {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
			 	
			 	IvParameterSpec zeroIV = new IvParameterSpec(new byte[8]);

			 	SecretKeySpec key = new SecretKeySpec(keyBytes,Constants.SECRET_KEY_DES_DER3_CBC /*"DES-EDE3-CBC"*/);

			 	Cipher	cipher = Cipher.getInstance(Constants.CIPHER_AND_PADDING /*"DESEDE/CBC/TBCPadding"*/, "BC");

				cipher.init(Cipher.ENCRYPT_MODE, key,zeroIV); 
				
				IvParameterSpec encryptionIV = new IvParameterSpec(cipher.doFinal(msgNumber),0,8);

				cipher.init(Cipher.ENCRYPT_MODE, key,encryptionIV);
				
				byte[] cipherText = new byte[cipher.getOutputSize(input.length)];
				
				int ctLength = cipher.update(input,0,input.length,cipherText, 0);
				
				ctLength += cipher.doFinal(cipherText,ctLength);
				
				FileStatics.BytesToFile(cipherText, new File(datei+".cipher"));
				//gleich wieder einlesen
				cipherText = FileStatics.BytesFromFile(new File(datei+".cipher"));
				/*************************Ende - Encrypt*******************************/
				
				System.out.println("\nLänge des Encrypted-File-Bytestreams="+ctLength);
				System.out.println("****************Beginn Encrypted-File******************************************");
				System.out.println(new String(cipherText));
				System.out.println("****************Ende Encrypted-File*********************************************");

				/**********************************Decrypt*****************************/
				cipher.init(Cipher.ENCRYPT_MODE, key,zeroIV);
				
				IvParameterSpec decryptionIV = new IvParameterSpec(cipher.doFinal(msgNumber),0,8);
				
				cipher.init(Cipher.DECRYPT_MODE, key,decryptionIV);
				
				//byte[] plainText = new byte[cipher.getOutputSize(ctLength)];
				byte[] plainText = new byte[cipher.getOutputSize(cipherText.length)];
				
				//int ptLength = cipher.update(cipherText, 0,ctLength,plainText,0);
				int ptLength = cipher.update(cipherText, 0,cipherText.length,plainText,0);
				
				ptLength += cipher.doFinal(plainText, ptLength);
				
				/**********************************Ende - Decrypt*****************************/
				
				System.out.println("\nLänge des Decrypted-File-Bytestreams="+ptLength);
				System.out.println("****************Beginn Dencrypted-File******************************************");				
				System.out.println(new String(FileStatics.TransferByteArray(plainText, 0, ptLength)));
				System.out.println("****************Ende Dencrypted-File******************************************");				
				FileStatics.BytesToFile(FileStatics.TransferByteArray(plainText, 0, ptLength), new File(datei+".plain"));
				 
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
			final JFileChooser chooser = new JFileChooser("Verzeichnis wählen");
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
/************************************************************************/
	 public static boolean installReply(KeyStore keyStore, 
			 							KeyStore trustStore, 
			 							String keyPassword, 
			 							String alias, 
			 							InputStream inputStream, 
			 							boolean trustCACerts,
			 							boolean validateRoot,PrivateKey privkey) throws Exception {

	// Check that there is a certificate for the specified alias
		 
	X509Certificate certificate = (X509Certificate) keyStore.getCertificate(alias);
	if (certificate == null) {
		System.out.println("Certificate not found for alias: " + alias);
		return false;
	}
	// Retrieve the private key of the stored certificate
	//PrivateKey privKey = (PrivateKey) keyStore.getKey(alias, keyPassword.toCharArray());
	PrivateKey privKey = privkey;
	
	//PrivateKey privKey = privkey;	 
	// Load certificates found in the PEM input stream
	List<X509Certificate> certs = new ArrayList<X509Certificate>();
	for (Certificate cert : CertificateFactory.getInstance("X509").generateCertificates(inputStream)) {
		certs.add((X509Certificate) cert);
	}
	if (certs.isEmpty()) {
		throw new Exception("Reply has no certificates");
	}
	List<X509Certificate> newCerts;
	if (certs.size() == 1) {
		// Reply has only one certificate
		newCerts = establishCertChain(keyStore, trustStore, certificate, certs.get(0), trustCACerts);
		System.out.println("Beginn Only one certs******************************");
		System.out.println("Only one certs Anzahl = "+newCerts.size());
		System.out.println("End Only one certs******************************");
	} else {
		//Reply has a chain of certificates
		newCerts = validateReply(keyStore, trustStore, alias, certificate, certs, trustCACerts, validateRoot);
		System.out.println("Beginn Chain of certs******************************"+keyStore+"-"+trustStore+"-"+alias+"-"+certificate+"-"+certs);
		System.out.println("*****************Chain of certs Anzahl = "+newCerts.size());
		System.out.println("End Chain of certs******************************");		
	}
	if (newCerts != null) {
		keyStore.setKeyEntry(alias, privKey, keyPassword.toCharArray(),
				newCerts.toArray(new X509Certificate[newCerts.size()]));
		// Notify listeners that a new certificate has been created
		for (CertificateEventListener listener : listeners) {
			try {
				listener.certificateSigned(keyStore, alias, newCerts);
			}
			catch (Exception e) {
				System.out.println(e);
			}
		}
		System.out.println("Returnwert von installReply != null "+true);
		return true;
	} else {
		System.out.println("New Certs == null");
		System.out.println("Returnwert von installReply"+newCerts);
		return false;
	}
	}
/***************************************************/
	 private static List<X509Certificate> establishCertChain(KeyStore keyStore, KeyStore trustStore,
             X509Certificate certificate,
             X509Certificate certReply, boolean trustCACerts)
             throws Exception {
		 System.out.println("KeyStore = "+keyStore);
		 System.out.println("TrusStore = "+trustStore);
		 System.out.println("Certificate = "+certificate);
		 System.out.println("Reply = "+certReply);
		 //System.out.println("trustCACerts  = "+trustCACerts);
		 if (certificate != null) {
			 PublicKey publickey = certificate.getPublicKey();
			 PublicKey publickey1 = certReply.getPublicKey();
			 if (!publickey.equals(publickey1)) {
				 throw new Exception("Public keys in reply and keystore don't match");
			 }
			 if (certReply.equals(certificate)) {
				 throw new Exception("Certificate reply and certificate in keystore are identical");
			 }
		 }
		 Map<Principal, List<X509Certificate>> knownCerts = new Hashtable<Principal, List<X509Certificate>>();
		 
		 if (keyStore.size() > 0) {
			 knownCerts.putAll(getCertsByIssuer(keyStore));
		 }
		 if (trustCACerts && trustStore.size() > 0) {
			 knownCerts.putAll(getCertsByIssuer(trustStore));
			 
		 }
		 LinkedList<X509Certificate> answer = new LinkedList<X509Certificate>();
		 if (buildChain(certReply, answer, knownCerts)) {
			System.out.println("Returnwert von establishCertChain "+answer);
			 return answer;
		 } else {
			 throw new Exception("Failed to establish chain from reply");
		 }
	 }


/**
* Builds the certificate chain of the specified certificate based on the known list of certificates
* that were issued by their respective Principals. Returns true if the entire chain of all certificates
* was successfully built.
*
* @param certificate certificate to build its chain.
* @param answer      the certificate chain for the corresponding certificate.
* @param knownCerts  list of known certificates grouped by their issues (i.e. Principals).
* @return true if the entire chain of all certificates was successfully built.
*/
	 private static boolean buildChain(X509Certificate certificate, LinkedList<X509Certificate> answer,
		 Map<Principal, List<X509Certificate>> knownCerts) {
		 Principal subject = certificate.getSubjectDN();
		 Principal issuer = certificate.getIssuerDN();
		 // Check if the certificate is a root certificate (i.e. was issued by the same Principal that
		 //is present in the subject)
		 if (subject.equals(issuer)) {
			 answer.addFirst(certificate);
			 System.out.println("Returnwert von buildChain "+true);
			 return true;
		 }
		 // Get the list of known certificates of the certificate's issuer
		 List<X509Certificate> issuerCerts = knownCerts.get(issuer);
		 if (issuerCerts == null || issuerCerts.isEmpty()) {
			 // No certificates were found so building of chain failed
			 System.out.println("Returnwert von buildChain "+false);
			 return false;
		 }
		 for (X509Certificate issuerCert : issuerCerts) {
			 PublicKey publickey = issuerCert.getPublicKey();
			 try {
				 // Verify the certificate with the specified public key
				 certificate.verify(publickey);
				 // Certificate was verified successfully so build chain of issuer's certificate
				 if (!buildChain(issuerCert, answer, knownCerts)) {
					 System.out.println("Returnwert von buildChain-2 "+false);
					 return false;
				 }
			 }
			 catch (Exception exception) {
				 // Failed to verify certificate
				 System.out.println("Returnwert von buildChain Exception "+false);
				 return false;
			 }
		 }
		 answer.addFirst(certificate);
		 System.out.println("Returnwert von buildChain Ende "+true);
		 return true;
	 }
	 
/***************************************************/
	 private static Map<Principal, List<X509Certificate>> getCertsByIssuer(KeyStore ks)
     throws Exception {
		 Map<Principal, List<X509Certificate>> answer = new HashMap<Principal, List<X509Certificate>>();
		 Enumeration<String> aliases = ks.aliases();
		 while (aliases.hasMoreElements()) {
			 String alias = aliases.nextElement();
			 X509Certificate cert = (X509Certificate) ks.getCertificate(alias);
			 if (cert != null) {
				 Principal subjectDN = (Principal) cert.getSubjectDN();
				 List<X509Certificate> vec = answer.get(subjectDN);
				 if (vec == null) {
					 vec = new ArrayList<X509Certificate>();
					 vec.add(cert);
				 }
				 else {
					 if (!vec.contains(cert)) {
						 vec.add(cert);
					 }
				 }
				 answer.put(subjectDN, vec);
			 }
		 }
		 return answer;
	 }
	 
/***************************************************/
	    private static List<X509Certificate> validateReply(KeyStore keyStore, KeyStore trustStore, String alias,
                X509Certificate userCert, List<X509Certificate> replyCerts,
                boolean trustCACerts, boolean verifyRoot)
                throws Exception {
	    	// order the certs in the reply (bottom-up).
			// System.out.println("KeyStore = "+keyStore);
			 //System.out.println("TrusStore = "+trustStore);
			 //System.out.println("Alias = "+alias);
			 //System.out.println("UserCert = "+userCert);
			 //System.out.println("ReplyCerts  = "+replyCerts);
			 //System.out.println("TrustCACerts  = "+trustCACerts);
			 //System.out.println("verifyRoot  = "+verifyRoot);
	    	int i;
	    	PublicKey userPubKey = userCert.getPublicKey();
	    	for (i = 0; i < replyCerts.size(); i++) {
	    		if (userPubKey.equals(replyCerts.get(i).getPublicKey())) {
	    			break;
	    		}
	    	}
	    	if (i == replyCerts.size()) {
	    		throw new Exception("Certificate reply does not contain public key for <alias>: " + alias);
	    	}

	    	X509Certificate tmpCert = replyCerts.get(0);
	    	replyCerts.set(0, replyCerts.get(i));
	    	replyCerts.set(i, tmpCert);
	    	Principal issuer = (Principal) replyCerts.get(0).getIssuerDN();

	    	for (i = 1; i < replyCerts.size() - 1; i++) {
	    		// find a cert in the reply whose "subject" is the same as the
	    		// given "issuer"
	    		int j;
	    		for (j = i; j < replyCerts.size(); j++) {
	    			Principal subject = (Principal) replyCerts.get(j).getSubjectDN();
	    			if (subject.equals(issuer)) {
	    				tmpCert = replyCerts.get(i);
	    				replyCerts.set(i, replyCerts.get(j));
	    				replyCerts.set(j, tmpCert);
	    				issuer = (Principal) replyCerts.get(i).getIssuerDN();
	    				break;
	    			}
	    		}
	    		if (j == replyCerts.size()) {
	    			throw new Exception("Incomplete certificate chain in reply");
	    		}
	    	}

	    	// now verify each cert in the ordered chain
	    	for (i = 0; i < replyCerts.size() - 1; i++) {
	    		PublicKey pubKey = replyCerts.get(i + 1).getPublicKey();
	    		try {
	    			replyCerts.get(i).verify(pubKey);
	    		}
	    		catch (Exception e) {
	    			throw new Exception(
	    					"Certificate chain in reply does not verify: " + e.getMessage());
	    		}
	    	}

	    	if (!verifyRoot) {
	    		return replyCerts;
	    	}

	    	// do we trust the (root) cert at the top?
	    	X509Certificate topCert = replyCerts.get(replyCerts.size() - 1);
	    	boolean foundInKeyStore = keyStore.getCertificateAlias(topCert) != null;
	    	boolean foundInCAStore = trustCACerts && (trustStore.getCertificateAlias(topCert) != null);
	    	if (!foundInKeyStore && !foundInCAStore) {
	    		boolean verified = false;
	    		X509Certificate rootCert = null;
	    		if (trustCACerts) {
	    			System.out.println("Der CACerts wird vertraut*************************");
	    			for (Enumeration<String> aliases = trustStore.aliases(); aliases.hasMoreElements();) {
	    				String name = aliases.nextElement();
	    				rootCert = (X509Certificate) trustStore.getCertificate(name);
	    				if (rootCert != null) {
	    					try {
	    						topCert.verify(rootCert.getPublicKey());
	    						verified = true;
	    						break;
	    					}
	    					catch (Exception e) {
	    						// Ignore
	    					}
	    				}
	    			}
	    		}else{
	    			System.out.println("Der CACerts wird nicht!!!!!vertraut*************************");
	    		}
	    		if (!verified) {
	    			return null;
	    		}
	    		else {
	    			// Check if the cert is a self-signed cert
	    			if (!topCert.getSubjectDN().equals(topCert.getIssuerDN())) {
	    				//append the (self-signed) root CA cert to the chain
	    				replyCerts.add(rootCert);
	    			}
	    		}
	    	}

	    	return replyCerts;
	    }
	    public static void addListener(CertificateEventListener listener) {
	        if (listener == null) {
	            throw new NullPointerException();
	        }
	        listeners.add(listener);
	    }

	    /**
	     * Unregisters a listener to receive events.
	     *
	     * @param listener the listener.
	     */
	    public static void removeListener(CertificateEventListener listener) {
	        listeners.remove(listener);
	    }

	 
/***************************************************/	 
}
