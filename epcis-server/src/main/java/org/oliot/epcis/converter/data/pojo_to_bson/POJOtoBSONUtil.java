package org.oliot.epcis.converter.data.pojo_to_bson;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.bson.Document;
import org.oliot.epcis.model.ActionType;
import org.oliot.epcis.model.BusinessLocationType;
import org.oliot.epcis.model.BusinessTransactionListType;
import org.oliot.epcis.model.BusinessTransactionType;
import org.oliot.epcis.model.CorrectiveEventIDsType;
import org.oliot.epcis.model.DestinationListType;
import org.oliot.epcis.model.EPC;
import org.oliot.epcis.model.EPCListType;
import org.oliot.epcis.model.ErrorDeclarationType;
import org.oliot.epcis.model.PersistentDispositionType;
import org.oliot.epcis.model.QuantityElementType;
import org.oliot.epcis.model.QuantityListType;
import org.oliot.epcis.model.ReadPointType;
import org.oliot.epcis.model.SensorElementListType;
import org.oliot.epcis.model.SensorElementType;
import org.oliot.epcis.model.SensorMetadataType;
import org.oliot.epcis.model.SensorReportType;
import org.oliot.epcis.model.SourceDestType;
import org.oliot.epcis.model.SourceListType;
import org.oliot.epcis.model.ValidationException;
import org.oliot.epcis.resource.StaticResource;
import org.oliot.epcis.server.EPCISServer;
import org.oliot.epcis.util.CBVAttributeUtil;
import org.oliot.epcis.util.TimeUtil;
import org.oliot.epcis.validation.IdentifierValidator;
import org.w3c.dom.*;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import static org.oliot.epcis.resource.StaticResource.*;

/**
 * Copyright (C) 2020-2023. (Jaewook Byun) all rights reserved.
 * <p>
 * This project is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * A set of methods related to the conversion from a storage unit to POJO.
 * <p>
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr, Associate Director, Auto-ID Labs, Korea,
 *         bjw0829@gmail.com
 */
public class POJOtoBSONUtil {

	// Time
	public static void putEventTime(Document o, XMLGregorianCalendar v) {
		o.put("eventTime", v.toGregorianCalendar().getTimeInMillis());
	}

	public static void putEventTimeZoneOffset(Document o, String v) throws ValidationException {
		SimpleDateFormat sdf = new SimpleDateFormat("XXX");
		try {
			sdf.parse(v);
		} catch (ParseException e) {
			ValidationException e1 = new ValidationException(e.getMessage());
			throw e1;
		}
		o.put("eventTimeZoneOffset", v);
	}

	public static void putRecordTime(Document o) {
		o.put("recordTime", System.currentTimeMillis());
	}

	public static void putCertificationInfo(Document o, String v) {
		if (v != null)
			o.put("certificationInfo", v);
	}

	// Object
	public static void putEPC(Document o, String fieldName, String v) throws ValidationException {
		if (v == null)
			return;
		IdentifierValidator.checkEPCPureIdentity(gcpLength, v);
		o.put(fieldName, v);
	}

	public static void putEPCList(Document o, String fieldName, EPCListType v) throws ValidationException {
		if (v == null)
			return;
		List<EPC> epcList = v.getEpc();
		if (epcList.isEmpty())
			return;
		ArrayList<String> epcDBList = new ArrayList<String>();
		for (EPC epc : epcList) {
			String epcStr = epc.getValue();
			IdentifierValidator.checkEPCPureIdentity(gcpLength, epcStr);
			epcDBList.add(epcStr);
		}
		o.put(fieldName, epcDBList);
	}

	public static void putQuantityList(Document o, String fieldName, QuantityListType v) throws ValidationException {
		if (v == null || v.getQuantityElement().isEmpty())
			return;
		ArrayList<Document> quantityList = new ArrayList<Document>();

		for (QuantityElementType qet : v.getQuantityElement()) {
			Document quantity = new Document();
			String epcClass = qet.getEpcClass();
			String uom = qet.getUom();
			IdentifierValidator.checkEPCClassPureIdentity(gcpLength, epcClass);
			quantity.put("epcClass", epcClass);

			if (qet.getQuantity() != null) {
				quantity.put("quantity", Double.valueOf(qet.getQuantity().getValue().toString()));
			}
			if (uom != null) {
				if (unitOfMeasure.contains(uom))
					quantity.put("uom", qet.getUom().toString());
				else {
					ValidationException e = new ValidationException(
							uom + " is not one of unit of measures listed in UN/CEFACT Recommendation 20");
					throw e;
				}
			}
			quantityList.add(quantity);
		}
		o.put(fieldName, quantityList);
	}

	// Action
	public static void putAction(Document o, ActionType v) {
		if (v != null)
			o.put("action", v.name());
	}

	// bizstep
	public static void putBizStep(Document o, String v) throws ValidationException {
		if (v == null)
			return;
		else if (bizSteps.contains(v))
			o.put("bizStep", v);
		else {
			ValidationException e = new ValidationException();
			e.setStackTrace(new StackTraceElement[0]);
			e.setReason("non-CBV bizStep: " + v);
			throw e;
		}
	}

	public static void putDisposition(Document o, String v) throws ValidationException {
		if (v == null)
			return;
		else if (dispositions.contains(v))
			o.put("disposition", v);
		else {
			ValidationException e = new ValidationException();
			e.setStackTrace(new StackTraceElement[0]);
			e.setReason("non-CBV disposition: " + v);
			throw e;
		}
	}

	public static void putPersistentDisposition(Document o, PersistentDispositionType pd) throws ValidationException {
		if (pd == null)
			return;

		List<String> unset = pd.getUnset();
		List<String> set = pd.getSet();
		Document pdObj = new Document();
		if (unset != null) {
			ArrayList<String> unsetArr = new ArrayList<String>();
			for (String elem : unset) {
				if (!dispositions.contains(elem)) {
					ValidationException e = new ValidationException();
					e.setStackTrace(new StackTraceElement[0]);
					e.setReason("non-CBV disposition: " + elem);
					throw e;
				}
				unsetArr.add(elem);
			}
			if (!unsetArr.isEmpty())
				pdObj.put("unset", unsetArr);
		}

		if (set != null) {
			ArrayList<String> setArr = new ArrayList<String>();
			for (String elem : set) {
				if (!dispositions.contains(elem)) {
					ValidationException e = new ValidationException();
					e.setStackTrace(new StackTraceElement[0]);
					e.setReason("non-CBV disposition: " + elem);
					throw e;
				}
				setArr.add(elem);
			}
			if (!setArr.isEmpty())
				pdObj.put("set", setArr);
		}
		if (!pdObj.isEmpty())
			o.put("persistentDisposition", pdObj);
	}

	public static void putReadPoint(Document o, ReadPointType v) throws ValidationException {
		if (v == null)
			return;
		IdentifierValidator.checkLocationEPCPureIdentity(gcpLength, v.getId());
		o.put("readPoint", v.getId());
		Document extension = putAny(o, "readPointExt", v.getAny(), false);
		if (extension != null)
			putFlatten(o, "rpf", extension);
	}

	public static void putBizLocation(Document o, BusinessLocationType v) throws ValidationException {
		if (v == null)
			return;
		IdentifierValidator.checkLocationEPCPureIdentity(gcpLength, v.getId());
		o.put("bizLocation", v.getId());
		Document extension = putAny(o, "bizLocationExt", v.getAny(), false);
		if (extension != null)
			putFlatten(o, "blf", extension);
	}

	public static void putBizTransactionList(Document o, BusinessTransactionListType v) throws ValidationException {
		if (v == null)
			return;
		ArrayList<Document> bizTranList = new ArrayList<Document>();
		try {
			for (BusinessTransactionType bizTranType : v.getBizTransaction()) {
				String type = bizTranType.getType();
				String value = bizTranType.getValue();
				if (type != null && !bizTransactionTypes.contains(type)) {
					ValidationException e = new ValidationException();
					e.setStackTrace(new StackTraceElement[0]);
					e.setReason("non-CBV business transaction type: " + type);
					throw e;
				}
				IdentifierValidator.checkBusinessTransactionEPCPureIdentity(gcpLength, value);

				Document bizTransaction = new Document();
				if (type != null)
					bizTransaction.put("type", type);
				bizTransaction.put("value", value.replaceAll("\n", "").replaceAll("\t", "").replaceAll("\\s", ""));
				bizTranList.add(bizTransaction);
			}
		} catch (ValidationException e) {
			throw e;
		}
		o.put("bizTransactionList", bizTranList);
	}

	public static void putSourceList(Document o, SourceListType v) throws ValidationException {
		if (v == null)
			return;
		List<SourceDestType> sdtList = v.getSource();
		if (sdtList.isEmpty())
			return;
		ArrayList<Document> dbList = new ArrayList<Document>();
		try {
			for (SourceDestType sdt : sdtList) {
				String type = sdt.getType();
				if (!StaticResource.sourceDestinationTypes.contains(type)) {
					ValidationException e = new ValidationException();
					e.setStackTrace(new StackTraceElement[0]);
					e.setReason("non-CBV source destination type: " + type);
					throw e;
				}
				Document sd = new Document();
				sd.put("type", type);
				String value = sdt.getValue().replaceAll("\n", "").replaceAll("\t", "").replaceAll("\\s", "");
				IdentifierValidator.checkSourceDestinationEPCPureIdentity(gcpLength, value);
				sd.put("value", value);
				dbList.add(sd);
			}
		} catch (ValidationException e) {
			throw e;
		}
		o.put("sourceList", dbList);
	}

	public static void putDestinationList(Document o, DestinationListType v) throws ValidationException {
		if (v == null)
			return;
		List<SourceDestType> sdtList = v.getDestination();
		if (sdtList.isEmpty())
			return;
		ArrayList<Document> dbList = new ArrayList<Document>();
		try {
			for (SourceDestType sdt : sdtList) {
				String type = sdt.getType();
				if (!StaticResource.sourceDestinationTypes.contains(type)) {
					ValidationException e = new ValidationException();
					e.setStackTrace(new StackTraceElement[0]);
					e.setReason("non-CBV source destination type: " + type);
					throw e;
				}
				Document sd = new Document();
				sd.put("type", type);
				String value = sdt.getValue().replaceAll("\n", "").replaceAll("\t", "").replaceAll("\\s", "");
				IdentifierValidator.checkSourceDestinationEPCPureIdentity(gcpLength, value);
				sd.put("value", value);
				dbList.add(sd);
			}
		} catch (ValidationException e) {
			throw e;
		}
		o.put("destinationList", dbList);
	}

	public static void putTransformationID(Document o, String v) throws ValidationException {
		if (v == null)
			return;
		IdentifierValidator.checkDocumentEPCPureIdentity(gcpLength, v);
		o.put("transformationID", v);
	}

	@SuppressWarnings("rawtypes")
	public static String getOrderedJsonString(List array) {
		List<String> list = new ArrayList<>();
		for (int i = 0; i < array.size(); i++) {
			Object value = array.get(i);
			if (value instanceof Document) {
				list.add(getOrderedJsonString((Document) value).toJson().toString());
			} else if (value instanceof List) {
				list.add(getOrderedJsonString((List) value).toString());
			} else {
				list.add(value.toString());
			}
		}
		Collections.sort(list);
		return list.toString();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Document getOrderedJsonString(Document dbo) {
		TreeSet<String> orderedKeySet = new TreeSet(dbo.keySet());
		Document orderedDbo = new Document();
		for (String key : orderedKeySet) {
			if (key.equals("errf"))
				continue;
			if (key.equals("extf"))
				continue;
			if (key.equals("sef"))
				continue;
			if (key.equals("ilmdf"))
				continue;
			if (key.equals("rpf"))
				continue;
			if (key.equals("blf"))
				continue;
			if (key.equals("errorDeclaration"))
				continue;
			if (key.equals("recordTime"))
				continue;
			if (key.equals("eventTimeZoneOffset"))
				continue;
			if (key.equals("otherAttributes"))
				continue;
			if (key.equals("uom"))
				continue;
			if (key.equals("rUom"))
				continue;
			if (key.equals("value"))
				continue;
			if (key.equals("rValue"))
				continue;
			if (key.equals("minValue"))
				continue;
			if (key.equals("rMinValue"))
				continue;
			if (key.equals("maxValue"))
				continue;
			if (key.equals("rMaxValue"))
				continue;
			if (key.equals("meanValue"))
				continue;
			if (key.equals("rMeanValue"))
				continue;
			if (key.equals("percValue"))
				continue;
			if (key.equals("rPercValue"))
				continue;
			if (key.equals("sDev"))
				continue;

			Object value = dbo.get(key);
			if (value instanceof Document) {
				if (key.equals("hexBinaryValue")) {
					orderedDbo.put(key, new String(Base64.getDecoder().decode(((Document) value).getString("$binary")),
							StandardCharsets.UTF_8));
				} else {
					orderedDbo.put(key, getOrderedJsonString((Document) value));
				}
			} else if (value instanceof List) {
				orderedDbo.put(key, getOrderedJsonString((List) value));
			} else if (value instanceof byte[]) {
				orderedDbo.put(key, new String((byte[]) value, StandardCharsets.UTF_8));
			} else {
				orderedDbo.put(key, value.toString());
			}
		}
		return orderedDbo;
	}

	static String getOrderedJsonString(JsonArray array) {
		List<String> list = new ArrayList<>();
		for (int i = 0; i < array.size(); i++) {
			Object value = array.getValue(i);
			if (value instanceof JsonObject) {
				list.add(getOrderedJsonString((JsonObject) value).toString());
			} else if (value instanceof JsonArray) {
				list.add(getOrderedJsonString((JsonArray) value).toString());
			} else {
				list.add(value.toString());
			}
		}
		Collections.sort(list);
		return list.toString();
	}

	static JsonObject getOrderedJsonString(JsonObject dbo) {
		TreeSet<String> orderedKeySet = new TreeSet<String>(dbo.fieldNames());
		JsonObject orderedDbo = new JsonObject();
		for (String key : orderedKeySet) {
			if (key.equals("errf"))
				continue;
			if (key.equals("extf"))
				continue;
			if (key.equals("sef"))
				continue;
			if (key.equals("ilmdf"))
				continue;
			if (key.equals("errorDeclaration"))
				continue;
			if (key.equals("recordTime"))
				continue;
			if (key.equals("eventTimeZoneOffset"))
				continue;
			if (key.equals("rType"))
				continue;
			if (key.equals("rValue"))
				continue;

			Object value = dbo.getValue(key);
			if (value instanceof JsonObject) {
				orderedDbo.put(key, getOrderedJsonString((JsonObject) value));
			} else if (value instanceof JsonArray) {
				orderedDbo.put(key, getOrderedJsonString((JsonArray) value));
			} else if (value instanceof byte[]) {
				orderedDbo.put(key, new String((byte[]) value, StandardCharsets.UTF_8));
			} else {
				orderedDbo.put(key, value.toString());
			}
		}
		return orderedDbo;
	}

	public static void putEventHashID(Document dbo) {
		// get ordered json string
		Document orderedJson = getOrderedJsonString(dbo);
		// debugging
		System.out.println(orderedJson.toJson().indent(1));
		dbo.put("eventID", "ni:///sha-256;" + getSHA256(orderedJson.toJson().toString()) + "?ver=CBV2.0");
	}

	public static void putEventHashID(JsonObject dbo) {
		// get ordered json string
		Document orderedJson = getOrderedJsonString(Document.parse(dbo.toString()));
		System.out.println(orderedJson.toJson().indent(1));
		dbo.put("eventID", "ni:///sha-256;" + getSHA256(orderedJson.toJson().toString()) + "?ver=CBV2.0");
	}

	public static String getSHA256(String input) {
		String toReturn = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			digest.reset();
			digest.update(input.getBytes(StandardCharsets.UTF_8));
			toReturn = String.format("%064x", new BigInteger(1, digest.digest()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return toReturn;
	}

	@SuppressWarnings("rawtypes")
	public static void putFlatten(Document dbo, String field, Document ext) {
		if (ext == null || ext.isEmpty())
			return;
		Document flat = new Document();
		for (String extKey : ext.keySet()) {
			Object entryValue = ext.get(extKey);
			if (entryValue instanceof Document) {
				putInnerFlatten(flat, (Document) entryValue);
			} else if (entryValue instanceof List) {
				List rootArr = (List) entryValue;
				for (Object objInRootArr : rootArr) {
					if (objInRootArr instanceof Document)
						putInnerFlatten(flat, (Document) objInRootArr);
				}
			}
		}
		dbo.put(field, flat);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void putInnerFlatten(Document flat, Document ext) {
		for (String entryKey : ext.keySet()) {
			Object entryValue = ext.get(entryKey);
			if (entryValue instanceof Document) {
				putInnerFlatten(flat, (Document) entryValue);
			} else {
				if (flat.containsKey(entryKey)) {
					Object obj = flat.get(entryKey);
					if (obj instanceof List) {
						List arr = (List) obj;
						arr.add(entryValue);
						flat.put(entryKey, arr);
					} else {
						obj = flat.remove(entryKey);
						ArrayList arr = new ArrayList();
						arr.add(obj);
						arr.add(entryValue);
						flat.put(entryKey, arr);
					}
				} else {
					if (entryValue instanceof List) {
						List arr = (List) entryValue;
						ArrayList arrWOObject = new ArrayList();
						for (Object objInArr : arr) {
							if (objInArr instanceof Document) {
								putInnerFlatten(flat, (Document) objInArr);
							} else {
								arrWOObject.add(objInArr);
							}
						}
						flat.put(entryKey, arrWOObject);
					} else {
						flat.put(entryKey, entryValue);
					}

				}
			}
		}
	}

	public static void putFlatten(JsonObject dbo, String field, JsonObject ext) {
		if (ext == null || ext.isEmpty())
			return;
		JsonObject flat = new JsonObject();
		for (String extKey : ext.fieldNames()) {
			Object entryValue = ext.getValue(extKey);
			if (entryValue instanceof JsonObject) {
				putInnerFlatten(flat, (JsonObject) entryValue);
			} else if (entryValue instanceof JsonArray) {
				JsonArray rootArr = (JsonArray) entryValue;
				for (Object objInRootArr : rootArr) {
					if (objInRootArr instanceof JsonObject)
						putInnerFlatten(flat, (JsonObject) objInRootArr);
				}
			}
		}
		dbo.put(field, flat);
	}

	private static void putInnerFlatten(JsonObject flat, JsonObject ext) {
		for (String entryKey : ext.fieldNames()) {
			Object entryValue = ext.getValue(entryKey);
			if (entryValue instanceof JsonObject) {
				putInnerFlatten(flat, (JsonObject) entryValue);
			} else {
				if (flat.containsKey(entryKey)) {
					Object obj = flat.getValue(entryKey);
					if (obj instanceof JsonArray) {
						JsonArray arr = (JsonArray) obj;
						arr.add(entryValue);
						flat.put(entryKey, arr);
					} else {
						obj = flat.remove(entryKey);
						JsonArray arr = new JsonArray();
						arr.add(obj);
						arr.add(entryValue);
						flat.put(entryKey, arr);
					}
				} else {
					if (entryValue instanceof JsonArray) {
						JsonArray arr = (JsonArray) entryValue;
						JsonArray arrWOObject = new JsonArray();
						for (Object objInArr : arr) {
							if (objInArr instanceof JsonObject) {
								putInnerFlatten(flat, (JsonObject) objInArr);
							} else {
								arrWOObject.add(objInArr);
							}
						}
						flat.put(entryKey, arrWOObject);
					} else {
						flat.put(entryKey, entryValue);
					}

				}
			}
		}
	}

	public static void putSensorElementList(Document o, SensorElementListType v) throws ValidationException {
		if (v == null)
			return;
		if (v.getSensorElement().isEmpty())
			return;
		ArrayList<Document> sensorArray = new ArrayList<Document>();
		for (SensorElementType set : v.getSensorElement()) {
			Document sensorObj = new Document();
			// sensor meta data
			SensorMetadataType smdt = set.getSensorMetadata();
			if (smdt != null) {
				Document sensorMeta = new Document();
				if (smdt.getTime() != null)
					sensorMeta.put("time", smdt.getTime().toGregorianCalendar().getTimeInMillis());
				if (smdt.getStartTime() != null)
					sensorMeta.put("startTime", smdt.getStartTime().toGregorianCalendar().getTimeInMillis());
				if (smdt.getEndTime() != null)
					sensorMeta.put("endTime", smdt.getEndTime().toGregorianCalendar().getTimeInMillis());
				if (smdt.getDeviceID() != null) {
					// EPC
					IdentifierValidator.checkEPCPureIdentity(gcpLength, smdt.getDeviceID());
					sensorMeta.put("deviceID", smdt.getDeviceID());
				}
				if (smdt.getDeviceMetadata() != null) {
					// ResourceID
					IdentifierValidator.checkDocumentEPCPureIdentity(gcpLength, smdt.getDeviceMetadata());
					sensorMeta.put("deviceMetadata", smdt.getDeviceMetadata());
				}
				if (smdt.getRawData() != null) {
					// ResourceID
					IdentifierValidator.checkDocumentEPCPureIdentity(gcpLength, smdt.getRawData());
					sensorMeta.put("rawData", smdt.getRawData());
				}
				if (smdt.getDataProcessingMethod() != null) {
					// ResourceID
					IdentifierValidator.checkDocumentEPCPureIdentity(gcpLength, smdt.getDataProcessingMethod());
					sensorMeta.put("dataProcessingMethod", smdt.getDataProcessingMethod());
				}
				if (smdt.getBizRules() != null) {
					// ResourceID
					IdentifierValidator.checkDocumentEPCPureIdentity(gcpLength, smdt.getBizRules());
					sensorMeta.put("bizRules", smdt.getBizRules());
				}
				if (smdt.getOtherAttributes() != null && smdt.getOtherAttributes().size() != 0)
					sensorMeta.put("otherAttributes", getOtherAttributesMap(smdt.getOtherAttributes()));

				sensorObj.put("sensorMetadata", sensorMeta);
			}

			ArrayList<Document> sensorReportArray = new ArrayList<Document>();
			// sensor report
			for (SensorReportType e1 : set.getSensorReport()) {
				Document sensorReportObj = new Document();
				String type = e1.getType();
				String exception = e1.getException();
				if ((type == null && exception == null) || (type != null && exception != null)) {
					throw new ValidationException("sensorReport should have one of 'type' or 'exception'");
				}

				if (exception != null) {
					if (!exception.equals("ALARM_CONDITION") && !exception.equals("ERROR_CONDITION"))
						throw new ValidationException(
								"sensorReport - exception should be one of 'ERROR_CONDITION' or 'ALARM_CONDITION'");
					else
						sensorReportObj.put("exception", e1.getException());
				}

				if (e1.getMicroorganism() != null && e1.getChemicalSubstance() != null) {
					throw new ValidationException(
							"microorganism and chemicalSubstance fields should not coexist in sensorReport field");
				}

				if (e1.getDeviceID() != null) {
					// EPC
					IdentifierValidator.checkEPCPureIdentity(gcpLength, e1.getDeviceID());
					sensorReportObj.put("deviceID", e1.getDeviceID());
				}
				if (e1.getDeviceMetadata() != null) {
					// ResourceID
					IdentifierValidator.checkDocumentEPCPureIdentity(gcpLength, e1.getDeviceMetadata());
					sensorReportObj.put("deviceMetadata", e1.getDeviceMetadata());
				}
				if (e1.getRawData() != null) {
					// ResourceID
					IdentifierValidator.checkDocumentEPCPureIdentity(gcpLength, e1.getRawData());
					sensorReportObj.put("rawData", e1.getRawData());
				}
				if (e1.getDataProcessingMethod() != null) {
					// ResourceID
					IdentifierValidator.checkDocumentEPCPureIdentity(gcpLength, e1.getDataProcessingMethod());
					sensorReportObj.put("dataProcessingMethod", e1.getDataProcessingMethod());
				}
				if (e1.getTime() != null) {
					sensorReportObj.put("time", e1.getTime().toGregorianCalendar().getTimeInMillis());
				}
				if (e1.getMicroorganism() != null) {
					IdentifierValidator.checkMicroorganismValue(e1.getMicroorganism());
					sensorReportObj.put("microorganism", e1.getMicroorganism());
				}
				if (e1.getChemicalSubstance() != null) {
					IdentifierValidator.checkChemicalSubstance(e1.getChemicalSubstance());
					sensorReportObj.put("chemicalSubstance", e1.getChemicalSubstance());
				}

				if (e1.getComponent() != null) {
					IdentifierValidator.checkComponent(e1.getComponent());
					sensorReportObj.put("component", e1.getComponent());
				}

				if (e1.getStringValue() != null)
					sensorReportObj.put("stringValue", e1.getStringValue());

				if (e1.getBooleanValue() != null)
					sensorReportObj.put("booleanValue", e1.getBooleanValue());

				if (e1.getHexBinaryValue() != null) {
					sensorReportObj.put("hexBinaryValue", e1.getHexBinaryValue());
				}

				if (e1.getUriValue() != null)
					sensorReportObj.put("uriValue", e1.getUriValue());

				if (e1.getCoordinateReferenceSystem() != null)
					sensorReportObj.put("coordinateReferenceSystem", e1.getCoordinateReferenceSystem());

				if (e1.getOtherAttributes() != null && !e1.getOtherAttributes().isEmpty())
					sensorReportObj.put("otherAttributes", getOtherAttributesMap(e1.getOtherAttributes()));

				if (e1.getPercRank() != null)
					sensorReportObj.put("percRank", Double.valueOf(e1.getPercRank().toString()));

				if (type != null) {
					if (measurements.contains(e1.getType())) {
						sensorReportObj.put("type", e1.getType());
					} else {
						ValidationException e = new ValidationException(
								"unsupported sensor measurement: " + e1.getType());
						throw e;
					}

					String uom = null;
					if (e1.getUom() == null) {
						uom = EPCISServer.unitConverter.getRepresentativeUoMFromType(type);
						sensorReportObj.put("uom", uom);
					} else {
						uom = e1.getUom();
						EPCISServer.unitConverter.checkUnitOfMeasure(type, e1.getUom());
						sensorReportObj.put("uom", uom);
					}

					// value / minValue / maxValue / meanValue
					String rUom = EPCISServer.unitConverter.getRepresentativeUoMFromType(type);
					sensorReportObj.put("rUom", rUom);

					// value
					if (e1.getValueAttribute() != null) {
						double value = e1.getValueAttribute();

						sensorReportObj.put("value", value);
						sensorReportObj.put("rValue",
								EPCISServer.unitConverter.getRepresentativeValue(type, uom, value));
					}

					// minValue
					if (e1.getMinValue() != null) {
						double value = e1.getMinValue();
						sensorReportObj.put("minValue", value);
						sensorReportObj.put("rMinValue",
								EPCISServer.unitConverter.getRepresentativeValue(type, uom, value));
					}

					// maxValue
					if (e1.getMaxValue() != null) {
						double value = e1.getMaxValue();
						sensorReportObj.put("maxValue", value);
						sensorReportObj.put("rMaxValue",
								EPCISServer.unitConverter.getRepresentativeValue(type, uom, value));
					}

					// meanValue
					if (e1.getMeanValue() != null) {
						double value = e1.getMeanValue();
						sensorReportObj.put("meanValue", value);
						sensorReportObj.put("rMeanValue",
								EPCISServer.unitConverter.getRepresentativeValue(type, uom, value));
					}

					// percValue
					if (e1.getPercValue() != null) {
						double value = e1.getPercValue();
						sensorReportObj.put("percValue", value);
						sensorReportObj.put("rPercValue",
								EPCISServer.unitConverter.getRepresentativeValue(type, uom, value));
					}

					// sDev
					if (e1.getSDev() != null) {
						double sDev = Double.valueOf(e1.getSDev().toString());
						sensorReportObj.put("sDev", sDev);
						sensorReportObj.put("rSDev", EPCISServer.unitConverter.getRepresentativeValue(type, uom, sDev));
					}

				} else {

				}
				sensorReportArray.add(sensorReportObj);
			}
			sensorObj.put("sensorReport", sensorReportArray);
			// sensorElement extension
			Document sEx = putAny(sensorObj, "extension", set.getAny(), false);
			if (sEx != null)
				putFlatten(sensorObj, "sef", sEx);

			sensorArray.add(sensorObj);
		}
		o.put("sensorElementList", sensorArray);
	}

	public static void putOtherAttributes(Document o, Map<QName, String> v) {
		if (v == null || v.isEmpty())
			return;
		Document map2Save = new Document();
		for (QName qName : v.keySet()) {
			String value = v.get(qName);
			map2Save.put(qName.toString(), value);
		}
		o.put("otherAttributes", map2Save);
	}

	public static void putEventID(Document o, String v) {
		if (v == null)
			return;
		o.put("eventID", v);
	}

	public static Document putErrorDeclaration(Document o, ErrorDeclarationType edt) throws ValidationException {
		if (edt == null)
			return null;
		Document errorBson = new Document();
		List<Object> obj = edt.getDeclarationTimeOrReasonOrCorrectiveEventIDs();
		List<Object> extObj = new ArrayList<Object>();
		for (Object elem : obj) {
			if (elem instanceof JAXBElement) {
				JAXBElement<?> element = (JAXBElement<?>) elem;
				String name = element.getName().getLocalPart();
				if (name.equals("declarationTime")) {
					long declarationTime = ((XMLGregorianCalendar) element.getValue()).toGregorianCalendar()
							.getTimeInMillis();
					errorBson.put("declarationTime", declarationTime);
				}

				if (name.equals("reason")) {
					String reason = (String) element.getValue();

					if (errorReasons.contains(reason)) {
						errorBson.put("reason", reason);
					} else {
						ValidationException e = new ValidationException("non-CBV error reason");
						throw e;
					}
				}
				if (name.equals("correctiveEventIDs")) {
					CorrectiveEventIDsType cIDs = (CorrectiveEventIDsType) element.getValue();
					List<String> cIDStringList = cIDs.getCorrectiveEventID();
					ArrayList<String> correctiveIDBsonArray = new ArrayList<String>();
					for (String cIDString : cIDStringList) {
						correctiveIDBsonArray.add(cIDString);
					}
					if (correctiveIDBsonArray.size() != 0) {
						errorBson.put("correctiveEventIDs", correctiveIDBsonArray);
					}
				}
			} else if (elem instanceof Element) {
				extObj.add(elem);
			}
		}
		putAny(errorBson, "extension", extObj, false);
		o.put("errorDeclaration", errorBson);

		return errorBson;
	}

	public static Document getOtherAttributesMap(Map<QName, String> map) {
		Document map2Save = new Document();
		for (QName qName : map.keySet()) {
			String value = map.get(qName);
			// example:someFurtherMetaData = "someText"
			// localPart=someFurtherMetadata
			String localPart = qName.getLocalPart();
			// namespaceURI = "http://~~~"
			String namespaceURI = qName.getNamespaceURI();
			// prefix = "example"
			map2Save.put(encodeMongoObjectKey(namespaceURI + "#" + localPart), value);
		}
		return map2Save;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Document putAny(Document o, String extKey, List<Object> v, boolean allowsLocalTag)
			throws ValidationException {
		if (v == null || v.isEmpty())
			return null;
		Document map2Save = new Document();
		for (Object obj : v) {
			if (obj instanceof Element) {
				Element element = (Element) obj;
				String qname = element.getNodeName();
				// Process Namespace
				String[] checkArr = qname.split(":");

				if (!allowsLocalTag) {
					if (checkArr.length != 2)
						continue;
				}

				String qnameKey;
				if (checkArr.length == 1) {
					qnameKey = "#" + qname;
				} else if (checkArr.length == 2) {
					qnameKey = encodeMongoObjectKey(element.getNamespaceURI() + "#" + checkArr[1]);
				} else {
					return null;
				}

				NodeList nl = element.getChildNodes();

				for (int nlIdx = 0; nlIdx < nl.getLength(); nlIdx++) {
					Node node = nl.item(nlIdx);

					if (nl.getLength() == 1 && node instanceof Text) {
						String value = node.getTextContent();
						CBVAttributeUtil.checkCBVCompliantAttribute(qnameKey, element.getAttributes(), value);
						Object convertedValue = null;
						try {
							convertedValue = convertType(value, element);
						} catch (ParseException e) {
							throw new ValidationException(e.getMessage());
						}
						if (!map2Save.containsKey(qnameKey)) {
							// first occurrence
							map2Save.put(qnameKey, convertedValue);
						} else if (map2Save.containsKey(qnameKey) && !(map2Save.get(qnameKey) instanceof List)) {
							// second occurrence
							Object firstObj = map2Save.remove(qnameKey);
							ArrayList arr = new ArrayList();
							arr.add(firstObj);
							arr.add(convertedValue);
							map2Save.put(qnameKey, arr);
						} else {
							// third or more occurrence
							List arr = map2Save.get(qnameKey, List.class);
							arr.add(convertedValue);
							map2Save.put(qnameKey, arr);
						}
						break;
					} else {
						// element inner node
						if (node instanceof Text)
							continue;

						Document obj2Save = new Document();
						Document sub2Save = new Document();
						do {
							if (node instanceof Element) {
								try {
									obj2Save = getAnyMap((Element) node, sub2Save);
								} catch (ParseException e) {
									throw new ValidationException(e.getMessage());
								}
							}
						} while ((node = node.getNextSibling()) != null);

						if (!map2Save.containsKey(qnameKey)) {
							// first occurrence
							map2Save.put(qnameKey, obj2Save);
						} else if (map2Save.containsKey(qnameKey) && !(map2Save.get(qnameKey) instanceof List)) {
							// second occurrence
							Object firstObj = map2Save.remove(qnameKey);
							ArrayList arr = new ArrayList();
							arr.add(firstObj);
							arr.add(obj2Save);
							map2Save.put(qnameKey, arr);
						} else {
							// third or more occurrence
							List arr = map2Save.get(qnameKey, List.class);
							arr.add(obj2Save);
							map2Save.put(qnameKey, arr);
						}
						break;
					}
				}
			}
		}
		if (!map2Save.isEmpty())
			o.put(extKey, map2Save);
		return map2Save;
	}

	// Inside recursive logic
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Document getAnyMap(Element element, Document map2Save) throws ParseException {

		String qname = element.getNodeName();
		// Process Namespace
		String[] checkArr = qname.split(":");

		String qnameKey;
		if (checkArr.length == 1) {
			qnameKey = "#" + qname;
		} else if (checkArr.length == 2) {
			qnameKey = encodeMongoObjectKey(element.getNamespaceURI() + "#" + checkArr[1]);
		} else {
			return null;
		}

		NodeList nl = element.getChildNodes();

		for (int nlIdx = 0; nlIdx < nl.getLength(); nlIdx++) {
			Node node = nl.item(nlIdx);
			if (nl.getLength() == 1 && node instanceof Text) {
				String value = node.getTextContent();
				if (!map2Save.containsKey(qnameKey)) {
					// first occurrence
					map2Save.put(qnameKey, convertType(value, element));
				} else if (map2Save.containsKey(qnameKey) && !(map2Save.get(qnameKey) instanceof List)) {
					// second occurrence
					Object firstObj = map2Save.remove(qnameKey);
					ArrayList arr = new ArrayList();
					arr.add(firstObj);
					arr.add(convertType(value, element));
					map2Save.put(qnameKey, arr);
				} else {
					// third or more occurrence
					List arr = map2Save.get(qnameKey, List.class);
					arr.add(convertType(value, element));
					map2Save.put(qnameKey, arr);
				}
				break;
			} else {
				// element inner node
				if (node instanceof Text)
					continue;

				Document obj2Save = new Document();
				Document sub2Save = new Document();
				do {
					if (node instanceof Element) {
						obj2Save = getAnyMap((Element) node, sub2Save);
					}
				} while ((node = node.getNextSibling()) != null);

				if (!map2Save.containsKey(qnameKey)) {
					// first occurrence
					map2Save.put(qnameKey, obj2Save);
				} else if (map2Save.containsKey(qnameKey) && !(map2Save.get(qnameKey) instanceof List)) {
					// second occurrence
					Object firstObj = map2Save.remove(qnameKey);
					ArrayList arr = new ArrayList();
					arr.add(firstObj);
					arr.add(obj2Save);
					map2Save.put(qnameKey, arr);
				} else {
					// third or more occurrence
					List arr = map2Save.get(qnameKey, List.class);
					arr.add(obj2Save);
					map2Save.put(qnameKey, arr);
				}
				break;
			}

		}

		return map2Save;
	}

	public static Object convertType(String targetValue, Element element) throws ParseException {
		// Int xsd:int Int
		// Float xsd:double Double
		// Time epcis:DateTimeStamp Long
		// Boolean xsd:boolean Boolean
		// String xsd:string String

		// List of String epcisq:ArrayOfString
		// Void epcisq:VoidHolder

		// xsi: int, double, boolean, DateTimeStamp, string
		String type = element.getAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "type");
		if (type != null && !type.isEmpty() && targetValue.indexOf('^') == -1) {
			if (type.contains("int")) {
				return Integer.parseInt(targetValue);
			} else if (type.contains("double")) {
				return Double.parseDouble(targetValue);
			} else if (type.contains("boolean")) {
				return Boolean.parseBoolean(targetValue);
			} else if (type.contains("DateTimeStamp")) {
				return TimeUtil.toUnixEpoch(targetValue);
			} else if (type.contains("string")) {
				return targetValue;
			}
		}
		return targetValue;
	}

	public static String encodeMongoObjectKey(String key) {
		key = key.replace(".", "\uff0e");
		return key;
	}
}
