package org.oliot.epcis.service.query;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.oliot.epcis.configuration.ConfigurationServlet;
import org.oliot.epcis.service.query.mongodb.restlike.MongoSubscription;

/**
 * Servlet implementation class SubscriptionServlet
 */
public class SubscriptionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SubscriptionServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		ConfigurationServlet.logger.log(Level.WARN,
				"SubscriptionServlet.doGet do nothing");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		ConfigurationServlet.logger.log(Level.WARN,
				"SubscriptionServlet.doPost do nothing");

		// Test DoPost for the callback result
		InputStream is = request.getInputStream();
		StringWriter writer = new StringWriter();
		IOUtils.copy(is, writer, "UTF-8");
		String xmlString = writer.toString();
		System.out.println(xmlString);
	}

	@Override
	public void init() {
		if (ConfigurationServlet.backend.equals("MongoDB")) {
			MongoSubscription ms = new MongoSubscription();
			ms.init();
		} else if (ConfigurationServlet.backend.equals("Cassandra")) {

		} else if (ConfigurationServlet.backend.equals("MySQL")) {

		}

	}
}
