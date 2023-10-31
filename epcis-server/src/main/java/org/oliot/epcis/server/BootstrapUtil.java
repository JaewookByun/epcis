package org.oliot.epcis.server;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.json.schema.Draft;
import io.vertx.json.schema.JsonSchemaOptions;
import io.vertx.json.schema.OutputFormat;
import io.vertx.rxjava.json.schema.JsonSchema;
import io.vertx.rxjava.json.schema.Validator;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.bson.Document;
import org.oliot.epcis.common.Metadata;
import org.oliot.epcis.converter.unit.UnitConverter;
import org.oliot.epcis.model.ActionType;
import org.oliot.epcis.model.cbv.BusinessStep;
import org.oliot.epcis.model.cbv.BusinessTransactionType;
import org.oliot.epcis.model.cbv.Disposition;
import org.oliot.epcis.model.cbv.EPCISEventType;
import org.oliot.epcis.model.cbv.EPCISVocabularyType;
import org.oliot.epcis.model.cbv.ErrorReason;
import org.oliot.epcis.model.cbv.FAO3AlphaCode;
import org.oliot.epcis.model.cbv.Measurement;
import org.oliot.epcis.model.cbv.SourceDestinationType;
import org.oliot.epcis.model.cbv.UnitOfMeasure;
import org.oliot.epcis.model.cbv.UnloadingPort;
import org.oliot.epcis.query.SubscriptionManager;
import org.oliot.epcis.resource.StaticResource;
import org.oliot.epcis.util.FileUtil;

import com.mongodb.client.MongoClients;
import com.mongodb.client.model.CreateCollectionOptions;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.validation.SchemaFactory;

import java.io.*;
import java.net.Inet4Address;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import static org.oliot.epcis.server.EPCISServer.*;

/**
 * Copyright (C) 2020-2023. (Jaewook Byun) all rights reserved.
 * <p>
 * This project is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * Among various modules, epcis-capture-xml acts as a server to receive
 * XML-formatted EPCIS documents to capture events in the documents into an
 * EPCIS repository.
 * <p>
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr, Associate Director, Auto-ID Labs, Korea,
 *         bjw0829@gmail.com
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
				System.out.println("args[0] has a syntax problem. try internal configuration.json");
			try {
				configuration = new JsonObject(
						FileUtil.readFile(BootstrapUtil.class.getResourceAsStream("/configuration.json")));
				System.out.println(Metadata.GS1_Vendor_Version + " (epcis-server) is running as developer mode (IDE)");
			} catch (Exception e1) {
				try {
					configuration = new JsonObject(FileUtil
							.readFile(BootstrapUtil.class.getResourceAsStream("/resources/configuration.json")));
					System.out.println(
							Metadata.GS1_Vendor_Version + " (epcis-server) is running as user mode (Runnable Jar)");
				} catch (Exception e2) {
					System.out.println("No configuration found. System Terminated");
					System.exit(1);
				}
			}
		}
	}

	private static void setLogger() {
		Logger.getRootLogger().setLevel(Level.OFF);
		try {
			new FileReader(configuration.getString("log4j_conf_location")).close();
			DOMConfigurator.configure(configuration.getString("log4j_conf_location"));
			logger.info("Log4j appender configured with a file located in xmlCaptureConfiguration.json");
		} catch (IOException | FactoryConfigurationError | NullPointerException e2) {
			try {
				DOMConfigurator.configure(EPCISServer.class.getResource("/log4j.xml"));
				logger.info("Log4j appender configured as developer mode ('/log4j.xml')");
			} catch (FactoryConfigurationError | NullPointerException e) {
				try {
					DOMConfigurator.configure(EPCISServer.class.getResource("/resources/log4j.xml"));
					logger.info("Log4j appender configured as user mode ('/resources/log4j.xml')");
				} catch (FactoryConfigurationError | NullPointerException e1) {
					e1.printStackTrace();
					System.out.println("No logger configuration found. System Terminated");
					System.exit(1);
				}
			}
		}
		logger.info("Logger ( " + logger.getName() + " ) - level: " + logger.getLevel());
	}

	private static void setBackend() {
		try {
			// use native mongodb reactive streams driver

			mClient = MongoClients.create(configuration.getString("db_connection_string"));
			mDatabase = mClient.getDatabase(configuration.getString("db_name"));
			mVocCollection = mDatabase.getCollection("MasterData", Document.class);
			mEventCollection = mDatabase.getCollection("EventData", Document.class);
			mSubscriptionCollection = mDatabase.getCollection("Subscription", Document.class);

			// set cappend collection for Capture Job (Tx)
			int maxCaptureJobSize = configuration.getInteger("max_capture_job_size");
			mTxCollection = mDatabase.getCollection("Tx", Document.class);
			mTxCollection.drop();
			mDatabase.createCollection("Tx", new CreateCollectionOptions().capped(true)
					.sizeInBytes(500 * maxCaptureJobSize).maxDocuments(maxCaptureJobSize));
			mTxCollection = mDatabase.getCollection("Tx", Document.class);

			Timer timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					logger.info("try to connect the backend...");
				}
			}, 0, 1000);

			long eCount = mEventCollection.countDocuments();
			long vCount = mVocCollection.countDocuments();
			// ping for checking connection
			logger.info("Backend configured: (# event: " + eCount + ", # vocabularies: " + vCount + ")");
			timer.cancel();

			monitoringClient = MongoClients.create(configuration.getString("db_connection_string"));
			monitoringDatabase = monitoringClient.getDatabase(configuration.getString("db_name"));
			monitoringVocCollection = monitoringDatabase.getCollection("MasterData", Document.class);
			monitoringEventCollection = monitoringDatabase.getCollection("EventData", Document.class);
			monitoringTxCollection = monitoringDatabase.getCollection("Tx", Document.class);
			monitoringSubscriptionCollection = monitoringDatabase.getCollection("Subscription", Document.class);
		} catch (Throwable e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private static void setXMLValidator() {
		try {
			EPCISServer.xmlValidator = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema")
					.newSchema(EPCISServer.class.getResource("/schema/epcglobal-epcis-2_0.xsd")).newValidator();
			logger.info("Schema Validator (XML) configured as a developer mode");
		} catch (Exception e) {
			try {
				EPCISServer.xmlValidator = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema")
						.newSchema(EPCISServer.class.getResource("/resources/schema/epcglobal-epcis-2_0.xsd"))
						.newValidator();
				logger.info("Schema Validator (XML) configured as a user mode");
			} catch (Exception e1) {
				logger.info("/schema/* not found");
				try {
					EPCISServer.xmlValidator = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema")
							.newSchema(new File(configuration.getString("xml_schema_location"))).newValidator();
					EPCISServer.xmlValidator = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema")
							.newSchema(new File(configuration.getString("xml_master_data_schema_location")))
							.newValidator();
					logger.info("Schema Validator (XML) configured");
				} catch (Exception e2) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		}
	}

	private static void setJSONValidator(Vertx vertx) {
		JsonObject schemaObj = null;
		try {
			schemaObj = new JsonObject(
					FileUtil.readFile(EPCISServer.class.getResourceAsStream("/schema/epcis-json-schema.json")));
			logger.info("Schema Validator (XML) configured as a developer mode");
		} catch (Exception e) {
			try {
				schemaObj = new JsonObject(FileUtil
						.readFile(EPCISServer.class.getResourceAsStream("/resources/schema/epcis-json-schema.json")));
				logger.info("Schema Validator (XML) configured as a user mode");
			} catch (Exception e1) {
				logger.info("/schema/* not found");
				try {
					schemaObj = new JsonObject(FileUtil.readFile(
							EPCISServer.class.getResourceAsStream(configuration.getString("json_schema_location"))));
					logger.info("Schema Validator (XML) configured");
				} catch (Exception e2) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		}

		JsonSchema sc = JsonSchema.of(schemaObj);
		JsonSchemaOptions opt = new JsonSchemaOptions().setBaseUri("http://http://dfpl.sejong.ac.kr/")
				.setDraft(Draft.DRAFT7).setOutputFormat(OutputFormat.Basic);

		EPCISServer.jsonValidator = Validator.create(sc, opt);
	}

	static void setHostPort() {
		try {
			host = Inet4Address.getLocalHost().getHostAddress();
		} catch (Exception e) {

		}
		try {
			port = EPCISServer.configuration.getInteger("port");
			logger.info(Metadata.GS1_Vendor_Version + " (epcis-server) listens to the port " + EPCISServer.port);
		} catch (Exception e) {
			port = 8080;
			logger.info(Metadata.GS1_Vendor_Version + " (epcis-server) listens to the default port 8080");
		}
	}

	static void setCaptureServiceMetadata() {
		if (configuration.containsKey("GS1-EPCIS-Capture-Limit")) {
			Metadata.GS1_CAPTURE_limit = configuration.getInteger("GS1-EPCIS-Capture-Limit", 500);
		}

		if (configuration.containsKey("GS1-EPCIS-Capture-File-Size-Limit")) {
			Metadata.GS1_CAPTURE_file_size_limit = configuration.getInteger("GS1-EPCIS-Capture-File-Size-Limit", 1024);
		}

		if (configuration.containsKey("GS1-EPCIS-Capture-Error-Behaviour")) {
			String behaviour = configuration.getString("GS1-EPCIS-Capture-Error-Behaviour");
			if (behaviour.equals("proceed"))
				Metadata.GS1_EPCIS_Capture_Error_Behaviour = "proceed";
		}

		if (configuration.containsKey("GS1-Next-Page-Token-Expires")) {
			String expiresStr = configuration.getString("GS1-Next-Page-Token-Expires");
			try {
				Metadata.GS1_Next_Page_Token_Expires = Long.parseLong(expiresStr);
			} catch (NumberFormatException e) {

			}
		}
	}

	static void setVocabularies() {
		// Unit Of Measure
		StaticResource.unitOfMeasure = new HashSet<String>();
		for (UnitOfMeasure uom : UnitOfMeasure.values()) {
			StaticResource.unitOfMeasure.add(uom.name());
		}
		// eventType
		StaticResource.eventTypes = new HashSet<String>();
		for (EPCISEventType value : EPCISEventType.values()) {
			StaticResource.eventTypes.add(value.name());
		}
		// action
		StaticResource.actions = new HashSet<String>();
		for (ActionType value : ActionType.values()) {
			StaticResource.actions.add(value.name());
		}
		// bizStep
		StaticResource.bizSteps = new HashSet<String>();
		for (BusinessStep bs : BusinessStep.values()) {
			StaticResource.bizSteps.add(bs.getBusinessStep());
		}
		// disposition
		StaticResource.dispositions = new HashSet<String>();
		for (Disposition d : Disposition.values()) {
			StaticResource.dispositions.add(d.getDisposition());
		}
		// bizTransactionType
		StaticResource.bizTransactionTypes = new HashSet<String>();
		for (BusinessTransactionType d : BusinessTransactionType.values()) {
			StaticResource.bizTransactionTypes.add(d.getBusinessTransactionType());
		}
		// source destination type
		StaticResource.sourceDestinationTypes = new HashSet<String>();
		for (SourceDestinationType d : SourceDestinationType.values()) {
			StaticResource.sourceDestinationTypes.add(d.getSourceDestinationType());
		}
		// error reason
		StaticResource.errorReasons = new HashSet<String>();
		for (ErrorReason d : ErrorReason.values()) {
			StaticResource.errorReasons.add(d.getErrorReason());
		}
		// measurement type
		StaticResource.measurements = new HashSet<String>();
		for (Measurement m : Measurement.values()) {
			StaticResource.measurements.add("gs1:" + m.name());
		}
		// vocabulary Types
		StaticResource.vocabularyTypes = new HashSet<String>();
		for (EPCISVocabularyType m : EPCISVocabularyType.values()) {
			StaticResource.vocabularyTypes.add(m.getVocabularyType());
		}

		// fao3alpha code
		FAO3AlphaCode.initialize();
		// unloaindg port
		UnloadingPort.initialize();
	}

	static void setGCPLengthList() {
		StaticResource.gcpLength = new HashMap<String, Integer>();
		JsonObject gcpPrefixFormatList = null;
		if (configuration.getString("gcp_source").equals("local")) {
			logger.info("load GCP Length from local");
			try {
				JsonObject gcpBase = new JsonObject(
						FileUtil.readFile(EPCISServer.class.getResourceAsStream("/gcp/gcpprefixformatlist.json")));
				gcpPrefixFormatList = gcpBase.getJsonObject("GCPPrefixFormatList");
				logger.info("Access to embedded GCP Length List (could outdated): succeed");
			} catch (Exception e1) {
				logger.debug("/gcp/* not found");
				try {
					JsonObject gcpBase = new JsonObject(FileUtil.readFile(
							EPCISServer.class.getResourceAsStream("/resources/gcp/gcpprefixformatlist.json")));
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
						new URI("https://www.gs1.org/sites/default/files/docs/gcp_length/gcpprefixformatlist.json")
								.toURL().openStream());
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
			StaticResource.gcpLength.put(entryObj.getString("prefix"), entryObj.getInteger("gcpLength"));
		}

		logger.info("GCP Length all retrieved (Last update: " + gcpPrefixFormatList.getString("date") + ")");

	}

	static void loadSubscriptions() {
		SubscriptionManager sub = new SubscriptionManager();
		sub.init();
	}

	public static void configureServer(Vertx vertx, String[] args, SubscriptionManager sm) {
		// Set Configuration (JSON)
		setConfigurationJson(args);
		// Set Logger
		setLogger();
		// Set Backend
		setBackend();
		// Set validator
		setXMLValidator();
		setJSONValidator(vertx);
		// host and port set
		setHostPort();
		// vocabulary
		setVocabularies();
		// unit converter
		EPCISServer.unitConverter = new UnitConverter();
		// gcp list
		setGCPLengthList();
		// capture service specific options
		setCaptureServiceMetadata();
		// load subscriptions
		loadSubscriptions();

		// Misc
		numOfVerticles = configuration.getInteger("number_of_verticles",
				Runtime.getRuntime().availableProcessors() + 1);
		logger.info("# of Vertx verticles: " + numOfVerticles);

		System.setProperty("java.net.preferIPv4Stack", "true");

		logger.info("The endpoint is at http://" + host + ":" + EPCISServer.port + "/epcis");
	}

}
