package org.oliot.epcis.query;

import java.net.URI;

import org.oliot.epcis.model.exception.ImplementationException;
import org.oliot.epcis.model.exception.InvalidURIException;
import org.oliot.epcis.model.exception.QueryParameterException;
import org.oliot.epcis.util.BSONReadUtil;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import io.vertx.core.MultiMap;

/**
 * Copyright (C) 2020-2021. (Jaewook Byun) all rights reserved.
 * <p>
 * Oliot EPCIS X is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * Among various modules, epcis-query-soap acts as a server to receive queries
 * to provide filtered, sorted, limited events or masterdata of interest inside EPCIS
 * repository.
 * <p>
 * 
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr
 *         <p>
 *         Associate Director, Auto-ID Labs, KAIST, bjw0829@kaist.ac.kr
 */
public class Subscription {
	private String subscriptionID;
	private URI dest;
	private String schedule;
	private URI trigger;
	private Long initialRecordTime;
	private Boolean reportIfEmpty;
	private QueryDescription queryDescription;

	public Subscription(Document doc) throws QueryParameterException, InvalidURIException, ImplementationException {

		/*
		 * <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
		 * xmlns:query="urn:epcglobal:epcis-query:xsd:1"> <soapenv:Header/>
		 * <soapenv:Body> <query:Subscribe> // <queryName>SimpleEventQuery</queryName>
		 * // <params></params>
		 * <dest>http://localhost:8080/epcis/SubscriptionTestServlet</dest> <controls>
		 * <schedule> <second>0/10</second>xx </schedule>
		 * <initialRecordTime>2008-03-16T00:00:00+01:00</initialRecordTime>
		 * <reportIfEmpty>false</reportIfEmpty> </controls>
		 * <subscriptionID>2</subscriptionID> </query:Subscribe> </soapenv:Body>
		 * </soapenv:Envelope>
		 */
		subscriptionID = getSingleTextContent(doc, "subscriptionID");
		dest = getSingleURIContent(doc, "dest");
		initialRecordTime = getSingleDateContent(doc);
		reportIfEmpty = getSingleBooleanContent(doc);
		schedule = getSchedule(doc);
		trigger = getSingleURIContent(doc, "trigger");

		queryDescription = new QueryDescription(doc);
	}

	public Subscription(MultiMap params) throws QueryParameterException, InvalidURIException, ImplementationException {

		initialRecordTime = getSingleDateContent(params.get("initialRecordTime"));
		reportIfEmpty = getSingleBooleanContent(params.get("reportIfEmpty"));
		schedule = getSchedule(params);
		queryDescription = new QueryDescription(params);
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

		} catch (Exception e) {
			e.printStackTrace();
		}
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

	private String getSingleTextContent(Document doc, String name) {
		try {
			NodeList nodeList = doc.getElementsByTagName(name);
			if (nodeList.getLength() == 1) {
				return nodeList.item(0).getTextContent();
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	private URI getSingleURIContent(Document doc, String name) throws InvalidURIException {
		try {
			NodeList nodeList = doc.getElementsByTagName(name);
			if (nodeList.getLength() == 1) {
				return new URI(nodeList.item(0).getTextContent());
			} else {
				return null;
			}
		} catch (Exception e) {
			InvalidURIException e1 = new InvalidURIException();
			e1.setStackTrace(new StackTraceElement[0]);
			e1.setReason(
					"The URI specified for a subscriber cannot be parsed, does not name a scheme recognised by the implementation, or violates rules imposed by a particular scheme.");
			throw e1;
		}
	}

	private Long getSingleDateContent(Document doc) {
		try {
			NodeList nodeList = doc.getElementsByTagName("initialRecordTime");
			if (nodeList.getLength() == 1) {
				String str = nodeList.item(0).getTextContent();
				return BSONReadUtil.getBsonDateTime(str);
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	private Long getSingleDateContent(String str) {
		try {
			return BSONReadUtil.getBsonDateTime(str);
		} catch (Exception e) {
			return null;
		}
	}

	private Boolean getSingleBooleanContent(Document doc) {
		try {
			NodeList nodeList = doc.getElementsByTagName("reportIfEmpty");
			if (nodeList.getLength() == 1) {
				String str = nodeList.item(0).getTextContent();
				return Boolean.parseBoolean(str);
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	private Boolean getSingleBooleanContent(String str) {
		try {
			return Boolean.parseBoolean(str);
		} catch (Exception e) {
			return null;
		}
	}

	private String getSchedule(Document doc) {
		NodeList sList = doc.getElementsByTagName("schedule");
		if (sList.getLength() == 0) {
			return null;
		}

		String sec = getSingleTextContent(doc, "second");
		String min = getSingleTextContent(doc, "minute");
		String hour = getSingleTextContent(doc, "hour");
		String dayOfMonth = getSingleTextContent(doc, "dayOfMonth");
		String month = getSingleTextContent(doc, "month");
		String dayOfWeek = getSingleTextContent(doc, "dayOfWeek");

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
	
	private String getSchedule(MultiMap map) {
		
		String sec = map.get("second");
		String min = map.get("minute");
		String hour = map.get("hour");
		String dayOfMonth = map.get("dayOfMonth");
		String month = map.get("month");
		String dayOfWeek = map.get("dayOfWeek");

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
