package org.oliot.epcis.util;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Map.Entry;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.bson.Document;
import org.bson.types.Binary;
import org.oliot.epcis.model.AttributeType;
import org.oliot.epcis.model.BusinessTransactionListType;
import org.oliot.epcis.model.BusinessTransactionType;
import org.oliot.epcis.model.DestinationListType;
import org.oliot.epcis.model.EPC;
import org.oliot.epcis.model.EPCListType;
import org.oliot.epcis.model.ILMDType;
import org.oliot.epcis.model.PersistentDispositionType;
import org.oliot.epcis.model.QuantityElementType;
import org.oliot.epcis.model.QuantityListType;
import org.oliot.epcis.model.SensorElementListType;
import org.oliot.epcis.model.SensorElementType;
import org.oliot.epcis.model.SensorMetadataType;
import org.oliot.epcis.model.SensorReportType;
import org.oliot.epcis.model.SourceDestType;
import org.oliot.epcis.model.SourceListType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class BSONReadUtil {
	static public String encodeMongoObjectKey(String key) {
		key = key.replace(".", "\uff0e");
		return key;
	}
	public static Long getBsonDateTime(String standardDateString) {
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
        return null;
    }
	public static XMLGregorianCalendar getGregorianCalendar(Long time) throws DatatypeConfigurationException {
		if (time == null)
			return null;
		GregorianCalendar eventCalendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
		eventCalendar.setTimeInMillis(time);
		XMLGregorianCalendar xmlEventTime;
		xmlEventTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(eventCalendar);
		return xmlEventTime;
	}

	public static EPCListType getEPCListType(List<String> arr) {
		ArrayList<EPC> epcList = new ArrayList<>();
		for (Object elem : arr) {
			epcList.add(new EPC(elem.toString()));
		}
		return new EPCListType(epcList);
	}

	public static BusinessTransactionListType getBusinessTransactionListType(List<org.bson.Document> arr) {
		ArrayList<BusinessTransactionType> btList = new ArrayList<>();
		for (org.bson.Document elem : arr) {
			for (String key : elem.keySet()) {
				btList.add(new BusinessTransactionType(decodeMongoObjectKey(key), elem.getString(key)));
			}
		}
		return new BusinessTransactionListType(btList);
	}

	public static SourceListType getSourceListType(List<Document> arr) {
		return new SourceListType(getSourceDestTypeList(arr));
	}

	public static DestinationListType getDestinationListType(List<Document> arr) {
		return new DestinationListType(getSourceDestTypeList(arr));
	}

	static ArrayList<SourceDestType> getSourceDestTypeList(List<Document> arr) {
		ArrayList<SourceDestType> sdList = new ArrayList<>();
		for (Document obj : arr) {
			for (String entryKey : obj.keySet()) {
				sdList.add(new SourceDestType(decodeMongoObjectKey(entryKey), obj.getString(entryKey)));
			}
		}
		return sdList;
	}

	public static QuantityListType getQuantityListType(List<Document> arr) {
		ArrayList<QuantityElementType> qeList = new ArrayList<>();
		for (Document elem : arr) {
			QuantityElementType qElem = new QuantityElementType(elem.getString("epcClass"), elem.getDouble("quantity"),
					elem.getString("uom"));
			qeList.add(qElem);
		}

		return new QuantityListType(qeList);
	}

	public static PersistentDispositionType getPersistentDispositionType(Document pdObj) {
		if (pdObj == null || pdObj.isEmpty())
			return null;
		PersistentDispositionType pd = new PersistentDispositionType();
		if (pdObj.containsKey("unset")) {
			pd.setUnset(pdObj.getList("unset", String.class));
		}
		if (pdObj.containsKey("set")) {
			pd.setSet(pdObj.getList("set", String.class));
		}

		return pd;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static SensorElementListType getSensorElementListType(List<Document> arr, org.w3c.dom.Document doc,
			Element envelope, ArrayList<String> nsList) {
		List<SensorElementType> seList = new ArrayList<>();

		for (Document se : arr) {
			SensorElementType set = new SensorElementType();
			Document m = se.get("sensorMetadata", Document.class);
			if (m != null) {
				SensorMetadataType smd = new SensorMetadataType();
				if (m.containsKey("bizRules"))
					smd.setBizRules(m.getString("bizRules"));
				if (m.containsKey("dataProcessingMethod"))
					smd.setDataProcessingMethod(m.getString("dataProcessingMethod"));
				if (m.containsKey("deviceID"))
					smd.setDeviceID(m.getString("deviceID"));
				if (m.containsKey("deviceMetadata"))
					smd.setDeviceMetadata(m.getString("deviceMetadata"));
				if (m.containsKey("endTime")) {
					try {
						smd.setEndTime(getGregorianCalendar(m.getLong("endTime")));
					} catch (DatatypeConfigurationException e) {
						e.printStackTrace();
					}
				}
				if (m.containsKey("otherAttributes")) {
					smd.setOtherAttributes(
							getOtherAttributes(m.get("otherAttributes", Document.class), envelope, nsList));
				}
				if (m.containsKey("rawData"))
					smd.setRawData(m.getString("rawData"));
				if (m.containsKey("startTime")) {
					try {
						smd.setStartTime(getGregorianCalendar(m.getLong("startTime")));
					} catch (DatatypeConfigurationException e) {
						e.printStackTrace();
					}
				}

				if (m.containsKey("time")) {
					try {
						smd.setTime(getGregorianCalendar(m.getLong("time")));
					} catch (DatatypeConfigurationException e) {
						e.printStackTrace();
					}
				}

				set.setSensorMetadata(smd);

			}
			List<Document> rArr = se.getList("sensorReport", Document.class);
			if (rArr != null && !rArr.isEmpty()) {
				List<SensorReportType> rList = new ArrayList<>();
				for (Document r : rArr) {
					SensorReportType srt = new SensorReportType();
					if (r.containsKey("chemicalSubstance"))
						srt.setChemicalSubstance(r.getString("chemicalSubstance"));
					if (r.containsKey("dataProcessingMethod"))
						srt.setDataProcessingMethod(r.getString("dataProcessingMethod"));
					if (r.containsKey("deviceID"))
						srt.setDeviceID(r.getString("deviceID"));
					if (r.containsKey("deviceMetadata"))
						srt.setDeviceMetadata(r.getString("deviceMetadata"));
					if (r.containsKey("booleanValue"))
						srt.setBooleanValue(r.getBoolean("booleanValue"));
					if (r.containsKey("hexBinaryValue")) {
						byte[] x = r.get("hexBinaryValue", Binary.class).getData();
						srt.setHexBinaryValue(x);
					}
					if (r.containsKey("maxValue"))
						srt.setMaxValue(r.getDouble("maxValue"));
					if (r.containsKey("meanValue"))
						srt.setMeanValue(r.getDouble("meanValue"));
					if (r.containsKey("microorganism"))
						srt.setMicroorganism(r.getString("microorganism"));
					if (r.containsKey("minValue"))
						srt.setMinValue(r.getDouble("minValue"));
					if (r.containsKey("otherAttributes"))
						srt.setOtherAttributes(
								getOtherAttributes(r.get("otherAttributes", Document.class), envelope, nsList));
					if (r.containsKey("percRank"))
						srt.setPercRank(r.getDouble("percRank"));
					if (r.containsKey("percValue"))
						srt.setPercValue(r.getDouble("percValue"));
					if (r.containsKey("rawData"))
						srt.setRawData(r.getString("rawData"));
					if (r.containsKey("sDev"))
						srt.setSDev(r.getDouble("sDev"));
					if (r.containsKey("stringValue"))
						srt.setStringValue(r.getString("stringValue"));
					if (r.containsKey("time")) {
						try {
							srt.setTime(getGregorianCalendar(r.getLong("time")));
						} catch (DatatypeConfigurationException e) {
							e.printStackTrace();
						}
					}
					if (r.containsKey("type"))
						srt.setType(r.getString("type"));
					if (r.containsKey("uom"))
						srt.setUom(r.getString("uom"));
					if (r.containsKey("uriValue"))
						srt.setUriValue(r.getString("uriValue"));
					if (r.containsKey("value")) {
						Double dVal = r.getDouble("value");
						if (dVal != null)
							srt.setValueAttribute(dVal);
					}

					rList.add(srt);
				}
				set.setSensorReport(rList);
			}
			Document sensorExt = se.get("extension", Document.class);
			if (sensorExt != null) {
				List ext = getAny(sensorExt, doc, envelope, nsList);
				set.setAny(ext);
			}
			seList.add(set);
		}

		return new SensorElementListType(seList);
	}

	public static ILMDType getILMD(Document obj, org.w3c.dom.Document doc, Element envelope, ArrayList<String> nsList) {
		return new ILMDType(getAny(obj, doc, envelope, nsList));
	}

	public static Map<QName, String> getOtherAttributes(Document obj, Element envelope, ArrayList<String> nsList) {
		Map<QName, String> oa = new HashMap<>();
		for (String key : obj.keySet()) {
			String[] parts = key.split("#");
			String prefix;
			int idx = nsList.indexOf(parts[0]);
			if (nsList.contains(parts[0])) {
				prefix = "ext" + idx;
			} else {
				nsList.add(parts[0]);
				prefix = "ext" + (nsList.size() - 1);
				envelope.setAttribute("xmlns:" + prefix, decodeMongoObjectKey(parts[0]));
			}
			QName n = new QName(decodeMongoObjectKey(parts[0]), parts[1], prefix);
			oa.put(n, obj.getString(key));
		}
		return oa;
	}

	public static Element getExtensionElement(org.w3c.dom.Document doc, String prefix, String namespaceURI,
			Element envelope, String qname, String type, String value) {
		Element element = doc.createElement(qname);
		if (prefix != null && namespaceURI != null) {
			element.setAttribute("xmlns:" + prefix, decodeMongoObjectKey(namespaceURI));
			envelope.setAttribute("xmlns:" + prefix, decodeMongoObjectKey(namespaceURI));
		}
		element.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:type", type);
		element.setAttribute("xsi:type", type);
		element.setTextContent(value);
		return element;
	}

	public static Element getAdditionalTradeItemIDElement(org.w3c.dom.Document doc, List<Document> value) {
		Element element = doc.createElement("AdditionalPartyID");
		for (Document obj : value) {
			Element inner = doc.createElement("additionalTradeItemID");
			String innerKey = obj.keySet().iterator().next();
			String innerValue = obj.getString(innerKey);
			inner.setAttribute("tradeItemIDTypeCode", innerKey);
			inner.setTextContent(innerValue);
			element.appendChild(inner);
		}
		return element;
	}

	public static Element getMeasurementExtensionElement(org.w3c.dom.Document doc, String prefix, String namespaceURI,
			Element envelope, String qname, Document objValue) {
		Element element = doc.createElement(qname);
		if (prefix != null && namespaceURI != null) {
			element.setAttribute("xmlns:" + prefix, decodeMongoObjectKey(namespaceURI));
			envelope.setAttribute("xmlns:" + prefix, decodeMongoObjectKey(namespaceURI));
		}
		element.setAttribute("measurementUnitCode", objValue.getString("measurementUnitCode"));
		element.setTextContent(objValue.getDouble("value").toString());
		return element;
	}

	public static AttributeType getMeasurementExtensionElement(AttributeType attribute, Document objValue) {
		attribute.setOtherAttributes(
				Map.of(new QName("measurementUnitCode"), objValue.getString("measurementUnitCode")));
		List<Object> content = new ArrayList<>();
		content.add(objValue.getDouble("value").toString());
		attribute.setContent(content);
		return attribute;
	}

	public static List<Object> getAny(Document obj, org.w3c.dom.Document doc, Element envelope,
			ArrayList<String> nsList) {

		if (nsList == null) {
			nsList = new ArrayList<>();
		}

		Iterator<Entry<String, Object>> anyIter = obj.entrySet().iterator();
		List<Object> elementList = new ArrayList<>();
		while (anyIter.hasNext()) {
			Entry<String, Object> anyEntry = anyIter.next();
			String anyKey = anyEntry.getKey();
			Object anyValue = anyEntry.getValue();
			elementList.addAll(getAny(anyKey, anyValue, doc, envelope, nsList));
		}
		return elementList;
	}

	public static List<Object> getAny(String anyKey, Object anyValue, org.w3c.dom.Document doc, Element envelope,
			ArrayList<String> nsList) {
		if (anyKey == null)
			return null;

		List<Object> elementList = new ArrayList<>();

		if (nsList == null) {
			nsList = new ArrayList<>();
		}

		String[] anyKeyCheck = anyKey.split("#");
		StringBuilder namespaceURI = new StringBuilder();
		String localName;
		String prefix = null;
		String qname;

		// Get Namespace

		if (anyKeyCheck.length >= 2 && !anyKeyCheck[0].equals("")) {
			namespaceURI = new StringBuilder();
			for (int i = 0; i < anyKeyCheck.length - 1; i++) {
				namespaceURI.append(anyKeyCheck[i]).append("#");
			}
			namespaceURI = new StringBuilder(namespaceURI.substring(0, namespaceURI.length() - 1));
			localName = anyKeyCheck[anyKeyCheck.length - 1];
			synchronized (nsList) {
				int idx = nsList.indexOf(namespaceURI.toString());
				if (nsList.contains(namespaceURI.toString())) {
					prefix = "ext" + idx;
				} else {
					nsList.add(namespaceURI.toString());
					prefix = "ext" + (nsList.size() - 1);
					envelope.setAttribute("xmlns:" + prefix, decodeMongoObjectKey(namespaceURI.toString()));
				}
			}
			qname = prefix + ":" + localName;
		} else {
			qname = anyKeyCheck[1];
		}

		// Int xsd:int Integer
		// Float xsd:double Double
		// Time xsd:dateTimeStamp Long
		// Boolean xsd:boolean Boolean
		// String xsd:string String

		if (anyValue instanceof String) {
			elementList.add(getExtensionElement(doc, prefix, namespaceURI.toString(), envelope, qname, "xsd:string",
					anyValue.toString()));
		} else if (anyValue instanceof Integer) {
			elementList.add(getExtensionElement(doc, prefix, namespaceURI.toString(), envelope, qname, "xsd:int",
					anyValue.toString()));
		} else if (anyValue instanceof Long) {
			elementList.add(getExtensionElement(doc, prefix, namespaceURI.toString(), envelope, qname, "xsd:dateTime",
					getDate((Long) anyValue)));
		} else if (anyValue instanceof Double) {
			elementList.add(getExtensionElement(doc, prefix, namespaceURI.toString(), envelope, qname, "xsd:double",
					anyValue.toString()));
		} else if (anyValue instanceof Boolean) {
			elementList.add(getExtensionElement(doc, prefix, namespaceURI.toString(), envelope, qname, "xsd:boolean",
					anyValue.toString()));
		} else if (anyValue instanceof Document) {
			if (anyKey.equals("urn:epcglobal:cbv:mda#drainedWeight")
					|| anyKey.equals("urn:epcglobal:cbv:mda#grossWeight")
					|| anyKey.equals("urn:epcglobal:cbv:mda#netWeight")) {
				Document objValue = (Document) anyValue;
				elementList.add(getMeasurementExtensionElement(doc, prefix, namespaceURI.toString(), envelope, qname,
						objValue));
			} else {
				elementList.add(getObjectAny(qname, prefix, namespaceURI.toString(), (Document) anyValue, doc, envelope,
						nsList));
			}
		} else if (anyValue instanceof List) {
			elementList.addAll(getArrayAny(anyKey, (List<?>) anyValue, doc, envelope, nsList));
		}
		return elementList;
	}

	public static List<Object> getArrayAny(String anyKey, List<?> anyValue, org.w3c.dom.Document doc, Element envelope,
			ArrayList<String> nsList) {
		List<Object> elementList = new ArrayList<>();
		for (Object inner : anyValue) {
			elementList.addAll(getAny(anyKey, inner, doc, envelope, nsList));
		}
		return elementList;
	}

	public static Element getObjectAny(String qname, String prefix, String namespaceURI, Document obj,
			org.w3c.dom.Document doc, Element envelope, ArrayList<String> nsList) {

		List<Object> nodeList = getAny(obj, doc, envelope, nsList);
		Element element = doc.createElement(qname);
		if (prefix != null && namespaceURI != null) {
			element.setAttribute("xmlns:" + prefix, decodeMongoObjectKey(namespaceURI));
			envelope.setAttribute("xmlns:" + prefix, decodeMongoObjectKey(namespaceURI));
		}
		for (Object node : nodeList) {
			element.appendChild((Node) node);
		}

		return element;
	}

	public static String getDate(Long t) {
		Date date = new Date(t);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		return sdf.format(date);
	}

	public static String decodeMongoObjectKey(String key) {
		key = key.replace("\uff0e", ".");
		return key;
	}
}
