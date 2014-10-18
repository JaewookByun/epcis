package org.oliot.epccr.rest;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Level;
import org.oliot.epccr.configuration.Configuration;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

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
public class PublishQListener implements MessageListener {

	private String destURL;
	
	public PublishQListener(String destURL)
	{
		this.destURL = destURL;
	}
	
	@Override
	public void onMessage(Message message) {
		
		try {
			URL dest = new URL(destURL);
			HttpURLConnection conn = (HttpURLConnection) dest.openConnection();
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
			conn.getResponseCode();
			conn.disconnect();
		} catch (MalformedURLException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
		} catch (IOException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
		}
	}

}
