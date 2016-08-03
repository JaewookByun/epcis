package org.oliot.epcis.service.query;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.xml.ws.ResponseWrapper;

import org.oliot.model.epcis.GetSubscriptionIDs;
import org.oliot.model.epcis.Poll;
import org.oliot.model.epcis.QueryResults;
import org.oliot.model.epcis.Subscribe;
import org.oliot.model.epcis.Unsubscribe;

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

@WebService(targetNamespace = "urn:epcglobal:epcis-query:xsd:1")
@SOAPBinding(parameterStyle = ParameterStyle.BARE)
public interface CoreQueryService {

	@WebMethod(operationName = "Subscribe")
	public void subscribe(@WebParam(name="Subscribe") Subscribe subscribe);

	@WebMethod(operationName = "Unsubscribe")
	public void unsubscribe(@WebParam(name="Unsubscribe") Unsubscribe unsubscribe);

	@WebMethod(operationName = "Poll")
	@WebResult(name="QueryResults")
	public QueryResults poll(@WebParam(name="Poll") Poll poll);

	@WebMethod(operationName = "GetQueryNames")
	public List<String> getQueryNames();

	@WebMethod(operationName = "GetSubscriptionIDs")
	public List<String> getSubscriptionIDs(@WebParam(name = "GetSubscriptionIDs") GetSubscriptionIDs getSubscriptionIDs);

	@WebMethod(operationName = "GetStandardVersion")
	@ResponseWrapper(targetNamespace = "urn:epcglobal:epcis-query:xsd:1", localName = "GetStandardVersionResponse")
	public String getStandardVersion();

	@WebMethod(operationName = "GetVendorVersion")
	public String getVendorVersion();

}
