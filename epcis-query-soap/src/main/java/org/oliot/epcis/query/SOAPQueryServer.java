package org.oliot.epcis.query;

import io.vertx.core.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.oliot.epcis.unit_converter.UnitConverter;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;

import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

/**
 * Copyright (C) 2020-2021. (Jaewook Byun) all rights reserved.
 * <p>
 * Oliot EPCIS X is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * Among various modules, epcis-query-soap acts as a server to receive queries
 * to provide filtered, sorted, limited events or masterdata of interest inside
 * EPCIS repository.
 * <p>
 * 
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr
 *         <p>
 *         Associate Director, Auto-ID Labs, KAIST, bjw0829@kaist.ac.kr
 */
public class SOAPQueryServer extends AbstractVerticle {

	public static JsonObject configuration = null;
	public static MongoClient mClient;
	public static MongoDatabase mDatabase;
	public static MongoCollection<Document> mMonitoringCollection;
	public static MongoCollection<Document> mVocCollection;
	public static MongoCollection<Document> mEventCollection;

	public static UnitConverter unitConverter;
	public static int numOfVerticles;
	public static String host;
	public static int port = 8081;

	public static Logger logger = Logger.getLogger(SOAPQueryServer.class);

	public static void monitoring(HttpServerRequest request, String api) {
		if (request != null) {
			String remoteAddr = request.getHeader("X-FORWARDED-FOR");
			if (remoteAddr == null || remoteAddr.equals("")) {
				remoteAddr = request.remoteAddress().host();
				mMonitoringCollection.insertOne(new Document().append("time", System.currentTimeMillis())
						.append("ip", remoteAddr).append("api", api));
				logger.debug(System.currentTimeMillis() + "\t" + remoteAddr + "\t" + api);
			}
		}
	}

	@Override
	public void start(Promise<Void> startPromise) {
		final HttpServer server = vertx.createHttpServer();
		final Router router = Router.router(vertx);
		SOAPQueryService soapQueryService = new SOAPQueryService();

		router.route().handler(CorsHandler.create("*").allowedHeader("Access-Control-Allow-Credentials")
				.allowedHeader("Access-Control-Allow-Origin").allowedHeader("Access-Control-Allow-Headers")
				.allowedHeader("Content-Type").allowedMethod(io.vertx.core.http.HttpMethod.GET)
				.allowedMethod(io.vertx.core.http.HttpMethod.POST).allowedMethod(io.vertx.core.http.HttpMethod.OPTIONS)
				.allowedHeader("Access-Control-Request-Method")).handler(BodyHandler.create());

		router.get("/epcis").handler(routingContext -> {
			monitoring(routingContext.request(), "ping");
			routingContext.response().setStatusCode(200).end();
		});

		router.post("/epcis/query").consumes("*/xml").handler(routingContext -> {
			soapQueryService.run(routingContext.request(), routingContext.response().setChunked(true),
					routingContext.body().asString());
		});

		server.requestHandler(router).listen(port);
	}

	public static void main(String[] args) {
		Logger.getRootLogger().setLevel(Level.OFF);

		Vertx vertx = Vertx.vertx();
		BootstrapUtil.configureServer(vertx, args);

		// vertx.deployVerticle(new SOAPQueryServer());
		
		DeploymentOptions dOptions = new DeploymentOptions().setInstances(numOfVerticles);
		vertx.deployVerticle("org.oliot.epcis.query.SOAPQueryServer", dOptions);
	}
}
