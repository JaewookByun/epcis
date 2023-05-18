package org.oliot.epcis.pagination;

import java.util.Timer;
import java.util.UUID;

import org.bson.Document;

/**
 * Copyright (C) 2020-2023. (Jaewook Byun) all rights reserved.
 * <p>
 * This project is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * Page abstracts each page in pagination
 * <p>
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr, Associate Director, Auto-ID Labs, Korea,
 *         bjw0829@gmail.com
 */
public class Page {

	private UUID uuid;
	private String queryName;
	private Document query;
	private Document projection;
	private Document sort;
	private Integer limit;
	private Integer skip;
	private Timer timer;

	public Page(UUID uuid, String queryName, Document query, Document projection, Document sort, Integer limit,
			Integer skip) {
		this.uuid = uuid;
		this.queryName = queryName;
		this.query = query;
		this.projection = projection;
		this.sort = sort;
		this.limit = limit;
		this.skip = skip;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public String getQueryName() {
		return queryName;
	}

	public Document getProjection() {
		return projection;
	}

	public void setProjection(Document projection) {
		this.projection = projection;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public Document getQuery() {
		return query;
	}

	public void setQuery(Document query) {
		this.query = query;
	}

	public Document getSort() {
		return sort;
	}

	public void setSort(Document sort) {
		this.sort = sort;
	}

	public int getSkip() {
		return skip;
	}

	public void incrSkip(int delta) {
		skip = skip + delta;
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
