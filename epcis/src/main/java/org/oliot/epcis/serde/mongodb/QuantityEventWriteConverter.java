package org.oliot.epcis.serde.mongodb;

import java.util.GregorianCalendar;
import java.util.List;

import org.oliot.model.epcis.BusinessLocationType;
import org.oliot.model.epcis.BusinessTransactionListType;
import org.oliot.model.epcis.BusinessTransactionType;
import org.oliot.model.epcis.EPCISEventExtensionType;
import org.oliot.model.epcis.QuantityEventExtensionType;
import org.oliot.model.epcis.QuantityEventType;
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
public class QuantityEventWriteConverter implements
		Converter<QuantityEventType, DBObject> {

	public DBObject convert(QuantityEventType quantityEventType) {

		DBObject dbo = new BasicDBObject();
		// Base Extension
		if (quantityEventType.getBaseExtension() != null) {
			EPCISEventExtensionType baseExtensionType = quantityEventType
					.getBaseExtension();
			DBObject baseExtension = getBaseExtensionObject(baseExtensionType);
			dbo.put("baseExtension", baseExtension);
		}
		// Event Time
		if (quantityEventType.getEventTime() != null)
			dbo.put("eventTime", quantityEventType.getEventTime()
					.toGregorianCalendar().getTimeInMillis());
		// Event Time zone
		if (quantityEventType.getEventTimeZoneOffset() != null)
			dbo.put("eventTimeZoneOffset",
					quantityEventType.getEventTimeZoneOffset());
		// Record Time : according to M5
		GregorianCalendar recordTime = new GregorianCalendar();
		long recordTimeMilis = recordTime.getTimeInMillis();
		dbo.put("recordTime", recordTimeMilis);
		// EPC Class
		if (quantityEventType.getEpcClass() != null)
			dbo.put("epcClass", quantityEventType.getEpcClass());
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
			DBObject readPoint = getReadPointObject(readPointType);
			dbo.put("readPoint", readPoint);
		}
		// BizLocation
		if (quantityEventType.getBizLocation() != null) {
			BusinessLocationType bizLocationType = quantityEventType
					.getBizLocation();
			DBObject bizLocation = getBizLocationObject(bizLocationType);
			dbo.put("bizLocation", bizLocation);
		}
		// BizTransaction
		if (quantityEventType.getBizTransactionList() != null) {
			BusinessTransactionListType bizListType = quantityEventType
					.getBizTransactionList();
			List<BusinessTransactionType> bizList = bizListType
					.getBizTransaction();
			List<DBObject> bizTranList = getBizTransactionObjectList(bizList);
			dbo.put("bizTransactionList", bizTranList);
		}
		// Extension
		if (quantityEventType.getExtension() != null) {
			QuantityEventExtensionType oee = quantityEventType.getExtension();
			DBObject extension = getQuantityEventExtensionObject(oee);
			dbo.put("extension", extension);
		}
		return dbo;
	}
}
