/**
 * CorrelationInformation.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader;

public class CorrelationInformation  implements java.io.Serializable {
    private java.util.Calendar requestingDocumentCreationDateTime;

    private java.lang.String requestingDocumentInstanceIdentifier;

    private java.util.Calendar expectedResponseDateTime;

    public CorrelationInformation() {
    }

    public CorrelationInformation(
           java.util.Calendar requestingDocumentCreationDateTime,
           java.lang.String requestingDocumentInstanceIdentifier,
           java.util.Calendar expectedResponseDateTime) {
           this.requestingDocumentCreationDateTime = requestingDocumentCreationDateTime;
           this.requestingDocumentInstanceIdentifier = requestingDocumentInstanceIdentifier;
           this.expectedResponseDateTime = expectedResponseDateTime;
    }


    /**
     * Gets the requestingDocumentCreationDateTime value for this CorrelationInformation.
     * 
     * @return requestingDocumentCreationDateTime
     */
    public java.util.Calendar getRequestingDocumentCreationDateTime() {
        return requestingDocumentCreationDateTime;
    }


    /**
     * Sets the requestingDocumentCreationDateTime value for this CorrelationInformation.
     * 
     * @param requestingDocumentCreationDateTime
     */
    public void setRequestingDocumentCreationDateTime(java.util.Calendar requestingDocumentCreationDateTime) {
        this.requestingDocumentCreationDateTime = requestingDocumentCreationDateTime;
    }


    /**
     * Gets the requestingDocumentInstanceIdentifier value for this CorrelationInformation.
     * 
     * @return requestingDocumentInstanceIdentifier
     */
    public java.lang.String getRequestingDocumentInstanceIdentifier() {
        return requestingDocumentInstanceIdentifier;
    }


    /**
     * Sets the requestingDocumentInstanceIdentifier value for this CorrelationInformation.
     * 
     * @param requestingDocumentInstanceIdentifier
     */
    public void setRequestingDocumentInstanceIdentifier(java.lang.String requestingDocumentInstanceIdentifier) {
        this.requestingDocumentInstanceIdentifier = requestingDocumentInstanceIdentifier;
    }


    /**
     * Gets the expectedResponseDateTime value for this CorrelationInformation.
     * 
     * @return expectedResponseDateTime
     */
    public java.util.Calendar getExpectedResponseDateTime() {
        return expectedResponseDateTime;
    }


    /**
     * Sets the expectedResponseDateTime value for this CorrelationInformation.
     * 
     * @param expectedResponseDateTime
     */
    public void setExpectedResponseDateTime(java.util.Calendar expectedResponseDateTime) {
        this.expectedResponseDateTime = expectedResponseDateTime;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CorrelationInformation)) return false;
        CorrelationInformation other = (CorrelationInformation) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.requestingDocumentCreationDateTime==null && other.getRequestingDocumentCreationDateTime()==null) || 
             (this.requestingDocumentCreationDateTime!=null &&
              this.requestingDocumentCreationDateTime.equals(other.getRequestingDocumentCreationDateTime()))) &&
            ((this.requestingDocumentInstanceIdentifier==null && other.getRequestingDocumentInstanceIdentifier()==null) || 
             (this.requestingDocumentInstanceIdentifier!=null &&
              this.requestingDocumentInstanceIdentifier.equals(other.getRequestingDocumentInstanceIdentifier()))) &&
            ((this.expectedResponseDateTime==null && other.getExpectedResponseDateTime()==null) || 
             (this.expectedResponseDateTime!=null &&
              this.expectedResponseDateTime.equals(other.getExpectedResponseDateTime())));
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
        if (getRequestingDocumentCreationDateTime() != null) {
            _hashCode += getRequestingDocumentCreationDateTime().hashCode();
        }
        if (getRequestingDocumentInstanceIdentifier() != null) {
            _hashCode += getRequestingDocumentInstanceIdentifier().hashCode();
        }
        if (getExpectedResponseDateTime() != null) {
            _hashCode += getExpectedResponseDateTime().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CorrelationInformation.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "CorrelationInformation"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("requestingDocumentCreationDateTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "RequestingDocumentCreationDateTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("requestingDocumentInstanceIdentifier");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "RequestingDocumentInstanceIdentifier"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("expectedResponseDateTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "ExpectedResponseDateTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
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
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
