package org.oliot.epcis.converter.mongodb;

import static org.oliot.epcis.converter.mongodb.MongoReaderUtil.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.oliot.model.epcis.BusinessLocationExtensionType;
import org.oliot.model.epcis.BusinessLocationType;
import org.oliot.model.epcis.BusinessTransactionListType;
import org.oliot.model.epcis.BusinessTransactionType;
import org.oliot.model.epcis.EPCISEventExtensionType;
import org.oliot.model.epcis.QuantityEventExtensionType;
import org.oliot.model.epcis.QuantityEventType;
import org.oliot.model.epcis.ReadPointExtensionType;
import org.oliot.model.epcis.ReadPointType;

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

public class QuantityEventReadConverter {

	public QuantityEventType convert(BsonDocument dbObject) {
		QuantityEventType quantityEventType = new QuantityEventType();
		int zone = 0;
		if (dbObject.get("eventTimeZoneOffset") != null) {
			String eventTimeZoneOffset = dbObject.getString("eventTimeZoneOffset").getValue();
			quantityEventType.setEventTimeZoneOffset(eventTimeZoneOffset);
			if (eventTimeZoneOffset.split(":").length == 2) {
				zone = Integer.parseInt(eventTimeZoneOffset.split(":")[0]);
			}
		}
		if (dbObject.get("eventTime") != null) {
			quantityEventType.setEventTime(getXMLGregorianCalendar(dbObject.getDateTime("eventTime")));
		}
		if (dbObject.get("recordTime") != null) {
			quantityEventType.setRecordTime(getXMLGregorianCalendar(dbObject.getDateTime("recordTime")));
		}
		if (dbObject.get("epcClass") != null)
			quantityEventType.setEpcClass(dbObject.getString("epcClass").getValue());
		if (dbObject.get("bizStep") != null)
			quantityEventType.setBizStep(dbObject.getString("bizStep").getValue());
		if (dbObject.get("disposition") != null)
			quantityEventType.setDisposition(dbObject.getString("disposition").getValue());
		if (dbObject.get("baseExtension") != null) {
			EPCISEventExtensionType eeet = new EPCISEventExtensionType();
			BsonDocument baseExtension = dbObject.getDocument("baseExtension");
			eeet = putEPCISExtension(eeet, baseExtension);
			quantityEventType.setBaseExtension(eeet);
		}
		if (dbObject.get("quantity") != null) {
			int quantity = (int) dbObject.getInt64("quantity").getValue();
			quantityEventType.setQuantity(quantity);
		}
		if (dbObject.get("readPoint") != null) {
			BsonDocument readPointObject = dbObject.getDocument("readPoint");
			ReadPointType readPointType = new ReadPointType();
			if (readPointObject.get("id") != null) {
				readPointType.setId(readPointObject.getString("id").getValue());
			}
			if (readPointObject.get("extension") != null) {
				ReadPointExtensionType rpet = new ReadPointExtensionType();
				BsonDocument extension = readPointObject.getDocument("extension");
				rpet = putReadPointExtension(rpet, extension);
				readPointType.setExtension(rpet);
			}
			quantityEventType.setReadPoint(readPointType);
		}
		// BusinessLocation
		if (dbObject.get("bizLocation") != null) {
			BsonDocument bizLocationObject = dbObject.getDocument("bizLocation");
			BusinessLocationType bizLocationType = new BusinessLocationType();
			if (bizLocationObject.get("id") != null) {
				bizLocationType.setId(bizLocationObject.getString("id").getValue());
			}
			if (bizLocationObject.get("extension") != null) {
				BusinessLocationExtensionType blet = new BusinessLocationExtensionType();
				BsonDocument extension = bizLocationObject.getDocument("extension");
				blet = putBusinessLocationExtension(blet, extension);
				bizLocationType.setExtension(blet);
			}
			quantityEventType.setBizLocation(bizLocationType);
		}
		if (dbObject.get("bizTransactionList") != null) {
			BsonArray bizTranList = dbObject.getArray("bizTransactionList");
			BusinessTransactionListType btlt = new BusinessTransactionListType();
			List<BusinessTransactionType> bizTranArrayList = new ArrayList<BusinessTransactionType>();
			for (int i = 0; i < bizTranList.size(); i++) {
				// DBObject, key and value
				BsonDocument bizTran = bizTranList.get(i).asDocument();
				BusinessTransactionType btt = new BusinessTransactionType();
				Iterator<String> keyIter = bizTran.keySet().iterator();
				// at most one bizTran
				if (keyIter.hasNext()) {
					String key = keyIter.next();
					String value = bizTran.getString(key).getValue();
					if (key != null && value != null) {
						btt.setType(key);
						btt.setValue(value);
					}
				}
				if (btt != null)
					bizTranArrayList.add(btt);
			}
			btlt.setBizTransaction(bizTranArrayList);
			quantityEventType.setBizTransactionList(btlt);
		}

		// EventID and ErrorDeclaration
		quantityEventType.setBaseExtension(putEPCISEventExtensionType(dbObject, zone));

		// Vendor Extension
		if (dbObject.get("any") != null) {
			BsonDocument anyObject = dbObject.getDocument("any");
			List<Object> any = putAny(anyObject, null);
			quantityEventType.setAny(any);
		}

		// Extension Field
		if (dbObject.get("extension") != null) {
			QuantityEventExtensionType qeet = new QuantityEventExtensionType();
			BsonDocument extension = dbObject.getDocument("extension");
			qeet = putQuantityExtension(qeet, extension);
			quantityEventType.setExtension(qeet);
		}
		return quantityEventType;
	}
}
