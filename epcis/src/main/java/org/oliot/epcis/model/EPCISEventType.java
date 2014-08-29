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
 * EPCISEventType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.model;

import java.util.Calendar;

/**
 * base type for all EPCIS events.
 */
public abstract class EPCISEventType {

	private Calendar eventTime;
	private Calendar recordTime;
	private String eventTimeZoneOffset;
	private EPCISEventExtensionType baseExtension;

	public EPCISEventType() {
	}

	public EPCISEventType(Calendar eventTime, Calendar recordTime,
			String eventTimeZoneOffset, EPCISEventExtensionType baseExtension) {
		this.eventTime = eventTime;
		this.recordTime = recordTime;
		this.eventTimeZoneOffset = eventTimeZoneOffset;
		this.baseExtension = baseExtension;
	}

	public Calendar getEventTime() {
		return eventTime;
	}

	public void setEventTime(Calendar eventTime) {
		this.eventTime = eventTime;
	}

	public Calendar getRecordTime() {
		return recordTime;
	}

	public void setRecordTime(Calendar recordTime) {
		this.recordTime = recordTime;
	}

	public String getEventTimeZoneOffset() {
		return eventTimeZoneOffset;
	}

	public void setEventTimeZoneOffset(String eventTimeZoneOffset) {
		this.eventTimeZoneOffset = eventTimeZoneOffset;
	}

	public EPCISEventExtensionType getBaseExtension() {
		return baseExtension;
	}

	public void setBaseExtension(EPCISEventExtensionType baseExtension) {
		this.baseExtension = baseExtension;
	}

}
