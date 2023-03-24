package org.oliot.epcis.query;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.apache.log4j.Level;
import org.apache.log4j.xml.DOMConfigurator;
import org.bson.Document;
import org.oliot.epcis.model.exception.ImplementationException;
import org.oliot.epcis.unit_converter.UnitConverter;
import org.oliot.epcis.util.ObservableSubscriber;

import com.mongodb.client.result.InsertOneResult;
import com.mongodb.reactivestreams.client.FindPublisher;
import com.mongodb.reactivestreams.client.MongoClients;

import javax.xml.parsers.FactoryConfigurationError;
import java.io.*;
import java.net.Inet4Address;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static org.oliot.epcis.query.RESTQueryServer.*;

/**
 * Copyright (C) 2020-2021. (Jaewook Byun) all rights reserved.
 * <p>
 * Oliot EPCIS X is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * Among various modules, epcis-query-rest acts as a server to receive queries
 * to provide filtered, sorted, limited events of interest inside EPCIS
 * repository.
 * <p>
 * 
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr
 *         <p>
 *         Associate Director, Auto-ID Labs, KAIST, bjw0829@kaist.ac.kr
 */
public class BootstrapUtil {

	private static String readFile(InputStream is) throws IOException {
		return new String(is.readAllBytes());
	}

	private static String readFile(String loc) throws IOException {
		return Files.readString(Paths.get(loc));
	}

	private static void setConfigurationJson(String[] args) {
		// try main argument[0]
		try {
			configuration = new JsonObject(readFile(args[0]));
			System.out.println("use configuration.json in args[0]");
		} catch (Exception e) {
			if (e instanceof IOException)
				System.out.println("args[0] not found. try internal configuration.json");
			else
				System.out.println("args[0] has a syntax problem. try internal restQueryConfiguration.json");
			try {
				configuration = new JsonObject(
						readFile(BootstrapUtil.class.getResourceAsStream("/restQueryConfiguration.json")));
				System.out.println("Oliot EPCIS X - Query (REST) is running as developer mode (Eclipse)");
			} catch (Exception e1) {
				try {
					configuration = new JsonObject(readFile(
							BootstrapUtil.class.getResourceAsStream("/resources/restQueryConfiguration.json")));
					System.out.println("Oliot EPCIS X - Query (REST) is running as user mode (Runnable Jar)");
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
			System.out.println("found");
			DOMConfigurator.configure(configuration.getString("log4j_conf_location"));
			logger.info("Log4j appender configured with a file located in restQueryConfiguration.json");
		} catch (IOException | FactoryConfigurationError | NullPointerException e2) {
			try {
				DOMConfigurator.configure(RESTQueryServer.class.getResource("/log4j.xml"));
				System.out.println("Log4j appender configured as developer mode ('/log4j.xml')");
				logger.info("Log4j appender configured as developer mode ('/log4j.xml')");
			} catch (FactoryConfigurationError | NullPointerException e) {
				try {
					DOMConfigurator.configure(RESTQueryServer.class.getResource("/resources/log4j.xml"));
					System.out.println("Log4j appender configured as user mode ('/resources/log4j.xml')");
					logger.info("Log4j appender configured as user mode ('/resources/log4j.xml')");
				} catch (FactoryConfigurationError | NullPointerException e1) {
					System.out.println("Log4j configured as user mode ('/resources/log4j.xml')");
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

			mEventTypes =  mDatabase.getCollection("eventTypes", Document.class);
			mEPCs =  mDatabase.getCollection("epcs", Document.class);
			mBizSteps =  mDatabase.getCollection("bizSteps", Document.class);
			mDispositions =  mDatabase.getCollection("dispositions", Document.class);
			mReadPoints =  mDatabase.getCollection("readPoints", Document.class);
			mBizLocations =  mDatabase.getCollection("bizLocations", Document.class);
		
			logger.info("Backend configured");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void scanResources() {
		
		logger.debug("Resource scan starts...");
		Document project = new Document();
		project.put("parentID", true);
		project.put("epcList", true);
		project.put("inputEPCList", true);
		project.put("outputEPCList", true);
		project.put("quantityList", true);
		project.put("inputQuantityList", true);
		project.put("outputQuantityList", true);
		project.put("bizStep", true);
		project.put("disposition", true);
		project.put("readPoint", true);
		project.put("bizLocation", true);
		project.put("isA", true);
		project.put("_id", false);
	
		
		FindPublisher<org.bson.Document> query = RESTQueryServer.mEventCollection.find().projection(project);
		ObservableSubscriber<org.bson.Document> collector = new ObservableSubscriber<org.bson.Document>();
		query.subscribe(collector);
		try {
			collector.await();
		} catch (Throwable e1) {
			ImplementationException e = new ImplementationException();
			e.setReason(e1.getMessage());
			e.setStackTrace(new StackTraceElement[0]);
			System.exit(1);
		}
		HashSet<String> epcs = new HashSet<String>();
		HashSet<String> eventTypes= new HashSet<String>();
		HashSet<String> bizSteps = new HashSet<String>();
		HashSet<String> dispositions = new HashSet<String>();
		HashSet<String> readPoints = new HashSet<String>();
		HashSet<String> bizLocations = new HashSet<String>();
	
		List<org.bson.Document> resultList = collector.getReceived();
		resultList.parallelStream().forEach(result -> {
			if (result.containsKey("parentID"))
				epcs.add(result.getString("parentID"));
			if (result.containsKey("epcList"))
				epcs.addAll(result.getList("epcList", String.class));
			if (result.containsKey("inputEPCList"))
				epcs.addAll(result.getList("inputEPCList", String.class));
			if (result.containsKey("outputEPCList"))
				epcs.addAll(result.getList("outputEPCList", String.class));
			if (result.containsKey("quantityList")) {
				for(org.bson.Document q: result.getList("quantityList", org.bson.Document.class)) {
					epcs.add(q.getString("epcClass"));
				}
			}
			if (result.containsKey("inputQuantityList")) {
				for(org.bson.Document q: result.getList("inputQuantityList", org.bson.Document.class)) {
					epcs.add(q.getString("epcClass"));
				}
			}
			if (result.containsKey("outputQuantityList")) {
				for(org.bson.Document q: result.getList("outputQuantityList", org.bson.Document.class)) {
					epcs.add(q.getString("epcClass"));
				}
			}
			if(result.containsKey("isA"))
				eventTypes.add(result.getString("isA"));
			if(result.containsKey("bizStep"))
				bizSteps.add(result.getString("bizStep"));
			if(result.containsKey("disposition"))
				dispositions.add(result.getString("disposition"));
			if(result.containsKey("readPoint"))
				readPoints.add(result.getString("readPoint"));
			if(result.containsKey("bizLocation"))
				bizLocations.add(result.getString("bizLocation"));
		});
		logger.debug("Resources retrieved");

		ObservableSubscriber<InsertOneResult> insertSub = new ObservableSubscriber<InsertOneResult>();
		
		for(String eventType: eventTypes) {
			try {
				mEventTypes.insertOne(new Document().append("_id", eventType)).subscribe(insertSub);
				insertSub.await();
			} catch (Throwable e) {
				
			}
		}
		logger.debug("EventTypes registered");
		
		for(String epc: epcs) {
			try {
				mEPCs.insertOne(new Document().append("_id", epc)).subscribe(insertSub);
				insertSub.await();
			} catch (Throwable e) {
				
			}
		}
		logger.debug("EPCs registered");
		
		for(String bizStep: bizSteps) {
			try {
				mBizSteps.insertOne(new Document().append("_id", bizStep)).subscribe(insertSub);
				insertSub.await();
			} catch (Throwable e) {
				
			}
		}
		logger.debug("BizSteps registered");
		
		for(String disposition: dispositions) {
			try {
				mDispositions.insertOne(new Document().append("_id", disposition)).subscribe(insertSub);
				insertSub.await();
			} catch (Throwable e) {
				
			}
		}
		logger.debug("Dispositions registered");
		
		for(String readPoint: readPoints) {
			try {
				mReadPoints.insertOne(new Document().append("_id", readPoint)).subscribe(insertSub);
				insertSub.await();
			} catch (Throwable e) {
				
			}
		}
		logger.debug("ReadPoints registered");
		
		for(String bizLocation: bizLocations) {
			try {
				mBizLocations.insertOne(new Document().append("_id", bizLocation)).subscribe(insertSub);
				insertSub.await();
			} catch (Throwable e) {
				
			}
		}
		logger.debug("BizLocations registered");
	}

	private static void setGCPLengthList() {
		RESTQueryServer.gcpLength = new HashMap<String, Integer>();
		JsonObject gcpPrefixFormatList = null;
		if (RESTQueryServer.configuration.getString("gcp_source").equals("local")) {
			logger.info("load GCP Length from local");
			try {
				JsonObject gcpBase = new JsonObject(
						readFile(RESTQueryServer.class.getResourceAsStream("/gcp/gcpprefixformatlist.json")));
				gcpPrefixFormatList = gcpBase.getJsonObject("GCPPrefixFormatList");
				logger.info("Access to embedded GCP Length List (could outdated): succeed");
			} catch (Exception e1) {
				logger.debug("/gcp/* not found");
				try {
					JsonObject gcpBase = new JsonObject(readFile(
							RESTQueryServer.class.getResourceAsStream("/resources/gcp/gcpprefixformatlist.json")));
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
			RESTQueryServer.gcpLength.put(entryObj.getString("prefix"), entryObj.getInteger("gcpLength"));
		}

		logger.info("GCP Length all retrieved (Last update: " + gcpPrefixFormatList.getString("date") + ")");

	}

	static void setHostPort() {
		try {
			host = Inet4Address.getLocalHost().getHostAddress();
		} catch (Exception e) {

		}
		try {
			port = RESTQueryServer.configuration.getInteger("port");
			logger.info("Oliot EPCIS X - Query (REST) listens to the port " + RESTQueryServer.port);
		} catch (Exception e) {
			port = 8084;
			logger.info("Oliot EPCIS X - Query (REST) listens to the default port 8084");
		}
	}

	static void configureServer(Vertx vertx, String[] args) {
		// Set Configuration (JSON)
		setConfigurationJson(args);
		// Set Logger
		setLogger(args);
		// Set Backend
		setBackend(vertx);
		// Scan Resources
		scanResources();
		// host and port set
		setHostPort();
		// gcpLength
		setGCPLengthList();

		// Misc
		numOfVerticles = configuration.getInteger("number_of_verticles",
				Runtime.getRuntime().availableProcessors() + 1);
		logger.info("# of Vertx verticles: " + numOfVerticles);

		unitConverter = new UnitConverter();

		System.setProperty("java.net.preferIPv4Stack", "true");

		logger.info("The endpoint is at http://" + host + ":" + RESTQueryServer.port + "/epcis/resource/events");
	}

}
