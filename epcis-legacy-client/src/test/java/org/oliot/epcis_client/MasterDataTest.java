package org.oliot.epcis_client;

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
public class MasterDataTest{
	
	@Test
	public void basicMasterDataCapture() {
		try {
			MasterData masterData = new MasterData(VocabularyType.BusinessLocationID, "urn:epc:id:sgln:0037000.00729.0");
			
			Map<String, String> attributes = new HashMap<String, String>();
			attributes.put("http://epcis.example.com/mda/latitude", "+28.0000");
			attributes.put("http://epcis.example.com/mda/longitude", "-70.0000");
			masterData.setAttributes(attributes);
			
			List<String> children = new ArrayList<String>();
			children.add("urn:epc:id:sgln:0037000.00729.8201");
			children.add("urn:epc:id:sgln:0037000.00729.8202");
			children.add("urn:epc:id:sgln:0037000.00729.8203");
			masterData.setChildren(children);
			
			EPCISClient client = new EPCISClient(new URL("http://localhost:8080/epcis-capture/Service/BsonDocumentCapture"));
			client.addMasterData(masterData);
			client.sendDocument();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}
