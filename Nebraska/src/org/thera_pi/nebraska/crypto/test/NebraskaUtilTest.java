package org.thera_pi.nebraska.crypto.test;

import junit.framework.TestCase;

import org.thera_pi.nebraska.crypto.NebraskaUtil;

public class NebraskaUtilTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetSubjectDN() {
		assertEquals("C=DE,O=ITSG TrustCenter fuer sonstige Leistungserbringer,OU=Test Institution,OU=IK123456789,CN=Max Mustermann",
				NebraskaUtil.getSubjectDN("123456789", "Test Institution", "Max Mustermann"));
		assertEquals("C=DE,O=ITSG TrustCenter fuer sonstige Leistungserbringer,OU=Logopaedie Semmelweiss,OU=IK000123456,CN=Waeaenae Broesel",
				NebraskaUtil.getSubjectDN("IK 000123456", "Łógòpädie Sèmméłweiß", "Wäänä Brösel"));
	}

	public void testNormalizeDnField() {
		assertEquals("Meissen", NebraskaUtil.normalizeDnField("Meißen"));
		assertEquals("alpha beta gamma", NebraskaUtil.normalizeDnField("alpha,beta,gamma"));
		assertEquals("aens zwo drei", NebraskaUtil.normalizeDnField("æns, zwø, dreí"));
	}

	public void testNormalizeIK() {
		assertEquals("123456789", NebraskaUtil.normalizeIK("123456789"));
		assertEquals("100200300", NebraskaUtil.normalizeIK("IK100200300"));
		assertEquals("111222333", NebraskaUtil.normalizeIK("IK 111222333"));
	}

}
