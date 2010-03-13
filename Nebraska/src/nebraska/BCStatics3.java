package nebraska;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Principal;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;

public class BCStatics3 {
	
	public static String caDN = "";
	
	public static X509Certificate[] holeZertifikate() throws Exception{
		Object[] annahmeRet =  BCStatics3.readAnnahme(true,null);
		int anzahl = (Integer)annahmeRet[0];
		System.out.println("Es befinden sich "+anzahl+" Zertifikate in der Datei");
		if(anzahl <= 0){
			return null;
		}
		X509Certificate[] chain = new X509Certificate[anzahl];
		annahmeRet = BCStatics3.readAnnahme(false,chain);
		chain = (X509Certificate[])annahmeRet[1];
		return  chain;
	}

	public static Object[] readAnnahme(boolean checkOnly,X509Certificate[] certs) throws Exception{
		final String zertanfang = "-----BEGIN CERTIFICATE-----";
		final String zertende = "-----END CERTIFICATE-----";
		String pfad = Constants.KEYSTORE_DIR;
		String sret = "";
		int certcount = 0;
		if(NebraskaTestPanel.annahmeKeyFile.equals("")){
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
	            if(inputVerzFile.getName().trim().equals("")){
	            	sret = "";
	            }else{
	            	sret = inputVerzFile.getName().trim();	
	            }
	        }else{
	        	sret = ""; 
	        }
	        if(sret.equals("")){
	        	return new Object[]{certcount,null};
	        }
	        NebraskaTestPanel.annahmeKeyFile = sret;
		}else{
			sret = NebraskaTestPanel.annahmeKeyFile;
		}
		String serial;
		BCStatics2.providerTest();
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
					if(!checkOnly){
						certs[certcount] = BCStatics2.makeCertFromPem(Constants.KEYSTORE_DIR,buf);
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
		return new Object[] {certcount,certs};
	}
	public static String extrahiereAlias(String subject){
		if(subject.indexOf("IK")<0){
			return subject;
		}else{
			String[] teile = subject.split(",");
			return teile[3].split("=")[1];
		}
	}
	 public static Map<Principal, List<X509Certificate>> getCertsByIssuer(X509Certificate[] certs)
     throws Exception {
		 Map<Principal, List<X509Certificate>> answer = new HashMap<Principal, List<X509Certificate>>();
		 
		 for(int i = 0; i < certs.length;i++) {
			 String alias = extrahiereAlias(certs[i].getSubjectDN().toString());
			 X509Certificate cert = certs[i];
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

	 public static KeyStore erzeugeLeerenKeyStore(String pw) throws Exception{
		 KeyStore store = KeyStore.getInstance("BCPKCS12","BC");
		 store.load(null,null);
		 return store;
	 }
	 /**
	 * @throws KeyStoreException ************************************************************************/
	 /*
	 private static List<X509Certificate> establishCertChain(KeyStore keyStore, KeyStore trustStore,
             X509Certificate certificate,
             X509Certificate certReply, boolean trustCACerts)
             throws Exception {
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
	 */
	 public static X509Certificate getRootCACert(KeyStore store) throws KeyStoreException{
		 Enumeration<String> aliases = store.aliases();
		 while (aliases.hasMoreElements()) {
			 String alias = aliases.nextElement();
			 X509Certificate cert = (X509Certificate) store.getCertificate(alias);
			 if (cert != null) {
				 if(cert.getSubjectDN().equals(cert.getIssuerDN())){
					 caDN = cert.getSubjectDN().toString();
					 return cert;
				 }
			 }
		 }
		 return null;
	 }
	 public static X509Certificate getIntermediateCert(KeyStore store) throws KeyStoreException{
		 if(caDN.equals("")){
			 return null;
		 }
		 Enumeration<String> aliases = store.aliases();
		 while (aliases.hasMoreElements()) {
			 String alias = aliases.nextElement();
			 X509Certificate cert = (X509Certificate) store.getCertificate(alias);
			 if (cert != null) {
				 if( (! cert.getSubjectDN().equals(cert.getIssuerDN())) &&
						 cert.getIssuerDN().toString().equals(caDN)){
					 return cert;
				 }
			 }
		 }
		 return null;
	 }


}
