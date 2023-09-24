package org.oliot.epcis.query;

import org.oliot.epcis.server.EPCISServer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.ext.web.Router;

public class SubscriptionMonitor {

	public static void registerEchoHandler(Router router, EventBus eventBus) {
		router.post("/epcis/echo").handler(routingContext -> {
			System.out.println(routingContext.body().asString());
			eventBus.send("subscriptionMonitor", routingContext.body().asString());
			routingContext.end();
		});
		EPCISServer.logger.info("[POST /epcis/echo] - router added");
	}

	public static void addSubscriptionMonitorWebSocket(Router router, EventBus eventBus) {

		router.get("/epcis/uiSocket").handler(routingContext -> {
			routingContext.request().toWebSocket().onSuccess(h -> {

				MessageConsumer<Object> consumer = eventBus.consumer("subscriptionMonitor", msg -> {
					h.writeTextMessage(msg.body().toString());
				});
				consumer.exceptionHandler(h2 -> {
					EPCISServer.logger.info(h2.getMessage());
				}).completionHandler(h1 -> {
					EPCISServer.logger.info("Subscription monitor active");
				});

			}).onFailure(h -> {
				EPCISServer.logger.info("/epcis/uiSocket connection fails");
			});
		});

		EPCISServer.logger.info("[/epcis/uiSocket] Subscription monitor web socket added");
	}
}
