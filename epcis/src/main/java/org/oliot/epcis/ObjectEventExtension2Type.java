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
 * ObjectEventExtension2Type.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis;

import java.io.Serializable;

import org.apache.axis.encoding.AnyContentType;
import org.apache.axis.message.MessageElement;

@SuppressWarnings("serial")
public class ObjectEventExtension2Type  implements Serializable, AnyContentType {
	
	private MessageElement [] _any;

	public ObjectEventExtension2Type() {}

	public ObjectEventExtension2Type(MessageElement [] _any) {
		this._any = _any;
	}

	/**
	 * Gets the _any value for this ObjectEventExtension2Type.
	 * 
	 * @return _any
	 */
	public org.apache.axis.message.MessageElement [] get_any() {
		return _any;
	}


	/**
	 * Sets the _any value for this ObjectEventExtension2Type.
	 * 
	 * @param _any
	 */
	public void set_any(org.apache.axis.message.MessageElement [] _any) {
		this._any = _any;
	}

	private java.lang.Object __equalsCalc = null;
	@SuppressWarnings("unused")
	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof ObjectEventExtension2Type)) return false;
		ObjectEventExtension2Type other = (ObjectEventExtension2Type) obj;
		if (obj == null) return false;
		if (this == obj) return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true && 
				((this._any==null && other.get_any()==null) || 
						(this._any!=null &&
						java.util.Arrays.equals(this._any, other.get_any())));
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
		if (get_any() != null) {
			for (int i=0;
					i<java.lang.reflect.Array.getLength(get_any());
					i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(get_any(), i);
				if (obj != null &&
						!obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc =
	new org.apache.axis.description.TypeDesc(ObjectEventExtension2Type.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("epcis.oliot.org", "ObjectEventExtension2Type"));
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
				new  org.apache.axis.encoding.ser.BeanSerializer(
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
				new  org.apache.axis.encoding.ser.BeanDeserializer(
						_javaType, _xmlType, typeDesc);
	}

}
