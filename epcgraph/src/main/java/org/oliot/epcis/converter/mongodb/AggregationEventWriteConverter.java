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
		Long eventTime = null;
		if (aggregationEventType.getEventTime() != null) {
			eventTime = aggregationEventType.getEventTime().toGregorianCalendar().getTimeInMillis();
			dbo.put("eventTime", new BsonDateTime(eventTime));
		} else {
			eventTime = System.currentTimeMillis();
			dbo.put("eventTime", new BsonDateTime(eventTime));
		}

		// Event Time Zone
		if (aggregationEventType.getEventTimeZoneOffset() != null)
			dbo.put("eventTimeZoneOffset", new BsonString(aggregationEventType.getEventTimeZoneOffset()));
		// Record Time : according to M5
		GregorianCalendar recordTime = new GregorianCalendar();
		long recordTimeMilis = recordTime.getTimeInMillis();
		dbo.put("recordTime", new BsonDateTime(recordTimeMilis));

		// Parent ID
		String parentID = null;
		if (aggregationEventType.getParentID() != null) {
			parentID = aggregationEventType.getParentID();
			dbo.put("parentID", new BsonString(parentID));
		}
		// Child EPCs
		Set<String> childEPCs = null;
		if (aggregationEventType.getChildEPCs() != null) {
			EPCListType epcs = aggregationEventType.getChildEPCs();
			List<EPC> epcList = epcs.getEpc();
			childEPCs = epcList.parallelStream().map(epc -> epc.getValue()).collect(Collectors.toSet());
			List<BsonDocument> bEPC = childEPCs.parallelStream()
					.map(sepc -> new BsonDocument("epc", new BsonString(sepc))).collect(Collectors.toList());
			BsonArray epcDBList = new BsonArray(bEPC);
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
		String readPoint = null;
		if (aggregationEventType.getReadPoint() != null) {
			ReadPointType readPointType = aggregationEventType.getReadPoint();
			readPoint = readPointType.getId();
			dbo.put("readPoint", new BsonDocument("id", new BsonString(readPoint)));
		}
		// BizLocation
		String bizLocation = null;
		if (aggregationEventType.getBizLocation() != null) {
			BusinessLocationType bizLocationType = aggregationEventType.getBizLocation();
			bizLocation = bizLocationType.getId();
			dbo.put("bizLocation", new BsonDocument("id", new BsonString(bizLocation)));
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
		BsonArray childQuantityList = null;
		BsonArray sourceList = null;
		BsonArray destinationList = null;
		if (aggregationEventType.getExtension() != null) {
			AggregationEventExtensionType aee = aggregationEventType.getExtension();
			BsonDocument extension = getAggregationEventExtensionObject(aee, gcpLength);
			if (extension.containsKey("childQuantityList"))
				childQuantityList = extension.getArray("childQuantityList");
			if (extension.containsKey("sourceList"))
				sourceList = extension.getArray("sourceList");
			if (extension.containsKey("destinationList"))
				destinationList = extension.getArray("destinationList");
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

		BsonObjectId dataID = new BsonObjectId();
		dbo.put("_id", dataID);
		ChronoVertex dataVertex = Configuration.persistentGraphData.getChronoVertex(dataID.toString());
		dataVertex.setProperties(dbo);

		// Build Graph
		capture(dataID, eventTime, parentID, childEPCs, childQuantityList, readPoint, bizLocation, sourceList,
				destinationList);

		return dbo;
	}

	public void capture(BsonObjectId dataID, Long eventTime, String parentID, Set<String> childEPCs,
			BsonArray childQuantities, String readPoint, String bizLocation, BsonArray sourceList,
			BsonArray destinationList) {

		ChronoGraph pg = Configuration.persistentGraph;

		// object = vid
		if (parentID != null) {
			MongoWriterUtil.addBasicTimestampProperties(pg, eventTime, parentID, readPoint, bizLocation, sourceList,
					destinationList);
		}

		childEPCs.stream().forEach(child -> {
			MongoWriterUtil.addBasicTimestampProperties(pg, eventTime, child, readPoint, bizLocation, sourceList,
					destinationList);
		});

		childQuantities.stream().forEach(classElem -> {
			MongoWriterUtil.addBasicTimestampProperties(pg, eventTime, classElem, readPoint, bizLocation, sourceList,
					destinationList);
		});

		if (parentID != null) {
			childEPCs.stream().forEach(child -> {
				pg.addTimestampEdgeProperties(parentID, child, "contains", eventTime, new BsonDocument("data", dataID));
			});
			childQuantities.stream().forEach(classElem -> {
				String epcClass = classElem.asDocument().getString("epcClass").getValue();
				pg.addTimestampEdgeProperties(parentID, epcClass, "contains", eventTime,
						new BsonDocument("data", dataID));
			});
		}

		return;
	}
}
