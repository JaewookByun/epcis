package org.oliot.epcis.query.converter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.model.VoidHolder;
import org.oliot.epcis.util.BSONReadUtil;
import org.oliot.epcis.util.TimeUtil;

@SuppressWarnings({ "unchecked" })
public class BaseConverter {

	protected Document getComparisonExtensionQuery(String key, String comparator, Object value)
			throws QueryParameterException {
		if (value instanceof Integer) {
			return getComparisonQuery(key, comparator, getInteger(value));
		} else if (value instanceof Double) {
			return getComparisonQuery(key, comparator, getDouble(value));
		} else if (value instanceof Long) {
			return getComparisonQuery(key, comparator, getLong(value));
		}

		throw new QueryParameterException("the value of a parameter is of the wrong type or out of range");
	}

	protected Document getEQExtensionQuery(String key, Object value) throws QueryParameterException {
		if (value instanceof Integer) {
			return getEQQuery(key, getInteger(value));
		} else if (value instanceof Double) {
			return getEQQuery(key, getDouble(value));
		} else if (value instanceof List<?>) {
			return getEQQuery(key, getListOfString(value));
		} else if (value instanceof Long) {
			return getEQQuery(key, getLong(value));
		}
		throw new QueryParameterException("the value of a parameter is of the wrong type or out of range");
	}

	protected Document getMATCHQuery(String[] fields, List<String> valueList) throws QueryParameterException {
		List<Document> docList = valueList.parallelStream().flatMap(v -> {
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

		return new Document("$or", docList);
	}

	protected Document getMATCHQuery(String field, List<String> valueList) throws QueryParameterException {
		List<Document> docList = valueList.parallelStream().map(v -> {
			if (v.contains("*")) {
				v = v.replaceAll("\\.", "[.]");
				v = v.replaceAll("\\*", "(.)*");
				return new Document(field, new Document("$regex", v));
			} else {
				return new Document(field, v);
			}
		}).collect(Collectors.toList());
		return new Document("$or", docList);
	}

	protected Document getEQQuery(String field, List<?> valueList) {
		return new Document(field, new Document("$in", valueList));
	}

	protected Document appendEQQuery(String field, List<?> valueList) {
		return new Document(field, new Document("$in", valueList));
	}

	
	protected Document getEQQuery(String field, long value) {
		return new Document(field, value);
	}

	protected Document getEQQuery(String field, double value) {
		return new Document(field, value);
	}

	protected Document getEQQuery(String field, int value) {
		return new Document(field, value);
	}

	protected Document getEQQuery(String field, boolean value) {
		return new Document(field, value);
	}

	protected Document getComparisonQuery(String field, String comparator, long value) throws QueryParameterException {
		return new Document(field, new Document(comparator, value));
	}

	protected Document getComparisonQuery(String field, String comparator, double value)
			throws QueryParameterException {
		return new Document(field, new Document(comparator, value));
	}

	protected Document getExistsQuery(String field) throws QueryParameterException {
		return new Document(field, new Document("$exists", true));
	}

	protected long getTimeMillis(Object value) throws QueryParameterException {
		try {
			return TimeUtil.toUnixEpoch(value.toString());
		} catch (ParseException | NullPointerException e) {
			throw new QueryParameterException(e.getMessage());
		}
	}

	protected double getDouble(Object value) throws QueryParameterException {
		try {
			return ((Double) value).doubleValue();
		} catch (ClassCastException | NullPointerException e) {
			throw new QueryParameterException(e.getMessage());
		}
	}

	protected long getLong(Object value) throws QueryParameterException {
		try {
			return ((Long) value).longValue();
		} catch (ClassCastException | NullPointerException e) {
			throw new QueryParameterException(e.getMessage());
		}
	}

	protected int getInteger(Object value) throws QueryParameterException {
		try {
			return ((Integer) value).intValue();
		} catch (ClassCastException | NullPointerException e) {
			throw new QueryParameterException(e.getMessage());
		}
	}

	protected List<String> getListOfString(Object value) throws QueryParameterException {
		if (!(value instanceof List))
			throw new QueryParameterException("the value of a parameter is of the wrong type: " + value.getClass());

		List<String> valueList = (List<String>) value;
		if (valueList == null || valueList.isEmpty()) {
			throw new QueryParameterException(
					"the value of a parameter is of the wrong type or out of range: null or empty");
		}
		return valueList;
	}

	protected VoidHolder getVoidHolder(Object value) throws QueryParameterException {
		try {
			return (VoidHolder) value;
		} catch (ClassCastException e) {
			throw new QueryParameterException(
					"the value of a parameter is of the wrong type or out of range: value should be" + VoidHolder.class
							+ "if given");
		}
	}

	protected boolean getBoolean(Object value) throws QueryParameterException {
		try {
			return ((Boolean) value).booleanValue();
		} catch (ClassCastException | NullPointerException e) {
			throw new QueryParameterException(e.getMessage());
		}
	}

	protected String retrieveParameterType(String parameterName, int prefix) {
		return BSONReadUtil.encodeMongoObjectKey(parameterName.substring(prefix));
	}

}
