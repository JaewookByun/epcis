package org.oliot.epcis.query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;

import java.util.UUID;

import org.bson.Document;
import org.oliot.epcis.capture.json.JSONMessageFactory;
import org.oliot.epcis.common.Metadata;
import org.oliot.epcis.converter.data.bson_to_pojo.AggregationEventConverter;
import org.oliot.epcis.converter.data.bson_to_pojo.AssociationEventConverter;
import org.oliot.epcis.converter.data.bson_to_pojo.MasterdataConverter;
import org.oliot.epcis.converter.data.bson_to_pojo.ObjectEventConverter;
import org.oliot.epcis.converter.data.bson_to_pojo.TransactionEventConverter;
import org.oliot.epcis.converter.data.bson_to_pojo.TransformationEventConverter;
import org.oliot.epcis.model.AggregationEventType;
import org.oliot.epcis.model.AssociationEventType;
import org.oliot.epcis.model.EPCISException;
import org.oliot.epcis.model.ImplementationException;
import org.oliot.epcis.model.ImplementationExceptionSeverity;
import org.oliot.epcis.model.ObjectEventType;
import org.oliot.epcis.model.TransactionEventType;
import org.oliot.epcis.model.TransformationEventType;
import org.oliot.epcis.model.ValidationException;
import org.oliot.epcis.model.VocabularyType;
import org.oliot.epcis.model.cbv.BusinessStep;
import org.oliot.epcis.model.cbv.Disposition;
import org.oliot.epcis.pagination.ResourcePage;
import org.oliot.epcis.resource.DynamicResource;
import org.oliot.epcis.resource.StaticResource;
import org.oliot.epcis.server.EPCISServer;
import org.oliot.epcis.tdt.TagDataTranslationEngine;
import org.oliot.epcis.util.EventTypesMessage;
import org.oliot.epcis.util.HTTPUtil;
import org.oliot.epcis.util.SOAPMessage;
import org.oliot.epcis.validation.HeaderValidator;

import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.impl.ConcurrentHashSet;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RequestBody;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * Copyright (C) 2020-2023. (Jaewook Byun) all rights reserved.
 * <p>
 * This project is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * SOAPQueryServiceHandler holds routers for Query Interface
 * <p>
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr, Associate Director, Auto-ID Labs, Korea,
 *         bjw0829@gmail.com
 */
public class RESTQueryServiceHandler {

	public static void checkJSONPollHeaders(RoutingContext routingContext) {
		if (!HeaderValidator.isEqualHeaderREST(routingContext, "GS1-CBV-Min", false))
			return;

		if (!HeaderValidator.isEqualHeaderREST(routingContext, "GS1-CBV-Max", false))
			return;

		if (!HeaderValidator.isEqualHeaderREST(routingContext, "GS1-EPCIS-Min", false))
			return;

		if (!HeaderValidator.isEqualHeaderREST(routingContext, "GS1-EPCIS-Max", false))
			return;

		if (!HeaderValidator.isEqualHeaderREST(routingContext, "GS1-EPC-Format", false))
			return;

		if (!HeaderValidator.isEqualHeaderREST(routingContext, "GS1-CBV-XML-Format", false))
			return;
	}

	public static void checkXMLPollHeaders(RoutingContext routingContext) {
		if (!HeaderValidator.isEqualHeaderSOAP(routingContext, "GS1-CBV-Min", false))
			return;

		if (!HeaderValidator.isEqualHeaderSOAP(routingContext, "GS1-CBV-Max", false))
			return;

		if (!HeaderValidator.isEqualHeaderSOAP(routingContext, "GS1-EPCIS-Min", false))
			return;

		if (!HeaderValidator.isEqualHeaderSOAP(routingContext, "GS1-EPCIS-Max", false))
			return;

		if (!HeaderValidator.isEqualHeaderSOAP(routingContext, "GS1-EPC-Format", false))
			return;

		if (!HeaderValidator.isEqualHeaderSOAP(routingContext, "GS1-CBV-XML-Format", false))
			return;
	}

	public static boolean isHeaderPassed(RoutingContext routingContext) {
		if (!HeaderValidator.isEqualHeaderSOAP(routingContext, "GS1-CBV-Min", false))
			return false;

		if (!HeaderValidator.isEqualHeaderSOAP(routingContext, "GS1-CBV-Max", false))
			return false;

		if (!HeaderValidator.isEqualHeaderSOAP(routingContext, "GS1-EPCIS-Min", false))
			return false;

		if (!HeaderValidator.isEqualHeaderSOAP(routingContext, "GS1-EPCIS-Max", false))
			return false;

		if (!HeaderValidator.isEqualHeaderSOAP(routingContext, "GS1-EPC-Format", false))
			return false;

		if (!HeaderValidator.isEqualHeaderSOAP(routingContext, "GS1-CBV-XML-Format", false))
			return false;
		return true;
	}

	public static String getHttpBody(RoutingContext routingContext, String queryName) {
		RequestBody body = routingContext.body();
		String inputString = null;
		if (body.isEmpty()) {
			MultiMap mm = routingContext.queryParams();
			Iterator<Entry<String, String>> iter = mm.iterator();
			while (iter.hasNext()) {
				Entry<String, String> entry = iter.next();
				String k = entry.getKey();
				if (k.contains("Envelope") && k.contains("Poll") && k.contains(queryName))
					inputString = k;
			}
			if (inputString == null) {
				EPCISException e = new EPCISException("[400ValidationException] Empty Request Body");
				EPCISServer.logger.error(e.getReason());
				HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(), 400);
				return null;
			}
		} else {
			inputString = body.asString();
		}
		return inputString;
	}

	public static JsonObject getJsonBody(RoutingContext routingContext) {
		JsonObject query = null;
		RequestBody body = routingContext.body();
		if (body.isEmpty()) {

			MultiMap m = routingContext.request().params();
			Iterator<Entry<String, String>> iter = m.iterator();
			while (iter.hasNext()) {
				try {
					String k = iter.next().getKey();
					query = new JsonObject(k);
					break;
				} catch (Exception e) {

				}
			}
		} else {
			try {
				query = body.asJsonObject();
			} catch (Exception e) {

			}
		}
		return query;
	}

	public static void registerGetEventsHandler(Router router, SOAPQueryService soapQueryService,
			RESTQueryService restQueryService) {

		router.get("/epcis/events").consumes("application/xml").handler(routingContext -> {

			if (!isHeaderPassed(routingContext))
				return;

			routingContext.response().setChunked(true);

			String nextPageToken = routingContext.request().getParam("nextPageToken");
			if (nextPageToken == null) {
				String inputString = getHttpBody(routingContext, "SimpleEventQuery");
				if (inputString == null)
					return;
				try {
					soapQueryService.pollEventsOrVocabularies(routingContext.request(), routingContext.response(),
							inputString, null, null);
				} catch (ValidationException e) {
					EPCISServer.logger.error(e.getReason());
					HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(), 400);
				}
			} else {
				soapQueryService.getNextEventPage(routingContext.request(), routingContext.response());
			}
		});
		EPCISServer.logger.info("[GET /epcis/events (application/xml)] - router added");

		router.get("/epcis/events").consumes("application/json").handler(routingContext -> {

			checkJSONPollHeaders(routingContext);
			if (routingContext.response().closed())
				return;

			routingContext.response().setChunked(true);

			String nextPageToken = routingContext.request().getParam("nextPageToken");
			if (nextPageToken == null) {
				JsonObject query = getJsonBody(routingContext);
				if (query == null) {
					HTTPUtil.sendQueryResults(routingContext.response(), JSONMessageFactory
							.get406NotAcceptableException("[406NotAcceptable] no valid simple event query (json)"),
							406);
					return;
				}
				restQueryService.query(routingContext, query, "SimpleEventQuery");
			} else {
				UUID uuid = null;
				try {
					uuid = UUID.fromString(nextPageToken);
				} catch (Exception e) {
					HTTPUtil.sendQueryResults(routingContext.response(),
							JSONMessageFactory.get406NotAcceptableException(
									"[406NotAcceptable] The server cannot return the response as requested: invalid nextPageToken - "
											+ uuid),
							406);
					return;
				}
				restQueryService.getNextEventPage(routingContext, uuid);
			}

		});
		EPCISServer.logger.info("[GET /epcis/events (application/json)] - router added");
	}

	public static void registerGetVocabulariesHandler(Router router, SOAPQueryService soapQueryService,
			RESTQueryService restQueryService) {

		router.get("/epcis/vocabularies").consumes("application/xml").handler(routingContext -> {

			if (!isHeaderPassed(routingContext))
				return;

			routingContext.response().setChunked(true);

			String nextPageToken = routingContext.request().getParam("nextPageToken");
			if (nextPageToken == null) {
				String inputString = getHttpBody(routingContext, "SimpleMasterDataQuery");
				if (inputString == null)
					return;
				try {
					soapQueryService.pollEventsOrVocabularies(routingContext.request(), routingContext.response(),
							inputString, null, null);
				} catch (ValidationException e) {
					EPCISServer.logger.error(e.getReason());
					HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(), 400);
				}
			} else {
				try {
					soapQueryService.getNextVocabularyPage(routingContext.request(), routingContext.response());
				} catch (ParserConfigurationException e) {
					ImplementationException e1 = new ImplementationException(ImplementationExceptionSeverity.ERROR,
							"Poll", e.getMessage());
					HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e1, e1.getClass(), 500);
				}
			}
		});
		EPCISServer.logger.info("[GET /epcis/vocabularies (application/xml)] - router added");

		router.get("/epcis/vocabularies").consumes("application/json").handler(routingContext -> {

			checkJSONPollHeaders(routingContext);
			if (routingContext.response().closed())
				return;

			routingContext.response().setChunked(true);

			String nextPageToken = routingContext.request().getParam("nextPageToken");
			if (nextPageToken == null) {
				JsonObject query = getJsonBody(routingContext);
				if (query == null) {
					HTTPUtil.sendQueryResults(routingContext.response(), JSONMessageFactory
							.get406NotAcceptableException("[406NotAcceptable] no valid simple event query (json)"),
							406);
					return;
				}
				restQueryService.query(routingContext, query, "SimpleMasterDataQuery");
			} else {
				UUID uuid = null;
				try {
					uuid = UUID.fromString(routingContext.request().getParam("nextPageToken"));
					restQueryService.getNextVocabularyPage(routingContext, uuid);
				} catch (Exception e) {
					HTTPUtil.sendQueryResults(routingContext.response(),
							JSONMessageFactory.get406NotAcceptableException(
									"[406NotAcceptable] The server cannot return the response as requested: invalid nextPageToken - "
											+ uuid),
							406);
					return;
				}

			}

		});
		EPCISServer.logger.info("[GET /epcis/vocabularies (application/json)] - router added");
	}

	/**
	 * Returns an individual EPCIS event.
	 * 
	 * @param router
	 * @param restQueryService
	 */
	public static void registerGetEventHandler(Router router, RESTQueryService restQueryService) {
		router.get("/epcis/events/:eventID").consumes("application/xml").handler(routingContext -> {
			checkXMLPollHeaders(routingContext);
			if (routingContext.response().closed())
				return;

			routingContext.response().setChunked(true);
			String eventID = null;
			try {
				eventID = routingContext.pathParam("eventID");
				List<Document> events = new ArrayList<Document>();
				EPCISServer.mEventCollection.find(new Document("eventID", eventID)).into(events);
				if (events.isEmpty()) {
					EPCISException e = new EPCISException(
							"[404NoSuchResourceException] There is no event with the given id: " + eventID);
					EPCISServer.logger.error(e.getReason());
					HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(), 404);
					return;
				}

				Document result = events.get(0);
				SOAPMessage message = new SOAPMessage();
				ArrayList<String> nsList = new ArrayList<>();

				String type = result.getString("type");
				if (type.equals("AggregationEvent")) {
					AggregationEventType event = new AggregationEventConverter().convert(result, message, nsList);
					HTTPUtil.sendQueryResults(routingContext.response(), message, event, AggregationEventType.class,
							200);
				} else if (type.equals("ObjectEvent")) {
					ObjectEventType event = new ObjectEventConverter().convert(result, message, nsList);
					HTTPUtil.sendQueryResults(routingContext.response(), message, event, ObjectEventType.class, 200);
				} else if (type.equals("TransactionEvent")) {
					TransactionEventType event = new TransactionEventConverter().convert(result, message, nsList);
					HTTPUtil.sendQueryResults(routingContext.response(), message, event, TransactionEventType.class,
							200);
				} else if (type.equals("TransformationEvent")) {
					TransformationEventType event = new TransformationEventConverter().convert(result, message, nsList);
					HTTPUtil.sendQueryResults(routingContext.response(), message, event, TransformationEventType.class,
							200);
				} else {
					AssociationEventType event = new AssociationEventConverter().convert(result, message, nsList);
					HTTPUtil.sendQueryResults(routingContext.response(), message, event, AssociationEventType.class,
							200);
				}
			} catch (Throwable throwable) {
				ImplementationException e1 = new ImplementationException(ImplementationExceptionSeverity.ERROR, "Poll",
						throwable.getMessage());
				HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e1, e1.getClass(), 500);
				return;
			}
		});

		router.get("/epcis/events/:eventID").consumes("application/json").handler(routingContext -> {
			checkJSONPollHeaders(routingContext);
			if (routingContext.response().closed())
				return;

			routingContext.response().setChunked(true);
			String eventID = null;
			try {
				eventID = routingContext.pathParam("eventID");
				List<Document> events = new ArrayList<Document>();
				EPCISServer.mEventCollection.find(new Document("eventID", eventID)).into(events);
				if (events.isEmpty()) {
					HTTPUtil.sendQueryResults(routingContext.response(), JSONMessageFactory
							.get404NoSuchResourceException("There is no event with the given id: " + eventID), 404);
					return;
				}

				Document result = events.get(0);

				JsonArray context = new JsonArray();
				context.add("https://ref.gs1.org/standards/epcis/2.0.0/epcis-context.jsonld");
				ArrayList<String> namespaces = getNamespaces(result);
				JsonObject extType = new JsonObject();
				JsonObject extContext = new JsonObject();
				for (int i = 0; i < namespaces.size(); i++) {
					extContext.put("ext" + i, decodeMongoObjectKey(namespaces.get(i)));
				}
				context.add(extContext);

				JsonObject convertedResult = null;
				switch (result.getString("type")) {
				case "AggregationEvent":
					convertedResult = EPCISServer.bsonToJsonConverter.convertAggregationEvent(result, namespaces,
							extType);

				case "ObjectEvent":
					convertedResult = EPCISServer.bsonToJsonConverter.convertObjectEvent(result, namespaces, extType);

				case "TransactionEvent":
					convertedResult = EPCISServer.bsonToJsonConverter.convertTransactionEvent(result, namespaces,
							extType);

				case "TransformationEvent":
					convertedResult = EPCISServer.bsonToJsonConverter.convertTransformationEvent(result, namespaces,
							extType);

				case "AssociationEvent":
					convertedResult = EPCISServer.bsonToJsonConverter.convertAssociationEvent(result, namespaces,
							extType);
				}
				HttpServerResponse serverResponse = routingContext.response();
				serverResponse.putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version).putHeader("GS1-Extensions",
						Metadata.GS1_Extensions);

				HTTPUtil.sendQueryResults(serverResponse, convertedResult, 200);

			} catch (Throwable throwable) {
				HTTPUtil.sendQueryResults(routingContext.response(),
						JSONMessageFactory.get500ImplementationException(throwable.getMessage()), 500);
				return;
			}
		});
	}

	public static void registerGetEPCQueriesHandler(Router router, RESTQueryService restQueryService) {
		router.get("/epcis/events/:eventID").consumes("application/xml").handler(routingContext -> {
			checkXMLPollHeaders(routingContext);
			if (routingContext.response().closed())
				return;

			routingContext.response().setChunked(true);
			String eventID = null;
			try {
				eventID = routingContext.pathParam("eventID");
				List<Document> events = new ArrayList<Document>();
				EPCISServer.mEventCollection.find(new Document("eventID", eventID)).into(events);
				if (events.isEmpty()) {
					EPCISException e = new EPCISException(
							"[404NoSuchResourceException] There is no event with the given id: " + eventID);
					EPCISServer.logger.error(e.getReason());
					HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(), 404);
					return;
				}

				Document result = events.get(0);
				SOAPMessage message = new SOAPMessage();
				ArrayList<String> nsList = new ArrayList<>();

				String type = result.getString("type");
				if (type.equals("AggregationEvent")) {
					AggregationEventType event = new AggregationEventConverter().convert(result, message, nsList);
					HTTPUtil.sendQueryResults(routingContext.response(), message, event, AggregationEventType.class,
							200);
				} else if (type.equals("ObjectEvent")) {
					ObjectEventType event = new ObjectEventConverter().convert(result, message, nsList);
					HTTPUtil.sendQueryResults(routingContext.response(), message, event, ObjectEventType.class, 200);
				} else if (type.equals("TransactionEvent")) {
					TransactionEventType event = new TransactionEventConverter().convert(result, message, nsList);
					HTTPUtil.sendQueryResults(routingContext.response(), message, event, TransactionEventType.class,
							200);
				} else if (type.equals("TransformationEvent")) {
					TransformationEventType event = new TransformationEventConverter().convert(result, message, nsList);
					HTTPUtil.sendQueryResults(routingContext.response(), message, event, TransformationEventType.class,
							200);
				} else {
					AssociationEventType event = new AssociationEventConverter().convert(result, message, nsList);
					HTTPUtil.sendQueryResults(routingContext.response(), message, event, AssociationEventType.class,
							200);
				}
			} catch (Throwable throwable) {
				ImplementationException e1 = new ImplementationException(ImplementationExceptionSeverity.ERROR, "Poll",
						throwable.getMessage());
				HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e1, e1.getClass(), 500);
				return;
			}
		});

		router.get("/epcis/events/:eventID").consumes("application/json").handler(routingContext -> {
			checkJSONPollHeaders(routingContext);
			if (routingContext.response().closed())
				return;

			routingContext.response().setChunked(true);
			String eventID = null;
			try {
				eventID = routingContext.pathParam("eventID");
				List<Document> events = new ArrayList<Document>();
				EPCISServer.mEventCollection.find(new Document("eventID", eventID)).into(events);
				if (events.isEmpty()) {
					HTTPUtil.sendQueryResults(routingContext.response(), JSONMessageFactory
							.get404NoSuchResourceException("There is no event with the given id: " + eventID), 404);
					return;
				}

				Document result = events.get(0);

				JsonArray context = new JsonArray();
				context.add("https://ref.gs1.org/standards/epcis/2.0.0/epcis-context.jsonld");
				ArrayList<String> namespaces = getNamespaces(result);
				JsonObject extType = new JsonObject();
				JsonObject extContext = new JsonObject();
				for (int i = 0; i < namespaces.size(); i++) {
					extContext.put("ext" + i, decodeMongoObjectKey(namespaces.get(i)));
				}
				context.add(extContext);

				JsonObject convertedResult = null;
				switch (result.getString("type")) {
				case "AggregationEvent":
					convertedResult = EPCISServer.bsonToJsonConverter.convertAggregationEvent(result, namespaces,
							extType);

				case "ObjectEvent":
					convertedResult = EPCISServer.bsonToJsonConverter.convertObjectEvent(result, namespaces, extType);

				case "TransactionEvent":
					convertedResult = EPCISServer.bsonToJsonConverter.convertTransactionEvent(result, namespaces,
							extType);

				case "TransformationEvent":
					convertedResult = EPCISServer.bsonToJsonConverter.convertTransformationEvent(result, namespaces,
							extType);

				case "AssociationEvent":
					convertedResult = EPCISServer.bsonToJsonConverter.convertAssociationEvent(result, namespaces,
							extType);
				}
				HttpServerResponse serverResponse = routingContext.response();
				serverResponse.putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version).putHeader("GS1-Extensions",
						Metadata.GS1_Extensions);

				HTTPUtil.sendQueryResults(serverResponse, convertedResult, 200);

			} catch (Throwable throwable) {
				HTTPUtil.sendQueryResults(routingContext.response(),
						JSONMessageFactory.get500ImplementationException(throwable.getMessage()), 500);
				return;
			}
		});
	}

	public static void registerGetVocabularyHandler(Router router, RESTQueryService restQueryService) {
		router.get("/epcis/vocabularies/:vocabularyID").consumes("application/xml").handler(routingContext -> {
			checkXMLPollHeaders(routingContext);
			if (routingContext.response().closed())
				return;

			routingContext.response().setChunked(true);
			String vocabularyID = null;
			try {
				vocabularyID = routingContext.pathParam("vocabularyID");
				List<Document> vocabularies = new ArrayList<Document>();
				EPCISServer.mVocCollection.find(new Document("id", TagDataTranslationEngine.toEPC(vocabularyID)))
						.into(vocabularies);
				if (vocabularies.isEmpty()) {
					EPCISException e = new EPCISException(
							"[404NoSuchResourceException] There is no vocabulary with the given id: " + vocabularyID);
					EPCISServer.logger.error(e.getReason());
					HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(), 400);
					return;
				}

				Document result = vocabularies.get(0);
				SOAPMessage message = new SOAPMessage();
				VocabularyType vocabulary = new MasterdataConverter().convert(result.getString("type"), result,
						message);
				HTTPUtil.sendQueryResults(routingContext.response(), message, vocabulary, VocabularyType.class, 200);
			} catch (ValidationException e) {
				SOAPMessage message = new SOAPMessage();
				EPCISException e1 = new EPCISException("Illegal vocabulary identifier: " + e.getReason());
				HTTPUtil.sendQueryResults(routingContext.response(), message, e1, e1.getClass(), 404);
				return;
			} catch (Throwable throwable) {
				ImplementationException e1 = new ImplementationException(ImplementationExceptionSeverity.ERROR, "Poll",
						throwable.getMessage());
				HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e1, e1.getClass(), 500);
				return;
			}
		});

		router.get("/epcis/vocabularies/:vocabularyID").consumes("application/json").handler(routingContext -> {
			checkJSONPollHeaders(routingContext);
			if (routingContext.response().closed())
				return;

			routingContext.response().setChunked(true);
			String vocabularyID = null;
			try {
				vocabularyID = routingContext.pathParam("vocabularyID");
				List<Document> vocabularies = new ArrayList<Document>();
				EPCISServer.mVocCollection.find(new Document("id", TagDataTranslationEngine.toEPC(vocabularyID)))
						.into(vocabularies);
				if (vocabularies.isEmpty()) {
					HTTPUtil.sendQueryResults(routingContext.response(), JSONMessageFactory
							.get404NoSuchResourceException("There is no vocabulary with the given id: " + vocabularyID),
							404);
					return;
				}

				Document result = vocabularies.get(0);

				JsonArray context = new JsonArray();
				context.add("https://ref.gs1.org/standards/epcis/2.0.0/epcis-context.jsonld");
				ArrayList<String> namespaces = RESTQueryService.getVocabularyNamespaces(vocabularies);

				JsonObject extType = new JsonObject();
				JsonObject extContext = new JsonObject();
				for (int i = 0; i < namespaces.size(); i++) {
					extContext.put("ext" + i, decodeMongoObjectKey(namespaces.get(i)));
				}
				context.add(extContext);

				JsonObject convertedResult = EPCISServer.bsonToJsonConverter.convertVocabulary(result, namespaces,
						extType);
				context.add(extType);

				convertedResult.put("@context", context);

				HttpServerResponse serverResponse = routingContext.response();
				serverResponse.putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version).putHeader("GS1-Extensions",
						Metadata.GS1_Extensions);

				HTTPUtil.sendQueryResults(serverResponse, convertedResult, 200);

			} catch (ValidationException e) {
				HTTPUtil.sendQueryResults(routingContext.response(), JSONMessageFactory
						.get404NoSuchResourceException("Illegal vocabulary identifier: " + e.getReason()), 404);
				return;
			} catch (Throwable throwable) {
				HTTPUtil.sendQueryResults(routingContext.response(),
						JSONMessageFactory.get500ImplementationException(throwable.getMessage()), 500);
				return;
			}
		});
	}

	/**
	 * Returns all EPCIS event types currently available in the EPCIS repository.
	 * 
	 */
	public static void registerGetEventTypes(Router router) {
		router.get("/epcis/eventTypes").consumes("application/xml").handler(routingContext -> {
			checkXMLPollHeaders(routingContext);
			if (routingContext.response().closed())
				return;
			HTTPUtil.sendQueryResults(routingContext.response(), new EventTypesMessage(), 200, "application/xml");
		});

		router.get("/epcis/eventTypes").consumes("application/json").handler(routingContext -> {
			checkJSONPollHeaders(routingContext);
			if (routingContext.response().closed())
				return;

			JsonArray eventTypes = new JsonArray();
			for (String eventType : DynamicResource.availableEventTypes) {
				eventTypes.add(eventType);
			}

			JsonObject result = new JsonObject().put("@set", eventTypes);

			HTTPUtil.sendQueryResults(routingContext.response(), result, 200);
		});
	}

	/**
	 * Returns known electronic product codes. An endpoint to list all electronic
	 * product codes known to this repository.
	 * 
	 */
	public static void registerGetEPCs(Router router, SOAPQueryService soapQueryService,
			RESTQueryService restQueryService) {
		router.get("/epcis/epcs").consumes("application/xml").handler(routingContext -> {
			checkXMLPollHeaders(routingContext);
			if (routingContext.response().closed())
				return;

			String nextPageToken = routingContext.request().getParam("nextPageToken");
			if (nextPageToken == null) {
				soapQueryService.getResources(routingContext.request(), routingContext.response(), "epc",
						EPCISServer.epcsPageMap, DynamicResource.availableEPCsInEvents,
						DynamicResource.availableEPCsInVocabularies);
			} else {
				UUID uuid = null;
				try {
					uuid = UUID.fromString(nextPageToken);
				} catch (Exception e) {
					HTTPUtil.sendQueryResults(routingContext.response(),
							JSONMessageFactory.get406NotAcceptableException(
									"[406NotAcceptable] The server cannot return the response as requested: invalid nextPageToken - "
											+ uuid),
							406);
					return;
				}
				soapQueryService.getNextResourcePage(routingContext.request(), routingContext.response(), "epc",
						EPCISServer.epcsPageMap, uuid);
			}

		});

		router.get("/epcis/epcs").consumes("application/json").handler(routingContext -> {
			checkJSONPollHeaders(routingContext);
			if (routingContext.response().closed())
				return;

			String nextPageToken = routingContext.request().getParam("nextPageToken");
			if (nextPageToken == null) {
				restQueryService.getResources(routingContext.request(), routingContext.response(), "epc",
						EPCISServer.epcsPageMap, DynamicResource.availableEPCsInEvents,
						DynamicResource.availableEPCsInVocabularies);
			} else {
				UUID uuid = null;
				try {
					uuid = UUID.fromString(nextPageToken);
				} catch (Exception e) {
					HTTPUtil.sendQueryResults(routingContext.response(),
							JSONMessageFactory.get406NotAcceptableException(
									"[406NotAcceptable] The server cannot return the response as requested: invalid nextPageToken - "
											+ uuid),
							406);
					return;
				}
				restQueryService.getNextResourcePage(routingContext.request(), routingContext.response(), "epc",
						EPCISServer.epcsPageMap, uuid);
			}
		});
	}

	/**
	 * Returns known business steps. This endpoint returns the CBV standard business
	 * steps as well as any custom business steps supported by this repository.
	 */
	public static void registerGetBizSteps(Router router, SOAPQueryService soapQueryService,
			RESTQueryService restQueryService) {
		router.get("/epcis/bizSteps").consumes("application/xml").handler(routingContext -> {
			checkXMLPollHeaders(routingContext);
			if (routingContext.response().closed())
				return;

			String nextPageToken = routingContext.request().getParam("nextPageToken");
			if (nextPageToken == null) {
				soapQueryService.getResources(routingContext.request(), routingContext.response(), "bizStep",
						EPCISServer.bizStepsPageMap, DynamicResource.availableBusinessSteps,
						new ConcurrentHashSet<String>());
			} else {
				UUID uuid = null;
				try {
					uuid = UUID.fromString(nextPageToken);
				} catch (Exception e) {
					HTTPUtil.sendQueryResults(routingContext.response(),
							JSONMessageFactory.get406NotAcceptableException(
									"[406NotAcceptable] The server cannot return the response as requested: invalid nextPageToken - "
											+ uuid),
							406);
					return;
				}
				soapQueryService.getNextResourcePage(routingContext.request(), routingContext.response(), "bizStep",
						EPCISServer.bizStepsPageMap, uuid);
			}

		});

		router.get("/epcis/bizSteps").consumes("application/json").handler(routingContext -> {
			checkJSONPollHeaders(routingContext);
			if (routingContext.response().closed())
				return;

			String nextPageToken = routingContext.request().getParam("nextPageToken");
			if (nextPageToken == null) {
				restQueryService.getResources(routingContext.request(), routingContext.response(), "bizStep",
						EPCISServer.bizStepsPageMap, DynamicResource.availableBusinessSteps,
						new ConcurrentHashSet<String>());
			} else {
				UUID uuid = null;
				try {
					uuid = UUID.fromString(nextPageToken);
				} catch (Exception e) {
					HTTPUtil.sendQueryResults(routingContext.response(),
							JSONMessageFactory.get406NotAcceptableException(
									"[406NotAcceptable] The server cannot return the response as requested: invalid nextPageToken - "
											+ uuid),
							406);
					return;
				}
				restQueryService.getNextResourcePage(routingContext.request(), routingContext.response(), "bizStep",
						EPCISServer.bizStepsPageMap, uuid);
			}
		});
	}

	/**
	 * Returns known business locations. An endpoint to list all the business
	 * locations known to this repository.
	 */
	public static void registerGetBizLocations(Router router, SOAPQueryService soapQueryService,
			RESTQueryService restQueryService) {
		router.get("/epcis/bizLocations").consumes("application/xml").handler(routingContext -> {
			checkXMLPollHeaders(routingContext);
			if (routingContext.response().closed())
				return;

			String nextPageToken = routingContext.request().getParam("nextPageToken");
			if (nextPageToken == null) {
				soapQueryService.getResources(routingContext.request(), routingContext.response(), "bizLocation",
						EPCISServer.bizLocationsPageMap, DynamicResource.availableBusinessLocationsInEvents,
						DynamicResource.availableBusinessLocationsInVocabularies);
			} else {
				UUID uuid = null;
				try {
					uuid = UUID.fromString(nextPageToken);
				} catch (Exception e) {
					HTTPUtil.sendQueryResults(routingContext.response(),
							JSONMessageFactory.get406NotAcceptableException(
									"[406NotAcceptable] The server cannot return the response as requested: invalid nextPageToken - "
											+ uuid),
							406);
					return;
				}
				soapQueryService.getNextResourcePage(routingContext.request(), routingContext.response(), "bizLocation",
						EPCISServer.bizLocationsPageMap, uuid);
			}

		});

		router.get("/epcis/bizLocations").consumes("application/json").handler(routingContext -> {
			checkJSONPollHeaders(routingContext);
			if (routingContext.response().closed())
				return;

			String nextPageToken = routingContext.request().getParam("nextPageToken");
			if (nextPageToken == null) {
				restQueryService.getResources(routingContext.request(), routingContext.response(), "bizLocation",
						EPCISServer.bizLocationsPageMap, DynamicResource.availableBusinessLocationsInEvents,
						DynamicResource.availableBusinessLocationsInVocabularies);
			} else {
				UUID uuid = null;
				try {
					uuid = UUID.fromString(nextPageToken);
				} catch (Exception e) {
					HTTPUtil.sendQueryResults(routingContext.response(),
							JSONMessageFactory.get406NotAcceptableException(
									"[406NotAcceptable] The server cannot return the response as requested: invalid nextPageToken - "
											+ uuid),
							406);
					return;
				}
				restQueryService.getNextResourcePage(routingContext.request(), routingContext.response(), "bizLocation",
						EPCISServer.bizLocationsPageMap, uuid);
			}
		});
	}

	/**
	 * Returns known read points. An endpoint to list all read points known to this
	 * repository.
	 */
	public static void registerGetReadPoints(Router router, SOAPQueryService soapQueryService,
			RESTQueryService restQueryService) {
		router.get("/epcis/readPoints").consumes("application/xml").handler(routingContext -> {
			checkXMLPollHeaders(routingContext);
			if (routingContext.response().closed())
				return;

			String nextPageToken = routingContext.request().getParam("nextPageToken");
			if (nextPageToken == null) {
				soapQueryService.getResources(routingContext.request(), routingContext.response(), "readPoint",
						EPCISServer.readPointsPageMap, DynamicResource.availableReadPointsInEvents,
						DynamicResource.availableReadPointsInVocabularies);
			} else {
				UUID uuid = null;
				try {
					uuid = UUID.fromString(nextPageToken);
				} catch (Exception e) {
					HTTPUtil.sendQueryResults(routingContext.response(),
							JSONMessageFactory.get406NotAcceptableException(
									"[406NotAcceptable] The server cannot return the response as requested: invalid nextPageToken - "
											+ uuid),
							406);
					return;
				}
				soapQueryService.getNextResourcePage(routingContext.request(), routingContext.response(), "readPoint",
						EPCISServer.readPointsPageMap, uuid);
			}

		});

		router.get("/epcis/readPoints").consumes("application/json").handler(routingContext -> {
			checkJSONPollHeaders(routingContext);
			if (routingContext.response().closed())
				return;

			String nextPageToken = routingContext.request().getParam("nextPageToken");
			if (nextPageToken == null) {
				restQueryService.getResources(routingContext.request(), routingContext.response(), "readPoint",
						EPCISServer.readPointsPageMap, DynamicResource.availableReadPointsInEvents,
						DynamicResource.availableReadPointsInVocabularies);
			} else {
				UUID uuid = null;
				try {
					uuid = UUID.fromString(nextPageToken);
				} catch (Exception e) {
					HTTPUtil.sendQueryResults(routingContext.response(),
							JSONMessageFactory.get406NotAcceptableException(
									"[406NotAcceptable] The server cannot return the response as requested: invalid nextPageToken - "
											+ uuid),
							406);
					return;
				}
				restQueryService.getNextResourcePage(routingContext.request(), routingContext.response(), "readPoint",
						EPCISServer.readPointsPageMap, uuid);
			}
		});
	}

	/**
	 * Returns known dispositions. This endpoint returns the CBV standard
	 * dispositions as well as any custom dispositions supported by this repository.
	 * 
	 */
	public static void registerGetDispositions(Router router, SOAPQueryService soapQueryService,
			RESTQueryService restQueryService) {
		router.get("/epcis/dispositions").consumes("application/xml").handler(routingContext -> {
			checkXMLPollHeaders(routingContext);
			if (routingContext.response().closed())
				return;

			String nextPageToken = routingContext.request().getParam("nextPageToken");
			if (nextPageToken == null) {
				soapQueryService.getResources(routingContext.request(), routingContext.response(), "disposition",
						EPCISServer.dispositionsPageMap, DynamicResource.availableDispositions,
						new ConcurrentHashSet<String>());
			} else {
				UUID uuid = null;
				try {
					uuid = UUID.fromString(nextPageToken);
				} catch (Exception e) {
					HTTPUtil.sendQueryResults(routingContext.response(),
							JSONMessageFactory.get406NotAcceptableException(
									"[406NotAcceptable] The server cannot return the response as requested: invalid nextPageToken - "
											+ uuid),
							406);
					return;
				}
				soapQueryService.getNextResourcePage(routingContext.request(), routingContext.response(), "disposition",
						EPCISServer.dispositionsPageMap, uuid);
			}

		});

		router.get("/epcis/dispositions").consumes("application/json").handler(routingContext -> {
			checkJSONPollHeaders(routingContext);
			if (routingContext.response().closed())
				return;

			String nextPageToken = routingContext.request().getParam("nextPageToken");
			if (nextPageToken == null) {
				restQueryService.getResources(routingContext.request(), routingContext.response(), "disposition",
						EPCISServer.dispositionsPageMap, DynamicResource.availableDispositions,
						new ConcurrentHashSet<String>());
			} else {
				UUID uuid = null;
				try {
					uuid = UUID.fromString(nextPageToken);
				} catch (Exception e) {
					HTTPUtil.sendQueryResults(routingContext.response(),
							JSONMessageFactory.get406NotAcceptableException(
									"[406NotAcceptable] The server cannot return the response as requested: invalid nextPageToken - "
											+ uuid),
							406);
					return;
				}
				restQueryService.getNextResourcePage(routingContext.request(), routingContext.response(), "disposition",
						EPCISServer.dispositionsPageMap, uuid);
			}
		});
	}

	/**
	 * Returns all sub-resources of an EPCIS event type. This endpoint returns all
	 * sub-resources of an EPCIS event type (for HATEOAS discovery), which includes
	 * at least `events`. A server may add additional endpoints, for example
	 * `schema` to access the EPCIS event type schema.
	 * 
	 */
	public static void registerGetEventTypeQueries(Router router) {
		router.get("/epcis/eventTypes/:eventType").consumes("application/xml").handler(routingContext -> {
			checkXMLPollHeaders(routingContext);
			if (routingContext.response().closed())
				return;

			String eventType = routingContext.pathParam("eventType");

			if (!DynamicResource.availableEventTypes.contains(eventType)) {
				EPCISException e = new EPCISException(
						"[404NoSuchResourceException] There is no available query for eventType: " + eventType);
				EPCISServer.logger.error(e.getReason());
				HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(), 404);
				return;
			} else {
				HTTPUtil.sendQueryResults(routingContext.response(),
						ResourcePage.getQueryNamesResults(List.of("events")), 200, "application/xml");
			}

		});

		router.get("/epcis/eventTypes/:eventType").consumes("application/json").handler(routingContext -> {
			checkJSONPollHeaders(routingContext);
			if (routingContext.response().closed())
				return;

			String eventType = routingContext.pathParam("eventType");

			if (!DynamicResource.availableEventTypes.contains(eventType)) {
				HTTPUtil.sendQueryResults(routingContext.response(),
						JSONMessageFactory.get404NoSuchResourceException(
								"[404NoSuchResourceException] There is no available query for eventType: " + eventType),
						404);
				return;
			} else {
				HTTPUtil.sendQueryResults(routingContext.response(),
						new JsonObject().put("@set", new JsonArray().add("events")), 200);
			}
		});
	}

	public static void registerGetEPCQueries(Router router) {
		String resourceType = "epc";

		router.get("/epcis/" + resourceType + "s/:" + resourceType).consumes("application/xml")
				.handler(routingContext -> {
					checkXMLPollHeaders(routingContext);
					if (routingContext.response().closed())
						return;

					String resource = routingContext.pathParam(resourceType);
					ArrayList<String> queryNames = new ArrayList<String>();
					if (DynamicResource.availableEPCsInEvents.contains(resource)) {
						queryNames.add("events");
					}
					if (DynamicResource.availableEPCsInVocabularies.contains(resource)) {
						queryNames.add("vocabularies");
					}

					HTTPUtil.sendQueryResults(routingContext.response(), ResourcePage.getQueryNamesResults(queryNames),
							200, "application/xml");
				});

		router.get("/epcis/" + resourceType + "s/:" + resourceType).consumes("application/json")
				.handler(routingContext -> {
					checkJSONPollHeaders(routingContext);
					if (routingContext.response().closed())
						return;

					String resource = routingContext.pathParam(resourceType);

					try {
						resource = TagDataTranslationEngine.toEPC(resource);
					} catch (ValidationException e) {

					}

					JsonArray queryNames = new JsonArray();
					if (DynamicResource.availableEPCsInEvents.contains(resource)) {
						queryNames.add("events");
					}
					if (DynamicResource.availableEPCsInVocabularies.contains(resource)) {
						queryNames.add("vocabularies");
					}

					HTTPUtil.sendQueryResults(routingContext.response(), new JsonObject().put("@set", queryNames), 200);

				});
	}

	public static void registerGetBizStepQueries(Router router) {
		String resourceType = "bizStep";

		router.get("/epcis/" + resourceType + "s/:" + resourceType).consumes("application/xml")
				.handler(routingContext -> {
					checkXMLPollHeaders(routingContext);
					if (routingContext.response().closed())
						return;

					String resource = routingContext.pathParam(resourceType);
					ArrayList<String> queryNames = new ArrayList<String>();
					if (DynamicResource.availableBusinessSteps.contains(resource)) {
						queryNames.add("events");
					}

					HTTPUtil.sendQueryResults(routingContext.response(), ResourcePage.getQueryNamesResults(queryNames),
							200, "application/xml");
				});

		router.get("/epcis/" + resourceType + "s/:" + resourceType).consumes("application/json")
				.handler(routingContext -> {
					checkJSONPollHeaders(routingContext);
					if (routingContext.response().closed())
						return;

					String resource = routingContext.pathParam(resourceType);

					try {
						resource = BusinessStep.getFullVocabularyName(resource);
					} catch (Exception e) {

					}

					JsonArray queryNames = new JsonArray();
					if (DynamicResource.availableBusinessSteps.contains(resource)) {
						queryNames.add("events");
					}

					HTTPUtil.sendQueryResults(routingContext.response(), new JsonObject().put("@set", queryNames), 200);

				});
	}

	public static void registerGetBizLocationQueries(Router router) {
		String resourceType = "bizLocation";

		router.get("/epcis/" + resourceType + "s/:" + resourceType).consumes("application/xml")
				.handler(routingContext -> {
					checkXMLPollHeaders(routingContext);
					if (routingContext.response().closed())
						return;

					String resource = routingContext.pathParam(resourceType);
					ArrayList<String> queryNames = new ArrayList<String>();
					if (DynamicResource.availableBusinessLocationsInEvents.contains(resource)) {
						queryNames.add("events");
					}
					if (DynamicResource.availableBusinessLocationsInVocabularies.contains(resource)) {
						queryNames.add("vocabularies");
					}

					HTTPUtil.sendQueryResults(routingContext.response(), ResourcePage.getQueryNamesResults(queryNames),
							200, "application/xml");
				});

		router.get("/epcis/" + resourceType + "s/:" + resourceType).consumes("application/json")
				.handler(routingContext -> {
					checkJSONPollHeaders(routingContext);
					if (routingContext.response().closed())
						return;

					String resource = routingContext.pathParam(resourceType);

					try {
						resource = TagDataTranslationEngine.toEPC(resource);
					} catch (ValidationException e) {

					}

					JsonArray queryNames = new JsonArray();
					if (DynamicResource.availableBusinessLocationsInEvents.contains(resource)) {
						queryNames.add("events");
					}
					if (DynamicResource.availableBusinessLocationsInVocabularies.contains(resource)) {
						queryNames.add("vocabularies");
					}

					HTTPUtil.sendQueryResults(routingContext.response(), new JsonObject().put("@set", queryNames), 200);

				});
	}

	public static void registerGetReadPointQueries(Router router) {
		String resourceType = "readPoint";

		router.get("/epcis/" + resourceType + "s/:" + resourceType).consumes("application/xml")
				.handler(routingContext -> {
					checkXMLPollHeaders(routingContext);
					if (routingContext.response().closed())
						return;

					String resource = routingContext.pathParam(resourceType);
					ArrayList<String> queryNames = new ArrayList<String>();
					if (DynamicResource.availableReadPointsInEvents.contains(resource)) {
						queryNames.add("events");
					}
					if (DynamicResource.availableReadPointsInVocabularies.contains(resource)) {
						queryNames.add("vocabularies");
					}

					HTTPUtil.sendQueryResults(routingContext.response(), ResourcePage.getQueryNamesResults(queryNames),
							200, "application/xml");
				});

		router.get("/epcis/" + resourceType + "s/:" + resourceType).consumes("application/json")
				.handler(routingContext -> {
					checkJSONPollHeaders(routingContext);
					if (routingContext.response().closed())
						return;

					String resource = routingContext.pathParam(resourceType);

					try {
						resource = TagDataTranslationEngine.toEPC(resource);
					} catch (ValidationException e) {

					}

					JsonArray queryNames = new JsonArray();
					if (DynamicResource.availableReadPointsInEvents.contains(resource)) {
						queryNames.add("events");
					}
					if (DynamicResource.availableReadPointsInVocabularies.contains(resource)) {
						queryNames.add("vocabularies");
					}

					HTTPUtil.sendQueryResults(routingContext.response(), new JsonObject().put("@set", queryNames), 200);

				});
	}

	public static void registerGetDispositionQueries(Router router) {
		String resourceType = "disposition";

		router.get("/epcis/" + resourceType + "s/:" + resourceType).consumes("application/xml")
				.handler(routingContext -> {
					checkXMLPollHeaders(routingContext);
					if (routingContext.response().closed())
						return;

					String resource = routingContext.pathParam(resourceType);
					ArrayList<String> queryNames = new ArrayList<String>();
					if (DynamicResource.availableDispositions.contains(resource)) {
						queryNames.add("events");
					}

					HTTPUtil.sendQueryResults(routingContext.response(), ResourcePage.getQueryNamesResults(queryNames),
							200, "application/xml");
				});

		router.get("/epcis/" + resourceType + "s/:" + resourceType).consumes("application/json")
				.handler(routingContext -> {
					checkJSONPollHeaders(routingContext);
					if (routingContext.response().closed())
						return;

					String resource = routingContext.pathParam(resourceType);

					try {
						resource = Disposition.getFullVocabularyName(resource);
					} catch (Exception e) {

					}

					JsonArray queryNames = new JsonArray();
					if (DynamicResource.availableDispositions.contains(resource)) {
						queryNames.add("events");
					}

					HTTPUtil.sendQueryResults(routingContext.response(), new JsonObject().put("@set", queryNames), 200);

				});
	}

	/**
	 * Returns EPCIS events of a given an EPCIS event type.
	 * 
	 */
	public static void registerGetEventsWithEventTypeHandler(Router router, SOAPQueryService soapQueryService,
			RESTQueryService restQueryService) {

		String resourceType = "eventType";

		router.get("/epcis/" + resourceType + "s/:" + resourceType + "/events").consumes("application/xml")
				.handler(routingContext -> {

					if (!isHeaderPassed(routingContext))
						return;

					routingContext.response().setChunked(true);

					String resource = routingContext.pathParam(resourceType);

					if (!DynamicResource.availableEventTypes.contains(resource)) {
						EPCISException e = new EPCISException(
								"[404NoSuchResourceException] There is no available query for eventType: " + resource);
						EPCISServer.logger.error(e.getReason());
						HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(), 404);
						return;
					}

					String nextPageToken = routingContext.request().getParam("nextPageToken");
					if (nextPageToken == null) {
						String inputString = getHttpBody(routingContext, "SimpleEventQuery");
						if (inputString == null)
							return;
						try {
							soapQueryService.pollEventsOrVocabularies(routingContext.request(),
									routingContext.response(), inputString, resourceType, resource);
						} catch (ValidationException e) {
							EPCISServer.logger.error(e.getReason());
							HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(),
									400);
						}
					} else {
						soapQueryService.getNextEventPage(routingContext.request(), routingContext.response());
					}
				});
		EPCISServer.logger.info(
				"[GET /epcis/" + resourceType + "s/:" + resourceType + "/events (application/xml)] - router added");

		router.get("/epcis/" + resourceType + "s/:" + resourceType + "/events").consumes("application/json")
				.handler(routingContext -> {

					checkJSONPollHeaders(routingContext);
					if (routingContext.response().closed())
						return;

					routingContext.response().setChunked(true);

					String resource = routingContext.pathParam(resourceType);

					if (!DynamicResource.availableEventTypes.contains(resource)) {
						HTTPUtil.sendQueryResults(routingContext.response(),
								JSONMessageFactory.get404NoSuchResourceException(
										"[404NoSuchResourceException] There is no available query for eventType: "
												+ resource),
								404);
						return;
					}

					String nextPageToken = routingContext.request().getParam("nextPageToken");
					if (nextPageToken == null) {
						JsonObject query = getJsonBody(routingContext);
						if (query == null) {
							HTTPUtil.sendQueryResults(routingContext.response(),
									JSONMessageFactory.get406NotAcceptableException(
											"[406NotAcceptable] no valid simple event query (json)"),
									406);
							return;
						}
						query.put(resourceType, new JsonArray().add(resource));
						restQueryService.query(routingContext, query, "SimpleEventQuery");
					} else {
						UUID uuid = null;
						try {
							uuid = UUID.fromString(nextPageToken);
						} catch (Exception e) {
							HTTPUtil.sendQueryResults(routingContext.response(),
									JSONMessageFactory.get406NotAcceptableException(
											"[406NotAcceptable] The server cannot return the response as requested: invalid nextPageToken - "
													+ uuid),
									406);
							return;
						}
						restQueryService.getNextEventPage(routingContext, uuid);
					}
				});
		EPCISServer.logger.info(
				"[GET /epcis/" + resourceType + "s/:" + resourceType + "/events (application/json)] - router added");
	}

	public static void registerGetEventsWithEPCHandler(Router router, SOAPQueryService soapQueryService,
			RESTQueryService restQueryService) {

		String resourceType = "epc";

		router.get("/epcis/" + resourceType + "s/:" + resourceType + "/events").consumes("application/xml")
				.handler(routingContext -> {

					if (!isHeaderPassed(routingContext))
						return;

					routingContext.response().setChunked(true);

					String resource = routingContext.pathParam(resourceType);

					if (!DynamicResource.availableEPCsInEvents.contains(resource)) {
						EPCISException e = new EPCISException(
								"[404NoSuchResourceException] There is no available query for: " + resource);
						EPCISServer.logger.error(e.getReason());
						HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(), 404);
						return;
					}

					String nextPageToken = routingContext.request().getParam("nextPageToken");
					if (nextPageToken == null) {
						String inputString = getHttpBody(routingContext, "SimpleEventQuery");
						if (inputString == null)
							return;
						try {
							boolean isInstanceLevel = true;
							try {
								TagDataTranslationEngine.checkEPCClassPureIdentity(StaticResource.gcpLength, resource);
								isInstanceLevel = false;
							} catch (ValidationException e1) {

							}

							if (isInstanceLevel) {
								soapQueryService.pollEventsOrVocabularies(routingContext.request(),
										routingContext.response(), inputString, "MATCH_anyEPC", resource);
							} else {
								soapQueryService.pollEventsOrVocabularies(routingContext.request(),
										routingContext.response(), inputString, "MATCH_anyEPCClass", resource);
							}
						} catch (ValidationException e) {
							EPCISServer.logger.error(e.getReason());
							HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(),
									400);
						}
					} else {
						soapQueryService.getNextEventPage(routingContext.request(), routingContext.response());
					}
				});
		EPCISServer.logger.info(
				"[GET /epcis/" + resourceType + "s/:" + resourceType + "/events (application/xml)] - router added");

		router.get("/epcis/" + resourceType + "s/:" + resourceType + "/events").consumes("application/json")
				.handler(routingContext -> {

					checkJSONPollHeaders(routingContext);
					if (routingContext.response().closed())
						return;

					routingContext.response().setChunked(true);

					String resource = routingContext.pathParam(resourceType);

					try {
						resource = TagDataTranslationEngine.toEPC(resource);
					} catch (ValidationException e) {

					}

					if (!DynamicResource.availableEPCsInEvents.contains(resource)) {
						HTTPUtil.sendQueryResults(routingContext.response(),
								JSONMessageFactory.get404NoSuchResourceException(
										"[404NoSuchResourceException] There is no available query for: " + resource),
								404);
						return;
					}

					String nextPageToken = routingContext.request().getParam("nextPageToken");
					if (nextPageToken == null) {
						JsonObject query = getJsonBody(routingContext);
						if (query == null) {
							HTTPUtil.sendQueryResults(routingContext.response(),
									JSONMessageFactory.get406NotAcceptableException(
											"[406NotAcceptable] no valid simple event query (json)"),
									406);
							return;
						}
						boolean isInstanceLevel = true;
						try {
							TagDataTranslationEngine.checkEPCClassPureIdentity(StaticResource.gcpLength, resource);
							isInstanceLevel = false;
						} catch (ValidationException e1) {

						}

						if (isInstanceLevel) {
							query.put("MATCH_anyEPC", new JsonArray().add(resource));
						} else {
							query.put("MATCH_anyEPCClass", new JsonArray().add(resource));
						}

						restQueryService.query(routingContext, query, "SimpleEventQuery");
					} else {
						UUID uuid = null;
						try {
							uuid = UUID.fromString(nextPageToken);
						} catch (Exception e) {
							HTTPUtil.sendQueryResults(routingContext.response(),
									JSONMessageFactory.get406NotAcceptableException(
											"[406NotAcceptable] The server cannot return the response as requested: invalid nextPageToken - "
													+ uuid),
									406);
							return;
						}
						restQueryService.getNextEventPage(routingContext, uuid);
					}
				});
		EPCISServer.logger.info(
				"[GET /epcis/" + resourceType + "s/:" + resourceType + "/events (application/json)] - router added");
	}

	

	public static void registerGetEventsWithBizStepHandler(Router router, SOAPQueryService soapQueryService,
			RESTQueryService restQueryService) {

		String resourceType = "bizStep";

		router.get("/epcis/" + resourceType + "s/:" + resourceType + "/events").consumes("application/xml")
				.handler(routingContext -> {

					if (!isHeaderPassed(routingContext))
						return;

					routingContext.response().setChunked(true);

					String resource = routingContext.pathParam(resourceType);

					if (!DynamicResource.availableBusinessSteps.contains(resource)) {
						EPCISException e = new EPCISException(
								"[404NoSuchResourceException] There is no available query for: " + resource);
						EPCISServer.logger.error(e.getReason());
						HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(), 404);
						return;
					}

					String nextPageToken = routingContext.request().getParam("nextPageToken");
					if (nextPageToken == null) {
						String inputString = getHttpBody(routingContext, "SimpleEventQuery");
						if (inputString == null)
							return;
						try {
							soapQueryService.pollEventsOrVocabularies(routingContext.request(),
									routingContext.response(), inputString, "EQ_bizStep", resource);
						} catch (ValidationException e) {
							EPCISServer.logger.error(e.getReason());
							HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(),
									400);
						}
					} else {
						soapQueryService.getNextEventPage(routingContext.request(), routingContext.response());
					}
				});
		EPCISServer.logger.info(
				"[GET /epcis/" + resourceType + "s/:" + resourceType + "/events (application/xml)] - router added");

		router.get("/epcis/" + resourceType + "s/:" + resourceType + "/events").consumes("application/json")
				.handler(routingContext -> {

					checkJSONPollHeaders(routingContext);
					if (routingContext.response().closed())
						return;

					routingContext.response().setChunked(true);

					String resource = routingContext.pathParam(resourceType);

					String fullResource = resource;
					try {
						fullResource = BusinessStep.getFullVocabularyName(resource);
					} catch (Exception e) {

					}

					if (!DynamicResource.availableBusinessSteps.contains(fullResource)) {
						HTTPUtil.sendQueryResults(routingContext.response(),
								JSONMessageFactory.get404NoSuchResourceException(
										"[404NoSuchResourceException] There is no available query for: " + resource),
								404);
						return;
					}

					String nextPageToken = routingContext.request().getParam("nextPageToken");
					if (nextPageToken == null) {
						JsonObject query = getJsonBody(routingContext);
						if (query == null) {
							HTTPUtil.sendQueryResults(routingContext.response(),
									JSONMessageFactory.get406NotAcceptableException(
											"[406NotAcceptable] no valid simple event query (json)"),
									406);
							return;
						}
						query.put("EQ_bizStep", new JsonArray().add(resource));
						restQueryService.query(routingContext, query, "SimpleEventQuery");
					} else {
						UUID uuid = null;
						try {
							uuid = UUID.fromString(nextPageToken);
						} catch (Exception e) {
							HTTPUtil.sendQueryResults(routingContext.response(),
									JSONMessageFactory.get406NotAcceptableException(
											"[406NotAcceptable] The server cannot return the response as requested: invalid nextPageToken - "
													+ uuid),
									406);
							return;
						}
						restQueryService.getNextEventPage(routingContext, uuid);
					}
				});
		EPCISServer.logger.info(
				"[GET /epcis/" + resourceType + "s/:" + resourceType + "/events (application/json)] - router added");
	}

	public static void registerGetEventsWithBizLocationHandler(Router router, SOAPQueryService soapQueryService,
			RESTQueryService restQueryService) {

		String resourceType = "bizLocation";

		router.get("/epcis/" + resourceType + "s/:" + resourceType + "/events").consumes("application/xml")
				.handler(routingContext -> {

					if (!isHeaderPassed(routingContext))
						return;

					routingContext.response().setChunked(true);

					String resource = routingContext.pathParam(resourceType);

					if (!DynamicResource.availableBusinessLocationsInEvents.contains(resource)) {
						EPCISException e = new EPCISException(
								"[404NoSuchResourceException] There is no available query for: " + resource);
						EPCISServer.logger.error(e.getReason());
						HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(), 404);
						return;
					}

					String nextPageToken = routingContext.request().getParam("nextPageToken");
					if (nextPageToken == null) {
						String inputString = getHttpBody(routingContext, "SimpleEventQuery");
						if (inputString == null)
							return;
						try {
							soapQueryService.pollEventsOrVocabularies(routingContext.request(),
									routingContext.response(), inputString, "EQ_bizLocation", resource);
						} catch (ValidationException e) {
							EPCISServer.logger.error(e.getReason());
							HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(),
									400);
						}
					} else {
						soapQueryService.getNextEventPage(routingContext.request(), routingContext.response());
					}
				});
		EPCISServer.logger.info(
				"[GET /epcis/" + resourceType + "s/:" + resourceType + "/events (application/xml)] - router added");

		router.get("/epcis/" + resourceType + "s/:" + resourceType + "/events").consumes("application/json")
				.handler(routingContext -> {

					checkJSONPollHeaders(routingContext);
					if (routingContext.response().closed())
						return;

					routingContext.response().setChunked(true);

					String resource = routingContext.pathParam(resourceType);

					try {
						resource = TagDataTranslationEngine.toEPC(resource);
					} catch (ValidationException e) {

					}

					if (!DynamicResource.availableBusinessLocationsInEvents.contains(resource)) {
						HTTPUtil.sendQueryResults(routingContext.response(),
								JSONMessageFactory.get404NoSuchResourceException(
										"[404NoSuchResourceException] There is no available query for: " + resource),
								404);
						return;
					}

					String nextPageToken = routingContext.request().getParam("nextPageToken");
					if (nextPageToken == null) {
						JsonObject query = getJsonBody(routingContext);
						if (query == null) {
							HTTPUtil.sendQueryResults(routingContext.response(),
									JSONMessageFactory.get406NotAcceptableException(
											"[406NotAcceptable] no valid simple event query (json)"),
									406);
							return;
						}
						query.put("EQ_bizLocation", new JsonArray().add(resource));
						restQueryService.query(routingContext, query, "SimpleEventQuery");
					} else {
						UUID uuid = null;
						try {
							uuid = UUID.fromString(nextPageToken);
						} catch (Exception e) {
							HTTPUtil.sendQueryResults(routingContext.response(),
									JSONMessageFactory.get406NotAcceptableException(
											"[406NotAcceptable] The server cannot return the response as requested: invalid nextPageToken - "
													+ uuid),
									406);
							return;
						}
						restQueryService.getNextEventPage(routingContext, uuid);
					}
				});
		EPCISServer.logger.info(
				"[GET /epcis/" + resourceType + "s/:" + resourceType + "/events (application/json)] - router added");
	}

	public static void registerGetEventsWithReadPointHandler(Router router, SOAPQueryService soapQueryService,
			RESTQueryService restQueryService) {

		String resourceType = "readPoint";

		router.get("/epcis/" + resourceType + "s/:" + resourceType + "/events").consumes("application/xml")
				.handler(routingContext -> {

					if (!isHeaderPassed(routingContext))
						return;

					routingContext.response().setChunked(true);

					String resource = routingContext.pathParam(resourceType);

					if (!DynamicResource.availableReadPointsInEvents.contains(resource)) {
						EPCISException e = new EPCISException(
								"[404NoSuchResourceException] There is no available query for: " + resource);
						EPCISServer.logger.error(e.getReason());
						HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(), 404);
						return;
					}

					String nextPageToken = routingContext.request().getParam("nextPageToken");
					if (nextPageToken == null) {
						String inputString = getHttpBody(routingContext, "SimpleEventQuery");
						if (inputString == null)
							return;
						try {
							soapQueryService.pollEventsOrVocabularies(routingContext.request(),
									routingContext.response(), inputString, "EQ_readPoint", resource);
						} catch (ValidationException e) {
							EPCISServer.logger.error(e.getReason());
							HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(),
									400);
						}
					} else {
						soapQueryService.getNextEventPage(routingContext.request(), routingContext.response());
					}
				});
		EPCISServer.logger.info(
				"[GET /epcis/" + resourceType + "s/:" + resourceType + "/events (application/xml)] - router added");

		router.get("/epcis/" + resourceType + "s/:" + resourceType + "/events").consumes("application/json")
				.handler(routingContext -> {

					checkJSONPollHeaders(routingContext);
					if (routingContext.response().closed())
						return;

					routingContext.response().setChunked(true);

					String resource = routingContext.pathParam(resourceType);

					try {
						resource = TagDataTranslationEngine.toEPC(resource);
					} catch (ValidationException e) {

					}

					if (!DynamicResource.availableReadPointsInEvents.contains(resource)) {
						HTTPUtil.sendQueryResults(routingContext.response(),
								JSONMessageFactory.get404NoSuchResourceException(
										"[404NoSuchResourceException] There is no available query for: " + resource),
								404);
						return;
					}

					String nextPageToken = routingContext.request().getParam("nextPageToken");
					if (nextPageToken == null) {
						JsonObject query = getJsonBody(routingContext);
						if (query == null) {
							HTTPUtil.sendQueryResults(routingContext.response(),
									JSONMessageFactory.get406NotAcceptableException(
											"[406NotAcceptable] no valid simple event query (json)"),
									406);
							return;
						}
						query.put("EQ_readPoint", new JsonArray().add(resource));
						restQueryService.query(routingContext, query, "SimpleEventQuery");
					} else {
						UUID uuid = null;
						try {
							uuid = UUID.fromString(nextPageToken);
						} catch (Exception e) {
							HTTPUtil.sendQueryResults(routingContext.response(),
									JSONMessageFactory.get406NotAcceptableException(
											"[406NotAcceptable] The server cannot return the response as requested: invalid nextPageToken - "
													+ uuid),
									406);
							return;
						}
						restQueryService.getNextEventPage(routingContext, uuid);
					}
				});
		EPCISServer.logger.info(
				"[GET /epcis/" + resourceType + "s/:" + resourceType + "/events (application/json)] - router added");
	}

	public static void registerGetEventsWithDispositionHandler(Router router, SOAPQueryService soapQueryService,
			RESTQueryService restQueryService) {

		String resourceType = "disposition";

		router.get("/epcis/" + resourceType + "s/:" + resourceType + "/events").consumes("application/xml")
				.handler(routingContext -> {

					if (!isHeaderPassed(routingContext))
						return;

					routingContext.response().setChunked(true);

					String resource = routingContext.pathParam(resourceType);

					if (!DynamicResource.availableDispositions.contains(resource)) {
						EPCISException e = new EPCISException(
								"[404NoSuchResourceException] There is no available query for: " + resource);
						EPCISServer.logger.error(e.getReason());
						HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(), 404);
						return;
					}

					String nextPageToken = routingContext.request().getParam("nextPageToken");
					if (nextPageToken == null) {
						String inputString = getHttpBody(routingContext, "SimpleEventQuery");
						if (inputString == null)
							return;
						try {
							soapQueryService.pollEventsOrVocabularies(routingContext.request(),
									routingContext.response(), inputString, "EQ_disposition", resource);
						} catch (ValidationException e) {
							EPCISServer.logger.error(e.getReason());
							HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(),
									400);
						}
					} else {
						soapQueryService.getNextEventPage(routingContext.request(), routingContext.response());
					}
				});
		EPCISServer.logger.info(
				"[GET /epcis/" + resourceType + "s/:" + resourceType + "/events (application/xml)] - router added");

		router.get("/epcis/" + resourceType + "s/:" + resourceType + "/events").consumes("application/json")
				.handler(routingContext -> {

					checkJSONPollHeaders(routingContext);
					if (routingContext.response().closed())
						return;

					routingContext.response().setChunked(true);

					String resource = routingContext.pathParam(resourceType);

					String fullResource = resource;
					try {
						fullResource = Disposition.getFullVocabularyName(resource);
					} catch (Exception e) {

					}

					if (!DynamicResource.availableDispositions.contains(fullResource)) {
						HTTPUtil.sendQueryResults(routingContext.response(),
								JSONMessageFactory.get404NoSuchResourceException(
										"[404NoSuchResourceException] There is no available query for: " + resource),
								404);
						return;
					}

					String nextPageToken = routingContext.request().getParam("nextPageToken");
					if (nextPageToken == null) {
						JsonObject query = getJsonBody(routingContext);
						if (query == null) {
							HTTPUtil.sendQueryResults(routingContext.response(),
									JSONMessageFactory.get406NotAcceptableException(
											"[406NotAcceptable] no valid simple event query (json)"),
									406);
							return;
						}
						query.put("EQ_disposition", new JsonArray().add(resource));
						restQueryService.query(routingContext, query, "SimpleEventQuery");
					} else {
						UUID uuid = null;
						try {
							uuid = UUID.fromString(nextPageToken);
						} catch (Exception e) {
							HTTPUtil.sendQueryResults(routingContext.response(),
									JSONMessageFactory.get406NotAcceptableException(
											"[406NotAcceptable] The server cannot return the response as requested: invalid nextPageToken - "
													+ uuid),
									406);
							return;
						}
						restQueryService.getNextEventPage(routingContext, uuid);
					}
				});
		EPCISServer.logger.info(
				"[GET /epcis/" + resourceType + "s/:" + resourceType + "/events (application/json)] - router added");
	}
	
	public static void registerGetVocabulariesWithEPCHandler(Router router, SOAPQueryService soapQueryService,
			RESTQueryService restQueryService) {

		String resourceType = "epc";

		router.get("/epcis/" + resourceType + "s/:" + resourceType + "/vocabularies").consumes("application/xml")
				.handler(routingContext -> {

					if (!isHeaderPassed(routingContext))
						return;

					routingContext.response().setChunked(true);

					String resource = routingContext.pathParam(resourceType);

					if (!DynamicResource.availableEPCsInVocabularies.contains(resource)) {
						EPCISException e = new EPCISException(
								"[404NoSuchResourceException] There is no available query for: " + resource);
						EPCISServer.logger.error(e.getReason());
						HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(), 404);
						return;
					}

					String nextPageToken = routingContext.request().getParam("nextPageToken");
					if (nextPageToken == null) {
						String inputString = getHttpBody(routingContext, "SimpleMasterDataQuery");
						if (inputString == null)
							return;
						try {
							soapQueryService.pollEventsOrVocabularies(routingContext.request(),
									routingContext.response(), inputString, "EQ_name", resource, "vocabularyName",
									"urn:epcglobal:epcis:vtype:EPCClass");
						} catch (ValidationException e) {
							EPCISServer.logger.error(e.getReason());
							HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(),
									400);
						}
					} else {
						try {
							soapQueryService.getNextVocabularyPage(routingContext.request(), routingContext.response());
						} catch (ParserConfigurationException e) {
							ImplementationException e1 = new ImplementationException(
									ImplementationExceptionSeverity.ERROR, "Poll", e.getMessage());
							HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e1, e1.getClass(),
									500);
						}
					}
				});

		EPCISServer.logger.info("[GET /epcis/" + resourceType + "s/:" + resourceType
				+ "/vocabularies  (application/xml)] - router added");

		router.get("/epcis/" + resourceType + "s/:" + resourceType + "/vocabularies").consumes("application/json")
				.handler(routingContext -> {

					checkJSONPollHeaders(routingContext);
					if (routingContext.response().closed())
						return;

					routingContext.response().setChunked(true);

					String resource = routingContext.pathParam(resourceType);

					try {
						resource = TagDataTranslationEngine.toEPC(resource);
					} catch (ValidationException e) {

					}

					if (!DynamicResource.availableEPCsInVocabularies.contains(resource)) {
						HTTPUtil.sendQueryResults(routingContext.response(),
								JSONMessageFactory.get404NoSuchResourceException(
										"[404NoSuchResourceException] There is no available query for: " + resource),
								404);
						return;
					}

					String nextPageToken = routingContext.request().getParam("nextPageToken");
					if (nextPageToken == null) {
						JsonObject query = getJsonBody(routingContext);
						if (query == null) {
							HTTPUtil.sendQueryResults(routingContext.response(),
									JSONMessageFactory.get406NotAcceptableException(
											"[406NotAcceptable] no valid simple event query (json)"),
									406);
							return;
						}

						query.put("EQ_name", new JsonArray().add(resource));
						query.put("vocabularyName", new JsonArray().add("urn:epcglobal:epcis:vtype:EPCClass"));

						restQueryService.query(routingContext, query, "SimpleMasterDataQuery");
					} else {
						UUID uuid = null;
						try {
							uuid = UUID.fromString(routingContext.request().getParam("nextPageToken"));
							restQueryService.getNextVocabularyPage(routingContext, uuid);
						} catch (Exception e) {
							HTTPUtil.sendQueryResults(routingContext.response(),
									JSONMessageFactory.get406NotAcceptableException(
											"[406NotAcceptable] The server cannot return the response as requested: invalid nextPageToken - "
													+ uuid),
									406);
							return;
						}

					}

				});
		EPCISServer.logger.info("[GET /epcis/" + resourceType + "s/:" + resourceType
				+ "/vocabularies  (application/json)] - router added");
	}
	
	public static void registerGetVocabulariesWithBizLocationHandler(Router router, SOAPQueryService soapQueryService,
			RESTQueryService restQueryService) {

		String resourceType = "bizLocation";

		router.get("/epcis/" + resourceType + "s/:" + resourceType + "/vocabularies").consumes("application/xml")
				.handler(routingContext -> {

					if (!isHeaderPassed(routingContext))
						return;

					routingContext.response().setChunked(true);

					String resource = routingContext.pathParam(resourceType);

					if (!DynamicResource.availableBusinessLocationsInVocabularies.contains(resource)) {
						EPCISException e = new EPCISException(
								"[404NoSuchResourceException] There is no available query for: " + resource);
						EPCISServer.logger.error(e.getReason());
						HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(), 404);
						return;
					}

					String nextPageToken = routingContext.request().getParam("nextPageToken");
					if (nextPageToken == null) {
						String inputString = getHttpBody(routingContext, "SimpleMasterDataQuery");
						if (inputString == null)
							return;
						try {
							soapQueryService.pollEventsOrVocabularies(routingContext.request(),
									routingContext.response(), inputString, "EQ_name", resource, "vocabularyName",
									"urn:epcglobal:epcis:vtype:BusinessLocation");
						} catch (ValidationException e) {
							EPCISServer.logger.error(e.getReason());
							HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(),
									400);
						}
					} else {
						try {
							soapQueryService.getNextVocabularyPage(routingContext.request(), routingContext.response());
						} catch (ParserConfigurationException e) {
							ImplementationException e1 = new ImplementationException(
									ImplementationExceptionSeverity.ERROR, "Poll", e.getMessage());
							HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e1, e1.getClass(),
									500);
						}
					}
				});

		EPCISServer.logger.info("[GET /epcis/" + resourceType + "s/:" + resourceType
				+ "/vocabularies  (application/xml)] - router added");

		router.get("/epcis/" + resourceType + "s/:" + resourceType + "/vocabularies").consumes("application/json")
				.handler(routingContext -> {

					checkJSONPollHeaders(routingContext);
					if (routingContext.response().closed())
						return;

					routingContext.response().setChunked(true);

					String resource = routingContext.pathParam(resourceType);

					try {
						resource = TagDataTranslationEngine.toEPC(resource);
					} catch (ValidationException e) {

					}

					if (!DynamicResource.availableBusinessLocationsInVocabularies.contains(resource)) {
						HTTPUtil.sendQueryResults(routingContext.response(),
								JSONMessageFactory.get404NoSuchResourceException(
										"[404NoSuchResourceException] There is no available query for: " + resource),
								404);
						return;
					}

					String nextPageToken = routingContext.request().getParam("nextPageToken");
					if (nextPageToken == null) {
						JsonObject query = getJsonBody(routingContext);
						if (query == null) {
							HTTPUtil.sendQueryResults(routingContext.response(),
									JSONMessageFactory.get406NotAcceptableException(
											"[406NotAcceptable] no valid simple event query (json)"),
									406);
							return;
						}

						query.put("EQ_name", new JsonArray().add(resource));
						query.put("vocabularyName", new JsonArray().add("urn:epcglobal:epcis:vtype:BusinessLocation"));

						restQueryService.query(routingContext, query, "SimpleMasterDataQuery");
					} else {
						UUID uuid = null;
						try {
							uuid = UUID.fromString(routingContext.request().getParam("nextPageToken"));
							restQueryService.getNextVocabularyPage(routingContext, uuid);
						} catch (Exception e) {
							HTTPUtil.sendQueryResults(routingContext.response(),
									JSONMessageFactory.get406NotAcceptableException(
											"[406NotAcceptable] The server cannot return the response as requested: invalid nextPageToken - "
													+ uuid),
									406);
							return;
						}

					}

				});
		EPCISServer.logger.info("[GET /epcis/" + resourceType + "s/:" + resourceType
				+ "/vocabularies  (application/json)] - router added");
	}
	
	public static void registerGetVocabulariesWithReadPointHandler(Router router, SOAPQueryService soapQueryService,
			RESTQueryService restQueryService) {

		String resourceType = "readPoint";

		router.get("/epcis/" + resourceType + "s/:" + resourceType + "/vocabularies").consumes("application/xml")
				.handler(routingContext -> {

					if (!isHeaderPassed(routingContext))
						return;

					routingContext.response().setChunked(true);

					String resource = routingContext.pathParam(resourceType);

					if (!DynamicResource.availableReadPointsInVocabularies.contains(resource)) {
						EPCISException e = new EPCISException(
								"[404NoSuchResourceException] There is no available query for: " + resource);
						EPCISServer.logger.error(e.getReason());
						HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(), 404);
						return;
					}

					String nextPageToken = routingContext.request().getParam("nextPageToken");
					if (nextPageToken == null) {
						String inputString = getHttpBody(routingContext, "SimpleMasterDataQuery");
						if (inputString == null)
							return;
						try {
							soapQueryService.pollEventsOrVocabularies(routingContext.request(),
									routingContext.response(), inputString, "EQ_name", resource, "vocabularyName",
									"urn:epcglobal:epcis:vtype:ReadPoint");
						} catch (ValidationException e) {
							EPCISServer.logger.error(e.getReason());
							HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(),
									400);
						}
					} else {
						try {
							soapQueryService.getNextVocabularyPage(routingContext.request(), routingContext.response());
						} catch (ParserConfigurationException e) {
							ImplementationException e1 = new ImplementationException(
									ImplementationExceptionSeverity.ERROR, "Poll", e.getMessage());
							HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e1, e1.getClass(),
									500);
						}
					}
				});

		EPCISServer.logger.info("[GET /epcis/" + resourceType + "s/:" + resourceType
				+ "/vocabularies  (application/xml)] - router added");

		router.get("/epcis/" + resourceType + "s/:" + resourceType + "/vocabularies").consumes("application/json")
				.handler(routingContext -> {

					checkJSONPollHeaders(routingContext);
					if (routingContext.response().closed())
						return;

					routingContext.response().setChunked(true);

					String resource = routingContext.pathParam(resourceType);

					try {
						resource = TagDataTranslationEngine.toEPC(resource);
					} catch (ValidationException e) {

					}

					if (!DynamicResource.availableReadPointsInVocabularies.contains(resource)) {
						HTTPUtil.sendQueryResults(routingContext.response(),
								JSONMessageFactory.get404NoSuchResourceException(
										"[404NoSuchResourceException] There is no available query for: " + resource),
								404);
						return;
					}

					String nextPageToken = routingContext.request().getParam("nextPageToken");
					if (nextPageToken == null) {
						JsonObject query = getJsonBody(routingContext);
						if (query == null) {
							HTTPUtil.sendQueryResults(routingContext.response(),
									JSONMessageFactory.get406NotAcceptableException(
											"[406NotAcceptable] no valid simple event query (json)"),
									406);
							return;
						}

						query.put("EQ_name", new JsonArray().add(resource));
						query.put("vocabularyName", new JsonArray().add("urn:epcglobal:epcis:vtype:ReadPoint"));

						restQueryService.query(routingContext, query, "SimpleMasterDataQuery");
					} else {
						UUID uuid = null;
						try {
							uuid = UUID.fromString(routingContext.request().getParam("nextPageToken"));
							restQueryService.getNextVocabularyPage(routingContext, uuid);
						} catch (Exception e) {
							HTTPUtil.sendQueryResults(routingContext.response(),
									JSONMessageFactory.get406NotAcceptableException(
											"[406NotAcceptable] The server cannot return the response as requested: invalid nextPageToken - "
													+ uuid),
									406);
							return;
						}

					}

				});
		EPCISServer.logger.info("[GET /epcis/" + resourceType + "s/:" + resourceType
				+ "/vocabularies  (application/json)] - router added");
	}
	

	// ---------------------------------------------------------------------------------------
	public static ArrayList<String> getNamespaces(org.bson.Document event) {
		ArrayList<String> namespaces = new ArrayList<String>();

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

	public static String decodeMongoObjectKey(String key) {
		key = key.replace("\uff0e", ".");
		return key;
	}
}
