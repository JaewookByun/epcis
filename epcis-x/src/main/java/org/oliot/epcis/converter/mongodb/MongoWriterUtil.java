package org.oliot.epcis.converter.mongodb;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

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
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

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

	static JsonObject getBaseExtensionObject(EPCISEventExtensionType baseExtensionType) {
		JsonObject baseExtension = new JsonObject();
		/*
		 * May be deprecated if (baseExtensionType.getAny() != null &&
		 * baseExtensionType.getAny().isEmpty() == false) { List<Object> objList =
		 * baseExtensionType.getAny(); BsonDocument map2Save = getAnyMap(objList); if
		 * (map2Save.isEmpty() == false) baseExtension.put("any", map2Save); }
		 */
		if (baseExtensionType.getOtherAttributes() != null
				&& baseExtensionType.getOtherAttributes().isEmpty() == false) {
			Map<QName, String> map = baseExtensionType.getOtherAttributes();
			JsonObject map2Save = getOtherAttributesMap(map);
			if (map2Save.isEmpty() == false)
				baseExtension.put("otherAttributes", map2Save);
		}
		return baseExtension;
	}

	static JsonObject getReadPointObject(ReadPointType readPointType) {
		JsonObject readPoint = new JsonObject();
		if (readPointType.getId() != null)
			readPoint.put("id", readPointType.getId());
		return readPoint;
	}

	static JsonObject getBizLocationObject(BusinessLocationType bizLocationType) {
		JsonObject bizLocation = new JsonObject();
		if (bizLocationType.getId() != null)
			bizLocation.put("id", bizLocationType.getId());

		BusinessLocationExtensionType bizLocationExtensionType = bizLocationType.getExtension();
		if (bizLocationExtensionType != null) {
			JsonObject extension = new JsonObject();
			if (bizLocationExtensionType.getAny() != null) {
				JsonObject map2Save = new JsonObject();
				List<Object> objList = bizLocationExtensionType.getAny();
				for (int i = 0; i < objList.size(); i++) {
					Object obj = objList.get(i);
					if (obj instanceof Element) {
						Element element = (Element) obj;
						if (element.getFirstChild() != null) {
							String name = element.getLocalName();
							String value = element.getFirstChild().getTextContent();
							map2Save.put(name, value);
						}
					}
				}
				if (map2Save != null)
					extension.put("any", map2Save);
			}

			if (bizLocationExtensionType.getOtherAttributes() != null) {
				Map<QName, String> map = bizLocationExtensionType.getOtherAttributes();
				JsonObject map2Save = new JsonObject();
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

	static JsonArray getBizTransactionObjectList(List<BusinessTransactionType> bizList) {
		JsonArray bizTranList = new JsonArray();
		for (int i = 0; i < bizList.size(); i++) {
			BusinessTransactionType bizTranType = bizList.get(i);
			if (bizTranType.getType() != null && bizTranType.getValue() != null) {
				JsonObject dbObj = new JsonObject();
				dbObj.put(bizTranType.getType(),
						bizTranType.getValue().replaceAll("\n", "").replaceAll("\t", "").replaceAll("\\s", ""));
				bizTranList.add(dbObj);
			}
		}
		return bizTranList;
	}

	static JsonObject getAggregationEventExtensionObject(AggregationEventExtensionType oee) {
		JsonObject extension = new JsonObject();
		if (oee.getChildQuantityList() != null) {
			QuantityListType qetl = oee.getChildQuantityList();
			List<QuantityElementType> qetList = qetl.getQuantityElement();
			JsonArray quantityList = new JsonArray();
			for (int i = 0; i < qetList.size(); i++) {
				JsonObject quantity = new JsonObject();
				QuantityElementType qet = qetList.get(i);
				if (qet.getEpcClass() != null)
					quantity.put("epcClass", qet.getEpcClass().toString());
				if (qet.getQuantity().doubleValue() != 0) {
					quantity.put("quantity", qet.getQuantity().doubleValue());
				}
				if (qet.getUom() != null)
					quantity.put("uom", qet.getUom().toString());
				quantityList.add(quantity);
			}
			extension.put("childQuantityList", quantityList);
		}

		if (oee.getSourceList() != null) {
			SourceListType sdtl = oee.getSourceList();
			List<SourceDestType> sdtList = sdtl.getSource();
			JsonArray dbList = new JsonArray();
			for (int i = 0; i < sdtList.size(); i++) {
				SourceDestType sdt = sdtList.get(i);
				JsonObject dbObj = new JsonObject();
				dbObj.put(sdt.getType(), sdt.getValue());
				dbList.add(dbObj);
			}
			extension.put("sourceList", dbList);
		}
		if (oee.getDestinationList() != null) {
			DestinationListType sdtl = oee.getDestinationList();
			List<SourceDestType> sdtList = sdtl.getDestination();
			JsonArray dbList = new JsonArray();
			for (int i = 0; i < sdtList.size(); i++) {
				SourceDestType sdt = sdtList.get(i);
				JsonObject dbObj = new JsonObject();
				dbObj.put(sdt.getType(), sdt.getValue());
				dbList.add(dbObj);
			}
			extension.put("destinationList", dbList);
		}
		if (oee.getExtension() != null) {
			AggregationEventExtension2Type extension2Type = oee.getExtension();
			JsonObject extension2 = new JsonObject();
			if (extension2Type.getAny() != null) {
				List<Object> objList = extension2Type.getAny();
				JsonObject map2Save = getAnyMap(objList);
				if (map2Save.isEmpty() == false)
					extension2.put("any", map2Save);
			}

			if (extension2Type.getOtherAttributes() != null) {
				Map<QName, String> map = extension2Type.getOtherAttributes();
				JsonObject map2Save = getOtherAttributesMap(map);
				if (map2Save.isEmpty() == false)
					extension2.put("otherAttributes", map2Save);
			}
			extension.put("extension", extension2);
		}
		return extension;
	}

	static JsonObject getILMDExtensionMap(ILMDExtensionType ilmdExtension) {
		List<Object> objList = ilmdExtension.getAny();
		JsonObject map2Save = getAnyMap(objList);
		return map2Save;
	}

	static JsonObject getObjectEventExtensionObject(ObjectEventExtensionType oee, List<EPC> epcList) {
		JsonObject extension = new JsonObject();
		if (oee.getQuantityList() != null) {
			QuantityListType qetl = oee.getQuantityList();
			List<QuantityElementType> qetList = qetl.getQuantityElement();
			JsonArray quantityList = new JsonArray();
			for (int i = 0; i < qetList.size(); i++) {
				JsonObject quantity = new JsonObject();
				QuantityElementType qet = qetList.get(i);
				if (qet.getEpcClass() != null)
					quantity.put("epcClass", qet.getEpcClass().toString());
				if (qet.getQuantity().doubleValue() != 0) {
					quantity.put("quantity", qet.getQuantity().doubleValue());
				}
				if (qet.getUom() != null)
					quantity.put("uom", qet.getUom().toString());
				quantityList.add(quantity);
			}
			extension.put("quantityList", quantityList);
		}
		if (oee.getSourceList() != null) {
			SourceListType sdtl = oee.getSourceList();
			List<SourceDestType> sdtList = sdtl.getSource();
			JsonArray dbList = new JsonArray();
			for (int i = 0; i < sdtList.size(); i++) {
				SourceDestType sdt = sdtList.get(i);
				JsonObject dbObj = new JsonObject();
				dbObj.put(sdt.getType(), sdt.getValue());
				dbList.add(dbObj);
			}
			extension.put("sourceList", dbList);
		}
		if (oee.getDestinationList() != null) {
			DestinationListType sdtl = oee.getDestinationList();
			List<SourceDestType> sdtList = sdtl.getDestination();
			JsonArray dbList = new JsonArray();
			for (int i = 0; i < sdtList.size(); i++) {
				SourceDestType sdt = sdtList.get(i);
				JsonObject dbObj = new JsonObject();
				dbObj.put(sdt.getType(), sdt.getValue());
				dbList.add(dbObj);
			}
			extension.put("destinationList", dbList);
		}

		// new in v1.2
		if (oee.getIlmd() != null) {
			ILMDType ilmd = oee.getIlmd();

			if (ilmd.getAny() != null) {
				JsonObject map2Save = getAnyMap(ilmd.getAny());
				if (map2Save != null && map2Save.isEmpty() == false) {
					extension.put("ilmd", new JsonObject().put("any", map2Save));
				}
//				if (epcList != null) {
//					MasterDataWriteConverter mdConverter = new MasterDataWriteConverter();
//					mdConverter.capture(epcList, map2Save);
//				}
			}

			/*
			 * Deprecated if (ilmd.getExtension() != null) { ILMDExtensionType ilmdExtension
			 * = ilmd.getExtension(); BsonDocument map2Save =
			 * getILMDExtensionMap(ilmdExtension); if (map2Save != null)
			 * extension.put("ilmd", map2Save); if (epcList != null) {
			 * MasterDataWriteConverter mdConverter = new MasterDataWriteConverter();
			 * mdConverter.capture(epcList, map2Save); } }
			 */
		}

		if (oee.getExtension() != null) {
			ObjectEventExtension2Type extension2Type = oee.getExtension();
			JsonObject extension2 = new JsonObject();
			if (extension2Type.getAny() != null) {
				List<Object> objList = extension2Type.getAny();
				JsonObject map2Save = getAnyMap(objList);
				if (map2Save != null)
					extension2.put("any", map2Save);
			}

			if (extension2Type.getOtherAttributes() != null) {
				Map<QName, String> map = extension2Type.getOtherAttributes();
				JsonObject map2Save = getOtherAttributesMap(map);
				if (map2Save.isEmpty() == false)
					extension2.put("otherAttributes", map2Save);
			}
			extension.put("extension", extension2);
		}
		return extension;
	}

	static JsonObject getQuantityEventExtensionObject(QuantityEventExtensionType oee) {
		JsonObject extension = new JsonObject();
		if (oee.getAny() != null) {
			List<Object> objList = oee.getAny();
			JsonObject map2Save = getAnyMap(objList);
			if (map2Save != null)
				extension.put("any", map2Save);
		}

		if (oee.getOtherAttributes() != null) {
			Map<QName, String> map = oee.getOtherAttributes();
			JsonObject map2Save = getOtherAttributesMap(map);
			if (map2Save.isEmpty() == false)
				extension.put("otherAttributes", map2Save);
		}
		return extension;
	}

	static JsonObject getTransactionEventExtensionObject(TransactionEventExtensionType oee) {
		JsonObject extension = new JsonObject();
		if (oee.getQuantityList() != null) {
			QuantityListType qetl = oee.getQuantityList();
			List<QuantityElementType> qetList = qetl.getQuantityElement();
			JsonArray quantityList = new JsonArray();
			for (int i = 0; i < qetList.size(); i++) {
				JsonObject quantity = new JsonObject();
				QuantityElementType qet = qetList.get(i);
				if (qet.getEpcClass() != null)
					quantity.put("epcClass", qet.getEpcClass().toString());
				if (qet.getQuantity().doubleValue() != 0) {
					quantity.put("quantity", qet.getQuantity().doubleValue());
				}
				if (qet.getUom() != null)
					quantity.put("uom", qet.getUom().toString());
				quantityList.add(quantity);
			}
			extension.put("quantityList", quantityList);
		}
		if (oee.getSourceList() != null) {
			SourceListType sdtl = oee.getSourceList();
			List<SourceDestType> sdtList = sdtl.getSource();
			JsonArray dbList = new JsonArray();
			for (int i = 0; i < sdtList.size(); i++) {
				SourceDestType sdt = sdtList.get(i);
				JsonObject dbObj = new JsonObject();
				dbObj.put(sdt.getType(), sdt.getValue());
				dbList.add(dbObj);
			}
			extension.put("sourceList", dbList);
		}
		if (oee.getDestinationList() != null) {
			DestinationListType sdtl = oee.getDestinationList();
			List<SourceDestType> sdtList = sdtl.getDestination();
			JsonArray dbList = new JsonArray();
			for (int i = 0; i < sdtList.size(); i++) {
				SourceDestType sdt = sdtList.get(i);
				JsonObject dbObj = new JsonObject();
				dbObj.put(sdt.getType(), sdt.getValue());
				dbList.add(dbObj);
			}
			extension.put("destinationList", dbList);
		}
		if (oee.getExtension() != null) {
			TransactionEventExtension2Type extension2Type = oee.getExtension();
			JsonObject extension2 = new JsonObject();
			if (extension2Type.getAny() != null) {
				List<Object> objList = extension2Type.getAny();
				JsonObject map2Save = getAnyMap(objList);
				if (map2Save != null)
					extension2.put("any", map2Save);
			}

			if (extension2Type.getOtherAttributes() != null) {
				Map<QName, String> map = extension2Type.getOtherAttributes();
				JsonObject map2Save = getOtherAttributesMap(map);
				if (map2Save.isEmpty() == false)
					extension2.put("otherAttributes", map2Save);
			}
			extension.put("extension", extension2);
		}
		return extension;
	}

	static JsonArray getQuantityObjectList(List<QuantityElementType> qetList) {
		JsonArray quantityList = new JsonArray();
		for (int i = 0; i < qetList.size(); i++) {
			JsonObject quantity = new JsonObject();
			QuantityElementType qet = qetList.get(i);
			if (qet.getEpcClass() != null)
				quantity.put("epcClass", qet.getEpcClass().toString());
			if (qet.getQuantity().doubleValue() != 0) {
				quantity.put("quantity", qet.getQuantity().doubleValue());
			}
			if (qet.getUom() != null)
				quantity.put("uom", qet.getUom().toString());
			quantityList.add(quantity);
		}
		return quantityList;
	}

	static JsonArray getSourceDestObjectList(List<SourceDestType> sdtList) {
		JsonArray dbList = new JsonArray();
		for (int i = 0; i < sdtList.size(); i++) {
			SourceDestType sdt = sdtList.get(i);
			JsonObject dbObj = new JsonObject();
			dbObj.put(sdt.getType(), sdt.getValue());
			dbList.add(dbObj);
		}
		return dbList;
	}

	static JsonObject getTransformationEventExtensionObject(TransformationEventExtensionType oee) {
		JsonObject extension = new JsonObject();
		if (oee.getAny() != null) {
			List<Object> objList = oee.getAny();
			JsonObject map2Save = getAnyMap(objList);
			if (map2Save != null)
				extension.put("any", map2Save);
		}

		if (oee.getOtherAttributes() != null) {
			Map<QName, String> map = oee.getOtherAttributes();
			JsonObject map2Save = getOtherAttributesMap(map);
			if (map2Save != null)
				extension.put("otherAttributes", map2Save);
		}
		return extension;
	}

	static JsonObject getAnyMap(List<Object> objList) {
		JsonObject map2Save = new JsonObject();
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
				map2Save.put("@" + namespaceURI, prefix);

				Node firstChildNode = element.getFirstChild();
				if (firstChildNode != null) {
					if (firstChildNode instanceof Text) {
						String value = firstChildNode.getTextContent();
						value = reflectXsiType(value, element);
						map2Save.put(qnameKey, converseType(value));
					} else if (firstChildNode instanceof Element) {
						Element childNode = null;
						JsonObject sub2Save = new JsonObject();
						do {
							if (firstChildNode instanceof Element) {
								childNode = (Element) firstChildNode;
								String childQName = childNode.getNodeName();
								String[] childCheckArr = childQName.split(":");
								if (childCheckArr.length != 2)
									continue;

								String childPrefix = childCheckArr[0];
								String childNamespaceURI = encodeMongoObjectKey(childNode.getNamespaceURI());

								sub2Save.put("@" + childNamespaceURI, childPrefix);

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
	static JsonObject getAnyMap(Element element, JsonObject map2Save) {

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
			JsonObject sub2Save = new JsonObject();
			do {
				if (firstChildNode instanceof Element) {
					childNode = (Element) firstChildNode;
					String childQName = childNode.getNodeName();
					String[] childCheckArr = childQName.split(":");
					if (childCheckArr.length != 2)
						continue;
					String childPrefix = childCheckArr[0];
					String childNamespaceURI = encodeMongoObjectKey(childNode.getNamespaceURI());
					sub2Save.put("@" + childNamespaceURI, childPrefix);
					map2Save.put(qnameKey, getAnyMap(childNode, sub2Save));
				}
			} while (firstChildNode.getNextSibling() != null && firstChildNode.getNextSibling() instanceof Element
					&& (firstChildNode = (Element) firstChildNode.getNextSibling()) != null);
		}

		return map2Save;
	}

	static JsonObject getOtherAttributesMap(Map<QName, String> map) {
		JsonObject map2Save = new JsonObject();
		Iterator<QName> iter = map.keySet().iterator();
		while (iter.hasNext()) {
			QName qName = iter.next();
			String value = map.get(qName);
			map2Save.put(qName.toString(), value);
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

	public static Object converseType(String value) {
		String[] valArr = value.split("\\^");
		if (valArr.length != 2) {
			return value;
		}
		try {
			String type = valArr[1];
			if (type.equals("int")) {
				return Integer.parseInt(valArr[0]);
			} else if (type.equals("long")) {
				return Long.parseLong(valArr[0]);
			} else if (type.equals("double")) {
				return Double.parseDouble(valArr[0]);
			} else if (type.equals("boolean")) {
				return Boolean.parseBoolean(valArr[0]);
			} else if (type.equals("float")) {
				return Double.parseDouble(valArr[0]);
			} else if (type.equals("dateTime")) {
				long time = getBsonDateTime(valArr[0]);
				if (time != 0)
					return time;
				return value;
			} else if (type.equals("geoPoint")) {
				JsonObject point = getBsonGeoPoint(valArr[0]);
				if (point == null)
					return value;
				return point;
			} else if (type.equals("geoArea")) {
				JsonObject area = getBsonGeoArea(valArr[0]);
				if (area == null)
					return value;
				return area;
			} else {
				return value;
			}
		} catch (NumberFormatException e) {
			return value;
		}
	}

	static JsonObject getErrorDeclaration(ErrorDeclarationType edt) {
		JsonObject errorBson = new JsonObject();
		long declarationTime = edt.getDeclarationTime().toGregorianCalendar().getTimeInMillis();
		errorBson.put("declarationTime", declarationTime);
		// (Optional) reason
		if (edt.getReason() != null) {
			errorBson.put("reason", edt.getReason());
		}
		// (Optional) correctiveEventIDs
		if (edt.getCorrectiveEventIDs() != null) {
			CorrectiveEventIDsType cIDs = edt.getCorrectiveEventIDs();
			List<String> cIDStringList = cIDs.getCorrectiveEventID();
			JsonArray correctiveIDBsonArray = new JsonArray();
			for (String cIDString : cIDStringList) {
				correctiveIDBsonArray.add(cIDString);
			}
			if (correctiveIDBsonArray.size() != 0) {
				errorBson.put("correctiveEventIDs", correctiveIDBsonArray);
			}
		}
		if (edt.getAny() != null) {
			JsonObject map2Save = getAnyMap(edt.getAny());
			if (map2Save != null && map2Save.isEmpty() == false) {
				errorBson.put("any", map2Save);
			}
		}

		return errorBson;
	}

	public static long getBsonDateTime(String standardDateString) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
			GregorianCalendar eventTimeCalendar = new GregorianCalendar();
			eventTimeCalendar.setTime(sdf.parse(standardDateString));
			return eventTimeCalendar.getTimeInMillis();
		} catch (ParseException e) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
				GregorianCalendar eventTimeCalendar = new GregorianCalendar();
				eventTimeCalendar.setTime(sdf.parse(standardDateString));
				return eventTimeCalendar.getTimeInMillis();
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
		}
		return 0;
	}

	public static JsonObject getBsonGeoPoint(String pointString) {
		try {
			JsonObject pointDoc = new JsonObject();
			pointDoc.put("type", "Point");

			String[] pointArr = pointString.split(",");
			if (pointArr.length != 2)
				return null;
			JsonArray arr = new JsonArray();
			arr.add(Double.parseDouble(pointArr[0]));
			arr.add(Double.parseDouble(pointArr[1]));
			pointDoc.put("coordinates", arr);
			return pointDoc;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static JsonObject getBsonGeoArea(String areaString) {
		try {
			JsonObject areaDoc = new JsonObject();
			areaDoc.put("type", "Polygon");

			String[] areaArr = areaString.split(",");
			if (areaArr.length < 2)
				return null;

			JsonArray area = new JsonArray();
			JsonArray point = null;
			for (String element : areaArr) {
				Double pointElementDouble = Double.parseDouble(element);
				if (point == null) {
					point = new JsonArray();
				} else if (point.size() == 2) {
					area.add(point);
					point = new JsonArray();
				}
				point.add(pointElementDouble);
			}

			if (area.size() > 2) {
				double first = area.getDouble(0);
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
