package org.oliot.epcis.query.converter.seq;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.bson.Document;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.query.converter.BaseConverter;
import org.oliot.epcis.query.converter.QueryConverter;

/**
 * If this parameter is specified, the result will only include events that (a)
 * accommodate a hexBinaryValue attribute; and where (b) the hexBinaryValue
 * attribute is equal to one of the values specified in this parameter. If this
 * parameter is omitted, events are returned regardless of the value of the
 * hexBinaryValue attribute or whether the hexBinaryValue attribute exists at
 * all.
 * 
 */
public class EQSensorReportHexBinaryValueConverter extends BaseConverter implements QueryConverter {

	@Override
	public Document convert(String key, Object value) throws QueryParameterException {
		List<String> valueList = getListOfString(value);
		ArrayList<byte[]> hexValues = new ArrayList<byte[]>();
		for (String v : valueList) {
			try {
				hexValues.add(DatatypeConverter.parseHexBinary(v));
			} catch (IllegalArgumentException e) {
				throw new QueryParameterException(e.getMessage());
			}
		}
		return getEQQuery("sensorElementList.sensorReport.hexBinaryValue", hexValues);
	}
}
