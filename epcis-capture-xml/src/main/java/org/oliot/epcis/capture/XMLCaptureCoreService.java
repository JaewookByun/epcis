package org.oliot.epcis.capture;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.bind.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;

import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.RoutingContext;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.oliot.epcis.common.Metadata;
import org.oliot.epcis.converter.pojo_to_bson.AggregationEventConverter;
import org.oliot.epcis.converter.pojo_to_bson.AssociationEventConverter;
import org.oliot.epcis.converter.pojo_to_bson.MasterDataConverter;
import org.oliot.epcis.converter.pojo_to_bson.ObjectEventConverter;
import org.oliot.epcis.converter.pojo_to_bson.TransactionEventConverter;
import org.oliot.epcis.converter.pojo_to_bson.TransformationEventConverter;
import org.oliot.epcis.model.*;
import org.oliot.epcis.model.ValidationException;
import org.oliot.epcis.model.cbv.EPCISEventType;
import org.oliot.epcis.pagination.Page;
import org.oliot.epcis.pagination.PageExpiryTimerTask;
import org.oliot.epcis.util.*;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.WriteModel;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import static org.oliot.epcis.capture.XMLCaptureServer.*;

/**
 * Copyright (C) 2020-2023. (Jaewook Byun) all rights reserved.
 * <p>
 * This project is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * Among various modules, epcis-capture-xml acts as a server to receive
 * XML-formatted EPCIS documents to capture events in the documents into an
 * EPCIS repository.
 * <p>
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr, Associate Director, Auto-ID Labs, Korea,
 *         bjw0829@gmail.com
 */
public class XMLCaptureCoreService {

	public void postValidationResult(RoutingContext routingContext) {
		String inputString = routingContext.body().asString();
		InputStream validateStream = XMLUtil.getXMLDocumentInputStream(XMLUtil.getByteArray(inputString));
		try {
			validateXML(validateStream);
			routingContext.response().end();
		} catch (ValidationException e) {
			routingContext.response().setStatusCode(400).end(e.getMessage());
		}
	}

	public void post(RoutingContext routingContext, EventBus eventBus) {
		String inputString = routingContext.body().asString();

		// payload check
		if (inputString.length() * 4 > Metadata.GS1_CAPTURE_file_size_limit) {
			EPCISException e = new EPCISException(
					"[413CapturePayloadTooLarge] The `POST` request is too large. It exceeds the limits set in `GS1-EPCIS-Capture-File-Size-Limit`.\n");
			HTTPUtil.sendQueryResults(routingContext.response(), e, null, null, null, e.getClass(), 413);
			return;
		}

		byte[] xmlByteArray = XMLUtil.getByteArray(inputString);

		// Validation
		try {
			validateXML(XMLUtil.getXMLDocumentInputStream(xmlByteArray));
		} catch (ValidationException e) {
			HTTPUtil.sendQueryResults(routingContext.response(), e, null, null, null, e.getClass(), 400);
			return;
		}

		// Unmarshalling
		EPCISDocumentType epcisDocument;
		try {
			epcisDocument = JAXB.unmarshal(XMLUtil.getXMLDocumentInputStream(xmlByteArray), EPCISDocumentType.class);
		} catch (DataBindingException e) {
			HTTPUtil.sendQueryResults(routingContext.response(), e, null, null, null, e.getClass(), 400);
			return;
		}

		XMLCaptureServer.logger.debug("unmarshal");
		boolean eventExist = true;
		boolean vocExist = true;
		try {
			List<Object> eventList = epcisDocument.getEPCISBody().getEventList()
					.getObjectEventOrAggregationEventOrTransformationEvent();

			if (eventList.size() > Metadata.GS1_CAPTURE_limit) {
				EPCISException e = new EPCISException(
						"[413CapturePayLodTooLarge] The `POST` request is too large. It exceeds the limits set in `GS1-EPCIS-Capture-Limit`.\n");
				HTTPUtil.sendQueryResults(routingContext.response(), e, null, null, null, e.getClass(), 406);
				return;
			}

			if (eventList != null && eventList.size() != 0) {
				// ready to 202
				Transaction tx = new Transaction(Metadata.GS1_EPCIS_Capture_Error_Behaviour);
				routingContext.response().putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
						.putHeader("GS1-CBV-Version", Metadata.GS1_CBV_Version)
						.putHeader("GS1-Extensions", Metadata.GS1_Extensions)
						.putHeader("Location",
								"http://" + host + ":" + XMLCaptureServer.port + "/epcis/capture/" + tx.getTxId())
						.setStatusCode(202).end();
				eventBus.send("txStart", tx.getJson());
				captureEvents(routingContext, eventList, eventBus, tx);
			} else
				eventExist = false;
		} catch (NullPointerException e) {
			// No eventList
			eventExist = false;
		}
		if (eventExist == true)
			return;

		try {
			List<VocabularyType> vList = epcisDocument.getEPCISHeader().getExtension().getEPCISMasterData()
					.getVocabularyList().getVocabulary();

			if (vList.size() > Metadata.GS1_CAPTURE_limit) {
				EPCISException e = new EPCISException(
						"[413CapturePayLodTooLarge] The `POST` request is too large. It exceeds the limits set in `GS1-EPCIS-Capture-Limit`.\n");
				HTTPUtil.sendQueryResults(routingContext.response(), e, null, null, null, e.getClass(), 406);
				return;
			}

			if (vList != null && vList.size() != 0) {
				captureMasterData(routingContext, vList, eventBus);
			} else
				vocExist = false;
		} catch (NullPointerException e) {
			// No master-data
			vocExist = false;
		}
		// No event and master-data
		if (vocExist == false) {
			ValidationException e = new ValidationException("There is no event and master-data.");
			HTTPUtil.sendQueryResults(routingContext.response(), e, null, null, null, e.getClass(), 400);
		}
	}

	public void postEvent(RoutingContext routingContext) {
		String inputString = routingContext.body().asString();

		// payload check
		if (inputString.length() * 4 > Metadata.GS1_CAPTURE_file_size_limit) {
			EPCISException e = new EPCISException(
					"[413CapturePayloadTooLarge] The `POST` request is too large. It exceeds the limits set in `GS1-EPCIS-Capture-File-Size-Limit`.\n");
			HTTPUtil.sendQueryResults(routingContext.response(), e, null, null, null, e.getClass(), 413);
			return;
		}

		byte[] xmlByteArray = XMLUtil.getByteArray(inputString);

		// Validation
		try {
			validateXML(XMLUtil.getXMLDocumentInputStream(xmlByteArray));
		} catch (ValidationException e) {
			logger.error(e.getReason());
			HTTPUtil.sendQueryResults(routingContext.response(), e, null, null, null, e.getClass(), 400);
			return;
		}

		// Get Event Type
		String type = XMLUtil.getCaptureInputType(XMLUtil.getXMLDocumentInputStream(xmlByteArray));
		InputStream epcisStream = XMLUtil.getXMLDocumentInputStream(xmlByteArray);
		if (type == null) {
			ValidationException e = new ValidationException("Input should be one of " + Stream.of(EPCISEventType.values()).toList());
			logger.error(e.getReason());
			HTTPUtil.sendQueryResults(routingContext.response(), e, null, null, null, e.getClass(), 400);
			return;
		}
		if (type.equals("AggregationEvent")) {
			AggregationEventType event = JAXB.unmarshal(epcisStream, AggregationEventType.class);
			XMLCaptureServer.logger.debug("unmarshal - aggregation event");
			captureEvent(routingContext, event);
		} else if (type.equals("AssociationEvent")) {
			AssociationEventType event = JAXB.unmarshal(epcisStream, AssociationEventType.class);
			XMLCaptureServer.logger.debug("unmarshal - association event");
			captureEvent(routingContext, event);
		} else if (type.equals("ObjectEvent")) {
			ObjectEventType event = JAXB.unmarshal(epcisStream, ObjectEventType.class);
			XMLCaptureServer.logger.debug("unmarshal - object event");
			captureEvent(routingContext, event);
		} else if (type.equals("TransactionEvent")) {
			TransactionEventType event = JAXB.unmarshal(epcisStream, TransactionEventType.class);
			XMLCaptureServer.logger.debug("unmarshal - transaction event");
			captureEvent(routingContext, event);
		} else if (type.equals("TransformationEvent")) {
			TransformationEventType event = JAXB.unmarshal(epcisStream, TransformationEventType.class);
			XMLCaptureServer.logger.debug("unmarshal - transformation event");
			captureEvent(routingContext, event);
		} 

	}

	private void captureEvent(RoutingContext routingContext, Object jaxbEvent) {
		Document obj = null;
		try {
			obj = prepareEvent(jaxbEvent);
		} catch (ValidationException e) {
			logger.error(e.getReason());
			HTTPUtil.sendQueryResults(routingContext.response(), e, null, null, null, e.getClass(), 400);
			return;
		}
		XMLCaptureServer.logger.debug("ready to capture");

		if (!obj.containsKey("errorDeclaration")) {
			ObservableSubscriber<InsertOneResult> subscriber = new ObservableSubscriber<InsertOneResult>();
			XMLCaptureServer.mEventCollection.insertOne(obj).subscribe(subscriber);
			try {
				subscriber.await();
			} catch (Throwable e) {
				logger.error(e.getMessage());
				ImplementationException e1 = new ImplementationException(e.getMessage());
				HTTPUtil.sendQueryResults(routingContext.response(), e1, null, null, null, e1.getClass(), 500);
				return;
			}
			XMLCaptureServer.logger.debug("event captured: " + subscriber.getReceived());
			try {
				routingContext.response().putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
						.putHeader("GS1-CBV-Version", Metadata.GS1_CBV_Version)
						.putHeader("GS1-Extension", Metadata.GS1_Extensions)
						.putHeader("Location", "/events/" + URLEncoder.encode(obj.getString("eventID"), "UTF-8"))
						.setStatusCode(201).end();
			} catch (Exception e) {
				// not happen
			}
		} else {
			Document filter = new Document("eventID", obj.getString("eventID"));
			ObservableSubscriber<UpdateResult> subscriber = new ObservableSubscriber<UpdateResult>();
			XMLCaptureServer.mEventCollection.replaceOne(filter, obj).subscribe(subscriber);
			try {
				subscriber.await();
			} catch (Throwable e) {
				logger.error(e.getMessage());
				ImplementationException e1 = new ImplementationException(e.getMessage());
				HTTPUtil.sendQueryResults(routingContext.response(), e1, null, null, null, e1.getClass(), 500);
				return;
			}
			XMLCaptureServer.logger.debug("event captured: " + subscriber.getReceived());
			try {
				routingContext.response().putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
						.putHeader("GS1-CBV-Version", Metadata.GS1_CBV_Version)
						.putHeader("GS1-Extension", Metadata.GS1_Extensions)
						.putHeader("Location",
								"http://" + host + ":8084/epcis/events/"
										+ URLEncoder.encode(obj.getString("eventID"), "UTF-8"))
						.setStatusCode(201).end();
			} catch (Exception e) {
				// not happened
			}
		}
	}

	private void captureEvents(RoutingContext routingContext, List<Object> eventList, EventBus eventBus,
			Transaction tx) {

		List<WriteModel<Document>> bulk = null;
		try {
			bulk = eventList.stream().map(jaxbEvent -> {
				Document obj = null;
				try {
					obj = prepareEvent(jaxbEvent);
					obj.put("_tx", tx.getTxId());
				} catch (ValidationException e) {
					logger.error(e.getReason());
					tx.setErrorType(ValidationException.class.getCanonicalName());
					tx.setErrorMessage(e.getReason());
					if (tx.isRollback()) {
						eventBus.send("txRollback", tx.getJson());

						throw new RuntimeException(e);
					} else {
						return null;
					}
				}

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
		XMLCaptureServer.logger.debug("ready to capture events");

		ObservableSubscriber<BulkWriteResult> subscriber = new ObservableSubscriber<BulkWriteResult>();

		XMLCaptureServer.mEventCollection.bulkWrite(bulk).subscribe(new Subscriber<BulkWriteResult>() {
			@Override
			public void onSubscribe(Subscription s) {
				s.request(1);
			}

			@Override
			public void onNext(BulkWriteResult bulkWriteResult) {
			}

			@Override
			public void onError(Throwable t) {
				logger.error(t.getMessage());
				tx.setErrorType(t.getCause().getClass().getCanonicalName());
				tx.setErrorMessage(t.getMessage());
				if (tx.isRollback())
					eventBus.send("txRollback", tx.getJson());
				else
					eventBus.send("txProceed", tx.getJson());
			}

			@Override
			public void onComplete() {
				XMLCaptureServer.logger.debug("event captured: " + subscriber.getReceived());
				if (tx.getErrorType() == null)
					eventBus.send("txSuccess", tx.getJson());
				else
					eventBus.send("txProceed", tx.getJson());
			}
		});
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

	private void captureMasterData(RoutingContext routingContext, List<VocabularyType> vList, EventBus eventBus) {
		List<WriteModel<Document>> bulk = null;
		try {
			bulk = vList.parallelStream().flatMap(v -> {
				try {
					return MasterDataConverter.toBson(v).parallelStream();
				} catch (ValidationException e) {
					throw new RuntimeException(e);
				}
			}).filter(Objects::nonNull).collect(Collectors.toList());
		} catch (RuntimeException e) {
			ValidationException ve = (ValidationException) e.getCause();
			logger.error(ve.getReason());
			HTTPUtil.sendQueryResults(routingContext.response(), ve, null, null, null, ve.getClass(), 400);
			return;
		}

		XMLCaptureServer.logger.debug("ready to capture");

		ObservableSubscriber<BulkWriteResult> subscriber = new ObservableSubscriber<BulkWriteResult>();

		XMLCaptureServer.mVocCollection.bulkWrite(bulk).subscribe(new Subscriber<BulkWriteResult>() {
			@Override
			public void onSubscribe(Subscription s) {
				s.request(1);
			}

			@Override
			public void onNext(BulkWriteResult bulkWriteResult) {

			}

			@Override
			public void onError(Throwable t) {
				ImplementationException ie = new ImplementationException(t.getMessage());
				logger.error(ie.getReason());
				HTTPUtil.sendQueryResults(routingContext.response(), ie, null, null, null, ie.getClass(), 500);
			}

			@Override
			public void onComplete() {
				logger.debug("vocabulary captured: " + subscriber.getReceived());
				routingContext.response().putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
				.putHeader("GS1-CBV-Version", Metadata.GS1_CBV_Version)
				.putHeader("GS1-Extensions", Metadata.GS1_Extensions)
				.setStatusCode(201).end();
			}
		});
	}

	public void validateXML(InputStream is) throws ValidationException {
		try {
			StreamSource xmlSource = new StreamSource(is);
			XMLCaptureServer.xmlValidator.validate(xmlSource);
			XMLCaptureServer.logger.debug("validated");
		} catch (Exception e) {
			throw new ValidationException(e.getMessage());
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

		ObservableSubscriber<Document> subscriber = new ObservableSubscriber<Document>();
		Document sort = new Document().append("createdAt", -1);
		XMLCaptureServer.mTxCollection.find().sort(sort).limit(perPage + 1).subscribe(subscriber);

		try {
			subscriber.await();
		} catch (Throwable e) {
			ImplementationException e1 = new ImplementationException(e.getMessage());
			HTTPUtil.sendQueryResults(routingContext.response(), e1, null, null, null, e1.getClass(), 500);
			return;
		}

		List<Document> jobs = subscriber.getReceived();
		String result = "<epcisCaptureJobList>";
		for (Document job : jobs) {
			try {
				EPCISCaptureJobType captureJob = Transaction.toCaptureJob(job);
				org.w3c.dom.Document retDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
				JAXBContext jc = JAXBContext.newInstance(EPCISCaptureJobType.class);
				Marshaller marshaller = jc.createMarshaller();
				marshaller.marshal(captureJob, retDoc);
				String[] lines = XMLUtil.toString(retDoc).split("\n");
				for (int i = 1; i < lines.length; i++) {
					result += lines[i] + "\n";
				}
			} catch (Exception throwable) {
				ImplementationException e = new ImplementationException(throwable.getMessage());
				HTTPUtil.sendQueryResults(routingContext.response(), e, null, null, null, e.getClass(), 500);
				return;
			}
		}
		result += "</epcisCaptureJobList>";

		if (perPage < jobs.size()) {
			UUID uuid;
			long currentTime = System.currentTimeMillis();

			while (true) {
				uuid = UUID.randomUUID();
				if (!captureIDPageMap.containsKey(uuid))
					break;
			}
			Page page = new Page(uuid, null, sort, perPage);
			Timer timer = new Timer();
			page.setTimer(timer);
			timer.schedule(new PageExpiryTimerTask("GET /capture", captureIDPageMap, uuid, logger),
					Metadata.GS1_Next_Page_Token_Expires);
			captureIDPageMap.put(uuid, page);
			logger.debug("[GET /capture] page - " + uuid + " added. # remaining pages - " + captureIDPageMap.size());

			routingContext.response().putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-Extension", Metadata.GS1_Extensions)
					.putHeader("Link",
							"http://" + host + ":8084/epcis/capture?PerPage=" + perPage + "&NextPageToken="
									+ uuid.toString())
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

		if (!captureIDPageMap.containsKey(uuid)) {
			EPCISException e = new EPCISException(
					"[406NotAcceptable] The given next page token does not exist or be no longer available.");
			HTTPUtil.sendQueryResults(routingContext.response(), e, null, null, null, e.getClass(), 406);
			return;
		} else {
			page = captureIDPageMap.get(uuid);
		}
		ObservableSubscriber<Document> subscriber = new ObservableSubscriber<Document>();
		Document sort = (Document) page.getSort();
		int skip = page.getSkip();
		page.setSkip(skip + perPage);
		XMLCaptureServer.mTxCollection.find().sort(sort).skip(skip).limit(perPage + 1).subscribe(subscriber);

		try {
			subscriber.await();
		} catch (Throwable e) {
			ImplementationException e1 = new ImplementationException(e.getMessage());
			HTTPUtil.sendQueryResults(routingContext.response(), e1, null, null, null, e1.getClass(), 500);
			return;
		}
		List<Document> jobs = subscriber.getReceived();
		String result = "<epcisCaptureJobList>";
		for (Document job : jobs) {
			try {
				EPCISCaptureJobType captureJob = Transaction.toCaptureJob(job);
				org.w3c.dom.Document retDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
				JAXBContext jc = JAXBContext.newInstance(EPCISCaptureJobType.class);
				Marshaller marshaller = jc.createMarshaller();
				marshaller.marshal(captureJob, retDoc);
				String[] lines = XMLUtil.toString(retDoc).split("\n");
				for (int i = 1; i < lines.length; i++) {
					result += lines[i] + "\n";
				}
			} catch (Exception throwable) {
				ImplementationException e = new ImplementationException(throwable.getMessage());
				HTTPUtil.sendQueryResults(routingContext.response(), e, null, null, null, e.getClass(), 500);
				return;
			}
		}
		result += "</epcisCaptureJobList>";
		if (perPage < jobs.size()) {
			long currentTime = System.currentTimeMillis();
			Timer timer = page.getTimer();
			if (timer != null)
				timer.cancel();

			Timer newTimer = new Timer();
			page.setTimer(newTimer);
			newTimer.schedule(new PageExpiryTimerTask("GET /capture", captureIDPageMap, uuid, logger),
					Metadata.GS1_Next_Page_Token_Expires);
			logger.debug("[GET /capture] page - " + uuid + " token expiry time extended to "
					+ TimeUtil.getDateTimeStamp(currentTime + Metadata.GS1_Next_Page_Token_Expires));
			routingContext.response().putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-Extension", Metadata.GS1_Extensions)
					.putHeader("Link",
							"http://" + host + ":8084/epcis/capture?PerPage=" + perPage + "&NextPageToken="
									+ uuid.toString())
					.putHeader("GS1-Next-Page-Token-Expires",
							TimeUtil.getDateTimeStamp(currentTime + Metadata.GS1_Next_Page_Token_Expires))
					.putHeader("content-type", "application/xml").setStatusCode(200).end(result);
		} else {
			captureIDPageMap.remove(uuid);
			logger.debug("[GET /capture] page - " + uuid + " expired. # remaining pages - " + captureIDPageMap.size());
			routingContext.response().putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-Extension", Metadata.GS1_Extensions).putHeader("content-type", "application/xml")
					.setStatusCode(200).end(result);
		}
	}

	public void postCaptureJob(RoutingContext routingContext, String captureID) {

		ObservableSubscriber<Document> subscriber = new ObservableSubscriber<Document>();

		XMLCaptureServer.mTxCollection.find(new Document("_id", new ObjectId(captureID))).subscribe(subscriber);

		try {
			subscriber.await();
		} catch (Throwable throwable) {
			ImplementationException e = new ImplementationException(throwable.getMessage());
			HTTPUtil.sendQueryResults(routingContext.response(), e, null, null, null, e.getClass(), 500);
			return;
		}

		List<Document> jobs = subscriber.getReceived();
		if (jobs == null || jobs.isEmpty()) {
			EPCISException e = new EPCISException("There is no capture job with id: " + captureID);
			HTTPUtil.sendQueryResults(routingContext.response(), e, null, null, null, e.getClass(), 404);
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
				ImplementationException e = new ImplementationException(throwable.getMessage());
				HTTPUtil.sendQueryResults(routingContext.response(), e, null, null, null, e.getClass(), 500);
			}
		}
	}
}
