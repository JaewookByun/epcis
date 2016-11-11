package org.oliot.epcis.service.subscription;

import java.util.List;

import org.apache.log4j.Level;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.epcis.serde.mysql.DBConfiguration;
import org.oliot.epcis.service.query.mysql.MysqlQueryService;
import org.oliot.epcis.service.query.mysql.QueryOprationBackend;
import org.oliot.model.epcis.SubscriptionType;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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
public class MysqlSubscription {

	public static SchedulerFactory schedFact;
	public static Scheduler sched;

	public void init() {

		try {
			schedFact = new org.quartz.impl.StdSchedulerFactory();
			sched = schedFact.getScheduler();
			if (sched.isStarted() == false)
				sched.start();
			
			Configuration.logger.log(Level.INFO,
					"Loading pre-existing subscription ...");
			ApplicationContext ctx=new ClassPathXmlApplicationContext(Configuration.DB); 
			QueryOprationBackend mysqlOperationdao=ctx.getBean
					("queryOprationBackend", QueryOprationBackend.class);
			List<SubscriptionType> allSubscription=mysqlOperationdao.findAllSubscriptionType();
			
			((AbstractApplicationContext) ctx).close();
			
			MysqlQueryService queryService = new MysqlQueryService();
			
			for (int i = 0; i < allSubscription.size(); i++) {
				Configuration.logger.info("subscription: "+i);
				SubscriptionType subscription = allSubscription.get(i);
				if(subscription.getSchedule() != null && subscription.getTrigger() != null){
					queryService.addScheduleToQuartz(subscription);
				}else if(subscription.getSchedule() == null && subscription.getTrigger() !=null){
					TriggerEngine.addTriggerSubscription(subscription.getSubscriptionID(), subscription);
				}
				
			}
			
		} catch (SchedulerException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
		}
	}
}
