/**
 * TransformationEventType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.axis;


/**
 * Transformation Event captures an event in which inputs are consumed
 * 				and outputs are produced
 */
public class TransformationEventType  extends org.oliot.epcis.axis.EPCISEventType  implements java.io.Serializable, org.apache.axis.encoding.AnyContentType {
    private org.oliot.epcis.axis.EPC[] inputEPCList;

    private org.oliot.epcis.axis.QuantityElementType[] inputQuantityList;

    private org.oliot.epcis.axis.EPC[] outputEPCList;

    private org.oliot.epcis.axis.QuantityElementType[] outputQuantityList;

    private org.apache.axis.types.URI transformationID;

    private org.apache.axis.types.URI bizStep;

    private org.apache.axis.types.URI disposition;

    private org.oliot.epcis.axis.ReadPointType readPoint;

    private org.oliot.epcis.axis.BusinessLocationType bizLocation;

    private org.oliot.epcis.axis.BusinessTransactionType[] bizTransactionList;

    private org.oliot.epcis.axis.SourceDestType[] sourceList;

    private org.oliot.epcis.axis.SourceDestType[] destinationList;

    private org.oliot.epcis.axis.ILMDType ilmd;

    private org.oliot.epcis.axis.TransformationEventExtensionType extension;

    private org.apache.axis.message.MessageElement [] _any;

    public TransformationEventType() {
    }

    public TransformationEventType(
           java.util.Calendar eventTime,
           java.util.Calendar recordTime,
           java.lang.String eventTimeZoneOffset,
           org.oliot.epcis.axis.EPCISEventExtensionType baseExtension,
           org.oliot.epcis.axis.EPC[] inputEPCList,
           org.oliot.epcis.axis.QuantityElementType[] inputQuantityList,
           org.oliot.epcis.axis.EPC[] outputEPCList,
           org.oliot.epcis.axis.QuantityElementType[] outputQuantityList,
           org.apache.axis.types.URI transformationID,
           org.apache.axis.types.URI bizStep,
           org.apache.axis.types.URI disposition,
           org.oliot.epcis.axis.ReadPointType readPoint,
           org.oliot.epcis.axis.BusinessLocationType bizLocation,
           org.oliot.epcis.axis.BusinessTransactionType[] bizTransactionList,
           org.oliot.epcis.axis.SourceDestType[] sourceList,
           org.oliot.epcis.axis.SourceDestType[] destinationList,
           org.oliot.epcis.axis.ILMDType ilmd,
           org.oliot.epcis.axis.TransformationEventExtensionType extension,
           org.apache.axis.message.MessageElement [] _any) {
        super(
            eventTime,
            recordTime,
            eventTimeZoneOffset,
            baseExtension);
        this.inputEPCList = inputEPCList;
        this.inputQuantityList = inputQuantityList;
        this.outputEPCList = outputEPCList;
        this.outputQuantityList = outputQuantityList;
        this.transformationID = transformationID;
        this.bizStep = bizStep;
        this.disposition = disposition;
        this.readPoint = readPoint;
        this.bizLocation = bizLocation;
        this.bizTransactionList = bizTransactionList;
        this.sourceList = sourceList;
        this.destinationList = destinationList;
        this.ilmd = ilmd;
        this.extension = extension;
        this._any = _any;
    }


    /**
     * Gets the inputEPCList value for this TransformationEventType.
     * 
     * @return inputEPCList
     */
    public org.oliot.epcis.axis.EPC[] getInputEPCList() {
        return inputEPCList;
    }


    /**
     * Sets the inputEPCList value for this TransformationEventType.
     * 
     * @param inputEPCList
     */
    public void setInputEPCList(org.oliot.epcis.axis.EPC[] inputEPCList) {
        this.inputEPCList = inputEPCList;
    }


    /**
     * Gets the inputQuantityList value for this TransformationEventType.
     * 
     * @return inputQuantityList
     */
    public org.oliot.epcis.axis.QuantityElementType[] getInputQuantityList() {
        return inputQuantityList;
    }


    /**
     * Sets the inputQuantityList value for this TransformationEventType.
     * 
     * @param inputQuantityList
     */
    public void setInputQuantityList(org.oliot.epcis.axis.QuantityElementType[] inputQuantityList) {
        this.inputQuantityList = inputQuantityList;
    }


    /**
     * Gets the outputEPCList value for this TransformationEventType.
     * 
     * @return outputEPCList
     */
    public org.oliot.epcis.axis.EPC[] getOutputEPCList() {
        return outputEPCList;
    }


    /**
     * Sets the outputEPCList value for this TransformationEventType.
     * 
     * @param outputEPCList
     */
    public void setOutputEPCList(org.oliot.epcis.axis.EPC[] outputEPCList) {
        this.outputEPCList = outputEPCList;
    }


    /**
     * Gets the outputQuantityList value for this TransformationEventType.
     * 
     * @return outputQuantityList
     */
    public org.oliot.epcis.axis.QuantityElementType[] getOutputQuantityList() {
        return outputQuantityList;
    }


    /**
     * Sets the outputQuantityList value for this TransformationEventType.
     * 
     * @param outputQuantityList
     */
    public void setOutputQuantityList(org.oliot.epcis.axis.QuantityElementType[] outputQuantityList) {
        this.outputQuantityList = outputQuantityList;
    }


    /**
     * Gets the transformationID value for this TransformationEventType.
     * 
     * @return transformationID
     */
    public org.apache.axis.types.URI getTransformationID() {
        return transformationID;
    }


    /**
     * Sets the transformationID value for this TransformationEventType.
     * 
     * @param transformationID
     */
    public void setTransformationID(org.apache.axis.types.URI transformationID) {
        this.transformationID = transformationID;
    }


    /**
     * Gets the bizStep value for this TransformationEventType.
     * 
     * @return bizStep
     */
    public org.apache.axis.types.URI getBizStep() {
        return bizStep;
    }


    /**
     * Sets the bizStep value for this TransformationEventType.
     * 
     * @param bizStep
     */
    public void setBizStep(org.apache.axis.types.URI bizStep) {
        this.bizStep = bizStep;
    }


    /**
     * Gets the disposition value for this TransformationEventType.
     * 
     * @return disposition
     */
    public org.apache.axis.types.URI getDisposition() {
        return disposition;
    }


    /**
     * Sets the disposition value for this TransformationEventType.
     * 
     * @param disposition
     */
    public void setDisposition(org.apache.axis.types.URI disposition) {
        this.disposition = disposition;
    }


    /**
     * Gets the readPoint value for this TransformationEventType.
     * 
     * @return readPoint
     */
    public org.oliot.epcis.axis.ReadPointType getReadPoint() {
        return readPoint;
    }


    /**
     * Sets the readPoint value for this TransformationEventType.
     * 
     * @param readPoint
     */
    public void setReadPoint(org.oliot.epcis.axis.ReadPointType readPoint) {
        this.readPoint = readPoint;
    }


    /**
     * Gets the bizLocation value for this TransformationEventType.
     * 
     * @return bizLocation
     */
    public org.oliot.epcis.axis.BusinessLocationType getBizLocation() {
        return bizLocation;
    }


    /**
     * Sets the bizLocation value for this TransformationEventType.
     * 
     * @param bizLocation
     */
    public void setBizLocation(org.oliot.epcis.axis.BusinessLocationType bizLocation) {
        this.bizLocation = bizLocation;
    }


    /**
     * Gets the bizTransactionList value for this TransformationEventType.
     * 
     * @return bizTransactionList
     */
    public org.oliot.epcis.axis.BusinessTransactionType[] getBizTransactionList() {
        return bizTransactionList;
    }


    /**
     * Sets the bizTransactionList value for this TransformationEventType.
     * 
     * @param bizTransactionList
     */
    public void setBizTransactionList(org.oliot.epcis.axis.BusinessTransactionType[] bizTransactionList) {
        this.bizTransactionList = bizTransactionList;
    }


    /**
     * Gets the sourceList value for this TransformationEventType.
     * 
     * @return sourceList
     */
    public org.oliot.epcis.axis.SourceDestType[] getSourceList() {
        return sourceList;
    }


    /**
     * Sets the sourceList value for this TransformationEventType.
     * 
     * @param sourceList
     */
    public void setSourceList(org.oliot.epcis.axis.SourceDestType[] sourceList) {
        this.sourceList = sourceList;
    }


    /**
     * Gets the destinationList value for this TransformationEventType.
     * 
     * @return destinationList
     */
    public org.oliot.epcis.axis.SourceDestType[] getDestinationList() {
        return destinationList;
    }


    /**
     * Sets the destinationList value for this TransformationEventType.
     * 
     * @param destinationList
     */
    public void setDestinationList(org.oliot.epcis.axis.SourceDestType[] destinationList) {
        this.destinationList = destinationList;
    }


    /**
     * Gets the ilmd value for this TransformationEventType.
     * 
     * @return ilmd
     */
    public org.oliot.epcis.axis.ILMDType getIlmd() {
        return ilmd;
    }


    /**
     * Sets the ilmd value for this TransformationEventType.
     * 
     * @param ilmd
     */
    public void setIlmd(org.oliot.epcis.axis.ILMDType ilmd) {
        this.ilmd = ilmd;
    }


    /**
     * Gets the extension value for this TransformationEventType.
     * 
     * @return extension
     */
    public org.oliot.epcis.axis.TransformationEventExtensionType getExtension() {
        return extension;
    }


    /**
     * Sets the extension value for this TransformationEventType.
     * 
     * @param extension
     */
    public void setExtension(org.oliot.epcis.axis.TransformationEventExtensionType extension) {
        this.extension = extension;
    }


    /**
     * Gets the _any value for this TransformationEventType.
     * 
     * @return _any
     */
    public org.apache.axis.message.MessageElement [] get_any() {
        return _any;
    }


    /**
     * Sets the _any value for this TransformationEventType.
     * 
     * @param _any
     */
    public void set_any(org.apache.axis.message.MessageElement [] _any) {
        this._any = _any;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TransformationEventType)) return false;
        TransformationEventType other = (TransformationEventType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.inputEPCList==null && other.getInputEPCList()==null) || 
             (this.inputEPCList!=null &&
              java.util.Arrays.equals(this.inputEPCList, other.getInputEPCList()))) &&
            ((this.inputQuantityList==null && other.getInputQuantityList()==null) || 
             (this.inputQuantityList!=null &&
              java.util.Arrays.equals(this.inputQuantityList, other.getInputQuantityList()))) &&
            ((this.outputEPCList==null && other.getOutputEPCList()==null) || 
             (this.outputEPCList!=null &&
              java.util.Arrays.equals(this.outputEPCList, other.getOutputEPCList()))) &&
            ((this.outputQuantityList==null && other.getOutputQuantityList()==null) || 
             (this.outputQuantityList!=null &&
              java.util.Arrays.equals(this.outputQuantityList, other.getOutputQuantityList()))) &&
            ((this.transformationID==null && other.getTransformationID()==null) || 
             (this.transformationID!=null &&
              this.transformationID.equals(other.getTransformationID()))) &&
            ((this.bizStep==null && other.getBizStep()==null) || 
             (this.bizStep!=null &&
              this.bizStep.equals(other.getBizStep()))) &&
            ((this.disposition==null && other.getDisposition()==null) || 
             (this.disposition!=null &&
              this.disposition.equals(other.getDisposition()))) &&
            ((this.readPoint==null && other.getReadPoint()==null) || 
             (this.readPoint!=null &&
              this.readPoint.equals(other.getReadPoint()))) &&
            ((this.bizLocation==null && other.getBizLocation()==null) || 
             (this.bizLocation!=null &&
              this.bizLocation.equals(other.getBizLocation()))) &&
            ((this.bizTransactionList==null && other.getBizTransactionList()==null) || 
             (this.bizTransactionList!=null &&
              java.util.Arrays.equals(this.bizTransactionList, other.getBizTransactionList()))) &&
            ((this.sourceList==null && other.getSourceList()==null) || 
             (this.sourceList!=null &&
              java.util.Arrays.equals(this.sourceList, other.getSourceList()))) &&
            ((this.destinationList==null && other.getDestinationList()==null) || 
             (this.destinationList!=null &&
              java.util.Arrays.equals(this.destinationList, other.getDestinationList()))) &&
            ((this.ilmd==null && other.getIlmd()==null) || 
             (this.ilmd!=null &&
              this.ilmd.equals(other.getIlmd()))) &&
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
        int _hashCode = super.hashCode();
        if (getInputEPCList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getInputEPCList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getInputEPCList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getInputQuantityList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getInputQuantityList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getInputQuantityList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getOutputEPCList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getOutputEPCList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getOutputEPCList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getOutputQuantityList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getOutputQuantityList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getOutputQuantityList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getTransformationID() != null) {
            _hashCode += getTransformationID().hashCode();
        }
        if (getBizStep() != null) {
            _hashCode += getBizStep().hashCode();
        }
        if (getDisposition() != null) {
            _hashCode += getDisposition().hashCode();
        }
        if (getReadPoint() != null) {
            _hashCode += getReadPoint().hashCode();
        }
        if (getBizLocation() != null) {
            _hashCode += getBizLocation().hashCode();
        }
        if (getBizTransactionList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getBizTransactionList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getBizTransactionList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getSourceList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSourceList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSourceList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getDestinationList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getDestinationList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getDestinationList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getIlmd() != null) {
            _hashCode += getIlmd().hashCode();
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
        new org.apache.axis.description.TypeDesc(TransformationEventType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("axis.epcis.oliot.org", "TransformationEventType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("inputEPCList");
        elemField.setXmlName(new javax.xml.namespace.QName("", "inputEPCList"));
        elemField.setXmlType(new javax.xml.namespace.QName("epcglobal.axis.epcis.oliot.org", "EPC"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("", "epc"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("inputQuantityList");
        elemField.setXmlName(new javax.xml.namespace.QName("", "inputQuantityList"));
        elemField.setXmlType(new javax.xml.namespace.QName("axis.epcis.oliot.org", "QuantityElementType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("", "quantityElement"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("outputEPCList");
        elemField.setXmlName(new javax.xml.namespace.QName("", "outputEPCList"));
        elemField.setXmlType(new javax.xml.namespace.QName("epcglobal.axis.epcis.oliot.org", "EPC"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("", "epc"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("outputQuantityList");
        elemField.setXmlName(new javax.xml.namespace.QName("", "outputQuantityList"));
        elemField.setXmlType(new javax.xml.namespace.QName("axis.epcis.oliot.org", "QuantityElementType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("", "quantityElement"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transformationID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "transformationID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyURI"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bizStep");
        elemField.setXmlName(new javax.xml.namespace.QName("", "bizStep"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyURI"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("disposition");
        elemField.setXmlName(new javax.xml.namespace.QName("", "disposition"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyURI"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("readPoint");
        elemField.setXmlName(new javax.xml.namespace.QName("", "readPoint"));
        elemField.setXmlType(new javax.xml.namespace.QName("axis.epcis.oliot.org", "ReadPointType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bizLocation");
        elemField.setXmlName(new javax.xml.namespace.QName("", "bizLocation"));
        elemField.setXmlType(new javax.xml.namespace.QName("axis.epcis.oliot.org", "BusinessLocationType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bizTransactionList");
        elemField.setXmlName(new javax.xml.namespace.QName("", "bizTransactionList"));
        elemField.setXmlType(new javax.xml.namespace.QName("axis.epcis.oliot.org", "BusinessTransactionType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("", "bizTransaction"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sourceList");
        elemField.setXmlName(new javax.xml.namespace.QName("", "sourceList"));
        elemField.setXmlType(new javax.xml.namespace.QName("axis.epcis.oliot.org", "SourceDestType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("", "source"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("destinationList");
        elemField.setXmlName(new javax.xml.namespace.QName("", "destinationList"));
        elemField.setXmlType(new javax.xml.namespace.QName("axis.epcis.oliot.org", "SourceDestType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("", "destination"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ilmd");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ilmd"));
        elemField.setXmlType(new javax.xml.namespace.QName("axis.epcis.oliot.org", "ILMDType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extension");
        elemField.setXmlName(new javax.xml.namespace.QName("", "extension"));
        elemField.setXmlType(new javax.xml.namespace.QName("axis.epcis.oliot.org", "TransformationEventExtensionType"));
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
