package nebraska;

import java.io.File;

import utils.INIFile;

public class Constants {
//	public static final String CRYPTO_FILES_DIR = "C:/Lost+Found/verschluesselung";
//	public static final String KEYSTORE_DIR = "C:/Nebraska/";
//	public static final String OPENOFFICE_HOME = "C:/Programme/OpenOffice.org 3";
//	public static final String OPENOFFICE_JARS = "C:/RehaVerwaltung/RTAJars/openofficeorg";
	public static String inifile = null;
	public static String CRYPTO_FILES_DIR = "/home/bodo/thera-pi/Nebraska/tmp";
	public static String KEYSTORE_DIR = "/home/bodo/thera-pi/Nebraska";
	public static String OPENOFFICE_HOME = "/usr/lib/openoffice";
	public static String OPENOFFICE_JARS = "/home/bodo/thera-pi/RehaVerwaltung/RTAJars/openofficeorg";
	

	public static String PRAXIS_CE = null;
	public static String PRAXIS_OU_FIRMA = null;
	public static String PRAXIS_OU_ALIAS = null;
	public static String PRAXIS_O = null;
	public static String PRAXIS_CN = null;
	public static String PRAXIS_KS_PW = null;

	public static String TEST_CA_CE = null;
	public static String TEST_CA_OU_FIRMA = null;
	public static String TEST_CA_OU_ALIAS = null;
	public static String TEST_CA_O = null;
	public static String TEST_CA_CN = null;
	public static String TEST_CA_KS_PW = null;
	
	public static String REAL_CA_O = null;
	
	Constants(){
		if(System.getProperty("os.name").contains("Windows")){
			inifile = System.getProperty("user.dir")+ File.separator +"nebraska_windows.conf";
		}else if(System.getProperty("os.name").contains("Linux")){
			inifile = System.getProperty("user.dir")+ File.separator +"nebraska_linux.conf";			
		}else if(System.getProperty("os.name").contains("String für MaxOSX")){
			inifile = System.getProperty("user.dir")+ File.separator +"nebraska_mac.conf";
		}
		
		INIFile inif = new INIFile(inifile);
		System.out.println(inifile);
		CRYPTO_FILES_DIR = inif.getStringProperty("Pfade","CRYPTO_FILES_DIR" );
		KEYSTORE_DIR = inif.getStringProperty("Pfade","KEYSTORE_DIR" );
		OPENOFFICE_HOME = inif.getStringProperty("Pfade","OPENOFFICE_HOME" );
		OPENOFFICE_JARS = inif.getStringProperty("Pfade","OPENOFFICE_JARS" );
		
		REAL_CA_O = inif.getStringProperty("General","REAL_CA_O"  );
		
		PRAXIS_CE = inif.getStringProperty("Praxis","PRAXIS_CE"  );
		PRAXIS_OU_FIRMA = inif.getStringProperty("Praxis","PRAXIS_OU_FIRMA"  );
		PRAXIS_OU_ALIAS = inif.getStringProperty("Praxis","PRAXIS_OU_ALIAS"  );
		PRAXIS_O = inif.getStringProperty("Praxis","PRAXIS_O"  );
		PRAXIS_CN = inif.getStringProperty("Praxis","PRAXIS_CN"  );
		// In der Entwicklungsversion steht das Passwort noch im Klartext
		PRAXIS_KS_PW = inif.getStringProperty("Praxis","PRAXIS_KS_PW"  );
		
		TEST_CA_CE = inif.getStringProperty("TestCA","TEST_CA_CE"  );
		TEST_CA_OU_FIRMA = inif.getStringProperty("TestCA","TEST_CA_OU_FIRMA"  );
		TEST_CA_OU_ALIAS = inif.getStringProperty("TestCA","TEST_CA_OU_ALIAS"  );
		TEST_CA_O = inif.getStringProperty("TestCA","TEST_CA_O"  );
		TEST_CA_CN = inif.getStringProperty("TestCA","TEST_CA_CN"  );
		// In der Entwicklungsversion steht das Passwort noch im Klartext
		TEST_CA_KS_PW = inif.getStringProperty("TestCA","TEST_CA_KS_PW"  ); 
		System.out.println(OPENOFFICE_HOME);
		
	}
	
	
	
	public static final int    AES_Key_Size = 256;
	public static final String CERTIFICATE_TYPE = "X.509";
	public static final String CIPHER_AND_PADDING = "DESEDE/CBC/PKCS7PADDING";
	public static final String CIPHER_AND_PADDING_OLD = "DES/CBC/PKCS7Padding";
	public static final String HASH_ALGORITHM_SHA_1 = "SHA-1";
	public static final String HASH_ALGORITHM_MD5 = "MD5";
	public static final String HASH_ALGORITHM = HASH_ALGORITHM_SHA_1;
	public static final String KEYSTORE_PASSWORD = "196205";
	public static final String KEYSTORE_PASSWORD2 = KEYSTORE_PASSWORD;
	public static final String KEYSTORE_TYPE = "BCPKCS12";
	public static final String MD5_WITH_RSA = "MD5WithRSA";
	public static final String PUBLIC_KEY_ALGORITHM = "RSA";
	public static final String SECRET_KEY_ALGORITHM = "AES";
	public static final String SECRET_KEY_DES_DER3_CBC = "DES-DER3-CBC";
	public static final String SECRET_KEY_ALGORITHM_DES = "DES";
	public static final String SECURITY_PROVIDER = "BC";
	public static final String SHA1WITH_RSA = "SHA1withRSA";
	public static final String SIGNATURE_ALGORITHM = "SHA1WithRSAEncryption";
	public static final String SIGNATURE_ALGORITHM_SHORT = "SHA1WithRSA";
	public static final String SIGNATURE_ALGORITHM_SHA256 = "SHA256WithRSAEncryption";
}
