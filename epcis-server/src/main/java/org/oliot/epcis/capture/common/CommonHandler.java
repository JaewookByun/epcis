package org.oliot.epcis.capture.common;

import io.vertx.ext.web.Router;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.oliot.epcis.common.Metadata;
import org.oliot.epcis.model.EPCISException;
import org.oliot.epcis.model.GS1CBVXMLFormat;
import org.oliot.epcis.model.GS1EPCFormat;
import org.oliot.epcis.resource.DynamicResource;
import org.oliot.epcis.server.EPCISServer;
import org.oliot.epcis.util.HTTPUtil;
import org.oliot.epcis.util.SOAPMessage;

/**
 * Copyright (C) 2020-2023. (Jaewook Byun) all rights reserved.
 * <p>
 * This project is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * XMLCaptureMetadataHandler holds routers for metadata services.
 * <p>
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr, Associate Director, Auto-ID Labs, Korea,
 *         bjw0829@gmail.com
 */
public class CommonHandler {
	public static void registerBaseHandler(Router router) {
		// Query server settings, EPCIS version(s) and related vocabularies/standards.
		// `OPTIONS` on the root path gives the client an overview of the server's
		// EPCIS-related configurations.
		router.options("/epcis").handler(routingContext -> {
			routingContext.response().putHeader("Allow", "OPTIONS")
					.putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-Vendor-Version", Metadata.GS1_Vendor_Version)
					.putHeader("GS1-CBV-Version", Metadata.GS1_CBV_Version)
					.putHeader("GS1-EPC-Format", GS1EPCFormat.Always_EPC_URN.toString())
					.putHeader("GS1-CBV-XML-Format", GS1CBVXMLFormat.Always_URN.toString())
					.putHeader("GS1-Extensions", Metadata.GS1_Extensions).setStatusCode(204).end();
		});
		EPCISServer.logger.info("[OPTIONS /epcis] - router added");
	}

	public static void registerCaptureHandler(Router router) {
		// Discover the settings of the capture interface.
		// The `OPTIONS` method is used as a discovery service for `/capture`.
		// It describes\n- which EPCIS and CBV versions are supported,\n-
		// the EPCIS and CBV extensions,\n-
		// the maximum payload size as count of EPCIS events (`GS1-EPCIS-Capture-Limit`
		// header)
		// or as a maximum payload size in bytes (`GS1-EPCIS-Capture-File-Size-Limit`
		// header)\n-
		// what the server will do if an error occurred during capture
		// (`GS1-Capture-Error-Behaviour` header).\n
		// The list of headers is not exhaustive. It only describes the functionality
		// specific to EPCIS 2.0.\n",
		router.options("/epcis/capture").handler(routingContext -> {
			routingContext.response().putHeader("Allow", "OPTIONS, GET, POST")
					.putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-Vendor-Version", Metadata.GS1_Vendor_Version)
					.putHeader("GS1-CBV-Version", Metadata.GS1_CBV_Version)
					.putHeader("GS1-EPC-Format", GS1EPCFormat.Always_EPC_URN.toString())
					.putHeader("GS1-CBV-XML-Format", GS1CBVXMLFormat.Always_URN.toString())
					.putHeader("GS1-Extensions", Metadata.GS1_Extensions)
					.putHeader("GS1-EPCIS-Capture-Limit", String.valueOf(Metadata.GS1_CAPTURE_limit))
					.putHeader("GS1-EPCIS-Capture-File-Size-Limit",
							String.valueOf(Metadata.GS1_CAPTURE_file_size_limit))
					.putHeader("GS1-EPCIS-Capture-Error-Behaviour", Metadata.GS1_EPCIS_Capture_Error_Behaviour)
					.setStatusCode(204).end();
		});
		EPCISServer.logger.info("[OPTIONS /epcis/capture] - router added");
	}

	public static void registerCaptureIDHandler(Router router) {
		// Query the metadata of the capture job endpoint.
		// "EPCIS 2.0 supports a number of custom headers to describe custom
		// vocabularies and support multiple versions\n
		// of EPCIS and CBV. The `OPTIONS` method allows the client to discover which
		// vocabularies and EPCIS and CBV\n
		// versions are used for a given capture job.\n",

		router.options("/epcis/capture/:captureID").handler(routingContext -> {
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
			} catch (Throwable throwable) {
				SOAPMessage message = new SOAPMessage();
				EPCISServer.logger.info(throwable.getMessage());
				EPCISException e = new EPCISException(throwable.getMessage());
				HTTPUtil.sendQueryResults(routingContext.response(), message, e, e.getClass(), 500);
				return;
			}
			routingContext.response().putHeader("Allow", "OPTIONS, GET")
					.putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-Vendor-Version", Metadata.GS1_Vendor_Version)
					.putHeader("GS1-CBV-Version", Metadata.GS1_CBV_Version)
					.putHeader("GS1-EPC-Format", GS1EPCFormat.Always_EPC_URN.toString())
					.putHeader("GS1-CBV-XML-Format", GS1CBVXMLFormat.Always_URN.toString())
					.putHeader("GS1-Extensions", Metadata.GS1_Extensions).setStatusCode(204).end();
		});
		EPCISServer.logger.info("[OPTIONS /epcis/capture/:captureID] - router added");
	}

	public static void registerEventsHandler(Router router) {
		// Query metadata for the EPCIS events endpoint.
		// EPCIS 2.0 supports a number of custom headers to describe custom vocabularies
		// and support multiple versions\n
		// of EPCIS and CBV. The `OPTIONS` method allows the client to discover which
		// vocabularies and EPCIS and CBV\n
		// versions are used.\n"
		router.options("/epcis/events").handler(routingContext -> {
			routingContext.response().putHeader("Allow", "OPTIONS, POST")
					.putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-Vendor-Version", Metadata.GS1_Vendor_Version)
					.putHeader("GS1-CBV-Version", Metadata.GS1_CBV_Version)
					.putHeader("GS1-EPC-Format", GS1EPCFormat.Always_EPC_URN.toString())
					.putHeader("GS1-CBV-XML-Format", GS1CBVXMLFormat.Always_URN.toString())
					.putHeader("GS1-Extensions", Metadata.GS1_Extensions).setStatusCode(204).end();
		});
		EPCISServer.logger.info("[OPTIONS /epcis/events] - router added");
	}	

	/**
	 * non-standard service to provide 'ping'
	 *
	 * @param router router
	 */
	public static void registerPingHandler(Router router) {
		router.get("/epcis").handler(routingContext -> {
			routingContext.response().setStatusCode(200).end();
		});
		EPCISServer.logger.info("[GET /epcis] - router added");
	}

	/**
	 * non-standard service to provide 'reset'
	 *
	 * @param router router
	 */
	public static void registerDeleteHandler(Router router) {
		router.delete("/epcis").handler(routingContext -> {
			EPCISServer.logger.debug("DB reset starts");

			try {
				EPCISServer.mDatabase.drop();
			} catch (Throwable e) {
				routingContext.response().end(e.getMessage());
				return;
			}
			EPCISServer.logger.debug("DB reset finished");

			routingContext.response().setStatusCode(200).end();
		});
	}
	
	public static void registerStatisticsHandler(Router router) {
		router.get("/epcis/statistics").handler(routingContext -> {
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
					.end(DynamicResource.getCounts().toString());
		});
		EPCISServer.logger.info("[POST /epcis/query] - router added");
	}
}
