//
// 이 파일은 JAXB(JavaTM Architecture for XML Binding) 참조 구현 2.2.8-b130911.1802 버전을 통해 생성되었습니다. 
// <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>를 참조하십시오. 
// 이 파일을 수정하면 소스 스키마를 재컴파일할 때 수정 사항이 손실됩니다. 
// 생성 날짜: 2016.04.29 시간 09:20:38 AM KST 
//

package org.oliot.model.epcis;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * ServiceTransaction complex type에 대한 Java 클래스입니다.
 * 
 * <p>
 * 다음 스키마 단편이 이 클래스에 포함되는 필요한 콘텐츠를 지정합니다.
 * 
 * <pre>
 * &lt;complexType name="ServiceTransaction">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="TypeOfServiceTransaction" type="{http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader}TypeOfServiceTransaction" />
 *       &lt;attribute name="IsNonRepudiationRequired" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="IsAuthenticationRequired" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="IsNonRepudiationOfReceiptRequired" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="IsIntegrityCheckRequired" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="IsApplicationErrorResponseRequested" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="TimeToAcknowledgeReceipt" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="TimeToAcknowledgeAcceptance" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="TimeToPerform" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Recurrence" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceTransaction", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader")
public class ServiceTransaction {

	@XmlAttribute(name = "TypeOfServiceTransaction")
	protected TypeOfServiceTransaction typeOfServiceTransaction;
	@XmlAttribute(name = "IsNonRepudiationRequired")
	protected String isNonRepudiationRequired;
	@XmlAttribute(name = "IsAuthenticationRequired")
	protected String isAuthenticationRequired;
	@XmlAttribute(name = "IsNonRepudiationOfReceiptRequired")
	protected String isNonRepudiationOfReceiptRequired;
	@XmlAttribute(name = "IsIntegrityCheckRequired")
	protected String isIntegrityCheckRequired;
	@XmlAttribute(name = "IsApplicationErrorResponseRequested")
	protected String isApplicationErrorResponseRequested;
	@XmlAttribute(name = "TimeToAcknowledgeReceipt")
	protected String timeToAcknowledgeReceipt;
	@XmlAttribute(name = "TimeToAcknowledgeAcceptance")
	protected String timeToAcknowledgeAcceptance;
	@XmlAttribute(name = "TimeToPerform")
	protected String timeToPerform;
	@XmlAttribute(name = "Recurrence")
	protected String recurrence;

	/**
	 * typeOfServiceTransaction 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link TypeOfServiceTransaction }
	 * 
	 */
	public TypeOfServiceTransaction getTypeOfServiceTransaction() {
		return typeOfServiceTransaction;
	}

	/**
	 * typeOfServiceTransaction 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link TypeOfServiceTransaction }
	 * 
	 */
	public void setTypeOfServiceTransaction(TypeOfServiceTransaction value) {
		this.typeOfServiceTransaction = value;
	}

	/**
	 * isNonRepudiationRequired 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getIsNonRepudiationRequired() {
		return isNonRepudiationRequired;
	}

	/**
	 * isNonRepudiationRequired 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setIsNonRepudiationRequired(String value) {
		this.isNonRepudiationRequired = value;
	}

	/**
	 * isAuthenticationRequired 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getIsAuthenticationRequired() {
		return isAuthenticationRequired;
	}

	/**
	 * isAuthenticationRequired 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setIsAuthenticationRequired(String value) {
		this.isAuthenticationRequired = value;
	}

	/**
	 * isNonRepudiationOfReceiptRequired 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getIsNonRepudiationOfReceiptRequired() {
		return isNonRepudiationOfReceiptRequired;
	}

	/**
	 * isNonRepudiationOfReceiptRequired 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setIsNonRepudiationOfReceiptRequired(String value) {
		this.isNonRepudiationOfReceiptRequired = value;
	}

	/**
	 * isIntegrityCheckRequired 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getIsIntegrityCheckRequired() {
		return isIntegrityCheckRequired;
	}

	/**
	 * isIntegrityCheckRequired 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setIsIntegrityCheckRequired(String value) {
		this.isIntegrityCheckRequired = value;
	}

	/**
	 * isApplicationErrorResponseRequested 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getIsApplicationErrorResponseRequested() {
		return isApplicationErrorResponseRequested;
	}

	/**
	 * isApplicationErrorResponseRequested 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setIsApplicationErrorResponseRequested(String value) {
		this.isApplicationErrorResponseRequested = value;
	}

	/**
	 * timeToAcknowledgeReceipt 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getTimeToAcknowledgeReceipt() {
		return timeToAcknowledgeReceipt;
	}

	/**
	 * timeToAcknowledgeReceipt 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setTimeToAcknowledgeReceipt(String value) {
		this.timeToAcknowledgeReceipt = value;
	}

	/**
	 * timeToAcknowledgeAcceptance 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getTimeToAcknowledgeAcceptance() {
		return timeToAcknowledgeAcceptance;
	}

	/**
	 * timeToAcknowledgeAcceptance 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setTimeToAcknowledgeAcceptance(String value) {
		this.timeToAcknowledgeAcceptance = value;
	}

	/**
	 * timeToPerform 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getTimeToPerform() {
		return timeToPerform;
	}

	/**
	 * timeToPerform 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setTimeToPerform(String value) {
		this.timeToPerform = value;
	}

	/**
	 * recurrence 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getRecurrence() {
		return recurrence;
	}

	/**
	 * recurrence 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setRecurrence(String value) {
		this.recurrence = value;
	}

}
