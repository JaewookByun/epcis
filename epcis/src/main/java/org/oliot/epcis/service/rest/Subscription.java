package org.oliot.epcis.service.rest;

import java.net.MalformedURLException;
import java.net.URL;

import org.oliot.epcis.configuration.ConfigurationServlet;
import org.oliot.epcis.service.rest.mongodb.MongoRESTQueryService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.quartz.CronScheduleBuilder.cronSchedule;

@Controller
@RequestMapping("/subscription")
public class Subscription {

	@RequestMapping(value = "/{subscriptionID}", method = RequestMethod.POST)
	@ResponseBody
	public String addSubscription(
			@PathVariable String subscriptionID,
			@RequestParam(required = true) String target,
			@RequestParam(required = true) String targetType,
			@RequestParam(required = true) String cronExpression,
			@RequestParam(required = true) String destURL) {

		try {
			new URL(destURL);
		} catch (MalformedURLException e) {
			return e.toString();
		}

		try {
			cronSchedule(cronExpression);
		} catch (RuntimeException e) {
			return e.toString();
		}

		String result = "";
		if (ConfigurationServlet.backend.equals("MongoDB")) {
			MongoRESTQueryService mrqs = new MongoRESTQueryService();
			result = mrqs.addSubscription(subscriptionID, target, targetType,
					cronExpression, destURL);
		} else if (ConfigurationServlet.backend.equals("Cassandra")) {

		} else if (ConfigurationServlet.backend.equals("MySQL")) {

		}
		return result;
	}
	
	@RequestMapping(value = "/{subscriptionID}", method = RequestMethod.DELETE)
	@ResponseBody
	public String removeSubscription(@PathVariable String subscriptionID){
		String result = "";
		if (ConfigurationServlet.backend.equals("MongoDB")) {
			MongoRESTQueryService mrqs = new MongoRESTQueryService();
			result = mrqs.removeSubscription(subscriptionID);
		} else if (ConfigurationServlet.backend.equals("Cassandra")) {

		} else if (ConfigurationServlet.backend.equals("MySQL")) {

		}
		return result;
	}
			
		
	
}
