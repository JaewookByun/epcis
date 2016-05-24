package org.oliot.epcis.service.capture.mongodb;

import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.BsonObjectId;
import org.bson.BsonString;
import org.bson.types.ObjectId;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.epcis.converter.mongodb.AggregationEventWriteConverter;
import org.oliot.epcis.converter.mongodb.MasterDataWriteConverter;
import org.oliot.epcis.converter.mongodb.ObjectEventWriteConverter;
import org.oliot.epcis.converter.mongodb.QuantityEventWriteConverter;
import org.oliot.epcis.converter.mongodb.TransactionEventWriteConverter;
import org.oliot.epcis.converter.mongodb.TransformationEventWriteConverter;
import org.oliot.epcis.service.subscription.TriggerEngine;
import org.oliot.model.epcis.AggregationEventType;
import org.oliot.model.epcis.ObjectEventType;
import org.oliot.model.epcis.QuantityEventType;
import org.oliot.model.epcis.TransactionEventType;
import org.oliot.model.epcis.TransformationEventType;
import org.oliot.model.epcis.VocabularyType;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

/**
 * Copyright (C) 2014-2016 Jaewook Byun
 *
 * This project is part of Oliot open source (http://oliot.org). Oliot EPCIS
 * v1.2.x is Java Web Service complying with Electronic Product Code Information
 * Service (EPCIS) v1.2.
 *
 * @author Jaewook Jack Byun, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         bjw0829@kaist.ac.kr, bjw0829@gmail.com
 */

public class MongoCaptureUtil {

	public void capture(Object event, String userID, String accessModifier, Integer gcpLength) {
		MongoCollection<BsonDocument> collection = Configuration.mongoDatabase.getCollection("EventData",
				BsonDocument.class);
		BsonDocument object2Save = null;
		String type = null;
		if (event instanceof AggregationEventType) {
			type = "AggregationEvent";
			AggregationEventWriteConverter wc = new AggregationEventWriteConverter();
			object2Save = wc.convert((AggregationEventType) event, gcpLength);
		} else if (event instanceof ObjectEventType) {
			type = "ObjectEvent";
			ObjectEventWriteConverter wc = new ObjectEventWriteConverter();
			object2Save = wc.convert((ObjectEventType) event, gcpLength);
		} else if (event instanceof QuantityEventType) {
			type = "QuantityEvent";
			QuantityEventWriteConverter wc = new QuantityEventWriteConverter();
			object2Save = wc.convert((QuantityEventType) event, gcpLength);
		} else if (event instanceof TransactionEventType) {
			type = "TransactionEvent";
			TransactionEventWriteConverter wc = new TransactionEventWriteConverter();
			object2Save = wc.convert((TransactionEventType) event, gcpLength);
		} else if (event instanceof TransformationEventType) {
			type = "TransformationEvent";
			TransformationEventWriteConverter wc = new TransformationEventWriteConverter();
			object2Save = wc.convert((TransformationEventType) event, gcpLength);
		}

		if (object2Save == null)
			return;

		if (Configuration.isTriggerSupported == true) {
			TriggerEngine.examineAndFire(type, object2Save);
		}
		
		if (!object2Save.containsKey("errorDeclaration")) {
			if (userID != null && accessModifier != null) {
				object2Save.put("userID", new BsonString(userID));
				object2Save.put("accessModifier", new BsonString(accessModifier));
			}
			collection.insertOne(object2Save);
			Configuration.logger.info(" Event Saved ");
		} else {
			BsonDocument error = object2Save.getDocument("errorDeclaration");
			if (!object2Save.containsKey("eventID")) {
				// If no eventID found, error
				Configuration.logger.info(" Error Declaration failed");
			} else {
				BsonString eventID = object2Save.getString("eventID");
				MongoCursor<BsonDocument> cursor = collection.find(new BsonDocument("eventID", eventID)).iterator();
				if (cursor.hasNext()) {
					BsonDocument foundDoc = cursor.next();
					foundDoc.put("recordTime", new BsonDateTime(System.currentTimeMillis()));
					foundDoc.put("errorDeclaration", error);
					collection.findOneAndReplace(new BsonDocument("eventID", eventID), foundDoc);
				} else {
					// There is no matched event ID
					// Try to find with ObjectID
					try {
						MongoCursor<BsonDocument> cursor2 = collection
								.find(new BsonDocument("_id", new BsonObjectId(new ObjectId(eventID.getValue()))))
								.iterator();
						if (cursor2.hasNext()) {
							BsonDocument foundDoc2 = cursor2.next();
							foundDoc2.put("recordTime", new BsonInt64(System.currentTimeMillis()));
							foundDoc2.put("errorDeclaration", error);
							collection.findOneAndReplace(
									new BsonDocument("_id", new BsonObjectId(new ObjectId(eventID.getValue()))),
									foundDoc2);
						} else {
							Configuration.logger.info(" Error Declaration failed");
						}
					} catch (IllegalArgumentException e) {
						Configuration.logger.info(" Error Declaration failed");
					}
				}
			}
		}
	}
	
	public void capture(VocabularyType vocabulary, String userID, String accessModifier, Integer gcpLength) {
		MasterDataWriteConverter mdConverter = new MasterDataWriteConverter();
		mdConverter.capture(vocabulary, gcpLength);
	}
}
