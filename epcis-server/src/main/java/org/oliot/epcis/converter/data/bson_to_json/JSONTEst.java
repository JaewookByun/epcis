package org.oliot.epcis.converter.data.bson_to_json;

import io.vertx.core.json.JsonObject;

public class JSONTEst {
	public static void main(String[] args) {
		JsonObject obj = new JsonObject();
		obj.put("EQ_http://dfpl.sejong.ac.kr/epcis/resource#default", "v");
		System.out.println(obj.toString());
	}

}
