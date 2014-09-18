package org.oliot.epcis.serde;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.axis.message.MessageElement;
import org.oliot.model.epcis.BusinessLocationExtensionType;
import org.oliot.model.epcis.BusinessLocationType;
import org.oliot.model.epcis.BusinessTransactionListType;
import org.oliot.model.epcis.BusinessTransactionType;
import org.oliot.model.epcis.EPCISEventExtensionType;
import org.oliot.model.epcis.ReadPointExtensionType;
import org.oliot.model.epcis.ReadPointType;
import org.oliot.model.epcis.SensingElementType;
import org.oliot.model.epcis.SensingListType;
import org.oliot.model.epcis.Sensor;
import org.oliot.model.epcis.SensorEventExtensionType;
import org.oliot.model.epcis.SensorEventType;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

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
public class SensorEventWriteConverter implements
		Converter<SensorEventType, DBObject> {

	public DBObject convert(SensorEventType sensorEventType) {

		DBObject dbo = new BasicDBObject();

		if (sensorEventType.getBaseExtension() != null) {
			EPCISEventExtensionType baseExtensionType = sensorEventType
					.getBaseExtension();
			DBObject baseExtension = new BasicDBObject();
			if (baseExtensionType.getAny() != null) {
				Map<String, String> map2Save = new HashMap<String, String>();
				List<Object> objList = baseExtensionType.getAny();
				for (int i = 0; i < objList.size(); i++) {
					Object obj = objList.get(i);
					if (obj instanceof Element) {
						Element element = (Element) obj;
						if (element.getFirstChild() != null) {
							String name = element.getLocalName();
							String value = element.getFirstChild()
									.getTextContent();
							map2Save.put(name, value);
						}
					}
				}
				if (map2Save != null)
					baseExtension.put("any", map2Save);
			}

			if (baseExtensionType.getOtherAttributes() != null) {
				Map<QName, String> map = baseExtensionType.getOtherAttributes();
				Map<String, String> map2Save = new HashMap<String, String>();
				Iterator<QName> iter = map.keySet().iterator();
				while (iter.hasNext()) {
					QName qName = iter.next();
					String value = map.get(qName);
					map2Save.put(qName.toString(), value);
				}
				baseExtension.put("otherAttributes", map2Save);
			}
			dbo.put("baseExtension", baseExtension);
		}

		if (sensorEventType.getEventTime() != null)
			dbo.put("eventTime", sensorEventType.getEventTime()
					.toGregorianCalendar().getTimeInMillis());
		if (sensorEventType.getEventTimeZoneOffset() != null)
			dbo.put("eventTimeZoneOffset",
					sensorEventType.getEventTimeZoneOffset());
		// Record Time : according to M5
		GregorianCalendar recordTime = new GregorianCalendar();
		long recordTimeMilis = recordTime.getTimeInMillis();
		dbo.put("recordTime", recordTimeMilis);
		if (sensorEventType.getFinishTime() != null)
			dbo.put("finishTime", sensorEventType.getFinishTime()
					.toGregorianCalendar().getTimeInMillis());
		if (sensorEventType.getAction() != null)
			dbo.put("action", sensorEventType.getAction().name());
		if (sensorEventType.getTargetObject() != null)
			dbo.put("targetObject", sensorEventType.getTargetObject());
		if (sensorEventType.getTargetArea() != null)
			dbo.put("targetArea", sensorEventType.getTargetArea());
		if (sensorEventType.getBizStep() != null)
			dbo.put("bizStep", sensorEventType.getBizStep());
		if (sensorEventType.getDisposition() != null)
			dbo.put("disposition", sensorEventType.getDisposition());
		if (sensorEventType.getReadPoint() != null) {
			ReadPointType readPointType = sensorEventType.getReadPoint();
			DBObject readPoint = new BasicDBObject();
			if (readPointType.getId() != null)
				readPoint.put("id", readPointType.getId());
			ReadPointExtensionType readPointExtensionType = readPointType
					.getExtension();
			DBObject extension = new BasicDBObject();
			if (readPointExtensionType.getAny() != null) {
				Map<String, String> map2Save = new HashMap<String, String>();
				List<Object> objList = readPointExtensionType.getAny();
				for (int i = 0; i < objList.size(); i++) {
					Object obj = objList.get(i);
					if (obj instanceof Element) {
						Element element = (Element) obj;
						if (element.getFirstChild() != null) {
							String name = element.getLocalName();
							String value = element.getFirstChild()
									.getTextContent();
							map2Save.put(name, value);
						}
					}
				}
				if (map2Save != null)
					extension.put("any", map2Save);
			}

			if (readPointExtensionType.getOtherAttributes() != null) {
				Map<QName, String> map = readPointExtensionType
						.getOtherAttributes();
				Map<String, String> map2Save = new HashMap<String, String>();
				Iterator<QName> iter = map.keySet().iterator();
				while (iter.hasNext()) {
					QName qName = iter.next();
					String value = map.get(qName);
					map2Save.put(qName.toString(), value);
				}
				extension.put("otherAttributes", map2Save);
			}
			readPoint.put("extension", extension);
			dbo.put("readPoint", readPoint);
		}
		if (sensorEventType.getBizLocation() != null) {
			BusinessLocationType bizLocationType = sensorEventType
					.getBizLocation();
			DBObject bizLocation = new BasicDBObject();
			if (bizLocationType.getId() != null)
				bizLocation.put("id", bizLocationType.getId());
			BusinessLocationExtensionType bizLocationExtensionType = bizLocationType
					.getExtension();
			DBObject extension = new BasicDBObject();
			if (bizLocationExtensionType.getAny() != null) {
				Map<String, String> map2Save = new HashMap<String, String>();
				List<Object> objList = bizLocationExtensionType.getAny();
				for (int i = 0; i < objList.size(); i++) {
					Object obj = objList.get(i);
					if (obj instanceof Element) {
						Element element = (Element) obj;
						if (element.getFirstChild() != null) {
							String name = element.getLocalName();
							String value = element.getFirstChild()
									.getTextContent();
							map2Save.put(name, value);
						}
					}
				}
				if (map2Save != null)
					extension.put("any", map2Save);
			}

			if (bizLocationExtensionType.getOtherAttributes() != null) {
				Map<QName, String> map = bizLocationExtensionType
						.getOtherAttributes();
				Map<String, String> map2Save = new HashMap<String, String>();
				Iterator<QName> iter = map.keySet().iterator();
				while (iter.hasNext()) {
					QName qName = iter.next();
					String value = map.get(qName);
					map2Save.put(qName.toString(), value);
				}
				extension.put("otherAttributes", map2Save);
			}
			bizLocation.put("extension", extension);
			dbo.put("bizLocation", bizLocation);
		}

		if (sensorEventType.getBizTransactionList() != null) {
			BusinessTransactionListType bizListType = sensorEventType
					.getBizTransactionList();
			List<BusinessTransactionType> bizList = bizListType
					.getBizTransaction();

			List<DBObject> bizTranList = new ArrayList<DBObject>();
			for (int i = 0; i < bizList.size(); i++) {
				BusinessTransactionType bizTranType = bizList.get(i);
				if (bizTranType.getType() != null
						&& bizTranType.getValue() != null) {
					DBObject dbObj = new BasicDBObject();
					dbObj.put(bizTranType.getType(), bizTranType.getValue());
					bizTranList.add(dbObj);
				}
			}
			dbo.put("bizTransactionList", bizTranList);
		}
		if (sensorEventType.getSensingList() != null) {
			SensingListType slt = sensorEventType.getSensingList();
			List<SensingElementType> setList = slt.getSensingElement();
			List<String> sensingList = new ArrayList<String>();
			ApplicationContext ctx = new GenericXmlApplicationContext(
					"classpath:MongoConfig.xml");
			MongoOperations mongoOperation = (MongoOperations) ctx
					.getBean("mongoTemplate");
			for (int i = 0; i < setList.size(); i++) {
				SensingElementType set = setList.get(i);
				Sensor sensor = new Sensor();
				if (set.getEpc() != null) {
					sensor.setEpc(set.getEpc().getValue());
					if (!sensingList.contains(set.getEpc().getValue()))
						sensingList.add(set.getEpc().getValue());
				}
				if (set.getType() != null)
					sensor.setType(set.getType());
				if (set.getUom() != null)
					sensor.setUom(set.getUom());
				if (set.getValue() != null)
					sensor.setValue(set.getValue());

				if (sensorEventType.getEventTime() != null)
					sensor.setStartTime(sensorEventType.getEventTime()
							.toGregorianCalendar().getTimeInMillis());
				if (sensorEventType.getEventTimeZoneOffset() != null)
					sensor.setEventTimeZoneOffset(sensorEventType
							.getEventTimeZoneOffset());
				if (sensorEventType.getFinishTime() != null)
					sensor.setFinishTime(sensorEventType.getFinishTime()
							.toGregorianCalendar().getTimeInMillis());
				mongoOperation.save(sensor);
			}
			dbo.put("sensingList", sensingList);
			((AbstractApplicationContext) ctx).close();
		}

		// Extension
		DBObject extension = new BasicDBObject();
		if (sensorEventType.getExtension() != null) {
			SensorEventExtensionType oee = sensorEventType.getExtension();
			if (oee.getAny() != null) {
				Map<String, String> map2Save = new HashMap<String, String>();
				List<Object> objList = oee.getAny();
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

			if (oee.getOtherAttributes() != null) {
				Map<QName, String> map = oee.getOtherAttributes();
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
		return dbo;
	}
	
	

	public DBObject getDBObjectFromMessageElement(MessageElement any) {
		NamedNodeMap attributes = any.getAttributes();
		DBObject attrObject = new BasicDBObject();
		for (int i = 0; i < attributes.getLength(); i++) {
			Attr attr = (Attr) attributes.item(i);

			String attrName = attr.getNodeName();
			String attrValue = attr.getNodeValue();
			attrObject.put(attrName, attrValue);
		}
		return attrObject;
	}
}
