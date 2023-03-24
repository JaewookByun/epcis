package org.oliot.epcis.query;

import java.util.ArrayList;
import java.util.List;

import org.oliot.epcis.converter.json.read.JSONEPCISEventReadConverter;
import org.oliot.epcis.model.exception.ImplementationException;
import org.oliot.epcis.model.exception.QueryTooLargeException;
import org.oliot.epcis.util.ObservableSubscriber;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;

import com.mongodb.reactivestreams.client.FindPublisher;

import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

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
public class SubscriptionTask implements Job {

	/**
	 * Whenever execute method invoked according to the cron expression Query the
	 * database and send the result to the destination.
	 */
	@Override
	public void execute(JobExecutionContext context) {
		JobDetail detail = context.getJobDetail();
		JobDataMap map = detail.getJobDataMap();
		Subscription sub = (Subscription) map.get("jobData");
		ServerWebSocket ws = (ServerWebSocket) map.get("webSocket");

		try {
			QueryDescription qd = sub.getQueryDescription();
			org.bson.Document mongoQuery = qd.getMongoQuery();
			if (sub.getInitialRecordTime() != null) {
				mongoQuery.put("recordTime", new org.bson.Document("$gte", sub.getInitialRecordTime()));
			}

			FindPublisher<org.bson.Document> query = RESTQueryServer.mEventCollection.find(mongoQuery);
			if (!qd.getMongoProjection().isEmpty())
				query.projection(qd.getMongoProjection());
			if (!qd.getMongoSort().isEmpty())
				query.sort(qd.getMongoSort());
			if (qd.getEventCountLimit() != null)
				query.limit(qd.getEventCountLimit());

			ObservableSubscriber<org.bson.Document> collector = new ObservableSubscriber<org.bson.Document>();

			query.subscribe(collector);
			try {
				collector.await();
			} catch (Throwable e1) {
				ImplementationException e = new ImplementationException();
				e.setReason(e1.getMessage());
				e.setStackTrace(new StackTraceElement[0]);
				ws.writeTextMessage(e.getMessage());
				return;
			}

			List<org.bson.Document> resultList = collector.getReceived();

			if (qd.getMaxCount() != null && (resultList.size() > qd.getMaxCount())) {
				QueryTooLargeException e = new QueryTooLargeException();
				e.setReason(
						"An attempt to execute a query resulted in more data than the service was willing to provide. ( result size: "
								+ resultList.size() + " )");
				e.setStackTrace(new StackTraceElement[0]);
				ws.writeTextMessage(e.getMessage());
				return;
			}

			JsonArray ctx = new JsonArray();
			ctx.add("https://gs1.github.io/EPCIS/epcis-context.jsonld");
			ArrayList<String> namespaces = JSONEPCISEventReadConverter.getNamespaces(resultList);
			JsonObject extType = new JsonObject();
			JsonObject extContext = new JsonObject();
			for (int i = 0; i < namespaces.size(); i++) {
				extContext.put("ext" + i, JSONEPCISEventReadConverter.decodeMongoObjectKey(namespaces.get(i)));
			}
			ctx.add(extContext);
			JsonArray list = RESTQueryService.getConvertedResultList(qd, resultList, namespaces, extType);
			JsonObject queryResult = new JsonObject();
			queryResult.put("@context", ctx);
			queryResult.put("isA", "EPCISQueryDocument");
			JsonObject queryResults = new JsonObject();
			queryResults.put("queryName", "SimpleEventQuery");
			queryResults.put("resultBody", new JsonObject().put("eventList", list));
			queryResult.put("epcisBody", new JsonObject().put("queryResults", queryResults));

			ws.writeTextMessage(queryResult.encodePrettily());
			RESTQueryServer.logger.debug("a scheduled query [" + qd.getMongoQuery().toJson() + sub.getSchedule() + "] sent to " + ws.textHandlerID());
			return;

		} catch (IllegalStateException e) {
			ws.writeTextMessage(e.getMessage());
		}
	}
}
