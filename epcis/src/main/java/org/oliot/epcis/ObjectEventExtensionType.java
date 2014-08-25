/**
 * ObjectEventExtensionType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis;

@SuppressWarnings("serial")
public class ObjectEventExtensionType  implements java.io.Serializable {
    private org.oliot.epcis.QuantityElementType[] quantityList;

    private org.oliot.epcis.SourceDestType[] sourceList;

    private org.oliot.epcis.SourceDestType[] destinationList;

    private org.oliot.epcis.ILMDType ilmd;

    private org.oliot.epcis.ObjectEventExtension2Type extension;

    public ObjectEventExtensionType() {
    }

    public ObjectEventExtensionType(
           org.oliot.epcis.QuantityElementType[] quantityList,
           org.oliot.epcis.SourceDestType[] sourceList,
           org.oliot.epcis.SourceDestType[] destinationList,
           org.oliot.epcis.ILMDType ilmd,
           org.oliot.epcis.ObjectEventExtension2Type extension) {
           this.quantityList = quantityList;
           this.sourceList = sourceList;
           this.destinationList = destinationList;
           this.ilmd = ilmd;
           this.extension = extension;
    }


    /**
     * Gets the quantityList value for this ObjectEventExtensionType.
     * 
     * @return quantityList
     */
    public org.oliot.epcis.QuantityElementType[] getQuantityList() {
        return quantityList;
    }


    /**
     * Sets the quantityList value for this ObjectEventExtensionType.
     * 
     * @param quantityList
     */
    public void setQuantityList(org.oliot.epcis.QuantityElementType[] quantityList) {
        this.quantityList = quantityList;
    }


    /**
     * Gets the sourceList value for this ObjectEventExtensionType.
     * 
     * @return sourceList
     */
    public org.oliot.epcis.SourceDestType[] getSourceList() {
        return sourceList;
    }


    /**
     * Sets the sourceList value for this ObjectEventExtensionType.
     * 
     * @param sourceList
     */
    public void setSourceList(org.oliot.epcis.SourceDestType[] sourceList) {
        this.sourceList = sourceList;
    }


    /**
     * Gets the destinationList value for this ObjectEventExtensionType.
     * 
     * @return destinationList
     */
    public org.oliot.epcis.SourceDestType[] getDestinationList() {
        return destinationList;
    }


    /**
     * Sets the destinationList value for this ObjectEventExtensionType.
     * 
     * @param destinationList
     */
    public void setDestinationList(org.oliot.epcis.SourceDestType[] destinationList) {
        this.destinationList = destinationList;
    }


    /**
     * Gets the ilmd value for this ObjectEventExtensionType.
     * 
     * @return ilmd
     */
    public org.oliot.epcis.ILMDType getIlmd() {
        return ilmd;
    }


    /**
     * Sets the ilmd value for this ObjectEventExtensionType.
     * 
     * @param ilmd
     */
    public void setIlmd(org.oliot.epcis.ILMDType ilmd) {
        this.ilmd = ilmd;
    }


    /**
     * Gets the extension value for this ObjectEventExtensionType.
     * 
     * @return extension
     */
    public org.oliot.epcis.ObjectEventExtension2Type getExtension() {
        return extension;
    }


    /**
     * Sets the extension value for this ObjectEventExtensionType.
     * 
     * @param extension
     */
    public void setExtension(org.oliot.epcis.ObjectEventExtension2Type extension) {
        this.extension = extension;
    }

    private java.lang.Object __equalsCalc = null;
    @SuppressWarnings("unused")
	public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ObjectEventExtensionType)) return false;
        ObjectEventExtensionType other = (ObjectEventExtensionType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.quantityList==null && other.getQuantityList()==null) || 
             (this.quantityList!=null &&
              java.util.Arrays.equals(this.quantityList, other.getQuantityList()))) &&
            ((this.sourceList==null && other.getSourceList()==null) || 
             (this.sourceList!=null &&
              java.util.Arrays.equals(this.sourceList, other.getSourceList()))) &&
            ((this.destinationList==null && other.getDestinationList()==null) || 
             (this.destinationList!=null &&
              java.util.Arrays.equals(this.destinationList, other.getDestinationList()))) &&
            ((this.ilmd==null && other.getIlmd()==null) || 
             (this.ilmd!=null &&
              this.ilmd.equals(other.getIlmd()))) &&
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
        if (getQuantityList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getQuantityList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getQuantityList(), i);
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
        if (getIlmd() != null) {
            _hashCode += getIlmd().hashCode();
        }
        if (getExtension() != null) {
            _hashCode += getExtension().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ObjectEventExtensionType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("epcis.oliot.org", "ObjectEventExtensionType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("quantityList");
        elemField.setXmlName(new javax.xml.namespace.QName("", "quantityList"));
        elemField.setXmlType(new javax.xml.namespace.QName("epcis.oliot.org", "QuantityElementType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("", "quantityElement"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sourceList");
        elemField.setXmlName(new javax.xml.namespace.QName("", "sourceList"));
        elemField.setXmlType(new javax.xml.namespace.QName("epcis.oliot.org", "SourceDestType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("", "source"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("destinationList");
        elemField.setXmlName(new javax.xml.namespace.QName("", "destinationList"));
        elemField.setXmlType(new javax.xml.namespace.QName("epcis.oliot.org", "SourceDestType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("", "destination"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ilmd");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ilmd"));
        elemField.setXmlType(new javax.xml.namespace.QName("epcis.oliot.org", "ILMDType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extension");
        elemField.setXmlName(new javax.xml.namespace.QName("", "extension"));
        elemField.setXmlType(new javax.xml.namespace.QName("epcis.oliot.org", "ObjectEventExtension2Type"));
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
