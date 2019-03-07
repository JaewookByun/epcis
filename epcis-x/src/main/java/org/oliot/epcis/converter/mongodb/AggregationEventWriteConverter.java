package org.oliot.epcis.converter.mongodb;

import static org.oliot.epcis.converter.mongodb.MongoWriterUtil.*;

import java.util.GregorianCalendar;
import java.util.List;

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

public class AggregationEventWriteConverter {

	public JsonObject convert(AggregationEventType aggregationEventType) {
		JsonObject dbo = new JsonObject();

		dbo.put("eventType", "AggregationEvent");
		// Event Time
		if (aggregationEventType.getEventTime() != null)
			dbo.put("eventTime", aggregationEventType.getEventTime().toGregorianCalendar().getTimeInMillis());
		// Event Time Zone
		if (aggregationEventType.getEventTimeZoneOffset() != null)
			dbo.put("eventTimeZoneOffset", aggregationEventType.getEventTimeZoneOffset());
		// Record Time : according to M5
		GregorianCalendar recordTime = new GregorianCalendar();
		long recordTimeMilis = recordTime.getTimeInMillis();
		dbo.put("recordTime", recordTimeMilis);

		// Parent ID
		if (aggregationEventType.getParentID() != null) {
			dbo.put("parentID", aggregationEventType.getParentID());
		}
		// Child EPCs
		if (aggregationEventType.getChildEPCs() != null) {
			EPCListType epcs = aggregationEventType.getChildEPCs();
			List<EPC> epcList = epcs.getEpc();
			JsonArray epcDBList = new JsonArray();

			for (int i = 0; i < epcList.size(); i++) {
				JsonObject epcDB = new JsonObject();
				epcDB.put("epc", epcList.get(i).getValue());
				epcDBList.add(epcDB);
			}
			dbo.put("childEPCs", epcDBList);
		}
		// Action
		if (aggregationEventType.getAction() != null)
			dbo.put("action", aggregationEventType.getAction().name());
		// Biz Step
		if (aggregationEventType.getBizStep() != null)
			dbo.put("bizStep", aggregationEventType.getBizStep());
		// Disposition
		if (aggregationEventType.getDisposition() != null)
			dbo.put("disposition", aggregationEventType.getDisposition());
		// ReadPoint
		if (aggregationEventType.getReadPoint() != null) {
			ReadPointType readPointType = aggregationEventType.getReadPoint();
			JsonObject readPoint = getReadPointObject(readPointType);
			dbo.put("readPoint", readPoint);
		}
		// BizLocation
		if (aggregationEventType.getBizLocation() != null) {
			BusinessLocationType bizLocationType = aggregationEventType.getBizLocation();
			JsonObject bizLocation = getBizLocationObject(bizLocationType);
			dbo.put("bizLocation", bizLocation);
		}

		if (aggregationEventType.getBizTransactionList() != null) {
			BusinessTransactionListType bizListType = aggregationEventType.getBizTransactionList();
			List<BusinessTransactionType> bizList = bizListType.getBizTransaction();

			JsonArray bizTranList = getBizTransactionObjectList(bizList);
			dbo.put("bizTransactionList", bizTranList);
		}

		// Vendor Extension
		if (aggregationEventType.getAny() != null) {
			List<Object> objList = aggregationEventType.getAny();
			JsonObject map2Save = getAnyMap(objList);
			if (map2Save != null && map2Save.isEmpty() == false)
				dbo.put("any", map2Save);

		}

		// Extension
		if (aggregationEventType.getExtension() != null) {
			AggregationEventExtensionType aee = aggregationEventType.getExtension();
			JsonObject extension = getAggregationEventExtensionObject(aee);
			dbo.put("extension", extension);

		}

		// Event ID
		if (aggregationEventType.getBaseExtension() != null) {
			if (aggregationEventType.getBaseExtension().getEventID() != null) {
				dbo.put("eventID", aggregationEventType.getBaseExtension().getEventID());
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
