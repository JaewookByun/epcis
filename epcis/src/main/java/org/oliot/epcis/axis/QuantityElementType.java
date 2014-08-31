/**
 * QuantityElementType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.axis;

public class QuantityElementType  implements java.io.Serializable {
    private org.apache.axis.types.URI epcClass;

    private float quantity;

    private org.apache.axis.types.URI uom;

    public QuantityElementType() {
    }

    public QuantityElementType(
           org.apache.axis.types.URI epcClass,
           float quantity,
           org.apache.axis.types.URI uom) {
           this.epcClass = epcClass;
           this.quantity = quantity;
           this.uom = uom;
    }


    /**
     * Gets the epcClass value for this QuantityElementType.
     * 
     * @return epcClass
     */
    public org.apache.axis.types.URI getEpcClass() {
        return epcClass;
    }


    /**
     * Sets the epcClass value for this QuantityElementType.
     * 
     * @param epcClass
     */
    public void setEpcClass(org.apache.axis.types.URI epcClass) {
        this.epcClass = epcClass;
    }


    /**
     * Gets the quantity value for this QuantityElementType.
     * 
     * @return quantity
     */
    public float getQuantity() {
        return quantity;
    }


    /**
     * Sets the quantity value for this QuantityElementType.
     * 
     * @param quantity
     */
    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }


    /**
     * Gets the uom value for this QuantityElementType.
     * 
     * @return uom
     */
    public org.apache.axis.types.URI getUom() {
        return uom;
    }


    /**
     * Sets the uom value for this QuantityElementType.
     * 
     * @param uom
     */
    public void setUom(org.apache.axis.types.URI uom) {
        this.uom = uom;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof QuantityElementType)) return false;
        QuantityElementType other = (QuantityElementType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.epcClass==null && other.getEpcClass()==null) || 
             (this.epcClass!=null &&
              this.epcClass.equals(other.getEpcClass()))) &&
            this.quantity == other.getQuantity() &&
            ((this.uom==null && other.getUom()==null) || 
             (this.uom!=null &&
              this.uom.equals(other.getUom())));
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
        if (getEpcClass() != null) {
            _hashCode += getEpcClass().hashCode();
        }
        _hashCode += new Float(getQuantity()).hashCode();
        if (getUom() != null) {
            _hashCode += getUom().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(QuantityElementType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("axis.epcis.oliot.org", "QuantityElementType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("epcClass");
        elemField.setXmlName(new javax.xml.namespace.QName("", "epcClass"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyURI"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("quantity");
        elemField.setXmlName(new javax.xml.namespace.QName("", "quantity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("uom");
        elemField.setXmlName(new javax.xml.namespace.QName("", "uom"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyURI"));
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
