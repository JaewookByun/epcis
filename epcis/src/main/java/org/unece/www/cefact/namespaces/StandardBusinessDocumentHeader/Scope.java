/**
 * Scope.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader;

@SuppressWarnings("serial")
public class Scope  implements java.io.Serializable {
    private java.lang.String type;

    private java.lang.String instanceIdentifier;

    private java.lang.String identifier;

    private java.lang.Object[] scopeInformation;

    public Scope() {
    }

    public Scope(
           java.lang.String type,
           java.lang.String instanceIdentifier,
           java.lang.String identifier,
           java.lang.Object[] scopeInformation) {
           this.type = type;
           this.instanceIdentifier = instanceIdentifier;
           this.identifier = identifier;
           this.scopeInformation = scopeInformation;
    }


    /**
     * Gets the type value for this Scope.
     * 
     * @return type
     */
    public java.lang.String getType() {
        return type;
    }


    /**
     * Sets the type value for this Scope.
     * 
     * @param type
     */
    public void setType(java.lang.String type) {
        this.type = type;
    }


    /**
     * Gets the instanceIdentifier value for this Scope.
     * 
     * @return instanceIdentifier
     */
    public java.lang.String getInstanceIdentifier() {
        return instanceIdentifier;
    }


    /**
     * Sets the instanceIdentifier value for this Scope.
     * 
     * @param instanceIdentifier
     */
    public void setInstanceIdentifier(java.lang.String instanceIdentifier) {
        this.instanceIdentifier = instanceIdentifier;
    }


    /**
     * Gets the identifier value for this Scope.
     * 
     * @return identifier
     */
    public java.lang.String getIdentifier() {
        return identifier;
    }


    /**
     * Sets the identifier value for this Scope.
     * 
     * @param identifier
     */
    public void setIdentifier(java.lang.String identifier) {
        this.identifier = identifier;
    }


    /**
     * Gets the scopeInformation value for this Scope.
     * 
     * @return scopeInformation
     */
    public java.lang.Object[] getScopeInformation() {
        return scopeInformation;
    }


    /**
     * Sets the scopeInformation value for this Scope.
     * 
     * @param scopeInformation
     */
    public void setScopeInformation(java.lang.Object[] scopeInformation) {
        this.scopeInformation = scopeInformation;
    }

    public java.lang.Object getScopeInformation(int i) {
        return this.scopeInformation[i];
    }

    public void setScopeInformation(int i, java.lang.Object _value) {
        this.scopeInformation[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    @SuppressWarnings("unused")
	public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Scope)) return false;
        Scope other = (Scope) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.type==null && other.getType()==null) || 
             (this.type!=null &&
              this.type.equals(other.getType()))) &&
            ((this.instanceIdentifier==null && other.getInstanceIdentifier()==null) || 
             (this.instanceIdentifier!=null &&
              this.instanceIdentifier.equals(other.getInstanceIdentifier()))) &&
            ((this.identifier==null && other.getIdentifier()==null) || 
             (this.identifier!=null &&
              this.identifier.equals(other.getIdentifier()))) &&
            ((this.scopeInformation==null && other.getScopeInformation()==null) || 
             (this.scopeInformation!=null &&
              java.util.Arrays.equals(this.scopeInformation, other.getScopeInformation())));
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
        if (getType() != null) {
            _hashCode += getType().hashCode();
        }
        if (getInstanceIdentifier() != null) {
            _hashCode += getInstanceIdentifier().hashCode();
        }
        if (getIdentifier() != null) {
            _hashCode += getIdentifier().hashCode();
        }
        if (getScopeInformation() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getScopeInformation());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getScopeInformation(), i);
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
        new org.apache.axis.description.TypeDesc(Scope.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "Scope"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("type");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "Type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("instanceIdentifier");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "InstanceIdentifier"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("identifier");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "Identifier"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("scopeInformation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "ScopeInformation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "ScopeInformation"));
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
