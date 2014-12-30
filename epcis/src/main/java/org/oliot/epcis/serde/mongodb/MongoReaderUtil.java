package org.oliot.epcis.serde.mongodb;

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

import com.mongodb.BasicDBObject;

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

public class MongoReaderUtil {

	static ILMDType putILMD(ILMDType ilmd, BasicDBObject anyObject) {
		try {
			ILMDExtensionType ilmdExtension = new ILMDExtensionType();
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
					DocumentBuilderFactory dbf = DocumentBuilderFactory
							.newInstance();
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

	static BusinessLocationExtensionType putBusinessLocationExtension(
			BusinessLocationExtensionType object, BasicDBObject extension) {
		try {
			if (extension.get("any") != null) {
				BasicDBObject anyObject = (BasicDBObject) extension.get("any");
				// Get Namespaces
				Iterator<String> anyKeysIterN = anyObject.keySet().iterator();
				Map<String, String> nsMap = new HashMap<String, String>();
				while (anyKeysIterN.hasNext()) {
					String anyKeyN = anyKeysIterN.next();
					String valueN = anyObject.get(anyKeyN).toString();
					if (anyKeyN.startsWith("@")) {
						nsMap.put(anyKeyN.substring(1, anyKeyN.length()),
								valueN);
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
						DocumentBuilderFactory dbf = DocumentBuilderFactory
								.newInstance();
						DocumentBuilder builder = dbf.newDocumentBuilder();
						Document doc = builder.newDocument();

						Node node = doc.createElement("value");
						node.setTextContent(value);
						Element element = doc.createElement(anyKey);
						if (namespace != null) {
							element.setAttribute("xmlns:" + namespace,
									namespaceURI);
						}
						element.appendChild(node);
						elementList.add(element);
					}
				}
				object.setAny(elementList);
			}
			if (extension.get("otherAttributes") != null) {
				Map<QName, String> otherAttributes = new HashMap<QName, String>();
				BasicDBObject otherAttributeObject = (BasicDBObject) extension
						.get("otherAttributes");
				Iterator<String> otherKeysIter = otherAttributeObject.keySet()
						.iterator();
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

	static ReadPointExtensionType putReadPointExtension(
			ReadPointExtensionType object, BasicDBObject extension) {
		try {
			if (extension.get("any") != null) {
				BasicDBObject anyObject = (BasicDBObject) extension.get("any");
				// Get Namespaces
				Iterator<String> anyKeysIterN = anyObject.keySet().iterator();
				Map<String, String> nsMap = new HashMap<String, String>();
				while (anyKeysIterN.hasNext()) {
					String anyKeyN = anyKeysIterN.next();
					String valueN = anyObject.get(anyKeyN).toString();
					if (anyKeyN.startsWith("@")) {
						nsMap.put(anyKeyN.substring(1, anyKeyN.length()),
								valueN);
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
						DocumentBuilderFactory dbf = DocumentBuilderFactory
								.newInstance();
						DocumentBuilder builder = dbf.newDocumentBuilder();
						Document doc = builder.newDocument();

						Node node = doc.createElement("value");
						node.setTextContent(value);
						Element element = doc.createElement(anyKey);
						if (namespace != null) {
							element.setAttribute("xmlns:" + namespace,
									namespaceURI);
						}
						element.appendChild(node);
						elementList.add(element);
					}
				}
				object.setAny(elementList);
			}
			if (extension.get("otherAttributes") != null) {
				Map<QName, String> otherAttributes = new HashMap<QName, String>();
				BasicDBObject otherAttributeObject = (BasicDBObject) extension
						.get("otherAttributes");
				Iterator<String> otherKeysIter = otherAttributeObject.keySet()
						.iterator();
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

	static AggregationEventExtension2Type putAggregationExtension(
			AggregationEventExtension2Type object, BasicDBObject extension) {
		try {
			if (extension.get("any") != null) {
				BasicDBObject anyObject = (BasicDBObject) extension.get("any");
				// Get Namespaces
				Iterator<String> anyKeysIterN = anyObject.keySet().iterator();
				Map<String, String> nsMap = new HashMap<String, String>();
				while (anyKeysIterN.hasNext()) {
					String anyKeyN = anyKeysIterN.next();
					String valueN = anyObject.get(anyKeyN).toString();
					if (anyKeyN.startsWith("@")) {
						nsMap.put(anyKeyN.substring(1, anyKeyN.length()),
								valueN);
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
						DocumentBuilderFactory dbf = DocumentBuilderFactory
								.newInstance();
						DocumentBuilder builder = dbf.newDocumentBuilder();
						Document doc = builder.newDocument();

						Node node = doc.createElement("value");
						node.setTextContent(value);
						Element element = doc.createElement(anyKey);
						if (namespace != null) {
							element.setAttribute("xmlns:" + namespace,
									namespaceURI);
						}
						element.appendChild(node);
						elementList.add(element);
					}
				}
				object.setAny(elementList);
			}
			if (extension.get("otherAttributes") != null) {
				Map<QName, String> otherAttributes = new HashMap<QName, String>();
				BasicDBObject otherAttributeObject = (BasicDBObject) extension
						.get("otherAttributes");
				Iterator<String> otherKeysIter = otherAttributeObject.keySet()
						.iterator();
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

	static EPCISEventExtensionType putEPCISExtension(
			EPCISEventExtensionType object, BasicDBObject extension) {
		try {
			if (extension.get("any") != null) {
				BasicDBObject anyObject = (BasicDBObject) extension.get("any");
				// Get Namespaces
				Iterator<String> anyKeysIterN = anyObject.keySet().iterator();
				Map<String, String> nsMap = new HashMap<String, String>();
				while (anyKeysIterN.hasNext()) {
					String anyKeyN = anyKeysIterN.next();
					String valueN = anyObject.get(anyKeyN).toString();
					if (anyKeyN.startsWith("@")) {
						nsMap.put(anyKeyN.substring(1, anyKeyN.length()),
								valueN);
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
						DocumentBuilderFactory dbf = DocumentBuilderFactory
								.newInstance();
						DocumentBuilder builder = dbf.newDocumentBuilder();
						Document doc = builder.newDocument();

						Node node = doc.createElement("value");
						node.setTextContent(value);
						Element element = doc.createElement(anyKey);
						if (namespace != null) {
							element.setAttribute("xmlns:" + namespace,
									namespaceURI);
						}
						element.appendChild(node);
						elementList.add(element);
					}
				}
				object.setAny(elementList);
			}
			if (extension.get("otherAttributes") != null) {
				Map<QName, String> otherAttributes = new HashMap<QName, String>();
				BasicDBObject otherAttributeObject = (BasicDBObject) extension
						.get("otherAttributes");
				Iterator<String> otherKeysIter = otherAttributeObject.keySet()
						.iterator();
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

	static ObjectEventExtension2Type putObjectExtension(
			ObjectEventExtension2Type oee2t, BasicDBObject extension) {
		try {
			if (extension.get("any") != null) {
				BasicDBObject anyObject = (BasicDBObject) extension.get("any");
				// Get Namespaces
				Iterator<String> anyKeysIterN = anyObject.keySet().iterator();
				Map<String, String> nsMap = new HashMap<String, String>();
				while (anyKeysIterN.hasNext()) {
					String anyKeyN = anyKeysIterN.next();
					String valueN = anyObject.get(anyKeyN).toString();
					if (anyKeyN.startsWith("@")) {
						nsMap.put(anyKeyN.substring(1, anyKeyN.length()),
								valueN);
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
						DocumentBuilderFactory dbf = DocumentBuilderFactory
								.newInstance();
						DocumentBuilder builder = dbf.newDocumentBuilder();
						Document doc = builder.newDocument();

						Node node = doc.createElement("value");
						node.setTextContent(value);
						Element element = doc.createElement(anyKey);
						if (namespace != null) {
							element.setAttribute("xmlns:" + namespace,
									namespaceURI);
						}
						element.appendChild(node);
						elementList.add(element);
					}
				}
				oee2t.setAny(elementList);
			}
			if (extension.get("otherAttributes") != null) {
				Map<QName, String> otherAttributes = new HashMap<QName, String>();
				BasicDBObject otherAttributeObject = (BasicDBObject) extension
						.get("otherAttributes");
				Iterator<String> otherKeysIter = otherAttributeObject.keySet()
						.iterator();
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

	static QuantityEventExtensionType putQuantityExtension(
			QuantityEventExtensionType object, BasicDBObject extension) {
		try {
			if (extension.get("any") != null) {
				BasicDBObject anyObject = (BasicDBObject) extension.get("any");
				// Get Namespaces
				Iterator<String> anyKeysIterN = anyObject.keySet().iterator();
				Map<String, String> nsMap = new HashMap<String, String>();
				while (anyKeysIterN.hasNext()) {
					String anyKeyN = anyKeysIterN.next();
					String valueN = anyObject.get(anyKeyN).toString();
					if (anyKeyN.startsWith("@")) {
						nsMap.put(anyKeyN.substring(1, anyKeyN.length()),
								valueN);
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
						DocumentBuilderFactory dbf = DocumentBuilderFactory
								.newInstance();
						DocumentBuilder builder = dbf.newDocumentBuilder();
						Document doc = builder.newDocument();

						Node node = doc.createElement("value");
						node.setTextContent(value);
						Element element = doc.createElement(anyKey);
						if (namespace != null) {
							element.setAttribute("xmlns:" + namespace,
									namespaceURI);
						}
						element.appendChild(node);
						elementList.add(element);
					}
				}
				object.setAny(elementList);
			}
			if (extension.get("otherAttributes") != null) {
				Map<QName, String> otherAttributes = new HashMap<QName, String>();
				BasicDBObject otherAttributeObject = (BasicDBObject) extension
						.get("otherAttributes");
				Iterator<String> otherKeysIter = otherAttributeObject.keySet()
						.iterator();
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

	static SensorEventExtensionType putSensorExtension(
			SensorEventExtensionType object, BasicDBObject extension) {
		try {
			if (extension.get("any") != null) {
				BasicDBObject anyObject = (BasicDBObject) extension.get("any");
				// Get Namespaces
				Iterator<String> anyKeysIterN = anyObject.keySet().iterator();
				Map<String, String> nsMap = new HashMap<String, String>();
				while (anyKeysIterN.hasNext()) {
					String anyKeyN = anyKeysIterN.next();
					String valueN = anyObject.get(anyKeyN).toString();
					if (anyKeyN.startsWith("@")) {
						nsMap.put(anyKeyN.substring(1, anyKeyN.length()),
								valueN);
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
						DocumentBuilderFactory dbf = DocumentBuilderFactory
								.newInstance();
						DocumentBuilder builder = dbf.newDocumentBuilder();
						Document doc = builder.newDocument();

						Node node = doc.createElement("value");
						node.setTextContent(value);
						Element element = doc.createElement(anyKey);
						if (namespace != null) {
							element.setAttribute("xmlns:" + namespace,
									namespaceURI);
						}
						element.appendChild(node);
						elementList.add(element);
					}
				}
				object.setAny(elementList);
			}
			if (extension.get("otherAttributes") != null) {
				Map<QName, String> otherAttributes = new HashMap<QName, String>();
				BasicDBObject otherAttributeObject = (BasicDBObject) extension
						.get("otherAttributes");
				Iterator<String> otherKeysIter = otherAttributeObject.keySet()
						.iterator();
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

	static TransactionEventExtension2Type putTransactionExtension(
			TransactionEventExtension2Type object, BasicDBObject extension) {
		try {
			if (extension.get("any") != null) {
				BasicDBObject anyObject = (BasicDBObject) extension.get("any");
				// Get Namespaces
				Iterator<String> anyKeysIterN = anyObject.keySet().iterator();
				Map<String, String> nsMap = new HashMap<String, String>();
				while (anyKeysIterN.hasNext()) {
					String anyKeyN = anyKeysIterN.next();
					String valueN = anyObject.get(anyKeyN).toString();
					if (anyKeyN.startsWith("@")) {
						nsMap.put(anyKeyN.substring(1, anyKeyN.length()),
								valueN);
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
						DocumentBuilderFactory dbf = DocumentBuilderFactory
								.newInstance();
						DocumentBuilder builder = dbf.newDocumentBuilder();
						Document doc = builder.newDocument();

						Node node = doc.createElement("value");
						node.setTextContent(value);
						Element element = doc.createElement(anyKey);
						if (namespace != null) {
							element.setAttribute("xmlns:" + namespace,
									namespaceURI);
						}
						element.appendChild(node);
						elementList.add(element);
					}
				}
				object.setAny(elementList);
			}
			if (extension.get("otherAttributes") != null) {
				Map<QName, String> otherAttributes = new HashMap<QName, String>();
				BasicDBObject otherAttributeObject = (BasicDBObject) extension
						.get("otherAttributes");
				Iterator<String> otherKeysIter = otherAttributeObject.keySet()
						.iterator();
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

	static TransformationEventExtensionType putTransformationExtension(
			TransformationEventExtensionType object, BasicDBObject extension) {
		try {
			if (extension.get("any") != null) {
				BasicDBObject anyObject = (BasicDBObject) extension.get("any");
				// Get Namespaces
				Iterator<String> anyKeysIterN = anyObject.keySet().iterator();
				Map<String, String> nsMap = new HashMap<String, String>();
				while (anyKeysIterN.hasNext()) {
					String anyKeyN = anyKeysIterN.next();
					String valueN = anyObject.get(anyKeyN).toString();
					if (anyKeyN.startsWith("@")) {
						nsMap.put(anyKeyN.substring(1, anyKeyN.length()),
								valueN);
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
						DocumentBuilderFactory dbf = DocumentBuilderFactory
								.newInstance();
						DocumentBuilder builder = dbf.newDocumentBuilder();
						Document doc = builder.newDocument();

						Node node = doc.createElement("value");
						node.setTextContent(value);
						Element element = doc.createElement(anyKey);
						if (namespace != null) {
							element.setAttribute("xmlns:" + namespace,
									namespaceURI);
						}
						element.appendChild(node);
						elementList.add(element);
					}
				}
				object.setAny(elementList);
			}
			if (extension.get("otherAttributes") != null) {
				Map<QName, String> otherAttributes = new HashMap<QName, String>();
				BasicDBObject otherAttributeObject = (BasicDBObject) extension
						.get("otherAttributes");
				Iterator<String> otherKeysIter = otherAttributeObject.keySet()
						.iterator();
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
