package org.oliot.epcis.service.query;

import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;
import javax.xml.bind.JAXB;

import org.oliot.epcis.configuration.ConfigurationServlet;
import org.oliot.epcis.service.query.mongodb.MongoQueryService;
import org.oliot.model.epcis.EPCISQueryDocumentType;
import org.oliot.model.epcis.QueryParams;
import org.oliot.model.epcis.QueryResults;
import org.oliot.model.epcis.SubscriptionControls;

@WebService(endpointInterface = "org.oliot.epcis.service.query.CoreQueryService")
public class SoapQueryService implements CoreQueryService {

	@Override
	public void subscribe(String queryName, QueryParams params, URI dest,
			SubscriptionControls controls, String subscriptionID) {
		if (ConfigurationServlet.backend.equals("MongoDB")) {
			MongoQueryService mqs = new MongoQueryService();
			mqs.subscribe(queryName, params, dest, controls, subscriptionID);
		} else if (ConfigurationServlet.backend.equals("Cassandra")) {

		} else if (ConfigurationServlet.backend.equals("MySQL")) {

		}
	}

	@Override
	public void unsubscribe(String subscriptionID) {

		if (ConfigurationServlet.backend.equals("MongoDB")) {
			MongoQueryService mqs = new MongoQueryService();
			mqs.unsubscribe(subscriptionID);
		} else if (ConfigurationServlet.backend.equals("Cassandra")) {

		} else if (ConfigurationServlet.backend.equals("MySQL")) {

		}
	}

	@Override
	public QueryResults poll(String queryName, QueryParams params) {
		if (ConfigurationServlet.backend.equals("MongoDB")) {
			MongoQueryService mqs = new MongoQueryService();
			String queryResultString = mqs.poll(queryName, params);
			// QueryResults Cannot Contains Error Message if according to SPEC
			EPCISQueryDocumentType resultXML = JAXB.unmarshal(new StringReader(
					queryResultString), EPCISQueryDocumentType.class);
			if (resultXML != null
					&& resultXML.getEPCISBody() != null
					&& resultXML.getEPCISBody().getQueryResults() != null
					&& resultXML.getEPCISBody().getQueryResults()
							.getResultsBody() != null) {
				QueryResults queryResults = new QueryResults();
				queryResults.setQueryName(queryName);
				queryResults.setResultsBody(resultXML.getEPCISBody()
						.getQueryResults().getResultsBody());
				return queryResults;
			}
		} else if (ConfigurationServlet.backend.equals("Cassandra")) {

		} else if (ConfigurationServlet.backend.equals("MySQL")) {

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
	public List<String> getSubscriptionIDs(String queryName) {
		if (ConfigurationServlet.backend.equals("MongoDB")) {
			MongoQueryService mqs = new MongoQueryService();
			return mqs.getSubscriptionIDs(queryName);
		} else if (ConfigurationServlet.backend.equals("Cassandra")) {

		} else if (ConfigurationServlet.backend.equals("MySQL")) {

		}
		return null;
	}

	@Override
	public String getStandardVersion() {
		return "1.1";
	}

	@Override
	public String getVendorVersion() {

		return null;
	}
}
