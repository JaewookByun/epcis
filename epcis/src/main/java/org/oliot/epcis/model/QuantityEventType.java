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
 * QuantityEventType.java
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
 * Quantity Event captures an event that takes place with respect to a specified
 * quantity of object class.
 */
@Document(collection="QuantityEvent")
public class QuantityEventType extends EPCISEventType {

	private URI epcClass;
	private int quantity;
	private URI bizStep;
	private URI disposition;
	private ReadPointType readPoint;
	private BusinessLocationType bizLocation;
	private BusinessTransactionType[] bizTransactionList;
	private QuantityEventExtensionType extension;
	private MessageElement[] _any;

	public QuantityEventType() {
	}

	public QuantityEventType(Calendar eventTime, Calendar recordTime,
			String eventTimeZoneOffset, EPCISEventExtensionType baseExtension,
			URI epcClass, int quantity, URI bizStep, URI disposition,
			ReadPointType readPoint, BusinessLocationType bizLocation,
			BusinessTransactionType[] bizTransactionList,
			QuantityEventExtensionType extension, MessageElement[] _any) {
		super(eventTime, recordTime, eventTimeZoneOffset, baseExtension);
		this.epcClass = epcClass;
		this.quantity = quantity;
		this.bizStep = bizStep;
		this.disposition = disposition;
		this.readPoint = readPoint;
		this.bizLocation = bizLocation;
		this.bizTransactionList = bizTransactionList;
		this.extension = extension;
		this._any = _any;
	}

	public URI getEpcClass() {
		return epcClass;
	}

	public void setEpcClass(URI epcClass) {
		this.epcClass = epcClass;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
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

	public QuantityEventExtensionType getExtension() {
		return extension;
	}

	public void setExtension(QuantityEventExtensionType extension) {
		this.extension = extension;
	}

	public MessageElement[] get_any() {
		return _any;
	}

	public void set_any(MessageElement[] _any) {
		this._any = _any;
	}

}
