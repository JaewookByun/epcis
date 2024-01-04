package org.oliot.epcis.server.handler;

import io.vertx.ext.web.Router;

/**
 * Copyright (C) 2020-2023. (Jaewook Byun) all rights reserved.
 * <p>
 * This project is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr, Associate Director, Auto-ID Labs, Korea,
 *         bjw0829@gmail.com
 */
public class SOAPQueryServiceHandler {

	public static void registerQueryHandler(Router router) {
		router.post("/epcis/query").consumes("application/xml").handler(routingContext -> {

		});
	}
}
