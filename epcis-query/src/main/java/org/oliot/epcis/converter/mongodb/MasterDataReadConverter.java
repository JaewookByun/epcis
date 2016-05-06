package org.oliot.epcis.converter.mongodb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.oliot.model.epcis.AttributeType;
import org.oliot.model.epcis.IDListType;
import org.oliot.model.epcis.VocabularyElementListType;
import org.oliot.model.epcis.VocabularyElementType;
import org.oliot.model.epcis.VocabularyType;

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

public class MasterDataReadConverter {

	@SuppressWarnings("unused")
	public VocabularyType convert(BsonDocument dbObject) {
		VocabularyType vt = new VocabularyType();

		if (dbObject.get("type") != null)
			vt.setType((String) dbObject.getString("type").getValue());

		VocabularyElementListType velt = new VocabularyElementListType();
		List<VocabularyElementType> vetList = new ArrayList<VocabularyElementType>();

		VocabularyElementType vet = new VocabularyElementType();
		BsonDocument attrObj = dbObject.getDocument("attributes");
		List<AttributeType> attrListType = new ArrayList<AttributeType>();
		if (attrObj != null) {
			Iterator<String> attrIter = attrObj.keySet().iterator();

			if (dbObject.containsKey("id"))
				vet.setId(dbObject.getString("id").getValue());

			while (attrIter.hasNext()) {
				String key = attrIter.next();
				if (!key.equals("lastUpdated")) {
					String value = attrObj.getString(key).getValue();
					key = decodeMongoObjectKey(key);
					AttributeType attrType = new AttributeType();
					attrType.setId(key);
					//attrType.setValue(value);
					attrListType.add(attrType);
				} else {
					String value = String.valueOf(attrObj.getDateTime(key).getValue());
					AttributeType attrType = new AttributeType();
					attrType.setId(key);
					//attrType.setValue(value);
					attrListType.add(attrType);
				}

			}
		}
		vet.setAttribute(attrListType);

		IDListType idListType = new IDListType();
		List<String> idList = new ArrayList<String>();
		BsonArray childList = dbObject.getArray("children");
		if (childList != null && childList.isEmpty() == false) {
			Iterator<BsonValue> childIter = childList.iterator();
			while (childIter.hasNext()) {
				idList.add(childIter.next().asString().getValue());
			}
			idListType.setId(idList);
		}
		vet.setChildren(idListType);
		vetList.add(vet);
		velt.setVocabularyElement(vetList);
		vt.setVocabularyElementList(velt);
		return vt;
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
