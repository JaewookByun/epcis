/**
 * EPCglobal document properties for all messages.
 */

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
 * Document.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcglobal;

@SuppressWarnings("serial")
public abstract class Document  implements java.io.Serializable {
	private java.math.BigDecimal schemaVersion;  // attribute

	private java.util.Calendar creationDate;  // attribute

	public Document() {}

	public Document(
			java.math.BigDecimal schemaVersion,
			java.util.Calendar creationDate) {
		this.schemaVersion = schemaVersion;
		this.creationDate = creationDate;
	}


	/**
	 * Gets the schemaVersion value for this Document.
	 * 
	 * @return schemaVersion
	 */
	public java.math.BigDecimal getSchemaVersion() {
		return schemaVersion;
	}


	/**
	 * Sets the schemaVersion value for this Document.
	 * 
	 * @param schemaVersion
	 */
	public void setSchemaVersion(java.math.BigDecimal schemaVersion) {
		this.schemaVersion = schemaVersion;
	}


	/**
	 * Gets the creationDate value for this Document.
	 * 
	 * @return creationDate
	 */
	public java.util.Calendar getCreationDate() {
		return creationDate;
	}


	/**
	 * Sets the creationDate value for this Document.
	 * 
	 * @param creationDate
	 */
	public void setCreationDate(java.util.Calendar creationDate) {
		this.creationDate = creationDate;
	}

	private java.lang.Object __equalsCalc = null;
	@SuppressWarnings("unused")
	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof Document)) return false;
		Document other = (Document) obj;
		if (obj == null) return false;
		if (this == obj) return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true && 
				((this.schemaVersion==null && other.getSchemaVersion()==null) || 
						(this.schemaVersion!=null &&
						this.schemaVersion.equals(other.getSchemaVersion()))) &&
						((this.creationDate==null && other.getCreationDate()==null) || 
								(this.creationDate!=null &&
								this.creationDate.equals(other.getCreationDate())));
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
		if (getSchemaVersion() != null) {
			_hashCode += getSchemaVersion().hashCode();
		}
		if (getCreationDate() != null) {
			_hashCode += getCreationDate().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc =
	new org.apache.axis.description.TypeDesc(Document.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("epcglobal.oliot.org", "Document"));
		org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
		attrField.setFieldName("schemaVersion");
		attrField.setXmlName(new javax.xml.namespace.QName("", "schemaVersion"));
		attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
		typeDesc.addFieldDesc(attrField);
		attrField = new org.apache.axis.description.AttributeDesc();
		attrField.setFieldName("creationDate");
		attrField.setXmlName(new javax.xml.namespace.QName("", "creationDate"));
		attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
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
