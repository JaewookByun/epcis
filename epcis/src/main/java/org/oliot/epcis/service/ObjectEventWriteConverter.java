package org.oliot.epcis.service;

import org.apache.axis.message.MessageElement;
import org.json.JSONArray;
import org.json.JSONObject;
import org.oliot.epcis.model.BusinessTransactionType;
import org.oliot.epcis.model.EPC;
import org.oliot.epcis.model.ObjectEventExtension2Type;
import org.oliot.epcis.model.ObjectEventExtensionType;
import org.oliot.epcis.model.ObjectEventType;
import org.oliot.epcis.model.QuantityElementType;
import org.oliot.epcis.model.SourceDestType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@Component
@WritingConverter
public class ObjectEventWriteConverter implements
		Converter<ObjectEventType, DBObject> {

	public DBObject convert(ObjectEventType objectEventType) {

		DBObject dbo = new BasicDBObject();
		if (objectEventType.getEventTime() != null)
			dbo.put("eventTime", objectEventType.getEventTime()
					.getTimeInMillis());
		if (objectEventType.getEventTimeZoneOffset() != null)
			dbo.put("eventTimeZoneOffset",
					objectEventType.getEventTimeZoneOffset());
		if (objectEventType.getRecordTime() != null)
			dbo.put("recordTime", objectEventType.getRecordTime()
					.getTimeInMillis());
		if (objectEventType.getEpcList() != null) {
			EPC[] epcs = objectEventType.getEpcList();
			String[] epcsStr = new String[epcs.length];
			for (int i = 0; i < epcs.length; i++) {
				epcsStr[i] = epcs[i].getValue();
			}
			dbo.put("epcList", epcsStr);
		}
		if (objectEventType.getAction() != null)
			dbo.put("action", objectEventType.getAction().getValue());
		if (objectEventType.getBizStep() != null)
			dbo.put("bizStep", objectEventType.getBizStep().toString());
		if (objectEventType.getDisposition() != null)
			dbo.put("disposition", objectEventType.getDisposition().toString());
		if (objectEventType.getReadPoint() != null)
			dbo.put("readPoint", objectEventType.getReadPoint().getId()
					.toString());
		if (objectEventType.getBizLocation() != null)
			dbo.put("bizLocation", objectEventType.getBizLocation().getId()
					.toString());
		if (objectEventType.getBizTransactionList() != null) {
			BusinessTransactionType[] bizList = objectEventType
					.getBizTransactionList();
			String[] bizListArr = new String[bizList.length];
			for (int i = 0; i < bizList.length; i++) {
				bizListArr[i] = bizList[i].getType().toString();
			}
			dbo.put("bizTransactionList", bizListArr);
		}
		// Extension
		DBObject extension = new BasicDBObject();
		if (objectEventType.getExtension() != null) {
			ObjectEventExtensionType oee = objectEventType.getExtension();
			if (oee.getQuantityList() != null) {
				QuantityElementType[] qetl = oee.getQuantityList();
				JSONArray quantityList = new JSONArray();
				for (int i = 0; i < qetl.length; i++) {
					JSONObject quantity = new JSONObject();
					QuantityElementType qet = qetl[i];
					if (qet.getEpcClass() != null)
						quantity.put("epcClass", qet.getEpcClass().toString());
					quantity.put("quantity", qet.getQuantity());
					if (qet.getUom() != null)
						quantity.put("uom", qet.getUom().toString());
					quantityList.put(quantity);
				}
				extension.put("quantityList", quantityList);
			}
			if (oee.getSourceList() != null) {
				SourceDestType[] sdtl = oee.getSourceList();
				String[] sdtlArr = new String[sdtl.length];
				for (int i = 0; i < sdtl.length; i++) {
					sdtlArr[i] = sdtl[i].getType().toString();
				}
				extension.put("sourceList", sdtlArr);
			}
			if (oee.getDestinationList() != null) {
				SourceDestType[] sdtl = oee.getDestinationList();
				String[] sdtlArr = new String[sdtl.length];
				for (int i = 0; i < sdtl.length; i++) {
					sdtlArr[i] = sdtl[i].getType().toString();
				}
				extension.put("destinationList", sdtlArr);
			}
			if (oee.getExtension() != null) {
				ObjectEventExtension2Type oee2 = oee.getExtension();
				MessageElement[] anys = oee2.get_any();
				if (anys.length >= 1) {
					MessageElement any = anys[0];
					DBObject attrObject = getDBObjectFromMessageElement(any);
					extension.put("extension", attrObject);
				}
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
