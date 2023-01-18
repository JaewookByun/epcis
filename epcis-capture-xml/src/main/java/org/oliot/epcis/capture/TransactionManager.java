package org.oliot.epcis.capture;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.oliot.epcis.util.ObservableSubscriber;

import static org.oliot.epcis.capture.XMLCaptureServer.mTxCollection;
import static org.oliot.epcis.capture.XMLCaptureServer.logger;
import static org.oliot.epcis.capture.XMLCaptureServer.mEventCollection;

/**
 * Copyright (C) 2020-2023. (Jaewook Byun) all rights reserved.
 * <p>
 * This project is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * Among various modules, epcis-capture-xml acts as a server to receive
 * XML-formatted EPCIS documents to capture events in the documents into an
 * EPCIS repository.
 * <p>
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 * jwbyun@sejong.ac.kr, Associate Director, Auto-ID Labs, Korea,
 * bjw0829@gmail.com
 */
public class TransactionManager {

    /**
     * GS1-Capture-Error-Behaviour = rollback
     * running=true, success=true , still capturing, no error still
     * running=true, success=false , error occurs, and rollback is in progress
     * running=false, success=true , all events are captured
     * running=false, success=false, all events are rejected
     * <p>
     * GS1-Capture-Error-Behaviour = proceed
     * running=true, success=true, still capturing, no error still
     * running=true, success=false,
     *
     * @param eventBus
     */
    void registerTransactionStartHandler(EventBus eventBus) {
        eventBus.consumer("txStart", msg -> {
            //
            JsonObject tx = (JsonObject) msg.body();
            ObservableSubscriber<InsertOneResult> subscriber = new ObservableSubscriber<InsertOneResult>();
            mTxCollection.insertOne(Transaction.toDocument(tx)).subscribe(subscriber);
            try {
                subscriber.await();
            } catch (Throwable e) {
                throw new RuntimeException(e.getMessage());
            }
            logger.debug("txStarts - " + tx);
        });
    }

    void registerTransactionSuccessHandler(EventBus eventBus) {
        eventBus.consumer("txSuccess", msg -> {
            JsonObject tx = (JsonObject) msg.body();
            long finishedAt = System.currentTimeMillis();
            ObservableSubscriber<UpdateResult> subscriber = new ObservableSubscriber<UpdateResult>();
            mTxCollection
                    .updateOne(new Document().append("_id", new ObjectId(tx.getString("_id"))),
                            new Document()
                                    .append("$set",
                                            new Document().append("running", false).append("success", true)
                                                    .append("finishedAt", finishedAt)))
                    .subscribe(subscriber);
            try {
                subscriber.await();
            } catch (Throwable e) {
                throw new RuntimeException(e.getMessage());
            }
            tx.put("running", false).put("success", true).put("finishAt", finishedAt);
            logger.debug("txSuccess - " + tx);
        });
    }

    void registerTransactionProceedHandler(EventBus eventBus){
        eventBus.consumer("txProceed", msg -> {
            JsonObject tx = (JsonObject) msg.body();
            long finishedAt = System.currentTimeMillis();
            ObservableSubscriber<UpdateResult> subscriber = new ObservableSubscriber<UpdateResult>();
            mTxCollection.updateOne(new Document().append("_id", new ObjectId(tx.getString("_id"))),
                            new Document().append("$set",
                                    new Document().append("running", false).append("success", false)
                                            .append("errorType", tx.getString("errorType"))
                                            .append("errorMessage", tx.getString("errorMessage"))
                                            .append("finishedAt", finishedAt)))
                    .subscribe(subscriber);
            try {
                subscriber.await();
            } catch (Throwable e) {
                throw new RuntimeException(e.getMessage());
            }
            tx.put("running", false).put("success", false)
                    .put("errorType", tx.getString("errorType"))
                    .put("errorMessage", tx.getString("errorMessage"))
                    .put("finishedAt", finishedAt);
            logger.debug("txProceed - " + tx);
        });
    }

    void registerTransactionRollBackHandler(EventBus eventBus){
        eventBus.consumer("txRollback", msg -> {
            JsonObject tx = (JsonObject) msg.body();
            ObservableSubscriber<UpdateResult> subscriber = new ObservableSubscriber<UpdateResult>();
            mTxCollection
                    .updateOne(new Document().append("_id", new ObjectId(tx.getString("_id"))),
                            new Document().append("$set",
                                    new Document().append("success", false)
                                            .append("errorType", tx.getString("errorType"))
                                            .append("errorMessage", tx.getString("errorMessage"))))
                    .subscribe(subscriber);
            try {
                subscriber.await();
            } catch (Throwable e) {
                throw new RuntimeException(e.getMessage());
            }
            tx.put("success", false)
                    .put("errorType", tx.getString("errorType"))
                    .put("errorMessage", tx.getString("errorMessage"));
            logger.debug(tx + " rollback starts");
            ObservableSubscriber<DeleteResult> subscriber2 = new ObservableSubscriber<DeleteResult>();
            mEventCollection.deleteMany(new Document().append("_tx", new ObjectId(tx.getString("_id"))))
                    .subscribe(subscriber2);
            try {
                subscriber2.await();
            } catch (Throwable e) {
                throw new RuntimeException(e.getMessage());
            }
            subscriber = new ObservableSubscriber<UpdateResult>();
            long finishedAt = System.currentTimeMillis();
            mTxCollection.updateOne(new Document().append("_id", new ObjectId(tx.getString("_id"))),
                            new Document().append("$set",
                                    new Document().append("running", false).append("finishedAt", finishedAt)))
                    .subscribe(subscriber);
            try {
                subscriber.await();
            } catch (Throwable e) {
                throw new RuntimeException(e.getMessage());
            }
            tx.put("finishedAt", finishedAt);
            logger.debug("txRollback - " + tx);
        });
    }
}
