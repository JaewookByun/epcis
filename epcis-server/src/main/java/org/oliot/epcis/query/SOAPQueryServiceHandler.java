package org.oliot.epcis.query;

import java.util.Iterator;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;

import org.oliot.epcis.model.EPCISException;
import org.oliot.epcis.model.ImplementationException;
import org.oliot.epcis.model.ImplementationExceptionSeverity;
import org.oliot.epcis.model.ValidationException;
import org.oliot.epcis.server.EPCISServer;
import org.oliot.epcis.util.HTTPUtil;
import org.oliot.epcis.util.SOAPMessage;
import org.oliot.epcis.validation.HeaderValidator;

import io.vertx.core.MultiMap;
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

	public static void registerPollHandler(Router router, SOAPQueryService soapQueryService) {
		router.get("/epcis/events").consumes("application/xml").handler(routingContext -> {
			if (!HeaderValidator.isEqualHeaderSOAP(routingContext, "GS1-CBV-Min", false))
				return;

			if (!HeaderValidator.isEqualHeaderSOAP(routingContext, "GS1-CBV-Max", false))
				return;

			if (!HeaderValidator.isEqualHeaderSOAP(routingContext, "GS1-EPCIS-Min", false))
				return;

			if (!HeaderValidator.isEqualHeaderSOAP(routingContext, "GS1-EPCIS-Max", false))
				return;

			if (!HeaderValidator.isEqualHeaderSOAP(routingContext, "GS1-EPC-Format", false))
				return;

			if (!HeaderValidator.isEqualHeaderSOAP(routingContext, "GS1-CBV-XML-Format", false))
				return;

			routingContext.response().setChunked(true);

			String nextPageToken = routingContext.request().getParam("nextPageToken");
			if (nextPageToken == null) {
				SOAPMessage message = new SOAPMessage();
				RequestBody body = routingContext.body();
				String inputString = null;
				if (body.isEmpty()) {
					MultiMap mm = routingContext.queryParams();
					Iterator<Entry<String, String>> iter = mm.iterator();
					while (iter.hasNext()) {
						Entry<String, String> entry = iter.next();
						String k = entry.getKey();
						if (k.contains("Envelope") && k.contains("Poll") && k.contains("SimpleEventQuery"))
							inputString = k;
					}
					if (inputString == null) {
						EPCISException e = new EPCISException("[400ValidationException] Empty Request Body");
						EPCISServer.logger.error(e.getReason());
						HTTPUtil.sendQueryResults(routingContext.response(), message, e, e.getClass(), 400);
						return;
					}
				} else {
					inputString = body.asString();
				}
				try {
					soapQueryService.pollEventsOrVocabularies(routingContext.request(), routingContext.response(), inputString);
				} catch (ValidationException e) {
					EPCISServer.logger.error(e.getReason());
					HTTPUtil.sendQueryResults(routingContext.response(), message, e, e.getClass(), 400);
				}
			} else {
				soapQueryService.getNextEventPage(routingContext.request(), routingContext.response());
			}
		});
		EPCISServer.logger.info("[GET /epcis/events (application/xml)] - router added");
		
		
		router.get("/epcis/vocabularies").consumes("application/xml").handler(routingContext -> {
			
			if (!HeaderValidator.isEqualHeaderSOAP(routingContext, "GS1-CBV-Min", false))
				return;

			if (!HeaderValidator.isEqualHeaderSOAP(routingContext, "GS1-CBV-Max", false))
				return;

			if (!HeaderValidator.isEqualHeaderSOAP(routingContext, "GS1-EPCIS-Min", false))
				return;

			if (!HeaderValidator.isEqualHeaderSOAP(routingContext, "GS1-EPCIS-Max", false))
				return;

			if (!HeaderValidator.isEqualHeaderSOAP(routingContext, "GS1-EPC-Format", false))
				return;

			if (!HeaderValidator.isEqualHeaderSOAP(routingContext, "GS1-CBV-XML-Format", false))
				return;

			routingContext.response().setChunked(true);

			String nextPageToken = routingContext.request().getParam("nextPageToken");
			if (nextPageToken == null) {
				SOAPMessage message = new SOAPMessage();
				RequestBody body = routingContext.body();
				String inputString = null;
				if (body.isEmpty()) {
					MultiMap mm = routingContext.queryParams();
					Iterator<Entry<String, String>> iter = mm.iterator();
					while (iter.hasNext()) {
						Entry<String, String> entry = iter.next();
						String k = entry.getKey();
						if (k.contains("Envelope") && k.contains("Poll") && k.contains("SimpleMasterDataQuery"))
							inputString = k;
					}
					if (inputString == null) {
						EPCISException e = new EPCISException("[400ValidationException] Empty Request Body");
						EPCISServer.logger.error(e.getReason());
						HTTPUtil.sendQueryResults(routingContext.response(), message, e, e.getClass(), 400);
						return;
					}
				} else {
					inputString = body.asString();
				}
				try {
					soapQueryService.pollEventsOrVocabularies(routingContext.request(), routingContext.response(), inputString);
				} catch (ValidationException e) {
					EPCISServer.logger.error(e.getReason());
					HTTPUtil.sendQueryResults(routingContext.response(), message, e, e.getClass(), 400);
				}
			} else {
				soapQueryService.getNextEventPage(routingContext.request(), routingContext.response());
			}
			
			
			
			
			// -----------------------------------------------------------------------------------------------
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
