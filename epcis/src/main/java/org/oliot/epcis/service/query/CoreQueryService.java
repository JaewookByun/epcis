package org.oliot.epcis.service.query;

import java.net.URI;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.oliot.model.epcis.QueryParams;
import org.oliot.model.epcis.QueryResults;
import org.oliot.model.epcis.SubscriptionControls;

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

@WebService
public interface CoreQueryService {

	@WebMethod
	public void subscribe(String queryName, QueryParams params, URI dest,
			SubscriptionControls controls, String subscriptionID);

	@WebMethod
	public void unsubscribe(String subscriptionID);

	@WebMethod
	public QueryResults poll(String queryName, QueryParams params);

	@WebMethod
	public List<String> getQueryNames();

	@WebMethod
	public List<String> getSubscriptionIDs(String queryName);

	@WebMethod
	public String getStandardVersion();

	@WebMethod
	public String getVendorVersion();

}
