package org.oliot.epcis.capture;

import io.vertx.core.json.JsonObject;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.bson.Document;
import org.oliot.epcis.common.Metadata;
import org.oliot.epcis.converter.pojo_to_bson.POJOtoBSONUtil;
import org.oliot.epcis.converter.unit.UnitConverter;
import org.oliot.epcis.model.cbv.BusinessStep;
import org.oliot.epcis.model.cbv.BusinessTransactionType;
import org.oliot.epcis.model.cbv.Disposition;
import org.oliot.epcis.model.cbv.EPCISVocabularyType;
import org.oliot.epcis.model.cbv.ErrorReason;
import org.oliot.epcis.model.cbv.FAO3AlphaCode;
import org.oliot.epcis.model.cbv.Measurement;
import org.oliot.epcis.model.cbv.SourceDestinationType;
import org.oliot.epcis.model.cbv.UnitOfMeasure;
import org.oliot.epcis.model.cbv.UnloadingPort;
import org.oliot.epcis.util.FileUtil;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.reactivestreams.client.MongoClients;
import org.oliot.epcis.util.ObservableSubscriber;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.net.Inet4Address;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static org.oliot.epcis.capture.XMLCaptureServer.*;

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
				System.out.println("args[0] has a syntax problem. try internal xmlCaptureConfiguration.json");
			try {
				configuration = new JsonObject(
						FileUtil.readFile(BootstrapUtil.class.getResourceAsStream("/xmlCaptureConfiguration.json")));
				System.out.println(Metadata.GS1_Vendor_Version + " is running as developer mode (IDE)");
			} catch (Exception e1) {
				try {
					configuration = new JsonObject(FileUtil.readFile(
							BootstrapUtil.class.getResourceAsStream("/resources/xmlCaptureConfiguration.json")));
					System.out.println(Metadata.GS1_Vendor_Version + " is running as user mode (Runnable Jar)");
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
				DOMConfigurator.configure(XMLCaptureServer.class.getResource("/log4j.xml"));
				logger.info("Log4j appender configured as developer mode ('/log4j.xml')");
			} catch (FactoryConfigurationError | NullPointerException e) {
				try {
					DOMConfigurator.configure(XMLCaptureServer.class.getResource("/resources/log4j.xml"));
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
			int maxCaptureJobSize = configuration.getInteger("max_capture_job_size");
			ObservableSubscriber<Void> s = new ObservableSubscriber<>();
			mDatabase.getCollection("Tx", Document.class).drop().subscribe(s);
			s.await();
			s = new ObservableSubscriber<>();
			mDatabase.createCollection("Tx", new CreateCollectionOptions().capped(true)
					.sizeInBytes(500 * maxCaptureJobSize).maxDocuments(maxCaptureJobSize)).subscribe(s);
			s.await();
			mTxCollection = mDatabase.getCollection("Tx", Document.class);

			Timer timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					logger.info("try to connect the backend...");
				}
			}, 0, 1000);

			ObservableSubscriber<Long> countSubscriber = new ObservableSubscriber<>();
			mEventCollection.countDocuments().subscribe(countSubscriber);
			countSubscriber.await(10, TimeUnit.SECONDS);
			long eCount = countSubscriber.getReceived().get(0);
			mVocCollection.countDocuments().subscribe(countSubscriber);

			countSubscriber.await();
			long vCount = countSubscriber.getReceived().get(0);
			// ping for checking connection
			logger.info("Backend configured: (# event: " + eCount + ", # vocabularies: " + vCount + ")");
			timer.cancel();
		} catch (Throwable e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private static void setValidator() {
		try {
			XMLCaptureServer.xmlValidator = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema")
					.newSchema(XMLCaptureServer.class.getResource("/schema/epcglobal-epcis-2_0.xsd")).newValidator();
			logger.info("Schema Validator configured as a developer mode");
		} catch (Exception e) {
			try {
				XMLCaptureServer.xmlValidator = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema")
						.newSchema(XMLCaptureServer.class.getResource("/resources/schema/epcglobal-epcis-2_0.xsd"))
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
			logger.info(Metadata.GS1_Vendor_Version + " listens to the port " + XMLCaptureServer.port);
		} catch (Exception e) {
			port = 8080;
			logger.info(Metadata.GS1_Vendor_Version + " listens to the default port 8080");
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
		POJOtoBSONUtil.unitOfMeasure = new HashSet<String>();
		for (UnitOfMeasure uom : UnitOfMeasure.values()) {
			POJOtoBSONUtil.unitOfMeasure.add(uom.name());
		}
		// bizStep
		POJOtoBSONUtil.bizStep = new HashSet<String>();
		for (BusinessStep bs : BusinessStep.values()) {
			POJOtoBSONUtil.bizStep.add(bs.getBusinessStep());
		}
		// disposition
		POJOtoBSONUtil.disposition = new HashSet<String>();
		for (Disposition d : Disposition.values()) {
			POJOtoBSONUtil.disposition.add(d.getDisposition());
		}
		// bizTransactionType
		POJOtoBSONUtil.btt = new HashSet<String>();
		for (BusinessTransactionType d : BusinessTransactionType.values()) {
			POJOtoBSONUtil.btt.add(d.getBusinessTransactionType());
		}
		// source destination type
		POJOtoBSONUtil.sdt = new HashSet<String>();
		for (SourceDestinationType d : SourceDestinationType.values()) {
			POJOtoBSONUtil.sdt.add(d.getSourceDestinationType());
		}
		// error reason
		POJOtoBSONUtil.er = new HashSet<String>();
		for (ErrorReason d : ErrorReason.values()) {
			POJOtoBSONUtil.er.add(d.getErrorReason());
		}
		// measurement type
		POJOtoBSONUtil.measurement = new HashSet<String>();
		for (Measurement m : Measurement.values()) {
			POJOtoBSONUtil.measurement.add("gs1:" + m.name());
		}
		// fao3alpha code
		FAO3AlphaCode.initialize();
		// unloaindg port
		UnloadingPort.initialize();

		// vocabulary type
		POJOtoBSONUtil.vocabularyType = new HashSet<String>();
		for (EPCISVocabularyType m : EPCISVocabularyType.values()) {
			POJOtoBSONUtil.vocabularyType.add(m.getVocabularyType());
		}

		POJOtoBSONUtil.unitConverter = new UnitConverter();
	}

	static void setGCPLengthList() {
		POJOtoBSONUtil.gcpLength = new HashMap<String, Integer>();
		JsonObject gcpPrefixFormatList = null;
		if (configuration.getString("gcp_source").equals("local")) {
			logger.info("load GCP Length from local");
			try {
				JsonObject gcpBase = new JsonObject(
						FileUtil.readFile(XMLCaptureServer.class.getResourceAsStream("/gcp/gcpprefixformatlist.json")));
				gcpPrefixFormatList = gcpBase.getJsonObject("GCPPrefixFormatList");
				logger.info("Access to embedded GCP Length List (could outdated): succeed");
			} catch (Exception e1) {
				logger.debug("/gcp/* not found");
				try {
					JsonObject gcpBase = new JsonObject(FileUtil.readFile(
							XMLCaptureServer.class.getResourceAsStream("/resources/gcp/gcpprefixformatlist.json")));
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
			POJOtoBSONUtil.gcpLength.put(entryObj.getString("prefix"), entryObj.getInteger("gcpLength"));
		}

		logger.info("GCP Length all retrieved (Last update: " + gcpPrefixFormatList.getString("date") + ")");

	}

	static void configureServer(String[] args) {
		// Set Configuration (JSON)
		setConfigurationJson(args);
		// Set Logger
		setLogger();
		// Set Backend
		setBackend();
		// Set validator
		setValidator();
		// host and port set
		setHostPort();
		// vocabulary
		setVocabularies();
		// gcp list
		setGCPLengthList();
		// capture service specific options
		setCaptureServiceMetadata();

		// Misc
		numOfVerticles = configuration.getInteger("number_of_verticles",
				Runtime.getRuntime().availableProcessors() + 1);
		logger.info("# of Vertx verticles: " + numOfVerticles);

		System.setProperty("java.net.preferIPv4Stack", "true");

		logger.info("The endpoint is at http://" + host + ":" + XMLCaptureServer.port + "/epcis/capture");
	}

}
