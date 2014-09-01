/**
 * SubscriptionControls.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.axis;

public class SubscriptionControls  implements java.io.Serializable, org.apache.axis.encoding.AnyContentType {
    private org.oliot.epcis.axis.QuerySchedule schedule;

    private org.apache.axis.types.URI trigger;

    private java.util.Calendar initialRecordTime;

    private boolean reportIfEmpty;

    private org.oliot.epcis.axis.SubscriptionControlsExtensionType extension;

    private org.apache.axis.message.MessageElement [] _any;

    public SubscriptionControls() {
    }

    public SubscriptionControls(
           org.oliot.epcis.axis.QuerySchedule schedule,
           org.apache.axis.types.URI trigger,
           java.util.Calendar initialRecordTime,
           boolean reportIfEmpty,
           org.oliot.epcis.axis.SubscriptionControlsExtensionType extension,
           org.apache.axis.message.MessageElement [] _any) {
           this.schedule = schedule;
           this.trigger = trigger;
           this.initialRecordTime = initialRecordTime;
           this.reportIfEmpty = reportIfEmpty;
           this.extension = extension;
           this._any = _any;
    }


    /**
     * Gets the schedule value for this SubscriptionControls.
     * 
     * @return schedule
     */
    public org.oliot.epcis.axis.QuerySchedule getSchedule() {
        return schedule;
    }


    /**
     * Sets the schedule value for this SubscriptionControls.
     * 
     * @param schedule
     */
    public void setSchedule(org.oliot.epcis.axis.QuerySchedule schedule) {
        this.schedule = schedule;
    }


    /**
     * Gets the trigger value for this SubscriptionControls.
     * 
     * @return trigger
     */
    public org.apache.axis.types.URI getTrigger() {
        return trigger;
    }


    /**
     * Sets the trigger value for this SubscriptionControls.
     * 
     * @param trigger
     */
    public void setTrigger(org.apache.axis.types.URI trigger) {
        this.trigger = trigger;
    }


    /**
     * Gets the initialRecordTime value for this SubscriptionControls.
     * 
     * @return initialRecordTime
     */
    public java.util.Calendar getInitialRecordTime() {
        return initialRecordTime;
    }


    /**
     * Sets the initialRecordTime value for this SubscriptionControls.
     * 
     * @param initialRecordTime
     */
    public void setInitialRecordTime(java.util.Calendar initialRecordTime) {
        this.initialRecordTime = initialRecordTime;
    }


    /**
     * Gets the reportIfEmpty value for this SubscriptionControls.
     * 
     * @return reportIfEmpty
     */
    public boolean isReportIfEmpty() {
        return reportIfEmpty;
    }


    /**
     * Sets the reportIfEmpty value for this SubscriptionControls.
     * 
     * @param reportIfEmpty
     */
    public void setReportIfEmpty(boolean reportIfEmpty) {
        this.reportIfEmpty = reportIfEmpty;
    }


    /**
     * Gets the extension value for this SubscriptionControls.
     * 
     * @return extension
     */
    public org.oliot.epcis.axis.SubscriptionControlsExtensionType getExtension() {
        return extension;
    }


    /**
     * Sets the extension value for this SubscriptionControls.
     * 
     * @param extension
     */
    public void setExtension(org.oliot.epcis.axis.SubscriptionControlsExtensionType extension) {
        this.extension = extension;
    }


    /**
     * Gets the _any value for this SubscriptionControls.
     * 
     * @return _any
     */
    public org.apache.axis.message.MessageElement [] get_any() {
        return _any;
    }


    /**
     * Sets the _any value for this SubscriptionControls.
     * 
     * @param _any
     */
    public void set_any(org.apache.axis.message.MessageElement [] _any) {
        this._any = _any;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SubscriptionControls)) return false;
        SubscriptionControls other = (SubscriptionControls) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.schedule==null && other.getSchedule()==null) || 
             (this.schedule!=null &&
              this.schedule.equals(other.getSchedule()))) &&
            ((this.trigger==null && other.getTrigger()==null) || 
             (this.trigger!=null &&
              this.trigger.equals(other.getTrigger()))) &&
            ((this.initialRecordTime==null && other.getInitialRecordTime()==null) || 
             (this.initialRecordTime!=null &&
              this.initialRecordTime.equals(other.getInitialRecordTime()))) &&
            this.reportIfEmpty == other.isReportIfEmpty() &&
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
        if (getSchedule() != null) {
            _hashCode += getSchedule().hashCode();
        }
        if (getTrigger() != null) {
            _hashCode += getTrigger().hashCode();
        }
        if (getInitialRecordTime() != null) {
            _hashCode += getInitialRecordTime().hashCode();
        }
        _hashCode += (isReportIfEmpty() ? Boolean.TRUE : Boolean.FALSE).hashCode();
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
        new org.apache.axis.description.TypeDesc(SubscriptionControls.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "SubscriptionControls"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("schedule");
        elemField.setXmlName(new javax.xml.namespace.QName("", "schedule"));
        elemField.setXmlType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "QuerySchedule"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("trigger");
        elemField.setXmlName(new javax.xml.namespace.QName("", "trigger"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyURI"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("initialRecordTime");
        elemField.setXmlName(new javax.xml.namespace.QName("", "initialRecordTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reportIfEmpty");
        elemField.setXmlName(new javax.xml.namespace.QName("", "reportIfEmpty"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extension");
        elemField.setXmlName(new javax.xml.namespace.QName("", "extension"));
        elemField.setXmlType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "SubscriptionControlsExtensionType"));
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
