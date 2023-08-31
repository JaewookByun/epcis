package org.oliot.epcis.capture.xml;

import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.Router;
import org.oliot.epcis.model.EPCISException;
import org.oliot.epcis.pagination.Page;
import org.oliot.epcis.server.EPCISServer;
import org.oliot.epcis.util.HTTPUtil;
import org.oliot.epcis.util.SOAPMessage;

import java.util.UUID;

import static org.oliot.epcis.validation.HeaderValidator.checkEPCISMinMaxVersion;
import static org.oliot.epcis.validation.HeaderValidator.isEqualHeader;

/**
 * Copyright (C) 2020-2023. (Jaewook Byun) all rights reserved.
 * <p>
 * This project is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * XMLCaptureServiceHandler holds routers for capture services.
 * <p>
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr, Associate Director, Auto-ID Labs, Korea,
 *         bjw0829@gmail.com
 */
public class XMLCaptureServiceHandler {

	/**
	 * EPCIS events are added in bulk using the capture interface. Four design
	 * considerations were made to remain compatible with EPCIS 1.2:\n EPCIS 2.0
	 * keeps event IDs optional. If event IDs are missing, the server should
	 * populate the event ID with a unique value.\n Otherwise, it won't be possible
	 * to retrieve these events by eventID.\n - By default, EPCIS events are only
	 * stored if the entire capture job was successful. This behaviour can be
	 * changed with the `GS1-Capture-Error-Behaviour` header.\n- EPCIS master data
	 * can be captured in the header (`epcisHeader`) of an `EPCISDocument`.\n - This
	 * endpoint should support both `EPCISDocument` and `EPCISQueryDocument` as
	 * input.\n To prevent timeouts for large payloads, the client potentially may
	 * need to split the payload into several capture calls. To that end, the server
	 * can specify a capture\nlimit (number of EPCIS events) and file size limit
	 * (payload size).\n A successful capturing of events does not guarantee that
	 * events will be stored. Instead, the server returns a\ncapture id, which the
	 * client can use to obtain information about the capture job.\n"
	 *
	 * @param router            router
	 * @param xmlCaptureService xmlCaptureService
	 * @param eventBus          eventBus
	 */
	public static void registerPostCaptureHandler(Router router, XMLCaptureService xmlCaptureService,
			EventBus eventBus) {

		router.post("/epcis/capture").consumes("application/xml").handler(routingContext -> {
			if (!isEqualHeader(routingContext, "GS1-EPCIS-Version"))
				return;
			if (!isEqualHeader(routingContext, "GS1-CBV-Version"))
				return;
			if (!isEqualHeader(routingContext, "GS1-EPCIS-Capture-Error-Behaviour"))
				return;

			xmlCaptureService.post(routingContext, eventBus);
		});
		EPCISServer.logger.info("[POST /epcis/capture (application/xml)] - router added");
	}

	/**
	 * Returns a list of capture jobs. When EPCIS events are added through the
	 * capture interface, the capture process can run asynchronously. If the payload
	 * is syntactically correct and the client is allowed to call `/capture`, the
	 * server returns a `202` HTTP response code. This endpoint returns all capture
	 * jobs that were created and supports pagination.
	 *
	 * @param router            router
	 * @param xmlCaptureService xmlCaptureService
	 */
	public static void registerGetCaptureHandler(Router router, XMLCaptureService xmlCaptureService) {
		router.get("/epcis/capture").handler(routingContext -> {
			if (!checkEPCISMinMaxVersion(routingContext))
				return;

			String nextPageToken = routingContext.request().getParam("NextPageToken");
			if (nextPageToken == null) {
				xmlCaptureService.postCaptureJobList(routingContext);
			} else {
				xmlCaptureService.postRemainingCaptureJobList(routingContext, nextPageToken);
			}
		});
		EPCISServer.logger.info("[GET /epcis/capture (application/xml)] - router added");
	}

	/**
	 * Returns information about the capture job.
	 *
	 * @param router            router
	 * @param xmlCaptureService xmlCaptureService
	 */
	public static void registerGetCaptureIDHandler(Router router, XMLCaptureService xmlCaptureService) {
		router.get("/epcis/capture/:captureID").handler(routingContext -> {
			if (!checkEPCISMinMaxVersion(routingContext))
				return;
			xmlCaptureService.postCaptureJob(routingContext, routingContext.pathParam("captureID"));
		});
		EPCISServer.logger.info("[GET /epcis/capture/:captureID (application/xml)] - router added");
	}

	/**
	 * Synchronous capture interface for a single EPCIS event. An individual EPCIS
	 * event can be created by making a `POST` request on the `/events` resource.
	 * Alternatively, the client can also use the `/capture` interface and capture a
	 * single event.
	 *
	 * @param router            router
	 * @param xmlCaptureService xmlCaptureService
	 */
	public static void registerPostEventsHandler(Router router, XMLCaptureService xmlCaptureService,
			EventBus eventBus) {
		router.post("/epcis/events").consumes("*/xml").blockingHandler(routingContext -> {
			if (!isEqualHeader(routingContext, "GS1-EPCIS-Version"))
				return;
			if (!isEqualHeader(routingContext, "GS1-CBV-Version"))
				return;
			xmlCaptureService.postEvent(routingContext, eventBus);
		});
		EPCISServer.logger.info("[POST /epcis/events (application/xml)] - router added");
	}

	/**
	 * Optional endpoint that allows on-demand release of any resources associated
	 * with `nextPageToken`.
	 *
	 * @param router
	 */
	public static void registerDeletePageToken(Router router) {
		router.delete("/epcis/nextPageToken/:token").handler(routingContext -> {
			UUID uuid = UUID.fromString(routingContext.pathParam("token"));
			Page page = EPCISServer.captureIDPageMap.remove(uuid);
			try {
				page.getTimer().cancel();
			} catch (Exception e) {

			}
			if (page != null) {
				routingContext.response().setStatusCode(204).end();
			} else {
				EPCISException e = new EPCISException("There is no page with token: " + uuid.toString());
				HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(), 404);
			}
		});
		EPCISServer.logger.info("[DELETE /nextPageToken/:token (application/xml)] - router added");
	}

	/**
	 * non-standard to provide validation service
	 *
	 * @param router            router
	 * @param xmlCaptureService xmlCaptureService
	 */
	public static void registerValidationHandler(Router router, XMLCaptureService xmlCaptureService) {
		router.post("/epcis/validation").consumes("*/xml").handler(xmlCaptureService::postValidationResult);
		EPCISServer.logger.info("[POST /epcis/validation (xml)] - router added");
	}

	
}
