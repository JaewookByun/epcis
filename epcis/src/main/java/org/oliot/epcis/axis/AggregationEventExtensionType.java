/**
 * AggregationEventExtensionType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.axis;

public class AggregationEventExtensionType  implements java.io.Serializable {
    private org.oliot.epcis.axis.QuantityElementType[] childQuantityList;

    private org.oliot.epcis.axis.SourceDestType[] sourceList;

    private org.oliot.epcis.axis.SourceDestType[] destinationList;

    private org.oliot.epcis.axis.AggregationEventExtension2Type extension;

    public AggregationEventExtensionType() {
    }

    public AggregationEventExtensionType(
           org.oliot.epcis.axis.QuantityElementType[] childQuantityList,
           org.oliot.epcis.axis.SourceDestType[] sourceList,
           org.oliot.epcis.axis.SourceDestType[] destinationList,
           org.oliot.epcis.axis.AggregationEventExtension2Type extension) {
           this.childQuantityList = childQuantityList;
           this.sourceList = sourceList;
           this.destinationList = destinationList;
           this.extension = extension;
    }


    /**
     * Gets the childQuantityList value for this AggregationEventExtensionType.
     * 
     * @return childQuantityList
     */
    public org.oliot.epcis.axis.QuantityElementType[] getChildQuantityList() {
        return childQuantityList;
    }


    /**
     * Sets the childQuantityList value for this AggregationEventExtensionType.
     * 
     * @param childQuantityList
     */
    public void setChildQuantityList(org.oliot.epcis.axis.QuantityElementType[] childQuantityList) {
        this.childQuantityList = childQuantityList;
    }


    /**
     * Gets the sourceList value for this AggregationEventExtensionType.
     * 
     * @return sourceList
     */
    public org.oliot.epcis.axis.SourceDestType[] getSourceList() {
        return sourceList;
    }


    /**
     * Sets the sourceList value for this AggregationEventExtensionType.
     * 
     * @param sourceList
     */
    public void setSourceList(org.oliot.epcis.axis.SourceDestType[] sourceList) {
        this.sourceList = sourceList;
    }


    /**
     * Gets the destinationList value for this AggregationEventExtensionType.
     * 
     * @return destinationList
     */
    public org.oliot.epcis.axis.SourceDestType[] getDestinationList() {
        return destinationList;
    }


    /**
     * Sets the destinationList value for this AggregationEventExtensionType.
     * 
     * @param destinationList
     */
    public void setDestinationList(org.oliot.epcis.axis.SourceDestType[] destinationList) {
        this.destinationList = destinationList;
    }


    /**
     * Gets the extension value for this AggregationEventExtensionType.
     * 
     * @return extension
     */
    public org.oliot.epcis.axis.AggregationEventExtension2Type getExtension() {
        return extension;
    }


    /**
     * Sets the extension value for this AggregationEventExtensionType.
     * 
     * @param extension
     */
    public void setExtension(org.oliot.epcis.axis.AggregationEventExtension2Type extension) {
        this.extension = extension;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AggregationEventExtensionType)) return false;
        AggregationEventExtensionType other = (AggregationEventExtensionType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.childQuantityList==null && other.getChildQuantityList()==null) || 
             (this.childQuantityList!=null &&
              java.util.Arrays.equals(this.childQuantityList, other.getChildQuantityList()))) &&
            ((this.sourceList==null && other.getSourceList()==null) || 
             (this.sourceList!=null &&
              java.util.Arrays.equals(this.sourceList, other.getSourceList()))) &&
            ((this.destinationList==null && other.getDestinationList()==null) || 
             (this.destinationList!=null &&
              java.util.Arrays.equals(this.destinationList, other.getDestinationList()))) &&
            ((this.extension==null && other.getExtension()==null) || 
             (this.extension!=null &&
              this.extension.equals(other.getExtension())));
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
        if (getChildQuantityList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getChildQuantityList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getChildQuantityList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getSourceList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSourceList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSourceList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getDestinationList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getDestinationList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getDestinationList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getExtension() != null) {
            _hashCode += getExtension().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AggregationEventExtensionType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("axis.epcis.oliot.org", "AggregationEventExtensionType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("childQuantityList");
        elemField.setXmlName(new javax.xml.namespace.QName("", "childQuantityList"));
        elemField.setXmlType(new javax.xml.namespace.QName("axis.epcis.oliot.org", "QuantityElementType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("", "quantityElement"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sourceList");
        elemField.setXmlName(new javax.xml.namespace.QName("", "sourceList"));
        elemField.setXmlType(new javax.xml.namespace.QName("axis.epcis.oliot.org", "SourceDestType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("", "source"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("destinationList");
        elemField.setXmlName(new javax.xml.namespace.QName("", "destinationList"));
        elemField.setXmlType(new javax.xml.namespace.QName("axis.epcis.oliot.org", "SourceDestType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("", "destination"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extension");
        elemField.setXmlName(new javax.xml.namespace.QName("", "extension"));
        elemField.setXmlType(new javax.xml.namespace.QName("axis.epcis.oliot.org", "AggregationEventExtension2Type"));
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
