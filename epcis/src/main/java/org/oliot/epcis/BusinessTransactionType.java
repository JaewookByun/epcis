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
 * BusinessTransactionType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis;

import java.io.Serializable;

import org.apache.axis.encoding.SimpleType;
import org.apache.axis.types.URI;

@SuppressWarnings("serial")
public class BusinessTransactionType  implements Serializable, SimpleType {

	private org.apache.axis.types.URI type;  // attribute

	public BusinessTransactionType() {}

	// Simple Types must have a String constructor
	public BusinessTransactionType(URI _value) {
		this.type = _value;
	}

	/**
	 * Gets the type value for this BusinessTransactionType.
	 * 
	 * @return type
	 */
	public URI getType() {
		return type;
	}

	/**
	 * Sets the type value for this BusinessTransactionType.
	 * 
	 * @param type
	 */
	public void setType(URI type) {
		this.type = type;
	}

	private Object __equalsCalc = null;
	@SuppressWarnings("unused")
	public synchronized boolean equals(Object obj) {
		if (!(obj instanceof BusinessTransactionType)) return false;
		BusinessTransactionType other = (BusinessTransactionType) obj;
		if (obj == null) return false;
		if (this == obj) return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = super.equals(obj) && 
				((this.type==null && other.getType()==null) || 
						(this.type!=null &&
						this.type.equals(other.getType())));
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
		if (getType() != null) {
			_hashCode += getType().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc =
			new org.apache.axis.description.TypeDesc(BusinessTransactionType.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("epcis.oliot.org", "BusinessTransactionType"));
		org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
		attrField.setFieldName("type");
		attrField.setXmlName(new javax.xml.namespace.QName("", "type"));
		attrField.setXmlType(new javax.xml.namespace.QName("epcis.oliot.org", "BusinessTransactionTypeIDType"));
		typeDesc.addFieldDesc(attrField);
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
