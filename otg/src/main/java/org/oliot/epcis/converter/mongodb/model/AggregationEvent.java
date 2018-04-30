package org.oliot.epcis.converter.mongodb.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.BsonDocument;
import org.bson.BsonString;

/**
 * Copyright (C) 2014-17 Jaewook Byun
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

	private String bizStep;
	private String disposition;
	private String readPoint;
	private String bizLocation;
	private Map<String, List<String>> bizTransactionList;
	private Map<String, List<String>> sourceList;
	private Map<String, List<String>> destinationList;
	private Map<String, String> namespaces;

	private BsonDocument extensions;

	public AggregationEvent() {
		super();

		action = "OBSERVE";
		childEPCs = new ArrayList<String>();
		childQuantityList = new ArrayList<QuantityElement>();
		bizTransactionList = new HashMap<String, List<String>>();
		sourceList = new HashMap<String, List<String>>();
		destinationList = new HashMap<String, List<String>>();
		namespaces = new HashMap<String, String>();
		extensions = new BsonDocument();
	}

	public AggregationEvent(long eventTime, String eventTimeZoneOffset, String action) {
		super(eventTime, eventTimeZoneOffset);

		this.action = action;
		childEPCs = new ArrayList<String>();
		childQuantityList = new ArrayList<QuantityElement>();
		bizTransactionList = new HashMap<String, List<String>>();
		sourceList = new HashMap<String, List<String>>();
		destinationList = new HashMap<String, List<String>>();
		namespaces = new HashMap<String, String>();
		extensions = new BsonDocument();
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getBizStep() {
		return bizStep;
	}

	public void setBizStep(String bizStep) {
		this.bizStep = bizStep;
	}

	public String getDisposition() {
		return disposition;
	}

	public void setDisposition(String disposition) {
		this.disposition = disposition;
	}

	public String getReadPoint() {
		return readPoint;
	}

	public void setReadPoint(String readPoint) {
		this.readPoint = readPoint;
	}

	public String getBizLocation() {
		return bizLocation;
	}

	public void setBizLocation(String bizLocation) {
		this.bizLocation = bizLocation;
	}

	public Map<String, List<String>> getBizTransactionList() {
		return bizTransactionList;
	}

	public void setBizTransactionList(Map<String, List<String>> bizTransactionList) {
		this.bizTransactionList = bizTransactionList;
	}

	public Map<String, List<String>> getSourceList() {
		return sourceList;
	}

	public void setSourceList(Map<String, List<String>> sourceList) {
		this.sourceList = sourceList;
	}

	public Map<String, List<String>> getDestinationList() {
		return destinationList;
	}

	public void setDestinationList(Map<String, List<String>> destinationList) {
		this.destinationList = destinationList;
	}

	public Map<String, String> getNamespaces() {
		return namespaces;
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

	public void setNamespaces(Map<String, String> namespaces) {
		this.namespaces = namespaces;
	}

	public BsonDocument getExtensions() {
		return extensions;
	}

	public void setExtensions(BsonDocument extensions) {
		this.extensions = extensions;
	}

	public BsonDocument asBsonDocument() {
		CaptureUtil util = new CaptureUtil();

		BsonDocument aggregationEvent = super.asBsonDocument();
		aggregationEvent.put("eventType", new BsonString("AggregationEvent"));
		// Required Fields
		aggregationEvent = util.putAction(aggregationEvent, action);

		// Optional Fields
		if (this.parentID != null) {
			aggregationEvent = util.putParentID(aggregationEvent, parentID);
		}
		if (this.childEPCs != null && this.childEPCs.size() != 0) {
			aggregationEvent = util.putChildEPCs(aggregationEvent, childEPCs);
		}
		if (this.bizStep != null) {
			aggregationEvent = util.putBizStep(aggregationEvent, bizStep);
		}
		if (this.disposition != null) {
			aggregationEvent = util.putDisposition(aggregationEvent, disposition);
		}
		if (this.readPoint != null) {
			aggregationEvent = util.putReadPoint(aggregationEvent, readPoint);
		}
		if (this.bizLocation != null) {
			aggregationEvent = util.putBizLocation(aggregationEvent, bizLocation);
		}
		if (this.bizTransactionList != null && this.bizTransactionList.isEmpty() == false) {
			aggregationEvent = util.putBizTransactionList(aggregationEvent, bizTransactionList);
		}
		if (this.extensions != null && this.extensions.isEmpty() == false) {
			aggregationEvent = util.putExtensions(aggregationEvent, namespaces, extensions);
		}

		BsonDocument extension = new BsonDocument();
		if (this.childQuantityList != null && this.childQuantityList.isEmpty() == false) {
			extension = util.putChildQuantityList(extension, childQuantityList);
		}
		if (this.sourceList != null && this.sourceList.isEmpty() == false) {
			extension = util.putSourceList(extension, sourceList);
		}
		if (this.destinationList != null && this.destinationList.isEmpty() == false) {
			extension = util.putDestinationList(extension, destinationList);
		}
		if (extension.isEmpty() == false)
			aggregationEvent.put("extension", extension);

		return aggregationEvent;
	}
}
