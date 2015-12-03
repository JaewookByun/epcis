package org.oliot.epcis.serde.mongodb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.axis.message.MessageElement;
import org.oliot.gcp.core.CodeParser;
import org.oliot.model.epcis.AggregationEventExtension2Type;
import org.oliot.model.epcis.AggregationEventExtensionType;
import org.oliot.model.epcis.BusinessLocationExtensionType;
import org.oliot.model.epcis.BusinessLocationType;
import org.oliot.model.epcis.BusinessTransactionType;
import org.oliot.model.epcis.DestinationListType;
import org.oliot.model.epcis.EPCISEventExtensionType;
import org.oliot.model.epcis.ILMDExtensionType;
import org.oliot.model.epcis.ObjectEventExtension2Type;
import org.oliot.model.epcis.ObjectEventExtensionType;
import org.oliot.model.epcis.QuantityElementType;
import org.oliot.model.epcis.QuantityEventExtensionType;
import org.oliot.model.epcis.QuantityListType;
import org.oliot.model.epcis.ReadPointType;
import org.oliot.model.epcis.SensorEventExtensionType;
import org.oliot.model.epcis.SourceDestType;
import org.oliot.model.epcis.SourceListType;
import org.oliot.model.epcis.TransactionEventExtension2Type;
import org.oliot.model.epcis.TransactionEventExtensionType;
import org.oliot.model.epcis.TransformationEventExtensionType;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

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

public class MongoWriterUtil {

	static String getInstanceEPC(String code, Integer gcpLength){
		if( gcpLength == null ){
			return code;
		}
		CodeParser codeParser = new CodeParser();
		HashMap<String, String> collection = codeParser.parse(code, gcpLength.intValue());
		if( collection.containsKey("sgtin") ){
			return collection.get("sgtin");
		}
		return code;
	}
	
	static String getClassEPC(String code, Integer gcpLength){
		if( gcpLength == null ){
			return code;
		}
		CodeParser codeParser = new CodeParser();
		HashMap<String, String> collection = codeParser.parse(code, gcpLength.intValue());
		if( collection.containsKey("lgtin") ){
			return collection.get("lgtin");
		}
		if( collection.containsKey("gtin")){
			return collection.get("gtin");
		}
		return code;
	}
	
	static String getLocationEPC(String code, Integer gcpLength){
		if( gcpLength == null ){
			return code;
		}
		CodeParser codeParser = new CodeParser();
		HashMap<String, String> collection = codeParser.parse(code, gcpLength.intValue());
		if( collection.containsKey("sgln") ){
			return collection.get("sgln");
		}
		return code;
	}
	
	static DBObject getDBObjectFromMessageElement(MessageElement any) {
		NamedNodeMap attributes = any.getAttributes();
		DBObject attrObject = new BasicDBObject();
		for (int i = 0; i < attributes.getLength(); i++) {
			Attr attr = (Attr) attributes.item(i);

			String attrName = attr.getNodeName();
			String attrValue = attr.getNodeValue();
			attrObject.put(attrName, attrValue);
		}
		return attrObject;
	}

	static DBObject getBaseExtensionObject(
			EPCISEventExtensionType baseExtensionType) {
		DBObject baseExtension = new BasicDBObject();
		if (baseExtensionType.getAny() != null
				&& baseExtensionType.getAny().isEmpty() == false) {
			List<Object> objList = baseExtensionType.getAny();
			Map<String, String> map2Save = getAnyMap(objList);
			if (map2Save.isEmpty() == false)
				baseExtension.put("any", map2Save);
		}

		if (baseExtensionType.getOtherAttributes() != null
				&& baseExtensionType.getOtherAttributes().isEmpty() == false) {
			Map<QName, String> map = baseExtensionType.getOtherAttributes();
			Map<String, String> map2Save = getOtherAttributesMap(map);
			if (map2Save.isEmpty() == false)
				baseExtension.put("otherAttributes", map2Save);
		}
		return baseExtension;
	}

	static DBObject getReadPointObject(ReadPointType readPointType, Integer gcpLength) {
		DBObject readPoint = new BasicDBObject();
		if (readPointType.getId() != null)
			readPoint.put("id", getLocationEPC(readPointType.getId(),gcpLength));
		// ReadPoint ExtensionType is not currently supported
		/*
		 * ReadPointExtensionType readPointExtensionType = readPointType
		 * .getExtension(); if (readPointExtensionType != null) { DBObject
		 * extension = new BasicDBObject(); if (readPointExtensionType.getAny()
		 * != null) { Map<String, String> map2Save = new HashMap<String,
		 * String>(); List<Object> objList = readPointExtensionType.getAny();
		 * for (int i = 0; i < objList.size(); i++) { Object obj =
		 * objList.get(i); if (obj instanceof Element) { Element element =
		 * (Element) obj; if (element.getFirstChild() != null) { String name =
		 * element.getLocalName(); String value = element.getFirstChild()
		 * .getTextContent(); map2Save.put(name, value); } } } if (map2Save !=
		 * null) extension.put("any", map2Save); }
		 * 
		 * if (readPointExtensionType.getOtherAttributes() != null) { Map<QName,
		 * String> map = readPointExtensionType .getOtherAttributes();
		 * Map<String, String> map2Save = new HashMap<String, String>();
		 * Iterator<QName> iter = map.keySet().iterator(); while
		 * (iter.hasNext()) { QName qName = iter.next(); String value =
		 * map.get(qName); map2Save.put(qName.toString(), value); }
		 * extension.put("otherAttributes", map2Save); }
		 * readPoint.put("extension", extension); }
		 */
		return readPoint;
	}

	static DBObject getBizLocationObject(BusinessLocationType bizLocationType, Integer gcpLength) {
		DBObject bizLocation = new BasicDBObject();
		if (bizLocationType.getId() != null)
			bizLocation.put("id", getLocationEPC(bizLocationType.getId(),gcpLength));

		BusinessLocationExtensionType bizLocationExtensionType = bizLocationType
				.getExtension();
		if (bizLocationExtensionType != null) {
			DBObject extension = new BasicDBObject();
			if (bizLocationExtensionType.getAny() != null) {
				Map<String, String> map2Save = new HashMap<String, String>();
				List<Object> objList = bizLocationExtensionType.getAny();
				for (int i = 0; i < objList.size(); i++) {
					Object obj = objList.get(i);
					if (obj instanceof Element) {
						Element element = (Element) obj;
						if (element.getFirstChild() != null) {
							String name = element.getLocalName();
							String value = element.getFirstChild()
									.getTextContent();
							map2Save.put(name, value);
						}
					}
				}
				if (map2Save != null)
					extension.put("any", map2Save);
			}

			if (bizLocationExtensionType.getOtherAttributes() != null) {
				Map<QName, String> map = bizLocationExtensionType
						.getOtherAttributes();
				Map<String, String> map2Save = new HashMap<String, String>();
				Iterator<QName> iter = map.keySet().iterator();
				while (iter.hasNext()) {
					QName qName = iter.next();
					String value = map.get(qName);
					map2Save.put(qName.toString(), value);
				}
				extension.put("otherAttributes", map2Save);
			}
			bizLocation.put("extension", extension);
		}

		return bizLocation;
	}

	static List<DBObject> getBizTransactionObjectList(
			List<BusinessTransactionType> bizList) {
		List<DBObject> bizTranList = new ArrayList<DBObject>();
		for (int i = 0; i < bizList.size(); i++) {
			BusinessTransactionType bizTranType = bizList.get(i);
			if (bizTranType.getType() != null && bizTranType.getValue() != null) {
				DBObject dbObj = new BasicDBObject();
				dbObj.put(bizTranType.getType(), bizTranType.getValue());
				bizTranList.add(dbObj);
			}
		}
		return bizTranList;
	}

	static DBObject getAggregationEventExtensionObject(
			AggregationEventExtensionType oee, Integer gcpLength) {
		DBObject extension = new BasicDBObject();
		if (oee.getChildQuantityList() != null) {
			QuantityListType qetl = oee.getChildQuantityList();
			List<QuantityElementType> qetList = qetl.getQuantityElement();
			List<DBObject> quantityList = new ArrayList<DBObject>();
			for (int i = 0; i < qetList.size(); i++) {
				DBObject quantity = new BasicDBObject();
				QuantityElementType qet = qetList.get(i);
				if (qet.getEpcClass() != null)
					quantity.put("epcClass", getClassEPC(qet.getEpcClass().toString(),gcpLength));
				quantity.put("quantity", qet.getQuantity());
				if (qet.getUom() != null)
					quantity.put("uom", qet.getUom().toString());
				quantityList.add(quantity);
			}
			extension.put("childQuantityList", quantityList);
		}

		if (oee.getSourceList() != null) {
			SourceListType sdtl = oee.getSourceList();
			List<SourceDestType> sdtList = sdtl.getSource();
			List<DBObject> dbList = new ArrayList<DBObject>();
			for (int i = 0; i < sdtList.size(); i++) {
				SourceDestType sdt = sdtList.get(i);
				DBObject dbObj = new BasicDBObject();
				dbObj.put(sdt.getType(), sdt.getValue());
				dbList.add(dbObj);
			}
			extension.put("sourceList", dbList);
		}
		if (oee.getDestinationList() != null) {
			DestinationListType sdtl = oee.getDestinationList();
			List<SourceDestType> sdtList = sdtl.getDestination();
			List<DBObject> dbList = new ArrayList<DBObject>();
			for (int i = 0; i < sdtList.size(); i++) {
				SourceDestType sdt = sdtList.get(i);
				DBObject dbObj = new BasicDBObject();
				dbObj.put(sdt.getType(), sdt.getValue());
				dbList.add(dbObj);
			}
			extension.put("destinationList", dbList);
		}
		if (oee.getExtension() != null) {
			AggregationEventExtension2Type extension2Type = oee.getExtension();
			DBObject extension2 = new BasicDBObject();
			if (extension2Type.getAny() != null) {
				List<Object> objList = extension2Type.getAny();
				Map<String, String> map2Save = getAnyMap(objList);
				if (map2Save.isEmpty() == false)
					extension2.put("any", map2Save);
			}

			if (extension2Type.getOtherAttributes() != null) {
				Map<QName, String> map = extension2Type.getOtherAttributes();
				Map<String, String> map2Save = getOtherAttributesMap(map);
				if (map2Save.isEmpty() == false)
					extension2.put("otherAttributes", map2Save);
			}
			extension.put("extension", extension2);
		}
		return extension;
	}

	static Map<String, String> getILMDExtensionMap(
			ILMDExtensionType ilmdExtension) {
		List<Object> objList = ilmdExtension.getAny();
		Map<String, String> map2Save = getAnyMap(objList);
		return map2Save;
	}

	static DBObject getObjectEventExtensionObject(ObjectEventExtensionType oee, Integer gcpLength) {
		DBObject extension = new BasicDBObject();
		if (oee.getQuantityList() != null) {
			QuantityListType qetl = oee.getQuantityList();
			List<QuantityElementType> qetList = qetl.getQuantityElement();
			List<DBObject> quantityList = new ArrayList<DBObject>();
			for (int i = 0; i < qetList.size(); i++) {
				DBObject quantity = new BasicDBObject();
				QuantityElementType qet = qetList.get(i);
				if (qet.getEpcClass() != null)
					quantity.put("epcClass", getClassEPC(qet.getEpcClass().toString(),gcpLength));
				quantity.put("quantity", qet.getQuantity());
				if (qet.getUom() != null)
					quantity.put("uom", qet.getUom().toString());
				quantityList.add(quantity);
			}
			extension.put("quantityList", quantityList);
		}
		if (oee.getSourceList() != null) {
			SourceListType sdtl = oee.getSourceList();
			List<SourceDestType> sdtList = sdtl.getSource();
			List<DBObject> dbList = new ArrayList<DBObject>();
			for (int i = 0; i < sdtList.size(); i++) {
				SourceDestType sdt = sdtList.get(i);
				DBObject dbObj = new BasicDBObject();
				dbObj.put(sdt.getType(), sdt.getValue());
				dbList.add(dbObj);
			}
			extension.put("sourceList", dbList);
		}
		if (oee.getDestinationList() != null) {
			DestinationListType sdtl = oee.getDestinationList();
			List<SourceDestType> sdtList = sdtl.getDestination();
			List<DBObject> dbList = new ArrayList<DBObject>();
			for (int i = 0; i < sdtList.size(); i++) {
				SourceDestType sdt = sdtList.get(i);
				DBObject dbObj = new BasicDBObject();
				dbObj.put(sdt.getType(), sdt.getValue());
				dbList.add(dbObj);
			}
			extension.put("destinationList", dbList);
		}
		if (oee.getExtension() != null) {
			ObjectEventExtension2Type extension2Type = oee.getExtension();
			DBObject extension2 = new BasicDBObject();
			if (extension2Type.getAny() != null) {
				List<Object> objList = extension2Type.getAny();
				Map<String, String> map2Save = getAnyMap(objList);
				if (map2Save != null)
					extension2.put("any", map2Save);
			}

			if (extension2Type.getOtherAttributes() != null) {
				Map<QName, String> map = extension2Type.getOtherAttributes();
				Map<String, String> map2Save = getOtherAttributesMap(map);
				if (map2Save.isEmpty() == false)
					extension2.put("otherAttributes", map2Save);
			}
			extension.put("extension", extension2);
		}
		return extension;
	}

	static DBObject getQuantityEventExtensionObject(
			QuantityEventExtensionType oee) {
		DBObject extension = new BasicDBObject();
		if (oee.getAny() != null) {
			List<Object> objList = oee.getAny();
			Map<String, String> map2Save = getAnyMap(objList);
			if (map2Save != null)
				extension.put("any", map2Save);
		}

		if (oee.getOtherAttributes() != null) {
			Map<QName, String> map = oee.getOtherAttributes();
			Map<String, String> map2Save = getOtherAttributesMap(map);
			if (map2Save.isEmpty() == false)
				extension.put("otherAttributes", map2Save);
		}
		return extension;
	}

	static DBObject getSensorEventExtensionObject(SensorEventExtensionType oee) {
		DBObject extension = new BasicDBObject();
		if (oee.getAny() != null) {
			List<Object> objList = oee.getAny();
			Map<String, String> map2Save = getAnyMap(objList);
			if (map2Save != null)
				extension.put("any", map2Save);
		}

		if (oee.getOtherAttributes() != null) {
			Map<QName, String> map = oee.getOtherAttributes();
			Map<String, String> map2Save = getOtherAttributesMap(map);
			if (map2Save.isEmpty() == false)
				extension.put("otherAttributes", map2Save);
		}
		return extension;
	}

	static DBObject getTransactionEventExtensionObject(
			TransactionEventExtensionType oee, Integer gcpLength) {
		DBObject extension = new BasicDBObject();
		if (oee.getQuantityList() != null) {
			QuantityListType qetl = oee.getQuantityList();
			List<QuantityElementType> qetList = qetl.getQuantityElement();
			List<DBObject> quantityList = new ArrayList<DBObject>();
			for (int i = 0; i < qetList.size(); i++) {
				DBObject quantity = new BasicDBObject();
				QuantityElementType qet = qetList.get(i);
				if (qet.getEpcClass() != null)
					quantity.put("epcClass", getClassEPC(qet.getEpcClass().toString(),gcpLength));
				quantity.put("quantity", qet.getQuantity());
				if (qet.getUom() != null)
					quantity.put("uom", qet.getUom().toString());
				quantityList.add(quantity);
			}
			extension.put("quantityList", quantityList);
		}
		if (oee.getSourceList() != null) {
			SourceListType sdtl = oee.getSourceList();
			List<SourceDestType> sdtList = sdtl.getSource();
			List<DBObject> dbList = new ArrayList<DBObject>();
			for (int i = 0; i < sdtList.size(); i++) {
				SourceDestType sdt = sdtList.get(i);
				DBObject dbObj = new BasicDBObject();
				dbObj.put(sdt.getType(), sdt.getValue());
				dbList.add(dbObj);
			}
			extension.put("sourceList", dbList);
		}
		if (oee.getDestinationList() != null) {
			DestinationListType sdtl = oee.getDestinationList();
			List<SourceDestType> sdtList = sdtl.getDestination();
			List<DBObject> dbList = new ArrayList<DBObject>();
			for (int i = 0; i < sdtList.size(); i++) {
				SourceDestType sdt = sdtList.get(i);
				DBObject dbObj = new BasicDBObject();
				dbObj.put(sdt.getType(), sdt.getValue());
				dbList.add(dbObj);
			}
			extension.put("destinationList", dbList);
		}
		if (oee.getExtension() != null) {
			TransactionEventExtension2Type extension2Type = oee.getExtension();
			DBObject extension2 = new BasicDBObject();
			if (extension2Type.getAny() != null) {
				List<Object> objList = extension2Type.getAny();
				Map<String, String> map2Save = getAnyMap(objList);
				if (map2Save != null)
					extension2.put("any", map2Save);
			}

			if (extension2Type.getOtherAttributes() != null) {
				Map<QName, String> map = extension2Type.getOtherAttributes();
				Map<String, String> map2Save = getOtherAttributesMap(map);
				if (map2Save.isEmpty() == false)
					extension2.put("otherAttributes", map2Save);
			}
			extension.put("extension", extension2);
		}
		return extension;
	}

	static List<DBObject> getQuantityObjectList(
			List<QuantityElementType> qetList, Integer gcpLength) {
		List<DBObject> quantityList = new ArrayList<DBObject>();
		for (int i = 0; i < qetList.size(); i++) {
			DBObject quantity = new BasicDBObject();
			QuantityElementType qet = qetList.get(i);
			if (qet.getEpcClass() != null)
				quantity.put("epcClass", getClassEPC(qet.getEpcClass().toString(),gcpLength));
			quantity.put("quantity", qet.getQuantity());
			if (qet.getUom() != null)
				quantity.put("uom", qet.getUom().toString());
			quantityList.add(quantity);
		}
		return quantityList;
	}

	static List<DBObject> getSourceDestObjectList(List<SourceDestType> sdtList) {
		List<DBObject> dbList = new ArrayList<DBObject>();
		for (int i = 0; i < sdtList.size(); i++) {
			SourceDestType sdt = sdtList.get(i);
			DBObject dbObj = new BasicDBObject();
			dbObj.put(sdt.getType(), sdt.getValue());
			dbList.add(dbObj);
		}
		return dbList;
	}

	static DBObject getTransformationEventExtensionObject(
			TransformationEventExtensionType oee) {
		DBObject extension = new BasicDBObject();
		if (oee.getAny() != null) {
			List<Object> objList = oee.getAny();
			Map<String, String> map2Save = getAnyMap(objList);
			if (map2Save != null)
				extension.put("any", map2Save);
		}

		if (oee.getOtherAttributes() != null) {
			Map<QName, String> map = oee.getOtherAttributes();
			Map<String, String> map2Save = getOtherAttributesMap(map);
			if (map2Save != null)
				extension.put("otherAttributes", map2Save);
		}
		return extension;
	}

	static Map<String, String> getAnyMap(List<Object> objList) {
		Map<String, String> map2Save = new HashMap<String, String>();
		for (int i = 0; i < objList.size(); i++) {
			Object obj = objList.get(i);
			if (obj instanceof Element) {
				Element element = (Element) obj;
				if (element.getFirstChild() != null) {
					String name = element.getNodeName();
					// Process Namespace
					String[] checkArr = name.split(":");
					if (checkArr.length == 2) {
						map2Save.put("@" + checkArr[0],
								element.getNamespaceURI());
					}
					String value = element.getFirstChild().getTextContent();
					map2Save.put(name, value);
				}
			}
		}
		return map2Save;
	}

	static Map<String, String> getOtherAttributesMap(Map<QName, String> map) {
		Map<String, String> map2Save = new HashMap<String, String>();
		Iterator<QName> iter = map.keySet().iterator();
		while (iter.hasNext()) {
			QName qName = iter.next();
			String value = map.get(qName);
			map2Save.put(qName.toString(), value);
		}
		return map2Save;
	}
}
