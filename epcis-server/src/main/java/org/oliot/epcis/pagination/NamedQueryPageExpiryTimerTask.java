package org.oliot.epcis.pagination;

import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

/**
 * Copyright (C) 2020-2023. (Jaewook Byun) all rights reserved.
 * <p>
 * This project is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * PageExpiryTimerTask manages page expiry.
 * <p>
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr, Associate Director, Auto-ID Labs, Korea,
 *         bjw0829@gmail.com
 */
public class NamedQueryPageExpiryTimerTask extends TimerTask {
	private String targetAPI;
	private ConcurrentHashMap<UUID, NamedQueryPage> pageMap;
	private UUID uuidToRemove;
	private Logger logger;

	public NamedQueryPageExpiryTimerTask(String targetAPI, ConcurrentHashMap<UUID, NamedQueryPage> pageMap, UUID uuidToRemove,
			Logger logger) {
		this.targetAPI = targetAPI;
		this.pageMap = pageMap;
		this.uuidToRemove = uuidToRemove;
		this.logger = logger;
	}

	@Override
	public void run() {
		pageMap.remove(uuidToRemove);
		logger.debug("[" + targetAPI + "] page - " + uuidToRemove + " expired. # remaining pages - " + pageMap.size());
	}
}
