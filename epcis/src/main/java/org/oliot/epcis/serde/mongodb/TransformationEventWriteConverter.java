package org.oliot.epcis.serde.mongodb;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.axis.message.MessageElement;
import org.oliot.model.epcis.BusinessLocationExtensionType;
import org.oliot.model.epcis.BusinessLocationType;
import org.oliot.model.epcis.BusinessTransactionListType;
import org.oliot.model.epcis.BusinessTransactionType;
import org.oliot.model.epcis.DestinationListType;
import org.oliot.model.epcis.EPC;
import org.oliot.model.epcis.EPCISEventExtensionType;
import org.oliot.model.epcis.EPCListType;
import org.oliot.model.epcis.ILMDExtensionType;
import org.oliot.model.epcis.ILMDType;
import org.oliot.model.epcis.QuantityElementType;
import org.oliot.model.epcis.QuantityListType;
import org.oliot.model.epcis.ReadPointExtensionType;
import org.oliot.model.epcis.ReadPointType;
import org.oliot.model.epcis.SourceDestType;
import org.oliot.model.epcis.SourceListType;
import org.oliot.model.epcis.TransformationEventExtensionType;
import org.oliot.model.epcis.TransformationEventType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Copyright (C) 2014 KAIST RESL
 *
 * This project is part of Oliot (oliot.org), pursuing the implementation of
 * Electronic Product Code Information Service(EPCIS) v1.1 specification in
 * EPCglobal.
 * [http://www.gs1.org/gsmp/kc/epcglobal/epcis/epcis_1_1-standard-20140520.pdf]
 * 
 *
 * @author Jack Jaewook Byun, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         bjw0829@kaist.ac.kr
 */
@Component
@WritingConverter
public class TransformationEventWriteConverter implements
		Converter<TransformationEventType, DBObject> {

	public DBObject convert(TransformationEventType transformationEventType) {

		DBObject dbo = new BasicDBObject();
		if (transformationEventType.getBaseExtension() != null) {
			EPCISEventExtensionType baseExtensionType = transformationEventType
					.getBaseExtension();
			DBObject baseExtension = new BasicDBObject();
			if (baseExtensionType.getAny() != null) {
				Map<String, String> map2Save = new HashMap<String, String>();
				List<Object> objList = baseExtensionType.getAny();
				for (int i = 0; i < objList.size(); i++) {
					Object obj = objList.get(i);
					if (obj instanceof Element) {
						Element element = (Element) obj;
						if (element.getFirstChild() != null) {
							String name = element.getLocalName();
							String value = element.getFirstChild()
									.getTextContent();
							map2Save.put(name, value);
						}
					}
				}
				if (map2Save != null)
					baseExtension.put("any", map2Save);
			}

			if (baseExtensionType.getOtherAttributes() != null) {
				Map<QName, String> map = baseExtensionType.getOtherAttributes();
				Map<String, String> map2Save = new HashMap<String, String>();
				Iterator<QName> iter = map.keySet().iterator();
				while (iter.hasNext()) {
					QName qName = iter.next();
					String value = map.get(qName);
					map2Save.put(qName.toString(), value);
				}
				baseExtension.put("otherAttributes", map2Save);
			}
			dbo.put("baseExtension", baseExtension);
		}
		if (transformationEventType.getEventTime() != null)
			dbo.put("eventTime", transformationEventType.getEventTime()
					.toGregorianCalendar().getTimeInMillis());
		if (transformationEventType.getEventTimeZoneOffset() != null)
			dbo.put("eventTimeZoneOffset",
					transformationEventType.getEventTimeZoneOffset());
		// Record Time : according to M5
		GregorianCalendar recordTime = new GregorianCalendar();
		long recordTimeMilis = recordTime.getTimeInMillis();
		dbo.put("recordTime", recordTimeMilis);
		if (transformationEventType.getInputEPCList() != null) {
			EPCListType epcs = transformationEventType.getInputEPCList();
			List<EPC> epcList = epcs.getEpc();
			List<DBObject> epcDBList = new ArrayList<DBObject>();

			for (int i = 0; i < epcList.size(); i++) {
				DBObject epcDB = new BasicDBObject();
				epcDB.put("epc", epcList.get(i).getValue());
				epcDBList.add(epcDB);
			}
			dbo.put("inputEPCList", epcDBList);
		}
		if (transformationEventType.getOutputEPCList() != null) {
			EPCListType epcs = transformationEventType.getOutputEPCList();
			List<EPC> epcList = epcs.getEpc();
			List<DBObject> epcDBList = new ArrayList<DBObject>();

			for (int i = 0; i < epcList.size(); i++) {
				DBObject epcDB = new BasicDBObject();
				epcDB.put("epc", epcList.get(i).getValue());
				epcDBList.add(epcDB);
			}
			dbo.put("outputEPCList", epcDBList);
		}
		if (transformationEventType.getBizStep() != null)
			dbo.put("bizStep", transformationEventType.getBizStep());
		if (transformationEventType.getDisposition() != null)
			dbo.put("disposition", transformationEventType.getDisposition());
		if (transformationEventType.getReadPoint() != null) {
			ReadPointType readPointType = transformationEventType
					.getReadPoint();
			DBObject readPoint = new BasicDBObject();
			if (readPointType.getId() != null)
				readPoint.put("id", readPointType.getId());
			ReadPointExtensionType readPointExtensionType = readPointType
					.getExtension();
			if (readPointExtensionType != null) {
				DBObject extension = new BasicDBObject();
				if (readPointExtensionType.getAny() != null) {
					Map<String, String> map2Save = new HashMap<String, String>();
					List<Object> objList = readPointExtensionType.getAny();
					for (int i = 0; i < objList.size(); i++) {
						Object obj = objList.get(i);
						if (obj instanceof Element) {
							Element element = (Element) obj;
							if (element.getFirstChild() != null) {
								String name = element.getLocalName();
								String value = element.getFirstChild()
										.getTextContent();
								map2Save.put(name, value);
							}
						}
					}
					if (map2Save != null)
						extension.put("any", map2Save);
				}

				if (readPointExtensionType.getOtherAttributes() != null) {
					Map<QName, String> map = readPointExtensionType
							.getOtherAttributes();
					Map<String, String> map2Save = new HashMap<String, String>();
					Iterator<QName> iter = map.keySet().iterator();
					while (iter.hasNext()) {
						QName qName = iter.next();
						String value = map.get(qName);
						map2Save.put(qName.toString(), value);
					}
					extension.put("otherAttributes", map2Save);
				}
				readPoint.put("extension", extension);
			}
			dbo.put("readPoint", readPoint);
		}
		if (transformationEventType.getBizLocation() != null) {
			BusinessLocationType bizLocationType = transformationEventType
					.getBizLocation();
			DBObject bizLocation = new BasicDBObject();
			if (bizLocationType.getId() != null)
				bizLocation.put("id", bizLocationType.getId());
			BusinessLocationExtensionType bizLocationExtensionType = bizLocationType
					.getExtension();
			if (bizLocationExtensionType != null) {
				DBObject extension = new BasicDBObject();
				if (bizLocationExtensionType.getAny() != null) {
					Map<String, String> map2Save = new HashMap<String, String>();
					List<Object> objList = bizLocationExtensionType.getAny();
					for (int i = 0; i < objList.size(); i++) {
						Object obj = objList.get(i);
						if (obj instanceof Element) {
							Element element = (Element) obj;
							if (element.getFirstChild() != null) {
								String name = element.getLocalName();
								String value = element.getFirstChild()
										.getTextContent();
								map2Save.put(name, value);
							}
						}
					}
					if (map2Save != null)
						extension.put("any", map2Save);
				}

				if (bizLocationExtensionType.getOtherAttributes() != null) {
					Map<QName, String> map = bizLocationExtensionType
							.getOtherAttributes();
					Map<String, String> map2Save = new HashMap<String, String>();
					Iterator<QName> iter = map.keySet().iterator();
					while (iter.hasNext()) {
						QName qName = iter.next();
						String value = map.get(qName);
						map2Save.put(qName.toString(), value);
					}
					extension.put("otherAttributes", map2Save);
				}
				bizLocation.put("extension", extension);
			}
			dbo.put("bizLocation", bizLocation);
		}

		if (transformationEventType.getBizTransactionList() != null) {
			BusinessTransactionListType bizListType = transformationEventType
					.getBizTransactionList();
			List<BusinessTransactionType> bizList = bizListType
					.getBizTransaction();

			List<DBObject> bizTranList = new ArrayList<DBObject>();
			for (int i = 0; i < bizList.size(); i++) {
				BusinessTransactionType bizTranType = bizList.get(i);
				if (bizTranType.getType() != null
						&& bizTranType.getValue() != null) {
					DBObject dbObj = new BasicDBObject();
					dbObj.put(bizTranType.getType(), bizTranType.getValue());
					bizTranList.add(dbObj);
				}
			}
			dbo.put("bizTransactionList", bizTranList);
		}

		if (transformationEventType.getInputQuantityList() != null) {
			QuantityListType qetl = transformationEventType
					.getInputQuantityList();
			List<QuantityElementType> qetList = qetl.getQuantityElement();
			List<DBObject> quantityList = new ArrayList<DBObject>();
			for (int i = 0; i < qetList.size(); i++) {
				DBObject quantity = new BasicDBObject();
				QuantityElementType qet = qetList.get(i);
				if (qet.getEpcClass() != null)
					quantity.put("epcClass", qet.getEpcClass().toString());
				quantity.put("quantity", qet.getQuantity());
				if (qet.getUom() != null)
					quantity.put("uom", qet.getUom().toString());
				quantityList.add(quantity);
			}
			dbo.put("inputQuantityList", quantityList);
		}
		if (transformationEventType.getOutputQuantityList() != null) {
			QuantityListType qetl = transformationEventType
					.getOutputQuantityList();
			List<QuantityElementType> qetList = qetl.getQuantityElement();
			List<DBObject> quantityList = new ArrayList<DBObject>();
			for (int i = 0; i < qetList.size(); i++) {
				DBObject quantity = new BasicDBObject();
				QuantityElementType qet = qetList.get(i);
				if (qet.getEpcClass() != null)
					quantity.put("epcClass", qet.getEpcClass().toString());
				quantity.put("quantity", qet.getQuantity());
				if (qet.getUom() != null)
					quantity.put("uom", qet.getUom().toString());
				quantityList.add(quantity);
			}
			dbo.put("outputQuantityList", quantityList);
		}
		if (transformationEventType.getSourceList() != null) {
			SourceListType sdtl = transformationEventType.getSourceList();
			List<SourceDestType> sdtList = sdtl.getSource();
			List<DBObject> dbList = new ArrayList<DBObject>();
			for (int i = 0; i < sdtList.size(); i++) {
				SourceDestType sdt = sdtList.get(i);
				DBObject dbObj = new BasicDBObject();
				dbObj.put(sdt.getType(), sdt.getValue());
				dbList.add(dbObj);
			}
			dbo.put("sourceList", dbList);
		}
		if (transformationEventType.getDestinationList() != null) {
			DestinationListType sdtl = transformationEventType
					.getDestinationList();
			List<SourceDestType> sdtList = sdtl.getDestination();
			List<DBObject> dbList = new ArrayList<DBObject>();
			for (int i = 0; i < sdtList.size(); i++) {
				SourceDestType sdt = sdtList.get(i);
				DBObject dbObj = new BasicDBObject();
				dbObj.put(sdt.getType(), sdt.getValue());
				dbList.add(dbObj);
			}
			dbo.put("destinationList", dbList);
		}

		if (transformationEventType.getIlmd() != null) {
			ILMDType ilmd = transformationEventType.getIlmd();
			if (ilmd.getExtension() != null) {
				ILMDExtensionType ilmdExtension = ilmd.getExtension();
				Map<String, String> map2Save = new HashMap<String, String>();
				List<Object> objList = ilmdExtension.getAny();
				for (int i = 0; i < objList.size(); i++) {
					Object obj = objList.get(i);
					if (obj instanceof Element) {
						Element element = (Element) obj;
						if (element.getFirstChild() != null) {
							String name = element.getLocalName();
							String value = element.getFirstChild()
									.getTextContent();
							map2Save.put(name, value);
						}
					}
				}
				if (map2Save != null)
					dbo.put("ilmd", map2Save);
			}

		}

		// Extension
		DBObject extension = new BasicDBObject();
		if (transformationEventType.getExtension() != null) {
			TransformationEventExtensionType oee = transformationEventType
					.getExtension();
			if (oee.getAny() != null) {
				Map<String, String> map2Save = new HashMap<String, String>();
				List<Object> objList = oee.getAny();
				for (int i = 0; i < objList.size(); i++) {
					Object obj = objList.get(i);
					if (obj instanceof Element) {
						Element element = (Element) obj;
						if (element.getFirstChild() != null) {
							String name = element.getNodeName();
							String value = element.getFirstChild()
									.getTextContent();
							map2Save.put(name, value);
						}
					}
				}
				if (map2Save != null)
					extension.put("any", map2Save);
			}

			if (oee.getOtherAttributes() != null) {
				Map<QName, String> map = oee.getOtherAttributes();
				Map<String, String> map2Save = new HashMap<String, String>();
				Iterator<QName> iter = map.keySet().iterator();
				while (iter.hasNext()) {
					QName qName = iter.next();
					String value = map.get(qName);
					map2Save.put(qName.toString(), value);
				}
				extension.put("otherAttributes", map2Save);
			}
		}
		dbo.put("extension", extension);
		return dbo;
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
