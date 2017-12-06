package org.oliot.epcis.converter.mongodb;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Level;
import org.bson.BsonArray;
import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonType;
import org.bson.BsonValue;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.model.epcis.AggregationEventExtension2Type;
import org.oliot.model.epcis.AttributeType;
import org.oliot.model.epcis.BusinessLocationExtensionType;
import org.oliot.model.epcis.CorrectiveEventIDsType;
import org.oliot.model.epcis.EPCISEventExtensionType;
import org.oliot.model.epcis.ErrorDeclarationType;
import org.oliot.model.epcis.ILMDType;
import org.oliot.model.epcis.ObjectEventExtension2Type;
import org.oliot.model.epcis.QuantityElementType;
import org.oliot.model.epcis.QuantityEventExtensionType;
import org.oliot.model.epcis.ReadPointExtensionType;
import org.oliot.model.epcis.TransactionEventExtension2Type;
import org.oliot.model.epcis.TransformationEventExtensionType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

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

public class MongoReaderUtil {

	static AttributeType getAttributeType(Document doc, String key, BsonValue attrValue) {
		AttributeType attrType = new AttributeType();
		Element element = doc.createElement("attribute");
		attrType.setId(key);
		BsonType attrValueType = attrValue.getBsonType();
		List<Object> content = new ArrayList<Object>();
		if (attrValueType == BsonType.STRING) {
			String value = attrValue.asString().getValue();
			element.setTextContent(value);
			content.add(value);
		} else if (attrValueType == BsonType.INT32) {
			String value = String.valueOf(attrValue.asInt32().getValue());
			element.setTextContent(value);
			content.add(value);
		} else if (attrValueType == BsonType.INT64) {
			String value = String.valueOf(attrValue.asInt64().getValue());
			element.setTextContent(value);
			content.add(value);
		} else if (attrValueType == BsonType.DOUBLE) {
			String value = String.valueOf(attrValue.asDouble().getValue());
			element.setTextContent(value);
			content.add(value);
		} else if (attrValueType == BsonType.BOOLEAN) {
			String value = String.valueOf(attrValue.asBoolean().getValue());
			element.setTextContent(value);
			content.add(value);
		} else if (attrValueType == BsonType.DATE_TIME) {
			String value = String.valueOf(attrValue.asDateTime().getValue());
			element.setTextContent(value);
			content.add(value);
		}
		attrType.setContent(content);
		return attrType;
	}

	static XMLGregorianCalendar getXMLGregorianCalendar(BsonDateTime bdt) {
		try {
			GregorianCalendar eventCalendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
			eventCalendar.setTimeInMillis(bdt.getValue());
			XMLGregorianCalendar xmlEventTime;
			xmlEventTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(eventCalendar);
			return xmlEventTime;
		} catch (DatatypeConfigurationException e) {
			Configuration.logger.error(e.toString());
		}
		return null;
	}

	static String getDateStream(BsonDateTime bdt) {
		Date date = new Date(bdt.getValue());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		return sdf.format(date);
	}

	static EPCISEventExtensionType putEPCISEventExtensionType(BsonDocument dbObject, int zone) {
		EPCISEventExtensionType eeet = new EPCISEventExtensionType();
		if (dbObject.get("eventID") != null) {
			eeet.setEventID(dbObject.getString("eventID").getValue());
		} else {
			if (dbObject.containsKey("_id")) {
				eeet.setEventID(dbObject.getObjectId("_id").getValue().toHexString());
			}
		}
		if (dbObject.get("errorDeclaration") != null) {
			ErrorDeclarationType edt = new ErrorDeclarationType();
			BsonDocument error = dbObject.getDocument("errorDeclaration");
			if (error.containsKey("declarationTime")) {
				edt.setDeclarationTime(getXMLGregorianCalendar(error.getDateTime("declarationTime")));
			}
			if (error.containsKey("reason")) {
				edt.setReason(error.getString("reason").getValue());
			}
			if (error.containsKey("correctiveEventIDs")) {
				BsonArray correctiveEventIDs = error.getArray("correctiveEventIDs");
				List<String> correctiveIDs = new ArrayList<String>();
				Iterator<BsonValue> cIDIterator = correctiveEventIDs.iterator();
				while (cIDIterator.hasNext()) {
					String cID = cIDIterator.next().asString().getValue();
					correctiveIDs.add(cID);
				}
				if (correctiveIDs.size() != 0) {
					CorrectiveEventIDsType ceit = new CorrectiveEventIDsType();
					ceit.setCorrectiveEventID(correctiveIDs);
					edt.setCorrectiveEventIDs(ceit);
				}
			}
			if (error.containsKey("any")) {
				edt.setAny(putAny(error.getDocument("any"), null));
			}
			eeet.setErrorDeclaration(edt);
		}
		return eeet;
	}

	static List<QuantityElementType> putQuantityElementTypeList(BsonArray quantityDBList) {
		List<QuantityElementType> qetList = new ArrayList<QuantityElementType>();

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
					qet.setQuantity(BigDecimal.valueOf(quantityDouble));
				}
				if (uom != null)
					qet.setUom(uom.asString().getValue());
				qetList.add(qet);
			}
		}
		return qetList;
	}

	static List<Object> putAny(BsonDocument anyObject, Document doc) {
		try {
			if (doc == null) {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = dbf.newDocumentBuilder();
				doc = builder.newDocument();
			}
			// Get Namespaces
			Iterator<String> anyKeysIterN = anyObject.keySet().iterator();
			Map<String, String> nsMap = new HashMap<String, String>();
			while (anyKeysIterN.hasNext()) {
				String anyKeyN = anyKeysIterN.next();
				if (anyObject.containsKey(anyKeyN) && anyObject.get(anyKeyN).getBsonType().equals(BsonType.STRING)
						&& anyKeyN.startsWith("@")) {
					nsMap.put(anyKeyN.substring(1, anyKeyN.length()), anyObject.getString(anyKeyN).getValue());
				}
			}
			Iterator<String> anyKeysIter = anyObject.keySet().iterator();
			List<Object> elementList = new ArrayList<Object>();
			while (anyKeysIter.hasNext()) {
				String anyKey = anyKeysIter.next();
				if (anyKey.startsWith("@"))
					continue;
				BsonType type = anyObject.get(anyKey).getBsonType();
				String value = null;
				List<Object> nodeList = null;
				if (type == BsonType.STRING) {
					value = anyObject.getString(anyKey).getValue();
				} else if (type == BsonType.INT32) {
					value = String.valueOf(anyObject.getInt32(anyKey).getValue());
				} else if (type == BsonType.INT64) {
					value = String.valueOf(anyObject.getInt64(anyKey).getValue());
				} else if (type == BsonType.DOUBLE) {
					value = String.valueOf(anyObject.getDouble(anyKey).getValue());
				} else if (type == BsonType.BOOLEAN) {
					value = String.valueOf(anyObject.getBoolean(anyKey).getValue());
				} else if (type == BsonType.DATE_TIME) {
					value = getDateStream(anyObject.getDateTime(anyKey));
				} else if (type == BsonType.DOCUMENT) {
					BsonDocument anyDocument = anyObject.getDocument(anyKey);
					if (anyDocument.containsKey("type") && anyDocument.containsKey("coordinates")) {
						List<Double> list = new ArrayList<Double>();
						Iterator<BsonValue> iter = anyDocument.getArray("coordinates").iterator();
						while (iter.hasNext()) {
							BsonValue val = iter.next();
							if (val.getBsonType() == BsonType.DOUBLE) {
								list.add(val.asDouble().getValue());
							} else if (val.getBsonType() == BsonType.ARRAY) {
								Iterator<BsonValue> iter2 = val.asArray().iterator();
								while (iter2.hasNext()) {
									BsonValue val2 = iter2.next();
									if (val2.getBsonType() == BsonType.DOUBLE) {
										list.add(val2.asDouble().getValue());
									}
								}
							}
						}
						value = list.toString();
					} else {
						nodeList = putAny(anyDocument, doc);
					}
				}

				// Get Namespace
				String[] anyKeyCheck = anyKey.split("#");
				String namespaceURI = null;
				String localName = null;
				String prefix = null;
				String qname = null;
				if (anyKeyCheck.length >= 2) {
					namespaceURI = "";
					for (int i = 0; i < anyKeyCheck.length - 1; i++) {
						namespaceURI += anyKeyCheck[i] + "#";
					}
					namespaceURI = namespaceURI.substring(0, namespaceURI.length() - 1);
					localName = anyKeyCheck[anyKeyCheck.length - 1];
					prefix = nsMap.get(namespaceURI).toString();
					qname = prefix + ":" + localName;
				} else {
					qname = anyKey;
				}

				if (anyKey != null) {
					Element element = doc.createElement(qname);
					if (prefix != null && namespaceURI != null) {
						element.setAttribute("xmlns:" + prefix, decodeMongoObjectKey(namespaceURI));
					}
					if (value != null) {
						element.setTextContent(value);
						elementList.add(element);
					}
					if (nodeList != null) {
						for (Object obj : nodeList) {
							element.appendChild((Node) obj);
						}
						elementList.add(element);
					}
				}
			}
			return elementList;
		} catch (ParserConfigurationException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
		}
		return null;
	}

	static ILMDType putILMD(ILMDType ilmd, BsonDocument anyObject) {
		ilmd.setAny(putAny(anyObject, null));
		return ilmd;
	}

	static BusinessLocationExtensionType putBusinessLocationExtension(BusinessLocationExtensionType object,
			BsonDocument extension) {
		try {
			if (extension.get("any") != null) {
				BsonDocument anyObject = extension.getDocument("any");
				// Get Namespaces
				Iterator<String> anyKeysIterN = anyObject.keySet().iterator();
				Map<String, String> nsMap = new HashMap<String, String>();
				while (anyKeysIterN.hasNext()) {
					String anyKeyN = anyKeysIterN.next();
					String valueN = anyObject.getString(anyKeyN).getValue();
					if (anyKeyN.startsWith("@")) {
						nsMap.put(anyKeyN.substring(1, anyKeyN.length()), valueN);
					}
				}
				// Process Any
				Iterator<String> anyKeysIter = anyObject.keySet().iterator();
				List<Object> elementList = new ArrayList<Object>();
				while (anyKeysIter.hasNext()) {
					String anyKey = anyKeysIter.next();
					if (anyKey.startsWith("@"))
						continue;
					String value = anyObject.getString(anyKey).getValue();
					// Get Namespace
					String[] anyKeyCheck = anyKey.split(":");
					String namespace = null;
					String namespaceURI = null;
					if (anyKeyCheck.length == 2) {
						namespace = anyKeyCheck[0];
						namespaceURI = nsMap.get(namespace).toString();
					}
					if (anyKey != null && value != null) {
						DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
						DocumentBuilder builder = dbf.newDocumentBuilder();
						Document doc = builder.newDocument();

						Node node = doc.createElement("value");
						node.setTextContent(value);
						Element element = doc.createElement(anyKey);
						if (namespace != null) {
							element.setAttribute("xmlns:" + namespace, namespaceURI);
						}
						element.appendChild(node);
						elementList.add(element);
					}
				}
				object.setAny(elementList);
			}
			if (extension.get("otherAttributes") != null) {
				Map<QName, String> otherAttributes = new HashMap<QName, String>();
				BsonDocument otherAttributeObject = extension.getDocument("otherAttributes");
				Iterator<String> otherKeysIter = otherAttributeObject.keySet().iterator();
				while (otherKeysIter.hasNext()) {
					String anyKey = otherKeysIter.next();
					String value = otherAttributeObject.get(anyKey).toString();
					otherAttributes.put(new QName("", anyKey), value);
				}
				object.setOtherAttributes(otherAttributes);
			}
		} catch (ParserConfigurationException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
		}
		return object;
	}

	static ReadPointExtensionType putReadPointExtension(ReadPointExtensionType object, BsonDocument extension) {
		try {
			if (extension.get("any") != null) {
				BsonDocument anyObject = extension.getDocument("any");
				// Get Namespaces
				Iterator<String> anyKeysIterN = anyObject.keySet().iterator();
				Map<String, String> nsMap = new HashMap<String, String>();
				while (anyKeysIterN.hasNext()) {
					String anyKeyN = anyKeysIterN.next();
					String valueN = anyObject.getString(anyKeyN).getValue();
					if (anyKeyN.startsWith("@")) {
						nsMap.put(anyKeyN.substring(1, anyKeyN.length()), valueN);
					}
				}
				// Process Any
				Iterator<String> anyKeysIter = anyObject.keySet().iterator();
				List<Object> elementList = new ArrayList<Object>();
				while (anyKeysIter.hasNext()) {
					String anyKey = anyKeysIter.next();
					if (anyKey.startsWith("@"))
						continue;
					String value = anyObject.getString(anyKey).getValue();
					// Get Namespace
					String[] anyKeyCheck = anyKey.split(":");
					String namespace = null;
					String namespaceURI = null;
					if (anyKeyCheck.length == 2) {
						namespace = anyKeyCheck[0];
						namespaceURI = nsMap.get(namespace).toString();
					}
					if (anyKey != null && value != null) {
						DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
						DocumentBuilder builder = dbf.newDocumentBuilder();
						Document doc = builder.newDocument();

						Node node = doc.createElement("value");
						node.setTextContent(value);
						Element element = doc.createElement(anyKey);
						if (namespace != null) {
							element.setAttribute("xmlns:" + namespace, namespaceURI);
						}
						element.appendChild(node);
						elementList.add(element);
					}
				}
				object.setAny(elementList);
			}
			if (extension.get("otherAttributes") != null) {
				Map<QName, String> otherAttributes = new HashMap<QName, String>();
				BsonDocument otherAttributeObject = extension.getDocument("otherAttributes");
				Iterator<String> otherKeysIter = otherAttributeObject.keySet().iterator();
				while (otherKeysIter.hasNext()) {
					String anyKey = otherKeysIter.next();
					String value = otherAttributeObject.getString(anyKey).getValue();
					otherAttributes.put(new QName("", anyKey), value);
				}
				object.setOtherAttributes(otherAttributes);
			}
		} catch (ParserConfigurationException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
		}
		return object;
	}

	static AggregationEventExtension2Type putAggregationExtension(AggregationEventExtension2Type object,
			BsonDocument extension) {
		try {
			if (extension.get("any") != null) {
				BsonDocument anyObject = extension.getDocument("any");
				// Get Namespaces
				Iterator<String> anyKeysIterN = anyObject.keySet().iterator();
				Map<String, String> nsMap = new HashMap<String, String>();
				while (anyKeysIterN.hasNext()) {
					String anyKeyN = anyKeysIterN.next();
					String valueN = anyObject.getString(anyKeyN).getValue();
					if (anyKeyN.startsWith("@")) {
						nsMap.put(anyKeyN.substring(1, anyKeyN.length()), valueN);
					}
				}
				// Process Any
				Iterator<String> anyKeysIter = anyObject.keySet().iterator();
				List<Object> elementList = new ArrayList<Object>();
				while (anyKeysIter.hasNext()) {
					String anyKey = anyKeysIter.next();
					if (anyKey.startsWith("@"))
						continue;
					String value = anyObject.getString(anyKey).getValue();
					// Get Namespace
					String[] anyKeyCheck = anyKey.split(":");
					String namespace = null;
					String namespaceURI = null;
					if (anyKeyCheck.length == 2) {
						namespace = anyKeyCheck[0];
						namespaceURI = nsMap.get(namespace).toString();
					}
					if (anyKey != null && value != null) {
						DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
						DocumentBuilder builder = dbf.newDocumentBuilder();
						Document doc = builder.newDocument();

						Node node = doc.createElement("value");
						node.setTextContent(value);
						Element element = doc.createElement(anyKey);
						if (namespace != null) {
							element.setAttribute("xmlns:" + namespace, namespaceURI);
						}
						element.appendChild(node);
						elementList.add(element);
					}
				}
				object.setAny(elementList);
			}
			if (extension.get("otherAttributes") != null) {
				Map<QName, String> otherAttributes = new HashMap<QName, String>();
				BsonDocument otherAttributeObject = extension.getDocument("otherAttributes");
				Iterator<String> otherKeysIter = otherAttributeObject.keySet().iterator();
				while (otherKeysIter.hasNext()) {
					String anyKey = otherKeysIter.next();
					String value = otherAttributeObject.getString(anyKey).getValue();
					otherAttributes.put(new QName("", anyKey), value);
				}
				object.setOtherAttributes(otherAttributes);
			}
		} catch (ParserConfigurationException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
		}
		return object;
	}

	static EPCISEventExtensionType putEPCISExtension(EPCISEventExtensionType object, BsonDocument extension) {
		/*
		 * Extension of extension may be deprecated if (extension.get("any") !=
		 * null) { BsonDocument anyObject = extension.getDocument("any"); // Get
		 * Namespaces Iterator<String> anyKeysIterN =
		 * anyObject.keySet().iterator(); Map<String, String> nsMap = new
		 * HashMap<String, String>(); while (anyKeysIterN.hasNext()) { String
		 * anyKeyN = anyKeysIterN.next(); String valueN =
		 * anyObject.getString(anyKeyN).getValue(); if (anyKeyN.startsWith("@"))
		 * { nsMap.put(anyKeyN.substring(1, anyKeyN.length()), valueN); } } //
		 * Process Any Iterator<String> anyKeysIter =
		 * anyObject.keySet().iterator(); List<Object> elementList = new
		 * ArrayList<Object>(); while (anyKeysIter.hasNext()) { String anyKey =
		 * anyKeysIter.next(); if (anyKey.startsWith("@")) continue; String
		 * value = anyObject.get(anyKey).toString(); // Get Namespace String[]
		 * anyKeyCheck = anyKey.split(":"); String namespace = null; String
		 * namespaceURI = null; if (anyKeyCheck.length == 2) { namespace =
		 * anyKeyCheck[0]; namespaceURI = nsMap.get(namespace).toString(); } if
		 * (anyKey != null && value != null) { DocumentBuilderFactory dbf =
		 * DocumentBuilderFactory.newInstance(); DocumentBuilder builder =
		 * dbf.newDocumentBuilder(); Document doc = builder.newDocument();
		 * 
		 * Node node = doc.createElement("value"); node.setTextContent(value);
		 * Element element = doc.createElement(anyKey); if (namespace != null) {
		 * element.setAttribute("xmlns:" + namespace, namespaceURI); }
		 * element.appendChild(node); elementList.add(element); } }
		 * object.setAny(elementList); }
		 */
		if (extension.get("otherAttributes") != null) {
			Map<QName, String> otherAttributes = new HashMap<QName, String>();
			BsonDocument otherAttributeObject = extension.getDocument("otherAttributes");
			Iterator<String> otherKeysIter = otherAttributeObject.keySet().iterator();
			while (otherKeysIter.hasNext()) {
				String anyKey = otherKeysIter.next();
				String value = otherAttributeObject.getString(anyKey).getValue();
				otherAttributes.put(new QName("", anyKey), value);
			}
			object.setOtherAttributes(otherAttributes);
		}
		return object;
	}

	static ObjectEventExtension2Type putObjectExtension(ObjectEventExtension2Type oee2t, BsonDocument extension) {
		try {
			if (extension.get("any") != null) {
				BsonDocument anyObject = extension.getDocument("any");
				// Get Namespaces
				Iterator<String> anyKeysIterN = anyObject.keySet().iterator();
				Map<String, String> nsMap = new HashMap<String, String>();
				while (anyKeysIterN.hasNext()) {
					String anyKeyN = anyKeysIterN.next();
					String valueN = anyObject.get(anyKeyN).toString();
					if (anyKeyN.startsWith("@")) {
						nsMap.put(anyKeyN.substring(1, anyKeyN.length()), valueN);
					}
				}
				// Process Any
				Iterator<String> anyKeysIter = anyObject.keySet().iterator();
				List<Object> elementList = new ArrayList<Object>();
				while (anyKeysIter.hasNext()) {
					String anyKey = anyKeysIter.next();
					if (anyKey.startsWith("@"))
						continue;
					String value = anyObject.get(anyKey).toString();
					// Get Namespace
					String[] anyKeyCheck = anyKey.split(":");
					String namespace = null;
					String namespaceURI = null;
					if (anyKeyCheck.length == 2) {
						namespace = anyKeyCheck[0];
						namespaceURI = nsMap.get(namespace).toString();
					}
					if (anyKey != null && value != null) {
						DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
						DocumentBuilder builder = dbf.newDocumentBuilder();
						Document doc = builder.newDocument();

						Node node = doc.createElement("value");
						node.setTextContent(value);
						Element element = doc.createElement(anyKey);
						if (namespace != null) {
							element.setAttribute("xmlns:" + namespace, namespaceURI);
						}
						element.appendChild(node);
						elementList.add(element);
					}
				}
				oee2t.setAny(elementList);
			}
			if (extension.get("otherAttributes") != null) {
				Map<QName, String> otherAttributes = new HashMap<QName, String>();
				BsonDocument otherAttributeObject = extension.getDocument("otherAttributes");
				Iterator<String> otherKeysIter = otherAttributeObject.keySet().iterator();
				while (otherKeysIter.hasNext()) {
					String anyKey = otherKeysIter.next();
					String value = otherAttributeObject.get(anyKey).toString();
					otherAttributes.put(new QName("", anyKey), value);
				}
				oee2t.setOtherAttributes(otherAttributes);
			}
		} catch (ParserConfigurationException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
		}
		return oee2t;
	}

	static QuantityEventExtensionType putQuantityExtension(QuantityEventExtensionType object, BsonDocument extension) {
		try {
			if (extension.get("any") != null) {
				BsonDocument anyObject = extension.getDocument("any");
				// Get Namespaces
				Iterator<String> anyKeysIterN = anyObject.keySet().iterator();
				Map<String, String> nsMap = new HashMap<String, String>();
				while (anyKeysIterN.hasNext()) {
					String anyKeyN = anyKeysIterN.next();
					String valueN = anyObject.get(anyKeyN).toString();
					if (anyKeyN.startsWith("@")) {
						nsMap.put(anyKeyN.substring(1, anyKeyN.length()), valueN);
					}
				}
				// Process Any
				Iterator<String> anyKeysIter = anyObject.keySet().iterator();
				List<Object> elementList = new ArrayList<Object>();
				while (anyKeysIter.hasNext()) {
					String anyKey = anyKeysIter.next();
					if (anyKey.startsWith("@"))
						continue;
					String value = anyObject.get(anyKey).toString();
					// Get Namespace
					String[] anyKeyCheck = anyKey.split(":");
					String namespace = null;
					String namespaceURI = null;
					if (anyKeyCheck.length == 2) {
						namespace = anyKeyCheck[0];
						namespaceURI = nsMap.get(namespace).toString();
					}
					if (anyKey != null && value != null) {
						DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
						DocumentBuilder builder = dbf.newDocumentBuilder();
						Document doc = builder.newDocument();

						Node node = doc.createElement("value");
						node.setTextContent(value);
						Element element = doc.createElement(anyKey);
						if (namespace != null) {
							element.setAttribute("xmlns:" + namespace, namespaceURI);
						}
						element.appendChild(node);
						elementList.add(element);
					}
				}
				object.setAny(elementList);
			}
			if (extension.get("otherAttributes") != null) {
				Map<QName, String> otherAttributes = new HashMap<QName, String>();
				BsonDocument otherAttributeObject = extension.getDocument("otherAttributes");
				Iterator<String> otherKeysIter = otherAttributeObject.keySet().iterator();
				while (otherKeysIter.hasNext()) {
					String anyKey = otherKeysIter.next();
					String value = otherAttributeObject.get(anyKey).toString();
					otherAttributes.put(new QName("", anyKey), value);
				}
				object.setOtherAttributes(otherAttributes);
			}
		} catch (ParserConfigurationException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
		}
		return object;
	}

	static TransactionEventExtension2Type putTransactionExtension(TransactionEventExtension2Type object,
			BsonDocument extension) {
		try {
			if (extension.get("any") != null) {
				BsonDocument anyObject = extension.getDocument("any");
				// Get Namespaces
				Iterator<String> anyKeysIterN = anyObject.keySet().iterator();
				Map<String, String> nsMap = new HashMap<String, String>();
				while (anyKeysIterN.hasNext()) {
					String anyKeyN = anyKeysIterN.next();
					String valueN = anyObject.get(anyKeyN).toString();
					if (anyKeyN.startsWith("@")) {
						nsMap.put(anyKeyN.substring(1, anyKeyN.length()), valueN);
					}
				}
				// Process Any
				Iterator<String> anyKeysIter = anyObject.keySet().iterator();
				List<Object> elementList = new ArrayList<Object>();
				while (anyKeysIter.hasNext()) {
					String anyKey = anyKeysIter.next();
					if (anyKey.startsWith("@"))
						continue;
					String value = anyObject.get(anyKey).toString();
					// Get Namespace
					String[] anyKeyCheck = anyKey.split(":");
					String namespace = null;
					String namespaceURI = null;
					if (anyKeyCheck.length == 2) {
						namespace = anyKeyCheck[0];
						namespaceURI = nsMap.get(namespace).toString();
					}
					if (anyKey != null && value != null) {
						DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
						DocumentBuilder builder = dbf.newDocumentBuilder();
						Document doc = builder.newDocument();

						Node node = doc.createElement("value");
						node.setTextContent(value);
						Element element = doc.createElement(anyKey);
						if (namespace != null) {
							element.setAttribute("xmlns:" + namespace, namespaceURI);
						}
						element.appendChild(node);
						elementList.add(element);
					}
				}
				object.setAny(elementList);
			}
			if (extension.get("otherAttributes") != null) {
				Map<QName, String> otherAttributes = new HashMap<QName, String>();
				BsonDocument otherAttributeObject = extension.getDocument("otherAttributes");
				Iterator<String> otherKeysIter = otherAttributeObject.keySet().iterator();
				while (otherKeysIter.hasNext()) {
					String anyKey = otherKeysIter.next();
					String value = otherAttributeObject.get(anyKey).toString();
					otherAttributes.put(new QName("", anyKey), value);
				}
				object.setOtherAttributes(otherAttributes);
			}
		} catch (ParserConfigurationException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
		}
		return object;
	}

	static TransformationEventExtensionType putTransformationExtension(TransformationEventExtensionType object,
			BsonDocument extension) {
		try {
			if (extension.get("any") != null) {
				BsonDocument anyObject = extension.getDocument("any");
				// Get Namespaces
				Iterator<String> anyKeysIterN = anyObject.keySet().iterator();
				Map<String, String> nsMap = new HashMap<String, String>();
				while (anyKeysIterN.hasNext()) {
					String anyKeyN = anyKeysIterN.next();
					String valueN = anyObject.get(anyKeyN).toString();
					if (anyKeyN.startsWith("@")) {
						nsMap.put(anyKeyN.substring(1, anyKeyN.length()), valueN);
					}
				}
				// Process Any
				Iterator<String> anyKeysIter = anyObject.keySet().iterator();
				List<Object> elementList = new ArrayList<Object>();
				while (anyKeysIter.hasNext()) {
					String anyKey = anyKeysIter.next();
					if (anyKey.startsWith("@"))
						continue;
					String value = anyObject.get(anyKey).toString();
					// Get Namespace
					String[] anyKeyCheck = anyKey.split(":");
					String namespace = null;
					String namespaceURI = null;
					if (anyKeyCheck.length == 2) {
						namespace = anyKeyCheck[0];
						namespaceURI = nsMap.get(namespace).toString();
					}
					if (anyKey != null && value != null) {
						DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
						DocumentBuilder builder = dbf.newDocumentBuilder();
						Document doc = builder.newDocument();

						Node node = doc.createElement("value");
						node.setTextContent(value);
						Element element = doc.createElement(anyKey);
						if (namespace != null) {
							element.setAttribute("xmlns:" + namespace, namespaceURI);
						}
						element.appendChild(node);
						elementList.add(element);
					}
				}
				object.setAny(elementList);
			}
			if (extension.get("otherAttributes") != null) {
				Map<QName, String> otherAttributes = new HashMap<QName, String>();
				BsonDocument otherAttributeObject = extension.getDocument("otherAttributes");
				Iterator<String> otherKeysIter = otherAttributeObject.keySet().iterator();
				while (otherKeysIter.hasNext()) {
					String anyKey = otherKeysIter.next();
					String value = otherAttributeObject.get(anyKey).toString();
					otherAttributes.put(new QName("", anyKey), value);
				}
				object.setOtherAttributes(otherAttributes);
			}
		} catch (ParserConfigurationException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
		}
		return object;
	}

	static public String decodeMongoObjectKey(String key) {
		key = key.replace("\uff0e", ".");
		return key;
	}
}
