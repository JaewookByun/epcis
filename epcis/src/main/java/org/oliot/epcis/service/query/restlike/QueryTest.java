package org.oliot.epcis.service.query.restlike;

import java.util.List;
import java.util.regex.Pattern;

import org.oliot.model.epcis.ObjectEventType;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
@Controller
@RequestMapping("/querytest")
public class QueryTest {
	
	@SuppressWarnings({ "unused" })
	@RequestMapping(value = "/a", method = RequestMethod.GET)
	@ResponseBody
	public String getTest()
	{
		ApplicationContext ctx = new GenericXmlApplicationContext(
				"classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx
				.getBean("mongoTemplate");
		
		DBObject d = new BasicDBObject();
		
		
		Criteria c = Criteria.where("action").is(Pattern.compile("^A.*"));
		Query q = new Query();
		q.addCriteria(c);
		List<ObjectEventType> ddd = mongoOperation.find(q, ObjectEventType.class);
		((AbstractApplicationContext) ctx).close();
		System.out.println();
		return null;
	}
	
}
