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
 * EventListType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis;

import java.io.Serializable;

import org.apache.axis.encoding.AnyContentType;
import org.apache.axis.message.MessageElement;

@SuppressWarnings("serial")
public class EventListType  implements Serializable, AnyContentType {

	private ObjectEventType[] objectEvent;
	private AggregationEventType[] aggregationEvent;
	private QuantityEventType[] quantityEvent;
	private TransactionEventType[] transactionEvent;
	private EPCISEventListExtensionType extension;

	private MessageElement [] _any;

	public EventListType() {	}

	public EventListType(ObjectEventType[] objectEvent,AggregationEventType[] aggregationEvent,QuantityEventType[] quantityEvent,
			TransactionEventType[] transactionEvent,EPCISEventListExtensionType extension,MessageElement [] _any) {
		this.objectEvent = objectEvent;
		this.aggregationEvent = aggregationEvent;
		this.quantityEvent = quantityEvent;
		this.transactionEvent = transactionEvent;
		this.extension = extension;
		this._any = _any;
	}


	/**
	 * Gets the objectEvent value for this EventListType.
	 * 
	 * @return objectEvent
	 */
	 public ObjectEventType[] getObjectEvent() {
		 return objectEvent;
	 }


	 /**
	  * Sets the objectEvent value for this EventListType.
	  * 
	  * @param objectEvent
	  */
	 public void setObjectEvent(ObjectEventType[] objectEvent) {
		 this.objectEvent = objectEvent;
	 }

	 public ObjectEventType getObjectEvent(int i) {
		 return this.objectEvent[i];
	 }

	 public void setObjectEvent(int i, ObjectEventType _value) {
		 this.objectEvent[i] = _value;
	 }


	 /**
	  * Gets the aggregationEvent value for this EventListType.
	  * 
	  * @return aggregationEvent
	  */
	 public AggregationEventType[] getAggregationEvent() {
		 return aggregationEvent;
	 }


	 /**
	  * Sets the aggregationEvent value for this EventListType.
	  * 
	  * @param aggregationEvent
	  */
	 public void setAggregationEvent(AggregationEventType[] aggregationEvent) {
		 this.aggregationEvent = aggregationEvent;
	 }

	 public AggregationEventType getAggregationEvent(int i) {
		 return this.aggregationEvent[i];
	 }

	 public void setAggregationEvent(int i, AggregationEventType _value) {
		 this.aggregationEvent[i] = _value;
	 }


	 /**
	  * Gets the quantityEvent value for this EventListType.
	  * 
	  * @return quantityEvent
	  */
	 public QuantityEventType[] getQuantityEvent() {
		 return quantityEvent;
	 }


	 /**
	  * Sets the quantityEvent value for this EventListType.
	  * 
	  * @param quantityEvent
	  */
	 public void setQuantityEvent(QuantityEventType[] quantityEvent) {
		 this.quantityEvent = quantityEvent;
	 }

	 public QuantityEventType getQuantityEvent(int i) {
		 return this.quantityEvent[i];
	 }

	 public void setQuantityEvent(int i, QuantityEventType _value) {
		 this.quantityEvent[i] = _value;
	 }


	 /**
	  * Gets the transactionEvent value for this EventListType.
	  * 
	  * @return transactionEvent
	  */
	 public org.oliot.epcis.TransactionEventType[] getTransactionEvent() {
		 return transactionEvent;
	 }


	 /**
	  * Sets the transactionEvent value for this EventListType.
	  * 
	  * @param transactionEvent
	  */
	 public void setTransactionEvent(org.oliot.epcis.TransactionEventType[] transactionEvent) {
		 this.transactionEvent = transactionEvent;
	 }

	 public org.oliot.epcis.TransactionEventType getTransactionEvent(int i) {
		 return this.transactionEvent[i];
	 }

	 public void setTransactionEvent(int i, org.oliot.epcis.TransactionEventType _value) {
		 this.transactionEvent[i] = _value;
	 }


	 /**
	  * Gets the extension value for this EventListType.
	  * 
	  * @return extension
	  */
	 public org.oliot.epcis.EPCISEventListExtensionType getExtension() {
		 return extension;
	 }


	 /**
	  * Sets the extension value for this EventListType.
	  * 
	  * @param extension
	  */
	 public void setExtension(org.oliot.epcis.EPCISEventListExtensionType extension) {
		 this.extension = extension;
	 }


	 /**
	  * Gets the _any value for this EventListType.
	  * 
	  * @return _any
	  */
	 public org.apache.axis.message.MessageElement [] get_any() {
		 return _any;
	 }


	 /**
	  * Sets the _any value for this EventListType.
	  * 
	  * @param _any
	  */
	 public void set_any(org.apache.axis.message.MessageElement [] _any) {
		 this._any = _any;
	 }

	 private java.lang.Object __equalsCalc = null;
	 @SuppressWarnings("unused")
	 public synchronized boolean equals(java.lang.Object obj) {
		 if (!(obj instanceof EventListType)) return false;
		 EventListType other = (EventListType) obj;
		 if (obj == null) return false;
		 if (this == obj) return true;
		 if (__equalsCalc != null) {
			 return (__equalsCalc == obj);
		 }
		 __equalsCalc = obj;
		 boolean _equals;
		 _equals = true && 
				 ((this.objectEvent==null && other.getObjectEvent()==null) || 
						 (this.objectEvent!=null &&
						 java.util.Arrays.equals(this.objectEvent, other.getObjectEvent()))) &&
						 ((this.aggregationEvent==null && other.getAggregationEvent()==null) || 
								 (this.aggregationEvent!=null &&
								 java.util.Arrays.equals(this.aggregationEvent, other.getAggregationEvent()))) &&
								 ((this.quantityEvent==null && other.getQuantityEvent()==null) || 
										 (this.quantityEvent!=null &&
										 java.util.Arrays.equals(this.quantityEvent, other.getQuantityEvent()))) &&
										 ((this.transactionEvent==null && other.getTransactionEvent()==null) || 
												 (this.transactionEvent!=null &&
												 java.util.Arrays.equals(this.transactionEvent, other.getTransactionEvent()))) &&
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
		 if (getObjectEvent() != null) {
			 for (int i=0;
					 i<java.lang.reflect.Array.getLength(getObjectEvent());
					 i++) {
				 java.lang.Object obj = java.lang.reflect.Array.get(getObjectEvent(), i);
				 if (obj != null &&
						 !obj.getClass().isArray()) {
					 _hashCode += obj.hashCode();
				 }
			 }
		 }
		 if (getAggregationEvent() != null) {
			 for (int i=0;
					 i<java.lang.reflect.Array.getLength(getAggregationEvent());
					 i++) {
				 java.lang.Object obj = java.lang.reflect.Array.get(getAggregationEvent(), i);
				 if (obj != null &&
						 !obj.getClass().isArray()) {
					 _hashCode += obj.hashCode();
				 }
			 }
		 }
		 if (getQuantityEvent() != null) {
			 for (int i=0;
					 i<java.lang.reflect.Array.getLength(getQuantityEvent());
					 i++) {
				 java.lang.Object obj = java.lang.reflect.Array.get(getQuantityEvent(), i);
				 if (obj != null &&
						 !obj.getClass().isArray()) {
					 _hashCode += obj.hashCode();
				 }
			 }
		 }
		 if (getTransactionEvent() != null) {
			 for (int i=0;
					 i<java.lang.reflect.Array.getLength(getTransactionEvent());
					 i++) {
				 java.lang.Object obj = java.lang.reflect.Array.get(getTransactionEvent(), i);
				 if (obj != null &&
						 !obj.getClass().isArray()) {
					 _hashCode += obj.hashCode();
				 }
			 }
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
	 new org.apache.axis.description.TypeDesc(EventListType.class, true);

	 static {
		 typeDesc.setXmlType(new javax.xml.namespace.QName("epcis.oliot.org", "EventListType"));
		 org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		 elemField.setFieldName("objectEvent");
		 elemField.setXmlName(new javax.xml.namespace.QName("", "ObjectEvent"));
		 elemField.setXmlType(new javax.xml.namespace.QName("epcis.oliot.org", "ObjectEventType"));
		 elemField.setMinOccurs(0);
		 elemField.setNillable(false);
		 elemField.setMaxOccursUnbounded(true);
		 typeDesc.addFieldDesc(elemField);
		 elemField = new org.apache.axis.description.ElementDesc();
		 elemField.setFieldName("aggregationEvent");
		 elemField.setXmlName(new javax.xml.namespace.QName("", "AggregationEvent"));
		 elemField.setXmlType(new javax.xml.namespace.QName("epcis.oliot.org", "AggregationEventType"));
		 elemField.setMinOccurs(0);
		 elemField.setNillable(false);
		 elemField.setMaxOccursUnbounded(true);
		 typeDesc.addFieldDesc(elemField);
		 elemField = new org.apache.axis.description.ElementDesc();
		 elemField.setFieldName("quantityEvent");
		 elemField.setXmlName(new javax.xml.namespace.QName("", "QuantityEvent"));
		 elemField.setXmlType(new javax.xml.namespace.QName("epcis.oliot.org", "QuantityEventType"));
		 elemField.setMinOccurs(0);
		 elemField.setNillable(false);
		 elemField.setMaxOccursUnbounded(true);
		 typeDesc.addFieldDesc(elemField);
		 elemField = new org.apache.axis.description.ElementDesc();
		 elemField.setFieldName("transactionEvent");
		 elemField.setXmlName(new javax.xml.namespace.QName("", "TransactionEvent"));
		 elemField.setXmlType(new javax.xml.namespace.QName("epcis.oliot.org", "TransactionEventType"));
		 elemField.setMinOccurs(0);
		 elemField.setNillable(false);
		 elemField.setMaxOccursUnbounded(true);
		 typeDesc.addFieldDesc(elemField);
		 elemField = new org.apache.axis.description.ElementDesc();
		 elemField.setFieldName("extension");
		 elemField.setXmlName(new javax.xml.namespace.QName("", "extension"));
		 elemField.setXmlType(new javax.xml.namespace.QName("epcis.oliot.org", "EPCISEventListExtensionType"));
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
