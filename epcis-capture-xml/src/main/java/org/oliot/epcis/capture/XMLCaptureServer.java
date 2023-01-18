package org.oliot.epcis.capture;

import javax.xml.validation.Validator;

import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpMethod;
import org.apache.log4j.Logger;
import org.bson.Document;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;

import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

import org.oliot.epcis.pagination.Page;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.oliot.epcis.capture.BootstrapUtil.configureServer;

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
public class XMLCaptureServer extends AbstractVerticle {

    public static JsonObject configuration = null;
    public static MongoClient mClient;
    public static MongoDatabase mDatabase;
    public static MongoCollection<Document> mVocCollection;
    public static MongoCollection<Document> mEventCollection;
    public static MongoCollection<Document> mTxCollection;
    public static Validator xmlValidator;
    public static int numOfVerticles;
    public static String host;
    public static int port = 8080;
    public static Logger logger = Logger.getLogger(XMLCaptureServer.class);
    public static ConcurrentHashMap<UUID, Page> captureIDPageMap = new ConcurrentHashMap<UUID, Page>();
    
    final XMLCaptureMetadataHandler xmlCaptureMetadataService = new XMLCaptureMetadataHandler();
    final XMLCaptureCoreServiceHandler xmlCaptureCoreServiceHandler = new XMLCaptureCoreServiceHandler();
    final TransactionManager transactionManager = new TransactionManager();
    final XMLCaptureCoreService xmlCaptureCoreService = new XMLCaptureCoreService();
    

    @Override
    public void start(Promise<Void> startPromise) {
        final HttpServer server = vertx.createHttpServer();
        final Router router = Router.router(vertx);
        final EventBus eventBus = vertx.eventBus();
        setRouter(router);
        xmlCaptureMetadataService.registerBaseHandler(router);
        xmlCaptureMetadataService.registerCaptureHandler(router);
        xmlCaptureMetadataService.registerCaptureIDHandler(router);
        xmlCaptureMetadataService.registerEventsHandler(router);

        xmlCaptureCoreServiceHandler.registerPostCaptureHandler(router, xmlCaptureCoreService, eventBus);
        xmlCaptureCoreServiceHandler.registerGetCaptureIDHandler(router, xmlCaptureCoreService);
        xmlCaptureCoreServiceHandler.registerPostEventsHandler(router, xmlCaptureCoreService);
        xmlCaptureCoreServiceHandler.registerGetCaptureHandler(router, xmlCaptureCoreService);
        xmlCaptureCoreServiceHandler.registerDeletePageToken(router);
        xmlCaptureCoreServiceHandler.registerValidationHandler(router, xmlCaptureCoreService);
        xmlCaptureCoreServiceHandler.registerPingHandler(router);

        transactionManager.registerTransactionStartHandler(eventBus);
        transactionManager.registerTransactionSuccessHandler(eventBus);
        transactionManager.registerTransactionProceedHandler(eventBus);
        transactionManager.registerTransactionRollBackHandler(eventBus);

        server.requestHandler(router).listen(port);

    }

    private void setRouter(Router router) {
        router.route().handler(CorsHandler.create().addOrigin("*").allowedHeader("Access-Control-Allow-Credentials")
                .allowedHeader("Access-Control-Allow-Origin").allowedHeader("Access-Control-Allow-Headers")
                .allowedHeader("Content-Type").allowedMethod(io.vertx.core.http.HttpMethod.GET)
                .allowedMethod(io.vertx.core.http.HttpMethod.POST).allowedMethod(io.vertx.core.http.HttpMethod.OPTIONS)
                .allowedMethod(HttpMethod.DELETE)
                .allowedHeader("Access-Control-Request-Method")).handler(BodyHandler.create());
    }


    public static void main(String[] args) {

        configureServer(args);

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new XMLCaptureServer());

        //DeploymentOptions dOptions = new DeploymentOptions().setInstances(numOfVerticles);
        //vertx.deployVerticle("org.oliot.epcis.capture.XMLCaptureServer", dOptions);
    }
}
