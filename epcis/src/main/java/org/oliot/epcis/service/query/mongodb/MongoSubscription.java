package org.oliot.epcis.service.query.mongodb;

import java.util.Iterator;

import org.apache.log4j.Level;
import org.bson.BsonDocument;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.model.epcis.SubscriptionType;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;

import com.mongodb.client.MongoCollection;

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
				queryService.addScheduleToQuartz(subscription);
			}

		} catch (SchedulerException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
		}
	}
}
