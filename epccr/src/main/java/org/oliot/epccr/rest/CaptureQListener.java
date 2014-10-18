package org.oliot.epccr.rest;

import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;
import org.oliot.epccr.configuration.Configuration;
import org.oliot.tdt.SimplePureIdentityFilter;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import com.mongodb.DBObject;

import static org.oliot.epccr.util.MongoUtil.*;
import static org.oliot.epccr.util.MQUtil.*;
import static org.oliot.epccr.util.DataUtil.*;

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
public class CaptureQListener implements MessageListener {

	@Override
	public void onMessage(Message message) {

		try {
			byte[] bytes = message.getBody();
			String jsonString = new String(bytes, "UTF-8");

			JSONObject jsonObject = new JSONObject(jsonString);

			String epc = "";

			// Verify input
			if (Configuration.isCaptureVerfificationOn == true) {
				if (jsonObject.isNull("epc") == false) {
					epc = jsonObject.getString("epc");
					if (!SimplePureIdentityFilter.isPureIdentity(epc)) {
						Configuration.logger
								.error("EPC should follow Pure Identity Form");
						return;
					}
				} else {
					Configuration.logger.error("epc should be inserted");
					return;
				}
			} else {
				epc = jsonObject.getString("epc");
			}

			// Prepare DBObject
			DBObject dbObject = prepareResource(jsonObject);
			
			// Save Resource To DB
			saveResourceToDB(epc, dbObject);
			
			// Send to Publish Message Queue
			addSubscriptionToPublishMQ(epc, jsonObject);
			
		} catch (UnsupportedEncodingException e) {
			Configuration.logger.error(e.toString());
		} catch (JSONException e) {
			Configuration.logger.error(e.toString());
		} catch (NumberFormatException e) {
			Configuration.logger.error(e.toString());
		}
	}
}
