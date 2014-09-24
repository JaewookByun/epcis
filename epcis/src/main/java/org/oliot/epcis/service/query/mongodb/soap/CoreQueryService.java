package org.oliot.epcis.service.query.mongodb.soap;

import java.net.URI;
import java.util.List;

import org.oliot.model.epcis.QueryParams;
import org.oliot.model.epcis.QueryResults;
import org.oliot.model.epcis.SubscriptionControls;

/**
 * Copyright (C) 2014 KAIST RESL
 *
 * This project is part of Oliot (oliot.org), pursuing the implementation of
 * Electronic Product Code Information Service(EPCIS) v1.1 specification in
 * EPCglobal.
 * [http://www.gs1.org/gsmp/kc/epcglobal/epcis/epcis_1_1-standard-20140520.pdf]
 * 
 *
 * @author Jack Jaewook Byun, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
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
