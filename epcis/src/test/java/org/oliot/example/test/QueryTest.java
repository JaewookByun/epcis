package org.oliot.example.test;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
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
public class QueryTest {

	/**
	 * Default Initialization Map Data Send
	 */
	@SuppressWarnings("resource")
	@Test
	public void subqueryTest1() {
		ApplicationContext ctx = new GenericXmlApplicationContext(
				"classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx
				.getBean("mongoTemplate");
		DBCollection collection = mongoOperation.getCollection("ObjectEvent");
		DBObject query = new BasicDBObject();
		query.put("readPoint.id", "urn:epc:id:sgln:0614141.07346.1234");
		DBCursor c = collection.find(query);
		while(c.hasNext())
		{
			DBObject d = c.next();
			System.out.println(d);
		}
	}
}
