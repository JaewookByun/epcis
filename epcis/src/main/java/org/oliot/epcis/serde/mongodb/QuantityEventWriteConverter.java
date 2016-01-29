package org.oliot.epcis.serde.mongodb;

import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.oliot.epcis.configuration.Configuration;
import org.oliot.epcis.service.registry.DiscoveryServiceAgent;
import org.oliot.model.epcis.BusinessLocationType;
import org.oliot.model.epcis.BusinessTransactionListType;
import org.oliot.model.epcis.BusinessTransactionType;
import org.oliot.model.epcis.EPCISEventExtensionType;
import org.oliot.model.epcis.QuantityEventExtensionType;
import org.oliot.model.epcis.QuantityEventType;
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

public class QuantityEventWriteConverter {

	public DBObject convert(QuantityEventType quantityEventType, Integer gcpLength) {

		DBObject dbo = new BasicDBObject();
		// Base Extension
		if (quantityEventType.getBaseExtension() != null) {
			EPCISEventExtensionType baseExtensionType = quantityEventType.getBaseExtension();
			DBObject baseExtension = getBaseExtensionObject(baseExtensionType);
			dbo.put("baseExtension", baseExtension);
		}
		// Event Time
		if (quantityEventType.getEventTime() != null)
			dbo.put("eventTime", quantityEventType.getEventTime().toGregorianCalendar().getTimeInMillis());
		// Event Time zone
		if (quantityEventType.getEventTimeZoneOffset() != null)
			dbo.put("eventTimeZoneOffset", quantityEventType.getEventTimeZoneOffset());
		// Record Time : according to M5
		GregorianCalendar recordTime = new GregorianCalendar();
		long recordTimeMilis = recordTime.getTimeInMillis();
		dbo.put("recordTime", recordTimeMilis);
		// EPC Class
		if (quantityEventType.getEpcClass() != null)
			dbo.put("epcClass", MongoWriterUtil.getClassEPC(quantityEventType.getEpcClass(),gcpLength));
		dbo.put("quantity", quantityEventType.getQuantity());
		// Business Step
		if (quantityEventType.getBizStep() != null)
			dbo.put("bizStep", quantityEventType.getBizStep());
		// Disposition
		if (quantityEventType.getDisposition() != null)
			dbo.put("disposition", quantityEventType.getDisposition());
		// Read Point
		if (quantityEventType.getReadPoint() != null) {
			ReadPointType readPointType = quantityEventType.getReadPoint();
			DBObject readPoint = getReadPointObject(readPointType, gcpLength);
			dbo.put("readPoint", readPoint);
		}
		// BizLocation
		if (quantityEventType.getBizLocation() != null) {
			BusinessLocationType bizLocationType = quantityEventType.getBizLocation();
			DBObject bizLocation = getBizLocationObject(bizLocationType, gcpLength);
			dbo.put("bizLocation", bizLocation);
		}

		// Vendor Extension
		if (quantityEventType.getAny() != null) {
			List<Object> objList = quantityEventType.getAny();
			Map<String, String> map2Save = getAnyMap(objList);
			if (map2Save != null && map2Save.isEmpty() == false)
				dbo.put("any", map2Save);

		}

		// BizTransaction
		if (quantityEventType.getBizTransactionList() != null) {
			BusinessTransactionListType bizListType = quantityEventType.getBizTransactionList();
			List<BusinessTransactionType> bizList = bizListType.getBizTransaction();
			List<DBObject> bizTranList = getBizTransactionObjectList(bizList);
			dbo.put("bizTransactionList", bizTranList);
		}
		// Extension
		if (quantityEventType.getExtension() != null) {
			QuantityEventExtensionType oee = quantityEventType.getExtension();
			DBObject extension = getQuantityEventExtensionObject(oee);
			dbo.put("extension", extension);
		}

		if (Configuration.isServiceRegistryReportOn == true) {
			HashSet<String> candidateSet = getCandidateEPCSet(quantityEventType);
			DiscoveryServiceAgent dsa = new DiscoveryServiceAgent();
			int updatedEPCCount = dsa.registerEPC(candidateSet);
			Configuration.logger.info(updatedEPCCount + " EPC(s) are registered to Discovery Service");
		}
		return dbo;
	}

	private HashSet<String> getCandidateEPCSet(QuantityEventType quantityEventType) {
		HashSet<String> candidateSet = new HashSet<String>();

		// EPC Class
		if (quantityEventType.getEpcClass() != null) {
			candidateSet.add(quantityEventType.getEpcClass());
		}
		return candidateSet;
	}
}
