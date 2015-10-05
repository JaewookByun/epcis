package org.oliot.epcis.serde.mongodb;

import java.util.Iterator;
import java.util.Map;
import org.oliot.model.epcis.SubscriptionType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

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

@Component
@WritingConverter
public class SubscriptionWriteConverter implements
		Converter<SubscriptionType, DBObject> {

	public DBObject convert(SubscriptionType subscription) {
		// Example Code , Not used
		DBObject dbObject = new BasicDBObject();
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

		dbObject.put("queryName", queryName);
		dbObject.put("subscriptionID", subscriptionID);
		dbObject.put("dest", dest);
		dbObject.put("cronExpression", cronExpression);
		dbObject.put("ignoreReceivedEvent", ignoreReceivedEvent);
		dbObject.put("reportIfEmpty", reportIfEmpty);
		dbObject.put("initialRecordTime", initialRecordTime);
		dbObject.put("eventType", eventType);
		dbObject.put("GE_eventTime", GE_eventTime);
		dbObject.put("LT_eventTime", LT_eventTime);
		dbObject.put("GE_recordTime", GE_recordTime);
		dbObject.put("LT_recordTime", LT_recordTime);
		dbObject.put("EQ_action", EQ_action);
		dbObject.put("EQ_bizStep", EQ_bizStep);
		dbObject.put("EQ_disposition", EQ_disposition);
		dbObject.put("EQ_readPoint", EQ_readPoint);
		dbObject.put("WD_readPoint", WD_readPoint);
		dbObject.put("EQ_bizLocation", EQ_bizLocation);
		dbObject.put("WD_bizLocation", WD_bizLocation);
		dbObject.put("EQ_transformationID", EQ_transformationID);
		dbObject.put("MATCH_epc", MATCH_epc);
		dbObject.put("MATCH_parentID", MATCH_parentID);
		dbObject.put("MATCH_inputEPC", MATCH_inputEPC);
		dbObject.put("MATCH_outputEPC", MATCH_outputEPC);
		dbObject.put("MATCH_anyEPC", MATCH_anyEPC);
		dbObject.put("MATCH_epcClass", MATCH_epcClass);
		dbObject.put("MATCH_inputEPCClass", MATCH_inputEPCClass);
		dbObject.put("MATCH_outputEPCClass", MATCH_outputEPCClass);
		dbObject.put("MATCH_anyEPCClass", MATCH_anyEPCClass);
		dbObject.put("EQ_quantity", EQ_quantity);
		dbObject.put("GT_quantity", GT_quantity);
		dbObject.put("GE_quantity", GE_quantity);
		dbObject.put("LT_quantity", LT_quantity);
		dbObject.put("LE_quantity", LE_quantity);
		dbObject.put("orderBy", orderBy);
		dbObject.put("orderDirection", orderDirection);
		dbObject.put("eventCountLimit", eventCountLimit);
		dbObject.put("maxEventCount", maxEventCount);
		dbObject.put("format", format );
		
		DBObject paramMapObject = new BasicDBObject();
		Iterator<String> paramMapIter = paramMap.keySet().iterator();
		while(paramMapIter.hasNext()){
			String paramKey = paramMapIter.next();
			String value = paramMap.get(paramKey);
			paramMapObject.put(paramKey, value);
		}
		dbObject.put("paramMap", paramMapObject);
		return dbObject;
	}

}
