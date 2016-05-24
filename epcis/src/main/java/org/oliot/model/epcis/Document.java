//
// 이 파일은 JAXB(JavaTM Architecture for XML Binding) 참조 구현 2.2.8-b130911.1802 버전을 통해 생성되었습니다. 
// <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>를 참조하십시오. 
// 이 파일을 수정하면 소스 스키마를 재컴파일할 때 수정 사항이 손실됩니다. 
// 생성 날짜: 2016.04.29 시간 09:20:38 AM KST 
//

package org.oliot.model.epcis;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * 
 * EPCglobal document properties for all messages.
 * 
 * 
 * <p>
 * Document complex type에 대한 Java 클래스입니다.
 * 
 * <p>
 * 다음 스키마 단편이 이 클래스에 포함되는 필요한 콘텐츠를 지정합니다.
 * 
 * <pre>
 * &lt;complexType name="Document">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="schemaVersion" use="required" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *       &lt;attribute name="creationDate" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Document", namespace = "urn:epcglobal:xsd:1")
@XmlSeeAlso({ EPCISQueryDocumentType.class, EPCISDocumentType.class })
public abstract class Document {

	@XmlAttribute(name = "schemaVersion", required = true)
	protected BigDecimal schemaVersion;
	@XmlAttribute(name = "creationDate", required = true)
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar creationDate;

	/**
	 * schemaVersion 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link BigDecimal }
	 * 
	 */
	public BigDecimal getSchemaVersion() {
		return schemaVersion;
	}

	/**
	 * schemaVersion 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link BigDecimal }
	 * 
	 */
	public void setSchemaVersion(BigDecimal value) {
		this.schemaVersion = value;
	}

	/**
	 * creationDate 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getCreationDate() {
		return creationDate;
	}

	/**
	 * creationDate 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setCreationDate(XMLGregorianCalendar value) {
		this.creationDate = value;
	}

}
