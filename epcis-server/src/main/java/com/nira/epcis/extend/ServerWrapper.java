package com.nira.epcis.extend;

import java.util.ArrayList;
import java.util.List;

import org.oliot.epcis.query.SubscriptionManager;
import org.oliot.epcis.server.BootstrapUtil;
import org.oliot.epcis.server.EPCISServer;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

/**
 * Copyright (C) 2023. NIRA, INC. all rights reserved.
 * 
 * A wrapper around the EPCIS main class to allow pre-processing of requests
 *
 * @author Wen Zhu wzhu@nira-inc.com
 */
public class ServerWrapper extends EPCISServer{
	
	private class HandlerWrapper implements Handler<RoutingContext> {
		List <RequestProcessor> processors = new ArrayList<>();
		Handler<RoutingContext> impl = null;

		public HandlerWrapper(Handler<RoutingContext> impl) {
			this.impl = impl;
			
			String user = configuration.getString("user");
			String password = configuration.getString("password");
			
			if((user!=null)&&(password!=null)) {
				processors.add(new BasicAuthenticator(user, password));
			}
			
			// processors.add(new HeaderLogger());
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
			impl.handle(routingContext);
		}
	}
	
	public static final EPCISServer.HandlerKey[] RequestToIntercept = new EPCISServer.HandlerKey[]
		{
			new EPCISServer.HandlerKey("post", "/epcis/query", "application/xml"),
			new EPCISServer.HandlerKey("get","/epcis/events","application/json")
		};
	
	public ServerWrapper() {
		super();
		
		// overwrite the handlers
		for(EPCISServer.HandlerKey key: RequestToIntercept) {
			Handler<RoutingContext> defaultHandler = EPCISServer.getHandler(key);
			EPCISServer.registerHandler(key, new HandlerWrapper(defaultHandler));
		}
	}
	
	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		SubscriptionManager sm = null;
		BootstrapUtil.configureServer(vertx, args, sm);
		vertx.deployVerticle(new ServerWrapper());
	}
}
