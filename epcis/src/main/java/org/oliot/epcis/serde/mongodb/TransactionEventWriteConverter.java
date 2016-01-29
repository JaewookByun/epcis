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
import org.oliot.model.epcis.QuantityElementType;
import org.oliot.model.epcis.QuantityListType;
import org.oliot.model.epcis.ReadPointType;
import org.oliot.model.epcis.TransactionEventExtensionType;
import org.oliot.model.epcis.TransactionEventType;

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

public class TransactionEventWriteConverter {

	public DBObject convert(TransactionEventType transactionEventType, Integer gcpLength) {

		DBObject dbo = new BasicDBObject();
		// Base Extension
		if (transactionEventType.getBaseExtension() != null) {
			EPCISEventExtensionType baseExtensionType = transactionEventType.getBaseExtension();
			DBObject baseExtension = getBaseExtensionObject(baseExtensionType);
			dbo.put("baseExtension", baseExtension);
		}
		// Event Time
		if (transactionEventType.getEventTime() != null)
			dbo.put("eventTime", transactionEventType.getEventTime().toGregorianCalendar().getTimeInMillis());
		// Event Time Zone
		if (transactionEventType.getEventTimeZoneOffset() != null)
			dbo.put("eventTimeZoneOffset", transactionEventType.getEventTimeZoneOffset());
		// Record Time : according to M5
		GregorianCalendar recordTime = new GregorianCalendar();
		long recordTimeMilis = recordTime.getTimeInMillis();
		dbo.put("recordTime", recordTimeMilis);
		// Parent ID
		if (transactionEventType.getParentID() != null)
			dbo.put("parentID", MongoWriterUtil.getInstanceEPC(transactionEventType.getParentID(),gcpLength));
		// EPC List
		if (transactionEventType.getEpcList() != null) {
			EPCListType epcs = transactionEventType.getEpcList();
			List<EPC> epcList = epcs.getEpc();
			List<DBObject> epcDBList = new ArrayList<DBObject>();
			for (int i = 0; i < epcList.size(); i++) {
				DBObject epcDB = new BasicDBObject();
				epcDB.put("epc", MongoWriterUtil.getInstanceEPC(epcList.get(i).getValue(),gcpLength));
				epcDBList.add(epcDB);
			}
			dbo.put("epcList", epcDBList);
		}
		// Action
		if (transactionEventType.getAction() != null)
			dbo.put("action", transactionEventType.getAction().name());
		// BizStep
		if (transactionEventType.getBizStep() != null)
			dbo.put("bizStep", transactionEventType.getBizStep());
		// Disposition
		if (transactionEventType.getDisposition() != null)
			dbo.put("disposition", transactionEventType.getDisposition());
		if (transactionEventType.getReadPoint() != null) {
			ReadPointType readPointType = transactionEventType.getReadPoint();
			DBObject readPoint = getReadPointObject(readPointType, gcpLength);
			dbo.put("readPoint", readPoint);
		}
		if (transactionEventType.getBizLocation() != null) {
			BusinessLocationType bizLocationType = transactionEventType.getBizLocation();
			DBObject bizLocation = getBizLocationObject(bizLocationType, gcpLength);
			dbo.put("bizLocation", bizLocation);
		}

		if (transactionEventType.getBizTransactionList() != null) {
			BusinessTransactionListType bizListType = transactionEventType.getBizTransactionList();
			List<BusinessTransactionType> bizList = bizListType.getBizTransaction();

			List<DBObject> bizTranList = getBizTransactionObjectList(bizList);
			dbo.put("bizTransactionList", bizTranList);
		}

		// Vendor Extension
		if (transactionEventType.getAny() != null) {
			List<Object> objList = transactionEventType.getAny();
			Map<String, String> map2Save = getAnyMap(objList);
			if (map2Save != null && map2Save.isEmpty() == false)
				dbo.put("any", map2Save);

		}

		// Extension
		if (transactionEventType.getExtension() != null) {
			TransactionEventExtensionType oee = transactionEventType.getExtension();
			DBObject extension = getTransactionEventExtensionObject(oee, gcpLength);
			dbo.put("extension", extension);
		}

		if (Configuration.isServiceRegistryReportOn == true) {
			HashSet<String> candidateSet = getCandidateEPCSet(transactionEventType);
			DiscoveryServiceAgent dsa = new DiscoveryServiceAgent();
			int updatedEPCCount = dsa.registerEPC(candidateSet);
			Configuration.logger.info(updatedEPCCount + " EPC(s) are registered to Discovery Service");
		}
		return dbo;
	}

	private HashSet<String> getCandidateEPCSet(TransactionEventType transactionEventType) {
		HashSet<String> candidateSet = new HashSet<String>();

		// Parent ID
		if (transactionEventType.getParentID() != null) {
			candidateSet.add(transactionEventType.getParentID());
		}
		// EPC List
		if (transactionEventType.getEpcList() != null) {
			EPCListType epcs = transactionEventType.getEpcList();
			List<EPC> epcList = epcs.getEpc();
			for (int i = 0; i < epcList.size(); i++) {
				candidateSet.add(epcList.get(i).getValue());
			}
		}
		// Extension
		if (transactionEventType.getExtension() != null) {
			TransactionEventExtensionType aee = transactionEventType.getExtension();
			if (aee.getQuantityList() != null) {
				QuantityListType qetl = aee.getQuantityList();
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
