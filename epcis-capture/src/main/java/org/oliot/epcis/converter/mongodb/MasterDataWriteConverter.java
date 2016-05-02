package org.oliot.epcis.converter.mongodb;

import java.util.Iterator;
import java.util.List;

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
import org.oliot.model.epcis.VocabularyType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
 * @author Jaewook Byun, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         bjw0829@kaist.ac.kr, bjw0829@gmail.com
 */

public class MasterDataWriteConverter {

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
				if (voc != null) {
					isExist = true;
				} else {
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
						// e.g. defnition
						String key = attribute.getId();
						key = encodeMongoObjectKey(key);
						List<Object> valueList = attribute.getContent();

						if (valueList.size() == 1 && valueList.get(0) instanceof String) {
							// SimpleType xsd:string
							String value = valueList.get(0).toString();
							BsonArray eArr = null;
							if( !attrObj.containsKey(key) ){
								eArr = new BsonArray();
							}else{
								eArr = attrObj.getArray(key);
							}
							eArr.add(MongoWriterUtil.converseType(value));
							attrObj.put(key, eArr);
						} else {
							// ComplexType
							for (Object value : valueList) {
								if (value instanceof Element) {
									BsonDocument complexAttr = new BsonDocument();
									BsonDocument map2Save = new BsonDocument();
									Element element = (Element) value;
									// example:Address
									String name = element.getNodeName();
									String[] checkArr = name.split(":");
									if (checkArr.length == 2) {
										map2Save.put("@" + checkArr[0], new BsonString(element.getNamespaceURI()));
									}
									NodeList childNodeList = element.getChildNodes();
									for (int n = 0; n < childNodeList.getLength(); n++) {
										Node childNode = childNodeList.item(n);
										// Street
										String cname = childNode.getLocalName();
										// 100 Nowhere Street
										String cval = childNode.getTextContent();
										if (cname != null) {
											map2Save.put(cname, new BsonString(cval));
										}
									}
									complexAttr.put(name, map2Save);
									attrObj.put(key, complexAttr);
								}
							}
						}
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

	public int capture(JSONObject event) {

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
