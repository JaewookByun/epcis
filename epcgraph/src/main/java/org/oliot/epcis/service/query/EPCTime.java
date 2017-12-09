package org.oliot.epcis.service.query;

public class EPCTime {
	public String epc;
	public Long time;

	public EPCTime(String epc, Long time) {
		this.epc = epc;
		this.time = time;
	}

	public EPCTime getThis() {
		return this;
	}
	
	public String getEpc() {
		return epc;
	}

	public void setEpc(String epc) {
		this.epc = epc;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}
	
	
}
