package org.oliot.epcis.query;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

import org.bson.Document;
import org.bson.types.Binary;
import org.oliot.epcis.converter.data.pojo_to_bson.POJOtoBSONUtil;
import org.oliot.epcis.model.ArrayOfString;
import org.oliot.epcis.model.ImplementationException;
import org.oliot.epcis.model.ImplementationExceptionSeverity;
import org.oliot.epcis.model.QueryParam;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.model.Subscribe;
import org.oliot.epcis.model.SubscribeNotPermittedException;
import org.oliot.epcis.model.ValidationException;
import org.oliot.epcis.model.VoidHolder;
import org.oliot.epcis.resource.Resource;
import org.oliot.epcis.server.EPCISServer;
import org.oliot.epcis.util.TimeUtil;
import org.oliot.epcis.validation.IdentifierValidator;
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
	private List<String> EXISTS_SENSORELEMENT;
	private HashMap<String, List<String>> EQ_SENSORMETADATA;
	private HashMap<String, List<String>> EQ_SENSORREPORT;
	private VoidHolder EXISTS_SENSORMETADATA;
	private VoidHolder EXISTS_SENSORREPORT;

	private HashMap<String, Object> EQ_readPoint_extension;
	private HashMap<String, Object> GT_readPoint_extension;
	private HashMap<String, Object> GE_readPoint_extension;
	private HashMap<String, Object> LT_readPoint_extension;
	private HashMap<String, Object> LE_readPoint_extension;
	private List<String> EXISTS_readPoint_extension;

	private HashMap<String, Object> EQ_bizLocation_extension;
	private HashMap<String, Object> GT_bizLocation_extension;
	private HashMap<String, Object> GE_bizLocation_extension;
	private HashMap<String, Object> LT_bizLocation_extension;
	private HashMap<String, Object> LE_bizLocation_extension;
	private List<String> EXISTS_bizLocation_extension;

	private HashMap<String, Object> EQ_ERROR_DECLARATION_extension;
	private HashMap<String, Object> GT_ERROR_DECLARATION_extension;
	private HashMap<String, Object> GE_ERROR_DECLARATION_extension;
	private HashMap<String, Object> LT_ERROR_DECLARATION_extension;
	private HashMap<String, Object> LE_ERROR_DECLARATION_extension;
	private List<String> EXISTS_ERROR_DECLARATION_extension;

	private HashMap<String, Object> EQ_extension;
	private HashMap<String, Object> GT_extension;
	private HashMap<String, Object> GE_extension;
	private HashMap<String, Object> LT_extension;
	private HashMap<String, Object> LE_extension;
	private List<String> EXISTS_extension;

	private List<String> EQ_type;
	private SensorUomValue EQ_value;
	private SensorUomValue GT_value;
	private SensorUomValue GE_value;
	private SensorUomValue LT_value;
	private SensorUomValue LE_value;

	private SensorUomValue EQ_minValue;
	private SensorUomValue GT_minValue;
	private SensorUomValue GE_minValue;
	private SensorUomValue LT_minValue;
	private SensorUomValue LE_minValue;

	private SensorUomValue EQ_maxValue;
	private SensorUomValue GT_maxValue;
	private SensorUomValue GE_maxValue;
	private SensorUomValue LT_maxValue;
	private SensorUomValue LE_maxValue;

	private SensorUomValue EQ_meanValue;
	private SensorUomValue GT_meanValue;
	private SensorUomValue GE_meanValue;
	private SensorUomValue LT_meanValue;
	private SensorUomValue LE_meanValue;

	private SensorUomValue EQ_sDev;
	private SensorUomValue GT_sDev;
	private SensorUomValue GE_sDev;
	private SensorUomValue LT_sDev;
	private SensorUomValue LE_sDev;

	private SensorUomValue EQ_percValue;
	private SensorUomValue GT_percValue;
	private SensorUomValue GE_percValue;
	private SensorUomValue LT_percValue;
	private SensorUomValue LE_percValue;

	private List<String> WD_readPoint;
	private List<String> WD_bizLocation;
	private HashMap<String, List<String>> HASATTR;
	private HashMap<String, List<String>> EQ_ATTR;

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

	public boolean isPassMatchString(List<String> query, String value) {
		for (String q : query) {
			if (q.contains("*")) {
				q = q.replaceAll("\\.", "[.]");
				q = q.replaceAll("\\*", "(.)*");
			}

			if (Pattern.matches(q, value)) {
				return true;
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

	public boolean isPassDouble(SensorUomValue eq, SensorUomValue gt, SensorUomValue ge, SensorUomValue lt,
			SensorUomValue le, String uom, Double value) {

		if (eq != null) {
			if (!eq.getUom().equals(uom) || (eq.getValue().doubleValue() != value.doubleValue())) {
				return false;
			}
		}
		if (gt != null) {
			if (!gt.getUom().equals(uom) || (gt.getValue().doubleValue() >= value.doubleValue())) {
				return false;
			}
		}
		if (ge != null) {
			if (!ge.getUom().equals(uom) || (ge.getValue().doubleValue() > value.doubleValue())) {
				return false;
			}
		}

		if (lt != null) {
			if (!lt.getUom().equals(uom) || (lt.getValue().doubleValue() <= value.doubleValue())) {
				return false;
			}
		}

		if (le != null) {
			if (!le.getUom().equals(uom) || (le.getValue().doubleValue() < value.doubleValue())) {
				return false;
			}
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

			if (MATCH_epcClass != null) {
				boolean isPass = false;
				List<Document> ecl = doc.getList("quantityList", Document.class);
				if (ecl == null)
					return false;
				for (Document ec : ecl) {
					if (isPassMatchString(MATCH_epcClass, ec.getString("epcClass"))) {
						isPass = true;
						break;
					}
				}
				if (!isPass)
					return false;
			}

			if (MATCH_inputEPCClass != null) {
				boolean isPass = false;
				List<Document> ecl = doc.getList("inputQuantityList", Document.class);
				if (ecl == null)
					return false;
				for (Document ec : ecl) {
					if (isPassMatchString(MATCH_inputEPCClass, ec.getString("epcClass"))) {
						isPass = true;
						break;
					}
				}
				if (!isPass)
					return false;
			}

			if (MATCH_outputEPCClass != null) {
				boolean isPass = false;
				List<Document> ecl = doc.getList("outputQuantityList", Document.class);
				if (ecl == null)
					return false;
				for (Document ec : ecl) {
					if (isPassMatchString(MATCH_outputEPCClass, ec.getString("epcClass"))) {
						isPass = true;
						break;
					}
				}
				if (!isPass)
					return false;
			}

			if (MATCH_anyEPCClass != null) {
				boolean isPass1 = false;
				List<Document> ecl = doc.getList("quantityList", Document.class);
				if (ecl != null) {
					for (Document ec : ecl) {
						if (isPassMatchString(MATCH_anyEPCClass, ec.getString("epcClass"))) {
							isPass1 = true;
							break;
						}
					}
				}

				boolean isPass2 = false;
				List<Document> ecl2 = doc.getList("inputQuantityList", Document.class);
				if (ecl2 != null) {
					for (Document ec : ecl2) {
						if (isPassMatchString(MATCH_anyEPCClass, ec.getString("epcClass"))) {
							isPass2 = true;
							break;
						}
					}
				}

				boolean isPass3 = false;
				List<Document> ecl3 = doc.getList("outputQuantityList", Document.class);
				if (ecl3 != null) {
					for (Document ec : ecl3) {
						if (isPassMatchString(MATCH_anyEPCClass, ec.getString("epcClass"))) {
							isPass3 = true;
							break;
						}
					}
				}

				if (!isPass1 && !isPass2 && !isPass3)
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
					if (isPassDocument(EQ_INNER_SENSORELEMENT, GT_INNER_SENSORELEMENT, GE_INNER_SENSORELEMENT,
							LT_INNER_SENSORELEMENT, LE_INNER_SENSORELEMENT, EXISTS_INNER_SENSORELEMENT, sef)) {
						isPass = true;
						break;
					}
				}
				if (!isPass)
					return false;
			}

			if ((EQ_INNER_readPoint != null && !EQ_INNER_readPoint.isEmpty())
					|| (GT_INNER_readPoint != null && !GT_INNER_readPoint.isEmpty())
					|| (GE_INNER_readPoint != null && !GE_INNER_readPoint.isEmpty())
					|| (LT_INNER_readPoint != null && !LT_INNER_readPoint.isEmpty())
					|| (LE_INNER_readPoint != null && !LE_INNER_readPoint.isEmpty())
					|| (EXISTS_INNER_readPoint != null && !EXISTS_INNER_readPoint.isEmpty())) {
				Document rpf = doc.get("rpf", Document.class);
				if (!isPassDocument(EQ_INNER_readPoint, GT_INNER_readPoint, GE_INNER_readPoint, LT_INNER_readPoint,
						LE_INNER_readPoint, EXISTS_INNER_readPoint, rpf))
					return false;
			}

			if ((EQ_INNER_bizLocation != null && !EQ_INNER_bizLocation.isEmpty())
					|| (GT_INNER_bizLocation != null && !GT_INNER_bizLocation.isEmpty())
					|| (GE_INNER_bizLocation != null && !GE_INNER_bizLocation.isEmpty())
					|| (LT_INNER_bizLocation != null && !LT_INNER_bizLocation.isEmpty())
					|| (LE_INNER_bizLocation != null && !LE_INNER_bizLocation.isEmpty())
					|| (EXISTS_INNER_bizLocation != null && !EXISTS_INNER_bizLocation.isEmpty())) {
				Document blf = doc.get("blf", Document.class);
				if (!isPassDocument(EQ_INNER_bizLocation, GT_INNER_bizLocation, GE_INNER_bizLocation,
						LT_INNER_bizLocation, LE_INNER_bizLocation, EXISTS_INNER_bizLocation, blf))
					return false;
			}

			if ((EQ_INNER_ERROR_DECLARATION != null && !EQ_INNER_ERROR_DECLARATION.isEmpty())
					|| (GT_INNER_ERROR_DECLARATION != null && !GT_INNER_ERROR_DECLARATION.isEmpty())
					|| (GE_INNER_ERROR_DECLARATION != null && !GE_INNER_ERROR_DECLARATION.isEmpty())
					|| (LT_INNER_ERROR_DECLARATION != null && !LT_INNER_ERROR_DECLARATION.isEmpty())
					|| (LE_INNER_ERROR_DECLARATION != null && !LE_INNER_ERROR_DECLARATION.isEmpty())
					|| (EXISTS_INNER_ERROR_DECLARATION != null && !EXISTS_INNER_ERROR_DECLARATION.isEmpty())) {
				Document errf = doc.get("errf", Document.class);
				if (!isPassDocument(EQ_INNER_ERROR_DECLARATION, GT_INNER_ERROR_DECLARATION, GE_INNER_ERROR_DECLARATION,
						LT_INNER_ERROR_DECLARATION, LE_INNER_ERROR_DECLARATION, EXISTS_INNER_ERROR_DECLARATION, errf))
					return false;
			}

			if ((EQ_INNER != null && !EQ_INNER.isEmpty()) || (GT_INNER != null && !GT_INNER.isEmpty())
					|| (GE_INNER != null && !GE_INNER.isEmpty()) || (LT_INNER != null && !LT_INNER.isEmpty())
					|| (LE_INNER != null && !LE_INNER.isEmpty()) || (EXISTS_INNER != null && !EXISTS_INNER.isEmpty())) {
				Document extf = doc.get("extf", Document.class);
				if (!isPassDocument(EQ_INNER, GT_INNER, GE_INNER, LT_INNER, LE_INNER, EXISTS_INNER, extf))
					return false;
			}

			if ((EQ_ILMD != null && !EQ_ILMD.isEmpty()) || (GT_ILMD != null && !GT_ILMD.isEmpty())
					|| (GE_ILMD != null && !GE_ILMD.isEmpty()) || (LT_ILMD != null && !LT_ILMD.isEmpty())
					|| (LE_ILMD != null && !LE_ILMD.isEmpty()) || (EXISTS_ILMD != null && !EXISTS_ILMD.isEmpty())) {
				Document ilmdf = doc.get("ilmd", Document.class);
				if (!isPassDocument(EQ_ILMD, GT_ILMD, GE_ILMD, LT_ILMD, LE_ILMD, EXISTS_ILMD, ilmdf))
					return false;
			}

			if ((EQ_SENSORELEMENT != null && !EQ_SENSORELEMENT.isEmpty())
					|| (GT_SENSORELEMENT != null && !GT_SENSORELEMENT.isEmpty())
					|| (GE_SENSORELEMENT != null && !GE_SENSORELEMENT.isEmpty())
					|| (LT_SENSORELEMENT != null && !LT_SENSORELEMENT.isEmpty())
					|| (LE_SENSORELEMENT != null && !LE_SENSORELEMENT.isEmpty())
					|| (EXISTS_SENSORELEMENT != null && !EXISTS_SENSORELEMENT.isEmpty())) {

				List<Document> sensorElementList = doc.getList("sensorElementList", Document.class);
				if (sensorElementList == null || sensorElementList.isEmpty())
					return false;
				boolean isPass = false;
				for (Document sensorElement : sensorElementList) {
					Document ext = sensorElement.get("extension", Document.class);
					if (isPassDocument(EQ_SENSORELEMENT, GT_SENSORELEMENT, GE_SENSORELEMENT, LT_SENSORELEMENT,
							LE_SENSORELEMENT, EXISTS_SENSORELEMENT, ext)) {
						isPass = true;
						break;
					}
				}
				if (!isPass)
					return false;
			}

			if ((EQ_SENSORMETADATA != null && !EQ_SENSORMETADATA.isEmpty())
					|| (EQ_SENSORREPORT != null && !EQ_SENSORREPORT.isEmpty())) {
				List<Document> sensorElementList = doc.getList("sensorElementList", Document.class);
				if (sensorElementList == null || sensorElementList.isEmpty())
					return false;

				boolean isPass1 = false;
				boolean isPass2 = false;
				for (Document sensorElement : sensorElementList) {
					isPass1 = false;
					isPass2 = false;
					Document sm = sensorElement.get("sensorMetadata", Document.class);
					if (sm == null || sm.isEmpty())
						continue;
					Document oa = sm.get("otherAttributes", Document.class);
					if (oa == null || oa.isEmpty())
						continue;
					for (String key : oa.keySet()) {
						if (isPassMap(EQ_SENSORMETADATA, key, oa.getString(key))) {
							isPass1 = true;
							break;
						}
					}

					List<Document> srl = sensorElement.getList("sensorReport", Document.class);
					for (Document sr : srl) {
						Document oa2 = sr.get("otherAttributes", Document.class);
						if (oa2 == null || oa2.isEmpty())
							continue;
						for (String key : oa2.keySet()) {
							if (isPassMap(EQ_SENSORREPORT, key, oa2.getString(key))) {
								isPass2 = true;
								break;
							}
						}
						if (isPass2 == true)
							break;
					}
					if (isPass1 == true && isPass2 == true)
						break;
				}

				if (isPass1 == false || isPass2 == false)
					return false;
			}

			if (EXISTS_SENSORMETADATA != null) {
				List<Document> sensorElementList = doc.getList("sensorElementList", Document.class);
				if (sensorElementList == null || sensorElementList.isEmpty())
					return false;
				boolean isPass = false;
				for (Document sensorElement : sensorElementList) {
					Document sm = sensorElement.get("sensorMetadata", Document.class);
					if (sm != null && !sm.isEmpty()) {
						isPass = true;
						break;
					}
					if (isPass)
						break;
				}
				if (!isPass)
					return false;

			}

			if (EXISTS_SENSORREPORT != null) {
				List<Document> sensorElementList = doc.getList("sensorElementList", Document.class);
				if (sensorElementList == null || sensorElementList.isEmpty())
					return false;

				boolean isPass = false;
				for (Document sensorElement : sensorElementList) {

					List<Document> sensorReportList = sensorElement.getList("sensorReport", Document.class);
					if (sensorReportList != null && !sensorReportList.isEmpty()) {
						isPass = true;
						break;
					}
				}
				if (!isPass)
					return false;
			}

			if ((EQ_readPoint_extension != null && !EQ_readPoint_extension.isEmpty())
					|| (GT_readPoint_extension != null && !GT_readPoint_extension.isEmpty())
					|| (GE_readPoint_extension != null && !GE_readPoint_extension.isEmpty())
					|| (LT_readPoint_extension != null && !LT_readPoint_extension.isEmpty())
					|| (LE_readPoint_extension != null && !LE_readPoint_extension.isEmpty())
					|| (EXISTS_readPoint_extension != null && !EXISTS_readPoint_extension.isEmpty())) {
				Document rpe = doc.get("readPointExt", Document.class);
				if (!isPassDocument(EQ_readPoint_extension, GT_readPoint_extension, GE_readPoint_extension,
						LT_readPoint_extension, LE_readPoint_extension, EXISTS_readPoint_extension, rpe))
					return false;
			}

			if ((EQ_bizLocation_extension != null && !EQ_bizLocation_extension.isEmpty())
					|| (GT_bizLocation_extension != null && !GT_bizLocation_extension.isEmpty())
					|| (GE_bizLocation_extension != null && !GE_bizLocation_extension.isEmpty())
					|| (LT_bizLocation_extension != null && !LT_bizLocation_extension.isEmpty())
					|| (LE_bizLocation_extension != null && !LE_bizLocation_extension.isEmpty())
					|| (EXISTS_bizLocation_extension != null && !EXISTS_bizLocation_extension.isEmpty())) {
				Document ble = doc.get("bizLocationExt", Document.class);
				if (!isPassDocument(EQ_bizLocation_extension, GT_bizLocation_extension, GE_bizLocation_extension,
						LT_bizLocation_extension, LE_bizLocation_extension, EXISTS_bizLocation_extension, ble))
					return false;
			}

			if ((EQ_ERROR_DECLARATION_extension != null && !EQ_ERROR_DECLARATION_extension.isEmpty())
					|| (GT_ERROR_DECLARATION_extension != null && !GT_ERROR_DECLARATION_extension.isEmpty())
					|| (GE_ERROR_DECLARATION_extension != null && !GE_ERROR_DECLARATION_extension.isEmpty())
					|| (LT_ERROR_DECLARATION_extension != null && !LT_ERROR_DECLARATION_extension.isEmpty())
					|| (LE_ERROR_DECLARATION_extension != null && !LE_ERROR_DECLARATION_extension.isEmpty())
					|| (EXISTS_ERROR_DECLARATION_extension != null && !EXISTS_ERROR_DECLARATION_extension.isEmpty())) {
				Document ed = doc.get("errorDeclaration", Document.class);
				if (ed == null)
					return false;
				Document errExt = ed.get("extension", Document.class);
				if (errExt == null)
					return false;

				if (!isPassDocument(EQ_ERROR_DECLARATION_extension, GT_ERROR_DECLARATION_extension,
						GE_ERROR_DECLARATION_extension, LT_ERROR_DECLARATION_extension, LE_ERROR_DECLARATION_extension,
						EXISTS_ERROR_DECLARATION_extension, errExt))
					return false;
			}

			if ((EQ_extension != null && !EQ_extension.isEmpty()) || (GT_extension != null && !GT_extension.isEmpty())
					|| (GE_extension != null && !GE_extension.isEmpty())
					|| (LT_extension != null && !LT_extension.isEmpty())
					|| (LE_extension != null && !LE_extension.isEmpty())
					|| (EXISTS_extension != null && !EXISTS_extension.isEmpty())) {
				Document ext = doc.get("extension", Document.class);
				if (!isPassDocument(EQ_extension, GT_extension, GE_extension, LT_extension, LE_extension,
						EXISTS_extension, ext))
					return false;
			}

			if (EQ_type != null && !EQ_type.isEmpty()) {
				List<Document> sensorElementList = doc.getList("sensorElementList", Document.class);
				if (sensorElementList == null || sensorElementList.isEmpty())
					return false;

				boolean isPass = false;
				for (Document sensorElement : sensorElementList) {
					List<Document> sensorReportList = sensorElement.getList("sensorReport", Document.class);
					for (Document sensorReport : sensorReportList) {
						String type = sensorReport.getString("type");
						if (isPassString(EQ_type, type)) {
							isPass = true;
							break;
						}
					}
					if (isPass == true)
						break;
				}
				if (!isPass)
					return false;
			}

			if (EQ_value != null || GT_value != null || GE_value != null || LT_value != null || LE_value != null) {
				List<Document> sensorElementList = doc.getList("sensorElementList", Document.class);
				if (sensorElementList == null || sensorElementList.isEmpty())
					return false;

				boolean isPass = false;
				for (Document sensorElement : sensorElementList) {
					List<Document> sensorReportList = sensorElement.getList("sensorReport", Document.class);
					for (Document sensorReport : sensorReportList) {
						String uom = sensorReport.getString("uom");
						Double value = sensorReport.getDouble("value");
						if (uom == null || value == null)
							continue;

						String type = Resource.unitConverter.getType(uom);
						String rUom = Resource.unitConverter.getRepresentativeUoMFromType(type);
						double rValue = Resource.unitConverter.getRepresentativeValue(type, uom, value);

						if (isPassDouble(EQ_value, GT_value, GE_value, LT_value, LE_value, rUom, rValue)) {
							isPass = true;
							break;
						}

					}
					if (isPass == true)
						break;
				}
				if (!isPass)
					return false;
			}

			if (EQ_minValue != null || GT_minValue != null || GE_minValue != null || LT_minValue != null
					|| LE_minValue != null) {
				List<Document> sensorElementList = doc.getList("sensorElementList", Document.class);
				if (sensorElementList == null || sensorElementList.isEmpty())
					return false;

				boolean isPass = false;
				for (Document sensorElement : sensorElementList) {
					List<Document> sensorReportList = sensorElement.getList("sensorReport", Document.class);
					for (Document sensorReport : sensorReportList) {
						String uom = sensorReport.getString("uom");
						Double value = sensorReport.getDouble("minValue");
						if (uom == null || value == null)
							continue;

						String type = Resource.unitConverter.getType(uom);
						String rUom = Resource.unitConverter.getRepresentativeUoMFromType(type);
						double rValue = Resource.unitConverter.getRepresentativeValue(type, uom, value);

						if (isPassDouble(EQ_minValue, GT_minValue, GE_minValue, LT_minValue, LE_minValue, rUom,
								rValue)) {
							isPass = true;
							break;
						}

					}
					if (isPass == true)
						break;
				}
				if (!isPass)
					return false;
			}

			if (EQ_maxValue != null || GT_maxValue != null || GE_maxValue != null || LT_maxValue != null
					|| LE_maxValue != null) {
				List<Document> sensorElementList = doc.getList("sensorElementList", Document.class);
				if (sensorElementList == null || sensorElementList.isEmpty())
					return false;

				boolean isPass = false;
				for (Document sensorElement : sensorElementList) {
					List<Document> sensorReportList = sensorElement.getList("sensorReport", Document.class);
					for (Document sensorReport : sensorReportList) {
						String uom = sensorReport.getString("uom");
						Double value = sensorReport.getDouble("maxValue");
						if (uom == null || value == null)
							continue;

						String type = Resource.unitConverter.getType(uom);
						String rUom = Resource.unitConverter.getRepresentativeUoMFromType(type);
						double rValue = Resource.unitConverter.getRepresentativeValue(type, uom, value);

						if (isPassDouble(EQ_maxValue, GT_maxValue, GE_maxValue, LT_maxValue, LE_maxValue, rUom,
								rValue)) {
							isPass = true;
							break;
						}

					}
					if (isPass == true)
						break;
				}
				if (!isPass)
					return false;
			}

			if (EQ_meanValue != null || GT_meanValue != null || GE_meanValue != null || LT_meanValue != null
					|| LE_meanValue != null) {
				List<Document> sensorElementList = doc.getList("sensorElementList", Document.class);
				if (sensorElementList == null || sensorElementList.isEmpty())
					return false;

				boolean isPass = false;
				for (Document sensorElement : sensorElementList) {
					List<Document> sensorReportList = sensorElement.getList("sensorReport", Document.class);
					for (Document sensorReport : sensorReportList) {
						String uom = sensorReport.getString("uom");
						Double value = sensorReport.getDouble("meanValue");
						if (uom == null || value == null)
							continue;

						String type = Resource.unitConverter.getType(uom);
						String rUom = Resource.unitConverter.getRepresentativeUoMFromType(type);
						double rValue = Resource.unitConverter.getRepresentativeValue(type, uom, value);

						if (isPassDouble(EQ_meanValue, GT_meanValue, GE_meanValue, LT_meanValue, LE_meanValue, rUom,
								rValue)) {
							isPass = true;
							break;
						}

					}
					if (isPass == true)
						break;
				}
				if (!isPass)
					return false;
			}

			if (EQ_sDev != null || GT_sDev != null || GE_sDev != null || LT_sDev != null || LE_sDev != null) {
				List<Document> sensorElementList = doc.getList("sensorElementList", Document.class);
				if (sensorElementList == null || sensorElementList.isEmpty())
					return false;

				boolean isPass = false;
				for (Document sensorElement : sensorElementList) {
					List<Document> sensorReportList = sensorElement.getList("sensorReport", Document.class);
					for (Document sensorReport : sensorReportList) {
						String uom = sensorReport.getString("uom");
						Double value = sensorReport.getDouble("sDev");
						if (uom == null || value == null)
							continue;

						String type = Resource.unitConverter.getType(uom);
						String rUom = Resource.unitConverter.getRepresentativeUoMFromType(type);
						double rValue = Resource.unitConverter.getRepresentativeValue(type, uom, value);

						if (isPassDouble(EQ_sDev, GT_sDev, GE_sDev, LT_sDev, LE_sDev, rUom, rValue)) {
							isPass = true;
							break;
						}

					}
					if (isPass == true)
						break;
				}
				if (!isPass)
					return false;
			}

			if (EQ_percValue != null || GT_percValue != null || GE_percValue != null || LT_percValue != null
					|| LE_percValue != null) {
				List<Document> sensorElementList = doc.getList("sensorElementList", Document.class);
				if (sensorElementList == null || sensorElementList.isEmpty())
					return false;

				boolean isPass = false;
				for (Document sensorElement : sensorElementList) {
					List<Document> sensorReportList = sensorElement.getList("sensorReport", Document.class);
					for (Document sensorReport : sensorReportList) {
						String uom = sensorReport.getString("uom");
						Double value = sensorReport.getDouble("percValue");
						if (uom == null || value == null)
							continue;

						String type = Resource.unitConverter.getType(uom);
						String rUom = Resource.unitConverter.getRepresentativeUoMFromType(type);
						double rValue = Resource.unitConverter.getRepresentativeValue(type, uom, value);

						if (isPassDouble(EQ_percValue, GT_percValue, GE_percValue, LT_percValue, LE_percValue, rUom,
								rValue)) {
							isPass = true;
							break;
						}

					}
					if (isPass == true)
						break;
				}
				if (!isPass)
					return false;
			}

			if (WD_readPoint != null) {

				Set<String> wdValues = new HashSet<String>();
				for (String v : WD_readPoint) {
					wdValues.add(v);
					List<org.bson.Document> rDocs = new ArrayList<org.bson.Document>();
					EPCISServer.mVocCollection.find(new org.bson.Document("id", v)).into(rDocs);
					for (org.bson.Document rDoc : rDocs) {
						if (rDoc.containsKey("children")) {
							List<String> children = rDoc.getList("children", String.class);
							wdValues.addAll(children);
						}
					}
				}

				List<String> wdList = new ArrayList<String>(wdValues);
				if (!isPassString(WD_readPoint, doc.getString("readPoint"))) {
					return false;
				}
			}

			if (WD_bizLocation != null) {

				Set<String> wdValues = new HashSet<String>();
				for (String v : WD_bizLocation) {
					wdValues.add(v);
					List<org.bson.Document> rDocs = new ArrayList<org.bson.Document>();
					EPCISServer.mVocCollection.find(new org.bson.Document("id", v)).into(rDocs);
					for (org.bson.Document rDoc : rDocs) {
						if (rDoc.containsKey("children")) {
							List<String> children = rDoc.getList("children", String.class);
							wdValues.addAll(children);
						}
					}
				}

				List<String> wdList = new ArrayList<String>(wdValues);
				if (!isPassString(WD_bizLocation, doc.getString("bizLocation"))) {
					return false;
				}
			}

			if (HASATTR != null) {

				for (Entry<String, List<String>> entry : HASATTR.entrySet()) {
					String fieldName = entry.getKey();
					List<String> mdValues = entry.getValue();
					HashSet<String> wdValues = new HashSet<String>();
					// wdValues.addAll(mdValues);
					String vtype;
					if (fieldName.equals("readPoint")) {
						vtype = "urn:epcglobal:epcis:vtype:ReadPoint";
					} else if (fieldName.equals("bizLocation")) {
						vtype = "urn:epcglobal:epcis:vtype:BusinessLocation";
					} else if (fieldName.equals("BusinessTransaction")) {
						vtype = "urn:epcglobal:epcis:vtype:BusinessTransaction";
					} else if (fieldName.equals("EPCClass")) {
						vtype = "urn:epcglobal:epcis:vtype:EPCClass";
					} else if (fieldName.equals("SourceDestID")) {
						vtype = "urn:epcglobal:epcis:vtype:SourceDest";
					} else if (fieldName.equals("LocationID")) {
						vtype = "urn:epcglobal:epcis:vtype:Location";
					} else if (fieldName.equals("PartyID")) {
						vtype = "urn:epcglobal:epcis:vtype:Party";
					} else if (fieldName.equals("MicroorganismID")) {
						vtype = "urn:epcglobal:epcis:vtype:Microorganism";
					} else if (fieldName.equals("ChemicalSubstanceID")) {
						vtype = "urn:epcglobal:epcis:vtype:ChemicalSubstance";
					} else if (fieldName.equals("ResourceID")) {
						vtype = "urn:epcglobal:epcis:vtype:Resource";
					} else {
						throw new QueryParameterException(fieldName
								+ " should be one of readPoint, bizLocation, BusinessTransaction, EPCClass, SourceDestID, LocationID, PartyID, MicroorganismID, ChemicalSubstanceID, ResourceID");
					}

					for (String v : mdValues) {
						Document first = null;
						try {
							first = EPCISServer.mVocCollection
									.find(new org.bson.Document("id", v).append("type", vtype)).first();
						} catch (Throwable e) {
							ImplementationException e1 = new ImplementationException(
									ImplementationExceptionSeverity.ERROR, null, null, e.getMessage());
							// throw e1;
						}

						if (first != null && first.getString("id") != null) {
							wdValues.add(first.getString("id"));
						}
					}

					if (fieldName.equals("readPoint")) {
						if (!isPassString(new ArrayList<String>(wdValues), doc.getString("readPoint"))) {
							return false;
						}
					} else if (fieldName.equals("bizLocation")) {
						if (!isPassString(new ArrayList<String>(wdValues), doc.getString("bizLocation"))) {
							return false;
						}
					} else if (fieldName.equals("BusinessTransaction")) {
						List<Document> bizTransactionList = doc.getList("bizTransactionList", Document.class);
						if (bizTransactionList == null || bizTransactionList.isEmpty())
							return false;
						boolean isPass = false;
						for (Document bizTransaction : bizTransactionList) {
							String value = bizTransaction.getString("value");
							if (isPassString(new ArrayList<String>(wdValues), value)) {
								isPass = true;
								break;
							}
						}
						if (!isPass)
							return false;
					} else if (fieldName.equals("EPCClass")) {

						boolean isPass1 = false;
						List<Document> ecl = doc.getList("quantityList", Document.class);
						if (ecl != null) {
							for (Document ec : ecl) {
								if (isPassString(new ArrayList<String>(wdValues), ec.getString("epcClass"))) {
									isPass1 = true;
									break;
								}
							}
						}

						boolean isPass2 = false;
						List<Document> ecl2 = doc.getList("inputQuantityList", Document.class);
						if (ecl2 != null) {
							for (Document ec : ecl2) {
								if (isPassString(new ArrayList<String>(wdValues), ec.getString("epcClass"))) {
									isPass2 = true;
									break;
								}
							}
						}

						boolean isPass3 = false;
						List<Document> ecl3 = doc.getList("outputQuantityList", Document.class);
						if (ecl3 != null) {
							for (Document ec : ecl3) {
								if (isPassString(new ArrayList<String>(wdValues), ec.getString("epcClass"))) {
									isPass3 = true;
									break;
								}
							}
						}

						if (!isPass1 && !isPass2 && !isPass3)
							return false;
					} else if (fieldName.equals("SourceDestID")) {
						boolean isPass1 = false;
						List<Document> sourceList = doc.getList("sourceList", Document.class);
						if (sourceList != null) {
							for (Document source : sourceList) {

								String value = source.getString("value");
								if (isPassString(new ArrayList<String>(wdValues), value)) {
									isPass1 = true;
									break;
								}
							}
						}
						boolean isPass2 = false;
						List<Document> destinationList = doc.getList("destinationList", Document.class);
						if (destinationList != null) {
							for (Document dest : destinationList) {

								String value = dest.getString("value");
								if (isPassString(new ArrayList<String>(wdValues), value)) {
									isPass2 = true;
									break;
								}
							}
						}

						if (!isPass1 && !isPass2)
							return false;

					} else if (fieldName.equals("LocationID")) {
						boolean isPass1 = false;
						boolean isPass2 = false;

						List<Document> sourceList = doc.getList("sourceList", Document.class);
						if (sourceList != null) {
							for (Document source : sourceList) {
								String type = source.getString("type");
								String value = source.getString("value");
								if (type != null && value != null && type.equals("urn:epcglobal:cbv:sdt:location")) {
									if (isPassString(new ArrayList<String>(wdValues), value)) {
										isPass1 = true;
										break;
									}
								}
							}
						}
						List<Document> destinationList = doc.getList("destinationList", Document.class);
						if (destinationList != null) {
							for (Document dest : destinationList) {
								String type = dest.getString("type");
								String value = dest.getString("value");
								if (type != null && value != null && type.equals("urn:epcglobal:cbv:sdt:location")) {
									if (isPassString(new ArrayList<String>(wdValues), value)) {
										isPass2 = true;
										break;
									}
								}
							}
						}
						if (!isPass1 && !isPass2)
							return false;
					} else if (fieldName.equals("PartyID")) {
						boolean isPass1 = false;
						boolean isPass2 = false;

						List<Document> sourceList = doc.getList("sourceList", Document.class);
						if (sourceList != null) {
							for (Document source : sourceList) {
								String type = source.getString("type");
								String value = source.getString("value");
								if (type != null && value != null && (type.equals("urn:epcglobal:cbv:sdt:owning_party")
										|| type.equals("urn:epcglobal:cbv:sdt:possessing_party"))) {
									if (isPassString(new ArrayList<String>(wdValues), value)) {
										isPass1 = true;
										break;
									}
								}
							}
						}
						List<Document> destinationList = doc.getList("destinationList", Document.class);
						if (destinationList != null) {
							for (Document dest : destinationList) {
								String type = dest.getString("type");
								String value = dest.getString("value");
								if (type != null && value != null && (type.equals("urn:epcglobal:cbv:sdt:owning_party")
										|| type.equals("urn:epcglobal:cbv:sdt:possessing_party"))) {
									if (isPassString(new ArrayList<String>(wdValues), value)) {
										isPass2 = true;
										break;
									}
								}
							}
						}
						if (!isPass1 && !isPass2)
							return false;
					} else if (fieldName.equals("MicroorganismID")) {
						List<Document> sensorElementList = doc.getList("sensorElementList", Document.class);
						if (sensorElementList == null)
							return false;
						boolean isPass = false;
						for (Document sensorElement : sensorElementList) {
							List<Document> sensorReportList = sensorElement.getList("sensorReport", Document.class);
							if (sensorReportList == null)
								continue;
							for (Document sensorReport : sensorReportList) {
								String value = sensorReport.getString("microorganism");
								if (isPassString(new ArrayList<String>(wdValues), value)) {
									isPass = true;
									break;
								}
							}
							if (isPass == true)
								break;
						}
						if (isPass == false)
							return false;
					} else if (fieldName.equals("ChemicalSubstanceID")) {
						List<Document> sensorElementList = doc.getList("sensorElementList", Document.class);
						if (sensorElementList == null)
							return false;
						boolean isPass = false;
						for (Document sensorElement : sensorElementList) {
							List<Document> sensorReportList = sensorElement.getList("sensorReport", Document.class);
							if (sensorReportList == null)
								continue;
							for (Document sensorReport : sensorReportList) {
								String value = sensorReport.getString("chemicalSubstance");
								if (isPassString(new ArrayList<String>(wdValues), value)) {
									isPass = true;
									break;
								}
							}
							if (isPass == true)
								break;
						}
						if (isPass == false)
							return false;
					} else if (fieldName.equals("ResourceID")) {
						vtype = "urn:epcglobal:epcis:vtype:Resource";
						// TODO: not supported yet
					} else {
						throw new QueryParameterException(fieldName
								+ " should be one of readPoint, bizLocation, BusinessTransaction, EPCClass, SourceDestID, LocationID, PartyID, MicroorganismID, ChemicalSubstanceID, ResourceID");
					}
					// map.put(fieldName, new ArrayList<String>(wdValues));
				}
			}

		} catch (Exception e) {
			return false;
		} catch (QueryParameterException e) {
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

			if (name.equals("eventType")) {
				eventType = (List<String>) value;
				continue;
			}

			if (name.equals("GE_eventTime")) {
				GE_eventTime = (long) value;
				continue;
			}

			if (name.equals("LT_eventTime")) {
				LT_eventTime = (long) value;
				continue;
			}

			if (name.equals("GE_recordTime")) {
				GE_recordTime = (long) value;
				continue;
			}

			if (name.equals("LT_recordTime")) {
				LT_recordTime = (long) value;
				continue;
			}

			if (name.equals("EQ_action")) {
				EQ_action = (List<String>) value;
				continue;
			}

			if (name.equals("EQ_bizStep")) {
				EQ_bizStep = (List<String>) value;
				continue;
			}

			if (name.equals("EQ_disposition")) {
				EQ_disposition = (List<String>) value;
				continue;
			}

			if (name.equals("EQ_persistentDisposition_set")) {
				EQ_persistentDisposition_set = (List<String>) value;
				continue;
			}

			if (name.equals("EQ_persistentDisposition_unset")) {
				EQ_persistentDisposition_unset = (List<String>) value;
				continue;
			}

			if (name.equals("EQ_readPoint")) {
				EQ_readPoint = (List<String>) value;
				continue;
			}

			if (name.equals("EQ_bizLocation")) {
				EQ_bizLocation = (List<String>) value;
				continue;
			}

			if (name.equals("EQ_transformationID")) {
				EQ_transformationID = (List<String>) value;
				continue;
			}

			if (name.equals("EQ_eventID")) {
				EQ_eventID = (List<String>) value;
				continue;
			}

			if (name.equals("EXISTS_errorDeclaration")) {
				EXISTS_errorDeclaration = (VoidHolder) value;
				continue;
			}

			if (name.equals("GE_errorDeclarationTime")) {
				GE_errorDeclarationTime = (long) value;
				continue;
			}

			if (name.equals("LT_errorDeclarationTime")) {
				LT_errorDeclarationTime = (long) value;
				continue;
			}

			if (name.equals("EQ_errorReason")) {
				EQ_errorReason = (List<String>) value;
				continue;
			}

			if (name.equals("EQ_correctiveEventID")) {
				EQ_correctiveEventID = (List<String>) value;
				continue;
			}

			if (name.equals("MATCH_epc")) {
				MATCH_epc = (List<String>) value;
				continue;
			}

			if (name.equals("MATCH_parentID")) {
				MATCH_parentID = (List<String>) value;
				continue;
			}

			if (name.equals("MATCH_inputEPC")) {
				MATCH_inputEPC = (List<String>) value;
				continue;
			}

			if (name.equals("MATCH_outputEPC")) {
				MATCH_outputEPC = (List<String>) value;
				continue;
			}

			if (name.equals("MATCH_anyEPC")) {
				MATCH_anyEPC = (List<String>) value;
				continue;
			}

			if (name.equals("MATCH_epcClass")) {
				MATCH_epcClass = (List<String>) value;
				continue;
			}

			if (name.equals("MATCH_inputEPCClass")) {
				MATCH_inputEPCClass = (List<String>) value;
				continue;
			}

			if (name.equals("MATCH_outputEPCClass")) {
				MATCH_outputEPCClass = (List<String>) value;
				continue;
			}

			if (name.equals("MATCH_anyEPCClass")) {
				MATCH_anyEPCClass = (List<String>) value;
				continue;
			}

			if (name.equals("GE_startTime")) {
				GE_startTime = (long) value;
				continue;
			}

			if (name.equals("LT_startTime")) {
				LT_startTime = (long) value;
				continue;
			}

			if (name.equals("GE_endTime")) {
				GE_endTime = (long) value;
				continue;
			}

			if (name.equals("LT_endTime")) {
				LT_endTime = (long) value;
				continue;

			}

			if (name.equals("GE_SENSORMETADATA_time")) {
				GE_SENSORMETADATA_time = (long) value;
				continue;
			}

			if (name.equals("LT_SENSORMETADATA_time")) {
				LT_SENSORMETADATA_time = (long) value;
				continue;
			}

			if (name.equals("GE_SENSORREPORT_time")) {
				GE_SENSORREPORT_time = (long) value;
				continue;
			}

			if (name.equals("LT_SENSORREPORT_time")) {
				LT_SENSORREPORT_time = (long) value;
				continue;
			}

			if (name.equals("EQ_deviceID")) {
				EQ_deviceID = (List<String>) value;
				continue;
			}

			if (name.equals("EQ_SENSORMETADATA_deviceID")) {
				EQ_SENSORMETADATA_deviceID = (List<String>) value;
				continue;
			}

			if (name.equals("EQ_SENSORREPORT_deviceID")) {
				EQ_SENSORREPORT_deviceID = (List<String>) value;
				continue;
			}

			if (name.equals("EQ_SENSORMETADATA_deviceMetadata")) {
				EQ_SENSORMETADATA_deviceMetadata = (List<String>) value;
				continue;
			}

			if (name.equals("EQ_SENSORREPORT_deviceMetadata")) {
				EQ_SENSORREPORT_deviceMetadata = (List<String>) value;
				continue;
			}

			if (name.equals("EQ_SENSORMETADATA_rawData")) {
				EQ_SENSORMETADATA_rawData = (List<String>) value;
				continue;
			}

			if (name.equals("EQ_SENSORREPORT_rawData")) {
				EQ_SENSORREPORT_rawData = (List<String>) value;
				continue;
			}

			if (name.equals("EQ_dataProcessingMethod")) {
				EQ_dataProcessingMethod = (List<String>) value;
				continue;
			}

			if (name.equals("EQ_SENSORMETADATA_dataProcessingMethod")) {
				EQ_SENSORMETADATA_dataProcessingMethod = (List<String>) value;
				continue;
			}

			if (name.equals("EQ_SENSORREPORT_dataProcessingMethod")) {
				EQ_SENSORREPORT_dataProcessingMethod = (List<String>) value;
				continue;
			}

			if (name.equals("EQ_microorganism")) {
				EQ_microorganism = (List<String>) value;
				continue;
			}

			if (name.equals("EQ_chemicalSubstance")) {
				EQ_chemicalSubstance = (List<String>) value;
				continue;
			}

			if (name.equals("EQ_bizRules")) {
				EQ_bizRules = (List<String>) value;
				continue;
			}

			if (name.equals("EQ_stringValue")) {
				EQ_stringValue = (List<String>) value;
				continue;
			}

			if (name.equals("EQ_booleanValue")) {
				EQ_booleanValue = (Boolean) value;
				continue;
			}

			if (name.equals("EQ_hexBinaryValue")) {
				EQ_hexBinaryValue = (List<String>) value;
				continue;
			}

			if (name.equals("EQ_uriValue")) {
				EQ_uriValue = (List<String>) value;
				continue;
			}

			if (name.equals("GE_percRank")) {
				GE_percRank = (double) value;
				continue;
			}

			if (name.equals("LT_percRank")) {
				LT_percRank = (double) value;
				continue;
			}

			if (name.startsWith("EQ_bizTransaction")) {
				if (EQ_bizTransaction == null)
					EQ_bizTransaction = new HashMap<String, List<String>>();
				String type = name.substring(18);
				List<String> values = EQ_bizTransaction.get(type);
				if (values == null)
					values = new ArrayList<String>();
				values.addAll((List<String>) value);
				EQ_bizTransaction.put(type, values);
				continue;
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
				continue;
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
				continue;
			}

			if (name.startsWith("EQ_quantity")) {
				if (EQ_quantity == null)
					EQ_quantity = new HashMap<String, Double>();
				String uom = name.substring(12);

				EQ_quantity.put(uom, (double) value);
				continue;
			}

			if (name.startsWith("GT_quantity")) {
				if (GT_quantity == null)
					GT_quantity = new HashMap<String, Double>();
				String uom = name.substring(12);

				GT_quantity.put(uom, (double) value);
				continue;
			}

			if (name.startsWith("GE_quantity")) {
				if (GE_quantity == null)
					GE_quantity = new HashMap<String, Double>();
				String uom = name.substring(12);

				GE_quantity.put(uom, (double) value);
				continue;
			}

			if (name.startsWith("LT_quantity")) {
				if (LT_quantity == null)
					LT_quantity = new HashMap<String, Double>();
				String uom = name.substring(12);

				LT_quantity.put(uom, (double) value);
				continue;
			}

			if (name.startsWith("LE_quantity")) {
				if (LE_quantity == null)
					LE_quantity = new HashMap<String, Double>();
				String uom = name.substring(12);

				LE_quantity.put(uom, (double) value);
				continue;
			}

			if (name.startsWith("EQ_INNER_ILMD")) {
				if (EQ_INNER_ILMD == null)
					EQ_INNER_ILMD = new HashMap<String, Object>();
				String key = name.substring(14);
				EQ_INNER_ILMD.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}

			if (name.startsWith("GT_INNER_ILMD")) {
				if (GT_INNER_ILMD == null)
					GT_INNER_ILMD = new HashMap<String, Object>();
				String key = name.substring(14);
				GT_INNER_ILMD.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}

			if (name.startsWith("GE_INNER_ILMD")) {
				if (GE_INNER_ILMD == null)
					GE_INNER_ILMD = new HashMap<String, Object>();
				String key = name.substring(14);
				GE_INNER_ILMD.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}

			if (name.startsWith("LT_INNER_ILMD")) {
				if (LT_INNER_ILMD == null)
					LT_INNER_ILMD = new HashMap<String, Object>();
				String key = name.substring(14);
				LT_INNER_ILMD.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}

			if (name.startsWith("LE_INNER_ILMD")) {
				if (LE_INNER_ILMD == null)
					LE_INNER_ILMD = new HashMap<String, Object>();
				String key = name.substring(14);
				LE_INNER_ILMD.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}

			if (name.startsWith("EXISTS_INNER_ILMD")) {
				if (EXISTS_INNER_ILMD == null)
					EXISTS_INNER_ILMD = new ArrayList<String>();
				EXISTS_INNER_ILMD.add(POJOtoBSONUtil.encodeMongoObjectKey(name.substring(18)));
				continue;
			}

			if (name.startsWith("EQ_INNER_SENSORELEMENT")) {
				if (EQ_INNER_SENSORELEMENT == null)
					EQ_INNER_SENSORELEMENT = new HashMap<String, Object>();
				String key = name.substring(23);
				EQ_INNER_SENSORELEMENT.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}

			if (name.startsWith("GT_INNER_SENSORELEMENT")) {
				if (GT_INNER_SENSORELEMENT == null)
					GT_INNER_SENSORELEMENT = new HashMap<String, Object>();
				String key = name.substring(23);
				GT_INNER_SENSORELEMENT.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}
			if (name.startsWith("GE_INNER_SENSORELEMENT")) {
				if (GE_INNER_SENSORELEMENT == null)
					GE_INNER_SENSORELEMENT = new HashMap<String, Object>();
				String key = name.substring(23);
				GE_INNER_SENSORELEMENT.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}
			if (name.startsWith("LT_INNER_SENSORELEMENT")) {
				if (LT_INNER_SENSORELEMENT == null)
					LT_INNER_SENSORELEMENT = new HashMap<String, Object>();
				String key = name.substring(23);
				LT_INNER_SENSORELEMENT.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}
			if (name.startsWith("LE_INNER_SENSORELEMENT")) {
				if (LE_INNER_SENSORELEMENT == null)
					LE_INNER_SENSORELEMENT = new HashMap<String, Object>();
				String key = name.substring(23);
				LE_INNER_SENSORELEMENT.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}

			if (name.startsWith("EXISTS_INNER_SENSORELEMENT")) {
				if (EXISTS_INNER_SENSORELEMENT == null)
					EXISTS_INNER_SENSORELEMENT = new ArrayList<String>();
				EXISTS_INNER_SENSORELEMENT.add(POJOtoBSONUtil.encodeMongoObjectKey(name.substring(27)));
				continue;
			}

			if (name.startsWith("EQ_INNER_readPoint")) {
				if (EQ_INNER_readPoint == null)
					EQ_INNER_readPoint = new HashMap<String, Object>();
				String key = name.substring(19);
				EQ_INNER_readPoint.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}

			if (name.startsWith("GT_INNER_readPoint")) {
				if (GT_INNER_readPoint == null)
					GT_INNER_readPoint = new HashMap<String, Object>();
				String key = name.substring(19);
				GT_INNER_readPoint.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}
			if (name.startsWith("GE_INNER_readPoint")) {
				if (GE_INNER_readPoint == null)
					GE_INNER_readPoint = new HashMap<String, Object>();
				String key = name.substring(19);
				GE_INNER_readPoint.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}
			if (name.startsWith("LT_INNER_readPoint")) {
				if (LT_INNER_readPoint == null)
					LT_INNER_readPoint = new HashMap<String, Object>();
				String key = name.substring(19);
				LT_INNER_readPoint.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}
			if (name.startsWith("LE_INNER_readPoint")) {
				if (LE_INNER_readPoint == null)
					LE_INNER_readPoint = new HashMap<String, Object>();
				String key = name.substring(19);
				LE_INNER_readPoint.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}

			if (name.startsWith("EXISTS_INNER_readPoint")) {
				if (EXISTS_INNER_readPoint == null)
					EXISTS_INNER_readPoint = new ArrayList<String>();
				EXISTS_INNER_readPoint.add(POJOtoBSONUtil.encodeMongoObjectKey(name.substring(23)));
				continue;
			}

			if (name.startsWith("EQ_INNER_bizLocation")) {
				if (EQ_INNER_bizLocation == null)
					EQ_INNER_bizLocation = new HashMap<String, Object>();
				String key = name.substring(21);
				EQ_INNER_bizLocation.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}

			if (name.startsWith("GT_INNER_bizLocation")) {
				if (GT_INNER_bizLocation == null)
					GT_INNER_bizLocation = new HashMap<String, Object>();
				String key = name.substring(21);
				GT_INNER_bizLocation.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}
			if (name.startsWith("GE_INNER_bizLocation")) {
				if (GE_INNER_bizLocation == null)
					GE_INNER_bizLocation = new HashMap<String, Object>();
				String key = name.substring(21);
				GE_INNER_bizLocation.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}
			if (name.startsWith("LT_INNER_bizLocation")) {
				if (LT_INNER_bizLocation == null)
					LT_INNER_bizLocation = new HashMap<String, Object>();
				String key = name.substring(21);
				LT_INNER_bizLocation.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}
			if (name.startsWith("LE_INNER_bizLocation")) {
				if (LE_INNER_bizLocation == null)
					LE_INNER_bizLocation = new HashMap<String, Object>();
				String key = name.substring(21);
				LE_INNER_bizLocation.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}

			if (name.startsWith("EXISTS_INNER_bizLocation")) {
				if (EXISTS_INNER_bizLocation == null)
					EXISTS_INNER_bizLocation = new ArrayList<String>();
				EXISTS_INNER_bizLocation.add(POJOtoBSONUtil.encodeMongoObjectKey(name.substring(25)));
				continue;
			}

			if (name.startsWith("EQ_INNER_ERROR_DECLARATION")) {
				if (EQ_INNER_ERROR_DECLARATION == null)
					EQ_INNER_ERROR_DECLARATION = new HashMap<String, Object>();
				String key = name.substring(27);
				EQ_INNER_ERROR_DECLARATION.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}

			if (name.startsWith("GT_INNER_ERROR_DECLARATION")) {
				if (GT_INNER_ERROR_DECLARATION == null)
					GT_INNER_ERROR_DECLARATION = new HashMap<String, Object>();
				String key = name.substring(27);
				GT_INNER_ERROR_DECLARATION.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}
			if (name.startsWith("GE_INNER_ERROR_DECLARATION")) {
				if (GE_INNER_ERROR_DECLARATION == null)
					GE_INNER_ERROR_DECLARATION = new HashMap<String, Object>();
				String key = name.substring(27);
				GE_INNER_ERROR_DECLARATION.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}
			if (name.startsWith("LT_INNER_ERROR_DECLARATION")) {
				if (LT_INNER_ERROR_DECLARATION == null)
					LT_INNER_ERROR_DECLARATION = new HashMap<String, Object>();
				String key = name.substring(27);
				LT_INNER_ERROR_DECLARATION.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}
			if (name.startsWith("LE_INNER_ERROR_DECLARATION")) {
				if (LE_INNER_ERROR_DECLARATION == null)
					LE_INNER_ERROR_DECLARATION = new HashMap<String, Object>();
				String key = name.substring(27);
				LE_INNER_ERROR_DECLARATION.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}

			if (name.startsWith("EXISTS_INNER_ERROR_DECLARATION")) {
				if (EXISTS_INNER_ERROR_DECLARATION == null)
					EXISTS_INNER_ERROR_DECLARATION = new ArrayList<String>();
				EXISTS_INNER_ERROR_DECLARATION.add(POJOtoBSONUtil.encodeMongoObjectKey(name.substring(31)));
				continue;
			}

			if (name.startsWith("EQ_INNER")) {
				if (EQ_INNER == null)
					EQ_INNER = new HashMap<String, Object>();
				String key = name.substring(9);
				EQ_INNER.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}

			if (name.startsWith("GT_INNER")) {
				if (GT_INNER == null)
					GT_INNER = new HashMap<String, Object>();
				String key = name.substring(9);
				GT_INNER.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}
			if (name.startsWith("GE_INNER")) {
				if (GE_INNER == null)
					GE_INNER = new HashMap<String, Object>();
				String key = name.substring(9);
				GE_INNER.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}
			if (name.startsWith("LT_INNER")) {
				if (LT_INNER == null)
					LT_INNER = new HashMap<String, Object>();
				String key = name.substring(9);
				LT_INNER.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}
			if (name.startsWith("LE_INNER")) {
				if (LE_INNER == null)
					LE_INNER = new HashMap<String, Object>();
				String key = name.substring(9);
				LE_INNER.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}

			if (name.startsWith("EXISTS_INNER")) {
				if (EXISTS_INNER == null)
					EXISTS_INNER = new ArrayList<String>();
				EXISTS_INNER.add(POJOtoBSONUtil.encodeMongoObjectKey(name.substring(13)));
				continue;
			}

			if (name.startsWith("EQ_ILMD")) {
				if (EQ_ILMD == null)
					EQ_ILMD = new HashMap<String, Object>();
				String key = name.substring(8);
				EQ_ILMD.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}

			if (name.startsWith("GT_ILMD")) {
				if (GT_ILMD == null)
					GT_ILMD = new HashMap<String, Object>();
				String key = name.substring(8);
				GT_ILMD.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}

			if (name.startsWith("GE_ILMD")) {
				if (GE_ILMD == null)
					GE_ILMD = new HashMap<String, Object>();
				String key = name.substring(8);
				GE_ILMD.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}

			if (name.startsWith("LT_ILMD")) {
				if (LT_ILMD == null)
					LT_ILMD = new HashMap<String, Object>();
				String key = name.substring(8);
				LT_ILMD.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}

			if (name.startsWith("LE_ILMD")) {
				if (LE_ILMD == null)
					LE_ILMD = new HashMap<String, Object>();
				String key = name.substring(8);
				LE_ILMD.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}

			if (name.startsWith("EXISTS_ILMD")) {
				if (EXISTS_ILMD == null)
					EXISTS_ILMD = new ArrayList<String>();
				EXISTS_ILMD.add(POJOtoBSONUtil.encodeMongoObjectKey(name.substring(12)));
				continue;
			}

			if (name.startsWith("EQ_SENSORELEMENT")) {
				if (EQ_SENSORELEMENT == null)
					EQ_SENSORELEMENT = new HashMap<String, Object>();
				String key = name.substring(17);
				EQ_SENSORELEMENT.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}

			if (name.startsWith("GT_SENSORELEMENT")) {
				if (GT_SENSORELEMENT == null)
					GT_SENSORELEMENT = new HashMap<String, Object>();
				String key = name.substring(17);
				GT_SENSORELEMENT.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}
			if (name.startsWith("GE_SENSORELEMENT")) {
				if (GE_SENSORELEMENT == null)
					GE_SENSORELEMENT = new HashMap<String, Object>();
				String key = name.substring(17);
				GE_SENSORELEMENT.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}
			if (name.startsWith("LT_SENSORELEMENT")) {
				if (LT_SENSORELEMENT == null)
					LT_SENSORELEMENT = new HashMap<String, Object>();
				String key = name.substring(17);
				LT_SENSORELEMENT.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}
			if (name.startsWith("LE_SENSORELEMENT")) {
				if (LE_SENSORELEMENT == null)
					LE_SENSORELEMENT = new HashMap<String, Object>();
				String key = name.substring(17);
				LE_SENSORELEMENT.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}

			if (name.startsWith("EXISTS_SENSORELEMENT")) {
				if (EXISTS_SENSORELEMENT == null)
					EXISTS_SENSORELEMENT = new ArrayList<String>();
				EXISTS_SENSORELEMENT.add(POJOtoBSONUtil.encodeMongoObjectKey(name.substring(21)));
				continue;
			}

			if (name.startsWith("EQ_SENSORMETADATA")) {
				if (EQ_SENSORMETADATA == null)
					EQ_SENSORMETADATA = new HashMap<String, List<String>>();
				String type = name.substring(18);
				List<String> values = EQ_SENSORMETADATA.get(type);
				if (values == null)
					values = new ArrayList<String>();
				values.addAll((List<String>) value);
				EQ_SENSORMETADATA.put(POJOtoBSONUtil.encodeMongoObjectKey(type), values);
				continue;
			}

			if (name.startsWith("EQ_SENSORREPORT")) {
				if (EQ_SENSORREPORT == null)
					EQ_SENSORREPORT = new HashMap<String, List<String>>();
				String type = name.substring(16);
				List<String> values = EQ_SENSORREPORT.get(type);
				if (values == null)
					values = new ArrayList<String>();
				values.addAll((List<String>) value);
				EQ_SENSORREPORT.put(POJOtoBSONUtil.encodeMongoObjectKey(type), values);
				continue;
			}

			if (name.equals("EXISTS_SENSORMETADATA")) {
				EXISTS_SENSORMETADATA = (VoidHolder) value;
				continue;
			}

			if (name.equals("EXISTS_SENSORREPORT")) {
				EXISTS_SENSORREPORT = (VoidHolder) value;
				continue;
			}

			if (name.startsWith("EQ_readPoint_")) {
				if (EQ_readPoint_extension == null)
					EQ_readPoint_extension = new HashMap<String, Object>();
				String key = name.substring(13);
				EQ_readPoint_extension.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}

			if (name.startsWith("GT_readPoint_")) {
				if (GT_readPoint_extension == null)
					GT_readPoint_extension = new HashMap<String, Object>();
				String key = name.substring(13);
				GT_readPoint_extension.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}

			if (name.startsWith("GE_readPoint_")) {
				if (GE_readPoint_extension == null)
					GE_readPoint_extension = new HashMap<String, Object>();
				String key = name.substring(13);
				GE_readPoint_extension.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}

			if (name.startsWith("LT_readPoint_")) {
				if (LT_readPoint_extension == null)
					LT_readPoint_extension = new HashMap<String, Object>();
				String key = name.substring(13);
				LT_readPoint_extension.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}

			if (name.startsWith("LE_readPoint_")) {
				if (LE_readPoint_extension == null)
					LE_readPoint_extension = new HashMap<String, Object>();
				String key = name.substring(13);
				LE_readPoint_extension.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}

			if (name.startsWith("EXISTS_readPoint_")) {
				if (EXISTS_readPoint_extension == null)
					EXISTS_readPoint_extension = new ArrayList<String>();
				EXISTS_readPoint_extension.add(POJOtoBSONUtil.encodeMongoObjectKey(name.substring(17)));
				continue;
			}

			if (name.startsWith("EQ_bizLocation_")) {
				if (EQ_bizLocation_extension == null)
					EQ_bizLocation_extension = new HashMap<String, Object>();
				String key = name.substring(15);
				EQ_bizLocation_extension.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}

			if (name.startsWith("GT_bizLocation_")) {
				if (GT_bizLocation_extension == null)
					GT_bizLocation_extension = new HashMap<String, Object>();
				String key = name.substring(15);
				GT_bizLocation_extension.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}

			if (name.startsWith("GE_bizLocation_")) {
				if (GE_bizLocation_extension == null)
					GE_bizLocation_extension = new HashMap<String, Object>();
				String key = name.substring(15);
				GE_bizLocation_extension.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}

			if (name.startsWith("LT_bizLocation_")) {
				if (LT_bizLocation_extension == null)
					LT_bizLocation_extension = new HashMap<String, Object>();
				String key = name.substring(15);
				LT_bizLocation_extension.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}

			if (name.startsWith("LE_bizLocation_")) {
				if (LE_bizLocation_extension == null)
					LE_bizLocation_extension = new HashMap<String, Object>();
				String key = name.substring(15);
				LE_bizLocation_extension.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}

			if (name.startsWith("EXISTS_bizLocation_")) {
				if (EXISTS_bizLocation_extension == null)
					EXISTS_bizLocation_extension = new ArrayList<String>();
				EXISTS_bizLocation_extension.add(POJOtoBSONUtil.encodeMongoObjectKey(name.substring(19)));
				continue;
			}

			if (name.startsWith("EQ_ERROR_DECLARATION_")) {
				if (EQ_ERROR_DECLARATION_extension == null)
					EQ_ERROR_DECLARATION_extension = new HashMap<String, Object>();
				String key = name.substring(21);
				EQ_ERROR_DECLARATION_extension.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}

			if (name.startsWith("GT_ERROR_DECLARATION_")) {
				if (GT_ERROR_DECLARATION_extension == null)
					GT_ERROR_DECLARATION_extension = new HashMap<String, Object>();
				String key = name.substring(21);
				GT_ERROR_DECLARATION_extension.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}

			if (name.startsWith("GE_ERROR_DECLARATION_")) {
				if (GE_ERROR_DECLARATION_extension == null)
					GE_ERROR_DECLARATION_extension = new HashMap<String, Object>();
				String key = name.substring(21);
				GE_ERROR_DECLARATION_extension.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}

			if (name.startsWith("LT_ERROR_DECLARATION_")) {
				if (LT_ERROR_DECLARATION_extension == null)
					LT_ERROR_DECLARATION_extension = new HashMap<String, Object>();
				String key = name.substring(21);
				LT_ERROR_DECLARATION_extension.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}

			if (name.startsWith("LE_ERROR_DECLARATION_")) {
				if (LE_ERROR_DECLARATION_extension == null)
					LE_ERROR_DECLARATION_extension = new HashMap<String, Object>();
				String key = name.substring(21);
				LE_ERROR_DECLARATION_extension.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}

			if (name.startsWith("EXISTS_ERROR_DECLARATION_")) {
				if (EXISTS_ERROR_DECLARATION_extension == null)
					EXISTS_ERROR_DECLARATION_extension = new ArrayList<String>();
				EXISTS_ERROR_DECLARATION_extension.add(POJOtoBSONUtil.encodeMongoObjectKey(name.substring(25)));
				continue;
			}

			if (name.equals("EQ_type")) {
				EQ_type = (List<String>) value;
				continue;
			}

			if (name.startsWith("EQ_value_")) {
				String uom = name.substring(9);
				Double v = (Double) value;

				String type = Resource.unitConverter.getType(uom);
				String rUom = Resource.unitConverter.getRepresentativeUoMFromType(type);
				double rValue = Resource.unitConverter.getRepresentativeValue(type, uom, v);

				EQ_value = new SensorUomValue(rUom, rValue);
				continue;
			}

			if (name.startsWith("GT_value_")) {
				String uom = name.substring(9);
				Double v = (Double) value;

				String type = Resource.unitConverter.getType(uom);
				String rUom = Resource.unitConverter.getRepresentativeUoMFromType(type);
				double rValue = Resource.unitConverter.getRepresentativeValue(type, uom, v);

				GT_value = new SensorUomValue(rUom, rValue);
				continue;
			}

			if (name.startsWith("GE_value_")) {
				String uom = name.substring(9);
				Double v = (Double) value;

				String type = Resource.unitConverter.getType(uom);
				String rUom = Resource.unitConverter.getRepresentativeUoMFromType(type);
				double rValue = Resource.unitConverter.getRepresentativeValue(type, uom, v);

				GE_value = new SensorUomValue(rUom, rValue);
				continue;
			}

			if (name.startsWith("LT_value_")) {
				String uom = name.substring(9);
				Double v = (Double) value;

				String type = Resource.unitConverter.getType(uom);
				String rUom = Resource.unitConverter.getRepresentativeUoMFromType(type);
				double rValue = Resource.unitConverter.getRepresentativeValue(type, uom, v);

				LT_value = new SensorUomValue(rUom, rValue);
				continue;
			}

			if (name.startsWith("LE_value_")) {
				String uom = name.substring(9);
				Double v = (Double) value;

				String type = Resource.unitConverter.getType(uom);
				String rUom = Resource.unitConverter.getRepresentativeUoMFromType(type);
				double rValue = Resource.unitConverter.getRepresentativeValue(type, uom, v);

				LE_value = new SensorUomValue(rUom, rValue);
				continue;
			}

			if (name.startsWith("EQ_minValue_")) {
				String uom = name.substring(12);
				Double v = (Double) value;

				String type = Resource.unitConverter.getType(uom);
				String rUom = Resource.unitConverter.getRepresentativeUoMFromType(type);
				double rValue = Resource.unitConverter.getRepresentativeValue(type, uom, v);

				EQ_minValue = new SensorUomValue(rUom, rValue);
				continue;
			}

			if (name.startsWith("GT_minValue_")) {
				String uom = name.substring(12);
				Double v = (Double) value;

				String type = Resource.unitConverter.getType(uom);
				String rUom = Resource.unitConverter.getRepresentativeUoMFromType(type);
				double rValue = Resource.unitConverter.getRepresentativeValue(type, uom, v);

				GT_minValue = new SensorUomValue(rUom, rValue);
				continue;
			}

			if (name.startsWith("GE_minValue_")) {
				String uom = name.substring(12);
				Double v = (Double) value;

				String type = Resource.unitConverter.getType(uom);
				String rUom = Resource.unitConverter.getRepresentativeUoMFromType(type);
				double rValue = Resource.unitConverter.getRepresentativeValue(type, uom, v);

				GE_minValue = new SensorUomValue(rUom, rValue);
				continue;
			}

			if (name.startsWith("LT_minValue_")) {
				String uom = name.substring(12);
				Double v = (Double) value;

				String type = Resource.unitConverter.getType(uom);
				String rUom = Resource.unitConverter.getRepresentativeUoMFromType(type);
				double rValue = Resource.unitConverter.getRepresentativeValue(type, uom, v);

				LT_minValue = new SensorUomValue(rUom, rValue);
				continue;
			}

			if (name.startsWith("LE_minValue_")) {
				String uom = name.substring(12);
				Double v = (Double) value;

				String type = Resource.unitConverter.getType(uom);
				String rUom = Resource.unitConverter.getRepresentativeUoMFromType(type);
				double rValue = Resource.unitConverter.getRepresentativeValue(type, uom, v);

				LE_minValue = new SensorUomValue(rUom, rValue);
				continue;
			}

			if (name.startsWith("EQ_maxValue_")) {
				String uom = name.substring(12);
				Double v = (Double) value;

				String type = Resource.unitConverter.getType(uom);
				String rUom = Resource.unitConverter.getRepresentativeUoMFromType(type);
				double rValue = Resource.unitConverter.getRepresentativeValue(type, uom, v);

				EQ_maxValue = new SensorUomValue(rUom, rValue);
				continue;
			}

			if (name.startsWith("GT_maxValue_")) {
				String uom = name.substring(12);
				Double v = (Double) value;

				String type = Resource.unitConverter.getType(uom);
				String rUom = Resource.unitConverter.getRepresentativeUoMFromType(type);
				double rValue = Resource.unitConverter.getRepresentativeValue(type, uom, v);

				GT_maxValue = new SensorUomValue(rUom, rValue);
				continue;
			}

			if (name.startsWith("GE_maxValue_")) {
				String uom = name.substring(12);
				Double v = (Double) value;

				String type = Resource.unitConverter.getType(uom);
				String rUom = Resource.unitConverter.getRepresentativeUoMFromType(type);
				double rValue = Resource.unitConverter.getRepresentativeValue(type, uom, v);

				GE_maxValue = new SensorUomValue(rUom, rValue);
				continue;
			}

			if (name.startsWith("LT_maxValue_")) {
				String uom = name.substring(12);
				Double v = (Double) value;

				String type = Resource.unitConverter.getType(uom);
				String rUom = Resource.unitConverter.getRepresentativeUoMFromType(type);
				double rValue = Resource.unitConverter.getRepresentativeValue(type, uom, v);

				LT_maxValue = new SensorUomValue(rUom, rValue);
				continue;
			}

			if (name.startsWith("LE_maxValue_")) {
				String uom = name.substring(12);
				Double v = (Double) value;

				String type = Resource.unitConverter.getType(uom);
				String rUom = Resource.unitConverter.getRepresentativeUoMFromType(type);
				double rValue = Resource.unitConverter.getRepresentativeValue(type, uom, v);

				LE_maxValue = new SensorUomValue(rUom, rValue);
				continue;
			}

			if (name.startsWith("EQ_meanValue_")) {
				String uom = name.substring(13);
				Double v = (Double) value;

				String type = Resource.unitConverter.getType(uom);
				String rUom = Resource.unitConverter.getRepresentativeUoMFromType(type);
				double rValue = Resource.unitConverter.getRepresentativeValue(type, uom, v);

				EQ_meanValue = new SensorUomValue(rUom, rValue);
				continue;
			}

			if (name.startsWith("GT_meanValue_")) {
				String uom = name.substring(13);
				Double v = (Double) value;

				String type = Resource.unitConverter.getType(uom);
				String rUom = Resource.unitConverter.getRepresentativeUoMFromType(type);
				double rValue = Resource.unitConverter.getRepresentativeValue(type, uom, v);

				GT_meanValue = new SensorUomValue(rUom, rValue);
				continue;
			}

			if (name.startsWith("GE_meanValue_")) {
				String uom = name.substring(13);
				Double v = (Double) value;

				String type = Resource.unitConverter.getType(uom);
				String rUom = Resource.unitConverter.getRepresentativeUoMFromType(type);
				double rValue = Resource.unitConverter.getRepresentativeValue(type, uom, v);

				GE_meanValue = new SensorUomValue(rUom, rValue);
				continue;
			}

			if (name.startsWith("LT_meanValue_")) {
				String uom = name.substring(13);
				Double v = (Double) value;

				String type = Resource.unitConverter.getType(uom);
				String rUom = Resource.unitConverter.getRepresentativeUoMFromType(type);
				double rValue = Resource.unitConverter.getRepresentativeValue(type, uom, v);

				LT_meanValue = new SensorUomValue(rUom, rValue);
				continue;
			}

			if (name.startsWith("LE_meanValue_")) {
				String uom = name.substring(13);
				Double v = (Double) value;

				String type = Resource.unitConverter.getType(uom);
				String rUom = Resource.unitConverter.getRepresentativeUoMFromType(type);
				double rValue = Resource.unitConverter.getRepresentativeValue(type, uom, v);

				LE_meanValue = new SensorUomValue(rUom, rValue);
				continue;
			}

			if (name.startsWith("EQ_sDev_")) {
				String uom = name.substring(8);
				Double v = (Double) value;

				String type = Resource.unitConverter.getType(uom);
				String rUom = Resource.unitConverter.getRepresentativeUoMFromType(type);
				double rValue = Resource.unitConverter.getRepresentativeValue(type, uom, v);

				EQ_sDev = new SensorUomValue(rUom, rValue);
				continue;
			}

			if (name.startsWith("GT_sDev_")) {
				String uom = name.substring(8);
				Double v = (Double) value;

				String type = Resource.unitConverter.getType(uom);
				String rUom = Resource.unitConverter.getRepresentativeUoMFromType(type);
				double rValue = Resource.unitConverter.getRepresentativeValue(type, uom, v);

				GT_sDev = new SensorUomValue(rUom, rValue);
				continue;
			}

			if (name.startsWith("GE_sDev_")) {
				String uom = name.substring(8);
				Double v = (Double) value;

				String type = Resource.unitConverter.getType(uom);
				String rUom = Resource.unitConverter.getRepresentativeUoMFromType(type);
				double rValue = Resource.unitConverter.getRepresentativeValue(type, uom, v);

				GE_sDev = new SensorUomValue(rUom, rValue);
				continue;
			}

			if (name.startsWith("LT_sDev_")) {
				String uom = name.substring(8);
				Double v = (Double) value;

				String type = Resource.unitConverter.getType(uom);
				String rUom = Resource.unitConverter.getRepresentativeUoMFromType(type);
				double rValue = Resource.unitConverter.getRepresentativeValue(type, uom, v);

				LT_sDev = new SensorUomValue(rUom, rValue);
				continue;
			}

			if (name.startsWith("LE_sDev_")) {
				String uom = name.substring(8);
				Double v = (Double) value;

				String type = Resource.unitConverter.getType(uom);
				String rUom = Resource.unitConverter.getRepresentativeUoMFromType(type);
				double rValue = Resource.unitConverter.getRepresentativeValue(type, uom, v);

				LE_sDev = new SensorUomValue(rUom, rValue);
				continue;
			}

			if (name.startsWith("EQ_percValue_")) {
				String uom = name.substring(13);
				Double v = (Double) value;

				String type = Resource.unitConverter.getType(uom);
				String rUom = Resource.unitConverter.getRepresentativeUoMFromType(type);
				double rValue = Resource.unitConverter.getRepresentativeValue(type, uom, v);

				EQ_percValue = new SensorUomValue(rUom, rValue);
				continue;
			}

			if (name.startsWith("GT_percValue_")) {
				String uom = name.substring(13);
				Double v = (Double) value;

				String type = Resource.unitConverter.getType(uom);
				String rUom = Resource.unitConverter.getRepresentativeUoMFromType(type);
				double rValue = Resource.unitConverter.getRepresentativeValue(type, uom, v);

				GT_percValue = new SensorUomValue(rUom, rValue);
				continue;
			}

			if (name.startsWith("GE_percValue_")) {
				String uom = name.substring(13);
				Double v = (Double) value;

				String type = Resource.unitConverter.getType(uom);
				String rUom = Resource.unitConverter.getRepresentativeUoMFromType(type);
				double rValue = Resource.unitConverter.getRepresentativeValue(type, uom, v);

				GE_percValue = new SensorUomValue(rUom, rValue);
				continue;
			}

			if (name.startsWith("LT_percValue_")) {
				String uom = name.substring(13);
				Double v = (Double) value;

				String type = Resource.unitConverter.getType(uom);
				String rUom = Resource.unitConverter.getRepresentativeUoMFromType(type);
				double rValue = Resource.unitConverter.getRepresentativeValue(type, uom, v);

				LT_percValue = new SensorUomValue(rUom, rValue);
				continue;
			}

			if (name.startsWith("LE_percValue_")) {
				String uom = name.substring(13);
				Double v = (Double) value;

				String type = Resource.unitConverter.getType(uom);
				String rUom = Resource.unitConverter.getRepresentativeUoMFromType(type);
				double rValue = Resource.unitConverter.getRepresentativeValue(type, uom, v);

				LE_percValue = new SensorUomValue(rUom, rValue);
				continue;
			}

			if (name.startsWith("EQ_")) {
				if (EQ_extension == null)
					EQ_extension = new HashMap<String, Object>();
				String key = name.substring(3);
				EQ_extension.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}

			if (name.startsWith("GT_")) {
				if (GT_extension == null)
					GT_extension = new HashMap<String, Object>();
				String key = name.substring(3);
				GT_extension.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}

			if (name.startsWith("GE_")) {
				if (GE_extension == null)
					GE_extension = new HashMap<String, Object>();
				String key = name.substring(3);
				GE_extension.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}

			if (name.startsWith("LT_")) {
				if (LT_extension == null)
					LT_extension = new HashMap<String, Object>();
				String key = name.substring(3);
				LT_extension.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}

			if (name.startsWith("LE_")) {
				if (LE_extension == null)
					LE_extension = new HashMap<String, Object>();
				String key = name.substring(3);
				LE_extension.put(POJOtoBSONUtil.encodeMongoObjectKey(key), value);
				continue;
			}

			if (name.startsWith("EXISTS_")) {
				if (EXISTS_extension == null)
					EXISTS_extension = new ArrayList<String>();
				EXISTS_extension.add(POJOtoBSONUtil.encodeMongoObjectKey(name.substring(7)));
				continue;
			}

			if (name.equals("WD_readPoint")) {
				WD_readPoint = (List<String>) value;
				continue;
			}

			if (name.equals("WD_bizLocation")) {
				WD_bizLocation = (List<String>) value;
				continue;
			}

			if (name.startsWith("HASATTR_")) {
				if (HASATTR == null)
					HASATTR = new HashMap<String, List<String>>();
				String key = POJOtoBSONUtil.encodeMongoObjectKey(name.substring(8));
				List<String> values = HASATTR.get(key);
				if (values == null)
					values = new ArrayList<String>();
				values.addAll((List<String>) value);
				HASATTR.put(key, values);
				continue;
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

		if (MATCH_epcClass != null) {
			doc.put("MATCH_epcClass", MATCH_epcClass);
		}

		if (MATCH_inputEPCClass != null) {
			doc.put("MATCH_inputEPCClass", MATCH_inputEPCClass);
		}

		if (MATCH_outputEPCClass != null) {
			doc.put("MATCH_outputEPCClass", MATCH_outputEPCClass);
		}

		if (MATCH_anyEPCClass != null) {
			doc.put("MATCH_anyEPCClass", MATCH_anyEPCClass);
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

		if (EQ_INNER_readPoint != null) {
			doc.put("EQ_INNER_readPoint", EQ_INNER_readPoint);
		}

		if (GT_INNER_readPoint != null) {
			doc.put("GT_INNER_readPoint", GT_INNER_readPoint);
		}

		if (GE_INNER_readPoint != null) {
			doc.put("GE_INNER_readPoint", GE_INNER_readPoint);
		}

		if (LT_INNER_readPoint != null) {
			doc.put("LT_INNER_readPoint", LT_INNER_readPoint);
		}

		if (LE_INNER_readPoint != null) {
			doc.put("LE_INNER_readPoint", LE_INNER_readPoint);
		}

		if (EXISTS_INNER_readPoint != null) {
			doc.put("EXISTS_INNER_readPoint", EXISTS_INNER_readPoint);
		}

		if (EQ_INNER_bizLocation != null) {
			doc.put("EQ_INNER_bizLocation", EQ_INNER_bizLocation);
		}

		if (GT_INNER_bizLocation != null) {
			doc.put("GT_INNER_bizLocation", GT_INNER_bizLocation);
		}

		if (GE_INNER_bizLocation != null) {
			doc.put("GE_INNER_bizLocation", GE_INNER_bizLocation);
		}

		if (LT_INNER_bizLocation != null) {
			doc.put("LT_INNER_bizLocation", LT_INNER_bizLocation);
		}

		if (LE_INNER_bizLocation != null) {
			doc.put("LE_INNER_bizLocation", LE_INNER_bizLocation);
		}

		if (EXISTS_INNER_bizLocation != null) {
			doc.put("EXISTS_INNER_bizLocation", EXISTS_INNER_bizLocation);
		}

		if (EQ_INNER_ERROR_DECLARATION != null) {
			doc.put("EQ_INNER_ERROR_DECLARATION", EQ_INNER_ERROR_DECLARATION);
		}

		if (GT_INNER_ERROR_DECLARATION != null) {
			doc.put("GT_INNER_ERROR_DECLARATION", GT_INNER_ERROR_DECLARATION);
		}

		if (GE_INNER_ERROR_DECLARATION != null) {
			doc.put("GE_INNER_ERROR_DECLARATION", GE_INNER_ERROR_DECLARATION);
		}

		if (LT_INNER_ERROR_DECLARATION != null) {
			doc.put("LT_INNER_ERROR_DECLARATION", LT_INNER_ERROR_DECLARATION);
		}

		if (LE_INNER_ERROR_DECLARATION != null) {
			doc.put("LE_INNER_ERROR_DECLARATION", LE_INNER_ERROR_DECLARATION);
		}

		if (EXISTS_INNER_ERROR_DECLARATION != null) {
			doc.put("EXISTS_INNER_ERROR_DECLARATION", EXISTS_INNER_ERROR_DECLARATION);
		}

		if (EQ_INNER != null) {
			doc.put("EQ_INNER", EQ_INNER);
		}

		if (GT_INNER != null) {
			doc.put("GT_INNER", GT_INNER);
		}

		if (GE_INNER != null) {
			doc.put("GE_INNER", GE_INNER);
		}

		if (LT_INNER != null) {
			doc.put("LT_INNER", LT_INNER);
		}

		if (LE_INNER != null) {
			doc.put("LE_INNER", LE_INNER);
		}

		if (EXISTS_INNER != null) {
			doc.put("EXISTS_INNER", EXISTS_INNER);
		}

		if (EQ_ILMD != null) {
			doc.put("EQ_ILMD", EQ_ILMD);
		}

		if (GT_ILMD != null) {
			doc.put("GT_ILMD", GT_ILMD);
		}

		if (GE_ILMD != null) {
			doc.put("GE_ILMD", GE_ILMD);
		}

		if (LT_ILMD != null) {
			doc.put("LT_ILMD", LT_ILMD);
		}

		if (LE_ILMD != null) {
			doc.put("LE_ILMD", LE_ILMD);
		}

		if (EXISTS_ILMD != null) {
			doc.put("EXISTS_ILMD", EXISTS_ILMD);
		}

		if (EQ_SENSORELEMENT != null) {
			doc.put("EQ_SENSORELEMENT", EQ_SENSORELEMENT);
		}

		if (GT_SENSORELEMENT != null) {
			doc.put("GT_SENSORELEMENT", GT_SENSORELEMENT);
		}

		if (GE_SENSORELEMENT != null) {
			doc.put("GE_SENSORELEMENT", GE_SENSORELEMENT);
		}

		if (LT_SENSORELEMENT != null) {
			doc.put("LT_SENSORELEMENT", LT_SENSORELEMENT);
		}

		if (LE_SENSORELEMENT != null) {
			doc.put("LE_SENSORELEMENT", LE_SENSORELEMENT);
		}

		if (EXISTS_SENSORELEMENT != null) {
			doc.put("EXISTS_SENSORELEMENT", EXISTS_SENSORELEMENT);
		}

		if (EQ_SENSORMETADATA != null) {
			doc.put("EQ_SENSORMETADATA", EQ_SENSORMETADATA);
		}

		if (EQ_SENSORREPORT != null) {
			doc.put("EQ_SENSORREPORT", EQ_SENSORREPORT);
		}

		if (EXISTS_SENSORMETADATA != null) {
			doc.put("EXISTS_SENSORMETADATA", "VoidHolder");
		}

		if (EXISTS_SENSORREPORT != null) {
			doc.put("EXISTS_SENSORREPORT", "VoidHolder");
		}

		if (EQ_readPoint_extension != null) {
			doc.put("EQ_readPoint_extension", EQ_readPoint_extension);
		}

		if (GT_readPoint_extension != null) {
			doc.put("GT_readPoint_extension", GT_readPoint_extension);
		}

		if (GE_readPoint_extension != null) {
			doc.put("GE_readPoint_extension", GE_readPoint_extension);
		}

		if (LT_readPoint_extension != null) {
			doc.put("LT_readPoint_extension", LT_readPoint_extension);
		}

		if (LE_readPoint_extension != null) {
			doc.put("LE_readPoint_extension", LE_readPoint_extension);
		}

		if (EXISTS_readPoint_extension != null) {
			doc.put("EXISTS_readPoint_extension", EXISTS_readPoint_extension);
		}

		if (EQ_bizLocation_extension != null) {
			doc.put("EQ_bizLocation_extension", EQ_bizLocation_extension);
		}

		if (GT_bizLocation_extension != null) {
			doc.put("GT_bizLocation_extension", GT_bizLocation_extension);
		}

		if (GE_bizLocation_extension != null) {
			doc.put("GE_bizLocation_extension", GE_bizLocation_extension);
		}

		if (LT_bizLocation_extension != null) {
			doc.put("LT_bizLocation_extension", LT_bizLocation_extension);
		}

		if (LE_bizLocation_extension != null) {
			doc.put("LE_bizLocation_extension", LE_bizLocation_extension);
		}

		if (EXISTS_bizLocation_extension != null) {
			doc.put("EXISTS_bizLocation_extension", EXISTS_bizLocation_extension);
		}

		if (EQ_ERROR_DECLARATION_extension != null) {
			doc.put("EQ_ERROR_DECLARATION_extension", EQ_ERROR_DECLARATION_extension);
		}

		if (GT_ERROR_DECLARATION_extension != null) {
			doc.put("GT_ERROR_DECLARATION_extension", GT_ERROR_DECLARATION_extension);
		}

		if (GE_ERROR_DECLARATION_extension != null) {
			doc.put("GE_ERROR_DECLARATION_extension", GE_ERROR_DECLARATION_extension);
		}

		if (LT_ERROR_DECLARATION_extension != null) {
			doc.put("LT_ERROR_DECLARATION_extension", LT_ERROR_DECLARATION_extension);
		}

		if (LE_ERROR_DECLARATION_extension != null) {
			doc.put("LE_ERROR_DECLARATION_extension", LE_ERROR_DECLARATION_extension);
		}

		if (EXISTS_ERROR_DECLARATION_extension != null) {
			doc.put("EXISTS_ERROR_DECLARATION_extension", EXISTS_ERROR_DECLARATION_extension);
		}

		if (EQ_extension != null) {
			doc.put("EQ_extension", EQ_extension);
		}

		if (GT_extension != null) {
			doc.put("GT_extension", GT_extension);
		}

		if (GE_extension != null) {
			doc.put("GE_extension", GE_extension);
		}

		if (LT_extension != null) {
			doc.put("LT_extension", LT_extension);
		}

		if (LE_extension != null) {
			doc.put("LE_extension", LE_extension);
		}

		if (EXISTS_extension != null) {
			doc.put("EXISTS_extension", EXISTS_extension);
		}

		if (EQ_type != null) {
			doc.put("EQ_type", EQ_type);
		}

		if (EQ_value != null) {
			doc.put("EQ_value", EQ_value.toMongoDocument());
		}

		if (GT_value != null) {
			doc.put("GT_value", GT_value.toMongoDocument());
		}

		if (GE_value != null) {
			doc.put("GE_value", GE_value.toMongoDocument());
		}

		if (LT_value != null) {
			doc.put("LT_value", LT_value.toMongoDocument());
		}

		if (LE_value != null) {
			doc.put("LE_value", LE_value.toMongoDocument());
		}

		if (EQ_minValue != null) {
			doc.put("EQ_minValue", EQ_minValue.toMongoDocument());
		}

		if (GT_minValue != null) {
			doc.put("GT_minValue", GT_minValue.toMongoDocument());
		}

		if (GE_minValue != null) {
			doc.put("GE_minValue", GE_minValue.toMongoDocument());
		}

		if (LT_minValue != null) {
			doc.put("LT_minValue", LT_minValue.toMongoDocument());
		}

		if (LE_minValue != null) {
			doc.put("LE_minValue", LE_minValue.toMongoDocument());
		}

		if (EQ_maxValue != null) {
			doc.put("EQ_maxValue", EQ_maxValue.toMongoDocument());
		}

		if (GT_maxValue != null) {
			doc.put("GT_maxValue", GT_maxValue.toMongoDocument());
		}

		if (GE_maxValue != null) {
			doc.put("GE_maxValue", GE_maxValue.toMongoDocument());
		}

		if (LT_maxValue != null) {
			doc.put("LT_maxValue", LT_maxValue.toMongoDocument());
		}

		if (LE_maxValue != null) {
			doc.put("LE_maxValue", LE_maxValue.toMongoDocument());
		}

		if (EQ_meanValue != null) {
			doc.put("EQ_meanValue", EQ_meanValue.toMongoDocument());
		}

		if (GT_meanValue != null) {
			doc.put("GT_meanValue", GT_meanValue.toMongoDocument());
		}

		if (GE_meanValue != null) {
			doc.put("GE_meanValue", GE_meanValue.toMongoDocument());
		}

		if (LT_meanValue != null) {
			doc.put("LT_meanValue", LT_meanValue.toMongoDocument());
		}

		if (LE_meanValue != null) {
			doc.put("LE_meanValue", LE_meanValue.toMongoDocument());
		}

		if (EQ_sDev != null) {
			doc.put("EQ_sDev", EQ_sDev.toMongoDocument());
		}

		if (GT_sDev != null) {
			doc.put("GT_sDev", GT_sDev.toMongoDocument());
		}

		if (GE_sDev != null) {
			doc.put("GE_sDev", GE_sDev.toMongoDocument());
		}

		if (LT_sDev != null) {
			doc.put("LT_sDev", LT_sDev.toMongoDocument());
		}

		if (LE_sDev != null) {
			doc.put("LE_sDev", LE_sDev.toMongoDocument());
		}

		if (EQ_percValue != null) {
			doc.put("EQ_percValue", EQ_percValue.toMongoDocument());
		}

		if (GT_percValue != null) {
			doc.put("GT_percValue", GT_percValue.toMongoDocument());
		}

		if (GE_percValue != null) {
			doc.put("GE_percValue", GE_percValue.toMongoDocument());
		}

		if (LT_percValue != null) {
			doc.put("LT_percValue", LT_percValue.toMongoDocument());
		}

		if (LE_percValue != null) {
			doc.put("LE_percValue", LE_percValue.toMongoDocument());
		}

		if (WD_readPoint != null) {
			doc.put("WD_readPoint", WD_readPoint);
		}

		if (WD_bizLocation != null) {
			doc.put("WD_bizLocation", WD_bizLocation);
		}

		if (HASATTR != null) {
			doc.put("HASATTR", HASATTR);
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

class SensorUomValue {
	private String uom;
	private Double value;

	public SensorUomValue(String uom, Double value) {
		this.uom = uom;
		this.value = value;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public Document toMongoDocument() {
		return new Document().append("uom", uom).append("value", value);
	}

	@Override
	public String toString() {
		return uom + "," + value;
	}
}
