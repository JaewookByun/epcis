/**
 * QueryResults.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.query;

public class QueryResults  implements java.io.Serializable, org.apache.axis.encoding.AnyContentType {
    private java.lang.String queryName;

    private java.lang.String subscriptionID;

    private org.oliot.epcis.query.QueryResultsBody resultsBody;

    private org.oliot.epcis.query.QueryResultsExtensionType extension;

    private org.apache.axis.message.MessageElement [] _any;

    public QueryResults() {
    }

    public QueryResults(
           java.lang.String queryName,
           java.lang.String subscriptionID,
           org.oliot.epcis.query.QueryResultsBody resultsBody,
           org.oliot.epcis.query.QueryResultsExtensionType extension,
           org.apache.axis.message.MessageElement [] _any) {
           this.queryName = queryName;
           this.subscriptionID = subscriptionID;
           this.resultsBody = resultsBody;
           this.extension = extension;
           this._any = _any;
    }


    /**
     * Gets the queryName value for this QueryResults.
     * 
     * @return queryName
     */
    public java.lang.String getQueryName() {
        return queryName;
    }


    /**
     * Sets the queryName value for this QueryResults.
     * 
     * @param queryName
     */
    public void setQueryName(java.lang.String queryName) {
        this.queryName = queryName;
    }


    /**
     * Gets the subscriptionID value for this QueryResults.
     * 
     * @return subscriptionID
     */
    public java.lang.String getSubscriptionID() {
        return subscriptionID;
    }


    /**
     * Sets the subscriptionID value for this QueryResults.
     * 
     * @param subscriptionID
     */
    public void setSubscriptionID(java.lang.String subscriptionID) {
        this.subscriptionID = subscriptionID;
    }


    /**
     * Gets the resultsBody value for this QueryResults.
     * 
     * @return resultsBody
     */
    public org.oliot.epcis.query.QueryResultsBody getResultsBody() {
        return resultsBody;
    }


    /**
     * Sets the resultsBody value for this QueryResults.
     * 
     * @param resultsBody
     */
    public void setResultsBody(org.oliot.epcis.query.QueryResultsBody resultsBody) {
        this.resultsBody = resultsBody;
    }


    /**
     * Gets the extension value for this QueryResults.
     * 
     * @return extension
     */
    public org.oliot.epcis.query.QueryResultsExtensionType getExtension() {
        return extension;
    }


    /**
     * Sets the extension value for this QueryResults.
     * 
     * @param extension
     */
    public void setExtension(org.oliot.epcis.query.QueryResultsExtensionType extension) {
        this.extension = extension;
    }


    /**
     * Gets the _any value for this QueryResults.
     * 
     * @return _any
     */
    public org.apache.axis.message.MessageElement [] get_any() {
        return _any;
    }


    /**
     * Sets the _any value for this QueryResults.
     * 
     * @param _any
     */
    public void set_any(org.apache.axis.message.MessageElement [] _any) {
        this._any = _any;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof QueryResults)) return false;
        QueryResults other = (QueryResults) obj;
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
            ((this.subscriptionID==null && other.getSubscriptionID()==null) || 
             (this.subscriptionID!=null &&
              this.subscriptionID.equals(other.getSubscriptionID()))) &&
            ((this.resultsBody==null && other.getResultsBody()==null) || 
             (this.resultsBody!=null &&
              this.resultsBody.equals(other.getResultsBody()))) &&
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
        if (getQueryName() != null) {
            _hashCode += getQueryName().hashCode();
        }
        if (getSubscriptionID() != null) {
            _hashCode += getSubscriptionID().hashCode();
        }
        if (getResultsBody() != null) {
            _hashCode += getResultsBody().hashCode();
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
        new org.apache.axis.description.TypeDesc(QueryResults.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("query.epcis.oliot.org", "QueryResults"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("queryName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "queryName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("subscriptionID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "subscriptionID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resultsBody");
        elemField.setXmlName(new javax.xml.namespace.QName("", "resultsBody"));
        elemField.setXmlType(new javax.xml.namespace.QName("query.epcis.oliot.org", "QueryResultsBody"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extension");
        elemField.setXmlName(new javax.xml.namespace.QName("", "extension"));
        elemField.setXmlType(new javax.xml.namespace.QName("query.epcis.oliot.org", "QueryResultsExtensionType"));
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
