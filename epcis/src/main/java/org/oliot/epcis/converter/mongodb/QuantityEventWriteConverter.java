package org.oliot.epcis.converter.mongodb;

import static org.oliot.epcis.converter.mongodb.MongoWriterUtil.*;

import java.util.GregorianCalendar;
import java.util.List;

import org.bson.BsonArray;
import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.BsonString;
import org.oliot.model.epcis.BusinessLocationType;
import org.oliot.model.epcis.BusinessTransactionListType;
import org.oliot.model.epcis.BusinessTransactionType;
import org.oliot.model.epcis.EPCISEventExtensionType;
import org.oliot.model.epcis.ErrorDeclarationType;
import org.oliot.model.epcis.QuantityEventExtensionType;
import org.oliot.model.epcis.QuantityEventType;
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

public class QuantityEventWriteConverter {

	public BsonDocument convert(QuantityEventType quantityEventType, Integer gcpLength) {

		BsonDocument dbo = new BsonDocument();

		dbo.put("eventType", new BsonString("QuantityEvent"));
		// Event Time
		if (quantityEventType.getEventTime() != null)
			dbo.put("eventTime",
					new BsonDateTime(quantityEventType.getEventTime().toGregorianCalendar().getTimeInMillis()));
		// Event Time zone
		if (quantityEventType.getEventTimeZoneOffset() != null)
			dbo.put("eventTimeZoneOffset", new BsonString(quantityEventType.getEventTimeZoneOffset()));
		// Record Time : according to M5
		GregorianCalendar recordTime = new GregorianCalendar();
		long recordTimeMilis = recordTime.getTimeInMillis();
		dbo.put("recordTime", new BsonDateTime(recordTimeMilis));
		// EPC Class
		if (quantityEventType.getEpcClass() != null)
			dbo.put("epcClass",
					new BsonString(MongoWriterUtil.getClassEPC(quantityEventType.getEpcClass(), gcpLength)));
		dbo.put("quantity", new BsonInt64(quantityEventType.getQuantity()));
		// Business Step
		if (quantityEventType.getBizStep() != null)
			dbo.put("bizStep", new BsonString(quantityEventType.getBizStep()));
		// Disposition
		if (quantityEventType.getDisposition() != null)
			dbo.put("disposition", new BsonString(quantityEventType.getDisposition()));
		// Read Point
		if (quantityEventType.getReadPoint() != null) {
			ReadPointType readPointType = quantityEventType.getReadPoint();
			BsonDocument readPoint = getReadPointObject(readPointType, gcpLength);
			dbo.put("readPoint", readPoint);
		}
		// BizLocation
		if (quantityEventType.getBizLocation() != null) {
			BusinessLocationType bizLocationType = quantityEventType.getBizLocation();
			BsonDocument bizLocation = getBizLocationObject(bizLocationType, gcpLength);
			dbo.put("bizLocation", bizLocation);
		}

		// Vendor Extension
		if (quantityEventType.getAny() != null) {
			List<Object> objList = quantityEventType.getAny();
			BsonDocument map2Save = getAnyMap(objList);
			if (map2Save != null && map2Save.isEmpty() == false)
				dbo.put("any", map2Save);

		}

		// BizTransaction
		if (quantityEventType.getBizTransactionList() != null) {
			BusinessTransactionListType bizListType = quantityEventType.getBizTransactionList();
			List<BusinessTransactionType> bizList = bizListType.getBizTransaction();
			BsonArray bizTranList = getBizTransactionObjectList(bizList);
			dbo.put("bizTransactionList", bizTranList);
		}
		// Extension
		if (quantityEventType.getExtension() != null) {
			QuantityEventExtensionType oee = quantityEventType.getExtension();
			BsonDocument extension = getQuantityEventExtensionObject(oee);
			dbo.put("extension", extension);
		}

		// Event ID
		if (quantityEventType.getBaseExtension() != null) {
			if (quantityEventType.getBaseExtension().getEventID() != null) {
				dbo.put("eventID", new BsonString(quantityEventType.getBaseExtension().getEventID()));
			}
		}

		// Error Declaration
		// If declared, it notes that the event is erroneous
		if (quantityEventType.getBaseExtension() != null) {
			EPCISEventExtensionType eeet = quantityEventType.getBaseExtension();
			ErrorDeclarationType edt = eeet.getErrorDeclaration();
			if (edt != null) {
				if (edt.getDeclarationTime() != null) {
					dbo.put("errorDeclaration", MongoWriterUtil.getErrorDeclaration(edt));
				}
			}
		}

		return dbo;
	}
}
