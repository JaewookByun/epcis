package org.oliot.epcis.query.converter.seq;

import java.util.List;

import org.bson.Document;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.query.converter.BaseConverter;
import org.oliot.epcis.query.converter.QueryConverter;
import org.oliot.epcis.resource.Resource;

/**
 * If specified, the result will only include events that (a) have a non-null
 * bizStep field; and where (b) the value of the bizStep field matches one of
 * the specified values. If this parameter is omitted, events are returned
 * regardless of the value of the bizStep field or whether the bizStep field
 * exists at all.
 * 
 * List of String
 */
public class EQBizStepConverter extends BaseConverter implements QueryConverter {

	@Override
	public Document convert(String key, Object value) throws QueryParameterException {
		List<String> valueList = getListOfString(value);
		if (!valueList.parallelStream().allMatch(Resource.bizSteps::contains))
			throw new QueryParameterException("the value of a parameter should be one of " + Resource.bizSteps);
		return getEQQuery("bizStep", valueList);
	}
}
