package org.oliot.epcis.service.query;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.oliot.epcis.configuration.ConfigurationServlet;
import org.oliot.model.epcis.SubscriptionType;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;

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
public class SubscriptionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	static SchedulerFactory schedFact;

	static Scheduler sched;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SubscriptionServlet() {
		super();
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

	@SuppressWarnings("resource")
	@Override
	public void init() {

		try {
			schedFact = new org.quartz.impl.StdSchedulerFactory();
			sched = schedFact.getScheduler();
			if (sched.isStarted() == false)
				sched.start();

			ApplicationContext ctx = new GenericXmlApplicationContext(
					"classpath:MongoConfig.xml");
			MongoOperations mongoOperation = (MongoOperations) ctx
					.getBean("mongoTemplate");

			List<SubscriptionType> allSubscription = mongoOperation
					.findAll(SubscriptionType.class);
			QueryService queryService = new QueryService();
			ConfigurationServlet.logger.log(Level.INFO,
					"Loading pre-existing subscription");
			for (int i = 0; i < allSubscription.size(); i++) {
				SubscriptionType subscription = allSubscription.get(i);
				queryService.addScheduleToQuartz(subscription);
			}
		} catch (SchedulerException e) {
			ConfigurationServlet.logger.log(Level.ERROR, e.toString());
		}

	}
}
