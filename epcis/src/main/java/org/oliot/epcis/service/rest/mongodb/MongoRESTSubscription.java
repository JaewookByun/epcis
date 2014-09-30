package org.oliot.epcis.service.rest.mongodb;

import java.util.List;

import org.apache.log4j.Level;
import org.oliot.epcis.configuration.ConfigurationServlet;
import org.oliot.model.epcis.RESTSubscriptionType;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;

public class MongoRESTSubscription {

	static SchedulerFactory schedFact;
	static Scheduler sched;

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

			List<RESTSubscriptionType> allSubscription = mongoOperation
					.findAll(RESTSubscriptionType.class);
			MongoRESTQueryService subscriptionService = new MongoRESTQueryService();
			ConfigurationServlet.logger.log(Level.INFO,
					"Loading pre-existing subscription");
			for (int i = 0; i < allSubscription.size(); i++) {
				RESTSubscriptionType subscription = allSubscription.get(i);

				subscriptionService.addToScheduler(
						subscription.getSubscriptionID(),
						subscription.getTarget(), subscription.getTargetType(),
						subscription.getCronExpression(),
						subscription.getDestURL());
			}
			((AbstractApplicationContext) ctx).close();

		} catch (SchedulerException e) {
			ConfigurationServlet.logger.log(Level.ERROR, e.toString());
		}

	}

}
