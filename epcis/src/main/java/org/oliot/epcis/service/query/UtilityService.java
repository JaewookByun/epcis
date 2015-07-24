package org.oliot.epcis.service.query;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Level;
import org.oliot.epcis.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.ServletContextAware;

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

@Controller
public class UtilityService implements ServletContextAware {

	@Autowired
	ServletContext servletContext;

	@SuppressWarnings("unused")
	@Autowired
	private HttpServletRequest request;

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;

	}

	/**
	 * Removes a previously registered subscription having the specified
	 * subscriptionID.
	 */
	@RequestMapping(value = "/ResetDB", method = RequestMethod.GET)
	public String resetDB() {

		if (Configuration.backend.equals("MongoDB")) {
			
			ApplicationContext ctx = new GenericXmlApplicationContext(
					"classpath:MongoConfig.xml");
			MongoOperations mongoOperation = (MongoOperations) ctx
					.getBean("mongoTemplate");
			
			mongoOperation.dropCollection("AggregationEvent");
			mongoOperation.dropCollection("ObjectEvent");
			mongoOperation.dropCollection("TransactionEvent");
			mongoOperation.dropCollection("TransformationEvent");
			mongoOperation.dropCollection("QuantityEvent");
			
			Configuration.logger.log(Level.INFO, " Repository Initialized ");
			
			((AbstractApplicationContext) ctx).close();
			
			return "Repository Initialized";
		} else if (Configuration.backend.equals("Cassandra")) {

		} else if (Configuration.backend.equals("MySQL")) {

		}
		return null;
	}
}
