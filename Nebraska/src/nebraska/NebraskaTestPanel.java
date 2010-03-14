package nebraska;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.KeyStore.Entry;
import java.security.KeyStore.ProtectionParameter;
import java.security.cert.CertPath;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;


import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.BERTaggedObject;
import org.bouncycastle.asn1.DEREncodable;
import org.bouncycastle.asn1.DEREncodableVector;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.Attribute;

import org.bouncycastle.asn1.cms.EncryptedContentInfoParser;
import org.bouncycastle.asn1.cms.RecipientInfo;
import org.bouncycastle.asn1.pkcs.CertificationRequestInfo;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSAPrivateKeyStructure;
import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSEnvelopedDataGenerator;
import org.bouncycastle.cms.CMSEnvelopedGenerator;
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
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.sasn1.Asn1Set;
import org.bouncycastle.util.encoders.Base64Encoder;
import org.jdesktop.swingx.JXTable;


import pdfDrucker.PDFDrucker;

import sun.security.krb5.EncryptionKey;
import utils.DatFunk;
import utils.INIFile;
import utils.JCompTools;
import utils.JLabelRenderer;
import utils.MitteRenderer;
import utils.NUtils;
import utils.OOorgTools;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

import encode.BodosDecryptor;
import encode.CreatePKCS7;
import encode.EncUtils;

public class NebraskaTestPanel  extends JPanel implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3831067622472056656L;
	JTextField[] tn1 = {null,null,null,null,null};
	JTextField[] tn2 = {null,null,null,null,null};
	JTextField[] tn3 = {null,null,null,null,null};
	JButton[] but1 = {null,null,null,null,null,null,null};
	JButton[] but2 = {null,null,null,null,null};

	Vector<String>vecprax=new Vector<String>();
	Vector<String>vecca=new Vector<String>();
	public static String keystoreDir = Constants.KEYSTORE_DIR;
	public static String praxisKeystore;
	public static String caKeystore;
	public static String praxisPassw;
	public static String caPassw;
	public static String annahmeKeyFile = "";
	
	public JXTable tabprax;
	public JXTable tabca;
	//public DefaultTableModel tabmodprax;
	public MyCertTableModel tabmodprax;
	public MyCertTableModel tabmodca;
	
	public KeyPair kpprax = null; 
	public KeyPair kpca = null;
	public static X509Certificate[] annahmeCerts =null;
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy - HH:mm");
	
	public ImageIcon imgKey = new ImageIcon( (Constants.KEYSTORE_DIR+File.separator+"icons"+File.separator+"entry_pk.gif").replace("/", File.separator));
	public ImageIcon imgCert = new ImageIcon( (Constants.KEYSTORE_DIR+File.separator+"icons"+File.separator+"certificate_node.gif").replace("/", File.separator));	
	public NebraskaTestPanel(){
		super();                   //     1      2                3         4          5            6
		FormLayout lay = new FormLayout("2dlu,fill:0:grow(0.5),2dlu,fill:0:grow(0.5),2dlu",
	//    1     2          3          4     5
		"5dlu,20dlu,fill:0:grow(0.5),5dlu,50dlu,5dlu");
		CellConstraints cc = new CellConstraints();
		setLayout(lay);
		add(getTN1(),cc.xy(2, 3,CellConstraints.FILL,CellConstraints.FILL));
		add(getTN2(),cc.xy(4, 3,CellConstraints.FILL,CellConstraints.FILL));
		add(getButs1(),cc.xy(2, 5,CellConstraints.FILL,CellConstraints.FILL));
		add(getButs2(),cc.xy(4, 5,CellConstraints.FILL,CellConstraints.FILL));
		validate();
	}
	
	private JPanel getTN1(){
		FormLayout lay = new FormLayout("5dlu,right:max(50dlu;p),5dlu,p:g,5dlu",
				"5dlu,p,5dlu,p,5dlu,p,5dlu,p,5dlu:g,75dlu");
		CellConstraints cc = new CellConstraints();
		PanelBuilder pb = new PanelBuilder(lay);
		pb.addLabel("OU=IK-Alias ( z.B.: IK540840108)",cc.xy(2,2));
		pb.add((tn1[0] = new JTextField(Constants.PRAXIS_OU_ALIAS)),cc.xy(4,2));
		pb.addLabel("OU=Praxisname",cc.xy(2,4));
		pb.add((tn1[1] = new JTextField(Constants.PRAXIS_OU_FIRMA)),cc.xy(4,4));
		pb.addLabel("CN=Ansprechpartner",cc.xy(2,6));
		pb.add((tn1[2] = new JTextField(Constants.PRAXIS_CN)),cc.xy(4,6));
		pb.addLabel("Passwort (max. 6 Zeichen)",cc.xy(2,8));
		pb.add((tn1[3] = new JTextField(Constants.PRAXIS_KS_PW)),cc.xy(4,8));
		tabmodprax = new MyCertTableModel();
		tabmodprax.setColumnIdentifiers(new String[] {"Alias","Zert oder Key?","CA-Root","Gültig bis","zurückgezogen"});
		tabprax = new JXTable(tabmodprax);
		tabprax.getColumn(2).setCellRenderer(new MitteRenderer());
		tabprax.getColumn(2).setMaxWidth(60);
		tabprax.getColumn(4).setMaxWidth(60);
		tabprax.getColumn(1).setMaxWidth(60);
		tabprax.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent evt){
				if(evt.getClickCount()==2){
					try {
						doCertAuswerten();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		tabprax.validate();
		//tabprax.getColumn(1).setCellRenderer(new JLabelRenderer());
		JScrollPane jscr = JCompTools.getTransparentScrollPane(tabprax);
		jscr.validate();
		pb.add(jscr,cc.xyw(2,10,3,CellConstraints.FILL,CellConstraints.BOTTOM));
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				//doFuelleTabelle(false);
				return null;
			}
		}.execute();
		pb.getPanel().validate();
		return pb.getPanel();
	}

	private JPanel getTN2(){
		FormLayout lay = new FormLayout("5dlu,right:max(50dlu;p),5dlu,p:g,5dlu",
		//		 1    2  3   4  5   6  7   8  9   10  11    12
				"5dlu,p,5dlu,p,5dlu,p,5dlu,p,5dlu,p,5dlu:g,75dlu");
		CellConstraints cc = new CellConstraints();
		PanelBuilder pb = new PanelBuilder(lay);
		pb.getPanel().setBackground(Color.WHITE);
		pb.addLabel("OU=IK-Alias ( z.B.: IK999999999)",cc.xy(2,2));
		pb.add((tn2[0] = new JTextField(Constants.TEST_CA_OU_ALIAS)),cc.xy(4,2));
		pb.addLabel("OU=Firma",cc.xy(2,4));
		pb.add((tn2[1] = new JTextField(Constants.TEST_CA_OU_FIRMA)),cc.xy(4,4));
		pb.addLabel("CN=Ansprechpartner",cc.xy(2,6));
		pb.add((tn2[2] = new JTextField(Constants.TEST_CA_CN)),cc.xy(4,6));
		pb.addLabel("O=CA-Organisation",cc.xy(2,8));
		pb.add((tn2[3] = new JTextField(Constants.TEST_CA_O)),cc.xy(4,8));
		pb.addLabel("Passwort (max. 6 Zeichen)",cc.xy(2,10));
		pb.add((tn2[4] = new JTextField(Constants.TEST_CA_KS_PW)),cc.xy(4,10));
		tabmodca = new MyCertTableModel();
		tabmodca.setColumnIdentifiers(new String[] {"Alias","Zert oder Key?","CA-Root","Gültig bis","zurückgezogen"});
		tabca = new JXTable(tabmodca);
		JScrollPane jscr = JCompTools.getTransparentScrollPane(tabca);
		jscr.validate();
		pb.add(jscr,cc.xyw(2,12,3,CellConstraints.FILL,CellConstraints.BOTTOM));
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				doFuelleTabelle(true);
				return null;
			}
		}.execute();
		pb.getPanel().validate();
		return pb.getPanel();
	}
	
	private JPanel getButs1(){
		FormLayout lay = new FormLayout("5dlu,p,2dlu,p,2dlu,p,2dlu,p,5dlu",
		"5dlu,p,2dlu,p,5dlu");
		CellConstraints cc = new CellConstraints();
		PanelBuilder pb = new PanelBuilder(lay);
		pb.add((but1[0] = macheBut("KStore gen.","kgen1")),cc.xy(2,2));
		pb.add((but1[1] = macheBut("Reques. gen.","requgen1")),cc.xy(4,2));
		pb.add((but1[2] = macheBut("ReqRepl. read","replread1")),cc.xy(6,2));
		pb.add((but1[3] = macheBut("AnnKey read","annread1")),cc.xy(8,2));
		pb.add((but1[4] = macheBut("Encode test","enc1")),cc.xy(2,4));
		pb.add((but1[4] = macheBut("Build 1.chain","dec1")),cc.xy(4,4));
		pb.add((but1[5] = macheBut("Cert create","create1")),cc.xy(6,4));
		pb.add((but1[6] = macheBut("löschen","sha1test1")),cc.xy(8,4));
		pb.getPanel().validate();
		return pb.getPanel();
	}
	private JPanel getButs2(){
		FormLayout lay = new FormLayout("5dlu,p,2dlu,p,2dlu,p,2dlu,p,5dlu",
		"5dlu,p,2dlu,p,5dlu");
		CellConstraints cc = new CellConstraints();
		PanelBuilder pb = new PanelBuilder(lay);
		pb.add((but2[0] = macheBut("KStore gen.","kgen2")),cc.xy(2,2));
		pb.add((but2[1] = macheBut("Request read / Reply generate","requread")),cc.xy(4,2));
		pb.add((but2[2] = macheBut("save TestCase-settings","savetc")),cc.xy(4,4));
		pb.add((but2[3] = macheBut("Zert.Antrag","certantrag")),cc.xy(2,4));
		pb.getPanel().validate();
		return pb.getPanel();

	}	

	public JButton macheBut(String titel,String cmd){
		JButton but = new JButton(titel);
		but.setActionCommand(cmd);
		but.addActionListener(this);
		return but;
	}
	public void doFuelleTabelle(boolean isRoot) throws Exception{
		// Hier werden die beiden Tabellen mit den Angaben zu enthaltenen Zertifikaten gefüllt
		String keystore = (isRoot ? "" : "");
		String pw = (isRoot ? "" : "");
		MyCertTableModel mod = (isRoot ? tabmodca : tabmodprax);
		mod.setRowCount(0);
		KeyStore store = BCStatics2.loadStore(Constants.KEYSTORE_DIR+File.separator+keystore, pw);
		//Hier die Enumeration durch die Aliases und dann die Tabellen füllen
	}
	@Override
	public void actionPerformed(ActionEvent arg0){
		String cmd = arg0.getActionCommand();
		setVecs();
		try{
			/***********Keystore anlegen******(April/April********/
			if(cmd.equals("kgen1")){
				String inFile = Constants.KEYSTORE_DIR+File.separator+"TSOL0021.org";
				String decFile = Constants.KEYSTORE_DIR+File.separator+"encrypted.dat";
				String kkAlias = "IK109900019";
				
				KeyStore store = BCStatics2.loadStore(Constants.KEYSTORE_DIR+File.separator+Constants.PRAXIS_OU_ALIAS.replace("IK", ""), Constants.PRAXIS_KS_PW);
				PrivateKey privKey = (PrivateKey) store.getKey(Constants.PRAXIS_OU_ALIAS, Constants.PRAXIS_KS_PW.toCharArray());
				FileStatics.BytesToFile(EncUtils.doNextEncTest4(inFile,kkAlias),new File(inFile+".encoded"));

				BodosDecryptor bodoDecrypt = new BodosDecryptor(this, store,privKey) ;
				FileInputStream inStream = new FileInputStream(inFile+".encoded");
				FileOutputStream outStream = new FileOutputStream(inFile+".decoded");
				bodoDecrypt.decrypt(inStream, outStream);
				inStream.close();
				outStream.close();
				
			}
			/*****Zertifikatsrequest* erzeugen*********/
			if(cmd.equals("requgen1")){
				doGenerateRequest();
				JOptionPane.showMessageDialog(null, "ZertifikatsRequest wurde erzeugt");
			}
			/****CertifikatsRequestReply einlesen ****/
			if(cmd.equals("replread1")){
				doReadAndManageReply();
				JOptionPane.showMessageDialog(null, "ZertifikatsReply wurde eingelesen");
			}
			/****Vergleicht den Fingerprint des Zerts der Datenbank mit dem der PEM-Datei ****/			
			if(cmd.equals("sha1test1")){
				doSHA1Test();
			}
			/*************/
			if(cmd.equals("dec1")){
				//doVergleichen();
				doMacheKeyStoreNeu();
			}

			if(cmd.equals("enc1")){
				//doCertTest();
				doCertInTable();
				doEncode();
				//doCertInTable();
			}
			if(cmd.equals("create1")){
				doCertCreate();
			}
			if(cmd.equals("annread1")){
				doAnnahmeReadAndStore();
			}

			
			/***********Keystore f�r CA anlegen********/
			if(cmd.equals("kgen2")){
				doKeystore(caPassw,true);
				JOptionPane.showMessageDialog(null, "Keystore wurde erzeugt");
			}
			/*****Zertifikatsrequest einlesen (CA)*****/
			if(cmd.equals("requread")){
				doRequestEinlesen();
				JOptionPane.showMessageDialog(null, "ZertifikatsRequest wurde eingelesen und Reply wurde erzeugt");
			}
			if(cmd.equals("savetc")){
				saveTestCase();
			}
			if(cmd.equals("certantrag")){
				//doCertAntrag();
				//new EnvelopeFile("C:/Nebraska/ESOL0655.org");
			}

		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	public void doNextEncTest2() throws Exception{
		BCStatics.providerTest();
		byte[] origBytes = FileStatics.BytesFromFile(new File(Constants.KEYSTORE_DIR+File.separator+"TSOL0021.org"));
		System.out.println("Länge des OriginalFiles in Byte: "+origBytes.length);
		byte[] result = createSignedMessage(
				origBytes,
				Constants.PRAXIS_OU_ALIAS,
				Constants.PRAXIS_OU_ALIAS,
				"IK109900019",
				"196205");
		FileStatics.BytesToFile(result, new File(Constants.KEYSTORE_DIR+File.separator+"TSOL0021"));
	}
	/******************************************************************/
	public byte[] createSignedMessage(byte[] dataParm,String pKeyAlias,String ourCertAlias,String theiCertAlias,String pw) {
		byte[] signedByteEnvelope = null;
		byte[] signedByteData = null;
		//String encodedData = null;
		X509Certificate ourCert = null;
		X509Certificate theirCert = null;
		CMSProcessable signContent, envContent = null;
		KeyStore keystore = null;
		PrivateKey priv = null;

		char[] passPhrase = pw.toCharArray();
		char[] keyPassPhrase = pw.toCharArray();
		String privateKeyAlias = pKeyAlias;
		String ourAlias = ourCertAlias;
		String theirAlias = theiCertAlias;
		String keyStore = Constants.KEYSTORE_DIR+File.separator+Constants.PRAXIS_OU_ALIAS.replace("IK", "")+".p12";

		try{
		keystore = KeyStore.getInstance("BCPKCS12","BC");
		keystore.load(new FileInputStream(keyStore),passPhrase);
		priv = (PrivateKey)(keystore.getKey(privateKeyAlias, keyPassPhrase));
		ourCert = (X509Certificate)keystore.getCertificate(ourAlias);
		theirCert = (X509Certificate)keystore.getCertificate(theirAlias);
		}
		catch(Exception exc){
		System.out.println("Problem with keystore access: " +exc.toString()) ;
		return exc.getMessage().getBytes();
		}

		// Use Bouncy Castle provider to create CSM/PKCS#7 signed message ---
		try{
			CMSSignedDataGenerator signGen = new CMSSignedDataGenerator();
			signGen.addSigner(priv, ourCert, CMSSignedDataGenerator.DIGEST_SHA1);
			
			signContent = new CMSProcessableByteArray(dataParm);
			CMSSignedData signedData = signGen.generate(signContent,true,"BC");
			signedByteData = signedData.getEncoded();
			//signedData = new CMSSignedData(dataParm,signedData.getEncoded());
			System.out.println("Länge der signierten Bytes: "+signedByteData.length);

			//encodedData = new Base64Encoder().encode(signedByteData);
		}
		catch(Exception ex){
			System.out.println("Couldn't generate CMS signed message\n"+ex.toString()) ;
			ex.printStackTrace();
		}


		// Use Bouncy Castle provider to create CSM/PKCS#7 enveloped message ---
		try{
			String algorithm = CMSEnvelopedDataGenerator.DES_EDE3_CBC;
			//int keysize = 128; // bits
			CMSEnvelopedDataGenerator envGen = new CMSEnvelopedDataGenerator();
			envGen.addKeyTransRecipient(theirCert);

			envContent = new CMSProcessableByteArray(signedByteData);
			CMSEnvelopedData envData = envGen.generate(envContent, algorithm, "BC");
			signedByteEnvelope = envData.getEncoded();
			System.out.println("Länge der signierten Bytes (Enveloped): "+signedByteEnvelope.length);
			//encodedData = new BASE64Encoder().encode(signedByteData);
		}
		catch(Exception ex){
			System.out.println("Couldn't generate CMS enveloped message\n" + ex.toString()) ;
			ex.printStackTrace();
		}
		return signedByteEnvelope;
		//return encodedData;
		}	
	
	/******************************************************************/
	public void doNextDecTest() throws Exception{
		System.out.println("****************Beginn der Entschlüsselung********************");
		BCStatics.providerTest();
		byte[] dataIn = FileStatics.BytesFromFile(new File(Constants.KEYSTORE_DIR+File.separator+"TSOL0021.org.encoded"));
		CMSEnvelopedData envelopedData = new CMSEnvelopedData(dataIn);
		org.bouncycastle.asn1.cms.ContentInfo coInfo = envelopedData.getContentInfo();
		DEREncodable enc = coInfo.getContent();

		
		//System.out.println("*****"+new String(envelopedData.getContentInfo().getEncoded()));
		System.out.println(envelopedData.getRecipientInfos().getRecipients().iterator());
/*******************/

/*******************/		
		KeyStore store = BCStatics2.loadStore(Constants.KEYSTORE_DIR+File.separator+Constants.PRAXIS_OU_ALIAS.replace("IK", ""), Constants.PRAXIS_KS_PW);
		System.out.println("EncryptionAlgOID "+ envelopedData.getEncryptionAlgOID());
		envelopedData = new CMSEnvelopedData(envelopedData.getEncoded());
		
		Iterator it = envelopedData.getRecipientInfos().getRecipients().iterator();

		
		PrivateKey privkey = (PrivateKey) store.getKey(Constants.PRAXIS_OU_ALIAS, Constants.PRAXIS_KS_PW.toCharArray());		

		RecipientInformationStore recipients = envelopedData.getRecipientInfos();
		Collection col = recipients.getRecipients();
		Iterator itx = col.iterator();
		while(itx.hasNext()){
			RecipientInformation inform = ((RecipientInformation)itx.next());
			//System.out.println( new String(inform.getRID().getKeyIdentifier()));
			
		}
		
		System.out.println("KeyTransRecipientInformation"+recipients.getRecipients());
		
		RecipientInformation recipient = null;
		X509Certificate xcert = null;
		boolean keyOwner = false;
		RecipientInformation inform = null;
		while(it.hasNext()){
			inform =  ((RecipientInformation)it.next());
			//inform.getContent(arg0, arg1)
			try{
        	String sRID = inform.getRID().getSerialNumber().toString();

        	RecipientId iRID = inform.getRID();
        	//System.out.println(((RecipientInformation)it.next()).getRID());
        	Enumeration<?> en = store.aliases();

        	recipient = envelopedData.getRecipientInfos().get(iRID);
        	
			while (en.hasMoreElements()){
				String aliases = (String)en.nextElement();
				if(aliases != null){
					xcert = (X509Certificate) store.getCertificate(aliases);
					String serial = xcert.getSerialNumber().toString(16);
					int i = Integer.valueOf(sRID);
					String hexRID =  Integer.toHexString(i);
					if(serial.equals(hexRID) /*&& xcert.getSubjectDN().toString().contains(Constants.PRAXIS_OU_ALIAS)*/){
						//System.out.println("Zertifikat gefunden = "+xcert.getSubjectDN());
						
			        	byte[] kident = null;
			        	System.out.println("Issuer: "+iRID.getIssuer());
			        	kident = iRID.getKeyIdentifier();
			        	if(kident!=null){
				        	for(int i2 = 0;i2<kident.length;i2++ ){
				        		System.out.println((int)kident[i2]);
				        	}
			        	}else{
			        		System.out.println("KeyIdentifier == null");
			        	}

						keyOwner = true;
						break;
					}else{
						System.out.println("Seriennummer nicht gefunden");
					}
				}
			}
			}catch(Exception ex){
				
			}

			if(keyOwner){
				break;
			}
			//System.out.println(new String(envelopedData.getEncryptionAlgParams()));
			//recipient.getContent(privkey, "BC");
        }
		
		RecipientId rid = inform.getRID();
		RecipientInformation reci = recipients.get(rid);


		RecipientId iRID = inform.getRID();
		//RecipientInfo   recip = RecipientInfo.getInstance(iRID);
		RecipientInformationStore rstore = envelopedData.getRecipientInfos();
		Collection rcols = rstore.getRecipients();
		Iterator ity = rcols.iterator();
		
		
		//System.out.println(xcert);
		//System.out.println(recipient.getKeyEncryptionAlgOID());
		//System.out.println(envelopedData.getContentInfo().getDERObject());

		DERObject der = envelopedData.getContentInfo().getDERObject();

		System.out.println(der.toASN1Object());
		ASN1Object obj = ASN1Object.fromByteArray(envelopedData.getEncoded());
		
		String str = ASN1Dump.dumpAsString(obj);
		//ASN1TaggedObject.getInstance(obj);
		//System.out.println("Dump As String "+str);
		ASN1Object asn = (ASN1Object) der.toASN1Object(); 
		
		//System.out.println("ASN1Object = "+asn);
		ASN1InputStream ain = new ASN1InputStream(asn.getDEREncoded());
		DERObject der2 = ain.readObject();

		ASN1Sequence seq = (ASN1Sequence)der.toASN1Object();
		if(seq != null){
			System.out.println("Sequenzen = "+seq.size());
			int seqs = seq.size();
			for(int i = 0; i < seqs;i++){
				//System.out.println(seq.getObjectAt(i));
				System.out.println(seq.getObjectAt(i).getDERObject());

				try{
					BERTaggedObject btag = null;
					if(seq.getObjectAt(i).getDERObject().toASN1Object() instanceof org.bouncycastle.asn1.DERObjectIdentifier){
						org.bouncycastle.asn1.DERObjectIdentifier ident = (DERObjectIdentifier)seq.getObjectAt(i).getDERObject().toASN1Object();
						System.out.println(ident);
						continue;
						
					}else{
						btag = (BERTaggedObject)((DERObject)seq.getObjectAt(i).getDERObject()).toASN1Object();	
					}
					
					//ASN1Sequence seq2 = (ASN1Sequence)((DERObject)seq.getObjectAt(i).getDERObject()).toASN1Object();
					
					System.out.println("Tag No="+btag.getObject());
					BERSequence bseq = (BERSequence)btag.getObject();
					int bsize = bseq.size();
					System.out.println("BER-sequenzen = "+bsize);
					for(int i2 = 0; i2 < bsize;i2++){
						System.out.println("Sequenz "+i2+" - "+bseq.getObjectAt(i2));

						if( (bseq.getObjectAt(i2) instanceof org.bouncycastle.asn1.DERSet) ){
							DERSet dset = (DERSet)bseq.getObjectAt(i2);		
							int dsize = dset.size();
							for(int i3 = 0;i3 < dsize;i3++){
								System.out.println("DSet "+i3+" - "+dset.getObjectAt(i3));
							}
						}else if(bseq.getObjectAt(i2) instanceof org.bouncycastle.asn1.BERSequence){
							BERSequence bseq2 = (BERSequence)bseq.getObjectAt(i2);
							for(int i4 = 0;i4 < bseq2.size();i4++){
								System.out.println("Sequenz2 "+i4+" - "+bseq2.getObjectAt(i4));
								System.out.println("Klasse Sequenz2 "+i4+" - "+bseq2.getObjectAt(i4).getClass());
								if(bseq2.getObjectAt(i4) instanceof DERObjectIdentifier){
									System.out.println("ObjectIdentifier i4 = "+bseq2.getObjectAt(i4));
								}else if(bseq2.getObjectAt(i4) instanceof org.bouncycastle.asn1.DERSequence){
									DERSequence bseq3 = (DERSequence)bseq2.getObjectAt(i4);
									for(int i5 = 0;i5 < bseq3.size();i5++){
										System.out.println("Sequenz von i5 Nr. "+i5+" = "+bseq3.getObjectAt(i5));
										System.out.println("Klasse von  i5 Nr. "+i5+" = "+bseq3.getObjectAt(i5).getClass());
										if(bseq3.getObjectAt(i5) instanceof DEROctetString){
											ASN1OctetString doct = (ASN1OctetString)((DEROctetString)bseq3.getObjectAt(i5)).parser();
											//System.out.println("OktetString i5 = "+new String(doct.getOctets()));
											System.out.println("toHex i5 = "+NUtils.toHex(doct.getOctets()));
											KeyPair kp = BCStatics2.getBothFromPem(Constants.KEYSTORE_DIR+File.separator+"540840108");
											PrivateKey pk = kp.getPrivate();
											ASN1Object pkobj = ASN1Object.fromByteArray(pk.getEncoded());
											ASN1Sequence encodedSeq =(ASN1Sequence)pkobj; 
											PrivateKeyInfo prikInfo = new PrivateKeyInfo(encodedSeq);
								            //DERObject derobj= prikInfo.getPrivateKey();
								            System.out.println(prikInfo.getPrivateKey());
 
											RSAPrivateKeyStructure privStruct = new RSAPrivateKeyStructure((DERSequence) prikInfo.getPrivateKey());
									           BigInteger coeficiente = privStruct.getCoefficient();
									            BigInteger modulo = privStruct.getModulus();
									            BigInteger exponente1 = privStruct.getExponent1();
									            BigInteger exponente2 = privStruct.getExponent2();
									            BigInteger primo1 = privStruct.getPrime1();
									            BigInteger primo2 = privStruct.getPrime2();
									            BigInteger privateExponent = privStruct.getPrivateExponent();
									            BigInteger publicExponent = privStruct.getPublicExponent();

									            RSAPrivateCrtKeyParameters keySpec = new RSAPrivateCrtKeyParameters(
									                    modulo, publicExponent, privateExponent, primo1, primo2,
									                    exponente1, exponente2, coeficiente); 											
									            AsymmetricBlockCipher rsaEngine = new RSAEngine();
									            rsaEngine.init(false, keySpec); // if true = encryption
									            String decrypted = ""; 
									            byte[] toDEcrypt = doct.getOctets();
									            byte[] bcrsacipher = rsaEngine.processBlock(toDEcrypt, 0, toDEcrypt.length);
								            decrypted = new String(NUtils.toHex(bcrsacipher)); 
								            System.out.println("Decrypted = "+decrypted);
										}
									}
								}else if(bseq2.getObjectAt(i4) instanceof org.bouncycastle.asn1.BERTaggedObject){
									BERTaggedObject btag5 = (BERTaggedObject)bseq2.getObjectAt(i4);
									System.out.println("TaggedObject btag5 =  "+btag5);
								}
							}
						}

					}
				}catch(Exception ex){
					System.out.println("Fehler");
					ex.printStackTrace();
				}

			}
				
		}else{
			System.out.println("seQ == null");
		}
		//System.out.println("ContentTyp = "+envelopedData.getContentInfo().getContentType());
		DERObjectIdentifier dident = envelopedData.getContentInfo().getContentType();
		//System.out.println("getDEREncoded = "+NUtils.toHex(dident.getDEREncoded()));
		
		
		
		ASN1EncodableVector avec = new ASN1EncodableVector();

		avec.add(der.getDERObject().toASN1Object());
		System.out.println(avec);
		//Attribute attributes = null;
		DEREncodable enc3 = avec.get(0);
		//System.out.println("DERObjekt = "+enc3.getDERObject());
		DEREncodableVector dervec = new DEREncodableVector();  
		dervec.add(der2);
		for(int i = 0 ; i < dervec.size();i++){
			//System.out.println(dervec.get(0).getDERObject());
		}
		
		

	}
	public byte[] doNextEncTest(String inFile,String kkAlias) throws Exception{
		X509Certificate praxiscert = null; //eigenes Zertifikat
		X509Certificate certRecipient = null; // Zertifikat des Empfängers
		
		BCStatics.providerTest();

		// Für den Test eigenes Zertifikat, den PrivKey und das Zertifikat des IKK-Bundesverbandes aus dem Keystore holen
		KeyStore store = BCStatics2.loadStore(Constants.KEYSTORE_DIR+File.separator+Constants.PRAXIS_OU_ALIAS.replace("IK", ""), Constants.PRAXIS_KS_PW);
		praxiscert = (X509Certificate) store.getCertificate(Constants.PRAXIS_OU_ALIAS);
        PrivateKey privkey = (PrivateKey) store.getKey(Constants.PRAXIS_OU_ALIAS, Constants.PRAXIS_KS_PW.toCharArray());
        certRecipient = (X509Certificate) store.getCertificate("IK109900019");        

        /*********************Secret Key *****************************/
		KeyGenerator    keyGen = KeyGenerator.getInstance("DESEDE", "BC");
        SecretKey       key  = keyGen.generateKey();
        byte[] kekID = {1, 17, 9, 5, 2, 8, 24, 18};
        
        /******************Signed Data*************************/
		CMSSignedDataGenerator sgen = new CMSSignedDataGenerator();
		sgen.addSigner(privkey,praxiscert,CMSSignedDataGenerator.DIGEST_SHA1);
		CMSProcessable dataToSign = new CMSProcessableFile(new File(inFile));
		CMSSignedData signed = sgen.generate(dataToSign,true,"BC");
		//erneut laden
		signed = new CMSSignedData(dataToSign,signed.getEncoded());

		//Signatur mit dem PublicKey verifizieren auf der Absenderseite überflüssig
		Iterator<?> its = signed.getSignerInfos().getSigners().iterator();
		if(its.hasNext()){
			SignerInformation signer = (SignerInformation) its.next();
			System.out.println("Verifikation des Signierers = "+signer.verify(praxiscert.getPublicKey(), "BC"));
		}

		/******************Enveloped Data*************************/
        // Generate an AES key
        KeyGenerator keygen = KeyGenerator.getInstance("DESEDE", "BC");
        keygen.init(new SecureRandom());
        Key seckey = keygen.generateKey();
        String[] names = getCryptoImpls("SecureRandom");
        for(int p = 0; p < names.length; p++){
        	System.out.println("SecureRandom "+p+" = "+names[p]);
        }
        CMSEnvelopedDataGenerator gen = new CMSEnvelopedDataGenerator();
        //gen.addKeyTransRecipient(certRecipient); //IKK
        //gen.addKeyAgreementRecipient( CMSEnvelopedGenerator.DES_EDE3_CBC, privkey, cert.getPublicKey(), certRecipient,  CMSEnvelopedGenerator.DES_EDE3_WRAP, "BC");
        gen.addKeyTransRecipient(praxiscert); //zum Test auf eigenes Zert verschlüsseln
        //gen.addKEKRecipient(key, kekID);

        
        //CMSProcessable data = new CMSProcessableFile(new File(Constants.KEYSTORE_DIR+File.separator+"TSOL0021.org"));

        CMSEnvelopedData envelopedData = gen.generate(signed.getSignedContent(),CMSEnvelopedDataGenerator.DES_EDE3_CBC ,"BC");
        //CMSEnvelopedData envelopedData = gen.generate(data,CMSEnvelopedDataGenerator.DES_EDE3_CBC ,"BC");
        //envelopedData = new CMSEnvelopedData(envelopedData.getEncoded());
        
        RecipientId recID = new RecipientId();
        
        /********************************/
        // Generate an AES key

        // Use the AES key to encrypt the doc, use the public key
        // to encrypt the AES key
        /*
        EncryptionKey ekey = EncryptionKey.
        Encryption enc = security.getEncryption();
        EncryptionOptions options = enc.getDefaultEncryptionOptions();
        options.setDataEncryptionKey(key);
        options.setKeyEncryptionKey(pkey);
        options.setKeyCipherAlgorithm(XMLCipher.RSA_v1dot5);
        options.setIncludeKeyInfo(true);
        */
        
        
        
        /********************************/
        
        recID.setKeyIdentifier(kekID);
        //recID.setCertificate(certRecipient);
        //recID.setSubjectPublicKey(pubRecipient.getEncoded());
        
        recID.setCertificate(praxiscert);
        recID.setSubjectPublicKey(praxiscert.getPublicKey().getEncoded());


        
        RecipientInformationStore recipients = envelopedData.getRecipientInfos();

        RecipientInformation recipient = recipients.get(recID);
        


        System.out.println("KeyIdentifier = "+NUtils.toHex(recID.getKeyIdentifier()));
        System.out.println("Länge des KeyIdentifier = "+recID.getKeyIdentifier().length);

        //System.out.println(new String(recipient.getContent(key, "BC")));
        
        System.out.println("Recipients = "+envelopedData.getRecipientInfos().size());
        System.out.println("ContentType = "+envelopedData.getContentInfo().getContentType());
        System.out.println("EncryptionAlgOID = "+envelopedData.getEncryptionAlgOID());
        System.out.println("EnvelopedData = "+new String(envelopedData.getEncoded()));

        FileStatics.BytesToFile(envelopedData.getEncoded(), new File(Constants.KEYSTORE_DIR+File.separator+"TSOL0021"));
        //System.out.println(recipient.getKeyEncryptionAlgOID());
        /*
        Iterator it = recipients.getRecipients().iterator();
        while(it.hasNext()){
        	RecipientInformation inform =  ((RecipientInformation)it.next());
        	String sRID = inform.getRID().getSerialNumber().toString();//((RecipientInformation)it.next()).getRID().getSerialNumber().toString();
        	RecipientId iRID = inform.getRID();
        	System.out.println("KeyIdetifier = "+inform.getRID().getKeyIdentifier());
        	System.out.println("RIDCertifikat = "+inform.getRID().getCertificate());

        	Enumeration<?> en = store.aliases();
			while (en.hasMoreElements()){
				String aliases = (String)en.nextElement();
				if(aliases != null){
					X509Certificate xcert = (X509Certificate) store.getCertificate(aliases);
					String serial = xcert.getSerialNumber().toString(16);
					int i = Integer.valueOf(sRID);
					String hexRID =  Integer.toHexString(i);
					if(serial.equals(hexRID)){
						System.out.println("Zertifikat gefunden = "+xcert.getSubjectDN());
					}
				}
			}	
        }
        */
        return envelopedData.getEncoded();
        
	}
	public static String[] getCryptoImpls(String serviceType) { 
		Set result = new HashSet(); 
		// All all providers 
		Provider[] providers = Security.getProviders(); 
		for (int i=0; i<providers.length; i++) { 
			// Get services provided by each provider 
			Set keys = providers[i].keySet(); 
			for (Iterator it=keys.iterator(); it.hasNext(); ) { 
				String key = (String)it.next(); key = key.split(" ")[0]; 
				if (key.startsWith(serviceType+".")) { 
					result.add(key.substring(serviceType.length()+1)); 
				} else if (key.startsWith("Alg.Alias."+serviceType+".")){ // This is an alias 
					result.add(key.substring(serviceType.length()+11)); 
				} 
			} 
		} 
		return (String[])result.toArray(new String[result.size()]); 
	} 
	
	public void doNextEncTest3() throws Exception{
		X509Certificate cert = null; //eigenes Zertifikat
		X509Certificate certRecipient = null; // Zertifikat des Empfängers
		BCStatics.providerTest();

		// Für den Test eigenes Zertifikat, den PrivKey und das Zertifikat des IKK-Bundesverbandes aus dem Keystore holen
		KeyStore store = BCStatics2.loadStore(Constants.KEYSTORE_DIR+File.separator+Constants.PRAXIS_OU_ALIAS.replace("IK", ""), Constants.PRAXIS_KS_PW);
        cert = (X509Certificate) store.getCertificate(Constants.PRAXIS_OU_ALIAS);
        PrivateKey privkey = (PrivateKey) store.getKey(Constants.PRAXIS_OU_ALIAS, Constants.PRAXIS_KS_PW.toCharArray());
        certRecipient = (X509Certificate) store.getCertificate("IK109900019");        

        /*********************Secret Key *****************************/
		KeyGenerator    keyGen = KeyGenerator.getInstance("DESEDE", "BC");
        SecretKey       key  = keyGen.generateKey();
        byte[] kekID = {1, 17, 9, 5, 2, 8, 24, 18};
        
        CMSEnvelopedDataGenerator gen = new CMSEnvelopedDataGenerator();
        
        if(cert==null){
        	System.out.println("Zertifikat = null");
        	return;
        }
  

        //gen.addKeyTransRecipient(certRecipient); //IKK
        //gen.addKeyAgreementRecipient( CMSEnvelopedGenerator.DES_EDE3_CBC, privkey, cert.getPublicKey(), certRecipient,  CMSEnvelopedGenerator.DES_EDE3_WRAP, "BC");
        gen.addKeyTransRecipient(cert); //zum Test auf eigenes Zert verschlüsseln
        
        CMSProcessable data = new CMSProcessableFile(new File(Constants.KEYSTORE_DIR+File.separator+"TSOL0021.org"));

        CMSEnvelopedData envelopedData = gen.generate(data,CMSEnvelopedDataGenerator.DES_EDE3_CBC ,"BC");
        envelopedData = new CMSEnvelopedData(envelopedData.getEncoded());
        
        RecipientId recID = new RecipientId();
        
        /********************************/
        // Generate an AES key
        KeyGenerator keygen = KeyGenerator.getInstance("DESEDE", "BC");
        keygen.init(64,new SecureRandom());
        Key seckey = keygen.generateKey();
       
        recID.setKeyIdentifier(kekID);
        
        recID.setCertificate(cert);
        recID.setSubjectPublicKey(cert.getPublicKey().getEncoded());

        
        RecipientInformationStore recipients = envelopedData.getRecipientInfos();

        RecipientInformation recipient = recipients.get(recID);
        


        System.out.println("KeyIdentifier = "+NUtils.toHex(recID.getKeyIdentifier()));
        System.out.println("Länge des KeyIdentifier = "+recID.getKeyIdentifier().length);

        //System.out.println(new String(recipient.getContent(key, "BC")));
        
        System.out.println("Recipients = "+envelopedData.getRecipientInfos().size());
        System.out.println("ContentType = "+envelopedData.getContentInfo().getContentType());
        System.out.println("EncryptionAlgOID = "+envelopedData.getEncryptionAlgOID());
        System.out.println("EnvelopedData = "+new String(envelopedData.getEncoded()));
        
        
        /******************Signed Data*************************/
		CMSSignedDataGenerator sgen = new CMSSignedDataGenerator();
		sgen.addSigner(privkey,cert,CMSSignedDataGenerator.DIGEST_SHA1);
		//CMSProcessable dataToSign = new CMSProcessableFile(new File(Constants.KEYSTORE_DIR+File.separator+"TSOL0021.org"));
		//CMSSignedData signed = sgen.generate(dataToSign,"BC");
		//signed = new CMSSignedData(dataToSign,signed.getEncoded());
		CMSSignedData signed = sgen.generate((CMSProcessable) envelopedData.getContentInfo().getContent(),"BC");
		

		//Signatur mit dem PublicKey verifizieren auf der Absenderseite überflüssig
		Iterator<?> its = signed.getSignerInfos().getSigners().iterator();
		if(its.hasNext()){
			SignerInformation signer = (SignerInformation) its.next();
			System.out.println("Verifikation des Signierers = "+signer.verify(cert.getPublicKey(), "BC"));
		}

		/******************Enveloped Data*************************/

        FileStatics.BytesToFile(signed.getEncoded(), new File(Constants.KEYSTORE_DIR+File.separator+"TSOL0021"));
        //System.out.println(recipient.getKeyEncryptionAlgOID());
        /*
        Iterator it = recipients.getRecipients().iterator();
        while(it.hasNext()){
        	RecipientInformation inform =  ((RecipientInformation)it.next());
        	String sRID = inform.getRID().getSerialNumber().toString();//((RecipientInformation)it.next()).getRID().getSerialNumber().toString();
        	RecipientId iRID = inform.getRID();
        	System.out.println("KeyIdetifier = "+inform.getRID().getKeyIdentifier());
        	System.out.println("RIDCertifikat = "+inform.getRID().getCertificate());

        	Enumeration<?> en = store.aliases();
			while (en.hasMoreElements()){
				String aliases = (String)en.nextElement();
				if(aliases != null){
					X509Certificate xcert = (X509Certificate) store.getCertificate(aliases);
					String serial = xcert.getSerialNumber().toString(16);
					int i = Integer.valueOf(sRID);
					String hexRID =  Integer.toHexString(i);
					if(serial.equals(hexRID)){
						System.out.println("Zertifikat gefunden = "+xcert.getSubjectDN());
					}
				}
			}	
        }
        */
        
	}

	public void doCertAuswerten() throws Exception{
		int row = tabprax.getSelectedRow();
		if(row < 0){return;}
		String alias = (String) tabprax.getValueAt(row,0);
		KeyStore store = BCStatics2.loadStore(Constants.KEYSTORE_DIR+File.separator+"540840108", Constants.PRAXIS_KS_PW);
		X509Certificate cert = (X509Certificate) store.getCertificate(alias);
		String certInfo =    "       Seriennummer = "+NUtils.toHex(cert.getSerialNumber().toByteArray())+"\n";
		certInfo = certInfo+ "    BasicConstaints = "+Integer.toString(cert.getBasicConstraints())+"\n";
		certInfo = certInfo+ "SignaturAlgorithmus = "+cert.getSigAlgName().toString()+"\n";
		certInfo = certInfo+ "     SignaturAlgOID = "+cert.getSigAlgOID().toString()+"\n";
		certInfo = certInfo+ "   Certificate-Type = "+cert.getType().toString()+"\n";
		certInfo = certInfo+ "Certificate-Version = "+Integer.toString(cert.getVersion())+"\n";
		certInfo = certInfo+ "         Subject-DN = "+cert.getSubjectDN().toString()+"\n";

		PublicKey pubKey = cert.getPublicKey();
		ASN1InputStream aIn = new ASN1InputStream(cert.getPublicKey().getEncoded());
		SubjectPublicKeyInfo sub = SubjectPublicKeyInfo.getInstance(aIn.readObject());

		certInfo = certInfo+ " MD5-Fingerpr. Cert = "+BCStatics2.macheHexDump(BCStatics2.getMD5fromByte(cert.getEncoded()), 20," ")+"\n";		
		certInfo = certInfo+ "MD5-Fingerpr.PubKey = "+BCStatics2.macheHexDump(BCStatics2.getMD5fromByte(sub.getPublicKeyData().getBytes()), 20," ")+"\n";		
		boolean[] ku = cert.getKeyUsage();
		if(ku != null){
			String inter ="";
			for(int i = 0;i<ku.length;i++){
				inter = inter + Boolean.toString(ku[i])+" ";
				//System.oucert.println(ku[i]);
			}
			certInfo = certInfo+ "       KeyUsage = "+inter+"\n";
		}
		byte[] b = cert.getExtensionValue("2.5.29.19");
		if(b != null){
			aIn = new ASN1InputStream(b);
			ASN1OctetString extnValue = (ASN1OctetString) aIn.readObject();
			certInfo = certInfo+ "    ExtensionValue = "+extnValue+"\n";
			aIn = new ASN1InputStream(extnValue.getOctets());
			DERObject extensionType = aIn.readObject();
			certInfo = certInfo+ "    ExtensionType = "+extensionType+"\n";
			System.out.println(extensionType);
		}
		JOptionPane.showMessageDialog(null, certInfo);

	}
	private void doCertInTable() throws Exception{
		KeyStore store = BCStatics2.loadStore(Constants.KEYSTORE_DIR+File.separator+"540840108", Constants.PRAXIS_KS_PW);
		Enumeration<?> en = store.aliases();
		Vector<X509Certificate> certVec = new Vector<X509Certificate>();
		Vector<String> certAlias = new Vector<String>();
		Vector<Vector> vecTabPrax = new Vector<Vector>(); 
		int durchlauf = 0;
		tabmodprax.setRowCount(0);
			while (en.hasMoreElements()){
				String aliases = (String)en.nextElement();
				if(aliases != null){
					if(store.isCertificateEntry(aliases)){
						//System.out.println("ZertifikatEntry = "+store.getEntry(aliases, null));
						//certVec.add((X509Certificate) store.getCertificate(aliases));
						//certAlias.add(aliases);
						vecTabPrax.add(macheTabellenZeile(false,aliases,(X509Certificate) store.getCertificate(aliases)));
					}else{
						//System.out.println("KeyEntry = "+store.getEntry(aliases,new KeyStore.PasswordProtection(Constants.PRAXIS_KS_PW.toCharArray()) ));
						//certVec.add((X509Certificate) store.getCertificate(aliases));
						//certAlias.add(aliases);
						vecTabPrax.add(macheTabellenZeile(true,aliases,(X509Certificate) store.getCertificate(aliases)));
					}
				}else{

				}

			}
	
	}
	private Vector macheTabellenZeile(boolean isKeyEntry,String alias,X509Certificate cert) throws Exception{
		Vector zeile = new Vector();
		zeile.add((String)alias);
		zeile.add( (isKeyEntry ? imgKey : imgCert));
		//byte[] ext = cert.getExtensionValue("1.3.6.1.5.5.7.3.1");
		byte[] ext = cert.getExtensionValue("2.5.29.19");
		//BasicConstraints bc = BasicConstraints.getInstance(cert.getBasicConstraints());
		zeile.add("?");
		zeile.add(sdf.format(cert.getNotAfter()));
		zeile.add("?");
		zeile.add(true);
		

		tabmodprax.addRow((Vector)zeile.clone());
		tabprax.setValueAt(imgKey, tabprax.getRowCount()-1, 1);
		return (Vector)zeile.clone();
		
	}
	private void doCertTest() throws Exception{
		KeyStore store = BCStatics2.loadStore(Constants.KEYSTORE_DIR+File.separator+"540840108", Constants.PRAXIS_KS_PW);
		/*
		Certificate[] chain = store.getCertificateChain("IK540840108");
		for(int i = 0; i < chain.length;i++){
			System.out.println(((X509Certificate)chain[i]).getSubjectDN());
		}*/
		Vector<X509Certificate> certVec = new Vector<X509Certificate>();
		Vector<String> certAlias = new Vector<String>();
		Enumeration<?> en = store.aliases();
		int durchlauf = 0;
			while (en.hasMoreElements()){
				String aliases = (String)en.nextElement();
				if(aliases != null){
					if(store.isCertificateEntry(aliases)){
						
						certVec.add((X509Certificate) store.getCertificate(aliases));
						certAlias.add(aliases);
					}else{
						
						certVec.add((X509Certificate) store.getCertificate(aliases));
						certAlias.add(aliases);
					}
				}else{
									}

			}
			System.out.println("Es sind bislang "+certVec.size()+" Zetrifikate im Vector");

			X509Certificate[] chainAnnahme = BCStatics3.holeZertifikate();
			System.out.println("Es sind "+chainAnnahme+" Zetrifikate in der AnnahmeKey");
			
			String alias ="";
			
			for(int i = 0;i < chainAnnahme.length;i++){
				alias = BCStatics3.extrahiereAlias(chainAnnahme[i].getSubjectDN().toString());
				if(alias==null){
					alias = chainAnnahme[i].getSubjectDN().toString();
				}
				if(! certVec.contains((X509Certificate) chainAnnahme[i])){
					certVec.add((X509Certificate) chainAnnahme[i]);
					certAlias.add(alias);
				}
			}
			KeyStore store2 = BCStatics3.erzeugeLeerenKeyStore(Constants.PRAXIS_KS_PW);
			
			int lang = certVec.size();
			System.out.println("Länge des Arrays = "+lang);
			X509Certificate[] chainGesamt = new X509Certificate[lang];
			for(int i = 0;i < lang;i++){
				try{
				chainGesamt[i] = certVec.get(i);
				}catch(Exception ex){
					System.out.println("Fahler bei Zertifikat "+i);
				}
			}
			//store.setKeyEntry(vecprax.get(0), kpprax.getPrivate(),praxisPassw.toCharArray(),chainGesamt );
			//BCStatics2.saveStore(store, praxisPassw, vecprax.get(0).replace("IK", ""));
			CertificateFactory fact = CertificateFactory.getInstance("X.509","BC");
			CertPath certPath = fact.generateCertPath(Arrays.asList(chainGesamt));
			List lcerts = certPath.getCertificates();
			for(int i = 0; i < lcerts.size();i++){
				alias = BCStatics3.extrahiereAlias(((X509Certificate) lcerts.get(i)).getSubjectDN().toString());
				if(alias==null){
					alias = ((X509Certificate) lcerts.get(i)).getSubjectDN().toString();
				}
				store2.setCertificateEntry(alias, (Certificate) lcerts.get(i));
				X509Certificate cert = (X509Certificate) lcerts.get(i);
				
				
			}
			 
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			store2.store(bout, Constants.PRAXIS_KS_PW.toCharArray());
			FileStatics.BytesToFile(bout.toByteArray(), new File(Constants.KEYSTORE_DIR+File.separator+"540840108.p12"));
			bout.close();
			//System.out.println(certPath);
			//byte[] bp = certPath.getEncoded();
			//FileStatics.BytesToFile(bp, new File(Constants.KEYSTORE_DIR+File.separator+"540840108.p12"));

			/*
			String alias = "";
			for(int i = 0; i < chainAnnahme.length;i++){
				alias = BCStatics3.extrahiereAlias(chainAnnahme[i].getSubjectDN().toString());
				store.setEntry(alias, new KeyStore.TrustedCertificateEntry(chainAnnahme[i]), null);
			}
			ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			store.store(bOut, Constants.PRAXIS_KS_PW.toCharArray());
			FileStatics.BytesToFile(bOut.toByteArray(), new File(Constants.KEYSTORE_DIR+File.separator+"540840108.p12"));
			bOut.close();
			*/
			
			
	}
	private void doMacheKeyStoreNeu() throws Exception{
		BCStatics.providerTest();
		KeyPair kp = BCStatics2.getBothFromPem(Constants.KEYSTORE_DIR+File.separator+"540840108");
		/*
		KeyPair kp = BCStatics2.getBothFromPem(Constants.KEYSTORE_DIR+File.separator+"540840108");
		KeyStore secretKeyStore = BCStatics2.loadStore(Constants.KEYSTORE_DIR+File.separator+"540840108_key", Constants.PRAXIS_KS_PW);
		X509Certificate[] chain = new X509Certificate[1];
		chain[0] = BCStatics2.generateSelfSignedV3Certificate(kp, vecprax, vecca); 
		secretKeyStore.setKeyEntry("IK540840108", kp.getPrivate(),"196205".toCharArray(),chain );
		try{
		BCStatics2.saveStore(secretKeyStore,"196205" , "540840108_key");
		}catch(Exception ex){
			System.out.println("Fehler.....");
		}
		*/

		System.out.println("Private Key = "+kp.getPrivate());
		KeyStore secretKeyStore = BCStatics2.loadStore(Constants.KEYSTORE_DIR+File.separator+"540840108_key", Constants.PRAXIS_KS_PW);
		KeyStore keyStore = BCStatics2.loadStore(Constants.KEYSTORE_DIR+File.separator+"540840108", Constants.PRAXIS_KS_PW);
		KeyStore trustStore = BCStatics2.loadStore(Constants.KEYSTORE_DIR+File.separator+"540840108_key", Constants.PRAXIS_KS_PW);
		FileInputStream in = new FileInputStream(new File(Constants.KEYSTORE_DIR+File.separator+"54084010.p7c"));
		//System.out.println("Länge des InputStreams = "+in.available()); 

		
		//boolean geklappt = BCStatics2.installReply(keyStore, trustStore, "196205", "C=DE,O=Datenaustausch im Gesundheits- und Sozialwesen", in, true, true, kp.getPrivate());
		boolean geklappt = BCStatics3.installReply(keyStore, trustStore, "196205", "IK540840108", in, true, true, kp.getPrivate());
		System.out.println("Reply wurde korrekt eingelesen und die Kette aufgebaut -> "+geklappt);
		/**********************************/
		ByteArrayOutputStream bOut1 = new ByteArrayOutputStream();
		keyStore.store(bOut1, "196205".toCharArray());
		FileStatics.BytesToFile(bOut1.toByteArray(), new File("C:/Nebraska/540840108.p12"));
		bOut1.close();
		
		keyStore = BCStatics2.loadStore(Constants.KEYSTORE_DIR+File.separator+"540840108", Constants.PRAXIS_KS_PW);
		/**********Zertifikate[] aus annahmeKey holen****/
		X509Certificate[] chain = BCStatics3.holeZertifikate();
		if(chain==null){
			return;
		}
		/*
		Object[] annahmeRet =  BCStatics3.readAnnahme(true,null);
		int anzahl = (Integer)annahmeRet[0];
		System.out.println("Es befinden sich "+anzahl+" Zertifikate in der Datei");
		if(anzahl <= 0){
			return;
		}
		X509Certificate[] chain = new X509Certificate[anzahl];
		annahmeRet = BCStatics3.readAnnahme(false,chain);
		chain = (X509Certificate[])annahmeRet[1];
		*/
		/**********Zertifikate[] aus annahmeKey holen****/
		annahmeCerts = chain;
		//System.out.println(BCStatics3.getCertsByIssuer(chain));
		
		/**********************************/
		String alias = "";
		for(int i = 0; i < chain.length;i++){
			alias = BCStatics3.extrahiereAlias(((X509Certificate)chain[i]).getSubjectDN().toString());
			try{
				//chain[i].verify(kp.getPublic());
				keyStore.setCertificateEntry(alias, ((X509Certificate)chain[i]));
				//keyStore.setEntry(arg0, arg1, arg2)
				
			}catch(Exception ex){
				ex.printStackTrace();
				System.out.println("Alias - "+alias+" - ist bereits im KeyStore enthalten");
			}
		}

		BCStatics2.saveStore(trustStore,"196205" , "540840108_key");
		in.close();
		bOut1 = new ByteArrayOutputStream();
		keyStore.store(bOut1, "196205".toCharArray());
		FileStatics.BytesToFile(bOut1.toByteArray(), new File("C:/Nebraska/540840108.p12"));
		bOut1.close();
		
		keyStore = BCStatics2.loadStore(Constants.KEYSTORE_DIR+File.separator+"540840108", Constants.PRAXIS_KS_PW);
		System.out.println("Zertifikatskette von Alias IK540840108**************************");
		Certificate[] chainCerts = (Certificate[]) keyStore.getCertificateChain("IK540840108");
		//Certificate[] chainCerts = (Certificate[]) keyStore.getCertificateChain("C=DE,O=ITSG TrustCenter fuer sonstige Leistungserbringer");
		for(int i = 0; i < chainCerts.length;i++){
			System.out.println(((X509Certificate)chainCerts[i]).getSubjectDN()+" - "+
					((X509Certificate)chainCerts[i]).getIssuerDN()+" - "+((X509Certificate)chainCerts[i]).getBasicConstraints());	
		}
		
		
	}
	private void doCertAntrag() throws Exception{
		// Umbau zum test der MD5-Fingerprints
		//ASN1InputStream ain = new ASN1InputStream(new FileInputStream(Constants.KEYSTORE_DIR+File.separator+"40091472.p10"));
		/*
		ASN1InputStream ain = new ASN1InputStream(new FileInputStream(Constants.KEYSTORE_DIR+File.separator+"54084010.p10"));
		DERObject derob = ain.readObject();
		PKCS10CertificationRequest csr = new PKCS10CertificationRequest(
            	  derob.getDEREncoded() );
		System.out.println("Fingerprint von Certifikation-Request:"+BCStatics2.getMD5fromByte(derob.getDEREncoded()));
		PublicKey key = csr.getPublicKey("BC");

		System.out.println(key);
		System.out.println("Fingerprint vom Public-Key: "+BCStatics2.getMD5fromByte(key.getEncoded()));
		
		
		CertificationRequestInfo csrInfo = csr.getCertificationRequestInfo();
		System.out.println("SubjectDN: "+csrInfo.getSubject());
		System.out.println("SubjectDN: "+csrInfo.getSubjectPublicKeyInfo().getPublicKey());
		System.out.println("SubjectDN: "+BCStatics2.getMD5fromByte(csrInfo.getSubjectPublicKeyInfo().getPublicKeyData().getBytes()));
		*/
		String outFile = null;
		PdfReader reader = new PdfReader(Constants.KEYSTORE_DIR+File.separator+"vorlagen"+File.separator+"Zertifizierungsantrag.pdf");
		outFile = Constants.KEYSTORE_DIR+File.separator+"vorlagen"+File.separator+"Zertifizierungsantrag"+DatFunk.sHeute()+".pdf"; 
		FileOutputStream out = new FileOutputStream(outFile);
		PdfStamper stamper = new PdfStamper(reader, out);
		AcroFields form = stamper.getAcroFields();
		Map fieldMap = form.getFields();
        Set keys = fieldMap.keySet();
        for (Iterator it = keys.iterator(); it.hasNext();){
            String fieldName = (String) it.next();
            AcroFields.Item field = (AcroFields.Item) fieldMap.get(fieldName);
        	System.out.println(fieldName);
            if(fieldName.equals("IK")){
            	//fieldMap.put("IK", "540840108");
            	//System.out.println("Feld IK ersetzt");
            	form.setField(fieldName, "540840108");
            }
        }
        stamper.setFormFlattening(true);
        stamper.close();
        reader.close();
        //PDFDrucker.setup(outFile);
        
	}
	private void saveTestCase(){
		INIFile inif = new INIFile(Constants.INI_FILE);
		// In der Entwicklungsversion werden verschiedene Items nicht gespeichert z.b. CE oder PRAXIS_O
		inif.setStringProperty("Praxis", "PRAXIS_OU_ALIAS", tn1[0].getText().trim(),null);
		inif.setStringProperty("Praxis", "PRAXIS_OU_FIRMA", tn1[1].getText().trim(),null);
		inif.setStringProperty("Praxis", "PRAXIS_CN", tn1[2].getText().trim(),null);
		inif.setStringProperty("Praxis", "PRAXIS_KS_PW", tn1[3].getText().trim(),null);
		inif.setStringProperty("TestCA", "TEST_CA_OU_ALIAS", tn2[0].getText().trim(),null);
		inif.setStringProperty("TestCA", "TEST_CA_OU_FIRMA", tn2[1].getText().trim(),null);
		inif.setStringProperty("TestCA", "TEST_CA_O", tn2[3].getText().trim(),null);
		inif.setStringProperty("TestCA", "TEST_CA_CN", tn2[2].getText().trim(),null);
		inif.setStringProperty("TestCA", "TEST_CA_KS_PW", tn2[4].getText().trim(),null);
		inif.save();
		JOptionPane.showMessageDialog(null, "Die aktuellen Angaben zu Praxis und CA wurden gesichert in Datei:\n"+Constants.INI_FILE);
		
	}
	public void doVergleichen()throws Exception{
		BCStatics2.providerTest();
		KeyStore store = BCStatics2.loadStore(keystoreDir + File.separator +"540840108",praxisPassw);
		X509Certificate storedcert = (X509Certificate) store.getCertificate("IK540840108");
		X509Certificate cert = BCStatics2.readSingleCert(keystoreDir);
		System.out.println(storedcert);
		System.out.println(cert);
	}
	public void doSHA1Test() throws Exception{
		// Umfunktioniert zum Löschen
		BCStatics2.deleteCertFromStore(null, Constants.KEYSTORE_DIR+File.separator+"540840108", Constants.PRAXIS_KS_PW);
		/*
		PublicKey pairDb = null;
		KeyPair pairPem = null;
		PublicKey pairSt = null;
		
		X509Certificate cert = null;
		X509Certificate storedcert = null;
		
		cert = BCStatics2.readSingleCert(keystoreDir);
		pairDb = cert.getPublicKey();
		
		KeyStore store = BCStatics2.loadStore(keystoreDir + File.separator +"540840108",praxisPassw);
		storedcert = (X509Certificate) store.getCertificate("IK540840108");
		pairSt = storedcert.getPublicKey();
		
		pairPem = BCStatics2.getBothFromPem(keystoreDir + File.separator + "540840108");
		
		System.out.println("Public-Key aus .p7b-File    = "+pairDb);
		System.out.println("Public-Key im PEM-File      = "+pairPem.getPublic());
		System.out.println("Public-Key KeyStore-File    = "+pairSt);
		System.out.println("**************** SHA1 Fingerprints ******************");
		System.out.println("SHA1-.p7b-File      "+BCStatics2.getSHA1fromByte(pairDb.getEncoded()));
		System.out.println("SHA1-PEM-File       "+BCStatics2.getSHA1fromByte(pairPem.getPublic().getEncoded()));
		System.out.println("SHA1-Keystore-File  "+BCStatics2.getSHA1fromByte(pairSt.getEncoded()));
		System.out.println("**************** SHA1 Fingerprint Zertifikat im KeyStore ******************");
		System.out.println("SHA1-Zertifikat p7b File  "+BCStatics2.getSHA1(cert));
		System.out.println("SHA1-KeyStore-Zertifikat  "+BCStatics2.getSHA1(storedcert));
		*/
	}
	
	public void doAnnahmeReadAndStore() throws Exception{
		annahmeKeyFile = "";
		Object[] annahmeRet =  BCStatics3.readAnnahme(true,null);
		int anzahl = (Integer)annahmeRet[0];
		System.out.println("Es befinden sich "+anzahl+" Zertifikate in der Datei");
		if(anzahl <= 0){
			return;
		}
		X509Certificate[] chain = new X509Certificate[anzahl];
		annahmeRet = BCStatics3.readAnnahme(false,chain);
		chain = (X509Certificate[])annahmeRet[1];
		for(int i = 0; i < anzahl;i++){
			String alias = getAliasFromCert(chain[i]); 
			//BCStatics2.importCertIntoStore(chain[i], keystoreDir+File.separator, praxisPassw, vecprax.get(0).replace("IK", "")+"_key");	
		}
		
		
		CertificateFactory fact = CertificateFactory.getInstance("X.509","BC");
		CertPath certPath = fact.generateCertPath(Arrays.asList(chain));

		byte[] encoded = certPath.getEncoded("PEM");
		//System.out.println(new String(encoded));
		CertPath newCertPath = fact.generateCertPath(new ByteArrayInputStream(encoded), "PEM");
		byte[] newEncoded = newCertPath.getEncoded("PEM");
		List<X509Certificate> lcert = (List<X509Certificate>) newCertPath.getCertificates();

		for(int i = 0; i < anzahl;i++){
			//System.out.println("Zertifikat Nr. "+i+" = "+lcert.get(i).getBasicConstraints());
			//System.out.println(lcert.get(i));
		}

		//System.out.println(new String(newEncoded));
		if(newCertPath.equals(certPath)){
			System.out.println("Bestehender Cert-Pfad war bereits korrekt");
		}else{
			System.out.println("Cert-Pfad wurde generiert");
		}
	}
	public String getAliasFromCert(X509Certificate cert){
		String alias = cert.getSubjectDN().toString();
		return alias;
	}
	public void doCertCreate() throws Exception{
		KeyPair kpprax = null;
		String datei = keystoreDir + File.separator +vecprax.get(0).replace("IK", ""); 
		File f = new File(datei+".prv");
		if(f.exists()){
			KeyPair kp = BCStatics2.getBothFromPem(datei);
			kpprax = kp;
			System.out.println("SHA1 von PrivateKey (aus Pem-File) = "+BCStatics2.getSHA1fromByte(kpprax.getPrivate().getEncoded()));
			System.out.println("SHA1 von PublicKey (aus Pem-File) "+BCStatics2.getSHA1fromByte(kpprax.getPublic().getEncoded()));
		}else if(kpprax==null){
			kpprax = BCStatics2.generateRSAKeyPair();
		}
		X509Certificate[] chain = {BCStatics2.generateV3Certificate(kpprax,vecprax,vecca)};
		KeyStore store = BCStatics2.loadStore(datei, praxisPassw);
		
		store.setKeyEntry(vecprax.get(0), kpprax.getPrivate(),praxisPassw.toCharArray(), chain);
		//store.setKeyEntry("secret", kpprax.getPrivate(),praxisPassw.toCharArray(), chain);

		BCStatics2.saveStore(store, praxisPassw, vecprax.get(0).replace("IK", ""));
		//BCStatics2.certToFile(cert, cert.getEncoded(), keystoreDir+ File.separator +"test");
		

		//System.out.println(cert);
		//FileStatics.BytesToFile(cert.getEncoded(), new File(keystoreDir+ File.separator +"test.p7b"));
		
	}
	public void doEncode() throws Exception{
		BCStatics2.verschluesseln(vecprax.get(0),keystoreDir + File.separator +vecprax.get(0).replace("IK", ""),praxisPassw);
	}
	public void doReadAndManageReply() throws Exception{
		BCStatics2.readCertReply(keystoreDir,vecprax.get(0).replace("IK", ""),praxisPassw);
	}
	public void doRequestEinlesen()throws Exception{
		BCStatics2.providerTest();
		String request = FileStatics.fileChooser(keystoreDir,"Request (.p10) öffnen");
		if(request.trim().equals("")){return;}
		if(request.indexOf(".p10") < 0){return;}
		setVecs();
	}
	/******************************************************************************/
	
	public void doGenerateRequest() throws Exception{
		Nebraska.jf.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		Nebraska.hmZertifikat.clear();
		String datei = keystoreDir + File.separator +vecprax.get(0).replace("IK", ""); 
		File f = new File(datei+".prv");
		if(f.exists()){
			KeyPair kp = BCStatics2.getBothFromPem(datei);
			kpprax = kp;
		}else if(kpprax==null){
			kpprax = BCStatics2.generateRSAKeyPair();
		}
		PKCS10CertificationRequest request = BCStatics2.generateRequest(kpprax,vecprax,vecca);
		request.verify(kpprax.getPublic(),Constants.SECURITY_PROVIDER);
		f = new File(datei+".p10");
		FileOutputStream fos = new FileOutputStream(f);
		fos.write(request.getEncoded());
		fos.flush();
		fos.close();
		FileWriter file = new FileWriter(new File(datei+".pem"));
        PEMWriter pemWriter = new PEMWriter(file);
        pemWriter.writeObject(request);
        pemWriter.close();
        file.close();
        /************/
        CertificationRequestInfo info = request.getCertificationRequestInfo();
        System.out.println(info.getSubject());
        System.out.println(info.getDEREncoded());
        System.out.println(info.getDERObject());
        System.out.println(info.getVersion());
        System.out.println(info.getDERObject().toASN1Object());
        System.out.println(info.getAttributes());
        ASN1Set attrib = info.getAttributes();
        System.out.println("Attribute = "+attrib);
        ASN1Object asno =  (ASN1Object) info.getDERObject().toASN1Object();
        ASN1Sequence aseq = ASN1Sequence.getInstance(asno);
        System.out.println(aseq);
        System.out.println(aseq.size());
        SubjectPublicKeyInfo spub = null;
        for(int i = 0; i < aseq.size();i++){
        	System.out.println("Objec Nr."+i+" aus der ASN1-Struktur = "+aseq.getObjectAt(i));
        	if(aseq.getObjectAt(i) instanceof SubjectPublicKeyInfo){
        		spub = (SubjectPublicKeyInfo) aseq.getObjectAt(i);
        		System.out.println("Public Key des Requests = "+spub.getPublicKeyData());
        		System.out.println("SHA1-Hash aus dem PubKey des Requests = "+BCStatics2.getSHA1fromByte(spub.getPublicKeyData().getEncoded()));
        	}
        }
        System.out.println("Origianl Public Key des PEM-Files = "+kpprax.getPublic());
        System.out.println("SHA-1 Fingerprint des Zerifikates = "+BCStatics2.getSHA1fromByte(FileStatics.BytesFromFile(new File(keystoreDir + File.separator +vecprax.get(0).replace("IK", "")+".p7b"))));
        System.out.println("**********Request Start************\n");
        System.out.println(request);
        System.out.println("**********Request Ende*************\n");
        System.out.println("**********SHA-1 des PublicKey*************");
        System.out.println("SHA-1-Fingerprint PublicKey  ="+BCStatics2.getSHA1fromByte(kpprax.getPublic().getEncoded()));
        System.out.println("**********SHA-1 des Requests*************");
        System.out.println("SHA-1-Fingerprint CertRequest="+BCStatics2.getSHA1fromByte(request.getEncoded()));
        /*********Test eines neuen Schl�ssels******///
        //BCStatics.machePublicKey(kpprax.getPublic(), cert1);
        
        
        Nebraska.hmZertifikat.put("<Ikpraxis>",vecprax.get(0));
        Nebraska.hmZertifikat.put("<Issuerc>","C=DE");
        Nebraska.hmZertifikat.put("<Issuero>","O="+vecca.get(3));
        Nebraska.hmZertifikat.put("<Subjectc>","C=DE");
        Nebraska.hmZertifikat.put("<Subjecto>","O="+vecca.get(3));
        Nebraska.hmZertifikat.put("<Subjectou1>","OU="+vecprax.get(1));
        Nebraska.hmZertifikat.put("<Subjectou2>","OU="+vecprax.get(0));
        Nebraska.hmZertifikat.put("<Subjectcn>","CN="+vecprax.get(2));
        Nebraska.hmZertifikat.put("<Algorithm>",kpprax.getPublic().getAlgorithm());
        
        String sha1 = BCStatics2.getSHA1fromByte(spub.getPublicKeyData().getBytes());
        //String sha1 = BCStatics2.getSHA1fromByte(kpprax.getPublic().getEncoded());
        Nebraska.hmZertifikat.put("<Sha1publickey>",BCStatics2.macheHexDump(sha1, 20," "));
        
        String md5 = BCStatics2.getMD5fromByte(spub.getPublicKeyData().getBytes());
        //String md5 = BCStatics2.getMD5fromByte(kpprax.getPublic().getEncoded());
        Nebraska.hmZertifikat.put("<Md5publickey>",BCStatics2.macheHexDump(md5, 20," "));
        
        sha1 = BCStatics2.getSHA1fromByte(request.getEncoded());
        Nebraska.hmZertifikat.put("<Sha1certificate>",BCStatics2.macheHexDump(sha1, 20," "));

        md5 = BCStatics2.getMD5fromByte(request.getEncoded());
        Nebraska.hmZertifikat.put("<Md5certificate>",BCStatics2.macheHexDump(md5, 20," "));

        
        java.security.interfaces.RSAPublicKey pub =
			(java.security.interfaces.RSAPublicKey)kpprax.getPublic();
        String hexstring = new BigInteger(pub.getModulus().toByteArray()).toString(16);
        System.out.println("Hexstring = "+hexstring);
        String modulus = BCStatics2.macheHexDump(hexstring, 20," ");
        Nebraska.hmZertifikat.put("<Modulus>",modulus);
        
        hexstring = new BigInteger(pub.getPublicExponent().toByteArray()).toString(16);
        Nebraska.hmZertifikat.put("<Exponent>",(hexstring.length()==5 ? "0"+hexstring : hexstring  ));
		OOorgTools.starteStandardFormular(keystoreDir + File.separator +"vorlagen"+File.separator+"ZertBegleitzettel.ott", null);
		Nebraska.jf.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}            

	
	/**
	 * @throws Exception ************************/
	private boolean doKeystore(String passw,boolean isRoot) throws Exception{
		String datei = "";
		String keystore = "";
		if(isRoot){
			String testdatei = keystoreDir+ File.separator +vecca.get(0).replace("IK", ""); 
			File f = new File(testdatei+".prv");
			if(f.exists()){
				KeyPair kp = BCStatics2.getBothFromPem(testdatei);
				kpca = kp;
			}else if(kpca==null){
					kpca = BCStatics2.generateRSAKeyPair();
			}
			datei =tn2[0].getText().trim().replace("IK", "");
			keystore =  keystoreDir+ File.separator +datei+".p12";
			if(datei.trim().equals("")){
				Nebraska.jf.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				JOptionPane.showMessageDialog(null,"Kein Alias angegeben");
				return false;
			}
		}else{
			String testdatei = keystoreDir + File.separator +vecprax.get(0).replace("IK", ""); 
			File f = new File(testdatei+".prv");
			if(f.exists()){
				KeyPair kp = BCStatics2.getBothFromPem(testdatei);
				kpprax = kp;
			}else if(kpprax==null){
				kpprax = BCStatics2.generateRSAKeyPair();
			}
			datei =tn1[0].getText().trim().replace("IK", "");
			keystore =  keystoreDir + File.separator +tn1[0].getText().trim().replace("IK", "")+".p12";
			if(tn1[0].getText().trim().equals("")){
				Nebraska.jf.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				JOptionPane.showMessageDialog(null,"Kein Alias angegeben");
				return false;
			}
		}
		if(passw.equals("")){
			Nebraska.jf.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			JOptionPane.showMessageDialog(null,"Kein Passwort angegeben");
			return false;
		}
		File f = new File(keystoreDir);
		if(!f.isDirectory()){f.mkdir();}
		f = new File(keystore);
		if(!f.exists()){
			System.out.println("Passwort f�r Keystore = "+passw);
			BCStatics2.createKeyStore(datei,passw,isRoot,vecprax,vecca,(isRoot ? kpca : kpprax));return true;}
		else{
			Nebraska.jf.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			Message("KeystoreFile "+keystore+" existiert bereits");
			return false;
		}
	}

	private void Message(String msg){
		JOptionPane.showMessageDialog(null, msg);
	}
	
	private void setVecs(){
		vecprax.clear();vecca.clear();
		for(int i = 0;i<5;i++){
			vecca.add(tn2[i].getText());
			if(i<4){
				vecprax.add(tn1[i].getText());
			}
		}
		praxisPassw = vecprax.get(3);
		caPassw = vecca.get(4);
	}

	class MyCertTableModel extends DefaultTableModel{
		   /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Class getColumnClass(int columnIndex) {
			   if(columnIndex==1 ){
				   return ImageIcon.class;
			   }else if(columnIndex==4){
				   return Boolean.class;
			   }
			   else{
				   return String.class;
			   }
		}

		public boolean isCellEditable(int row, int col) {
		          return false;
        }
	}

}
