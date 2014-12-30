package org.oliot.epcis.serde.mongodb;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.oliot.model.epcis.BusinessLocationType;
import org.oliot.model.epcis.BusinessTransactionListType;
import org.oliot.model.epcis.BusinessTransactionType;
import org.oliot.model.epcis.EPCISEventExtensionType;
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
public class SensorEventWriteConverter implements
		Converter<SensorEventType, DBObject> {

	public DBObject convert(SensorEventType sensorEventType) {

		DBObject dbo = new BasicDBObject();
		// Base Extension
		if (sensorEventType.getBaseExtension() != null) {
			EPCISEventExtensionType baseExtensionType = sensorEventType
					.getBaseExtension();
			DBObject baseExtension = getBaseExtensionObject(baseExtensionType);
			dbo.put("baseExtension", baseExtension);
		}
		// Event Time
		if (sensorEventType.getEventTime() != null)
			dbo.put("eventTime", sensorEventType.getEventTime()
					.toGregorianCalendar().getTimeInMillis());
		// Event Time Zone
		if (sensorEventType.getEventTimeZoneOffset() != null)
			dbo.put("eventTimeZoneOffset",
					sensorEventType.getEventTimeZoneOffset());
		// Record Time : according to M5
		GregorianCalendar recordTime = new GregorianCalendar();
		long recordTimeMilis = recordTime.getTimeInMillis();
		dbo.put("recordTime", recordTimeMilis);
		// Finish Time
		if (sensorEventType.getFinishTime() != null)
			dbo.put("finishTime", sensorEventType.getFinishTime()
					.toGregorianCalendar().getTimeInMillis());
		// Action
		if (sensorEventType.getAction() != null)
			dbo.put("action", sensorEventType.getAction().name());
		// TargetObject
		if (sensorEventType.getTargetObject() != null)
			dbo.put("targetObject", sensorEventType.getTargetObject());
		// TargetArea
		if (sensorEventType.getTargetArea() != null)
			dbo.put("targetArea", sensorEventType.getTargetArea());
		// BizStep
		if (sensorEventType.getBizStep() != null)
			dbo.put("bizStep", sensorEventType.getBizStep());
		// Disposition
		if (sensorEventType.getDisposition() != null)
			dbo.put("disposition", sensorEventType.getDisposition());
		// ReadPoint
		if (sensorEventType.getReadPoint() != null) {
			ReadPointType readPointType = sensorEventType.getReadPoint();
			DBObject readPoint = getReadPointObject(readPointType);
			dbo.put("readPoint", readPoint);
		}
		// BizLocation
		if (sensorEventType.getBizLocation() != null) {
			BusinessLocationType bizLocationType = sensorEventType
					.getBizLocation();
			DBObject bizLocation = getBizLocationObject(bizLocationType);
			dbo.put("bizLocation", bizLocation);
		}
		// BizTransaction
		if (sensorEventType.getBizTransactionList() != null) {
			BusinessTransactionListType bizListType = sensorEventType
					.getBizTransactionList();
			List<BusinessTransactionType> bizList = bizListType
					.getBizTransaction();
			List<DBObject> bizTranList = getBizTransactionObjectList(bizList);
			dbo.put("bizTransactionList", bizTranList);
		}
		// Extension
		if (sensorEventType.getExtension() != null) {
			SensorEventExtensionType oee = sensorEventType.getExtension();
			DBObject extension = getSensorEventExtensionObject(oee);
			dbo.put("extension", extension);
		}
		// Sensing List
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
		return dbo;
	}
}
