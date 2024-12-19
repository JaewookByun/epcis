//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2024.12.19 at 01:15:43 PM KST 
//


package org.oliot.epcis.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EPCISCaptureJobListType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EPCISCaptureJobListType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="EPCISCaptureJob" type="{urn:epcglobal:epcis:xsd:2}EPCISCaptureJobType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EPCISCaptureJobListType", propOrder = {
    "epcisCaptureJob"
})
public class EPCISCaptureJobListType {

    @XmlElement(name = "EPCISCaptureJob")
    protected List<EPCISCaptureJobType> epcisCaptureJob;

    /**
     * Gets the value of the epcisCaptureJob property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the epcisCaptureJob property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEPCISCaptureJob().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EPCISCaptureJobType }
     * 
     * 
     */
    public List<EPCISCaptureJobType> getEPCISCaptureJob() {
        if (epcisCaptureJob == null) {
            epcisCaptureJob = new ArrayList<EPCISCaptureJobType>();
        }
        return this.epcisCaptureJob;
    }

}
