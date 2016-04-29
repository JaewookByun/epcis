//
// 이 파일은 JAXB(JavaTM Architecture for XML Binding) 참조 구현 2.2.8-b130911.1802 버전을 통해 생성되었습니다. 
// <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>를 참조하십시오. 
// 이 파일을 수정하면 소스 스키마를 재컴파일할 때 수정 사항이 손실됩니다. 
// 생성 날짜: 2016.04.29 시간 09:20:38 AM KST 
//

package org.oliot.model.epcis;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * StandardBusinessDocumentHeader complex type에 대한 Java 클래스입니다.
 * 
 * <p>
 * 다음 스키마 단편이 이 클래스에 포함되는 필요한 콘텐츠를 지정합니다.
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

	public void setSender(List<Partner> sender) {
		this.sender = sender;
	}

	public void setReceiver(List<Partner> receiver) {
		this.receiver = receiver;
	}

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
	 * headerVersion 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getHeaderVersion() {
		return headerVersion;
	}

	/**
	 * headerVersion 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setHeaderVersion(String value) {
		this.headerVersion = value;
	}

	/**
	 * Gets the value of the sender property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the sender property.
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
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the receiver property.
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
	 * documentIdentification 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link DocumentIdentification }
	 * 
	 */
	public DocumentIdentification getDocumentIdentification() {
		return documentIdentification;
	}

	/**
	 * documentIdentification 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link DocumentIdentification }
	 * 
	 */
	public void setDocumentIdentification(DocumentIdentification value) {
		this.documentIdentification = value;
	}

	/**
	 * manifest 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link Manifest }
	 * 
	 */
	public Manifest getManifest() {
		return manifest;
	}

	/**
	 * manifest 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link Manifest }
	 * 
	 */
	public void setManifest(Manifest value) {
		this.manifest = value;
	}

	/**
	 * businessScope 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link BusinessScope }
	 * 
	 */
	public BusinessScope getBusinessScope() {
		return businessScope;
	}

	/**
	 * businessScope 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link BusinessScope }
	 * 
	 */
	public void setBusinessScope(BusinessScope value) {
		this.businessScope = value;
	}

}
