package nebraska;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.sql.Time;
import java.util.Date;

import javax.security.auth.x500.X500Principal;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.bouncycastle.asn1.pkcs.CertificationRequestInfo;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.x509.X509V3CertificateGenerator;

import utils.DatFunk;
import utils.JCompTools;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class NebraskaPanel extends JPanel implements ActionListener{
	JButton[] but = {null,null,null,null,null,null,null,null};
	KeyPair kp;
	JTextArea jta;
	JScrollPane jsca;
	/**
	 * 
	 */
	private static final long serialVersionUID = -2328868562054207798L;
	
	public NebraskaPanel(){
		//setBackground(Color.WHITE);
		setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		//                                1    2     3    4    5    6     7    8         
		FormLayout lay = new FormLayout("5dlu,50dlu,5dlu,50dlu,5dlu,50dlu,5dlu,50dlu,"+
				// 9   10       11
				"5dlu,50dlu,fill:0:grow(1.0)",
				"fill:0:grow(1.0),2dlu,p,2dlu,p,2dlu");
		CellConstraints cc = new CellConstraints();
		setLayout(lay);
		but[0] = new JButton("Key-Pair erzeugen");
		but[0].setActionCommand("generatekey");
		but[0].addActionListener(this);
		add(but[0],cc.xy(2,3));
		
		but[1] = new JButton("Zertifikat erzeugen");
		but[1].setActionCommand("generatecert");
		but[1].addActionListener(this);
		add(but[1],cc.xy(4,3));
		
		but[2] = new JButton("Request erzeugen");
		but[2].setActionCommand("generaterequest");
		but[2].addActionListener(this);
		add(but[2],cc.xy(6,3));
		
		but[3] = new JButton("Annahme einlesen");
		but[3].setActionCommand("annahmeeinlesen");
		but[3].addActionListener(this);
		add(but[3],cc.xy(8,3));

		but[3] = new JButton("KeyStore erzeugen");
		but[3].setActionCommand("createkeystore");
		but[3].addActionListener(this);
		add(but[3],cc.xy(10,3));

		but[4] = new JButton("Zertifikate zeigen");
		but[4].setActionCommand("showcerts");
		but[4].addActionListener(this);
		add(but[4],cc.xy(2,5));

		but[5] = new JButton("Einzelnes lesen");
		but[5].setActionCommand("readsingle");
		but[5].addActionListener(this);
		add(but[5],cc.xy(4,5));
		
		but[6] = new JButton("Verschl�sseln");
		but[6].setActionCommand("verschluesseln");
		but[6].addActionListener(this);
		add(but[6],cc.xy(6,5));
		
		but[7] = new JButton("l�schen Zertifikate");
		but[7].setActionCommand("deletecert");
		but[7].addActionListener(this);
		add(but[7],cc.xy(8,5));
		
		jta = new JTextArea();
		jta.setFont(new Font("Courier",Font.PLAIN,11));
		jta.setLineWrap(true);
		jta.setName("saetze");
		jta.setWrapStyleWord(true);
		jta.setEditable(true);
		jta.setBackground(Color.WHITE);
		jta.setForeground(Color.BLUE);
		
		
		jsca = JCompTools.getTransparentScrollPane(jta);
		jsca.validate();
		add(jsca,cc.xyw(1, 1,11));
		
		
		/*
		try {
			KeyPair kp = generateRSAKeyPair();
			System.out.println("Private key:\n"+kp.getPrivate());
			System.out.println("Public key:\n"+kp.getPublic());
			X509Certificate cert = generateV3Certificate(kp);
			cert.checkValidity(new Date());
			cert.verify(cert.getPublicKey());
			System.out.println("G�ltiges Zertifikat wurde generiert");
			//System.out.println(new String(cert.getEncoded()));
			ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			bOut.write(cert.getEncoded());
			bOut.flush();
			bOut.close();
			InputStream in = new ByteArrayInputStream(bOut.toByteArray());
			CertificateFactory fact = CertificateFactory.getInstance("X.509",Constants.SECURITY_PROVIDER);
			X509Certificate x509Cert;
			Collection collection = new ArrayList();
			x509Cert = (X509Certificate) fact.generateCertificate(in);
			*/
			System.out.println("/*****************/");
			//System.out.println(new String(bOut.toByteArray()));
			//System.out.println(new String(x509Cert.getEncoded()));
			/*
			System.out.println(x509Cert.getSigAlgOID());
			System.out.println(x509Cert.getType());
			System.out.println(x509Cert.getVersion());
			System.out.println(x509Cert.getIssuerDN());
			System.out.println(x509Cert.getIssuerUniqueID());
			System.out.println(x509Cert.getIssuerAlternativeNames());
			System.out.println(x509Cert.getSubjectDN());
			System.out.println(x509Cert.getSubjectUniqueID());
			System.out.println(x509Cert.getSubjectX500Principal());
			*/
			System.out.println("/*****************/");
			//System.out.println(x509Cert);
			System.out.println("/*****************/");
			/*
			PKCS10CertificationRequest request = generateRequest(kp);
			
			//PEMWriter pemWrt = new PEMWriter(new OutputStreamWriter(new FileWriter("C:/Lost+Found/verschluesselung/51084110.crq")));
			bOut = new ByteArrayOutputStream ( ) ;
			PEMWriter pemWrt = new PEMWriter(new OutputStreamWriter(bOut));
			pemWrt.writeObject(request);
			pemWrt.close();
			System.out.println(bOut);
			*/
			//System.out.println("/*****************/");
			//System.out.close();
			//System.out.println(x509Cert);
			/*
			Thread.sleep(100);
			//RSAPublicKey pubkey = getPubKeyFromFile("C:/Lost+Found/verschluesselung/einschluessel.pem.txt");
			//RSAPublicKey pubkey = getPubKeyFromFile("C:\\Lost+Found\\verschluesselung\\ANNAHME.KEY");
			getPubKeyFromFile("C:\\Lost+Found\\verschluesselung\\51084110.CRP");
			
			Thread.sleep(100);
			//System.out.println(new String( pubkey.getEncoded()) ); 
			
			
			//System.exit(0);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (SignatureException e) {
			e.printStackTrace();
		} catch (CertificateExpiredException e) {
			e.printStackTrace();
		} catch (CertificateNotYetValidException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}

	
	public static X509Certificate generateV3Certificate(KeyPair pair) 
	throws InvalidKeyException, NoSuchProviderException, SecurityException, SignatureException, CertificateEncodingException, IllegalStateException, NoSuchAlgorithmException{
		Security.addProvider(new BouncyCastleProvider());
		X509V3CertificateGenerator certGen = new X509V3CertificateGenerator(); 

		certGen.setSerialNumber(BigInteger.valueOf(19620502));
		certGen.setIssuerDN(new X500Principal("O=ITSG TrustCenter fuer sonstige Leistungserbringer,C=DE"));
		/*
		certGen.setIssuerDN(new X500Principal("CN=Herr Steinhilber,OU=IK510844109," +
				"OU=Reutlinger Therapie- und Analysezentrum GmbH,O=ITSG TrustCenter fuer sonstige Leistungserbringer,C=DE"));
		*/		
		certGen.setNotBefore(new Date(BCStatics.certifikatsDatum(DatFunk.sHeute(), 0)));
		certGen.setNotAfter(new Date(BCStatics.certifikatsDatum(DatFunk.sHeute(), 3)));
		certGen.setSubjectDN(new X500Principal("CN=Herr Steinhilber,OU=IK510844109," +
		"OU=RTA GmbH, O=ITSG TrustCenter fuer sonstige Leistungserbringer,C=DE"));
		certGen.setPublicKey(pair.getPublic());
		certGen.setSignatureAlgorithm(Constants.SIGNATURE_ALGORITHM);
		//DateFormat df = new DateFormat("hh:mm:ss");
		Date d = new Date(BCStatics.certifikatsDatum(DatFunk.sHeute(), 0));
		Time time = new Time(d.getTime());
		System.out.println("Zeit = "+time.toString());
		try {
			Signature sig = Signature.getInstance(Constants.MD5_WITH_RSA);
			sig.initSign(pair.getPrivate());
			System.out.println(sig);
			System.out.println("HashCode des PublicKey = "+pair.getPublic().hashCode());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return certGen.generate(pair.getPrivate(),Constants.SECURITY_PROVIDER);

		
	}
	
	public static PKCS10CertificationRequest generateRequest(KeyPair pair) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException{
		return new PKCS10CertificationRequest(Constants.SHA1WITH_RSA,
				new X500Principal("CN=Herr Steinhilber," +
				"OU=IK510844109,OU=Reutlinger Therapie- und Analysezentrum GmbH,"+
				"O=ITSG TrustCenter fuer sonstige Leistungserbringer,C=DE"),
				pair.getPublic(),
				null,
				pair.getPrivate() );
	}
	
	public static int unsignedByteToInt(byte b) {
		return (int) b & 0xFF;
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		String cmd = arg0.getActionCommand();
		if(cmd.equals("generatekey")){
			doKeyPair();
		}else if(cmd.equals("generatecert")){
			if(kp==null){
				JOptionPane.showMessageDialog(null, "Bitte zuerst ein Keypair generieren");
				return;
			}
			doCertificate();
		}else if(cmd.equals("generaterequest")){
			doGenerateRequest();
		}else if(cmd.equals("annahmeeinlesen")){
			BCStatics.readMultipleAnnahme(Nebraska.keystoredir);
			//BCStatics.readMultiple2("C:/Lost+Found/verschluesselung/annahme-pkcs.key.p7b.pem");
		}else if(cmd.equals("createkeystore")){
			try {
				if(kp==null){
					JOptionPane.showMessageDialog(null, "Bitte zuerst ein Keypair generieren");
					return;
				}
				kp = BCStatics.generateRSAKeyPair();
				BCStatics.createKeyStore(kp);
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchProviderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(cmd.equals("showcerts")){
			BCStatics.showAllCertsInStore();
		}else if(cmd.equals("readsingle")){
			try {
				BCStatics.readSingleCert(Nebraska.keystoredir);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if(cmd.equals("verschluesseln")){
			BCStatics.verschluesseln("IK510841109");
		}else if(cmd.equals("deletecert")){
			try {
				BCStatics.deleteCertFromStore(null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	public void doGenerateRequest(){
		try {
			if(kp==null){
				JOptionPane.showMessageDialog(null,"Bitte zuerts das Key-Pair generieren");
				return;
			}
			PKCS10CertificationRequest request = generateRequest(kp);
			File f = new File(Constants.CRYPTO_FILES_DIR + File.separator +"51084110.p10");
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(request.getEncoded());
			fos.flush();
			fos.close();
			
			 FileWriter file = new FileWriter(new File(Constants.CRYPTO_FILES_DIR + File.separator + "51084110.pem"));
             PEMWriter pemWriter = new PEMWriter(file);
             pemWriter.writeObject(request);
             pemWriter.close();
             file.close();


			CertificationRequestInfo inf = request.getCertificationRequestInfo();
			System.out.println(inf.getSubject());
			System.out.println(inf.getSubjectPublicKeyInfo());
			jta.setText("Certifikation Request Encoded = :\n"+request.getCertificationRequestInfo()+jta.getText());		
			System.out.println(request.verify());
			System.out.println(request.getPublicKey());
			System.out.println(request.getSignature());
			System.out.println(request.hashCode());
			
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void doKeyPair(){
		try {
			kp = BCStatics.generateRSAKeyPair();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
		jta.setText("Private key:\n"+kp.getPrivate()+"\n"+jta.getText());
		jta.setText("********************************************************************\n\n"+jta.getText());
		jta.setText("Public  key:\n"+kp.getPublic()+"\n"+jta.getText());
		jta.setText("********************************************************************\n\n"+jta.getText());
	}
	public void doCertificate(){
		X509Certificate cert;
		try {
			cert = generateV3Certificate(kp);
			cert.checkValidity(new Date());
			cert.verify(cert.getPublicKey());
			System.out.println("G�ltiges Zertifikat wurde generiert");
			ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			bOut.write(cert.getEncoded());
			bOut.flush();
			bOut.close();
			InputStream in = new ByteArrayInputStream(bOut.toByteArray());
			Provider provBC = Security.getProvider(Constants.SECURITY_PROVIDER);
			CertificateFactory fact = CertificateFactory.getInstance(Constants.CERTIFICATE_TYPE,provBC);
			//CertificateFactory fact = CertificateFactory.getInstance(Constants.CERTIFICATE_TYPE,Constants.SECURITY_PROVIDER);
			X509Certificate x509Cert;
			//Collection collection = new ArrayList();
			x509Cert = (X509Certificate) fact.generateCertificate(in);
			byte[] b = x509Cert.getEncoded();
			String name = Constants.CRYPTO_FILES_DIR + File.separator + System.currentTimeMillis();
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
			jta.setText("Zerifikat:\n"+x509Cert+"\n"+jta.getText());
			jta.setText("********************************************************************\n\n"+jta.getText());
			
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateExpiredException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateNotYetValidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}


}
