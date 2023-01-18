package org.oliot.epcis.index;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.bson.Document;

/**
 * Copyright (C) 2020-2022. (Jaewook Byun) all rights reserved.
 * <p>
 * Oliot EPCIS X is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * Among various modules, epcis-query-soap acts as a server to receive queries
 * to provide filtered, sorted, limited events or masterdata of interest inside
 * EPCIS repository.
 * <p>
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr
 *         <p>
 *         Associate Director, Auto-ID Labs, KAIST, bjw0829@kaist.ac.kr
 */
public class IndexManager {

	public static Document toMongoIndex(JsonArray indexQuery) throws IllegalArgumentException {

		Document mongoIndex = new Document();
		for (int i = 0; i < indexQuery.size(); i++) {
			JsonObject idx = indexQuery.getJsonObject(i);
			String name = idx.getString("field");
			int type = 1;
			try {
				if (Integer.parseInt(idx.getValue("type").toString()) == -1)
					type = -1;
			} catch (Exception e) {
			}

			if (name.equals("eventType")) {
				// type
				mongoIndex.put("eventType", type);
			} else if (name.equals("eventTime")) {
				// eventTime
				mongoIndex.put("eventTime", type);
			} else if (name.equals("recordTime")) {
				// recordTime
				mongoIndex.put("recordTime", type);
			} else if (name.equals("errorDeclarationTime")) {
				// errorDeclaration.declarationTime
				mongoIndex.put("errorDeclaration.declarationTime", type);
			} else if (name.equals("sensorMetadataTime")) {
				// sensorElementList.sensorMetadata.time
				mongoIndex.put("sensorElementList.sensorMetadata.time", type);
			} else if (name.equals("sensorMetadataStartTime")) {
				// sensorElementList.sensorMetadata.startTime
				mongoIndex.put("sensorElementList.sensorMetadata.startTime", type);
			} else if (name.equals("sensorMetadataEndTime")) {
				// sensorElementList.sensorMetadata.endTime
				mongoIndex.put("sensorElementList.sensorMetadata.endTime", type);
			} else if (name.equals("action")) {
				// action
				mongoIndex.put("action", type);
			} else if (name.equals("bizStep")) {
				// bizStep
				mongoIndex.put("bizStep", type);
			} else if (name.equals("disposition")) {
				// disposition
				mongoIndex.put("disposition", type);
			} else if (name.equals("setPersistentDisposition")) {
				// persistentDisposition.set
				mongoIndex.put("persistentDisposition.set", type);
			} else if (name.equals("unsetPersistentDisposition")) {
				// persistentDisposition.unset
				mongoIndex.put("persistentDisposition.unset", type);
			} else if (name.equals("transformationID")) {
				// transformationID
				mongoIndex.put("transformationID", type);
			} else if (name.equals("sensorReportType")) {
				// sensorElementList.sensorReport.type
				mongoIndex.put("sensorElementList.sensorReport.type", type);
			} else if (name.equals("sensorReportDeviceID")) {
				// sensorElementList.sensorReport.deviceID
				mongoIndex.put("sensorElementList.sensorReport.deviceID", type);
			} else if (name.equals("sensorReportDeviceMetaData")) {
				// sensorElementList.sensorReport.deviceMetaData
				mongoIndex.put("sensorElementList.sensorReport.deviceMetaData", type);
			} else if (name.equals("sensorReportRawData")) {
				// sensorElementList.sensorReport.rawData
				mongoIndex.put("sensorElementList.sensorReport.rawData", type);
			} else if (name.equals("sensorReportDataProcessingMethod")) {
				// sensorElementList.sensorReport.dataProcessingMethod
				mongoIndex.put("sensorElementList.sensorReport.dataProcessingMethod", type);
			} else if (name.equals("sensorReportMicroorganism")) {
				// sensorElementList.sensorReport.microorganism
				mongoIndex.put("sensorElementList.sensorReport.microorganism", type);
			} else if (name.equals("sensorReportChemicalSubstance")) {
				// sensorElementList.sensorReport.chemicalSubstance
				mongoIndex.put("sensorElementList.sensorReport.chemicalSubstance", type);
			} else if (name.equals("sensorMetadataBizRules")) {
				// sensorElementList.sensorMetadata.bizRules
				mongoIndex.put("sensorElementList.sensorMetadata.bizRules", type);
			} else if (name.equals("sensorReportStringValue")) {
				// sensorElementList.sensorReport.stringValue
				mongoIndex.put("sensorElementList.sensorReport.stringValue", type);
			} else if (name.equals("sensorReportBooleanValue")) {
				// sensorElementList.sensorReport.booleanValue
				mongoIndex.put("sensorElementList.sensorReport.booleanValue", type);
			} else if (name.equals("sensorReportHexBinaryValue")) {
				// sensorElementList.sensorReport.hexBinaryValue
				mongoIndex.put("sensorElementList.sensorReport.hexBinaryValue", type);
			} else if (name.equals("sensorReportMaxValue")) {
				// sensorElementList.sensorReport.maxValue
				mongoIndex.put("sensorElementList.sensorReport.maxValue", type);
			} else if (name.equals("sensorReportMinValue")) {
				// sensorElementList.sensorReport.minValue
				mongoIndex.put("sensorElementList.sensorReport.minValue", type);
			} else if (name.equals("sensorReportMeanValue")) {
				// sensorElementList.sensorReport.meanValue
				mongoIndex.put("sensorElementList.sensorReport.meanValue", type);
			} else if (name.equals("sensorReportSDev")) {
				// sensorElementList.sensorReport.sDev
				mongoIndex.put("sensorElementList.sensorReport.sDev", type);
			} else if (name.equals("sensorReportPercValue")) {
				// sensorElementList.sensorReport.percValue
				mongoIndex.put("sensorElementList.sensorReport.percValue", type);
			} else if (name.equals("sensorReportPercRank")) {
				// sensorElementList.sensorReport.percRank
				mongoIndex.put("sensorElementList.sensorReport.percRank", type);
			} else if (name.equals("epc")) {
				// epcList
				mongoIndex.put("epcList", type);
			} else if (name.equals("parentID")) {
				// parentID
				mongoIndex.put("parentID", type);
			} else if (name.equals("inputEPCList")) {
				// inputEPCList
				mongoIndex.put("inputEPCList", type);
			} else if (name.equals("outputEPCList")) {
				// outputEPCList
				mongoIndex.put("outputEPCList", type);
			} else if (name.equals("epcClass")) {
				// quantityList.epcClass
				mongoIndex.put("quantityList.epcClass", type);
			} else if (name.equals("inputEPCClass")) {
				// inputQuantityList.epcClass
				mongoIndex.put("inputQuantityList.epcClass", type);
			} else if (name.equals("outputEPCClass")) {
				// outputQuantityList.epcClass
				mongoIndex.put("outputQuantityList.epcClass", type);
			} else if (name.equals("readPoint")) {
				// readPoint
				mongoIndex.put("readPoint", type);
			} else if (name.equals("bizLocation")) {
				// bizLocation
				mongoIndex.put("bizLocation", type);
			} else if (name.equals("errorReason")) {
				// errorDeclaration.reason
				mongoIndex.put("errorDeclaration.reason", type);
			} else if (name.equals("correctiveEventID")) {
				// errorDeclaration.correctiveEventIDs
				mongoIndex.put("errorDeclaration.correctiveEventIDs", type);
			} else if (name.equals("errorDeclaration")) {
				// errorDeclaration
				mongoIndex.put("errorDeclaration", type);
			} else if (name.equals("sensorElementList")) {
				// sensorElementList
				mongoIndex.put("sensorElementList", type);
			} else if (name.startsWith("sensorReportValue")) {
				// sensorElementList.sensorReport.rValue
				// sensorElementList.sensorReport.rType
				mongoIndex.put("sensorElementList.sensorReport.rValue", type);
				mongoIndex.put("sensorElementList.sensorReport.rType", type);
			} else if (name.startsWith("bizTransaction")) {
				// bizTransactionList.
				if (mongoIndex.size() != 0) {
					throw new IllegalArgumentException(name + " should not be compound index");
				} else {
					return new Document("bizTransactionList.$**", type);
				}
			} else if (name.startsWith("source")) {
				// sourceList.
				if (mongoIndex.size() != 0) {
					throw new IllegalArgumentException(name + " should not be compound index");
				} else {
					return new Document("sourceList.$**", type);
				}
			} else if (name.startsWith("destination")) {
				// destinationList.
				if (mongoIndex.size() != 0) {
					throw new IllegalArgumentException(name + " should not be compound index");
				} else {
					return new Document("destinationList.$**", type);
				}
			} else if (name.startsWith("extension")) {
				// extension.
				if (mongoIndex.size() != 0) {
					throw new IllegalArgumentException(name + " should not be compound index");
				} else {
					return new Document("extension.$**", type);
				}
			} else if (name.startsWith("innerExtension")) {
				// extf.
				if (mongoIndex.size() != 0) {
					throw new IllegalArgumentException(name + " should not be compound index");
				} else {
					return new Document("extf.$**", type);
				}
			} else if (name.startsWith("ilmd")) {
				// ilmd.
				if (mongoIndex.size() != 0) {
					throw new IllegalArgumentException(name + " should not be compound index");
				} else {
					return new Document("ilmd.$**", type);
				}
			} else if (name.startsWith("innerIlmd")) {
				// ilmdf.
				if (mongoIndex.size() != 0) {
					throw new IllegalArgumentException(name + " should not be compound index");
				} else {
					return new Document("ilmdf.$**", type);
				}
			} else if (name.startsWith("errorDeclarationExtension")) {
				// errorDeclaration.extension.
				if (mongoIndex.size() != 0) {
					throw new IllegalArgumentException(name + " should not be compound index");
				} else {
					return new Document("ilmdf.$**", type);
				}
			} else if (name.startsWith("innerErrorDeclarationExtension")) {
				// errf.
				if (mongoIndex.size() != 0) {
					throw new IllegalArgumentException(name + " should not be compound index");
				} else {
					return new Document("errf.$**", type);
				}
			} else if (name.startsWith("sensorExtension")) {
				// sensorElementList.extension.
				if (mongoIndex.size() != 0) {
					throw new IllegalArgumentException(name + " should not be compound index");
				} else {
					return new Document("sensorElementList.extension.$**", type);
				}
			} else if (name.startsWith("innerSensorExtension")) {
				// sensorElementList.sef.
				if (mongoIndex.size() != 0) {
					throw new IllegalArgumentException(name + " should not be compound index");
				} else {
					return new Document("sensorElementList.sef.$**", type);
				}
			} else if (name.startsWith("sensorMetadataOtherAttributes")) {
				// sensorElementList.sensorMetadata.otherAttributes.
				if (mongoIndex.size() != 0) {
					throw new IllegalArgumentException(name + " should not be compound index");
				} else {
					return new Document("sensorElementList.sensorMetadata.otherAttributes.$**", type);
				}
			} else if (name.startsWith("sensorReportOtherAttributes")) {
				// sensorElementList.sensorReport.otherAttributes.
				if (mongoIndex.size() != 0) {
					throw new IllegalArgumentException(name + " should not be compound index");
				} else {
					return new Document("sensorElementList.sensorReport.otherAttributes..$**", type);
				}
			} else {
				 throw new IllegalArgumentException(name + " is not supported for index field");
			}
		}
		return mongoIndex;
	}
}