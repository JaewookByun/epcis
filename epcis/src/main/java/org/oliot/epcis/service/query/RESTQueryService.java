package org.oliot.epcis.service.query;

import org.oliot.epcis.configuration.ConfigurationServlet;
import org.oliot.epcis.service.query.mongodb.MongoRESTQueryService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/rest")
public class RESTQueryService {

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public String getEPCResource(@RequestParam(required = false) String target,
			@RequestParam(required = false) String targetType,
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

	@RequestMapping(value = "/one", method = RequestMethod.GET)
	@ResponseBody
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
