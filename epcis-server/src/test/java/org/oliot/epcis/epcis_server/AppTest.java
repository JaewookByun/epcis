package org.oliot.epcis.epcis_server;

import java.util.regex.Pattern;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {
	/**
	 * Rigorous Test :-)
	 */
	@Test
	public void shouldAnswerWithTrue() {
		System.out.println(
				Pattern.matches("urn:epc:id:sgtin:0614141[.](.)*[.]2020", "urn:epc:id:sgtin:0614141.107346.2020"));
	}
}
