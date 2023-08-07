package org.oliot.epcis.query.converter.tdt;

import org.oliot.epcis.model.ValidationException;
import org.oliot.epcis.server.EPCISServer;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class TagDataTranslationServiceHandler {
	public static void registerPostEventsHandler(Router router) {

		router.get("/tdt/:id").handler(routingContext -> {
			String id = routingContext.pathParam("id");
			try {
				JsonObject obj = TagDataTranslationEngine.parse(id);
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
						.setStatusCode(200).end(obj.toString());
			} catch (IllegalArgumentException | ValidationException e) {
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
						.setStatusCode(400).end(e.getMessage());
			}
		});
		EPCISServer.logger.info("[GET /tdt/:id] - router added");

		router.get("/tdt").consumes("application/json").handler(routingContext -> {
			try {
				JsonObject obj = TagDataTranslationEngine.parse(routingContext.body().asJsonObject().getString("id"));
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
						.setStatusCode(200).end(obj.toString());
			} catch (IllegalArgumentException | ValidationException e) {
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
						.setStatusCode(400).end(e.getMessage());
			}
		});
		EPCISServer.logger.info("[GET /tdt] - router added");
	}
}
