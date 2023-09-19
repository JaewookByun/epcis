package org.oliot.epcis.capture.common;

import io.vertx.ext.web.Router;
import org.oliot.epcis.server.EPCISServer;

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
}
