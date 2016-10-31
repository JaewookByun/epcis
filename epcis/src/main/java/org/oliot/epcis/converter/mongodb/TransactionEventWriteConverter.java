package org.oliot.epcis.converter.mongodb;

import static org.oliot.epcis.converter.mongodb.MongoWriterUtil.*;

import java.util.GregorianCalendar;
import java.util.List;

import org.bson.BsonArray;
import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.oliot.model.epcis.BusinessLocationType;
import org.oliot.model.epcis.BusinessTransactionListType;
import org.oliot.model.epcis.BusinessTransactionType;
import org.oliot.model.epcis.EPC;
import org.oliot.model.epcis.EPCISEventExtensionType;
import org.oliot.model.epcis.EPCListType;
import org.oliot.model.epcis.ErrorDeclarationType;
import org.oliot.model.epcis.ReadPointType;
import org.oliot.model.epcis.TransactionEventExtensionType;
import org.oliot.model.epcis.TransactionEventType;

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

public class TransactionEventWriteConverter {

	public BsonDocument convert(TransactionEventType transactionEventType, Integer gcpLength) {

		BsonDocument dbo = new BsonDocument();

		dbo.put("eventType", new BsonString("TransactionEvent"));
		// Event Time
		if (transactionEventType.getEventTime() != null)
			dbo.put("eventTime",
					new BsonDateTime(transactionEventType.getEventTime().toGregorianCalendar().getTimeInMillis()));
		// Event Time Zone
		if (transactionEventType.getEventTimeZoneOffset() != null)
			dbo.put("eventTimeZoneOffset", new BsonString(transactionEventType.getEventTimeZoneOffset()));
		// Record Time : according to M5
		GregorianCalendar recordTime = new GregorianCalendar();
		long recordTimeMilis = recordTime.getTimeInMillis();
		dbo.put("recordTime", new BsonDateTime(recordTimeMilis));
		// Parent ID
		if (transactionEventType.getParentID() != null)
			dbo.put("parentID",
					new BsonString(MongoWriterUtil.getInstanceEPC(transactionEventType.getParentID(), gcpLength)));
		// EPC List
		if (transactionEventType.getEpcList() != null) {
			EPCListType epcs = transactionEventType.getEpcList();
			List<EPC> epcList = epcs.getEpc();
			BsonArray epcDBList = new BsonArray();
			for (int i = 0; i < epcList.size(); i++) {
				BsonDocument epcDB = new BsonDocument();
				epcDB.put("epc", new BsonString(MongoWriterUtil.getInstanceEPC(epcList.get(i).getValue(), gcpLength)));
				epcDBList.add(epcDB);
			}
			dbo.put("epcList", epcDBList);
		}
		// Action
		if (transactionEventType.getAction() != null)
			dbo.put("action", new BsonString(transactionEventType.getAction().name()));
		// BizStep
		if (transactionEventType.getBizStep() != null)
			dbo.put("bizStep", new BsonString(transactionEventType.getBizStep()));
		// Disposition
		if (transactionEventType.getDisposition() != null)
			dbo.put("disposition", new BsonString(transactionEventType.getDisposition()));
		if (transactionEventType.getReadPoint() != null) {
			ReadPointType readPointType = transactionEventType.getReadPoint();
			BsonDocument readPoint = getReadPointObject(readPointType, gcpLength);
			dbo.put("readPoint", readPoint);
		}
		if (transactionEventType.getBizLocation() != null) {
			BusinessLocationType bizLocationType = transactionEventType.getBizLocation();
			BsonDocument bizLocation = getBizLocationObject(bizLocationType, gcpLength);
			dbo.put("bizLocation", bizLocation);
		}

		if (transactionEventType.getBizTransactionList() != null) {
			BusinessTransactionListType bizListType = transactionEventType.getBizTransactionList();
			List<BusinessTransactionType> bizList = bizListType.getBizTransaction();

			BsonArray bizTranList = getBizTransactionObjectList(bizList);
			dbo.put("bizTransactionList", bizTranList);
		}

		// Vendor Extension
		if (transactionEventType.getAny() != null) {
			List<Object> objList = transactionEventType.getAny();
			BsonDocument map2Save = getAnyMap(objList);
			if (map2Save != null && map2Save.isEmpty() == false)
				dbo.put("any", map2Save);

		}

		// Extension
		if (transactionEventType.getExtension() != null) {
			TransactionEventExtensionType oee = transactionEventType.getExtension();
			BsonDocument extension = getTransactionEventExtensionObject(oee, gcpLength);
			dbo.put("extension", extension);
		}

		// Event ID
		if (transactionEventType.getBaseExtension() != null) {
			if (transactionEventType.getBaseExtension().getEventID() != null) {
				dbo.put("eventID", new BsonString(transactionEventType.getBaseExtension().getEventID()));
			}
		}

		// Error Declaration
		// If declared, it notes that the event is erroneous
		if (transactionEventType.getBaseExtension() != null) {
			EPCISEventExtensionType eeet = transactionEventType.getBaseExtension();
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
