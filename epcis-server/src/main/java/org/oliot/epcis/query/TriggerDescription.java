package org.oliot.epcis.query;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

import org.bson.Document;
import org.bson.types.Binary;
import org.oliot.epcis.model.ArrayOfString;
import org.oliot.epcis.model.ImplementationException;
import org.oliot.epcis.model.QueryParam;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.model.Subscribe;
import org.oliot.epcis.model.SubscribeNotPermittedException;
import org.oliot.epcis.model.VoidHolder;

import org.oliot.epcis.util.TimeUtil;
import org.w3c.dom.Element;

@SuppressWarnings("unused")
public class TriggerDescription {

	private SOAPQueryUnmarshaller unmarshaller;

	private List<String> eventType;
	private Long GE_eventTime;
	private Long LT_eventTime;
	private Long GE_recordTime;
	private Long LT_recordTime;
	private List<String> EQ_action;
	private List<String> EQ_bizStep;
	private List<String> EQ_disposition;
	private List<String> EQ_persistentDisposition_set;
	private List<String> EQ_persistentDisposition_unset;
	private List<String> EQ_readPoint;
	private List<String> EQ_bizLocation;
	private List<String> EQ_transformationID;
	private List<String> EQ_eventID;
	private VoidHolder EXISTS_errorDeclaration;
	private Long GE_errorDeclarationTime;
	private Long LT_errorDeclarationTime;
	private List<String> EQ_errorReason;
	private List<String> EQ_correctiveEventID;
	private List<String> MATCH_epc;
	private List<String> MATCH_parentID;
	private List<String> MATCH_inputEPC;
	private List<String> MATCH_outputEPC;
	private List<String> MATCH_anyEPC;
	private List<String> MATCH_epcClass;
	private List<String> MATCH_inputEPCClass;
	private List<String> MATCH_outputEPCClass;
	private List<String> MATCH_anyEPCClass;

	private Long GE_startTime;
	private Long LT_startTime;
	private Long GE_endTime;
	private Long LT_endTime;
	private Long GE_SENSORMETADATA_time;
	private Long LT_SENSORMETADATA_time;
	private Long GE_SENSORREPORT_time;
	private Long LT_SENSORREPORT_time;

	private List<String> EQ_deviceID;
	private List<String> EQ_SENSORMETADATA_deviceID;
	private List<String> EQ_SENSORREPORT_deviceID;
	private List<String> EQ_SENSORMETADATA_deviceMetadata;
	private List<String> EQ_SENSORREPORT_deviceMetadata;
	private List<String> EQ_SENSORMETADATA_rawData;
	private List<String> EQ_SENSORREPORT_rawData;

	private List<String> EQ_dataProcessingMethod;
	private List<String> EQ_SENSORMETADATA_dataProcessingMethod;
	private List<String> EQ_SENSORREPORT_dataProcessingMethod;

	private List<String> EQ_microorganism;
	private List<String> EQ_chemicalSubstance;
	private List<String> EQ_bizRules;
	private List<String> EQ_stringValue;
	private Boolean EQ_booleanValue;
	private List<String> EQ_hexBinaryValue;
	private List<String> EQ_uriValue;
	private Long GE_percRank;
	private Long LT_percRank;
	private Entry<List<String>> EQ_bizTransaction;
	private Entry<List<String>> EQ_source;
	private Entry<List<String>> EQ_destination;

	private Entry<Double> EQ_quantity;
	private Entry<Double> GT_quantity;
	private Entry<Double> GE_quantity;
	private Entry<Double> LT_quantity;
	private Entry<Double> LE_quantity;

	private Entry<Object> EQ_INNER_ILMD;
	private Entry<Object> GT_INNER_ILMD;
	private Entry<Object> GE_INNER_ILMD;
	private Entry<Object> LT_INNER_ILMD;
	private Entry<Object> LE_INNER_ILMD;
	private String EXISTS_INNER_ILMD;

	private Entry<Object> EQ_INNER_SENSORELEMENT;
	private Entry<Object> GT_INNER_SENSORELEMENT;
	private Entry<Object> GE_INNER_SENSORELEMENT;
	private Entry<Object> LT_INNER_SENSORELEMENT;
	private Entry<Object> LE_INNER_SENSORELEMENT;
	private String EXISTS_INNER_SENSORELEMENT;

	private Entry<Object> EQ_INNER_readPoint;
	private Entry<Object> GT_INNER_readPoint;
	private Entry<Object> GE_INNER_readPoint;
	private Entry<Object> LT_INNER_readPoint;
	private Entry<Object> LE_INNER_readPoint;
	private String EXISTS_INNER_readPoint;

	private Entry<Object> EQ_INNER_bizLocation;
	private Entry<Object> GT_INNER_bizLocation;
	private Entry<Object> GE_INNER_bizLocation;
	private Entry<Object> LT_INNER_bizLocation;
	private Entry<Object> LE_INNER_bizLocation;
	private String EXISTS_INNER_bizLocation;

	private Entry<Object> EQ_INNER_ERROR_DECLARATION;
	private Entry<Object> GT_INNER_ERROR_DECLARATION;
	private Entry<Object> GE_INNER_ERROR_DECLARATION;
	private Entry<Object> LT_INNER_ERROR_DECLARATION;
	private Entry<Object> LE_INNER_ERROR_DECLARATION;
	private String EXISTS_INNER_ERROR_DECLARATION;

	private Entry<Object> EQ_INNER;
	private Entry<Object> GT_INNER;
	private Entry<Object> GE_INNER;
	private Entry<Object> LT_INNER;
	private Entry<Object> LE_INNER;
	private String EXISTS_INNER;

	private Entry<Object> EQ_ILMD;
	private Entry<Object> GT_ILMD;
	private Entry<Object> GE_ILMD;
	private Entry<Object> LT_ILMD;
	private Entry<Object> LE_ILMD;
	private String EXISTS_ILMD;

	private Entry<Object> EQ_SENSORELEMENT;
	private Entry<Object> GT_SENSORELEMENT;
	private Entry<Object> GE_SENSORELEMENT;
	private Entry<Object> LT_SENSORELEMENT;
	private Entry<Object> LE_SENSORELEMENT;
	private String EXISTS_SENSORELEMENT;
	private Entry<Object> EQ_SENSORMETADATA;
	private Entry<Object> EQ_SENSORREPORT;
	private String EXISTS_SENSORMETADATA;
	private String EXISTS_SENSORREPORT;

	private Entry<Object> EQ_readPoint_extension;
	private Entry<Object> GT_readPoint_extension;
	private Entry<Object> GE_readPoint_extension;
	private Entry<Object> LT_readPoint_extension;
	private Entry<Object> LE_readPoint_extension;
	private String EXISTS_readPoint_extension;

	private Entry<Object> EQ_bizLocation_extension;
	private Entry<Object> GT_bizLocation_extension;
	private Entry<Object> GE_bizLocation_extension;
	private Entry<Object> LT_bizLocation_extension;
	private Entry<Object> LE_bizLocation_extension;
	private String EXISTS_bizLocation_extension;

	private Entry<Object> EQ_ERROR_DECLARATION_extension;
	private Entry<Object> GT_ERROR_DECLARATION_extension;
	private Entry<Object> GE_ERROR_DECLARATION_extension;
	private Entry<Object> LT_ERROR_DECLARATION_extension;
	private Entry<Object> LE_ERROR_DECLARATION_extension;
	private String EXISTS_ERROR_DECLARATION_extension;

	private Entry<Object> EQ_extension;
	private Entry<Object> GT_extension;
	private Entry<Object> GE_extension;
	private Entry<Object> LT_extension;
	private Entry<Object> LE_extension;
	private String EXISTS_extension;

	private List<String> EQ_type;
	private Double EQ_value;
	private Double GT_value;
	private Double GE_value;
	private Double LT_value;
	private Double LE_value;

	private Double EQ_minValue;
	private Double GT_minValue;
	private Double GE_minValue;
	private Double LT_minValue;
	private Double LE_minValue;

	private Double EQ_maxValue;
	private Double GT_maxValue;
	private Double GE_maxValue;
	private Double LT_maxValue;
	private Double LE_maxValue;

	private Double EQ_meanValue;
	private Double GT_meanValue;
	private Double GE_meanValue;
	private Double LT_meanValue;
	private Double LE_meanValue;

	private Double EQ_sDev;
	private Double GT_sDev;
	private Double GE_sDev;
	private Double LT_sDev;
	private Double LE_sDev;

	private Double EQ_percValue;
	private Double GT_percValue;
	private Double GE_percValue;
	private Double LT_percValue;
	private Double LE_percValue;

	private List<String> WD_readPoint;
	private List<String> WD_bizLocation;
	private Entry<List<String>> HASATTR;
	private Entry<List<String>> EQ_ATTR;

	private String subscriptionID;

	private String triggerKey;

	public boolean isPassVoidHolder(Object value) {
		if (value == null)
			return false;
		return true;
	}

	public boolean isPassString(List<String> query, String value) {
		if (!query.contains(value))
			return false;
		return true;
	}
	
	public boolean isPassBoolean(Boolean query, Boolean value) {
		if(query.equals(value))
			return true;
		return false;
	}

	public boolean isPassListOfString(List<String> query, List<String> value) {
		if (Collections.disjoint(query, value))
			return false;
		return true;
	}

	@SuppressWarnings("unchecked")
	public boolean isPassListOfMatchString(List<String> query, List<String>... value) {
		for (String q : query) {
			if (q.contains("*")) {
				q = q.replaceAll("\\.", "[.]");
				q = q.replaceAll("\\*", "(.)*");
			}
			for (List<String> v : value) {
				if (v == null)
					continue;
				for (String vv : v) {
					if (Pattern.matches(q, vv)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean isPassGELong(Long query, Long value) {
		if (value < query)
			return false;
		return true;
	}

	public boolean isPassLTLong(Long query, Long value) {
		if (value >= query)
			return false;
		return true;
	}

	public boolean isPassLong(Long ge, Long lt, Long value) {
		if (ge != null && lt != null) {
			return value >= ge && value < lt;
		} else if (ge != null) {
			return value >= ge;
		} else if (lt != null) {
			return value < lt;
		} else {
			// not happen
			return false;
		}
	}

	// TODO:
	@SuppressWarnings("unchecked")
	public boolean isPass(Document doc) {
		try {
			if (eventType != null && isPassString(eventType, doc.getString("type"))) {
				return false;
			}

			if (GE_eventTime != null || LT_eventTime != null) {
				if (!isPassLong(GE_eventTime, LT_eventTime, doc.getLong("eventTime")))
					return false;
			}

			if (GE_recordTime != null || LT_recordTime != null) {
				if (!isPassLong(GE_recordTime, LT_recordTime, doc.getLong("recordTime")))
					return false;
			}

			if (EQ_action != null && !isPassString(EQ_action, doc.getString("action"))) {
				return false;
			}

			if (EQ_bizStep != null && !isPassString(EQ_bizStep, doc.getString("bizStep"))) {
				return false;
			}

			if (EQ_disposition != null && !isPassString(EQ_disposition, doc.getString("disposition"))) {
				return false;
			}

			if (EQ_persistentDisposition_set != null && !isPassListOfString(EQ_persistentDisposition_set,
					doc.get("persistentDisposition", Document.class).getList("set", String.class))) {
				return false;
			}

			if (EQ_persistentDisposition_unset != null && !isPassListOfString(EQ_persistentDisposition_unset,
					doc.get("persistentDisposition", Document.class).getList("unset", String.class))) {
				return false;
			}

			if (EQ_readPoint != null && !isPassString(EQ_readPoint, doc.getString("readPoint"))) {
				return false;
			}

			if (EQ_bizLocation != null && !isPassString(EQ_bizLocation, doc.getString("bizLocation"))) {
				return false;
			}

			if (EQ_transformationID != null && !isPassString(EQ_transformationID, doc.getString("transformationID"))) {
				return false;
			}

			if (EQ_eventID != null && !isPassString(EQ_eventID, doc.getString("eventID"))) {
				return false;
			}

			if (EXISTS_errorDeclaration != null && !isPassVoidHolder(doc.get("errorDeclaration"))) {
				return false;
			}

			if (GE_errorDeclarationTime != null || LT_errorDeclarationTime != null) {
				if (!isPassLong(GE_errorDeclarationTime, LT_errorDeclarationTime,
						doc.get("errorDeclaration", Document.class).getLong("declarationTime")))
					return false;
			}

			if (EQ_errorReason != null
					&& !isPassString(EQ_errorReason, doc.get("errorDeclaration", Document.class).getString("reason"))) {
				return false;
			}
			if (EQ_correctiveEventID != null && !isPassListOfString(EQ_correctiveEventID,
					doc.get("errorDeclaration", Document.class).getList("correctiveEventIDs", String.class))) {
				return false;
			}
			if (MATCH_epc != null && !isPassListOfMatchString(MATCH_epc, doc.getList("epcList", String.class))) {
				return false;
			}
			if (MATCH_parentID != null
					&& !isPassListOfMatchString(MATCH_parentID, List.of(doc.getString("parentID")))) {
				return false;
			}
			if (MATCH_inputEPC != null
					&& !isPassListOfMatchString(MATCH_inputEPC, doc.getList("inputEPCList", String.class))) {
				return false;
			}
			if (MATCH_outputEPC != null
					&& !isPassListOfMatchString(MATCH_outputEPC, doc.getList("outputEPCList", String.class))) {
				return false;
			}
			if (MATCH_anyEPC != null && !isPassListOfMatchString(MATCH_anyEPC, List.of(doc.getString("parentID")),
					doc.getList("epcList", String.class), doc.getList("inputEPCList", String.class),
					doc.getList("outputEPCList", String.class))) {
				return false;
			}

			if (GE_startTime != null || LT_startTime != null) {
				List<Document> sensorElementList = doc.getList("sensorElementList", Document.class);
				if (sensorElementList == null || sensorElementList.isEmpty())
					return false;

				boolean isPartialPass = false;
				for (Document sensorElement : sensorElementList) {
					Document sensorMetadata = sensorElement.get("sensorMetadata", Document.class);
					if (sensorMetadata == null)
						continue;
					Long startTime = sensorMetadata.getLong("startTime");
					if (startTime == null)
						continue;
					if (isPassLong(GE_startTime, LT_startTime, startTime)) {
						isPartialPass = true;
						break;
					}

				}
				if (!isPartialPass)
					return false;
			}

			if (GE_endTime != null || LT_endTime != null) {
				List<Document> sensorElementList = doc.getList("sensorElementList", Document.class);
				if (sensorElementList == null || sensorElementList.isEmpty())
					return false;

				boolean isPartialPass = false;
				for (Document sensorElement : sensorElementList) {
					Document sensorMetadata = sensorElement.get("sensorMetadata", Document.class);
					if (sensorMetadata == null)
						continue;
					Long endTime = sensorMetadata.getLong("endTime");
					if (endTime == null)
						continue;
					if (isPassLong(GE_endTime, LT_endTime, endTime)) {
						isPartialPass = true;
						break;
					}

				}
				if (!isPartialPass)
					return false;
			}

			if (GE_SENSORMETADATA_time != null || LT_SENSORMETADATA_time != null) {
				List<Document> sensorElementList = doc.getList("sensorElementList", Document.class);
				if (sensorElementList == null || sensorElementList.isEmpty())
					return false;

				boolean isPartialPass = false;
				for (Document sensorElement : sensorElementList) {
					Document sensorMetadata = sensorElement.get("sensorMetadata", Document.class);
					if (sensorMetadata == null)
						continue;
					Long time = sensorMetadata.getLong("time");
					if (time == null)
						continue;
					if (isPassLong(GE_SENSORMETADATA_time, LT_SENSORMETADATA_time, time)) {
						isPartialPass = true;
						break;
					}

				}
				if (!isPartialPass)
					return false;
			}

			if (GE_SENSORREPORT_time != null || LT_SENSORREPORT_time != null) {
				List<Document> sensorElementList = doc.getList("sensorElementList", Document.class);
				if (sensorElementList == null || sensorElementList.isEmpty())
					return false;

				boolean isPartialPass = false;
				for (Document sensorElement : sensorElementList) {
					List<Document> sensorReports = sensorElement.getList("sensorReport", Document.class);
					if (sensorReports == null || sensorReports.isEmpty())
						continue;
					for (Document sensorReport : sensorReports) {
						Long time = sensorReport.getLong("time");
						if (time == null)
							continue;
						if (isPassLong(GE_SENSORREPORT_time, LT_SENSORREPORT_time, time)) {
							isPartialPass = true;
							break;
						}
					}
				}
				if (!isPartialPass)
					return false;
			}

			if (EQ_deviceID != null || EQ_SENSORMETADATA_deviceID != null || EQ_SENSORREPORT_deviceID != null) {
				List<Document> sensorElementList = doc.getList("sensorElementList", Document.class);
				if (sensorElementList == null || sensorElementList.isEmpty())
					return false;

				boolean isPartialPass1 = false;
				boolean isPartialPass2 = false;
				for (Document sensorElement : sensorElementList) {
					Document sensorMetadata = sensorElement.get("sensorMetadata", Document.class);
					if (sensorMetadata != null) {
						String did = sensorMetadata.getString("deviceID");
						if (did != null) {
							if (EQ_deviceID != null && isPassString(EQ_deviceID, did)) {
								isPartialPass1 = true;
							} else if (EQ_SENSORMETADATA_deviceID != null
									&& isPassString(EQ_SENSORMETADATA_deviceID, did)) {
								isPartialPass1 = true;
							}
						}
					}

					List<Document> sensorReports = sensorElement.getList("sensorReport", Document.class);
					if (sensorReports == null || sensorReports.isEmpty())
						continue;
					for (Document sensorReport : sensorReports) {
						String did2 = sensorReport.getString("deviceID");
						if (did2 == null)
							continue;
						if (EQ_deviceID != null && isPassString(EQ_deviceID, did2)) {
							isPartialPass2 = true;
							break;
						} else if (EQ_SENSORREPORT_deviceID != null && isPassString(EQ_SENSORREPORT_deviceID, did2)) {
							isPartialPass2 = true;
							break;
						}
					}
				}
				if (EQ_deviceID != null && (!isPartialPass1 && !isPartialPass2))
					return false;
				if (EQ_SENSORMETADATA_deviceID != null && !isPartialPass1)
					return false;
				if (EQ_SENSORREPORT_deviceID != null && !isPartialPass2)
					return false;
			}

			if (EQ_SENSORMETADATA_deviceMetadata != null) {
				List<Document> sensorElementList = doc.getList("sensorElementList", Document.class);
				if (sensorElementList == null || sensorElementList.isEmpty())
					return false;

				boolean isPartialPass = false;
				for (Document sensorElement : sensorElementList) {
					Document sensorMetadata = sensorElement.get("sensorMetadata", Document.class);
					if (sensorMetadata != null) {
						String dmd = sensorMetadata.getString("deviceMetadata");
						if (dmd == null)
							continue;
						if (isPassString(EQ_SENSORMETADATA_deviceMetadata, dmd)) {
							isPartialPass = true;
							break;
						}
					}
				}
				if (!isPartialPass)
					return false;
			}

			if (EQ_SENSORREPORT_deviceMetadata != null) {
				List<Document> sensorElementList = doc.getList("sensorElementList", Document.class);
				if (sensorElementList == null || sensorElementList.isEmpty())
					return false;

				boolean isPartialPass = false;
				for (Document sensorElement : sensorElementList) {
					List<Document> sensorReports = sensorElement.getList("sensorReport", Document.class);
					if (sensorReports == null || sensorReports.isEmpty())
						continue;
					for (Document sensorReport : sensorReports) {
						String dmd = sensorReport.getString("deviceMetadata");
						if (dmd == null)
							continue;
						if (isPassString(EQ_SENSORREPORT_deviceMetadata, dmd)) {
							isPartialPass = true;
							break;
						}
					}
				}
				if (!isPartialPass)
					return false;
			}

			if (EQ_SENSORMETADATA_rawData != null) {
				List<Document> sensorElementList = doc.getList("sensorElementList", Document.class);
				if (sensorElementList == null || sensorElementList.isEmpty())
					return false;

				boolean isPartialPass = false;
				for (Document sensorElement : sensorElementList) {
					Document sensorMetadata = sensorElement.get("sensorMetadata", Document.class);
					if (sensorMetadata != null) {
						String rd = sensorMetadata.getString("rawData");
						if (rd == null)
							continue;
						if (isPassString(EQ_SENSORMETADATA_rawData, rd)) {
							isPartialPass = true;
							break;
						}
					}
				}
				if (!isPartialPass)
					return false;
			}

			if (EQ_SENSORREPORT_rawData != null) {
				List<Document> sensorElementList = doc.getList("sensorElementList", Document.class);
				if (sensorElementList == null || sensorElementList.isEmpty())
					return false;

				boolean isPartialPass = false;
				for (Document sensorElement : sensorElementList) {
					List<Document> sensorReports = sensorElement.getList("sensorReport", Document.class);
					if (sensorReports == null || sensorReports.isEmpty())
						continue;
					for (Document sensorReport : sensorReports) {
						String rd = sensorReport.getString("rawData");
						if (rd == null)
							continue;
						if (isPassString(EQ_SENSORREPORT_rawData, rd)) {
							isPartialPass = true;
							break;
						}
					}
				}
				if (!isPartialPass)
					return false;
			}

			if (EQ_dataProcessingMethod != null || EQ_SENSORMETADATA_dataProcessingMethod != null
					|| EQ_SENSORREPORT_dataProcessingMethod != null) {
				List<Document> sensorElementList = doc.getList("sensorElementList", Document.class);
				if (sensorElementList == null || sensorElementList.isEmpty())
					return false;

				boolean isPartialPass1 = false;
				boolean isPartialPass2 = false;
				for (Document sensorElement : sensorElementList) {
					Document sensorMetadata = sensorElement.get("sensorMetadata", Document.class);
					if (sensorMetadata != null) {
						String dpm = sensorMetadata.getString("dataProcessingMethod");
						if (dpm != null) {
							if (EQ_dataProcessingMethod != null && isPassString(EQ_dataProcessingMethod, dpm)) {
								isPartialPass1 = true;
							} else if (EQ_SENSORMETADATA_dataProcessingMethod != null
									&& isPassString(EQ_SENSORMETADATA_dataProcessingMethod, dpm)) {
								isPartialPass1 = true;
							}
						}
					}

					List<Document> sensorReports = sensorElement.getList("sensorReport", Document.class);
					if (sensorReports == null || sensorReports.isEmpty())
						continue;
					for (Document sensorReport : sensorReports) {
						String dpm2 = sensorReport.getString("dataProcessingMethod");
						if (dpm2 == null)
							continue;
						if (EQ_dataProcessingMethod != null && isPassString(EQ_dataProcessingMethod, dpm2)) {
							isPartialPass2 = true;
							break;
						} else if (EQ_SENSORREPORT_dataProcessingMethod != null
								&& isPassString(EQ_SENSORREPORT_dataProcessingMethod, dpm2)) {
							isPartialPass2 = true;
							break;
						}
					}
				}
				if (EQ_dataProcessingMethod != null && (!isPartialPass1 && !isPartialPass2))
					return false;
				if (EQ_SENSORMETADATA_dataProcessingMethod != null && !isPartialPass1)
					return false;
				if (EQ_SENSORREPORT_dataProcessingMethod != null && !isPartialPass2)
					return false;
			}

			if (EQ_microorganism != null) {
				List<Document> sensorElementList = doc.getList("sensorElementList", Document.class);
				if (sensorElementList == null || sensorElementList.isEmpty())
					return false;

				boolean isPartialPass = false;
				for (Document sensorElement : sensorElementList) {
					List<Document> sensorReports = sensorElement.getList("sensorReport", Document.class);
					if (sensorReports == null || sensorReports.isEmpty())
						continue;
					for (Document sensorReport : sensorReports) {
						String mo = sensorReport.getString("microorganism");
						if (mo == null)
							continue;
						if (isPassString(EQ_microorganism, mo)) {
							isPartialPass = true;
							break;
						}
					}
				}
				if (!isPartialPass)
					return false;
			}

			if (EQ_chemicalSubstance != null) {
				List<Document> sensorElementList = doc.getList("sensorElementList", Document.class);
				if (sensorElementList == null || sensorElementList.isEmpty())
					return false;

				boolean isPartialPass = false;
				for (Document sensorElement : sensorElementList) {
					List<Document> sensorReports = sensorElement.getList("sensorReport", Document.class);
					if (sensorReports == null || sensorReports.isEmpty())
						continue;
					for (Document sensorReport : sensorReports) {
						String cs = sensorReport.getString("chemicalSubstance");
						if (cs == null)
							continue;
						if (isPassString(EQ_chemicalSubstance, cs)) {
							isPartialPass = true;
							break;
						}
					}
				}
				if (!isPartialPass)
					return false;
			}

			if (EQ_bizRules != null) {
				List<Document> sensorElementList = doc.getList("sensorElementList", Document.class);
				if (sensorElementList == null || sensorElementList.isEmpty())
					return false;

				boolean isPartialPass = false;
				for (Document sensorElement : sensorElementList) {
					Document sensorMetadata = sensorElement.get("sensorMetadata", Document.class);
					if (sensorMetadata != null) {
						String br = sensorMetadata.getString("bizRules");
						if (br != null) {
							if (isPassString(EQ_bizRules, br)) {
								isPartialPass = true;
								break;
							}
						}
					}
				}
				if (!isPartialPass)
					return false;
			}

			if (EQ_stringValue != null) {
				List<Document> sensorElementList = doc.getList("sensorElementList", Document.class);
				if (sensorElementList == null || sensorElementList.isEmpty())
					return false;

				boolean isPartialPass = false;
				for (Document sensorElement : sensorElementList) {
					List<Document> sensorReports = sensorElement.getList("sensorReport", Document.class);
					if (sensorReports == null || sensorReports.isEmpty())
						continue;
					for (Document sensorReport : sensorReports) {
						String s = sensorReport.getString("stringValue");
						if (s == null)
							continue;
						if (isPassString(EQ_stringValue, s)) {
							isPartialPass = true;
							break;
						}
					}
				}
				if (!isPartialPass)
					return false;
			}

			if (EQ_booleanValue != null) {
				List<Document> sensorElementList = doc.getList("sensorElementList", Document.class);
				if (sensorElementList == null || sensorElementList.isEmpty())
					return false;

				boolean isPartialPass = false;
				for (Document sensorElement : sensorElementList) {
					List<Document> sensorReports = sensorElement.getList("sensorReport", Document.class);
					if (sensorReports == null || sensorReports.isEmpty())
						continue;
					for (Document sensorReport : sensorReports) {
						Boolean s = sensorReport.getBoolean("booleanValue");
						if (s == null)
							continue;
						if (isPassBoolean(EQ_booleanValue, s)) {
							isPartialPass = true;
							break;
						}
					}
				}
				if (!isPartialPass)
					return false;
			}
			
			if (EQ_hexBinaryValue != null) {
				List<Document> sensorElementList = doc.getList("sensorElementList", Document.class);
				if (sensorElementList == null || sensorElementList.isEmpty())
					return false;

				boolean isPartialPass = false;
				for (Document sensorElement : sensorElementList) {
					List<Document> sensorReports = sensorElement.getList("sensorReport", Document.class);
					if (sensorReports == null || sensorReports.isEmpty())
						continue;
					for (Document sensorReport : sensorReports) {
						// TODO: 
						Object obj = sensorReport.get("hexBinaryValue", Binary.class);
						if(obj == null)
							continue;
						byte[] hbv = (byte[]) obj;
						if (isPassString(EQ_hexBinaryValue, hbv.toString())) {
							isPartialPass = true;
							break;
						}

					}
				}
				if (!isPartialPass)
					return false;
			}
			
			
			// hexBinaryValue
			// uriValue
			// percRank
			// bizTransaction

		} catch (Exception e) {
			return false;
		}

		return true;
	}

	public String getSubscriptionID() {
		return subscriptionID;
	}

	public void setSubscriptionID(String subscriptionID) {
		this.subscriptionID = subscriptionID;
	}

	@SuppressWarnings("unchecked")
	public TriggerDescription(Subscribe subscribe, SOAPQueryUnmarshaller unmarshaller)
			throws QueryParameterException, ImplementationException, SubscribeNotPermittedException {
		this.unmarshaller = unmarshaller;

		this.subscriptionID = subscribe.getSubscriptionID();

		if (subscribe.getQueryName().equals("SimpleMasterDataQuery")) {
			SubscribeNotPermittedException e = new SubscribeNotPermittedException(
					"The specified query name may not be used with subscribe, only with poll.");
			throw e;
		}

		List<QueryParam> paramList = subscribe.getParams().getParam();
		convertQueryParams(paramList);

		// TODO:
		for (QueryParam param : paramList) {
			String name = param.getName();
			Object value = param.getValue();

			if (name.equals("eventType"))
				eventType = (List<String>) value;

			if (name.equals("GE_eventTime"))
				GE_eventTime = (long) value;

			if (name.equals("LT_eventTime"))
				LT_eventTime = (long) value;

			if (name.equals("GE_recordTime"))
				GE_recordTime = (long) value;

			if (name.equals("LT_recordTime"))
				LT_recordTime = (long) value;

			if (name.equals("EQ_action"))
				EQ_action = (List<String>) value;

			if (name.equals("EQ_bizStep"))
				EQ_bizStep = (List<String>) value;

			if (name.equals("EQ_disposition"))
				EQ_disposition = (List<String>) value;

			if (name.equals("EQ_persistentDisposition_set"))
				EQ_persistentDisposition_set = (List<String>) value;

			if (name.equals("EQ_persistentDisposition_unset"))
				EQ_persistentDisposition_unset = (List<String>) value;

			if (name.equals("EQ_readPoint"))
				EQ_readPoint = (List<String>) value;

			if (name.equals("EQ_bizLocation"))
				EQ_bizLocation = (List<String>) value;

			if (name.equals("EQ_transformationID"))
				EQ_transformationID = (List<String>) value;

			if (name.equals("EQ_eventID"))
				EQ_eventID = (List<String>) value;

			if (name.equals("EXISTS_errorDeclaration"))
				EXISTS_errorDeclaration = (VoidHolder) value;

			if (name.equals("GE_errorDeclarationTime"))
				GE_errorDeclarationTime = (long) value;

			if (name.equals("LT_errorDeclarationTime"))
				LT_errorDeclarationTime = (long) value;

			if (name.equals("EQ_errorReason"))
				EQ_errorReason = (List<String>) value;

			if (name.equals("EQ_correctiveEventID"))
				EQ_correctiveEventID = (List<String>) value;

			if (name.equals("MATCH_epc"))
				MATCH_epc = (List<String>) value;
			if (name.equals("MATCH_parentID"))
				MATCH_parentID = (List<String>) value;
			if (name.equals("MATCH_inputEPC"))
				MATCH_inputEPC = (List<String>) value;
			if (name.equals("MATCH_outputEPC"))
				MATCH_outputEPC = (List<String>) value;
			if (name.equals("MATCH_anyEPC"))
				MATCH_anyEPC = (List<String>) value;
			if (name.equals("GE_startTime"))
				GE_startTime = (long) value;
			if (name.equals("LT_startTime"))
				LT_startTime = (long) value;
			if (name.equals("GE_endTime"))
				GE_endTime = (long) value;
			if (name.equals("LT_endTime"))
				LT_endTime = (long) value;

			if (name.equals("GE_SENSORMETADATA_time"))
				GE_SENSORMETADATA_time = (long) value;
			if (name.equals("LT_SENSORMETADATA_time"))
				LT_SENSORMETADATA_time = (long) value;

			if (name.equals("GE_SENSORREPORT_time"))
				GE_SENSORREPORT_time = (long) value;
			if (name.equals("LT_SENSORREPORT_time"))
				LT_SENSORREPORT_time = (long) value;
			if (name.equals("EQ_deviceID"))
				EQ_deviceID = (List<String>) value;
			if (name.equals("EQ_SENSORMETADATA_deviceID"))
				EQ_SENSORMETADATA_deviceID = (List<String>) value;
			if (name.equals("EQ_SENSORREPORT_deviceID"))
				EQ_SENSORREPORT_deviceID = (List<String>) value;
			if (name.equals("EQ_SENSORMETADATA_deviceMetadata"))
				EQ_SENSORMETADATA_deviceMetadata = (List<String>) value;
			if (name.equals("EQ_SENSORREPORT_deviceMetadata"))
				EQ_SENSORREPORT_deviceMetadata = (List<String>) value;
			if (name.equals("EQ_SENSORMETADATA_rawData"))
				EQ_SENSORMETADATA_rawData = (List<String>) value;
			if (name.equals("EQ_SENSORREPORT_rawData"))
				EQ_SENSORREPORT_rawData = (List<String>) value;

			if (name.equals("EQ_dataProcessingMethod"))
				EQ_dataProcessingMethod = (List<String>) value;
			if (name.equals("EQ_SENSORMETADATA_dataProcessingMethod"))
				EQ_SENSORMETADATA_dataProcessingMethod = (List<String>) value;
			if (name.equals("EQ_SENSORREPORT_dataProcessingMethod"))
				EQ_SENSORREPORT_dataProcessingMethod = (List<String>) value;

			if (name.equals("EQ_microorganism"))
				EQ_microorganism = (List<String>) value;

			if (name.equals("EQ_chemicalSubstance"))
				EQ_chemicalSubstance = (List<String>) value;

			if (name.equals("EQ_bizRules"))
				EQ_bizRules = (List<String>) value;

			if (name.equals("EQ_stringValue"))
				EQ_stringValue = (List<String>) value;
			
			if (name.equals("EQ_booleanValue"))
				EQ_booleanValue = (Boolean) value;
			
			if (name.equals("EQ_hexBinaryValue"))
				EQ_hexBinaryValue = (List<String>) value;
		}
	}

	// TODO:
	public Document getMongoQueryParameter() {
		Document doc = new Document();
		if (eventType != null) {
			doc.put("eventType", eventType);
		}
		if (GE_eventTime != null) {
			doc.put("GE_eventTime", GE_eventTime);
		}
		if (LT_eventTime != null) {
			doc.put("LT_eventTime", LT_eventTime);
		}
		if (GE_recordTime != null) {
			doc.put("GE_recordTime", GE_recordTime);
		}
		if (LT_recordTime != null) {
			doc.put("LT_recordTime", LT_recordTime);
		}

		if (EQ_action != null) {
			doc.put("EQ_action", EQ_action);
		}

		if (EQ_bizStep != null) {
			doc.put("EQ_bizStep", EQ_bizStep);
		}

		if (EQ_disposition != null) {
			doc.put("EQ_disposition", EQ_disposition);
		}

		if (EQ_persistentDisposition_set != null) {
			doc.put("EQ_persistentDisposition_set", EQ_persistentDisposition_set);
		}

		if (EQ_persistentDisposition_unset != null) {
			doc.put("EQ_persistentDisposition_unset", EQ_persistentDisposition_unset);
		}

		if (EQ_readPoint != null) {
			doc.put("EQ_readPoint", EQ_readPoint);
		}

		if (EQ_bizLocation != null) {
			doc.put("EQ_bizLocation", EQ_bizLocation);
		}

		if (EQ_transformationID != null) {
			doc.put("EQ_transformationID", EQ_transformationID);
		}

		if (EQ_eventID != null) {
			doc.put("EQ_eventID", EQ_eventID);
		}

		if (EXISTS_errorDeclaration != null) {
			doc.put("EXISTS_errorDeclaration", "VoidHolder");
		}

		if (GE_errorDeclarationTime != null) {
			doc.put("GE_errorDeclarationTime", GE_errorDeclarationTime);
		}
		if (LT_errorDeclarationTime != null) {
			doc.put("LT_errorDeclarationTime", LT_errorDeclarationTime);
		}

		if (EQ_errorReason != null) {
			doc.put("EQ_errorReason", EQ_errorReason);
		}

		if (EQ_correctiveEventID != null) {
			doc.put("EQ_correctiveEventID", EQ_correctiveEventID);
		}

		if (MATCH_epc != null) {
			doc.put("MATCH_epc", MATCH_epc);
		}

		if (MATCH_parentID != null) {
			doc.put("MATCH_parentID", MATCH_parentID);
		}

		if (MATCH_inputEPC != null) {
			doc.put("MATCH_inputEPC", MATCH_inputEPC);
		}

		if (MATCH_outputEPC != null) {
			doc.put("MATCH_outputEPC", MATCH_outputEPC);
		}

		if (MATCH_anyEPC != null) {
			doc.put("MATCH_anyEPC", MATCH_anyEPC);
		}

		if (GE_startTime != null) {
			doc.put("GE_startTime", GE_startTime);
		}
		if (LT_startTime != null) {
			doc.put("LT_startTime", LT_startTime);
		}

		if (GE_endTime != null) {
			doc.put("GE_endTime", GE_endTime);
		}
		if (LT_endTime != null) {
			doc.put("LT_endTime", LT_endTime);
		}

		if (GE_SENSORMETADATA_time != null) {
			doc.put("GE_SENSORMETADATA_time", GE_SENSORMETADATA_time);
		}
		if (LT_SENSORMETADATA_time != null) {
			doc.put("LT_SENSORMETADATA_time", LT_SENSORMETADATA_time);
		}

		if (GE_SENSORREPORT_time != null) {
			doc.put("GE_SENSORREPORT_time", GE_SENSORREPORT_time);
		}
		if (LT_SENSORREPORT_time != null) {
			doc.put("LT_SENSORREPORT_time", LT_SENSORREPORT_time);
		}

		if (EQ_deviceID != null) {
			doc.put("EQ_deviceID", EQ_deviceID);
		}

		if (EQ_SENSORMETADATA_deviceID != null) {
			doc.put("EQ_SENSORMETADATA_deviceID", EQ_SENSORMETADATA_deviceID);
		}

		if (EQ_SENSORREPORT_deviceID != null) {
			doc.put("EQ_SENSORREPORT_deviceID", EQ_SENSORREPORT_deviceID);
		}

		if (EQ_SENSORMETADATA_deviceMetadata != null) {
			doc.put("EQ_SENSORMETADATA_deviceMetadata", EQ_SENSORMETADATA_deviceMetadata);
		}

		if (EQ_SENSORREPORT_deviceMetadata != null) {
			doc.put("EQ_SENSORREPORT_deviceMetadata", EQ_SENSORREPORT_deviceMetadata);
		}

		if (EQ_SENSORMETADATA_rawData != null) {
			doc.put("EQ_SENSORMETADATA_rawData", EQ_SENSORMETADATA_rawData);
		}

		if (EQ_SENSORREPORT_rawData != null) {
			doc.put("EQ_SENSORREPORT_rawData", EQ_SENSORREPORT_rawData);
		}

		if (EQ_dataProcessingMethod != null) {
			doc.put("EQ_dataProcessingMethod", EQ_dataProcessingMethod);
		}

		if (EQ_SENSORMETADATA_dataProcessingMethod != null) {
			doc.put("EQ_SENSORMETADATA_dataProcessingMethod", EQ_SENSORMETADATA_dataProcessingMethod);
		}

		if (EQ_SENSORREPORT_dataProcessingMethod != null) {
			doc.put("EQ_SENSORREPORT_dataProcessingMethod", EQ_SENSORREPORT_dataProcessingMethod);
		}

		if (EQ_microorganism != null) {
			doc.put("EQ_microorganism", EQ_microorganism);
		}

		if (EQ_chemicalSubstance != null) {
			doc.put("EQ_chemicalSubstance", EQ_chemicalSubstance);
		}

		if (EQ_bizRules != null) {
			doc.put("EQ_bizRules", EQ_bizRules);
		}

		if (EQ_stringValue != null) {
			doc.put("EQ_stringValue", EQ_stringValue);
		}
		
		if (EQ_booleanValue != null) {
			doc.put("EQ_booleanValue", EQ_booleanValue);
		}
		
		if (EQ_hexBinaryValue != null) {
			doc.put("EQ_hexBinaryValue", EQ_hexBinaryValue);
		}

		return doc;
	}

	private void convertQueryParams(List<QueryParam> paramList) throws QueryParameterException {
		for (QueryParam param : paramList) {
			Object value = param.getValue();
			if (value instanceof Element) {
				// ArrayOfString
				// DateTimeStamp
				// VoidHolder
				Element element = (Element) value;
				String attribute = element.getAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "type");
				if (attribute.contains(":ArrayOfString")) {
					param.setValue(fromArrayOfString(value));
				} else if (attribute.contains(":DateTimeStamp")) {
					param.setValue(fromDateTimeStamp(value));
				} else if (attribute.contains(":VoidHolder")) {
					param.setValue(fromVoidHolder(value));
				}
			} else if (value instanceof Double) {
				continue;
			} else if (value instanceof String) {
				continue;
			} else if (value instanceof Boolean) {
				continue;
			} else if (value instanceof Integer) {
				continue;
			} else {
				throw new QueryParameterException(
						"value of SOAP Query Parameter should be one of ArrayOfString, DateTimeStamp, VoidHolder, Double, String, Boolean, Integer");
			}
		}
	}

	private List<String> fromArrayOfString(Object value) throws QueryParameterException {
		try {
			return unmarshaller.getArrayOfString((Element) value).getString();
		} catch (Exception e1) {
			QueryParameterException e = new QueryParameterException(
					"the value of a parameter is of the wrong type or out of range: value should be"
							+ ArrayOfString.class + "if given");
			throw e;
		}
	}

	private VoidHolder fromVoidHolder(Object value) throws QueryParameterException {
		try {
			return unmarshaller.getVoidHolder((Element) value);
		} catch (Exception e1) {
			QueryParameterException e = new QueryParameterException(
					"the value of a parameter is of the wrong type or out of range: value should be" + VoidHolder.class
							+ "if given");
			throw e;
		}
	}

	private long fromDateTimeStamp(Object value) throws QueryParameterException {
		try {
			return TimeUtil.toUnixEpoch(((Element) value).getTextContent());
		} catch (ParseException | NullPointerException e) {
			throw new QueryParameterException(e.getMessage());
		}
	}
}

class Entry<V> {
	private String key;
	private V value;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}

	public Entry(String key, V value) {
		super();
		this.key = key;
		this.value = value;
	}

}
