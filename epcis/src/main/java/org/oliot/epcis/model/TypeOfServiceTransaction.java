/**
 * TypeOfServiceTransaction.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.model;

import java.util.HashMap;

public class TypeOfServiceTransaction {
	private String _value_;
	@SuppressWarnings("rawtypes")
	private static HashMap _table_ = new HashMap();

	// Constructor
	@SuppressWarnings("unchecked")
	protected TypeOfServiceTransaction(java.lang.String value) {
		_value_ = value;
		_table_.put(_value_, this);
	}

	public static final String _RequestingServiceTransaction = "RequestingServiceTransaction";
	public static final String _RespondingServiceTransaction = "RespondingServiceTransaction";
	public static final TypeOfServiceTransaction RequestingServiceTransaction = new TypeOfServiceTransaction(
			_RequestingServiceTransaction);
	public static final TypeOfServiceTransaction RespondingServiceTransaction = new TypeOfServiceTransaction(
			_RespondingServiceTransaction);

	public String get_value_() {
		return _value_;
	}

	public void set_value_(String _value_) {
		this._value_ = _value_;
	}

	@SuppressWarnings("rawtypes")
	public static HashMap get_table_() {
		return _table_;
	}

	@SuppressWarnings("rawtypes")
	public static void set_table_(HashMap _table_) {
		TypeOfServiceTransaction._table_ = _table_;
	}

}
