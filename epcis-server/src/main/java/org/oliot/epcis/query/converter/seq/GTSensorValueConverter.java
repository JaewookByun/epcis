package org.oliot.epcis.query.converter.seq;

import org.bson.Document;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.query.converter.BaseConverter;
import org.oliot.epcis.query.converter.QueryConverter;
import org.oliot.epcis.resource.Resource;

/**
 * This is not a single parameter, but a family of parameters. If a parameter of
 * this form is specified, the result will only include events that (a) include
 * a sourceList; (b) where the source list includes an entry whose type subfield
 * is equal to type extracted from the name of this parameter; and (c) where the
 * source subfield of that entry is equal to one of the values specified in this
 * parameter.
 * 
 * v2.0.0
 * 
 * List of URIs
 * 
 */
public class GTSensorValueConverter extends BaseConverter implements QueryConverter {

	@Override
	public Document convert(String key, Object value) throws QueryParameterException {
		double dValue = getDouble(value);
		String uom = retrieveParameterType(key, 9);
		String type = Resource.unitConverter.getType(uom);
		String rUom = Resource.unitConverter.getRepresentativeUoMFromType(type);
		double rValue = Resource.unitConverter.getRepresentativeValue(type, uom, dValue);

		return new Document().append("sensorElementList.sensorReport.rUom", rUom)
				.append("sensorElementList.sensorReport.rValue", new Document("$gt", rValue));
	}
}
