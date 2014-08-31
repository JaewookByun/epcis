/**
 * Subscribe.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.axis;

public class Subscribe  implements java.io.Serializable {
    private java.lang.String queryName;

    private org.oliot.epcis.axis.QueryParam[] params;

    private org.apache.axis.types.URI dest;

    private org.oliot.epcis.axis.SubscriptionControls controls;

    private java.lang.String subscriptionID;

    public Subscribe() {
    }

    public Subscribe(
           java.lang.String queryName,
           org.oliot.epcis.axis.QueryParam[] params,
           org.apache.axis.types.URI dest,
           org.oliot.epcis.axis.SubscriptionControls controls,
           java.lang.String subscriptionID) {
           this.queryName = queryName;
           this.params = params;
           this.dest = dest;
           this.controls = controls;
           this.subscriptionID = subscriptionID;
    }


    /**
     * Gets the queryName value for this Subscribe.
     * 
     * @return queryName
     */
    public java.lang.String getQueryName() {
        return queryName;
    }


    /**
     * Sets the queryName value for this Subscribe.
     * 
     * @param queryName
     */
    public void setQueryName(java.lang.String queryName) {
        this.queryName = queryName;
    }


    /**
     * Gets the params value for this Subscribe.
     * 
     * @return params
     */
    public org.oliot.epcis.axis.QueryParam[] getParams() {
        return params;
    }


    /**
     * Sets the params value for this Subscribe.
     * 
     * @param params
     */
    public void setParams(org.oliot.epcis.axis.QueryParam[] params) {
        this.params = params;
    }


    /**
     * Gets the dest value for this Subscribe.
     * 
     * @return dest
     */
    public org.apache.axis.types.URI getDest() {
        return dest;
    }


    /**
     * Sets the dest value for this Subscribe.
     * 
     * @param dest
     */
    public void setDest(org.apache.axis.types.URI dest) {
        this.dest = dest;
    }


    /**
     * Gets the controls value for this Subscribe.
     * 
     * @return controls
     */
    public org.oliot.epcis.axis.SubscriptionControls getControls() {
        return controls;
    }


    /**
     * Sets the controls value for this Subscribe.
     * 
     * @param controls
     */
    public void setControls(org.oliot.epcis.axis.SubscriptionControls controls) {
        this.controls = controls;
    }


    /**
     * Gets the subscriptionID value for this Subscribe.
     * 
     * @return subscriptionID
     */
    public java.lang.String getSubscriptionID() {
        return subscriptionID;
    }


    /**
     * Sets the subscriptionID value for this Subscribe.
     * 
     * @param subscriptionID
     */
    public void setSubscriptionID(java.lang.String subscriptionID) {
        this.subscriptionID = subscriptionID;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Subscribe)) return false;
        Subscribe other = (Subscribe) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.queryName==null && other.getQueryName()==null) || 
             (this.queryName!=null &&
              this.queryName.equals(other.getQueryName()))) &&
            ((this.params==null && other.getParams()==null) || 
             (this.params!=null &&
              java.util.Arrays.equals(this.params, other.getParams()))) &&
            ((this.dest==null && other.getDest()==null) || 
             (this.dest!=null &&
              this.dest.equals(other.getDest()))) &&
            ((this.controls==null && other.getControls()==null) || 
             (this.controls!=null &&
              this.controls.equals(other.getControls()))) &&
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
        int _hashCode = 1;
        if (getQueryName() != null) {
            _hashCode += getQueryName().hashCode();
        }
        if (getParams() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getParams());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getParams(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getDest() != null) {
            _hashCode += getDest().hashCode();
        }
        if (getControls() != null) {
            _hashCode += getControls().hashCode();
        }
        if (getSubscriptionID() != null) {
            _hashCode += getSubscriptionID().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Subscribe.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "Subscribe"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("queryName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "queryName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("params");
        elemField.setXmlName(new javax.xml.namespace.QName("", "params"));
        elemField.setXmlType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "QueryParam"));
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("", "param"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dest");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dest"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyURI"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("controls");
        elemField.setXmlName(new javax.xml.namespace.QName("", "controls"));
        elemField.setXmlType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "SubscriptionControls"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("subscriptionID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "subscriptionID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
