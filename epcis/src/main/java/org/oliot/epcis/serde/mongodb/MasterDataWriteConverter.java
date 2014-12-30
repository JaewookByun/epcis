package org.oliot.epcis.serde.mongodb;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.oliot.model.epcis.AttributeType;
import org.oliot.model.epcis.IDListType;
import org.oliot.model.epcis.VocabularyElementListType;
import org.oliot.model.epcis.VocabularyElementType;
import org.oliot.model.epcis.VocabularyExtensionType;
import org.oliot.model.epcis.VocabularyType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import static org.oliot.epcis.serde.mongodb.MongoWriterUtil.*;

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
public class MasterDataWriteConverter implements
		Converter<VocabularyType, DBObject> {

	public DBObject convert(VocabularyType vocabulary) {

		DBObject dbo = new BasicDBObject();

		if (vocabulary.getAny() != null
				&& vocabulary.getAny().isEmpty() == false) {
			List<Object> objList = vocabulary.getAny();
			Map<String, String> map2Save = getAnyMap(objList);
			if (map2Save.isEmpty() == false)
				dbo.put("any", map2Save);
		}

		if (vocabulary.getOtherAttributes() != null
				&& vocabulary.getOtherAttributes().isEmpty() == false) {
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
			VocabularyElementListType velt = vocabulary
					.getVocabularyElementList();
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
					List<AttributeType> attributeList = vocabularyElement
							.getAttribute();
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
					Map<QName, String> map = vocabularyElement
							.getOtherAttributes();
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

}
