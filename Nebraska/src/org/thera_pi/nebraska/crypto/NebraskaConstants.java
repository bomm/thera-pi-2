package org.thera_pi.nebraska.crypto;

class NebraskaConstants {

	// constants to select algorithms and data types for BC function calls 
	static final String SECURITY_PROVIDER = "BC";
	static final String KEYSTORE_TYPE = "BCPKCS12";
	static final String KEY_ALGORITHM = "RSA";
	static final String CERTIFICATE_SIGNATURE_ALGORITHM = "SHA1WithRSAEncryption";
	static final String CRQ_SIGNATURE_ALGORITHM = "SHA1withRSA";
	static final String FINGERPRINT_ALGORITHM = "MD5";
	static final String CERTIFICATE_TYPE = "X509";
	static final String CERTSTORE_TYPE = "Collection";
	
	static final int CERTIFICATE_YEARS = 3;
	
	// common part of X509 principal
	static final String X500_PRINCIPAL_COUNTRY = "DE";
	static final String X500_PRINCIPAL_ORGANIZATION = "ITSG TrustCenter fuer sonstige Leistungserbringer";
	
}
