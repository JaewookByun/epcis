package org.oliot.epcis.common;

import io.vertx.ext.web.Router;

import java.util.UUID;

import org.oliot.epcis.capture.json.JSONMessageFactory;
import org.oliot.epcis.model.EPCISException;
import org.oliot.epcis.resource.DynamicResource;
import org.oliot.epcis.server.EPCISServer;
import org.oliot.epcis.util.HTTPUtil;
import org.oliot.epcis.util.SOAPMessage;

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

	public static void registerStatisticsHandler(Router router) {
		router.get("/epcis/statistics").handler(routingContext -> {
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
					.end(DynamicResource.getCounts().toString());
		});
		EPCISServer.logger.info("[POST /epcis/query] - router added");
	}

	/**
	 * * Optional endpoint that allows on-demand release of any resources associated
	 * with `nextPageToken`.
	 * 
	 * @param router
	 */
	public static void registerDeletePageTokenHandler(Router router) {
		router.delete("/epcis/nextPageToken/:token").consumes("application/xml").handler(routingContext -> {

			String token = routingContext.pathParam("token");

			UUID uuidToken = null;
			try {
				uuidToken = UUID.fromString(token);
			} catch (IllegalArgumentException e) {
				EPCISException e1 = new EPCISException("[404NoSuchResourceException] There is no page token: " + token);
				EPCISServer.logger.error("[404NoSuchResourceException] There is no page token: " + token);
				HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e1, e1.getClass(), 404);
				return;
			}

			if (EPCISServer.eventPageMap.remove(uuidToken) != null) {
				EPCISServer.logger.debug("Page token " + uuidToken + " is invalidated for events");
				routingContext.response().setStatusCode(204).end();
			} else if (EPCISServer.vocabularyPageMap.remove(uuidToken) != null) {
				EPCISServer.logger.debug("Page token " + uuidToken + " is invalidated for vocabularies");
				routingContext.response().setStatusCode(204).end();
			} else if (EPCISServer.captureIDPageMap.remove(uuidToken) != null) {
				EPCISServer.logger.debug("Page token " + uuidToken + " is invalidated for capture jobs");
				routingContext.response().setStatusCode(204).end();
			} else if (EPCISServer.epcsPageMap.remove(uuidToken) != null) {
				EPCISServer.logger.debug("Page token " + uuidToken + " is invalidated for epcs");
				routingContext.response().setStatusCode(204).end();
			} else if (EPCISServer.bizStepsPageMap.remove(uuidToken) != null) {
				EPCISServer.logger.debug("Page token " + uuidToken + " is invalidated for bizSteps");
				routingContext.response().setStatusCode(204).end();
			} else if (EPCISServer.bizLocationsPageMap.remove(uuidToken) != null) {
				EPCISServer.logger.debug("Page token " + uuidToken + " is invalidated for bizLocations");
				routingContext.response().setStatusCode(204).end();
			} else if (EPCISServer.readPointsPageMap.remove(uuidToken) != null) {
				EPCISServer.logger.debug("Page token " + uuidToken + " is invalidated for readPoints");
				routingContext.response().setStatusCode(204).end();
			} else if (EPCISServer.dispositionsPageMap.remove(uuidToken) != null) {
				EPCISServer.logger.debug("Page token " + uuidToken + " is invalidated for dispositions");
				routingContext.response().setStatusCode(204).end();
			} else if (EPCISServer.namedQueriesPageMap.remove(uuidToken) != null) {
				EPCISServer.logger.debug("Page token " + uuidToken + " is invalidated for named queries");
				routingContext.response().setStatusCode(204).end();
			}

			EPCISException e1 = new EPCISException("[404NoSuchResourceException] There is no page token: " + uuidToken);
			EPCISServer.logger.error("[404NoSuchResourceException] There is no page token: " + uuidToken);
			HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e1, e1.getClass(), 404);
		});
		EPCISServer.logger.info("[DELETE /epcis/nextPageToken/:token (application/xml)] - router added");

		router.delete("/epcis/nextPageToken/:token").consumes("application/json").handler(routingContext -> {

			String token = routingContext.pathParam("token");

			UUID uuidToken = null;
			try {
				uuidToken = UUID.fromString(token);
			} catch (IllegalArgumentException e) {
				HTTPUtil.sendQueryResults(routingContext.response(), JSONMessageFactory.get404NoSuchResourceException(
						"[404NoSuchResourceException] There is no available query for : " + token), 404);
				return;
			}

			if (EPCISServer.eventPageMap.remove(uuidToken) != null) {
				EPCISServer.logger.debug("Page token " + uuidToken + " is invalidated for events");
				routingContext.response().setStatusCode(204).end();
			} else if (EPCISServer.vocabularyPageMap.remove(uuidToken) != null) {
				EPCISServer.logger.debug("Page token " + uuidToken + " is invalidated for vocabularies");
				routingContext.response().setStatusCode(204).end();
			} else if (EPCISServer.captureIDPageMap.remove(uuidToken) != null) {
				EPCISServer.logger.debug("Page token " + uuidToken + " is invalidated for capture jobs");
				routingContext.response().setStatusCode(204).end();
			} else if (EPCISServer.epcsPageMap.remove(uuidToken) != null) {
				EPCISServer.logger.debug("Page token " + uuidToken + " is invalidated for epcs");
				routingContext.response().setStatusCode(204).end();
			} else if (EPCISServer.bizStepsPageMap.remove(uuidToken) != null) {
				EPCISServer.logger.debug("Page token " + uuidToken + " is invalidated for bizSteps");
				routingContext.response().setStatusCode(204).end();
			} else if (EPCISServer.bizLocationsPageMap.remove(uuidToken) != null) {
				EPCISServer.logger.debug("Page token " + uuidToken + " is invalidated for bizLocations");
				routingContext.response().setStatusCode(204).end();
			} else if (EPCISServer.readPointsPageMap.remove(uuidToken) != null) {
				EPCISServer.logger.debug("Page token " + uuidToken + " is invalidated for readPoints");
				routingContext.response().setStatusCode(204).end();
			} else if (EPCISServer.dispositionsPageMap.remove(uuidToken) != null) {
				EPCISServer.logger.debug("Page token " + uuidToken + " is invalidated for dispositions");
				routingContext.response().setStatusCode(204).end();
			} else if (EPCISServer.namedQueriesPageMap.remove(uuidToken) != null) {
				EPCISServer.logger.debug("Page token " + uuidToken + " is invalidated for named queries");
				routingContext.response().setStatusCode(204).end();
			}

			HTTPUtil.sendQueryResults(routingContext.response(), JSONMessageFactory.get404NoSuchResourceException(
					"[404NoSuchResourceException] There is no available query for : " + token), 404);
			return;
		});
		EPCISServer.logger.info("[DELETE /epcis/nextPageToken/:token (application/json)] - router added");
	}

}
