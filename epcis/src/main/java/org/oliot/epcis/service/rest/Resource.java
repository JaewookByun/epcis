package org.oliot.epcis.service.rest;

import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;

import org.oliot.epcis.configuration.ConfigurationServlet;
import org.oliot.epcis.service.rest.mongodb.MongoRESTQueryService;
import org.oliot.tdt.SimplePureIdentityFilter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * Copyright (C) 2014 KAIST RESL
 *
 * This project is part of Oliot (oliot.org), pursuing the implementation of
 * Electronic Product Code Information Service(EPCIS) v1.1 specification in
 * EPCglobal.
 * [http://www.gs1.org/gsmp/kc/epcglobal/epcis/epcis_1_1-standard-20140520.pdf]
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
@RequestMapping("/resource")
public class Resource {

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public String getEPCResource(@RequestParam(required = true) String target,
			@RequestParam(required = true) String targetType,
			@RequestParam(required = false) String from,
			@RequestParam(required = false) String until) {

		String result = "";
		if (ConfigurationServlet.backend.equals("MongoDB")) {
			MongoRESTQueryService mrqs = new MongoRESTQueryService();
			result = mrqs.getEPCResource(target, targetType, from, until);
		} else if (ConfigurationServlet.backend.equals("Cassandra")) {

		} else if (ConfigurationServlet.backend.equals("MySQL")) {

		}
		return result;
	}

	@RequestMapping(method = RequestMethod.PUT)
	@ResponseBody
	public String putEPCResource(@RequestParam(required = true) String target,
			@RequestParam(required = true) String targetType,
			@RequestParam(required = false) Long eventTime,
			@RequestParam(required = false) Long finishTime,
			@RequestParam Map<String, String> params) {

		DBObject dbObject = new BasicDBObject();
		
		if (!SimplePureIdentityFilter.isPureIdentity(target)) {
			return "targetObject or targetArea should follow Pure Identity Form";
		}

		if (!targetType.equals("Object") && !targetType.equals("Area")) {
			return "targetType should be Object or Area";
		}
		
		// Processing target
		if( targetType.equals("Object"))
		{
			dbObject.put("targetObject", target);
		}
		else
		{
			dbObject.put("targetArea", target);
		}
		
		// Processing Time
		if( eventTime == null )
		{
			long time = new GregorianCalendar().getTimeInMillis();
			dbObject.put("eventTime", time);
			dbObject.put("finishTime", time);
		}
		else if( eventTime != null && finishTime == null )
		{
			dbObject.put("eventTime", eventTime.longValue());
			dbObject.put("finishTime", eventTime.longValue());
		}
		else if( eventTime != null && finishTime != null )
		{
			dbObject.put("eventTime", eventTime.longValue());
			dbObject.put("finishTime", finishTime.longValue());
		}
		
		// Retrieve sensor values
		// 
		Iterator<String> paramIter = params.keySet().iterator();
		
		while( paramIter.hasNext() )
		{
			String key =  paramIter.next();
			
			//filters
			if( key.equals("targetType") || key.equals("target") || key.equals("eventTime") || key.equals("finishTime"))
			{
				continue;
			}
			
			String value = params.get(key);
			// Supported Type : int -> long -> float -> double --> string
			
			try{
				dbObject.put(key, Integer.parseInt(value));
			}catch( NumberFormatException e1 )
			{
				try{
					dbObject.put(key, Long.parseLong(value));
				}catch( NumberFormatException e2 )
				{
					try{
						dbObject.put(key, Float.parseFloat(value));
					}catch( NumberFormatException e3 )
					{
						try{
							dbObject.put(key, Double.parseDouble(value));
						}catch( NumberFormatException e4)
						{
							dbObject.put(key, value);
						}						
					}
				}
			}
		}
		
		ApplicationContext ctx = new GenericXmlApplicationContext(
				"classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx
				.getBean("mongoTemplate");

		DBCollection collection = mongoOperation.getCollection("Context");
		collection.insert(dbObject);
		((AbstractApplicationContext) ctx).close();

		return "Event Captured";
	}

	public String getEPCResourceOne(
			@RequestParam(required = false) String target,
			@RequestParam(required = false) String targetType) {

		String result = "";
		if (ConfigurationServlet.backend.equals("MongoDB")) {
			MongoRESTQueryService mrqs = new MongoRESTQueryService();
			result = mrqs.getEPCResourceOne(target, targetType);
		} else if (ConfigurationServlet.backend.equals("Cassandra")) {

		} else if (ConfigurationServlet.backend.equals("MySQL")) {

		}
		return result;
	}

	
}
