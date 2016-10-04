//
// 이 파일은 JAXB(JavaTM Architecture for XML Binding) 참조 구현 2.2.8-b130911.1802 버전을 통해 생성되었습니다. 
// <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>를 참조하십시오. 
// 이 파일을 수정하면 소스 스키마를 재컴파일할 때 수정 사항이 손실됩니다. 
// 생성 날짜: 2016.04.29 시간 09:20:38 AM KST 
//

package org.oliot.model.epcis;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

/**
 * 
 * base type for all EPCIS events.
 * 
 * 
 * <p>
 * EPCISEventType complex type에 대한 Java 클래스입니다.
 * 
 * <p>
 * 다음 스키마 단편이 이 클래스에 포함되는 필요한 콘텐츠를 지정합니다.
 * 
 * <pre>
 * &lt;complexType name="EPCISEventType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="eventTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="recordTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="eventTimeZoneOffset" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="baseExtension" type="{urn:epcglobal:epcis:xsd:1}EPCISEventExtensionType" minOccurs="0"/>
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
@XmlType(name = "EPCISEventType", propOrder = { "eventTime", "recordTime", "eventTimeZoneOffset", "baseExtension" })
@XmlSeeAlso({ TransformationEventType.class, TransactionEventType.class, ObjectEventType.class, QuantityEventType.class,
		AggregationEventType.class })
public abstract class EPCISEventType {

	public void setOtherAttributes(Map<QName, String> otherAttributes) {
		this.otherAttributes = otherAttributes;
	}

	@XmlElement(required = true)
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar eventTime;
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar recordTime;
	@XmlElement(required = true)
	protected String eventTimeZoneOffset;
	protected EPCISEventExtensionType baseExtension;
	@XmlAnyAttribute
	private Map<QName, String> otherAttributes = new HashMap<QName, String>();

	/**
	 * eventTime 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getEventTime() {
		return eventTime;
	}

	/**
	 * eventTime 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setEventTime(XMLGregorianCalendar value) {
		this.eventTime = value;
	}

	/**
	 * recordTime 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getRecordTime() {
		return recordTime;
	}

	/**
	 * recordTime 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setRecordTime(XMLGregorianCalendar value) {
		this.recordTime = value;
	}

	/**
	 * eventTimeZoneOffset 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getEventTimeZoneOffset() {
		return eventTimeZoneOffset;
	}

	/**
	 * eventTimeZoneOffset 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setEventTimeZoneOffset(String value) {
		this.eventTimeZoneOffset = value;
	}

	/**
	 * baseExtension 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link EPCISEventExtensionType }
	 * 
	 */
	public EPCISEventExtensionType getBaseExtension() {
		return baseExtension;
	}

	/**
	 * baseExtension 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link EPCISEventExtensionType }
	 * 
	 */
	public void setBaseExtension(EPCISEventExtensionType value) {
		this.baseExtension = value;
	}

	/**
	 * Gets a map that contains attributes that aren't bound to any typed
	 * property on this class.
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
