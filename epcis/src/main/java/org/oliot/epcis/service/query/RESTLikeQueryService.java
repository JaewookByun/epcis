package org.oliot.epcis.service.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.oliot.epcis.configuration.ConfigurationServlet;
import org.oliot.epcis.service.query.mongodb.restlike.MongoQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ServletContextAware;

/**
 * Copyright (C) 2014 KAIST RESL
 *
 * This project is part of Oliot (oliot.org), pursuing the implementation of
 * Electronic Product Code Information Service(EPCIS) v1.1 specification in
 * EPCglobal.
 * [http://www.gs1.org/gsmp/kc/epcglobal/epcis/epcis_1_1-standard-20140520.pdf]
 * 
 *
 * @author Jack Jaewook Byun, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         bjw0829@kaist.ac.kr
 */
@Controller
@RequestMapping("/query")
public class RESTLikeQueryService implements ServletContextAware {

	@Autowired
	ServletContext servletContext;

	@Autowired
	private HttpServletRequest request;

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;

	}

	/**
	 * Registers a subscriber for a previously defined query having the
	 * specified name. The params argument provides the values to be used for
	 * any named parameters defined by the query. The dest parameter specifies a
	 * destination where results from the query are to be delivered, via the
	 * Query Callback Interface. The dest parameter is a URI that both
	 * identifies a specific binding of the Query Callback Interface to use and
	 * specifies addressing information. The controls parameter controls how the
	 * subscription is to be processed; in particular, it specifies the
	 * conditions under which the query is to be invoked (e.g., specifying a
	 * periodic schedule). The subscriptionID is an arbitrary string that is
	 * copied into every response delivered to the specified destination, and
	 * otherwise not interpreted by the EPCIS service. The client may use the
	 * subscriptionID to identify from which subscription a given result was
	 * generated, especially when several subscriptions are made to the same
	 * destination. The dest argument MAY be null or empty, in which case
	 * results are delivered to a pre-arranged destination based on the
	 * authenticated identity of the caller. If the EPCIS implementation does
	 * not have a destination pre-arranged for the caller, or does not permit
	 * this usage, it SHALL raise an InvalidURIException.
	 */
	@RequestMapping(value = "/Subscribe/{queryName}/{subscriptionID}", method = RequestMethod.GET)
	@ResponseBody
	public String subscribe(@PathVariable String queryName,
			@PathVariable String subscriptionID, @RequestParam String dest,
			@RequestParam String cronExpression,
			@RequestParam boolean reportIfEmpty,
			@RequestParam(required = false) String eventType,
			@RequestParam(required = false) String GE_eventTime,
			@RequestParam(required = false) String LT_eventTime,
			@RequestParam(required = false) String GE_recordTime,
			@RequestParam(required = false) String LT_recordTime,
			@RequestParam(required = false) String EQ_action,
			@RequestParam(required = false) String EQ_bizStep,
			@RequestParam(required = false) String EQ_disposition,
			@RequestParam(required = false) String EQ_readPoint,
			@RequestParam(required = false) String WD_readPoint,
			@RequestParam(required = false) String EQ_bizLocation,
			@RequestParam(required = false) String WD_bizLocation,
			@RequestParam(required = false) String EQ_transformationID,
			@RequestParam(required = false) String MATCH_epc,
			@RequestParam(required = false) String MATCH_parentID,
			@RequestParam(required = false) String MATCH_inputEPC,
			@RequestParam(required = false) String MATCH_outputEPC,
			@RequestParam(required = false) String MATCH_anyEPC,
			@RequestParam(required = false) String MATCH_epcClass,
			@RequestParam(required = false) String MATCH_inputEPCClass,
			@RequestParam(required = false) String MATCH_outputEPCClass,
			@RequestParam(required = false) String MATCH_anyEPCClass,
			@RequestParam(required = false) String EQ_quantity,
			@RequestParam(required = false) String GT_quantity,
			@RequestParam(required = false) String GE_quantity,
			@RequestParam(required = false) String LT_quantity,
			@RequestParam(required = false) String LE_quantity,
			@RequestParam(required = false) String orderBy,
			@RequestParam(required = false) String orderDirection,
			@RequestParam(required = false) String eventCountLimit,
			@RequestParam(required = false) String maxEventCount,
			Map<String, String[]> extMap) {

		if (ConfigurationServlet.backend.equals("MongoDB")) {
			MongoQueryService mongoQueryService = new MongoQueryService();
			return mongoQueryService.subscribe(queryName, subscriptionID, dest,
					cronExpression, reportIfEmpty, eventType, GE_eventTime,
					LT_eventTime, GE_recordTime, LT_recordTime, EQ_action,
					EQ_bizStep, EQ_disposition, EQ_readPoint, WD_readPoint,
					EQ_bizLocation, WD_bizLocation, EQ_transformationID,
					MATCH_epc, MATCH_parentID, MATCH_inputEPC, MATCH_outputEPC,
					MATCH_anyEPC, MATCH_epcClass, MATCH_inputEPCClass,
					MATCH_outputEPCClass, MATCH_anyEPCClass, EQ_quantity,
					GT_quantity, GE_quantity, LT_quantity, LE_quantity,
					orderBy, orderDirection, eventCountLimit, maxEventCount,
					extMap);
		} else if (ConfigurationServlet.backend.equals("Cassandra")) {
			return null;
		} else if (ConfigurationServlet.backend.equals("MySQL")) {
			return null;
		}

		return null;
	}

	/**
	 * Removes a previously registered subscription having the specified
	 * subscriptionID.
	 */
	@RequestMapping(value = "/Unsubscribe/{subscriptionID}", method = RequestMethod.GET)
	public void unsubscribe(@PathVariable String subscriptionID) {

		if (ConfigurationServlet.backend.equals("MongoDB")) {
			MongoQueryService mongoQueryService = new MongoQueryService();
			mongoQueryService.unsubscribe(subscriptionID);
		} else if (ConfigurationServlet.backend.equals("Cassandra")) {

		} else if (ConfigurationServlet.backend.equals("MySQL")) {

		}

	}

	/**
	 * Returns a list of all subscriptionIDs currently subscribed to the
	 * specified named query.
	 */
	@RequestMapping(value = "/SubscriptionIDs/{queryName}", method = RequestMethod.GET)
	@ResponseBody
	public String getSubscriptionIDsREST(@PathVariable String queryName) {

		if (ConfigurationServlet.backend.equals("MongoDB")) {
			MongoQueryService mongoQueryService = new MongoQueryService();
			return mongoQueryService.getSubscriptionIDsREST(queryName);
		} else if (ConfigurationServlet.backend.equals("Cassandra")) {
			return null;
		} else if (ConfigurationServlet.backend.equals("MySQL")) {
			return null;
		}

		return null;
	}

	@RequestMapping(value = "/Poll/{queryName}", method = RequestMethod.GET)
	@ResponseBody
	public String poll(@PathVariable String queryName,
			@RequestParam(required = false) String eventType,
			@RequestParam(required = false) String GE_eventTime,
			@RequestParam(required = false) String LT_eventTime,
			@RequestParam(required = false) String GE_recordTime,
			@RequestParam(required = false) String LT_recordTime,
			@RequestParam(required = false) String EQ_action,
			@RequestParam(required = false) String EQ_bizStep,
			@RequestParam(required = false) String EQ_disposition,
			@RequestParam(required = false) String EQ_readPoint,
			@RequestParam(required = false) String WD_readPoint,
			@RequestParam(required = false) String EQ_bizLocation,
			@RequestParam(required = false) String WD_bizLocation,
			@RequestParam(required = false) String EQ_transformationID,
			@RequestParam(required = false) String MATCH_epc,
			@RequestParam(required = false) String MATCH_parentID,
			@RequestParam(required = false) String MATCH_inputEPC,
			@RequestParam(required = false) String MATCH_outputEPC,
			@RequestParam(required = false) String MATCH_anyEPC,
			@RequestParam(required = false) String MATCH_epcClass,
			@RequestParam(required = false) String MATCH_inputEPCClass,
			@RequestParam(required = false) String MATCH_outputEPCClass,
			@RequestParam(required = false) String MATCH_anyEPCClass,
			@RequestParam(required = false) String EQ_quantity,
			@RequestParam(required = false) String GT_quantity,
			@RequestParam(required = false) String GE_quantity,
			@RequestParam(required = false) String LT_quantity,
			@RequestParam(required = false) String LE_quantity,
			@RequestParam(required = false) String orderBy,
			@RequestParam(required = false) String orderDirection,
			@RequestParam(required = false) String eventCountLimit,
			@RequestParam(required = false) String maxEventCount,

			@RequestParam(required = false) String vocabularyName,
			@RequestParam(required = false) boolean includeAttributes,
			@RequestParam(required = false) boolean includeChildren,
			@RequestParam(required = false) String attributeNames,
			@RequestParam(required = false) String EQ_name,
			@RequestParam(required = false) String WD_name,
			@RequestParam(required = false) String HASATTR,
			@RequestParam(required = false) String maxElementCount,
			Map<String, String[]> extMap) {

		if (ConfigurationServlet.backend.equals("MongoDB")) {
			MongoQueryService mongoQueryService = new MongoQueryService();
			return mongoQueryService.poll(queryName, eventType, GE_eventTime,
					LT_eventTime, GE_recordTime, LT_recordTime, EQ_action,
					EQ_bizStep, EQ_disposition, EQ_readPoint, WD_readPoint,
					EQ_bizLocation, WD_bizLocation, EQ_transformationID,
					MATCH_epc, MATCH_parentID, MATCH_inputEPC, MATCH_outputEPC,
					MATCH_anyEPC, MATCH_epcClass, MATCH_inputEPCClass,
					MATCH_outputEPCClass, MATCH_anyEPCClass, EQ_quantity,
					GT_quantity, GE_quantity, LT_quantity, LE_quantity,
					orderBy, orderDirection, eventCountLimit, maxEventCount,
					vocabularyName, includeAttributes, includeChildren,
					attributeNames, EQ_name, WD_name, HASATTR, maxElementCount,
					extMap);
		} else if (ConfigurationServlet.backend.equals("Cassandra")) {
			return null;
		} else if (ConfigurationServlet.backend.equals("MySQL")) {
			return null;
		}

		return null;
	}

	/**
	 * [REST Version of getQueryNames] Returns a list of all query names
	 * available for use with the subscribe and poll methods. This includes all
	 * pre- defined queries provided by the implementation, including those
	 * specified in Section 8.2.7.
	 * 
	 * No Dependency with Backend
	 * 
	 * @return JSONArray of query names ( String )
	 */
	@RequestMapping(value = "/QueryNames", method = RequestMethod.GET)
	@ResponseBody
	public String getQueryNamesREST() {
		JSONArray jsonArray = new JSONArray();
		List<String> queryNames = getQueryNames();
		for (int i = 0; i < queryNames.size(); i++) {
			jsonArray.put(queryNames.get(i));
		}
		return jsonArray.toString(1);
	}

	/**
	 * Returns a list of all query names available for use with the subscribe
	 * and poll methods. This includes all pre- defined queries provided by the
	 * implementation, including those specified in Section 8.2.7.
	 * 
	 * No Dependency with Backend
	 */
	public List<String> getQueryNames() {
		List<String> queryNames = new ArrayList<String>();
		queryNames.add("SimpleEventQuery");
		queryNames.add("SimpleMasterDataQuery");
		return queryNames;
	}

	/**
	 * Returns a string that identifies what version of the specification this
	 * implementation complies with. The possible values for this string are
	 * defined by GS1. An implementation SHALL return a string corresponding to
	 * a version of this specification to which the implementation fully
	 * complies, and SHOULD return the string corresponding to the latest
	 * version to which it complies. To indicate compliance with this Version
	 * 1.1 of the EPCIS specification, the implementation SHALL return the
	 * string 1.1.
	 * 
	 * No Dependency with Backend
	 */
	@RequestMapping(value = "/StandardVersion", method = RequestMethod.GET)
	@ResponseBody
	public String getStandardVersion() {
		return "1.1";
	}

	/**
	 * Returns a string that identifies what vendor extensions this
	 * implementation provides. The possible values of this string and their
	 * meanings are vendor-defined, except that the empty string SHALL indicate
	 * that the implementation implements only standard functionality with no
	 * vendor extensions. When an implementation chooses to return a non-empty
	 * string, the value returned SHALL be a URI where the vendor is the owning
	 * authority. For example, this may be an HTTP URL whose authority portion
	 * is a domain name owned by the vendor, a URN having a URN namespace
	 * identifier issued to the vendor by IANA, an OID URN whose initial path is
	 * a Private Enterprise Number assigned to the vendor, etc.
	 * 
	 * No Dependency with Backend
	 */
	@RequestMapping(value = "/VendorVersion", method = RequestMethod.GET)
	@ResponseBody
	public String getVendorVersion() {
		// It is not a version of Vendor
		return null;
	}
}
