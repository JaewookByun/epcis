package org.oliot.epcis.serde.mongodb;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.oliot.model.epcis.BusinessLocationType;
import org.oliot.model.epcis.BusinessTransactionListType;
import org.oliot.model.epcis.BusinessTransactionType;
import org.oliot.model.epcis.EPC;
import org.oliot.model.epcis.EPCISEventExtensionType;
import org.oliot.model.epcis.EPCListType;
import org.oliot.model.epcis.ReadPointType;
import org.oliot.model.epcis.TransactionEventExtensionType;
import org.oliot.model.epcis.TransactionEventType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

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

@Component
@WritingConverter
public class TransactionEventWriteConverter implements
		Converter<TransactionEventType, DBObject> {

	public DBObject convert(TransactionEventType transactionEventType) {

		DBObject dbo = new BasicDBObject();
		// Base Extension
		if (transactionEventType.getBaseExtension() != null) {
			EPCISEventExtensionType baseExtensionType = transactionEventType
					.getBaseExtension();
			DBObject baseExtension = getBaseExtensionObject(baseExtensionType);
			dbo.put("baseExtension", baseExtension);
		}
		// Event Time
		if (transactionEventType.getEventTime() != null)
			dbo.put("eventTime", transactionEventType.getEventTime()
					.toGregorianCalendar().getTimeInMillis());
		// Event Time Zone
		if (transactionEventType.getEventTimeZoneOffset() != null)
			dbo.put("eventTimeZoneOffset",
					transactionEventType.getEventTimeZoneOffset());
		// Record Time : according to M5
		GregorianCalendar recordTime = new GregorianCalendar();
		long recordTimeMilis = recordTime.getTimeInMillis();
		dbo.put("recordTime", recordTimeMilis);
		// Parent ID
		if (transactionEventType.getParentID() != null)
			dbo.put("parentID", transactionEventType.getParentID());
		// EPC List
		if (transactionEventType.getEpcList() != null) {
			EPCListType epcs = transactionEventType.getEpcList();
			List<EPC> epcList = epcs.getEpc();
			List<DBObject> epcDBList = new ArrayList<DBObject>();
			for (int i = 0; i < epcList.size(); i++) {
				DBObject epcDB = new BasicDBObject();
				epcDB.put("epc", epcList.get(i).getValue());
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
			DBObject readPoint = getReadPointObject(readPointType);
			dbo.put("readPoint", readPoint);
		}
		if (transactionEventType.getBizLocation() != null) {
			BusinessLocationType bizLocationType = transactionEventType
					.getBizLocation();
			DBObject bizLocation = getBizLocationObject(bizLocationType);
			dbo.put("bizLocation", bizLocation);
		}

		if (transactionEventType.getBizTransactionList() != null) {
			BusinessTransactionListType bizListType = transactionEventType
					.getBizTransactionList();
			List<BusinessTransactionType> bizList = bizListType
					.getBizTransaction();

			List<DBObject> bizTranList = getBizTransactionObjectList(bizList);
			dbo.put("bizTransactionList", bizTranList);
		}
		// Extension
		if (transactionEventType.getExtension() != null) {
			TransactionEventExtensionType oee = transactionEventType
					.getExtension();
			DBObject extension = getTransactionEventExtensionObject(oee);
			dbo.put("extension", extension);
		}
		return dbo;
	}
}
