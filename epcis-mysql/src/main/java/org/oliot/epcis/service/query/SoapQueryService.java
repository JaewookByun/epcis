package org.oliot.epcis.service.query;

import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;
import javax.xml.bind.JAXB;

import org.oliot.epcis.configuration.Configuration;
import org.oliot.epcis.service.query.mysql.MysqlQueryService;
import org.oliot.model.epcis.DuplicateSubscriptionException;
import org.oliot.model.epcis.EPCISQueryDocumentType;
import org.oliot.model.epcis.GetSubscriptionIDs;
import org.oliot.model.epcis.ImplementationException;
import org.oliot.model.epcis.InvalidURIException;
import org.oliot.model.epcis.NoSuchNameException;
import org.oliot.model.epcis.NoSuchSubscriptionException;
import org.oliot.model.epcis.Poll;
import org.oliot.model.epcis.QueryParameterException;
import org.oliot.model.epcis.QueryResults;
import org.oliot.model.epcis.QueryTooComplexException;
import org.oliot.model.epcis.QueryTooLargeException;
import org.oliot.model.epcis.SecurityException;
import org.oliot.model.epcis.Subscribe;
import org.oliot.model.epcis.SubscribeNotPermittedException;
import org.oliot.model.epcis.SubscriptionControlsException;
import org.oliot.model.epcis.Unsubscribe;
import org.oliot.model.epcis.ValidationException;

/**
 * Copyright (C) 2014-2016 Jaewook Byun
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

@WebService(endpointInterface = "org.oliot.epcis.service.query.CoreQueryService", targetNamespace = "urn:epcglobal:epcis-query:xsd:1")
public class SoapQueryService implements CoreQueryService {

	@Override
	public void subscribe(Subscribe subscribe)
			throws NoSuchNameException, InvalidURIException, DuplicateSubscriptionException, QueryParameterException,
			QueryTooComplexException, SubscriptionControlsException, SubscribeNotPermittedException, SecurityException,
			ValidationException, ImplementationException {
		URI destURI = null;
		try {
			destURI = new URI(subscribe.getDest());
		} catch (URISyntaxException e) {
			return;
		}

		//MongoQueryService mqs = new MongoQueryService();
		MysqlQueryService mqs=new MysqlQueryService();
		mqs.subscribe(subscribe.getQueryName(), subscribe.getParams(), destURI, subscribe.getControls(),
				subscribe.getSubscriptionID());
		
		
	}

	@Override
	public void unsubscribe(Unsubscribe unsubscribe)
			throws NoSuchSubscriptionException, ValidationException, ImplementationException {
		MysqlQueryService mqs=new MysqlQueryService();
		//MongoQueryService mqs = new MongoQueryService();
		mqs.unsubscribe(unsubscribe.getSubscriptionID());
	}

	@Override
	public List<String> getSubscriptionIDs(GetSubscriptionIDs getSubscriptionIDs)
			throws NoSuchNameException, SecurityException, ValidationException, ImplementationException {
		MysqlQueryService mqs=new MysqlQueryService();
		//MongoQueryService mqs = new MongoQueryService();
		return mqs.getSubscriptionIDs(getSubscriptionIDs.getQueryName());
	}

	@Override
	public List<String> getQueryNames() throws SecurityException, ValidationException, ImplementationException {
		List<String> queryNames = new ArrayList<String>();
		queryNames.add("SimpleEventQuery");
		queryNames.add("SimpleMasterDataQuery");
		return queryNames;
	}

	@Override
	public String getStandardVersion() throws SecurityException, ValidationException, ImplementationException {
		return "1.2";
	}

	@Override
	public String getVendorVersion() throws SecurityException, ValidationException, ImplementationException {
		return "org.oliot.epcis-1.2.1";
	}

	@Override
	public QueryResults poll(Poll poll)
			throws QueryParameterException, QueryTooLargeException, QueryTooComplexException, NoSuchNameException,
			SecurityException, ValidationException, ImplementationException {
		// ----- Saop update started from here
		//MongoQueryService mqs = new MongoQueryService();
		//String queryResultString = mqs.poll(poll.getQueryName(), poll.getParams());
		Configuration.logger.info("Saop poll with mysql started");
		MysqlQueryService mqs=new MysqlQueryService();
		String queryResultString = mqs.poll(poll.getQueryName(), poll.getParams());
		
		// QueryResults Cannot Contains Error Message if according to SPEC
		EPCISQueryDocumentType resultXML = JAXB.unmarshal(new StringReader(queryResultString),
				EPCISQueryDocumentType.class);
		if (resultXML != null && resultXML.getEPCISBody() != null && resultXML.getEPCISBody().getQueryResults() != null
				&& resultXML.getEPCISBody().getQueryResults().getResultsBody() != null) {
			QueryResults queryResults = new QueryResults();
			queryResults.setQueryName(poll.getQueryName());
			queryResults.setResultsBody(resultXML.getEPCISBody().getQueryResults().getResultsBody());
			return queryResults;
		}
		return null;
	}
}
