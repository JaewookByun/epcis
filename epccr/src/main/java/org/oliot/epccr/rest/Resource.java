package org.oliot.epccr.rest;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.oliot.epccr.configuration.Configuration;
import org.oliot.tdt.SimplePureIdentityFilter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mongodb.DBObject;

import static org.oliot.epccr.util.TimeUtil.*;
import static org.oliot.epccr.util.MongoUtil.*;
import static org.oliot.epccr.util.MQUtil.*;
import static org.oliot.epccr.util.DataUtil.*;

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
	 * @param epc
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

		// Check EPC
		if (!SimplePureIdentityFilter.isPureIdentity(epc)) {
			return "EPC should follow Pure Identity Form";
		}

		// Prepare Resource
		DBObject dbObject = prepareResource(epc, eventTime, finishTime, params);

		// Save Resource to DB
		saveResourceToDB(epc, dbObject);

		// Change DBObject to JSONObject
		JSONObject jsonObject = parseDBObject(dbObject);
		
		// Send to Publish Message Queue
		addSubscriptionToPublishMQ(epc, jsonObject);
		
		return "OK";
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
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public String getEPCResource(@RequestParam(required = true) String epc,
			@RequestParam(required = false) String from,
			@RequestParam(required = false) String until) {

		// Check EPC
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

		// Query Resource From DB
		JSONArray jArray = queryResourceFromDB(epc, fromTime, untilTime);		

		return jArray.toString(1);
	}

	/**
	 * post a subscription to specific EPC Once event arrives and is saved, it
	 * would be sent to RabbitMQ exchange
	 * 
	 * @param epc
	 *            EPC
	 * @param destURL
	 *            URL to send recent event
	 * @return return error if URL is malformed one
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public String postSubscription(@RequestParam(required = true) String epc,
			@RequestParam(required = true) String destURL) {

		// Check URL
		String e;
		if ((e = checkURL(destURL)) != null)
			return e;

		// Check Redundancy : URL & destURL
		if( isExistingSubscription(epc, destURL) == true  )
		{
			Configuration.logger
			.error("Redundant Subscription for " + epc + " to " + destURL );
		}
		
		// Add Subscription to Message Queue
		addSubscriptionToMQ(epc, destURL);

		// Add Subscription to DataBase
		addSubscriptionToDB(epc, destURL);

		return null;
	}

	/**
	 * delete a subscription to specific EPC
	 * 
	 * @param epc
	 *            EPC
	 * @param destURL
	 *            URL to send recent event
	 * @return
	 */
	@RequestMapping(method = RequestMethod.DELETE)
	@ResponseBody
	public String deleteSubscription(@RequestParam(required = true) String epc,
			@RequestParam(required = true) String destURL) {

		// Delete Subscription from Message Queue
		delSubscriptionFromMQ(epc, destURL);
		
		// Delete Subscription to DataBase
		delSubscriptionFromDB(epc, destURL);
		
		return null;
	}
}
