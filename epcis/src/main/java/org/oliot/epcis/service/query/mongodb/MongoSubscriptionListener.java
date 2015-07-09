package org.oliot.epcis.service.query.mongodb;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Level;
import org.oliot.epcis.configuration.Configuration;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

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

public class MongoSubscriptionListener implements MessageListener {

	private URL destURL;

	public MongoSubscriptionListener(String destStr) {
		try {
			destURL = new URL(destStr);
		} catch (MalformedURLException e) {

		}
	}

	@Override
	public void onMessage(Message message) {

		try {
			HttpURLConnection conn = (HttpURLConnection) destURL
					.openConnection();
			byte[] bytes = message.getBody();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestProperty("Content-Length",
					"" + Integer.toString(bytes.length));
			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
			String sendString = new String(bytes, "UTF-8");
			wr.writeBytes(sendString);
			wr.flush();
			wr.close();
			int x = conn.getResponseCode();
			System.out.println(x);
			conn.disconnect();
		} catch (MalformedURLException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
		} catch (IOException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
		}
	}

}
