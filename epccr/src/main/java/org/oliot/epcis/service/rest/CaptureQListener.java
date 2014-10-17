package org.oliot.epcis.service.rest;

import java.io.UnsupportedEncodingException;
import java.util.GregorianCalendar;

import org.json.JSONException;
import org.json.JSONObject;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.tdt.SimplePureIdentityFilter;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

/**
 * Copyright (C) 2014 Jaewook Jack Byun
 *
 * This project is experimental project named Electronic Product Code Context
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
					return;
				}
				jsonObject.put("eventTime", eventTime.longValue());
				jsonObject.put("finishTime", finishTime.longValue());
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
			DBObject dbObject = (DBObject) JSON.parse(jsonObject.toString());
			collection.insert(dbObject);
			System.out.println(new GregorianCalendar().getTime().toString()
					+ " saved ");
			((AbstractApplicationContext) ctx).close();

			// Send to queue
			ConnectionFactory connectionFactory = new CachingConnectionFactory();
			AmqpAdmin admin = new RabbitAdmin(connectionFactory);
			FanoutExchange exchange = new FanoutExchange(epc);			
			admin.declareExchange(exchange);
			AmqpTemplate template = new RabbitTemplate(connectionFactory);
			template.convertAndSend(epc, null, jsonObject.toString());
			
		} catch (UnsupportedEncodingException e) {
			Configuration.logger.error(e.toString());
		} catch (JSONException e) {
			Configuration.logger.error(e.toString());
		} catch (NumberFormatException e) {
			Configuration.logger.error(e.toString());
		}
	}
}
