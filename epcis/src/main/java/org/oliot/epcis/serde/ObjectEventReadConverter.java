package org.oliot.epcis.serde;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.axis.message.MessageElement;
import org.oliot.model.ale.ECReportMemberField;
import org.oliot.model.epcis.ActionType;
import org.oliot.model.epcis.BusinessLocationExtensionType;
import org.oliot.model.epcis.BusinessLocationType;
import org.oliot.model.epcis.BusinessTransactionListType;
import org.oliot.model.epcis.BusinessTransactionType;
import org.oliot.model.epcis.DestinationListType;
import org.oliot.model.epcis.EPC;
import org.oliot.model.epcis.EPCISEventExtensionType;
import org.oliot.model.epcis.EPCListType;
import org.oliot.model.epcis.ObjectEventExtension2Type;
import org.oliot.model.epcis.ObjectEventExtensionType;
import org.oliot.model.epcis.ObjectEventType;
import org.oliot.model.epcis.QuantityElementType;
import org.oliot.model.epcis.QuantityListType;
import org.oliot.model.epcis.ReadPointExtensionType;
import org.oliot.model.epcis.ReadPointType;
import org.oliot.model.epcis.SourceDestType;
import org.oliot.model.epcis.SourceListType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@Component
@ReadingConverter
public class ObjectEventReadConverter implements
		Converter<DBObject, ObjectEventType> {

	public ObjectEventType convert(DBObject dbObject) {

		
		try {
			ObjectEventType objectEventType = new ObjectEventType();
			if (dbObject.get("eventTime") != null) {
				long eventTime = (long) dbObject.get("eventTime");
				GregorianCalendar eventCalendar = new GregorianCalendar();
				eventCalendar.setTimeInMillis(eventTime);
				XMLGregorianCalendar xmlEventTime = DatatypeFactory
						.newInstance().newXMLGregorianCalendar(eventCalendar);
				objectEventType.setEventTime(xmlEventTime);
			}
			if (dbObject.get("eventTimeZoneOffset") != null) {
				String eventTimeZoneOffset = (String) dbObject
						.get("eventTimeZoneOffset");
				objectEventType.setEventTimeZoneOffset(eventTimeZoneOffset);
			}
			if (dbObject.get("recordTime") != null) {
				long eventTime = (long) dbObject.get("recordTime");
				GregorianCalendar recordCalendar = new GregorianCalendar();
				recordCalendar.setTimeInMillis(eventTime);
				XMLGregorianCalendar xmlRecordTime = DatatypeFactory
						.newInstance().newXMLGregorianCalendar(recordCalendar);
				objectEventType.setRecordTime(xmlRecordTime);
			}
			if (dbObject.get("epcList") != null) {
				BasicDBList epcListM = (BasicDBList)dbObject.get("epcList");
				EPCListType epcListType = new EPCListType();
				List<EPC> epcs = new ArrayList<EPC>();
				for(int i = 0 ; i < epcListM.size() ; i++ )
				{
					EPC epc = new EPC();
					epc.setValue(epcListM.get(i).toString());
					epcs.add(epc);
				}
				epcListType.setEpc(epcs);
				objectEventType.setEpcList(epcListType);
			}
			if (dbObject.get("action") != null )
			{
				objectEventType.setAction(ActionType.fromValue(dbObject.get("action").toString()));
			}
			if (dbObject.get("bizStep") != null )
			{
				objectEventType.setBizStep(dbObject.get("bizStep").toString());
			}
			if (dbObject.get("disposition") != null )
			{
				objectEventType.setDisposition(dbObject.get("disposition").toString());
			}
			if( dbObject.get("baseExtension") != null )
			{
				EPCISEventExtensionType eeet = new EPCISEventExtensionType();
				BasicDBObject baseExtension = (BasicDBObject) dbObject.get("baseExtension");
				if( baseExtension.get("any") != null )
				{
					BasicDBObject anyObject = (BasicDBObject) baseExtension.get("any");
					Iterator<String> anyKeysIter = anyObject.keySet().iterator();
					List<Object> elementList = new ArrayList<Object>();
					while(anyKeysIter.hasNext())
					{
						String anyKey = anyKeysIter.next();
						String value = anyObject.get(anyKey).toString();
						if( anyKey != null && value != null )
						{
							DocumentBuilderFactory dbf = DocumentBuilderFactory
									.newInstance();
							DocumentBuilder builder = dbf
									.newDocumentBuilder();
							Document doc = builder.newDocument();
							
							Node node = doc.createElement("value");
							node.setTextContent(value);
							Element element = doc.createElement(anyKey);
							element.appendChild(node);
							elementList.add(element);
						}
					}
					eeet.setAny(elementList);
				}
				if( baseExtension.get("otherAttributes") != null)
				{
					Map<QName, String> otherAttributes = new HashMap<QName, String>();
					BasicDBObject otherAttributeObject = (BasicDBObject) baseExtension.get("otherAttributes");
					Iterator<String> otherKeysIter = otherAttributeObject.keySet().iterator();
					while(otherKeysIter.hasNext())
					{
						String anyKey = otherKeysIter.next();
						String value = otherAttributeObject.get(anyKey).toString();
						otherAttributes.put(new QName("", anyKey), value);
					}
					eeet.setOtherAttributes(otherAttributes);
				}
				objectEventType.setBaseExtension(eeet);
			}
			if ( dbObject.get("readPoint") != null )
			{
				BasicDBObject readPointObject = (BasicDBObject) dbObject.get("readPoint");
				ReadPointType readPointType = new ReadPointType();
				if( readPointObject.get("id") != null )
				{
					readPointType.setId(readPointObject.get("id").toString());
				}
				if( readPointObject.get("extension") != null)
				{
					ReadPointExtensionType rpet = new ReadPointExtensionType();
					//
					BasicDBObject extension = (BasicDBObject) readPointObject.get("extension");
					if( extension.get("any") != null )
					{
						BasicDBObject anyObject = (BasicDBObject) extension.get("any");
						Iterator<String> anyKeysIter = anyObject.keySet().iterator();
						List<Object> elementList = new ArrayList<Object>();
						while(anyKeysIter.hasNext())
						{
							String anyKey = anyKeysIter.next();
							String value = anyObject.get(anyKey).toString();
							if( anyKey != null && value != null )
							{
								DocumentBuilderFactory dbf = DocumentBuilderFactory
										.newInstance();
								DocumentBuilder builder = dbf
										.newDocumentBuilder();
								Document doc = builder.newDocument();
								
								Node node = doc.createElement("value");
								node.setTextContent(value);
								Element element = doc.createElement(anyKey);
								element.appendChild(node);
								elementList.add(element);
							}
						}
						rpet.setAny(elementList);
					}
					if( extension.get("otherAttributes") != null)
					{
						Map<QName, String> otherAttributes = new HashMap<QName, String>();
						BasicDBObject otherAttributeObject = (BasicDBObject) extension.get("otherAttributes");
						Iterator<String> otherKeysIter = otherAttributeObject.keySet().iterator();
						while(otherKeysIter.hasNext())
						{
							String anyKey = otherKeysIter.next();
							String value = otherAttributeObject.get(anyKey).toString();
							otherAttributes.put(new QName("", anyKey), value);
						}
						rpet.setOtherAttributes(otherAttributes);
					}
					//
					readPointType.setExtension(rpet);
				}
				objectEventType.setReadPoint(readPointType);
			}
			//BusinessLocation
			if ( dbObject.get("bizLocation") != null )
			{
				BasicDBObject bizLocationObject = (BasicDBObject) dbObject.get("bizLocation");
				BusinessLocationType bizLocationType = new BusinessLocationType();
				if( bizLocationObject.get("id") != null )
				{
					bizLocationType.setId(bizLocationObject.get("id").toString());
				}
				if( bizLocationObject.get("extension") != null)
				{
					BusinessLocationExtensionType blet = new BusinessLocationExtensionType();
					//
					BasicDBObject extension = (BasicDBObject) bizLocationObject.get("extension");
					if( extension.get("any") != null )
					{
						BasicDBObject anyObject = (BasicDBObject) extension.get("any");
						Iterator<String> anyKeysIter = anyObject.keySet().iterator();
						List<Object> elementList = new ArrayList<Object>();
						while(anyKeysIter.hasNext())
						{
							String anyKey = anyKeysIter.next();
							String value = anyObject.get(anyKey).toString();
							if( anyKey != null && value != null )
							{
								DocumentBuilderFactory dbf = DocumentBuilderFactory
										.newInstance();
								DocumentBuilder builder = dbf
										.newDocumentBuilder();
								Document doc = builder.newDocument();
								
								Node node = doc.createElement("value");
								node.setTextContent(value);
								Element element = doc.createElement(anyKey);
								element.appendChild(node);
								elementList.add(element);
							}
						}
						blet.setAny(elementList);
					}
					if( extension.get("otherAttributes") != null)
					{
						Map<QName, String> otherAttributes = new HashMap<QName, String>();
						BasicDBObject otherAttributeObject = (BasicDBObject) extension.get("otherAttributes");
						Iterator<String> otherKeysIter = otherAttributeObject.keySet().iterator();
						while(otherKeysIter.hasNext())
						{
							String anyKey = otherKeysIter.next();
							String value = otherAttributeObject.get(anyKey).toString();
							otherAttributes.put(new QName("", anyKey), value);
						}
						blet.setOtherAttributes(otherAttributes);
					}
					//
					bizLocationType.setExtension(blet);
				}
				objectEventType.setBizLocation(bizLocationType);
			}
			if( dbObject.get("bizTransactionList") != null )
			{
				BasicDBList bizTranList = (BasicDBList) dbObject.get("bizTransactionList");
				BusinessTransactionListType btlt = new BusinessTransactionListType();
				List<BusinessTransactionType> bizTranArrayList = new ArrayList<BusinessTransactionType>();
				for(int i = 0 ; i < bizTranList.size() ; i++ )
				{
					// DBObject, key and value
					BasicDBObject bizTran = (BasicDBObject) bizTranList.get(i);
					BusinessTransactionType btt = new BusinessTransactionType();
					Iterator<String> keyIter = bizTran.keySet().iterator();
					// at most one bizTran
					if(keyIter.hasNext())
					{
						String key = keyIter.next();
						String value = bizTran.getString(key);
						if( key != null && value != null )
						{
							btt.setType(key);
							btt.setValue(value);
						}
					}
					if( btt != null )
						bizTranArrayList.add(btt);
				}
				btlt.setBizTransaction(bizTranArrayList);
				objectEventType.setBizTransactionList(btlt);
			}
			
			// Extension Field
			if( dbObject.get("extension") != null )
			{
				BasicDBObject extObject = (BasicDBObject) dbObject.get("extension");
				// Quantity
				if( extObject.get("quantityList") != null )
				{
					QuantityListType qlt = new QuantityListType();
					List<QuantityElementType> qetList = new ArrayList<QuantityElementType>();
					BasicDBList quantityDBList = (BasicDBList)extObject.get("quantityList");
					for(int i = 0 ; i < quantityDBList.size() ; i++ )
					{
						QuantityElementType qet = new QuantityElementType();
						BasicDBObject quantityDBObject = (BasicDBObject)quantityDBList.get(i);
						Object epcClassObject = quantityDBObject.get("epcClass");
						Object quantity = quantityDBObject.get("quantity");
					}
				}
				
				
				
				
			}
			
			
			
			
			System.out.println();
		} catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		/*
		 * // Extension DBObject
		 * extension = new BasicDBObject(); if (objectEventType.getExtension()
		 * != null) { ObjectEventExtensionType oee =
		 * objectEventType.getExtension(); if (oee.getQuantityList() != null) {
		 * QuantityListType qetl = oee.getQuantityList();
		 * List<QuantityElementType> qetList = qetl.getQuantityElement();
		 * List<DBObject> quantityList = new ArrayList<DBObject>(); for( int i =
		 * 0 ; i < qetList.size() ; i++ ) { DBObject quantity = new
		 * BasicDBObject(); QuantityElementType qet = qetList.get(i); if
		 * (qet.getEpcClass() != null) quantity.put("epcClass",
		 * qet.getEpcClass().toString()); quantity.put("quantity",
		 * qet.getQuantity()); if (qet.getUom() != null) quantity.put("uom",
		 * qet.getUom().toString()); quantityList.add(quantity); }
		 * extension.put("quantityList", quantityList); } if
		 * (oee.getSourceList() != null) { SourceListType sdtl =
		 * oee.getSourceList(); List<SourceDestType> sdtList = sdtl.getSource();
		 * List<DBObject> dbList = new ArrayList<DBObject>(); for (int i = 0; i
		 * < sdtList.size(); i++) { SourceDestType sdt = sdtList.get(i);
		 * DBObject dbObj = new BasicDBObject(); dbObj.put(sdt.getType(),
		 * sdt.getValue()); dbList.add(dbObj); } extension.put("sourceList",
		 * dbList); } if (oee.getDestinationList() != null) {
		 * DestinationListType sdtl = oee.getDestinationList();
		 * List<SourceDestType> sdtList = sdtl.getDestination(); List<DBObject>
		 * dbList = new ArrayList<DBObject>(); for (int i = 0; i <
		 * sdtList.size(); i++) { SourceDestType sdt = sdtList.get(i); DBObject
		 * dbObj = new BasicDBObject(); dbObj.put(sdt.getType(),
		 * sdt.getValue()); dbList.add(dbObj); }
		 * extension.put("destinationList", dbList); } if (oee.getExtension() !=
		 * null) { ObjectEventExtension2Type extension2Type =
		 * oee.getExtension(); DBObject extension2 = new BasicDBObject(); if
		 * (extension2Type.getAny() != null) { Map<String, String> map2Save =
		 * new HashMap<String, String>(); List<Object> objList =
		 * extension2Type.getAny(); for (int i = 0; i < objList.size(); i++) {
		 * Object obj = objList.get(i); if (obj instanceof Element) { Element
		 * element = (Element) obj; if (element.getFirstChild() != null) {
		 * String name = element.getNodeName(); String value =
		 * element.getFirstChild() .getTextContent(); map2Save.put(name, value);
		 * } } } if (map2Save != null) extension2.put("any", map2Save); }
		 * 
		 * if (extension2Type.getOtherAttributes() != null) { Map<QName, String>
		 * map = extension2Type.getOtherAttributes(); Map<String, String>
		 * map2Save = new HashMap<String, String>(); Iterator<QName> iter =
		 * map.keySet().iterator(); while (iter.hasNext()) { QName qName =
		 * iter.next(); String value = map.get(qName);
		 * map2Save.put(qName.toString(), value); }
		 * extension2.put("otherAttributes", map2Save); }
		 * extension.put("extension", extension2); } } dbo.put("extension",
		 * extension); return dbo;
		 */
		return null;
	}

	public DBObject getDBObjectFromMessageElement(MessageElement any) {
		NamedNodeMap attributes = any.getAttributes();
		DBObject attrObject = new BasicDBObject();
		for (int i = 0; i < attributes.getLength(); i++) {
			Attr attr = (Attr) attributes.item(i);

			String attrName = attr.getNodeName();
			String attrValue = attr.getNodeValue();
			attrObject.put(attrName, attrValue);
		}
		return attrObject;
	}

}
