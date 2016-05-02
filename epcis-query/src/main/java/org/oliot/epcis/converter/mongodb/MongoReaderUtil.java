package org.oliot.epcis.converter.mongodb;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

public class MongoReaderUtil {

	static List<QuantityElementType> putQuantityElementTypeList(BsonArray quantityDBList){
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
	
	
	static List<Object> putAny(BsonDocument anyObject) {
		try {
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
				}

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
					// element.appendChild(node);
					element.setTextContent(value);
					elementList.add(element);
				}
			}
			return elementList;
		} catch (ParserConfigurationException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
		}
		return null;
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
}
