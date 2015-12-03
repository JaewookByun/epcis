package org.oliot.epcis.service.query.mongodb;

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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONObject;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.epcis.security.OAuthUtil;
import org.oliot.epcis.serde.mongodb.AggregationEventReadConverter;
import org.oliot.epcis.serde.mongodb.MasterDataReadConverter;
import org.oliot.epcis.serde.mongodb.ObjectEventReadConverter;
import org.oliot.epcis.serde.mongodb.QuantityEventReadConverter;
import org.oliot.epcis.serde.mongodb.TransactionEventReadConverter;
import org.oliot.epcis.serde.mongodb.TransformationEventReadConverter;
import org.oliot.model.epcis.AggregationEventType;
import org.oliot.model.epcis.AttributeType;
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
import org.oliot.model.epcis.QuerySchedule;
import org.oliot.model.epcis.QueryTooLargeException;
import org.oliot.model.epcis.SubscribeNotPermittedException;
import org.oliot.model.epcis.SubscriptionControls;
import org.oliot.model.epcis.SubscriptionControlsException;
import org.oliot.model.epcis.SubscriptionType;
import org.oliot.model.epcis.TransactionEventType;
import org.oliot.model.epcis.TransformationEventType;
import org.oliot.model.epcis.VocabularyElementType;
import org.oliot.model.epcis.VocabularyListType;
import org.oliot.model.epcis.VocabularyType;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.PathVariable;

import static org.quartz.TriggerKey.*;
import static org.quartz.JobKey.*;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import static org.oliot.epcis.service.query.mongodb.MongoQueryUtil.*;

/**
 * Copyright (C) 2014 Jaewook Byun
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

public class MongoQueryService {

	public String subscribe(SubscriptionType subscription) {
		String queryName = subscription.getQueryName();
		String subscriptionID = subscription.getSubscriptionID();
		String dest = subscription.getDest();
		String cronExpression = subscription.getCronExpression();
		boolean reportIfEmpty = subscription.isReportIfEmpty();
		String initialRecordTime = subscription.getInitialRecordTime();
		String eventType = subscription.getEventType();
		String GE_eventTime = subscription.getGE_eventTime();
		String LT_eventTime = subscription.getLT_eventTime();
		String GE_recordTime = subscription.getGE_recordTime();
		String LT_recordTime = subscription.getLT_recordTime();
		String EQ_action = subscription.getEQ_action();
		String EQ_bizStep = subscription.getEQ_bizStep();
		String EQ_disposition = subscription.getEQ_disposition();
		String EQ_readPoint = subscription.getEQ_readPoint();
		String WD_readPoint = subscription.getWD_readPoint();
		String EQ_bizLocation = subscription.getEQ_bizLocation();
		String WD_bizLocation = subscription.getWD_bizLocation();
		String EQ_transformationID = subscription.getEQ_transformationID();
		String MATCH_epc = subscription.getMATCH_epc();
		String MATCH_parentID = subscription.getMATCH_parentID();
		String MATCH_inputEPC = subscription.getMATCH_inputEPC();
		String MATCH_outputEPC = subscription.getMATCH_outputEPC();
		String MATCH_anyEPC = subscription.getMATCH_anyEPC();
		String MATCH_epcClass = subscription.getMATCH_epcClass();
		String MATCH_inputEPCClass = subscription.getMATCH_inputEPCClass();
		String MATCH_outputEPCClass = subscription.getMATCH_outputEPCClass();
		String MATCH_anyEPCClass = subscription.getMATCH_anyEPCClass();
		String EQ_quantity = subscription.getEQ_quantity();
		String GT_quantity = subscription.getGT_quantity();
		String GE_quantity = subscription.getGE_quantity();
		String LT_quantity = subscription.getLT_quantity();
		String LE_quantity = subscription.getLE_quantity();
		String orderBy = subscription.getOrderBy();
		String orderDirection = subscription.getOrderDirection();
		String eventCountLimit = subscription.getEventCountLimit();
		String maxEventCount = subscription.getMaxEventCount();
		Map<String, String> paramMap = subscription.getParamMap();

		// Oliot EPCIS doesn't support ignoreReceivedEvent for SOAP Interface
		String result = subscribe(queryName, subscriptionID, dest, cronExpression, false, reportIfEmpty,
				initialRecordTime, eventType, GE_eventTime, LT_eventTime, GE_recordTime, LT_recordTime, EQ_action,
				EQ_bizStep, EQ_disposition, EQ_readPoint, WD_readPoint, EQ_bizLocation, WD_bizLocation,
				EQ_transformationID, MATCH_epc, MATCH_parentID, MATCH_inputEPC, MATCH_outputEPC, MATCH_anyEPC,
				MATCH_epcClass, MATCH_inputEPCClass, MATCH_outputEPCClass, MATCH_anyEPCClass, EQ_quantity, GT_quantity,
				GE_quantity, LT_quantity, LE_quantity, orderBy, orderDirection, eventCountLimit, maxEventCount, null,
				paramMap);

		return result;
	}

	public String subscribeEventQuery(String queryName, String subscriptionID, String dest, String cronExpression,
			boolean ignoreReceivedEvent, boolean reportIfEmpty, String initialRecordTimeStr, String eventType,
			String GE_eventTime, String LT_eventTime, String GE_recordTime, String LT_recordTime, String EQ_action,
			String EQ_bizStep, String EQ_disposition, String EQ_readPoint, String WD_readPoint, String EQ_bizLocation,
			String WD_bizLocation, String EQ_transformationID, String MATCH_epc, String MATCH_parentID,
			String MATCH_inputEPC, String MATCH_outputEPC, String MATCH_anyEPC, String MATCH_epcClass,
			String MATCH_inputEPCClass, String MATCH_outputEPCClass, String MATCH_anyEPCClass, String EQ_quantity,
			String GT_quantity, String GE_quantity, String LT_quantity, String LE_quantity, String orderBy,
			String orderDirection, String eventCountLimit, String maxEventCount, String format,
			Map<String, String> paramMap) {

		// M27 - query params' constraint
		// M39 - query params' constraint
		String reason = checkConstraintSimpleEventQuery(queryName, eventType, GE_eventTime, LT_eventTime, GE_recordTime,
				LT_recordTime, EQ_action, EQ_bizStep, EQ_disposition, EQ_readPoint, WD_readPoint, EQ_bizLocation,
				WD_bizLocation, EQ_transformationID, MATCH_epc, MATCH_parentID, MATCH_inputEPC, MATCH_outputEPC,
				MATCH_anyEPC, MATCH_epcClass, MATCH_inputEPCClass, MATCH_outputEPCClass, MATCH_anyEPCClass, EQ_quantity,
				GT_quantity, GE_quantity, LT_quantity, LE_quantity, orderBy, orderDirection, eventCountLimit,
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
			return makeErrorResult(e.toString(), SubscriptionControlsException.class);
		}

		// Add Schedule with Query
		addScheduleToQuartz(queryName, subscriptionID, dest, cronExpression, ignoreReceivedEvent, reportIfEmpty,
				initialRecordTimeStr, eventType, GE_eventTime, LT_eventTime, GE_recordTime, LT_recordTime, EQ_action,
				EQ_bizStep, EQ_disposition, EQ_readPoint, WD_readPoint, EQ_bizLocation, WD_bizLocation,
				EQ_transformationID, MATCH_epc, MATCH_parentID, MATCH_inputEPC, MATCH_outputEPC, MATCH_anyEPC,
				MATCH_epcClass, MATCH_inputEPCClass, MATCH_outputEPCClass, MATCH_anyEPCClass, EQ_quantity, GT_quantity,
				GE_quantity, LT_quantity, LE_quantity, orderBy, orderDirection, eventCountLimit, maxEventCount, format,
				paramMap);

		// Manage Subscription Persistently
		addScheduleToDB(queryName, subscriptionID, dest, cronExpression, ignoreReceivedEvent, reportIfEmpty,
				initialRecordTimeStr, eventType, GE_eventTime, LT_eventTime, GE_recordTime, LT_recordTime, EQ_action,
				EQ_bizStep, EQ_disposition, EQ_readPoint, WD_readPoint, EQ_bizLocation, WD_bizLocation,
				EQ_transformationID, MATCH_epc, MATCH_parentID, MATCH_inputEPC, MATCH_outputEPC, MATCH_anyEPC,
				MATCH_epcClass, MATCH_inputEPCClass, MATCH_outputEPCClass, MATCH_anyEPCClass, EQ_quantity, GT_quantity,
				GE_quantity, LT_quantity, LE_quantity, orderBy, orderDirection, eventCountLimit, maxEventCount, format,
				paramMap);

		String retString = "SubscriptionID : " + subscriptionID + " is successfully triggered. ";
		return retString;
	}

	// Soap Query Adaptor
	public void subscribe(String queryName, QueryParams params, URI dest, SubscriptionControls controls,
			String subscriptionID) {

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

		// Subscription Control Processing
		/*
		 * QuerySchedule: (Optional) Defines the periodic schedule on which the
		 * query is to be executed. See Section 8.2.5.3. Exactly one of schedule
		 * or trigger is required; if both are specified or both are omitted,
		 * the implementation SHALL raise a SubscriptionControls- Exception..
		 */
		QuerySchedule querySchedule = controls.getSchedule();
		if (cronExpression == null) {
			String sec = querySchedule.getSecond();
			String min = querySchedule.getMinute();
			String hours = querySchedule.getHour();
			String dayOfMonth = querySchedule.getDayOfMonth();
			String month = querySchedule.getMonth();
			String dayOfWeek = querySchedule.getDayOfWeek();
			cronExpression = sec + " " + min + " " + hours + " " + dayOfMonth + " " + month + " " + dayOfWeek;
		}

		/*
		 * InitialRecordTime: (Optional) Specifies a time used to constrain what
		 * events are considered when processing the query when it is executed
		 * for the first time. See Section 8.2.5.2. If omitted, defaults to the
		 * time at which the subscription is created.
		 */
		XMLGregorianCalendar initialRecordTime = controls.getInitialRecordTime();
		if (initialRecordTime == null) {
			try {
				initialRecordTime = DatatypeFactory.newInstance().newXMLGregorianCalendar();
			} catch (DatatypeConfigurationException e) {

				e.printStackTrace();
			}
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		Date initialRecordDate = initialRecordTime.toGregorianCalendar().getTime();
		String initialRecordTimeStr = sdf.format(initialRecordDate);
		/*
		 * reportIfEmpty: If true, a QueryResults instance is always sent to the
		 * subscriber when the query is executed. If false, a QueryResults
		 * instance is sent to the subscriber only when the results are
		 * non-empty.
		 */
		reportIfEmpty = controls.isReportIfEmpty();

		// Oliot doesn't support ignoreReceivedEvent for SOAP interface
		// Oliot doesn't support to select output format for SOAP interface
		subscribe(queryName, subscriptionID, destStr, cronExpression, false, reportIfEmpty, initialRecordTimeStr,
				eventType, GE_eventTime, LT_eventTime, GE_recordTime, LT_recordTime, EQ_action, EQ_bizStep,
				EQ_disposition, EQ_readPoint, WD_readPoint, EQ_bizLocation, WD_bizLocation, EQ_transformationID,
				MATCH_epc, MATCH_parentID, MATCH_inputEPC, MATCH_outputEPC, MATCH_anyEPC, MATCH_epcClass,
				MATCH_inputEPCClass, MATCH_outputEPCClass, MATCH_anyEPCClass, EQ_quantity, GT_quantity, GE_quantity,
				LT_quantity, LE_quantity, orderBy, orderDirection, eventCountLimit, maxEventCount, null, extMap);

	}

	public String subscribe(String queryName, String subscriptionID, String dest, String cronExpression,
			boolean ignoreReceivedEvent, boolean reportIfEmpty, String initialRecordTimeStr, String eventType,
			String GE_eventTime, String LT_eventTime, String GE_recordTime, String LT_recordTime, String EQ_action,
			String EQ_bizStep, String EQ_disposition, String EQ_readPoint, String WD_readPoint, String EQ_bizLocation,
			String WD_bizLocation, String EQ_transformationID, String MATCH_epc, String MATCH_parentID,
			String MATCH_inputEPC, String MATCH_outputEPC, String MATCH_anyEPC, String MATCH_epcClass,
			String MATCH_inputEPCClass, String MATCH_outputEPCClass, String MATCH_anyEPCClass, String EQ_quantity,
			String GT_quantity, String GE_quantity, String LT_quantity, String LE_quantity, String orderBy,
			String orderDirection, String eventCountLimit, String maxEventCount, String format,
			Map<String, String> paramMap) {

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
			return makeErrorResult("Fill the mandatory field in subscribe method", QueryParameterException.class);
		}

		// M46
		if (queryName.equals("SimpleMasterDataQuery")) {
			return makeErrorResult("SimpleMasterDataQuery is not available in subscription method",
					SubscribeNotPermittedException.class);
		}

		String retString = "";
		if (queryName.equals("SimpleEventQuery")) {
			retString = subscribeEventQuery(queryName, subscriptionID, dest, cronExpression, ignoreReceivedEvent,
					reportIfEmpty, initialRecordTimeStr, eventType, GE_eventTime, LT_eventTime, GE_recordTime,
					LT_recordTime, EQ_action, EQ_bizStep, EQ_disposition, EQ_readPoint, WD_readPoint, EQ_bizLocation,
					WD_bizLocation, EQ_transformationID, MATCH_epc, MATCH_parentID, MATCH_inputEPC, MATCH_outputEPC,
					MATCH_anyEPC, MATCH_epcClass, MATCH_inputEPCClass, MATCH_outputEPCClass, MATCH_anyEPCClass,
					EQ_quantity, GT_quantity, GE_quantity, LT_quantity, LE_quantity, orderBy, orderDirection,
					eventCountLimit, maxEventCount, format, paramMap);
		}

		return retString;
	}

	public void unsubscribe(String subscriptionID) {
		ApplicationContext ctx = new GenericXmlApplicationContext("classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");

		// Its size should be 0 or 1
		List<SubscriptionType> subscriptions = mongoOperation
				.find(new Query(Criteria.where("subscriptionID").is(subscriptionID)), SubscriptionType.class);

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

		ApplicationContext ctx = new GenericXmlApplicationContext("classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");

		List<SubscriptionType> allSubscription = mongoOperation
				.find(new Query(Criteria.where("queryName").is(queryName)), SubscriptionType.class);

		JSONArray retArray = new JSONArray();
		for (int i = 0; i < allSubscription.size(); i++) {
			SubscriptionType subscription = allSubscription.get(i);
			retArray.put(subscription.getSubscriptionID());
		}
		((AbstractApplicationContext) ctx).close();
		return retArray.toString(1);
	}

	public List<String> getSubscriptionIDs(String queryName) {
		ApplicationContext ctx = new GenericXmlApplicationContext("classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");

		List<SubscriptionType> allSubscription = mongoOperation
				.find(new Query(Criteria.where("queryName").is(queryName)), SubscriptionType.class);
		List<String> retList = new ArrayList<String>();
		for (int i = 0; i < allSubscription.size(); i++) {
			SubscriptionType subscription = allSubscription.get(i);
			retList.add(subscription.getSubscriptionID());
		}
		((AbstractApplicationContext) ctx).close();
		return retList;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String pollEventQuery(String queryName, String eventType, String GE_eventTime, String LT_eventTime,
			String GE_recordTime, String LT_recordTime, String EQ_action, String EQ_bizStep, String EQ_disposition,
			String EQ_readPoint, String WD_readPoint, String EQ_bizLocation, String WD_bizLocation,
			String EQ_transformationID, String MATCH_epc, String MATCH_parentID, String MATCH_inputEPC,
			String MATCH_outputEPC, String MATCH_anyEPC, String MATCH_epcClass, String MATCH_inputEPCClass,
			String MATCH_outputEPCClass, String MATCH_anyEPCClass, String EQ_quantity, String GT_quantity,
			String GE_quantity, String LT_quantity, String LE_quantity, String orderBy, String orderDirection,
			String eventCountLimit, String maxEventCount, String format, String userID, List<String> friendList,
			Map<String, String> paramMap) {

		// M27 - query params' constraint
		// M39 - query params' constraint
		String reason = checkConstraintSimpleEventQuery(queryName, eventType, GE_eventTime, LT_eventTime, GE_recordTime,
				LT_recordTime, EQ_action, EQ_bizStep, EQ_disposition, EQ_readPoint, WD_readPoint, EQ_bizLocation,
				WD_bizLocation, EQ_transformationID, MATCH_epc, MATCH_parentID, MATCH_inputEPC, MATCH_outputEPC,
				MATCH_anyEPC, MATCH_epcClass, MATCH_inputEPCClass, MATCH_outputEPCClass, MATCH_anyEPCClass, EQ_quantity,
				GT_quantity, GE_quantity, LT_quantity, LE_quantity, orderBy, orderDirection, eventCountLimit,
				maxEventCount, paramMap);
		if (reason != null) {
			return makeErrorResult(reason, QueryParameterException.class);
		}

		// Make Base Result Document
		EPCISQueryDocumentType epcisQueryDocumentType = null;
		JSONObject retJSON = new JSONObject();

		if (format == null || format.equals("XML")) {
			epcisQueryDocumentType = makeBaseResultDocument(queryName);
		} else if (format.equals("JSON")) {
			// Do Nothing
		} else {
			return makeErrorResult("format param should be one of XML or JSON", QueryParameterException.class);
		}

		ApplicationContext ctx = new GenericXmlApplicationContext("classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");

		// Prepare container which query results are included
		// eventObjects : Container which all the query results (events) will be
		// contained
		List<Object> eventObjects = null;
		if (format == null || format.equals("XML")) {
			eventObjects = epcisQueryDocumentType.getEPCISBody().getQueryResults().getResultsBody().getEventList()
					.getObjectEventOrAggregationEventOrQuantityEvent();
		} else {
			// foramt == JSON -> Do Nothing
		}
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
			// Aggregation Event Collection
			DBCollection collection = mongoOperation.getCollection("AggregationEvent");
			// Queries
			List<DBObject> queryList = makeQueryObjects("AggregationEvent", GE_eventTime, LT_eventTime, GE_recordTime,
					LT_recordTime, EQ_action, EQ_bizStep, EQ_disposition, EQ_readPoint, WD_readPoint, EQ_bizLocation,
					WD_bizLocation, EQ_transformationID, MATCH_epc, MATCH_parentID, MATCH_inputEPC, MATCH_outputEPC,
					MATCH_anyEPC, MATCH_epcClass, MATCH_inputEPCClass, MATCH_outputEPCClass, MATCH_anyEPCClass,
					EQ_quantity, GT_quantity, GE_quantity, LT_quantity, LE_quantity, orderBy, orderDirection,
					eventCountLimit, maxEventCount, paramMap);

			// Merge All the queries with $and
			DBObject baseQuery = new BasicDBObject();
			DBCursor cursor;
			if (queryList.isEmpty() == false) {
				BasicDBList aggreQueryList = new BasicDBList();
				for (int i = 0; i < queryList.size(); i++) {
					aggreQueryList.add(queryList.get(i));
				}
				baseQuery.put("$and", aggreQueryList);
				// Query
				cursor = collection.find(baseQuery);
			} else {
				cursor = collection.find();
			}
			// Sort and Limit
			cursor = makeSortedLimitedCursor(cursor, orderBy, orderDirection, eventCountLimit);

			JSONArray aggrJSONArray = new JSONArray();

			while (cursor.hasNext()) {
				DBObject dbObject = cursor.next();

				if (OAuthUtil.isAccessible(userID, friendList, dbObject) == false) {
					continue;
				}

				if (format == null || format.equals("XML")) {
					AggregationEventReadConverter con = new AggregationEventReadConverter();
					JAXBElement element = new JAXBElement(new QName("AggregationEvent"), AggregationEventType.class,
							con.convert(dbObject));
					eventObjects.add(element);
				} else {
					dbObject.removeField("_id");
					aggrJSONArray.put(dbObject);
				}
			}
			if (aggrJSONArray.length() > 0) {
				retJSON.put("AggregationEvent", aggrJSONArray);
			}
		}

		// For Each Event Type!
		if (toGetObjectEvent == true) {

			// Aggregation Event Collection
			DBCollection collection = mongoOperation.getCollection("ObjectEvent");
			// Queries
			List<DBObject> queryList = makeQueryObjects("ObjectEvent", GE_eventTime, LT_eventTime, GE_recordTime,
					LT_recordTime, EQ_action, EQ_bizStep, EQ_disposition, EQ_readPoint, WD_readPoint, EQ_bizLocation,
					WD_bizLocation, EQ_transformationID, MATCH_epc, MATCH_parentID, MATCH_inputEPC, MATCH_outputEPC,
					MATCH_anyEPC, MATCH_epcClass, MATCH_inputEPCClass, MATCH_outputEPCClass, MATCH_anyEPCClass,
					EQ_quantity, GT_quantity, GE_quantity, LT_quantity, LE_quantity, orderBy, orderDirection,
					eventCountLimit, maxEventCount, paramMap);

			// Merge All the queries with $and
			DBObject baseQuery = new BasicDBObject();
			DBCursor cursor;
			if (queryList.isEmpty() == false) {
				BasicDBList aggreQueryList = new BasicDBList();
				for (int i = 0; i < queryList.size(); i++) {
					aggreQueryList.add(queryList.get(i));
				}
				baseQuery.put("$and", aggreQueryList);
				// Query
				cursor = collection.find(baseQuery);
			} else {
				cursor = collection.find();
			}
			// Sort and Limit
			cursor = makeSortedLimitedCursor(cursor, orderBy, orderDirection, eventCountLimit);

			JSONArray objJSONArray = new JSONArray();

			while (cursor.hasNext()) {
				DBObject dbObject = cursor.next();

				if (OAuthUtil.isAccessible(userID, friendList, dbObject) == false) {
					continue;
				}

				if (format == null || format.equals("XML")) {
					ObjectEventReadConverter con = new ObjectEventReadConverter();
					JAXBElement element = new JAXBElement(new QName("ObjectEvent"), ObjectEventType.class,
							con.convert(dbObject));
					eventObjects.add(element);
				} else {
					dbObject.removeField("_id");
					objJSONArray.put(dbObject);
				}
			}
			if (objJSONArray.length() > 0) {
				retJSON.put("ObjectEvent", objJSONArray);
			}
		}
		if (toGetQuantityEvent == true) {
			// Aggregation Event Collection
			DBCollection collection = mongoOperation.getCollection("QuantityEvent");
			// Queries
			List<DBObject> queryList = makeQueryObjects("QuantityEvent", GE_eventTime, LT_eventTime, GE_recordTime,
					LT_recordTime, EQ_action, EQ_bizStep, EQ_disposition, EQ_readPoint, WD_readPoint, EQ_bizLocation,
					WD_bizLocation, EQ_transformationID, MATCH_epc, MATCH_parentID, MATCH_inputEPC, MATCH_outputEPC,
					MATCH_anyEPC, MATCH_epcClass, MATCH_inputEPCClass, MATCH_outputEPCClass, MATCH_anyEPCClass,
					EQ_quantity, GT_quantity, GE_quantity, LT_quantity, LE_quantity, orderBy, orderDirection,
					eventCountLimit, maxEventCount, paramMap);

			// Merge All the queries with $and
			DBObject baseQuery = new BasicDBObject();
			DBCursor cursor;
			if (queryList.isEmpty() == false) {
				BasicDBList aggreQueryList = new BasicDBList();
				for (int i = 0; i < queryList.size(); i++) {
					aggreQueryList.add(queryList.get(i));
				}
				baseQuery.put("$and", aggreQueryList);
				// Query
				cursor = collection.find(baseQuery);
			} else {
				cursor = collection.find();
			}
			// Sort and Limit
			cursor = makeSortedLimitedCursor(cursor, orderBy, orderDirection, eventCountLimit);

			JSONArray qntJSONArray = new JSONArray();

			while (cursor.hasNext()) {
				DBObject dbObject = cursor.next();

				if (OAuthUtil.isAccessible(userID, friendList, dbObject) == false) {
					continue;
				}

				if (format == null || format.equals("XML")) {
					QuantityEventReadConverter con = new QuantityEventReadConverter();
					JAXBElement element = new JAXBElement(new QName("QuantityEvent"), QuantityEventType.class,
							con.convert(dbObject));
					eventObjects.add(element);
				} else {
					dbObject.removeField("_id");
					qntJSONArray.put(dbObject);
				}
			}
			if (qntJSONArray.length() > 0) {
				retJSON.put("QuantityEvent", qntJSONArray);
			}
		}
		if (toGetTransactionEvent == true) {
			// Aggregation Event Collection
			DBCollection collection = mongoOperation.getCollection("TransactionEvent");
			// Queries
			List<DBObject> queryList = makeQueryObjects("TransactionEvent", GE_eventTime, LT_eventTime, GE_recordTime,
					LT_recordTime, EQ_action, EQ_bizStep, EQ_disposition, EQ_readPoint, WD_readPoint, EQ_bizLocation,
					WD_bizLocation, EQ_transformationID, MATCH_epc, MATCH_parentID, MATCH_inputEPC, MATCH_outputEPC,
					MATCH_anyEPC, MATCH_epcClass, MATCH_inputEPCClass, MATCH_outputEPCClass, MATCH_anyEPCClass,
					EQ_quantity, GT_quantity, GE_quantity, LT_quantity, LE_quantity, orderBy, orderDirection,
					eventCountLimit, maxEventCount, paramMap);

			// Merge All the queries with $and
			DBObject baseQuery = new BasicDBObject();
			DBCursor cursor;
			if (queryList.isEmpty() == false) {
				BasicDBList aggreQueryList = new BasicDBList();
				for (int i = 0; i < queryList.size(); i++) {
					aggreQueryList.add(queryList.get(i));
				}
				baseQuery.put("$and", aggreQueryList);
				// Query
				cursor = collection.find(baseQuery);
			} else {
				cursor = collection.find();
			}
			// Sort and Limit
			cursor = makeSortedLimitedCursor(cursor, orderBy, orderDirection, eventCountLimit);

			JSONArray transactionJSONArray = new JSONArray();

			while (cursor.hasNext()) {
				DBObject dbObject = cursor.next();

				if (OAuthUtil.isAccessible(userID, friendList, dbObject) == false) {
					continue;
				}

				if (format == null || format.equals("XML")) {
					TransactionEventReadConverter con = new TransactionEventReadConverter();
					JAXBElement element = new JAXBElement(new QName("TransactionEvent"), TransactionEventType.class,
							con.convert(dbObject));
					eventObjects.add(element);
				} else {
					dbObject.removeField("_id");
					transactionJSONArray.put(dbObject);
				}
			}
			if (transactionJSONArray.length() > 0) {
				retJSON.put("TransactionEvent", transactionJSONArray);
			}
		}
		if (toGetTransformationEvent == true) {
			// Aggregation Event Collection
			DBCollection collection = mongoOperation.getCollection("TransformationEvent");
			// Queries
			List<DBObject> queryList = makeQueryObjects("TransformationEvent", GE_eventTime, LT_eventTime,
					GE_recordTime, LT_recordTime, EQ_action, EQ_bizStep, EQ_disposition, EQ_readPoint, WD_readPoint,
					EQ_bizLocation, WD_bizLocation, EQ_transformationID, MATCH_epc, MATCH_parentID, MATCH_inputEPC,
					MATCH_outputEPC, MATCH_anyEPC, MATCH_epcClass, MATCH_inputEPCClass, MATCH_outputEPCClass,
					MATCH_anyEPCClass, EQ_quantity, GT_quantity, GE_quantity, LT_quantity, LE_quantity, orderBy,
					orderDirection, eventCountLimit, maxEventCount, paramMap);

			// Merge All the queries with $and
			DBObject baseQuery = new BasicDBObject();
			DBCursor cursor;
			if (queryList.isEmpty() == false) {
				BasicDBList aggreQueryList = new BasicDBList();
				for (int i = 0; i < queryList.size(); i++) {
					aggreQueryList.add(queryList.get(i));
				}
				baseQuery.put("$and", aggreQueryList);
				// Query
				cursor = collection.find(baseQuery);
			} else {
				cursor = collection.find();
			}
			// Sort and Limit
			cursor = makeSortedLimitedCursor(cursor, orderBy, orderDirection, eventCountLimit);

			JSONArray transformationJSONArray = new JSONArray();

			while (cursor.hasNext()) {
				DBObject dbObject = cursor.next();

				if (OAuthUtil.isAccessible(userID, friendList, dbObject) == false) {
					continue;
				}

				if (format == null || format.equals("XML")) {
					TransformationEventReadConverter con = new TransformationEventReadConverter();
					JAXBElement element = new JAXBElement(new QName("TransformationEvent"),
							TransformationEventType.class, con.convert(dbObject));
					eventObjects.add(element);
				} else {
					dbObject.removeField("_id");
					transformationJSONArray.put(dbObject);
				}
			}
			if (transformationJSONArray.length() > 0) {
				retJSON.put("TransformationEvent", transformationJSONArray);
			}
		}

		// M44
		if (maxEventCount != null) {
			if (format == null || format.equals("XML")) {
				if (eventObjects.size() > Integer.parseInt(maxEventCount)) {
					((AbstractApplicationContext) ctx).close();
					return makeErrorResult("Violate maxEventCount", QueryTooLargeException.class);
				}
			} else {
				int cnt = 0;
				if (!retJSON.isNull("AggregationEvent")) {
					cnt += retJSON.getJSONArray("AggregationEvent").length();
				}
				if (!retJSON.isNull("ObjectEvent")) {
					cnt += retJSON.getJSONArray("ObjectEvent").length();
				}
				if (!retJSON.isNull("QuantityEvent")) {
					cnt += retJSON.getJSONArray("QuantityEvent").length();
				}
				if (!retJSON.isNull("TransactionEvent")) {
					cnt += retJSON.getJSONArray("TransactionEvent").length();
				}
				if (!retJSON.isNull("TransformationEvent")) {
					cnt += retJSON.getJSONArray("TransformationEvent").length();
				}
				if (cnt > Integer.parseInt(maxEventCount)) {
					((AbstractApplicationContext) ctx).close();
					return makeErrorResult("Violate maxEventCount", QueryTooLargeException.class);
				}
			}
		}
		((AbstractApplicationContext) ctx).close();
		if (format == null || format.equals("XML")) {
			StringWriter sw = new StringWriter();
			JAXB.marshal(epcisQueryDocumentType, sw);
			return sw.toString();
		} else {
			return retJSON.toString(1);
		}
	}

	public String pollMasterDataQuery(String queryName, String vocabularyName, boolean includeAttributes,
			boolean includeChildren, String attributeNames, String eQ_name, String wD_name, String hASATTR,
			String maxElementCount, String format, Map<String, String> paramMap) {

		// Make Base Result Document
		EPCISQueryDocumentType epcisQueryDocumentType = null;
		JSONArray retArray = new JSONArray();

		if (format == null || format.equals("XML")) {
			epcisQueryDocumentType = makeBaseResultDocument(queryName);
		} else if (format.equals("JSON")) {
			// Do Nothing
		} else {
			return makeErrorResult("format param should be one of XML or JSON", QueryParameterException.class);
		}

		ApplicationContext ctx = new GenericXmlApplicationContext("classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");

		DBCollection collection = mongoOperation.getCollection("MasterData");

		// Make Query
		List<DBObject> queryList = makeMasterQueryObjects(vocabularyName, includeAttributes, includeChildren,
				attributeNames, eQ_name, wD_name, hASATTR, maxElementCount, paramMap);

		// Merge All the queries with $and
		DBObject baseQuery = new BasicDBObject();
		DBCursor cursor;
		if (queryList.isEmpty() == false) {
			BasicDBList aggreQueryList = new BasicDBList();
			for (int i = 0; i < queryList.size(); i++) {
				aggreQueryList.add(queryList.get(i));
			}
			baseQuery.put("$and", aggreQueryList);
			// Query
			cursor = collection.find(baseQuery);
		} else {
			cursor = collection.find();
		}

		// Cursor needed to ordered

		List<VocabularyType> vList = new ArrayList<>();
		while (cursor.hasNext()) {
			DBObject dbObject = cursor.next();

			if (format == null || format.equals("XML")) {
				MasterDataReadConverter con = new MasterDataReadConverter();
				VocabularyType vt = con.convert(dbObject);

				boolean isMatched = true;
				if (vt.getVocabularyElementList() != null) {
					if (vt.getVocabularyElementList().getVocabularyElement() != null) {
						List<VocabularyElementType> vetList = vt.getVocabularyElementList().getVocabularyElement();
						for (int i = 0; i < vetList.size(); i++) {
							VocabularyElementType vet = vetList.get(i);
							if (includeAttributes == false) {
								vet.setAttribute(null);
							} else if (includeAttributes == true && attributeNames != null) {
								/**
								 * attributeNames : If specified, only those
								 * attributes whose names match one of the
								 * specified names will be included in the
								 * results. If omitted, all attributes for each
								 * matching vocabulary element will be included.
								 * (To obtain a list of vocabulary element names
								 * with no attributes, specify false for
								 * includeAttributes.) The value of this
								 * parameter SHALL be ignored if
								 * includeAttributes is false. Note that this
								 * parameter does not affect which vocabulary
								 * elements are included in the result; it only
								 * limits which attributes will be included with
								 * each vocabulary element.
								 */
								isMatched = false;
								String[] attrArr = attributeNames.split(",");
								Set<String> attrSet = new HashSet<String>();
								for (int j = 0; j < attrArr.length; j++) {
									attrSet.add(attrArr[j].trim());
								}

								List<AttributeType> atList = vet.getAttribute();
								for (int j = 0; j < atList.size(); j++) {
									if (attrSet.contains(atList.get(j).getId())) {
										isMatched = true;
									}
								}
							}

							if (includeChildren == false) {
								vet.setChildren(null);
							}
						}
					}
				}
				if (isMatched == true)
					vList.add(vt);
			} else {
				boolean isMatched = true;
				dbObject.removeField("_id");
				if (includeAttributes == false) {
					dbObject.removeField("attributes");
				} else if (includeAttributes == true && attributeNames != null) {
					String[] attrArr = attributeNames.split(",");
					Set<String> attrSet = new HashSet<String>();
					for (int j = 0; j < attrArr.length; j++) {
						attrSet.add(attrArr[j].trim());
					}
					Object attrObject = dbObject.get("attributes");
					isMatched = false;
					if (attrObject != null) {
						DBObject attrDBObject = (DBObject) attrObject;
						Iterator<String> attrKeys = attrDBObject.keySet().iterator();
						while (attrKeys.hasNext()) {
							String attrKey = attrKeys.next();
							if (attrSet.contains(attrKey)) {
								isMatched = true;
							}
						}
					}

				}
				if (includeChildren == false) {
					dbObject.removeField("children");
				}

				if (isMatched == true) {
					retArray.put(dbObject);
				}
			}
		}

		if (format == null || format.equals("XML")) {
			QueryResultsBody qbt = epcisQueryDocumentType.getEPCISBody().getQueryResults().getResultsBody();

			VocabularyListType vlt = new VocabularyListType();
			vlt.setVocabulary(vList);
			qbt.setVocabularyList(vlt);
		}
		((AbstractApplicationContext) ctx).close();

		// M47
		if (maxElementCount != null) {
			try {
				int maxElement = Integer.parseInt(maxElementCount);
				if (format == null || format.equals("XML")) {
					if (vList.size() > maxElement) {
						return makeErrorResult("Too Large Master Data result", QueryTooLargeException.class);
					}
				} else {
					if (retArray.length() > maxElement) {
						return makeErrorResult("Too Large Master Data result", QueryTooLargeException.class);
					}
				}
			} catch (NumberFormatException e) {

			}
		}
		if (format == null || format.equals("XML")) {
			StringWriter sw = new StringWriter();
			JAXB.marshal(epcisQueryDocumentType, sw);
			return sw.toString();
		} else {
			return retArray.toString(1);
		}
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

		return poll(queryName, eventType, GE_eventTime, LT_eventTime, GE_recordTime, LT_recordTime, EQ_action,
				EQ_bizStep, EQ_disposition, EQ_readPoint, WD_readPoint, EQ_bizLocation, WD_bizLocation,
				EQ_transformationID, MATCH_epc, MATCH_parentID, MATCH_inputEPC, MATCH_outputEPC, MATCH_anyEPC,
				MATCH_epcClass, MATCH_inputEPCClass, MATCH_outputEPCClass, MATCH_anyEPCClass, EQ_quantity, GT_quantity,
				GE_quantity, LT_quantity, LE_quantity, orderBy, orderDirection, eventCountLimit, maxEventCount,
				vocabularyName, includeAttributes, includeChildren, attributeNames, EQ_name, WD_name, HASATTR,
				maxElementCount, null, null, null, extMap);
	}

	public String poll(@PathVariable String queryName, String eventType, String GE_eventTime, String LT_eventTime,
			String GE_recordTime, String LT_recordTime, String EQ_action, String EQ_bizStep, String EQ_disposition,
			String EQ_readPoint, String WD_readPoint, String EQ_bizLocation, String WD_bizLocation,
			String EQ_transformationID, String MATCH_epc, String MATCH_parentID, String MATCH_inputEPC,
			String MATCH_outputEPC, String MATCH_anyEPC, String MATCH_epcClass, String MATCH_inputEPCClass,
			String MATCH_outputEPCClass, String MATCH_anyEPCClass, String EQ_quantity, String GT_quantity,
			String GE_quantity, String LT_quantity, String LE_quantity, String orderBy, String orderDirection,
			String eventCountLimit, String maxEventCount,

	String vocabularyName, boolean includeAttributes, boolean includeChildren, String attributeNames, String EQ_name,
			String WD_name, String HASATTR, String maxElementCount, String format, String userID,
			List<String> friendList, Map<String, String> paramMap) {

		// M24
		if (queryName == null) {
			// It is not possible, automatically filtered by URI param
			return makeErrorResult("queryName is mandatory field in poll method", QueryParameterException.class);
		}

		if (queryName.equals("SimpleEventQuery"))
			return pollEventQuery(queryName, eventType, GE_eventTime, LT_eventTime, GE_recordTime, LT_recordTime,
					EQ_action, EQ_bizStep, EQ_disposition, EQ_readPoint, WD_readPoint, EQ_bizLocation, WD_bizLocation,
					EQ_transformationID, MATCH_epc, MATCH_parentID, MATCH_inputEPC, MATCH_outputEPC, MATCH_anyEPC,
					MATCH_epcClass, MATCH_inputEPCClass, MATCH_outputEPCClass, MATCH_anyEPCClass, EQ_quantity,
					GT_quantity, GE_quantity, LT_quantity, LE_quantity, orderBy, orderDirection, eventCountLimit,
					maxEventCount, format, userID, friendList, paramMap);

		if (queryName.equals("SimpleMasterDataQuery"))
			return pollMasterDataQuery(queryName, vocabularyName, includeAttributes, includeChildren, attributeNames,
					EQ_name, WD_name, HASATTR, maxElementCount, format, paramMap);
		return "";
	}

	private String checkConstraintSimpleEventQuery(String queryName, String eventType, String GE_eventTime,
			String LT_eventTime, String GE_recordTime, String LT_recordTime, String EQ_action, String EQ_bizStep,
			String EQ_disposition, String EQ_readPoint, String WD_readPoint, String EQ_bizLocation,
			String WD_bizLocation, String EQ_transformationID, String MATCH_epc, String MATCH_parentID,
			String MATCH_inputEPC, String MATCH_outputEPC, String MATCH_anyEPC, String MATCH_epcClass,
			String MATCH_inputEPCClass, String MATCH_outputEPCClass, String MATCH_anyEPCClass, String EQ_quantity,
			String GT_quantity, String GE_quantity, String LT_quantity, String LE_quantity, String orderBy,
			String orderDirection, String eventCountLimit, String maxEventCount, Map<String, String> paramMap) {

		// M27
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
			if (GE_eventTime != null)
				sdf.parse(GE_eventTime);
			if (LT_eventTime != null)
				sdf.parse(LT_eventTime);
			if (GE_recordTime != null)
				sdf.parse(GE_recordTime);
			if (LT_recordTime != null)
				sdf.parse(LT_recordTime);
		} catch (ParseException e) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
				if (GE_eventTime != null)
					sdf.parse(GE_eventTime);
				if (LT_eventTime != null)
					sdf.parse(LT_eventTime);
				if (GE_recordTime != null)
					sdf.parse(GE_recordTime);
				if (LT_recordTime != null)
					sdf.parse(LT_recordTime);
			} catch (ParseException e1) {
				return makeErrorResult(e.toString(), QueryParameterException.class);
			}
		}

		// M27
		if (orderBy != null) {
			if (!orderBy.equals("eventTime") && !orderBy.equals("recordTime")) {
				return makeErrorResult("orderBy should be eventTime or recordTime", QueryParameterException.class);
			}
			if (orderDirection != null) {
				if (!orderDirection.equals("ASC") && !orderDirection.equals("DESC")) {
					return makeErrorResult("orderDirection should be ASC or DESC", QueryParameterException.class);
				}
			}
		}

		// M27
		if (eventCountLimit != null) {
			try {
				int c = Integer.parseInt(eventCountLimit);
				if (c <= 0) {
					return makeErrorResult("eventCount should be natural number", QueryParameterException.class);
				}
			} catch (NumberFormatException e) {
				return makeErrorResult("eventCount: " + e.toString(), QueryParameterException.class);
			}
		}

		// M27
		if (maxEventCount != null) {
			try {
				int c = Integer.parseInt(maxEventCount);
				if (c <= 0) {
					return makeErrorResult("maxEventCount should be natural number", QueryParameterException.class);
				}
			} catch (NumberFormatException e) {
				return makeErrorResult("maxEventCount: " + e.toString(), QueryParameterException.class);
			}
		}

		// M39
		if (EQ_action != null) {
			if (!EQ_action.equals("ADD") && !EQ_action.equals("OBSERVE") && !EQ_action.equals("DELETE")) {
				return makeErrorResult("EQ_action: ADD | OBSERVE | DELETE", QueryParameterException.class);
			}
		}

		// M42
		if (eventCountLimit != null && maxEventCount != null) {
			return makeErrorResult("One of eventCountLimit and maxEventCount should be omitted",
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
		eventListType.setObjectEventOrAggregationEventOrQuantityEvent(eventObjects);
		return epcisQueryDocumentType;
	}

	boolean isExtraParameter(String paramName) {

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
			map.put("queryName", subscription.getQueryName());
			map.put("subscriptionID", subscription.getSubscriptionID());
			map.put("dest", subscription.getDest());
			map.put("cronExpression", subscription.getCronExpression());
			map.put("initialRecordTime", subscription.getInitialRecordTime());
			map.put("ignoreReceivedEvent", subscription.isIgnoreReceivedEvent());
			map.put("reportIfEmpty", subscription.isReportIfEmpty());

			if (subscription.getEventType() != null)
				map.put("eventType", subscription.getEventType());
			if (subscription.getGE_eventTime() != null)
				map.put("GE_eventTime", subscription.getGE_eventTime());
			if (subscription.getLT_eventTime() != null)
				map.put("LT_eventTime", subscription.getLT_eventTime());
			if (subscription.getGE_recordTime() != null)
				map.put("GE_recordTime", subscription.getGE_recordTime());
			if (subscription.getLT_recordTime() != null)
				map.put("LT_recordTime", subscription.getLT_recordTime());
			if (subscription.getEQ_action() != null)
				map.put("EQ_action", subscription.getEQ_action());
			if (subscription.getEQ_bizStep() != null)
				map.put("EQ_bizStep", subscription.getEQ_bizStep());
			if (subscription.getEQ_disposition() != null)
				map.put("EQ_disposition", subscription.getEQ_disposition());
			if (subscription.getEQ_readPoint() != null)
				map.put("EQ_readPoint", subscription.getEQ_readPoint());
			if (subscription.getWD_readPoint() != null)
				map.put("WD_readPoint", subscription.getWD_readPoint());
			if (subscription.getEQ_bizLocation() != null)
				map.put("EQ_bizLocation", subscription.getEQ_bizLocation());
			if (subscription.getWD_bizLocation() != null)
				map.put("WD_bizLocation", subscription.getWD_bizLocation());
			if (subscription.getEQ_transformationID() != null)
				map.put("EQ_transformationID", subscription.getEQ_transformationID());
			if (subscription.getMATCH_epc() != null)
				map.put("MATCH_epc", subscription.getMATCH_epc());
			if (subscription.getMATCH_parentID() != null)
				map.put("MATCH_parentID", subscription.getMATCH_parentID());
			if (subscription.getMATCH_inputEPC() != null)
				map.put("MATCH_inputEPC", subscription.getMATCH_inputEPC());
			if (subscription.getMATCH_outputEPC() != null)
				map.put("MATCH_outputEPC", subscription.getMATCH_outputEPC());
			if (subscription.getMATCH_anyEPC() != null)
				map.put("MATCH_anyEPC", subscription.getMATCH_anyEPC());
			if (subscription.getMATCH_epcClass() != null)
				map.put("MATCH_epcClass", subscription.getMATCH_epcClass());
			if (subscription.getMATCH_inputEPCClass() != null)
				map.put("MATCH_inputEPCClass", subscription.getMATCH_inputEPCClass());
			if (subscription.getMATCH_outputEPCClass() != null)
				map.put("MATCH_outputEPCClass", subscription.getMATCH_outputEPCClass());
			if (subscription.getMATCH_anyEPCClass() != null)
				map.put("MATCH_anyEPCClass", subscription.getMATCH_anyEPCClass());
			if (subscription.getEQ_quantity() != null)
				map.put("EQ_quantity", subscription.getEQ_quantity());
			if (subscription.getGT_quantity() != null)
				map.put("GT_quantity", subscription.getGT_quantity());
			if (subscription.getGE_quantity() != null)
				map.put("GE_quantity", subscription.getGE_quantity());
			if (subscription.getLT_quantity() != null)
				map.put("LT_quantity", subscription.getLT_quantity());
			if (subscription.getLE_quantity() != null)
				map.put("LE_quantity", subscription.getLE_quantity());
			if (subscription.getOrderBy() != null)
				map.put("orderBy", subscription.getOrderBy());
			if (subscription.getOrderDirection() != null)
				map.put("orderDirection", subscription.getOrderDirection());
			if (subscription.getEventCountLimit() != null)
				map.put("eventCountLimit", subscription.getEventCountLimit());
			if (subscription.getMaxEventCount() != null)
				map.put("maxEventCount", subscription.getMaxEventCount());
			if (subscription.getFormat() != null)
				map.put("format", subscription.getFormat());
			if (subscription.getParamMap() != null)
				map.put("paramMap", subscription.getParamMap());

			JobDetail job = newJob(MongoSubscriptionTask.class)
					.withIdentity(subscription.getSubscriptionID(), subscription.getQueryName()).setJobData(map)
					.storeDurably(false).build();

			Trigger trigger = newTrigger().withIdentity(subscription.getSubscriptionID(), subscription.getQueryName())
					.startNow().withSchedule(cronSchedule(subscription.getCronExpression())).build();

			// ClassPathXmlApplicationContext context = new
			// ClassPathXmlApplicationContext(
			// "classpath:QuartzConfig.xml");
			// Scheduler sched = (Scheduler) context
			// .getBean("schedulerFactoryBean");

			if (MongoSubscription.sched.isStarted() != true)
				MongoSubscription.sched.start();
			MongoSubscription.sched.scheduleJob(job, trigger);
			Configuration.logger.log(Level.INFO,
					"Subscription ID: " + subscription.getSubscriptionID() + " is added to quartz scheduler. ");
		} catch (SchedulerException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
		} catch (RuntimeException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
		}
	}

	private void addScheduleToQuartz(String queryName, String subscriptionID, String dest, String cronExpression,
			boolean ignoreReceivedEvent, boolean reportIfEmpty, String initialRecordTimeStr, String eventType,
			String GE_eventTime, String LT_eventTime, String GE_recordTime, String LT_recordTime, String EQ_action,
			String EQ_bizStep, String EQ_disposition, String EQ_readPoint, String WD_readPoint, String EQ_bizLocation,
			String WD_bizLocation, String EQ_transformationID, String MATCH_epc, String MATCH_parentID,
			String MATCH_inputEPC, String MATCH_outputEPC, String MATCH_anyEPC, String MATCH_epcClass,
			String MATCH_inputEPCClass, String MATCH_outputEPCClass, String MATCH_anyEPCClass, String EQ_quantity,
			String GT_quantity, String GE_quantity, String LT_quantity, String LE_quantity, String orderBy,
			String orderDirection, String eventCountLimit, String maxEventCount, String format,
			Map<String, String> paramMap) {
		try {
			JobDataMap map = new JobDataMap();
			map.put("queryName", queryName);
			map.put("subscriptionID", subscriptionID);
			map.put("dest", dest);
			map.put("ignoreReceivedEvent", ignoreReceivedEvent);
			map.put("cronExpression", cronExpression);
			map.put("reportIfEmpty", reportIfEmpty);
			map.put("initialRecordTime", initialRecordTimeStr);
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
			if (format != null)
				map.put("format", format);
			if (paramMap != null)
				map.put("paramMap", paramMap);
			JobDetail job = newJob(MongoSubscriptionTask.class).withIdentity(subscriptionID, queryName).setJobData(map)
					.storeDurably(false).build();

			Trigger trigger = newTrigger().withIdentity(subscriptionID, queryName).startNow()
					.withSchedule(cronSchedule(cronExpression)).build();

			// ClassPathXmlApplicationContext context = new
			// ClassPathXmlApplicationContext(
			// "classpath:QuartzConfig.xml");
			// Scheduler sched = (Scheduler) context
			// .getBean("schedulerFactoryBean");

			if (MongoSubscription.sched.isStarted() != true)
				MongoSubscription.sched.start();
			MongoSubscription.sched.scheduleJob(job, trigger);

			Configuration.logger.log(Level.INFO,
					"Subscription ID: " + subscriptionID + " is added to quartz scheduler. ");
		} catch (SchedulerException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
		}
	}

	@SuppressWarnings("resource")
	private boolean addScheduleToDB(String queryName, String subscriptionID, String dest, String cronExpression,
			boolean ignoreReceivedEvent, boolean reportIfEmpty, String initialRecordTime, String eventType,
			String GE_eventTime, String LT_eventTime, String GE_recordTime, String LT_recordTime, String EQ_action,
			String EQ_bizStep, String EQ_disposition, String EQ_readPoint, String WD_readPoint, String EQ_bizLocation,
			String WD_bizLocation, String EQ_transformationID, String MATCH_epc, String MATCH_parentID,
			String MATCH_inputEPC, String MATCH_outputEPC, String MATCH_anyEPC, String MATCH_epcClass,
			String MATCH_inputEPCClass, String MATCH_outputEPCClass, String MATCH_anyEPCClass, String EQ_quantity,
			String GT_quantity, String GE_quantity, String LT_quantity, String LE_quantity, String orderBy,
			String orderDirection, String eventCountLimit, String maxEventCount, String format,
			Map<String, String> paramMap) {

		SubscriptionType st = new SubscriptionType(queryName, subscriptionID, dest, cronExpression, ignoreReceivedEvent,
				reportIfEmpty, initialRecordTime, eventType, GE_eventTime, LT_eventTime, GE_recordTime, LT_recordTime,
				EQ_action, EQ_bizStep, EQ_disposition, EQ_readPoint, WD_readPoint, EQ_bizLocation, WD_bizLocation,
				EQ_transformationID, MATCH_epc, MATCH_parentID, MATCH_inputEPC, MATCH_outputEPC, MATCH_anyEPC,
				MATCH_epcClass, MATCH_inputEPCClass, MATCH_outputEPCClass, MATCH_anyEPCClass, EQ_quantity, GT_quantity,
				GE_quantity, LT_quantity, LE_quantity, orderBy, orderDirection, eventCountLimit, maxEventCount, format,
				paramMap);
		ApplicationContext ctx = new GenericXmlApplicationContext("classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");

		List<SubscriptionType> existenceTest = mongoOperation
				.find(new Query(Criteria.where("subscriptionID").is(subscriptionID)), SubscriptionType.class);
		if (existenceTest.size() != 0)
			return false;
		if (existenceTest.size() == 0)
			mongoOperation.save(st);

		Configuration.logger.log(Level.INFO, "Subscription ID: " + subscriptionID + " is added to DB. ");
		((AbstractApplicationContext) ctx).close();
		return true;
	}

	private void removeScheduleFromQuartz(SubscriptionType subscription) {
		try {
			MongoSubscription.sched
					.unscheduleJob(triggerKey(subscription.getSubscriptionID(), subscription.getQueryName()));
			MongoSubscription.sched.deleteJob(jobKey(subscription.getSubscriptionID(), subscription.getQueryName()));
			Configuration.logger.log(Level.INFO,
					"Subscription ID: " + subscription.getSubscriptionID() + " is removed from scheduler");
		} catch (SchedulerException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
		}
	}

	private void removeScheduleFromDB(MongoOperations mongoOperation, SubscriptionType subscription) {
		mongoOperation.remove(new Query(Criteria.where("subscriptionID").is(subscription.getSubscriptionID())),
				SubscriptionType.class);
		Configuration.logger.log(Level.INFO,
				"Subscription ID: " + subscription.getSubscriptionID() + " is removed from DB");
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

	private DBCursor makeSortedLimitedCursor(DBCursor cursor, String orderBy, String orderDirection,
			String eventCountLimit) {
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
						cursor = cursor.sort(new BasicDBObject("eventTime", 1));
					} else if (orderDirection.trim().equals("DESC")) {
						cursor = cursor.sort(new BasicDBObject("eventTime", -1));
					}
				}
			} else if (orderBy.trim().equals("recordTime")) {
				if (orderDirection != null) {
					if (orderDirection.trim().equals("ASC")) {
						cursor = cursor.sort(new BasicDBObject("recordTime", 1));
					} else if (orderDirection.trim().equals("DESC")) {
						cursor = cursor.sort(new BasicDBObject("recordTime", -1));
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
				cursor = cursor.limit(eventCount);
			} catch (NumberFormatException nfe) {
				Configuration.logger.log(Level.ERROR, nfe.toString());
			}
		}

		return cursor;
	}

	private List<DBObject> makeQueryObjects(String eventType, String GE_eventTime, String LT_eventTime,
			String GE_recordTime, String LT_recordTime, String EQ_action, String EQ_bizStep, String EQ_disposition,
			String EQ_readPoint, String WD_readPoint, String EQ_bizLocation, String WD_bizLocation,
			String EQ_transformationID, String MATCH_epc, String MATCH_parentID, String MATCH_inputEPC,
			String MATCH_outputEPC, String MATCH_anyEPC, String MATCH_epcClass, String MATCH_inputEPCClass,
			String MATCH_outputEPCClass, String MATCH_anyEPCClass, String EQ_quantity, String GT_quantity,
			String GE_quantity, String LT_quantity, String LE_quantity, String orderBy, String orderDirection,
			String eventCountLimit, String maxEventCount, Map<String, String> paramMap) {
		List<DBObject> queryList = new ArrayList<DBObject>();
		/**
		 * GE_eventTime: If specified, only events with eventTime greater than
		 * or equal to the specified value will be included in the result. If
		 * omitted, events are included regardless of their eventTime (unless
		 * constrained by the LT_eventTime parameter). Example:
		 * 2014-08-11T19:57:59.717+09:00 SimpleDateFormat sdf = new
		 * SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		 * eventTime.setTime(sdf.parse(timeString)); e.g.
		 * 1988-07-04T12:08:56.235-07:00
		 * 
		 * Verified
		 */
		if (GE_eventTime != null) {
			long geEventTimeMillis = getTimeMillis(GE_eventTime);
			DBObject query = new BasicDBObject();
			query.put("eventTime", new BasicDBObject("$gte", geEventTimeMillis));
			queryList.add(query);
		}
		/**
		 * LT_eventTime: If specified, only events with eventTime less than the
		 * specified value will be included in the result. If omitted, events
		 * are included regardless of their eventTime (unless constrained by the
		 * GE_eventTime parameter).
		 * 
		 * Verified
		 */
		if (LT_eventTime != null) {
			long ltEventTimeMillis = getTimeMillis(LT_eventTime);
			DBObject query = new BasicDBObject();
			query.put("eventTime", new BasicDBObject("$lt", ltEventTimeMillis));
			queryList.add(query);
		}
		/**
		 * GE_recordTime: If provided, only events with recordTime greater than
		 * or equal to the specified value will be returned. The automatic
		 * limitation based on event record time (Section 8.2.5.2) may
		 * implicitly provide a constraint similar to this parameter. If
		 * omitted, events are included regardless of their recordTime, other
		 * than automatic limitation based on event record time (Section
		 * 8.2.5.2).
		 * 
		 * Verified
		 */
		if (GE_recordTime != null) {
			long geRecordTimeMillis = getTimeMillis(GE_recordTime);
			DBObject query = new BasicDBObject();
			query.put("recordTime", new BasicDBObject("$gte", geRecordTimeMillis));
			queryList.add(query);
		}
		/**
		 * LE_recordTime: If provided, only events with recordTime less than the
		 * specified value will be returned. If omitted, events are included
		 * regardless of their recordTime (unless constrained by the
		 * GE_recordTime parameter or the automatic limitation based on event
		 * record time).
		 * 
		 * Verified
		 */
		if (LT_recordTime != null) {
			long ltRecordTimeMillis = getTimeMillis(LT_recordTime);
			DBObject query = new BasicDBObject();
			query.put("recordTime", new BasicDBObject("$lt", ltRecordTimeMillis));
			queryList.add(query);
		}

		/**
		 * EQ_action: If specified, the result will only include events that (a)
		 * have an action field; and where (b) the value of the action field
		 * matches one of the specified values. The elements of the value of
		 * this parameter each must be one of the strings ADD, OBSERVE, or
		 * DELETE; if not, the implementation SHALL raise a
		 * QueryParameterException. If omitted, events are included regardless
		 * of their action field.
		 * 
		 * Verified
		 */
		if (EQ_action != null) {
			// Constrained already checked
			DBObject query = new BasicDBObject();
			query.put("action", EQ_action);
			queryList.add(query);
		}
		/**
		 * EQ_bizStep: If specified, the result will only include events that
		 * (a) have a non-null bizStep field; and where (b) the value of the
		 * bizStep field matches one of the specified values. If this parameter
		 * is omitted, events are returned regardless of the value of the
		 * bizStep field or whether the bizStep field exists at all.
		 * 
		 * Verified
		 */
		if (EQ_bizStep != null) {
			DBObject query = getINQueryObject("bizStep", EQ_bizStep);
			if (query != null)
				queryList.add(query);
		}
		/**
		 * EQ_disposition: Like the EQ_bizStep parameter, but for the
		 * disposition field.
		 * 
		 * Verified
		 */
		if (EQ_disposition != null) {
			DBObject query = getINQueryObject("disposition", EQ_disposition);
			if (query != null)
				queryList.add(query);
		}
		/**
		 * EQ_readPoint: If specified, the result will only include events that
		 * (a) have a non-null readPoint field; and where (b) the value of the
		 * readPoint field matches one of the specified values. If this
		 * parameter and WD_readPoint are both omitted, events are returned
		 * regardless of the value of the readPoint field or whether the
		 * readPoint field exists at all.
		 */
		Set<String> readPointSet = new HashSet<String>();
		if (EQ_readPoint != null) {
			String[] eqArr = EQ_readPoint.split(",");
			for (int i = 0; i < eqArr.length; i++) {
				String eqString = eqArr[i].trim();
				readPointSet.add(eqString);
			}
		}

		/**
		 * WD_readPoint: If specified, the result will only include events that
		 * (a) have a non-null readPoint field; and where (b) the value of the
		 * readPoint field matches one of the specified values, or is a direct
		 * or indirect descendant of one of the specified values. The meaning of
		 * “direct or indirect descendant” is specified by master data, as
		 * described in Section 6.5. (WD is an abbreviation for “with
		 * descendants.”) If this parameter and EQ_readPoint are both omitted,
		 * events are returned regardless of the value of the readPoint field or
		 * whether the readPoint field exists at all.
		 */

		if (WD_readPoint != null) {

			String[] eqArr = WD_readPoint.split(",");
			for (int i = 0; i < eqArr.length; i++) {
				eqArr[i] = eqArr[i].trim();
			}
			for (int i = 0; i < eqArr.length; i++) {
				// Invoke vocabulary query with EQ_name and includeChildren
				readPointSet = getWDList(readPointSet, eqArr[i]);
			}
		}

		if (!readPointSet.isEmpty()) {
			DBObject query = getINQueryObject("readPoint.id", readPointSet);
			if (query != null)
				queryList.add(query);
		}

		/**
		 * EQ_bizLocation: Like the EQ_readPoint parameter, but for the
		 * bizLocation field.
		 */
		Set<String> bizLocationSet = new HashSet<String>();
		if (EQ_bizLocation != null) {
			String[] eqArr = EQ_bizLocation.split(",");
			for (int i = 0; i < eqArr.length; i++) {
				String eqString = eqArr[i].trim();
				bizLocationSet.add(eqString);
			}
		}
		/**
		 * WD_bizLocation: Like the WD_readPoint parameter, but for the
		 * bizLocation field.
		 */
		if (WD_bizLocation != null) {
			String[] eqArr = WD_bizLocation.split(",");
			for (int i = 0; i < eqArr.length; i++) {
				eqArr[i] = eqArr[i].trim();
			}
			for (int i = 0; i < eqArr.length; i++) {
				// Invoke vocabulary query with EQ_name and includeChildren
				bizLocationSet = getWDList(bizLocationSet, eqArr[i]);
			}
		}

		if (!bizLocationSet.isEmpty()) {
			DBObject query = getINQueryObject("bizLocation.id", bizLocationSet);
			if (query != null)
				queryList.add(query);
		}

		/**
		 * EQ_transformationID: If this parameter is specified, the result will
		 * only include events that (a) have a transformationID field (that is,
		 * TransformationEvents or extension event type that extend
		 * TransformationEvent); and where (b) the transformationID field is
		 * equal to one of the values specified in this parameter.
		 */
		if (EQ_transformationID != null) {
			DBObject query = getINQueryObject("transformationID", EQ_transformationID);
			if (query != null)
				queryList.add(query);
		}

		/**
		 * MATCH_epc: If this parameter is specified, the result will only
		 * include events that (a) have an epcList or a childEPCs field (that
		 * is, ObjectEvent, AggregationEvent, TransactionEvent or extension
		 * event types that extend one of those three); and where (b) one of the
		 * EPCs listed in the epcList or childEPCs field (depending on event
		 * type) matches one of the EPC patterns or URIs specified in this
		 * parameter, where the meaning of “matches” is as specified in Section
		 * 8.2.7.1.1. If this parameter is omitted, events are included
		 * regardless of their epcList or childEPCs field or whether the epcList
		 * or childEPCs field exists.
		 * 
		 * Somewhat verified
		 */
		if (MATCH_epc != null) {
			DBObject query = getINQueryObject(new String[] { "epcList.epc", "childEPCs.epc" }, MATCH_epc);
			if (query != null)
				queryList.add(query);
		}

		/**
		 * MATCH_parentID: Like MATCH_epc, but matches the parentID field of
		 * AggregationEvent, the parentID field of TransactionEvent, and
		 * extension event types that extend either AggregationEvent or
		 * TransactionEvent. The meaning of “matches” is as specified in Section
		 * 8.2.7.1.1.
		 */
		if (MATCH_parentID != null) {
			DBObject query = getINQueryObject("parentID", MATCH_parentID);
			if (query != null)
				queryList.add(query);
		}

		/**
		 * MATCH_inputEPC: If this parameter is specified, the result will only
		 * include events that (a) have an inputEPCList (that is,
		 * TransformationEvent or an extension event type that extends
		 * TransformationEvent); and where (b) one of the EPCs listed in the
		 * inputEPCList field matches one of the EPC patterns or URIs specified
		 * in this parameter. The meaning of “matches” is as specified in
		 * Section 8.2.7.1.1. If this parameter is omitted, events are included
		 * regardless of their inputEPCList field or whether the inputEPCList
		 * field exists.
		 */
		if (MATCH_inputEPC != null) {
			DBObject query = getINQueryObject("inputEPCList.epc", MATCH_inputEPC);
			if (query != null)
				queryList.add(query);
		}

		/**
		 * MATCH_outputEPC: If this parameter is specified, the result will only
		 * include events that (a) have an inputEPCList (that is,
		 * TransformationEvent or an extension event type that extends
		 * TransformationEvent); and where (b) one of the EPCs listed in the
		 * inputEPCList field matches one of the EPC patterns or URIs specified
		 * in this parameter. The meaning of “matches” is as specified in
		 * Section 8.2.7.1.1. If this parameter is omitted, events are included
		 * regardless of their inputEPCList field or whether the inputEPCList
		 * field exists.
		 */
		if (MATCH_outputEPC != null) {
			DBObject query = getINQueryObject("outputEPCList.epc", MATCH_outputEPC);
			if (query != null)
				queryList.add(query);
		}

		/**
		 * MATCH_anyEPC: If this parameter is specified, the result will only
		 * include events that (a) have an epcList field, a childEPCs field, a
		 * parentID field, an inputEPCList field, or an outputEPCList field
		 * (that is, ObjectEvent, AggregationEvent, TransactionEvent,
		 * TransformationEvent, or extension event types that extend one of
		 * those four); and where (b) the parentID field or one of the EPCs
		 * listed in the epcList, childEPCs, inputEPCList, or outputEPCList
		 * field (depending on event type) matches one of the EPC patterns or
		 * URIs specified in this parameter. The meaning of “matches” is as
		 * specified in Section 8.2.7.1.1.
		 */

		if (MATCH_anyEPC != null) {
			DBObject query = getINQueryObject(
					new String[] { "epcList.epc", "childEPCs.epc", "inputEPCList.epc", "outputEPCList.epc" },
					MATCH_anyEPC);
			if (query != null)
				queryList.add(query);
		}

		/**
		 * MATCH_epcClass: If this parameter is specified, the result will only
		 * include events that (a) have a quantityList or a childQuantityList
		 * field (that is, ObjectEvent, AggregationEvent, TransactionEvent or
		 * extension event types that extend one of those three); and where (b)
		 * one of the EPC classes listed in the quantityList or
		 * childQuantityList field (depending on event type) matches one of the
		 * EPC patterns or URIs specified in this parameter. The result will
		 * also include QuantityEvents whose epcClass field matches one of the
		 * EPC patterns or URIs specified in this parameter. The meaning of
		 * “matches” is as specified in Section 8.2.7.1.1.
		 */
		if (MATCH_epcClass != null) {
			DBObject query = getINQueryObject(
					new String[] { "extension.quantityList.epcClass", "extension.childQuantityList.epcClass" },
					MATCH_epcClass);
			if (query != null)
				queryList.add(query);
		}

		/**
		 * MATCH_inputEPCClass: If this parameter is specified, the result will
		 * only include events that (a) have an inputQuantityList field (that
		 * is, TransformationEvent or extension event types that extend it); and
		 * where (b) one of the EPC classes listed in the inputQuantityList
		 * field (depending on event type) matches one of the EPC patterns or
		 * URIs specified in this parameter. The meaning of “matches” is as
		 * specified in Section 8.2.7.1.1.
		 */
		if (MATCH_inputEPCClass != null) {
			DBObject query = getINQueryObject("inputQuantityList.epcClass", MATCH_inputEPCClass);
			if (query != null)
				queryList.add(query);
		}

		/**
		 * MATCH_outputEPCClass: If this parameter is specified, the result will
		 * only include events that (a) have an outputQuantityList field (that
		 * is, TransformationEvent or extension event types that extend it); and
		 * where (b) one of the EPC classes listed in the outputQuantityList
		 * field (depending on event type) matches one of the EPC patterns or
		 * URIs specified in this parameter. The meaning of “matches” is as
		 * specified in Section 8.2.7.1.1.
		 */

		if (MATCH_outputEPCClass != null) {
			DBObject query = getINQueryObject("outputQuantityList.epcClass", MATCH_outputEPCClass);
			if (query != null)
				queryList.add(query);
		}

		/**
		 * MATCH_anyEPCClass: If this parameter is specified, the result will
		 * only include events that (a) have a quantityList, childQuantityList,
		 * inputQuantityList, or outputQuantityList field (that is, ObjectEvent,
		 * AggregationEvent, TransactionEvent, TransformationEvent, or extension
		 * event types that extend one of those four); and where (b) one of the
		 * EPC classes listed in any of those fields matches one of the EPC
		 * patterns or URIs specified in this parameter. The result will also
		 * include QuantityEvents whose epcClass field matches one of the EPC
		 * patterns or URIs specified in this parameter. The meaning of
		 * “matches” is as specified in Section 8.2.7.1.1.
		 */
		if (MATCH_anyEPCClass != null) {
			DBObject query = getINQueryObject(
					new String[] { "extension.quantityList.epcClass", "extension.childQuantityList.epcClass",
							"inputQuantityList.epcClass", "outputQuantityList.epcClass" },
					MATCH_anyEPCClass);
			if (query != null)
				queryList.add(query);
		}

		/**
		 * (DEPCRECATED in EPCIS 1.1) EQ_quantity; GT_quantity; GE_quantity;
		 * LT_quantity; LE_quantity
		 **/

		/**
		 * EQ_fieldname: This is not a single parameter, but a family of
		 * parameters. If a parameter of this form is specified, the result will
		 * only include events that (a) have a field named fieldname whose type
		 * is either String or a vocabulary type; and where (b) the value of
		 * that field matches one of the values specified in this parameter.
		 * Fieldname is the fully qualified name of an extension field. The name
		 * of an extension field is an XML qname; that is, a pair consisting of
		 * an XML namespace URI and a name. The name of the corresponding query
		 * parameter is constructed by concatenating the following: the string
		 * EQ_, the namespace URI for the extension field, a pound sign (#), and
		 * the name of the extension field.
		 */

		Iterator<String> paramIter = paramMap.keySet().iterator();
		while (paramIter.hasNext()) {
			String paramName = paramIter.next();
			String paramValues = paramMap.get(paramName);

			/**
			 * EQ_bizTransaction_type: This is not a single parameter, but a
			 * family of parameters. If a parameter of this form is specified,
			 * the result will only include events that (a) include a
			 * bizTransactionList; (b) where the business transaction list
			 * includes an entry whose type subfield is equal to type extracted
			 * from the name of this parameter; and (c) where the bizTransaction
			 * subfield of that entry is equal to one of the values specified in
			 * this parameter.
			 */
			if (paramName.contains("EQ_bizTransaction_")) {
				String type = paramName.substring(18, paramName.length());
				DBObject query = getINFamilyQueryObject(type, "bizTransactionList", paramValues);
				if (query != null)
					queryList.add(query);
			}

			/**
			 * EQ_source_type: This is not a single parameter, but a family of
			 * parameters. If a parameter of this form is specified, the result
			 * will only include events that (a) include a sourceList; (b) where
			 * the source list includes an entry whose type subfield is equal to
			 * type extracted from the name of this parameter; and (c) where the
			 * source subfield of that entry is equal to one of the values
			 * specified in this parameter.
			 */

			if (paramName.contains("EQ_source_")) {
				String type = paramName.substring(10, paramName.length());
				if (eventType.equals("AggregationEvent") || eventType.equals("ObjectEvent")
						|| eventType.equals("TransactionEvent")) {
					DBObject query = getINFamilyQueryObject(type, "extension.sourceList", paramValues);
					if (query != null)
						queryList.add(query);
				}
				if (eventType.equals("TransformationEvent")) {
					DBObject query = getINFamilyQueryObject(type, "sourceList", paramValues);
					if (query != null)
						queryList.add(query);
				}
			}

			/**
			 * EQ_destination_type: This is not a single parameter, but a family
			 * of parameters. If a parameter of this form is specified, the
			 * result will only include events that (a) include a
			 * destinationList; (b) where the destination list includes an entry
			 * whose type subfield is equal to type extracted from the name of
			 * this parameter; and (c) where the destination subfield of that
			 * entry is equal to one of the values specified in this parameter.
			 */
			if (paramName.contains("EQ_destination_")) {
				String type = paramName.substring(15, paramName.length());
				if (eventType.equals("AggregationEvent") || eventType.equals("ObjectEvent")
						|| eventType.equals("TransactionEvent")) {
					DBObject query = getINFamilyQueryObject(type, "extension.destinationList", paramValues);
					if (query != null)
						queryList.add(query);
				}
				if (eventType.equals("TransformationEvent")) {
					DBObject query = getINFamilyQueryObject(type, "destinationList", paramValues);
					if (query != null)
						queryList.add(query);
				}
			}
			boolean isExtraParam = isExtraParameter(paramName);

			if (isExtraParam == true) {

				/**
				 * EQ_fieldname: This is not a single parameter, but a family of
				 * parameters. If a parameter of this form is specified, the
				 * result will only include events that (a) have a field named
				 * fieldname whose type is either String or a vocabulary type;
				 * and where (b) the value of that field matches one of the
				 * values specified in this parameter. Fieldname is the fully
				 * qualified name of an extension field. The name of an
				 * extension field is an XML qname; that is, a pair consisting
				 * of an XML namespace URI and a name. The name of the
				 * corresponding query parameter is constructed by concatenating
				 * the following: the string EQ_, the namespace URI for the
				 * extension field, a pound sign (#), and the name of the
				 * extension field.
				 */
				if (paramName.startsWith("EQ_")) {
					String type = paramName.substring(3, paramName.length());
					/*
					 * if (eventType.equals("AggregationEvent") ||
					 * eventType.equals("ObjectEvent") ||
					 * eventType.equals("TransactionEvent")) { DBObject query =
					 * getINExtensionQueryObject(type, new String[] {
					 * "extension.extension.any." + type,
					 * "extension.extension.otherAttributes." + type },
					 * paramValues); if (query != null) queryList.add(query); }
					 * if (eventType.equals("QuantityEvent") ||
					 * eventType.equals("TransformationEvent")) { DBObject query
					 * = getINExtensionQueryObject( type, new String[] {
					 * "extension.any." + type, "extension.otherAttributes." +
					 * type }, paramValues); if (query != null)
					 * queryList.add(query); }
					 */
					DBObject query = getINExtensionQueryObject(type,
							new String[] { "any." + type, "otherAttributes." + type }, paramValues);
					if (query != null)
						queryList.add(query);
				}

				/**
				 * GT/GE/LT/LE_fieldname: Like EQ_fieldname as described above,
				 * but may be applied to a field of type Int, Float, or Time.
				 * The result will include events that (a) have a field named
				 * fieldname; and where (b) the type of the field matches the
				 * type of this parameter (Int, Float, or Time); and where (c)
				 * the value of the field is greater than the specified value.
				 * Fieldname is constructed as for EQ_fieldname.
				 */

				if (paramName.startsWith("GT_") || paramName.startsWith("GE_") || paramName.startsWith("LT_")
						|| paramName.startsWith("LE_")) {
					String type = paramName.substring(3, paramName.length());

					/*
					 * if (eventType.equals("AggregationEvent") ||
					 * eventType.equals("ObjectEvent") ||
					 * eventType.equals("TransactionEvent")) { if
					 * (paramName.startsWith("GT_")) { DBObject query =
					 * getCompExtensionQueryObject( type, new String[] {
					 * "extension.extension.any." + type,
					 * "extension.extension.otherAttributes." + type },
					 * paramValues, "GT"); if (query != null)
					 * queryList.add(query); } if (paramName.startsWith("GE_"))
					 * { DBObject query = getCompExtensionQueryObject( type, new
					 * String[] { "extension.extension.any." + type,
					 * "extension.extension.otherAttributes." + type },
					 * paramValues, "GE"); if (query != null)
					 * queryList.add(query); } if (paramName.startsWith("LT_"))
					 * { DBObject query = getCompExtensionQueryObject( type, new
					 * String[] { "extension.extension.any." + type,
					 * "extension.extension.otherAttributes." + type },
					 * paramValues, "LT"); if (query != null)
					 * queryList.add(query); } if (paramName.startsWith("LE_"))
					 * { DBObject query = getCompExtensionQueryObject( type, new
					 * String[] { "extension.extension.any." + type,
					 * "extension.extension.otherAttributes." + type },
					 * paramValues, "LE"); if (query != null)
					 * queryList.add(query); } } if
					 * (eventType.equals("QuantityEvent") ||
					 * eventType.equals("TransformationEvent")) { if
					 * (paramName.startsWith("GT_")) {
					 * 
					 * DBObject query = getCompExtensionQueryObject( type, new
					 * String[] { "extension.any." + type,
					 * "extension.otherAttributes." + type }, paramValues,
					 * "GT"); if (query != null) queryList.add(query); } if
					 * (paramName.startsWith("GE_")) {
					 * 
					 * DBObject query = getCompExtensionQueryObject( type, new
					 * String[] { "extension.any." + type,
					 * "extension.otherAttributes." + type }, paramValues,
					 * "GE"); if (query != null) queryList.add(query); } if
					 * (paramName.startsWith("LT_")) { DBObject query =
					 * getCompExtensionQueryObject( type, new String[] {
					 * "extension.any." + type, "extension.otherAttributes." +
					 * type }, paramValues, "LT"); if (query != null)
					 * queryList.add(query); } if (paramName.startsWith("LE_"))
					 * {
					 * 
					 * DBObject query = getCompExtensionQueryObject( type, new
					 * String[] { "extension.any." + type,
					 * "extension.otherAttributes." + type }, paramValues,
					 * "LE"); if (query != null) queryList.add(query); } }
					 */
					if (paramName.startsWith("GT_")) {
						DBObject query = getCompExtensionQueryObject(type,
								new String[] { "any." + type, "otherAttributes." + type }, paramValues, "GT");
						if (query != null)
							queryList.add(query);
					}
					if (paramName.startsWith("GE_")) {
						DBObject query = getCompExtensionQueryObject(type,
								new String[] { "any." + type, "otherAttributes." + type }, paramValues, "GE");
						if (query != null)
							queryList.add(query);
					}
					if (paramName.startsWith("LT_")) {
						DBObject query = getCompExtensionQueryObject(type,
								new String[] { "any." + type, "otherAttributes." + type }, paramValues, "LT");
						if (query != null)
							queryList.add(query);
					}
					if (paramName.startsWith("LE_")) {
						DBObject query = getCompExtensionQueryObject(type,
								new String[] { "any." + type, "otherAttributes." + type }, paramValues, "LE");
						if (query != null)
							queryList.add(query);
					}
				}
			}
		}
		return queryList;
	}

	private List<DBObject> makeMasterQueryObjects(String vocabularyName, boolean includeAttributes,
			boolean includeChildren, String attributeNames, String eQ_name, String wD_name, String hASATTR,
			String maxElementCount, Map<String, String> paramMap) {

		List<DBObject> queryList = new ArrayList<DBObject>();

		/**
		 * vocabularyName : If specified, only vocabulary elements drawn from
		 * one of the specified vocabularies will be included in the results.
		 * Each element of the specified list is the formal URI name for a
		 * vocabulary; e.g., one of the URIs specified in the table at the end
		 * of Section 7.2. If omitted, all vocabularies are considered.
		 */

		if (vocabularyName != null) {
			DBObject query = getINQueryObject("type", vocabularyName);
			if (query != null)
				queryList.add(query);
		}

		/**
		 * EQ_name : If specified, the result will only include vocabulary
		 * elements whose names are equal to one of the specified values. If
		 * this parameter and WD_name are both omitted, vocabulary elements are
		 * included regardless of their names.
		 */
		Set<String> idSet = new HashSet<String>();
		if (eQ_name != null) {
			String[] eqArr = eQ_name.split(",");
			for (int i = 0; i < eqArr.length; i++) {
				String eqString = eqArr[i].trim();
				idSet.add(eqString);
			}
		}

		/**
		 * WD_name : If specified, the result will only include vocabulary
		 * elements that either match one of the specified names, or are direct
		 * or indirect descendants of a vocabulary element that matches one of
		 * the specified names. The meaning of “direct or indirect descendant”
		 * is described in Section 6.5. (WD is an abbreviation for “with
		 * descendants.”) If this parameter and EQ_name are both omitted,
		 * vocabulary elements are included regardless of their names.
		 */

		if (wD_name != null) {
			String[] eqArr = wD_name.split(",");
			for (int i = 0; i < eqArr.length; i++) {
				eqArr[i] = eqArr[i].trim();
			}
			for (int i = 0; i < eqArr.length; i++) {
				// Invoke vocabulary query with EQ_name and includeChildren
				idSet = getWDList(idSet, eqArr[i]);
			}
		}

		if (!idSet.isEmpty()) {
			DBObject query = getINQueryObject("id", idSet);
			if (query != null)
				queryList.add(query);
		}

		/**
		 * HASATTR : If specified, the result will only include vocabulary
		 * elements that have a non-null attribute whose name matches one of the
		 * values specified in this parameter.
		 */

		if (hASATTR != null) {
			String[] attrArr = hASATTR.split(",");
			for (int i = 0; i < attrArr.length; i++) {
				String attrString = attrArr[i].trim();
				DBObject query = getExistsQueryObject("attributes", attrString);
				if (query != null)
					queryList.add(query);
			}
		}

		/**
		 * EQATTR_attrnam : This is not a single parameter, but a family of
		 * parameters. If a parameter of this form is specified, the result will
		 * only include vocabulary elements that have a non-null attribute named
		 * attrname, and where the value of that attribute matches one of the
		 * values specified in this parameter.
		 */
		if (paramMap != null) {
			Iterator<String> paramIter = paramMap.keySet().iterator();
			while (paramIter.hasNext()) {
				String paramName = paramIter.next();
				String paramValues = paramMap.get(paramName);

				if (paramName.contains("EQATTR_")) {
					String type = paramName.substring(7, paramName.length());
					DBObject query = getVocFamilyQueryObject(type, "attributes", paramValues);
					if (query != null)
						queryList.add(query);
				}
			}
		}
		return queryList;
	}

	private long getTimeMillis(String standardDateString) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
			GregorianCalendar eventTimeCalendar = new GregorianCalendar();
			eventTimeCalendar.setTime(sdf.parse(standardDateString));
			return eventTimeCalendar.getTimeInMillis();
		} catch (ParseException e) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
				GregorianCalendar eventTimeCalendar = new GregorianCalendar();
				eventTimeCalendar.setTime(sdf.parse(standardDateString));
				return eventTimeCalendar.getTimeInMillis();
			} catch (ParseException e1) {
				Configuration.logger.log(Level.ERROR, e1.toString());
			}
		}
		// Never Happened
		return 0;
	}
}
