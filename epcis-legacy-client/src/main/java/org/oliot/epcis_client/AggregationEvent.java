package org.oliot.epcis_client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.BsonDocument;

/**
 * Copyright (C) 2014-16 Jaewook Byun
 *
 * This project is part of Oliot (oliot.org), pursuing the implementation of
 * Electronic Product Code Information Service(EPCIS) v1.1 specification in
 * EPCglobal.
 * [http://www.gs1.org/gsmp/kc/epcglobal/epcis/epcis_1_1-standard-20140520.pdf]
 * 
 *
 * @author Jaewook Jack Byun, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         bjw0829@kaist.ac.kr, bjw0829@gmail.com
 */
public class AggregationEvent extends EPCISEvent {

	// EventTime, EventTimeZoneOffset,Action required
	private String action;
	private String parentID;
	private List<String> childEPCs;
	private List<QuantityElement> childQuantityList;
	private Map<String, List<String>> bizTransactionList;

	public AggregationEvent() {
		super();

		action = "OBSERVE";
		childEPCs = new ArrayList<String>();
		childQuantityList = new ArrayList<QuantityElement>();
		bizTransactionList = new HashMap<String, List<String>>();
	}

	public AggregationEvent(long eventTime, String eventTimeZoneOffset, String action) {
		super(eventTime, eventTimeZoneOffset);

		this.action = action;
		childEPCs = new ArrayList<String>();
		childQuantityList = new ArrayList<QuantityElement>();
		bizTransactionList = new HashMap<String, List<String>>();
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}



	public Map<String, List<String>> getBizTransactionList() {
		return bizTransactionList;
	}

	public void setBizTransactionList(Map<String, List<String>> bizTransactionList) {
		this.bizTransactionList = bizTransactionList;
	}



	public String getParentID() {
		return parentID;
	}

	public void setParentID(String parentID) {
		this.parentID = parentID;
	}

	public List<String> getChildEPCs() {
		return childEPCs;
	}

	public void setChildEPCs(List<String> childEPCs) {
		this.childEPCs = childEPCs;
	}

	public List<QuantityElement> getChildQuantityList() {
		return childQuantityList;
	}

	public void setChildQuantityList(List<QuantityElement> childQuantityList) {
		this.childQuantityList = childQuantityList;
	}

	public BsonDocument asBsonDocument() {
		CaptureUtil util = new CaptureUtil();

		BsonDocument aggregationEvent = super.asBsonDocument();
		// Required Fields
		aggregationEvent = util.putAction(aggregationEvent, action);

		// Optional Fields
		if (this.parentID != null) {
			aggregationEvent = util.putParentID(aggregationEvent, parentID);
		}
		if (this.childEPCs != null && this.childEPCs.size() != 0) {
			aggregationEvent = util.putChildEPCs(aggregationEvent, childEPCs);
		}
		if (this.bizTransactionList != null && this.bizTransactionList.isEmpty() == false) {
			aggregationEvent = util.putBizTransactionList(aggregationEvent, bizTransactionList);
		}

		BsonDocument extension = asExtensionBsonDocument(util);
		if (this.childQuantityList != null && this.childQuantityList.isEmpty() == false) {
			extension = util.putChildQuantityList(extension, childQuantityList);
		}
		if (extension.isEmpty() == false)
			aggregationEvent.put("extension", extension);

		return aggregationEvent;
	}
}
