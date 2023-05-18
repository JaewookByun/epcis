package org.oliot.epcis.query.converter.seq;

import java.util.List;

import org.bson.Document;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.query.converter.BaseConverter;
import org.oliot.epcis.query.converter.QueryConverter;

/**
 * Like MATCH_epc, but matches the parentID field of AggregationEvent, the
 * parentID field of TransactionEvent, the parentID field of AssociationEvent
 * and extension event types that extend those event types. The meaning of
 * “matches” is as specified in section 8.2.7.1.1.
 * 
 * List of URIs
 * 
 */
public class MATCHparentIDConverter extends BaseConverter implements QueryConverter {

	@Override
	public Document convert(String key, Object value) throws QueryParameterException {
		List<String> valueList = getListOfString(value);
		return getMATCHQuery("parentID", valueList);
	}
}
