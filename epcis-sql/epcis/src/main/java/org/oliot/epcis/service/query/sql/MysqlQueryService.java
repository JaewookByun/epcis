package org.oliot.epcis.service.query.sql;


/**
 * Copyright (C) 2014-2016 Jaewook Byun, Yalew kidane
 *
 * This project is part of Oliot open source (http://oliot.org). Oliot EPCIS
 * v1.2.x is Java Web Service complying with Electronic Product Code Information
 * Service (EPCIS) v1.2.
 *
 *         
 * @author Yalew kidane, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         yalewkidane@gmail.com/@kaist.ac.kr
 */

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.apache.log4j.Level;
import org.hibernate.Criteria;
import org.json.JSONArray;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.epcis.serde.sql.EventToEventTypeConverter;
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
import org.oliot.model.epcis.QuerySchedule;
import org.oliot.model.epcis.QueryTooLargeException;
import org.oliot.model.epcis.SubscribeNotPermittedException;
import org.oliot.model.epcis.SubscriptionControls;
import org.oliot.model.epcis.SubscriptionControlsException;
import org.oliot.model.epcis.SubscriptionType;
import org.oliot.model.epcis.TransactionEventType;
import org.oliot.model.epcis.VocabularyListType;
import org.oliot.model.epcis.VocabularyType;
import org.oliot.model.oliot.AggregationEvent;
import org.oliot.model.oliot.Attribute;
import org.oliot.model.oliot.ObjectEvent;
import org.oliot.model.oliot.PollParameters;
import org.oliot.model.oliot.QuantityElement;
import org.oliot.model.oliot.QuantityEvent;
import org.oliot.model.oliot.Subscription;
import org.oliot.model.oliot.TransactionEvent;
import org.oliot.model.oliot.TransformationEvent;
import org.oliot.model.oliot.Vocabulary;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.web.bind.annotation.PathVariable;


import static org.quartz.TriggerKey.*;
import static org.quartz.JobKey.*;



public class MysqlQueryService {

	public String subscribe(SubscriptionType subscription) {
		
		
		String queryName = subscription.getPollParameters().getQueryName();
		String subscriptionID = subscription.getSubscriptionID();
		String dest = subscription.getDest();
		String cronExpression ="";// subscription.getPollParameters().getCronExpression();
		boolean reportIfEmpty = subscription.getReportIfEmpty();
		String initialRecordTime = subscription.getInitialRecordTime();
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
		
		String EQ_eventID= subscription.getPollParameters().getEQ_eventID(); 
		boolean EXISTS_errorDeclaration= subscription.getPollParameters().getEXISTS_errorDeclaration();
		String GE_errorDeclarationTime= subscription.getPollParameters().getGE_errorDeclarationTime();
		String LT_errorDeclarationTime= subscription.getPollParameters().getLT_errorDeclarationTime();
		String EQ_errorReason= subscription.getPollParameters().getEQ_errorReason();
		String EQ_correctiveEventID= subscription.getPollParameters().getEQ_correctiveEventID();
		
		String orderBy = subscription.getPollParameters().getOrderBy();
		String orderDirection = subscription.getPollParameters().getOrderDirection();
		String eventCountLimit = subscription.getPollParameters().getEventCountLimit().toString();
		String maxEventCount = subscription.getPollParameters().getMaxEventCount().toString();
		Map<String, String> paramMap = subscription.getPollParameters().getParams();

		String result = subscribe(queryName, subscriptionID, dest,
				cronExpression, reportIfEmpty, initialRecordTime, eventType,
				GE_eventTime, LT_eventTime, GE_recordTime, LT_recordTime,
				EQ_action, EQ_bizStep, EQ_disposition, EQ_readPoint,
				WD_readPoint, EQ_bizLocation, WD_bizLocation,
				EQ_transformationID, MATCH_epc, MATCH_parentID, MATCH_inputEPC,
				MATCH_outputEPC, MATCH_anyEPC, MATCH_epcClass,
				MATCH_inputEPCClass, MATCH_outputEPCClass, MATCH_anyEPCClass,
				EQ_quantity, GT_quantity, GE_quantity, LT_quantity,	LE_quantity, 
				EQ_eventID, EXISTS_errorDeclaration,GE_errorDeclarationTime,
				LT_errorDeclarationTime, EQ_errorReason, EQ_correctiveEventID,
				orderBy, orderDirection, eventCountLimit,
				maxEventCount, paramMap);

		return result;
	
	}

	public String subscribeEventQuery(String queryName, String subscriptionID,
			String dest, String cronExpression, boolean reportIfEmpty,
			String initialRecordTimeStr, String eventType, String GE_eventTime,
			String LT_eventTime, String GE_recordTime, String LT_recordTime,
			String EQ_action, String EQ_bizStep, String EQ_disposition,
			String EQ_readPoint, String WD_readPoint, String EQ_bizLocation,
			String WD_bizLocation, String EQ_transformationID,
			String MATCH_epc, String MATCH_parentID, String MATCH_inputEPC,
			String MATCH_outputEPC, String MATCH_anyEPC, String MATCH_epcClass,
			String MATCH_inputEPCClass, String MATCH_outputEPCClass,
			String MATCH_anyEPCClass, String EQ_quantity, String GT_quantity,
			String GE_quantity, String LT_quantity, String LE_quantity,
			String EQ_eventID, boolean EXISTS_errorDeclaration,String GE_errorDeclarationTime,
			String LT_errorDeclarationTime, String EQ_errorReason, String EQ_correctiveEventID,
			String orderBy, String orderDirection, String eventCountLimit,
			String maxEventCount, Map<String, String> paramMap) {

		
		System.out.println("subscribeEventQuery");
		// M27 - query params' constraint
		// M39 - query params' constraint

		String reason = checkConstraintSimpleEventQuery(queryName, eventType,
				GE_eventTime, LT_eventTime, GE_recordTime, LT_recordTime,
				EQ_action, EQ_bizStep, EQ_disposition, EQ_readPoint,
				WD_readPoint, EQ_bizLocation, WD_bizLocation,
				EQ_transformationID, MATCH_epc, MATCH_parentID, MATCH_inputEPC,
				MATCH_outputEPC, MATCH_anyEPC, MATCH_epcClass,
				MATCH_inputEPCClass, MATCH_outputEPCClass, MATCH_anyEPCClass,
				EQ_quantity, GT_quantity, GE_quantity, LT_quantity,LE_quantity, 
				EQ_eventID, EXISTS_errorDeclaration,GE_errorDeclarationTime,
				LT_errorDeclarationTime, EQ_errorReason, EQ_correctiveEventID,
				orderBy, orderDirection, eventCountLimit,
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
				reportIfEmpty, initialRecordTimeStr, eventType, GE_eventTime,
				LT_eventTime, GE_recordTime, LT_recordTime, EQ_action,
				EQ_bizStep, EQ_disposition, EQ_readPoint, WD_readPoint,
				EQ_bizLocation, WD_bizLocation, EQ_transformationID, MATCH_epc,
				MATCH_parentID, MATCH_inputEPC, MATCH_outputEPC, MATCH_anyEPC,
				MATCH_epcClass, MATCH_inputEPCClass, MATCH_outputEPCClass,
				MATCH_anyEPCClass, EQ_quantity, GT_quantity, GE_quantity,
				LT_quantity, LE_quantity, orderBy, orderDirection,
				eventCountLimit, maxEventCount, paramMap);

		String retString = "SubscriptionID : " + subscriptionID
				+ " is successfully triggered. ";
		return retString;
	}
	

	// Soap Query Adaptor
	public void subscribe(String queryName, QueryParams params, URI dest,
			SubscriptionControls controls, String subscriptionID) {
  System.out.println("subscribe soap");
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
		
		String EQ_eventID= null; 
		String EXISTS_errorDeclaration = null;
		String GE_errorDeclarationTime= null;
		String LT_errorDeclarationTime= null;
		String EQ_errorReason= null;
		String EQ_correctiveEventID= null;
		
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
			}else if (name.equals("EQ_eventID")) {
				EQ_eventID = value;
				continue;
			}else if (name.equals("EXISTS_errorDeclaration")) {
				EXISTS_errorDeclaration = value;
				continue;
			}else if (name.equals("GE_errorDeclarationTime")) {
				GE_errorDeclarationTime = value;
				continue;
			}else if (name.equals("LT_errorDeclarationTime")) {
				LT_errorDeclarationTime = value;
				continue;
			}else if (name.equals("EQ_errorReason")) {
				EQ_errorReason = value;
				continue;
			}else if (name.equals("EQ_correctiveEventID")) {
				EQ_correctiveEventID = value;
				continue;
			}else if (name.equals("orderBy")) {
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
		
		QuerySchedule querySchedule = controls.getSchedule();
		if (cronExpression == null) {
			String sec = querySchedule.getSecond();
			String min = querySchedule.getMinute();
			String hours = querySchedule.getHour();
			String dayOfMonth = querySchedule.getDayOfMonth();
			String month = querySchedule.getMonth();
			String dayOfWeek = querySchedule.getDayOfWeek();
			cronExpression = sec + " " + min + " " + hours + " " + dayOfMonth
					+ " " + month + " " + dayOfWeek;
		}

		/*
		 * InitialRecordTime: (Optional) Specifies a time used to constrain what
		 * events are considered when processing the query when it is executed
		 * for the first time. See Section 8.2.5.2. If omitted, defaults to the
		 * time at which the subscription is created.
		 */
		XMLGregorianCalendar initialRecordTime = controls
				.getInitialRecordTime();
		if (initialRecordTime == null) {
			try {
				initialRecordTime = DatatypeFactory.newInstance()
						.newXMLGregorianCalendar();
			} catch (DatatypeConfigurationException e) {

				e.printStackTrace();
			}
		}
		SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		Date initialRecordDate = initialRecordTime.toGregorianCalendar()
				.getTime();
		String initialRecordTimeStr = sdf.format(initialRecordDate);
		/*
		 * reportIfEmpty: If true, a QueryResults instance is always sent to the
		 * subscriber when the query is executed. If false, a QueryResults
		 * instance is sent to the subscriber only when the results are
		 * non-empty.
		 */
		reportIfEmpty = controls.isReportIfEmpty();

		subscribe(queryName, subscriptionID, destStr, cronExpression,
				reportIfEmpty, initialRecordTimeStr, eventType, GE_eventTime,
				LT_eventTime, GE_recordTime, LT_recordTime, EQ_action,
				EQ_bizStep, EQ_disposition, EQ_readPoint, WD_readPoint,
				EQ_bizLocation, WD_bizLocation, EQ_transformationID, MATCH_epc,
				MATCH_parentID, MATCH_inputEPC, MATCH_outputEPC, MATCH_anyEPC,
				MATCH_epcClass, MATCH_inputEPCClass, MATCH_outputEPCClass,
				MATCH_anyEPCClass, EQ_quantity, GT_quantity, GE_quantity,
				LT_quantity, LE_quantity, 
				EQ_eventID,  Boolean.parseBoolean(EXISTS_errorDeclaration), GE_errorDeclarationTime,
				 LT_errorDeclarationTime,  EQ_errorReason,  EQ_correctiveEventID,
				orderBy, orderDirection,
				eventCountLimit, maxEventCount, extMap);

	}

	public String subscribe(Subscription s,String userID, List<String> friendList) throws QueryParameterException,SubscriptionControlsException, InvalidURIException, SubscribeNotPermittedException {
	// M20 : Throw an InvalidURIException for an incorrect dest argument in
	// the subscribe method in EPCIS Query Control Interface
	try {
		new URL(s.getDest());
	} catch (MalformedURLException e) {
		throw new InvalidURIException();
		// return makeErrorResult(e.toString(), InvalidURIException.class);
	}

	// M24 : Virtual Error Handling
	// Automatically processed by URI param
	// v1.2 not work
	if (s.getDest() == null) {
		throw new QueryParameterException();
		// return makeErrorResult("Fill the mandatory field in subscribe
		// method", QueryParameterException.class);
	}

	// M46
	if (s.getPollParametrs().getQueryName().equals("SimpleMasterDataQuery")) {
		throw new SubscribeNotPermittedException();
		// return makeErrorResult("SimpleMasterDataQuery is not available in
		// subscription method", SubscribeNotPermittedException.class);
	}

	String retString = "";
	if (s.getPollParametrs().getQueryName().equals("SimpleEventQuery")) {
		//retString = subscribeEventQuery(s, userID, friendList);
		
		PollParameters p=s.getPollParametrs();
		retString =subscribe(p.getQueryName(), s.getSubscriptionID(), s.getDest(), "cronExpression",s.getReportIfEmpty(), 
				s.getInitialRecordTime(), p.getEventType(), p.getGE_eventTime(), 
				p.getLT_eventTime(), p.getGE_recordTime(), p.getLT_recordTime(), 
				p.getEQ_action(),	p.getEQ_bizStep(), p.getEQ_disposition(), p.getEQ_readPoint(), 
				p.getWD_readPoint(), p.getEQ_bizLocation(), p.getWD_bizLocation(), p.getEQ_transformationID(), 
				p.getMATCH_epc(), p.getMATCH_parentID(), p.getMATCH_inputEPC(), p.getMATCH_outputEPC(), 
				p.getMATCH_anyEPC(), p.getMATCH_epcClass(), p.getMATCH_inputEPCClass(), p.getMATCH_outputEPCClass(), 
				p.getMATCH_anyEPCClass(), p.getEQ_quantity().toString(), p.getGT_quantity().toString(), 
				p.getGE_quantity().toString(), p.getLT_quantity().toString(), p.getLE_quantity().toString(), p.getEQ_eventID(), 
				p.getEXISTS_errorDeclaration(), p.getGE_errorDeclarationTime(), p.getLT_errorDeclarationTime(), 
				p.getEQ_errorReason(), p.getEQ_correctiveEventID(), p.getOrderBy(), p.getOrderDirection(), 
				p.getEventCountLimit().toString(), p.getMaxEventCount().toString(), p.getParams());
	}

	return retString;
}
	public String subscribe(String queryName, String subscriptionID,
			String dest, String cronExpression, boolean reportIfEmpty,
			String initialRecordTimeStr, String eventType, String GE_eventTime,
			String LT_eventTime, String GE_recordTime, String LT_recordTime,
			String EQ_action, String EQ_bizStep, String EQ_disposition,
			String EQ_readPoint, String WD_readPoint, String EQ_bizLocation,
			String WD_bizLocation, String EQ_transformationID,
			String MATCH_epc, String MATCH_parentID, String MATCH_inputEPC,
			String MATCH_outputEPC, String MATCH_anyEPC, String MATCH_epcClass,
			String MATCH_inputEPCClass, String MATCH_outputEPCClass,
			String MATCH_anyEPCClass, String EQ_quantity, String GT_quantity,
			String GE_quantity, String LT_quantity, String LE_quantity,
			String EQ_eventID, boolean EXISTS_errorDeclaration,String GE_errorDeclarationTime,
			String LT_errorDeclarationTime, String EQ_errorReason, String EQ_correctiveEventID,
			String orderBy, String orderDirection, String eventCountLimit,
			String maxEventCount, Map<String, String> paramMap) {
		
		System.out.println(2);

		String retString = "";
		if (queryName.equals("SimpleEventQuery")) {
			retString = subscribeEventQuery(queryName, subscriptionID, dest,
					cronExpression, reportIfEmpty, initialRecordTimeStr,
					eventType, GE_eventTime, LT_eventTime, GE_recordTime,
					LT_recordTime, EQ_action, EQ_bizStep, EQ_disposition,
					EQ_readPoint, WD_readPoint, EQ_bizLocation, WD_bizLocation,
					EQ_transformationID, MATCH_epc, MATCH_parentID,
					MATCH_inputEPC, MATCH_outputEPC, MATCH_anyEPC,
					MATCH_epcClass, MATCH_inputEPCClass, MATCH_outputEPCClass,
					MATCH_anyEPCClass, EQ_quantity, GT_quantity, GE_quantity,
					LT_quantity, LE_quantity, 
					 EQ_eventID,  EXISTS_errorDeclaration, GE_errorDeclarationTime,
					 LT_errorDeclarationTime,  EQ_errorReason,  EQ_correctiveEventID,
					orderBy, orderDirection,
					eventCountLimit, maxEventCount, paramMap);;
		}

		return retString;
	}

	public void unsubscribe(String subscriptionID) {
//		ApplicationContext ctx=new ClassPathXmlApplicationContext(Configuration.DB);
//		QueryOprationBackend mysqlOperationdao=ctx.getBean
//				("queryOprationBackend", QueryOprationBackend.class);

		// Its size should be 0 or 1
		List<SubscriptionType> subscriptions=Configuration.mysqlOperationdaoQr.findAllSubscriptionType( subscriptionID);
		

		for (int i = 0; i < subscriptions.size(); i++) {
			SubscriptionType subscription = subscriptions.get(i);
			//subscriptions
			// Remove from current Quartz
			removeScheduleFromQuartz(subscription);
			// Remove from DB list
			removeScheduleFromDB(Configuration.mysqlOperationdaoQr, subscription);
		}
	//	((AbstractApplicationContext) ctx).close();
	}

	public String getSubscriptionIDsREST(@PathVariable String queryName) {

//		ApplicationContext ctx=new ClassPathXmlApplicationContext(Configuration.DB);
//		QueryOprationBackend mysqlOperationdao=ctx.getBean
//				("queryOprationBackend", QueryOprationBackend.class);
		
		List<String> IdList=Configuration.mysqlOperationdaoQr.find(queryName);
				
		JSONArray retArray = new JSONArray();
		for (int i = 0; i < IdList.size(); i++) {
			retArray.put(IdList.get(i));
		}
		//((AbstractApplicationContext) ctx).close();
		return retArray.toString(1);
	}

	public List<String> getSubscriptionIDs(String queryName) {
		List<String> retList = new ArrayList<String>();
				
//		ApplicationContext ctx=new ClassPathXmlApplicationContext(Configuration.DB);
//		QueryOprationBackend mysqlOperationdao=ctx.getBean
//				("queryOprationBackend", QueryOprationBackend.class);
		
		retList=Configuration.mysqlOperationdaoQr.find(queryName);		
		//Configuration.logger.info(" Event Saved ");
	//	((AbstractApplicationContext) ctx).close();
		return retList;
	}
	

	@SuppressWarnings({ "unchecked", "rawtypes"})
	
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
			String EQ_eventID, Boolean EXISTS_errorDeclaration, String GE_errorDeclarationTime,
			String LT_errorDeclarationTime,String EQ_errorReason,String EQ_correctiveEventID,
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
				EQ_quantity, GT_quantity, GE_quantity, LT_quantity,LE_quantity, 
				EQ_eventID, EXISTS_errorDeclaration,GE_errorDeclarationTime,
				LT_errorDeclarationTime, EQ_errorReason, EQ_correctiveEventID,
				orderBy, orderDirection, eventCountLimit,
				maxEventCount, paramMap);
		
		if (reason != null) {
			return makeErrorResult(reason, QueryParameterException.class);
		}
		
		//EventCountLimit;
		
		int countLimit=0;
		int eventCount=0;
		if(eventCountLimit!=null){
			countLimit=Integer.parseInt(eventCountLimit);
		}else{
			countLimit=Integer.MAX_VALUE;
		}
		// Make Base Result Document
		EPCISQueryDocumentType epcisQueryDocumentType = makeBaseResultDocument(queryName);

		//ApplicationContext ctx=new ClassPathXmlApplicationContext(Configuration.DB);
		//QueryOprationBackend mysqlOperationdao=ctx.getBean
		//		("queryOprationBackend", QueryOprationBackend.class);

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
		Configuration.logger.info("eventType"+ eventType);
		
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
		//	DBCollection collection = mongoOperation.getCollection("AggregationEvent");
			
			 
			 Criteria criteria=Configuration.mysqlOperationdaoQr.makeQueryCriteria("AggregationEvent",
					GE_eventTime, LT_eventTime, GE_recordTime, LT_recordTime,
					EQ_action, EQ_bizStep, EQ_disposition, EQ_readPoint,
					WD_readPoint, EQ_bizLocation, WD_bizLocation,
					EQ_transformationID, MATCH_epc, MATCH_parentID,
					MATCH_inputEPC, MATCH_outputEPC, MATCH_anyEPC,
					MATCH_epcClass, MATCH_inputEPCClass, MATCH_outputEPCClass,
					MATCH_anyEPCClass, EQ_quantity, GT_quantity, GE_quantity,
					LT_quantity, LE_quantity, 
					EQ_eventID, EXISTS_errorDeclaration,GE_errorDeclarationTime,
					LT_errorDeclarationTime, EQ_errorReason, EQ_correctiveEventID,
					orderBy, orderDirection,
					eventCountLimit, maxEventCount, paramMap);
			
			//Configuration.logger.info("AggregationEvent after criteria list");
			List<AggregationEvent> aggregationEventList=criteria.list();
			EventToEventTypeConverter conv=new EventToEventTypeConverter();
			for(int i=0;(i<aggregationEventList.size())&&(eventCount<countLimit);i++,eventCount++){
				//System.out.println("in Aggregation count= "+ eventCount + "   Size of event = " + aggregationEventList.size() +"   limit= "+countLimit);
				AggregationEvent aggregationEvent=aggregationEventList.get(i);
				JAXBElement element = new JAXBElement(new QName(
						"AggregationEvent"), AggregationEventType.class,
						conv.convert(aggregationEvent));
				eventObjects.add(element);
			}
		}
		
		if (toGetObjectEvent == true) {
		// Aggregation Event Collection
		//	DBCollection collection = mongoOperation.getCollection("AggregationEvent");
			
			 
			 Criteria criteria=Configuration.mysqlOperationdaoQr.makeQueryCriteria("ObjectEvent",
					GE_eventTime, LT_eventTime, GE_recordTime, LT_recordTime,
					EQ_action, EQ_bizStep, EQ_disposition, EQ_readPoint,
					WD_readPoint, EQ_bizLocation, WD_bizLocation,
					EQ_transformationID, MATCH_epc, MATCH_parentID,
					MATCH_inputEPC, MATCH_outputEPC, MATCH_anyEPC,
					MATCH_epcClass, MATCH_inputEPCClass, MATCH_outputEPCClass,
					MATCH_anyEPCClass, EQ_quantity, GT_quantity, GE_quantity,
					LT_quantity, LE_quantity, 
					EQ_eventID, EXISTS_errorDeclaration,GE_errorDeclarationTime,
					LT_errorDeclarationTime, EQ_errorReason, EQ_correctiveEventID,
					orderBy, orderDirection,
					eventCountLimit, maxEventCount, paramMap);
			
			//Configuration.logger.info("ObjectEvent after criteria list");
			// Configuration.logger.info("Before Criteria listed propoerly");
			 
			List<ObjectEvent> objectEventList=criteria.list();
			
			//Configuration.logger.info("After Criteria listed propoerly");
			EventToEventTypeConverter conv=new EventToEventTypeConverter();
			for(int i=0;(i<objectEventList.size())&&(eventCount<countLimit);i++,eventCount++){
				ObjectEvent objectEvent=objectEventList.get(i);
				JAXBElement element = new JAXBElement(new QName(
						"ObjectEvent"), ObjectEventType.class,
						conv.convert(objectEvent));
				eventObjects.add(element);
			}
		}
		/*
		if (toGetObjectEvent == true) {

			 Criteria criteria=mysqlOperationdao.makeQueryCriteria("ObjectEvent",
					GE_eventTime, LT_eventTime, GE_recordTime, LT_recordTime,
					EQ_action, EQ_bizStep, EQ_disposition, EQ_readPoint,
					WD_readPoint, EQ_bizLocation, WD_bizLocation,
					EQ_transformationID, MATCH_epc, MATCH_parentID,
					MATCH_inputEPC, MATCH_outputEPC, MATCH_anyEPC,
					MATCH_epcClass, MATCH_inputEPCClass, MATCH_outputEPCClass,
					MATCH_anyEPCClass, EQ_quantity, GT_quantity, GE_quantity,
					LT_quantity, LE_quantity, orderBy, orderDirection,
					eventCountLimit, maxEventCount, paramMap);
			
			List<ObjectEvent> objectEventList=criteria.list();
			EventToEventTypeConverter conv=new EventToEventTypeConverter();
			for(int i=0;i<objectEventList.size()&&(eventCount<countLimit);i++,eventCount++){
				System.out.println("in object count= "+ eventCount);
				System.out.println("--------------------------------------------------------");
				ObjectEvent objectEvent=objectEventList.get(i);
				JAXBElement element = new JAXBElement(new QName(
						"ObjectEvent"), ObjectEventType.class,
						conv.convert(objectEvent));
				eventObjects.add(element);
			}
		}
		*/
		
		if (toGetQuantityEvent == true) {
			
			 
			 Criteria criteria=Configuration.mysqlOperationdaoQr.makeQueryCriteria("QuantityEvent",
					GE_eventTime, LT_eventTime, GE_recordTime, LT_recordTime,
					EQ_action, EQ_bizStep, EQ_disposition, EQ_readPoint,
					WD_readPoint, EQ_bizLocation, WD_bizLocation,
					EQ_transformationID, MATCH_epc, MATCH_parentID,
					MATCH_inputEPC, MATCH_outputEPC, MATCH_anyEPC,
					MATCH_epcClass, MATCH_inputEPCClass, MATCH_outputEPCClass,
					MATCH_anyEPCClass, EQ_quantity, GT_quantity, GE_quantity,
					LT_quantity, LE_quantity, 
					EQ_eventID, EXISTS_errorDeclaration,GE_errorDeclarationTime,
					LT_errorDeclarationTime, EQ_errorReason, EQ_correctiveEventID,
					orderBy, orderDirection,eventCountLimit, maxEventCount, paramMap);	
			
			List<QuantityEvent> quantityEventList=criteria.list();
			EventToEventTypeConverter conv=new EventToEventTypeConverter();
			for(int i=0;i<quantityEventList.size()&&(eventCount<countLimit);i++,eventCount++){
				System.out.println("in quantity count= "+ eventCount);
				QuantityEvent quantityEvent=quantityEventList.get(i);
				JAXBElement element = new JAXBElement(new QName(
						"QuantityEvent"), QuantityEventType.class,
						conv.convert(quantityEvent));
				eventObjects.add(element);
			} 
		}
		if (toGetTransactionEvent == true) {
			// Aggregation Event Collection
			//DBCollection collection = mongoOperation.getCollection("TransactionEvent");
			
			 
			 Criteria criteria=Configuration.mysqlOperationdaoQr.makeQueryCriteria("TransactionEvent",
					GE_eventTime, LT_eventTime, GE_recordTime, LT_recordTime,
					EQ_action, EQ_bizStep, EQ_disposition, EQ_readPoint,
					WD_readPoint, EQ_bizLocation, WD_bizLocation,
					EQ_transformationID, MATCH_epc, MATCH_parentID,
					MATCH_inputEPC, MATCH_outputEPC, MATCH_anyEPC,
					MATCH_epcClass, MATCH_inputEPCClass, MATCH_outputEPCClass,
					MATCH_anyEPCClass, EQ_quantity, GT_quantity, GE_quantity,
					LT_quantity, LE_quantity, 
					EQ_eventID, EXISTS_errorDeclaration,GE_errorDeclarationTime,
					LT_errorDeclarationTime, EQ_errorReason, EQ_correctiveEventID,
					orderBy, orderDirection,
					eventCountLimit, maxEventCount, paramMap);	
			
			List<TransactionEvent> transactionEventList=criteria.list();
			EventToEventTypeConverter conv=new EventToEventTypeConverter();
			for(int i=0;i<transactionEventList.size()&&(eventCount<countLimit);i++,eventCount++){
				System.out.println("in transaction count= "+ eventCount);
				TransactionEvent transactionEvent=transactionEventList.get(i);
				JAXBElement element = new JAXBElement(new QName(
						"TransactionEvent"), TransactionEventType.class,
						conv.convert(transactionEvent));
				eventObjects.add(element);
			} 
			
		}
		if (toGetTransformationEvent == true) {
			// Aggregation Event Collection
			//DBCollection collection = mongoOperation.getCollection("TransformationEvent");
			
			 
			 Criteria criteria=Configuration.mysqlOperationdaoQr.makeQueryCriteria("TransformationEvent",
					GE_eventTime, LT_eventTime, GE_recordTime, LT_recordTime,
					EQ_action, EQ_bizStep, EQ_disposition, EQ_readPoint,
					WD_readPoint, EQ_bizLocation, WD_bizLocation,
					EQ_transformationID, MATCH_epc, MATCH_parentID,
					MATCH_inputEPC, MATCH_outputEPC, MATCH_anyEPC,
					MATCH_epcClass, MATCH_inputEPCClass, MATCH_outputEPCClass,
					MATCH_anyEPCClass, EQ_quantity, GT_quantity, GE_quantity,
					LT_quantity, LE_quantity, 
					EQ_eventID, EXISTS_errorDeclaration,GE_errorDeclarationTime,
					LT_errorDeclarationTime, EQ_errorReason, EQ_correctiveEventID,
					orderBy, orderDirection,
					eventCountLimit, maxEventCount, paramMap);	
			
			
			List<TransformationEvent> transformationEventList=criteria.list();
			EventToEventTypeConverter conv=new EventToEventTypeConverter();
			for(int i=0;i<transformationEventList.size()&&(eventCount<countLimit);i++,eventCount++){
				System.out.println("in transformation count= "+ eventCount);
				TransformationEvent transformationEvent=transformationEventList.get(i);
				JAXBElement element = new JAXBElement(new QName(
						"TransformationEvent"), TransactionEventType.class,
						conv.convert(transformationEvent));
				eventObjects.add(element);
			}
		}

		// M44
		if (maxEventCount != null) {
			if (eventObjects.size() > Integer.parseInt(maxEventCount)) {
				//((AbstractApplicationContext) ctx).close();
				return makeErrorResult("Violate maxEventCount",
						QueryTooLargeException.class);
			}
		}
		//((AbstractApplicationContext) ctx).close();
		
		StringWriter sw = new StringWriter();
		JAXB.marshal(epcisQueryDocumentType, sw);
		return sw.toString();
	}

	@SuppressWarnings({  "unchecked" })
	public String pollMasterDataQuery(String queryName, String vocabularyName,
			boolean includeAttributes, boolean includeChildren,
			String attributeNames, String eQ_name, String wD_name,
			String hASATTR, String maxElementCount,Map<String, String> paramMap) {

		// Make Base Result Document
		EPCISQueryDocumentType epcisQueryDocumentType = makeBaseResultDocument(queryName);
		
//		ApplicationContext ctx=new ClassPathXmlApplicationContext(Configuration.DB);
//		QueryOprationBackend mysqlOperationdao=ctx.getBean
//				("queryOprationBackend", QueryOprationBackend.class);

		
		 
		 Criteria criteria=Configuration.mysqlOperationdaoQr.makeVocQueryCriteria(vocabularyName,
					includeAttributes, includeChildren, attributeNames, eQ_name,
					wD_name, hASATTR, maxElementCount,paramMap);
		 
		 

		 List<Vocabulary> vocabularyList=criteria.list();
		 /**
		  * If true, the results will include
		  * attribute names and values for
		  * matching vocabulary elements. If
		  * false, attribute names and values
		  * will not be included in the result.
		  */
		 List<Attribute> attribute=new ArrayList<Attribute>();
		 if(!includeAttributes){
			 for(int i=0; i<vocabularyList.size(); i++){
					for(int j=0;j<vocabularyList.get(i).getVocabularyElementList().getVocabularyElement().size();j++){
						vocabularyList.get(0).getVocabularyElementList().getVocabularyElement().get(0).setAttribute(attribute);
					}
				}
		 }
		 /**
		  * If true, the results will include the
		  * children list for matching 
		  * vocabulary elements. If false,
		  * children lists will not be included in the result.
		  */
		 if(!includeChildren){
			 for(int i=0; i<vocabularyList.size(); i++){
					for(int j=0;j<vocabularyList.get(i).getVocabularyElementList().getVocabularyElement().size();j++){
						vocabularyList.get(0).getVocabularyElementList().getVocabularyElement().get(0).setChildren(null);
					}
				}
		 }
		 
		QueryResultsBody qbt = epcisQueryDocumentType.getEPCISBody()
				.getQueryResults().getResultsBody();
		List<VocabularyType> vocabularyTypeList=new ArrayList<VocabularyType>();
		EventToEventTypeConverter conv=new EventToEventTypeConverter();
		for(int i=0;i<vocabularyList.size();i++){
			vocabularyTypeList.add(conv.convert(vocabularyList.get(i)));
		}
		VocabularyListType vlt = new VocabularyListType();
		vlt.setVocabulary(vocabularyTypeList);
		qbt.setVocabularyList(vlt);

//		((AbstractApplicationContext) ctx).close();

		// M47
		if (maxElementCount != null) {
			try {
				int maxElement = Integer.parseInt(maxElementCount);
				if (vocabularyTypeList.size() > maxElement) {
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

	System.out.println(" poll(String queryName, QueryParams queryParams)  started...");	
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
		String EQ_eventID = null; 
		Boolean EXISTS_errorDeclaration = null;
		String GE_errorDeclarationTime = null;
		String LT_errorDeclarationTime = null;
		String EQ_errorReason = null;
		String EQ_correctiveEventID = null;
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
				EQ_quantity, GT_quantity, GE_quantity, LT_quantity,LE_quantity,
				EQ_eventID, EXISTS_errorDeclaration,GE_errorDeclarationTime,
				LT_errorDeclarationTime, EQ_errorReason, EQ_correctiveEventID,
				orderBy, orderDirection, eventCountLimit,
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
			String EQ_eventID, Boolean EXISTS_errorDeclaration, String GE_errorDeclarationTime,
			String LT_errorDeclarationTime,String EQ_errorReason,String EQ_correctiveEventID,
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
					EQ_eventID, EXISTS_errorDeclaration,GE_errorDeclarationTime,
					LT_errorDeclarationTime, EQ_errorReason, EQ_correctiveEventID,
					orderBy, orderDirection, eventCountLimit, maxEventCount,
					paramMap);

		if (queryName.equals("SimpleMasterDataQuery"))
			return pollMasterDataQuery(queryName, vocabularyName,
					includeAttributes, includeChildren, attributeNames,
					EQ_name, WD_name, HASATTR, maxElementCount,paramMap);
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
			
			
			
			String EQ_eventID, Boolean EXISTS_errorDeclaration, String GE_errorDeclarationTime,
			String LT_errorDeclarationTime,String EQ_errorReason,String EQ_correctiveEventID,
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

	public EPCISQueryDocumentType makeBaseResultDocument(String queryName, String subscriptionID) {
		// Make Base Result Document
		EPCISQueryDocumentType epcisQueryDocumentType = new EPCISQueryDocumentType();
		EPCISQueryBodyType epcisBody = new EPCISQueryBodyType();
		epcisQueryDocumentType.setEPCISBody(epcisBody);
		QueryResults queryResults = new QueryResults();
		queryResults.setQueryName(queryName);
		epcisBody.setQueryResults(queryResults);
		QueryResultsBody queryResultsBody = new QueryResultsBody();
		queryResults.setResultsBody(queryResultsBody);
		if(subscriptionID != null )
			queryResults.setSubscriptionID(subscriptionID);
		
		EventListType eventListType = new EventListType();
		queryResultsBody.setEventList(eventListType);
		// Object instanceof JAXBElement
		List<Object> eventObjects = new ArrayList<Object>();
		eventListType
				.setObjectEventOrAggregationEventOrQuantityEvent(eventObjects);
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
			map.put("queryName", subscription.getPollParameters().getQueryName());
			map.put("subscriptionID", subscription.getSubscriptionID());
			map.put("dest", subscription.getDest());
			//map.put("cronExpression", subscription.getPollParameters().getCronExpression());
			if(subscription.getPollParameters() != null){
				
			}

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
			if (subscription.getPollParameters().getParams() != null )
				map.put("paramMap", subscription.getPollParameters().getParams());
			
			JobDetail job = newJob(MysqlSubscriptionTask.class)
					.withIdentity(subscription.getSubscriptionID(), subscription.getPollParameters().getQueryName()).setJobData(map)
					.storeDurably(false).build();

			Trigger trigger = newTrigger()
					.withIdentity(subscription.getSubscriptionID(), subscription.getPollParameters().getQueryName()).startNow()
					.withSchedule(cronSchedule(subscription.getSchedule())).build();
			
			// ClassPathXmlApplicationContext context = new
			// ClassPathXmlApplicationContext(
			// "classpath:QuartzConfig.xml");
			// Scheduler sched = (Scheduler) context
			// .getBean("schedulerFactoryBean");

			if (MysqlSubscription.sched.isStarted() != true)
				MysqlSubscription.sched.start();
			MysqlSubscription.sched.scheduleJob(job, trigger);
	//		Configuration.logger.log(Level.INFO, "Subscription ID: "
	//				+ subscription.getSubscriptionID()
	//				+ " is added to quartz scheduler. ");
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

	
	private boolean addScheduleToDB(String queryName, String subscriptionID,
			String dest, String cronExpression, boolean reportIfEmpty,
			String initialRecordTime, String eventType, String GE_eventTime,
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
		
		
		PollParameters pollparam= new PollParameters(queryName, eventType, GE_eventTime, LT_eventTime,
				GE_recordTime, LT_recordTime, EQ_action, EQ_bizStep, EQ_disposition,
				EQ_readPoint, WD_readPoint, EQ_bizLocation, WD_bizLocation,
				EQ_transformationID, MATCH_epc, MATCH_parentID, MATCH_inputEPC,
				MATCH_outputEPC, MATCH_anyEPC, MATCH_epcClass, MATCH_inputEPCClass,
				MATCH_outputEPCClass, MATCH_anyEPCClass, Integer.parseInt(EQ_quantity), Integer.parseInt(GT_quantity),
				Integer.parseInt(GE_quantity), Integer.parseInt(LT_quantity), Integer.parseInt(LE_quantity), "",
				Boolean.parseBoolean("false"), "", "",
				"",  "",  orderBy,  orderDirection,
				Integer.parseInt(eventCountLimit), Integer.parseInt(maxEventCount),  "", false,
				false,  "",  "",  "",  "",
				1200,  "", paramMap);
				
	
		
		Subscription st = new Subscription();
		st.setDest(dest);
		st.setSubscriptionID(subscriptionID);
		st.setPollParametrs(pollparam);
		st.setInitialRecordTime(initialRecordTime);
		st.setReportIfEmpty(reportIfEmpty);
		//st.setTrigger(trigger);
		
		
//		ApplicationContext ctx=new ClassPathXmlApplicationContext(Configuration.DB);
//		QueryOprationBackend mysqlOperationdao=ctx.getBean
//				("queryOprationBackend", QueryOprationBackend.class);

		// check existence 
		int existenceTest=Configuration.mysqlOperationdaoQr.CountSubscriptionType(subscriptionID);
		
		
		if (existenceTest != 0)
			return false;
		if (existenceTest == 0)
			Configuration.mysqlOperationdaoQr.save(st);
			

		//Configuration.logger.log(Level.INFO, "Subscription ID: "
		//		+ subscriptionID + " is added to DB. ");
		System.out.println( subscriptionID + " is added to DB. ");
		
//		((AbstractApplicationContext) ctx).close();
		return true;
	}


	public List<String> checking(){
		System.out.println("1 here");
//		ApplicationContext ctx=new ClassPathXmlApplicationContext(Configuration.DB);
//		QueryOprationBackend mysqlOperationdao=ctx.getBean
//				("queryOprationBackend", QueryOprationBackend.class);
		List<String> ids=Configuration.mysqlOperationdaoQr.select();
		
		
		return ids;
	}
	
	
	public List<String> checking2(){
		System.out.println("1 here");
//		ApplicationContext ctx=new ClassPathXmlApplicationContext(Configuration.DB);
//		QueryOprationBackend mysqlOperationdao=ctx.getBean
//				("queryOprationBackend", QueryOprationBackend.class);
		List<String> ids=Configuration.mysqlOperationdaoQr.findVocabilaryChildren("urn:epcglobal:epcis:vtype:BusinessLocation","urn:epc:id:sgln:0037000.%");//00729.0");
		
		
		return ids;//epc1
	}
	
	public List<String> checking3(){
		System.out.println("checking3 here");
//		ApplicationContext ctx=new ClassPathXmlApplicationContext(Configuration.DB);
//		QueryOprationBackend mysqlOperationdao=ctx.getBean
//				("queryOprationBackend", QueryOprationBackend.class);
		List<String> ids=new ArrayList<String>();
		Criteria cr=Configuration.mysqlOperationdaoQr.findepc("epc2");
				@SuppressWarnings("unchecked")
				List<AggregationEvent> aggList=cr.list();
				for(int i=0; i<aggList.size();i++){
					List<QuantityElement> quantityList=aggList.get(i).getExtension().getChildQuantityList().getQuantityElement();//.getInputQuantityList().getQuantityElement();
					for(int j=0; j<quantityList.size();j++){
						ids.add(quantityList.get(j).getEpcClass());
					}
				}
		
		
		return ids;//epc1
	}
	
	public List<Vocabulary> checking4(){
		System.out.println("checking4 here");
//		ApplicationContext ctx=new ClassPathXmlApplicationContext(Configuration.DB);
//		QueryOprationBackend mysqlOperationdao=ctx.getBean
//				("queryOprationBackend", QueryOprationBackend.class);
		
		List<Vocabulary> vocabulary=Configuration.mysqlOperationdaoQr.checkTransformationEvent();
		return vocabulary;
		
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

	private void removeScheduleFromDB(QueryOprationBackend queryOprationBackend,
			SubscriptionType subscription) {
		
		queryOprationBackend.remove(subscription);
		Configuration.logger.log(Level.INFO, "Subscription ID: " + subscription	+ " is removed from DB");
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
