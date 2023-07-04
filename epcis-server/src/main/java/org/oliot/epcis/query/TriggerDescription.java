package org.oliot.epcis.query;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

import org.bson.Document;
import org.bson.types.Binary;
import org.oliot.epcis.converter.data.pojo_to_bson.POJOtoBSONUtil;
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
	private Double GE_percRank;
	private Double LT_percRank;

	private HashMap<String, List<String>> EQ_bizTransaction;
	private HashMap<String, List<String>> EQ_source;
	private HashMap<String, List<String>> EQ_destination;

	private HashMap<String, Double> EQ_quantity;
	private HashMap<String, Double> GT_quantity;
	private HashMap<String, Double> GE_quantity;
	private HashMap<String, Double> LT_quantity;
	private HashMap<String, Double> LE_quantity;

	private HashMap<String, Object> EQ_INNER_ILMD;
	private HashMap<String, Object> GT_INNER_ILMD;
	private HashMap<String, Object> GE_INNER_ILMD;
	private HashMap<String, Object> LT_INNER_ILMD;
	private HashMap<String, Object> LE_INNER_ILMD;
	private List<String> EXISTS_INNER_ILMD;

	private HashMap<String, Object> EQ_INNER_SENSORELEMENT;
	private HashMap<String, Object> GT_INNER_SENSORELEMENT;
	private HashMap<String, Object> GE_INNER_SENSORELEMENT;
	private HashMap<String, Object> LT_INNER_SENSORELEMENT;
	private HashMap<String, Object> LE_INNER_SENSORELEMENT;
	private List<String> EXISTS_INNER_SENSORELEMENT;

	private HashMap<String, Object> EQ_INNER_readPoint;
	private HashMap<String, Object> GT_INNER_readPoint;
	private HashMap<String, Object> GE_INNER_readPoint;
	private HashMap<String, Object> LT_INNER_readPoint;
	private HashMap<String, Object> LE_INNER_readPoint;
	private List<String> EXISTS_INNER_readPoint;

	private HashMap<String, Object> EQ_INNER_bizLocation;
	private HashMap<String, Object> GT_INNER_bizLocation;
	private HashMap<String, Object> GE_INNER_bizLocation;
	private HashMap<String, Object> LT_INNER_bizLocation;
	private HashMap<String, Object> LE_INNER_bizLocation;
	private List<String> EXISTS_INNER_bizLocation;

	private HashMap<String, Object> EQ_INNER_ERROR_DECLARATION;
	private HashMap<String, Object> GT_INNER_ERROR_DECLARATION;
	private HashMap<String, Object> GE_INNER_ERROR_DECLARATION;
	private HashMap<String, Object> LT_INNER_ERROR_DECLARATION;
	private HashMap<String, Object> LE_INNER_ERROR_DECLARATION;
	private List<String> EXISTS_INNER_ERROR_DECLARATION;

	private HashMap<String, Object> EQ_INNER;
	private HashMap<String, Object> GT_INNER;
	private HashMap<String, Object> GE_INNER;
	private HashMap<String, Object> LT_INNER;
	private HashMap<String, Object> LE_INNER;
	private List<String> EXISTS_INNER;

	private HashMap<String, Object> EQ_ILMD;
	private HashMap<String, Object> GT_ILMD;
	private HashMap<String, Object> GE_ILMD;
	private HashMap<String, Object> LT_ILMD;
	private HashMap<String, Object> LE_ILMD;
	private List<String> EXISTS_ILMD;

	private HashMap<String, Object> EQ_SENSORELEMENT;
	private HashMap<String, Object> GT_SENSORELEMENT;
	private HashMap<String, Object> GE_SENSORELEMENT;
	private HashMap<String, Object> LT_SENSORELEMENT;
	private HashMap<String, Object> LE_SENSORELEMENT;
	private String EXISTS_SENSORELEMENT;
	private HashMap<String, Object> EQ_SENSORMETADATA;
	private HashMap<String, Object> EQ_SENSORREPORT;
	private String EXISTS_SENSORMETADATA;
	private String EXISTS_SENSORREPORT;

	private HashMap<String, Object> EQ_readPoint_extension;
	private HashMap<String, Object> GT_readPoint_extension;
	private HashMap<String, Object> GE_readPoint_extension;
	private HashMap<String, Object> LT_readPoint_extension;
	private HashMap<String, Object> LE_readPoint_extension;
	private String EXISTS_readPoint_extension;

	private HashMap<String, Object> EQ_bizLocation_extension;
	private HashMap<String, Object> GT_bizLocation_extension;
	private HashMap<String, Object> GE_bizLocation_extension;
	private HashMap<String, Object> LT_bizLocation_extension;
	private HashMap<String, Object> LE_bizLocation_extension;
	private String EXISTS_bizLocation_extension;

	private HashMap<String, Object> EQ_ERROR_DECLARATION_extension;
	private HashMap<String, Object> GT_ERROR_DECLARATION_extension;
	private HashMap<String, Object> GE_ERROR_DECLARATION_extension;
	private HashMap<String, Object> LT_ERROR_DECLARATION_extension;
	private HashMap<String, Object> LE_ERROR_DECLARATION_extension;
	private String EXISTS_ERROR_DECLARATION_extension;

	private HashMap<String, Object> EQ_extension;
	private HashMap<String, Object> GT_extension;
	private HashMap<String, Object> GE_extension;
	private HashMap<String, Object> LT_extension;
	private HashMap<String, Object> LE_extension;
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
	private HashMap<String, Object> HASATTR;
	private HashMap<String, Object> EQ_ATTR;

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
		if (query.equals(value))
			return true;
		return false;
	}

	public boolean isPassListOfString(List<String> query, List<String> value) {
		if (Collections.disjoint(query, value))
			return false;
		return true;
	}

	public boolean isPassMap(HashMap<String, List<String>> query, String type, String value) {
		if (!query.containsKey(type))
			return false;
		else {
			return query.get(type).contains(value);
		}
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

	public boolean isPassDouble(Double ge, Double lt, Double value) {

		if (ge != null && ge < value)
			return false;

		if (lt != null && lt >= value)
			return false;
		return true;

	}

	public boolean isPassDouble(HashMap<String, Double> eq, HashMap<String, Double> gt, HashMap<String, Double> ge,
			HashMap<String, Double> lt, HashMap<String, Double> le, String uom, Double value) {

		if (eq != null) {
			Double tVal = eq.get(uom);
			if (tVal == null)
				return false;
			if (tVal.doubleValue() != value.doubleValue())
				return false;
		}
		if (gt != null) {
			Double tVal = gt.get(uom);
			if (tVal == null)
				return false;
			if (tVal.doubleValue() >= value.doubleValue())
				return false;
		}
		if (ge != null) {
			Double tVal = ge.get(uom);
			if (tVal == null)
				return false;
			if (tVal.doubleValue() > value.doubleValue())
				return false;
		}

		if (lt != null) {
			Double tVal = lt.get(uom);
			if (tVal == null)
				return false;
			if (tVal.doubleValue() <= value.doubleValue())
				return false;
		}

		if (le != null) {
			Double tVal = le.get(uom);
			if (tVal == null)
				return false;
			if (tVal.doubleValue() < value.doubleValue())
				return false;
		}

		return true;
	}

	public boolean isPassDocument(HashMap<String, Object> eq, HashMap<String, Object> gt, HashMap<String, Object> ge,
			HashMap<String, Object> lt, HashMap<String, Object> le, List<String> exists, Document ext) {
		if (eq != null) {
			for (Entry<String, Object> ent : eq.entrySet()) {
				String tk = ent.getKey();
				Object tv = ent.getValue();
				if (!ext.containsKey(tk))
					return false;
				Object v = ext.get(tk);
				if (tv instanceof Integer) {
					if (!(v instanceof Integer))
						return false;
					if (((Integer) tv).intValue() != ((Integer) v).intValue())
						return false;
				} else if (tv instanceof Long) {
					if (!(v instanceof Long))
						return false;
					if (((Long) tv).longValue() != ((Long) v).longValue())
						return false;
				} else if (tv instanceof Double) {
					if (!(v instanceof Double))
						return false;
					if (((Double) tv).doubleValue() != ((Double) v).doubleValue())
						return false;
				} else if (tv instanceof List) {
					if (!((List<?>) tv).contains(v))
						return false;
				} else
					return false;
			}
		}

		if (gt != null) {
			for (Entry<String, Object> ent : gt.entrySet()) {
				String tk = ent.getKey();
				Object tv = ent.getValue();
				if (!ext.containsKey(tk))
					return false;
				Object v = ext.get(tk);
				if (tv instanceof Integer) {
					if (!(v instanceof Integer))
						return false;
					if (((Integer) tv).intValue() >= ((Integer) v).intValue())
						return false;
				} else if (tv instanceof Long) {
					if (!(v instanceof Long))
						return false;
					if (((Long) tv).longValue() >= ((Long) v).longValue())
						return false;
				} else if (tv instanceof Double) {
					if (!(v instanceof Double))
						return false;
					if (((Double) tv).doubleValue() >= ((Double) v).doubleValue())
						return false;
				} else
					return false;
			}
		}

		if (ge != null) {
			for (Entry<String, Object> ent : ge.entrySet()) {
				String tk = ent.getKey();
				Object tv = ent.getValue();
				if (!ext.containsKey(tk))
					return false;
				Object v = ext.get(tk);
				if (tv instanceof Integer) {
					if (!(v instanceof Integer))
						return false;
					if (((Integer) tv).intValue() > ((Integer) v).intValue())
						return false;
				} else if (tv instanceof Long) {
					if (!(v instanceof Long))
						return false;
					if (((Long) tv).longValue() > ((Long) v).longValue())
						return false;
				} else if (tv instanceof Double) {
					if (!(v instanceof Double))
						return false;
					if (((Double) tv).doubleValue() > ((Double) v).doubleValue())
						return false;
				} else
					return false;
			}
		}

		if (lt != null) {
			for (Entry<String, Object> ent : lt.entrySet()) {
				String tk = ent.getKey();
				Object tv = ent.getValue();
				if (!ext.containsKey(tk))
					return false;
				Object v = ext.get(tk);
				if (tv instanceof Integer) {
					if (!(v instanceof Integer))
						return false;
					if (((Integer) tv).intValue() <= ((Integer) v).intValue())
						return false;
				} else if (tv instanceof Long) {
					if (!(v instanceof Long))
						return false;
					if (((Long) tv).longValue() <= ((Long) v).longValue())
						return false;
				} else if (tv instanceof Double) {
					if (!(v instanceof Double))
						return false;
					if (((Double) tv).doubleValue() <= ((Double) v).doubleValue())
						return false;
				} else
					return false;
			}
		}

		if (le != null) {
			for (Entry<String, Object> ent : le.entrySet()) {
				String tk = ent.getKey();
				Object tv = ent.getValue();
				if (!ext.containsKey(tk))
					return false;
				Object v = ext.get(tk);
				if (tv instanceof Integer) {
					if (!(v instanceof Integer))
						return false;
					if (((Integer) tv).intValue() < ((Integer) v).intValue())
						return false;
				} else if (tv instanceof Long) {
					if (!(v instanceof Long))
						return false;
					if (((Long) tv).longValue() < ((Long) v).longValue())
						return false;
				} else if (tv instanceof Double) {
					if (!(v instanceof Double))
						return false;
					if (((Double) tv).doubleValue() < ((Double) v).doubleValue())
						return false;
				} else
					return false;
			}
		}

		if (exists != null) {
			for (String exist : exists) {
				if (!ext.containsKey(exist))
					return false;
			}
		}
		return true;
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
						Object obj = sensorReport.get("hexBinaryValue");
						if (obj == null)
							continue;
						String strHex = DatatypeConverter.printHexBinary((byte[]) obj);
						if (isPassString(EQ_hexBinaryValue, strHex)) {
							isPartialPass = true;
							break;
						}
					}
				}
				if (!isPartialPass)
					return false;
			}

			if (EQ_uriValue != null) {
				List<Document> sensorElementList = doc.getList("sensorElementList", Document.class);
				if (sensorElementList == null || sensorElementList.isEmpty())
					return false;

				boolean isPartialPass = false;
				for (Document sensorElement : sensorElementList) {
					List<Document> sensorReports = sensorElement.getList("sensorReport", Document.class);
					if (sensorReports == null || sensorReports.isEmpty())
						continue;
					for (Document sensorReport : sensorReports) {
						String s = sensorReport.getString("uriValue");
						if (s == null)
							continue;
						if (isPassString(EQ_uriValue, s)) {
							isPartialPass = true;
							break;
						}
					}
				}
				if (!isPartialPass)
					return false;
			}

			if (GE_percRank != null || LT_percRank != null) {
				List<Document> sensorElementList = doc.getList("sensorElementList", Document.class);
				if (sensorElementList == null || sensorElementList.isEmpty())
					return false;

				boolean isPartialPass = false;
				for (Document sensorElement : sensorElementList) {
					List<Document> sensorReports = sensorElement.getList("sensorReport", Document.class);
					if (sensorReports == null || sensorReports.isEmpty())
						continue;
					for (Document sensorReport : sensorReports) {
						Double time = sensorReport.getDouble("percRank");
						if (time == null)
							continue;
						if (isPassDouble(GE_percRank, LT_percRank, time)) {
							isPartialPass = true;
							break;
						}
					}
				}
				if (!isPartialPass)
					return false;
			}

			if (EQ_bizTransaction != null && !EQ_bizTransaction.isEmpty()) {
				List<Document> bizTransactionList = doc.getList("bizTransactionList", Document.class);
				if (bizTransactionList == null || bizTransactionList.isEmpty())
					return false;
				boolean isPass = false;
				for (Document bizTransaction : bizTransactionList) {
					String type = bizTransaction.getString("type");
					if (type == null)
						type = "";
					String value = bizTransaction.getString("value");
					if (isPassMap(EQ_bizTransaction, type, value)) {
						isPass = true;
						break;
					}
				}
				if (!isPass)
					return false;
			}

			if (EQ_source != null && !EQ_source.isEmpty()) {
				List<Document> sourceList = doc.getList("sourceList", Document.class);
				if (sourceList == null || sourceList.isEmpty())
					return false;
				boolean isPass = false;
				for (Document source : sourceList) {
					String type = source.getString("type");
					if (type == null)
						type = "";
					String value = source.getString("value");
					if (isPassMap(EQ_source, type, value)) {
						isPass = true;
						break;
					}
				}
				if (!isPass)
					return false;
			}

			if (EQ_destination != null && !EQ_destination.isEmpty()) {
				List<Document> destinationList = doc.getList("destinationList", Document.class);
				if (destinationList == null || destinationList.isEmpty())
					return false;
				boolean isPass = false;
				for (Document destination : destinationList) {
					String type = destination.getString("type");
					if (type == null)
						type = "";
					String value = destination.getString("value");
					if (isPassMap(EQ_destination, type, value)) {
						isPass = true;
						break;
					}
				}
				if (!isPass)
					return false;
			}

			if ((EQ_quantity != null && !EQ_quantity.isEmpty()) || (GT_quantity != null && !GT_quantity.isEmpty())
					|| (GE_quantity != null && !GE_quantity.isEmpty())
					|| (LT_quantity != null && !LT_quantity.isEmpty())
					|| (LE_quantity != null && !LE_quantity.isEmpty())) {

				boolean isPass1 = false;
				List<Document> quantityList = doc.getList("quantityList", Document.class);
				if (quantityList != null) {
					for (Document quantity : quantityList) {
						String uom = quantity.getString("uom");
						if (uom == null)
							uom = "";
						Double value = quantity.getDouble("quantity");
						if (isPassDouble(EQ_quantity, GT_quantity, GE_quantity, LT_quantity, LE_quantity, uom, value)) {
							isPass1 = true;
							break;
						}
					}
				}

				boolean isPass2 = false;
				List<Document> inputQuantityList = doc.getList("inputQuantityList", Document.class);
				if (inputQuantityList != null) {
					for (Document quantity : inputQuantityList) {
						String uom = quantity.getString("uom");
						if (uom == null)
							uom = "";
						Double value = quantity.getDouble("quantity");
						if (isPassDouble(EQ_quantity, GT_quantity, GE_quantity, LT_quantity, LE_quantity, uom, value)) {
							isPass2 = true;
							break;
						}
					}
				}

				boolean isPass3 = false;
				List<Document> outputQuantityList = doc.getList("outputQuantityList", Document.class);
				if (outputQuantityList != null) {
					for (Document quantity : outputQuantityList) {
						String uom = quantity.getString("uom");
						if (uom == null)
							uom = "";
						Double value = quantity.getDouble("quantity");
						if (isPassDouble(EQ_quantity, GT_quantity, GE_quantity, LT_quantity, LE_quantity, uom, value)) {
							isPass3 = true;
							break;
						}
					}
				}

				if (isPass1 == false && isPass2 == false && isPass3 == false)
					return false;
			}

			if ((EQ_INNER_ILMD != null && !EQ_INNER_ILMD.isEmpty())
					|| (GT_INNER_ILMD != null && !GT_INNER_ILMD.isEmpty())
					|| (GE_INNER_ILMD != null && !GE_INNER_ILMD.isEmpty())
					|| (LT_INNER_ILMD != null && !LT_INNER_ILMD.isEmpty())
					|| (LE_INNER_ILMD != null && !LE_INNER_ILMD.isEmpty())
					|| (EXISTS_INNER_ILMD != null && !EXISTS_INNER_ILMD.isEmpty())) {
				Document ilmdf = doc.get("ilmdf", Document.class);
				if (!isPassDocument(EQ_INNER_ILMD, GT_INNER_ILMD, GE_INNER_ILMD, LT_INNER_ILMD, LE_INNER_ILMD,
						EXISTS_INNER_ILMD, ilmdf))
					return false;
			}

			if ((EQ_INNER_SENSORELEMENT != null && !EQ_INNER_SENSORELEMENT.isEmpty())
					|| (GT_INNER_SENSORELEMENT != null && !GT_INNER_SENSORELEMENT.isEmpty())
					|| (GE_INNER_SENSORELEMENT != null && !GE_INNER_SENSORELEMENT.isEmpty())
					|| (LT_INNER_SENSORELEMENT != null && !LT_INNER_SENSORELEMENT.isEmpty())
					|| (LE_INNER_SENSORELEMENT != null && !LE_INNER_SENSORELEMENT.isEmpty())
					|| (EXISTS_INNER_SENSORELEMENT != null && !EXISTS_INNER_SENSORELEMENT.isEmpty())) {

				List<Document> sensorElementList = doc.getList("sensorElementList", Document.class);
				if (sensorElementList == null || sensorElementList.isEmpty())
					return false;
				boolean isPass = false;
				for (Document sensorElement : sensorElementList) {
					Document sef = sensorElement.get("sef", Document.class);
					if (isPassDocument(EQ_INNER_SENSORELEMENT, GT_INNER_SENSORELEMENT, GE_INNER_SENSORELEMENT, LT_INNER_SENSORELEMENT, LE_INNER_SENSORELEMENT,
							EXISTS_INNER_SENSORELEMENT, sef)) {
						isPass = true;
						break;
					}
				}
				if (!isPass)
					return false;
			}

			// TODO

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

			if (name.equals("EQ_uriValue"))
				EQ_uriValue = (List<String>) value;

			if (name.equals("GE_percRank"))
				GE_percRank = (double) value;
			if (name.equals("LT_percRank"))
				LT_percRank = (double) value;

			if (name.startsWith("EQ_bizTransaction")) {
				if (EQ_bizTransaction == null)
					EQ_bizTransaction = new HashMap<String, List<String>>();
				String type = name.substring(18);
				List<String> values = EQ_bizTransaction.get(type);
				if (values == null)
					values = new ArrayList<String>();
				values.addAll((List<String>) value);
				EQ_bizTransaction.put(type, values);
			}

			if (name.startsWith("EQ_source")) {
				if (EQ_source == null)
					EQ_source = new HashMap<String, List<String>>();
				String type = name.substring(10);
				List<String> values = EQ_source.get(type);
				if (values == null)
					values = new ArrayList<String>();
				values.addAll((List<String>) value);
				EQ_source.put(type, values);
			}

			if (name.startsWith("EQ_destination")) {
				if (EQ_destination == null)
					EQ_destination = new HashMap<String, List<String>>();
				String type = name.substring(15);
				List<String> values = EQ_destination.get(type);
				if (values == null)
					values = new ArrayList<String>();
				values.addAll((List<String>) value);
				EQ_destination.put(type, values);
			}

			if (name.startsWith("EQ_quantity")) {
				if (EQ_quantity == null)
					EQ_quantity = new HashMap<String, Double>();
				String uom = name.substring(12);

				EQ_quantity.put(uom, (double) value);
			}

			if (name.startsWith("GT_quantity")) {
				if (GT_quantity == null)
					GT_quantity = new HashMap<String, Double>();
				String uom = name.substring(12);

				GT_quantity.put(uom, (double) value);
			}

			if (name.startsWith("GE_quantity")) {
				if (GE_quantity == null)
					GE_quantity = new HashMap<String, Double>();
				String uom = name.substring(12);

				GE_quantity.put(uom, (double) value);
			}

			if (name.startsWith("LT_quantity")) {
				if (LT_quantity == null)
					LT_quantity = new HashMap<String, Double>();
				String uom = name.substring(12);

				LT_quantity.put(uom, (double) value);
			}

			if (name.startsWith("LE_quantity")) {
				if (LE_quantity == null)
					LE_quantity = new HashMap<String, Double>();
				String uom = name.substring(12);

				LE_quantity.put(uom, (double) value);
			}

			if (name.startsWith("EQ_INNER_ILMD")) {
				if (EQ_INNER_ILMD == null)
					EQ_INNER_ILMD = new HashMap<String, Object>();
				String key = name.substring(14);
				EQ_INNER_ILMD.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
			}

			if (name.startsWith("GT_INNER_ILMD")) {
				if (GT_INNER_ILMD == null)
					GT_INNER_ILMD = new HashMap<String, Object>();
				String key = name.substring(14);
				GT_INNER_ILMD.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
			}

			if (name.startsWith("GE_INNER_ILMD")) {
				if (GE_INNER_ILMD == null)
					GE_INNER_ILMD = new HashMap<String, Object>();
				String key = name.substring(14);
				GE_INNER_ILMD.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
			}

			if (name.startsWith("LT_INNER_ILMD")) {
				if (LT_INNER_ILMD == null)
					LT_INNER_ILMD = new HashMap<String, Object>();
				String key = name.substring(14);
				LT_INNER_ILMD.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
			}

			if (name.startsWith("LE_INNER_ILMD")) {
				if (LE_INNER_ILMD == null)
					LE_INNER_ILMD = new HashMap<String, Object>();
				String key = name.substring(14);
				LE_INNER_ILMD.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
			}

			if (name.startsWith("EXISTS_INNER_ILMD")) {
				if (EXISTS_INNER_ILMD == null)
					EXISTS_INNER_ILMD = new ArrayList<String>();
				EXISTS_INNER_ILMD.add(POJOtoBSONUtil.encodeMongoObjectKey(name.substring(18)));
			}

			if (name.startsWith("EQ_INNER_SENSORELEMENT")) {
				if (EQ_INNER_SENSORELEMENT == null)
					EQ_INNER_SENSORELEMENT = new HashMap<String, Object>();
				String key = name.substring(23);
				EQ_INNER_SENSORELEMENT.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
			}

			if (name.startsWith("GT_INNER_SENSORELEMENT")) {
				if (GT_INNER_SENSORELEMENT == null)
					GT_INNER_SENSORELEMENT = new HashMap<String, Object>();
				String key = name.substring(23);
				GT_INNER_SENSORELEMENT.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
			}
			if (name.startsWith("GE_INNER_SENSORELEMENT")) {
				if (GE_INNER_SENSORELEMENT == null)
					GE_INNER_SENSORELEMENT = new HashMap<String, Object>();
				String key = name.substring(23);
				GE_INNER_SENSORELEMENT.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
			}
			if (name.startsWith("LT_INNER_SENSORELEMENT")) {
				if (LT_INNER_SENSORELEMENT == null)
					LT_INNER_SENSORELEMENT = new HashMap<String, Object>();
				String key = name.substring(23);
				LT_INNER_SENSORELEMENT.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
			}
			if (name.startsWith("LE_INNER_SENSORELEMENT")) {
				if (LE_INNER_SENSORELEMENT == null)
					LE_INNER_SENSORELEMENT = new HashMap<String, Object>();
				String key = name.substring(23);
				LE_INNER_SENSORELEMENT.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
			}

			if (name.startsWith("EXISTS_INNER_SENSORELEMENT")) {
				if (EXISTS_INNER_SENSORELEMENT == null)
					EXISTS_INNER_SENSORELEMENT = new ArrayList<String>();
				EXISTS_INNER_SENSORELEMENT.add(POJOtoBSONUtil.encodeMongoObjectKey(name.substring(27)));
			}
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

		if (EQ_uriValue != null) {
			doc.put("EQ_uriValue", EQ_uriValue);
		}

		if (GE_percRank != null) {
			doc.put("GE_percRank", GE_percRank);
		}

		if (LT_percRank != null) {
			doc.put("LT_percRank", LT_percRank);
		}

		if (EQ_bizTransaction != null) {
			doc.put("EQ_bizTransaction", EQ_bizTransaction);
		}

		if (EQ_source != null) {
			doc.put("EQ_source", EQ_source);
		}

		if (EQ_destination != null) {
			doc.put("EQ_destination", EQ_destination);
		}

		if (EQ_quantity != null) {
			doc.put("EQ_quantity", EQ_quantity);
		}

		if (GT_quantity != null) {
			doc.put("GT_quantity", GT_quantity);
		}

		if (GE_quantity != null) {
			doc.put("GE_quantity", GE_quantity);
		}

		if (LT_quantity != null) {
			doc.put("LT_quantity", LT_quantity);
		}

		if (LE_quantity != null) {
			doc.put("LE_quantity", LE_quantity);
		}

		if (EQ_INNER_ILMD != null) {
			doc.put("EQ_INNER_ILMD", EQ_INNER_ILMD);
		}

		if (GT_INNER_ILMD != null) {
			doc.put("GT_INNER_ILMD", GT_INNER_ILMD);
		}

		if (GE_INNER_ILMD != null) {
			doc.put("GE_INNER_ILMD", GE_INNER_ILMD);
		}

		if (LT_INNER_ILMD != null) {
			doc.put("LT_INNER_ILMD", LT_INNER_ILMD);
		}

		if (LE_INNER_ILMD != null) {
			doc.put("LE_INNER_ILMD", LE_INNER_ILMD);
		}

		if (EXISTS_INNER_ILMD != null) {
			doc.put("EXISTS_INNER_ILMD", EXISTS_INNER_ILMD);
		}
		
		if (EQ_INNER_SENSORELEMENT != null) {
			doc.put("EQ_INNER_SENSORELEMENT", EQ_INNER_SENSORELEMENT);
		}

		if (GT_INNER_SENSORELEMENT != null) {
			doc.put("GT_INNER_SENSORELEMENT", GT_INNER_SENSORELEMENT);
		}

		if (GE_INNER_SENSORELEMENT != null) {
			doc.put("GE_INNER_SENSORELEMENT", GE_INNER_SENSORELEMENT);
		}

		if (LT_INNER_SENSORELEMENT != null) {
			doc.put("LT_INNER_SENSORELEMENT", LT_INNER_SENSORELEMENT);
		}

		if (LE_INNER_SENSORELEMENT != null) {
			doc.put("LE_INNER_SENSORELEMENT", LE_INNER_SENSORELEMENT);
		}

		if (EXISTS_INNER_SENSORELEMENT != null) {
			doc.put("EXISTS_INNER_SENSORELEMENT", EXISTS_INNER_SENSORELEMENT);
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
