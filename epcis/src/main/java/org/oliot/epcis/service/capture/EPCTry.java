package org.oliot.epcis.service.capture;

import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.oliot.epcis.serde.mysql.CaptureOperationsBackend;
import org.oliot.model.epcis.ActionType;
import org.oliot.model.epcis.AggregationEventExtension2Type;
import org.oliot.model.epcis.AggregationEventExtensionType;
import org.oliot.model.epcis.AggregationEventType;
import org.oliot.model.epcis.AttributeType;
import org.oliot.model.epcis.BusinessLocationExtensionType;
import org.oliot.model.epcis.BusinessLocationType;
import org.oliot.model.epcis.BusinessTransactionListType;
import org.oliot.model.epcis.BusinessTransactionType;
import org.oliot.model.epcis.DestinationListType;
import org.oliot.model.epcis.EPC;
import org.oliot.model.epcis.EPCListType;
import org.oliot.model.epcis.IDListType;
import org.oliot.model.epcis.ILMDExtensionType;
import org.oliot.model.epcis.ILMDType;
import org.oliot.model.epcis.ObjectEventExtension2Type;
import org.oliot.model.epcis.ObjectEventExtensionType;
import org.oliot.model.epcis.ObjectEventType;
import org.oliot.model.epcis.QuantityElementType;
import org.oliot.model.epcis.QuantityEventExtensionType;
import org.oliot.model.epcis.QuantityEventType;
import org.oliot.model.epcis.QuantityListType;
import org.oliot.model.epcis.ReadPointExtensionType;
import org.oliot.model.epcis.ReadPointType;
import org.oliot.model.epcis.SensingElementType;
import org.oliot.model.epcis.SensingListType;
import org.oliot.model.epcis.SensorEventExtensionType;
import org.oliot.model.epcis.SensorEventType;
import org.oliot.model.epcis.SourceDestType;
import org.oliot.model.epcis.SourceListType;
import org.oliot.model.epcis.TransactionEventExtension2Type;
import org.oliot.model.epcis.TransactionEventExtensionType;
import org.oliot.model.epcis.TransactionEventType;
import org.oliot.model.epcis.TransformationEventExtensionType;
import org.oliot.model.epcis.TransformationEventType;
import org.oliot.model.epcis.VocabularyElementExtensionType;
import org.oliot.model.epcis.VocabularyElementListType;
import org.oliot.model.epcis.VocabularyElementType;
import org.oliot.model.epcis.VocabularyExtensionType;
import org.oliot.model.epcis.VocabularyType;
import org.oliot.model.oliot.TransactionEventExtension2;
import org.oliot.model.oliot.TransformationEventExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class EPCTry {

	public static void main(String[] args) {
		System.out.println("it is working");
		
		
		ApplicationContext ctx=new ClassPathXmlApplicationContext("MysqlConfig.xml");
		CaptureOperationsBackend mysqlOperationdao=ctx.getBean
				("captureOperationsBackend", CaptureOperationsBackend.class);
		
		
		//EPCISEventType ePCISEvent=new EPCISEventType();
		
		
		
		//GregorianCalendar eventTime = new GregorianCalendar();
		//GregorianCalendar recordTime = new GregorianCalendar();
	   
	  //  protected String eventTimeZoneOffset;
	    //protected EPCISEventExtensionType baseExtension;
		
		//EPC epc=new EPC();
		//epc.setValue("epc1");
		
		//long recordTimeMilis = recordTime.getTimeInMillis();
		
	
		
		
		
		

		//mysqlOperationdao.save(getAggregationEventTypeSample());
		
		//mysqlOperationdao.save(getObjectEventTypeSample());
		
		//mysqlOperationdao.save(getTransactionEventTypeSample());
		//mysqlOperationdao.save(getQuantityEventTypeSample());
		//mysqlOperationdao.save(getTransformationEventTypeSample());
		//mysqlOperationdao.save(getSensorEventTypeSample());
		mysqlOperationdao.save(getVocabularyTypeSample());
		
		
	}
//create table EPC ( id bigint NOT NULL AUTO_INCREMENT, 	 value varchar(50),  primary key (id) );
	
	public static 	TransactionEventType getTransactionEventTypeSample(){
		
		TransactionEventType transactionEventType=new TransactionEventType();
		
		GregorianCalendar gRecordTime = new GregorianCalendar();
		XMLGregorianCalendar recordTime;
		try {
			recordTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(gRecordTime);
			transactionEventType.setEventTime(recordTime);
			transactionEventType.setRecordTime(recordTime);
		} catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		transactionEventType.setEventTimeZoneOffset("eventTimeZoneOffset_T");
		transactionEventType.setParentID("parentID_T");
		
		EPCListType transactionEventEPCs=new EPCListType();
		
		
		org.oliot.model.epcis.EPC epc1=new org.oliot.model.epcis.EPC ("epc1_T");
		
		org.oliot.model.epcis.EPC  epc2=new org.oliot.model.epcis.EPC ("epc2_T");
				
		transactionEventEPCs.getEpc().add(epc1);
		transactionEventEPCs.getEpc().add(epc2);
		transactionEventType.setEpcList(transactionEventEPCs);
		
		transactionEventType.setAction(ActionType.fromValue("ADD"));	
		transactionEventType.setBizStep("bizStep_T");
		transactionEventType.setDisposition("disposition_T");
		
		
		ReadPointType readPoint=new ReadPointType();
		readPoint.setId("ReadPoint_object");
		ReadPointExtensionType readPointExtension=new ReadPointExtensionType();
		readPoint.setExtension(readPointExtension);
		
		transactionEventType.setReadPoint(readPoint);
		
		BusinessLocationType businessLocation =new BusinessLocationType();
		businessLocation.setId("BusinessLocation_object");
		BusinessLocationExtensionType businessLocationExtension= new BusinessLocationExtensionType();
		businessLocation.setExtension(businessLocationExtension);
		
		transactionEventType.setBizLocation(businessLocation);
		
		BusinessTransactionListType businessTransactionList=new BusinessTransactionListType();	
		BusinessTransactionType businessTransaction1=new BusinessTransactionType();
		businessTransaction1.setType("type1_o");
		businessTransaction1.setValue("value1_o");
		BusinessTransactionType businessTransaction2=new BusinessTransactionType();
		businessTransaction1.setType("type2_o");
		businessTransaction1.setValue("value2_o");
		businessTransactionList.getBizTransaction().add(businessTransaction1);
		businessTransactionList.getBizTransaction().add(businessTransaction2);
		transactionEventType.setBizTransactionList(businessTransactionList);
		
		
		TransactionEventExtensionType transactionEventExtension =new TransactionEventExtensionType();
		
		QuantityListType quantityList =new QuantityListType();
		QuantityElementType quantityElement1=new QuantityElementType();
		quantityElement1.setEpcClass("epcClass1_O");
		quantityElement1.setQuantity(111);
		quantityElement1.setUom("uom1_O");
		QuantityElementType quantityElement2=new QuantityElementType();
		quantityElement2.setEpcClass("epcClass2_O");
		quantityElement2.setQuantity(111);
		quantityElement2.setUom("uom2_O");
		quantityList.getQuantityElement().add(quantityElement1);
		quantityList.getQuantityElement().add(quantityElement2);
		
		transactionEventExtension.setQuantityList(quantityList);
		
		DestinationListType destinationList =new DestinationListType();
		SourceDestType sourceDest3=new SourceDestType(  );
		SourceDestType sourceDest4=new SourceDestType( );
		sourceDest3.setValue("value3");
		sourceDest3.setType("type3");
		sourceDest4.setValue("value4");
		sourceDest4.setType("type4");
		
		destinationList.getDestination().add(sourceDest3);
		destinationList.getDestination().add(sourceDest4);
		transactionEventExtension.setDestinationList(destinationList);
		
		SourceListType sourceList =new SourceListType();
		SourceDestType sourceDest1=new SourceDestType( );
		SourceDestType sourceDest2=new SourceDestType( );
		sourceDest1.setValue("value1");
		sourceDest1.setType("type1");
		sourceDest1.setValue("value2");
		sourceDest1.setType("type2");
		
		sourceList.getSource().add(sourceDest1);
		sourceList.getSource().add(sourceDest2);
		
		
		transactionEventExtension.setSourceList(sourceList);
		
		
		

		TransactionEventExtension2Type transactionEventExtension2=
				new TransactionEventExtension2Type();
		
		transactionEventExtension.setExtension(transactionEventExtension2);
		
		
		transactionEventType.setExtension(transactionEventExtension);
		
		return transactionEventType;
		
	}
	
public static 	AggregationEventType getAggregationEventTypeSample(){
	AggregationEventType aggregationEventType=new AggregationEventType();
	GregorianCalendar gRecordTime = new GregorianCalendar();
	XMLGregorianCalendar recordTime;
	try {
		recordTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(gRecordTime);
		aggregationEventType.setEventTime(recordTime);
		aggregationEventType.setRecordTime(recordTime);
	} catch (DatatypeConfigurationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	aggregationEventType.setEventTimeZoneOffset("eventTimeZoneOffset");
	aggregationEventType.setParentID("parentID");
	
	EPCListType aggregationEventEPCs = new EPCListType();
	org.oliot.model.epcis.EPC epc1=new org.oliot.model.epcis.EPC ("epc1");
	
	org.oliot.model.epcis.EPC  epc2=new org.oliot.model.epcis.EPC ("epc2");
			
	aggregationEventEPCs.getEpc().add(epc1);
	aggregationEventEPCs.getEpc().add(epc2);
	aggregationEventType.setChildEPCs(aggregationEventEPCs);
	
	aggregationEventType.setAction(ActionType.fromValue("ADD"));	
	aggregationEventType.setBizStep("bizStep");
	aggregationEventType.setDisposition("disposition");
	
	
	ReadPointType readPoint=new ReadPointType();
	readPoint.setId("ReadPoint_object");
	ReadPointExtensionType readPointExtension=new ReadPointExtensionType();
	readPoint.setExtension(readPointExtension);
	
	aggregationEventType.setReadPoint(readPoint);
	
	BusinessLocationType businessLocation =new BusinessLocationType();
	businessLocation.setId("BusinessLocation_object");
	BusinessLocationExtensionType businessLocationExtension= new BusinessLocationExtensionType();
	businessLocation.setExtension(businessLocationExtension);
	
	aggregationEventType.setBizLocation(businessLocation);
	
	BusinessTransactionListType businessTransactionList=new BusinessTransactionListType();
	BusinessTransactionType businessTransaction1=new BusinessTransactionType();
	businessTransaction1.setType("type1_o");
	businessTransaction1.setValue("value1_o");
	BusinessTransactionType businessTransaction2=new BusinessTransactionType();
	businessTransaction1.setType("type2_o");
	businessTransaction1.setValue("value2_o");
	businessTransactionList.getBizTransaction().add(businessTransaction1);
	businessTransactionList.getBizTransaction().add(businessTransaction2);
	aggregationEventType.setBizTransactionList(businessTransactionList);
	
	AggregationEventExtensionType aggregationEventExtension =new AggregationEventExtensionType();
	
	QuantityListType quantityList =new QuantityListType();
	QuantityElementType quantityElement1=new QuantityElementType();
	quantityElement1.setEpcClass("epcClass1_O");
	quantityElement1.setQuantity(111);
	quantityElement1.setUom("uom1_O");
	QuantityElementType quantityElement2=new QuantityElementType();
	quantityElement2.setEpcClass("epcClass2_O");
	quantityElement2.setQuantity(111);
	quantityElement2.setUom("uom2_O");
	
	quantityList.getQuantityElement().add(quantityElement1);
	quantityList.getQuantityElement().add(quantityElement2);
	
	aggregationEventExtension.setChildQuantityList(quantityList);
	
	DestinationListType destinationList =new DestinationListType();
	SourceDestType sourceDest3=new SourceDestType(  );
	SourceDestType sourceDest4=new SourceDestType( );
	sourceDest3.setValue("value3");
	sourceDest3.setType("type3");
	sourceDest4.setValue("value4");
	sourceDest4.setType("type4");
	
	destinationList.getDestination().add(sourceDest3);
	destinationList.getDestination().add(sourceDest4);
	aggregationEventExtension.setDestinationList(destinationList);
	
	SourceListType sourceList =new SourceListType();
	SourceDestType sourceDest1=new SourceDestType( );
	SourceDestType sourceDest2=new SourceDestType( );
	sourceDest1.setValue("value1");
	sourceDest1.setType("type1");
	sourceDest1.setValue("value2");
	sourceDest1.setType("type2");
	
	sourceList.getSource().add(sourceDest1);
	sourceList.getSource().add(sourceDest2);
	
	
	aggregationEventExtension.setSourceList(sourceList);
	
	
	

	
	AggregationEventExtension2Type aggregationEventExtension2= 
			new AggregationEventExtension2Type();
	
	aggregationEventExtension.setExtension(aggregationEventExtension2);
	
	aggregationEventType.setExtension(aggregationEventExtension);
	
	return aggregationEventType;
}

public static 	ObjectEventType getObjectEventTypeSample(){
	
	ObjectEventType objectEventType=new ObjectEventType();
	
	GregorianCalendar gRecordTime = new GregorianCalendar();
	XMLGregorianCalendar recordTime;
	try {
		recordTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(gRecordTime);
		objectEventType.setEventTime(recordTime);
		objectEventType.setRecordTime(recordTime);
	} catch (DatatypeConfigurationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	objectEventType.setEventTimeZoneOffset("eventTimeZoneOffset_o");
	
	
	EPCListType objectEventEPCs = new EPCListType();
	org.oliot.model.epcis.EPC epc1=new org.oliot.model.epcis.EPC ("epc1_o");
	
	org.oliot.model.epcis.EPC  epc2=new org.oliot.model.epcis.EPC ("epc2_o");
			
	objectEventEPCs.getEpc().add(epc1);
	objectEventEPCs.getEpc().add(epc2);
	objectEventType.setEpcList(objectEventEPCs);
	
	objectEventType.setAction(ActionType.fromValue("ADD"));	
	objectEventType.setBizStep("bizStep_o");
	objectEventType.setDisposition("disposition_o");
	
	
	ReadPointType readPoint=new ReadPointType();
	readPoint.setId("ReadPoint_object");
	ReadPointExtensionType readPointExtension=new ReadPointExtensionType();

	readPoint.setExtension(readPointExtension);
	
	objectEventType.setReadPoint(readPoint);
	
	BusinessLocationType businessLocation =new BusinessLocationType();
	businessLocation.setId("BusinessLocation_object");
	BusinessLocationExtensionType businessLocationExtension= new BusinessLocationExtensionType();
	businessLocation.setExtension(businessLocationExtension);
	
	objectEventType.setBizLocation(businessLocation);
	
	BusinessTransactionListType businessTransactionList=new BusinessTransactionListType();
	BusinessTransactionType businessTransaction1=new BusinessTransactionType();
	businessTransaction1.setType("type1_o");
	businessTransaction1.setValue("value1_o");
	BusinessTransactionType businessTransaction2=new BusinessTransactionType();
	businessTransaction1.setType("type2_o");
	businessTransaction1.setValue("value2_o");
	businessTransactionList.getBizTransaction().add(businessTransaction1);
	businessTransactionList.getBizTransaction().add(businessTransaction2);
	objectEventType.setBizTransactionList(businessTransactionList);
	
	
	ILMDType iLMDType =new ILMDType ();
	ILMDExtensionType iLMDExtensionType =new ILMDExtensionType();
	iLMDType.setExtension(iLMDExtensionType);
	objectEventType.setIlmd(iLMDType);
	
	
	
	ObjectEventExtensionType ObjectEventExtension = new ObjectEventExtensionType();
	
	
	QuantityListType quantityList =new QuantityListType();
	QuantityElementType quantityElement1=new QuantityElementType();
	quantityElement1.setEpcClass("epcClass1_O");
	quantityElement1.setQuantity(111);
	quantityElement1.setUom("uom1_O");
	QuantityElementType quantityElement2=new QuantityElementType();
	quantityElement2.setEpcClass("epcClass2_O");
	quantityElement2.setQuantity(111);
	quantityElement2.setUom("uom2_O");
	quantityList.getQuantityElement().add(quantityElement1);
	quantityList.getQuantityElement().add(quantityElement2);
	
	ObjectEventExtension.setQuantityList(quantityList);
	
	DestinationListType destinationList =new DestinationListType();
	SourceDestType sourceDest3=new SourceDestType(  );
	SourceDestType sourceDest4=new SourceDestType( );
	sourceDest3.setValue("value3");
	sourceDest3.setType("type3");
	sourceDest4.setValue("value4");
	sourceDest4.setType("type4");
	
	destinationList.getDestination().add(sourceDest3);
	destinationList.getDestination().add(sourceDest4);
	ObjectEventExtension.setDestinationList(destinationList);
	
	SourceListType sourceList =new SourceListType();
	SourceDestType sourceDest1=new SourceDestType( );
	SourceDestType sourceDest2=new SourceDestType( );
	sourceDest1.setValue("value1");
	sourceDest1.setType("type1");
	sourceDest1.setValue("value2");
	sourceDest1.setType("type2");
	
	
	sourceList.getSource().add(sourceDest1);
	sourceList.getSource().add(sourceDest2);
	
	
	ObjectEventExtension.setSourceList(sourceList);
	
	ILMDType iLMDType2 =new ILMDType ();
	ILMDExtensionType iLMDExtensionType2 =new ILMDExtensionType();
	iLMDType.setExtension(iLMDExtensionType2);
	ObjectEventExtension.setIlmd(iLMDType2);
	
	
	ObjectEventExtension2Type objectEventExtension2Type =
			new ObjectEventExtension2Type();
	
	
	
	ObjectEventExtension.setExtension(objectEventExtension2Type);
	
	objectEventType.setExtension(ObjectEventExtension);
	
	return objectEventType;
}
/*
public static 	QuantityEventType getQuantityEventTypeSample(){
	
	QuantityEventType quantityEventType=new QuantityEventType();
	
	GregorianCalendar gRecordTime = new GregorianCalendar();
	XMLGregorianCalendar recordTime;
	try {
		recordTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(gRecordTime);
		quantityEventType.setEventTime(recordTime);
		quantityEventType.setRecordTime(recordTime);
	} catch (DatatypeConfigurationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	quantityEventType.setEventTimeZoneOffset("eventTimeZoneOffset_o");
	quantityEventType.setEpcClass("epcClass");
	quantityEventType.setQuantity(12);
	
		
	quantityEventType.setBizStep("bizStep_q");
	quantityEventType.setDisposition("disposition_q");
	
	
	ReadPointType readPoint=new ReadPointType("ReadPoint_q");
	ReadPointExtensionType readPointExtension=new ReadPointExtensionType("ReadPointExtension_q");
	readPoint.setExtension(readPointExtension);
	
	quantityEventType.setReadPoint(readPoint);
	
	BusinessLocationType businessLocation =new BusinessLocationType("BusinessLocation_object");
	BusinessLocationExtensionType businessLocationExtension= new BusinessLocationExtensionType("BusinessLocationExtension_q");
	businessLocation.setExtension(businessLocationExtension);
	
	quantityEventType.setBizLocation(businessLocation);
	
	BusinessTransactionListType businessTransactionList=new BusinessTransactionListType();
	BusinessTransactionType businessTransaction1=new BusinessTransactionType("value1_q","type1_q");
	BusinessTransactionType businessTransaction2=new BusinessTransactionType("value2_q","type2_q");
	businessTransactionList.getBizTransaction().add(businessTransaction1);
	businessTransactionList.getBizTransaction().add(businessTransaction2);
	quantityEventType.setBizTransactionList(businessTransactionList);
	
	
	
	
	
	
	QuantityEventExtensionType quantityEventExtensionType = new QuantityEventExtensionType();
	quantityEventExtensionType.setQuantityEventType(quantityEventType);
	
	
	
	
	return quantityEventType;
}
*/

public static 	TransformationEventType getTransformationEventTypeSample(){
	TransformationEventType transformationEventType=new  TransformationEventType();
	
	GregorianCalendar gRecordTime = new GregorianCalendar();
	XMLGregorianCalendar recordTime;
	try {
		recordTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(gRecordTime);
		transformationEventType.setEventTime(recordTime);
		transformationEventType.setRecordTime(recordTime);
	} catch (DatatypeConfigurationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	transformationEventType.setEventTimeZoneOffset("eventTimeZoneOffset");
	
	
	EPC epc1=new EPC ("epc1_I");
	EPC  epc2=new EPC ("epc2_I");
	EPCListType ePCListType1=new EPCListType();
	ePCListType1.getEpc().add(epc1);
	ePCListType1.getEpc().add(epc2);
	transformationEventType.setInputEPCList(ePCListType1);
	
	
	EPC epc3=new EPC ("epc1_O");	
	EPC  epc4=new EPC ("epc2_O");
	EPCListType ePCListType2=new EPCListType();
	ePCListType2.getEpc().add(epc3);
	ePCListType2.getEpc().add(epc4);
	transformationEventType.setOutputEPCList(ePCListType2);
	
	
	QuantityElementType quantityElement1=new QuantityElementType();
	quantityElement1.setEpcClass("epcClass1_O");
	quantityElement1.setQuantity(111);
	quantityElement1.setUom("uom1_O");
	QuantityElementType quantityElement2=new QuantityElementType();
	quantityElement2.setEpcClass("epcClass2_O");
	quantityElement2.setQuantity(111);
	quantityElement2.setUom("uom2_O");
	QuantityListType quantityListType=new QuantityListType();
	
	quantityListType.getQuantityElement().add(quantityElement1);
	quantityListType.getQuantityElement().add(quantityElement2);
	transformationEventType.setInputQuantityList(quantityListType);
	//transformationEventType.getInputQuantityList().getQuantityElement().add(quantityElement1);
	//transformationEventType.getInputQuantityList().getQuantityElement().add(quantityElement2);
	
	QuantityElementType quantityElement3=new QuantityElementType();
	quantityElement3.setEpcClass("epcClass1_O");
	quantityElement3.setQuantity(111);
	quantityElement3.setUom("uom1_O");
	QuantityElementType quantityElement4=new QuantityElementType();
	quantityElement4.setEpcClass("epcClass2_O");
	quantityElement4.setQuantity(111);
	quantityElement4.setUom("uom2_O");
	
	QuantityListType quantityListType2=new QuantityListType();
	quantityListType2.getQuantityElement().add(quantityElement3);
	quantityListType2.getQuantityElement().add(quantityElement4);
	transformationEventType.setOutputQuantityList(quantityListType);
	
	
	//transformationID
	transformationEventType.setTransformationID("transformationID");
	transformationEventType.setBizStep("bizStep");
	transformationEventType.setDisposition("disposition");
	
	
	ReadPointType readPoint=new ReadPointType();
	readPoint.setId("ReadPoint_object");
	ReadPointExtensionType readPointExtension=new ReadPointExtensionType();
	readPoint.setExtension(readPointExtension);
	
	transformationEventType.setReadPoint(readPoint);
	
	BusinessLocationType businessLocation =new BusinessLocationType();
	businessLocation.setId("BusinessLocation_object");
	BusinessLocationExtensionType businessLocationExtension= new BusinessLocationExtensionType();
	businessLocation.setExtension(businessLocationExtension);
	
	transformationEventType.setBizLocation(businessLocation);
	
	BusinessTransactionListType businessTransactionList=new BusinessTransactionListType();
	BusinessTransactionType businessTransaction1=new BusinessTransactionType();
	businessTransaction1.setType("type1_o");
	businessTransaction1.setValue("value1_o");
	BusinessTransactionType businessTransaction2=new BusinessTransactionType();
	businessTransaction1.setType("type2_o");
	businessTransaction1.setValue("value2_o");
	businessTransactionList.getBizTransaction().add(businessTransaction1);
	businessTransactionList.getBizTransaction().add(businessTransaction2);
	transformationEventType.setBizTransactionList(businessTransactionList);
	
	SourceListType sourceList =new SourceListType();
	SourceDestType sourceDest1=new SourceDestType( );
	SourceDestType sourceDest2=new SourceDestType( );
	sourceDest1.setValue("value1");
	sourceDest1.setType("type1");
	sourceDest1.setValue("value2");
	sourceDest1.setType("type2");
	
	sourceList.getSource().add(sourceDest1);
	sourceList.getSource().add(sourceDest2);
	
	transformationEventType.setSourceList(sourceList);
	
	DestinationListType destinationList =new DestinationListType();
	SourceDestType sourceDest3=new SourceDestType(  );
	SourceDestType sourceDest4=new SourceDestType( );
	sourceDest3.setValue("value3");
	sourceDest3.setType("type3");
	sourceDest4.setValue("value4");
	sourceDest4.setType("type4");
	
	destinationList.getDestination().add(sourceDest3);
	destinationList.getDestination().add(sourceDest4);
	transformationEventType.setDestinationList(destinationList);
	
	TransformationEventExtensionType transformationEventExtension=new TransformationEventExtensionType();
	transformationEventType.setExtension(transformationEventExtension);
	
	
	
	
	
	return transformationEventType;
}
/*
public static 	SensorEventType getSensorEventTypeSample(){
	SensorEventType sensorEventType = new SensorEventType();
	
	GregorianCalendar gRecordTime = new GregorianCalendar();
	XMLGregorianCalendar recordTime;
	try {
		recordTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(gRecordTime);
		sensorEventType.setEventTime(recordTime);
		sensorEventType.setRecordTime(recordTime);
		sensorEventType.setFinishTime(recordTime);
	} catch (DatatypeConfigurationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	sensorEventType.setEventTimeZoneOffset("eventTimeZoneOffset");
	
	
	
	
	sensorEventType.setAction(ActionType.fromValue("ADD"));	
	sensorEventType.setBizStep("bizStep");
	sensorEventType.setDisposition("disposition");
	
	
	ReadPointType readPoint=new ReadPointType("ReadPoint");
	ReadPointExtensionType readPointExtension=new ReadPointExtensionType("ReadPointExtension");
	readPoint.setExtension(readPointExtension);
	
	sensorEventType.setReadPoint(readPoint);
	
	BusinessLocationType businessLocation =new BusinessLocationType("BusinessLocation");
	BusinessLocationExtensionType businessLocationExtension= new BusinessLocationExtensionType("BusinessLocationExtension");
	businessLocation.setExtension(businessLocationExtension);
	
	sensorEventType.setBizLocation(businessLocation);
	
	BusinessTransactionListType businessTransactionList=new BusinessTransactionListType();
	BusinessTransactionType businessTransaction1=new BusinessTransactionType("value1","type1");
	BusinessTransactionType businessTransaction2=new BusinessTransactionType("value2","type2");
	businessTransactionList.getBizTransaction().add(businessTransaction1);
	businessTransactionList.getBizTransaction().add(businessTransaction2);
	sensorEventType.setBizTransactionList(businessTransactionList);
	
	sensorEventType.setTargetObject("TargetObject");
	sensorEventType.setTargetArea("TargetArea");
	
	EPC epc1=new EPC("Value1");
	SensingElementType SensingElementType1=new SensingElementType("type1","value1", "uom1");
	SensingElementType1.setEpc(epc1);
	EPC epc2=new EPC("Value2");
	SensingElementType SensingElementType2=new SensingElementType("type2","value2", "uom2");
	SensingElementType2.setEpc(epc2);
	SensingListType sensingListType=new SensingListType();
	sensingListType.getSensingElement().add(SensingElementType1);
	sensingListType.getSensingElement().add(SensingElementType1);
	//sensorEventType.getSensingList().getSensingElement().add(SensingElementType1);
	//sensorEventType.getSensingList().getSensingElement().add(SensingElementType2);
	sensorEventType.setSensingList(sensingListType);
	
	SensorEventExtensionType SensorEventExtensionType=new SensorEventExtensionType();
	sensorEventType.setExtension(SensorEventExtensionType);
	
	
	
	return sensorEventType;
}*/
public static 	VocabularyType getVocabularyTypeSample(){
	
	// create a <VocabularyElement> which contains two <attribute> elements
	VocabularyType voc = new VocabularyType();
	voc.setType("urn:epcglobal:epcis:vtype:BusinessLocation");
	VocabularyElementListType vocElemList = new VocabularyElementListType();
	
	VocabularyElementType vocElem = new VocabularyElementType();
	vocElem.setId("urn:epc:id:sgln:0037000.00729.0");
	AttributeType attr1 = new AttributeType();
	attr1.setId("urn:epcglobal:fmcg:mda:latitude");
	attr1.setValue("+18.0000");
	AttributeType attr2 = new AttributeType();
	attr2.setId("urn:epcglobal:fmcg:mda:longitude");
	attr2.setValue("-70.0000");
	vocElem.getAttribute().add(attr1);
	vocElem.getAttribute().add(attr2);
	
	IDListType iDListType1=new IDListType();
	iDListType1.getId().add("urn:id:sgln:0037000.00729.8201");
	iDListType1.getId().add("urn:id:sgln:0037000.00729.8202");
	iDListType1.getId().add("urn:id:sgln:0037000.00729.8203");
	vocElem.setChildren(iDListType1);
	vocElemList.getVocabularyElement().add(vocElem);
	voc.setVocabularyElementList(vocElemList);
	
	VocabularyElementExtensionType vocabularyElementExtensionType1 =new VocabularyElementExtensionType();
	vocElem.setExtension(vocabularyElementExtensionType1);
	
	
	


	
	return voc;
}

}
