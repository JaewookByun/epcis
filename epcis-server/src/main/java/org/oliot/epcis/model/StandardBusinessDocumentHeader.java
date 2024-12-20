//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2024.12.19 at 01:15:43 PM KST 
//

package org.oliot.epcis.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Java class for StandardBusinessDocumentHeader complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="StandardBusinessDocumentHeader">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="HeaderVersion" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Sender" type="{http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader}Partner" maxOccurs="unbounded"/>
 *         &lt;element name="Receiver" type="{http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader}Partner" maxOccurs="unbounded"/>
 *         &lt;element name="DocumentIdentification" type="{http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader}DocumentIdentification"/>
 *         &lt;element name="Manifest" type="{http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader}Manifest" minOccurs="0"/>
 *         &lt;element name="BusinessScope" type="{http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader}BusinessScope" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StandardBusinessDocumentHeader", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", propOrder = {
		"headerVersion", "sender", "receiver", "documentIdentification", "manifest", "businessScope" })
public class StandardBusinessDocumentHeader {

	@XmlElement(name = "HeaderVersion", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", required = true)
	protected String headerVersion;
	@XmlElement(name = "Sender", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", required = true)
	protected List<Partner> sender;
	@XmlElement(name = "Receiver", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", required = true)
	protected List<Partner> receiver;
	@XmlElement(name = "DocumentIdentification", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", required = true)
	protected DocumentIdentification documentIdentification;
	@XmlElement(name = "Manifest", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader")
	protected Manifest manifest;
	@XmlElement(name = "BusinessScope", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader")
	protected BusinessScope businessScope;

	/**
	 * Gets the value of the headerVersion property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getHeaderVersion() {
		return headerVersion;
	}

	/**
	 * Sets the value of the headerVersion property.
	 * 
	 * @param value allowed object is {@link String }
	 * 
	 */
	public void setHeaderVersion(String value) {
		this.headerVersion = value;
	}

	/**
	 * Gets the value of the sender property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot.
	 * Therefore any modification you make to the returned list will be present
	 * inside the JAXB object. This is why there is not a <CODE>set</CODE> method
	 * for the sender property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getSender().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link Partner }
	 * 
	 * 
	 */
	public List<Partner> getSender() {
		if (sender == null) {
			sender = new ArrayList<Partner>();
		}
		return this.sender;
	}

	/**
	 * Gets the value of the receiver property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot.
	 * Therefore any modification you make to the returned list will be present
	 * inside the JAXB object. This is why there is not a <CODE>set</CODE> method
	 * for the receiver property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getReceiver().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link Partner }
	 * 
	 * 
	 */
	public List<Partner> getReceiver() {
		if (receiver == null) {
			receiver = new ArrayList<Partner>();
		}
		return this.receiver;
	}

	/**
	 * Gets the value of the documentIdentification property.
	 * 
	 * @return possible object is {@link DocumentIdentification }
	 * 
	 */
	public DocumentIdentification getDocumentIdentification() {
		return documentIdentification;
	}

	/**
	 * Sets the value of the documentIdentification property.
	 * 
	 * @param value allowed object is {@link DocumentIdentification }
	 * 
	 */
	public void setDocumentIdentification(DocumentIdentification value) {
		this.documentIdentification = value;
	}

	/**
	 * Gets the value of the manifest property.
	 * 
	 * @return possible object is {@link Manifest }
	 * 
	 */
	public Manifest getManifest() {
		return manifest;
	}

	/**
	 * Sets the value of the manifest property.
	 * 
	 * @param value allowed object is {@link Manifest }
	 * 
	 */
	public void setManifest(Manifest value) {
		this.manifest = value;
	}

	/**
	 * Gets the value of the businessScope property.
	 * 
	 * @return possible object is {@link BusinessScope }
	 * 
	 */
	public BusinessScope getBusinessScope() {
		return businessScope;
	}

	/**
	 * Sets the value of the businessScope property.
	 * 
	 * @param value allowed object is {@link BusinessScope }
	 * 
	 */
	public void setBusinessScope(BusinessScope value) {
		this.businessScope = value;
	}

}
