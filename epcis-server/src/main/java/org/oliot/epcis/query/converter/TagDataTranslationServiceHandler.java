package org.oliot.epcis.query.converter;

import org.oliot.epcis.server.EPCISServer;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class TagDataTranslationServiceHandler {
	public static void registerPostEventsHandler(Router router) {
		router.get("/tdt/epc/:id").handler(routingContext -> {
			String id = routingContext.pathParam("id");

			JsonObject obj = TagDataTranslationEngine.parseEPC(id);

			if (!obj.containsKey("invalid"))
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
						.setStatusCode(200).end(obj.toString());
			else
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
						.setStatusCode(400).end(obj.toString());
		});
		EPCISServer.logger.info("[GET /tdt/:id] - router added");
	}
}
