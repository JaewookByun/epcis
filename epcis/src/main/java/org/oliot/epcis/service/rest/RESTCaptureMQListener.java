package org.oliot.epcis.service.rest;

import java.io.UnsupportedEncodingException;
import java.util.GregorianCalendar;

import org.json.JSONException;
import org.json.JSONObject;
import org.oliot.epcis.configuration.ConfigurationServlet;
import org.oliot.tdt.SimplePureIdentityFilter;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class RESTCaptureMQListener implements MessageListener {

	@Override
	public void onMessage(Message message) {

		try {
			byte[] bytes = message.getBody();
			String jsonString = new String(bytes, "UTF-8");

			JSONObject jsonObject = new JSONObject(jsonString);

			if (ConfigurationServlet.isCaptureVerfificationOn == true) {
				// Verify input
				if (jsonObject.isNull("targetObject") == false) {
					if (!SimplePureIdentityFilter.isPureIdentity(jsonObject
							.getString("targetObject"))) {
						ConfigurationServlet.logger
								.error("targetObject should follow Pure Identity Form");
						return;
					}
				} else if (jsonObject.isNull("targetArea") == false) {
					if (!SimplePureIdentityFilter.isPureIdentity(jsonObject
							.getString("targetArea"))) {
						ConfigurationServlet.logger
								.error("targetArea should follow Pure Identity Form");
						return;
					}
				} else if (jsonObject.isNull("targetObject") == true
						|| jsonObject.isNull("targetArea") == true) {
					ConfigurationServlet.logger
							.error("targetObject or targetArea should be inserted");
					return;
				}
			}

			// Process time

			String eventTime = jsonObject.getString("eventTime");
			String finishTime = jsonObject.getString("finishTime");

			if (eventTime == null) {
				long time = new GregorianCalendar().getTimeInMillis();
				jsonObject.put("eventTime", time);
				jsonObject.put("finishTime", time);
			} else if (eventTime != null && finishTime == null) {
				Long eventLong = Long.parseLong(eventTime);
				jsonObject.put("eventTime", eventLong.longValue());
				jsonObject.put("finishTime", eventLong.longValue());
			} else if (eventTime != null && finishTime != null) {
				Long eventLong = Long.parseLong(eventTime);
				Long finishLong = Long.parseLong(finishTime);
				if (eventLong > finishLong) {
					ConfigurationServlet.logger
							.error("eventTime should be larger than finishTime");
					return;
				}
				jsonObject.put("eventTime", eventLong.longValue());
				jsonObject.put("finishTime", finishLong.longValue());
			}
			ApplicationContext ctx = new GenericXmlApplicationContext(
					"classpath:MongoConfig.xml");
			MongoOperations mongoOperation = (MongoOperations) ctx
					.getBean("mongoTemplate");

			DBCollection collection = mongoOperation.getCollection("Context");
			DBObject dbObject = (DBObject) JSON.parse(jsonObject.toString());
			collection.insert(dbObject);
			((AbstractApplicationContext) ctx).close();

		} catch (UnsupportedEncodingException e) {
			ConfigurationServlet.logger.error(e.toString());
		} catch (JSONException e) {
			ConfigurationServlet.logger.error(e.toString());
		} catch (NumberFormatException e) {
			ConfigurationServlet.logger.error(e.toString());
		}
	}
}
