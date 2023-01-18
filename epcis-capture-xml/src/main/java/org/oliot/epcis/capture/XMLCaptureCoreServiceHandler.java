package org.oliot.epcis.capture;

import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.Router;
import org.oliot.epcis.model.EPCISException;
import org.oliot.epcis.pagination.Page;
import org.oliot.epcis.util.HTTPUtil;

import java.util.UUID;

import static org.oliot.epcis.capture.XMLCaptureServer.captureIDPageMap;
import static org.oliot.epcis.capture.XMLCaptureServer.logger;
import static org.oliot.epcis.validation.HeaderValidator.checkEPCISMinMaxVersion;
import static org.oliot.epcis.validation.HeaderValidator.isEqualHeader;

/**
 * Copyright (C) 2020-2023. (Jaewook Byun) all rights reserved.
 * <p>
 * This project is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * Among various modules, epcis-capture-xml acts as a server to receive
 * XML-formatted EPCIS documents to capture events in the documents into an
 * EPCIS repository.
 * <p>
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 * jwbyun@sejong.ac.kr, Associate Director, Auto-ID Labs, Korea,
 * bjw0829@gmail.com
 */
public class XMLCaptureCoreServiceHandler {

    /**
     * EPCIS events are added in bulk using the capture interface. Four design considerations were made to remain compatible with EPCIS 1.2:\n
     * EPCIS 2.0 keeps event IDs optional. If event IDs are missing, the server should populate the event ID with a unique value.\n
     * Otherwise, it won't be possible to retrieve these events by eventID.\n
     * - By default, EPCIS events are only stored if the entire capture job was successful.
     * This behaviour can be changed with the `GS1-Capture-Error-Behaviour` header.\n-
     * EPCIS master data can be captured in the header (`epcisHeader`) of an `EPCISDocument`.\n
     * - This endpoint should support both `EPCISDocument` and `EPCISQueryDocument` as input.\n
     * To prevent timeouts for large payloads, the client potentially may need to split the payload into several capture calls.
     * To that end, the server can specify a capture\nlimit (number of EPCIS events) and file size limit (payload size).\n
     * A successful capturing of events does not guarantee that events will be stored.
     * Instead, the server returns a\ncapture id, which the client can use to obtain information about the capture job.\n"
     *
     * @param router            router
     * @param xmlCaptureService xmlCaptureService
     * @param eventBus          eventBus
     */
    void registerPostCaptureHandler(Router router, XMLCaptureCoreService xmlCaptureService, EventBus eventBus) {

        router.post("/epcis/capture").consumes("application/xml").handler(routingContext -> {
            if (!isEqualHeader(routingContext, "GS1-EPCIS-Version"))
                return;
            if (!isEqualHeader(routingContext, "GS1-CBV-Version"))
                return;
            if (!isEqualHeader(routingContext, "GS1-EPCIS-Capture-Error-Behaviour"))
                return;

            xmlCaptureService.post(routingContext, eventBus);
        });
        logger.info("[POST /epcis/capture] - router added");
    }


    /**
     * Returns a list of capture jobs.
     * When EPCIS events are added through the capture interface, the capture process can run asynchronously. If the payload
     * is syntactically correct and the client is allowed to call `/capture`, the server returns a `202` HTTP response code.
     * This endpoint returns all capture jobs that were created and supports pagination.
     *
     * @param router            router
     * @param xmlCaptureService xmlCaptureService
     */
    void registerGetCaptureHandler(Router router, XMLCaptureCoreService xmlCaptureService) {
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
        logger.info("[GET /epcis/capture] - router added");
    }

    /**
     * Returns information about the capture job.
     *
     * @param router            router
     * @param xmlCaptureService xmlCaptureService
     */
    void registerGetCaptureIDHandler(Router router, XMLCaptureCoreService xmlCaptureService) {
        router.get("/epcis/capture/:captureID").handler(routingContext -> {
            if (!checkEPCISMinMaxVersion(routingContext))
                return;
            xmlCaptureService.postCaptureJob(routingContext, routingContext.pathParam("captureID"));
        });
        logger.info("[GET /epcis/capture/:captureID] - router added");
    }

    /**
     * Synchronous capture interface for a single EPCIS event.
     * An individual EPCIS event can be created by making a `POST` request on the `/events` resource.
     * Alternatively, the client can also use the `/capture` interface and capture a single event.
     *
     * @param router            router
     * @param xmlCaptureService xmlCaptureService
     */
    void registerPostEventsHandler(Router router, XMLCaptureCoreService xmlCaptureService) {
        router.post("/epcis/events").consumes("*/xml").blockingHandler(routingContext -> {
            if (!isEqualHeader(routingContext, "GS1-EPCIS-Version"))
                return;
            if (!isEqualHeader(routingContext, "GS1-CBV-Version"))
                return;
            xmlCaptureService.postEvent(routingContext);
        });
        logger.info("[POST /epcis/events] - router added");
    }

    /**
     * Optional endpoint that allows on-demand release of any resources associated with `nextPageToken`.
     *
     * @param router
     */
    void registerDeletePageToken(Router router) {
        router.delete("/epcis/nextPageToken/:token").handler(routingContext -> {
            UUID uuid = UUID.fromString(routingContext.pathParam("token"));
            Page page = captureIDPageMap.remove(uuid);
            try {
                page.getTimer().cancel();
            } catch (Exception e) {

            }
            if (page != null) {
                routingContext.response().setStatusCode(204).end();
            } else {
                EPCISException e = new EPCISException("There is no page with token: " + uuid.toString());
                HTTPUtil.sendQueryResults(routingContext.response(), e, null, null, null, e.getClass(), 404);
            }
        });
        logger.info("[DELETE /nextPageToken/:token] - router added");
    }

    /**
     * non-standard to provide validation service
     *
     * @param router            router
     * @param xmlCaptureService xmlCaptureService
     */
    void registerValidationHandler(Router router, XMLCaptureCoreService xmlCaptureService) {
        router.post("/epcis/validation").consumes("*/xml").handler(xmlCaptureService::postValidationResult);
        logger.info("[GET /epcis/validation] - router added");
    }

    /**
     * non-standard service to provide 'ping'
     *
     * @param router router
     */
    void registerPingHandler(Router router) {
        router.get("/epcis").handler(routingContext -> {
            routingContext.response().setStatusCode(200).end();
        });
        logger.info("[GET /epcis] - router added");
    }
}
