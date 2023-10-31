package com.nira.epcis.extend;

import java.util.Base64;

import io.vertx.ext.web.RoutingContext;

/**
 * Copyright (C) 2023. NIRA, INC. all rights reserved.
 * 
 * Implement basic authentication in EPCIS
 *
 * @author Wen Zhu wzhu@nira-inc.com
 */
public class BasicAuthenticator implements RequestProcessor {
	private String username;
	
	private String password;

	public BasicAuthenticator(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}

	@Override
	public boolean process(RoutingContext routingContext) {
		// looking for BASIC header
		String authString = routingContext.request().getHeader("Authorization");
		if(authString==null) {
			routingContext.response().setStatusCode(401).setStatusMessage("No authentication information received").send();
			return false;
		}
		
        String[] authParts = authString.split("\\s+");
        String authInfo = authParts[1];
        byte[] bytes = Base64.getDecoder().decode(authInfo);
        String decodedAuth = new String(bytes);
        
        String[] parts = decodedAuth.split(":");
        
		if(parts.length!=2) {
			routingContext.response().setStatusCode(401).setStatusMessage("Invalid authentication information received").send();
			return false;
		}

		if((!parts[0].equals(username))||(!parts[1].equals(password))) {
			routingContext.response().setStatusCode(401).setStatusMessage("Cannot authenticate user").send();
			return false;
		}

		return true;
	}
}
