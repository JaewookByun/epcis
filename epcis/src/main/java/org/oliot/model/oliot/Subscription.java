package org.oliot.model.oliot;

import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.springframework.data.mongodb.core.mapping.Document;
@Entity
@Document(collection = "Subscription")
public class Subscription {
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;
	private String queryName;
	private String subscriptionID;
	private String dest;
	private String cronExpression;
	private boolean reportIfEmpty;
	private String eventType;

	public boolean isReportIfEmpty() {
		return reportIfEmpty;
	}

	public void setReportIfEmpty(boolean reportIfEmpty) {
		this.reportIfEmpty = reportIfEmpty;
	}

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
	@Transient
	private Map<String, String> paramMap;

	public Map<String, String> getParamMap() {
		return paramMap;
	}

	public void setParamMap(Map<String, String> paramMap) {
		this.paramMap = paramMap;
	}

	public Subscription(String queryName, String subscriptionID,
			String dest, String cronExpression, String eventType,
			String GE_eventTime, String LT_eventTime, String GE_recordTime,
			String LT_recordTime, String EQ_action, String EQ_bizStep,
			String EQ_disposition, String EQ_readPoint, String WD_readPoint,
			String EQ_bizLocation, String WD_bizLocation,
			String EQ_transformationID, String MATCH_epc,
			String MATCH_parentID, String MATCH_inputEPC,
			String MATCH_outputEPC, String MATCH_anyEPC, String MATCH_epcClass,
			String MATCH_inputEPCClass, String MATCH_outputEPCClass,
			String MATCH_anyEPCClass, String EQ_quantity, String GT_quantity,
			String GE_quantity, String LT_quantity, String LE_quantity,
			String orderBy, String orderDirection, String eventCountLimit,
			String maxEventCount, Map<String, String> paramMap) {
		this.queryName = queryName;
		this.subscriptionID = subscriptionID;
		this.dest = dest;
		this.cronExpression = cronExpression;
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
		this.paramMap = paramMap;
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

	public Subscription() {
		super();
	}

}
