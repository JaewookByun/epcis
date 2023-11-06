package org.oliot.epcis.capture.common;

import io.vertx.ext.web.Router;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.oliot.epcis.capture.json.JSONMessageFactory;
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

	public static void registerQueryHandler(Router router) {
		// Query server settings, EPCIS version(s) and related vocabularies/standards.
		// `OPTIONS` on the root path gives the client an overview of the server's
		// EPCIS-related configurations.
		router.options("/epcis/query").consumes("application/xml").handler(routingContext -> {
			routingContext.response().putHeader("Allow", "OPTIONS, POST")
					.putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
					.putHeader("GS1-Vendor-Version", Metadata.GS1_Vendor_Version)
					.putHeader("GS1-CBV-Version", Metadata.GS1_CBV_Version)
					.putHeader("GS1-EPC-Format", GS1EPCFormat.Always_EPC_URN.toString())
					.putHeader("GS1-CBV-XML-Format", GS1CBVXMLFormat.Always_URN.toString())
					.putHeader("GS1-Extensions", Metadata.GS1_Extensions).setStatusCode(204).end();
		});
		EPCISServer.logger.info("[OPTIONS /epcis (application/xml)] - router added");
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
