package org.oliot.epcis.server;

import javax.xml.validation.Validator;

import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpMethod;
import org.apache.log4j.Logger;
import org.bson.Document;

import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

import org.oliot.epcis.capture.common.TransactionManager;
import org.oliot.epcis.capture.json.JSONCaptureService;
import org.oliot.epcis.capture.json.JSONCaptureServiceHandler;
import org.oliot.epcis.capture.xml.XMLCaptureService;
import org.oliot.epcis.capture.xml.XMLCaptureServiceHandler;
import org.oliot.epcis.common.CommonHandler;
import org.oliot.epcis.common.MetadataHandler;
import org.oliot.epcis.converter.data.bson_to_json.EPCISDocumentConverter;
import org.oliot.epcis.converter.unit.UnitConverter;
import org.oliot.epcis.pagination.DataPage;
import org.oliot.epcis.pagination.NamedQueryPage;
import org.oliot.epcis.pagination.ResourcePage;
import org.oliot.epcis.query.RESTQueryService;
import org.oliot.epcis.query.RESTQueryServiceHandler;
import org.oliot.epcis.query.SOAPQueryService;
import org.oliot.epcis.query.SOAPQueryServiceHandler;
import org.oliot.epcis.query.SubscriptionManager;
import org.oliot.epcis.query.SubscriptionMonitor;
import org.oliot.epcis.query.TriggerEngine;
import org.oliot.epcis.query.response.StaticResponseBuilder;
import org.oliot.epcis.resource.DynamicResource;
import org.oliot.epcis.tdt.TagDataTranslationServiceHandler;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Copyright (C) 2020-2023. (Jaewook Byun) all rights reserved.
 * <p>
 * This project is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * EPCISServer is executable as Java application
 * <p>
 * java -jar EPCISServer.jar configuration.json
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr, Associate Director, Auto-ID Labs, Korea,
 *         bjw0829@gmail.com
 */
public class EPCISServer extends AbstractVerticle {

	public static JsonObject configuration = null;

	public static MongoClient mClient;
	public static MongoDatabase mDatabase;
	public static MongoCollection<Document> mVocCollection;
	public static MongoCollection<Document> mEventCollection;
	public static MongoCollection<Document> mTxCollection;
	public static MongoCollection<Document> mSubscriptionCollection;
	public static MongoCollection<Document> mNamedQueryCollection;

	public static Validator xmlValidator;
	public static io.vertx.rxjava.json.schema.Validator jsonValidator;
	public static int numOfVerticles;
	public static String host;
	public static int port = 8080;
	public static Logger logger = Logger.getLogger(EPCISServer.class);

	// pagination
	public static ConcurrentHashMap<UUID, DataPage> eventPageMap = new ConcurrentHashMap<UUID, DataPage>();
	public static ConcurrentHashMap<UUID, DataPage> vocabularyPageMap = new ConcurrentHashMap<UUID, DataPage>();
	public static ConcurrentHashMap<UUID, DataPage> captureIDPageMap = new ConcurrentHashMap<UUID, DataPage>();
	public static ConcurrentHashMap<UUID, ResourcePage> epcsPageMap = new ConcurrentHashMap<UUID, ResourcePage>();
	public static ConcurrentHashMap<UUID, ResourcePage> bizStepsPageMap = new ConcurrentHashMap<UUID, ResourcePage>();
	public static ConcurrentHashMap<UUID, ResourcePage> bizLocationsPageMap = new ConcurrentHashMap<UUID, ResourcePage>();
	public static ConcurrentHashMap<UUID, ResourcePage> readPointsPageMap = new ConcurrentHashMap<UUID, ResourcePage>();
	public static ConcurrentHashMap<UUID, ResourcePage> dispositionsPageMap = new ConcurrentHashMap<UUID, ResourcePage>();
	public static ConcurrentHashMap<UUID, NamedQueryPage> namedQueriesPageMap = new ConcurrentHashMap<UUID, NamedQueryPage>();

	final XMLCaptureService xmlCaptureCoreService = new XMLCaptureService();
	final SOAPQueryService soapQueryService = new SOAPQueryService();
	final RESTQueryService restQueryService = new RESTQueryService();
	public static org.oliot.epcis.converter.data.bson_to_json.EPCISDocumentConverter bsonToJsonConverter = new EPCISDocumentConverter();
	public SubscriptionManager subscriptionManager;
	public static TriggerEngine triggerEngine = new TriggerEngine();

	final JSONCaptureService jsonCaptureCoreService = new JSONCaptureService();

	public static UnitConverter unitConverter;

	public static String getStandardVersionResponse;
	public static String getVendorVersionResponse;
	public static String getQueryNamesResponse;
	public static String subscribeResponse;

	public static java.net.http.HttpClient clientForSubscriptionCallback;

	// resource monitor
	public static MongoClient monitoringClient;
	public static MongoDatabase monitoringDatabase;
	public static MongoCollection<Document> monitoringVocCollection;
	public static MongoCollection<Document> monitoringEventCollection;
	public static MongoCollection<Document> monitoringTxCollection;
	public static MongoCollection<Document> monitoringSubscriptionCollection;
	public static AtomicLong resourceDiscoveryInterval;

	@Override
	public void start(Promise<Void> startPromise) {
		final HttpServer server = vertx.createHttpServer();
		final Router router = Router.router(vertx);
		final EventBus eventBus = vertx.eventBus();
		clientForSubscriptionCallback = java.net.http.HttpClient.newHttpClient();
		setRouter(router);
		loadStaticResponses();

		registerMetadataHandler(router);
		registerCommonHandler(router);
		registerTDTServiceHandler(router);
		registerCaptureServiceHandler(router, eventBus);
		registerXMLCaptureServiceHandler(router, eventBus);
		registerSOAPQueryServiceHandler(router, eventBus);
		registerJSONCaptureServiceHandler(router, eventBus);
		registerSubscriptionMonitorHandler(router, eventBus);
		registerRESTQueryServiceHandler(router, eventBus);

		server.requestHandler(router).listen(port);

		new DynamicResource(vertx).start();
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

	public void registerRESTQueryServiceHandler(Router router, EventBus eventBus) {
		RESTQueryServiceHandler.registerGetEventsHandler(router, soapQueryService, restQueryService);
		RESTQueryServiceHandler.registerGetVocabulariesHandler(router, soapQueryService, restQueryService);
		RESTQueryServiceHandler.registerGetEventHandler(router, restQueryService);
		RESTQueryServiceHandler.registerGetVocabularyHandler(router, restQueryService);

		RESTQueryServiceHandler.registerGetEventTypes(router);
		RESTQueryServiceHandler.registerGetEPCs(router, soapQueryService, restQueryService);
		RESTQueryServiceHandler.registerGetBizSteps(router, soapQueryService, restQueryService);
		RESTQueryServiceHandler.registerGetBizLocations(router, soapQueryService, restQueryService);
		RESTQueryServiceHandler.registerGetReadPoints(router, soapQueryService, restQueryService);
		RESTQueryServiceHandler.registerGetDispositions(router, soapQueryService, restQueryService);

		RESTQueryServiceHandler.registerGetEventTypeQueries(router);
		RESTQueryServiceHandler.registerGetEPCQueries(router);
		RESTQueryServiceHandler.registerGetBizStepQueries(router);
		RESTQueryServiceHandler.registerGetBizLocationQueries(router);
		RESTQueryServiceHandler.registerGetReadPointQueries(router);
		RESTQueryServiceHandler.registerGetDispositionQueries(router);

		RESTQueryServiceHandler.registerGetEventsWithEventTypeHandler(router, soapQueryService, restQueryService);
		RESTQueryServiceHandler.registerGetEventsWithEPCHandler(router, soapQueryService, restQueryService);
		RESTQueryServiceHandler.registerGetEventsWithBizStepHandler(router, soapQueryService, restQueryService);
		RESTQueryServiceHandler.registerGetEventsWithBizLocationHandler(router, soapQueryService, restQueryService);
		RESTQueryServiceHandler.registerGetEventsWithReadPointHandler(router, soapQueryService, restQueryService);
		RESTQueryServiceHandler.registerGetEventsWithDispositionHandler(router, soapQueryService, restQueryService);

		RESTQueryServiceHandler.registerGetVocabulariesWithEPCHandler(router, soapQueryService, restQueryService);
		RESTQueryServiceHandler.registerGetVocabulariesWithBizLocationHandler(router, soapQueryService,
				restQueryService);
		RESTQueryServiceHandler.registerGetVocabulariesWithReadPointHandler(router, soapQueryService, restQueryService);

		RESTQueryServiceHandler.registerPostQueryHandler(router, soapQueryService, restQueryService);
		RESTQueryServiceHandler.registerGetQueryHandler(router, soapQueryService, restQueryService);
		RESTQueryServiceHandler.registerDeleteQueryHandler(router, soapQueryService, restQueryService);
		RESTQueryServiceHandler.registerGetQueriesHandler(router, soapQueryService, restQueryService);
		RESTQueryServiceHandler.registerGetEventsWithNamedQueryHandler(router, soapQueryService, restQueryService);
		RESTQueryServiceHandler.registerGetVocabulariesWithNamedQueryHandler(router, soapQueryService,
				restQueryService);
		
		RESTQueryServiceHandler.registerPostSubscriptionHandler(router, soapQueryService, restQueryService);
	}

	public void registerSubscriptionMonitorHandler(Router router, EventBus eventBus) {
		SubscriptionMonitor.registerEchoHandler(router, eventBus);
		SubscriptionMonitor.addSubscriptionMonitorWebSocket(router, eventBus);
	}

	private void registerCommonHandler(Router router) {

		CommonHandler.registerPingHandler(router);
		CommonHandler.registerDeleteHandler(router);
		CommonHandler.registerStatisticsHandler(router);
		CommonHandler.registerDeletePageTokenHandler(router);
	}

	private void registerTDTServiceHandler(Router router) {
		TagDataTranslationServiceHandler.registerPostEventsHandler(router);
	}

	private void registerSOAPQueryServiceHandler(Router router, EventBus eventBus) {
		SOAPQueryServiceHandler.registerQueryHandler(router, soapQueryService);
		// SOAPQueryServiceHandler.registerPollHandler(router, soapQueryService);
		// SOAPQueryServiceHandler.registerPollWithEventTypeHandler(router,
		// soapQueryService);
		TriggerEngine.registerTransactionStartHandler(eventBus);
	}

	private void registerCaptureServiceHandler(Router router, EventBus eventBus) {
		TransactionManager.registerTransactionStartHandler(eventBus);
		TransactionManager.registerTransactionSuccessHandler(eventBus);
		TransactionManager.registerTransactionProceedHandler(eventBus);
		TransactionManager.registerTransactionRollBackHandler(eventBus);
	}

	private void registerXMLCaptureServiceHandler(Router router, EventBus eventBus) {
		XMLCaptureServiceHandler.registerPostCaptureHandler(router, xmlCaptureCoreService, eventBus);
		XMLCaptureServiceHandler.registerGetCaptureIDHandler(router, xmlCaptureCoreService);
		XMLCaptureServiceHandler.registerPostEventsHandler(router, xmlCaptureCoreService, eventBus);
		XMLCaptureServiceHandler.registerGetCaptureHandler(router, xmlCaptureCoreService);
		XMLCaptureServiceHandler.registerValidationHandler(router, xmlCaptureCoreService);
	}

	private void registerJSONCaptureServiceHandler(Router router, EventBus eventBus) {
		JSONCaptureServiceHandler.registerPostCaptureHandler(router, jsonCaptureCoreService, eventBus);
		JSONCaptureServiceHandler.registerGetCaptureIDHandler(router, jsonCaptureCoreService);
		JSONCaptureServiceHandler.registerPostEventsHandler(router, jsonCaptureCoreService, eventBus);
		JSONCaptureServiceHandler.registerGetCaptureHandler(router, jsonCaptureCoreService);
		JSONCaptureServiceHandler.registerValidationHandler(router, jsonCaptureCoreService);
	}

	private void loadStaticResponses() {
		try {
			getStandardVersionResponse = StaticResponseBuilder.getStandardVersion();
			getVendorVersionResponse = StaticResponseBuilder.getVendorVersion();
			getQueryNamesResponse = StaticResponseBuilder.getQueryNames();
			subscribeResponse = StaticResponseBuilder.subscribe();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
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
		SubscriptionManager sm = null;
		BootstrapUtil.configureServer(vertx, args, sm);
		vertx.deployVerticle(new EPCISServer());

		// DeploymentOptions dOptions = new
		// DeploymentOptions().setInstances(numOfVerticles);
		// vertx.deployVerticle("org.oliot.epcis.capture.XMLCaptureServer", dOptions);
	}
}
