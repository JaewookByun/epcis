package org.oliot.epcis.query;

import java.util.Iterator;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;

import org.oliot.epcis.model.EPCISException;
import org.oliot.epcis.model.ImplementationException;
import org.oliot.epcis.model.ImplementationExceptionSeverity;
import org.oliot.epcis.model.ValidationException;
import org.oliot.epcis.resource.DynamicResource;
import org.oliot.epcis.server.EPCISServer;
import org.oliot.epcis.util.HTTPUtil;
import org.oliot.epcis.util.SOAPMessage;
import org.oliot.epcis.validation.HeaderValidator;

import io.vertx.core.MultiMap;
import io.vertx.ext.web.RequestBody;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

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

	public static String getHttpBody(RoutingContext routingContext) {
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
				HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(), 400);
				return null;
			}
		} else {
			inputString = body.asString();
		}
		return inputString;
	}

	public static boolean isHeaderPassed(RoutingContext routingContext) {
		if (!HeaderValidator.isEqualHeaderSOAP(routingContext, "GS1-CBV-Min", false))
			return false;

		if (!HeaderValidator.isEqualHeaderSOAP(routingContext, "GS1-CBV-Max", false))
			return false;

		if (!HeaderValidator.isEqualHeaderSOAP(routingContext, "GS1-EPCIS-Min", false))
			return false;

		if (!HeaderValidator.isEqualHeaderSOAP(routingContext, "GS1-EPCIS-Max", false))
			return false;

		if (!HeaderValidator.isEqualHeaderSOAP(routingContext, "GS1-EPC-Format", false))
			return false;

		if (!HeaderValidator.isEqualHeaderSOAP(routingContext, "GS1-CBV-XML-Format", false))
			return false;
		return true;
	}

	public static void registerPollHandler(Router router, SOAPQueryService soapQueryService) {
		router.get("/epcis/events").consumes("application/xml").handler(routingContext -> {

			if (!isHeaderPassed(routingContext))
				return;

			routingContext.response().setChunked(true);

			String nextPageToken = routingContext.request().getParam("nextPageToken");
			if (nextPageToken == null) {
				String inputString = getHttpBody(routingContext);
				if (inputString == null)
					return;
				try {
					soapQueryService.pollEventsOrVocabularies(routingContext.request(), routingContext.response(),
							inputString, null, null);
				} catch (ValidationException e) {
					EPCISServer.logger.error(e.getReason());
					HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(), 400);
				}
			} else {
				soapQueryService.getNextEventPage(routingContext.request(), routingContext.response());
			}
		});
		EPCISServer.logger.info("[GET /epcis/events (application/xml)] - router added");

		router.get("/epcis/vocabularies").consumes("application/xml").handler(routingContext -> {

			if (!isHeaderPassed(routingContext))
				return;

			routingContext.response().setChunked(true);

			String nextPageToken = routingContext.request().getParam("nextPageToken");
			if (nextPageToken == null) {
				String inputString = getHttpBody(routingContext);
				if (inputString == null)
					return;
				try {
					soapQueryService.pollEventsOrVocabularies(routingContext.request(), routingContext.response(),
							inputString, null, null);
				} catch (ValidationException e) {
					EPCISServer.logger.error(e.getReason());
					HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(), 400);
				}
			} else {
				try {
					soapQueryService.getNextVocabularyPage(routingContext.request(), routingContext.response());
				} catch (ParserConfigurationException e) {
					ImplementationException e1 = new ImplementationException(ImplementationExceptionSeverity.ERROR,
							"Poll", e.getMessage());
					HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e1, e1.getClass(), 500);
				}
			}
		});
		EPCISServer.logger.info("[GET /epcis/vocabularies (application/xml)] - router added");
	}

	public static void registerPollWithEventTypeHandler(Router router, SOAPQueryService soapQueryService) {
		router.get("/epcis/eventTypes/:eventType/events").consumes("application/xml").handler(routingContext -> {

			if (!isHeaderPassed(routingContext))
				return;

			routingContext.response().setChunked(true);

			String eventType = routingContext.pathParam("eventType");

			if (!DynamicResource.availableEventTypes.contains(eventType)) {
				EPCISException e = new EPCISException(
						"[404NoSuchResourceException] There is no available query for eventType: " + eventType);
				EPCISServer.logger.error(e.getReason());
				HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(), 404);
				return;
			}

			String nextPageToken = routingContext.request().getParam("nextPageToken");
			if (nextPageToken == null) {
				String inputString = getHttpBody(routingContext);
				if (inputString == null)
					return;
				try {
					soapQueryService.pollEventsOrVocabularies(routingContext.request(), routingContext.response(),
							inputString, "eventType", eventType);
				} catch (ValidationException e) {
					EPCISServer.logger.error(e.getReason());
					HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(), 400);
				}
			} else {
				soapQueryService.getNextEventPage(routingContext.request(), routingContext.response());
			}
		});
		EPCISServer.logger.info("[GET /epcis/events (application/xml)] - router added");
	}
}
