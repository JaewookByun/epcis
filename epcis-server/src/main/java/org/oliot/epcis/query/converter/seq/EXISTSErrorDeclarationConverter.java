package org.oliot.epcis.query.converter.seq;

import org.bson.Document;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.query.converter.BaseConverter;
import org.oliot.epcis.query.converter.QueryConverter;

/**
 * If this parameter is specified, the result will only include events that
 * contain an ErrorDeclaration. If this parameter is omitted, events are
 * returned regardless of whether they contain an ErrorDeclaration.
 * 
 * VoidHolder
 * 
 */
public class EXISTSErrorDeclarationConverter extends BaseConverter implements QueryConverter {

	@Override
	public Document convert(String key, Object value) throws QueryParameterException {
		getVoidHolder(value);
		return getExistsQuery("errorDeclaration");
	}
}
