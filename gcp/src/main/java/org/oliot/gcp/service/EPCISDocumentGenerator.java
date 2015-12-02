package org.oliot.gcp.service;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.oliot.gcp.core.CodeParser;

public class EPCISDocumentGenerator {
	
	public JSONObject generateEPCISDocument(String code, int gcpLength, String action, String bizStep, String disposition,
		HashMap<String, String> extensions) {
		
		// Parsing gs1 code
		CodeParser codeParser = new CodeParser();
		HashMap<String,String> collection = codeParser.parse(code, gcpLength);
		if(collection == null){
			return null;
		}
		
		JSONObject objectEvent = new JSONObject();
		objectEvent.put("eventTime", System.currentTimeMillis());
		objectEvent.put("eventTimeZoneOffset", "+09:00");
		objectEvent.put("action", "OBSERVE");
		if( action != null ){
			if(action.equals("ADD")||action.equals("OBSERVE")||action.equals("DELETE")){
				objectEvent.put("action", action);
			}
		}
		if( bizStep != null ) objectEvent.put("bizStep", bizStep);
		if( disposition != null ) objectEvent.put("disposition", disposition);
		
		if (collection.containsKey("sgtin")) {
			JSONObject epcObject = new JSONObject();
			epcObject.put("epc", collection.get("sgtin"));
			JSONArray epcListArray = new JSONArray();
			epcListArray.put(epcObject);
			objectEvent.put("epcList", epcListArray);
			JSONObject ilmdObject = new JSONObject();
			if( collection.containsKey("urn:id:epc:ai:11")){
				ilmdObject.put("urn:id:epc:ai:11", collection.get("urn:id:epc:ai:11"));
			}
			if( collection.containsKey("urn:id:epc:ai:13")){
				ilmdObject.put("urn:id:epc:ai:13", collection.get("urn:id:epc:ai:13"));
			}
			if( collection.containsKey("urn:id:epc:ai:30")){
				ilmdObject.put("urn:id:epc:ai:30", collection.get("urn:id:epc:ai:30"));
			}
			if( collection.containsKey("urn:id:epc:ai:310n")){
				ilmdObject.put("urn:id:epc:ai:310n", collection.get("urn:id:epc:ai:310n"));
			}
			if( collection.containsKey("urn:id:epc:ai:393n")){
				ilmdObject.put("urn:id:epc:ai:393n", collection.get("urn:id:epc:ai:393n"));
			}
			if( ilmdObject.length() > 0 ){
				objectEvent.put("ilmd", ilmdObject);
			}
		}
		
		if (collection.containsKey("sgln")){
			JSONObject locationObj = new JSONObject();
			locationObj.put("id", collection.get("sgln"));
			objectEvent.put("readPoint", locationObj);
		}
		
		if (collection.containsKey("lgtin")){
			JSONObject quantityObject = new JSONObject();
			quantityObject.put("epcClass", collection.get("lgtin"));
			
			if( collection.containsKey("urn:id:epc:ai:11")){
				quantityObject.put("urn:id:epc:ai:11", collection.get("urn:id:epc:ai:11"));
			}
			if( collection.containsKey("urn:id:epc:ai:13")){
				quantityObject.put("urn:id:epc:ai:13", collection.get("urn:id:epc:ai:13"));
			}
			if( collection.containsKey("urn:id:epc:ai:30")){
				quantityObject.put("urn:id:epc:ai:30", collection.get("urn:id:epc:ai:30"));
			}
			if( collection.containsKey("urn:id:epc:ai:310n")){
				quantityObject.put("urn:id:epc:ai:310n", collection.get("urn:id:epc:ai:310n"));
			}
			if( collection.containsKey("urn:id:epc:ai:393n")){
				quantityObject.put("urn:id:epc:ai:393n", collection.get("urn:id:epc:ai:393n"));
			}
			JSONArray quantityList = new JSONArray();
			quantityList.put(quantityObject);
			JSONObject extension = new JSONObject();
			extension.put("quantityList", quantityList);
			objectEvent.put("extension", extension);
		}
		JSONObject objectEventWrap = new JSONObject();
		objectEventWrap.put("ObjectEvent", objectEvent);
		JSONArray eventList = new JSONArray();
		eventList.put(objectEventWrap);
		JSONObject epcisBody = new JSONObject();
		epcisBody.put("EventList", eventList);
		JSONObject epcis = new JSONObject();
		epcis.put("EPCISBody", epcisBody);
		JSONObject epcisDocument = new JSONObject();
		epcisDocument.put("epcis", epcis);
		return epcisDocument;
	}
}
