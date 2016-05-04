package org.oliot.epcis.converter.mongodb;

import static org.oliot.epcis.converter.mongodb.MongoReaderUtil.*;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.log4j.Level;
import org.bson.BsonArray;
import org.bson.BsonDocument;
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

/**
 * Copyright (C) 2014-2016 Jaewook Byun
 *
 * This project is part of Oliot (oliot.org), pursuing the implementation of
 * Electronic Product Code Information Service(EPCIS) v1.1 specification in
 * EPCglobal.
 * [http://www.gs1.org/gsmp/kc/epcglobal/epcis/epcis_1_1-standard-20140520.pdf]
 * 
 *
 * @author Jaewook Byun, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         bjw0829@kaist.ac.kr, bjw0829@gmail.com
 */

public class TransformationEventReadConverter {

	public TransformationEventType convert(BsonDocument dbObject) {
		try {
			TransformationEventType transformationEventType = new TransformationEventType();
			int zone = 0;
			if (dbObject.get("eventTimeZoneOffset") != null) {
				String eventTimeZoneOffset = dbObject.get("eventTimeZoneOffset").asString().getValue();
				transformationEventType.setEventTimeZoneOffset(eventTimeZoneOffset);
				if (eventTimeZoneOffset.split(":").length == 2) {
					zone = Integer.parseInt(eventTimeZoneOffset.split(":")[0]);
				}
			}
			if (dbObject.get("eventTime") != null) {
				long eventTime = dbObject.get("eventTime").asInt64().getValue();
				GregorianCalendar eventCalendar = new GregorianCalendar();
				eventCalendar.setTimeInMillis(eventTime);
				XMLGregorianCalendar xmlEventTime = DatatypeFactory.newInstance()
						.newXMLGregorianCalendar(eventCalendar);
				xmlEventTime.setTimezone(zone * 60);
				transformationEventType.setEventTime(xmlEventTime);
			}
			if (dbObject.get("recordTime") != null) {
				long eventTime = dbObject.get("recordTime").asInt64().getValue();
				GregorianCalendar recordCalendar = new GregorianCalendar();
				recordCalendar.setTimeInMillis(eventTime);
				XMLGregorianCalendar xmlRecordTime = DatatypeFactory.newInstance()
						.newXMLGregorianCalendar(recordCalendar);
				xmlRecordTime.setTimezone(zone * 60);
				transformationEventType.setRecordTime(xmlRecordTime);
			}
			if (dbObject.get("inputEPCList") != null) {
				BsonArray epcListM = dbObject.get("inputEPCList").asArray();
				EPCListType epcListType = new EPCListType();
				List<EPC> epcs = new ArrayList<EPC>();
				for (int i = 0; i < epcListM.size(); i++) {
					EPC epc = new EPC();
					BsonDocument epcObject = epcListM.get(i).asDocument();
					epc.setValue(epcObject.getString("epc").getValue());
					epcs.add(epc);
				}
				epcListType.setEpc(epcs);
				transformationEventType.setInputEPCList(epcListType);
			}
			if (dbObject.get("outputEPCList") != null) {
				BsonArray epcListM = dbObject.get("outputEPCList").asArray();
				EPCListType epcListType = new EPCListType();
				List<EPC> epcs = new ArrayList<EPC>();
				for (int i = 0; i < epcListM.size(); i++) {
					EPC epc = new EPC();
					BsonDocument epcObject = epcListM.get(i).asDocument();
					epc.setValue(epcObject.getString("epc").getValue());
					epcs.add(epc);
				}
				epcListType.setEpc(epcs);
				transformationEventType.setOutputEPCList(epcListType);
			}
			if (dbObject.get("transformationID") != null)
				transformationEventType.setTransformationID(dbObject.get("transformationID").asString().getValue());
			if (dbObject.get("bizStep") != null)
				transformationEventType.setBizStep(dbObject.get("bizStep").asString().getValue());
			if (dbObject.get("disposition") != null)
				transformationEventType.setDisposition(dbObject.get("disposition").asString().getValue());
			if (dbObject.get("baseExtension") != null) {
				EPCISEventExtensionType eeet = new EPCISEventExtensionType();
				BsonDocument baseExtension = dbObject.get("baseExtension").asDocument();
				eeet = putEPCISExtension(eeet, baseExtension);
				transformationEventType.setBaseExtension(eeet);
			}
			if (dbObject.get("readPoint") != null) {
				BsonDocument readPointObject = dbObject.get("readPoint").asDocument();
				ReadPointType readPointType = new ReadPointType();
				if (readPointObject.get("id") != null) {
					readPointType.setId(readPointObject.get("id").toString());
				}
				if (readPointObject.get("extension") != null) {
					ReadPointExtensionType rpet = new ReadPointExtensionType();
					BsonDocument extension = readPointObject.get("extension").asDocument();
					rpet = putReadPointExtension(rpet, extension);
					readPointType.setExtension(rpet);
				}
				transformationEventType.setReadPoint(readPointType);
			}
			// BusinessLocation
			if (dbObject.get("bizLocation") != null) {
				BsonDocument bizLocationObject = dbObject.get("bizLocation").asDocument();
				BusinessLocationType bizLocationType = new BusinessLocationType();
				if (bizLocationObject.get("id") != null) {
					bizLocationType.setId(bizLocationObject.get("id").toString());
				}
				if (bizLocationObject.get("extension") != null) {
					BusinessLocationExtensionType blet = new BusinessLocationExtensionType();
					BsonDocument extension = bizLocationObject.get("extension").asDocument();
					blet = putBusinessLocationExtension(blet, extension);
					bizLocationType.setExtension(blet);
				}
				transformationEventType.setBizLocation(bizLocationType);
			}
			if (dbObject.get("bizTransactionList") != null) {
				BsonArray bizTranList = dbObject.get("bizTransactionList").asArray();
				BusinessTransactionListType btlt = new BusinessTransactionListType();
				List<BusinessTransactionType> bizTranArrayList = new ArrayList<BusinessTransactionType>();
				for (int i = 0; i < bizTranList.size(); i++) {
					// DBObject, key and value
					BsonDocument bizTran = bizTranList.get(i).asDocument();
					BusinessTransactionType btt = new BusinessTransactionType();
					Iterator<String> keyIter = bizTran.keySet().iterator();
					// at most one bizTran
					if (keyIter.hasNext()) {
						String key = keyIter.next();
						String value = bizTran.getString(key).getValue();
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
				BsonArray quantityDBList = dbObject.getArray("inputQuantityList");
				List<QuantityElementType> qetList = putQuantityElementTypeList(quantityDBList);
				qlt.setQuantityElement(qetList);
				transformationEventType.setInputQuantityList(qlt);
			}
			if (dbObject.get("outputQuantityList") != null) {
				QuantityListType qlt = new QuantityListType();
				BsonArray quantityDBList = dbObject.getArray("outputQuantityList");
				List<QuantityElementType> qetList = putQuantityElementTypeList(quantityDBList);
				qlt.setQuantityElement(qetList);
				transformationEventType.setOutputQuantityList(qlt);
			}
			// SourceList
			if (dbObject.get("sourceList") != null) {
				// Source Dest Type : Key / Value
				SourceListType slt = new SourceListType();
				List<SourceDestType> sdtList = new ArrayList<SourceDestType>();
				BsonArray sourceDBList = dbObject.get("sourceList").asArray();
				for (int i = 0; i < sourceDBList.size(); i++) {
					BsonDocument sdObject = sourceDBList.get(i).asDocument();
					// DBObject, key and value
					SourceDestType sdt = new SourceDestType();
					Iterator<String> keyIter = sdObject.keySet().iterator();
					// at most one bizTran
					if (keyIter.hasNext()) {
						String key = keyIter.next();
						String value = sdObject.getString(key).getValue();
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
				BsonArray destinationDBList = dbObject.get("destinationList").asArray();
				for (int i = 0; i < destinationDBList.size(); i++) {
					BsonDocument sdObject = destinationDBList.get(i).asDocument();
					// DBObject, key and value
					SourceDestType sdt = new SourceDestType();
					Iterator<String> keyIter = sdObject.keySet().iterator();
					// at most one bizTran
					if (keyIter.hasNext()) {
						String key = keyIter.next();
						String value = sdObject.getString(key).getValue();
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
				BsonDocument ilmdObject = dbObject.getDocument("ilmd");
				if(ilmdObject.containsKey("any")){
					ilmd = putILMD(ilmd, ilmdObject.get("any").asDocument());						
					transformationEventType.setIlmd(ilmd);
				}
			}

			// Vendor Extension
			if (dbObject.get("any") != null) {
				BsonDocument anyObject = dbObject.get("any").asDocument();
				List<Object> any = putAny(anyObject);
				transformationEventType.setAny(any);
			}

			// extension
			if (dbObject.get("extension") != null) {
				TransformationEventExtensionType tfeet = new TransformationEventExtensionType();
				BsonDocument extension = dbObject.get("extension").asDocument();
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
