/**
 * BusinessService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.axis;

public class BusinessService  implements java.io.Serializable {
    private java.lang.String businessServiceName;

    private org.oliot.epcis.axis.ServiceTransaction serviceTransaction;

    public BusinessService() {
    }

    public BusinessService(
           java.lang.String businessServiceName,
           org.oliot.epcis.axis.ServiceTransaction serviceTransaction) {
           this.businessServiceName = businessServiceName;
           this.serviceTransaction = serviceTransaction;
    }


    /**
     * Gets the businessServiceName value for this BusinessService.
     * 
     * @return businessServiceName
     */
    public java.lang.String getBusinessServiceName() {
        return businessServiceName;
    }


    /**
     * Sets the businessServiceName value for this BusinessService.
     * 
     * @param businessServiceName
     */
    public void setBusinessServiceName(java.lang.String businessServiceName) {
        this.businessServiceName = businessServiceName;
    }


    /**
     * Gets the serviceTransaction value for this BusinessService.
     * 
     * @return serviceTransaction
     */
    public org.oliot.epcis.axis.ServiceTransaction getServiceTransaction() {
        return serviceTransaction;
    }


    /**
     * Sets the serviceTransaction value for this BusinessService.
     * 
     * @param serviceTransaction
     */
    public void setServiceTransaction(org.oliot.epcis.axis.ServiceTransaction serviceTransaction) {
        this.serviceTransaction = serviceTransaction;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof BusinessService)) return false;
        BusinessService other = (BusinessService) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.businessServiceName==null && other.getBusinessServiceName()==null) || 
             (this.businessServiceName!=null &&
              this.businessServiceName.equals(other.getBusinessServiceName()))) &&
            ((this.serviceTransaction==null && other.getServiceTransaction()==null) || 
             (this.serviceTransaction!=null &&
              this.serviceTransaction.equals(other.getServiceTransaction())));
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
        if (getBusinessServiceName() != null) {
            _hashCode += getBusinessServiceName().hashCode();
        }
        if (getServiceTransaction() != null) {
            _hashCode += getServiceTransaction().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(BusinessService.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "BusinessService"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("businessServiceName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "BusinessServiceName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serviceTransaction");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "ServiceTransaction"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "ServiceTransaction"));
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
