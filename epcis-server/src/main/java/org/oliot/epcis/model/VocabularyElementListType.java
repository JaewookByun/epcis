//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.01.05 at 11:33:26 AM KST 
//

package org.oliot.epcis.model;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for VocabularyElementListType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="VocabularyElementListType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="VocabularyElement" type="{urn:epcglobal:epcis:xsd:2}VocabularyElementType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VocabularyElementListType", propOrder = { "vocabularyElement" })
public class VocabularyElementListType {

	@XmlElement(name = "VocabularyElement", required = true)
	protected List<VocabularyElementType> vocabularyElement;

	public void setVocabularyElement(List<VocabularyElementType> vocabularyElement) {
		this.vocabularyElement = vocabularyElement;
	}

	/**
	 * Gets the value of the vocabularyElement property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot.
	 * Therefore any modification you make to the returned list will be present
	 * inside the JAXB object. This is why there is not a <CODE>set</CODE> method
	 * for the vocabularyElement property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getVocabularyElement().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link VocabularyElementType }
	 * 
	 * 
	 */
	public List<VocabularyElementType> getVocabularyElement() {
		if (vocabularyElement == null) {
			vocabularyElement = new ArrayList<VocabularyElementType>();
		}
		return this.vocabularyElement;
	}

}
