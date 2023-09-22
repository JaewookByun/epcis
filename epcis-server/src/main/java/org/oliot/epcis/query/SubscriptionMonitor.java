package org.oliot.epcis.query;

import org.oliot.epcis.server.EPCISServer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpClient;
import io.vertx.ext.web.Router;

public class SubscriptionMonitor {

	
	public static void registerEchoHandler(Router router, EventBus eventBus) {
		router.post("/epcis/echo").handler(routingContext -> {
			System.out.println(routingContext.body().asString());
			eventBus.send("subscriptionMonitor", routingContext.body().asString());
		});
		EPCISServer.logger.info("[POST /epcis/echo] - router added");
	}
	
	
	public static void addSubscriptionMonitorWebSocket(HttpClient client, EventBus eventBus) {

		client.webSocket("/epcis/uiSocket").onSuccess(h -> {
			eventBus.consumer("subscriptionMonitor", msg -> {
				h.writeTextMessage(msg.body().toString());
			}).completionHandler(h1 -> {
				EPCISServer.logger.info("Subscription result sent");
			});
			h.writeTextMessage("OK");
		}).onFailure(h -> {
			EPCISServer.logger.info("/epcis/uiSocket connection fails");
		});
		EPCISServer.logger.info("[/epcis/uiSocket] Subscription monitor web socket added");
	}
}
