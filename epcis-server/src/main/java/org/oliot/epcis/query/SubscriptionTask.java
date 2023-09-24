package org.oliot.epcis.query;

import org.quartz.Job;

import org.quartz.JobExecutionContext;

/**
 * Copyright (C) 2020-2023. (Jaewook Byun) all rights reserved.
 * <p>
 * This project is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * a callback for scheduled query
 * <p>
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr, Associate Director, Auto-ID Labs, Korea,
 *         bjw0829@gmail.com
 */
public class SubscriptionTask implements Job {

	SOAPQueryService qs = new SOAPQueryService();

	/**
	 * Whenever execute method invoked according to the cron expression Query the
	 * database and send the result to the destination.
	 */
	@Override
	public void execute(JobExecutionContext context) {
		qs.sendSubscriptionResult(context);
	}

}
