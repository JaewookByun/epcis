package org.oliot.epcis.configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.oliot.epcis.service.rest.CaptureQListener;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

/**
 * Copyright (C) 2014 Jaewook Jack Byun
 *
 * This project is experimental project named Electronic Product Code Context
 * Repository (EPCCR). This project pursues Resource Oriented Architecture (ROA) for EPC-based event
 * 
 * Commonality with EPCIS
 * 		Getting powered with EPC's global uniqueness
 * 
 * Differences
 * 		Resource Oriented, not Service Oriented 
 * 			Resource(EPC)-driven URL scheme
 * 			Best efforts to comply RESTful principle
 * 		Exploit flexibility rather than formal verification
 * 			JSON vs. XML
 * 			NOSQL vs. SQL
 * 		Focus on the Internet of Things beyond Supply Chain Management
 * 
 * @author Jaewook Jack Byun, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         bjw0829@{kaist.ac.kr,gmail.com}
 */
public class Configuration implements ServletContextListener {

	public static String backend;
	public static Logger logger;
	public static String webInfoPath;
	public static int cappedSize;
	public static boolean isCaptureVerfificationOn;
	public static boolean isMessageQueueOn;
	public static int numCaptureListener;
	public static String captureQueue;
	public static String queryExchange;

	public static boolean isRestMessageQueueOn;
	public static int numRestCaptureListener;
	public static String restCaptureQueue;

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {

	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {

		ServletContext context = servletContextEvent.getServletContext();

		setLogger();

		doBasicConfiguration(context);

		setMessageQueue();

	}

	private void setLogger() {
		// Log4j Setting
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO);
		Configuration.logger = Logger.getRootLogger();
	}

	private void doBasicConfiguration(ServletContext context) {
		String path = context.getRealPath("/WEB-INF");
		try {
			// Get Configuration.json
			File file = new File(path + "/Configuration.json");
			FileReader fileReader = new FileReader(file);
			BufferedReader reader = new BufferedReader(fileReader);

			String data = "";
			String line = null;
			while ((line = reader.readLine()) != null) {
				data += line;
			}
			reader.close();
			JSONObject json = new JSONObject(data);

			// Set up Backend
			String backend = json.getString("backend");
			if (backend == null) {
				Configuration.logger
						.error("Backend is null, please make sure Configuration.json is correct, and restart.");
			} else {
				Configuration.backend = backend;
				Configuration.logger.info("Backend - " + Configuration.backend);
			}
			Configuration.webInfoPath = context.getRealPath("/WEB-INF");

			// Set up capped_size
			String capped_size = json.getString("capped_size");
			if (capped_size == null )
			{
				Configuration.logger
				.info("capped_size is null, use default size 50mb");
				cappedSize = 52428800;
			}else
			{
				try{
					cappedSize = Integer.parseInt(capped_size);
					Configuration.logger
					.info("capped_size : " + cappedSize);
				}catch(NumberFormatException e)
				{
					Configuration.logger
					.info("capped_size is not Long type, use default size 50mb");
					cappedSize = 52428800;
				}
			}			
			
			// Set up capture_verification
			String captureVerification = json.getString("capture_verification");
			if (captureVerification == null) {
				Configuration.logger
						.error("capture_verification is null, please make sure Configuration.json is correct, and restart.");
			}
			captureVerification = captureVerification.trim();
			if (captureVerification.equals("on")) {
				Configuration.isCaptureVerfificationOn = true;
				Configuration.logger.info("Capture_Verification - ON ");
			} else if (captureVerification.equals("off")) {
				Configuration.isCaptureVerfificationOn = false;
				Configuration.logger.info("Capture_Verification - OFF ");
			} else {
				Configuration.logger
						.error("capture_verification should be (on|off), please make sure Configuration.json is correct, and restart.");
			}

			// Set up REST Message Queue
			String rest_message_queue = json.getString("rest_message_queue");
			if (rest_message_queue.equals("on")) {
				Configuration.isRestMessageQueueOn = true;
				Configuration.logger.info("REST Message Queue - ON ");
			} else if (rest_message_queue.equals("off")) {
				Configuration.isRestMessageQueueOn = false;
				Configuration.logger.info("REST Message Queue - OFF ");
			} else {
				Configuration.logger
						.error("rest_message_queue should be (on|off), please make sure Configuration.json is correct, and restart.");
			}
			String numRestListener = json
					.getString("rest_message_queue_capture_listener");
			restCaptureQueue = json
					.getString("rest_message_queue_capture_name");
			if (isRestMessageQueueOn == true
					&& (numRestListener == null || restCaptureQueue == null)) {
				Configuration.logger
						.error("if rest_message_queue on, number of capture listener should be described, please make sure Configuration.json is correct, and restart.");
			} else {
				try {
					numRestCaptureListener = Integer.parseInt(numRestListener);
				} catch (NumberFormatException e) {
					Configuration.logger
							.error("number of rest capture listener should be integer, please make sure Configuration.json is correct, and restart.");
				}
			}
			

		} catch (Exception ex) {
			Configuration.logger.error(ex.toString());
		}
	}

	private void setMessageQueue() {
		if (Configuration.isRestMessageQueueOn == true) {
			Configuration.logger.info("REST Message Queue Service - Started ");
			Configuration.logger
					.info("REST Message Queue Service - Number of Capture Listener: "
							+ Configuration.numRestCaptureListener);
			Configuration.logger
					.info("REST Message Queue Service - REST Capture Queue Name: "
							+ Configuration.restCaptureQueue);

			// Message Queue Initialization
			ConnectionFactory connectionFactory = new CachingConnectionFactory();
			AmqpAdmin admin = new RabbitAdmin(connectionFactory);
			boolean isExistQueue = admin
					.deleteQueue(Configuration.restCaptureQueue);
			if (isExistQueue == true) {
				Configuration.logger.info("REST Capture Queue Initialized");
			}
			
			Queue queue = new Queue(Configuration.restCaptureQueue);
			admin.declareQueue(queue);
			
			for (int i = 0; i < Configuration.numRestCaptureListener; i++) {
				SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
				container.setConnectionFactory(connectionFactory);
				container.setQueueNames(Configuration.restCaptureQueue);
				CaptureQListener listener = new CaptureQListener();
				container.setMessageListener(listener);
				container.start();
				Configuration.logger
						.info("REST Message Queue Service - REST Capture Listener "
								+ (i + 1) + " started");
			}
		}
	}
}
