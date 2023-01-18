package org.oliot.epcis.pagination;

import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

public class PageExpiryTimerTask extends TimerTask {
	private String targetAPI;
	private ConcurrentHashMap<UUID, Page> pageMap;
	private UUID uuidToRemove;
	private Logger logger;

	public PageExpiryTimerTask(String targetAPI, ConcurrentHashMap<UUID, Page> pageMap, UUID uuidToRemove,
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
