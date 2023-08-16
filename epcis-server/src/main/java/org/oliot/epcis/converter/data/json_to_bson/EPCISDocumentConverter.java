package org.oliot.epcis.converter.data.json_to_bson;

import java.net.URI;
import java.net.URISyntaxException;
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
import org.oliot.epcis.converter.data.pojo_to_bson.POJOtoBSONUtil;
import org.oliot.epcis.model.ActionType;
import org.oliot.epcis.model.ValidationException;
import org.oliot.epcis.model.cbv.BusinessStep;
import org.oliot.epcis.model.cbv.BusinessTransactionType;
import org.oliot.epcis.model.cbv.Disposition;
import org.oliot.epcis.model.cbv.EPCISEventType;
import org.oliot.epcis.model.cbv.ErrorReason;
import org.oliot.epcis.model.cbv.Measurement;
import org.oliot.epcis.model.cbv.SourceDestinationType;
import org.oliot.epcis.query.converter.tdt.GlobalDocumentTypeIdentifier;
import org.oliot.epcis.query.converter.tdt.GlobalLocationNumber;
import org.oliot.epcis.query.converter.tdt.GlobalLocationNumberOfParty;
import org.oliot.epcis.query.converter.tdt.TagDataTranslationEngine;
import org.oliot.epcis.server.EPCISServer;
import org.oliot.epcis.validation.IdentifierValidator;

import com.mongodb.client.model.ReplaceOneModel;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import static org.oliot.epcis.converter.data.pojo_to_bson.POJOtoBSONUtil.*;

@SuppressWarnings("unused")
public class EPCISDocumentConverter {

	// instance EPC: parentID, childEPCs, epcList, inputEPCList, outputEPCList
	// SGTIN, SSCC, SGLN, GRAI, GAIAI, GSRN, GSRNP, GDTI, CPI, SGCN, GINC, GSIN,
	// ITIP, UPUI, PGLN

	// class EPC: childQuantityList, quantityList, inputQuantityList,
	// outputQuantityList
	// LGTIN, GTIN, CPI, ITIP

	// readPoint, businessLocation
	// SGLN

	// sourceDestID
	// owning_party, possessing_party: pgln
	// location: sgln

	// businessTransactionID
	// GDTI, GSRN, CBV 8.5

	// transformationID
	// GDTI, CBV 8.8

	// resourceID
	// GDTI

	private void putType(Document original, Document converted) throws ValidationException {
		String type = original.getString("type");
		if (type == null) {
			throw new ValidationException("type should exist");
		}
		try {
			EPCISEventType.valueOf(type);
		} catch (IllegalArgumentException e) {
			throw new ValidationException(e.getMessage());
		}
		converted.put("type", type);
	}

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

	private void putEventTime(Document original, Document converted) throws ValidationException {
		String eventTime = original.getString("eventTime");
		if (eventTime == null)
			throw new ValidationException("eventTime should exist");
		converted.put("eventTime", getTime(eventTime));
	}

	private void putEventTimeZoneOffset(Document original, Document converted) throws ValidationException {
		String eventTimeZoneOffset = original.getString("eventTimeZoneOffset");
		if (eventTimeZoneOffset == null) {
			throw new ValidationException("eventTimeZoneOffset should exist");
		}

		SimpleDateFormat sdf = new SimpleDateFormat("XXX");
		try {
			sdf.parse(eventTimeZoneOffset);
			converted.put("eventTimeZoneOffset", eventTimeZoneOffset);
		} catch (ParseException e) {
			ValidationException e1 = new ValidationException(e.getMessage());
			throw e1;
		}
	}

	private void putParentID(Document original, Document converted) throws ValidationException {
		String parentID = original.getString("parentID");
		if (parentID != null) {
			converted.put("parentID", TagDataTranslationEngine.toInstanceLevelEPC(parentID));
		}
	}

	private void putChildEPCs(Document original, Document converted) throws ValidationException {
		List<String> childEPCs = original.getList("childEPCs", String.class);
		if (childEPCs != null) {
			List<String> newArray = new ArrayList<String>();
			for (String elem : childEPCs) {
				newArray.add(TagDataTranslationEngine.toInstanceLevelEPC(elem));
			}
			converted.put("epcList", newArray);
		}
	}

	private void putEPCList(Document original, Document converted) throws ValidationException {
		List<String> newArray = new ArrayList<String>();
		for (String elem : original.getList("epcList", String.class)) {
			newArray.add(TagDataTranslationEngine.toInstanceLevelEPC(elem));
		}
		converted.put("epcList", newArray);
	}

	private void putInputEPCList(Document original, Document converted) throws ValidationException {
		if (original.containsKey("inputEPCList")) {
			List<String> newArray = new ArrayList<String>();
			for (String elem : original.getList("inputEPCList", String.class)) {
				newArray.add(TagDataTranslationEngine.toInstanceLevelEPC(elem));
			}
			original.put("inputEPCList", newArray);
		}
	}

	private void putOutputEPCList(Document original, Document converted) throws ValidationException {
		if (original.containsKey("outputEPCList")) {
			List<String> newArray = new ArrayList<String>();
			for (String elem : original.getList("outputEPCList", String.class)) {
				newArray.add(TagDataTranslationEngine.toInstanceLevelEPC(elem));
			}
			original.put("outputEPCList", newArray);
		}
	}

	private void putChildQuantityList(Document original, Document converted) throws ValidationException {
		List<Document> array = original.getList("childQuantityList", Document.class);
		if (array != null) {
			List<Document> newArray = new ArrayList<Document>();
			for (Document qElem : array) {
				qElem.put("epcClass", TagDataTranslationEngine.toClassLevelEPC(qElem.getString("epcClass")));
				if (qElem.containsKey("quantity")) {
					Object obj = qElem.get("quantity");
					qElem.put("quantity", Double.valueOf(obj.toString()));
				}
				newArray.add(qElem);
			}
			converted.put("quantityList", newArray);
		}
	}

	private void putQuantityList(Document original, Document converted) throws ValidationException {
		List<Document> newArray = new ArrayList<Document>();
		for (Document qElem : original.getList("quantityList", Document.class)) {
			qElem.put("epcClass", TagDataTranslationEngine.toClassLevelEPC(qElem.getString("epcClass")));
			if (qElem.containsKey("quantity")) {
				qElem.put("quantity", Double.valueOf(qElem.getDouble("quantity").toString()));
			}
			newArray.add(qElem);
		}
		converted.put("quantityList", newArray);
	}

	private void putInputQuantityList(Document original, Document converted) throws ValidationException {
		List<Document> newArray = new ArrayList<Document>();
		for (Document qElem : original.getList("inputQuantityList", Document.class)) {
			qElem.put("epcClass", TagDataTranslationEngine.toClassLevelEPC(qElem.getString("epcClass")));
			if (qElem.containsKey("quantity")) {
				qElem.put("quantity", Double.valueOf(qElem.getDouble("quantity").toString()));
			}
			newArray.add(qElem);
		}
		converted.put("inputQuantityList", newArray);
	}

	private void putOutputQuantityList(Document original, Document converted) throws ValidationException {
		List<Document> newArray = new ArrayList<Document>();
		for (Document qElem : original.getList("outputQuantityList", Document.class)) {
			qElem.put("epcClass", TagDataTranslationEngine.toClassLevelEPC(qElem.getString("epcClass")));
			if (qElem.containsKey("quantity")) {
				qElem.put("quantity", Double.valueOf(qElem.getDouble("quantity").toString()));
			}
			newArray.add(qElem);
		}
		converted.put("outputQuantityList", newArray);
	}

	private void putAction(Document original, Document converted) throws ValidationException {
		String action = original.getString("action");
		if (action == null)
			throw new ValidationException("action should exist");
		try {
			ActionType.valueOf(action);
		} catch (IllegalArgumentException e) {
			throw e;
		}
		converted.put("action", original.getString("action"));
	}

	private void putBizStep(Document original, Document converted) {
		if (original.containsKey("bizStep")) {
			converted.put("bizStep", BusinessStep.getFullVocabularyName(original.getString("bizStep")));
		}
	}

	private void putDisposition(Document original, Document converted) {
		if (original.containsKey("disposition")) {
			// standard vocabulary in brief form should change to its full name
			// (compatibility with XML)
			converted.put("disposition", Disposition.getFullVocabularyName(original.getString("disposition")));
		}
	}

	private void putReadPoint(Document original, Document converted) throws ValidationException {
		// readPoint: "readPoint": {"id": "urn:epc:id:sgln:4012345.00001.0"}, -> string
		if (original.containsKey("readPoint")) {
			Document readPoint = original.get("readPoint", Document.class);
			converted.put("readPoint", GlobalLocationNumber.toEPC(readPoint.getString("id")));
		}
	}

	private void putBusinessLocation(Document original, Document converted) throws ValidationException {
		// bizLocation: "bizLocation": {"id": "urn:epc:id:sgln:4012345.00002.0"}, ->
		// string
		if (original.containsKey("bizLocation")) {
			Document bizLocation = original.get("bizLocation", Document.class);
			converted.put("bizLocation", GlobalLocationNumber.toEPC(bizLocation.getString("id")));
		}
	}

	private void putBusinessTransactionList(Document original, Document converted) throws ValidationException {
		// bizTransactionList
		if (original.containsKey("bizTransactionList")) {
			List<Document> arr = original.getList("bizTransactionList", Document.class);
			List<Document> newBizTransactionArr = new ArrayList<Document>();
			for (Document elemObj : arr) {
				Document t = new Document();
				// type is optional field
				String ttype = elemObj.getString("type");
				if (ttype != null) {
					t.append("type", encodeMongoObjectKey(BusinessTransactionType.getFullVocabularyName(ttype)));
				}
				t.append("value",
						TagDataTranslationEngine.toBusinessTransactionEPC(elemObj.getString("bizTransaction")));
				newBizTransactionArr.add(t);
			}
			converted.put("bizTransactionList", newBizTransactionArr);
		}
	}

	private void putSourceList(Document original, Document converted) throws ValidationException {
		// sourceList
		if (original.containsKey("sourceList")) {
			List<Document> arr = original.getList("sourceList", Document.class);
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
			converted.put("sourceList", newSourceArr);
		}

	}

	private void putDestinationList(Document original, Document converted) throws ValidationException {
		// destinationList
		if (original.containsKey("destinationList")) {
			List<Document> arr = original.getList("destinationList", Document.class);
			List<Document> newDestinationArr = new ArrayList<Document>();
			for (Document elemObj : arr) {
				Document destination = new Document();
				// type is mandatory
				String shortType = elemObj.getString("type");
				destination.put("type", encodeMongoObjectKey(SourceDestinationType.getFullVocabularyName(shortType)));
				if (shortType.equals("location")) {
					destination.put("value", GlobalLocationNumber.toEPC(elemObj.getString("destination")));
				} else {
					destination.put("value", GlobalLocationNumberOfParty.toEPC(elemObj.getString("destination")));
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
					Document extension = retrieveExtension(sensorMetadata);
					Document convertedExt = getExtension(context, extension);
					if (!convertedExt.isEmpty())
						newSensorMetadata.put("otherAttributes", convertedExt);

					if (!newSensorMetadata.isEmpty()) {
						newSensorElement.put("sensorMetadata", newSensorMetadata);
					}
				}

				if (sensorElement.containsKey("sensorReport")) {
					List<Document> newSensorReportList = new ArrayList<Document>();
					List<Document> sensorReport = sensorElement.getList("sensorReport", Document.class);
					for (Document sensorReportElement : sensorReport) {

						/*
						 * 
						 * "value", "minValue", "maxValue", "meanValue", "sDev", "percValue", "uom"
						 */

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

						// TODO: otherAttributes

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
		// ILMD
		if (original.containsKey("ilmd")) {
			Document ilmd = original.get("ilmd", Document.class);
			ilmd = getExtension(context, ilmd);
			converted.put("ilmd", ilmd);
			putFlatten(converted, "ilmdf", ilmd);
		}
	}

	private void putPersistentDisposition(Document original, Document converted) {
		// persistent disposition
		if (original.containsKey("persistentDisposition")) {
			Document pd = original.get("persistentDisposition", Document.class);
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
	}

	private void putErrorDeclaration(Document original, Document context, Document converted)
			throws ValidationException {
		// Error Declaration
		if (original.containsKey("errorDeclaration")) {
			Document errorDeclaration = original.get("errorDeclaration", Document.class);
			errorDeclaration.put("declarationTime", getTime(errorDeclaration.getString("declarationTime")));

			if (errorDeclaration.containsKey("reason")) {
				errorDeclaration.put("reason", ErrorReason.getFullVocabularyName(errorDeclaration.getString("reason")));
			}

			Document extension = retrieveExtension(errorDeclaration);
			if (!extension.isEmpty()) {
				extension = getExtension(context, extension);
				errorDeclaration.put("extension", getExtension(context, extension));
				putFlatten(converted, "errf", extension);
			}
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

	private void putEventHashID(Document converted) {
		// put event id
		if (!converted.containsKey("eventID")) {
			POJOtoBSONUtil.putEventHashID(converted);
		}
	}

	private void putTransactionID(Document converted, Transaction tx) {
		if (tx != null)
			converted.put("_tx", tx.getTxId());
	}

	private void putRecordTime(Document converted) {
		converted.put("recordTime", System.currentTimeMillis());
	}

	private void putCertificationInfo(Document original, Document converted) throws ValidationException {
		String certificationInfo = original.getString("certificationInfo");
		if (certificationInfo != null) {
			try {
				new URI(certificationInfo);
				converted.put("certificationInfo", certificationInfo);
			} catch (URISyntaxException e) {
				throw new ValidationException(e.getMessage());
			}
		}
	}

	private void putEventID(Document original, Document converted) {
		String eventID = original.getString("eventID");
		if (eventID != null) {
			converted.put("eventID", eventID);
		}
	}

	private Document getBaseEPCISEvent(Document original, Document context) throws ValidationException {

		Document converted = new Document();

		putType(original, converted);
		putEventTime(original, converted);
		putEventTimeZoneOffset(original, converted);
		putRecordTime(converted);
		putCertificationInfo(original, converted);
		putEventID(original, converted);
		putErrorDeclaration(original, original, converted);
		putBaseExtension(original, context, converted);

		return converted;
	}

	void putAggregationEventFields(Document original, Document context, Document converted) throws ValidationException {
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
		putSensorElementList(original, context, converted);
		putEventHashID(converted);
	}

	public Document convertEvent(JsonObject jsonContext, JsonObject jsonEvent, Transaction tx)
			throws ValidationException {

		try {
			Document original = Document.parse(jsonEvent.toString());
			Document context = Document.parse(jsonContext.toString());

			// type: use as itself
			String type = original.getString("type");
			Document converted = getBaseEPCISEvent(original, context);
			if (type.equals("AggregationEvent")) {
				putAggregationEventFields(original, context, converted);
			} else if (type.equals("ObjectEvent")) {

			} else if (type.equals("TransactionEvent")) {

			} else if (type.equals("TransformationEvent")) {

			} else if (type.equals("AssociationEvent")) {

			} else {
				throw new ValidationException("invalid event type: " + type);
			}

			putTransactionID(converted, tx);
			return converted;
		} catch (Exception e) {
			throw new ValidationException(e.getMessage());
		}
	}

	public Document retrieveExtension(Document object) {
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

	public Document getExtension(Document context, Document ext) throws ValidationException {
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
	public List<Object> getExtension(Document context, List<Object> extRawValue) throws ValidationException {
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

	@SuppressWarnings("unchecked")
	public Object getExtension(Document context, String key, Object extRawValue) throws ValidationException {
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

	public static String resolveKey(String key, JsonObject context) {
		String[] fieldArr = key.split(":");
		String namespace = context.getString(fieldArr[0]);
		if (namespace == null)
			throw new RuntimeException(fieldArr[0] + " not found in @context");
		if (namespace.endsWith("/") || namespace.endsWith(":"))
			namespace = namespace.substring(0, namespace.length() - 1);
		return namespace + "#" + fieldArr[1];
	}

	public Stream<ReplaceOneModel<Document>> convertVocabulary(JsonObject context, String type,
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
				Object attrValue = null;
				if (value instanceof JsonObject) {
					try {
						attrValue = getMasterStorableExtension(context, attribute.getJsonObject("attribute"));
					} catch (ValidationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
}
