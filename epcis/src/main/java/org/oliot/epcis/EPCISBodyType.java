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
 * EPCISBodyType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis;

import java.io.Serializable;

import org.apache.axis.encoding.AnyContentType;
import org.apache.axis.message.MessageElement;


/**
 * specific body that contains EPCIS related Events.
 */
@SuppressWarnings("serial")
public class EPCISBodyType  implements Serializable, AnyContentType {
	
	private EventListType eventList;
	private EPCISBodyExtensionType extension;
	private MessageElement [] _any;
	
	public EPCISBodyType() {	}
	
	public EPCISBodyType(EventListType eventList,EPCISBodyExtensionType extension,MessageElement [] _any) {
		this.eventList = eventList;
		this.extension = extension;
		this._any = _any;
	}

	/**
	 * Gets the eventList value for this EPCISBodyType.
	 * 
	 * @return eventList
	 */
	public EventListType getEventList() {
		return eventList;
	}


	/**
	 * Sets the eventList value for this EPCISBodyType.
	 * 
	 * @param eventList
	 */
	public void setEventList(EventListType eventList) {
		this.eventList = eventList;
	}

	/**
	 * Gets the extension value for this EPCISBodyType.
	 * 
	 * @return extension
	 */
	public EPCISBodyExtensionType getExtension() {
		return extension;
	}


	/**
	 * Sets the extension value for this EPCISBodyType.
	 * 
	 * @param extension
	 */
	public void setExtension(EPCISBodyExtensionType extension) {
		this.extension = extension;
	}


	/**
	 * Gets the _any value for this EPCISBodyType.
	 * 
	 * @return _any
	 */
	public org.apache.axis.message.MessageElement [] get_any() {
		return _any;
	}


	/**
	 * Sets the _any value for this EPCISBodyType.
	 * 
	 * @param _any
	 */
	public void set_any(org.apache.axis.message.MessageElement [] _any) {
		this._any = _any;
	}

	private java.lang.Object __equalsCalc = null;
	@SuppressWarnings("unused")
	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof EPCISBodyType)) return false;
		EPCISBodyType other = (EPCISBodyType) obj;
		if (obj == null) return false;
		if (this == obj) return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true && 
				((this.eventList==null && other.getEventList()==null) || 
						(this.eventList!=null &&
						this.eventList.equals(other.getEventList()))) &&
						((this.extension==null && other.getExtension()==null) || 
								(this.extension!=null &&
								this.extension.equals(other.getExtension()))) &&
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
		if (getEventList() != null) {
			_hashCode += getEventList().hashCode();
		}
		if (getExtension() != null) {
			_hashCode += getExtension().hashCode();
		}
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
	new org.apache.axis.description.TypeDesc(EPCISBodyType.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("epcis.oliot.org", "EPCISBodyType"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("eventList");
		elemField.setXmlName(new javax.xml.namespace.QName("", "EventList"));
		elemField.setXmlType(new javax.xml.namespace.QName("epcis.oliot.org", "EventListType"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("extension");
		elemField.setXmlName(new javax.xml.namespace.QName("", "extension"));
		elemField.setXmlType(new javax.xml.namespace.QName("epcis.oliot.org", "EPCISBodyExtensionType"));
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
