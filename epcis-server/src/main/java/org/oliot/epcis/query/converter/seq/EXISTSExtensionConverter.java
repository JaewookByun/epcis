package org.oliot.epcis.query.converter.seq;

import org.bson.Document;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.query.converter.BaseConverter;
import org.oliot.epcis.query.converter.QueryConverter;

/**
 * Like EQ_fieldname as described above, but may be applied to a field of any
 * type (including complex types). The result will include events that have a
 * non￾empty field named fieldname. Fieldname is constructed as for
 * EQ_fieldname. Note that the value for this query parameter is ignored.
 * 
 * v2.0.0
 */
public class EXISTSExtensionConverter extends BaseConverter implements QueryConverter {

	@Override
	public Document convert(String key, Object value) throws QueryParameterException {
		return getExistsQuery("extension." + retrieveParameterType(key, 7));
	}
}
