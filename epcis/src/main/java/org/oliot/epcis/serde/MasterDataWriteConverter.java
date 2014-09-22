package org.oliot.epcis.serde;

import java.util.HashMap;
import java.util.Iterator;
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
import org.w3c.dom.Element;

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
@WritingConverter
public class MasterDataWriteConverter implements
		Converter<VocabularyType, DBObject> {

	public DBObject convert(VocabularyType vocabulary) {

		DBObject dbo = new BasicDBObject();

		if (vocabulary.getAny() != null) {
			Map<String, String> map2Save = new HashMap<String, String>();
			List<Object> objList = vocabulary.getAny();
			for (int i = 0; i < objList.size(); i++) {
				Object obj = objList.get(i);
				if (obj instanceof Element) {
					Element element = (Element) obj;
					if (element.getFirstChild() != null) {
						String name = element.getLocalName();
						String value = element.getFirstChild().getTextContent();
						map2Save.put(name, value);
					}
				}
			}
			if (map2Save != null)
				dbo.put("any", map2Save);
		}

		if (vocabulary.getOtherAttributes() != null) {
			Map<QName, String> map = vocabulary.getOtherAttributes();
			Map<String, String> map2Save = new HashMap<String, String>();
			Iterator<QName> iter = map.keySet().iterator();
			while (iter.hasNext()) {
				QName qName = iter.next();
				String value = map.get(qName);
				map2Save.put(qName.toString(), value);
			}
			dbo.put("otherAttributes", map2Save);
		}

		// Extension
		DBObject extension = new BasicDBObject();
		if (vocabulary.getExtension() != null) {
			VocabularyExtensionType vet = vocabulary
					.getExtension();
			if (vet.getAny() != null) {
				Map<String, String> map2Save = new HashMap<String, String>();
				List<Object> objList = vet.getAny();
				for (int i = 0; i < objList.size(); i++) {
					Object obj = objList.get(i);
					if (obj instanceof Element) {
						Element element = (Element) obj;
						if (element.getFirstChild() != null) {
							String name = element.getNodeName();
							String value = element.getFirstChild()
									.getTextContent();
							map2Save.put(name, value);
						}
					}
				}
				if (map2Save != null)
					extension.put("any", map2Save);
			}

			if (vet.getOtherAttributes() != null) {
				Map<QName, String> map = vet.getOtherAttributes();
				Map<String, String> map2Save = new HashMap<String, String>();
				Iterator<QName> iter = map.keySet().iterator();
				while (iter.hasNext()) {
					QName qName = iter.next();
					String value = map.get(qName);
					map2Save.put(qName.toString(), value);
				}
				extension.put("otherAttributes", map2Save);
			}
		}
		dbo.put("extension", extension);
		
		if( vocabulary.getType() != null )
			dbo.put("type", vocabulary.getType());
		
		if( vocabulary.getVocabularyElementList() != null )
		{
			VocabularyElementListType velt = vocabulary.getVocabularyElementList();
			List<VocabularyElementType> vetList = velt.getVocabularyElement();
			BasicDBList vocDBList = new BasicDBList();
			for(int i = 0 ; i < vetList.size() ; i++ )
			{
				VocabularyElementType vocabularyElement = vetList.get(i);
				
				DBObject elementObject = new BasicDBObject();
				
				if( vocabularyElement.getId() != null )
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
					Map<String, String> map2Save = new HashMap<String, String>();
					List<Object> objList = vocabularyElement.getAny();
					for (int j = 0; j < objList.size(); j++) {
						Object obj = objList.get(j);
						if (obj instanceof Element) {
							Element element = (Element) obj;
							if (element.getFirstChild() != null) {
								String name = element.getLocalName();
								String value = element.getFirstChild().getTextContent();
								map2Save.put(name, value);
							}
						}
					}
					if (map2Save != null)
						elementObject.put("any", map2Save);
				}

				if (vocabularyElement.getOtherAttributes() != null) {
					Map<QName, String> map = vocabularyElement.getOtherAttributes();
					Map<String, String> map2Save = new HashMap<String, String>();
					Iterator<QName> iter = map.keySet().iterator();
					while (iter.hasNext()) {
						QName qName = iter.next();
						String value = map.get(qName);
						map2Save.put(qName.toString(), value);
					}
					elementObject.put("otherAttributes", map2Save);
				}
				vocDBList.add(elementObject);
			}
			dbo.put("vocabularyList", vocDBList);
		}
		return dbo;
	}

}
