package org.thera_pi.nebraska.crypto.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.thera_pi.nebraska.crypto.NebraskaKeystore;
import org.thera_pi.nebraska.crypto.NebraskaCryptoException;
import org.thera_pi.nebraska.crypto.NebraskaFileException;
import org.thera_pi.nebraska.crypto.NebraskaNotInitializedException;

public class NebraskaKeystoreTest extends TestCase {
	private NebraskaKeystore nebraskaKeystore;
	private final String keystoreFilename = "/tmp/keystore.p12";
	private final String requestFilename = "/tmp/request.p10";
	
	protected void setUp() throws Exception {
		super.setUp();
		File keystoreFile = new File(keystoreFilename);
		if(keystoreFile.exists()) {
			keystoreFile.delete();
		}
		nebraskaKeystore = new NebraskaKeystore(keystoreFilename, "123456", "abcdef", "IK123456789", "Test Institution", "Max Mustermann");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testHasPrivateKey() {
		fail("Not yet implemented");
	}

	public void testHasCertificate() {
		fail("Not yet implemented");
	}

	public void testGenerateKeyPair() throws NebraskaCryptoException, NebraskaFileException, NebraskaNotInitializedException {
		assertFalse(nebraskaKeystore.hasPrivateKey());
		nebraskaKeystore.generateKeyPair(true);
		assertTrue(nebraskaKeystore.hasPrivateKey());
	}

	public void testCreateCertificateRequest() throws NebraskaCryptoException, NebraskaFileException, IOException, NebraskaNotInitializedException {
		StringBuffer md5Hash = new StringBuffer();
		assertFalse(nebraskaKeystore.hasPrivateKey());
		nebraskaKeystore.generateKeyPair(true);
		assertTrue(nebraskaKeystore.hasPrivateKey());

		File requestFile = new File(requestFilename);
		if(requestFile.exists()) {
			requestFile.delete();
		}
		FileOutputStream requestStream = new FileOutputStream(requestFile);
		nebraskaKeystore.createCertificateRequest(requestStream, md5Hash);
		requestStream.close();
		
		assertTrue(requestFile.exists());
//		assertEquals("", md5Hash.toString());
	}

	public void testGetPublicKeyMD5() throws NebraskaCryptoException, NebraskaFileException, NebraskaNotInitializedException {
		assertFalse(nebraskaKeystore.hasPrivateKey());
		nebraskaKeystore.generateKeyPair(true);
		assertTrue(nebraskaKeystore.hasPrivateKey());

		String fingerprint = nebraskaKeystore.getPublicKeyMD5();
		assertNotNull(fingerprint);
		assertEquals(fingerprint, nebraskaKeystore.getPublicKeyMD5());
		assertTrue(fingerprint.length() == 47);
		// FIXME find a way to really check the fingerprint 
	}

	public void testImportCertificateReply() {
		fail("Not yet implemented");
	}

	public void testImportReceiverCertificates() {
		fail("Not yet implemented");
	}

	public void testDeleteExpiredCertificates() {
		fail("Not yet implemented");
	}

	public void testExportKeyAndCertificate() {
		fail("Not yet implemented");
	}

	public void testImportKeyAndCertificate() {
		fail("Not yet implemented");
	}

	public void testGetSubjectDN() {
		fail("Not yet implemented");
	}

}
