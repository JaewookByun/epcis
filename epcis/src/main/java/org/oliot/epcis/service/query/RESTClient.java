package org.oliot.epcis.service.query;

import org.apache.cxf.jaxrs.client.WebClient;



public class RESTClient {
	public static void main(String[] args) {
		WebClient client = WebClient.create("http://localhost:8080/");
		RESTLikeQueryService RESTLikeQueryService=client.path("epcis/Service/GetStandardVersion").accept(
				"application/xml").get(RESTLikeQueryService.class);
		System.out.println(RESTLikeQueryService.getStandardVersion());
	}

}
