package org.oliot.epcis.converter.data.json_to_bson;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import javax.xml.bind.DatatypeConverter;

import org.bson.Document;
import org.oliot.epcis.capture.common.Transaction;
import org.oliot.epcis.model.ValidationException;
import org.oliot.epcis.model.cbv.BusinessStep;
import org.oliot.epcis.model.cbv.BusinessTransactionType;
import org.oliot.epcis.model.cbv.Disposition;
import org.oliot.epcis.model.cbv.ErrorReason;
import org.oliot.epcis.model.cbv.Measurement;
import org.oliot.epcis.model.cbv.SensorAlertType;
import org.oliot.epcis.model.cbv.SourceDestinationType;
import org.oliot.epcis.query.converter.tdt.GlobalDocumentTypeIdentifier;
import org.oliot.epcis.query.converter.tdt.GlobalLocationNumber;
import org.oliot.epcis.query.converter.tdt.GlobalLocationNumberOfParty;
import org.oliot.epcis.query.converter.tdt.TagDataTranslationEngine;
import org.oliot.epcis.resource.StaticResource;
import org.oliot.epcis.validation.IdentifierValidator;
import org.oliot.gcp.core.DLConverter;

import com.mongodb.client.model.ReplaceOneModel;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import static org.oliot.epcis.converter.data.pojo_to_bson.POJOtoBSONUtil.*;

public class EPCISDocumentConverter {
	@SuppressWarnings({ "unchecked" })
	public static Document convertEvent(JsonObject jsonContext, JsonObject jsonEvent, Transaction tx)
			throws ValidationException {

		try {
			Document event = Document.parse(jsonEvent.toString());
			Document context = Document.parse(jsonContext.toString());
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
				event.put("parentID", TagDataTranslationEngine.toInstanceLevelEPC(event.getString("parentID")));
			}
			if (event.containsKey("childEPCs")) {
				// child EPCs: using epcList for query efficiency
				List<String> newArray = new ArrayList<String>();
				for (String elem : (List<String>) event.remove("childEPCs")) {
					newArray.add(TagDataTranslationEngine.toInstanceLevelEPC(elem));
				}
				event.put("epcList", newArray);
			} else if (event.containsKey("epcList")) {
				// epcList
				List<String> newArray = new ArrayList<String>();
				for (String elem : event.getList("epcList", String.class)) {
					newArray.add(TagDataTranslationEngine.toInstanceLevelEPC(elem));
				}
				event.put("epcList", newArray);
			}
			// inputEPCList
			if (event.containsKey("inputEPCList")) {
				List<String> newArray = new ArrayList<String>();
				for (String elem : event.getList("inputEPCList", String.class)) {
					newArray.add(TagDataTranslationEngine.toInstanceLevelEPC(elem));
				}
				event.put("inputEPCList", newArray);
			}
			// outputEPCList
			if (event.containsKey("outputEPCList")) {
				List<String> newArray = new ArrayList<String>();
				for (String elem : event.getList("outputEPCList", String.class)) {
					newArray.add(TagDataTranslationEngine.toInstanceLevelEPC(elem));
				}
				event.put("outputEPCList", newArray);
			}
			if (event.containsKey("childQuantityList")) {
				// child quantityList in association event: using QuantityList for query
				// efficiency
				List<Document> newArray = new ArrayList<Document>();
				List<Document> array = event.getList("childQuantityList", Document.class);
				event.remove("childQuantityList");
				for (Document qElem : array) {
					qElem.put("epcClass", TagDataTranslationEngine.toClassLevelEPC(qElem.getString("epcClass")));
					if (qElem.containsKey("quantity")) {
						Object obj = qElem.get("quantity");
						qElem.put("quantity", Double.valueOf(obj.toString()));
					}
					newArray.add(qElem);
				}
				event.put("quantityList", newArray);
			} else if (event.containsKey("quantityList")) {
				// quantityList
				List<Document> newArray = new ArrayList<Document>();
				for (Document qElem : event.getList("quantityList", Document.class)) {
					qElem.put("epcClass", TagDataTranslationEngine.toClassLevelEPC(qElem.getString("epcClass")));
					if (qElem.containsKey("quantity")) {
						qElem.put("quantity", Double.valueOf(qElem.getDouble("quantity").toString()));
					}
					newArray.add(qElem);
				}
				event.put("quantityList", newArray);
			}
			// inputQuantityList
			if (event.containsKey("inputQuantityList")) {
				List<Document> newArray = new ArrayList<Document>();
				for (Document qElem : event.getList("inputQuantityList", Document.class)) {
					qElem.put("epcClass", TagDataTranslationEngine.toClassLevelEPC(qElem.getString("epcClass")));
					if (qElem.containsKey("quantity")) {
						qElem.put("quantity", Double.valueOf(qElem.getDouble("quantity").toString()));
					}
					newArray.add(qElem);
				}
				event.put("inputQuantityList", newArray);
			}
			// outputQuantityList
			if (event.containsKey("outputQuantityList")) {
				List<Document> newArray = new ArrayList<Document>();
				for (Document qElem : event.getList("outputQuantityList", Document.class)) {
					qElem.put("epcClass", TagDataTranslationEngine.toClassLevelEPC(qElem.getString("epcClass")));
					if (qElem.containsKey("quantity")) {
						qElem.put("quantity", Double.valueOf(qElem.getDouble("quantity").toString()));
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
				Document readPoint = event.get("readPoint", Document.class);
				event.put("readPoint", GlobalLocationNumber.toEPC(readPoint.getString("id")));
			}
			// bizLocation: "bizLocation": {"id": "urn:epc:id:sgln:4012345.00002.0"}, ->
			// string
			if (event.containsKey("bizLocation")) {
				Document bizLocation = event.get("bizLocation", Document.class);
				event.put("bizLocation", GlobalLocationNumber.toEPC(bizLocation.getString("id")));
			}
			// bizTransactionList
			if (event.containsKey("bizTransactionList")) {
				List<Document> arr = event.getList("bizTransactionList", Document.class);
				List<Document> newBizTransactionArr = new ArrayList<Document>();
				for (Document elemObj : arr) {
					Document t = new Document();
					// type is optional field
					String type = elemObj.getString("type");
					if (type != null) {
						t.append("type", encodeMongoObjectKey(BusinessTransactionType.getFullVocabularyName(type)));
					}
					t.append("value", GlobalDocumentTypeIdentifier.toEPC(elemObj.getString("bizTransaction")));
					newBizTransactionArr.add(t);
				}
				event.put("bizTransactionList", newBizTransactionArr);
			}
			// sourceList
			if (event.containsKey("sourceList")) {
				List<Document> arr = event.getList("sourceList", Document.class);
				List<Document> newSourceArr = new ArrayList<Document>();
				for (Document elemObj : arr) {
					Document source = new Document();
					// type is mandatory
					String shortType = elemObj.getString("type");
					source.put("type", encodeMongoObjectKey(SourceDestinationType.getFullVocabularyName(shortType)));
					if (shortType.equals("location")) {
						source.put("value", GlobalLocationNumber.toEPC(elemObj.getString("source")));
					} else {
						source.put("value", GlobalLocationNumberOfParty.toEPC(elemObj.getString("source")));
					}
					newSourceArr.add(source);
				}
				event.put("sourceList", newSourceArr);
			}
			// destinationList
			if (event.containsKey("destinationList")) {
				List<Document> arr = event.getList("destinationList", Document.class);
				List<Document> newDestinationArr = new ArrayList<Document>();
				for (Document elemObj : arr) {
					Document destination = new Document();
					// type is mandatory
					String shortType = elemObj.getString("type");
					destination.put("type",
							encodeMongoObjectKey(SourceDestinationType.getFullVocabularyName(shortType)));
					if (shortType.equals("location")) {
						destination.put("value", GlobalLocationNumber.toEPC(elemObj.getString("destination")));
					} else {
						destination.put("value", GlobalLocationNumberOfParty.toEPC(elemObj.getString("destination")));
					}
					newDestinationArr.add(destination);
				}
				event.put("destinationList", newDestinationArr);
			}

			// sensorElementList
			if (event.containsKey("sensorElementList")) {
				List<Document> sensorElementList = event.getList("sensorElementList", Document.class);
				for (Document sensorElement : sensorElementList) {
					sensorElement.remove("isA");
					if (sensorElement.containsKey("sensorMetadata")) {
						Document sensorMetadata = sensorElement.get("sensorMetadata", Document.class);
						if (sensorMetadata.containsKey("time")) {
							sensorMetadata.put("time", getStorableDateTime(sensorMetadata.getString("time")));
						}
						if (sensorMetadata.containsKey("startTime")) {
							sensorMetadata.put("startTime", getStorableDateTime(sensorMetadata.getString("startTime")));
						}
						if (sensorMetadata.containsKey("endTime")) {
							sensorMetadata.put("endTime", getStorableDateTime(sensorMetadata.getString("endTime")));
						}
						if (sensorMetadata.containsKey("deviceID")) {
							sensorMetadata.put("deviceID",
									TagDataTranslationEngine.toEPC(sensorMetadata.getString("deviceID")));
						}
						if (sensorMetadata.containsKey("deviceMetadata")) {
							sensorMetadata.put("deviceMetadata",
									GlobalDocumentTypeIdentifier.toEPC(sensorMetadata.getString("deviceMetadata")));
						}
						if (sensorMetadata.containsKey("rawData")) {
							sensorMetadata.put("rawData",
									GlobalDocumentTypeIdentifier.toEPC(sensorMetadata.getString("rawData")));
						}
						if (sensorMetadata.containsKey("dataProcessingMethod")) {
							sensorMetadata.put("dataProcessingMethod", GlobalDocumentTypeIdentifier
									.toEPC(sensorMetadata.getString("dataProcessingMethod")));
						}
						if (sensorMetadata.containsKey("bizRules")) {
							sensorMetadata.put("bizRules",
									GlobalDocumentTypeIdentifier.toEPC(sensorMetadata.getString("bizRules")));
						}
						Document extension = retrieveExtension(sensorMetadata);
						Document convertedExt = getStorableExtension(context, extension);
						if (!convertedExt.isEmpty())
							sensorMetadata.put("otherAttributes", convertedExt);
					}

					if (sensorElement.containsKey("sensorReport")) {
						List<Document> sensorReport = sensorElement.getList("sensorReport", Document.class);
						for (Document sensorReportElement : sensorReport) {

							/*
							 * 
							 * "coordinateReferenceSystem", "value", "component", "minValue", "maxValue",
							 * "meanValue", "sDev", "percValue", "uom"
							 */

							if ((sensorReportElement.containsKey("type")
									&& sensorReportElement.containsKey("exception"))
									|| (!sensorReportElement.containsKey("type")
											&& !sensorReportElement.containsKey("exception"))) {
								throw new ValidationException(
										"One of type and exception fields should exist in sensorReport field");
							}
							if (sensorReportElement.containsKey("microorganism")
									&& sensorReportElement.containsKey("chemicalSubstance")) {
								throw new ValidationException(
										"microorganism and chemicalSubstance fields should not coexist in sensorReport field");
							}

							if (sensorReportElement.containsKey("type")) {
								sensorReportElement.put("type",
										Measurement.getFullVocabularyName(sensorReportElement.getString("type")));
							} else {
								SensorAlertType.valueOf(sensorReportElement.getString("exception"));
							}

							if (sensorReportElement.containsKey("time")) {
								sensorReportElement.put("time",
										getStorableDateTime(sensorReportElement.getString("time")));
							}

							if (sensorReportElement.containsKey("deviceID")) {
								sensorReportElement.put("deviceID",
										TagDataTranslationEngine.toEPC(sensorReportElement.getString("deviceID")));
							}
							if (sensorReportElement.containsKey("deviceMetadata")) {
								sensorReportElement.put("deviceMetadata", GlobalDocumentTypeIdentifier
										.toEPC(sensorReportElement.getString("deviceMetadata")));
							}
							if (sensorReportElement.containsKey("rawData")) {
								sensorReportElement.put("rawData",
										GlobalDocumentTypeIdentifier.toEPC(sensorReportElement.getString("rawData")));
							}
							if (sensorReportElement.containsKey("dataProcessingMethod")) {
								sensorReportElement.put("dataProcessingMethod", GlobalDocumentTypeIdentifier
										.toEPC(sensorReportElement.getString("dataProcessingMethod")));
							}
							if (sensorReportElement.containsKey("microorganism")) {
								IdentifierValidator
										.checkMicroorganismValue(sensorReportElement.getString("microorganism"));
							}

							if (sensorReportElement.containsKey("chemicalSubstance")) {
								IdentifierValidator
										.checkChemicalSubstance(sensorReportElement.getString("chemicalSubstance"));
							}

							if (sensorReportElement.containsKey("hexBinaryValue")) {
								sensorReportElement.put("hexBinaryValue", DatatypeConverter
										.parseHexBinary(sensorReportElement.getString("hexBinaryValue")));
								// sensorReportElement.put("hexBinaryValue",
								// new Document().append("$binary", DatatypeConverter
								// .parseHexBinary(sensorReportElement.getString("hexBinaryValue"))));
							}

							if (sensorReportElement.containsKey("stringValue")) {
								sensorReportElement.getString("stringValue");
							}

							if (sensorReportElement.containsKey("booleanValue")) {
								sensorReportElement.getBoolean("booleanValue");
							}

							if (sensorReportElement.containsKey("uriValue")) {
								new URI(sensorReportElement.getString("uriValue"));
							}

							if (sensorReportElement.containsKey("percRank")) {
								sensorReportElement.put("percRank",
										Double.valueOf(sensorReportElement.getDouble("percRank").toString()));
							}

							/*
							 * TODO
							 * 
							 * if (sensorReportElement.containsKey("value")) {
							 * sensorReportElement.put("value", sensorReportElement.getDouble("value")); }
							 * if (sensorReportElement.containsKey("minValue")) {
							 * sensorReportElement.put("minValue",
							 * sensorReportElement.getDouble("minValue")); } if
							 * (sensorReportElement.containsKey("maxValue")) {
							 * sensorReportElement.put("maxValue",
							 * sensorReportElement.getDouble("maxValue")); } if
							 * (sensorReportElement.containsKey("sDev")) { sensorReportElement.put("sDev",
							 * sensorReportElement.getDouble("sDev")); } if
							 * (sensorReportElement.containsKey("meanValue")) {
							 * sensorReportElement.put("meanValue",
							 * sensorReportElement.getDouble("meanValue")); } if
							 * (sensorReportElement.containsKey("percRank")) {
							 * sensorReportElement.put("percRank",
							 * sensorReportElement.getDouble("percRank")); } if
							 * (sensorReportElement.containsKey("percValue")) {
							 * sensorReportElement.put("percValue",
							 * sensorReportElement.getDouble("percValue")); } JsonObject extension =
							 * retrieveExtension(sensorReportElement); JsonObject convertedExt =
							 * getStorableExtension(context, extension); if (!convertedExt.isEmpty())
							 * sensorReportElement.put("otherAttributes", convertedExt);
							 * 
							 * String uom = sensorReportElement.getString("uom"); Double value =
							 * sensorReportElement.getDouble("value"); if (uom != null && value != null) {
							 * String rType = EPCISServer.unitConverter.getRepresentativeType(uom); Double
							 * rValue = EPCISServer.unitConverter.getRepresentativeValue(uom, value); if
							 * (rValue != null && rType != null) { sensorReportElement.put("rValue",
							 * rValue); sensorReportElement.put("rType", rType); } }
							 */
						}
					}

					Document extension = retrieveExtension(sensorElement);
					if (!extension.isEmpty()) {
						extension = getStorableExtension(context, extension);
						sensorElement.put("extension", extension);
						putFlatten(sensorElement, "sef", extension);
					}

				}
			}

			// TODO -----------------------------------------------------------------------
			// quantityList: No change
			// ILMD
			if (event.containsKey("ilmd")) {
				Document ilmd = event.get("ilmd", Document.class);
				ilmd = getStorableExtension(context, ilmd);
				event.put("ilmd", ilmd);
				putFlatten(event, "ilmdf", ilmd);
			}

			// persistent disposition
			if (event.containsKey("persistentDisposition")) {
				Document pd = event.get("persistentDisposition", Document.class);
				if (pd.containsKey("set")) {
					List<String> oldSet = pd.getList("set", String.class);
					List<String> newSet = new ArrayList<String>();
					for (int i = 0; i < oldSet.size(); i++) {
						newSet.add(Disposition.getFullVocabularyName(oldSet.get(i)));
					}
					pd.put("set", newSet);
				}
				if (pd.containsKey("unset")) {
					List<String> oldSet = pd.getList("unset", String.class);
					List<String> newSet = new ArrayList<String>();
					for (int i = 0; i < oldSet.size(); i++) {
						newSet.add(Disposition.getFullVocabularyName(oldSet.get(i)));
					}
					pd.put("unset", newSet);
				}
			}

			// Error Declaration
			if (event.containsKey("errorDeclaration")) {
				Document errorDeclaration = event.get("errorDeclaration", Document.class);
				errorDeclaration.put("declarationTime",
						getStorableDateTime(errorDeclaration.getString("declarationTime")));

				if (errorDeclaration.containsKey("reason")) {
					errorDeclaration.put("reason",
							ErrorReason.getFullVocabularyName(errorDeclaration.getString("reason")));
				}

				Document extension = retrieveExtension(errorDeclaration);
				if (!extension.isEmpty()) {
					extension = getStorableExtension(context, extension);
					errorDeclaration.put("extension", getStorableExtension(context, extension));
					putFlatten(event, "errf", extension);
				}
			}

			// vendor extension
			// Collect extension fields
			Document extension = retrieveExtension(event);
			if (!extension.isEmpty()) {
				extension = getStorableExtension(context, extension);
				event.put("extension", extension);
				putFlatten(event, "extf", extension);
			}

			// remove unnecessary fields
			event.remove("@context");
			event.remove("otherAttributes");

			// put event id
			if (!event.containsKey("eventID")) {
				putEventHashID(event);
			}

			if (tx != null)
				event.put("_tx", tx.getTxId());

			return event;

		} catch (Exception e) {
			throw new ValidationException(e.getMessage());
		}
	}

	public static Document retrieveExtension(Document object) {
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

	public static Document getStorableExtension(Document context, Document ext) {
		Document storable = new Document();
		for (String key : ext.keySet()) {
			String[] fieldArr = key.split(":");
			String extKey;
			if (fieldArr.length == 1) {
				extKey = "#" + key;
			} else {
				extKey = context.getString(fieldArr[0]) + "#" + fieldArr[1];
			}
			Object extRawValue = ext.get(key);

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

	@SuppressWarnings("unchecked")
	public static List<Object> getStorableExtension(Document context, List<Object> extRawValue) {
		List<Object> newExtArray = new ArrayList<Object>();
		for (Object elem : extRawValue) {
			if (elem instanceof Document) {
				newExtArray.add(getStorableExtension(context, (Document) elem));
			} else if (elem instanceof List) {
				newExtArray.add(getStorableExtension(context, (List<Object>) elem));
			} else {
				newExtArray.add(elem.toString());
			}
		}
		return newExtArray;
	}

	@SuppressWarnings("unchecked")
	public static Object getStorableExtension(Document context, String key, Object extRawValue) {
		if (extRawValue instanceof String) {
			if (context.containsKey(key) && context.get(key, Document.class).containsKey("@type")) {
				String type = context.get(key, Document.class).getString("@type");
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
		} else if (extRawValue instanceof Document) {
			return getStorableExtension(context, (Document) extRawValue);
		} else if (extRawValue instanceof JsonArray) {
			return getStorableExtension(context, (List<Object>) extRawValue);
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

	public static Document getMasterStorableExtension(JsonObject jsonContext, JsonObject jsonExt) {

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
			attrWONamespace = getStorableExtension(context, attrWONamespace);
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
				Long time = getStorableDateTime((String) value);
				eNewValue = Objects.requireNonNullElseGet(time, () -> (String) value);
			} else if (value instanceof Integer || value instanceof Double || value instanceof Boolean) {
				eNewValue = value;
			} else if (value instanceof Document) {
				eNewValue = getStorableExtension(context, (Document) value);
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

	public static Stream<ReplaceOneModel<Document>> convertVocabulary(JsonObject context, String type,
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

			// return new ReplaceOneModel<Document>(find, update);
			return new ReplaceOneModel<Document>(null, null);
		});
	}

	public static String encodeMongoObjectKey(String key) {
		key = key.replace(".", "\uff0e");
		return key;
	}

	public static String getEPC2(String uri) throws ValidationException {
		if (uri == null)
			return null;
		if (!uri.contains("https://id.gs1.org"))
			return null;
		String _01 = null;
		String _21 = null;
		String _10 = null;
		String _00 = null;
		boolean is01 = false;
		boolean is21 = false;
		boolean is10 = false;
		boolean is00 = false;
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
			case "00" -> {
				is01 = false;
				is21 = false;
				is10 = false;
				is00 = true;
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
			} else if (is00) {
				_00 = elem;
			}
		}

		if (_01 != null) {
//			String _01back = _01.substring(1);
//			Integer gcpLength = null;
//			while (!_01back.equals("")) {
//				if (_01back.length() == 0)
//					return null;
//
//				gcpLength = StaticResource.gcpLength.get(_01back);
//
//				if (gcpLength != null)
//					break;
//
//				_01back = _01back.substring(0, _01back.length() - 1);
//			}
//
//			if (gcpLength == null)
//				return null;
			Integer gcpLength = DLConverter.getGCPLength(StaticResource.gcpLength, _01.substring(1));

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
		} else if (_00 != null) {
			Integer gcpLength = DLConverter.getGCPLength(StaticResource.gcpLength, _00.substring(1));
			return DLConverter.generateSscc(_00, gcpLength);
		}

		return null;
	}
}
