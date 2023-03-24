package org.oliot.epcis.capture;

import javax.xml.validation.Validator;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.oliot.epcis.model.exception.ValidationException;
import org.oliot.epcis.unit_converter.UnitConverter;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.oliot.epcis.transaction.Transaction;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import org.oliot.epcis.util.ObservableSubscriber;

import static org.oliot.epcis.capture.BootstrapUtil.configureServer;

/**
 * Copyright (C) 2020-2022. (Jaewook Byun) all rights reserved.
 * <p>
 * Oliot EPCIS X is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * Among various modules, epcis-capture-xml acts as a server to receive
 * XML-formatted EPCIS documents to capture events in the documents into an
 * EPCIS repository.
 * <p>
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr
 *         <p>
 *         Associate Director, Auto-ID Labs, KAIST, bjw0829@kaist.ac.kr
 */
public class XMLCaptureServer extends AbstractVerticle {

	public static JsonObject configuration = null;
	public static MongoClient mClient;
	public static MongoDatabase mDatabase;
	public static MongoCollection<Document> mMonitoringCollection;
	public static MongoCollection<Document> mVocCollection;
	public static MongoCollection<Document> mEventCollection;
	public static MongoCollection<Document> mTxCollection;

	public static Validator xmlValidator;
	public static Validator xmlMasterDataValidator;
	public static UnitConverter unitConverter;
	public static int numOfVerticles;
	public static int GS1_CAPTURE_limit = 10;
	public static int GS1_CAPTURE_file_size_limit = 40960;
	public static String host;
	public static int port = 8080;

	public static Logger logger = Logger.getLogger(XMLCaptureServer.class);

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
		XMLCaptureService xmlCaptureService = new XMLCaptureService();
		final EventBus eventBus = vertx.eventBus();

		router.route().handler(CorsHandler.create("*").allowedHeader("Access-Control-Allow-Credentials")
				.allowedHeader("Access-Control-Allow-Origin").allowedHeader("Access-Control-Allow-Headers")
				.allowedHeader("Content-Type").allowedMethod(io.vertx.core.http.HttpMethod.GET)
				.allowedMethod(io.vertx.core.http.HttpMethod.POST).allowedMethod(io.vertx.core.http.HttpMethod.OPTIONS)
				.allowedHeader("Access-Control-Request-Method")).handler(BodyHandler.create());

		router.get("/epcis").handler(routingContext -> {
			monitoring(routingContext.request(), "ping");
			routingContext.response().setStatusCode(200).end();
		});

		router.post("/epcis/capture").consumes("*/xml").handler(routingContext -> {
			monitoring(routingContext.request(), "capture(xml)");
			try {
				Transaction tx = new Transaction(routingContext.request().getHeader("GS1-Capture-Error-Behaviour"));
				eventBus.send("txStart", tx.getJson());
				xmlCaptureService.post(routingContext, eventBus, tx);
			} catch (RuntimeException e) {
				XMLCaptureServer.logger.info(e.getMessage());
				if (!routingContext.response().closed()) {
					routingContext.response().setStatusCode(400).end(e.getMessage());
				}
			}
		});
		
		router.get("/epcis/capture").handler(routingContext -> {
			monitoring(routingContext.request(), "captureIDs(xml)");
			try {
				xmlCaptureService.postCaptureJob(routingContext);
			} catch (Exception e) {
				XMLCaptureServer.logger.info(e.getMessage());
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
			monitoring(routingContext.request(), "captureID(xml)");
			try {
				String captureID = routingContext.pathParam("captureID");
				xmlCaptureService.postCaptureJob(routingContext, captureID);
			} catch (Exception e) {
				XMLCaptureServer.logger.info(e.getMessage());
				Throwable cause = e.getCause();
				if (cause instanceof ValidationException) {
					ValidationException v = (ValidationException) e.getCause();
					routingContext.response().setStatusCode(400).end(e.getMessage() + " : " + v.getReason());
				} else {
					routingContext.response().setStatusCode(400).end(e.getMessage());
				}
			}
		});

		router.post("/epcis/vocabularyCapture").consumes("*/xml").handler(routingContext -> {
			monitoring(routingContext.request(), "capture(xml-vocabulary)");
			try {
				xmlCaptureService.postMasterData(routingContext, eventBus);
			} catch (RuntimeException e) {
				XMLCaptureServer.logger.info(e.getMessage());
				routingContext.response().setStatusCode(400).end(e.getMessage());
			}
		});

		router.post("/epcis/events").consumes("*/xml").blockingHandler(routingContext -> {
			monitoring(routingContext.request(), "capture(xml-single)");
			try {
				xmlCaptureService.postEvent(routingContext, eventBus);
			} catch (RuntimeException e) {
				XMLCaptureServer.logger.info(e.getMessage());
				Throwable cause = e.getCause();
				if (cause instanceof ValidationException) {
					ValidationException v = (ValidationException) e.getCause();
					routingContext.response().setStatusCode(400).end(e.getMessage() + " : " + v.getReason());
				} else {
					routingContext.response().setStatusCode(400).end(e.getMessage());
				}
			}
		});

		router.post("/epcis/validation").consumes("*/xml").handler(routingContext -> {
			monitoring(routingContext.request(), "validation(xml)");
			xmlCaptureService.postValidationResult(routingContext);
		});

		server.requestHandler(router).listen(port);

		eventBus.consumer("txStart", msg -> {
			// GS1-Capture-Error-Behaviour = rollback
			// running=true, success=true , still capturing, no error still
			// running=true, success=false , error occurs, and rollback is in progress
			// running=false, success=true , all events are captured
			// running=false, success=false, all events are rejected

			// GS1-Capture-Error-Behaviour = proceed
			// running=true, success=true, still capturing, no error still
			// running=true, success=false,
			JsonObject tx = (JsonObject) msg.body();
			ObservableSubscriber<InsertOneResult> subscriber = new ObservableSubscriber<InsertOneResult>();
			mTxCollection.insertOne(Transaction.toDocument(tx)).subscribe(subscriber);
			try {
				subscriber.await();
			} catch (Throwable e) {
				throw new RuntimeException(e.getMessage());
			}
			logger.debug(tx);
		});

		eventBus.consumer("txSuccess", msg -> {
			JsonObject tx = (JsonObject) msg.body();
			ObservableSubscriber<UpdateResult> subscriber = new ObservableSubscriber<UpdateResult>();
			mTxCollection
					.updateOne(new Document().append("_id", new ObjectId(tx.getString("_id"))),
							new Document()
									.append("$set",
											new Document().append("running", false).append("success", true)
													.append("finishedAt", System.currentTimeMillis())))
					.subscribe(subscriber);
			try {
				subscriber.await();
			} catch (Throwable e) {
				throw new RuntimeException(e.getMessage());
			}
			logger.debug(tx);
		});

		eventBus.consumer("txProceed", msg -> {
			JsonObject tx = (JsonObject) msg.body();
			ObservableSubscriber<UpdateResult> subscriber = new ObservableSubscriber<UpdateResult>();
			mTxCollection.updateOne(new Document().append("_id", new ObjectId(tx.getString("_id"))),
					new Document().append("$set",
							new Document().append("running", false).append("success", false)
									.append("errorType", tx.getString("errorType"))
									.append("errorMessage", tx.getString("errorMessage"))
									.append("finishedAt", System.currentTimeMillis())))
					.subscribe(subscriber);
			try {
				subscriber.await();
			} catch (Throwable e) {
				throw new RuntimeException(e.getMessage());
			}
			logger.debug(tx + " proceed");
		});

		eventBus.consumer("txRollback", msg -> {
			JsonObject tx = (JsonObject) msg.body();
			ObservableSubscriber<UpdateResult> subscriber = new ObservableSubscriber<UpdateResult>();
			mTxCollection
					.updateOne(new Document().append("_id", new ObjectId(tx.getString("_id"))),
							new Document().append("$set",
									new Document().append("success", false)
											.append("errorType", tx.getString("errorType"))
											.append("errorMessage", tx.getString("errorMessage"))))
					.subscribe(subscriber);
			try {
				subscriber.await();
			} catch (Throwable e) {
				throw new RuntimeException(e.getMessage());
			}
			logger.debug(tx + " rollback starts");
			ObservableSubscriber<DeleteResult> subscriber2 = new ObservableSubscriber<DeleteResult>();
			mEventCollection.deleteMany(new Document().append("_tx", new ObjectId(tx.getString("_id")))).subscribe(subscriber2);
			try {
				subscriber2.await();
			} catch (Throwable e) {
				throw new RuntimeException(e.getMessage());
			}
			subscriber = new ObservableSubscriber<UpdateResult>();
			mTxCollection
					.updateOne(new Document().append("_id",  new ObjectId(tx.getString("_id"))), new Document().append("$set",
							new Document().append("running", false).append("finishedAt", System.currentTimeMillis())))
					.subscribe(subscriber);
			try {
				subscriber.await();
			} catch (Throwable e) {
				throw new RuntimeException(e.getMessage());
			}
			logger.debug(tx + " rollback done");
		});

	}

	public static void main(String[] args) {
		Logger.getRootLogger().setLevel(Level.OFF);

		Vertx vertx = Vertx.vertx();
		configureServer(vertx, args);

		// vertx.deployVerticle(new XMLCaptureServer());

		DeploymentOptions dOptions = new DeploymentOptions().setInstances(numOfVerticles);
		vertx.deployVerticle("org.oliot.epcis.capture.XMLCaptureServer", dOptions);
	}
}
