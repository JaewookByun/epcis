package org.oliot.epcis.query.converter;

import org.bson.Document;
import org.oliot.epcis.model.ImplementationException;
import org.oliot.epcis.model.QueryParameterException;

public interface QueryConverter {
	public Document convert(String key, Object value) throws QueryParameterException, ImplementationException;
}
