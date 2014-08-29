/**
 * Copyright (C) 2014 KAIST RESL 
 *
 * This file is part of Oliot (oliot.org).

 * @author Jack Jaewook Byun, Ph.D student
 * Korea Advanced Institute of Science and Technology
 * Real-time Embedded System Laboratory(RESL)
 * bjw0829@kaist.ac.kr
 */

package org.oliot.epcis.service.capture;

import org.oliot.epcis.model.AggregationEventType;
import org.oliot.epcis.model.ObjectEventType;
import org.oliot.epcis.model.QuantityEventType;
import org.oliot.epcis.model.TransactionEventType;
import org.oliot.epcis.model.TransformationEventType;
import org.oliot.epcis.service.ConfigurationServlet;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;


public class CaptureService implements CoreCaptureService{
	
	@SuppressWarnings({ "resource" })
	@Override
	public void capture(AggregationEventType event) {
		if(ConfigurationServlet.backend.equals("MongoDB"))
		{
			ApplicationContext ctx = new GenericXmlApplicationContext("classpath:MongoConfig.xml");
			MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");
			mongoOperation.save(event);
			ConfigurationServlet.logger.info(" Event Saved ");
		}
	}

	@SuppressWarnings("resource")
	@Override
	public void capture(ObjectEventType event){
		if(ConfigurationServlet.backend.equals("MongoDB"))
		{
			ApplicationContext ctx = new GenericXmlApplicationContext("classpath:MongoConfig.xml");
			MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");
			mongoOperation.save(event);
			ConfigurationServlet.logger.info(" Event Saved ");
		}
	}

	@SuppressWarnings({ "resource" })
	@Override
	public void capture(QuantityEventType event) {
		if(ConfigurationServlet.backend.equals("MongoDB"))
		{
			ApplicationContext ctx = new GenericXmlApplicationContext("classpath:MongoConfig.xml");
			MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");
			mongoOperation.save(event);
			ConfigurationServlet.logger.info(" Event Saved ");
		}
	}

	@SuppressWarnings({ "resource" })
	@Override
	public void capture(TransactionEventType event) {
		if(ConfigurationServlet.backend.equals("MongoDB"))
		{
			ApplicationContext ctx = new GenericXmlApplicationContext("classpath:MongoConfig.xml");
			MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");
			mongoOperation.save(event);
			ConfigurationServlet.logger.info(" Event Saved ");
		}
	}


	@SuppressWarnings({ "resource" })
	@Override
	public void capture(TransformationEventType event) {
		if(ConfigurationServlet.backend.equals("MongoDB"))
		{
			ApplicationContext ctx = new GenericXmlApplicationContext("classpath:MongoConfig.xml");
			MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");
			mongoOperation.save(event);
			ConfigurationServlet.logger.info(" Event Saved ");
		}
	}
}
