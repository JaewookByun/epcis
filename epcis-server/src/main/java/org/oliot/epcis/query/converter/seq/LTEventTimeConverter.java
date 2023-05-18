package org.oliot.epcis.query.converter.seq;

import org.bson.Document;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.query.converter.BaseConverter;
import org.oliot.epcis.query.converter.QueryConverter;

/**
 * If specified, only events with eventTime less than the specified value will
 * be included in the result. If omitted, events are included regardless of
 * their eventTime (unless constrained by the GE_eventTime parameter).
 *
 * DateTimeStamp
 */
public class LTEventTimeConverter extends BaseConverter implements QueryConverter {

	@Override
	public Document convert(String key, Object value) throws QueryParameterException {
		return getComparisonQuery("eventTime", "$lt", getLong(value));
	}
}
