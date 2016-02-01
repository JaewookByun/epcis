package org.oliot.epcis.service.capture.mysql;

import org.oliot.epcis.configuration.Configuration;
import org.oliot.epcis.serde.mysql.CaptureOperationsBackend;
import org.oliot.model.epcis.AggregationEventType;
import org.oliot.model.epcis.ObjectEventType;
import org.oliot.model.epcis.QuantityEventType;
import org.oliot.model.epcis.SensorEventType;
import org.oliot.model.epcis.TransactionEventType;
import org.oliot.model.epcis.TransformationEventType;
import org.oliot.model.epcis.VocabularyType;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Copyright (C) 2015 Yalew Kidane
 *
 * This project is part of Oliot (oliot.org), pursuing the implementation of
 * Electronic Product Code Information Service(EPCIS) v1.1 specification in
 * EPCglobal.
 * [http://www.gs1.org/gsmp/kc/epcglobal/epcis/epcis_1_1-standard-20140520.pdf]
 * 
 *
 * @author Yalew Kidane, MSc student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         yalewkidane@kaist.ac.kr, yalewkidane@gmail.com
 */
public class MysqlCaptureUtil {
	
	

	public void capture(AggregationEventType event)
	{
		ApplicationContext ctx=new ClassPathXmlApplicationContext("MysqlConfig.xml");
		
		CaptureOperationsBackend mysqlOperationdao=ctx.getBean
				("captureOperationsBackend", CaptureOperationsBackend.class);
		
		mysqlOperationdao.save(event);
		Configuration.logger.info(" Event Saved ");
		((AbstractApplicationContext) ctx).close();
	}
	
	public void capture(ObjectEventType event)
	{
		ApplicationContext ctx=new ClassPathXmlApplicationContext("MysqlConfig.xml");
		
		CaptureOperationsBackend mysqlOperationdao=ctx.getBean
				("captureOperationsBackend", CaptureOperationsBackend.class);
		
		mysqlOperationdao.save(event);
		Configuration.logger.info(" Event Saved ");
		((AbstractApplicationContext) ctx).close();
	}
	
	public void capture(QuantityEventType event)
	{
		ApplicationContext ctx=new ClassPathXmlApplicationContext("MysqlConfig.xml");
		
		CaptureOperationsBackend mysqlOperationdao=ctx.getBean
				("captureOperationsBackend", CaptureOperationsBackend.class);
		
		mysqlOperationdao.save(event);
		Configuration.logger.info(" Event Saved ");
		((AbstractApplicationContext) ctx).close();
		
	}
	
	public void capture(TransactionEventType event)
	{
		ApplicationContext ctx=new ClassPathXmlApplicationContext("MysqlConfig.xml");
		
		CaptureOperationsBackend mysqlOperationdao=ctx.getBean
				("captureOperationsBackend", CaptureOperationsBackend.class);
		
		mysqlOperationdao.save(event);
		Configuration.logger.info(" Event Saved ");
		((AbstractApplicationContext) ctx).close();
	}
	public void capture(TransformationEventType event)
	{
		ApplicationContext ctx=new ClassPathXmlApplicationContext("MysqlConfig.xml");
		
		CaptureOperationsBackend mysqlOperationdao=ctx.getBean
				("captureOperationsBackend", CaptureOperationsBackend.class);
		
		mysqlOperationdao.save(event);
		Configuration.logger.info(" Event Saved ");
		((AbstractApplicationContext) ctx).close();
	}
	public void capture(SensorEventType event)
	{
		ApplicationContext ctx=new ClassPathXmlApplicationContext("MysqlConfig.xml");
		
		CaptureOperationsBackend mysqlOperationdao=ctx.getBean
				("captureOperationsBackend", CaptureOperationsBackend.class);
		
		mysqlOperationdao.save(event);
		Configuration.logger.info(" Event Saved ");
		((AbstractApplicationContext) ctx).close();
	}
	
	public void capture(VocabularyType vocabulary)
	{
		
		ApplicationContext ctx=new ClassPathXmlApplicationContext("MysqlConfig.xml");
		
		CaptureOperationsBackend mysqlOperationdao=ctx.getBean
				("captureOperationsBackend", CaptureOperationsBackend.class);
		
		mysqlOperationdao.save(vocabulary);
		Configuration.logger.info(" Vocabulary Saved ");
		((AbstractApplicationContext) ctx).close();
	
	}
}
