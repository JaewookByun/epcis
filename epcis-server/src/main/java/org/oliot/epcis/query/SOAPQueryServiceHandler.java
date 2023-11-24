package org.oliot.epcis.query;

import org.oliot.epcis.model.EPCISException;
import org.oliot.epcis.server.EPCISServer;
import org.oliot.epcis.util.HTTPUtil;
import org.oliot.epcis.util.SOAPMessage;
import io.vertx.ext.web.RequestBody;
import io.vertx.ext.web.Router;

/**
 * Copyright (C) 2020-2023. (Jaewook Byun) all rights reserved.
 * <p>
 * This project is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * SOAPQueryServiceHandler holds routers for Query Interface
 * <p>
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr, Associate Director, Auto-ID Labs, Korea,
 *         bjw0829@gmail.com
 */
public class SOAPQueryServiceHandler {

	public static void registerQueryHandler(Router router, SOAPQueryService soapQueryService) {
		router.post("/epcis/query").consumes("application/xml").handler(routingContext -> {
			SOAPMessage message = new SOAPMessage();
			RequestBody body = routingContext.body();
			if (body.isEmpty()) {
				EPCISException e = new EPCISException("[400ValidationException] Empty Request Body");
				EPCISServer.logger.error(e.getReason());
				HTTPUtil.sendQueryResults(routingContext.response(), message, e, e.getClass(), 400);
				return;
			}
			String inputString = body.asString();
			soapQueryService.query(routingContext.request(), routingContext.response().setChunked(true), inputString);
		});
		EPCISServer.logger.info("[POST /epcis/query] - router added");
	}
}
