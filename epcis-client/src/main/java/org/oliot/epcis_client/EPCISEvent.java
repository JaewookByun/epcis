package org.oliot.epcis_client;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bson.BsonDocument;

public abstract class EPCISEvent {
	private long eventTime;
	private long recordTime;
	private String eventTimeZoneOffset;

	public EPCISEvent() {
		eventTime = System.currentTimeMillis();
		SimpleDateFormat format = new SimpleDateFormat("XXX");
		eventTimeZoneOffset = format.format(new Date());
		recordTime = 0;
	}

	public EPCISEvent(long eventTime, String eventTimeZoneOffset) {
		this.eventTime = eventTime;
		this.eventTimeZoneOffset = eventTimeZoneOffset;
		recordTime = 0;
	}

	public long getEventTime() {
		return eventTime;
	}

	public void setEventTime(long eventTime) {
		this.eventTime = eventTime;
	}

	public long getRecordTime() {
		return recordTime;
	}

	public void setRecordTime(long recordTime) {
		this.recordTime = recordTime;
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

		// Optional Fields
		if (this.recordTime != 0) {
			baseEvent = util.putRecordTime(baseEvent, recordTime);
		}
		return baseEvent;
	}

}
