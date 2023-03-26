package org.oliot.epcis.capture.xml;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.oliot.epcis.model.EPCISCaptureDocumentErrorListType;
import org.oliot.epcis.model.EPCISCaptureErrorBehaviourType;
import org.oliot.epcis.model.EPCISCaptureJobType;
import org.oliot.epcis.model.RFC7807ProblemResponseBodyType;
import org.oliot.epcis.util.BSONReadUtil;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Copyright (C) 2020-2023. (Jaewook Byun) all rights reserved.
 * <p>
 * This project is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * Transaction is an abstraction of capture jobs.
 * <p>
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr, Associate Director, Auto-ID Labs, Korea,
 *         bjw0829@gmail.com
 */
public class Transaction {
	private String txId;
	private boolean isRollback;
	private boolean running;
	private boolean success;
	private long createdAt;
	private String errorType;
	private String errorMessage;

	public Transaction(String gs1CaptureErrorBehaviour) {
		txId = new ObjectId().toHexString();
		if (gs1CaptureErrorBehaviour == null) {
			isRollback = true;
		} else if (gs1CaptureErrorBehaviour.equals("proceed"))
			isRollback = false;
		else
			isRollback = true;
		running = true;
		success = true;
		createdAt = System.currentTimeMillis();
	}

	public Transaction(ObjectId txId, String gs1CaptureErrorBehaviour) {
		this.txId = txId.toHexString();
		if (gs1CaptureErrorBehaviour == null) {
			isRollback = true;
		} else if (gs1CaptureErrorBehaviour.equals("proceed"))
			isRollback = false;
		else
			isRollback = true;
		running = true;
		success = true;
		createdAt = System.currentTimeMillis();
	}

	public String getTxId() {
		return txId;
	}

	public void setTxId(String txId) {
		this.txId = txId;
	}

	public boolean isRollback() {
		return isRollback;
	}

	public void setRollback(boolean rollback) {
		isRollback = rollback;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public JsonObject getJson() {
		return new JsonObject().put("_id", txId).put("isRollback", isRollback).put("running", running)
				.put("success", success).put("createdAt", createdAt).put("errorType", errorType)
				.put("errorMessage", errorMessage);
	}

	public static Document toDocument(JsonObject tx) {
		return new Document().append("_id", new ObjectId(tx.getString("_id")))
				.append("isRollback", tx.getBoolean("isRollback")).append("running", tx.getBoolean("running"))
				.append("success", tx.getBoolean("success")).append("createdAt", tx.getLong("createdAt"))
				.append("errorType", tx.getString("errorType")).append("errorMessage", tx.getString("errorMessage"));
	}

	public static JsonObject toCaptureJobReport(JsonObject obj) {

		JsonObject captureJobReport = new JsonObject();
		captureJobReport.put("captureID", obj.getString("_id"));
		if (obj.containsKey("createdAt"))
			captureJobReport.put("createdAt", BSONReadUtil.getDate(obj.getLong("createdAt")));
		if (obj.containsKey("finishedAt"))
			captureJobReport.put("finishedAt", BSONReadUtil.getDate(obj.getLong("finishedAt")));
		if (obj.containsKey("running"))
			captureJobReport.put("running", obj.getBoolean("running"));
		if (obj.containsKey("success"))
			captureJobReport.put("success", obj.getBoolean("success"));
		if (obj.containsKey("isRollback") && obj.getBoolean("isRollback"))
			captureJobReport.put("captureErrorBehaviour", "rollback");
		else
			captureJobReport.put("captureErrorBehaviour", "proceed");
		if (obj.containsKey("errorType") && obj.getString("errorType") != null) {
			JsonObject err = new JsonObject();
			err.put("type", obj.getString("errorType"));
			err.put("title", obj.getString("errorMessage"));
			JsonArray errArr = new JsonArray();
			errArr.add(err);
			captureJobReport.put("errors", errArr);
		} else {
			captureJobReport.put("errors", new JsonArray());
		}
		return captureJobReport;
	}

	/**
	 * Used in XML Capture should use ObjectId
	 * 
	 * @param jobDoc
	 * @return
	 * @throws DatatypeConfigurationException
	 */
	public static EPCISCaptureJobType toCaptureJob(Document jobDoc) throws DatatypeConfigurationException {
		EPCISCaptureJobType captureJob = new EPCISCaptureJobType();
		XMLGregorianCalendar createdAt = getGregorianCalendar(jobDoc.getLong("createdAt"));
		captureJob.setCreatedAt(createdAt);
		captureJob.setRunning(jobDoc.getBoolean("running"));
		captureJob.setSuccess(jobDoc.getBoolean("success"));
		captureJob.setCaptureID(jobDoc.getObjectId("_id").toHexString());
		if (jobDoc.getBoolean("isRollback")) {
			captureJob.setCaptureErrorBehaviour(EPCISCaptureErrorBehaviourType.ROLLBACK);
		} else {
			captureJob.setCaptureErrorBehaviour(EPCISCaptureErrorBehaviourType.PROCEED);
		}
		if (jobDoc.containsKey("errorType")) {
			RFC7807ProblemResponseBodyType error = new RFC7807ProblemResponseBodyType();
			error.setType(jobDoc.getString("errorType"));
			error.setTitle(jobDoc.getString("errorMessage"));
			EPCISCaptureDocumentErrorListType errors = new EPCISCaptureDocumentErrorListType(error);
			captureJob.setErrors(errors);
		}
		if (jobDoc.containsKey("finishedAt")) {
			XMLGregorianCalendar finishedAt = getGregorianCalendar(jobDoc.getLong("finishedAt"));
			captureJob.setFinishedAt(finishedAt);
		}
		return captureJob;
	}

	@Override
	public String toString() {
		return "Transaction{" + "txId=" + txId + ", isRollback=" + isRollback + ", running=" + running + ", success="
				+ success + ", createdAt=" + createdAt + ", errorType='" + errorType + '\'' + ", errorMessage='"
				+ errorMessage + '\'' + '}';
	}

	public static XMLGregorianCalendar getGregorianCalendar(Long time) throws DatatypeConfigurationException {
		if (time == null)
			return null;
		GregorianCalendar eventCalendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
		eventCalendar.setTimeInMillis(time);
		XMLGregorianCalendar xmlEventTime;
		xmlEventTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(eventCalendar);
		return xmlEventTime;
	}
}
