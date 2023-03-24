package org.oliot.epcis.query;

import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import org.oliot.epcis.converter.json.read.JSONAggregationEventReadConverter;
import org.oliot.epcis.converter.json.read.JSONAssociationEventReadConverter;
import org.oliot.epcis.converter.json.read.JSONEPCISEventReadConverter;
import org.oliot.epcis.converter.json.read.JSONObjectEventReadConverter;
import org.oliot.epcis.converter.json.read.JSONTransactionEventReadConverter;
import org.oliot.epcis.converter.json.read.JSONTransformationEventReadConverter;
import org.oliot.epcis.converter.xml.read.*;
import org.oliot.epcis.model.*;
import org.oliot.epcis.model.exception.ImplementationException;
import org.oliot.epcis.model.exception.InvalidURIException;
import org.oliot.epcis.model.exception.QueryParameterException;
import org.oliot.epcis.model.exception.QueryTooLargeException;
import org.oliot.epcis.model.exception.SubscriptionControlsException;
import org.oliot.epcis.model.exception.ValidationException;
import org.oliot.epcis.util.HTTPUtil;
import org.oliot.epcis.util.ObservableSubscriber;
import org.oliot.gcp.core.DLConverter;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mongodb.reactivestreams.client.FindPublisher;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Copyright (C) 2020-2021. (Jaewook Byun) all rights reserved.
 * <p>
 * Oliot EPCIS X is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * Among various modules, epcis-query-rest acts as a server to receive queries
 * to provide filtered, sorted, limited events of interest inside EPCIS
 * repository.
 * <p>
 * 
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr
 *         <p>
 *         Associate Director, Auto-ID Labs, KAIST, bjw0829@kaist.ac.kr
 */
public class RESTQueryService {

	static HashMap<String, QueryDescription> remainingQueries = new HashMap<>();
	public static SchedulerFactory schedFact;
	public static Scheduler sched;

	public static HashSet<String> epcs;

	public void getResources(HttpServerRequest request, HttpServerResponse response, boolean requireScan, String type) {
		if (requireScan)
			BootstrapUtil.scanResources();
		ObservableSubscriber<org.bson.Document> collector = new ObservableSubscriber<org.bson.Document>();
		if (type.equals("eventTypes")) {
			FindPublisher<org.bson.Document> query = RESTQueryServer.mEventTypes.find()
					.sort(new org.bson.Document().append("_id", 1));
			query.subscribe(collector);
			try {
				collector.await();
			} catch (Throwable e1) {
				ImplementationException e = new ImplementationException();
				e.setReason(e1.getMessage());
				e.setStackTrace(new StackTraceElement[0]);
				HTTPUtil.sendQueryResults(response, e, null, null, null, e.getClass());
				return;
			}

			List<org.bson.Document> resultList = collector.getReceived();
			JsonArray eventTypes = new JsonArray();
			for (org.bson.Document result : resultList) {
				eventTypes.add(result.getString("_id"));
			}
			HTTPUtil.sendQueryResults(response, eventTypes, 200);
		} else if (type.equals("epcs")) {
			FindPublisher<org.bson.Document> query = RESTQueryServer.mEPCs.find()
					.sort(new org.bson.Document().append("_id", 1));
			query.subscribe(collector);
			try {
				collector.await();
			} catch (Throwable e1) {
				ImplementationException e = new ImplementationException();
				e.setReason(e1.getMessage());
				e.setStackTrace(new StackTraceElement[0]);
				HTTPUtil.sendQueryResults(response, e, null, null, null, e.getClass());
				return;
			}

			List<org.bson.Document> resultList = collector.getReceived();
			JsonArray eventTypes = new JsonArray();
			for (org.bson.Document result : resultList) {
				eventTypes.add(result.getString("_id"));
			}
			HTTPUtil.sendQueryResults(response, eventTypes, 200);
		} else if (type.equals("bizSteps")) {
			FindPublisher<org.bson.Document> query = RESTQueryServer.mBizSteps.find()
					.sort(new org.bson.Document().append("_id", 1));
			query.subscribe(collector);
			try {
				collector.await();
			} catch (Throwable e1) {
				ImplementationException e = new ImplementationException();
				e.setReason(e1.getMessage());
				e.setStackTrace(new StackTraceElement[0]);
				HTTPUtil.sendQueryResults(response, e, null, null, null, e.getClass());
				return;
			}

			List<org.bson.Document> resultList = collector.getReceived();
			JsonArray eventTypes = new JsonArray();
			for (org.bson.Document result : resultList) {
				eventTypes.add(result.getString("_id"));
			}
			HTTPUtil.sendQueryResults(response, eventTypes, 200);
		} else if (type.equals("dispositions")) {
			FindPublisher<org.bson.Document> query = RESTQueryServer.mDispositions.find()
					.sort(new org.bson.Document().append("_id", 1));
			query.subscribe(collector);
			try {
				collector.await();
			} catch (Throwable e1) {
				ImplementationException e = new ImplementationException();
				e.setReason(e1.getMessage());
				e.setStackTrace(new StackTraceElement[0]);
				HTTPUtil.sendQueryResults(response, e, null, null, null, e.getClass());
				return;
			}

			List<org.bson.Document> resultList = collector.getReceived();
			JsonArray eventTypes = new JsonArray();
			for (org.bson.Document result : resultList) {
				eventTypes.add(result.getString("_id"));
			}
			HTTPUtil.sendQueryResults(response, eventTypes, 200);
		} else if (type.equals("readPoints")) {
			FindPublisher<org.bson.Document> query = RESTQueryServer.mReadPoints.find()
					.sort(new org.bson.Document().append("_id", 1));
			query.subscribe(collector);
			try {
				collector.await();
			} catch (Throwable e1) {
				ImplementationException e = new ImplementationException();
				e.setReason(e1.getMessage());
				e.setStackTrace(new StackTraceElement[0]);
				HTTPUtil.sendQueryResults(response, e, null, null, null, e.getClass());
				return;
			}

			List<org.bson.Document> resultList = collector.getReceived();
			JsonArray eventTypes = new JsonArray();
			for (org.bson.Document result : resultList) {
				eventTypes.add(result.getString("_id"));
			}
			HTTPUtil.sendQueryResults(response, eventTypes, 200);
		} else {
			// bizLocations
			FindPublisher<org.bson.Document> query = RESTQueryServer.mBizLocations.find()
					.sort(new org.bson.Document().append("_id", 1));
			query.subscribe(collector);
			try {
				collector.await();
			} catch (Throwable e1) {
				ImplementationException e = new ImplementationException();
				e.setReason(e1.getMessage());
				e.setStackTrace(new StackTraceElement[0]);
				HTTPUtil.sendQueryResults(response, e, null, null, null, e.getClass());
				return;
			}

			List<org.bson.Document> resultList = collector.getReceived();
			JsonArray eventTypes = new JsonArray();
			for (org.bson.Document result : resultList) {
				eventTypes.add(result.getString("_id"));
			}
			HTTPUtil.sendQueryResults(response, eventTypes, 200);
		}
	}

	public void checkResource(HttpServerRequest request, HttpServerResponse response, String type, String resource) {

		org.bson.Document q = new org.bson.Document().append("_id", resource);

		ObservableSubscriber<org.bson.Document> collector = new ObservableSubscriber<org.bson.Document>();
		if (type.equals("eventTypes")) {
			FindPublisher<org.bson.Document> query = RESTQueryServer.mEventTypes.find(q)
					.sort(new org.bson.Document().append("_id", 1));
			query.subscribe(collector);
			try {
				collector.await();
			} catch (Throwable e1) {
				ImplementationException e = new ImplementationException();
				e.setReason(e1.getMessage());
				e.setStackTrace(new StackTraceElement[0]);
				HTTPUtil.sendQueryResults(response, e, null, null, null, e.getClass());
				return;
			}

			List<org.bson.Document> resultList = collector.getReceived();
			if(resultList.size() == 0)
				HTTPUtil.sendQueryResults(response, new JsonArray(), 404);
			else
				HTTPUtil.sendQueryResults(response, new JsonArray().add("events"), 200);
		} else if (type.equals("epcs")) {
			FindPublisher<org.bson.Document> query = RESTQueryServer.mEPCs.find(q)
					.sort(new org.bson.Document().append("_id", 1));
			query.subscribe(collector);
			try {
				collector.await();
			} catch (Throwable e1) {
				ImplementationException e = new ImplementationException();
				e.setReason(e1.getMessage());
				e.setStackTrace(new StackTraceElement[0]);
				HTTPUtil.sendQueryResults(response, e, null, null, null, e.getClass());
				return;
			}

			List<org.bson.Document> resultList = collector.getReceived();
			if(resultList.size() == 0)
				HTTPUtil.sendQueryResults(response, new JsonArray(), 404);
			else
				HTTPUtil.sendQueryResults(response, new JsonArray().add("events"), 200);
		} else if (type.equals("bizSteps")) {
			FindPublisher<org.bson.Document> query = RESTQueryServer.mBizSteps.find(q)
					.sort(new org.bson.Document().append("_id", 1));
			query.subscribe(collector);
			try {
				collector.await();
			} catch (Throwable e1) {
				ImplementationException e = new ImplementationException();
				e.setReason(e1.getMessage());
				e.setStackTrace(new StackTraceElement[0]);
				HTTPUtil.sendQueryResults(response, e, null, null, null, e.getClass());
				return;
			}

			List<org.bson.Document> resultList = collector.getReceived();
			if(resultList.size() == 0)
				HTTPUtil.sendQueryResults(response, new JsonArray(), 404);
			else
				HTTPUtil.sendQueryResults(response, new JsonArray().add("events"), 200);
		} else if (type.equals("dispositions")) {
			FindPublisher<org.bson.Document> query = RESTQueryServer.mDispositions.find(q)
					.sort(new org.bson.Document().append("_id", 1));
			query.subscribe(collector);
			try {
				collector.await();
			} catch (Throwable e1) {
				ImplementationException e = new ImplementationException();
				e.setReason(e1.getMessage());
				e.setStackTrace(new StackTraceElement[0]);
				HTTPUtil.sendQueryResults(response, e, null, null, null, e.getClass());
				return;
			}

			List<org.bson.Document> resultList = collector.getReceived();
			if(resultList.size() == 0)
				HTTPUtil.sendQueryResults(response, new JsonArray(), 404);
			else
				HTTPUtil.sendQueryResults(response, new JsonArray().add("events"), 200);
		} else if (type.equals("readPoints")) {
			FindPublisher<org.bson.Document> query = RESTQueryServer.mReadPoints.find(q)
					.sort(new org.bson.Document().append("_id", 1));
			query.subscribe(collector);
			try {
				collector.await();
			} catch (Throwable e1) {
				ImplementationException e = new ImplementationException();
				e.setReason(e1.getMessage());
				e.setStackTrace(new StackTraceElement[0]);
				HTTPUtil.sendQueryResults(response, e, null, null, null, e.getClass());
				return;
			}

			List<org.bson.Document> resultList = collector.getReceived();
			if(resultList.size() == 0)
				HTTPUtil.sendQueryResults(response, new JsonArray(), 404);
			else
				HTTPUtil.sendQueryResults(response, new JsonArray().add("events"), 200);
		} else {
			// bizLocations
			FindPublisher<org.bson.Document> query = RESTQueryServer.mBizLocations.find(q)
					.sort(new org.bson.Document().append("_id", 1));
			query.subscribe(collector);
			try {
				collector.await();
			} catch (Throwable e1) {
				ImplementationException e = new ImplementationException();
				e.setReason(e1.getMessage());
				e.setStackTrace(new StackTraceElement[0]);
				HTTPUtil.sendQueryResults(response, e, null, null, null, e.getClass());
				return;
			}

			List<org.bson.Document> resultList = collector.getReceived();
			if(resultList.size() == 0)
				HTTPUtil.sendQueryResults(response, new JsonArray(), 404);
			else
				HTTPUtil.sendQueryResults(response, new JsonArray().add("events"), 200);

		}
	}

	public void getNextEvents(HttpServerResponse response, String queryID) {
		try {
			if (remainingQueries.containsKey(queryID)) {

				QueryDescription qd = remainingQueries.get(queryID);

				FindPublisher<org.bson.Document> query = RESTQueryServer.mEventCollection.find(qd.getMongoQuery());

				if (!qd.getMongoProjection().isEmpty())
					query.projection(qd.getMongoProjection());
				if (!qd.getMongoSort().isEmpty())
					query.sort(qd.getMongoSort());

				final int min;

				if (qd.getEventCountLimit() != null) {
					min = Math.min(qd.getPerPage().get(), qd.getEventCountLimit());
					query.limit(min);
				} else {
					min = qd.getPerPage().get();
					query.limit(min);
				}
				query.skip(qd.getSkip().getAndAdd(qd.getPerPage().get()));

				ObservableSubscriber<org.bson.Document> collector = new ObservableSubscriber<org.bson.Document>();

				query.subscribe(collector);
				try {
					collector.await();
				} catch (Throwable e1) {
					ImplementationException e = new ImplementationException();
					e.setReason(e1.getMessage());
					e.setStackTrace(new StackTraceElement[0]);
					HTTPUtil.sendQueryResults(response, e, null, null, null, e.getClass());
					return;
				}

				List<org.bson.Document> resultList = collector.getReceived();

				if (qd.getMaxCount() != null && (resultList.size() > qd.getMaxCount())) {
					QueryTooLargeException e = new QueryTooLargeException();
					e.setReason(
							"An attempt to execute a query resulted in more data than the service was willing to provide. ( result size: "
									+ resultList.size() + " )");
					e.setStackTrace(new StackTraceElement[0]);
					HTTPUtil.sendQueryResults(response, e, null, null, null, e.getClass());
					return;
				}

				JsonArray context = new JsonArray();
				context.add("https://ref.gs1.org/standards/epcis/2.0.0/epcis-context.jsonld");
				ArrayList<String> namespaces = JSONEPCISEventReadConverter.getNamespaces(resultList);
				JsonObject extContext = new JsonObject();
				for (int i = 0; i < namespaces.size(); i++) {
					extContext.put("ext" + i, JSONEPCISEventReadConverter.decodeMongoObjectKey(namespaces.get(i)));
				}
				context.add(extContext);
				JsonObject extType = new JsonObject();
				JsonArray list = getConvertedResultList(qd, resultList, namespaces, extType);
				context.add(extType);
				JsonObject queryResult = new JsonObject();
				queryResult.put("@context", context);
				queryResult.put("type", "EPCISQueryDocument");
				queryResult.put("schemaVersion", "2.0");
				queryResult.put("creationDate", JSONEPCISEventReadConverter.getQueriedDateTime(System.currentTimeMillis()));
				JsonObject queryResults = new JsonObject();
				queryResults.put("queryName", "SimpleEventQuery");
				queryResults.put("resultBody", new JsonObject().put("eventList", list));
				queryResult.put("epcisBody", new JsonObject().put("queryResults", queryResults));

				if (resultList.size() < min) {
					HTTPUtil.sendQueryResults(response.putHeader("link", "null").putHeader("rel", "last"), queryResult);
				} else {
					String id;
					while (true) {
						id = UUID.randomUUID().toString();
						if (!remainingQueries.containsKey(id))
							break;
					}
					remainingQueries.put(id, qd);
					remainingQueries.remove(queryID);
					HTTPUtil.sendQueryResults(response.putHeader("link", "http://" + RESTQueryServer.host + ":"
							+ RESTQueryServer.port + "/epcis/resource/next/" + id).putHeader("rel", "next"),
							queryResult);
				}

			} else {
				ImplementationException err = new ImplementationException();
				err.setReason("Invalid next result id: " + queryID);
				err.setStackTrace(new StackTraceElement[0]);
				HTTPUtil.sendQueryResults(response, err, null, null, null, err.getClass());
				RESTQueryServer.logger.error("Invalid next result id: " + queryID);
				return;
			}
		} catch (Exception e) {
			ImplementationException err = new ImplementationException();
			err.setReason(e.getMessage());
			err.setStackTrace(new StackTraceElement[0]);
			HTTPUtil.sendQueryResults(response, err, null, null, null, err.getClass());
			RESTQueryServer.logger.error(e.getMessage());
			return;
		}

	}

	public void getEvents(HttpServerRequest request, HttpServerResponse response) {
		boolean isSubscription = false;
		for (Entry<String, String> param : request.params()) {
			String key = param.getKey();
			if (key.equals("second") || key.equals("minute") || key.equals("hour") || key.equals("dayOfMonth")
					|| key.equals("month") || key.equals("dayOfWeek")) {
				isSubscription = true;
				break;
			}
		}
		if (isSubscription == true) {
			subscribe(request, response);
		} else {
			poll(request, response);
		}
	}

	public void subscribe(HttpServerRequest request, HttpServerResponse response) {
		try {
			Subscription sub = new Subscription(request.params());

			if (sub.getSchedule() != null && sub.getTrigger() == null) {
				try {
					request.toWebSocket().onSuccess(ws -> {
						cronSchedule(sub.getSchedule());
						final TriggerKey key = addScheduleToQuartz(ws, sub);

						ws.closeHandler(h -> {
							try {
								sched.unscheduleJob(key);
							} catch (SchedulerException e) {
								e.printStackTrace();
							}
						});
					});
				} catch (RuntimeException e) {
					SubscriptionControlsException e1 = new SubscriptionControlsException();
					e1.setStackTrace(new StackTraceElement[0]);
					e1.setReason(
							"The specified subscription controls was invalid; e.g., the schedule parameters were out of range, the trigger URI could not be parsed or did not name a recognised trigger, etc.");
					HTTPUtil.sendQueryResults(response, e, null, null, null, e.getClass());
				}
			} else {
				SubscriptionControlsException e = new SubscriptionControlsException();
				e.setStackTrace(new StackTraceElement[0]);
				e.setReason(
						"The specified subscription controls was invalid; e.g., the schedule parameters were out of range, the trigger URI could not be parsed or did not name a recognised trigger, etc.");
				HTTPUtil.sendQueryResults(response, e, null, null, null, e.getClass());
			}

		} catch (QueryParameterException | InvalidURIException | ImplementationException e2) {
			ImplementationException err = new ImplementationException();
			err.setReason(e2.getMessage());
			HTTPUtil.sendQueryResults(response, err, null, null, null, err.getClass());
			RESTQueryServer.logger.error(e2.getMessage());
		}
	}

	public TriggerKey addScheduleToQuartz(ServerWebSocket ws, Subscription subscription) {
		try {
			JobDataMap map = new JobDataMap();
			map.put("jobData", subscription);
			map.put("webSocket", ws);

			JobDetail job = newJob(SubscriptionTask.class).withIdentity(ws.textHandlerID(), "SimpleEventQuery")
					.setJobData(map).storeDurably(false).build();

			Trigger trigger = newTrigger().withIdentity(ws.textHandlerID(), "SimpleEventQuery").startNow()
					.withSchedule(cronSchedule(subscription.getSchedule())).build();

			if (schedFact == null) {
				schedFact = new org.quartz.impl.StdSchedulerFactory();
				sched = schedFact.getScheduler();
			}
			if (!sched.isStarted())
				sched.start();

			sched.scheduleJob(job, trigger);

			return trigger.getKey();
		} catch (SchedulerException | RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void poll(HttpServerRequest request, HttpServerResponse response) {

		try {
			QueryDescription qd = new QueryDescription(request.params());

			FindPublisher<org.bson.Document> query = RESTQueryServer.mEventCollection.find(qd.getMongoQuery());
			if (!qd.getMongoProjection().isEmpty())
				query.projection(qd.getMongoProjection());
			if (!qd.getMongoSort().isEmpty())
				query.sort(qd.getMongoSort());

			final int min;

			if (qd.getEventCountLimit() != null) {
				min = Math.min(qd.getPerPage().get(), qd.getEventCountLimit());
				query.limit(min);
			} else {
				min = qd.getPerPage().get();
				query.limit(min);
			}
			query.skip(qd.getSkip().getAndAdd(qd.getPerPage().get()));

			ObservableSubscriber<org.bson.Document> collector = new ObservableSubscriber<org.bson.Document>();

			query.subscribe(collector);
			try {
				collector.await();
			} catch (Throwable e1) {
				ImplementationException e = new ImplementationException();
				e.setReason(e1.getMessage());
				e.setStackTrace(new StackTraceElement[0]);
				HTTPUtil.sendQueryResults(response, e, null, null, null, e.getClass());
				return;
			}

			List<org.bson.Document> resultList = collector.getReceived();

			if (qd.getMaxCount() != null && (resultList.size() > qd.getMaxCount())) {
				QueryTooLargeException e = new QueryTooLargeException();
				e.setReason(
						"An attempt to execute a query resulted in more data than the service was willing to provide. ( result size: "
								+ resultList.size() + " )");
				e.setStackTrace(new StackTraceElement[0]);
				HTTPUtil.sendQueryResults(response, e, null, null, null, e.getClass());
				return;
			}

			JsonArray context = new JsonArray();
			context.add("https://ref.gs1.org/standards/epcis/2.0.0/epcis-context.jsonld");
			ArrayList<String> namespaces = JSONEPCISEventReadConverter.getNamespaces(resultList);
			JsonObject extType = new JsonObject();
			JsonObject extContext = new JsonObject();
			for (int i = 0; i < namespaces.size(); i++) {
				extContext.put("ext" + i, JSONEPCISEventReadConverter.decodeMongoObjectKey(namespaces.get(i)));
			}
			context.add(extContext);
			JsonArray list = getConvertedResultList(qd, resultList, namespaces, extType);
			context.add(extType);
			JsonObject queryResult = new JsonObject();
			queryResult.put("@context", context);
			queryResult.put("type", "EPCISQueryDocument");
			queryResult.put("schemaVersion", "2.0");
			queryResult.put("creationDate", JSONEPCISEventReadConverter.getQueriedDateTime(System.currentTimeMillis()));
			JsonObject queryResults = new JsonObject();
			queryResults.put("queryName", "SimpleEventQuery");
			queryResults.put("resultBody", new JsonObject().put("eventList", list));
			queryResult.put("epcisBody", new JsonObject().put("queryResults", queryResults));

			if (resultList.size() < min) {
				HTTPUtil.sendQueryResults(response.putHeader("link", "null").putHeader("rel", "last"), queryResult);
			} else {
				String id;
				while (true) {
					id = UUID.randomUUID().toString();
					if (!remainingQueries.containsKey(id))
						break;
				}
				remainingQueries.put(id, qd);
				HTTPUtil.sendQueryResults(response.putHeader("link",
						"http://" + RESTQueryServer.host + ":" + RESTQueryServer.port + "/epcis/resource/next/" + id)
						.putHeader("rel", "next"), queryResult);
			}

		} catch (QueryParameterException | ImplementationException e) {
			ImplementationException err = new ImplementationException();
			err.setReason(e.getMessage());
			HTTPUtil.sendQueryResults(response, err, null, null, null, err.getClass());
			RESTQueryServer.logger.error(e.getMessage());
			return;
		}
	}

	public static JsonArray getConvertedResultList(QueryDescription qd, List<org.bson.Document> resultList,
			ArrayList<String> namespaces, JsonObject extType) {
		Stream<org.bson.Document> resultStream;
		if (qd != null && (qd.getMongoSort() == null || qd.getMongoSort().isEmpty())) {
			resultStream = resultList.parallelStream();
		} else {
			resultStream = resultList.stream();
		}
		List<JsonObject> results = resultStream.map(result -> {

			switch (result.getString("type")) {
			case "AggregationEvent":
				return JSONAggregationEventReadConverter.convert(result, namespaces, extType);

			case "ObjectEvent":
				return JSONObjectEventReadConverter.convert(result, namespaces, extType);

			case "TransactionEvent":
				return JSONTransactionEventReadConverter.convert(result, namespaces, extType);

			case "TransformationEvent":
				return JSONTransformationEventReadConverter.convert(result, namespaces, extType);

			case "AssociationEvent":
				return JSONAssociationEventReadConverter.convert(result, namespaces, extType);

			default:
				return null;
			}
		}).filter(Objects::nonNull).collect(Collectors.toList());
		JsonArray resultArray = new JsonArray(results);
		return resultArray;
	}

	public List<Object> getConvertedResultList(QueryDescription qd, List<org.bson.Document> resultList, Document retDoc,
			Element envelope) {
		Stream<org.bson.Document> resultStream;
		if (qd != null && (qd.getMongoSort() == null || qd.getMongoSort().isEmpty())) {
			resultStream = resultList.parallelStream();
		} else {
			resultStream = resultList.stream();
		}

		ArrayList<String> nsList = new ArrayList<>();

		List<Object> results = resultStream.map(result -> {
			try {
				switch (result.getString("type")) {
				case "AggregationEvent":
					return new XMLAggregationEventReadConverter().convert(result, retDoc, envelope, nsList);

				case "ObjectEvent":
					return new XMLObjectEventReadConverter().convert(result, retDoc, envelope, nsList);

				case "TransactionEvent":
					return new XMLTransactionEventReadConverter().convert(result, retDoc, envelope, nsList);

				case "TransformationEvent":
					return new XMLTransformationEventReadConverter().convert(result, retDoc, envelope, nsList);

				case "AssociationEvent":
					return new XMLAssociationEventReadConverter().convert(result, retDoc, envelope, nsList);

				default:
					return null;
				}
			} catch (DatatypeConfigurationException e) {
				return null;
			}
		}).filter(Objects::nonNull).collect(Collectors.toList());
		return results;
	}

	public void getGTINVocabularies(RoutingContext routingContext) {
		String gtin = routingContext.pathParam("gtin");
		String epc = null;
		try {
			epc = DLConverter.generateGtin(RESTQueryServer.gcpLength, gtin);
		} catch (ValidationException e) {
			e.setReason("Invalid GS1 Element String");
			e.setStackTrace(new StackTraceElement[0]);
			HTTPUtil.sendQueryResults(routingContext.response(), e, null, null, null, e.getClass());
			return;
		}
		getVocabularies(epc, null, routingContext.response());
	}

	public void getLGTINVocabularies(RoutingContext routingContext) {
		String gtin = routingContext.pathParam("gtin");
		String lot = routingContext.pathParam("lot");
		String epc = null;
		try {
			epc = DLConverter.generateLgtin(RESTQueryServer.gcpLength, gtin, lot);
		} catch (ValidationException e) {
			e.setReason("Invalid GS1 Element String");
			e.setStackTrace(new StackTraceElement[0]);
			HTTPUtil.sendQueryResults(routingContext.response(), e, null, null, null, e.getClass());
			return;
		}
		getVocabularies(epc, null, routingContext.response());
	}

	public void getGLNVocabularies(RoutingContext routingContext) {
		String gln = routingContext.pathParam("gln");
		String ext = routingContext.pathParam("ext");
		String sglnEPC = null;
		try {
			sglnEPC = DLConverter.generateSGLN(RESTQueryServer.gcpLength, gln, ext);
		} catch (ValidationException e) {
			e.setReason("Invalid GS1 Element String");
			e.setStackTrace(new StackTraceElement[0]);
			HTTPUtil.sendQueryResults(routingContext.response(), e, null, null, null, e.getClass());
			return;
		}
		getVocabularies(sglnEPC, null, routingContext.response());
	}

	public void getPGLNVocabularies(RoutingContext routingContext) {
		String gln = routingContext.pathParam("gln");
		String pglnEPC = null;
		try {
			pglnEPC = DLConverter.generatePGLN(RESTQueryServer.gcpLength, gln);
		} catch (ValidationException e) {
			e.setReason("Invalid GS1 Element String");
			e.setStackTrace(new StackTraceElement[0]);
			HTTPUtil.sendQueryResults(routingContext.response(), e, null, null, null, e.getClass());
			return;
		}
		getVocabularies(pglnEPC, null, routingContext.response());
	}

	public void getGDTIVocabularies(RoutingContext routingContext) {
		String gdti = routingContext.pathParam("gdti");
		String gdtiEPC = null;
		try {
			gdtiEPC = DLConverter.generateGDTI(RESTQueryServer.gcpLength, gdti);
		} catch (ValidationException e) {
			e.setReason("Invalid GS1 Element String");
			e.setStackTrace(new StackTraceElement[0]);
			HTTPUtil.sendQueryResults(routingContext.response(), e, null, null, null, e.getClass());
			return;
		}
		getVocabularies(gdtiEPC, null, routingContext.response());
	}

	public void getSGTINVocabularies(RoutingContext routingContext) {
		String gtin = routingContext.pathParam("gtin");
		String serial = routingContext.pathParam("serial");
		String epc = null;
		try {
			epc = DLConverter.generateSgtin(RESTQueryServer.gcpLength, gtin, serial);
		} catch (ValidationException e) {
			e.setReason("Invalid GS1 Element String");
			e.setStackTrace(new StackTraceElement[0]);
			HTTPUtil.sendQueryResults(routingContext.response(), e, null, null, null, e.getClass());
			return;
		}
		getILMD(epc, routingContext.response());
	}

	public void getILMD(String epc, HttpServerResponse response) {

		try {
			Document retDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element envelope = retDoc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Envelope");
			envelope.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");

			Element body = retDoc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Body");

			// make query
			// epcList $in epc or outputEPCList
			// ilmd exists
			org.bson.Document q = new org.bson.Document();
			q.put("ilmd", new org.bson.Document("$exists", true));

			ArrayList<org.bson.Document> orArr = new ArrayList<org.bson.Document>();
			orArr.add(new org.bson.Document("epcList", new org.bson.Document("$in", List.of(epc))));
			orArr.add(new org.bson.Document("outputEPCList", new org.bson.Document("$in", List.of(epc))));
			q.put("$or", orArr);

			FindPublisher<org.bson.Document> query = RESTQueryServer.mEventCollection.find(q);

			ObservableSubscriber<org.bson.Document> collector = new ObservableSubscriber<org.bson.Document>();

			query.subscribe(collector);
			try {
				collector.await();
			} catch (Throwable e1) {
				ImplementationException e = new ImplementationException();
				e.setReason(e1.getMessage());
				e.setStackTrace(new StackTraceElement[0]);
				HTTPUtil.sendQueryResults(response, e, null, null, null, e.getClass());
				return;
			}

			List<org.bson.Document> resultList = collector.getReceived();

			List<Object> convertedResultList = getConvertedResultList(null, resultList, retDoc, envelope);

			QueryResults queryResults = new QueryResults();
			queryResults.setQueryName("SimpleEventQuery");

			QueryResultsBody resultsBody = new QueryResultsBody();

			EventListType elt = new EventListType();
			elt.setObjectEventOrAggregationEventOrTransformationEvent(convertedResultList);
			resultsBody.setEventList(elt);
			queryResults.setResultsBody(resultsBody);

			HTTPUtil.sendQueryResults(response.putHeader("link", "null").putHeader("rel", "last"), queryResults, retDoc,
					envelope, body, QueryResults.class);

		} catch (ParserConfigurationException e) {
			ImplementationException err = new ImplementationException();
			err.setReason(e.getMessage());
			HTTPUtil.sendQueryResults(response, err, null, null, null, err.getClass());
			RESTQueryServer.logger.error(e.getMessage());
			return;
		}
	}

	public void getVocabularies(String epc, MultiMap params, HttpServerResponse serverResponse) {
		try {
			Document retDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			QueryDescription qd = new QueryDescription(epc, params);
			Element envelope = retDoc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Envelope");
			envelope.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");

			Element body = retDoc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Body");

			FindPublisher<org.bson.Document> query = RESTQueryServer.mVocCollection.find(qd.getMongoQuery());
			if (qd.getMongoProjection() != null && !qd.getMongoProjection().isEmpty())
				query.projection(qd.getMongoProjection());

			ObservableSubscriber<org.bson.Document> collector = new ObservableSubscriber<org.bson.Document>();
			query.subscribe(collector);
			try {
				collector.await();
			} catch (Throwable e1) {
				ImplementationException e = new ImplementationException();
				e.setReason(e1.getMessage());
				e.setStackTrace(new StackTraceElement[0]);
				HTTPUtil.sendQueryResults(serverResponse, e, null, null, null, e.getClass());
				return;
			}

			List<org.bson.Document> resultList = collector.getReceived();

			if (qd.getMaxCount() != null && (resultList.size() > qd.getMaxCount())) {
				QueryTooLargeException e = new QueryTooLargeException();
				e.setReason(
						"An attempt to execute a query resulted in more data than the service was willing to provide. ( result size: "
								+ resultList.size() + " )");
				e.setStackTrace(new StackTraceElement[0]);
				HTTPUtil.sendQueryResults(serverResponse, e, null, null, null, e.getClass());
				return;
			}

			class TypeDocument {
				final String type;
				final org.bson.Document object;

				TypeDocument(org.bson.Document obj) {
					type = obj.getString("type");
					object = obj;
				}

				public String getType() {
					return type;
				}

				public org.bson.Document getDocument() {
					return object;
				}
			}

			List<VocabularyType> vList = resultList.parallelStream().map(TypeDocument::new)
					.collect(Collectors.groupingBy(TypeDocument::getType,
							Collectors.mapping(TypeDocument::getDocument, Collectors.toSet())))
					.entrySet().parallelStream()
					.map(e -> new XMLMasterDataReadConverter().convert(e.getKey(), e.getValue(), retDoc, envelope))
					.collect(Collectors.toList());

			QueryResults queryResults = new QueryResults();
			queryResults.setQueryName("SimpleMasterDataQuery");

			QueryResultsBody resultsBody = new QueryResultsBody();
			VocabularyListType vlt = new VocabularyListType();
			vlt.setVocabulary(vList);
			resultsBody.setVocabularyList(vlt);

			queryResults.setResultsBody(resultsBody);

			HTTPUtil.sendQueryResults(serverResponse, queryResults, retDoc, envelope, body, QueryResults.class);

		} catch (ParserConfigurationException | QueryParameterException | ImplementationException e) {
			ImplementationException err = new ImplementationException();
			err.setReason(e.getMessage());
			HTTPUtil.sendQueryResults(serverResponse, err, null, null, null, err.getClass());
			RESTQueryServer.logger.error(e.getMessage());
			return;
		}

	}

	
}
