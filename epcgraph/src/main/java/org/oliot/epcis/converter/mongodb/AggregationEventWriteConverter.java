package org.oliot.epcis.converter.mongodb;

import static org.oliot.epcis.converter.mongodb.MongoWriterUtil.*;

import java.util.GregorianCalendar;
import java.util.List;

import org.bson.BsonArray;
import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.oliot.model.epcis.AggregationEventExtensionType;
import org.oliot.model.epcis.AggregationEventType;
import org.oliot.model.epcis.BusinessLocationType;
import org.oliot.model.epcis.BusinessTransactionListType;
import org.oliot.model.epcis.BusinessTransactionType;
import org.oliot.model.epcis.EPC;
import org.oliot.model.epcis.EPCISEventExtensionType;
import org.oliot.model.epcis.EPCListType;
import org.oliot.model.epcis.ErrorDeclarationType;
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

public class AggregationEventWriteConverter {

	public BsonDocument convert(AggregationEventType aggregationEventType, Integer gcpLength) {
		BsonDocument dbo = new BsonDocument();

		dbo.put("eventType", new BsonString("AggregationEvent"));
		// Event Time
		if (aggregationEventType.getEventTime() != null)
			dbo.put("eventTime",
					new BsonDateTime(aggregationEventType.getEventTime().toGregorianCalendar().getTimeInMillis()));
		// Event Time Zone
		if (aggregationEventType.getEventTimeZoneOffset() != null)
			dbo.put("eventTimeZoneOffset", new BsonString(aggregationEventType.getEventTimeZoneOffset()));
		// Record Time : according to M5
		GregorianCalendar recordTime = new GregorianCalendar();
		long recordTimeMilis = recordTime.getTimeInMillis();
		dbo.put("recordTime", new BsonDateTime(recordTimeMilis));

		// Parent ID
		if (aggregationEventType.getParentID() != null) {
			dbo.put("parentID",
					new BsonString(MongoWriterUtil.getInstanceEPC(aggregationEventType.getParentID(), gcpLength)));
		}
		// Child EPCs
		if (aggregationEventType.getChildEPCs() != null) {
			EPCListType epcs = aggregationEventType.getChildEPCs();
			List<EPC> epcList = epcs.getEpc();
			BsonArray epcDBList = new BsonArray();

			for (int i = 0; i < epcList.size(); i++) {
				BsonDocument epcDB = new BsonDocument();
				epcDB.put("epc", new BsonString(MongoWriterUtil.getInstanceEPC(epcList.get(i).getValue(), gcpLength)));
				epcDBList.add(epcDB);
			}
			dbo.put("childEPCs", epcDBList);
		}
		// Action
		if (aggregationEventType.getAction() != null)
			dbo.put("action", new BsonString(aggregationEventType.getAction().name()));
		// Biz Step
		if (aggregationEventType.getBizStep() != null)
			dbo.put("bizStep", new BsonString(aggregationEventType.getBizStep()));
		// Disposition
		if (aggregationEventType.getDisposition() != null)
			dbo.put("disposition", new BsonString(aggregationEventType.getDisposition()));
		// ReadPoint
		if (aggregationEventType.getReadPoint() != null) {
			ReadPointType readPointType = aggregationEventType.getReadPoint();
			BsonDocument readPoint = getReadPointObject(readPointType, gcpLength);
			dbo.put("readPoint", readPoint);
		}
		// BizLocation
		if (aggregationEventType.getBizLocation() != null) {
			BusinessLocationType bizLocationType = aggregationEventType.getBizLocation();
			BsonDocument bizLocation = getBizLocationObject(bizLocationType, gcpLength);
			dbo.put("bizLocation", bizLocation);
		}

		if (aggregationEventType.getBizTransactionList() != null) {
			BusinessTransactionListType bizListType = aggregationEventType.getBizTransactionList();
			List<BusinessTransactionType> bizList = bizListType.getBizTransaction();

			BsonArray bizTranList = getBizTransactionObjectList(bizList);
			dbo.put("bizTransactionList", bizTranList);
		}

		// Vendor Extension
		if (aggregationEventType.getAny() != null) {
			List<Object> objList = aggregationEventType.getAny();
			BsonDocument map2Save = getAnyMap(objList);
			if (map2Save != null && map2Save.isEmpty() == false)
				dbo.put("any", map2Save);

		}

		// Extension
		if (aggregationEventType.getExtension() != null) {
			AggregationEventExtensionType aee = aggregationEventType.getExtension();
			BsonDocument extension = getAggregationEventExtensionObject(aee, gcpLength);
			dbo.put("extension", extension);

		}

		// Event ID
		if (aggregationEventType.getBaseExtension() != null) {
			if (aggregationEventType.getBaseExtension().getEventID() != null) {
				dbo.put("eventID", new BsonString(aggregationEventType.getBaseExtension().getEventID()));
			}
		}

		// Error Declaration
		// If declared, it notes that the event is erroneous
		if (aggregationEventType.getBaseExtension() != null) {
			EPCISEventExtensionType eeet = aggregationEventType.getBaseExtension();
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
