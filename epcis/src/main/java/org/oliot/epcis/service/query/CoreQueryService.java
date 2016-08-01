package org.oliot.epcis.service.query;

import java.net.URI;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.oliot.model.epcis.QueryParams;
import org.oliot.model.epcis.QueryResults;
import org.oliot.model.epcis.SubscriptionControls;

/**
 * Copyright (C) 2014 Jaewook Byun
 *
 * This project is part of Oliot (oliot.org), pursuing the implementation of
 * Electronic Product Code Information Service(EPCIS) v1.1 specification in
 * EPCglobal.
 * [http://www.gs1.org/gsmp/kc/epcglobal/epcis/epcis_1_1-standard-20140520.pdf]
 * 
 *
 * @author Jaewook Byun, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         bjw0829@kaist.ac.kr, bjw0829@gmail.com
 */

@WebService(targetNamespace="urn:epcglobal:epcis-query:xsd:1")
public interface CoreQueryService {

	@WebMethod(operationName="Subscribe")
	public void subscribe(@WebParam(name = "queryName") String queryName, @WebParam(name = "params") QueryParams params,
			@WebParam(name = "dest") URI dest, @WebParam(name = "controls") SubscriptionControls controls,
			@WebParam(name = "subscriptionID") String subscriptionID);

	@WebMethod(operationName="Unsubscribe")
	public void unsubscribe(@WebParam(name = "subscriptionID") String subscriptionID);

	@WebMethod(operationName="Poll")
	public QueryResults poll(@WebParam(name = "queryName") String queryName,
			@WebParam(name = "params") QueryParams params);

	@WebMethod(operationName="GetQueryNames")
	public List<String> getQueryNames();

	@WebMethod(operationName="GetSubscriptionIDs")
	public List<String> getSubscriptionIDs(@WebParam(name = "queryName") String queryName);

	@WebMethod(operationName="GetStandardVersion")
	public String getStandardVersion();

	@WebMethod(operationName="GetVendorVersion")
	public String getVendorVersion();

}
