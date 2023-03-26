package org.oliot.epcis.query.converter.seq;

import org.bson.Document;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.query.converter.BaseConverter;
import org.oliot.epcis.query.converter.QueryConverter;

/**
 * If provided, only events with recordTime greater than or equal to the
 * specified value will be returned. The automatic limitation based on event
 * record time (section 8.2.5.2) may implicitly provide a constraint similar to
 * this parameter. If omitted, events are included regardless of their
 * recordTime, other than automatic limitation based on event record time
 * (section 8.2.5.2).
 *
 * DateTimeStamp
 */
public class GERecordTimeConverter extends BaseConverter implements QueryConverter {

	@Override
	public Document convert(String key, Object value) throws QueryParameterException {
		return getComparisonQuery("recordTime", "$gte", getLong(value));
	}
}
