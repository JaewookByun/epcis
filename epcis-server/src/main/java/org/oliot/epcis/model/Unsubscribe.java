//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2024.12.19 at 02:07:29 PM KST 
//

package org.oliot.epcis.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for Unsubscribe complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="Unsubscribe">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="subscriptionID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Unsubscribe", namespace = "urn:epcglobal:epcis-query:xsd:2", propOrder = { "subscriptionID" })
public class Unsubscribe {

	@XmlElement(required = true)
	protected String subscriptionID;

	/**
	 * Gets the value of the subscriptionID property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getSubscriptionID() {
		return subscriptionID;
	}

	/**
	 * Sets the value of the subscriptionID property.
	 * 
	 * @param value allowed object is {@link String }
	 * 
	 */
	public void setSubscriptionID(String value) {
		this.subscriptionID = value;
	}

}
