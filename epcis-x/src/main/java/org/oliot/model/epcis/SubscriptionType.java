package org.oliot.model.epcis;

import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonString;

public class SubscriptionType {

	private String subscriptionID;
	private String dest;
	private String schedule;
	private String trigger;
	private String initialRecordTime;
	private Boolean reportIfEmpty;
	private PollParameters pollParameters;

	public SubscriptionType(BsonDocument doc) {

		if (doc.containsKey("subscriptionID")) {
			this.subscriptionID = doc.getString("subscriptionID").getValue();
		}
		if (doc.containsKey("dest")) {
			this.dest = doc.getString("dest").getValue();
		}
		if (doc.containsKey("schedule")) {
			this.schedule = doc.getString("schedule").getValue();
		}
		if (doc.containsKey("trigger")) {
			this.trigger = doc.getString("trigger").getValue();
		}
		if (doc.containsKey("initialRecordTime")) {
			this.initialRecordTime = doc.getString("initialRecordTime").getValue();
		}
		if (doc.containsKey("reportIfEmpty")) {
			this.reportIfEmpty = doc.getBoolean("reportIfEmpty").getValue();
		}
		if (doc.containsKey("pollParameters")) {
			this.pollParameters = new PollParameters(doc.getDocument("pollParameters"));
		}
	}

	public static BsonDocument asBsonDocument(SubscriptionType subscription) {

		BsonDocument bson = new BsonDocument();

		if (subscription.getSubscriptionID() != null) {
			bson.put("subscriptionID", new BsonString(subscription.getSubscriptionID()));
		}
		if (subscription.getDest() != null) {
			bson.put("dest", new BsonString(subscription.getDest()));
		}
		if (subscription.getSchedule() != null) {
			bson.put("schedule", new BsonString(subscription.getSchedule()));
		}
		if (subscription.getTrigger() != null) {
			bson.put("trigger", new BsonString(subscription.getTrigger()));
		}
		if (subscription.getInitialRecordTime() != null) {
			bson.put("initialRecordTime", new BsonString(subscription.getInitialRecordTime()));
		}
		bson.put("reportIfEmpty", new BsonBoolean(subscription.getReportIfEmpty()));
		bson.put("pollParameters", PollParameters.asBsonDocument(subscription.getPollParameters()));
		return bson;
	}

	public SubscriptionType(String subscriptionID, String dest, String schedule, String trigger,
			String initialRecordTime, Boolean reportIfEmpty, PollParameters pollParameters) {
		this.subscriptionID = subscriptionID;
		this.dest = dest;
		this.schedule = schedule;
		this.trigger = trigger;
		this.initialRecordTime = initialRecordTime;
		this.reportIfEmpty = reportIfEmpty;
		this.pollParameters = pollParameters;
	}

	public Boolean getReportIfEmpty() {
		return reportIfEmpty;
	}

	public void setReportIfEmpty(Boolean reportIfEmpty) {
		this.reportIfEmpty = reportIfEmpty;
	}

	public String getSubscriptionID() {
		return subscriptionID;
	}

	public void setSubscriptionID(String subscriptionID) {
		this.subscriptionID = subscriptionID;
	}

	public String getDest() {
		return dest;
	}

	public void setDest(String dest) {
		this.dest = dest;
	}

	public String getSchedule() {
		return schedule;
	}

	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}

	public String getTrigger() {
		return trigger;
	}

	public void setTrigger(String trigger) {
		this.trigger = trigger;
	}

	public String getInitialRecordTime() {
		return initialRecordTime;
	}

	public void setInitialRecordTime(String initialRecordTime) {
		this.initialRecordTime = initialRecordTime;
	}

	public PollParameters getPollParameters() {
		return pollParameters;
	}

	public void setPollParameters(PollParameters pollParameters) {
		this.pollParameters = pollParameters;
	}
}
