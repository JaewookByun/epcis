package org.oliot.epcis.service.query;

import java.net.URI;
import java.util.List;

import org.oliot.model.epcis.QueryParams;
import org.oliot.model.epcis.QueryResults;
import org.oliot.model.epcis.SubscriptionControls;

/**
 * Copyright (C) 2014 KAIST RESL
 *
 * This file is part of Oliot (oliot.org).
 *
 * @author Jack Jaewook Byun, Ph.D student Korea Advanced Institute of Science
 *         and Technology Real-time Embedded System Laboratory(RESL)
 *         bjw0829@kaist.ac.kr
 */
public interface CoreQueryService {
	public void subscribe(String queryName, QueryParams params, URI dest,
			SubscriptionControls controls, String subscriptionID);

	public void unsubscribe(String subscriptionID);

	public QueryResults poll(String queryName, QueryParams params);

	public List<String> getQueryNames();

	public List<String> getSubscriptionIDs(String queryName);

	public String getStandardVersion();

	public String getVendorVersion();
}
