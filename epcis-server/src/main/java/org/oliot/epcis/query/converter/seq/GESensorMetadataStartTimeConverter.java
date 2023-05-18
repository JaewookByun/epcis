package org.oliot.epcis.query.converter.seq;

import org.bson.Document;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.query.converter.BaseConverter;
import org.oliot.epcis.query.converter.QueryConverter;

/**
 * If this parameter is specified, the result will only include events that (a)
 * contain an ErrorDeclaration; and where (b) the value of the
 * errorDeclarationTime field is greater than or equal to the specified value.
 * If this parameter is omitted, events are returned regardless of whether they
 * contain an ErrorDeclaration or what the value of the errorDeclarationTime
 * field is.
 * 
 * v2.0.0
 * 
 * DateTimeStamp
 * 
 */
public class GESensorMetadataStartTimeConverter extends BaseConverter implements QueryConverter {

	@Override
	public Document convert(String key, Object value) throws QueryParameterException {
		return getComparisonQuery("sensorElementList.sensorMetadata.startTime", "$gte", getLong(value));
	}
}
