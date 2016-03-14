package org.oliot.epcis.serde.mongodb;

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
import org.bson.BsonValue;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.model.epcis.ActionType;
import org.oliot.model.epcis.AggregationEventExtension2Type;
import org.oliot.model.epcis.AggregationEventExtensionType;
import org.oliot.model.epcis.AggregationEventType;
import org.oliot.model.epcis.BusinessLocationExtensionType;
import org.oliot.model.epcis.BusinessLocationType;
import org.oliot.model.epcis.BusinessTransactionListType;
import org.oliot.model.epcis.BusinessTransactionType;
import org.oliot.model.epcis.DestinationListType;
import org.oliot.model.epcis.EPC;
import org.oliot.model.epcis.EPCISEventExtensionType;
import org.oliot.model.epcis.EPCListType;
import org.oliot.model.epcis.QuantityElementType;
import org.oliot.model.epcis.QuantityListType;
import org.oliot.model.epcis.ReadPointExtensionType;
import org.oliot.model.epcis.ReadPointType;
import org.oliot.model.epcis.SourceDestType;
import org.oliot.model.epcis.SourceListType;

import static org.oliot.epcis.serde.mongodb.MongoReaderUtil.*;

/**
 * Copyright (C) 2014-2016 Jaewook Byun
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

public class AggregationEventReadConverter {

	public AggregationEventType convert(BsonDocument dbObject) {

		try {
			AggregationEventType aggregationEventType = new AggregationEventType();
			int zone = 0;
			if (dbObject.get("eventTimeZoneOffset") != null) {
				String eventTimeZoneOffset = dbObject.getString("eventTimeZoneOffset").getValue();
				aggregationEventType.setEventTimeZoneOffset(eventTimeZoneOffset);
				if (eventTimeZoneOffset.split(":").length == 2) {
					zone = Integer.parseInt(eventTimeZoneOffset.split(":")[0]);
				}
			}
			if (dbObject.get("eventTime") != null) {
				long eventTime = dbObject.getInt64("eventTime").getValue();
				GregorianCalendar eventCalendar = new GregorianCalendar();
				eventCalendar.setTimeInMillis(eventTime);
				XMLGregorianCalendar xmlEventTime = DatatypeFactory.newInstance()
						.newXMLGregorianCalendar(eventCalendar);
				xmlEventTime.setTimezone(zone * 60);
				aggregationEventType.setEventTime(xmlEventTime);
			}
			if (dbObject.get("recordTime") != null) {
				long eventTime = dbObject.getInt64("recordTime").getValue();
				GregorianCalendar recordCalendar = new GregorianCalendar();
				recordCalendar.setTimeInMillis(eventTime);
				XMLGregorianCalendar xmlRecordTime = DatatypeFactory.newInstance()
						.newXMLGregorianCalendar(recordCalendar);
				xmlRecordTime.setTimezone(zone * 60);
				aggregationEventType.setRecordTime(xmlRecordTime);
			}
			if (dbObject.get("parentID") != null)
				aggregationEventType.setParentID(dbObject.getString("parentID").getValue());
			if (dbObject.get("childEPCs") != null) {
				BsonArray epcListM = dbObject.getArray("childEPCs");
				EPCListType epcListType = new EPCListType();
				List<EPC> epcs = new ArrayList<EPC>();
				Iterator<BsonValue> bsonEPCIterator = epcListM.iterator();
				while (bsonEPCIterator.hasNext()) {
					BsonValue bson = bsonEPCIterator.next();
					EPC epc = new EPC();
					epc.setValue(bson.asString().getValue());
					epcs.add(epc);
				}
				epcListType.setEpc(epcs);
				aggregationEventType.setChildEPCs(epcListType);
			}
			if (dbObject.get("action") != null) {
				aggregationEventType.setAction(ActionType.fromValue(dbObject.getString("action").getValue()));
			}
			if (dbObject.get("bizStep") != null) {
				aggregationEventType.setBizStep(dbObject.getString("bizStep").getValue());
			}
			if (dbObject.get("disposition") != null) {
				aggregationEventType.setDisposition(dbObject.getString("disposition").getValue());
			}
			if (dbObject.get("baseExtension") != null) {
				EPCISEventExtensionType eeet = new EPCISEventExtensionType();
				BsonDocument baseExtension = (BsonDocument) dbObject.getDocument("baseExtension");
				eeet = putEPCISExtension(eeet, baseExtension);
				aggregationEventType.setBaseExtension(eeet);
			}
			if (dbObject.get("readPoint") != null) {
				BsonDocument readPointObject = dbObject.getDocument("readPoint");
				ReadPointType readPointType = new ReadPointType();
				if (readPointObject.get("id") != null) {
					readPointType.setId(readPointObject.getString("id").getValue());
				}
				if (readPointObject.get("extension") != null) {
					ReadPointExtensionType rpet = new ReadPointExtensionType();
					BsonDocument extension = readPointObject.getDocument("extension");
					rpet = putReadPointExtension(rpet, extension);
					readPointType.setExtension(rpet);
				}
				aggregationEventType.setReadPoint(readPointType);
			}
			// BusinessLocation
			if (dbObject.get("bizLocation") != null) {
				BsonDocument bizLocationObject = dbObject.getDocument("bizLocation");
				BusinessLocationType bizLocationType = new BusinessLocationType();
				if (bizLocationObject.get("id") != null) {
					bizLocationType.setId(bizLocationObject.getString("id").getValue());
				}
				if (bizLocationObject.get("extension") != null) {
					BusinessLocationExtensionType blet = new BusinessLocationExtensionType();
					BsonDocument extension = bizLocationObject.getDocument("extension");
					blet = putBusinessLocationExtension(blet, extension);
					bizLocationType.setExtension(blet);
				}
				aggregationEventType.setBizLocation(bizLocationType);
			}
			if (dbObject.get("bizTransactionList") != null) {
				BsonArray bizTranList = dbObject.getArray("bizTransactionList");
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
				aggregationEventType.setBizTransactionList(btlt);
			}

			// Vendor Extension
			if (dbObject.get("any") != null) {
				BsonDocument anyObject = dbObject.getDocument("any");
				List<Object> any = putAny(anyObject);
				aggregationEventType.setAny(any);
			}

			// Extension Field
			if (dbObject.get("extension") != null) {
				AggregationEventExtensionType aeet = new AggregationEventExtensionType();
				BsonDocument extObject = dbObject.getDocument("extension");
				// Quantity
				if (extObject.get("childQuantityList") != null) {
					QuantityListType qlt = new QuantityListType();
					List<QuantityElementType> qetList = new ArrayList<QuantityElementType>();
					BsonArray quantityDBList = extObject.getArray("childQuantityList");
					for (int i = 0; i < quantityDBList.size(); i++) {
						QuantityElementType qet = new QuantityElementType();
						BsonDocument quantityDBObject = quantityDBList.get(i).asDocument();
						BsonValue epcClassObject = quantityDBObject.get("epcClass");
						BsonValue quantity = quantityDBObject.get("quantity");
						BsonValue uom = quantityDBObject.get("uom");
						if (epcClassObject != null) {
							qet.setEpcClass(epcClassObject.asString().getValue());
							if (quantity != null) {
								double quantityDouble = quantity.asDouble().getValue();
								qet.setQuantity((float) quantityDouble);
							}
							if (uom != null)
								qet.setUom(uom.asString().getValue());
							qetList.add(qet);
						}
					}
					qlt.setQuantityElement(qetList);
					aeet.setChildQuantityList(qlt);
				}
				// SourceList
				if (extObject.get("sourceList") != null) {
					// Source Dest Type : Key / Value
					SourceListType slt = new SourceListType();
					List<SourceDestType> sdtList = new ArrayList<SourceDestType>();
					BsonArray sourceDBList = extObject.getArray("sourceList");
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
					aeet.setSourceList(slt);
				}
				// DestinationList
				if (extObject.get("destinationList") != null) {
					// Source Dest Type : Key / Value
					DestinationListType dlt = new DestinationListType();
					List<SourceDestType> sdtList = new ArrayList<SourceDestType>();
					BsonArray destinationDBList = extObject.getArray("destinationList");
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
					aeet.setDestinationList(dlt);
				}
				// extension2
				if (extObject.get("extension") != null) {
					AggregationEventExtension2Type aee2t = new AggregationEventExtension2Type();
					BsonDocument extension = extObject.getDocument("extension");
					aee2t = putAggregationExtension(aee2t, extension);
					aeet.setExtension(aee2t);
				}
				aggregationEventType.setExtension(aeet);
			}
			return aggregationEventType;
		} catch (DatatypeConfigurationException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
		}

		return null;
	}
}
