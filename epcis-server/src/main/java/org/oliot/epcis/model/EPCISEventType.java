//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.01.05 at 11:33:26 AM KST 
//

package org.oliot.epcis.model;

import java.util.HashMap;
import java.util.Map;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

/**
 * 
 * base type for all EPCIS events.
 * 
 * 
 * <p>
 * Java class for EPCISEventType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="EPCISEventType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="eventTime" type="{urn:epcglobal:epcis:xsd:2}DateTimeStamp"/>
 *         &lt;element name="recordTime" type="{urn:epcglobal:epcis:xsd:2}DateTimeStamp" minOccurs="0"/>
 *         &lt;element name="eventTimeZoneOffset" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="eventID" type="{urn:epcglobal:epcis:xsd:2}EventIDType" minOccurs="0"/>
 *         &lt;element name="errorDeclaration" type="{urn:epcglobal:epcis:xsd:2}ErrorDeclarationType" minOccurs="0"/>
 *         &lt;element name="certificationInfo" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         &lt;element name="baseExtension" type="{urn:epcglobal:epcis:xsd:2}EPCISEventExtensionType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax'/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EPCISEventType", propOrder = { "eventTime", "recordTime", "eventTimeZoneOffset", "eventID",
		"errorDeclaration", "certificationInfo", "baseExtension" })
@XmlSeeAlso({ AssociationEventType.class, TransformationEventType.class, TransactionEventType.class,
		ObjectEventType.class, AggregationEventType.class })
public abstract class EPCISEventType {

	@XmlElement(required = true)
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar eventTime;
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar recordTime;
	@XmlElement(required = true)
	protected String eventTimeZoneOffset;
	@XmlSchemaType(name = "anyURI")
	protected String eventID;
	protected ErrorDeclarationType errorDeclaration;
	@XmlSchemaType(name = "anyURI")
	protected String certificationInfo;
	protected EPCISEventExtensionType baseExtension;
	@XmlAnyAttribute
	private Map<QName, String> otherAttributes = new HashMap<QName, String>();

	/**
	 * Gets the value of the eventTime property.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getEventTime() {
		return eventTime;
	}

	/**
	 * Sets the value of the eventTime property.
	 * 
	 * @param value allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setEventTime(XMLGregorianCalendar value) {
		this.eventTime = value;
	}

	/**
	 * Gets the value of the recordTime property.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getRecordTime() {
		return recordTime;
	}

	/**
	 * Sets the value of the recordTime property.
	 * 
	 * @param value allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setRecordTime(XMLGregorianCalendar value) {
		this.recordTime = value;
	}

	/**
	 * Gets the value of the eventTimeZoneOffset property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getEventTimeZoneOffset() {
		return eventTimeZoneOffset;
	}

	/**
	 * Sets the value of the eventTimeZoneOffset property.
	 * 
	 * @param value allowed object is {@link String }
	 * 
	 */
	public void setEventTimeZoneOffset(String value) {
		this.eventTimeZoneOffset = value;
	}

	/**
	 * Gets the value of the eventID property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getEventID() {
		return eventID;
	}

	/**
	 * Sets the value of the eventID property.
	 * 
	 * @param value allowed object is {@link String }
	 * 
	 */
	public void setEventID(String value) {
		this.eventID = value;
	}

	/**
	 * Gets the value of the errorDeclaration property.
	 * 
	 * @return possible object is {@link ErrorDeclarationType }
	 * 
	 */
	public ErrorDeclarationType getErrorDeclaration() {
		return errorDeclaration;
	}

	/**
	 * Sets the value of the errorDeclaration property.
	 * 
	 * @param value allowed object is {@link ErrorDeclarationType }
	 * 
	 */
	public void setErrorDeclaration(ErrorDeclarationType value) {
		this.errorDeclaration = value;
	}

	/**
	 * Gets the value of the certificationInfo property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCertificationInfo() {
		return certificationInfo;
	}

	/**
	 * Sets the value of the certificationInfo property.
	 * 
	 * @param value allowed object is {@link String }
	 * 
	 */
	public void setCertificationInfo(String value) {
		this.certificationInfo = value;
	}

	/**
	 * Gets the value of the baseExtension property.
	 * 
	 * @return possible object is {@link EPCISEventExtensionType }
	 * 
	 */
	public EPCISEventExtensionType getBaseExtension() {
		return baseExtension;
	}

	/**
	 * Sets the value of the baseExtension property.
	 * 
	 * @param value allowed object is {@link EPCISEventExtensionType }
	 * 
	 */
	public void setBaseExtension(EPCISEventExtensionType value) {
		this.baseExtension = value;
	}

	/**
	 * Gets a map that contains attributes that aren't bound to any typed property
	 * on this class.
	 * 
	 * <p>
	 * the map is keyed by the name of the attribute and the value is the string
	 * value of the attribute.
	 * 
	 * the map returned by this method is live, and you can add new attribute by
	 * updating the map directly. Because of this design, there's no setter.
	 * 
	 * 
	 * @return always non-null
	 */
	public Map<QName, String> getOtherAttributes() {
		return otherAttributes;
	}

}
