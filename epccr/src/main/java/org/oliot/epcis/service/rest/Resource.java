package org.oliot.epcis.service.rest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.log4j.Level;
import org.json.JSONArray;
import org.oliot.epcis.configuration.ConfigurationServlet;
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
	
	
	/**
	 * Return the resource indicating {epc} Time range can be adjustable
	 * 
	 * @param epc
	 *            Name of Resource
	 * @param from
	 *            Refer to Graphite URL API model
	 * 
	 *            RELATIVE_TIME or ABSOLUTE_TIME
	 * 
	 *            RELATIVE_TIME
	 * 
	 *            s: Seconds
	 * 
	 *            min: Minutes
	 * 
	 *            h: Hours
	 * 
	 *            d: Days
	 * 
	 *            w: Weeks mon: 30 Days(month)
	 * 
	 *            y: 365 Days (year)
	 * 
	 *            ABSOLUTE_TIME FORMAT SimpleDateFormat sdf = new
	 *            SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss"); GregorianCalendar
	 *            eventTimeCalendar = new GregorianCalendar(); Date date =
	 *            sdf.parse("time");
	 * @param until
	 *            examples: &from=-8d&until=-7d
	 *            &from=2007-12-02T21:32:52&until=2007-12-02T21:35:55
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "resource" })
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public String getEPCResource(@RequestParam(required = true) String target,
			@RequestParam(required = true) String targetType,
			@RequestParam(required = false) String from,
			@RequestParam(required = false) String until) {

		// Process Target
		if (!targetType.equals("Object") && !targetType.equals("Area")) {
			ConfigurationServlet.logger.log(Level.ERROR,
					"Service/query/rest/{target} : Need (Object|Area)");
			return new String(
					"ERROR: Service/query/rest/{target} : Need (Object|Area)");
		}
		targetType = "target" + targetType;
		
		// Process Time
		long fromTime = 0;
		long untilTime = 0;
		if (from != null) {
			from = from.trim();
			if (from.startsWith("-"))
				fromTime = getRelativeMiliTimes(from);
			else {
				fromTime = getAbsoluteMiliTimes(from);
			}
		}
		if (until != null) {
			until = until.trim();
			if (until.startsWith("-"))
				untilTime = getRelativeMiliTimes(until);
			else {
				untilTime = getAbsoluteMiliTimes(until);
			}
		}
		
		ApplicationContext ctx = new GenericXmlApplicationContext(
				"classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx
				.getBean("mongoTemplate");
		DBCollection collection = mongoOperation.getCollection("Context");
		
		if( collection == null )
		{
			return null;
		}
		
		DBObject query = new BasicDBObject();
		query.put(targetType, target);
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
		fields.put("targetObject", false);
		fields.put("targetArea", false);
		fields.put("_id", false);
		DBCursor cursor = collection.find(query, fields);
		cursor.sort((DBObject)new BasicDBObject().put("eventTime", new Integer(1)));
		
		JSONArray jArray = new JSONArray();
		while (cursor.hasNext()) {
			DBObject base = cursor.next();
			Map baseMap = base.toMap();
			jArray.put(baseMap);
		}		
		
		return jArray.toString(1);
	}	
	
	private long getAbsoluteMiliTimes(String absString) {

		try {
			SimpleDateFormat sdf = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
			Date date = sdf.parse(absString);
			return date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private long getRelativeMiliTimes(String relString) {
		try {
			int periodNumber;
			GregorianCalendar currentCalendar = new GregorianCalendar();
			DatatypeFactory df;

			df = DatatypeFactory.newInstance();
			XMLGregorianCalendar currentTime = df
					.newXMLGregorianCalendar(currentCalendar);
			if (relString.endsWith("s")) {
				// second
				periodNumber = Integer.parseInt(relString.substring(1,
						relString.length() - 1));

				Duration duration = DatatypeFactory.newInstance().newDuration(
						"-PT" + periodNumber + "S");
				currentTime.add(duration);

			} else if (relString.endsWith("min")) {
				// minute
				periodNumber = Integer.parseInt(relString.substring(1,
						relString.length() - 3));

				Duration duration = DatatypeFactory.newInstance().newDuration(
						"-PT" + periodNumber + "M");
				currentTime.add(duration);

			} else if (relString.endsWith("h")) {
				// hour
				periodNumber = Integer.parseInt(relString.substring(1,
						relString.length() - 1));

				Duration duration = DatatypeFactory.newInstance().newDuration(
						"-PT" + periodNumber + "H");
				currentTime.add(duration);

			} else if (relString.endsWith("d")) {
				// days
				periodNumber = Integer.parseInt(relString.substring(1,
						relString.length() - 1));

				Duration duration = DatatypeFactory.newInstance().newDuration(
						"-P" + periodNumber + "D");
				currentTime.add(duration);

			} else if (relString.endsWith("w")) {
				// weeks mon
				periodNumber = Integer.parseInt(relString.substring(1,
						relString.length() - 1));

				Duration duration = DatatypeFactory.newInstance().newDuration(
						"-P" + periodNumber + "M");
				currentTime.add(duration);

			} else if (relString.endsWith("y")) {
				// year
				periodNumber = Integer.parseInt(relString.substring(1,
						relString.length() - 1));

				Duration duration = DatatypeFactory.newInstance().newDuration(
						"-P" + periodNumber + "Y");
				currentTime.add(duration);
			}
			long timeMil = currentTime.toGregorianCalendar().getTimeInMillis();

			return timeMil;
		} catch (DatatypeConfigurationException e) {
			ConfigurationServlet.logger.log(Level.ERROR, e.toString());
		}
		return 0;
	}
}
