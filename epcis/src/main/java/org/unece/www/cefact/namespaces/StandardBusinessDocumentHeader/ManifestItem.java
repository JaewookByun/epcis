/**
 * ManifestItem.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader;

@SuppressWarnings("serial")
public class ManifestItem  implements java.io.Serializable {
    private java.lang.String mimeTypeQualifierCode;

    private org.apache.axis.types.URI uniformResourceIdentifier;

    private java.lang.String description;

    private java.lang.String languageCode;

    public ManifestItem() {
    }

    public ManifestItem(
           java.lang.String mimeTypeQualifierCode,
           org.apache.axis.types.URI uniformResourceIdentifier,
           java.lang.String description,
           java.lang.String languageCode) {
           this.mimeTypeQualifierCode = mimeTypeQualifierCode;
           this.uniformResourceIdentifier = uniformResourceIdentifier;
           this.description = description;
           this.languageCode = languageCode;
    }


    /**
     * Gets the mimeTypeQualifierCode value for this ManifestItem.
     * 
     * @return mimeTypeQualifierCode
     */
    public java.lang.String getMimeTypeQualifierCode() {
        return mimeTypeQualifierCode;
    }


    /**
     * Sets the mimeTypeQualifierCode value for this ManifestItem.
     * 
     * @param mimeTypeQualifierCode
     */
    public void setMimeTypeQualifierCode(java.lang.String mimeTypeQualifierCode) {
        this.mimeTypeQualifierCode = mimeTypeQualifierCode;
    }


    /**
     * Gets the uniformResourceIdentifier value for this ManifestItem.
     * 
     * @return uniformResourceIdentifier
     */
    public org.apache.axis.types.URI getUniformResourceIdentifier() {
        return uniformResourceIdentifier;
    }


    /**
     * Sets the uniformResourceIdentifier value for this ManifestItem.
     * 
     * @param uniformResourceIdentifier
     */
    public void setUniformResourceIdentifier(org.apache.axis.types.URI uniformResourceIdentifier) {
        this.uniformResourceIdentifier = uniformResourceIdentifier;
    }


    /**
     * Gets the description value for this ManifestItem.
     * 
     * @return description
     */
    public java.lang.String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this ManifestItem.
     * 
     * @param description
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }


    /**
     * Gets the languageCode value for this ManifestItem.
     * 
     * @return languageCode
     */
    public java.lang.String getLanguageCode() {
        return languageCode;
    }


    /**
     * Sets the languageCode value for this ManifestItem.
     * 
     * @param languageCode
     */
    public void setLanguageCode(java.lang.String languageCode) {
        this.languageCode = languageCode;
    }

    private java.lang.Object __equalsCalc = null;
    @SuppressWarnings("unused")
	public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ManifestItem)) return false;
        ManifestItem other = (ManifestItem) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.mimeTypeQualifierCode==null && other.getMimeTypeQualifierCode()==null) || 
             (this.mimeTypeQualifierCode!=null &&
              this.mimeTypeQualifierCode.equals(other.getMimeTypeQualifierCode()))) &&
            ((this.uniformResourceIdentifier==null && other.getUniformResourceIdentifier()==null) || 
             (this.uniformResourceIdentifier!=null &&
              this.uniformResourceIdentifier.equals(other.getUniformResourceIdentifier()))) &&
            ((this.description==null && other.getDescription()==null) || 
             (this.description!=null &&
              this.description.equals(other.getDescription()))) &&
            ((this.languageCode==null && other.getLanguageCode()==null) || 
             (this.languageCode!=null &&
              this.languageCode.equals(other.getLanguageCode())));
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
        if (getMimeTypeQualifierCode() != null) {
            _hashCode += getMimeTypeQualifierCode().hashCode();
        }
        if (getUniformResourceIdentifier() != null) {
            _hashCode += getUniformResourceIdentifier().hashCode();
        }
        if (getDescription() != null) {
            _hashCode += getDescription().hashCode();
        }
        if (getLanguageCode() != null) {
            _hashCode += getLanguageCode().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ManifestItem.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "ManifestItem"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mimeTypeQualifierCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "MimeTypeQualifierCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("uniformResourceIdentifier");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "UniformResourceIdentifier"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyURI"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("description");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "Description"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("languageCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "LanguageCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
