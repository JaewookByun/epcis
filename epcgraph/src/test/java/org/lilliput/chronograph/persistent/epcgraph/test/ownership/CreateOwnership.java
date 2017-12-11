package org.lilliput.chronograph.persistent.epcgraph.test.ownership;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;
import org.oliot.epcis.service.capture.EventCapture;

public class CreateOwnership {
	
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
			cTime = sdf.format(new Date());
			
			body += "<ObjectEvent>\n" + 
					"	<eventTime>" + cTime + "</eventTime>\n" + 
					"	<eventTimeZoneOffset>+00:00</eventTimeZoneOffset>\n" + 
					"	<epcList>\n" + 
					"		<epc>urn:epc:id:sgtin:0000001.000001.1</epc>\n" +
					"	</epcList>\n" + 
					"	<action>OBSERVE</action>\n" + 
					"	<bizStep>urn:epcglobal:cbv:bizstep:receiving</bizStep>\n" + 
					"	<bizLocation>\n" + 
					"		<id>urn:epc:id:sgln:0000001.00002.1</id>\n" + 
					"	</bizLocation>\n" + 
					"	<extension>\n" + 
					"		<sourceList>\n" + 
					"			<source type=\"urn:epcglobal:cbv:sdt:possessing_party\">urn:epc:id:sgln:0000001.00001." + i + "</source>\n" + 
					"		</sourceList>\n" + 
					"		<destinationList>\n" + 
					"			<destination type=\"urn:epcglobal:cbv:sdt:possessing_party\">urn:epc:id:sgln:0000001.00002." + (i+1) + "</destination>\n" + 
					"		</destinationList>\n" + 
					"	</extension>\n" + 
					"</ObjectEvent>";
		}

		EventCapture cap = new EventCapture();
		cap.capture(top + body + bottom);
	}
}
