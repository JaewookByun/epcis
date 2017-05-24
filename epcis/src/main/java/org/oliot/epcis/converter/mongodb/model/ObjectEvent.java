package org.oliot.epcis.converter.mongodb.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.BsonDocument;
import org.bson.BsonValue;

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
public class ObjectEvent extends EPCISEvent {

	// EventTime, EventTimeZoneOffset,Action required

	private List<String> epcList;
	private List<QuantityElement> quantityList;
	private String action;
	private String bizStep;
	private String disposition;
	private String readPoint;
	private String bizLocation;
	private Map<String, List<String>> bizTransactionList;
	private Map<String, List<String>> sourceList;
	private Map<String, List<String>> destinationList;
	private Map<String, String> namespaces;
	private BsonDocument ilmds;
	private BsonDocument extensions;

	public ObjectEvent() {
		super();
		action = "OBSERVE";
		epcList = new ArrayList<String>();
		quantityList = new ArrayList<QuantityElement>();
		bizTransactionList = new HashMap<String, List<String>>();
		sourceList = new HashMap<String, List<String>>();
		destinationList = new HashMap<String, List<String>>();
		namespaces = new HashMap<String, String>();
		ilmds = new BsonDocument();
		extensions = new BsonDocument();
	}

	public ObjectEvent(long eventTime, String eventTimeZoneOffset, String action) {
		super(eventTime, eventTimeZoneOffset);

		this.action = action;
		epcList = new ArrayList<String>();
		quantityList = new ArrayList<QuantityElement>();
		bizTransactionList = new HashMap<String, List<String>>();
		sourceList = new HashMap<String, List<String>>();
		destinationList = new HashMap<String, List<String>>();
		namespaces = new HashMap<String, String>();
		ilmds = new BsonDocument();
		extensions = new BsonDocument();
	}

	public List<String> getEpcList() {
		return epcList;
	}

	public void setEpcList(List<String> epcList) {
		this.epcList = epcList;
	}

	public List<QuantityElement> getQuantityList() {
		return quantityList;
	}

	public void setQuantityList(List<QuantityElement> quantityList) {
		this.quantityList = quantityList;
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

	public void setNamespaces(Map<String, String> namespaces) {
		this.namespaces = namespaces;
	}

	public Map<String, BsonValue> getIlmd() {
		return ilmds;
	}

	public void setIlmd(BsonDocument ilmds) {
		this.ilmds = ilmds;
	}

	public Map<String, BsonValue> getExtensions() {
		return extensions;
	}

	public void setExtensions(BsonDocument extensions) {
		this.extensions = extensions;
	}

	public BsonDocument asBsonDocument() {
		CaptureUtil util = new CaptureUtil();

		BsonDocument objectEvent = super.asBsonDocument();
		// Required Fields
		objectEvent = util.putEventType(objectEvent, "ObjectEvent");
		objectEvent = util.putAction(objectEvent, action);

		// Optional Fields
		if (this.epcList != null && this.epcList.size() != 0) {
			objectEvent = util.putEPCList(objectEvent, epcList);
		}
		if (this.bizStep != null) {
			objectEvent = util.putBizStep(objectEvent, bizStep);
		}
		if (this.disposition != null) {
			objectEvent = util.putDisposition(objectEvent, disposition);
		}
		if (this.readPoint != null) {
			objectEvent = util.putReadPoint(objectEvent, readPoint);
		}
		if (this.bizLocation != null) {
			objectEvent = util.putBizLocation(objectEvent, bizLocation);
		}
		if (this.bizTransactionList != null && this.bizTransactionList.isEmpty() == false) {
			objectEvent = util.putBizTransactionList(objectEvent, bizTransactionList);
		}
		if (this.extensions != null && this.extensions.isEmpty() == false) {
			objectEvent = util.putExtensions(objectEvent, namespaces, extensions);
		}

		BsonDocument extension = new BsonDocument();
		if (this.quantityList != null && this.quantityList.isEmpty() == false) {
			extension = util.putQuantityList(extension, quantityList);
		}
		if (this.sourceList != null && this.sourceList.isEmpty() == false) {
			extension = util.putSourceList(extension, sourceList);
		}
		if (this.destinationList != null && this.destinationList.isEmpty() == false) {
			extension = util.putDestinationList(extension, destinationList);
		}
		if (this.ilmds != null && this.ilmds.isEmpty() == false) {
			extension = util.putILMD(extension, namespaces, ilmds);
		}
		if (extension.isEmpty() == false)
			objectEvent.put("extension", extension);

		return objectEvent;
	}
}
