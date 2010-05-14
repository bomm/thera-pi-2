package org.thera_pi.nebraska.crypto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.pkcs.CertificationRequestInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.x509.X509V3CertificateGenerator;

public class NebraskaKeystore {
	private BouncyCastleProvider bcProvider;

	private String keystoreFileName;
	private String keystorePassword;
	private String keyPassword;
	private File keystoreFile;
	private KeyStore keyStore;

	// data of signer/sender
	private String institutionID;
	private String institutionName;
	private String personName;
	
	// cache for alias strings
	/**
	 * Alias for a newly created key with self-signed certificate
	 */
	private String newKeyAlias;
	/**
	 * Alias for a officially certified key. 
	 */
	private String keyCertAlias;

	/**
	 * Initialize key store for specified principal using specified file.
	 * This constructor can be used if the key store already contains a certificate
	 * for the specified ID. The missing fields institution name and person name
	 * can be read from the existing certificate or must be specified using setters.
	 *  
	 * @param keystoreFileName name of key store file
	 * @param keystorePassword password for key store file
	 * @param keyPassword password for private key
	 * @param IK institution ID
	 * @throws NebraskaCryptoException on cryptography related errors
	 * @throws NebraskaFileException on I/O related errors
	 */
	public NebraskaKeystore(String keystoreFileName, String keystorePassword, String keyPassword, String IK)
	throws NebraskaCryptoException, NebraskaFileException
	{
		this(keystoreFileName, keystorePassword, keyPassword, IK, null, null);
	}

	/**
	 * Initialize key store for specified principal using specified file.
	 * Since this constructor specifies all fields it is possible to generate 
	 * a key pair or certificate request without using setters.
	 * 
	 * @param keystoreFileName name of key store file
	 * @param keystorePassword password for key store file
	 * @param keyPassword password for private key
	 * @param institutionID institution ID number (IK)
	 * @param institutionName institution name
	 * @param personName person name
	 * @throws NebraskaCryptoException on cryptography related errors
	 * @throws NebraskaFileException on I/O related errors
	 */
	public NebraskaKeystore(String keystoreFileName,
			String keystorePassword, String keyPassword, String institutionID, String institutionName, String personName)
	throws NebraskaCryptoException, NebraskaFileException
	{
		this.keystoreFileName = keystoreFileName;
		this.institutionID = NebraskaUtil.normalizeIK(institutionID);
		this.institutionName = institutionName;
		this.personName = personName;

		this.keystorePassword = keystorePassword;
		this.keyPassword = keyPassword;
		
		initSecurityProvider();
		initKeystore();

	}

	/**
	 * Switch to a different institution as sender/signer.
	 * 
	 * @param keyPassword password for private key
	 * @param institutionID institution ID number (IK)
	 * 
	 * @throws NebraskaCryptoException on cryptography related errors
	 * @throws NebraskaFileException on I/O related errors
	 */
	public void setIdentification(String keyPassword,
			String institutionID)
	throws NebraskaCryptoException, NebraskaFileException
	{
		setIdentification(keyPassword, institutionID, null, null);
	}

	/**
	 * Switch to a different institution as sender/signer.
	 * 
	 * @param keyPassword password for private key
	 * @param institutionID institution ID number (IK)
	 * @param institutionName institution name
	 * @param personName person name
	 * 
	 * @throws NebraskaCryptoException on cryptography related errors
	 * @throws NebraskaFileException on I/O related errors
	 */
	public void setIdentification(String keyPassword,
			String institutionID, String institutionName, String personName)
	throws NebraskaCryptoException, NebraskaFileException
	{
		this.institutionID = NebraskaUtil.normalizeIK(institutionID);
		this.institutionName = institutionName;
		this.personName = personName;

		this.keyPassword = keyPassword;
		
		// clear cache
		newKeyAlias = null;
		keyCertAlias = null;
	}

	/**
	 * add BouncyCastleProvider as security provider if necessary
	 */
	private void initSecurityProvider()
	{
		Provider provBC = Security.getProvider(NebraskaConstants.SECURITY_PROVIDER);
		if(provBC==null){
			bcProvider = new BouncyCastleProvider();
			Security.addProvider(bcProvider);			 
		} else {
			bcProvider = (BouncyCastleProvider) provBC;
		}
	}

	/**
	 * Load key store from file or initialize new key store and save to file.
	 * 
	 * @throws NebraskaCryptoException on cryptography related errors
	 * @throws NebraskaFileException on I/O related errors
	 */
	private void initKeystore() throws NebraskaCryptoException, NebraskaFileException
	{
		this.keystoreFile = new File(this.keystoreFileName);

		File keystoreDir = keystoreFile.getAbsoluteFile().getParentFile();
		if(!keystoreDir.exists())
		{
			keystoreDir.mkdirs();
		}
		
		try {
			keyStore = KeyStore.getInstance(NebraskaConstants.KEYSTORE_TYPE,NebraskaConstants.SECURITY_PROVIDER);

			if(keystoreFile.exists())
			{
				keyStore.load(new FileInputStream(keystoreFile), keystorePassword.toCharArray());
			} else {
				keyStore.load(null, null);
				saveKeystore();
			}
		} catch (KeyStoreException e) {
			throw new NebraskaCryptoException(e);
		} catch (NoSuchProviderException e) {
			throw new NebraskaCryptoException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new NebraskaCryptoException(e);
		} catch (CertificateException e) {
			throw new NebraskaCryptoException(e);
		} catch (FileNotFoundException e) {
			throw new NebraskaCryptoException(e);
		} catch (IOException e) {
			throw new NebraskaFileException(e);
		}
	}

	/**
	 * Save key store data to file.
	 * 
	 * @throws NebraskaCryptoException on cryptography related errors
	 * @throws NebraskaFileException on I/O related errors
	 */
	private void saveKeystore() throws NebraskaCryptoException, NebraskaFileException {
		try {
			// keystoreFile.createNewFile();
			FileOutputStream keystoreStream = new FileOutputStream(keystoreFile);
			keyStore.store(keystoreStream, keystorePassword.toCharArray());
		} catch (KeyStoreException e) {
			throw new NebraskaCryptoException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new NebraskaCryptoException(e);
		} catch (CertificateException e) {
			throw new NebraskaCryptoException(e);
		} catch (FileNotFoundException e) {
			throw new NebraskaFileException(e);
		} catch (IOException e) {
			throw new NebraskaFileException(e);
		}
	}
	
	/**
	 * Check if key store contains private key for institution ID.
	 * The result will be true if a key entry is present either with an officially
	 * signed certificate or with a self-signed certificate.
	 * 
	 * @return true if private key exists
	 * @throws NebraskaCryptoException on cryptography related errors
	 * @throws NebraskaNotInitializedException if institution ID, institution name 
	 * or person name is not initialized
	 */
	public boolean hasPrivateKey() throws NebraskaCryptoException, NebraskaNotInitializedException
	{
		try {
			// first check for entry with officially signed certificate
			return (keyStore.containsAlias(getKeyCertAlias()) && keyStore.entryInstanceOf(getKeyCertAlias(), KeyStore.PrivateKeyEntry.class)) ||
				// second check for self-signed certificate
				(keyStore.containsAlias(getNewKeyAlias()) && keyStore.entryInstanceOf(getNewKeyAlias(), KeyStore.PrivateKeyEntry.class));
		} catch (KeyStoreException e) {
			throw new NebraskaCryptoException(e);
		}
	}
	
	/**
	 * Check if key store contains certificate for IK.
	 * 
	 * @return true if certificate exists
	 */
	public boolean hasCertificate()
	{
		// FIXME check if keystore contains certificate for IK
		return false;
	}
	
	/**
	 * Create key pair and save to keystore.
	 * Overwrite existing key pair only if requested.
	 * Additionally save key pair to external file in specified directory.
	 * 
	 * @param overwrite flag to allow overwriting existing key pair
	 * @param filename name of external key file
	 * @param directory directory for key file
	 * 
	 * @throws NebraskaCryptoException on cryptography related errors
	 * @throws NebraskaFileException on I/O related errors
	 * @throws NebraskaNotInitializedException if institution ID, institution name 
	 * or person name is not initialized
	 */
	public void generateKeyPairAndSaveToFile(boolean overwrite, String filename, String directory)
		throws NebraskaCryptoException, NebraskaFileException, NebraskaNotInitializedException
	{
		File dir = new File(directory);
		if(!dir.exists())
		{
			dir.mkdirs();
		}
		
		generateKeyPair(overwrite);

		exportKey(directory + File.separator + filename, keyPassword);
	}
	
	/**
	 * Create key pair and save to keystore.
	 * Overwrite existing key pair only if requested.
	 * @param overwrite flag to allow overwriting existing key pair
	 * 
	 * @throws NebraskaCryptoException on cryptography related errors
	 * @throws NebraskaFileException on I/O related errors
	 * @throws NebraskaNotInitializedException if institution ID, institution name 
	 * or person name is not initialized
	 */
	public void generateKeyPair(boolean overwrite)
		throws NebraskaCryptoException, NebraskaFileException, NebraskaNotInitializedException
	{
		if((institutionID == null) || (institutionName == null) || (personName == null))
		{
			throw new NebraskaNotInitializedException(new Exception(
					"institution ID or name or person name not initialized"));
		}
		try {
			if(!overwrite && keyStore.containsAlias(getNewKeyAlias())) {
				throw new NebraskaCryptoException(new Exception("overwriting existing key not allowed"));
			}
		} catch (KeyStoreException e) {
			throw new NebraskaCryptoException(e);
		}

		KeyPairGenerator kpGen = null;
		try {
			kpGen = KeyPairGenerator.getInstance(NebraskaConstants.KEY_ALGORITHM,NebraskaConstants.SECURITY_PROVIDER);
		} catch (NoSuchAlgorithmException e) {
			throw new NebraskaCryptoException(e);
		} catch (NoSuchProviderException e) {
			throw new NebraskaCryptoException(e);
		}
		kpGen.initialize(2048, new SecureRandom());
		KeyPair keyPair = kpGen.generateKeyPair();

		X509Certificate cert = generateSelfSignedCert(keyPair);
		Certificate[] chain = { cert };
		try
		{
			keyStore.setKeyEntry(getNewKeyAlias(), keyPair.getPrivate(), keyPassword.toCharArray(), chain);
		} catch (IllegalStateException e) {
			throw new NebraskaCryptoException(e);
		} catch (KeyStoreException e) {
			throw new NebraskaCryptoException(e);
		}

		saveKeystore();
	}

	/**
	 * Getter for newKeyAlias with lazy initialization.
	 * Alias for a newly generated key that does not yet have a certificate from a CA. 
	 * 
	 * @return the key alias
	 * @throws NebraskaNotInitializedException if institution ID, institution name 
	 * or person name is not initialized
	 */
	private String getNewKeyAlias() throws NebraskaNotInitializedException {
		if(newKeyAlias == null)
		{
			if(institutionID == null)
			{
				throw new NebraskaNotInitializedException(new Exception(
						"institution ID not initialized"));
			}
			newKeyAlias = NebraskaUtil.getNewKeyAlias(institutionID);
		}
		return newKeyAlias;
	}

	/**
	 * Getter for keyCertAlias with lazy initialization.
	 * Alias for a key certified by a CA.
	 * 
	 * @return the alias for certified key
	 * @throws NebraskaNotInitializedException if institution ID, institution name 
	 * or person name is not initialized
	 */
	private String getKeyCertAlias() throws NebraskaNotInitializedException {
		if(keyCertAlias == null)
		{
			if(institutionID == null)
			{
				throw new NebraskaNotInitializedException(new Exception(
						"institution ID not initialized"));
			}
			keyCertAlias = NebraskaUtil.getCertAlias(institutionID);
		}
		return keyCertAlias;
	}

	/**
	 * generate self-signed certificate from key pair
	 * 
	 * @param keyPair key pair
	 * @return self-signed certificate
	 * @throws NebraskaCryptoException on cryptography related errors
	 */
	private X509Certificate generateSelfSignedCert(KeyPair keyPair)
			throws NebraskaCryptoException {
		// FIXME get serial number of existing certificate and increment
		BigInteger serialNumber = BigInteger.valueOf(1);
		
		Date now = new Date();

		String subjectDN = getSubjectDN();
		
		X509V3CertificateGenerator certGen = new X509V3CertificateGenerator(); 
		certGen.setSerialNumber(serialNumber);
		certGen.setIssuerDN(new X500Principal(subjectDN));
		certGen.setNotBefore(NebraskaUtil.certificateStart(now));
		certGen.setNotAfter(NebraskaUtil.certificateEnd(now));
		certGen.setSubjectDN(new X500Principal(subjectDN));
		certGen.setPublicKey(keyPair.getPublic());
		certGen.setSignatureAlgorithm(NebraskaConstants.CERTIFICATE_SIGNATURE_ALGORITHM);

		X509Certificate cert;
		try {
			cert = certGen.generate(keyPair.getPrivate(),NebraskaConstants.SECURITY_PROVIDER);
			cert.checkValidity(new Date());
			cert.verify(keyPair.getPublic(),NebraskaConstants.SECURITY_PROVIDER);

		} catch(CertificateException e) {
			throw new NebraskaCryptoException(e);
		} catch (InvalidKeyException e) {
			throw new NebraskaCryptoException(e);
		} catch (IllegalStateException e) {
			throw new NebraskaCryptoException(e);
		} catch (NoSuchProviderException e) {
			throw new NebraskaCryptoException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new NebraskaCryptoException(e);
		} catch (SignatureException e) {
			throw new NebraskaCryptoException(e);
		}
		return cert;
	}

	/**
	 * Create certificate request and write to specified stream.
	 * 
	 * @param outputStream the stream to write the certificate request to (or null)
	 * @throws NebraskaCryptoException on cryptography related errors
	 * @throws NebraskaFileException on I/O related errors
	 * @throws NebraskaNotInitializedException if institution ID, institution name 
	 * or person name is not initialized
	 */
	public void createCertificateRequest(OutputStream requestStream) throws NebraskaCryptoException, NebraskaFileException, NebraskaNotInitializedException {
		createCertificateRequest(requestStream, null);
	}

	/**
	 * Create certificate request and write to specified stream.
	 * 
	 * @param outputStream the stream to write the certificate request to (or null)
	 * @param md5Hash StringBuffer for MD5 fingerprint of public key (or null)
	 * @throws NebraskaCryptoException on cryptography related errors
	 * @throws NebraskaFileException on I/O related errors
	 * @throws NebraskaNotInitializedException if institution ID, institution name 
	 * or person name is not initialized
	 */
	public void createCertificateRequest(OutputStream requestStream, StringBuffer md5Hash) throws NebraskaCryptoException, NebraskaFileException, NebraskaNotInitializedException {
		if((institutionName == null) || (personName == null)) {
			readNameFromCert();
		}
		
		KeyStore.PrivateKeyEntry privateKeyEntry = getPrivateKeyEntry();
		
		X509Certificate selfsignedCert;
		RSAPrivateKey privateKey;
		RSAPublicKey publicKey;
		try {
			privateKey = (RSAPrivateKey) privateKeyEntry.getPrivateKey();
			selfsignedCert = (X509Certificate) privateKeyEntry.getCertificate();
			publicKey = (RSAPublicKey) selfsignedCert.getPublicKey(); 
		} catch(ClassCastException e) {
			throw new NebraskaCryptoException(e);
		}

		
		PKCS10CertificationRequest request;
		try {
			request = new PKCS10CertificationRequest(
					NebraskaConstants.CRQ_SIGNATURE_ALGORITHM,
					new X500Principal(getSubjectDN()),
					publicKey,
					null,
					privateKey);
		} catch (InvalidKeyException e) {
			throw new NebraskaCryptoException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new NebraskaCryptoException(e);
		} catch (NoSuchProviderException e) {
			throw new NebraskaCryptoException(e);
		} catch (SignatureException e) {
			throw new NebraskaCryptoException(e);
		}

		if(md5Hash != null) {
			CertificationRequestInfo info = request.getCertificationRequestInfo();
			ASN1Object asno =  (ASN1Object) info.getDERObject().toASN1Object();
			ASN1Sequence aseq = ASN1Sequence.getInstance(asno);
			SubjectPublicKeyInfo spub = null;
			for(int i = 0; i < aseq.size();i++){
				if(aseq.getObjectAt(i) instanceof SubjectPublicKeyInfo){
					spub = (SubjectPublicKeyInfo) aseq.getObjectAt(i);
				}
			}

			DERBitString pubKeyData = spub.getPublicKeyData();
			byte[] keyBytes = pubKeyData.getBytes();

			MessageDigest messageDigest;
			try {
				messageDigest = MessageDigest.getInstance(NebraskaConstants.FINGERPRINT_ALGORITHM,NebraskaConstants.SECURITY_PROVIDER);
			} catch (NoSuchAlgorithmException e) {
				throw new NebraskaCryptoException(e);
			} catch (NoSuchProviderException e) {
				throw new NebraskaCryptoException(e);
			}
			messageDigest.update(keyBytes);
			byte[] digest = messageDigest.digest();

			//md5Hash.append("key length: " + keyBytes.length + "\n");
			//md5Hash.append(NebraskaUtil.toHexString(keyBytes) + "\n");
			md5Hash.append(NebraskaUtil.toHexString(digest));
		}
		
        if(requestStream != null) {
        	byte[] encodedRequest = request.getDEREncoded();

        	try {
        		requestStream.write(encodedRequest);
        	} catch (IOException e) {
        		throw new NebraskaFileException(e);
        	}
        }
		
	}

	/**
	 * Fill instiutionName and personName from existing certificate for IK
	 * 
	 * @throws NebraskaCryptoException on cryptography related errors
	 * @throws NebraskaNotInitializedException if institution ID, institution name 
	 * or person name is not initialized
	 */
	private void readNameFromCert() throws NebraskaCryptoException, NebraskaNotInitializedException {
		KeyStore.PrivateKeyEntry privateKeyEntry = getPrivateKeyEntry();
		
		X509Certificate selfsignedCert;
		try {
			selfsignedCert = (X509Certificate) privateKeyEntry.getCertificate();
		} catch(ClassCastException e) {
			throw new NebraskaCryptoException(e);
		}

		NebraskaPrincipal subject = new NebraskaPrincipal(
				selfsignedCert.getSubjectDN().getName());
		personName = subject.getPersonName();
		institutionName = subject.getInstitutionName();
	}
	
	/**
	 * Read certificate file (PKCS#7 reply from CA) and save certificates to keystore.
	 * 
	 * @param fileName certificate file to read
	 * @throws NebraskaFileException 
	 * @throws NebraskaCryptoException on cryptography related errors
	 * @throws NebraskaNotInitializedException if institution ID, institution name 
	 * or person name is not initialized
	 */
	public void importCertificateReply(String fileName) throws NebraskaFileException, NebraskaCryptoException, NebraskaNotInitializedException {

		// we must already have the private key
		if(!hasPrivateKey())
		{
			throw new NebraskaCryptoException(new Exception("private key must be present when importing certificate"));
		}

		// read certificate collection from file
		File certFile = new File(fileName);
		InputStream certStream;
		try {
			certStream = new FileInputStream(certFile);
		} catch (FileNotFoundException e) {
			throw new NebraskaFileException(e);
		}
		Collection<?> certColl;
		try {
			CertificateFactory certFactory = CertificateFactory.getInstance(NebraskaConstants.CERTIFICATE_TYPE, NebraskaConstants.SECURITY_PROVIDER);
			certColl = certFactory.generateCertificates(certStream);
		} catch (CertificateException e) {
			throw new NebraskaCryptoException(e);
		} catch (NoSuchProviderException e) {
			throw new NebraskaCryptoException(e);
		}
		
		// convert to an array and check institution ID
		boolean matches = false;
		ArrayList<X509Certificate> certs = new ArrayList<X509Certificate>();
		// TODO rearrange certificates if they are not in the correct order
		for (Iterator<?> certIt = certColl.iterator(); certIt.hasNext(); ) {
			X509Certificate cert = (X509Certificate) certIt.next();
			X500Principal subject = cert.getSubjectX500Principal();
			String certIK = new NebraskaPrincipal(subject.getName()).getInstitutionID();
			if(certIK != null && certIK.equals(this.institutionID))
			{
				// FIXME check if certificate matches private key
				matches = true;
			}
			certs.add(cert);
		}
		if(!matches)
		{
			throw new NebraskaCryptoException(new Exception("certificate does not match my institution ID"));
		}
		X509Certificate[] chain = new X509Certificate[certs.size()];
		chain = certs.toArray(chain);

		// overwrite the private key entry with new certificate chain
		KeyStore.PrivateKeyEntry entry = getPrivateKeyEntry();
		
		try
		{
			keyStore.setKeyEntry(getKeyCertAlias(), entry.getPrivateKey(), keyPassword.toCharArray(), chain);
		} catch (IllegalStateException e) {
			throw new NebraskaCryptoException(e);
		} catch (KeyStoreException e) {
			throw new NebraskaCryptoException(e);
		}

		// save changes to file
		saveKeystore();
	}
	
	/**
	 * Read receiver certificates from file (annahme-pkcs.key) and save to keystore.
	 * 
	 * @param fileName receiver certificates file to read
	 * @throws NebraskaFileException on file related errors
	 * @throws NebraskaCryptoException on cryptography related errors
	 */
	public void importReceiverCertificates(String fileName) throws NebraskaFileException, NebraskaCryptoException {
		final String certHeader = "-----BEGIN CERTIFICATE-----";
		final String certTrailer = "-----END CERTIFICATE-----";
		final String separator = System.getProperty("line.separator");

		File receiverCertFile = new File(fileName);
		FileInputStream receiverCertStream;
		try {
			receiverCertStream = new FileInputStream(receiverCertFile);
		} catch (FileNotFoundException e) {
			throw new NebraskaFileException(e);
		}
		BufferedReader receiverCertReader = new BufferedReader(new InputStreamReader(receiverCertStream));

		StringBuffer certBuf = new StringBuffer(certHeader + separator);
		String line;
		boolean empty = true;
		try {
			while((line = receiverCertReader.readLine()) != null)
			{
				// end of certificate
				if(line.trim().length() == 0)
				{
					if(!empty) {
						certBuf.append(certTrailer + separator);
						readAndStoreCert(certBuf);
						certBuf.setLength(0);
						certBuf.append(certHeader + separator);
						empty = true;
					}
				}
				else
				{
					empty = false;
					certBuf.append(line + separator);
				}
			}
			if(!empty)
			{
				certBuf.append(certTrailer + separator);
				readAndStoreCert(certBuf);
			}
		} catch (IOException e) {
			throw new NebraskaFileException(e);
		}
		saveKeystore();
	}

	/**
	 * read certificate from string buffer and store it in key store
	 * 
	 * @param certBuf certificate in PEM format with header and trailer line
	 * @throws NebraskaCryptoException on cryptography related errors
	 * @throws NebraskaFileException on file or I/O related errors
	 */
	private void readAndStoreCert(StringBuffer certBuf) throws NebraskaCryptoException, NebraskaFileException {
		StringReader certBufReader = new StringReader(certBuf.toString());
		PEMReader pemReader = new PEMReader(certBufReader);
		Object o;
		try {
			o = pemReader.readObject();
			if (o instanceof X509Certificate){
				X509Certificate cert = (X509Certificate) o;
				storeCertificate(cert);
		}	
		pemReader.close();
		} catch (IOException e) {
			throw new NebraskaFileException(e);
		}
	}
	
	/**
	 * Store certificate in key store with an alias generated from the 
	 * certificate's subject distinguished name. Works in memory only, 
	 * does not save the keystore file.
	 * 
	 * @param cert certificate to store
	 * @throws NebraskaCryptoException on cryptography related errors
	 */
	private void storeCertificate(X509Certificate cert) throws NebraskaCryptoException {
		NebraskaPrincipal subject = new NebraskaPrincipal(cert.getSubjectDN().getName());
		String alias = NebraskaUtil.getCertAlias(subject.getAlias());
		try {
			keyStore.setCertificateEntry(alias, cert);
		} catch (KeyStoreException e) {
			throw new NebraskaCryptoException(e);
		}
	}

	/**
	 * Delete expired certificates from keystore.
	 */
	public void deleteExpiredCertificates() {
		// FIXME delete expired certs
	}
	
	/**
	 * Export key pair to file.
	 * 
	 * @param fileName the file to write
	 * @param keyPassword password for private key
	 * @throws NebraskaNotInitializedException if Nebraska key store is not properly initialized
	 * @throws NebraskaCryptoException on cryptography related errors
	 * @throws NebraskaFileException on I/O related errors
	 */
	public void exportKey(String fileName, String keyPassword) throws NebraskaCryptoException, NebraskaNotInitializedException, NebraskaFileException {
		PrivateKeyEntry keyEntry = getPrivateKeyEntry();
		PrivateKey privateKey = keyEntry.getPrivateKey();
		FileWriter fWriter;
		try {
			fWriter = new FileWriter(new File(fileName));
		} catch (IOException e) {
			throw new NebraskaFileException(e);
		}
        PEMWriter pemWriter = new PEMWriter(fWriter, NebraskaConstants.SECURITY_PROVIDER);
        try {
        	pemWriter.writeObject(privateKey);
        	pemWriter.close();
        	fWriter.close();
        } catch (IOException e) {
			throw new NebraskaFileException(e);
		}
	}
	
	/**
	 * Import key pair from file.
	 * 
	 * @param keyFileName the file to read
	 * @throws NebraskaFileException 
	 * @throws NebraskaCryptoException on cryptography related errors
	 * @throws NebraskaNotInitializedException if institution ID, institution name 
	 * or person name is not initialized
	 */
	public void importKeyPair(String keyFileName) throws NebraskaFileException, NebraskaCryptoException, NebraskaNotInitializedException {
		if(institutionID == null)
		{
			throw new NebraskaNotInitializedException(new Exception(
					"institution ID not initialized"));
		}

		// first read key pair from file
  		FileReader fReader;
  		KeyPair keyPair = null;
		try {
			fReader = new FileReader(new File(keyFileName));
			PEMReader pReader = new PEMReader(fReader);
			keyPair = (KeyPair)pReader.readObject();
			pReader.close();
			fReader.close();
		} catch (FileNotFoundException e) {
			throw new NebraskaFileException(e);
		} catch (IOException e) {
			throw new NebraskaFileException(e);
		}

		// setKeyEntry() needs a certificate chain, that's why we use a self-signed certificate 
		X509Certificate[] chain = { generateSelfSignedCert(keyPair) };
		try {
			keyStore.setKeyEntry(getNewKeyAlias(), keyPair.getPrivate(), keyPassword.toCharArray(), chain);
		} catch (KeyStoreException e) {
			throw new NebraskaCryptoException(e);
		}
		// save changes to file
		saveKeystore();
	}

	/**
	 * Get the Subject DN for the principal specified in the constructor.
	 * 
	 * @return Subject DN string
	 */
	public String getSubjectDN() {
		return NebraskaUtil.getSubjectDN(institutionID, institutionName, personName);
	}
	
	/**
	 * Get the public key fingerprint.
	 * Currently the function to generate a certificate request is used for this purpose.
	 * There should be an easier method as we don't need the CRQ here.
	 * 
	 * @return MD5 fingerprint as String
	 * @throws NebraskaCryptoException on cryptography related errors
	 * @throws NebraskaFileException on I/O related errors
	 * @throws NebraskaNotInitializedException if institution ID, institution name 
	 * or person name is not initialized
	 */
	public String getPublicKeyMD5() throws NebraskaCryptoException, NebraskaFileException, NebraskaNotInitializedException {
		StringBuffer md5Hash = new StringBuffer();
		createCertificateRequest(null, md5Hash);
		return md5Hash.toString();
	}

	/**
	 * Retrieve my private key from the keystore. 
	 * @return the private key
	 * @throws NebraskaCryptoException on cryptography related errors
	 * @throws NebraskaNotInitializedException if institution ID, institution name 
	 * or person name is not initialized
	 */
	private KeyStore.PrivateKeyEntry getPrivateKeyEntry()
			throws NebraskaCryptoException, NebraskaNotInitializedException {
		KeyStore.Entry keyEntry;
		try {
			keyEntry = keyStore.getEntry(getKeyCertAlias(),
					new KeyStore.PasswordProtection(keyPassword.toCharArray()));
			if(keyEntry == null) {
				keyEntry = keyStore.getEntry(getNewKeyAlias(),
						new KeyStore.PasswordProtection(keyPassword.toCharArray()));
			}
		} catch (NoSuchAlgorithmException e) {
			throw new NebraskaCryptoException(e);
		} catch (UnrecoverableEntryException e) {
			throw new NebraskaCryptoException(e);
		} catch (KeyStoreException e) {
			throw new NebraskaCryptoException(e);
		}
		
		KeyStore.PrivateKeyEntry privateKeyEntry;
		try {
			privateKeyEntry = (KeyStore.PrivateKeyEntry) keyEntry;
		} catch(ClassCastException e) {
			throw new NebraskaCryptoException(e);
		}
		return privateKeyEntry;
	}

	PrivateKey getSenderKey() throws NebraskaCryptoException, NebraskaNotInitializedException {
		KeyStore.PrivateKeyEntry entry = getPrivateKeyEntry();
		return entry.getPrivateKey();
	}

	/**
	 * Read the certificate chain for the sender/signer from the key store
	 * 
	 * @return certificate chain
	 * @throws NebraskaCryptoException on cryptography related errors
	 * @throws NebraskaNotInitializedException if institution ID, institution name 
	 * or person name is not initialized
	 */
	CertStore getSenderCertChain() throws NebraskaCryptoException, NebraskaNotInitializedException
	{
		String alias = getKeyCertAlias();
		try {
			Certificate[] certs = keyStore.getCertificateChain(alias);
			Collection<X509Certificate> certColl = new ArrayList<X509Certificate>();
			for(int i = 0; i < certs.length; i++)
			{
				Certificate cert = certs[i];
				if(cert instanceof X509Certificate)
				{
					certColl.add((X509Certificate)cert);
				}
				else
				{
					throw new NebraskaCryptoException(new Exception("certificate is not an X509 certificate"));
				}
			}
			CertStore certStore = CertStore.getInstance(NebraskaConstants.CERTSTORE_TYPE, 
					new CollectionCertStoreParameters(certColl), 
					NebraskaConstants.SECURITY_PROVIDER);
			return certStore;
		} catch (KeyStoreException e) {
			throw new NebraskaCryptoException(e);
		} catch (InvalidAlgorithmParameterException e) {
			throw new NebraskaCryptoException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new NebraskaCryptoException(e);
		} catch (NoSuchProviderException e) {
			throw new NebraskaCryptoException(e);
		}
		
	}
	
	/**
	 * Read the sender/signer certificate from key store.
	 * 
	 * @return certificate
	 * @throws NebraskaCryptoException on cryptography related errors
	 */
	X509Certificate getSenderCertificate() throws NebraskaCryptoException {
		return (X509Certificate) getCertificate(institutionID);
	}

	/**
	 * Read the certificate for a specified institution ID (IK) from key store.
	 *  
	 * @param institutionID desired institution
	 * @return certificate
	 * @throws NebraskaCryptoException on cryptography related errors
	 */
	X509Certificate getCertificate(String institutionID) throws NebraskaCryptoException {
		String alias = NebraskaUtil.getCertAlias(institutionID);
		try {
			return (X509Certificate) keyStore.getCertificate(alias);
		} catch (KeyStoreException e) {
			throw new NebraskaCryptoException(e);
		}
	}
	
	/**
	 * Get an encryptor object that will encrypt data for the specified receiver.
	 * 
	 * @param receiverIK receiver's institution ID
	 * @return encryptor object
	 * @throws NebraskaCryptoException on cryptography related errors
	 * @throws NebraskaNotInitializedException if institution ID, institution name 
	 * or person name is not initialized
	 */
	public NebraskaEncryptor getEncryptor(String receiverIK) throws NebraskaCryptoException, NebraskaNotInitializedException
	{
		return new NebraskaEncryptor(receiverIK, this);
	}
	
	/**
	 * Get a decryptor object that will decrypt data for the own institution ID.
	 * 
	 * @return decryptor object
	 * @throws NebraskaCryptoException on cryptography related errors
	 * @throws NebraskaNotInitializedException if institution ID, institution name 
	 * or person name is not initialized
	 */
	public NebraskaDecryptor getDecryptor() throws NebraskaCryptoException, NebraskaNotInitializedException
	{
		return new NebraskaDecryptor(this);
	}
}
