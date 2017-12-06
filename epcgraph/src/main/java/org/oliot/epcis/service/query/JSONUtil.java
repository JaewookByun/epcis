package org.oliot.epcis.service.query;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

public class JSONUtil {
	public static String toString(JSONObject obj) {
		String ret = obj.toString();
		ret = ret.replaceAll("\"\\[", "[");
		ret = ret.replaceAll("]\"", "]");
		ret = ret.replaceAll("\\\\\\\\\\\\\\\"", "\"");
		ret = ret.replaceAll("\\\\\\\\/", "/");
		ret = ret.replaceAll("\\\\\"", "");
		return ret;
	}

	public static JSONObject toJson(Document doc) {
		JSONObject propObj = new JSONObject();
		Iterator<String> iter = doc.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			Object value = doc.get(key);
			if (value instanceof String) {
				propObj.put(key, (String) value);
			} else if (value instanceof Long) {
				propObj.put(key, ((Long) value).toString());
			} else if (value instanceof ArrayList) {
				@SuppressWarnings("rawtypes")
				ArrayList subList = (ArrayList) value;
				JSONArray propSubList = new JSONArray();
				for (int i = 0; i < subList.size(); i++) {
					Object element = subList.get(i);
					if (element instanceof String) {
						propSubList.put((String) element);
					} else if (element instanceof Document) {
						Document subDocument = (Document) element;
						Iterator<String> subIter = subDocument.keySet().iterator();
						JSONObject propSubObj = new JSONObject();
						while (subIter.hasNext()) {
							String subKey = subIter.next();
							Object subValue = subDocument.get(subKey);
							if (subValue instanceof String) {
								propSubObj.put(subKey, (String) subValue);
							}
						}
						propSubList.put(propSubObj);
					}
				}
				propObj.put(key, propSubList);
			} else if (value instanceof Document) {
				Document subDocument = (Document) value;
				Iterator<String> subIter = subDocument.keySet().iterator();
				JSONObject propSubObj = new JSONObject();
				while (subIter.hasNext()) {
					String subKey = subIter.next();
					Object subValue = subDocument.get(subKey);
					if (subValue instanceof String) {
						propSubObj.put(subKey, (String) subValue);
					}
				}
				propObj.put(key, propSubObj);
			} else if (value instanceof JSONObject) {
				propObj.put(key, (JSONObject) value);
			}
		}
		return propObj;
	}

	public static String convertJsonLDID(String epc, HttpServletRequest request) {
		String[] uri = request.getRequestURI().split("/");
		String retStr = "";
		for (int i = 0; i < uri.length - 1; i++) {
			retStr += uri[i] + "/";
		}
		retStr += epc;

		try {
			String resourceURL = new URL(request.getScheme(), request.getServerName(), request.getServerPort(), retStr)
					.toString();

			return resourceURL;
		} catch (MalformedURLException e) {

			e.printStackTrace();
		}

		return null;
	}

	public static JSONObject putJsonLDID(JSONObject obj, HttpServletRequest request) {
		try {
			String resourceURL = new URL(request.getScheme(), request.getServerName(), request.getServerPort(),
					request.getRequestURI()).toString();
			obj.put("@id", resourceURL);
			return obj;
		} catch (MalformedURLException e) {

			e.printStackTrace();
		}
		return null;
	}

	public static JSONObject putJsonLDRelContext(JSONObject obj) {

		JSONObject context = new JSONObject();

		// Base
		context.put("sepcis", "http://bjack.kaist.ac.kr/Lilliput/Service/sepcis.jsonld");
		context.put("epcis", "http://www.gs1.org/docs/epc/epcis_1_1-schema-20140520/EPCglobal-epcis-1_1.xsd");

		obj.put("@context", context);

		return obj;
	}

	public static JSONObject putJsonLDContext(JSONObject obj, JSONObject context) {

		if (context == null)
			context = new JSONObject();

		// Base
		context.put("epcis", "http://www.gs1.org/docs/epc/epcis_1_1-schema-20140520/EPCglobal-epcis-1_1.xsd");

		obj.put("@context", context);

		return obj;
	}
}
