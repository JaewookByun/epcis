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
		Long eventTime = null;
		if (transactionEventType.getEventTime() != null) {
			eventTime = transactionEventType.getEventTime().toGregorianCalendar().getTimeInMillis();
			dbo.put("eventTime", new BsonDateTime(eventTime));
		} else {
			eventTime = System.currentTimeMillis();
			dbo.put("eventTime", new BsonDateTime(eventTime));
		}
		// Event Time Zone
		if (transactionEventType.getEventTimeZoneOffset() != null)
			dbo.put("eventTimeZoneOffset", new BsonString(transactionEventType.getEventTimeZoneOffset()));
		// Record Time : according to M5
		GregorianCalendar recordTime = new GregorianCalendar();
		long recordTimeMilis = recordTime.getTimeInMillis();
		dbo.put("recordTime", new BsonDateTime(recordTimeMilis));
		Set<String> epcList = null;
		// EPC List
		List<EPC> epcListObject = null;
		if (transactionEventType.getEpcList() != null) {
			EPCListType epcs = transactionEventType.getEpcList();
			epcListObject = epcs.getEpc();
			epcList = epcListObject.parallelStream().map(epc -> epc.getValue()).collect(Collectors.toSet());
			List<BsonDocument> bEPC = epcList.parallelStream()
					.map(sepc -> new BsonDocument("epc", new BsonString(sepc))).collect(Collectors.toList());
			BsonArray epcDBList = new BsonArray(bEPC);
			dbo.put("epcList", epcDBList);
		}

		// Parent ID
		if (transactionEventType.getParentID() != null) {
			String parentID = transactionEventType.getParentID();
			dbo.put("parentID", new BsonString(parentID));
			epcList.add(parentID);
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
		// ReadPoint
		String readPoint = null;
		if (transactionEventType.getReadPoint() != null) {
			ReadPointType readPointType = transactionEventType.getReadPoint();
			readPoint = readPointType.getId();
			dbo.put("readPoint", new BsonDocument("id", new BsonString(readPoint)));
		}
		// BizLocation
		String bizLocation = null;
		if (transactionEventType.getBizLocation() != null) {
			BusinessLocationType bizLocationType = transactionEventType.getBizLocation();
			bizLocation = bizLocationType.getId();
			dbo.put("bizLocation", new BsonDocument("id", new BsonString(bizLocation)));
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
		BsonArray epcQuantities = null;
		BsonArray sourceList = null;
		BsonArray destinationList = null;
		if (transactionEventType.getExtension() != null) {
			TransactionEventExtensionType oee = transactionEventType.getExtension();
			BsonDocument extension = getTransactionEventExtensionObject(oee, gcpLength);
			if (extension.containsKey("quantityList"))
				epcQuantities = extension.getArray("quantityList");
			if (extension.containsKey("sourceList"))
				sourceList = extension.getArray("sourceList");
			if (extension.containsKey("destinationList"))
				destinationList = extension.getArray("destinationList");
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

		BsonObjectId dataID = new BsonObjectId();
		dbo.put("_id", dataID);
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
	}
}
