/**
 * VocabularyType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.axis;

public class VocabularyType  implements java.io.Serializable, org.apache.axis.encoding.AnyContentType {
    private org.oliot.epcis.axis.VocabularyElementType[] vocabularyElementList;

    private org.oliot.epcis.axis.VocabularyExtensionType extension;

    private org.apache.axis.message.MessageElement [] _any;

    private org.apache.axis.types.URI type;  // attribute

    public VocabularyType() {
    }

    public VocabularyType(
           org.oliot.epcis.axis.VocabularyElementType[] vocabularyElementList,
           org.oliot.epcis.axis.VocabularyExtensionType extension,
           org.apache.axis.message.MessageElement [] _any,
           org.apache.axis.types.URI type) {
           this.vocabularyElementList = vocabularyElementList;
           this.extension = extension;
           this._any = _any;
           this.type = type;
    }


    /**
     * Gets the vocabularyElementList value for this VocabularyType.
     * 
     * @return vocabularyElementList
     */
    public org.oliot.epcis.axis.VocabularyElementType[] getVocabularyElementList() {
        return vocabularyElementList;
    }


    /**
     * Sets the vocabularyElementList value for this VocabularyType.
     * 
     * @param vocabularyElementList
     */
    public void setVocabularyElementList(org.oliot.epcis.axis.VocabularyElementType[] vocabularyElementList) {
        this.vocabularyElementList = vocabularyElementList;
    }


    /**
     * Gets the extension value for this VocabularyType.
     * 
     * @return extension
     */
    public org.oliot.epcis.axis.VocabularyExtensionType getExtension() {
        return extension;
    }


    /**
     * Sets the extension value for this VocabularyType.
     * 
     * @param extension
     */
    public void setExtension(org.oliot.epcis.axis.VocabularyExtensionType extension) {
        this.extension = extension;
    }


    /**
     * Gets the _any value for this VocabularyType.
     * 
     * @return _any
     */
    public org.apache.axis.message.MessageElement [] get_any() {
        return _any;
    }


    /**
     * Sets the _any value for this VocabularyType.
     * 
     * @param _any
     */
    public void set_any(org.apache.axis.message.MessageElement [] _any) {
        this._any = _any;
    }


    /**
     * Gets the type value for this VocabularyType.
     * 
     * @return type
     */
    public org.apache.axis.types.URI getType() {
        return type;
    }


    /**
     * Sets the type value for this VocabularyType.
     * 
     * @param type
     */
    public void setType(org.apache.axis.types.URI type) {
        this.type = type;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof VocabularyType)) return false;
        VocabularyType other = (VocabularyType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.vocabularyElementList==null && other.getVocabularyElementList()==null) || 
             (this.vocabularyElementList!=null &&
              java.util.Arrays.equals(this.vocabularyElementList, other.getVocabularyElementList()))) &&
            ((this.extension==null && other.getExtension()==null) || 
             (this.extension!=null &&
              this.extension.equals(other.getExtension()))) &&
            ((this._any==null && other.get_any()==null) || 
             (this._any!=null &&
              java.util.Arrays.equals(this._any, other.get_any()))) &&
            ((this.type==null && other.getType()==null) || 
             (this.type!=null &&
              this.type.equals(other.getType())));
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
        if (getVocabularyElementList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getVocabularyElementList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getVocabularyElementList(), i);
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
        if (getType() != null) {
            _hashCode += getType().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(VocabularyType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("masterdata.axis.epcis.oliot.org", "VocabularyType"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("type");
        attrField.setXmlName(new javax.xml.namespace.QName("", "type"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyURI"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("vocabularyElementList");
        elemField.setXmlName(new javax.xml.namespace.QName("", "VocabularyElementList"));
        elemField.setXmlType(new javax.xml.namespace.QName("masterdata.axis.epcis.oliot.org", "VocabularyElementType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("", "VocabularyElement"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extension");
        elemField.setXmlName(new javax.xml.namespace.QName("", "extension"));
        elemField.setXmlType(new javax.xml.namespace.QName("masterdata.axis.epcis.oliot.org", "VocabularyExtensionType"));
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
