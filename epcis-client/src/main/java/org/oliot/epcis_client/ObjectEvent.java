package org.oliot.epcis_client;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.BsonArray;
import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonString;

/**
 * Copyright (C) 2014-16 Jaewook Jack Byun
 *
 * This project is part of Oliot (oliot.org), pursuing the implementation of
 * Electronic Product Code Information Service(EPCIS) v1.1 specification in
 * EPCglobal.
 * [http://www.gs1.org/gsmp/kc/epcglobal/epcis/epcis_1_1-standard-20140520.pdf]
 * 
 *
 * @author Jaewook Jack Byun, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         bjw0829@kaist.ac.kr, bjw0829@gmail.com
 */
public class ObjectEvent {

	// EventTime, EventTimeZoneOffset,Action required
	private long eventTime;
	private long recordTime;
	private String eventTimeZoneOffset;

	private List<String> epcList;
	private List<QuantityElement> quantityList;
	private String action;
	private String bizStep;
	private String disposition;
	private String readPoint;
	private String bizLocation;
	private Map<String, List<String>> bizTransactionList;
	private Map<String, List<String>> sourceList;
	private Map<String, List<String>> destinationList;
	private Map<String, String> namespaces;
	private Map<String, Map<String, Object>> ilmd;
	private Map<String, Map<String, Object>> extensions;

	public ObjectEvent() {
		eventTime = System.currentTimeMillis();
		SimpleDateFormat format = new SimpleDateFormat("XXX");
		eventTimeZoneOffset = format.format(new Date());
		recordTime = 0;
		action = "OBSERVE";
		epcList = new ArrayList<String>();
		quantityList = new ArrayList<QuantityElement>();
		bizTransactionList = new HashMap<String, List<String>>();
		sourceList = new HashMap<String, List<String>>();
		destinationList = new HashMap<String, List<String>>();
		namespaces = new HashMap<String, String>();
		ilmd = new HashMap<String, Map<String, Object>>();
		extensions = new HashMap<String, Map<String, Object>>();
	}

	public ObjectEvent(long eventTime, String eventTimeZoneOffset, String action) {
		this.eventTime = eventTime;
		this.eventTimeZoneOffset = eventTimeZoneOffset;
		this.action = action;
		recordTime = 0;
		epcList = new ArrayList<String>();
		quantityList = new ArrayList<QuantityElement>();
		bizTransactionList = new HashMap<String, List<String>>();
		sourceList = new HashMap<String, List<String>>();
		destinationList = new HashMap<String, List<String>>();
		namespaces = new HashMap<String, String>();
		ilmd = new HashMap<String, Map<String, Object>>();
		extensions = new HashMap<String, Map<String, Object>>();
	}

	public long getEventTime() {
		return eventTime;
	}

	public void setEventTime(long eventTime) {
		this.eventTime = eventTime;
	}

	public long getRecordTime() {
		return recordTime;
	}

	public void setRecordTime(long recordTime) {
		this.recordTime = recordTime;
	}

	public String getEventTimeZoneOffset() {
		return eventTimeZoneOffset;
	}

	public void setEventTimeZoneOffset() {
		SimpleDateFormat format = new SimpleDateFormat("XXX");
		eventTimeZoneOffset = format.format(new Date());
	}

	public void setEventTimeZoneOffset(String eventTimeZoneOffset) {
		this.eventTimeZoneOffset = eventTimeZoneOffset;
	}

	public List<String> getEpcList() {
		return epcList;
	}

	public void setEpcList(List<String> epcList) {
		this.epcList = epcList;
	}

	public List<QuantityElement> getQuantityList() {
		return quantityList;
	}

	public void setQuantityList(List<QuantityElement> quantityList) {
		this.quantityList = quantityList;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
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

	public String getReadPoint() {
		return readPoint;
	}

	public void setReadPoint(String readPoint) {
		this.readPoint = readPoint;
	}

	public String getBizLocation() {
		return bizLocation;
	}

	public void setBizLocation(String bizLocation) {
		this.bizLocation = bizLocation;
	}

	public Map<String, List<String>> getBizTransactionList() {
		return bizTransactionList;
	}

	public void setBizTransactionList(Map<String, List<String>> bizTransactionList) {
		this.bizTransactionList = bizTransactionList;
	}

	public Map<String, List<String>> getSourceList() {
		return sourceList;
	}

	public void setSourceList(Map<String, List<String>> sourceList) {
		this.sourceList = sourceList;
	}

	public Map<String, List<String>> getDestinationList() {
		return destinationList;
	}

	public void setDestinationList(Map<String, List<String>> destinationList) {
		this.destinationList = destinationList;
	}

	public Map<String, String> getNamespaces() {
		return namespaces;
	}

	public void setNamespaces(Map<String, String> namespaces) {
		this.namespaces = namespaces;
	}

	public Map<String, Map<String, Object>> getIlmd() {
		return ilmd;
	}

	public void setIlmd(Map<String, Map<String, Object>> ilmd) {
		this.ilmd = ilmd;
	}

	public Map<String, Map<String, Object>> getExtensions() {
		return extensions;
	}

	public void setExtensions(Map<String, Map<String, Object>> extensions) {
		this.extensions = extensions;
	}

	public BsonDocument asBsonDocument() {
		BsonDocument objectEvent = new BsonDocument();
		// Required Fields
		objectEvent.put("eventTime", new BsonInt64(this.eventTime));
		objectEvent.put("eventTimeZoneOffset", new BsonString(this.eventTimeZoneOffset));
		objectEvent.put("action", new BsonString(this.action));

		// Optional Fields
		if (this.recordTime != 0) {
			objectEvent.put("recordTime", new BsonInt64(this.recordTime));
		}
		if (this.epcList != null && this.epcList.size() != 0) {
			BsonArray bsonEPCList = new BsonArray();
			for (String epc : this.epcList) {
				bsonEPCList.add(new BsonDocument("epc", new BsonString(epc)));
			}
			objectEvent.put("epcList", bsonEPCList);
		}
		if (this.bizStep != null) {
			objectEvent.put("bizStep", new BsonString(this.bizStep));
		}
		if (this.disposition != null) {
			objectEvent.put("disposition", new BsonString(this.disposition));
		}
		if (this.readPoint != null) {
			objectEvent.put("readPoint", new BsonDocument("id", new BsonString(this.readPoint)));
		}
		if (this.bizLocation != null) {
			objectEvent.put("bizLocation", new BsonDocument("id", new BsonString(this.bizLocation)));
		}
		if (this.bizTransactionList != null && this.bizTransactionList.isEmpty() == false) {
			BsonArray bsonBizTransactionList = new BsonArray();
			for (String key : this.bizTransactionList.keySet()) {
				List<String> list = this.bizTransactionList.get(key);
				for (String element : list) {
					bsonBizTransactionList.add(new BsonDocument(key, new BsonString(element)));
				}
			}
			objectEvent.put("bizTransactionList", bsonBizTransactionList);
		}
		if (this.ilmd != null && this.ilmd.isEmpty() == false) {
			BsonDocument ilmd = new BsonDocument();
			for (String nsKey : this.ilmd.keySet()) {
				if (!this.namespaces.containsKey(nsKey))
					continue;
				ilmd.put("@" + nsKey, new BsonString(this.namespaces.get(nsKey)));
				Map<String, Object> ilmdElementList = this.ilmd.get(nsKey);
				for (String ilmdKey : ilmdElementList.keySet()) {
					Object ilmdElement = ilmdElementList.get(ilmdKey);
					if (ilmdElement instanceof Integer) {
						// Integer
						ilmd.put(nsKey + ":" + ilmdKey, new BsonInt32((Integer) ilmdElement));
					} else if (ilmdElement instanceof Long) {
						// Long
						ilmd.put(nsKey + ":" + ilmdKey, new BsonInt64((Long) ilmdElement));
					} else if (ilmdElement instanceof Double) {
						// Double
						ilmd.put(nsKey + ":" + ilmdKey, new BsonDouble((Double) ilmdElement));
					} else if (ilmdElement instanceof Boolean) {
						// Boolean
						ilmd.put(nsKey + ":" + ilmdKey, new BsonBoolean((Boolean) ilmdElement));
					} else {
						// String
						ilmd.put(nsKey + ":" + ilmdKey, new BsonString(ilmdElement.toString()));
					}
				}
			}
			objectEvent.put("ilmd", ilmd);
		}
		if (this.extensions != null && this.extensions.isEmpty() == false) {
			BsonDocument any = new BsonDocument();
			for (String nsKey : this.extensions.keySet()) {
				if (!this.namespaces.containsKey(nsKey))
					continue;
				any.put("@" + nsKey, new BsonString(this.namespaces.get(nsKey)));
				Map<String, Object> extensionList = this.extensions.get(nsKey);
				for (String extensionKey : extensionList.keySet()) {
					Object extensionElement = extensionList.get(extensionKey);
					if (extensionElement instanceof Integer) {
						// Integer
						any.put(nsKey + ":" + extensionKey, new BsonInt32((Integer) extensionElement));
					} else if (extensionElement instanceof Long) {
						// Long
						any.put(nsKey + ":" + extensionKey, new BsonInt64((Long) extensionElement));
					} else if (extensionElement instanceof Double) {
						// Double
						any.put(nsKey + ":" + extensionKey, new BsonDouble((Double) extensionElement));
					} else if (extensionElement instanceof Boolean) {
						// Boolean
						any.put(nsKey + ":" + extensionKey, new BsonBoolean((Boolean) extensionElement));
					} else {
						// String
						any.put(nsKey + ":" + extensionKey, new BsonString(extensionElement.toString()));
					}
				}
			}
			objectEvent.put("any", any);
		}

		BsonDocument extension = new BsonDocument();
		if (this.quantityList != null && this.quantityList.isEmpty() == false) {
			BsonArray quantityArray = new BsonArray();
			for (QuantityElement quantityElement : this.quantityList) {
				BsonDocument bsonQuantityElement = new BsonDocument("epcClass",
						new BsonString(quantityElement.getEpcClass()));
				if (quantityElement.getQuantity() != null) {
					bsonQuantityElement.put("quantity", new BsonDouble(quantityElement.getQuantity()));
				}
				if (quantityElement.getUom() != null) {
					bsonQuantityElement.put("uom", new BsonString(quantityElement.getUom()));
				}
				quantityArray.add(bsonQuantityElement);
			}
			extension.put("quantityList", quantityArray);
		}
		if (this.sourceList != null && this.sourceList.isEmpty() == false) {
			BsonArray bsonSourceList = new BsonArray();
			for (String key : this.sourceList.keySet()) {
				List<String> sourceArrayList = this.sourceList.get(key);
				for (String source : sourceArrayList) {
					BsonDocument bsonSource = new BsonDocument(key, new BsonString(source));
					bsonSourceList.add(bsonSource);
				}
			}
			extension.put("sourceList", bsonSourceList);
		}
		if (this.destinationList != null && this.destinationList.isEmpty() == false) {
			BsonArray bsonDestinationList = new BsonArray();
			for (String key : this.destinationList.keySet()) {
				List<String> destinationArrayList = this.destinationList.get(key);
				for (String destination : destinationArrayList) {
					BsonDocument bsonDestination = new BsonDocument(key, new BsonString(destination));
					bsonDestinationList.add(bsonDestination);
				}
			}
			extension.put("destinationList", bsonDestinationList);
		}
		if (extension.isEmpty() == false)
			objectEvent.put("extension", extension);

		return objectEvent;
	}
}
