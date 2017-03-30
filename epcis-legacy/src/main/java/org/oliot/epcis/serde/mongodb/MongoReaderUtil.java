package org.oliot.epcis.serde.mongodb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Level;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonType;
import org.bson.BsonValue;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.model.epcis.AggregationEventExtension2Type;
import org.oliot.model.epcis.BusinessLocationExtensionType;
import org.oliot.model.epcis.EPCISEventExtensionType;
import org.oliot.model.epcis.ILMDExtensionType;
import org.oliot.model.epcis.ILMDType;
import org.oliot.model.epcis.ObjectEventExtension2Type;
import org.oliot.model.epcis.QuantityEventExtensionType;
import org.oliot.model.epcis.ReadPointExtensionType;
import org.oliot.model.epcis.SensorEventExtensionType;
import org.oliot.model.epcis.TransactionEventExtension2Type;
import org.oliot.model.epcis.TransformationEventExtensionType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Copyright (C) 2015 Jaewook Byun
 *
 * This project is part of Oliot (oliot.org), pursuing the implementation of Electronic Product Code Information Service(EPCIS) v1.1 specification in EPCglobal.
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

public class MongoReaderUtil {

	static List<Object> putAny(BsonDocument anyObject) {
		try {
			// Get Namespaces
			Iterator<String> anyKeysIterN = anyObject.keySet().iterator();
			Map<String, String> nsMap = new HashMap<String, String>();
			while (anyKeysIterN.hasNext()) {
				String anyKeyN = anyKeysIterN.next();
				if (anyObject.containsKey(anyKeyN) && anyObject.get(anyKeyN).getBsonType().equals(BsonType.STRING) && anyKeyN.startsWith("@")) {
					nsMap.put(anyKeyN.substring(1, anyKeyN.length()), anyObject.getString(anyKeyN).getValue());
				}
			}
			Iterator<String> anyKeysIter = anyObject.keySet().iterator();
			List<Object> elementList = new ArrayList<Object>();

			while (anyKeysIter.hasNext()) {
				// If namespace, continue to next iteration
				String anyKey = anyKeysIter.next();
				if (anyKey.startsWith("@"))
					continue;

				// Convert bson value to a more readable String (List are
				// handled differently)
				BsonValue bsonValue = anyObject.get(anyKey);
				boolean isBsonArray = (bsonValue.getBsonType() == BsonType.ARRAY);
				boolean isBsonDocument = (bsonValue.getBsonType() == BsonType.DOCUMENT);
				String value = convertBsonValueToString(bsonValue);

				// Get Namespace
				String[] anyKeyCheck = anyKey.split(":");
				String namespace = null;
				String namespaceURI = null;
				if (anyKeyCheck.length == 2) {
					namespace = anyKeyCheck[0];
					namespaceURI = nsMap.get(namespace).toString();
				}
				if (anyKey != null && (value != null || isBsonArray || isBsonDocument)) {
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					DocumentBuilder builder = dbf.newDocumentBuilder();
					Document doc = builder.newDocument();

					if (isBsonArray) {
						Element parentElement = convertBsonValueToArrayElement(bsonValue, anyKey, doc, namespace, namespaceURI);
						elementList.add(parentElement);

					} else if (isBsonDocument) {
						String documentSuffix = "Document";
						String parentTagName = anyKey.endsWith(documentSuffix) ? anyKey : anyKey + documentSuffix;
						Element parentElement = createElement(doc, parentTagName, namespace, namespaceURI);
						for(Element child : convertBsonValueToDocumentElements(bsonValue, doc, namespace, namespaceURI)) {
							parentElement.appendChild(child);
						}
						elementList.add(parentElement);
					} else {
						Element element = createElement(doc, anyKey, namespace, namespaceURI);
						element.setTextContent(value);
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

	private static String convertBsonValueToString(BsonValue bsonValue) {
		BsonType type = bsonValue.getBsonType();
		String value = null;
		if (type == BsonType.STRING) {
			value = bsonValue.asString().getValue();
		} else if (type == BsonType.INT32) {
			value = String.valueOf(bsonValue.asInt32().getValue());
		} else if (type == BsonType.INT64) {
			value = String.valueOf(bsonValue.asInt64().getValue());
		} else if (type == BsonType.DOUBLE) {
			value = String.valueOf(bsonValue.asDouble().getValue());
		} else if (type == BsonType.BOOLEAN) {
			value = String.valueOf(bsonValue.asBoolean().getValue());
		}
		return value;
	}

	private static Element createElement(Document doc, String tagName, String namespace, String namespaceURI) {
		Element element = doc.createElement(tagName);
		if (namespace != null) {
			element.setAttribute("xmlns:" + namespace, namespaceURI);
		}
		return element;
	}

	private static Element convertBsonValueToArrayElement(BsonValue bsonValue, String anyKey, Document doc, String namespace, String namespaceURI) {
		String listSuffix = "List";
		String parentTagName = anyKey.endsWith(listSuffix) ? anyKey : anyKey + listSuffix;
		String childTagName = anyKey.endsWith(listSuffix)
				? anyKey.substring(0, anyKey.length() - listSuffix.length()) : anyKey;

		Element parentElement = createElement(doc, parentTagName, namespace, namespaceURI);
		BsonArray arr = bsonValue.asArray();
		Iterator<BsonValue> it = arr.iterator();
		while (it.hasNext()) {
			bsonValue = it.next();
			Element element = createElement(doc, childTagName, namespace, namespaceURI);
			if (bsonValue.isArray()) {
				element.appendChild(convertBsonValueToArrayElement(bsonValue, anyKey, doc, namespace, namespaceURI));
			} else if (bsonValue.isDocument()) {
				for(Element child : convertBsonValueToDocumentElements(bsonValue, doc, namespace, namespaceURI)) {
					element.appendChild(child);
				}
			} else {
				String value = convertBsonValueToString(bsonValue);
				element.setTextContent(value);
			}

			parentElement.appendChild(element);
		}
		return parentElement;
	}

	private static List<Element> convertBsonValueToDocumentElements(BsonValue bsonValue, Document doc, String namespace, String namespaceURI) {
		List<Element> elements = new LinkedList<>();
		BsonDocument document = bsonValue.asDocument();
		for (String key : document.keySet()) {
			bsonValue = document.get(key);
			Element element;
			if (bsonValue.isArray()) {
				element = convertBsonValueToArrayElement(bsonValue, key, doc, namespace, namespaceURI);
			} else if (bsonValue.isDocument()) {
				element = doc.createElement(key);
				for(Element child : convertBsonValueToDocumentElements(bsonValue, doc, namespace, namespaceURI)) {
					element.appendChild(child);
				}
			} else {
				element = doc.createElement(key);
				String value = convertBsonValueToString(bsonValue);
				element.setTextContent(value);
			}
			elements.add(element);
		}
		return elements;
	}

	static ILMDType putILMD(ILMDType ilmd, BsonDocument anyObject) {
		try {
			ILMDExtensionType ilmdExtension = new ILMDExtensionType();
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
			ilmdExtension.setAny(elementList);
			ilmd.setExtension(ilmdExtension);
		} catch (ParserConfigurationException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
		}
		return ilmd;
	}

	static BusinessLocationExtensionType putBusinessLocationExtension(BusinessLocationExtensionType object, BsonDocument extension) {
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

	static AggregationEventExtension2Type putAggregationExtension(AggregationEventExtension2Type object, BsonDocument extension) {
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

	static SensorEventExtensionType putSensorExtension(SensorEventExtensionType object, BsonDocument extension) {
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

	static TransactionEventExtension2Type putTransactionExtension(TransactionEventExtension2Type object, BsonDocument extension) {
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

	static TransformationEventExtensionType putTransformationExtension(TransformationEventExtensionType object, BsonDocument extension) {
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
}
