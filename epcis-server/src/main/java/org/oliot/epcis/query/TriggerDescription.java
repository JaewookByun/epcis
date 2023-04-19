package org.oliot.epcis.query;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import org.bson.Document;
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

	private List<String> EQ_type;
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

	// TODO:
	public boolean isPass(Document doc) {
		if (eventType != null) {
			if (!doc.containsKey("type"))
				return false;
			else if (!eventType.contains(doc.getString("type")))
				return false;
		}
		if (GE_eventTime != null) {
			if (!doc.containsKey("eventTime"))
				return false;
			else if (doc.getLong("eventTime") < GE_eventTime)
				return false;
		}
		if (LT_eventTime != null) {
			if (!doc.containsKey("eventTime"))
				return false;
			else if (doc.getLong("eventTime") >= LT_eventTime)
				return false;
		}
		if (GE_recordTime != null) {
			if (!doc.containsKey("recordTime"))
				return false;
			else if (doc.getLong("recordTime") < GE_recordTime)
				return false;
		}
		if (LT_recordTime != null) {
			if (!doc.containsKey("recordTime"))
				return false;
			else if (doc.getLong("recordTime") >= LT_recordTime)
				return false;
		}

		if (EQ_action != null) {
			if (!doc.containsKey("action"))
				return false;
			else if (!EQ_action.contains(doc.getString("action")))
				return false;
		}

		if (EQ_bizStep != null) {
			if (!doc.containsKey("bizStep"))
				return false;
			else if (!EQ_bizStep.contains(doc.getString("bizStep")))
				return false;
		}

		if (EQ_disposition != null) {
			if (!doc.containsKey("disposition"))
				return false;
			else if (!EQ_disposition.contains(doc.getString("disposition")))
				return false;
		}

		if (EQ_persistentDisposition_set != null) {
			try {
				if (Collections.disjoint(EQ_persistentDisposition_set,
						doc.get("persistentDisposition", Document.class).getList("set", String.class)))
					return false;
			} catch (NullPointerException e) {
				return false;
			}
		}

		if (EQ_persistentDisposition_unset != null) {
			try {
				if (Collections.disjoint(EQ_persistentDisposition_unset,
						doc.get("persistentDisposition", Document.class).getList("unset", String.class)))
					return false;
			} catch (NullPointerException e) {
				return false;
			}
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
