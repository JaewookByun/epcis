package org.oliot.epcis.capture.common;

import com.mongodb.MongoException;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.oliot.epcis.server.EPCISServer;

/**
 * Copyright (C) 2020-2024. (Jaewook Byun) all rights reserved.
 * <p>
 * This project is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * TransactionManager holds event-bus handlers for processing capture jobs.
 * <p>
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr, Associate Director, Auto-ID Labs, Korea,
 *         bjw0829@gmail.com
 */
public class TransactionManager {

	/**
	 * GS1-Capture-Error-Behaviour = rollback running=true, success=true , still
	 * capturing, no error still running=true, success=false , error occurs, and
	 * rollback is in progress running=false, success=true , all events are captured
	 * running=false, success=false, all events are rejected
	 * <p>
	 * GS1-Capture-Error-Behaviour = proceed running=true, success=true, still
	 * capturing, no error still running=true, success=false,
	 *
	 * @param eventBus
	 */
	public static void registerTransactionStartHandler(EventBus eventBus) {
		eventBus.consumer("txStart", msg -> {
			//
			JsonObject tx = (JsonObject) msg.body();
			try {
				InsertOneResult result = EPCISServer.mTxCollection.insertOne(Transaction.toDocument(tx));
				EPCISServer.logger.debug("txStarts - " + tx + " | " + result);
			} catch (MongoException e) {
				throw new RuntimeException(e.getMessage());
			}
		});
	}

	public static void registerTransactionSuccessHandler(EventBus eventBus) {
		eventBus.consumer("txSuccess", msg -> {
			JsonObject tx = (JsonObject) msg.body();
			long finishedAt = System.currentTimeMillis();

			try {
				UpdateResult result = EPCISServer.mTxCollection.updateOne(
						new Document().append("_id", new ObjectId(tx.getString("_id"))),
						new Document().append("$set", new Document().append("running", false).append("success", true)
								.append("finishedAt", finishedAt)));
				tx.put("running", false).put("success", true).put("finishAt", finishedAt);
				EPCISServer.logger.debug("txSuccess - " + tx + " | " + result);
			} catch (MongoException e) {
				throw new RuntimeException(e.getMessage());
			}
		});
	}

	public static void registerTransactionProceedHandler(EventBus eventBus) {
		eventBus.consumer("txProceed", msg -> {
			JsonObject tx = (JsonObject) msg.body();
			long finishedAt = System.currentTimeMillis();

			try {
				UpdateResult result = EPCISServer.mTxCollection.updateOne(
						new Document().append("_id", new ObjectId(tx.getString("_id"))),
						new Document().append("$set",
								new Document().append("running", false).append("success", false)
										.append("errorType", tx.getString("errorType"))
										.append("errorMessage", tx.getString("errorMessage"))
										.append("finishedAt", finishedAt)));
				tx.put("running", false).put("success", false).put("errorType", tx.getString("errorType"))
						.put("errorMessage", tx.getString("errorMessage")).put("finishedAt", finishedAt);
				EPCISServer.logger.debug("txProceed - " + tx + " | " + result);

			} catch (MongoException e) {
				throw new RuntimeException(e.getMessage());
			}

		});
	}

	public static void registerTransactionRollBackHandler(EventBus eventBus) {
		eventBus.consumer("txRollback", msg -> {
			JsonObject tx = (JsonObject) msg.body();

			try {
				UpdateResult result = EPCISServer.mTxCollection
						.updateOne(new Document().append("_id", new ObjectId(tx.getString("_id"))),
								new Document().append("$set",
										new Document().append("success", false)
												.append("errorType", tx.getString("errorType"))
												.append("errorMessage", tx.getString("errorMessage"))));
				tx.put("success", false).put("errorType", tx.getString("errorType")).put("errorMessage",
						tx.getString("errorMessage"));
				EPCISServer.logger.debug(tx + " rollback starts" + " | " + result);

				EPCISServer.mEventCollection
						.deleteMany(new Document().append("_tx", new ObjectId(tx.getString("_id"))));
				long finishedAt = System.currentTimeMillis();

				result = EPCISServer.mTxCollection.updateOne(
						new Document().append("_id", new ObjectId(tx.getString("_id"))), new Document().append("$set",
								new Document().append("running", false).append("finishedAt", finishedAt)));
				tx.put("finishedAt", finishedAt);
				EPCISServer.logger.debug("txRollback - " + tx + " | " + result);
			} catch (MongoException e) {
				throw new RuntimeException(e.getMessage());
			}
		});
	}
}
