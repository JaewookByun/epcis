package org.oliot.epcis.converter.mongodb;

import static org.oliot.epcis.converter.mongodb.MongoWriterUtil.*;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bson.BsonArray;
import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonObjectId;
import org.bson.BsonString;
import org.lilliput.chronograph.persistent.ChronoGraph;
import org.lilliput.chronograph.persistent.ChronoVertex;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.model.epcis.BusinessLocationType;
import org.oliot.model.epcis.BusinessTransactionListType;
import org.oliot.model.epcis.BusinessTransactionType;
import org.oliot.model.epcis.EPC;
import org.oliot.model.epcis.EPCISEventExtensionType;
import org.oliot.model.epcis.EPCListType;
import org.oliot.model.epcis.ErrorDeclarationType;
import org.oliot.model.epcis.ObjectEventExtensionType;
import org.oliot.model.epcis.ObjectEventType;
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

public class ObjectEventWriteConverter {

	public BsonDocument convert(ObjectEventType objectEventType, Integer gcpLength) {
		BsonDocument dbo = new BsonDocument();
		dbo.put("eventType", new BsonString("ObjectEvent"));
		// Event Time
		Long eventTime = null;
		if (objectEventType.getEventTime() != null) {
			eventTime = objectEventType.getEventTime().toGregorianCalendar().getTimeInMillis();
			dbo.put("eventTime", new BsonDateTime(eventTime));
		} else {
			eventTime = System.currentTimeMillis();
			dbo.put("eventTime", new BsonDateTime(eventTime));
		}
		// Event Time Zone
		if (objectEventType.getEventTimeZoneOffset() != null)
			dbo.put("eventTimeZoneOffset", new BsonString(objectEventType.getEventTimeZoneOffset()));
		// Record Time : according to M5
		GregorianCalendar recordTime = new GregorianCalendar();
		long recordTimeMilis = recordTime.getTimeInMillis();
		dbo.put("recordTime", new BsonDateTime(recordTimeMilis));
		// EPC List
		Set<String> epcList = null;
		List<EPC> epcsL = null;
		if (objectEventType.getEpcList() != null) {
			EPCListType epcs = objectEventType.getEpcList();
			epcsL = epcs.getEpc();

			epcList = epcsL.parallelStream().map(epc -> epc.getValue()).collect(Collectors.toSet());
			List<BsonString> bEPC = epcList.parallelStream().map(sepc -> new BsonString(sepc))
					.collect(Collectors.toList());
			BsonArray epcDBList = new BsonArray(bEPC);
			dbo.put("epcList", epcDBList);
		}
		// Action
		if (objectEventType.getAction() != null)
			dbo.put("action", new BsonString(objectEventType.getAction().name()));
		// Biz Step
		if (objectEventType.getBizStep() != null)
			dbo.put("bizStep", new BsonString(objectEventType.getBizStep()));
		// Disposition
		if (objectEventType.getDisposition() != null)
			dbo.put("disposition", new BsonString(objectEventType.getDisposition()));
		// ReadPoint
		String readPoint = null;
		if (objectEventType.getReadPoint() != null) {
			ReadPointType readPointType = objectEventType.getReadPoint();
			readPoint = readPointType.getId();
			dbo.put("readPoint", new BsonString(readPoint));
		}
		// BizLocation
		String bizLocation = null;
		if (objectEventType.getBizLocation() != null) {
			BusinessLocationType bizLocationType = objectEventType.getBizLocation();
			bizLocation = bizLocationType.getId();
			dbo.put("bizLocation", new BsonString(bizLocation));
		}
		// BizTransaction
		if (objectEventType.getBizTransactionList() != null) {
			BusinessTransactionListType bizListType = objectEventType.getBizTransactionList();
			List<BusinessTransactionType> bizList = bizListType.getBizTransaction();
			BsonArray bizTranList = getBizTransactionObjectList(bizList);
			dbo.put("bizTransactionList", bizTranList);
		}

		// ILMD: moves to Extension
		/*
		 * if (objectEventType.getIlmd() != null) { ILMDType ilmd =
		 * objectEventType.getIlmd(); if (ilmd.getExtension() != null) {
		 * ILMDExtensionType ilmdExtension = ilmd.getExtension(); BsonDocument map2Save
		 * = getILMDExtensionMap(ilmdExtension); if (map2Save != null) dbo.put("ilmd",
		 * map2Save); if (epcList != null) { MasterDataWriteConverter mdConverter = new
		 * MasterDataWriteConverter(); mdConverter.capture(epcList, map2Save); } } }
		 */
		// Vendor Extension
		if (objectEventType.getAny() != null) {
			List<Object> objList = objectEventType.getAny();
			BsonDocument map2Save = getAnyMap(objList);
			if (map2Save != null && map2Save.isEmpty() == false)
				dbo.put("any", map2Save);

		}

		// Extension
		BsonArray epcQuantities = null;
		BsonArray sourceList = null;
		BsonArray destinationList = null;
		if (objectEventType.getExtension() != null) {
			ObjectEventExtensionType oee = objectEventType.getExtension();
			BsonDocument extension = getObjectEventExtensionObject(oee, gcpLength, epcsL);
			epcQuantities = extension.getArray("quantityList");
			sourceList = extension.getArray("sourceList");
			destinationList = extension.getArray("destinationList");
			dbo.put("extension", extension);
		}

		// Event ID
		if (objectEventType.getBaseExtension() != null) {
			if (objectEventType.getBaseExtension().getEventID() != null) {
				dbo.put("eventID", new BsonString(objectEventType.getBaseExtension().getEventID()));
			}
		}

		// Error Declaration
		// If declared, it notes that the event is erroneous
		if (objectEventType.getBaseExtension() != null) {
			EPCISEventExtensionType eeet = objectEventType.getBaseExtension();
			ErrorDeclarationType edt = eeet.getErrorDeclaration();
			if (edt != null) {
				if (edt.getDeclarationTime() != null) {
					dbo.put("errorDeclaration", MongoWriterUtil.getErrorDeclaration(edt));
				}
			}
		}

		BsonObjectId dataID = new BsonObjectId();
		ChronoVertex dataVertex = Configuration.persistentGraphData.getChronoVertex(dataID.toString());
		dataVertex.setProperties(dbo);

		// Build Graph
		capture(dataID, eventTime, epcList, epcQuantities, readPoint, bizLocation, sourceList, destinationList);

		return dbo;
	}

	public void capture(BsonObjectId dataID, Long eventTime, Set<String> epcList, BsonArray epcQuantities,
			String readPoint, String bizLocation, BsonArray sourceList, BsonArray destinationList) {

		ChronoGraph pg = Configuration.persistentGraph;

		if (epcList != null && !epcList.isEmpty()) {
			epcList.stream().forEach(object -> {
				MongoWriterUtil.addBasicTimestampProperties(pg, eventTime, object, readPoint, bizLocation, sourceList,
						destinationList);
				pg.addTimestampVertexProperty(object, eventTime, "data", dataID);
			});
		}

		if (epcQuantities != null && !epcQuantities.isEmpty()) {
			epcQuantities.stream().forEach(classElem -> {
				MongoWriterUtil.addBasicTimestampProperties(pg, eventTime, classElem, readPoint, bizLocation,
						sourceList, destinationList);
				pg.addTimestampVertexProperty(classElem.asDocument().getString("epcClass").getValue(), eventTime,
						"data", dataID);
			});
		}
		return;
	}

}
