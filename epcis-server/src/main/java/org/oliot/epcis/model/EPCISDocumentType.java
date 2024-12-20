//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2024.12.19 at 01:15:43 PM KST 
//


package org.oliot.epcis.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

import jakarta.xml.bind.annotation.*;
import org.w3c.dom.Element;


/**
 * 
 *         document that contains a Header and a Body.
 *       
 * 
 * <p>Java class for EPCISDocumentType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EPCISDocumentType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:epcglobal:xsd:1}Document">
 *       &lt;sequence>
 *         &lt;element name="EPCISHeader" type="{urn:epcglobal:epcis:xsd:2}EPCISHeaderType" minOccurs="0"/>
 *         &lt;element name="EPCISBody" type="{urn:epcglobal:epcis:xsd:2}EPCISBodyType"/>
 *         &lt;element name="extension" type="{urn:epcglobal:epcis:xsd:2}EPCISDocumentExtensionType" minOccurs="0"/>
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
@XmlType(name = "EPCISDocumentType", propOrder = {
    "epcisHeader",
    "epcisBody",
    "extension",
    "any"
})
public class EPCISDocumentType
    extends Document
{

    @XmlElement(name = "EPCISHeader")
    protected EPCISHeaderType epcisHeader;
    @XmlElement(name = "EPCISBody", required = true)
    protected EPCISBodyType epcisBody;
    protected EPCISDocumentExtensionType extension;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the epcisHeader property.
     * 
     * @return
     *     possible object is
     *     {@link EPCISHeaderType }
     *     
     */
    public EPCISHeaderType getEPCISHeader() {
        return epcisHeader;
    }

    /**
     * Sets the value of the epcisHeader property.
     * 
     * @param value
     *     allowed object is
     *     {@link EPCISHeaderType }
     *     
     */
    public void setEPCISHeader(EPCISHeaderType value) {
        this.epcisHeader = value;
    }

    /**
     * Gets the value of the epcisBody property.
     * 
     * @return
     *     possible object is
     *     {@link EPCISBodyType }
     *     
     */
    public EPCISBodyType getEPCISBody() {
        return epcisBody;
    }

    /**
     * Sets the value of the epcisBody property.
     * 
     * @param value
     *     allowed object is
     *     {@link EPCISBodyType }
     *     
     */
    public void setEPCISBody(EPCISBodyType value) {
        this.epcisBody = value;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link EPCISDocumentExtensionType }
     *     
     */
    public EPCISDocumentExtensionType getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link EPCISDocumentExtensionType }
     *     
     */
    public void setExtension(EPCISDocumentExtensionType value) {
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

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     * 
     * <p>
     * the map is keyed by the name of the attribute and 
     * the value is the string value of the attribute.
     * 
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     * 
     * 
     * @return
     *     always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

}
