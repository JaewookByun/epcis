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
 * EPC.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcglobal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * EPC represents the Electronic Product Code.
 */

@SuppressWarnings("serial")
@Document(collection = "Test")
public class EPC  implements java.io.Serializable, org.apache.axis.encoding.SimpleType {
	@Id
	private java.lang.String _value;

	public EPC() {
	}

	// Simple Types must have a String constructor
	public EPC(java.lang.String _value) {
		this._value = _value;
	}
	// Simple Types must have a toString for serializing the value
	public java.lang.String toString() {
		return _value;
	}


	/**
	 * Gets the _value value for this EPC.
	 * 
	 * @return _value
	 */
	public java.lang.String get_value() {
		return _value;
	}


	/**
	 * Sets the _value value for this EPC.
	 * 
	 * @param _value
	 */
	public void set_value(java.lang.String _value) {
		this._value = _value;
	}

	
	private java.lang.Object __equalsCalc = null;
	@SuppressWarnings("unused")
	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof EPC)) return false;
		EPC other = (EPC) obj;
		if (obj == null) return false;
		if (this == obj) return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true && 
				((this._value==null && other.get_value()==null) || 
						(this._value!=null &&
						this._value.equals(other.get_value())));
		__equalsCalc = null;
		return _equals;
	}

	private boolean __hashCodeCalc = false;
	public synchronized int hashCode() {
		if (__hashCodeCalc) {
			return 0;
		}
		__hashCodeCalc = true;
		int _hashCode = 1;
		if (get_value() != null) {
			_hashCode += get_value().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc =
	new org.apache.axis.description.TypeDesc(EPC.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("epcglobal.oliot.org", "EPC"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("_value");
		elemField.setXmlName(new javax.xml.namespace.QName("", "_value"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
	}

	/**
	 * Return type metadata object
	 */
	public static org.apache.axis.description.TypeDesc getTypeDesc() {
		return typeDesc;
	}

	/**
	 * Get Custom Serializer
	 */
	@SuppressWarnings("rawtypes")
	public static org.apache.axis.encoding.Serializer getSerializer(
			java.lang.String mechType, 
			java.lang.Class _javaType,  
			javax.xml.namespace.QName _xmlType) {
		return 
				new  org.apache.axis.encoding.ser.SimpleSerializer(
						_javaType, _xmlType, typeDesc);
	}

	/**
	 * Get Custom Deserializer
	 */
	@SuppressWarnings("rawtypes")
	public static org.apache.axis.encoding.Deserializer getDeserializer(
			java.lang.String mechType, 
			java.lang.Class _javaType,  
			javax.xml.namespace.QName _xmlType) {
		return 
				new  org.apache.axis.encoding.ser.SimpleDeserializer(
						_javaType, _xmlType, typeDesc);
	}

}
