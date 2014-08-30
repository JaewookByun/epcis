package org.oliot.epcis.serde;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.axis.message.MessageElement;
import org.oliot.model.epcis.AggregationEventExtension2Type;
import org.oliot.model.epcis.AggregationEventExtensionType;
import org.oliot.model.epcis.AggregationEventType;
import org.oliot.model.epcis.BusinessLocationExtensionType;
import org.oliot.model.epcis.BusinessLocationType;
import org.oliot.model.epcis.BusinessTransactionListType;
import org.oliot.model.epcis.BusinessTransactionType;
import org.oliot.model.epcis.DestinationListType;
import org.oliot.model.epcis.EPC;
import org.oliot.model.epcis.EPCISEventExtensionType;
import org.oliot.model.epcis.EPCListType;
import org.oliot.model.epcis.ReadPointExtensionType;
import org.oliot.model.epcis.ReadPointType;
import org.oliot.model.epcis.SourceDestType;
import org.oliot.model.epcis.SourceListType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@Component
@WritingConverter
public class AggregationEventWriteConverter implements
		Converter<AggregationEventType, DBObject> {

	public DBObject convert(AggregationEventType aggregationEventType) {

		DBObject dbo = new BasicDBObject();
		if (aggregationEventType.getBaseExtension() != null) {
			EPCISEventExtensionType baseExtensionType = aggregationEventType
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
		if (aggregationEventType.getEventTime() != null)
			dbo.put("eventTime", aggregationEventType.getEventTime()
					.toGregorianCalendar().getTimeInMillis());
		if (aggregationEventType.getEventTimeZoneOffset() != null)
			dbo.put("eventTimeZoneOffset",
					aggregationEventType.getEventTimeZoneOffset());
		if (aggregationEventType.getRecordTime() != null)
			dbo.put("recordTime", aggregationEventType.getRecordTime()
					.toGregorianCalendar().getTimeInMillis());
		if (aggregationEventType.getChildEPCs() != null) {
			EPCListType epcs = aggregationEventType.getChildEPCs();
			List<EPC> epcList = epcs.getEpc();
			List<DBObject> epcDBList = new ArrayList<DBObject>();
			
			for (int i = 0; i < epcList.size(); i++) {
				DBObject epcDB = new BasicDBObject();
				epcDB.put("epc", epcList.get(i).getValue());
				epcDBList.add(epcDB);
			}
			dbo.put("epcList", epcDBList);
		}
		if (aggregationEventType.getAction() != null)
			dbo.put("action", aggregationEventType.getAction().name());
		if (aggregationEventType.getBizStep() != null)
			dbo.put("bizStep", aggregationEventType.getBizStep());
		if (aggregationEventType.getDisposition() != null)
			dbo.put("disposition", aggregationEventType.getDisposition());
		if (aggregationEventType.getReadPoint() != null) {
			ReadPointType readPointType = aggregationEventType.getReadPoint();
			DBObject readPoint = new BasicDBObject();
			if (readPointType.getId() != null)
				readPoint.put("id", readPointType.getId());
			ReadPointExtensionType readPointExtensionType = readPointType
					.getExtension();
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
			dbo.put("readPoint", readPoint);
		}
		if (aggregationEventType.getBizLocation() != null) {
			BusinessLocationType bizLocationType = aggregationEventType
					.getBizLocation();
			DBObject bizLocation = new BasicDBObject();
			if (bizLocationType.getId() != null)
				bizLocation.put("id", bizLocationType.getId());
			BusinessLocationExtensionType bizLocationExtensionType = bizLocationType
					.getExtension();
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
			dbo.put("readPoint", bizLocation);
		}

		if (aggregationEventType.getBizTransactionList() != null) {
			BusinessTransactionListType bizListType = aggregationEventType
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
		// Extension
		DBObject extension = new BasicDBObject();
		if (aggregationEventType.getExtension() != null) {
			AggregationEventExtensionType oee = aggregationEventType.getExtension();
			if (oee.getSourceList() != null) {
				SourceListType sdtl = oee.getSourceList();
				List<SourceDestType> sdtList = sdtl.getSource();
				List<DBObject> dbList = new ArrayList<DBObject>();
				for (int i = 0; i < sdtList.size(); i++) {
					SourceDestType sdt = sdtList.get(i);
					DBObject dbObj = new BasicDBObject();
					dbObj.put(sdt.getType(), sdt.getValue());
					dbList.add(dbObj);
				}
				extension.put("sourceList", dbList);
			}
			if (oee.getDestinationList() != null) {
				DestinationListType sdtl = oee.getDestinationList();
				List<SourceDestType> sdtList = sdtl.getDestination();
				List<DBObject> dbList = new ArrayList<DBObject>();
				for (int i = 0; i < sdtList.size(); i++) {
					SourceDestType sdt = sdtList.get(i);
					DBObject dbObj = new BasicDBObject();
					dbObj.put(sdt.getType(), sdt.getValue());
					dbList.add(dbObj);
				}
				extension.put("destinationList", dbList);
			}
			if (oee.getExtension() != null) {
				AggregationEventExtension2Type extension2Type = oee.getExtension();
				DBObject extension2 = new BasicDBObject();
				if (extension2Type.getAny() != null) {
					Map<String, String> map2Save = new HashMap<String, String>();
					List<Object> objList = extension2Type.getAny();
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
						extension2.put("any", map2Save);
				}

				if (extension2Type.getOtherAttributes() != null) {
					Map<QName, String> map = extension2Type.getOtherAttributes();
					Map<String, String> map2Save = new HashMap<String, String>();
					Iterator<QName> iter = map.keySet().iterator();
					while (iter.hasNext()) {
						QName qName = iter.next();
						String value = map.get(qName);
						map2Save.put(qName.toString(), value);
					}
					extension2.put("otherAttributes", map2Save);
				}
				extension.put("extension", extension2);
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
