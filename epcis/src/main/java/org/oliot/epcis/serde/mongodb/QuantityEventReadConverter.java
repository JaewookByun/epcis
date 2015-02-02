package org.oliot.epcis.serde.mongodb;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.log4j.Level;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.model.epcis.BusinessLocationExtensionType;
import org.oliot.model.epcis.BusinessLocationType;
import org.oliot.model.epcis.BusinessTransactionListType;
import org.oliot.model.epcis.BusinessTransactionType;
import org.oliot.model.epcis.EPCISEventExtensionType;
import org.oliot.model.epcis.QuantityEventExtensionType;
import org.oliot.model.epcis.QuantityEventType;
import org.oliot.model.epcis.ReadPointExtensionType;
import org.oliot.model.epcis.ReadPointType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import static org.oliot.epcis.serde.mongodb.MongoReaderUtil.*;

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
@ReadingConverter
public class QuantityEventReadConverter implements
		Converter<DBObject, QuantityEventType> {

	public QuantityEventType convert(DBObject dbObject) {
		try {
			QuantityEventType quantityEventType = new QuantityEventType();
			int zone = 0;
			if (dbObject.get("eventTimeZoneOffset") != null) {
				String eventTimeZoneOffset = (String) dbObject
						.get("eventTimeZoneOffset");
				quantityEventType
						.setEventTimeZoneOffset(eventTimeZoneOffset);
				if(eventTimeZoneOffset.split(":").length == 2 ){
					zone = Integer.parseInt(eventTimeZoneOffset.split(":")[0]);
				}
			}
			if (dbObject.get("eventTime") != null) {
				long eventTime = (long) dbObject.get("eventTime");
				GregorianCalendar eventCalendar = new GregorianCalendar();
				eventCalendar.setTimeInMillis(eventTime);
				XMLGregorianCalendar xmlEventTime = DatatypeFactory
						.newInstance().newXMLGregorianCalendar(eventCalendar);
				xmlEventTime.setTimezone(zone * 60);
				quantityEventType.setEventTime(xmlEventTime);
			}
			if (dbObject.get("recordTime") != null) {
				long eventTime = (long) dbObject.get("recordTime");
				GregorianCalendar recordCalendar = new GregorianCalendar();
				recordCalendar.setTimeInMillis(eventTime);
				XMLGregorianCalendar xmlRecordTime = DatatypeFactory
						.newInstance().newXMLGregorianCalendar(recordCalendar);
				xmlRecordTime.setTimezone(zone * 60);
				quantityEventType.setRecordTime(xmlRecordTime);
			}
			if (dbObject.get("epcClass") != null)
				quantityEventType.setEpcClass(dbObject.get("epcClass")
						.toString());
			if (dbObject.get("bizStep") != null)
				quantityEventType
						.setBizStep(dbObject.get("bizStep").toString());
			if (dbObject.get("disposition") != null)
				quantityEventType.setDisposition(dbObject.get("disposition")
						.toString());
			if (dbObject.get("baseExtension") != null) {
				EPCISEventExtensionType eeet = new EPCISEventExtensionType();
				BasicDBObject baseExtension = (BasicDBObject) dbObject
						.get("baseExtension");
				eeet = putEPCISExtension(eeet, baseExtension);
				quantityEventType.setBaseExtension(eeet);
			}
			if (dbObject.get("quantity") != null) {
				int quantity = (int) dbObject.get("quantity");
				quantityEventType.setQuantity(quantity);
			}
			if (dbObject.get("readPoint") != null) {
				BasicDBObject readPointObject = (BasicDBObject) dbObject
						.get("readPoint");
				ReadPointType readPointType = new ReadPointType();
				if (readPointObject.get("id") != null) {
					readPointType.setId(readPointObject.get("id").toString());
				}
				if (readPointObject.get("extension") != null) {
					ReadPointExtensionType rpet = new ReadPointExtensionType();
					BasicDBObject extension = (BasicDBObject) readPointObject
							.get("extension");
					rpet = putReadPointExtension(rpet, extension);
					readPointType.setExtension(rpet);
				}
				quantityEventType.setReadPoint(readPointType);
			}
			// BusinessLocation
			if (dbObject.get("bizLocation") != null) {
				BasicDBObject bizLocationObject = (BasicDBObject) dbObject
						.get("bizLocation");
				BusinessLocationType bizLocationType = new BusinessLocationType();
				if (bizLocationObject.get("id") != null) {
					bizLocationType.setId(bizLocationObject.get("id")
							.toString());
				}
				if (bizLocationObject.get("extension") != null) {
					BusinessLocationExtensionType blet = new BusinessLocationExtensionType();
					BasicDBObject extension = (BasicDBObject) bizLocationObject
							.get("extension");
					blet = putBusinessLocationExtension(blet, extension);
					bizLocationType.setExtension(blet);
				}
				quantityEventType.setBizLocation(bizLocationType);
			}
			if (dbObject.get("bizTransactionList") != null) {
				BasicDBList bizTranList = (BasicDBList) dbObject
						.get("bizTransactionList");
				BusinessTransactionListType btlt = new BusinessTransactionListType();
				List<BusinessTransactionType> bizTranArrayList = new ArrayList<BusinessTransactionType>();
				for (int i = 0; i < bizTranList.size(); i++) {
					// DBObject, key and value
					BasicDBObject bizTran = (BasicDBObject) bizTranList.get(i);
					BusinessTransactionType btt = new BusinessTransactionType();
					Iterator<String> keyIter = bizTran.keySet().iterator();
					// at most one bizTran
					if (keyIter.hasNext()) {
						String key = keyIter.next();
						String value = bizTran.getString(key);
						if (key != null && value != null) {
							btt.setType(key);
							btt.setValue(value);
						}
					}
					if (btt != null)
						bizTranArrayList.add(btt);
				}
				btlt.setBizTransaction(bizTranArrayList);
				quantityEventType.setBizTransactionList(btlt);
			}

			// Extension Field
			if (dbObject.get("extension") != null) {
				QuantityEventExtensionType qeet = new QuantityEventExtensionType();
				BasicDBObject extension = (BasicDBObject) dbObject
						.get("extension");
				qeet = putQuantityExtension(qeet, extension);
				quantityEventType.setExtension(qeet);
			}
			return quantityEventType;
		} catch (DatatypeConfigurationException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
		}
		return null;
	}
}
