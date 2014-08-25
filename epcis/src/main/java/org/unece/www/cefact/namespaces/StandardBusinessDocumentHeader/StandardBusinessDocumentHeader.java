/**
 * StandardBusinessDocumentHeader.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader;

@SuppressWarnings("serial")
public class StandardBusinessDocumentHeader  implements java.io.Serializable {
    private java.lang.String headerVersion;

    private org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.Partner[] sender;

    private org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.Partner[] receiver;

    private org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.DocumentIdentification documentIdentification;

    private org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.Manifest manifest;

    private org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.Scope[] businessScope;

    public StandardBusinessDocumentHeader() {
    }

    public StandardBusinessDocumentHeader(
           java.lang.String headerVersion,
           org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.Partner[] sender,
           org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.Partner[] receiver,
           org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.DocumentIdentification documentIdentification,
           org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.Manifest manifest,
           org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.Scope[] businessScope) {
           this.headerVersion = headerVersion;
           this.sender = sender;
           this.receiver = receiver;
           this.documentIdentification = documentIdentification;
           this.manifest = manifest;
           this.businessScope = businessScope;
    }


    /**
     * Gets the headerVersion value for this StandardBusinessDocumentHeader.
     * 
     * @return headerVersion
     */
    public java.lang.String getHeaderVersion() {
        return headerVersion;
    }


    /**
     * Sets the headerVersion value for this StandardBusinessDocumentHeader.
     * 
     * @param headerVersion
     */
    public void setHeaderVersion(java.lang.String headerVersion) {
        this.headerVersion = headerVersion;
    }


    /**
     * Gets the sender value for this StandardBusinessDocumentHeader.
     * 
     * @return sender
     */
    public org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.Partner[] getSender() {
        return sender;
    }


    /**
     * Sets the sender value for this StandardBusinessDocumentHeader.
     * 
     * @param sender
     */
    public void setSender(org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.Partner[] sender) {
        this.sender = sender;
    }

    public org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.Partner getSender(int i) {
        return this.sender[i];
    }

    public void setSender(int i, org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.Partner _value) {
        this.sender[i] = _value;
    }


    /**
     * Gets the receiver value for this StandardBusinessDocumentHeader.
     * 
     * @return receiver
     */
    public org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.Partner[] getReceiver() {
        return receiver;
    }


    /**
     * Sets the receiver value for this StandardBusinessDocumentHeader.
     * 
     * @param receiver
     */
    public void setReceiver(org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.Partner[] receiver) {
        this.receiver = receiver;
    }

    public org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.Partner getReceiver(int i) {
        return this.receiver[i];
    }

    public void setReceiver(int i, org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.Partner _value) {
        this.receiver[i] = _value;
    }


    /**
     * Gets the documentIdentification value for this StandardBusinessDocumentHeader.
     * 
     * @return documentIdentification
     */
    public org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.DocumentIdentification getDocumentIdentification() {
        return documentIdentification;
    }


    /**
     * Sets the documentIdentification value for this StandardBusinessDocumentHeader.
     * 
     * @param documentIdentification
     */
    public void setDocumentIdentification(org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.DocumentIdentification documentIdentification) {
        this.documentIdentification = documentIdentification;
    }


    /**
     * Gets the manifest value for this StandardBusinessDocumentHeader.
     * 
     * @return manifest
     */
    public org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.Manifest getManifest() {
        return manifest;
    }


    /**
     * Sets the manifest value for this StandardBusinessDocumentHeader.
     * 
     * @param manifest
     */
    public void setManifest(org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.Manifest manifest) {
        this.manifest = manifest;
    }


    /**
     * Gets the businessScope value for this StandardBusinessDocumentHeader.
     * 
     * @return businessScope
     */
    public org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.Scope[] getBusinessScope() {
        return businessScope;
    }


    /**
     * Sets the businessScope value for this StandardBusinessDocumentHeader.
     * 
     * @param businessScope
     */
    public void setBusinessScope(org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.Scope[] businessScope) {
        this.businessScope = businessScope;
    }

    private java.lang.Object __equalsCalc = null;
    @SuppressWarnings("unused")
	public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof StandardBusinessDocumentHeader)) return false;
        StandardBusinessDocumentHeader other = (StandardBusinessDocumentHeader) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.headerVersion==null && other.getHeaderVersion()==null) || 
             (this.headerVersion!=null &&
              this.headerVersion.equals(other.getHeaderVersion()))) &&
            ((this.sender==null && other.getSender()==null) || 
             (this.sender!=null &&
              java.util.Arrays.equals(this.sender, other.getSender()))) &&
            ((this.receiver==null && other.getReceiver()==null) || 
             (this.receiver!=null &&
              java.util.Arrays.equals(this.receiver, other.getReceiver()))) &&
            ((this.documentIdentification==null && other.getDocumentIdentification()==null) || 
             (this.documentIdentification!=null &&
              this.documentIdentification.equals(other.getDocumentIdentification()))) &&
            ((this.manifest==null && other.getManifest()==null) || 
             (this.manifest!=null &&
              this.manifest.equals(other.getManifest()))) &&
            ((this.businessScope==null && other.getBusinessScope()==null) || 
             (this.businessScope!=null &&
              java.util.Arrays.equals(this.businessScope, other.getBusinessScope())));
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
        if (getHeaderVersion() != null) {
            _hashCode += getHeaderVersion().hashCode();
        }
        if (getSender() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSender());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSender(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getReceiver() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getReceiver());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getReceiver(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getDocumentIdentification() != null) {
            _hashCode += getDocumentIdentification().hashCode();
        }
        if (getManifest() != null) {
            _hashCode += getManifest().hashCode();
        }
        if (getBusinessScope() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getBusinessScope());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getBusinessScope(), i);
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
        new org.apache.axis.description.TypeDesc(StandardBusinessDocumentHeader.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "StandardBusinessDocumentHeader"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("headerVersion");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "HeaderVersion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sender");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "Sender"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "Partner"));
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("receiver");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "Receiver"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "Partner"));
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("documentIdentification");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "DocumentIdentification"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "DocumentIdentification"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("manifest");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "Manifest"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "Manifest"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("businessScope");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "BusinessScope"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "Scope"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "Scope"));
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
