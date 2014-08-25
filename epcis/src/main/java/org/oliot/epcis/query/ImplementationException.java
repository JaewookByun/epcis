/**
 * ImplementationException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.query;

import java.io.IOException;
import java.io.Serializable;

import javax.xml.namespace.QName;

import org.apache.axis.description.ElementDesc;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.ser.BeanDeserializer;
import org.apache.axis.encoding.ser.BeanSerializer;

@SuppressWarnings("serial")
public class ImplementationException  extends EPCISException  implements Serializable {
    private ImplementationExceptionSeverity severity;

    private String queryName;

    private String subscriptionID;

    public ImplementationException() {}

    public ImplementationException( String reason, ImplementationExceptionSeverity severity, String queryName, String subscriptionID) {
        super(reason);
        this.severity = severity;
        this.queryName = queryName;
        this.subscriptionID = subscriptionID;
    }

    /**
     * Gets the severity value for this ImplementationException.
     * 
     * @return severity
     */
    public ImplementationExceptionSeverity getSeverity() {
        return severity;
    }

    /**
     * Sets the severity value for this ImplementationException.
     * 
     * @param severity
     */
    public void setSeverity( ImplementationExceptionSeverity severity) {
        this.severity = severity;
    }


    /**
     * Gets the queryName value for this ImplementationException.
     * 
     * @return queryName
     */
    public String getQueryName() {
        return queryName;
    }


    /**
     * Sets the queryName value for this ImplementationException.
     * 
     * @param queryName
     */
    public void setQueryName( String queryName) {
        this.queryName = queryName;
    }


    /**
     * Gets the subscriptionID value for this ImplementationException.
     * 
     * @return subscriptionID
     */
    public String getSubscriptionID() {
        return subscriptionID;
    }


    /**
     * Sets the subscriptionID value for this ImplementationException.
     * 
     * @param subscriptionID
     */
    public void setSubscriptionID( String subscriptionID) {
        this.subscriptionID = subscriptionID;
    }

    private Object __equalsCalc = null;
    @SuppressWarnings("unused")
	public synchronized boolean equals(Object obj) {
        if (!(obj instanceof ImplementationException)) return false;
        ImplementationException other = (ImplementationException) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.severity==null && other.getSeverity()==null) || 
             (this.severity!=null &&
              this.severity.equals(other.getSeverity()))) &&
            ((this.queryName==null && other.getQueryName()==null) || 
             (this.queryName!=null &&
              this.queryName.equals(other.getQueryName()))) &&
            ((this.subscriptionID==null && other.getSubscriptionID()==null) || 
             (this.subscriptionID!=null &&
              this.subscriptionID.equals(other.getSubscriptionID())));
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
        if (getSeverity() != null) {
            _hashCode += getSeverity().hashCode();
        }
        if (getQueryName() != null) {
            _hashCode += getQueryName().hashCode();
        }
        if (getSubscriptionID() != null) {
            _hashCode += getSubscriptionID().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static TypeDesc typeDesc = new TypeDesc(ImplementationException.class, true);

    static {
        typeDesc.setXmlType(new QName("query.epcis.oliot.org", "ImplementationException"));
        ElementDesc elemField = new ElementDesc();
        elemField.setFieldName("severity");
        elemField.setXmlName(new QName("", "severity"));
        elemField.setXmlType(new QName("query.epcis.oliot.org", "ImplementationExceptionSeverity"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new ElementDesc();
        elemField.setFieldName("queryName");
        elemField.setXmlName(new QName("", "queryName"));
        elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new ElementDesc();
        elemField.setFieldName("subscriptionID");
        elemField.setXmlName(new QName("", "subscriptionID"));
        elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    @SuppressWarnings("rawtypes")
	public static Serializer getSerializer( String mechType, Class _javaType, QName _xmlType) {
        return new  BeanSerializer( _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    @SuppressWarnings("rawtypes")
	public static Deserializer getDeserializer( String mechType, Class _javaType,  QName _xmlType) {
        return new  BeanDeserializer( _javaType, _xmlType, typeDesc);
    }


    /**
     * Writes the exception data to the faultDetails
     */
    public void writeDetails( QName qname, SerializationContext context) throws IOException {
        context.serialize(qname, null, this);
    }
}
