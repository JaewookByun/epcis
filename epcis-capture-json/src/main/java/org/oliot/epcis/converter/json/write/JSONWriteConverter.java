package org.oliot.epcis.converter.json.write;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.BulkOperation;

import org.oliot.epcis.capture.JSONCaptureServer;
import org.oliot.epcis.model.cbv.*;
import org.oliot.epcis.transaction.Transaction;
import org.oliot.epcis.util.BSONWriteUtil;
import org.oliot.gcp.core.DLConverter;

import javax.xml.bind.DatatypeConverter;

/**
 * Copyright (C) 2020-2022. (Jaewook Byun) all rights reserved.
 * <p>
 * Oliot EPCIS X is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * Among various modules, epcis-capture-json acts as a server to receive
 * JSON-formatted EPCIS documents to capture events in the documents into an
 * EPCIS repository.
 * <p>
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr
 *         <p>
 *         Associate Director, Auto-ID Labs, KAIST, bjw0829@kaist.ac.kr
 */
public class JSONWriteConverter {

	public static JsonObject convertEvent(JsonObject context, JsonObject event, Transaction tx) {

		// type: use as itself

		// Event Time
		event.put("eventTime", getStorableDateTime(event.getString("eventTime")));
		// Event Time Zone: No change
		// Record Time
		event.put("recordTime", System.currentTimeMillis());

		// eventID: use as itself

		// certificationInfo: new in ratified schema

		// parent ID
		if (event.containsKey("parentID")) {
			String epc = getEPC(event.getString("parentID"));
			if (epc != null) {
				event.put("parentID", epc);
			}
		}

		if (event.containsKey("childEPCs")) {
			// child EPCs: using epcList for query efficiency
			JsonArray newArray = new JsonArray();
			for (Object elem : (JsonArray) event.remove("childEPCs")) {
				String epc = getEPC((String) elem);
				if (epc != null) {
					newArray.add(epc);
				} else
					newArray.add((String) elem);
			}
			event.put("epcList", newArray);
		} else if (event.containsKey("epcList")) {
			// epcList
			JsonArray newArray = new JsonArray();
			for (Object elem : event.getJsonArray("epcList")) {
				String epc = getEPC((String) elem);
				if (epc != null) {
					newArray.add(epc);
				} else
					newArray.add((String) elem);
			}
			event.put("epcList", newArray);
		}
		// inputEPCList
		if (event.containsKey("inputEPCList")) {
			JsonArray newArray = new JsonArray();
			for (Object elem : event.getJsonArray("inputEPCList")) {
				String epc = getEPC((String) elem);
				if (epc != null) {
					newArray.add(epc);
				} else
					newArray.add((String) elem);
			}
			event.put("inputEPCList", newArray);
		}
		// outputEPCList
		if (event.containsKey("outputEPCList")) {
			JsonArray newArray = new JsonArray();
			for (Object elem : event.getJsonArray("outputEPCList")) {
				String epc = getEPC((String) elem);
				if (epc != null) {
					newArray.add(epc);
				} else
					newArray.add((String) elem);
			}
			event.put("outputEPCList", newArray);
		}
		if (event.containsKey("childQuantityList")) {
			// child quantityList in association event: using QuantityList for query
			// efficiency
			JsonArray newArray = new JsonArray();
			JsonArray array = event.getJsonArray("childQuantityList");
			event.remove("childQuantityList");
			for (Object elem : array) {
				JsonObject qElem = (JsonObject) elem;
				if (qElem.containsKey("epcClass")) {
					String epcClass = getEPC(qElem.getString("epcClass"));
					if (epcClass != null) {
						qElem.put("epcClass", epcClass);
					}
				}
				if (qElem.containsKey("quantity")) {
					qElem.put("quantity", Double.valueOf(qElem.getValue("quantity").toString()));
				}
				newArray.add(qElem);
			}
			event.put("quantityList", newArray);
		} else if (event.containsKey("quantityList")) {
			// quantityList
			JsonArray newArray = new JsonArray();
			for (Object elem : event.getJsonArray("quantityList")) {
				JsonObject qElem = (JsonObject) elem;
				if (qElem.containsKey("epcClass")) {
					String epcClass = getEPC(qElem.getString("epcClass"));
					if (epcClass != null) {
						qElem.put("epcClass", epcClass);
					}
				}
				if (qElem.containsKey("quantity")) {
					qElem.put("quantity", Double.valueOf(qElem.getValue("quantity").toString()));
				}
				newArray.add(qElem);
			}
			event.put("quantityList", newArray);
		}
		// inputQuantityList
		if (event.containsKey("inputQuantityList")) {
			JsonArray newArray = new JsonArray();
			for (Object elem : event.getJsonArray("inputQuantityList")) {
				JsonObject qElem = (JsonObject) elem;
				if (qElem.containsKey("epcClass")) {
					String epcClass = getEPC(qElem.getString("epcClass"));
					if (epcClass != null) {
						qElem.put("epcClass", epcClass);
					}
				}
				if (qElem.containsKey("quantity")) {
					qElem.put("quantity", Double.valueOf(qElem.getValue("quantity").toString()));
				}
				newArray.add(qElem);
			}
			event.put("inputQuantityList", newArray);
		}
		// outputQuantityList
		if (event.containsKey("outputQuantityList")) {
			JsonArray newArray = new JsonArray();
			for (Object elem : event.getJsonArray("outputQuantityList")) {
				JsonObject qElem = (JsonObject) elem;
				if (qElem.containsKey("epcClass")) {
					String epcClass = getEPC(qElem.getString("epcClass"));
					if (epcClass != null) {
						qElem.put("epcClass", epcClass);
					}
				}
				if (qElem.containsKey("quantity")) {
					qElem.put("quantity", Double.valueOf(qElem.getValue("quantity").toString()));
				}
				newArray.add(qElem);
			}
			event.put("outputQuantityList", newArray);
		}
		// action: No change
		// bizStep: No change -> change
		if (event.containsKey("bizStep")) {
			// standard vocabulary in brief form should change to its full name
			// (compatibility with XML)
			event.put("bizStep", BusinessStep.getFullVocabularyName(event.getString("bizStep")));
		}
		// disposition: No change
		if (event.containsKey("disposition")) {
			// standard vocabulary in brief form should change to its full name
			// (compatibility with XML)
			event.put("disposition", Disposition.getFullVocabularyName(event.getString("disposition")));
		}

		// readPoint: "readPoint": {"id": "urn:epc:id:sgln:4012345.00001.0"}, -> string
		if (event.containsKey("readPoint")) {
			JsonObject readPoint = event.getJsonObject("readPoint");
			event.put("readPoint", readPoint.getString("id"));
		}
		// bizLocation: "bizLocation": {"id": "urn:epc:id:sgln:4012345.00002.0"}, ->
		// string
		if (event.containsKey("bizLocation")) {
			JsonObject bizLocation = event.getJsonObject("bizLocation");
			event.put("bizLocation", bizLocation.getString("id"));
		}
		// bizTransactionList
		if (event.containsKey("bizTransactionList")) {
			JsonArray arr = event.getJsonArray("bizTransactionList");
			JsonArray newBizTransactionArr = new JsonArray();
			for (Object elem : arr) {
				JsonObject elemObj = (JsonObject) elem;
				// type is optional field
				String type = elemObj.getString("type");
				String value = elemObj.getString("bizTransaction");
				if (type == null) {
					newBizTransactionArr.add(new JsonObject().put("", value));
				} else {
					newBizTransactionArr.add(new JsonObject()
							.put(encodeMongoObjectKey(BusinessTransactionType.getFullVocabularyName(type)), value));
				}

			}
			event.put("bizTransactionList", newBizTransactionArr);
		}
		// sourceList
		if (event.containsKey("sourceList")) {
			JsonArray arr = event.getJsonArray("sourceList");
			JsonArray newBizTransactionArr = new JsonArray();
			for (Object elem : arr) {
				JsonObject elemObj = (JsonObject) elem;
				// type is mandatory
				newBizTransactionArr.add(new JsonObject().put(encodeMongoObjectKey(SourceDestinationType.getFullVocabularyName(elemObj.getString("type"))), elemObj.getString("source")));
			}
			event.put("sourceList", newBizTransactionArr);
		}
		// destinationList
		if (event.containsKey("destinationList")) {
			JsonArray arr = event.getJsonArray("destinationList");
			JsonArray newBizTransactionArr = new JsonArray();
			for (Object elem : arr) {
				JsonObject elemObj = (JsonObject) elem;
				// type is mandatory				
				newBizTransactionArr.add(new JsonObject().put(encodeMongoObjectKey(SourceDestinationType.getFullVocabularyName(elemObj.getString("type"))), elemObj.getString("destination")));
			}
			event.put("destinationList", newBizTransactionArr);
		}
		// quantityList: No change
		// ILMD
		if (event.containsKey("ilmd")) {
			JsonObject ilmd = event.getJsonObject("ilmd");
			ilmd = getStorableExtension(context, ilmd);
			event.put("ilmd", ilmd);
			BSONWriteUtil.putFlatten(event, "ilmdf", ilmd);
		}

		// persistent disposition
		if (event.containsKey("persistentDisposition")) {
			JsonObject pd = event.getJsonObject("persistentDisposition");
			if (pd.containsKey("set")) {
				JsonArray oldSet = pd.getJsonArray("set");
				JsonArray newSet = new JsonArray();
				for (int i = 0; i < oldSet.size(); i++) {
					newSet.add(Disposition.getFullVocabularyName(oldSet.getString(i)));
				}
				pd.put("set", newSet);
			}
			if (pd.containsKey("unset")) {
				JsonArray oldSet = pd.getJsonArray("unset");
				JsonArray newSet = new JsonArray();
				for (int i = 0; i < oldSet.size(); i++) {
					newSet.add(Disposition.getFullVocabularyName(oldSet.getString(i)));
				}
				pd.put("unset", newSet);
			}
		}

		// sensorElementList
		if (event.containsKey("sensorElementList")) {
			JsonArray sensorElementList = event.getJsonArray("sensorElementList");
			for (Object sensorElementObj : sensorElementList) {
				JsonObject sensorElement = (JsonObject) sensorElementObj;
				if (sensorElement.containsKey("isA"))
					sensorElement.remove("isA");
				if (sensorElement.containsKey("sensorMetadata")) {
					JsonObject sensorMetadata = sensorElement.getJsonObject("sensorMetadata");
					if (sensorMetadata.containsKey("time")) {
						sensorMetadata.put("time", getStorableDateTime(sensorMetadata.getString("time")));
					}
					if (sensorMetadata.containsKey("startTime")) {
						sensorMetadata.put("startTime", getStorableDateTime(sensorMetadata.getString("startTime")));
					}
					if (sensorMetadata.containsKey("endTime")) {
						sensorMetadata.put("endTime", getStorableDateTime(sensorMetadata.getString("endTime")));
					}
					JsonObject extension = retrieveExtension(sensorMetadata);
					JsonObject convertedExt = getStorableExtension(context, extension);
					if (!convertedExt.isEmpty())
						sensorMetadata.put("otherAttributes", convertedExt);
				}

				if (sensorElement.containsKey("sensorReport")) {
					JsonArray sensorReport = sensorElement.getJsonArray("sensorReport");
					for (Object sensorReportElementObj : sensorReport) {
						JsonObject sensorReportElement = (JsonObject) sensorReportElementObj;
						if (sensorReportElement.containsKey("type")) {
							sensorReportElement.put("type", Measurement.getFullVocabularyName(sensorReportElement.getString("type")));
						}
						if (sensorReportElement.containsKey("time")) {
							sensorReportElement.put("time", getStorableDateTime(sensorReportElement.getString("time")));
						}
						if (sensorReportElement.containsKey("hexBinaryValue")) {
							sensorReportElement.put("hexBinaryValue", new JsonObject().put("$binary",
									DatatypeConverter.parseHexBinary(sensorReportElement.getString("hexBinaryValue"))));
						}

						if (sensorReportElement.containsKey("value")) {
							sensorReportElement.put("value", sensorReportElement.getDouble("value"));
						}
						if (sensorReportElement.containsKey("minValue")) {
							sensorReportElement.put("minValue", sensorReportElement.getDouble("minValue"));
						}
						if (sensorReportElement.containsKey("maxValue")) {
							sensorReportElement.put("maxValue", sensorReportElement.getDouble("maxValue"));
						}
						if (sensorReportElement.containsKey("sDev")) {
							sensorReportElement.put("sDev", sensorReportElement.getDouble("sDev"));
						}
						if (sensorReportElement.containsKey("meanValue")) {
							sensorReportElement.put("meanValue", sensorReportElement.getDouble("meanValue"));
						}
						if (sensorReportElement.containsKey("percRank")) {
							sensorReportElement.put("percRank", sensorReportElement.getDouble("percRank"));
						}
						if (sensorReportElement.containsKey("percValue")) {
							sensorReportElement.put("percValue", sensorReportElement.getDouble("percValue"));
						}
						JsonObject extension = retrieveExtension(sensorReportElement);
						JsonObject convertedExt = getStorableExtension(context, extension);
						if (!convertedExt.isEmpty())
							sensorReportElement.put("otherAttributes", convertedExt);

						String uom = sensorReportElement.getString("uom");
						Double value = sensorReportElement.getDouble("value");
						if (uom != null && value != null) {
							String rType = JSONCaptureServer.unitConverter.getRepresentativeType(uom);
							Double rValue = JSONCaptureServer.unitConverter.getRepresentativeValue(uom, value);
							if (rValue != null && rType != null) {
								sensorReportElement.put("rValue", rValue);
								sensorReportElement.put("rType", rType);
							}
						}

					}
				}

				JsonObject extension = retrieveExtension(sensorElement);
				if (!extension.isEmpty()) {
					extension = getStorableExtension(context, extension);
					sensorElement.put("extension", extension);
					BSONWriteUtil.putFlatten(sensorElement, "sef", extension);
				}

			}
		}

		// Error Declaration
		if (event.containsKey("errorDeclaration")) {
			JsonObject errorDeclaration = event.getJsonObject("errorDeclaration");
			errorDeclaration.put("declarationTime", getStorableDateTime(errorDeclaration.getString("declarationTime")));

			if (errorDeclaration.containsKey("reason")) {
				errorDeclaration.put("reason", ErrorReason.getFullVocabularyName(errorDeclaration.getString("reason")));
			}

			JsonObject extension = retrieveExtension(errorDeclaration);
			if (!extension.isEmpty()) {
				extension = getStorableExtension(context, extension);
				errorDeclaration.put("extension", getStorableExtension(context, extension));
				BSONWriteUtil.putFlatten(event, "errf", extension);
			}
		}

		// vendor extension
		// Collect extension fields
		JsonObject extension = retrieveExtension(event);
		if (!extension.isEmpty()) {
			extension = getStorableExtension(context, extension);
			event.put("extension", extension);
			BSONWriteUtil.putFlatten(event, "extf", extension);
		}

		// put event id
		if (!event.containsKey("eventID")) {
			BSONWriteUtil.putEventHashID(event);
		}
		
		event.put("_tx", tx.getTxId());

		return event;
	}

	public static JsonObject retrieveExtension(JsonObject object) {
		Iterator<String> extFieldIter = object.fieldNames().iterator();
		JsonObject extension = new JsonObject();
		while (extFieldIter.hasNext()) {
			String extField = extFieldIter.next();
			if (extField.contains(":")) {
				extension.put(extField, object.getValue(extField));
			}
		}
		for (String removalField : extension.fieldNames()) {
			object.remove(removalField);
		}
		return extension;
	}

	public static JsonObject getStorableExtension(JsonObject context, JsonObject ext) {
		JsonObject storable = new JsonObject();
		for (String key : ext.fieldNames()) {
			String[] fieldArr = key.split(":");
			String extKey;
			if (fieldArr.length == 1) {
				extKey = "#" + key;
			} else {
				extKey = context.getString(fieldArr[0]) + "#" + fieldArr[1];
			}
			Object extRawValue = ext.getValue(key);

			storable.put(encodeMongoObjectKey(extKey), getStorableExtension(context, key, extRawValue));

			/*
			 * if (extRawValue instanceof String) { Long time = getStorableDateTime((String)
			 * extRawValue); if (time == null) { storable.put(encodeMongoObjectKey(extKey),
			 * (String) extRawValue); } else { storable.put(encodeMongoObjectKey(extKey),
			 * time); } } else if (extRawValue instanceof Integer) {
			 * storable.put(encodeMongoObjectKey(extKey), (Integer) extRawValue); } else if
			 * (extRawValue instanceof Double) { storable.put(encodeMongoObjectKey(extKey),
			 * (Double) extRawValue); } else if (extRawValue instanceof Boolean) {
			 * storable.put(encodeMongoObjectKey(extKey), (Boolean) extRawValue); } else if
			 * (extRawValue instanceof JsonObject) { JsonObject extValue = (JsonObject)
			 * extRawValue; Object extObj = getStorableExtension(context, extValue);
			 * storable.put(encodeMongoObjectKey(extKey), extObj); }
			 *
			 */
		}
		return storable;
	}

	public static JsonArray getStorableExtension(JsonObject context, JsonArray extRawValue) {
		JsonArray newExtArray = new JsonArray();
		for (Object elem : extRawValue) {
			if (elem instanceof JsonObject) {
				newExtArray.add(getStorableExtension(context, (JsonObject) elem));
			} else if (elem instanceof JsonArray) {
				newExtArray.add(getStorableExtension(context, (JsonArray) elem));
			} else {
				newExtArray.add(elem.toString());
			}
		}
		return newExtArray;
	}

	public static Object getStorableExtension(JsonObject context, String key, Object extRawValue) {
		if (extRawValue instanceof String) {
			if (context.containsKey(key) && context.getJsonObject(key).containsKey("@type")) {
				String type = context.getJsonObject(key).getString("@type");
				try {
					if (type.equals("xsd:int")) {
						return Integer.parseInt((String) extRawValue);
					} else if (type.equals("xsd:double")) {
						return Double.parseDouble((String) extRawValue);
					} else if (type.equals("xsd:boolean")) {
						return Boolean.parseBoolean((String) extRawValue);
					} else if (type.equals("xsd:dateTimeStamp")) {
						Long time = getStorableDateTime((String) extRawValue);
						if (time == null)
							return extRawValue;
						else
							return time;
					} else {
						return extRawValue;
					}
				} catch (Exception e) {
					return extRawValue;
				}
			}
			return extRawValue;
		} else if (extRawValue instanceof JsonObject) {
			return getStorableExtension(context, (JsonObject) extRawValue);
		} else if (extRawValue instanceof JsonArray) {
			return getStorableExtension(context, (JsonArray) extRawValue);
		}
		/*
		 * if (extRawValue instanceof String) { System.out.println(extRawValue); // key:
		 * rail:vehicleMasterGIAI if(context.containsKey(key) &&
		 * context.getJsonObject(key).containsKey("@type")){ String type =
		 * context.getJsonObject(key).getString("@type");
		 * if(type.equals("xsd:integer")){
		 * 
		 * }else if(type.equals()) }
		 * 
		 * Long time = getStorableDateTime((String) extRawValue); return
		 * Objects.requireNonNullElseGet(time, () -> (String) extRawValue); } else if
		 * (extRawValue instanceof Integer || extRawValue instanceof Double ||
		 * extRawValue instanceof Boolean) { return extRawValue; } else if (extRawValue
		 * instanceof Double) { return new BigDecimal((double)
		 * extRawValue).stripTrailingZeros().doubleValue(); } else if (extRawValue
		 * instanceof JsonObject) { return getStorableExtension(context, (JsonObject)
		 * extRawValue); } else if (extRawValue instanceof JsonArray) { // JsonArray
		 * JsonArray extValueArray = (JsonArray) extRawValue; JsonArray newExtArray =
		 * new JsonArray(); for (Object elem : extValueArray) {
		 * newExtArray.add(getStorableExtension(context, elem)); } return newExtArray; }
		 */
		return null;
	}

	public static Long getStorableDateTime(String string) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		try {
			Date time = sdf.parse(string);
			return time.getTime();
		} catch (ParseException e) {
			return null;
		}
	}

	public static JsonObject getMasterStorableExtension(JsonObject context, JsonObject ext) {
		JsonObject storable = new JsonObject();

		// Retreive @context, id -> id of jsonobject
		// Retreive other keys without namespace
		JsonObject attrWONamespace = new JsonObject();
		String innerKeyWONamespace = null;
		if (ext.containsKey("isA")) {
			String namespace;
			try {
				namespace = ext.getJsonObject("@context").getString("@vocab");
				if (namespace.endsWith("/") || namespace.endsWith(":"))
					namespace = namespace.substring(0, namespace.length() - 1);
				ext.remove("@context");
				innerKeyWONamespace = namespace + "#" + ext.remove("isA");

				for (String extKey : ext.fieldNames()) {
					if (!extKey.contains(":"))
						attrWONamespace.put(extKey, ext.getValue(extKey));
				}
			} catch (Exception ignored) {

			}
		}
		if (innerKeyWONamespace != null) {
			attrWONamespace = getStorableExtension(context, attrWONamespace);
			storable.put(innerKeyWONamespace, attrWONamespace);
		}

		for (String key : ext.fieldNames()) {
			Object value = ext.getValue(key);
			String[] keyArr = key.split(":");
			if (keyArr.length != 2)
				continue;
			if (!context.containsKey(keyArr[0]))
				continue;
			String eNewKey = encodeMongoObjectKey(context.getString(keyArr[0]) + "#" + keyArr[1]);
			Object eNewValue = null;
			if (value instanceof String) {
				Long time = getStorableDateTime((String) value);
				eNewValue = Objects.requireNonNullElseGet(time, () -> (String) value);
			} else if (value instanceof Integer || value instanceof Double || value instanceof Boolean) {
				eNewValue = value;
			} else if (value instanceof JsonObject) {
				eNewValue = getStorableExtension(context, (JsonObject) value);
			}
			storable.put(eNewKey, eNewValue);
		}

		return storable;
	}

	public static String resolveKey(String key, JsonObject context) {
		String[] fieldArr = key.split(":");
		String namespace = context.getString(fieldArr[0]);
		if (namespace == null)
			throw new RuntimeException(fieldArr[0] + " not found in @context");
		if (namespace.endsWith("/") || namespace.endsWith(":"))
			namespace = namespace.substring(0, namespace.length() - 1);
		return namespace + "#" + fieldArr[1];
	}

	public static Stream<BulkOperation> convertVocabulary(JsonObject context, String type,
			JsonArray vocabularyElementList) {
		return vocabularyElementList.stream().parallel().map(v -> {
			JsonObject vocabularyElement = (JsonObject) v;
			JsonObject find = new JsonObject();
			String id = vocabularyElement.getString("id");
			find.put("id", id);
			find.put("type", type);

			JsonArray attributes = vocabularyElement.getJsonArray("attributes");
			JsonObject newAttribute = new JsonObject();

			attributes.stream().parallel().forEach(a -> {
				JsonObject attribute = (JsonObject) a;
				Object value = attribute.getValue("attribute");
				String attrKey = encodeMongoObjectKey(resolveKey(attribute.getString("id"), context));
				Object attrValue;
				if (value instanceof JsonObject) {
					attrValue = getMasterStorableExtension(context, attribute.getJsonObject("attribute"));
				} else {
					attrValue = attribute.getValue("attribute").toString();
				}
				synchronized (newAttribute) {
					if (!newAttribute.containsKey(attrKey)) {
						newAttribute.put(attrKey, attrValue);
					} else if (newAttribute.containsKey(attrKey)
							&& !(newAttribute.getValue(attrKey) instanceof JsonArray)) {
						Object existing = newAttribute.remove(attrKey);
						JsonArray arr = new JsonArray();
						arr.add(existing);
						arr.add(attrValue);
						newAttribute.put(attrKey, arr);
					} else {
						JsonArray arr = (JsonArray) newAttribute.remove(attrKey);
						arr.add(attrValue);
						newAttribute.put(attrKey, arr);
					}
				}
			});
			newAttribute.put("lastUpdate", System.currentTimeMillis());
			JsonObject update = new JsonObject();
			JsonObject updateSet = new JsonObject();
			updateSet.put("attributes", newAttribute);
			if (vocabularyElement.containsKey("children")) {
				updateSet.put("children", vocabularyElement.getJsonArray("children"));
				// update.put("$addToSet", new JsonObject().put("children", new
				// JsonObject().put("$each", vocabularyElement.getJsonArray("children"))));
			}
			update.put("$set", updateSet);

			/*
			 * JsonObject update = new JsonObject();
			 *
			 * JsonObject attributes = retrieveExtension(vocabularyElement); if(attributes
			 * != null && !attributes.isEmpty()){ attributes =
			 * getStorableVocabulary(context, attributes); } attributes.put("lastUpdated",
			 * System.currentTimeMillis()); update.put("$set", new
			 * JsonObject().put("attributes", attributes)); update.put("$addToSet", new
			 * JsonObject().put("children", new JsonObject().put("$each",
			 * vocabularyElement.getJsonArray("children"))));
			 */
			return BulkOperation.createUpdate(find, update, true, false);
		});
	}

	public static String encodeMongoObjectKey(String key) {
		key = key.replace(".", "\uff0e");
		return key;
	}

	@SuppressWarnings("unused")
	public static String getEPC(String uri) {
		if (uri == null)
			return null;
		if (!uri.contains("https://id.gs1.org"))
			return null;
		String _01 = null;
		String _21 = null;
		String _10 = null;
		boolean is01 = false;
		boolean is21 = false;
		boolean is10 = false;
		String[] arr = uri.split("\\/");
		for (String elem : arr) {
			switch (elem) {
			case "01" -> {
				is01 = true;
				is21 = false;
				is10 = false;
				continue;
			}
			case "10" -> {
				is01 = false;
				is21 = false;
				is10 = true;
				continue;
			}
			case "21" -> {
				is01 = false;
				is21 = true;
				is10 = false;
				continue;
			}
			}
			if (is01) {
				is01 = false;
				_01 = elem;
			} else if (is10) {
				is10 = false;
				_10 = elem;
			} else if (is21) {
				is21 = false;
				_21 = elem;
			}
		}

		if (_01 == null)
			return null;

		String _01back = _01.substring(1);
		Integer gcpLength = null;
		while (!_01back.equals("")) {
			if (_01back.length() == 0)
				return null;

			gcpLength = JSONCaptureServer.gcpLength.get(_01back);

			if (gcpLength != null)
				break;

			_01back = _01back.substring(0, _01back.length() - 1);
		}

		if (gcpLength == null)
			return null;

		if (_01 != null && _21 != null) {
			// 01 & 21 formulate SGTIN
			return DLConverter.generateSgtin(_01, _21, gcpLength);
		} else if (_01 != null && _10 != null) {
			// 01 & 10 formulate LGTIN
			return DLConverter.generateLgtin(_01, _10, gcpLength);
		} else if (_01 != null) {
			// 01 formulate GTIN
			return DLConverter.generateGtin(_01, gcpLength);
		}
		return null;
	}
}