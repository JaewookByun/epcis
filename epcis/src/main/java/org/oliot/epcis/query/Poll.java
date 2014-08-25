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
 * Poll.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.query;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;

import javax.xml.namespace.QName;

import org.apache.axis.description.ElementDesc;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.ser.BeanDeserializer;
import org.apache.axis.encoding.ser.BeanSerializer;

@SuppressWarnings("serial")
public class Poll  implements Serializable {
	private String queryName;

	private QueryParam[] params;

	public Poll() {
	}

	public Poll( String queryName, QueryParam[] params) {
		this.queryName = queryName;
		this.params = params;
	}

	/**
	 * Gets the queryName value for this Poll.
	 * 
	 * @return queryName
	 */
	public String getQueryName() {
		return queryName;
	}

	/**
	 * Sets the queryName value for this Poll.
	 * 
	 * @param queryName
	 */
	public void setQueryName( String queryName) {
		this.queryName = queryName;
	}


	/**
	 * Gets the params value for this Poll.
	 * 
	 * @return params
	 */
	public QueryParam[] getParams() {
		return params;
	}


	/**
	 * Sets the params value for this Poll.
	 * 
	 * @param params
	 */
	public void setParams( QueryParam[] params) {
		this.params = params;
	}

	private Object __equalsCalc = null;
	@SuppressWarnings("unused")
	public synchronized boolean equals( Object obj) {
		if (!(obj instanceof Poll)) return false;
		Poll other = (Poll) obj;
		if (obj == null) return false;
		if (this == obj) return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true && 
				((this.queryName==null && other.getQueryName()==null) || 
						(this.queryName!=null &&
						this.queryName.equals(other.getQueryName()))) &&
						((this.params==null && other.getParams()==null) || 
								(this.params!=null &&
								Arrays.equals(this.params, other.getParams())));
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
		if (getQueryName() != null) {
			_hashCode += getQueryName().hashCode();
		}
		if (getParams() != null) {
			for (int i=0;
					i< Array.getLength(getParams());
					i++) {
				java.lang.Object obj = Array.get(getParams(), i);
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
	private static TypeDesc typeDesc = new TypeDesc(Poll.class, true);

	static {
		typeDesc.setXmlType(new QName("query.epcis.oliot.org", "Poll"));
		ElementDesc elemField = new ElementDesc();
		elemField.setFieldName("queryName");
		elemField.setXmlName(new QName("", "queryName"));
		elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new ElementDesc();
		elemField.setFieldName("params");
		elemField.setXmlName(new QName("", "params"));
		elemField.setXmlType(new QName("query.epcis.oliot.org", "QueryParam"));
		elemField.setNillable(false);
		elemField.setItemQName(new QName("", "param"));
		typeDesc.addFieldDesc(elemField);
	}

	/**
	 * Return type metadata object
	 */
	public static TypeDesc getTypeDesc() {
		return typeDesc;
	}

	/**
	 * Get Custom Serializer
	 */
	@SuppressWarnings("rawtypes")
	public static Serializer getSerializer( String mechType, Class _javaType, QName _xmlType) {
		return new  BeanSerializer(_javaType, _xmlType, typeDesc);
	}

	/**
	 * Get Custom Deserializer
	 */
	@SuppressWarnings("rawtypes")
	public static Deserializer getDeserializer( String mechType, Class _javaType, QName _xmlType) {
		return new  BeanDeserializer( _javaType, _xmlType, typeDesc);
	}

}
