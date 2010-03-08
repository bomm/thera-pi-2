package org.thera_pi.nebraska;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.regex.Pattern;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.pkcs.CertificationRequestInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V3CertificateGenerator;

public class Nebraska {
	private String keystoreFileName;
	private String keystorePassword;
	private String IK;
	private String institutionName;
	private String personName;
	private KeyStore keyStore;
	private File keystoreFile;
	private String alias;
	private String keyPassword;
	private BouncyCastleProvider bcProvider;

	/**
	 * Initialize keystore for specified principal using specified file.
	 * 
	 * @param keystoreFileName name of keystore file
	 * @param keystorePassword password for keystore file
	 * @param keyPassword TODO
	 * @param IK institution ID
	 * @throws NebraskaCryptoException on cryptography related errors
	 * @throws NebraskaFileException on I/O related errors
	 */
	public Nebraska(String keystoreFileName, String keystorePassword, String keyPassword, String IK)
	throws NebraskaCryptoException, NebraskaFileException
	{
		this(keystoreFileName, keystorePassword, keyPassword, IK, null, null);
	}

	/**
	 * Initialize keystore for specified principal using specified file.
	 * 
	 * @param keystoreFileName name of keystore file
	 * @param keystorePassword password for keystore file
	 * @param keyPassword password for private key
	 * @param IK institution ID
	 * @param institutionName
	 * @param personName
	 * @throws NebraskaCryptoException on cryptography related errors
	 * @throws NebraskaFileException on I/O related errors
	 */
	public Nebraska(String keystoreFileName,
			String keystorePassword, String keyPassword, String IK, String institutionName, String personName)
	throws NebraskaCryptoException, NebraskaFileException
	{
		this.keystoreFileName = keystoreFileName;
		this.IK = NebraskaUtil.normalizeIK(IK);
		this.alias = "IK" + this.IK;
		this.institutionName = institutionName;
		this.personName = personName;

		this.keystorePassword = keystorePassword;
		this.keyPassword = keyPassword;
		
		initSecurityProvider();
		initKeystore();

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
	 * Load keystore from file or initialize new keystore and save to file.
	 * 
	 * @throws NebraskaCryptoException on cryptography related errors
	 * @throws NebraskaFileException on I/O related errors
	 */
	private void initKeystore() throws NebraskaCryptoException, NebraskaFileException
	{
		this.keystoreFile = new File(this.keystoreFileName);

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
	 * Save keystore data to file.
	 * 
	 * @throws NebraskaCryptoException on cryptography related errors
	 * @throws NebraskaFileException on I/O related errors
	 */
	private void saveKeystore() throws NebraskaCryptoException, NebraskaFileException {
		try {
			FileOutputStream keystoreStream = new FileOutputStream(keystoreFile);
			keyStore.store(keystoreStream, keystorePassword.toCharArray());
		} catch (KeyStoreException e) {
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
	 * Check if keystore contains private key for IK.
	 * 
	 * @return true if private key exists
	 * @throws NebraskaCryptoException 
	 */
	public boolean hasPrivateKey() throws NebraskaCryptoException
	{
		try {
			return keyStore.containsAlias(alias) && keyStore.entryInstanceOf(alias, KeyStore.PrivateKeyEntry.class);
		} catch (KeyStoreException e) {
			throw new NebraskaCryptoException(e);
		}
	}
	
	/**
	 * Check if keystore contains certificate for IK.
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
	 * 
	 * @param keyPassword password used to protect the key
	 * @param overwrite flag to allow overwriting existing key pair
	 * 
	 * @throws NebraskaCryptoException on cryptography related errors
	 * @throws NebraskaFileException on I/O related errors
	 */
	public void generateKeyPair(String keyPassword, boolean overwrite)
		throws NebraskaCryptoException, NebraskaFileException
	{
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

			if(!overwrite && keyStore.containsAlias(alias)) {
				throw new NebraskaCryptoException(new Exception("overwriting existing key not allowed"));
			}
			Certificate[] chain = { cert };
			
			keyStore.setKeyEntry(alias, keyPair.getPrivate(), keyPassword.toCharArray(), chain);
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
		} catch (KeyStoreException e) {
			throw new NebraskaCryptoException(e);
		}

		saveKeystore();
	}

	/**
	 * Create certificate request and write to specified stream.
	 * 
	 * @param outputStream the stream to write the certificate request to (or null)
	 * @throws NebraskaCryptoException on cryptography related errors
	 * @throws NebraskaFileException on I/O related errors
	 */
	public void createCertificateRequest(OutputStream requestStream) throws NebraskaCryptoException, NebraskaFileException {
		createCertificateRequest(requestStream, null);
	}

	/**
	 * Create certificate request and write to specified stream.
	 * 
	 * @param outputStream the stream to write the certificate request to (or null)
	 * @param md5Hash StringBuffer for MD5 fingerprint of public key (or null)
	 * @throws NebraskaCryptoException on cryptography related errors
	 * @throws NebraskaFileException on I/O related errors
	 */
	public void createCertificateRequest(OutputStream requestStream, StringBuffer md5Hash) throws NebraskaCryptoException, NebraskaFileException {
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
	 */
	private void readNameFromCert() throws NebraskaCryptoException {
		KeyStore.PrivateKeyEntry privateKeyEntry = getPrivateKeyEntry();
		
		X509Certificate selfsignedCert;
		try {
			selfsignedCert = (X509Certificate) privateKeyEntry.getCertificate();
		} catch(ClassCastException e) {
			throw new NebraskaCryptoException(e);
		}

		String subjectDN = selfsignedCert.getSubjectDN().getName();
		String[] dnParts = subjectDN.split(",");
		for(int i = 0; i < dnParts.length; i++) {
			String[] keyVal = dnParts[i].trim().split(" *= *", 2);
			if(keyVal.length == 2) {
				if("CN".equals(keyVal[0])) {
					personName = keyVal[1];
				} else if("OU".equals(keyVal[0])) {
					if(!Pattern.matches("^IK[0-9]+$", keyVal[1])) {
						institutionName = keyVal[1];
					}
				}
			}
		}
	}
	
	/**
	 * Read certificate file (PKCS#7 reply from CA) and save certificates to keystore.
	 * 
	 * @param fileName certificate file to read
	 */
	public void importCertificateReply(String fileName) {
		// FIXME read file and store certificates in keystore
		/* Wie können privater Schlüssel, selbstsigniertes Zertifikat und offizielles
		 * Zertifikat im Keystore abgelegt werden?
		 * Einzeln mit unterschiedlichem Alias? Alles zusammen?
		 */
	}
	
	/**
	 * Read receiver certificates from file (annahme-pkcs.key) and save to keystore.
	 * 
	 * @param fileName receiver certificates file to read
	 */
	public void importReceiverCertificates(String fileName) {
		// FIXME import receiver certs
	}
	
	/**
	 * Delete expired certificates from keystore.
	 */
	public void deleteExpiredCertificates() {
		// FIXME delete expired certs
	}
	
	/**
	 * Export private key and certificate to file.
	 * 
	 * @param fileName the file to write
	 * @param keyPassword password for private key
	 */
	public void exportKeyAndCertificate(String fileName, String keyPassword) {
		// FIXME export private key and certificate
	}
	
	/**
	 * Import private key and certificate from file.
	 * 
	 * @param fileName the file to read
	 * @param keyPassword password for private key
	 */
	public void importKeyAndCertificate(String fileName, String keyPassword) {
		// FIXME import private key and certificate
	}

	/**
	 * Get the Subject DN for the principal specified in the constructor.
	 * 
	 * @return Subject DN string
	 */
	public String getSubjectDN() {
		return NebraskaUtil.getSubjectDN(IK, institutionName, personName);
	}
	
	/**
	 * Get the public key fingerprint.
	 * Currently the function to generate a certificate request is used for this purpose.
	 * There should be an easier method as we don't need the CRQ here.
	 * 
	 * @return MD5 fingerprint as String
	 * @throws NebraskaCryptoException on cryptography related errors
	 * @throws NebraskaFileException on I/O related errors
	 */
	public String getPublicKeyMD5() throws NebraskaCryptoException, NebraskaFileException {
		StringBuffer md5Hash = new StringBuffer();
		createCertificateRequest(null, md5Hash);
		return md5Hash.toString();
	}

	/**
	 * Retrieve my private key from the keystore. 
	 * @return the privaet key
	 * @throws NebraskaCryptoException on cryptography related errors
	 */
	private KeyStore.PrivateKeyEntry getPrivateKeyEntry()
			throws NebraskaCryptoException {
		KeyStore.Entry keyEntry;
		try {
			keyEntry = keyStore.getEntry(alias,
					new KeyStore.PasswordProtection(keyPassword.toCharArray()));
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
}
