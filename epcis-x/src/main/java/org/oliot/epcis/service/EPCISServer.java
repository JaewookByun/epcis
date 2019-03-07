package org.oliot.epcis.service;

import org.bson.BsonDocument;
import org.oliot.epcis.service.capture.CaptureService;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class EPCISServer extends AbstractVerticle{

	public static MongoClient mClient;
	public static MongoDatabase mDatabase;
	public static MongoCollection<BsonDocument> eventCollection;

	@Override
	public void start(Future<Void> future) throws Exception {
		HttpServer server = vertx.createHttpServer();

		Router router = Router.router(vertx);

		router.route().handler(BodyHandler.create());

		router.post("/epcis/CaptureService").consumes("*/xml").handler(routingContext -> {

			HttpServerResponse response = routingContext.response();
			response.setChunked(true);
			String body = routingContext.getBodyAsString();
			CaptureService cs = new CaptureService();
			cs.post(body);
			routingContext.response().end();
		});

		server.requestHandler(router).listen(8080);
	}
	
	public static void main(String[] args) {
		// Initialize Backend
		mClient = new MongoClient();
		mDatabase = mClient.getDatabase("epcis");
		eventCollection = mDatabase.getCollection("EventData", BsonDocument.class);

		Vertx vertx = Vertx.vertx();
		DeploymentOptions options = new DeploymentOptions().setInstances(16);
		vertx.deployVerticle("org.oliot.epcis.service.EPCISServer", options);
	}
}
