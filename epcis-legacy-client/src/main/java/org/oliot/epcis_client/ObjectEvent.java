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
public class ObjectEvent extends EPCISEvent {

	// EventTime, EventTimeZoneOffset,Action required
	private List<String> epcList;
	private List<QuantityElement> quantityList;
	private String action;
	private Map<String, List<String>> bizTransactionList;
	private Map<String, Map<String, Object>> ilmds;

	public ObjectEvent() {
		super();

		action = "OBSERVE";
		epcList = new ArrayList<String>();
		quantityList = new ArrayList<QuantityElement>();
		bizTransactionList = new HashMap<String, List<String>>();
		ilmds = new HashMap<String, Map<String, Object>>();
	}

	public ObjectEvent(long eventTime, String eventTimeZoneOffset, String action) {
		super(eventTime, eventTimeZoneOffset);

		this.action = action;
		epcList = new ArrayList<String>();
		quantityList = new ArrayList<QuantityElement>();
		bizTransactionList = new HashMap<String, List<String>>();
		ilmds = new HashMap<String, Map<String, Object>>();
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

	public Map<String, List<String>> getBizTransactionList() {
		return bizTransactionList;
	}

	public void setBizTransactionList(Map<String, List<String>> bizTransactionList) {
		this.bizTransactionList = bizTransactionList;
	}

	public Map<String, Map<String, Object>> getIlmd() {
		return ilmds;
	}

	public void setIlmd(Map<String, Map<String, Object>> ilmds) {
		this.ilmds = ilmds;
	}

	public BsonDocument asBsonDocument() {
		CaptureUtil util = new CaptureUtil();

		BsonDocument objectEvent = super.asBsonDocument();
		// Required Fields
		objectEvent = util.putAction(objectEvent, action);

		// Optional Fields
		if (this.epcList != null && this.epcList.size() != 0) {
			objectEvent = util.putEPCList(objectEvent, epcList);
		}
		if (this.bizTransactionList != null && this.bizTransactionList.isEmpty() == false) {
			objectEvent = util.putBizTransactionList(objectEvent, bizTransactionList);
		}
		if (this.ilmds != null && this.ilmds.isEmpty() == false) {
			objectEvent = util.putILMD(objectEvent, getNamespaces(), ilmds);
		}

		BsonDocument extension = asExtensionBsonDocument(util);
		if (this.quantityList != null && this.quantityList.isEmpty() == false) {
			extension = util.putQuantityList(extension, quantityList);
		}
		if (extension.isEmpty() == false)
			objectEvent.put("extension", extension);

		return objectEvent;
	}
}
