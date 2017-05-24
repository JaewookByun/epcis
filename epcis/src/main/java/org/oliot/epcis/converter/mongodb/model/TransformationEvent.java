package org.oliot.epcis.converter.mongodb.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.BsonDocument;

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
public class TransformationEvent extends EPCISEvent {

	// EventTime, EventTimeZoneOffset required
	private List<String> inputEPCList;
	private List<QuantityElement> inputQuantityList;
	private List<String> outputEPCList;
	private List<QuantityElement> outputQuantityList;

	private String transformationID;

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

	public TransformationEvent() {
		super();

		inputEPCList = new ArrayList<String>();
		inputQuantityList = new ArrayList<QuantityElement>();
		outputEPCList = new ArrayList<String>();
		outputQuantityList = new ArrayList<QuantityElement>();

		bizTransactionList = new HashMap<String, List<String>>();
		sourceList = new HashMap<String, List<String>>();
		destinationList = new HashMap<String, List<String>>();
		namespaces = new HashMap<String, String>();
		ilmds = new BsonDocument();
		extensions = new BsonDocument();
	}

	public TransformationEvent(long eventTime, String eventTimeZoneOffset) {
		super(eventTime, eventTimeZoneOffset);

		inputEPCList = new ArrayList<String>();
		inputQuantityList = new ArrayList<QuantityElement>();
		outputEPCList = new ArrayList<String>();
		outputQuantityList = new ArrayList<QuantityElement>();

		bizTransactionList = new HashMap<String, List<String>>();
		sourceList = new HashMap<String, List<String>>();
		destinationList = new HashMap<String, List<String>>();
		namespaces = new HashMap<String, String>();
		ilmds = new BsonDocument();
		extensions = new BsonDocument();
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

	public BsonDocument getIlmds() {
		return ilmds;
	}

	public void setIlmds(BsonDocument ilmds) {
		this.ilmds = ilmds;
	}

	public BsonDocument getExtensions() {
		return extensions;
	}

	public void setExtensions(BsonDocument extensions) {
		this.extensions = extensions;
	}

	public BsonDocument asBsonDocument() {
		CaptureUtil util = new CaptureUtil();

		BsonDocument transformationEvent = super.asBsonDocument();
		transformationEvent = util.putEventType(transformationEvent, "TransformationEvent");
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

		if (this.bizStep != null) {
			transformationEvent = util.putBizStep(transformationEvent, bizStep);
		}
		if (this.disposition != null) {
			transformationEvent = util.putDisposition(transformationEvent, disposition);
		}
		if (this.readPoint != null) {
			transformationEvent = util.putReadPoint(transformationEvent, readPoint);
		}
		if (this.bizLocation != null) {
			transformationEvent = util.putBizLocation(transformationEvent, bizLocation);
		}
		if (this.bizTransactionList != null && this.bizTransactionList.isEmpty() == false) {
			transformationEvent = util.putBizTransactionList(transformationEvent, bizTransactionList);
		}
		if (this.ilmds != null && this.ilmds.isEmpty() == false) {
			transformationEvent = util.putILMD(transformationEvent, namespaces, ilmds);
		}
		if (this.extensions != null && this.extensions.isEmpty() == false) {
			transformationEvent = util.putExtensions(transformationEvent, namespaces, extensions);
		}

		BsonDocument extension = new BsonDocument();
		if (this.sourceList != null && this.sourceList.isEmpty() == false) {
			extension = util.putSourceList(extension, sourceList);
		}
		if (this.destinationList != null && this.destinationList.isEmpty() == false) {
			extension = util.putDestinationList(extension, destinationList);
		}
		if (extension.isEmpty() == false)
			transformationEvent.put("extension", extension);

		return transformationEvent;
	}
}
