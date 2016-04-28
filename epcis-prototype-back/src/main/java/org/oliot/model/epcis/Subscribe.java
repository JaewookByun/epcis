//
// 이 파일은 JAXB(JavaTM Architecture for XML Binding) 참조 구현 2.2.8-b130911.1802 버전을 통해 생성되었습니다. 
// <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>를 참조하십시오. 
// 이 파일을 수정하면 소스 스키마를 재컴파일할 때 수정 사항이 손실됩니다. 
// 생성 날짜: 2016.04.28 시간 03:13:36 PM KST 
//


package org.oliot.model.epcis;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Subscribe complex type에 대한 Java 클래스입니다.
 * 
 * <p>다음 스키마 단편이 이 클래스에 포함되는 필요한 콘텐츠를 지정합니다.
 * 
 * <pre>
 * &lt;complexType name="Subscribe">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="queryName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="params" type="{urn:epcglobal:epcis-query:xsd:1}QueryParams"/>
 *         &lt;element name="dest" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="controls" type="{urn:epcglobal:epcis-query:xsd:1}SubscriptionControls"/>
 *         &lt;element name="subscriptionID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Subscribe", namespace = "urn:epcglobal:epcis-query:xsd:1", propOrder = {
    "queryName",
    "params",
    "dest",
    "controls",
    "subscriptionID"
})
public class Subscribe {

    @XmlElement(required = true)
    protected String queryName;
    @XmlElement(required = true)
    protected QueryParams params;
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String dest;
    @XmlElement(required = true)
    protected SubscriptionControls controls;
    @XmlElement(required = true)
    protected String subscriptionID;

    /**
     * queryName 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQueryName() {
        return queryName;
    }

    /**
     * queryName 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQueryName(String value) {
        this.queryName = value;
    }

    /**
     * params 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link QueryParams }
     *     
     */
    public QueryParams getParams() {
        return params;
    }

    /**
     * params 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link QueryParams }
     *     
     */
    public void setParams(QueryParams value) {
        this.params = value;
    }

    /**
     * dest 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDest() {
        return dest;
    }

    /**
     * dest 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDest(String value) {
        this.dest = value;
    }

    /**
     * controls 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link SubscriptionControls }
     *     
     */
    public SubscriptionControls getControls() {
        return controls;
    }

    /**
     * controls 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link SubscriptionControls }
     *     
     */
    public void setControls(SubscriptionControls value) {
        this.controls = value;
    }

    /**
     * subscriptionID 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubscriptionID() {
        return subscriptionID;
    }

    /**
     * subscriptionID 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubscriptionID(String value) {
        this.subscriptionID = value;
    }

}
