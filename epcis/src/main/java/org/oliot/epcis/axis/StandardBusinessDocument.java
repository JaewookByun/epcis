/**
 * StandardBusinessDocument.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.axis;

public class StandardBusinessDocument  implements java.io.Serializable, org.apache.axis.encoding.AnyContentType {
    private org.oliot.epcis.axis.StandardBusinessDocumentHeader standardBusinessDocumentHeader;

    private org.apache.axis.message.MessageElement [] _any;

    public StandardBusinessDocument() {
    }

    public StandardBusinessDocument(
           org.oliot.epcis.axis.StandardBusinessDocumentHeader standardBusinessDocumentHeader,
           org.apache.axis.message.MessageElement [] _any) {
           this.standardBusinessDocumentHeader = standardBusinessDocumentHeader;
           this._any = _any;
    }


    /**
     * Gets the standardBusinessDocumentHeader value for this StandardBusinessDocument.
     * 
     * @return standardBusinessDocumentHeader
     */
    public org.oliot.epcis.axis.StandardBusinessDocumentHeader getStandardBusinessDocumentHeader() {
        return standardBusinessDocumentHeader;
    }


    /**
     * Sets the standardBusinessDocumentHeader value for this StandardBusinessDocument.
     * 
     * @param standardBusinessDocumentHeader
     */
    public void setStandardBusinessDocumentHeader(org.oliot.epcis.axis.StandardBusinessDocumentHeader standardBusinessDocumentHeader) {
        this.standardBusinessDocumentHeader = standardBusinessDocumentHeader;
    }


    /**
     * Gets the _any value for this StandardBusinessDocument.
     * 
     * @return _any
     */
    public org.apache.axis.message.MessageElement [] get_any() {
        return _any;
    }


    /**
     * Sets the _any value for this StandardBusinessDocument.
     * 
     * @param _any
     */
    public void set_any(org.apache.axis.message.MessageElement [] _any) {
        this._any = _any;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof StandardBusinessDocument)) return false;
        StandardBusinessDocument other = (StandardBusinessDocument) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.standardBusinessDocumentHeader==null && other.getStandardBusinessDocumentHeader()==null) || 
             (this.standardBusinessDocumentHeader!=null &&
              this.standardBusinessDocumentHeader.equals(other.getStandardBusinessDocumentHeader()))) &&
            ((this._any==null && other.get_any()==null) || 
             (this._any!=null &&
              java.util.Arrays.equals(this._any, other.get_any())));
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
        if (getStandardBusinessDocumentHeader() != null) {
            _hashCode += getStandardBusinessDocumentHeader().hashCode();
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
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(StandardBusinessDocument.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "StandardBusinessDocument"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("standardBusinessDocumentHeader");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "StandardBusinessDocumentHeader"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "StandardBusinessDocumentHeader"));
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
