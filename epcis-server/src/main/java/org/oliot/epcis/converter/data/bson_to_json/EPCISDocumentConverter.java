package org.oliot.epcis.converter.data.bson_to_json;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.DatatypeConverter;

import org.bson.Document;
import org.oliot.epcis.converter.data.pojo_to_bson.MasterDataConverter;
import org.oliot.epcis.model.ValidationException;
import org.oliot.epcis.model.cbv.BusinessStep;
import org.oliot.epcis.model.cbv.BusinessTransactionType;
import org.oliot.epcis.model.cbv.Disposition;
import org.oliot.epcis.model.cbv.ErrorReason;
import org.oliot.epcis.model.cbv.Measurement;
import org.oliot.epcis.model.cbv.SourceDestinationType;
import org.oliot.epcis.query.converter.tdt.GlobalDocumentTypeIdentifier;
import org.oliot.epcis.query.converter.tdt.TagDataTranslationEngine;
import org.oliot.epcis.server.EPCISServer;
import org.oliot.epcis.util.TimeUtil;
import org.oliot.epcis.validation.IdentifierValidator;

import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

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

	private void putErrorDeclaration(Document original, Document context, Document converted)
			throws ValidationException {
		if (original.containsKey("errorDeclaration")) {
			Document errorDeclaration = original.get("errorDeclaration", Document.class);
			Document newErrorDeclaration = new Document();
			newErrorDeclaration.put("declarationTime", getTime(errorDeclaration.getString("declarationTime")));

			if (errorDeclaration.containsKey("reason")) {
				newErrorDeclaration.put("reason",
						ErrorReason.getFullVocabularyName(errorDeclaration.getString("reason")));
			}

			Document extension = retrieveExtension(errorDeclaration);
			if (!extension.isEmpty()) {
				extension = getExtension(context, extension);
				newErrorDeclaration.put("extension", extension);
				putFlatten(converted, "errf", extension);
			}

			if (!newErrorDeclaration.isEmpty())
				converted.put("errorDeclaration", newErrorDeclaration);
		}
	}

	private void putBaseExtension(Document original, Document context, Document converted) throws ValidationException {
		Document extension = retrieveExtension(original);
		if (!extension.isEmpty()) {
			extension = getExtension(context, extension);
			converted.put("extension", extension);
			putFlatten(converted, "extf", extension);
		}
	}

	private void putParentID(Document original, JsonObject converted) throws ValidationException {
		if (original.containsKey("parentID")) {
			String parentID = original.getString("parentID");
			// TODO: epc to dl
			converted.put("parentID", parentID);
		}
	}

	private void putChildEPCs(Document original, JsonObject converted) throws ValidationException {
		if (original.containsKey("epcList")) {
			List<String> childEPCs = original.getList("epcList", String.class);
			JsonArray childEPCArray = new JsonArray();
			for (String childEPC : childEPCs) {
				// TODO: epc to dl
				childEPCArray.add(childEPC);
			}
			converted.put("childEPCs", childEPCArray);
		}
	}

	private void putEPCList(Document original, JsonObject converted) throws ValidationException {
		if (original.containsKey("epcList")) {
			List<String> childEPCs = original.getList("epcList", String.class);
			JsonArray childEPCArray = new JsonArray();
			for (String childEPC : childEPCs) {
				// TODO: epc to dl
				childEPCArray.add(childEPC);
			}
			converted.put("epcList", childEPCArray);
		}
	}

	private void putInputEPCList(Document original, JsonObject converted) throws ValidationException {
		if (original.containsKey("inputEPCList")) {
			List<String> childEPCs = original.getList("inputEPCList", String.class);
			JsonArray childEPCArray = new JsonArray();
			for (String childEPC : childEPCs) {
				// TODO: epc to dl
				childEPCArray.add(childEPC);
			}
			converted.put("inputEPCList", childEPCArray);
		}
	}

	private void putOutputEPCList(Document original, JsonObject converted) throws ValidationException {
		if (original.containsKey("outputEPCList")) {
			List<String> childEPCs = original.getList("outputEPCList", String.class);
			JsonArray childEPCArray = new JsonArray();
			for (String childEPC : childEPCs) {
				// TODO: epc to dl
				childEPCArray.add(childEPC);
			}
			converted.put("outputEPCList", childEPCArray);
		}
	}

	private void putTransformationID(Document original, JsonObject converted) throws ValidationException {
		// TODO: epc to dl
		if (original.containsKey("transformationID")) {
			converted.put("transformationID", original.getString("transformationID"));
		}
	}

	private void putChildQuantityList(Document original, JsonObject converted) throws ValidationException {
		if (original.containsKey("quantityList")) {
			List<Document> array = original.getList("quantityList", Document.class);
			if (array != null) {
				List<JsonObject> newArray = new ArrayList<JsonObject>();
				for (Document qElem : array) {
					// TODO: epc to dl
					JsonObject newObject = new JsonObject();
					newObject.put("epcClass", qElem.getString("epcClass"));
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
					// TODO: epc to dl
					JsonObject newObject = new JsonObject();
					newObject.put("epcClass", qElem.getString("epcClass"));
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
					// TODO: epc to dl
					JsonObject newObject = new JsonObject();
					newObject.put("epcClass", qElem.getString("epcClass"));
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
					// TODO: epc to dl
					JsonObject newObject = new JsonObject();
					newObject.put("epcClass", qElem.getString("epcClass"));
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

	private void putReadPoint(Document original, JsonObject converted) throws ValidationException {
		// readPoint: "readPoint": {"id": "urn:epc:id:sgln:4012345.00001.0"}, -> string
		// TODO: epc to dl, extension
		if (original.containsKey("readPoint")) {
			JsonObject obj = new JsonObject();
			obj.put("id", original.getString("readPoint"));
			converted.put("readPoint", obj);

			/*
			 * Document extension = retrieveExtension(readPoint); if (!extension.isEmpty())
			 * { extension = getExtension(context, extension); converted.put("readPointExt",
			 * extension); putFlatten(converted, "rpf", extension); }
			 */
		}
	}

	private void putBusinessLocation(Document original, JsonObject converted) throws ValidationException {
		// bizLocation: "bizLocation": {"id": "urn:epc:id:sgln:4012345.00002.0"}, ->
		// string
		// TODO: epc to dl, extension
		if (original.containsKey("bizLocation")) {
			JsonObject obj = new JsonObject();
			obj.put("id", original.getString("bizLocation"));
			converted.put("bizLocation", obj);

			/*
			 * Document extension = retrieveExtension(bizLocation); if
			 * (!extension.isEmpty()) { extension = getExtension(context, extension);
			 * converted.put("bizLocationExt", extension); putFlatten(converted, "blf",
			 * extension); }
			 */
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
				// TODO: epc to dl
				t.put("bizTransaction", elemObj.getString("value"));
				newBizTransactionArr.add(t);
			}
			converted.put("bizTransactionList", newBizTransactionArr);
		}
	}

	private void putSourceList(Document original, JsonObject converted) throws ValidationException {
		// sourceList
		// TODO: epc to dl
		if (original.containsKey("sourceList")) {
			List<Document> arr = original.getList("sourceList", Document.class);
			List<JsonObject> newSourceArr = new ArrayList<JsonObject>();
			for (Document elemObj : arr) {
				JsonObject source = new JsonObject();
				// type is mandatory
				String shortType = SourceDestinationType.getShortVocabularyName(elemObj.getString("type"));
				source.put("type", shortType);
				if (shortType.equals("location")) {
					source.put("source", elemObj.getString("value"));
				} else {
					source.put("source", elemObj.getString("value="));
				}
				newSourceArr.add(source);
			}
			converted.put("sourceList", newSourceArr);
		}
	}

	private void putDestinationList(Document original, JsonObject converted) throws ValidationException {
		// destinationList
		// TODO: epc to dl
		if (original.containsKey("destinationList")) {
			List<Document> arr = original.getList("destinationList", Document.class);
			List<JsonObject> newDestinationArr = new ArrayList<JsonObject>();
			for (Document elemObj : arr) {
				JsonObject destination = new JsonObject();
				// type is mandatory
				String shortType = SourceDestinationType.getShortVocabularyName(elemObj.getString("type"));
				destination.put("type", shortType);
				if (shortType.equals("location")) {
					destination.put("destination", elemObj.getString("value"));
				} else {
					destination.put("destination", elemObj.getString("value"));
				}
				newDestinationArr.add(destination);
			}
			converted.put("destinationList", newDestinationArr);
		}
	}

	private void putSensorElementList(Document original, Document context, Document converted)
			throws ValidationException {
		// sensorElementList
		if (original.containsKey("sensorElementList")) {
			List<Document> newSensorElementList = new ArrayList<Document>();
			List<Document> sensorElementList = original.getList("sensorElementList", Document.class);
			for (Document sensorElement : sensorElementList) {
				Document newSensorElement = new Document();
				if (sensorElement.containsKey("sensorMetadata")) {
					Document newSensorMetadata = new Document();
					Document sensorMetadata = sensorElement.get("sensorMetadata", Document.class);
					if (sensorMetadata.containsKey("time")) {
						newSensorMetadata.put("time", getTime(sensorMetadata.getString("time")));
					}
					if (sensorMetadata.containsKey("startTime")) {
						newSensorMetadata.put("startTime", getTime(sensorMetadata.getString("startTime")));
					}
					if (sensorMetadata.containsKey("endTime")) {
						newSensorMetadata.put("endTime", getTime(sensorMetadata.getString("endTime")));
					}
					if (sensorMetadata.containsKey("deviceID")) {
						newSensorMetadata.put("deviceID",
								TagDataTranslationEngine.toEPC(sensorMetadata.getString("deviceID")));
					}
					if (sensorMetadata.containsKey("deviceMetadata")) {
						newSensorMetadata.put("deviceMetadata",
								GlobalDocumentTypeIdentifier.toEPC(sensorMetadata.getString("deviceMetadata")));
					}
					if (sensorMetadata.containsKey("rawData")) {
						newSensorMetadata.put("rawData",
								GlobalDocumentTypeIdentifier.toEPC(sensorMetadata.getString("rawData")));
					}
					if (sensorMetadata.containsKey("dataProcessingMethod")) {
						newSensorMetadata.put("dataProcessingMethod",
								GlobalDocumentTypeIdentifier.toEPC(sensorMetadata.getString("dataProcessingMethod")));
					}
					if (sensorMetadata.containsKey("bizRules")) {
						newSensorMetadata.put("bizRules",
								GlobalDocumentTypeIdentifier.toEPC(sensorMetadata.getString("bizRules")));
					}
					Document otherAttributes = retrieveExtension(sensorMetadata);
					Document convertedOtherAttributes = getExtension(context, otherAttributes);
					if (!convertedOtherAttributes.isEmpty())
						newSensorMetadata.put("otherAttributes", convertedOtherAttributes);

					if (!newSensorMetadata.isEmpty()) {
						newSensorElement.put("sensorMetadata", newSensorMetadata);
					}
				}

				if (sensorElement.containsKey("sensorReport")) {
					List<Document> newSensorReportList = new ArrayList<Document>();
					List<Document> sensorReport = sensorElement.getList("sensorReport", Document.class);
					for (Document sensorReportElement : sensorReport) {

						Document newSensorReport = new Document();

						String type = sensorReportElement.getString("type");
						String exception = sensorReportElement.getString("exception");

						if ((type == null && exception == null) || (type != null && exception != null)) {
							throw new ValidationException("sensorReport should have one of 'type' or 'exception'");
						}

						if (exception != null) {
							if (!exception.equals("ALARM_CONDITION") && !exception.equals("ERROR_CONDITION"))
								throw new ValidationException(
										"sensorReport - exception should be one of 'ERROR_CONDITION' or 'ALARM_CONDITION'");
							newSensorReport.put("exception", exception);
						}

						String microorganism = sensorReportElement.getString("microorganism");
						String chemicalSubstance = sensorReportElement.getString("chemicalSubstance");

						if (microorganism != null && chemicalSubstance != null) {
							throw new ValidationException(
									"microorganism and chemicalSubstance fields should not coexist in sensorReport field");
						}

						if (sensorReportElement.containsKey("deviceID")) {
							newSensorReport.put("deviceID",
									TagDataTranslationEngine.toEPC(sensorReportElement.getString("deviceID")));
						}

						if (sensorReportElement.containsKey("deviceMetadata")) {
							newSensorReport.put("deviceMetadata", GlobalDocumentTypeIdentifier
									.toEPC(sensorReportElement.getString("deviceMetadata")));
						}

						if (sensorReportElement.containsKey("rawData")) {
							newSensorReport.put("rawData",
									GlobalDocumentTypeIdentifier.toEPC(sensorReportElement.getString("rawData")));
						}

						if (sensorReportElement.containsKey("dataProcessingMethod")) {
							newSensorReport.put("dataProcessingMethod", GlobalDocumentTypeIdentifier
									.toEPC(sensorReportElement.getString("dataProcessingMethod")));
						}

						if (sensorReportElement.containsKey("time")) {
							newSensorReport.put("time", getTime(sensorReportElement.getString("time")));
						}

						if (microorganism != null) {
							IdentifierValidator.checkMicroorganismValue(microorganism);
							newSensorReport.put("microorganism", microorganism);
						}

						if (chemicalSubstance != null) {
							IdentifierValidator
									.checkChemicalSubstance(sensorReportElement.getString("chemicalSubstance"));
							newSensorReport.put("chemicalSubstance", chemicalSubstance);
						}

						String component = sensorReportElement.getString("component");
						if (component != null) {
							IdentifierValidator.checkComponent(component);
							newSensorReport.put("component", component);
						}

						if (sensorReportElement.containsKey("hexBinaryValue")) {
							newSensorReport.put("hexBinaryValue",
									DatatypeConverter.parseHexBinary(sensorReportElement.getString("hexBinaryValue")));
							// sensorReportElement.put("hexBinaryValue",
							// new Document().append("$binary", DatatypeConverter
							// .parseHexBinary(sensorReportElement.getString("hexBinaryValue"))));
						}

						if (sensorReportElement.containsKey("stringValue")) {
							newSensorReport.put("stringValue", sensorReportElement.getString("stringValue"));
						}

						if (sensorReportElement.containsKey("booleanValue")) {
							newSensorReport.put("booleanValue", sensorReportElement.getBoolean("booleanValue"));
						}

						String uriValue = sensorReportElement.getString("uriValue");
						if (uriValue != null) {
							try {
								new URI(uriValue);
								newSensorReport.put("uriValue", uriValue);
							} catch (URISyntaxException e) {
								throw new ValidationException(e.getMessage());
							}
						}

						if (sensorReportElement.containsKey("coordinateReferenceSystem")) {
							newSensorReport.put("coordinateReferenceSystem",
									sensorReportElement.getString("coordinateReferenceSystem"));
						}

						if (sensorReportElement.containsKey("percRank")) {
							newSensorReport.put("percRank",
									Double.valueOf(sensorReportElement.getDouble("percRank").toString()));
						}

						Document otherAttributes = retrieveExtension(sensorReportElement);
						Document convertedOtherAttributes = getExtension(context, otherAttributes);
						if (!convertedOtherAttributes.isEmpty())
							newSensorReport.put("otherAttributes", convertedOtherAttributes);

						if (type != null) {
							newSensorReport.put("type", Measurement.getFullVocabularyName(type));

							String uom = null;
							if (!sensorReportElement.containsKey("uom")) {
								uom = EPCISServer.unitConverter.getRepresentativeUoMFromType(type);
								newSensorReport.put("uom", uom);
							} else {
								uom = sensorReportElement.getString("uom");
								EPCISServer.unitConverter.checkUnitOfMeasure(type, uom);
								newSensorReport.put("uom", uom);
							}

							// value / minValue / maxValue / meanValue
							String rUom = EPCISServer.unitConverter.getRepresentativeUoMFromType(type);
							newSensorReport.put("rUom", rUom);

							// value
							if (sensorReportElement.containsKey("value")) {
								double value = sensorReportElement.getDouble("value");
								newSensorReport.put("value", value);
								newSensorReport.put("rValue",
										EPCISServer.unitConverter.getRepresentativeValue(type, uom, value));
							}

							// minValue
							if (sensorReportElement.containsKey("minValue")) {
								double minValue = sensorReportElement.getDouble("minValue");
								newSensorReport.put("minValue", minValue);
								newSensorReport.put("rMinValue",
										EPCISServer.unitConverter.getRepresentativeValue(type, uom, minValue));
							}

							// maxValue
							if (sensorReportElement.containsKey("maxValue")) {
								double maxValue = sensorReportElement.getDouble("maxValue");
								newSensorReport.put("maxValue", maxValue);
								newSensorReport.put("rMaxValue",
										EPCISServer.unitConverter.getRepresentativeValue(type, uom, maxValue));
							}

							// meanValue
							if (sensorReportElement.containsKey("meanValue")) {
								double meanValue = sensorReportElement.getDouble("meanValue");
								newSensorReport.put("meanValue", meanValue);
								newSensorReport.put("rMeanValue",
										EPCISServer.unitConverter.getRepresentativeValue(type, uom, meanValue));
							}

							// percValue
							if (sensorReportElement.containsKey("percValue")) {
								double percValue = sensorReportElement.getDouble("percValue");
								newSensorReport.put("percValue", percValue);
								newSensorReport.put("rPercValue",
										EPCISServer.unitConverter.getRepresentativeValue(type, uom, percValue));
							}

							// sDev
							if (sensorReportElement.containsKey("sDev")) {
								double sDev = sensorReportElement.getDouble("sDev");
								newSensorReport.put("sDev", sDev);
								newSensorReport.put("rSDev",
										EPCISServer.unitConverter.getRepresentativeValue(type, uom, sDev));
							}

						}

						if (!newSensorReport.isEmpty())
							newSensorReportList.add(newSensorReport);

					}

					if (!newSensorReportList.isEmpty()) {
						newSensorElement.put("sensorReport", newSensorReportList);
					}
				}

				Document extension = retrieveExtension(sensorElement);
				if (!extension.isEmpty()) {
					extension = getExtension(context, extension);
					newSensorElement.put("extension", extension);
					putFlatten(newSensorElement, "sef", extension);
				}
				if (!newSensorElement.isEmpty())
					newSensorElementList.add(newSensorElement);
			}
			if (!newSensorElementList.isEmpty()) {
				converted.put("sensorElementList", newSensorElementList);
			}

		}
	}


	private void putILMD(Document original, Document context, Document converted) throws ValidationException {
		if (original.containsKey("ilmd")) {
			String action = original.getString("action");
			if (action != null && !action.equals("ADD"))
				throw new ValidationException("action should be ADD if ilmd is provided.");

			Document ilmd = original.get("ilmd", Document.class);
			ilmd = getExtension(context, ilmd);
			converted.put("ilmd", ilmd);
			putFlatten(converted, "ilmdf", ilmd);
		}
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

	void convertBase(Document original, JsonObject converted) throws ValidationException {
		putEventTime(original, converted);
		putEventTimeZoneOffset(original, converted);
		putRecordTime(original, converted);
		putCertificationInfo(original, converted);
		putEventID(original, converted);

		// putErrorDeclaration(original, context, converted);
		// putBaseExtension(original, context, converted);
	}

	public JsonObject convertAggregationEvent(Document original) throws ValidationException {
		JsonObject converted = new JsonObject();
		converted.put("type", "AggregationEvent");
		convertBase(original, converted);

		putParentID(original, converted);
		putChildEPCs(original, converted);
		putChildQuantityList(original, converted);
		putAction(original, converted);
		putBizStep(original, converted);
		putDisposition(original, converted);
		putReadPoint(original, converted);
		putBusinessLocation(original, converted);
		putBusinessTransactionList(original, converted);
		putSourceList(original, converted);
		putDestinationList(original, converted);

//		putSensorElementList(original, context, converted);
		return converted;
	}

	public JsonObject convertObjectEvent(Document original) throws ValidationException {
		JsonObject converted = new JsonObject();
		converted.put("type", "ObjectEvent");
		convertBase(original, converted);

		putEPCList(original, converted);
		putQuantityList(original, converted);
		putAction(original, converted);
		putBizStep(original, converted);
		putDisposition(original, converted);
		putPersistentDisposition(original, converted);
		putReadPoint(original, converted);
		putBusinessLocation(original, converted);
		putBusinessTransactionList(original, converted);
		putSourceList(original, converted);
		putDestinationList(original, converted);

//		putILMD(original, context, converted);
//		putSensorElementList(original, context, converted);
		return converted;
	}

	public JsonObject convertTransactionEvent(Document original) throws ValidationException {
		JsonObject converted = new JsonObject();
		converted.put("type", "TransactionEvent");
		convertBase(original, converted);

		putBusinessTransactionList(original, converted);
		putParentID(original, converted);
		putEPCList(original, converted);
		putQuantityList(original, converted);
		putAction(original, converted);
		putBizStep(original, converted);
		putDisposition(original, converted);
		putReadPoint(original, converted);
		putBusinessLocation(original, converted);
		putSourceList(original, converted);
		putDestinationList(original, converted);
//		putSensorElementList(original, context, converted);
		return converted;
	}

	public JsonObject convertTransformationEvent(Document original) throws ValidationException {
		JsonObject converted = new JsonObject();
		converted.put("type", "TransformationEvent");
		convertBase(original, converted);
		putInputEPCList(original, converted);
		putOutputEPCList(original, converted);
		putInputQuantityList(original, converted);
		putOutputQuantityList(original, converted);
		putTransformationID(original, converted);
		putBusinessTransactionList(original, converted);
		putBizStep(original, converted);
		putDisposition(original, converted);
		putPersistentDisposition(original, converted);
		putReadPoint(original, converted);
		putBusinessLocation(original, converted);
		putSourceList(original, converted);
		putDestinationList(original, converted);
//		putILMD(original, context, converted);
//		putSensorElementList(original, context, converted);
		return converted;
	}

	public JsonObject convertAssociationEvent(Document original) throws ValidationException {
		JsonObject converted = new JsonObject();
		converted.put("type", "AssociationEvent");
		convertBase(original, converted);

		putParentID(original, converted);
		putChildEPCs(original, converted);
		putChildQuantityList(original, converted);
		putAction(original, converted);
		putBizStep(original, converted);
		putDisposition(original, converted);
		putReadPoint(original, converted);
		putBusinessLocation(original, converted);
		putBusinessTransactionList(original, converted);
		putSourceList(original, converted);
		putDestinationList(original, converted);
//		putSensorElementList(original, context, converted);
		return converted;
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<WriteModel<Document>> convertVocabulary(JsonObject context, String type,
			JsonArray vocabularyElementList) throws ValidationException {

		List<WriteModel<Document>> list = new ArrayList<WriteModel<Document>>();

		for (int i = 0; i < vocabularyElementList.size(); i++) {
			JsonObject vocabularyElement = vocabularyElementList.getJsonObject(i);
			Document find = new Document();
			String id = vocabularyElement.getString("id");
			try {
				id = TagDataTranslationEngine.toEPC(id);
			} catch (ValidationException e) {
				try {
					TagDataTranslationEngine.checkMicroorganismValue(id);
				} catch (ValidationException e1) {
					TagDataTranslationEngine.checkChemicalSubstance(id);
				}
			}

			// check id is compatible with type
			MasterDataConverter.checkVocabularyTypeID(type, id);
			find.put("id", id);
			find.put("type", type);
			Document update = new Document();
			JsonArray attributes = vocabularyElement.getJsonArray("attributes");
			Document newAttribute = new Document();
			for (int j = 0; j < attributes.size(); j++) {
				JsonObject attribute = attributes.getJsonObject(j);
				String attrKey = encodeMongoObjectKey(resolveKey(attribute.getString("id"), context));
				Object value = attribute.getValue("attribute");
				Object attrValue = null;
				if (value instanceof JsonObject) {
					try {
						attrValue = getMasterStorableExtension(context, attribute.getJsonObject("attribute"));
					} catch (ValidationException e) {
						e.printStackTrace();
					}
				} else {
					attrValue = attribute.getValue("attribute").toString();
				}
				if (!newAttribute.containsKey(attrKey)) {
					newAttribute.put(attrKey, attrValue);
				} else if (newAttribute.containsKey(attrKey) && !(newAttribute.get(attrKey) instanceof JsonArray)) {
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
			newAttribute.put("lastUpdate", System.currentTimeMillis());
			Document updateElement = new Document();
			updateElement.put("attributes", newAttribute);
			try {
				ArrayList childArray = new ArrayList();
				if (vocabularyElement.containsKey("children")) {
					JsonArray children = vocabularyElement.getJsonArray("children");
					for (Object c : children) {
						String cstr = c.toString();
						try {
							childArray.add(TagDataTranslationEngine.toEPC(cstr));
						} catch (ValidationException e) {
							try {
								TagDataTranslationEngine.checkMicroorganismValue(cstr);
								childArray.add(cstr);
							} catch (ValidationException e1) {
								TagDataTranslationEngine.checkChemicalSubstance(cstr);
								childArray.add(cstr);
							}
						}

					}
				}
				updateElement.put("children", childArray);

			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			update.append("$set", updateElement);
			list.add(new UpdateOneModel<Document>(find, update, new UpdateOptions().upsert(true)));
		}
		return list;
	}

	public static String encodeMongoObjectKey(String key) {
		key = key.replace(".", "\uff0e");
		return key;
	}
}
