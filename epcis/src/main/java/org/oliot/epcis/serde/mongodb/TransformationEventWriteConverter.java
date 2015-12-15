package org.oliot.epcis.serde.mongodb;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.oliot.epcis.configuration.Configuration;
import org.oliot.epcis.service.registry.DiscoveryServiceAgent;
import org.oliot.model.epcis.BusinessLocationType;
import org.oliot.model.epcis.BusinessTransactionListType;
import org.oliot.model.epcis.BusinessTransactionType;
import org.oliot.model.epcis.DestinationListType;
import org.oliot.model.epcis.EPC;
import org.oliot.model.epcis.EPCISEventExtensionType;
import org.oliot.model.epcis.EPCListType;
import org.oliot.model.epcis.ILMDExtensionType;
import org.oliot.model.epcis.ILMDType;
import org.oliot.model.epcis.QuantityElementType;
import org.oliot.model.epcis.QuantityListType;
import org.oliot.model.epcis.ReadPointType;
import org.oliot.model.epcis.SourceDestType;
import org.oliot.model.epcis.SourceListType;
import org.oliot.model.epcis.TransformationEventExtensionType;
import org.oliot.model.epcis.TransformationEventType;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import static org.oliot.epcis.serde.mongodb.MongoWriterUtil.*;

/**
 * Copyright (C) 2014 Jaewook Jack Byun
 *
 * This project is part of Oliot (oliot.org), pursuing the implementation of
 * Electronic Product Code Information Service(EPCIS) v1.1 specification in
 * EPCglobal.
 * [http://www.gs1.org/gsmp/kc/epcglobal/epcis/epcis_1_1-standard-20140520.pdf]
 * 
 *
 * @author Jaewook Jack Byun, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         bjw0829@kaist.ac.kr, bjw0829@gmail.com
 */

public class TransformationEventWriteConverter {

	public DBObject convert(TransformationEventType transformationEventType, Integer gcpLength) {

		DBObject dbo = new BasicDBObject();
		// Base Extension
		if (transformationEventType.getBaseExtension() != null) {
			EPCISEventExtensionType baseExtensionType = transformationEventType.getBaseExtension();
			DBObject baseExtension = getBaseExtensionObject(baseExtensionType);
			dbo.put("baseExtension", baseExtension);
		}
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
			List<DBObject> epcDBList = new ArrayList<DBObject>();

			for (int i = 0; i < epcList.size(); i++) {
				DBObject epcDB = new BasicDBObject();
				epcDB.put("epc", MongoWriterUtil.getInstanceEPC(epcList.get(i).getValue(),gcpLength));
				epcDBList.add(epcDB);
			}
			dbo.put("inputEPCList", epcDBList);
		}
		// Output EPCList
		List<EPC> outputList = null;
		if (transformationEventType.getOutputEPCList() != null) {
			EPCListType epcs = transformationEventType.getOutputEPCList();
			outputList = epcs.getEpc();
			List<DBObject> epcDBList = new ArrayList<DBObject>();

			for (int i = 0; i < outputList.size(); i++) {
				DBObject epcDB = new BasicDBObject();
				epcDB.put("epc", MongoWriterUtil.getInstanceEPC(outputList.get(i).getValue(),gcpLength));
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
			DBObject readPoint = getReadPointObject(readPointType, gcpLength);
			dbo.put("readPoint", readPoint);
		}
		// BizLocation
		if (transformationEventType.getBizLocation() != null) {
			BusinessLocationType bizLocationType = transformationEventType.getBizLocation();
			DBObject bizLocation = getBizLocationObject(bizLocationType, gcpLength);
			dbo.put("bizLocation", bizLocation);
		}
		// BizTransaction
		if (transformationEventType.getBizTransactionList() != null) {
			BusinessTransactionListType bizListType = transformationEventType.getBizTransactionList();
			List<BusinessTransactionType> bizList = bizListType.getBizTransaction();
			List<DBObject> bizTranList = getBizTransactionObjectList(bizList);
			dbo.put("bizTransactionList", bizTranList);
		}
		// Input Quantity List
		if (transformationEventType.getInputQuantityList() != null) {
			QuantityListType qetl = transformationEventType.getInputQuantityList();
			List<QuantityElementType> qetList = qetl.getQuantityElement();
			List<DBObject> quantityList = getQuantityObjectList(qetList, gcpLength);
			dbo.put("inputQuantityList", quantityList);
		}
		// Output Quantity List
		if (transformationEventType.getOutputQuantityList() != null) {
			QuantityListType qetl = transformationEventType.getOutputQuantityList();
			List<QuantityElementType> qetList = qetl.getQuantityElement();
			List<DBObject> quantityList = getQuantityObjectList(qetList, gcpLength);
			dbo.put("outputQuantityList", quantityList);
		}
		// Source List
		if (transformationEventType.getSourceList() != null) {
			SourceListType sdtl = transformationEventType.getSourceList();
			List<SourceDestType> sdtList = sdtl.getSource();
			List<DBObject> dbList = getSourceDestObjectList(sdtList, gcpLength);
			dbo.put("sourceList", dbList);
		}
		// Dest List
		if (transformationEventType.getDestinationList() != null) {
			DestinationListType sdtl = transformationEventType.getDestinationList();
			List<SourceDestType> sdtList = sdtl.getDestination();
			List<DBObject> dbList = getSourceDestObjectList(sdtList, gcpLength);
			dbo.put("destinationList", dbList);
		}
		// ILMD
		if (transformationEventType.getIlmd() != null) {
			ILMDType ilmd = transformationEventType.getIlmd();
			if (ilmd.getExtension() != null) {
				ILMDExtensionType ilmdExtension = ilmd.getExtension();
				Map<String, String> map2Save = getILMDExtensionMap(ilmdExtension);
				if (map2Save != null)
					dbo.put("ilmd", map2Save);
				if (outputList != null) {
					MasterDataWriteConverter mdConverter = new MasterDataWriteConverter();
					mdConverter.capture(outputList, map2Save);
				}
			}
		}

		// Vendor Extension
		if (transformationEventType.getAny() != null) {
			List<Object> objList = transformationEventType.getAny();
			Map<String, String> map2Save = getAnyMap(objList);
			if (map2Save != null && map2Save.isEmpty() == false)
				dbo.put("any", map2Save);

		}

		// Extension
		if (transformationEventType.getExtension() != null) {
			TransformationEventExtensionType oee = transformationEventType.getExtension();
			DBObject extension = getTransformationEventExtensionObject(oee);
			dbo.put("extension", extension);
		}

		if (Configuration.isServiceRegistryReportOn == true) {
			HashSet<String> candidateSet = getCandidateEPCSet(transformationEventType);
			DiscoveryServiceAgent dsa = new DiscoveryServiceAgent();
			int updatedEPCCount = dsa.registerEPC(candidateSet);
			Configuration.logger.info(updatedEPCCount + " EPC(s) are registered to Discovery Service");
		}
		return dbo;
	}

	private HashSet<String> getCandidateEPCSet(TransformationEventType transformationEventType) {
		HashSet<String> candidateSet = new HashSet<String>();

		// Input EPC List
		if (transformationEventType.getInputEPCList() != null) {
			EPCListType epcs = transformationEventType.getInputEPCList();
			List<EPC> epcList = epcs.getEpc();
			for (int i = 0; i < epcList.size(); i++) {
				candidateSet.add(epcList.get(i).getValue());
			}
		}

		// Input Quantity
		if (transformationEventType.getInputQuantityList() != null) {
			if (transformationEventType.getInputEPCList() != null) {
				QuantityListType qetl = transformationEventType.getInputQuantityList();
				List<QuantityElementType> qetList = qetl.getQuantityElement();
				for (int i = 0; i < qetList.size(); i++) {
					QuantityElementType qet = qetList.get(i);
					if (qet.getEpcClass() != null)
						candidateSet.add(qet.getEpcClass().toString());
				}
			}
		}

		// Output EPC List
		if (transformationEventType.getOutputEPCList() != null) {
			EPCListType epcs = transformationEventType.getOutputEPCList();
			List<EPC> epcList = epcs.getEpc();
			for (int i = 0; i < epcList.size(); i++) {
				candidateSet.add(epcList.get(i).getValue());
			}
		}

		// Output Quantity
		if (transformationEventType.getOutputQuantityList() != null) {
			if (transformationEventType.getOutputQuantityList() != null) {
				QuantityListType qetl = transformationEventType.getOutputQuantityList();
				List<QuantityElementType> qetList = qetl.getQuantityElement();
				for (int i = 0; i < qetList.size(); i++) {
					QuantityElementType qet = qetList.get(i);
					if (qet.getEpcClass() != null)
						candidateSet.add(qet.getEpcClass().toString());
				}
			}
		}
		return candidateSet;
	}
}
