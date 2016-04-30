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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>EPCISQueryBodyType complex type에 대한 Java 클래스입니다.
 * 
 * <p>다음 스키마 단편이 이 클래스에 포함되는 필요한 콘텐츠를 지정합니다.
 * 
 * <pre>
 * &lt;complexType name="EPCISQueryBodyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element ref="{urn:epcglobal:epcis-query:xsd:1}GetQueryNames"/>
 *         &lt;element ref="{urn:epcglobal:epcis-query:xsd:1}GetQueryNamesResult"/>
 *         &lt;element ref="{urn:epcglobal:epcis-query:xsd:1}Subscribe"/>
 *         &lt;element ref="{urn:epcglobal:epcis-query:xsd:1}SubscribeResult"/>
 *         &lt;element ref="{urn:epcglobal:epcis-query:xsd:1}Unsubscribe"/>
 *         &lt;element ref="{urn:epcglobal:epcis-query:xsd:1}UnsubscribeResult"/>
 *         &lt;element ref="{urn:epcglobal:epcis-query:xsd:1}GetSubscriptionIDs"/>
 *         &lt;element ref="{urn:epcglobal:epcis-query:xsd:1}GetSubscriptionIDsResult"/>
 *         &lt;element ref="{urn:epcglobal:epcis-query:xsd:1}Poll"/>
 *         &lt;element ref="{urn:epcglobal:epcis-query:xsd:1}GetStandardVersion"/>
 *         &lt;element ref="{urn:epcglobal:epcis-query:xsd:1}GetStandardVersionResult"/>
 *         &lt;element ref="{urn:epcglobal:epcis-query:xsd:1}GetVendorVersion"/>
 *         &lt;element ref="{urn:epcglobal:epcis-query:xsd:1}GetVendorVersionResult"/>
 *         &lt;element ref="{urn:epcglobal:epcis-query:xsd:1}DuplicateNameException"/>
 *         &lt;element ref="{urn:epcglobal:epcis-query:xsd:1}InvalidURIException"/>
 *         &lt;element ref="{urn:epcglobal:epcis-query:xsd:1}NoSuchNameException"/>
 *         &lt;element ref="{urn:epcglobal:epcis-query:xsd:1}NoSuchSubscriptionException"/>
 *         &lt;element ref="{urn:epcglobal:epcis-query:xsd:1}DuplicateSubscriptionException"/>
 *         &lt;element ref="{urn:epcglobal:epcis-query:xsd:1}QueryParameterException"/>
 *         &lt;element ref="{urn:epcglobal:epcis-query:xsd:1}QueryTooLargeException"/>
 *         &lt;element ref="{urn:epcglobal:epcis-query:xsd:1}QueryTooComplexException"/>
 *         &lt;element ref="{urn:epcglobal:epcis-query:xsd:1}SubscriptionControlsException"/>
 *         &lt;element ref="{urn:epcglobal:epcis-query:xsd:1}SubscribeNotPermittedException"/>
 *         &lt;element ref="{urn:epcglobal:epcis-query:xsd:1}SecurityException"/>
 *         &lt;element ref="{urn:epcglobal:epcis-query:xsd:1}ValidationException"/>
 *         &lt;element ref="{urn:epcglobal:epcis-query:xsd:1}ImplementationException"/>
 *         &lt;element ref="{urn:epcglobal:epcis-query:xsd:1}QueryResults"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EPCISQueryBodyType", namespace = "urn:epcglobal:epcis-query:xsd:1", propOrder = {
    "getQueryNames",
    "getQueryNamesResult",
    "subscribe",
    "subscribeResult",
    "unsubscribe",
    "unsubscribeResult",
    "getSubscriptionIDs",
    "getSubscriptionIDsResult",
    "poll",
    "getStandardVersion",
    "getStandardVersionResult",
    "getVendorVersion",
    "getVendorVersionResult",
    "duplicateNameException",
    "invalidURIException",
    "noSuchNameException",
    "noSuchSubscriptionException",
    "duplicateSubscriptionException",
    "queryParameterException",
    "queryTooLargeException",
    "queryTooComplexException",
    "subscriptionControlsException",
    "subscribeNotPermittedException",
    "securityException",
    "validationException",
    "implementationException",
    "queryResults"
})
public class EPCISQueryBodyType {

    @XmlElement(name = "GetQueryNames", namespace = "urn:epcglobal:epcis-query:xsd:1")
    protected EmptyParms getQueryNames;
    @XmlElement(name = "GetQueryNamesResult", namespace = "urn:epcglobal:epcis-query:xsd:1")
    protected ArrayOfString getQueryNamesResult;
    @XmlElement(name = "Subscribe", namespace = "urn:epcglobal:epcis-query:xsd:1")
    protected Subscribe subscribe;
    @XmlElement(name = "SubscribeResult", namespace = "urn:epcglobal:epcis-query:xsd:1")
    protected VoidHolder subscribeResult;
    @XmlElement(name = "Unsubscribe", namespace = "urn:epcglobal:epcis-query:xsd:1")
    protected Unsubscribe unsubscribe;
    @XmlElement(name = "UnsubscribeResult", namespace = "urn:epcglobal:epcis-query:xsd:1")
    protected VoidHolder unsubscribeResult;
    @XmlElement(name = "GetSubscriptionIDs", namespace = "urn:epcglobal:epcis-query:xsd:1")
    protected GetSubscriptionIDs getSubscriptionIDs;
    @XmlElement(name = "GetSubscriptionIDsResult", namespace = "urn:epcglobal:epcis-query:xsd:1")
    protected ArrayOfString getSubscriptionIDsResult;
    @XmlElement(name = "Poll", namespace = "urn:epcglobal:epcis-query:xsd:1")
    protected Poll poll;
    @XmlElement(name = "GetStandardVersion", namespace = "urn:epcglobal:epcis-query:xsd:1")
    protected EmptyParms getStandardVersion;
    @XmlElement(name = "GetStandardVersionResult", namespace = "urn:epcglobal:epcis-query:xsd:1")
    protected String getStandardVersionResult;
    @XmlElement(name = "GetVendorVersion", namespace = "urn:epcglobal:epcis-query:xsd:1")
    protected EmptyParms getVendorVersion;
    @XmlElement(name = "GetVendorVersionResult", namespace = "urn:epcglobal:epcis-query:xsd:1")
    protected String getVendorVersionResult;
    @XmlElement(name = "DuplicateNameException", namespace = "urn:epcglobal:epcis-query:xsd:1")
    protected DuplicateNameException duplicateNameException;
    @XmlElement(name = "InvalidURIException", namespace = "urn:epcglobal:epcis-query:xsd:1")
    protected InvalidURIException invalidURIException;
    @XmlElement(name = "NoSuchNameException", namespace = "urn:epcglobal:epcis-query:xsd:1")
    protected NoSuchNameException noSuchNameException;
    @XmlElement(name = "NoSuchSubscriptionException", namespace = "urn:epcglobal:epcis-query:xsd:1")
    protected NoSuchSubscriptionException noSuchSubscriptionException;
    @XmlElement(name = "DuplicateSubscriptionException", namespace = "urn:epcglobal:epcis-query:xsd:1")
    protected DuplicateSubscriptionException duplicateSubscriptionException;
    @XmlElement(name = "QueryParameterException", namespace = "urn:epcglobal:epcis-query:xsd:1")
    protected QueryParameterException queryParameterException;
    @XmlElement(name = "QueryTooLargeException", namespace = "urn:epcglobal:epcis-query:xsd:1")
    protected QueryTooLargeException queryTooLargeException;
    @XmlElement(name = "QueryTooComplexException", namespace = "urn:epcglobal:epcis-query:xsd:1")
    protected QueryTooComplexException queryTooComplexException;
    @XmlElement(name = "SubscriptionControlsException", namespace = "urn:epcglobal:epcis-query:xsd:1")
    protected SubscriptionControlsException subscriptionControlsException;
    @XmlElement(name = "SubscribeNotPermittedException", namespace = "urn:epcglobal:epcis-query:xsd:1")
    protected SubscribeNotPermittedException subscribeNotPermittedException;
    @XmlElement(name = "SecurityException", namespace = "urn:epcglobal:epcis-query:xsd:1")
    protected SecurityException securityException;
    @XmlElement(name = "ValidationException", namespace = "urn:epcglobal:epcis-query:xsd:1")
    protected ValidationException validationException;
    @XmlElement(name = "ImplementationException", namespace = "urn:epcglobal:epcis-query:xsd:1")
    protected ImplementationException implementationException;
    @XmlElement(name = "QueryResults", namespace = "urn:epcglobal:epcis-query:xsd:1")
    protected QueryResults queryResults;

    /**
     * getQueryNames 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link EmptyParms }
     *     
     */
    public EmptyParms getGetQueryNames() {
        return getQueryNames;
    }

    /**
     * getQueryNames 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link EmptyParms }
     *     
     */
    public void setGetQueryNames(EmptyParms value) {
        this.getQueryNames = value;
    }

    /**
     * getQueryNamesResult 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *     
     */
    public ArrayOfString getGetQueryNamesResult() {
        return getQueryNamesResult;
    }

    /**
     * getQueryNamesResult 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *     
     */
    public void setGetQueryNamesResult(ArrayOfString value) {
        this.getQueryNamesResult = value;
    }

    /**
     * subscribe 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link Subscribe }
     *     
     */
    public Subscribe getSubscribe() {
        return subscribe;
    }

    /**
     * subscribe 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link Subscribe }
     *     
     */
    public void setSubscribe(Subscribe value) {
        this.subscribe = value;
    }

    /**
     * subscribeResult 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link VoidHolder }
     *     
     */
    public VoidHolder getSubscribeResult() {
        return subscribeResult;
    }

    /**
     * subscribeResult 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link VoidHolder }
     *     
     */
    public void setSubscribeResult(VoidHolder value) {
        this.subscribeResult = value;
    }

    /**
     * unsubscribe 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link Unsubscribe }
     *     
     */
    public Unsubscribe getUnsubscribe() {
        return unsubscribe;
    }

    /**
     * unsubscribe 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link Unsubscribe }
     *     
     */
    public void setUnsubscribe(Unsubscribe value) {
        this.unsubscribe = value;
    }

    /**
     * unsubscribeResult 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link VoidHolder }
     *     
     */
    public VoidHolder getUnsubscribeResult() {
        return unsubscribeResult;
    }

    /**
     * unsubscribeResult 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link VoidHolder }
     *     
     */
    public void setUnsubscribeResult(VoidHolder value) {
        this.unsubscribeResult = value;
    }

    /**
     * getSubscriptionIDs 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link GetSubscriptionIDs }
     *     
     */
    public GetSubscriptionIDs getGetSubscriptionIDs() {
        return getSubscriptionIDs;
    }

    /**
     * getSubscriptionIDs 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link GetSubscriptionIDs }
     *     
     */
    public void setGetSubscriptionIDs(GetSubscriptionIDs value) {
        this.getSubscriptionIDs = value;
    }

    /**
     * getSubscriptionIDsResult 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *     
     */
    public ArrayOfString getGetSubscriptionIDsResult() {
        return getSubscriptionIDsResult;
    }

    /**
     * getSubscriptionIDsResult 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *     
     */
    public void setGetSubscriptionIDsResult(ArrayOfString value) {
        this.getSubscriptionIDsResult = value;
    }

    /**
     * poll 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link Poll }
     *     
     */
    public Poll getPoll() {
        return poll;
    }

    /**
     * poll 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link Poll }
     *     
     */
    public void setPoll(Poll value) {
        this.poll = value;
    }

    /**
     * getStandardVersion 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link EmptyParms }
     *     
     */
    public EmptyParms getGetStandardVersion() {
        return getStandardVersion;
    }

    /**
     * getStandardVersion 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link EmptyParms }
     *     
     */
    public void setGetStandardVersion(EmptyParms value) {
        this.getStandardVersion = value;
    }

    /**
     * getStandardVersionResult 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGetStandardVersionResult() {
        return getStandardVersionResult;
    }

    /**
     * getStandardVersionResult 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGetStandardVersionResult(String value) {
        this.getStandardVersionResult = value;
    }

    /**
     * getVendorVersion 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link EmptyParms }
     *     
     */
    public EmptyParms getGetVendorVersion() {
        return getVendorVersion;
    }

    /**
     * getVendorVersion 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link EmptyParms }
     *     
     */
    public void setGetVendorVersion(EmptyParms value) {
        this.getVendorVersion = value;
    }

    /**
     * getVendorVersionResult 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGetVendorVersionResult() {
        return getVendorVersionResult;
    }

    /**
     * getVendorVersionResult 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGetVendorVersionResult(String value) {
        this.getVendorVersionResult = value;
    }

    /**
     * duplicateNameException 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link DuplicateNameException }
     *     
     */
    public DuplicateNameException getDuplicateNameException() {
        return duplicateNameException;
    }

    /**
     * duplicateNameException 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link DuplicateNameException }
     *     
     */
    public void setDuplicateNameException(DuplicateNameException value) {
        this.duplicateNameException = value;
    }

    /**
     * invalidURIException 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link InvalidURIException }
     *     
     */
    public InvalidURIException getInvalidURIException() {
        return invalidURIException;
    }

    /**
     * invalidURIException 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link InvalidURIException }
     *     
     */
    public void setInvalidURIException(InvalidURIException value) {
        this.invalidURIException = value;
    }

    /**
     * noSuchNameException 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link NoSuchNameException }
     *     
     */
    public NoSuchNameException getNoSuchNameException() {
        return noSuchNameException;
    }

    /**
     * noSuchNameException 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link NoSuchNameException }
     *     
     */
    public void setNoSuchNameException(NoSuchNameException value) {
        this.noSuchNameException = value;
    }

    /**
     * noSuchSubscriptionException 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link NoSuchSubscriptionException }
     *     
     */
    public NoSuchSubscriptionException getNoSuchSubscriptionException() {
        return noSuchSubscriptionException;
    }

    /**
     * noSuchSubscriptionException 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link NoSuchSubscriptionException }
     *     
     */
    public void setNoSuchSubscriptionException(NoSuchSubscriptionException value) {
        this.noSuchSubscriptionException = value;
    }

    /**
     * duplicateSubscriptionException 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link DuplicateSubscriptionException }
     *     
     */
    public DuplicateSubscriptionException getDuplicateSubscriptionException() {
        return duplicateSubscriptionException;
    }

    /**
     * duplicateSubscriptionException 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link DuplicateSubscriptionException }
     *     
     */
    public void setDuplicateSubscriptionException(DuplicateSubscriptionException value) {
        this.duplicateSubscriptionException = value;
    }

    /**
     * queryParameterException 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link QueryParameterException }
     *     
     */
    public QueryParameterException getQueryParameterException() {
        return queryParameterException;
    }

    /**
     * queryParameterException 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link QueryParameterException }
     *     
     */
    public void setQueryParameterException(QueryParameterException value) {
        this.queryParameterException = value;
    }

    /**
     * queryTooLargeException 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link QueryTooLargeException }
     *     
     */
    public QueryTooLargeException getQueryTooLargeException() {
        return queryTooLargeException;
    }

    /**
     * queryTooLargeException 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link QueryTooLargeException }
     *     
     */
    public void setQueryTooLargeException(QueryTooLargeException value) {
        this.queryTooLargeException = value;
    }

    /**
     * queryTooComplexException 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link QueryTooComplexException }
     *     
     */
    public QueryTooComplexException getQueryTooComplexException() {
        return queryTooComplexException;
    }

    /**
     * queryTooComplexException 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link QueryTooComplexException }
     *     
     */
    public void setQueryTooComplexException(QueryTooComplexException value) {
        this.queryTooComplexException = value;
    }

    /**
     * subscriptionControlsException 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link SubscriptionControlsException }
     *     
     */
    public SubscriptionControlsException getSubscriptionControlsException() {
        return subscriptionControlsException;
    }

    /**
     * subscriptionControlsException 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link SubscriptionControlsException }
     *     
     */
    public void setSubscriptionControlsException(SubscriptionControlsException value) {
        this.subscriptionControlsException = value;
    }

    /**
     * subscribeNotPermittedException 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link SubscribeNotPermittedException }
     *     
     */
    public SubscribeNotPermittedException getSubscribeNotPermittedException() {
        return subscribeNotPermittedException;
    }

    /**
     * subscribeNotPermittedException 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link SubscribeNotPermittedException }
     *     
     */
    public void setSubscribeNotPermittedException(SubscribeNotPermittedException value) {
        this.subscribeNotPermittedException = value;
    }

    /**
     * securityException 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link SecurityException }
     *     
     */
    public SecurityException getSecurityException() {
        return securityException;
    }

    /**
     * securityException 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link SecurityException }
     *     
     */
    public void setSecurityException(SecurityException value) {
        this.securityException = value;
    }

    /**
     * validationException 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link ValidationException }
     *     
     */
    public ValidationException getValidationException() {
        return validationException;
    }

    /**
     * validationException 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link ValidationException }
     *     
     */
    public void setValidationException(ValidationException value) {
        this.validationException = value;
    }

    /**
     * implementationException 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link ImplementationException }
     *     
     */
    public ImplementationException getImplementationException() {
        return implementationException;
    }

    /**
     * implementationException 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link ImplementationException }
     *     
     */
    public void setImplementationException(ImplementationException value) {
        this.implementationException = value;
    }

    /**
     * queryResults 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link QueryResults }
     *     
     */
    public QueryResults getQueryResults() {
        return queryResults;
    }

    /**
     * queryResults 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link QueryResults }
     *     
     */
    public void setQueryResults(QueryResults value) {
        this.queryResults = value;
    }

}
