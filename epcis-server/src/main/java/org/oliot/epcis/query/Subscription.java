package org.oliot.epcis.query;

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.datatype.XMLGregorianCalendar;

import org.bson.Document;
import org.oliot.epcis.model.ImplementationException;
import org.oliot.epcis.model.ImplementationExceptionSeverity;
import org.oliot.epcis.model.InvalidURIException;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.model.QuerySchedule;
import org.oliot.epcis.model.Subscribe;
import org.oliot.epcis.model.SubscribeNotPermittedException;
import org.oliot.epcis.model.SubscriptionControls;
import org.oliot.epcis.model.SubscriptionControlsException;

import io.vertx.core.json.JsonObject;

/**
 * Copyright (C) 2020-2023. (Jaewook Byun) all rights reserved.
 * <p>
 * This project is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * Subscription abstracts each subscription
 * <p>
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr, Associate Director, Auto-ID Labs, Korea,
 *         bjw0829@gmail.com
 */
public class Subscription {
	private String subscriptionID;
	private URI dest;
	private String schedule;
	private URI trigger;
	private Long initialRecordTime;
	private Boolean reportIfEmpty;
	private QueryDescription queryDescription;
	private TriggerDescription triggerDescription;

	@Override
		public String toString() {
			if(schedule != null) {
				JsonObject obj = new JsonObject();
				obj.put("subscriptionID", subscriptionID);
				obj.put("dest", dest.toString());
				obj.put("schedule", schedule);
				obj.put("initialRecordTime", initialRecordTime);
				obj.put("reportIfEmpty", reportIfEmpty);
				return obj.toString();
			}else {
				return null;
			}			
		}
	
	public Subscription(Subscribe sub, SOAPQueryUnmarshaller unmarshaller)
			throws InvalidURIException, SubscriptionControlsException, ImplementationException, QueryParameterException,
			SubscribeNotPermittedException {
		subscriptionID = sub.getSubscriptionID();
		try {
			dest = new URI(sub.getDest());
		} catch (URISyntaxException e) {
			InvalidURIException e1 = new InvalidURIException("Invalid URI: " + e.getMessage());
			throw e1;
		}
		reportIfEmpty = sub.getControls().isReportIfEmpty();

		SubscriptionControls controls = sub.getControls();
		XMLGregorianCalendar xmlInitialRecordTime = controls.getInitialRecordTime();
		if (xmlInitialRecordTime != null) {
			initialRecordTime = xmlInitialRecordTime.toGregorianCalendar().getTimeInMillis();
		}

		schedule = getSchedule(controls.getSchedule());
		if (controls.getTrigger() != null) {
			try {
				trigger = new URI(controls.getTrigger());
			} catch (URISyntaxException e) {
				InvalidURIException e1 = new InvalidURIException("Invalid URI: " + e.getMessage());
				throw e1;
			}
		}

		if ((schedule == null && trigger == null) || (schedule != null && trigger != null)) {
			SubscriptionControlsException se = new SubscriptionControlsException(
					"The specified subscription controls was invalid; e.g., the schedule parameters were out of range, the trigger URI could not be parsed or did not name a recognised trigger, etc.");
			throw se;
		}

		if (schedule != null) {
			try {
				queryDescription = new QueryDescription(sub, unmarshaller);
			} catch (ImplementationException e2) {
				throw new ImplementationException(ImplementationExceptionSeverity.ERROR, null, null, e2.getMessage());
			} catch (QueryParameterException | SubscribeNotPermittedException e) {
				throw e;
			}
		} else {
			triggerDescription = new TriggerDescription(sub, unmarshaller);
		}
	}
	
	

	public TriggerDescription getTriggerDescription() {
		return triggerDescription;
	}

	public void setTriggerDescription(TriggerDescription triggerDescription) {
		this.triggerDescription = triggerDescription;
	}

	public Subscription(org.bson.Document doc) {
		try {
			subscriptionID = doc.getString("_id");
			if (doc.getString("dest") != null)
				dest = new URI(doc.getString("dest"));
			initialRecordTime = doc.getLong("initialRecordTime");
			reportIfEmpty = doc.getBoolean("reportIfEmpty");
			schedule = doc.getString("schedule");
			if (doc.getString("trigger") != null)
				trigger = new URI(doc.getString("trigger"));

			queryDescription = new QueryDescription(doc);
			triggerDescription = new TriggerDescription(doc);
		} catch (Exception e) {
			e.printStackTrace();
		} catch (QueryParameterException e) {
			e.printStackTrace();
		}
	}

	public Document toMongoDocument() {
		org.bson.Document doc = new org.bson.Document();
		doc.put("_id", subscriptionID);
		doc.put("dest", dest.toString());

		if (initialRecordTime != null)
			doc.put("initialRecordTime", initialRecordTime);
		doc.put("reportIfEmpty", reportIfEmpty);
		if (schedule != null) {
			doc.put("schedule", schedule);
			doc.put("eventCountLimit", queryDescription.getEventCountLimit());
			doc.put("maxCount", queryDescription.getMaxCount());
			doc.put("projection", queryDescription.getMongoProjection());
			doc.put("query", queryDescription.getMongoQuery());
			doc.put("sort", queryDescription.getMongoSort());
			doc.put("queryName", queryDescription.getQueryName());
		}

		if (trigger != null) {
			doc.put("trigger", trigger.toString());
			doc.put("query", triggerDescription.getMongoQueryParameter());
		}		
		return doc;
	}

	public String getSubscriptionID() {
		return subscriptionID;
	}

	public void setSubscriptionID(String subscriptionID) {
		this.subscriptionID = subscriptionID;
	}

	public URI getDest() {
		return dest;
	}

	public void setDest(URI dest) {
		this.dest = dest;
	}

	public String getSchedule() {
		return schedule;
	}

	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}

	public URI getTrigger() {
		return trigger;
	}

	public void setTrigger(URI trigger) {
		this.trigger = trigger;
	}

	public Long getInitialRecordTime() {
		return initialRecordTime;
	}

	public void setInitialRecordTime(Long initialRecordTime) {
		this.initialRecordTime = initialRecordTime;
	}

	public Boolean getReportIfEmpty() {
		return reportIfEmpty;
	}

	public void setReportIfEmpty(Boolean reportIfEmpty) {
		this.reportIfEmpty = reportIfEmpty;
	}

	public QueryDescription getQueryDescription() {
		return queryDescription;
	}

	public void setQueryDescription(QueryDescription queryDescription) {
		this.queryDescription = queryDescription;
	}

	private String getSchedule(QuerySchedule schedule) {

		if (schedule == null)
			return null;

		String sec = schedule.getSecond();
		String min = schedule.getMinute();
		String hour = schedule.getHour();
		String dayOfMonth = schedule.getDayOfMonth();
		String month = schedule.getMonth();
		String dayOfWeek = schedule.getDayOfWeek();

		if (sec == null)
			sec = "*";
		if (min == null)
			min = "*";
		if (hour == null)
			hour = "*";
		if (dayOfMonth == null)
			dayOfMonth = "*";

		// either month or dayOfWeek should be ?
		// two are not null -> dayOfWeek = ?
		// one of two exists -> non-exist = ?
		// two are null -> month=* , dayOfWeek=?

		if (month == null && dayOfWeek == null) {
			month = "*";
			dayOfWeek = "?";
		} else if (month != null && dayOfWeek == null) {
			dayOfWeek = "?";
		} else if (month == null && dayOfWeek != null) {
			month = "?";
		} else {
			dayOfWeek = "?";
		}
		return sec + " " + min + " " + hour + " " + dayOfMonth + " " + month + " " + dayOfWeek;
	}
}
