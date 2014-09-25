package org.oliot.epcis.configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONObject;

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
public class ConfigurationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static String backend;
	public static Logger logger;
	public static String webInfoPath;
	public static boolean isCaptureVerfificationOn;
	public static boolean isMessageQueueOn;
	public static int numCaptureListener;
	public static String MQCaptureQueue;
	public static String MQQueryExchange;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ConfigurationServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		// Log4j Setting
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO);
		ConfigurationServlet.logger = Logger.getRootLogger();
		String path = servletConfig.getServletContext().getRealPath("/WEB-INF");
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
				ConfigurationServlet.logger
						.error("Backend is null, please make sure Configuration.json is correct, and restart.");
			} else {
				ConfigurationServlet.backend = backend;
				ConfigurationServlet.logger.info("Backend - "
						+ ConfigurationServlet.backend);
			}
			ConfigurationServlet.webInfoPath = servletConfig
					.getServletContext().getRealPath("/WEB-INF");

			// Set up capture_verification
			String captureVerification = json.getString("capture_verification");
			if (captureVerification == null) {
				ConfigurationServlet.logger
						.error("capture_verification is null, please make sure Configuration.json is correct, and restart.");
			}
			captureVerification = captureVerification.trim();
			if (captureVerification.equals("on")) {
				ConfigurationServlet.isCaptureVerfificationOn = true;
				ConfigurationServlet.logger.info("Capture_Verification - ON ");
			} else if (captureVerification.equals("off")) {
				ConfigurationServlet.isCaptureVerfificationOn = false;
				ConfigurationServlet.logger.info("Capture_Verification - OFF ");
			} else {
				ConfigurationServlet.logger
						.error("capture_verification should be (on|off), please make sure Configuration.json is correct, and restart.");
			}

			// Set up Message Queue
			String message_queue = json.getString("message_queue");
			if (message_queue.equals("on")) {			
				ConfigurationServlet.isMessageQueueOn = true;
				ConfigurationServlet.logger.info("Message Queue - ON ");
			} else if( message_queue.equals("on")) {
				ConfigurationServlet.isMessageQueueOn = false;
				ConfigurationServlet.logger.info("Message Queue - OFF ");
			} else {
				ConfigurationServlet.logger
				.error("message_queue should be (on|off), please make sure Configuration.json is correct, and restart.");
			}
			String numListener = json.getString("message_queue_capture_listener");
			MQCaptureQueue = json.getString("message_queue_capture_name");
			if( isMessageQueueOn == true && ( numListener == null || MQCaptureQueue == null )){
				ConfigurationServlet.logger
				.error("if message_queue on, number of capture listener should be described, please make sure Configuration.json is correct, and restart.");
			}else{
				try
				{
					numCaptureListener = Integer.parseInt(numListener);
				}catch( NumberFormatException e )
				{
					ConfigurationServlet.logger
					.error("number of capture listener should be integer, please make sure Configuration.json is correct, and restart.");
				}
			}
			MQQueryExchange = json.getString("message_queue_exchange_query");
			if( isMessageQueueOn == true && MQQueryExchange == null )
			{
				ConfigurationServlet.logger
				.error("if message_queue on, query exchange should be described, please make sure Configuration.json is correct, and restart.");
			}

		} catch (Exception ex) {
			ConfigurationServlet.logger.error(ex.toString());
		}
	}
}
