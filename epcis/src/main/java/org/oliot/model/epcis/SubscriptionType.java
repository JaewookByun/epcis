package org.oliot.model.epcis;

import java.util.HashMap;
import java.util.Map;

import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonString;

public class SubscriptionType {
	private String queryName;
	private String subscriptionID;
	private String dest;
	private String cronExpression;
	private boolean ignoreReceivedEvent;
	private boolean reportIfEmpty;
	private String initialRecordTime;
	private String GE_eventTime;
	private String LT_eventTime;
	private String GE_recordTime;
	private String LT_recordTime;
	private String EQ_action;
	private String EQ_bizStep;
	private String EQ_disposition;
	private String EQ_readPoint;
	private String WD_readPoint;
	private String EQ_bizLocation;
	private String WD_bizLocation;
	private String EQ_transformationID;
	private String MATCH_epc;
	private String MATCH_parentID;
	private String MATCH_inputEPC;
	private String MATCH_outputEPC;
	private String MATCH_anyEPC;
	private String MATCH_epcClass;
	private String MATCH_inputEPCClass;
	private String MATCH_outputEPCClass;
	private String MATCH_anyEPCClass;
	private String EQ_quantity;
	private String GT_quantity;
	private String GE_quantity;
	private String LT_quantity;
	private String LE_quantity;
	private String orderBy;
	private String orderDirection;
	private String eventCountLimit;
	private String maxEventCount;
	private String format;
	private Map<String, String> paramMap;

	public SubscriptionType() {
	}

	public SubscriptionType(BsonDocument doc) {
		if (doc.containsKey("queryName"))
			this.queryName = doc.getString("queryName").getValue();
		this.subscriptionID = doc.getString("subscriptionID").getValue();
		this.dest = doc.getString("dest").getValue();
		this.cronExpression = doc.getString("cronExpression").getValue();
		if (doc.containsKey("ignoreReceivedEvent"))
			this.ignoreReceivedEvent = doc.getBoolean("ignoreReceivedEvent").getValue();
		if (doc.containsKey("reportIfEmpty"))
			this.reportIfEmpty = doc.getBoolean("reportIfEmpty").getValue();
		if (doc.containsKey("initialRecordTime"))
			this.initialRecordTime = doc.getString(initialRecordTime).getValue();
		if (doc.containsKey("eventType"))
			this.eventType = doc.getString("eventType").getValue();
		if (doc.containsKey("GE_eventTime"))
			this.GE_eventTime = doc.getString("GE_eventTime").getValue();
		if (doc.containsKey("LT_eventTime"))
			this.LT_eventTime = doc.getString("LT_eventTime").getValue();
		if (doc.containsKey("GE_recordTime"))
			this.GE_recordTime = doc.getString("GE_recordTime").getValue();
		if (doc.containsKey("LT_recordTime"))
			this.LT_recordTime = doc.getString("LT_recordTime").getValue();
		if (doc.containsKey("EQ_action"))
			this.EQ_action = doc.getString("EQ_action").getValue();
		if (doc.containsKey("EQ_bizStep"))
			this.EQ_bizStep = doc.getString("EQ_bizStep").getValue();
		if (doc.containsKey("EQ_disposition"))
			this.EQ_disposition = doc.getString("EQ_disposition").getValue();
		if (doc.containsKey("WD_bizLocation"))
			this.WD_bizLocation = doc.getString("WD_bizLocation").getValue();
		if (doc.containsKey("EQ_transformationID"))
			this.EQ_transformationID = doc.getString("EQ_transformationID").getValue();
		if (doc.containsKey("MATCH_epc"))
			this.MATCH_epc = doc.getString("MATCH_epc").getValue();
		if (doc.containsKey("MATCH_parentID"))
			this.MATCH_parentID = doc.getString("MATCH_parentID").getValue();
		if (doc.containsKey("MATCH_inputEPC"))
			this.MATCH_inputEPC = doc.getString("MATCH_inputEPC").getValue();
		if (doc.containsKey("MATCH_outputEPC"))
			this.MATCH_outputEPC = doc.getString("MATCH_outputEPC").getValue();
		if (doc.containsKey("MATCH_anyEPC"))
			this.MATCH_anyEPC = doc.getString("MATCH_anyEPC").getValue();
		if (doc.containsKey("MATCH_epcClass"))
			this.MATCH_epcClass = doc.getString("MATCH_epcClass").getValue();
		if (doc.containsKey("MATCH_anyEPCClass"))
			this.MATCH_anyEPCClass = doc.getString("MATCH_anyEPCClass").getValue();
		if (doc.containsKey("EQ_quantity"))
			this.EQ_quantity = doc.getString("EQ_quantity").getValue();
		if (doc.containsKey("GT_quantity"))
			this.GT_quantity = doc.getString("GT_quantity").getValue();
		if (doc.containsKey("GE_quantity"))
			this.GE_quantity = doc.getString("GE_quantity").getValue();
		if (doc.containsKey("LT_quantity"))
			this.LT_quantity = doc.getString("LT_quantity").getValue();
		if (doc.containsKey("LE_quantity"))
			this.LE_quantity = doc.getString("LE_quantity").getValue();
		if (doc.containsKey("orderBy"))
			this.orderBy = doc.getString("orderBy").getValue();
		if (doc.containsKey("orderDirection"))
			this.orderDirection = doc.getString("orderDirection").getValue();
		if (doc.containsKey("eventCountLimit"))
			this.eventCountLimit = doc.getString("eventCountLimit").getValue();
		if (doc.containsKey("maxEventCount"))
			this.maxEventCount = doc.getString("maxEventCount").getValue();
		if (doc.containsKey("format"))
			this.format = doc.getString("format").getValue();
		if (doc.containsKey("paramMap")) {
			Map<String, String> paramMap = new HashMap<String, String>();
			BsonDocument bsonParam = doc.getDocument("paramMap");
			for (String key : bsonParam.keySet()) {
				paramMap.put(key, bsonParam.getString(key).getValue());
			}
			if (bsonParam.isEmpty() == false)
				this.paramMap = paramMap;
		}
	}

	public SubscriptionType(String queryName, String subscriptionID, String dest, String cronExpression,
			boolean ignoreReceivedEvent, boolean reportIfEmpty, String initialRecordTime, String eventType,
			String GE_eventTime, String LT_eventTime, String GE_recordTime, String LT_recordTime, String EQ_action,
			String EQ_bizStep, String EQ_disposition, String EQ_readPoint, String WD_readPoint, String EQ_bizLocation,
			String WD_bizLocation, String EQ_transformationID, String MATCH_epc, String MATCH_parentID,
			String MATCH_inputEPC, String MATCH_outputEPC, String MATCH_anyEPC, String MATCH_epcClass,
			String MATCH_inputEPCClass, String MATCH_outputEPCClass, String MATCH_anyEPCClass, String EQ_quantity,
			String GT_quantity, String GE_quantity, String LT_quantity, String LE_quantity, String orderBy,
			String orderDirection, String eventCountLimit, String maxEventCount, String format,
			Map<String, String> paramMap) {
		this.queryName = queryName;
		this.subscriptionID = subscriptionID;
		this.dest = dest;
		this.cronExpression = cronExpression;
		this.ignoreReceivedEvent = ignoreReceivedEvent;
		this.reportIfEmpty = reportIfEmpty;
		this.initialRecordTime = initialRecordTime;
		this.eventType = eventType;
		this.GE_eventTime = GE_eventTime;
		this.LT_eventTime = LT_eventTime;
		this.GE_recordTime = GE_recordTime;
		this.LT_recordTime = LT_recordTime;
		this.EQ_action = EQ_action;
		this.EQ_bizStep = EQ_bizStep;
		this.EQ_disposition = EQ_disposition;
		this.WD_bizLocation = WD_bizLocation;
		this.EQ_transformationID = EQ_transformationID;
		this.MATCH_epc = MATCH_epc;
		this.MATCH_parentID = MATCH_parentID;
		this.MATCH_inputEPC = MATCH_inputEPC;
		this.MATCH_outputEPC = MATCH_outputEPC;
		this.MATCH_anyEPC = MATCH_anyEPC;
		this.MATCH_epcClass = MATCH_epcClass;
		this.MATCH_anyEPCClass = MATCH_anyEPCClass;
		this.EQ_quantity = EQ_quantity;
		this.GT_quantity = GT_quantity;
		this.GE_quantity = GE_quantity;
		this.LT_quantity = LT_quantity;
		this.LE_quantity = LE_quantity;
		this.orderBy = orderBy;
		this.orderDirection = orderDirection;
		this.eventCountLimit = eventCountLimit;
		this.maxEventCount = maxEventCount;
		this.format = format;
		this.paramMap = paramMap;
	}

	public static BsonDocument asBsonDocument(SubscriptionType subscription) {
		BsonDocument bson = new BsonDocument();
		if (subscription.getQueryName() != null) {
			bson.put("queryName", new BsonString(subscription.getQueryName()));
		}
		if (subscription.getSubscriptionID() != null) {
			bson.put("subscriptionID", new BsonString(subscription.getSubscriptionID()));
		}
		if (subscription.getDest() != null) {
			bson.put("dest", new BsonString(subscription.getDest()));
		}
		if (subscription.getCronExpression() != null) {
			bson.put("cronExpression", new BsonString(subscription.getCronExpression()));
		}
		bson.put("ignoreReceivedEvent", new BsonBoolean(subscription.isIgnoreReceivedEvent()));
		bson.put("reportIfEmpty", new BsonBoolean(subscription.isReportIfEmpty()));

		if (subscription.getInitialRecordTime() != null) {
			bson.put("initialRecordTime", new BsonString(subscription.getInitialRecordTime()));
		}
		if (subscription.getEventType() != null) {
			bson.put("eventType", new BsonString(subscription.getEventType()));
		}
		if (subscription.getGE_eventTime() != null) {
			bson.put("GE_eventTime", new BsonString(subscription.getGE_eventTime()));
		}
		if (subscription.getLT_eventTime() != null) {
			bson.put("LT_eventTime", new BsonString(subscription.getLT_eventTime()));
		}
		if (subscription.getGE_recordTime() != null) {
			bson.put("GE_recordTime", new BsonString(subscription.getGE_recordTime()));
		}
		if (subscription.getLT_recordTime() != null) {
			bson.put("LT_recordTime", new BsonString(subscription.getLT_recordTime()));
		}
		if (subscription.getEQ_action() != null) {
			bson.put("EQ_action", new BsonString(subscription.getEQ_action()));
		}

		if (subscription.getEQ_bizStep() != null) {
			bson.put("EQ_bizStep", new BsonString(subscription.getEQ_bizStep()));
		}
		if (subscription.getEQ_disposition() != null) {
			bson.put("EQ_disposition", new BsonString(subscription.getEQ_disposition()));
		}
		if (subscription.getWD_bizLocation() != null) {
			bson.put("WD_bizLocation", new BsonString(subscription.getWD_bizLocation()));
		}
		if (subscription.getEQ_transformationID() != null) {
			bson.put("EQ_transformationID", new BsonString(subscription.getEQ_transformationID()));
		}
		if (subscription.getMATCH_epc() != null) {
			bson.put("MATCH_epc", new BsonString(subscription.getMATCH_epc()));
		}
		if (subscription.getMATCH_parentID() != null) {
			bson.put("MATCH_parentID", new BsonString(subscription.getMATCH_parentID()));
		}
		if (subscription.getMATCH_inputEPC() != null) {
			bson.put("MATCH_inputEPC", new BsonString(subscription.getMATCH_inputEPC()));
		}

		if (subscription.getMATCH_outputEPC() != null) {
			bson.put("MATCH_outputEPC", new BsonString(subscription.getMATCH_outputEPC()));
		}
		if (subscription.getMATCH_anyEPC() != null) {
			bson.put("MATCH_anyEPC", new BsonString(subscription.getMATCH_anyEPC()));
		}
		if (subscription.getMATCH_epcClass() != null) {
			bson.put("MATCH_epcClass", new BsonString(subscription.getMATCH_epcClass()));
		}
		if (subscription.getMATCH_anyEPCClass() != null) {
			bson.put("MATCH_anyEPCClass", new BsonString(subscription.getMATCH_anyEPCClass()));
		}
		if (subscription.getMATCH_inputEPC() != null) {
			bson.put("MATCH_inputEPC", new BsonString(subscription.getMATCH_inputEPC()));
		}
		if (subscription.getMATCH_inputEPC() != null) {
			bson.put("MATCH_inputEPC", new BsonString(subscription.getMATCH_inputEPC()));
		}

		if (subscription.getEQ_quantity() != null) {
			bson.put("EQ_quantity", new BsonString(subscription.getEQ_quantity()));
		}
		if (subscription.getGT_quantity() != null) {
			bson.put("GT_quantity", new BsonString(subscription.getGT_quantity()));
		}
		if (subscription.getGE_quantity() != null) {
			bson.put("GE_quantity", new BsonString(subscription.getGE_quantity()));
		}
		if (subscription.getLT_quantity() != null) {
			bson.put("LT_quantity", new BsonString(subscription.getLT_quantity()));
		}
		if (subscription.getLE_quantity() != null) {
			bson.put("LE_quantity", new BsonString(subscription.getLE_quantity()));
		}

		if (subscription.getOrderBy() != null) {
			bson.put("orderBy", new BsonString(subscription.getOrderBy()));
		}
		if (subscription.getOrderDirection() != null) {
			bson.put("orderDirection", new BsonString(subscription.getOrderDirection()));
		}
		if (subscription.getEventCountLimit() != null) {
			bson.put("eventCountLimit", new BsonString(subscription.getEventCountLimit()));
		}
		if (subscription.getMaxEventCount() != null) {
			bson.put("maxEventCount", new BsonString(subscription.getMaxEventCount()));
		}
		if (subscription.getFormat() != null) {
			bson.put("format", new BsonString(subscription.getFormat()));
		}
		if (subscription.getParamMap() != null && subscription.getParamMap().isEmpty() == false) {
			BsonDocument paramMap = new BsonDocument();
			for (String key : subscription.getParamMap().keySet()) {
				String value = subscription.getParamMap().get(key).toString();
				paramMap.put(key, new BsonString(value));
			}
			bson.put("paramMap", paramMap);
		}
		return bson;
	}

	public boolean isIgnoreReceivedEvent() {
		return ignoreReceivedEvent;
	}

	public void setIgnoreReceivedEvent(boolean ignoreReceivedEvent) {
		this.ignoreReceivedEvent = ignoreReceivedEvent;
	}

	public String getInitialRecordTime() {
		return initialRecordTime;
	}

	public void setInitialRecordTime(String initialRecordTime) {
		this.initialRecordTime = initialRecordTime;
	}

	private String eventType;

	public boolean isReportIfEmpty() {
		return reportIfEmpty;
	}

	public void setReportIfEmpty(boolean reportIfEmpty) {
		this.reportIfEmpty = reportIfEmpty;
	}

	public Map<String, String> getParamMap() {
		return paramMap;
	}

	public void setParamMap(Map<String, String> paramMap) {
		this.paramMap = paramMap;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getQueryName() {
		return queryName;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}

	public String getSubscriptionID() {
		return subscriptionID;
	}

	public void setSubscriptionID(String subscriptionID) {
		this.subscriptionID = subscriptionID;
	}

	public String getDest() {
		return dest;
	}

	public void setDest(String dest) {
		this.dest = dest;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getGE_eventTime() {
		return GE_eventTime;
	}

	public void setGE_eventTime(String gE_eventTime) {
		GE_eventTime = gE_eventTime;
	}

	public String getLT_eventTime() {
		return LT_eventTime;
	}

	public void setLT_eventTime(String lT_eventTime) {
		LT_eventTime = lT_eventTime;
	}

	public String getGE_recordTime() {
		return GE_recordTime;
	}

	public void setGE_recordTime(String gE_recordTime) {
		GE_recordTime = gE_recordTime;
	}

	public String getLT_recordTime() {
		return LT_recordTime;
	}

	public void setLT_recordTime(String lT_recordTime) {
		LT_recordTime = lT_recordTime;
	}

	public String getEQ_action() {
		return EQ_action;
	}

	public void setEQ_action(String eQ_action) {
		EQ_action = eQ_action;
	}

	public String getEQ_bizStep() {
		return EQ_bizStep;
	}

	public void setEQ_bizStep(String eQ_bizStep) {
		EQ_bizStep = eQ_bizStep;
	}

	public String getEQ_disposition() {
		return EQ_disposition;
	}

	public void setEQ_disposition(String eQ_disposition) {
		EQ_disposition = eQ_disposition;
	}

	public String getEQ_readPoint() {
		return EQ_readPoint;
	}

	public void setEQ_readPoint(String eQ_readPoint) {
		EQ_readPoint = eQ_readPoint;
	}

	public String getWD_readPoint() {
		return WD_readPoint;
	}

	public void setWD_readPoint(String wD_readPoint) {
		WD_readPoint = wD_readPoint;
	}

	public String getEQ_bizLocation() {
		return EQ_bizLocation;
	}

	public void setEQ_bizLocation(String eQ_bizLocation) {
		EQ_bizLocation = eQ_bizLocation;
	}

	public String getWD_bizLocation() {
		return WD_bizLocation;
	}

	public void setWD_bizLocation(String wD_bizLocation) {
		WD_bizLocation = wD_bizLocation;
	}

	public String getEQ_transformationID() {
		return EQ_transformationID;
	}

	public void setEQ_transformationID(String eQ_transformationID) {
		EQ_transformationID = eQ_transformationID;
	}

	public String getMATCH_epc() {
		return MATCH_epc;
	}

	public void setMATCH_epc(String mATCH_epc) {
		MATCH_epc = mATCH_epc;
	}

	public String getMATCH_parentID() {
		return MATCH_parentID;
	}

	public void setMATCH_parentID(String mATCH_parentID) {
		MATCH_parentID = mATCH_parentID;
	}

	public String getMATCH_inputEPC() {
		return MATCH_inputEPC;
	}

	public void setMATCH_inputEPC(String mATCH_inputEPC) {
		MATCH_inputEPC = mATCH_inputEPC;
	}

	public String getMATCH_outputEPC() {
		return MATCH_outputEPC;
	}

	public void setMATCH_outputEPC(String mATCH_outputEPC) {
		MATCH_outputEPC = mATCH_outputEPC;
	}

	public String getMATCH_anyEPC() {
		return MATCH_anyEPC;
	}

	public void setMATCH_anyEPC(String mATCH_anyEPC) {
		MATCH_anyEPC = mATCH_anyEPC;
	}

	public String getMATCH_epcClass() {
		return MATCH_epcClass;
	}

	public void setMATCH_epcClass(String mATCH_epcClass) {
		MATCH_epcClass = mATCH_epcClass;
	}

	public String getMATCH_inputEPCClass() {
		return MATCH_inputEPCClass;
	}

	public void setMATCH_inputEPCClass(String mATCH_inputEPCClass) {
		MATCH_inputEPCClass = mATCH_inputEPCClass;
	}

	public String getMATCH_outputEPCClass() {
		return MATCH_outputEPCClass;
	}

	public void setMATCH_outputEPCClass(String mATCH_outputEPCClass) {
		MATCH_outputEPCClass = mATCH_outputEPCClass;
	}

	public String getMATCH_anyEPCClass() {
		return MATCH_anyEPCClass;
	}

	public void setMATCH_anyEPCClass(String mATCH_anyEPCClass) {
		MATCH_anyEPCClass = mATCH_anyEPCClass;
	}

	public String getEQ_quantity() {
		return EQ_quantity;
	}

	public void setEQ_quantity(String eQ_quantity) {
		EQ_quantity = eQ_quantity;
	}

	public String getGT_quantity() {
		return GT_quantity;
	}

	public void setGT_quantity(String gT_quantity) {
		GT_quantity = gT_quantity;
	}

	public String getGE_quantity() {
		return GE_quantity;
	}

	public void setGE_quantity(String gE_quantity) {
		GE_quantity = gE_quantity;
	}

	public String getLT_quantity() {
		return LT_quantity;
	}

	public void setLT_quantity(String lT_quantity) {
		LT_quantity = lT_quantity;
	}

	public String getLE_quantity() {
		return LE_quantity;
	}

	public void setLE_quantity(String lE_quantity) {
		LE_quantity = lE_quantity;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String getOrderDirection() {
		return orderDirection;
	}

	public void setOrderDirection(String orderDirection) {
		this.orderDirection = orderDirection;
	}

	public String getEventCountLimit() {
		return eventCountLimit;
	}

	public void setEventCountLimit(String eventCountLimit) {
		this.eventCountLimit = eventCountLimit;
	}

	public String getMaxEventCount() {
		return maxEventCount;
	}

	public void setMaxEventCount(String maxEventCount) {
		this.maxEventCount = maxEventCount;
	}

}
