package org.oliot.epcis.converter.mongodb;

import static org.oliot.epcis.converter.mongodb.MongoWriterUtil.*;

import java.util.GregorianCalendar;
import java.util.List;

import org.oliot.model.epcis.BusinessLocationType;
import org.oliot.model.epcis.BusinessTransactionListType;
import org.oliot.model.epcis.BusinessTransactionType;
import org.oliot.model.epcis.DestinationListType;
import org.oliot.model.epcis.EPC;
import org.oliot.model.epcis.EPCISEventExtensionType;
import org.oliot.model.epcis.EPCListType;
import org.oliot.model.epcis.ErrorDeclarationType;
import org.oliot.model.epcis.QuantityElementType;
import org.oliot.model.epcis.QuantityListType;
import org.oliot.model.epcis.ReadPointType;
import org.oliot.model.epcis.SourceDestType;
import org.oliot.model.epcis.SourceListType;
import org.oliot.model.epcis.TransformationEventExtensionType;
import org.oliot.model.epcis.TransformationEventType;

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

public class TransformationEventWriteConverter {

	public JsonObject convert(TransformationEventType transformationEventType) {

		JsonObject dbo = new JsonObject();

		dbo.put("eventType", "TransformationEvent");
		// Event Time
		if (transformationEventType.getEventTime() != null)
			dbo.put("eventTime", transformationEventType.getEventTime().toGregorianCalendar().getTimeInMillis());
		// Event Time Zone
		if (transformationEventType.getEventTimeZoneOffset() != null)
			dbo.put("eventTimeZoneOffset", transformationEventType.getEventTimeZoneOffset());
		// Record Time : according to M5
		GregorianCalendar recordTime = new GregorianCalendar();
		long recordTimeMilis = recordTime.getTimeInMillis();
		dbo.put("recordTime", recordTimeMilis);
		// Input EPCList
		if (transformationEventType.getInputEPCList() != null) {
			EPCListType epcs = transformationEventType.getInputEPCList();
			List<EPC> epcList = epcs.getEpc();
			JsonArray epcDBList = new JsonArray();

			for (int i = 0; i < epcList.size(); i++) {
				JsonObject epcDB = new JsonObject();
				epcDB.put("epc", epcList.get(i).getValue());
				epcDBList.add(epcDB);
			}
			dbo.put("inputEPCList", epcDBList);
		}
		// Output EPCList
		List<EPC> outputList = null;
		if (transformationEventType.getOutputEPCList() != null) {
			EPCListType epcs = transformationEventType.getOutputEPCList();
			outputList = epcs.getEpc();
			JsonArray epcDBList = new JsonArray();

			for (int i = 0; i < outputList.size(); i++) {
				JsonObject epcDB = new JsonObject();
				epcDB.put("epc", outputList.get(i).getValue());
				epcDBList.add(epcDB);
			}
			dbo.put("outputEPCList", epcDBList);
		}
		// TransformationID
		if (transformationEventType.getTransformationID() != null) {
			dbo.put("transformationID", transformationEventType.getTransformationID());
		}
		// BizStep
		if (transformationEventType.getBizStep() != null)
			dbo.put("bizStep", transformationEventType.getBizStep());
		// Disposition
		if (transformationEventType.getDisposition() != null)
			dbo.put("disposition", transformationEventType.getDisposition());
		// ReadPoint
		if (transformationEventType.getReadPoint() != null) {
			ReadPointType readPointType = transformationEventType.getReadPoint();
			JsonObject readPoint = getReadPointObject(readPointType);
			dbo.put("readPoint", readPoint);
		}
		// BizLocation
		if (transformationEventType.getBizLocation() != null) {
			BusinessLocationType bizLocationType = transformationEventType.getBizLocation();
			JsonObject bizLocation = getBizLocationObject(bizLocationType);
			dbo.put("bizLocation", bizLocation);
		}
		// BizTransaction
		if (transformationEventType.getBizTransactionList() != null) {
			BusinessTransactionListType bizListType = transformationEventType.getBizTransactionList();
			List<BusinessTransactionType> bizList = bizListType.getBizTransaction();
			JsonArray bizTranList = getBizTransactionObjectList(bizList);
			dbo.put("bizTransactionList", bizTranList);
		}
		// Input Quantity List
		if (transformationEventType.getInputQuantityList() != null) {
			QuantityListType qetl = transformationEventType.getInputQuantityList();
			List<QuantityElementType> qetList = qetl.getQuantityElement();
			JsonArray quantityList = getQuantityObjectList(qetList);
			dbo.put("inputQuantityList", quantityList);
		}
		// Output Quantity List
		if (transformationEventType.getOutputQuantityList() != null) {
			QuantityListType qetl = transformationEventType.getOutputQuantityList();
			List<QuantityElementType> qetList = qetl.getQuantityElement();
			JsonArray quantityList = getQuantityObjectList(qetList);
			dbo.put("outputQuantityList", quantityList);
		}
		// Source List
		if (transformationEventType.getSourceList() != null) {
			SourceListType sdtl = transformationEventType.getSourceList();
			List<SourceDestType> sdtList = sdtl.getSource();
			JsonArray dbList = getSourceDestObjectList(sdtList);
			dbo.put("sourceList", dbList);
		}
		// Dest List
		if (transformationEventType.getDestinationList() != null) {
			DestinationListType sdtl = transformationEventType.getDestinationList();
			List<SourceDestType> sdtList = sdtl.getDestination();
			JsonArray dbList = getSourceDestObjectList(sdtList);
			dbo.put("destinationList", dbList);
		}
		// ILMD
//		if (transformationEventType.getIlmd() != null) {
//			ILMDType ilmd = transformationEventType.getIlmd();
//
//			if (ilmd.getAny() != null) {
//				BsonDocument map2Save = getAnyMap(ilmd.getAny());
//				if (map2Save != null && map2Save.isEmpty() == false) {
//					dbo.put("ilmd", new BsonDocument("any", map2Save));
//				}
//				if (outputList != null) {
//					MasterDataWriteConverter mdConverter = new MasterDataWriteConverter();
//					mdConverter.capture(outputList, map2Save);
//				}
//			}
//		}

		// Vendor Extension
		if (transformationEventType.getAny() != null) {
			List<Object> objList = transformationEventType.getAny();
			JsonObject map2Save = getAnyMap(objList);
			if (map2Save != null && map2Save.isEmpty() == false)
				dbo.put("any", map2Save);

		}

		// Extension
		if (transformationEventType.getExtension() != null) {
			TransformationEventExtensionType oee = transformationEventType.getExtension();
			JsonObject extension = getTransformationEventExtensionObject(oee);
			dbo.put("extension", extension);
		}

		// Event ID
		if (transformationEventType.getBaseExtension() != null) {
			if (transformationEventType.getBaseExtension().getEventID() != null) {
				dbo.put("eventID", transformationEventType.getBaseExtension().getEventID());
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

		return dbo;
	}
}
