package org.oliot.epcis.query.converter.seq;

import org.bson.Document;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.query.converter.BaseConverter;
import org.oliot.epcis.query.converter.QueryConverter;

/**
 * If this parameter is specified, the result will only include events that (a)
 * have a percRank attribute; and where (b) a value of percRank is less than the
 * specified parameter. NOTE: since percRank and percValue should be specified
 * together in the data, if present, it may be appropriate for EPCIS queries to
 * express a query constraint of both percRank and percValue together (i.e., not
 * independently of each other).
 * 
 */
public class LTSensorReportPercRankConverter extends BaseConverter implements QueryConverter {

	@Override
	public Document convert(String key, Object value) throws QueryParameterException {
		return getComparisonQuery("sensorElementList.sensorReport.percRank", "$lt", getDouble(value));
	}
}
