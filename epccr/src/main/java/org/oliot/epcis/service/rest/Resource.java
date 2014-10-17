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
import org.oliot.epcis.configuration.Configuration;
import org.oliot.tdt.SimplePureIdentityFilter;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.data.mongodb.core.CollectionOptions;
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
 * Copyright (C) 2014 Jaewook Jack Byun
 *
 * This project is experimental project named Electronic Product Code Context
 * Repository (EPCCR). This project pursues Resource Oriented Architecture (ROA)
 * for EPC-based event
 * 
 * Commonality with EPCIS: Getting powered with EPC's global uniqueness
 * 
 * Differences: Resource Oriented, not Service Oriented Resource(EPC)-driven URL
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
@Controller
@RequestMapping("/resource")
public class Resource {

	/**
	 * Put EPC Resource Method: PUT
	 * 
	 * @param target
	 *            EPC
	 * @param eventTime
	 *            SystemTimeMilis
	 * @param finishTime
	 *            SystemTimeMilis
	 * @param params
	 *            Other values to save
	 * @return
	 */
	@RequestMapping(method = RequestMethod.PUT)
	@ResponseBody
	public String putEPCResource(@RequestParam(required = true) String epc,
			@RequestParam(required = false) Long eventTime,
			@RequestParam(required = false) Long finishTime,
			@RequestParam Map<String, String> params) {

		DBObject dbObject = new BasicDBObject();

		if (!SimplePureIdentityFilter.isPureIdentity(epc)) {
			return "EPC should follow Pure Identity Form";
		} else {
			dbObject.put("epc", epc);
		}

		// Processing Time
		if (eventTime == null) {
			long time = new GregorianCalendar().getTimeInMillis();
			dbObject.put("eventTime", time);
			dbObject.put("finishTime", time);
		} else if (eventTime != null && finishTime == null) {
			dbObject.put("eventTime", eventTime.longValue());
			dbObject.put("finishTime", eventTime.longValue());
		} else if (eventTime != null && finishTime != null) {
			dbObject.put("eventTime", eventTime.longValue());
			dbObject.put("finishTime", finishTime.longValue());
		}

		// Retrieve sensor values
		Iterator<String> paramIter = params.keySet().iterator();

		while (paramIter.hasNext()) {
			String key = paramIter.next();

			// filters
			if (key.equals("targetType") || key.equals("target")
					|| key.equals("eventTime") || key.equals("finishTime")) {
				continue;
			}

			String value = params.get(key);
			// Supported Type : int -> long -> float -> double --> string

			try {
				dbObject.put(key, Integer.parseInt(value));
			} catch (NumberFormatException e1) {
				try {
					dbObject.put(key, Long.parseLong(value));
				} catch (NumberFormatException e2) {
					try {
						dbObject.put(key, Float.parseFloat(value));
					} catch (NumberFormatException e3) {
						try {
							dbObject.put(key, Double.parseDouble(value));
						} catch (NumberFormatException e4) {
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
	public String getEPCResource(@RequestParam(required = true) String epc,
			@RequestParam(required = false) String from,
			@RequestParam(required = false) String until) {

		if (!SimplePureIdentityFilter.isPureIdentity(epc)) {
			return "EPC should follow Pure Identity Form";
		}

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

		return jArray.toString(1);
	}

	
	/**
	 * post a subscription to specific EPC
	 * Once event arrives and is saved, it would be sent to RabbitMQ exchange
	 * @param epc			EPC
	 * @param destURL		URL to send recent event
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public String postSubscription(
			@RequestParam(required = true) String epc,
			@RequestParam(required = true) String destURL) {

		ConnectionFactory connectionFactory = new CachingConnectionFactory();
		AmqpAdmin admin = new RabbitAdmin(connectionFactory);
		FanoutExchange exchange = new FanoutExchange(epc);		
		admin.declareExchange(exchange);
		Queue queue = new Queue(epc+destURL);
		admin.declareQueue(queue);
		Binding binding = BindingBuilder.bind(queue).to(exchange);
		admin.declareBinding(binding);
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(epc+destURL);
		PublishQListener listener = new PublishQListener(destURL);
		container.setMessageListener(listener);
		container.start();
				
		return null;
	}
	
	/**
	 * delete a subscription to specific EPC
	 * @param epc			EPC
	 * @param destURL		URL to send recent event
	 * @return
	 */
	@RequestMapping(method = RequestMethod.DELETE)
	@ResponseBody
	public String deleteSubscription(
			@RequestParam(required = true) String epc,
			@RequestParam(required = true) String destURL) {

		return null;
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
			Configuration.logger.log(Level.ERROR, e.toString());
		}
		return 0;
	}
}
