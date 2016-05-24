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
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

/**
 * <p>
 * ObjectEventExtensionType complex type에 대한 Java 클래스입니다.
 * 
 * <p>
 * 다음 스키마 단편이 이 클래스에 포함되는 필요한 콘텐츠를 지정합니다.
 * 
 * <pre>
 * &lt;complexType name="ObjectEventExtensionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="quantityList" type="{urn:epcglobal:epcis:xsd:1}QuantityListType" minOccurs="0"/>
 *         &lt;element name="sourceList" type="{urn:epcglobal:epcis:xsd:1}SourceListType" minOccurs="0"/>
 *         &lt;element name="destinationList" type="{urn:epcglobal:epcis:xsd:1}DestinationListType" minOccurs="0"/>
 *         &lt;element name="ilmd" type="{urn:epcglobal:epcis:xsd:1}ILMDType" minOccurs="0"/>
 *         &lt;element name="extension" type="{urn:epcglobal:epcis:xsd:1}ObjectEventExtension2Type" minOccurs="0"/>
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
@XmlType(name = "ObjectEventExtensionType", propOrder = { "quantityList", "sourceList", "destinationList", "ilmd",
		"extension" })
public class ObjectEventExtensionType {

	protected QuantityListType quantityList;
	protected SourceListType sourceList;
	protected DestinationListType destinationList;
	protected ILMDType ilmd;
	protected ObjectEventExtension2Type extension;
	@XmlAnyAttribute
	private Map<QName, String> otherAttributes = new HashMap<QName, String>();

	/**
	 * quantityList 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link QuantityListType }
	 * 
	 */
	public QuantityListType getQuantityList() {
		return quantityList;
	}

	public void setOtherAttributes(Map<QName, String> otherAttributes) {
		this.otherAttributes = otherAttributes;
	}

	/**
	 * quantityList 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link QuantityListType }
	 * 
	 */
	public void setQuantityList(QuantityListType value) {
		this.quantityList = value;
	}

	/**
	 * sourceList 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link SourceListType }
	 * 
	 */
	public SourceListType getSourceList() {
		return sourceList;
	}

	/**
	 * sourceList 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link SourceListType }
	 * 
	 */
	public void setSourceList(SourceListType value) {
		this.sourceList = value;
	}

	/**
	 * destinationList 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link DestinationListType }
	 * 
	 */
	public DestinationListType getDestinationList() {
		return destinationList;
	}

	/**
	 * destinationList 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link DestinationListType }
	 * 
	 */
	public void setDestinationList(DestinationListType value) {
		this.destinationList = value;
	}

	/**
	 * ilmd 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link ILMDType }
	 * 
	 */
	public ILMDType getIlmd() {
		return ilmd;
	}

	/**
	 * ilmd 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link ILMDType }
	 * 
	 */
	public void setIlmd(ILMDType value) {
		this.ilmd = value;
	}

	/**
	 * extension 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link ObjectEventExtension2Type }
	 * 
	 */
	public ObjectEventExtension2Type getExtension() {
		return extension;
	}

	/**
	 * extension 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link ObjectEventExtension2Type }
	 * 
	 */
	public void setExtension(ObjectEventExtension2Type value) {
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
