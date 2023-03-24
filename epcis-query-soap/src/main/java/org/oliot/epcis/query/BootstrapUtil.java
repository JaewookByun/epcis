package org.oliot.epcis.query;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.apache.log4j.Level;
import org.apache.log4j.xml.DOMConfigurator;
import org.bson.Document;
import org.oliot.epcis.unit_converter.UnitConverter;
import org.oliot.epcis.util.FileUtil;

import com.mongodb.reactivestreams.client.MongoClients;

import javax.xml.parsers.FactoryConfigurationError;
import java.io.*;
import java.net.Inet4Address;

import static org.oliot.epcis.query.SOAPQueryServer.*;

/**
 * Copyright (C) 2020-2021. (Jaewook Byun) all rights reserved.
 * <p>
 * Oliot EPCIS X is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * Among various modules, epcis-query-soap acts as a server to receive queries
 * to provide filtered, sorted, limited events or masterdata of interest inside EPCIS
 * repository.
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
				System.out.println("args[0] has a syntax problem. try internal soapQueryConfiguration.json");
			try {
				configuration = new JsonObject(
						FileUtil.readFile(BootstrapUtil.class.getResourceAsStream("/soapQueryConfiguration.json")));
				System.out.println("Oliot EPCIS X - Query (SOAP) is running as developer mode (Eclipse)");
			} catch (Exception e1) {
				try {
					configuration = new JsonObject(FileUtil.readFile(
							BootstrapUtil.class.getResourceAsStream("/resources/soapQueryConfiguration.json")));
					System.out.println("Oliot EPCIS X - Query (SOAP) is running as user mode (Runnable Jar)");
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
			logger.info("Log4j appender configured with a file located in soapQueryConfiguration.json");
		} catch (IOException | FactoryConfigurationError | NullPointerException e2) {
			try {
				DOMConfigurator.configure(SOAPQueryServer.class.getResource("/log4j.xml"));
				logger.info("Log4j appender configured as developer mode ('/log4j.xml')");
			} catch (FactoryConfigurationError | NullPointerException e) {
				try {
					DOMConfigurator.configure(SOAPQueryServer.class.getResource("/resources/log4j.xml"));
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

			logger.info("Backend configured");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	static void setHostPort() {
		try {
			host = Inet4Address.getLocalHost().getHostAddress();
		} catch (Exception e) {

		}
		try {
			port = SOAPQueryServer.configuration.getInteger("port");
			logger.info("Oliot EPCIS X - Query (SOAP) listens to the port " + SOAPQueryServer.port);
		} catch (Exception e) {
			port = 8081;
			logger.info("Oliot EPCIS X - Query (SOAP) listens to the default port 8081");
		}
	}

	static void configureServer(Vertx vertx, String[] args) {
		// Set Configuration (JSON)
		setConfigurationJson(args);
		// Set Logger
		setLogger(args);
		// Set Backend
		setBackend(vertx);
		// host and port set
		setHostPort();

		// Misc
		numOfVerticles = configuration.getInteger("number_of_verticles",
				Runtime.getRuntime().availableProcessors() + 1);
		logger.info("# of Vertx verticles: " + numOfVerticles);

		unitConverter = new UnitConverter();

		System.setProperty("java.net.preferIPv4Stack", "true");

		logger.info("The endpoint is at http://" + host + ":" + SOAPQueryServer.port + "/epcis/query");
	}

}
