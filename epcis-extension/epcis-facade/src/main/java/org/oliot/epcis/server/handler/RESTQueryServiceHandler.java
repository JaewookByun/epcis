package org.oliot.epcis.server.handler;

import io.vertx.ext.web.Router;

/**
 * Copyright (C) 2020-2024. (Jaewook Byun) all rights reserved.
 * <p>
 * This project is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 *
 * 
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr, Associate Director, Auto-ID Labs, Korea,
 *         bjw0829@gmail.com
 */
public class RESTQueryServiceHandler {

	public static void registerGetEventsHandler(Router router) {

		router.get("/epcis/events").consumes("application/xml").handler(routingContext -> {

		});

		router.get("/epcis/events").consumes("application/json").handler(routingContext -> {

		});
	}

	public static void registerGetVocabulariesHandler(Router router) {

		router.get("/epcis/vocabularies").consumes("application/xml").handler(routingContext -> {

		});

		router.get("/epcis/vocabularies").consumes("application/json").handler(routingContext -> {

		});
	}

	public static void registerGetEventHandler(Router router) {
		router.get("/epcis/events/:eventID").consumes("application/xml").handler(routingContext -> {

		});

		router.get("/epcis/events/:eventID").consumes("application/json").handler(routingContext -> {

		});
	}

	public static void registerGetEPCQueriesHandler(Router router) {
		router.get("/epcis/events/:eventID").consumes("application/xml").handler(routingContext -> {

		});

		router.get("/epcis/events/:eventID").consumes("application/json").handler(routingContext -> {

		});
	}

	public static void registerGetVocabularyHandler(Router router) {
		router.get("/epcis/vocabularies/:vocabularyID").consumes("application/xml").handler(routingContext -> {

		});

		router.get("/epcis/vocabularies/:vocabularyID").consumes("application/json").handler(routingContext -> {

		});
	}

	public static void registerGetEventTypes(Router router) {
		router.get("/epcis/eventTypes").consumes("application/xml").handler(routingContext -> {

		});

		router.get("/epcis/eventTypes").consumes("application/json").handler(routingContext -> {

		});
	}

	public static void registerGetEPCs(Router router) {
		router.get("/epcis/epcs").consumes("application/xml").handler(routingContext -> {

		});

		router.get("/epcis/epcs").consumes("application/json").handler(routingContext -> {

		});
	}

	public static void registerGetBizSteps(Router router) {
		router.get("/epcis/bizSteps").consumes("application/xml").handler(routingContext -> {

		});

		router.get("/epcis/bizSteps").consumes("application/json").handler(routingContext -> {

		});
	}

	public static void registerGetBizLocations(Router router) {
		router.get("/epcis/bizLocations").consumes("application/xml").handler(routingContext -> {

		});

		router.get("/epcis/bizLocations").consumes("application/json").handler(routingContext -> {

		});
	}

	public static void registerGetReadPoints(Router router) {
		router.get("/epcis/readPoints").consumes("application/xml").handler(routingContext -> {

		});

		router.get("/epcis/readPoints").consumes("application/json").handler(routingContext -> {

		});
	}

	public static void registerGetDispositions(Router router) {
		router.get("/epcis/dispositions").consumes("application/xml").handler(routingContext -> {

		});

		router.get("/epcis/dispositions").consumes("application/json").handler(routingContext -> {

		});
	}

	public static void registerGetEventTypeQueries(Router router) {
		router.get("/epcis/eventTypes/:eventType").consumes("application/xml").handler(routingContext -> {

		});

		router.get("/epcis/eventTypes/:eventType").consumes("application/json").handler(routingContext -> {

		});
	}

	public static void registerGetEPCQueries(Router router) {
		String resourceType = "epc";

		router.get("/epcis/" + resourceType + "s/:" + resourceType).consumes("application/xml")
				.handler(routingContext -> {

				});

		router.get("/epcis/" + resourceType + "s/:" + resourceType).consumes("application/json")
				.handler(routingContext -> {

				});
	}

	public static void registerGetBizStepQueries(Router router) {
		String resourceType = "bizStep";

		router.get("/epcis/" + resourceType + "s/:" + resourceType).consumes("application/xml")
				.handler(routingContext -> {

				});

		router.get("/epcis/" + resourceType + "s/:" + resourceType).consumes("application/json")
				.handler(routingContext -> {

				});
	}

	public static void registerGetBizLocationQueries(Router router) {
		String resourceType = "bizLocation";

		router.get("/epcis/" + resourceType + "s/:" + resourceType).consumes("application/xml")
				.handler(routingContext -> {

				});

		router.get("/epcis/" + resourceType + "s/:" + resourceType).consumes("application/json")
				.handler(routingContext -> {

				});
	}

	public static void registerGetReadPointQueries(Router router) {
		String resourceType = "readPoint";

		router.get("/epcis/" + resourceType + "s/:" + resourceType).consumes("application/xml")
				.handler(routingContext -> {

				});

		router.get("/epcis/" + resourceType + "s/:" + resourceType).consumes("application/json")
				.handler(routingContext -> {

				});
	}

	public static void registerGetDispositionQueries(Router router) {
		String resourceType = "disposition";

		router.get("/epcis/" + resourceType + "s/:" + resourceType).consumes("application/xml")
				.handler(routingContext -> {

				});

		router.get("/epcis/" + resourceType + "s/:" + resourceType).consumes("application/json")
				.handler(routingContext -> {

				});
	}

	public static void registerGetEventsWithEventTypeHandler(Router router) {

		String resourceType = "eventType";

		router.get("/epcis/" + resourceType + "s/:" + resourceType + "/events").consumes("application/xml")
				.handler(routingContext -> {

				});

		router.get("/epcis/" + resourceType + "s/:" + resourceType + "/events").consumes("application/json")
				.handler(routingContext -> {

				});

	}

	public static void registerGetEventsWithEPCHandler(Router router) {

		String resourceType = "epc";

		router.get("/epcis/" + resourceType + "s/:" + resourceType + "/events").consumes("application/xml")
				.handler(routingContext -> {

				});

		router.get("/epcis/" + resourceType + "s/:" + resourceType + "/events").consumes("application/json")
				.handler(routingContext -> {

				});

	}

	public static void registerGetEventsWithBizStepHandler(Router router) {

		String resourceType = "bizStep";

		router.get("/epcis/" + resourceType + "s/:" + resourceType + "/events").consumes("application/xml")
				.handler(routingContext -> {

				});

		router.get("/epcis/" + resourceType + "s/:" + resourceType + "/events").consumes("application/json")
				.handler(routingContext -> {

				});

	}

	public static void registerGetEventsWithBizLocationHandler(Router router) {

		String resourceType = "bizLocation";

		router.get("/epcis/" + resourceType + "s/:" + resourceType + "/events").consumes("application/xml")
				.handler(routingContext -> {

				});

		router.get("/epcis/" + resourceType + "s/:" + resourceType + "/events").consumes("application/json")
				.handler(routingContext -> {

				});

	}

	public static void registerGetEventsWithReadPointHandler(Router router) {

		String resourceType = "readPoint";

		router.get("/epcis/" + resourceType + "s/:" + resourceType + "/events").consumes("application/xml")
				.handler(routingContext -> {

				});

		router.get("/epcis/" + resourceType + "s/:" + resourceType + "/events").consumes("application/json")
				.handler(routingContext -> {

				});

	}

	public static void registerGetEventsWithDispositionHandler(Router router) {

		String resourceType = "disposition";

		router.get("/epcis/" + resourceType + "s/:" + resourceType + "/events").consumes("application/xml")
				.handler(routingContext -> {

				});
		router.get("/epcis/" + resourceType + "s/:" + resourceType + "/events").consumes("application/json")
				.handler(routingContext -> {

				});

	}

	public static void registerGetVocabulariesWithEPCHandler(Router router) {

		String resourceType = "epc";

		router.get("/epcis/" + resourceType + "s/:" + resourceType + "/vocabularies").consumes("application/xml")
				.handler(routingContext -> {

				});

		router.get("/epcis/" + resourceType + "s/:" + resourceType + "/vocabularies").consumes("application/json")
				.handler(routingContext -> {

				});

	}

	public static void registerGetVocabulariesWithBizLocationHandler(Router router) {

		String resourceType = "bizLocation";

		router.get("/epcis/" + resourceType + "s/:" + resourceType + "/vocabularies").consumes("application/xml")
				.handler(routingContext -> {

				});

		router.get("/epcis/" + resourceType + "s/:" + resourceType + "/vocabularies").consumes("application/json")
				.handler(routingContext -> {

				});

	}

	public static void registerGetVocabulariesWithReadPointHandler(Router router) {

		String resourceType = "readPoint";

		router.get("/epcis/" + resourceType + "s/:" + resourceType + "/vocabularies").consumes("application/xml")
				.handler(routingContext -> {

				});

		router.get("/epcis/" + resourceType + "s/:" + resourceType + "/vocabularies").consumes("application/json")
				.handler(routingContext -> {

				});

	}

	public static void registerPostQueryHandler(Router router) {

		router.post("/epcis/queries").consumes("application/xml").handler(routingContext -> {

		});

		router.post("/epcis/queries").consumes("application/json").handler(routingContext -> {

		});

	}

	public static void registerGetQueryHandler(Router router) {

		router.get("/epcis/queries/:queryName").consumes("application/xml").handler(routingContext -> {

		});

		router.get("/epcis/queries/:queryName").consumes("application/json").handler(routingContext -> {

		});

	}

	public static void registerDeleteQueryHandler(Router router) {

		router.delete("/epcis/queries/:queryName").consumes("application/xml").handler(routingContext -> {

		});

		router.delete("/epcis/queries/:queryName").consumes("application/json").handler(routingContext -> {

		});

	}

	public static void registerGetQueriesHandler(Router router) {
		router.get("/epcis/queries").consumes("application/xml").handler(routingContext -> {

		});

		router.get("/epcis/queries").consumes("application/json").handler(routingContext -> {

		});

	}

	public static void registerGetEventsWithNamedQueryHandler(Router router) {

		router.get("/epcis/queries/:queryName/events").consumes("application/xml").handler(routingContext -> {

		});

		router.get("/epcis/queries/:queryName/events").handler(routingContext -> {

		});

	}

	public static void registerGetVocabulariesWithNamedQueryHandler(Router router) {

		router.get("/epcis/queries/:queryName/vocabularies").consumes("application/xml").handler(routingContext -> {

		});

		router.get("/epcis/queries/:queryName/vocabularies").consumes("application/json").handler(routingContext -> {

		});

	}

	public static void registerPostSubscriptionHandler(Router router) {

		router.post("/epcis/queries/:queryName/subscriptions").consumes("application/xml").handler(routingContext -> {

		});

		router.post("/epcis/queries/:queryName/subscriptions").handler(routingContext -> {

		});

	}

	public static void registerDeleteSubscriptionHandler(Router router) {

		router.delete("/epcis/queries/:queryName/subscriptions/:subscriptionID").consumes("application/xml")
				.handler(routingContext -> {

				});
		router.delete("/epcis/queries/:queryName/subscriptions/:subscriptionID").consumes("application/json")
				.handler(routingContext -> {

				});

	}

	public static void registerGetSubscriptionsHandler(Router router) {
		router.get("/epcis/queries/:queryName/subscriptions").consumes("application/xml").handler(routingContext -> {

		});

		router.get("/epcis/queries/:queryName/subscriptions").handler(routingContext -> {

		});

	}
}
