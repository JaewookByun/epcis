package org.oliot.epcis.query;

import java.io.IOException;
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
import org.oliot.epcis.converter.xml.read.XMLMasterDataReadConverter;
import org.oliot.epcis.converter.xml.read.XMLObjectEventReadConverter;
import org.oliot.epcis.converter.xml.read.XMLTransactionEventReadConverter;
import org.oliot.epcis.converter.xml.read.XMLTransformationEventReadConverter;
import org.oliot.epcis.model.*;
import org.oliot.epcis.model.exception.ImplementationException;
import org.oliot.epcis.model.exception.QueryParameterException;
import org.oliot.epcis.model.exception.QueryTooLargeException;
import org.oliot.epcis.model.exception.SubscribeNotPermittedException;
import org.oliot.epcis.model.exception.ValidationException;
import org.oliot.epcis.util.ObservableSubscriber;
import org.oliot.epcis.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.mongodb.reactivestreams.client.FindPublisher;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

/**
 * Copyright (C) 2020-2021. (Jaewook Byun) all rights reserved.
 * <p>
 * Oliot EPCIS X is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * Among various modules, epcis-query-soap acts as a server to receive queries
 * to provide filtered, sorted, limited events or masterdata of interest inside EPCIS
 * repository.
 * <p>
 * 
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr
 *         <p>
 *         Associate Director, Auto-ID Labs, KAIST, bjw0829@kaist.ac.kr
 */
public class SOAPQueryService {

	

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
			SOAPQueryServer.monitoring(request, "poll");
			poll(response, doc);
			return;
		}

		Node getStandardVersion = doc.getElementsByTagNameNS("urn:epcglobal:epcis-query:xsd:1", "GetStandardVersion")
				.item(0);
		if (getStandardVersion != null) {
			SOAPQueryServer.monitoring(request, "getStandardVersion");
			getStandardVersion(response);
			return;
		}
		Node getVendorVersion = doc.getElementsByTagNameNS("urn:epcglobal:epcis-query:xsd:1", "GetVendorVersion")
				.item(0);
		if (getVendorVersion != null) {
			SOAPQueryServer.monitoring(request, "getVendorVersion");
			getVendorVersion(response);
			return;
		}

		Node getQueryNames = doc.getElementsByTagNameNS("urn:epcglobal:epcis-query:xsd:1", "GetQueryNames").item(0);
		if (getQueryNames != null) {
			SOAPQueryServer.monitoring(request, "getQueryNames");
			getQueryNames(response);
			return;
		}
		Node getSubscriptionIDs = doc.getElementsByTagNameNS("urn:epcglobal:epcis-query:xsd:1", "GetSubscriptionIDs")
				.item(0);
		if (getSubscriptionIDs != null) {
			ImplementationException e = new ImplementationException();
			e.setReason("GetSubscriptionIDs is not supported in SOAPQueryServer; use SOAPSubscriptionServer instead");
			e.setStackTrace(new StackTraceElement[0]);
			sendQueryResults(response, e, null, null, null, e.getClass());
			return;
		}
		Node getSubscribe = doc.getElementsByTagNameNS("urn:epcglobal:epcis-query:xsd:1", "Subscribe").item(0);
		if (getSubscribe != null) {
			SubscribeNotPermittedException e = new SubscribeNotPermittedException();
			e.setReason("Subscribe is not supported in SOAPQueryServer; use SOAPSubscriptionServer instead");
			e.setStackTrace(new StackTraceElement[0]);
			sendQueryResults(response, e, null, null, null, e.getClass());
			return;
		}
		Node unsubscribe = doc.getElementsByTagNameNS("urn:epcglobal:epcis-query:xsd:1", "Unsubscribe").item(0);
		if (unsubscribe != null) {
			ImplementationException e = new ImplementationException();
			e.setReason("Unsubscribe is not supported in SOAPQueryServer; use SOAPSubscriptionServer instead");
			e.setStackTrace(new StackTraceElement[0]);
			sendQueryResults(response, e, null, null, null, e.getClass());
		}

	}

	// void subscribe throws NoSuchNameException, InvalidURIException,
	// DuplicateSubscriptionException, QueryParameterException,
	// QueryTooComplexException, SubscriptionControlsException,
	// SubscribeNotPermittedException, SecurityException, ValidationException,
	// ImplementationException;

	// void unsubscribe throws NoSuchSubscriptionException, ValidationException,
	// ImplementationException;

	// void getSubscriptionIDs throws NoSuchNameException, SecurityException,
	// ValidationException, ImplementationException;
	public void poll(HttpServerResponse serverResponse, Document doc) {

		try {
			Document retDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element envelope = retDoc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Envelope");
			envelope.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");

			Element body = retDoc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Body");

			/*
			 * <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
			 * <soap:Body> <ns3:QueryResults xmlns:ns4="urn:epcglobal:epcis:xsd:1"
			 * xmlns:ns3="urn:epcglobal:epcis-query:xsd:1" xmlns:ns2=
			 * "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader">
			 * <queryName>SimpleEventQuery</queryName> <resultsBody> <EventList> ...
			 */
			QueryDescription qd;
			try {
				qd = new QueryDescription(doc);
			} catch (ImplementationException e2) {
				ImplementationException e = new ImplementationException();
				e.setReason(e2.getMessage());
				e.setStackTrace(new StackTraceElement[0]);
				sendQueryResults(serverResponse, e, null, null, null, e.getClass());
				return;
			}

			// SimpleEventQuery
			if (qd.getQueryName().equals("SimpleEventQuery")) {

				FindPublisher<org.bson.Document> query = SOAPQueryServer.mEventCollection.find(qd.getMongoQuery());
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
					sendQueryResults(serverResponse, e, null, null, null, e.getClass());
					return;
				}

				List<org.bson.Document> resultList = collector.getReceived();

				if (qd.getMaxCount() != null && (resultList.size() > qd.getMaxCount())) {
					QueryTooLargeException e = new QueryTooLargeException();
					e.setReason(
							"An attempt to execute a query resulted in more data than the service was willing to provide. ( result size: "
									+ resultList.size() + " )");
					e.setStackTrace(new StackTraceElement[0]);
					sendQueryResults(serverResponse, e, null, null, null, e.getClass());
					return;
				}

				List<Object> convertedResultList = getConvertedResultList(qd, resultList, retDoc, envelope);

				QueryResults queryResults = new QueryResults();
				queryResults.setQueryName("SimpleEventQuery");

				QueryResultsBody resultsBody = new QueryResultsBody();

				EventListType elt = new EventListType();
				elt.setObjectEventOrAggregationEventOrTransformationEvent(convertedResultList);
				resultsBody.setEventList(elt);
				queryResults.setResultsBody(resultsBody);

				sendQueryResults(serverResponse, queryResults, retDoc, envelope, body, QueryResults.class);

			} else if (qd.getQueryName().equals("SimpleMasterDataQuery")) {

				FindPublisher<org.bson.Document> query = SOAPQueryServer.mVocCollection.find(qd.getMongoQuery());
				if (!qd.getMongoProjection().isEmpty())
					query.projection(qd.getMongoProjection());

				ObservableSubscriber<org.bson.Document> collector = new ObservableSubscriber<org.bson.Document>();
				query.subscribe(collector);
				try {
					collector.await();
				} catch (Throwable e1) {
					ImplementationException e = new ImplementationException();
					e.setReason(e1.getMessage());
					e.setStackTrace(new StackTraceElement[0]);
					sendQueryResults(serverResponse, e, null, null, null, e.getClass());
					return;
				}

				List<org.bson.Document> resultList = collector.getReceived();

				if (qd.getMaxCount() != null && (resultList.size() > qd.getMaxCount())) {
					QueryTooLargeException e = new QueryTooLargeException();
					e.setReason(
							"An attempt to execute a query resulted in more data than the service was willing to provide. ( result size: "
									+ resultList.size() + " )");
					e.setStackTrace(new StackTraceElement[0]);
					sendQueryResults(serverResponse, e, null, null, null, e.getClass());
					return;
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

				List<VocabularyType> vList = resultList.parallelStream().map(TypeDocument::new)
						.collect(Collectors.groupingBy(TypeDocument::getType,
								Collectors.mapping(TypeDocument::getDocument, Collectors.toSet())))
						.entrySet().parallelStream()
						.map(e -> new XMLMasterDataReadConverter().convert(e.getKey(), e.getValue(), retDoc, envelope))
						.collect(Collectors.toList());

				QueryResults queryResults = new QueryResults();
				queryResults.setQueryName("SimpleMasterDataQuery");

				QueryResultsBody resultsBody = new QueryResultsBody();
				VocabularyListType vlt = new VocabularyListType();
				vlt.setVocabulary(vList);
				resultsBody.setVocabularyList(vlt);

				queryResults.setResultsBody(resultsBody);

				sendQueryResults(serverResponse, queryResults, retDoc, envelope, body, QueryResults.class);

			}
		} catch (QueryParameterException | IllegalStateException e) {
			sendQueryResults(serverResponse, e, null, null, null, e.getClass());
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		}
		// QueryParameterException, QueryTooLargeException, QueryTooComplexException,
		// NoSuchNameException, SecurityException, ValidationException,
		// ImplementationException
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
			serverResponse.putHeader("content-type", "application/xml; charset=utf-8").setStatusCode(200)
					.end(XMLUtil.toString(retDoc));
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
			serverResponse.putHeader("content-type", "application/xml; charset=utf-8").end(XMLUtil.toString(retDoc));
		} catch (ParserConfigurationException | TransformerException e) {
			ImplementationException err = new ImplementationException();
			err.setReason(e.getMessage());
			sendQueryResults(serverResponse, err, null, null, null, err.getClass());
			SOAPQueryServer.logger.error(e.getMessage());
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
			serverResponse.putHeader("content-type", "application/xml; charset=utf-8").end(XMLUtil.toString(retDoc));
		} catch (ParserConfigurationException | TransformerException e) {
			ImplementationException err = new ImplementationException();
			err.setReason(e.getMessage());
			sendQueryResults(serverResponse, err, null, null, null, err.getClass());
			SOAPQueryServer.logger.error(e.getMessage());
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
			serverResponse.putHeader("content-type", "application/xml; charset=utf-8").end(XMLUtil.toString(retDoc));
		} catch (ParserConfigurationException | TransformerException e) {
			ImplementationException err = new ImplementationException();
			err.setReason(e.getMessage());
			sendQueryResults(serverResponse, err, null, null, null, err.getClass());
			SOAPQueryServer.logger.error(e.getMessage());
		}

		// public List<String> getQueryNames() throws SecurityException,
		// ValidationException, ImplementationException;
	}
}
