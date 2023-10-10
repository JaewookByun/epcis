package org.oliot.epcis.query.converter.seq;

import java.util.List;

import org.bson.Document;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.query.converter.BaseConverter;
import org.oliot.epcis.query.converter.QueryConverter;
import org.oliot.epcis.resource.StaticResource;

/**
 * If this parameter is specified, the result will only include events that (a)
 * accommodate one or more sensorElement fields; and where (b) the type
 * attribute in one of these sensorElement fields is equal to one of the values
 * specified in this parameter. If this parameter is omitted, events are
 * returned regardless of the value of the type attribute or whether a
 * sensorElement field exists at all.
 * 
 * v2.0.0
 * 
 */
public class EQSensorReportSensorTypeConverter extends BaseConverter implements QueryConverter {

	@Override
	public Document convert(String key, Object value) throws QueryParameterException {
		List<String> valueList = getListOfString(value);
		for (String v : valueList) {
			if (!StaticResource.measurements.contains(v)) {
				throw new QueryParameterException("unsupported sensor measurement: " + v);
			}
		}
		return getEQQuery("sensorElementList.sensorReport.type", valueList);
	}
}
