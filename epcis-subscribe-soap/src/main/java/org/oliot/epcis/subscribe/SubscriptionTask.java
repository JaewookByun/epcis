package org.oliot.epcis.subscribe;

import org.quartz.Job;
import org.quartz.JobExecutionContext;

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
public class  SubscriptionTask implements Job {

	/**
	 * Whenever execute method invoked according to the cron expression Query the
	 * database and send the result to the destination.
	 */
	@Override
	public void execute(JobExecutionContext context) {
		new SOAPSubscribeService().sendSubscriptionResult(context);
	}
}
