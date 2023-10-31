package com.nira.epcis.extend;

import io.vertx.ext.web.RoutingContext;

public interface RequestProcessor {
	/**
	 * 
	 * @param routingContext
	 * @return true if continue processing the chain
	 */
	boolean process(RoutingContext routingContext);
}
