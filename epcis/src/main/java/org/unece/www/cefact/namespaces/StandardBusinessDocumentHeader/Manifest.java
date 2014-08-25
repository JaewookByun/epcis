/**
 * Manifest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader;

public class Manifest  implements java.io.Serializable {
    private java.math.BigInteger numberOfItems;

    private org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.ManifestItem[] manifestItem;

    public Manifest() {
    }

    public Manifest(
           java.math.BigInteger numberOfItems,
           org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.ManifestItem[] manifestItem) {
           this.numberOfItems = numberOfItems;
           this.manifestItem = manifestItem;
    }


    /**
     * Gets the numberOfItems value for this Manifest.
     * 
     * @return numberOfItems
     */
    public java.math.BigInteger getNumberOfItems() {
        return numberOfItems;
    }


    /**
     * Sets the numberOfItems value for this Manifest.
     * 
     * @param numberOfItems
     */
    public void setNumberOfItems(java.math.BigInteger numberOfItems) {
        this.numberOfItems = numberOfItems;
    }


    /**
     * Gets the manifestItem value for this Manifest.
     * 
     * @return manifestItem
     */
    public org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.ManifestItem[] getManifestItem() {
        return manifestItem;
    }


    /**
     * Sets the manifestItem value for this Manifest.
     * 
     * @param manifestItem
     */
    public void setManifestItem(org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.ManifestItem[] manifestItem) {
        this.manifestItem = manifestItem;
    }

    public org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.ManifestItem getManifestItem(int i) {
        return this.manifestItem[i];
    }

    public void setManifestItem(int i, org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.ManifestItem _value) {
        this.manifestItem[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Manifest)) return false;
        Manifest other = (Manifest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.numberOfItems==null && other.getNumberOfItems()==null) || 
             (this.numberOfItems!=null &&
              this.numberOfItems.equals(other.getNumberOfItems()))) &&
            ((this.manifestItem==null && other.getManifestItem()==null) || 
             (this.manifestItem!=null &&
              java.util.Arrays.equals(this.manifestItem, other.getManifestItem())));
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
        if (getNumberOfItems() != null) {
            _hashCode += getNumberOfItems().hashCode();
        }
        if (getManifestItem() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getManifestItem());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getManifestItem(), i);
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
        new org.apache.axis.description.TypeDesc(Manifest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "Manifest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numberOfItems");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "NumberOfItems"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("manifestItem");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "ManifestItem"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "ManifestItem"));
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
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
