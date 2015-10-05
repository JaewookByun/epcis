package org.oliot.epcis.serde.mongodb;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.oliot.model.epcis.SubscriptionType;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

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
@ReadingConverter
public class SubscriptionReadConverter implements Converter<DBObject, SubscriptionType> {

	public SubscriptionType convert(DBObject dbObject) {

		String queryName = (String) dbObject.get("queryName");
		String subscriptionID = (String) dbObject.get("subscriptionID");
		String dest = (String) dbObject.get("dest");
		String cronExpression = (String) dbObject.get("cronExpression");
		boolean ignoreReceivedEvent = (boolean) dbObject.get("ignoreReceivedEvent");
		boolean reportIfEmpty = (boolean) dbObject.get("reportIfEmpty");
		String initialRecordTime = (String) dbObject.get("initialRecordTime");
		String eventType = (String) dbObject.get("eventType");
		String GE_eventTime = (String) dbObject.get("GE_eventTime");
		String LT_eventTime = (String) dbObject.get("LT_eventTime");
		String GE_recordTime = (String) dbObject.get("GE_recordTime");
		String LT_recordTime = (String) dbObject.get("LT_recordTime");
		String EQ_action = (String) dbObject.get("EQ_action");
		String EQ_bizStep = (String) dbObject.get("EQ_bizStep");
		String EQ_disposition = (String) dbObject.get("EQ_disposition");
		String EQ_readPoint = (String) dbObject.get("EQ_readPoint");
		String WD_readPoint = (String) dbObject.get("WD_readPoint");
		String EQ_bizLocation = (String) dbObject.get("EQ_bizLocation");
		String WD_bizLocation = (String) dbObject.get("WD_bizLocation");
		String EQ_transformationID = (String) dbObject.get("EQ_transformationID");
		String MATCH_epc = (String) dbObject.get("MATCH_epc");
		String MATCH_parentID = (String) dbObject.get("MATCH_parentID");
		String MATCH_inputEPC = (String) dbObject.get("MATCH_inputEPC");
		String MATCH_outputEPC = (String) dbObject.get("MATCH_outputEPC");
		String MATCH_anyEPC = (String) dbObject.get("MATCH_anyEPC");
		String MATCH_epcClass = (String) dbObject.get("MATCH_epcClass");
		String MATCH_inputEPCClass = (String) dbObject.get("MATCH_inputEPCClass");
		String MATCH_outputEPCClass = (String) dbObject.get("MATCH_outputEPCClass");
		String MATCH_anyEPCClass = (String) dbObject.get("MATCH_anyEPCClass");
		String EQ_quantity = (String) dbObject.get("EQ_quantity");
		String GT_quantity = (String) dbObject.get("GT_quantity");
		String GE_quantity = (String) dbObject.get("GE_quantity");
		String LT_quantity = (String) dbObject.get("LT_quantity");
		String LE_quantity = (String) dbObject.get("LE_quantity");
		String orderBy = (String) dbObject.get("orderBy");
		String orderDirection = (String) dbObject.get("orderDirection");
		String eventCountLimit = (String) dbObject.get(" eventCountLimit");
		String maxEventCount = (String) dbObject.get("maxEventCount");
		String format = (String) dbObject.get("format");
		DBObject paramMapObject = (DBObject) dbObject.get("paramMap");
		Map<String, String> paramMap = new HashMap<String, String>();
		Iterator<String> paramIter = paramMapObject.keySet().iterator();
		while (paramIter.hasNext()) {
			String key = paramIter.next();
			String value = (String) paramMapObject.get(key);
			paramMap.put(key, value);
		}

		SubscriptionType st = new SubscriptionType(queryName, subscriptionID, dest, cronExpression, ignoreReceivedEvent,
				reportIfEmpty, initialRecordTime, eventType, GE_eventTime, LT_eventTime, GE_recordTime, LT_recordTime,
				EQ_action, EQ_bizStep, EQ_disposition, EQ_readPoint, WD_readPoint, EQ_bizLocation, WD_bizLocation,
				EQ_transformationID, MATCH_epc, MATCH_parentID, MATCH_inputEPC, MATCH_outputEPC, MATCH_anyEPC,
				MATCH_epcClass, MATCH_inputEPCClass, MATCH_outputEPCClass, MATCH_anyEPCClass, EQ_quantity, GT_quantity,
				GE_quantity, LT_quantity, LE_quantity, orderBy, orderDirection, eventCountLimit, maxEventCount, format,
				paramMap);

		return st;
	}
}
