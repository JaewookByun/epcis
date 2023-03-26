package org.oliot.epcis.query.converter.seq;

import java.net.URI;
import java.util.List;

import org.bson.Document;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.query.converter.BaseConverter;
import org.oliot.epcis.query.converter.QueryConverter;

/**
 * If this parameter is specified, the result will only include events that (a)
 * have a non-null eventID field; and where (b) the eventID field is equal to
 * one of the values specified in this parameter. If this parameter is omitted,
 * events are returned regardless of the value of the eventID field or whether
 * the eventID field exists at all.
 * 
 * List of URIs
 * 
 */
public class EQEventIDConverter extends BaseConverter implements QueryConverter {

	@Override
	public Document convert(String key, Object value) throws QueryParameterException {
		List<String> valueList = getListOfString(value);
		for (String v : valueList) {
			try {
				URI.create(v);
			} catch (IllegalArgumentException e) {
				throw new QueryParameterException("the value of a parameter is of the wrong type or out of range: URI");
			}
		}

		return getEQQuery("eventID", valueList);
	}
}
