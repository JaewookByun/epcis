package org.oliot.epcis.converter.data.bson_to_json;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

import org.bson.Document;
import org.bson.types.Binary;
import org.oliot.epcis.converter.data.pojo_to_bson.MasterDataConverter;
import org.oliot.epcis.model.ValidationException;
import org.oliot.epcis.model.cbv.BusinessStep;
import org.oliot.epcis.model.cbv.BusinessTransactionType;
import org.oliot.epcis.model.cbv.Disposition;
import org.oliot.epcis.model.cbv.ErrorReason;
import org.oliot.epcis.model.cbv.Measurement;
import org.oliot.epcis.model.cbv.SourceDestinationType;
import org.oliot.epcis.server.EPCISServer;
import org.oliot.epcis.tdt.GlobalDocumentTypeIdentifier;
import org.oliot.epcis.tdt.GlobalLocationNumber;
import org.oliot.epcis.tdt.GlobalLocationNumberOfParty;
import org.oliot.epcis.tdt.TagDataTranslationEngine;
import org.oliot.epcis.util.TimeUtil;

import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import jakarta.xml.bind.DatatypeConverter;

import static org.oliot.epcis.converter.data.pojo_to_bson.POJOtoBSONUtil.*;

@SuppressWarnings("unused")
public class EPCISDocumentConverter {

	private Long getTime(String time) throws ValidationException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		String timeString = (String) time;
		try {
			Date t = sdf.parse(timeString);
			return t.getTime();
		} catch (ParseException e) {
			throw new ValidationException(e.getMessage());
		}
	}

	private String getTime(Long time) {
		return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(new Date(time));
	}

	private Document retrieveExtension(Document object) {
		Iterator<String> extFieldIter = object.keySet().iterator();
		Document extension = new Document();
		while (extFieldIter.hasNext()) {
			String extField = extFieldIter.next();
			if (extField.contains(":")) {
				extension.put(extField, object.get(extField));
			}
		}
		for (String removalField : extension.keySet()) {
			object.remove(removalField);
		}
		return extension;
	}

	private Document getExtension(Document context, Document ext) throws ValidationException {
		Document extension = new Document();
		for (String key : ext.keySet()) {
			String[] fieldArr = key.split(":");
			String extKey;
			if (fieldArr.length == 1) {
				extKey = "#" + key;
			} else {
				String namespace = context.getString(fieldArr[0]);
				if (namespace == null)
					throw new ValidationException("Cannot find a namespace " + namespace + " in the context.");
				extKey = context.getString(fieldArr[0]) + "#" + fieldArr[1];
			}
			Object extRawValue = ext.get(key);
			extension.put(encodeMongoObjectKey(extKey), getExtension(context, key, extRawValue));
		}
		return extension;
	}

	@SuppressWarnings("unchecked")
	private Object getExtension(Document context, String key, Object extRawValue) throws ValidationException {
		if (extRawValue instanceof String) {
			if (context.containsKey(key) && context.get(key, Document.class).containsKey("@type")) {
				String type = context.get(key, Document.class).getString("@type");
				try {
					if (type.equals("xsd:int")) {
						return Integer.parseInt((String) extRawValue);
					} else if (type.equals("xsd:double")) {
						return Double.parseDouble((String) extRawValue);
					} else if (type.equals("xsd:dateTimeStamp")) {
						return getTime((String) extRawValue);
					} else {
						return extRawValue;
					}
				} catch (Exception e) {
					return extRawValue;
				}
			}
			return extRawValue;
		} else if (extRawValue instanceof Integer) {
			return (Integer) extRawValue;
		} else if (extRawValue instanceof Double) {
			return (Double) extRawValue;
		} else if (extRawValue instanceof Document) {
			return getExtension(context, (Document) extRawValue);
		} else if (extRawValue instanceof List<?>) {
			return getExtension(context, (List<Object>) extRawValue);
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private List<Object> getExtension(Document context, List<Object> extRawValue) throws ValidationException {
		List<Object> newExtArray = new ArrayList<Object>();
		for (Object elem : extRawValue) {
			if (elem instanceof Document) {
				newExtArray.add(getExtension(context, (Document) elem));
			} else if (elem instanceof List) {
				newExtArray.add(getExtension(context, (List<Object>) elem));
			} else if (elem instanceof Integer) {
				newExtArray.add((Integer) elem);
			} else if (elem instanceof Double) {
				newExtArray.add((Double) elem);
			} else {
				String inner = elem.toString();
				try {
					Long t = getTime(inner);
					newExtArray.add(t);
				} catch (ValidationException e) {
					newExtArray.add(inner);
				}

			}
		}
		return newExtArray;
	}

	private void putEventTime(Document original, JsonObject converted) {
		long eventTime = original.getLong("eventTime");
		converted.put("eventTime", TimeUtil.getDateTimeStamp(eventTime));
	}

	private void putEventTimeZoneOffset(Document original, JsonObject converted) {
		converted.put("eventTimeZoneOffset", original.getString("eventTimeZoneOffset"));
	}

	private void putRecordTime(Document original, JsonObject converted) {
		long recordTime = original.getLong("recordTime");
		converted.put("recordTime", TimeUtil.getDateTimeStamp(recordTime));
	}

	private void putCertificationInfo(Document original, JsonObject converted) throws ValidationException {
		if (original.containsKey("certificationInfo"))
			converted.put("certificationInfo", original.getString("certificationInfo"));
	}

	private void putEventID(Document original, JsonObject converted) {
		converted.put("eventID", original.getString("eventID"));
	}

	private void putErrorDeclaration(Document original, JsonObject converted, ArrayList<String> namespaces,
			JsonObject extType) throws ValidationException {
		if (original.containsKey("errorDeclaration")) {
			Document errorDeclaration = original.get("errorDeclaration", Document.class);
			JsonObject newErrorDeclaration = new JsonObject();

			newErrorDeclaration.put("declarationTime",
					TimeUtil.getDateTimeStamp(errorDeclaration.getLong("declarationTime")));

			if (errorDeclaration.containsKey("reason")) {
				newErrorDeclaration.put("reason",
						ErrorReason.getShortVocabularyName(errorDeclaration.getString("reason")));
			}

			if (errorDeclaration.containsKey("correctiveEventIDs")) {
				JsonArray newCorrectiveEventIDs = new JsonArray();
				for (String cid : errorDeclaration.getList("correctiveEventIDs", String.class)) {
					newCorrectiveEventIDs.add(cid);
				}
				newErrorDeclaration.put("correctiveEventIDs", newCorrectiveEventIDs);
			}

			if (errorDeclaration.containsKey("extension")) {
				JsonObject ext = getExtension(errorDeclaration.get("extension", org.bson.Document.class), namespaces,
						extType);
				ext.forEach(entry -> newErrorDeclaration.put(entry.getKey(), entry.getValue()));
			}

			converted.put("errorDeclaration", newErrorDeclaration);
		}
	}

	private void putBaseExtension(Document original, JsonObject converted, ArrayList<String> namespaces,
			JsonObject extType) throws ValidationException {
		if (!original.containsKey("extension"))
			return;
		JsonObject ext = getExtension(original.get("extension", org.bson.Document.class), namespaces, extType);
		ext.forEach(entry -> converted.put(entry.getKey(), entry.getValue()));
	}

	private void putParentID(Document original, JsonObject converted) throws ValidationException {
		if (original.containsKey("parentID")) {
			String parentID = original.getString("parentID");
			converted.put("parentID", TagDataTranslationEngine.toInstanceLevelDL(parentID));
		}
	}

	private void putChildEPCs(Document original, JsonObject converted) throws ValidationException {
		if (original.containsKey("epcList")) {
			List<String> childEPCs = original.getList("epcList", String.class);
			JsonArray childEPCArray = new JsonArray();
			for (String childEPC : childEPCs) {
				childEPCArray.add(TagDataTranslationEngine.toInstanceLevelDL(childEPC));
			}
			converted.put("childEPCs", childEPCArray);
		}
	}

	private void putEPCList(Document original, JsonObject converted) throws ValidationException {
		if (original.containsKey("epcList")) {
			List<String> epcList = original.getList("epcList", String.class);
			JsonArray epcListArray = new JsonArray();
			for (String epc : epcList) {
				epcListArray.add(TagDataTranslationEngine.toInstanceLevelDL(epc));
			}
			converted.put("epcList", epcListArray);
		}
	}

	private void putInputEPCList(Document original, JsonObject converted) throws ValidationException {
		if (original.containsKey("inputEPCList")) {
			List<String> epcList = original.getList("inputEPCList", String.class);
			JsonArray epcListArray = new JsonArray();
			for (String epc : epcList) {
				epcListArray.add(TagDataTranslationEngine.toInstanceLevelDL(epc));
			}
			converted.put("inputEPCList", epcListArray);
		}
	}

	private void putOutputEPCList(Document original, JsonObject converted) throws ValidationException {
		if (original.containsKey("outputEPCList")) {
			List<String> epcList = original.getList("outputEPCList", String.class);
			JsonArray epcListArray = new JsonArray();
			for (String epc : epcList) {
				epcListArray.add(TagDataTranslationEngine.toInstanceLevelDL(epc));
			}
			converted.put("outputEPCList", epcListArray);
		}
	}

	private void putTransformationID(Document original, JsonObject converted) throws ValidationException {
		if (original.containsKey("transformationID")) {
			converted.put("transformationID",
					GlobalDocumentTypeIdentifier.toDL(original.getString("transformationID")));
		}
	}

	private void putChildQuantityList(Document original, JsonObject converted) throws ValidationException {
		if (original.containsKey("quantityList")) {
			List<Document> array = original.getList("quantityList", Document.class);
			if (array != null) {
				List<JsonObject> newArray = new ArrayList<JsonObject>();
				for (Document qElem : array) {
					JsonObject newObject = new JsonObject();
					newObject.put("epcClass", TagDataTranslationEngine.toClassLevelDL(qElem.getString("epcClass")));
					if (qElem.containsKey("quantity")) {
						newObject.put("quantity", qElem.getDouble("quantity"));
					}
					if (qElem.containsKey("uom")) {
						newObject.put("uom", qElem.getString("uom"));
					}
					newArray.add(newObject);
				}
				converted.put("childQuantityList", newArray);
			}
		}
	}

	private void putQuantityList(Document original, JsonObject converted) throws ValidationException {
		if (original.containsKey("quantityList")) {
			List<Document> array = original.getList("quantityList", Document.class);
			if (array != null) {
				List<JsonObject> newArray = new ArrayList<JsonObject>();
				for (Document qElem : array) {
					JsonObject newObject = new JsonObject();
					newObject.put("epcClass", TagDataTranslationEngine.toClassLevelDL(qElem.getString("epcClass")));
					if (qElem.containsKey("quantity")) {
						newObject.put("quantity", qElem.getDouble("quantity"));
					}
					if (qElem.containsKey("uom")) {
						newObject.put("uom", qElem.getString("uom"));
					}
					newArray.add(newObject);
				}
				converted.put("quantityList", newArray);
			}
		}
	}

	private void putInputQuantityList(Document original, JsonObject converted) throws ValidationException {
		if (original.containsKey("inputQuantityList")) {
			List<Document> array = original.getList("inputQuantityList", Document.class);
			if (array != null) {
				List<JsonObject> newArray = new ArrayList<JsonObject>();
				for (Document qElem : array) {
					JsonObject newObject = new JsonObject();
					newObject.put("epcClass", TagDataTranslationEngine.toClassLevelDL(qElem.getString("epcClass")));
					if (qElem.containsKey("quantity")) {
						newObject.put("quantity", qElem.getDouble("quantity"));
					}
					if (qElem.containsKey("uom")) {
						newObject.put("uom", qElem.getString("uom"));
					}
					newArray.add(newObject);
				}
				converted.put("inputQuantityList", newArray);
			}
		}
	}

	private void putOutputQuantityList(Document original, JsonObject converted) throws ValidationException {
		if (original.containsKey("outputQuantityList")) {
			List<Document> array = original.getList("outputQuantityList", Document.class);
			if (array != null) {
				List<JsonObject> newArray = new ArrayList<JsonObject>();
				for (Document qElem : array) {
					JsonObject newObject = new JsonObject();
					newObject.put("epcClass", TagDataTranslationEngine.toClassLevelDL(qElem.getString("epcClass")));
					if (qElem.containsKey("quantity")) {
						newObject.put("quantity", qElem.getDouble("quantity"));
					}
					if (qElem.containsKey("uom")) {
						newObject.put("uom", qElem.getString("uom"));
					}
					newArray.add(newObject);
				}
				converted.put("outputQuantityList", newArray);
			}
		}
	}

	private void putAction(Document original, JsonObject converted) throws ValidationException {
		converted.put("action", original.getString("action"));
	}

	private void putBizStep(Document original, JsonObject converted) throws IllegalArgumentException {
		if (original.containsKey("bizStep")) {
			converted.put("bizStep", BusinessStep.getShortVocabularyName(original.getString("bizStep")));
		}
	}

	private void putDisposition(Document original, JsonObject converted) throws IllegalArgumentException {
		if (original.containsKey("disposition")) {
			converted.put("disposition", Disposition.getShortVocabularyName(original.getString("disposition")));
		}
	}

	private void putReadPoint(Document original, JsonObject converted, ArrayList<String> namespaces, JsonObject extType)
			throws ValidationException {
		// readPoint: "readPoint": {"id": "urn:epc:id:sgln:4012345.00001.0"}, -> string
		if (original.containsKey("readPoint")) {
			JsonObject obj = new JsonObject();
			obj.put("id", GlobalLocationNumber.toDL(original.getString("readPoint")));
			if (!original.containsKey("readPointExt"))
				return;
			JsonObject ext = getExtension(original.get("readPointExt", org.bson.Document.class), namespaces, extType);
			ext.forEach(entry -> obj.put(entry.getKey(), entry.getValue()));
			converted.put("readPoint", obj);
		}
	}

	private void putBusinessLocation(Document original, JsonObject converted, ArrayList<String> namespaces,
			JsonObject extType) throws ValidationException {
		// bizLocation: "bizLocation": {"id": "urn:epc:id:sgln:4012345.00002.0"}, ->
		// string
		if (original.containsKey("bizLocation")) {
			JsonObject obj = new JsonObject();
			obj.put("id", GlobalLocationNumber.toDL(original.getString("bizLocation")));
			if (!original.containsKey("bizLocationExt"))
				return;
			JsonObject ext = getExtension(original.get("bizLocationExt", org.bson.Document.class), namespaces, extType);
			ext.forEach(entry -> obj.put(entry.getKey(), entry.getValue()));
			converted.put("bizLocation", obj);
		}

	}

	private void putBusinessTransactionList(Document original, JsonObject converted) throws ValidationException {
		// bizTransactionList
		if (original.containsKey("bizTransactionList")) {
			List<Document> arr = original.getList("bizTransactionList", Document.class);
			List<JsonObject> newBizTransactionArr = new ArrayList<JsonObject>();
			for (Document elemObj : arr) {
				JsonObject t = new JsonObject();
				// type is optional field
				String ttype = elemObj.getString("type");
				if (ttype != null) {
					t.put("type", BusinessTransactionType.getShortVocabularyName(ttype));
				}
				t.put("bizTransaction", TagDataTranslationEngine.toBusinessTransactionDL(elemObj.getString("value")));
				newBizTransactionArr.add(t);
			}
			converted.put("bizTransactionList", newBizTransactionArr);
		}
	}

	private void putSourceList(Document original, JsonObject converted) throws ValidationException {
		// sourceList
		if (original.containsKey("sourceList")) {
			List<Document> arr = original.getList("sourceList", Document.class);
			List<JsonObject> newSourceArr = new ArrayList<JsonObject>();
			for (Document elemObj : arr) {
				JsonObject source = new JsonObject();
				// type is mandatory
				String shortType = SourceDestinationType.getShortVocabularyName(elemObj.getString("type"));
				source.put("type", shortType);
				if (shortType.equals("location")) {
					source.put("source", GlobalLocationNumber.toDL(elemObj.getString("value")));
				} else {
					source.put("source", GlobalLocationNumberOfParty.toDL(elemObj.getString("value")));
				}
				newSourceArr.add(source);
			}
			converted.put("sourceList", newSourceArr);
		}
	}

	private void putDestinationList(Document original, JsonObject converted) throws ValidationException {
		// destinationList
		if (original.containsKey("destinationList")) {
			List<Document> arr = original.getList("destinationList", Document.class);
			List<JsonObject> newDestinationArr = new ArrayList<JsonObject>();
			for (Document elemObj : arr) {
				JsonObject destination = new JsonObject();
				// type is mandatory
				String shortType = SourceDestinationType.getShortVocabularyName(elemObj.getString("type"));
				destination.put("type", shortType);
				if (shortType.equals("location")) {
					destination.put("destination", GlobalLocationNumber.toDL(elemObj.getString("value")));
				} else {
					destination.put("destination", GlobalLocationNumberOfParty.toDL(elemObj.getString("value")));
				}
				newDestinationArr.add(destination);
			}
			converted.put("destinationList", newDestinationArr);
		}
	}

	private void putSensorElementList(Document original, JsonObject converted, ArrayList<String> namespaces,
			JsonObject extType) throws ValidationException {
		// sensorElementList
		if (original.containsKey("sensorElementList")) {
			JsonArray newSensorElementList = new JsonArray();
			List<Document> sensorElementList = original.getList("sensorElementList", Document.class);
			for (Document sensorElement : sensorElementList) {
				JsonObject newSensorElement = new JsonObject();
				if (sensorElement.containsKey("sensorMetadata")) {
					JsonObject newSensorMetadata = new JsonObject();
					Document sensorMetadata = sensorElement.get("sensorMetadata", Document.class);
					if (sensorMetadata.containsKey("time")) {
						newSensorMetadata.put("time", getTime(sensorMetadata.getLong("time")));
					}
					if (sensorMetadata.containsKey("startTime")) {
						newSensorMetadata.put("startTime", getTime(sensorMetadata.getLong("startTime")));
					}
					if (sensorMetadata.containsKey("endTime")) {
						newSensorMetadata.put("endTime", getTime(sensorMetadata.getLong("endTime")));
					}
					if (sensorMetadata.containsKey("deviceID")) {
						newSensorMetadata.put("deviceID",
								TagDataTranslationEngine.toDL(sensorMetadata.getString("deviceID")));
					}
					if (sensorMetadata.containsKey("deviceMetadata")) {
						newSensorMetadata.put("deviceMetadata",
								GlobalDocumentTypeIdentifier.toDL(sensorMetadata.getString("deviceMetadata")));
					}
					if (sensorMetadata.containsKey("rawData")) {
						newSensorMetadata.put("rawData",
								GlobalDocumentTypeIdentifier.toDL(sensorMetadata.getString("rawData")));
					}
					if (sensorMetadata.containsKey("dataProcessingMethod")) {
						newSensorMetadata.put("dataProcessingMethod",
								GlobalDocumentTypeIdentifier.toDL(sensorMetadata.getString("dataProcessingMethod")));
					}
					if (sensorMetadata.containsKey("bizRules")) {
						newSensorMetadata.put("bizRules",
								GlobalDocumentTypeIdentifier.toDL(sensorMetadata.getString("bizRules")));
					}

					if (sensorMetadata.containsKey("otherAttributes")) {
						JsonObject otherAttributes = getExtension(
								sensorMetadata.get("otherAttributes", org.bson.Document.class), namespaces, extType);
						otherAttributes.forEach(entry -> newSensorMetadata.put(entry.getKey(), entry.getValue()));
					}

					if (!newSensorMetadata.isEmpty()) {
						newSensorElement.put("sensorMetadata", newSensorMetadata);
					}
				}

				if (sensorElement.containsKey("sensorReport")) {
					JsonArray newSensorReportList = new JsonArray();
					List<Document> sensorReport = sensorElement.getList("sensorReport", Document.class);
					for (Document sensorReportElement : sensorReport) {
						JsonObject newSensorReport = new JsonObject();

						if (sensorReportElement.containsKey("exception"))
							newSensorReport.put("exception", sensorReportElement.getString("exception"));

						if (sensorReportElement.containsKey("microorganism"))
							newSensorReport.put("microorganism", sensorReportElement.getString("microorganism"));

						if (sensorReportElement.containsKey("chemicalSubstance"))
							newSensorReport.put("chemicalSubstance",
									sensorReportElement.getString("chemicalSubstance"));

						if (sensorReportElement.containsKey("deviceID")) {
							newSensorReport.put("deviceID",
									TagDataTranslationEngine.toDL(sensorReportElement.getString("deviceID")));
						}

						if (sensorReportElement.containsKey("deviceMetadata")) {
							newSensorReport.put("deviceMetadata",
									GlobalDocumentTypeIdentifier.toDL(sensorReportElement.getString("deviceMetadata")));
						}

						if (sensorReportElement.containsKey("rawData")) {
							newSensorReport.put("rawData",
									GlobalDocumentTypeIdentifier.toDL(sensorReportElement.getString("rawData")));
						}

						if (sensorReportElement.containsKey("dataProcessingMethod")) {
							newSensorReport.put("dataProcessingMethod", GlobalDocumentTypeIdentifier
									.toDL(sensorReportElement.getString("dataProcessingMethod")));
						}

						if (sensorReportElement.containsKey("time")) {
							newSensorReport.put("time", getTime(sensorReportElement.getLong("time")));
						}

						if (sensorReportElement.containsKey("component")) {
							newSensorReport.put("component", sensorReportElement.getString("component"));
						}

						if (sensorReportElement.containsKey("hexBinaryValue")) {
							newSensorReport.put("hexBinaryValue", DatatypeConverter
									.printHexBinary(sensorReportElement.get("hexBinaryValue", Binary.class).getData()));

						}

						if (sensorReportElement.containsKey("stringValue")) {
							newSensorReport.put("stringValue", sensorReportElement.getString("stringValue"));
						}

						if (sensorReportElement.containsKey("booleanValue")) {
							newSensorReport.put("booleanValue", sensorReportElement.getBoolean("booleanValue"));
						}

						if (sensorReportElement.containsKey("uriValue")) {
							newSensorReport.put("uriValue", sensorReportElement.getString("uriValue"));
						}

						if (sensorReportElement.containsKey("coordinateReferenceSystem")) {
							newSensorReport.put("coordinateReferenceSystem",
									sensorReportElement.getString("coordinateReferenceSystem"));
						}

						if (sensorReportElement.containsKey("percRank")) {
							newSensorReport.put("percRank", sensorReportElement.getDouble("percRank"));
						}

						if (sensorReportElement.containsKey("otherAttributes")) {
							JsonObject otherAttributes = getExtension(
									sensorReportElement.get("otherAttributes", org.bson.Document.class), namespaces,
									extType);
							otherAttributes.forEach(entry -> newSensorReport.put(entry.getKey(), entry.getValue()));
						}

						if (sensorReportElement.containsKey("type")) {
							newSensorReport.put("type",
									Measurement.getShortVocabularyName(sensorReportElement.getString("type")));
						}

						if (sensorReportElement.containsKey("uom")) {
							newSensorReport.put("uom", sensorReportElement.getString("uom"));
						}

						if (sensorReportElement.containsKey("value")) {
							newSensorReport.put("value", sensorReportElement.getDouble("value"));
						}

						if (sensorReportElement.containsKey("minValue")) {
							newSensorReport.put("minValue", sensorReportElement.getDouble("minValue"));
						}

						if (sensorReportElement.containsKey("maxValue")) {
							newSensorReport.put("maxValue", sensorReportElement.getDouble("maxValue"));
						}

						if (sensorReportElement.containsKey("meanValue")) {
							newSensorReport.put("meanValue", sensorReportElement.getDouble("meanValue"));
						}

						if (sensorReportElement.containsKey("percValue")) {
							newSensorReport.put("percValue", sensorReportElement.getDouble("percValue"));
						}

						if (sensorReportElement.containsKey("sDev")) {
							newSensorReport.put("sDev", sensorReportElement.getDouble("sDev"));
						}

						if (!newSensorReport.isEmpty())
							newSensorReportList.add(newSensorReport);

					}

					if (!newSensorReportList.isEmpty()) {
						newSensorElement.put("sensorReport", newSensorReportList);
					}
				}

				if (sensorElement.containsKey("extension")) {
					JsonObject extension = getExtension(sensorElement.get("extension", org.bson.Document.class),
							namespaces, extType);
					extension.forEach(entry -> newSensorElement.put(entry.getKey(), entry.getValue()));
				}

				if (!newSensorElement.isEmpty())
					newSensorElementList.add(newSensorElement);
			}
			if (!newSensorElementList.isEmpty()) {
				converted.put("sensorElementList", newSensorElementList);
			}

		}
	}

	private void putILMD(Document original, JsonObject converted, ArrayList<String> namespaces, JsonObject extType)
			throws ValidationException {
		if (!original.containsKey("ilmd"))
			return;
		JsonObject ext = getExtension(original.get("ilmd", org.bson.Document.class), namespaces, extType);
		JsonObject newIlmd = new JsonObject();
		ext.forEach(entry -> newIlmd.put(entry.getKey(), entry.getValue()));
		converted.put("ilmd", newIlmd);
	}

	private void putPersistentDisposition(Document original, JsonObject converted) {
		if (original.containsKey("persistentDisposition")) {
			Document pd = original.get("persistentDisposition", Document.class);
			JsonObject newPd = new JsonObject();
			if (pd.containsKey("set")) {
				List<String> oldSet = pd.getList("set", String.class);
				JsonArray newSet = new JsonArray();
				for (int i = 0; i < oldSet.size(); i++) {
					newSet.add(Disposition.getShortVocabularyName(oldSet.get(i)));
				}
				newPd.put("set", newSet);
			}
			if (pd.containsKey("unset")) {
				List<String> oldSet = pd.getList("unset", String.class);
				JsonArray newSet = new JsonArray();
				for (int i = 0; i < oldSet.size(); i++) {
					newSet.add(Disposition.getShortVocabularyName(oldSet.get(i)));
				}
				newPd.put("unset", newSet);
			}
			converted.put("persistentDisposition", newPd);
		}
	}

	void convertEventBase(Document original, JsonObject converted, ArrayList<String> namespaces, JsonObject extType)
			throws ValidationException {
		putEventTime(original, converted);
		putEventTimeZoneOffset(original, converted);
		putRecordTime(original, converted);
		putCertificationInfo(original, converted);
		putEventID(original, converted);

		putErrorDeclaration(original, converted, namespaces, extType);
		putBaseExtension(original, converted, namespaces, extType);
	}

	public JsonObject convertAggregationEvent(Document original, ArrayList<String> namespaces, JsonObject extType)
			throws ValidationException {
		JsonObject converted = new JsonObject();
		converted.put("type", "AggregationEvent");
		convertEventBase(original, converted, namespaces, extType);

		putParentID(original, converted);
		putChildEPCs(original, converted);
		putChildQuantityList(original, converted);
		putAction(original, converted);
		putBizStep(original, converted);
		putDisposition(original, converted);
		putReadPoint(original, converted, namespaces, extType);
		putBusinessLocation(original, converted, namespaces, extType);
		putBusinessTransactionList(original, converted);
		putSourceList(original, converted);
		putDestinationList(original, converted);

		putSensorElementList(original, converted, namespaces, extType);
		return converted;
	}

	public JsonObject convertObjectEvent(Document original, ArrayList<String> namespaces, JsonObject extType)
			throws ValidationException {
		JsonObject converted = new JsonObject();
		converted.put("type", "ObjectEvent");
		convertEventBase(original, converted, namespaces, extType);

		putEPCList(original, converted);
		putQuantityList(original, converted);
		putAction(original, converted);
		putBizStep(original, converted);
		putDisposition(original, converted);
		putPersistentDisposition(original, converted);
		putReadPoint(original, converted, namespaces, extType);
		putBusinessLocation(original, converted, namespaces, extType);
		putBusinessTransactionList(original, converted);
		putSourceList(original, converted);
		putDestinationList(original, converted);

		putILMD(original, converted, namespaces, extType);
		putSensorElementList(original, converted, namespaces, extType);
		return converted;
	}

	public JsonObject convertTransactionEvent(Document original, ArrayList<String> namespaces, JsonObject extType)
			throws ValidationException {
		JsonObject converted = new JsonObject();
		converted.put("type", "TransactionEvent");
		convertEventBase(original, converted, namespaces, extType);

		putBusinessTransactionList(original, converted);
		putParentID(original, converted);
		putEPCList(original, converted);
		putQuantityList(original, converted);
		putAction(original, converted);
		putBizStep(original, converted);
		putDisposition(original, converted);
		putReadPoint(original, converted, namespaces, extType);
		putBusinessLocation(original, converted, namespaces, extType);
		putSourceList(original, converted);
		putDestinationList(original, converted);
		putSensorElementList(original, converted, namespaces, extType);
		return converted;
	}

	public JsonObject convertTransformationEvent(Document original, ArrayList<String> namespaces, JsonObject extType)
			throws ValidationException {
		JsonObject converted = new JsonObject();
		converted.put("type", "TransformationEvent");
		convertEventBase(original, converted, namespaces, extType);
		putInputEPCList(original, converted);
		putOutputEPCList(original, converted);
		putInputQuantityList(original, converted);
		putOutputQuantityList(original, converted);
		putTransformationID(original, converted);
		putBusinessTransactionList(original, converted);
		putBizStep(original, converted);
		putDisposition(original, converted);
		putPersistentDisposition(original, converted);
		putReadPoint(original, converted, namespaces, extType);
		putBusinessLocation(original, converted, namespaces, extType);
		putSourceList(original, converted);
		putDestinationList(original, converted);
		putILMD(original, converted, namespaces, extType);
		putSensorElementList(original, converted, namespaces, extType);
		return converted;
	}

	public JsonObject convertAssociationEvent(Document original, ArrayList<String> namespaces, JsonObject extType)
			throws ValidationException {
		JsonObject converted = new JsonObject();
		converted.put("type", "AssociationEvent");
		convertEventBase(original, converted, namespaces, extType);

		putParentID(original, converted);
		putChildEPCs(original, converted);
		putChildQuantityList(original, converted);
		putAction(original, converted);
		putBizStep(original, converted);
		putDisposition(original, converted);
		putReadPoint(original, converted, namespaces, extType);
		putBusinessLocation(original, converted, namespaces, extType);
		putBusinessTransactionList(original, converted);
		putSourceList(original, converted);
		putDestinationList(original, converted);
		putSensorElementList(original, converted, namespaces, extType);
		return converted;
	}

	public JsonObject convertVocabulary(Document original, ArrayList<String> namespaces, JsonObject extType)
			throws ValidationException {

		JsonObject converted = new JsonObject();
		try {
			converted.put("type", original.getString("type"));
			JsonArray convertedVocabularyElementList = new JsonArray();
			JsonObject vocabulary = new JsonObject();
			vocabulary.put("id", TagDataTranslationEngine.toDL(original.getString("id")));
			Document attributes = original.get("attributes", Document.class);
			JsonArray convertedAttributes = new JsonArray();
			for (String key : attributes.keySet()) {
				Object value = attributes.get(key);
				JsonObject convertedAttribute = new JsonObject();
				String[] arr = key.split("#");
				if (arr.length > 1) {
					int idx = namespaces.indexOf(arr[0]);
					if (idx != -1) {
						key = "ext" + idx + ":" + decodeMongoObjectKey(arr[1]);
					}
				} else if (key.contains("urn:epcglobal:cbv:mda")) {
					key = key.replace("urn:epcglobal:cbv:mda", "ext" + namespaces.indexOf("urn:epcglobal:cbv:mda")); 
				} else {
					key = decodeMongoObjectKey(key);
				}
				convertedAttribute.put("id", key);

				if (value instanceof org.bson.Document) {
					convertedAttribute.put(key, getExtension((Document) value, namespaces, extType));
				} else if (value instanceof List) {
					convertedAttribute.put(key, getExtension((List<?>) value, namespaces, extType));
				} else {
					if (value instanceof Double) {
						extType.put(key, new JsonObject().put("@type", "xsd:double"));
						convertedAttribute.put("attribute", value.toString());
					} else if (value instanceof Integer) {
						extType.put(key, new JsonObject().put("@type", "xsd:int"));
						convertedAttribute.put("attribute", value.toString());
					} else if (value instanceof Long) {
						extType.put(key, new JsonObject().put("@type", "xsd:dateTime"));
						convertedAttribute.put("attribute", TimeUtil.getDateTimeStamp((Long) value));
					} else if (value instanceof Boolean) {
						extType.put(key, new JsonObject().put("@type", "xsd:boolean"));
						convertedAttribute.put("attribute", value.toString());
					} else {
						extType.put(key, new JsonObject().put("@type", "xsd:string"));
						convertedAttribute.put("attribute", value.toString());
					}
				}

				convertedAttributes.add(convertedAttribute);
			}
			vocabulary.put("attributes", convertedAttributes);

			List<String> children = original.getList("children", String.class);
			JsonArray convertedChildren = new JsonArray();
			for (String child : children) {
				convertedChildren.add(TagDataTranslationEngine.toDL(child));
			}
			vocabulary.put("children", convertedChildren);
			convertedVocabularyElementList.add(vocabulary);

			converted.put("vocabularyElementList", convertedVocabularyElementList);
			return converted;
		} catch (NullPointerException e) {
			throw new ValidationException(e.getMessage());
		}
	}

	public Document getMasterStorableExtension(JsonObject jsonContext, JsonObject jsonExt) throws ValidationException {

		Document ext = Document.parse(jsonExt.toString());
		Document context = Document.parse(jsonContext.toString());

		Document storable = new Document();

		// Retreive @context, id -> id of jsonobject
		// Retreive other keys without namespace
		Document attrWONamespace = new Document();
		String innerKeyWONamespace = null;
		if (ext.containsKey("isA")) {
			String namespace;
			try {
				namespace = ext.get("@context", Document.class).getString("@vocab");
				if (namespace.endsWith("/") || namespace.endsWith(":"))
					namespace = namespace.substring(0, namespace.length() - 1);
				ext.remove("@context");
				innerKeyWONamespace = namespace + "#" + ext.remove("isA");

				for (String extKey : ext.keySet()) {
					if (!extKey.contains(":"))
						attrWONamespace.put(extKey, ext.get(extKey));
				}
			} catch (Exception ignored) {

			}
		}
		if (innerKeyWONamespace != null) {
			attrWONamespace = getExtension(context, attrWONamespace);
			storable.put(innerKeyWONamespace, attrWONamespace);
		}

		for (String key : ext.keySet()) {
			Object value = ext.get(key);
			String[] keyArr = key.split(":");
			if (keyArr.length != 2)
				continue;
			if (!context.containsKey(keyArr[0]))
				continue;
			String eNewKey = encodeMongoObjectKey(context.getString(keyArr[0]) + "#" + keyArr[1]);
			Object eNewValue = null;
			if (value instanceof String) {
				Long time = getTime((String) value);
				eNewValue = Objects.requireNonNullElseGet(time, () -> (String) value);
			} else if (value instanceof Integer || value instanceof Double || value instanceof Boolean) {
				eNewValue = value;
			} else if (value instanceof Document) {
				eNewValue = getExtension(context, (Document) value);
			}
			storable.put(eNewKey, eNewValue);
		}

		return storable;
	}

	public static String resolveKey(String key, JsonObject context) throws ValidationException {
		String[] fieldArr = key.split(":");
		String namespace = context.getString(fieldArr[0]);
		if (namespace == null)
			throw new ValidationException(fieldArr[0] + " not found in @context");
		if (namespace.endsWith("/") || namespace.endsWith(":"))
			namespace = namespace.substring(0, namespace.length() - 1);
		if (namespace.contains("urn:epcglobal:cbv:mda")) {
			return namespace + ":" + fieldArr[1];
		} else {
			return namespace + "#" + fieldArr[1];
		}

	}

	public static String encodeMongoObjectKey(String key) {
		key = key.replace(".", "\uff0e");
		return key;
	}

	public static String decodeMongoObjectKey(String key) {
		key = key.replace("\uff0e", ".");
		return key;
	}

	public static JsonArray getExtension(List<?> innerList, ArrayList<String> namespaces, JsonObject extType) {
		JsonArray convertedInnerArray = new JsonArray();
		for (Object inner : innerList) {
			if (inner instanceof org.bson.Document) {
				convertedInnerArray.add(getExtension((Document) inner, namespaces, extType));
			} else {
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
				if (value instanceof Double) {
					extType.put(key, new JsonObject().put("@type", "xsd:double"));
					convertedExt.put(key, value.toString());
				} else if (value instanceof Integer) {
					extType.put(key, new JsonObject().put("@type", "xsd:int"));
					convertedExt.put(key, value.toString());
				} else if (value instanceof Long) {
					extType.put(key, new JsonObject().put("@type", "xsd:dateTime"));
					convertedExt.put(key, TimeUtil.getDateTimeStamp((Long) value));
				} else if (value instanceof Boolean) {
					extType.put(key, new JsonObject().put("@type", "xsd:boolean"));
					convertedExt.put(key, value.toString());
				} else {
					extType.put(key, new JsonObject().put("@type", "xsd:string"));
					convertedExt.put(key, value.toString());
				}
			}
		}
		return convertedExt;
	}

	public static JsonArray getExtension(JsonArray innerArray, ArrayList<String> namespaces, JsonObject extType) {
		JsonArray convertedInnerArray = new JsonArray();
		for (Object inner : innerArray) {
			if (inner instanceof JsonObject) {
				convertedInnerArray.add(getExtension((JsonObject) inner, namespaces, extType));
			} else if (inner instanceof JsonArray) {
				convertedInnerArray.add(getExtension((JsonArray) inner, namespaces, extType));
			} else {
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
			} else if (value instanceof JsonArray) {
				convertedExt.put(key, getExtension((JsonArray) value, namespaces, extType));
			} else {
				if (value instanceof Double) {
					extType.put(key, new JsonObject().put("@type", "xsd:double"));
					convertedExt.put(key, value.toString());
				} else if (value instanceof Integer) {
					extType.put(key, new JsonObject().put("@type", "xsd:int"));
					convertedExt.put(key, value.toString());
				} else if (value instanceof Long) {
					extType.put(key, new JsonObject().put("@type", "xsd:dateTime"));
					convertedExt.put(key, TimeUtil.getDateTimeStamp((Long) value));
				} else if (value instanceof Boolean) {
					extType.put(key, new JsonObject().put("@type", "xsd:boolean"));
					convertedExt.put(key, value.toString());
				} else {
					extType.put(key, new JsonObject().put("@type", "xsd:string"));
					convertedExt.put(key, value.toString());
				}
			}
		}
		return convertedExt;
	}
}
