package org.oliot.epcis.serde.mongodb;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.log4j.Level;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.model.epcis.ActionType;
import org.oliot.model.epcis.BusinessLocationExtensionType;
import org.oliot.model.epcis.BusinessLocationType;
import org.oliot.model.epcis.BusinessTransactionListType;
import org.oliot.model.epcis.BusinessTransactionType;
import org.oliot.model.epcis.EPC;
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
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import static org.oliot.epcis.serde.mongodb.MongoReaderUtil.*;

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
public class SensorEventReadConverter implements
		Converter<DBObject, SensorEventType> {

	public SensorEventType convert(DBObject dbObject) {
		try {
			SensorEventType sensorEventType = new SensorEventType();
			int zone = 0;
			if (dbObject.get("eventTimeZoneOffset") != null) {
				String eventTimeZoneOffset = (String) dbObject
						.get("eventTimeZoneOffset");
				sensorEventType
						.setEventTimeZoneOffset(eventTimeZoneOffset);
				if(eventTimeZoneOffset.split(":").length == 2 ){
					zone = Integer.parseInt(eventTimeZoneOffset.split(":")[0]);
				}
			}
			if (dbObject.get("eventTime") != null) {
				long eventTime = (long) dbObject.get("eventTime");
				GregorianCalendar eventCalendar = new GregorianCalendar();
				eventCalendar.setTimeInMillis(eventTime);
				XMLGregorianCalendar xmlEventTime = DatatypeFactory
						.newInstance().newXMLGregorianCalendar(eventCalendar);
				xmlEventTime.setTimezone(zone * 60);
				sensorEventType.setEventTime(xmlEventTime);
			}
			if (dbObject.get("recordTime") != null) {
				long eventTime = (long) dbObject.get("recordTime");
				GregorianCalendar recordCalendar = new GregorianCalendar();
				recordCalendar.setTimeInMillis(eventTime);
				XMLGregorianCalendar xmlRecordTime = DatatypeFactory
						.newInstance().newXMLGregorianCalendar(recordCalendar);
				xmlRecordTime.setTimezone(zone * 60);
				sensorEventType.setRecordTime(xmlRecordTime);
			}
			if (dbObject.get("finishTime") != null) {
				long finishTime = (long) dbObject.get("finishTime");
				GregorianCalendar finishCalendar = new GregorianCalendar();
				finishCalendar.setTimeInMillis(finishTime);
				XMLGregorianCalendar xmlFinishTime = DatatypeFactory
						.newInstance().newXMLGregorianCalendar(finishCalendar);
				xmlFinishTime.setTimezone(zone * 60);
				sensorEventType.setFinishTime(xmlFinishTime);
			}
			if (dbObject.get("action") != null) {
				sensorEventType.setAction(ActionType.fromValue(dbObject.get(
						"action").toString()));
			}
			if (dbObject.get("bizStep") != null)
				sensorEventType.setBizStep(dbObject.get("bizStep").toString());
			if (dbObject.get("disposition") != null)
				sensorEventType.setDisposition(dbObject.get("disposition")
						.toString());
			if (dbObject.get("targetObject") != null)
				sensorEventType.setTargetObject(dbObject.get("targetObject")
						.toString());
			if (dbObject.get("targetArea") != null)
				sensorEventType.setTargetArea(dbObject.get("targetArea")
						.toString());
			if (dbObject.get("baseExtension") != null) {
				EPCISEventExtensionType eeet = new EPCISEventExtensionType();
				BasicDBObject baseExtension = (BasicDBObject) dbObject
						.get("baseExtension");
				eeet = putEPCISExtension(eeet, baseExtension);
				sensorEventType.setBaseExtension(eeet);
			}
			if (dbObject.get("readPoint") != null) {
				BasicDBObject readPointObject = (BasicDBObject) dbObject
						.get("readPoint");
				ReadPointType readPointType = new ReadPointType();
				if (readPointObject.get("id") != null) {
					readPointType.setId(readPointObject.get("id").toString());
				}
				if (readPointObject.get("extension") != null) {
					ReadPointExtensionType rpet = new ReadPointExtensionType();
					BasicDBObject extension = (BasicDBObject) readPointObject
							.get("extension");
					rpet = putReadPointExtension(rpet, extension);
					readPointType.setExtension(rpet);
				}
				sensorEventType.setReadPoint(readPointType);
			}
			// BusinessLocation
			if (dbObject.get("bizLocation") != null) {
				BasicDBObject bizLocationObject = (BasicDBObject) dbObject
						.get("bizLocation");
				BusinessLocationType bizLocationType = new BusinessLocationType();
				if (bizLocationObject.get("id") != null) {
					bizLocationType.setId(bizLocationObject.get("id")
							.toString());
				}
				if (bizLocationObject.get("extension") != null) {
					BusinessLocationExtensionType blet = new BusinessLocationExtensionType();
					BasicDBObject extension = (BasicDBObject) bizLocationObject
							.get("extension");
					blet = putBusinessLocationExtension(blet, extension);
					bizLocationType.setExtension(blet);
				}
				sensorEventType.setBizLocation(bizLocationType);
			}
			if (dbObject.get("bizTransactionList") != null) {
				BasicDBList bizTranList = (BasicDBList) dbObject
						.get("bizTransactionList");
				BusinessTransactionListType btlt = new BusinessTransactionListType();
				List<BusinessTransactionType> bizTranArrayList = new ArrayList<BusinessTransactionType>();
				for (int i = 0; i < bizTranList.size(); i++) {
					// DBObject, key and value
					BasicDBObject bizTran = (BasicDBObject) bizTranList.get(i);
					BusinessTransactionType btt = new BusinessTransactionType();
					Iterator<String> keyIter = bizTran.keySet().iterator();
					// at most one bizTran
					if (keyIter.hasNext()) {
						String key = keyIter.next();
						String value = bizTran.getString(key);
						if (key != null && value != null) {
							btt.setType(key);
							btt.setValue(value);
						}
					}
					if (btt != null)
						bizTranArrayList.add(btt);
				}
				btlt.setBizTransaction(bizTranArrayList);
				sensorEventType.setBizTransactionList(btlt);
			}

			if (dbObject.get("sensingList") != null) {
				BasicDBList sensingList = (BasicDBList) dbObject
						.get("sensingList");
				SensingListType slt = new SensingListType();
				List<SensingElementType> setList = new ArrayList<SensingElementType>();
				ApplicationContext ctx = new GenericXmlApplicationContext(
						"classpath:MongoConfig.xml");
				MongoOperations mongoOperation = (MongoOperations) ctx
						.getBean("mongoTemplate");
				long startTime = (long) dbObject.get("eventTime");
				long finishTime = (long) dbObject.get("finishTime");
				for (int i = 0; i < sensingList.size(); i++) {
					String sensorEPC = sensingList.get(i).toString();
					Criteria criteria = new Criteria();
					criteria.andOperator(Criteria.where("epc").is(sensorEPC),
							Criteria.where("startTime").gte(startTime),
							Criteria.where("finishTime").lte(finishTime));
					List<Sensor> sensors = mongoOperation.find(new Query(
							criteria), Sensor.class);
					for (int j = 0; j < sensors.size(); j++) {
						SensingElementType set = new SensingElementType();
						Sensor sensor = sensors.get(j);
						set.setEpc(new EPC(sensorEPC));
						set.setType(sensor.getType());
						set.setUom(sensor.getUom());
						set.setValue(sensor.getValue());
						setList.add(set);
					}
				}
				slt.setSensingElement(setList);
				sensorEventType.setSensingList(slt);
				((AbstractApplicationContext) ctx).close();
			}
			// extension
			if (dbObject.get("extension") != null) {
				SensorEventExtensionType seet = new SensorEventExtensionType();
				BasicDBObject extension = (BasicDBObject) dbObject
						.get("extension");
				seet = putSensorExtension(seet, extension);
				sensorEventType.setExtension(seet);
			}
			return sensorEventType;
		} catch (DatatypeConfigurationException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
		}
		return null;
	}
}
