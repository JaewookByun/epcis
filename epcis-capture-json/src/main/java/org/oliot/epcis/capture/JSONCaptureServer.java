package org.oliot.epcis.capture;

import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.oliot.epcis.common.Metadata;
import org.oliot.epcis.model.exception.ValidationException;
import org.oliot.epcis.transaction.Transaction;
import org.oliot.epcis.unit_converter.UnitConverter;

import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.json.schema.Schema;

import static org.oliot.epcis.capture.BootstrapUtil.configureServer;

import java.util.HashMap;

/**
 * Copyright (C) 2020-2022. (Jaewook Byun) all rights reserved.
 * <p>
 * Oliot EPCIS X is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * Among various modules, epcis-capture-json acts as a server to receive
 * JSON-formatted EPCIS documents to capture events in the documents into an
 * EPCIS repository.
 * <p>
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr
 *         <p>
 *         Associate Director, Auto-ID Labs, KAIST, bjw0829@kaist.ac.kr
 */
public class JSONCaptureServer extends AbstractVerticle {

	public static JsonObject configuration = null;
	public static MongoClient mClient;

	public static Schema jsonValidator;

	public static UnitConverter unitConverter;
	public static int numOfVerticles;
	public static int GS1_CAPTURE_limit = 10;
	// public static int GS1_CAPTURE_file_size_limit = 40960;
	public static int GS1_CAPTURE_file_size_limit = 400000;
	public static String host;
	public static int port = 8083;

	public static JsonObject context;

	public static HashMap<String, Integer> gcpLength;

	public static Logger logger = Logger.getLogger(JSONCaptureServer.class);

	public static boolean isSkipValidation = false;

	public static void monitoring(HttpServerRequest request, String api) {
		if (request != null) {
			String remoteAddr = request.getHeader("X-FORWARDED-FOR");
			if (remoteAddr == null || remoteAddr.equals("")) {
				remoteAddr = request.remoteAddress().host();
				mClient.insert("monitoring",
						new JsonObject().put("time", System.currentTimeMillis()).put("ip", remoteAddr).put("api", api),
						e -> {
						});
				logger.debug(System.currentTimeMillis() + "\t" + remoteAddr + "\t" + api);
			}
		}
	}

	@Override
	public void start(Promise<Void> startPromise) {
		final HttpServer server = vertx.createHttpServer();
		final Router router = Router.router(vertx);
		final EventBus eventBus = vertx.eventBus();

		JSONCaptureService jsonCaptureService = new JSONCaptureService();

		router.route().handler(CorsHandler.create("*").allowedHeader("Access-Control-Allow-Credentials")
				.allowedHeader("Access-Control-Allow-Origin").allowedHeader("Access-Control-Allow-Headers")
				.allowedHeader("Content-Type").allowedMethod(io.vertx.core.http.HttpMethod.GET)
				.allowedMethod(io.vertx.core.http.HttpMethod.POST).allowedMethod(io.vertx.core.http.HttpMethod.OPTIONS)
				.allowedHeader("Access-Control-Request-Method")).handler(BodyHandler.create());

		router.get("/epcis").handler(routingContext -> {
			monitoring(routingContext.request(), "ping");
			routingContext.response().setStatusCode(200).end();
		});

		router.post("/epcis/capture").consumes("*/json").handler(routingContext -> {
			monitoring(routingContext.request(), "capture(json)");
			try {
				Transaction tx = new Transaction(routingContext.request().getHeader("GS1-Capture-Error-Behaviour"));
				eventBus.send("txStart", tx.getJson());
				jsonCaptureService.post(routingContext, eventBus, tx);
			} catch (RuntimeException e) {
				JSONCaptureServer.logger.info(e.getMessage());
				if (!routingContext.response().closed()) {
					routingContext.response().setStatusCode(400).end(e.getMessage());
				}
			}
		});

		
		router.get("/epcis/capture").handler(routingContext -> {
			monitoring(routingContext.request(), "captureIDs(json)");
			try {
				jsonCaptureService.postCaptureJob(routingContext);
			} catch (Exception e) {
				JSONCaptureServer.logger.info(e.getMessage());
				Throwable cause = e.getCause();
				if (cause instanceof ValidationException) {
					ValidationException v = (ValidationException) e.getCause();
					routingContext.response().setStatusCode(400).end(e.getMessage() + " : " + v.getReason());
				} else {
					routingContext.response().setStatusCode(400).end(e.getMessage());
				}
			}
		});
		
		router.get("/epcis/capture/:captureID").handler(routingContext -> {
			monitoring(routingContext.request(), "captureID(json)");
			try {
				String captureID = routingContext.pathParam("captureID");
				jsonCaptureService.postCaptureJob(routingContext, captureID);
			} catch (Exception e) {
				JSONCaptureServer.logger.info(e.getMessage());
				Throwable cause = e.getCause();
				if (cause instanceof ValidationException) {
					ValidationException v = (ValidationException) e.getCause();
					routingContext.response().setStatusCode(400).end(e.getMessage() + " : " + v.getReason());
				} else {
					routingContext.response().setStatusCode(400).end(e.getMessage());
				}
			}
		});

		router.post("/epcis/validation").consumes("*/json").handler(routingContext -> {
			monitoring(routingContext.request(), "validation(json)");
			jsonCaptureService.postValidationResult(routingContext);
		});

		router.options("/epcis/capture").consumes("*/json").handler(routingContext -> {
			monitoring(routingContext.request(), "capture-option");
			routingContext.response().putHeader("GS1-EPCIS-version", "2.0").putHeader("GS1-CBV-version", "2.0")
					.putHeader("GS1-Vendor-version", Metadata.vendorVersion).putHeader("GS1-EPCIS-min", ">=1.0")
					.putHeader("GS1-EPCIS-max", "2.0")
					.putHeader("GS1-EPCIS-extensions", "cbvmda=<urn:epcglobal:cbv:mda>")
					.putHeader("GS1-CBV-extensions", "cbvmda=<urn:epcglobal:cbv:mda>")
					.putHeader("GS1-CAPTURE-limit", String.valueOf(JSONCaptureServer.GS1_CAPTURE_limit))
					.putHeader("GS1-CAPTURE-file-size-limit",
							String.valueOf(JSONCaptureServer.GS1_CAPTURE_file_size_limit))
					.end();
		});

		server.requestHandler(router).listen(port);

		eventBus.consumer("txStart", msg -> {
			JsonObject tx = (JsonObject) msg.body();
			mClient.insert("Tx", tx, result -> {
				if (!result.succeeded()) {
					throw new RuntimeException(result.toString());
				}
			});
			logger.debug(tx + "start");
		});

		eventBus.consumer("txSuccess", msg -> {
			JsonObject tx = (JsonObject) msg.body();
			JsonObject q = new JsonObject().put("_id", tx.getString("_id"));
			mClient.findOneAndUpdate("Tx", q, new JsonObject().put("$set", new JsonObject().put("running", false)
					.put("success", true).put("finishedAt", System.currentTimeMillis())), h -> {
						if (!h.succeeded()) {
							throw new RuntimeException(h.toString());
						}
					});
			logger.debug(tx + " success");
		});

		eventBus.consumer("txProceed", msg -> {
			JsonObject tx = (JsonObject) msg.body();
			JsonObject q = new JsonObject().put("_id", tx.getString("_id"));
			mClient.findOneAndUpdate("Tx", q, new JsonObject().put("$set", new JsonObject().put("running", false)
					.put("success", false).put("errorType", tx.getString("errorType"))
					.put("errorMessage", tx.getString("errorMessage")).put("finishedAt", System.currentTimeMillis())),
					h -> {
						if (!h.succeeded()) {
							throw new RuntimeException(h.toString());
						}
					});
			logger.debug(tx + " proceed");
		});

		eventBus.consumer("txRollback", msg -> {
			JsonObject tx = (JsonObject) msg.body();

			JsonObject q = new JsonObject().put("_id", tx.getString("_id"));

			mClient.findOneAndUpdate("Tx", q, new JsonObject().put("$set", new JsonObject().put("success", false)
					.put("errorType", tx.getString("errorType")).put("errorMessage", tx.getString("errorMessage"))),
					h -> {
						logger.debug(tx + " rollback starts");
						if (!h.succeeded()) {
							throw new RuntimeException(h.toString());
						} else {
							JsonObject q2 = new JsonObject().put("_tx", tx.getString("_id"));
							mClient.removeDocument("EventData", q2, h1 -> {
								if (!h.succeeded()) {
									throw new RuntimeException(h1.toString());
								} else {
									mClient.findOneAndUpdate("Tx", q, new JsonObject().put("$set", new JsonObject()
											.put("running", false).put("finishedAt", System.currentTimeMillis())),
											h2 -> {
												if (!h.succeeded()) {
													throw new RuntimeException(h2.toString());
												} else {
													logger.debug(tx + " rollback done");
												}
											});
								}
							});

						}
					});

		});

	}

	public static void main(String[] args) {
		Logger.getRootLogger().setLevel(Level.OFF);

		Vertx vertx = Vertx.vertx();
		configureServer(vertx, args);

		// vertx.deployVerticle(new JSONCaptureServer());

		DeploymentOptions dOptions = new DeploymentOptions().setInstances(numOfVerticles);
		vertx.deployVerticle("org.oliot.epcis.capture.JSONCaptureServer", dOptions);
	}
}
