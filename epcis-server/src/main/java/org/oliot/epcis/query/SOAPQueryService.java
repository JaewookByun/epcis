package org.oliot.epcis.query;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.oliot.epcis.common.Metadata;
import org.oliot.epcis.converter.data.bson_to_pojo.AggregationEventConverter;
import org.oliot.epcis.converter.data.bson_to_pojo.AssociationEventConverter;
import org.oliot.epcis.converter.data.bson_to_pojo.MasterdataConverter;
import org.oliot.epcis.converter.data.bson_to_pojo.ObjectEventConverter;
import org.oliot.epcis.converter.data.bson_to_pojo.TransactionEventConverter;
import org.oliot.epcis.converter.data.bson_to_pojo.TransformationEventConverter;
import org.oliot.epcis.model.*;
import org.oliot.epcis.pagination.Page;
import org.oliot.epcis.pagination.PageExpiryTimerTask;
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

import com.mongodb.client.FindIterable;
import com.mongodb.client.result.InsertOneResult;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

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
public class SOAPQueryService {

	public final static SOAPQueryUnmarshaller soapQueryUnmarshaller = new SOAPQueryUnmarshaller();

	public void pollEventsOrVocabularies(HttpServerRequest request, HttpServerResponse response, String soapMessage, String key, String value)
			throws ValidationException {
		SOAPMessage message = new SOAPMessage();
		Document doc;
		try {
			doc = createDocument(soapMessage);
		} catch (ValidationException e) {
			EPCISServer.logger.error(e.getReason());
			HTTPUtil.sendQueryResults(response, message, e, e.getClass(), 400);
			return;
		}

		Node pollNode = doc.getElementsByTagNameNS("urn:epcglobal:epcis-query:xsd:2", "Poll").item(0);
		if (pollNode != null) {
			try {
				Poll poll = soapQueryUnmarshaller.getPoll(pollNode);
				ArrayOfString aos = new ArrayOfString();
				List<String> list = aos.getString();
				list.add(value);
				poll.setParams(new QueryParams(key, aos));
				poll(request, response, soapQueryUnmarshaller.getPoll(pollNode));
			} catch (JAXBException e) {
				throw new ValidationException(e.getMessage());
			}
			return;
		} else
			throw new ValidationException("Invalid SOAP message: no Poll");
	}

	public void query(HttpServerRequest request, HttpServerResponse response, String soapMessage) {

		SOAPMessage message = new SOAPMessage();
		Document doc;
		try {
			doc = createDocument(soapMessage);
		} catch (ValidationException e) {
			EPCISServer.logger.error(e.getReason());
			HTTPUtil.sendQueryResults(response, message, e, e.getClass(), 400);
			return;
		}

		Node poll = doc.getElementsByTagNameNS("urn:epcglobal:epcis-query:xsd:2", "Poll").item(0);
		if (poll != null) {
			try {
				poll(request, response, soapQueryUnmarshaller.getPoll(poll));
			} catch (JAXBException e) {
				EPCISServer.logger.error(e.getMessage());
				ValidationException e1 = new ValidationException(e.getMessage());
				HTTPUtil.sendQueryResults(response, message, e1, e1.getClass(), 400);
			}
			return;
		}

		Node getSubscribe = doc.getElementsByTagNameNS("urn:epcglobal:epcis-query:xsd:1", "Subscribe").item(0);
		if (getSubscribe != null) {
			try {
				subscribe(response, soapQueryUnmarshaller.getSubscription(getSubscribe));
			} catch (JAXBException e) {
				EPCISServer.logger.error(e.getMessage());
				ValidationException e1 = new ValidationException(e.getMessage());
				HTTPUtil.sendQueryResults(response, message, e1, e1.getClass(), 400);
			}
			return;
		}

		Node getSubscriptionIDs = doc.getElementsByTagNameNS("urn:epcglobal:epcis-query:xsd:1", "GetSubscriptionIDs")
				.item(0);
		if (getSubscriptionIDs != null) {
			getSubscriptionIDs(response);
			return;
		}
		Node unsubscribe = doc.getElementsByTagNameNS("urn:epcglobal:epcis-query:xsd:1", "Unsubscribe").item(0);
		if (unsubscribe != null) {
			unsubscribe(response, doc);
		}

		Node getStandardVersion = doc.getElementsByTagNameNS("urn:epcglobal:epcis-query:xsd:2", "GetStandardVersion")
				.item(0);
		if (getStandardVersion != null) {
			sendQueryResults(response, EPCISServer.getStandardVersionResponse);
			return;
		}
		Node getVendorVersion = doc.getElementsByTagNameNS("urn:epcglobal:epcis-query:xsd:2", "GetVendorVersion")
				.item(0);
		if (getVendorVersion != null) {
			sendQueryResults(response, EPCISServer.getVendorVersionResponse);
			return;
		}

		Node getQueryNames = doc.getElementsByTagNameNS("urn:epcglobal:epcis-query:xsd:2", "GetQueryNames").item(0);
		if (getQueryNames != null) {
			sendQueryResults(response, EPCISServer.getQueryNamesResponse);
			return;
		}
		EPCISServer.logger.error("Unsupported SOAP operation");
		ValidationException e = new ValidationException("Unsupported SOAP operation");
		HTTPUtil.sendQueryResults(response, message, e, e.getClass(), 400);
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

	private void invokeSimpleEventQuery(HttpServerResponse serverResponse, SOAPMessage message, QueryDescription qd,
			int perPage) {

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
			ImplementationException e = new ImplementationException(ImplementationExceptionSeverity.ERROR, "Poll",
					e1.getMessage());
			EPCISServer.logger.error(e.getReason());
			HTTPUtil.sendQueryResults(serverResponse, message, e, e.getClass(), 500);
			return;
		}

		if (qd.getMaxCount() != null && (resultList.size() > qd.getMaxCount())) {
			QueryTooLargeException e = new QueryTooLargeException(
					"An attempt to execute a query resulted in more data than the service was willing to provide. ( result size: "
							+ resultList.size() + " )");
			EPCISServer.logger.error(e.getReason());
			HTTPUtil.sendQueryResults(serverResponse, message, e, e.getClass(), 413);
			return;
		}

		List<Object> convertedResultList = getConvertedResultList(isResultSorted(qd), resultList, message);

		if (needPagination == true && perPage >= convertedResultList.size()) {
			needPagination = false;
		} else if (needPagination == true && perPage < convertedResultList.size()) {
			if (!convertedResultList.isEmpty())
				convertedResultList = convertedResultList.subList(0, perPage);
		}

		QueryResults queryResults = new QueryResults();
		queryResults.setQueryName("SimpleEventQuery");

		QueryResultsBody resultsBody = new QueryResultsBody();

		EventListType elt = new EventListType();
		elt.setObjectEventOrAggregationEventOrTransformationEvent(convertedResultList);
		resultsBody.setEventList(elt);
		queryResults.setResultsBody(resultsBody);

		if (needPagination) {
			UUID uuid;
			long currentTime = System.currentTimeMillis();

			while (true) {
				uuid = UUID.randomUUID();
				if (!EPCISServer.eventPageMap.containsKey(uuid))
					break;
			}

			Page page = new Page(uuid, "SimpleEventQuery", qd.getMongoQuery(), null, qd.getMongoSort(),
					qd.getEventCountLimit(), perPage);
			Timer timer = new Timer();
			page.setTimer(timer);
			timer.schedule(new PageExpiryTimerTask("GET /events", EPCISServer.eventPageMap, uuid, EPCISServer.logger),
					Metadata.GS1_Next_Page_Token_Expires);
			EPCISServer.eventPageMap.put(uuid, page);
			EPCISServer.logger.debug(
					"[GET /events] page - " + uuid + " added. # remaining pages - " + EPCISServer.eventPageMap.size());

			serverResponse.putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-Extension", Metadata.GS1_Extensions).putHeader("Link", uuid.toString())
					.putHeader("GS1-Next-Page-Token-Expires",
							TimeUtil.getDateTimeStamp(currentTime + Metadata.GS1_Next_Page_Token_Expires));
			HTTPUtil.sendQueryResults(serverResponse, message, queryResults, QueryResults.class, 200);
		} else {
			HTTPUtil.sendQueryResults(serverResponse, message, queryResults, QueryResults.class, 200);
		}
	}

	private void invokeSimpleMasterDataQuery(HttpServerResponse serverResponse, SOAPMessage message,
			QueryDescription qd, int perPage) {

		FindIterable<org.bson.Document> query = EPCISServer.mVocCollection.find(qd.getMongoQuery());
		if (!qd.getMongoProjection().isEmpty())
			query.projection(qd.getMongoProjection());

		List<org.bson.Document> resultList = new ArrayList<org.bson.Document>();
		try {
			query.into(resultList);
		} catch (Throwable e1) {
			ImplementationException e = new ImplementationException(ImplementationExceptionSeverity.ERROR, "Poll",
					e1.getMessage());
			HTTPUtil.sendQueryResults(serverResponse, message, e, e.getClass(), 500);
			return;
		}

		if (qd.getMaxCount() != null && (resultList.size() > qd.getMaxCount())) {
			QueryTooLargeException e = new QueryTooLargeException(
					"An attempt to execute a query resulted in more data than the service was willing to provide. ( result size: "
							+ resultList.size() + " )");
			HTTPUtil.sendQueryResults(serverResponse, message, e, e.getClass(), 413);
			return;
		}

		List<VocabularyType> vList = resultList.parallelStream().map(TypeDocument::new)
				.collect(Collectors.groupingBy(TypeDocument::getType,
						Collectors.mapping(TypeDocument::getDocument, Collectors.toSet())))
				.entrySet().parallelStream()
				.map(e -> new MasterdataConverter().convert(e.getKey(), e.getValue(), message))
				.collect(Collectors.toList());

		boolean needPagination = false;
		if (perPage < vList.size()) {
			needPagination = true;
			vList = vList.subList(0, perPage);
		}

		QueryResults queryResults = new QueryResults();
		queryResults.setQueryName("SimpleMasterDataQuery");

		QueryResultsBody resultsBody = new QueryResultsBody();
		VocabularyListType vlt = new VocabularyListType();
		vlt.setVocabulary(vList);
		resultsBody.setVocabularyList(vlt);

		queryResults.setResultsBody(resultsBody);

		if (needPagination) {
			UUID uuid;
			long currentTime = System.currentTimeMillis();

			while (true) {
				uuid = UUID.randomUUID();
				if (!EPCISServer.vocabularyPageMap.containsKey(uuid))
					break;
			}

			Page page = new Page(uuid, "SimpleMasterDataQuery", qd.getMongoQuery(), qd.getMongoProjection(), null, null,
					perPage);

			Timer timer = new Timer();
			page.setTimer(timer);
			timer.schedule(new PageExpiryTimerTask("GET /vocabularies", EPCISServer.vocabularyPageMap, uuid,
					EPCISServer.logger), Metadata.GS1_Next_Page_Token_Expires);
			EPCISServer.vocabularyPageMap.put(uuid, page);
			EPCISServer.logger.debug("[GET /vocabularies] page - " + uuid + " added. # remaining pages - "
					+ EPCISServer.vocabularyPageMap.size());

			serverResponse.putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-Extension", Metadata.GS1_Extensions).putHeader("Link", uuid.toString())
					.putHeader("GS1-Next-Page-Token-Expires",
							TimeUtil.getDateTimeStamp(currentTime + Metadata.GS1_Next_Page_Token_Expires));
			HTTPUtil.sendQueryResults(serverResponse, message, queryResults, QueryResults.class, 200);
		} else {
			HTTPUtil.sendQueryResults(serverResponse, message, queryResults, QueryResults.class, 200);
		}
	}

	public void poll(HttpServerRequest serverRequest, HttpServerResponse serverResponse, Poll poll) {

		// create SOAP message to return
		SOAPMessage message = new SOAPMessage();

		// get perPage
		int perPage;

		try {
			perPage = getPerPage(serverRequest);
		} catch (QueryParameterException e) {
			HTTPUtil.sendQueryResults(serverResponse, message, e, e.getClass(), 400);
			return;
		}

		// create query description
		QueryDescription qd = null;
		try {
			qd = new QueryDescription(poll, soapQueryUnmarshaller);
		} catch (QueryParameterException e1) {
			EPCISServer.logger.error(e1.getReason());
			HTTPUtil.sendQueryResults(serverResponse, message, e1, e1.getClass(), 400);
			return;
		} catch (ImplementationException e2) {
			EPCISServer.logger.error(e2.getReason());
			HTTPUtil.sendQueryResults(serverResponse, message, e2, e2.getClass(), 500);
			return;
		}

		if (qd.getQueryName().equals("SimpleEventQuery")) {
			// SimpleEventQuery
			invokeSimpleEventQuery(serverResponse, message, qd, perPage);
		} else if (qd.getQueryName().equals("SimpleMasterDataQuery")) {
			// SimpleMasterDataQuery
			invokeSimpleMasterDataQuery(serverResponse, message, qd, perPage);
		} else {
			QueryParameterException e = new QueryParameterException(
					"queryName should be one of SimpleEventQuery and SimpleMasterDataQuery");
			EPCISServer.logger.error(e.getReason());
			HTTPUtil.sendQueryResults(serverResponse, message, e, e.getClass(), 400);
			return;
		}
	}

	public void getNextVocabularyPage(HttpServerRequest serverRequest, HttpServerResponse serverResponse)
			throws ParserConfigurationException {
		SOAPMessage message = new SOAPMessage();
		UUID uuid = null;
		try {
			uuid = UUID.fromString(serverRequest.getParam("nextPageToken"));
		} catch (Exception e) {
			QueryParameterException e1 = new QueryParameterException("nextPageToken should exist - " + uuid);
			HTTPUtil.sendQueryResults(serverResponse, message, e1, e1.getClass(), 400);
		}

		// get perPage
		int perPage;

		try {
			perPage = getPerPage(serverRequest);
		} catch (QueryParameterException e) {
			HTTPUtil.sendQueryResults(serverResponse, message, e, e.getClass(), 400);
			return;
		}

		// get Page
		Page page = null;
		if (!EPCISServer.vocabularyPageMap.containsKey(uuid)) {
			EPCISException e = new EPCISException(
					"[406NotAcceptable] The given next page token does not exist or be no longer available.");
			HTTPUtil.sendQueryResults(serverResponse, message, e, e.getClass(), 406);
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
			ImplementationException e = new ImplementationException(ImplementationExceptionSeverity.ERROR, null, null,
					e1.getMessage());
			HTTPUtil.sendQueryResults(serverResponse, message, e, e.getClass(), 500);
			return;
		}

		List<VocabularyType> vList = resultList.parallelStream().map(TypeDocument::new)
				.collect(Collectors.groupingBy(TypeDocument::getType,
						Collectors.mapping(TypeDocument::getDocument, Collectors.toSet())))
				.entrySet().parallelStream()
				.map(e -> new MasterdataConverter().convert(e.getKey(), e.getValue(), message))
				.collect(Collectors.toList());

		boolean needPagination = false;
		if (perPage < vList.size()) {
			needPagination = true;
			vList = vList.subList(0, perPage);
		}

		QueryResults queryResults = new QueryResults();
		queryResults.setQueryName("SimpleMasterDataQuery");

		QueryResultsBody resultsBody = new QueryResultsBody();
		VocabularyListType vlt = new VocabularyListType();
		vlt.setVocabulary(vList);
		resultsBody.setVocabularyList(vlt);

		queryResults.setResultsBody(resultsBody);

		if (needPagination) {
			page.incrSkip(perPage);
			long currentTime = System.currentTimeMillis();
			Timer timer = page.getTimer();
			if (timer != null)
				timer.cancel();

			Timer newTimer = new Timer();
			page.setTimer(newTimer);
			newTimer.schedule(new PageExpiryTimerTask("GET /vocabularies", EPCISServer.vocabularyPageMap, uuid,
					EPCISServer.logger), Metadata.GS1_Next_Page_Token_Expires);
			EPCISServer.logger.debug("[GET /vocabularies] page - " + uuid + " token expiry time extended to "
					+ TimeUtil.getDateTimeStamp(currentTime + Metadata.GS1_Next_Page_Token_Expires));

			serverResponse.putHeader("Link", uuid.toString()).putHeader("GS1-Next-Page-Token-Expires",
					TimeUtil.getDateTimeStamp(currentTime + Metadata.GS1_Next_Page_Token_Expires));
			HTTPUtil.sendQueryResults(serverResponse, message, queryResults, QueryResults.class, 200);
		} else {
			EPCISServer.vocabularyPageMap.remove(uuid);
			EPCISServer.logger.debug("[GET /vocabularies] page - " + uuid + " expired. # remaining pages - "
					+ EPCISServer.vocabularyPageMap.size());
			HTTPUtil.sendQueryResults(serverResponse, message, queryResults, QueryResults.class, 200);
		}
	}

	public void getNextEventPage(HttpServerRequest serverRequest, HttpServerResponse serverResponse) {
		SOAPMessage message = new SOAPMessage();

		// get UUID
		UUID uuid = null;
		try {
			uuid = UUID.fromString(serverRequest.getParam("NextPageToken"));
		} catch (Exception e) {
			QueryParameterException e1 = new QueryParameterException("invalid nextPageToken - " + uuid);
			EPCISServer.logger.error(e1.getReason());
			HTTPUtil.sendQueryResults(serverResponse, message, e1, e1.getClass(), 400);
			return;
		}

		// get perPage
		int perPage;

		try {
			perPage = getPerPage(serverRequest);
		} catch (QueryParameterException e) {
			EPCISServer.logger.error(e.getReason());
			HTTPUtil.sendQueryResults(serverResponse, message, e, e.getClass(), 400);
			return;
		}

		Page page = null;
		if (!EPCISServer.eventPageMap.containsKey(uuid)) {
			EPCISException e = new EPCISException(
					"[406NotAcceptable] The given next page token does not exist or be no longer available.");
			EPCISServer.logger.error(e.getReason());
			HTTPUtil.sendQueryResults(serverResponse, message, e, e.getClass(), 406);
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
			ImplementationException e1 = new ImplementationException(ImplementationExceptionSeverity.ERROR, "Poll",
					e.getMessage());
			EPCISServer.logger.error(e1.getReason());
			HTTPUtil.sendQueryResults(serverResponse, message, e1, e1.getClass(), 500);
			return;
		}

		List<Object> convertedResultList = getConvertedResultList(isResultSorted(sort), results, message);

		if (needPagination == true && perPage >= convertedResultList.size()) {
			needPagination = false;
		} else if (needPagination == true && perPage < convertedResultList.size()) {
			convertedResultList = convertedResultList.subList(0, perPage);
		}

		QueryResults queryResults = new QueryResults();
		queryResults.setQueryName("SimpleEventQuery");

		QueryResultsBody resultsBody = new QueryResultsBody();

		EventListType elt = new EventListType();
		elt.setObjectEventOrAggregationEventOrTransformationEvent(convertedResultList);
		resultsBody.setEventList(elt);
		queryResults.setResultsBody(resultsBody);

		if (perPage < results.size()) {
			long currentTime = System.currentTimeMillis();
			Timer timer = page.getTimer();
			if (timer != null)
				timer.cancel();

			Timer newTimer = new Timer();
			page.setTimer(newTimer);
			newTimer.schedule(
					new PageExpiryTimerTask("GET /events", EPCISServer.eventPageMap, uuid, EPCISServer.logger),
					Metadata.GS1_Next_Page_Token_Expires);
			EPCISServer.logger.debug("[GET /events] page - " + uuid + " token expiry time extended to "
					+ TimeUtil.getDateTimeStamp(currentTime + Metadata.GS1_Next_Page_Token_Expires));

			serverResponse.putHeader("Link", uuid.toString()).putHeader("GS1-Next-Page-Token-Expires",
					TimeUtil.getDateTimeStamp(currentTime + Metadata.GS1_Next_Page_Token_Expires));
			HTTPUtil.sendQueryResults(serverResponse, message, queryResults, QueryResults.class, 200);
		} else {
			EPCISServer.eventPageMap.remove(uuid);
			EPCISServer.logger.debug("[GET /events] page - " + uuid + " expired. # remaining pages - "
					+ EPCISServer.eventPageMap.size());
			HTTPUtil.sendQueryResults(serverResponse, message, queryResults, QueryResults.class, 200);
		}
	}

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

	public void subscribe(HttpServerResponse serverResponse, Subscribe subscribe) {

		SOAPMessage message = new SOAPMessage();

		Subscription subscription = null;
		try {
			subscription = new Subscription(subscribe, soapQueryUnmarshaller);
		} catch (ImplementationException e) {
			HTTPUtil.sendQueryResults(serverResponse, message, e, e.getClass(), 500);
			return;
		} catch (InvalidURIException | SubscriptionControlsException | QueryParameterException
				| SubscribeNotPermittedException e) {
			HTTPUtil.sendQueryResults(serverResponse, message, e, e.getClass(), 400);
			return;
		}

		// Existing subscription Check
		org.bson.Document result = null;
		try {
			result = EPCISServer.mSubscriptionCollection
					.find(new org.bson.Document().append("_id", subscription.getSubscriptionID())).first();
		} catch (Throwable e2) {
			ImplementationException e = new ImplementationException(ImplementationExceptionSeverity.ERROR, null, null,
					e2.getMessage());
			HTTPUtil.sendQueryResults(serverResponse, message, e, e.getClass(), 500);
		}

		if (result != null) {
			DuplicateSubscriptionException e = new DuplicateSubscriptionException(
					"The specified subscriptionID is identical to a previous subscription that was created and not yet unsubscribed.: "
							+ subscription.getSubscriptionID());
			HTTPUtil.sendQueryResults(serverResponse, message, e, e.getClass(), 400);
			return;
		}

		// cron Example
		// 0/10 * * * * ? : every 10 second
		String schedule = subscription.getSchedule();
		@SuppressWarnings("unused")
		URI trigger = subscription.getTrigger();
		if (schedule != null) {
			try {
				cronSchedule(schedule);
				addScheduleToQuartz(subscription);
				addScheduleToDB(subscription);
				sendQueryResults(serverResponse, EPCISServer.subscribeResponse);
				return;
			} catch (Throwable e) {
				SubscriptionControlsException e1 = new SubscriptionControlsException(
						"The specified subscription controls was invalid; e.g., the schedule parameters were out of range, the trigger URI could not be parsed or did not name a recognised trigger, etc."
								+ e.getMessage());
				HTTPUtil.sendQueryResults(serverResponse, message, e1, e1.getClass(), 500);
				return;
			}
		} else {
			EPCISServer.triggerEngine.addSubscription(subscription.getTriggerDescription(), subscription.getDest());
			addScheduleToDB(subscription);
			sendQueryResults(serverResponse, EPCISServer.subscribeResponse);
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

	private List<Object> getConvertedResultList(boolean isResultSorted, List<org.bson.Document> resultList,
			SOAPMessage message) {
		Stream<org.bson.Document> resultStream;
		if (isResultSorted == true) {
			resultStream = resultList.stream();
		} else {
			resultStream = resultList.parallelStream();
		}

		ArrayList<String> nsList = new ArrayList<>();

		List<Object> results = resultStream.map(result -> {
			try {
				switch (result.getString("type")) {
				case "AggregationEvent":
					return new AggregationEventConverter().convert(result, message, nsList);

				case "ObjectEvent":
					return new ObjectEventConverter().convert(result, message, nsList);

				case "TransactionEvent":
					return new TransactionEventConverter().convert(result, message, nsList);

				case "TransformationEvent":
					return new TransformationEventConverter().convert(result, message, nsList);

				case "AssociationEvent":
					return new AssociationEventConverter().convert(result, message, nsList);

				default:
					return null;
				}
			} catch (DatatypeConfigurationException e) {
				return null;
			}
		}).filter(Objects::nonNull).collect(Collectors.toList());
		return results;
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
		serverResponse.putHeader("Access-Control-Expose-Headers", "*")
				.putHeader("content-type", "application/xml; charset=utf-8").setStatusCode(200).end(result);
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

			List<Object> convertedResultList = getConvertedResultList(isResultSorted(sort), resultList, message);

			if (reportIfEmpty == false && convertedResultList.size() == 0) {
				EPCISServer.logger.debug("Subscription " + sub + " invoked but not sent due to reportIfEmpty");
				return;
			}

			QueryResults queryResults = new QueryResults();
			queryResults.setQueryName("SimpleEventQuery");
			queryResults.setSubscriptionID(subscriptionID);

			QueryResultsBody resultsBody = new QueryResultsBody();

			EventListType elt = new EventListType();
			elt.setObjectEventOrAggregationEventOrTransformationEvent(convertedResultList);
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
}

class TypeDocument {
	final String type;
	final org.bson.Document object;

	TypeDocument(org.bson.Document obj) {
		type = obj.getString("type");
		object = obj;
	}

	public String getType() {
		return type;
	}

	public org.bson.Document getDocument() {
		return object;
	}
}