//
// 이 파일은 JAXB(JavaTM Architecture for XML Binding) 참조 구현 2.2.8-b130911.1802 버전을 통해 생성되었습니다. 
// <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>를 참조하십시오. 
// 이 파일을 수정하면 소스 스키마를 재컴파일할 때 수정 사항이 손실됩니다. 
// 생성 날짜: 2016.04.29 시간 09:20:38 AM KST 
//

package org.oliot.model.epcis;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Scope complex type에 대한 Java 클래스입니다.
 * 
 * <p>
 * 다음 스키마 단편이 이 클래스에 포함되는 필요한 콘텐츠를 지정합니다.
 * 
 * <pre>
 * &lt;complexType name="Scope">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader}ScopeAttributes"/>
 *         &lt;element ref="{http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader}ScopeInformation" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Scope", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", propOrder = {
		"type", "instanceIdentifier", "identifier", "scopeInformation" })
public class Scope {

	public void setScopeInformation(List<JAXBElement<?>> scopeInformation) {
		this.scopeInformation = scopeInformation;
	}

	@XmlElement(name = "Type", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", required = true)
	protected String type;
	@XmlElement(name = "InstanceIdentifier", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", required = true)
	protected String instanceIdentifier;
	@XmlElement(name = "Identifier", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader")
	protected String identifier;
	@XmlElementRef(name = "ScopeInformation", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", type = JAXBElement.class, required = false)
	protected List<JAXBElement<?>> scopeInformation;

	/**
	 * type 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getType() {
		return type;
	}

	/**
	 * type 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setType(String value) {
		this.type = value;
	}

	/**
	 * instanceIdentifier 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getInstanceIdentifier() {
		return instanceIdentifier;
	}

	/**
	 * instanceIdentifier 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setInstanceIdentifier(String value) {
		this.instanceIdentifier = value;
	}

	/**
	 * identifier 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * identifier 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setIdentifier(String value) {
		this.identifier = value;
	}

	/**
	 * Gets the value of the scopeInformation property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the scopeInformation property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getScopeInformation().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link JAXBElement }{@code <}{@link CorrelationInformation }{@code >}
	 * {@link JAXBElement }{@code <}{@link BusinessService }{@code >}
	 * {@link JAXBElement }{@code <}{@link Object }{@code >}
	 * 
	 * 
	 */
	public List<JAXBElement<?>> getScopeInformation() {
		if (scopeInformation == null) {
			scopeInformation = new ArrayList<JAXBElement<?>>();
		}
		return this.scopeInformation;
	}

}
