package org.oliot.epcis.query;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.bson.Document;
import org.oliot.epcis.model.ArrayOfString;
import org.oliot.epcis.model.ImplementationException;
import org.oliot.epcis.model.Poll;
import org.oliot.epcis.model.QueryParam;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.model.Subscribe;
import org.oliot.epcis.model.SubscribeNotPermittedException;
import org.oliot.epcis.model.VoidHolder;
import org.oliot.epcis.query.converter.QueryConverter;
import org.oliot.epcis.resource.StaticResource;
import org.oliot.epcis.util.BSONReadUtil;
import org.oliot.epcis.util.TimeUtil;
import org.w3c.dom.Element;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Copyright (C) 2020-2023. (Jaewook Byun) all rights reserved.
 * <p>
 * This project is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * QueryDescription abstract query parameters.
 * <p>
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr, Associate Director, Auto-ID Labs, Korea,
 *         bjw0829@gmail.com
 */
public class QueryDescription {

	private SOAPQueryUnmarshaller unmarshaller;

	private String queryName;
	private Document mongoQuery;
	private Document mongoProjection;
	private Document mongoSort;
	private Integer eventCountLimit;
	private Integer maxCount;

	private HashSet<String> attributeProjection;
	private Boolean includeAttributes;
	private Boolean includeChildren;

	// for pagination
	private AtomicInteger perPage = new AtomicInteger(30);
	private AtomicInteger skip = new AtomicInteger(0);

	// for sort
	private String orderBy = null;
	private int orderDirection = -1;

	public QueryDescription(Document doc) {
		queryName = doc.getString("queryName");
		mongoQuery = doc.get("query", Document.class);
		mongoProjection = doc.get("projection", Document.class);
		mongoSort = doc.get("sort", Document.class);
		eventCountLimit = doc.getInteger("eventCountLimit");
		maxCount = doc.getInteger("maxCount");
	}

	public QueryDescription(Subscribe subscribe, SOAPQueryUnmarshaller unmarshaller)
			throws QueryParameterException, ImplementationException, SubscribeNotPermittedException {
		this.unmarshaller = unmarshaller;
		if (subscribe.getQueryName().equals("SimpleEventQuery")) {
			List<QueryParam> queryParam = subscribe.getParams().getParam();
			convertQueryParams(queryParam);
			makeSimpleEventQuery(queryParam);
		} else if (subscribe.getQueryName().equals("SimpleMasterDataQuery")) {
			SubscribeNotPermittedException e = new SubscribeNotPermittedException(
					"The specified query name may not be used with subscribe, only with poll.");
			throw e;
		}
	}

	public QueryDescription(Poll poll, SOAPQueryUnmarshaller unmarshaller)
			throws QueryParameterException, ImplementationException {
		this.unmarshaller = unmarshaller;
		if (poll.getQueryName().equals("SimpleEventQuery")) {
			List<QueryParam> queryParam = poll.getParams().getParam();
			convertQueryParams(queryParam);
			makeSimpleEventQuery(queryParam);
		} else if (poll.getQueryName().equals("SimpleMasterDataQuery")) {
			makeSimpleMasterDataQuery(poll.getParams().getParam());
		}
	}

	public QueryDescription(JsonObject query) throws QueryParameterException, ImplementationException {
		String queryName = query.getString("queryName");

		if (queryName == null || queryName.equals("SimpleEventQuery")) {
			makeSimpleEventQuery(query);
		} else {
			// makeSimpleMasterDataQuery(poll.getParams().getParam());
		}
	}

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

	private String getString(Object value) throws QueryParameterException {
		try {
			return (String) value;
		} catch (ClassCastException e) {
			throw new QueryParameterException(e.getMessage());
		}
	}

	private void putOrderBy(List<Document> mongoQueryElements, Object value) throws QueryParameterException {
		String string = getString(value);
		checkNull(string);
		if (!value.equals("eventTime") && !value.equals("recordTime"))
			orderBy = "extension." + BSONReadUtil.encodeMongoObjectKey(string);
		else
			orderBy = string;
	}

	private void putOrderDirection(List<Document> mongoQueryElements, Object value) throws QueryParameterException {
		String string = getString(value);
		checkNull(string);
		if (value.equals("ASC"))
			orderDirection = 1;
		else if (value.equals("DESC"))
			orderDirection = -1;
		else {
			QueryParameterException e = new QueryParameterException(
					"the value of a parameter is of the wrong type or out of range: value should be (ASC|DESC) if given");
			throw e;
		}
	}

	private void putEventCountLimit(List<Document> mongoQueryElements, Object value) throws QueryParameterException {
		eventCountLimit = getInteger(value);
	}

	private void putMaxEventCount(List<Document> mongoQueryElements, Object value) throws QueryParameterException {
		maxCount = getInteger(value);
	}

	private List<String> fromArrayOfString(Object value) throws QueryParameterException {
		try {
			return unmarshaller.getArrayOfString((Element) value).getString();
		} catch (Exception e1) {
			QueryParameterException e = new QueryParameterException(
					"the value of a parameter is of the wrong type or out of range: value should be"
							+ ArrayOfString.class + "if given");
			throw e;
		}
	}

	private VoidHolder fromVoidHolder(Object value) throws QueryParameterException {
		try {
			return unmarshaller.getVoidHolder((Element) value);
		} catch (Exception e1) {
			QueryParameterException e = new QueryParameterException(
					"the value of a parameter is of the wrong type or out of range: value should be" + VoidHolder.class
							+ "if given");
			throw e;
		}
	}

	private long fromDateTimeStamp(Object value) throws QueryParameterException {
		try {
			return TimeUtil.toUnixEpoch(((Element) value).getTextContent());
		} catch (ParseException | NullPointerException e) {
			throw new QueryParameterException(e.getMessage());
		}
	}

	/**
	 * ArrayOfString -> List<String> DateTimeStamp -> String (did not validated)
	 * VoidHolder -> VoidHolder Double -> Double String -> String Boolean -> Boolean
	 * Integer -> Integer
	 * 
	 * @param paramList
	 * @throws QueryParameterException
	 */
	private void convertQueryParams(List<QueryParam> paramList) throws QueryParameterException {
		for (QueryParam param : paramList) {
			Object value = param.getValue();
			if (value instanceof Element) {
				// ArrayOfString
				// DateTimeStamp
				// VoidHolder
				Element element = (Element) value;
				String attribute = element.getAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "type");
				if (attribute.contains(":ArrayOfString")) {
					param.setValue(fromArrayOfString(value));
				} else if (attribute.contains(":DateTimeStamp")) {
					param.setValue(fromDateTimeStamp(value));
				} else if (attribute.contains(":VoidHolder")) {
					param.setValue(fromVoidHolder(value));
				}
			} else if (value instanceof Double) {
				continue;
			} else if (value instanceof String) {
				continue;
			} else if (value instanceof Boolean) {
				continue;
			} else if (value instanceof Integer) {
				continue;
			} else {
				throw new QueryParameterException(
						"value of SOAP Query Parameter should be one of ArrayOfString, DateTimeStamp, VoidHolder, Double, String, Boolean, Integer");
			}
		}
	}

	private List<QueryParam> convertToQueryParams(JsonObject query) throws QueryParameterException {
		List<QueryParam> queryParams = new ArrayList<QueryParam>();

		for (String field : query.fieldNames()) {
			Object value = query.getValue(field);

			if (field.equals("@context"))
				continue;

			if (value == null) {
				queryParams.add(new QueryParam(field, new VoidHolder()));
			} else if (value instanceof JsonArray) {
				List<String> arrayOfString = new ArrayList<String>();
				JsonArray arr = (JsonArray) value;
				for (Object arrValue : arr) {
					arrayOfString.add(arrValue.toString());
				}
				queryParams.add(new QueryParam(field, arrayOfString));
			} else if (value instanceof String) {
				try {
					long t = TimeUtil.toUnixEpoch(value.toString());
					queryParams.add(new QueryParam(field, t));

				} catch (ParseException | NullPointerException e) {
					queryParams.add(new QueryParam(field, value.toString()));
				}
			} else if (value instanceof Boolean) {
				queryParams.add(new QueryParam(field, value.toString()));
			} else if (value instanceof Double) {
				queryParams.add(new QueryParam(field, (Double) value));
			} else if (value instanceof Integer) {
				queryParams.add(new QueryParam(field, (Integer) value));
			} else {
				throw new QueryParameterException(
						"value of REST Query Parameter should be one of JsonArray, String, Time, Boolean, Double, Integer");
			}
		}
		return queryParams;
	}

	private boolean getBoolean(Object value) throws QueryParameterException {
		try {
			return ((Boolean) value).booleanValue();
		} catch (ClassCastException | NullPointerException e) {
			throw new QueryParameterException(e.getMessage());
		}
	}

	private int getInteger(Object value) throws QueryParameterException {
		try {
			return ((Integer) value).intValue();
		} catch (ClassCastException | NullPointerException e) {
			throw new QueryParameterException(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	private List<String> getListOfString(Object value) throws QueryParameterException {
		if (!(value instanceof List))
			throw new QueryParameterException("the value of a parameter is of the wrong type: " + value.getClass());

		List<String> valueList = (List<String>) value;
		if (valueList == null || valueList.isEmpty()) {
			throw new QueryParameterException(
					"the value of a parameter is of the wrong type or out of range: null or empty");
		}
		return valueList;
	}

	private void checkNull(String value) throws QueryParameterException {
		if (value == null || value.isEmpty()) {
			QueryParameterException e = new QueryParameterException(
					"the value of a parameter is of the wrong type or out of range: null");
			throw e;
		}
	}

	private void putListOfStringAttributeProjection(List<String> valueList) {
		attributeProjection.addAll(valueList);
	}

	void makeSimpleMasterDataQuery(List<QueryParam> paramList) throws QueryParameterException, ImplementationException {
		queryName = "SimpleMasterDataQuery";
		List<Document> mongoQueryElements = new ArrayList<Document>();
		mongoSort = new Document();
		attributeProjection = new HashSet<String>();
		mongoProjection = new Document();

		// convert values
		convertQueryParams(paramList);

		for (QueryParam param : paramList) {
			String name = param.getName();
			Object value = param.getValue();

			// single param
			QueryConverter converter = StaticResource.simpleMasterDataQueryFactory.getConverterMap().get(name);
			if (converter != null) {
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("EQATTR_")) {
				converter = StaticResource.simpleMasterDataQueryFactory.getConverterMap().get("EQATTR_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.equals("includeAttributes")) {
				includeAttributes = getBoolean(value);
				continue;
			}

			if (name.equals("includeChildren")) {
				includeChildren = getBoolean(value);
				continue;
			}

			if (name.equals("attributeNames")) {
				putListOfStringAttributeProjection(getListOfString(value));
				continue;
			}

			if (name.equals("maxElementCount")) {
				maxCount = getInteger(value);
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
		if (mongoQueryElements.size() != 0)
			mongoQuery.put("$and", mongoQueryElements);
	}

	void makeSimpleEventQuery(List<QueryParam> paramList) throws QueryParameterException, ImplementationException {
		queryName = "SimpleEventQuery";
		mongoSort = new Document();
		
		List<Document> mongoQueryElements = new ArrayList<Document>();
		for (QueryParam param : paramList) {
			String name = param.getName();
			Object value = param.getValue();

			// single param
			QueryConverter converter = StaticResource.simpleEventQueryFactory.getConverterMap().get(name);
			if (converter != null) {
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			// complex param
			if (name.startsWith("EQ_bizTransaction_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("EQ_bizTransaction_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("EQ_source_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("EQ_source_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("EQ_destination_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("EQ_destination_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("EQ_quantity_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("EQ_quantity_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("GT_quantity_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("GT_quantity_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("GE_quantity_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("GE_quantity_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("LT_quantity_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("LT_quantity_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("LE_quantity_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("LE_quantity_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("EQ_INNER_ILMD_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("EQ_INNER_ILMD_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("GT_INNER_ILMD_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("GT_INNER_ILMD_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("GE_INNER_ILMD_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("GE_INNER_ILMD_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("LT_INNER_ILMD_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("LT_INNER_ILMD_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("LE_INNER_ILMD_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("LE_INNER_ILMD_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("EXISTS_INNER_ILMD_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("EXISTS_INNER_ILMD_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("EQ_INNER_SENSORELEMENT_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("EQ_INNER_SENSORELEMENT_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("GT_INNER_SENSORELEMENT_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("GT_INNER_SENSORELEMENT_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("GE_INNER_SENSORELEMENT_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("GE_INNER_SENSORELEMENT_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("LT_INNER_SENSORELEMENT_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("LT_INNER_SENSORELEMENT_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("LE_INNER_SENSORELEMENT_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("LE_INNER_SENSORELEMENT_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("EXISTS_INNER_SENSORELEMENT_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("EXISTS_INNER_SENSORELEMENT_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("EQ_INNER_readPoint_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("EQ_INNER_readPoint_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("GT_INNER_readPoint_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("GT_INNER_readPoint_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("GE_INNER_readPoint_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("GE_INNER_readPoint_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("LT_INNER_readPoint_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("LT_INNER_readPoint_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("LE_INNER_readPoint_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("LE_INNER_readPoint_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("EXISTS_INNER_readPoint_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("EXISTS_INNER_readPoint_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("EQ_INNER_bizLocation_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("EQ_INNER_bizLocation_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("GT_INNER_bizLocation_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("GT_INNER_bizLocation_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("GE_INNER_bizLocation_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("GE_INNER_bizLocation_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("LT_INNER_bizLocation_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("LT_INNER_bizLocation_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("LE_INNER_bizLocation_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("LE_INNER_bizLocation_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("EXISTS_INNER_bizLocation_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("EXISTS_INNER_bizLocation_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("EQ_INNER_ERROR_DECLARATION_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("EQ_INNER_ERROR_DECLARATION_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("GT_INNER_ERROR_DECLARATION_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("GT_INNER_ERROR_DECLARATION_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("GE_INNER_ERROR_DECLARATION_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("GE_INNER_ERROR_DECLARATION_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("LT_INNER_ERROR_DECLARATION_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("LT_INNER_ERROR_DECLARATION_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("LE_INNER_ERROR_DECLARATION_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("LE_INNER_ERROR_DECLARATION_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("EXISTS_INNER_ERROR_DECLARATION_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap()
						.get("EXISTS_INNER_ERROR_DECLARATION_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("EQ_INNER_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("EQ_INNER_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("GT_INNER_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("GT_INNER_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("GE_INNER_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("GE_INNER_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("LT_INNER_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("LT_INNER_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("LE_INNER_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("LE_INNER_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("EXISTS_INNER_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("EXISTS_INNER_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("EQ_ILMD_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("EQ_ILMD_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("GT_ILMD_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("GT_ILMD_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("GE_ILMD_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("GE_ILMD_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("LT_ILMD_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("LT_ILMD_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("LE_ILMD_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("LE_ILMD_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("EXISTS_ILMD_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("EXISTS_ILMD_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("EQ_SENSORELEMENT_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("EQ_SENSORELEMENT_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("GT_SENSORELEMENT_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("GT_SENSORELEMENT_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("GE_SENSORELEMENT_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("GE_SENSORELEMENT_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("LT_SENSORELEMENT_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("LT_SENSORELEMENT_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("LE_SENSORELEMENT_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("LE_SENSORELEMENT_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("EXISTS_SENSORELEMENT_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("EXISTS_SENSORELEMENT_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("EQ_readPoint_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("EQ_readPoint_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("GT_readPoint_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("GT_readPoint_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("GE_readPoint_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("GE_readPoint_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("LT_readPoint_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("LT_readPoint_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("LE_readPoint_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("LE_readPoint_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("EXISTS_readPoint_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("EXISTS_readPoint_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("EQ_SENSORMETADATA_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("EQ_SENSORMETADATA_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("EQ_SENSORREPORT_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("EQ_SENSORREPORT_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("EQ_bizLocation_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("EQ_bizLocation_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("GT_bizLocation_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("GT_bizLocation_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("GE_bizLocation_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("GE_bizLocation_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("LT_bizLocation_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("LT_bizLocation_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("LE_bizLocation_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("LE_bizLocation_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("EXISTS_bizLocation_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("EXISTS_bizLocation_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("EQ_ERROR_DECLARATION_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("EQ_ERROR_DECLARATION_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("GT_ERROR_DECLARATION_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("GT_ERROR_DECLARATION_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("GE_ERROR_DECLARATION_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("GE_ERROR_DECLARATION_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("LT_ERROR_DECLARATION_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("LT_ERROR_DECLARATION_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("LE_ERROR_DECLARATION_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("LE_ERROR_DECLARATION_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("EXISTS_ERROR_DECLARATION_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("EXISTS_ERROR_DECLARATION_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			// sensor
			if (name.startsWith("EQ_value_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("EQ_value_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("GT_value_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("GT_value_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("GE_value_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("GE_value_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("LT_value_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("LT_value_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("LE_value_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("LE_value_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("EQ_minValue_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("EQ_minValue_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("GT_minValue_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("GT_minValue_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("GE_minValue_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("GE_minValue_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("LT_minValue_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("LT_minValue_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("LE_minValue_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("LE_minValue_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("EQ_maxValue_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("EQ_maxValue_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("GT_maxValue_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("GT_maxValue_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("GE_maxValue_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("GE_maxValue_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("LT_maxValue_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("LT_maxValue_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("LE_maxValue_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("LE_maxValue_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("EQ_meanValue_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("EQ_meanValue_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("GT_meanValue_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("GT_meanValue_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("GE_meanValue_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("GE_meanValue_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("LT_meanValue_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("LT_meanValue_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("LE_meanValue_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("LE_meanValue_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("EQ_sDev_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("EQ_sDev_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("GT_sDev_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("GT_sDev_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("GE_sDev_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("GE_sDev_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("LT_sDev_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("LT_sDev_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("LE_sDev_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("LE_sDev_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("EQ_percValue_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("EQ_percValue_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("GT_percValue_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("GT_percValue_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("GE_percValue_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("GE_percValue_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("LT_percValue_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("LT_percValue_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("LE_percValue_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("LE_percValue_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("EQ_ATTR_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("EQ_ATTR_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("EQ_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("EQ_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("GT_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("GT_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("GE_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("GE_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("LT_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("LT_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("LE_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("LE_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("EXISTS_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("EXISTS_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("HASATTR_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("HASATTR_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			if (name.startsWith("EQ_SENSORMETADATA_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("EQ_SENSORMETADATA_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}
			if (name.startsWith("EQ_SENSORREPORT_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("EQ_SENSORREPORT_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}
			if (name.startsWith("EXISTS_SENSORMETADATA_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("EXISTS_SENSORMETADATA_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}
			if (name.startsWith("EXISTS_SENSORREPORT_")) {
				converter = StaticResource.simpleEventQueryFactory.getConverterMap().get("EXISTS_SENSORREPORT_");
				mongoQueryElements.add(converter.convert(name, value));
				continue;
			}

			// sort limit
			switch (name) {
			case "orderBy":
				putOrderBy(mongoQueryElements, value);
				continue;
			case "orderDirection":
				putOrderDirection(mongoQueryElements, value);
				continue;
			case "eventCountLimit":
				putEventCountLimit(mongoQueryElements, value);
				continue;
			case "maxEventCount":
				putMaxEventCount(mongoQueryElements, value);
				continue;
			}

		}

		mongoQuery = new Document();
		if (mongoQueryElements.size() != 0)
			mongoQuery.put("$and", mongoQueryElements);

		if (orderBy != null) {
			mongoSort.put(orderBy, orderDirection);
		} else if (eventCountLimit != null) {
			throw new QueryParameterException(
					"eventCountLimit parameter should only be used when orderBy is specified");
		}

		if (eventCountLimit != null && maxCount != null)
			throw new QueryParameterException("maxEventCountr and eventCountLimit are mutually exclusive.");

	}

	void makeSimpleEventQuery(JsonObject query) throws QueryParameterException, ImplementationException {
		queryName = "SimpleEventQuery";
		mongoSort = new Document();

		// convert values
		List<QueryParam> paramList = convertToQueryParams(query);
		makeSimpleEventQuery(paramList);
	}
}
