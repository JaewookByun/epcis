package org.oliot.epcis.converter.data.bson_to_pojo;

import org.bson.Document;
import org.oliot.epcis.model.AttributeType;
import org.oliot.epcis.model.IDListType;
import org.oliot.epcis.model.VocabularyElementListType;
import org.oliot.epcis.model.VocabularyElementType;
import org.oliot.epcis.model.VocabularyType;
import org.oliot.epcis.util.SOAPMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.oliot.epcis.util.BSONReadUtil.*;

/**
 * Copyright (C) 2020-2023. (Jaewook Byun) all rights reserved.
 * <p>
 * This project is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * The class converts Masterdata from a POJO to a storage unit, BSON.
 * <p>
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr, Associate Director, Auto-ID Labs, Korea,
 *         bjw0829@gmail.com
 */
public class MasterdataConverter {

	@SuppressWarnings("unchecked")
	public VocabularyType convert(String type, Set<Document> objSet, SOAPMessage message) {

		VocabularyType vt = new VocabularyType();
		vt.setType(type);
		VocabularyElementListType velt = new VocabularyElementListType();

		List<VocabularyElementType> vetList = objSet.parallelStream().map(obj -> {
			VocabularyElementType vet = new VocabularyElementType();
			vet.setId(obj.getString("id"));

			if (obj.containsKey("children")) {
				IDListType idListType = new IDListType();
				idListType.setId(obj.getList("children", String.class));
				vet.setChildren(idListType);
			}

			if (obj.containsKey("attributes")) {
				List<AttributeType> attrList = new ArrayList<>();
				Document attributeObj = obj.get("attributes", Document.class);

				for (String entryKey : attributeObj.keySet()) {
					Object value = attributeObj.get(entryKey);
					if (value instanceof String) {
						AttributeType at = new AttributeType();
						at.setId(decodeMongoObjectKey(entryKey));
						List<Object> tList = new ArrayList<>();
						tList.add(value.toString());
						at.setContent(tList);
						attrList.add(at);
					} else if (value instanceof Long) {
						AttributeType at = new AttributeType();
						at.setId(decodeMongoObjectKey(entryKey));
						List<Object> tList = new ArrayList<>();
						tList.add(getDate((Long) value));
						at.setContent(tList);
						attrList.add(at);
					} else if (value instanceof List) {
						if (entryKey.equals("urn:epcglobal:cbv:mda#additionalTradeItemID")) {
							AttributeType at = new AttributeType();
							at.setId(decodeMongoObjectKey(entryKey));
							List<Object> tList = new ArrayList<>();
							tList.add(getAdditionalTradeItemIDElement(message.getMessage(), (List<Document>) value));
							at.setContent(tList);
							attrList.add(at);
						} else {
							for (Object inner : (List<?>) value) {
								if (inner instanceof String) {
									AttributeType at = new AttributeType();
									at.setId(decodeMongoObjectKey(entryKey));
									List<Object> tList = new ArrayList<>();
									tList.add(inner.toString());
									at.setContent(tList);
									attrList.add(at);
								} else {
									AttributeType at = new AttributeType();
									at.setId(decodeMongoObjectKey(entryKey));
									at.setContent(getAny((Document) inner, message, null));
									attrList.add(at);
								}
							}
						}
					} else {
						String qname = decodeMongoObjectKey(entryKey);
						if (qname.equals("urn:epcglobal:cbv:mda#drainedWeight")
								|| qname.equals("urn:epcglobal:cbv:mda#grossWeight")
								|| qname.equals("urn:epcglobal:cbv:mda#netWeight")) {
							Document objValue = (Document) value;
							AttributeType at = new AttributeType();
							at.setId(qname);
							attrList.add(getMeasurementExtensionElement(at, objValue));
						} else {
							AttributeType at = new AttributeType();
							at.setId(qname);
							at.setContent(getAny((Document) value, message, null));
							attrList.add(at);
						}
					}

				}
				vet.setAttribute(attrList);
			}
			return vet;
		}).collect(Collectors.toList());

		velt.setVocabularyElement(vetList);
		vt.setVocabularyElementList(velt);
		return vt;
	}

	@SuppressWarnings("unchecked")
	public VocabularyType convert(String type, Document obj, SOAPMessage message) {

		VocabularyType vt = new VocabularyType();
		vt.setType(type);
		VocabularyElementListType velt = new VocabularyElementListType();

		List<VocabularyElementType> vetList = new ArrayList<VocabularyElementType>();

		VocabularyElementType vet = new VocabularyElementType();
		vet.setId(obj.getString("id"));

		if (obj.containsKey("children")) {
			IDListType idListType = new IDListType();
			idListType.setId(obj.getList("children", String.class));
			vet.setChildren(idListType);
		}

		if (obj.containsKey("attributes")) {
			List<AttributeType> attrList = new ArrayList<>();
			Document attributeObj = obj.get("attributes", Document.class);

			for (String entryKey : attributeObj.keySet()) {
				Object value = attributeObj.get(entryKey);
				if (value instanceof String) {
					AttributeType at = new AttributeType();
					at.setId(decodeMongoObjectKey(entryKey));
					List<Object> tList = new ArrayList<>();
					tList.add(value.toString());
					at.setContent(tList);
					attrList.add(at);
				} else if (value instanceof Long) {
					AttributeType at = new AttributeType();
					at.setId(decodeMongoObjectKey(entryKey));
					List<Object> tList = new ArrayList<>();
					tList.add(getDate((Long) value));
					at.setContent(tList);
					attrList.add(at);
				} else if (value instanceof List) {
					if (entryKey.equals("urn:epcglobal:cbv:mda#additionalTradeItemID")) {
						AttributeType at = new AttributeType();
						at.setId(decodeMongoObjectKey(entryKey));
						List<Object> tList = new ArrayList<>();
						tList.add(getAdditionalTradeItemIDElement(message.getMessage(), (List<Document>) value));
						at.setContent(tList);
						attrList.add(at);
					} else {
						for (Object inner : (List<?>) value) {
							if (inner instanceof String) {
								AttributeType at = new AttributeType();
								at.setId(decodeMongoObjectKey(entryKey));
								List<Object> tList = new ArrayList<>();
								tList.add(inner.toString());
								at.setContent(tList);
								attrList.add(at);
							} else {
								AttributeType at = new AttributeType();
								at.setId(decodeMongoObjectKey(entryKey));
								at.setContent(getAny((Document) inner, message, null));
								attrList.add(at);
							}
						}
					}
				} else {
					String qname = decodeMongoObjectKey(entryKey);
					if (qname.equals("urn:epcglobal:cbv:mda#drainedWeight")
							|| qname.equals("urn:epcglobal:cbv:mda#grossWeight")
							|| qname.equals("urn:epcglobal:cbv:mda#netWeight")) {
						Document objValue = (Document) value;
						AttributeType at = new AttributeType();
						at.setId(qname);
						attrList.add(getMeasurementExtensionElement(at, objValue));
					} else {
						AttributeType at = new AttributeType();
						at.setId(qname);
						at.setContent(getAny((Document) value, message, null));
						attrList.add(at);
					}
				}

			}
			vet.setAttribute(attrList);
		}
		vetList.add(vet);
		velt.setVocabularyElement(vetList);
		vt.setVocabularyElementList(velt);
		return vt;
	}
}
