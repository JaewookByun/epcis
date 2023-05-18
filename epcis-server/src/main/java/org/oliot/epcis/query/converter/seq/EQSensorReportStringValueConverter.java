package org.oliot.epcis.query.converter.seq;

import java.util.List;

import org.bson.Document;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.query.converter.BaseConverter;
import org.oliot.epcis.query.converter.QueryConverter;

/**
 * If this parameter is specified, the result will only include events that (a)
 * accommodate a stringValue attribute; and where (b) the stringValue attribute
 * is equal to one of the specified parameter. If this parameter is omitted,
 * events are returned regardless of the value of the stringValue attribute or
 * whether the stringValue attribute exists at all.
 */
public class EQSensorReportStringValueConverter extends BaseConverter implements QueryConverter {

	@Override
	public Document convert(String key, Object value) throws QueryParameterException {
		List<String> valueList = getListOfString(value);
		return getEQQuery("sensorElementList.sensorReport.stringValue", valueList);
	}
}
