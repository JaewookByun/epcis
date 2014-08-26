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
 * EPCISEventListExtensionType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis;

import java.io.Serializable;

@SuppressWarnings("serial")
public class EPCISEventListExtensionType  implements Serializable {

	private TransformationEventType transformationEvent;
	private EPCISEventListExtension2Type extension;

	public EPCISEventListExtensionType() {
	}

	public EPCISEventListExtensionType( TransformationEventType transformationEvent,	EPCISEventListExtension2Type extension) {
		this.transformationEvent = transformationEvent;
		this.extension = extension;
	}


	/**
	 * Gets the transformationEvent value for this EPCISEventListExtensionType.
	 * 
	 * @return transformationEvent
	 */
	public TransformationEventType getTransformationEvent() {
		return transformationEvent;
	}


	/**
	 * Sets the transformationEvent value for this EPCISEventListExtensionType.
	 * 
	 * @param transformationEvent
	 */
	public void setTransformationEvent(TransformationEventType transformationEvent) {
		this.transformationEvent = transformationEvent;
	}

	/**
	 * Gets the extension value for this EPCISEventListExtensionType.
	 * 
	 * @return extension
	 */
	public EPCISEventListExtension2Type getExtension() {
		return extension;
	}

	/**
	 * Sets the extension value for this EPCISEventListExtensionType.
	 * 
	 * @param extension
	 */
	public void setExtension(EPCISEventListExtension2Type extension) {
		this.extension = extension;
	}

	private Object __equalsCalc = null;
	@SuppressWarnings("unused")
	public synchronized boolean equals(Object obj) {
		if (!(obj instanceof EPCISEventListExtensionType)) return false;
		EPCISEventListExtensionType other = (EPCISEventListExtensionType) obj;
		if (obj == null) return false;
		if (this == obj) return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true && 
				((this.transformationEvent==null && other.getTransformationEvent()==null) || 
						(this.transformationEvent!=null &&
						this.transformationEvent.equals(other.getTransformationEvent()))) &&
						((this.extension==null && other.getExtension()==null) || 
								(this.extension!=null &&
								this.extension.equals(other.getExtension())));
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
		if (getTransformationEvent() != null) {
			_hashCode += getTransformationEvent().hashCode();
		}
		if (getExtension() != null) {
			_hashCode += getExtension().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc =
			new org.apache.axis.description.TypeDesc(EPCISEventListExtensionType.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("epcis.oliot.org", "EPCISEventListExtensionType"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("transformationEvent");
		elemField.setXmlName(new javax.xml.namespace.QName("", "TransformationEvent"));
		elemField.setXmlType(new javax.xml.namespace.QName("epcis.oliot.org", "TransformationEventType"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("extension");
		elemField.setXmlName(new javax.xml.namespace.QName("", "extension"));
		elemField.setXmlType(new javax.xml.namespace.QName("epcis.oliot.org", "EPCISEventListExtension2Type"));
		elemField.setMinOccurs(0);
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
