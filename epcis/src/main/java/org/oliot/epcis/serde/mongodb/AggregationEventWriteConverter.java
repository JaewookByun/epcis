package org.oliot.epcis.serde.mongodb;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.oliot.model.epcis.AggregationEventExtensionType;
import org.oliot.model.epcis.AggregationEventType;
import org.oliot.model.epcis.BusinessLocationType;
import org.oliot.model.epcis.BusinessTransactionListType;
import org.oliot.model.epcis.BusinessTransactionType;
import org.oliot.model.epcis.EPC;
import org.oliot.model.epcis.EPCISEventExtensionType;
import org.oliot.model.epcis.EPCListType;
import org.oliot.model.epcis.ReadPointType;
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
public class AggregationEventWriteConverter implements
		Converter<AggregationEventType, DBObject> {

	public DBObject convert(AggregationEventType aggregationEventType) {

		DBObject dbo = new BasicDBObject();
		// Base Extension
		if (aggregationEventType.getBaseExtension() != null) {
			EPCISEventExtensionType baseExtensionType = aggregationEventType
					.getBaseExtension();
			DBObject baseExtension = getBaseExtensionObject(baseExtensionType);
			dbo.put("baseExtension", baseExtension);
		}
		// Event Time
		if (aggregationEventType.getEventTime() != null)
			dbo.put("eventTime", aggregationEventType.getEventTime()
					.toGregorianCalendar().getTimeInMillis());
		// Event Time Zone
		if (aggregationEventType.getEventTimeZoneOffset() != null)
			dbo.put("eventTimeZoneOffset",
					aggregationEventType.getEventTimeZoneOffset());
		// Record Time : according to M5
		GregorianCalendar recordTime = new GregorianCalendar();
		long recordTimeMilis = recordTime.getTimeInMillis();
		dbo.put("recordTime", recordTimeMilis);

		// Parent ID
		if (aggregationEventType.getParentID() != null)
			dbo.put("parentID", aggregationEventType.getParentID());
		// Child EPCs
		if (aggregationEventType.getChildEPCs() != null) {
			EPCListType epcs = aggregationEventType.getChildEPCs();
			List<EPC> epcList = epcs.getEpc();
			List<DBObject> epcDBList = new ArrayList<DBObject>();

			for (int i = 0; i < epcList.size(); i++) {
				DBObject epcDB = new BasicDBObject();
				epcDB.put("epc", epcList.get(i).getValue());
				epcDBList.add(epcDB);
			}
			dbo.put("childEPCs", epcDBList);
		}
		// Action
		if (aggregationEventType.getAction() != null)
			dbo.put("action", aggregationEventType.getAction().name());
		// Biz Step
		if (aggregationEventType.getBizStep() != null)
			dbo.put("bizStep", aggregationEventType.getBizStep());
		// Disposition
		if (aggregationEventType.getDisposition() != null)
			dbo.put("disposition", aggregationEventType.getDisposition());
		// ReadPoint
		if (aggregationEventType.getReadPoint() != null) {
			ReadPointType readPointType = aggregationEventType.getReadPoint();
			DBObject readPoint = getReadPointObject(readPointType);
			dbo.put("readPoint", readPoint);
		}
		// BizLocation
		if (aggregationEventType.getBizLocation() != null) {
			BusinessLocationType bizLocationType = aggregationEventType
					.getBizLocation();
			DBObject bizLocation = getBizLocationObject(bizLocationType);
			dbo.put("bizLocation", bizLocation);
		}

		if (aggregationEventType.getBizTransactionList() != null) {
			BusinessTransactionListType bizListType = aggregationEventType
					.getBizTransactionList();
			List<BusinessTransactionType> bizList = bizListType
					.getBizTransaction();

			List<DBObject> bizTranList = getBizTransactionObjectList(bizList);
			dbo.put("bizTransactionList", bizTranList);
		}
		
		// Extension
		if (aggregationEventType.getExtension() != null) {
			AggregationEventExtensionType oee = aggregationEventType
					.getExtension();
			DBObject extension = getAggregationEventExtensionObject(oee);
			dbo.put("extension", extension);
		}
		return dbo;
	}
}
