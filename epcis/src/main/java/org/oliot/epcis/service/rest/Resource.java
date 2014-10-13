package org.oliot.epcis.service.rest;

import org.oliot.epcis.configuration.ConfigurationServlet;
import org.oliot.epcis.service.rest.mongodb.MongoRESTQueryService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/resource")
public class Resource {

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public String getEPCResource(@RequestParam(required = true) String target,
			@RequestParam(required = true) String targetType,
			@RequestParam(required = false) String from,
			@RequestParam(required = false) String until) {

		String result = "";
		if (ConfigurationServlet.backend.equals("MongoDB")) {
			MongoRESTQueryService mrqs = new MongoRESTQueryService();
			result = mrqs.getEPCResource(target, targetType, from, until);
		} else if (ConfigurationServlet.backend.equals("Cassandra")) {

		} else if (ConfigurationServlet.backend.equals("MySQL")) {

		}
		return result;
	}
	
	public String getEPCResourceOne(
			@RequestParam(required = false) String target,
			@RequestParam(required = false) String targetType) {

		String result = "";
		if (ConfigurationServlet.backend.equals("MongoDB")) {
			MongoRESTQueryService mrqs = new MongoRESTQueryService();
			result = mrqs.getEPCResourceOne(target, targetType);
		} else if (ConfigurationServlet.backend.equals("Cassandra")) {

		} else if (ConfigurationServlet.backend.equals("MySQL")) {

		}
		return result;
	}
}
