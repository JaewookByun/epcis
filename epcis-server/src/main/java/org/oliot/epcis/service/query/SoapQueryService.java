package org.oliot.epcis.service.query;

import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;
import javax.xml.bind.JAXB;

import org.oliot.epcis.configuration.Configuration;
import org.oliot.epcis.service.query.mongodb.MongoQueryService;
import org.oliot.model.epcis.EPCISQueryDocumentType;
import org.oliot.model.epcis.QueryParams;
import org.oliot.model.epcis.QueryResults;
import org.oliot.model.epcis.SubscriptionControls;

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

@WebService(endpointInterface = "org.oliot.epcis.service.query.CoreQueryService")
public class SoapQueryService implements CoreQueryService {

	@Deprecated
	@Override
	public void subscribe(String queryName, QueryParams params, URI dest, SubscriptionControls controls,
			String subscriptionID) {
		Configuration.logger.error("epcis-query service does not provide this API");
	}

	@Deprecated
	@Override
	public void unsubscribe(String subscriptionID) {
		Configuration.logger.error("epcis-query service does not provide this API");
	}

	@Deprecated
	@Override
	public List<String> getSubscriptionIDs(String queryName) {
		Configuration.logger.error("epcis-query service does not provide this API");
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
		return null;
	}

	@Override
	public QueryResults poll(String queryName, QueryParams params) {
		if (Configuration.backend.equals("MongoDB")) {
			MongoQueryService mqs = new MongoQueryService();
			String queryResultString = mqs.poll(queryName, params);
			// QueryResults Cannot Contains Error Message if according to SPEC
			EPCISQueryDocumentType resultXML = JAXB.unmarshal(new StringReader(queryResultString),
					EPCISQueryDocumentType.class);
			if (resultXML != null && resultXML.getEPCISBody() != null
					&& resultXML.getEPCISBody().getQueryResults() != null
					&& resultXML.getEPCISBody().getQueryResults().getResultsBody() != null) {
				QueryResults queryResults = new QueryResults();
				queryResults.setQueryName(queryName);
				queryResults.setResultsBody(resultXML.getEPCISBody().getQueryResults().getResultsBody());
				return queryResults;
			}
		} else if (Configuration.backend.equals("Cassandra")) {

		} else if (Configuration.backend.equals("MySQL")) {

		}
		return null;
	}
}
