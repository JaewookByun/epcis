


package org.oliot.model.oliot;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.namespace.QName;


@Entity
public class ObjectEventExtension {

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	
	@OneToOne
	@JoinColumn(name="quantityList_id")
    protected QuantityList quantityList;
	@OneToOne
	@JoinColumn(name="sourceList_id")
    protected SourceList sourceList;
	@OneToOne
	@JoinColumn(name="destinationList_id")
    protected DestinationList destinationList;
	
	@OneToOne
	@JoinColumn(name="ilmd_id")
    protected ILMD ilmd;
	@OneToOne
	@JoinColumn(name="objectEventExtension2_id")
    protected ObjectEventExtension2 extension;
	@Transient
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

	
	
	
	
    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	

	public void setOtherAttributes(Map<QName, String> otherAttributes) {
		this.otherAttributes = otherAttributes;
	}

	/**
     * Gets the value of the quantityList property.
     * 
     * @return
     *     possible object is
     *     {@link QuantityList }
     *     
     */
    public QuantityList getQuantityList() {
        return quantityList;
    }

    /**
     * Sets the value of the quantityList property.
     * 
     * @param value
     *     allowed object is
     *     {@link QuantityList }
     *     
     */
    public void setQuantityList(QuantityList value) {
        this.quantityList = value;
    }

    /**
     * Gets the value of the sourceList property.
     * 
     * @return
     *     possible object is
     *     {@link SourceList }
     *     
     */
    public SourceList getSourceList() {
        return sourceList;
    }

    /**
     * Sets the value of the sourceList property.
     * 
     * @param value
     *     allowed object is
     *     {@link SourceList }
     *     
     */
    public void setSourceList(SourceList value) {
        this.sourceList = value;
    }

    /**
     * Gets the value of the destinationList property.
     * 
     * @return
     *     possible object is
     *     {@link DestinationList }
     *     
     */
    public DestinationList getDestinationList() {
        return destinationList;
    }

    /**
     * Sets the value of the destinationList property.
     * 
     * @param value
     *     allowed object is
     *     {@link DestinationList }
     *     
     */
    public void setDestinationList(DestinationList value) {
        this.destinationList = value;
    }

    /**
     * Gets the value of the ilmd property.
     * 
     * @return
     *     possible object is
     *     {@link ILMD }
     *     
     */
    public ILMD getIlmd() {
        return ilmd;
    }

    /**
     * Sets the value of the ilmd property.
     * 
     * @param value
     *     allowed object is
     *     {@link ILMD }
     *     
     */
    public void setIlmd(ILMD value) {
        this.ilmd = value;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link ObjectEventExtension2 }
     *     
     */
    public ObjectEventExtension2 getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link ObjectEventExtension2 }
     *     
     */
    public void setExtension(ObjectEventExtension2 value) {
        this.extension = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     * 
     * <p>
     * the map is keyed by the name of the attribute and 
     * the value is the string value of the attribute.
     * 
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     * 
     * 
     * @return
     *     always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

}
