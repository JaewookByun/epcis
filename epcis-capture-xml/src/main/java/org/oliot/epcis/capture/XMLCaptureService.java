package org.oliot.epcis.capture;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.xml.bind.*;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;

import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.oliot.epcis.converter.xml.write.XMLAggregationEventWriteConverter;
import org.oliot.epcis.converter.xml.write.XMLAssociationEventWriteConverter;
import org.oliot.epcis.converter.xml.write.XMLMasterDataWriteConverter;
import org.oliot.epcis.converter.xml.write.XMLObjectEventWriteConverter;
import org.oliot.epcis.converter.xml.write.XMLTransactionEventWriteConverter;
import org.oliot.epcis.converter.xml.write.XMLTransformationEventWriteConverter;
import org.oliot.epcis.model.*;
import org.oliot.epcis.model.exception.ValidationException;
import org.oliot.epcis.util.ObservableSubscriber;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.WriteModel;
import org.oliot.epcis.util.XMLUtil;
import org.oliot.epcis.transaction.Transaction;

/**
 * Copyright (C) 2020-2022. (Jaewook Byun) all rights reserved.
 * <p>
 * Oliot EPCIS X is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * Among various modules, epcis-capture-xml acts as a server to receive
 * XML-formatted EPCIS documents to capture events in the documents into an
 * EPCIS repository.
 * <p>
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr
 *         <p>
 *         Associate Director, Auto-ID Labs, KAIST, bjw0829@kaist.ac.kr
 */
public class XMLCaptureService {

	public void postValidationResult(RoutingContext routingContext) {
		String inputString = routingContext.body().asString();
		InputStream validateStream = XMLUtil.getXMLDocumentInputStream(inputString);
		try {
			validateXML(validateStream);
			routingContext.response().end();
		} catch (Exception e) {
			routingContext.response().setStatusCode(400).end(e.getMessage());
		}
	}

	public void post(RoutingContext routingContext, EventBus eventBus, Transaction tx) throws RuntimeException {
		String inputString = routingContext.body().asString();
		InputStream epcisStream = XMLUtil.getXMLDocumentInputStream(inputString);
		InputStream validateStream = XMLUtil.getXMLDocumentInputStream(inputString);
		// Validation
		try {
			validateXML(validateStream);
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
		// Unmarshalling
		EPCISDocumentType epcisDocument;
		try {
			epcisDocument = JAXB.unmarshal(epcisStream, EPCISDocumentType.class);
		} catch (DataBindingException e) {
			tx.setErrorType(e.getCause().getClass().getCanonicalName());
			tx.setErrorMessage(e.getCause().getMessage());
			if (tx.isRollback())
				eventBus.send("txRollback", tx.getJson());
			else
				eventBus.send("txProceed", tx.getJson());
			routingContext.response().setStatusCode(400).end(tx.getTxId());
			throw new RuntimeException(e);
		}
		XMLCaptureServer.logger.debug("unmarshal");
		boolean eventExist = true;
		boolean vocExist = true;
		try {
			List<Object> eventList = epcisDocument.getEPCISBody().getEventList()
					.getObjectEventOrAggregationEventOrTransformationEvent();
			if (eventList != null && eventList.size() != 0)
				captureEvents(routingContext, eventList, eventBus, tx);
			else
				eventExist = false;
		} catch (NullPointerException e) {
			// No eventList
			eventExist = false;
		}
		try {
			List<VocabularyType> vList = epcisDocument.getEPCISHeader().getExtension().getEPCISMasterData()
					.getVocabularyList().getVocabulary();
			if (vList != null && vList.size() != 0)
				captureMasterData(routingContext, vList);
			else
				vocExist = false;
		} catch (NullPointerException e) {
			// No master-data
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

	public void postMasterData(RoutingContext routingContext, EventBus eventBus) throws RuntimeException {
		String inputString = routingContext.body().asString();
		InputStream epcisStream = XMLUtil.getXMLDocumentInputStream(inputString);
		InputStream validateStream = XMLUtil.getXMLDocumentInputStream(inputString);
		// Validation
		validateMasterDataXML(validateStream);
		// Unmarshallling
		EPCISMasterDataDocumentType epcisMasterDataDocument = null;
		try {
			epcisMasterDataDocument = JAXB.unmarshal(epcisStream, EPCISMasterDataDocumentType.class);
		} catch (DataBindingException e) {
			throw new RuntimeException(e);
		}
		XMLCaptureServer.logger.debug("unmarshal");
		try {
			List<VocabularyType> vList = epcisMasterDataDocument.getEPCISBody().getVocabularyList().getVocabulary();
			if (vList != null && vList.size() != 0)
				captureMasterData(routingContext, vList);
		} catch (NullPointerException e) {
			// No master-data
		}
	}

	public void postEvent(RoutingContext routingContext, EventBus eventBus) throws RuntimeException {
		String type = routingContext.request().getParam("type");

		String inputString = routingContext.body().asString();
		InputStream epcisStream = XMLUtil.getXMLDocumentInputStream(inputString);
		InputStream validateStream = XMLUtil.getXMLDocumentInputStream(inputString);
		// Validation
		validateXML(validateStream);
		// Unmarshalling
		if (type == null) {
			try {
				AggregationEventType event = JAXB.unmarshal(epcisStream, AggregationEventType.class);
				XMLCaptureServer.logger.debug("unmarshal");
				captureEvent(routingContext, event);
			} catch (DataBindingException e) {
				try {
					AssociationEventType event = JAXB.unmarshal(epcisStream, AssociationEventType.class);
					XMLCaptureServer.logger.debug("unmarshal");
					captureEvent(routingContext, event);
				} catch (DataBindingException e1) {
					try {
						ObjectEventType event = JAXB.unmarshal(epcisStream, ObjectEventType.class);
						XMLCaptureServer.logger.debug("unmarshal");
						captureEvent(routingContext, event);
					} catch (DataBindingException e2) {
						try {
							TransactionEventType event = JAXB.unmarshal(epcisStream, TransactionEventType.class);
							XMLCaptureServer.logger.debug("unmarshal");
							captureEvent(routingContext, event);
						} catch (DataBindingException e3) {
							try {
								TransformationEventType event = JAXB.unmarshal(epcisStream,
										TransformationEventType.class);
								XMLCaptureServer.logger.debug("unmarshal");
								captureEvent(routingContext, event);
							} catch (DataBindingException e4) {
								routingContext.response().setStatusCode(400).end();
							}
						}
					}
				}
			}
		} else {
			if (type.equals("AggregationEvent")) {
				AggregationEventType event = JAXB.unmarshal(epcisStream, AggregationEventType.class);
				XMLCaptureServer.logger.debug("unmarshal");
				captureEvent(routingContext, event);
			} else if (type.equals("AssociationEvent")) {
				AssociationEventType event = JAXB.unmarshal(epcisStream, AssociationEventType.class);
				XMLCaptureServer.logger.debug("unmarshal");
				captureEvent(routingContext, event);
			} else if (type.equals("ObjectEvent")) {
				ObjectEventType event = JAXB.unmarshal(epcisStream, ObjectEventType.class);
				XMLCaptureServer.logger.debug("unmarshal");
				captureEvent(routingContext, event);
			} else if (type.equals("TransactionEvent")) {
				TransactionEventType event = JAXB.unmarshal(epcisStream, TransactionEventType.class);
				XMLCaptureServer.logger.debug("unmarshal");
				captureEvent(routingContext, event);
			} else if (type.equals("TransformationEvent")) {
				TransformationEventType event = JAXB.unmarshal(epcisStream, TransformationEventType.class);
				XMLCaptureServer.logger.debug("unmarshal");
				captureEvent(routingContext, event);
			} else {
				routingContext.response().setStatusCode(400).end();
			}
		}
	}

	private void captureEvent(RoutingContext routingContext, Object jaxbEvent) throws RuntimeException {
		Document obj = null;
		try {
			obj = prepareEvent(jaxbEvent);
		} catch (ValidationException e) {
			throw new RuntimeException(e);
		}
		XMLCaptureServer.logger.debug("ready");

		if (!obj.containsKey("errorDeclaration")) {
			ObservableSubscriber<InsertOneResult> subscriber = new ObservableSubscriber<InsertOneResult>();
			XMLCaptureServer.mEventCollection.insertOne(obj).subscribe(subscriber);
			try {
				subscriber.await();
			} catch (Throwable e) {
				throw new RuntimeException(e.getMessage());
			}
			XMLCaptureServer.logger.debug("event captured: " + subscriber.getReceived());
			routingContext.response().end();
		} else {
			Document filter = new Document("eventID", obj.getString("eventID"));
			ObservableSubscriber<UpdateResult> subscriber = new ObservableSubscriber<UpdateResult>();
			XMLCaptureServer.mEventCollection.replaceOne(filter, obj).subscribe(subscriber);
			try {
				subscriber.await();
			} catch (Throwable e) {
				throw new RuntimeException(e.getMessage());
			}
			XMLCaptureServer.logger.debug("event captured: " + subscriber.getReceived());
			routingContext.response().end();
		}
	}

	private void captureEvents(RoutingContext routingContext, List<Object> eventList, EventBus eventBus, Transaction tx)
			throws RuntimeException {
		List<WriteModel<Document>> bulk = eventList.stream().map(jaxbEvent -> {
			Document obj = null;
			try {
				obj = prepareEvent(jaxbEvent);
				obj.put("_tx", tx.getTxId());
			} catch (ValidationException e) {
				tx.setErrorType(ValidationException.class.getCanonicalName());
				tx.setErrorMessage(e.getMessage());
				if (tx.isRollback())
					eventBus.send("txRollback", tx.getJson());
				else
					eventBus.send("txProceed", tx.getJson());
				routingContext.response().setStatusCode(400).end(tx.getTxId());
				throw new RuntimeException(e);
			}

			if (!obj.containsKey("errorDeclaration")) {
				return new InsertOneModel<Document>(obj);
			} else {
				Document filter = new Document("eventID", obj.getString("eventID"));
				return new ReplaceOneModel<Document>(filter, obj);
			}

		}).filter(Objects::nonNull).collect(Collectors.toList());

		XMLCaptureServer.logger.debug("ready");

		ObservableSubscriber<BulkWriteResult> subscriber = new ObservableSubscriber<BulkWriteResult>();

		XMLCaptureServer.mEventCollection.bulkWrite(bulk).subscribe(subscriber);

		try {
			subscriber.await();
		} catch (Throwable e) {
			tx.setErrorType(e.getCause().getClass().getCanonicalName());
			tx.setErrorMessage(e.getMessage());
			if (tx.isRollback())
				eventBus.send("txRollback", tx.getJson());
			else
				eventBus.send("txProceed", tx.getJson());
			routingContext.response().setStatusCode(400).end(tx.getTxId());
			throw new RuntimeException(e.getMessage());
		}
		XMLCaptureServer.logger.debug("event captured: " + subscriber.getReceived());
		eventBus.send("txSuccess", tx.getJson());
		routingContext.response().end(tx.getTxId());

		/*
		 * for (int i = 0; i < bulk.size(); i++) { try { JsonObject jsonEvent =
		 * bulk.get(i).getDocument(); TriggerEngine.examineAndFire(jsonEvent,
		 * eventList.get(i)); } catch (Exception e) {
		 * EPCISServer.logger.error(e.getMessage()); } }
		 */
	}

	private Document prepareEvent(Object event) throws ValidationException {

		Document object2Save = null;
		if (event instanceof AggregationEventType) {
			object2Save = XMLAggregationEventWriteConverter.convert((AggregationEventType) event);
		} else if (event instanceof ObjectEventType) {
			object2Save = XMLObjectEventWriteConverter.convert((ObjectEventType) event);
		} else if (event instanceof TransactionEventType) {
			object2Save = XMLTransactionEventWriteConverter.convert((TransactionEventType) event);
		} else if (event instanceof TransformationEventType) {
			object2Save = XMLTransformationEventWriteConverter.convert((TransformationEventType) event);
		} else if (event instanceof AssociationEventType) {
			object2Save = XMLAssociationEventWriteConverter.convert((AssociationEventType) event);
		}

		return object2Save;
	}

	private void captureMasterData(RoutingContext routingContext, List<VocabularyType> vList) {
		List<WriteModel<Document>> bulk = vList.parallelStream().flatMap(v -> XMLMasterDataWriteConverter.convert(v))
				.filter(Objects::nonNull).collect(Collectors.toList());

		XMLCaptureServer.logger.debug("ready");

		ObservableSubscriber<BulkWriteResult> subscriber = new ObservableSubscriber<BulkWriteResult>();

		XMLCaptureServer.mVocCollection.bulkWrite(bulk).subscribe(subscriber);

		try {
			subscriber.await();
		} catch (Throwable e) {
			throw new RuntimeException(e.getMessage());
		}

		XMLCaptureServer.logger.debug("vocabulary captured: " + subscriber.getReceived());
		routingContext.response().end();

	}

	public void validateXML(InputStream is) throws RuntimeException {
		try {
			StreamSource xmlSource = new StreamSource(is);
			XMLCaptureServer.xmlValidator.validate(xmlSource);
			XMLCaptureServer.logger.debug("validated");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void validateMasterDataXML(InputStream is) throws RuntimeException {
		try {
			StreamSource xmlSource = new StreamSource(is);
			XMLCaptureServer.xmlMasterDataValidator.validate(xmlSource);
			XMLCaptureServer.logger.debug("validated");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void postCaptureJob(RoutingContext routingContext)
			throws ParserConfigurationException, JAXBException, TransformerException, DatatypeConfigurationException {

		ObservableSubscriber<Document> subscriber = new ObservableSubscriber<Document>();

		XMLCaptureServer.mTxCollection.find().projection(new Document().append("_id", true)).subscribe(subscriber);

		try {
			subscriber.await();
		} catch (Throwable e) {
			throw new RuntimeException(e.getMessage());
		}

		List<Document> jobs = subscriber.getReceived();
		if (jobs == null || jobs.isEmpty()) {
			routingContext.response().setStatusCode(404).end();
		} else {
			JsonArray array = new JsonArray();
			for(Document job: jobs) {
				array.add(job.getObjectId("_id").toHexString());
			}
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
					.end(array.toString());
		}
	}
	

	public void postCaptureJob(RoutingContext routingContext, String captureID)
			throws ParserConfigurationException, JAXBException, TransformerException, DatatypeConfigurationException {

		ObservableSubscriber<Document> subscriber = new ObservableSubscriber<Document>();

		XMLCaptureServer.mTxCollection.find(new Document("_id", new ObjectId(captureID))).subscribe(subscriber);

		try {
			subscriber.await();
		} catch (Throwable e) {
			throw new RuntimeException(e.getMessage());
		}

		List<Document> jobs = subscriber.getReceived();
		if (jobs == null || jobs.isEmpty()) {
			routingContext.response().setStatusCode(404).end();
		} else {
			EPCISCaptureJobType captureJob = Transaction.toCaptureJob(jobs.get(0));
			org.w3c.dom.Document retDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			JAXBContext jc = JAXBContext.newInstance(EPCISCaptureJobType.class);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.marshal(captureJob, retDoc);
			routingContext.response().putHeader("content-type", "application/xml; charset=utf-8").setStatusCode(200)
					.end(XMLUtil.toString(retDoc));
		}
	}
}
