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
public class MetadataHandler {

	public static void registerBaseHandler(Router router) {

		router.options("/epcis").consumes("application/xml").handler(routingContext -> {
			
		});

		router.options("/epcis").consumes("application/json").handler(routingContext -> {

		});

	}

	public static void registerCaptureHandler(Router router) {

		router.options("/epcis/capture").consumes("application/xml").handler(routingContext -> {

		});

		router.options("/epcis/capture").consumes("application/json").handler(routingContext -> {

		});
	}

	public static void registerCaptureIDHandler(Router router) {

		router.options("/epcis/capture/:captureID").consumes("application/xml").handler(routingContext -> {

		});

		router.options("/epcis/capture/:captureID").consumes("application/json").handler(routingContext -> {

		});
	}

	public static void registerEventsHandler(Router router) {

		router.options("/epcis/events").consumes("application/xml").handler(routingContext -> {

		});

		router.options("/epcis/events").consumes("application/json").handler(routingContext -> {

		});
	}

	public static void registerVocabualariesHandler(Router router) {

		router.options("/epcis/vocabularies").consumes("application/xml").handler(routingContext -> {

		});

		router.options("/epcis/vocabularies").consumes("application/json").handler(routingContext -> {

		});

	}

	public static void registerGetEventHandler(Router router) {

		router.options("/epcis/events/:eventID").consumes("application/xml").handler(routingContext -> {

		});

		router.options("/epcis/events/:eventID").consumes("application/json").handler(routingContext -> {

		});

	}

	public static void registerGetVocabularyHandler(Router router) {

		router.options("/epcis/vocabularies/:vocabularyID").consumes("application/xml").handler(routingContext -> {

		});

		router.options("/epcis/vocabularies/:vocabularyID").consumes("application/json").handler(routingContext -> {

		});

	}

	public static void registerQueryHandler(Router router) {

		router.options("/epcis/query").consumes("application/xml").handler(routingContext -> {

		});

	}

	public static void registerGetEventTypesHandler(Router router) {
		router.options("/epcis/eventTypes").consumes("application/xml").handler(routingContext -> {

		});

		router.options("/epcis/eventTypes").consumes("application/json").handler(routingContext -> {

		});

	}

	public static void registerGetEPCsHandler(Router router) {
		router.options("/epcis/epcs").consumes("application/xml").handler(routingContext -> {

		});

		router.options("/epcis/epcs").consumes("application/json").handler(routingContext -> {

		});
	}

	public static void registerGetBizStepsHandler(Router router) {
		router.options("/epcis/bizSteps").consumes("application/xml").handler(routingContext -> {

		});

		router.options("/epcis/bizSteps").consumes("application/json").handler(routingContext -> {

		});

	}

	public static void registerGetBizLocationsHandler(Router router) {
		router.options("/epcis/bizLocations").consumes("application/xml").handler(routingContext -> {

		});

		router.options("/epcis/bizLocations").consumes("application/json").handler(routingContext -> {

		});

	}

	public static void registerGetReadPointsHandler(Router router) {
		router.options("/epcis/readPoints").consumes("application/xml").handler(routingContext -> {

		});

		router.options("/epcis/readPoints").consumes("application/json").handler(routingContext -> {

		});

	}

	public static void registerGetDispositionsHandler(Router router) {
		router.options("/epcis/dispositions").consumes("application/xml").handler(routingContext -> {

		});

		router.options("/epcis/dispositions").consumes("application/json").handler(routingContext -> {

		});

	}

	public static void registerGetEventTypeQueriesHandler(Router router) {
		router.options("/epcis/eventTypes/:eventType").consumes("application/xml").handler(routingContext -> {

		});

		router.options("/epcis/eventTypes/:eventType").consumes("application/json").handler(routingContext -> {

		});

	}

	public static void registerGetEPCQueriesHandler(Router router) {
		router.options("/epcis/epcs/:epc").consumes("application/xml").handler(routingContext -> {

		});

		router.options("/epcis/epcs/:epc").consumes("application/json").handler(routingContext -> {

		});

	}

	public static void registerGetBizStepQueriesHandler(Router router) {
		router.options("/epcis/bizSteps/:bizStep").consumes("application/xml").handler(routingContext -> {

		});

		router.options("/epcis/bizSteps/:bizStep").consumes("application/json").handler(routingContext -> {

		});

	}

	public static void registerGetBizLocationQueriesHandler(Router router) {
		router.options("/epcis/bizLocations/:bizLocation").consumes("application/xml").handler(routingContext -> {

		});

		router.options("/epcis/bizLocations/:bizLocation").consumes("application/json").handler(routingContext -> {

		});

	}

	public static void registerGetReadPointQueriesHandler(Router router) {
		router.options("/epcis/readPoints/:readPoint").consumes("application/xml").handler(routingContext -> {

		});

		router.options("/epcis/readPoints/:readPoint").consumes("application/json").handler(routingContext -> {

		});

	}

	public static void registerGetDispositionQueriesHandler(Router router) {
		router.options("/epcis/dispositions/:disposition").consumes("application/xml").handler(routingContext -> {

		});

		router.options("/epcis/dispositions/:disposition").consumes("application/json").handler(routingContext -> {

		});

	}

	public static void registerGetEventsWithEventType(Router router) {
		router.options("/epcis/eventTypes/:eventType/events").consumes("application/xml").handler(routingContext -> {

		});

		router.options("/epcis/eventTypes/:eventType/events").consumes("application/json").handler(routingContext -> {

		});

	}

	public static void registerGetEventsWithEPC(Router router) {
		router.options("/epcis/epcs/:epc/events").consumes("application/xml").handler(routingContext -> {

		});
		router.options("/epcis/epcs/:epc/events").consumes("application/json").handler(routingContext -> {

		});

	}

	public static void registerGetEventsWithBizStep(Router router) {
		router.options("/epcis/bizSteps/:bizStep/events").consumes("application/xml").handler(routingContext -> {

		});

		router.options("/epcis/bizSteps/:bizStep/events").consumes("application/json").handler(routingContext -> {

		});

	}

	public static void registerGetEventsWithBizLocation(Router router) {
		router.options("/epcis/bizLocations/:bizLocation/events").consumes("application/xml")
				.handler(routingContext -> {

				});

		router.options("/epcis/bizLocations/:bizLocation/events").consumes("application/json")
				.handler(routingContext -> {

				});

	}

	public static void registerGetEventsWithReadPoint(Router router) {
		router.options("/epcis/readPoints/:readPoint/events").consumes("application/xml").handler(routingContext -> {

		});

		router.options("/epcis/readPoints/:readPoint/events").consumes("application/json").handler(routingContext -> {

		});

	}

	public static void registerGetEventsWithDisposition(Router router) {
		router.options("/epcis/dispositions/:disposition/events").consumes("application/xml")
				.handler(routingContext -> {

				});

		router.options("/epcis/dispositions/:disposition/events").consumes("application/json")
				.handler(routingContext -> {

				});

	}

	public static void registerGetVocabulariesWithEPC(Router router) {
		router.options("/epcis/epcs/:epc/vocabularies").consumes("application/xml").handler(routingContext -> {

		});

		router.options("/epcis/epcs/:epc/vocabularies").consumes("application/json").handler(routingContext -> {

		});

	}

	public static void registerGetVocabulariesWithBizLocation(Router router) {
		router.options("/epcis/bizLocations/:bizLocation/vocabularies").consumes("application/xml")
				.handler(routingContext -> {

				});

		router.options("/epcis/bizLocations/:bizLocation/vocabularies").consumes("application/json")
				.handler(routingContext -> {

				});

	}

	public static void registerGetVocabulariesWithReadPoint(Router router) {
		router.options("/epcis/readPoints/:readPoint/vocabularies").consumes("application/xml")
				.handler(routingContext -> {

				});

		router.options("/epcis/readPoints/:readPoint/vocabularies").consumes("application/json")
				.handler(routingContext -> {

				});

	}

	public static void registerGetQueriesHandler(Router router) {

		router.options("/epcis/queries").consumes("application/xml").handler(routingContext -> {

		});

		router.options("/epcis/queries").consumes("application/json").handler(routingContext -> {

		});

	}

	public static void registerNamedQueryHandler(Router router) {

		router.options("/epcis/queries/:queryName").consumes("application/xml").handler(routingContext -> {

		});

		router.options("/epcis/queries/:queryName").consumes("application/json").handler(routingContext -> {

		});

	}

	public static void registerGetEventsWithNamedQuery(Router router) {
		router.options("/epcis/queries/:queryName/events").consumes("application/xml").handler(routingContext -> {

		});

		router.options("/epcis/queries/:queryName/events").consumes("application/json").handler(routingContext -> {

		});

	}

	public static void registerGetVocabulariesWithNamedQuery(Router router) {
		router.options("/epcis/queries/:queryName/vocabularies").consumes("application/xml").handler(routingContext -> {

		});

		router.options("/epcis/queries/:queryName/vocabularies").consumes("application/json")
				.handler(routingContext -> {

				});

	}

	public static void registerGetPostSubscriptions(Router router) {
		router.options("/epcis/queries/:queryName/subscriptions").consumes("application/xml")
				.handler(routingContext -> {

				});
		router.options("/epcis/queries/:queryName/subscriptions").consumes("application/json")
				.handler(routingContext -> {

				});

	}

	public static void registerGetDeleteSubscription(Router router) {
		router.options("/epcis/queries/:queryName/subscriptions/:subscriptionID").consumes("application/xml")
				.handler(routingContext -> {

				});
		router.options("/epcis/queries/:queryName/subscriptions/:subscriptionID").consumes("application/json")
				.handler(routingContext -> {

				});

	}
}
