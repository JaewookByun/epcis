package org.oliot.epcis.serde.mongodb;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bson.BsonDocument;
import org.oliot.model.epcis.SubscriptionType;

/**
 * Copyright (C) 2014 Jaewook Jack Byun
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

public class SubscriptionReadConverter {

	public SubscriptionType convert(BsonDocument dbObject) {

		String queryName = dbObject.getString("queryName").getValue();
		String subscriptionID = dbObject.getString("subscriptionID").getValue();
		String dest = dbObject.getString("dest").getValue();
		String cronExpression = dbObject.getString("cronExpression").getValue();
		boolean isScheduledSubscription = dbObject.getBoolean("isScheduledSubscription").getValue();
		boolean ignoreReceivedEvent = dbObject.getBoolean("ignoreReceivedEvent").getValue();
		boolean reportIfEmpty = dbObject.getBoolean("reportIfEmpty").getValue();
		String initialRecordTime = dbObject.getString("initialRecordTime").getValue();
		String eventType = dbObject.getString("eventType").getValue();
		String GE_eventTime = dbObject.getString("GE_eventTime").getValue();
		String LT_eventTime = dbObject.getString("LT_eventTime").getValue();
		String GE_recordTime = dbObject.getString("GE_recordTime").getValue();
		String LT_recordTime = dbObject.getString("LT_recordTime").getValue();
		String EQ_action = dbObject.getString("EQ_action").getValue();
		String EQ_bizStep = dbObject.getString("EQ_bizStep").getValue();
		String EQ_disposition = dbObject.getString("EQ_disposition").getValue();
		String EQ_readPoint = dbObject.getString("EQ_readPoint").getValue();
		String WD_readPoint = dbObject.getString("WD_readPoint").getValue();
		String EQ_bizLocation = dbObject.getString("EQ_bizLocation").getValue();
		String WD_bizLocation = dbObject.getString("WD_bizLocation").getValue();
		String EQ_transformationID = dbObject.getString("EQ_transformationID").getValue();
		String MATCH_epc = dbObject.getString("MATCH_epc").getValue();
		String MATCH_parentID = dbObject.getString("MATCH_parentID").getValue();
		String MATCH_inputEPC = dbObject.getString("MATCH_inputEPC").getValue();
		String MATCH_outputEPC = dbObject.getString("MATCH_outputEPC").getValue();
		String MATCH_anyEPC = dbObject.getString("MATCH_anyEPC").getValue();
		String MATCH_epcClass = dbObject.getString("MATCH_epcClass").getValue();
		String MATCH_inputEPCClass = dbObject.getString("MATCH_inputEPCClass").getValue();
		String MATCH_outputEPCClass = dbObject.getString("MATCH_outputEPCClass").getValue();
		String MATCH_anyEPCClass = dbObject.getString("MATCH_anyEPCClass").getValue();
		String EQ_quantity = dbObject.getString("EQ_quantity").getValue();
		String GT_quantity = dbObject.getString("GT_quantity").getValue();
		String GE_quantity = dbObject.getString("GE_quantity").getValue();
		String LT_quantity = dbObject.getString("LT_quantity").getValue();
		String LE_quantity = dbObject.getString("LE_quantity").getValue();
		String orderBy = dbObject.getString("orderBy").getValue();
		String orderDirection = dbObject.getString("orderDirection").getValue();
		String eventCountLimit = dbObject.getString(" eventCountLimit").getValue();
		String maxEventCount = dbObject.getString("maxEventCount").getValue();
		String format = dbObject.getString("format").getValue();
		BsonDocument paramMapObject = dbObject.getDocument("paramMap");
		Map<String, String> paramMap = new HashMap<String, String>();
		Iterator<String> paramIter = paramMapObject.keySet().iterator();
		while (paramIter.hasNext()) {
			String key = paramIter.next();
			String value = (String) paramMapObject.getString(key).getValue();
			paramMap.put(key, value);
		}

		SubscriptionType st = new SubscriptionType(queryName, subscriptionID, dest, cronExpression, isScheduledSubscription, ignoreReceivedEvent,
				reportIfEmpty, initialRecordTime, eventType, GE_eventTime, LT_eventTime, GE_recordTime, LT_recordTime,
				EQ_action, EQ_bizStep, EQ_disposition, EQ_readPoint, WD_readPoint, EQ_bizLocation, WD_bizLocation,
				EQ_transformationID, MATCH_epc, MATCH_parentID, MATCH_inputEPC, MATCH_outputEPC, MATCH_anyEPC,
				MATCH_epcClass, MATCH_inputEPCClass, MATCH_outputEPCClass, MATCH_anyEPCClass, EQ_quantity, GT_quantity,
				GE_quantity, LT_quantity, LE_quantity, orderBy, orderDirection, eventCountLimit, maxEventCount, format,
				paramMap);

		return st;
	}
}
