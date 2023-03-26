package org.oliot.epcis.query.converter.seq;

import java.util.List;

import org.bson.Document;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.query.converter.BaseConverter;
import org.oliot.epcis.query.converter.QueryConverter;
import org.oliot.epcis.resource.Resource;

/**
 * Like the EQ_bizStep parameter, but for the persistentDisposition unset field.
 * 
 * List of URIs
 */
public class EQPersistentDispositionUnsetConverter extends BaseConverter implements QueryConverter {

	@Override
	public Document convert(String key, Object value) throws QueryParameterException {
		List<String> valueList = getListOfString(value);
		if (!valueList.parallelStream().allMatch(Resource.dispositions::contains))
			throw new QueryParameterException("the value of a parameter should be one of " + Resource.dispositions);
		return getEQQuery("persistentDisposition.unset", valueList);
	}
}
