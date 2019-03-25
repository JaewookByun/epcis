package org.lilliput.chronograph.persistent.epcgraph.test.location;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.oliot.epcis.service.capture.EventCapture;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class CreateLocation {
	
	public void test() throws InterruptedException {
		

		MongoClient client = new MongoClient();
		MongoDatabase db = client.getDatabase("epcis");
		db.getCollection("EventData").drop();
		db.getCollection("edges").drop();
		db.getCollection("vertices").drop();
		db.getCollection("edges").createIndex(new BsonDocument("_outV", new BsonInt32(1)).append("_t", new BsonInt32(1))
				.append("_inV", new BsonInt32(1)));

		client.close();
		
		int count = 3;
		
		
		String top = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + "<!DOCTYPE project>\n"
				+ "<epcis:EPCISDocument schemaVersion=\"1.2\"\n"
				+ "	creationDate=\"2013-06-04T14:59:02.099+02:00\" xmlns:epcis=\"urn:epcglobal:epcis:xsd:1\"\n"
				+ "	xmlns:example=\"http://ns.example.com/epcis\">\n" + "	<EPCISBody>\n" + "		<EventList>";

		String bottom = "</EventList>\n" + "	</EPCISBody>\n" + "</epcis:EPCISDocument>";

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		String cTime = sdf.format(new Date());

		String body = "";
		
		for (int i = 0; i < count; i++) {
			
			
			Thread.sleep(1000);
			
			cTime = sdf.format(new Date());
			
			body += "<ObjectEvent>\n" + 
					"	<eventTime>" + cTime + "</eventTime>\n" + 
					"	<eventTimeZoneOffset>+00:00</eventTimeZoneOffset>\n" + 
					"	<epcList>\n" + 
					"		<epc>urn:epc:id:sgtin:0000001.000001.0</epc>\n" +
					"	</epcList>\n" + 
					"	<action>OBSERVE</action>\n" + 
					"	<bizStep>urn:epcglobal:cbv:bizstep:receiving</bizStep>\n" + 
					"	<bizLocation>\n" + 
					"		<id>urn:epc:id:sgln:0000001.00001."+i+"</id>\n" + 
					"	</bizLocation>\n" + 
					"</ObjectEvent>";
			
		}		

		EventCapture cap = new EventCapture();
		cap.capture(top + body + bottom);
	}
}
