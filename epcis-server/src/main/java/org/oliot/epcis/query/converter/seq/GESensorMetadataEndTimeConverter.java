package org.oliot.epcis.query.converter.seq;

import org.bson.Document;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.query.converter.BaseConverter;
import org.oliot.epcis.query.converter.QueryConverter;

/**
 * If specified, only events with endTime greater than or equal to the specified
 * value will be included in the result. If omitted, events are included
 * regardless of their endTime (unless constrained by the LT_endTime parameter).
 * 
 * v2.0.0
 * 
 */
public class GESensorMetadataEndTimeConverter extends BaseConverter implements QueryConverter {

	@Override
	public Document convert(String key, Object value) throws QueryParameterException {
		return getComparisonQuery("sensorElementList.sensorMetadata.endTime", "$gte", getLong(value));
	}
}
