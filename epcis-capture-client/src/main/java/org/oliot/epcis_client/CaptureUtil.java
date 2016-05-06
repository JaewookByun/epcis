package org.oliot.epcis_client;

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
 * Copyright (C) 2014-16 Jaewook Byun
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

public class CaptureUtil {

	public BsonDocument putEventTime(BsonDocument base, Long eventTime) {
		base.put("eventTime", new BsonInt64(eventTime));
		return base;
	}

	public BsonDocument putEventTimeZoneOffset(BsonDocument base, String eventTimeZoneOffset) {
		base.put("eventTimeZoneOffset", new BsonString(eventTimeZoneOffset));
		return base;
	}

	public BsonDocument putAction(BsonDocument base, String action) {
		base.put("action", new BsonString(action));
		return base;
	}

	public BsonDocument putRecordTime(BsonDocument base, Long recordTime) {
		base.put("recordTime", new BsonInt64(recordTime));
		return base;
	}

	public BsonDocument putParentID(BsonDocument base, String parentID) {
		base.put("parentID", new BsonString(parentID));
		return base;
	}

	public BsonDocument putEPCList(BsonDocument base, List<String> epcList) {
		BsonArray bsonEPCList = new BsonArray();
		for (String epc : epcList) {
			bsonEPCList.add(new BsonDocument("epc", new BsonString(epc)));
		}
		base.put("epcList", bsonEPCList);
		return base;
	}

	public BsonDocument putChildEPCs(BsonDocument base, List<String> childEPCs) {
		BsonArray bsonEPCList = new BsonArray();
		for (String epc : childEPCs) {
			bsonEPCList.add(new BsonDocument("epc", new BsonString(epc)));
		}
		base.put("childEPCs", bsonEPCList);
		return base;
	}

	public BsonDocument putInputEPCList(BsonDocument base, List<String> inputEPCList) {
		BsonArray bsonEPCList = new BsonArray();
		for (String epc : inputEPCList) {
			bsonEPCList.add(new BsonDocument("epc", new BsonString(epc)));
		}
		base.put("inputEPCList", bsonEPCList);
		return base;
	}

	public BsonDocument putOutputEPCList(BsonDocument base, List<String> outputEPCList) {
		BsonArray bsonEPCList = new BsonArray();
		for (String epc : outputEPCList) {
			bsonEPCList.add(new BsonDocument("epc", new BsonString(epc)));
		}
		base.put("outputEPCList", bsonEPCList);
		return base;
	}

	public BsonDocument putTransformationID(BsonDocument base, String transformationID) {
		base.put("transformationID", new BsonString(transformationID));
		return base;
	}

	public BsonDocument putBizStep(BsonDocument base, String bizStep) {
		base.put("bizStep", new BsonString(bizStep));
		return base;
	}

	public BsonDocument putDisposition(BsonDocument base, String disposition) {
		base.put("disposition", new BsonString(disposition));
		return base;
	}

	public BsonDocument putReadPoint(BsonDocument base, String readPoint) {
		base.put("readPoint", new BsonDocument("id", new BsonString(readPoint)));
		return base;
	}

	public BsonDocument putBizLocation(BsonDocument base, String bizLocation) {
		base.put("bizLocation", new BsonDocument("id", new BsonString(bizLocation)));
		return base;
	}

	public BsonDocument putBizTransactionList(BsonDocument base, Map<String, List<String>> bizTransactionList) {
		BsonArray bsonBizTransactionList = new BsonArray();
		for (String key : bizTransactionList.keySet()) {
			List<String> list = bizTransactionList.get(key);
			for (String element : list) {
				bsonBizTransactionList.add(new BsonDocument(key, new BsonString(element)));
			}
		}
		base.put("bizTransactionList", bsonBizTransactionList);
		return base;
	}

	public BsonDocument putILMD(BsonDocument base, Map<String, String> namespaces,
			Map<String, Map<String, Object>> ilmds) {
		BsonDocument bsonILMD = new BsonDocument();
		for (String nsKey : ilmds.keySet()) {
			if (!namespaces.containsKey(nsKey))
				continue;
			bsonILMD.put("@" + nsKey, new BsonString(namespaces.get(nsKey)));
			Map<String, Object> ilmdElementList = ilmds.get(nsKey);
			for (String ilmdKey : ilmdElementList.keySet()) {
				Object ilmdElement = ilmdElementList.get(ilmdKey);
				if (ilmdElement instanceof Integer) {
					// Integer
					bsonILMD.put(nsKey + ":" + ilmdKey, new BsonInt32((Integer) ilmdElement));
				} else if (ilmdElement instanceof Long) {
					// Long
					bsonILMD.put(nsKey + ":" + ilmdKey, new BsonInt64((Long) ilmdElement));
				} else if (ilmdElement instanceof Double) {
					// Double
					bsonILMD.put(nsKey + ":" + ilmdKey, new BsonDouble((Double) ilmdElement));
				} else if (ilmdElement instanceof Boolean) {
					// Boolean
					bsonILMD.put(nsKey + ":" + ilmdKey, new BsonBoolean((Boolean) ilmdElement));
				} else {
					// String
					bsonILMD.put(nsKey + ":" + ilmdKey, new BsonString(ilmdElement.toString()));
				}
			}
		}
		base.put("ilmd", bsonILMD);
		return base;
	}

	public BsonDocument putExtensions(BsonDocument base, Map<String, String> namespaces,
			Map<String, Map<String, Object>> extensions) {
		BsonDocument any = new BsonDocument();
		for (String nsKey : extensions.keySet()) {
			if (!namespaces.containsKey(nsKey))
				continue;
			any.put("@" + nsKey, new BsonString(namespaces.get(nsKey)));
			Map<String, Object> extensionList = extensions.get(nsKey);
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
		base.put("any", any);
		return base;
	}

	public BsonDocument putQuantityList(BsonDocument base, List<QuantityElement> quantityList) {
		BsonArray quantityArray = new BsonArray();
		for (QuantityElement quantityElement : quantityList) {
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
		base.put("quantityList", quantityArray);
		return base;
	}

	public BsonDocument putChildQuantityList(BsonDocument base, List<QuantityElement> childQuantityList) {
		BsonArray quantityArray = new BsonArray();
		for (QuantityElement quantityElement : childQuantityList) {
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
		base.put("childQuantityList", quantityArray);
		return base;
	}

	public BsonDocument putInputQuantityList(BsonDocument base, List<QuantityElement> inputQuantityList) {
		BsonArray quantityArray = new BsonArray();
		for (QuantityElement quantityElement : inputQuantityList) {
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
		base.put("inputQuantityList", quantityArray);
		return base;
	}

	public BsonDocument putOutputQuantityList(BsonDocument base, List<QuantityElement> outputQuantityList) {
		BsonArray quantityArray = new BsonArray();
		for (QuantityElement quantityElement : outputQuantityList) {
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
		base.put("outputQuantityList", quantityArray);
		return base;
	}

	public BsonDocument putSourceList(BsonDocument base, Map<String, List<String>> sourceList) {
		BsonArray bsonSourceList = new BsonArray();
		for (String key : sourceList.keySet()) {
			List<String> sourceArrayList = sourceList.get(key);
			for (String source : sourceArrayList) {
				BsonDocument bsonSource = new BsonDocument(key, new BsonString(source));
				bsonSourceList.add(bsonSource);
			}
		}
		base.put("sourceList", bsonSourceList);
		return base;
	}

	public BsonDocument putDestinationList(BsonDocument base, Map<String, List<String>> destinationList) {
		BsonArray bsonDestinationList = new BsonArray();
		for (String key : destinationList.keySet()) {
			List<String> destinationArrayList = destinationList.get(key);
			for (String destination : destinationArrayList) {
				BsonDocument bsonDestination = new BsonDocument(key, new BsonString(destination));
				bsonDestinationList.add(bsonDestination);
			}
		}
		base.put("destinationList", bsonDestinationList);
		return base;
	}

	public BsonDocument putType(BsonDocument base, VocabularyType type) {
		base.put("type", new BsonString(type.getVocabularyType()));
		return base;
	}

	public BsonDocument putID(BsonDocument base, String id) {
		base.put("id", new BsonString(id));
		return base;
	}

	public BsonDocument putAttributes(BsonDocument base, Map<String, String> attributes) {
		BsonDocument bsonAttributes = new BsonDocument();
		for (String key : attributes.keySet()) {
			String value = attributes.get(key);
			bsonAttributes.put(encodeMongoObjectKey(key), new BsonString(value));
		}
		base.put("attributes", bsonAttributes);
		return base;
	}

	public BsonDocument putChildren(BsonDocument base, List<String> children) {
		BsonArray bsonChildren = new BsonArray();
		for (String child : children) {
			bsonChildren.add(new BsonString(child));
		}
		base.put("children", bsonChildren);
		return base;
	}

	public String encodeMongoObjectKey(String key) {
		key = key.replace(".", "\uff0e");
		return key;
	}
}
