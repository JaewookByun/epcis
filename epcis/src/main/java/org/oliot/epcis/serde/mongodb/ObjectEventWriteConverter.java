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
import org.oliot.model.epcis.EPC;
import org.oliot.model.epcis.EPCISEventExtensionType;
import org.oliot.model.epcis.EPCListType;
import org.oliot.model.epcis.ILMDExtensionType;
import org.oliot.model.epcis.ILMDType;
import org.oliot.model.epcis.ObjectEventExtensionType;
import org.oliot.model.epcis.ObjectEventType;
import org.oliot.model.epcis.QuantityElementType;
import org.oliot.model.epcis.QuantityListType;
import org.oliot.model.epcis.ReadPointType;

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

public class ObjectEventWriteConverter {

	public DBObject convert(ObjectEventType objectEventType, Integer gcpLength) {
		DBObject dbo = new BasicDBObject();
		// Base Extension
		if (objectEventType.getBaseExtension() != null) {
			EPCISEventExtensionType baseExtensionType = objectEventType.getBaseExtension();
			DBObject baseExtension = getBaseExtensionObject(baseExtensionType);
			dbo.put("baseExtension", baseExtension);
		}
		// Event Time
		if (objectEventType.getEventTime() != null)
			dbo.put("eventTime", objectEventType.getEventTime().toGregorianCalendar().getTimeInMillis());
		// Event Time Zone
		if (objectEventType.getEventTimeZoneOffset() != null)
			dbo.put("eventTimeZoneOffset", objectEventType.getEventTimeZoneOffset());
		// Record Time : according to M5
		GregorianCalendar recordTime = new GregorianCalendar();
		long recordTimeMilis = recordTime.getTimeInMillis();
		dbo.put("recordTime", recordTimeMilis);
		// EPC List
		List<EPC> epcList = null;
		if (objectEventType.getEpcList() != null) {
			EPCListType epcs = objectEventType.getEpcList();
			epcList = epcs.getEpc();
			List<DBObject> epcDBList = new ArrayList<DBObject>();

			for (int i = 0; i < epcList.size(); i++) {
				DBObject epcDB = new BasicDBObject();
				epcDB.put("epc", MongoWriterUtil.getInstanceEPC(epcList.get(i).getValue(),gcpLength));
				epcDBList.add(epcDB);
			}
			dbo.put("epcList", epcDBList);
		}
		// Action
		if (objectEventType.getAction() != null)
			dbo.put("action", objectEventType.getAction().name());
		// Biz Step
		if (objectEventType.getBizStep() != null)
			dbo.put("bizStep", objectEventType.getBizStep());
		// Disposition
		if (objectEventType.getDisposition() != null)
			dbo.put("disposition", objectEventType.getDisposition());
		// Read Point
		if (objectEventType.getReadPoint() != null) {
			ReadPointType readPointType = objectEventType.getReadPoint();
			DBObject readPoint = getReadPointObject(readPointType, gcpLength);
			dbo.put("readPoint", readPoint);
		}
		// BizLocation
		if (objectEventType.getBizLocation() != null) {
			BusinessLocationType bizLocationType = objectEventType.getBizLocation();
			DBObject bizLocation = getBizLocationObject(bizLocationType, gcpLength);
			dbo.put("bizLocation", bizLocation);
		}
		// BizTransaction
		if (objectEventType.getBizTransactionList() != null) {
			BusinessTransactionListType bizListType = objectEventType.getBizTransactionList();
			List<BusinessTransactionType> bizList = bizListType.getBizTransaction();
			List<DBObject> bizTranList = getBizTransactionObjectList(bizList);
			dbo.put("bizTransactionList", bizTranList);
		}
		// ILMD
		if (objectEventType.getIlmd() != null) {
			ILMDType ilmd = objectEventType.getIlmd();
			if (ilmd.getExtension() != null) {
				ILMDExtensionType ilmdExtension = ilmd.getExtension();
				Map<String, String> map2Save = getILMDExtensionMap(ilmdExtension);
				if (map2Save != null)
					dbo.put("ilmd", map2Save);
				if (epcList != null) {
					MasterDataWriteConverter mdConverter = new MasterDataWriteConverter();
					mdConverter.capture(epcList, map2Save);
				}
			}
		}
		// Vendor Extension
		if (objectEventType.getAny() != null) {
			List<Object> objList = objectEventType.getAny();
			Map<String, String> map2Save = getAnyMap(objList);
			if (map2Save != null && map2Save.isEmpty() == false)
				dbo.put("any", map2Save);

		}

		// Extension
		if (objectEventType.getExtension() != null) {
			ObjectEventExtensionType oee = objectEventType.getExtension();
			DBObject extension = getObjectEventExtensionObject(oee, gcpLength);
			dbo.put("extension", extension);
		}

		if (Configuration.isServiceRegistryReportOn == true) {
			HashSet<String> candidateSet = getCandidateEPCSet(objectEventType);
			DiscoveryServiceAgent dsa = new DiscoveryServiceAgent();
			int updatedEPCCount = dsa.registerEPC(candidateSet);
			Configuration.logger.info(updatedEPCCount + " EPC(s) are registered to Discovery Service");
		}
		return dbo;
	}

	private HashSet<String> getCandidateEPCSet(ObjectEventType objectEventType) {
		HashSet<String> candidateSet = new HashSet<String>();

		// EPCList
		if (objectEventType.getEpcList() != null) {
			EPCListType epcs = objectEventType.getEpcList();
			List<EPC> epcList = epcs.getEpc();
			for (int i = 0; i < epcList.size(); i++) {
				candidateSet.add(epcList.get(i).getValue());
			}
		}
		// Extension
		if (objectEventType.getExtension() != null) {
			ObjectEventExtensionType oee = objectEventType.getExtension();
			if (oee.getQuantityList() != null) {
				QuantityListType qetl = oee.getQuantityList();
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
