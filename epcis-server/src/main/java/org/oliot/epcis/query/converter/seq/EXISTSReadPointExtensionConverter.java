package org.oliot.epcis.query.converter.seq;

import org.bson.Document;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.query.converter.BaseConverter;
import org.oliot.epcis.query.converter.QueryConverter;

/**
 * If this parameter is specified, the result will only include events that (a)
 * accommodate a bizRules attribute; and where (b) the bizRules attribute is
 * equal to one of the values specified in this parameter. If this parameter is
 * omitted, events are returned regardless of the value of the bizRules
 * attribute or whether the bizRules attribute exists at all.
 * 
 */
public class EXISTSReadPointExtensionConverter extends BaseConverter implements QueryConverter {

	@Override
	public Document convert(String key, Object value) throws QueryParameterException {
		return getExistsQuery("readPointExt." + retrieveParameterType(key, 17));
	}
}
