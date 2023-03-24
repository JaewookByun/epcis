package org.oliot.epcis.query;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpClient;
import io.vertx.rxjava.core.Vertx;

/**
 * Copyright (C) 2020-2021. (Jaewook Byun) all rights reserved.
 * <p>
 * Oliot EPCIS X is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * Among various modules, epcis-query-rest acts as a server to receive queries
 * to provide filtered, sorted, limited events of interest inside EPCIS
 * repository.
 * <p>
 * 
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr
 *         <p>
 *         Associate Director, Auto-ID Labs, KAIST, bjw0829@kaist.ac.kr
 */
public class SubscriberExample extends AbstractVerticle {

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		super.start(startPromise);

		HttpClient client = vertx.createHttpClient();

		client.webSocket(8084, "localhost", "/epcis/resource/events?second=0/10").onSuccess(h -> {
			h.textMessageHandler(msg -> {
				System.out.println(msg);
			});
		}).onFailure(h -> {
			System.out.println(h.getLocalizedMessage());
		});
	}

	public static void main(String[] args) {
		Vertx.vertx().deployVerticle(new SubscriberExample());
	}

}
