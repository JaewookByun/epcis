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
 * ActionType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.HashMap;

import javax.xml.namespace.QName;

import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.ser.EnumDeserializer;
import org.apache.axis.encoding.ser.EnumSerializer;


@SuppressWarnings("serial")
public class ActionType implements Serializable {
	private String _value_;
	@SuppressWarnings("rawtypes")
	private static HashMap _table_ = new HashMap();

	// Constructor
	@SuppressWarnings("unchecked")
	public ActionType(String value) {
		_value_ = value;
		_table_.put(_value_,this);
	}

	public static final String _ADD = "ADD";
	public static final String _OBSERVE = "OBSERVE";
	public static final String _DELETE = "DELETE";
	public static final ActionType ADD = new ActionType(_ADD);
	public static final ActionType OBSERVE = new ActionType(_OBSERVE);
	public static final ActionType DELETE = new ActionType(_DELETE);
	public String getValue() { return _value_;}
	public static ActionType fromValue(String value) throws IllegalArgumentException {
		ActionType enumeration = (ActionType) _table_.get(value);
		if (enumeration==null) throw new IllegalArgumentException();
		return enumeration;
	}
	public static ActionType fromString(String value) throws IllegalArgumentException {
		return fromValue(value);
	}
	public boolean equals(Object obj) {return (obj == this);}
	public int hashCode() { return toString().hashCode();}
	public String toString() { return _value_;}
	public Object readResolve() throws ObjectStreamException { return fromValue(_value_);}

	@SuppressWarnings("rawtypes")
	public static Serializer getSerializer( String mechType, Class _javaType,  QName _xmlType) {
		return new EnumSerializer( _javaType, _xmlType);
	}
	@SuppressWarnings("rawtypes")
	public static Deserializer getDeserializer( String mechType, Class _javaType, QName _xmlType) {
		return new EnumDeserializer(_javaType, _xmlType);
	}
	// Type metadata
	private static TypeDesc typeDesc =	new TypeDesc(ActionType.class);

	static {
		typeDesc.setXmlType(new QName("epcis.oliot.org", "ActionType"));
	}
	/**
	 * Return type metadata object
	 */
	public static TypeDesc getTypeDesc() {
		return typeDesc;
	}

}
