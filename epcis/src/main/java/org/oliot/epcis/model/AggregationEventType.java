/**
 * Copyright (C) 2014 KAIST RESL 
 *
 * This file is part of Oliot (oliot.org).

 * @author Jack Jaewook Byun, Ph.D student
 * Korea Advanced Institute of Science and Technology
 * Real-time Embedded System Laboratory(RESL)
 * bjw0829@kaist.ac.kr
 */

/**
 * AggregationEventType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.model;

import org.apache.axis.message.MessageElement;
import org.apache.axis.types.URI;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Aggregation Event captures an event that applies to objects that
 * 			have a physical association with one another.
 */

@Document(collection = "AggregationEvent")
public class AggregationEventType  extends EPCISEventType{

	private URI parentID;
	private EPC[] childEPCs;
	private ActionType action;
	private URI bizStep;
	private URI disposition;
	private ReadPointType readPoint;
	private BusinessLocationType bizLocation;
	private BusinessTransactionType[] bizTransactionList;
	//WSDL doesn't reflect QuantityElements List
	private QuantityElementType[] childQuantityList;
	private AggregationEventExtensionType extension;
	private MessageElement [] _any;

	public AggregationEventType() {	}

	public AggregationEventType(
			java.util.Calendar eventTime,
			java.util.Calendar recordTime,
			java.lang.String eventTimeZoneOffset,
			EPCISEventExtensionType baseExtension,
			URI parentID,
			EPC[] childEPCs,
			ActionType action,
			URI bizStep,
			URI disposition,
			ReadPointType readPoint,
			BusinessLocationType bizLocation,
			BusinessTransactionType[] bizTransactionList,
			AggregationEventExtensionType extension,
			MessageElement [] _any) {
		super(
				eventTime,
				recordTime,
				eventTimeZoneOffset,
				baseExtension);
		this.parentID = parentID;
		this.childEPCs = childEPCs;
		this.action = action;
		this.bizStep = bizStep;
		this.disposition = disposition;
		this.readPoint = readPoint;
		this.bizLocation = bizLocation;
		this.bizTransactionList = bizTransactionList;
		this.extension = extension;
		this._any = _any;
	}

	public URI getParentID() {
		return parentID;
	}

	public void setParentID(URI parentID) {
		this.parentID = parentID;
	}

	public EPC[] getChildEPCs() {
		return childEPCs;
	}

	public void setChildEPCs(EPC[] childEPCs) {
		this.childEPCs = childEPCs;
	}

	public ActionType getAction() {
		return action;
	}

	public void setAction(ActionType action) {
		this.action = action;
	}

	public URI getBizStep() {
		return bizStep;
	}

	public void setBizStep(URI bizStep) {
		this.bizStep = bizStep;
	}

	public URI getDisposition() {
		return disposition;
	}

	public void setDisposition(URI disposition) {
		this.disposition = disposition;
	}

	public ReadPointType getReadPoint() {
		return readPoint;
	}

	public void setReadPoint(ReadPointType readPoint) {
		this.readPoint = readPoint;
	}

	public BusinessLocationType getBizLocation() {
		return bizLocation;
	}

	public void setBizLocation(BusinessLocationType bizLocation) {
		this.bizLocation = bizLocation;
	}

	public BusinessTransactionType[] getBizTransactionList() {
		return bizTransactionList;
	}

	public void setBizTransactionList(BusinessTransactionType[] bizTransactionList) {
		this.bizTransactionList = bizTransactionList;
	}

	public QuantityElementType[] getChildQuantityList() {
		return childQuantityList;
	}

	public void setChildQuantityList(QuantityElementType[] childQuantityList) {
		this.childQuantityList = childQuantityList;
	}

	public AggregationEventExtensionType getExtension() {
		return extension;
	}

	public void setExtension(AggregationEventExtensionType extension) {
		this.extension = extension;
	}

	public MessageElement[] get_any() {
		return _any;
	}

	public void set_any(MessageElement[] _any) {
		this._any = _any;
	}
}
