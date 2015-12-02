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

	public void testSgtin(){
		CodeParser codeParser = new CodeParser();
		String sgtin = "(01)80614141123458(21)6789";
		int gcpLength = 7;
		HashMap<String,String> collection = codeParser.parse(sgtin, gcpLength);
		System.out.println("[TEST]" + sgtin + " == " + collection.get("sgtin") );
		assertTrue( collection.get("sgtin").equals("urn:epc:id:sgtin:0614141.812345.6789"));
	}
	
	public void testLgtin(){
		CodeParser codeParser = new CodeParser();
		String lgtin = "(01)80614141123458(10)6789";
		int gcpLength = 7;
		HashMap<String,String> collection = codeParser.parse(lgtin, gcpLength);
		System.out.println("[TEST]" + lgtin + " == " + collection.get("lgtin") );
		assertTrue( collection.get("lgtin").equals("urn:epc:class:lgtin:0614141.812345.6789"));
	}
	
	public void testSgtinAndLgtin(){
		CodeParser codeParser = new CodeParser();
		String slgtin = "(01)80614141123458(21)6789(10)4222";
		int gcpLength = 7;
		HashMap<String,String> collection = codeParser.parse(slgtin, gcpLength);
		System.out.println("[TEST]" + slgtin + " includes " + collection.get("sgtin") );
		assertTrue( collection.get("sgtin").equals("urn:epc:id:sgtin:0614141.812345.6789"));
		System.out.println("[TEST]" + slgtin + " includes " + collection.get("lgtin") );
		assertTrue( collection.get("lgtin").equals("urn:epc:class:lgtin:0614141.812345.4222"));
	}
	
	public void testSgln(){
		CodeParser codeParser = new CodeParser();
		String sgln = "(414)0614141123452(254)333";
		int gcpLength = 7;
		HashMap<String,String> collection = codeParser.parse(sgln, gcpLength);
		System.out.println("[TEST]" + sgln + " == " + collection.get("sgln") );
		assertTrue( collection.get("sgln").equals("urn:epc:id:sgln:0614141.12345.333"));
	}
	
	public void testSgtinAndLgtinAndSgln(){
		CodeParser codeParser = new CodeParser();
		String slgtin = "(01)80614141123458(21)6789(10)4222(414)0614141123452(254)333";
		int gcpLength = 7;
		HashMap<String,String> collection = codeParser.parse(slgtin, gcpLength);
		System.out.println("[TEST]" + slgtin + " includes " + collection.get("sgtin") );
		assertTrue( collection.get("sgtin").equals("urn:epc:id:sgtin:0614141.812345.6789"));
		System.out.println("[TEST]" + slgtin + " includes " + collection.get("lgtin") );
		assertTrue( collection.get("lgtin").equals("urn:epc:class:lgtin:0614141.812345.4222"));
		System.out.println("[TEST]" + slgtin + " includes " + collection.get("sgln") );
		assertTrue( collection.get("sgln").equals("urn:epc:id:sgln:0614141.12345.333"));
	}
	
	public void testExtraCode(){
		CodeParser codeParser = new CodeParser();
		String extra = "(11)151201(13)151203(30)1(3104)000600(3930)41028000";
		// Don't care GCP, insert any positive integer
		HashMap<String,String> collection = codeParser.parse(extra, 1);
		//{ , urn:id:epc:ai:310n=0.06, , urn:id:epc:ai:393n:410=28000.0}
		System.out.println("[TEST]" + extra + " includes " + collection.get("urn:id:epc:ai:11") + " means Production Date" );
		assertTrue( collection.get("urn:id:epc:ai:11").equals("151201"));
		System.out.println("[TEST]" + extra + " includes " + collection.get("urn:id:epc:ai:13") + " means Packaging Date" );
		assertTrue( collection.get("urn:id:epc:ai:13").equals("151203"));
		System.out.println("[TEST]" + extra + " includes " + collection.get("urn:id:epc:ai:30") + " means Count of Item" );
		assertTrue( collection.get("urn:id:epc:ai:30").equals("1"));
		System.out.println("[TEST]" + extra + " includes " + collection.get("urn:id:epc:ai:310n") + " means Kilogram Weight" );
		assertTrue( collection.get("urn:id:epc:ai:310n").equals("0.06"));
		System.out.println("[TEST]" + extra + " includes " + collection.get("urn:id:epc:ai:393n") + " means Price as 410 iso currency code" );
		assertTrue( collection.get("urn:id:epc:ai:393n").equals("410|28000.0"));
		
		System.out.println();
	}
}
