package org.lilliput.chronograph.persistent.epcgraph.test;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;
import org.oliot.epcis.service.capture.EventCapture;

public class CreateTransformationTestDoc {

	@Test
	public void test() {
		String top = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + "<!DOCTYPE project>\n"
				+ "<epcis:EPCISDocument schemaVersion=\"1.2\"\n"
				+ "	creationDate=\"2013-06-04T14:59:02.099+02:00\" xmlns:epcis=\"urn:epcglobal:epcis:xsd:1\"\n"
				+ "	xmlns:example=\"http://ns.example.com/epcis\">\n" + "	<EPCISBody>\n" + "		<EventList>";

		String bottom = "</EventList>\n" + "	</EPCISBody>\n" + "</epcis:EPCISDocument>";

		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		String cTime = sdf.format(new Date());
		
		String body = "";
		for (int i = 0; i < 3; i++) {
			body += "<extension>\n" + "				<TransformationEvent>\n"
					+ "					<eventTime>" +cTime + "</eventTime>\n"
					+ "					<eventTimeZoneOffset>+00:00</eventTimeZoneOffset>\n"
					+ "					<inputEPCList>\n"
					+ "						<epc>urn:epc:id:sgtin:0000001.000001." + i + "</epc>\n"
					+ "					</inputEPCList>\n" + "					<outputEPCList>\n"
					+ "						<epc>urn:epc:id:sgtin:0000001.000001." + (i + 1) + "</epc>\n"
					+ "					</outputEPCList>\n" + "				</TransformationEvent>\n"
					+ "			</extension>";
		}

		EventCapture cap = new EventCapture();
		cap.capture(top + body + bottom);
	}
}
