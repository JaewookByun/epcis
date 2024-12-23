package org.oliot.epcis.tdt;

import org.oliot.epcis.model.ValidationException;
import org.oliot.epcis.server.EPCISServer;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

/**
 * Copyright (C) 2020-2024. (Jaewook Byun) all rights reserved.
 * <p>
 * This project is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * TransactionManager holds event-bus handlers for processing capture jobs.
 * <p>
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr, Associate Director, Auto-ID Labs, Korea,
 *         bjw0829@gmail.com
 */
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

		router.get("/tdt").handler(routingContext -> {
			try {
				JsonObject obj = TagDataTranslationEngine.parse(routingContext.body().asJsonObject().getString("id"));
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
						.setStatusCode(200).end(obj.toString());
			} catch (ValidationException e) {
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
						.setStatusCode(400).end(e.getReason());
			} catch (IllegalArgumentException e) {
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
						.setStatusCode(400).end(e.getMessage());
			}
		});
		EPCISServer.logger.info("[GET /tdt] - router added");
	}
}
