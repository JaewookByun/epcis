package org.oliot.epcis.subscribe;

import org.oliot.epcis.query.Subscription;
import org.oliot.epcis.util.ObservableSubscriber;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;

/**
 * Copyright (C) 2020-2021. (Jaewook Byun) all rights reserved.
 * <p>
 * Oliot EPCIS X is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * Among various modules, epcis-subscribe-soap acts as a special server to receive subscription queries
 * to provide filtered, sorted, limited events in a periodic manner or on demand.
 * <p>
 * 
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr
 *         <p>
 *         Associate Director, Auto-ID Labs, KAIST, bjw0829@kaist.ac.kr
 */
public class SubscriptionScheduler {

	public static SchedulerFactory schedFact;
	public static Scheduler sched;

	public void init() {

		try {
			schedFact = new org.quartz.impl.StdSchedulerFactory();
			sched = schedFact.getScheduler();
			if (!sched.isStarted())
				sched.start();
			
			
			ObservableSubscriber<org.bson.Document> subscriber = new ObservableSubscriber<org.bson.Document>();		
			SOAPSubscribeServer.mSubscribeCollection.find().subscribe(subscriber);;		
			subscriber.await();
			SOAPSubscribeService subscribeService = new SOAPSubscribeService();
			for(org.bson.Document doc: subscriber.getReceived()) {
				Subscription subscription = new Subscription(doc);
				if (subscription.getSchedule() != null) {
					subscribeService.addScheduleToQuartz(subscription);
					SOAPSubscribeServer.logger.debug("Existing scheduled subscription " + subscription.getSubscriptionID() + " activated");
				} else if (subscription.getTrigger() != null) {
					TriggerEngine.addTriggerSubscription(subscription.getSubscriptionID(), subscription);
					SOAPSubscribeServer.logger.debug("Existing trigger subscription " + subscription.getSubscriptionID() + " activated");
				}
			}
		} catch (SchedulerException e) {
			SOAPSubscribeServer.logger.error("Subscription loading fails: " + e.getMessage());
			SOAPSubscribeServer.logger.error("System terminated");
			System.exit(1);;
		} catch (Throwable e) {
			SOAPSubscribeServer.logger.error("Subscription loading fails: " + e.getMessage());
			SOAPSubscribeServer.logger.error("System terminated");
			System.exit(1);;
		}
	}
}
