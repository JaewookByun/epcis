/**
 * QueryResultsBody.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.query;

@SuppressWarnings("serial")
public class QueryResultsBody  implements java.io.Serializable {
    private org.oliot.epcis.EventListType eventList;

    private org.oliot.epcis.masterdata.VocabularyType[] vocabularyList;

    public QueryResultsBody() {
    }

    public QueryResultsBody(
           org.oliot.epcis.EventListType eventList,
           org.oliot.epcis.masterdata.VocabularyType[] vocabularyList) {
           this.eventList = eventList;
           this.vocabularyList = vocabularyList;
    }


    /**
     * Gets the eventList value for this QueryResultsBody.
     * 
     * @return eventList
     */
    public org.oliot.epcis.EventListType getEventList() {
        return eventList;
    }


    /**
     * Sets the eventList value for this QueryResultsBody.
     * 
     * @param eventList
     */
    public void setEventList(org.oliot.epcis.EventListType eventList) {
        this.eventList = eventList;
    }


    /**
     * Gets the vocabularyList value for this QueryResultsBody.
     * 
     * @return vocabularyList
     */
    public org.oliot.epcis.masterdata.VocabularyType[] getVocabularyList() {
        return vocabularyList;
    }


    /**
     * Sets the vocabularyList value for this QueryResultsBody.
     * 
     * @param vocabularyList
     */
    public void setVocabularyList(org.oliot.epcis.masterdata.VocabularyType[] vocabularyList) {
        this.vocabularyList = vocabularyList;
    }

    private java.lang.Object __equalsCalc = null;
    @SuppressWarnings("unused")
	public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof QueryResultsBody)) return false;
        QueryResultsBody other = (QueryResultsBody) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.eventList==null && other.getEventList()==null) || 
             (this.eventList!=null &&
              this.eventList.equals(other.getEventList()))) &&
            ((this.vocabularyList==null && other.getVocabularyList()==null) || 
             (this.vocabularyList!=null &&
              java.util.Arrays.equals(this.vocabularyList, other.getVocabularyList())));
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
        if (getEventList() != null) {
            _hashCode += getEventList().hashCode();
        }
        if (getVocabularyList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getVocabularyList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getVocabularyList(), i);
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
        new org.apache.axis.description.TypeDesc(QueryResultsBody.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("query.epcis.oliot.org", "QueryResultsBody"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("eventList");
        elemField.setXmlName(new javax.xml.namespace.QName("", "EventList"));
        elemField.setXmlType(new javax.xml.namespace.QName("epcis.oliot.org", "EventListType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("vocabularyList");
        elemField.setXmlName(new javax.xml.namespace.QName("", "VocabularyList"));
        elemField.setXmlType(new javax.xml.namespace.QName("masterdata.epcis.oliot.org", "VocabularyType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("", "Vocabulary"));
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
