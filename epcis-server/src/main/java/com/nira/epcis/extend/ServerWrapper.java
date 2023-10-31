package com.nira.epcis.extend;

import java.util.ArrayList;
import java.util.List;

import org.oliot.epcis.query.SOAPQueryService;
import org.oliot.epcis.query.SOAPQueryServiceHandler;
import org.oliot.epcis.query.SubscriptionManager;
import org.oliot.epcis.query.TriggerEngine;
import org.oliot.epcis.server.BootstrapUtil;
import org.oliot.epcis.server.EPCISServer;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * Copyright (C) 2023. NIRA, INC. all rights reserved.
 * 
 * A wrapper around the EPCIS main class to allow pre-processing of requests
 *
 * @author Wen Zhu wzhu@nira-inc.com
 */
public class ServerWrapper extends EPCISServer{
	private SOAPQueryService soapQueryService = new  SOAPQueryService();
	
	private class SOAPQueryHandler implements Handler<RoutingContext> {
		List <RequestProcessor> processors = new ArrayList<>();

		public SOAPQueryHandler() {
			String user = configuration.getString("user");
			String password = configuration.getString("password");
			
			if((user!=null)&&(password!=null)) {
				processors.add(new BasicAuthenticator(user, password));
			}
			
			processors.add(new HeaderLogger());
		}

		@Override
		public void handle(RoutingContext routingContext) {
			for(RequestProcessor p: processors) {
				if(!p.process(routingContext)) {
					System.out.println("Aborting request...");
					return;
				}
			}
			// make a call to the default handler
			soapQueryService.query(routingContext.request(), routingContext.response().setChunked(true),
					routingContext.body().asString());
		}
	}
	
	public ServerWrapper() {
		super();
	}

	@Override
	protected void registerSOAPQueryServiceHandler(Router router, EventBus eventBus) {
		//SOAPQueryServiceHandler.registerQueryHandler(router, soapQueryService);
		router.post("/epcis/query").consumes("application/xml").handler(new SOAPQueryHandler());

		SOAPQueryServiceHandler.registerPaginationHandler(router, soapQueryService);
		TriggerEngine.registerTransactionStartHandler(eventBus);
	}

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		SubscriptionManager sm = null;
		BootstrapUtil.configureServer(vertx, args, sm);
		vertx.deployVerticle(new ServerWrapper());
	}
}
