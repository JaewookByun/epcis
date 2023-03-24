package org.oliot.epcis.query;

import io.vertx.core.*;

import java.util.HashMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.oliot.epcis.common.Metadata;
import org.oliot.epcis.unit_converter.UnitConverter;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;

import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

/**
 * Copyright (C) 2020-2022. (Jaewook Byun) all rights reserved.
 * <p>
 * Oliot EPCIS X is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * Among various modules, epcis-query-rest acts as a server to receive queries
 * to provide filtered, sorted, limited events of interest inside EPCIS
 * repository.
 * <p>
 * 
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr
 *         <p>
 *         Associate Director, Auto-ID Labs, KAIST, bjw0829@kaist.ac.kr
 */
public class RESTQueryServer extends AbstractVerticle {

	public static JsonObject configuration = null;
	public static MongoClient mClient;
	public static MongoDatabase mDatabase;
	public static MongoCollection<Document> mMonitoringCollection;
	public static MongoCollection<Document> mVocCollection;
	public static MongoCollection<Document> mEventCollection;

	public static MongoCollection<Document> mEventTypes;
	public static MongoCollection<Document> mEPCs;
	public static MongoCollection<Document> mBizSteps;
	public static MongoCollection<Document> mDispositions;
	public static MongoCollection<Document> mReadPoints;
	public static MongoCollection<Document> mBizLocations;

	public static UnitConverter unitConverter;
	public static int numOfVerticles;
	public static String host;
	public static int port = 8084;
	public static int wsPort = 8085;

	public static int GS1_CAPTURE_limit = 10;
	public static int GS1_CAPTURE_file_size_limit = 40960;

	public static HashMap<String, Integer> gcpLength;

	public static Logger logger = Logger.getLogger(RESTQueryServer.class);

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

	void registerGetResourcesHandler(Router router, RESTQueryService restQueryServce) {
		router.get("/epcis/resource/eventTypes").handler(routingContext -> {
			monitoring(routingContext.request(), "/eventTypes-resources");
			try {
				boolean requestScan = Boolean.parseBoolean(routingContext.queryParams().get("requestScan"));
				restQueryServce.getResources(routingContext.request(), routingContext.response(), requestScan,
						"eventTypes");
			} catch (Exception e) {
				restQueryServce.getResources(routingContext.request(), routingContext.response(), false, "eventTypes");
			}
		});
		router.get("/epcis/resource/epcs").handler(routingContext -> {
			monitoring(routingContext.request(), "/epcs-resources");
			try {
				boolean requestScan = Boolean.parseBoolean(routingContext.queryParams().get("requestScan"));
				restQueryServce.getResources(routingContext.request(), routingContext.response(), requestScan, "epcs");
			} catch (Exception e) {
				restQueryServce.getResources(routingContext.request(), routingContext.response(), false, "epcs");
			}
		});
		router.get("/epcis/resource/bizSteps").handler(routingContext -> {
			monitoring(routingContext.request(), "/bizSteps-resources");
			try {
				boolean requestScan = Boolean.parseBoolean(routingContext.queryParams().get("requestScan"));
				restQueryServce.getResources(routingContext.request(), routingContext.response(), requestScan,
						"bizSteps");
			} catch (Exception e) {
				restQueryServce.getResources(routingContext.request(), routingContext.response(), false, "bizSteps");
			}
		});
		router.get("/epcis/resource/dispositions").handler(routingContext -> {
			monitoring(routingContext.request(), "/dispositions-resources");
			try {
				boolean requestScan = Boolean.parseBoolean(routingContext.queryParams().get("requestScan"));
				restQueryServce.getResources(routingContext.request(), routingContext.response(), requestScan,
						"dispositions");
			} catch (Exception e) {
				restQueryServce.getResources(routingContext.request(), routingContext.response(), false,
						"dispositions");
			}
		});
		router.get("/epcis/resource/readPoints").handler(routingContext -> {
			monitoring(routingContext.request(), "/readPoints-resources");
			try {
				boolean requestScan = Boolean.parseBoolean(routingContext.queryParams().get("requestScan"));
				restQueryServce.getResources(routingContext.request(), routingContext.response(), requestScan,
						"readPoints");
			} catch (Exception e) {
				restQueryServce.getResources(routingContext.request(), routingContext.response(), false, "readPoints");
			}
		});
		router.get("/epcis/resource/bizLocations").handler(routingContext -> {
			monitoring(routingContext.request(), "/bizLocations-resources");
			try {
				boolean requestScan = Boolean.parseBoolean(routingContext.queryParams().get("requestScan"));
				restQueryServce.getResources(routingContext.request(), routingContext.response(), requestScan,
						"bizLocations");
			} catch (Exception e) {
				restQueryServce.getResources(routingContext.request(), routingContext.response(), false,
						"bizLocations");
			}
		});
	}

	void registerCheckResourceHandler(Router router, RESTQueryService restQueryService) {
		router.get("/epcis/resource/eventTypes/:eventType").handler(routingContext -> {
			monitoring(routingContext.request(), "/eventTypes/:eventType-exist");
			restQueryService.checkResource(routingContext.request(), routingContext.response(), "eventTypes",
					routingContext.pathParam("eventType"));
		});

		router.get("/epcis/resource/epcs/:epc").handler(routingContext -> {
			monitoring(routingContext.request(), "/epcs/:epc-exist");
			restQueryService.checkResource(routingContext.request(), routingContext.response(), "epcs",
					routingContext.pathParam("epc"));
		});

		router.get("/epcis/resource/bizSteps/:bizStep").handler(routingContext -> {
			monitoring(routingContext.request(), "/bizSteps/:bizStep-exist");
			restQueryService.checkResource(routingContext.request(), routingContext.response(), "bizSteps",
					routingContext.pathParam("bizStep"));
		});

		router.get("/epcis/resource/dispositions/:disposition").handler(routingContext -> {
			monitoring(routingContext.request(), "/dispositions/:disposition-exist");
			restQueryService.checkResource(routingContext.request(), routingContext.response(), "dispositions",
					routingContext.pathParam("disposition"));
		});

		router.get("/epcis/resource/readPoints/:readPoint").handler(routingContext -> {
			monitoring(routingContext.request(), "/readPoints/:readPoint-exist");
			restQueryService.checkResource(routingContext.request(), routingContext.response(), "readPoints",
					routingContext.pathParam("readPoint"));
		});

		router.get("/epcis/resource/bizLocations/:bizLocation").handler(routingContext -> {
			monitoring(routingContext.request(), "/bizLocations/:bizLocation-exist");
			restQueryService.checkResource(routingContext.request(), routingContext.response(), "bizLocations",
					routingContext.pathParam("bizLocation"));
		});

		// events
		// events/{eventID}
		// eventTypes
		// eventTypes/{eventType}
		// eventTypes/{eventType}/events
		// eventTypes/{eventType}/events/{eventID}
		// epcs
		// epcs/{epc}
		// epcs/{epc}/events
		// epcs/{epc}/events/{eventID}
		// bizSteps
		// bizSteps/{bizStep}
		// bizSteps/{bizStep}/events
		// bizSteps/{bizStep}/events/{eventID}
		// dispositions
		// dispositions/{disposition}
		// dispositions/{disposition}/events
		// dispositions/{disposition}/events/{eventID}
		// readPoints
		// readPoints/{readPoint}
		// readPoints/{readPoint}/events
		// readPoints/{readPoint}/events/{eventID}
		// bizLocations
		// bizLocations/{bizLocation}
		// bizLocations/{bizLocation}/events
		// bizLocations/{bizLocation}/events/{eventID}
	}

	void registerEventsHandler(Router router, RESTQueryService restQueryService) {
		router.get("/epcis/resource/events").handler(routingContext -> {
			monitoring(routingContext.request(), "events-get");
			restQueryService.getEvents(routingContext.request(), routingContext.response());
		});

		router.get("/epcis/resource/eventTypes/:eventType/events").handler(routingContext -> {
			monitoring(routingContext.request(), "events-get");
			routingContext.request().params().add("eventType", routingContext.pathParam("eventType"));
			restQueryService.getEvents(routingContext.request(), routingContext.response());
		});

		router.get("/epcis/resource/epcs/:epc/events").handler(routingContext -> {
			monitoring(routingContext.request(), "events-get");
			String epc = routingContext.pathParam("epc");
			if (!epc.contains("urn:epc:class"))
				routingContext.request().params().add("MATCH_anyEPC", epc);
			else
				routingContext.request().params().add("MATCH_anyEPCClass", epc);
			restQueryService.getEvents(routingContext.request(), routingContext.response());
		});

		router.get("/epcis/resource/bizSteps/:bizStep/events").handler(routingContext -> {
			monitoring(routingContext.request(), "events-get");
			routingContext.request().params().add("EQ_bizStep", routingContext.pathParam("bizStep"));
			restQueryService.getEvents(routingContext.request(), routingContext.response());
		});

		router.get("/epcis/resource/dispositions/:disposition/events").handler(routingContext -> {
			monitoring(routingContext.request(), "events-get");
			routingContext.request().params().add("EQ_disposition", routingContext.pathParam("disposition"));
			restQueryService.getEvents(routingContext.request(), routingContext.response());
		});

		router.get("/epcis/resource/readPoints/:readPoint/events").handler(routingContext -> {
			monitoring(routingContext.request(), "events-get");
			routingContext.request().params().add("EQ_readPoint", routingContext.pathParam("readPoint"));
			restQueryService.getEvents(routingContext.request(), routingContext.response());
		});

		router.get("/epcis/resource/bizLocations/:bizLocation/events").handler(routingContext -> {
			monitoring(routingContext.request(), "events-get");
			routingContext.request().params().add("EQ_bizLocation", routingContext.pathParam("bizLocation"));
			restQueryService.getEvents(routingContext.request(), routingContext.response());
		});

		router.get("/epcis/resource/next/:id").handler(routingContext -> {
			monitoring(routingContext.request(), "next-get");
			restQueryService.getNextEvents(routingContext.response(), routingContext.request().getParam("id"));
		});
	}

	void registerEventHandler(Router router, RESTQueryService restQueryService) {
		router.get("/epcis/resource/events/:eventID").handler(routingContext -> {
			monitoring(routingContext.request(), "/events/:eventID-get");
			routingContext.request().params().add("EQ_eventID", routingContext.pathParam("eventID"));
			restQueryService.getEvents(routingContext.request(), routingContext.response());
		});

		router.get("/epcis/resource/eventTypes/:eventType/events/:eventID").handler(routingContext -> {
			monitoring(routingContext.request(), "/eventTypes/:eventType/events/:eventID-get");
			routingContext.request().params().add("eventType", routingContext.pathParam("eventType")).add("EQ_eventID",
					routingContext.pathParam("eventID"));
			restQueryService.getEvents(routingContext.request(), routingContext.response());
		});

		router.get("/epcis/resource/epcs/:epc/events/:eventID").handler(routingContext -> {
			monitoring(routingContext.request(), "/epcs/:epc/events/:eventID-get");
			String epc = routingContext.pathParam("epc");
			if (!epc.contains("urn:epc:class"))
				routingContext.request().params().add("MATCH_anyEPC", routingContext.pathParam("epc")).add("EQ_eventID",
						routingContext.pathParam("eventID"));
			else
				routingContext.request().params().add("MATCH_anyEPCClass", routingContext.pathParam("epc"))
						.add("EQ_eventID", routingContext.pathParam("eventID"));

			restQueryService.getEvents(routingContext.request(), routingContext.response());
		});

		router.get("/epcis/resource/bizSteps/:bizStep/events/:eventID").handler(routingContext -> {
			monitoring(routingContext.request(), "/bizSteps/:bizStep/events/:eventID-get");
			routingContext.request().params().add("EQ_bizStep", routingContext.pathParam("bizStep")).add("EQ_eventID",
					routingContext.pathParam("eventID"));
			restQueryService.getEvents(routingContext.request(), routingContext.response());
		});

		router.get("/epcis/resource/dispositions/:disposition/events/:eventID").handler(routingContext -> {
			monitoring(routingContext.request(), "/dispositions/:disposition/events/:eventID-get");
			routingContext.request().params().add("EQ_disposition", routingContext.pathParam("disposition"))
					.add("EQ_eventID", routingContext.pathParam("eventID"));
			restQueryService.getEvents(routingContext.request(), routingContext.response());
		});

		router.get("/epcis/resource/readPoints/:readPoint/events/:eventID").handler(routingContext -> {
			monitoring(routingContext.request(), "/readPoints/:readPoint/events/:eventID-get");
			routingContext.request().params().add("EQ_readPoint", routingContext.pathParam("readPoint"))
					.add("EQ_eventID", routingContext.pathParam("eventID"));
			restQueryService.getEvents(routingContext.request(), routingContext.response());
		});

		router.get("/epcis/resource/bizLocations/:bizLocation/events/:eventID").handler(routingContext -> {
			// ni%3A%2F%2F%2Fsha%2D256%3Bfe1206a2e148283261233bdcecc737f60b5fc934a9bf37ceed34b40aac200f30%3Fver%3DCBV2.0
			// ni:///sha-256;fe1206a2e148283261233bdcecc737f60b5fc934a9bf37ceed34b40aac200f30?ver=CBV2.0
			monitoring(routingContext.request(), "/bizLocations/:bizLocation/events/:eventID-get");
			routingContext.request().params().add("EQ_bizLocation", routingContext.pathParam("bizLocation"))
					.add("EQ_eventID", routingContext.pathParam("eventID"));
			restQueryService.getEvents(routingContext.request(), routingContext.response());
		});
	}

	void registerDLMasterDataQuery(Router router, RESTQueryService restQueryService) {
		router.get("/epcis/resource/vocabularies").handler(routingContext -> {
			monitoring(routingContext.request(), "digitallink");
			restQueryService.getVocabularies(null, routingContext.request().params(), routingContext.response());
		});

		router.get("/epcis/resource/01/:gtin").handler(routingContext -> {
			monitoring(routingContext.request(), "digitallink");
			restQueryService.getGTINVocabularies(routingContext);
		});

		router.get("/epcis/resource/01/:gtin/10/:lot").handler(routingContext -> {
			monitoring(routingContext.request(), "digitallink");
			restQueryService.getLGTINVocabularies(routingContext);
		});

		router.get("/epcis/resource/414/:gln").handler(routingContext -> {
			monitoring(routingContext.request(), "digitallink");
			restQueryService.getGLNVocabularies(routingContext);
		});

		router.get("/epcis/resource/414/:gln/254/:ext").handler(routingContext -> {
			monitoring(routingContext.request(), "digitallink");
			restQueryService.getGLNVocabularies(routingContext);
		});
		router.get("/epcis/resource/417/:gln").handler(routingContext -> {
			monitoring(routingContext.request(), "digitallink");
			restQueryService.getPGLNVocabularies(routingContext);
		});

		router.get("/epcis/resource/253/:gdti").handler(routingContext -> {
			monitoring(routingContext.request(), "digitallink");
			restQueryService.getGDTIVocabularies(routingContext);
		});

		router.get("/epcis/resource/01/:gtin/21/:serial").handler(routingContext -> {
			monitoring(routingContext.request(), "digitallink");
			restQueryService.getSGTINVocabularies(routingContext);
		});
	}

	void postOptions(RoutingContext routingContext) {
		routingContext.response().putHeader("GS1-EPCIS-version", "2.0").putHeader("GS1-CBV-version", "2.0")
				.putHeader("GS1-Vendor-version", Metadata.vendorVersion).putHeader("GS1-EPCIS-min", ">=1.0")
				.putHeader("GS1-EPCIS-max", "2.0").putHeader("GS1-EPCIS-extensions", "cbvmda=<urn:epcglobal:cbv:mda>")
				.putHeader("GS1-CBV-extensions", "cbvmda=<urn:epcglobal:cbv:mda>")
				.putHeader("GS1-CAPTURE-limit", String.valueOf(RESTQueryServer.GS1_CAPTURE_limit))
				.putHeader("GS1-CAPTURE-file-size-limit", String.valueOf(RESTQueryServer.GS1_CAPTURE_file_size_limit))
				.end();
	}

	void registerOptions(Router router) {
		router.options("/epcis/resource/events").consumes("*/json").handler(routingContext -> {
			monitoring(routingContext.request(), "/events-options");
			postOptions(routingContext);
		});
		router.options("/epcis/resource/events/:event").consumes("*/json").handler(routingContext -> {
			monitoring(routingContext.request(), "/events/:event-options");
			postOptions(routingContext);
		});
		router.options("/epcis/resource/eventTypes").consumes("*/json").handler(routingContext -> {
			monitoring(routingContext.request(), "/eventTypes-options");
			postOptions(routingContext);
		});
		router.options("/epcis/resource/eventTypes/:eventType").consumes("*/json").handler(routingContext -> {
			monitoring(routingContext.request(), "/eventTypes/:eventType-options");
			postOptions(routingContext);
		});
		router.options("/epcis/resource/eventTypes/:eventType/events").consumes("*/json").handler(routingContext -> {
			monitoring(routingContext.request(), "/eventTypes/:eventType/events-options");
			postOptions(routingContext);
		});
		router.options("/epcis/resource/eventTypes/:eventType/events/:eventID").consumes("*/json")
				.handler(routingContext -> {
					monitoring(routingContext.request(), "/eventTypes/:eventType/events/:eventID-options");
					postOptions(routingContext);
				});
		router.options("/epcis/resource/epcs").consumes("*/json").handler(routingContext -> {
			monitoring(routingContext.request(), "/epcs-options");
			postOptions(routingContext);
		});
		router.options("/epcis/resource/epcs/:epc").consumes("*/json").handler(routingContext -> {
			monitoring(routingContext.request(), "/epcs/:epc-options");
			postOptions(routingContext);
		});
		router.options("/epcis/resource/epcs/:epc/events").consumes("*/json").handler(routingContext -> {
			monitoring(routingContext.request(), "/epcs/:epc/events-option");
			postOptions(routingContext);
		});
		router.options("/epcis/resource/epcs/:epc/events/:eventID").consumes("*/json").handler(routingContext -> {
			monitoring(routingContext.request(), "/epcs/:epc/events/:eventID-options");
			postOptions(routingContext);
		});
		router.options("/epcis/resource/bizSteps").consumes("*/json").handler(routingContext -> {
			monitoring(routingContext.request(), "/bizSteps-options");
			postOptions(routingContext);
		});
		router.options("/epcis/resource/bizSteps/:bizStep").consumes("*/json").handler(routingContext -> {
			monitoring(routingContext.request(), "/bizSteps/:bizStep-options");
			postOptions(routingContext);
		});
		router.options("/epcis/resource/bizSteps/:bizStep/events").consumes("*/json").handler(routingContext -> {
			monitoring(routingContext.request(), "/bizSteps/:bizStep/events-option");
			postOptions(routingContext);
		});
		router.options("/epcis/resource/bizSteps/:bizStep/events/:eventID").consumes("*/json")
				.handler(routingContext -> {
					monitoring(routingContext.request(), "/bizSteps/:bizStep/events/:eventID-options");
					postOptions(routingContext);
				});
		router.options("/epcis/resource/dispositions").consumes("*/json").handler(routingContext -> {
			monitoring(routingContext.request(), "/dispositions-options");
			postOptions(routingContext);
		});
		router.options("/epcis/resource/dispositions/:disposition").consumes("*/json").handler(routingContext -> {
			monitoring(routingContext.request(), "/dispositions/:disposition-options");
			postOptions(routingContext);
		});
		router.options("/epcis/resource/dispositions/:disposition/events").consumes("*/json")
				.handler(routingContext -> {
					monitoring(routingContext.request(), "/dispositions/:disposition/events-options");
					postOptions(routingContext);
				});
		router.options("/epcis/resource/dispositions/:disposition/events/:eventID").consumes("*/json")
				.handler(routingContext -> {
					monitoring(routingContext.request(), "/dispositions/:disposition/events/:eventID-options");
					postOptions(routingContext);
				});
		router.options("/epcis/resource/readPoints").consumes("*/json").handler(routingContext -> {
			monitoring(routingContext.request(), "/readPoints-options");
			postOptions(routingContext);
		});
		router.options("/epcis/resource/readPoints/:readPoint").consumes("*/json").handler(routingContext -> {
			monitoring(routingContext.request(), "/readPoints/:readPoint-options");
			postOptions(routingContext);
		});
		router.options("/epcis/resource/readPoints/:readPoint/events").consumes("*/json").handler(routingContext -> {
			monitoring(routingContext.request(), "/readPoints/:readPoint/events-options");
			postOptions(routingContext);
		});
		router.options("/epcis/resource/readPoints/:readPoint/events/:eventID").consumes("*/json")
				.handler(routingContext -> {
					monitoring(routingContext.request(), "/readPoints/:readPoint/events/:eventID-options");
					postOptions(routingContext);
				});
		router.options("/epcis/resource/bizLocations").consumes("*/json").handler(routingContext -> {
			monitoring(routingContext.request(), "/bizLocations-options");
			postOptions(routingContext);
		});
		router.options("/epcis/resource/bizLocations/:bizLocation").consumes("*/json").handler(routingContext -> {
			monitoring(routingContext.request(), "/bizLocations/:bizLocation-options");
			postOptions(routingContext);
		});
		router.options("/epcis/resource/bizLocations/:bizLocation/events").consumes("*/json")
				.handler(routingContext -> {
					monitoring(routingContext.request(), "/bizLocations/:bizLocation/events-options");
					postOptions(routingContext);
				});
		router.options("/epcis/resource/bizLocations/:bizLocation/events/:eventID").consumes("*/json")
				.handler(routingContext -> {
					monitoring(routingContext.request(), "/bizLocations/:bizLocation/events/:eventID-options");
					postOptions(routingContext);
				});

	}

	@Override
	public void start(Promise<Void> startPromise) {
		final HttpServer server = vertx.createHttpServer();
		final Router router = Router.router(vertx);

		RESTQueryService restQueryService = new RESTQueryService();

		router.route().handler(CorsHandler.create("*")
				.allowedHeader("Access-Control-Allow-Credentials").allowedHeader("Access-Control-Allow-Origin")
				.allowedHeader("Access-Control-Allow-Headers").allowedHeader("Content-Type")
				.allowedMethod(io.vertx.core.http.HttpMethod.GET).allowedMethod(io.vertx.core.http.HttpMethod.POST)
				.allowedMethod(io.vertx.core.http.HttpMethod.OPTIONS).allowedHeader("Access-Control-Request-Method")
				).handler(BodyHandler.create());


		router.get("/epcis").handler(routingContext -> {
			monitoring(routingContext.request(), "ping");
			routingContext.response().setStatusCode(200).end();
		});

		registerGetResourcesHandler(router, restQueryService);
		registerCheckResourceHandler(router, restQueryService);
		registerEventsHandler(router, restQueryService);
		registerEventHandler(router, restQueryService);
		registerDLMasterDataQuery(router, restQueryService);
		registerOptions(router);

		server.requestHandler(router).listen(port);

	}

	public static void main(String[] args) {
		Logger.getRootLogger().setLevel(Level.OFF);

		Vertx vertx = Vertx.vertx();
		BootstrapUtil.configureServer(vertx, args);

		// vertx.deployVerticle(new RESTQueryServer());
		
		DeploymentOptions dOptions = new DeploymentOptions().setInstances(numOfVerticles);
		vertx.deployVerticle("org.oliot.epcis.query.RESTQueryServer", dOptions);
	}
}
