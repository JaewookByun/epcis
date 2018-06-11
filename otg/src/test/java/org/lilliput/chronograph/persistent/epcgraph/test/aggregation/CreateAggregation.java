package org.lilliput.chronograph.persistent.epcgraph.test.aggregation;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.oliot.epcis.service.capture.EventCapture;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class CreateAggregation {
	
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

		long lastTimeMil = System.currentTimeMillis()+100000000;
		
		String body = "";
		
		for (int i = 0; i < count; i++) {
			
			Thread.sleep(1000);
			 
			cTime = sdf.format(new Date());
			String epc = String.format("%010d", i);
			
			// urn:epc:id:sscc:0000002.0000000001
			
			body += "<AggregationEvent>\n" + 
					"				<eventTime>"+ cTime +"</eventTime>\n" + 
					"				<eventTimeZoneOffset>+00:00</eventTimeZoneOffset>\n" + 
					"				<parentID>urn:epc:id:sscc:0000001."+epc+"</parentID>\n" + 
					"				<childEPCs>\n" + 
					"					<epc>urn:epc:id:sgtin:0000001.000001.0</epc>\n" + 
					"				</childEPCs>\n" + 
					"				<action>ADD</action>\n" + 
					"				<bizStep>urn:epcglobal:cbv:bizstep:loading</bizStep>\n" + 
					"				<!-- TNT Liverpool depot -->\n" + 
					"				<bizLocation>\n" + 
					"					<id>urn:epc:id:sgln:0000001.00002.1</id>\n" + 
					"				</bizLocation>\n" + 
					"			</AggregationEvent>";
			
			String lastTime = sdf.format(new Date(lastTimeMil-i));
			
			body += "<AggregationEvent>\n" + 
					"				<eventTime>"+ lastTime +"</eventTime>\n" + 
					"				<eventTimeZoneOffset>+00:00</eventTimeZoneOffset>\n" + 
					"				<parentID>urn:epc:id:sscc:0000001."+epc+"</parentID>\n" + 
					"				<childEPCs>\n" + 
					"					<epc>urn:epc:id:sgtin:0000001.000001.0</epc>\n" + 
					"				</childEPCs>\n" + 
					"				<action>DELETE</action>\n" + 
					"				<bizStep>urn:epcglobal:cbv:bizstep:loading</bizStep>\n" + 
					"				<!-- TNT Liverpool depot -->\n" + 
					"				<bizLocation>\n" + 
					"					<id>urn:epc:id:sgln:0000001.00002.1</id>\n" + 
					"				</bizLocation>\n" + 
					"			</AggregationEvent>";
			
		}		

		EventCapture cap = new EventCapture();
		cap.capture(top + body + bottom);
	}
}
