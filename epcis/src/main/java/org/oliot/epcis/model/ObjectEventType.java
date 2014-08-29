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
 * ObjectEventType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.model;

import java.util.Calendar;

import org.apache.axis.message.MessageElement;
import org.apache.axis.types.URI;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Object Event captures information about an event pertaining to one or more
 * objects identified by EPCs.
 */
@Document(collection = "ObjectEvent")
public class ObjectEventType extends EPCISEventType {

	private EPC[] epcList;
	private ActionType action;
	private URI bizStep;
	private URI disposition;
	private ReadPointType readPoint;
	private BusinessLocationType bizLocation;
	private BusinessTransactionType[] bizTransactionList;
	private ObjectEventExtensionType extension;

	public EPC[] getEpcList() {
		return epcList;
	}

	public void setEpcList(EPC[] epcList) {
		this.epcList = epcList;
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

	public void setBizTransactionList(
			BusinessTransactionType[] bizTransactionList) {
		this.bizTransactionList = bizTransactionList;
	}

	public ObjectEventExtensionType getExtension() {
		return extension;
	}

	public void setExtension(ObjectEventExtensionType extension) {
		this.extension = extension;
	}

	public MessageElement[] get_any() {
		return _any;
	}

	public void set_any(MessageElement[] _any) {
		this._any = _any;
	}

	private MessageElement[] _any;

	public ObjectEventType() {
	}

	public ObjectEventType(Calendar eventTime, Calendar recordTime,
			String eventTimeZoneOffset, EPCISEventExtensionType baseExtension,
			EPC[] epcList, ActionType action, URI bizStep, URI disposition,
			ReadPointType readPoint, BusinessLocationType bizLocation,
			BusinessTransactionType[] bizTransactionList,
			ObjectEventExtensionType extension, MessageElement[] _any) {
		super(eventTime, recordTime, eventTimeZoneOffset, baseExtension);
		this.epcList = epcList;
		this.action = action;
		this.bizStep = bizStep;
		this.disposition = disposition;
		this.readPoint = readPoint;
		this.bizLocation = bizLocation;
		this.bizTransactionList = bizTransactionList;
		this.extension = extension;
		this._any = _any;
	}

}
