package org.oliot.epcis.serde.mongodb;

import java.util.Iterator;
import java.util.Map;

import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.oliot.model.epcis.SubscriptionType;

/**
 * Copyright (C) 2014-2016 Jaewook Byun
 *
 * This project is part of Oliot (oliot.org), pursuing the implementation of
 * Electronic Product Code Information Service(EPCIS) v1.1 specification in
 * EPCglobal.
 * [http://www.gs1.org/gsmp/kc/epcglobal/epcis/epcis_1_1-standard-20140520.pdf]
 * 
 *
 * @author Jaewook Byun, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         bjw0829@kaist.ac.kr, bjw0829@gmail.com
 */

public class SubscriptionWriteConverter {

	public BsonDocument convert(SubscriptionType subscription) {
		// Example Code , Not used
		BsonDocument dbObject = new BsonDocument();
		String queryName = subscription.getQueryName();
		String subscriptionID = subscription.getSubscriptionID();
		String dest = subscription.getDest();
		boolean ignoreReceivedEvent = subscription.isIgnoreReceivedEvent();
		String cronExpression = subscription.getCronExpression();
		boolean reportIfEmpty = subscription.isReportIfEmpty();
		String initialRecordTime = subscription.getInitialRecordTime();
		String eventType = subscription.getEventType();
		String GE_eventTime = subscription.getGE_eventTime();
		String LT_eventTime = subscription.getLT_eventTime();
		String GE_recordTime = subscription.getGE_recordTime();
		String LT_recordTime = subscription.getLT_recordTime();
		String EQ_action = subscription.getEQ_action();
		String EQ_bizStep = subscription.getEQ_bizStep();
		String EQ_disposition = subscription.getEQ_disposition();
		String EQ_readPoint = subscription.getEQ_readPoint();
		String WD_readPoint = subscription.getWD_readPoint();
		String EQ_bizLocation = subscription.getEQ_bizLocation();
		String WD_bizLocation = subscription.getWD_bizLocation();
		String EQ_transformationID = subscription.getEQ_transformationID();
		String MATCH_epc = subscription.getMATCH_epc();
		String MATCH_parentID = subscription.getMATCH_parentID();
		String MATCH_inputEPC = subscription.getMATCH_inputEPC();
		String MATCH_outputEPC = subscription.getMATCH_outputEPC();
		String MATCH_anyEPC = subscription.getMATCH_anyEPC();
		String MATCH_epcClass = subscription.getMATCH_epcClass();
		String MATCH_inputEPCClass = subscription.getMATCH_inputEPCClass();
		String MATCH_outputEPCClass = subscription.getMATCH_outputEPCClass();
		String MATCH_anyEPCClass = subscription.getMATCH_anyEPCClass();
		String EQ_quantity = subscription.getEQ_quantity();
		String GT_quantity = subscription.getGT_quantity();
		String GE_quantity = subscription.getGE_quantity();
		String LT_quantity = subscription.getLT_quantity();
		String LE_quantity = subscription.getLE_quantity();
		String orderBy = subscription.getOrderBy();
		String orderDirection = subscription.getOrderDirection();
		String eventCountLimit = subscription.getEventCountLimit();
		String maxEventCount = subscription.getMaxEventCount();
		String format = subscription.getFormat();
		Map<String, String> paramMap = subscription.getParamMap();

		dbObject.put("queryName", new BsonString(queryName));
		dbObject.put("subscriptionID", new BsonString(subscriptionID));
		dbObject.put("dest", new BsonString(dest));
		dbObject.put("cronExpression", new BsonString(cronExpression));
		dbObject.put("ignoreReceivedEvent", new BsonBoolean(ignoreReceivedEvent));
		dbObject.put("reportIfEmpty", new BsonBoolean(reportIfEmpty));
		dbObject.put("initialRecordTime", new BsonString(initialRecordTime));
		dbObject.put("eventType", new BsonString(eventType));
		dbObject.put("GE_eventTime", new BsonString(GE_eventTime));
		dbObject.put("LT_eventTime", new BsonString(LT_eventTime));
		dbObject.put("GE_recordTime", new BsonString(GE_recordTime));
		dbObject.put("LT_recordTime", new BsonString(LT_recordTime));
		dbObject.put("EQ_action", new BsonString(EQ_action));
		dbObject.put("EQ_bizStep", new BsonString(EQ_bizStep));
		dbObject.put("EQ_disposition", new BsonString(EQ_disposition));
		dbObject.put("EQ_readPoint", new BsonString(EQ_readPoint));
		dbObject.put("WD_readPoint", new BsonString(WD_readPoint));
		dbObject.put("EQ_bizLocation", new BsonString(EQ_bizLocation));
		dbObject.put("WD_bizLocation", new BsonString(WD_bizLocation));
		dbObject.put("EQ_transformationID", new BsonString(EQ_transformationID));
		dbObject.put("MATCH_epc", new BsonString(MATCH_epc));
		dbObject.put("MATCH_parentID", new BsonString(MATCH_parentID));
		dbObject.put("MATCH_inputEPC", new BsonString(MATCH_inputEPC));
		dbObject.put("MATCH_outputEPC", new BsonString(MATCH_outputEPC));
		dbObject.put("MATCH_anyEPC", new BsonString(MATCH_anyEPC));
		dbObject.put("MATCH_epcClass", new BsonString(MATCH_epcClass));
		dbObject.put("MATCH_inputEPCClass", new BsonString(MATCH_inputEPCClass));
		dbObject.put("MATCH_outputEPCClass", new BsonString(MATCH_outputEPCClass));
		dbObject.put("MATCH_anyEPCClass", new BsonString(MATCH_anyEPCClass));
		dbObject.put("EQ_quantity", new BsonString(EQ_quantity));
		dbObject.put("GT_quantity", new BsonString(GT_quantity));
		dbObject.put("GE_quantity", new BsonString(GE_quantity));
		dbObject.put("LT_quantity", new BsonString(LT_quantity));
		dbObject.put("LE_quantity", new BsonString(LE_quantity));
		dbObject.put("orderBy", new BsonString(orderBy));
		dbObject.put("orderDirection", new BsonString(orderDirection));
		dbObject.put("eventCountLimit", new BsonString(eventCountLimit));
		dbObject.put("maxEventCount", new BsonString(maxEventCount));
		dbObject.put("format", new BsonString(format));

		BsonDocument paramMapObject = new BsonDocument();
		Iterator<String> paramMapIter = paramMap.keySet().iterator();
		while (paramMapIter.hasNext()) {
			String paramKey = paramMapIter.next();
			String value = paramMap.get(paramKey);
			paramMapObject.put(paramKey, new BsonString(value));
		}
		dbObject.put("paramMap", paramMapObject);
		return dbObject;
	}

}
