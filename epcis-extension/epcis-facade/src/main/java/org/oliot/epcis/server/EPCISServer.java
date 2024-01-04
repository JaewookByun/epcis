package org.oliot.epcis.server;

import org.oliot.epcis.server.handler.JSONCaptureServiceHandler;
import org.oliot.epcis.server.handler.MetadataHandler;
import org.oliot.epcis.server.handler.RESTQueryServiceHandler;
import org.oliot.epcis.server.handler.SOAPQueryServiceHandler;
import org.oliot.epcis.server.handler.XMLCaptureServiceHandler;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

/**
 * Copyright (C) 2020-2024. (Jaewook Byun) all rights reserved.
 * <p>
 * This project is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr, Associate Director, Auto-ID Labs, Korea,
 *         bjw0829@gmail.com
 */
public class EPCISServer extends AbstractVerticle {

	private int port = 80;

	@Override
	public void start(Promise<Void> startPromise) {
		final HttpServer server = vertx.createHttpServer();
		final Router router = Router.router(vertx);

		setRouter(router);

		registerMetadataHandler(router);
		registerXMLCaptureServiceHandler(router);
		registerJSONCaptureServiceHandler(router);
		registerSOAPQueryServiceHandler(router);
		registerRESTQueryServiceHandler(router);

		server.requestHandler(router).listen(port);
		System.out.println("Server turns on with a port: " + port);
	}

	public void registerMetadataHandler(Router router) {
		MetadataHandler.registerBaseHandler(router);
		MetadataHandler.registerCaptureHandler(router);
		MetadataHandler.registerCaptureIDHandler(router);
		MetadataHandler.registerEventsHandler(router);
		MetadataHandler.registerVocabualariesHandler(router);
		MetadataHandler.registerGetEventHandler(router);
		MetadataHandler.registerGetVocabularyHandler(router);
		MetadataHandler.registerQueryHandler(router);

		MetadataHandler.registerGetEventTypesHandler(router);
		MetadataHandler.registerGetEventTypeQueriesHandler(router);
		MetadataHandler.registerGetEventsWithEventType(router);

		MetadataHandler.registerGetEPCsHandler(router);
		MetadataHandler.registerGetEPCQueriesHandler(router);
		MetadataHandler.registerGetEventsWithEPC(router);
		MetadataHandler.registerGetVocabulariesWithEPC(router);

		MetadataHandler.registerGetBizStepsHandler(router);
		MetadataHandler.registerGetBizStepQueriesHandler(router);
		MetadataHandler.registerGetEventsWithBizStep(router);

		MetadataHandler.registerGetBizLocationsHandler(router);
		MetadataHandler.registerGetBizLocationQueriesHandler(router);
		MetadataHandler.registerGetEventsWithBizLocation(router);
		MetadataHandler.registerGetVocabulariesWithBizLocation(router);

		MetadataHandler.registerGetReadPointsHandler(router);
		MetadataHandler.registerGetReadPointQueriesHandler(router);
		MetadataHandler.registerGetEventsWithReadPoint(router);
		MetadataHandler.registerGetVocabulariesWithReadPoint(router);

		MetadataHandler.registerGetDispositionsHandler(router);
		MetadataHandler.registerGetDispositionQueriesHandler(router);
		MetadataHandler.registerGetEventsWithDisposition(router);

		MetadataHandler.registerGetQueriesHandler(router);
		MetadataHandler.registerNamedQueryHandler(router);
		MetadataHandler.registerGetEventsWithNamedQuery(router);
		MetadataHandler.registerGetVocabulariesWithNamedQuery(router);
		MetadataHandler.registerGetPostSubscriptions(router);
		MetadataHandler.registerGetDeleteSubscription(router);
	}

	private void registerXMLCaptureServiceHandler(Router router) {
		XMLCaptureServiceHandler.registerPostCaptureHandler(router);
		XMLCaptureServiceHandler.registerGetCaptureIDHandler(router);
		XMLCaptureServiceHandler.registerPostEventsHandler(router);
		XMLCaptureServiceHandler.registerGetCaptureHandler(router);
	}

	private void registerJSONCaptureServiceHandler(Router router) {
		JSONCaptureServiceHandler.registerPostCaptureHandler(router);
		JSONCaptureServiceHandler.registerGetCaptureIDHandler(router);
		JSONCaptureServiceHandler.registerPostEventsHandler(router);
		JSONCaptureServiceHandler.registerGetCaptureHandler(router);
	}

	private void registerSOAPQueryServiceHandler(Router router) {
		SOAPQueryServiceHandler.registerQueryHandler(router);
	}

	public void registerRESTQueryServiceHandler(Router router) {
		RESTQueryServiceHandler.registerGetEventsHandler(router);
		RESTQueryServiceHandler.registerGetVocabulariesHandler(router);
		RESTQueryServiceHandler.registerGetEventHandler(router);
		RESTQueryServiceHandler.registerGetVocabularyHandler(router);

		RESTQueryServiceHandler.registerGetEventTypes(router);
		RESTQueryServiceHandler.registerGetEPCs(router);
		RESTQueryServiceHandler.registerGetBizSteps(router);
		RESTQueryServiceHandler.registerGetBizLocations(router);
		RESTQueryServiceHandler.registerGetReadPoints(router);
		RESTQueryServiceHandler.registerGetDispositions(router);

		RESTQueryServiceHandler.registerGetEventTypeQueries(router);
		RESTQueryServiceHandler.registerGetEPCQueries(router);
		RESTQueryServiceHandler.registerGetBizStepQueries(router);
		RESTQueryServiceHandler.registerGetBizLocationQueries(router);
		RESTQueryServiceHandler.registerGetReadPointQueries(router);
		RESTQueryServiceHandler.registerGetDispositionQueries(router);

		RESTQueryServiceHandler.registerGetEventsWithEventTypeHandler(router);
		RESTQueryServiceHandler.registerGetEventsWithEPCHandler(router);
		RESTQueryServiceHandler.registerGetEventsWithBizStepHandler(router);
		RESTQueryServiceHandler.registerGetEventsWithBizLocationHandler(router);
		RESTQueryServiceHandler.registerGetEventsWithReadPointHandler(router);
		RESTQueryServiceHandler.registerGetEventsWithDispositionHandler(router);

		RESTQueryServiceHandler.registerGetVocabulariesWithEPCHandler(router);
		RESTQueryServiceHandler.registerGetVocabulariesWithBizLocationHandler(router);
		RESTQueryServiceHandler.registerGetVocabulariesWithReadPointHandler(router);

		RESTQueryServiceHandler.registerPostQueryHandler(router);
		RESTQueryServiceHandler.registerGetQueryHandler(router);
		RESTQueryServiceHandler.registerDeleteQueryHandler(router);
		RESTQueryServiceHandler.registerGetQueriesHandler(router);
		RESTQueryServiceHandler.registerGetEventsWithNamedQueryHandler(router);
		RESTQueryServiceHandler.registerGetVocabulariesWithNamedQueryHandler(router);

		RESTQueryServiceHandler.registerPostSubscriptionHandler(router);
		RESTQueryServiceHandler.registerDeleteSubscriptionHandler(router);
		RESTQueryServiceHandler.registerGetSubscriptionsHandler(router);
	}

	private void setRouter(Router router) {
		router.route()
				.handler(CorsHandler.create().addOrigin("*").allowedHeader("Content-Type")
						.allowedHeader("Access-Control-Allow-Credentials").allowedHeader("GS1-EPCIS-Version")
						.allowedHeader("GS1-CBV-Version").allowedHeader("GS1-EPCIS-Max").allowedHeader("GS1-EPCIS-Min")
						.allowedHeader("GS1-CBV-Max").allowedHeader("GS1-CBV-Min").allowedHeader("GS1-EPC-Format")
						.allowedHeader("GS1-CBV-XML-Format").allowedHeader("GS1-EPCIS-Capture-Error-Behaviour")
						.allowedHeader("Access-Control-Allow-Origin").allowedHeader("Access-Control-Allow-Headers")
						.allowedHeader("Access-Control-Expose-Headers").allowedHeader("Access-Control-Request-Method")
						.allowedMethod(HttpMethod.GET).allowedMethod(HttpMethod.POST).allowedMethod(HttpMethod.OPTIONS)
						.allowedMethod(HttpMethod.DELETE))
				.handler(BodyHandler.create());
	}

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new EPCISServer());
	}
}
