//
// 이 파일은 JAXB(JavaTM Architecture for XML Binding) 참조 구현 2.2.8-b130911.1802 버전을 통해 생성되었습니다. 
// <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>를 참조하십시오. 
// 이 파일을 수정하면 소스 스키마를 재컴파일할 때 수정 사항이 손실됩니다. 
// 생성 날짜: 2016.04.28 시간 03:13:36 PM KST 
//


package org.oliot.model.epcis;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import org.w3c.dom.Element;


/**
 * <p>SubscriptionControls complex type에 대한 Java 클래스입니다.
 * 
 * <p>다음 스키마 단편이 이 클래스에 포함되는 필요한 콘텐츠를 지정합니다.
 * 
 * <pre>
 * &lt;complexType name="SubscriptionControls">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="schedule" type="{urn:epcglobal:epcis-query:xsd:1}QuerySchedule" minOccurs="0"/>
 *         &lt;element name="trigger" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         &lt;element name="initialRecordTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="reportIfEmpty" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="extension" type="{urn:epcglobal:epcis-query:xsd:1}SubscriptionControlsExtensionType" minOccurs="0"/>
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SubscriptionControls", namespace = "urn:epcglobal:epcis-query:xsd:1", propOrder = {
    "schedule",
    "trigger",
    "initialRecordTime",
    "reportIfEmpty",
    "extension",
    "any"
})
public class SubscriptionControls {

    protected QuerySchedule schedule;
    @XmlSchemaType(name = "anyURI")
    protected String trigger;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar initialRecordTime;
    protected boolean reportIfEmpty;
    protected SubscriptionControlsExtensionType extension;
    @XmlAnyElement(lax = true)
    protected List<Object> any;

    /**
     * schedule 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link QuerySchedule }
     *     
     */
    public QuerySchedule getSchedule() {
        return schedule;
    }

    /**
     * schedule 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link QuerySchedule }
     *     
     */
    public void setSchedule(QuerySchedule value) {
        this.schedule = value;
    }

    /**
     * trigger 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTrigger() {
        return trigger;
    }

    /**
     * trigger 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTrigger(String value) {
        this.trigger = value;
    }

    /**
     * initialRecordTime 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getInitialRecordTime() {
        return initialRecordTime;
    }

    /**
     * initialRecordTime 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setInitialRecordTime(XMLGregorianCalendar value) {
        this.initialRecordTime = value;
    }

    /**
     * reportIfEmpty 속성의 값을 가져옵니다.
     * 
     */
    public boolean isReportIfEmpty() {
        return reportIfEmpty;
    }

    /**
     * reportIfEmpty 속성의 값을 설정합니다.
     * 
     */
    public void setReportIfEmpty(boolean value) {
        this.reportIfEmpty = value;
    }

    /**
     * extension 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link SubscriptionControlsExtensionType }
     *     
     */
    public SubscriptionControlsExtensionType getExtension() {
        return extension;
    }

    /**
     * extension 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link SubscriptionControlsExtensionType }
     *     
     */
    public void setExtension(SubscriptionControlsExtensionType value) {
        this.extension = value;
    }

    /**
     * Gets the value of the any property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the any property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAny().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Element }
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

}
