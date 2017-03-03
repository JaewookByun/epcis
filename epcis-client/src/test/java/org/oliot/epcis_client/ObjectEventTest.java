package org.oliot.epcis_client;

import org.bson.BsonBoolean;
import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C) 2014-16 Jaewook Byun
 *
 * This project is part of Oliot (oliot.org), pursuing the implementation of
 * Electronic Product Code Information Service(EPCIS) v1.1 specification in
 * EPCglobal.
 * [http://www.gs1.org/gsmp/kc/epcglobal/epcis/epcis_1_1-standard-20140520.pdf]
 * 
 *
 * @author Jaewook Jack Byun, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         bjw0829@kaist.ac.kr, bjw0829@gmail.com
 */
public class ObjectEventTest {

	public void baseObjectEventCapture() {
		try {
			// Make basic Object Event
			ObjectEvent objectEvent = new ObjectEvent();
			EPCISClient client = new EPCISClient(new URL("http://localhost:8080/epcis/Service/BsonDocumentCapture"));
			client.addObjectEvent(objectEvent);
			client.sendDocument();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void basicObjectEventCapture() {
		try {
			// Make basic Object Event
			ObjectEvent objectEvent = new ObjectEvent(System.currentTimeMillis(), "-06:00", "OBSERVE");

			List<String> epcList = new ArrayList<String>();
			epcList.add("urn:epc:id:sgtin:0614141.107346.2018");
			objectEvent.setEpcList(epcList);

			objectEvent.setBizStep("urn:epcglobal:cbv:bizstep:receiving");
			objectEvent.setDisposition("urn:epcglobal:cbv:disp:in_progress");
			objectEvent.setReadPoint("urn:epc:id:sgln:0012345.11111.400");
			objectEvent.setBizLocation("urn:epc:id:sgln:0012345.11111.0");

			Map<String, List<String>> bizTransactionList = new HashMap<String, List<String>>();
			List<String> bizTransaction1 = new ArrayList<String>();
			bizTransaction1.add("http://transaction.acme.com/po/12345678");
			bizTransactionList.put("urn:epcglobal:cbv:btt:po", bizTransaction1);
			List<String> bizTransaction2 = new ArrayList<String>();
			bizTransaction2.add("urn:epcglobal:cbv:bt:0614141073467:1152");
			bizTransactionList.put("urn:epcglobal:cbv:btt:desadv", bizTransaction2);
			objectEvent.setBizTransactionList(bizTransactionList);

			Map<String, String> namespaces = new HashMap<String, String>();
			namespaces.put("http://ns.example.com/epcis0", "example0");
			namespaces.put("http://ns.example.com/epcis1", "example1");
			namespaces.put("http://ns.example.com/epcis2", "example2");
			objectEvent.setNamespaces(namespaces);

			BsonDocument extensionMap = new BsonDocument();
			extensionMap.put("http://ns.example.com/epcis0#a", new BsonInt32(15));
			BsonDocument b = new BsonDocument();
			b.put("http://ns.example.com/epcis1#c", new BsonDouble(20.5));
			extensionMap.put("http://ns.example.com/epcis0#b", b);
			BsonDocument h = new BsonDocument();
			h.put("http://ns.example.com/epcis1#d", new BsonBoolean(true));
			BsonDocument e = new BsonDocument();
			e.put("http://ns.example.com/epcis2#f", new BsonDateTime(System.currentTimeMillis()));
			h.put("http://ns.example.com/epcis1#e", e);
			h.put("http://ns.example.com/epcis1#g", new BsonInt64(50));
			extensionMap.put("http://ns.example.com/epcis0#h", h);
			objectEvent.setExtensions(extensionMap);

			QuantityElement quantity = new QuantityElement();
			quantity.setEpcClass("urn:epc:class:lgtin:4012345.012345.998877");
			quantity.setQuantity(200d);
			quantity.setUom("KGM");
			List<QuantityElement> quantityList = new ArrayList<QuantityElement>();
			quantityList.add(quantity);
			objectEvent.setQuantityList(quantityList);

			Map<String, List<String>> sourceList = new HashMap<String, List<String>>();
			List<String> source1 = new ArrayList<String>();
			source1.add("urn:epc:id:sgln:4012345.00001.0");
			sourceList.put("urn:epcglobal:cbv:sdt:possessing_party", source1);
			List<String> source2 = new ArrayList<String>();
			source2.add("urn:epc:id:sgln:4012345.00225.0");
			sourceList.put("urn:epcglobal:cbv:sdt:location", source2);
			objectEvent.setBizTransactionList(sourceList);

			Map<String, List<String>> destList = new HashMap<String, List<String>>();
			List<String> dest1 = new ArrayList<String>();
			dest1.add("urn:epc:id:sgln:4012345.00001.0");
			destList.put("urn:epcglobal:cbv:sdt:possessing_party", dest1);
			List<String> dest2 = new ArrayList<String>();
			dest2.add("urn:epc:id:sgln:4012345.00225.0");
			destList.put("urn:epcglobal:cbv:sdt:location", dest2);
			objectEvent.setBizTransactionList(destList);

			objectEvent.setIlmd(extensionMap);

			EPCISClient client = new EPCISClient(new URL("http://localhost:8080/epcis/Service/BsonDocumentCapture"));
			for(int i = 0 ; i < 10000 ; i++){
				client.addObjectEvent(objectEvent);
			}
			client.sendDocument();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}
