package com.nira.epcis.extend;

import io.vertx.ext.web.RoutingContext;

/**
 * Copyright (C) 2023. NIRA, INC. all rights reserved.
 * 
 * Allow interception of HTTP requests to do pre-processing.
 *
 * @author Wen Zhu wzhu@nira-inc.com
 */
public interface RequestProcessor {
	/**
	 * A filter-like object that intercepts the HTTP requests.
	 * @param routingContext
	 * @return true if continue processing the chain
	 */
	boolean process(RoutingContext routingContext);
}
