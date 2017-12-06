package org.oliot.epcis.service.subscription;

import java.util.Iterator;

import org.apache.log4j.Level;
import org.bson.BsonDocument;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.epcis.service.query.mongodb.MongoQueryService;
import org.oliot.model.epcis.SubscriptionType;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;

import com.mongodb.client.MongoCollection;

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

public class MongoSubscription {

	public static SchedulerFactory schedFact;
	public static Scheduler sched;

	public void init() {

		try {
			schedFact = new org.quartz.impl.StdSchedulerFactory();
			sched = schedFact.getScheduler();
			if (sched.isStarted() == false)
				sched.start();

			MongoCollection<BsonDocument> collection = Configuration.mongoDatabase.getCollection("Subscription",
					BsonDocument.class);

			Iterator<BsonDocument> subIterator = collection.find(BsonDocument.class).iterator();
			MongoQueryService queryService = new MongoQueryService();
			while (subIterator.hasNext()) {
				BsonDocument sub = subIterator.next();
				SubscriptionType subscription = new SubscriptionType(sub);
				if (subscription.getSchedule() != null && subscription.getTrigger() == null) {
					queryService.addScheduleToQuartz(subscription);
				} else if (subscription.getSchedule() == null && subscription.getTrigger() != null) {
					TriggerEngine.addTriggerSubscription(sub.getString("subscriptionID").getValue(), subscription);
				}
			}

		} catch (SchedulerException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
		}
	}
}
