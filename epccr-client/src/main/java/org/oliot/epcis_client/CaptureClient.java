package org.oliot.epcis_client;

import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONObject;
import org.oliot.tdt.SimplePureIdentityFilter;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

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
public class CaptureClient {

	private ConnectionFactory factory = null;
	private Connection conn = null;
	private Channel channel = null;
	private String queueName = null;

	/**
	 * Constructor of Capture Client with basic host : 127.0.0.1 with basic port
	 * : 5683
	 * 
	 * @param queueName
	 *            the name of work queue specified in Configuration.json e.g.
	 *            epcis_capture
	 */
	public CaptureClient(String queueName) {
		try {
			factory = new ConnectionFactory();
			factory.setUsername("guest");
			factory.setPassword("guest");
			factory.setVirtualHost("/");
			factory.setHost("localhost");
			factory.setPort(5672);
			conn = factory.newConnection();
			channel = conn.createChannel();
			this.queueName = queueName;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Constructor of Capture Client with host and port
	 * 
	 * @param queueName
	 *            the name of work queue specified in Configuration.json e.g.
	 *            epcis_capture
	 */
	public CaptureClient(String host, int port, String queueName) {
		try {
			factory = new ConnectionFactory();
			factory.setUsername("guest");
			factory.setPassword("guest");
			factory.setVirtualHost("/");
			factory.setHost(host);
			factory.setPort(port);
			conn = factory.newConnection();
			channel = conn.createChannel();
			this.queueName = queueName;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Send MAP data to Message Queue using current time
	 * 
	 * @param epc
	 *            EPC
	 * @param data
	 *            Map to send
	 */
	public void send(String epc, Map<String, Object> data) {

		try {
			JSONObject jObject = new JSONObject();

			// Process epc
			epc = epc.trim();
			if (!SimplePureIdentityFilter.isPureIdentity(epc)) {
				return;
			} else {
				jObject.put("epc", epc);
			}

			GregorianCalendar cal = new GregorianCalendar();
			jObject.put("eventTime", cal.getTimeInMillis());
			jObject.put("finishTime", cal.getTimeInMillis());

			Iterator<String> iter = data.keySet().iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				jObject.put(key, data.get(key));
			}
			channel.basicPublish("", queueName, null, jObject.toString()
					.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Send MAP data to Message Queue with specified time
	 * 
	 * @param epc
	 *            EPC
	 * @param eventTime
	 *            time when event occurred
	 * @param data
	 *            Map to send
	 */
	public void send(String epc, long eventTime, Map<String, Object> data) {

		try {
			JSONObject jObject = new JSONObject();

			// Process epc
			epc = epc.trim();
			if (!SimplePureIdentityFilter.isPureIdentity(epc)) {
				return;
			} else {
				jObject.put("epc", epc);
			}

			jObject.put("eventTime", eventTime);
			jObject.put("finishTime", eventTime);

			Iterator<String> iter = data.keySet().iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				jObject.put(key, data.get(key));
			}
			channel.basicPublish("", queueName, null, jObject.toString()
					.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Send MAP data to Message Queue with specified time range
	 * 
	 * @param targetType
	 *            (Object|Area)
	 * @param target
	 *            EPC
	 * @param eventTime
	 *            time when event occurred
	 * @param finishTime
	 *            time when event finished
	 * @param data
	 *            Map to send
	 */
	public void send(String epc, long eventTime, long finishTime,
			Map<String, Object> data) {

		try {

			if (eventTime > finishTime) {
				return;
			}

			JSONObject jObject = new JSONObject();

			// Process epc
			epc = epc.trim();
			if (!SimplePureIdentityFilter.isPureIdentity(epc)) {
				return;
			} else {
				jObject.put("epc", epc);
			}

			jObject.put("eventTime", eventTime);
			jObject.put("finishTime", finishTime);

			Iterator<String> iter = data.keySet().iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				jObject.put(key, data.get(key));
			}
			channel.basicPublish("", queueName, null, jObject.toString()
					.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reconnect to message queue with previous setting
	 */
	public void reconnect() {
		try {
			conn = factory.newConnection();
			channel = conn.createChannel();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Close channel and connection of message queue Reconnectable with
	 * reconnect() method
	 */
	public void close() {
		try {
			channel.close();
			conn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
