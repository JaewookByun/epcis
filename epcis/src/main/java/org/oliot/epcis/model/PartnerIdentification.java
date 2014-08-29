/**
 * PartnerIdentification.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.model;

public class PartnerIdentification {
	private String _value;

	private String authority; // attribute

	public PartnerIdentification() {
	}

	// Simple Types must have a String constructor
	public PartnerIdentification(String _value) {
		this._value = _value;
	}

	public String get_value() {
		return _value;
	}

	public void set_value(String _value) {
		this._value = _value;
	}

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	// Simple Types must have a toString for serializing the value
	public String toString() {
		return _value;
	}

}
