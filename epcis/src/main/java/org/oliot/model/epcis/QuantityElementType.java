//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.09.15 at 01:39:48 PM KST 
//


package org.oliot.model.epcis;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for QuantityElementType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="QuantityElementType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="epcClass" type="{axis.epcis.oliot.org}EPCClassType"/>
 *         &lt;element name="quantity" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="uom" type="{axis.epcis.oliot.org}UOMType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuantityElementType", propOrder = {
    "epcClass",
    "quantity",
    "uom"
})
public class QuantityElementType {

    @XmlElement(required = true)
    protected String epcClass;
    protected float quantity;
    protected String uom;

    /**
     * Gets the value of the epcClass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEpcClass() {
        return epcClass;
    }

    /**
     * Sets the value of the epcClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEpcClass(String value) {
        this.epcClass = value;
    }

    /**
     * Gets the value of the quantity property.
     * 
     */
    public float getQuantity() {
        return quantity;
    }

    /**
     * Sets the value of the quantity property.
     * 
     */
    public void setQuantity(float value) {
        this.quantity = value;
    }

    /**
     * Gets the value of the uom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUom() {
        return uom;
    }

    /**
     * Sets the value of the uom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUom(String value) {
        this.uom = value;
    }

	public QuantityElementType(String epcClass, float quantity, String uom) {
		super();
		this.epcClass = epcClass;
		this.quantity = quantity;
		this.uom = uom;
	}

	public QuantityElementType() {
		super();
	}

    
    
    
}
