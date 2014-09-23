package org.oliot.epcis.service.query.restlike;

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
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.apache.log4j.Level;
import org.json.JSONArray;
import org.oliot.epcis.configuration.ConfigurationServlet;
import org.oliot.model.epcis.AggregationEventType;
import org.oliot.model.epcis.EPCISQueryBodyType;
import org.oliot.model.epcis.EPCISQueryDocumentType;
import org.oliot.model.epcis.EventListType;
import org.oliot.model.epcis.InvalidURIException;
import org.oliot.model.epcis.ObjectEventType;
import org.oliot.model.epcis.QuantityEventType;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.data.domain.Sort;
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

import static org.quartz.TriggerKey.*;
import static org.quartz.JobKey.*;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

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
public class QueryService implements CoreQueryService, ServletContextAware {

	// Due to Created JAXB is not Throwable and need to send Exception to the
	// Result
	@SuppressWarnings("unused")
	private boolean isQueryParameterException;
	// Not EPC Spec
	@SuppressWarnings("unused")
	private boolean isNumberFormatException;
	@Autowired
	ServletContext servletContext;

	/**
	 * 
	 */
	@Autowired
	private HttpServletRequest request;

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;

	}

	public String subscribe(SubscriptionType subscription) {
		String queryName = subscription.getQueryName();
		String subscriptionID = subscription.getSubscriptionID();
		String dest = subscription.getDest();
		String cronExpression = subscription.getCronExpression();
		boolean reportIfEmpty = subscription.isReportIfEmpty();
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
		String EQ_bizTransaction_type = subscription
				.getEQ_bizTransaction_type();
		String EQ_source_type = subscription.getEQ_source_type();
		String EQ_destination_type = subscription.getEQ_destination_type();
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
		String EQ_fieldname = subscription.getEQ_fieldname();
		String GT_fieldname = subscription.getGT_fieldname();
		String GE_fieldname = subscription.getGE_fieldname();
		String LT_fieldname = subscription.getLT_fieldname();
		String LE_fieldname = subscription.getLE_fieldname();
		String EQ_ILMD_fieldname = subscription.getEQ_ILMD_fieldname();
		String GT_ILMD_fieldname = subscription.getGT_ILMD_fieldname();
		String GE_ILMD_fieldname = subscription.getGE_ILMD_fieldname();
		String LT_ILMD_fieldname = subscription.getLT_ILMD_fieldname();
		String LE_ILMD_fieldname = subscription.getLE_ILMD_fieldname();
		String EXIST_fieldname = subscription.getEXIST_fieldname();
		String EXIST_ILMD_fieldname = subscription.getEXIST_ILMD_fieldname();
		String HASATTR_fieldname = subscription.getHASATTR_fieldname();
		String EQATTR_fieldname_attrname = subscription
				.getEQATTR_fieldname_attrname();
		String orderBy = subscription.getOrderBy();
		String orderDirection = subscription.getOrderDirection();
		String eventCountLimit = subscription.getEventCountLimit();
		String maxEventCount = subscription.getMaxEventCount();

		String result = subscribe(queryName, subscriptionID, dest,
				cronExpression, reportIfEmpty, eventType, GE_eventTime,
				LT_eventTime, GE_recordTime, LT_recordTime, EQ_action,
				EQ_bizStep, EQ_disposition, EQ_readPoint, WD_readPoint,
				EQ_bizLocation, WD_bizLocation, EQ_bizTransaction_type,
				EQ_source_type, EQ_destination_type, EQ_transformationID,
				MATCH_epc, MATCH_parentID, MATCH_inputEPC, MATCH_outputEPC,
				MATCH_anyEPC, MATCH_epcClass, MATCH_inputEPCClass,
				MATCH_outputEPCClass, MATCH_anyEPCClass, EQ_quantity,
				GT_quantity, GE_quantity, LT_quantity, LE_quantity,
				EQ_fieldname, GT_fieldname, GE_fieldname, LT_fieldname,
				LE_fieldname, EQ_ILMD_fieldname, GT_ILMD_fieldname,
				GE_ILMD_fieldname, LT_ILMD_fieldname, LE_ILMD_fieldname,
				EXIST_fieldname, EXIST_ILMD_fieldname, HASATTR_fieldname,
				EQATTR_fieldname_attrname, orderBy, orderDirection,
				eventCountLimit, maxEventCount);

		return result;
	}

	public String subscribeEventQuery(String queryName, String subscriptionID,
			String dest, String cronExpression, String eventType,
			String GE_eventTime, String LT_eventTime, String GE_recordTime,
			String LT_recordTime, String EQ_action, String EQ_bizStep,
			String EQ_disposition, String EQ_readPoint, String WD_readPoint,
			String EQ_bizLocation, String WD_bizLocation,
			String EQ_bizTransaction_type, String EQ_source_type,
			String EQ_destination_type, String EQ_transformationID,
			String MATCH_epc, String MATCH_parentID, String MATCH_inputEPC,
			String MATCH_outputEPC, String MATCH_anyEPC, String MATCH_epcClass,
			String MATCH_inputEPCClass, String MATCH_outputEPCClass,
			String MATCH_anyEPCClass, String EQ_quantity, String GT_quantity,
			String GE_quantity, String LT_quantity, String LE_quantity,
			String EQ_fieldname, String GT_fieldname, String GE_fieldname,
			String LT_fieldname, String LE_fieldname, String EQ_ILMD_fieldname,
			String GT_ILMD_fieldname, String GE_ILMD_fieldname,
			String LT_ILMD_fieldname, String LE_ILMD_fieldname,
			String EXIST_fieldname, String EXIST_ILMD_fieldname,
			String HASATTR_fieldname, String EQATTR_fieldname_attrname,
			String orderBy, String orderDirection, String eventCountLimit,
			String maxEventCount) {

		// M27 - query params' constraint
		// M39 - query params' constraint
		String reason = checkConstraintSimpleEventQuery(queryName, eventType,
				GE_eventTime, LT_eventTime, GE_recordTime, LT_recordTime,
				EQ_action, EQ_bizStep, EQ_disposition, EQ_readPoint,
				WD_readPoint, EQ_bizLocation, WD_bizLocation,
				EQ_bizTransaction_type, EQ_source_type, EQ_destination_type,
				EQ_transformationID, MATCH_epc, MATCH_parentID, MATCH_inputEPC,
				MATCH_outputEPC, MATCH_anyEPC, MATCH_epcClass,
				MATCH_inputEPCClass, MATCH_outputEPCClass, MATCH_anyEPCClass,
				EQ_quantity, GT_quantity, GE_quantity, LT_quantity,
				LE_quantity, EQ_fieldname, GT_fieldname, GE_fieldname,
				LT_fieldname, LE_fieldname, EQ_ILMD_fieldname,
				GT_ILMD_fieldname, GE_ILMD_fieldname, LT_ILMD_fieldname,
				LE_ILMD_fieldname, EXIST_fieldname, EXIST_ILMD_fieldname,
				HASATTR_fieldname, EQATTR_fieldname_attrname, orderBy,
				orderDirection, eventCountLimit, maxEventCount);
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
				EQ_bizTransaction_type, EQ_source_type, EQ_destination_type,
				EQ_transformationID, MATCH_epc, MATCH_parentID, MATCH_inputEPC,
				MATCH_outputEPC, MATCH_anyEPC, MATCH_epcClass,
				MATCH_inputEPCClass, MATCH_outputEPCClass, MATCH_anyEPCClass,
				EQ_quantity, GT_quantity, GE_quantity, LT_quantity,
				LE_quantity, EQ_fieldname, GT_fieldname, GE_fieldname,
				LT_fieldname, LE_fieldname, EQ_ILMD_fieldname,
				GT_ILMD_fieldname, GE_ILMD_fieldname, LT_ILMD_fieldname,
				LE_ILMD_fieldname, EXIST_fieldname, EXIST_ILMD_fieldname,
				HASATTR_fieldname, EQATTR_fieldname_attrname, orderBy,
				orderDirection, eventCountLimit, maxEventCount);

		// Manage Subscription Persistently
		addScheduleToDB(queryName, subscriptionID, dest, cronExpression,
				eventType, GE_eventTime, LT_eventTime, GE_recordTime,
				LT_recordTime, EQ_action, EQ_bizStep, EQ_disposition,
				EQ_readPoint, WD_readPoint, EQ_bizLocation, WD_bizLocation,
				EQ_bizTransaction_type, EQ_source_type, EQ_destination_type,
				EQ_transformationID, MATCH_epc, MATCH_parentID, MATCH_inputEPC,
				MATCH_outputEPC, MATCH_anyEPC, MATCH_epcClass,
				MATCH_inputEPCClass, MATCH_outputEPCClass, MATCH_anyEPCClass,
				EQ_quantity, GT_quantity, GE_quantity, LT_quantity,
				LE_quantity, EQ_fieldname, GT_fieldname, GE_fieldname,
				LT_fieldname, LE_fieldname, EQ_ILMD_fieldname,
				GT_ILMD_fieldname, GE_ILMD_fieldname, LT_ILMD_fieldname,
				LE_ILMD_fieldname, EXIST_fieldname, EXIST_ILMD_fieldname,
				HASATTR_fieldname, EQATTR_fieldname_attrname, orderBy,
				orderDirection, eventCountLimit, maxEventCount);

		String retString = "SubscriptionID : " + subscriptionID
				+ " is successfully triggered. ";
		return retString;
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
			return makeErrorResult("SimpleMasterDataQuery is not available in subscription method", SubscribeNotPermittedException.class);
		}

		String retString = "";
		if (queryName.equals("SimpleEventQuery")) {
			retString = subscribeEventQuery(queryName, subscriptionID, dest,
					cronExpression, eventType, GE_eventTime, LT_eventTime,
					GE_recordTime, LT_recordTime, EQ_action, EQ_bizStep,
					EQ_disposition, EQ_readPoint, WD_readPoint, EQ_bizLocation,
					WD_bizLocation, EQ_bizTransaction_type, EQ_source_type,
					EQ_destination_type, EQ_transformationID, MATCH_epc,
					MATCH_parentID, MATCH_inputEPC, MATCH_outputEPC,
					MATCH_anyEPC, MATCH_epcClass, MATCH_inputEPCClass,
					MATCH_outputEPCClass, MATCH_anyEPCClass, EQ_quantity,
					GT_quantity, GE_quantity, LT_quantity, LE_quantity,
					EQ_fieldname, GT_fieldname, GE_fieldname, LT_fieldname,
					LE_fieldname, EQ_ILMD_fieldname, GT_ILMD_fieldname,
					GE_ILMD_fieldname, LT_ILMD_fieldname, LE_ILMD_fieldname,
					EXIST_fieldname, EXIST_ILMD_fieldname, HASATTR_fieldname,
					EQATTR_fieldname_attrname, orderBy, orderDirection,
					eventCountLimit, maxEventCount);
		}

		return retString;
	}

	/**
	 * @author Jack Jaewook Byun
	 * 
	 *         Reason of deprecation : EPCIS v1.1 spec's QuerySchedule is not
	 *         described in fine-grained manner. This description seems refering
	 *         to 'Unix-cron'-like expression, but not describing seemlessly.
	 * 
	 */
	@Deprecated
	@Override
	public void subscribe(String queryName, QueryParams params, URI dest,
			SubscriptionControls controls, String subscriptionID) {

	}

	/**
	 * Removes a previously registered subscription having the specified
	 * subscriptionID.
	 */
	@RequestMapping(value = "/Unsubscribe/{subscriptionID}", method = RequestMethod.GET)
	@Override
	public void unsubscribe(@PathVariable String subscriptionID) {
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

	/**
	 * Returns a list of all subscriptionIDs currently subscribed to the
	 * specified named query.
	 */
	@RequestMapping(value = "/SubscriptionIDs/{queryName}", method = RequestMethod.GET)
	@ResponseBody
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

	/**
	 * Alternatively use public String getSubscriptionIDsREST(String queryName)
	 */
	@Deprecated
	@Override
	public List<String> getSubscriptionIDs(String queryName) {
		ConfigurationServlet.logger
				.log(Level.WARN,
						"Alternatively use public String getSubscriptionIDsREST(String queryName)");
		return null;
	}

	public String checkConstraintSimpleEventQuery(String queryName,
			String eventType, String GE_eventTime, String LT_eventTime,
			String GE_recordTime, String LT_recordTime, String EQ_action,
			String EQ_bizStep, String EQ_disposition, String EQ_readPoint,
			String WD_readPoint, String EQ_bizLocation, String WD_bizLocation,
			String EQ_bizTransaction_type, String EQ_source_type,
			String EQ_destination_type, String EQ_transformationID,
			String MATCH_epc, String MATCH_parentID, String MATCH_inputEPC,
			String MATCH_outputEPC, String MATCH_anyEPC, String MATCH_epcClass,
			String MATCH_inputEPCClass, String MATCH_outputEPCClass,
			String MATCH_anyEPCClass, String EQ_quantity, String GT_quantity,
			String GE_quantity, String LT_quantity, String LE_quantity,
			String EQ_fieldname, String GT_fieldname, String GE_fieldname,
			String LT_fieldname, String LE_fieldname, String EQ_ILMD_fieldname,
			String GT_ILMD_fieldname, String GE_ILMD_fieldname,
			String LT_ILMD_fieldname, String LE_ILMD_fieldname,
			String EXIST_fieldname, String EXIST_ILMD_fieldname,
			String HASATTR_fieldname, String EQATTR_fieldname_attrname,
			String orderBy, String orderDirection, String eventCountLimit,
			String maxEventCount) {

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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String pollEventQuery(String queryName, String eventType,
			String GE_eventTime, String LT_eventTime, String GE_recordTime,
			String LT_recordTime, String EQ_action, String EQ_bizStep,
			String EQ_disposition, String EQ_readPoint, String WD_readPoint,
			String EQ_bizLocation, String WD_bizLocation,
			String EQ_bizTransaction_type, String EQ_source_type,
			String EQ_destination_type, String EQ_transformationID,
			String MATCH_epc, String MATCH_parentID, String MATCH_inputEPC,
			String MATCH_outputEPC, String MATCH_anyEPC, String MATCH_epcClass,
			String MATCH_inputEPCClass, String MATCH_outputEPCClass,
			String MATCH_anyEPCClass, String EQ_quantity, String GT_quantity,
			String GE_quantity, String LT_quantity, String LE_quantity,
			String EQ_fieldname, String GT_fieldname, String GE_fieldname,
			String LT_fieldname, String LE_fieldname, String EQ_ILMD_fieldname,
			String GT_ILMD_fieldname, String GE_ILMD_fieldname,
			String LT_ILMD_fieldname, String LE_ILMD_fieldname,
			String EXIST_fieldname, String EXIST_ILMD_fieldname,
			String HASATTR_fieldname, String EQATTR_fieldname_attrname,
			String orderBy, String orderDirection, String eventCountLimit,
			String maxEventCount) {

		// M27 - query params' constraint
		// M39 - query params' constraint
		String reason = checkConstraintSimpleEventQuery(queryName, eventType,
				GE_eventTime, LT_eventTime, GE_recordTime, LT_recordTime,
				EQ_action, EQ_bizStep, EQ_disposition, EQ_readPoint,
				WD_readPoint, EQ_bizLocation, WD_bizLocation,
				EQ_bizTransaction_type, EQ_source_type, EQ_destination_type,
				EQ_transformationID, MATCH_epc, MATCH_parentID, MATCH_inputEPC,
				MATCH_outputEPC, MATCH_anyEPC, MATCH_epcClass,
				MATCH_inputEPCClass, MATCH_outputEPCClass, MATCH_anyEPCClass,
				EQ_quantity, GT_quantity, GE_quantity, LT_quantity,
				LE_quantity, EQ_fieldname, GT_fieldname, GE_fieldname,
				LT_fieldname, LE_fieldname, EQ_ILMD_fieldname,
				GT_ILMD_fieldname, GE_ILMD_fieldname, LT_ILMD_fieldname,
				LE_ILMD_fieldname, EXIST_fieldname, EXIST_ILMD_fieldname,
				HASATTR_fieldname, EQATTR_fieldname_attrname, orderBy,
				orderDirection, eventCountLimit, maxEventCount);
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
			List<Criteria> criteriaList = makeCriteria(GE_eventTime,
					LT_eventTime, GE_recordTime, LT_recordTime, EQ_action,
					EQ_bizStep, EQ_disposition, EQ_readPoint, WD_readPoint,
					EQ_bizLocation, WD_bizLocation, EQ_bizTransaction_type,
					EQ_source_type, EQ_destination_type, EQ_transformationID,
					MATCH_epc, MATCH_parentID, MATCH_inputEPC, MATCH_outputEPC,
					MATCH_anyEPC, MATCH_epcClass, MATCH_inputEPCClass,
					MATCH_outputEPCClass, MATCH_anyEPCClass, EQ_quantity,
					GT_quantity, GE_quantity, LT_quantity, LE_quantity,
					EQ_fieldname, GT_fieldname, GE_fieldname, LT_fieldname,
					LE_fieldname, EQ_ILMD_fieldname, GT_ILMD_fieldname,
					GE_ILMD_fieldname, LT_ILMD_fieldname, LE_ILMD_fieldname,
					EXIST_fieldname, EXIST_ILMD_fieldname, HASATTR_fieldname,
					EQATTR_fieldname_attrname, orderBy, orderDirection,
					eventCountLimit, maxEventCount);

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
			List<Criteria> criteriaList = makeCriteria(GE_eventTime,
					LT_eventTime, GE_recordTime, LT_recordTime, EQ_action,
					EQ_bizStep, EQ_disposition, EQ_readPoint, WD_readPoint,
					EQ_bizLocation, WD_bizLocation, EQ_bizTransaction_type,
					EQ_source_type, EQ_destination_type, EQ_transformationID,
					MATCH_epc, MATCH_parentID, MATCH_inputEPC, MATCH_outputEPC,
					MATCH_anyEPC, MATCH_epcClass, MATCH_inputEPCClass,
					MATCH_outputEPCClass, MATCH_anyEPCClass, EQ_quantity,
					GT_quantity, GE_quantity, LT_quantity, LE_quantity,
					EQ_fieldname, GT_fieldname, GE_fieldname, LT_fieldname,
					LE_fieldname, EQ_ILMD_fieldname, GT_ILMD_fieldname,
					GE_ILMD_fieldname, LT_ILMD_fieldname, LE_ILMD_fieldname,
					EXIST_fieldname, EXIST_ILMD_fieldname, HASATTR_fieldname,
					EQATTR_fieldname_attrname, orderBy, orderDirection,
					eventCountLimit, maxEventCount);

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
			List<Criteria> criteriaList = makeCriteria(GE_eventTime,
					LT_eventTime, GE_recordTime, LT_recordTime, EQ_action,
					EQ_bizStep, EQ_disposition, EQ_readPoint, WD_readPoint,
					EQ_bizLocation, WD_bizLocation, EQ_bizTransaction_type,
					EQ_source_type, EQ_destination_type, EQ_transformationID,
					MATCH_epc, MATCH_parentID, MATCH_inputEPC, MATCH_outputEPC,
					MATCH_anyEPC, MATCH_epcClass, MATCH_inputEPCClass,
					MATCH_outputEPCClass, MATCH_anyEPCClass, EQ_quantity,
					GT_quantity, GE_quantity, LT_quantity, LE_quantity,
					EQ_fieldname, GT_fieldname, GE_fieldname, LT_fieldname,
					LE_fieldname, EQ_ILMD_fieldname, GT_ILMD_fieldname,
					GE_ILMD_fieldname, LT_ILMD_fieldname, LE_ILMD_fieldname,
					EXIST_fieldname, EXIST_ILMD_fieldname, HASATTR_fieldname,
					EQATTR_fieldname_attrname, orderBy, orderDirection,
					eventCountLimit, maxEventCount);

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
			List<Criteria> criteriaList = makeCriteria(GE_eventTime,
					LT_eventTime, GE_recordTime, LT_recordTime, EQ_action,
					EQ_bizStep, EQ_disposition, EQ_readPoint, WD_readPoint,
					EQ_bizLocation, WD_bizLocation, EQ_bizTransaction_type,
					EQ_source_type, EQ_destination_type, EQ_transformationID,
					MATCH_epc, MATCH_parentID, MATCH_inputEPC, MATCH_outputEPC,
					MATCH_anyEPC, MATCH_epcClass, MATCH_inputEPCClass,
					MATCH_outputEPCClass, MATCH_anyEPCClass, EQ_quantity,
					GT_quantity, GE_quantity, LT_quantity, LE_quantity,
					EQ_fieldname, GT_fieldname, GE_fieldname, LT_fieldname,
					LE_fieldname, EQ_ILMD_fieldname, GT_ILMD_fieldname,
					GE_ILMD_fieldname, LT_ILMD_fieldname, LE_ILMD_fieldname,
					EXIST_fieldname, EXIST_ILMD_fieldname, HASATTR_fieldname,
					EQATTR_fieldname_attrname, orderBy, orderDirection,
					eventCountLimit, maxEventCount);

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
			List<Criteria> criteriaList = makeCriteria(GE_eventTime,
					LT_eventTime, GE_recordTime, LT_recordTime, EQ_action,
					EQ_bizStep, EQ_disposition, EQ_readPoint, WD_readPoint,
					EQ_bizLocation, WD_bizLocation, EQ_bizTransaction_type,
					EQ_source_type, EQ_destination_type, EQ_transformationID,
					MATCH_epc, MATCH_parentID, MATCH_inputEPC, MATCH_outputEPC,
					MATCH_anyEPC, MATCH_epcClass, MATCH_inputEPCClass,
					MATCH_outputEPCClass, MATCH_anyEPCClass, EQ_quantity,
					GT_quantity, GE_quantity, LT_quantity, LE_quantity,
					EQ_fieldname, GT_fieldname, GE_fieldname, LT_fieldname,
					LE_fieldname, EQ_ILMD_fieldname, GT_ILMD_fieldname,
					GE_ILMD_fieldname, LT_ILMD_fieldname, LE_ILMD_fieldname,
					EXIST_fieldname, EXIST_ILMD_fieldname, HASATTR_fieldname,
					EQATTR_fieldname_attrname, orderBy, orderDirection,
					eventCountLimit, maxEventCount);

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
		if (eventObjects.size() > Integer.parseInt(maxEventCount)) {
			((AbstractApplicationContext) ctx).close();
			return makeErrorResult("Violate maxEventCount",
					QueryTooLargeException.class);
		}

		((AbstractApplicationContext) ctx).close();
		StringWriter sw = new StringWriter();
		JAXB.marshal(epcisQueryDocumentType, sw);
		return sw.toString();
	}

	public String pollMasterDataQuery(String queryName, String vocabularyName,
			boolean includeAttributes, boolean includeChildren,
			String attributeNames, String eQ_name, String wD_name,
			String hASATTR, String eQATTR_attrname, String maxElementCount) {

		// Make Base Result Document
		EPCISQueryDocumentType epcisQueryDocumentType = makeBaseResultDocument(queryName);

		ApplicationContext ctx = new GenericXmlApplicationContext(
				"classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx
				.getBean("mongoTemplate");

		List<Criteria> criteriaList = makeCritera(vocabularyName,
				includeAttributes, includeChildren, attributeNames, eQ_name,
				wD_name, hASATTR, eQATTR_attrname, maxElementCount);

		Query query = new Query();
		for (int i = 0; i < criteriaList.size(); i++) {
			query.addCriteria(criteriaList.get(i));
		}

		List<VocabularyType> vList = mongoOperation.find(query,
				VocabularyType.class);

		// TODO: Filter Should be implemented

		QueryResultsBody qbt = epcisQueryDocumentType.getEPCISBody()
				.getQueryResults().getResultsBody();

		VocabularyListType vlt = new VocabularyListType();
		vlt.setVocabulary(vList);
		qbt.setVocabularyList(vlt);

		((AbstractApplicationContext) ctx).close();
		
		// M47
		if( maxElementCount != null )
		{
			try{
				int maxElement = Integer.parseInt(maxElementCount);
				if( vList.size() > maxElement )
				{
					return makeErrorResult("Too Large Master Data result", QueryTooLargeException.class);
				}
			}catch( NumberFormatException e )
			{
				
			}
		}
		
		
		StringWriter sw = new StringWriter();
		JAXB.marshal(epcisQueryDocumentType, sw);
		return sw.toString();
	}

	@SuppressWarnings({})
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
			@RequestParam(required = false) String maxEventCount,

			@RequestParam(required = false) String vocabularyName,
			@RequestParam(required = false) boolean includeAttributes,
			@RequestParam(required = false) boolean includeChildren,
			@RequestParam(required = false) String attributeNames,
			@RequestParam(required = false) String EQ_name,
			@RequestParam(required = false) String WD_name,
			@RequestParam(required = false) String HASATTR,
			@RequestParam(required = false) String EQATTR_attrname,
			@RequestParam(required = false) String maxElementCount) {

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
					EQ_bizLocation, WD_bizLocation, EQ_bizTransaction_type,
					EQ_source_type, EQ_destination_type, EQ_transformationID,
					MATCH_epc, MATCH_parentID, MATCH_inputEPC, MATCH_outputEPC,
					MATCH_anyEPC, MATCH_epcClass, MATCH_inputEPCClass,
					MATCH_outputEPCClass, MATCH_anyEPCClass, EQ_quantity,
					GT_quantity, GE_quantity, LT_quantity, LE_quantity,
					EQ_fieldname, GT_fieldname, GE_fieldname, LT_fieldname,
					LE_fieldname, EQ_ILMD_fieldname, GT_ILMD_fieldname,
					GE_ILMD_fieldname, LT_ILMD_fieldname, LE_ILMD_fieldname,
					EXIST_fieldname, EXIST_ILMD_fieldname, HASATTR_fieldname,
					EQATTR_fieldname_attrname, orderBy, orderDirection,
					eventCountLimit, maxEventCount);

		if (queryName.equals("SimpleMasterDataQuery"))
			return pollMasterDataQuery(queryName, vocabularyName,
					includeAttributes, includeChildren, attributeNames,
					EQ_name, WD_name, HASATTR, EQATTR_attrname, maxElementCount);
		return "";
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
	@Override
	public QueryResults poll(String queryName, QueryParams params) {
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

	public Query makeSortAndLimitQuery(Query query, String orderBy,
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
				isNumberFormatException = true;
				ConfigurationServlet.logger.log(Level.ERROR, nfe.toString());
			}
		}

		return query;
	}

	public List<Criteria> makeCritera(String vocabularyName,
			boolean includeAttributes, boolean includeChildren,
			String attributeNames, String eQ_name, String wD_name,
			String hASATTR, String eQATTR_attrname, String maxElementCount) {

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

	public List<Criteria> makeCriteria(String GE_eventTime,
			String LT_eventTime, String GE_recordTime, String LT_recordTime,
			String EQ_action, String EQ_bizStep, String EQ_disposition,
			String EQ_readPoint, String WD_readPoint, String EQ_bizLocation,
			String WD_bizLocation, String EQ_bizTransaction_type,
			String EQ_source_type, String EQ_destination_type,
			String EQ_transformationID, String MATCH_epc,
			String MATCH_parentID, String MATCH_inputEPC,
			String MATCH_outputEPC, String MATCH_anyEPC, String MATCH_epcClass,
			String MATCH_inputEPCClass, String MATCH_outputEPCClass,
			String MATCH_anyEPCClass, String EQ_quantity, String GT_quantity,
			String GE_quantity, String LT_quantity, String LE_quantity,
			String EQ_fieldname, String GT_fieldname, String GE_fieldname,
			String LT_fieldname, String LE_fieldname, String EQ_ILMD_fieldname,
			String GT_ILMD_fieldname, String GE_ILMD_fieldname,
			String LT_ILMD_fieldname, String LE_ILMD_fieldname,
			String EXIST_fieldname, String EXIST_ILMD_fieldname,
			String HASATTR_fieldname, String EQATTR_fieldname_attrname,
			String orderBy, String orderDirection, String eventCountLimit,
			String maxEventCount) {

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
				String[] eqActionArray = EQ_action.split(",");
				List<String> subStringList = new ArrayList<String>();
				for (int i = 0; i < eqActionArray.length; i++) {
					String eqActionString = eqActionArray[i].trim();
					// According to SPEC, this condition thwos
					// QueryParameterException
					if (!eqActionString.equals("ADD")
							&& !eqActionString.equals("OBSERVE")
							&& !eqActionString.equals("DELETE")) {
						isQueryParameterException = true;
					}
					if (eqActionString.equals("ADD")
							|| eqActionString.equals("OBSERVE")
							|| eqActionString.equals("DELETE"))
						subStringList.add(eqActionString);
				}

				if (subStringList != null)
					criteriaList
							.add(Criteria.where("action").in(subStringList));
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
			 * Implementation Pending
			 * 
			 * *_fieldname
			 * 
			 **/

		} catch (ParseException e) {
			ConfigurationServlet.logger.log(Level.ERROR, e.toString());
		}
		return criteriaList;
	}

	public void addScheduleToQuartz(SubscriptionType subscription) {
		try {
			JobDataMap map = new JobDataMap();
			map.put("queryName", subscription.getQueryName());
			map.put("subscriptionID", subscription.getSubscriptionID());
			map.put("dest", subscription.getDest());
			map.put("cronExpression", subscription.getCronExpression());

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
			if (subscription.getEQ_bizTransaction_type() != null)
				map.put("EQ_bizTransaction_type",
						subscription.getEQ_bizTransaction_type());
			if (subscription.getEQ_source_type() != null)
				map.put("EQ_source_type", subscription.getEQ_source_type());
			if (subscription.getEQ_destination_type() != null)
				map.put("EQ_destination_type",
						subscription.getEQ_destination_type());
			if (subscription.getEQ_transformationID() != null)
				map.put("EQ_transformationID",
						subscription.getEQ_transformationID());
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
				map.put("MATCH_inputEPCClass",
						subscription.getMATCH_inputEPCClass());
			if (subscription.getMATCH_outputEPCClass() != null)
				map.put("MATCH_outputEPCClass",
						subscription.getMATCH_outputEPCClass());
			if (subscription.getMATCH_anyEPCClass() != null)
				map.put("MATCH_anyEPCClass",
						subscription.getMATCH_anyEPCClass());
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

			if (subscription.getEQ_fieldname() != null)
				map.put("EQ_fieldname", subscription.getEQ_fieldname());
			if (subscription.getGT_fieldname() != null)
				map.put("GT_fieldname", subscription.getGT_fieldname());
			if (subscription.getGE_fieldname() != null)
				map.put("GE_fieldname", subscription.getGE_fieldname());
			if (subscription.getLT_fieldname() != null)
				map.put("LT_fieldname", subscription.getLT_fieldname());
			if (subscription.getLE_fieldname() != null)
				map.put("LE_fieldname", subscription.getLE_fieldname());

			if (subscription.getEQ_ILMD_fieldname() != null)
				map.put("EQ_ILMD_fieldname",
						subscription.getEQ_ILMD_fieldname());
			if (subscription.getGT_ILMD_fieldname() != null)
				map.put("GT_ILMD_fieldname",
						subscription.getGT_ILMD_fieldname());
			if (subscription.getGE_ILMD_fieldname() != null)
				map.put("GE_ILMD_fieldname",
						subscription.getGE_ILMD_fieldname());
			if (subscription.getLT_ILMD_fieldname() != null)
				map.put("LT_ILMD_fieldname",
						subscription.getLT_ILMD_fieldname());
			if (subscription.getLE_ILMD_fieldname() != null)
				map.put("LE_ILMD_fieldname",
						subscription.getLE_ILMD_fieldname());

			if (subscription.getEXIST_fieldname() != null)
				map.put("EXIST_fieldname", subscription.getEXIST_fieldname());
			if (subscription.getEXIST_ILMD_fieldname() != null)
				map.put("EXIST_ILMD_fieldname",
						subscription.getEXIST_ILMD_fieldname());
			if (subscription.getHASATTR_fieldname() != null)
				map.put("HASATTR_fieldname",
						subscription.getHASATTR_fieldname());
			if (subscription.getEQATTR_fieldname_attrname() != null)
				map.put("EQATTR_fieldname_attrname",
						subscription.getEQATTR_fieldname_attrname());
			if (subscription.getOrderBy() != null)
				map.put("orderBy", subscription.getOrderBy());
			if (subscription.getOrderDirection() != null)
				map.put("orderDirection", subscription.getOrderDirection());
			if (subscription.getEventCountLimit() != null)
				map.put("eventCountLimit", subscription.getEventCountLimit());
			if (subscription.getMaxEventCount() != null)
				map.put("maxEventCount", subscription.getMaxEventCount());

			JobDetail job = newJob(SubscriptionTask.class)
					.withIdentity(subscription.getSubscriptionID(),
							subscription.getQueryName()).setJobData(map)
					.build();

			Trigger trigger = newTrigger()
					.withIdentity(subscription.getSubscriptionID(),
							subscription.getQueryName())
					.startNow()
					.withSchedule(
							cronSchedule(subscription.getCronExpression()))
					.forJob(subscription.getSubscriptionID(),
							subscription.getQueryName()).build();

			// ClassPathXmlApplicationContext context = new
			// ClassPathXmlApplicationContext(
			// "classpath:QuartzConfig.xml");
			// Scheduler sched = (Scheduler) context
			// .getBean("schedulerFactoryBean");

			if (SubscriptionServlet.sched.isStarted() != true)
				SubscriptionServlet.sched.start();
			SubscriptionServlet.sched.scheduleJob(job, trigger);
			ConfigurationServlet.logger.log(Level.INFO, "Subscription ID: "
					+ subscription.getSubscriptionID()
					+ " is added to quartz scheduler. ");
		} catch (SchedulerException e) {
			ConfigurationServlet.logger.log(Level.ERROR, e.toString());
		}
	}

	public void addScheduleToQuartz(String queryName, String subscriptionID,
			String dest, String cronExpression, String eventType,
			String GE_eventTime, String LT_eventTime, String GE_recordTime,
			String LT_recordTime, String EQ_action, String EQ_bizStep,
			String EQ_disposition, String EQ_readPoint, String WD_readPoint,
			String EQ_bizLocation, String WD_bizLocation,
			String EQ_bizTransaction_type, String EQ_source_type,
			String EQ_destination_type, String EQ_transformationID,
			String MATCH_epc, String MATCH_parentID, String MATCH_inputEPC,
			String MATCH_outputEPC, String MATCH_anyEPC, String MATCH_epcClass,
			String MATCH_inputEPCClass, String MATCH_outputEPCClass,
			String MATCH_anyEPCClass, String EQ_quantity, String GT_quantity,
			String GE_quantity, String LT_quantity, String LE_quantity,
			String EQ_fieldname, String GT_fieldname, String GE_fieldname,
			String LT_fieldname, String LE_fieldname, String EQ_ILMD_fieldname,
			String GT_ILMD_fieldname, String GE_ILMD_fieldname,
			String LT_ILMD_fieldname, String LE_ILMD_fieldname,
			String EXIST_fieldname, String EXIST_ILMD_fieldname,
			String HASATTR_fieldname, String EQATTR_fieldname_attrname,
			String orderBy, String orderDirection, String eventCountLimit,
			String maxEventCount) {
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
			if (EQ_bizTransaction_type != null)
				map.put("EQ_bizTransaction_type", EQ_bizTransaction_type);
			if (EQ_source_type != null)
				map.put("EQ_source_type", EQ_source_type);
			if (EQ_destination_type != null)
				map.put("EQ_destination_type", EQ_destination_type);
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

			if (EQ_fieldname != null)
				map.put("EQ_fieldname", EQ_fieldname);
			if (GT_fieldname != null)
				map.put("GT_fieldname", GT_fieldname);
			if (GE_fieldname != null)
				map.put("GE_fieldname", GE_fieldname);
			if (LT_fieldname != null)
				map.put("LT_fieldname", LT_fieldname);
			if (LE_fieldname != null)
				map.put("LE_fieldname", LE_fieldname);

			if (EQ_ILMD_fieldname != null)
				map.put("EQ_ILMD_fieldname", EQ_ILMD_fieldname);
			if (GT_ILMD_fieldname != null)
				map.put("GT_ILMD_fieldname", GT_ILMD_fieldname);
			if (GE_ILMD_fieldname != null)
				map.put("GE_ILMD_fieldname", GE_ILMD_fieldname);
			if (LT_ILMD_fieldname != null)
				map.put("LT_ILMD_fieldname", LT_ILMD_fieldname);
			if (LE_ILMD_fieldname != null)
				map.put("LE_ILMD_fieldname", LE_ILMD_fieldname);

			if (EXIST_fieldname != null)
				map.put("EXIST_fieldname", EXIST_fieldname);
			if (EXIST_ILMD_fieldname != null)
				map.put("EXIST_ILMD_fieldname", EXIST_ILMD_fieldname);
			if (HASATTR_fieldname != null)
				map.put("HASATTR_fieldname", HASATTR_fieldname);
			if (EQATTR_fieldname_attrname != null)
				map.put("EQATTR_fieldname_attrname", EQATTR_fieldname_attrname);
			if (orderBy != null)
				map.put("orderBy", orderBy);
			if (orderDirection != null)
				map.put("orderDirection", orderDirection);
			if (eventCountLimit != null)
				map.put("eventCountLimit", eventCountLimit);
			if (maxEventCount != null)
				map.put("maxEventCount", maxEventCount);

			JobDetail job = newJob(SubscriptionTask.class)
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

			if (SubscriptionServlet.sched.isStarted() != true)
				SubscriptionServlet.sched.start();
			SubscriptionServlet.sched.scheduleJob(job, trigger);

			ConfigurationServlet.logger.log(Level.INFO, "Subscription ID: "
					+ subscriptionID + " is added to quartz scheduler. ");
		} catch (SchedulerException e) {
			ConfigurationServlet.logger.log(Level.ERROR, e.toString());
		}
	}

	@SuppressWarnings("resource")
	public boolean addScheduleToDB(String queryName, String subscriptionID,
			String dest, String cronExpression, String eventType,
			String GE_eventTime, String LT_eventTime, String GE_recordTime,
			String LT_recordTime, String EQ_action, String EQ_bizStep,
			String EQ_disposition, String EQ_readPoint, String WD_readPoint,
			String EQ_bizLocation, String WD_bizLocation,
			String EQ_bizTransaction_type, String EQ_source_type,
			String EQ_destination_type, String EQ_transformationID,
			String MATCH_epc, String MATCH_parentID, String MATCH_inputEPC,
			String MATCH_outputEPC, String MATCH_anyEPC, String MATCH_epcClass,
			String MATCH_inputEPCClass, String MATCH_outputEPCClass,
			String MATCH_anyEPCClass, String EQ_quantity, String GT_quantity,
			String GE_quantity, String LT_quantity, String LE_quantity,
			String EQ_fieldname, String GT_fieldname, String GE_fieldname,
			String LT_fieldname, String LE_fieldname, String EQ_ILMD_fieldname,
			String GT_ILMD_fieldname, String GE_ILMD_fieldname,
			String LT_ILMD_fieldname, String LE_ILMD_fieldname,
			String EXIST_fieldname, String EXIST_ILMD_fieldname,
			String HASATTR_fieldname, String EQATTR_fieldname_attrname,
			String orderBy, String orderDirection, String eventCountLimit,
			String maxEventCount) {
		SubscriptionType st = new SubscriptionType(queryName, subscriptionID,
				dest, cronExpression, eventType, GE_eventTime, LT_eventTime,
				GE_recordTime, LT_recordTime, EQ_action, EQ_bizStep,
				EQ_disposition, EQ_readPoint, WD_readPoint, EQ_bizLocation,
				WD_bizLocation, EQ_bizTransaction_type, EQ_source_type,
				EQ_destination_type, EQ_transformationID, MATCH_epc,
				MATCH_parentID, MATCH_inputEPC, MATCH_outputEPC, MATCH_anyEPC,
				MATCH_epcClass, MATCH_inputEPCClass, MATCH_outputEPCClass,
				MATCH_anyEPCClass, EQ_quantity, GT_quantity, GE_quantity,
				LT_quantity, LE_quantity, EQ_fieldname, GT_fieldname,
				GE_fieldname, LT_fieldname, LE_fieldname, EQ_ILMD_fieldname,
				GT_ILMD_fieldname, GE_ILMD_fieldname, LT_ILMD_fieldname,
				LE_ILMD_fieldname, EXIST_fieldname, EXIST_ILMD_fieldname,
				HASATTR_fieldname, EQATTR_fieldname_attrname, orderBy,
				orderDirection, eventCountLimit, maxEventCount);
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
			mongoOperation.save(st);

		ConfigurationServlet.logger.log(Level.INFO, "Subscription ID: "
				+ subscriptionID + " is added to DB. ");
		((AbstractApplicationContext) ctx).close();
		return true;
	}

	public void removeScheduleFromQuartz(SubscriptionType subscription) {
		try {
			SubscriptionServlet.sched.unscheduleJob(triggerKey(
					subscription.getSubscriptionID(),
					subscription.getQueryName()));
			SubscriptionServlet.sched.deleteJob(jobKey(
					subscription.getSubscriptionID(),
					subscription.getQueryName()));
			ConfigurationServlet.logger.log(Level.INFO, "Subscription ID: "
					+ subscription + " is removed from scheduler");
		} catch (SchedulerException e) {
			ConfigurationServlet.logger.log(Level.ERROR, e.toString());
		}
	}

	public void removeScheduleFromDB(MongoOperations mongoOperation,
			SubscriptionType subscription) {
		mongoOperation.remove(
				new Query(Criteria.where("subscriptionID").is(
						subscription.getSubscriptionID())),
				SubscriptionType.class);
		ConfigurationServlet.logger.log(Level.INFO, "Subscription ID: "
				+ subscription + " is removed from DB");
	}

	@SuppressWarnings("rawtypes")
	public String makeErrorResult(String err, Class type) {
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
		if (type == SubscribeNotPermittedException.class )
		{
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
