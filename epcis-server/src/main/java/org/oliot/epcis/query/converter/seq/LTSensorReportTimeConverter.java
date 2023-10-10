package org.oliot.epcis.query.converter.seq;

import org.bson.Document;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.query.converter.BaseConverter;
import org.oliot.epcis.query.converter.QueryConverter;

public class LTSensorReportTimeConverter extends BaseConverter implements QueryConverter {

	@Override
	public Document convert(String key, Object value) throws QueryParameterException {
		return getComparisonQuery("sensorElementList.sensorReport.time", "$lt", getLong(value));
	}
}
