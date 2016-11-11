package org.oliot.epcis.serde.mysql;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Level;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.model.epcis.ActionType;
import org.oliot.model.epcis.AggregationEventExtension2Type;
import org.oliot.model.epcis.AggregationEventExtensionType;
import org.oliot.model.epcis.AggregationEventType;
import org.oliot.model.epcis.AttributeType;
import org.oliot.model.epcis.BusinessLocationExtensionType;
import org.oliot.model.epcis.BusinessLocationType;
import org.oliot.model.epcis.BusinessTransactionListType;
import org.oliot.model.epcis.BusinessTransactionType;
import org.oliot.model.epcis.CorrectiveEventIDsType;
import org.oliot.model.epcis.DestinationListType;
import org.oliot.model.epcis.EPC;
import org.oliot.model.epcis.EPCISEventExtension2Type;
import org.oliot.model.epcis.EPCISEventExtensionType;
import org.oliot.model.epcis.EPCListType;
import org.oliot.model.epcis.ErrorDeclarationType;
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
import org.oliot.model.oliot.Action;
import org.oliot.model.oliot.AggregationEvent;
import org.oliot.model.oliot.Attribute;
import org.oliot.model.oliot.BusinessTransaction;
import org.oliot.model.oliot.CorrectiveEventID;
import org.oliot.model.oliot.EPCList;
import org.oliot.model.oliot.EPCN;
import org.oliot.model.oliot.ExtensionMap;
//import org.oliot.model.oliot.MapExt;
import org.oliot.model.oliot.ObjectEvent;
import org.oliot.model.oliot.QuantityElement;
import org.oliot.model.oliot.QuantityEvent;
import org.oliot.model.oliot.SensingElement;
import org.oliot.model.oliot.SensorEvent;
import org.oliot.model.oliot.SourceDest;
import org.oliot.model.oliot.TransactionEvent;
import org.oliot.model.oliot.TransformationEvent;
import org.oliot.model.oliot.Vocabulary;
import org.oliot.model.oliot.VocabularyElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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

public class EventToEventTypeConverter {
	
	
	public AggregationEventType convert(AggregationEvent aggregationEvent) {
		try{
		AggregationEventType aggregationEventType = new AggregationEventType();
		
		// Event Time
		if (aggregationEvent.getEventTime() != null) {
			GregorianCalendar eventTimeGerogy=new GregorianCalendar();
			eventTimeGerogy.setTime(aggregationEvent.getEventTime());
			XMLGregorianCalendar eventTimeXMLG=DatatypeFactory.newInstance().newXMLGregorianCalendar(eventTimeGerogy);
			aggregationEventType.setEventTime(eventTimeXMLG);
		}
		
		if (aggregationEvent.getRecordTime()!=null){
			GregorianCalendar recordTimeGerogy=new GregorianCalendar();
			recordTimeGerogy.setTime(aggregationEvent.getRecordTime());
			XMLGregorianCalendar recordTimeXMLG=DatatypeFactory.newInstance().newXMLGregorianCalendar(recordTimeGerogy);
			aggregationEventType.setRecordTime(recordTimeXMLG);
		}
		
		// Aggregation Time offset
		if (aggregationEvent.getEventTimeZoneOffset() != null) {
			aggregationEventType.setEventTimeZoneOffset(aggregationEvent
					.getEventTimeZoneOffset());
		}
		
		
		// Parent ID
		if (aggregationEvent.getParentID() != null) {
			aggregationEventType.setParentID(aggregationEvent.getParentID());
		}

		// Child EPCs
		if (aggregationEvent.getChildEPCs() != null) {
			EPCListType childEPCsEventEPCs = new EPCListType();
			EPCList epcs = aggregationEvent.getChildEPCs();
			List<EPCN> epcList = epcs.getEpc();
			EPC epctn;
			for (int i = 0; i < epcList.size(); i++) {
				epctn = new EPC(epcList.get(i).getValue());
				childEPCsEventEPCs.getEpc().add(epctn);
				
			}
			aggregationEventType.setChildEPCs(childEPCsEventEPCs);

		}
		
		//Base EPCISEventExtension
		if(aggregationEvent.getBaseExtension()!=null){
			EPCISEventExtensionType epcisEventExtensionType=new EPCISEventExtensionType();
			epcisEventExtensionType.setEventID(aggregationEvent.getBaseExtension().getEventID());
			EPCISEventExtension2Type epcisEventExtension2Type=new EPCISEventExtension2Type();
			epcisEventExtensionType.setExtension(epcisEventExtension2Type);
			if(aggregationEvent.getBaseExtension().getErrorDeclaration()!=null){
				ErrorDeclarationType errorDeclarationType=new ErrorDeclarationType();
				if(aggregationEvent.getBaseExtension().getErrorDeclaration().getDeclarationTime()!=null){
					GregorianCalendar declarationTimeGerogian=new GregorianCalendar();
					declarationTimeGerogian.setTime(aggregationEvent.getBaseExtension().getErrorDeclaration().getDeclarationTime());
					XMLGregorianCalendar declarationTimeXMLG=DatatypeFactory.newInstance().newXMLGregorianCalendar(declarationTimeGerogian);
					errorDeclarationType.setDeclarationTime(declarationTimeXMLG);
				}
				if(aggregationEvent.getBaseExtension().getErrorDeclaration().getReason()!=null){
					errorDeclarationType.setReason(aggregationEvent.getBaseExtension().getErrorDeclaration().getReason());
				}
				if(aggregationEvent.getBaseExtension().getErrorDeclaration().getCorrectiveEventIDs()!=null){
					
					CorrectiveEventIDsType correctiveEventIDsType = new CorrectiveEventIDsType();
					if(aggregationEvent.getBaseExtension().getErrorDeclaration().getCorrectiveEventIDs().getCorrectiveEventID()!=null){
						List<CorrectiveEventID> correctiveEventIDList=aggregationEvent.getBaseExtension().getErrorDeclaration().getCorrectiveEventIDs().getCorrectiveEventID();
						List<String> correctiveEventIDListStr=new ArrayList<String>();
						for(int i=0;i<correctiveEventIDList.size();i++){
							correctiveEventIDListStr.add(correctiveEventIDList.get(i).getCorrectiveEventID());
						}
						correctiveEventIDsType.setCorrectiveEventID(correctiveEventIDListStr);
					}
					errorDeclarationType.setCorrectiveEventIDs(correctiveEventIDsType);
					
				}
				if(aggregationEvent.getBaseExtension().getErrorDeclaration().getExtensionMaps()!=null){
					if(aggregationEvent.getBaseExtension().getErrorDeclaration().getExtensionMaps().getExtensionMapList()!=null){
						List<ExtensionMap> extensionMaps=aggregationEvent.getBaseExtension().getErrorDeclaration().getExtensionMaps()
								.getExtensionMapList();
						try{
							List<Object> elementList=new ArrayList<Object>();
							Document doc;
							DocumentBuilderFactory dbf= DocumentBuilderFactory.newInstance();
							DocumentBuilder builder=dbf.newDocumentBuilder();
							doc=builder.newDocument();
							
							Map<Integer, ExtensionMap> map=new HashMap<Integer, ExtensionMap>();
							
							for(int i=0;i<extensionMaps.size();i++){
								map.put(extensionMaps.get(i).getLeftNodeNumber(), extensionMaps.get(i));
							}
							WriteUtility.leftNodeNumber=1;
							WriteUtility.rightNodeNumber=1;
							int[] level=new int[1];
							level[0]=0;
							if(map.size()>1)
								ReaderUtility.putAny(map,1,level,null,elementList,doc);
							errorDeclarationType.setAny(elementList);
						} catch (ParserConfigurationException e){
							Configuration.logger.log(Level.ERROR, e.toString());
						}	
					}
				}
				
				epcisEventExtensionType.setErrorDeclaration(errorDeclarationType);
			}
			aggregationEventType.setBaseExtension(epcisEventExtensionType);
		}
		
		// action
		aggregationEventType.setAction(ActionType.fromValue(aggregationEvent.getAction().name()));
		// Business step
		if (aggregationEvent.getBizStep() != null) {
			aggregationEventType.setBizStep(aggregationEvent.getBizStep());
		}
		// Disposition
		if (aggregationEvent.getDisposition() != null) {
			aggregationEventType.setDisposition(aggregationEvent
					.getDisposition());
		}
		//EPCISEventExtension
		
		// read point
		if (aggregationEvent.getReadPoint() != null) {
			ReadPointType readpoint = new ReadPointType();
			readpoint.setId(aggregationEvent.getReadPoint().getsId());
			
			  if(aggregationEvent.getReadPoint().getExtension() !=null){
				  ReadPointExtensionType readPointExtension=new  ReadPointExtensionType();
			  readpoint.setExtension(readPointExtension);
			  
			  }
			  aggregationEventType.setReadPoint(readpoint);
		}

		// business transaction
		if (aggregationEvent.getBizTransactionList() != null) {
			List<BusinessTransaction> bizTransaction = aggregationEvent
					.getBizTransactionList().getBizTransaction();
			BusinessTransactionListType businessTransactionList = new BusinessTransactionListType();
			BusinessTransactionType businessTransaction;
			
			for (int i = 0; i < bizTransaction.size(); i++) {
				businessTransaction = new BusinessTransactionType();
				businessTransaction.setType(bizTransaction.get(i).getType());
				businessTransaction.setValue(bizTransaction.get(i).getValue());
				businessTransactionList.getBizTransaction().add(
						businessTransaction);
				}
			aggregationEventType.setBizTransactionList(businessTransactionList);
			
		}
		 
		
		// Business location
		if (aggregationEvent.getBizLocation() != null) {
			BusinessLocationType businessLocationH = new BusinessLocationType();
			businessLocationH.setId(aggregationEvent.getBizLocation().getsId());
			if(aggregationEvent.getBizLocation().getExtension() !=null){
				  BusinessLocationExtensionType businessLocationExtensionH=
						  new  BusinessLocationExtensionType();
			      businessLocationH.setExtension(businessLocationExtensionH);
			  }
			  aggregationEventType.setBizLocation(businessLocationH);
		}


		// Aggregation Event Extension
		if (aggregationEvent.getExtension() != null) {
			AggregationEventExtensionType aggregationEventExtensionH = 
					new AggregationEventExtensionType();
			
			if(aggregationEvent.getExtension().getChildQuantityList()!=null){
				List<QuantityElement> quantityList = aggregationEvent
						.getExtension().getChildQuantityList().getQuantityElement();
				QuantityListType childQuantityListH = new QuantityListType();
				QuantityElementType quantityElement;
				for (int i = 0; i < quantityList.size(); i++) {
					quantityElement = new QuantityElementType();
					quantityElement.setEpcClass(quantityList.get(i).getEpcClass());
					//quantityElement.setQuantity(quantityList.get(i).getQuantity());
					quantityElement.setUom(quantityList.get(i).getUom());
					childQuantityListH.getQuantityElement().add(quantityElement);
					
				}
				aggregationEventExtensionH.setChildQuantityList(childQuantityListH);
				
			}
			 
			if(aggregationEvent.getExtension().getSourceList()!=null){
				List<SourceDest> sourceList = aggregationEvent
						.getExtension().getSourceList().getSource();
				SourceListType sourceListH = new SourceListType();
				SourceDestType sourceDestS;
				for (int i = 0; i < sourceList.size(); i++) {
					sourceDestS = new SourceDestType();
					sourceDestS.setType(sourceList.get(i).getType());
					sourceDestS.setValue(sourceList.get(i).getValue());
					sourceListH.getSource().add(sourceDestS);
				}
	
				aggregationEventExtensionH.setSourceList(sourceListH);
			}
			
			
			if(aggregationEvent.getExtension().getDestinationList()!=null){
				List<SourceDest> destinationList = aggregationEvent
						.getExtension().getDestinationList().getDestination();
				DestinationListType destinationListH = new DestinationListType();
				SourceDestType sourceDestD;
				for (int i = 0; i < destinationList.size(); i++) {
					sourceDestD = new SourceDestType();
					sourceDestD.setType(destinationList.get(i).getType());
					sourceDestD.setValue(destinationList.get(i).getValue());
					destinationListH.getDestination().add(sourceDestD);
				}
	
				aggregationEventExtensionH.setDestinationList(destinationListH);
			}
			

			//aggregationEventExtensionH.setAggregationEvent(aggregationEvent);
			
			
			 if(aggregationEvent.getExtension().getExtension() != null){
				 AggregationEventExtension2Type aggregationEventExtension2=new
						 AggregationEventExtension2Type(); 
				
//				 if(aggregationEvent.getExtension().getExtension().getMapExt() != null){
//					 List<MapExt> mapExtList=aggregationEvent.getExtension().getExtension().getMapExt();
//					 
//					 
//					 Map<QName, String> otherAttribute=new HashMap<QName, String>();
////						for(int i=0;i<mapExtList.size();i++){
////								QName name=new QName(mapExtList.get(i).getType(),"","");
////								otherAttribute.put(name,mapExtList.get(i).getValue());	
////						}
//					 aggregationEventExtension2.setOtherAttributes(otherAttribute);
//				 }
				 
			  aggregationEventExtensionH.setExtension(aggregationEventExtension2);
			 }
			 aggregationEventType.setExtension(aggregationEventExtensionH);
			}
		return aggregationEventType;
		} catch (DatatypeConfigurationException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
		}
		return null;
	}

//========================================================================================================================	
	public ObjectEventType convert(ObjectEvent objectEvent) {

		try{

		ObjectEventType objectEventType = new ObjectEventType();
		
		// Event Time
		if (objectEvent.getEventTime() != null) {
			GregorianCalendar eventTimeGerogy=new GregorianCalendar();
			eventTimeGerogy.setTime(objectEvent.getEventTime());
			XMLGregorianCalendar eventTimeXMLG=DatatypeFactory.newInstance().newXMLGregorianCalendar(eventTimeGerogy);
			objectEventType.setEventTime(eventTimeXMLG);
		}

		if (objectEvent.getRecordTime()!=null){
			GregorianCalendar recordTimeGerogy=new GregorianCalendar();
			recordTimeGerogy.setTime(objectEvent.getRecordTime());
			XMLGregorianCalendar recordTimeXMLG=DatatypeFactory.newInstance().newXMLGregorianCalendar(recordTimeGerogy);
			objectEventType.setRecordTime(recordTimeXMLG);
		}
		// ObjectEvent Time offset
		if (objectEvent.getEventTimeZoneOffset() != null) {
			objectEventType.setEventTimeZoneOffset(objectEvent
					.getEventTimeZoneOffset());
		}
		
		//Base EPCISEventExtension
		if(objectEvent.getBaseExtension()!=null){
			EPCISEventExtensionType epcisEventExtensionType=new EPCISEventExtensionType();
			epcisEventExtensionType.setEventID(objectEvent.getBaseExtension().getEventID());
			EPCISEventExtension2Type epcisEventExtension2Type=new EPCISEventExtension2Type();
			epcisEventExtensionType.setExtension(epcisEventExtension2Type);
			if(objectEvent.getBaseExtension().getErrorDeclaration()!=null){
				ErrorDeclarationType errorDeclarationType=new ErrorDeclarationType();
				if(objectEvent.getBaseExtension().getErrorDeclaration().getDeclarationTime()!=null){
					GregorianCalendar declarationTimeGerogian=new GregorianCalendar();
					declarationTimeGerogian.setTime(objectEvent.getBaseExtension().getErrorDeclaration().getDeclarationTime());
					XMLGregorianCalendar declarationTimeXMLG=DatatypeFactory.newInstance().newXMLGregorianCalendar(declarationTimeGerogian);
					errorDeclarationType.setDeclarationTime(declarationTimeXMLG);
				}
				if(objectEvent.getBaseExtension().getErrorDeclaration().getReason()!=null){
					errorDeclarationType.setReason(objectEvent.getBaseExtension().getErrorDeclaration().getReason());
				}
				if(objectEvent.getBaseExtension().getErrorDeclaration().getCorrectiveEventIDs()!=null){
					
					CorrectiveEventIDsType correctiveEventIDsType = new CorrectiveEventIDsType();
					if(objectEvent.getBaseExtension().getErrorDeclaration().getCorrectiveEventIDs().getCorrectiveEventID()!=null){
						List<CorrectiveEventID> correctiveEventIDList=objectEvent.getBaseExtension().getErrorDeclaration().getCorrectiveEventIDs().getCorrectiveEventID();
						List<String> correctiveEventIDListStr=new ArrayList<String>();
						for(int i=0;i<correctiveEventIDList.size();i++){
							correctiveEventIDListStr.add(correctiveEventIDList.get(i).getCorrectiveEventID());
						}
						correctiveEventIDsType.setCorrectiveEventID(correctiveEventIDListStr);
					}
					errorDeclarationType.setCorrectiveEventIDs(correctiveEventIDsType);
					
				}
				if(objectEvent.getBaseExtension().getErrorDeclaration().getExtensionMaps()!=null){
					if(objectEvent.getBaseExtension().getErrorDeclaration().getExtensionMaps().getExtensionMapList()!=null){
						List<ExtensionMap> extensionMaps=objectEvent.getBaseExtension().getErrorDeclaration().getExtensionMaps()
								.getExtensionMapList();
						try{
							List<Object> elementList=new ArrayList<Object>();
							Document doc;
							DocumentBuilderFactory dbf= DocumentBuilderFactory.newInstance();
							DocumentBuilder builder=dbf.newDocumentBuilder();
							doc=builder.newDocument();
							
							Map<Integer, ExtensionMap> map=new HashMap<Integer, ExtensionMap>();
							
							for(int i=0;i<extensionMaps.size();i++){
								map.put(extensionMaps.get(i).getLeftNodeNumber(), extensionMaps.get(i));
							}
							WriteUtility.leftNodeNumber=1;
							WriteUtility.rightNodeNumber=1;
							int[] level=new int[1];
							level[0]=0;
							if(map.size()>1)
								ReaderUtility.putAny(map,1,level,null,elementList,doc);
							errorDeclarationType.setAny(elementList);
						} catch (ParserConfigurationException e){
							Configuration.logger.log(Level.ERROR, e.toString());
						}	
					}
				}
				
				epcisEventExtensionType.setErrorDeclaration(errorDeclarationType);
			}
			objectEventType.setBaseExtension(epcisEventExtensionType);
		}

		// action
		objectEventType.setAction(ActionType.fromValue(objectEvent.getAction().name()));
		// Business step
		if (objectEvent.getBizStep() != null) {
			objectEventType.setBizStep(objectEvent.getBizStep());
		}
		// Disposition
		if (objectEvent.getDisposition() != null) {
			objectEventType.setDisposition(objectEvent.getDisposition());
		}
		// read point
		if (objectEvent.getReadPoint() != null) {
			ReadPointType readpointH = new ReadPointType();
			readpointH.setId(objectEvent.getReadPoint().getsId());
			  if(objectEvent.getReadPoint().getExtension() !=null){
			  ReadPointExtensionType readPointExtensionH=new ReadPointExtensionType();
				readpointH.setExtension(readPointExtensionH);; 
			  
			  }
			  objectEventType.setReadPoint(readpointH);
		}
		
		// business transaction
		if (objectEvent.getBizTransactionList() != null) {
			List<BusinessTransaction> bizTransaction = objectEvent
					.getBizTransactionList().getBizTransaction();
			BusinessTransactionListType businessTransactionList = new BusinessTransactionListType();
			BusinessTransactionType businessTransaction;
			
			for (int i = 0; i < bizTransaction.size(); i++) {
				businessTransaction = new BusinessTransactionType();
				businessTransaction.setType(bizTransaction.get(i).getType());
				businessTransaction.setValue(bizTransaction.get(i).getValue());
				businessTransactionList.getBizTransaction().add(
						businessTransaction);
				}
			objectEventType.setBizTransactionList(businessTransactionList);
		}
		 
		// Business location
		if (objectEvent.getBizLocation() != null) {
			BusinessLocationType businessLocationH = new BusinessLocationType();
			businessLocationH.setId(objectEvent.getBizLocation().getsId());
			if(objectEvent.getBizLocation().getExtension() !=null){
				  BusinessLocationExtensionType businessLocationExtensionH=
						  new  BusinessLocationExtensionType();
			      businessLocationH.setExtension(businessLocationExtensionH);
			  }
			objectEventType.setBizLocation(businessLocationH);
		}
		
		
		if (objectEvent.getIlmd() != null) {
			ILMDType iLMD = new ILMDType();
//			if(objectEvent.getIlmd().getMapExt() != null){
//				 List<MapExt> mapExtList=objectEvent.getIlmd().getMapExt();
//				 			 
//				 Map<QName, String> otherAttribute=new HashMap<QName, String>();
////					for(int i=0;i<mapExtList.size();i++){
////							QName name=new QName(mapExtList.get(i).getType(),"","");
////							otherAttribute.put(name,mapExtList.get(i).getValue());	
////					}
//					
//					iLMD.setOtherAttributes(otherAttribute);
//			 }
			if (objectEvent.getIlmd().getExtension() != null) {
				ILMDExtensionType iLMDExtension = new ILMDExtensionType();
				iLMD.setExtension(iLMDExtension);

			}
			//objectEventType.setIlmd(iLMD); // *********************************
		}

		

		// object Event Extension
		if (objectEvent.getExtension() != null) {
			ObjectEventExtensionType	objectEventExtensionH = new ObjectEventExtensionType();
			
			if (objectEvent.getExtension().getQuantityList() != null){
				List<QuantityElement> quantityList = objectEvent.getExtension().getQuantityList().getQuantityElement();
				QuantityListType	childQuantityListH = new QuantityListType();
				for (int i = 0; i < quantityList.size(); i++) {
					QuantityElementType quantityElement = new QuantityElementType();
					quantityElement.setEpcClass(quantityList.get(i).getEpcClass());
				//	quantityElement.setQuantity(quantityList.get(i).getQuantity()); // *********************************
					quantityElement.setUom(quantityList.get(i).getUom());
					childQuantityListH.getQuantityElement().add(quantityElement);
				}
				objectEventExtensionH.setQuantityList(childQuantityListH);
			}
			
			if(objectEvent.getExtension().getSourceList()!=null){
					List<SourceDest> sourceList = objectEvent.getExtension()
							.getSourceList().getSource();
					SourceListType	sourceListH = new SourceListType();
					SourceDestType sourceDestS;
					for (int i = 0; i < sourceList.size(); i++) {
						sourceDestS = new SourceDestType();
						sourceDestS.setType(sourceList.get(i).getType());
						sourceDestS.setValue(sourceList.get(i).getValue());
						sourceListH.getSource().add(sourceDestS);
					}
					
					objectEventExtensionH.setSourceList(sourceListH);
			}
			
			if(objectEvent.getExtension().getDestinationList()!=null){
				List<SourceDest> destinationList = objectEvent
						.getExtension().getDestinationList().getDestination();
				DestinationListType destinationListH = new DestinationListType();
				SourceDestType sourceDestD;
				for (int i = 0; i < destinationList.size(); i++) {
					sourceDestD = new SourceDestType();
					sourceDestD.setType(destinationList.get(i).getType());
					sourceDestD.setValue(destinationList.get(i).getValue());
					destinationListH.getDestination().add(sourceDestD);
				}
	
				objectEventExtensionH.setDestinationList(destinationListH);
			}
			

			if (objectEvent.getExtension().getIlmd() != null) {
				ILMDType iLMD = new ILMDType();
				
				//if (objectEvent.getIlmd().getExtension() != null) {
					//ILMDExtensionType iLMDExtension = new ILMDExtensionType();
					//iLMD.setExtension(iLMDExtension); // *********************************

				//}
				//objectEventType.setIlmd(iLMD); // *********************************
				if(objectEvent.getExtension().getIlmd().getExtensionMaps()!=null){
					if(objectEvent.getExtension().getIlmd().getExtensionMaps().getExtensionMapList()!=null){
						List<ExtensionMap> extensionMaps=objectEvent.getExtension()
								.getIlmd().getExtensionMaps().getExtensionMapList();
						try{
							List<Object> elementList=new ArrayList<Object>();
							Document doc;
							DocumentBuilderFactory dbf= DocumentBuilderFactory.newInstance();
							DocumentBuilder builder=dbf.newDocumentBuilder();
							doc=builder.newDocument();
							
							Map<Integer, ExtensionMap> map=new HashMap<Integer, ExtensionMap>();
							
							for(int i=0;i<extensionMaps.size();i++){
								map.put(extensionMaps.get(i).getLeftNodeNumber(), extensionMaps.get(i));
							}
							WriteUtility.leftNodeNumber=1;
							WriteUtility.rightNodeNumber=1;
							int[] level=new int[1];
							level[0]=0;
							if(map.size()>1)
								ReaderUtility.putAny(map,1,level,null,elementList,doc);
							iLMD.setAny(elementList);
						} catch (ParserConfigurationException e){
							Configuration.logger.log(Level.ERROR, e.toString());
						}	
					}
					
				}
//				List<ExtensionMap> extensionMaps=objectEvent.getExtensionMaps().getExtensionMapList();
//				try{
//					String namespaceURI="http://namespaceURI";
//					String localName="localName";
//					String prefix="prefix";
//					String value="value";
//					
//					List<Object> elementList=new ArrayList<Object>();
//					
//					Document doc;
//					DocumentBuilderFactory dbf= DocumentBuilderFactory.newInstance();
//					DocumentBuilder builder=dbf.newDocumentBuilder();
//					doc=builder.newDocument();
//					
//					
//					String qName=prefix+":"+localName;
//					Element element=doc.createElement(qName);
//					
//					if(prefix !=null && namespaceURI != null){
//						element.setAttribute("xmlns:"+prefix, namespaceURI);
//					}
//					if(value != null){
//						element.setTextContent(value);
//					//	elementList.add(element);
//					}
//					
//					Map<Integer, ExtensionMap> map=new HashMap<Integer, ExtensionMap>();
//					
//					for(int i=0;i<extensionMaps.size();i++){
//						map.put(extensionMaps.get(i).getLeftNodeNumber(), extensionMaps.get(i));
//					}
//					Configuration.logger.info("-----------------------------");
//					WriteUtility.leftNodeNumber=1;
//					WriteUtility.rightNodeNumber=1;
//					int[] level=new int[1];
//					level[0]=0;
//					ReaderUtility.putAny(map,1,level,null,elementList,doc);
//					objectEventType.setAny(elementList);
//				} catch (ParserConfigurationException e){
//					Configuration.logger.log(Level.ERROR, e.toString());
//				}
				objectEventExtensionH.setIlmd(iLMD);
			}

			//objectEventExtensionH.setObjectEvent(objectEvent);
			
			
			  if(objectEvent.getExtension().getExtension2() != null){
				  ObjectEventExtension2Type objectEventExtension2= 
						  new  ObjectEventExtension2Type();
				  
					 if(objectEvent.getExtension().getExtension2().getAny()!=null){
						 List<String> any=objectEvent.getExtension().getExtension2().getAny();
						 List<Object> anyOut=new ArrayList<Object>();
						 for(int i=0;i<any.size();i++){
								anyOut.add(any.get(i));							
							}
							
						 objectEventExtension2.setAny(anyOut);	
					 }
//					 if(objectEvent.getExtension().getExtension().getMapExt() != null){
//						 List<MapExt> mapExtList=objectEvent.getExtension().getExtension().getMapExt();
//						 
//						 
//						 Map<QName, String> otherAttribute=new HashMap<QName, String>();
//							for(int i=0;i<mapExtList.size();i++){
//									QName name=new QName(mapExtList.get(i).getType(),"","");
//									otherAttribute.put(name,mapExtList.get(i).getValue());	
//							}
//							objectEventExtension2.setOtherAttributes(otherAttribute);
//					 }
				  objectEventExtensionH.setExtension(objectEventExtension2);
			  
			  }
			  objectEventType.setExtension(objectEventExtensionH);

		}

		// chield epcs ObjectEventEPCs
		if (objectEvent.getEpcList() != null) {
			EPCList epcs = objectEvent.getEpcList();
			EPCListType objectEventEPCs=new EPCListType();
			List<EPCN> epcList = epcs.getEpc();
			EPC epctn;
			for (int i = 0; i < epcList.size(); i++) {
				epctn = new EPC();
				epctn.setValue(epcList.get(i).getValue());
				objectEventEPCs.getEpc().add(epctn);
			}
			objectEventType.setEpcList(objectEventEPCs);

		}
		//any object
		if(objectEvent.getExtensionMaps() != null){
			if(objectEvent.getExtensionMaps().getExtensionMapList()!=null){
				List<ExtensionMap> extensionMaps=objectEvent.getExtensionMaps().getExtensionMapList();
				
				try{
					String namespaceURI="http://namespaceURI";
					String localName="localName";
					String prefix="prefix";
					String value="value";
					
					List<Object> elementList=new ArrayList<Object>();
					
					Document doc;
					DocumentBuilderFactory dbf= DocumentBuilderFactory.newInstance();
					DocumentBuilder builder=dbf.newDocumentBuilder();
					doc=builder.newDocument();
					
					
					String qName=prefix+":"+localName;
					Element element=doc.createElement(qName);
					
					if(prefix !=null && namespaceURI != null){
						element.setAttribute("xmlns:"+prefix, namespaceURI);
					}
					if(value != null){
						element.setTextContent(value);
					//	elementList.add(element);
					}
					
					Map<Integer, ExtensionMap> map=new HashMap<Integer, ExtensionMap>();
					
					for(int i=0;i<extensionMaps.size();i++){
						map.put(extensionMaps.get(i).getLeftNodeNumber(), extensionMaps.get(i));
					}
				//	Configuration.logger.info("-----------------------------");
					WriteUtility.leftNodeNumber=1;
					WriteUtility.rightNodeNumber=1;
					int[] level=new int[1];
					level[0]=0;
					if(map.size()>1)
						ReaderUtility.putAny(map,1,level,null,elementList,doc);
					objectEventType.setAny(elementList);
				} catch (ParserConfigurationException e){
					Configuration.logger.log(Level.ERROR, e.toString());
				}
				
			}
			
		}
		
		return objectEventType;
		} catch (DatatypeConfigurationException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
		}
		return null;
	}
	
	
	
//=====================================================================================================================

	public QuantityEventType convert(QuantityEvent quantityEvent) {
		
	try{
		QuantityEventType quantityEventType = new QuantityEventType();

		

		// Event Time
		if (quantityEvent.getEventTime() != null) {
			GregorianCalendar eventTimeGerogy=new GregorianCalendar();
			eventTimeGerogy.setTime(quantityEvent.getEventTime());
			XMLGregorianCalendar eventTimeXMLG=DatatypeFactory.newInstance().newXMLGregorianCalendar(eventTimeGerogy);
			quantityEventType.setEventTime(eventTimeXMLG);
		}

		// Record Time
		if (quantityEvent.getRecordTime()!=null){
			GregorianCalendar recordTimeGerogy=new GregorianCalendar();
			recordTimeGerogy.setTime(quantityEvent.getRecordTime());
			XMLGregorianCalendar recordTimeXMLG=DatatypeFactory.newInstance().newXMLGregorianCalendar(recordTimeGerogy);
			quantityEventType.setRecordTime(recordTimeXMLG);
		}
		// Aggregation Time offset
		if (quantityEvent.getEventTimeZoneOffset() != null) {
			quantityEventType.setEventTimeZoneOffset(quantityEvent
					.getEventTimeZoneOffset());
		}
		
		//EPCISEventExtension
		if(quantityEvent.getBaseExtension()!=null){
			EPCISEventExtensionType epcisEventExtensionType=new EPCISEventExtensionType();
			epcisEventExtensionType.setEventID(quantityEvent.getBaseExtension().getEventID());
			EPCISEventExtension2Type epcisEventExtension2Type=new EPCISEventExtension2Type();
			epcisEventExtensionType.setExtension(epcisEventExtension2Type);
			if(quantityEvent.getBaseExtension().getErrorDeclaration()!=null){
				ErrorDeclarationType errorDeclarationType=new ErrorDeclarationType();
				if(quantityEvent.getBaseExtension().getErrorDeclaration().getDeclarationTime()!=null){
					GregorianCalendar declarationTimeGerogian=new GregorianCalendar();
					declarationTimeGerogian.setTime(quantityEvent.getBaseExtension().getErrorDeclaration().getDeclarationTime());
					XMLGregorianCalendar declarationTimeXMLG=DatatypeFactory.newInstance().newXMLGregorianCalendar(declarationTimeGerogian);
					errorDeclarationType.setDeclarationTime(declarationTimeXMLG);
				}
				if(quantityEvent.getBaseExtension().getErrorDeclaration().getReason()!=null){
					errorDeclarationType.setReason(quantityEvent.getBaseExtension().getErrorDeclaration().getReason());
				}
				if(quantityEvent.getBaseExtension().getErrorDeclaration().getCorrectiveEventIDs()!=null){
					
					CorrectiveEventIDsType correctiveEventIDsType = new CorrectiveEventIDsType();
					if(quantityEvent.getBaseExtension().getErrorDeclaration().getCorrectiveEventIDs().getCorrectiveEventID()!=null){
						List<CorrectiveEventID> correctiveEventIDList=quantityEvent.getBaseExtension().getErrorDeclaration().getCorrectiveEventIDs().getCorrectiveEventID();
						List<String> correctiveEventIDListStr=new ArrayList<String>();
						for(int i=0;i<correctiveEventIDList.size();i++){
							correctiveEventIDListStr.add(correctiveEventIDList.get(i).getCorrectiveEventID());
						}
						correctiveEventIDsType.setCorrectiveEventID(correctiveEventIDListStr);
					}
					errorDeclarationType.setCorrectiveEventIDs(correctiveEventIDsType);
				}				
				if(quantityEvent.getBaseExtension().getErrorDeclaration().getExtensionMaps()!=null){
					if(quantityEvent.getBaseExtension().getErrorDeclaration().getExtensionMaps().getExtensionMapList()!=null){
						List<ExtensionMap> extensionMaps=quantityEvent.getBaseExtension().getErrorDeclaration().getExtensionMaps()
								.getExtensionMapList();
						try{
							List<Object> elementList=new ArrayList<Object>();
							Document doc;
							DocumentBuilderFactory dbf= DocumentBuilderFactory.newInstance();
							DocumentBuilder builder=dbf.newDocumentBuilder();
							doc=builder.newDocument();
							
							Map<Integer, ExtensionMap> map=new HashMap<Integer, ExtensionMap>();
							
							for(int i=0;i<extensionMaps.size();i++){
								map.put(extensionMaps.get(i).getLeftNodeNumber(), extensionMaps.get(i));
							}
							WriteUtility.leftNodeNumber=1;
							WriteUtility.rightNodeNumber=1;
							int[] level=new int[1];
							level[0]=0;
							if(map.size()>1)
								ReaderUtility.putAny(map,1,level,null,elementList,doc);
							errorDeclarationType.setAny(elementList);
						} catch (ParserConfigurationException e){
							Configuration.logger.log(Level.ERROR, e.toString());
						}	
					}
				}
				epcisEventExtensionType.setErrorDeclaration(errorDeclarationType);
			}
			quantityEventType.setBaseExtension(epcisEventExtensionType);
		}
		
		// epc class
		if (quantityEvent.getEpcClass() != null) {
			quantityEventType.setEpcClass(quantityEvent.getEpcClass());
		}
		// quantity
		
		quantityEventType.setQuantity(quantityEvent.getQuantity());

		// Business step
		if (quantityEvent.getBizStep() != null) {
			quantityEventType.setBizStep(quantityEvent.getBizStep());
		}
		// Disposition
		if (quantityEvent.getDisposition() != null) {
			quantityEventType.setDisposition(quantityEvent.getDisposition());
		}
		// read point
		if (quantityEvent.getReadPoint() != null) {
			ReadPointType readpointH = new ReadPointType();
			readpointH.setId(quantityEvent.getReadPoint().getsId());
			  if(quantityEvent.getReadPoint().getExtension() !=null){
			  ReadPointExtensionType readPointExtensionH=new ReadPointExtensionType();
				readpointH.setExtension(readPointExtensionH);; 
			  
			  }
			  quantityEventType.setReadPoint(readpointH);
		}

		// business transaction
		if (quantityEvent.getBizTransactionList() != null) {
			List<BusinessTransaction> bizTransaction = quantityEvent
					.getBizTransactionList().getBizTransaction();
			BusinessTransactionListType businessTransactionList = new BusinessTransactionListType();
			BusinessTransactionType businessTransaction;
			
			for (int i = 0; i < bizTransaction.size(); i++) {
				businessTransaction = new BusinessTransactionType();
				businessTransaction.setType(bizTransaction.get(i).getType());
				businessTransaction.setValue(bizTransaction.get(i).getValue());
				businessTransactionList.getBizTransaction().add(
						businessTransaction);
				}
			quantityEventType.setBizTransactionList(businessTransactionList);
		}

		// Business location
		if (quantityEvent.getBizLocation() != null) {
			BusinessLocationType businessLocationH = new BusinessLocationType();
			businessLocationH.setId(quantityEvent.getBizLocation().getsId());
			if(quantityEvent.getBizLocation().getExtension() !=null){
				  BusinessLocationExtensionType businessLocationExtensionH=
						  new  BusinessLocationExtensionType();
			      businessLocationH.setExtension(businessLocationExtensionH);
			  }
			quantityEventType.setBizLocation(businessLocationH);
			}

		// quantity Event Extension
				if (quantityEvent.getExtension() != null) {
					QuantityEventExtensionType quantityEventExtension = new QuantityEventExtensionType();
					quantityEventType.setExtension(quantityEventExtension);
				}

		return quantityEventType;
		} catch (DatatypeConfigurationException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
		}
		return null;	

	}
	
//==================================================================================================================
	
	public TransactionEventType convert(TransactionEvent transactionEvent) {
		

		try{
		TransactionEventType transactionEventType = new TransactionEventType();
		
	
		// Event Time
		if (transactionEvent.getEventTime() != null) {
			GregorianCalendar eventTimeGerogy=new GregorianCalendar();
			eventTimeGerogy.setTime(transactionEvent.getEventTime());
			XMLGregorianCalendar eventTimeXMLG=DatatypeFactory.newInstance().newXMLGregorianCalendar(eventTimeGerogy);
			transactionEventType.setEventTime(eventTimeXMLG);
		}

		// Record Time
		if (transactionEvent.getRecordTime()!=null){
			GregorianCalendar recordTimeGerogy=new GregorianCalendar();
			recordTimeGerogy.setTime(transactionEvent.getRecordTime());
			XMLGregorianCalendar recordTimeXMLG=DatatypeFactory.newInstance().newXMLGregorianCalendar(recordTimeGerogy);
			transactionEventType.setRecordTime(recordTimeXMLG);
		}
		// Aggregation Time offset
		if (transactionEvent.getEventTimeZoneOffset() != null) {
			transactionEventType.setEventTimeZoneOffset(transactionEvent
					.getEventTimeZoneOffset());
		}
		
		//EPCISEventExtension
		if(transactionEvent.getBaseExtension()!=null){
			EPCISEventExtensionType epcisEventExtensionType=new EPCISEventExtensionType();
			epcisEventExtensionType.setEventID(transactionEvent.getBaseExtension().getEventID());
			EPCISEventExtension2Type epcisEventExtension2Type=new EPCISEventExtension2Type();
			epcisEventExtensionType.setExtension(epcisEventExtension2Type);
			if(transactionEvent.getBaseExtension().getErrorDeclaration()!=null){
				ErrorDeclarationType errorDeclarationType=new ErrorDeclarationType();
				if(transactionEvent.getBaseExtension().getErrorDeclaration().getDeclarationTime()!=null){
					GregorianCalendar declarationTimeGerogian=new GregorianCalendar();
					declarationTimeGerogian.setTime(transactionEvent.getBaseExtension().getErrorDeclaration().getDeclarationTime());
					XMLGregorianCalendar declarationTimeXMLG=DatatypeFactory.newInstance().newXMLGregorianCalendar(declarationTimeGerogian);
					errorDeclarationType.setDeclarationTime(declarationTimeXMLG);
				}
				if(transactionEvent.getBaseExtension().getErrorDeclaration().getReason()!=null){
					errorDeclarationType.setReason(transactionEvent.getBaseExtension().getErrorDeclaration().getReason());
				}
				if(transactionEvent.getBaseExtension().getErrorDeclaration().getCorrectiveEventIDs()!=null){
					CorrectiveEventIDsType correctiveEventIDsType = new CorrectiveEventIDsType();
					if(transactionEvent.getBaseExtension().getErrorDeclaration().getCorrectiveEventIDs().getCorrectiveEventID()!=null){
						List<CorrectiveEventID> correctiveEventIDList=transactionEvent.getBaseExtension().getErrorDeclaration().getCorrectiveEventIDs().getCorrectiveEventID();
						List<String> correctiveEventIDListStr=new ArrayList<String>();
						for(int i=0;i<correctiveEventIDList.size();i++){
							correctiveEventIDListStr.add(correctiveEventIDList.get(i).getCorrectiveEventID());
						}
						correctiveEventIDsType.setCorrectiveEventID(correctiveEventIDListStr);
					}
					errorDeclarationType.setCorrectiveEventIDs(correctiveEventIDsType);
				}	
				if(transactionEvent.getBaseExtension().getErrorDeclaration().getExtensionMaps()!=null){
					if(transactionEvent.getBaseExtension().getErrorDeclaration().getExtensionMaps().getExtensionMapList()!=null){
						List<ExtensionMap> extensionMaps=transactionEvent.getBaseExtension().getErrorDeclaration().getExtensionMaps()
								.getExtensionMapList();
						try{
							List<Object> elementList=new ArrayList<Object>();
							Document doc;
							DocumentBuilderFactory dbf= DocumentBuilderFactory.newInstance();
							DocumentBuilder builder=dbf.newDocumentBuilder();
							doc=builder.newDocument();
							
							Map<Integer, ExtensionMap> map=new HashMap<Integer, ExtensionMap>();
							
							for(int i=0;i<extensionMaps.size();i++){
								map.put(extensionMaps.get(i).getLeftNodeNumber(), extensionMaps.get(i));
							}
							WriteUtility.leftNodeNumber=1;
							WriteUtility.rightNodeNumber=1;
							int[] level=new int[1];
							level[0]=0;
							if(map.size()>1)
								ReaderUtility.putAny(map,1,level,null,elementList,doc);
							errorDeclarationType.setAny(elementList);
						} catch (ParserConfigurationException e){
							Configuration.logger.log(Level.ERROR, e.toString());
						}	
					}
				}
				epcisEventExtensionType.setErrorDeclaration(errorDeclarationType);
			}
			transactionEventType.setBaseExtension(epcisEventExtensionType);
		}

		// Parent ID
		if (transactionEvent.getParentID() != null) {
			transactionEventType.setParentID(transactionEvent.getParentID());
		}

		// action
		transactionEventType.setAction(ActionType.fromValue(transactionEvent
				.getAction().name()));
		// Business step
		if (transactionEvent.getBizStep() != null) {
			transactionEventType.setBizStep(transactionEvent.getBizStep());
		}
		// Disposition
		if (transactionEvent.getDisposition() != null) {
			transactionEventType.setDisposition(transactionEvent
					.getDisposition());
		}

		// read point
		if (transactionEvent.getReadPoint() != null) {
			ReadPointType readpointH = new ReadPointType();
			readpointH.setId(transactionEvent.getReadPoint().getsId());
			  if(transactionEvent.getReadPoint().getExtension() !=null){
			  ReadPointExtensionType readPointExtensionH=new ReadPointExtensionType();
				readpointH.setExtension(readPointExtensionH);; 
			  
			  }
			  transactionEventType.setReadPoint(readpointH);
		}

		// business transaction
		if (transactionEvent.getBizTransactionList() != null) {
			List<BusinessTransaction> bizTransaction = transactionEvent
					.getBizTransactionList().getBizTransaction();
			BusinessTransactionListType businessTransactionList = new BusinessTransactionListType();
			BusinessTransactionType businessTransaction;
			
			for (int i = 0; i < bizTransaction.size(); i++) {
				businessTransaction = new BusinessTransactionType();
				businessTransaction.setType(bizTransaction.get(i).getType());
				businessTransaction.setValue(bizTransaction.get(i).getValue());
				businessTransactionList.getBizTransaction().add(
						businessTransaction);
				}
			transactionEventType.setBizTransactionList(businessTransactionList);
		}

		// Business location
		if (transactionEvent.getBizLocation() != null) {
			BusinessLocationType businessLocationH = new BusinessLocationType();
			businessLocationH.setId(transactionEvent.getBizLocation().getsId());
			if(transactionEvent.getBizLocation().getExtension() !=null){
				  BusinessLocationExtensionType businessLocationExtensionH=
						  new  BusinessLocationExtensionType();
			      businessLocationH.setExtension(businessLocationExtensionH);
			  }
			transactionEventType.setBizLocation(businessLocationH);
		}

	

		

		// Transaction Event Extension
		if (transactionEvent.getExtension() != null) {
			TransactionEventExtensionType transactionEventExtensionH = new TransactionEventExtensionType();

			if(transactionEvent.getExtension().getQuantityList() !=null){
				List<QuantityElement> quantityList = transactionEvent.getExtension().getQuantityList().getQuantityElement();
				QuantityListType	childQuantityListH = new QuantityListType();
				for (int i = 0; i < quantityList.size(); i++) {
//					QuantityElementType quantityElement = new QuantityElementType();
//					quantityElement.setEpcClass(quantityList.get(i).getEpcClass());
//					quantityElement.setQuantity(quantityList.get(i).getQuantity());
//					quantityElement.setUom(quantityList.get(i).getUom());
//					childQuantityListH.getQuantityElement().add(quantityElement); // *********************************
				}
				transactionEventExtensionH.setQuantityList(childQuantityListH);
			}

			if(transactionEvent.getExtension().getSourceList() != null){
				List<SourceDest> sourceList = transactionEvent
						.getExtension().getSourceList().getSource();
				SourceListType sourceListH = new SourceListType();
				SourceDestType sourceDestS;
				for (int i = 0; i < sourceList.size(); i++) {
					sourceDestS = new SourceDestType();
					sourceDestS.setType(sourceList.get(i).getType());
					sourceDestS.setValue(sourceList.get(i).getValue());
					sourceListH.getSource().add(sourceDestS);
				}
	
				transactionEventExtensionH.setSourceList(sourceListH);
			}

			if(transactionEvent.getExtension().getDestinationList() != null){
				List<SourceDest> destinationList = transactionEvent
						.getExtension().getDestinationList().getDestination();
				DestinationListType destinationListH = new DestinationListType();
				SourceDestType 	sourceDestD;
				for (int i = 0; i < destinationList.size(); i++) {
					sourceDestD = new SourceDestType();
					sourceDestD.setType(destinationList.get(i).getType());
					sourceDestD.setValue(destinationList.get(i).getValue());
					destinationListH.getDestination().add(sourceDestD);
				}
	
				transactionEventExtensionH.setDestinationList(destinationListH);
			}


			

			if (transactionEvent.getExtension().getExtension() != null) {
				TransactionEventExtension2Type transactionEventExtension2 = new TransactionEventExtension2Type();
				 if(transactionEvent.getExtension().getExtension().getAny()!=null){
					 List<String> any=transactionEvent.getExtension().getExtension().getAny();
					 List<Object> anyOut=new ArrayList<Object>();
					 for(int i=0;i<any.size();i++){
							anyOut.add(any.get(i));							
						}
						
					 transactionEventExtension2.setAny(anyOut);	
				 }
//				 if(transactionEvent.getExtension().getExtension().getMapExt() != null){
//					 List<MapExt> mapExtList=transactionEvent.getExtension().getExtension().getMapExt();
//					 
//					 
//					 Map<QName, String> otherAttribute=new HashMap<QName, String>();
////						for(int i=0;i<mapExtList.size();i++){
////								QName name=new QName(mapExtList.get(i).getType(),"","");
////								otherAttribute.put(name,mapExtList.get(i).getValue());	
////						}
//						transactionEventExtension2.setOtherAttributes(otherAttribute);
//				 }
				
				transactionEventExtensionH.setExtension(transactionEventExtension2);

			}
			transactionEventType.setExtension(transactionEventExtensionH);

		}

		// Child EPCs ObjectEvent EPCs
		if (transactionEvent.getEpcList() != null) {
			EPCList epcs = transactionEvent.getEpcList();
			EPCListType transactionEventEPCs= new EPCListType();
			List<EPCN> epcList = epcs.getEpc();
			EPC epct;
			for (int i = 0; i < epcList.size(); i++) {
				epct = new EPC();
				epct.setValue(epcList.get(i).getValue());
				transactionEventEPCs.getEpc().add(epct);
			}
			transactionEventType.setEpcList(transactionEventEPCs);

		}

		return transactionEventType;
		} catch (DatatypeConfigurationException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
		}
		return null;

	}

//==========================================================================================================================
	
	public TransformationEventType convert(TransformationEvent transformationEvent) {

		try{
		TransformationEventType transformationEventType = new TransformationEventType();

		// Event Time
		if (transformationEvent.getEventTime() != null) {
			GregorianCalendar eventTimeGerogy=new GregorianCalendar();
			eventTimeGerogy.setTime(transformationEvent.getEventTime());
			XMLGregorianCalendar eventTimeXMLG=DatatypeFactory.newInstance().newXMLGregorianCalendar(eventTimeGerogy);
			transformationEventType.setEventTime(eventTimeXMLG);
		}

		// Record Time
		if (transformationEvent.getRecordTime()!=null){
			GregorianCalendar recordTimeGerogy=new GregorianCalendar();
			recordTimeGerogy.setTime(transformationEvent.getRecordTime());
			XMLGregorianCalendar recordTimeXMLG=DatatypeFactory.newInstance().newXMLGregorianCalendar(recordTimeGerogy);
			transformationEventType.setRecordTime(recordTimeXMLG);
		}
		// TransformationEventType Time offset
		if (transformationEvent.getEventTimeZoneOffset() != null) {
			transformationEventType.setEventTimeZoneOffset(transformationEvent
					.getEventTimeZoneOffset());
		}

		//EPCISEventExtension
		if(transformationEvent.getBaseExtension()!=null){
			EPCISEventExtensionType epcisEventExtensionType=new EPCISEventExtensionType();
			epcisEventExtensionType.setEventID(transformationEvent.getBaseExtension().getEventID());
			EPCISEventExtension2Type epcisEventExtension2Type=new EPCISEventExtension2Type();
			epcisEventExtensionType.setExtension(epcisEventExtension2Type);
			if(transformationEvent.getBaseExtension().getErrorDeclaration()!=null){
				ErrorDeclarationType errorDeclarationType=new ErrorDeclarationType();
				if(transformationEvent.getBaseExtension().getErrorDeclaration().getDeclarationTime()!=null){
					GregorianCalendar declarationTimeGerogian=new GregorianCalendar();
					declarationTimeGerogian.setTime(transformationEvent.getBaseExtension().getErrorDeclaration().getDeclarationTime());
					XMLGregorianCalendar declarationTimeXMLG=DatatypeFactory.newInstance().newXMLGregorianCalendar(declarationTimeGerogian);
					errorDeclarationType.setDeclarationTime(declarationTimeXMLG);
				}
				if(transformationEvent.getBaseExtension().getErrorDeclaration().getReason()!=null){
					errorDeclarationType.setReason(transformationEvent.getBaseExtension().getErrorDeclaration().getReason());
				}
				if(transformationEvent.getBaseExtension().getErrorDeclaration().getCorrectiveEventIDs()!=null){
					
					CorrectiveEventIDsType correctiveEventIDsType = new CorrectiveEventIDsType();
					if(transformationEvent.getBaseExtension().getErrorDeclaration().getCorrectiveEventIDs().getCorrectiveEventID()!=null){
						List<CorrectiveEventID> correctiveEventIDList=transformationEvent.getBaseExtension().getErrorDeclaration().getCorrectiveEventIDs().getCorrectiveEventID();
						List<String> correctiveEventIDListStr=new ArrayList<String>();
						for(int i=0;i<correctiveEventIDList.size();i++){
							correctiveEventIDListStr.add(correctiveEventIDList.get(i).getCorrectiveEventID());
						}
						correctiveEventIDsType.setCorrectiveEventID(correctiveEventIDListStr);
					}
					errorDeclarationType.setCorrectiveEventIDs(correctiveEventIDsType);
				}			
				if(transformationEvent.getBaseExtension().getErrorDeclaration().getExtensionMaps()!=null){
					if(transformationEvent.getBaseExtension().getErrorDeclaration().getExtensionMaps().getExtensionMapList()!=null){
						List<ExtensionMap> extensionMaps=transformationEvent.getBaseExtension().getErrorDeclaration().getExtensionMaps()
								.getExtensionMapList();
						try{
							List<Object> elementList=new ArrayList<Object>();
							Document doc;
							DocumentBuilderFactory dbf= DocumentBuilderFactory.newInstance();
							DocumentBuilder builder=dbf.newDocumentBuilder();
							doc=builder.newDocument();
							
							Map<Integer, ExtensionMap> map=new HashMap<Integer, ExtensionMap>();
							
							for(int i=0;i<extensionMaps.size();i++){
								map.put(extensionMaps.get(i).getLeftNodeNumber(), extensionMaps.get(i));
							}
							WriteUtility.leftNodeNumber=1;
							WriteUtility.rightNodeNumber=1;
							int[] level=new int[1];
							level[0]=0;
							if(map.size()>1)
								ReaderUtility.putAny(map,1,level,null,elementList,doc);
							errorDeclarationType.setAny(elementList);
						} catch (ParserConfigurationException e){
							Configuration.logger.log(Level.ERROR, e.toString());
						}	
					}
				}
				epcisEventExtensionType.setErrorDeclaration(errorDeclarationType);
			}
			transformationEventType.setBaseExtension(epcisEventExtensionType);
		}
		
		// Input EPC List
		if (transformationEvent.getInputEPCList() != null) {
			EPCListType inputEPCList=new EPCListType();
			EPCList epcs = transformationEvent.getInputEPCList();
			List<EPCN> epcList = epcs.getEpc();
			EPC epcI;
			for (int i = 0; i < epcList.size(); i++) {
				epcI = new EPC();
				epcI.setValue(epcList.get(i).getValue());
				inputEPCList.getEpc().add(epcI);
			}
			transformationEventType.setInputEPCList(inputEPCList);
		}
		// Output EPC List
		if (transformationEvent.getOutputEPCList() != null) {
			EPCList epcs = transformationEvent.getOutputEPCList();
			EPCListType outputEPCList=new EPCListType();
			List<EPCN> epcList = epcs.getEpc();
			EPC epcO;
			for (int i = 0; i < epcList.size(); i++) {
				epcO = new EPC(epcList.get(i).getValue());
				epcO.setValue(epcList.get(i).getValue());
				outputEPCList.getEpc().add(epcO);
			}
			transformationEventType.setOutputEPCList(outputEPCList);

		}

		// Input Quantity Element List
		if (transformationEvent.getInputQuantityList()!= null) {

			List<QuantityElement> quantityListInput = transformationEvent
					.getInputQuantityList().getQuantityElement();
			QuantityListType inputQuantityList=new QuantityListType();
			QuantityElementType quantityElementInput;
			for (int i = 0; i < quantityListInput.size(); i++) {
				quantityElementInput = new QuantityElementType();
				quantityElementInput.setEpcClass(quantityListInput.get(i).getEpcClass());
				//quantityElementInput.setQuantity(quantityListInput.get(i).getQuantity()); // *********************************
				quantityElementInput.setUom(quantityListInput.get(i).getUom());
				inputQuantityList.getQuantityElement().add(quantityElementInput);
			}
			transformationEventType.setInputQuantityList(inputQuantityList);

		}

		// Output Quantity Element List
		if (transformationEvent.getOutputQuantityList() != null) {
			List<QuantityElement> quantityListOutput = transformationEvent
					.getOutputQuantityList().getQuantityElement();
			QuantityListType outputQuantityList=new QuantityListType();
			QuantityElementType quantityElementOutput;
			for (int i = 0; i < quantityListOutput.size(); i++) {
				quantityElementOutput = new QuantityElementType();
				quantityElementOutput.setEpcClass(quantityListOutput.get(i).getEpcClass());
				//quantityElementOutput.setQuantity(quantityListOutput.get(i).getQuantity()); // *********************************
				quantityElementOutput.setUom(quantityListOutput.get(i).getUom());
				outputQuantityList.getQuantityElement().add(quantityElementOutput);
			}
			transformationEventType.setOutputQuantityList(outputQuantityList);

		}

		// transformationID
		transformationEventType.setTransformationID(transformationEvent
				.getTransformationID());
		// bizStep
		transformationEventType.setBizStep(transformationEvent.getBizStep());
		// disposition
		transformationEventType.setDisposition(transformationEvent
				.getDisposition());

		// read point
		if (transformationEvent.getReadPoint() != null) {
			ReadPointType readpointH = new ReadPointType();
			readpointH.setId(transformationEvent.getReadPoint().getsId());
			  if(transformationEvent.getReadPoint().getExtension() !=null){
			  ReadPointExtensionType readPointExtensionH=new ReadPointExtensionType();
				readpointH.setExtension(readPointExtensionH);; 
			  
			  }
			  transformationEventType.setReadPoint(readpointH);
		}

		// business transaction
		if (transformationEvent.getBizTransactionList() != null) {
			List<BusinessTransaction> bizTransaction = transformationEvent
					.getBizTransactionList().getBizTransaction();
			BusinessTransactionListType businessTransactionList = new BusinessTransactionListType();
			BusinessTransactionType businessTransaction;
			
			for (int i = 0; i < bizTransaction.size(); i++) {
				businessTransaction = new BusinessTransactionType();
				businessTransaction.setType(bizTransaction.get(i).getType());
				businessTransaction.setValue(bizTransaction.get(i).getValue());
				businessTransactionList.getBizTransaction().add(
						businessTransaction);
				}
			transformationEventType.setBizTransactionList(businessTransactionList);
		}

		// Business location
		if (transformationEvent.getBizLocation() != null) {
			BusinessLocationType businessLocationH = new BusinessLocationType();
			businessLocationH.setId(transformationEvent.getBizLocation().getsId());
			if(transformationEvent.getBizLocation().getExtension() !=null){
				  BusinessLocationExtensionType businessLocationExtensionH=
						  new  BusinessLocationExtensionType();
			      businessLocationH.setExtension(businessLocationExtensionH);
			  }
			transformationEventType.setBizLocation(businessLocationH);
		}
		// Source List
		if (transformationEvent.getSourceList() != null) {
			List<SourceDest> sourceList = transformationEvent
					.getSourceList().getSource();
			SourceListType sourceListH = new SourceListType();
			SourceDestType sourceDestS;
			for (int i = 0; i < sourceList.size(); i++) {
				sourceDestS = new SourceDestType();
				sourceDestS.setType(sourceList.get(i).getType());
				sourceDestS.setValue(sourceList.get(i).getValue());
				sourceListH.getSource().add(sourceDestS);
			}

			transformationEventType.setSourceList(sourceListH);
		}

		// Source List
		if (transformationEvent.getDestinationList() != null) {
			List<SourceDest> destinationList = transformationEvent
					.getDestinationList().getDestination();
			DestinationListType destinationListH = new DestinationListType();
			SourceDestType sourceDestD;
			for (int i = 0; i < destinationList.size(); i++) {
				sourceDestD = new SourceDestType();
				sourceDestD.setType(destinationList.get(i).getType());
				sourceDestD.setValue(destinationList.get(i).getValue());
				destinationListH.getDestination().add(sourceDestD);
			}

			transformationEventType.setDestinationList(destinationListH);
		}
		// ilmd
		if (transformationEvent.getIlmd() != null) {
			ILMDType iLMD = new ILMDType();
//			if(transformationEvent.getIlmd().getMapExt() != null){
//				 List<MapExt> mapExtList=transformationEvent.getIlmd().getMapExt();
//				 			 
//				 Map<QName, String> otherAttribute=new HashMap<QName, String>();
////					for(int i=0;i<mapExtList.size();i++){
////							QName name=new QName(mapExtList.get(i).getType(),"","");
////							otherAttribute.put(name,mapExtList.get(i).getValue());	
////					}
//					
//					iLMD.setOtherAttributes(otherAttribute);
//			 }
			if (transformationEvent.getIlmd().getExtension() != null) {
				ILMDExtensionType iLMDExtension = new ILMDExtensionType();
				iLMD.setExtension(iLMDExtension);

			}
			transformationEventType.setIlmd(iLMD);

		}

		

		// Transformation Event Extension
		if (transformationEvent.getExtension() != null) {
			TransformationEventExtensionType transformationEventExtension = new TransformationEventExtensionType();
			 if(transformationEvent.getExtension().getAny()!=null){
				 List<String> any=transformationEvent.getExtension().getAny();
				 List<Object> anyOut=new ArrayList<Object>();
				 for(int i=0;i<any.size();i++){
						anyOut.add(any.get(i));							
					}
					
				 transformationEventExtension.setAny(anyOut);	
			 }
//			 if(transformationEvent.getExtension().getMapExt() != null){
//				 List<MapExt> mapExtList=transformationEvent.getExtension().getMapExt();
//				 			 
//				 Map<QName, String> otherAttribute=new HashMap<QName, String>();
////					for(int i=0;i<mapExtList.size();i++){
////							QName name=new QName(mapExtList.get(i).getType(),"","");
////							otherAttribute.put(name,mapExtList.get(i).getValue());	
////					}
//					transformationEventExtension.setOtherAttributes(otherAttribute);
//			 }
			transformationEventType.setExtension(transformationEventExtension);
		}

		return transformationEventType;
		} catch (DatatypeConfigurationException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
		}
		return null;
	}

//====================================================================================================================	

	public VocabularyType convert(Vocabulary vocabulary) {
	
		VocabularyType vocabularyType = new VocabularyType();
		vocabulary.setType(vocabulary.getType());
		if (vocabulary.getVocabularyElementList().getVocabularyElement() != null) {
			List<VocabularyElement> vocabularyElementList = vocabulary
					.getVocabularyElementList().getVocabularyElement();
			VocabularyElementListType vocabularyElementListType = new VocabularyElementListType();
			VocabularyElementType vocabularyElement;
			VocabularyElementExtensionType vocabularyElementExtension;
			for (int i = 0; i < vocabularyElementList.size(); i++) {

				vocabularyElement = new VocabularyElementType();
				vocabularyElement.setId(vocabularyElementList.get(i).getsId());
				if (vocabularyElementList.get(i).getAttribute() != null) {
					List<Attribute> attributeList = vocabularyElementList
							.get(i).getAttribute();
					AttributeType attribute;
					for (int j = 0; j < attributeList.size(); j++) {
						attribute = new AttributeType();
						attribute.setId(attributeList.get(j).getsId());
						// attribute.setValue(attributeList.get(j).getValue()); // *********************************
						vocabularyElement.getAttribute().add(attribute);
						
					}
				}
				if (vocabularyElementList.get(i).getChildren() != null) {
					// IDListType IDListType
					List<String> idType = vocabularyElementList.get(i)
							.getChildren().getId();
					IDListType iDList = new IDListType();
					for (int k = 0; k < idType.size(); k++) {
						iDList.getId().add(idType.get(k));
					}
					vocabularyElement.setChildren(iDList);
					
				}

				
				if (vocabularyElementList.get(i).getExtension() != null) {
					vocabularyElementExtension = new VocabularyElementExtensionType();
//					if(vocabularyElementList.get(i).getExtension().getMapExt() != null){
//						 List<MapExt> mapExtList=vocabularyElementList.get(i).getExtension().getMapExt();
//						 			 
//						 Map<QName, String> otherAttribute=new HashMap<QName, String>();
////							for(int j=0;i<mapExtList.size();j++){
////									QName name=new QName(mapExtList.get(j).getType(),"","");
////									otherAttribute.put(name,mapExtList.get(j).getValue());	
////							}
//							
//						//	vocabularyElementExtension.setOtherAttributes(otherAttribute); // *********************************
//					 }
					
					vocabularyElement.setExtension(vocabularyElementExtension);
				}
				vocabularyElementListType.getVocabularyElement().add(
						vocabularyElement);

			}

			vocabularyType.setVocabularyElementList(vocabularyElementListType);
		
		}

		

		if (vocabulary.getExtension() != null) {
			VocabularyExtensionType vocabularyExtension = new VocabularyExtensionType();
			
			vocabularyType.setExtension(vocabularyExtension);
		}

	return vocabularyType;
	}
}
