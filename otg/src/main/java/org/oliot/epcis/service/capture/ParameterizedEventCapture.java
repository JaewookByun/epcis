package org.oliot.epcis.service.capture;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.epcis.converter.mongodb.MongoWriterUtil;
import org.oliot.epcis.converter.mongodb.model.AggregationEvent;
import org.oliot.epcis.converter.mongodb.model.ObjectEvent;
import org.oliot.epcis.converter.mongodb.model.QuantityElement;
import org.oliot.epcis.converter.mongodb.model.TransactionEvent;
import org.oliot.epcis.converter.mongodb.model.TransformationEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ServletContextAware;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * Copyright (C) 2014-2017 Jaewook Byun
 *
 * This project is part of Oliot open source (http://oliot.org). Oliot EPCIS
 * v1.2.x is Java Web Service complying with Electronic Product Code Information
 * Service (EPCIS) v1.2.
 *
 * @author Jaewook Byun, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         bjw0829@kaist.ac.kr, bjw0829@gmail.com
 * 
 * @author Sungpil Woo, Master student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         woosungpil@kaist.ac.kr, woosungpil7@gmail.com
 * 
 */

@Controller
@RequestMapping("/ParameterizedEventCapture")
public class ParameterizedEventCapture implements ServletContextAware {

	@Autowired
	ServletContext servletContext;

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> capture(@RequestParam String eventType, @RequestParam(required = false) String action,
			@RequestParam(required = false) String eventTime,
			@RequestParam(required = false) String eventTimeZoneOffset, @RequestParam(required = false) String parentID,
			@RequestParam(required = false) String epcList, @RequestParam(required = false) String inputEPCList,
			@RequestParam(required = false) String outputEPCList, @RequestParam(required = false) String childEPCs,
			@RequestParam(required = false) String quantityList,
			@RequestParam(required = false) String inputQuantityList,
			@RequestParam(required = false) String outputQuantityList,
			@RequestParam(required = false) String transformationID,
			@RequestParam(required = false) String childQuantityList, @RequestParam(required = false) String bizStep,
			@RequestParam(required = false) String disposition, @RequestParam(required = false) String readPoint,
			@RequestParam(required = false) String bizLocation,
			@RequestParam(required = false) String bizTransactionList,
			@RequestParam(required = false) String sourceList, @RequestParam(required = false) String destinationList,
			@RequestParam(required = false) String ilmds, @RequestParam(required = false) String extensions) {

		Configuration.logger.info(" EPCIS Parameter Event Capture Started.... ");

		// Prepare: Event Time
		Long eventTimeLong = null;

		SimpleDateFormat format = new SimpleDateFormat("XXX");

		try {
			eventTimeLong = Long.parseLong(eventTime);
		} catch (NumberFormatException e1) {
			eventTimeLong = System.currentTimeMillis();
		}

		try {
			if (eventTimeZoneOffset != null)
				format.parse(eventTimeZoneOffset);
			else
				eventTimeZoneOffset = format.format(new Date());
		} catch (IllegalArgumentException e2) {
			eventTimeZoneOffset = format.format(new Date());
		} catch (ParseException e) {
			eventTimeZoneOffset = format.format(new Date());
		}

		// Prepare: Action
		if (action == null)
			action = "OBSERVE";
		else if (!action.equals("ADD") && !action.equals("OBSERVE") && !action.equals("DELETE")) {
			action = "OBSERVE";
		}

		// Prepare: epcList
		List<String> epcArrayList = getCSVList(epcList);
		List<String> childEPCArrayList = getCSVList(childEPCs);
		List<String> inputEPCArrayList = getCSVList(inputEPCList);
		List<String> outputEPCArrayList = getCSVList(outputEPCList);

		// Prepare: BizTransactionList
		Map<String, List<String>> bizTransactionListMap = getTypeValueMap(bizTransactionList);

		// Prepare: sourceList
		Map<String, List<String>> sourceListMap = getTypeValueMap(sourceList);

		// Prepare: destinationList
		Map<String, List<String>> destinationListMap = getTypeValueMap(destinationList);

		// Prepare: Extension Fields & ILMD
		BsonDocument extensionDocument = getExtension(extensions);
		BsonDocument ilmdDocument = getExtension(ilmds);
		Map<String, String> namespaces = new HashMap<String, String>();
		namespaces = getNamespaces(extensionDocument, ilmdDocument);

		// Prepare: QuantityList
		List<QuantityElement> quantityArrayList = getQuantityElementList(quantityList);
		List<QuantityElement> childQuantityArrayList = getQuantityElementList(childQuantityList);
		List<QuantityElement> inputQuantityArrayList = getQuantityElementList(inputQuantityList);
		List<QuantityElement> outputQuantityArrayList = getQuantityElementList(outputQuantityList);

		MongoClient dbClient = new MongoClient();
		MongoDatabase db = dbClient.getDatabase(Configuration.databaseName);

		if (eventType.equals("ObjectEvent")) {
			ObjectEvent objectEvent = new ObjectEvent(eventTimeLong, eventTimeZoneOffset, action);

			if (!epcArrayList.isEmpty())
				objectEvent.setEpcList(epcArrayList);

			if (!quantityArrayList.isEmpty())
				objectEvent.setQuantityList(quantityArrayList);

			if (readPoint != null)
				objectEvent.setReadPoint(readPoint);

			if (bizLocation != null)
				objectEvent.setBizLocation(bizLocation);

			if (bizStep != null)
				objectEvent.setBizStep(bizStep);

			if (disposition != null)
				objectEvent.setDisposition(disposition);

			if (!bizTransactionListMap.isEmpty())
				objectEvent.setBizTransactionList(bizTransactionListMap);

			if (!sourceListMap.isEmpty())
				objectEvent.setSourceList(sourceListMap);

			if (!destinationListMap.isEmpty())
				objectEvent.setDestinationList(destinationListMap);

			if (!extensionDocument.isEmpty()) {
				objectEvent.setExtensions(extensionDocument);
				objectEvent.setNamespaces(namespaces);
			}

			if (!ilmdDocument.isEmpty()) {
				objectEvent.setIlmd(ilmdDocument);
				objectEvent.setNamespaces(namespaces);
			}

			MongoCollection<BsonDocument> eventCol = db.getCollection("EventData", BsonDocument.class);
			eventCol.insertOne(objectEvent.asBsonDocument());
		} else if (eventType.equals("AggregationEvent")) {
			AggregationEvent aggregationEvent = new AggregationEvent(eventTimeLong, eventTimeZoneOffset, action);

			if (parentID != null)
				aggregationEvent.setParentID(parentID);

			if (!childEPCArrayList.isEmpty())
				aggregationEvent.setChildEPCs(childEPCArrayList);

			if (!childQuantityArrayList.isEmpty())
				aggregationEvent.setChildQuantityList(childQuantityArrayList);

			if (readPoint != null)
				aggregationEvent.setReadPoint(readPoint);

			if (bizLocation != null)
				aggregationEvent.setBizLocation(bizLocation);

			if (bizStep != null)
				aggregationEvent.setBizStep(bizStep);

			if (disposition != null)
				aggregationEvent.setDisposition(disposition);

			if (!bizTransactionListMap.isEmpty())
				aggregationEvent.setBizTransactionList(bizTransactionListMap);

			if (!sourceListMap.isEmpty())
				aggregationEvent.setSourceList(sourceListMap);

			if (!destinationListMap.isEmpty())
				aggregationEvent.setDestinationList(destinationListMap);

			if (!extensionDocument.isEmpty()) {
				aggregationEvent.setExtensions(extensionDocument);
				aggregationEvent.setNamespaces(namespaces);
			}

			MongoCollection<BsonDocument> eventCol = db.getCollection("EventData", BsonDocument.class);
			eventCol.insertOne(aggregationEvent.asBsonDocument());
		} else if (eventType.equals("TransactionEvent")) {

			TransactionEvent transactionEvent = new TransactionEvent(eventTimeLong, eventTimeZoneOffset, action,
					bizTransactionListMap);

			if (parentID != null)
				transactionEvent.setParentID(parentID);

			if (!epcArrayList.isEmpty())
				transactionEvent.setEpcList(epcArrayList);

			if (!quantityArrayList.isEmpty())
				transactionEvent.setQuantityList(quantityArrayList);

			if (readPoint != null)
				transactionEvent.setReadPoint(readPoint);

			if (bizLocation != null)
				transactionEvent.setBizLocation(bizLocation);

			if (bizStep != null)
				transactionEvent.setBizStep(bizStep);

			if (disposition != null)
				transactionEvent.setDisposition(disposition);

			if (!sourceListMap.isEmpty())
				transactionEvent.setSourceList(sourceListMap);

			if (!destinationListMap.isEmpty())
				transactionEvent.setDestinationList(destinationListMap);

			if (!extensionDocument.isEmpty()) {
				transactionEvent.setExtensions(extensionDocument);
				transactionEvent.setNamespaces(namespaces);
			}

			MongoCollection<BsonDocument> eventCol = db.getCollection("EventData", BsonDocument.class);
			eventCol.insertOne(transactionEvent.asBsonDocument());
		} else if (eventType.equals("TransformationEvent")) {

			TransformationEvent transformationEvent = new TransformationEvent(eventTimeLong, eventTimeZoneOffset);

			if (!inputEPCArrayList.isEmpty())
				transformationEvent.setInputEPCList(inputEPCArrayList);
			if (!inputQuantityArrayList.isEmpty())
				transformationEvent.setInputQuantityList(inputQuantityArrayList);
			if (!outputEPCArrayList.isEmpty())
				transformationEvent.setOutputEPCList(outputEPCArrayList);
			if (!outputQuantityArrayList.isEmpty())
				transformationEvent.setOutputQuantityList(outputQuantityArrayList);

			if (transformationID != null)
				transformationEvent.setTransformationID(transformationID);

			if (readPoint != null)
				transformationEvent.setReadPoint(readPoint);
			if (bizLocation != null)
				transformationEvent.setBizLocation(bizLocation);

			if (bizStep != null)
				transformationEvent.setBizStep(bizStep);
			if (disposition != null)
				transformationEvent.setDisposition(disposition);

			if (!bizTransactionListMap.isEmpty())
				transformationEvent.setBizTransactionList(bizTransactionListMap);

			if (!sourceListMap.isEmpty())
				transformationEvent.setSourceList(sourceListMap);

			if (!destinationListMap.isEmpty())
				transformationEvent.setDestinationList(destinationListMap);

			if (!extensionDocument.isEmpty()) {
				transformationEvent.setExtensions(extensionDocument);
				transformationEvent.setNamespaces(namespaces);
			}

			if (!ilmdDocument.isEmpty()) {
				transformationEvent.setIlmds(ilmdDocument);
				transformationEvent.setNamespaces(namespaces);
			}

			MongoCollection<BsonDocument> eventCol = db.getCollection("EventData", BsonDocument.class);
			eventCol.insertOne(transformationEvent.asBsonDocument());
		}

		dbClient.close();

		System.out.println("Parameterized Event Captured");
		return new ResponseEntity<>("EPCIS Document : Captured ", HttpStatus.OK);
	}

	private List<String> getCSVList(String csv) {
		List<String> list = new ArrayList<String>();
		if (csv == null)
			return list;
		String[] arr = csv.split(",");
		for (String elem : arr) {
			list.add(elem.trim());
		}
		return list;
	}

	private Map<String, List<String>> getTypeValueMap(String stringRepresentation) {
		// Format: type|value,type|value,type|value...
		Map<String, List<String>> stringListMap = new HashMap<String, List<String>>();
		if (stringRepresentation == null)
			return stringListMap;
		String[] stringArray = stringRepresentation.split(",");
		for (String stringPair : stringArray) {
			String[] stringElements = stringPair.split("\\|");
			if (stringElements.length == 2) {
				String type = stringElements[0].trim();
				String value = stringElements[1].trim();
				if (stringListMap.containsKey(type)) {
					List<String> destList = stringListMap.get(type);
					destList.add(value);
					stringListMap.put(type, destList);
				} else {
					List<String> destList = new ArrayList<String>();
					destList.add(value);
					stringListMap.put(type, destList);
				}
			}
		}
		return stringListMap;
	}

	private BsonDocument getExtension(String stringRepresentation) {
		BsonDocument extensionMap = new BsonDocument();
		if (stringRepresentation == null)
			return extensionMap;
		// format: namespace|value^type, namespace|value^tpye, ...
		String[] extensionPairArr = stringRepresentation.split(",");
		for (String extensionPair : extensionPairArr) {
			String[] namespaceValuePair = extensionPair.split("\\|");
			if (namespaceValuePair.length == 2) {
				String namespace = namespaceValuePair[0];
				String valueString = namespaceValuePair[1];
				String[] valueTypePair = valueString.split("\\^");
				if (valueTypePair.length != 2) {
					extensionMap.put(namespace, new BsonString(valueString));
				} else {
					String val = valueTypePair[0].trim();
					String type = valueTypePair[1].trim();
					BsonValue bsonValue;
					if (type.equals("int")) {
						bsonValue = new BsonInt32(Integer.parseInt(val));
					} else if (type.equals("long")) {
						bsonValue = new BsonInt64(Long.parseLong(val));
					} else if (type.equals("double")) {
						bsonValue = new BsonDouble(Double.parseDouble(val));
					} else if (type.equals("boolean")) {
						bsonValue = new BsonBoolean(Boolean.parseBoolean(val));
					} else if (type.equals("float")) {
						bsonValue = new BsonDouble(Double.parseDouble(val));
					} else if (type.equals("dateTime")) {
						BsonValue temp = MongoWriterUtil.getBsonDateTime(val);
						if (temp != null)
							bsonValue = temp;
						else
							bsonValue = new BsonString(val);
					} else {
						bsonValue = new BsonString(val);
					}
					extensionMap.put(namespace, bsonValue);
				}
			}
		}
		return extensionMap;
	}

	private Map<String, String> getNamespaces(BsonDocument extensionDocument, BsonDocument ilmdDocument) {
		Map<String, String> namespaces = new HashMap<String, String>();

		int cnt = 0;
		if (!extensionDocument.isEmpty()) {
			Iterator<String> iterator = extensionDocument.keySet().iterator();
			while (iterator.hasNext()) {
				String prefix = iterator.next().split("#")[0].trim();
				if (!namespaces.containsKey(prefix)) {
					namespaces.put(prefix, "ns" + cnt++);
				}
			}
		}
		if (!ilmdDocument.isEmpty()) {
			Iterator<String> iterator = ilmdDocument.keySet().iterator();
			while (iterator.hasNext()) {
				String prefix = iterator.next().split("#")[0].trim();
				if (!namespaces.containsKey(prefix)) {
					namespaces.put(prefix, "ns" + cnt++);
				}
			}
		}
		return namespaces;
	}

	private List<QuantityElement> getQuantityElementList(String quantityList) {
		List<QuantityElement> quantityElementList = new ArrayList<QuantityElement>();
		// format: class|qnt|uom, class|qnt|uom, ...
		if (quantityList == null)
			return quantityElementList;
		String[] tripleArr = quantityList.split(",");
		for (String triple : tripleArr) {
			QuantityElement qe = new QuantityElement();
			String[] tripleElement = triple.split("\\|");
			if (tripleElement.length == 3) {
				if (tripleElement[0].trim().length() > 0) {
					qe.setEpcClass(tripleElement[0].trim());
					try {
						qe.setQuantity(Double.parseDouble(tripleElement[1]));
					} catch (NumberFormatException e) {

					}
					if (tripleElement[2].trim().length() > 0)
						qe.setUom(tripleElement[2].trim());
					quantityElementList.add(qe);
				}
			}
		}
		return quantityElementList;
	}
}
