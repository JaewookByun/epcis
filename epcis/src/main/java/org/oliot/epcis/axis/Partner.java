/**
 * Partner.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.axis;

public class Partner  implements java.io.Serializable {
    private org.oliot.epcis.axis.PartnerIdentification identifier;

    private org.oliot.epcis.axis.ContactInformation[] contactInformation;

    public Partner() {
    }

    public Partner(
           org.oliot.epcis.axis.PartnerIdentification identifier,
           org.oliot.epcis.axis.ContactInformation[] contactInformation) {
           this.identifier = identifier;
           this.contactInformation = contactInformation;
    }


    /**
     * Gets the identifier value for this Partner.
     * 
     * @return identifier
     */
    public org.oliot.epcis.axis.PartnerIdentification getIdentifier() {
        return identifier;
    }


    /**
     * Sets the identifier value for this Partner.
     * 
     * @param identifier
     */
    public void setIdentifier(org.oliot.epcis.axis.PartnerIdentification identifier) {
        this.identifier = identifier;
    }


    /**
     * Gets the contactInformation value for this Partner.
     * 
     * @return contactInformation
     */
    public org.oliot.epcis.axis.ContactInformation[] getContactInformation() {
        return contactInformation;
    }


    /**
     * Sets the contactInformation value for this Partner.
     * 
     * @param contactInformation
     */
    public void setContactInformation(org.oliot.epcis.axis.ContactInformation[] contactInformation) {
        this.contactInformation = contactInformation;
    }

    public org.oliot.epcis.axis.ContactInformation getContactInformation(int i) {
        return this.contactInformation[i];
    }

    public void setContactInformation(int i, org.oliot.epcis.axis.ContactInformation _value) {
        this.contactInformation[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Partner)) return false;
        Partner other = (Partner) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.identifier==null && other.getIdentifier()==null) || 
             (this.identifier!=null &&
              this.identifier.equals(other.getIdentifier()))) &&
            ((this.contactInformation==null && other.getContactInformation()==null) || 
             (this.contactInformation!=null &&
              java.util.Arrays.equals(this.contactInformation, other.getContactInformation())));
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
        if (getIdentifier() != null) {
            _hashCode += getIdentifier().hashCode();
        }
        if (getContactInformation() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getContactInformation());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getContactInformation(), i);
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
        new org.apache.axis.description.TypeDesc(Partner.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "Partner"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("identifier");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "Identifier"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "PartnerIdentification"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("contactInformation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "ContactInformation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "ContactInformation"));
        elemField.setMinOccurs(0);
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
