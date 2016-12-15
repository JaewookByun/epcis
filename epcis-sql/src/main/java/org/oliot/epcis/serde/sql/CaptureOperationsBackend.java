package org.oliot.epcis.serde.sql;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.model.epcis.AggregationEventType;
import org.oliot.model.epcis.AttributeType;
import org.oliot.model.epcis.BusinessTransactionType;
import org.oliot.model.epcis.EPC;
import org.oliot.model.epcis.EPCListType;
import org.oliot.model.epcis.ObjectEventType;
import org.oliot.model.epcis.QuantityElementType;
import org.oliot.model.epcis.QuantityEventType;
import org.oliot.model.epcis.SourceDestType;
import org.oliot.model.epcis.TransactionEventType;
import org.oliot.model.epcis.TransformationEventType;
import org.oliot.model.epcis.VocabularyElementType;
import org.oliot.model.epcis.VocabularyType;
import org.oliot.model.oliot.Action;
import org.oliot.model.oliot.AggregationEvent;
import org.oliot.model.oliot.AggregationEventExtension;
import org.oliot.model.oliot.AggregationEventExtension2;
import org.oliot.model.oliot.Attribute;
import org.oliot.model.oliot.BusinessLocation;
import org.oliot.model.oliot.BusinessLocationExtension;
import org.oliot.model.oliot.BusinessTransaction;
import org.oliot.model.oliot.BusinessTransactionList;
import org.oliot.model.oliot.CorrectiveEventID;
import org.oliot.model.oliot.CorrectiveEventIDs;
import org.oliot.model.oliot.DestinationList;
import org.oliot.model.oliot.EPCISEventExtension;
import org.oliot.model.oliot.EPCISEventExtension2;
import org.oliot.model.oliot.EPCList;
import org.oliot.model.oliot.EPCN;
import org.oliot.model.oliot.ErrorDeclaration;
import org.oliot.model.oliot.ErrorDeclarationExtension;
import org.oliot.model.oliot.ExtensionMap;
import org.oliot.model.oliot.ExtensionMaps;
import org.oliot.model.oliot.IDList;
import org.oliot.model.oliot.ILMD;
import org.oliot.model.oliot.ILMDExtension;
import org.oliot.model.oliot.ObjectEvent;
import org.oliot.model.oliot.ObjectEventExtension;
import org.oliot.model.oliot.ObjectEventExtension2;
import org.oliot.model.oliot.QuantityElement;
import org.oliot.model.oliot.QuantityEvent;
import org.oliot.model.oliot.QuantityEventExtension;
import org.oliot.model.oliot.QuantityList;
import org.oliot.model.oliot.ReadPoint;
import org.oliot.model.oliot.ReadPointExtension;
import org.oliot.model.oliot.SensingElement;
import org.oliot.model.oliot.SensingList;
import org.oliot.model.oliot.SensorEvent;
import org.oliot.model.oliot.SensorEventExtension;
import org.oliot.model.oliot.SourceDest;
import org.oliot.model.oliot.SourceList;
import org.oliot.model.oliot.TransactionEvent;
import org.oliot.model.oliot.TransactionEventExtension;
import org.oliot.model.oliot.TransactionEventExtension2;
import org.oliot.model.oliot.TransformationEvent;
import org.oliot.model.oliot.TransformationEventExtension;
import org.oliot.model.oliot.Vocabulary;
import org.oliot.model.oliot.VocabularyElement;
import org.oliot.model.oliot.VocabularyElementExtension;
import org.oliot.model.oliot.VocabularyElementList;
import org.oliot.model.oliot.VocabularyExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * Copyright (C) 2015 Yalew Kidane
 *
 * This project is part of Oliot (oliot.org), pursuing the implementation of
 * Electronic Product Code Information Service(EPCIS) v1.1 specification in
 * EPCglobal.
 * [http://www.gs1.org/gsmp/kc/epcglobal/epcis/epcis_1_1-standard-20140520.pdf]
 * 
 *
 * @author Yalew Kidane, MSc student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         yalewkidane@kaist.ac.kr, yalewkidane@gmail.com
 */

@Repository
public class CaptureOperationsBackend {
	@Autowired
	private SessionFactory sessionFactory; // check this

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	@SuppressWarnings("rawtypes")
	public void save(AggregationEventType aggregationEventType) {
		System.out.println("Aggregation Event capture operation");
		Session session = getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();

		AggregationEvent aggregationEvent = new AggregationEvent();

		EPCList aggregationEventEPCs = new EPCList();
		ReadPoint readpointH;
		ReadPointExtension readPointExtensionH;
		BusinessTransactionList businessTransactionList;
		BusinessTransaction businessTransaction;
		BusinessLocation businessLocationH;
		BusinessLocationExtension businessLocationExtensionH;

		AggregationEventExtension aggregationEventExtensionH;
		QuantityList childQuantityListH;
		SourceList sourceListH;
		DestinationList destinationListH;
		QuantityElement quantityElement;
		SourceDest sourceDestS, sourceDestD;
		// Event Time
		if (aggregationEventType.getEventTime() != null) {
			aggregationEvent.setEventTime(aggregationEventType.getEventTime()
					.toGregorianCalendar().getTime());
		}

		// Record Time
		GregorianCalendar gRecordTime = new GregorianCalendar();
		XMLGregorianCalendar recordTime;
		try {
			recordTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(
					gRecordTime);
			aggregationEvent.setRecordTime(recordTime.toGregorianCalendar()
					.getTime());
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
		// Aggregation Time offset
		if (aggregationEventType.getEventTimeZoneOffset() != null) {
			aggregationEvent.setEventTimeZoneOffset(aggregationEventType
					.getEventTimeZoneOffset());
		}
		// Parent ID
		if (aggregationEventType.getParentID() != null) {
			aggregationEvent.setParentID(aggregationEventType.getParentID());
		}

		// Child EPCs
		if (aggregationEventType.getChildEPCs() != null) {
			EPCListType epcs = aggregationEventType.getChildEPCs();
			List<EPC> epcList = epcs.getEpc();
			EPCN epctn;
			for (int i = 0; i < epcList.size(); i++) {
				epctn = new EPCN(epcList.get(i).getValue());
				aggregationEventEPCs.getEpc().add(epctn);
				session.save(epctn);
			}
			// aggregationEvent.setChildEPCs(aggregationEventEPCs);
			//aggregationEventEPCs.setAggregationEvent(aggregationEvent);
			session.save(aggregationEventEPCs);
			aggregationEvent.setChildEPCs(aggregationEventEPCs);

		}
		
		//BaseExtension
		if(aggregationEventType.getBaseExtension() !=null){
			EPCISEventExtension ePCISEventExtension=new EPCISEventExtension();
			ePCISEventExtension.setEventID(aggregationEventType.getBaseExtension().getEventID());
			if(aggregationEventType.getBaseExtension().getErrorDeclaration()!=null){
				ErrorDeclaration errorDeclaration=new ErrorDeclaration();
				errorDeclaration.setDeclarationTime(aggregationEventType.getBaseExtension()
						.getErrorDeclaration().getDeclarationTime()
						.toGregorianCalendar().getTime());
				errorDeclaration.setReason(aggregationEventType.getBaseExtension().getErrorDeclaration().getReason());
				if(aggregationEventType.getBaseExtension().getErrorDeclaration().getCorrectiveEventIDs()!=null){
					
					CorrectiveEventID CorrectiveEventID;
					CorrectiveEventIDs correctiveEventIDs=new CorrectiveEventIDs(); 
					List<CorrectiveEventID> correctiveEventIDList = new ArrayList<CorrectiveEventID>();
					if(aggregationEventType.getBaseExtension().getErrorDeclaration().getCorrectiveEventIDs().getCorrectiveEventID()!=null){
						List<String> ceidList=aggregationEventType.getBaseExtension().getErrorDeclaration().getCorrectiveEventIDs().getCorrectiveEventID();
						for(int i=0; i<ceidList.size();i++){
							CorrectiveEventID=new CorrectiveEventID();
							CorrectiveEventID.setCorrectiveEventID(ceidList.get(i));
							session.save(CorrectiveEventID);
							correctiveEventIDList.add(CorrectiveEventID);
						}
						correctiveEventIDs.setCorrectiveEventID(correctiveEventIDList);
					}
					errorDeclaration.setCorrectiveEventIDs(correctiveEventIDs);
					session.save(correctiveEventIDs);
					
				}
				if(aggregationEventType.getBaseExtension().getErrorDeclaration().getExtension()!=null){
					ErrorDeclarationExtension errorDeclarationExtension= new ErrorDeclarationExtension();
					
					errorDeclaration.setExtension(errorDeclarationExtension);
					session.save(errorDeclarationExtension);
				}
				//Error Declaration Any
				if(aggregationEventType.getBaseExtension().getErrorDeclaration().getAny()!=null){
					List<Object> objectList=aggregationEventType.getBaseExtension().getErrorDeclaration().getAny();
					List<ExtensionMap> extensionMapList=new ArrayList<>();
					WriteUtility.getAnyObject(objectList,extensionMapList);
					ExtensionMaps extensionMaps= new ExtensionMaps();
					extensionMaps.setExtensionMapList(extensionMapList);
					ExtensionMap extensionMap;
					for(int i=0;i<extensionMapList.size();i++){
						extensionMap=new ExtensionMap();
						extensionMap=extensionMapList.get(i);
						session.save(extensionMap);
					}
					session.save(extensionMaps);
					errorDeclaration.setExtensionMaps(extensionMaps);
				}
				
				ePCISEventExtension.setErrorDeclaration(errorDeclaration);
				session.save(errorDeclaration);
			}
			if(aggregationEventType.getBaseExtension().getExtension()!=null){
				EPCISEventExtension2 epcisEventExtension2= new EPCISEventExtension2();
				ePCISEventExtension.setExtension(epcisEventExtension2);
				session.save(epcisEventExtension2);
			}
			aggregationEvent.setBaseExtension(ePCISEventExtension);
			session.save(ePCISEventExtension);
		}
		
		
		
		
		// action
		aggregationEvent.setAction(Action.fromValue(aggregationEventType
				.getAction().name()));
		// Business step
		if (aggregationEventType.getBizStep() != null) {
			aggregationEvent.setBizStep(aggregationEventType.getBizStep());
		}
		// Disposition
		if (aggregationEventType.getDisposition() != null) {
			aggregationEvent.setDisposition(aggregationEventType
					.getDisposition());
		}
		// read point
		if (aggregationEventType.getReadPoint() != null) {
			readpointH = new ReadPoint(aggregationEventType.getReadPoint()
					.getId());
			
			
		
			  if(aggregationEventType.getReadPoint().getExtension() !=null){
			  readPointExtensionH=new  ReadPointExtension();
					
			  session.save(readPointExtensionH);
			  readpointH.setExtension(readPointExtensionH);
			  
			  }
			  session.save(readpointH);
			  aggregationEvent.setReadPoint(readpointH);
		}

		// business transaction
		if (aggregationEventType.getBizTransactionList() != null) {
			List<BusinessTransactionType> bizTransaction = aggregationEventType
					.getBizTransactionList().getBizTransaction();
			businessTransactionList = new BusinessTransactionList();

			for (int i = 0; i < bizTransaction.size(); i++) {
				businessTransaction = new BusinessTransaction(bizTransaction
						.get(i).getValue(), bizTransaction.get(i).getType());
				businessTransactionList.getBizTransaction().add(
						businessTransaction);
				session.save(businessTransaction);
			}
			aggregationEvent.setBizTransactionList(businessTransactionList);
			session.save(businessTransactionList);
		}

		// Business location
		if (aggregationEventType.getBizLocation() != null) {
			businessLocationH = new BusinessLocation(aggregationEventType
					.getBizLocation().getId());
			
			
			  if(aggregationEventType.getBizLocation().getExtension() !=null){
			  businessLocationExtensionH=new  BusinessLocationExtension();
				
			  
			  session.save(businessLocationExtensionH); 
			  businessLocationH.setExtension(businessLocationExtensionH);
			  }
			aggregationEvent.setBizLocation(businessLocationH);
			session.save(businessLocationH); 
		}

		

		// Aggregation Event Extension
		if (aggregationEventType.getExtension() != null) {
			aggregationEventExtensionH = new AggregationEventExtension();
			
			if(aggregationEventType.getExtension().getChildQuantityList()!=null){
				List<QuantityElementType> quantityList = aggregationEventType
						.getExtension().getChildQuantityList().getQuantityElement();
				childQuantityListH = new QuantityList();
				for (int i = 0; i < quantityList.size(); i++) {
					quantityElement = new QuantityElement();
					quantityElement.setEpcClass(quantityList.get(i).getEpcClass());
					quantityElement.setQuantity(quantityList.get(i).getQuantity().floatValue());
					quantityElement.setUom(quantityList.get(i).getUom());
					childQuantityListH.getQuantityElement().add(quantityElement);
					session.save(quantityElement);
				}
				aggregationEventExtensionH.setChildQuantityList(childQuantityListH);
				session.save(childQuantityListH);
			}
			
			if(aggregationEventType.getExtension().getSourceList()!=null){
				List<SourceDestType> sourceList = aggregationEventType
						.getExtension().getSourceList().getSource();
				sourceListH = new SourceList();
				for (int i = 0; i < sourceList.size(); i++) {
					sourceDestS = new SourceDest(sourceList.get(i).getValue(),
							sourceList.get(i).getType());
					sourceListH.getSource().add(sourceDestS);
					session.save(sourceDestS);
				}
	
				aggregationEventExtensionH.setSourceList(sourceListH);
				session.save(sourceListH);
			}

			if(aggregationEventType.getExtension().getDestinationList()!=null){
				List<SourceDestType> destinationList = aggregationEventType
						.getExtension().getDestinationList().getDestination();
				destinationListH = new DestinationList();
				for (int i = 0; i < destinationList.size(); i++) {
					sourceDestD = new SourceDest(destinationList.get(i).getValue(),
							destinationList.get(i).getType());
					destinationListH.getDestination().add(sourceDestD);
					session.save(sourceDestD);
				}
	
				aggregationEventExtensionH.setDestinationList(destinationListH);
				session.save(destinationListH);
			}
			

			//aggregationEventExtensionH.setAggregationEvent(aggregationEvent);
			
			
			 if(aggregationEventType.getExtension().getExtension() != null){
				 AggregationEventExtension2 aggregationEventExtension2=new
						 AggregationEventExtension2();
				 
//				 if(aggregationEventType.getExtension().getExtension().getOtherAttributes() != null){
//					 Map<QName, String> otherAtfrom=aggregationEventType.getExtension().getExtension().getOtherAttributes();
//					 List<MapExt> mapExtList=new ArrayList<MapExt>();
//						Set<QName> setKeyAll=otherAtfrom.keySet();
//						Iterator iteratorAll=setKeyAll.iterator();
//						 
//						while(iteratorAll.hasNext()){
//							MapExt mapExt= new MapExt();
//							QName keyname=(QName) iteratorAll.next();
//							mapExt.setType(keyname.getNamespaceURI());
//							String value=otherAtfrom.get(keyname);
//							mapExt.setValue(otherAtfrom.get(keyname));
//							float f=0;
//							
//							try{
//							 f=Float.parseFloat(otherAtfrom.get(keyname)); 
//							}
//							catch(NumberFormatException e){	
//							}
//							try{
//								DateFormat format=new SimpleDateFormat("MMMM d, yyyy");
//								Date date= format.parse(value);
//								mapExt.setTimeValue(date);
//							}
//							catch (ParseException e) {
//								
//							}
//							mapExt.setFloatValue(f);
//							
//							
//							mapExtList.add(mapExt);
//							session.save(mapExt);
//						}
//						aggregationEventExtension2.setMapExt(mapExtList);
//				 }
			
			  
			  session.save(aggregationEventExtension2); 
			  aggregationEventExtensionH.setExtension(aggregationEventExtension2);
			 }
			 
			 session.save(aggregationEventExtensionH);
			 aggregationEvent.setExtension(aggregationEventExtensionH);
			}
		//AggregationEvent any
		if(aggregationEventType.getAny()!=null){
			List<Object> objectList=aggregationEventType.getAny();
			
			List<ExtensionMap> extensionMapList=new ArrayList<>();
			
			WriteUtility.getAnyObject(objectList,extensionMapList);
			ExtensionMaps extensionMaps= new ExtensionMaps();
			extensionMaps.setExtensionMapList(extensionMapList);
			ExtensionMap extensionMap;
			for(int i=0;i<extensionMapList.size();i++){
				extensionMap=new ExtensionMap();
				extensionMap=extensionMapList.get(i);
				session.save(extensionMap);
			}
			aggregationEvent.setExtensionMaps(extensionMaps);
			session.save(extensionMaps);
		}

		session.save(aggregationEvent);
		tx.commit();
		session.close();

	}
//============================================================================================================================
	
	@SuppressWarnings("rawtypes")
	public void save(ObjectEventType objectEventType) {

		System.out.println("Object Event capture operation");
		Session session = getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();

		ObjectEvent objectEvent = new ObjectEvent();
		//ObjectEventEPCs objectEventEPCs = new ObjectEventEPCs();
		EPCISEventExtension baseExtension;
		EPCList objectEventEPCs=new EPCList();

		ReadPoint readpointH;
		ReadPointExtension readPointExtensionH;
		BusinessTransactionList businessTransactionList;
		BusinessTransaction businessTransaction;
		BusinessLocation businessLocationH;
		BusinessLocationExtension businessLocationExtensionH;

		ObjectEventExtension objectEventExtensionH;

		QuantityList childQuantityListH;
		SourceList sourceListH;
		DestinationList destinationListH;
		QuantityElement quantityElement;
		SourceDest sourceDestS, sourceDestD;
		// Event Time
		if (objectEventType.getEventTime() != null) {
			objectEvent.setEventTime(objectEventType.getEventTime()
					.toGregorianCalendar().getTime());
		}

		// Record Time
		GregorianCalendar gRecordTime = new GregorianCalendar();
		XMLGregorianCalendar recordTime;
		try {
			recordTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(
					gRecordTime);
			objectEvent.setRecordTime(recordTime.toGregorianCalendar()
					.getTime());
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
		// Event Time offset
		if (objectEventType.getEventTimeZoneOffset() != null) {
			objectEvent.setEventTimeZoneOffset(objectEventType
					.getEventTimeZoneOffset());
		}
		
		//BaseExtension
		if(objectEventType.getBaseExtension() !=null){
			EPCISEventExtension ePCISEventExtension=new EPCISEventExtension();
			ePCISEventExtension.setEventID(objectEventType.getBaseExtension().getEventID());
			if(objectEventType.getBaseExtension().getErrorDeclaration()!=null){
				ErrorDeclaration errorDeclaration=new ErrorDeclaration();
				errorDeclaration.setDeclarationTime(objectEventType.getBaseExtension()
						.getErrorDeclaration().getDeclarationTime()
						.toGregorianCalendar().getTime());
				errorDeclaration.setReason(objectEventType.getBaseExtension().getErrorDeclaration().getReason());
				if(objectEventType.getBaseExtension().getErrorDeclaration().getCorrectiveEventIDs()!=null){
					
					CorrectiveEventID CorrectiveEventID;
					CorrectiveEventIDs correctiveEventIDs=new CorrectiveEventIDs(); 
					List<CorrectiveEventID> correctiveEventIDList = new ArrayList<CorrectiveEventID>();
					if(objectEventType.getBaseExtension().getErrorDeclaration().getCorrectiveEventIDs().getCorrectiveEventID()!=null){
						List<String> ceidList=objectEventType.getBaseExtension().getErrorDeclaration().getCorrectiveEventIDs().getCorrectiveEventID();
						for(int i=0; i<ceidList.size();i++){
							CorrectiveEventID=new CorrectiveEventID();
							CorrectiveEventID.setCorrectiveEventID(ceidList.get(i));
							session.save(CorrectiveEventID);
							correctiveEventIDList.add(CorrectiveEventID);
						}
						correctiveEventIDs.setCorrectiveEventID(correctiveEventIDList);
					}
					errorDeclaration.setCorrectiveEventIDs(correctiveEventIDs);
					session.save(correctiveEventIDs);
					
				}
				if(objectEventType.getBaseExtension().getErrorDeclaration().getExtension()!=null){
					ErrorDeclarationExtension errorDeclarationExtension= new ErrorDeclarationExtension();
					
					errorDeclaration.setExtension(errorDeclarationExtension);
					session.save(errorDeclarationExtension);
				}
				//Error Declaration Any
				if(objectEventType.getBaseExtension().getErrorDeclaration().getAny()!=null){
					List<Object> objectList=objectEventType.getBaseExtension().getErrorDeclaration().getAny();
					List<ExtensionMap> extensionMapList=new ArrayList<>();
					WriteUtility.getAnyObject(objectList,extensionMapList);
					ExtensionMaps extensionMaps= new ExtensionMaps();
					extensionMaps.setExtensionMapList(extensionMapList);
					ExtensionMap extensionMap;
					for(int i=0;i<extensionMapList.size();i++){
						extensionMap=new ExtensionMap();
						extensionMap=extensionMapList.get(i);
						session.save(extensionMap);
					}
					session.save(extensionMaps);
					errorDeclaration.setExtensionMaps(extensionMaps);
				}
				
				ePCISEventExtension.setErrorDeclaration(errorDeclaration);
				session.save(errorDeclaration);
			}
			if(objectEventType.getBaseExtension().getExtension()!=null){
				EPCISEventExtension2 epcisEventExtension2= new EPCISEventExtension2();
				ePCISEventExtension.setExtension(epcisEventExtension2);
				session.save(epcisEventExtension2);
			}
			objectEvent.setBaseExtension(ePCISEventExtension);
			session.save(ePCISEventExtension);
		}

		// action
		objectEvent.setAction(Action.fromValue(objectEventType.getAction()
				.name()));
		// Business step
		if (objectEventType.getBizStep() != null) {
			objectEvent.setBizStep(objectEventType.getBizStep());
		}
		// Disposition
		if (objectEventType.getDisposition() != null) {
			objectEvent.setDisposition(objectEventType.getDisposition());
		}
		// read point
		if (objectEventType.getReadPoint() != null) {
			readpointH = new ReadPoint(objectEventType.getReadPoint().getId());
			
			
			  if(objectEventType.getReadPoint().getExtension() !=null){
			  readPointExtensionH=new ReadPointExtension();
					
			 
			  session.save(readPointExtensionH);
				readpointH.setExtension(readPointExtensionH);; 
			  
			  }
			  objectEvent.setReadPoint(readpointH);
				session.save(readpointH);
		}

		// business transaction
		if (objectEventType.getBizTransactionList() != null) {
			List<BusinessTransactionType> bizTransaction = objectEventType
					.getBizTransactionList().getBizTransaction();
			businessTransactionList = new BusinessTransactionList();

			for (int i = 0; i < bizTransaction.size(); i++) {
				businessTransaction = new BusinessTransaction(bizTransaction
						.get(i).getValue(), bizTransaction.get(i).getType());
				businessTransactionList.getBizTransaction().add(
						businessTransaction);
				session.save(businessTransaction);
			}
			objectEvent.setBizTransactionList(businessTransactionList);
			session.save(businessTransactionList);
		}

		// Business location
		if (objectEventType.getBizLocation() != null) {
			businessLocationH = new BusinessLocation(objectEventType
					.getBizLocation().getId());
			objectEvent.setBizLocation(businessLocationH);
			session.save(businessLocationH);
			
			  if(objectEventType.getBizLocation() !=null){
			  businessLocationExtensionH=new BusinessLocationExtension();
			  
			  session.save(businessLocationExtensionH); 
			 
			  }

		}
		
		
//		if (objectEventType.getIlmd() != null) {
//			ILMD iLMD = new ILMD();
//			
//			if (objectEventType.getIlmd().getExtension() != null) {
//				ILMDExtension iLMDExtension = new ILMDExtension();
//				
//				session.save(iLMDExtension);
//				iLMD.setExtension(iLMDExtension);
//
//			}
//			objectEvent.setIlmd(iLMD);
//			session.save(iLMD);
//		}

		

		// objectEventType Event Extension
		if (objectEventType.getExtension() != null) {
			objectEventExtensionH = new ObjectEventExtension();
			
			if (objectEventType.getExtension().getQuantityList() != null){
				List<QuantityElementType> quantityList = objectEventType
						.getExtension().getQuantityList().getQuantityElement();
				childQuantityListH = new QuantityList();
				for (int i = 0; i < quantityList.size(); i++) {
					quantityElement = new QuantityElement();
					quantityElement.setEpcClass(quantityList.get(i).getEpcClass());
					quantityElement.setQuantity(quantityList.get(i).getQuantity().floatValue());
					quantityElement.setUom(quantityList.get(i).getUom());
					childQuantityListH.getQuantityElement().add(quantityElement);
					session.save(quantityElement);
				}
				objectEventExtensionH.setQuantityList(childQuantityListH);
				session.save(childQuantityListH);
			}
			
			if(objectEventType.getExtension().getSourceList()!=null){
					List<SourceDestType> sourceList = objectEventType.getExtension()
							.getSourceList().getSource();
					sourceListH = new SourceList();
					for (int i = 0; i < sourceList.size(); i++) {
						sourceDestS = new SourceDest(sourceList.get(i).getValue(),
								sourceList.get(i).getType());
						sourceListH.getSource().add(sourceDestS);
						session.save(sourceDestS);
					}
					
					objectEventExtensionH.setSourceList(sourceListH);
					session.save(sourceListH);
			}
			
			if(objectEventType.getExtension().getDestinationList()!=null){
				List<SourceDestType> destinationList = objectEventType
						.getExtension().getDestinationList().getDestination();
				destinationListH = new DestinationList();
				for (int i = 0; i < destinationList.size(); i++) {
					sourceDestD = new SourceDest(destinationList.get(i).getValue(),
							destinationList.get(i).getType());
					destinationListH.getDestination().add(sourceDestD);
					session.save(sourceDestD);
				}
	
				objectEventExtensionH.setDestinationList(destinationListH);
				session.save(destinationListH);	
			}
			

			if (objectEventType.getExtension().getIlmd() != null) {
				ILMD iLMD = new ILMD();
				if(objectEventType.getExtension().getIlmd().getAny()!=null){
					List<Object> objectList=objectEventType.getExtension().getIlmd().getAny();
					List<ExtensionMap> extensionMapList=new ArrayList<>();
					WriteUtility.getAnyObject(objectList,extensionMapList);
					ExtensionMaps extensionMaps= new ExtensionMaps();
					extensionMaps.setExtensionMapList(extensionMapList);
					ExtensionMap extensionMap;
					for(int i=0;i<extensionMapList.size();i++){
						extensionMap=new ExtensionMap();
						extensionMap=extensionMapList.get(i);
						session.save(extensionMap);
					}
					session.save(extensionMaps);
					iLMD.setExtensionMaps(extensionMaps);
				}
				
				
//				List<Object> objectList=objectEventType.getAny();
//				
//				List<ExtensionMap> extensionMapList=new ArrayList<>();
//				
//				WriteUtility.getAnyObject(objectList,extensionMapList);
//				ExtensionMaps extensionMaps= new ExtensionMaps();
//				extensionMaps.setExtensionMapList(extensionMapList);
//				ExtensionMap extensionMap;
//				for(int i=0;i<extensionMapList.size();i++){
//					extensionMap=new ExtensionMap();
//					extensionMap=extensionMapList.get(i);
//					session.save(extensionMap);
//				}
//				objectEvent.setExtensionMaps(extensionMaps);
//				session.save(extensionMaps);
				
//				 if(objectEventType.getIlmd().getOtherAttributes() != null){
//					 Map<QName, String> otherAtfrom=objectEventType.getIlmd().getOtherAttributes();
//					 List<MapExt> mapExtList=new ArrayList<MapExt>();
//						Set<QName> setKeyAll=otherAtfrom.keySet();
//						Iterator iteratorAll=setKeyAll.iterator();
//						 
//						while(iteratorAll.hasNext()){
//							MapExt mapExt= new MapExt();
//							QName keyname=(QName) iteratorAll.next();
//							mapExt.setType(keyname.getNamespaceURI());
//							String value=otherAtfrom.get(keyname);
//							mapExt.setValue(otherAtfrom.get(keyname));
//							float f=0;
//							
//							try{
//							 f=Float.parseFloat(otherAtfrom.get(keyname)); 
//							}
//							catch(NumberFormatException e){	
//							}
//							try{
//								DateFormat format=new SimpleDateFormat("MMMM d, yyyy");
//								Date date= format.parse(value);
//								mapExt.setTimeValue(date);
//							}
//							catch (ParseException e) {
//								
//							}
//							mapExt.setFloatValue(f);
//							
//							
//							mapExtList.add(mapExt);
//							session.save(mapExt);
//						}
//						iLMD.setMapExt(mapExtList);
//				 }
				
				if (objectEventType.getExtension().getIlmd().getExtension() != null) {
					ILMDExtension iLMDExtension = new ILMDExtension();
					
					session.save(iLMDExtension);
					iLMD.setExtension(iLMDExtension);

				}
				objectEventExtensionH.setIlmd(iLMD);
				session.save(iLMD);
			}

			//objectEventExtensionH.setObjectEvent(objectEvent);
			
			/*
			  if(objectEventType.getExtension().getExtension() != null){
			  ObjectEventExtension2 objectEventExtension2= 
					  new  ObjectEventExtension2();
				
				 if(objectEventType.getExtension().getExtension().getOtherAttributes() != null){
					 Map<QName, String> otherAtfrom=objectEventType.getExtension().getExtension().getOtherAttributes();
					 List<MapExt> mapExtList=new ArrayList<MapExt>();
						Set<QName> setKeyAll=otherAtfrom.keySet();
						Iterator iteratorAll=setKeyAll.iterator();
						 
						while(iteratorAll.hasNext()){
							MapExt mapExt= new MapExt();
							QName keyname=(QName) iteratorAll.next();
							mapExt.setType(keyname.getNamespaceURI());
							String value=otherAtfrom.get(keyname);
							mapExt.setValue(otherAtfrom.get(keyname));
							float f=0;
							
							try{
							 f=Float.parseFloat(otherAtfrom.get(keyname)); 
							}
							catch(NumberFormatException e){	
							}
							try{
								DateFormat format=new SimpleDateFormat("MMMM d, yyyy");
								Date date= format.parse(value);
								mapExt.setTimeValue(date);
							}
							catch (ParseException e) {
								
							}
							mapExt.setFloatValue(f);
							
							
							mapExtList.add(mapExt);
							session.save(mapExt);
						}
						objectEventExtension2.setMapExt(mapExtList);
				 }
			  
			  session.save(objectEventExtension2);
			  objectEventExtensionH.setExtension(objectEventExtension2);
			  
			  }*/
			  
			  session.save(objectEventExtensionH);
			  objectEvent.setExtension(objectEventExtensionH);

		}

		// chield epcs ObjectEventEPCs
		if (objectEventType.getEpcList() != null) {
			EPCListType epcs = objectEventType.getEpcList();
			List<EPC> epcList = epcs.getEpc();
			EPCN epctn;
			for (int i = 0; i < epcList.size(); i++) {
				epctn = new EPCN(epcList.get(i).getValue());
				objectEventEPCs.getEpc().add(epctn);
				session.save(epctn);
			}
			// aggregationEvent.setChildEPCs(aggregationEventEPCs);
			//objectEventEPCs.setObjectEvent(objectEvent);
			session.save(objectEventEPCs);
			objectEvent.setEpcList(objectEventEPCs);

		}
		//ObjectEvent any
		if(objectEventType.getAny()!=null){
			List<Object> objectList=objectEventType.getAny();
			
			List<ExtensionMap> extensionMapList=new ArrayList<>();
			
			WriteUtility.getAnyObject(objectList,extensionMapList);
			ExtensionMaps extensionMaps= new ExtensionMaps();
			extensionMaps.setExtensionMapList(extensionMapList);
			ExtensionMap extensionMap;
			for(int i=0;i<extensionMapList.size();i++){
				extensionMap=new ExtensionMap();
				extensionMap=extensionMapList.get(i);
				session.save(extensionMap);
			}
			objectEvent.setExtensionMaps(extensionMaps);
			session.save(extensionMaps);
		}
		
		session.save(objectEvent);
		tx.commit();
		session.close();

	}
	
//================================================================================================================================
	@SuppressWarnings("rawtypes")
	public void save(QuantityEventType quantityEventType) {
		System.out.println("Quantity Event capture operation");
		Session session = getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		QuantityEvent quantityEvent = new QuantityEvent();

		ReadPoint readpointH;
		ReadPointExtension readPointExtensionH;
		BusinessTransactionList businessTransactionList;
		BusinessTransaction businessTransaction;
		BusinessLocation businessLocationH;
		BusinessLocationExtension businessLocationExtensionH;

		// Event Time
		if (quantityEventType.getEventTime() != null) {
			quantityEvent.setEventTime(quantityEventType.getEventTime()
					.toGregorianCalendar().getTime());
		}

		// Record Time
		GregorianCalendar gRecordTime = new GregorianCalendar();
		XMLGregorianCalendar recordTime;
		try {
			recordTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(
					gRecordTime);
			quantityEvent.setRecordTime(recordTime.toGregorianCalendar()
					.getTime());
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
		// Aggregation Time offset
		if (quantityEventType.getEventTimeZoneOffset() != null) {
			quantityEvent.setEventTimeZoneOffset(quantityEventType
					.getEventTimeZoneOffset());
		}
		// epc class
		if (quantityEventType.getEpcClass() != null) {
			quantityEvent.setEpcClass(quantityEventType.getEpcClass());
		}
		
		//BaseExtension
		if(quantityEventType.getBaseExtension() !=null){
			EPCISEventExtension ePCISEventExtension=new EPCISEventExtension();
			ePCISEventExtension.setEventID(quantityEventType.getBaseExtension().getEventID());
			if(quantityEventType.getBaseExtension().getErrorDeclaration()!=null){
				ErrorDeclaration errorDeclaration=new ErrorDeclaration();
				errorDeclaration.setDeclarationTime(quantityEventType.getBaseExtension()
						.getErrorDeclaration().getDeclarationTime()
						.toGregorianCalendar().getTime());
				errorDeclaration.setReason(quantityEventType.getBaseExtension().getErrorDeclaration().getReason());
				if(quantityEventType.getBaseExtension().getErrorDeclaration().getCorrectiveEventIDs()!=null){
					
					CorrectiveEventID CorrectiveEventID;
					CorrectiveEventIDs correctiveEventIDs=new CorrectiveEventIDs(); 
					List<CorrectiveEventID> correctiveEventIDList = new ArrayList<CorrectiveEventID>();
					if(quantityEventType.getBaseExtension().getErrorDeclaration().getCorrectiveEventIDs().getCorrectiveEventID()!=null){
						List<String> ceidList=quantityEventType.getBaseExtension().getErrorDeclaration().getCorrectiveEventIDs().getCorrectiveEventID();
						for(int i=0; i<ceidList.size();i++){
							CorrectiveEventID=new CorrectiveEventID();
							CorrectiveEventID.setCorrectiveEventID(ceidList.get(i));
							session.save(CorrectiveEventID);
							correctiveEventIDList.add(CorrectiveEventID);
						}
						correctiveEventIDs.setCorrectiveEventID(correctiveEventIDList);
					}
					errorDeclaration.setCorrectiveEventIDs(correctiveEventIDs);
					session.save(correctiveEventIDs);
					
				}
				if(quantityEventType.getBaseExtension().getErrorDeclaration().getExtension()!=null){
					ErrorDeclarationExtension errorDeclarationExtension= new ErrorDeclarationExtension();
					
					errorDeclaration.setExtension(errorDeclarationExtension);
					session.save(errorDeclarationExtension);
				}
				//Error Declaration Any
				if(quantityEventType.getBaseExtension().getErrorDeclaration().getAny()!=null){
					List<Object> objectList=quantityEventType.getBaseExtension().getErrorDeclaration().getAny();
					List<ExtensionMap> extensionMapList=new ArrayList<>();
					WriteUtility.getAnyObject(objectList,extensionMapList);
					ExtensionMaps extensionMaps= new ExtensionMaps();
					extensionMaps.setExtensionMapList(extensionMapList);
					ExtensionMap extensionMap;
					for(int i=0;i<extensionMapList.size();i++){
						extensionMap=new ExtensionMap();
						extensionMap=extensionMapList.get(i);
						session.save(extensionMap);
					}
					session.save(extensionMaps);
					errorDeclaration.setExtensionMaps(extensionMaps);
				}
				
				ePCISEventExtension.setErrorDeclaration(errorDeclaration);
				session.save(errorDeclaration);
			}
			if(quantityEventType.getBaseExtension().getExtension()!=null){
				EPCISEventExtension2 epcisEventExtension2= new EPCISEventExtension2();
				ePCISEventExtension.setExtension(epcisEventExtension2);
				session.save(epcisEventExtension2);
			}
			quantityEvent.setBaseExtension(ePCISEventExtension);
			session.save(ePCISEventExtension);
		}
		
		// action
		
		// quantity

		quantityEvent.setQuantity(quantityEventType.getQuantity());

		// Business step
		if (quantityEventType.getBizStep() != null) {
			quantityEvent.setBizStep(quantityEventType.getBizStep());
		}
		// Disposition
		if (quantityEventType.getDisposition() != null) {
			quantityEvent.setDisposition(quantityEventType.getDisposition());
		}
		// read point
		if (quantityEventType.getReadPoint() != null) {
			readpointH = new ReadPoint(quantityEventType.getReadPoint().getId());
			
			
			 if(quantityEventType.getReadPoint().getExtension() !=null){
			  readPointExtensionH=new ReadPointExtension();
			  session.save(readPointExtensionH);
				readpointH.setExtension(readPointExtensionH);
			  }
			 quantityEvent.setReadPoint(readpointH);
			 session.save(readpointH);
		}

		// business transaction
		if (quantityEventType.getBizTransactionList() != null) {
			List<BusinessTransactionType> bizTransaction = quantityEventType
					.getBizTransactionList().getBizTransaction();
			businessTransactionList = new BusinessTransactionList();

			for (int i = 0; i < bizTransaction.size(); i++) {
				businessTransaction = new BusinessTransaction(bizTransaction
						.get(i).getValue(), bizTransaction.get(i).getType());
				businessTransactionList.getBizTransaction().add(
						businessTransaction);
				session.save(businessTransaction);
			}
			quantityEvent.setBizTransactionList(businessTransactionList);
			session.save(businessTransactionList);
		}

		// Business location
		if (quantityEventType.getBizLocation() != null) {
			businessLocationH = new BusinessLocation(quantityEventType
					.getBizLocation().getId());

			if (quantityEventType.getBizLocation().getExtension() != null) {
				businessLocationExtensionH = new BusinessLocationExtension();

					
					session.save(businessLocationExtensionH);
					businessLocationH.setExtension(businessLocationExtensionH);
				}
			quantityEvent.setBizLocation(businessLocationH);
			session.save(businessLocationH);
			}

		// quantity Event Extension
				if (quantityEventType.getExtension() != null) {
					QuantityEventExtension quantityEventExtension = new QuantityEventExtension();
//					 if(quantityEventType.getExtension().getOtherAttributes() != null){
//						 Map<QName, String> otherAtfrom=quantityEventType.getExtension().getOtherAttributes();
//						 List<MapExt> mapExtList=new ArrayList<MapExt>();
//							Set<QName> setKeyAll=otherAtfrom.keySet();
//							Iterator iteratorAll=setKeyAll.iterator();
//							 
//							while(iteratorAll.hasNext()){
//								MapExt mapExt= new MapExt();
//								QName keyname=(QName) iteratorAll.next();
//								mapExt.setType(keyname.getNamespaceURI());
//								String value=otherAtfrom.get(keyname);
//								mapExt.setValue(otherAtfrom.get(keyname));
//								float f=0;
//								
//								try{
//								 f=Float.parseFloat(otherAtfrom.get(keyname)); 
//								}
//								catch(NumberFormatException e){	
//								}
//								try{
//									DateFormat format=new SimpleDateFormat("MMMM d, yyyy");
//									Date date= format.parse(value);
//									mapExt.setTimeValue(date);
//								}
//								catch (ParseException e) {
//									
//								}
//								mapExt.setFloatValue(f);
//								
//								
//								mapExtList.add(mapExt);
//								session.save(mapExt);
//							}
//							quantityEventExtension.setMapExt(mapExtList);
//					 }
					session.save(quantityEventExtension);
					quantityEvent.setExtension(quantityEventExtension);
				}

				
				if(quantityEventType.getAny()!=null){
					List<Object> objectList=quantityEventType.getAny();
					
					List<ExtensionMap> extensionMapList=new ArrayList<>();
					
					WriteUtility.getAnyObject(objectList,extensionMapList);
					ExtensionMaps extensionMaps= new ExtensionMaps();
					extensionMaps.setExtensionMapList(extensionMapList);
					ExtensionMap extensionMap;
					for(int i=0;i<extensionMapList.size();i++){
						extensionMap=new ExtensionMap();
						extensionMap=extensionMapList.get(i);
						session.save(extensionMap);
					}
					quantityEvent.setExtensionMaps(extensionMaps);
					session.save(extensionMaps);
				}
				
		session.save(quantityEvent);
		tx.commit();
		session.close();

	}
	
//=======================================================================================================================	
	@SuppressWarnings("rawtypes")
	public void save(TransactionEventType transactionEventType) {
		System.out.println("Transaction Event capture operation");
		Session session = getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();

		TransactionEvent transactionEvent = new TransactionEvent();
		//TransactionEventEPCs transactionEventEPCs = new TransactionEventEPCs();
		EPCList transactionEventEPCs=new EPCList();
		
		ReadPoint readpointH;
		ReadPointExtension readPointExtensionH;
		BusinessTransactionList businessTransactionList;
		BusinessTransaction businessTransaction;
		BusinessLocation businessLocationH;
		BusinessLocationExtension businessLocationExtensionH;

		TransactionEventExtension transactionEventExtensionH;
   
		QuantityList childQuantityListH;
		SourceList sourceListH;
		DestinationList destinationListH;
		QuantityElement quantityElement;
		SourceDest sourceDestS, sourceDestD;
		// Event Time
		if (transactionEventType.getEventTime() != null) {
			transactionEvent.setEventTime(transactionEventType.getEventTime()
					.toGregorianCalendar().getTime());
		}

		// Record Time
		GregorianCalendar gRecordTime = new GregorianCalendar();
		XMLGregorianCalendar recordTime;
		try {
			recordTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(
					gRecordTime);
			transactionEvent.setRecordTime(recordTime.toGregorianCalendar()
					.getTime());
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
		// Aggregation Time offset
		if (transactionEventType.getEventTimeZoneOffset() != null) {
			transactionEvent.setEventTimeZoneOffset(transactionEventType
					.getEventTimeZoneOffset());
		}

		// Parent ID
		if (transactionEventType.getParentID() != null) {
			transactionEvent.setParentID(transactionEventType.getParentID());
		}
		
		//BaseExtension
		if(transactionEventType.getBaseExtension() !=null){
			EPCISEventExtension ePCISEventExtension=new EPCISEventExtension();
			ePCISEventExtension.setEventID(transactionEventType.getBaseExtension().getEventID());
			if(transactionEventType.getBaseExtension().getErrorDeclaration()!=null){
				ErrorDeclaration errorDeclaration=new ErrorDeclaration();
				errorDeclaration.setDeclarationTime(transactionEventType.getBaseExtension()
						.getErrorDeclaration().getDeclarationTime()
						.toGregorianCalendar().getTime());
				errorDeclaration.setReason(transactionEventType.getBaseExtension().getErrorDeclaration().getReason());
				if(transactionEventType.getBaseExtension().getErrorDeclaration().getCorrectiveEventIDs()!=null){
					
					CorrectiveEventID CorrectiveEventID;
					CorrectiveEventIDs correctiveEventIDs=new CorrectiveEventIDs(); 
					List<CorrectiveEventID> correctiveEventIDList = new ArrayList<CorrectiveEventID>();
					if(transactionEventType.getBaseExtension().getErrorDeclaration().getCorrectiveEventIDs().getCorrectiveEventID()!=null){
						List<String> ceidList=transactionEventType.getBaseExtension().getErrorDeclaration().getCorrectiveEventIDs().getCorrectiveEventID();
						for(int i=0; i<ceidList.size();i++){
							CorrectiveEventID=new CorrectiveEventID();
							CorrectiveEventID.setCorrectiveEventID(ceidList.get(i));
							session.save(CorrectiveEventID);
							correctiveEventIDList.add(CorrectiveEventID);
						}
						correctiveEventIDs.setCorrectiveEventID(correctiveEventIDList);
					}
					errorDeclaration.setCorrectiveEventIDs(correctiveEventIDs);
					session.save(correctiveEventIDs);
					
				}
				if(transactionEventType.getBaseExtension().getErrorDeclaration().getExtension()!=null){
					ErrorDeclarationExtension errorDeclarationExtension= new ErrorDeclarationExtension();
					
					errorDeclaration.setExtension(errorDeclarationExtension);
					session.save(errorDeclarationExtension);
				}
				//Error Declaration Any
				if(transactionEventType.getBaseExtension().getErrorDeclaration().getAny()!=null){
					List<Object> objectList=transactionEventType.getBaseExtension().getErrorDeclaration().getAny();
					List<ExtensionMap> extensionMapList=new ArrayList<>();
					WriteUtility.getAnyObject(objectList,extensionMapList);
					ExtensionMaps extensionMaps= new ExtensionMaps();
					extensionMaps.setExtensionMapList(extensionMapList);
					ExtensionMap extensionMap;
					for(int i=0;i<extensionMapList.size();i++){
						extensionMap=new ExtensionMap();
						extensionMap=extensionMapList.get(i);
						session.save(extensionMap);
					}
					session.save(extensionMaps);
					errorDeclaration.setExtensionMaps(extensionMaps);
				}
				
				ePCISEventExtension.setErrorDeclaration(errorDeclaration);
				session.save(errorDeclaration);
			}
			if(transactionEventType.getBaseExtension().getExtension()!=null){
				EPCISEventExtension2 epcisEventExtension2= new EPCISEventExtension2();
				ePCISEventExtension.setExtension(epcisEventExtension2);
				session.save(epcisEventExtension2);
			}
			transactionEvent.setBaseExtension(ePCISEventExtension);
			session.save(ePCISEventExtension);
		}

		// action
		transactionEvent.setAction(Action.fromValue(transactionEventType
				.getAction().name()));
		// Business step
		if (transactionEventType.getBizStep() != null) {
			transactionEvent.setBizStep(transactionEventType.getBizStep());
		}
		// Disposition
		if (transactionEventType.getDisposition() != null) {
			transactionEvent.setDisposition(transactionEventType
					.getDisposition());
		}

		// read point
		if (transactionEventType.getReadPoint() != null) {
			readpointH = new ReadPoint(transactionEventType.getReadPoint()
					.getId());
			

			if (transactionEventType.getReadPoint().getExtension() != null) {
				readPointExtensionH = new ReadPointExtension();

				session.save(readPointExtensionH);
				readpointH.setExtension(readPointExtensionH);
				
			}
			
			transactionEvent.setReadPoint(readpointH);
			session.save(readpointH);
		}

		// business transaction
		if (transactionEventType.getBizTransactionList() != null) {
			List<BusinessTransactionType> bizTransaction = transactionEventType
					.getBizTransactionList().getBizTransaction();
			businessTransactionList = new BusinessTransactionList();

			for (int i = 0; i < bizTransaction.size(); i++) {
				businessTransaction = new BusinessTransaction(bizTransaction
						.get(i).getValue(), bizTransaction.get(i).getType());
				businessTransactionList.getBizTransaction().add(
						businessTransaction);
				session.save(businessTransaction);
			}
			transactionEvent.setBizTransactionList(businessTransactionList);
			session.save(businessTransactionList);
		}

		// Business location
		if (transactionEventType.getBizLocation() != null) {
			businessLocationH = new BusinessLocation(transactionEventType
					.getBizLocation().getId());

			if (transactionEventType.getBizLocation() != null) {
				businessLocationExtensionH = new BusinessLocationExtension();
			
				session.save(businessLocationExtensionH);
				businessLocationH.setExtension(businessLocationExtensionH);
			}
			transactionEvent.setBizLocation(businessLocationH);
			session.save(businessLocationH);
		}

	

		

		// Transaction Event Extension
		if (transactionEventType.getExtension() != null) {
			transactionEventExtensionH = new TransactionEventExtension();

			if(transactionEventType.getExtension().getQuantityList() !=null){
				List<QuantityElementType> quantityList = transactionEventType
						.getExtension().getQuantityList().getQuantityElement();
				childQuantityListH = new QuantityList();
				for (int i = 0; i < quantityList.size(); i++) {
					quantityElement = new QuantityElement();
					quantityElement.setEpcClass(quantityList.get(i).getEpcClass());
					quantityElement.setQuantity(quantityList.get(i).getQuantity().floatValue());
					quantityElement.setUom(quantityList.get(i).getUom());
					childQuantityListH.getQuantityElement().add(quantityElement);
					session.save(quantityElement);
				}
				transactionEventExtensionH.setQuantityList(childQuantityListH);
				session.save(childQuantityListH);
			}

			if(transactionEventType.getExtension().getSourceList() != null){
				List<SourceDestType> sourceList = transactionEventType
						.getExtension().getSourceList().getSource();
				sourceListH = new SourceList();
				for (int i = 0; i < sourceList.size(); i++) {
					sourceDestS = new SourceDest(sourceList.get(i).getValue(),
							sourceList.get(i).getType());
					sourceListH.getSource().add(sourceDestS);
					session.save(sourceDestS);
				}
	
				transactionEventExtensionH.setSourceList(sourceListH);
				session.save(sourceListH);
			}

			if(transactionEventType.getExtension().getDestinationList() != null){
				List<SourceDestType> destinationList = transactionEventType
						.getExtension().getDestinationList().getDestination();
				destinationListH = new DestinationList();
				for (int i = 0; i < destinationList.size(); i++) {
					sourceDestD = new SourceDest(destinationList.get(i).getValue(),
							destinationList.get(i).getType());
					destinationListH.getDestination().add(sourceDestD);
					session.save(sourceDestD);
				}
	
				transactionEventExtensionH.setDestinationList(destinationListH);
				session.save(destinationListH);
			}


			

			if (transactionEventType.getExtension().getExtension() != null) {
				TransactionEventExtension2 transactionEventExtension2 = new TransactionEventExtension2();
				
//				 if(transactionEventType.getExtension().getExtension().getOtherAttributes() != null){
//					 Map<QName, String> otherAtfrom=transactionEventType.getExtension().getExtension().getOtherAttributes();
//					 List<MapExt> mapExtList=new ArrayList<MapExt>();
//						Set<QName> setKeyAll=otherAtfrom.keySet();
//						Iterator iteratorAll=setKeyAll.iterator();
//						 
//						while(iteratorAll.hasNext()){
//							MapExt mapExt= new MapExt();
//							QName keyname=(QName) iteratorAll.next();
//							mapExt.setType(keyname.getNamespaceURI());
//							String value=otherAtfrom.get(keyname);
//							mapExt.setValue(otherAtfrom.get(keyname));
//							float f=0;
//							
//							try{
//							 f=Float.parseFloat(otherAtfrom.get(keyname)); 
//							}
//							catch(NumberFormatException e){	
//							}
//							try{
//								DateFormat format=new SimpleDateFormat("MMMM d, yyyy");
//								Date date= format.parse(value);
//								mapExt.setTimeValue(date);
//							}
//							catch (ParseException e) {
//								
//							}
//							mapExt.setFloatValue(f);
//							
//							
//							mapExtList.add(mapExt);
//							session.save(mapExt);
//						}
//						transactionEventExtension2.setMapExt(mapExtList);
//				 }
				//transactionEventExtension2.setTransactionEventExtension(transactionEventExtensionH);
				session.save(transactionEventExtension2);
				transactionEventExtensionH.setExtension(transactionEventExtension2);

			}
			//transactionEventExtensionH.setTransactionEvent(transactionEvent);
			session.save(transactionEventExtensionH);
			transactionEvent.setExtension(transactionEventExtensionH);

		}

		// Child EPCs ObjectEvent EPCs
		if (transactionEventType.getEpcList() != null) {
			EPCListType epcs = transactionEventType.getEpcList();
			List<EPC> epcList = epcs.getEpc();
			EPCN epctn;
			for (int i = 0; i < epcList.size(); i++) {
				epctn = new EPCN(epcList.get(i).getValue());
				transactionEventEPCs.getEpc().add(epctn);
				session.save(epctn);
			}
			//transactionEventEPCs.setTransactionEvent(transactionEvent);
			session.save(transactionEventEPCs);
			transactionEvent.setEpcList(transactionEventEPCs);

		}
		
		//Transaction any extension
		if(transactionEventType.getAny()!=null){
			List<Object> objectList=transactionEventType.getAny();
			
			List<ExtensionMap> extensionMapList=new ArrayList<>();
			
			WriteUtility.getAnyObject(objectList,extensionMapList);
			ExtensionMaps extensionMaps= new ExtensionMaps();
			extensionMaps.setExtensionMapList(extensionMapList);
			ExtensionMap extensionMap;
			for(int i=0;i<extensionMapList.size();i++){
				extensionMap=new ExtensionMap();
				extensionMap=extensionMapList.get(i);
				session.save(extensionMap);
			}
			transactionEvent.setExtensionMaps(extensionMaps);
			session.save(extensionMaps);
		}

		session.save(transactionEvent);
		tx.commit();
		session.close();

	}

//=================================================================================================================================	
	@SuppressWarnings("rawtypes")
	public void save(TransformationEventType transformationEventType) {
		System.out.println("Transformation Event capture operation");
		Session session = getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		TransformationEvent transformationEvent = new TransformationEvent();

		ReadPoint readpointH;
		ReadPointExtension readPointExtensionH;
		BusinessTransactionList businessTransactionList;
		BusinessTransaction businessTransaction;
		BusinessLocation businessLocationH;
		BusinessLocationExtension businessLocationExtensionH;

		// QuantityElement quantityElement;
		SourceDest sourceDestS, sourceDestD;
		// Event Time
		if (transformationEventType.getEventTime() != null) {
			transformationEvent.setEventTime(transformationEventType
					.getEventTime().toGregorianCalendar().getTime());
		}

		// Record Time
		GregorianCalendar gRecordTime = new GregorianCalendar();
		XMLGregorianCalendar recordTime;
		try {
			recordTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(
					gRecordTime);
			transformationEvent.setRecordTime(recordTime.toGregorianCalendar()
					.getTime());
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
		// Aggregation Time offset
		if (transformationEventType.getEventTimeZoneOffset() != null) {
			transformationEvent.setEventTimeZoneOffset(transformationEventType
					.getEventTimeZoneOffset());
		}
		
		
		//BaseExtension
		if(transformationEventType.getBaseExtension() !=null){
			EPCISEventExtension ePCISEventExtension=new EPCISEventExtension();
			ePCISEventExtension.setEventID(transformationEventType.getBaseExtension().getEventID());
			if(transformationEventType.getBaseExtension().getErrorDeclaration()!=null){
				ErrorDeclaration errorDeclaration=new ErrorDeclaration();
				errorDeclaration.setDeclarationTime(transformationEventType.getBaseExtension()
						.getErrorDeclaration().getDeclarationTime()
						.toGregorianCalendar().getTime());
				errorDeclaration.setReason(transformationEventType.getBaseExtension().getErrorDeclaration().getReason());
				if(transformationEventType.getBaseExtension().getErrorDeclaration().getCorrectiveEventIDs()!=null){
					
					CorrectiveEventID CorrectiveEventID;
					CorrectiveEventIDs correctiveEventIDs=new CorrectiveEventIDs(); 
					List<CorrectiveEventID> correctiveEventIDList = new ArrayList<CorrectiveEventID>();
					if(transformationEventType.getBaseExtension().getErrorDeclaration().getCorrectiveEventIDs().getCorrectiveEventID()!=null){
						List<String> ceidList=transformationEventType.getBaseExtension().getErrorDeclaration().getCorrectiveEventIDs().getCorrectiveEventID();
						for(int i=0; i<ceidList.size();i++){
							CorrectiveEventID=new CorrectiveEventID();
							CorrectiveEventID.setCorrectiveEventID(ceidList.get(i));
							session.save(CorrectiveEventID);
							correctiveEventIDList.add(CorrectiveEventID);
						}
						correctiveEventIDs.setCorrectiveEventID(correctiveEventIDList);
					}
					errorDeclaration.setCorrectiveEventIDs(correctiveEventIDs);
					session.save(correctiveEventIDs);
					
				}
				if(transformationEventType.getBaseExtension().getErrorDeclaration().getExtension()!=null){
					ErrorDeclarationExtension errorDeclarationExtension= new ErrorDeclarationExtension();
					
					errorDeclaration.setExtension(errorDeclarationExtension);
					session.save(errorDeclarationExtension);
				}
				//Error Declaration Any
				if(transformationEventType.getBaseExtension().getErrorDeclaration().getAny()!=null){
					List<Object> objectList=transformationEventType.getBaseExtension().getErrorDeclaration().getAny();
					List<ExtensionMap> extensionMapList=new ArrayList<>();
					WriteUtility.getAnyObject(objectList,extensionMapList);
					ExtensionMaps extensionMaps= new ExtensionMaps();
					extensionMaps.setExtensionMapList(extensionMapList);
					ExtensionMap extensionMap;
					for(int i=0;i<extensionMapList.size();i++){
						extensionMap=new ExtensionMap();
						extensionMap=extensionMapList.get(i);
						session.save(extensionMap);
					}
					session.save(extensionMaps);
					errorDeclaration.setExtensionMaps(extensionMaps);
				}
				
				ePCISEventExtension.setErrorDeclaration(errorDeclaration);
				session.save(errorDeclaration);
			}
			if(transformationEventType.getBaseExtension().getExtension()!=null){
				EPCISEventExtension2 epcisEventExtension2= new EPCISEventExtension2();
				ePCISEventExtension.setExtension(epcisEventExtension2);
				session.save(epcisEventExtension2);
			}
			transformationEvent.setBaseExtension(ePCISEventExtension);
			session.save(ePCISEventExtension);
		}

		// Input EPC List
		if (transformationEventType.getInputEPCList() != null) {
			EPCList inputEPCList=new EPCList();
			EPCListType epcs = transformationEventType.getInputEPCList();
			List<EPC> epcList = epcs.getEpc();
			EPCN epcI;
			for (int i = 0; i < epcList.size(); i++) {
				epcI = new EPCN(epcList.get(i).getValue());
				inputEPCList.getEpc().add(epcI);
				session.save(epcI);
			}
			session.save(inputEPCList);
			transformationEvent.setInputEPCList(inputEPCList);
		}
		// Output EPC List
		if (transformationEventType.getOutputEPCList() != null) {
			EPCListType epcs = transformationEventType.getOutputEPCList();
			EPCList outputEPCList=new EPCList();
			List<EPC> epcList = epcs.getEpc();
			EPCN epcI;
			for (int i = 0; i < epcList.size(); i++) {
				epcI = new EPCN(epcList.get(i).getValue());
				outputEPCList.getEpc().add(epcI);
				
				session.save(epcI);
			}
			session.save(outputEPCList);
			transformationEvent.setOutputEPCList(outputEPCList);

		}

		// Input Quantity Element List
		if (transformationEventType.getInputQuantityList()!= null) {

			List<QuantityElementType> quantityListInput = transformationEventType
					.getInputQuantityList().getQuantityElement();
			QuantityList inputQuantityList=new QuantityList();
			QuantityElement quantityElementInput;
			for (int i = 0; i < quantityListInput.size(); i++) {
				quantityElementInput = new QuantityElement();
				quantityElementInput.setEpcClass(quantityListInput.get(i).getEpcClass());
				quantityElementInput.setQuantity(quantityListInput.get(i).getQuantity().floatValue());
				quantityElementInput.setUom(quantityListInput.get(i).getUom());
				inputQuantityList.getQuantityElement().add(quantityElementInput);
				
				session.save(quantityElementInput);
			}
			session.save(inputQuantityList);
			transformationEvent.setInputQuantityList(inputQuantityList);

		}

		// Output Quantity Element List
		//if(transformationEventType.getOutputQuantityList())
		if (transformationEventType.getOutputQuantityList() != null) {

			List<QuantityElementType> quantityListOutput = transformationEventType
					.getOutputQuantityList().getQuantityElement();
			QuantityList outputQuantityList=new QuantityList();
			QuantityElement quantityElementOutput;
			for (int i = 0; i < quantityListOutput.size(); i++) {
				quantityElementOutput = new QuantityElement();
				quantityElementOutput.setEpcClass(quantityListOutput.get(i).getEpcClass());
				quantityElementOutput.setQuantity(quantityListOutput.get(i).getQuantity().floatValue());
				quantityElementOutput.setUom(quantityListOutput.get(i).getUom());
				outputQuantityList.getQuantityElement().add(quantityElementOutput);
				
				session.save(quantityElementOutput);
			}
			session.save(outputQuantityList);
			transformationEvent.setOutputQuantityList(outputQuantityList);

		}

		// transformationID
		transformationEvent.setTransformationID(transformationEventType
				.getTransformationID());
		// bizStep
		transformationEvent.setBizStep(transformationEventType.getBizStep());
		// disposition
		transformationEvent.setDisposition(transformationEventType
				.getDisposition());

		// read point
		if (transformationEventType.getReadPoint() != null) {
			readpointH = new ReadPoint(transformationEventType.getReadPoint()
					.getId());
			

			if (transformationEventType.getReadPoint().getExtension() != null) {
				readPointExtensionH = new ReadPointExtension();

					
					session.save(readPointExtensionH);
					readpointH.setExtension(readPointExtensionH);
				}
			transformationEvent.setReadPoint(readpointH);
			session.save(readpointH);
		}

		// business transaction
		if (transformationEventType.getBizTransactionList() != null) {
			List<BusinessTransactionType> bizTransaction = transformationEventType
					.getBizTransactionList().getBizTransaction();
			businessTransactionList = new BusinessTransactionList();

			for (int i = 0; i < bizTransaction.size(); i++) {
				businessTransaction = new BusinessTransaction(bizTransaction
						.get(i).getValue(), bizTransaction.get(i).getType());
				businessTransactionList.getBizTransaction().add(
						businessTransaction);
				session.save(businessTransaction);
			}
			transformationEvent.setBizTransactionList(businessTransactionList);
			session.save(businessTransactionList);
		}

		// Business location
		if (transformationEventType.getBizLocation() != null) {
			businessLocationH = new BusinessLocation(transformationEventType
					.getBizLocation().getId());


			if (transformationEventType.getBizLocation().getExtension() != null) {
				businessLocationExtensionH = new BusinessLocationExtension();

					session.save(businessLocationExtensionH);
					businessLocationH.setExtension(businessLocationExtensionH);
			}
			transformationEvent.setBizLocation(businessLocationH);
			session.save(businessLocationH);
		}
		// Source List
		if (transformationEventType.getSourceList() != null) {
			List<SourceDestType> sourceList = transformationEventType
					.getSourceList().getSource();
			SourceList sourceListH = new SourceList();
			for (int i = 0; i < sourceList.size(); i++) {
				sourceDestS = new SourceDest(sourceList.get(i).getValue(),
						sourceList.get(i).getType());
				sourceListH.getSource().add(sourceDestS);
				session.save(sourceDestS);
			}

			transformationEvent.setSourceList(sourceListH);
			session.save(sourceListH);
		}

		// Source List
		if (transformationEventType.getDestinationList() != null) {
			List<SourceDestType> destinationList = transformationEventType
					.getDestinationList().getDestination();
			DestinationList destinationListH = new DestinationList();
			for (int i = 0; i < destinationList.size(); i++) {
				sourceDestD = new SourceDest(destinationList.get(i).getValue(),
						destinationList.get(i).getType());
				destinationListH.getDestination().add(sourceDestD);
				session.save(sourceDestD);
			}

			transformationEvent.setDestinationList(destinationListH);
			session.save(destinationListH);
		}
		// ilmd
		if (transformationEventType.getIlmd() != null) {
			ILMD iLMD = new ILMD();
			
			if(transformationEventType.getIlmd().getAny()!=null){
				List<Object> objectList=transformationEventType.getIlmd().getAny();
				List<ExtensionMap> extensionMapList=new ArrayList<>();
				WriteUtility.getAnyObject(objectList,extensionMapList);
				ExtensionMaps extensionMaps= new ExtensionMaps();
				extensionMaps.setExtensionMapList(extensionMapList);
				ExtensionMap extensionMap;
				for(int i=0;i<extensionMapList.size();i++){
					extensionMap=new ExtensionMap();
					extensionMap=extensionMapList.get(i);
					session.save(extensionMap);
				}
				session.save(extensionMaps);
				iLMD.setExtensionMaps(extensionMaps);
			}
//		 if(transformationEventType.getIlmd().getOtherAttributes() != null){
//				 Map<QName, String> otherAtfrom=transformationEventType.getIlmd().getOtherAttributes();
//				 List<MapExt> mapExtList=new ArrayList<MapExt>();
//					Set<QName> setKeyAll=otherAtfrom.keySet();
//					Iterator iteratorAll=setKeyAll.iterator();
//					 
//					while(iteratorAll.hasNext()){
//						MapExt mapExt= new MapExt();
//						QName keyname=(QName) iteratorAll.next();
//						mapExt.setType(keyname.getNamespaceURI());
//						String value=otherAtfrom.get(keyname);
//						mapExt.setValue(otherAtfrom.get(keyname));
//						float f=0;
//						
//						try{
//						 f=Float.parseFloat(otherAtfrom.get(keyname)); 
//						}
//						catch(NumberFormatException e){	
//						}
//						try{
//							DateFormat format=new SimpleDateFormat("MMMM d, yyyy");
//							Date date= format.parse(value);
//							mapExt.setTimeValue(date);
//						}
//						catch (ParseException e) {
//							
//						}
//						mapExt.setFloatValue(f);
//						
//						
//						mapExtList.add(mapExt);
//						session.save(mapExt);
//					}
//					iLMD.setMapExt(mapExtList);
//			 }
			
			if (transformationEventType.getIlmd().getExtension() != null) {
				ILMDExtension iLMDExtension = new ILMDExtension();
				
				session.save(iLMDExtension);
				iLMD.setExtension(iLMDExtension);

			}
			transformationEvent.setIlmd(iLMD);
			session.save(iLMD);

		}

		

		// Transformation Event Extension
		if (transformationEventType.getExtension() != null) {
			TransformationEventExtension transformationEventExtension = new TransformationEventExtension();
//			 if(transformationEventType.getExtension().getOtherAttributes() != null){
//				 Map<QName, String> otherAtfrom=transformationEventType.getExtension().getOtherAttributes();
//				 List<MapExt> mapExtList=new ArrayList<MapExt>();
//					Set<QName> setKeyAll=otherAtfrom.keySet();
//					Iterator iteratorAll=setKeyAll.iterator();
//					 
//					while(iteratorAll.hasNext()){
//						MapExt mapExt= new MapExt();
//						QName keyname=(QName) iteratorAll.next();
//						mapExt.setType(keyname.getNamespaceURI());
//						String value=otherAtfrom.get(keyname);
//						mapExt.setValue(otherAtfrom.get(keyname));
//						float f=0;
//						
//						try{
//						 f=Float.parseFloat(otherAtfrom.get(keyname)); 
//						}
//						catch(NumberFormatException e){	
//						}
//						try{
//							DateFormat format=new SimpleDateFormat("MMMM d, yyyy");
//							Date date= format.parse(value);
//							mapExt.setTimeValue(date);
//						}
//						catch (ParseException e) {
//							
//						}
//						mapExt.setFloatValue(f);
//						
//						
//						mapExtList.add(mapExt);
//						session.save(mapExt);
//					}
//					transformationEventExtension.setMapExt(mapExtList);
//			 }
			session.save(transformationEventExtension);
			transformationEvent.setExtension(transformationEventExtension);
		}
		
		//object any extension
		if(transformationEventType.getAny()!=null){
			List<Object> objectList=transformationEventType.getAny();
			
			List<ExtensionMap> extensionMapList=new ArrayList<>();
			
			WriteUtility.getAnyObject(objectList,extensionMapList);
			ExtensionMaps extensionMaps= new ExtensionMaps();
			extensionMaps.setExtensionMapList(extensionMapList);
			ExtensionMap extensionMap;
			for(int i=0;i<extensionMapList.size();i++){
				extensionMap=new ExtensionMap();
				extensionMap=extensionMapList.get(i);
				session.save(extensionMap);
			}
			transformationEvent.setExtensionMaps(extensionMaps);
			session.save(extensionMaps);
		}

		session.save(transformationEvent);
		
		tx.commit();
		session.close();

	}
	

//=============================================================================================================	
	@SuppressWarnings("rawtypes")
	public void save(VocabularyType VocabularyType) {
		System.out.println("Vocabulary capture operation");
		Session session = getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Vocabulary vocabulary = new Vocabulary();
		vocabulary.setType(VocabularyType.getType());
		if (VocabularyType.getVocabularyElementList().getVocabularyElement() != null) {
			List<VocabularyElementType> VocabularyElementListType = VocabularyType
					.getVocabularyElementList().getVocabularyElement();
			VocabularyElementList VocabularyElementList = new VocabularyElementList();
			VocabularyElement vocabularyElement;
			VocabularyElementExtension vocabularyElementExtension;
			for (int i = 0; i < VocabularyElementListType.size(); i++) {

				vocabularyElement = new VocabularyElement(
						VocabularyElementListType.get(i).getId());
				if (VocabularyElementListType.get(i).getAttribute() != null) {
					List<AttributeType> attributeType = VocabularyElementListType
							.get(i).getAttribute();
					Attribute attribute;
//					for (int j = 0; j < attributeType.size(); j++) {
//						attribute = new Attribute(attributeType.get(j)
//								.getValue(), attributeType.get(j).getId());
//						vocabularyElement.getAttribute().add(attribute);
//						session.save(attribute);
//					}
				}
				if (VocabularyElementListType.get(i).getChildren() != null) {
					// IDListType IDListType
					List<String> sIdType = VocabularyElementListType.get(i)
							.getChildren().getId();
					IDList iDList = new IDList();
					for (int k = 0; k < sIdType.size(); k++) {
						iDList.getId().add(sIdType.get(k));
					}
					vocabularyElement.setChildren(iDList);
					session.save(iDList);
				}
				VocabularyElementList.getVocabularyElement().add(
						vocabularyElement);
				
				if (VocabularyElementListType.get(i).getExtension() != null) {
					vocabularyElementExtension = new VocabularyElementExtension();
//					 if(VocabularyElementListType.get(i).getExtension().getOtherAttributes() != null){
//						 Map<QName, String> otherAtfrom=VocabularyElementListType.get(i).getExtension().getOtherAttributes();
//						 List<MapExt> mapExtList=new ArrayList<MapExt>();
//							Set<QName> setKeyAll=otherAtfrom.keySet();
//							Iterator iteratorAll=setKeyAll.iterator();
//							 
//							while(iteratorAll.hasNext()){
//								MapExt mapExt= new MapExt();
//								QName keyname=(QName) iteratorAll.next();
//								mapExt.setType(keyname.getNamespaceURI());
//								String value=otherAtfrom.get(keyname);
//								mapExt.setValue(otherAtfrom.get(keyname));
//								float f=0;
//								
//								try{
//								 f=Float.parseFloat(otherAtfrom.get(keyname)); 
//								}
//								catch(NumberFormatException e){	
//								}
//								try{
//									DateFormat format=new SimpleDateFormat("MMMM d, yyyy");
//									Date date= format.parse(value);
//									mapExt.setTimeValue(date);
//								}
//								catch (ParseException e) {
//									
//								}
//								mapExt.setFloatValue(f);
//								
//								
//								mapExtList.add(mapExt);
//								session.save(mapExt);
//							}
//							vocabularyElementExtension.setMapExt(mapExtList);
//					 }
					session.save(vocabularyElementExtension);
					vocabularyElement.setExtension(vocabularyElementExtension);
				}
				session.save(vocabularyElement);

			}

			vocabulary.setVocabularyElementList(VocabularyElementList);
			session.save(VocabularyElementList);
		}

		

		if (VocabularyType.getExtension() != null) {
			VocabularyExtension vocabularyExtension = new VocabularyExtension();
			
			session.save(vocabularyExtension);
			vocabulary.setExtension(vocabularyExtension);
		}
		
		//Vocabulary any extension
		if(VocabularyType.getAny()!=null){
			List<Object> objectList=VocabularyType.getAny();
			
			List<ExtensionMap> extensionMapList=new ArrayList<>();
			
			WriteUtility.getAnyObject(objectList,extensionMapList);
			ExtensionMaps extensionMaps= new ExtensionMaps();
			extensionMaps.setExtensionMapList(extensionMapList);
			ExtensionMap extensionMap;
			for(int i=0;i<extensionMapList.size();i++){
				extensionMap=new ExtensionMap();
				extensionMap=extensionMapList.get(i);
				session.save(extensionMap);
			}
			vocabulary.setExtensionMaps(extensionMaps);
			session.save(extensionMaps);
		}		
		
		
		session.save(vocabulary);
		tx.commit();
		session.close();

	}

}
