package org.oliot.epcis.query.converter.seq;

import java.util.List;

import org.bson.Document;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.query.converter.BaseConverter;
import org.oliot.epcis.query.converter.QueryConverter;
import org.oliot.epcis.resource.Resource;

/**
 * If specified, the result will only include events that (a) have an action
 * field; and where (b) the value of the action field matches one of the
 * specified values. The properties of the value of this parameter each must be
 * one of the strings ADD, OBSERVE, or DELETE; if not, the implementation SHALL
 * raise a QueryParameterException. If omitted, events are included regardless
 * of their action field.
 * 
 * List of String
 *
 */
public class EQActionConverter extends BaseConverter implements QueryConverter {

	@Override
	public Document convert(String key, Object value) throws QueryParameterException {
		List<String> valueList = getListOfString(value);
		if (!valueList.parallelStream().allMatch(Resource.actions::contains))
			throw new QueryParameterException("the value of a parameter should be one of " + Resource.actions);
		return getEQQuery("action", valueList);
	}
}
