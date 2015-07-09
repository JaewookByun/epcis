package org.oliot.epcis.service.query.mysql;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Level;
import org.oliot.epcis.configuration.Configuration;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

public class MysqlSubscriptionListener implements MessageListener {

	private URL destURL;

	public MysqlSubscriptionListener(String destStr) {
		try {
			destURL = new URL(destStr);
		} catch (MalformedURLException e) {

		}
	}

	@Override
	public void onMessage(Message message) {

		try {
			HttpURLConnection conn = (HttpURLConnection) destURL.openConnection();
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
