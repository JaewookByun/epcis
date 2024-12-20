//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2024.12.19 at 01:15:43 PM KST 
//


package org.oliot.epcis.model;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Element;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 *         Association Event captures an event that applies to objects that
 *         have a physical association with one another, a physical association being more
 *         permanent or persistent than a physical aggregation.
 *       
 * 
 * <p>Java class for AssociationEventType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AssociationEventType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:epcglobal:epcis:xsd:2}EPCISEventType">
 *       &lt;sequence>
 *         &lt;element name="parentID" type="{urn:epcglobal:epcis:xsd:2}ParentIDType"/>
 *         &lt;element name="childEPCs" type="{urn:epcglobal:epcis:xsd:2}EPCListType" minOccurs="0"/>
 *         &lt;element name="childQuantityList" type="{urn:epcglobal:epcis:xsd:2}QuantityListType" minOccurs="0"/>
 *         &lt;element name="action" type="{urn:epcglobal:epcis:xsd:2}ActionType"/>
 *         &lt;element name="bizStep" type="{urn:epcglobal:epcis:xsd:2}BusinessStepIDType" minOccurs="0"/>
 *         &lt;element name="disposition" type="{urn:epcglobal:epcis:xsd:2}DispositionIDType" minOccurs="0"/>
 *         &lt;element name="readPoint" type="{urn:epcglobal:epcis:xsd:2}ReadPointType" minOccurs="0"/>
 *         &lt;element name="bizLocation" type="{urn:epcglobal:epcis:xsd:2}BusinessLocationType" minOccurs="0"/>
 *         &lt;element name="bizTransactionList" type="{urn:epcglobal:epcis:xsd:2}BusinessTransactionListType" minOccurs="0"/>
 *         &lt;element name="sourceList" type="{urn:epcglobal:epcis:xsd:2}SourceListType" minOccurs="0"/>
 *         &lt;element name="destinationList" type="{urn:epcglobal:epcis:xsd:2}DestinationListType" minOccurs="0"/>
 *         &lt;element name="sensorElementList" type="{urn:epcglobal:epcis:xsd:2}SensorElementListType" minOccurs="0"/>
 *         &lt;element name="persistentDisposition" type="{urn:epcglobal:epcis:xsd:2}PersistentDispositionType" minOccurs="0"/>
 *         &lt;element name="extension" type="{urn:epcglobal:epcis:xsd:2}AssociationEventExtensionType" minOccurs="0"/>
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AssociationEventType", propOrder = {
    "parentID",
    "childEPCs",
    "childQuantityList",
    "action",
    "bizStep",
    "disposition",
    "readPoint",
    "bizLocation",
    "bizTransactionList",
    "sourceList",
    "destinationList",
    "sensorElementList",
    "persistentDisposition",
    "extension",
    "any"
})
@XmlRootElement
public class AssociationEventType
    extends EPCISEventType
{

    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String parentID;
    protected EPCListType childEPCs;
    protected QuantityListType childQuantityList;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected ActionType action;
    @XmlSchemaType(name = "anyURI")
    protected String bizStep;
    @XmlSchemaType(name = "anyURI")
    protected String disposition;
    protected ReadPointType readPoint;
    protected BusinessLocationType bizLocation;
    protected BusinessTransactionListType bizTransactionList;
    protected SourceListType sourceList;
    protected DestinationListType destinationList;
    protected SensorElementListType sensorElementList;
    protected PersistentDispositionType persistentDisposition;
    protected AssociationEventExtensionType extension;
    @XmlAnyElement(lax = true)
    protected List<Object> any;

    /**
     * Gets the value of the parentID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParentID() {
        return parentID;
    }

    /**
     * Sets the value of the parentID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParentID(String value) {
        this.parentID = value;
    }

    /**
     * Gets the value of the childEPCs property.
     * 
     * @return
     *     possible object is
     *     {@link EPCListType }
     *     
     */
    public EPCListType getChildEPCs() {
        return childEPCs;
    }

    /**
     * Sets the value of the childEPCs property.
     * 
     * @param value
     *     allowed object is
     *     {@link EPCListType }
     *     
     */
    public void setChildEPCs(EPCListType value) {
        this.childEPCs = value;
    }

    /**
     * Gets the value of the childQuantityList property.
     * 
     * @return
     *     possible object is
     *     {@link QuantityListType }
     *     
     */
    public QuantityListType getChildQuantityList() {
        return childQuantityList;
    }

    /**
     * Sets the value of the childQuantityList property.
     * 
     * @param value
     *     allowed object is
     *     {@link QuantityListType }
     *     
     */
    public void setChildQuantityList(QuantityListType value) {
        this.childQuantityList = value;
    }

    /**
     * Gets the value of the action property.
     * 
     * @return
     *     possible object is
     *     {@link ActionType }
     *     
     */
    public ActionType getAction() {
        return action;
    }

    /**
     * Sets the value of the action property.
     * 
     * @param value
     *     allowed object is
     *     {@link ActionType }
     *     
     */
    public void setAction(ActionType value) {
        this.action = value;
    }

    /**
     * Gets the value of the bizStep property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBizStep() {
        return bizStep;
    }

    /**
     * Sets the value of the bizStep property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBizStep(String value) {
        this.bizStep = value;
    }

    /**
     * Gets the value of the disposition property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDisposition() {
        return disposition;
    }

    /**
     * Sets the value of the disposition property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDisposition(String value) {
        this.disposition = value;
    }

    /**
     * Gets the value of the readPoint property.
     * 
     * @return
     *     possible object is
     *     {@link ReadPointType }
     *     
     */
    public ReadPointType getReadPoint() {
        return readPoint;
    }

    /**
     * Sets the value of the readPoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReadPointType }
     *     
     */
    public void setReadPoint(ReadPointType value) {
        this.readPoint = value;
    }

    /**
     * Gets the value of the bizLocation property.
     * 
     * @return
     *     possible object is
     *     {@link BusinessLocationType }
     *     
     */
    public BusinessLocationType getBizLocation() {
        return bizLocation;
    }

    /**
     * Sets the value of the bizLocation property.
     * 
     * @param value
     *     allowed object is
     *     {@link BusinessLocationType }
     *     
     */
    public void setBizLocation(BusinessLocationType value) {
        this.bizLocation = value;
    }

    /**
     * Gets the value of the bizTransactionList property.
     * 
     * @return
     *     possible object is
     *     {@link BusinessTransactionListType }
     *     
     */
    public BusinessTransactionListType getBizTransactionList() {
        return bizTransactionList;
    }

    /**
     * Sets the value of the bizTransactionList property.
     * 
     * @param value
     *     allowed object is
     *     {@link BusinessTransactionListType }
     *     
     */
    public void setBizTransactionList(BusinessTransactionListType value) {
        this.bizTransactionList = value;
    }

    /**
     * Gets the value of the sourceList property.
     * 
     * @return
     *     possible object is
     *     {@link SourceListType }
     *     
     */
    public SourceListType getSourceList() {
        return sourceList;
    }

    /**
     * Sets the value of the sourceList property.
     * 
     * @param value
     *     allowed object is
     *     {@link SourceListType }
     *     
     */
    public void setSourceList(SourceListType value) {
        this.sourceList = value;
    }

    /**
     * Gets the value of the destinationList property.
     * 
     * @return
     *     possible object is
     *     {@link DestinationListType }
     *     
     */
    public DestinationListType getDestinationList() {
        return destinationList;
    }

    /**
     * Sets the value of the destinationList property.
     * 
     * @param value
     *     allowed object is
     *     {@link DestinationListType }
     *     
     */
    public void setDestinationList(DestinationListType value) {
        this.destinationList = value;
    }

    /**
     * Gets the value of the sensorElementList property.
     * 
     * @return
     *     possible object is
     *     {@link SensorElementListType }
     *     
     */
    public SensorElementListType getSensorElementList() {
        return sensorElementList;
    }

    /**
     * Sets the value of the sensorElementList property.
     * 
     * @param value
     *     allowed object is
     *     {@link SensorElementListType }
     *     
     */
    public void setSensorElementList(SensorElementListType value) {
        this.sensorElementList = value;
    }

    /**
     * Gets the value of the persistentDisposition property.
     * 
     * @return
     *     possible object is
     *     {@link PersistentDispositionType }
     *     
     */
    public PersistentDispositionType getPersistentDisposition() {
        return persistentDisposition;
    }

    /**
     * Sets the value of the persistentDisposition property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersistentDispositionType }
     *     
     */
    public void setPersistentDisposition(PersistentDispositionType value) {
        this.persistentDisposition = value;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link AssociationEventExtensionType }
     *     
     */
    public AssociationEventExtensionType getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link AssociationEventExtensionType }
     *     
     */
    public void setExtension(AssociationEventExtensionType value) {
        this.extension = value;
    }

    /**
     * Gets the value of the any property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the any property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAny().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * {@link Element }
     * 
     * 
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

    public void setAny(List<Object> any) {
        this.any = any;
    }
}
