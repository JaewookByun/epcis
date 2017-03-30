package org.oliot.epcis_client;

import java.util.ArrayList;
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
public class TransactionEvent extends EPCISEvent {

	// EventTime, EventTimeZoneOffset,Action, bizTransactionList required
	private String action;
	private Map<String, List<String>> bizTransactionList;
	private String parentID;
	private List<String> epcList;
	private List<QuantityElement> quantityList;

	public TransactionEvent(Map<String, List<String>> bizTransactionList) {
		super();

		// Required Fields
		action = "OBSERVE";
		this.bizTransactionList = bizTransactionList;

		epcList = new ArrayList<String>();
		quantityList = new ArrayList<QuantityElement>();
	}

	public TransactionEvent(long eventTime, String eventTimeZoneOffset, String action,
			Map<String, List<String>> bizTransactionList) {
		super(eventTime, eventTimeZoneOffset);

		this.action = action;
		this.bizTransactionList = bizTransactionList;

		epcList = new ArrayList<String>();
		quantityList = new ArrayList<QuantityElement>();
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

	public BsonDocument asBsonDocument() {
		CaptureUtil util = new CaptureUtil();

		BsonDocument transactionEvent = super.asBsonDocument();
		// Required Fields
		transactionEvent = util.putAction(transactionEvent, action);
		transactionEvent = util.putBizTransactionList(transactionEvent, bizTransactionList);

		// Optional Fields
		if (this.parentID != null) {
			transactionEvent = util.putParentID(transactionEvent, parentID);
		}
		if (this.epcList != null && this.epcList.size() != 0) {
			transactionEvent = util.putEPCList(transactionEvent, epcList);
		}

		BsonDocument extension = asExtensionBsonDocument(util);
		if (this.quantityList != null && this.quantityList.isEmpty() == false) {
			extension = util.putQuantityList(extension, quantityList);
		}
		if (extension.isEmpty() == false)
			transactionEvent.put("extension", extension);

		return transactionEvent;
	}
}
