package org.oliot.epcis.service.query.mongodb;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.JobKey.jobKey;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.TriggerKey.triggerKey;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.log4j.Level;
import org.json.JSONObject;
import org.oliot.epcis.configuration.ConfigurationServlet;
import org.oliot.model.epcis.RESTSubscriptionType;
import org.oliot.model.epcis.SensingElementType;
import org.oliot.model.epcis.SensorEventType;
import org.oliot.model.epcis.SubscriptionType;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.BasicDBList;
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
public class MongoRESTQueryService {

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
	public String getEPCResource(String target, String targetType, String from,
			String until) {

		String resultString = "";

		if (!targetType.equals("Object") && !targetType.equals("Area")) {
			ConfigurationServlet.logger.log(Level.ERROR,
					"Service/query/rest/{target} : Need (Object|Area)");
			return new String(
					"ERROR: Service/query/rest/{target} : Need (Object|Area)");
		}
		targetType = "target" + targetType;

		ApplicationContext ctx = new GenericXmlApplicationContext(
				"classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx
				.getBean("mongoTemplate");

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

		DBCollection collection = mongoOperation.getCollection("SensorEvent");
		DBCollection sensorCollection = mongoOperation.getCollection("Sensor");
		if (collection != null) {
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
			fields.put("targetObject", true);
			fields.put("targetArea", true);
			fields.put("sensingList", true);
			fields.put("_id", false);
			DBCursor cursor = collection.find(query, fields);
			BasicDBList sensingDBList = new BasicDBList();
			while (cursor.hasNext()) {
				DBObject base = cursor.next();
				BasicDBList sensingList = (BasicDBList) base.get("sensingList");
				for (int i = 0; i < sensingList.size(); i++) {
					String sensorEPC = sensingList.get(i).toString();
					DBObject subQuery = new BasicDBObject();
					subQuery.put("epc", sensorEPC);
					if (fromTime != 0) {
						DBObject sub = new BasicDBObject();
						sub.put("$gte", fromTime);
						subQuery.put("eventTime", sub);
					}
					if (untilTime != 0) {
						DBObject sub = new BasicDBObject();
						sub.put("$lte", untilTime);
						subQuery.put("eventTime", sub);
					}
					DBObject subFields = new BasicDBObject();
					subFields.put("type", true);
					subFields.put("value", true);
					subFields.put("startTime", true);
					subFields.put("finishTime", true);
					subFields.put("_id", false);
					DBCursor subCursor = sensorCollection.find(subQuery,
							subFields);
					while (subCursor.hasNext()) {
						DBObject subResult = subCursor.next();
						if (subResult.get("type").equals("time")
								|| subResult.get("type").equals("did"))
							continue;

						subResult.put(subResult.get("type").toString(),
								subResult.get("value").toString());
						subResult.removeField("type");
						subResult.removeField("value");
						sensingDBList.add(subResult);
					}
				}
			}
			resultString = sensingDBList.toString();
		}

		((AbstractApplicationContext) ctx).close();
		return resultString;
	}

	public String removeSubscription(String subscriptionID) {
		ApplicationContext ctx = new GenericXmlApplicationContext(
				"classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx
				.getBean("mongoTemplate");

		// Its size should be 0 or 1
		List<RESTSubscriptionType> subscriptions = mongoOperation.find(
				new Query(Criteria.where("subscriptionID").is(subscriptionID)),
				RESTSubscriptionType.class);

		for (int i = 0; i < subscriptions.size(); i++) {
			RESTSubscriptionType subscription = subscriptions.get(i);
			// Remove from current Quartz
			String error = removeScheduleFromQuartz(subscription);
			if (error != null) {
				((AbstractApplicationContext) ctx).close();
				return error;
			}
			// Remove from DB list
			removeScheduleFromDB(mongoOperation, subscription);
		}
		((AbstractApplicationContext) ctx).close();
		return new String("Subscription : " + subscriptionID + " removed ");
	}

	private String removeScheduleFromQuartz(RESTSubscriptionType subscription) {
		try {
			String destURL = subscription.getDestURL();
			String targetType = subscription.getTargetType();
			String target = subscription.getTarget();
			String cronExpression = subscription.getCronExpression();

			MongoSubscription.sched.unscheduleJob(triggerKey(destURL,
					targetType + "," + target + "," + cronExpression));
			MongoSubscription.sched.deleteJob(jobKey(destURL, targetType + ","
					+ target + "," + cronExpression));
			ConfigurationServlet.logger.log(Level.INFO, "Subscription ID: "
					+ subscription + " is removed from scheduler");
		} catch (SchedulerException e) {
			ConfigurationServlet.logger.log(Level.ERROR, e.toString());
			return e.toString();
		}
		return null;
	}

	private void removeScheduleFromDB(MongoOperations mongoOperation,
			RESTSubscriptionType subscription) {
		mongoOperation.remove(
				new Query(Criteria.where("subscriptionID").is(
						subscription.getSubscriptionID())),
				SubscriptionType.class);
		ConfigurationServlet.logger.log(Level.INFO, "Subscription ID: "
				+ subscription + " is removed from DB");
	}

	public String addSubscription(String subscriptionID, String target,
			String targetType, String cronExpression, String destURL) {

		// Check whether exists;
		RESTSubscriptionType st = new RESTSubscriptionType(subscriptionID,
				destURL, cronExpression, target, targetType);
		ApplicationContext ctx = new GenericXmlApplicationContext(
				"classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx
				.getBean("mongoTemplate");

		List<RESTSubscriptionType> existenceTest = mongoOperation.find(
				new Query(Criteria.where("subscriptionID").is(subscriptionID)),
				RESTSubscriptionType.class);
		if (existenceTest.size() != 0) {
			((AbstractApplicationContext) ctx).close();
			return "ERROR: Redundant Subscription ID: " + subscriptionID;
		}

		String error = addToScheduler(subscriptionID, target, targetType,
				cronExpression, destURL);
		if (error != null) {
			((AbstractApplicationContext) ctx).close();
			return error;
		}
		mongoOperation.save(st);

		ConfigurationServlet.logger.log(Level.INFO, "Subscription ID: "
				+ subscriptionID + " is added to DB. ");
		((AbstractApplicationContext) ctx).close();

		return new String("Subscription ID: " + target + " for " + targetType
				+ " is added to quartz scheduler. ");
	}

	public String addToScheduler(String subscriptionID, String target,
			String targetType, String cronExpression, String destURL) {

		try {
			JobDataMap map = new JobDataMap();
			map.put("target", target);
			map.put("targetType", targetType);
			map.put("destURL", destURL);
			map.put("cronExpression", cronExpression);
			map.put("subscriptionID", subscriptionID);

			// targetType target destURL , should be distinguished
			JobDetail job = newJob(MongoRESTSubscriptionTask.class)
					.withIdentity(destURL,
							targetType + "," + target + "," + cronExpression)
					.setJobData(map).build();

			Trigger trigger = newTrigger()
					.withIdentity(destURL,
							targetType + "," + target + "," + cronExpression)
					.startNow()
					.withSchedule(cronSchedule(cronExpression))
					.forJob(destURL,
							targetType + "," + target + "," + cronExpression)
					.build();

			if (MongoRESTSubscription.sched.isStarted() != true)
				MongoRESTSubscription.sched.start();
			MongoRESTSubscription.sched.scheduleJob(job, trigger);
			ConfigurationServlet.logger.log(Level.INFO, "Subscription ID: "
					+ target + " for " + targetType + " to the destination "
					+ destURL + " is added to quartz scheduler. ");
		} catch (SchedulerException e) {
			return e.toString();
		}
		return null;
	}

	public String getEPCResourceOne(String target, String targetType) {

		String resultString = "";

		if (!targetType.equals("Object") && !targetType.equals("Area")) {
			ConfigurationServlet.logger.log(Level.ERROR,
					"Service/query/rest/{target} : Need (Object|Area)");
			return new String(
					"ERROR: Service/query/rest/{target} : Need (Object|Area)");
		}
		targetType = "target" + targetType;

		ApplicationContext ctx = new GenericXmlApplicationContext(
				"classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx
				.getBean("mongoTemplate");

		Query query = new Query();
		query.addCriteria(Criteria.where(targetType).is(target));
		query.with(new Sort(Sort.Direction.DESC, "eventTime"));

		SensorEventType sensorEvent = mongoOperation.findOne(query,
				SensorEventType.class);
		if (sensorEvent.getSensingList() != null) {
			if (sensorEvent.getSensingList().getSensingElement() != null) {
				List<SensingElementType> setList = sensorEvent.getSensingList()
						.getSensingElement();
				String startTime = sensorEvent.getEventTime().toString();
				String finishTime = sensorEvent.getFinishTime().toString();
				JSONObject jObj = new JSONObject();
				for (int i = 0; i < setList.size(); i++) {
					SensingElementType set = setList.get(i);
					if (set.getType() == null || set.getValue() == null)
						continue;
					if (set.getType().equals("time")
							|| set.getType().equals("did"))
						continue;
					jObj.put(set.getType(), set.getValue());
					jObj.put("startTime", startTime);
					jObj.put("finishTime", finishTime);
				}
				resultString = jObj.toString(1);
			}
		}
		((AbstractApplicationContext) ctx).close();
		return resultString;
	}

	public long getAbsoluteMiliTimes(String absString) {

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

	public long getRelativeMiliTimes(String relString) {
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
