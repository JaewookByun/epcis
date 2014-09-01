package org.oliot.epcis.service.query;

import java.net.URI;
import java.util.List;

import org.oliot.model.epcis.QueryParams;
import org.oliot.model.epcis.QueryResults;
import org.oliot.model.epcis.SubscriptionControls;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/query")
public class Query implements CoreQueryService {

	@Override
	public void subscribe(String queryName, QueryParams params, URI dest,
			SubscriptionControls controls, String subscriptionID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unsubscribe(String subscriptionID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public QueryResults poll(String queryName, QueryParams params) {
		
		return null;
	}

	@Override
	public List<String> getQueryNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getSubscriptionIDs(String queryName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@RequestMapping(value = "/StandardVersion", method=RequestMethod.GET)
	public String getStandardVersion() {
		return "1.1";
	}

	@Override
	@RequestMapping(value = "/VendorVersion", method=RequestMethod.GET)
	public String getVendorVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	
}
