//
// 이 파일은 JAXB(JavaTM Architecture for XML Binding) 참조 구현 2.2.8-b130911.1802 버전을 통해 생성되었습니다. 
// <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>를 참조하십시오. 
// 이 파일을 수정하면 소스 스키마를 재컴파일할 때 수정 사항이 손실됩니다. 
// 생성 날짜: 2016.04.29 시간 09:20:38 AM KST 
//

package org.oliot.model.epcis;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * EPCISMasterDataType complex type에 대한 Java 클래스입니다.
 * 
 * <p>
 * 다음 스키마 단편이 이 클래스에 포함되는 필요한 콘텐츠를 지정합니다.
 * 
 * <pre>
 * &lt;complexType name="EPCISMasterDataType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="VocabularyList" type="{urn:epcglobal:epcis:xsd:1}VocabularyListType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EPCISMasterDataType", propOrder = { "vocabularyList" })
public class EPCISMasterDataType {

	@XmlElement(name = "VocabularyList", required = true)
	protected VocabularyListType vocabularyList;

	/**
	 * vocabularyList 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link VocabularyListType }
	 * 
	 */
	public VocabularyListType getVocabularyList() {
		return vocabularyList;
	}

	/**
	 * vocabularyList 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link VocabularyListType }
	 * 
	 */
	public void setVocabularyList(VocabularyListType value) {
		this.vocabularyList = value;
	}

}
