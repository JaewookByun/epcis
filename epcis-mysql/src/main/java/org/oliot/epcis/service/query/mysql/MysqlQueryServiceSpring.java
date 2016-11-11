package org.oliot.epcis.service.query.mysql;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.apache.log4j.Level;
import org.json.JSONArray;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.epcis.service.subscription.MysqlSubscription;
import org.oliot.epcis.service.subscription.MysqlSubscriptionTask;
import org.oliot.model.epcis.AggregationEventType;
import org.oliot.model.epcis.EPCISQueryBodyType;
import org.oliot.model.epcis.EPCISQueryDocumentType;
import org.oliot.model.epcis.EventListType;
import org.oliot.model.epcis.InvalidURIException;
import org.oliot.model.epcis.ObjectEventType;
import org.oliot.model.epcis.QuantityEventType;
import org.oliot.model.epcis.QueryParam;
import org.oliot.model.epcis.QueryParameterException;
import org.oliot.model.epcis.QueryParams;
import org.oliot.model.epcis.QueryResults;
import org.oliot.model.epcis.QueryResultsBody;
import org.oliot.model.epcis.QueryTooLargeException;
import org.oliot.model.epcis.SubscribeNotPermittedException;
import org.oliot.model.epcis.SubscriptionControls;
import org.oliot.model.epcis.SubscriptionControlsException;
import org.oliot.model.epcis.SubscriptionType;
import org.oliot.model.epcis.TransactionEventType;
import org.oliot.model.epcis.TransformationEventType;
import org.oliot.model.epcis.VocabularyListType;
import org.oliot.model.epcis.VocabularyType;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.PathVariable;

import static org.quartz.TriggerKey.*;
import static org.quartz.JobKey.*;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Currently Not Used
 * SpringData-Mongo currently does not support Sub-collection query
 * @author jack
 *
 */
public class MysqlQueryServiceSpring {

	public String subscribe(SubscriptionType subscription) {
		String queryName = subscription.getPollParameters().getQueryName();
		String subscriptionID = subscription.getSubscriptionID();
		String dest = subscription.getDest();
		//String cronExpression = subscription.getCronExpression();
		boolean reportIfEmpty = subscription.getReportIfEmpty();
		String eventType = subscription.getPollParameters().getEventType();
		String GE_eventTime = subscription.getPollParameters().getGE_eventTime();
		String LT_eventTime = subscription.getPollParameters().getLT_eventTime();
		String GE_recordTime = subscription.getPollParameters().getGE_recordTime();
		String LT_recordTime = subscription.getPollParameters().getLT_recordTime();
		String EQ_action = subscription.getPollParameters().getEQ_action();
		String EQ_bizStep = subscription.getPollParameters().getEQ_bizStep();
		String EQ_disposition = subscription.getPollParameters().getEQ_disposition();
		String EQ_readPoint = subscription.getPollParameters().getEQ_readPoint();
		String WD_readPoint = subscription.getPollParameters().getWD_readPoint();
		String EQ_bizLocation = subscription.getPollParameters().getEQ_bizLocation();
		String WD_bizLocation = subscription.getPollParameters().getWD_bizLocation();
		String EQ_transformationID = subscription.getPollParameters().getEQ_transformationID();
		String MATCH_epc = subscription.getPollParameters().getMATCH_epc();
		String MATCH_parentID = subscription.getPollParameters().getMATCH_parentID();
		String MATCH_inputEPC = subscription.getPollParameters().getMATCH_inputEPC();
		String MATCH_outputEPC = subscription.getPollParameters().getMATCH_outputEPC();
		String MATCH_anyEPC = subscription.getPollParameters().getMATCH_anyEPC();
		String MATCH_epcClass = subscription.getPollParameters().getMATCH_epcClass();
		String MATCH_inputEPCClass = subscription.getPollParameters().getMATCH_inputEPCClass();
		String MATCH_outputEPCClass = subscription.getPollParameters().getMATCH_outputEPCClass();
		String MATCH_anyEPCClass = subscription.getPollParameters().getMATCH_anyEPCClass();
		String EQ_quantity = subscription.getPollParameters().getEQ_quantity().toString();
		String GT_quantity = subscription.getPollParameters().getGT_quantity().toString();
		String GE_quantity = subscription.getPollParameters().getGE_quantity().toString();
		String LT_quantity = subscription.getPollParameters().getLT_quantity().toString();
		String LE_quantity = subscription.getPollParameters().getLE_quantity().toString();
		String orderBy = subscription.getPollParameters().getOrderBy();
		String orderDirection = subscription.getPollParameters().getOrderDirection();
		String eventCountLimit = subscription.getPollParameters().getEventCountLimit().toString();
		String maxEventCount = subscription.getPollParameters().getMaxEventCount().toString();
		Map<String, String> paramMap = subscription.getPollParameters().getParams();

		String result = subscribe(queryName, subscriptionID, dest,
				"cronExpression", reportIfEmpty, eventType, GE_eventTime,
				LT_eventTime, GE_recordTime, LT_recordTime, EQ_action,
				EQ_bizStep, EQ_disposition, EQ_readPoint, WD_readPoint,
				EQ_bizLocation, WD_bizLocation, EQ_transformationID, MATCH_epc,
				MATCH_parentID, MATCH_inputEPC, MATCH_outputEPC, MATCH_anyEPC,
				MATCH_epcClass, MATCH_inputEPCClass, MATCH_outputEPCClass,
				MATCH_anyEPCClass, EQ_quantity, GT_quantity, GE_quantity,
				LT_quantity, LE_quantity, orderBy, orderDirection,
				eventCountLimit, maxEventCount, paramMap);

		return result;
	}

	public String subscribeEventQuery(String queryName, String subscriptionID,
			String dest, String cronExpression, String eventType,
			String GE_eventTime, String LT_eventTime, String GE_recordTime,
			String LT_recordTime, String EQ_action, String EQ_bizStep,
			String EQ_disposition, String EQ_readPoint, String WD_readPoint,
			String EQ_bizLocation, String WD_bizLocation,
			String EQ_transformationID, String MATCH_epc,
			String MATCH_parentID, String MATCH_inputEPC,
			String MATCH_outputEPC, String MATCH_anyEPC, String MATCH_epcClass,
			String MATCH_inputEPCClass, String MATCH_outputEPCClass,
			String MATCH_anyEPCClass, String EQ_quantity, String GT_quantity,
			String GE_quantity, String LT_quantity, String LE_quantity,
			String orderBy, String orderDirection, String eventCountLimit,
			String maxEventCount, Map<String, String> paramMap) {

		// M27 - query params' constraint
		// M39 - query params' constraint
		String reason = checkConstraintSimpleEventQuery(queryName, eventType,
				GE_eventTime, LT_eventTime, GE_recordTime, LT_recordTime,
				EQ_action, EQ_bizStep, EQ_disposition, EQ_readPoint,
				WD_readPoint, EQ_bizLocation, WD_bizLocation,
				EQ_transformationID, MATCH_epc, MATCH_parentID, MATCH_inputEPC,
				MATCH_outputEPC, MATCH_anyEPC, MATCH_epcClass,
				MATCH_inputEPCClass, MATCH_outputEPCClass, MATCH_anyEPCClass,
				EQ_quantity, GT_quantity, GE_quantity, LT_quantity,
				LE_quantity, orderBy, orderDirection, eventCountLimit,
				maxEventCount, paramMap);
		if (reason != null) {
			return makeErrorResult(reason, QueryParameterException.class);
		}

		// cron Example
		// 0/10 * * * * ? : every 10 second

		// M30
		try {
			cronSchedule(cronExpression);
		} catch (RuntimeException e) {
			return makeErrorResult(e.toString(),
					SubscriptionControlsException.class);
		}

		// Add Schedule with Query
		addScheduleToQuartz(queryName, subscriptionID, dest, cronExpression,
				eventType, GE_eventTime, LT_eventTime, GE_recordTime,
				LT_recordTime, EQ_action, EQ_bizStep, EQ_disposition,
				EQ_readPoint, WD_readPoint, EQ_bizLocation, WD_bizLocation,
				EQ_transformationID, MATCH_epc, MATCH_parentID, MATCH_inputEPC,
				MATCH_outputEPC, MATCH_anyEPC, MATCH_epcClass,
				MATCH_inputEPCClass, MATCH_outputEPCClass, MATCH_anyEPCClass,
				EQ_quantity, GT_quantity, GE_quantity, LT_quantity,
				LE_quantity, orderBy, orderDirection, eventCountLimit,
				maxEventCount, paramMap);

		// Manage Subscription Persistently
		addScheduleToDB(queryName, subscriptionID, dest, cronExpression,
				eventType, GE_eventTime, LT_eventTime, GE_recordTime,
				LT_recordTime, EQ_action, EQ_bizStep, EQ_disposition,
				EQ_readPoint, WD_readPoint, EQ_bizLocation, WD_bizLocation,
				EQ_transformationID, MATCH_epc, MATCH_parentID, MATCH_inputEPC,
				MATCH_outputEPC, MATCH_anyEPC, MATCH_epcClass,
				MATCH_inputEPCClass, MATCH_outputEPCClass, MATCH_anyEPCClass,
				EQ_quantity, GT_quantity, GE_quantity, LT_quantity,
				LE_quantity, orderBy, orderDirection, eventCountLimit,
				maxEventCount, paramMap);

		String retString = "SubscriptionID : " + subscriptionID
				+ " is successfully triggered. ";
		return retString;
	}

	// Soap Query Adaptor
	public void subscribe(String queryName, QueryParams params, URI dest,
			SubscriptionControls controls, String subscriptionID) {

		List<QueryParam> queryParamList = params.getParam();
		String destStr = dest.toString();

		String eventType = null;
		String GE_eventTime = null;
		String LT_eventTime = null;
		String GE_recordTime = null;
		String LT_recordTime = null;
		String EQ_action = null;
		String EQ_bizStep = null;
		String EQ_disposition = null;
		String EQ_readPoint = null;
		String WD_readPoint = null;
		String EQ_bizLocation = null;
		String WD_bizLocation = null;
		String EQ_transformationID = null;
		String MATCH_epc = null;
		String MATCH_parentID = null;
		String MATCH_inputEPC = null;
		String MATCH_outputEPC = null;
		String MATCH_anyEPC = null;
		String MATCH_epcClass = null;
		String MATCH_inputEPCClass = null;
		String MATCH_outputEPCClass = null;
		String MATCH_anyEPCClass = null;
		String EQ_quantity = null;
		String GT_quantity = null;
		String GE_quantity = null;
		String LT_quantity = null;
		String LE_quantity = null;
		String orderBy = null;
		String orderDirection = null;
		String eventCountLimit = null;
		String maxEventCount = null;
		String cronExpression = null;
		boolean reportIfEmpty = false;

		Map<String, String> extMap = new HashMap<String, String>();
		for (int i = 0; i < queryParamList.size(); i++) {

			QueryParam qp = queryParamList.get(i);
			String name = qp.getName();
			String value = (String) qp.getValue();

			if (name.equals("cronExpression")) {
				cronExpression = value;
				continue;
			} else if (name.equals("reportIfEmpty")) {
				if (value.equals("true"))
					reportIfEmpty = true;
				else
					reportIfEmpty = false;
				continue;
			} else if (name.equals("eventType")) {
				eventType = value;
				continue;
			} else if (name.equals("GE_eventTime")) {
				GE_eventTime = value;
				continue;
			} else if (name.equals("LT_eventTime")) {
				LT_eventTime = value;
				continue;
			} else if (name.equals("GE_recordTime")) {
				GE_recordTime = value;
				continue;
			} else if (name.equals("LT_recordTime")) {
				LT_recordTime = value;
				continue;
			} else if (name.equals("EQ_action")) {
				EQ_action = value;
				continue;
			} else if (name.equals("EQ_bizStep")) {
				EQ_bizStep = value;
				continue;
			} else if (name.equals("EQ_disposition")) {
				EQ_disposition = value;
				continue;
			} else if (name.equals("EQ_readPoint")) {
				EQ_readPoint = value;
				continue;
			} else if (name.equals("WD_readPoint")) {
				WD_readPoint = value;
				continue;
			} else if (name.equals("EQ_bizLocation")) {
				EQ_bizLocation = value;
				continue;
			} else if (name.equals("WD_bizLocation")) {
				WD_bizLocation = value;
				continue;
			} else if (name.equals("EQ_transformationID")) {
				EQ_transformationID = value;
				continue;
			} else if (name.equals("MATCH_epc")) {
				MATCH_epc = value;
				continue;
			} else if (name.equals("MATCH_parentID")) {
				MATCH_parentID = value;
				continue;
			} else if (name.equals("MATCH_inputEPC")) {
				MATCH_inputEPC = value;
				continue;
			} else if (name.equals("MATCH_outputEPC")) {
				MATCH_outputEPC = value;
				continue;
			} else if (name.equals("MATCH_anyEPC")) {
				MATCH_anyEPC = value;
				continue;
			} else if (name.equals("MATCH_epcClass")) {
				MATCH_epcClass = value;
				continue;
			} else if (name.equals("MATCH_inputEPCClass")) {
				MATCH_inputEPCClass = value;
				continue;
			} else if (name.equals("MATCH_outputEPCClass")) {
				MATCH_outputEPCClass = value;
				continue;
			} else if (name.equals("MATCH_anyEPCClass")) {
				MATCH_anyEPCClass = value;
				continue;
			} else if (name.equals("EQ_quantity")) {
				EQ_quantity = value;
				continue;
			} else if (name.equals("GT_quantity")) {
				GT_quantity = value;
				continue;
			} else if (name.equals("GE_quantity")) {
				GE_quantity = value;
				continue;
			} else if (name.equals("LT_quantity")) {
				LT_quantity = value;
				continue;
			} else if (name.equals("LE_quantity")) {
				LE_quantity = value;
				continue;
			} else if (name.equals("orderBy")) {
				orderBy = value;
				continue;
			} else if (name.equals("orderDirection")) {
				orderDirection = value;
				continue;
			} else if (name.equals("eventCountLimit")) {
				eventCountLimit = value;
				continue;
			} else if (name.equals("maxEventCount")) {
				maxEventCount = value;
				continue;
			} else {
				extMap.put(name, value);
			}
		}

		subscribe(queryName, subscriptionID, destStr, cronExpression,
				reportIfEmpty, eventType, GE_eventTime, LT_eventTime,
				GE_recordTime, LT_recordTime, EQ_action, EQ_bizStep,
				EQ_disposition, EQ_readPoint, WD_readPoint, EQ_bizLocation,
				WD_bizLocation, EQ_transformationID, MATCH_epc, MATCH_parentID,
				MATCH_inputEPC, MATCH_outputEPC, MATCH_anyEPC, MATCH_epcClass,
				MATCH_inputEPCClass, MATCH_outputEPCClass, MATCH_anyEPCClass,
				EQ_quantity, GT_quantity, GE_quantity, LT_quantity,
				LE_quantity, orderBy, orderDirection, eventCountLimit,
				maxEventCount, extMap);

	}

	public String subscribe(String queryName, String subscriptionID,
			String dest, String cronExpression, boolean reportIfEmpty,
			String eventType, String GE_eventTime, String LT_eventTime,
			String GE_recordTime, String LT_recordTime, String EQ_action,
			String EQ_bizStep, String EQ_disposition, String EQ_readPoint,
			String WD_readPoint, String EQ_bizLocation, String WD_bizLocation,
			String EQ_transformationID, String MATCH_epc,
			String MATCH_parentID, String MATCH_inputEPC,
			String MATCH_outputEPC, String MATCH_anyEPC, String MATCH_epcClass,
			String MATCH_inputEPCClass, String MATCH_outputEPCClass,
			String MATCH_anyEPCClass, String EQ_quantity, String GT_quantity,
			String GE_quantity, String LT_quantity, String LE_quantity,
			String orderBy, String orderDirection, String eventCountLimit,
			String maxEventCount, Map<String, String> paramMap) {

		// M20 : Throw an InvalidURIException for an incorrect dest argument in
		// the subscribe method in EPCIS Query Control Interface
		try {
			new URL(dest);
		} catch (MalformedURLException e) {
			return makeErrorResult(e.toString(), InvalidURIException.class);
		}

		// M24 : Virtual Error Handling
		// Automatically processed by URI param
		if (dest == null || cronExpression == null) {
			return makeErrorResult(
					"Fill the mandatory field in subscribe method",
					QueryParameterException.class);
		}

		// M46
		if (queryName.equals("SimpleMasterDataQuery")) {
			return makeErrorResult(
					"SimpleMasterDataQuery is not available in subscription method",
					SubscribeNotPermittedException.class);
		}

		String retString = "";
		if (queryName.equals("SimpleEventQuery")) {
			retString = subscribeEventQuery(queryName, subscriptionID, dest,
					cronExpression, eventType, GE_eventTime, LT_eventTime,
					GE_recordTime, LT_recordTime, EQ_action, EQ_bizStep,
					EQ_disposition, EQ_readPoint, WD_readPoint, EQ_bizLocation,
					WD_bizLocation, EQ_transformationID, MATCH_epc,
					MATCH_parentID, MATCH_inputEPC, MATCH_outputEPC,
					MATCH_anyEPC, MATCH_epcClass, MATCH_inputEPCClass,
					MATCH_outputEPCClass, MATCH_anyEPCClass, EQ_quantity,
					GT_quantity, GE_quantity, LT_quantity, LE_quantity,
					orderBy, orderDirection, eventCountLimit, maxEventCount,
					paramMap);
		}

		return retString;
	}

	public void unsubscribe(String subscriptionID) {
		ApplicationContext ctx = new GenericXmlApplicationContext(
				"classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx
				.getBean("mongoTemplate");

		// Its size should be 0 or 1
		List<SubscriptionType> subscriptions = mongoOperation.find(new Query(
				Criteria.where("subscriptionID").is(subscriptionID)),
				SubscriptionType.class);

		for (int i = 0; i < subscriptions.size(); i++) {
			SubscriptionType subscription = subscriptions.get(i);
			// Remove from current Quartz
			removeScheduleFromQuartz(subscription);
			// Remove from DB list
			removeScheduleFromDB(mongoOperation, subscription);
		}
		((AbstractApplicationContext) ctx).close();
	}

	public String getSubscriptionIDsREST(@PathVariable String queryName) {

		ApplicationContext ctx = new GenericXmlApplicationContext(
				"classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx
				.getBean("mongoTemplate");

		List<SubscriptionType> allSubscription = mongoOperation.find(new Query(
				Criteria.where("queryName").is(queryName)),
				SubscriptionType.class);

		JSONArray retArray = new JSONArray();
		for (int i = 0; i < allSubscription.size(); i++) {
			SubscriptionType subscription = allSubscription.get(i);
			retArray.put(subscription.getSubscriptionID());
		}
		((AbstractApplicationContext) ctx).close();
		return retArray.toString(1);
	}

	public List<String> getSubscriptionIDs(String queryName) {
		ApplicationContext ctx = new GenericXmlApplicationContext(
				"classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx
				.getBean("mongoTemplate");

		List<SubscriptionType> allSubscription = mongoOperation.find(new Query(
				Criteria.where("queryName").is(queryName)),
				SubscriptionType.class);
		List<String> retList = new ArrayList<String>();
		for (int i = 0; i < allSubscription.size(); i++) {
			SubscriptionType subscription = allSubscription.get(i);
			retList.add(subscription.getSubscriptionID());
		}
		((AbstractApplicationContext) ctx).close();
		return retList;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String pollEventQuery(String queryName, String eventType,
			String GE_eventTime, String LT_eventTime, String GE_recordTime,
			String LT_recordTime, String EQ_action, String EQ_bizStep,
			String EQ_disposition, String EQ_readPoint, String WD_readPoint,
			String EQ_bizLocation, String WD_bizLocation,
			String EQ_transformationID, String MATCH_epc,
			String MATCH_parentID, String MATCH_inputEPC,
			String MATCH_outputEPC, String MATCH_anyEPC, String MATCH_epcClass,
			String MATCH_inputEPCClass, String MATCH_outputEPCClass,
			String MATCH_anyEPCClass, String EQ_quantity, String GT_quantity,
			String GE_quantity, String LT_quantity, String LE_quantity,
			String orderBy, String orderDirection, String eventCountLimit,
			String maxEventCount, Map<String, String> paramMap) {

		// M27 - query params' constraint
		// M39 - query params' constraint
		String reason = checkConstraintSimpleEventQuery(queryName, eventType,
				GE_eventTime, LT_eventTime, GE_recordTime, LT_recordTime,
				EQ_action, EQ_bizStep, EQ_disposition, EQ_readPoint,
				WD_readPoint, EQ_bizLocation, WD_bizLocation,
				EQ_transformationID, MATCH_epc, MATCH_parentID, MATCH_inputEPC,
				MATCH_outputEPC, MATCH_anyEPC, MATCH_epcClass,
				MATCH_inputEPCClass, MATCH_outputEPCClass, MATCH_anyEPCClass,
				EQ_quantity, GT_quantity, GE_quantity, LT_quantity,
				LE_quantity, orderBy, orderDirection, eventCountLimit,
				maxEventCount, paramMap);
		if (reason != null) {
			return makeErrorResult(reason, QueryParameterException.class);
		}

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

		/**
		 * EventType : If specified, the result will only include events whose
		 * type matches one of the types specified in the parameter value. Each
		 * element of the parameter value may be one of the following strings:
		 * ObjectEvent, AggregationEvent, QuantityEvent, TransactionEvent, or
		 * TransformationEvent. An element of the parameter value may also be
		 * the name of an extension event type. If omitted, all event types will
		 * be considered for inclusion in the result.
		 */
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
			List<Criteria> criteriaList = makeCriteria("AggregationEvent",
					GE_eventTime, LT_eventTime, GE_recordTime, LT_recordTime,
					EQ_action, EQ_bizStep, EQ_disposition, EQ_readPoint,
					WD_readPoint, EQ_bizLocation, WD_bizLocation,
					EQ_transformationID, MATCH_epc, MATCH_parentID,
					MATCH_inputEPC, MATCH_outputEPC, MATCH_anyEPC,
					MATCH_epcClass, MATCH_inputEPCClass, MATCH_outputEPCClass,
					MATCH_anyEPCClass, EQ_quantity, GT_quantity, GE_quantity,
					LT_quantity, LE_quantity, orderBy, orderDirection,
					eventCountLimit, maxEventCount, paramMap);

			// Make Query
			Query searchQuery = new Query();
			for (int i = 0; i < criteriaList.size(); i++) {
				searchQuery.addCriteria(criteriaList.get(i));
			}

			// Sort and Limit Query
			searchQuery = makeSortAndLimitQuery(searchQuery, orderBy,
					orderDirection, eventCountLimit, maxEventCount);

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
			List<Criteria> criteriaList = makeCriteria("ObjectEvent",
					GE_eventTime, LT_eventTime, GE_recordTime, LT_recordTime,
					EQ_action, EQ_bizStep, EQ_disposition, EQ_readPoint,
					WD_readPoint, EQ_bizLocation, WD_bizLocation,
					EQ_transformationID, MATCH_epc, MATCH_parentID,
					MATCH_inputEPC, MATCH_outputEPC, MATCH_anyEPC,
					MATCH_epcClass, MATCH_inputEPCClass, MATCH_outputEPCClass,
					MATCH_anyEPCClass, EQ_quantity, GT_quantity, GE_quantity,
					LT_quantity, LE_quantity, orderBy, orderDirection,
					eventCountLimit, maxEventCount, paramMap);

			// Make Query
			Query searchQuery = new Query();
			for (int i = 0; i < criteriaList.size(); i++) {
				searchQuery.addCriteria(criteriaList.get(i));
			}

			// Sort and Limit Query
			searchQuery = makeSortAndLimitQuery(searchQuery, orderBy,
					orderDirection, eventCountLimit, maxEventCount);

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
			List<Criteria> criteriaList = makeCriteria("QuantityEvent",
					GE_eventTime, LT_eventTime, GE_recordTime, LT_recordTime,
					EQ_action, EQ_bizStep, EQ_disposition, EQ_readPoint,
					WD_readPoint, EQ_bizLocation, WD_bizLocation,
					EQ_transformationID, MATCH_epc, MATCH_parentID,
					MATCH_inputEPC, MATCH_outputEPC, MATCH_anyEPC,
					MATCH_epcClass, MATCH_inputEPCClass, MATCH_outputEPCClass,
					MATCH_anyEPCClass, EQ_quantity, GT_quantity, GE_quantity,
					LT_quantity, LE_quantity, orderBy, orderDirection,
					eventCountLimit, maxEventCount, paramMap);

			// Make Query
			Query searchQuery = new Query();
			for (int i = 0; i < criteriaList.size(); i++) {
				searchQuery.addCriteria(criteriaList.get(i));
			}

			// Sort and Limit Query
			searchQuery = makeSortAndLimitQuery(searchQuery, orderBy,
					orderDirection, eventCountLimit, maxEventCount);

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
			List<Criteria> criteriaList = makeCriteria("TransactionEvent",
					GE_eventTime, LT_eventTime, GE_recordTime, LT_recordTime,
					EQ_action, EQ_bizStep, EQ_disposition, EQ_readPoint,
					WD_readPoint, EQ_bizLocation, WD_bizLocation,
					EQ_transformationID, MATCH_epc, MATCH_parentID,
					MATCH_inputEPC, MATCH_outputEPC, MATCH_anyEPC,
					MATCH_epcClass, MATCH_inputEPCClass, MATCH_outputEPCClass,
					MATCH_anyEPCClass, EQ_quantity, GT_quantity, GE_quantity,
					LT_quantity, LE_quantity, orderBy, orderDirection,
					eventCountLimit, maxEventCount, paramMap);

			// Make Query
			Query searchQuery = new Query();
			for (int i = 0; i < criteriaList.size(); i++) {
				searchQuery.addCriteria(criteriaList.get(i));
			}

			// Sort and Limit Query
			searchQuery = makeSortAndLimitQuery(searchQuery, orderBy,
					orderDirection, eventCountLimit, maxEventCount);

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
			List<Criteria> criteriaList = makeCriteria("TransformationEvent",
					GE_eventTime, LT_eventTime, GE_recordTime, LT_recordTime,
					EQ_action, EQ_bizStep, EQ_disposition, EQ_readPoint,
					WD_readPoint, EQ_bizLocation, WD_bizLocation,
					EQ_transformationID, MATCH_epc, MATCH_parentID,
					MATCH_inputEPC, MATCH_outputEPC, MATCH_anyEPC,
					MATCH_epcClass, MATCH_inputEPCClass, MATCH_outputEPCClass,
					MATCH_anyEPCClass, EQ_quantity, GT_quantity, GE_quantity,
					LT_quantity, LE_quantity, orderBy, orderDirection,
					eventCountLimit, maxEventCount, paramMap);

			// Make Query
			Query searchQuery = new Query();
			for (int i = 0; i < criteriaList.size(); i++) {
				searchQuery.addCriteria(criteriaList.get(i));
			}

			// Sort and Limit Query
			searchQuery = makeSortAndLimitQuery(searchQuery, orderBy,
					orderDirection, eventCountLimit, maxEventCount);

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

		// M44
		if (maxEventCount != null) {
			if (eventObjects.size() > Integer.parseInt(maxEventCount)) {
				((AbstractApplicationContext) ctx).close();
				return makeErrorResult("Violate maxEventCount",
						QueryTooLargeException.class);
			}
		}
		((AbstractApplicationContext) ctx).close();
		StringWriter sw = new StringWriter();
		JAXB.marshal(epcisQueryDocumentType, sw);
		return sw.toString();
	}

	public String pollMasterDataQuery(String queryName, String vocabularyName,
			boolean includeAttributes, boolean includeChildren,
			String attributeNames, String eQ_name, String wD_name,
			String hASATTR, String maxElementCount) {

		// Make Base Result Document
		EPCISQueryDocumentType epcisQueryDocumentType = makeBaseResultDocument(queryName);

		ApplicationContext ctx = new GenericXmlApplicationContext(
				"classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx
				.getBean("mongoTemplate");

		List<Criteria> criteriaList = makeCritera(vocabularyName,
				includeAttributes, includeChildren, attributeNames, eQ_name,
				wD_name, hASATTR, maxElementCount);

		Query query = new Query();
		for (int i = 0; i < criteriaList.size(); i++) {
			query.addCriteria(criteriaList.get(i));
		}

		List<VocabularyType> vList = mongoOperation.find(query,
				VocabularyType.class);

		QueryResultsBody qbt = epcisQueryDocumentType.getEPCISBody()
				.getQueryResults().getResultsBody();

		VocabularyListType vlt = new VocabularyListType();
		vlt.setVocabulary(vList);
		qbt.setVocabularyList(vlt);

		((AbstractApplicationContext) ctx).close();

		// M47
		if (maxElementCount != null) {
			try {
				int maxElement = Integer.parseInt(maxElementCount);
				if (vList.size() > maxElement) {
					return makeErrorResult("Too Large Master Data result",
							QueryTooLargeException.class);
				}
			} catch (NumberFormatException e) {

			}
		}

		StringWriter sw = new StringWriter();
		JAXB.marshal(epcisQueryDocumentType, sw);
		return sw.toString();
	}

	// Soap Service Adaptor
	public String poll(String queryName, QueryParams queryParams) {
		List<QueryParam> queryParamList = queryParams.getParam();

		String eventType = null;
		String GE_eventTime = null;
		String LT_eventTime = null;
		String GE_recordTime = null;
		String LT_recordTime = null;
		String EQ_action = null;
		String EQ_bizStep = null;
		String EQ_disposition = null;
		String EQ_readPoint = null;
		String WD_readPoint = null;
		String EQ_bizLocation = null;
		String WD_bizLocation = null;
		String EQ_transformationID = null;
		String MATCH_epc = null;
		String MATCH_parentID = null;
		String MATCH_inputEPC = null;
		String MATCH_outputEPC = null;
		String MATCH_anyEPC = null;
		String MATCH_epcClass = null;
		String MATCH_inputEPCClass = null;
		String MATCH_outputEPCClass = null;
		String MATCH_anyEPCClass = null;
		String EQ_quantity = null;
		String GT_quantity = null;
		String GE_quantity = null;
		String LT_quantity = null;
		String LE_quantity = null;
		String orderBy = null;
		String orderDirection = null;
		String eventCountLimit = null;
		String maxEventCount = null;
		String vocabularyName = null;
		boolean includeAttributes = false;
		boolean includeChildren = false;
		String attributeNames = null;
		String EQ_name = null;
		String WD_name = null;
		String HASATTR = null;
		String maxElementCount = null;
		Map<String, String> extMap = new HashMap<String, String>();
		for (int i = 0; i < queryParamList.size(); i++) {

			QueryParam qp = queryParamList.get(i);
			String name = qp.getName();
			String value = (String) qp.getValue();

			if (name.equals("eventType")) {
				eventType = value;
				continue;
			} else if (name.equals("GE_eventTime")) {
				GE_eventTime = value;
				continue;
			} else if (name.equals("LT_eventTime")) {
				LT_eventTime = value;
				continue;
			} else if (name.equals("GE_recordTime")) {
				GE_recordTime = value;
				continue;
			} else if (name.equals("LT_recordTime")) {
				LT_recordTime = value;
				continue;
			} else if (name.equals("EQ_action")) {
				EQ_action = value;
				continue;
			} else if (name.equals("EQ_bizStep")) {
				EQ_bizStep = value;
				continue;
			} else if (name.equals("EQ_disposition")) {
				EQ_disposition = value;
				continue;
			} else if (name.equals("EQ_readPoint")) {
				EQ_readPoint = value;
				continue;
			} else if (name.equals("WD_readPoint")) {
				WD_readPoint = value;
				continue;
			} else if (name.equals("EQ_bizLocation")) {
				EQ_bizLocation = value;
				continue;
			} else if (name.equals("WD_bizLocation")) {
				WD_bizLocation = value;
				continue;
			} else if (name.equals("EQ_transformationID")) {
				EQ_transformationID = value;
				continue;
			} else if (name.equals("MATCH_epc")) {
				MATCH_epc = value;
				continue;
			} else if (name.equals("MATCH_parentID")) {
				MATCH_parentID = value;
				continue;
			} else if (name.equals("MATCH_inputEPC")) {
				MATCH_inputEPC = value;
				continue;
			} else if (name.equals("MATCH_outputEPC")) {
				MATCH_outputEPC = value;
				continue;
			} else if (name.equals("MATCH_anyEPC")) {
				MATCH_anyEPC = value;
				continue;
			} else if (name.equals("MATCH_epcClass")) {
				MATCH_epcClass = value;
				continue;
			} else if (name.equals("MATCH_inputEPCClass")) {
				MATCH_inputEPCClass = value;
				continue;
			} else if (name.equals("MATCH_outputEPCClass")) {
				MATCH_outputEPCClass = value;
				continue;
			} else if (name.equals("MATCH_anyEPCClass")) {
				MATCH_anyEPCClass = value;
				continue;
			} else if (name.equals("EQ_quantity")) {
				EQ_quantity = value;
				continue;
			} else if (name.equals("GT_quantity")) {
				GT_quantity = value;
				continue;
			} else if (name.equals("GE_quantity")) {
				GE_quantity = value;
				continue;
			} else if (name.equals("LT_quantity")) {
				LT_quantity = value;
				continue;
			} else if (name.equals("LE_quantity")) {
				LE_quantity = value;
				continue;
			} else if (name.equals("orderBy")) {
				orderBy = value;
				continue;
			} else if (name.equals("orderDirection")) {
				orderDirection = value;
				continue;
			} else if (name.equals("eventCountLimit")) {
				eventCountLimit = value;
				continue;
			} else if (name.equals("maxEventCount")) {
				maxEventCount = value;
				continue;
			} else if (name.equals("vocabularyName")) {
				vocabularyName = value;
				continue;
			} else if (name.equals("includeAttributes")) {
				if (value.equals("true"))
					includeAttributes = true;
				else
					includeAttributes = false;
				continue;
			} else if (name.equals("includeChildren")) {
				if (value.equals("true"))
					includeChildren = true;
				else
					includeChildren = false;
				continue;
			} else if (name.equals("attributeNames")) {
				attributeNames = value;
				continue;
			} else if (name.equals("EQ_name")) {
				EQ_name = value;
				continue;
			} else if (name.equals("WD_name")) {
				WD_name = value;
				continue;
			} else if (name.equals("HASATTR")) {
				HASATTR = value;
				continue;
			} else if (name.equals("maxElementCount")) {
				maxElementCount = value;
				continue;
			} else {
				extMap.put(name, value);
			}
		}

		return poll(queryName, eventType, GE_eventTime, LT_eventTime,
				GE_recordTime, LT_recordTime, EQ_action, EQ_bizStep,
				EQ_disposition, EQ_readPoint, WD_readPoint, EQ_bizLocation,
				WD_bizLocation, EQ_transformationID, MATCH_epc, MATCH_parentID,
				MATCH_inputEPC, MATCH_outputEPC, MATCH_anyEPC, MATCH_epcClass,
				MATCH_inputEPCClass, MATCH_outputEPCClass, MATCH_anyEPCClass,
				EQ_quantity, GT_quantity, GE_quantity, LT_quantity,
				LE_quantity, orderBy, orderDirection, eventCountLimit,
				maxEventCount, vocabularyName, includeAttributes,
				includeChildren, attributeNames, EQ_name, WD_name, HASATTR,
				maxElementCount, extMap);
	}

	public String poll(@PathVariable String queryName, String eventType,
			String GE_eventTime, String LT_eventTime, String GE_recordTime,
			String LT_recordTime, String EQ_action, String EQ_bizStep,
			String EQ_disposition, String EQ_readPoint, String WD_readPoint,
			String EQ_bizLocation, String WD_bizLocation,
			String EQ_transformationID, String MATCH_epc,
			String MATCH_parentID, String MATCH_inputEPC,
			String MATCH_outputEPC, String MATCH_anyEPC, String MATCH_epcClass,
			String MATCH_inputEPCClass, String MATCH_outputEPCClass,
			String MATCH_anyEPCClass, String EQ_quantity, String GT_quantity,
			String GE_quantity, String LT_quantity, String LE_quantity,
			String orderBy, String orderDirection, String eventCountLimit,
			String maxEventCount,

			String vocabularyName, boolean includeAttributes,
			boolean includeChildren, String attributeNames, String EQ_name,
			String WD_name, String HASATTR, String maxElementCount,
			Map<String, String> paramMap) {

		// M24
		if (queryName == null) {
			// It is not possible, automatically filtered by URI param
			return makeErrorResult(
					"queryName is mandatory field in poll method",
					QueryParameterException.class);
		}

		if (queryName.equals("SimpleEventQuery"))
			return pollEventQuery(queryName, eventType, GE_eventTime,
					LT_eventTime, GE_recordTime, LT_recordTime, EQ_action,
					EQ_bizStep, EQ_disposition, EQ_readPoint, WD_readPoint,
					EQ_bizLocation, WD_bizLocation, EQ_transformationID,
					MATCH_epc, MATCH_parentID, MATCH_inputEPC, MATCH_outputEPC,
					MATCH_anyEPC, MATCH_epcClass, MATCH_inputEPCClass,
					MATCH_outputEPCClass, MATCH_anyEPCClass, EQ_quantity,
					GT_quantity, GE_quantity, LT_quantity, LE_quantity,
					orderBy, orderDirection, eventCountLimit, maxEventCount,
					paramMap);

		if (queryName.equals("SimpleMasterDataQuery"))
			return pollMasterDataQuery(queryName, vocabularyName,
					includeAttributes, includeChildren, attributeNames,
					EQ_name, WD_name, HASATTR, maxElementCount);
		return "";
	}

	private String checkConstraintSimpleEventQuery(String queryName,
			String eventType, String GE_eventTime, String LT_eventTime,
			String GE_recordTime, String LT_recordTime, String EQ_action,
			String EQ_bizStep, String EQ_disposition, String EQ_readPoint,
			String WD_readPoint, String EQ_bizLocation, String WD_bizLocation,
			String EQ_transformationID, String MATCH_epc,
			String MATCH_parentID, String MATCH_inputEPC,
			String MATCH_outputEPC, String MATCH_anyEPC, String MATCH_epcClass,
			String MATCH_inputEPCClass, String MATCH_outputEPCClass,
			String MATCH_anyEPCClass, String EQ_quantity, String GT_quantity,
			String GE_quantity, String LT_quantity, String LE_quantity,
			String orderBy, String orderDirection, String eventCountLimit,
			String maxEventCount, Map<String, String> paramMap) {

		// M27
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
			if (GE_eventTime != null)
				sdf.parse(GE_eventTime);
			if (LT_eventTime != null)
				sdf.parse(LT_eventTime);
			if (GE_recordTime != null)
				sdf.parse(GE_recordTime);
			if (LT_recordTime != null)
				sdf.parse(LT_recordTime);
		} catch (ParseException e) {
			return makeErrorResult(e.toString(), QueryParameterException.class);
		}

		// M27
		if (orderBy != null) {
			if (!orderBy.equals("eventTime") && !orderBy.equals("recordTime")) {
				return makeErrorResult(
						"orderBy should be eventTime or recordTime",
						QueryParameterException.class);
			}
			if (orderDirection != null) {
				if (!orderDirection.equals("ASC")
						&& !orderDirection.equals("DESC")) {
					return makeErrorResult(
							"orderDirection should be ASC or DESC",
							QueryParameterException.class);
				}
			}
		}

		// M27
		if (eventCountLimit != null) {
			try {
				int c = Integer.parseInt(eventCountLimit);
				if (c <= 0) {
					return makeErrorResult(
							"eventCount should be natural number",
							QueryParameterException.class);
				}
			} catch (NumberFormatException e) {
				return makeErrorResult("eventCount: " + e.toString(),
						QueryParameterException.class);
			}
		}

		// M27
		if (maxEventCount != null) {
			try {
				int c = Integer.parseInt(maxEventCount);
				if (c <= 0) {
					return makeErrorResult(
							"maxEventCount should be natural number",
							QueryParameterException.class);
				}
			} catch (NumberFormatException e) {
				return makeErrorResult("maxEventCount: " + e.toString(),
						QueryParameterException.class);
			}
		}

		// M39
		if (EQ_action != null) {
			if (!EQ_action.equals("ADD") && !EQ_action.equals("OBSERVE")
					&& !EQ_action.equals("DELETE")) {
				return makeErrorResult("EQ_action: ADD | OBSERVE | DELETE",
						QueryParameterException.class);
			}
		}

		// M42
		if (eventCountLimit != null && maxEventCount != null) {
			return makeErrorResult(
					"One of eventCountLimit and maxEventCount should be omitted",
					QueryParameterException.class);
		}
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

	private Query makeSortAndLimitQuery(Query query, String orderBy,
			String orderDirection, String eventCountLimit, String maxEventCount) {
		/**
		 * orderBy : If specified, names a single field that will be used to
		 * order the results. The orderDirection field specifies whether the
		 * ordering is in ascending sequence or descending sequence. Events
		 * included in the result that lack the specified field altogether may
		 * occur in any position within the result event list. The value of this
		 * parameter SHALL be one of: eventTime, recordTime, or the fully
		 * qualified name of an extension field whose type is Int, Float, Time,
		 * or String. A fully qualified fieldname is constructed as for the
		 * EQ_fieldname parameter. In the case of a field of type String, the
		 * ordering SHOULD be in lexicographic order based on the Unicode
		 * encoding of the strings, or in some other collating sequence
		 * appropriate to the locale. If omitted, no order is specified. The
		 * implementation MAY order the results in any order it chooses, and
		 * that order MAY differ even when the same query is executed twice on
		 * the same data. (In EPCIS 1.0, the value quantity was also permitted,
		 * but its use is deprecated in EPCIS 1.1.)
		 * 
		 * orderDirection : If specified and orderBy is also specified,
		 * specifies whether the results are ordered in ascending or descending
		 * sequence according to the key specified by orderBy. The value of this
		 * parameter must be one of ASC (for ascending order) or DESC (for
		 * descending order); if not, the implementation SHALL raise a
		 * QueryParameterException. If omitted, defaults to DESC.
		 */

		// Update Query with ORDER and LIMIT
		if (orderBy != null) {
			// Currently only eventTime, recordTime can be used
			if (orderBy.trim().equals("eventTime")) {
				if (orderDirection != null) {
					if (orderDirection.trim().equals("ASC")) {
						query.with(new Sort(Sort.Direction.ASC, "eventTime"));
					} else if (orderDirection.trim().equals("DESC")) {
						query.with(new Sort(Sort.Direction.DESC, "eventTime"));
					}
				}
			} else if (orderBy.trim().equals("recordTime")) {
				if (orderDirection != null) {
					if (orderDirection.trim().equals("ASC")) {
						query.with(new Sort(Sort.Direction.ASC, "recordTime"));
					} else if (orderDirection.trim().equals("DESC")) {
						query.with(new Sort(Sort.Direction.DESC, "recordTime"));
					}
				}
			}
		}

		/**
		 * eventCountLimit: If specified, the results will only include the
		 * first N events that match the other criteria, where N is the value of
		 * this parameter. The ordering specified by the orderBy and
		 * orderDirection parameters determine the meaning of “first” for this
		 * purpose. If omitted, all events matching the specified criteria will
		 * be included in the results. This parameter and maxEventCount are
		 * mutually exclusive; if both are specified, a QueryParameterException
		 * SHALL be raised. This parameter may only be used when orderBy is
		 * specified; if orderBy is omitted and eventCountLimit is specified, a
		 * QueryParameterException SHALL be raised. This parameter differs from
		 * maxEventCount in that this parameter limits the amount of data
		 * returned, whereas maxEventCount causes an exception to be thrown if
		 * the limit is exceeded.
		 */
		if (eventCountLimit != null) {
			try {
				int eventCount = Integer.parseInt(eventCountLimit);
				query.limit(eventCount);
			} catch (NumberFormatException nfe) {
				Configuration.logger.log(Level.ERROR, nfe.toString());
			}
		}

		return query;
	}

	private List<Criteria> makeCritera(String vocabularyName,
			boolean includeAttributes, boolean includeChildren,
			String attributeNames, String eQ_name, String wD_name,
			String hASATTR, String maxElementCount) {

		List<Criteria> criteriaList = new ArrayList<Criteria>();

		/**
		 * If specified, only vocabulary elements drawn from one of the
		 * specified vocabularies will be included in the results. Each element
		 * of the specified list is the formal URI name for a vocabulary; e.g.,
		 * one of the URIs specified in the table at the end of Section 7.2. If
		 * omitted, all vocabularies are considered.
		 */

		if (vocabularyName != null) {
			String[] vocNameArray = vocabularyName.split(",");
			List<String> subStringList = new ArrayList<String>();
			for (int i = 0; i < vocNameArray.length; i++) {
				String vocNameString = vocNameArray[i].trim();
				subStringList.add(vocNameString);
			}
			if (subStringList != null)
				criteriaList.add(Criteria.where("type").in(subStringList));
		}

		/**
		 * If true, the results will include attribute names and values for
		 * matching vocabulary elements. If false, attribute names and values
		 * will not be included in the result.
		 */

		return criteriaList;

	}

	private List<Criteria> makeCriteria(String eventType, String GE_eventTime,
			String LT_eventTime, String GE_recordTime, String LT_recordTime,
			String EQ_action, String EQ_bizStep, String EQ_disposition,
			String EQ_readPoint, String WD_readPoint, String EQ_bizLocation,
			String WD_bizLocation, String EQ_transformationID,
			String MATCH_epc, String MATCH_parentID, String MATCH_inputEPC,
			String MATCH_outputEPC, String MATCH_anyEPC, String MATCH_epcClass,
			String MATCH_inputEPCClass, String MATCH_outputEPCClass,
			String MATCH_anyEPCClass, String EQ_quantity, String GT_quantity,
			String GE_quantity, String LT_quantity, String LE_quantity,
			String orderBy, String orderDirection, String eventCountLimit,
			String maxEventCount, Map<String, String> paramMap) {

		List<Criteria> criteriaList = new ArrayList<Criteria>();
		try {
			/**
			 * GE_eventTime: If specified, only events with eventTime greater
			 * than or equal to the specified value will be included in the
			 * result. If omitted, events are included regardless of their
			 * eventTime (unless constrained by the LT_eventTime parameter).
			 * Example: 2014-08-11T19:57:59.717+09:00 SimpleDateFormat sdf = new
			 * SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
			 * eventTime.setTime(sdf.parse(timeString)); e.g.
			 * 1988-07-04T12:08:56.235-07:00
			 * 
			 * Verified
			 */
			if (GE_eventTime != null) {
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
				GregorianCalendar geEventTimeCalendar = new GregorianCalendar();

				geEventTimeCalendar.setTime(sdf.parse(GE_eventTime));
				long geEventTimeMillis = geEventTimeCalendar.getTimeInMillis();
				criteriaList.add(Criteria.where("eventTime").gt(
						geEventTimeMillis));
			}
			/**
			 * LT_eventTime: If specified, only events with eventTime less than
			 * the specified value will be included in the result. If omitted,
			 * events are included regardless of their eventTime (unless
			 * constrained by the GE_eventTime parameter).
			 * 
			 * Verified
			 */
			if (LT_eventTime != null) {
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
				GregorianCalendar ltEventTimeCalendar = new GregorianCalendar();
				Date date = sdf.parse(LT_eventTime);
				ltEventTimeCalendar.setTime(date);
				long ltEventTimeMillis = ltEventTimeCalendar.getTimeInMillis();
				criteriaList.add(Criteria.where("eventTime").lt(
						ltEventTimeMillis));
			}
			/**
			 * GE_recordTime: If provided, only events with recordTime greater
			 * than or equal to the specified value will be returned. The
			 * automatic limitation based on event record time (Section 8.2.5.2)
			 * may implicitly provide a constraint similar to this parameter. If
			 * omitted, events are included regardless of their recordTime,
			 * other than automatic limitation based on event record time
			 * (Section 8.2.5.2).
			 * 
			 * Verified
			 */
			if (GE_recordTime != null) {
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
				GregorianCalendar geRecordTimeCalendar = new GregorianCalendar();

				geRecordTimeCalendar.setTime(sdf.parse(GE_recordTime));
				long geRecordTimeMillis = geRecordTimeCalendar
						.getTimeInMillis();
				criteriaList.add(Criteria.where("recordTime").gt(
						geRecordTimeMillis));
			}
			/**
			 * LE_recordTime: If provided, only events with recordTime less than
			 * the specified value will be returned. If omitted, events are
			 * included regardless of their recordTime (unless constrained by
			 * the GE_recordTime parameter or the automatic limitation based on
			 * event record time).
			 * 
			 * Verified
			 */
			if (LT_recordTime != null) {
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
				GregorianCalendar ltRecordTimeCalendar = new GregorianCalendar();

				ltRecordTimeCalendar.setTime(sdf.parse(LT_recordTime));
				long ltRecordTimeMillis = ltRecordTimeCalendar
						.getTimeInMillis();
				criteriaList.add(Criteria.where("recordTime").lt(
						ltRecordTimeMillis));
			}

			/**
			 * EQ_action: If specified, the result will only include events that
			 * (a) have an action field; and where (b) the value of the action
			 * field matches one of the specified values. The elements of the
			 * value of this parameter each must be one of the strings ADD,
			 * OBSERVE, or DELETE; if not, the implementation SHALL raise a
			 * QueryParameterException. If omitted, events are included
			 * regardless of their action field.
			 * 
			 * Verified
			 */
			if (EQ_action != null) {
				// Constrained already checked
				criteriaList.add(Criteria.where("action").is(EQ_action));
			}
			/**
			 * EQ_bizStep: If specified, the result will only include events
			 * that (a) have a non-null bizStep field; and where (b) the value
			 * of the bizStep field matches one of the specified values. If this
			 * parameter is omitted, events are returned regardless of the value
			 * of the bizStep field or whether the bizStep field exists at all.
			 * 
			 * Verified
			 */
			if (EQ_bizStep != null) {
				String[] eqBizStepArray = EQ_bizStep.split(",");
				List<String> subStringList = new ArrayList<String>();
				for (int i = 0; i < eqBizStepArray.length; i++) {
					String eqBizStepString = eqBizStepArray[i].trim();
					subStringList.add(eqBizStepString);
				}
				if (subStringList != null)
					criteriaList.add(Criteria.where("bizStep")
							.in(subStringList));
			}
			/**
			 * EQ_disposition: Like the EQ_bizStep parameter, but for the
			 * disposition field.
			 * 
			 * Verified
			 */
			if (EQ_disposition != null) {
				String[] eqDispositionArray = EQ_disposition.split(",");
				List<String> subStringList = new ArrayList<String>();
				for (int i = 0; i < eqDispositionArray.length; i++) {
					String eqDispositionString = eqDispositionArray[i].trim();
					subStringList.add(eqDispositionString);
				}
				if (subStringList != null)
					criteriaList.add(Criteria.where("disposition").in(
							subStringList));
			}
			/**
			 * EQ_readPoint: If specified, the result will only include events
			 * that (a) have a non-null readPoint field; and where (b) the value
			 * of the readPoint field matches one of the specified values. If
			 * this parameter and WD_readPoint are both omitted, events are
			 * returned regardless of the value of the readPoint field or
			 * whether the readPoint field exists at all.
			 */
			if (EQ_readPoint != null) {
				String[] eqReadPointArray = EQ_readPoint.split(",");
				List<String> subStringList = new ArrayList<String>();
				for (int i = 0; i < eqReadPointArray.length; i++) {
					String eqReadPointString = eqReadPointArray[i].trim();
					subStringList.add(eqReadPointString);
				}
				Criteria criteria = Criteria.where("readPoint.id").in(
						subStringList);
				criteriaList.add(criteria);
			}

			/**
			 * WD_readPoint: If specified, the result will only include events
			 * that (a) have a non-null readPoint field; and where (b) the value
			 * of the readPoint field matches one of the specified values, or is
			 * a direct or indirect descendant of one of the specified values.
			 * The meaning of “direct or indirect descendant” is specified by
			 * master data, as described in Section 6.5. (WD is an abbreviation
			 * for “with descendants.”) If this parameter and EQ_readPoint are
			 * both omitted, events are returned regardless of the value of the
			 * readPoint field or whether the readPoint field exists at all.
			 */
			if (WD_readPoint != null) {
				String[] wdReadPointArray = WD_readPoint.split(",");
				List<Pattern> patternArray = new ArrayList<Pattern>();
				for (int i = 0; i < wdReadPointArray.length; i++) {
					String wdReadPointString = wdReadPointArray[i].trim();
					patternArray.add(Pattern.compile("/^" + wdReadPointString
							+ ".*/"));
				}
				Criteria criteria = Criteria.where("readPoint.id").in(
						patternArray);
				criteriaList.add(criteria);
			}
			/**
			 * EQ_bizLocation: Like the EQ_readPoint parameter, but for the
			 * bizLocation field.
			 */
			if (EQ_bizLocation != null) {
				String[] eqBizLocationArray = EQ_bizLocation.split(",");
				List<String> subStringList = new ArrayList<String>();
				for (int i = 0; i < eqBizLocationArray.length; i++) {
					String eqBizLocationString = eqBizLocationArray[i].trim();
					subStringList.add(eqBizLocationString);
				}
				Criteria criteria = Criteria.where("bizLocation.id").in(
						subStringList);
				criteriaList.add(criteria);
			}
			/**
			 * WD_bizLocation: Like the WD_readPoint parameter, but for the
			 * bizLocation field.
			 */
			if (WD_bizLocation != null) {
				String[] wdBizLocationArray = WD_bizLocation.split(",");
				List<Pattern> patternArray = new ArrayList<Pattern>();
				for (int i = 0; i < wdBizLocationArray.length; i++) {
					String wdBizLocationString = wdBizLocationArray[i].trim();
					patternArray.add(Pattern.compile("/^" + wdBizLocationString
							+ ".*/"));
				}
				Criteria criteria = Criteria.where("bizLocation.id").in(
						patternArray);
				criteriaList.add(criteria);
			}
			/**
			 * EQ_bizTransaction_type: EQ_source_type: EQ_destination_type: is
			 * currently not processed, since its description seems ambiguous
			 */

			/**
			 * EQ_transformationID: If this parameter is specified, the result
			 * will only include events that (a) have a transformationID field
			 * (that is, TransformationEvents or extension event type that
			 * extend TransformationEvent); and where (b) the transformationID
			 * field is equal to one of the values specified in this parameter.
			 */
			if (EQ_transformationID != null) {
				String[] eqTransformationIDArray = EQ_transformationID
						.split(",");
				List<String> subStringList = new ArrayList<String>();
				for (int i = 0; i < eqTransformationIDArray.length; i++) {
					String eqTransformationIDString = eqTransformationIDArray[i]
							.trim();
					subStringList.add(eqTransformationIDString);
				}
				Criteria criteria = Criteria.where("bizLocation.id").in(
						subStringList);
				criteriaList.add(criteria);
			}

			/**
			 * MATCH_epc: If this parameter is specified, the result will only
			 * include events that (a) have an epcList or a childEPCs field
			 * (that is, ObjectEvent, AggregationEvent, TransactionEvent or
			 * extension event types that extend one of those three); and where
			 * (b) one of the EPCs listed in the epcList or childEPCs field
			 * (depending on event type) matches one of the EPC patterns or URIs
			 * specified in this parameter, where the meaning of “matches” is as
			 * specified in Section 8.2.7.1.1. If this parameter is omitted,
			 * events are included regardless of their epcList or childEPCs
			 * field or whether the epcList or childEPCs field exists.
			 * 
			 * Somewhat verified
			 */
			if (MATCH_epc != null) {
				String[] match_EPCArray = MATCH_epc.split(",");
				List<DBObject> subDBObjectList = new ArrayList<DBObject>();
				for (int i = 0; i < match_EPCArray.length; i++) {
					String match_EPCString = match_EPCArray[i].trim();
					DBObject queryObj = new BasicDBObject();
					queryObj.put("epc", match_EPCString);
					subDBObjectList.add(queryObj);
				}
				Criteria criteria = new Criteria();
				criteria.orOperator(
						Criteria.where("epcList").in(subDBObjectList), Criteria
								.where("childEPCs").in(subDBObjectList));

				criteriaList.add(criteria);
			}

			/**
			 * MATCH_parentID: Like MATCH_epc, but matches the parentID field of
			 * AggregationEvent, the parentID field of TransactionEvent, and
			 * extension event types that extend either AggregationEvent or
			 * TransactionEvent. The meaning of “matches” is as specified in
			 * Section 8.2.7.1.1.
			 */
			if (MATCH_parentID != null) {
				String[] match_parentEPCArray = MATCH_parentID.split(",");

				List<DBObject> subDBObjectList = new ArrayList<DBObject>();
				for (int i = 0; i < match_parentEPCArray.length; i++) {
					String match_parentEPCString = match_parentEPCArray[i]
							.trim();
					DBObject queryObj = new BasicDBObject();
					queryObj.put("epc", match_parentEPCString);
					subDBObjectList.add(queryObj);
				}
				Criteria criteria = Criteria.where("parentID").in(
						subDBObjectList);
				criteriaList.add(criteria);
			}

			/**
			 * MATCH_inputEPC: If this parameter is specified, the result will
			 * only include events that (a) have an inputEPCList (that is,
			 * TransformationEvent or an extension event type that extends
			 * TransformationEvent); and where (b) one of the EPCs listed in the
			 * inputEPCList field matches one of the EPC patterns or URIs
			 * specified in this parameter. The meaning of “matches” is as
			 * specified in Section 8.2.7.1.1. If this parameter is omitted,
			 * events are included regardless of their inputEPCList field or
			 * whether the inputEPCList field exists.
			 */
			if (MATCH_inputEPC != null) {
				String[] match_inputEPCArray = MATCH_inputEPC.split(",");

				List<DBObject> subDBObjectList = new ArrayList<DBObject>();

				for (int i = 0; i < match_inputEPCArray.length; i++) {
					String match_inputEPCString = match_inputEPCArray[i].trim();
					DBObject queryObj = new BasicDBObject();
					queryObj.put("epc", match_inputEPCString);
					subDBObjectList.add(queryObj);
				}
				Criteria criteria = Criteria.where("inputEPCList").in(
						subDBObjectList);
				criteriaList.add(criteria);
			}

			/**
			 * MATCH_outputEPC: If this parameter is specified, the result will
			 * only include events that (a) have an inputEPCList (that is,
			 * TransformationEvent or an extension event type that extends
			 * TransformationEvent); and where (b) one of the EPCs listed in the
			 * inputEPCList field matches one of the EPC patterns or URIs
			 * specified in this parameter. The meaning of “matches” is as
			 * specified in Section 8.2.7.1.1. If this parameter is omitted,
			 * events are included regardless of their inputEPCList field or
			 * whether the inputEPCList field exists.
			 */
			if (MATCH_outputEPC != null) {
				String[] match_outputEPCArray = MATCH_outputEPC.split(",");

				List<DBObject> subDBObjectList = new ArrayList<DBObject>();

				for (int i = 0; i < match_outputEPCArray.length; i++) {
					String match_outputEPCString = match_outputEPCArray[i]
							.trim();
					DBObject queryObj = new BasicDBObject();
					queryObj.put("epc", match_outputEPCString);
					subDBObjectList.add(queryObj);
				}
				Criteria criteria = Criteria.where("outputEPCList").in(
						subDBObjectList);
				criteriaList.add(criteria);
			}

			/**
			 * MATCH_anyEPC: If this parameter is specified, the result will
			 * only include events that (a) have an epcList field, a childEPCs
			 * field, a parentID field, an inputEPCList field, or an
			 * outputEPCList field (that is, ObjectEvent, AggregationEvent,
			 * TransactionEvent, TransformationEvent, or extension event types
			 * that extend one of those four); and where (b) the parentID field
			 * or one of the EPCs listed in the epcList, childEPCs,
			 * inputEPCList, or outputEPCList field (depending on event type)
			 * matches one of the EPC patterns or URIs specified in this
			 * parameter. The meaning of “matches” is as specified in Section
			 * 8.2.7.1.1.
			 */

			if (MATCH_anyEPC != null) {
				String[] match_anyEPCArray = MATCH_anyEPC.split(",");
				List<DBObject> subDBObjectList = new ArrayList<DBObject>();

				for (int i = 0; i < match_anyEPCArray.length; i++) {
					String match_anyEPCString = match_anyEPCArray[i].trim();
					DBObject queryObj = new BasicDBObject();
					queryObj.put("epc", match_anyEPCString);
					subDBObjectList.add(queryObj);
				}
				Criteria criteria = new Criteria();
				criteria.orOperator(
						Criteria.where("epcList").in(subDBObjectList), Criteria
								.where("childEPCs").in(subDBObjectList),
						Criteria.where("inputEPCList").in(subDBObjectList),
						Criteria.where("outputEPCList").in(subDBObjectList));
				criteriaList.add(criteria);
			}

			/**
			 * MATCH_epcClass: If this parameter is specified, the result will
			 * only include events that (a) have a quantityList or a
			 * childQuantityList field (that is, ObjectEvent, AggregationEvent,
			 * TransactionEvent or extension event types that extend one of
			 * those three); and where (b) one of the EPC classes listed in the
			 * quantityList or childQuantityList field (depending on event type)
			 * matches one of the EPC patterns or URIs specified in this
			 * parameter. The result will also include QuantityEvents whose
			 * epcClass field matches one of the EPC patterns or URIs specified
			 * in this parameter. The meaning of “matches” is as specified in
			 * Section 8.2.7.1.1.
			 */
			if (MATCH_epcClass != null) {
				String[] match_epcClassArray = MATCH_epcClass.split(",");

				List<String> subStringList = new ArrayList<String>();
				Criteria criteria = new Criteria();

				for (int i = 0; i < match_epcClassArray.length; i++) {
					String match_epcClassString = match_epcClassArray[i].trim();
					subStringList.add(match_epcClassString);
				}
				criteria.orOperator(
						Criteria.where("extension.quantityList.epcClass").in(
								subStringList),
						Criteria.where("extension.childQuantityList.epcClass")
								.in(subStringList));
				criteriaList.add(criteria);
			}

			/**
			 * MATCH_inputEPCClass: If this parameter is specified, the result
			 * will only include events that (a) have an inputQuantityList field
			 * (that is, TransformationEvent or extension event types that
			 * extend it); and where (b) one of the EPC classes listed in the
			 * inputQuantityList field (depending on event type) matches one of
			 * the EPC patterns or URIs specified in this parameter. The meaning
			 * of “matches” is as specified in Section 8.2.7.1.1.
			 */
			if (MATCH_inputEPCClass != null) {
				String[] match_inputEPCClassArray = MATCH_inputEPCClass
						.split(",");

				List<String> subStringList = new ArrayList<String>();

				for (int i = 0; i < match_inputEPCClassArray.length; i++) {
					String match_inputEPCClassString = match_inputEPCClassArray[i]
							.trim();
					subStringList.add(match_inputEPCClassString);
				}
				Criteria criteria = Criteria
						.where("inputQuantityList.epcClass").in(subStringList);
				criteriaList.add(criteria);
			}

			/**
			 * MATCH_outputEPCClass: If this parameter is specified, the result
			 * will only include events that (a) have an outputQuantityList
			 * field (that is, TransformationEvent or extension event types that
			 * extend it); and where (b) one of the EPC classes listed in the
			 * outputQuantityList field (depending on event type) matches one of
			 * the EPC patterns or URIs specified in this parameter. The meaning
			 * of “matches” is as specified in Section 8.2.7.1.1.
			 */

			if (MATCH_outputEPCClass != null) {
				String[] match_outputEPCClassArray = MATCH_outputEPCClass
						.split(",");
				List<String> subStringList = new ArrayList<String>();

				for (int i = 0; i < match_outputEPCClassArray.length; i++) {
					String match_outputEPCClassString = match_outputEPCClassArray[i]
							.trim();
					subStringList.add(match_outputEPCClassString);
				}
				Criteria criteria = Criteria.where(
						"outputQuantityList.epcClass").in(subStringList);
				criteriaList.add(criteria);
			}

			/**
			 * MATCH_anyEPCClass: If this parameter is specified, the result
			 * will only include events that (a) have a quantityList,
			 * childQuantityList, inputQuantityList, or outputQuantityList field
			 * (that is, ObjectEvent, AggregationEvent, TransactionEvent,
			 * TransformationEvent, or extension event types that extend one of
			 * those four); and where (b) one of the EPC classes listed in any
			 * of those fields matches one of the EPC patterns or URIs specified
			 * in this parameter. The result will also include QuantityEvents
			 * whose epcClass field matches one of the EPC patterns or URIs
			 * specified in this parameter. The meaning of “matches” is as
			 * specified in Section 8.2.7.1.1.
			 */
			if (MATCH_anyEPCClass != null) {
				String[] match_anyEPCClassArray = MATCH_anyEPCClass.split(",");

				List<String> subStringList = new ArrayList<String>();

				for (int i = 0; i < match_anyEPCClassArray.length; i++) {
					String match_anyEPCClassString = match_anyEPCClassArray[i]
							.trim();
					subStringList.add(match_anyEPCClassString);

				}
				Criteria criteria = new Criteria();
				criteria.orOperator(
						Criteria.where("extension.quantityList.epcClass").in(
								subStringList),
						Criteria.where("extension.childQuantityList.epcClass")
								.in(subStringList),
						Criteria.where("inputQuantityList.epcClass").in(
								subStringList),
						Criteria.where("outputQuantityList.epcClass").in(
								subStringList));
				criteriaList.add(criteria);
			}

			/**
			 * (DEPCRECATED in EPCIS 1.1) EQ_quantity; GT_quantity; GE_quantity;
			 * LT_quantity; LE_quantity
			 **/

			/**
			 * EQ_fieldname: This is not a single parameter, but a family of
			 * parameters. If a parameter of this form is specified, the result
			 * will only include events that (a) have a field named fieldname
			 * whose type is either String or a vocabulary type; and where (b)
			 * the value of that field matches one of the values specified in
			 * this parameter. Fieldname is the fully qualified name of an
			 * extension field. The name of an extension field is an XML qname;
			 * that is, a pair consisting of an XML namespace URI and a name.
			 * The name of the corresponding query parameter is constructed by
			 * concatenating the following: the string EQ_, the namespace URI
			 * for the extension field, a pound sign (#), and the name of the
			 * extension field.
			 */

			Iterator<String> paramIter = paramMap.keySet().iterator();
			while (paramIter.hasNext()) {
				String paramName = paramIter.next();
				String paramValues = paramMap.get(paramName);

				/**
				 * EQ_bizTransaction_type: This is not a single parameter, but a
				 * family of parameters. If a parameter of this form is
				 * specified, the result will only include events that (a)
				 * include a bizTransactionList; (b) where the business
				 * transaction list includes an entry whose type subfield is
				 * equal to type extracted from the name of this parameter; and
				 * (c) where the bizTransaction subfield of that entry is equal
				 * to one of the values specified in this parameter.
				 */
				if (paramName.contains("EQ_bizTransaction_")) {
					String type = paramName.substring(18, paramName.length());
					List<DBObject> subObjList = new ArrayList<DBObject>();
					String[] paramValueArr = paramValues.split(",");
					for (int i = 0; i < paramValueArr.length; i++) {
						String val = paramValueArr[i].trim();
						DBObject dbo = new BasicDBObject();
						dbo.put(type, val);
						subObjList.add(dbo);
					}
					Criteria criteria = Criteria.where("bizTransactionList")
							.in(subObjList);
					criteriaList.add(criteria);
				}

				/**
				 * EQ_source_type: This is not a single parameter, but a family
				 * of parameters. If a parameter of this form is specified, the
				 * result will only include events that (a) include a
				 * sourceList; (b) where the source list includes an entry whose
				 * type subfield is equal to type extracted from the name of
				 * this parameter; and (c) where the source subfield of that
				 * entry is equal to one of the values specified in this
				 * parameter.
				 */

				if (paramName.contains("EQ_source_")) {
					String type = paramName.substring(10, paramName.length());
					List<DBObject> subObjList = new ArrayList<DBObject>();
					String[] paramValueArr = paramValues.split(",");
					for (int i = 0; i < paramValueArr.length; i++) {
						String val = paramValueArr[i].trim();
						DBObject dbo = new BasicDBObject();
						dbo.put(type, val);
						subObjList.add(dbo);
					}
					if (eventType.equals("AggregationEvent")
							|| eventType.equals("ObjectEvent")
							|| eventType.equals("TransactionEvent")) {
						Criteria criteria = Criteria.where(
								"extension.sourceList").in(subObjList);
						criteriaList.add(criteria);
					}
					if (eventType.equals("TransformationEvent")) {
						Criteria criteria = Criteria.where("sourceList").in(
								subObjList);
						criteriaList.add(criteria);
					}
				}

				/**
				 * EQ_destination_type: This is not a single parameter, but a
				 * family of parameters. If a parameter of this form is
				 * specified, the result will only include events that (a)
				 * include a destinationList; (b) where the destination list
				 * includes an entry whose type subfield is equal to type
				 * extracted from the name of this parameter; and (c) where the
				 * destination subfield of that entry is equal to one of the
				 * values specified in this parameter.
				 */
				if (paramName.contains("EQ_destination_")) {
					String type = paramName.substring(15, paramName.length());
					List<DBObject> subObjList = new ArrayList<DBObject>();
					String[] paramValueArr = paramValues.split(",");
					for (int i = 0; i < paramValueArr.length; i++) {
						String val = paramValueArr[i].trim();
						DBObject dbo = new BasicDBObject();
						dbo.put(type, val);
						subObjList.add(dbo);
					}
					if (eventType.equals("AggregationEvent")
							|| eventType.equals("ObjectEvent")
							|| eventType.equals("TransactionEvent")) {
						Criteria criteria = Criteria.where(
								"extension.destinationList").in(subObjList);
						criteriaList.add(criteria);
					}
					if (eventType.equals("TransformationEvent")) {
						Criteria criteria = Criteria.where("destinationList")
								.in(subObjList);
						criteriaList.add(criteria);
					}
				}
				boolean isExtraParam = isExtraParameter(paramName);

				if (isExtraParam == true) {

					/**
					 * EQ_fieldname: This is not a single parameter, but a
					 * family of parameters. If a parameter of this form is
					 * specified, the result will only include events that (a)
					 * have a field named fieldname whose type is either String
					 * or a vocabulary type; and where (b) the value of that
					 * field matches one of the values specified in this
					 * parameter. Fieldname is the fully qualified name of an
					 * extension field. The name of an extension field is an XML
					 * qname; that is, a pair consisting of an XML namespace URI
					 * and a name. The name of the corresponding query parameter
					 * is constructed by concatenating the following: the string
					 * EQ_, the namespace URI for the extension field, a pound
					 * sign (#), and the name of the extension field.
					 */
					if (paramName.startsWith("EQ_")) {
						String type = paramName
								.substring(3, paramName.length());
						List<String> subObjList = new ArrayList<String>();
						String[] paramValueArr = paramValues.split(",");
						for (int i = 0; i < paramValueArr.length; i++) {
							String val = paramValueArr[i].trim();
							subObjList.add(val);
						}
						Criteria criteria = new Criteria();
						if (eventType.equals("AggregationEvent")
								|| eventType.equals("ObjectEvent")
								|| eventType.equals("TransactionEvent")) {
							criteria.orOperator(
									Criteria.where(
											"extension.extension.any." + type)
											.in(subObjList),
									Criteria.where(
											"extension.extension.otherAttributes."
													+ type).in(subObjList));
							criteriaList.add(criteria);
						}
						if (eventType.equals("QuantityEvent")
								|| eventType.equals("TransformationEvent")
								|| eventType.equals("SensorEvent")) {
							criteria.orOperator(
									Criteria.where("extension.any." + type).in(
											subObjList),
									Criteria.where(
											"extension.otherAttributes." + type)
											.in(subObjList));
							criteriaList.add(criteria);
						}
					}

					/**
					 * GT/GE/LT/LE_fieldname: Like EQ_fieldname as described
					 * above, but may be applied to a field of type Int, Float,
					 * or Time. The result will include events that (a) have a
					 * field named fieldname; and where (b) the type of the
					 * field matches the type of this parameter (Int, Float, or
					 * Time); and where (c) the value of the field is greater
					 * than the specified value. Fieldname is constructed as for
					 * EQ_fieldname.
					 */

					if (paramName.startsWith("GT_")
							|| paramName.startsWith("GE_")
							|| paramName.startsWith("LT_")
							|| paramName.startsWith("LE_")) {
						String type = paramName
								.substring(3, paramName.length());
						// Already error handled
						String value = paramValues;
						Criteria criteria = new Criteria();
						if (eventType.equals("AggregationEvent")
								|| eventType.equals("ObjectEvent")
								|| eventType.equals("TransactionEvent")) {
							if (paramName.startsWith("GT_")) {
								criteria.orOperator(
										Criteria.where(
												"extension.extension.any."
														+ type).gt(value),
										Criteria.where(
												"extension.extension.otherAttributes."
														+ type).gt(value));
								criteriaList.add(criteria);
							}
							if (paramName.startsWith("GE_")) {
								criteria.orOperator(
										Criteria.where(
												"extension.extension.any."
														+ type).gte(value),
										Criteria.where(
												"extension.extension.otherAttributes."
														+ type).gte(value));
								criteriaList.add(criteria);
							}
							if (paramName.startsWith("LT_")) {
								criteria.orOperator(
										Criteria.where(
												"extension.extension.any."
														+ type).lt(value),
										Criteria.where(
												"extension.extension.otherAttributes."
														+ type).lt(value));
								criteriaList.add(criteria);
							}
							if (paramName.startsWith("LE_")) {
								criteria.orOperator(
										Criteria.where(
												"extension.extension.any."
														+ type).lte(value),
										Criteria.where(
												"extension.extension.otherAttributes."
														+ type).lte(value));
								criteriaList.add(criteria);
							}
						}
						if (eventType.equals("QuantityEvent")
								|| eventType.equals("TransformationEvent")
								|| eventType.equals("SensorEvent")) {
							if (paramName.startsWith("GT_")) {
								criteria.orOperator(
										Criteria.where("extension.any." + type)
												.gt(value),
										Criteria.where(
												"extension.otherAttributes."
														+ type).gt(value));
								criteriaList.add(criteria);
							}
							if (paramName.startsWith("GE_")) {
								criteria.orOperator(
										Criteria.where("extension.any." + type)
												.gte(value),
										Criteria.where(
												"extension.otherAttributes."
														+ type).gte(value));
								criteriaList.add(criteria);
							}
							if (paramName.startsWith("LT_")) {
								criteria.orOperator(
										Criteria.where("extension.any." + type)
												.lt(value),
										Criteria.where(
												"extension.otherAttributes."
														+ type).lt(value));
								criteriaList.add(criteria);
							}
							if (paramName.startsWith("LE_")) {
								criteria.orOperator(
										Criteria.where("extension.any." + type)
												.lte(value),
										Criteria.where(
												"extension.otherAttributes."
														+ type).lte(value));
								criteriaList.add(criteria);
							}
						}
					}

				}
			}

		} catch (ParseException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
		}
		return criteriaList;
	}

	private boolean isExtraParameter(String paramName) {
		
		if (paramName.contains("eventTime"))
			return false;
		if (paramName.contains("recordTime"))
			return false;
		if (paramName.contains("action"))
			return false;
		if (paramName.contains("bizStep"))
			return false;
		if (paramName.contains("disposition"))
			return false;
		if (paramName.contains("readPoint"))
			return false;
		if (paramName.contains("bizLocation"))
			return false;
		if (paramName.contains("bizTransaction"))
			return false;
		if (paramName.contains("source"))
			return false;
		if (paramName.contains("destination"))
			return false;
		if (paramName.contains("transformationID"))
			return false;
		return true;
	}

	public void addScheduleToQuartz(SubscriptionType subscription) {
		try {
			JobDataMap map = new JobDataMap();
			map.put("queryName", subscription.getPollParameters().getQueryName());
			map.put("subscriptionID", subscription.getSubscriptionID());
			map.put("dest", subscription.getDest());
			//map.put("cronExpression", subscription.getCronExpression());

			if (subscription.getPollParameters().getEventType() != null)
				map.put("eventType", subscription.getPollParameters().getEventType());
			if (subscription.getPollParameters().getGE_eventTime() != null)
				map.put("GE_eventTime", subscription.getPollParameters().getGE_eventTime());
			if (subscription.getPollParameters().getLT_eventTime() != null)
				map.put("LT_eventTime", subscription.getPollParameters().getLT_eventTime());
			if (subscription.getPollParameters().getGE_recordTime() != null)
				map.put("GE_recordTime", subscription.getPollParameters().getGE_recordTime());
			if (subscription.getPollParameters().getLT_recordTime() != null)
				map.put("LT_recordTime", subscription.getPollParameters().getLT_recordTime());
			if (subscription.getPollParameters().getEQ_action() != null)
				map.put("EQ_action", subscription.getPollParameters().getEQ_action());
			if (subscription.getPollParameters().getEQ_bizStep() != null)
				map.put("EQ_bizStep", subscription.getPollParameters().getEQ_bizStep());
			if (subscription.getPollParameters().getEQ_disposition() != null)
				map.put("EQ_disposition", subscription.getPollParameters().getEQ_disposition());
			if (subscription.getPollParameters().getEQ_readPoint() != null)
				map.put("EQ_readPoint", subscription.getPollParameters().getEQ_readPoint());
			if (subscription.getPollParameters().getWD_readPoint() != null)
				map.put("WD_readPoint", subscription.getPollParameters().getWD_readPoint());
			if (subscription.getPollParameters().getEQ_bizLocation() != null)
				map.put("EQ_bizLocation", subscription.getPollParameters().getEQ_bizLocation());
			if (subscription.getPollParameters().getWD_bizLocation() != null)
				map.put("WD_bizLocation", subscription.getPollParameters().getWD_bizLocation());
			if (subscription.getPollParameters().getEQ_transformationID() != null)
				map.put("EQ_transformationID",
						subscription.getPollParameters().getEQ_transformationID());
			if (subscription.getPollParameters().getMATCH_epc() != null)
				map.put("MATCH_epc", subscription.getPollParameters().getMATCH_epc());
			if (subscription.getPollParameters().getMATCH_parentID() != null)
				map.put("MATCH_parentID", subscription.getPollParameters().getMATCH_parentID());
			if (subscription.getPollParameters().getMATCH_inputEPC() != null)
				map.put("MATCH_inputEPC", subscription.getPollParameters().getMATCH_inputEPC());
			if (subscription.getPollParameters().getMATCH_outputEPC() != null)
				map.put("MATCH_outputEPC", subscription.getPollParameters().getMATCH_outputEPC());
			if (subscription.getPollParameters().getMATCH_anyEPC() != null)
				map.put("MATCH_anyEPC", subscription.getPollParameters().getMATCH_anyEPC());
			if (subscription.getPollParameters().getMATCH_epcClass() != null)
				map.put("MATCH_epcClass", subscription.getPollParameters().getMATCH_epcClass());
			if (subscription.getPollParameters().getMATCH_inputEPCClass() != null)
				map.put("MATCH_inputEPCClass",
						subscription.getPollParameters().getMATCH_inputEPCClass());
			if (subscription.getPollParameters().getMATCH_outputEPCClass() != null)
				map.put("MATCH_outputEPCClass",
						subscription.getPollParameters().getMATCH_outputEPCClass());
			if (subscription.getPollParameters().getMATCH_anyEPCClass() != null)
				map.put("MATCH_anyEPCClass",
						subscription.getPollParameters().getMATCH_anyEPCClass());
			if (subscription.getPollParameters().getEQ_quantity() != null)
				map.put("EQ_quantity", subscription.getPollParameters().getEQ_quantity());
			if (subscription.getPollParameters().getGT_quantity() != null)
				map.put("GT_quantity", subscription.getPollParameters().getGT_quantity());
			if (subscription.getPollParameters().getGE_quantity() != null)
				map.put("GE_quantity", subscription.getPollParameters().getGE_quantity());
			if (subscription.getPollParameters().getLT_quantity() != null)
				map.put("LT_quantity", subscription.getPollParameters().getLT_quantity());
			if (subscription.getPollParameters().getLE_quantity() != null)
				map.put("LE_quantity", subscription.getPollParameters().getLE_quantity());
			if (subscription.getPollParameters().getOrderBy() != null)
				map.put("orderBy", subscription.getPollParameters().getOrderBy());
			if (subscription.getPollParameters().getOrderDirection() != null)
				map.put("orderDirection", subscription.getPollParameters().getOrderDirection());
			if (subscription.getPollParameters().getEventCountLimit() != null)
				map.put("eventCountLimit", subscription.getPollParameters().getEventCountLimit());
			if (subscription.getPollParameters().getMaxEventCount() != null)
				map.put("maxEventCount", subscription.getPollParameters().getMaxEventCount());

			JobDetail job = newJob(MysqlSubscriptionTask.class)
					.withIdentity(subscription.getSubscriptionID(),
							subscription.getPollParameters().getQueryName()).setJobData(map)
					.build();

			Trigger trigger = newTrigger()
					.withIdentity(subscription.getSubscriptionID(),
							subscription.getPollParameters().getQueryName())
					.startNow()
					//.withSchedule(cronSchedule(subscription.getCronExpression()))
					.forJob(subscription.getSubscriptionID(),
							subscription.getPollParameters().getQueryName()).build();

			// ClassPathXmlApplicationContext context = new
			// ClassPathXmlApplicationContext(
			// "classpath:QuartzConfig.xml");
			// Scheduler sched = (Scheduler) context
			// .getBean("schedulerFactoryBean");

			if (MysqlSubscription.sched.isStarted() != true)
				MysqlSubscription.sched.start();
			MysqlSubscription.sched.scheduleJob(job, trigger);
			Configuration.logger.log(Level.INFO, "Subscription ID: "
					+ subscription.getSubscriptionID()
					+ " is added to quartz scheduler. ");
		} catch (SchedulerException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
		}
	}

	private void addScheduleToQuartz(String queryName, String subscriptionID,
			String dest, String cronExpression, String eventType,
			String GE_eventTime, String LT_eventTime, String GE_recordTime,
			String LT_recordTime, String EQ_action, String EQ_bizStep,
			String EQ_disposition, String EQ_readPoint, String WD_readPoint,
			String EQ_bizLocation, String WD_bizLocation,
			String EQ_transformationID, String MATCH_epc,
			String MATCH_parentID, String MATCH_inputEPC,
			String MATCH_outputEPC, String MATCH_anyEPC, String MATCH_epcClass,
			String MATCH_inputEPCClass, String MATCH_outputEPCClass,
			String MATCH_anyEPCClass, String EQ_quantity, String GT_quantity,
			String GE_quantity, String LT_quantity, String LE_quantity,
			String orderBy, String orderDirection, String eventCountLimit,
			String maxEventCount, Map<String, String> paramMap) {
		try {
			JobDataMap map = new JobDataMap();
			map.put("queryName", queryName);
			map.put("subscriptionID", subscriptionID);
			map.put("dest", dest);
			map.put("cronExpression", cronExpression);

			if (eventType != null)
				map.put("eventType", eventType);
			if (GE_eventTime != null)
				map.put("GE_eventTime", GE_eventTime);
			if (LT_eventTime != null)
				map.put("LT_eventTime", LT_eventTime);
			if (GE_recordTime != null)
				map.put("GE_recordTime", GE_recordTime);
			if (LT_recordTime != null)
				map.put("LT_recordTime", LT_recordTime);
			if (EQ_action != null)
				map.put("EQ_action", EQ_action);
			if (EQ_bizStep != null)
				map.put("EQ_bizStep", EQ_bizStep);
			if (EQ_disposition != null)
				map.put("EQ_disposition", EQ_disposition);
			if (EQ_readPoint != null)
				map.put("EQ_readPoint", EQ_readPoint);
			if (WD_readPoint != null)
				map.put("WD_readPoint", WD_readPoint);
			if (EQ_bizLocation != null)
				map.put("EQ_bizLocation", EQ_bizLocation);
			if (WD_bizLocation != null)
				map.put("WD_bizLocation", WD_bizLocation);
			if (EQ_transformationID != null)
				map.put("EQ_transformationID", EQ_transformationID);
			if (MATCH_epc != null)
				map.put("MATCH_epc", MATCH_epc);
			if (MATCH_parentID != null)
				map.put("MATCH_parentID", MATCH_parentID);
			if (MATCH_inputEPC != null)
				map.put("MATCH_inputEPC", MATCH_inputEPC);
			if (MATCH_outputEPC != null)
				map.put("MATCH_outputEPC", MATCH_outputEPC);
			if (MATCH_anyEPC != null)
				map.put("MATCH_anyEPC", MATCH_anyEPC);
			if (MATCH_epcClass != null)
				map.put("MATCH_epcClass", MATCH_epcClass);
			if (MATCH_inputEPCClass != null)
				map.put("MATCH_inputEPCClass", MATCH_inputEPCClass);
			if (MATCH_outputEPCClass != null)
				map.put("MATCH_outputEPCClass", MATCH_outputEPCClass);
			if (MATCH_anyEPCClass != null)
				map.put("MATCH_anyEPCClass", MATCH_anyEPCClass);
			if (EQ_quantity != null)
				map.put("EQ_quantity", EQ_quantity);
			if (GT_quantity != null)
				map.put("GT_quantity", GT_quantity);
			if (GE_quantity != null)
				map.put("GE_quantity", GE_quantity);
			if (LT_quantity != null)
				map.put("LT_quantity", LT_quantity);
			if (LE_quantity != null)
				map.put("LE_quantity", LE_quantity);
			if (orderBy != null)
				map.put("orderBy", orderBy);
			if (orderDirection != null)
				map.put("orderDirection", orderDirection);
			if (eventCountLimit != null)
				map.put("eventCountLimit", eventCountLimit);
			if (maxEventCount != null)
				map.put("maxEventCount", maxEventCount);
			if (paramMap != null)
				map.put("paramMap", paramMap);
			JobDetail job = newJob(MysqlSubscriptionTask.class)
					.withIdentity(subscriptionID, queryName).setJobData(map)
					.storeDurably(false).build();

			Trigger trigger = newTrigger()
					.withIdentity(subscriptionID, queryName).startNow()
					.withSchedule(cronSchedule(cronExpression)).build();

			// ClassPathXmlApplicationContext context = new
			// ClassPathXmlApplicationContext(
			// "classpath:QuartzConfig.xml");
			// Scheduler sched = (Scheduler) context
			// .getBean("schedulerFactoryBean");

			if (MysqlSubscription.sched.isStarted() != true)
				MysqlSubscription.sched.start();
			MysqlSubscription.sched.scheduleJob(job, trigger);

			Configuration.logger.log(Level.INFO, "Subscription ID: "
					+ subscriptionID + " is added to quartz scheduler. ");
		} catch (SchedulerException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
		}
	}

	@SuppressWarnings("resource")
	private boolean addScheduleToDB(String queryName, String subscriptionID,
			String dest, String cronExpression, String eventType,
			String GE_eventTime, String LT_eventTime, String GE_recordTime,
			String LT_recordTime, String EQ_action, String EQ_bizStep,
			String EQ_disposition, String EQ_readPoint, String WD_readPoint,
			String EQ_bizLocation, String WD_bizLocation,
			String EQ_transformationID, String MATCH_epc,
			String MATCH_parentID, String MATCH_inputEPC,
			String MATCH_outputEPC, String MATCH_anyEPC, String MATCH_epcClass,
			String MATCH_inputEPCClass, String MATCH_outputEPCClass,
			String MATCH_anyEPCClass, String EQ_quantity, String GT_quantity,
			String GE_quantity, String LT_quantity, String LE_quantity,
			String orderBy, String orderDirection, String eventCountLimit,
			String maxEventCount, Map<String, String> paramMap) {
/*
		SubscriptionType st = new SubscriptionType(queryName, subscriptionID,
				dest, cronExpression, eventType, GE_eventTime, LT_eventTime,
				GE_recordTime, LT_recordTime, EQ_action, EQ_bizStep,
				EQ_disposition, EQ_readPoint, WD_readPoint, EQ_bizLocation,
				WD_bizLocation, EQ_transformationID, MATCH_epc, MATCH_parentID,
				MATCH_inputEPC, MATCH_outputEPC, MATCH_anyEPC, MATCH_epcClass,
				MATCH_inputEPCClass, MATCH_outputEPCClass, MATCH_anyEPCClass,
				EQ_quantity, GT_quantity, GE_quantity, LT_quantity,
				LE_quantity, orderBy, orderDirection, eventCountLimit,
				maxEventCount, paramMap);
				*/
		ApplicationContext ctx = new GenericXmlApplicationContext(
				"classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx
				.getBean("mongoTemplate");

		List<SubscriptionType> existenceTest = mongoOperation.find(new Query(
				Criteria.where("subscriptionID").is(subscriptionID)),
				SubscriptionType.class);
		if (existenceTest.size() != 0)
			return false;
		if (existenceTest.size() == 0)
			mongoOperation.save(null);

		Configuration.logger.log(Level.INFO, "Subscription ID: "
				+ subscriptionID + " is added to DB. ");
		((AbstractApplicationContext) ctx).close();
		return true;
	}

	private void removeScheduleFromQuartz(SubscriptionType subscription) {
		try {
			MysqlSubscription.sched.unscheduleJob(triggerKey(
					subscription.getSubscriptionID(),
					subscription.getPollParameters().getQueryName()));
			MysqlSubscription.sched.deleteJob(jobKey(
					subscription.getSubscriptionID(),
					subscription.getPollParameters().getQueryName()));
			Configuration.logger.log(Level.INFO, "Subscription ID: "
					+ subscription + " is removed from scheduler");
		} catch (SchedulerException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
		}
	}

	private void removeScheduleFromDB(MongoOperations mongoOperation,
			SubscriptionType subscription) {
		mongoOperation.remove(
				new Query(Criteria.where("subscriptionID").is(
						subscription.getSubscriptionID())),
				SubscriptionType.class);
		Configuration.logger.log(Level.INFO, "Subscription ID: " + subscription
				+ " is removed from DB");
	}

	@SuppressWarnings("rawtypes")
	private String makeErrorResult(String err, Class type) {
		if (type == InvalidURIException.class) {
			InvalidURIException e = new InvalidURIException();
			e.setReason(err);
			EPCISQueryDocumentType retDoc = new EPCISQueryDocumentType();
			EPCISQueryBodyType retBody = new EPCISQueryBodyType();
			retBody.setInvalidURIException(e);
			retDoc.setEPCISBody(retBody);
			StringWriter sw = new StringWriter();
			JAXB.marshal(retDoc, sw);
			return sw.toString();
		}
		if (type == QueryParameterException.class) {
			QueryParameterException e = new QueryParameterException();
			e.setReason(err);
			EPCISQueryDocumentType retDoc = new EPCISQueryDocumentType();
			EPCISQueryBodyType retBody = new EPCISQueryBodyType();
			retBody.setQueryParameterException(e);
			retDoc.setEPCISBody(retBody);
			StringWriter sw = new StringWriter();
			JAXB.marshal(retDoc, sw);
			return sw.toString();
		}
		if (type == SubscriptionControlsException.class) {
			SubscriptionControlsException e = new SubscriptionControlsException();
			e.setReason(err);
			EPCISQueryDocumentType retDoc = new EPCISQueryDocumentType();
			EPCISQueryBodyType retBody = new EPCISQueryBodyType();
			retBody.setSubscriptionControlsException(e);
			retDoc.setEPCISBody(retBody);
			StringWriter sw = new StringWriter();
			JAXB.marshal(retDoc, sw);
			return sw.toString();
		}
		if (type == QueryTooLargeException.class) {
			QueryTooLargeException e = new QueryTooLargeException();
			e.setReason(err);
			EPCISQueryDocumentType retDoc = new EPCISQueryDocumentType();
			EPCISQueryBodyType retBody = new EPCISQueryBodyType();
			retBody.setQueryTooLargeException(e);
			retDoc.setEPCISBody(retBody);
			StringWriter sw = new StringWriter();
			JAXB.marshal(retDoc, sw);
			return sw.toString();
		}
		if (type == SubscribeNotPermittedException.class) {
			SubscribeNotPermittedException e = new SubscribeNotPermittedException();
			e.setReason(err);
			EPCISQueryDocumentType retDoc = new EPCISQueryDocumentType();
			EPCISQueryBodyType retBody = new EPCISQueryBodyType();
			retBody.setSubscribeNotPermittedException(e);
			retDoc.setEPCISBody(retBody);
			StringWriter sw = new StringWriter();
			JAXB.marshal(retDoc, sw);
			return sw.toString();
		}
		return null;
	}
}
