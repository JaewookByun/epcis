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
import org.oliot.model.epcis.DestinationListType;
import org.oliot.model.epcis.EPC;
import org.oliot.model.epcis.EPCISEventExtensionType;
import org.oliot.model.epcis.EPCListType;
import org.oliot.model.epcis.ILMDType;
import org.oliot.model.epcis.QuantityElementType;
import org.oliot.model.epcis.QuantityListType;
import org.oliot.model.epcis.ReadPointExtensionType;
import org.oliot.model.epcis.ReadPointType;
import org.oliot.model.epcis.SourceDestType;
import org.oliot.model.epcis.SourceListType;
import org.oliot.model.epcis.TransformationEventExtensionType;
import org.oliot.model.epcis.TransformationEventType;
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
public class TransformationEventReadConverter implements
		Converter<DBObject, TransformationEventType> {

	public TransformationEventType convert(DBObject dbObject) {
		try {
			TransformationEventType transformationEventType = new TransformationEventType();
			int zone = 0;
			if (dbObject.get("eventTimeZoneOffset") != null) {
				String eventTimeZoneOffset = (String) dbObject
						.get("eventTimeZoneOffset");
				transformationEventType
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
				xmlEventTime.setTimezone(zone*60);
				transformationEventType.setEventTime(xmlEventTime);
			}
			if (dbObject.get("recordTime") != null) {
				long eventTime = (long) dbObject.get("recordTime");
				GregorianCalendar recordCalendar = new GregorianCalendar();
				recordCalendar.setTimeInMillis(eventTime);
				XMLGregorianCalendar xmlRecordTime = DatatypeFactory
						.newInstance().newXMLGregorianCalendar(recordCalendar);
				xmlRecordTime.setTimezone(zone*60);
				transformationEventType.setRecordTime(xmlRecordTime);
			}
			if (dbObject.get("inputEPCList") != null) {
				BasicDBList epcListM = (BasicDBList) dbObject
						.get("inputEPCList");
				EPCListType epcListType = new EPCListType();
				List<EPC> epcs = new ArrayList<EPC>();
				for (int i = 0; i < epcListM.size(); i++) {
					EPC epc = new EPC();
					BasicDBObject epcObject = (BasicDBObject) epcListM.get(i);
					epc.setValue(epcObject.getString("epc"));
					epcs.add(epc);
				}
				epcListType.setEpc(epcs);
				transformationEventType.setInputEPCList(epcListType);
			}
			if (dbObject.get("outputEPCList") != null) {
				BasicDBList epcListM = (BasicDBList) dbObject
						.get("outputEPCList");
				EPCListType epcListType = new EPCListType();
				List<EPC> epcs = new ArrayList<EPC>();
				for (int i = 0; i < epcListM.size(); i++) {
					EPC epc = new EPC();
					BasicDBObject epcObject = (BasicDBObject) epcListM.get(i);
					epc.setValue(epcObject.getString("epc"));
					epcs.add(epc);
				}
				epcListType.setEpc(epcs);
				transformationEventType.setOutputEPCList(epcListType);
			}
			if (dbObject.get("transformationID") != null)
				transformationEventType.setTransformationID(dbObject.get(
						"transformationID").toString());
			if (dbObject.get("bizStep") != null)
				transformationEventType.setBizStep(dbObject.get("bizStep")
						.toString());
			if (dbObject.get("disposition") != null)
				transformationEventType.setDisposition(dbObject.get(
						"disposition").toString());
			if (dbObject.get("baseExtension") != null) {
				EPCISEventExtensionType eeet = new EPCISEventExtensionType();
				BasicDBObject baseExtension = (BasicDBObject) dbObject
						.get("baseExtension");
				eeet = putEPCISExtension(eeet, baseExtension);
				transformationEventType.setBaseExtension(eeet);
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
				transformationEventType.setReadPoint(readPointType);
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
				transformationEventType.setBizLocation(bizLocationType);
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
				transformationEventType.setBizTransactionList(btlt);
			}

			// Quantity
			if (dbObject.get("inputQuantityList") != null) {
				QuantityListType qlt = new QuantityListType();
				List<QuantityElementType> qetList = new ArrayList<QuantityElementType>();
				BasicDBList quantityDBList = (BasicDBList) dbObject
						.get("inputQuantityList");
				for (int i = 0; i < quantityDBList.size(); i++) {
					QuantityElementType qet = new QuantityElementType();
					BasicDBObject quantityDBObject = (BasicDBObject) quantityDBList
							.get(i);
					Object epcClassObject = quantityDBObject.get("epcClass");
					Object quantity = quantityDBObject.get("quantity");
					Object uom = quantityDBObject.get("uom");
					if (epcClassObject != null) {
						qet.setEpcClass(epcClassObject.toString());
						if (quantity != null) {
							double quantityDouble = (double) quantity;
							qet.setQuantity((float) quantityDouble);
						}
						if (uom != null)
							qet.setUom(uom.toString());
						qetList.add(qet);
					}
				}
				qlt.setQuantityElement(qetList);
				transformationEventType.setInputQuantityList(qlt);
			}
			if (dbObject.get("outputQuantityList") != null) {
				QuantityListType qlt = new QuantityListType();
				List<QuantityElementType> qetList = new ArrayList<QuantityElementType>();
				BasicDBList quantityDBList = (BasicDBList) dbObject
						.get("outputQuantityList");
				for (int i = 0; i < quantityDBList.size(); i++) {
					QuantityElementType qet = new QuantityElementType();
					BasicDBObject quantityDBObject = (BasicDBObject) quantityDBList
							.get(i);
					Object epcClassObject = quantityDBObject.get("epcClass");
					Object quantity = quantityDBObject.get("quantity");
					Object uom = quantityDBObject.get("uom");
					if (epcClassObject != null) {
						qet.setEpcClass(epcClassObject.toString());
						if (quantity != null) {
							double quantityDouble = (double) quantity;
							qet.setQuantity((float) quantityDouble);
						}
						if (uom != null)
							qet.setUom(uom.toString());
						qetList.add(qet);
					}
				}
				qlt.setQuantityElement(qetList);
				transformationEventType.setOutputQuantityList(qlt);
			}
			// SourceList
			if (dbObject.get("sourceList") != null) {
				// Source Dest Type : Key / Value
				SourceListType slt = new SourceListType();
				List<SourceDestType> sdtList = new ArrayList<SourceDestType>();
				BasicDBList sourceDBList = (BasicDBList) dbObject
						.get("sourceList");
				for (int i = 0; i < sourceDBList.size(); i++) {
					BasicDBObject sdObject = (BasicDBObject) sourceDBList
							.get(i);
					// DBObject, key and value
					SourceDestType sdt = new SourceDestType();
					Iterator<String> keyIter = sdObject.keySet().iterator();
					// at most one bizTran
					if (keyIter.hasNext()) {
						String key = keyIter.next();
						String value = sdObject.getString(key);
						if (key != null && value != null) {
							sdt.setType(key);
							sdt.setValue(value);
						}
					}
					if (sdt != null)
						sdtList.add(sdt);
				}
				slt.setSource(sdtList);
				transformationEventType.setSourceList(slt);
			}
			// DestinationList
			if (dbObject.get("destinationList") != null) {
				// Source Dest Type : Key / Value
				DestinationListType dlt = new DestinationListType();
				List<SourceDestType> sdtList = new ArrayList<SourceDestType>();
				BasicDBList destinationDBList = (BasicDBList) dbObject
						.get("destinationList");
				for (int i = 0; i < destinationDBList.size(); i++) {
					BasicDBObject sdObject = (BasicDBObject) destinationDBList
							.get(i);
					// DBObject, key and value
					SourceDestType sdt = new SourceDestType();
					Iterator<String> keyIter = sdObject.keySet().iterator();
					// at most one bizTran
					if (keyIter.hasNext()) {
						String key = keyIter.next();
						String value = sdObject.getString(key);
						if (key != null && value != null) {
							sdt.setType(key);
							sdt.setValue(value);
						}
					}
					if (sdt != null)
						sdtList.add(sdt);
				}
				dlt.setDestination(sdtList);
				transformationEventType.setDestinationList(dlt);
			}
			if (dbObject.get("ilmd") != null) {
				ILMDType ilmd = new ILMDType();
				BasicDBObject anyObject = (BasicDBObject) dbObject.get("ilmd");
				ilmd = putILMD(ilmd, anyObject);
				transformationEventType.setIlmd(ilmd);
			}
			// extension
			if (dbObject.get("extension") != null) {
				TransformationEventExtensionType tfeet = new TransformationEventExtensionType();
				BasicDBObject extension = (BasicDBObject) dbObject
						.get("extension");
				tfeet = putTransformationExtension(tfeet, extension);
				transformationEventType.setExtension(tfeet);
			}
			return transformationEventType;
		} catch (DatatypeConfigurationException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
		}
		return null;
	}
}
