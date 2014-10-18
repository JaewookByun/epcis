package org.oliot.epccr.util;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.oliot.epccr.configuration.Configuration;
import org.oliot.epccr.rest.Subscription;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * Copyright (C) 2014 Jaewook Jack Byun
 *
 * This project is incubating project named Electronic Product Code Context
 * Repository (EPCCR). This project pursues Resource Oriented Architecture (ROA)
 * for EPC-based event
 * 
 * Commonality with EPCIS Getting powered with EPC's global uniqueness
 * 
 * Differences Resource Oriented, not Service Oriented Resource(EPC)-driven URL
 * scheme Best efforts to comply RESTful principle Exploit flexibility rather
 * than formal verification JSON vs. XML NOSQL vs. SQL Focus on the Internet of
 * Things beyond Supply Chain Management
 * 
 * @author Jaewook Jack Byun, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         bjw0829@{kaist.ac.kr,gmail.com}
 */
public class MongoUtil {
	public static void addSubscriptionToDB(String epc, String destURL) {
		ApplicationContext ctx = new GenericXmlApplicationContext(
				"classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx
				.getBean("mongoTemplate");
		Subscription s = new Subscription(epc, destURL);
		mongoOperation.save(s);
		((AbstractApplicationContext) ctx).close();
	}

	public static void delSubscriptionFromDB(String epc, String destURL) {
		ApplicationContext ctx = new GenericXmlApplicationContext(
				"classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx
				.getBean("mongoTemplate");

		Query query = new Query();
		query.addCriteria(Criteria.where("epc").is(epc));
		query.addCriteria(Criteria.where("destURL").is(destURL));

		mongoOperation.remove(query, Subscription.class);
		((AbstractApplicationContext) ctx).close();
	}

	public static void saveResourceToDB(String epc, DBObject dbObject) {
		ApplicationContext ctx = new GenericXmlApplicationContext(
				"classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx
				.getBean("mongoTemplate");

		DBCollection collection;
		if (mongoOperation.collectionExists(epc)) {
			collection = mongoOperation.getCollection(epc);
		} else {
			CollectionOptions options = new CollectionOptions(
					Configuration.cappedSize, null, true);
			collection = mongoOperation.createCollection(epc, options);
		}

		collection.insert(dbObject);
		((AbstractApplicationContext) ctx).close();
	}

	public static List<Subscription> queryExistingSubscription() {
		ApplicationContext ctx = new GenericXmlApplicationContext(
				"classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx
				.getBean("mongoTemplate");

		List<Subscription> ret = mongoOperation.findAll(Subscription.class);
		((AbstractApplicationContext) ctx).close();
		return ret;
	}

	@SuppressWarnings({ "rawtypes", "resource" })
	public static JSONArray queryResourceFromDB(String epc, long fromTime,
			long untilTime) {
		ApplicationContext ctx = new GenericXmlApplicationContext(
				"classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx
				.getBean("mongoTemplate");
		DBCollection collection = mongoOperation.getCollection(epc);

		if (collection == null) {
			return null;
		}

		DBObject query = new BasicDBObject();
		query.put("epc", epc);
		if (fromTime != 0) {
			DBObject sub = new BasicDBObject();
			sub.put("$gte", fromTime);
			query.put("eventTime", sub);
		}
		if (untilTime != 0) {
			DBObject sub = new BasicDBObject();
			sub.put("$lte", untilTime);
			query.put("eventTime", sub);
		}

		DBObject fields = new BasicDBObject();
		fields.put("epc", false);
		fields.put("_id", false);
		DBCursor cursor = collection.find(query, fields);
		cursor.sort((DBObject) new BasicDBObject().put("eventTime",
				new Integer(1)));

		JSONArray jArray = new JSONArray();
		while (cursor.hasNext()) {
			DBObject base = cursor.next();
			Map baseMap = base.toMap();
			jArray.put(baseMap);
		}
		((AbstractApplicationContext) ctx).close();
		return jArray;
	}

	@SuppressWarnings("resource")
	public static boolean isExistingSubscription(String epc, String destURL) {
		ApplicationContext ctx = new GenericXmlApplicationContext(
				"classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx
				.getBean("mongoTemplate");

		Query query = new Query();
		query.addCriteria(Criteria.where("epc").is(epc));
		query.addCriteria(Criteria.where("destURL").is(destURL));

		List<Subscription> subList = mongoOperation.find(query,
				Subscription.class);
		if (subList.size() == 0) {
			return false;
		} else {
			return true;
		}
	}
}
