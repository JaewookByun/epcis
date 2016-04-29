//
// 이 파일은 JAXB(JavaTM Architecture for XML Binding) 참조 구현 2.2.8-b130911.1802 버전을 통해 생성되었습니다. 
// <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>를 참조하십시오. 
// 이 파일을 수정하면 소스 스키마를 재컴파일할 때 수정 사항이 손실됩니다. 
// 생성 날짜: 2016.04.29 시간 09:20:38 AM KST 
//

package org.oliot.model.epcis;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Manifest complex type에 대한 Java 클래스입니다.
 * 
 * <p>
 * 다음 스키마 단편이 이 클래스에 포함되는 필요한 콘텐츠를 지정합니다.
 * 
 * <pre>
 * &lt;complexType name="Manifest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="NumberOfItems" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="ManifestItem" type="{http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader}ManifestItem" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Manifest", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", propOrder = {
		"numberOfItems", "manifestItem" })
public class Manifest {

	@XmlElement(name = "NumberOfItems", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", required = true)
	protected BigInteger numberOfItems;
	@XmlElement(name = "ManifestItem", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", required = true)
	protected List<ManifestItem> manifestItem;

	public void setManifestItem(List<ManifestItem> manifestItem) {
		this.manifestItem = manifestItem;
	}

	/**
	 * numberOfItems 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link BigInteger }
	 * 
	 */
	public BigInteger getNumberOfItems() {
		return numberOfItems;
	}

	/**
	 * numberOfItems 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link BigInteger }
	 * 
	 */
	public void setNumberOfItems(BigInteger value) {
		this.numberOfItems = value;
	}

	/**
	 * Gets the value of the manifestItem property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the manifestItem property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getManifestItem().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link ManifestItem }
	 * 
	 * 
	 */
	public List<ManifestItem> getManifestItem() {
		if (manifestItem == null) {
			manifestItem = new ArrayList<ManifestItem>();
		}
		return this.manifestItem;
	}

}
