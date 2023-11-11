package org.oliot.epcis.common;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.oliot.epcis.capture.json.JSONMessageFactory;
import org.oliot.epcis.model.EPCISException;
import org.oliot.epcis.model.GS1CBVXMLFormat;
import org.oliot.epcis.model.GS1EPCFormat;
import org.oliot.epcis.model.ValidationException;
import org.oliot.epcis.server.EPCISServer;
import org.oliot.epcis.tdt.TagDataTranslationEngine;
import org.oliot.epcis.util.HTTPUtil;
import org.oliot.epcis.util.SOAPMessage;

import io.vertx.ext.web.Router;

public class MetadataHandler {
	public static void registerBaseHandler(Router router) {
		/**
		 * Query server settings, EPCIS version(s) and related vocabularies/standards.
		 * // `OPTIONS` on the root path gives the client an overview of the server's //
		 * EPCIS-related configurations. (application/xml)
		 */
		router.options("/epcis").consumes("application/xml").handler(routingContext -> {
			routingContext.response().putHeader("Access-Control-Expose-Headers", "*").putHeader("Allow", "OPTIONS")
					.putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-EPCIS-Min", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-EPCIS-Max", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-CBV-Version", Metadata.GS1_CBV_Version)
					.putHeader("GS1-CBV-Max", Metadata.GS1_CBV_Version)
					.putHeader("GS1-CBV-Min", Metadata.GS1_CBV_Version)
					.putHeader("GS1-Vendor-Version", Metadata.GS1_Vendor_Version)
					.putHeader("GS1-EPC-Format", GS1EPCFormat.Always_EPC_URN.toString())
					.putHeader("GS1-CBV-XML-Format", GS1CBVXMLFormat.Always_URN.toString())
					.putHeader("GS1-Extensions", Metadata.GS1_Extensions).setStatusCode(204).end();
		});
		EPCISServer.logger.info("[OPTIONS /epcis (application/xml)] - router added");

		/**
		 * Query server settings, EPCIS version(s) and related vocabularies/standards.
		 * // `OPTIONS` on the root path gives the client an overview of the server's //
		 * EPCIS-related configurations. (application/json)
		 */
		router.options("/epcis").consumes("application/json").handler(routingContext -> {
			routingContext.response().putHeader("Access-Control-Expose-Headers", "*").putHeader("Allow", "OPTIONS")
					.putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-EPCIS-Min", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-EPCIS-Max", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-CBV-Version", Metadata.GS1_CBV_Version)
					.putHeader("GS1-CBV-Max", Metadata.GS1_CBV_Version)
					.putHeader("GS1-CBV-Min", Metadata.GS1_CBV_Version)
					.putHeader("GS1-Vendor-Version", Metadata.GS1_Vendor_Version)
					.putHeader("GS1-EPC-Format", GS1EPCFormat.Always_GS1_Digital_Link.toString())
					.putHeader("GS1-CBV-XML-Format", GS1CBVXMLFormat.Always_Web_URI.toString())
					.putHeader("GS1-Extensions", Metadata.GS1_Extensions).setStatusCode(204).end();
		});
		EPCISServer.logger.info("[OPTIONS /epcis (application/json)] - router added");
	}

	public static void registerCaptureHandler(Router router) {
		/**
		 * Discover the settings of the capture interface. The `OPTIONS` method is used
		 * as a discovery service for `/capture`. It describes- which EPCIS and CBV
		 * versions are supported,- the EPCIS and CBV extensions,- the maximum payload
		 * size as count of EPCIS events (`GS1-EPCIS-Capture-Limit` header) or as a
		 * maximum payload size in bytes (`GS1-EPCIS-Capture-File-Size-Limit` header)-
		 * what the server will do if an error occurred during capture
		 * (`GS1-Capture-Error-Behaviour` header). The list of headers is not
		 * exhaustive. It only describes the functionality specific to EPCIS 2.0.
		 * (application/xml)
		 */
		router.options("/epcis/capture").consumes("application/xml").handler(routingContext -> {
			routingContext.response().putHeader("Access-Control-Expose-Headers", "*")
					.putHeader("Allow", "OPTIONS, GET, POST").putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-EPCIS-Min", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-EPCIS-Max", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-CBV-Version", Metadata.GS1_CBV_Version)
					.putHeader("GS1-CBV-Max", Metadata.GS1_CBV_Version)
					.putHeader("GS1-CBV-Min", Metadata.GS1_CBV_Version)
					.putHeader("GS1-Vendor-Version", Metadata.GS1_Vendor_Version)
					.putHeader("GS1-EPC-Format", GS1EPCFormat.Always_EPC_URN.toString())
					.putHeader("GS1-CBV-XML-Format", GS1CBVXMLFormat.Always_URN.toString())
					.putHeader("GS1-Extensions", Metadata.GS1_Extensions)
					.putHeader("GS1-EPCIS-Capture-Limit", String.valueOf(Metadata.GS1_CAPTURE_limit))
					.putHeader("GS1-EPCIS-Capture-File-Size-Limit",
							String.valueOf(Metadata.GS1_CAPTURE_file_size_limit))
					.putHeader("GS1-EPCIS-Capture-Error-Behaviour", Metadata.GS1_EPCIS_Capture_Error_Behaviour)
					.setStatusCode(204).end();
		});
		EPCISServer.logger.info("[OPTIONS /epcis/capture (application/xml)] - router added");

		/**
		 * Discover the settings of the capture interface. The `OPTIONS` method is used
		 * as a discovery service for `/capture`. It describes- which EPCIS and CBV
		 * versions are supported,- the EPCIS and CBV extensions,- the maximum payload
		 * size as count of EPCIS events (`GS1-EPCIS-Capture-Limit` header) or as a
		 * maximum payload size in bytes (`GS1-EPCIS-Capture-File-Size-Limit` header)-
		 * what the server will do if an error occurred during capture
		 * (`GS1-Capture-Error-Behaviour` header). The list of headers is not
		 * exhaustive. It only describes the functionality specific to EPCIS 2.0.
		 * (application/json)
		 */
		router.options("/epcis/capture").consumes("application/json").handler(routingContext -> {
			routingContext.response().putHeader("Access-Control-Expose-Headers", "*")
					.putHeader("Allow", "OPTIONS, GET, POST").putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-EPCIS-Min", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-EPCIS-Max", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-CBV-Version", Metadata.GS1_CBV_Version)
					.putHeader("GS1-CBV-Max", Metadata.GS1_CBV_Version)
					.putHeader("GS1-CBV-Min", Metadata.GS1_CBV_Version)
					.putHeader("GS1-Vendor-Version", Metadata.GS1_Vendor_Version)
					.putHeader("GS1-EPC-Format", GS1EPCFormat.Always_GS1_Digital_Link.toString())
					.putHeader("GS1-CBV-XML-Format", GS1CBVXMLFormat.Always_Web_URI.toString())
					.putHeader("GS1-Extensions", Metadata.GS1_Extensions)
					.putHeader("GS1-EPCIS-Capture-Limit", String.valueOf(Metadata.GS1_CAPTURE_limit))
					.putHeader("GS1-EPCIS-Capture-File-Size-Limit",
							String.valueOf(Metadata.GS1_CAPTURE_file_size_limit))
					.putHeader("GS1-EPCIS-Capture-Error-Behaviour", Metadata.GS1_EPCIS_Capture_Error_Behaviour)
					.setStatusCode(204).end();
		});
		EPCISServer.logger.info("[OPTIONS /epcis/capture (application/json)] - router added");
	}

	public static void registerCaptureIDHandler(Router router) {

		/**
		 * Query the metadata of the capture job endpoint. EPCIS 2.0 supports a number
		 * of custom headers to describe custom vocabularies and support multiple
		 * versions of EPCIS and CBV. The `OPTIONS` method allows the client to discover
		 * which vocabularies and EPCIS and CBV versions are used for a given capture
		 * job. (application/xml)
		 */
		router.options("/epcis/capture/:captureID").consumes("application/xml").handler(routingContext -> {
			try {
				SOAPMessage message = new SOAPMessage();
				String captureID = routingContext.pathParam("captureID");
				List<Document> jobs = new ArrayList<Document>();
				EPCISServer.mTxCollection.find(new Document("_id", new ObjectId(captureID))).into(jobs);
				if (jobs.isEmpty()) {
					EPCISException e = new EPCISException("There is no capture job with id: " + captureID);
					HTTPUtil.sendQueryResults(routingContext.response(), message, e, e.getClass(), 404);
					return;
				}
			} catch (IllegalArgumentException e) {
				SOAPMessage message = new SOAPMessage();
				EPCISException e1 = new EPCISException("Illegal capture job identifier: " + e.getMessage());
				HTTPUtil.sendQueryResults(routingContext.response(), message, e1, e1.getClass(), 404);
				return;
			} catch (Throwable throwable) {
				SOAPMessage message = new SOAPMessage();
				EPCISServer.logger.info(throwable.getMessage());
				EPCISException e = new EPCISException(throwable.getMessage());
				HTTPUtil.sendQueryResults(routingContext.response(), message, e, e.getClass(), 500);
				return;
			}
			routingContext.response().putHeader("Access-Control-Expose-Headers", "*").putHeader("Allow", "GET")
					.putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-EPCIS-Min", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-EPCIS-Max", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-CBV-Version", Metadata.GS1_CBV_Version)
					.putHeader("GS1-CBV-Max", Metadata.GS1_CBV_Version)
					.putHeader("GS1-CBV-Min", Metadata.GS1_CBV_Version)
					.putHeader("GS1-Vendor-Version", Metadata.GS1_Vendor_Version)
					.putHeader("GS1-EPC-Format", GS1EPCFormat.Always_EPC_URN.toString())
					.putHeader("GS1-CBV-XML-Format", GS1CBVXMLFormat.Always_URN.toString())
					.putHeader("GS1-Extensions", Metadata.GS1_Extensions).setStatusCode(204).end();
		});
		EPCISServer.logger.info("[OPTIONS /epcis/capture/:captureID (application/xml)] - router added");

		/**
		 * Query the metadata of the capture job endpoint. EPCIS 2.0 supports a number
		 * of custom headers to describe custom vocabularies and support multiple
		 * versions of EPCIS and CBV. The `OPTIONS` method allows the client to discover
		 * which vocabularies and EPCIS and CBV versions are used for a given capture
		 * job. (application/json)
		 */
		router.options("/epcis/capture/:captureID").consumes("application/json").handler(routingContext -> {
			try {
				String captureID = routingContext.pathParam("captureID");
				List<Document> jobs = new ArrayList<Document>();
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
			routingContext.response().putHeader("Access-Control-Expose-Headers", "*").putHeader("Allow", "OPTIONS, GET")
					.putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-EPCIS-Min", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-EPCIS-Max", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-CBV-Version", Metadata.GS1_CBV_Version)
					.putHeader("GS1-CBV-Max", Metadata.GS1_CBV_Version)
					.putHeader("GS1-CBV-Min", Metadata.GS1_CBV_Version)
					.putHeader("GS1-Vendor-Version", Metadata.GS1_Vendor_Version)
					.putHeader("GS1-EPC-Format", GS1EPCFormat.Always_GS1_Digital_Link.toString())
					.putHeader("GS1-CBV-XML-Format", GS1CBVXMLFormat.Always_Web_URI.toString())
					.putHeader("GS1-Extensions", Metadata.GS1_Extensions).setStatusCode(204).end();
		});
		EPCISServer.logger.info("[OPTIONS /epcis/capture/:captureID (application/json)] - router added");
	}

	public static void registerEventsHandler(Router router) {
		/**
		 * Query metadata for the EPCIS events endpoint. EPCIS 2.0 supports a number of
		 * custom headers to describe custom vocabularies and support multiple versions
		 * of EPCIS and CBV. The `OPTIONS` method allows the client to discover which
		 * vocabularies and EPCIS and CBV versions are used. (application/xml)
		 */
		router.options("/epcis/events").consumes("application/xml").handler(routingContext -> {
			routingContext.response().putHeader("Access-Control-Expose-Headers", "*")
					.putHeader("Allow", "OPTIONS, GET, POST").putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-EPCIS-Min", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-EPCIS-Max", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-CBV-Version", Metadata.GS1_CBV_Version)
					.putHeader("GS1-CBV-Max", Metadata.GS1_CBV_Version)
					.putHeader("GS1-CBV-Min", Metadata.GS1_CBV_Version)
					.putHeader("GS1-Vendor-Version", Metadata.GS1_Vendor_Version)
					.putHeader("GS1-EPC-Format", GS1EPCFormat.Always_EPC_URN.toString())
					.putHeader("GS1-CBV-XML-Format", GS1CBVXMLFormat.Always_URN.toString())
					.putHeader("GS1-Extensions", Metadata.GS1_Extensions).setStatusCode(204).end();
		});
		EPCISServer.logger.info("[OPTIONS /epcis/events (application/xml)] - router added");

		/**
		 * Query metadata for the EPCIS events endpoint. EPCIS 2.0 supports a number of
		 * custom headers to describe custom vocabularies and support multiple versions
		 * of EPCIS and CBV. The `OPTIONS` method allows the client to discover which
		 * vocabularies and EPCIS and CBV versions are used. (application/json)
		 */
		router.options("/epcis/events").consumes("application/json").handler(routingContext -> {
			routingContext.response().putHeader("Access-Control-Expose-Headers", "*")
					.putHeader("Allow", "OPTIONS, GET, POST").putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-EPCIS-Min", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-EPCIS-Max", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-CBV-Version", Metadata.GS1_CBV_Version)
					.putHeader("GS1-CBV-Max", Metadata.GS1_CBV_Version)
					.putHeader("GS1-CBV-Min", Metadata.GS1_CBV_Version)
					.putHeader("GS1-Vendor-Version", Metadata.GS1_Vendor_Version)
					.putHeader("GS1-EPC-Format", GS1EPCFormat.Always_GS1_Digital_Link.toString())
					.putHeader("GS1-CBV-XML-Format", GS1CBVXMLFormat.Always_Web_URI.toString())
					.putHeader("GS1-Extensions", Metadata.GS1_Extensions).setStatusCode(204).end();
		});
		EPCISServer.logger.info("[OPTIONS /epcis/events (application/json)] - router added");
	}

	public static void registerVocabualariesHandler(Router router) {
		/**
		 * Query metadata for the EPCIS events endpoint. EPCIS 2.0 supports a number of
		 * custom headers to describe custom vocabularies and support multiple versions
		 * of EPCIS and CBV. The `OPTIONS` method allows the client to discover which
		 * vocabularies and EPCIS and CBV versions are used. (application/xml)
		 */
		router.options("/epcis/vocabularies").consumes("application/xml").handler(routingContext -> {
			routingContext.response().putHeader("Access-Control-Expose-Headers", "*").putHeader("Allow", "OPTIONS, GET")
					.putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-EPCIS-Min", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-EPCIS-Max", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-CBV-Version", Metadata.GS1_CBV_Version)
					.putHeader("GS1-CBV-Max", Metadata.GS1_CBV_Version)
					.putHeader("GS1-CBV-Min", Metadata.GS1_CBV_Version)
					.putHeader("GS1-Vendor-Version", Metadata.GS1_Vendor_Version)
					.putHeader("GS1-EPC-Format", GS1EPCFormat.Always_EPC_URN.toString())
					.putHeader("GS1-CBV-XML-Format", GS1CBVXMLFormat.Always_URN.toString())
					.putHeader("GS1-Extensions", Metadata.GS1_Extensions).setStatusCode(204).end();
		});
		EPCISServer.logger.info("[OPTIONS /epcis/vocabularies (application/xml)] - router added");

		/**
		 * Query metadata for the EPCIS events endpoint. EPCIS 2.0 supports a number of
		 * custom headers to describe custom vocabularies and support multiple versions
		 * of EPCIS and CBV. The `OPTIONS` method allows the client to discover which
		 * vocabularies and EPCIS and CBV versions are used. (application/json)
		 */
		router.options("/epcis/vocabularies").consumes("application/json").handler(routingContext -> {
			routingContext.response().putHeader("Access-Control-Expose-Headers", "*").putHeader("Allow", "OPTIONS, GET")
					.putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-EPCIS-Min", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-EPCIS-Max", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-CBV-Version", Metadata.GS1_CBV_Version)
					.putHeader("GS1-CBV-Max", Metadata.GS1_CBV_Version)
					.putHeader("GS1-CBV-Min", Metadata.GS1_CBV_Version)
					.putHeader("GS1-Vendor-Version", Metadata.GS1_Vendor_Version)
					.putHeader("GS1-EPC-Format", GS1EPCFormat.Always_GS1_Digital_Link.toString())
					.putHeader("GS1-CBV-XML-Format", GS1CBVXMLFormat.Always_Web_URI.toString())
					.putHeader("GS1-Extensions", Metadata.GS1_Extensions).setStatusCode(204).end();
		});
		EPCISServer.logger.info("[OPTIONS /epcis/vocabularies (application/json)] - router added");
	}

	public static void registerGetEventHandler(Router router) {
		/**
		 * Query metadata for the endpoint to access an individual EPCIS event. EPCIS
		 * 2.0 supports a number of custom headers to describe custom vocabularies and
		 * support multiple versions of EPCIS and CBV. The `OPTIONS` method allows the
		 * client to discover which vocabularies and EPCIS and CBV versions are used.
		 * (application/xml)
		 */
		router.options("/epcis/events/:eventID").consumes("application/xml").handler(routingContext -> {
			try {
				SOAPMessage message = new SOAPMessage();
				String eventID = routingContext.pathParam("eventID");
				List<Document> jobs = new ArrayList<Document>();
				EPCISServer.mEventCollection.find(new Document("eventID", eventID)).into(jobs);
				if (jobs.isEmpty()) {
					EPCISException e = new EPCISException("There is no event with the given id: " + eventID);
					HTTPUtil.sendQueryResults(routingContext.response(), message, e, e.getClass(), 404);
					return;
				}
			} catch (IllegalArgumentException e) {
				SOAPMessage message = new SOAPMessage();
				EPCISException e1 = new EPCISException("Illegal event identifier: " + e.getMessage());
				HTTPUtil.sendQueryResults(routingContext.response(), message, e1, e1.getClass(), 404);
				return;
			} catch (Throwable throwable) {
				SOAPMessage message = new SOAPMessage();
				EPCISServer.logger.info(throwable.getMessage());
				EPCISException e = new EPCISException(throwable.getMessage());
				HTTPUtil.sendQueryResults(routingContext.response(), message, e, e.getClass(), 500);
				return;
			}
			routingContext.response().putHeader("Access-Control-Expose-Headers", "*").putHeader("Allow", "OPTIONS, GET")
					.putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-EPCIS-Min", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-EPCIS-Max", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-CBV-Version", Metadata.GS1_CBV_Version)
					.putHeader("GS1-CBV-Max", Metadata.GS1_CBV_Version)
					.putHeader("GS1-CBV-Min", Metadata.GS1_CBV_Version)
					.putHeader("GS1-Vendor-Version", Metadata.GS1_Vendor_Version)
					.putHeader("GS1-EPC-Format", GS1EPCFormat.Always_EPC_URN.toString())
					.putHeader("GS1-CBV-XML-Format", GS1CBVXMLFormat.Always_URN.toString())
					.putHeader("GS1-Extensions", Metadata.GS1_Extensions).setStatusCode(204).end();
		});
		EPCISServer.logger.info("[OPTIONS /epcis/events/:eventID (application/xml)] - router added");

		/**
		 * Query metadata for the endpoint to access an individual EPCIS event. EPCIS
		 * 2.0 supports a number of custom headers to describe custom vocabularies and
		 * support multiple versions of EPCIS and CBV. The `OPTIONS` method allows the
		 * client to discover which vocabularies and EPCIS and CBV versions are used.
		 * (application/json)
		 */
		router.options("/epcis/events/:eventID").consumes("application/json").handler(routingContext -> {
			try {
				String eventID = routingContext.pathParam("eventID");
				List<Document> jobs = new ArrayList<Document>();
				EPCISServer.mEventCollection.find(new Document("eventID", eventID)).into(jobs);
				if (jobs.isEmpty()) {
					HTTPUtil.sendQueryResults(routingContext.response(), JSONMessageFactory
							.get404NoSuchResourceException("There is no event with the given id: " + eventID), 404);
					return;
				}
			} catch (IllegalArgumentException e) {
				HTTPUtil.sendQueryResults(routingContext.response(),
						JSONMessageFactory.get404NoSuchResourceException("Illegal event identifier: " + e.getMessage()),
						404);
				return;
			} catch (Throwable throwable) {
				HTTPUtil.sendQueryResults(routingContext.response(),
						JSONMessageFactory.get500ImplementationException(throwable.getMessage()), 500);
				return;
			}
			routingContext.response().putHeader("Access-Control-Expose-Headers", "*").putHeader("Allow", "OPTIONS, GET")
					.putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-EPCIS-Min", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-EPCIS-Max", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-CBV-Version", Metadata.GS1_CBV_Version)
					.putHeader("GS1-CBV-Max", Metadata.GS1_CBV_Version)
					.putHeader("GS1-CBV-Min", Metadata.GS1_CBV_Version)
					.putHeader("GS1-Vendor-Version", Metadata.GS1_Vendor_Version)
					.putHeader("GS1-EPC-Format", GS1EPCFormat.Always_GS1_Digital_Link.toString())
					.putHeader("GS1-CBV-XML-Format", GS1CBVXMLFormat.Always_Web_URI.toString())
					.putHeader("GS1-Extensions", Metadata.GS1_Extensions).setStatusCode(204).end();
		});
		EPCISServer.logger.info("[OPTIONS /epcis/events/:eventID (application/json)] - router added");
	}

	public static void registerGetVocabularyHandler(Router router) {

		router.options("/epcis/vocabularies/:vocabularyID").consumes("application/xml").handler(routingContext -> {
			try {
				SOAPMessage message = new SOAPMessage();
				String vocabularyID = routingContext.pathParam("vocabularyID");
				List<Document> jobs = new ArrayList<Document>();
				EPCISServer.mVocCollection.find(new Document("id", TagDataTranslationEngine.toEPC(vocabularyID)))
						.into(jobs);
				if (jobs.isEmpty()) {
					EPCISException e = new EPCISException("There is no vocabulary with the given id: " + vocabularyID);
					HTTPUtil.sendQueryResults(routingContext.response(), message, e, e.getClass(), 404);
					return;
				}
			} catch (IllegalArgumentException e) {
				SOAPMessage message = new SOAPMessage();
				EPCISException e1 = new EPCISException("Illegal vocabulary identifier: " + e.getMessage());
				HTTPUtil.sendQueryResults(routingContext.response(), message, e1, e1.getClass(), 404);
				return;
			} catch (ValidationException e) {
				SOAPMessage message = new SOAPMessage();
				EPCISException e1 = new EPCISException("Illegal vocabulary identifier: " + e.getReason());
				HTTPUtil.sendQueryResults(routingContext.response(), message, e1, e1.getClass(), 404);
				return;
			} catch (Throwable throwable) {
				SOAPMessage message = new SOAPMessage();
				EPCISServer.logger.info(throwable.getMessage());
				EPCISException e = new EPCISException(throwable.getMessage());
				HTTPUtil.sendQueryResults(routingContext.response(), message, e, e.getClass(), 500);
				return;
			}
			routingContext.response().putHeader("Access-Control-Expose-Headers", "*").putHeader("Allow", "OPTIONS, GET")
					.putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-EPCIS-Min", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-EPCIS-Max", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-CBV-Version", Metadata.GS1_CBV_Version)
					.putHeader("GS1-CBV-Max", Metadata.GS1_CBV_Version)
					.putHeader("GS1-CBV-Min", Metadata.GS1_CBV_Version)
					.putHeader("GS1-Vendor-Version", Metadata.GS1_Vendor_Version)
					.putHeader("GS1-EPC-Format", GS1EPCFormat.Always_EPC_URN.toString())
					.putHeader("GS1-CBV-XML-Format", GS1CBVXMLFormat.Always_URN.toString())
					.putHeader("GS1-Extensions", Metadata.GS1_Extensions).setStatusCode(204).end();
		});
		EPCISServer.logger.info("[OPTIONS /epcis/vocabularies/:vocabularyID (application/xml)] - router added");

		/**
		 * Query metadata for the endpoint to access an individual EPCIS event. EPCIS
		 * 2.0 supports a number of custom headers to describe custom vocabularies and
		 * support multiple versions of EPCIS and CBV. The `OPTIONS` method allows the
		 * client to discover which vocabularies and EPCIS and CBV versions are used.
		 * (application/json)
		 */
		router.options("/epcis/vocabularies/:vocabularyID").consumes("application/json").handler(routingContext -> {
			try {
				String vocabularyID = routingContext.pathParam("vocabularyID");
				List<Document> jobs = new ArrayList<Document>();
				EPCISServer.mVocCollection
						.find(new Document("id", TagDataTranslationEngine.toEPC(vocabularyID))).into(jobs);
				if (jobs.isEmpty()) {
					HTTPUtil.sendQueryResults(routingContext.response(), JSONMessageFactory
							.get404NoSuchResourceException("There is no vocabulary with the given id: " + vocabularyID),
							404);
					return;
				}
			} catch (IllegalArgumentException e) {
				HTTPUtil.sendQueryResults(routingContext.response(), JSONMessageFactory
						.get404NoSuchResourceException("Illegal vocabulary identifier: " + e.getMessage()), 404);
				return;
			} catch (ValidationException e) {
				HTTPUtil.sendQueryResults(routingContext.response(), JSONMessageFactory
						.get404NoSuchResourceException("Illegal vocabulary identifier: " + e.getReason()), 404);
				return;
			} catch (Throwable throwable) {
				HTTPUtil.sendQueryResults(routingContext.response(),
						JSONMessageFactory.get500ImplementationException(throwable.getMessage()), 500);
				return;
			}
			routingContext.response().putHeader("Access-Control-Expose-Headers", "*").putHeader("Allow", "OPTIONS, GET")
					.putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-EPCIS-Min", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-EPCIS-Max", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-CBV-Version", Metadata.GS1_CBV_Version)
					.putHeader("GS1-CBV-Max", Metadata.GS1_CBV_Version)
					.putHeader("GS1-CBV-Min", Metadata.GS1_CBV_Version)
					.putHeader("GS1-Vendor-Version", Metadata.GS1_Vendor_Version)
					.putHeader("GS1-EPC-Format", GS1EPCFormat.Always_GS1_Digital_Link.toString())
					.putHeader("GS1-CBV-XML-Format", GS1CBVXMLFormat.Always_Web_URI.toString())
					.putHeader("GS1-Extensions", Metadata.GS1_Extensions).setStatusCode(204).end();
		});
		EPCISServer.logger.info("[OPTIONS /epcis/vocabularies/:vocabularyID (application/json)] - router added");
	}

	public static void registerQueryHandler(Router router) {
		// Additional interface for SOAP QUERY interface
		router.options("/epcis/query").consumes("application/xml").handler(routingContext -> {
			routingContext.response().putHeader("Access-Control-Expose-Headers", "*")
					.putHeader("Allow", "OPTIONS, POST").putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-EPCIS-Min", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-EPCIS-Max", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-CBV-Version", Metadata.GS1_CBV_Version)
					.putHeader("GS1-CBV-Max", Metadata.GS1_CBV_Version)
					.putHeader("GS1-CBV-Min", Metadata.GS1_CBV_Version)
					.putHeader("GS1-Vendor-Version", Metadata.GS1_Vendor_Version)
					.putHeader("GS1-EPC-Format", GS1EPCFormat.Always_EPC_URN.toString())
					.putHeader("GS1-CBV-XML-Format", GS1CBVXMLFormat.Always_URN.toString())
					.putHeader("GS1-Extensions", Metadata.GS1_Extensions).setStatusCode(204).end();
		});
		EPCISServer.logger.info("[OPTIONS /epcis/query (application/xml)] - router added");
	}
}
