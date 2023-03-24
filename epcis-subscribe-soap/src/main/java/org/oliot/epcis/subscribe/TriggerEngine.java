package org.oliot.epcis.subscribe;

/**
 * Copyright (C) 2020-2021. (Jaewook Byun) all rights reserved.
 * <p>
 * Oliot EPCIS X is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * Among various modules, epcis-subscribe-soap acts as a special server to receive subscription queries
 * to provide filtered, sorted, limited events in a periodic manner or on demand.
 * <p>
 * 
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr
 *         <p>
 *         Associate Director, Auto-ID Labs, KAIST, bjw0829@kaist.ac.kr
 */
import java.net.URI;
import java.util.*;

import javax.xml.bind.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import io.vertx.core.buffer.Buffer;

import org.bson.Document;
import org.oliot.epcis.model.EventListType;
import org.oliot.epcis.model.QueryResults;
import org.oliot.epcis.model.QueryResultsBody;
import org.oliot.epcis.query.Subscription;
import org.oliot.epcis.util.XMLUtil;
import org.w3c.dom.Element;

public class TriggerEngine {
	private static Map<String, Subscription> triggerSubscriptionMap = new HashMap<>();
	private static final Object syncObject = new Object();

	public static void addTriggerSubscription(String subscriptionID, Subscription subscription) {
		synchronized (syncObject) {
			triggerSubscriptionMap.put(subscriptionID, subscription);
		}
	}

	public static Map<String, Subscription> getTriggerSubscriptionMap() {
		synchronized (syncObject) {
			return triggerSubscriptionMap;
		}
	}

	public static void setTriggerSubscriptionMap(Map<String, Subscription> triggerSubscriptionMap) {
		synchronized (syncObject) {
			TriggerEngine.triggerSubscriptionMap = triggerSubscriptionMap;
		}
	}

	public static void removeTriggerSubscription(String subscriptionID) {
		synchronized (syncObject) {
			triggerSubscriptionMap.remove(subscriptionID);
		}
	}

	public static void examineAndFire(Document mongoEvent, Object xmlEvent)
			throws IllegalStateException, ParserConfigurationException {

		synchronized (syncObject) {

			org.w3c.dom.Document retDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element envelope = retDoc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Envelope");
			envelope.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");

			Element body = retDoc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Body");

			triggerSubscriptionMap.entrySet().parallelStream().forEach(elem -> {
				Subscription sub = elem.getValue();

				if (isPassed(mongoEvent, sub)) {
					QueryResults queryResults = new QueryResults();
					queryResults.setQueryName("SimpleEventQuery");
					queryResults.setSubscriptionID(sub.getSubscriptionID());

					QueryResultsBody resultsBody = new QueryResultsBody();

					EventListType elt = new EventListType();
					List<Object> eventList = new ArrayList<Object>();
					eventList.add(xmlEvent);
					elt.setObjectEventOrAggregationEventOrTransformationEvent(eventList);
					resultsBody.setEventList(elt);
					queryResults.setResultsBody(resultsBody);

					sendQueryResults(sub.getDest(), queryResults, retDoc, envelope, body, QueryResults.class);
				}

			});

		}
	}

	public static boolean isEQListOfString(String fieldName, Document objToSave, Document query) {
		if (query.containsKey(fieldName) && objToSave.containsKey(fieldName)) {
			List<String> arr = query.get(fieldName, Document.class).getList("$in", String.class);
			for (String elem : arr) {
				if (objToSave.getString(fieldName).equals(elem))
					return true;
			}
		}
		return false;
	}

	public static boolean isGEorLTLong(String fieldName, Document objToSave, Document query) {
		if (query.containsKey(fieldName) && objToSave.containsKey(fieldName)) {
			Long target = objToSave.getLong(fieldName);
			Document field = query.get(fieldName, Document.class);
			if (field.containsKey("$gte") && target >= field.getLong("$gte"))
				return true;
			else if (field.containsKey("$lt") && target < field.getLong("$lt"))
				return true;
		}
		return false;
	}

	public static boolean isGEorLTLong(Long target, String queryFieldName, Document query) {
		Document field = query.get(queryFieldName, Document.class);
		if (field.containsKey("$gte") && target >= field.getLong("$gte"))
			return true;
		else if (field.containsKey("$lt") && target < field.getLong("$lt"))
			return true;
		return false;
	}

	public static boolean isPassed(Document objToSave, Subscription subscription) {
		Document base = subscription.getQueryDescription().getMongoQuery();
		if (base.size() == 0)
			return true;
		List<Document> queries = base.getList("$and", Document.class);

		for (Document query : queries) {
			if (!isEQListOfString("isA", objToSave, query))
				return false;
			if (!isGEorLTLong("eventTime", objToSave, query))
				return false;
			if (!isGEorLTLong("recordTime", objToSave, query))
				return false;
			if (query.containsKey("errorDeclaration.declarationTime") && objToSave.containsKey("errorDeclaration")
					&& objToSave.get("errorDeclaration", Document.class).containsKey("declarationTime")) {
				Long declarationTime = objToSave.get("errorDeclaration", Document.class).getLong("declarationTime");
				if (!isGEorLTLong(declarationTime, "errorDeclaration.declarationTime", query))
					return false;
			}

			if (query.containsKey("sensorElementList.sensorMetadata.time")
					&& objToSave.containsKey("sensorElementList")) {
				boolean isPass = false;
				for (Document sensorElementObj : objToSave.getList("sensorElementList", Document.class)) {
					if (sensorElementObj.containsKey("sensorMetadata")
							&& sensorElementObj.get("sensorMetadata",Document.class).containsKey("time")) {
						if (!isGEorLTLong(sensorElementObj.get("sensorMetadata", Document.class).getLong("time"),
								"sensorElementList.sensorMetadata.time", query)) {
							isPass = true;
							break;
						}
					}
				}
				if (isPass == false)
					return false;
			}

			if (query.containsKey("sensorElementList.sensorMetadata.startTime")
					&& objToSave.containsKey("sensorElementList")) {
				boolean isPass = false;
				for (Document sensorElementObj : objToSave.getList("sensorElementList", Document.class)) {
					if (sensorElementObj.containsKey("sensorMetadata")
							&& sensorElementObj.get("sensorMetadata", Document.class).containsKey("startTime")) {
						if (!isGEorLTLong(sensorElementObj.get("sensorMetadata", Document.class).getLong("time"),
								"sensorElementList.sensorMetadata.startTime", query)) {
							isPass = true;
							break;
						}
					}
				}
				if (isPass == false)
					return false;
			}
			if (query.containsKey("sensorElementList.sensorMetadata.endTime")
					&& objToSave.containsKey("sensorElementList")) {
				boolean isPass = false;
				for (Document sensorElementObj : objToSave.getList("sensorElementList", Document.class)) {
					if (sensorElementObj.containsKey("sensorMetadata")
							&& sensorElementObj.get("sensorMetadata", Document.class).containsKey("endTime")) {
						if (!isGEorLTLong(sensorElementObj.get("sensorMetadata", Document.class).getLong("endTime"),
								"sensorElementList.sensorMetadata.endTime", query)) {
							isPass = true;
							break;
						}
					}
				}
				if (isPass == false)
					return false;
			}

			if (!isEQListOfString("action", objToSave, query))
				return false;

			if (!isEQListOfString("bizStep", objToSave, query))
				return false;

			if (!isEQListOfString("disposition", objToSave, query))
				return false;

			if (!isEQListOfString("readPoint", objToSave, query))
				return false;

			if (!isEQListOfString("bizLocation", objToSave, query))
				return false;

			if (!isEQListOfString("transformationID", objToSave, query))
				return false;

			// TODO: Trigger Engine will be implemented

		}

		return true;
	}

	public static void sendQueryResults(URI uri, Object result, org.w3c.dom.Document retDoc, Element envelope, Element body,
			Class<?> resultType) {
		try {
			if (retDoc == null) {
				retDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
				envelope = retDoc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Envelope");
				body = retDoc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Body");
			}

			JAXBContext jc = JAXBContext.newInstance(resultType);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.marshal(result, body);

			envelope.appendChild(body);
			envelope.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			retDoc.appendChild(envelope);
			retDoc.normalize();
			String resultString = XMLUtil.toString(retDoc);
			SOAPSubscribeServer.clientForSubscriptionCallback.post(uri.toString()).sendBuffer(Buffer.buffer(resultString),
					ar -> {
						if (ar.succeeded()) {
							SOAPSubscribeServer.logger.debug("Subscription result sent to " + uri.toString());
						} else {
							SOAPSubscribeServer.logger
									.debug("Subscription result delivery failed: " + ar.result().statusMessage());
						}
					});
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
}
