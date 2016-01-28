package org.oliot.epcis.serde.mongodb;

import static org.oliot.epcis.serde.mongodb.MongoWriterUtil.getAnyMap;
import static org.oliot.epcis.serde.mongodb.MongoWriterUtil.getOtherAttributesMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.json.JSONArray;
import org.json.JSONObject;
import org.oliot.model.epcis.AttributeType;
import org.oliot.model.epcis.EPC;
import org.oliot.model.epcis.IDListType;
import org.oliot.model.epcis.VocabularyElementListType;
import org.oliot.model.epcis.VocabularyElementType;
import org.oliot.model.epcis.VocabularyExtensionType;
import org.oliot.model.epcis.VocabularyType;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
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
@WritingConverter
public class MasterDataWriteConverter implements Converter<VocabularyType, DBObject> {

	public DBObject convert(VocabularyType vocabulary) {

		DBObject dbo = new BasicDBObject();

		if (vocabulary.getAny() != null && vocabulary.getAny().isEmpty() == false) {
			List<Object> objList = vocabulary.getAny();
			Map<String, String> map2Save = getAnyMap(objList);
			if (map2Save != null && map2Save.isEmpty() == false)
				dbo.put("any", map2Save);
		}

		if (vocabulary.getOtherAttributes() != null && vocabulary.getOtherAttributes().isEmpty() == false) {
			Map<QName, String> map = vocabulary.getOtherAttributes();
			Map<String, String> map2Save = getOtherAttributesMap(map);
			if (map2Save.isEmpty() == false)
				dbo.put("otherAttributes", map2Save);
		}

		// Extension
		DBObject extension = new BasicDBObject();
		if (vocabulary.getExtension() != null) {
			VocabularyExtensionType vet = vocabulary.getExtension();
			if (vet.getAny() != null) {
				List<Object> objList = vet.getAny();
				Map<String, String> map2Save = getAnyMap(objList);
				if (map2Save.isEmpty() == false)
					extension.put("any", map2Save);
			}

			if (vet.getOtherAttributes() != null) {
				Map<QName, String> map = vet.getOtherAttributes();
				Map<String, String> map2Save = getOtherAttributesMap(map);
				if (map2Save.isEmpty() == false)
					extension.put("otherAttributes", map2Save);
			}
		}
		if (extension != null && extension.toMap().isEmpty() == false)
			dbo.put("extension", extension);

		if (vocabulary.getType() != null)
			dbo.put("type", vocabulary.getType());

		if (vocabulary.getVocabularyElementList() != null) {
			VocabularyElementListType velt = vocabulary.getVocabularyElementList();
			List<VocabularyElementType> vetList = velt.getVocabularyElement();
			BasicDBList vocDBList = new BasicDBList();
			for (int i = 0; i < vetList.size(); i++) {
				VocabularyElementType vocabularyElement = vetList.get(i);

				DBObject elementObject = new BasicDBObject();

				if (vocabularyElement.getId() != null)
					elementObject.put("id", vocabularyElement.getId());

				// According to XML rule
				// Specification is not possible
				// Select Simple Content as one of two option
				if (vocabularyElement.getAttribute() != null) {
					List<AttributeType> attributeList = vocabularyElement.getAttribute();
					BasicDBList attrList = new BasicDBList();
					for (int j = 0; j < attributeList.size(); j++) {
						AttributeType attribute = attributeList.get(j);
						DBObject attrObject = new BasicDBObject();
						String key = attribute.getId();
						String value = attribute.getValue();
						attrObject.put("id", key);
						attrObject.put("value", value);
						attrList.add(attrObject);
					}
					elementObject.put("attributeList", attrList);
				}

				if (vocabularyElement.getChildren() != null) {
					IDListType idlist = vocabularyElement.getChildren();
					elementObject.put("children", idlist.getId());
				}

				if (vocabularyElement.getAny() != null) {
					List<Object> objList = vocabularyElement.getAny();
					Map<String, String> map2Save = getAnyMap(objList);
					if (map2Save.isEmpty() == false)
						elementObject.put("any", map2Save);
				}

				if (vocabularyElement.getOtherAttributes() != null) {
					Map<QName, String> map = vocabularyElement.getOtherAttributes();
					Map<String, String> map2Save = getOtherAttributesMap(map);
					if (map2Save.isEmpty() == false)
						elementObject.put("otherAttributes", map2Save);
				}
				vocDBList.add(elementObject);
			}
			dbo.put("vocabularyList", vocDBList);
		}
		return dbo;
	}

	public int capture(VocabularyType vocabulary, Integer gcpLength) {

		DBObject dbo = new BasicDBObject();

		ApplicationContext ctx = new GenericXmlApplicationContext("classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");

		DBCollection collection = mongoOperation.getCollection("MasterData");

		// Mongo Initialization
		if (vocabulary.getVocabularyElementList() != null) {
			VocabularyElementListType velt = vocabulary.getVocabularyElementList();
			List<VocabularyElementType> vetList = velt.getVocabularyElement();
			for (int i = 0; i < vetList.size(); i++) {
				VocabularyElementType vocabularyElement = vetList.get(i);

				// ID is mandatory
				if (vocabularyElement.getId() == null) {
					continue;
				}

				// Existence Check
				String vocID = vocabularyElement.getId();

				// Barcode Transform
				vocID = MongoWriterUtil.getVocabularyEPC(vocabulary.getType(), vocID, gcpLength);
				
				// each id should have one document
				DBObject voc = collection.findOne(new BasicDBObject("id", vocID));

				if (voc == null) {
					voc = new BasicDBObject();

				}

				if (vocabulary.getType() != null)
					dbo.put("type", vocabulary.getType());
				if (vocabularyElement.getId() != null)
					dbo.put("id", vocID);

				// Prepare vocabularyList JSONObject
				Object tempAttrObj = dbo.get("attributes");
				DBObject attrObj = null;
				if (tempAttrObj == null) {
					attrObj = new BasicDBObject();
				} else {
					attrObj = (DBObject) tempAttrObj;
				}

				// According to XML rule
				// Specification is not possible
				// Select Simple Content as one of two option
				if (vocabularyElement.getAttribute() != null) {
					List<AttributeType> attributeList = vocabularyElement.getAttribute();
					for (int j = 0; j < attributeList.size(); j++) {
						AttributeType attribute = attributeList.get(j);
						String key = attribute.getId();
						key = encodeMongoObjectKey(key);
						String value = attribute.getValue();
						attrObj.put(key, value);
					}
					attrObj.put("lastUpdated", System.currentTimeMillis());
					dbo.put("attributes", attrObj);
				}

				// If children found, overwrite previous one(s)
				if (vocabularyElement.getChildren() != null) {
					IDListType idlist = vocabularyElement.getChildren();
					dbo.put("children", idlist.getId());
				}
				collection.update(new BasicDBObject("id", vocID), dbo, true, false);
			}
		}
		((AbstractApplicationContext) ctx).close();
		return 0;
	}
	
	@SuppressWarnings("unchecked")
	public int json_capture(JSONObject event) {

		DBObject dbo = new BasicDBObject();

		ApplicationContext ctx = new GenericXmlApplicationContext("classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");

		DBCollection collection = mongoOperation.getCollection("MasterData");

				// ID is mandatory
				if (event.has("id") == false) {
					System.out.println("no id!!");
				}

				// Existence Check
				String vocID = event.getString("id");

				// each id should have one document
				DBObject voc = collection.findOne(new BasicDBObject("id", vocID));

				if (voc == null) {
					voc = new BasicDBObject();

				}

				if (event.has("type") != false)
					dbo.put("type", event.getString("type"));
				if (event.has("id") != false)
					dbo.put("id", event.getString("id"));

				// Prepare vocabularyList JSONObject
				Object tempAttrObj = dbo.get("attributes");
				DBObject attrObj = null;
				if (tempAttrObj == null) {
					attrObj = new BasicDBObject();
				} else {
					attrObj = (DBObject) tempAttrObj;
				}

				// According to XML rule
				// Specification is not possible
				// Select Simple Content as one of two option
				if (event.getJSONObject("attributes") != null) {
					JSONObject json_attr = event.getJSONObject("attributes");
					Iterator<String> json_iter = json_attr.keys();
					while(json_iter.hasNext()){
						String temp = json_iter.next();
						attrObj.put(temp, json_attr.get(temp));					
					}
					attrObj.put("lastUpdated", System.currentTimeMillis());
					dbo.put("attributes", attrObj);
				}
				
				// If children found, overwrite previous one(s)
				if (event.has("children") == true){
					List<String> abc = new ArrayList<String>();
					JSONArray abc_json = event.getJSONArray("children");
					
					for(int i = 0 ; i <abc_json.length() ; i++){
						abc.add(abc_json.get(i).toString());
					}
					dbo.put("children", abc);
				}
				
				collection.update(new BasicDBObject("id", vocID), dbo, true, false);
		
		((AbstractApplicationContext) ctx).close();
		return 0;
	}
	

	public int capture(List<EPC> epcList, Map<String, String> map2Save) {
		if (map2Save == null) {
			return 0;
		}
		if (map2Save.size() == 0) {
			return 0;
		}
		DBObject dbo = new BasicDBObject();

		ApplicationContext ctx = new GenericXmlApplicationContext("classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");

		DBCollection collection = mongoOperation.getCollection("MasterData");

		for (int i = 0; i < epcList.size(); i++) {
			EPC epc = epcList.get(i);
			String id = epc.getValue();

			// each id should have one document
			DBObject voc = collection.findOne(new BasicDBObject("id", id));

			if (voc == null) {
				voc = new BasicDBObject();
			}

			dbo.put("type", "urn:epcglobal:epcis:vtype:EPCInstance");
			dbo.put("id", id);

			// Prepare vocabularyList JSONObject
			Object tempAttrObj = dbo.get("attributes");
			DBObject attrObj = null;
			if (tempAttrObj == null) {
				attrObj = new BasicDBObject();
			} else {
				attrObj = (DBObject) tempAttrObj;
			}

			Iterator<String> mapIter = map2Save.keySet().iterator();
			while (mapIter.hasNext()) {
				String key = mapIter.next();
				key = encodeMongoObjectKey(key);
				String value = map2Save.get(key);
				attrObj.put(key, value);
			}

			attrObj.put("lastUpdated", System.currentTimeMillis());
			dbo.put("attributes", attrObj);

			collection.update(new BasicDBObject("id", id), dbo, true, false);
		}

		((AbstractApplicationContext) ctx).close();
		return 0;
	}

	public String encodeMongoObjectKey(String key) {
		key = key.replace(".", "\uff0e");
		return key;
	}

	public String decodeMongoObjectKey(String key) {
		key = key.replace("\uff0e", ".");
		return key;
	}

}
