package org.oliot.epcis.converter.mongodb;

import static org.oliot.epcis.converter.mongodb.MongoReaderUtil.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.oliot.model.epcis.ActionType;
import org.oliot.model.epcis.BusinessLocationExtensionType;
import org.oliot.model.epcis.BusinessLocationType;
import org.oliot.model.epcis.BusinessTransactionListType;
import org.oliot.model.epcis.BusinessTransactionType;
import org.oliot.model.epcis.DestinationListType;
import org.oliot.model.epcis.EPC;
import org.oliot.model.epcis.EPCISEventExtensionType;
import org.oliot.model.epcis.EPCListType;
import org.oliot.model.epcis.ILMDType;
import org.oliot.model.epcis.ObjectEventExtension2Type;
import org.oliot.model.epcis.ObjectEventExtensionType;
import org.oliot.model.epcis.ObjectEventType;
import org.oliot.model.epcis.QuantityElementType;
import org.oliot.model.epcis.QuantityListType;
import org.oliot.model.epcis.ReadPointExtensionType;
import org.oliot.model.epcis.ReadPointType;
import org.oliot.model.epcis.SourceDestType;
import org.oliot.model.epcis.SourceListType;

/**
 * Copyright (C) 2014-2016 Jaewook Byun
 *
 * This project is part of Oliot open source (http://oliot.org). Oliot EPCIS
 * v1.2.x is Java Web Service complying with Electronic Product Code Information
 * Service (EPCIS) v1.2.
 *
 * @author Jaewook Byun, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         bjw0829@kaist.ac.kr, bjw0829@gmail.com
 */

public class ObjectEventReadConverter {

	public ObjectEventType convert(BsonDocument dbObject) {

		ObjectEventType objectEventType = new ObjectEventType();
		int zone = 0;
		if (dbObject.get("eventTimeZoneOffset") != null) {
			String eventTimeZoneOffset = dbObject.getString("eventTimeZoneOffset").getValue();
			objectEventType.setEventTimeZoneOffset(eventTimeZoneOffset);
			if (eventTimeZoneOffset.split(":").length == 2) {
				zone = Integer.parseInt(eventTimeZoneOffset.split(":")[0]);
			}
		}
		if (dbObject.get("eventTime") != null) {
			objectEventType.setEventTime(getXMLGregorianCalendar(dbObject.getDateTime("eventTime")));
		}
		if (dbObject.get("recordTime") != null) {
			objectEventType.setRecordTime(getXMLGregorianCalendar(dbObject.getDateTime("recordTime")));
		}
		if (dbObject.get("epcList") != null) {
			BsonArray epcListM = dbObject.getArray("epcList");
			EPCListType epcListType = new EPCListType();
			List<EPC> epcs = new ArrayList<EPC>();
			for (int i = 0; i < epcListM.size(); i++) {
				EPC epc = new EPC();
				BsonDocument epcObject = epcListM.get(i).asDocument();
				epc.setValue(epcObject.getString("epc").getValue());
				epcs.add(epc);
			}
			epcListType.setEpc(epcs);
			objectEventType.setEpcList(epcListType);
		}
		if (dbObject.get("action") != null) {
			objectEventType.setAction(ActionType.fromValue(dbObject.getString("action").getValue()));
		}
		if (dbObject.get("bizStep") != null) {
			objectEventType.setBizStep(dbObject.getString("bizStep").getValue());
		}
		if (dbObject.get("disposition") != null) {
			objectEventType.setDisposition(dbObject.getString("disposition").getValue());
		}
		if (dbObject.get("baseExtension") != null) {
			EPCISEventExtensionType eeet = new EPCISEventExtensionType();
			BsonDocument baseExtension = dbObject.getDocument("baseExtension");
			eeet = putEPCISExtension(eeet, baseExtension);
			objectEventType.setBaseExtension(eeet);
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
			objectEventType.setReadPoint(readPointType);
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
			objectEventType.setBizLocation(bizLocationType);
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
			objectEventType.setBizTransactionList(btlt);
		}

		/*
		 * ILMD field moves to extension if (dbObject.get("ilmd") != null) {
		 * ILMDType ilmd = new ILMDType(); BsonDocument anyObject =
		 * dbObject.getDocument("ilmd"); ilmd = putILMD(ilmd, anyObject);
		 * objectEventType.setIlmd(ilmd); }
		 */
		// Vendor Extension
		// Supported Type: int, long, float, double, boolean, time
		// xsi: int, long, float, double, boolean, dateTime
		if (dbObject.get("any") != null) {
			BsonDocument anyObject = dbObject.getDocument("any");
			List<Object> any = putAny(anyObject, null);
			objectEventType.setAny(any);
		}

		// EventID and ErrorDeclaration
		objectEventType.setBaseExtension(putEPCISEventExtensionType(dbObject, zone));

		// Extension Field
		if (dbObject.get("extension") != null) {
			ObjectEventExtensionType oeet = new ObjectEventExtensionType();
			BsonDocument extObject = dbObject.getDocument("extension");
			// Quantity
			if (extObject.get("quantityList") != null) {
				QuantityListType qlt = new QuantityListType();
				BsonArray quantityDBList = extObject.getArray("quantityList");
				List<QuantityElementType> qetList = putQuantityElementTypeList(quantityDBList);
				qlt.setQuantityElement(qetList);
				oeet.setQuantityList(qlt);
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
				oeet.setSourceList(slt);
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
				oeet.setDestinationList(dlt);
			}

			if (extObject.get("ilmd") != null) {
				ILMDType ilmd = new ILMDType();
				BsonDocument ilmdObject = extObject.getDocument("ilmd");
				if (ilmdObject.containsKey("any")) {
					ilmd = putILMD(ilmd, ilmdObject.get("any").asDocument());
					oeet.setIlmd(ilmd);
				}
			}

			// extension2
			if (extObject.get("extension") != null) {
				ObjectEventExtension2Type oee2t = new ObjectEventExtension2Type();
				BsonDocument extension = extObject.getDocument("extension");
				oee2t = putObjectExtension(oee2t, extension);
				oeet.setExtension(oee2t);
			}
			objectEventType.setExtension(oeet);
		}
		return objectEventType;
	}
}
