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
 * EPCISDocumentType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;

import org.apache.axis.encoding.AnyContentType;
import org.apache.axis.message.MessageElement;
import org.oliot.epcglobal.Document;


/**
 * document that contains a Header and a Body.
 */
@SuppressWarnings("serial")
public class EPCISDocumentType  extends Document  implements Serializable, AnyContentType {

	private EPCISHeaderType EPCISHeader;
	private EPCISBodyType EPCISBody;
	private EPCISDocumentExtensionType extension;
	private MessageElement [] _any;

	public EPCISDocumentType() {}

	public EPCISDocumentType( 
			BigDecimal schemaVersion,	
			Calendar creationDate,
			EPCISHeaderType EPCISHeader,
			EPCISBodyType EPCISBody,
			EPCISDocumentExtensionType extension,	
			MessageElement [] _any) {
		super(
				schemaVersion,
				creationDate);
		this.EPCISHeader = EPCISHeader;
		this.EPCISBody = EPCISBody;
		this.extension = extension;
		this._any = _any;
	}


	/**
	 * Gets the EPCISHeader value for this EPCISDocumentType.
	 * 
	 * @return EPCISHeader
	 */
	public EPCISHeaderType getEPCISHeader() {
		return EPCISHeader;
	}


	/**
	 * Sets the EPCISHeader value for this EPCISDocumentType.
	 * 
	 * @param EPCISHeader
	 */
	public void setEPCISHeader(EPCISHeaderType EPCISHeader) {
		this.EPCISHeader = EPCISHeader;
	}


	/**
	 * Gets the EPCISBody value for this EPCISDocumentType.
	 * 
	 * @return EPCISBody
	 */
	public EPCISBodyType getEPCISBody() {
		return EPCISBody;
	}


	/**
	 * Sets the EPCISBody value for this EPCISDocumentType.
	 * 
	 * @param EPCISBody
	 */
	public void setEPCISBody(EPCISBodyType EPCISBody) {
		this.EPCISBody = EPCISBody;
	}


	/**
	 * Gets the extension value for this EPCISDocumentType.
	 * 
	 * @return extension
	 */
	public EPCISDocumentExtensionType getExtension() {
		return extension;
	}


	/**
	 * Sets the extension value for this EPCISDocumentType.
	 * 
	 * @param extension
	 */
	public void setExtension(EPCISDocumentExtensionType extension) {
		this.extension = extension;
	}


	/**
	 * Gets the _any value for this EPCISDocumentType.
	 * 
	 * @return _any
	 */
	public MessageElement [] get_any() {
		return _any;
	}


	/**
	 * Sets the _any value for this EPCISDocumentType.
	 * 
	 * @param _any
	 */
	public void set_any(MessageElement [] _any) {
		this._any = _any;
	}

	private java.lang.Object __equalsCalc = null;
	@SuppressWarnings("unused")
	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof EPCISDocumentType)) return false;
		EPCISDocumentType other = (EPCISDocumentType) obj;
		if (obj == null) return false;
		if (this == obj) return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = super.equals(obj) && 
				((this.EPCISHeader==null && other.getEPCISHeader()==null) || 
						(this.EPCISHeader!=null &&
						this.EPCISHeader.equals(other.getEPCISHeader()))) &&
						((this.EPCISBody==null && other.getEPCISBody()==null) || 
								(this.EPCISBody!=null &&
								this.EPCISBody.equals(other.getEPCISBody()))) &&
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
		int _hashCode = super.hashCode();
		if (getEPCISHeader() != null) {
			_hashCode += getEPCISHeader().hashCode();
		}
		if (getEPCISBody() != null) {
			_hashCode += getEPCISBody().hashCode();
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
			new org.apache.axis.description.TypeDesc(EPCISDocumentType.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("epcis.oliot.org", "EPCISDocumentType"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("EPCISHeader");
		elemField.setXmlName(new javax.xml.namespace.QName("", "EPCISHeader"));
		elemField.setXmlType(new javax.xml.namespace.QName("epcis.oliot.org", "EPCISHeaderType"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("EPCISBody");
		elemField.setXmlName(new javax.xml.namespace.QName("", "EPCISBody"));
		elemField.setXmlType(new javax.xml.namespace.QName("epcis.oliot.org", "EPCISBodyType"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("extension");
		elemField.setXmlName(new javax.xml.namespace.QName("", "extension"));
		elemField.setXmlType(new javax.xml.namespace.QName("epcis.oliot.org", "EPCISDocumentExtensionType"));
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
