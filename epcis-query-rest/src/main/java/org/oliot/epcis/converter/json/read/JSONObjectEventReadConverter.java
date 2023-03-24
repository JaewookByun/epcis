package org.oliot.epcis.converter.json.read;

import java.util.ArrayList;

import org.bson.Document;

import io.vertx.core.json.JsonObject;

import static org.oliot.epcis.converter.json.read.JSONEPCISEventReadConverter.*;

/**
 * Copyright (C) 2020-2021. (Jaewook Byun) all rights reserved.
 * <p>
 * Oliot EPCIS X is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * Among various modules, epcis-query-rest acts as a server to receive queries
 * to provide filtered, sorted, limited events of interest inside EPCIS
 * repository.
 * <p>
 * 
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr
 *         <p>
 *         Associate Director, Auto-ID Labs, KAIST, bjw0829@kaist.ac.kr
 */
public class JSONObjectEventReadConverter {

	public static JsonObject convert(Document bsonEvent, ArrayList<String> namespaces, JsonObject extType) {
		JsonObject converted = new JsonObject().put("type", "ObjectEvent");
		
		putCommonEventFields(converted, bsonEvent, namespaces, extType);
		putEpcList(converted, bsonEvent);
		putAction(converted, bsonEvent);
		putBizStep(converted, bsonEvent);
		putDisposition(converted, bsonEvent);
		putReadPoint(converted, bsonEvent);
		putBizLocation(converted, bsonEvent);
		putBizTransactionList(converted, bsonEvent);
		putExtension(converted, bsonEvent, namespaces, extType);
		putSourceList(converted, bsonEvent);
		putDestinationList(converted, bsonEvent);
		putQuantityList(converted, bsonEvent);
		putILMD(converted, bsonEvent, namespaces, extType);
		putSensorElementList(converted, bsonEvent, namespaces, extType);
		putPersistentDisposition(converted, bsonEvent);
		
		return converted;
	}
}
