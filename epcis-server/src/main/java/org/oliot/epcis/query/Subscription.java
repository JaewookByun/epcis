package org.oliot.epcis.query;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.bson.Document;
import org.oliot.epcis.model.ArrayOfString;
import org.oliot.epcis.model.ImplementationException;
import org.oliot.epcis.model.ImplementationExceptionSeverity;
import org.oliot.epcis.model.InvalidURIException;
import org.oliot.epcis.model.QueryParam;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.model.QueryParams;
import org.oliot.epcis.model.QuerySchedule;
import org.oliot.epcis.model.Subscribe;
import org.oliot.epcis.model.SubscribeNotPermittedException;
import org.oliot.epcis.model.SubscriptionControls;
import org.oliot.epcis.model.SubscriptionControlsException;
import org.oliot.epcis.model.ValidationException;
import org.oliot.epcis.model.VoidHolder;
import org.oliot.epcis.util.BSONReadUtil;
import org.oliot.epcis.util.TimeUtil;
import org.oliot.epcis.util.XMLUtil;

import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import jakarta.xml.bind.JAXBException;

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
	private String trigger;
	private Long initialRecordTime;
	private Boolean reportIfEmpty;
	private QueryDescription queryDescription;
	private TriggerDescription triggerDescription;
	private ServerWebSocket serverWebSocket;

	// for json
	private String signatureToken;
	private Long createdAt;
	private Long lastNotifiedAt;
	private Long minRecordTime;
	private String namedQuery;

	public String getNamedQuery() {
		return namedQuery;
	}

	public void setNamedQuery(String namedQuery) {
		this.namedQuery = namedQuery;
	}

	private String resultFormat;

	public String getSignatureToken() {
		return signatureToken;
	}

	public void setSignatureToken(String signatureToken) {
		this.signatureToken = signatureToken;
	}

	public Long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Long createdAt) {
		this.createdAt = createdAt;
	}

	public Long getLastNotifiedAt() {
		return lastNotifiedAt;
	}

	public void setLastNotifiedAt(Long lastNotifiedAt) {
		this.lastNotifiedAt = lastNotifiedAt;
	}

	public Long getMinRecordTime() {
		return minRecordTime;
	}

	public void setMinRecordTime(Long minRecordTime) {
		this.minRecordTime = minRecordTime;
	}

	public JsonObject toJSONResponse() {
		JsonObject result = new JsonObject();
		result.put("dest", dest.toString());
		result.put("subscriptionID", subscriptionID);
		if (schedule != null)
			result.put("schedule", getSchedule(schedule));
		if (trigger != null)
			result.put("stream", true);
		if (initialRecordTime != null) {
			result.put("initialRecordTime", TimeUtil.getDateTimeStamp(initialRecordTime));
		}
		if (createdAt != null) {
			result.put("createdAt", TimeUtil.getDateTimeStamp(createdAt));
		}
		if (lastNotifiedAt != null) {
			result.put("lastNotifiedAt", TimeUtil.getDateTimeStamp(lastNotifiedAt));
		}
		if (minRecordTime != null) {
			result.put("minRecordTime", TimeUtil.getDateTimeStamp(minRecordTime));
		}

		result.put("epcFormat", "Always_GS1_Digital_Link");
		return result;
	}

	public static JsonObject toJSONResponse(org.bson.Document doc) {
		JsonObject result = new JsonObject();
		result.put("dest", doc.getString("dest"));
		result.put("subscriptionID", doc.getString("_id"));

		String schedule = doc.getString("schedule");
		if (schedule != null)
			result.put("schedule", getSchedule(schedule));
		String trigger = doc.getString("trigger");
		if (trigger != null)
			result.put("stream", true);
		Long initialRecordTime = doc.getLong("initialRecordTime");
		if (initialRecordTime != null) {
			result.put("initialRecordTime", TimeUtil.getDateTimeStamp(initialRecordTime));
		}
		Long createdAt = doc.getLong("createdAt");
		if (createdAt != null) {
			result.put("createdAt", TimeUtil.getDateTimeStamp(createdAt));
		}
		Long lastNotifiedAt = doc.getLong("lastNotifiedAt");
		if (lastNotifiedAt != null) {
			result.put("lastNotifiedAt", TimeUtil.getDateTimeStamp(lastNotifiedAt));
		}
		Long minRecordTime = doc.getLong("minRecordTime");
		if (minRecordTime != null) {
			result.put("minRecordTime", TimeUtil.getDateTimeStamp(minRecordTime));
		}
		result.put("epcFormat", "Always_GS1_Digital_Link");
		return result;
	}

	public static String toJSONResponse(List<org.bson.Document> docs) {
		JsonArray results = new JsonArray();
		for (org.bson.Document doc : docs) {
			JsonObject result = new JsonObject();
			result.put("dest", doc.getString("dest"));
			result.put("subscriptionID", doc.getString("_id"));

			String schedule = doc.getString("schedule");
			if (schedule != null)
				result.put("schedule", getSchedule(schedule));
			String trigger = doc.getString("trigger");
			if (trigger != null)
				result.put("stream", true);
			Long initialRecordTime = doc.getLong("initialRecordTime");
			if (initialRecordTime != null) {
				result.put("initialRecordTime", TimeUtil.getDateTimeStamp(initialRecordTime));
			}
			Long createdAt = doc.getLong("createdAt");
			if (createdAt != null) {
				result.put("createdAt", TimeUtil.getDateTimeStamp(createdAt));
			}
			Long lastNotifiedAt = doc.getLong("lastNotifiedAt");
			if (lastNotifiedAt != null) {
				result.put("lastNotifiedAt", TimeUtil.getDateTimeStamp(lastNotifiedAt));
			}
			Long minRecordTime = doc.getLong("minRecordTime");
			if (minRecordTime != null) {
				result.put("minRecordTime", TimeUtil.getDateTimeStamp(minRecordTime));
			}
			result.put("epcFormat", "Always_GS1_Digital_Link");
			results.add(result);
		}
		return results.toString();
	}

	public static QueryParams getQueryParams(org.bson.Document doc) throws QueryParameterException {
		QueryParams queryParamList = new QueryParams();

		List<QueryParam> queryParams = new ArrayList<QueryParam>();
		for (String field : doc.keySet()) {
			Object value = doc.get(field);
			if (value == null) {
				queryParams.add(new QueryParam(field, new VoidHolder()));
			} else if (value instanceof ArrayList) {
				List<String> arrayOfString = new ArrayList<String>();
				ArrayList<?> arr = (ArrayList<?>) value;
				for (Object arrValue : arr) {
					arrayOfString.add(arrValue.toString());
				}
				ArrayOfString aos = new ArrayOfString();
				aos.setString(arrayOfString);
				queryParams.add(new QueryParam(field, aos));
			} else if (value instanceof String) {
				try {
					long t = TimeUtil.toUnixEpoch(value.toString());
					queryParams.add(new QueryParam(field, t));

				} catch (ParseException | NullPointerException e) {
					queryParams.add(new QueryParam(field, value.toString()));
				}
			} else if (value instanceof Boolean) {
				queryParams.add(new QueryParam(field, (Boolean) value));
			} else if (value instanceof Double) {
				queryParams.add(new QueryParam(field, (Double) value));
			} else if (value instanceof Integer) {
				queryParams.add(new QueryParam(field, (Integer) value));
			} else {
				throw new QueryParameterException(
						"value of REST Query Parameter should be one of JsonArray, String, Time, Boolean, Double, Integer");
			}
		}
		queryParamList.setParam(queryParams);

		return queryParamList;
	}

	public static String toXMLResponse(List<org.bson.Document> docs)
			throws ParserConfigurationException, JAXBException, TransformerException, QueryParameterException {
		String result = "<Subscribes>";
		for (org.bson.Document doc : docs) {
			Subscribe subscribe = new Subscribe();
			subscribe.setDest(doc.getString("dest"));
			subscribe.setSubscriptionID(doc.getString("_id"));

			SubscriptionControls controls = new SubscriptionControls();
			String schedule = doc.getString("schedule");
			if (schedule != null) {
				controls.setSchedule(getQuerySchedule(schedule));
			}

			String trigger = doc.getString("trigger");
			if (trigger != null) {
				controls.setTrigger(trigger);
			}

			Long initialRecordTime = doc.getLong("initialRecordTime");
			if (initialRecordTime != null) {
				controls.setInitialRecordTime(BSONReadUtil.getGregorianCalendar(initialRecordTime));
			}

			Boolean reportIfEmpty = doc.getBoolean("reportIfEmpty");
			if (reportIfEmpty != null) {
				controls.setReportIfEmpty(reportIfEmpty);
			}

			subscribe.setControls(controls);

			subscribe.setQueryName(doc.getString("queryName"));

			subscribe.setParams(getQueryParams(doc.get("rawQuery", org.bson.Document.class)));

			String subscribeString = XMLUtil.toString(subscribe, Subscribe.class);
			result += subscribeString;
		}
		result += "</Subscribes>";
		return result;
	}

	@Override
	public String toString() {
		if (schedule != null) {
			JsonObject obj = new JsonObject();
			obj.put("subscriptionID", subscriptionID);
			obj.put("dest", dest.toString());
			obj.put("schedule", schedule);
			obj.put("initialRecordTime", initialRecordTime);
			obj.put("reportIfEmpty", reportIfEmpty);
			return obj.toString();
		} else {
			return null;
		}
	}

	public ServerWebSocket getServerWebSocket() {
		return serverWebSocket;
	}

	public void setServerWebSocket(ServerWebSocket serverWebSocket) {
		this.serverWebSocket = serverWebSocket;
	}

	public String getResultFormat() {
		return resultFormat;
	}

	public void setResultFormat(String resultFormat) {
		this.resultFormat = resultFormat;
	}

	/**
	 * for JSON WebSocket
	 * 
	 * @throws Exception
	 * @throws ValidationException
	 * @throws ImplementationException
	 * @throws QueryParameterException
	 * @throws SubscribeNotPermittedException
	 * 
	 */
	public Subscription(String queryName, RoutingContext routingContext, org.bson.Document namedQuery,
			ServerWebSocket serverWebSocket) throws QueryParameterException, ImplementationException,
			ValidationException, Exception, SubscribeNotPermittedException {

		subscriptionID = queryName + "_" + UUID.randomUUID().toString();
		dest = null;

		String second = routingContext.queryParam("GS1-Query-Second").isEmpty() ? null
				: routingContext.queryParam("GS1-Query-Second").get(0);
		String minute = routingContext.queryParam("GS1-Query-Minute").isEmpty() ? null
				: routingContext.queryParam("GS1-Query-Minute").get(0);
		String hour = routingContext.queryParam("GS1-Query-Hour").isEmpty() ? null
				: routingContext.queryParam("GS1-Query-Hour").get(0);
		String dayOfMonth = routingContext.queryParam("GS1-Query-DayOfMonth").isEmpty() ? null
				: routingContext.queryParam("GS1-Query-DayOfMonth").get(0);
		String month = routingContext.queryParam("GS1-Query-Month").isEmpty() ? null
				: routingContext.queryParam("GS1-Query-Month").get(0);
		String dayOfWeek = routingContext.queryParam("GS1-Query-DayOfWeek").isEmpty() ? null
				: routingContext.queryParam("GS1-Query-DayOfWeek").get(0);

		try {
			reportIfEmpty = Boolean.parseBoolean(routingContext.queryParam("GS1-Query-ReportIfEmpty").get(0));
		} catch (Exception e) {
			reportIfEmpty = true;
		}

		try {

			initialRecordTime = TimeUtil
					.toUnixEpoch(routingContext.queryParam("GS1-Query-InitialRecordTime").isEmpty() ? null
							: routingContext.queryParam("GS1-Query-InitialRecordTime").get(0));
		} catch (Exception e) {

		}

		schedule = getSchedule(new QuerySchedule(second, minute, hour, dayOfMonth, month, dayOfWeek));

		trigger = routingContext.queryParam("GS1-Query-Stream").isEmpty() ? null
				: routingContext.queryParam("GS1-Query-Stream").get(0);

		if (schedule == null && trigger == null)
			throw new QueryParameterException("no schedule and stream in Websocket subscription request");
		queryDescription = new QueryDescription(new JsonObject(namedQuery.get("rawQuery", Document.class).toJson()),
				"SimpleEventQuery");
		this.serverWebSocket = serverWebSocket;

		triggerDescription = new TriggerDescription(this, SOAPQueryService.soapQueryUnmarshaller);

		createdAt = System.currentTimeMillis();

		resultFormat = "JSON";
	}

	/**
	 * for JSON Webhook
	 * 
	 * @throws Exception
	 * @throws ValidationException
	 * @throws ImplementationException
	 * @throws QueryParameterException
	 * @throws SubscribeNotPermittedException
	 * 
	 */
	public Subscription(String queryName, RoutingContext routingContext, org.bson.Document namedQuery,
			JsonObject subscriptionBase) throws QueryParameterException, ImplementationException, ValidationException,
			Exception, SubscribeNotPermittedException {

		/*
		 * { “dest”: // mandatory “signatureToken” : // mandatory “reportIfEmpty” :
		 * true, “initialRecordTime” : “schedule” : { “second” : “minute” : “hour” :
		 * “dayOfMonth” : “month” : “dayOfWeek“ : } }
		 * 
		 */

		subscriptionID = queryName + "_" + UUID.randomUUID().toString();
		this.namedQuery = queryName;

		try {
			dest = new URI(subscriptionBase.getString("dest"));
			if (dest == null) {
				throw new ValidationException("dest field is not given (mandatory)");
			}
			signatureToken = subscriptionBase.getString("signatureToken");
			if (signatureToken == null) {
				throw new ValidationException("signatureToken field is not given (mandatory)");
			}
		} catch (URISyntaxException e) {
			throw new ValidationException("Invalid dest field");
		}

		JsonObject schedule = subscriptionBase.getJsonObject("schedule");
		if (schedule != null) {
			String second = schedule.getString("second");
			String minute = schedule.getString("minute");
			String hour = schedule.getString("hour");
			String dayOfMonth = schedule.getString("dayOfMonth");
			String month = schedule.getString("month");
			String dayOfWeek = schedule.getString("dayOfWeek");
			this.schedule = getSchedule(new QuerySchedule(second, minute, hour, dayOfMonth, month, dayOfWeek));
		} else {
			Object str = subscriptionBase.getValue("stream");
			if (str == null || str.toString().equals("false")) {
				throw new ValidationException("either schedule or stream field is not given (mandatory)");
			}
			trigger = str.toString();
		}

		try {
			reportIfEmpty = subscriptionBase.getBoolean("reportIfEmpty");
		} catch (Exception e) {
			reportIfEmpty = true;
		}

		try {
			initialRecordTime = TimeUtil.toUnixEpoch(subscriptionBase.getString("initialRecordTime"));
		} catch (Exception e) {

		}

		queryDescription = new QueryDescription(new JsonObject(namedQuery.get("rawQuery", Document.class).toJson()),
				"SimpleEventQuery");

		triggerDescription = new TriggerDescription(this, SOAPQueryService.soapQueryUnmarshaller);

		createdAt = System.currentTimeMillis();

		resultFormat = "JSON";
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
			trigger = controls.getTrigger();
		}

		if ((schedule == null && trigger == null) || (schedule != null && trigger != null)) {
			SubscriptionControlsException se = new SubscriptionControlsException(
					"The specified subscription controls was invalid; e.g., the schedule parameters were out of range, the trigger URI could not be parsed or did not name a recognised trigger, etc.");
			throw se;
		}

		try {
			queryDescription = new QueryDescription(sub, unmarshaller);
		} catch (ImplementationException e2) {
			throw new ImplementationException(ImplementationExceptionSeverity.ERROR, null, null, e2.getMessage());
		} catch (QueryParameterException | SubscribeNotPermittedException e) {
			throw e;
		}

		triggerDescription = new TriggerDescription(sub, unmarshaller);

		createdAt = System.currentTimeMillis();
	}

	public Subscription(Subscribe sub, SOAPQueryUnmarshaller unmarshaller, Document namedQuery, String queryName,
			String signatureToken) throws InvalidURIException, SubscriptionControlsException, ImplementationException,
			QueryParameterException, SubscribeNotPermittedException {
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
			trigger = controls.getTrigger();
		}

		if ((schedule == null && trigger == null) || (schedule != null && trigger != null)) {
			SubscriptionControlsException se = new SubscriptionControlsException(
					"The specified subscription controls was invalid; e.g., the schedule parameters were out of range, the trigger URI could not be parsed or did not name a recognised trigger, etc.");
			throw se;
		}

		queryDescription = new QueryDescription(namedQuery);

		triggerDescription = new TriggerDescription(subscriptionID, namedQuery);

		createdAt = System.currentTimeMillis();

		this.namedQuery = queryName;
	}

	public Subscription(org.bson.Document doc) {
		try {
			subscriptionID = doc.getString("_id");
			if (doc.getString("dest") != null)
				dest = new URI(doc.getString("dest"));

			schedule = doc.getString("schedule");
			if (doc.getString("trigger") != null)
				trigger = doc.getString("trigger");

			initialRecordTime = doc.getLong("initialRecordTime");
			reportIfEmpty = doc.getBoolean("reportIfEmpty");

			queryDescription = new QueryDescription(doc);
			triggerDescription = new TriggerDescription(doc);

			signatureToken = doc.getString("signatureToken");
			createdAt = doc.getLong("createdAt");
			lastNotifiedAt = doc.getLong("lastNotifiedAt");
			minRecordTime = doc.getLong("minRecordTime");

			resultFormat = doc.getString("resultFormat");

			namedQuery = doc.getString("namedQuery");

		} catch (Exception e) {
			e.printStackTrace();
		} catch (QueryParameterException e) {
			e.printStackTrace();
		}
	}

	public TriggerDescription getTriggerDescription() {
		return triggerDescription;
	}

	public void setTriggerDescription(TriggerDescription triggerDescription) {
		this.triggerDescription = triggerDescription;
	}

	public Document toMongoDocument() {
		org.bson.Document doc = new org.bson.Document();
		doc.put("_id", subscriptionID);
		doc.put("dest", dest.toString());

		if (schedule != null) {
			doc.put("schedule", schedule);
		}

		if (trigger != null) {
			doc.put("trigger", trigger.toString());
		}

		doc.put("query", queryDescription.getMongoQuery());
		doc.put("eventCountLimit", queryDescription.getEventCountLimit());
		doc.put("maxCount", queryDescription.getMaxCount());
		doc.put("projection", queryDescription.getMongoProjection());
		doc.put("sort", queryDescription.getMongoSort());
		doc.put("queryName", queryDescription.getQueryName());
		doc.put("rawQuery", queryDescription.getRawQuery());

		doc.put("initialRecordTime", initialRecordTime);
		doc.put("reportIfEmpty", reportIfEmpty);
		doc.put("signatureToken", signatureToken);
		doc.put("createdAt", createdAt);
		doc.put("lastNotifiedAt", lastNotifiedAt);
		doc.put("minRecordTime", minRecordTime);
		doc.put("resultFormat", resultFormat);
		doc.put("namedQuery", namedQuery);

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

	public String getTrigger() {
		return trigger;
	}

	public void setTrigger(String trigger) {
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

		if (sec == null && min == null && hour == null && dayOfMonth == null && month == null && dayOfWeek == null) {
			return null;
		}

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

	private static JsonObject getSchedule(String schedule) {
		JsonObject scheduleObj = new JsonObject();
		String[] arr = schedule.split("\\s");
		if (arr.length == 6) {
			if (!arr[0].equals("*"))
				scheduleObj.put("second", arr[0]);
			if (!arr[1].equals("*"))
				scheduleObj.put("minute", arr[1]);
			if (!arr[2].equals("*"))
				scheduleObj.put("hour", arr[2]);
			if (!arr[3].equals("*"))
				scheduleObj.put("dayOfMonth", arr[3]);
			if (!arr[4].equals("*"))
				scheduleObj.put("month", arr[4]);
			if (!arr[5].equals("*"))
				scheduleObj.put("dayOfWeek", arr[5]);
			return scheduleObj;
		} else {
			// not happen
			return null;
		}
	}

	private static QuerySchedule getQuerySchedule(String schedule) {
		QuerySchedule querySchedule = new QuerySchedule();
		String[] arr = schedule.split("\\s");
		if (arr.length == 6) {
			if (!arr[0].equals("*"))
				querySchedule.setSecond(arr[0]);
			if (!arr[1].equals("*"))
				querySchedule.setMinute(arr[1]);
			if (!arr[2].equals("*"))
				querySchedule.setHour(arr[2]);
			if (!arr[3].equals("*"))
				querySchedule.setDayOfMonth(arr[3]);
			if (!arr[4].equals("*"))
				querySchedule.setMonth(arr[4]);
			if (!arr[5].equals("*"))
				querySchedule.setDayOfWeek(arr[5]);
			return querySchedule;
		} else {
			// not happen
			return null;
		}
	}
}
