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
import org.oliot.epcis.service.query.mongodb.MongoSubscription;
import org.oliot.epcis.service.query.mysql.MySQLBackendCheck;
import org.oliot.epcis.service.query.mysql.MysqlSubscription;

/**
 * Copyright (C) 2014 Jaewook Jack Byun
 *
 * This project is part of Oliot (oliot.org), pursuing the implementation of
 * Electronic Product Code Information Service(EPCIS) v1.1 specification in
 * EPCglobal.
 * [http://www.gs1.org/gsmp/kc/epcglobal/epcis/epcis_1_1-standard-20140520.pdf]
 * 
 *
 * @author Jaewook Jack Byun, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         bjw0829@kaist.ac.kr, bjw0829@gmail.com
 */

public class Configuration implements ServletContextListener {

	public static String backend;
	public static Logger logger;
	public static String webInfoPath;
	public static String wsdlPath;
	public static boolean isCaptureVerfificationOn;
	public static boolean isServiceRegistryReportOn;
	public static String onsAddress;
	public static boolean isQueryAccessControlOn;
	public static String facebookAppID;

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {

	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {

		// Set Logger
		setLogger();

		// Set Basic Configuration with Configuration.json
		setBasicConfiguration(servletContextEvent.getServletContext());

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
				if (Configuration.backend.equals("MariaDB")){
					Configuration.backend="MySQL";
				}
				if (Configuration.backend.equals("MySQL")){
					MySQLBackendCheck check=new MySQLBackendCheck();
					check.createDatabaseIfNotExist();
				}
			}
			Configuration.webInfoPath = context.getRealPath("/WEB-INF");
			Configuration.wsdlPath = context.getRealPath("/wsdl");			
			
			// Set up capture_verification
			String captureVerification = json.getString("capture_verification");
			if (captureVerification == null) {
				Configuration.logger.error(
						"capture_verification is null, please make sure Configuration.json is correct, and restart.");
			}
			captureVerification = captureVerification.trim();
			if (captureVerification.equals("on")) {
				Configuration.isCaptureVerfificationOn = true;
				Configuration.logger.info("Capture_Verification - ON ");
			} else if (captureVerification.equals("off")) {
				Configuration.isCaptureVerfificationOn = false;
				Configuration.logger.info("Capture_Verification - OFF ");
			} else {
				Configuration.logger.error(
						"capture_verification should be (on|off), please make sure Configuration.json is correct, and restart.");
			}

			// Set up service_registry_report
			String serviceRegistryReport = json.getString("service_registry_report");
			if (serviceRegistryReport == null) {
				Configuration.logger.error(
						"service_registry_report is null, please make sure Configuration.json is correct, and restart.");
			}
			serviceRegistryReport = serviceRegistryReport.trim();
			if (serviceRegistryReport.equals("on")) {
				Configuration.isServiceRegistryReportOn = true;
				Configuration.logger.info("Service_Registry_Report - ON");

			} else if (serviceRegistryReport.equals("off")) {
				Configuration.isServiceRegistryReportOn = false;
				Configuration.logger.info("Service_Registry_Report - OFF");
			} else {
				Configuration.logger.error(
						"service_registry_report should be (on|off), please make sure Configuration.json is correct, and restart.");
			}

			// Set up ons_address
			String ons_address = json.getString("ons_address");
			if (ons_address == null) {
				Configuration.logger
						.error("ons_address is null, please make sure Configuration.json is correct, and restart.");
			} else {
				Configuration.onsAddress = ons_address;
			}

			// Query Access Control
			// Set up capture_verification
			String queryAC = json.getString("query_access_control");
			if (queryAC == null) {
				Configuration.logger.error(
						"query_access_control, please make sure Configuration.json is correct, and restart.");
			}
			queryAC = queryAC.trim();
			if (queryAC.equals("on")) {
				Configuration.isQueryAccessControlOn = true;
				Configuration.logger.info("Query_AccessControl - ON ");
			} else if (captureVerification.equals("off")) {
				Configuration.isQueryAccessControlOn = false;
				Configuration.logger.info("Query_AccessControl - OFF ");
			} else {
				Configuration.logger.error(
						"query_access_control should be (on|off), please make sure Configuration.json is correct, and restart.");
			}
			
			// Facebook Application ID
			String fai = json.getString("facebook_app_id");
			if (fai == null) {
				Configuration.logger.error(
						"facebook_app_id, please make sure Configuration.json is correct, and restart.");
			}
			facebookAppID = fai.trim();

		} catch (Exception ex) {
			Configuration.logger.error(ex.toString());
		}
	}

	private void loadExistingSubscription() {
		if (Configuration.backend.equals("MongoDB")) {
			MongoSubscription ms = new MongoSubscription();
			ms.init();
		} else if (Configuration.backend.equals("Cassandra")) {

		} else if (Configuration.backend.equals("MySQL")) {
			MysqlSubscription ms = new MysqlSubscription();
			ms.init();
		}
	}
}
