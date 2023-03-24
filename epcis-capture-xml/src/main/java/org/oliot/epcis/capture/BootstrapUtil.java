package org.oliot.epcis.capture;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.apache.log4j.Level;
import org.apache.log4j.xml.DOMConfigurator;
import org.bson.Document;
import org.oliot.epcis.unit_converter.UnitConverter;
import org.oliot.epcis.util.FileUtil;

import com.mongodb.reactivestreams.client.MongoClients;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.net.Inet4Address;

import static org.oliot.epcis.capture.XMLCaptureServer.*;

/**
 * Copyright (C) 2020-2022. (Jaewook Byun) all rights reserved.
 * <p>
 * Oliot EPCIS X is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * Among various modules, epcis-capture-xml acts as a server to receive
 * XML-formatted EPCIS documents to capture events in the documents into an
 * EPCIS repository.
 * <p>
 * 
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr
 *         <p>
 *         Associate Director, Auto-ID Labs, KAIST, bjw0829@kaist.ac.kr
 */
public class BootstrapUtil {

	private static void setConfigurationJson(String[] args) {
		// try main argument[0]
		try {
			configuration = new JsonObject(FileUtil.readFile(args[0]));
		} catch (Exception e) {
			if (e instanceof IOException)
				System.out.println("args[0] not found. try internal configuration.json");
			else
				System.out.println("args[0] has a syntax problem. try internal xmlCaptureConfiguration.json");
			try {
				configuration = new JsonObject(
						FileUtil.readFile(BootstrapUtil.class.getResourceAsStream("/xmlCaptureConfiguration.json")));
				System.out.println("Oliot EPCIS X - Capture (XML) is running as developer mode (Eclipse)");
			} catch (Exception e1) {
				try {
					configuration = new JsonObject(FileUtil.readFile(
							BootstrapUtil.class.getResourceAsStream("/resources/xmlCaptureConfiguration.json")));
					System.out.println("Oliot EPCIS X - Capture (XML) is running as user mode (Runnable Jar)");
				} catch (Exception e2) {
					System.out.println("No configration found. Terminated");
					System.exit(1);
				}
			}
		}
	}

	private static void setLogger(String args[]) {
		try {
			new FileReader(configuration.getString("log4j_conf_location")).close();
			DOMConfigurator.configure(configuration.getString("log4j_conf_location"));
			logger.info("Log4j appender configured with a file located in xmlCaptureConfiguration.json");
		} catch (IOException | FactoryConfigurationError | NullPointerException e2) {
			try {
				DOMConfigurator.configure(XMLCaptureServer.class.getResource("/log4j.xml"));
				logger.info("Log4j appender configured as developer mode ('/log4j.xml')");
			} catch (FactoryConfigurationError | NullPointerException e) {
				try {
					DOMConfigurator.configure(XMLCaptureServer.class.getResource("/resources/log4j.xml"));
					logger.info("Log4j appender configured as user mode ('/resources/log4j.xml')");
				} catch (FactoryConfigurationError | NullPointerException e1) {
					e1.printStackTrace();
					System.exit(1);
				}
			}
		}

		String level = configuration.getString("log4j_log_level");
		if (level.equals("ALL"))
			logger.setLevel(Level.ALL);
		else if (level.equals("DEBUG"))
			logger.setLevel(Level.DEBUG);
		else if (level.equals("INFO"))
			logger.setLevel(Level.INFO);
		else if (level.equals("WARN"))
			logger.setLevel(Level.WARN);
		else if (level.equals("ERROR"))
			logger.setLevel(Level.ERROR);
		else if (level.equals("FATAL"))
			logger.setLevel(Level.FATAL);
		else if (level.equals("OFF"))
			logger.setLevel(Level.OFF);
		else if (level.equals("TRACE"))
			logger.setLevel(Level.TRACE);
		logger.info("Logger level: " + level);
	}

	private static void setBackend(Vertx vertx) {
		try {
			// use native mongodb reactive streams driver
			mClient = MongoClients.create(configuration.getString("db_connection_string"));
			mDatabase = mClient.getDatabase(configuration.getString("db_name"));
			mMonitoringCollection = mDatabase.getCollection("monitoring", Document.class);
			mVocCollection = mDatabase.getCollection("MasterData", Document.class);
			mEventCollection = mDatabase.getCollection("EventData", Document.class);
			mTxCollection = mDatabase.getCollection("Tx", Document.class);
			logger.info("Backend configured");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private static void setValidator() {
		try {
			XMLCaptureServer.xmlValidator = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema")
					.newSchema(XMLCaptureServer.class.getResource("/schema/EPCglobal-epcis-2_0.xsd")).newValidator();
			XMLCaptureServer.xmlMasterDataValidator = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema")
					.newSchema(XMLCaptureServer.class.getResource("/schema/EPCglobal-epcis-masterdata-2_0.xsd"))
					.newValidator();
			logger.info("Schema Validator configured as a developer mode");
		} catch (Exception e) {
			try {
				XMLCaptureServer.xmlValidator = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema")
						.newSchema(XMLCaptureServer.class.getResource("/resources/schema/EPCglobal-epcis-2_0.xsd"))
						.newValidator();
				XMLCaptureServer.xmlMasterDataValidator = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema")
						.newSchema(XMLCaptureServer.class
								.getResource("/resources/schema/EPCglobal-epcis-masterdata-2_0.xsd"))
						.newValidator();
				logger.info("Schema Validator configured as a user mode");
			} catch (Exception e1) {
				logger.info("/schema/* not found");
				try {
					XMLCaptureServer.xmlValidator = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema")
							.newSchema(new File(configuration.getString("xml_schema_location"))).newValidator();
					XMLCaptureServer.xmlValidator = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema")
							.newSchema(new File(configuration.getString("xml_master_data_schema_location")))
							.newValidator();
					logger.info("Schema Validator configured");
				} catch (Exception e2) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		}
	}

	static void setHostPort() {
		try {
			host = Inet4Address.getLocalHost().getHostAddress();
		} catch (Exception e) {

		}
		try {
			port = XMLCaptureServer.configuration.getInteger("port");
			logger.info("Oliot EPCIS X - Capture (XML) listens to the port " + XMLCaptureServer.port);
		} catch (Exception e) {
			port = 8080;
			logger.info("Oliot EPCIS X - Capture (XML) listens to the default port 8080");
		}
	}

	static void configureServer(Vertx vertx, String[] args) {
		// Set Configuration (JSON)
		setConfigurationJson(args);
		// Set Logger
		setLogger(args);
		// Set Backend
		setBackend(vertx);
		// Set validator
		setValidator();
		// host and port set
		setHostPort();

		// Misc
		numOfVerticles = configuration.getInteger("number_of_verticles",
				Runtime.getRuntime().availableProcessors() + 1);
		logger.info("# of Vertx verticles: " + numOfVerticles);

		unitConverter = new UnitConverter();

		System.setProperty("java.net.preferIPv4Stack", "true");

		logger.info("The endpoint is at http://" + host + ":" + XMLCaptureServer.port + "/epcis/capture");
	}

}
