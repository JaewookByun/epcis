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
 * EventListType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.model;

import org.apache.axis.message.MessageElement;

public class EventListType {

	private ObjectEventType[] objectEvent;
	private AggregationEventType[] aggregationEvent;
	private QuantityEventType[] quantityEvent;
	private TransactionEventType[] transactionEvent;
	private EPCISEventListExtensionType extension;

	public ObjectEventType[] getObjectEvent() {
		return objectEvent;
	}

	public void setObjectEvent(ObjectEventType[] objectEvent) {
		this.objectEvent = objectEvent;
	}

	public AggregationEventType[] getAggregationEvent() {
		return aggregationEvent;
	}

	public void setAggregationEvent(AggregationEventType[] aggregationEvent) {
		this.aggregationEvent = aggregationEvent;
	}

	public QuantityEventType[] getQuantityEvent() {
		return quantityEvent;
	}

	public void setQuantityEvent(QuantityEventType[] quantityEvent) {
		this.quantityEvent = quantityEvent;
	}

	public TransactionEventType[] getTransactionEvent() {
		return transactionEvent;
	}

	public void setTransactionEvent(TransactionEventType[] transactionEvent) {
		this.transactionEvent = transactionEvent;
	}

	public EPCISEventListExtensionType getExtension() {
		return extension;
	}

	public void setExtension(EPCISEventListExtensionType extension) {
		this.extension = extension;
	}

	public MessageElement[] get_any() {
		return _any;
	}

	public void set_any(MessageElement[] _any) {
		this._any = _any;
	}

	private MessageElement[] _any;

	public EventListType() {
	}

	public EventListType(ObjectEventType[] objectEvent,
			AggregationEventType[] aggregationEvent,
			QuantityEventType[] quantityEvent,
			TransactionEventType[] transactionEvent,
			EPCISEventListExtensionType extension, MessageElement[] _any) {
		this.objectEvent = objectEvent;
		this.aggregationEvent = aggregationEvent;
		this.quantityEvent = quantityEvent;
		this.transactionEvent = transactionEvent;
		this.extension = extension;
		this._any = _any;
	}

}
