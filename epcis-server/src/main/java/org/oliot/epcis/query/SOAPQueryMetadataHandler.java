package org.oliot.epcis.query;

import org.oliot.epcis.common.Metadata;
import org.oliot.epcis.model.GS1CBVXMLFormat;
import org.oliot.epcis.model.GS1EPCFormat;
import org.oliot.epcis.server.EPCISServer;

import io.vertx.ext.web.Router;

/**
 * Copyright (C) 2020-2023. (Jaewook Byun) all rights reserved.
 * <p>
 * This project is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * SOAPQueryMetadataHandler holds a router handling metadata service.
 * <p>
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr, Associate Director, Auto-ID Labs, Korea,
 *         bjw0829@gmail.com
 */
public class SOAPQueryMetadataHandler {
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
}
