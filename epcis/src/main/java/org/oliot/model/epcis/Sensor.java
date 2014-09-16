package org.oliot.model.epcis;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Sensor")
public class Sensor {

	private String epc;
	private String type;
	private String value;
	private String uom;
	private long startTime;
	private long finishTime;
	private String eventTimeZoneOffset;
	
	public String getEpc() {
		return epc;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(long finishTime) {
		this.finishTime = finishTime;
	}

	public String getEventTimeZoneOffset() {
		return eventTimeZoneOffset;
	}

	public void setEventTimeZoneOffset(String eventTimeZoneOffset) {
		this.eventTimeZoneOffset = eventTimeZoneOffset;
	}

	public void setEpc(String epc) {
		this.epc = epc;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

}
