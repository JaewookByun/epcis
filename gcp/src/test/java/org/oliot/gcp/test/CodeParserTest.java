package org.oliot.gcp.test;

import java.util.HashMap;

import org.oliot.gcp.core.CodeParser;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class CodeParserTest extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public CodeParserTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(CodeParserTest.class);
	}

	public void testSgtin() {
		CodeParser codeParser = new CodeParser();
		String sgtin = "(01)80614141123458(21)6789";
		int gcpLength = 7;
		HashMap<String, String> collection = codeParser.parse(sgtin, gcpLength);
		System.out.println("[TEST]" + sgtin + " == " + collection.get("sgtin"));
		assertTrue(collection.get("sgtin").equals("urn:epc:id:sgtin:0614141.812345.6789"));
		assertTrue(collection.get("gtin").equals("urn:epc:idpat:sgtin:0614141.812345.*"));
	}

	public void testLgtin() {
		CodeParser codeParser = new CodeParser();
		String lgtin = "(01)80614141123458(10)6789";
		int gcpLength = 7;
		HashMap<String, String> collection = codeParser.parse(lgtin, gcpLength);
		System.out.println("[TEST]" + lgtin + " == " + collection.get("lgtin"));
		assertTrue(collection.get("lgtin").equals("urn:epc:class:lgtin:0614141.812345.6789"));
	}

	public void testSgtinAndLgtin() {
		CodeParser codeParser = new CodeParser();
		String slgtin = "(01)80614141123458(21)6789(10)4222";
		int gcpLength = 7;
		HashMap<String, String> collection = codeParser.parse(slgtin, gcpLength);
		System.out.println("[TEST]" + slgtin + " includes " + collection.get("sgtin"));
		assertTrue(collection.get("sgtin").equals("urn:epc:id:sgtin:0614141.812345.6789"));
		System.out.println("[TEST]" + slgtin + " includes " + collection.get("lgtin"));
		assertTrue(collection.get("lgtin").equals("urn:epc:class:lgtin:0614141.812345.4222"));
	}

	public void testSgln() {
		CodeParser codeParser = new CodeParser();
		String sgln = "(414)0614141123452(254)333";
		int gcpLength = 7;
		HashMap<String, String> collection = codeParser.parse(sgln, gcpLength);
		System.out.println("[TEST]" + sgln + " == " + collection.get("sgln"));
		assertTrue(collection.get("sgln").equals("urn:epc:id:sgln:0614141.12345.333"));
	}

	public void testSgtinAndLgtinAndSgln() {
		CodeParser codeParser = new CodeParser();
		String slgtin = "(01)80614141123458(21)6789(10)4222(414)0614141123452(254)333";
		int gcpLength = 7;
		HashMap<String, String> collection = codeParser.parse(slgtin, gcpLength);
		System.out.println("[TEST]" + slgtin + " includes " + collection.get("sgtin"));
		assertTrue(collection.get("sgtin").equals("urn:epc:id:sgtin:0614141.812345.6789"));
		System.out.println("[TEST]" + slgtin + " includes " + collection.get("lgtin"));
		assertTrue(collection.get("lgtin").equals("urn:epc:class:lgtin:0614141.812345.4222"));
		System.out.println("[TEST]" + slgtin + " includes " + collection.get("sgln"));
		assertTrue(collection.get("sgln").equals("urn:epc:id:sgln:0614141.12345.333"));
	}

	public void testExtraCode() {
		CodeParser codeParser = new CodeParser();
		String extra = "(11)151201(13)151203(30)1(3104)000600(3930)41028000";
		// Don't care GCP, insert any positive integer
		HashMap<String, String> collection = codeParser.parse(extra, 1);
		// { , urn:epc:id:ai:310n=0.06, , urn:epc:id:ai:393n:410=28000.0}
		System.out.println(
				"[TEST]" + extra + " includes " + collection.get("urn:epc:id:ai:11") + " means Production Date");
		assertTrue(collection.get("urn:epc:id:ai:11").equals("151201"));
		System.out.println(
				"[TEST]" + extra + " includes " + collection.get("urn:epc:id:ai:13") + " means Packaging Date");
		assertTrue(collection.get("urn:epc:id:ai:13").equals("151203"));
		System.out
				.println("[TEST]" + extra + " includes " + collection.get("urn:epc:id:ai:30") + " means Count of Item");
		assertTrue(collection.get("urn:epc:id:ai:30").equals("1"));
		System.out.println(
				"[TEST]" + extra + " includes " + collection.get("urn:epc:id:ai:310n") + " means Kilogram Weight");
		assertTrue(collection.get("urn:epc:id:ai:310n").equals("0.06"));
		System.out.println("[TEST]" + extra + " includes " + collection.get("urn:epc:id:ai:393n")
				+ " means Price as 410 iso currency code");
		assertTrue(collection.get("urn:epc:id:ai:393n").equals("410|28000.0"));

		System.out.println();
	}

	public void testSscc() {
		CodeParser codeParser = new CodeParser();
		String sscc = "(00)1 0614141 234567890 8";
		int gcpLength = 7;
		HashMap<String, String> collection = codeParser.parse(sscc, gcpLength);
		System.out.println("[TEST]" + sscc + " == " + collection.get("sscc"));
		assertTrue(collection.get("sscc").equals("urn:epc:id:sscc:0614141.1234567890"));
	}

	public void testGsrn() {
		CodeParser codeParser = new CodeParser();
		String gsrn = "(8018) 0614141 1234567890 2";
		int gcpLength = 7;
		HashMap<String, String> collection = codeParser.parse(gsrn, gcpLength);
		System.out.println("[TEST]" + gsrn + " == " + collection.get("gsrn"));
		assertTrue(collection.get("gsrn").equals("urn:epc:id:gsrn:0614141.1234567890"));
	}

	public void testGsrnp() {
		CodeParser codeParser = new CodeParser();
		String gsrnp = "(8017) 0614141 1234567890 2";
		int gcpLength = 7;
		HashMap<String, String> collection = codeParser.parse(gsrnp, gcpLength);
		System.out.println("[TEST]" + gsrnp + " == " + collection.get("gsrnp"));
		assertTrue(collection.get("gsrnp").equals("urn:epc:id:gsrnp:0614141.1234567890"));
	}

	public void testGrai() {
		CodeParser codeParser = new CodeParser();
		String grai = "(8003) 0 0614141 12345 2 32a/b";
		int gcpLength = 7;
		HashMap<String, String> collection = codeParser.parse(grai, gcpLength);
		System.out.println("[TEST]" + grai + " == " + collection.get("grai"));
		assertTrue(collection.get("grai").equals("urn:epc:id:grai:0614141.12345.32a%2Fb"));
	}

	public void testGiai() {
		// Typo is identified in TDT GIAI XML
		CodeParser codeParser = new CodeParser();
		String giai = "(8004) 0614141 32a/b";
		int gcpLength = 7;
		HashMap<String, String> collection = codeParser.parse(giai, gcpLength);
		System.out.println("[TEST]" + giai + " == " + collection.get("giai"));
		assertTrue(collection.get("giai").equals("urn:epc:id:giai:0614141.32a%2Fb"));
	}

	public void testGdti() {
		CodeParser codeParser = new CodeParser();
		String gdti = "(253) 0614141 12345 2 006847";
		int gcpLength = 7;
		HashMap<String, String> collection = codeParser.parse(gdti, gcpLength);
		System.out.println("[TEST]" + gdti + " == " + collection.get("gdti"));
		assertTrue(collection.get("gdti").equals("urn:epc:id:gdti:0614141.12345.006847"));
	}

	public void testCpi() {
		CodeParser codeParser = new CodeParser();
		String cpi = "(8010) 0614141 5PQ7/Z43 (8011) 12345";
		int gcpLength = 7;
		HashMap<String, String> collection = codeParser.parse(cpi, gcpLength);
		System.out.println("[TEST]" + cpi + " == " + collection.get("cpi"));
		assertTrue(collection.get("cpi").equals("urn:epc:id:cpi:0614141.5PQ7%2FZ43.12345"));
	}

	public void testSgcn() {
		CodeParser codeParser = new CodeParser();
		String sgcn = "(255) 4012345 67890 1 0";
		int gcpLength = 7;
		HashMap<String, String> collection = codeParser.parse(sgcn, gcpLength);
		System.out.println("[TEST]" + sgcn + " == " + collection.get("sgcn"));
		assertTrue(collection.get("sgcn").equals("urn:epc:id:sgcn:4012345.67890.0"));
	}
}
