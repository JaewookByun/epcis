package org.oliot.epccr.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONObject;
import org.oliot.epccr.configuration.Configuration;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

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
public class DataUtil {
	/**
	 * Check whether Correct URL or not
	 * 
	 * @param destURL
	 *            URL to be checked
	 * @return
	 */
	public static String checkURL(String destURL) {
		try {
			new URL(destURL);
		} catch (MalformedURLException e) {
			return e.toString();
		}
		return null;
	}

	public static DBObject prepareResource(String epc, Long eventTime,
			Long finishTime, Map<String, String> params) {

		DBObject dbObject = new BasicDBObject();

		// Processing EPC
		dbObject.put("epc", epc);

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
		return dbObject;
	}

	public static DBObject prepareResource(JSONObject jsonObject) {
		// Process time
		if (jsonObject.isNull("eventTime") == true) {
			long time = new GregorianCalendar().getTimeInMillis();
			jsonObject.put("eventTime", time);
			jsonObject.put("finishTime", time);
		} else if (jsonObject.isNull("eventTime") == false
				&& jsonObject.isNull("finishTime") == true) {
			Long eventTime = jsonObject.getLong("eventTime");
			jsonObject.put("eventTime", eventTime.longValue());
			jsonObject.put("finishTime", eventTime.longValue());
		} else if (jsonObject.isNull("eventTime") == false
				&& jsonObject.isNull("finishTime") == false) {

			Long eventTime = jsonObject.getLong("eventTime");
			Long finishTime = jsonObject.getLong("finishTime");

			if (eventTime > finishTime) {
				Configuration.logger
						.error("eventTime should be larger than finishTime");
				return	null;
			}
			jsonObject.put("eventTime", eventTime.longValue());
			jsonObject.put("finishTime", finishTime.longValue());
		}
		DBObject dbObject = (DBObject) JSON.parse(jsonObject.toString());
		return dbObject;
	}
	
	@SuppressWarnings("rawtypes")
	public static JSONObject parseDBObject(DBObject dbObject)
	{
		Map map = dbObject.toMap();
		return new JSONObject(map);
	}
}
