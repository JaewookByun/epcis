package org.oliot.epcis.query;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Objects;
import java.util.Timer;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.oliot.epcis.capture.common.Transaction;
import org.oliot.epcis.capture.json.JSONMessageFactory;
import org.oliot.epcis.common.Metadata;
import org.oliot.epcis.model.*;
import org.oliot.epcis.pagination.DataPage;
import org.oliot.epcis.pagination.DataPageExpiryTimerTask;
import org.oliot.epcis.pagination.NamedQueryPage;
import org.oliot.epcis.pagination.NamedQueryPageExpiryTimerTask;
import org.oliot.epcis.pagination.ResourcePage;
import org.oliot.epcis.pagination.ResourcePageExpiryTimerTask;
import org.oliot.epcis.resource.StaticResource;
import org.oliot.epcis.server.EPCISServer;
import org.oliot.epcis.util.FileUtil;
import org.oliot.epcis.util.HTTPUtil;
import org.oliot.epcis.util.SOAPMessage;
import org.oliot.epcis.util.TimeUtil;
import org.oliot.epcis.util.XMLUtil;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.impl.ConcurrentHashSet;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Copyright (C) 2020-2023. (Jaewook Byun) all rights reserved.
 * <p>
 * This project is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * SOAPQueryService provides methods for enabling Query Service.
 * <p>
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr, Associate Director, Auto-ID Labs, Korea,
 *         bjw0829@gmail.com
 */
@SuppressWarnings("unused")
public class RESTQueryService {

	final static SOAPQueryUnmarshaller soapQueryUnmarshaller = new SOAPQueryUnmarshaller();

	public static String decodeMongoObjectKey(String key) {
		key = key.replace("\uff0e", ".");
		return key;
	}

	public JsonObject retrieveContext(List<org.bson.Document> resultList) {
		// JsonArray context = new JsonArray();
		// context.add("https://ref.gs1.org/standards/epcis/2.0.0/epcis-context.jsonld");
		ArrayList<String> namespaces = getNamespaces(resultList);
		JsonObject extContext = new JsonObject();
		for (int i = 0; i < namespaces.size(); i++) {
			extContext.put("ext" + i, decodeMongoObjectKey(namespaces.get(i)));
		}
		return extContext;
	}

	public void query(RoutingContext routingContext, JsonObject query, String queryName) {

		try {
			if (queryName.equals("SimpleEventQuery"))
				pollEvents(routingContext, query);
			else
				pollVocabularies(routingContext, query);
		} catch (QueryParameterException | ValidationException e) {
			HTTPUtil.sendQueryResults(routingContext.response(),
					JSONMessageFactory.get406NotAcceptableException(
							"[406NotAcceptable] The server cannot return the response as requested: " + e.getReason()),
					406);
		} catch (ImplementationException e) {
			HTTPUtil.sendQueryResults(routingContext.response(),
					JSONMessageFactory.get500ImplementationException(
							"[500ImplementationException] The server cannot return the response as requested: "
									+ e.getReason()),
					500);
		} catch (Exception e) {
			HTTPUtil.sendQueryResults(routingContext.response(),
					JSONMessageFactory.get500ImplementationException(
							"[500ImplementationException] The server cannot return the response as requested: "
									+ e.getMessage()),
					500);
		}
	}

	public void query(RoutingContext routingContext, org.bson.Document namedQuery, String queryName) {

		try {
			if (queryName.equals("SimpleEventQuery"))
				pollEvents(routingContext, namedQuery);
			else
				pollVocabularies(routingContext, namedQuery);
		} catch (QueryParameterException | ValidationException e) {
			HTTPUtil.sendQueryResults(routingContext.response(),
					JSONMessageFactory.get406NotAcceptableException(
							"[406NotAcceptable] The server cannot return the response as requested: " + e.getReason()),
					406);
		} catch (ImplementationException e) {
			HTTPUtil.sendQueryResults(routingContext.response(),
					JSONMessageFactory.get500ImplementationException(
							"[500ImplementationException] The server cannot return the response as requested: "
									+ e.getReason()),
					500);
		} catch (Exception e) {
			HTTPUtil.sendQueryResults(routingContext.response(),
					JSONMessageFactory.get500ImplementationException(
							"[500ImplementationException] The server cannot return the response as requested: "
									+ e.getMessage()),
					500);
		}
	}

	public void getXMLQuery(RoutingContext routingContext, String queryName) {

		org.bson.Document namedQueryDocument = EPCISServer.mNamedQueryCollection
				.find(new org.bson.Document().append("id", queryName)).first();

		if (namedQueryDocument == null || namedQueryDocument.isEmpty()) {
			EPCISException e = new EPCISException("[404ResourceNotFoundException] " + queryName);
			EPCISServer.logger.error("[404ResourceNotFoundException] " + queryName);
			HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(), 404);
			return;
		}

		Document result = QueryDescription.toXMLCreateQuery(namedQueryDocument);
		try {
			HTTPUtil.sendQueryResults(routingContext.response(), XMLUtil.toString(result, false), 200,
					"application/xml");
		} catch (TransformerException e) {
			// not happen
			EPCISException e1 = new EPCISException("[500ImplementationException] " + e.getMessage());
			EPCISServer.logger.error("[500ImplementationException] " + e.getMessage());
			HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e1, e1.getClass(), 500);
			return;
		}

	}

	public void getXMLQueries(HttpServerRequest serverRequest, HttpServerResponse serverResponse,
			ConcurrentHashMap<UUID, NamedQueryPage> pages, List<org.bson.Document> namedQueryDocuments) {

		// get perPage
		int perPage;

		try {
			perPage = getPerPage(serverRequest);
		} catch (QueryParameterException e) {
			HTTPUtil.sendQueryResults(serverResponse, new SOAPMessage(), e, e.getClass(), 400);
			return;
		}

		NamedQueryPage message = new NamedQueryPage(namedQueryDocuments);
		String result;
		try {
			result = message.getXMLNextPage(perPage);
		} catch (ParserConfigurationException e) {
			EPCISException e1 = new EPCISException("[500ImplementationException] " + e.getMessage());
			EPCISServer.logger.error("[500ImplementationException] " + e.getMessage());
			HTTPUtil.sendQueryResults(serverResponse, new SOAPMessage(), e1, e1.getClass(), 500);
			return;
		}
		if (!message.isClosed()) {
			UUID uuid;
			long currentTime = System.currentTimeMillis();

			while (true) {
				uuid = UUID.randomUUID();
				if (!pages.containsKey(uuid))
					break;
			}

			Timer timer = new Timer();
			message.setTimer(timer);
			timer.schedule(new NamedQueryPageExpiryTimerTask("GET /queries", pages, uuid, EPCISServer.logger),
					Metadata.GS1_Next_Page_Token_Expires);
			pages.put(uuid, message);
			EPCISServer.logger.debug("[GET /queries] page - " + uuid + " added. # remaining pages - " + pages.size());

			serverResponse.putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-Extensions", Metadata.GS1_Extensions).putHeader("Link", uuid.toString())
					.putHeader("GS1-Next-Page-Token-Expires",
							TimeUtil.getDateTimeStamp(currentTime + Metadata.GS1_Next_Page_Token_Expires));
			HTTPUtil.sendQueryResults(serverResponse, result, 200, "application/xml");
		} else {
			HTTPUtil.sendQueryResults(serverResponse, result, 200, "application/xml");
		}
	}

	public void getNextXMLQueries(HttpServerRequest serverRequest, HttpServerResponse serverResponse,
			ConcurrentHashMap<UUID, NamedQueryPage> pages, UUID uuid) {

		// get perPage
		int perPage;

		try {
			perPage = getPerPage(serverRequest);
		} catch (QueryParameterException e) {
			HTTPUtil.sendQueryResults(serverResponse, new SOAPMessage(), e, e.getClass(), 400);
			return;
		}

		pages.get(uuid);

		NamedQueryPage page = null;
		if (!pages.containsKey(uuid)) {
			EPCISException e = new EPCISException(
					"[406NotAcceptable] The given next page token does not exist or be no longer available.");
			EPCISServer.logger.error(e.getReason());
			HTTPUtil.sendQueryResults(serverResponse, new SOAPMessage(), e, e.getClass(), 406);
			return;
		} else {
			page = pages.get(uuid);
		}

		String result = null;
		try {
			result = page.getXMLNextPage(perPage);
		} catch (ParserConfigurationException e) {
			EPCISException e1 = new EPCISException("[500ImplementationException] " + e.getMessage());
			EPCISServer.logger.error("[500ImplementationException] " + e.getMessage());
			HTTPUtil.sendQueryResults(serverResponse, new SOAPMessage(), e1, e1.getClass(), 500);
			return;
		}

		if (!page.isClosed()) {
			long currentTime = System.currentTimeMillis();
			Timer timer = page.getTimer();
			if (timer != null)
				timer.cancel();

			Timer newTimer = new Timer();
			page.setTimer(newTimer);
			newTimer.schedule(new NamedQueryPageExpiryTimerTask("GET /queries", pages, uuid, EPCISServer.logger),
					Metadata.GS1_Next_Page_Token_Expires);
			EPCISServer.logger.debug("[GET /queries] page - " + uuid + " token expiry time extended to "
					+ TimeUtil.getDateTimeStamp(currentTime + Metadata.GS1_Next_Page_Token_Expires));

			serverResponse.putHeader("Link", uuid.toString()).putHeader("GS1-Next-Page-Token-Expires",
					TimeUtil.getDateTimeStamp(currentTime + Metadata.GS1_Next_Page_Token_Expires));
			HTTPUtil.sendQueryResults(serverResponse, result, 200, "application/xml");

		} else {
			pages.remove(uuid);
			EPCISServer.logger.debug("[GET /queries] page - " + uuid + " expired. # remaining pages - " + pages.size());
			HTTPUtil.sendQueryResults(serverResponse, result, 200, "application/xml");
		}
	}

	public void getJSONQueries(HttpServerRequest serverRequest, HttpServerResponse serverResponse,
			ConcurrentHashMap<UUID, NamedQueryPage> pages, List<org.bson.Document> namedQueryDocuments) {

		// get perPage
		int perPage;

		try {
			perPage = getPerPage(serverRequest);
		} catch (QueryParameterException e) {
			HTTPUtil.sendQueryResults(serverResponse,
					JSONMessageFactory.get406NotAcceptableException(
							"[406NotAcceptable] The server cannot return the response as requested: " + e.getReason()),
					406);
			return;
		}

		NamedQueryPage message = new NamedQueryPage(namedQueryDocuments);
		String result = message.getJSONNextPage(perPage);
		if (!message.isClosed()) {
			UUID uuid;
			long currentTime = System.currentTimeMillis();

			while (true) {
				uuid = UUID.randomUUID();
				if (!pages.containsKey(uuid))
					break;
			}

			Timer timer = new Timer();
			message.setTimer(timer);
			timer.schedule(new NamedQueryPageExpiryTimerTask("GET /queries", pages, uuid, EPCISServer.logger),
					Metadata.GS1_Next_Page_Token_Expires);
			pages.put(uuid, message);
			EPCISServer.logger.debug("[GET /queries] page - " + uuid + " added. # remaining pages - " + pages.size());

			serverResponse.putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-Extensions", Metadata.GS1_Extensions).putHeader("Link", uuid.toString())
					.putHeader("GS1-Next-Page-Token-Expires",
							TimeUtil.getDateTimeStamp(currentTime + Metadata.GS1_Next_Page_Token_Expires));
			HTTPUtil.sendQueryResults(serverResponse, result, 200, "application/json");
		} else {
			HTTPUtil.sendQueryResults(serverResponse, result, 200, "application/json");
		}
	}

	public void getNextJSONQueries(HttpServerRequest serverRequest, HttpServerResponse serverResponse,
			ConcurrentHashMap<UUID, NamedQueryPage> pages, UUID uuid) {

		// get perPage
		int perPage;

		try {
			perPage = getPerPage(serverRequest);
		} catch (QueryParameterException e) {
			HTTPUtil.sendQueryResults(serverResponse,
					JSONMessageFactory.get406NotAcceptableException(
							"[406NotAcceptable] The server cannot return the response as requested: " + e.getReason()),
					406);
			return;
		}

		pages.get(uuid);

		NamedQueryPage page = null;
		if (!pages.containsKey(uuid)) {
			HTTPUtil.sendQueryResults(serverResponse,
					JSONMessageFactory.get406NotAcceptableException(
							"[406NotAcceptable] The given next page token does not exist or be no longer available."),
					406);
			EPCISServer.logger
					.error("[406NotAcceptable] The given next page token does not exist or be no longer available.");
			return;
		} else {
			page = pages.get(uuid);
		}

		String result = page.getJSONNextPage(perPage);

		if (!page.isClosed()) {
			long currentTime = System.currentTimeMillis();
			Timer timer = page.getTimer();
			if (timer != null)
				timer.cancel();

			Timer newTimer = new Timer();
			page.setTimer(newTimer);
			newTimer.schedule(new NamedQueryPageExpiryTimerTask("GET /queries", pages, uuid, EPCISServer.logger),
					Metadata.GS1_Next_Page_Token_Expires);
			EPCISServer.logger.debug("[GET /queries] page - " + uuid + " token expiry time extended to "
					+ TimeUtil.getDateTimeStamp(currentTime + Metadata.GS1_Next_Page_Token_Expires));

			serverResponse.putHeader("Link", uuid.toString()).putHeader("GS1-Next-Page-Token-Expires",
					TimeUtil.getDateTimeStamp(currentTime + Metadata.GS1_Next_Page_Token_Expires));
			HTTPUtil.sendQueryResults(serverResponse, result, 200, "application/json");

		} else {
			pages.remove(uuid);
			EPCISServer.logger.debug("[GET /queries] page - " + uuid + " expired. # remaining pages - " + pages.size());
			HTTPUtil.sendQueryResults(serverResponse, result, 200, "application/json");
		}
	}

	public void deleteXMLQuery(RoutingContext routingContext, String queryName) {

		DeleteResult dr = EPCISServer.mNamedQueryCollection.deleteOne(new org.bson.Document().append("id", queryName));

		long cnt = dr.getDeletedCount();
		if (cnt == 0) {
			EPCISException e = new EPCISException("[404ResourceNotFoundException] " + queryName);
			EPCISServer.logger.error("[404ResourceNotFoundException] " + queryName);
			HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(), 404);
			return;
		} else {
			// remove all the related subscriptions
			MongoCursor<org.bson.Document> cursor = EPCISServer.mSubscriptionCollection
					.find(new org.bson.Document().append("namedQuery", queryName)).iterator();
			while (cursor.hasNext()) {
				org.bson.Document next = cursor.next();
				String subscriptionID = next.getString("_id");
				String trigger = next.getString("trigger");

				if (trigger == null) {
					try {
						SubscriptionManager.sched
								.unscheduleJob(org.quartz.TriggerKey.triggerKey(subscriptionID, "SimpleEventQuery"));
						SubscriptionManager.sched
								.deleteJob(org.quartz.JobKey.jobKey(subscriptionID, "SimpleEventQuery"));
						EPCISServer.logger.debug("Subscription " + subscriptionID + " is removed from scheduler");
					} catch (SchedulerException e) {
						EPCISException e1 = new EPCISException(e.getMessage());
						EPCISServer.logger.error(e.getMessage());
						HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e1, e1.getClass(), 404);
						return;
					}
				} else {
					Enumeration<TriggerDescription> enumeration = TriggerEngine.triggerSubscription.keys();
					TriggerDescription tdd = null;
					while (enumeration.hasMoreElements()) {
						TriggerDescription elem = enumeration.nextElement();
						if (elem.getSubscriptionID().equals(subscriptionID)) {
							tdd = elem;
							break;
						}
					}
					if (tdd != null) {
						TriggerEngine.triggerSubscription.remove(tdd);
						EPCISServer.logger.debug("Subscription " + subscriptionID + " is removed from trigger engine");
					}
				}

			}
			DeleteResult deleteResult = EPCISServer.mSubscriptionCollection
					.deleteMany(new org.bson.Document().append("namedQuery", queryName));

			routingContext.response().putHeader("Content-Type", "application/xml").setStatusCode(204).end();
			return;
		}
	}

	public void deleteJSONQuery(RoutingContext routingContext, String queryName) {

		DeleteResult dr = EPCISServer.mNamedQueryCollection.deleteOne(new org.bson.Document().append("id", queryName));

		long cnt = dr.getDeletedCount();
		if (cnt == 0) {
			HTTPUtil.sendQueryResults(routingContext.response(),
					JSONMessageFactory.get404NoSuchResourceException("[404] Named Query Not Found: " + queryName), 404);
			return;
		} else {
			// remove all the related subscriptions
			MongoCursor<org.bson.Document> cursor = EPCISServer.mSubscriptionCollection
					.find(new org.bson.Document().append("namedQuery", queryName)).iterator();
			while (cursor.hasNext()) {
				org.bson.Document next = cursor.next();
				String subscriptionID = next.getString("_id");
				String trigger = next.getString("trigger");

				if (trigger == null) {
					try {
						SubscriptionManager.sched
								.unscheduleJob(org.quartz.TriggerKey.triggerKey(subscriptionID, "SimpleEventQuery"));
						SubscriptionManager.sched
								.deleteJob(org.quartz.JobKey.jobKey(subscriptionID, "SimpleEventQuery"));
						EPCISServer.logger.debug("Subscription " + subscriptionID + " is removed from scheduler");
					} catch (SchedulerException e) {
						HTTPUtil.sendQueryResults(routingContext.response(),
								JSONMessageFactory.get500ImplementationException(e.getMessage()), 500);
						return;
					}
				} else {
					Enumeration<TriggerDescription> enumeration = TriggerEngine.triggerSubscription.keys();
					TriggerDescription tdd = null;
					while (enumeration.hasMoreElements()) {
						TriggerDescription elem = enumeration.nextElement();
						if (elem.getSubscriptionID().equals(subscriptionID)) {
							tdd = elem;
							break;
						}
					}
					if (tdd != null) {
						TriggerEngine.triggerSubscription.remove(tdd);
						EPCISServer.logger.debug("Subscription " + subscriptionID + " is removed from trigger engine");
					}
				}

			}
			DeleteResult deleteResult = EPCISServer.mSubscriptionCollection
					.deleteMany(new org.bson.Document().append("namedQuery", queryName));

			routingContext.response().putHeader("Content-Type", "application/json").setStatusCode(204).end();
			return;
		}
	}

	public void deleteXMLSubscription(RoutingContext routingContext, String queryName, String subscriptionID) {

		org.bson.Document dr = EPCISServer.mSubscriptionCollection.findOneAndDelete(
				new org.bson.Document().append("_id", subscriptionID).append("namedQuery", queryName));

		if (dr == null) {
			EPCISException e = new EPCISException("[404ResourceNotFoundException] " + subscriptionID);
			EPCISServer.logger.error("[404ResourceNotFoundException] " + subscriptionID);
			HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(), 404);
			return;
		} else {
			if (dr.getString("trigger") == null) {
				try {
					SubscriptionManager.sched
							.unscheduleJob(org.quartz.TriggerKey.triggerKey(subscriptionID, "SimpleEventQuery"));
					SubscriptionManager.sched.deleteJob(org.quartz.JobKey.jobKey(subscriptionID, "SimpleEventQuery"));
					EPCISServer.logger.debug("Subscription " + subscriptionID + " is removed from scheduler");
					routingContext.response().putHeader("Content-Type", "application/xml").setStatusCode(204).end();
				} catch (SchedulerException e) {
					EPCISException e1 = new EPCISException(e.getMessage());
					EPCISServer.logger.error(e.getMessage());
					HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e1, e1.getClass(), 404);
					return;
				}
			} else {
				Enumeration<TriggerDescription> enumeration = TriggerEngine.triggerSubscription.keys();
				TriggerDescription tdd = null;
				while (enumeration.hasMoreElements()) {
					TriggerDescription elem = enumeration.nextElement();
					if (elem.getSubscriptionID().equals(subscriptionID)) {
						tdd = elem;
						break;
					}
				}
				if (tdd != null) {
					TriggerEngine.triggerSubscription.remove(tdd);
					EPCISServer.logger.debug("Subscription " + subscriptionID + " is removed from trigger engine");
					routingContext.response().putHeader("Content-Type", "application/xml").setStatusCode(204).end();
				}
			}
			return;
		}
	}

	public void deleteJSONSubscription(RoutingContext routingContext, String queryName, String subscriptionID) {

		org.bson.Document dr = EPCISServer.mSubscriptionCollection.findOneAndDelete(
				new org.bson.Document().append("_id", subscriptionID).append("namedQuery", queryName));

		if (dr == null) {
			HTTPUtil.sendQueryResults(routingContext.response(),
					JSONMessageFactory.get404NoSuchResourceException("[404] subscription not found: " + subscriptionID),
					404);
			return;
		} else {
			if (dr.getString("trigger") == null) {
				try {
					SubscriptionManager.sched
							.unscheduleJob(org.quartz.TriggerKey.triggerKey(subscriptionID, "SimpleEventQuery"));
					SubscriptionManager.sched.deleteJob(org.quartz.JobKey.jobKey(subscriptionID, "SimpleEventQuery"));
					EPCISServer.logger.debug("Subscription " + subscriptionID + " is removed from scheduler");
					routingContext.response().putHeader("Content-Type", "application/json").setStatusCode(204).end();
				} catch (SchedulerException e) {
					HTTPUtil.sendQueryResults(routingContext.response(),
							JSONMessageFactory.get500ImplementationException(e.getMessage()), 500);
					return;
				}
			} else {
				Enumeration<TriggerDescription> enumeration = TriggerEngine.triggerSubscription.keys();
				TriggerDescription tdd = null;
				while (enumeration.hasMoreElements()) {
					TriggerDescription elem = enumeration.nextElement();
					if (elem.getSubscriptionID().equals(subscriptionID)) {
						tdd = elem;
						break;
					}
				}
				if (tdd != null) {
					TriggerEngine.triggerSubscription.remove(tdd);
					EPCISServer.logger.debug("Subscription " + subscriptionID + " is removed from trigger engine");
					routingContext.response().putHeader("Content-Type", "application/json").setStatusCode(204).end();
				}
			}
			return;
		}
	}

	public void getJSONQuery(RoutingContext routingContext, String queryName) {

		org.bson.Document namedQueryDocument = EPCISServer.mNamedQueryCollection
				.find(new org.bson.Document().append("id", queryName)).first();

		if (namedQueryDocument == null || namedQueryDocument.isEmpty()) {
			HTTPUtil.sendQueryResults(routingContext.response(),
					JSONMessageFactory.get404NoSuchResourceException("[404] Named Query Not Found: " + queryName), 404);
			return;
		}

		JsonObject result = QueryDescription.toJSONCreateQuery(namedQueryDocument);
		HTTPUtil.sendQueryResults(routingContext.response(), result.toString(), 200, "application/json");
	}

	public void getJSONQueries(RoutingContext routingContext) {
		MongoCursor<org.bson.Document> cursor = EPCISServer.mNamedQueryCollection.find().iterator();

		JsonArray createQueries = new JsonArray();

		while (cursor.hasNext()) {
			org.bson.Document namedQueryDocument = cursor.next();
			JsonObject createQuery = QueryDescription.toJSONCreateQuery(namedQueryDocument);
			createQueries.add(createQuery);
		}

		HTTPUtil.sendQueryResults(routingContext.response(), createQueries.toString(), 200, "application/json");
	}

	public void postQuery(RoutingContext routingContext, Poll poll) {

		QueryDescription qd;
		try {
			qd = new QueryDescription(poll, RESTQueryService.soapQueryUnmarshaller);

			List<String> givenQueryNames = routingContext.queryParam("name");

			String id = null;
			if (givenQueryNames == null || givenQueryNames.isEmpty()) {
				EPCISException e = new EPCISException("[406NotAcceptable] the name of given named query is empty");
				EPCISServer.logger.error("[406NotAcceptable] the name of given named query is empty");
				HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(), 406);
				return;
			} else {
				try {
					String givenQueryName = givenQueryNames.get(0);
					org.bson.Document first = EPCISServer.mNamedQueryCollection
							.find(new org.bson.Document("id", givenQueryName)).first();
					if (first != null) {
						EPCISException e = new EPCISException("[409] Existing named query with id: " + givenQueryName);
						EPCISServer.logger.error("[409] Existing query name");
						HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(), 409);
						return;
					} else {
						id = givenQueryName;
					}
				} catch (Exception e) {
					EPCISServer.logger.error(e.getMessage());
					HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(), 500);
					return;
				}
			}

			EPCISServer.mNamedQueryCollection.insertOne(qd.toMongoDocument().append("id", id));
			EPCISServer.logger.error("Named query is inserted with id: " + id);

			HTTPUtil.sendQueryEmptyResults(routingContext.response().putHeader("location", id), 201);

		} catch (QueryParameterException e) {
			EPCISServer.logger.error(e.getReason());
			HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(), 406);
			return;
		} catch (ImplementationException e) {
			EPCISServer.logger.error(e.getReason());
			HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(), 500);
			return;
		}

	}

	public void postQuery(RoutingContext routingContext, JsonObject createQuery) {

		QueryDescription qd;
		try {
			JsonObject q = createQuery.getJsonObject("query");
			qd = new QueryDescription(q);

			String id = createQuery.getString("name", null);
			if (id == null) {
				HTTPUtil.sendQueryResults(routingContext.response(), JSONMessageFactory.get406NotAcceptableException(
						"[406NotAcceptable] the name of given named query is empty"), 406);
				return;
			} else {
				try {

					org.bson.Document first = EPCISServer.mNamedQueryCollection.find(new org.bson.Document("id", id))
							.first();
					if (first != null) {
						HTTPUtil.sendQueryResults(routingContext.response(), JSONMessageFactory
								.get409ResourceAlreadyExistsException("[409] Existing named query with id: " + id),
								406);
						return;
					}
				} catch (Exception e) {
					HTTPUtil.sendQueryResults(routingContext.response(),
							JSONMessageFactory.get500ImplementationException(e.getMessage()), 500);
					return;
				}
			}

			EPCISServer.mNamedQueryCollection.insertOne(qd.toMongoDocument().append("id", id));
			EPCISServer.logger.error("Named query is inserted with id: " + id);

			HTTPUtil.sendQueryEmptyResults(routingContext.response().putHeader("location", id), 201);

		} catch (QueryParameterException e) {
			HTTPUtil.sendQueryResults(routingContext.response(),
					JSONMessageFactory.get406NotAcceptableException("[406NotAcceptable] " + e), 406);
			return;
		} catch (ImplementationException e) {
			HTTPUtil.sendQueryResults(routingContext.response(),
					JSONMessageFactory.get500ImplementationException("[500ImplementationException] " + e), 500);
			return;
		} catch (ValidationException e) {
			HTTPUtil.sendQueryResults(routingContext.response(),
					JSONMessageFactory.get406NotAcceptableException("[406NotAcceptable] " + e), 406);
			return;
		} catch (NullPointerException e) {
			HTTPUtil.sendQueryResults(routingContext.response(),
					JSONMessageFactory.get406NotAcceptableException("[406NotAcceptable] " + e), 406);
			return;
		} catch (Exception e) {
			HTTPUtil.sendQueryResults(routingContext.response(),
					JSONMessageFactory.get500ImplementationException("[500ImplementationException] " + e), 500);
			return;
		}

	}

	public void pollEvents(RoutingContext routingContext, JsonObject query)
			throws QueryParameterException, ImplementationException, Exception, ValidationException {
		// get perPage
		int perPage;

		try {
			perPage = getPerPage(routingContext.request());
		} catch (QueryParameterException e) {
			throw e;
		}

		// create query description
		QueryDescription qd = null;
		try {
			qd = new QueryDescription(query, "SimpleEventQuery");
		} catch (QueryParameterException e1) {
			throw e1;
		} catch (ImplementationException e2) {
			throw e2;
		} catch (Exception e3) {
			throw e3;
		}
		invokeSimpleEventQuery(routingContext.response(), qd, perPage);
	}

	public void pollEvents(RoutingContext routingContext, org.bson.Document namedQuery)
			throws QueryParameterException, ImplementationException, Exception, ValidationException {
		// get perPage
		int perPage;

		try {
			perPage = getPerPage(routingContext.request());
		} catch (QueryParameterException e) {
			throw e;
		}

		invokeSimpleEventQuery(routingContext.response(), namedQuery, perPage);
	}

	public void pollVocabularies(RoutingContext routingContext, JsonObject query)
			throws QueryParameterException, ImplementationException, Exception, ValidationException {

		// get perPage
		int perPage;

		try {
			perPage = getPerPage(routingContext.request());
		} catch (QueryParameterException e) {
			throw e;
		}

		// create query description
		QueryDescription qd = null;
		try {
			qd = new QueryDescription(query, "SimpleMasterDataQuery");
		} catch (QueryParameterException e1) {
			throw e1;
		} catch (ImplementationException e2) {
			throw e2;
		} catch (Exception e3) {
			throw e3;
		}
		invokeSimpleMasterDataQuery(routingContext.response(), qd, perPage);
	}

	public void pollVocabularies(RoutingContext routingContext, org.bson.Document namedQuery)
			throws QueryParameterException, ImplementationException, Exception, ValidationException {

		// get perPage
		int perPage;

		try {
			perPage = getPerPage(routingContext.request());
		} catch (QueryParameterException e) {
			throw e;
		}

		invokeSimpleMasterDataQuery(routingContext.response(), namedQuery, perPage);
	}

	public void getNextEventPage(RoutingContext routingContext, UUID uuid) {

		HttpServerRequest serverRequest = routingContext.request();
		HttpServerResponse serverResponse = routingContext.response();
		// get perPage
		int perPage;

		try {
			perPage = getPerPage(serverRequest);
		} catch (QueryParameterException e) {
			HTTPUtil.sendQueryResults(routingContext.response(),
					JSONMessageFactory.get406NotAcceptableException(
							"[406NotAcceptable] The server cannot return the response as requested: " + e.getReason()),
					406);
			return;
		}

		DataPage page = null;
		if (!EPCISServer.eventPageMap.containsKey(uuid)) {
			HTTPUtil.sendQueryResults(routingContext.response(),
					JSONMessageFactory.get406NotAcceptableException(
							"[406NotAcceptable] The given next page token does not exist or be no longer available."),
					406);
			return;
		} else {
			page = EPCISServer.eventPageMap.get(uuid);
		}

		org.bson.Document query = (org.bson.Document) page.getQuery();
		org.bson.Document sort = (org.bson.Document) page.getSort();
		int skip = page.getSkip();
		Integer limit = page.getLimit();
		int qLimit;
		page.setSkip(skip + perPage);

		boolean needPagination = true;

		if (limit != null) {
			if (perPage < limit - skip) {
				qLimit = perPage + 1;
			} else {
				qLimit = limit - skip;
				needPagination = false;
			}
		} else {
			qLimit = perPage + 1;
		}

		List<org.bson.Document> results = new ArrayList<org.bson.Document>();

		try {
			EPCISServer.mEventCollection.find(query).sort(sort).skip(skip).limit(qLimit).into(results);
		} catch (Throwable e) {
			HTTPUtil.sendQueryResults(serverResponse,
					JSONMessageFactory.get500ImplementationException(
							"[500ImplementationException] The server cannot return the response as requested: "
									+ e.getMessage()),
					500);
			return;
		}

		JsonArray context = new JsonArray();
		context.add("https://ref.gs1.org/standards/epcis/2.0.0/epcis-context.jsonld");
		ArrayList<String> namespaces = getNamespaces(results);
		JsonObject extType = new JsonObject();
		JsonObject extContext = new JsonObject();
		for (int i = 0; i < namespaces.size(); i++) {
			extContext.put("ext" + i, decodeMongoObjectKey(namespaces.get(i)));
		}
		context.add(extContext);
		// conversion
		List<JsonObject> convertedResultList = getConvertedResultList(isResultSorted(page.getSort()), results,
				namespaces, extType);
		context.add(extType);
		if (needPagination == true && perPage >= convertedResultList.size()) {
			needPagination = false;
		} else if (needPagination == true && perPage < convertedResultList.size()) {
			if (!convertedResultList.isEmpty())
				convertedResultList = convertedResultList.subList(0, perPage);
		}

		JsonObject queryResultDocument = StaticResource.simpleEventQueryResults.copy();
		JsonArray eventList = queryResultDocument.getJsonObject("epcisBody").getJsonObject("queryResults")
				.getJsonObject("resultsBody").getJsonArray("eventList");
		for (JsonObject list : convertedResultList) {
			eventList.add(list);
		}
		queryResultDocument.put("@context", context);

		if (needPagination) {
			long currentTime = System.currentTimeMillis();
			Timer timer = page.getTimer();
			if (timer != null)
				timer.cancel();

			Timer newTimer = new Timer();
			page.setTimer(newTimer);
			newTimer.schedule(
					new DataPageExpiryTimerTask("GET /events", EPCISServer.eventPageMap, uuid, EPCISServer.logger),
					Metadata.GS1_Next_Page_Token_Expires);
			EPCISServer.logger.debug("[GET /events] page - " + uuid + " token expiry time extended to "
					+ TimeUtil.getDateTimeStamp(currentTime + Metadata.GS1_Next_Page_Token_Expires));

			serverResponse.putHeader("Link", uuid.toString()).putHeader("GS1-Next-Page-Token-Expires",
					TimeUtil.getDateTimeStamp(currentTime + Metadata.GS1_Next_Page_Token_Expires));
			HTTPUtil.sendQueryResults(serverResponse, queryResultDocument, 200);
		} else {
			EPCISServer.eventPageMap.remove(uuid);
			EPCISServer.logger.debug("[GET /events] page - " + uuid + " expired. # remaining pages - "
					+ EPCISServer.eventPageMap.size());
			HTTPUtil.sendQueryResults(serverResponse, queryResultDocument, 200);
		}
	}

	private void invokeSimpleEventQuery(HttpServerResponse serverResponse, QueryDescription qd, int perPage) {

		// CREATE MONGODB QUERY
		FindIterable<org.bson.Document> query = EPCISServer.mEventCollection.find(qd.getMongoQuery());

		if (!qd.getMongoSort().isEmpty())
			query.sort(qd.getMongoSort());

		boolean needPagination = true;
		int qLimit = perPage;
		if (qd.getMaxCount() != null && qd.getMaxCount() > perPage) {
			qLimit = qd.getMaxCount();
		} else {
			qLimit = perPage;
		}
		if (qd.getEventCountLimit() != null) {
			if (qLimit < qd.getEventCountLimit()) {
				query.limit(qLimit + 1);
			} else {
				query.limit(qd.getEventCountLimit());
				needPagination = false;
			}
		} else {
			query.limit(qLimit + 1);
		}

		// RETRIEVE
		List<org.bson.Document> resultList = new ArrayList<org.bson.Document>();
		try {
			query.into(resultList);
		} catch (Throwable e1) {
			HTTPUtil.sendQueryResults(serverResponse,
					JSONMessageFactory.get500ImplementationException(
							"[500ImplementationException] The server cannot return the response as requested: "
									+ e1.getMessage()),
					500);
			return;
		}

		if (qd.getMaxCount() != null && (resultList.size() > qd.getMaxCount())) {
			HTTPUtil.sendQueryResults(serverResponse, JSONMessageFactory.get413QueryTooLargeException(
					"[413QueryTooLargeException] The server cannot return the response as requested: "
							+ "An attempt to execute a query resulted in more data than the service was willing to provide. ( result size: "
							+ resultList.size() + " )"),
					413);
			return;
		}

		JsonArray context = new JsonArray();
		context.add("https://ref.gs1.org/standards/epcis/2.0.0/epcis-context.jsonld");
		ArrayList<String> namespaces = getNamespaces(resultList);
		JsonObject extType = new JsonObject();
		JsonObject extContext = new JsonObject();
		for (int i = 0; i < namespaces.size(); i++) {
			extContext.put("ext" + i, decodeMongoObjectKey(namespaces.get(i)));
		}
		context.add(extContext);
		// conversion
		List<JsonObject> convertedResultList = getConvertedResultList(isResultSorted(qd), resultList, namespaces,
				extType);
		context.add(extType);
		if (needPagination == true && perPage >= convertedResultList.size()) {
			needPagination = false;
		} else if (needPagination == true && perPage < convertedResultList.size()) {
			if (!convertedResultList.isEmpty())
				convertedResultList = convertedResultList.subList(0, perPage);
		}

		JsonObject queryResultDocument = StaticResource.simpleEventQueryResults.copy();
		JsonArray eventList = queryResultDocument.getJsonObject("epcisBody").getJsonObject("queryResults")
				.getJsonObject("resultsBody").getJsonArray("eventList");
		for (JsonObject list : convertedResultList) {
			eventList.add(list);
		}
		queryResultDocument.put("@context", context);

		if (needPagination) {
			UUID uuid;
			long currentTime = System.currentTimeMillis();

			while (true) {
				uuid = UUID.randomUUID();
				if (!EPCISServer.eventPageMap.containsKey(uuid))
					break;
			}

			DataPage page = new DataPage(uuid, "SimpleEventQuery", qd.getMongoQuery(), null, qd.getMongoSort(),
					qd.getEventCountLimit(), perPage);
			Timer timer = new Timer();
			page.setTimer(timer);
			timer.schedule(
					new DataPageExpiryTimerTask("GET /events", EPCISServer.eventPageMap, uuid, EPCISServer.logger),
					Metadata.GS1_Next_Page_Token_Expires);
			EPCISServer.eventPageMap.put(uuid, page);
			EPCISServer.logger.debug(
					"[GET /events] page - " + uuid + " added. # remaining pages - " + EPCISServer.eventPageMap.size());

			serverResponse.putHeader("Link", uuid.toString()).putHeader("GS1-Next-Page-Token-Expires",
					TimeUtil.getDateTimeStamp(currentTime + Metadata.GS1_Next_Page_Token_Expires));

			HTTPUtil.sendQueryResults(serverResponse, queryResultDocument, 200);
		} else {
			HTTPUtil.sendQueryResults(serverResponse, queryResultDocument, 200);
		}
	}

	private void invokeSimpleEventQuery(HttpServerResponse serverResponse, org.bson.Document namedQuery, int perPage) {

		// CREATE MONGODB QUERY
		org.bson.Document q = namedQuery.get("query", org.bson.Document.class);
		FindIterable<org.bson.Document> query = EPCISServer.mEventCollection.find(q);

		org.bson.Document sort = namedQuery.get("sort", org.bson.Document.class);
		if (sort != null && !sort.isEmpty())
			query.sort(sort);

		boolean needPagination = true;
		int qLimit = perPage;
		Integer maxCount = namedQuery.getInteger("maxCount");
		if (maxCount != null && maxCount > perPage) {
			qLimit = maxCount;
		} else {
			qLimit = perPage;
		}

		Integer eventCountLimit = namedQuery.getInteger("eventCountLimit");
		if (eventCountLimit != null) {
			if (qLimit < eventCountLimit) {
				query.limit(qLimit + 1);
			} else {
				query.limit(eventCountLimit);
				needPagination = false;
			}
		} else {
			query.limit(qLimit + 1);
		}

		// RETRIEVE
		List<org.bson.Document> resultList = new ArrayList<org.bson.Document>();
		try {
			query.into(resultList);
		} catch (Throwable e1) {
			HTTPUtil.sendQueryResults(serverResponse,
					JSONMessageFactory.get500ImplementationException(
							"[500ImplementationException] The server cannot return the response as requested: "
									+ e1.getMessage()),
					500);
			return;
		}

		if (maxCount != null && (resultList.size() > maxCount)) {
			HTTPUtil.sendQueryResults(serverResponse, JSONMessageFactory.get413QueryTooLargeException(
					"[413QueryTooLargeException] The server cannot return the response as requested: "
							+ "An attempt to execute a query resulted in more data than the service was willing to provide. ( result size: "
							+ resultList.size() + " )"),
					413);
			return;
		}

		JsonArray context = new JsonArray();
		context.add("https://ref.gs1.org/standards/epcis/2.0.0/epcis-context.jsonld");
		ArrayList<String> namespaces = getNamespaces(resultList);
		JsonObject extType = new JsonObject();
		JsonObject extContext = new JsonObject();
		for (int i = 0; i < namespaces.size(); i++) {
			extContext.put("ext" + i, decodeMongoObjectKey(namespaces.get(i)));
		}
		context.add(extContext);
		// conversion
		List<JsonObject> convertedResultList = getConvertedResultList(sort != null && !sort.isEmpty(), resultList,
				namespaces, extType);
		context.add(extType);
		if (needPagination == true && perPage >= convertedResultList.size()) {
			needPagination = false;
		} else if (needPagination == true && perPage < convertedResultList.size()) {
			if (!convertedResultList.isEmpty())
				convertedResultList = convertedResultList.subList(0, perPage);
		}

		JsonObject queryResultDocument = StaticResource.simpleEventQueryResults.copy();
		JsonArray eventList = queryResultDocument.getJsonObject("epcisBody").getJsonObject("queryResults")
				.getJsonObject("resultsBody").getJsonArray("eventList");
		for (JsonObject list : convertedResultList) {
			eventList.add(list);
		}
		queryResultDocument.put("@context", context);

		if (needPagination) {
			UUID uuid;
			long currentTime = System.currentTimeMillis();

			while (true) {
				uuid = UUID.randomUUID();
				if (!EPCISServer.eventPageMap.containsKey(uuid))
					break;
			}

			DataPage page = new DataPage(uuid, "SimpleEventQuery", q, null, sort, eventCountLimit, perPage);
			Timer timer = new Timer();
			page.setTimer(timer);
			timer.schedule(
					new DataPageExpiryTimerTask("GET /events", EPCISServer.eventPageMap, uuid, EPCISServer.logger),
					Metadata.GS1_Next_Page_Token_Expires);
			EPCISServer.eventPageMap.put(uuid, page);
			EPCISServer.logger.debug(
					"[GET /events] page - " + uuid + " added. # remaining pages - " + EPCISServer.eventPageMap.size());

			serverResponse.putHeader("Link", uuid.toString()).putHeader("GS1-Next-Page-Token-Expires",
					TimeUtil.getDateTimeStamp(currentTime + Metadata.GS1_Next_Page_Token_Expires));

			HTTPUtil.sendQueryResults(serverResponse, queryResultDocument, 200);
		} else {
			HTTPUtil.sendQueryResults(serverResponse, queryResultDocument, 200);
		}
	}

	private void invokeSimpleMasterDataQuery(HttpServerResponse serverResponse, QueryDescription qd, int perPage) {

		FindIterable<org.bson.Document> query = EPCISServer.mVocCollection.find(qd.getMongoQuery());
		if (!qd.getMongoProjection().isEmpty())
			query.projection(qd.getMongoProjection());

		List<org.bson.Document> resultList = new ArrayList<org.bson.Document>();
		try {
			query.into(resultList);
		} catch (Throwable e1) {
			HTTPUtil.sendQueryResults(serverResponse,
					JSONMessageFactory.get500ImplementationException(
							"[500ImplementationException] The server cannot return the response as requested: "
									+ e1.getMessage()),
					500);
			return;
		}

		if (qd.getMaxCount() != null && (resultList.size() > qd.getMaxCount())) {
			HTTPUtil.sendQueryResults(serverResponse, JSONMessageFactory.get413QueryTooLargeException(
					"[413QueryTooLargeException] The server cannot return the response as requested: "
							+ "An attempt to execute a query resulted in more data than the service was willing to provide. ( result size: "
							+ resultList.size() + " )"),
					413);
			return;
		}

		JsonArray context = new JsonArray();
		context.add("https://ref.gs1.org/standards/epcis/2.0.0/epcis-context.jsonld");
		ArrayList<String> namespaces = getVocabularyNamespaces(resultList);

		JsonObject extType = new JsonObject();
		JsonObject extContext = new JsonObject();
		for (int i = 0; i < namespaces.size(); i++) {
			extContext.put("ext" + i, decodeMongoObjectKey(namespaces.get(i)));
		}
		context.add(extContext);

		boolean needPagination = false;
		if (perPage < resultList.size()) {
			needPagination = true;
			resultList = resultList.subList(0, perPage);
		}

		List<JsonObject> convertedResultList = new ArrayList<JsonObject>();
		for (org.bson.Document result : resultList) {
			try {
				convertedResultList.add(EPCISServer.bsonToJsonConverter.convertVocabulary(result, namespaces, extType));
			} catch (ValidationException e) {
				// should not happen
				e.printStackTrace();
			}
		}
		context.add(extType);

		JsonObject queryResultDocument = StaticResource.simpleMasterDataQueryResults.copy();
		JsonArray vocabularyList = queryResultDocument.getJsonObject("epcisBody").getJsonObject("queryResults")
				.getJsonObject("resultsBody").getJsonArray("vocabularyList");
		for (JsonObject list : convertedResultList) {
			vocabularyList.add(list);
		}
		queryResultDocument.put("@context", context);

		if (needPagination) {
			UUID uuid;
			long currentTime = System.currentTimeMillis();

			while (true) {
				uuid = UUID.randomUUID();
				if (!EPCISServer.vocabularyPageMap.containsKey(uuid))
					break;
			}

			DataPage page = new DataPage(uuid, "SimpleMasterDataQuery", qd.getMongoQuery(), qd.getMongoProjection(),
					null, null, perPage);

			Timer timer = new Timer();
			page.setTimer(timer);
			timer.schedule(new DataPageExpiryTimerTask("GET /vocabularies", EPCISServer.vocabularyPageMap, uuid,
					EPCISServer.logger), Metadata.GS1_Next_Page_Token_Expires);
			EPCISServer.vocabularyPageMap.put(uuid, page);
			EPCISServer.logger.debug("[GET /vocabularies] page - " + uuid + " added. # remaining pages - "
					+ EPCISServer.vocabularyPageMap.size());

			serverResponse.putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-Extensions", Metadata.GS1_Extensions).putHeader("Link", uuid.toString())
					.putHeader("GS1-Next-Page-Token-Expires",
							TimeUtil.getDateTimeStamp(currentTime + Metadata.GS1_Next_Page_Token_Expires));

			HTTPUtil.sendQueryResults(serverResponse, queryResultDocument, 200);
		} else {

			HTTPUtil.sendQueryResults(serverResponse, queryResultDocument, 200);
		}

	}

	private void invokeSimpleMasterDataQuery(HttpServerResponse serverResponse, org.bson.Document namedQuery,
			int perPage) {

		org.bson.Document q = namedQuery.get("query", org.bson.Document.class);
		FindIterable<org.bson.Document> query = EPCISServer.mVocCollection.find(q);
		org.bson.Document projection = namedQuery.get("projection", org.bson.Document.class);
		if (projection != null && !projection.isEmpty())
			query.projection(projection);

		List<org.bson.Document> resultList = new ArrayList<org.bson.Document>();
		try {
			query.into(resultList);
		} catch (Throwable e1) {
			HTTPUtil.sendQueryResults(serverResponse,
					JSONMessageFactory.get500ImplementationException(
							"[500ImplementationException] The server cannot return the response as requested: "
									+ e1.getMessage()),
					500);
			return;
		}

		Integer maxCount = namedQuery.getInteger("maxCount");
		if (maxCount != null && (resultList.size() > maxCount)) {
			HTTPUtil.sendQueryResults(serverResponse, JSONMessageFactory.get413QueryTooLargeException(
					"[413QueryTooLargeException] The server cannot return the response as requested: "
							+ "An attempt to execute a query resulted in more data than the service was willing to provide. ( result size: "
							+ resultList.size() + " )"),
					413);
			return;
		}

		JsonArray context = new JsonArray();
		context.add("https://ref.gs1.org/standards/epcis/2.0.0/epcis-context.jsonld");
		ArrayList<String> namespaces = getVocabularyNamespaces(resultList);

		JsonObject extType = new JsonObject();
		JsonObject extContext = new JsonObject();
		for (int i = 0; i < namespaces.size(); i++) {
			extContext.put("ext" + i, decodeMongoObjectKey(namespaces.get(i)));
		}
		context.add(extContext);

		boolean needPagination = false;
		if (perPage < resultList.size()) {
			needPagination = true;
			resultList = resultList.subList(0, perPage);
		}

		List<JsonObject> convertedResultList = new ArrayList<JsonObject>();
		for (org.bson.Document result : resultList) {
			try {
				convertedResultList.add(EPCISServer.bsonToJsonConverter.convertVocabulary(result, namespaces, extType));
			} catch (ValidationException e) {
				// should not happen
				e.printStackTrace();
			}
		}
		context.add(extType);

		JsonObject queryResultDocument = StaticResource.simpleMasterDataQueryResults.copy();
		JsonArray vocabularyList = queryResultDocument.getJsonObject("epcisBody").getJsonObject("queryResults")
				.getJsonObject("resultsBody").getJsonArray("vocabularyList");
		for (JsonObject list : convertedResultList) {
			vocabularyList.add(list);
		}
		queryResultDocument.put("@context", context);

		if (needPagination) {
			UUID uuid;
			long currentTime = System.currentTimeMillis();

			while (true) {
				uuid = UUID.randomUUID();
				if (!EPCISServer.vocabularyPageMap.containsKey(uuid))
					break;
			}

			DataPage page = new DataPage(uuid, "SimpleMasterDataQuery", q, projection, null, null, perPage);

			Timer timer = new Timer();
			page.setTimer(timer);
			timer.schedule(new DataPageExpiryTimerTask("GET /vocabularies", EPCISServer.vocabularyPageMap, uuid,
					EPCISServer.logger), Metadata.GS1_Next_Page_Token_Expires);
			EPCISServer.vocabularyPageMap.put(uuid, page);
			EPCISServer.logger.debug("[GET /vocabularies] page - " + uuid + " added. # remaining pages - "
					+ EPCISServer.vocabularyPageMap.size());

			serverResponse.putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-Extensions", Metadata.GS1_Extensions).putHeader("Link", uuid.toString())
					.putHeader("GS1-Next-Page-Token-Expires",
							TimeUtil.getDateTimeStamp(currentTime + Metadata.GS1_Next_Page_Token_Expires));

			HTTPUtil.sendQueryResults(serverResponse, queryResultDocument, 200);
		} else {

			HTTPUtil.sendQueryResults(serverResponse, queryResultDocument, 200);
		}

	}

	public void getNextVocabularyPage(RoutingContext routingContext, UUID uuid) throws ParserConfigurationException {
		HttpServerRequest serverRequest = routingContext.request();
		HttpServerResponse serverResponse = routingContext.response();

		// get perPage
		int perPage;

		try {
			perPage = getPerPage(serverRequest);
		} catch (QueryParameterException e) {
			HTTPUtil.sendQueryResults(serverResponse,
					JSONMessageFactory.get406NotAcceptableException(
							"[406NotAcceptable] The server cannot return the response as requested: " + e.getReason()),
					406);
			return;
		}

		// get Page
		DataPage page = null;
		if (!EPCISServer.vocabularyPageMap.containsKey(uuid)) {
			HTTPUtil.sendQueryResults(serverResponse,
					JSONMessageFactory.get406NotAcceptableException(
							"[406NotAcceptable] The given next page token does not exist or be no longer available."),
					406);
			return;
		} else {
			page = EPCISServer.vocabularyPageMap.get(uuid);
		}

		// CREATE QUERY
		FindIterable<org.bson.Document> query = EPCISServer.mVocCollection.find(page.getQuery());
		if (!page.getProjection().isEmpty())
			query.projection(page.getProjection());
		query.skip(page.getSkip());
		query.limit(perPage + 1);

		List<org.bson.Document> resultList = new ArrayList<org.bson.Document>();
		try {
			query.into(resultList);
		} catch (Throwable e1) {
			HTTPUtil.sendQueryResults(serverResponse,
					JSONMessageFactory.get500ImplementationException(
							"[500ImplementationException] The server cannot return the response as requested: "
									+ e1.getMessage()),
					500);
			return;
		}

		JsonArray context = new JsonArray();
		context.add("https://ref.gs1.org/standards/epcis/2.0.0/epcis-context.jsonld");
		ArrayList<String> namespaces = getVocabularyNamespaces(resultList);

		JsonObject extType = new JsonObject();
		JsonObject extContext = new JsonObject();
		for (int i = 0; i < namespaces.size(); i++) {
			extContext.put("ext" + i, decodeMongoObjectKey(namespaces.get(i)));
		}
		context.add(extContext);

		boolean needPagination = false;
		if (perPage < resultList.size()) {
			needPagination = true;
			resultList = resultList.subList(0, perPage);
		}

		List<JsonObject> convertedResultList = new ArrayList<JsonObject>();
		for (org.bson.Document result : resultList) {
			try {
				convertedResultList.add(EPCISServer.bsonToJsonConverter.convertVocabulary(result, namespaces, extType));
			} catch (ValidationException e) {
				// should not happen
				e.printStackTrace();
			}
		}
		context.add(extType);

		JsonObject queryResultDocument = StaticResource.simpleMasterDataQueryResults.copy();
		JsonArray vocabularyList = queryResultDocument.getJsonObject("epcisBody").getJsonObject("queryResults")
				.getJsonObject("resultsBody").getJsonArray("vocabularyList");
		for (JsonObject list : convertedResultList) {
			vocabularyList.add(list);
		}
		queryResultDocument.put("@context", context);

		if (needPagination) {
			page.incrSkip(perPage);
			long currentTime = System.currentTimeMillis();
			Timer timer = page.getTimer();
			if (timer != null)
				timer.cancel();

			Timer newTimer = new Timer();
			page.setTimer(newTimer);
			newTimer.schedule(new DataPageExpiryTimerTask("GET /vocabularies", EPCISServer.vocabularyPageMap, uuid,
					EPCISServer.logger), Metadata.GS1_Next_Page_Token_Expires);
			EPCISServer.logger.debug("[GET /vocabularies] page - " + uuid + " token expiry time extended to "
					+ TimeUtil.getDateTimeStamp(currentTime + Metadata.GS1_Next_Page_Token_Expires));

			serverResponse.putHeader("Link", uuid.toString()).putHeader("GS1-Next-Page-Token-Expires",
					TimeUtil.getDateTimeStamp(currentTime + Metadata.GS1_Next_Page_Token_Expires));

			HTTPUtil.sendQueryResults(serverResponse, queryResultDocument, 200);
		} else {
			EPCISServer.vocabularyPageMap.remove(uuid);
			EPCISServer.logger.debug("[GET /vocabularies] page - " + uuid + " expired. # remaining pages - "
					+ EPCISServer.vocabularyPageMap.size());

			HTTPUtil.sendQueryResults(serverResponse, queryResultDocument, 200);
		}

	}

	private int getPerPage(HttpServerRequest serverRequest) throws QueryParameterException {
		String perPageParam = serverRequest.getParam("PerPage");
		int perPage = 30;
		if (perPageParam != null) {
			try {
				int t = Integer.parseInt(perPageParam);
				if (t > 0)
					perPage = t;
			} catch (NumberFormatException e) {
				throw new QueryParameterException("invalid PerPage - " + perPageParam);
			}
		}
		return perPage;
	}

	/**
	 * WebSocket subscription Volatile
	 *
	 */
	public void subscribeWebsocket(HttpServerResponse serverResponse, Subscription subscription) {

		String schedule = subscription.getSchedule();
		String trigger = subscription.getTrigger();
		if (schedule == null && trigger == null) {
			HTTPUtil.sendQueryResults(subscription.getServerWebSocket(),
					JSONMessageFactory.get400SubscriptionControlsException(
							"WebSocket Subscription should contain one of schedule or trigger information"));
			return;
		}

		if (schedule != null) {
			try {
				cronSchedule(schedule);
				addScheduleToQuartz(subscription);
				subscription.getServerWebSocket().closeHandler(h -> {
					try {
						SubscriptionManager.sched.unscheduleJob(
								org.quartz.TriggerKey.triggerKey(subscription.getSubscriptionID(), "SimpleEventQuery"));
						SubscriptionManager.sched.deleteJob(
								org.quartz.JobKey.jobKey(subscription.getSubscriptionID(), "SimpleEventQuery"));
					} catch (SchedulerException e) {
						e.printStackTrace();
					}
				});
				return;
			} catch (Throwable e) {
				HTTPUtil.sendQueryResults(subscription.getServerWebSocket(),
						JSONMessageFactory.get400SubscriptionControlsException(
								"The specified subscription controls was invalid; e.g., the schedule parameters were out of range, the trigger URI could not be parsed or did not name a recognised trigger, etc."
										+ e.getMessage()));
				return;
			}
		} else {
			EPCISServer.triggerEngine.addSubscription(subscription.getTriggerDescription(),
					subscription.getServerWebSocket());
			subscription.getServerWebSocket().closeHandler(h -> {

				HashSet<Object> obj = TriggerEngine.triggerSubscription.remove(subscription.getTriggerDescription());
				if (obj != null) {
					EPCISServer.logger.debug("Trigger Subscription (Web Socket) " + subscription.getTriggerDescription()
							+ " is removed ");
				}
			});
		}
	}

	public void subscribeWebhook(HttpServerResponse serverResponse, Subscription subscription) {

		String schedule = subscription.getSchedule();
		String trigger = subscription.getTrigger();
		if (schedule == null && trigger == null) {
			HTTPUtil.sendQueryResults(subscription.getServerWebSocket(),
					JSONMessageFactory.get400SubscriptionControlsException(
							"WebSocket Subscription should contain one of schedule or trigger information"));
			return;
		}

		if (schedule != null) {
			try {
				cronSchedule(schedule);
				addScheduleToQuartz(subscription);
				addScheduleToDB(subscription);
				serverResponse.putHeader("Location", subscription.getSubscriptionID());
				HTTPUtil.sendQueryResults(serverResponse, subscription.toJSONResponse(), 201);
				return;
			} catch (Throwable e) {
				HTTPUtil.sendQueryResults(subscription.getServerWebSocket(),
						JSONMessageFactory.get400SubscriptionControlsException(
								"The specified subscription controls was invalid; e.g., the schedule parameters were out of range, the trigger URI could not be parsed or did not name a recognised trigger, etc."
										+ e.getMessage()));
				return;
			}
		} else {
			EPCISServer.triggerEngine.addSubscription(subscription.getTriggerDescription(), subscription.getDest());
			addScheduleToDB(subscription);
			serverResponse.putHeader("Location", subscription.getSubscriptionID());
			HTTPUtil.sendQueryResults(serverResponse, subscription.toJSONResponse(), 201);
		}
	}

	// ------------TODO-------------------------------------------------------------------------------------------

	public void getSubscriptionIDs(HttpServerResponse serverResponse) {
		try {
			Document retDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element envelope = retDoc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Envelope");
			Element body = retDoc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Body");
			Element response = retDoc.createElementNS("urn:epcglobal:epcis-query:xsd:1",
					"query:GetSubscriptionIDsResult");

			List<org.bson.Document> results = new ArrayList<org.bson.Document>();

			try {
				EPCISServer.mSubscriptionCollection.find(new org.bson.Document())
						.projection(new org.bson.Document("_id", true)).into(results);
			} catch (Throwable e) {
				ImplementationException e1 = new ImplementationException(ImplementationExceptionSeverity.ERROR, "Poll",
						e.getMessage());
				HTTPUtil.sendQueryResults(serverResponse, new SOAPMessage(), e1, e1.getClass(), 500);
				return;
			}

			for (org.bson.Document obj : results) {
				Element item = retDoc.createElement("string");
				item.setTextContent(obj.getString("_id"));
				response.appendChild(item);
			}
			envelope.appendChild(body);
			body.appendChild(response);
			retDoc.appendChild(envelope);

			serverResponse.putHeader("content-type", "*/xml; charset=utf-8").end(XMLUtil.toString(retDoc));

		} catch (ParserConfigurationException e) {
			ImplementationException err = new ImplementationException(ImplementationExceptionSeverity.ERROR,
					"GetSubscriptionIDs", e.getMessage());
			HTTPUtil.sendQueryResults(serverResponse, new SOAPMessage(), err, err.getClass(), 400);
			EPCISServer.logger.error(e.getMessage());
		} catch (Throwable e) {
			ImplementationException err = new ImplementationException(ImplementationExceptionSeverity.ERROR,
					"GetSubscriptionIDs", e.getMessage());
			HTTPUtil.sendQueryResults(serverResponse, new SOAPMessage(), err, err.getClass(), 500);
			EPCISServer.logger.error(e.getMessage());
		}
	}

	void addScheduleToQuartz(Subscription subscription) throws SchedulerException {
		JobDataMap map = new JobDataMap();
		map.put("jobData", subscription);
		String subscriptionID = subscription.getSubscriptionID();
		String schedule = subscription.getSchedule();
		JobDetail job = newJob(SubscriptionTask.class).withIdentity(subscriptionID, "SimpleEventQuery").setJobData(map)
				.storeDurably(false).build();

		Trigger trigger = newTrigger().withIdentity(subscriptionID, "SimpleEventQuery").startNow()
				.withSchedule(cronSchedule(schedule)).build();

		if (!SubscriptionManager.sched.isStarted())
			SubscriptionManager.sched.start();
		SubscriptionManager.sched.scheduleJob(job, trigger);
	}

	void addScheduleToDB(Subscription subscription) {
		try {
			InsertOneResult result = EPCISServer.mSubscriptionCollection.insertOne(subscription.toMongoDocument());
			EPCISServer.logger.debug(result);
		} catch (Exception e) {
			EPCISServer.logger.error(e.getMessage());
		} catch (Throwable e) {
			EPCISServer.logger.error(e.getMessage());
		}
	}

	private boolean isResultSorted(QueryDescription qd) {
		if (qd.getMongoSort() == null || qd.getMongoSort().isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

	private boolean isResultSorted(org.bson.Document sort) {
		if (sort == null || sort.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

	private List<JsonObject> getConvertedResultList(boolean isResultSorted, List<org.bson.Document> resultList,
			ArrayList<String> namespaces, JsonObject extType) throws RuntimeException {
		Stream<org.bson.Document> resultStream;
		if (isResultSorted == true) {
			resultStream = resultList.stream();
		} else {
			resultStream = resultList.parallelStream();
		}

		ArrayList<String> nsList = new ArrayList<>();

		try {
			List<JsonObject> results = resultStream.map(result -> {
				try {
					switch (result.getString("type")) {
					case "AggregationEvent":
						return EPCISServer.bsonToJsonConverter.convertAggregationEvent(result, namespaces, extType);

					case "ObjectEvent":
						return EPCISServer.bsonToJsonConverter.convertObjectEvent(result, namespaces, extType);

					case "TransactionEvent":
						return EPCISServer.bsonToJsonConverter.convertTransactionEvent(result, namespaces, extType);

					case "TransformationEvent":
						return EPCISServer.bsonToJsonConverter.convertTransformationEvent(result, namespaces, extType);

					case "AssociationEvent":
						return EPCISServer.bsonToJsonConverter.convertAssociationEvent(result, namespaces, extType);

					default:
						return null;
					}
				} catch (ValidationException e) {
					throw new RuntimeException(e.getReason());
				}
			}).filter(Objects::nonNull).collect(Collectors.toList());
			return results;
		} catch (RuntimeException e) {
			throw e;
		}
	}

	private void updateInitialRecordTime(String subscriptionID, long initialRecordTime) throws ImplementationException {

		try {
			EPCISServer.mSubscriptionCollection.findOneAndUpdate(new org.bson.Document("_id", subscriptionID),
					new org.bson.Document("$set", new org.bson.Document("initialRecordTime", initialRecordTime)));
			EPCISServer.logger.debug("initialRecordTime updated to " + initialRecordTime);
		} catch (Throwable e2) {
			ImplementationException e = new ImplementationException(ImplementationExceptionSeverity.ERROR, null, null,
					e2.getMessage());
			EPCISServer.logger.error(e.getMessage());
			throw e;
		}
	}

	public void unsubscribe(HttpServerResponse serverResponse, Document doc) {

		SOAPMessage message = new SOAPMessage();
		String subscriptionID = null;
		try {
			NodeList nodeList = doc.getElementsByTagName("subscriptionID");
			if (nodeList.getLength() == 1) {
				subscriptionID = nodeList.item(0).getTextContent();
			}

			if (subscriptionID == null) {
				QueryParameterException e = new QueryParameterException();
				e.setStackTrace(new StackTraceElement[0]);
				e.setReason("Invalid unsubscribe invocation: missing subscriptionID");
				HTTPUtil.sendQueryResults(serverResponse, message, e, e.getClass(), 400);
				return;
			}

			Document retDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element envelope = retDoc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Envelope");
			Element body = retDoc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Body");
			Element response = retDoc.createElementNS("urn:epcglobal:epcis-query:xsd:1", "query:UnsubscribeResult");
			body.appendChild(response);
			envelope.appendChild(body);
			body.appendChild(response);
			retDoc.appendChild(envelope);

			org.bson.Document result = null;

			try {
				result = EPCISServer.mSubscriptionCollection
						.findOneAndDelete(new org.bson.Document("_id", subscriptionID));
			} catch (Throwable e2) {
				ImplementationException e = new ImplementationException(ImplementationExceptionSeverity.ERROR, null,
						null, e2.getMessage());
				HTTPUtil.sendQueryResults(serverResponse, message, e, e.getClass(), 500);
			}

			if (result == null) {
				NoSuchSubscriptionException err = new NoSuchSubscriptionException("No such subscription exception");
				HTTPUtil.sendQueryResults(serverResponse, message, err, err.getClass(), 400);
				EPCISServer.logger.error("No such subscription exception");
				return;
			} else {
				try {
					SubscriptionManager.sched.unscheduleJob(
							org.quartz.TriggerKey.triggerKey(result.getString("_id"), result.getString("queryName")));
					SubscriptionManager.sched.deleteJob(
							org.quartz.JobKey.jobKey(result.getString("_id"), result.getString("queryName")));
					serverResponse.putHeader("content-type", "*/xml; charset=utf-8").setStatusCode(200)
							.end(XMLUtil.toString(retDoc));
				} catch (org.quartz.SchedulerException e) {
					ImplementationException err = new ImplementationException(ImplementationExceptionSeverity.ERROR,
							"Subscribe", subscriptionID, e.getMessage());
					HTTPUtil.sendQueryResults(serverResponse, message, err, err.getClass(), 500);
					EPCISServer.logger.error("Implementation Exception: " + e.getMessage());
				}
			}
		} catch (IllegalStateException | ParserConfigurationException e) {
			QueryParameterException e1 = new QueryParameterException(e.getMessage());
			HTTPUtil.sendQueryResults(serverResponse, message, e1, e1.getClass(), 400);
		} catch (Throwable e) {
			ImplementationException err = new ImplementationException(ImplementationExceptionSeverity.ERROR,
					"Subscribe", subscriptionID, e.getMessage());
			HTTPUtil.sendQueryResults(serverResponse, message, err, err.getClass(), 500);
		}
	}

	private Document createDocument(String soapMessage) throws ValidationException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		docFactory.setNamespaceAware(true);
		Document doc;
		try {
			doc = docFactory.newDocumentBuilder()
					.parse(XMLUtil.getXMLDocumentInputStream(FileUtil.getByteArray(soapMessage)));
			return doc;
		} catch (SAXException | IOException | ParserConfigurationException e) {
			ValidationException e1 = new ValidationException();
			e1.setStackTrace(new StackTraceElement[0]);
			e1.setReason(
					"The input to the operation was not syntactically valid according to the syntax defined by the binding. Each binding specifies the particular circumstances under which this exception is raised.: "
							+ e.getMessage());
			throw e1;
		}
	}

	public void sendQueryResults(HttpServerResponse serverResponse, String result) {
		serverResponse.putHeader("content-type", "application/xml; charset=utf-8").setStatusCode(200).end(result);
	}

	public void sendSubscriptionResult(JobExecutionContext context) {
		SOAPMessage message = new SOAPMessage();
		JobDetail detail = context.getJobDetail();
		JobDataMap map = detail.getJobDataMap();

		Subscription sub = (Subscription) map.get("jobData");
		String subscriptionID = sub.getSubscriptionID();
		URI dest = sub.getDest();
		Long initialRecordTime = sub.getInitialRecordTime();
		boolean reportIfEmpty = sub.getReportIfEmpty();

		QueryDescription qd = sub.getQueryDescription();
		org.bson.Document mongoQuery = qd.getMongoQuery();
		org.bson.Document sort = qd.getMongoSort();
		Integer eventCountLimit = qd.getEventCountLimit();
		Integer maxCount = qd.getMaxCount();

		try {
			if (initialRecordTime != null) {
				mongoQuery.put("recordTime", new org.bson.Document("$gte", initialRecordTime));
			}

			FindIterable<org.bson.Document> query = EPCISServer.mEventCollection.find(mongoQuery);

			if (sort != null)
				query.sort(sort);
			if (eventCountLimit != null)
				query.limit(eventCountLimit);

			List<org.bson.Document> resultList = new ArrayList<org.bson.Document>();
			try {
				query.into(resultList);
			} catch (Throwable e1) {
				ImplementationException e = new ImplementationException(ImplementationExceptionSeverity.ERROR,
						"Subscribe", subscriptionID, e1.getMessage());
				HTTPUtil.sendQueryResults(EPCISServer.clientForSubscriptionCallback, dest, EPCISServer.logger, message,
						e, e.getClass());
				return;
			}

			if (maxCount != null && (resultList.size() > maxCount)) {
				QueryTooLargeException e = new QueryTooLargeException(
						"An attempt to execute a query resulted in more data than the service was willing to provide. ( result size: "
								+ resultList.size() + " )");
				HTTPUtil.sendQueryResults(EPCISServer.clientForSubscriptionCallback, dest, EPCISServer.logger, message,
						e, e.getClass());
				return;
			}

			List<JsonObject> convertedResultList = getConvertedResultList(isResultSorted(sort), resultList, null, null);

			if (reportIfEmpty == false && convertedResultList.size() == 0) {
				EPCISServer.logger.debug("Subscription " + sub + " invoked but not sent due to reportIfEmpty");
				return;
			}

			QueryResults queryResults = new QueryResults();
			queryResults.setQueryName("SimpleEventQuery");
			queryResults.setSubscriptionID(subscriptionID);

			QueryResultsBody resultsBody = new QueryResultsBody();

			EventListType elt = new EventListType();
			// elt.setObjectEventOrAggregationEventOrTransformationEvent(convertedResultList);
			resultsBody.setEventList(elt);
			queryResults.setResultsBody(resultsBody);

			// InitialRecordTime limits recordTime
			if (initialRecordTime != null) {
				try {
					long cur = System.currentTimeMillis();
					sub.setInitialRecordTime(cur);
					updateInitialRecordTime(subscriptionID, cur);
					map.put("jobData", sub);
					SubscriptionManager.sched.addJob(detail, true, true);
				} catch (SchedulerException e) {
					HTTPUtil.sendQueryResults(EPCISServer.clientForSubscriptionCallback, dest, EPCISServer.logger,
							message, e, e.getClass());
					return;
				} catch (ImplementationException e) {
					HTTPUtil.sendQueryResults(EPCISServer.clientForSubscriptionCallback, dest, EPCISServer.logger,
							message, e, e.getClass());
					return;
				}
			}

			HTTPUtil.sendQueryResults(EPCISServer.clientForSubscriptionCallback, dest, EPCISServer.logger, message,
					queryResults, QueryResults.class);

		} catch (IllegalStateException e) {
			ImplementationException e1 = new ImplementationException(ImplementationExceptionSeverity.ERROR, "Subscribe",
					subscriptionID, e.getMessage());
			HTTPUtil.sendQueryResults(EPCISServer.clientForSubscriptionCallback, dest, EPCISServer.logger, message, e1,
					e1.getClass());
		}
		// QueryParameterException, QueryTooLargeException, QueryTooComplexException,
		// NoSuchNameException, SecurityException, ValidationException,
		// ImplementationException
	}

	public static ArrayList<String> getNamespaces(List<org.bson.Document> events) {
		ArrayList<String> namespaces = new ArrayList<String>();
		for (org.bson.Document event : events) {
			org.bson.Document ext = event.get("extension", org.bson.Document.class);
			if (ext != null) {
				getNamespaces(namespaces, ext);
			}
			List<org.bson.Document> sel = event.getList("sensorElementList", org.bson.Document.class);
			if (sel != null) {
				for (org.bson.Document se : sel) {
					org.bson.Document seExt = se.get("extension", org.bson.Document.class);
					if (seExt != null) {
						getNamespaces(namespaces, seExt);
					}
				}
			}

			org.bson.Document errorDeclaration = event.get("errorDeclaration", org.bson.Document.class);
			if (errorDeclaration != null) {
				org.bson.Document errExt = errorDeclaration.get("extension", org.bson.Document.class);
				if (errExt != null) {
					getNamespaces(namespaces, errExt);
				}
			}
		}
		return namespaces;
	}

	public static ArrayList<String> getVocabularyNamespaces(List<org.bson.Document> vocabularies) {
		ArrayList<String> namespaces = new ArrayList<String>();

		for (org.bson.Document vocabulary : vocabularies) {

			for (String key : vocabulary.get("attributes", org.bson.Document.class).keySet()) {
				String[] arr = key.split("#");
				if (arr.length == 2) {
					if (!namespaces.contains(arr[0])) {
						namespaces.add(arr[0]);
					}
					Object value = vocabulary.get(key);
					if (value instanceof org.bson.Document) {
						getNamespaces(namespaces, (org.bson.Document) value);
					}
				}
			}
		}
		return namespaces;
	}

	public static void getNamespaces(ArrayList<String> namespaces, org.bson.Document inner) {
		for (Entry<String, Object> entry : inner.entrySet()) {
			String nonDecodedNamespace = entry.getKey();
			String[] arr = nonDecodedNamespace.split("#");
			if (arr.length == 2) {
				if (!namespaces.contains(arr[0])) {
					namespaces.add(arr[0]);
				}
				if (entry.getValue() instanceof org.bson.Document) {
					getNamespaces(namespaces, (org.bson.Document) entry.getValue());
				}
			}

		}
	}

	public void getResources(HttpServerRequest serverRequest, HttpServerResponse serverResponse, String tag,
			ConcurrentHashMap<UUID, ResourcePage> pages, ConcurrentHashSet<String> eventResources,
			ConcurrentHashSet<String> vocResources) {

		// get perPage
		int perPage;

		try {
			perPage = getPerPage(serverRequest);
		} catch (QueryParameterException e) {
			HTTPUtil.sendQueryResults(serverResponse,
					JSONMessageFactory.get406NotAcceptableException(
							"[406NotAcceptable] The server cannot return the response as requested: " + e.getReason()),
					406);
			return;
		}

		ResourcePage message = new ResourcePage(tag, eventResources, vocResources);
		String result = message.getJSONNextPage(perPage, tag);
		if (!message.isClosed()) {
			UUID uuid;
			long currentTime = System.currentTimeMillis();

			while (true) {
				uuid = UUID.randomUUID();
				if (!pages.containsKey(uuid))
					break;
			}

			Timer timer = new Timer();
			message.setTimer(timer);
			timer.schedule(new ResourcePageExpiryTimerTask(tag, pages, uuid, EPCISServer.logger),
					Metadata.GS1_Next_Page_Token_Expires);
			pages.put(uuid, message);
			EPCISServer.logger
					.debug("[GET /" + tag + "] page - " + uuid + " added. # remaining pages - " + pages.size());

			serverResponse.putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-Extensions", Metadata.GS1_Extensions).putHeader("Link", uuid.toString())
					.putHeader("GS1-Next-Page-Token-Expires",
							TimeUtil.getDateTimeStamp(currentTime + Metadata.GS1_Next_Page_Token_Expires));
			HTTPUtil.sendQueryResults(serverResponse, result, 200, "application/json");
		} else {
			HTTPUtil.sendQueryResults(serverResponse, result, 200, "application/json");
		}
	}

	public void getNextResourcePage(HttpServerRequest serverRequest, HttpServerResponse serverResponse, String tag,
			ConcurrentHashMap<UUID, ResourcePage> pages, UUID uuid) {

		// get perPage
		int perPage;

		try {
			perPage = getPerPage(serverRequest);
		} catch (QueryParameterException e) {
			HTTPUtil.sendQueryResults(serverResponse,
					JSONMessageFactory.get406NotAcceptableException(
							"[406NotAcceptable] The server cannot return the response as requested: " + e.getReason()),
					406);
			return;
		}

		pages.get(uuid);

		ResourcePage page = null;
		if (!pages.containsKey(uuid)) {
			HTTPUtil.sendQueryResults(serverResponse,
					JSONMessageFactory.get406NotAcceptableException(
							"[406NotAcceptable] The given next page token does not exist or be no longer available."),
					406);
			EPCISServer.logger
					.error("[406NotAcceptable] The given next page token does not exist or be no longer available.");
			return;
		} else {
			page = pages.get(uuid);
		}

		String result = page.getJSONNextPage(perPage, tag);

		if (!page.isClosed()) {
			long currentTime = System.currentTimeMillis();
			Timer timer = page.getTimer();
			if (timer != null)
				timer.cancel();

			Timer newTimer = new Timer();
			page.setTimer(newTimer);
			newTimer.schedule(new ResourcePageExpiryTimerTask("GET /" + tag, pages, uuid, EPCISServer.logger),
					Metadata.GS1_Next_Page_Token_Expires);
			EPCISServer.logger.debug("[GET /" + tag + "] page - " + uuid + " token expiry time extended to "
					+ TimeUtil.getDateTimeStamp(currentTime + Metadata.GS1_Next_Page_Token_Expires));

			serverResponse.putHeader("Link", uuid.toString()).putHeader("GS1-Next-Page-Token-Expires",
					TimeUtil.getDateTimeStamp(currentTime + Metadata.GS1_Next_Page_Token_Expires));
			HTTPUtil.sendQueryResults(serverResponse, result, 200, "application/json");

		} else {
			pages.remove(uuid);
			EPCISServer.logger
					.debug("[GET /" + tag + "] page - " + uuid + " expired. # remaining pages - " + pages.size());
			HTTPUtil.sendQueryResults(serverResponse, result, 200, "application/json");
		}
	}

}