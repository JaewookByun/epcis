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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

/**
 * <p>
 * EPCISEventExtensionType complex type에 대한 Java 클래스입니다.
 * 
 * <p>
 * 다음 스키마 단편이 이 클래스에 포함되는 필요한 콘텐츠를 지정합니다.
 * 
 * <pre>
 * &lt;complexType name="EPCISEventExtensionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="eventID" type="{urn:epcglobal:epcis:xsd:1}EventIDType" minOccurs="0"/>
 *         &lt;element name="errorDeclaration" type="{urn:epcglobal:epcis:xsd:1}ErrorDeclarationType" minOccurs="0"/>
 *         &lt;element name="extension" type="{urn:epcglobal:epcis:xsd:1}EPCISEventExtension2Type" minOccurs="0"/>
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
@XmlType(name = "EPCISEventExtensionType", propOrder = { "eventID", "errorDeclaration", "extension" })
public class EPCISEventExtensionType {

	public void setOtherAttributes(Map<QName, String> otherAttributes) {
		this.otherAttributes = otherAttributes;
	}

	@XmlSchemaType(name = "anyURI")
	protected String eventID;
	protected ErrorDeclarationType errorDeclaration;
	protected EPCISEventExtension2Type extension;
	@XmlAnyAttribute
	private Map<QName, String> otherAttributes = new HashMap<QName, String>();

	/**
	 * eventID 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getEventID() {
		return eventID;
	}

	/**
	 * eventID 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setEventID(String value) {
		this.eventID = value;
	}

	/**
	 * errorDeclaration 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link ErrorDeclarationType }
	 * 
	 */
	public ErrorDeclarationType getErrorDeclaration() {
		return errorDeclaration;
	}

	/**
	 * errorDeclaration 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link ErrorDeclarationType }
	 * 
	 */
	public void setErrorDeclaration(ErrorDeclarationType value) {
		this.errorDeclaration = value;
	}

	/**
	 * extension 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link EPCISEventExtension2Type }
	 * 
	 */
	public EPCISEventExtension2Type getExtension() {
		return extension;
	}

	/**
	 * extension 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link EPCISEventExtension2Type }
	 * 
	 */
	public void setExtension(EPCISEventExtension2Type value) {
		this.extension = value;
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
