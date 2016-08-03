package org.oliot.epcis.service.query;

import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;
import javax.xml.bind.JAXB;

import org.oliot.epcis.configuration.Configuration;
import org.oliot.epcis.service.query.mongodb.MongoQueryService;
import org.oliot.model.epcis.EPCISQueryDocumentType;
import org.oliot.model.epcis.GetSubscriptionIDs;
import org.oliot.model.epcis.Poll;
import org.oliot.model.epcis.QueryResults;
import org.oliot.model.epcis.Subscribe;
import org.oliot.model.epcis.Unsubscribe;

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
	public void subscribe(Subscribe subscribe) {
		URI destURI = null;
		try {
			destURI = new URI(subscribe.getDest());
		} catch (URISyntaxException e) {
			return;
		}

		if (Configuration.backend.equals("MongoDB")) {
			MongoQueryService mqs = new MongoQueryService();
			mqs.subscribe(subscribe.getQueryName(), subscribe.getParams(), destURI, subscribe.getControls(),
					subscribe.getSubscriptionID());
		} else if (Configuration.backend.equals("Cassandra")) {

		} else if (Configuration.backend.equals("MySQL")) {

		}
	}

	@Override
	public void unsubscribe(Unsubscribe unsubscribe) {
		if (Configuration.backend.equals("MongoDB")) {
			MongoQueryService mqs = new MongoQueryService();
			mqs.unsubscribe(unsubscribe.getSubscriptionID());
		} else if (Configuration.backend.equals("Cassandra")) {

		} else if (Configuration.backend.equals("MySQL")) {

		}
	}

	@Override
	public List<String> getSubscriptionIDs(GetSubscriptionIDs getSubscriptionIDs) {
		if (Configuration.backend.equals("MongoDB")) {
			MongoQueryService mqs = new MongoQueryService();
			return mqs.getSubscriptionIDs(getSubscriptionIDs.getQueryName());
		} else if (Configuration.backend.equals("Cassandra")) {

		} else if (Configuration.backend.equals("MySQL")) {

		}
		return null;
	}

	@Override
	public List<String> getQueryNames() {
		List<String> queryNames = new ArrayList<String>();
		queryNames.add("SimpleEventQuery");
		queryNames.add("SimpleMasterDataQuery");
		return queryNames;
	}

	@Override
	public String getStandardVersion() {
		return "1.2";
	}

	@Override
	public String getVendorVersion() {
		return "org.oliot.epcis-1.2.1";
	}

	@Override
	public QueryResults poll(Poll poll) {
		if (Configuration.backend.equals("MongoDB")) {
			MongoQueryService mqs = new MongoQueryService();
			String queryResultString = mqs.poll(poll.getQueryName(), poll.getParams());
			// QueryResults Cannot Contains Error Message if according to SPEC
			EPCISQueryDocumentType resultXML = JAXB.unmarshal(new StringReader(queryResultString),
					EPCISQueryDocumentType.class);
			if (resultXML != null && resultXML.getEPCISBody() != null
					&& resultXML.getEPCISBody().getQueryResults() != null
					&& resultXML.getEPCISBody().getQueryResults().getResultsBody() != null) {
				QueryResults queryResults = new QueryResults();
				queryResults.setQueryName(poll.getQueryName());
				queryResults.setResultsBody(resultXML.getEPCISBody().getQueryResults().getResultsBody());
				return queryResults;
			}
		} else if (Configuration.backend.equals("Cassandra")) {

		} else if (Configuration.backend.equals("MySQL")) {

		}
		return null;
	}
}
