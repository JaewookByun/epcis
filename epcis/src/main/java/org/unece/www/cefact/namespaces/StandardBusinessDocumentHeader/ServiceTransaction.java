/**
 * ServiceTransaction.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader;

public class ServiceTransaction  implements java.io.Serializable {
    private org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.TypeOfServiceTransaction typeOfServiceTransaction;  // attribute

    private java.lang.String isNonRepudiationRequired;  // attribute

    private java.lang.String isAuthenticationRequired;  // attribute

    private java.lang.String isNonRepudiationOfReceiptRequired;  // attribute

    private java.lang.String isIntegrityCheckRequired;  // attribute

    private java.lang.String isApplicationErrorResponseRequested;  // attribute

    private java.lang.String timeToAcknowledgeReceipt;  // attribute

    private java.lang.String timeToAcknowledgeAcceptance;  // attribute

    private java.lang.String timeToPerform;  // attribute

    private java.lang.String recurrence;  // attribute

    public ServiceTransaction() {
    }

    public ServiceTransaction(
           org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.TypeOfServiceTransaction typeOfServiceTransaction,
           java.lang.String isNonRepudiationRequired,
           java.lang.String isAuthenticationRequired,
           java.lang.String isNonRepudiationOfReceiptRequired,
           java.lang.String isIntegrityCheckRequired,
           java.lang.String isApplicationErrorResponseRequested,
           java.lang.String timeToAcknowledgeReceipt,
           java.lang.String timeToAcknowledgeAcceptance,
           java.lang.String timeToPerform,
           java.lang.String recurrence) {
           this.typeOfServiceTransaction = typeOfServiceTransaction;
           this.isNonRepudiationRequired = isNonRepudiationRequired;
           this.isAuthenticationRequired = isAuthenticationRequired;
           this.isNonRepudiationOfReceiptRequired = isNonRepudiationOfReceiptRequired;
           this.isIntegrityCheckRequired = isIntegrityCheckRequired;
           this.isApplicationErrorResponseRequested = isApplicationErrorResponseRequested;
           this.timeToAcknowledgeReceipt = timeToAcknowledgeReceipt;
           this.timeToAcknowledgeAcceptance = timeToAcknowledgeAcceptance;
           this.timeToPerform = timeToPerform;
           this.recurrence = recurrence;
    }


    /**
     * Gets the typeOfServiceTransaction value for this ServiceTransaction.
     * 
     * @return typeOfServiceTransaction
     */
    public org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.TypeOfServiceTransaction getTypeOfServiceTransaction() {
        return typeOfServiceTransaction;
    }


    /**
     * Sets the typeOfServiceTransaction value for this ServiceTransaction.
     * 
     * @param typeOfServiceTransaction
     */
    public void setTypeOfServiceTransaction(org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.TypeOfServiceTransaction typeOfServiceTransaction) {
        this.typeOfServiceTransaction = typeOfServiceTransaction;
    }


    /**
     * Gets the isNonRepudiationRequired value for this ServiceTransaction.
     * 
     * @return isNonRepudiationRequired
     */
    public java.lang.String getIsNonRepudiationRequired() {
        return isNonRepudiationRequired;
    }


    /**
     * Sets the isNonRepudiationRequired value for this ServiceTransaction.
     * 
     * @param isNonRepudiationRequired
     */
    public void setIsNonRepudiationRequired(java.lang.String isNonRepudiationRequired) {
        this.isNonRepudiationRequired = isNonRepudiationRequired;
    }


    /**
     * Gets the isAuthenticationRequired value for this ServiceTransaction.
     * 
     * @return isAuthenticationRequired
     */
    public java.lang.String getIsAuthenticationRequired() {
        return isAuthenticationRequired;
    }


    /**
     * Sets the isAuthenticationRequired value for this ServiceTransaction.
     * 
     * @param isAuthenticationRequired
     */
    public void setIsAuthenticationRequired(java.lang.String isAuthenticationRequired) {
        this.isAuthenticationRequired = isAuthenticationRequired;
    }


    /**
     * Gets the isNonRepudiationOfReceiptRequired value for this ServiceTransaction.
     * 
     * @return isNonRepudiationOfReceiptRequired
     */
    public java.lang.String getIsNonRepudiationOfReceiptRequired() {
        return isNonRepudiationOfReceiptRequired;
    }


    /**
     * Sets the isNonRepudiationOfReceiptRequired value for this ServiceTransaction.
     * 
     * @param isNonRepudiationOfReceiptRequired
     */
    public void setIsNonRepudiationOfReceiptRequired(java.lang.String isNonRepudiationOfReceiptRequired) {
        this.isNonRepudiationOfReceiptRequired = isNonRepudiationOfReceiptRequired;
    }


    /**
     * Gets the isIntegrityCheckRequired value for this ServiceTransaction.
     * 
     * @return isIntegrityCheckRequired
     */
    public java.lang.String getIsIntegrityCheckRequired() {
        return isIntegrityCheckRequired;
    }


    /**
     * Sets the isIntegrityCheckRequired value for this ServiceTransaction.
     * 
     * @param isIntegrityCheckRequired
     */
    public void setIsIntegrityCheckRequired(java.lang.String isIntegrityCheckRequired) {
        this.isIntegrityCheckRequired = isIntegrityCheckRequired;
    }


    /**
     * Gets the isApplicationErrorResponseRequested value for this ServiceTransaction.
     * 
     * @return isApplicationErrorResponseRequested
     */
    public java.lang.String getIsApplicationErrorResponseRequested() {
        return isApplicationErrorResponseRequested;
    }


    /**
     * Sets the isApplicationErrorResponseRequested value for this ServiceTransaction.
     * 
     * @param isApplicationErrorResponseRequested
     */
    public void setIsApplicationErrorResponseRequested(java.lang.String isApplicationErrorResponseRequested) {
        this.isApplicationErrorResponseRequested = isApplicationErrorResponseRequested;
    }


    /**
     * Gets the timeToAcknowledgeReceipt value for this ServiceTransaction.
     * 
     * @return timeToAcknowledgeReceipt
     */
    public java.lang.String getTimeToAcknowledgeReceipt() {
        return timeToAcknowledgeReceipt;
    }


    /**
     * Sets the timeToAcknowledgeReceipt value for this ServiceTransaction.
     * 
     * @param timeToAcknowledgeReceipt
     */
    public void setTimeToAcknowledgeReceipt(java.lang.String timeToAcknowledgeReceipt) {
        this.timeToAcknowledgeReceipt = timeToAcknowledgeReceipt;
    }


    /**
     * Gets the timeToAcknowledgeAcceptance value for this ServiceTransaction.
     * 
     * @return timeToAcknowledgeAcceptance
     */
    public java.lang.String getTimeToAcknowledgeAcceptance() {
        return timeToAcknowledgeAcceptance;
    }


    /**
     * Sets the timeToAcknowledgeAcceptance value for this ServiceTransaction.
     * 
     * @param timeToAcknowledgeAcceptance
     */
    public void setTimeToAcknowledgeAcceptance(java.lang.String timeToAcknowledgeAcceptance) {
        this.timeToAcknowledgeAcceptance = timeToAcknowledgeAcceptance;
    }


    /**
     * Gets the timeToPerform value for this ServiceTransaction.
     * 
     * @return timeToPerform
     */
    public java.lang.String getTimeToPerform() {
        return timeToPerform;
    }


    /**
     * Sets the timeToPerform value for this ServiceTransaction.
     * 
     * @param timeToPerform
     */
    public void setTimeToPerform(java.lang.String timeToPerform) {
        this.timeToPerform = timeToPerform;
    }


    /**
     * Gets the recurrence value for this ServiceTransaction.
     * 
     * @return recurrence
     */
    public java.lang.String getRecurrence() {
        return recurrence;
    }


    /**
     * Sets the recurrence value for this ServiceTransaction.
     * 
     * @param recurrence
     */
    public void setRecurrence(java.lang.String recurrence) {
        this.recurrence = recurrence;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ServiceTransaction)) return false;
        ServiceTransaction other = (ServiceTransaction) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.typeOfServiceTransaction==null && other.getTypeOfServiceTransaction()==null) || 
             (this.typeOfServiceTransaction!=null &&
              this.typeOfServiceTransaction.equals(other.getTypeOfServiceTransaction()))) &&
            ((this.isNonRepudiationRequired==null && other.getIsNonRepudiationRequired()==null) || 
             (this.isNonRepudiationRequired!=null &&
              this.isNonRepudiationRequired.equals(other.getIsNonRepudiationRequired()))) &&
            ((this.isAuthenticationRequired==null && other.getIsAuthenticationRequired()==null) || 
             (this.isAuthenticationRequired!=null &&
              this.isAuthenticationRequired.equals(other.getIsAuthenticationRequired()))) &&
            ((this.isNonRepudiationOfReceiptRequired==null && other.getIsNonRepudiationOfReceiptRequired()==null) || 
             (this.isNonRepudiationOfReceiptRequired!=null &&
              this.isNonRepudiationOfReceiptRequired.equals(other.getIsNonRepudiationOfReceiptRequired()))) &&
            ((this.isIntegrityCheckRequired==null && other.getIsIntegrityCheckRequired()==null) || 
             (this.isIntegrityCheckRequired!=null &&
              this.isIntegrityCheckRequired.equals(other.getIsIntegrityCheckRequired()))) &&
            ((this.isApplicationErrorResponseRequested==null && other.getIsApplicationErrorResponseRequested()==null) || 
             (this.isApplicationErrorResponseRequested!=null &&
              this.isApplicationErrorResponseRequested.equals(other.getIsApplicationErrorResponseRequested()))) &&
            ((this.timeToAcknowledgeReceipt==null && other.getTimeToAcknowledgeReceipt()==null) || 
             (this.timeToAcknowledgeReceipt!=null &&
              this.timeToAcknowledgeReceipt.equals(other.getTimeToAcknowledgeReceipt()))) &&
            ((this.timeToAcknowledgeAcceptance==null && other.getTimeToAcknowledgeAcceptance()==null) || 
             (this.timeToAcknowledgeAcceptance!=null &&
              this.timeToAcknowledgeAcceptance.equals(other.getTimeToAcknowledgeAcceptance()))) &&
            ((this.timeToPerform==null && other.getTimeToPerform()==null) || 
             (this.timeToPerform!=null &&
              this.timeToPerform.equals(other.getTimeToPerform()))) &&
            ((this.recurrence==null && other.getRecurrence()==null) || 
             (this.recurrence!=null &&
              this.recurrence.equals(other.getRecurrence())));
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
        if (getTypeOfServiceTransaction() != null) {
            _hashCode += getTypeOfServiceTransaction().hashCode();
        }
        if (getIsNonRepudiationRequired() != null) {
            _hashCode += getIsNonRepudiationRequired().hashCode();
        }
        if (getIsAuthenticationRequired() != null) {
            _hashCode += getIsAuthenticationRequired().hashCode();
        }
        if (getIsNonRepudiationOfReceiptRequired() != null) {
            _hashCode += getIsNonRepudiationOfReceiptRequired().hashCode();
        }
        if (getIsIntegrityCheckRequired() != null) {
            _hashCode += getIsIntegrityCheckRequired().hashCode();
        }
        if (getIsApplicationErrorResponseRequested() != null) {
            _hashCode += getIsApplicationErrorResponseRequested().hashCode();
        }
        if (getTimeToAcknowledgeReceipt() != null) {
            _hashCode += getTimeToAcknowledgeReceipt().hashCode();
        }
        if (getTimeToAcknowledgeAcceptance() != null) {
            _hashCode += getTimeToAcknowledgeAcceptance().hashCode();
        }
        if (getTimeToPerform() != null) {
            _hashCode += getTimeToPerform().hashCode();
        }
        if (getRecurrence() != null) {
            _hashCode += getRecurrence().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ServiceTransaction.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "ServiceTransaction"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("typeOfServiceTransaction");
        attrField.setXmlName(new javax.xml.namespace.QName("", "TypeOfServiceTransaction"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "TypeOfServiceTransaction"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("isNonRepudiationRequired");
        attrField.setXmlName(new javax.xml.namespace.QName("", "IsNonRepudiationRequired"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("isAuthenticationRequired");
        attrField.setXmlName(new javax.xml.namespace.QName("", "IsAuthenticationRequired"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("isNonRepudiationOfReceiptRequired");
        attrField.setXmlName(new javax.xml.namespace.QName("", "IsNonRepudiationOfReceiptRequired"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("isIntegrityCheckRequired");
        attrField.setXmlName(new javax.xml.namespace.QName("", "IsIntegrityCheckRequired"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("isApplicationErrorResponseRequested");
        attrField.setXmlName(new javax.xml.namespace.QName("", "IsApplicationErrorResponseRequested"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("timeToAcknowledgeReceipt");
        attrField.setXmlName(new javax.xml.namespace.QName("", "TimeToAcknowledgeReceipt"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("timeToAcknowledgeAcceptance");
        attrField.setXmlName(new javax.xml.namespace.QName("", "TimeToAcknowledgeAcceptance"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("timeToPerform");
        attrField.setXmlName(new javax.xml.namespace.QName("", "TimeToPerform"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("recurrence");
        attrField.setXmlName(new javax.xml.namespace.QName("", "Recurrence"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
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
