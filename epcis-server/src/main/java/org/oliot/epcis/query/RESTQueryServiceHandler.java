package org.oliot.epcis.query;

import static org.oliot.epcis.validation.HeaderValidator.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bson.Document;
import org.oliot.epcis.capture.json.JSONMessageFactory;
import org.oliot.epcis.common.Metadata;
import org.oliot.epcis.model.EPCISException;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.server.EPCISServer;
import org.oliot.epcis.util.HTTPUtil;
import org.oliot.epcis.util.SOAPMessage;
import org.oliot.epcis.util.TimeUtil;

import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

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

	/*
	 * 
	 * eventType GE_eventTime LT_eventTime GE_recordTime LT_recordTime EQ_action
	 * EQ_bizStep EQ_disposition EQ_persistentDisposition_set
	 * EQ_persistentDisposition_unset EQ_readPoint WD_readPoint EQ_bizLocation
	 * WD_bizLocation EQ_transformationID MATCH_epc MATCH_parentID MATCH_inputEPC
	 * MATCH_outputEPC MATCH_anyEPC MATCH_epcClass MATCH_inputEPCClass
	 * MATCH_outputEPCClass MATCH_anyEPCClass EQ_quantity GT_quantity GE_quantity
	 * LT_quantity LE_quantity EQ_eventID EXISTS_errorDeclaration
	 * GE_errorDeclarationTime LT_errorDeclarationTime EQ_errorReason
	 * EQ_correctiveEventID orderBy orderDirection eventCountLimit maxEventCount
	 * GE_startTime LT_startTime GE_endTime LT_endTime EQ_type EQ_deviceID
	 * EQ_dataProcessingMethod EQ_microorganism EQ_chemicalSubstance EQ_bizRules
	 * EQ_stringValue EQ_hexBinaryValue EQ_uriValue EQ_booleanValue
	 */

	public static void registerGetEventsHandler(Router router, RESTQueryService restQueryService) {
		/*
		 * NextPageToken PerPage GS1-CBV-Min GS1-CBV-Max GS1-EPCIS-Min GS1-EPCIS-Max
		 * GS1-EPC-Format GS1-CBV-XML-Format
		 */
		router.get("/epcis/events").consumes("application/json").handler(routingContext -> {
			if (!checkEPCISMinMaxVersion(routingContext))
				return;
			if (!isEqualHeaderREST(routingContext, "GS1-EPC-Format"))
				return;
			if (!isEqualHeaderREST(routingContext, "GS1-CBV-XML-Format"))
				return;
			routingContext.response().setChunked(true);

			JsonObject query = null;

			MultiMap m = routingContext.request().params();
			Iterator<Entry<String, String>> iter = m.iterator();
			while (iter.hasNext()) {
				try {
					String k = iter.next().getKey();
					query = new JsonObject(k);
					break;
				} catch (Exception e) {
					// DO NOTHING
				}
			}

			if (query == null) {
				query = routingContext.body().asJsonObject();
			}

			// get UUID
			String nextPageToken = routingContext.request().getParam("NextPageToken");
			if (nextPageToken == null) {
				restQueryService.query(routingContext, query);
			} else {
				UUID uuid = null;
				try {
					uuid = UUID.fromString(routingContext.request().getParam("NextPageToken"));
				} catch (Exception e) {
					QueryParameterException e1 = new QueryParameterException("invalid nextPageToken - " + uuid);
					HTTPUtil.sendQueryResults(routingContext.response(),
							JSONMessageFactory.get406NotAcceptableException(
									"[406NotAcceptable] The server cannot return the response as requested: "
											+ e1.getMessage()),
							406);
					return;
				}
				restQueryService.getNextEventPage(routingContext, uuid);
			}
		});
		EPCISServer.logger.info("[GET /epcis/evetns (application/json)] - router added");
	}

	// Returns an individual EPCIS event.
	public static void registerGetEventHandler(Router router, RESTQueryService restQueryService) {
		router.get("/epcis/events/:eventID").consumes("application/json").handler(routingContext -> {
			if (!checkEPCISMinMaxVersion(routingContext))
				return;
			if (!isEqualHeaderREST(routingContext, "GS1-EPC-Format"))
				return;
			if (!isEqualHeaderREST(routingContext, "GS1-CBV-XML-Format"))
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
				serverResponse.putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version).putHeader("GS1-Extension",
						Metadata.GS1_Extensions);

				HTTPUtil.sendQueryResults(serverResponse, convertedResult, 200);

			} catch (Throwable throwable) {
				HTTPUtil.sendQueryResults(routingContext.response(),
						JSONMessageFactory.get500ImplementationException(throwable.getMessage()), 500);
				return;
			}
		});
	}

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
