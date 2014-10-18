package org.oliot.epccr.configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.oliot.epccr.rest.CaptureQListener;
import org.oliot.epccr.rest.Subscription;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

import static org.oliot.epccr.util.MongoUtil.*;
import static org.oliot.epccr.util.MQUtil.*;

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
public class Configuration implements ServletContextListener {

	// Logger
	public static Logger logger;
	public static String webInfoPath;

	// Mongo DB
	public static String backend;
	public static int cappedSize;

	// Verification
	public static boolean isCaptureVerfificationOn;

	// Message Queue
	public static int numCaptureListener;
	public static String captureQueue;

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {

	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {

		ServletContext context = servletContextEvent.getServletContext();

		setLogger();

		doBasicConfiguration(context);

		initializeCaptureMessageQueue();

		initializePublishMessageQueue();

	}

	private void initializePublishMessageQueue() {

		Configuration.logger.info(" Initialize Existing Publish Message Queue ");
		List<Subscription> subList = queryExistingSubscription();
		for (int i = 0; i < subList.size(); i++) {
			Subscription sub = subList.get(i);
			addSubscriptionToMQ(sub.getEpc(), sub.getDestURL());
			Configuration.logger.info(" Started : MQ " + sub.getEpc() + " to " + sub.getDestURL() );
		}
	}

	private void setLogger() {
		// Log4j Setting
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO);
		Configuration.logger = Logger.getRootLogger();
	}

	private void doBasicConfiguration(ServletContext context) {
		// Get Congfiguration
		JSONObject json = getConfiguration(context);

		if( json == null )
		{
			Configuration.logger.error("Could not set up Configuration.json, please restart");
			return;
		}
		
		// Set up Backend
		setupBackend(json);

		// Set up Web Info Path
		Configuration.webInfoPath = context.getRealPath("/WEB-INF");

		// Set up capped_size
		setupCappedSize(json);

		// Set up capture_verification
		setupVerification(json);

		// Set up Message Queue
		setupMessageQueue(json);
	}

	private void initializeCaptureMessageQueue() {

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
		boolean isExistQueue = admin.deleteQueue(Configuration.captureQueue);
		if (isExistQueue == true) {
			Configuration.logger.info("Capture Queue Initialized");
		}

		Queue queue = new Queue(Configuration.captureQueue);
		admin.declareQueue(queue);

		for (int i = 0; i < Configuration.numCaptureListener; i++) {
			SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
			container.setConnectionFactory(connectionFactory);
			container.setQueueNames(Configuration.captureQueue);
			CaptureQListener listener = new CaptureQListener();
			container.setMessageListener(listener);
			container.start();
			Configuration.logger
					.info("Message Queue Service - Capture Listener " + (i + 1)
							+ " started");
		}

	}

	private JSONObject getConfiguration(ServletContext context) {
		try {
			String path = context.getRealPath("/WEB-INF");
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
			return new JSONObject(data);
		} catch (JSONException e) {
			Configuration.logger.error(e.toString());
		} catch (FileNotFoundException e) {
			Configuration.logger.error(e.toString());
		} catch (IOException e) {
			Configuration.logger.error(e.toString());
		}
		return null;
	}

	private void setupBackend(JSONObject json) {
		// Set up Backend
		String backend = json.getString("backend");
		if (backend == null) {
			Configuration.logger
					.error("Backend is null, please make sure Configuration.json is correct, and restart.");
		} else {
			Configuration.backend = backend;
			Configuration.logger.info("Backend - " + Configuration.backend);
		}

	}

	private void setupCappedSize(JSONObject json) {
		String capped_size = json.getString("capped_size");
		if (capped_size == null) {
			Configuration.logger
					.info("capped_size is null, use default size 50mb");
			cappedSize = 52428800;
		} else {
			try {
				cappedSize = Integer.parseInt(capped_size);
				Configuration.logger.info("capped_size : " + cappedSize);
			} catch (NumberFormatException e) {
				Configuration.logger
						.info("capped_size is not Long type, use default size 50mb");
				cappedSize = 52428800;
			}
		}
	}

	private void setupVerification(JSONObject json) {
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
	}

	private void setupMessageQueue(JSONObject json) {

		String numListener = json.getString("message_queue_capture_listener");
		captureQueue = json.getString("message_queue_capture_name");
		try {
			numCaptureListener = Integer.parseInt(numListener);
		} catch (NumberFormatException e) {
			Configuration.logger
					.error("number of capture listener should be integer, please make sure Configuration.json is correct, and restart.");
		}

	}
}
