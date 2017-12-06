package org.oliot.epcis.converter.mongodb;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.axis.message.MessageElement;
import org.apache.log4j.Level;
import org.bson.BsonArray;
import org.bson.BsonBoolean;
import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.gcp.core.AICodeParser;
import org.oliot.model.epcis.AggregationEventExtension2Type;
import org.oliot.model.epcis.AggregationEventExtensionType;
import org.oliot.model.epcis.BusinessLocationExtensionType;
import org.oliot.model.epcis.BusinessLocationType;
import org.oliot.model.epcis.BusinessTransactionType;
import org.oliot.model.epcis.CorrectiveEventIDsType;
import org.oliot.model.epcis.DestinationListType;
import org.oliot.model.epcis.EPC;
import org.oliot.model.epcis.EPCISEventExtensionType;
import org.oliot.model.epcis.ErrorDeclarationType;
import org.oliot.model.epcis.ILMDExtensionType;
import org.oliot.model.epcis.ILMDType;
import org.oliot.model.epcis.ObjectEventExtension2Type;
import org.oliot.model.epcis.ObjectEventExtensionType;
import org.oliot.model.epcis.QuantityElementType;
import org.oliot.model.epcis.QuantityEventExtensionType;
import org.oliot.model.epcis.QuantityListType;
import org.oliot.model.epcis.ReadPointType;
import org.oliot.model.epcis.SourceDestType;
import org.oliot.model.epcis.SourceListType;
import org.oliot.model.epcis.TransactionEventExtension2Type;
import org.oliot.model.epcis.TransactionEventExtensionType;
import org.oliot.model.epcis.TransformationEventExtensionType;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

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

public class MongoWriterUtil {

	static String getInstanceEPC(String code, Integer gcpLength) {
		if (gcpLength == null) {
			return code;
		}
		AICodeParser codeParser = new AICodeParser();
		HashMap<String, String> collection = codeParser.parse(code, gcpLength.intValue());
		if (collection.containsKey("sgtin")) {
			return collection.get("sgtin");
		} else if (collection.containsKey("sscc")) {
			return collection.get("sscc");
		} else if (collection.containsKey("grai")) {
			return collection.get("grai");
		} else if (collection.containsKey("giai")) {
			return collection.get("giai");
		} else if (collection.containsKey("gsrn")) {
			return collection.get("gsrn");
		} else if (collection.containsKey("gdti")) {
			return collection.get("gdti");
		}
		return code;
	}

	static String getClassEPC(String code, Integer gcpLength) {
		if (gcpLength == null) {
			return code;
		}
		AICodeParser codeParser = new AICodeParser();
		HashMap<String, String> collection = codeParser.parse(code, gcpLength.intValue());

		// Priority LGTIN -> GTIN
		if (collection.containsKey("lgtin")) {
			return collection.get("lgtin");
		}
		if (collection.containsKey("gtin")) {
			return collection.get("gtin");
		}
		return code;
	}

	static String getLocationEPC(String code, Integer gcpLength) {
		if (gcpLength == null) {
			return code;
		}
		AICodeParser codeParser = new AICodeParser();
		HashMap<String, String> collection = codeParser.parse(code, gcpLength.intValue());
		if (collection.containsKey("sgln")) {
			return collection.get("sgln");
		}
		return code;
	}

	static String getSourceDestinationEPC(String code, Integer gcpLength) {
		if (gcpLength == null) {
			return code;
		}
		AICodeParser codeParser = new AICodeParser();
		HashMap<String, String> collection = codeParser.parse(code, gcpLength.intValue());
		if (collection.containsKey("sgln")) {
			return collection.get("sgln");
		} else if (collection.containsKey("gsrn")) {
			return collection.get("gsrn");
		}
		return code;
	}

	static String getVocabularyEPC(String vocType, String code, Integer gcpLength) {
		if (vocType == null) {
			return code;
		}
		if (gcpLength == null) {
			return code;
		}
		AICodeParser codeParser = new AICodeParser();
		HashMap<String, String> collection = codeParser.parse(code, gcpLength.intValue());

		if (vocType.equals("urn:epcglobal:epcis:vtype:BusinessLocation")) {
			if (collection.containsKey("sgln")) {
				return collection.get("sgln");
			}
		} else if (vocType.equals("urn:epcglobal:epcis:vtype:ReadPoint")) {
			if (collection.containsKey("sgln")) {
				return collection.get("sgln");
			}
		} else if (vocType.equals("urn:epcglobal:epcis:vtype:EPCClass")) {
			if (collection.containsKey("lgtin")) {
				return collection.get("lgtin");
			} else if (collection.containsKey("gtin")) {
				return collection.get("lgtin");
			}
		} else if (vocType.equals("urn:epcglobal:epcis:vtype:SourceDest")) {
			if (collection.containsKey("sgln")) {
				return collection.get("sgln");
			} else if (collection.containsKey("gsrn")) {
				return collection.get("gsrn");
			}
		} else if (vocType.equals("urn:epcglobal:epcis:vtype:EPCInstance")) {
			if (collection.containsKey("sgtin")) {
				return collection.get("sgtin");
			} else if (collection.containsKey("sscc")) {
				return collection.get("sscc");
			} else if (collection.containsKey("grai")) {
				return collection.get("grai");
			} else if (collection.containsKey("giai")) {
				return collection.get("giai");
			} else if (collection.containsKey("gsrn")) {
				return collection.get("gsrn");
			} else if (collection.containsKey("gdti")) {
				return collection.get("gdti");
			}
		}
		return code;
	}

	static BsonDocument getDBObjectFromMessageElement(MessageElement any) {
		NamedNodeMap attributes = any.getAttributes();
		BsonDocument attrObject = new BsonDocument();
		for (int i = 0; i < attributes.getLength(); i++) {
			Attr attr = (Attr) attributes.item(i);

			String attrName = attr.getNodeName();
			String attrValue = attr.getNodeValue();
			attrObject.put(attrName, new BsonString(attrValue));
		}
		return attrObject;
	}

	static BsonDocument getBaseExtensionObject(EPCISEventExtensionType baseExtensionType) {
		BsonDocument baseExtension = new BsonDocument();
		/*
		 * May be deprecated if (baseExtensionType.getAny() != null &&
		 * baseExtensionType.getAny().isEmpty() == false) { List<Object> objList
		 * = baseExtensionType.getAny(); BsonDocument map2Save =
		 * getAnyMap(objList); if (map2Save.isEmpty() == false)
		 * baseExtension.put("any", map2Save); }
		 */
		if (baseExtensionType.getOtherAttributes() != null
				&& baseExtensionType.getOtherAttributes().isEmpty() == false) {
			Map<QName, String> map = baseExtensionType.getOtherAttributes();
			BsonDocument map2Save = getOtherAttributesMap(map);
			if (map2Save.isEmpty() == false)
				baseExtension.put("otherAttributes", map2Save);
		}
		return baseExtension;
	}

	static BsonDocument getReadPointObject(ReadPointType readPointType, Integer gcpLength) {
		BsonDocument readPoint = new BsonDocument();
		if (readPointType.getId() != null)
			readPoint.put("id", new BsonString(getLocationEPC(readPointType.getId(), gcpLength)));
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

	static BsonDocument getBizLocationObject(BusinessLocationType bizLocationType, Integer gcpLength) {
		BsonDocument bizLocation = new BsonDocument();
		if (bizLocationType.getId() != null)
			bizLocation.put("id", new BsonString(getLocationEPC(bizLocationType.getId(), gcpLength)));

		BusinessLocationExtensionType bizLocationExtensionType = bizLocationType.getExtension();
		if (bizLocationExtensionType != null) {
			BsonDocument extension = new BsonDocument();
			if (bizLocationExtensionType.getAny() != null) {
				BsonDocument map2Save = new BsonDocument();
				List<Object> objList = bizLocationExtensionType.getAny();
				for (int i = 0; i < objList.size(); i++) {
					Object obj = objList.get(i);
					if (obj instanceof Element) {
						Element element = (Element) obj;
						if (element.getFirstChild() != null) {
							String name = element.getLocalName();
							String value = element.getFirstChild().getTextContent();
							map2Save.put(name, new BsonString(value));
						}
					}
				}
				if (map2Save != null)
					extension.put("any", map2Save);
			}

			if (bizLocationExtensionType.getOtherAttributes() != null) {
				Map<QName, String> map = bizLocationExtensionType.getOtherAttributes();
				BsonDocument map2Save = new BsonDocument();
				Iterator<QName> iter = map.keySet().iterator();
				while (iter.hasNext()) {
					QName qName = iter.next();
					String value = map.get(qName);
					map2Save.put(qName.toString(), new BsonString(value));
				}
				extension.put("otherAttributes", map2Save);
			}
			bizLocation.put("extension", extension);
		}

		return bizLocation;
	}

	static BsonArray getBizTransactionObjectList(List<BusinessTransactionType> bizList) {
		BsonArray bizTranList = new BsonArray();
		for (int i = 0; i < bizList.size(); i++) {
			BusinessTransactionType bizTranType = bizList.get(i);
			if (bizTranType.getType() != null && bizTranType.getValue() != null) {
				BsonDocument dbObj = new BsonDocument();
				dbObj.put(bizTranType.getType(), new BsonString(
						bizTranType.getValue().replaceAll("\n", "").replaceAll("\t", "").replaceAll("\\s", "")));
				bizTranList.add(dbObj);
			}
		}
		return bizTranList;
	}

	static BsonDocument getAggregationEventExtensionObject(AggregationEventExtensionType oee, Integer gcpLength) {
		BsonDocument extension = new BsonDocument();
		if (oee.getChildQuantityList() != null) {
			QuantityListType qetl = oee.getChildQuantityList();
			List<QuantityElementType> qetList = qetl.getQuantityElement();
			BsonArray quantityList = new BsonArray();
			for (int i = 0; i < qetList.size(); i++) {
				BsonDocument quantity = new BsonDocument();
				QuantityElementType qet = qetList.get(i);
				if (qet.getEpcClass() != null)
					quantity.put("epcClass", new BsonString(getClassEPC(qet.getEpcClass().toString(), gcpLength)));
				if (qet.getQuantity().doubleValue() != 0) {
					quantity.put("quantity", new BsonDouble(qet.getQuantity().doubleValue()));
				}
				if (qet.getUom() != null)
					quantity.put("uom", new BsonString(qet.getUom().toString()));
				quantityList.add(quantity);
			}
			extension.put("childQuantityList", quantityList);
		}

		if (oee.getSourceList() != null) {
			SourceListType sdtl = oee.getSourceList();
			List<SourceDestType> sdtList = sdtl.getSource();
			BsonArray dbList = new BsonArray();
			for (int i = 0; i < sdtList.size(); i++) {
				SourceDestType sdt = sdtList.get(i);
				BsonDocument dbObj = new BsonDocument();
				dbObj.put(sdt.getType(), new BsonString(getSourceDestinationEPC(sdt.getValue(), gcpLength)));
				dbList.add(dbObj);
			}
			extension.put("sourceList", dbList);
		}
		if (oee.getDestinationList() != null) {
			DestinationListType sdtl = oee.getDestinationList();
			List<SourceDestType> sdtList = sdtl.getDestination();
			BsonArray dbList = new BsonArray();
			for (int i = 0; i < sdtList.size(); i++) {
				SourceDestType sdt = sdtList.get(i);
				BsonDocument dbObj = new BsonDocument();
				dbObj.put(sdt.getType(), new BsonString(getSourceDestinationEPC(sdt.getValue(), gcpLength)));
				dbList.add(dbObj);
			}
			extension.put("destinationList", dbList);
		}
		if (oee.getExtension() != null) {
			AggregationEventExtension2Type extension2Type = oee.getExtension();
			BsonDocument extension2 = new BsonDocument();
			if (extension2Type.getAny() != null) {
				List<Object> objList = extension2Type.getAny();
				BsonDocument map2Save = getAnyMap(objList);
				if (map2Save.isEmpty() == false)
					extension2.put("any", map2Save);
			}

			if (extension2Type.getOtherAttributes() != null) {
				Map<QName, String> map = extension2Type.getOtherAttributes();
				BsonDocument map2Save = getOtherAttributesMap(map);
				if (map2Save.isEmpty() == false)
					extension2.put("otherAttributes", map2Save);
			}
			extension.put("extension", extension2);
		}
		return extension;
	}

	static BsonDocument getILMDExtensionMap(ILMDExtensionType ilmdExtension) {
		List<Object> objList = ilmdExtension.getAny();
		BsonDocument map2Save = getAnyMap(objList);
		return map2Save;
	}

	static BsonDocument getObjectEventExtensionObject(ObjectEventExtensionType oee, Integer gcpLength) {
		BsonDocument extension = new BsonDocument();
		if (oee.getQuantityList() != null) {
			QuantityListType qetl = oee.getQuantityList();
			List<QuantityElementType> qetList = qetl.getQuantityElement();
			BsonArray quantityList = new BsonArray();
			for (int i = 0; i < qetList.size(); i++) {
				BsonDocument quantity = new BsonDocument();
				QuantityElementType qet = qetList.get(i);
				if (qet.getEpcClass() != null)
					quantity.put("epcClass", new BsonString(qet.getEpcClass().toString()));
				if (qet.getQuantity().doubleValue() != 0) {
					quantity.put("quantity", new BsonDouble(qet.getQuantity().doubleValue()));
				}
				if (qet.getUom() != null)
					quantity.put("uom", new BsonString(qet.getUom().toString()));
				quantityList.add(quantity);
			}
			extension.put("quantityList", quantityList);
		}
		if (oee.getSourceList() != null) {
			SourceListType sdtl = oee.getSourceList();
			List<SourceDestType> sdtList = sdtl.getSource();
			BsonArray dbList = new BsonArray();
			for (int i = 0; i < sdtList.size(); i++) {
				SourceDestType sdt = sdtList.get(i);
				BsonDocument dbObj = new BsonDocument();
				dbObj.put(sdt.getType(), new BsonString(sdt.getValue()));
				dbList.add(dbObj);
			}
			extension.put("sourceList", dbList);
		}
		if (oee.getDestinationList() != null) {
			DestinationListType sdtl = oee.getDestinationList();
			List<SourceDestType> sdtList = sdtl.getDestination();
			BsonArray dbList = new BsonArray();
			for (int i = 0; i < sdtList.size(); i++) {
				SourceDestType sdt = sdtList.get(i);
				BsonDocument dbObj = new BsonDocument();
				dbObj.put(sdt.getType(), new BsonString(sdt.getValue()));
				dbList.add(dbObj);
			}
			extension.put("destinationList", dbList);
		}

		if (oee.getExtension() != null) {
			ObjectEventExtension2Type extension2Type = oee.getExtension();
			BsonDocument extension2 = new BsonDocument();
			if (extension2Type.getAny() != null) {
				List<Object> objList = extension2Type.getAny();
				BsonDocument map2Save = getAnyMap(objList);
				if (map2Save != null)
					extension2.put("any", map2Save);
			}

			if (extension2Type.getOtherAttributes() != null) {
				Map<QName, String> map = extension2Type.getOtherAttributes();
				BsonDocument map2Save = getOtherAttributesMap(map);
				if (map2Save.isEmpty() == false)
					extension2.put("otherAttributes", map2Save);
			}
			extension.put("extension", extension2);
		}
		return extension;
	}
	
	static BsonDocument getObjectEventExtensionObject(ObjectEventExtensionType oee, Integer gcpLength,
			List<EPC> epcList) {
		BsonDocument extension = new BsonDocument();
		if (oee.getQuantityList() != null) {
			QuantityListType qetl = oee.getQuantityList();
			List<QuantityElementType> qetList = qetl.getQuantityElement();
			BsonArray quantityList = new BsonArray();
			for (int i = 0; i < qetList.size(); i++) {
				BsonDocument quantity = new BsonDocument();
				QuantityElementType qet = qetList.get(i);
				if (qet.getEpcClass() != null)
					quantity.put("epcClass", new BsonString(getClassEPC(qet.getEpcClass().toString(), gcpLength)));
				if (qet.getQuantity().doubleValue() != 0) {
					quantity.put("quantity", new BsonDouble(qet.getQuantity().doubleValue()));
				}
				if (qet.getUom() != null)
					quantity.put("uom", new BsonString(qet.getUom().toString()));
				quantityList.add(quantity);
			}
			extension.put("quantityList", quantityList);
		}
		if (oee.getSourceList() != null) {
			SourceListType sdtl = oee.getSourceList();
			List<SourceDestType> sdtList = sdtl.getSource();
			BsonArray dbList = new BsonArray();
			for (int i = 0; i < sdtList.size(); i++) {
				SourceDestType sdt = sdtList.get(i);
				BsonDocument dbObj = new BsonDocument();
				dbObj.put(sdt.getType(), new BsonString(getSourceDestinationEPC(sdt.getValue(), gcpLength)));
				dbList.add(dbObj);
			}
			extension.put("sourceList", dbList);
		}
		if (oee.getDestinationList() != null) {
			DestinationListType sdtl = oee.getDestinationList();
			List<SourceDestType> sdtList = sdtl.getDestination();
			BsonArray dbList = new BsonArray();
			for (int i = 0; i < sdtList.size(); i++) {
				SourceDestType sdt = sdtList.get(i);
				BsonDocument dbObj = new BsonDocument();
				dbObj.put(sdt.getType(), new BsonString(getSourceDestinationEPC(sdt.getValue(), gcpLength)));
				dbList.add(dbObj);
			}
			extension.put("destinationList", dbList);
		}

		// new in v1.2
		if (oee.getIlmd() != null) {
			ILMDType ilmd = oee.getIlmd();

			if (ilmd.getAny() != null) {
				BsonDocument map2Save = getAnyMap(ilmd.getAny());
				if (map2Save != null && map2Save.isEmpty() == false) {
					extension.put("ilmd", new BsonDocument("any", map2Save));
				}
				if (epcList != null) {
					MasterDataWriteConverter mdConverter = new MasterDataWriteConverter();
					mdConverter.capture(epcList, map2Save);
				}
			}

			/*
			 * Deprecated if (ilmd.getExtension() != null) { ILMDExtensionType
			 * ilmdExtension = ilmd.getExtension(); BsonDocument map2Save =
			 * getILMDExtensionMap(ilmdExtension); if (map2Save != null)
			 * extension.put("ilmd", map2Save); if (epcList != null) {
			 * MasterDataWriteConverter mdConverter = new
			 * MasterDataWriteConverter(); mdConverter.capture(epcList,
			 * map2Save); } }
			 */
		}

		if (oee.getExtension() != null) {
			ObjectEventExtension2Type extension2Type = oee.getExtension();
			BsonDocument extension2 = new BsonDocument();
			if (extension2Type.getAny() != null) {
				List<Object> objList = extension2Type.getAny();
				BsonDocument map2Save = getAnyMap(objList);
				if (map2Save != null)
					extension2.put("any", map2Save);
			}

			if (extension2Type.getOtherAttributes() != null) {
				Map<QName, String> map = extension2Type.getOtherAttributes();
				BsonDocument map2Save = getOtherAttributesMap(map);
				if (map2Save.isEmpty() == false)
					extension2.put("otherAttributes", map2Save);
			}
			extension.put("extension", extension2);
		}
		return extension;
	}

	static BsonDocument getQuantityEventExtensionObject(QuantityEventExtensionType oee) {
		BsonDocument extension = new BsonDocument();
		if (oee.getAny() != null) {
			List<Object> objList = oee.getAny();
			BsonDocument map2Save = getAnyMap(objList);
			if (map2Save != null)
				extension.put("any", map2Save);
		}

		if (oee.getOtherAttributes() != null) {
			Map<QName, String> map = oee.getOtherAttributes();
			BsonDocument map2Save = getOtherAttributesMap(map);
			if (map2Save.isEmpty() == false)
				extension.put("otherAttributes", map2Save);
		}
		return extension;
	}

	static BsonDocument getTransactionEventExtensionObject(TransactionEventExtensionType oee, Integer gcpLength) {
		BsonDocument extension = new BsonDocument();
		if (oee.getQuantityList() != null) {
			QuantityListType qetl = oee.getQuantityList();
			List<QuantityElementType> qetList = qetl.getQuantityElement();
			BsonArray quantityList = new BsonArray();
			for (int i = 0; i < qetList.size(); i++) {
				BsonDocument quantity = new BsonDocument();
				QuantityElementType qet = qetList.get(i);
				if (qet.getEpcClass() != null)
					quantity.put("epcClass", new BsonString(getClassEPC(qet.getEpcClass().toString(), gcpLength)));
				if (qet.getQuantity().doubleValue() != 0) {
					quantity.put("quantity", new BsonDouble(qet.getQuantity().doubleValue()));
				}
				if (qet.getUom() != null)
					quantity.put("uom", new BsonString(qet.getUom().toString()));
				quantityList.add(quantity);
			}
			extension.put("quantityList", quantityList);
		}
		if (oee.getSourceList() != null) {
			SourceListType sdtl = oee.getSourceList();
			List<SourceDestType> sdtList = sdtl.getSource();
			BsonArray dbList = new BsonArray();
			for (int i = 0; i < sdtList.size(); i++) {
				SourceDestType sdt = sdtList.get(i);
				BsonDocument dbObj = new BsonDocument();
				dbObj.put(sdt.getType(), new BsonString(getSourceDestinationEPC(sdt.getValue(), gcpLength)));
				dbList.add(dbObj);
			}
			extension.put("sourceList", dbList);
		}
		if (oee.getDestinationList() != null) {
			DestinationListType sdtl = oee.getDestinationList();
			List<SourceDestType> sdtList = sdtl.getDestination();
			BsonArray dbList = new BsonArray();
			for (int i = 0; i < sdtList.size(); i++) {
				SourceDestType sdt = sdtList.get(i);
				BsonDocument dbObj = new BsonDocument();
				dbObj.put(sdt.getType(), new BsonString(getSourceDestinationEPC(sdt.getValue(), gcpLength)));
				dbList.add(dbObj);
			}
			extension.put("destinationList", dbList);
		}
		if (oee.getExtension() != null) {
			TransactionEventExtension2Type extension2Type = oee.getExtension();
			BsonDocument extension2 = new BsonDocument();
			if (extension2Type.getAny() != null) {
				List<Object> objList = extension2Type.getAny();
				BsonDocument map2Save = getAnyMap(objList);
				if (map2Save != null)
					extension2.put("any", map2Save);
			}

			if (extension2Type.getOtherAttributes() != null) {
				Map<QName, String> map = extension2Type.getOtherAttributes();
				BsonDocument map2Save = getOtherAttributesMap(map);
				if (map2Save.isEmpty() == false)
					extension2.put("otherAttributes", map2Save);
			}
			extension.put("extension", extension2);
		}
		return extension;
	}

	static BsonArray getQuantityObjectList(List<QuantityElementType> qetList, Integer gcpLength) {
		BsonArray quantityList = new BsonArray();
		for (int i = 0; i < qetList.size(); i++) {
			BsonDocument quantity = new BsonDocument();
			QuantityElementType qet = qetList.get(i);
			if (qet.getEpcClass() != null)
				quantity.put("epcClass", new BsonString(getClassEPC(qet.getEpcClass().toString(), gcpLength)));
			if (qet.getQuantity().doubleValue() != 0) {
				quantity.put("quantity", new BsonDouble(qet.getQuantity().doubleValue()));
			}
			if (qet.getUom() != null)
				quantity.put("uom", new BsonString(qet.getUom().toString()));
			quantityList.add(quantity);
		}
		return quantityList;
	}

	static BsonArray getSourceDestObjectList(List<SourceDestType> sdtList, Integer gcpLength) {
		BsonArray dbList = new BsonArray();
		for (int i = 0; i < sdtList.size(); i++) {
			SourceDestType sdt = sdtList.get(i);
			BsonDocument dbObj = new BsonDocument();
			dbObj.put(sdt.getType(), new BsonString(getSourceDestinationEPC(sdt.getValue(), gcpLength)));
			dbList.add(dbObj);
		}
		return dbList;
	}

	static BsonDocument getTransformationEventExtensionObject(TransformationEventExtensionType oee) {
		BsonDocument extension = new BsonDocument();
		if (oee.getAny() != null) {
			List<Object> objList = oee.getAny();
			BsonDocument map2Save = getAnyMap(objList);
			if (map2Save != null)
				extension.put("any", map2Save);
		}

		if (oee.getOtherAttributes() != null) {
			Map<QName, String> map = oee.getOtherAttributes();
			BsonDocument map2Save = getOtherAttributesMap(map);
			if (map2Save != null)
				extension.put("otherAttributes", map2Save);
		}
		return extension;
	}

	static BsonDocument getAnyMap(List<Object> objList) {
		BsonDocument map2Save = new BsonDocument();
		for (int i = 0; i < objList.size(); i++) {
			Object obj = objList.get(i);
			if (obj instanceof Element) {
				Element element = (Element) obj;
				String qname = element.getNodeName();
				// Process Namespace
				String[] checkArr = qname.split(":");

				if (checkArr.length != 2)
					continue;

				String prefix = checkArr[0];
				String localName = checkArr[1];
				String namespaceURI = encodeMongoObjectKey(element.getNamespaceURI());
				String qnameKey = encodeMongoObjectKey(namespaceURI + "#" + localName);
				// checkArr[0] : example1
				// getNamespaceURI : http
				map2Save.put("@" + namespaceURI, new BsonString(prefix));

				Node firstChildNode = element.getFirstChild();
				if (firstChildNode != null) {
					if (firstChildNode instanceof Text) {
						String value = firstChildNode.getTextContent();
						value = reflectXsiType(value, element);
						map2Save.put(qnameKey, converseType(value));
					} else if (firstChildNode instanceof Element) {
						Element childNode = null;
						BsonDocument sub2Save = new BsonDocument();
						do {
							if (firstChildNode instanceof Element) {
								childNode = (Element) firstChildNode;
								String childQName = childNode.getNodeName();
								String[] childCheckArr = childQName.split(":");
								if (childCheckArr.length != 2)
									continue;

								String childPrefix = childCheckArr[0];
								String childNamespaceURI = encodeMongoObjectKey(childNode.getNamespaceURI());

								sub2Save.put("@" + childNamespaceURI, new BsonString(childPrefix));

								map2Save.put(qnameKey, getAnyMap(childNode, sub2Save));
							}
						} while ((firstChildNode = firstChildNode.getNextSibling()) != null);
					}
				}
			}
		}
		return map2Save;
	}

	// Inside recursive logic
	static BsonDocument getAnyMap(Element element, BsonDocument map2Save) {

		String qname = element.getNodeName();
		// Process Namespace
		String[] checkArr = qname.split(":");
		if (checkArr.length != 2)
			return null;

		String localName = checkArr[1];
		String namespaceURI = encodeMongoObjectKey(element.getNamespaceURI());
		String qnameKey = encodeMongoObjectKey(namespaceURI + "#" + localName);

		Node firstChildNode = element.getFirstChild();

		if (firstChildNode instanceof Text) {
			// A
			String value = firstChildNode.getTextContent();
			value = reflectXsiType(value, element);
			map2Save.put(qnameKey, converseType(value));
		} else if (firstChildNode instanceof Element) {
			// example1:b, example1:d, example1:b, example1:e
			Element childNode = null;
			BsonDocument sub2Save = new BsonDocument();
			do {
				if (firstChildNode instanceof Element) {
					childNode = (Element) firstChildNode;
					String childQName = childNode.getNodeName();
					String[] childCheckArr = childQName.split(":");
					if (childCheckArr.length != 2)
						continue;
					String childPrefix = childCheckArr[0];
					String childNamespaceURI = encodeMongoObjectKey(childNode.getNamespaceURI());
					sub2Save.put("@" + childNamespaceURI, new BsonString(childPrefix));
					map2Save.put(qnameKey, getAnyMap(childNode, sub2Save));
				}
			} while (firstChildNode.getNextSibling() != null && firstChildNode.getNextSibling() instanceof Element
					&& (firstChildNode = (Element) firstChildNode.getNextSibling()) != null);
		}

		return map2Save;
	}

	static BsonDocument getOtherAttributesMap(Map<QName, String> map) {
		BsonDocument map2Save = new BsonDocument();
		Iterator<QName> iter = map.keySet().iterator();
		while (iter.hasNext()) {
			QName qName = iter.next();
			String value = map.get(qName);
			map2Save.put(qName.toString(), new BsonString(value));
		}
		return map2Save;
	}

	static String reflectXsiType(String targetValue, Element element) {
		// xsi: int, long, float, double, boolean, dateTime
		// Wedge is evaluated before xsi
		String type = element.getAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "type");
		if (type != null && !type.isEmpty() && targetValue.indexOf('^') == -1) {
			if (type.contains("int")) {
				targetValue = targetValue.trim();
				targetValue += "^int";
			} else if (type.contains("long")) {
				targetValue = targetValue.trim();
				targetValue += "^long";
			} else if (type.contains("float")) {
				targetValue = targetValue.trim();
				targetValue += "^float";
			} else if (type.contains("double")) {
				targetValue = targetValue.trim();
				targetValue += "^double";
			} else if (type.contains("boolean")) {
				targetValue = targetValue.trim();
				targetValue += "^boolean";
			} else if (type.contains("dateTime")) {
				targetValue = targetValue.trim();
				targetValue += "^dateTime";
			}
		}
		return targetValue;
	}

	public static BsonValue converseType(String value) {
		String[] valArr = value.split("\\^");
		if (valArr.length != 2) {
			return new BsonString(value);
		}
		try {
			String type = valArr[1];
			if (type.equals("int")) {
				return new BsonInt32(Integer.parseInt(valArr[0]));
			} else if (type.equals("long")) {
				return new BsonInt64(Long.parseLong(valArr[0]));
			} else if (type.equals("double")) {
				return new BsonDouble(Double.parseDouble(valArr[0]));
			} else if (type.equals("boolean")) {
				return new BsonBoolean(Boolean.parseBoolean(valArr[0]));
			} else if (type.equals("float")) {
				return new BsonDouble(Double.parseDouble(valArr[0]));
			} else if (type.equals("dateTime")) {
				BsonDateTime time = getBsonDateTime(valArr[0]);
				if (time != null)
					return time;
				return new BsonString(value);
			} else if (type.equals("geoPoint")) {
				BsonDocument point = getBsonGeoPoint(valArr[0]);
				if (point == null)
					return new BsonString(value);
				return point;
			} else if (type.equals("geoArea")) {
				BsonDocument area = getBsonGeoArea(valArr[0]);
				if (area == null)
					return new BsonString(value);
				return area;
			} else {
				return new BsonString(value);
			}
		} catch (NumberFormatException e) {
			return new BsonString(value);
		}
	}

	static BsonDocument getErrorDeclaration(ErrorDeclarationType edt) {
		BsonDocument errorBson = new BsonDocument();
		long declarationTime = edt.getDeclarationTime().toGregorianCalendar().getTimeInMillis();
		errorBson.put("declarationTime", new BsonDateTime(declarationTime));
		// (Optional) reason
		if (edt.getReason() != null) {
			errorBson.put("reason", new BsonString(edt.getReason()));
		}
		// (Optional) correctiveEventIDs
		if (edt.getCorrectiveEventIDs() != null) {
			CorrectiveEventIDsType cIDs = edt.getCorrectiveEventIDs();
			List<String> cIDStringList = cIDs.getCorrectiveEventID();
			BsonArray correctiveIDBsonArray = new BsonArray();
			for (String cIDString : cIDStringList) {
				correctiveIDBsonArray.add(new BsonString(cIDString));
			}
			if (correctiveIDBsonArray.size() != 0) {
				errorBson.put("correctiveEventIDs", correctiveIDBsonArray);
			}
		}
		if (edt.getAny() != null) {
			BsonDocument map2Save = getAnyMap(edt.getAny());
			if (map2Save != null && map2Save.isEmpty() == false) {
				errorBson.put("any", map2Save);
			}
		}

		return errorBson;
	}

	public static BsonDateTime getBsonDateTime(String standardDateString) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
			GregorianCalendar eventTimeCalendar = new GregorianCalendar();
			eventTimeCalendar.setTime(sdf.parse(standardDateString));
			return new BsonDateTime(eventTimeCalendar.getTimeInMillis());
		} catch (ParseException e) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
				GregorianCalendar eventTimeCalendar = new GregorianCalendar();
				eventTimeCalendar.setTime(sdf.parse(standardDateString));
				return new BsonDateTime(eventTimeCalendar.getTimeInMillis());
			} catch (ParseException e1) {
				Configuration.logger.log(Level.ERROR, e1.toString());
			}
		}
		// Never Happened
		return null;
	}

	public static BsonDocument getBsonGeoPoint(String pointString) {
		try {
			BsonDocument pointDoc = new BsonDocument();
			pointDoc.put("type", new BsonString("Point"));

			String[] pointArr = pointString.split(",");
			if (pointArr.length != 2)
				return null;
			BsonArray arr = new BsonArray();
			arr.add(new BsonDouble(Double.parseDouble(pointArr[0])));
			arr.add(new BsonDouble(Double.parseDouble(pointArr[1])));
			pointDoc.put("coordinates", arr);
			return pointDoc;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static BsonDocument getBsonGeoArea(String areaString) {
		try {
			BsonDocument areaDoc = new BsonDocument();
			areaDoc.put("type", new BsonString("Polygon"));

			String[] areaArr = areaString.split(",");
			if (areaArr.length < 2)
				return null;

			BsonArray area = new BsonArray();
			BsonArray point = null;
			for (String element : areaArr) {
				Double pointElementDouble = Double.parseDouble(element);
				if (point == null) {
					point = new BsonArray();
				} else if (point.size() == 2) {
					area.add(point);
					point = new BsonArray();
				}
				point.add(new BsonDouble(pointElementDouble));
			}

			if (area.size() > 2) {
				BsonValue first = area.get(0);
				area.add(first);
				areaDoc.put("coordinates", area);
				return areaDoc;
			}
			return null;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return null;
		}
	}

	static public String encodeMongoObjectKey(String key) {
		key = key.replace(".", "\uff0e");
		return key;
	}
}
