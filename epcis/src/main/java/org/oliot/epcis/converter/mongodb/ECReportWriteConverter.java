package org.oliot.epcis.converter.mongodb;

import java.util.Iterator;
import java.util.Map;

import org.bson.BsonArray;
import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.BsonValue;

/**
 * Copyright (C) 2014-2016 Jaewook Byun
 *
 * This project is part of Oliot open source (http://oliot.org). Oliot EPCIS
 * v1.2.x is Java Web Service complying with Electronic Product Code Information
 * Service (EPCIS) v1.2.
 *
 * @author Jaewook Byun, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         bjw0829@kaist.ac.kr, bjw0829@gmail.com
 */

public class ECReportWriteConverter {

	public static BsonDocument convert(String epcString, long eventTime, String eventTimeZoneOffset,
			long recordTimeMillis, String action, String bizStep, String disposition, String readPoint,
			String bizLocation, Map<String, BsonValue> extMap) {
		BsonDocument dbo = new BsonDocument();

		// EPC
		BsonArray epcList = new BsonArray();
		BsonDocument epc = new BsonDocument();
		epc.put("epc", new BsonString(epcString));
		epcList.add(epc);
		dbo.put("epcList", epcList);
		dbo.put("eventType", new BsonString("ObjectEvent"));
		dbo.put("eventTime", new BsonDateTime(eventTime));
		if (eventTimeZoneOffset == null) {
			dbo.put("eventTimeZoneOffset", new BsonString("+09:00"));
		} else {
			dbo.put("eventTimeZoneOffset", new BsonString(eventTimeZoneOffset));
		}
		dbo.put("recordTime", new BsonDateTime(recordTimeMillis));
		if (action == null) {
			dbo.put("action", new BsonString("OBSERVE"));
		} else {
			dbo.put("action", new BsonString(action));
		}
		if (bizStep != null) {
			dbo.put("bizStep", new BsonString(bizStep));
		}
		if (disposition != null) {
			dbo.put("dispsition", new BsonString(disposition));
		}
		if (readPoint != null) {
			dbo.put("readPoint", new BsonDocument("id", new BsonString(readPoint)));
		}
		if (bizLocation != null) {
			dbo.put("bizLocation", new BsonDocument("id", new BsonString(bizLocation)));
		}
		// Extension Field
		if (extMap.isEmpty() == false) {
			Iterator<String> keyIterator = extMap.keySet().iterator();
			BsonDocument any = new BsonDocument();
			String namespaceURI = MongoWriterUtil.encodeMongoObjectKey(
					"http://www.gs1.org/docs/epc/ale_1_1-schemas-20071202/EPCglobal-ale-1_1-ale.xsd");
			any.put("@" + namespaceURI, new BsonString("ale"));

			while (keyIterator.hasNext()) {
				String key = keyIterator.next();
				BsonValue value = extMap.get(key);
				String qnameKey = MongoWriterUtil.encodeMongoObjectKey(namespaceURI + "#" + key);

				any.put(qnameKey, value);
			}
			dbo.put("any", any);
		}

		return dbo;
	}
}
