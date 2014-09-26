package org.oliot.model.epcis;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "RESTSubscription")
public class RESTSubscriptionType {
	private String subscriptionID;
	private String destURL;
	private String cronExpression;
	private String target;
	private String targetType;

	public RESTSubscriptionType(String subscriptionID, String destURL,
			String cronExpression, String target, String targetType) {
		this.subscriptionID = subscriptionID;
		this.destURL = destURL;
		this.cronExpression = cronExpression;
		this.target = target;
		this.targetType = targetType;
	}

	public String getSubscriptionID() {
		return subscriptionID;
	}

	public void setSubscriptionID(String subscriptionID) {
		this.subscriptionID = subscriptionID;
	}

	public String getDestURL() {
		return destURL;
	}

	public void setDestURL(String destURL) {
		this.destURL = destURL;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getTargetType() {
		return targetType;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

}
