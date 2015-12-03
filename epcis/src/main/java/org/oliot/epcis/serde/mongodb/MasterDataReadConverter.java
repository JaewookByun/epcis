package org.oliot.epcis.serde.mongodb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.oliot.model.epcis.AttributeType;
import org.oliot.model.epcis.IDListType;
import org.oliot.model.epcis.VocabularyElementListType;
import org.oliot.model.epcis.VocabularyElementType;
import org.oliot.model.epcis.VocabularyType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

/**
 * Copyright (C) 2014 Jaewook Byun
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

@Component
@ReadingConverter
public class MasterDataReadConverter implements Converter<DBObject, VocabularyType> {

	public VocabularyType convert(DBObject dbObject) {
		VocabularyType vt = new VocabularyType();

		if (dbObject.get("type") != null)
			vt.setType((String) dbObject.get("type"));

		VocabularyElementListType velt = new VocabularyElementListType();
		List<VocabularyElementType> vetList = new ArrayList<VocabularyElementType>();

		VocabularyElementType vet = new VocabularyElementType();
		Object attrObj = dbObject.get("attributes");
		List<AttributeType> attrListType = new ArrayList<AttributeType>();
		if (attrObj != null) {
			DBObject attrDBObject = (DBObject) attrObj;

			Iterator<String> attrIter = attrDBObject.keySet().iterator();

			if (dbObject.get("id") != null)
				vet.setId(dbObject.get("id").toString());

			while (attrIter.hasNext()) {
				String key = attrIter.next();
				String value = attrDBObject.get(key).toString();
				key = decodeMongoObjectKey(key);

				AttributeType attrType = new AttributeType();
				attrType.setId(key);
				attrType.setValue(value);
				attrListType.add(attrType);
			}
		}
		vet.setAttribute(attrListType);

		IDListType idListType = new IDListType();
		List<String> idList = new ArrayList<String>();
		Object childrenObj = dbObject.get("children");
		if (childrenObj != null) {
			BasicDBList childList = (BasicDBList) childrenObj;
			Iterator<Object> childIter = childList.iterator();
			while (childIter.hasNext()) {
				Object childObj = childIter.next();
				String childID = childObj.toString();
				idList.add(childID);
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
