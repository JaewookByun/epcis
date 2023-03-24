package org.oliot.epcis.converter.json.read;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import org.bson.Document;
import org.oliot.gcp.core.DLConverter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.bind.DatatypeConverter;

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
public class JSONEPCISEventReadConverter {

	public static void putParentID(JsonObject base, Document bsonEvent) {
		if (bsonEvent.containsKey("parentID"))
			base.put("parentID", DLConverter.getDL(bsonEvent.getString("parentID")));
	}

	public static void putInputEPCList(JsonObject base, Document bsonEvent) {
		if (bsonEvent.containsKey("inputEPCList")){
            List<String> list = bsonEvent.getList("inputEPCList", String.class);
            JsonArray newList = new JsonArray();
            for(String s: list){
                newList.add(DLConverter.getDL(s));
            }
            base.put("inputEPCList", newList);
        }
	}

	public static void putOutputEPCList(JsonObject base, Document bsonEvent) {
        if (bsonEvent.containsKey("outputEPCList")){
            List<String> list = bsonEvent.getList("outputEPCList", String.class);
            JsonArray newList = new JsonArray();
            for(String s: list){
                newList.add(DLConverter.getDL(s));
            }
            base.put("outputEPCList", newList);
        }
	}

	public static void putTransformationID(JsonObject base, Document bsonEvent) {
		if (bsonEvent.containsKey("transformationID"))
			base.put("transformationID", bsonEvent.getString("transformationID"));
	}

	public static void putEpcList(JsonObject base, Document bsonEvent) {
        if (bsonEvent.containsKey("epcList")){
            List<String> list = bsonEvent.getList("epcList", String.class);
            JsonArray newList = new JsonArray();
            for(String s: list){
                newList.add(DLConverter.getDL(s));
            }
            base.put("epcList", newList);
        }
	}

	public static void putChildEPCs(JsonObject base, String baseKey, Document bsonEvent) {
        if (bsonEvent.containsKey("epcList")){
            List<String> list = bsonEvent.getList("epcList", String.class);
            JsonArray newList = new JsonArray();
            for(String s: list){
                newList.add(DLConverter.getDL(s));
            }
            base.put("childEPCs", newList);
        }
	}

	public static void putAction(JsonObject base, Document bsonEvent) {
		if (bsonEvent.containsKey("action"))
			base.put("action", bsonEvent.getString("action"));
	}

	public static void putBizStep(JsonObject base, Document bsonEvent) {
		if (bsonEvent.containsKey("bizStep")){
            String bizStep = bsonEvent.getString("bizStep");
            if(bizStep.contains("urn:epcglobal:cbv:bizstep:")){
                String[] arr = bizStep.split(":");
                base.put("bizStep", arr[arr.length-1]);
            }else{
                base.put("bizStep", bsonEvent.getString("bizStep"));
            }
        }

	}

	public static void putDisposition(JsonObject base, Document bsonEvent) {
		if (bsonEvent.containsKey("disposition")){
            String disposition = bsonEvent.getString("disposition");
            if(disposition.contains("urn:epcglobal:cbv:disp:")){
                String[] arr = disposition.split(":");
                base.put("disposition", arr[arr.length-1]);
            }else{
                base.put("disposition", bsonEvent.getString("disposition"));
            }
        }
	}

	public static void putReadPoint(JsonObject base, Document bsonEvent) {
		if (bsonEvent.containsKey("readPoint")) {
			base.put("readPoint", new JsonObject().put("id", DLConverter.getDL(bsonEvent.getString("readPoint"))));
		}
	}

	public static void putBizLocation(JsonObject base, Document bsonEvent) {
		if (bsonEvent.containsKey("bizLocation")) {
			base.put("bizLocation", new JsonObject().put("id", DLConverter.getDL(bsonEvent.getString("bizLocation"))));
		}
	}

	public static void putBizTransactionList(JsonObject base, Document bsonEvent) {
		JsonArray convertedBizTransactionList = new JsonArray();
		
		if (bsonEvent.getList("bizTransactionList", org.bson.Document.class) == null)
			return;
		
		for (org.bson.Document bizTransaction : bsonEvent.getList("bizTransactionList", org.bson.Document.class)) {
			JsonObject convertedBizTransaction = new JsonObject();
			for (String bizTransactionKey : bizTransaction.keySet()) {
                String type = JSONEPCISEventReadConverter.decodeMongoObjectKey(bizTransactionKey);
                if(type != null){
                    if(type.contains("urn:epcglobal:cbv:btt:")){
                        String[] arr = type.split(":");
                        convertedBizTransaction.put("type", arr[arr.length-1]);
                    }else{
                        convertedBizTransaction.put("type", type);
                    }
                }
				convertedBizTransaction.put("bizTransaction", bizTransaction.get(bizTransactionKey));
			}
			convertedBizTransactionList.add(convertedBizTransaction);
		}
		base.put("bizTransactionList", convertedBizTransactionList);
	}

	public static void putExtension(JsonObject base, Document bsonEvent, ArrayList<String> namespaces, JsonObject extType) {
		if(!bsonEvent.containsKey("extension"))
			return;
		JsonObject ext = JSONEPCISEventReadConverter.getExtension(bsonEvent.get("extension", org.bson.Document.class),
				namespaces, extType);
		ext.forEach(entry -> base.put(entry.getKey(), entry.getValue()));
	}

	public static void putILMD(JsonObject base, Document bsonEvent, ArrayList<String> namespaces, JsonObject extType) {
		if(!bsonEvent.containsKey("ilmd"))
			return;
		JsonObject ext = JSONEPCISEventReadConverter.getExtension(bsonEvent.get("ilmd", org.bson.Document.class),
				namespaces, extType);
		ext.forEach(entry -> base.put(entry.getKey(), entry.getValue()));
	}

	public static void putQuantityList(JsonObject base, Document bsonEvent) {
		if (bsonEvent.containsKey("quantityList")) {
			List<org.bson.Document> quantityList = bsonEvent.getList("quantityList", org.bson.Document.class);
			JsonArray convertedQuantityList = new JsonArray();
			for (org.bson.Document quantity : quantityList) {
                if(quantity.containsKey("epcClass")){
                    quantity.put("epcClass", DLConverter.getDL(quantity.getString("epcClass")));
                }
				convertedQuantityList.add(new JsonObject(quantity.toJson()));
			}
			base.put("quantityList", convertedQuantityList);
		}
	}

	public static void putInputQuantityList(JsonObject base, Document bsonEvent) {
		if (bsonEvent.containsKey("inputQuantityList")) {
			List<org.bson.Document> quantityList = bsonEvent.getList("inputQuantityList", org.bson.Document.class);
			JsonArray convertedQuantityList = new JsonArray();
			for (org.bson.Document quantity : quantityList) {
                if(quantity.containsKey("epcClass")){
                    quantity.put("epcClass", DLConverter.getDL(quantity.getString("epcClass")));
                }
				convertedQuantityList.add(new JsonObject(quantity.toJson()));
			}
			base.put("inputQuantityList", convertedQuantityList);
		}
	}

	public static void putOutputQuantityList(JsonObject base, Document bsonEvent) {
		if (bsonEvent.containsKey("outputQuantityList")) {
			List<org.bson.Document> quantityList = bsonEvent.getList("outputQuantityList", org.bson.Document.class);
			JsonArray convertedQuantityList = new JsonArray();
			for (org.bson.Document quantity : quantityList) {
                if(quantity.containsKey("epcClass")){
                    quantity.put("epcClass", DLConverter.getDL(quantity.getString("epcClass")));
                }
				convertedQuantityList.add(new JsonObject(quantity.toJson()));
			}
			base.put("outputQuantityList", convertedQuantityList);
		}
	}

	public static void putChildQuantityList(JsonObject base, String baseKey, Document bsonEvent) {
		if (bsonEvent.containsKey(baseKey)) {
			List<org.bson.Document> quantityList = bsonEvent.getList(baseKey, org.bson.Document.class);
			JsonArray convertedQuantityList = new JsonArray();
			for (org.bson.Document quantity : quantityList) {
                if(quantity.containsKey("epcClass")){
                    quantity.put("epcClass", DLConverter.getDL(quantity.getString("epcClass")));
                }
				convertedQuantityList.add(new JsonObject(quantity.toJson()));
			}
			base.put("childQuantityList", convertedQuantityList);
		}
	}

	public static void putSourceList(JsonObject base, Document bsonEvent) {
		if (bsonEvent.containsKey("sourceList")) {
			JsonArray convertedSourceList = new JsonArray();
			for (org.bson.Document source : bsonEvent.getList("sourceList", org.bson.Document.class)) {
				JsonObject convertedSource = new JsonObject();
				for (String sourceKey : source.keySet()) {
                    String type = JSONEPCISEventReadConverter.decodeMongoObjectKey(sourceKey);
                    if(type != null){
                        if(type.contains("urn:epcglobal:cbv:sdt:")){
                            String[] arr = type.split(":");
                            convertedSource.put("type", arr[arr.length-1]);
                        }else{
                            convertedSource.put("type", type);
                        }
                    }
					convertedSource.put("source", source.get(sourceKey));
				}
				convertedSourceList.add(convertedSource);
			}
			base.put("sourceList", convertedSourceList);
		}
	}

	public static void putDestinationList(JsonObject base, Document bsonEvent) {
		if (bsonEvent.containsKey("destinationList")) {
			JsonArray convertedDestinationList = new JsonArray();
			for (org.bson.Document destination : bsonEvent.getList("destinationList", org.bson.Document.class)) {
				JsonObject convertedDestination = new JsonObject();
				for (String destinationKey : destination.keySet()) {
                    String type = JSONEPCISEventReadConverter.decodeMongoObjectKey(destinationKey);
                    if(type != null){
                        if(type.contains("urn:epcglobal:cbv:sdt:")){
                            String[] arr = type.split(":");
                            convertedDestination.put("type", arr[arr.length-1]);
                        }else{
                            convertedDestination.put("type", type);
                        }
                    }
					convertedDestination.put("destination", destination.get(destinationKey));
				}
				convertedDestinationList.add(convertedDestination);
			}
			base.put("destinationList", convertedDestinationList);
		}
	}

	public static void putPersistentDisposition(JsonObject base, Document bsonEvent) {
		if (bsonEvent.containsKey("persistentDisposition")) {
			JsonObject convertedPersistentDisposition = new JsonObject();
			org.bson.Document persistentDisposition = bsonEvent.get("persistentDisposition", org.bson.Document.class);
			List<String> unsetList = persistentDisposition.getList("unset", String.class);
			JsonArray convertedUnsetList = new JsonArray();
			for (String unset : unsetList) {
                if(unset.contains("urn:epcglobal:cbv:disp:")){
                    String[] arr = unset.split(":");
                    convertedUnsetList.add(arr[arr.length-1]);
                }else{
                    convertedUnsetList.add(unset);
                }
			}
			if (!convertedUnsetList.isEmpty())
				convertedPersistentDisposition.put("unset", convertedUnsetList);

			List<String> setList = persistentDisposition.getList("set", String.class);
			JsonArray convertedSetList = new JsonArray();
			for (String set : setList) {
                if(set.contains("urn:epcglobal:cbv:disp:")){
                    String[] arr = set.split(":");
					convertedSetList.add(arr[arr.length-1]);
                }else{
					convertedSetList.add(set);
                }
			}
			if (!convertedSetList.isEmpty())
				convertedPersistentDisposition.put("set", convertedSetList);
			if (!convertedPersistentDisposition.isEmpty())
				base.put("persistentDisposition", convertedPersistentDisposition);
		}
	}

	public static void putSensorElementList(JsonObject base, Document bsonEvent, ArrayList<String> namespaces, JsonObject extType) {
		// sensorElementList
		if (bsonEvent.containsKey("sensorElementList")) {
			List<org.bson.Document> sensorElementList = bsonEvent.getList("sensorElementList", org.bson.Document.class);
			JsonArray convertedSensorElementList = new JsonArray();
			for (org.bson.Document sensorElementDoc : sensorElementList) {
				JsonObject sensorElement = new JsonObject(sensorElementDoc.toJson());
				if (sensorElement.containsKey("sensorMetadata")) {
					JsonObject sensorMetadata = sensorElement.getJsonObject("sensorMetadata");
					if (sensorMetadata.containsKey("time")) {
						sensorMetadata.put("time",
								JSONEPCISEventReadConverter.getQueriedDateTime(sensorMetadata.getLong("time")));
					}
					if (sensorMetadata.containsKey("startTime")) {
						sensorMetadata.put("startTime",
								JSONEPCISEventReadConverter.getQueriedDateTime(sensorMetadata.getLong("startTime")));
					}
					if (sensorMetadata.containsKey("endTime")) {
						sensorMetadata.put("endTime",
								JSONEPCISEventReadConverter.getQueriedDateTime(sensorMetadata.getLong("endTime")));
					}
					if (sensorMetadata.containsKey("otherAttributes")) {
						JsonObject otherAttributes = sensorMetadata.getJsonObject("otherAttributes");
						if (otherAttributes != null && !otherAttributes.isEmpty()) {
							JsonObject convertedAttributes = JSONEPCISEventReadConverter.getExtension(otherAttributes,
									namespaces, extType);
							convertedAttributes.forEach(e -> sensorMetadata.put(e.getKey(), e.getValue()));
						}
						sensorMetadata.remove("otherAttributes");
					}
				}

				if (sensorElement.containsKey("sensorReport")) {
					JsonArray sensorReportList = sensorElement.getJsonArray("sensorReport");
					for (Object sensorReportObject : sensorReportList) {
						JsonObject sensorReport = (JsonObject) sensorReportObject;
						if (sensorReport.containsKey("time")) {
							sensorReport.put("time",
									JSONEPCISEventReadConverter.getQueriedDateTime(sensorReport.getLong("time")));
						}

						if (sensorReport.containsKey("hexBinaryValue")) {
							sensorReport.put("hexBinaryValue",
									DatatypeConverter.printHexBinary(
											Base64.getDecoder().decode(sensorReport.getJsonObject("hexBinaryValue")
													.getJsonObject("$binary").getString("base64"))));
						}
						if (sensorReport.containsKey("otherAttributes")) {
							JsonObject otherAttributes = sensorReport.getJsonObject("otherAttributes");
							if (otherAttributes != null && !otherAttributes.isEmpty()) {
								JsonObject convertedAttributes = JSONEPCISEventReadConverter
										.getExtension(otherAttributes, namespaces, extType);
								convertedAttributes.forEach(e -> sensorReport.put(e.getKey(), e.getValue()));
							}
							sensorReport.remove("otherAttributes");
						}
						if(sensorReport.containsKey("type")){
							String type = sensorReport.getString("type");
							String[] arr = type.split(":");
							sensorReport.put("type", arr[arr.length-1]);
						}
						sensorReport.remove("rValue");
						sensorReport.remove("rType");
					}
				}

				if (sensorElement.containsKey("extension")) {
					JsonObject ext = sensorElement.getJsonObject("extension");
					if (ext != null && !ext.isEmpty()) {
						JsonObject convertedExt = JSONEPCISEventReadConverter.getExtension(ext, namespaces, extType);
						convertedExt.forEach(e -> sensorElement.put(e.getKey(), e.getValue()));
					}
					sensorElement.remove("extension");
				}
				sensorElement.remove("sef");
				convertedSensorElementList.add(sensorElement);
			}
			base.put("sensorElementList", convertedSensorElementList);
		}
	}

	public static String getQueriedDateTime(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		return sdf.format(time);
	}

	public static String getDL(String epc) {
		return DLConverter.getDL(epc);
	}

	public static JsonArray getDLInstanceList(JsonArray instanceList) {
		JsonArray dlArray = new JsonArray();
		for (int i = 0; i < instanceList.size(); i++) {
			dlArray.add(getDL(instanceList.getString(i)));
		}
		return dlArray;
	}

	public static JsonArray getDLClassList(JsonArray quantityList) {
		JsonArray dlArray = new JsonArray();
		for (int i = 0; i < quantityList.size(); i++) {
			JsonObject quantity = quantityList.getJsonObject(i);
			dlArray.add(quantity.put("epcClass", getDL(quantity.getString("epcClass"))));
		}
		return dlArray;
	}

	public static void getNamespaces(ArrayList<String> namespaces, org.bson.Document inner) {
		for (Entry<String, Object> entry : inner.entrySet()) {
			String nonDecodedNamespace = entry.getKey();
			String[] arr = nonDecodedNamespace.split("#");
			if (arr.length == 2) {
				if (!namespaces.contains(arr[0])) {
					namespaces.add(arr[0]);
				}
				if (entry.getValue() instanceof org.bson.Document) {
					getNamespaces(namespaces, (org.bson.Document) entry.getValue());
				}
			}

		}
	}

	public static ArrayList<String> getNamespaces(List<org.bson.Document> events) {
		ArrayList<String> namespaces = new ArrayList<String>();
		for (org.bson.Document event : events) {
			org.bson.Document ext = event.get("extension", org.bson.Document.class);
			if (ext != null) {
				getNamespaces(namespaces, ext);
			}
			List<org.bson.Document> sel = event.getList("sensorElementList", org.bson.Document.class);
			if (sel != null) {
				for (org.bson.Document se : sel) {
					org.bson.Document seExt = se.get("extension", org.bson.Document.class);
					if (seExt != null) {
						getNamespaces(namespaces, seExt);
					}
				}
			}

			org.bson.Document errorDeclaration = event.get("errorDeclaration", org.bson.Document.class);
			if (errorDeclaration != null) {
				org.bson.Document errExt = errorDeclaration.get("extension", org.bson.Document.class);
				if (errExt != null) {
					getNamespaces(namespaces, errExt);
				}
			}
		}
		return namespaces;
	}

	public static String decodeMongoObjectKey(String key) {
		key = key.replace("\uff0e", ".");
		return key;
	}

	public static void putCommonEventFields(JsonObject converted, Document bsonEvent, ArrayList<String> namespaces, JsonObject extType) {

		// Event Time
		converted.put("eventTime", getQueriedDateTime(bsonEvent.getLong("eventTime")));

		// Event Time Zone
		converted.put("eventTimeZoneOffset", bsonEvent.getString("eventTimeZoneOffset"));

		// Record Time
		converted.put("recordTime", getQueriedDateTime(bsonEvent.getLong("recordTime")));

		// Event ID
		converted.put("eventID", bsonEvent.getString("eventID"));

		// Error Declaration
		Document errorDeclaration = bsonEvent.get("errorDeclaration", Document.class);
		JsonObject convertedErrorDeclaration = new JsonObject();
		if (errorDeclaration != null && !errorDeclaration.isEmpty()) {
			convertedErrorDeclaration.put("declarationTime",
					getQueriedDateTime(errorDeclaration.getLong("declarationTime")));
			if (errorDeclaration.containsKey("reason")){
				String reason = errorDeclaration.getString("reason");
				if(reason.contains("urn:epcglobal:cbv:er:")){
					String[] arr = reason.split(":");
					convertedErrorDeclaration.put("reason", arr[arr.length-1]);
				}else{
					convertedErrorDeclaration.put("reason", errorDeclaration.getString("reason"));
				}
			}
			if (errorDeclaration.containsKey("correctiveEventIDs")) {
				convertedErrorDeclaration.put("correctiveEventIDs",
						errorDeclaration.getList("correctiveEventIDs", String.class));
			}
			if (errorDeclaration.containsKey("extension")) {
				JsonObject ext = getExtension(errorDeclaration.get("extension", org.bson.Document.class), namespaces, extType);
				ext.forEach(entry -> convertedErrorDeclaration.put(entry.getKey(), entry.getValue()));
			}
			converted.put("errorDeclaration", convertedErrorDeclaration);
		}

		// CertificationInfo
		if(bsonEvent.containsKey("certificationInfo")){
			Object ci = bsonEvent.get("certificationInfo");
			if(ci instanceof String){
				converted.put("certificationInfo", (String)ci);
			}else if(ci instanceof List){
				JsonArray cir = new JsonArray();
				List<?> cil = (List<?>)ci;
				for(Object cio: cil){
					cir.add(cio.toString());
				}
			}
		}
	}

	public static JsonArray getExtension(List<?> innerList, ArrayList<String> namespaces, JsonObject extType) {
		JsonArray convertedInnerArray = new JsonArray();
		for(Object inner: innerList) {
			if(inner instanceof org.bson.Document) {
				convertedInnerArray.add(getExtension((Document) inner, namespaces, extType));
			}else {
				convertedInnerArray.add(inner);
			}
		}
		return convertedInnerArray;
	}

	public static JsonObject getExtension(Document document, ArrayList<String> namespaces, JsonObject extType) {
		JsonObject convertedExt = new JsonObject();
		for (Entry<String, Object> entry : document.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			String[] arr = key.split("#");
			if (arr.length > 1) {
				int idx = namespaces.indexOf(arr[0]);
				if (idx != -1) {
					key = "ext" + idx + ":" + decodeMongoObjectKey(arr[1]);
				}
			} else {
				key = decodeMongoObjectKey(key);
			}
			if (value instanceof org.bson.Document) {
				convertedExt.put(key, getExtension((Document) value, namespaces, extType));
			} else if (value instanceof List) {
				convertedExt.put(key, getExtension((List<?>) value, namespaces, extType));
			} else {
				if(value instanceof Double){
					extType.put(key, new JsonObject().put("@type", "xsd:double"));
					convertedExt.put(key, value.toString());
				}else if(value instanceof Integer){
					extType.put(key, new JsonObject().put("@type", "xsd:int"));
					convertedExt.put(key, value.toString());
				}else if(value instanceof Long){
					extType.put(key, new JsonObject().put("@type", "xsd:dateTime"));
					convertedExt.put(key, getQueriedDateTime((Long)value));
				}else if(value instanceof Boolean){
					extType.put(key, new JsonObject().put("@type", "xsd:boolean"));
					convertedExt.put(key, value.toString());
				}else{
					convertedExt.put(key, value.toString());
				}
			}
		}
		return convertedExt;
	}

	public static JsonArray getExtension(JsonArray innerArray, ArrayList<String> namespaces, JsonObject extType) {
		JsonArray convertedInnerArray = new JsonArray();
		for(Object inner: innerArray) {
			if(inner instanceof JsonObject) {
				convertedInnerArray.add(getExtension((JsonObject) inner, namespaces, extType));
			}else if(inner instanceof JsonArray){
				convertedInnerArray.add(getExtension((JsonArray) inner, namespaces, extType));
			}else {
				convertedInnerArray.add(inner.toString());
			}
		}
		return convertedInnerArray;
	}

	public static JsonObject getExtension(JsonObject jsonObject, ArrayList<String> namespaces, JsonObject extType) {
		JsonObject convertedExt = new JsonObject();
		for (String key : jsonObject.fieldNames()) {
			Object value = jsonObject.getValue(key);
			String[] arr = key.split("#");
			if (arr.length > 1) {
				int idx = namespaces.indexOf(arr[0]);
				if (idx != -1) {
					key = "ext" + idx + ":" + decodeMongoObjectKey(arr[1]);
				}
			} else {
				key = decodeMongoObjectKey(key);
			}
			if (value instanceof JsonObject) {
				convertedExt.put(key, getExtension((JsonObject) value, namespaces, extType));
			} else if(value instanceof JsonArray){
				convertedExt.put(key, getExtension((JsonArray) value, namespaces, extType));
			}else {
				if(value instanceof Double){
					extType.put(key, new JsonObject().put("@type", "xsd:double"));
					convertedExt.put(key, value.toString());
				}else if(value instanceof Integer){
					extType.put(key, new JsonObject().put("@type", "xsd:int"));
					convertedExt.put(key, value.toString());
				}else if(value instanceof Long){
					extType.put(key, new JsonObject().put("@type", "xsd:dateTime"));
					convertedExt.put(key, getQueriedDateTime((Long)value));
				}else if(value instanceof Boolean){
					extType.put(key, new JsonObject().put("@type", "xsd:boolean"));
					convertedExt.put(key, value.toString());
				}else{
					convertedExt.put(key, value.toString());
				}
			}
		}
		return convertedExt;
	}
}
