//
// 이 파일은 JAXB(JavaTM Architecture for XML Binding) 참조 구현 2.2.8-b130911.1802 버전을 통해 생성되었습니다. 
// <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>를 참조하십시오. 
// 이 파일을 수정하면 소스 스키마를 재컴파일할 때 수정 사항이 손실됩니다. 
// 생성 날짜: 2016.04.28 시간 02:25:53 PM KST 
//


package org.oliot.model.epcis;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>CorrelationInformation complex type에 대한 Java 클래스입니다.
 * 
 * <p>다음 스키마 단편이 이 클래스에 포함되는 필요한 콘텐츠를 지정합니다.
 * 
 * <pre>
 * &lt;complexType name="CorrelationInformation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RequestingDocumentCreationDateTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="RequestingDocumentInstanceIdentifier" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ExpectedResponseDateTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CorrelationInformation", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", propOrder = {
    "requestingDocumentCreationDateTime",
    "requestingDocumentInstanceIdentifier",
    "expectedResponseDateTime"
})
public class CorrelationInformation {

    @XmlElement(name = "RequestingDocumentCreationDateTime", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar requestingDocumentCreationDateTime;
    @XmlElement(name = "RequestingDocumentInstanceIdentifier", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader")
    protected String requestingDocumentInstanceIdentifier;
    @XmlElement(name = "ExpectedResponseDateTime", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar expectedResponseDateTime;

    /**
     * requestingDocumentCreationDateTime 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getRequestingDocumentCreationDateTime() {
        return requestingDocumentCreationDateTime;
    }

    /**
     * requestingDocumentCreationDateTime 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setRequestingDocumentCreationDateTime(XMLGregorianCalendar value) {
        this.requestingDocumentCreationDateTime = value;
    }

    /**
     * requestingDocumentInstanceIdentifier 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestingDocumentInstanceIdentifier() {
        return requestingDocumentInstanceIdentifier;
    }

    /**
     * requestingDocumentInstanceIdentifier 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestingDocumentInstanceIdentifier(String value) {
        this.requestingDocumentInstanceIdentifier = value;
    }

    /**
     * expectedResponseDateTime 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getExpectedResponseDateTime() {
        return expectedResponseDateTime;
    }

    /**
     * expectedResponseDateTime 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setExpectedResponseDateTime(XMLGregorianCalendar value) {
        this.expectedResponseDateTime = value;
    }

}
