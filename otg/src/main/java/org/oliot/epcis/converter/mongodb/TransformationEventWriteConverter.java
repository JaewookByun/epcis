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
import org.oliot.epcis.configuration.Configuration;
import org.oliot.khronos.persistent.ChronoGraph;
import org.oliot.khronos.persistent.ChronoVertex;
import org.oliot.model.epcis.BusinessLocationType;
import org.oliot.model.epcis.BusinessTransactionListType;
import org.oliot.model.epcis.BusinessTransactionType;
import org.oliot.model.epcis.DestinationListType;
import org.oliot.model.epcis.EPC;
import org.oliot.model.epcis.EPCISEventExtensionType;
import org.oliot.model.epcis.EPCListType;
import org.oliot.model.epcis.ErrorDeclarationType;
import org.oliot.model.epcis.ILMDType;
import org.oliot.model.epcis.QuantityElementType;
import org.oliot.model.epcis.QuantityListType;
import org.oliot.model.epcis.ReadPointType;
import org.oliot.model.epcis.SourceDestType;
import org.oliot.model.epcis.SourceListType;
import org.oliot.model.epcis.TransformationEventExtensionType;
import org.oliot.model.epcis.TransformationEventType;

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

public class TransformationEventWriteConverter {

	public BsonDocument convert(TransformationEventType transformationEventType, Integer gcpLength) {

		BsonDocument dbo = new BsonDocument();

		dbo.put("eventType", new BsonString("TransformationEvent"));
		// Event Time
		Long eventTime = null;
		if (transformationEventType.getEventTime() != null) {
			eventTime = transformationEventType.getEventTime().toGregorianCalendar().getTimeInMillis();
			dbo.put("eventTime", new BsonDateTime(eventTime));
		} else {
			eventTime = System.currentTimeMillis();
			dbo.put("eventTime", new BsonDateTime(eventTime));
		}
		// Event Time Zone
		if (transformationEventType.getEventTimeZoneOffset() != null)
			dbo.put("eventTimeZoneOffset", new BsonString(transformationEventType.getEventTimeZoneOffset()));
		// Record Time : according to M5
		GregorianCalendar recordTime = new GregorianCalendar();
		long recordTimeMilis = recordTime.getTimeInMillis();
		dbo.put("recordTime", new BsonDateTime(recordTimeMilis));

		// Input EPCList
		Set<String> inputSet = null;
		if (transformationEventType.getInputEPCList() != null) {
			EPCListType epcs = transformationEventType.getInputEPCList();
			List<EPC> epcList = epcs.getEpc();
			inputSet = epcList.parallelStream().map(epc -> epc.getValue()).collect(Collectors.toSet());
			List<BsonDocument> bEPC = inputSet.parallelStream()
					.map(sepc -> new BsonDocument("epc", new BsonString(sepc))).collect(Collectors.toList());
			BsonArray epcDBList = new BsonArray(bEPC);
			dbo.put("inputEPCList", epcDBList);
		}
		// Output EPCList
		Set<String> outputSet = null;
		List<EPC> outputList = null;
		if (transformationEventType.getOutputEPCList() != null) {
			EPCListType epcs = transformationEventType.getOutputEPCList();
			outputList = epcs.getEpc();
			outputSet = outputList.parallelStream().map(epc -> epc.getValue()).collect(Collectors.toSet());
			List<BsonDocument> bEPC = outputSet.parallelStream()
					.map(sepc -> new BsonDocument("epc", new BsonString(sepc))).collect(Collectors.toList());
			BsonArray epcDBList = new BsonArray(bEPC);
			dbo.put("outputEPCList", epcDBList);
		}
		// TransformationID
		if (transformationEventType.getTransformationID() != null) {
			dbo.put("transformationID", new BsonString(transformationEventType.getTransformationID()));
		}
		// BizStep
		if (transformationEventType.getBizStep() != null)
			dbo.put("bizStep", new BsonString(transformationEventType.getBizStep()));
		// Disposition
		if (transformationEventType.getDisposition() != null)
			dbo.put("disposition", new BsonString(transformationEventType.getDisposition()));
		// ReadPoint
		String readPoint = null;
		if (transformationEventType.getReadPoint() != null) {
			ReadPointType readPointType = transformationEventType.getReadPoint();
			readPoint = readPointType.getId();
			dbo.put("readPoint", new BsonDocument("id", new BsonString(readPoint)));
		}
		// BizLocation
		String bizLocation = null;
		if (transformationEventType.getBizLocation() != null) {
			BusinessLocationType bizLocationType = transformationEventType.getBizLocation();
			bizLocation = bizLocationType.getId();
			dbo.put("bizLocation", new BsonDocument("id", new BsonString(bizLocation)));
		}
		// BizTransaction
		if (transformationEventType.getBizTransactionList() != null) {
			BusinessTransactionListType bizListType = transformationEventType.getBizTransactionList();
			List<BusinessTransactionType> bizList = bizListType.getBizTransaction();
			BsonArray bizTranList = getBizTransactionObjectList(bizList);
			dbo.put("bizTransactionList", bizTranList);
		}
		// Input Quantity List
		BsonArray inputClassSet = null;
		if (transformationEventType.getInputQuantityList() != null) {
			QuantityListType qetl = transformationEventType.getInputQuantityList();
			List<QuantityElementType> qetList = qetl.getQuantityElement();
			inputClassSet = getQuantityObjectList(qetList, gcpLength);
			dbo.put("inputQuantityList", inputClassSet);
		}
		// Output Quantity List
		BsonArray outputClassSet = null;
		if (transformationEventType.getOutputQuantityList() != null) {
			QuantityListType qetl = transformationEventType.getOutputQuantityList();
			List<QuantityElementType> qetList = qetl.getQuantityElement();
			outputClassSet = getQuantityObjectList(qetList, gcpLength);
			dbo.put("outputQuantityList", outputClassSet);
		}
		// Source List
		if (transformationEventType.getSourceList() != null) {
			SourceListType sdtl = transformationEventType.getSourceList();
			List<SourceDestType> sdtList = sdtl.getSource();
			BsonArray dbList = getSourceDestObjectList(sdtList, gcpLength);
			dbo.put("sourceList", dbList);
		}
		// Dest List
		if (transformationEventType.getDestinationList() != null) {
			DestinationListType sdtl = transformationEventType.getDestinationList();
			List<SourceDestType> sdtList = sdtl.getDestination();
			BsonArray dbList = getSourceDestObjectList(sdtList, gcpLength);
			dbo.put("destinationList", dbList);
		}
		// ILMD
		if (transformationEventType.getIlmd() != null) {
			ILMDType ilmd = transformationEventType.getIlmd();

			if (ilmd.getAny() != null) {
				BsonDocument map2Save = getAnyMap(ilmd.getAny());
				if (map2Save != null && map2Save.isEmpty() == false) {
					dbo.put("ilmd", new BsonDocument("any", map2Save));
				}
				if (outputList != null) {
					MasterDataWriteConverter mdConverter = new MasterDataWriteConverter();
					mdConverter.capture(outputList, map2Save);
				}
			}
		}

		// Vendor Extension
		if (transformationEventType.getAny() != null) {
			List<Object> objList = transformationEventType.getAny();
			BsonDocument map2Save = getAnyMap(objList);
			if (map2Save != null && map2Save.isEmpty() == false)
				dbo.put("any", map2Save);

		}

		// Extension
		BsonArray sourceList = null;
		BsonArray destinationList = null;
		if (transformationEventType.getExtension() != null) {
			TransformationEventExtensionType oee = transformationEventType.getExtension();
			BsonDocument extension = getTransformationEventExtensionObject(oee);
			if (extension.containsKey("sourceList"))
				sourceList = extension.getArray("sourceList");
			if (extension.containsKey("destinationList"))
				destinationList = extension.getArray("destinationList");
			dbo.put("extension", extension);
		}

		// Event ID
		if (transformationEventType.getBaseExtension() != null) {
			if (transformationEventType.getBaseExtension().getEventID() != null) {
				dbo.put("eventID", new BsonString(transformationEventType.getBaseExtension().getEventID()));
			}
		}

		// Error Declaration
		// If declared, it notes that the event is erroneous
		if (transformationEventType.getBaseExtension() != null) {
			EPCISEventExtensionType eeet = transformationEventType.getBaseExtension();
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
		capture(dataID, eventTime, inputSet, outputSet, inputClassSet, outputClassSet, readPoint, bizLocation,
				sourceList, destinationList);

		return dbo;
	}

	public void capture(BsonObjectId dataID, Long eventTime, Set<String> inputSet, Set<String> outputSet,
			BsonArray inputClassSet, BsonArray outputClassSet, String readPoint, String bizLocation,
			BsonArray sourceList, BsonArray destinationList) {

		ChronoGraph pg = Configuration.persistentGraph;

		if (inputSet != null)
			inputSet.stream().forEach(input -> {
				MongoWriterUtil.addBasicTimestampProperties(pg, eventTime, input, readPoint, bizLocation, sourceList,
						destinationList);

				if (outputSet != null)
					outputSet.stream().forEach(output -> {
						pg.addTimestampEdgeProperties(input, output, "transformsTo", eventTime,
								new BsonDocument("data", dataID));
					});

				if (outputClassSet != null)
					outputClassSet.stream().forEach(classElem -> {
						String epcClass = classElem.asDocument().getString("epcClass").getValue();
						pg.addTimestampEdgeProperties(input, epcClass, "transformsTo", eventTime,
								new BsonDocument("data", dataID));
					});

			});

		if (inputClassSet != null)
			inputClassSet.stream().forEach(inputClassElem -> {
				MongoWriterUtil.addBasicTimestampProperties(pg, eventTime, inputClassElem, readPoint, bizLocation,
						sourceList, destinationList);

				String inputClassID = inputClassElem.asDocument().getString("epcClass").getValue();
				if (outputSet != null)
					outputSet.stream().forEach(output -> {
						pg.addTimestampEdgeProperties(inputClassID, output, "transformsTo", eventTime,
								new BsonDocument("data", dataID));
					});

				if (outputClassSet != null)
					outputClassSet.stream().forEach(classElem -> {
						String epcClass = classElem.asDocument().getString("epcClass").getValue();
						pg.addTimestampEdgeProperties(inputClassID, epcClass, "transformsTo", eventTime,
								new BsonDocument("data", dataID));
					});
			});

		if (outputSet != null)
			outputSet.stream().forEach(output -> {
				MongoWriterUtil.addBasicTimestampProperties(pg, eventTime, output, readPoint, bizLocation, sourceList,
						destinationList);
			});

		if (outputClassSet != null)
			outputClassSet.stream().forEach(outputClassElem -> {
				MongoWriterUtil.addBasicTimestampProperties(pg, eventTime, outputClassElem, readPoint, bizLocation,
						sourceList, destinationList);
			});

		return;
	}
}
