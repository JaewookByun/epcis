package org.oliot.epcis.query.converter.seq;

import java.util.List;

import org.bson.Document;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.query.converter.BaseConverter;
import org.oliot.epcis.query.converter.QueryConverter;

/**
 * If this parameter is specified, the result will only include events that (a)
 * have an inputQuantityList field (that is, TransformationEvent or extension
 * event types that extend it); and where (b) one of the EPC classes listed in
 * the inputQuantityList field (depending on event type) matches one of the EPC
 * patterns or URIs specified in this parameter. The meaning of “matches” is as
 * specified in section 8.2.7.1.1.
 * 
 * List of URIs
 * 
 */
public class MATCHinputEPCClassConverter extends BaseConverter implements QueryConverter {

	@Override
	public Document convert(String key, Object value) throws QueryParameterException {
		List<String> valueList = getListOfString(value);
		return getMATCHQuery("inputQuantityList.epcClass", valueList);
	}
}
