package org.oliot.epcis.service.query.mongodb;

import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.apache.log4j.Level;
import org.bson.BsonArray;
import org.bson.BsonBoolean;
import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonObjectId;
import org.bson.BsonString;
import org.bson.BsonType;
import org.bson.BsonValue;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.epcis.converter.mongodb.AggregationEventReadConverter;
import org.oliot.epcis.converter.mongodb.MasterDataReadConverter;
import org.oliot.epcis.converter.mongodb.MongoWriterUtil;
import org.oliot.epcis.converter.mongodb.ObjectEventReadConverter;
import org.oliot.epcis.converter.mongodb.QuantityEventReadConverter;
import org.oliot.epcis.converter.mongodb.TransactionEventReadConverter;
import org.oliot.epcis.converter.mongodb.TransformationEventReadConverter;
import org.oliot.epcis.security.OAuthUtil;
import org.oliot.epcis.service.subscription.MongoSubscription;
import org.oliot.epcis.service.subscription.MongoSubscriptionTask;
import org.oliot.epcis.service.subscription.TriggerEngine;
import org.oliot.model.epcis.AggregationEventType;
import org.oliot.model.epcis.AttributeType;
import org.oliot.model.epcis.EPCISEventListExtensionType;
import org.oliot.model.epcis.EPCISQueryBodyType;
import org.oliot.model.epcis.EPCISQueryDocumentType;
import org.oliot.model.epcis.EventListType;
import org.oliot.model.epcis.InvalidURIException;
import org.oliot.model.epcis.ObjectEventType;
import org.oliot.model.epcis.PollParameters;
import org.oliot.model.epcis.QuantityEventType;
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
import org.springframework.web.bind.annotation.PathVariable;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import static org.oliot.epcis.service.query.mongodb.MongoQueryUtil.*;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.JobKey.jobKey;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.TriggerKey.triggerKey;

/**
 * Copyright (C) 2014-2016 Jaewook Byun
 *
 * This project is part of Oliot open source (http://oliot.org). Oliot EPCIS
 * v1.2.x is Java Web Service complying with Electronic Product Code Information
 * Service (EPCIS) v1.2.
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

	public String subscribeEventQuery(SubscriptionType s, String userID, List<String> friendList)
			throws QueryParameterException, SubscriptionControlsException {

		// M27 - query params' constraint
		// M39 - query params' constraint
		String reason = checkConstraintSimpleEventQuery(s.getPollParameters());
		if (reason != null) {
			throw new QueryParameterException();
			// return makeErrorResult(reason, QueryParameterException.class);
		}

		// Existing subscription Check
		MongoCollection<BsonDocument> collection = Configuration.mongoDatabase.getCollection("Subscription",
				BsonDocument.class);
		BsonDocument exist = collection.find(new BsonDocument("subscriptionID", new BsonString(s.getSubscriptionID())))
				.first();
		if (exist != null) {
			return "SubscriptionID : " + s.getSubscriptionID() + " is already subscribed. ";
		}

		// cron Example
		// 0/10 * * * * ? : every 10 second

		if (s.getSchedule() != null && s.getTrigger() == null) {
			try {
				cronSchedule(s.getSchedule());
				addScheduleToQuartz(s);

			} catch (RuntimeException e) {
				throw new SubscriptionControlsException();
				// return makeErrorResult(e.toString(),
				// SubscriptionControlsException.class);
			}
		} else if (s.getSchedule() == null && s.getTrigger() != null) {
			// Add Trigger with Query
			TriggerEngine.addTriggerSubscription(s.getSubscriptionID(), s);
		} else {
			throw new SubscriptionControlsException();
			// return makeErrorResult("One of schedule, trigger should be null",
			// SubscriptionControlsException.class);
		}

		// Manage Subscription Persistently
		addScheduleToDB(s, userID, friendList);

		String retString = "SubscriptionID : " + s.getSubscriptionID() + " is successfully triggered. ";
		return retString;
	}

	public String subscribe(SubscriptionType s, String userID, List<String> friendList) throws QueryParameterException,
			SubscriptionControlsException, InvalidURIException, SubscribeNotPermittedException {
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
		if (s.getPollParameters().getQueryName().equals("SimpleMasterDataQuery")) {
			throw new SubscribeNotPermittedException();
			// return makeErrorResult("SimpleMasterDataQuery is not available in
			// subscription method", SubscribeNotPermittedException.class);
		}

		String retString = "";
		if (s.getPollParameters().getQueryName().equals("SimpleEventQuery")) {
			retString = subscribeEventQuery(s, userID, friendList);
		}

		return retString;
	}

	// Soap Query Adaptor
	public void subscribe(String queryName, QueryParams params, URI dest, SubscriptionControls controls,
			String subscriptionID) throws QueryParameterException, SubscriptionControlsException, InvalidURIException,
			SubscribeNotPermittedException {
		PollParameters p = new PollParameters(queryName, params);

		// Subscription Control Processing
		/*
		 * QuerySchedule: (Optional) Defines the periodic schedule on which the query is
		 * to be executed. See Section 8.2.5.3. Exactly one of schedule or trigger is
		 * required; if both are specified or both are omitted, the implementation SHALL
		 * raise a SubscriptionControls- Exception..
		 */
		QuerySchedule querySchedule = controls.getSchedule();
		String schedule = null;
		if (querySchedule != null) {
			String sec = querySchedule.getSecond();
			if (sec == null)
				sec = "*";
			String min = querySchedule.getMinute();
			if (min == null)
				min = "*";
			String hour = querySchedule.getHour();
			if (hour == null)
				hour = "*";
			String dayOfMonth = querySchedule.getDayOfMonth();
			if (dayOfMonth == null)
				dayOfMonth = "*";
			String month = querySchedule.getMonth();
			String dayOfWeek = querySchedule.getDayOfWeek();

			// either month or dayOfWeek should be ?
			// two are not null -> dayOfWeek = ?
			// one of two exists -> non-exist = ?
			// two are null -> month=* , dayOfWeek=?

			if (month == null && dayOfWeek == null) {
				month = "*";
				dayOfWeek = "?";
			} else if (month != null && dayOfWeek == null) {
				dayOfWeek = "?";
			} else if (month == null && dayOfWeek != null) {
				month = "?";
			} else {
				dayOfWeek = "?";
			}

			schedule = sec + " " + min + " " + hour + " " + dayOfMonth + " " + month + " " + dayOfWeek;
		}

		/*
		 * InitialRecordTime: (Optional) Specifies a time used to constrain what events
		 * are considered when processing the query when it is executed for the first
		 * time. See Section 8.2.5.2. If omitted, defaults to the time at which the
		 * subscription is created.
		 */
		XMLGregorianCalendar initialRecordTime = controls.getInitialRecordTime();
		String initialRecordTimeStr = null;
		if (initialRecordTime != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
			Date initialRecordDate = initialRecordTime.toGregorianCalendar().getTime();
			initialRecordTimeStr = sdf.format(initialRecordDate);
		}

		/*
		 * reportIfEmpty: If true, a QueryResults instance is always sent to the
		 * subscriber when the query is executed. If false, a QueryResults instance is
		 * sent to the subscriber only when the results are non-empty.
		 */
		Boolean reportIfEmpty = controls.isReportIfEmpty();

		SubscriptionType subscription = new SubscriptionType(subscriptionID, dest.toString(), schedule,
				controls.getTrigger(), initialRecordTimeStr, reportIfEmpty, p);

		subscribe(subscription, null, null);

	}

	public void unsubscribe(String subscriptionID) {

		MongoCollection<BsonDocument> collection = Configuration.mongoDatabase.getCollection("Subscription",
				BsonDocument.class);

		// Its size should be 0 or 1
		BsonDocument s = collection
				.findOneAndDelete(new BsonDocument("subscriptionID", new BsonString(subscriptionID)));

		if (s != null) {
			SubscriptionType subscription = new SubscriptionType(s);
			if (subscription.getSchedule() != null && subscription.getTrigger() == null) {
				// Remove from current Quartz
				removeScheduleFromQuartz(subscription);
			} else {
				TriggerEngine.removeTriggerSubscription(subscription.getSubscriptionID());
			}
		}
	}

	public String getSubscriptionIDsREST(@PathVariable String queryName) {

		JSONArray retArray = new JSONArray();

		MongoCollection<BsonDocument> collection = Configuration.mongoDatabase.getCollection("Subscription",
				BsonDocument.class);

		Iterator<BsonDocument> subIterator = collection
				.find(new BsonDocument("pollParameters.queryName", new BsonString(queryName)), BsonDocument.class)
				.iterator();

		while (subIterator.hasNext()) {
			BsonDocument subscription = subIterator.next();
			retArray.put(subscription.getString("subscriptionID").getValue());
		}

		return retArray.toString(1);
	}

	public List<String> getSubscriptionIDs(String queryName) {

		List<String> retList = new ArrayList<String>();

		MongoCollection<BsonDocument> collection = Configuration.mongoDatabase.getCollection("Subscription",
				BsonDocument.class);

		Iterator<BsonDocument> subIterator = collection
				.find(new BsonDocument("pollParameters.queryName", new BsonString(queryName)), BsonDocument.class)
				.iterator();

		while (subIterator.hasNext()) {
			BsonDocument subscription = subIterator.next();
			retList.add(subscription.getString("subscriptionID").asString().getValue());
		}

		return retList;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String pollEventQuery(PollParameters p, String userID, List<String> friendList, String subscriptionID)
			throws QueryParameterException, QueryTooLargeException {

		// M27 - query params' constraint
		// M39 - query params' constraint
		String reason = checkConstraintSimpleEventQuery(p);

		if (reason != null) {
			throw new QueryParameterException();
			// return makeErrorResult(reason, QueryParameterException.class);
		}

		// Make Base Result Document
		EPCISQueryDocumentType epcisQueryDocumentType = null;

		if (p.getFormat() == null || p.getFormat().equals("XML")) {
			epcisQueryDocumentType = makeBaseResultDocument(p.getQueryName(), subscriptionID);
		} else if (p.getFormat().equals("JSON")) {
			// Do Nothing
		} else {
			throw new QueryParameterException();
			// return makeErrorResult("format param should be one of XML or
			// JSON", QueryParameterException.class);
		}

		// Prepare container which query results are included
		// eventObjects : Container which all the query results (events) will be
		// contained
		List<Object> eventObjects = null;
		if (p.getFormat() == null || p.getFormat().equals("XML")) {
			eventObjects = epcisQueryDocumentType.getEPCISBody().getQueryResults().getResultsBody().getEventList()
					.getObjectEventOrAggregationEventOrQuantityEvent();
		} else {
			// foramt == JSON -> Do Nothing
		}

		// Event Collection
		MongoCollection<BsonDocument> collection = Configuration.mongoDatabase.getCollection("EventData",
				BsonDocument.class);
		// Queries
		BsonArray queryList = makeQueryObjects(p, userID, friendList);
		// Merge All the queries with $and
		BsonDocument baseQuery = new BsonDocument();
		FindIterable<BsonDocument> cursor;
		if (queryList.isEmpty() == false) {
			BsonArray aggreQueryList = new BsonArray();
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
		cursor = makeProjectSortedLimitedCursor(cursor, p.getParams(), p.getOrderBy(), p.getOrderDirection(),
				p.getEventCountLimit());

		JSONArray retArray = new JSONArray();

		MongoCursor<BsonDocument> slCursor = cursor.iterator();

		ArrayList<BsonDocument> dbObjectSet = new ArrayList<BsonDocument>();

		while (slCursor.hasNext()) {
			BsonDocument dbObject = slCursor.next();

			String eventTypeInDoc = dbObject.getString("eventType").getValue();

			if (OAuthUtil.isAccessible(userID, friendList, dbObject) == false) {
				continue;
			}

			if (!isPostFilterPassed(eventTypeInDoc, dbObject, p.getParams()))
				continue;

			dbObjectSet.add(dbObject);
		}

		if (p.getFormat() == null || p.getFormat().equals("XML")) {

			List<JAXBElement> converted = null;

			if (p.getOrderBy() == null) {
				converted = dbObjectSet.parallelStream().map(obj -> {
					String eventTypeInDoc = obj.getString("eventType").getValue();
					if (eventTypeInDoc.equals("AggregationEvent")) {
						AggregationEventReadConverter con = new AggregationEventReadConverter();
						JAXBElement element = new JAXBElement(new QName("AggregationEvent"), AggregationEventType.class,
								con.convert(obj));
						return element;
					} else if (eventTypeInDoc.equals("ObjectEvent")) {
						ObjectEventReadConverter con = new ObjectEventReadConverter();
						JAXBElement element = new JAXBElement(new QName("ObjectEvent"), ObjectEventType.class,
								con.convert(obj));
						return element;
					} else if (eventTypeInDoc.equals("QuantityEvent")) {
						QuantityEventReadConverter con = new QuantityEventReadConverter();
						JAXBElement element = new JAXBElement(new QName("QuantityEvent"), QuantityEventType.class,
								con.convert(obj));
						return element;
					} else if (eventTypeInDoc.equals("TransactionEvent")) {
						TransactionEventReadConverter con = new TransactionEventReadConverter();
						JAXBElement element = new JAXBElement(new QName("TransactionEvent"), TransactionEventType.class,
								con.convert(obj));
						return element;
					} else if (eventTypeInDoc.equals("TransformationEvent")) {
						TransformationEventReadConverter con = new TransformationEventReadConverter();
						TransformationEventType transformationEvent = con.convert(obj);
						EPCISEventListExtensionType extension = new EPCISEventListExtensionType();
						extension.setTransformationEvent(transformationEvent);
						JAXBElement element = new JAXBElement(new QName("extension"), EPCISEventListExtensionType.class,
								extension);
						return element;
					} else
						return null;
				}).filter(obj -> obj != null).collect(Collectors.toList());
			} else {
				converted = dbObjectSet.stream().map(obj -> {
					String eventTypeInDoc = obj.getString("eventType").getValue();
					if (eventTypeInDoc.equals("AggregationEvent")) {
						AggregationEventReadConverter con = new AggregationEventReadConverter();
						JAXBElement element = new JAXBElement(new QName("AggregationEvent"), AggregationEventType.class,
								con.convert(obj));
						return element;
					} else if (eventTypeInDoc.equals("ObjectEvent")) {
						ObjectEventReadConverter con = new ObjectEventReadConverter();
						JAXBElement element = new JAXBElement(new QName("ObjectEvent"), ObjectEventType.class,
								con.convert(obj));
						return element;
					} else if (eventTypeInDoc.equals("QuantityEvent")) {
						QuantityEventReadConverter con = new QuantityEventReadConverter();
						JAXBElement element = new JAXBElement(new QName("QuantityEvent"), QuantityEventType.class,
								con.convert(obj));
						return element;
					} else if (eventTypeInDoc.equals("TransactionEvent")) {
						TransactionEventReadConverter con = new TransactionEventReadConverter();
						JAXBElement element = new JAXBElement(new QName("TransactionEvent"), TransactionEventType.class,
								con.convert(obj));
						return element;
					} else if (eventTypeInDoc.equals("TransformationEvent")) {
						TransformationEventReadConverter con = new TransformationEventReadConverter();
						TransformationEventType transformationEvent = con.convert(obj);
						EPCISEventListExtensionType extension = new EPCISEventListExtensionType();
						extension.setTransformationEvent(transformationEvent);
						JAXBElement element = new JAXBElement(new QName("extension"), EPCISEventListExtensionType.class,
								extension);
						return element;
					} else
						return null;
				}).filter(obj -> obj != null).collect(Collectors.toList());
			}
			eventObjects.addAll(converted);
		} else {

			List<JSONObject> objList = null;

			if (p.getOrderBy() == null) {
				objList = dbObjectSet.parallelStream().map(obj -> {
					return new JSONObject(obj.toJson());
				}).collect(Collectors.toList());
			} else {
				objList = dbObjectSet.stream().map(obj -> {
					return new JSONObject(obj.toJson());
				}).collect(Collectors.toList());
			}

			retArray = new JSONArray(objList);
		}

		/*
		 * before Parallel conversion while (slCursor.hasNext()) { BsonDocument dbObject
		 * = slCursor.next();
		 * 
		 * String eventTypeInDoc = dbObject.getString("eventType").getValue();
		 * 
		 * if (OAuthUtil.isAccessible(userID, friendList, dbObject) == false) {
		 * continue; }
		 * 
		 * if (!isPostFilterPassed(eventTypeInDoc, dbObject, p.getParams())) continue;
		 * 
		 * if (p.getFormat() == null || p.getFormat().equals("XML")) { if
		 * (eventTypeInDoc.equals("AggregationEvent")) { AggregationEventReadConverter
		 * con = new AggregationEventReadConverter(); JAXBElement element = new
		 * JAXBElement(new QName("AggregationEvent"), AggregationEventType.class,
		 * con.convert(dbObject)); eventObjects.add(element); } else if
		 * (eventTypeInDoc.equals("ObjectEvent")) { ObjectEventReadConverter con = new
		 * ObjectEventReadConverter(); JAXBElement element = new JAXBElement(new
		 * QName("ObjectEvent"), ObjectEventType.class, con.convert(dbObject));
		 * eventObjects.add(element); } else if (eventTypeInDoc.equals("QuantityEvent"))
		 * { QuantityEventReadConverter con = new QuantityEventReadConverter();
		 * JAXBElement element = new JAXBElement(new QName("QuantityEvent"),
		 * QuantityEventType.class, con.convert(dbObject)); eventObjects.add(element); }
		 * else if (eventTypeInDoc.equals("TransactionEvent")) {
		 * TransactionEventReadConverter con = new TransactionEventReadConverter();
		 * JAXBElement element = new JAXBElement(new QName("TransactionEvent"),
		 * TransactionEventType.class, con.convert(dbObject));
		 * eventObjects.add(element); } else if
		 * (eventTypeInDoc.equals("TransformationEvent")) {
		 * TransformationEventReadConverter con = new
		 * TransformationEventReadConverter(); TransformationEventType
		 * transformationEvent = con.convert(dbObject); EPCISEventListExtensionType
		 * extension = new EPCISEventListExtensionType();
		 * extension.setTransformationEvent(transformationEvent); JAXBElement element =
		 * new JAXBElement(new QName("extension"), EPCISEventListExtensionType.class,
		 * extension); eventObjects.add(element); } } else { retArray.put(new
		 * JSONObject(dbObject.toJson())); } }
		 */

		// M44
		if (p.getMaxEventCount() != null) {
			if (p.getFormat() == null || p.getFormat().equals("XML")) {
				if (eventObjects.size() > p.getMaxEventCount()) {
					throw new QueryTooLargeException(eventObjects.size(), p.getMaxEventCount(), "SimpleEventQuery",
							null);
					// return makeErrorResult("Violate maxEventCount",
					// QueryTooLargeException.class);
				}
			} else {
				if (retArray.length() > p.getMaxEventCount()) {
					throw new QueryTooLargeException(retArray.length(), p.getMaxEventCount(), "SimpleEventQuery", null);
					// return makeErrorResult("Violate maxEventCount",
					// QueryTooLargeException.class);
				}
			}
		}

		if (p.getFormat() == null || p.getFormat().equals("XML")) {
			StringWriter sw = new StringWriter();
			JAXB.marshal(epcisQueryDocumentType, sw);

			String result = sw.toString();
			// String simple = result.replaceAll("(<\\?[^<]*\\?>)?", "")
			// . /* remove preamble */
			// replaceAll("xmlns.*?(\"|\').*?(\"|\')",
			// "")
			// .replaceAll("(<)(\\w+:)(.*?>)",
			// "$1$3")
			// .replaceAll("(</)(\\w+:)(.*?>)",
			// "$1$3");

			// try {
			// DocumentBuilderFactory dbFactory =
			// DocumentBuilderFactory.newInstance();
			// DocumentBuilder dBuilder;
			// dBuilder = dbFactory.newDocumentBuilder();
			// Document doc =
			// dBuilder.parse(IOUtils.toInputStream(sw.toString(), "UTF-8"));
			// Transformer tf =
			// TransformerFactory.newInstance().newTransformer();
			// tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			// tf.setOutputProperty(OutputKeys.INDENT, "yes");
			// Writer out = new StringWriter();
			// tf.transform(new DOMSource(doc), new StreamResult(out));
			// System.out.println(out.toString());
			//
			// } catch (ParserConfigurationException e) {
			//
			// e.printStackTrace();
			// } catch (SAXException e) {
			//
			// e.printStackTrace();
			// } catch (IOException e) {
			//
			// e.printStackTrace();
			// } catch (TransformerException e) {
			//
			// e.printStackTrace();
			// }

			return result;
		} else {
			return retArray.toString(1);
		}
	}

	public String pollMasterDataQuery(PollParameters p, String userID, List<String> friendList)
			throws QueryTooLargeException, QueryParameterException {

		// Required Field Check
		if (p.getIncludeAttributes() == null || p.getIncludeChildren() == null) {
			throw new QueryParameterException();
			// return makeErrorResult("SimpleMasterDataQuery's Required Field:
			// includeAttributes, includeChildren",
			// QueryTooLargeException.class);
		}

		// Make Base Result Document
		EPCISQueryDocumentType epcisQueryDocumentType = null;
		JSONArray retArray = new JSONArray();

		if (p.getFormat() == null || p.getFormat().equals("XML")) {
			epcisQueryDocumentType = makeBaseResultDocument(p.getQueryName(), null);
		} else if (p.getFormat().equals("JSON")) {
			// Do Nothing
		} else {
			throw new QueryParameterException();
			// return makeErrorResult("format param should be one of XML or
			// JSON", QueryParameterException.class);
		}

		MongoCollection<BsonDocument> collection = Configuration.mongoDatabase.getCollection("MasterData",
				BsonDocument.class);

		// Make Query
		BsonArray queryList = makeMasterQueryObjects(p);

		// Merge All the queries with $and
		BsonDocument baseQuery = new BsonDocument();
		FindIterable<BsonDocument> cursor;
		if (queryList.isEmpty() == false) {
			BsonArray aggreQueryList = new BsonArray();
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

		MongoCursor<BsonDocument> slCursor = cursor.iterator();
		while (slCursor.hasNext()) {
			BsonDocument dbObject = slCursor.next();

			if (p.getFormat() == null || p.getFormat().equals("XML")) {
				MasterDataReadConverter con = new MasterDataReadConverter();
				VocabularyType vt = con.convert(dbObject);

				if (vt.getVocabularyElementList() != null) {
					if (vt.getVocabularyElementList().getVocabularyElement() != null) {
						List<VocabularyElementType> vetList = vt.getVocabularyElementList().getVocabularyElement();
						for (int i = 0; i < vetList.size(); i++) {
							VocabularyElementType vet = vetList.get(i);
							if (p.getIncludeAttributes() == false) {
								vet.setAttribute(null);
							} else if (p.getIncludeAttributes() == true && p.getAttributeNames() != null) {
								String[] attrArr = p.getAttributeNames().split(",");
								Set<String> attrSet = new HashSet<String>();
								for (int j = 0; j < attrArr.length; j++) {
									attrSet.add(attrArr[j].trim());
								}

								List<AttributeType> atList = vet.getAttribute();
								List<AttributeType> filteredList = new ArrayList<AttributeType>();
								for (int j = 0; j < atList.size(); j++) {
									if (attrSet.contains(atList.get(j).getId())) {
										filteredList.add(atList.get(j));
									}
								}
								vet.setAttribute(filteredList);
							}

							if (p.getIncludeChildren() == false) {
								vet.setChildren(null);
							}
						}
					}
				}
				vList.add(vt);
			} else {
				dbObject.remove("_id");
				if (p.getIncludeAttributes() == false) {
					dbObject.remove("attributes");
				} else if (p.getIncludeAttributes() == true && p.getAttributeNames() != null) {
					String[] attrArr = p.getAttributeNames().split(",");
					Set<String> attrSet = new HashSet<String>();
					for (int j = 0; j < attrArr.length; j++) {
						attrSet.add(attrArr[j].trim());
					}
					BsonDocument attrObject = dbObject.get("attributes").asDocument();
					BsonDocument newObject = new BsonDocument();
					if (attrObject != null) {
						Iterator<String> attrKeys = attrObject.keySet().iterator();
						while (attrKeys.hasNext()) {
							String attrKey = attrKeys.next();
							if (attrSet.contains(attrKey)) {
								newObject.put(attrKey, attrObject.get(attrKey));
							}
						}
					}
					dbObject.put("attributes", newObject);

				}
				if (p.getIncludeChildren() == false) {
					dbObject.remove("children");
				}
				retArray.put(new JSONObject(dbObject.toJson()));
			}

		}

		if (p.getFormat() == null || p.getFormat().equals("XML")) {
			QueryResultsBody qbt = epcisQueryDocumentType.getEPCISBody().getQueryResults().getResultsBody();

			VocabularyListType vlt = new VocabularyListType();
			vlt.setVocabulary(vList);
			qbt.setVocabularyList(vlt);
		}

		// M47
		if (p.getMaxElementCount() != null) {
			try {
				if (p.getFormat() == null || p.getFormat().equals("XML")) {
					if (vList.size() > p.getMaxElementCount()) {
						throw new QueryTooLargeException(vList.size(), p.getMaxElementCount(), "SimpleMasterDataQuery",
								null);
						// return makeErrorResult("Too Large Master Data
						// result", QueryTooLargeException.class);
					}
				} else {
					if (retArray.length() > p.getMaxElementCount()) {
						throw new QueryTooLargeException(retArray.length(), p.getMaxElementCount(),
								"SimpleMasterDataQuery", null);
						// return makeErrorResult("Too Large Master Data
						// result", QueryTooLargeException.class);
					}
				}
			} catch (NumberFormatException e) {

			}
		}
		if (p.getFormat() == null || p.getFormat().equals("XML")) {
			StringWriter sw = new StringWriter();
			JAXB.marshal(epcisQueryDocumentType, sw);
			return sw.toString();
		} else {
			return retArray.toString(1);
		}
	}

	// Soap Service Adaptor
	public String poll(String queryName, QueryParams queryParams)
			throws QueryParameterException, QueryTooLargeException {
		PollParameters p = new PollParameters(queryName, queryParams);
		return poll(p, null, null, null);
	}

	public String poll(PollParameters p, String userID, List<String> friendList, String subscriptionID)
			throws QueryParameterException, QueryTooLargeException {

		// M24
		if (p.getQueryName() == null) {
			// It is not possible, automatically filtered by URI param
			throw new QueryParameterException();
			// return makeErrorResult("queryName is mandatory field in poll
			// method", QueryParameterException.class);
		}

		if (p.getQueryName().equals("SimpleEventQuery"))
			return pollEventQuery(p, userID, friendList, subscriptionID);

		if (p.getQueryName().equals("SimpleMasterDataQuery"))
			return pollMasterDataQuery(p, userID, friendList);
		return "";
	}

	static BsonDateTime getTimeMillis(String standardDateString) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
			GregorianCalendar eventTimeCalendar = new GregorianCalendar();
			eventTimeCalendar.setTime(sdf.parse(standardDateString));
			return new BsonDateTime(eventTimeCalendar.getTimeInMillis());
		} catch (ParseException e) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
				GregorianCalendar eventTimeCalendar = new GregorianCalendar();
				eventTimeCalendar.setTime(sdf.parse(standardDateString));
				return new BsonDateTime(eventTimeCalendar.getTimeInMillis());
			} catch (ParseException e1) {
				Configuration.logger.log(Level.ERROR, e1.toString());
			}
		}
		// Never Happened
		return null;
	}

	boolean isExtraParameter(String paramName) {

		if (paramName.contains("eventTime"))
			return false;
		if (paramName.contains("recordTime"))
			return false;
		if (paramName.contains("errorDeclarationTime"))
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
		if (paramName.contains("ILMD"))
			return false;
		if (paramName.contains("eventID"))
			return false;
		if (paramName.contains("errorReason"))
			return false;
		if (paramName.contains("correctiveEventID"))
			return false;
		if (paramName.contains("errorDeclaration"))
			return false;
		if (paramName.contains("ERROR_DECLARATION"))
			return false;
		if (paramName.contains("INNER"))
			return false;

		return true;
	}

	public String checkConstraintSimpleEventQuery(PollParameters p) throws QueryParameterException {

		// M27
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
			if (p.getGE_eventTime() != null)
				sdf.parse(p.getGE_eventTime());
			if (p.getLT_eventTime() != null)
				sdf.parse(p.getLT_eventTime());
			if (p.getGE_recordTime() != null)
				sdf.parse(p.getGE_recordTime());
			if (p.getLT_recordTime() != null)
				sdf.parse(p.getLT_recordTime());
		} catch (ParseException e) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
				if (p.getGE_eventTime() != null)
					sdf.parse(p.getGE_eventTime());
				if (p.getLT_eventTime() != null)
					sdf.parse(p.getLT_eventTime());
				if (p.getGE_recordTime() != null)
					sdf.parse(p.getGE_recordTime());
				if (p.getLT_recordTime() != null)
					sdf.parse(p.getLT_recordTime());
			} catch (ParseException e1) {
				throw new QueryParameterException();
				// return makeErrorResult(e.toString(),
				// QueryParameterException.class);
			}
		}

		// M27
		if (p.getOrderBy() != null) {
			/*
			 * if (!orderBy.equals("eventTime") && !orderBy.equals("recordTime")) { return
			 * makeErrorResult( "orderBy should be eventTime or recordTime",
			 * QueryParameterException.class); }
			 */
			if (p.getOrderDirection() != null) {
				if (!p.getOrderDirection().equals("ASC") && !p.getOrderDirection().equals("DESC")) {
					throw new QueryParameterException();
					// return makeErrorResult("orderDirection should be ASC or
					// DESC", QueryParameterException.class);
				}
			}
		}

		// M27
		if (p.getEventCountLimit() != null) {
			if (p.getEventCountLimit() <= 0) {
				throw new QueryParameterException();
				// return makeErrorResult("eventCount should be natural number",
				// QueryParameterException.class);
			}
		}

		// M27
		if (p.getMaxEventCount() != null) {
			if (p.getMaxEventCount() <= 0) {
				throw new QueryParameterException();
				// return makeErrorResult("maxEventCount should be natural
				// number", QueryParameterException.class);
			}
		}

		// M39
		if (p.getEQ_action() != null) {

			String[] actionArr = p.getEQ_action().split(",");
			for (String action : actionArr) {
				action = action.trim();
				if (action.equals(""))
					continue;
				if (!action.equals("ADD") && !action.equals("OBSERVE") && !action.equals("DELETE")) {
					throw new QueryParameterException();
					// return makeErrorResult("EQ_action: ADD | OBSERVE |
					// DELETE", QueryParameterException.class);
				}
			}
		}

		// M42
		if (p.getEventCountLimit() != null && p.getMaxEventCount() != null) {
			throw new QueryParameterException();
			// return makeErrorResult("One of eventCountLimit and maxEventCount
			// should be omitted", QueryParameterException.class);
		}
		return null;
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
		if (subscriptionID != null)
			queryResults.setSubscriptionID(subscriptionID);
		EventListType eventListType = new EventListType();
		queryResultsBody.setEventList(eventListType);
		// Object instanceof JAXBElement
		List<Object> eventObjects = new ArrayList<Object>();
		eventListType.setObjectEventOrAggregationEventOrQuantityEvent(eventObjects);
		return epcisQueryDocumentType;
	}

	@SuppressWarnings({ "rawtypes", "unused" })
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

	private FindIterable<BsonDocument> makeProjectSortedLimitedCursor(FindIterable<BsonDocument> cursor,
			Map<String, String> extParams, String orderBy, String orderDirection, Integer eventCountLimit) {

		Iterator<Entry<String, String>> extParamIter = extParams.entrySet().iterator();
		BsonDocument projection = new BsonDocument();
		BsonBoolean projValue = null;
		while (extParamIter.hasNext()) {
			Entry<String, String> entry = extParamIter.next();
			String paramKey = entry.getKey();
			String paramValue = entry.getValue();
			if (paramKey.startsWith("PROJECTION_")) {
				if (projValue == null) {
					if (paramValue != null && (paramValue.equals("true") || paramValue.equals("true^boolean"))) {
						projValue = BsonBoolean.TRUE;
					} else {
						projValue = BsonBoolean.FALSE;
					}
				}
				String projKey = paramKey.substring(11, paramKey.length());
				// eventType is prohibited for projection
				if (!projKey.equals("eventType"))
					projection.put(projKey, projValue);
			}
		}
		if (!projection.isEmpty()) {
			if (projValue.getValue() == true)
				projection.append("eventType", BsonBoolean.TRUE);
			cursor.projection(projection);
		}

		/**
		 * orderBy : If specified, names a single field that will be used to order the
		 * results. The orderDirection field specifies whether the ordering is in
		 * ascending sequence or descending sequence. Events included in the result that
		 * lack the specified field altogether may occur in any position within the
		 * result event list. The value of this parameter SHALL be one of: eventTime,
		 * recordTime, or the fully qualified name of an extension field whose type is
		 * Int, Float, Time, or String. A fully qualified fieldname is constructed as
		 * for the EQ_fieldname parameter. In the case of a field of type String, the
		 * ordering SHOULD be in lexicographic order based on the Unicode encoding of
		 * the strings, or in some other collating sequence appropriate to the locale.
		 * If omitted, no order is specified. The implementation MAY order the results
		 * in any order it chooses, and that order MAY differ even when the same query
		 * is executed twice on the same data. (In EPCIS 1.0, the value quantity was
		 * also permitted, but its use is deprecated in EPCIS 1.1.)
		 * 
		 * orderDirection : If specified and orderBy is also specified, specifies
		 * whether the results are ordered in ascending or descending sequence according
		 * to the key specified by orderBy. The value of this parameter must be one of
		 * ASC (for ascending order) or DESC (for descending order); if not, the
		 * implementation SHALL raise a QueryParameterException. If omitted, defaults to
		 * DESC.
		 */

		// Update Query with ORDER and LIMIT
		if (orderBy != null) {
			orderBy = MongoWriterUtil.encodeMongoObjectKey(orderBy);
			// Currently only eventTime, recordTime can be used
			if (orderBy.trim().equals("eventTime")) {
				if (orderDirection != null) {
					if (orderDirection.trim().equals("ASC")) {
						cursor = cursor.sort(new BsonDocument("eventTime", new BsonInt32(1)));
					} else if (orderDirection.trim().equals("DESC")) {
						cursor = cursor.sort(new BsonDocument("eventTime", new BsonInt32(-1)));
					}
				} else {
					cursor = cursor.sort(new BsonDocument("eventTime", new BsonInt32(-1)));
				}
			} else if (orderBy.trim().equals("recordTime")) {
				if (orderDirection != null) {
					if (orderDirection.trim().equals("ASC")) {
						cursor = cursor.sort(new BsonDocument("recordTime", new BsonInt32(1)));
					} else if (orderDirection.trim().equals("DESC")) {
						cursor = cursor.sort(new BsonDocument("recordTime", new BsonInt32(-1)));
					}
				} else {
					cursor = cursor.sort(new BsonDocument("recordTime", new BsonInt32(-1)));
				}
			} else {
				if (orderDirection != null) {
					if (orderDirection.trim().equals("ASC")) {
						cursor = cursor.sort(new BsonDocument("any." + orderBy, new BsonInt32(1)));
					} else if (orderDirection.trim().equals("DESC")) {
						cursor = cursor.sort(new BsonDocument("any." + orderBy, new BsonInt32(-1)));
					}
				} else {
					cursor = cursor.sort(new BsonDocument("any." + orderBy, new BsonInt32(-1)));
				}
			}
		}

		/**
		 * eventCountLimit: If specified, the results will only include the first N
		 * events that match the other criteria, where N is the value of this parameter.
		 * The ordering specified by the orderBy and orderDirection parameters determine
		 * the meaning of “first” for this purpose. If omitted, all events matching the
		 * specified criteria will be included in the results. This parameter and
		 * maxEventCount are mutually exclusive; if both are specified, a
		 * QueryParameterException SHALL be raised. This parameter may only be used when
		 * orderBy is specified; if orderBy is omitted and eventCountLimit is specified,
		 * a QueryParameterException SHALL be raised. This parameter differs from
		 * maxEventCount in that this parameter limits the amount of data returned,
		 * whereas maxEventCount causes an exception to be thrown if the limit is
		 * exceeded.
		 */
		if (eventCountLimit != null) {
			try {
				cursor = cursor.limit(eventCountLimit);
			} catch (NumberFormatException nfe) {
				Configuration.logger.log(Level.ERROR, nfe.toString());
			}
		}

		return cursor;
	}

	private BsonArray makeQueryObjects(PollParameters p, String userID, List<String> friendList) {

		BsonArray queryList = new BsonArray();

		/**
		 * eventType : If specified, the result will only include events whose type
		 * matches one of the types specified in the parameter value. Each element of
		 * the parameter value may be one of the following strings: ObjectEvent,
		 * AggregationEvent, QuantityEvent, TransactionEvent, or TransformationEvent. An
		 * element of the parameter value may also be the name of an extension event
		 * type. If omitted, all event types will be considered for inclusion in the
		 * result.
		 * 
		 * List of String CSV REGEX
		 */
		if (p.getEventType() != null) {
			BsonArray paramArray = getParamBsonArray(p.getEventType());
			BsonDocument queryObject = getQueryObject(new String[] { "eventType" }, paramArray);
			if (queryObject != null) {
				queryList.add(queryObject);
			}
		}

		/**
		 * GE_eventTime: If specified, only events with eventTime greater than or equal
		 * to the specified value will be included in the result. If omitted, events are
		 * included regardless of their eventTime (unless constrained by the LT_
		 * eventTime parameter). Example: 2014-08-11T19:57:59.717+09:00 SimpleDateFormat
		 * sdf = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		 * eventTime.setTime(sdf.parse(timeString)); e.g. 1988-07-04T12:08:56.235-07:00
		 * 
		 * Verified
		 */
		if (p.getGE_eventTime() != null) {
			BsonDateTime geBsonDateTime = getTimeMillis(p.getGE_eventTime());
			BsonDocument query = new BsonDocument();
			query.put("eventTime", new BsonDocument("$gte", geBsonDateTime));
			queryList.add(query);
		}
		/**
		 * LT_eventTime: If specified, only events with eventTime less than the
		 * specified value will be included in the result. If omitted, events are
		 * included regardless of their eventTime (unless constrained by the GE_
		 * eventTime parameter).
		 * 
		 * Verified
		 */
		if (p.getLT_eventTime() != null) {
			BsonDateTime ltBsonDateTime = getTimeMillis(p.getLT_eventTime());
			BsonDocument query = new BsonDocument();
			query.put("eventTime", new BsonDocument("$lt", ltBsonDateTime));
			queryList.add(query);
		}
		/**
		 * GE_recordTime: If provided, only events with recordTime greater than or equal
		 * to the specified value will be returned. The automatic limitation based on
		 * event record time (Section 8.2.5.2) may implicitly provide a constraint
		 * similar to this parameter. If omitted, events are included regardless of
		 * their recordTime , other than automatic limitation based on event record time
		 * (Section 8.2.5.2).
		 * 
		 * Verified
		 */
		if (p.getGE_recordTime() != null) {
			BsonDateTime geBsonDateTime = getTimeMillis(p.getGE_recordTime());
			BsonDocument query = new BsonDocument();
			query.put("recordTime", new BsonDocument("$gte", geBsonDateTime));
			queryList.add(query);
		}
		/**
		 * LE_recordTime: If provided, only events with recordTime less than the
		 * specified value will be returned. If omitted, events are included regardless
		 * of their recordTime (unless constrained by the GE_ recordTime parameter or
		 * the automatic limitation based on event record time).
		 * 
		 * Verified
		 */
		if (p.getLT_recordTime() != null) {
			BsonDateTime ltBsonDateTime = getTimeMillis(p.getLT_recordTime());
			BsonDocument query = new BsonDocument();
			query.put("recordTime", new BsonDocument("$lt", ltBsonDateTime));
			queryList.add(query);
		}

		/**
		 * GE_errorDeclaration Time: If this parameter is specified, the result will
		 * only include events that (a) contain an ErrorDeclaration ; and where (b) the
		 * value of the errorDeclarationTime field is greater than or equal to the
		 * specified value. If this parameter is omitted, events are returned regardless
		 * of whether they contain an ErrorDeclaration or what the value of the
		 * errorDeclarationTime field is.
		 */
		if (p.getGE_errorDeclarationTime() != null) {
			BsonDateTime geBsonDateTime = getTimeMillis(p.getGE_errorDeclarationTime());
			BsonDocument query = new BsonDocument();
			query.put("errorDeclaration.declarationTime", new BsonDocument("$gte", geBsonDateTime));
			queryList.add(query);
		}

		/**
		 * LT_errorDeclaration Time: contain an ErrorDeclaration ; and where (b) the
		 * value of the errorDeclarationTime field is less than to the specified value.
		 * If this parameter is omitted, events are returned regardless of whether they
		 * contain an ErrorDeclaration or what the value of the errorDeclarationTime
		 * field is.
		 */
		if (p.getLT_errorDeclarationTime() != null) {
			BsonDateTime ltBsonDateTime = getTimeMillis(p.getLT_errorDeclarationTime());
			BsonDocument query = new BsonDocument();
			query.put("errorDeclaration.declarationTime", new BsonDocument("$lt", ltBsonDateTime));
			queryList.add(query);
		}

		/**
		 * EQ_action: If specified, the result will only include events that (a) have an
		 * action field; and where (b) the value of the action field matches one of the
		 * specified values. The elements of the value of this parameter each must be
		 * one of the strings ADD , OBSERVE , or DELETE ; if not, the implementation
		 * SHALL raise a QueryParameterException . If omitted, events are included
		 * regardless of their action field.
		 * 
		 * OR semantic
		 * 
		 * Verified
		 */
		if (p.getEQ_action() != null) {
			// Constrained already checked
			BsonArray paramArray = getParamBsonArray(p.getEQ_action());
			BsonDocument queryObject = getQueryObject(new String[] { "action" }, paramArray);
			if (queryObject != null) {
				queryList.add(queryObject);
			}
		}
		/**
		 * EQ_bizStep: If specified, the result will only include events that (a) have a
		 * non-null bizStep field; and where (b) the value of the bizStep field matches
		 * one of the specified values. If this parameter is omitted, events are
		 * returned regardless of the value of the bizStep field or whether the bizStep
		 * field exists at all.
		 * 
		 * OR semantic Regex supported
		 * 
		 * Verified
		 */
		if (p.getEQ_bizStep() != null) {
			BsonArray paramArray = getParamBsonArray(p.getEQ_bizStep());
			BsonDocument queryObject = getQueryObject(new String[] { "bizStep" }, paramArray);
			if (queryObject != null) {
				queryList.add(queryObject);
			}
		}
		/**
		 * Like the EQ_ bizStep parameter, but for the disposition field.
		 * 
		 * OR semantic Regex Supported
		 * 
		 * Verified
		 */
		if (p.getEQ_disposition() != null) {
			BsonArray paramArray = getParamBsonArray(p.getEQ_disposition());
			BsonDocument queryObject = getQueryObject(new String[] { "disposition" }, paramArray);
			if (queryObject != null) {
				queryList.add(queryObject);
			}
		}
		/**
		 * EQ_readPoint: If specified, the result will only include events that (a) have
		 * a non-null readPoint field; and where (b) the value of the readPoint field
		 * matches one of the specified values. If this parameter and WD_ readPoint are
		 * both omitted, events are returned regardless of the value of the readPoint
		 * field or whether the readPoint field exists at all.
		 * 
		 * OR semantic Regex supported
		 * 
		 */
		if (p.getEQ_readPoint() != null) {
			BsonArray paramArray = getParamBsonArray(p.getEQ_readPoint());
			BsonDocument queryObject = getQueryObject(new String[] { "readPoint.id" }, paramArray);
			if (queryObject != null) {
				queryList.add(queryObject);
			}
		}

		/**
		 * WD_readPoint: If specified, the result will only include events that (a) have
		 * a non-null readPoint field; and where (b) the value of the readPoint field
		 * matches one of the specified values, or is a direct or indirect descendant of
		 * one of the specified values. The meaning of “direct or indirect descendant”
		 * is specified by master data, as described in Section 6.5. (WD is an
		 * abbreviation for “with descendants.”) If this parameter and EQ_readPoint are
		 * both omitted, events are returned regardless of the value of the readPoint
		 * field or whether the readPoint field exists at all.
		 * 
		 * OR semantic Regex Supported
		 * 
		 */

		if (p.getWD_readPoint() != null) {
			BsonArray paramArray = getWDParamBsonArray(p.getWD_readPoint());
			BsonDocument queryObject = getQueryObject(new String[] { "readPoint.id" }, paramArray);
			if (queryObject != null) {
				queryList.add(queryObject);
			}
		}

		/**
		 * EQ_bizLocation: Like the EQ_ readPoint parameter, but for the bizLocation
		 * field.
		 * 
		 * OR semantic Regex Supported
		 * 
		 */
		if (p.getEQ_bizLocation() != null) {
			BsonArray paramArray = getParamBsonArray(p.getEQ_bizLocation());
			BsonDocument queryObject = getQueryObject(new String[] { "bizLocation.id" }, paramArray);
			if (queryObject != null) {
				queryList.add(queryObject);
			}
		}

		/**
		 * WD_bizLocation: Like the WD_readPoint parameter, but for the bizLocation
		 * field.
		 * 
		 * OR semantic Regex Supported
		 * 
		 */

		if (p.getWD_bizLocation() != null) {
			BsonArray paramArray = getWDParamBsonArray(p.getWD_bizLocation());
			BsonDocument queryObject = getQueryObject(new String[] { "bizLocation.id" }, paramArray);
			if (queryObject != null) {
				queryList.add(queryObject);
			}
		}

		/**
		 * EQ_transformationID: If this parameter is specified, the result will only
		 * include events that (a) have a transformationID field (that is,
		 * TransformationEvents or extension event type that extend
		 * TransformationEvent); and where (b) the transformationID field is equal to
		 * one of the values specified in this parameter.
		 * 
		 * OR semantic Regex Supported
		 * 
		 */
		if (p.getEQ_transformationID() != null) {
			BsonArray paramArray = getParamBsonArray(p.getEQ_transformationID());
			BsonDocument queryObject = getQueryObject(new String[] { "transformationID" }, paramArray);
			if (queryObject != null) {
				queryList.add(queryObject);
			}
		}

		/**
		 * MATCH_epc: If this parameter is specified, the result will only include
		 * events that (a) have an epcList or a childEPCs field (that is, ObjectEvent,
		 * AggregationEvent, TransactionEvent or extension event types that extend one
		 * of those three); and where (b) one of the EPCs listed in the epcList or
		 * childEPCs field (depending on event type) matches one of the EPC patterns or
		 * URIs specified in this parameter, where the meaning of “matches” is as
		 * specified in Section 8.2.7.1.1. If this parameter is omitted, events are
		 * included regardless of their epcList or childEPCs field or whether the
		 * epcList or childEPCs field exists.
		 * 
		 * 
		 */
		if (p.getMATCH_epc() != null) {
			BsonArray paramArray = getParamBsonArray(p.getMATCH_epc());
			BsonDocument queryObject = getMatchQueryObject(new String[] { "epcList.epc", "childEPCs.epc" }, paramArray);
			if (queryObject != null) {
				queryList.add(queryObject);
			}
		}

		/**
		 * MATCH_parentID: Like MATCH_epc, but matches the parentID field of
		 * AggregationEvent, the parentID field of TransactionEvent, and extension event
		 * types that extend either AggregationEvent or TransactionEvent. The meaning of
		 * “matches” is as specified in Section 8.2.7.1.1.
		 */
		if (p.getMATCH_parentID() != null) {
			BsonArray paramArray = getParamBsonArray(p.getMATCH_parentID());
			BsonDocument queryObject = getMatchQueryObject(new String[] { "parentID" }, paramArray);
			if (queryObject != null) {
				queryList.add(queryObject);
			}
		}

		/**
		 * MATCH_inputEPC: If this parameter is specified, the result will only include
		 * events that (a) have an inputEPCList (that is, TransformationEvent or an
		 * extension event type that extends TransformationEvent); and where (b) one of
		 * the EPCs listed in the inputEPCList field matches one of the EPC patterns or
		 * URIs specified in this parameter. The meaning of “matches” is as specified in
		 * Section 8.2.7.1.1. If this parameter is omitted, events are included
		 * regardless of their inputEPCList field or whether the inputEPCList field
		 * exists.
		 */
		if (p.getMATCH_inputEPC() != null) {
			BsonArray paramArray = getParamBsonArray(p.getMATCH_inputEPC());
			BsonDocument queryObject = getMatchQueryObject(new String[] { "inputEPCList.epc" }, paramArray);
			if (queryObject != null) {
				queryList.add(queryObject);
			}
		}

		/**
		 * MATCH_outputEPC: If this parameter is specified, the result will only include
		 * events that (a) have an inputEPCList (that is, TransformationEvent or an
		 * extension event type that extends TransformationEvent); and where (b) one of
		 * the EPCs listed in the inputEPCList field matches one of the EPC patterns or
		 * URIs specified in this parameter. The meaning of “matches” is as specified in
		 * Section 8.2.7.1.1. If this parameter is omitted, events are included
		 * regardless of their inputEPCList field or whether the inputEPCList field
		 * exists.
		 */
		if (p.getMATCH_outputEPC() != null) {
			BsonArray paramArray = getParamBsonArray(p.getMATCH_outputEPC());
			BsonDocument queryObject = getMatchQueryObject(new String[] { "outputEPCList.epc" }, paramArray);
			if (queryObject != null) {
				queryList.add(queryObject);
			}
		}

		/**
		 * MATCH_anyEPC: If this parameter is specified, the result will only include
		 * events that (a) have an epcList field, a childEPCs field, a parentID field,
		 * an inputEPCList field, or an outputEPCList field (that is, ObjectEvent,
		 * AggregationEvent, TransactionEvent, TransformationEvent, or extension event
		 * types that extend one of those four); and where (b) the parentID field or one
		 * of the EPCs listed in the epcList, childEPCs, inputEPCList, or outputEPCList
		 * field (depending on event type) matches one of the EPC patterns or URIs
		 * specified in this parameter. The meaning of “matches” is as specified in
		 * Section 8.2.7.1.1.
		 */

		if (p.getMATCH_anyEPC() != null) {
			BsonArray paramArray = getParamBsonArray(p.getMATCH_anyEPC());
			BsonDocument queryObject = getMatchQueryObject(new String[] { "epcList.epc", "childEPCs.epc",
					"inputEPCList.epc", "outputEPCList.epc", "parentID" }, paramArray);
			if (queryObject != null) {
				queryList.add(queryObject);
			}
		}

		/**
		 * MATCH_epcClass: If this parameter is specified, the result will only include
		 * events that (a) have a quantityList or a childQuantityList field (that is,
		 * ObjectEvent, AggregationEvent, TransactionEvent or extension event types that
		 * extend one of those three); and where (b) one of the EPC classes listed in
		 * the quantityList or childQuantityList field (depending on event type) matches
		 * one of the EPC patterns or URIs specified in this parameter. The result will
		 * also include QuantityEvents whose epcClass field matches one of the EPC
		 * patterns or URIs specified in this parameter. The meaning of “matches” is as
		 * specified in Section 8.2.7.1.1.
		 */
		if (p.getMATCH_epcClass() != null) {

			BsonArray paramArray = getParamBsonArray(p.getMATCH_epcClass());
			BsonDocument queryObject = getMatchQueryObject(
					new String[] { "extension.quantityList.epcClass", "extension.childQuantityList.epcClass" },
					paramArray);
			if (queryObject != null) {
				queryList.add(queryObject);
			}
		}

		/**
		 * MATCH_inputEPCClass: If this parameter is specified, the result will only
		 * include events that (a) have an inputQuantityList field (that is,
		 * TransformationEvent or extension event types that extend it); and where (b)
		 * one of the EPC classes listed in the inputQuantityList field (depending on
		 * event type) matches one of the EPC patterns or URIs specified in this
		 * parameter. The meaning of “matches” is as specified in Section 8.2.7.1.1.
		 */
		if (p.getMATCH_inputEPCClass() != null) {
			BsonArray paramArray = getParamBsonArray(p.getMATCH_inputEPCClass());
			BsonDocument queryObject = getMatchQueryObject(new String[] { "inputQuantityList.epcClass" }, paramArray);
			if (queryObject != null) {
				queryList.add(queryObject);
			}
		}

		/**
		 * MATCH_outputEPCClass: If this parameter is specified, the result will only
		 * include events that (a) have an outputQuantityList field (that is,
		 * TransformationEvent or extension event types that extend it); and where (b)
		 * one of the EPC classes listed in the outputQuantityList field (depending on
		 * event type) matches one of the EPC patterns or URIs specified in this
		 * parameter. The meaning of “matches” is as specified in Section 8.2.7.1.1.
		 */

		if (p.getMATCH_outputEPCClass() != null) {
			BsonArray paramArray = getParamBsonArray(p.getMATCH_outputEPCClass());
			BsonDocument queryObject = getMatchQueryObject(new String[] { "outputQuantityList.epcClass" }, paramArray);
			if (queryObject != null) {
				queryList.add(queryObject);
			}
		}

		/**
		 * MATCH_anyEPCClass: If this parameter is specified, the result will only
		 * include events that (a) have a quantityList, childQuantityList,
		 * inputQuantityList, or outputQuantityList field (that is, ObjectEvent,
		 * AggregationEvent, TransactionEvent, TransformationEvent, or extension event
		 * types that extend one of those four); and where (b) one of the EPC classes
		 * listed in any of those fields matches one of the EPC patterns or URIs
		 * specified in this parameter. The result will also include QuantityEvents
		 * whose epcClass field matches one of the EPC patterns or URIs specified in
		 * this parameter. The meaning of “matches” is as specified in Section
		 * 8.2.7.1.1.
		 */
		if (p.getMATCH_anyEPCClass() != null) {
			BsonArray paramArray = getParamBsonArray(p.getMATCH_anyEPCClass());
			BsonDocument queryObject = getMatchQueryObject(
					new String[] { "extension.quantityList.epcClass", "extension.childQuantityList.epcClass",
							"inputQuantityList.epcClass", "outputQuantityList.epcClass" },
					paramArray);
			if (queryObject != null) {
				queryList.add(queryObject);
			}
		}

		/**
		 * (DEPCRECATED in EPCIS 1.1) EQ_quantity; GT_quantity; GE_quantity;
		 * LT_quantity; LE_quantity
		 **/

		/**
		 * EQ_eventID : If this parameter is specified, the result will only include
		 * events that (a) have a non-null eventID field; and where (b) the eventID
		 * field is equal to one of the values specified in this parameter. If this
		 * parameter is omitted, events are returned regardless of the value of the
		 * eventID field or whether the eventID field exists at all.
		 * 
		 * List of String
		 * 
		 */
		if (p.getEQ_eventID() != null) {

			BsonArray orQueryArray = new BsonArray();
			BsonArray paramArray = getParamBsonArray(p.getEQ_eventID());
			BsonDocument queryObject = getQueryObject(new String[] { "eventID" }, paramArray);
			if (queryObject != null) {
				orQueryArray.add(queryObject);
			}
			BsonArray objectIDParamArray = new BsonArray();
			for (int i = 0; i < paramArray.size(); i++) {
				BsonValue paramValue = paramArray.get(i);
				if (paramValue instanceof BsonString) {
					try {
						objectIDParamArray.add(new BsonObjectId(new ObjectId(paramValue.asString().getValue())));
					} catch (IllegalArgumentException e) {
						Configuration.logger.debug("Non MongoDB ObjectID: " + e.toString());
					}
				}
			}
			BsonDocument objectIDQueryObject = getQueryObject(new String[] { "_id" }, objectIDParamArray);
			if (objectIDQueryObject != null) {
				orQueryArray.add(objectIDQueryObject);
			}
			if (orQueryArray.size() != 0) {
				BsonDocument orQueryObject = new BsonDocument();
				orQueryObject.put("$or", orQueryArray);
				queryList.add(orQueryObject);
			}

		}

		/**
		 * EQ_errorReason: If this parameter is specified, the result will only include
		 * events that (a) contain an ErrorDeclaration ; and where (b) the error
		 * declaration contains a non-null reason field; and where (c) the reason field
		 * is equal to one of the values specified in this parameter. If this parameter
		 * is omitted, events are returned regardless of the they contain an
		 * ErrorDeclaration or what the value of the reason field is.
		 */

		if (p.getEQ_errorReason() != null) {
			BsonArray paramArray = getParamBsonArray(p.getEQ_errorReason());
			BsonDocument queryObject = getQueryObject(new String[] { "errorDeclaration.reason" }, paramArray);
			if (queryObject != null) {
				queryList.add(queryObject);
			}
		}

		/**
		 * EQ_correctiveEventID: If this parameter is specified, the result will only
		 * include events that (a) contain an ErrorDeclaration ; and where (b) one of
		 * the elements of the correctiveEventIDs list is equal to one of the values
		 * specified in this parameter. If this parameter is omitted, events are
		 * returned regardless of the they contain an ErrorDeclaration or the contents
		 * of the correctiveEventIDs list.
		 */

		if (p.getEQ_correctiveEventID() != null) {
			BsonArray paramArray = getParamBsonArray(p.getEQ_correctiveEventID());
			BsonDocument queryObject = getQueryObject(new String[] { "errorDeclaration.correctiveEventIDs" },
					paramArray);
			if (queryObject != null) {
				queryList.add(queryObject);
			}
		}

		/**
		 * EXISTS_errorDeclaration: If this parameter is specified, the result will only
		 * include events that contain an ErrorDeclaration . If this parameter is
		 * omitted, events are returned regardless of whether they contain an
		 * ErrorDeclaration .
		 */

		if (p.getEXISTS_errorDeclaration() != null) {

			Boolean isExist = Boolean.parseBoolean(p.getEXISTS_errorDeclaration().toString());
			BsonBoolean isExistBson = new BsonBoolean(isExist);
			BsonDocument query = getExistsQueryObject("errorDeclaration", null, isExistBson);
			if (query != null)
				queryList.add(query);
		}

		if (p.getParams() != null) {
			Iterator<String> paramIter = p.getParams().keySet().iterator();
			while (paramIter.hasNext()) {
				String paramName = paramIter.next();
				String paramValues = p.getParams().get(paramName);

				// db.EventData.createIndex({"any.http://ns．example．com/epcis#point":
				// "2dsphere" });
				if (paramName.contains("NEAR_")) {
					String type = paramName.substring(5, paramName.length());
					type = MongoWriterUtil.encodeMongoObjectKey(type);
					BsonDocument query = getNearQueryObject(type, paramValues);
					if (query != null)
						queryList.add(query);
				}

				/**
				 * EQ_bizTransaction_type: This is not a single parameter, but a family of
				 * parameters. If a parameter of this form is specified, the result will only
				 * include events that (a) include a bizTransactionList; (b) where the business
				 * transaction list includes an entry whose type subfield is equal to type
				 * extracted from the name of this parameter; and (c) where the bizTransaction
				 * subfield of that entry is equal to one of the values specified in this
				 * parameter.
				 */
				if (paramName.contains("EQ_bizTransaction_")) {
					String type = paramName.substring(18, paramName.length());
					type = MongoWriterUtil.encodeMongoObjectKey(type);
					BsonDocument query = getFamilyQueryObject(type, new String[] { "bizTransactionList" }, paramValues);
					if (query != null)
						queryList.add(query);
				}

				/**
				 * EQ_source_type: This is not a single parameter, but a family of parameters.
				 * If a parameter of this form is specified, the result will only include events
				 * that (a) include a sourceList; (b) where the source list includes an entry
				 * whose type subfield is equal to type extracted from the name of this
				 * parameter; and (c) where the source subfield of that entry is equal to one of
				 * the values specified in this parameter.
				 */

				if (paramName.contains("EQ_source_")) {
					String type = paramName.substring(10, paramName.length());
					type = MongoWriterUtil.encodeMongoObjectKey(type);
					/*
					 * if (eventType.equals("AggregationEvent") || eventType.equals("ObjectEvent")
					 * || eventType.equals("TransactionEvent")) { BsonDocument query =
					 * getFamilyQueryObject(type, "extension.sourceList", paramValues); if (query !=
					 * null) queryList.add(query); } if (eventType.equals("TransformationEvent")) {
					 * BsonDocument query = getFamilyQueryObject(type, "sourceList", paramValues);
					 * if (query != null) queryList.add(query); }
					 */
					/*
					 * if (eventType.equals("AggregationEvent") || eventType.equals("ObjectEvent")
					 * || eventType.equals("TransactionEvent")) {
					 * 
					 * } if (eventType.equals("TransformationEvent")) { BsonDocument query =
					 * getFamilyQueryObject(type, "sourceList", paramValues); if (query != null)
					 * queryList.add(query); }
					 */
					BsonDocument query = getFamilyQueryObject(type,
							new String[] { "extension.sourceList", "sourceList" }, paramValues);
					if (query != null)
						queryList.add(query);
				}

				/**
				 * EQ_destination_type: This is not a single parameter, but a family of
				 * parameters. If a parameter of this form is specified, the result will only
				 * include events that (a) include a destinationList; (b) where the destination
				 * list includes an entry whose type subfield is equal to type extracted from
				 * the name of this parameter; and (c) where the destination subfield of that
				 * entry is equal to one of the values specified in this parameter.
				 */
				if (paramName.contains("EQ_destination_")) {
					String type = paramName.substring(15, paramName.length());
					type = MongoWriterUtil.encodeMongoObjectKey(type);
					/*
					 * if (eventType.equals("AggregationEvent") || eventType.equals("ObjectEvent")
					 * || eventType.equals("TransactionEvent")) { BsonDocument query =
					 * getFamilyQueryObject(type, "extension.destinationList", paramValues); if
					 * (query != null) queryList.add(query); } if
					 * (eventType.equals("TransformationEvent")) { BsonDocument query =
					 * getFamilyQueryObject(type, "destinationList", paramValues); if (query !=
					 * null) queryList.add(query); }
					 */
					BsonDocument query = getFamilyQueryObject(type,
							new String[] { "extension.destinationList", "destinationList" }, paramValues);
					if (query != null)
						queryList.add(query);
				}

				/**
				 * EQ_ILMD_field: Analogous to EQ_fieldname , but matches events whose ILMD area
				 * (Section 7.3.6) contains a top-level field having the specified fieldname
				 * whose value matches one of the specified values. “Top level” means that the
				 * matching ILMD element must be an immediate child of the <ilmd> element, not
				 * an element nested within such an element. See EQ_INNER_ILMD_fieldname for
				 * querying inner extension elements.
				 */

				if (paramName.startsWith("EQ_ILMD_")) {
					String type = paramName.substring(8, paramName.length());
					type = MongoWriterUtil.encodeMongoObjectKey(type);
					BsonArray paramArray = getParamBsonArray(paramValues);
					BsonDocument queryObject = getQueryObject(
							new String[] { "extension.ilmd.any." + type, "ilmd.any." + type }, paramArray);
					if (queryObject != null) {
						queryList.add(queryObject);
					}
				}

				/**
				 * GT|GE|LT|LE_ILMD_field: Analogous to EQ_fieldname , GT_fieldname ,
				 * GE_fieldname , GE_fieldname , LT_fieldname , and LE_fieldname , respectively,
				 * but matches events whose ILMD area (Section 7.3.6) contains a field having
				 * the specified fieldname whose integer, float, or time value matches the
				 * specified value according to the specified relational operator.
				 */

				if (paramName.startsWith("GT_ILMD_") || paramName.startsWith("GE_ILMD_")
						|| paramName.startsWith("LT_ILMD_") || paramName.startsWith("LE_ILMD_")) {
					String type = paramName.substring(8, paramName.length());
					type = MongoWriterUtil.encodeMongoObjectKey(type);

					if (paramName.startsWith("GT_")) {
						BsonDocument query = getCompExtensionQueryObject(type,
								new String[] { "extension.ilmd.any." + type, "ilmd.any." + type }, paramValues, "GT");
						if (query != null)
							queryList.add(query);
					}
					if (paramName.startsWith("GE_")) {
						BsonDocument query = getCompExtensionQueryObject(type,
								new String[] { "extension.ilmd.any." + type, "ilmd.any." + type }, paramValues, "GE");
						if (query != null)
							queryList.add(query);
					}
					if (paramName.startsWith("LT_")) {
						BsonDocument query = getCompExtensionQueryObject(type,
								new String[] { "extension.ilmd.any." + type, "ilmd.any." + type }, paramValues, "LT");
						if (query != null)
							queryList.add(query);
					}
					if (paramName.startsWith("LE_")) {
						BsonDocument query = getCompExtensionQueryObject(type,
								new String[] { "extension.ilmd.any." + type, "ilmd.any." + type }, paramValues, "LE");
						if (query != null)
							queryList.add(query);
					}
				}

				/**
				 * EXISTS_ILMD_fieldname: Like EXISTS_fieldname as described above, but events
				 * that have a non-empty field named fieldname in the ILMD area (Section 7.3.6).
				 * Fieldname is constructed as for EQ_ILMD_fieldname . Note that the value for
				 * this query parameter is ignored.
				 */
				if (paramName.startsWith("EXISTS_ILMD_")) {
					/*
					 * if (p.getEventType().equals("ObjectEvent")) { String field =
					 * paramName.substring(12, paramName.length()); field =
					 * MongoWriterUtil.encodeMongoObjectKey(field); Boolean isExist =
					 * Boolean.parseBoolean(paramValues); BsonBoolean isExistBson = new
					 * BsonBoolean(isExist); BsonDocument query =
					 * getExistsQueryObject("extension.ilmd", field, isExistBson); if (query !=
					 * null) queryList.add(query); } else if
					 * (p.getEventType().equals("TransformationEvent")) { String field =
					 * paramName.substring(12, paramName.length()); field =
					 * MongoWriterUtil.encodeMongoObjectKey(field); Boolean isExist =
					 * Boolean.parseBoolean(paramValues); BsonBoolean isExistBson = new
					 * BsonBoolean(isExist); BsonDocument query = getExistsQueryObject("ilmd",
					 * field, isExistBson); if (query != null) queryList.add(query); }
					 */

					String field = paramName.substring(12, paramName.length());
					field = MongoWriterUtil.encodeMongoObjectKey(field);
					Boolean isExist = Boolean.parseBoolean(paramValues);
					BsonBoolean isExistBson = new BsonBoolean(isExist);
					BsonDocument query = getExistsQueryObject(new String[] { "extension.ilmd.any", "ilmd.any" }, field,
							isExistBson);
					if (query != null)
						queryList.add(query);
				}

				/**
				 * EQ_ERROR_DECLARATION_Fieldname : Analogous to EQ_fieldname , but matches
				 * events containing an ErrorDeclaration and where the ErrorDeclaration contains
				 * a field having the specified fieldname whose value matches one of the
				 * specified values.
				 * 
				 * List of String
				 * 
				 */

				if (paramName.startsWith("EQ_ERROR_DECLARATION_")) {
					String type = paramName.substring(21, paramName.length());
					type = MongoWriterUtil.encodeMongoObjectKey(type);

					BsonArray paramArray = getParamBsonArray(paramValues);
					BsonDocument queryObject = getQueryObject(new String[] { "errorDeclaration.any." + type },
							paramArray);
					if (queryObject != null) {
						queryList.add(queryObject);
					}
				}

				/**
				 * Analogous to EQ_fieldname , GT_fieldname , GE_fieldname , GE_fieldname ,
				 * LT_fieldname , and LE_fieldname , respectively, but matches events containing
				 * an ErrorDeclaration and where the ErrorDeclaration contains a field having
				 * the specified fieldname whose integer, float, or time value matches the
				 * specified value according to the specified relational operator.
				 */

				if (paramName.startsWith("GT_ERROR_DECLARATION_") || paramName.startsWith("GE_ERROR_DECLARATION_")
						|| paramName.startsWith("LT_ERROR_DECLARATION_")
						|| paramName.startsWith("LE_ERROR_DECLARATION_")) {
					String type = paramName.substring(21, paramName.length());
					type = MongoWriterUtil.encodeMongoObjectKey(type);

					if (paramName.startsWith("GT_")) {
						BsonDocument query = getCompExtensionQueryObject(type,
								new String[] { "errorDeclaration.any." + type }, paramValues, "GT");
						if (query != null)
							queryList.add(query);
					}
					if (paramName.startsWith("GE_")) {
						BsonDocument query = getCompExtensionQueryObject(type,
								new String[] { "errorDeclaration.any." + type }, paramValues, "GE");
						if (query != null)
							queryList.add(query);
					}
					if (paramName.startsWith("LT_")) {
						BsonDocument query = getCompExtensionQueryObject(type,
								new String[] { "errorDeclaration.any." + type }, paramValues, "LT");
						if (query != null)
							queryList.add(query);
					}
					if (paramName.startsWith("LE_")) {
						BsonDocument query = getCompExtensionQueryObject(type,
								new String[] { "errorDeclaration.any." + type }, paramValues, "LE");
						if (query != null)
							queryList.add(query);
					}
				}

				boolean isExtraParam = isExtraParameter(paramName);

				if (isExtraParam == true) {

					/**
					 * EQ_fieldname: This is not a single parameter, but a family of parameters. If
					 * a parameter of this form is specified, the result will only include events
					 * that (a) have a field named fieldname whose type is either String or a
					 * vocabulary type; and where (b) the value of that field matches one of the
					 * values specified in this parameter. Fieldname is the fully qualified name of
					 * an extension field. The name of an extension field is an XML qname; that is,
					 * a pair consisting of an XML namespace URI and a name. The name of the
					 * corresponding query parameter is constructed by concatenating the following:
					 * the string EQ_, the namespace URI for the extension field, a pound sign (#),
					 * and the name of the extension field.
					 */
					if (paramName.startsWith("EQ_")) {
						String type = paramName.substring(3, paramName.length());
						type = MongoWriterUtil.encodeMongoObjectKey(type);

						BsonArray paramArray = getParamBsonArray(paramValues);
						BsonDocument queryObject = getQueryObject(
								new String[] { "any." + type, "otherAttributes." + type }, paramArray);
						if (queryObject != null) {
							queryList.add(queryObject);
						}
					}

					/**
					 * GT/GE/LT/LE_fieldname: Like EQ_fieldname as described above, but may be
					 * applied to a field of type Int, Float, or Time. The result will include
					 * events that (a) have a field named fieldname; and where (b) the type of the
					 * field matches the type of this parameter (Int, Float, or Time); and where (c)
					 * the value of the field is greater than the specified value. Fieldname is
					 * constructed as for EQ_fieldname.
					 */

					if (paramName.startsWith("GT_") || paramName.startsWith("GE_") || paramName.startsWith("LT_")
							|| paramName.startsWith("LE_")) {
						String type = paramName.substring(3, paramName.length());
						type = MongoWriterUtil.encodeMongoObjectKey(type);

						if (paramName.startsWith("GT_")) {
							BsonDocument query = getCompExtensionQueryObject(type,
									new String[] { "any." + type, "otherAttributes." + type }, paramValues, "GT");
							if (query != null)
								queryList.add(query);
						}
						if (paramName.startsWith("GE_")) {
							BsonDocument query = getCompExtensionQueryObject(type,
									new String[] { "any." + type, "otherAttributes." + type }, paramValues, "GE");
							if (query != null)
								queryList.add(query);
						}
						if (paramName.startsWith("LT_")) {
							BsonDocument query = getCompExtensionQueryObject(type,
									new String[] { "any." + type, "otherAttributes." + type }, paramValues, "LT");
							if (query != null)
								queryList.add(query);
						}
						if (paramName.startsWith("LE_")) {
							BsonDocument query = getCompExtensionQueryObject(type,
									new String[] { "any." + type, "otherAttributes." + type }, paramValues, "LE");
							if (query != null)
								queryList.add(query);
						}
					}

					/**
					 * EXISTS_fieldname: Like EQ_fieldname as described above, but may be applied to
					 * a field of any type (including complex types). The result will include events
					 * that have a non-empty field named fieldname . Fieldname is constructed as for
					 * EQ_fieldname . EXISTS_ ILMD_fieldname HASATTR_fieldname Void Note that the
					 * value for this query parameter is ignored.
					 * 
					 * Regex not supported
					 * 
					 */

					if (paramName.startsWith("EXISTS_")) {

						String field = paramName.substring(7, paramName.length());
						field = MongoWriterUtil.encodeMongoObjectKey(field);
						paramValues = MongoWriterUtil.encodeMongoObjectKey(paramValues);
						Boolean isExist = Boolean.parseBoolean(paramValues);
						BsonBoolean isExistBson = new BsonBoolean(isExist);
						BsonDocument query = getExistsQueryObject("any", field, isExistBson);
						if (query != null)
							queryList.add(query);
					}
				}
			}
		}
		return queryList;
	}

	@SuppressWarnings("unused")
	private boolean isPostFilterPassed(String eventType, BsonDocument dbObject, Map<String, String> paramMap) {
		if (paramMap == null || paramMap.isEmpty())
			return true;
		Iterator<String> paramIter = paramMap.keySet().iterator();
		while (paramIter.hasNext()) {
			String paramName = paramIter.next();
			String paramValues = paramMap.get(paramName);

			BsonDocument ilmd = null;
			BsonDocument error = null;
			BsonDocument ext = null;

			// Prepare BsonDocument
			if (eventType.equals("ObjectEvent")) {
				if (dbObject.containsKey("extension") && dbObject.getDocument("extension").containsKey("ilmd")
						&& dbObject.getDocument("extension").getDocument("ilmd").containsKey("any")) {
					ilmd = dbObject.getDocument("extension").getDocument("ilmd").getDocument("any");
				}
			} else if (eventType.equals("TransformationEvent")) {
				if (dbObject.containsKey("ilmd") && dbObject.getDocument("ilmd").containsKey("any")) {
					ilmd = dbObject.getDocument("ilmd").getDocument("any");
				}
			}

			if (dbObject.containsKey("errorDeclaration")) {
				if (dbObject.getDocument("errorDeclaration").containsKey("any")) {
					error = dbObject.getDocument("errorDeclaration").getDocument("any");
				}
			}

			if (dbObject.containsKey("any")) {
				ext = dbObject.getDocument("any");
			}

			// TODO: HASATTR_fieldname

			/**
			 * HASATTR_fieldname: This is not a single parameter, but a family of
			 * parameters. If a parameter of this form is specified, the result will only
			 * include events that (a) have a field named fieldname whose type is a
			 * vocabulary type; and (b) where the value of that field is a vocabulary
			 * element for which master data is available; and (c) the master data has a
			 * non-null attribute whose name matches one of the values specified in this
			 * parameter. Fieldname is the fully qualified name of a field. For a standard
			 * field, this is simply the field name; e.g., bizLocation . For an extension
			 * EQATTR_fieldname _attrname List of String field, the name of an extension
			 * field is an XML qname; that is, a pair consisting of an XML namespace URI and
			 * a name. The name of the corresponding query parameter is constructed by
			 * concatenating the following: the string HASATTR_ , the namespace URI for the
			 * extension field, a pound sign (#), and the name of the extension field.
			 */

			if (paramName.startsWith("HASATTR_")) {
				String type = paramName.substring(8, paramName.length());
				type = MongoWriterUtil.encodeMongoObjectKey(type);

				BsonArray paramArray = getParamBsonArray(paramValues);

				continue;
			}

			// TODO: EQATTR_fieldname_attrname

			/**
			 * This is not a single parameter, but a family of parameters. If a parameter of
			 * this form is specified, the result will only include events that (a) have a
			 * field named fieldname whose type is a vocabulary type; and (b) where the
			 * value of that field is a vocabulary element for which master data is
			 * available; and (c) the master data has a non-null attribute named attrname ;
			 * and (d) where the value of that attribute matches one of the values specified
			 * in this parameter. Fieldname is constructed as for HASATTR_fieldname . The
			 * implementation MAY raise a QueryParameterException if fieldname or attrname
			 * includes an underscore character. EQ_eventID List of String EXISTS_
			 * errorDeclaration Void GE_errorDeclaration Time Time Explanation
			 * (non-normative): because the presence of an underscore in fieldname or
			 * attrname presents an ambiguity as to where the division between fieldname and
			 * attrname lies, an implementation is free to reject the query parameter if it
			 * cannot disambiguate.
			 */

			if (paramName.startsWith("EQATTR_")) {
				String type = paramName.substring(7, paramName.length());
				String[] typeArr = type.trim().split("_");
				if (typeArr.length != 2)
					continue;
				String fieldname = typeArr[0];
				String attrname = typeArr[1];

				BsonArray paramArray = getParamBsonArray(paramValues);

				continue;
			}

			/**
			 * Analogous to EQ_ILMD_fieldname , but matches inner ILMD elements; that is,
			 * any XML element nested within a top-level ILMD element. Note that a matching
			 * inner element may exist within in more than one top-level element or may
			 * occur more than once within a single top-level element; this parameter
			 * matches if at least one matching occurrence is found anywhere in the ILMD
			 * section (except at top-level).
			 */

			if (paramName.startsWith("EQ_INNER_ILMD_")) {
				if (eventType.equals("AggregationEvent") || eventType.equals("QuantityEvent")
						|| eventType.equals("TransactionEvent")) {
					return false;
				}
				if (ilmd == null)
					return false;
				String type = paramName.substring(14, paramName.length());
				BsonArray paramArray = getParamBsonArray(paramValues);

				if (isExtensionFilterPassed(type, paramArray, ilmd, true) == true)
					return true;
				else
					return false;
			}

			/**
			 * Like EQ_INNER_ILMD_ fieldname as described above, but may be applied to a
			 * field of type Int, Float, or Time.
			 */

			if (paramName.startsWith("GT_INNER_ILMD_") || paramName.startsWith("GE_INNER_ILMD_")
					|| paramName.startsWith("LT_INNER_ILMD_") || paramName.startsWith("LE_INNER_ILMD_")) {

				if (eventType.equals("AggregationEvent") || eventType.equals("QuantityEvent")
						|| eventType.equals("TransactionEvent")) {
					return false;
				}
				if (ilmd == null)
					return false;
				String type = paramName.substring(14, paramName.length());
				BsonArray paramArray = getParamBsonArray(paramValues);

				if (paramName.startsWith("GT_")) {
					if (isCompExtensionFilterPassed(type, "GT", paramArray, ilmd) == true)
						return true;
					else
						return false;
				}
				if (paramName.startsWith("GE_")) {
					if (isCompExtensionFilterPassed(type, "GE", paramArray, ilmd) == true)
						return true;
					else
						return false;
				}
				if (paramName.startsWith("LT_")) {
					if (isCompExtensionFilterPassed(type, "LT", paramArray, ilmd) == true)
						return true;
					else
						return false;
				}
				if (paramName.startsWith("LE_")) {
					if (isCompExtensionFilterPassed(type, "LE", paramArray, ilmd) == true)
						return true;
					else
						return false;
				}
			}

			/**
			 * Analogous to EQ_ERROR_DECLARATION_fieldname , but matches inner extension
			 * elements; that is, any XML element nested within a top-level extension
			 * element. Note that a matching inner element may exist within in more than one
			 * top-level element or may occur more than once within a single top-level
			 * element; this parameter matches if at least one matching occurrence is found
			 * anywhere in the event (except at top-level)..
			 */

			if (paramName.startsWith("EQ_INNER_ERROR_DECLARATION_")) {
				if (error == null)
					return false;
				String type = paramName.substring(27, paramName.length());
				BsonArray paramArray = getParamBsonArray(paramValues);

				if (isExtensionFilterPassed(type, paramArray, error, true) == true)
					return true;
				else
					return false;
			}

			/**
			 * Like EQ_INNER_ERROR_DECLARATION _ fieldname as described above, but may be
			 * applied to a field of type Int, Float, or Time.
			 */

			if (paramName.startsWith("GT_INNER_ERROR_DECLARATION_")
					|| paramName.startsWith("GE_INNER_ERROR_DECLARATION_")
					|| paramName.startsWith("LT_INNER_ERROR_DECLARATION_")
					|| paramName.startsWith("LE_INNER_ERROR_DECLARATION_")) {

				if (error == null)
					return false;
				String type = paramName.substring(27, paramName.length());
				BsonArray paramArray = getParamBsonArray(paramValues);

				if (paramName.startsWith("GT_")) {
					if (isCompExtensionFilterPassed(type, "GT", paramArray, error) == true)
						return true;
					else
						return false;
				}
				if (paramName.startsWith("GE_")) {
					if (isCompExtensionFilterPassed(type, "GE", paramArray, error) == true)
						return true;
					else
						return false;
				}
				if (paramName.startsWith("LT_")) {
					if (isCompExtensionFilterPassed(type, "LT", paramArray, error) == true)
						return true;
					else
						return false;
				}
				if (paramName.startsWith("LE_")) {
					if (isCompExtensionFilterPassed(type, "LE", paramArray, error) == true)
						return true;
					else
						return false;
				}
			}

			/**
			 * Analogous to EQ_fieldname , but matches inner extension elements; that is,
			 * any XML element nested within a top-level extension element. Note that a
			 * matching inner element may exist within in more than one top-level element or
			 * may occur more than once within a single top-level element; this parameter
			 * matches if at least one matching occurrence is found anywhere in the event
			 * (except at top-level).
			 */

			if (paramName.startsWith("EQ_INNER_")) {
				if (ext == null)
					return false;
				String type = paramName.substring(9, paramName.length());
				BsonArray paramArray = getParamBsonArray(paramValues);

				if (isExtensionFilterPassed(type, paramArray, ext, true) == true)
					return true;
				else
					return false;
			}

			/**
			 * Like EQ_INNER _ fieldname as described above, but may be applied to a field
			 * of type Int, Float, or Time.
			 */

			if (paramName.startsWith("GT_INNER_") || paramName.startsWith("GE_INNER_")
					|| paramName.startsWith("LT_INNER_") || paramName.startsWith("LE_INNER_")) {

				if (ext == null)
					return false;
				String type = paramName.substring(9, paramName.length());
				BsonArray paramArray = getParamBsonArray(paramValues);

				if (paramName.startsWith("GT_")) {
					if (isCompExtensionFilterPassed(type, "GT", paramArray, ext) == true)
						return true;
					else
						return false;
				}
				if (paramName.startsWith("GE_")) {
					if (isCompExtensionFilterPassed(type, "GE", paramArray, ext) == true)
						return true;
					else
						return false;
				}
				if (paramName.startsWith("LT_")) {
					if (isCompExtensionFilterPassed(type, "LT", paramArray, ext) == true)
						return true;
					else
						return false;
				}
				if (paramName.startsWith("LE_")) {
					if (isCompExtensionFilterPassed(type, "LE", paramArray, ext) == true)
						return true;
					else
						return false;
				}
			}

		}

		return true;
	}

	private boolean isExtensionFilterPassed(String type, BsonArray paramArray, BsonDocument ext, boolean isTopLevel) {
		type = MongoWriterUtil.encodeMongoObjectKey(type);
		Iterator<String> keyIterator = ext.keySet().iterator();
		while (keyIterator.hasNext()) {
			String key = keyIterator.next();
			BsonValue sub = ext.get(key);
			if (isTopLevel == false) {
				if (key.equals(type)) {
					for (int i = 0; i < paramArray.size(); i++) {
						BsonValue param = paramArray.get(i);
						if (sub.getBsonType() == param.getBsonType() && sub.toString().equals(param.toString())) {
							return true;
						}
						if (param.getBsonType() == BsonType.REGULAR_EXPRESSION
								&& sub.getBsonType() == BsonType.STRING) {
							if (Pattern.matches(param.asRegularExpression().getPattern(), sub.asString().getValue()))
								return true;
						}
					}
					return false;
				}
			}
			if (sub.getBsonType() == BsonType.DOCUMENT) {
				if (isExtensionFilterPassed(type, paramArray, sub.asDocument(), false) == true) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isCompExtensionFilterPassed(String type, String comp, BsonArray paramArray, BsonDocument ext) {
		type = MongoWriterUtil.encodeMongoObjectKey(type);
		Iterator<String> keyIterator = ext.keySet().iterator();
		while (keyIterator.hasNext()) {
			String key = keyIterator.next();
			BsonValue sub = ext.get(key);
			if (key.equals(type)) {
				for (int i = 0; i < paramArray.size(); i++) {
					BsonValue param = paramArray.get(i);
					if (sub.getBsonType() == param.getBsonType()) {
						if (sub.getBsonType() == BsonType.INT32) {
							if (comp.equals("GT")) {
								if (sub.asInt32().getValue() > param.asInt32().getValue())
									return true;
							} else if (comp.equals("GE")) {
								if (sub.asInt32().getValue() >= param.asInt32().getValue())
									return true;
							} else if (comp.equals("LT")) {
								if (sub.asInt32().getValue() < param.asInt32().getValue())
									return true;
							} else if (comp.equals("LE")) {
								if (sub.asInt32().getValue() <= param.asInt32().getValue())
									return true;
							}
						} else if (sub.getBsonType() == BsonType.INT64) {
							if (comp.equals("GT")) {
								if (sub.asInt64().getValue() > param.asInt64().getValue())
									return true;
							} else if (comp.equals("GE")) {
								if (sub.asInt64().getValue() >= param.asInt64().getValue())
									return true;
							} else if (comp.equals("LT")) {
								if (sub.asInt64().getValue() < param.asInt64().getValue())
									return true;
							} else if (comp.equals("LE")) {
								if (sub.asInt64().getValue() <= param.asInt64().getValue())
									return true;
							}
						} else if (sub.getBsonType() == BsonType.DOUBLE) {
							if (comp.equals("GT")) {
								if (sub.asDouble().getValue() > param.asDouble().getValue())
									return true;
							} else if (comp.equals("GE")) {
								if (sub.asDouble().getValue() >= param.asDouble().getValue())
									return true;
							} else if (comp.equals("LT")) {
								if (sub.asDouble().getValue() < param.asDouble().getValue())
									return true;
							} else if (comp.equals("LE")) {
								if (sub.asDouble().getValue() <= param.asDouble().getValue())
									return true;
							}
						} else if (sub.getBsonType() == BsonType.DATE_TIME) {
							if (comp.equals("GT")) {
								if (sub.asDateTime().getValue() > param.asDateTime().getValue())
									return true;
							} else if (comp.equals("GE")) {
								if (sub.asDateTime().getValue() >= param.asDateTime().getValue())
									return true;
							} else if (comp.equals("LT")) {
								if (sub.asDateTime().getValue() < param.asDateTime().getValue())
									return true;
							} else if (comp.equals("LE")) {
								if (sub.asDateTime().getValue() <= param.asDateTime().getValue())
									return true;
							}
						}
					}
				}
				return false;
			}
			if (sub.getBsonType() == BsonType.DOCUMENT) {
				if (isCompExtensionFilterPassed(type, comp, paramArray, sub.asDocument()) == true) {
					return true;
				}
			}
		}
		return false;
	}

	private BsonArray makeMasterQueryObjects(PollParameters p) {

		BsonArray queryList = new BsonArray();

		/**
		 * vocabularyName : If specified, only vocabulary elements drawn from one of the
		 * specified vocabularies will be included in the results. Each element of the
		 * specified list is the formal URI name for a vocabulary; e.g., one of the URIs
		 * specified in the table at the end of Section 7.2. If omitted, all
		 * vocabularies are considered.
		 */

		if (p.getVocabularyName() != null) {

			BsonArray paramArray = getParamBsonArray(p.getVocabularyName());
			BsonDocument queryObject = getQueryObject(new String[] { "type" }, paramArray);
			if (queryObject != null) {
				queryList.add(queryObject);
			}

		}

		/**
		 * EQ_name : If specified, the result will only include vocabulary elements
		 * whose names are equal to one of the specified values. If this parameter and
		 * WD_name are both omitted, vocabulary elements are included regardless of
		 * their names.
		 */
		if (p.getEQ_name() != null) {
			BsonArray paramArray = getParamBsonArray(p.getEQ_name());
			BsonDocument queryObject = getQueryObject(new String[] { "id" }, paramArray);
			if (queryObject != null) {
				queryList.add(queryObject);
			}
		}

		/**
		 * WD_name : If specified, the result will only include vocabulary elements that
		 * either match one of the specified names, or are direct or indirect
		 * descendants of a vocabulary element that matches one of the specified names.
		 * The meaning of “direct or indirect descendant” is described in Section 6.5.
		 * (WD is an abbreviation for “with descendants.”) If this parameter and EQ_name
		 * are both omitted, vocabulary elements are included regardless of their names.
		 */
		if (p.getWD_name() != null) {

			BsonArray paramArray = getWDParamBsonArray(p.getWD_name());
			BsonDocument queryObject = getQueryObject(new String[] { "id" }, paramArray);
			if (queryObject != null) {
				queryList.add(queryObject);
			}

		}

		/**
		 * HASATTR : If specified, the result will only include vocabulary elements that
		 * have a non-null attribute whose name matches one of the values specified in
		 * this parameter.
		 */

		if (p.getHASATTR() != null) {
			String[] attrArr = p.getHASATTR().split(",");
			for (int i = 0; i < attrArr.length; i++) {
				String attrString = attrArr[i].trim();
				BsonDocument query = getExistsQueryObject("attributes", attrString, new BsonBoolean(true));
				if (query != null)
					queryList.add(query);
			}
		}

		/**
		 * EQATTR_attrnam : This is not a single parameter, but a family of parameters.
		 * If a parameter of this form is specified, the result will only include
		 * vocabulary elements that have a non-null attribute named attrname, and where
		 * the value of that attribute matches one of the values specified in this
		 * parameter.
		 */
		if (p.getParams() != null) {
			Iterator<String> paramIter = p.getParams().keySet().iterator();
			while (paramIter.hasNext()) {
				String paramName = paramIter.next();
				String paramValues = p.getParams().get(paramName);

				if (paramName.contains("EQATTR_")) {
					String type = paramName.substring(7, paramName.length());

					BsonArray paramArray = getParamBsonArray(paramValues);
					BsonDocument queryObject = getQueryObject(
							new String[] { "attributes." + encodeMongoObjectKey(type) }, paramArray);
					if (queryObject != null) {
						queryList.add(queryObject);
					}
				}
			}
		}
		return queryList;
	}

	public void addScheduleToQuartz(SubscriptionType subscription) {
		try {
			JobDataMap map = new JobDataMap();
			map.put("jobData", SubscriptionType.asBsonDocument(subscription));

			JobDetail job = newJob(MongoSubscriptionTask.class)
					.withIdentity(subscription.getSubscriptionID(), subscription.getPollParameters().getQueryName())
					.setJobData(map).storeDurably(false).build();

			Trigger trigger = newTrigger()
					.withIdentity(subscription.getSubscriptionID(), subscription.getPollParameters().getQueryName())
					.startNow().withSchedule(cronSchedule(subscription.getSchedule())).build();

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

	private boolean addScheduleToDB(SubscriptionType s, String userID, List<String> friendList) {

		MongoCollection<BsonDocument> collection = Configuration.mongoDatabase.getCollection("Subscription",
				BsonDocument.class);

		BsonDocument subscription = collection
				.find(new BsonDocument("subscriptionID", new BsonString(s.getSubscriptionID()))).first();

		if (subscription == null) {
			collection.insertOne(SubscriptionType.asBsonDocument(s));
		}

		Configuration.logger.log(Level.INFO, "Subscription ID: " + s.getSubscriptionID() + " is added to DB. ");
		return true;
	}

	private void removeScheduleFromQuartz(SubscriptionType subscription) {
		try {
			MongoSubscription.sched.unscheduleJob(
					triggerKey(subscription.getSubscriptionID(), subscription.getPollParameters().getQueryName()));
			MongoSubscription.sched.deleteJob(
					jobKey(subscription.getSubscriptionID(), subscription.getPollParameters().getQueryName()));
			Configuration.logger.log(Level.INFO,
					"Subscription ID: " + subscription.getSubscriptionID() + " is removed from scheduler");
		} catch (SchedulerException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
		}
	}

}
