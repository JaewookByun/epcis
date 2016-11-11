package org.oliot.model.oliot;



import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.springframework.data.mongodb.core.mapping.Document;
@Entity
//@Document(collection = "Subscription")
public class Subscription {
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;
	
	private String subscriptionID;
	//@Transient
	private String dest;
	//@Transient
	private String schedule;
	//@Transient
	private String triggerSub;
	//@Transient
	private String initialRecordTime;
	//@Transient
	private Boolean reportIfEmpty;
	@OneToOne
    @JoinColumn(name="pollParametrs_id")
	private PollParameters pollParametrs;
	
	
	public Subscription() {
		super();
	}
	
	public Subscription(String subscriptionID, String dest, String schedule,
			String triggerSub, String initialRecordTime,boolean reportIfEmpty, PollParameters pollParametrs) {
		this.subscriptionID=subscriptionID;
		this.dest=dest;
		this.schedule=schedule;
		this.triggerSub=triggerSub;
		this.initialRecordTime=initialRecordTime;
		this.reportIfEmpty=reportIfEmpty;
		this.pollParametrs=pollParametrs;
		
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

	public String getTriggerSub() {
		return triggerSub;
	}

	public void setTriggerSub(String triggerSub) {
		this.triggerSub = triggerSub;
	}

	public String getInitialRecordTime() {
		return initialRecordTime;
	}

	public void setInitialRecordTime(String initialRecordTime) {
		this.initialRecordTime = initialRecordTime;
	}

	public boolean getReportIfEmpty() {
		return reportIfEmpty;
	}

	public void setReportIfEmpty(boolean reportIfEmpty) {
		this.reportIfEmpty = reportIfEmpty;
	}

	public PollParameters getPollParametrs() {
		return pollParametrs;
	}

	public void setPollParametrs(PollParameters pollParametrs) {
		this.pollParametrs = pollParametrs;
	}
	

}
