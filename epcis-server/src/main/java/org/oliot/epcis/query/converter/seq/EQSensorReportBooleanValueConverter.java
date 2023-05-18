package org.oliot.epcis.query.converter.seq;

import org.bson.Document;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.query.converter.BaseConverter;
import org.oliot.epcis.query.converter.QueryConverter;

/**
 * If this parameter is specified, the result will only include events that (a)
 * accommodate a booleanValue attribute; and where (b) the booleanValue
 * attribute is equal to the specified value (i.e. ‘true’ or ‘false’). If this
 * parameter is omitted, events are returned regardless of the value of the
 * booleanValue attribute or whether the booleanValue attribute exists at all.
 * 
 */
public class EQSensorReportBooleanValueConverter extends BaseConverter implements QueryConverter {

	@Override
	public Document convert(String key, Object value) throws QueryParameterException {
		return getEQQuery("sensorElementList.sensorReport.booleanValue", getBoolean(value));
	}
}
