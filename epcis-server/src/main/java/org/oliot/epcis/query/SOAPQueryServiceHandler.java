package org.oliot.epcis.query;

import javax.xml.parsers.ParserConfigurationException;

import org.oliot.epcis.model.ImplementationException;
import org.oliot.epcis.model.ImplementationExceptionSeverity;
import org.oliot.epcis.server.EPCISServer;
import org.oliot.epcis.util.HTTPUtil;
import org.oliot.epcis.util.SOAPMessage;

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

	public static void registerQueryHandler(SOAPQueryService soapQueryService) {
		EPCISServer.registerHandler("post", "/epcis/query", "application/xml", 
				routingContext -> {
					soapQueryService.query(routingContext.request(), routingContext.response().setChunked(true),
					routingContext.body().asString());}	);
			
//		router.post("/epcis/query").consumes("application/xml").handler(routingContext -> {
//			soapQueryService.query(routingContext.request(), routingContext.response().setChunked(true),
//					routingContext.body().asString());
//		});
		EPCISServer.logger.info("[POST /epcis/query] - router added");
	}

	public static void registerPaginationHandler(SOAPQueryService soapQueryService) {
		EPCISServer.registerHandler("get", "/epcis/events", "application/xml",routingContext -> {
			soapQueryService.getNextEventPage(routingContext.request(), routingContext.response());
		});
		EPCISServer.logger.info("[GET /epcis/events (application/xml)] - router added");
		
		EPCISServer.registerHandler("get","/epcis/vocabularies","application/xml", routingContext -> {
			try {
				soapQueryService.getNextVocabularyPage(routingContext.request(), routingContext.response());
			} catch (ParserConfigurationException e) {
				ImplementationException e1 = new ImplementationException(ImplementationExceptionSeverity.ERROR, "Poll",
						e.getMessage());
				HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e1, e1.getClass(), 500);
			}
		});
		EPCISServer.logger.info("[GET /epcis/vocabularies (application/xml)] - router added");
	}
}
