package org.oliot.epcis.converter.mongodb;

import static org.oliot.epcis.converter.mongodb.MongoWriterUtil.*;

import java.util.GregorianCalendar;
import java.util.List;

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

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

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

	public JsonObject convert(TransactionEventType transactionEventType) {

		JsonObject dbo = new JsonObject();

		dbo.put("eventType", "TransactionEvent");
		// Event Time
		if (transactionEventType.getEventTime() != null)
			dbo.put("eventTime", transactionEventType.getEventTime().toGregorianCalendar().getTimeInMillis());
		// Event Time Zone
		if (transactionEventType.getEventTimeZoneOffset() != null)
			dbo.put("eventTimeZoneOffset", transactionEventType.getEventTimeZoneOffset());
		// Record Time : according to M5
		GregorianCalendar recordTime = new GregorianCalendar();
		long recordTimeMilis = recordTime.getTimeInMillis();
		dbo.put("recordTime", recordTimeMilis);
		// Parent ID
		if (transactionEventType.getParentID() != null)
			dbo.put("parentID", transactionEventType.getParentID());
		// EPC List
		if (transactionEventType.getEpcList() != null) {
			EPCListType epcs = transactionEventType.getEpcList();
			List<EPC> epcList = epcs.getEpc();
			JsonArray epcDBList = new JsonArray();
			for (int i = 0; i < epcList.size(); i++) {
				JsonObject epcDB = new JsonObject();
				epcDB.put("epc", epcList.get(i).getValue());
				epcDBList.add(epcDB);
			}
			dbo.put("epcList", epcDBList);
		}
		// Action
		if (transactionEventType.getAction() != null)
			dbo.put("action", transactionEventType.getAction().name());
		// BizStep
		if (transactionEventType.getBizStep() != null)
			dbo.put("bizStep", transactionEventType.getBizStep());
		// Disposition
		if (transactionEventType.getDisposition() != null)
			dbo.put("disposition", transactionEventType.getDisposition());
		if (transactionEventType.getReadPoint() != null) {
			ReadPointType readPointType = transactionEventType.getReadPoint();
			JsonObject readPoint = getReadPointObject(readPointType);
			dbo.put("readPoint", readPoint);
		}
		if (transactionEventType.getBizLocation() != null) {
			BusinessLocationType bizLocationType = transactionEventType.getBizLocation();
			JsonObject bizLocation = getBizLocationObject(bizLocationType);
			dbo.put("bizLocation", bizLocation);
		}

		if (transactionEventType.getBizTransactionList() != null) {
			BusinessTransactionListType bizListType = transactionEventType.getBizTransactionList();
			List<BusinessTransactionType> bizList = bizListType.getBizTransaction();

			JsonArray bizTranList = getBizTransactionObjectList(bizList);
			dbo.put("bizTransactionList", bizTranList);
		}

		// Vendor Extension
		if (transactionEventType.getAny() != null) {
			List<Object> objList = transactionEventType.getAny();
			JsonObject map2Save = getAnyMap(objList);
			if (map2Save != null && map2Save.isEmpty() == false)
				dbo.put("any", map2Save);

		}

		// Extension
		if (transactionEventType.getExtension() != null) {
			TransactionEventExtensionType oee = transactionEventType.getExtension();
			JsonObject extension = getTransactionEventExtensionObject(oee);
			dbo.put("extension", extension);
		}

		// Event ID
		if (transactionEventType.getBaseExtension() != null) {
			if (transactionEventType.getBaseExtension().getEventID() != null) {
				dbo.put("eventID", transactionEventType.getBaseExtension().getEventID());
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
