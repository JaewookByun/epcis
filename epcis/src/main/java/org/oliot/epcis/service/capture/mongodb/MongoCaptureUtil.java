package org.oliot.epcis.service.capture.mongodb;

import org.oliot.epcis.configuration.Configuration;
import org.oliot.model.epcis.AggregationEventType;
import org.oliot.model.epcis.ObjectEventType;
import org.oliot.model.epcis.QuantityEventType;
import org.oliot.model.epcis.SensorEventType;
import org.oliot.model.epcis.TransactionEventType;
import org.oliot.model.epcis.TransformationEventType;
import org.oliot.model.epcis.VocabularyType;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;

public class MongoCaptureUtil {

	public void capture(AggregationEventType event)
	{
		ApplicationContext ctx = new GenericXmlApplicationContext(
				"classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx
				.getBean("mongoTemplate");
		mongoOperation.save(event);
		Configuration.logger.info(" Event Saved ");
		((AbstractApplicationContext) ctx).close();
	}
	
	public void capture(ObjectEventType event)
	{
		ApplicationContext ctx = new GenericXmlApplicationContext(
				"classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx
				.getBean("mongoTemplate");
		mongoOperation.save(event);
		Configuration.logger.info(" Event Saved ");
		((AbstractApplicationContext) ctx).close();
	}
	
	public void capture(QuantityEventType event)
	{
		ApplicationContext ctx = new GenericXmlApplicationContext(
				"classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx
				.getBean("mongoTemplate");
		mongoOperation.save(event);
		Configuration.logger.info(" Event Saved ");
		((AbstractApplicationContext) ctx).close();
	}
	
	public void capture(TransactionEventType event)
	{
		ApplicationContext ctx = new GenericXmlApplicationContext(
				"classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx
				.getBean("mongoTemplate");
		mongoOperation.save(event);
		Configuration.logger.info(" Event Saved ");
		((AbstractApplicationContext) ctx).close();
	}
	public void capture(TransformationEventType event)
	{
		ApplicationContext ctx = new GenericXmlApplicationContext(
				"classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx
				.getBean("mongoTemplate");
		mongoOperation.save(event);
		Configuration.logger.info(" Event Saved ");
		((AbstractApplicationContext) ctx).close();
	}
	public void capture(SensorEventType event)
	{
		ApplicationContext ctx = new GenericXmlApplicationContext(
				"classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx
				.getBean("mongoTemplate");
		mongoOperation.save(event);
		Configuration.logger.info(" Event Saved ");
		((AbstractApplicationContext) ctx).close();
	}
	
	public void capture(VocabularyType vocabulary)
	{
		ApplicationContext ctx = new GenericXmlApplicationContext(
				"classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx
				.getBean("mongoTemplate");
		mongoOperation.save(vocabulary);
		Configuration.logger.info(" Event Saved ");
		((AbstractApplicationContext) ctx).close();
	}
}
