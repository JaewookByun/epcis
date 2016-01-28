package org.oliot.epcis.service.capture.mongodb;

import org.json.JSONObject;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.epcis.serde.mongodb.AggregationEventWriteConverter;
import org.oliot.epcis.serde.mongodb.ObjectEventWriteConverter;
import org.oliot.epcis.serde.mongodb.QuantityEventWriteConverter;
import org.oliot.epcis.serde.mongodb.TransactionEventWriteConverter;
import org.oliot.epcis.serde.mongodb.TransformationEventWriteConverter;
import org.oliot.model.epcis.AggregationEventType;
import org.oliot.model.epcis.ObjectEventType;
import org.oliot.model.epcis.QuantityEventType;
import org.oliot.model.epcis.TransactionEventType;
import org.oliot.model.epcis.TransformationEventType;
import org.oliot.model.epcis.VocabularyType;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

/**
 * Copyright (C) 2014 Jaewook Byun
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

public class MongoCaptureUtil {

	public void capture(AggregationEventType event, String userID, String accessModifier, Integer gcpLength) {
		ApplicationContext ctx = new GenericXmlApplicationContext("classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");

		// Utilize Aggregation Event Write Converter itself
		AggregationEventWriteConverter wc = new AggregationEventWriteConverter();
		DBObject object2Save = wc.convert(event, gcpLength);
		if (userID != null && accessModifier != null) {
			object2Save.put("userID", userID);
			object2Save.put("accessModifier", accessModifier);
		}
		mongoOperation.save(object2Save, "AggregationEvent");
		Configuration.logger.info(" Event Saved ");
		((AbstractApplicationContext) ctx).close();

	}

	public void capture(ObjectEventType event, String userID, String accessModifier, Integer gcpLength) {
		ApplicationContext ctx = new GenericXmlApplicationContext("classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");

		// Utilize Object Event Write Converter itself
		ObjectEventWriteConverter wc = new ObjectEventWriteConverter();
		DBObject object2Save = wc.convert(event, gcpLength);
		if (userID != null && accessModifier != null) {
			object2Save.put("userID", userID);
			object2Save.put("accessModifier", accessModifier);
		}
		mongoOperation.save(object2Save, "ObjectEvent");
		Configuration.logger.info(" Event Saved ");
		((AbstractApplicationContext) ctx).close();

	}

	public void capture(QuantityEventType event, String userID, String accessModifier, Integer gcpLength) {
		ApplicationContext ctx = new GenericXmlApplicationContext("classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");

		// Utilize Quantity Event Write Converter itself
		QuantityEventWriteConverter wc = new QuantityEventWriteConverter();
		DBObject object2Save = wc.convert(event, gcpLength);
		if (userID != null && accessModifier != null) {
			object2Save.put("userID", userID);
			object2Save.put("accessModifier", accessModifier);
		}
		mongoOperation.save(object2Save, "QuantityEvent");
		Configuration.logger.info(" Event Saved ");
		((AbstractApplicationContext) ctx).close();

	}

	public void capture(TransactionEventType event, String userID, String accessModifier, Integer gcpLength) {
		ApplicationContext ctx = new GenericXmlApplicationContext("classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");

		// Utilize Transaction Event Write Converter itself
		TransactionEventWriteConverter wc = new TransactionEventWriteConverter();
		DBObject object2Save = wc.convert(event, gcpLength);
		if (userID != null && accessModifier != null) {
			object2Save.put("userID", userID);
			object2Save.put("accessModifier", accessModifier);
		}
		mongoOperation.save(object2Save, "TransactionEvent");
		Configuration.logger.info(" Event Saved ");
		((AbstractApplicationContext) ctx).close();

	}

	public void capture(TransformationEventType event, String userID, String accessModifier, Integer gcpLength) {
		ApplicationContext ctx = new GenericXmlApplicationContext("classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");

		// Utilize Transaction Event Write Converter itself
		TransformationEventWriteConverter wc = new TransformationEventWriteConverter();
		DBObject object2Save = wc.convert(event, gcpLength);
		if (userID != null && accessModifier != null) {
			object2Save.put("userID", userID);
			object2Save.put("accessModifier", accessModifier);
		}
		mongoOperation.save(object2Save, "TransformationEvent");
		Configuration.logger.info(" Event Saved ");
		((AbstractApplicationContext) ctx).close();

	}

	public void capture(VocabularyType vocabulary) {
		ApplicationContext ctx = new GenericXmlApplicationContext("classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");

		mongoOperation.save(vocabulary);
		Configuration.logger.info(" Vocabulary Saved ");
		((AbstractApplicationContext) ctx).close();
	}

	// JsonObject event capture series..

	public void objectevent_capture(JSONObject event, MongoOperations mongoOperation) {

		DBCollection collection = mongoOperation.getCollection("ObjectEvent");

		DBObject dbObject = (DBObject) JSON.parse(event.toString());

		collection.save(dbObject);
		Configuration.logger.info(" Event Saved ");
	}

	public void aggregationevent_capture(JSONObject event, MongoOperations mongoOperation) {

		DBCollection collection = mongoOperation.getCollection("AggregationEvent");

		DBObject dbObject = (DBObject) JSON.parse(event.toString());

		collection.save(dbObject);
		Configuration.logger.info(" Event Saved ");
	}

	public void transformationevent_capture(JSONObject event, MongoOperations mongoOperation) {
		DBCollection collection = mongoOperation.getCollection("TransformationEvent");

		DBObject dbObject = (DBObject) JSON.parse(event.toString());

		collection.save(dbObject);
		Configuration.logger.info(" Event Saved ");
	}

	public void masterdata_capture(JSONObject event, MongoOperations mongoOperation) {

		DBCollection collection = mongoOperation.getCollection("MasterData");

		DBObject dbObject = (DBObject) JSON.parse(event.toString());

		collection.save(dbObject);
		Configuration.logger.info(" Event Saved ");
	}
	
	public void transactionevent_capture(JSONObject event, MongoOperations mongoOperation) {

		DBCollection collection = mongoOperation.getCollection("TransactionEvent");

		DBObject dbObject = (DBObject) JSON.parse(event.toString());

		collection.save(dbObject);
		Configuration.logger.info(" Event Saved ");
	}
}
