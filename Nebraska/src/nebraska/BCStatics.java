package nebraska;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.security.auth.x500.X500Principal;
import javax.swing.JFileChooser;

import opencard.core.util.HexString;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.x509.X509V3CertificateGenerator;

import utils.DatFunk;
import utils.NUtils;




public class BCStatics {
	static final String ROOT_ALIAS = "IK510841109";

	public static void readMultiple(String datei){
		String pfad = Nebraska.keystoredir;
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
                    // final File f = (File) e.getNewValue();
                }
            }

        });
        chooser.setVisible(true);
        final int result = chooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File inputVerzFile = chooser.getSelectedFile();
            if(inputVerzFile.getName().trim().equals("")){
            	sret = "";
            }else{
            	sret = inputVerzFile.getName().trim();	
            }
        }else{
        	sret = ""; //vorlagenname.setText(SystemConfig.oTerminListe.NameTemplate);
        }

        if(sret.equals("")){
        	return;
        }
		byte[] byteArray = null;
        try {
            byteArray = BytesFromFile(new File(pfad+sret));
            ByteArrayInputStream byteArrayInputStream = new 
            ByteArrayInputStream(byteArray);
            System.out.println("Gr��e der Datei = "+byteArray.length+" Bytes");
            
            //while(byteArrayInputStream.read()!=-1)
            //	  System.out.println(byteArrayInputStream.read());
            CertificateFactory fact;
			try {
				providerTest();
				//fact = CertificateFactory.getInstance("X.509",Constants.SECURITY_PROVIDER);
				fact = CertificateFactory.getInstance(Constants.CERTIFICATE_TYPE,Constants.SECURITY_PROVIDER);
	            X509Certificate x509Cert;
	            Collection<X509Certificate> collection = new ArrayList<X509Certificate>();
	            while( (x509Cert = (X509Certificate)fact.generateCertificate(byteArrayInputStream)) != null){
	            	collection.add(x509Cert);
	            	System.out.println("Zertifikat hinzugef�gt");
	            }
	            Iterator<X509Certificate> it = collection.iterator();
	            while(it.hasNext()){
        			X509Certificate x509Certifikat = ((X509Certificate)it.next());

        			System.out.println("Version = "+
 
        					x509Certifikat.getVersion());
	            	System.out.println("Algorythmus-Name = "+
	            			x509Certifikat.getSigAlgName());
	            	System.out.println("Public-Key = "+
	            			x509Certifikat.getPublicKey());
	            	
	            	System.out.println("Algorythmus = "+
	            			x509Certifikat.getSigAlgOID());
	            	System.out.println("Distinguished Name = "+
	            			x509Certifikat.getIssuerDN());
	            	System.out.println("Principal = "+
	            			x509Certifikat.getIssuerX500Principal());
	            	//System.out.println("Signatur = "+
	            	//		new String(x509Certifikat.getSignature()));
	            	//System.out.println("Signatur Parameters= "+
	            	//new String(x509Certifikat.getSigAlgParams()));
	            	
	            	 byte[] sig = x509Certifikat.getSignature();
	            	 System.out.println("Signatur: = ");
	            	 System.out.println(new BigInteger(sig).toString(16));
	            	 System.out.println("PublicKey: = ");
	            	 PublicKey pk = x509Certifikat.getPublicKey();
	            	    byte[] pkenc = pk.getEncoded();
	            	    for (int i = 0; i < pkenc.length; i++) {
	            	    	//System.out.print(Integer.toHexString((int)pkenc[i])+" ");
	            	      System.out.print(Integer.toHexString(unsignedByteToInt(pkenc[i])) + " ");
	            	    }

	            }
	            System.out.println("\nAnzahl Zertifikate = "+collection.size());
				
			} catch (CertificateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchProviderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
            
            
            
        } catch (IOException e) {
            e.printStackTrace();
        }

	}
	public static int unsignedByteToInt(byte b){
		return (int)b & 0xff;
	}

	/*************************************************************************************/
	public static void readMultipleAnnahme(String keystoreDir){
		final String zertanfang = "-----BEGIN CERTIFICATE-----";
		final String zertende = "-----END CERTIFICATE-----";
		String pfad = Nebraska.keystoredir; // FIXME warum nicht der Parameter keystoreDir
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
                    // final File f = (File) e.getNewValue();
                }
            }

        });
        chooser.setVisible(true);
        final int result = chooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File inputVerzFile = chooser.getSelectedFile();
            if(inputVerzFile.getName().trim().equals("")){
            	sret = "";
            }else{
            	sret = inputVerzFile.getName().trim();	
            }
        }else{
        	sret = ""; //vorlagenname.setText(SystemConfig.oTerminListe.NameTemplate);
        }

        if(sret.equals("")){
        	return;
        }
        
		providerTest();

		int eingelesen = 0;
		try {
			BufferedReader in
			   = new BufferedReader(new FileReader(pfad + File.separator +sret));

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
						// Hier das Pem Ged�nse anlegen....
						importCertsPEM(keystoreDir,buf);
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
			/*
			PEMReader pemReader = new PEMReader(reader);
			Object o;
			while( (o = pemReader.readObject()) != null){
				//Object o = pemReader.readObject();
				if (o instanceof X509Certificate){
					X509Certificate cert = (X509Certificate) o;
					jta.setText(jta.getText()+"\n************************************"+
							cert);
					jta.setText(jta.getText()+"\n"+
							cert.getIssuerDN());
					jta.setText(jta.getText()+"\n"+
							cert.getSubjectDN());
					zertifikate++;
				}else{
				}
			}
			pemReader.close();
			System.out.println("Insgesamt eingelesene Zertifikate = "+eingelesen);
			System.out.println("Insgesamt importierte Zertifikate = "+zertifikate);
			*/
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public static void importCertsPEM(String keystoreDir, StringBuffer buf){
		File f = new File(Nebraska.keystoredir + File.separator +"temp.pem");
		FileWriter writer;
		try {
			writer = new FileWriter(f);
			writer.write(buf.toString());
			writer.close();
			FileReader reader = new FileReader(f); 
			PEMReader pemReader = new PEMReader(reader);
			Object o;
			o = pemReader.readObject();
			if (o instanceof X509Certificate){
					X509Certificate cert = (X509Certificate) o;
					importCertIntoStore(keystoreDir,cert);
			}	
			pemReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/*************************************************************************************/
	public static void createKeyStore(KeyPair kp){
		try {
			KeyStore store;
			
			KeyPair rootPair = kp;
			X509Certificate rootCert = gnerateRootCert(rootPair);
			System.out.println(rootCert);
			certToFile(rootCert,rootCert.getEncoded());
			store = KeyStore.getInstance(Constants.KEYSTORE_TYPE,Constants.SECURITY_PROVIDER);
			store.load(null,null);
			store.setCertificateEntry(ROOT_ALIAS, rootCert);
			X509Certificate[] chain = new X509Certificate[1];
			chain[0] = (X509Certificate) rootCert;
			store.setKeyEntry(ROOT_ALIAS,rootPair.getPrivate(),Constants.KEYSTORE_PASSWORD.toCharArray(),(java.security.cert.Certificate[]) chain);

			ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			
			store.store(bOut,Constants.KEYSTORE_PASSWORD.toCharArray());
			
			System.out.println("fertig gestored....");
			storeToFile(Nebraska.keystorefile,bOut.toByteArray());
			
			store = KeyStore.getInstance(Constants.KEYSTORE_TYPE,Constants.SECURITY_PROVIDER);
			InputStream in = new FileInputStream(new File(Nebraska.keystorefile));
			store.load(in,Constants.KEYSTORE_PASSWORD.toCharArray());
			
			Enumeration<String> en = store.aliases();
			while (en.hasMoreElements()){
				String alias = (String)en.nextElement();
				System.out.println("gefunden wurde: "+alias+" ist es ein Zertifikat? "+
						store.isCertificateEntry(alias)+ " ist es ein Key entry ?"+store.isKeyEntry(alias)+
						"");
				if(store.isCertificateEntry(alias)){
					store.getCertificate(alias).getType();
					System.out.println(store.getCertificate(alias).getPublicKey());
					//System.out.println(new String(store.getCertificate("IK999999999").getPublicKey().getEncoded()));
					Key key = store.getCertificate(alias).getPublicKey();
					System.out.println( "algorythmus  = "+key.getAlgorithm());
					System.out.println( "format        = "+key.getFormat());
					System.out.println( "serialversion = "+Key.serialVersionUID);
					System.out.println( "toString      = "+key.toString());
					System.out.println( "alias         = "+alias+"");
					X509Certificate cert = (X509Certificate)store.getCertificate(alias);
					System.out.println( "Subject        = "+cert.getSubjectDN());
					System.out.println( "G�ltig bis     = "+cert.getNotAfter());
					try{
						cert.checkValidity();	
					}catch(Exception ex){
						System.out.println("Fehler im Zertifikat "+cert);
					}
					
					

				}
				if(store.isKeyEntry(alias)){
					try {
						System.out.println(store.getKey(alias,Constants.KEYSTORE_PASSWORD.toCharArray()));
					} catch (UnrecoverableKeyException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		}catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		} catch (SignatureException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
		}catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static void showAllCertsInStore(){
		KeyStore store;
		try {
			providerTest();
			store = KeyStore.getInstance(Constants.KEYSTORE_TYPE,Constants.SECURITY_PROVIDER);
			InputStream in = new FileInputStream(new File(Nebraska.keystorefile));
			store.load(in,Constants.KEYSTORE_PASSWORD.toCharArray());
			Enumeration<String> en = store.aliases();
			while (en.hasMoreElements()){
				String alias = (String)en.nextElement();
				if(store.isCertificateEntry(alias)){
					X509Certificate cert = (X509Certificate)store.getCertificate(alias);
					java.security.interfaces.RSAPublicKey pub =
					(java.security.interfaces.RSAPublicKey)cert.getPublicKey();
					System.out.println("Public Key:\n"+cert.getPublicKey());
					System.out.println("Issuer-DN:\n"+cert.getIssuerDN());
					System.out.println("Subject-DN:\n"+cert.getSubjectDN());
					//System.out.println("Public-Key:\n"+pub);
					//System.out.println("Exponent:\n"+pue_bi);
					//System.out.println("Hex Exponent:\n"+HexString.hexify(pub_exp));
					//System.out.println("Bouncy Exponent:\n"+pub.getPublicExponent());
					System.out.println("Schl�sselformat: "+pub.getFormat());
					System.out.println("Public-Key SerienNr. "+RSAPublicKey.serialVersionUID);
					MessageDigest messageDigest = MessageDigest.getInstance(Constants.HASH_ALGORITHM_MD5,Constants.SECURITY_PROVIDER);
					 messageDigest.update(pub.getModulus().toByteArray());
				        System.out.println( "MD5-Digest-L�nge: "+messageDigest.getDigestLength());
				        byte[] dig = messageDigest.digest();
				        String hex = "";
				        for(int i = 0; i < messageDigest.getDigestLength();i++){
				        	if(i>0){
				        		hex = hex+":"+HexString.hexify(dig[i]);
				        	}else{
						        hex = hex+ HexString.hexify(dig[i]);				        		
				        	}
				        }
				        System.out.println( "MD5-AS-Hex: "+hex );
				        //System.out.println( "\nDigest: " );
				        //System.out.println(  messageDigest.digest() );

				        messageDigest = MessageDigest.getInstance(Constants.HASH_ALGORITHM_SHA_1,Constants.SECURITY_PROVIDER);
						 messageDigest.update(pub.getModulus().toByteArray());
					        System.out.println( "SHA1-Digest-L�nge: "+messageDigest.getDigestLength());
					        dig = messageDigest.digest();
					        hex = "";
					        for(int i = 0; i < messageDigest.getDigestLength();i++){
					        	if(i>0){
					        		hex = hex+":"+HexString.hexify(dig[i]);
					        	}else{
							        hex = hex+ HexString.hexify(dig[i]);				        		
					        	}
					        }
					        System.out.println( "SHA1-AS-Hex: "+hex );
					        //System.out.println( "\nDigest: " );
					        //System.out.println(  messageDigest.digest() );

					
				}else if(store.isKeyEntry(alias)){
					System.out.println("KeyEntry");
				}
			}
			in.close();

		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void importCertIntoStore(String kystoreDir,X509Certificate cert){
		KeyStore store;
		String alias;
		try {
			store = KeyStore.getInstance(Constants.KEYSTORE_TYPE,Constants.SECURITY_PROVIDER);
			InputStream in = new FileInputStream(new File(Nebraska.keystorefile));
			store.load(in,Constants.KEYSTORE_PASSWORD.toCharArray());

			String[] splits = cert.getSubjectDN().toString().split(",");


				if(splits.length==5){
					alias = splits[3].split("=")[1];
					if(!store.containsAlias(alias)){
						//X509Certificate[] chain = (X509Certificate[]) store.getCertificateChain(schain);
						store.setCertificateEntry(alias, cert);
						//store.setKeyEntry(alias,cert.getPublicKey(),"196205".toCharArray(),(java.security.cert.Certificate[]) chain);
						System.out.println("Zertifikat von "+alias+" der Datenbank hinzugef�gt");
						ByteArrayOutputStream bOut = new ByteArrayOutputStream();
						store.store(bOut,Constants.KEYSTORE_PASSWORD.toCharArray());
						System.out.println("fertig gestored....");
						storeToFile(Nebraska.keystorefile,bOut.toByteArray());
						bOut.close();

					}else{
						cert.getBasicConstraints();
						System.out.println("Zertifikat von "+alias+" bereits enthalten");
					}	
				}
				
			/*	
			store.setCertificateEntry(ROOT_ALIAS, rootCert);
			X509Certificate[] chain = new X509Certificate[1];
			chain[0] = (X509Certificate) rootCert;
			store.setKeyEntry(ROOT_ALIAS,rootPair.getPrivate(),"196205".toCharArray(),(java.security.cert.Certificate[]) chain);
			*/

		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public static void deleteCertFromStore(String alias) throws Exception{
		KeyStore store;
		providerTest();
		store = KeyStore.getInstance(Constants.KEYSTORE_TYPE,Constants.SECURITY_PROVIDER);
		InputStream in = new FileInputStream(new File(Nebraska.keystorefile));
		store.load(in,Constants.KEYSTORE_PASSWORD.toCharArray());
		in.close();
		Enumeration<String> en = store.aliases();
		if(alias == null){
			while (en.hasMoreElements()){
				String aliases = (String)en.nextElement();
				if(aliases != null){
					if(store.isCertificateEntry(aliases)){
						store.deleteEntry(aliases);
						System.out.println("Store Entry mit alias "+aliases+" wurde gel�scht!");
					}
				}
			}	
		}else{
			if(store.containsAlias(alias)){
				store.deleteEntry(alias);
			}
		}
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		store.store(bOut,Constants.KEYSTORE_PASSWORD.toCharArray());
		System.out.println("fertig gestored....");
		storeToFile(Nebraska.keystorefile,bOut.toByteArray());
		bOut.close();
	}

	public static KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException{
		providerTest();
		KeyPairGenerator kpGen= KeyPairGenerator.getInstance(Constants.PUBLIC_KEY_ALGORITHM,Constants.SECURITY_PROVIDER);
		kpGen.initialize(2048, new SecureRandom());
		return kpGen.generateKeyPair();
	}
	public static X509Certificate gnerateRootCert(KeyPair pair) throws InvalidKeyException, NoSuchProviderException, SecurityException, SignatureException, CertificateEncodingException, IllegalStateException, NoSuchAlgorithmException{
		providerTest();
		X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
		certGen.setSerialNumber(BigInteger.valueOf(19920502));
		certGen.setIssuerDN(new X500Principal("OU=RTA GmbH, O=ITSG TrustCenter fuer sonstige Leistungserbringer,C=DE"));
		certGen.setNotBefore(new Date(BCStatics.certifikatsDatum(DatFunk.sHeute(), 0)));
		certGen.setNotAfter(new Date(BCStatics.certifikatsDatum(DatFunk.sHeute(), 3)));
		certGen.setSubjectDN(new X500Principal("CN=Herr Steinhilber,OU=IK510844109," +
		"OU=RTA GmbH, O=ITSG TrustCenter fuer sonstige Leistungserbringer,C=DE"));
		certGen.setPublicKey(pair.getPublic());
		certGen.setSignatureAlgorithm(Constants.SIGNATURE_ALGORITHM);
		return certGen.generate(pair.getPrivate(),Constants.SECURITY_PROVIDER);
	}
	public static X509Certificate generateV3Certificate(KeyPair pair,Vector<String>vec) 
	throws InvalidKeyException, NoSuchProviderException, SecurityException, SignatureException, CertificateEncodingException, IllegalStateException, NoSuchAlgorithmException{
		Security.addProvider(new BouncyCastleProvider());
		X509V3CertificateGenerator certGen = new X509V3CertificateGenerator(); 
		certGen.setSerialNumber(BigInteger.valueOf(19620502));
		certGen.setIssuerDN(new X500Principal("O=ITSG TrustCenter fuer sonstige Leistungserbringer,C=DE"));
		certGen.setNotBefore(new Date(BCStatics.certifikatsDatum(DatFunk.sHeute(), 0)));
		certGen.setNotAfter(new Date(BCStatics.certifikatsDatum(DatFunk.sHeute(), 3)));
		certGen.setSubjectDN(new X500Principal("CN=Herr Steinhilber,OU=IK510844109," +
		"OU=RTA GmbH, O=ITSG TrustCenter fuer sonstige Leistungserbringer,C=DE"));
		certGen.setPublicKey(pair.getPublic());
		certGen.setSignatureAlgorithm(Constants.SIGNATURE_ALGORITHM);
		return certGen.generate(pair.getPrivate(),Constants.SECURITY_PROVIDER);
	}
	
	
	/*************************************************************************************/	
	 public static byte[] BytesFromFile(File file) throws IOException {
	        InputStream is = new FileInputStream(file);
	        long length = file.length();
	    
	        if (length > Integer.MAX_VALUE) {
	      System.out.println("Sorry! Your given file is too large.");
	      System.exit(0);
	        }

	        byte[] bytes = new byte[(int)length];
	        int offset = 0;
	        int numRead = 0;
	        while (offset < bytes.length && (numRead=is.read(bytes, 
	                    offset, bytes.length-offset)) >= 0) {
	            offset += numRead;
	        }
	        if (offset < bytes.length) {
	            throw new IOException("Could not completely read file "
	                                + file.getName());
	        }
	        is.close();
	        return bytes;
	    }
	 	public static void BytesToFile(byte[] xdata,File fileout){
			try
			{
			  // Byte Array laden
			  byte[] data = xdata;
			  // Zu erzeugende Datei angeben
			  // Datei schreiben
			  FileOutputStream fileOut = new FileOutputStream(fileout);
			  fileOut.write(data);
			  fileOut.close();
			}
			catch (IOException e)
			{
			  e.printStackTrace();
			}		
		}
	 	
	 	public static byte[] inputStreamToBytes(InputStream in) throws IOException {

	 		ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
	 		byte[] buffer = new byte[1024];
	 		int len;

	 		while((len = in.read(buffer)) >= 0)
	 		out.write(buffer, 0, len);

	 		in.close();
	 		out.close();
	 		return out.toByteArray();
	 		} 	 	
	 public static void certToFile(X509Certificate x509Cert,byte[] b) throws IOException{

			String name = Nebraska.keystoredir + File.separator +System.currentTimeMillis();
			File f = new File(name+".p7b");
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(b);
			fos.flush();
			fos.close();
			f =  new File(name+".pem");
			fos = new FileOutputStream(f);
			fos.write(x509Cert.toString().getBytes());
			fos.flush();
			fos.close();
		 
	 }
	 public static void storeToFile(String file,byte[] b) throws IOException{
			File f = new File(file);
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(b);
			fos.flush();
			fos.close();
		 
	 }
	 public static long  certifikatsDatum(String datum, int jahre){
		 int aktjahr = Integer.parseInt(datum.substring(6));
		 aktjahr = aktjahr+jahre;
		 return DatFunk.DatumsWert(datum.substring(0,6)+Integer.toString(aktjahr));
		 
	 }
	 public static void generateRequest(){
		 
	 }
	 public static void generateRequestReply(){
		 
	 }
	 public static void importRequestReply(){
		 
	 }
	 public static void importRootCert(){
		 
	 }
	 /***************************************************************************/
	 public static String getSHA1(X509Certificate cert){
		 	byte[] encryptCert;
		 	byte[] dig = null;;
			try {
				encryptCert = cert.getEncoded();
				MessageDigest messageDigest = MessageDigest.getInstance(Constants.HASH_ALGORITHM_SHA_1,Constants.SECURITY_PROVIDER);
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
	 public static String getMD5(X509Certificate cert){
		 	byte[] encryptCert;
		 	byte[] dig = null;;
			try {
				encryptCert = cert.getEncoded();
				MessageDigest messageDigest = MessageDigest.getInstance(Constants.HASH_ALGORITHM_MD5,Constants.SECURITY_PROVIDER);
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
	 /***************************************************************************/
	 public static X509Certificate readSingleCert(String xpfad) throws Exception{
		 String pfad = xpfad;
		 String datei = chooser(pfad);
		 providerTest();
		 if(datei.equals("")){return null;}

			File f = new File(datei);

			FileInputStream in = new FileInputStream(f);
			CertificateFactory fact = CertificateFactory.getInstance(Constants.CERTIFICATE_TYPE,Constants.SECURITY_PROVIDER);
			X509Certificate cert = (X509Certificate) fact.generateCertificate(in);
			in.close();
			return cert;
	 }
	 public static void machePublicKey(PublicKey pubKey,X509Certificate cert){
		 java.security.interfaces.RSAPublicKey pub =
				(java.security.interfaces.RSAPublicKey)pubKey;
		 String hexstring = new BigInteger(pub.getModulus().toByteArray()).toString(16);
		 EncodedKeySpec publicKeySpec =new X509EncodedKeySpec(cert.getPublicKey().getEncoded()); 
		 System.out.println(publicKeySpec);

		 int lang = hexstring.length();
		 int teiler = lang/20;
		 if((lang % teiler)>0){
			 teiler++;
		 }
		 //System.out.println(hexstring);
		 System.out.println("L�nge des tbString = "+(hexstring.length()/2)+"\n");
		 String zeile = "";
		 int stelle;
		 for(int i = 0;i < teiler ;i++){
			 zeile = "";
			 stelle = i*20;
			 for(int i2 = 0; i2 < 20;i2+=2){
				 try{
					 zeile = zeile+ hexstring.substring(stelle+i2,stelle+i2+2)+" ";
				 }catch(Exception ex){
					 
				 }
			 }
			 System.out.println("Zeile "+i+(i < 10 ? "=   " : "=  ")+zeile);
		 }
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
	                    // final File f = (File) e.getNewValue();
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
	 
	 public static void verschluesseln(String alias){
		 String pfad = Nebraska.keystoredir;
		 String datei = chooser(pfad);
		 if(datei.equals("")){
			 return;
		 }
		 try {
			 providerTest();
			 KeyStore store;
			 store = KeyStore.getInstance(Constants.KEYSTORE_TYPE,Constants.SECURITY_PROVIDER);
			 InputStream in;
			 in = new FileInputStream(new File(Nebraska.keystorefile));
			 store.load(in,Constants.KEYSTORE_PASSWORD.toCharArray());
			 if(store.containsAlias(alias)){
				 X509Certificate cert = (X509Certificate)store.getCertificate(alias);
					Cipher	cipher = Cipher.getInstance(Constants.CIPHER_AND_PADDING, Constants.SECURITY_PROVIDER);
					/*
					cipher.init(Cipher.ENCRYPT_MODE,pub,
							new Byte[10]
							);
					*/
					byte[] sigBytes = BytesFromFile(new File(pfad+datei));
					byte[] out = new byte[sigBytes.length];
					
					 
					cipher.init(Cipher.ENCRYPT_MODE,  cert.getPublicKey());
					 int offset = 0;
					 while(offset >=0){
						offset =	cipher.update(sigBytes,
			                        offset,
			                        2,
			                        out,
			                        offset);  
					 }
					 cipher.doFinal();
					 BytesToFile(out,new File(pfad+datei+".cipher"));
			 }
			 
		 } catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ShortBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		 

	 }
	 public static void providerTest(){
		 Provider provBC = Security.getProvider(Constants.SECURITY_PROVIDER);
		 if(provBC==null){
			 Security.addProvider(new BouncyCastleProvider());			 
		 }
	 }

}
