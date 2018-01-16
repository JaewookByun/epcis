package org.oliot.epcis.service.capture.mongodb;

import java.util.HashMap;
import java.util.List;
import org.json.JSONObject;
import org.lilliput.chronograph.cache.CachedChronoGraph;
import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.epcis.converter.mongodb.AggregationEventWriteConverter;
import org.oliot.epcis.converter.mongodb.MasterDataWriteConverter;
import org.oliot.epcis.converter.mongodb.ObjectEventWriteConverter;
import org.oliot.epcis.converter.mongodb.QuantityEventWriteConverter;
import org.oliot.epcis.converter.mongodb.TransactionEventWriteConverter;
import org.oliot.epcis.converter.mongodb.TransformationEventWriteConverter;
import org.oliot.epcis.service.subscription.TriggerEngine;
import org.oliot.model.epcis.AggregationEventType;
import org.oliot.model.epcis.EPCISEventListExtensionType;
import org.oliot.model.epcis.ObjectEventType;
import org.oliot.model.epcis.QuantityEventType;
import org.oliot.model.epcis.TransactionEventType;
import org.oliot.model.epcis.TransformationEventType;
import org.oliot.model.epcis.VocabularyType;

import com.mongodb.MongoBulkWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.InsertManyOptions;

/**
 * Copyright (C) 2014-2016 Jaewook Byun
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
 */

public class MongoCaptureUtil {

	public HashMap<String, Object> capture(List<BsonDocument> bsonDocumentList) {
		HashMap<String, Object> retMsg = new HashMap<String, Object>();
		MongoCollection<BsonDocument> collection = Configuration.mongoDatabase.getCollection("EventData",
				BsonDocument.class);
		try {
			InsertManyOptions option = new InsertManyOptions();
			option.ordered(false);
			collection.insertMany(bsonDocumentList, option);
		} catch (MongoBulkWriteException e) {
			retMsg.put("error", e.getMessage());
			return retMsg;
		}
		retMsg.put("eventCaptured", bsonDocumentList.size());
		return retMsg;
	}

	public BsonDocument convert(Object event, String userID, String accessModifier, Integer gcpLength,
			CachedChronoGraph cg) {
		BsonDocument object2Save = null;
		String type = null;
		if (event instanceof AggregationEventType) {
			type = "AggregationEvent";
			AggregationEventWriteConverter wc = new AggregationEventWriteConverter();
			object2Save = wc.convert((AggregationEventType) event, gcpLength, cg);
		} else if (event instanceof ObjectEventType) {
			type = "ObjectEvent";
			ObjectEventWriteConverter wc = new ObjectEventWriteConverter();
			object2Save = wc.convert((ObjectEventType) event, gcpLength, cg);
		} else if (event instanceof QuantityEventType) {
			type = "QuantityEvent";
			QuantityEventWriteConverter wc = new QuantityEventWriteConverter();
			object2Save = wc.convert((QuantityEventType) event, gcpLength, cg);
		} else if (event instanceof TransactionEventType) {
			type = "TransactionEvent";
			TransactionEventWriteConverter wc = new TransactionEventWriteConverter();
			object2Save = wc.convert((TransactionEventType) event, gcpLength, cg);
		} else if (event instanceof EPCISEventListExtensionType) {
			type = "TransformationEvent";
			TransformationEventWriteConverter wc = new TransformationEventWriteConverter();
			object2Save = wc.convert(((EPCISEventListExtensionType) event).getTransformationEvent(), gcpLength, cg);
		}

		if (object2Save == null)
			return null;

		if (Configuration.isTriggerSupported == true) {
			TriggerEngine.examineAndFire(type, object2Save);
		}

		if (userID != null && accessModifier != null) {
			object2Save.put("userID", new BsonString(userID));
			object2Save.put("accessModifier", new BsonString(accessModifier));
		}
		return object2Save;
	}

	/* added for JSONcapture */
	public void captureJSONEvent(JSONObject event) {

		MongoCollection<BsonDocument> collection = Configuration.mongoDatabase.getCollection("EventData",
				BsonDocument.class);
		BsonDocument dbObject = BsonDocument.parse(event.toString());
		if (Configuration.isTriggerSupported == true) {
			TriggerEngine.examineAndFire("EventData", dbObject);
		}
		collection.insertOne(dbObject);
		Configuration.logger.info(" Event Saved ");
	}
	/* added for JSONcapture */

	public String capture(Object event, String userID, String accessModifier, Integer gcpLength, CachedChronoGraph cg) {
		MongoCollection<BsonDocument> collection = Configuration.mongoDatabase.getCollection("EventData",
				BsonDocument.class);
		BsonDocument object2Save = null;
		String type = null;
		if (event instanceof AggregationEventType) {
			type = "AggregationEvent";
			AggregationEventWriteConverter wc = new AggregationEventWriteConverter();
			object2Save = wc.convert((AggregationEventType) event, gcpLength, cg);
		} else if (event instanceof ObjectEventType) {
			type = "ObjectEvent";
			ObjectEventWriteConverter wc = new ObjectEventWriteConverter();
			object2Save = wc.convert((ObjectEventType) event, gcpLength, cg);
		} else if (event instanceof QuantityEventType) {
			type = "QuantityEvent";
			QuantityEventWriteConverter wc = new QuantityEventWriteConverter();
			object2Save = wc.convert((QuantityEventType) event, gcpLength, cg);
		} else if (event instanceof TransactionEventType) {
			type = "TransactionEvent";
			TransactionEventWriteConverter wc = new TransactionEventWriteConverter();
			object2Save = wc.convert((TransactionEventType) event, gcpLength, cg);
		} else if (event instanceof TransformationEventType) {
			type = "TransformationEvent";
			TransformationEventWriteConverter wc = new TransformationEventWriteConverter();
			object2Save = wc.convert((TransformationEventType) event, gcpLength, cg);
		}

		if (object2Save == null)
			return null;

		if (Configuration.isTriggerSupported == true) {
			TriggerEngine.examineAndFire(type, object2Save);
		}

		if (userID != null && accessModifier != null) {
			object2Save.put("userID", new BsonString(userID));
			object2Save.put("accessModifier", new BsonString(accessModifier));
		}
		collection.insertOne(object2Save);
		Configuration.logger.info(" Event Saved ");

		/*
		 * if (!object2Save.containsKey("errorDeclaration")) { if (userID != null &&
		 * accessModifier != null) { object2Save.put("userID", new BsonString(userID));
		 * object2Save.put("accessModifier", new BsonString(accessModifier)); }
		 * collection.insertOne(object2Save);
		 * Configuration.logger.info(" Event Saved "); } else { // Error Declaration
		 * Mechanism BsonDocument filter = object2Save.clone(); // Make 'otherwise
		 * identical' event filter filter = makeOtherwiseIdenticalFilter(filter);
		 * boolean isReplacing = replaceErroneousEvents(collection, filter,
		 * object2Save); if (isReplacing == true) { Configuration.logger.info(
		 * " Error Declaration succeed"); } else { Configuration.logger.info(
		 * " Error Declaration failed"); return "[ERROR] Error Declaration failed"; } }
		 */
		return null;
	}

	@SuppressWarnings("unused")
	private BsonDocument makeOtherwiseIdenticalFilter(BsonDocument filter) {
		filter.remove("errorDeclaration");
		filter.put("errorDeclaration", new BsonDocument("$exists", new BsonBoolean(false)));
		filter.remove("recordTime");
		return filter;
	}

	@SuppressWarnings("unused")
	private boolean replaceErroneousEvents(MongoCollection<BsonDocument> collection, BsonDocument filter,
			BsonDocument object2Save) {
		boolean isReplacing = false;
		while (true) {
			Object result = collection.findOneAndReplace(filter, object2Save);

			if (result == null)
				break;
			else {
				isReplacing = true;

			}
		}
		return isReplacing;
	}

	public String capture(VocabularyType vocabulary, String userID, String accessModifier, Integer gcpLength,
			CachedChronoGraph cg) {
		MasterDataWriteConverter mdConverter = new MasterDataWriteConverter();
		if (mdConverter.capture(vocabulary, gcpLength, cg) != 0) {
			return "[ERROR] Vocabulary Capture Failed";
		} else {
			return null;
		}
	}
}
