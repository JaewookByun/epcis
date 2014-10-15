package org.oliot.epcis_client;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONObject;

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
	 * Constructor of Capture Client
	 * with basic host : 127.0.0.1
	 * with basic port : 5683
	 * @param queueName the name of work queue specified in Configuration.json e.g. epcis_capture
	 */
	public CaptureClient(String queueName) {
		try {
			factory = new ConnectionFactory();
			factory.setUsername("guest");
			factory.setPassword("guest");
			factory.setVirtualHost("/");
			factory.setHost("127.0.0.1");
			factory.setPort(5683);
			conn = factory.newConnection();
			channel = conn.createChannel();
			this.queueName = queueName;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Constructor of Capture Client
	 * with host and port 
	 * @param queueName the name of work queue specified in Configuration.json e.g. epcis_capture
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
	 * Send data identifiable with epc
	 * Use currentTime
	 * @param data
	 */
	public void send(String targetType, String epc, Map<String, Object> data) {
		
		try {
			JSONObject jObject = new JSONObject();			
			Iterator<String> iter = data.keySet().iterator();
			while(iter.hasNext())
			{
				
			}
			channel.basicPublish("", queueName, null, jObject.toString().getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			channel.close();
			conn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
