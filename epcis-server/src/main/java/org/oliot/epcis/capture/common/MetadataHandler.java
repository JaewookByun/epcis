package org.oliot.epcis.capture.common;

import org.oliot.epcis.common.Metadata;
import org.oliot.epcis.model.GS1CBVXMLFormat;
import org.oliot.epcis.model.GS1EPCFormat;
import org.oliot.epcis.server.EPCISServer;

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
		 * exhaustive. It only describes the functionality specific to EPCIS 2.0."
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
		 * exhaustive. It only describes the functionality specific to EPCIS 2.0."
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
}
