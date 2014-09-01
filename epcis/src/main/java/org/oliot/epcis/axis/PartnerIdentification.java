/**
 * PartnerIdentification.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.axis;

public class PartnerIdentification  implements java.io.Serializable, org.apache.axis.encoding.SimpleType {
    private java.lang.String _value;

    private java.lang.String authority;  // attribute

    public PartnerIdentification() {
    }

    // Simple Types must have a String constructor
    public PartnerIdentification(java.lang.String _value) {
        this._value = _value;
    }
    // Simple Types must have a toString for serializing the value
    public java.lang.String toString() {
        return _value;
    }


    /**
     * Gets the _value value for this PartnerIdentification.
     * 
     * @return _value
     */
    public java.lang.String get_value() {
        return _value;
    }


    /**
     * Sets the _value value for this PartnerIdentification.
     * 
     * @param _value
     */
    public void set_value(java.lang.String _value) {
        this._value = _value;
    }


    /**
     * Gets the authority value for this PartnerIdentification.
     * 
     * @return authority
     */
    public java.lang.String getAuthority() {
        return authority;
    }


    /**
     * Sets the authority value for this PartnerIdentification.
     * 
     * @param authority
     */
    public void setAuthority(java.lang.String authority) {
        this.authority = authority;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PartnerIdentification)) return false;
        PartnerIdentification other = (PartnerIdentification) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this._value==null && other.get_value()==null) || 
             (this._value!=null &&
              this._value.equals(other.get_value()))) &&
            ((this.authority==null && other.getAuthority()==null) || 
             (this.authority!=null &&
              this.authority.equals(other.getAuthority())));
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
        if (get_value() != null) {
            _hashCode += get_value().hashCode();
        }
        if (getAuthority() != null) {
            _hashCode += getAuthority().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PartnerIdentification.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "PartnerIdentification"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("authority");
        attrField.setXmlName(new javax.xml.namespace.QName("", "Authority"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("_value");
        elemField.setXmlName(new javax.xml.namespace.QName("", "_value"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
          new  org.apache.axis.encoding.ser.SimpleSerializer(
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
          new  org.apache.axis.encoding.ser.SimpleDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
