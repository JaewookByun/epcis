package org.oliot.epcis.query.converter.tdt;

import java.util.Arrays;

import org.oliot.epcis.server.EPCISServer;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class TagDataTranslationServiceHandler {
	public static void registerPostEventsHandler(Router router) {
		router.get("/tdt/epc/:epc").handler(routingContext -> {
			String epc = routingContext.pathParam("epc");
			try {
				String type = routingContext.queryParam("type").get(0);
				JsonObject obj = TagDataTranslationEngine.parseEPC(epc, IdentifierType.valueOf(type));
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
						.setStatusCode(200).end(obj.toString());
			} catch (IllegalArgumentException e) {
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
						.setStatusCode(400)
						.end(e.getMessage());
			} catch (IndexOutOfBoundsException e) {
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
				.setStatusCode(400)
				.end("type is a mandatory URL parameter [one of " + Arrays.asList(IdentifierType.values()) + "]");
			}
		});
		EPCISServer.logger.info("[GET /tdt/:id] - router added");
	}
}
