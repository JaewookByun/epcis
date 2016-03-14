package org.oliot.epcis.serde.mongodb;

import static org.oliot.epcis.serde.mongodb.MongoWriterUtil.getAnyMap;
import static org.oliot.epcis.serde.mongodb.MongoWriterUtil.getOtherAttributesMap;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.BsonString;
import org.json.JSONArray;
import org.json.JSONObject;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.model.epcis.AttributeType;
import org.oliot.model.epcis.EPC;
import org.oliot.model.epcis.VocabularyElementListType;
import org.oliot.model.epcis.VocabularyElementType;
import org.oliot.model.epcis.VocabularyExtensionType;
import org.oliot.model.epcis.VocabularyType;

import com.mongodb.client.MongoCollection;

/**
 * Copyright (C) 2014-2016 Jaewook Byun
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

public class MasterDataWriteConverter {

	public BsonDocument convert(VocabularyType vocabulary) {

		BsonDocument dbo = new BsonDocument();

		if (vocabulary.getAny() != null && vocabulary.getAny().isEmpty() == false) {
			List<Object> objList = vocabulary.getAny();
			BsonDocument map2Save = getAnyMap(objList);
			if (map2Save != null && map2Save.isEmpty() == false)
				dbo.put("any", map2Save);
		}

		if (vocabulary.getOtherAttributes() != null && vocabulary.getOtherAttributes().isEmpty() == false) {
			Map<QName, String> map = vocabulary.getOtherAttributes();
			BsonDocument map2Save = getOtherAttributesMap(map);
			if (map2Save.isEmpty() == false)
				dbo.put("otherAttributes", map2Save);
		}

		// Extension
		BsonDocument extension = new BsonDocument();
		if (vocabulary.getExtension() != null) {
			VocabularyExtensionType vet = vocabulary.getExtension();
			if (vet.getAny() != null) {
				List<Object> objList = vet.getAny();
				BsonDocument map2Save = getAnyMap(objList);
				if (map2Save.isEmpty() == false)
					extension.put("any", map2Save);
			}

			if (vet.getOtherAttributes() != null) {
				Map<QName, String> map = vet.getOtherAttributes();
				BsonDocument map2Save = getOtherAttributesMap(map);
				if (map2Save.isEmpty() == false)
					extension.put("otherAttributes", map2Save);
			}
		}
		if (extension != null && extension.isEmpty() == false)
			dbo.put("extension", extension);

		if (vocabulary.getType() != null)
			dbo.put("type", new BsonString(vocabulary.getType()));

		if (vocabulary.getVocabularyElementList() != null) {
			VocabularyElementListType velt = vocabulary.getVocabularyElementList();
			List<VocabularyElementType> vetList = velt.getVocabularyElement();
			BsonArray vocDBList = new BsonArray();
			for (int i = 0; i < vetList.size(); i++) {
				VocabularyElementType vocabularyElement = vetList.get(i);

				BsonDocument elementObject = new BsonDocument();

				if (vocabularyElement.getId() != null)
					elementObject.put("id", new BsonString(vocabularyElement.getId()));

				// According to XML rule
				// Specification is not possible
				// Select Simple Content as one of two option
				if (vocabularyElement.getAttribute() != null) {
					List<AttributeType> attributeList = vocabularyElement.getAttribute();
					BsonArray attrList = new BsonArray();
					for (int j = 0; j < attributeList.size(); j++) {
						AttributeType attribute = attributeList.get(j);
						BsonDocument attrObject = new BsonDocument();
						String key = attribute.getId();
						String value = attribute.getValue();
						attrObject.put("id", new BsonString(key));
						attrObject.put("value", MongoWriterUtil.converseType(value));
						attrList.add(attrObject);
					}
					elementObject.put("attributeList", attrList);
				}

				if (vocabularyElement.getChildren() != null) {
					List<String> idlist = vocabularyElement.getChildren().getId();
					BsonArray bsonChildList = new BsonArray();
					for (String child : idlist) {
						bsonChildList.add(new BsonString(child));
					}
					elementObject.put("children", bsonChildList);
				}

				if (vocabularyElement.getAny() != null) {
					List<Object> objList = vocabularyElement.getAny();
					BsonDocument map2Save = getAnyMap(objList);
					if (map2Save.isEmpty() == false)
						elementObject.put("any", map2Save);
				}

				if (vocabularyElement.getOtherAttributes() != null) {
					Map<QName, String> map = vocabularyElement.getOtherAttributes();
					BsonDocument map2Save = getOtherAttributesMap(map);
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

		MongoCollection<BsonDocument> collection = Configuration.mongoDatabase.getCollection("MasterData",
				BsonDocument.class);

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
				BsonDocument voc = collection.find(new BsonDocument("id", new BsonString(vocID))).first();

				boolean isExist = false;
				if( voc != null){
					isExist = true;
				}else{
					voc = new BsonDocument();
				}
				
				if (vocabulary.getType() != null)
					voc.put("type", new BsonString(vocabulary.getType()));
				if (vocabularyElement.getId() != null)
					voc.put("id", new BsonString(vocID));

				// Prepare vocabularyList JSONObject
				BsonDocument attrObj = null;
				if (!voc.containsKey("attributes")) {
					attrObj = new BsonDocument();
				} else {
					attrObj = voc.getDocument("attributes");
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
						attrObj.put(key, MongoWriterUtil.converseType(value));
					}
					attrObj.put("lastUpdated", new BsonInt64(System.currentTimeMillis()));
					voc.put("attributes", attrObj);
				}

				// If children found, overwrite previous one(s)
				if (vocabularyElement.getChildren() != null) {
					List<String> idlist = vocabularyElement.getChildren().getId();
					BsonArray bsonIDList = new BsonArray();
					for (String id : idlist) {
						bsonIDList.add(new BsonString(id));
					}
					voc.put("children", bsonIDList);
				}

				if (isExist == false) {
					collection.insertOne(voc);
				} else {
					collection.findOneAndReplace(new BsonDocument("id", new BsonString(vocID)), voc);
				}
			}
		}
		return 0;
	}

	public int json_capture(JSONObject event) {

		MongoCollection<BsonDocument> collection = Configuration.mongoDatabase.getCollection("MasterData",
				BsonDocument.class);

		// ID is mandatory
		if (event.has("id") == false) {
			System.out.println("no id!!");
		}

		// Existence Check
		String vocID = event.getString("id");

		// each id should have one document
		BsonDocument voc = collection.find(new BsonDocument("id", new BsonString(vocID))).first();

		if (voc == null) {
			voc = new BsonDocument();

		}

		if (event.has("type") != false)
			voc.put("type", new BsonString(event.getString("type")));
		if (event.has("id") != false)
			voc.put("id", new BsonString(event.getString("id")));

		// Prepare vocabularyList JSONObject
		BsonDocument attrObj = null;
		if (!voc.containsKey("attributes")) {
			attrObj = new BsonDocument();
		} else {
			attrObj = voc.getDocument("attributes");
		}

		// According to XML rule
		// Specification is not possible
		// Select Simple Content as one of two option
		if (event.getJSONObject("attributes") != null) {
			JSONObject json_attr = event.getJSONObject("attributes");
			Iterator<String> json_iter = json_attr.keys();
			while (json_iter.hasNext()) {
				String temp = json_iter.next();
				attrObj.put(temp, new BsonString(json_attr.getString(temp)));
			}
			attrObj.put("lastUpdated", new BsonInt64(System.currentTimeMillis()));
			voc.put("attributes", attrObj);
		}

		// If children found, overwrite previous one(s)
		if (event.has("children") == true) {
			BsonArray bsonChildArray = new BsonArray();
			JSONArray jsonChildArray = event.getJSONArray("children");

			Iterator<Object> jsonChildIterator = jsonChildArray.iterator();
			while (jsonChildIterator.hasNext()) {
				String childStr = jsonChildIterator.next().toString();
				bsonChildArray.add(new BsonString(childStr));
			}
			voc.put("children", bsonChildArray);
		}

		collection.findOneAndReplace(new BsonDocument("id", new BsonString(vocID)), voc);
		return 0;
	}

	public int capture(List<EPC> epcList, BsonDocument map2Save) {
		if (map2Save == null) {
			return 0;
		}
		if (map2Save.size() == 0) {
			return 0;
		}

		MongoCollection<BsonDocument> collection = Configuration.mongoDatabase.getCollection("MasterData",
				BsonDocument.class);

		for (EPC epc : epcList) {
			String id = epc.getValue();

			// each id should have one document
			BsonDocument voc = collection.find(new BsonDocument("id", new BsonString(id))).first();

			if (voc == null) {
				voc = new BsonDocument();
			}

			voc.put("type", new BsonString("urn:epcglobal:epcis:vtype:EPCInstance"));
			voc.put("id", new BsonString(id));

			// Prepare vocabularyList JSONObject
			BsonDocument attrObj = null;
			if (!voc.containsKey("attributes")) {
				attrObj = new BsonDocument();
			} else {
				attrObj = voc.getDocument("attributes");
			}

			Iterator<String> mapIter = map2Save.keySet().iterator();
			while (mapIter.hasNext()) {
				String key = mapIter.next();
				key = encodeMongoObjectKey(key);
				Object value = map2Save.get(key);
				attrObj.put(key, new BsonString(value.toString()));
			}

			attrObj.put("lastUpdated", new BsonInt64(System.currentTimeMillis()));
			voc.put("attributes", attrObj);

			collection.findOneAndReplace(new BsonDocument("id", new BsonString(id)), voc);
		}

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
