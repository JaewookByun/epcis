package org.oliot.epcis.query.converter.seq;

import org.bson.Document;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.query.converter.BaseConverter;
import org.oliot.epcis.query.converter.QueryConverter;

/**
 * If specified, only events with startTime less than the specified value will
 * be included in the result. If omitted, events are included regardless of
 * their startTime (unless constrained by the GE_startTime parameter).
 * 
 * v2.0.0
 * 
 */
public class LTSensorMetadataStartTimeConverter extends BaseConverter implements QueryConverter {

	@Override
	public Document convert(String key, Object value) throws QueryParameterException {
		return getComparisonQuery("sensorElementList.sensorMetadata.startTime", "$lt", getLong(value));
	}
}
