package org.oliot.epcis.pagination;

import java.util.Timer;
import java.util.UUID;

public class Page {

	private UUID uuid;
	private Object query;
	private Object sort;
	private int limit;
	private int skip;
	private Timer timer;

	public Page(UUID uuid, Object query, Object sort, int limit) {
		this.uuid = uuid;
		this.query = query;
		this.sort = sort;
		this.limit = limit;
		this.skip = 0;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public Object getQuery() {
		return query;
	}

	public void setQuery(Object query) {
		this.query = query;
	}

	public Object getSort() {
		return sort;
	}

	public void setSort(Object sort) {
		this.sort = sort;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getSkip() {
		return skip;
	}

	public void setSkip(int skip) {
		this.skip = skip;
	}

	public Timer getTimer() {
		return timer;
	}

	public void setTimer(Timer timer) {
		this.timer = timer;
	}

}
