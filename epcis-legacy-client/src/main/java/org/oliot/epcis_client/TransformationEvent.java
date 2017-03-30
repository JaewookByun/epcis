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
public class TransformationEvent extends EPCISEvent {

	// EventTime, EventTimeZoneOffset required
	private List<String> inputEPCList;
	private List<QuantityElement> inputQuantityList;
	private List<String> outputEPCList;
	private List<QuantityElement> outputQuantityList;
	private String transformationID;
	private Map<String, List<String>> bizTransactionList;
	private Map<String, Map<String, Object>> ilmds;

	public TransformationEvent() {
		super();

		inputEPCList = new ArrayList<String>();
		inputQuantityList = new ArrayList<QuantityElement>();
		outputEPCList = new ArrayList<String>();
		outputQuantityList = new ArrayList<QuantityElement>();

		bizTransactionList = new HashMap<String, List<String>>();
		ilmds = new HashMap<String, Map<String, Object>>();
	}

	public TransformationEvent(long eventTime, String eventTimeZoneOffset) {
		super(eventTime, eventTimeZoneOffset);

		inputEPCList = new ArrayList<String>();
		inputQuantityList = new ArrayList<QuantityElement>();
		outputEPCList = new ArrayList<String>();
		outputQuantityList = new ArrayList<QuantityElement>();

		bizTransactionList = new HashMap<String, List<String>>();
		ilmds = new HashMap<String, Map<String, Object>>();
	}

	public List<String> getInputEPCList() {
		return inputEPCList;
	}

	public void setInputEPCList(List<String> inputEPCList) {
		this.inputEPCList = inputEPCList;
	}

	public List<QuantityElement> getInputQuantityList() {
		return inputQuantityList;
	}

	public void setInputQuantityList(List<QuantityElement> inputQuantityList) {
		this.inputQuantityList = inputQuantityList;
	}

	public List<String> getOutputEPCList() {
		return outputEPCList;
	}

	public void setOutputEPCList(List<String> outputEPCList) {
		this.outputEPCList = outputEPCList;
	}

	public List<QuantityElement> getOutputQuantityList() {
		return outputQuantityList;
	}

	public void setOutputQuantityList(List<QuantityElement> outputQuantityList) {
		this.outputQuantityList = outputQuantityList;
	}

	public String getTransformationID() {
		return transformationID;
	}

	public void setTransformationID(String transformationID) {
		this.transformationID = transformationID;
	}

	public Map<String, List<String>> getBizTransactionList() {
		return bizTransactionList;
	}

	public void setBizTransactionList(Map<String, List<String>> bizTransactionList) {
		this.bizTransactionList = bizTransactionList;
	}

	public Map<String, Map<String, Object>> getIlmds() {
		return ilmds;
	}

	public void setIlmds(Map<String, Map<String, Object>> ilmds) {
		this.ilmds = ilmds;
	}

	public BsonDocument asBsonDocument() {
		CaptureUtil util = new CaptureUtil();

		BsonDocument transformationEvent = super.asBsonDocument();

		// Optional Fields
		if (this.inputEPCList != null && this.inputEPCList.size() != 0) {
			transformationEvent = util.putInputEPCList(transformationEvent, inputEPCList);
		}
		if (this.inputQuantityList != null && this.inputQuantityList.isEmpty() == false) {
			transformationEvent = util.putInputQuantityList(transformationEvent, inputQuantityList);
		}
		if (this.outputEPCList != null && this.outputEPCList.size() != 0) {
			transformationEvent = util.putOutputEPCList(transformationEvent, outputEPCList);
		}
		if (this.outputQuantityList != null && this.outputQuantityList.isEmpty() == false) {
			transformationEvent = util.putOutputQuantityList(transformationEvent, outputQuantityList);
		}

		if (this.transformationID != null) {
			transformationEvent = util.putTransformationID(transformationEvent, transformationID);
		}

		if (this.bizTransactionList != null && this.bizTransactionList.isEmpty() == false) {
			transformationEvent = util.putBizTransactionList(transformationEvent, bizTransactionList);
		}
		if (this.ilmds != null && this.ilmds.isEmpty() == false) {
			transformationEvent = util.putILMD(transformationEvent, getNamespaces(), ilmds);
		}

		BsonDocument extension = asExtensionBsonDocument(util);
		if (extension.isEmpty() == false)
			transformationEvent.put("extension", extension);

		return transformationEvent;
	}
}
