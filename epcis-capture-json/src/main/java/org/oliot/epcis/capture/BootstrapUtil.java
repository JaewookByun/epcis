package org.oliot.epcis.capture;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.json.schema.SchemaParser;
import io.vertx.json.schema.SchemaRouter;
import io.vertx.json.schema.SchemaRouterOptions;
import io.vertx.json.schema.draft7.Draft7SchemaParser;

import org.apache.log4j.Level;
import org.apache.log4j.xml.DOMConfigurator;
import org.oliot.epcis.unit_converter.UnitConverter;
import org.oliot.epcis.util.FileUtil;

import javax.xml.parsers.FactoryConfigurationError;

import java.io.*;
import java.net.Inet4Address;
import java.net.URL;
import java.util.HashMap;

import static org.oliot.epcis.capture.JSONCaptureServer.*;

/**
 * Copyright (C) 2020-2022. (Jaewook Byun) all rights reserved.
 * <p>
 * Oliot EPCIS X is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * Among various modules, epcis-capture-json acts as a server to receive
 * JSON-formatted EPCIS documents to capture events in the documents into an
 * EPCIS repository.
 * <p>
 * 
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr
 *         <p>
 *         Associate Director, Auto-ID Labs, KAIST, bjw0829@kaist.ac.kr
 */
public class BootstrapUtil {

	static void setConfigurationJson(String[] args) {
		// try main argument[0]
		try {
			configuration = new JsonObject(FileUtil.readFile(args[0]));
		} catch (Exception e) {
			if (e instanceof IOException)
				System.out.println("args[0] not found. try internal configuration.json");
			else
				System.out.println("args[0] has a syntax problem. try internal jsonCaptureConfiguration.json");
			try {
				configuration = new JsonObject(
						FileUtil.readFile(BootstrapUtil.class.getResourceAsStream("/jsonCaptureConfiguration.json")));
				System.out.println("Oliot EPCIS X - Capture (JSON) is running as developer mode (Eclipse)");
			} catch (Exception e1) {
				try {
					configuration = new JsonObject(FileUtil.readFile(
							BootstrapUtil.class.getResourceAsStream("/resources/jsonCaptureConfiguration.json")));
					System.out.println("Oliot EPCIS X - Capture (JSON) is running as user mode (Runnable Jar)");
				} catch (Exception e2) {
					System.out.println("No configration found. Terminated");
					System.exit(1);
				}
			}
		}
	}

	static void setLogger(String args[]) {
		try {
			new FileReader(configuration.getString("log4j_conf_location")).close();
			DOMConfigurator.configure(configuration.getString("log4j_conf_location"));
			logger.info("Log4j appender configured with a file located in jsonCaptureConfiguration.json");
		} catch (IOException | FactoryConfigurationError | NullPointerException e2) {
			try {
				DOMConfigurator.configure(JSONCaptureServer.class.getResource("/log4j.xml"));
				logger.info("Log4j appender configured as developer mode ('/log4j.xml')");
			} catch (FactoryConfigurationError | NullPointerException e) {
				try {
					DOMConfigurator.configure(JSONCaptureServer.class.getResource("/resources/log4j.xml"));
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

	static void setBackend(Vertx vertx) {
		try {
			// use vertx mongo for avoiding unnecessary computation regarding schema
			// validation
			mClient = MongoClient.createShared(vertx,
					configuration.getJsonObject("db_conf").put("db_name", configuration.getString("db_name")).put("useObjectId", true));
			logger.info("Backend configured");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	static void setValidator(Vertx vertx) {

		SchemaRouter router = SchemaRouter.create(vertx, new SchemaRouterOptions());
		SchemaParser parser = Draft7SchemaParser.create(router);

		try {
			JSONCaptureServer.jsonValidator = parser.parse(new JsonObject(
					FileUtil.readFile(JSONCaptureServer.class.getResourceAsStream("/schema/EPCIS-JSON-Schema.json"))));
			logger.info("Schema Validator configured as a developer mode");
		} catch (Exception e) {
			try {
				JSONCaptureServer.jsonValidator = parser.parse(new JsonObject(FileUtil.readFile(
						JSONCaptureServer.class.getResourceAsStream("/resources/schema/EPCIS-JSON-Schema.json"))));
				logger.info("Schema Validator configured as a user mode");
			} catch (Exception e1) {
				logger.info("/schema/* not found");
				try {
					JSONCaptureServer.jsonValidator = parser.parse(new JsonObject(FileUtil.readFile(JSONCaptureServer.class
							.getResourceAsStream(configuration.getString("json_schema_location")))));
					logger.info("Schema Validator configured");
				} catch (Exception e2) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		}
	}

	static void setGCPLengthList() {
		JSONCaptureServer.gcpLength = new HashMap<String, Integer>();
		JsonObject gcpPrefixFormatList = null;
		if (JSONCaptureServer.configuration.getString("gcp_source").equals("local")) {
			logger.info("load GCP Length from local");
			try {
				JsonObject gcpBase = new JsonObject(
						FileUtil.readFile(JSONCaptureServer.class.getResourceAsStream("/gcp/gcpprefixformatlist.json")));
				gcpPrefixFormatList = gcpBase.getJsonObject("GCPPrefixFormatList");
				logger.info("Access to embedded GCP Length List (could outdated): succeed");
			} catch (Exception e1) {
				logger.debug("/gcp/* not found");
				try {
					JsonObject gcpBase = new JsonObject(FileUtil.readFile(
							JSONCaptureServer.class.getResourceAsStream("/resources/gcp/gcpprefixformatlist.json")));
					gcpPrefixFormatList = gcpBase.getJsonObject("GCPPrefixFormatList");
					logger.info("Access to embedded GCP Length List (could outdated): succeed");
				} catch (Exception e2) {
					e2.printStackTrace();
					System.exit(1);
				}
			}
		} else {
			logger.info("Access to official GCP Length List");
			try {
				BufferedInputStream in = new BufferedInputStream(
						new URL("https://www.gs1.org/sites/default/files/docs/gcp_length/gcpprefixformatlist.json")
								.openStream());
				gcpPrefixFormatList = new JsonObject(new String(in.readAllBytes()))
						.getJsonObject("GCPPrefixFormatList");
				logger.info("Access to official GCP Length List: succeed");
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}

		for (Object entry : gcpPrefixFormatList.getJsonArray("entry")) {
			JsonObject entryObj = (JsonObject) entry;
			JSONCaptureServer.gcpLength.put(entryObj.getString("prefix"), entryObj.getInteger("gcpLength"));
		}

		logger.info("GCP Length all retrieved (Last update: " + gcpPrefixFormatList.getString("date") + ")");

	}

	static void setJsonLDContext() {

		try {
			JSONCaptureServer.context = new JsonObject(
					FileUtil.readFile(JSONCaptureServer.class.getResourceAsStream("/schema/epcis-context.jsonld")));
			logger.info("JsonLD context configured as a developer mode");
		} catch (Exception e) {
			try {
				JSONCaptureServer.context = new JsonObject(
						FileUtil.readFile(JSONCaptureServer.class.getResourceAsStream("/resources/schema/epcis-context.jsonld")));
				logger.info("JsonLD context configured as a user mode");
			} catch (Exception e1) {
				logger.info("/schema/* not found");
				try {
					System.out.println(configuration.getString("jsonld_context_location"));
					JSONCaptureServer.context = new JsonObject(FileUtil.readFile(configuration.getString("jsonld_context_location")));
					logger.info("JsonLD context configured");
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
			port = JSONCaptureServer.configuration.getInteger("port");
			logger.info("Oliot EPCIS X - Capture (JSON) listens to the port " + JSONCaptureServer.port);
		} catch (Exception e) {
			port = 8080;
			logger.info("Oliot EPCIS X - Capture (JSON) listens to the default port 8083");
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
		setValidator(vertx);
		// GCP Length Resolve
		setGCPLengthList();
		
		JSONCaptureServer.isSkipValidation = JSONCaptureServer.configuration.getBoolean("skip_schema_validation");
		
		
		// Set JsonLD context
		setJsonLDContext();
		// host and port set
		setHostPort();

		// Misc
		numOfVerticles = configuration.getInteger("number_of_verticles",
				Runtime.getRuntime().availableProcessors() + 1);
		logger.info("# of Vertx verticles: " + numOfVerticles);

		unitConverter = new UnitConverter();

		System.setProperty("java.net.preferIPv4Stack", "true");

		logger.info("The endpoint is at http://" + host + ":" + JSONCaptureServer.port + "/epcis/capture");
	}

}
