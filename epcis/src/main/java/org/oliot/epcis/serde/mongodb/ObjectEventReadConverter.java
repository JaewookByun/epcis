package org.oliot.epcis.serde.mongodb;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Level;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.model.epcis.ActionType;
import org.oliot.model.epcis.BusinessLocationExtensionType;
import org.oliot.model.epcis.BusinessLocationType;
import org.oliot.model.epcis.BusinessTransactionListType;
import org.oliot.model.epcis.BusinessTransactionType;
import org.oliot.model.epcis.DestinationListType;
import org.oliot.model.epcis.EPC;
import org.oliot.model.epcis.EPCISEventExtensionType;
import org.oliot.model.epcis.EPCListType;
import org.oliot.model.epcis.ILMDExtensionType;
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
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Copyright (C) 2014 KAIST RESL
 *
 * This project is part of Oliot (oliot.org), pursuing the implementation of
 * Electronic Product Code Information Service(EPCIS) v1.1 specification in
 * EPCglobal.
 * [http://www.gs1.org/gsmp/kc/epcglobal/epcis/epcis_1_1-standard-20140520.pdf]
 * 
 *
 * @author Jack Jaewook Byun, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         bjw0829@kaist.ac.kr
 */
@Component
@ReadingConverter
public class ObjectEventReadConverter implements
		Converter<DBObject, ObjectEventType> {

	public ObjectEventType convert(DBObject dbObject) {

		try {
			ObjectEventType objectEventType = new ObjectEventType();
			if (dbObject.get("eventTime") != null) {
				long eventTime = (long) dbObject.get("eventTime");
				GregorianCalendar eventCalendar = new GregorianCalendar();
				eventCalendar.setTimeInMillis(eventTime);
				XMLGregorianCalendar xmlEventTime = DatatypeFactory
						.newInstance().newXMLGregorianCalendar(eventCalendar);
				objectEventType.setEventTime(xmlEventTime);
			}
			if (dbObject.get("eventTimeZoneOffset") != null) {
				String eventTimeZoneOffset = (String) dbObject
						.get("eventTimeZoneOffset");
				objectEventType.setEventTimeZoneOffset(eventTimeZoneOffset);
			}
			if (dbObject.get("recordTime") != null) {
				long eventTime = (long) dbObject.get("recordTime");
				GregorianCalendar recordCalendar = new GregorianCalendar();
				recordCalendar.setTimeInMillis(eventTime);
				XMLGregorianCalendar xmlRecordTime = DatatypeFactory
						.newInstance().newXMLGregorianCalendar(recordCalendar);
				objectEventType.setRecordTime(xmlRecordTime);
			}
			if (dbObject.get("epcList") != null) {
				BasicDBList epcListM = (BasicDBList) dbObject.get("epcList");
				EPCListType epcListType = new EPCListType();
				List<EPC> epcs = new ArrayList<EPC>();
				for (int i = 0; i < epcListM.size(); i++) {
					EPC epc = new EPC();
					BasicDBObject epcObject = (BasicDBObject) epcListM.get(i);
					epc.setValue(epcObject.getString("epc"));
					epcs.add(epc);
				}
				epcListType.setEpc(epcs);
				objectEventType.setEpcList(epcListType);
			}
			if (dbObject.get("action") != null) {
				objectEventType.setAction(ActionType.fromValue(dbObject.get(
						"action").toString()));
			}
			if (dbObject.get("bizStep") != null) {
				objectEventType.setBizStep(dbObject.get("bizStep").toString());
			}
			if (dbObject.get("disposition") != null) {
				objectEventType.setDisposition(dbObject.get("disposition")
						.toString());
			}
			if (dbObject.get("baseExtension") != null) {
				EPCISEventExtensionType eeet = new EPCISEventExtensionType();
				BasicDBObject baseExtension = (BasicDBObject) dbObject
						.get("baseExtension");
				if (baseExtension.get("any") != null) {
					BasicDBObject anyObject = (BasicDBObject) baseExtension
							.get("any");
					Iterator<String> anyKeysIter = anyObject.keySet()
							.iterator();
					List<Object> elementList = new ArrayList<Object>();
					while (anyKeysIter.hasNext()) {
						String anyKey = anyKeysIter.next();
						String value = anyObject.get(anyKey).toString();
						if (anyKey != null && value != null) {
							DocumentBuilderFactory dbf = DocumentBuilderFactory
									.newInstance();
							DocumentBuilder builder = dbf.newDocumentBuilder();
							Document doc = builder.newDocument();

							Node node = doc.createElement("value");
							node.setTextContent(value);
							Element element = doc.createElement(anyKey);
							element.appendChild(node);
							elementList.add(element);
						}
					}
					eeet.setAny(elementList);
				}
				if (baseExtension.get("otherAttributes") != null) {
					Map<QName, String> otherAttributes = new HashMap<QName, String>();
					BasicDBObject otherAttributeObject = (BasicDBObject) baseExtension
							.get("otherAttributes");
					Iterator<String> otherKeysIter = otherAttributeObject
							.keySet().iterator();
					while (otherKeysIter.hasNext()) {
						String anyKey = otherKeysIter.next();
						String value = otherAttributeObject.get(anyKey)
								.toString();
						otherAttributes.put(new QName("", anyKey), value);
					}
					eeet.setOtherAttributes(otherAttributes);
				}
				objectEventType.setBaseExtension(eeet);
			}
			if (dbObject.get("readPoint") != null) {
				BasicDBObject readPointObject = (BasicDBObject) dbObject
						.get("readPoint");
				ReadPointType readPointType = new ReadPointType();
				if (readPointObject.get("id") != null) {
					readPointType.setId(readPointObject.get("id").toString());
				}
				if (readPointObject.get("extension") != null) {
					ReadPointExtensionType rpet = new ReadPointExtensionType();
					//
					BasicDBObject extension = (BasicDBObject) readPointObject
							.get("extension");
					if (extension.get("any") != null) {
						BasicDBObject anyObject = (BasicDBObject) extension
								.get("any");
						Iterator<String> anyKeysIter = anyObject.keySet()
								.iterator();
						List<Object> elementList = new ArrayList<Object>();
						while (anyKeysIter.hasNext()) {
							String anyKey = anyKeysIter.next();
							String value = anyObject.get(anyKey).toString();
							if (anyKey != null && value != null) {
								DocumentBuilderFactory dbf = DocumentBuilderFactory
										.newInstance();
								DocumentBuilder builder = dbf
										.newDocumentBuilder();
								Document doc = builder.newDocument();

								Node node = doc.createElement("value");
								node.setTextContent(value);
								Element element = doc.createElement(anyKey);
								element.appendChild(node);
								elementList.add(element);
							}
						}
						rpet.setAny(elementList);
					}
					if (extension.get("otherAttributes") != null) {
						Map<QName, String> otherAttributes = new HashMap<QName, String>();
						BasicDBObject otherAttributeObject = (BasicDBObject) extension
								.get("otherAttributes");
						Iterator<String> otherKeysIter = otherAttributeObject
								.keySet().iterator();
						while (otherKeysIter.hasNext()) {
							String anyKey = otherKeysIter.next();
							String value = otherAttributeObject.get(anyKey)
									.toString();
							otherAttributes.put(new QName("", anyKey), value);
						}
						rpet.setOtherAttributes(otherAttributes);
					}
					//
					readPointType.setExtension(rpet);
				}
				objectEventType.setReadPoint(readPointType);
			}
			// BusinessLocation
			if (dbObject.get("bizLocation") != null) {
				BasicDBObject bizLocationObject = (BasicDBObject) dbObject
						.get("bizLocation");
				BusinessLocationType bizLocationType = new BusinessLocationType();
				if (bizLocationObject.get("id") != null) {
					bizLocationType.setId(bizLocationObject.get("id")
							.toString());
				}
				if (bizLocationObject.get("extension") != null) {
					BusinessLocationExtensionType blet = new BusinessLocationExtensionType();
					//
					BasicDBObject extension = (BasicDBObject) bizLocationObject
							.get("extension");
					if (extension.get("any") != null) {
						BasicDBObject anyObject = (BasicDBObject) extension
								.get("any");
						Iterator<String> anyKeysIter = anyObject.keySet()
								.iterator();
						List<Object> elementList = new ArrayList<Object>();
						while (anyKeysIter.hasNext()) {
							String anyKey = anyKeysIter.next();
							String value = anyObject.get(anyKey).toString();
							if (anyKey != null && value != null) {
								DocumentBuilderFactory dbf = DocumentBuilderFactory
										.newInstance();
								DocumentBuilder builder = dbf
										.newDocumentBuilder();
								Document doc = builder.newDocument();

								Node node = doc.createElement("value");
								node.setTextContent(value);
								Element element = doc.createElement(anyKey);
								element.appendChild(node);
								elementList.add(element);
							}
						}
						blet.setAny(elementList);
					}
					if (extension.get("otherAttributes") != null) {
						Map<QName, String> otherAttributes = new HashMap<QName, String>();
						BasicDBObject otherAttributeObject = (BasicDBObject) extension
								.get("otherAttributes");
						Iterator<String> otherKeysIter = otherAttributeObject
								.keySet().iterator();
						while (otherKeysIter.hasNext()) {
							String anyKey = otherKeysIter.next();
							String value = otherAttributeObject.get(anyKey)
									.toString();
							otherAttributes.put(new QName("", anyKey), value);
						}
						blet.setOtherAttributes(otherAttributes);
					}
					//
					bizLocationType.setExtension(blet);
				}
				objectEventType.setBizLocation(bizLocationType);
			}
			if (dbObject.get("bizTransactionList") != null) {
				BasicDBList bizTranList = (BasicDBList) dbObject
						.get("bizTransactionList");
				BusinessTransactionListType btlt = new BusinessTransactionListType();
				List<BusinessTransactionType> bizTranArrayList = new ArrayList<BusinessTransactionType>();
				for (int i = 0; i < bizTranList.size(); i++) {
					// DBObject, key and value
					BasicDBObject bizTran = (BasicDBObject) bizTranList.get(i);
					BusinessTransactionType btt = new BusinessTransactionType();
					Iterator<String> keyIter = bizTran.keySet().iterator();
					// at most one bizTran
					if (keyIter.hasNext()) {
						String key = keyIter.next();
						String value = bizTran.getString(key);
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

			if (dbObject.get("ilmd") != null) {
				ILMDType ilmd = new ILMDType();
				ILMDExtensionType ilmdExtension = new ILMDExtensionType();

				BasicDBObject anyObject = (BasicDBObject) dbObject.get("ilmd");
				Iterator<String> anyKeysIter = anyObject.keySet().iterator();
				List<Object> elementList = new ArrayList<Object>();
				while (anyKeysIter.hasNext()) {
					String anyKey = anyKeysIter.next();
					String value = anyObject.get(anyKey).toString();
					if (anyKey != null && value != null) {
						DocumentBuilderFactory dbf = DocumentBuilderFactory
								.newInstance();
						DocumentBuilder builder = dbf.newDocumentBuilder();
						Document doc = builder.newDocument();

						Node node = doc.createElement("value");
						node.setTextContent(value);
						Element element = doc.createElement(anyKey);
						element.appendChild(node);
						elementList.add(element);
					}
				}
				ilmdExtension.setAny(elementList);
				ilmd.setExtension(ilmdExtension);
				objectEventType.setIlmd(ilmd);
			}

			// Extension Field
			if (dbObject.get("extension") != null) {
				ObjectEventExtensionType oeet = new ObjectEventExtensionType();
				BasicDBObject extObject = (BasicDBObject) dbObject
						.get("extension");
				// Quantity
				if (extObject.get("quantityList") != null) {
					QuantityListType qlt = new QuantityListType();
					List<QuantityElementType> qetList = new ArrayList<QuantityElementType>();
					BasicDBList quantityDBList = (BasicDBList) extObject
							.get("quantityList");
					for (int i = 0; i < quantityDBList.size(); i++) {
						QuantityElementType qet = new QuantityElementType();
						BasicDBObject quantityDBObject = (BasicDBObject) quantityDBList
								.get(i);
						Object epcClassObject = quantityDBObject
								.get("epcClass");
						Object quantity = quantityDBObject.get("quantity");
						Object uom = quantityDBObject.get("uom");
						if (epcClassObject != null && quantity != null
								&& uom != null) {
							qet.setEpcClass(epcClassObject.toString());
							double quantityDouble = (double) quantity;
							qet.setQuantity((float) quantityDouble);
							qet.setUom(uom.toString());
							qetList.add(qet);
						}
					}
					qlt.setQuantityElement(qetList);
					oeet.setQuantityList(qlt);
				}
				// SourceList
				if (extObject.get("sourceList") != null) {
					// Source Dest Type : Key / Value
					SourceListType slt = new SourceListType();
					List<SourceDestType> sdtList = new ArrayList<SourceDestType>();
					BasicDBList sourceDBList = (BasicDBList) extObject
							.get("sourceList");
					for (int i = 0; i < sourceDBList.size(); i++) {
						BasicDBObject sdObject = (BasicDBObject) sourceDBList
								.get(i);
						// DBObject, key and value
						SourceDestType sdt = new SourceDestType();
						Iterator<String> keyIter = sdObject.keySet().iterator();
						// at most one bizTran
						if (keyIter.hasNext()) {
							String key = keyIter.next();
							String value = sdObject.getString(key);
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
					BasicDBList destinationDBList = (BasicDBList) extObject
							.get("destinationList");
					for (int i = 0; i < destinationDBList.size(); i++) {
						BasicDBObject sdObject = (BasicDBObject) destinationDBList
								.get(i);
						// DBObject, key and value
						SourceDestType sdt = new SourceDestType();
						Iterator<String> keyIter = sdObject.keySet().iterator();
						// at most one bizTran
						if (keyIter.hasNext()) {
							String key = keyIter.next();
							String value = sdObject.getString(key);
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
				// extension2
				if (extObject.get("extension") != null) {
					ObjectEventExtension2Type oee2t = new ObjectEventExtension2Type();
					BasicDBObject extension = (BasicDBObject) extObject
							.get("extension");
					if (extension.get("any") != null) {
						BasicDBObject anyObject = (BasicDBObject) extension
								.get("any");
						Iterator<String> anyKeysIter = anyObject.keySet()
								.iterator();
						List<Object> elementList = new ArrayList<Object>();
						while (anyKeysIter.hasNext()) {
							String anyKey = anyKeysIter.next();
							String value = anyObject.get(anyKey).toString();
							if (anyKey != null && value != null) {
								DocumentBuilderFactory dbf = DocumentBuilderFactory
										.newInstance();
								DocumentBuilder builder = dbf
										.newDocumentBuilder();
								Document doc = builder.newDocument();

								Node node = doc.createElement("value");
								node.setTextContent(value);
								Element element = doc.createElement(anyKey);
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
						Iterator<String> otherKeysIter = otherAttributeObject
								.keySet().iterator();
						while (otherKeysIter.hasNext()) {
							String anyKey = otherKeysIter.next();
							String value = otherAttributeObject.get(anyKey)
									.toString();
							otherAttributes.put(new QName("", anyKey), value);
						}
						oee2t.setOtherAttributes(otherAttributes);
					}
					oeet.setExtension(oee2t);
				}
				objectEventType.setExtension(oeet);
			}

			return objectEventType;
		} catch (DatatypeConfigurationException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
		} catch (ParserConfigurationException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
		}
		return null;
	}
}
