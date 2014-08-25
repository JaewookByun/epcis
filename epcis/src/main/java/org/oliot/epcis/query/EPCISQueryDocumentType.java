/**
 * EPCISQueryDocumentType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.query;

@SuppressWarnings("serial")
public class EPCISQueryDocumentType  extends org.oliot.epcglobal.Document  implements java.io.Serializable, org.apache.axis.encoding.AnyContentType {
    private org.oliot.epcis.EPCISHeaderType EPCISHeader;

    private org.oliot.epcis.query.EPCISQueryBodyType EPCISBody;

    private org.oliot.epcis.query.EPCISQueryDocumentExtensionType extension;

    private org.apache.axis.message.MessageElement [] _any;

    public EPCISQueryDocumentType() {
    }

    public EPCISQueryDocumentType(
           java.math.BigDecimal schemaVersion,
           java.util.Calendar creationDate,
           org.oliot.epcis.EPCISHeaderType EPCISHeader,
           org.oliot.epcis.query.EPCISQueryBodyType EPCISBody,
           org.oliot.epcis.query.EPCISQueryDocumentExtensionType extension,
           org.apache.axis.message.MessageElement [] _any) {
        super(
            schemaVersion,
            creationDate);
        this.EPCISHeader = EPCISHeader;
        this.EPCISBody = EPCISBody;
        this.extension = extension;
        this._any = _any;
    }


    /**
     * Gets the EPCISHeader value for this EPCISQueryDocumentType.
     * 
     * @return EPCISHeader
     */
    public org.oliot.epcis.EPCISHeaderType getEPCISHeader() {
        return EPCISHeader;
    }


    /**
     * Sets the EPCISHeader value for this EPCISQueryDocumentType.
     * 
     * @param EPCISHeader
     */
    public void setEPCISHeader(org.oliot.epcis.EPCISHeaderType EPCISHeader) {
        this.EPCISHeader = EPCISHeader;
    }


    /**
     * Gets the EPCISBody value for this EPCISQueryDocumentType.
     * 
     * @return EPCISBody
     */
    public org.oliot.epcis.query.EPCISQueryBodyType getEPCISBody() {
        return EPCISBody;
    }


    /**
     * Sets the EPCISBody value for this EPCISQueryDocumentType.
     * 
     * @param EPCISBody
     */
    public void setEPCISBody(org.oliot.epcis.query.EPCISQueryBodyType EPCISBody) {
        this.EPCISBody = EPCISBody;
    }


    /**
     * Gets the extension value for this EPCISQueryDocumentType.
     * 
     * @return extension
     */
    public org.oliot.epcis.query.EPCISQueryDocumentExtensionType getExtension() {
        return extension;
    }


    /**
     * Sets the extension value for this EPCISQueryDocumentType.
     * 
     * @param extension
     */
    public void setExtension(org.oliot.epcis.query.EPCISQueryDocumentExtensionType extension) {
        this.extension = extension;
    }


    /**
     * Gets the _any value for this EPCISQueryDocumentType.
     * 
     * @return _any
     */
    public org.apache.axis.message.MessageElement [] get_any() {
        return _any;
    }


    /**
     * Sets the _any value for this EPCISQueryDocumentType.
     * 
     * @param _any
     */
    public void set_any(org.apache.axis.message.MessageElement [] _any) {
        this._any = _any;
    }

    private java.lang.Object __equalsCalc = null;
    @SuppressWarnings("unused")
	public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EPCISQueryDocumentType)) return false;
        EPCISQueryDocumentType other = (EPCISQueryDocumentType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.EPCISHeader==null && other.getEPCISHeader()==null) || 
             (this.EPCISHeader!=null &&
              this.EPCISHeader.equals(other.getEPCISHeader()))) &&
            ((this.EPCISBody==null && other.getEPCISBody()==null) || 
             (this.EPCISBody!=null &&
              this.EPCISBody.equals(other.getEPCISBody()))) &&
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
        int _hashCode = super.hashCode();
        if (getEPCISHeader() != null) {
            _hashCode += getEPCISHeader().hashCode();
        }
        if (getEPCISBody() != null) {
            _hashCode += getEPCISBody().hashCode();
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
        new org.apache.axis.description.TypeDesc(EPCISQueryDocumentType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("query.epcis.oliot.org", "EPCISQueryDocumentType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("EPCISHeader");
        elemField.setXmlName(new javax.xml.namespace.QName("", "EPCISHeader"));
        elemField.setXmlType(new javax.xml.namespace.QName("epcis.oliot.org", "EPCISHeaderType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("EPCISBody");
        elemField.setXmlName(new javax.xml.namespace.QName("", "EPCISBody"));
        elemField.setXmlType(new javax.xml.namespace.QName("query.epcis.oliot.org", "EPCISQueryBodyType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extension");
        elemField.setXmlName(new javax.xml.namespace.QName("", "extension"));
        elemField.setXmlType(new javax.xml.namespace.QName("query.epcis.oliot.org", "EPCISQueryDocumentExtensionType"));
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
