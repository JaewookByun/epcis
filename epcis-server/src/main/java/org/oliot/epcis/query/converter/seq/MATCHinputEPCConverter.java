package org.oliot.epcis.query.converter.seq;

import java.util.List;

import org.bson.Document;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.query.converter.BaseConverter;
import org.oliot.epcis.query.converter.QueryConverter;

/**
 * 
 * If this parameter is specified, the result will only include events that (a)
 * have an inputEPCList (that is, TransformationEvent or an extension event type
 * that extends TransformationEvent); and where (b) one of the EPCs listed in
 * the inputEPCList field matches one of the URIs specified in this parameter.
 * The meaning of “matches” is as specified in section 8.2.7.1.1. If this
 * parameter is omitted, events are included regardless of their inputEPCList
 * field or whether the inputEPCList field exists.
 * 
 * List of URIs
 */
public class MATCHinputEPCConverter extends BaseConverter implements QueryConverter {

	@Override
	public Document convert(String key, Object value) throws QueryParameterException {
		List<String> valueList = getListOfString(value);
		return getMATCHQuery("inputEPCList", valueList);
	}
}
