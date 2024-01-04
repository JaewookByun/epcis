package org.oliot.epcis.server.handler;

import io.vertx.ext.web.Router;

/**
 * Copyright (C) 2020-2024. (Jaewook Byun) all rights reserved.
 * <p>
 * This project is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr, Associate Director, Auto-ID Labs, Korea,
 *         bjw0829@gmail.com
 */
public class XMLCaptureServiceHandler {

	public static void registerPostCaptureHandler(Router router) {
		router.post("/epcis/capture").consumes("application/xml").handler(routingContext -> {

		});
	}

	public static void registerGetCaptureIDHandler(Router router) {
		router.get("/epcis/capture/:captureID").consumes("application/xml").handler(routingContext -> {

		});
	}

	public static void registerGetCaptureHandler(Router router) {
		router.get("/epcis/capture").consumes("application/xml").handler(routingContext -> {

		});
	}

	public static void registerPostEventsHandler(Router router) {
		router.post("/epcis/events").consumes("application/xml").blockingHandler(routingContext -> {

		});
	}
}
