package org.oliot.model.oliot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;

public class ObjectEventT {
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
    
   // @OneToOne
	//@JoinColumn(name="baseExtension_id")
   // protected EPCISEventExtension baseExtension;
    //===========================================================EPCISEventExtension
	protected String eventID;
	
    //@OneToOne
	//@JoinColumn(name="errorDeclaration_id")
	//protected ErrorDeclaration errorDeclaration;
  //=====================ErrorDeclaration
    
	protected Date declarationTime;
	
	protected String reason;
	
    @OneToOne
	@JoinColumn(name="correctiveEventIDs_id")
	protected CorrectiveEventIDs correctiveEventIDs;
	
    @OneToOne
	@JoinColumn(name="ErrorDeclarationExtension_id")
	protected ErrorDeclarationExtension extension;
    
    @OneToOne
    @JoinColumn(name="baseExtensionMaps_id")
    protected ExtensionMaps errorDeclarationextensionMaps;
  //=====================ErrorDeclaration
	
	//@OneToOne
	//@JoinColumn(name="EPCISEventExtension2_id")
	//protected EPCISEventExtension2 epcisEventExtension2;
//===============================================================EPCISEventExtension	
	@OneToOne
	@JoinColumn(name="epcList_id")
	protected EPCList epcList;
	
	@Enumerated (EnumType.STRING)
	protected Action action;
	
	protected String bizStep;
	protected String disposition;
	
   //@OneToOne
	//@JoinColumn(name="readPoint_id")
	//protected ReadPoint readPoint;
    //===================================================ReadPoint
    protected String readPointId;
    
    @OneToOne
	@JoinColumn(name="readPointExtension_id")
    protected ReadPointExtension readPointExtension;
	//===================================================ReadPoint
    //@OneToOne
	//@JoinColumn(name="bizLocation_id")
	//protected BusinessLocation bizLocation;
  //===================================================BusinessLocation
    protected String bizLocationId;
	@OneToOne
	@JoinColumn(name="extension_id")
    protected BusinessLocationExtension businessLocationExtension;
	//===================================================BusinessLocation
    
    //@OneToOne
	//@JoinColumn(name="bizTransactionList_id")
	//protected BusinessTransactionList bizTransactionList;
	//===================================================BusinessTransactionList
	@OneToMany
    @XmlElement(required = true)
    protected List<BusinessTransaction> bizTransaction =new ArrayList<BusinessTransaction>();
	//===================================================BusinessTransactionList
    
    //@OneToOne
	//@JoinColumn(name="ilmd_id")
	//protected ILMD ilmd;
	//===================================================ILMD
    @OneToOne
    @JoinColumn(name="baseExtensionMaps_id")
    protected ExtensionMaps ilmdextensionMaps;
	
	@OneToOne
	@JoinColumn(name="iLMD_id")
    protected ILMDExtension ilmdExtension;
	//===================================================ILMD

    
    //@OneToOne
    //@JoinColumn(name="objectEventExtension_id")
	//protected ObjectEventExtension extension;
	//===================================================ObjectEventExtension
	//@OneToOne
	//@JoinColumn(name="quantityList_id")
    //protected QuantityList quantityList;
	//=====================QuantityList
	@OneToMany
    protected List<QuantityElement> quantityElement=new ArrayList<QuantityElement>();
	//=====================QuantityList
	//@OneToOne
	//@JoinColumn(name="sourceList_id")
    //protected SourceList sourceList;
	//=====================SourceList
	@OneToMany
    protected List<SourceDest> source =new ArrayList<SourceDest>();
	//=====================SourceList
	//@OneToOne
	//@JoinColumn(name="destinationList_id")
    //protected DestinationList destinationList;
	//=====================DestinationList
	@OneToMany
    protected List<SourceDest> destination =new ArrayList<SourceDest>();
	//=====================DestinationList
	
	@OneToOne
	@JoinColumn(name="ilmd_id")
    protected ILMD ilmd;
	
	
	//@OneToOne
	//@JoinColumn(name="objectEventExtension2_id")
    //protected ObjectEventExtension2 objectEventExtension2;
	//===================================================ObjectEventExtension
	
    @OneToOne
    @JoinColumn(name="baseExtensionMaps_id")
    protected ExtensionMaps extensionMaps;
    

}
