/**
 * VocabularyElementType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.masterdata;

@SuppressWarnings("serial")
public class VocabularyElementType  implements java.io.Serializable, org.apache.axis.encoding.AnyContentType {
    private org.oliot.epcis.masterdata.AttributeType[] attribute;

    private org.apache.axis.types.URI[] children;

    private org.oliot.epcis.masterdata.VocabularyElementExtensionType extension;

    private org.apache.axis.message.MessageElement [] _any;

    private org.apache.axis.types.URI id;  // attribute

    public VocabularyElementType() {
    }

    public VocabularyElementType(
           org.oliot.epcis.masterdata.AttributeType[] attribute,
           org.apache.axis.types.URI[] children,
           org.oliot.epcis.masterdata.VocabularyElementExtensionType extension,
           org.apache.axis.message.MessageElement [] _any,
           org.apache.axis.types.URI id) {
           this.attribute = attribute;
           this.children = children;
           this.extension = extension;
           this._any = _any;
           this.id = id;
    }


    /**
     * Gets the attribute value for this VocabularyElementType.
     * 
     * @return attribute
     */
    public org.oliot.epcis.masterdata.AttributeType[] getAttribute() {
        return attribute;
    }


    /**
     * Sets the attribute value for this VocabularyElementType.
     * 
     * @param attribute
     */
    public void setAttribute(org.oliot.epcis.masterdata.AttributeType[] attribute) {
        this.attribute = attribute;
    }

    public org.oliot.epcis.masterdata.AttributeType getAttribute(int i) {
        return this.attribute[i];
    }

    public void setAttribute(int i, org.oliot.epcis.masterdata.AttributeType _value) {
        this.attribute[i] = _value;
    }


    /**
     * Gets the children value for this VocabularyElementType.
     * 
     * @return children
     */
    public org.apache.axis.types.URI[] getChildren() {
        return children;
    }


    /**
     * Sets the children value for this VocabularyElementType.
     * 
     * @param children
     */
    public void setChildren(org.apache.axis.types.URI[] children) {
        this.children = children;
    }


    /**
     * Gets the extension value for this VocabularyElementType.
     * 
     * @return extension
     */
    public org.oliot.epcis.masterdata.VocabularyElementExtensionType getExtension() {
        return extension;
    }


    /**
     * Sets the extension value for this VocabularyElementType.
     * 
     * @param extension
     */
    public void setExtension(org.oliot.epcis.masterdata.VocabularyElementExtensionType extension) {
        this.extension = extension;
    }


    /**
     * Gets the _any value for this VocabularyElementType.
     * 
     * @return _any
     */
    public org.apache.axis.message.MessageElement [] get_any() {
        return _any;
    }


    /**
     * Sets the _any value for this VocabularyElementType.
     * 
     * @param _any
     */
    public void set_any(org.apache.axis.message.MessageElement [] _any) {
        this._any = _any;
    }


    /**
     * Gets the id value for this VocabularyElementType.
     * 
     * @return id
     */
    public org.apache.axis.types.URI getId() {
        return id;
    }


    /**
     * Sets the id value for this VocabularyElementType.
     * 
     * @param id
     */
    public void setId(org.apache.axis.types.URI id) {
        this.id = id;
    }

    private java.lang.Object __equalsCalc = null;
    @SuppressWarnings("unused")
	public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof VocabularyElementType)) return false;
        VocabularyElementType other = (VocabularyElementType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.attribute==null && other.getAttribute()==null) || 
             (this.attribute!=null &&
              java.util.Arrays.equals(this.attribute, other.getAttribute()))) &&
            ((this.children==null && other.getChildren()==null) || 
             (this.children!=null &&
              java.util.Arrays.equals(this.children, other.getChildren()))) &&
            ((this.extension==null && other.getExtension()==null) || 
             (this.extension!=null &&
              this.extension.equals(other.getExtension()))) &&
            ((this._any==null && other.get_any()==null) || 
             (this._any!=null &&
              java.util.Arrays.equals(this._any, other.get_any()))) &&
            ((this.id==null && other.getId()==null) || 
             (this.id!=null &&
              this.id.equals(other.getId())));
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
        if (getAttribute() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAttribute());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAttribute(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getChildren() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getChildren());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getChildren(), i);
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
        if (getId() != null) {
            _hashCode += getId().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(VocabularyElementType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("masterdata.epcis.oliot.org", "VocabularyElementType"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("id");
        attrField.setXmlName(new javax.xml.namespace.QName("", "id"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyURI"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("attribute");
        elemField.setXmlName(new javax.xml.namespace.QName("", "attribute"));
        elemField.setXmlType(new javax.xml.namespace.QName("masterdata.epcis.oliot.org", "AttributeType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("children");
        elemField.setXmlName(new javax.xml.namespace.QName("", "children"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyURI"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("", "id"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extension");
        elemField.setXmlName(new javax.xml.namespace.QName("", "extension"));
        elemField.setXmlType(new javax.xml.namespace.QName("masterdata.epcis.oliot.org", "VocabularyElementExtensionType"));
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
