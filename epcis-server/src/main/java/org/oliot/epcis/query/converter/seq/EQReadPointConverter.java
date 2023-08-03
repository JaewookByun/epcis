package org.oliot.epcis.query.converter.seq;

import java.util.List;

import org.bson.Document;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.model.ValidationException;
import org.oliot.epcis.query.converter.BaseConverter;
import org.oliot.epcis.query.converter.QueryConverter;
import org.oliot.epcis.resource.StaticResource;
import org.oliot.epcis.validation.IdentifierValidator;

/**
 * If specified, the result will only include events that (a) have a non-null
 * readPoint field; and where (b) the value of the readPoint field matches one
 * of the specified URIs. If this parameter and WD_readPoint are both omitted,
 * events are returned regardless of the value of the readPoint field or whether
 * the readPoint field exists at all.
 * 
 * List of URIs
 * 
 */
public class EQReadPointConverter extends BaseConverter implements QueryConverter {

	@Override
	public Document convert(String key, Object value) throws QueryParameterException {
		List<String> valueList = getListOfString(value);
		for (String v : valueList) {
			try {
				IdentifierValidator.checkLocationEPCPureIdentity(StaticResource.gcpLength, v);
			} catch (ValidationException e) {
				throw new QueryParameterException(e.getReason());
			}
		}
		return getEQQuery("readPoint", valueList);
	}
}
