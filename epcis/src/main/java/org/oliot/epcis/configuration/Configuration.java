package org.oliot.epcis.configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.oliot.epcis.service.capture.CaptureMQListener;
import org.oliot.epcis.service.query.mongodb.MongoSubscription;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

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
public class Configuration implements ServletContextListener {

	public static String backend;
	public static Logger logger;
	public static String webInfoPath;
	public static boolean isCaptureVerfificationOn;
	public static boolean isMessageQueueOn;
	public static int numCaptureListener;
	public static String captureQueue;
	public static String queryExchange;

	public static boolean isRestMessageQueueOn;
	public static int numRestCaptureListener;
	public static String restCaptureQueue;
	public static String restQueryExchange;

	public static List<SimpleMessageListenerContainer> MQContainerList;

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {

		// Set Logger
		setLogger();

		// Set Basic Configuration with Configuration.json
		setBasicConfiguration(servletContextEvent.getServletContext());

		// Set Capture Message Queue
		MQContainerList = new ArrayList<SimpleMessageListenerContainer>();
		setCaptureMessageQueue();

		// load existing subscription
		loadExistingSubscription();
	}

	private void setLogger() {
		// Log4j Setting
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO);
		Configuration.logger = Logger.getRootLogger();
	}

	private void setBasicConfiguration(ServletContext context) {
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

			// Set up Message Queue
			String message_queue = json.getString("message_queue");
			if (message_queue.equals("on")) {
				Configuration.isMessageQueueOn = true;
				Configuration.logger.info("Message Queue - ON ");
			} else if (message_queue.equals("off")) {
				Configuration.isMessageQueueOn = false;
				Configuration.logger.info("Message Queue - OFF ");
			} else {
				Configuration.logger
						.error("message_queue should be (on|off), please make sure Configuration.json is correct, and restart.");
			}
			String numListener = json
					.getString("message_queue_capture_listener");
			captureQueue = json.getString("message_queue_capture_name");
			if (isMessageQueueOn == true
					&& (numListener == null || captureQueue == null)) {
				Configuration.logger
						.error("if message_queue on, number of capture listener should be described, please make sure Configuration.json is correct, and restart.");
			} else {
				try {
					numCaptureListener = Integer.parseInt(numListener);
				} catch (NumberFormatException e) {
					Configuration.logger
							.error("number of capture listener should be integer, please make sure Configuration.json is correct, and restart.");
				}
			}
			queryExchange = json.getString("message_queue_exchange_query");
			if (isMessageQueueOn == true && queryExchange == null) {
				Configuration.logger
						.error("if message_queue on, query exchange should be described, please make sure Configuration.json is correct, and restart.");
			}
		} catch (Exception ex) {
			Configuration.logger.error(ex.toString());
		}

	}

	private void setCaptureMessageQueue() {
		if (Configuration.isMessageQueueOn == true) {
			Configuration.logger.info("Message Queue Service - Started ");
			Configuration.logger
					.info("Message Queue Service - Number of Capture Listener: "
							+ Configuration.numCaptureListener);
			Configuration.logger
					.info("Message Queue Service - Capture Queue Name: "
							+ Configuration.captureQueue);

			// Message Queue Initialization
			ConnectionFactory connectionFactory = new CachingConnectionFactory();
			AmqpAdmin admin = new RabbitAdmin(connectionFactory);
			boolean isExistQueue = admin
					.deleteQueue(Configuration.captureQueue);
			if (isExistQueue == true) {
				Configuration.logger.info("Capture Queue Initialized");
			}
			Queue queue = new Queue(Configuration.captureQueue);
			admin.declareQueue(queue);

			for (int i = 0; i < Configuration.numCaptureListener; i++) {
				SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
				container.setConnectionFactory(connectionFactory);
				container.setQueueNames(Configuration.captureQueue);
				CaptureMQListener listener = new CaptureMQListener();
				container.setMessageListener(listener);
				container.start();
				MQContainerList.add(container);
				Configuration.logger
						.info("Message Queue Service - Capture Listener "
								+ (i + 1) + " started");
			}

		}
	}

	private void loadExistingSubscription() {
		if (Configuration.backend.equals("MongoDB")) {
			MongoSubscription ms = new MongoSubscription();
			ms.init();
		} else if (Configuration.backend.equals("Cassandra")) {

		} else if (Configuration.backend.equals("MySQL")) {

		}
	}
}
