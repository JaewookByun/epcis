package org.oliot.epcis.subscribe;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.oliot.epcis.common.Metadata;
import org.oliot.epcis.converter.xml.read.XMLAggregationEventReadConverter;
import org.oliot.epcis.converter.xml.read.XMLAssociationEventReadConverter;
import org.oliot.epcis.converter.xml.read.XMLObjectEventReadConverter;
import org.oliot.epcis.converter.xml.read.XMLTransactionEventReadConverter;
import org.oliot.epcis.converter.xml.read.XMLTransformationEventReadConverter;
import org.oliot.epcis.model.EventListType;
import org.oliot.epcis.model.QueryResults;
import org.oliot.epcis.model.QueryResultsBody;
import org.oliot.epcis.model.exception.DuplicateSubscriptionException;
import org.oliot.epcis.model.exception.ImplementationException;
import org.oliot.epcis.model.exception.NoSuchSubscriptionException;
import org.oliot.epcis.model.exception.QueryParameterException;
import org.oliot.epcis.model.exception.QueryTooLargeException;
import org.oliot.epcis.model.exception.SubscribeNotPermittedException;
import org.oliot.epcis.model.exception.SubscriptionControlsException;
import org.oliot.epcis.model.exception.ValidationException;
import org.oliot.epcis.query.QueryDescription;
import org.oliot.epcis.query.Subscription;
import org.oliot.epcis.util.ObservableSubscriber;
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

import com.mongodb.client.result.InsertOneResult;
import com.mongodb.reactivestreams.client.FindPublisher;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

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
public class SOAPSubscribeService {

	public void run(HttpServerRequest request, HttpServerResponse response, String soapMessage) {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		docFactory.setNamespaceAware(true);
		Document doc;
		try {
			doc = docFactory.newDocumentBuilder().parse(XMLUtil.getXMLDocumentInputStream(soapMessage));
		} catch (SAXException | IOException | ParserConfigurationException e) {
			ValidationException e1 = new ValidationException();
			e1.setStackTrace(new StackTraceElement[0]);
			e1.setReason(
					"The input to the operation was not syntactically valid according to the syntax defined by the binding. Each binding specifies the particular circumstances under which this exception is raised.: "
							+ e.getMessage());

			sendQueryResults(response, e1, null, null, null, e1.getClass());
			return;
		}
		Node poll = doc.getElementsByTagNameNS("urn:epcglobal:epcis-query:xsd:1", "Poll").item(0);
		if (poll != null) {
			ImplementationException e = new ImplementationException();
			e.setReason("Poll is not supported in SOAPSubscribeServer; use SOAPQueryServer instead");
			e.setStackTrace(new StackTraceElement[0]);
			sendQueryResults(response, e, null, null, null, e.getClass());
			return;
		}

		Node getStandardVersion = doc.getElementsByTagNameNS("urn:epcglobal:epcis-query:xsd:1", "GetStandardVersion")
				.item(0);
		if (getStandardVersion != null) {
			SOAPSubscribeServer.monitoring(request, "getStandardVersion");
			getStandardVersion(response);
			return;
		}
		Node getVendorVersion = doc.getElementsByTagNameNS("urn:epcglobal:epcis-query:xsd:1", "GetVendorVersion")
				.item(0);
		if (getVendorVersion != null) {
			SOAPSubscribeServer.monitoring(request, "getVendorVersion");
			getVendorVersion(response);
			return;
		}

		Node getQueryNames = doc.getElementsByTagNameNS("urn:epcglobal:epcis-query:xsd:1", "GetQueryNames").item(0);
		if (getQueryNames != null) {
			SOAPSubscribeServer.monitoring(request, "getQueryNames");
			getQueryNames(response);
			return;
		}
		Node getSubscriptionIDs = doc.getElementsByTagNameNS("urn:epcglobal:epcis-query:xsd:1", "GetSubscriptionIDs")
				.item(0);
		if (getSubscriptionIDs != null) {
			SOAPSubscribeServer.monitoring(request, "getSubscriptionIDs");
			getSubscriptionIDs(response);
			return;
		}
		Node getSubscribe = doc.getElementsByTagNameNS("urn:epcglobal:epcis-query:xsd:1", "Subscribe").item(0);
		if (getSubscribe != null) {
			SOAPSubscribeServer.monitoring(request, "subscribe");
			subscribe(response, doc);
			return;
		}
		Node unsubscribe = doc.getElementsByTagNameNS("urn:epcglobal:epcis-query:xsd:1", "Unsubscribe").item(0);
		if (unsubscribe != null) {
			SOAPSubscribeServer.monitoring(request, "unsubscribe");
			unsubscribe(response, doc);
		}

	}

	public void sendSubscriptionResult(JobExecutionContext context) {

		JobDetail detail = context.getJobDetail();
		JobDataMap map = detail.getJobDataMap();
		Subscription sub = (Subscription) map.get("jobData");
		try {
			Document retDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element envelope = retDoc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Envelope");
			Element body = retDoc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Body");

			QueryDescription qd = sub.getQueryDescription();
			org.bson.Document mongoQuery = qd.getMongoQuery();
			if (sub.getInitialRecordTime() != null) {
				mongoQuery.put("recordTime", new org.bson.Document("$gte", sub.getInitialRecordTime()));
			}

			FindPublisher<org.bson.Document> query = SOAPSubscribeServer.mEventCollection.find(mongoQuery);
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
				sendQueryResults(sub.getDest(), e, null, null, null, e.getClass());
				return;
			}

			List<org.bson.Document> resultList = collector.getReceived();

			if (qd.getMaxCount() != null && (resultList.size() > qd.getMaxCount())) {
				QueryTooLargeException e = new QueryTooLargeException();
				e.setReason(
						"An attempt to execute a query resulted in more data than the service was willing to provide. ( result size: "
								+ resultList.size() + " )");
				e.setStackTrace(new StackTraceElement[0]);
				sendQueryResults(sub.getDest(), e, null, null, null, e.getClass());
				return;
			}

			List<Object> convertedResultList = getConvertedResultList(qd, resultList, retDoc, envelope);

			if (!sub.getReportIfEmpty() && convertedResultList.size() == 0) {
				SOAPSubscribeServer.logger.debug("Subscription invoked but not sent due to reportIfEmpty");
				return;
			}

			QueryResults queryResults = new QueryResults();
			queryResults.setQueryName("SimpleEventQuery");
			queryResults.setSubscriptionID(sub.getSubscriptionID());

			QueryResultsBody resultsBody = new QueryResultsBody();

			EventListType elt = new EventListType();
			elt.setObjectEventOrAggregationEventOrTransformationEvent(convertedResultList);
			resultsBody.setEventList(elt);
			queryResults.setResultsBody(resultsBody);

			sendQueryResults(sub.getDest(), queryResults, retDoc, envelope, body, QueryResults.class);

			// InitialRecordTime limits recordTime
			if (sub.getInitialRecordTime() != null) {
				try {
					long cur = System.currentTimeMillis();
					sub.setInitialRecordTime(cur);
					updateInitialRecordTime(sub.getSubscriptionID(), cur);
					map.put("jobData", sub);
					SubscriptionScheduler.sched.addJob(detail, true, true);
				} catch (SchedulerException e) {
					e.printStackTrace();
				}
			}

		} catch (IllegalStateException e) {
			sendQueryResults(sub.getDest(), e, null, null, null, e.getClass());
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		}
		// QueryParameterException, QueryTooLargeException, QueryTooComplexException,
		// NoSuchNameException, SecurityException, ValidationException,
		// ImplementationException
	}

	public void addScheduleToQuartz(Subscription subscription) {
		try {
			JobDataMap map = new JobDataMap();
			map.put("jobData", subscription);

			JobDetail job = newJob(SubscriptionTask.class)
					.withIdentity(subscription.getSubscriptionID(), subscription.getQueryDescription().getQueryName())
					.setJobData(map).storeDurably(false).build();

			Trigger trigger = newTrigger()
					.withIdentity(subscription.getSubscriptionID(), subscription.getQueryDescription().getQueryName())
					.startNow().withSchedule(cronSchedule(subscription.getSchedule())).build();

			if (!SubscriptionScheduler.sched.isStarted())
				SubscriptionScheduler.sched.start();
			SubscriptionScheduler.sched.scheduleJob(job, trigger);
		} catch (SchedulerException | RuntimeException e) {
			e.printStackTrace();
		}
	}

	public void addScheduleToDB(Subscription subscription) {

		org.bson.Document doc = new org.bson.Document();

		doc.put("_id", subscription.getSubscriptionID());
		if (subscription.getDest() != null)
			doc.put("dest", subscription.getDest().toString());
		else
			doc.put("dest", null);
		doc.put("initialRecordTime", subscription.getInitialRecordTime());
		doc.put("reportIfEmpty", subscription.getReportIfEmpty());
		doc.put("schedule", subscription.getSchedule());
		if (subscription.getTrigger() != null)
			doc.put("trigger", subscription.getTrigger().toString());
		else
			doc.put("trigger", null);

		QueryDescription qd = subscription.getQueryDescription();

		doc.put("eventCountLimit", qd.getEventCountLimit());
		doc.put("maxCount", qd.getMaxCount());
		doc.put("projection", qd.getMongoProjection());
		doc.put("query", qd.getMongoQuery());
		doc.put("sort", qd.getMongoSort());
		doc.put("queryName", qd.getQueryName());
		try {
			ObservableSubscriber<InsertOneResult> subscriber = new ObservableSubscriber<InsertOneResult>();
			SOAPSubscribeServer.mSubscribeCollection.insertOne(doc).subscribe(subscriber);
			subscriber.await();
			SOAPSubscribeServer.logger.debug(subscriber.getReceived());
		} catch (Exception e) {
			SOAPSubscribeServer.logger.error(e.getMessage());
		} catch (Throwable e) {
			SOAPSubscribeServer.logger.error(e.getMessage());
		}
	}

	private void updateInitialRecordTime(String subscriptionID, long initialRecordTime) {
		ObservableSubscriber<org.bson.Document> subscriber = new ObservableSubscriber<org.bson.Document>();
		SOAPSubscribeServer.mSubscribeCollection
				.findOneAndUpdate(new org.bson.Document("_id", subscriptionID),
						new org.bson.Document("$set", new org.bson.Document("initialRecordTime", initialRecordTime)))
				.subscribe(subscriber);
		try {
			subscriber.await();
			SOAPSubscribeServer.logger.debug(subscriber.getReceived());
		} catch (Throwable e) {
			SOAPSubscribeServer.logger.error(e.getMessage());
		}

	}

	public void subscribe(HttpServerResponse serverResponse, Document doc) {
		try {
			Document retDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element envelope = retDoc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Envelope");
			Element body = retDoc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Body");
			Element response = retDoc.createElementNS("urn:epcglobal:epcis-query:xsd:1", "query:SubscribeResult");
			body.appendChild(response);
			envelope.appendChild(body);
			body.appendChild(response);
			retDoc.appendChild(envelope);

			Subscription sub = new Subscription(doc);

			// M46
			if (sub.getQueryDescription().getQueryName().equals("SimpleMasterDataQuery")) {
				SubscribeNotPermittedException e = new SubscribeNotPermittedException();
				e.setStackTrace(new StackTraceElement[0]);
				e.setReason("The specified query name may not be used with subscribe, only with poll.");
				sendQueryResults(serverResponse, e, null, null, null, e.getClass());
				return;
			}

			// Existing subscription Check

			ObservableSubscriber<org.bson.Document> subscriber = new ObservableSubscriber<org.bson.Document>();

			SOAPSubscribeServer.mSubscribeCollection
					.find(new org.bson.Document().append("_id", sub.getSubscriptionID())).first().subscribe(subscriber);

			subscriber.await();

			if (!subscriber.getReceived().isEmpty()) {
				DuplicateSubscriptionException e = new DuplicateSubscriptionException();
				e.setStackTrace(new StackTraceElement[0]);
				e.setReason(
						"The specified subscriptionID is identical to a previous subscription that was created and not yet unsubscribed.");
				sendQueryResults(serverResponse, e, null, null, null, e.getClass());
				return;
			}

			// cron Example
			// 0/10 * * * * ? : every 10 second
			if (sub.getSchedule() != null && sub.getTrigger() == null) {
				try {
					cronSchedule(sub.getSchedule());
					addScheduleToQuartz(sub);
					addScheduleToDB(sub);
					serverResponse.putHeader("content-type", "*/xml; charset=utf-8").setStatusCode(200)
							.end(XMLUtil.toString(retDoc));
				} catch (RuntimeException e) {
					SubscriptionControlsException e1 = new SubscriptionControlsException();
					e1.setStackTrace(new StackTraceElement[0]);
					e1.setReason(
							"The specified subscription controls was invalid; e.g., the schedule parameters were out of range, the trigger URI could not be parsed or did not name a recognised trigger, etc.");
					sendQueryResults(serverResponse, e, null, null, null, e.getClass());
				}
			} else if (sub.getTrigger() != null && sub.getSchedule() == null) {
				TriggerEngine.addTriggerSubscription(sub.getSubscriptionID(), sub);
				addScheduleToDB(sub);
				serverResponse.putHeader("content-type", "*/xml; charset=utf-8").setStatusCode(200)
						.end(XMLUtil.toString(retDoc));
			} else {
				SubscriptionControlsException e = new SubscriptionControlsException();
				e.setStackTrace(new StackTraceElement[0]);
				e.setReason(
						"The specified subscription controls was invalid; e.g., the schedule parameters were out of range, the trigger URI could not be parsed or did not name a recognised trigger, etc.");
				sendQueryResults(serverResponse, e, null, null, null, e.getClass());
			}
		} catch (Throwable e) {
			sendQueryResults(serverResponse, e, null, null, null, e.getClass());
		}
		// QueryParameterException, QueryTooLargeException, QueryTooComplexException,
		// NoSuchNameException, SecurityException, ValidationException,
		// ImplementationException

		// Document retDoc =
		// DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		// Element envelope =
		// retDoc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/",
		// "soapenv:Envelope");

		// Element body =
		// retDoc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/",
		// "soapenv:Body");

		// QueryParameterException, QueryTooLargeException, QueryTooComplexException,
		// NoSuchNameException, SecurityException, ValidationException,
		// ImplementationException
	}

	public void unsubscribe(HttpServerResponse serverResponse, Document doc) {
		try {
			String subscriptionID = null;
			NodeList nodeList = doc.getElementsByTagName("subscriptionID");
			if (nodeList.getLength() == 1) {
				subscriptionID = nodeList.item(0).getTextContent();
			}

			if (subscriptionID == null) {
				QueryParameterException e = new QueryParameterException();
				e.setStackTrace(new StackTraceElement[0]);
				e.setReason("Invalid unsubscribe invocation: missing subscriptionID");
				sendQueryResults(serverResponse, e, null, null, null, e.getClass());
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

			ObservableSubscriber<org.bson.Document> subscriber = new ObservableSubscriber<org.bson.Document>();
			SOAPSubscribeServer.mSubscribeCollection.findOneAndDelete(new org.bson.Document("_id", subscriptionID))
					.subscribe(subscriber);

			subscriber.await();

			List<org.bson.Document> received = subscriber.getReceived();
			if (received.isEmpty()) {
				NoSuchSubscriptionException err = new NoSuchSubscriptionException();
				err.setStackTrace(new StackTraceElement[0]);
				err.setReason("No such subscription exception");
				sendQueryResults(serverResponse, err, null, null, null, err.getClass());
				SOAPSubscribeServer.logger.error("No such subscription exception");
				return;
			} else {
				org.bson.Document result = received.get(0);
				try {
					SubscriptionScheduler.sched.unscheduleJob(
							org.quartz.TriggerKey.triggerKey(result.getString("_id"), result.getString("queryName")));
					SubscriptionScheduler.sched.deleteJob(
							org.quartz.JobKey.jobKey(result.getString("_id"), result.getString("queryName")));
					serverResponse.putHeader("content-type", "*/xml; charset=utf-8").setStatusCode(200)
							.end(XMLUtil.toString(retDoc));
				} catch (org.quartz.SchedulerException | TransformerException e) {
					ImplementationException err = new ImplementationException();
					err.setStackTrace(new StackTraceElement[0]);
					err.setReason(e.getMessage());
					sendQueryResults(serverResponse, err, null, null, null, err.getClass());
					SOAPSubscribeServer.logger.error("Implementation Exception: " + e.getMessage());
				}
			}
		} catch (IllegalStateException | ParserConfigurationException e) {
			ImplementationException err = new ImplementationException();
			err.setStackTrace(new StackTraceElement[0]);
			err.setReason(e.getMessage());
			sendQueryResults(serverResponse, err, null, null, null, err.getClass());
		} catch (Throwable e) {
			ImplementationException err = new ImplementationException();
			err.setStackTrace(new StackTraceElement[0]);
			err.setReason(e.getMessage());
			sendQueryResults(serverResponse, err, null, null, null, err.getClass());
		}
	}

	public void getSubscriptionIDs(HttpServerResponse serverResponse) {
		/*
		 * <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
		 * <soap:Body> <ns3:GetQueryNamesResponse xmlns:ns4="urn:epcglobal:epcis:xsd:1"
		 * xmlns:ns3="urn:epcglobal:epcis-query:xsd:1" xmlns:ns2=
		 * "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader">
		 * <item>SimpleEventQuery</item> <item>SimpleMasterDataQuery</item>
		 * </ns3:GetQueryNamesResponse> </soap:Body> </soap:Envelope>
		 */
		try {
			Document retDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element envelope = retDoc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Envelope");
			Element body = retDoc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Body");
			Element response = retDoc.createElementNS("urn:epcglobal:epcis-query:xsd:1",
					"query:GetSubscriptionIDsResult");

			ObservableSubscriber<org.bson.Document> subscriber = new ObservableSubscriber<org.bson.Document>();

			SOAPSubscribeServer.mSubscribeCollection.find(new org.bson.Document())
					.projection(new org.bson.Document("_id", true)).subscribe(subscriber);

			subscriber.await();

			for (org.bson.Document obj : subscriber.getReceived()) {
				Element item = retDoc.createElement("string");
				item.setTextContent(obj.getString("_id"));
				response.appendChild(item);
			}
			envelope.appendChild(body);
			body.appendChild(response);
			retDoc.appendChild(envelope);

			serverResponse.putHeader("content-type", "*/xml; charset=utf-8").end(XMLUtil.toString(retDoc));

		} catch (ParserConfigurationException e) {
			ImplementationException err = new ImplementationException();
			err.setReason(e.getMessage());
			sendQueryResults(serverResponse, err, null, null, null, err.getClass());
			SOAPSubscribeServer.logger.error(e.getMessage());
		} catch (Throwable e) {
			ImplementationException err = new ImplementationException();
			err.setReason(e.getMessage());
			sendQueryResults(serverResponse, err, null, null, null, err.getClass());
			SOAPSubscribeServer.logger.error(e.getMessage());
		}
	}

	public List<Object> getConvertedResultList(QueryDescription qd, List<org.bson.Document> resultList, Document retDoc,
			Element envelope) {
		Stream<org.bson.Document> resultStream;
		if (qd.getMongoSort() == null || qd.getMongoSort().isEmpty()) {
			resultStream = resultList.parallelStream();
		} else {
			resultStream = resultList.stream();
		}

		ArrayList<String> nsList = new ArrayList<>();

		List<Object> results = resultStream.map(result -> {
			try {
				switch (result.getString("type")) {
				case "AggregationEvent":
					return new XMLAggregationEventReadConverter().convert(result, retDoc, envelope, nsList);

				case "ObjectEvent":
					return new XMLObjectEventReadConverter().convert(result, retDoc, envelope, nsList);

				case "TransactionEvent":
					return new XMLTransactionEventReadConverter().convert(result, retDoc, envelope, nsList);

				case "TransformationEvent":
					return new XMLTransformationEventReadConverter().convert(result, retDoc, envelope, nsList);

				case "AssociationEvent":
					return new XMLAssociationEventReadConverter().convert(result, retDoc, envelope, nsList);

				default:
					return null;
				}
			} catch (DatatypeConfigurationException e) {
				return null;
			}
		}).filter(Objects::nonNull).collect(Collectors.toList());
		return results;
	}

	@SuppressWarnings("rawtypes")
	public void sendQueryResults(HttpServerResponse serverResponse, Object result, Document retDoc, Element envelope,
			Element body, Class resultType) {
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
			serverResponse.putHeader("content-type", "*/xml; charset=utf-8").setStatusCode(200)
					.end(XMLUtil.toString(retDoc));
		} catch (JAXBException | ParserConfigurationException | TransformerException e) {
			e.printStackTrace();
		}
	}

	public void sendQueryResults(URI uri, Object result, Document retDoc, Element envelope, Element body,
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

			SOAPSubscribeServer.clientForSubscriptionCallback.post(uri.getPort(), uri.getHost(), uri.getPath())
					.sendBuffer(Buffer.buffer(resultString)).onSuccess(handler -> {
						SOAPSubscribeServer.logger.debug("Subscription result sent to " + uri.toString());
					}).onFailure(handler -> {
						SOAPSubscribeServer.logger
								.debug("Subscription result delivery failed: " + handler.getMessage());
					});

		} catch (JAXBException | ParserConfigurationException | TransformerException e) {
			e.printStackTrace();
		}
	}

	public void getStandardVersion(HttpServerResponse serverResponse) {
		/*
		 * <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
		 * <soap:Body> <ns3:GetStandardVersionResponse
		 * xmlns:ns4="urn:epcglobal:epcis:xsd:1"
		 * xmlns:ns3="urn:epcglobal:epcis-query:xsd:1" xmlns:ns2=
		 * "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader"> 1.2
		 * </ns3:GetStandardVersionResponse> </soap:Body> </soap:Envelope>
		 */

//		EPCISQueryDocumentType retDoc = new EPCISQueryDocumentType();
//		EPCISQueryBodyType retBody = new EPCISQueryBodyType();
//		retBody.setGetStandardVersionResult("2.0");
//		retDoc.setEPCISBody(retBody);
//		StringWriter sw = new StringWriter();
//		JAXB.marshal(retDoc, sw);
//		

		Document retDoc;
		try {
			retDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element envelope = retDoc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Envelope");
			Element body = retDoc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Body");
			Element response = retDoc.createElementNS("urn:epcglobal:epcis-query:xsd:1",
					"query:GetStandardVersionResult");
			response.setTextContent("2.0");
			envelope.appendChild(body);
			body.appendChild(response);
			retDoc.appendChild(envelope);
			serverResponse.putHeader("content-type", "*/xml; charset=utf-8").end(XMLUtil.toString(retDoc));
		} catch (ParserConfigurationException | TransformerException e) {
			ImplementationException err = new ImplementationException();
			err.setReason(e.getMessage());
			sendQueryResults(serverResponse, err, null, null, null, err.getClass());
			SOAPSubscribeServer.logger.error(e.getMessage());
		}

		// SecurityException, ValidationException, ImplementationException;
	}

	public void getVendorVersion(HttpServerResponse serverResponse) {
		/*
		 * <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
		 * <soap:Body> <ns3:GetVendorVersionResponse
		 * xmlns:ns4="urn:epcglobal:epcis:xsd:1"
		 * xmlns:ns3="urn:epcglobal:epcis-query:xsd:1" xmlns:ns2=
		 * "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader">org.
		 * oliot.epcis-1.2.10</ns3:GetVendorVersionResponse> </soap:Body>
		 * </soap:Envelope>
		 */
		try {
			Document retDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element envelope = retDoc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Envelope");
			Element body = retDoc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Body");
			Element response = retDoc.createElementNS("urn:epcglobal:epcis-query:xsd:1",
					"query:GetVendorVersionResult");
			response.setTextContent(Metadata.vendorVersion);
			envelope.appendChild(body);
			body.appendChild(response);
			retDoc.appendChild(envelope);
			serverResponse.putHeader("content-type", "*/xml; charset=utf-8").end(XMLUtil.toString(retDoc));
		} catch (ParserConfigurationException | TransformerException e) {
			ImplementationException err = new ImplementationException();
			err.setReason(e.getMessage());
			sendQueryResults(serverResponse, err, null, null, null, err.getClass());
			SOAPSubscribeServer.logger.error(e.getMessage());
		}

		// SecurityException, ValidationException, ImplementationException;
	}

	public void getQueryNames(HttpServerResponse serverResponse) {
		/*
		 * <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
		 * <soap:Body> <ns3:GetQueryNamesResponse xmlns:ns4="urn:epcglobal:epcis:xsd:1"
		 * xmlns:ns3="urn:epcglobal:epcis-query:xsd:1" xmlns:ns2=
		 * "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader">
		 * <item>SimpleEventQuery</item> <item>SimpleMasterDataQuery</item>
		 * </ns3:GetQueryNamesResponse> </soap:Body> </soap:Envelope>
		 */

		try {
			Document retDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element envelope = retDoc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Envelope");
			Element body = retDoc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Body");
			Element response = retDoc.createElementNS("urn:epcglobal:epcis-query:xsd:1", "query:GetQueryNamesResult");
			Element item1 = retDoc.createElement("string");
			item1.setTextContent("SimpleEventQuery");
			Element item2 = retDoc.createElement("string");
			item2.setTextContent("SimpleMasterDataQuery");
			response.appendChild(item1);
			response.appendChild(item2);
			envelope.appendChild(body);
			body.appendChild(response);
			retDoc.appendChild(envelope);
			serverResponse.putHeader("content-type", "*/xml; charset=utf-8").end(XMLUtil.toString(retDoc));
		} catch (ParserConfigurationException | TransformerException e) {
			ImplementationException err = new ImplementationException();
			err.setReason(e.getMessage());
			sendQueryResults(serverResponse, err, null, null, null, err.getClass());
			SOAPSubscribeServer.logger.error(e.getMessage());
		}

		// public List<String> getQueryNames() throws SecurityException,
		// ValidationException, ImplementationException;
	}

}
