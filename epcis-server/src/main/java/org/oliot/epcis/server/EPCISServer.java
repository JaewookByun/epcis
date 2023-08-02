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
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

import org.oliot.epcis.capture.common.CaptureMetadataHandler;
import org.oliot.epcis.capture.common.TransactionManager;
import org.oliot.epcis.capture.xml.XMLCaptureService;
import org.oliot.epcis.capture.xml.XMLCaptureServiceHandler;
import org.oliot.epcis.converter.unit.UnitConverter;
import org.oliot.epcis.pagination.Page;
import org.oliot.epcis.query.SOAPQueryMetadataHandler;
import org.oliot.epcis.query.SOAPQueryService;
import org.oliot.epcis.query.SOAPQueryServiceHandler;
import org.oliot.epcis.query.TriggerEngine;
import org.oliot.epcis.query.response.StaticResponseBuilder;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

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
	public static int numOfVerticles;
	public static String host;
	public static int port = 8080;
	public static Logger logger = Logger.getLogger(EPCISServer.class);

	// pagination
	public static ConcurrentHashMap<UUID, Page> eventPageMap = new ConcurrentHashMap<UUID, Page>();
	public static ConcurrentHashMap<UUID, Page> vocabularyPageMap = new ConcurrentHashMap<UUID, Page>();
	public static ConcurrentHashMap<UUID, Page> captureIDPageMap = new ConcurrentHashMap<UUID, Page>();

	final XMLCaptureService xmlCaptureCoreService = new XMLCaptureService();
	final SOAPQueryService soapQueryService = new SOAPQueryService();
	public static TriggerEngine triggerEngine = new TriggerEngine();

	public static UnitConverter unitConverter;

	public static String getStandardVersionResponse;
	public static String getVendorVersionResponse;
	public static String getQueryNamesResponse;
	public static String subscribeResponse;

	public static WebClient clientForSubscriptionCallback;

	@Override
	public void start(Promise<Void> startPromise) {
		final HttpServer server = vertx.createHttpServer();
		final Router router = Router.router(vertx);
		final EventBus eventBus = vertx.eventBus();
		clientForSubscriptionCallback = WebClient.create(vertx);
		setRouter(router);
		loadStaticResponses();

		registerCaptureServiceHandler(router, eventBus);
		registerXMLCaptureServiceHandler(router, eventBus);
		registerSOAPQueryServiceHandler(router, eventBus);

		server.requestHandler(router).listen(port);
	}

	private void registerSOAPQueryServiceHandler(Router router, EventBus eventBus) {
		SOAPQueryMetadataHandler.registerBaseHandler(router);
		SOAPQueryServiceHandler.registerPingHandler(router);
		SOAPQueryServiceHandler.registerDeleteHandler(router);
		SOAPQueryServiceHandler.registerQueryHandler(router, soapQueryService);
		SOAPQueryServiceHandler.registerPaginationHandler(router, soapQueryService);
		SOAPQueryServiceHandler.registerEchoHandler(router);
		TriggerEngine.registerTransactionStartHandler(eventBus);
	}
	
	private void registerCaptureServiceHandler(Router router, EventBus eventBus) {
		CaptureMetadataHandler.registerBaseHandler(router);
		CaptureMetadataHandler.registerCaptureHandler(router);
		CaptureMetadataHandler.registerCaptureIDHandler(router);
		CaptureMetadataHandler.registerEventsHandler(router);

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
		XMLCaptureServiceHandler.registerDeletePageToken(router);
		XMLCaptureServiceHandler.registerValidationHandler(router, xmlCaptureCoreService);
		XMLCaptureServiceHandler.registerPingHandler(router);
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
						.allowedHeader("Access-Control-Allow-Origin").allowedHeader("Access-Control-Allow-Headers")
						.allowedHeader("Content-Type").allowedMethod(HttpMethod.GET).allowedMethod(HttpMethod.POST)
						.allowedMethod(HttpMethod.OPTIONS).allowedMethod(HttpMethod.DELETE)
						.allowedHeader("Access-Control-Request-Method"))
				.handler(BodyHandler.create());
	}

	public static void main(String[] args) {

		BootstrapUtil.configureServer(args);

		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new EPCISServer());

		// DeploymentOptions dOptions = new
		// DeploymentOptions().setInstances(numOfVerticles);
		// vertx.deployVerticle("org.oliot.epcis.capture.XMLCaptureServer", dOptions);
	}
}
