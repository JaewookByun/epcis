package org.oliot.epcis.capture.json;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.oliot.epcis.capture.common.Transaction;
import org.oliot.epcis.common.Metadata;
import org.oliot.epcis.converter.data.json_to_bson.EPCISDocumentConverter;
import org.oliot.epcis.model.EPCISException;
import org.oliot.epcis.model.ImplementationException;
import org.oliot.epcis.model.ImplementationExceptionSeverity;
import org.oliot.epcis.model.ValidationException;
import org.oliot.epcis.pagination.Page;
import org.oliot.epcis.pagination.PageExpiryTimerTask;
import org.oliot.epcis.server.EPCISServer;
import org.oliot.epcis.util.HTTPUtil;
import org.oliot.epcis.util.TimeUtil;

import com.mongodb.MongoException;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.WriteModel;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;

import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RequestBody;
import io.vertx.ext.web.RoutingContext;
import io.vertx.json.schema.OutputUnit;

public class JSONCaptureService {
	private EPCISDocumentConverter jsonCaptureConverter;

	public JSONCaptureService() {
		this.jsonCaptureConverter = new EPCISDocumentConverter();
	}

	public void postValidationResult(RoutingContext routingContext) {
		OutputUnit res = EPCISServer.jsonValidator.validate(routingContext.body().asJsonObject());
		if (res.getValid()) {
			routingContext.response().setStatusCode(200).end();
		} else {
			JsonArray arr = new JsonArray();
			for (OutputUnit o : res.getErrors()) {
				arr.add(o.toJson());
			}
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(400)
					.end(arr.toString());
		}
	}

	public String validateJSON(JsonObject data) {
		OutputUnit res = EPCISServer.jsonValidator.validate(data);
		if (res.getValid()) {
			return null;
		} else {
			JsonArray arr = new JsonArray();
			for (OutputUnit o : res.getErrors()) {
				arr.add(o.toJson());
			}
			return arr.toString();
		}
	}

	public JsonObject retrieveContext(JsonObject epcisDocument) {
		JsonObject context = new JsonObject();
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

	public void post(RoutingContext routingContext, EventBus eventBus) {

		RequestBody body = routingContext.body();
		if (body.isEmpty()) {
			EPCISServer.logger.error("[400ValidationException] Empty Request Body");
			HTTPUtil.sendQueryResults(routingContext.response(),
					JSONMessageFactory.get400ValidationException("Empty Request Body"), 400);
			return;
		}

		String inputString = body.asString();

		// payload check
		if (inputString.length() * 4 > Metadata.GS1_CAPTURE_file_size_limit) {
			EPCISServer.logger.error("[413CapturePayloadTooLarge] Payload is too large: " + (inputString.length() * 4)
					+ " > " + Metadata.GS1_CAPTURE_file_size_limit);
			HTTPUtil.sendQueryResults(routingContext.response(), JSONMessageFactory.exception413CapturePayloadTooLarge,
					413);
			return;
		}

		JsonObject epcisDocument = new JsonObject(inputString);

		// Validation
		String validationError = validateJSON(epcisDocument);
		if (validationError == null) {
			EPCISServer.logger.debug("An incoming EPCIS document is valid against the json schema 2.0.0");
		} else {
			HTTPUtil.sendQueryResults(routingContext.response(),
					JSONMessageFactory.get400ValidationException(validationError), 400);
			return;
		}

		// Retrieving context: naive
		JsonObject context = retrieveContext(epcisDocument);
		boolean eventExist = true;
		boolean vocExist = true;
		try {
			JsonArray eventList = epcisDocument.getJsonObject("epcisBody").getJsonArray("eventList");

			if (eventList.size() > Metadata.GS1_CAPTURE_limit) {
				EPCISServer.logger.error("[413CapturePayloadTooLarge] the number of events is too large: "
						+ eventList.size() + " > " + Metadata.GS1_CAPTURE_limit);
				HTTPUtil.sendQueryResults(routingContext.response(),
						JSONMessageFactory.exception413CapturePayloadTooLarge, 413);
				return;
			}

			if (eventList != null && eventList.size() != 0) {
				// ready to 202
				Transaction tx = new Transaction(Metadata.GS1_EPCIS_Capture_Error_Behaviour);
				routingContext.response().putHeader("Access-Control-Expose-Headers", "*")
						.putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
						.putHeader("GS1-CBV-Version", Metadata.GS1_CBV_Version)
						.putHeader("GS1-Extensions", Metadata.GS1_Extensions)
						.putHeader("Location", "/capture/" + tx.getTxId()).setStatusCode(202).end();
				eventBus.send("txStart", tx.getJson());

				captureEvents(routingContext, context, eventList, eventBus, tx);
			} else
				eventExist = false;
		} catch (NullPointerException e) {
			// No eventList
			eventExist = false;
		}
		if (eventExist == true)
			return;

		try {
			JsonArray vocabularyList = epcisDocument.getJsonObject("epcisHeader").getJsonObject("epcisMasterData")
					.getJsonArray("vocabularyList");

			if (vocabularyList.size() > Metadata.GS1_CAPTURE_limit) {
				EPCISServer.logger.error("[413CapturePayloadTooLarge] the number of events is too large: "
						+ vocabularyList.size() + " > " + Metadata.GS1_CAPTURE_limit);
				HTTPUtil.sendQueryResults(routingContext.response(),
						JSONMessageFactory.exception413CapturePayloadTooLarge, 413);
				return;
			}

			if (vocabularyList != null && vocabularyList.size() != 0) {
				captureMasterData(routingContext, context, vocabularyList);
			} else
				vocExist = false;
		} catch (NullPointerException e) {
			// No master-data
			vocExist = false;
		}
		// No event and master-data
		if (vocExist == false) {
			EPCISServer.logger.error("[400ValidationException] Capture fails: nothing to capture ");
			HTTPUtil.sendQueryResults(routingContext.response(),
					JSONMessageFactory.get400ValidationException("Capture fails: nothing to capture"), 400);
		}
	}

	private void captureEvents(RoutingContext routingContext, JsonObject context, JsonArray eventList,
			EventBus eventBus, Transaction tx) {
		List<WriteModel<Document>> bulk = null;
		try {
			bulk = eventList.stream().parallel().map(event -> {
				Document obj = null;
				try {
					obj = jsonCaptureConverter.convertEvent(context, (JsonObject) event, tx);
					obj.put("_tx", tx.getTxId());
				} catch (ValidationException e) {
					EPCISServer.logger.error(e.getReason());
					tx.setErrorType(ValidationException.class.getCanonicalName());
					tx.setErrorMessage(e.getReason());
					if (tx.isRollback()) {
						eventBus.send("txRollback", tx.getJson());

						throw new RuntimeException(e);
					} else {
						return null;
					}
				}
				// send to trigger event bus
				eventBus.send("trigger", obj);
				if (!obj.containsKey("errorDeclaration")) {
					return new InsertOneModel<Document>(obj);
				} else {
					Document filter = new Document("eventID", obj.getString("eventID"));
					return new ReplaceOneModel<Document>(filter, obj);
				}
			}).filter(Objects::nonNull).collect(Collectors.toList());
		} catch (RuntimeException re) {
			return;
		}
		EPCISServer.logger.debug("ready to capture events");

		try {
			BulkWriteResult result = EPCISServer.mEventCollection.bulkWrite(bulk);
			EPCISServer.logger.debug("event captured: " + result);
			if (tx.getErrorType() == null)
				eventBus.send("txSuccess", tx.getJson());
			else
				eventBus.send("txProceed", tx.getJson());
		} catch (MongoException e) {
			EPCISServer.logger.error(e.getMessage());
			tx.setErrorType(e.getCause().getClass().getCanonicalName());
			tx.setErrorMessage(e.getMessage());
			if (tx.isRollback())
				eventBus.send("txRollback", tx.getJson());
			else
				eventBus.send("txProceed", tx.getJson());
		}
	}

	private void captureMasterData(RoutingContext routingContext, JsonObject context, JsonArray vocabularyList) {
		List<WriteModel<Document>> bulk = new ArrayList<WriteModel<Document>>();

		for (int i = 0; i < vocabularyList.size(); i++) {
			JsonObject vocabulary = vocabularyList.getJsonObject(i);
			String type = vocabulary.getString("type");
			JsonArray vocabularyElementList = vocabulary.getJsonArray("vocabularyElementList");
			try {
				bulk.addAll(jsonCaptureConverter.convertVocabulary(context, type, vocabularyElementList));
			} catch (ValidationException e) {
				EPCISServer.logger.error(e.getReason());
				HTTPUtil.sendQueryResults(routingContext.response(),
						JSONMessageFactory.get400ValidationException(e.getReason()), 400);
				return;
			}
		}

		EPCISServer.logger.debug("ready to capture");

		try {
			BulkWriteResult result = EPCISServer.mVocCollection.bulkWrite(bulk);
			EPCISServer.logger.debug("vocabulary captured: " + result);
			routingContext.response().putHeader("Access-Control-Expose-Headers", "*")
					.putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-CBV-Version", Metadata.GS1_CBV_Version)
					.putHeader("GS1-Extensions", Metadata.GS1_Extensions).setStatusCode(201).end();
		} catch (MongoException e) {
			EPCISServer.logger.error(e.getMessage());
			HTTPUtil.sendQueryResults(routingContext.response(),
					JSONMessageFactory.get500ImplementationException(e.getMessage()), 500);
			return;
		}

	}

	public void postEvent(RoutingContext routingContext, EventBus eventBus) {

		RequestBody body = routingContext.body();
		if (body.isEmpty()) {
			EPCISServer.logger.error("[400ValidationException] Empty Request Body");
			HTTPUtil.sendQueryResults(routingContext.response(),
					JSONMessageFactory.get400ValidationException("Empty Request Body"), 400);
			return;
		}

		String inputString = body.asString();

		// payload check
		if (inputString.length() * 4 > Metadata.GS1_CAPTURE_file_size_limit) {
			EPCISServer.logger.error(
					"[413CapturePayloadTooLarge] The `POST` request is too large. It exceeds the limits set in `GS1-EPCIS-Capture-File-Size-Limit`.");
			HTTPUtil.sendQueryResults(routingContext.response(), JSONMessageFactory.exception413CapturePayloadTooLarge,
					413);
			return;
		}

		JsonObject epcisEvent;
		JsonObject context;
		try {
			epcisEvent = new JsonObject(inputString);
			context = retrieveContext(epcisEvent);
		} catch (Exception e) {
			EPCISServer.logger.error("[400ValidationException] " + e.getMessage());
			HTTPUtil.sendQueryResults(routingContext.response(),
					JSONMessageFactory.get400ValidationException(e.getMessage()), 400);
			return;
		}

		// Validation
		String validationError = validateJSON(epcisEvent);
		if (validationError == null) {
			EPCISServer.logger.debug(
					"[400ValidationException] An incoming EPCIS document is valid against the json schema 2.0.0");
		} else {
			HTTPUtil.sendQueryResults(routingContext.response(),
					JSONMessageFactory.get400ValidationException(validationError), 400);
			return;
		}
		captureEvent(routingContext, context, epcisEvent, eventBus);
	}

	private void captureEvent(RoutingContext routingContext, JsonObject jsonContext, JsonObject jsonEvent,
			EventBus eventBus) {
		Document obj = null;
		try {
			obj = jsonCaptureConverter.convertEvent(jsonContext, jsonEvent, null);
		} catch (ValidationException e) {
			EPCISServer.logger.error("[400ValidationException] " + e.getMessage());
			HTTPUtil.sendQueryResults(routingContext.response(),
					JSONMessageFactory.get400ValidationException(e.getReason()), 400);
			return;
		}
		EPCISServer.logger.debug("ready to capture");
		// send to trigger event bus
		eventBus.send("trigger", obj);
		if (!obj.containsKey("errorDeclaration")) {
			try {
				InsertOneResult result = EPCISServer.mEventCollection.insertOne(obj);
				EPCISServer.logger.debug("event captured: " + result);
				routingContext.response().putHeader("Access-Control-Expose-Headers", "*")
						.putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
						.putHeader("GS1-CBV-Version", Metadata.GS1_CBV_Version)
						.putHeader("GS1-Extension", Metadata.GS1_Extensions)
						.putHeader("Location", "/events/" + URLEncoder.encode(obj.getString("eventID"), "UTF-8"))
						.setStatusCode(201).end();
			} catch (MongoException | UnsupportedEncodingException e) {
				EPCISServer.logger.error(e.getMessage());
				HTTPUtil.sendQueryResults(routingContext.response(),
						JSONMessageFactory.get500ImplementationException(e.getMessage()), 500);
				return;
			}
		} else {
			Document filter = new Document("eventID", obj.getString("eventID"));

			try {
				UpdateResult result = EPCISServer.mEventCollection.replaceOne(filter, obj);
				EPCISServer.logger.debug("event captured: " + result);
				routingContext.response().putHeader("Access-Control-Expose-Headers", "*")
						.putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
						.putHeader("GS1-CBV-Version", Metadata.GS1_CBV_Version)
						.putHeader("GS1-Extension", Metadata.GS1_Extensions)
						.putHeader("Location", "/events/" + URLEncoder.encode(obj.getString("eventID"), "UTF-8"))
						.setStatusCode(201).end();
			} catch (Throwable e) {
				EPCISServer.logger.error(e.getMessage());
				HTTPUtil.sendQueryResults(routingContext.response(),
						JSONMessageFactory.get500ImplementationException(e.getMessage()), 500);
				return;
			}
		}
	}

	public void postCaptureJobList(RoutingContext routingContext) {
		String perPageParam = routingContext.request().getParam("PerPage");
		int perPage = 30;
		if (perPageParam != null) {
			try {
				int t = Integer.parseInt(perPageParam);
				if (t > 0)
					perPage = t;
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}

		Document sort = new Document().append("createdAt", -1);
		List<Document> jobs = new ArrayList<Document>();
		try {
			EPCISServer.mTxCollection.find().sort(sort).limit(perPage + 1).into(jobs);
		} catch (MongoException e) {
			ImplementationException e1 = new ImplementationException(ImplementationExceptionSeverity.ERROR, null, null,
					e.getMessage());
			HTTPUtil.sendQueryResults(routingContext.response(),
					JSONMessageFactory.get500ImplementationException(e1.getMessage()), 500);
			return;
		}

		// there are remaining list
		int jobSize = jobs.size();
		if (perPage < jobs.size()) {
			jobSize = perPage;
		}
		JsonArray result = new JsonArray();
		for (int i = 0; i < jobSize; i++) {
			result.add(Transaction.toJson(jobs.get(i)));
		}
		// There are remaining lists
		if (perPage < jobs.size()) {
			UUID uuid;
			long currentTime = System.currentTimeMillis();

			while (true) {
				uuid = UUID.randomUUID();
				if (!EPCISServer.captureIDPageMap.containsKey(uuid))
					break;
			}
			Page page = new Page(uuid, "captureJob", null, null, sort, Integer.MAX_VALUE, perPage);
			Timer timer = new Timer();
			page.setTimer(timer);
			timer.schedule(
					new PageExpiryTimerTask("GET /capture", EPCISServer.captureIDPageMap, uuid, EPCISServer.logger),
					Metadata.GS1_Next_Page_Token_Expires);
			EPCISServer.captureIDPageMap.put(uuid, page);
			EPCISServer.logger.debug("[GET /capture] page - " + uuid + " added. # remaining pages - "
					+ EPCISServer.captureIDPageMap.size());

			routingContext.response().putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-Extension", Metadata.GS1_Extensions)
					.putHeader("Link",
							"http://" + EPCISServer.host + ":" + EPCISServer.port + "/epcis/capture?PerPage=" + perPage
									+ "&NextPageToken=" + uuid.toString())
					.putHeader("GS1-Next-Page-Token-Expires",
							TimeUtil.getDateTimeStamp(currentTime + Metadata.GS1_Next_Page_Token_Expires))
					.putHeader("content-type", "application/json").putHeader("Access-Control-Expose-Headers", "*")
					.setStatusCode(200).end(result.toString());
		} else {
			routingContext.response().putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-Extension", Metadata.GS1_Extensions).putHeader("content-type", "application/json")
					.putHeader("Access-Control-Expose-Headers", "*").setStatusCode(200).end(result.toString());
		}
	}

	public void postRemainingCaptureJobList(RoutingContext routingContext, String nextPagetoken) {
		String perPageParam = routingContext.request().getParam("PerPage");
		int perPage = 30;
		if (perPageParam != null) {
			try {
				int t = Integer.parseInt(perPageParam);
				if (t > 0)
					perPage = t;
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		Page page = null;
		UUID uuid = UUID.fromString(nextPagetoken);

		if (!EPCISServer.captureIDPageMap.containsKey(uuid)) {
			EPCISException e = new EPCISException(
					"[406NotAcceptable] The given next page token does not exist or be no longer available.");
			HTTPUtil.sendQueryResults(routingContext.response(),
					JSONMessageFactory.get406NotAcceptableException(e.getReason()), 500);
			return;
		} else {
			page = EPCISServer.captureIDPageMap.get(uuid);
		}

		Document sort = (Document) page.getSort();
		int skip = page.getSkip();
		List<Document> jobs = new ArrayList<Document>();
		try {
			EPCISServer.mTxCollection.find().sort(sort).skip(skip).limit(perPage + 1).into(jobs);
		} catch (MongoException e) {
			ImplementationException e1 = new ImplementationException(ImplementationExceptionSeverity.ERROR, null, null,
					e.getMessage());
			HTTPUtil.sendQueryResults(routingContext.response(),
					JSONMessageFactory.get500ImplementationException(e1.getReason()), 500);
			return;
		}

		// there are remaining list
		int jobSize = jobs.size();
		if (perPage < jobs.size()) {
			jobSize = perPage;
		}
		JsonArray result = new JsonArray();
		for (int i = 0; i < jobSize; i++) {
			result.add(Transaction.toJson(jobs.get(i)));
		}
		if (perPage < jobs.size()) {
			page.incrSkip(perPage);
			long currentTime = System.currentTimeMillis();
			Timer timer = page.getTimer();
			if (timer != null)
				timer.cancel();
			Timer newTimer = new Timer();
			page.setTimer(newTimer);
			newTimer.schedule(
					new PageExpiryTimerTask("GET /capture", EPCISServer.captureIDPageMap, uuid, EPCISServer.logger),
					Metadata.GS1_Next_Page_Token_Expires);
			EPCISServer.logger.debug("[GET /capture] page - " + uuid + " token expiry time extended to "
					+ TimeUtil.getDateTimeStamp(currentTime + Metadata.GS1_Next_Page_Token_Expires));
			routingContext.response().putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-Extension", Metadata.GS1_Extensions)
					.putHeader("Link",
							"http://" + EPCISServer.host + ":" + EPCISServer.port + "/epcis/capture?PerPage=" + perPage
									+ "&NextPageToken=" + uuid.toString())
					.putHeader("GS1-Next-Page-Token-Expires",
							TimeUtil.getDateTimeStamp(currentTime + Metadata.GS1_Next_Page_Token_Expires))
					.putHeader("content-type", "application/json").putHeader("Access-Control-Expose-Headers", "*")
					.setStatusCode(200).end(result.toString());
		} else {
			EPCISServer.captureIDPageMap.remove(uuid);
			EPCISServer.logger.debug("[GET /capture] page - " + uuid + " expired. # remaining pages - "
					+ EPCISServer.captureIDPageMap.size());
			routingContext.response().putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-Extension", Metadata.GS1_Extensions).putHeader("Access-Control-Expose-Headers", "*")
					.putHeader("content-type", "application/json").setStatusCode(200).end(result.toString());
		}
	}

	/**
	 * Returns information about the capture job. When EPCIS events are added
	 * through the capture interface, the capture process can run asynchronously. If
	 * the payload is syntactically correct and the client is allowed to call
	 * `/capture`, the server returns a `202` HTTP response code. This endpoint
	 * exposes the state of the capture job to the client.
	 * 
	 * @param routingContext
	 * @param captureID
	 */
	public void postCaptureJob(RoutingContext routingContext, String captureID) {
		List<Document> jobs = new ArrayList<Document>();
		try {
			EPCISServer.mTxCollection.find(new Document("_id", new ObjectId(captureID))).into(jobs);
			if (jobs.isEmpty()) {
				HTTPUtil.sendQueryResults(routingContext.response(), JSONMessageFactory
						.get404NoSuchResourceException("There is no capture job with id: " + captureID), 404);
				return;
			}
		} catch (IllegalArgumentException e) {
			HTTPUtil.sendQueryResults(routingContext.response(), JSONMessageFactory
					.get404NoSuchResourceException("Illegal capture job identifier: " + e.getMessage()), 404);
			return;
		} catch (Throwable throwable) {
			HTTPUtil.sendQueryResults(routingContext.response(),
					JSONMessageFactory.get500ImplementationException(throwable.getMessage()), 500);
			return;
		}

		try {
			JsonObject captureJob = Transaction.toJson(jobs.get(0));
			routingContext.response().putHeader("Access-Control-Expose-Headers", "*")
					.putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-Extension", Metadata.GS1_Extensions)
					.putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
					.end(captureJob.toString());
		} catch (Exception throwable) {
			HTTPUtil.sendQueryResults(routingContext.response(),
					JSONMessageFactory.get500ImplementationException(throwable.getMessage()), 500);
		}

	}
}
