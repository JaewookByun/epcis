package org.oliot.epcis.query.converter.tdt;

import org.oliot.epcis.server.EPCISServer;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class TagDataTranslationServiceHandler {
	public static void registerPostEventsHandler(Router router) {
		router.get("/tdt/epc/:epc").handler(routingContext -> {
			String epc = routingContext.pathParam("epc");
			try {
				JsonObject obj = TagDataTranslationEngine.parseEPC(epc);
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
						.setStatusCode(200).end(obj.toString());
			} catch (IllegalArgumentException e) {
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
						.setStatusCode(400)
						.end(e.getMessage());
			} 
		});
		EPCISServer.logger.info("[GET /tdt/epc/:epc] - router added");
	}
}
