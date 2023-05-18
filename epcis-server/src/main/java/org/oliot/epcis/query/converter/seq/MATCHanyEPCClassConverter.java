package org.oliot.epcis.query.converter.seq;

import java.util.List;

import org.bson.Document;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.query.converter.BaseConverter;
import org.oliot.epcis.query.converter.QueryConverter;

/**
 * 
 * If this parameter is specified, the result will only include events that (a)
 * have an epcList field, a childEPCs field, a parentID field, an inputEPCList
 * field, or an outputEPCList field (that is, ObjectEvent, AggregationEvent,
 * TransactionEvent, TransformationEvent, AssociationEvent or extension event
 * types that extend one of those event types); and where (b) the parentID field
 * or one of the EPCs listed in the epcList, childEPCs, inputEPCList, or
 * outputEPCList field (depending on event type) matches one of URIs specified
 * in this parameter. The meaning of “matches” is as specified in section
 * 8.2.7.1.1.
 * 
 * List of URIs
 * 
 */
public class MATCHanyEPCClassConverter extends BaseConverter implements QueryConverter {

	@Override
	public Document convert(String key, Object value) throws QueryParameterException {
		List<String> valueList = getListOfString(value);
		return getMATCHQuery(
				new String[] { "quantityList.epcClass", "inputQuantityList.epcClass", "outputQuantityList.epcClass" },
				valueList);
	}
}
