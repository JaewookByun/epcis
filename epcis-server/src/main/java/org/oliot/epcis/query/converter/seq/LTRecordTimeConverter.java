package org.oliot.epcis.query.converter.seq;

import org.bson.Document;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.query.converter.BaseConverter;
import org.oliot.epcis.query.converter.QueryConverter;

/**
 * If provided, only events with recordTime less than the specified value will
 * be returned. If omitted, events are included regardless of their recordTime
 * (unless constrained by the GE_recordTime parameter or the automatic
 * limitation based on event record time).
 *
 * DateTimeStamp
 */
public class LTRecordTimeConverter extends BaseConverter implements QueryConverter {

	@Override
	public Document convert(String key, Object value) throws QueryParameterException {
		return getComparisonQuery("recordTime", "$lt", getLong(value));
	}
}
