/**
 * ImplementationExceptionSeverity.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.axis;

public class ImplementationExceptionSeverity implements java.io.Serializable {
    private org.apache.axis.types.NCName _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected ImplementationExceptionSeverity(org.apache.axis.types.NCName value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final org.apache.axis.types.NCName _ERROR = new org.apache.axis.types.NCName("ERROR");
    public static final org.apache.axis.types.NCName _SEVERE = new org.apache.axis.types.NCName("SEVERE");
    public static final ImplementationExceptionSeverity ERROR = new ImplementationExceptionSeverity(_ERROR);
    public static final ImplementationExceptionSeverity SEVERE = new ImplementationExceptionSeverity(_SEVERE);
    public org.apache.axis.types.NCName getValue() { return _value_;}
    public static ImplementationExceptionSeverity fromValue(org.apache.axis.types.NCName value)
          throws java.lang.IllegalArgumentException {
        ImplementationExceptionSeverity enumeration = (ImplementationExceptionSeverity)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static ImplementationExceptionSeverity fromString(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        try {
            return fromValue(new org.apache.axis.types.NCName(value));
        } catch (Exception e) {
            throw new java.lang.IllegalArgumentException();
        }
    }
    public boolean equals(java.lang.Object obj) {return (obj == this);}
    public int hashCode() { return toString().hashCode();}
    public java.lang.String toString() { return _value_.toString();}
    public java.lang.Object readResolve() throws java.io.ObjectStreamException { return fromValue(_value_);}
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumSerializer(
            _javaType, _xmlType);
    }
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumDeserializer(
            _javaType, _xmlType);
    }
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ImplementationExceptionSeverity.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "ImplementationExceptionSeverity"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
