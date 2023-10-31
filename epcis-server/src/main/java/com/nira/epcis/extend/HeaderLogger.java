package com.nira.epcis.extend;

import java.util.Map;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

public class HeaderLogger implements RequestProcessor{
	public boolean process(RoutingContext event) {
		HttpServerRequest request = event.request();
		for(Map.Entry<String,String> header: request.headers()) {
			System.out.println(header.getKey()+":"+header.getValue());
		}
		
		return true;
	}
}
