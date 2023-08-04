package org.oliot.epcis.capture.json;

import io.vertx.core.json.JsonObject;

public class JSONMessageFactory {
	public static JsonObject exception413CapturePayloadTooLarge = new JsonObject()
			.put("type", "epcisException:CaptureLimitExceededException").put("title", "Capture Payload Too large")
			.put("status", 413);
	private static JsonObject exception400ValidationException = new JsonObject()
			.put("type", "epcisException:ValidationException").put("status", 400);

	private static JsonObject exception500ImplementationException = new JsonObject()
			.put("type", "epcisException:ImplementationException").put("status", 500);
	
	public static JsonObject get400ValidationException(String title) {
		JsonObject cloned = exception400ValidationException.copy();
		cloned.put("title", title);
		return cloned;
	}
	public static JsonObject get500ImplementationException(String title) {
		JsonObject cloned = exception500ImplementationException.copy();
		cloned.put("title", title);
		return cloned;
	}
}
