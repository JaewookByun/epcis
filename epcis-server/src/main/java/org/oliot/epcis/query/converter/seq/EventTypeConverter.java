package org.oliot.epcis.query.converter.seq;

import java.util.List;

import org.bson.Document;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.query.converter.BaseConverter;
import org.oliot.epcis.query.converter.QueryConverter;
import org.oliot.epcis.resource.StaticResource;

/**
 * If specified, the result will only include events whose type matches one of
 * the types specified in the parameter value. Each property of the parameter
 * value may be one of the following strings: ObjectEvent, AggregationEvent,
 * TransactionEvent, TransformationEvent or AssociationEvent. A property of the
 * parameter value may also be the name of an extension event type. If omitted,
 * all event types will be considered for inclusion in the result.
 *
 * List of String
 */
public class EventTypeConverter extends BaseConverter implements QueryConverter {

	@Override
	public Document convert(String key, Object value) throws QueryParameterException {
		List<String> valueList = null;
		valueList = getListOfString(value);
		if (!valueList.parallelStream().allMatch(StaticResource.eventTypes::contains))
			throw new QueryParameterException(
					"the value of a parameter should be one of " + StaticResource.eventTypes);
		return getEQQuery("type", valueList);
	}
}
