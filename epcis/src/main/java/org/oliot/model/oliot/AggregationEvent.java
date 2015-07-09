


package org.oliot.model.oliot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;





@Entity
//@PrimaryKeyJoinColumn (name="EPCISEvent_id")
public class AggregationEvent {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected Date eventTime;
    @XmlSchemaType(name = "dateTime")
    protected Date recordTime;
    @XmlElement(required = true)
    protected String eventTimeZoneOffset;
    
    
       
    protected String parentID;
    @OneToOne
	@JoinColumn(name="childEPCs_id")
    //protected EPCList childEPCs;
    protected EPCList childEPCs;
    
    @Enumerated (EnumType.STRING)
    protected Action action;
    
    
    protected String bizStep;
    protected String disposition;
    
    @OneToOne
	@JoinColumn(name="readPoint_id")
    protected ReadPoint readPoint;
    
    @OneToOne
	@JoinColumn(name="bizLocation_id")
    protected BusinessLocation bizLocation;
    
    @OneToOne
	@JoinColumn(name="bizTransactionList_id")
    protected BusinessTransactionList bizTransactionList;
	
    @OneToOne
	@JoinColumn(name="extension_id")
    protected AggregationEventExtension extension;
    
    
    @Transient
    @XmlAnyElement(lax = true)
    protected List<Object> any;

  
   

    public int getId() {
		return id;
	}




	public void setId(int id) {
		this.id = id;
	}




	public Date getEventTime() {
		return eventTime;
	}








	public void setEventTime(Date eventTime) {
		this.eventTime = eventTime;
	}




	public Date getRecordTime() {
		return recordTime;
	}




	public void setRecordTime(Date recordTime) {
		this.recordTime = recordTime;
	}




	public String getEventTimeZoneOffset() {
		return eventTimeZoneOffset;
	}




	public void setEventTimeZoneOffset(String eventTimeZoneOffset) {
		this.eventTimeZoneOffset = eventTimeZoneOffset;
	}




	public String getParentID() {
		return parentID;
	}




	public void setParentID(String parentID) {
		this.parentID = parentID;
	}




	public EPCList getChildEPCs() {
		return childEPCs;
	}




	public void setChildEPCs(EPCList childEPCs) {
		this.childEPCs = childEPCs;
	}




	public Action getAction() {
		return action;
	}




	public void setAction(Action action) {
		this.action = action;
	}




	public String getBizStep() {
		return bizStep;
	}




	public void setBizStep(String bizStep) {
		this.bizStep = bizStep;
	}




	public String getDisposition() {
		return disposition;
	}




	public void setDisposition(String disposition) {
		this.disposition = disposition;
	}




	public ReadPoint getReadPoint() {
		return readPoint;
	}




	public void setReadPoint(ReadPoint readPoint) {
		this.readPoint = readPoint;
	}




	public BusinessLocation getBizLocation() {
		return bizLocation;
	}




	public void setBizLocation(BusinessLocation bizLocation) {
		this.bizLocation = bizLocation;
	}




	public BusinessTransactionList getBizTransactionList() {
		return bizTransactionList;
	}




	public void setBizTransactionList(BusinessTransactionList bizTransactionList) {
		this.bizTransactionList = bizTransactionList;
	}




	





	public AggregationEventExtension getExtension() {
		return extension;
	}




	public void setExtension(AggregationEventExtension extension) {
		this.extension = extension;
	}




	public void setAny(List<Object> any) {
		this.any = any;
	}




	public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }



	

}
