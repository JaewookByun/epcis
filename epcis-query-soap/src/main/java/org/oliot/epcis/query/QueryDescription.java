package org.oliot.epcis.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import io.vertx.core.MultiMap;

import org.bson.Document;
import org.oliot.epcis.model.VoidHolder;
import org.oliot.epcis.model.exception.ImplementationException;
import org.oliot.epcis.model.exception.QueryParameterException;
import org.oliot.epcis.util.BSONReadUtil;
import org.oliot.epcis.util.ObservableSubscriber;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
public class QueryDescription {
	private String queryName;
	private Document mongoQuery;
	private Document mongoProjection;
	private Document mongoSort;
	private Integer eventCountLimit;
	private Integer maxCount;

	private HashSet<String> readPointCandidate;
	private HashSet<String> bizLocationCandidate;

	private HashSet<String> attributeProjection;
	private Boolean includeAttributes;
	private Boolean includeChildren;

	// for pagination
	private AtomicInteger perPage = new AtomicInteger(30);
	private AtomicInteger skip = new AtomicInteger(0);

	/**
	 * used in SimpleMasterDataQuery candidate will be in a filter query.
	 */
	private HashSet<String> elementNameCandidate;

	List<String> listTester = new ArrayList<String>();
	Integer intTester = 0;
	Double doubleTester = 0d;
	Boolean booleanTester = true;
	String stringTester = "";
	Long dateTester = 0l;
	VoidHolder voidTester = new VoidHolder();

	public AtomicInteger getPerPage() {
		return perPage;
	}

	public void setPerPage(AtomicInteger perPage) {
		this.perPage = perPage;
	}

	public AtomicInteger getSkip() {
		return skip;
	}

	public void setSkip(AtomicInteger skip) {
		this.skip = skip;
	}

	public String getQueryName() {
		return queryName;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}

	public Document getMongoQuery() {
		return mongoQuery;
	}

	public Document getMongoProjection() {
		return mongoProjection;
	}

	public Document getMongoSort() {
		return mongoSort;
	}

	public Integer getEventCountLimit() {
		return eventCountLimit;
	}

	public Integer getMaxCount() {
		return maxCount;
	}

	/**
	 * Create a query description by cloning
	 * 
	 * @param qd
	 */
	public QueryDescription(QueryDescription qd) {
		this.eventCountLimit = qd.getEventCountLimit();
		this.maxCount = qd.getMaxCount();
		this.mongoProjection = qd.getMongoProjection();
		this.mongoSort = qd.getMongoSort();
		this.mongoQuery = qd.getMongoQuery();
		this.queryName = qd.getQueryName();
	}

	/**
	 * Create a query description from MongoDB
	 * 
	 * @param doc
	 */
	public QueryDescription(Document doc) {
		eventCountLimit = doc.getInteger("eventCountLimit");
		maxCount = doc.getInteger("maxCount");
		mongoProjection = doc.get("projection", Document.class);
		mongoQuery = doc.get("query", Document.class);
		mongoSort = doc.get("sort", Document.class);
		queryName = doc.getString("queryName");
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/// Create a query description from SOAP service
	///////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Create a query description from SOAP service
	 * 
	 * @param doc
	 * @throws QueryParameterException
	 * @throws ImplementationException
	 */
	public QueryDescription(org.w3c.dom.Document doc) throws QueryParameterException, ImplementationException {
		queryName = doc.getElementsByTagName("queryName").item(0).getTextContent();
		HashMap<String, Object> map = getParameters(doc);
		if (queryName.equals("SimpleEventQuery"))
			makeSimpleEventQuery(map.entrySet().iterator());
		else if (queryName.equals("SimpleMasterDataQuery"))
			makeSimpleMasterDataQuery(map.entrySet().iterator());
	}

	private HashMap<String, Object> getParameters(org.w3c.dom.Document doc) throws QueryParameterException {
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		NodeList params = doc.getElementsByTagName("params").item(0).getChildNodes();
		for (int i = 0; i < params.getLength(); i++) {
			Node node = params.item(i);
			if (node.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			Element param = (Element) params.item(i);
			String name = param.getElementsByTagName("name").item(0).getTextContent();
			NodeList valueList = null;
			NodeList tList = param.getElementsByTagName("value");
			if (tList != null && tList.getLength() != 0)
				valueList = tList.item(0).getChildNodes();

			try {
				Element element = (Element) valueList;
				String type = element.getAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "type");

				if (type.contains("ArrayOfString")) {
					// { field: { $in: [<value1>, <value2>, ... <valueN> ] } }
					// valueList attributes = xsi:type="epcisq:ArrayOfString"
					List<String> values = new ArrayList<String>();
					for (int j = 0; j < valueList.getLength(); j++) {
						Node value = valueList.item(j);
						if (value.getNodeType() == Node.ELEMENT_NODE) {
							Element vElem = (Element) value;
							String v = vElem.getTextContent();
							if (vElem.getTagName().equals("string")) {
								values.add(v);
							}
						}
					}
					if (values.isEmpty()) {
						QueryParameterException e = new QueryParameterException();
						e.setStackTrace(new StackTraceElement[0]);
						e.setReason("the value of a parameter is of the wrong type or out of range: null");
						throw e;
					}
					paramMap.put(name, values);
				} else if (type.contains("integer")) {
					Integer intVal = Integer.parseInt(element.getTextContent());
					paramMap.put(name, intVal);
				} else if (type.contains("double")) {
					Double dVal = Double.parseDouble(element.getTextContent());
					paramMap.put(name, dVal);
				} else if (type.contains("dateTime")) {
					Long tVal = BSONReadUtil.getBsonDateTime(element.getTextContent());
					paramMap.put(name, tVal);
				} else if (type.contains("boolean")) {
					Boolean bVal = Boolean.parseBoolean(element.getTextContent());
					paramMap.put(name, bVal);
				} else if (type.contains("string")) {
					String sVal = element.getTextContent();
					paramMap.put(name, sVal);
				} else if (type.contains("VoidHolder")) {
					paramMap.put(name, new VoidHolder());
				} else {
					QueryParameterException e = new QueryParameterException();
					e.setStackTrace(new StackTraceElement[0]);
					e.setReason("the value of a parameter is of the wrong type or out of range");
					throw e;
				}
			} catch (Exception e1) {
				QueryParameterException e2 = new QueryParameterException();
				e2.setStackTrace(new StackTraceElement[0]);
				e2.setReason("the value of a parameter is of the wrong type or out of range");
				throw e2;
			}
		}
		return paramMap;
	}

	@SuppressWarnings("unchecked")
	private void makeSimpleEventQuery(Iterator<Map.Entry<String, Object>> paramIterator)
			throws QueryParameterException, ImplementationException {
		List<Document> mongoQueryElements = new ArrayList<Document>();
		mongoProjection = new Document();
		mongoSort = new Document();
		readPointCandidate = new HashSet<>();
		bizLocationCandidate = new HashSet<>();
		String orderBy = null;
		int orderDirection = -1;

		while (paramIterator.hasNext()) {
			Map.Entry<String, Object> entry = paramIterator.next();
			String name = entry.getKey();
			Object value = entry.getValue();

			/**
			 * orderBy : If specified, names a single field that will be used to order the
			 * results. The orderDirection field specifies whether the ordering is in
			 * ascending sequence or descending sequence. Events included in the result that
			 * lack the specified field altogether may occur in any position within the
			 * result event list. The value of this parameter SHALL be one of: eventTime,
			 * recordTime, or the fully qualified name of an extension field whose type is
			 * Int, Float, Time, or String. A fully qualified fieldname is constructed as
			 * for the EQ_fieldname parameter. In the case of a field of type String, the
			 * ordering SHOULD be in lexicographic order based on the Unicode encoding of
			 * the strings, or in some other collating sequence appropriate to the locale.
			 * If omitted, no order is specified. The implementation MAY order the results
			 * in any order it chooses, and that order MAY differ even when the same query
			 * is executed twice on the same data. (In EPCIS 1.0, the value quantity was
			 * also permitted, but its use is deprecated in EPCIS 1.1.)
			 *
			 * orderDirection : If specified and orderBy is also specified, specifies
			 * whether the results are ordered in ascending or descending sequence according
			 * to the key specified by orderBy. The value of this parameter must be one of
			 * ASC (for ascending order) or DESC (for descending order); if not, the
			 * implementation SHALL raise a QueryParameterException. If omitted, defaults to
			 * DESC.
			 */
			if (name.equals("orderBy")) {
				checkParameterValueType(value, stringTester);
				if (!value.equals("eventTime") && !value.equals("recordTime"))
					orderBy = "extension." + BSONReadUtil.encodeMongoObjectKey((String) value);
				else
					orderBy = (String) value;
				continue;
			}
			if (name.equals("orderDirection")) {
				checkParameterValueType(value, stringTester);
				if (value.equals("ASC"))
					orderDirection = 1;
				else if (value.equals("DESC"))
					orderDirection = -1;
				else {
					QueryParameterException e = new QueryParameterException();
					e.setStackTrace(new StackTraceElement[0]);
					e.setReason(
							"the value of a parameter is of the wrong type or out of range: value should be (ASC|DESC) if given");
					throw e;
				}
				continue;
			}
			/**
			 * eventCountLimit: If specified, the results will only include the first N
			 * events that match the other criteria, where N is the value of this parameter.
			 * The ordering specified by the orderBy and orderDirection parameters determine
			 * the meaning of “first” for this purpose. If omitted, all events matching the
			 * specified criteria will be included in the results. This parameter and
			 * maxEventCount are mutually exclusive; if both are specified, a
			 * QueryParameterException SHALL be raised. This parameter may only be used when
			 * orderBy is specified; if orderBy is omitted and eventCountLimit is specified,
			 * a QueryParameterException SHALL be raised. This parameter differs from
			 * maxEventCount in that this parameter limits the amount of data returned,
			 * whereas maxEventCount causes an exception to be thrown if the limit is
			 * exceeded.
			 *
			 * v1.2
			 *
			 * Int
			 */
			if (name.equals("eventCountLimit")) {
				checkParameterValueType(value, intTester);
				eventCountLimit = (Integer) value;
				continue;
			}

			/**
			 * maxEventCount: If specified, at most this many events will be included in the
			 * query result. If the query would otherwise return more than this number of
			 * events, a QueryTooLargeException SHALL be raised instead of a normal query
			 * result. This parameter and eventCountLimit are mutually exclusive; if both
			 * are specified, a QueryParameterException SHALL be raised. If this parameter
			 * is omitted, any number of events may be included in the query result. Note,
			 * however, that the EPCIS implementation is free to raise a
			 * QueryTooLargeException regardless of the setting of this parameter (see
			 * Section 8.2.3).
			 *
			 * v1.2
			 *
			 * Int
			 */

			if (name.equals("maxEventCount")) {
				checkParameterValueType(value, intTester);
				maxCount = (Integer) value;
				continue;
			}

			/**
			 * eventType : If specified, the result will only include events whose type
			 * matches one of the types specified in the parameter value. Each element of
			 * the parameter value may be one of the following strings: ObjectEvent,
			 * AggregationEvent, QuantityEvent, TransactionEvent, or TransformationEvent. An
			 * element of the parameter value may also be the name of an extension event
			 * type. If omitted, all event types will be considered for inclusion in the
			 * result.
			 *
			 * v1.2
			 *
			 * List of String
			 */
			if (name.equals("eventType")) {
				checkParameterValueType(value, listTester);
				putEQListOfStringQuery(mongoQueryElements, "type", (List<String>) value);
				continue;
			}

			/**
			 * GE_eventTime: If specified, only events with eventTime greater than or equal
			 * to the specified value will be included in the result. If omitted, events are
			 * included regardless of their eventTime (unless constrained by the LT_
			 * eventTime parameter). Example: 2014-08-11T19:57:59.717+09:00 SimpleDateFormat
			 * sdf = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
			 * eventTime.setTime(sdf.parse(timeString)); e.g. 1988-07-04T12:08:56.235-07:00
			 *
			 * v1.2
			 *
			 * Time
			 */
			if (name.equals("GE_eventTime")) {
				checkParameterValueType(value, dateTester);
				putDateQuery(mongoQueryElements, "eventTime", "$gte", (Long) value);
				continue;
			}

			/**
			 * LT_eventTime: If specified, only events with eventTime less than the
			 * specified value will be included in the result. If omitted, events are
			 * included regardless of their eventTime (unless constrained by the GE_
			 * eventTime parameter).
			 *
			 * v1.2
			 *
			 * Time
			 */
			if (name.equals("LT_eventTime")) {
				checkParameterValueType(value, dateTester);
				putDateQuery(mongoQueryElements, "eventTime", "$lt", (Long) value);
				continue;
			}

			/**
			 * GE_recordTime: If provided, only events with recordTime greater than or equal
			 * to the specified value will be returned. The automatic limitation based on
			 * event record time (Section 8.2.5.2) may implicitly provide a constraint
			 * similar to this parameter. If omitted, events are included regardless of
			 * their recordTime , other than automatic limitation based on event record time
			 * (Section 8.2.5.2).
			 *
			 * v1.2
			 *
			 * Time
			 */
			if (name.equals("GE_recordTime")) {
				checkParameterValueType(value, dateTester);
				putDateQuery(mongoQueryElements, "recordTime", "$gte", (Long) value);
				continue;
			}

			/**
			 * LT_recordTime: If provided, only events with recordTime less than the
			 * specified value will be returned. If omitted, events are included regardless
			 * of their recordTime (unless constrained by the GE_ recordTime parameter or
			 * the automatic limitation based on event record time).
			 *
			 * v1.2
			 *
			 * Time
			 */
			if (name.equals("LT_recordTime")) {
				checkParameterValueType(value, dateTester);
				putDateQuery(mongoQueryElements, "recordTime", "$lt", (Long) value);
				continue;
			}

			/**
			 * GE_errorDeclarationTime: If this parameter is specified, the result will only
			 * include events that (a) contain an ErrorDeclaration ; and where (b) the value
			 * of the errorDeclarationTime field is greater than or equal to the specified
			 * value. If this parameter is omitted, events are returned regardless of
			 * whether they contain an ErrorDeclaration or what the value of the
			 * errorDeclarationTime field is.
			 *
			 * v1.2
			 *
			 * Time *
			 */
			if (name.equals("GE_errorDeclarationTime")) {
				checkParameterValueType(value, dateTester);
				putDateQuery(mongoQueryElements, "errorDeclaration.declarationTime", "$gte", (Long) value);
				continue;
			}

			/**
			 * LT_errorDeclarationTime: contain an ErrorDeclaration ; and where (b) the
			 * value of the errorDeclarationTime field is less than to the specified value.
			 * If this parameter is omitted, events are returned regardless of whether they
			 * contain an ErrorDeclaration or what the value of the errorDeclarationTime
			 * field is.
			 *
			 * v1.2
			 *
			 * Time
			 */
			if (name.equals("LT_errorDeclarationTime")) {
				checkParameterValueType(value, dateTester);
				putDateQuery(mongoQueryElements, "errorDeclaration.declarationTime", "$lt", (Long) value);
				continue;
			}

			/**
			 *
			 * GE_time
			 *
			 * LT_time
			 *
			 * GE_startTime
			 *
			 * LT_startTime
			 *
			 * GE_endTime
			 *
			 * LT_endTime
			 *
			 */
			if (name.equals("GE_time")) {
				checkParameterValueType(value, dateTester);
				putDateQuery(mongoQueryElements, "sensorElementList.sensorMetadata.time", "$gte", (Long) value);
				continue;
			}
			if (name.equals("LT_time")) {
				checkParameterValueType(value, dateTester);
				putDateQuery(mongoQueryElements, "sensorElementList.sensorMetadata.time", "$lt", (Long) value);
				continue;
			}
			if (name.equals("GE_startTime")) {
				checkParameterValueType(value, dateTester);
				putDateQuery(mongoQueryElements, "sensorElementList.sensorMetadata.startTime", "$gte", (Long) value);
				continue;
			}
			if (name.equals("LT_startTime")) {
				checkParameterValueType(value, dateTester);
				putDateQuery(mongoQueryElements, "sensorElementList.sensorMetadata.startTime", "$lt", (Long) value);
				continue;
			}
			if (name.equals("GE_endTime")) {
				checkParameterValueType(value, dateTester);
				putDateQuery(mongoQueryElements, "sensorElementList.sensorMetadata.endTime", "$gte", (Long) value);
				continue;
			}
			if (name.equals("LT_endTime")) {
				checkParameterValueType(value, dateTester);
				putDateQuery(mongoQueryElements, "sensorElementList.sensorMetadata.endTime", "$lt", (Long) value);
				continue;
			}

			/**
			 * EQ_action: If specified, the result will only include events that (a) have an
			 * action field; and where (b) the value of the action field matches one of the
			 * specified values. The elements of the value of this parameter each must be
			 * one of the strings ADD , OBSERVE , or DELETE ; if not, the implementation
			 * SHALL raise a QueryParameterException . If omitted, events are included
			 * regardless of their action field.
			 *
			 * v1.2
			 *
			 * List of String
			 *
			 */
			if (name.equals("EQ_action")) {
				checkParameterValueType(value, listTester);
				putEQListOfStringQuery(mongoQueryElements, "action", (List<String>) value);
				continue;
			}

			/**
			 * EQ_bizStep: If specified, the result will only include events that (a) have a
			 * non-null bizStep field; and where (b) the value of the bizStep field matches
			 * one of the specified values. If this parameter is omitted, events are
			 * returned regardless of the value of the bizStep field or whether the bizStep
			 * field exists at all.
			 *
			 * v1.2
			 *
			 * List of String
			 */
			if (name.equals("EQ_bizStep")) {
				checkParameterValueType(value, listTester);
				putEQListOfStringQuery(mongoQueryElements, "bizStep", (List<String>) value);
				continue;
			}

			/**
			 * EQ_disposition: Like the EQ_bizStep parameter, but for the disposition field.
			 *
			 * v1.2
			 *
			 * List of String
			 */
			if (name.equals("EQ_disposition")) {
				checkParameterValueType(value, listTester);
				putEQListOfStringQuery(mongoQueryElements, "disposition", (List<String>) value);
				continue;
			}

			/**
			 * EQ_set or unsetPersistentDisposition: Like the EQ_bizStep parameter, but for
			 * the persistentDisposition set field.
			 *
			 * v2.0
			 *
			 * List of String
			 *
			 */
			if (name.equals("EQ_setPersistentDisposition")) {
				checkParameterValueType(value, listTester);
				putEQListOfStringQuery(mongoQueryElements, "persistentDisposition.set", (List<String>) value);
				continue;
			}
			if (name.equals("EQ_unsetPersistentDisposition")) {
				checkParameterValueType(value, listTester);
				putEQListOfStringQuery(mongoQueryElements, "persistentDisposition.unset", (List<String>) value);
				continue;
			}

			/**
			 * EQ_transformationID: If this parameter is specified, the result will only
			 * include events that (a) have a transformationID field (that is,
			 * TransformationEvents or extension event type that extend
			 * TransformationEvent); and where (b) the transformationID field is equal to
			 * one of the values specified in this parameter.
			 *
			 * v1.2
			 *
			 * List of String
			 *
			 */
			if (name.equals("EQ_transformationID")) {
				checkParameterValueType(value, listTester);
				putEQListOfStringQuery(mongoQueryElements, "transformationID", (List<String>) value);
				continue;
			}

			/**
			 *
			 * EQ_type
			 *
			 * EQ_deviceID
			 *
			 * EQ_deviceMetaData
			 *
			 * EQ_rawData
			 *
			 * EQ_dataProcessingMethod
			 *
			 * EQ_microorganism
			 *
			 * EQ_chemicalSubstance
			 *
			 * EQ_bizRules
			 *
			 */
			if (name.equals("EQ_type")) {
				checkParameterValueType(value, listTester);
				putEQListOfStringQuery(mongoQueryElements, "sensorElementList.sensorReport.type", (List<String>) value);
				continue;
			}

			if (name.equals("EQ_deviceID")) {
				checkParameterValueType(value, listTester);
				putEQListOfStringQuery(mongoQueryElements, "sensorElementList.sensorReport.deviceID",
						(List<String>) value);
				continue;
			}

			if (name.equals("EQ_deviceMetaData")) {
				checkParameterValueType(value, listTester);
				putEQListOfStringQuery(mongoQueryElements, "sensorElementList.sensorReport.deviceMetaData",
						(List<String>) value);
				continue;
			}

			if (name.equals("EQ_rawData")) {
				checkParameterValueType(value, listTester);
				putEQListOfStringQuery(mongoQueryElements, "sensorElementList.sensorReport.rawData",
						(List<String>) value);
				continue;
			}

			if (name.equals("EQ_dataProcessingMethod")) {
				checkParameterValueType(value, listTester);
				putEQListOfStringQuery(mongoQueryElements, "sensorElementList.sensorReport.dataProcessingMethod",
						(List<String>) value);
				continue;
			}

			if (name.equals("EQ_microorganism")) {
				checkParameterValueType(value, listTester);
				putEQListOfStringQuery(mongoQueryElements, "sensorElementList.sensorReport.microorganism",
						(List<String>) value);
				continue;
			}

			if (name.equals("EQ_chemicalSubstance")) {
				checkParameterValueType(value, listTester);
				putEQListOfStringQuery(mongoQueryElements, "sensorElementList.sensorReport.chemicalSubstance",
						(List<String>) value);
				continue;
			}

			if (name.equals("EQ_bizRules")) {
				checkParameterValueType(value, listTester);
				putEQListOfStringQuery(mongoQueryElements, "sensorElementList.sensorMetadata.bizRules",
						(List<String>) value);
				continue;
			}

			/**
			 *
			 * EQ_stringValue
			 *
			 * EQ_booleanValue
			 *
			 * EQ_hexBinaryValue
			 *
			 * LT_maxValue
			 *
			 * LT_minValue
			 *
			 * LT_meanValue
			 *
			 * GE_maxValue
			 *
			 * GE_minValue
			 *
			 * GE_meanValue
			 *
			 */
			if (name.equals("EQ_stringValue")) {
				putEQQuery(mongoQueryElements, "sensorElementList.sensorReport.stringValue", value, true);
				continue;
			}
			if (name.equals("EQ_booleanValue")) {
				putEQQuery(mongoQueryElements, "sensorElementList.sensorReport.booleanValue", value, true);
				continue;
			}
			if (name.equals("EQ_hexBinaryValue")) {
				putEQQuery(mongoQueryElements, "sensorElementList.sensorReport.hexBinaryValue", value, true);
				continue;
			}
			if (name.equals("LT_maxValue")) {
				putCompQuery(mongoQueryElements, "sensorElementList.sensorReport.maxValue", "$lt", value, true);
				continue;
			}
			if (name.equals("LT_minValue")) {
				putCompQuery(mongoQueryElements, "sensorElementList.sensorReport.minValue", "$lt", value, true);
				continue;
			}
			if (name.equals("LT_meanValue")) {
				putCompQuery(mongoQueryElements, "sensorElementList.sensorReport.meanValue", "$lt", value, true);
				continue;
			}
			if (name.equals("GE_maxValue")) {
				putCompQuery(mongoQueryElements, "sensorElementList.sensorReport.maxValue", "$gte", value, true);
				continue;
			}
			if (name.equals("GE_minValue")) {
				putCompQuery(mongoQueryElements, "sensorElementList.sensorReport.minValue", "$gte", value, true);
				continue;
			}
			if (name.equals("GE_meanValue")) {
				putCompQuery(mongoQueryElements, "sensorElementList.sensorReport.meanValue", "$gte", value, true);
				continue;
			}

			/**
			 *
			 * GE_sDev
			 *
			 * LT_sDev
			 *
			 * GE_percValue
			 *
			 * LT_percValue
			 *
			 * GE_percRank
			 *
			 * LT_percRank
			 *
			 */
			if (name.equals("GE_sDev")) {
				putCompQuery(mongoQueryElements, "sensorElementList.sensorReport.sDev", "$gte", value, true);
				continue;
			}
			if (name.equals("LT_sDev")) {
				putCompQuery(mongoQueryElements, "sensorElementList.sensorReport.sDev", "$lt", value, true);
				continue;
			}
			if (name.equals("GE_percValue")) {
				putCompQuery(mongoQueryElements, "sensorElementList.sensorReport.percValue", "$gte", value, true);
				continue;
			}
			if (name.equals("LT_percValue")) {
				putCompQuery(mongoQueryElements, "sensorElementList.sensorReport.percValue", "$lt", value, true);
				continue;
			}
			if (name.equals("GE_percRank")) {
				putCompQuery(mongoQueryElements, "sensorElementList.sensorReport.percRank", "$gte", value, true);
				continue;
			}
			if (name.equals("LT_percRank")) {
				putCompQuery(mongoQueryElements, "sensorElementList.sensorReport.percValue", "$lt", value, true);
				continue;
			}

			/**
			 * MATCH_epc: If this parameter is specified, the result will only include
			 * events that (a) have an epcList or a childEPCs field (that is, ObjectEvent,
			 * AggregationEvent, TransactionEvent or extension event types that extend one
			 * of those three); and where (b) one of the EPCs listed in the epcList or
			 * childEPCs field (depending on event type) matches one of the EPC patterns or
			 * URIs specified in this parameter, where the meaning of “matches” is as
			 * specified in Section 8.2.7.1.1. If this parameter is omitted, events are
			 * included regardless of their epcList or childEPCs field or whether the
			 * epcList or childEPCs field exists.
			 *
			 * v1.2
			 *
			 * List of String
			 *
			 * urn:epc:id:sgln:0614141[.]07346[.]....^regex
			 *
			 * String value = param.asString().getValue(); value = value.replace(".",
			 * "[.]"); value = value.replace("*", "(.)*"); BsonRegularExpression expr = new
			 * BsonRegularExpression(value); BsonDocument regexQuery = new
			 * BsonDocument(field, new BsonDocument("$regex", expr));
			 * orQueries.add(regexQuery);
			 *
			 */
			if (name.equals("MATCH_epc")) {
				checkParameterValueType(value, listTester);
				putMATCHListOfStringQuery(mongoQueryElements, "epcList", (List<String>) value);
				continue;
			}

			/**
			 * MATCH_parentID: Like MATCH_epc, but matches the parentID field of
			 * AggregationEvent, the parentID field of TransactionEvent, and extension event
			 * types that extend either AggregationEvent or TransactionEvent. The meaning of
			 * “matches” is as specified in Section 8.2.7.1.1.
			 *
			 * v1.2
			 *
			 * List of String
			 *
			 */
			if (name.equals("MATCH_parentID")) {
				checkParameterValueType(value, listTester);
				putMATCHListOfStringQuery(mongoQueryElements, "parentID", (List<String>) value);
				continue;
			}

			/**
			 * MATCH_inputEPC: If this parameter is specified, the result will only include
			 * events that (a) have an inputEPCList (that is, TransformationEvent or an
			 * extension event type that extends TransformationEvent); and where (b) one of
			 * the EPCs listed in the inputEPCList field matches one of the EPC patterns or
			 * URIs specified in this parameter. The meaning of “matches” is as specified in
			 * Section 8.2.7.1.1. If this parameter is omitted, events are included
			 * regardless of their inputEPCList field or whether the inputEPCList field
			 * exists.
			 *
			 * v1.2
			 *
			 * List of String
			 *
			 */
			if (name.equals("MATCH_inputEPC")) {
				checkParameterValueType(value, listTester);
				putMATCHListOfStringQuery(mongoQueryElements, "inputEPCList", (List<String>) value);
				continue;
			}

			/**
			 * MATCH_outputEPC: If this parameter is specified, the result will only include
			 * events that (a) have an inputEPCList (that is, TransformationEvent or an
			 * extension event type that extends TransformationEvent); and where (b) one of
			 * the EPCs listed in the inputEPCList field matches one of the EPC patterns or
			 * URIs specified in this parameter. The meaning of “matches” is as specified in
			 * Section 8.2.7.1.1. If this parameter is omitted, events are included
			 * regardless of their inputEPCList field or whether the inputEPCList field
			 * exists.
			 *
			 * v1.2
			 *
			 * List of String
			 *
			 */
			if (name.equals("MATCH_outputEPC")) {
				checkParameterValueType(value, listTester);
				putMATCHListOfStringQuery(mongoQueryElements, "outputEPCList", (List<String>) value);
				continue;
			}

			/**
			 * MATCH_anyEPC: If this parameter is specified, the result will only include
			 * events that (a) have an epcList field, a childEPCs field, a parentID field,
			 * an inputEPCList field, or an outputEPCList field (that is, ObjectEvent,
			 * AggregationEvent, TransactionEvent, TransformationEvent, or extension event
			 * types that extend one of those four); and where (b) the parentID field or one
			 * of the EPCs listed in the epcList, childEPCs, inputEPCList, or outputEPCList
			 * field (depending on event type) matches one of the EPC patterns or URIs
			 * specified in this parameter. The meaning of “matches” is as specified in
			 * Section 8.2.7.1.1.
			 *
			 * v1.2
			 *
			 * List of String
			 *
			 */
			if (name.equals("MATCH_anyEPC")) {
				checkParameterValueType(value, listTester);
				putMultiMATCHListOfStringQuery(mongoQueryElements,
						new String[] { "epcList", "inputEPCList", "outputEPCList", "parentID" }, (List<String>) value);
				continue;
			}

			/**
			 * MATCH_epcClass: If this parameter is specified, the result will only include
			 * events that (a) have a quantityList or a childQuantityList field (that is,
			 * ObjectEvent, AggregationEvent, TransactionEvent or extension event types that
			 * extend one of those three); and where (b) one of the EPC classes listed in
			 * the quantityList or childQuantityList field (depending on event type) matches
			 * one of the EPC patterns or URIs specified in this parameter. The result will
			 * also include QuantityEvents whose epcClass field matches one of the EPC
			 * patterns or URIs specified in this parameter. The meaning of “matches” is as
			 * specified in Section 8.2.7.1.1.
			 *
			 * v1.2
			 *
			 * List of String
			 */
			if (name.equals("MATCH_epcClass")) {
				checkParameterValueType(value, listTester);
				putMATCHListOfStringQuery(mongoQueryElements, "quantityList.epcClass", (List<String>) value);
				continue;
			}

			/**
			 * MATCH_inputEPCClass: If this parameter is specified, the result will only
			 * include events that (a) have an inputQuantityList field (that is,
			 * TransformationEvent or extension event types that extend it); and where (b)
			 * one of the EPC classes listed in the inputQuantityList field (depending on
			 * event type) matches one of the EPC patterns or URIs specified in this
			 * parameter. The meaning of “matches” is as specified in Section 8.2.7.1.1.
			 *
			 * v1.2
			 *
			 * List of String
			 *
			 */
			if (name.equals("MATCH_inputEPCClass")) {
				checkParameterValueType(value, listTester);
				putMATCHListOfStringQuery(mongoQueryElements, "inputQuantityList.epcClass", (List<String>) value);
				continue;
			}

			/**
			 * MATCH_outputEPCClass: If this parameter is specified, the result will only
			 * include events that (a) have an outputQuantityList field (that is,
			 * TransformationEvent or extension event types that extend it); and where (b)
			 * one of the EPC classes listed in the outputQuantityList field (depending on
			 * event type) matches one of the EPC patterns or URIs specified in this
			 * parameter. The meaning of “matches” is as specified in Section 8.2.7.1.1.
			 *
			 * v1.2
			 *
			 * List of String
			 *
			 */
			if (name.equals("MATCH_outputEPCClass")) {
				checkParameterValueType(value, listTester);
				putMATCHListOfStringQuery(mongoQueryElements, "outputQuantityList.epcClass", (List<String>) value);
				continue;
			}

			/**
			 * MATCH_anyEPCClass: If this parameter is specified, the result will only
			 * include events that (a) have a quantityList, childQuantityList,
			 * inputQuantityList, or outputQuantityList field (that is, ObjectEvent,
			 * AggregationEvent, TransactionEvent, TransformationEvent, or extension event
			 * types that extend one of those four); and where (b) one of the EPC classes
			 * listed in any of those fields matches one of the EPC patterns or URIs
			 * specified in this parameter. The result will also include QuantityEvents
			 * whose epcClass field matches one of the EPC patterns or URIs specified in
			 * this parameter. The meaning of “matches” is as specified in Section
			 * 8.2.7.1.1.
			 *
			 * v1.2
			 *
			 * List of String
			 *
			 */
			if (name.equals("MATCH_anyEPCClass")) {
				checkParameterValueType(value, listTester);
				putMultiMATCHListOfStringQuery(mongoQueryElements, new String[] { "quantityList.epcClass",
						"inputQuantityList.epcClass", "outputQuantityList.epcClass" }, (List<String>) value);
				continue;
			}

			/**
			 * EQ_readPoint: If specified, the result will only include events that (a) have
			 * a non-null readPoint field; and where (b) the value of the readPoint field
			 * matches one of the specified values. If this parameter and WD_ readPoint are
			 * both omitted, events are returned regardless of the value of the readPoint
			 * field or whether the readPoint field exists at all.
			 *
			 * v1.2
			 *
			 * List of String
			 *
			 */
			if (name.equals("EQ_readPoint")) {
				checkParameterValueType(value, listTester);
				readPointCandidate.addAll((List<String>) value);
				continue;
			}

			/**
			 * WD_readPoint: If specified, the result will only include events that (a) have
			 * a non-null readPoint field; and where (b) the value of the readPoint field
			 * matches one of the specified values, or is a direct or indirect descendant of
			 * one of the specified values. The meaning of “direct or indirect descendant”
			 * is specified by master data, as described in Section 6.5. (WD is an
			 * abbreviation for “with descendants.”) If this parameter and EQ_readPoint are
			 * both omitted, events are returned regardless of the value of the readPoint
			 * field or whether the readPoint field exists at all.
			 *
			 * v1.2
			 *
			 * List of String
			 *
			 */
			if (name.equals("WD_readPoint")) {
				checkParameterValueType(value, listTester);
				List<String> wdValues = new ArrayList<String>();
				for (String v : (List<String>) value) {
					wdValues.add(v);

					ObservableSubscriber<org.bson.Document> wdSubscriber = new ObservableSubscriber<org.bson.Document>();
					SOAPQueryServer.mVocCollection.find(new org.bson.Document("id", value)).subscribe(wdSubscriber);

					try {
						wdSubscriber.await();
					} catch (Throwable e) {
						ImplementationException e1 = new ImplementationException();
						e1.setStackTrace(new StackTraceElement[0]);
						e1.setReason(e.getMessage());
						throw e1;
					}

					List<org.bson.Document> rDocs = wdSubscriber.getReceived();

					for (org.bson.Document rDoc : rDocs) {
						if (rDoc.containsKey("children")) {
							List<String> children = rDoc.getList("children", String.class);
							wdValues.addAll(children);
						}
					}
				}

				readPointCandidate.addAll(wdValues);
				continue;
			}

			/**
			 * EQ_bizLocation: Like the EQ_ readPoint parameter, but for the bizLocation
			 * field.
			 *
			 * v1.2
			 *
			 * List of String
			 *
			 */
			if (name.equals("EQ_bizLocation")) {
				checkParameterValueType(value, listTester);
				bizLocationCandidate.addAll((List<String>) value);
				continue;
			}

			/**
			 * WD_bizLocation: Like the WD_readPoint parameter, but for the bizLocation
			 * field.
			 *
			 * v1.2
			 *
			 * List of String
			 * 
			 */
			if (name.equals("WD_bizLocation")) {
				checkParameterValueType(value, listTester);
				List<String> wdValues = new ArrayList<String>();
				for (String v : (List<String>) value) {
					wdValues.add(v);

					ObservableSubscriber<org.bson.Document> wdSubscriber = new ObservableSubscriber<org.bson.Document>();
					SOAPQueryServer.mVocCollection.find(new org.bson.Document("id", value)).subscribe(wdSubscriber);

					try {
						wdSubscriber.await();
					} catch (Throwable e) {
						ImplementationException e1 = new ImplementationException();
						e1.setStackTrace(new StackTraceElement[0]);
						e1.setReason(e.getMessage());
						throw e1;
					}

					List<org.bson.Document> rDocs = wdSubscriber.getReceived();

					for (org.bson.Document rDoc : rDocs) {
						if (rDoc.containsKey("children")) {
							List<String> children = rDoc.getList("children", String.class);
							wdValues.addAll(children);
						}
					}
				}

				bizLocationCandidate.addAll(wdValues);
				continue;
			}

			/**
			 * EQ_quantity (DEPCRECATED in EPCIS 1.1) If this parameter is specified, the
			 * result will only include events that (a) have a quantity field (that is,
			 * QuantityEvents or extension event type that extend QuantityEvent); and where
			 * (b) the quantity field is equal to the specified parameter.
			 *
			 * v1.1
			 *
			 * int
			 **/
			if (name.equals("EQ_quantity")) {
				checkParameterValueType(value, intTester);
				putIntQuery(mongoQueryElements, "quantity", "$eq", (Integer) value);
				continue;
			}

			/**
			 * GT_quantity (DEPCRECATED in EPCIS 1.1) Like EQ_quantity, but includes events
			 * whose quantity field is greater than the specified parameter.
			 *
			 * v1.1
			 *
			 */
			if (name.equals("GT_quantity")) {
				checkParameterValueType(value, intTester);
				putIntQuery(mongoQueryElements, "quantity", "$gt", (Integer) value);
				continue;
			}

			/**
			 * GE_quantity (DEPCRECATED in EPCIS 1.1) Like EQ_quantity, but includes events
			 * whose quantity field is greater than or equal to the specified parameter.
			 *
			 * v1.1
			 *
			 */
			if (name.equals("GE_quantity")) {
				checkParameterValueType(value, intTester);
				putIntQuery(mongoQueryElements, "quantity", "$gte", (Integer) value);
				continue;
			}

			/**
			 * LT_quantity (DEPCRECATED in EPCIS 1.1) Like EQ_quantity, but includes events
			 * whose quantity field is less than the specified parameter.
			 *
			 * v1.1
			 */
			if (name.equals("LT_quantity")) {
				checkParameterValueType(value, intTester);
				putIntQuery(mongoQueryElements, "quantity", "$lt", (Integer) value);
				continue;
			}

			/**
			 * LE_quantity (DEPCRECATED in EPCIS 1.1) Like EQ_quantity, but includes events
			 * whose quantity field is less than or equal to the specified parameter.
			 *
			 * v1.1
			 *
			 */
			if (name.equals("LE_quantity")) {
				checkParameterValueType(value, intTester);
				putIntQuery(mongoQueryElements, "quantity", "$lte", (Integer) value);
				continue;
			}

			/**
			 * EQ_eventID : If this parameter is specified, the result will only include
			 * events that (a) have a non-null eventID field; and where (b) the eventID
			 * field is equal to one of the values specified in this parameter. If this
			 * parameter is omitted, events are returned regardless of the value of the
			 * eventID field or whether the eventID field exists at all.
			 *
			 * v1.2
			 *
			 * List of String
			 *
			 */
			if (name.equals("EQ_eventID")) {
				checkParameterValueType(value, listTester);
				putEQListOfStringQuery(mongoQueryElements, "eventID", (List<String>) value);
				continue;
			}

			/**
			 * EQ_errorReason: If this parameter is specified, the result will only include
			 * events that (a) contain an ErrorDeclaration ; and where (b) the error
			 * declaration contains a non-null reason field; and where (c) the reason field
			 * is equal to one of the values specified in this parameter. If this parameter
			 * is omitted, events are returned regardless of the they contain an
			 * ErrorDeclaration or what the value of the reason field is.
			 *
			 * v1.2
			 *
			 * List of String
			 */
			if (name.equals("EQ_errorReason")) {
				checkParameterValueType(value, listTester);
				putEQListOfStringQuery(mongoQueryElements, "errorDeclaration.reason", (List<String>) value);
				continue;
			}

			/**
			 * EQ_correctiveEventID: If this parameter is specified, the result will only
			 * include events that (a) contain an ErrorDeclaration ; and where (b) one of
			 * the elements of the correctiveEventIDs list is equal to one of the values
			 * specified in this parameter. If this parameter is omitted, events are
			 * returned regardless of the they contain an ErrorDeclaration or the contents
			 * of the correctiveEventIDs list.
			 *
			 * v1.2
			 *
			 * List of String
			 *
			 */
			if (name.equals("EQ_correctiveEventID")) {
				checkParameterValueType(value, listTester);
				putEQListOfStringQuery(mongoQueryElements, "errorDeclaration.correctiveEventIDs", (List<String>) value);
				continue;
			}

			/**
			 * EXISTS_errorDeclaration: If this parameter is specified, the result will only
			 * include events that contain an ErrorDeclaration . If this parameter is
			 * omitted, events are returned regardless of whether they contain an
			 * ErrorDeclaration.
			 *
			 * v1.2
			 *
			 * Void
			 */
			if (name.equals("EXISTS_errorDeclaration")) {
				checkParameterValueType(value, voidTester);
				putExistsQuery(mongoQueryElements, "errorDeclaration");
				continue;
			}

			/**
			 * Maybe new in 2.0 EXISTS_sensorElement To confine query results to those
			 * containing sensor data:
			 *
			 */
			if (name.equals("EXISTS_sensorElement")) {
				checkParameterValueType(value, voidTester);
				putExistsQuery(mongoQueryElements, "sensorElementList");
				continue;
			}

			/**
			 * EQ_bizTransaction_type: This is not a single parameter, but a family of
			 * parameters. If a parameter of this form is specified, the result will only
			 * include events that (a) include a bizTransactionList; (b) where the business
			 * transaction list includes an entry whose type subfield is equal to type
			 * extracted from the name of this parameter; and (c) where the bizTransaction
			 * subfield of that entry is equal to one of the values specified in this
			 * parameter.
			 *
			 * v1.2
			 *
			 * List of String
			 *
			 */
			if (name.startsWith("EQ_bizTransaction_")) {
				checkParameterValueType(value, listTester);
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(18));
				putEQListOfStringQuery(mongoQueryElements, "bizTransactionList." + subField, (List<String>) value);
				continue;
			}

			/**
			 * EQ_source_type: This is not a single parameter, but a family of parameters.
			 * If a parameter of this form is specified, the result will only include events
			 * that (a) include a sourceList; (b) where the source list includes an entry
			 * whose type subfield is equal to type extracted from the name of this
			 * parameter; and (c) where the source subfield of that entry is equal to one of
			 * the values specified in this parameter.
			 *
			 * v1.2
			 *
			 * List of String
			 *
			 */
			if (name.startsWith("EQ_source_")) {
				checkParameterValueType(value, listTester);
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(10));
				putEQListOfStringQuery(mongoQueryElements, "sourceList." + subField, (List<String>) value);
				continue;
			}

			/**
			 * EQ_destination_type: This is not a single parameter, but a family of
			 * parameters. If a parameter of this form is specified, the result will only
			 * include events that (a) include a destinationList; (b) where the destination
			 * list includes an entry whose type subfield is equal to type extracted from
			 * the name of this parameter; and (c) where the destination subfield of that
			 * entry is equal to one of the values specified in this parameter.
			 *
			 * v1.2
			 *
			 * List of String
			 *
			 */
			if (name.startsWith("EQ_destination_")) {
				checkParameterValueType(value, listTester);
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(15));
				putEQListOfStringQuery(mongoQueryElements, "destinationList." + subField, (List<String>) value);
				continue;
			}

			/**
			 * EQ_ILMD_field: Analogous to EQ_fieldname , but matches events whose ILMD area
			 * (Section 7.3.6) contains a top-level field having the specified fieldname
			 * whose value matches one of the specified values. “Top level” means that the
			 * matching ILMD element must be an immediate child of the <ilmd> element, not
			 * an element nested within such an element. See EQ_INNER_ILMD_fieldname for
			 * querying inner extension elements.
			 *
			 * v1.2
			 *
			 * List of String, Int, Float, Time
			 *
			 */
			if (name.startsWith("EQ_ILMD_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(8));
				putEQQuery(mongoQueryElements, "ilmd." + subField, value, true);
				continue;
			}

			/**
			 * EQ_INNER_ERROR_DECLARATION_fieldname : Analogous to
			 * EQ_ERROR_DECLARATION_fieldname, but matches inner extension elements; that
			 * is, any XML element nested within a top-level extension element. Note that a
			 * matching inner element may exist within more than one top-level element or
			 * may occur more than once within a single top-level element; this parameter
			 * matches if at least one matching occurrence is found anywhere in the event
			 * (except at top-level)..
			 *
			 * v1.2
			 *
			 * List of String, Int, Float, Time
			 *
			 */
			if (name.startsWith("EQ_INNER_ERROR_DECLARATION_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(27));
				putEQQuery(mongoQueryElements, "errf." + subField, value, true);
				continue;
			}

			/**
			 * EQ/GT/GE/LT/LE_INNER_ERROR_DECLARATION_fieldname : Like
			 * EQ_INNER_ERROR_DECLARATION_fieldname as described above, but may be applied
			 * to a field of type Int, Float, or Time.
			 *
			 * v1.2
			 *
			 * Int, Float, Time
			 */
			if (name.startsWith("GT_INNER_ERROR_DECLARATION_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(27));
				putCompQuery(mongoQueryElements, "errf." + subField, "$gt", value, true);
				continue;
			}
			if (name.startsWith("GE_INNER_ERROR_DECLARATION_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(27));
				putCompQuery(mongoQueryElements, "errf." + subField, "$gte", value, true);
				continue;
			}
			if (name.startsWith("LT_INNER_ERROR_DECLARATION_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(27));
				putCompQuery(mongoQueryElements, "errf." + subField, "$lt", value, true);
				continue;
			}
			if (name.startsWith("LE_INNER_ERROR_DECLARATION_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(27));
				putCompQuery(mongoQueryElements, "errf." + subField, "$lte", value, true);
				continue;
			}

			/**
			 * EQ_INNER_ILMD_fieldname: Analogous to EQ_ILMD_fieldname, but matches inner
			 * ILMD elements; that is, any XML element nested at any level within a
			 * top-level ILMD element. Note that a matching inner element may exist within
			 * more than one top-level element or may occur more than once within a single
			 * top-level element; this parameter matches if at least one matching occurrence
			 * is found anywhere in the ILMD section (except at top-level).
			 *
			 * v1.2
			 *
			 * List of String, Int, Float, Time
			 *
			 */
			if (name.startsWith("EQ_INNER_ILMD_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(14));
				putEQQuery(mongoQueryElements, "ilmdf." + subField, value, true);
				continue;
			}

			/**
			 * EQ/GT/GE/LT/LE_INNER_ILMD_fieldname: Like EQ_INNER_ILMD_fieldname as
			 * described above, but may be applied to a field of type Int, Float, or Time.
			 *
			 * v1.2
			 *
			 * Int, Float, Time
			 */
			if (name.startsWith("GT_INNER_ILMD_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(14));
				putCompQuery(mongoQueryElements, "ilmdf." + subField, "$gt", value, true);
				continue;
			}
			if (name.startsWith("GE_INNER_ILMD_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(14));
				putCompQuery(mongoQueryElements, "ilmdf." + subField, "$gte", value, true);
				continue;
			}
			if (name.startsWith("LT_INNER_ILMD_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(14));
				putCompQuery(mongoQueryElements, "ilmdf." + subField, "$lt", value, true);
				continue;
			}
			if (name.startsWith("LE_INNER_ILMD_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(14));
				putCompQuery(mongoQueryElements, "ilmdf." + subField, "$lte", value, true);
				continue;
			}

			/**
			 * GT|GE|LT|LE_ILMD_field: Analogous to EQ_fieldname , GT_fieldname ,
			 * GE_fieldname , GE_fieldname , LT_fieldname , and LE_fieldname , respectively,
			 * but matches events whose ILMD area (Section 7.3.6) contains a field having
			 * the specified fieldname whose integer, float, or time value matches the
			 * specified value according to the specified relational operator.
			 *
			 * v1.2
			 *
			 * Int, Float, Time
			 *
			 */
			if (name.startsWith("GT_ILMD_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(8));
				putCompQuery(mongoQueryElements, "ilmd." + subField, "$gt", value, true);
				continue;
			}
			if (name.startsWith("GE_ILMD_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(8));
				putCompQuery(mongoQueryElements, "ilmd." + subField, "$gte", value, true);
				continue;
			}
			if (name.startsWith("LT_ILMD_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(8));
				putCompQuery(mongoQueryElements, "ilmd." + subField, "$lt", value, true);
				continue;
			}
			if (name.startsWith("LE_ILMD_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(8));
				putCompQuery(mongoQueryElements, "ilmd." + subField, "$lte", value, true);
				continue;
			}

			/**
			 * EXISTS_ILMD_fieldname: Like EXISTS_fieldname as described above, but events
			 * that have a non-empty field named fieldname in the ILMD area (Section 7.3.6).
			 * Fieldname is constructed as for EQ_ILMD_fieldname . Note that the value for
			 * this query parameter is ignored.
			 *
			 * v1.2
			 *
			 * Void
			 *
			 */
			if (name.startsWith("EXISTS_ILMD_")) {
				checkParameterValueType(value, voidTester);
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(12));
				putExistsQuery(mongoQueryElements, "ilmd." + subField);
				continue;
			}

			/**
			 * EXISTS_INNER_ILMD_fieldname : Like EXISTS_ILMD_fieldname as described above,
			 * but includes events that have a non-empty inner extension field named
			 * fieldname within the ILMD area. Note that the value for this query parameter
			 * is ignored.
			 *
			 * v1.2
			 *
			 * Void
			 *
			 */
			if (name.startsWith("EXISTS_INNER_ILMD_")) {
				checkParameterValueType(value, voidTester);
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(18));
				putExistsQuery(mongoQueryElements, "ilmdf." + subField);
				continue;
			}

			/**
			 * EQ_ERROR_DECLARATION_Fieldname : Analogous to EQ_fieldname , but matches
			 * events containing an ErrorDeclaration and where the ErrorDeclaration contains
			 * a field having the specified fieldname whose value matches one of the
			 * specified values.
			 *
			 * v1.2
			 *
			 * List of String, int, float, time
			 *
			 */
			if (name.startsWith("EQ_ERROR_DECLARATION_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(21));
				putEQQuery(mongoQueryElements, "errorDeclaration.extension." + subField, value, true);
				continue;
			}

			/**
			 * EQ/GT/GE/LT/LE_ERROR_DECLARATION_fieldname: Analogous to EQ_fieldname ,
			 * GT_fieldname , GE_fieldname , GE_fieldname , LT_fieldname , and LE_fieldname
			 * , respectively, but matches events containing an ErrorDeclaration and where
			 * the ErrorDeclaration contains a field having the specified fieldname whose
			 * integer, float, or time value matches the specified value according to the
			 * specified relational operator.
			 *
			 * v1.2
			 *
			 * Int, Float, Time
			 *
			 */
			if (name.startsWith("GT_ERROR_DECLARATION_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(21));
				putCompQuery(mongoQueryElements, "errorDeclaration.extension." + subField, "$gt", value, true);
				continue;
			}
			if (name.startsWith("GE_ERROR_DECLARATION_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(21));
				putCompQuery(mongoQueryElements, "errorDeclaration.extension." + subField, "$gte", value, true);
				continue;
			}
			if (name.startsWith("LT_ERROR_DECLARATION_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(21));
				putCompQuery(mongoQueryElements, "errorDeclaration.extension." + subField, "$lt", value, true);
				continue;
			}
			if (name.startsWith("LE_ERROR_DECLARATION_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(21));
				putCompQuery(mongoQueryElements, "errorDeclaration.extension." + subField, "$lte", value, true);
				continue;
			}

			/**
			 * EXISTS_ERROR_DECLARATION_fieldname : Like EXISTS_fieldname as described
			 * above, but events that have an error declaration containing a non-empty
			 * extension field named fieldname. Fieldname is constructed as for
			 * EQ_ERROR_DECLARATION_fieldname. Note that the value for this query parameter
			 * is ignored
			 *
			 * v1.2
			 *
			 * Void
			 *
			 */
			if (name.startsWith("EXISTS_ERROR_DECLARATION_")) {
				checkParameterValueType(value, voidTester);
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(25));
				putExistsQuery(mongoQueryElements, "errorDeclaration.extension." + subField);
				continue;
			}

			/**
			 * EXISTS_INNER_ERROR_DECLARATION_fieldname : Like
			 * EXISTS_ERROR_DECLARATION_fieldname as described above, but includes events
			 * that have an error declaration containing a non-empty inner extension field
			 * named fieldname. Note that the value for this query parameter is ignored.
			 *
			 * v1.2
			 *
			 * Void
			 *
			 */
			if (name.startsWith("EXISTS_INNER_ERROR_DECLARATION_")) {
				checkParameterValueType(value, voidTester);
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(31));
				putExistsQuery(mongoQueryElements, "errf." + subField);
				continue;
			}

			/**
			 *
			 * EQ_INNER_SENSORELEMENT_ GT/GE/LT/LE/EXISTS
			 */
			if (name.startsWith("EQ_INNER_SENSORELEMENT_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(23));
				putEQQuery(mongoQueryElements, "sensorElementList.sef." + subField, value, true);
				continue;
			}
			if (name.startsWith("GT_INNER_SENSORELEMENT_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(23));
				putCompQuery(mongoQueryElements, "sensorElementList.sef." + subField, "$gt", value, true);
				continue;
			}
			if (name.startsWith("GE_INNER_SENSORELEMENT_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(23));
				putCompQuery(mongoQueryElements, "sensorElementList.sef." + subField, "$gte", value, true);
				continue;
			}
			if (name.startsWith("LT_INNER_SENSORELEMENT_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(23));
				putCompQuery(mongoQueryElements, "sensorElementList.sef." + subField, "$lt", value, true);
				continue;
			}
			if (name.startsWith("LE_INNER_SENSORELEMENT_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(23));
				putCompQuery(mongoQueryElements, "sensorElementList.sef." + subField, "$lte", value, true);
				continue;
			}
			if (name.startsWith("EXISTS_INNER_SENSORELEMENT_")) {
				checkParameterValueType(value, voidTester);
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(27));
				putExistsQuery(mongoQueryElements, "sensorElementList.sef." + subField);
				continue;
			}

			/**
			 *
			 * EQ_SENSORMETADATA_
			 *
			 * EQ_SENSORREPORT_
			 *
			 * EXISTS_SENSORMETADATA_
			 *
			 * EXISTS_SENSORREPORT_
			 *
			 */
			if (name.startsWith("EQ_SENSORMETADATA_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(18));
				putEQQuery(mongoQueryElements, "sensorElementList.sensorMetadata.otherAttributes." + subField, value,
						true);
				continue;
			}
			if (name.startsWith("EQ_SENSORREPORT_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(16));
				putEQQuery(mongoQueryElements, "sensorElementList.sensorReport.otherAttributes." + subField, value,
						true);
				continue;
			}
			if (name.startsWith("EXISTS_SENSORMETADATA_")) {
				checkParameterValueType(value, voidTester);
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(22));
				putExistsQuery(mongoQueryElements, "sensorElementList.sensorMetadata.otherAttributes." + subField);
				continue;
			}
			if (name.startsWith("EXISTS_SENSORREPORT_")) {
				checkParameterValueType(value, voidTester);
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(20));
				putExistsQuery(mongoQueryElements, "sensorElementList.sensorReport.otherAttributes." + subField);
				continue;
			}

			/**
			 *
			 * EQ_SENSORELEMENT_ GT/GE/LT/LE/EXISTS
			 */
			if (name.startsWith("EQ_SENSORELEMENT_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(17));
				putEQQuery(mongoQueryElements, "sensorElementList.extension." + subField, value, true);
				continue;
			}
			if (name.startsWith("GT_SENSORELEMENT_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(17));
				putCompQuery(mongoQueryElements, "sensorElementList.extension." + subField, "$gt", value, true);
				continue;
			}
			if (name.startsWith("GE_SENSORELEMENT_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(17));
				putCompQuery(mongoQueryElements, "sensorElementList.extension." + subField, "$gte", value, true);
				continue;
			}
			if (name.startsWith("LT_SENSORELEMENT_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(17));
				putCompQuery(mongoQueryElements, "sensorElementList.extension." + subField, "$lt", value, true);
				continue;
			}
			if (name.startsWith("LE_SENSORELEMENT_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(17));
				putCompQuery(mongoQueryElements, "sensorElementList.extension." + subField, "$lte", value, true);
				continue;
			}
			if (name.startsWith("EXISTS_SENSORELEMENT_")) {
				checkParameterValueType(value, voidTester);
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(21));
				putExistsQuery(mongoQueryElements, "sensorElementList.extension." + subField);
				continue;
			}

			/**
			 * EQ_INNER_fieldname : Analogous to EQ_fieldname, but matches inner extension
			 * elements; that is, any XML element nested at any level within a top-level
			 * extension element. Note that a matching inner element may exist within more
			 * than one top-level element or may occur more than once within a single
			 * top-level element; this parameter matches if at least one matching occurrence
			 * is found anywhere in the event (except at top-level). Note that unlike a
			 * top-level extension element, an inner extension element may have a null XML
			 * namespace. To match such an inner element, the empty string is used in place
			 * of the XML namespace when constructing the query parameter name. For example,
			 * to match inner element <elt1> with no XML namespace, the query parameter
			 * would be EQ_INNER_#elt1.
			 *
			 * v1.2
			 *
			 * List of String, Int, Float, Time
			 *
			 */
			if (name.startsWith("EQ_INNER_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(9));
				putEQQuery(mongoQueryElements, "extf." + subField, value, true);
				continue;
			}

			/**
			 * EQ/GT/GE/LT/LE_INNER_fieldname: Like EQ_INNER_fieldname as described above,
			 * but may be applied to a field of type Int, Float, or Time.
			 *
			 * v1.2
			 *
			 * Int, Float, Time
			 */
			if (name.startsWith("GT_INNER_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(9));
				putCompQuery(mongoQueryElements, "extf." + subField, "$gt", value, true);
				continue;
			}
			if (name.startsWith("GE_INNER_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(9));
				putCompQuery(mongoQueryElements, "extf." + subField, "$gte", value, true);
				continue;
			}
			if (name.startsWith("LT_INNER_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(9));
				putCompQuery(mongoQueryElements, "extf." + subField, "$lt", value, true);
				continue;
			}
			if (name.startsWith("LE_INNER_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(9));
				putCompQuery(mongoQueryElements, "extf." + subField, "$lte", value, true);
				continue;
			}

			/**
			 *
			 * EQ_value_uom
			 *
			 * LT_value_uom
			 *
			 * GE_value_uom
			 */
			if (name.startsWith("EQ_value_")) {
				checkParameterValueType(value, doubleTester);
				String subField = name.substring(9);
				Double dVal = (Double) value;
				Double rValue = SOAPQueryServer.unitConverter.getRepresentativeValue(subField, dVal);
				String rType = SOAPQueryServer.unitConverter.getRepresentativeType(subField);

				if (rValue == null || rType == null) {
					QueryParameterException e = new QueryParameterException();
					e.setStackTrace(new StackTraceElement[0]);
					e.setReason("the value of a parameter is of the wrong type or out of range: uom, value");
					throw e;
				}

				putEQQuery(mongoQueryElements, "sensorElementList.sensorReport.rValue", rValue, true);
				putEQQuery(mongoQueryElements, "sensorElementList.sensorReport.rType", rType, true);
				continue;
			}
			if (name.startsWith("LT_value_")) {
				checkParameterValueType(value, doubleTester);
				String subField = name.substring(9);
				Double dVal = (Double) value;
				Double rValue = SOAPQueryServer.unitConverter.getRepresentativeValue(subField, dVal);
				String rType = SOAPQueryServer.unitConverter.getRepresentativeType(subField);

				if (rValue == null || rType == null) {
					QueryParameterException e = new QueryParameterException();
					e.setStackTrace(new StackTraceElement[0]);
					e.setReason("the value of a parameter is of the wrong type or out of range: uom, value");
					throw e;
				}

				putCompQuery(mongoQueryElements, "sensorElementList.sensorReport.value", "$lt", rValue, true);
				putEQQuery(mongoQueryElements, "sensorElementList.sensorReport.uom", rType, true);
				continue;
			}
			if (name.startsWith("GE_value_")) {
				checkParameterValueType(value, doubleTester);
				String subField = name.substring(9);
				Double dVal = (Double) value;
				Double rValue = SOAPQueryServer.unitConverter.getRepresentativeValue(subField, dVal);
				String rType = SOAPQueryServer.unitConverter.getRepresentativeType(subField);

				if (rValue == null || rType == null) {
					QueryParameterException e = new QueryParameterException();
					e.setStackTrace(new StackTraceElement[0]);
					e.setReason("the value of a parameter is of the wrong type or out of range: uom, value");
					throw e;
				}

				putCompQuery(mongoQueryElements, "sensorElementList.sensorReport.value", "$gte", rValue, true);
				putEQQuery(mongoQueryElements, "sensorElementList.sensorReport.uom", rType, true);
				continue;
			}

			/**
			 * EQ_fieldname: This is not a single parameter, but a family of parameters. If
			 * a parameter of this form is specified, the result will only include events
			 * that (a) have a field named fieldname whose type is either String or a
			 * vocabulary type; and where (b) the value of that field matches one of the
			 * values specified in this parameter. Fieldname is the fully qualified name of
			 * an extension field. The name of an extension field is an XML qname; that is,
			 * a pair consisting of an XML namespace URI and a name. The name of the
			 * corresponding query parameter is constructed by concatenating the following:
			 * the string EQ_, the namespace URI for the extension field, a pound sign (#),
			 * and the name of the extension field.
			 *
			 * v1.2
			 *
			 * List of String, Int, Float, Time
			 *
			 */
			if (name.startsWith("EQ_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(3));
				putEQQuery(mongoQueryElements, "extension." + subField, value, true);
				continue;
			}
			/**
			 * GT/GE/LT/LE_fieldname: Like EQ_fieldname as described above, but may be
			 * applied to a field of type Int, Float, or Time. The result will include
			 * events that (a) have a field named fieldname; and where (b) the type of the
			 * field matches the type of this parameter (Int, Float, or Time); and where (c)
			 * the value of the field is greater than the specified value. Fieldname is
			 * constructed as for EQ_fieldname.
			 *
			 * v1.2
			 *
			 * Int, Float, Time
			 *
			 */
			if (name.startsWith("GT_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(3));
				putCompQuery(mongoQueryElements, "extension." + subField, "$gt", value, true);
				continue;
			}
			if (name.startsWith("GE_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(3));
				putCompQuery(mongoQueryElements, "extension." + subField, "$gte", value, true);
				continue;
			}
			if (name.startsWith("LT_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(3));
				putCompQuery(mongoQueryElements, "extension." + subField, "$lt", value, true);
				continue;
			}
			if (name.startsWith("LE_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(3));
				putCompQuery(mongoQueryElements, "extension." + subField, "$lte", value, true);
				continue;
			}

			/**
			 * EXISTS_INNER_fieldname : Like EQ_fieldname as described above, but may be
			 * applied to a field of any type (including complex types). The result will
			 * include events that have a non-empty field named fieldname. Fieldname is
			 * constructed as for EQ_fieldname. Note that the value for this query parameter
			 * is ignored.
			 *
			 * v1.2
			 *
			 * Void
			 *
			 */
			if (name.startsWith("EXISTS_INNER_")) {
				checkParameterValueType(value, voidTester);
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(13));
				putExistsQuery(mongoQueryElements, "extf." + subField);
				continue;
			}

			/**
			 * EXISTS_fieldname: Like EQ_fieldname as described above, but may be applied to
			 * a field of any type (including complex types). The result will include events
			 * that have a non-empty field named fieldname . Fieldname is constructed as for
			 * EQ_fieldname . EXISTS_ ILMD_fieldname HASATTR_fieldname Void Note that the
			 * value for this query parameter is ignored.
			 *
			 * v1.2
			 *
			 * Void
			 */
			if (name.startsWith("EXISTS_")) {
				checkParameterValueType(value, voidTester);
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(7));
				putExistsQuery(mongoQueryElements, "extension." + subField);
				continue;
			}

			/**
			 * HASATTR_fieldname : This is not a single parameter, but a family of
			 * parameters. If a parameter of this form is specified, the result will only
			 * include events that (a) have a field named fieldname whose type is a
			 * vocabulary type; and (b) where the value of that field is a vocabulary
			 * element for which master data is available; and (c) the master data has a
			 * non-null attribute whose name matches one of the values specified in this
			 * parameter. Fieldname is the fully qualified name of a field. For a standard
			 * field, this is simply the field name; e.g., bizLocation. For an extension
			 * field, the name of an extension field is an XML qname; that is, a pair
			 * consisting of an XML namespace URI and a name. The name of the corresponding
			 * query parameter is constructed by concatenating the following: the string
			 * HASATTR_, the namespace URI for the extension field, a pound sign (#), and
			 * the name of the extension field.
			 *
			 * v1.2
			 *
			 * check: fieldname and value is matched check: value is available in MasterData
			 *
			 * List of String
			 */
			if (name.startsWith("HASATTR_")) {
				checkParameterValueType(value, listTester);
				checkNull((List<String>) value);
				// Check value is available in MasterData
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(8));
				for (Object valueObj : (List<String>) value) {
					String str = valueObj.toString();

					ObservableSubscriber<org.bson.Document> wdSubscriber = new ObservableSubscriber<org.bson.Document>();
					SOAPQueryServer.mVocCollection.find(new org.bson.Document("id", str)).subscribe(wdSubscriber);

					try {
						wdSubscriber.await();
					} catch (Throwable e) {
						ImplementationException e1 = new ImplementationException();
						e1.setStackTrace(new StackTraceElement[0]);
						e1.setReason(e.getMessage());
						throw e1;
					}

					if (wdSubscriber.getReceived().isEmpty()) {
						QueryParameterException e = new QueryParameterException();
						e.setStackTrace(new StackTraceElement[0]);
						e.setReason(
								"the value of a parameter is of the wrong type or out of range ( MasterData is not available )");
						throw e;
					}
				}

				if (subField.contains("#")) {
					subField = "extension." + subField;
				}
				mongoQueryElements.add(new Document(subField, new Document("$in", (List<String>) value)));
				continue;
			}

			/**
			 * EQATTR_fieldname _attrname : This is not a single parameter, but a family of
			 * parameters. If a parameter of this form is specified, the result will only
			 * include events that (a) have a field named fieldname whose type is a
			 * vocabulary type; and (b) where the value of that field is a vocabulary
			 * element for which master data is available; and (c) the master data has a
			 * non-null attribute named attrname; and (d) where the value of that attribute
			 * matches one of the values specified in this parameter. Fieldname is
			 * constructed as for HASATTR_fieldname. The implementation MAY raise a
			 * QueryParameterException if fieldname or attrname includes an underscore
			 * character. Explanation (non-normative): because the presence of an underscore
			 * in fieldname or attrname presents an ambiguity as to where the division
			 * between fieldname and attrname lies, an implementation is free to reject the
			 * query parameter if it cannot disambiguate.
			 *
			 * required SimpleMasterDataQuery
			 *
			 * v1.2
			 *
			 * step1: get vocabulary ids where attrname == value
			 *
			 * step2: EQATTR_fieldname == ids
			 *
			 * List of String
			 *
			 */
			if (name.startsWith("EQATTR_")) {
				checkParameterValueType(value, listTester);
				String[] nameArr = name.split("_");
				if (nameArr.length < 3) {
					QueryParameterException e = new QueryParameterException();
					e.setStackTrace(new StackTraceElement[0]);
					e.setReason(
							"the value of a parameter is of the wrong type or out of range ( EQATTR requires two underscores )");
					throw e;
				}
				String fieldname = nameArr[1];
				String attrname = nameArr[2];

				// Step1
				List<String> ids = new ArrayList<String>();

				ObservableSubscriber<org.bson.Document> subscriber = new ObservableSubscriber<org.bson.Document>();
				SOAPQueryServer.mVocCollection
						.find(new org.bson.Document().append("attributes." + BSONReadUtil.encodeMongoObjectKey(attrname),
								new org.bson.Document().append("$in", value)))
						.subscribe(subscriber);

				try {
					subscriber.await();
				} catch (Throwable e) {
					ImplementationException e1 = new ImplementationException();
					e1.setStackTrace(new StackTraceElement[0]);
					e1.setReason(e.getMessage());
					throw e1;
				}

				List<org.bson.Document> mdDocs = subscriber.getReceived();

				for (org.bson.Document mdDoc : mdDocs) {
					ids.add(mdDoc.getString("id"));
				}

				// Step2
				if (fieldname.contains("#")) {
					fieldname = "extension." + BSONReadUtil.encodeMongoObjectKey(fieldname);
				}
				mongoQueryElements.add(new Document(fieldname, new Document("$in", ids)));
			}
		}

		// readPoint, bizLocation Candidates
		if (readPointCandidate != null && !readPointCandidate.isEmpty()) {
			mongoQueryElements
					.add(new Document("readPoint", new Document("$in", new ArrayList<String>(readPointCandidate))));
		}
		if (bizLocationCandidate != null && !bizLocationCandidate.isEmpty()) {
			mongoQueryElements
					.add(new Document("bizLocation", new Document("$in", new ArrayList<String>(bizLocationCandidate))));
		}

		mongoQuery = new Document();
		if (mongoQueryElements.size() != 0)
			mongoQuery.put("$and", mongoQueryElements);

		/**
		 * orderBy : If specified, names a single field that will be used to order the
		 * results. The orderDirection field specifies whether the ordering is in
		 * ascending sequence or descending sequence. Events included in the result that
		 * lack the specified field altogether may occur in any position within the
		 * result event list. The value of this parameter SHALL be one of: eventTime,
		 * recordTime, or the fully qualified name of an extension field whose type is
		 * Int, Float, Time, or String. A fully qualified fieldname is constructed as
		 * for the EQ_fieldname parameter. In the case of a field of type String, the
		 * ordering SHOULD be in lexicographic order based on the Unicode encoding of
		 * the strings, or in some other collating sequence appropriate to the locale.
		 * If omitted, no order is specified. The implementation MAY order the results
		 * in any order it chooses, and that order MAY differ even when the same query
		 * is executed twice on the same data. (In EPCIS 1.0, the value quantity was
		 * also permitted, but its use is deprecated in EPCIS 1.1.)
		 *
		 * orderDirection : If specified and orderBy is also specified, specifies
		 * whether the results are ordered in ascending or descending sequence according
		 * to the key specified by orderBy. The value of this parameter must be one of
		 * ASC (for ascending order) or DESC (for descending order); if not, the
		 * implementation SHALL raise a QueryParameterException. If omitted, defaults to
		 * DESC.
		 *
		 * v1.2
		 *
		 * String
		 */
		if (orderBy != null) {
			mongoSort.put(orderBy, orderDirection);
		}

	}

	private void putEQListOfStringQuery(List<Document> mongoQueryElements, String field, List<String> valueList)
			throws QueryParameterException {
		checkNull(valueList);
		mongoQueryElements.add(new Document(field, new Document("$in", valueList)));
	}

	private void putDateQuery(List<Document> mongoQueryElements, String field, String comparator, Long value)
			throws QueryParameterException {
		checkNull(value);
		mongoQueryElements.add(new Document(field, new Document(comparator, value)));
	}

	/**
	 * for EQ_fieldName query: contains List of String as well as int, float, time
	 */
	@SuppressWarnings("unchecked")
	private void putEQQuery(List<Document> mongoQueryElements, String field, Object value, boolean x)
			throws QueryParameterException {
		if (value instanceof List) {
			checkParameterValueType(value, listTester);
			checkNull((List<String>) value);
			mongoQueryElements.add(new Document(field, new Document("$in", (List<String>) value)));
		} else if (value instanceof Integer) {
			mongoQueryElements.add(new Document(field, (Integer) value));
		} else if (value instanceof Double) {
			mongoQueryElements.add(new Document(field, (Double) value));
		} else if (value instanceof Long) {
			mongoQueryElements.add(new Document(field, (Long) value));
		} else if (value instanceof Boolean) {
			mongoQueryElements.add(new Document(field, (Boolean) value));
		} else if (value instanceof String) {
			mongoQueryElements.add(new Document(field, (String) value));
		} else {
			QueryParameterException e = new QueryParameterException();
			e.setStackTrace(new StackTraceElement[0]);
			e.setReason("the value of a parameter is of the wrong type or out of range");
			throw e;
		}
	}

	private void putCompQuery(List<Document> mongoQueryElements, String field, String comparator, Object value,
			boolean x) throws QueryParameterException {
		if (value instanceof Integer) {
			mongoQueryElements.add(new Document(field, new Document(comparator, (Integer) value)));
		} else if (value instanceof Double) {
			mongoQueryElements.add(new Document(field, new Document(comparator, (Double) value)));
		} else if (value instanceof Long) {
			mongoQueryElements.add(new Document(field, new Document(comparator, (Long) value)));
		} else {
			QueryParameterException e = new QueryParameterException();
			e.setStackTrace(new StackTraceElement[0]);
			e.setReason("the value of a parameter is of the wrong type or out of range");
			throw e;
		}
	}

	private void putMATCHListOfStringQuery(List<Document> mongoQueryElements, String field, List<String> value)
			throws QueryParameterException {
		checkNull(value);
		List<Document> values = value.parallelStream().map(v -> {
			if (v.contains("*")) {
				v = v.replaceAll("\\.", "[.]");
				v = v.replaceAll("\\*", "(.)*");
				return new Document(field, new Document("$regex", v));
			} else {
				return new Document(field, v);
			}
		}).collect(Collectors.toList());

		mongoQueryElements.add(new Document("$or", values));
	}

	private void putMultiMATCHListOfStringQuery(List<Document> mongoQueryElements, String[] fields,
			List<String> valueList) throws QueryParameterException {
		checkNull(valueList);
		List<Document> values = valueList.parallelStream().flatMap(v -> {
			List<Document> inner = new ArrayList<Document>();
			for (String field : fields) {
				if (v.contains("*")) {
					v = v.replaceAll("\\.", "[.]");
					v = v.replaceAll("\\*", "(.)*");
					inner.add(new Document(field, new Document("$regex", v)));
				} else {
					inner.add(new Document(field, v));
				}
			}
			return inner.parallelStream();
		}).collect(Collectors.toList());

		mongoQueryElements.add(new Document("$or", values));
	}

	private void putIntQuery(List<Document> mongoQueryElements, String field, String comparator, Integer value)
			throws QueryParameterException {
		checkNull(value);
		mongoQueryElements.add(new Document(field, new Document(comparator, value)));
	}

	private void putExistsQuery(List<Document> mongoQueryElements, String field) throws QueryParameterException {
		mongoQueryElements.add(new Document(field, new Document("$exists", true)));
	}

	private void checkNull(List<String> valueList) throws QueryParameterException {
		if (valueList == null || valueList.isEmpty()) {
			QueryParameterException e = new QueryParameterException();
			e.setStackTrace(new StackTraceElement[0]);
			e.setReason("the value of a parameter is of the wrong type or out of range: null");
			throw e;
		}
	}

	private void checkNull(Long value) throws QueryParameterException {
		if (value == null) {
			QueryParameterException e = new QueryParameterException();
			e.setStackTrace(new StackTraceElement[0]);
			e.setReason("the value of a parameter is of the wrong type or out of range: null");
			throw e;
		}
	}

	private void checkNull(Integer value) throws QueryParameterException {
		if (value == null) {
			QueryParameterException e = new QueryParameterException();
			e.setStackTrace(new StackTraceElement[0]);
			e.setReason("the value of a parameter is of the wrong type or out of range: null");
			throw e;
		}
	}

	private void checkNull(Boolean value) throws QueryParameterException {
		if (value == null) {
			QueryParameterException e = new QueryParameterException();
			e.setStackTrace(new StackTraceElement[0]);
			e.setReason("the value of a parameter is of the wrong type or out of range: null");
			throw e;
		}
	}

	@SuppressWarnings({ "unchecked" })
	private <T> void checkParameterValueType(Object value, T container) throws QueryParameterException {
		try {
			container = (T) value;
		} catch (Exception e1) {
			QueryParameterException e = new QueryParameterException();
			e.setStackTrace(new StackTraceElement[0]);
			e.setReason("the value of a parameter is of the wrong type or out of range: value should be"
					+ container.getClass() + "if given");
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	private void makeSimpleMasterDataQuery(Iterator<Map.Entry<String, Object>> paramIterator)
			throws QueryParameterException, ImplementationException {
		List<Document> mongoQueryElements = new ArrayList<Document>();
		mongoProjection = new Document();
		elementNameCandidate = new HashSet<>();
		attributeProjection = new HashSet<>();

		while (paramIterator.hasNext()) {
			Map.Entry<String, Object> entry = paramIterator.next();
			String name = entry.getKey();
			Object value = entry.getValue();

			/**
			 * vocabularyName: If specified, only vocabulary elements drawn from one of the
			 * specified vocabularies will be included in the results. Each element of the
			 * specified list is the formal URI name for a vocabulary; e.g., one of the URIs
			 * specified in the table at the end of Section 7.2. If omitted, all
			 * vocabularies are considered.
			 *
			 * e.g., urn:epcglobal:epcis:vtype:BusinessLocation
			 *
			 * v1.2
			 *
			 * List of String
			 *
			 * Optional
			 */

			if (name.equals("vocabularyName")) {
				checkParameterValueType(value, listTester);
				putEQListOfStringQuery(mongoQueryElements, "type", (List<String>) value);
				continue;
			}

			/**
			 * includeAttributes: If true, the results will include attribute names and
			 * values for matching vocabulary elements. If false, attribute names and values
			 * will not be included in the result.
			 *
			 * v1.2
			 *
			 * Boolean
			 *
			 * Mandatory
			 */
			if (name.equals("includeAttributes")) {
				checkParameterValueType(value, booleanTester);
				checkNull((Boolean) value);
				includeAttributes = (Boolean) value;
				continue;
			}

			/**
			 * includeChildren: If true, the results will include the children list for
			 * matching vocabulary elements. If false, children lists will not be included
			 * in the result.
			 *
			 * v1.2
			 *
			 * Boolean
			 *
			 * Mandatory
			 */
			if (name.equals("includeChildren")) {
				checkParameterValueType(value, booleanTester);
				checkNull((Boolean) value);
				includeChildren = (Boolean) value;
				continue;
			}

			/**
			 * attributeNames: If specified, only those attributes whose names match one of
			 * the specified names will be included in the results. If omitted, all
			 * attributes for each matching vocabulary element will be included. (To obtain
			 * a list of vocabulary element names with no attributes, specify false for
			 * includeAttributes.) The value of this parameter SHALL be ignored if
			 * includeAttributes is false. Note that this parameter does not affect which
			 * vocabulary elements are included in the result; it only limits which
			 * attributes will be included with each vocabulary element.
			 *
			 * (Projection)
			 *
			 * v1.2
			 *
			 * List of String
			 *
			 * Optional
			 */
			if (name.equals("attributeNames")) {
				checkParameterValueType(value, listTester);
				checkNull((List<String>) value);
				putListOfStringAttributeProjection((List<String>) value);
				continue;
			}

			/**
			 * EQ_name: If specified, the result will only include vocabulary elements whose
			 * names are equal to one of the specified values. If this parameter and WD_name
			 * are both omitted, vocabulary elements are included regardless of their names.
			 *
			 * v1.2
			 *
			 * List of String
			 *
			 * Optional
			 */
			if (name.equals("EQ_name")) {
				checkParameterValueType(value, listTester);
				checkNull((List<String>) value);
				elementNameCandidate.addAll((List<String>) value);
				continue;
			}

			/**
			 * WD_name: If specified, the result will only include vocabulary elements that
			 * either match one of the specified names, or are direct or indirect
			 * descendants of a vocabulary element that matches one of the specified names.
			 * The meaning of “direct or indirect descendant” is described in Section 6.5.
			 * (WD is an abbreviation for “with descendants.”) If this parameter and EQ_name
			 * are both omitted, vocabulary elements are included regardless of their names.
			 *
			 * v1.2
			 *
			 * List of String
			 *
			 * Optional
			 */
			if (name.equals("WD_name")) {
				checkParameterValueType(value, listTester);
				checkNull((List<String>) value);
				List<String> wdValues = new ArrayList<String>();
				for (String str : (List<String>) value) {
					wdValues.add(str);

					ObservableSubscriber<org.bson.Document> wdSubscriber = new ObservableSubscriber<org.bson.Document>();
					SOAPQueryServer.mVocCollection.find(new org.bson.Document("id", str)).subscribe(wdSubscriber);

					try {
						wdSubscriber.await();
					} catch (Throwable e) {
						ImplementationException e1 = new ImplementationException();
						e1.setStackTrace(new StackTraceElement[0]);
						e1.setReason(e.getMessage());
						throw e1;
					}

					List<org.bson.Document> rDocs = wdSubscriber.getReceived();

					for (org.bson.Document rDoc : rDocs) {
						if (rDoc.containsKey("children")) {
							List<String> children = rDoc.getList("children", String.class);
							wdValues.addAll(children);
						}
					}
				}
				elementNameCandidate.addAll(wdValues);
				continue;
			}

			/**
			 * HASATTR: If specified, the result will only include vocabulary elements that
			 * have a non-null attribute whose name matches one of the values specified in
			 * this parameter.
			 *
			 * v1.2
			 *
			 * (EXISTS)
			 *
			 * List of String
			 *
			 * Optional
			 */
			if (name.equals("HASATTR")) {
				checkParameterValueType(value, listTester);
				putExistsListOfStringQuery(mongoQueryElements, (List<String>) value);
				continue;
			}

			/**
			 * EQATTR_attrname: This is not a single parameter, but a family of parameters.
			 * If a parameter of this form is specified, the result will only include
			 * vocabulary elements that have a non-null attribute named attrname, and where
			 * the value of that attribute matches one of the values specified in this
			 * parameter.
			 *
			 * v1.2
			 *
			 * (Extension)
			 *
			 * List of String
			 *
			 * Optional
			 */
			if (name.startsWith("EQATTR_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(name.substring(7));
				putEQQuery(mongoQueryElements, "attributes." + subField, value, true);
				continue;
			}

			/**
			 * maxElementCount: If specified, at most this many vocabulary elements will be
			 * included in the query result. If the query would otherwise return more than
			 * this number of vocabulary elements, a QueryTooLargeException SHALL be raised
			 * instead of a normal query result. If this parameter is omitted, any number of
			 * vocabulary elements may be included in the query result. Note, however, that
			 * the EPCIS implementation is free to raise a QueryTooLargeException regardless
			 * of the setting of this parameter (see Section 8.2.3).
			 *
			 * v1.2
			 *
			 * Int
			 *
			 * Optional
			 */
			if (name.equals("maxElementCount")) {
				checkParameterValueType(value, intTester);
				checkNull((Integer) value);
				maxCount = (Integer) value;
			}

		}

		if (includeAttributes == null || includeChildren == null) {
			QueryParameterException e = new QueryParameterException();
			e.setStackTrace(new StackTraceElement[0]);
			e.setReason(
					"the value of a parameter is of the wrong type or out of range [mandatory query param. is missing]");
			throw e;
		}

		if (includeAttributes && attributeProjection.size() != 0) {
			// project has only true values
			mongoProjection.put("id", true);
			mongoProjection.put("type", true);
			if (includeChildren)
				mongoProjection.put("children", true);
			for (String s : attributeProjection) {
				mongoProjection.put("attributes." + BSONReadUtil.encodeMongoObjectKey(s), true);
			}
		} else {
			if (!includeAttributes)
				mongoProjection.put("attributes", false);
			if (!includeChildren)
				mongoProjection.put("children", false);
		}

		mongoQuery = new Document();

		if (elementNameCandidate != null && !elementNameCandidate.isEmpty()) {
			mongoQueryElements
					.add(new Document("id", new Document("$in", new ArrayList<String>(elementNameCandidate))));
		}

		if (mongoQueryElements.size() != 0)
			mongoQuery.put("$and", mongoQueryElements);

	}

	private void putListOfStringAttributeProjection(List<String> valueList) {
		attributeProjection.addAll(valueList);
	}

	private void putExistsListOfStringQuery(List<Document> mongoQueryElements, List<String> valueList)
			throws QueryParameterException {
		checkNull(valueList);
		for (String value : valueList) {
			mongoQueryElements.add(
					new Document("attributes." + BSONReadUtil.encodeMongoObjectKey(value), new Document("$exists", true)));
		}
	}

	private QueryParameterException getQueryParameterException(String target) {
		QueryParameterException e = new QueryParameterException();
		e.setStackTrace(new StackTraceElement[0]);
		e.setReason(
				"the value of a parameter is of the wrong type or out of range: value should be epcisq:ArrayOfString if given: "
						+ target);
		return e;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Create a query description from REST servie
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public QueryDescription(MultiMap paramMap) throws QueryParameterException, ImplementationException {
		makeSimpleEventQuery(paramMap);
	}

	public QueryDescription(String epc, MultiMap paramMap) throws QueryParameterException, ImplementationException {
		if (epc == null)
			makeSimpleMasterDataQuery(paramMap);
		else {
			mongoQuery = new Document("id", epc);
		}
	}

	private void makeSimpleMasterDataQuery(MultiMap paramMap)
			throws QueryParameterException, ImplementationException, NumberFormatException {

		List<Document> mongoQueryElements = new ArrayList<Document>();
		mongoProjection = new Document();
		elementNameCandidate = new HashSet<>();
		attributeProjection = new HashSet<>();

		Map<String, List<String>> params = paramMap.entries().parallelStream().collect(
				Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
		for (Map.Entry<String, List<String>> entry : params.entrySet()) {
			String key = entry.getKey();
			List<String> value = entry.getValue();

			if (key.equals("vocabularyName")) {
				putURLEQListOfStringQuery(mongoQueryElements, "type", value);
				continue;
			}

			if (key.equals("includeAttributes")) {
				includeAttributes = Boolean.parseBoolean(value.get(0));
				continue;
			}

			if (key.equals("includeChildren")) {
				includeChildren = Boolean.parseBoolean(value.get(0));
				continue;
			}

			if (key.equals("attributeNames")) {
				putListOfStringAttributeProjection(value);
				continue;
			}

			if (key.equals("EQ_name")) {
				elementNameCandidate.addAll(value);
				continue;
			}

			if (key.equals("WD_name")) {
				List<String> wdValues = new ArrayList<String>();
				for (String str : value) {
					wdValues.add(str);

					ObservableSubscriber<org.bson.Document> wdSubscriber = new ObservableSubscriber<org.bson.Document>();
					SOAPQueryServer.mVocCollection.find(new org.bson.Document("id", str)).subscribe(wdSubscriber);

					try {
						wdSubscriber.await();
					} catch (Throwable e) {
						ImplementationException e1 = new ImplementationException();
						e1.setStackTrace(new StackTraceElement[0]);
						e1.setReason(e.getMessage());
						throw e1;
					}

					List<org.bson.Document> rDocs = wdSubscriber.getReceived();

					for (org.bson.Document rDoc : rDocs) {
						if (rDoc.containsKey("children")) {
							List<String> children = rDoc.getList("children", String.class);
							wdValues.addAll(children);
						}
					}
				}
				elementNameCandidate.addAll(wdValues);
				continue;
			}

			if (key.equals("HASATTR")) {
				putURLExistsListOfStringQuery(mongoQueryElements, value);
				continue;
			}

			if (key.startsWith("EQATTR_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(7));
				putURLEQListOfStringQuery(mongoQueryElements, "attributes." + subField, value);
				continue;
			}

			if (key.equals("maxElementCount")) {
				maxCount = Integer.parseInt(value.get(0));
			}
		}

		if (includeAttributes == null)
			includeAttributes = true;
		if (includeChildren == null)
			includeChildren = true;

		if (includeAttributes && attributeProjection.size() != 0) {
			// project has only true values
			mongoProjection.put("id", true);
			mongoProjection.put("type", true);
			if (includeChildren)
				mongoProjection.put("children", true);
			for (String s : attributeProjection) {
				mongoProjection.put("attributes." + BSONReadUtil.encodeMongoObjectKey(s), true);
			}
		} else {
			if (!includeAttributes)
				mongoProjection.put("attributes", false);
			if (!includeChildren)
				mongoProjection.put("children", false);
		}

		mongoQuery = new Document();

		if (elementNameCandidate != null && !elementNameCandidate.isEmpty()) {
			mongoQueryElements
					.add(new Document("id", new Document("$in", new ArrayList<String>(elementNameCandidate))));
		}

		if (mongoQueryElements.size() != 0)
			mongoQuery.put("$and", mongoQueryElements);
	}

	private void makeSimpleEventQuery(MultiMap paramMap) throws QueryParameterException, ImplementationException {
		List<Document> mongoQueryElements = new ArrayList<Document>();
		mongoProjection = new Document();
		mongoSort = new Document();
		readPointCandidate = new HashSet<>();
		bizLocationCandidate = new HashSet<>();
		String orderBy = null;
		int orderDirection = -1;

		Map<String, List<String>> params = paramMap.entries().parallelStream().collect(
				Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toList())));

		for (Map.Entry<String, List<String>> entry : params.entrySet()) {
			String key = entry.getKey();
			List<String> value = entry.getValue();

			if (key.equals("perPage")) {
				perPage.set(getURLIntegerQueryValue(key, value));
				continue;
			}

			if (key.equals("orderBy")) {
				orderBy = value.get(0);
				if (!orderBy.equals("eventTime") && !orderBy.equals("recordTime"))
					orderBy = "extension." + BSONReadUtil.encodeMongoObjectKey(orderBy);
				continue;
			}
			if (key.equals("orderDirection")) {
				String ret = value.get(0);
				if (ret.equals("ASC"))
					orderDirection = 1;
				else if (ret.equals("DESC")) {

				} else {
					throw getQueryParameterException("orderDirection");
				}
				continue;
			}

			if (key.equals("eventCountLimit")) {
				eventCountLimit = getURLIntegerQueryValue(key, value);
				continue;
			}

			if (key.equals("maxEventCount")) {
				maxCount = getURLIntegerQueryValue(key, value);
				continue;
			}

			if (key.equals("eventType")) {
				putURLEQListOfStringQuery(mongoQueryElements, "type", value);
				continue;
			}

			if (key.equals("GE_eventTime")) {
				putURLDateQuery(mongoQueryElements, "eventTime", "$gte", value);
				continue;
			}

			if (key.equals("LT_eventTime")) {
				putURLDateQuery(mongoQueryElements, "eventTime", "$lt", value);
				continue;
			}

			if (key.equals("GE_recordTime")) {
				putURLDateQuery(mongoQueryElements, "recordTime", "$gte", value);
				continue;
			}

			if (key.equals("LT_recordTime")) {
				putURLDateQuery(mongoQueryElements, "recordTime", "$lt", value);
				continue;
			}

			if (key.equals("GE_errorDeclarationTime")) {
				putURLDateQuery(mongoQueryElements, "errorDeclaration.declarationTime", "$gte", value);
				continue;
			}

			if (key.equals("LT_errorDeclarationTime")) {
				putURLDateQuery(mongoQueryElements, "errorDeclaration.declarationTime", "$lt", value);
				continue;
			}

			if (key.equals("GE_time")) {
				putURLDateQuery(mongoQueryElements, "sensorElementList.sensorMetadata.time", "$gte", value);
				continue;
			}
			if (key.equals("LT_time")) {
				putURLDateQuery(mongoQueryElements, "sensorElementList.sensorMetadata.time", "$lt", value);
				continue;
			}
			if (key.equals("GE_startTime")) {
				putURLDateQuery(mongoQueryElements, "sensorElementList.sensorMetadata.startTime", "$gte", value);
				continue;
			}
			if (key.equals("LT_startTime")) {
				putURLDateQuery(mongoQueryElements, "sensorElementList.sensorMetadata.startTime", "$lt", value);
				continue;
			}
			if (key.equals("GE_endTime")) {
				putURLDateQuery(mongoQueryElements, "sensorElementList.sensorMetadata.endTime", "$gte", value);
				continue;
			}
			if (key.equals("LT_endTime")) {
				putURLDateQuery(mongoQueryElements, "sensorElementList.sensorMetadata.endTime", "$lt", value);
				continue;
			}

			if (key.equals("EQ_action")) {
				putURLEQListOfStringQuery(mongoQueryElements, "action", value);
				continue;
			}

			if (key.equals("EQ_bizStep")) {
				putURLEQListOfStringQuery(mongoQueryElements, "bizStep", value);
				continue;
			}

			if (key.equals("EQ_disposition")) {
				putURLEQListOfStringQuery(mongoQueryElements, "disposition", value);
				continue;
			}

			if (key.equals("EQ_setPersistentDisposition")) {
				putURLEQListOfStringQuery(mongoQueryElements, "persistentDisposition.set", value);
				continue;
			}
			if (key.equals("EQ_unsetPersistentDisposition")) {
				putURLEQListOfStringQuery(mongoQueryElements, "persistentDisposition.unset", value);
				continue;
			}

			if (key.equals("EQ_readPoint")) {
				readPointCandidate.addAll(value);
				continue;
			}

			if (key.equals("WD_readPoint")) {
				HashSet<String> wdValues = new HashSet<>();
				for (String v : value) {
					wdValues.add(v);

					ObservableSubscriber<org.bson.Document> wdSubscriber = new ObservableSubscriber<org.bson.Document>();
					SOAPQueryServer.mVocCollection.find(new org.bson.Document("id", value)).subscribe(wdSubscriber);

					try {
						wdSubscriber.await();
					} catch (Throwable e) {
						ImplementationException e1 = new ImplementationException();
						e1.setStackTrace(new StackTraceElement[0]);
						e1.setReason(e.getMessage());
						throw e1;
					}

					List<org.bson.Document> rDocs = wdSubscriber.getReceived();

					for (org.bson.Document rDoc : rDocs) {
						if (rDoc.containsKey("children")) {
							List<String> children = rDoc.getList("children", String.class);
							wdValues.addAll(children);
						}
					}
				}
				readPointCandidate.addAll(wdValues);
				continue;
			}

			if (key.equals("EQ_bizLocation")) {
				bizLocationCandidate.addAll(value);
				continue;
			}

			if (key.equals("WD_bizLocation")) {
				HashSet<String> wdValues = new HashSet<>();
				for (String v : value) {
					wdValues.add(v);

					ObservableSubscriber<org.bson.Document> wdSubscriber = new ObservableSubscriber<org.bson.Document>();
					SOAPQueryServer.mVocCollection.find(new org.bson.Document("id", value)).subscribe(wdSubscriber);

					try {
						wdSubscriber.await();
					} catch (Throwable e) {
						ImplementationException e1 = new ImplementationException();
						e1.setStackTrace(new StackTraceElement[0]);
						e1.setReason(e.getMessage());
						throw e1;
					}

					List<org.bson.Document> rDocs = wdSubscriber.getReceived();

					for (org.bson.Document rDoc : rDocs) {
						if (rDoc.containsKey("children")) {
							List<String> children = rDoc.getList("children", String.class);
							wdValues.addAll(children);
						}
					}
				}
				bizLocationCandidate.addAll(wdValues);
				continue;
			}

			if (key.equals("EQ_transformationID")) {
				putURLEQListOfStringQuery(mongoQueryElements, "transformationID", value);
				continue;
			}

			if (key.equals("EQ_type")) {
				putURLEQListOfStringQuery(mongoQueryElements, "sensorElementList.sensorReport.type", value);
				continue;
			}

			if (key.equals("EQ_deviceID")) {
				putURLEQListOfStringQuery(mongoQueryElements, "sensorElementList.sensorReport.deviceID", value);
				continue;
			}

			if (key.equals("EQ_deviceMetaData")) {
				putURLEQListOfStringQuery(mongoQueryElements, "sensorElementList.sensorReport.deviceMetaData", value);
				continue;
			}

			if (key.equals("EQ_rawData")) {
				putURLEQListOfStringQuery(mongoQueryElements, "sensorElementList.sensorReport.rawData", value);
				continue;
			}

			if (key.equals("EQ_dataProcessingMethod")) {
				putURLEQListOfStringQuery(mongoQueryElements, "sensorElementList.sensorReport.dataProcessingMethod",
						value);
				continue;
			}

			if (key.equals("EQ_microorganism")) {
				putURLEQListOfStringQuery(mongoQueryElements, "sensorElementList.sensorReport.microorganism", value);
				continue;
			}

			if (key.equals("EQ_chemicalSubstance")) {
				putURLEQListOfStringQuery(mongoQueryElements, "sensorElementList.sensorReport.chemicalSubstance",
						value);
				continue;
			}

			if (key.equals("EQ_bizRules")) {
				putURLEQListOfStringQuery(mongoQueryElements, "sensorElementList.sensorMetadata.bizRules", value);
				continue;
			}

			if (key.equals("EQ_stringValue")) {
				putURLEQListOfStringQuery(mongoQueryElements, "sensorElementList.sensorReport.stringValue", value);
				continue;
			}
			if (key.equals("EQ_booleanValue")) {
				putURLBooleanQueryValue(mongoQueryElements, "sensorElementList.sensorReport.booleanValue", value);
				continue;
			}
			if (key.equals("EQ_hexBinaryValue")) {
				putURLEQListOfStringQuery(mongoQueryElements, "sensorElementList.sensorReport.hexBinaryValue", value);
				continue;
			}
			if (key.equals("LT_maxValue")) {
				putURLDoubleCompQuery(mongoQueryElements, "sensorElementList.sensorReport.maxValue", "$lt", value);
				continue;
			}
			if (key.equals("LT_minValue")) {
				putURLDoubleCompQuery(mongoQueryElements, "sensorElementList.sensorReport.minValue", "$lt", value);
				continue;
			}
			if (key.equals("LT_meanValue")) {
				putURLDoubleCompQuery(mongoQueryElements, "sensorElementList.sensorReport.meanValue", "$lt", value);
				continue;
			}
			if (key.equals("GE_maxValue")) {
				putURLDoubleCompQuery(mongoQueryElements, "sensorElementList.sensorReport.maxValue", "$gte", value);
				continue;
			}
			if (key.equals("GE_minValue")) {
				putURLDoubleCompQuery(mongoQueryElements, "sensorElementList.sensorReport.minValue", "$gte", value);
				continue;
			}
			if (key.equals("GE_meanValue")) {
				putURLDoubleCompQuery(mongoQueryElements, "sensorElementList.sensorReport.meanValue", "$gte", value);
				continue;
			}

			if (key.equals("GE_sDev")) {
				putURLDoubleCompQuery(mongoQueryElements, "sensorElementList.sensorReport.sDev", "$gte", value);
				continue;
			}
			if (key.equals("LT_sDev")) {
				putURLDoubleCompQuery(mongoQueryElements, "sensorElementList.sensorReport.sDev", "$lt", value);
				continue;
			}
			if (key.equals("GE_percValue")) {
				putURLDoubleCompQuery(mongoQueryElements, "sensorElementList.sensorReport.percValue", "$gte", value);
				continue;
			}
			if (key.equals("LT_percValue")) {
				putURLDoubleCompQuery(mongoQueryElements, "sensorElementList.sensorReport.percValue", "$lt", value);
				continue;
			}
			if (key.equals("GE_percRank")) {
				putURLDoubleCompQuery(mongoQueryElements, "sensorElementList.sensorReport.percRank", "$gte", value);
				continue;
			}
			if (key.equals("LT_percRank")) {
				putURLDoubleCompQuery(mongoQueryElements, "sensorElementList.sensorReport.percValue", "$lt", value);
				continue;
			}

			if (key.equals("MATCH_epc")) {
				putURLMATCHListOfStringQuery(mongoQueryElements, "epcList", value);
				continue;
			}

			if (key.equals("MATCH_parentID")) {
				putURLMATCHListOfStringQuery(mongoQueryElements, "parentID", value);
				continue;
			}

			if (key.equals("MATCH_inputEPC")) {
				putURLMATCHListOfStringQuery(mongoQueryElements, "inputEPCList", value);
				continue;
			}

			if (key.equals("MATCH_outputEPC")) {
				putURLMATCHListOfStringQuery(mongoQueryElements, "outputEPCList", value);
				continue;
			}

			if (key.equals("MATCH_anyEPC")) {
				putURLMultiMATCHListOfStringQuery(mongoQueryElements,
						new String[] { "epcList", "inputEPCList", "outputEPCList", "parentID" }, value);
				continue;
			}

			if (key.equals("MATCH_epcClass")) {
				putURLMATCHListOfStringQuery(mongoQueryElements, "quantityList.epcClass", value);
				continue;
			}

			if (key.equals("MATCH_inputEPCClass")) {
				putURLMATCHListOfStringQuery(mongoQueryElements, "inputQuantityList.epcClass", value);
				continue;
			}

			if (key.equals("MATCH_outputEPCClass")) {
				putURLMATCHListOfStringQuery(mongoQueryElements, "outputQuantityList.epcClass", value);
				continue;
			}

			if (key.equals("MATCH_anyEPCClass")) {
				putURLMultiMATCHListOfStringQuery(mongoQueryElements, new String[] { "quantityList.epcClass",
						"inputQuantityList.epcClass", "outputQuantityList.epcClass" }, value);
				continue;
			}

			if (key.equals("EQ_quantity")) {
				putURLIntQuery(mongoQueryElements, "quantity", "$eq", value);
				continue;
			}

			if (key.equals("GT_quantity")) {
				putURLIntQuery(mongoQueryElements, "quantity", "$gt", value);
				continue;
			}

			if (key.equals("GE_quantity")) {
				putURLIntQuery(mongoQueryElements, "quantity", "$gte", value);
				continue;
			}

			if (key.equals("LT_quantity")) {
				putURLIntQuery(mongoQueryElements, "quantity", "$lt", value);
				continue;
			}

			if (key.equals("LE_quantity")) {
				putURLIntQuery(mongoQueryElements, "quantity", "$lte", value);
				continue;
			}

			if (key.equals("EQ_eventID")) {
				putURLEQListOfStringQuery(mongoQueryElements, "eventID", value);
				continue;
			}

			if (key.equals("EQ_errorReason")) {
				putURLEQListOfStringQuery(mongoQueryElements, "errorDeclaration.reason", value);
				continue;
			}

			if (key.equals("EQ_correctiveEventID")) {
				putURLEQListOfStringQuery(mongoQueryElements, "errorDeclaration.correctiveEventIDs", value);
				continue;
			}

			if (key.equals("EXISTS_errorDeclaration")) {
				putURLExistsQuery(mongoQueryElements, "errorDeclaration");
				continue;
			}

			if (key.equals("EXISTS_sensorElement")) {
				putURLExistsQuery(mongoQueryElements, "sensorElementList");
				continue;
			}

			if (key.startsWith("EQ_bizTransaction_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(18));
				putURLEQListOfStringQuery(mongoQueryElements, "bizTransactionList." + subField, value);
				continue;
			}

			if (key.startsWith("EQ_source_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(10));
				putURLEQListOfStringQuery(mongoQueryElements, "sourceList." + subField, value);
				continue;
			}

			if (key.startsWith("EQ_destination_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(15));
				putURLEQListOfStringQuery(mongoQueryElements, "destinationList." + subField, value);
				continue;
			}

			if (key.startsWith("EQ_quantity_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(12));
				putURLQuantityCompQuery(mongoQueryElements, "quantityList", "$eq", subField, value);
				continue;
			}

			if (key.startsWith("GT_quantity_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(12));
				putURLQuantityCompQuery(mongoQueryElements, "quantityList", "$gt", subField, value);
				continue;
			}

			if (key.startsWith("GE_quantity_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(12));
				putURLQuantityCompQuery(mongoQueryElements, "quantityList", "$gte", subField, value);
				continue;
			}

			if (key.startsWith("LT_quantity_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(12));
				putURLQuantityCompQuery(mongoQueryElements, "quantityList", "$lt", subField, value);
				continue;
			}

			if (key.startsWith("LE_quantity_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(12));
				putURLQuantityCompQuery(mongoQueryElements, "quantityList", "$lte", subField, value);
				continue;
			}

			if (key.startsWith("EQ_ILMD_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(8));
				putURLEQQuery(mongoQueryElements, "ilmd." + subField, value);
				continue;
			}

			if (key.startsWith("EQ_INNER_ERROR_DECLARATION_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(27));
				putURLEQQuery(mongoQueryElements, "errf." + subField, value);
				continue;
			}

			if (key.startsWith("GT_INNER_ERROR_DECLARATION_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(27));
				putURLDoubleCompQuery(mongoQueryElements, "errf." + subField, "$gt", value);
				continue;
			}
			if (key.startsWith("GE_INNER_ERROR_DECLARATION_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(27));
				putURLDoubleCompQuery(mongoQueryElements, "errf." + subField, "$gte", value);
				continue;
			}
			if (key.startsWith("LT_INNER_ERROR_DECLARATION_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(27));
				putURLDoubleCompQuery(mongoQueryElements, "errf." + subField, "$lt", value);
				continue;
			}
			if (key.startsWith("LE_INNER_ERROR_DECLARATION_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(27));
				putURLDoubleCompQuery(mongoQueryElements, "errf." + subField, "$lte", value);
				continue;
			}

			if (key.startsWith("EQ_INNER_ILMD_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(14));
				putURLEQQuery(mongoQueryElements, "ilmdf." + subField, value);
				continue;
			}

			if (key.startsWith("GT_INNER_ILMD_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(14));
				putURLDoubleCompQuery(mongoQueryElements, "ilmdf." + subField, "$gt", value);
				continue;
			}
			if (key.startsWith("GE_INNER_ILMD_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(14));
				putURLDoubleCompQuery(mongoQueryElements, "ilmdf." + subField, "$gte", value);
				continue;
			}
			if (key.startsWith("LT_INNER_ILMD_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(14));
				putURLDoubleCompQuery(mongoQueryElements, "ilmdf." + subField, "$lt", value);
				continue;
			}
			if (key.startsWith("LE_INNER_ILMD_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(14));
				putURLDoubleCompQuery(mongoQueryElements, "ilmdf." + subField, "$lte", value);
				continue;
			}

			if (key.startsWith("GT_ILMD_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(8));
				putURLDoubleCompQuery(mongoQueryElements, "ilmd." + subField, "$gt", value);
				continue;
			}
			if (key.startsWith("GE_ILMD_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(8));
				putURLDoubleCompQuery(mongoQueryElements, "ilmd." + subField, "$gte", value);
				continue;
			}
			if (key.startsWith("LT_ILMD_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(8));
				putURLDoubleCompQuery(mongoQueryElements, "ilmd." + subField, "$lt", value);
				continue;
			}
			if (key.startsWith("LE_ILMD_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(8));
				putURLDoubleCompQuery(mongoQueryElements, "ilmd." + subField, "$lte", value);
				continue;
			}

			if (key.startsWith("EXISTS_ILMD_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(12));
				putURLExistsQuery(mongoQueryElements, "ilmd." + subField);
				continue;
			}

			if (key.startsWith("EXISTS_INNER_ILMD_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(18));
				putURLExistsQuery(mongoQueryElements, "ilmdf." + subField);
				continue;
			}

			if (key.startsWith("EQ_ERROR_DECLARATION_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(21));
				putURLEQQuery(mongoQueryElements, "errorDeclaration.extension." + subField, value);
				continue;
			}

			if (key.startsWith("GT_ERROR_DECLARATION_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(21));
				putURLDoubleCompQuery(mongoQueryElements, "errorDeclaration.extension." + subField, "$gt", value);
				continue;
			}
			if (key.startsWith("GE_ERROR_DECLARATION_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(21));
				putURLDoubleCompQuery(mongoQueryElements, "errorDeclaration.extension." + subField, "$gte", value);
				continue;
			}
			if (key.startsWith("LT_ERROR_DECLARATION_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(21));
				putURLDoubleCompQuery(mongoQueryElements, "errorDeclaration.extension." + subField, "$lt", value);
				continue;
			}
			if (key.startsWith("LE_ERROR_DECLARATION_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(21));
				putURLDoubleCompQuery(mongoQueryElements, "errorDeclaration.extension." + subField, "$lte", value);
				continue;
			}

			if (key.startsWith("EXISTS_ERROR_DECLARATION_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(25));
				putURLExistsQuery(mongoQueryElements, "errorDeclaration.extension." + subField);
				continue;
			}

			if (key.startsWith("EXISTS_INNER_ERROR_DECLARATION_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(31));
				putURLExistsQuery(mongoQueryElements, "errf." + subField);
				continue;
			}

			if (key.startsWith("EQ_INNER_SENSORELEMENT_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(23));
				putURLEQQuery(mongoQueryElements, "sensorElementList.sef." + subField, value);
				continue;
			}
			if (key.startsWith("GT_INNER_SENSORELEMENT_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(23));
				putURLDoubleCompQuery(mongoQueryElements, "sensorElementList.sef." + subField, "$gt", value);
				continue;
			}
			if (key.startsWith("GE_INNER_SENSORELEMENT_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(23));
				putURLDoubleCompQuery(mongoQueryElements, "sensorElementList.sef." + subField, "$gte", value);
				continue;
			}
			if (key.startsWith("LT_INNER_SENSORELEMENT_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(23));
				putURLDoubleCompQuery(mongoQueryElements, "sensorElementList.sef." + subField, "$lt", value);
				continue;
			}
			if (key.startsWith("LE_INNER_SENSORELEMENT_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(23));
				putURLDoubleCompQuery(mongoQueryElements, "sensorElementList.sef." + subField, "$lte", value);
				continue;
			}
			if (key.startsWith("EXISTS_INNER_SENSORELEMENT_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(27));
				putURLExistsQuery(mongoQueryElements, "sensorElementList.sef." + subField);
				continue;
			}

			if (key.startsWith("EQ_SENSORMETADATA_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(18));
				putURLEQQuery(mongoQueryElements, "sensorElementList.sensorMetadata.otherAttributes." + subField,
						value);
				continue;
			}
			if (key.startsWith("EQ_SENSORREPORT_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(16));
				putURLEQQuery(mongoQueryElements, "sensorElementList.sensorReport.otherAttributes." + subField, value);
				continue;
			}
			if (key.startsWith("EXISTS_SENSORMETADATA_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(22));
				putURLExistsQuery(mongoQueryElements, "sensorElementList.sensorMetadata.otherAttributes." + subField);
				continue;
			}
			if (key.startsWith("EXISTS_SENSORREPORT_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(20));
				putURLExistsQuery(mongoQueryElements, "sensorElementList.sensorReport.otherAttributes." + subField);
				continue;
			}

			if (key.startsWith("EQ_SENSORELEMENT_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(17));
				putURLEQQuery(mongoQueryElements, "sensorElementList.extension." + subField, value);
				continue;
			}
			if (key.startsWith("GT_SENSORELEMENT_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(17));
				putURLDoubleCompQuery(mongoQueryElements, "sensorElementList.extension." + subField, "$gt", value);
				continue;
			}
			if (key.startsWith("GE_SENSORELEMENT_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(17));
				putURLDoubleCompQuery(mongoQueryElements, "sensorElementList.extension." + subField, "$gte", value);
				continue;
			}
			if (key.startsWith("LT_SENSORELEMENT_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(17));
				putURLDoubleCompQuery(mongoQueryElements, "sensorElementList.extension." + subField, "$lt", value);
				continue;
			}
			if (key.startsWith("LE_SENSORELEMENT_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(17));
				putURLDoubleCompQuery(mongoQueryElements, "sensorElementList.extension." + subField, "$lte", value);
				continue;
			}
			if (key.startsWith("EXISTS_SENSORELEMENT_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(21));
				putURLExistsQuery(mongoQueryElements, "sensorElementList.extension." + subField);
				continue;
			}

			if (key.startsWith("EQ_INNER_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(9));
				putURLEQQuery(mongoQueryElements, "extf." + subField, value);
				continue;
			}

			if (key.startsWith("GT_INNER_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(9));
				putURLDoubleCompQuery(mongoQueryElements, "extf." + subField, "$gt", value);
				continue;
			}
			if (key.startsWith("GE_INNER_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(9));
				putURLDoubleCompQuery(mongoQueryElements, "extf." + subField, "$gte", value);
				continue;
			}
			if (key.startsWith("LT_INNER_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(9));
				putURLDoubleCompQuery(mongoQueryElements, "extf." + subField, "$lt", value);
				continue;
			}
			if (key.startsWith("LE_INNER_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(9));
				putURLDoubleCompQuery(mongoQueryElements, "extf." + subField, "$lte", value);
				continue;
			}

			if (key.startsWith("EQ_value_")) {
				String subField = key.substring(9);
				Double dVal = getURLDoubleQueryValue(key, value);
				Double rValue = SOAPQueryServer.unitConverter.getRepresentativeValue(subField, dVal);
				String rType = SOAPQueryServer.unitConverter.getRepresentativeType(subField);
				if (rValue == null || rType == null) {
					throw getQueryParameterException(key);
				}
				putURLEQQuery(mongoQueryElements, "sensorElementList.sensorReport.rValue", rValue);
				putURLEQQuery(mongoQueryElements, "sensorElementList.sensorReport.rType", rType);
				continue;
			}
			if (key.startsWith("LT_value_")) {
				String subField = key.substring(9);
				Double dVal = getURLDoubleQueryValue(key, value);
				Double rValue = SOAPQueryServer.unitConverter.getRepresentativeValue(subField, dVal);
				String rType = SOAPQueryServer.unitConverter.getRepresentativeType(subField);
				if (rValue == null || rType == null) {
					throw getQueryParameterException(key);
				}
				putURLDoubleCompQuery(mongoQueryElements, "sensorElementList.sensorReport.value", "$lt", rValue);
				putURLEQQuery(mongoQueryElements, "sensorElementList.sensorReport.uom", rType);
				continue;
			}
			if (key.startsWith("GE_value_")) {
				String subField = key.substring(9);
				Double dVal = getURLDoubleQueryValue(key, value);
				Double rValue = SOAPQueryServer.unitConverter.getRepresentativeValue(subField, dVal);
				String rType = SOAPQueryServer.unitConverter.getRepresentativeType(subField);
				if (rValue == null || rType == null) {
					throw getQueryParameterException(key);
				}
				putURLDoubleCompQuery(mongoQueryElements, "sensorElementList.sensorReport.value", "$gte", rValue);
				putURLEQQuery(mongoQueryElements, "sensorElementList.sensorReport.uom", rType);
				continue;
			}

			if (key.startsWith("EQ_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(3));
				putURLEQQuery(mongoQueryElements, "extension." + subField, value);
				continue;
			}

			if (key.startsWith("GT_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(3));
				putURLDoubleCompQuery(mongoQueryElements, "extension." + subField, "$gt", value);
				continue;
			}
			if (key.startsWith("GE_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(3));
				putURLDoubleCompQuery(mongoQueryElements, "extension." + subField, "$gte", value);
				continue;
			}
			if (key.startsWith("LT_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(3));
				putURLDoubleCompQuery(mongoQueryElements, "extension." + subField, "$lt", value);
				continue;
			}
			if (key.startsWith("LE_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(3));
				putURLDoubleCompQuery(mongoQueryElements, "extension." + subField, "$lte", value);
				continue;
			}

			if (key.startsWith("EXISTS_INNER_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(13));
				putURLExistsQuery(mongoQueryElements, "extf." + subField);
				continue;
			}

			if (key.startsWith("EXISTS_")) {
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(7));
				putURLExistsQuery(mongoQueryElements, "extension." + subField);
				continue;
			}

			if (key.startsWith("HASATTR_")) {
				// Check value is available in MasterData
				String subField = BSONReadUtil.encodeMongoObjectKey(key.substring(8));

				if (value.isEmpty()) {
					throw getQueryParameterException(key);
				}

				for (String valueStr : value) {

					ObservableSubscriber<org.bson.Document> wdSubscriber = new ObservableSubscriber<org.bson.Document>();
					SOAPQueryServer.mVocCollection.find(new org.bson.Document("id", valueStr)).subscribe(wdSubscriber);

					try {
						wdSubscriber.await();
					} catch (Throwable e) {
						ImplementationException e1 = new ImplementationException();
						e1.setStackTrace(new StackTraceElement[0]);
						e1.setReason(e.getMessage());
						throw e1;
					}

					List<org.bson.Document> rDocs = wdSubscriber.getReceived();

					if (rDocs.isEmpty())
						throw getQueryParameterException(key);

				}

				if (subField.contains("#")) {
					subField = "extension." + subField;
				}
				mongoQueryElements.add(new Document(subField, new Document("$in", value)));
				continue;
			}

			if (key.startsWith("EQATTR_")) {
				String[] nameArr = key.split("_");
				if (nameArr.length < 3) {
					throw getQueryParameterException(key);
				}
				String fieldname = nameArr[1];
				String attrname = nameArr[2];

				// Step1
				List<String> ids = new ArrayList<String>();

				ObservableSubscriber<org.bson.Document> subscriber = new ObservableSubscriber<org.bson.Document>();
				SOAPQueryServer.mVocCollection
						.find(new org.bson.Document().append("attributes." + BSONReadUtil.encodeMongoObjectKey(attrname),
								new org.bson.Document().append("$in", value)))
						.subscribe(subscriber);

				try {
					subscriber.await();
				} catch (Throwable e) {
					ImplementationException e1 = new ImplementationException();
					e1.setStackTrace(new StackTraceElement[0]);
					e1.setReason(e.getMessage());
					throw e1;
				}

				List<org.bson.Document> mdDocs = subscriber.getReceived();

				for (org.bson.Document mdDoc : mdDocs) {
					ids.add(mdDoc.getString("id"));
				}

				// Step2
				if (fieldname.contains("#")) {
					fieldname = "extension." + BSONReadUtil.encodeMongoObjectKey(fieldname);
				}
				mongoQueryElements.add(new Document(fieldname, new Document("$in", ids)));
			}
		}

		mongoQuery = new Document();
		if (mongoQueryElements.size() != 0)
			mongoQuery.put("$and", mongoQueryElements);
		if (orderBy != null) {
			mongoSort.put(orderBy, orderDirection);
		}
	}

	// URL as parameter
	private void putURLEQListOfStringQuery(List<Document> mongoQueryElements, String key, List<String> value) {
		if (value.size() == 1) {
			mongoQueryElements.add(new Document(key, value.get(0)));
		} else {
			mongoQueryElements.add(new Document(key, new Document("$in", value)));
		}
	}

	private Integer getURLIntegerQueryValue(String key, List<String> values) throws QueryParameterException {
		try {
			return Integer.parseInt(values.get(0));
		} catch (Exception e1) {
			throw getQueryParameterException(key);
		}
	}

	private Double getURLDoubleQueryValue(String key, List<String> values) throws QueryParameterException {
		try {
			return Double.parseDouble(values.get(0));
		} catch (Exception e1) {
			throw getQueryParameterException(key);
		}
	}

	private void putURLBooleanQueryValue(List<Document> mongoQueryElements, String key, List<String> values)
			throws QueryParameterException {
		try {
			mongoQueryElements.add(new Document(key, Boolean.parseBoolean(values.get(0))));
		} catch (Exception e1) {
			throw getQueryParameterException(key);
		}
	}

	private void putURLDateQuery(List<Document> mongoQueryElements, String key, String comparator, List<String> value)
			throws QueryParameterException {
		try {
			Long time = BSONReadUtil.getBsonDateTime(value.get(0));
			mongoQueryElements.add(new Document(key, new Document(comparator, time)));
		} catch (Exception e1) {
			throw getQueryParameterException(key);
		}
	}

	private void putURLDoubleCompQuery(List<Document> mongoQueryElements, String field, String comparator,
			List<String> valueList) throws QueryParameterException {
		try {
			Double dVal = Double.parseDouble(valueList.get(0));
			mongoQueryElements.add(new Document(field, new Document(comparator, dVal)));
		} catch (Exception e1) {
			throw getQueryParameterException(field);
		}
	}

	private void putURLQuantityCompQuery(List<Document> mongoQueryElements, String field, String comparator, String uom,
			List<String> valueList) throws QueryParameterException {
		try {
			Double dVal = Double.parseDouble(valueList.get(0));

			List<Document> arr = new ArrayList<Document>();
			arr.add(new Document(field + ".uom", uom));
			arr.add(new Document(field + ".quantity", new Document(comparator, dVal)));

			mongoQueryElements.add(new Document("$and", arr));
		} catch (Exception e1) {
			throw getQueryParameterException(field);
		}
	}

	private void putURLDoubleCompQuery(List<Document> mongoQueryElements, String field, String comparator, Double value)
			throws QueryParameterException {
		try {
			mongoQueryElements.add(new Document(field, new Document(comparator, value)));
		} catch (Exception e1) {
			throw getQueryParameterException(field);
		}
	}

	private void putURLMATCHListOfStringQuery(List<Document> mongoQueryElements, String field, List<String> valueList)
			throws QueryParameterException {
		try {
			List<Document> values = new ArrayList<Document>();
			for (String v : valueList) {
				if (v.contains("*")) {
					v = v.replaceAll("\\.", "[.]");
					v = v.replaceAll("\\*", "(.)*");
					values.add(new Document(field, new Document("$regex", v)));
				} else {
					values.add(new Document(field, v));
				}
			}
			if (values.isEmpty()) {
				throw getQueryParameterException(field);
			}
			mongoQueryElements.add(new Document("$or", values));
		} catch (Exception e1) {
			throw getQueryParameterException(field);
		}
	}

	private void putURLMultiMATCHListOfStringQuery(List<Document> mongoQueryElements, String[] fields,
			List<String> valueList) throws QueryParameterException {
		try {
			List<Document> values = new ArrayList<Document>();
			for (String field : fields) {
				for (String v : valueList) {
					if (v.contains("*")) {
						v = v.replaceAll("\\.", "[.]");
						v = v.replaceAll("\\*", "(.)*");
						values.add(new Document(field, new Document("$regex", v)));
					} else {
						values.add(new Document(field, v));
					}
				}
				if (values.isEmpty()) {
					throw getQueryParameterException(field);
				}
			}
			if (values.isEmpty()) {
				throw getQueryParameterException(fields.toString());
			}
			mongoQueryElements.add(new Document("$or", values));
		} catch (Exception e1) {
			throw getQueryParameterException(fields.toString());
		}
	}

	private void putURLIntQuery(List<Document> mongoQueryElements, String field, String comparator,
			List<String> valueList) throws QueryParameterException {
		try {
			int v = Integer.parseInt(valueList.get(0));
			mongoQueryElements.add(new Document(field, new Document(comparator, v)));
		} catch (Exception e1) {
			throw getQueryParameterException(field);
		}
	}

	private void putURLExistsQuery(List<Document> mongoQueryElements, String field) {
		mongoQueryElements.add(new Document(field, new Document("$exists", true)));
	}

	private void putURLExistsListOfStringQuery(List<Document> mongoQueryElements, List<String> fields) {
		for (String field : fields)
			mongoQueryElements.add(new Document(field, new Document("$exists", true)));
	}

	private void putURLEQQuery(List<Document> mongoQueryElements, String field, List<String> value) {
		mongoQueryElements.add(new Document(field, value.get(0)));
	}

	private void putURLEQQuery(List<Document> mongoQueryElements, String field, Object value) {
		mongoQueryElements.add(new Document(field, value));
	}
}
