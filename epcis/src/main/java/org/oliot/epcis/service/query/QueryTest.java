package org.oliot.epcis.service.query;

import java.util.List;
import java.util.regex.Pattern;

import org.oliot.model.epcis.ObjectEventType;
import org.springframework.context.ApplicationContext;
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


@Controller
@RequestMapping("/querytest")
public class QueryTest {
	
	@SuppressWarnings("unused")
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
		
		System.out.println();
		return null;
	}
	
}
