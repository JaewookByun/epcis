/**
 * EPCISHeaderType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis;


/**
 * specific header(s) including the Standard Business Document Header.
 */
@SuppressWarnings("serial")
public class EPCISHeaderType  implements java.io.Serializable, org.apache.axis.encoding.AnyContentType {
    private org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.StandardBusinessDocumentHeader standardBusinessDocumentHeader;

    private org.oliot.epcis.EPCISHeaderExtensionType extension;

    private org.apache.axis.message.MessageElement [] _any;

    public EPCISHeaderType() {
    }

    public EPCISHeaderType(
           org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.StandardBusinessDocumentHeader standardBusinessDocumentHeader,
           org.oliot.epcis.EPCISHeaderExtensionType extension,
           org.apache.axis.message.MessageElement [] _any) {
           this.standardBusinessDocumentHeader = standardBusinessDocumentHeader;
           this.extension = extension;
           this._any = _any;
    }


    /**
     * Gets the standardBusinessDocumentHeader value for this EPCISHeaderType.
     * 
     * @return standardBusinessDocumentHeader
     */
    public org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.StandardBusinessDocumentHeader getStandardBusinessDocumentHeader() {
        return standardBusinessDocumentHeader;
    }


    /**
     * Sets the standardBusinessDocumentHeader value for this EPCISHeaderType.
     * 
     * @param standardBusinessDocumentHeader
     */
    public void setStandardBusinessDocumentHeader(org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.StandardBusinessDocumentHeader standardBusinessDocumentHeader) {
        this.standardBusinessDocumentHeader = standardBusinessDocumentHeader;
    }


    /**
     * Gets the extension value for this EPCISHeaderType.
     * 
     * @return extension
     */
    public org.oliot.epcis.EPCISHeaderExtensionType getExtension() {
        return extension;
    }


    /**
     * Sets the extension value for this EPCISHeaderType.
     * 
     * @param extension
     */
    public void setExtension(org.oliot.epcis.EPCISHeaderExtensionType extension) {
        this.extension = extension;
    }


    /**
     * Gets the _any value for this EPCISHeaderType.
     * 
     * @return _any
     */
    public org.apache.axis.message.MessageElement [] get_any() {
        return _any;
    }


    /**
     * Sets the _any value for this EPCISHeaderType.
     * 
     * @param _any
     */
    public void set_any(org.apache.axis.message.MessageElement [] _any) {
        this._any = _any;
    }

    private java.lang.Object __equalsCalc = null;
    @SuppressWarnings("unused")
	public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EPCISHeaderType)) return false;
        EPCISHeaderType other = (EPCISHeaderType) obj;
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
            ((this.extension==null && other.getExtension()==null) || 
             (this.extension!=null &&
              this.extension.equals(other.getExtension()))) &&
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
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(EPCISHeaderType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("epcis.oliot.org", "EPCISHeaderType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("standardBusinessDocumentHeader");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "StandardBusinessDocumentHeader"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "StandardBusinessDocumentHeader"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extension");
        elemField.setXmlName(new javax.xml.namespace.QName("", "extension"));
        elemField.setXmlType(new javax.xml.namespace.QName("epcis.oliot.org", "EPCISHeaderExtensionType"));
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
