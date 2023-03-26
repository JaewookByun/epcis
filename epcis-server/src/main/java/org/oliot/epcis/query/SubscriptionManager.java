package org.oliot.epcis.query;

import java.util.ArrayList;
import java.util.List;

import org.oliot.epcis.server.EPCISServer;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;

/**
 * Copyright (C) 2020-2023. (Jaewook Byun) all rights reserved.
 * <p>
 * This project is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * SubscriptionManager manages a subscription
 * <p>
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr, Associate Director, Auto-ID Labs, Korea,
 *         bjw0829@gmail.com
 */
public class SubscriptionManager {

	public static SchedulerFactory schedFact;
	public static Scheduler sched;

	public void init() {

		try {
			schedFact = new org.quartz.impl.StdSchedulerFactory();
			sched = schedFact.getScheduler();
			if (!sched.isStarted())
				sched.start();

			List<org.bson.Document> results = new ArrayList<org.bson.Document>();
			EPCISServer.mSubscriptionCollection.find().into(results);
			SOAPQueryService subscribeService = new SOAPQueryService();

			for (org.bson.Document doc : results) {
				if (doc.getString("schedule") != null) {
					Subscription s = new Subscription(doc);
					subscribeService.addScheduleToQuartz(s);
					EPCISServer.logger
							.debug("Existing scheduled subscription " + doc.getString("_id") + " activated");
				} else if (doc.getString("trigger") != null) {

					// TriggerEngine.addTriggerSubscription(subscription.getSubscriptionID(),
					// subscription);
					// SOAPSubscribeServer.logger.debug("Existing trigger subscription " +
					// subscription.getSubscriptionID() + " activated");
				}
			}
		} catch (SchedulerException e) {
			EPCISServer.logger.error("Subscription loading fails: " + e.getMessage());
			EPCISServer.logger.error("System terminated");
			System.exit(1);
		} catch (Throwable e) {
			EPCISServer.logger.error("Subscription loading fails: " + e.getMessage());
			EPCISServer.logger.error("System terminated");
			System.exit(1);
		}
	}
}
