package org.oliot.epcis.server;

import javax.xml.validation.Validator;

import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpMethod;
import org.apache.log4j.Logger;
import org.bson.Document;

import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

import org.oliot.epcis.capture.common.CommonHandler;
import org.oliot.epcis.capture.common.TransactionManager;
import org.oliot.epcis.capture.json.JSONCaptureService;
import org.oliot.epcis.capture.json.JSONCaptureServiceHandler;
import org.oliot.epcis.capture.xml.XMLCaptureService;
import org.oliot.epcis.capture.xml.XMLCaptureServiceHandler;
import org.oliot.epcis.converter.data.bson_to_json.EPCISDocumentConverter;
import org.oliot.epcis.converter.unit.UnitConverter;
import org.oliot.epcis.pagination.Page;
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

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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

	public static Validator xmlValidator;
	public static io.vertx.rxjava.json.schema.Validator jsonValidator;
	public static int numOfVerticles;
	public static String host;
	public static int port = 8080;
	public static Logger logger = Logger.getLogger(EPCISServer.class);

	// pagination
	public static ConcurrentHashMap<UUID, Page> eventPageMap = new ConcurrentHashMap<UUID, Page>();
	public static ConcurrentHashMap<UUID, Page> vocabularyPageMap = new ConcurrentHashMap<UUID, Page>();
	public static ConcurrentHashMap<UUID, Page> captureIDPageMap = new ConcurrentHashMap<UUID, Page>();

	protected final XMLCaptureService xmlCaptureCoreService = new XMLCaptureService();
	protected final SOAPQueryService soapQueryService = new SOAPQueryService();
	protected final RESTQueryService restQueryService = new RESTQueryService();
	public static org.oliot.epcis.converter.data.bson_to_json.EPCISDocumentConverter bsonToJsonConverter = new EPCISDocumentConverter();
	public SubscriptionManager subscriptionManager;
	public static TriggerEngine triggerEngine = new TriggerEngine();

	protected final JSONCaptureService jsonCaptureCoreService = new JSONCaptureService();

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
	
	public EPCISServer() {
		// TODO: The registration needs to be moved to the constructor so that they can be overriden. 
		SOAPQueryServiceHandler.registerQueryHandler(soapQueryService);
		SOAPQueryServiceHandler.registerPaginationHandler(soapQueryService);
		RESTQueryServiceHandler.registerGetEventsHandler(restQueryService);
	}

	@Override
	public void start(Promise<Void> startPromise) {
		final HttpServer server = vertx.createHttpServer();
		final Router router = Router.router(vertx);
		final EventBus eventBus = vertx.eventBus();
		clientForSubscriptionCallback = java.net.http.HttpClient.newHttpClient();
		setRouter(router);
		loadStaticResponses();

		registerCommonHandler(router);
		registerTDTServiceHandler(router);
		registerSOAPQueryServiceHandler(router, eventBus);
		registerCaptureServiceHandler(router, eventBus);
		registerXMLCaptureServiceHandler(router, eventBus);
		registerJSONCaptureServiceHandler(router, eventBus);
		registerSubscriptionMonitorHandler(router, eventBus);

		startHandlers(router);
		server.requestHandler(router).listen(port);

		new DynamicResource(vertx).start();
	}

	public void registerSubscriptionMonitorHandler(Router router, EventBus eventBus) {
		SubscriptionMonitor.registerEchoHandler(router, eventBus);
		SubscriptionMonitor.addSubscriptionMonitorWebSocket(router, eventBus);
	}

	private void registerCommonHandler(Router router) {
		CommonHandler.registerBaseHandler(router);
		CommonHandler.registerCaptureHandler(router);
		CommonHandler.registerCaptureIDHandler(router);
		CommonHandler.registerEventsHandler(router);
		CommonHandler.registerPingHandler(router);
		CommonHandler.registerDeleteHandler(router);
		CommonHandler.registerStatisticsHandler(router);
	}

	private void registerTDTServiceHandler(Router router) {
		TagDataTranslationServiceHandler.registerPostEventsHandler(router);
	}

	private void registerSOAPQueryServiceHandler(Router router, EventBus eventBus) {
		//SOAPQueryServiceHandler.registerQueryHandler(router, soapQueryService);
		//SOAPQueryServiceHandler.registerPaginationHandler(router, soapQueryService);
		TriggerEngine.registerTransactionStartHandler(eventBus);
	}

	protected void registerCaptureServiceHandler(Router router, EventBus eventBus) {
		TransactionManager.registerTransactionStartHandler(eventBus);
		TransactionManager.registerTransactionSuccessHandler(eventBus);
		TransactionManager.registerTransactionProceedHandler(eventBus);
		TransactionManager.registerTransactionRollBackHandler(eventBus);
	}

	protected void registerXMLCaptureServiceHandler(Router router, EventBus eventBus) {
		XMLCaptureServiceHandler.registerPostCaptureHandler(router, xmlCaptureCoreService, eventBus);
		XMLCaptureServiceHandler.registerGetCaptureIDHandler(router, xmlCaptureCoreService);
		XMLCaptureServiceHandler.registerPostEventsHandler(router, xmlCaptureCoreService, eventBus);
		XMLCaptureServiceHandler.registerGetCaptureHandler(router, xmlCaptureCoreService);
		XMLCaptureServiceHandler.registerDeletePageToken(router);
		XMLCaptureServiceHandler.registerValidationHandler(router, xmlCaptureCoreService);
	}

	protected void registerJSONCaptureServiceHandler(Router router, EventBus eventBus) {
		JSONCaptureServiceHandler.registerPostCaptureHandler(router, jsonCaptureCoreService, eventBus);
		JSONCaptureServiceHandler.registerGetCaptureIDHandler(router, jsonCaptureCoreService);
		JSONCaptureServiceHandler.registerPostEventsHandler(router, jsonCaptureCoreService, eventBus);
		JSONCaptureServiceHandler.registerGetCaptureHandler(router, jsonCaptureCoreService);
		JSONCaptureServiceHandler.registerDeletePageToken(router);
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
				.handler(CorsHandler.create().addOrigin("*").allowedHeader("Access-Control-Allow-Credentials")
						.allowedHeader("GS1-EPCIS-Version").allowedHeader("GS1-CBV-Version")
						.allowedHeader("GS1-EPCIS-Max").allowedHeader("GS1-EPCIS-Min")
						.allowedHeader("GS1-CBV-Max").allowedHeader("GS1-CBV-Min")
						.allowedHeader("GS1-EPC-Format").allowedHeader("GS1-CBV-XML-Format")
						.allowedHeader("GS1-EPCIS-Capture-Error-Behaviour").allowedHeader("Access-Control-Allow-Origin")
						.allowedHeader("Access-Control-Allow-Headers").allowedHeader("Content-Type")
						.allowedMethod(HttpMethod.GET).allowedMethod(HttpMethod.POST).allowedMethod(HttpMethod.OPTIONS)
						.allowedMethod(HttpMethod.DELETE).allowedHeader("Access-Control-Request-Method"))
				.handler(BodyHandler.create());
	}
	
	// We keep a list of handlers registered at different points of the startup process. These handlers will be registered with the router
	// in start() method. We do this because a later-registered handler may override a previously registered one. 
	private static HashMap<HandlerKey, Handler<RoutingContext>> handlerMap = new HashMap<>();
	
	public static class HandlerKey {
		public String httpMethod;
		public String path;
		public String contentType;
		public HandlerKey(String httpMethod, String path, String contentType) {
			super();
			this.httpMethod = httpMethod;
			this.path = path;
			this.contentType = contentType;
		}
		@Override
		public int hashCode() {
			return Objects.hash(contentType, httpMethod, path);
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			HandlerKey other = (HandlerKey) obj;
			return Objects.equals(contentType, other.contentType) && Objects.equals(httpMethod, other.httpMethod)
					&& Objects.equals(path, other.path);
		}
		@Override
		public String toString() {
			return "HandlerKey [httpMethod=" + httpMethod + ", path=" + path + ", contentType=" + contentType + "]";
		}
	}
	
	public static void registerHandler(HandlerKey key, Handler<RoutingContext> handler) {
		handlerMap.put(key, handler);
	}
	
	public static Handler<RoutingContext> getHandler(HandlerKey key){
		return handlerMap.get(key);
	}

	public static void registerHandler(String method, String path, String contentType, Handler<RoutingContext> handler) {
		registerHandler(new HandlerKey(method, path, contentType), handler);
	}
	
	public static Handler<RoutingContext> getHandler(String method, String path, String contentType){
		return getHandler(new HandlerKey(method, path, contentType));
	}

	// we delay the start of handlers until all handlers has been registered
	private void startHandlers(Router router) {
		// register the handlers with the router
		Route route = null;
		for(HandlerKey key: handlerMap.keySet()) {
			Handler<RoutingContext> handler = handlerMap.get(key);
			System.out.println("Starting handler "+handler+ " for "+key);
			switch(key.httpMethod) {
				case "get":
					route = router.get(key.path).path(key.path).consumes(key.contentType).handler(handler);
					break;
				case "options":
					route = router.options(key.path).path(key.path).consumes(key.contentType).handler(handler);
					break;
				case "post":
					route = router.post(key.path).path(key.path).consumes(key.contentType).handler(handler);
					break;
				case "put":
					route = router.put(key.path).path(key.path).consumes(key.contentType).handler(handler);
					break;
				case "delete":
					route = router.delete(key.path).path(key.path).consumes(key.contentType).handler(handler);
					break;
				case "patch":
					route = router.patch(key.path).path(key.path).consumes(key.contentType).handler(handler);
					break;
				case "trace":
					route = router.trace(key.path).path(key.path).consumes(key.contentType).handler(handler);
					break;
			}
		}
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
