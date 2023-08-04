package org.oliot.epcis.capture.json;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilderFactory;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.oliot.epcis.capture.common.Transaction;
import org.oliot.epcis.common.Metadata;
import org.oliot.epcis.converter.data.json_to_bson.EPCISDocumentConverter;
import org.oliot.epcis.converter.data.pojo_to_bson.AggregationEventConverter;
import org.oliot.epcis.converter.data.pojo_to_bson.AssociationEventConverter;
import org.oliot.epcis.converter.data.pojo_to_bson.ObjectEventConverter;
import org.oliot.epcis.converter.data.pojo_to_bson.TransactionEventConverter;
import org.oliot.epcis.converter.data.pojo_to_bson.TransformationEventConverter;
import org.oliot.epcis.model.AggregationEventType;
import org.oliot.epcis.model.AssociationEventType;
import org.oliot.epcis.model.EPCISCaptureJobType;
import org.oliot.epcis.model.EPCISException;
import org.oliot.epcis.model.ImplementationException;
import org.oliot.epcis.model.ImplementationExceptionSeverity;
import org.oliot.epcis.model.ObjectEventType;
import org.oliot.epcis.model.TransactionEventType;
import org.oliot.epcis.model.TransformationEventType;
import org.oliot.epcis.model.ValidationException;
import org.oliot.epcis.model.cbv.EPCISEventType;
import org.oliot.epcis.pagination.Page;
import org.oliot.epcis.pagination.PageExpiryTimerTask;
import org.oliot.epcis.server.EPCISServer;
import org.oliot.epcis.util.FileUtil;
import org.oliot.epcis.util.HTTPUtil;
import org.oliot.epcis.util.SOAPMessage;
import org.oliot.epcis.util.TimeUtil;
import org.oliot.epcis.util.XMLUtil;

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
import io.vertx.ext.web.RoutingContext;
import io.vertx.json.schema.OutputUnit;

public class JSONCaptureService {

	public void postValidationResult(RoutingContext routingContext) {
		String validationError = validateJSON(routingContext.body().asJsonObject());
		if (validationError == null) {
			routingContext.response().setStatusCode(200).end();
		} else {
			HTTPUtil.sendQueryResults(routingContext.response(),
					JSONMessageFactory.get400ValidationException(validationError), 400);
		}
	}

	public String validateJSON(JsonObject data) {
		OutputUnit res = EPCISServer.jsonValidator.validate(data);
		if (res.getValid()) {
			return null;
		} else {
			return res.getError();
		}
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

	public void post(RoutingContext routingContext, EventBus eventBus) {
		String inputString = routingContext.body().asString();
		// SOAPMessage message = new SOAPMessage();
		// payload check
		if (inputString.length() * 4 > Metadata.GS1_CAPTURE_file_size_limit) {
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
				HTTPUtil.sendQueryResults(routingContext.response(),
						JSONMessageFactory.exception413CapturePayloadTooLarge, 413);
				return;
			}

			if (eventList != null && eventList.size() != 0) {
				// ready to 202
				Transaction tx = new Transaction(Metadata.GS1_EPCIS_Capture_Error_Behaviour);
				routingContext.response().putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
						.putHeader("GS1-CBV-Version", Metadata.GS1_CBV_Version)
						.putHeader("GS1-Extensions", Metadata.GS1_Extensions)
						.putHeader("Access-Control-Expose-Headers", "Location").putHeader("Location", "http://"
								+ EPCISServer.host + ":" + EPCISServer.port + "/epcis/capture/" + tx.getTxId())
						.setStatusCode(202).end();
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
					obj = EPCISDocumentConverter.convertEvent(context, (JsonObject) event, tx);
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
		List<WriteModel<Document>> bulk = null;
		try {
			bulk = vocabularyList.stream().parallel().flatMap(v -> {
				JsonObject vocabulary = (JsonObject) v;
				String type = vocabulary.getString("type");
				JsonArray vocabularyElementList = vocabulary.getJsonArray("vocabularyElementList");
				return EPCISDocumentConverter.convertVocabulary(context, type, vocabularyElementList);
			}).filter(Objects::nonNull).collect(Collectors.toList());
		} catch (RuntimeException e) {
			ValidationException ve = (ValidationException) e.getCause();
			HTTPUtil.sendQueryResults(routingContext.response(),
					JSONMessageFactory.get400ValidationException(ve.getReason()), 400);
			return;
		}

		EPCISServer.logger.debug("ready to capture");

		try {
			BulkWriteResult result = EPCISServer.mVocCollection.bulkWrite(bulk);
			EPCISServer.logger.debug("vocabulary captured: " + result);
			routingContext.response().putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-CBV-Version", Metadata.GS1_CBV_Version)
					.putHeader("GS1-Extensions", Metadata.GS1_Extensions).setStatusCode(201).end();
		} catch (MongoException e) {
			ImplementationException ie = new ImplementationException(ImplementationExceptionSeverity.ERROR, null, null,
					e.getMessage());
			HTTPUtil.sendQueryResults(routingContext.response(),
					JSONMessageFactory.get500ImplementationException(ie.getReason()), 400);
			return;
		}

	}

	// TODO:
	// -----------------------------------------------------------------------------------

	public void postEvent(RoutingContext routingContext, EventBus eventBus) {
		String inputString = routingContext.body().asString();
		SOAPMessage message = new SOAPMessage();
		// payload check
		if (inputString.length() * 4 > Metadata.GS1_CAPTURE_file_size_limit) {
			EPCISException e = new EPCISException(
					"[413CapturePayloadTooLarge] The `POST` request is too large. It exceeds the limits set in `GS1-EPCIS-Capture-File-Size-Limit`.\n");
			HTTPUtil.sendQueryResults(routingContext.response(), message, e, e.getClass(), 413);
			return;
		}

		byte[] xmlByteArray = FileUtil.getByteArray(inputString);

		// Validation
//		try {
//			// validateXML(XMLUtil.getXMLDocumentInputStream(xmlByteArray));
//		} catch (ValidationException e) {
//			EPCISServer.logger.error(e.getReason());
//			HTTPUtil.sendQueryResults(routingContext.response(), message, e, e.getClass(), 400);
//			return;
//		}

		// Get Event Type
		String type = XMLUtil.getCaptureInputType(XMLUtil.getXMLDocumentInputStream(xmlByteArray));
		InputStream epcisStream = XMLUtil.getXMLDocumentInputStream(xmlByteArray);
		if (type == null) {
			ValidationException e = new ValidationException(
					"Input should be one of " + Stream.of(EPCISEventType.values()).toList());
			EPCISServer.logger.error(e.getReason());
			HTTPUtil.sendQueryResults(routingContext.response(), message, e, e.getClass(), 400);
			return;
		}
		if (type.equals("AggregationEvent")) {
			AggregationEventType event = JAXB.unmarshal(epcisStream, AggregationEventType.class);
			EPCISServer.logger.debug("unmarshal - aggregation event");
			captureEvent(routingContext, event, eventBus);
		} else if (type.equals("AssociationEvent")) {
			AssociationEventType event = JAXB.unmarshal(epcisStream, AssociationEventType.class);
			EPCISServer.logger.debug("unmarshal - association event");
			captureEvent(routingContext, event, eventBus);
		} else if (type.equals("ObjectEvent")) {
			ObjectEventType event = JAXB.unmarshal(epcisStream, ObjectEventType.class);
			EPCISServer.logger.debug("unmarshal - object event");
			captureEvent(routingContext, event, eventBus);
		} else if (type.equals("TransactionEvent")) {
			TransactionEventType event = JAXB.unmarshal(epcisStream, TransactionEventType.class);
			EPCISServer.logger.debug("unmarshal - transaction event");
			captureEvent(routingContext, event, eventBus);
		} else if (type.equals("TransformationEvent")) {
			TransformationEventType event = JAXB.unmarshal(epcisStream, TransformationEventType.class);
			EPCISServer.logger.debug("unmarshal - transformation event");
			captureEvent(routingContext, event, eventBus);
		}

	}

	private void captureEvent(RoutingContext routingContext, Object jaxbEvent, EventBus eventBus) {
		Document obj = null;
		SOAPMessage message = new SOAPMessage();
		try {
			obj = prepareEvent(jaxbEvent);
		} catch (ValidationException e) {
			EPCISServer.logger.error(e.getReason());
			HTTPUtil.sendQueryResults(routingContext.response(), message, e, e.getClass(), 400);
			return;
		}
		EPCISServer.logger.debug("ready to capture");
		// send to trigger event bus
		eventBus.send("trigger", obj);
		if (!obj.containsKey("errorDeclaration")) {
			try {

				InsertOneResult result = EPCISServer.mEventCollection.insertOne(obj);
				EPCISServer.logger.debug("event captured: " + result);
				routingContext.response().putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
						.putHeader("GS1-CBV-Version", Metadata.GS1_CBV_Version)
						.putHeader("GS1-Extension", Metadata.GS1_Extensions)
						.putHeader("Access-Control-Expose-Headers", "Location")
						.putHeader("Location", "/events/" + URLEncoder.encode(obj.getString("eventID"), "UTF-8"))
						.setStatusCode(201).end();
			} catch (MongoException | UnsupportedEncodingException e) {
				EPCISServer.logger.error(e.getMessage());
				ImplementationException e1 = new ImplementationException(ImplementationExceptionSeverity.ERROR, null,
						null, e.getMessage());
				HTTPUtil.sendQueryResults(routingContext.response(), message, e1, e1.getClass(), 500);
				return;
			}
		} else {
			Document filter = new Document("eventID", obj.getString("eventID"));

			try {
				UpdateResult result = EPCISServer.mEventCollection.replaceOne(filter, obj);
				EPCISServer.logger.debug("event captured: " + result);
				routingContext.response().putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
						.putHeader("GS1-CBV-Version", Metadata.GS1_CBV_Version)
						.putHeader("GS1-Extension", Metadata.GS1_Extensions)
						.putHeader("Access-Control-Expose-Headers", "Location")
						.putHeader("Location",
								"http://" + EPCISServer.host + ":8084/epcis/events/"
										+ URLEncoder.encode(obj.getString("eventID"), "UTF-8"))
						.setStatusCode(201).end();
			} catch (Throwable e) {
				EPCISServer.logger.error(e.getMessage());
				ImplementationException e1 = new ImplementationException(ImplementationExceptionSeverity.ERROR, null,
						null, e.getMessage());
				HTTPUtil.sendQueryResults(routingContext.response(), message, e1, e1.getClass(), 500);
				return;
			}
		}
	}

	private Document prepareEvent(Object event) throws ValidationException {

		Document object2Save = null;
		if (event instanceof AggregationEventType) {
			object2Save = AggregationEventConverter.toBson((AggregationEventType) event);
		} else if (event instanceof ObjectEventType) {
			object2Save = ObjectEventConverter.toBson((ObjectEventType) event);
		} else if (event instanceof TransactionEventType) {
			object2Save = TransactionEventConverter.toBson((TransactionEventType) event);
		} else if (event instanceof TransformationEventType) {
			object2Save = TransformationEventConverter.toBson((TransformationEventType) event);
		} else if (event instanceof AssociationEventType) {
			object2Save = AssociationEventConverter.toBson((AssociationEventType) event);
		}

		return object2Save;
	}

	public void postCaptureJobList(RoutingContext routingContext) {
		SOAPMessage message = new SOAPMessage();
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
			HTTPUtil.sendQueryResults(routingContext.response(), message, e1, e1.getClass(), 500);
			return;
		}
		String result = "<epcisCaptureJobList>";
		// there are remaining list
		int jobSize = jobs.size();
		if (perPage < jobs.size()) {
			jobSize = perPage;
		}
		for (int j = 0; j < jobSize; j++) {
			try {
				EPCISCaptureJobType captureJob = Transaction.toCaptureJob(jobs.get(j));
				org.w3c.dom.Document retDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
				JAXBContext jc = JAXBContext.newInstance(EPCISCaptureJobType.class);
				Marshaller marshaller = jc.createMarshaller();
				marshaller.marshal(captureJob, retDoc);
				String resultString = XMLUtil.toString(retDoc, true);
				String[] lines = resultString.split("\n");
				for (int i = 1; i < lines.length; i++) {
					result += lines[i] + "\n";
				}
			} catch (Exception throwable) {
				ImplementationException e = new ImplementationException(ImplementationExceptionSeverity.ERROR, null,
						null, throwable.getMessage());
				HTTPUtil.sendQueryResults(routingContext.response(), message, e, e.getClass(), 500);
				return;
			}
		}
		result += "</epcisCaptureJobList>";

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
					.putHeader("content-type", "application/xml").setStatusCode(200).end(result);
		} else {
			routingContext.response().putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-Extension", Metadata.GS1_Extensions).putHeader("content-type", "application/xml")
					.setStatusCode(200).end(result);
		}
	}

	public void postRemainingCaptureJobList(RoutingContext routingContext, String nextPagetoken) {
		SOAPMessage message = new SOAPMessage();
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
			HTTPUtil.sendQueryResults(routingContext.response(), message, e, e.getClass(), 406);
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
			HTTPUtil.sendQueryResults(routingContext.response(), message, e1, e1.getClass(), 500);
			return;
		}

		String result = "<epcisCaptureJobList>";
		// there are remaining list
		int jobSize = jobs.size();
		if (perPage < jobs.size()) {
			jobSize = perPage;
		}
		for (int j = 0; j < jobSize; j++) {
			try {
				EPCISCaptureJobType captureJob = Transaction.toCaptureJob(jobs.get(j));
				org.w3c.dom.Document retDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
				JAXBContext jc = JAXBContext.newInstance(EPCISCaptureJobType.class);
				Marshaller marshaller = jc.createMarshaller();
				marshaller.marshal(captureJob, retDoc);
				String[] lines = XMLUtil.toString(retDoc, true).split("\n");
				for (int i = 1; i < lines.length; i++) {
					result += lines[i] + "\n";
				}
			} catch (Exception throwable) {
				ImplementationException e = new ImplementationException(ImplementationExceptionSeverity.ERROR, null,
						null, throwable.getMessage());
				HTTPUtil.sendQueryResults(routingContext.response(), message, e, e.getClass(), 500);
				return;
			}
		}
		result += "</epcisCaptureJobList>";
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
					.putHeader("content-type", "application/xml").setStatusCode(200).end(result);
		} else {
			EPCISServer.captureIDPageMap.remove(uuid);
			EPCISServer.logger.debug("[GET /capture] page - " + uuid + " expired. # remaining pages - "
					+ EPCISServer.captureIDPageMap.size());
			routingContext.response().putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-Extension", Metadata.GS1_Extensions).putHeader("content-type", "application/xml")
					.setStatusCode(200).end(result);
		}
	}

	public void postCaptureJob(RoutingContext routingContext, String captureID) {
		SOAPMessage message = new SOAPMessage();

		List<Document> jobs = new ArrayList<Document>();
		try {

			EPCISServer.mTxCollection.find(new Document("_id", new ObjectId(captureID))).into(jobs);

		} catch (MongoException e) {
			ImplementationException e1 = new ImplementationException(ImplementationExceptionSeverity.ERROR, null, null,
					e.getMessage());
			HTTPUtil.sendQueryResults(routingContext.response(), message, e1, e1.getClass(), 500);
			return;
		}

		if (jobs == null || jobs.isEmpty()) {
			EPCISException e = new EPCISException("There is no capture job with id: " + captureID);
			HTTPUtil.sendQueryResults(routingContext.response(), message, e, e.getClass(), 404);
		} else {
			try {
				EPCISCaptureJobType captureJob = Transaction.toCaptureJob(jobs.get(0));
				org.w3c.dom.Document retDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
				JAXBContext jc = JAXBContext.newInstance(EPCISCaptureJobType.class);
				Marshaller marshaller = jc.createMarshaller();
				marshaller.marshal(captureJob, retDoc);
				routingContext.response().putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
						.putHeader("GS1-Extension", Metadata.GS1_Extensions)
						.putHeader("content-type", "application/xml; charset=utf-8").setStatusCode(200)
						.end(XMLUtil.toString(retDoc));
			} catch (Exception throwable) {
				ImplementationException e = new ImplementationException(ImplementationExceptionSeverity.ERROR, null,
						null, throwable.getMessage());
				HTTPUtil.sendQueryResults(routingContext.response(), message, e, e.getClass(), 500);
			}
		}
	}
}
