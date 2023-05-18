package org.oliot.epcis.query.converter.seq;

import java.net.URI;
import java.util.List;

import org.bson.Document;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.query.converter.BaseConverter;
import org.oliot.epcis.query.converter.QueryConverter;

/**
 * 
 * If this parameter is specified, the result will only include events that (a)
 * accommodate a uriValue attribute; and where (b) the uriValue attribute is
 * equal to one of the URIs specified in this parameter. If this parameter is
 * omitted, events are returned regardless of the value of the uriValue
 * attribute or whether the uriValue attribute exists at all.
 * 
 */
public class EQSensorReportUriValueConverter extends BaseConverter implements QueryConverter {

	@Override
	public Document convert(String key, Object value) throws QueryParameterException {
		List<String> valueList = getListOfString(value);
		for (String v : valueList) {
			try {
				URI.create(v);
			} catch (IllegalArgumentException e) {
				throw new QueryParameterException(e.getMessage());
			}
		}
		return getEQQuery("sensorElementList.sensorReport.uriValue", valueList);
	}
}
