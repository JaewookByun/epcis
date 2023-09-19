package org.oliot.epcis.query;

import javax.xml.parsers.ParserConfigurationException;

import org.oliot.epcis.model.ImplementationException;
import org.oliot.epcis.model.ImplementationExceptionSeverity;
import org.oliot.epcis.resource.DynamicResource;
import org.oliot.epcis.server.EPCISServer;
import org.oliot.epcis.util.HTTPUtil;
import org.oliot.epcis.util.SOAPMessage;

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

	public static void registerStatisticsHandler(Router router) {
		router.get("/epcis/statistics").handler(routingContext -> {
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200).end(DynamicResource.getCounts().toString());
		});
		EPCISServer.logger.info("[POST /epcis/query] - router added");
	}
	
	public static void registerQueryHandler(Router router, SOAPQueryService soapQueryService) {
		router.post("/epcis/query").consumes("*/xml").handler(routingContext -> {
			soapQueryService.query(routingContext.request(), routingContext.response().setChunked(true),
					routingContext.body().asString());
		});
		EPCISServer.logger.info("[POST /epcis/query] - router added");
	}

	public static void registerPaginationHandler(Router router, SOAPQueryService soapQueryService) {
		router.get("/epcis/events").handler(routingContext -> {
			soapQueryService.getNextEventPage(routingContext.request(), routingContext.response());
		});
		EPCISServer.logger.info("[GET /epcis/events] - router added");
		router.get("/epcis/vocabularies").handler(routingContext -> {
			try {
				soapQueryService.getNextVocabularyPage(routingContext.request(), routingContext.response());
			} catch (ParserConfigurationException e) {
				ImplementationException e1 = new ImplementationException(ImplementationExceptionSeverity.ERROR, "Poll",
						e.getMessage());
				HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e1, e1.getClass(), 500);
			}
		});
		EPCISServer.logger.info("[GET /epcis/vocabularies] - router added");
	}

	

	public static void registerEchoHandler(Router router) {
		router.post("/epcis/echo").handler(routingContext -> {
			EPCISServer.logger.info(routingContext.body().asString());
			routingContext.response().setStatusCode(200).end();
		});
	}
}
