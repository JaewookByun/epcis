package org.oliot.epcis.converter.mongodb.model;

import java.text.SimpleDateFormat;
import java.util.Date;

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
public abstract class EPCISEvent {
	private long eventTime;
	private String eventTimeZoneOffset;

	public EPCISEvent() {
		eventTime = System.currentTimeMillis();
		SimpleDateFormat format = new SimpleDateFormat("XXX");
		eventTimeZoneOffset = format.format(new Date());
	}

	public EPCISEvent(long eventTime, String eventTimeZoneOffset) {
		this.eventTime = eventTime;
		this.eventTimeZoneOffset = eventTimeZoneOffset;
	}

	public long getEventTime() {
		return eventTime;
	}

	public void setEventTime(long eventTime) {
		this.eventTime = eventTime;
	}

	public String getEventTimeZoneOffset() {
		return eventTimeZoneOffset;
	}

	public void setEventTimeZoneOffset() {
		SimpleDateFormat format = new SimpleDateFormat("XXX");
		eventTimeZoneOffset = format.format(new Date());
	}

	public void setEventTimeZoneOffset(String eventTimeZoneOffset) {
		this.eventTimeZoneOffset = eventTimeZoneOffset;
	}

	public BsonDocument asBsonDocument() {
		CaptureUtil util = new CaptureUtil();

		BsonDocument baseEvent = new BsonDocument();
		// Required Fields
		baseEvent = util.putEventTime(baseEvent, eventTime);
		baseEvent = util.putEventTimeZoneOffset(baseEvent, eventTimeZoneOffset);

		return baseEvent;
	}

}
