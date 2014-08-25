/**
 * DocumentIdentification.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader;

public class DocumentIdentification  implements java.io.Serializable {
    private java.lang.String standard;

    private java.lang.String typeVersion;

    private java.lang.String instanceIdentifier;

    private java.lang.String type;

    private java.lang.Boolean multipleType;

    private java.util.Calendar creationDateAndTime;

    public DocumentIdentification() {
    }

    public DocumentIdentification(
           java.lang.String standard,
           java.lang.String typeVersion,
           java.lang.String instanceIdentifier,
           java.lang.String type,
           java.lang.Boolean multipleType,
           java.util.Calendar creationDateAndTime) {
           this.standard = standard;
           this.typeVersion = typeVersion;
           this.instanceIdentifier = instanceIdentifier;
           this.type = type;
           this.multipleType = multipleType;
           this.creationDateAndTime = creationDateAndTime;
    }


    /**
     * Gets the standard value for this DocumentIdentification.
     * 
     * @return standard
     */
    public java.lang.String getStandard() {
        return standard;
    }


    /**
     * Sets the standard value for this DocumentIdentification.
     * 
     * @param standard
     */
    public void setStandard(java.lang.String standard) {
        this.standard = standard;
    }


    /**
     * Gets the typeVersion value for this DocumentIdentification.
     * 
     * @return typeVersion
     */
    public java.lang.String getTypeVersion() {
        return typeVersion;
    }


    /**
     * Sets the typeVersion value for this DocumentIdentification.
     * 
     * @param typeVersion
     */
    public void setTypeVersion(java.lang.String typeVersion) {
        this.typeVersion = typeVersion;
    }


    /**
     * Gets the instanceIdentifier value for this DocumentIdentification.
     * 
     * @return instanceIdentifier
     */
    public java.lang.String getInstanceIdentifier() {
        return instanceIdentifier;
    }


    /**
     * Sets the instanceIdentifier value for this DocumentIdentification.
     * 
     * @param instanceIdentifier
     */
    public void setInstanceIdentifier(java.lang.String instanceIdentifier) {
        this.instanceIdentifier = instanceIdentifier;
    }


    /**
     * Gets the type value for this DocumentIdentification.
     * 
     * @return type
     */
    public java.lang.String getType() {
        return type;
    }


    /**
     * Sets the type value for this DocumentIdentification.
     * 
     * @param type
     */
    public void setType(java.lang.String type) {
        this.type = type;
    }


    /**
     * Gets the multipleType value for this DocumentIdentification.
     * 
     * @return multipleType
     */
    public java.lang.Boolean getMultipleType() {
        return multipleType;
    }


    /**
     * Sets the multipleType value for this DocumentIdentification.
     * 
     * @param multipleType
     */
    public void setMultipleType(java.lang.Boolean multipleType) {
        this.multipleType = multipleType;
    }


    /**
     * Gets the creationDateAndTime value for this DocumentIdentification.
     * 
     * @return creationDateAndTime
     */
    public java.util.Calendar getCreationDateAndTime() {
        return creationDateAndTime;
    }


    /**
     * Sets the creationDateAndTime value for this DocumentIdentification.
     * 
     * @param creationDateAndTime
     */
    public void setCreationDateAndTime(java.util.Calendar creationDateAndTime) {
        this.creationDateAndTime = creationDateAndTime;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DocumentIdentification)) return false;
        DocumentIdentification other = (DocumentIdentification) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.standard==null && other.getStandard()==null) || 
             (this.standard!=null &&
              this.standard.equals(other.getStandard()))) &&
            ((this.typeVersion==null && other.getTypeVersion()==null) || 
             (this.typeVersion!=null &&
              this.typeVersion.equals(other.getTypeVersion()))) &&
            ((this.instanceIdentifier==null && other.getInstanceIdentifier()==null) || 
             (this.instanceIdentifier!=null &&
              this.instanceIdentifier.equals(other.getInstanceIdentifier()))) &&
            ((this.type==null && other.getType()==null) || 
             (this.type!=null &&
              this.type.equals(other.getType()))) &&
            ((this.multipleType==null && other.getMultipleType()==null) || 
             (this.multipleType!=null &&
              this.multipleType.equals(other.getMultipleType()))) &&
            ((this.creationDateAndTime==null && other.getCreationDateAndTime()==null) || 
             (this.creationDateAndTime!=null &&
              this.creationDateAndTime.equals(other.getCreationDateAndTime())));
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
        if (getStandard() != null) {
            _hashCode += getStandard().hashCode();
        }
        if (getTypeVersion() != null) {
            _hashCode += getTypeVersion().hashCode();
        }
        if (getInstanceIdentifier() != null) {
            _hashCode += getInstanceIdentifier().hashCode();
        }
        if (getType() != null) {
            _hashCode += getType().hashCode();
        }
        if (getMultipleType() != null) {
            _hashCode += getMultipleType().hashCode();
        }
        if (getCreationDateAndTime() != null) {
            _hashCode += getCreationDateAndTime().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DocumentIdentification.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "DocumentIdentification"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("standard");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "Standard"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("typeVersion");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "TypeVersion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("instanceIdentifier");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "InstanceIdentifier"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("type");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "Type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("multipleType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "MultipleType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("creationDateAndTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "CreationDateAndTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
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
