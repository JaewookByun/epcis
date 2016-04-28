//
// 이 파일은 JAXB(JavaTM Architecture for XML Binding) 참조 구현 2.2.8-b130911.1802 버전을 통해 생성되었습니다. 
// <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>를 참조하십시오. 
// 이 파일을 수정하면 소스 스키마를 재컴파일할 때 수정 사항이 손실됩니다. 
// 생성 날짜: 2016.04.28 시간 02:25:53 PM KST 
//

package org.oliot.model.epcis;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.w3c.dom.Element;

/**
 * 
 * Transaction Event describes the association or disassociation of physical
 * objects to one or more business transactions.
 * 
 * 
 * <p>
 * TransactionEventType complex type에 대한 Java 클래스입니다.
 * 
 * <p>
 * 다음 스키마 단편이 이 클래스에 포함되는 필요한 콘텐츠를 지정합니다.
 * 
 * <pre>
 * &lt;complexType name="TransactionEventType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:epcglobal:epcis:xsd:1}EPCISEventType">
 *       &lt;sequence>
 *         &lt;element name="bizTransactionList" type="{urn:epcglobal:epcis:xsd:1}BusinessTransactionListType"/>
 *         &lt;element name="parentID" type="{urn:epcglobal:epcis:xsd:1}ParentIDType" minOccurs="0"/>
 *         &lt;element name="epcList" type="{urn:epcglobal:epcis:xsd:1}EPCListType"/>
 *         &lt;element name="action" type="{urn:epcglobal:epcis:xsd:1}ActionType"/>
 *         &lt;element name="bizStep" type="{urn:epcglobal:epcis:xsd:1}BusinessStepIDType" minOccurs="0"/>
 *         &lt;element name="disposition" type="{urn:epcglobal:epcis:xsd:1}DispositionIDType" minOccurs="0"/>
 *         &lt;element name="readPoint" type="{urn:epcglobal:epcis:xsd:1}ReadPointType" minOccurs="0"/>
 *         &lt;element name="bizLocation" type="{urn:epcglobal:epcis:xsd:1}BusinessLocationType" minOccurs="0"/>
 *         &lt;element name="extension" type="{urn:epcglobal:epcis:xsd:1}TransactionEventExtensionType" minOccurs="0"/>
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
@XmlType(name = "TransactionEventType", propOrder = { "bizTransactionList", "parentID", "epcList", "action", "bizStep",
		"disposition", "readPoint", "bizLocation", "extension", "any" })
public class TransactionEventType extends EPCISEventType {

	public void setAny(List<Object> any) {
		this.any = any;
	}

	@XmlElement(required = true)
	protected BusinessTransactionListType bizTransactionList;
	@XmlSchemaType(name = "anyURI")
	protected String parentID;
	@XmlElement(required = true)
	protected EPCListType epcList;
	@XmlElement(required = true)
	@XmlSchemaType(name = "string")
	protected ActionType action;
	@XmlSchemaType(name = "anyURI")
	protected String bizStep;
	@XmlSchemaType(name = "anyURI")
	protected String disposition;
	protected ReadPointType readPoint;
	protected BusinessLocationType bizLocation;
	protected TransactionEventExtensionType extension;
	@XmlAnyElement(lax = true)
	protected List<Object> any;

	/**
	 * bizTransactionList 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link BusinessTransactionListType }
	 * 
	 */
	public BusinessTransactionListType getBizTransactionList() {
		return bizTransactionList;
	}

	/**
	 * bizTransactionList 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link BusinessTransactionListType }
	 * 
	 */
	public void setBizTransactionList(BusinessTransactionListType value) {
		this.bizTransactionList = value;
	}

	/**
	 * parentID 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getParentID() {
		return parentID;
	}

	/**
	 * parentID 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setParentID(String value) {
		this.parentID = value;
	}

	/**
	 * epcList 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link EPCListType }
	 * 
	 */
	public EPCListType getEpcList() {
		return epcList;
	}

	/**
	 * epcList 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link EPCListType }
	 * 
	 */
	public void setEpcList(EPCListType value) {
		this.epcList = value;
	}

	/**
	 * action 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link ActionType }
	 * 
	 */
	public ActionType getAction() {
		return action;
	}

	/**
	 * action 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link ActionType }
	 * 
	 */
	public void setAction(ActionType value) {
		this.action = value;
	}

	/**
	 * bizStep 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getBizStep() {
		return bizStep;
	}

	/**
	 * bizStep 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setBizStep(String value) {
		this.bizStep = value;
	}

	/**
	 * disposition 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getDisposition() {
		return disposition;
	}

	/**
	 * disposition 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setDisposition(String value) {
		this.disposition = value;
	}

	/**
	 * readPoint 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link ReadPointType }
	 * 
	 */
	public ReadPointType getReadPoint() {
		return readPoint;
	}

	/**
	 * readPoint 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link ReadPointType }
	 * 
	 */
	public void setReadPoint(ReadPointType value) {
		this.readPoint = value;
	}

	/**
	 * bizLocation 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link BusinessLocationType }
	 * 
	 */
	public BusinessLocationType getBizLocation() {
		return bizLocation;
	}

	/**
	 * bizLocation 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link BusinessLocationType }
	 * 
	 */
	public void setBizLocation(BusinessLocationType value) {
		this.bizLocation = value;
	}

	/**
	 * extension 속성의 값을 가져옵니다.
	 * 
	 * @return possible object is {@link TransactionEventExtensionType }
	 * 
	 */
	public TransactionEventExtensionType getExtension() {
		return extension;
	}

	/**
	 * extension 속성의 값을 설정합니다.
	 * 
	 * @param value
	 *            allowed object is {@link TransactionEventExtensionType }
	 * 
	 */
	public void setExtension(TransactionEventExtensionType value) {
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

}
