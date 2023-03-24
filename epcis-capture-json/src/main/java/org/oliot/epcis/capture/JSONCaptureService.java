package org.oliot.epcis.capture;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.RoutingContext;

import org.oliot.epcis.converter.json.write.JSONWriteConverter;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.BulkOperation;
import org.oliot.epcis.transaction.Transaction;

/**
 * Copyright (C) 2020-2022. (Jaewook Byun) all rights reserved.
 * <p>
 * Oliot EPCIS X is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * Among various modules, epcis-capture-json acts as a server to receive
 * JSON-formatted EPCIS documents to capture events in the documents into an
 * EPCIS repository.
 * <p>
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr
 *         <p>
 *         Associate Director, Auto-ID Labs, KAIST, bjw0829@kaist.ac.kr
 */
public class JSONCaptureService {

	public void postValidationResult(RoutingContext routingContext) {
		JsonObject docForValidation = new JsonObject(routingContext.body().asString());
		JSONCaptureServer.jsonValidator.validateAsync(docForValidation).onSuccess(h -> {
			routingContext.response().end();
		}).onFailure(h -> {
			routingContext.response().setStatusCode(400).end(h.getMessage());
		});
	}

	public static void validateJSON(JsonObject data) throws RuntimeException {
		JSONCaptureServer.jsonValidator.validateAsync(data).onSuccess(h -> {
			JSONCaptureServer.logger.debug("validated");
		}).onFailure(h -> {
			JSONCaptureServer.logger.debug(h.getMessage());
			throw new RuntimeException(h);
		});
	}

	public JsonObject retrieveContext(JsonObject epcisDocument) {
		JsonObject context = null;
		Object contextObj = epcisDocument.getValue("@context");
		if (contextObj instanceof JsonObject) {
			context = (JsonObject) contextObj;
		} else if (contextObj instanceof JsonArray) {
			JsonArray contextArr = (JsonArray) contextObj;
			context = new JsonObject();
			for (Object contextElemObj : contextArr) {
				if (contextElemObj instanceof JsonObject) {
					JsonObject contextElem = (JsonObject) contextElemObj;
					context.mergeIn(contextElem, true);
				}
			}
		}
		return context;
	}

	public void post(RoutingContext routingContext, EventBus eventBus, Transaction tx) throws RuntimeException {
		String body = routingContext.body().asString();
		JsonObject epcisDocument = new JsonObject(body);
		// Validation
		if (!JSONCaptureServer.isSkipValidation) {
			try {
				validateJSON(epcisDocument);
			} catch (RuntimeException e) {
				tx.setErrorType(e.getCause().getClass().getCanonicalName());
				tx.setErrorMessage(e.getCause().getMessage());
				if (tx.isRollback())
					eventBus.send("txRollback", tx.getJson());
				else
					eventBus.send("txProceed", tx.getJson());
				routingContext.response().setStatusCode(400).end(tx.getTxId());
				throw e;
			}
		}
		// file-size-limit
		if (JSONCaptureServer.GS1_CAPTURE_file_size_limit < body.getBytes().length) {
			JSONCaptureServer.logger.error("Payload Too Large: " + body.getBytes().length + " > "
					+ JSONCaptureServer.GS1_CAPTURE_file_size_limit);
			routingContext.response().setStatusCode(413).end();
			return;
		}

		// Retrieving context: naive
		JsonObject context = retrieveContext(epcisDocument);
		boolean eventExist = true;
		boolean vocExist = true;
		try {
			JsonArray eventList = epcisDocument.getJsonObject("epcisBody").getJsonArray("eventList");
			if (eventList != null && !eventList.isEmpty())
				captureEvents(routingContext, context, eventList, eventBus, tx);
			else
				eventExist = false;
		} catch (NullPointerException e) {
			eventExist = false;
		}
		try {
			JsonArray vocabularyList = epcisDocument.getJsonObject("epcisHeader").getJsonObject("epcisMasterData")
					.getJsonArray("vocabularyList");
			if (vocabularyList != null && !vocabularyList.isEmpty())
				captureMasterData(routingContext, context, vocabularyList);
			else
				vocExist = false;
		} catch (NullPointerException e) {
			vocExist = false;
		}
		// No event and master-data
		if (eventExist == false && vocExist == false) {
			tx.setErrorType("No Data");
			tx.setErrorMessage("No event or vocabulary found");
			eventBus.send("txProceed", tx.getJson());
			routingContext.response().setStatusCode(400).end(tx.getTxId());
		}
	}

	private void captureEvents(RoutingContext routingContext, JsonObject context, JsonArray eventList,
			EventBus eventBus, Transaction tx) {
		List<BulkOperation> bulk = eventList.stream().parallel().map(event -> {
			JsonObject obj = JSONWriteConverter.convertEvent(context, (JsonObject) event, tx);
			if (!obj.containsKey("errorDeclaration")) {
				return BulkOperation.createInsert(obj);
			} else {
				JsonObject filter = new JsonObject(obj.toString());
				// make filter
				filter.remove("errorDeclaration");
				filter.remove("errf");
				filter.put("errorDeclaration", new JsonObject().put("$exists", false));
				filter.remove("recordTime");
				return BulkOperation.createReplace(filter, obj);
			}
		}).filter(Objects::nonNull).collect(Collectors.toList());

		JSONCaptureServer.mClient.bulkWrite("EventData", bulk, res -> {
			if (res.succeeded()) {
				JSONCaptureServer.logger.debug("event captured: " + res.result().toJson());
				routingContext.response().end(tx.getTxId());
				eventBus.send("txSuccess", tx.getJson());
			} else {
				tx.setErrorType(res.cause().getClass().getCanonicalName());
				tx.setErrorMessage(res.cause().getMessage());
				if (tx.isRollback())
					eventBus.send("txRollback", tx.getJson());
				else
					eventBus.send("txProceed", tx.getJson());
				throw new RuntimeException(res.cause().getMessage());
			}
		});
	}

	private void captureMasterData(RoutingContext routingContext, JsonObject context, JsonArray vocabularyList) {
		List<BulkOperation> bulk = vocabularyList.stream().parallel().flatMap(v -> {
			JsonObject vocabulary = (JsonObject) v;
			String type = vocabulary.getString("type");
			JsonArray vocabularyElementList = vocabulary.getJsonArray("vocabularyElementList");
			return JSONWriteConverter.convertVocabulary(context, type, vocabularyElementList);
		}).filter(Objects::nonNull).collect(Collectors.toList());

		JSONCaptureServer.mClient.bulkWrite("MasterData", bulk, res -> {
			if (res.succeeded()) {
				JsonObject r = res.result().toJson();
				if (r == null) {
					JSONCaptureServer.logger.debug("vocabulary captured (new)");
				} else {
					JSONCaptureServer.logger.debug("vocabulary captured: " + r);
				}
				routingContext.response().end();
			} else {
				throw new RuntimeException(res.cause().getMessage());
			}
		});
	}

	public void postCaptureJob(RoutingContext routingContext, String captureID) {

		JSONCaptureServer.mClient.findOne("Tx", new JsonObject().put("_id", captureID), new JsonObject(), h -> {
			JsonObject result = h.result();
			if (result == null || result.isEmpty()) {
				routingContext.response().setStatusCode(404).end();
			} else {
				try {
					JsonObject captureJobReport = Transaction.toCaptureJobReport(result);
					routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
							.setStatusCode(200).end(captureJobReport.toString());
				} catch (Exception e) {
					routingContext.response().setStatusCode(404).end();
				}
			}
		});

	}

	public void postCaptureJob(RoutingContext routingContext) {

		JSONCaptureServer.mClient.find("Tx", new JsonObject(), h -> {
			List<JsonObject> list = h.result();
			if (list == null || list.isEmpty()) {
				routingContext.response().setStatusCode(404).end();
				return;
			}
			JsonArray array = new JsonArray();
			for (JsonObject obj : list) {
				array.add(obj.getString("_id"));
			}
			try {
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
						.setStatusCode(200).end(array.toString());
			} catch (Exception e) {
				routingContext.response().setStatusCode(404).end();
			}

		});
	}

}
