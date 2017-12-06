package org.oliot.epcis.service.subscription;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.oliot.epcis.configuration.Configuration;

/**
 * Copyright (C) 2014-2016 Jaewook Byun
 *
 * This project is part of Oliot open source (http://oliot.org). Oliot EPCIS
 * v1.2.x is Java Web Service complying with Electronic Product Code Information
 * Service (EPCIS) v1.2.
 *
 * @author Jaewook Byun, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         bjw0829@kaist.ac.kr, bjw0829@gmail.com
 */

/**
 * Servlet implementation class SubscriptionTestServlet
 */
public class SubscriptionTestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SubscriptionTestServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Configuration.logger.log(Level.WARN, "SubscriptionServlet.doPost do nothing");

		// Test DoPost for the callback result
		InputStream is = request.getInputStream();
		StringWriter writer = new StringWriter();
		IOUtils.copy(is, writer, "UTF-8");
		String xmlString = writer.toString();
		System.out.println(xmlString);
	}

}
