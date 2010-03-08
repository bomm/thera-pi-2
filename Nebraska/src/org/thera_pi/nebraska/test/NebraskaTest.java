package org.thera_pi.nebraska.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.thera_pi.nebraska.Nebraska;
import org.thera_pi.nebraska.NebraskaCryptoException;
import org.thera_pi.nebraska.NebraskaFileException;

public class NebraskaTest extends TestCase {
	private Nebraska nebraska;
	private final String keystoreFilename = "/tmp/keystore.p12";
	private final String requestFilename = "/tmp/request.p10";
	
	protected void setUp() throws Exception {
		super.setUp();
		File keystoreFile = new File(keystoreFilename);
		if(keystoreFile.exists()) {
			keystoreFile.delete();
		}
		nebraska = new Nebraska(keystoreFilename, "123456", "abcdef", "IK123456789", "Test Institution", "Max Mustermann");
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

	public void testGenerateKeyPair() throws NebraskaCryptoException, NebraskaFileException {
		assertFalse(nebraska.hasPrivateKey());
		nebraska.generateKeyPair("abcdef", true);
		assertTrue(nebraska.hasPrivateKey());
	}

	public void testCreateCertificateRequest() throws NebraskaCryptoException, NebraskaFileException, IOException {
		StringBuffer md5Hash = new StringBuffer();
		assertFalse(nebraska.hasPrivateKey());
		nebraska.generateKeyPair("abcdef", true);
		assertTrue(nebraska.hasPrivateKey());

		File requestFile = new File(requestFilename);
		if(requestFile.exists()) {
			requestFile.delete();
		}
		FileOutputStream requestStream = new FileOutputStream(requestFile);
		nebraska.createCertificateRequest(requestStream, md5Hash);
		requestStream.close();
		
		assertTrue(requestFile.exists());
//		assertEquals("", md5Hash.toString());
	}

	public void testGetPublicKeyMD5() throws NebraskaCryptoException, NebraskaFileException {
		assertFalse(nebraska.hasPrivateKey());
		nebraska.generateKeyPair("abcdef", true);
		assertTrue(nebraska.hasPrivateKey());

		String fingerprint = nebraska.getPublicKeyMD5();
		assertNotNull(fingerprint);
		assertEquals(fingerprint, nebraska.getPublicKeyMD5());
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
