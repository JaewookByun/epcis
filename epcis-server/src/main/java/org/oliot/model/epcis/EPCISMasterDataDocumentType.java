//
// 이 파일은 JAXB(JavaTM Architecture for XML Binding) 참조 구현 2.2.8-b130911.1802 버전을 통해 생성되었습니다. 
// <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>를 참조하십시오. 
// 이 파일을 수정하면 소스 스키마를 재컴파일할 때 수정 사항이 손실됩니다. 
// 생성 날짜: 2016.04.29 시간 09:20:25 AM KST 
//

package org.oliot.model.epcis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;

/**
 * 
 * MasterData document that contains a Header and a Body.
 * 
 * 
 * <p>
 * EPCISMasterDataDocumentType complex type에 대한 Java 클래스입니다.
 * 
 * <p>
 * 다음 스키마 단편이 이 클래스에 포함되는 필요한 콘텐츠를 지정합니다.
 * 
 * <pre>
 * &lt;complexType name="EPCISMasterDataDocumentType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:epcglobal:xsd:1}Document">
 *       &lt;sequence>
 *         &lt;element name="EPCISHeader" type="{urn:epcglobal:epcis:xsd:1}EPCISHeaderType" minOccurs="0"/>
 *         &lt;element name="EPCISBody" type="{urn:epcglobal:epcis-masterdata:xsd:1}EPCISMasterDataBodyType"/>
 *         &lt;element name="extension" type="{urn:epcglobal:epcis-masterdata:xsd:1}EPCISMasterDataDocumentExtensionType" minOccurs="0"/>
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
@XmlType(name = "EPCISMasterDataDocumentType", namespace = "urn:epcglobal:epcis-masterdata:xsd:1", propOrder = {
		"epcisHeader", "epcisBody", "extension", "any" })
public class EPCISMasterDataDocumentType extends Document {

	@XmlElement(name = "EPCISHeader")
	protected EPCISHeaderType epcisHeader;
	@XmlElement(name = "EPCISBody", required = true)
	protected EPCISMasterDataBodyType epcisBody;
	protected EPCISMasterDataDocumentExtensionType extension;
	@XmlAnyElement(lax = true)
	protected List<Object> any;
	@XmlAnyAttribute
	private Map<QName, String> otherAttributes = new HashMap<QName, String>();

	/**
	 * epcisHeader 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link EPCISHeaderType }
	 * 
	 */
	public EPCISHeaderType getEPCISHeader() {
		return epcisHeader;
	}

	public EPCISHeaderType getEpcisHeader() {
		return epcisHeader;
	}

	public void setEpcisHeader(EPCISHeaderType epcisHeader) {
		this.epcisHeader = epcisHeader;
	}

	public EPCISMasterDataBodyType getEpcisBody() {
		return epcisBody;
	}

	public void setEpcisBody(EPCISMasterDataBodyType epcisBody) {
		this.epcisBody = epcisBody;
	}

	public void setAny(List<Object> any) {
		this.any = any;
	}

	public void setOtherAttributes(Map<QName, String> otherAttributes) {
		this.otherAttributes = otherAttributes;
	}

	/**
	 * epcisHeader 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link EPCISHeaderType }
	 * 
	 */
	public void setEPCISHeader(EPCISHeaderType value) {
		this.epcisHeader = value;
	}

	/**
	 * epcisBody 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link EPCISMasterDataBodyType }
	 * 
	 */
	public EPCISMasterDataBodyType getEPCISBody() {
		return epcisBody;
	}

	/**
	 * epcisBody 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link EPCISMasterDataBodyType }
	 * 
	 */
	public void setEPCISBody(EPCISMasterDataBodyType value) {
		this.epcisBody = value;
	}

	/**
	 * extension 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link EPCISMasterDataDocumentExtensionType }
	 * 
	 */
	public EPCISMasterDataDocumentExtensionType getExtension() {
		return extension;
	}

	/**
	 * extension 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link EPCISMasterDataDocumentExtensionType
	 *            }
	 * 
	 */
	public void setExtension(EPCISMasterDataDocumentExtensionType value) {
		this.extension = value;
	}

	/**
	 * Gets the value of the any property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the any property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getAny().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link Element }
	 * {@link Object }
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
