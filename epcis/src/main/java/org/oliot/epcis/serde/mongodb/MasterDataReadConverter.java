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
import org.oliot.model.epcis.AttributeType;
import org.oliot.model.epcis.IDListType;
import org.oliot.model.epcis.VocabularyElementListType;
import org.oliot.model.epcis.VocabularyElementType;
import org.oliot.model.epcis.VocabularyExtensionType;
import org.oliot.model.epcis.VocabularyType;
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

@Component
@ReadingConverter
public class MasterDataReadConverter implements
		Converter<DBObject, VocabularyType> {

	public VocabularyType convert(DBObject dbObject) {
		try {

			VocabularyType vt = new VocabularyType();

			if (dbObject.get("any") != null) {
				BasicDBObject anyObject = (BasicDBObject) dbObject.get("any");
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
				vt.setAny(elementList);
			}
			if (dbObject.get("otherAttributes") != null) {
				Map<QName, String> otherAttributes = new HashMap<QName, String>();
				BasicDBObject otherAttributeObject = (BasicDBObject) dbObject
						.get("otherAttributes");
				Iterator<String> otherKeysIter = otherAttributeObject.keySet()
						.iterator();
				while (otherKeysIter.hasNext()) {
					String anyKey = otherKeysIter.next();
					String value = otherAttributeObject.get(anyKey).toString();
					otherAttributes.put(new QName("", anyKey), value);
				}
				vt.setOtherAttributes(otherAttributes);
			}

			// extension
			if (dbObject.get("extension") != null) {
				VocabularyExtensionType veet = new VocabularyExtensionType();
				BasicDBObject extension = (BasicDBObject) dbObject
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
							DocumentBuilder builder = dbf.newDocumentBuilder();
							Document doc = builder.newDocument();

							Node node = doc.createElement("value");
							node.setTextContent(value);
							Element element = doc.createElement(anyKey);
							element.appendChild(node);
							elementList.add(element);
						}
					}
					veet.setAny(elementList);
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
					veet.setOtherAttributes(otherAttributes);
				}
				vt.setExtension(veet);
			}

			if (dbObject.get("type") != null)
				vt.setType((String) dbObject.get("type"));

			if (dbObject.get("vocabularyList") != null) {
				BasicDBList vocList = (BasicDBList) dbObject
						.get("vocabularyList");
				VocabularyElementListType velt = new VocabularyElementListType();
				List<VocabularyElementType> vetList = new ArrayList<VocabularyElementType>();
				for (int i = 0; i < vocList.size(); i++) {
					DBObject vocElement = (DBObject) vocList.get(i);

					VocabularyElementType vet = new VocabularyElementType();
					if (vocElement.get("id") != null)
						vet.setId(vocElement.get("id").toString());

					if (vocElement.get("any") != null) {
						BasicDBObject anyObject = (BasicDBObject) vocElement
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
						vet.setAny(elementList);
					}
					if (vocElement.get("otherAttributes") != null) {
						Map<QName, String> otherAttributes = new HashMap<QName, String>();
						BasicDBObject otherAttributeObject = (BasicDBObject) vocElement
								.get("otherAttributes");
						Iterator<String> otherKeysIter = otherAttributeObject
								.keySet().iterator();
						while (otherKeysIter.hasNext()) {
							String anyKey = otherKeysIter.next();
							String value = otherAttributeObject.get(anyKey)
									.toString();
							otherAttributes.put(new QName("", anyKey), value);
						}
						vet.setOtherAttributes(otherAttributes);
					}
					if (vocElement.get("attributeList") != null) {
						List<AttributeType> attrListType = new ArrayList<AttributeType>();
						BasicDBList attrList = (BasicDBList) vocElement
								.get("attributeList");
						for (int j = 0; j < attrList.size(); j++) {
							AttributeType attrType = new AttributeType();
							BasicDBObject attr = (BasicDBObject) attrList
									.get(j);
							attrType.setId(attr.getString("id"));
							attrType.setValue(attr.getString("value"));
							attrListType.add(attrType);
						}
						vet.setAttribute(attrListType);
					}
					if (vocElement.get("children") != null) {
						IDListType idListType = new IDListType();
						List<String> idList = new ArrayList<String>();
						BasicDBList children = (BasicDBList) vocElement
								.get("children");
						for (int j = 0; j < children.size(); j++) {
							String child = (String) children.get(j);
							idList.add(child);
						}
						idListType.setId(idList);
						vet.setChildren(idListType);
					}
					vetList.add(vet);
				}
				velt.setVocabularyElement(vetList);
				vt.setVocabularyElementList(velt);
			}
			return vt;
		} catch (ParserConfigurationException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
		}
		return null;
	}
}
