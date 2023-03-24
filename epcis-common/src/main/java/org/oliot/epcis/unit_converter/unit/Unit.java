package org.oliot.epcis.unit_converter.unit;

public abstract class Unit {

	protected String uom;
	private Double value;

	
	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}
}
