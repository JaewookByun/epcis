/**
 * Copyright (C) 2006, 2005 EPCglobal Inc., All Rights Reserved.
 * EPCglobal Inc., its members, officers, directors, employees, or agents shall
 * not be liable for any injury, loss, damages, financial or otherwise,
 *	arising from, related to, or caused by the use of this document. 
 * The use of said document shall constitute your express consent 
 * to the foregoing exculpation.
 */

package org.oliot.epcis.service.query;

import java.io.StringWriter;
import java.net.URI;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.json.JSONArray;
import org.oliot.model.epcis.AggregationEventType;
import org.oliot.model.epcis.EPCISQueryBodyType;
import org.oliot.model.epcis.EPCISQueryDocumentType;
import org.oliot.model.epcis.EventListType;
import org.oliot.model.epcis.ObjectEventType;
import org.oliot.model.epcis.QuantityEventType;
import org.oliot.model.epcis.QueryParam;
import org.oliot.model.epcis.QueryParams;
import org.oliot.model.epcis.QueryResults;
import org.oliot.model.epcis.QueryResultsBody;
import org.oliot.model.epcis.SubscriptionControls;
import org.oliot.model.epcis.TransactionEventType;
import org.oliot.model.epcis.TransformationEventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ServletContextAware;

@Controller
@RequestMapping("/query")
public class QueryService implements CoreQueryService, ServletContextAware {

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
	@Override
	public void subscribe(String queryName, QueryParams params, URI dest,
			SubscriptionControls controls, String subscriptionID) {
		// TODO Auto-generated method stub

	}

	/**
	 * Removes a previously registered subscription having the specified
	 * subscriptionID.
	 */
	@Override
	public void unsubscribe(String subscriptionID) {
		// TODO Auto-generated method stub

	}

	/**
	 * Returns a list of all subscriptionIDs currently subscribed to the
	 * specified named query.
	 */
	@Override
	public List<String> getSubscriptionIDs(String queryName) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings({ "resource", "rawtypes", "unchecked" })
	@RequestMapping(value = "/Poll/{queryName}", method = RequestMethod.GET)
	@ResponseBody
	public String poll(@PathVariable String queryName,
			@RequestParam(required = false) String eventType,
			@RequestParam(required = false) String GE_eventTime,
			@RequestParam(required = false) String LE_eventTime,
			@RequestParam(required = false) String GE_recordTime,
			@RequestParam(required = false) String LT_recordTime,
			@RequestParam(required = false) String EQ_action,
			@RequestParam(required = false) String EQ_bizStep,
			@RequestParam(required = false) String EQ_disposition,
			@RequestParam(required = false) String EQ_readPoint,
			@RequestParam(required = false) String WD_readPoint,
			@RequestParam(required = false) String EQ_bizLocation,
			@RequestParam(required = false) String WD_bizLocation,
			@RequestParam(required = false) String EQ_bizTransaction_type,
			@RequestParam(required = false) String EQ_source_type,
			@RequestParam(required = false) String EQ_destination_type,
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
			@RequestParam(required = false) String EQ_fieldname,
			@RequestParam(required = false) String GT_fieldname,
			@RequestParam(required = false) String GE_fieldname,
			@RequestParam(required = false) String LT_fieldname,
			@RequestParam(required = false) String LE_fieldname,
			@RequestParam(required = false) String EQ_ILMD_fieldname,
			@RequestParam(required = false) String GT_ILMD_fieldname,
			@RequestParam(required = false) String GE_ILMD_fieldname,
			@RequestParam(required = false) String LT_ILMD_fieldname,
			@RequestParam(required = false) String LE_ILMD_fieldname,
			@RequestParam(required = false) String EXIST_fieldname,
			@RequestParam(required = false) String EXIST_ILMD_fieldname,
			@RequestParam(required = false) String HASATTR_fieldname,
			@RequestParam(required = false) String EQATTR_fieldname_attrname,
			@RequestParam(required = false) String orderBy,
			@RequestParam(required = false) String orderDirection,
			@RequestParam(required = false) String eventCountLimit,
			@RequestParam(required = false) String maxEventCount) {
		if (!queryName.equals("SimpleEventQuery"))
			return "Unavailable Query Name";

		// Make Base Result Document
		EPCISQueryDocumentType epcisQueryDocumentType = makeBaseResultDocument(queryName);

		ApplicationContext ctx = new GenericXmlApplicationContext(
				"classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx
				.getBean("mongoTemplate");

		// eventObjects : Container which all the query results (events) will be
		// contained
		List<Object> eventObjects = epcisQueryDocumentType.getEPCISBody()
				.getQueryResults().getResultsBody().getEventList()
				.getObjectEventOrAggregationEventOrQuantityEvent();

		// To be filtered by eventType
		boolean toGetAggregationEvent = true;
		boolean toGetObjectEvent = true;
		boolean toGetQuantityEvent = true;
		boolean toGetTransactionEvent = true;
		boolean toGetTransformationEvent = true;

		// Criteria1 : Projection of Event Type
		if (eventType != null) {
			toGetAggregationEvent = false;
			toGetObjectEvent = false;
			toGetQuantityEvent = false;
			toGetTransactionEvent = false;
			toGetTransformationEvent = false;

			String[] eventTypeArray = eventType.split(",");

			for (int i = 0; i < eventTypeArray.length; i++) {
				String eventTypeString = eventTypeArray[i];

				if (eventTypeString != null)
					eventTypeString = eventTypeString.trim();

				if (eventTypeString.equals("AggregationEvent"))
					toGetAggregationEvent = true;
				else if (eventTypeString.equals("ObjectEvent"))
					toGetObjectEvent = true;
				else if (eventTypeString.equals("QuantityEvent"))
					toGetQuantityEvent = true;
				else if (eventTypeString.equals("TransactionEvent"))
					toGetTransactionEvent = true;
				else if (eventTypeString.equals("TransformationEvent"))
					toGetTransformationEvent = true;
			}
		}

		if (toGetAggregationEvent == true) {
			// Criteria
			Criteria criteria = new Criteria();
			// Make Query
			org.springframework.data.mongodb.core.query.Query searchQuery = new org.springframework.data.mongodb.core.query.Query(
					criteria);

			// Query
			List<AggregationEventType> aggregationEvents = mongoOperation.find(
					searchQuery, AggregationEventType.class);

			// Adding Query Result after converting DBObject to JAXB
			for (int j = 0; j < aggregationEvents.size(); j++) {
				AggregationEventType aggregationEvent = aggregationEvents
						.get(j);
				JAXBElement element = new JAXBElement(new QName(
						"AggregationEvent"), AggregationEventType.class,
						aggregationEvent);
				eventObjects.add(element);
			}
		}

		// For Each Event Type!
		if (toGetObjectEvent == true) {
			// Criteria
			Criteria criteria = new Criteria();
			// Make Query
			Query searchQuery = new Query(criteria);
			// Invoke Query
			List<ObjectEventType> objectEvents = mongoOperation.find(
					searchQuery, ObjectEventType.class);

			// Adding Query Result after converting DBObject to JAXB
			for (int j = 0; j < objectEvents.size(); j++) {
				ObjectEventType objectEvent = objectEvents.get(j);
				JAXBElement element = new JAXBElement(new QName("ObjectEvent"),
						ObjectEventType.class, objectEvent);
				eventObjects.add(element);
			}
		}
		if (toGetQuantityEvent == true) {
			// Criteria
			Criteria criteria = new Criteria();
			// Make Query
			Query searchQuery = new Query(criteria);

			// Query
			List<QuantityEventType> quantityEvents = mongoOperation.find(
					searchQuery, QuantityEventType.class);

			// Adding Query Result after converting DBObject to JAXB
			for (int j = 0; j < quantityEvents.size(); j++) {
				QuantityEventType quantityEvent = quantityEvents.get(j);
				JAXBElement element = new JAXBElement(
						new QName("QuantityEvent"), QuantityEventType.class,
						quantityEvent);
				eventObjects.add(element);
			}
		}
		if (toGetTransactionEvent == true) {
			// Criteria
			Criteria criteria = new Criteria();
			// Make Query
			Query searchQuery = new Query(criteria);

			// Query
			List<TransactionEventType> transactionEvents = mongoOperation.find(
					searchQuery, TransactionEventType.class);

			// Adding Query Result after converting DBObject to JAXB
			for (int j = 0; j < transactionEvents.size(); j++) {
				TransactionEventType transactionEvent = transactionEvents
						.get(j);
				JAXBElement element = new JAXBElement(new QName(
						"TransactionEvent"), TransactionEventType.class,
						transactionEvent);
				eventObjects.add(element);
			}
		}
		if (toGetTransformationEvent == true) {
			// Criteria
			Criteria criteria = new Criteria();
			// Make Query
			Query searchQuery = new Query(criteria);

			// Query
			List<TransformationEventType> transformationEvents = mongoOperation
					.find(searchQuery, TransformationEventType.class);

			// Adding Query Result after converting DBObject to JAXB
			for (int j = 0; j < transformationEvents.size(); j++) {
				TransformationEventType transformationEvent = transformationEvents
						.get(j);
				JAXBElement element = new JAXBElement(new QName(
						"TransformationEvent"), TransformationEventType.class,
						transformationEvent);
				eventObjects.add(element);
			}
		}

		StringWriter sw = new StringWriter();
		JAXB.marshal(epcisQueryDocumentType, sw);
		return sw.toString();
	}

	/**
	 * Invokes a previously defined query having the specified name, returning
	 * the results. The params argument provides the values to be used for any
	 * named parameters defined by the query.
	 * 
	 * @author jack Since var 'Query Param' is just key-value pairs this method
	 *         is better to be parameter of http servlet request
	 * @deprecated
	 */
	@SuppressWarnings("unused")
	@Override
	public QueryResults poll(String queryName, QueryParams params) {

		List<QueryParam> queryParams = params.getParam();
		// Query Parameter is just key-value pairs
		for (int i = 0; i < queryParams.size(); i++) {
			QueryParam queryParam = queryParams.get(i);
			String name = queryParam.getName();
			Object value = queryParam.getValue();
		}

		// Criteria1: Collection Level
		// List<String> eventType;

		// Time
		Time GE_eventTime;
		Time LT_eventTime;
		Time GE_recordTime;
		Time LT_recordTime;

		// Action
		List<String> EQ_action;

		// BizStep
		List<String> EQ_bizStep;

		// Disposition
		List<String> EQ_disposition;

		// Location
		List<String> EQ_readPoint;
		List<String> WD_readPoint;
		List<String> EQ_bizLocation;
		List<String> WD_bizLocation;

		// Transaction
		List<String> EQ_bizTransaction_type;

		// Source Dest Type
		List<String> EQ_source_type;
		List<String> EQ_destination_type;

		// Transformation ID
		List<String> EQ_transformationID;

		// EPC!
		List<String> MATCH_epc;
		List<String> MATCH_parentID;
		List<String> MATCH_inputEPC;
		List<String> MATCH_outputEPC;
		List<String> MATCH_anyEPC;
		List<String> MATCH_epcClass;
		List<String> MATCH_inputEPCClass;
		List<String> MATCH_outputEPCClass;
		List<String> MATCH_anyEPCClass;

		int EQ_quantity;
		int GT_quantity;
		int GE_quantity;
		int LT_quantity;
		int LE_quantity;

		Object EQ_fieldname; // List<String> , int, Float, Time
		Object GT_fieldname; // int, Float, Time,
		Object GE_fieldname; // int, Float, Time,
		Object LT_fieldname; // int, Float, Time,
		Object LE_fieldname; // int, Float, Time,
		Object EQ_ILMD_fieldname; // List<String> , int, Float, Time,
		Object GT_ILMD_fieldname; // int, Float, Time,
		Object GE_ILMD_fieldname; // int, Float, Time,
		Object LT_ILMD_fieldname; // int, Float, Time,
		Object LE_ILMD_fieldname; // Lint, Float, Time,

		Void EXIST_fieldname;
		Void EXIST_ILMD_fieldname;

		List<String> HASATTR_fieldname;
		List<String> EQATTR_fieldname_attrname;

		String orderBy;
		String orderDirection;
		int eventCountLimit;
		int maxEventCount;

		return null;
	}

	/**
	 * [REST Version of getQueryNames] Returns a list of all query names
	 * available for use with the subscribe and poll methods. This includes all
	 * pre- defined queries provided by the implementation, including those
	 * specified in Section 8.2.7.
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
	 */
	@Override
	public List<String> getQueryNames() {
		List<String> queryNames = new ArrayList<String>();
		queryNames.add("SimpleEventQuery");
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
	 */
	@Override
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
	 */
	@Override
	@RequestMapping(value = "/VendorVersion", method = RequestMethod.GET)
	@ResponseBody
	public String getVendorVersion() {
		// It is not a version of Vendor
		return null;
	}

	private EPCISQueryDocumentType makeBaseResultDocument(String queryName) {
		// Make Base Result Document
		EPCISQueryDocumentType epcisQueryDocumentType = new EPCISQueryDocumentType();
		EPCISQueryBodyType epcisBody = new EPCISQueryBodyType();
		epcisQueryDocumentType.setEPCISBody(epcisBody);
		QueryResults queryResults = new QueryResults();
		queryResults.setQueryName(queryName);
		epcisBody.setQueryResults(queryResults);
		QueryResultsBody queryResultsBody = new QueryResultsBody();
		queryResults.setResultsBody(queryResultsBody);
		EventListType eventListType = new EventListType();
		queryResultsBody.setEventList(eventListType);
		// Object instanceof JAXBElement
		List<Object> eventObjects = new ArrayList<Object>();
		eventListType
				.setObjectEventOrAggregationEventOrQuantityEvent(eventObjects);
		return epcisQueryDocumentType;
	}
}
