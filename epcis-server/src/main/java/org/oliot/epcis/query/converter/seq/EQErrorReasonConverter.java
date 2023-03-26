package org.oliot.epcis.query.converter.seq;

import java.util.List;

import org.bson.Document;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.query.converter.BaseConverter;
import org.oliot.epcis.query.converter.QueryConverter;
import org.oliot.epcis.resource.Resource;

/**
 * If this parameter is specified, the result will only include events that (a)
 * contain an ErrorDeclaration; and where (b) the error declaration contains a
 * non-null reason field; and where (c) the reason field is equal to one of the
 * values specified in this parameter. If this parameter is omitted, events are
 * returned regardless of whether they contain an ErrorDeclaration or what the
 * value of the reason field is.
 * 
 * List of URIs
 * 
 */
public class EQErrorReasonConverter extends BaseConverter implements QueryConverter {

	@Override
	public Document convert(String key, Object value) throws QueryParameterException {

		List<String> valueList = getListOfString(value);
		if (!valueList.parallelStream().allMatch(Resource.errorReasons::contains))
			throw new QueryParameterException("the value of a parameter should be one of " + Resource.errorReasons);
		return getEQQuery("errorDeclaration.reason", valueList);
	}
}