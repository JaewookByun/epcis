package org.oliot.epcis.query.converter.seq;

import org.bson.Document;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.query.converter.BaseConverter;
import org.oliot.epcis.query.converter.QueryConverter;

/**
 * If specified, only events with eventTime greater than or equal to the
 * specified value will be included in the result. If omitted, events are
 * included regardless of their eventTime (unless constrained by the
 * LT_eventTime parameter).
 *
 * DateTimeStamp
 */
public class GEEventTimeConverter extends BaseConverter implements QueryConverter {

	@Override
	public Document convert(String key, Object value) throws QueryParameterException {
		return getComparisonQuery("eventTime", "$gte", getLong(value));
	}
}
