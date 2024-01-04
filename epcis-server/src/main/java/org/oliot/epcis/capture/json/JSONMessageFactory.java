package org.oliot.epcis.capture.json;

import io.vertx.core.json.JsonObject;

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
public class JSONMessageFactory {

	public static JsonObject exception400SubscriptionControlsException = new JsonObject()
			.put("type", "epcisException:SubscriptionControlsException").put("status", 400);

	public static JsonObject exception413CapturePayloadTooLarge = new JsonObject()
			.put("type", "epcisException:CaptureLimitExceededException").put("title", "Capture Payload Too large")
			.put("status", 413);
	private static JsonObject exception400ValidationException = new JsonObject()
			.put("type", "epcisException:ValidationException").put("status", 400);

	private static JsonObject exception404NoSuchResourceException = new JsonObject()
			.put("type", "epcisException:NoSuchResourceException").put("status", 404);

	private static JsonObject exception406NotAcceptableException = new JsonObject()
			.put("type", "epcisException:NotAcceptableException").put("status", 406);

	private static JsonObject exception409ResourceAlreadyExistsException = new JsonObject()
			.put("type", "epcisException:ResourceAlreadyExistsException").put("status", 409);

	private static JsonObject exception413QueryTooLargeException = new JsonObject()
			.put("type", "epcisException:QueryTooLargeException").put("status", 413);
	
	private static JsonObject exception500ImplementationException = new JsonObject()
			.put("type", "epcisException:ImplementationException").put("status", 500);

	public static JsonObject get400ValidationException(String title) {
		JsonObject cloned = exception400ValidationException.copy();
		cloned.put("title", title);
		return cloned;
	}

	public static JsonObject get400SubscriptionControlsException(String title) {
		JsonObject cloned = exception400SubscriptionControlsException.copy();
		cloned.put("title", title);
		return cloned;
	}

	public static JsonObject get406NotAcceptableException(String title) {
		JsonObject cloned = exception406NotAcceptableException.copy();
		cloned.put("title", title);
		return cloned;
	}

	public static JsonObject get404NoSuchResourceException(String title) {
		JsonObject cloned = exception404NoSuchResourceException.copy();
		cloned.put("title", title);
		return cloned;
	}

	public static JsonObject get409ResourceAlreadyExistsException(String title) {
		JsonObject cloned = exception409ResourceAlreadyExistsException.copy();
		cloned.put("title", title);
		return cloned;
	}

	public static JsonObject get413QueryTooLargeException(String title) {
		JsonObject cloned = exception413QueryTooLargeException.copy();
		cloned.put("title", title);
		return cloned;
	}

	public static JsonObject get500ImplementationException(String title) {
		JsonObject cloned = exception500ImplementationException.copy();
		cloned.put("title", title);
		return cloned;
	}
}
